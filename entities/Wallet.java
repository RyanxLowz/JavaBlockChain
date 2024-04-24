package entities;

import java.security.*;
import java.security.spec.ECGenParameterSpec;

public class Wallet {
    PrivateKey privateKey; // sign transactions
    PublicKey publicKey; // wallet address

    public Wallet() {
        generateKeyPair();
    }

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
