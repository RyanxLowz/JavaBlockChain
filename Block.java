import java.util.Date;

import Utils.StringUtil;

public class Block {

    public String hash;
    public String previousHash;
    private String data; // A simple message.
    private long timeStamp; // as number of milliseconds since 1/1/1970.
    private int nonce;

    // Block Constructor.
    public Block(String data, String previousHash) {
        this.data = data;
        this.previousHash = previousHash;
        this.timeStamp = new Date().getTime();
        this.hash = calculateHash(); // Making sure we do this after we set the other values.
    }

    // Calculate new hash based on blocks contents
    public String calculateHash() {
        return StringUtil.applySha256(
                previousHash +
                        Long.toString(timeStamp) +
                        Integer.toString(nonce) +
                        data);
    }
}