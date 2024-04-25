package entities;

import entities.utils.StringUtil;

import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.ArrayList;

public class Transaction {
    public String transactionId; // this is also the hash of the transaction.
    public PublicKey sender; // senders address/public key.
    public PublicKey recipient; // Recipients address/public key.
    public float value;
    public byte[] signature; // this is to prevent anybody else from spending funds in our wallet.
    public ArrayList<TransactionInput> inputs = new ArrayList<>();
    public ArrayList<TransactionOutput> outputs = new ArrayList<>();

    private static int sequence = 0; // a rough count of how many transactions have been generated.

    // Constructor
    public Transaction(PublicKey from, PublicKey to, float value, ArrayList<TransactionInput> inputs) {
        this.sender = from;
        this.recipient = to;
        this.value = value;
        this.inputs = inputs;
    }

    /**
     * @return the transaction hash (which will be used as the Transaction ID)
     */
    private String calculateHash() {
        sequence++; //increase the sequence to avoid 2 identical transactions having the same hash
        return StringUtil.applySha256(
                StringUtil.getStringFromKey(sender) +
                        StringUtil.getStringFromKey(recipient) +
                        value + sequence
        );
    }

    /**
     * @param privateKey the private key of the sender.
     *                   Signs all the data we don't wish to be tampered with.
     */
    public void generateSignature(PrivateKey privateKey) {
        String data = StringUtil.getStringFromKey(sender) + StringUtil.getStringFromKey(recipient) + value;
        signature = StringUtil.applyECDSASig(privateKey, data);
    }

    /**
     * @return true if signature is verified, false otherwise.
     */
    public boolean verifySignature() {
        String data = StringUtil.getStringFromKey(sender) + StringUtil.getStringFromKey(recipient) + value;
        return StringUtil.verifyECDSASig(sender, data, signature);
    }

    /**
     * @return true if new transaction could be created.
     */
    public boolean processTransaction() {

        // verify the signature of the transaction
        if(verifySignature() == false) {
            System.out.println("#Transaction Signature failed to verify");
            return false;
        }

        // gather transaction inputs (Make sure they are unspent):
        for(TransactionInput i : inputs) {
            i.UTXO = BasicChain.UTXOs.get(i.transactionOutputId);
        }

        // check if transaction is valid:
        if(getInputsValue() < BasicChain.minimumTransaction) {
            System.out.println("#Transaction Inputs too small: " + getInputsValue());
            return false;
        }

        // generate transaction outputs:
        float leftOver = getInputsValue() - value; //get value of inputs then the leftover change:
        transactionId = calculateHash(); // generate transaction id
        outputs.add(new TransactionOutput( this.recipient, value, transactionId)); //send value to recipient
        outputs.add(new TransactionOutput( this.sender, leftOver, transactionId)); //send the left over 'change' back to sender

        // add outputs to Unspent list
        for(TransactionOutput o : outputs) {
            BasicChain.UTXOs.put(o.id , o);
        }

        // remove transaction inputs from UTXO lists as spent:
        for(TransactionInput i : inputs) {
            if(i.UTXO == null) continue; //if Transaction can't be found skip it
            BasicChain.UTXOs.remove(i.UTXO.id);
        }

        return true;
    }

    /**
     * @return sum of inputs(UTXOs) values.
     */
    public float getInputsValue() {
        float total = 0;
        for(TransactionInput i : inputs) {
            if(i.UTXO == null) continue; //if Transaction can't be found skip it
            total += i.UTXO.value;
        }
        return total;
    }

    /**
     * @return sum of outputs.
     */
    public float getOutputsValue() {
        float total = 0;
        for(TransactionOutput o : outputs) {
            total += o.value;
        }
        return total;
    }
}
