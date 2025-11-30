import java.math.BigInteger;
import java.util.Arrays;

public class StringByteDemo {

    public static void main(String[] args) {
        try {
            System.out.println("=========================================");
            System.out.println("RSA String and Byte[] Support Demo");
            System.out.println("=========================================");

            // 1. Setup
            System.out.println("\n[1] Generating Keys...");
            KeyPair keyPair = KeyPair.generateRandomKeyPair(1024);
            RSACipher rsa = new RSAUtils();

            System.out.println("Modulus bit length: " + keyPair.getModulus().bitLength());

            // 2. String Demo
            System.out.println("\n[2] Testing String Encryption/Decryption");
            String originalMessage = "Hello, RSA World! This is a test string";
            System.out.println("Original: " + originalMessage);

            // Encrypt
            BigInteger cipherText = rsa.encrypt(originalMessage, keyPair.getEncryptKey(), keyPair.getModulus());
            System.out.println("Ciphertext (BigInteger): " + cipherText);

            // Decrypt
            String decryptedMessage = rsa.decryptToString(cipherText, keyPair.getDecryptKey(), keyPair.getModulus());
            System.out.println("Decrypted: " + decryptedMessage);

            if (originalMessage.equals(decryptedMessage)) {
                System.out.println("SUCCESS: String decryption matches original.");
            } else {
                System.out.println("FAILURE: String decryption does not match.");
            }

            // 3. Byte[] Demo
            System.out.println("\n[3] Testing Byte[] Encryption/Decryption");
            byte[] originalBytes = new byte[] { 0x01, 0x02, 0x03, 0x04, (byte) 0xFF, (byte) 0xFE };
            System.out.println("Original Bytes: " + Arrays.toString(originalBytes));

            // Encrypt
            BigInteger cipherBytes = rsa.encrypt(originalBytes, keyPair.getEncryptKey(), keyPair.getModulus());
            System.out.println("Ciphertext (BigInteger): " + cipherBytes);

            // Decrypt
            byte[] decryptedBytes = rsa.decryptToBytes(cipherBytes, keyPair.getDecryptKey(), keyPair.getModulus());
            System.out.println("Decrypted Bytes: " + Arrays.toString(decryptedBytes));

            if (Arrays.equals(originalBytes, decryptedBytes)) {
                System.out.println("SUCCESS: Byte[] decryption matches original.");
            } else {
                System.out.println("FAILURE: Byte[] decryption does not match.");
            }

            // 6. OAEP + CRT String Demo
            System.out.println("\n[6] Testing OAEP + CRT String Decryption");
            // Re-use OAEP cipher from step 4
            String oaepctrMessage = "Secret OAEP Message";
            System.out.println("Original: " + oaepctrMessage);
            BigInteger oaepctrCipher = rsa.encryptOAEP(oaepctrMessage, keyPair.getEncryptKey(),
                    keyPair.getModulus());
            System.out.println("Ciphertext (BigInteger): " + oaepctrCipher);
            String oaepCrtDecrypted = rsa.decryptOAEP_CRTToString(oaepctrCipher, keyPair);
            System.out.println("Decrypted (OAEP+CRT): " + oaepCrtDecrypted);

            if (oaepctrMessage.equals(oaepCrtDecrypted)) {
                System.out.println("SUCCESS: OAEP + CRT String decryption matches original.");
            } else {
                System.out.println("FAILURE: OAEP + CRT String decryption does not match.");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
