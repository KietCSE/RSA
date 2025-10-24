import java.math.BigInteger;

public class Main {
    public static void main(String[] args) {
        System.out.println("Hello world!");

        System.out.println("GCD(48, 18) = " + Utils.gcd(BigInteger.valueOf(48), BigInteger.valueOf(18))); // 6

        BigInteger e = BigInteger.valueOf(7);
        BigInteger phi = BigInteger.valueOf(40);

        BigInteger d = Utils.modMulInverse(e, phi);

        System.out.println("Nghịch đảo modular của " + e + " mod " + phi + " là: " + d);
        System.out.println("Kiểm tra: (" + e + " * " + d + ") % " + phi + " = " + (e.multiply(d).mod(phi)));
        // BigInteger p = primeGen.generatePrime(512);
        // BigInteger q = primeGen.generatePrime(512);
        // KeyPair keyPair = KeyPair.generateRandomKeyPair(p, q);
        // BigInteger message = new BigInteger("1234567890");

        // BigInteger cipher = RSAEncryptor.encrypt(message, keyPair.e, keyPair.n);
        // System.out.println("Cipher: " + cipher);

        // BigInteger decrypted = RSAEncryptor.decrypt(cipher, keyPair.d, keyPair.n);
        // System.out.println("Decrypted: " + decrypted);
    }
}
