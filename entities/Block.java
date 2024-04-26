package entities;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import entities.utils.StringUtil;

public class Block {

    public String hash;
    public String previousHash;
    public String merkleRoot;
    public List<Transaction> transactions = new ArrayList<>();
    private long timeStamp; // as number of milliseconds since 1/1/1970.
    private int nonce;

    // entities.Block Constructor.
    public Block(String previousHash) {
        this.previousHash = previousHash;
        this.timeStamp = new Date().getTime();
        this.hash = calculateHash(); // Making sure we do this after we set the other values.
    }

    //Calculate new hash based on blocks contents
    public String calculateHash() {
        return StringUtil.applySha256(
                previousHash +
                        Long.toString(timeStamp) +
                        Integer.toString(nonce) +
                        merkleRoot
        );
    }

    public void mineBlock(int difficulty) {
        //Create a string with difficulty * "0"
        String target = new String(new char[difficulty]).replace('\0', '0');

        // Keep mining until the hash is equal to the target.
        while(!hash.substring( 0, difficulty).equals(target)) {
            //Increases nonce value until hash target is reached.
            nonce ++;
            hash = calculateHash();
        }
        System.out.println("entities.Block Mined!!! : " + hash);
    }

    // Add transactions to this block
    public boolean addTransaction(Transaction transaction) {
        // process transaction and check if valid, unless block is genesis block then ignore.
        if(transaction == null) return false;
        if((!"0".equals(previousHash))) {
            if((transaction.processTransaction() != true)) {
                System.out.println("entities.Transaction failed to process. Discarded.");
                return false;
            }
        }
        transactions.add(transaction);
        System.out.println("entities.Transaction Successfully added to Block");
        return true;
    }
}