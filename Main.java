
import java.math.BigInteger;

public class Main {
    public static void main(String[] args) {
        System.out.println("Hello world!");

        BigInteger p = PrimeGenerator.generatePrime(2048);

        BigInteger q = PrimeGenerator.generatePrime(2048);

        KeyPair keyPair = new KeyPair(p, q);

        System.out.println("p = " + p.toString());
        System.out.println("q = " + q.toString());
        System.out.println("n = " + keyPair.getModulus().toString());
        System.out.println("e = " + keyPair.getEncryptKey().toString());
        System.out.println("d = " + keyPair.getDecryptKey().toString());

        BigInteger message = new BigInteger("1234567890");

        BigInteger cipher = RSAEncryptor.encrypt(message, keyPair.getDecryptKey(), keyPair.getModulus());
        System.out.println("Cipher: " + cipher);

        BigInteger decrypted = RSAEncryptor.decrypt(cipher, keyPair.getEncryptKey(), keyPair.getModulus());
        System.out.println("Decrypted: " + decrypted);
    }
}
