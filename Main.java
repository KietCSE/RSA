
import java.math.BigInteger;
import java.security.SecureRandom;

public class Main {
    public static void main(String[] args) {
        int bitLength = 2048; // RSA key size (modulus)
        KeyPair keyPair = KeyPair.generateRandomKeyPair(bitLength);

        System.out.println("Public key (e): " + keyPair.getEncryptKey());
        System.out.println("Private key (d): " + keyPair.getDecryptKey());
        System.out.println("Modulus (n): " + keyPair.getModulus());

        SecureRandom random = new SecureRandom();
        // Create a message of size bigLength-1 since n is of size bigLength
        BigInteger message = new BigInteger(bitLength - 1, random);

        System.out.println("Message (m): " + message.toString());

        BigInteger encryptedMessage = RSAUtils.encrypt(message, keyPair.getEncryptKey(), keyPair.getModulus());
        System.out.println("Encrypted message (c): " + encryptedMessage.toString());

        BigInteger decryptedMessage = RSAUtils.decrypt(encryptedMessage, keyPair.getDecryptKey(), keyPair.getModulus());
        System.out.println("Decrypted message (m'): " + decryptedMessage.toString());

        System.out.println(
                "Decrypted message is equal to original message: " + (message.compareTo(decryptedMessage) == 0));
    }
}
