package entities;

import java.util.Date;

import entities.utils.StringUtil;

public class Block {

    public String hash;
    public String previousHash;
    private String data; // A simple message.
    private long timeStamp; // as number of milliseconds since 1/1/1970.
    private int nonce;

    // entities.Block Constructor.
    public Block(String data, String previousHash) {
        this.data = data;
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
                        data
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
}