package entities;

import java.security.*;
import java.security.spec.ECGenParameterSpec;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Wallet class to store the public and private keys of a wallet.
 */
public class Wallet {
    public PrivateKey privateKey; // sign transactions
    public PublicKey publicKey; // wallet address
    public Map<String, TransactionOutput> UTXOs = new HashMap<>(); // only UTXOs owned by this wallet

    /**
     * Constructor
     */
    public Wallet() {
        generateKeyPair();
    }

    /**
     * @return the balance of this wallet.
     * This method iterates over all the UTXOs in the BasicChain.UTXOs map and checks if the output belongs to this wallet.
     * If it does, the output is added to the UTXOs map of this wallet and the value of the output is added to the total balance.
     * Finally, the total balance is returned.
     */
    public float getBalance(){
        float total = 0;
        for (Map.Entry<String, TransactionOutput> item: BasicChain.UTXOs.entrySet()){
            TransactionOutput UTXO = item.getValue();
            if(UTXO.isMine(publicKey)) { //if output belongs to me ( if coins belong to me )
                UTXOs.put(UTXO.id, UTXO); //add it to our list of unspent transactions.
                total += UTXO.value ;
            }
        }
        return total;
    }

    /**
     * Sends funds to a recipient.
     * @param _recipient the recipient of the funds.
     * @param value the amount of funds to send.
     * @return the transaction.
     */
    public Transaction sendFunds(PublicKey _recipient, float value) {
        if(getBalance() < value) { //gather balance and check funds.
            System.out.println("#Not Enough funds to send transaction. Transaction Discarded.");
            return null;
        }
        //create array list of inputs
        ArrayList<TransactionInput> inputs = new ArrayList<>();

        float total = 0;
        // iterate over UTXOs and add them to inputs until the total is greater than or equal to the value of the transaction.
        for (Map.Entry<String, TransactionOutput> item: UTXOs.entrySet()){
            TransactionOutput UTXO = item.getValue();
            total += UTXO.value;
            inputs.add(new TransactionInput(UTXO.id));
            if(total > value) break;
        }

        Transaction newTransaction = new Transaction(publicKey, _recipient, value, inputs);
        newTransaction.generateSignature(privateKey);

        // remove the inputs from the UTXOs list as they are now spent.
        for(TransactionInput input: inputs){
            UTXOs.remove(input.transactionOutputId);
        }

        return newTransaction;
    }

    /**
     * Generates Elliptic Curve key pair (private and public key).
     */
    public void generateKeyPair() {
        // generate public and private keys
        try {
            // Elliptic Curve Digital Signature Algorithm
            KeyPairGenerator keyGen = KeyPairGenerator.getInstance("ECDSA", "BC"); // Elliptic Curve Digital Signature Algorithm
            SecureRandom random = SecureRandom.getInstance("SHA1PRNG"); // SecureRandom.getInstanceStrong() is recommended for production
            ECGenParameterSpec ecSpec = new ECGenParameterSpec("prime192v1"); // prime192v1 is the name of the elliptic curve used by bitcoin

            // Initialize the key generator and generate a KeyPair
            keyGen.initialize(ecSpec, random); // 256 bytes provides an acceptable security level
            KeyPair keyPair = keyGen.generateKeyPair();

            // Set the public and private keys from the keyPair
            privateKey = keyPair.getPrivate();
            publicKey = keyPair.getPublic();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
