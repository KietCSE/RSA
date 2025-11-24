package com.example;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.Security;
import javax.crypto.Cipher;
import java.util.Base64;
import java.math.BigInteger;

public class TestJCA {
    public static void main(String[] args) {
        try {
            // 1. Add the custom provider
            Security.addProvider(new MyRSAProvider());
            System.out.println("Provider added: " + Security.getProvider("MyRSAProvider").getInfo());

            // 2. Generate Key Pair using the provider
            System.out.println("\nGenerating Keys (1024 bits)...");
            KeyPairGenerator kpg = KeyPairGenerator.getInstance("RSA", "MyRSAProvider");
            kpg.initialize(1024);
            KeyPair kp = kpg.generateKeyPair();
            System.out.println("Keys generated successfully.");

            // 3. Encrypt a message
            String originalMessage = "Hello, JCA World! This is a test.";
            System.out.println("\nOriginal Message: " + originalMessage);

            Cipher encryptCipher = Cipher.getInstance("RSA", "MyRSAProvider");
            encryptCipher.init(Cipher.ENCRYPT_MODE, kp.getPublic());
            byte[] cipherText = encryptCipher.doFinal(originalMessage.getBytes());

            // Show BigInteger value to match Main.java style
            BigInteger cipherBigInt = new BigInteger(1, cipherText);
            System.out.println("Encrypted (BigInteger): " + cipherBigInt);
            System.out.println("Encrypted (Base64): " + Base64.getEncoder().encodeToString(cipherText));

            // 4. Decrypt the message
            Cipher decryptCipher = Cipher.getInstance("RSA", "MyRSAProvider");
            decryptCipher.init(Cipher.DECRYPT_MODE, kp.getPrivate());
            byte[] decryptedBytes = decryptCipher.doFinal(cipherText);
            String decryptedMessage = new String(decryptedBytes);
            System.out.println("Decrypted Message: " + decryptedMessage);

            // 5. Verify
            if (originalMessage.equals(decryptedMessage)) {
                System.out.println("\nSUCCESS: Decrypted message matches original.");
            } else {
                System.out.println("\nFAILURE: Messages do not match.");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
