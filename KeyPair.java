import java.math.BigInteger;
import java.security.SecureRandom;

public class KeyPair {
    // e (public exponent)
    private BigInteger encryptKey;
    // d (private exponent)
    private BigInteger decryptKey;
    // n (modulus)
    private BigInteger modulus;

    private KeyPair(BigInteger e, BigInteger d, BigInteger n) {
        this.encryptKey = e;
        this.decryptKey = d;
        this.modulus = n;
    }

    // Find public key e such that gcd(e, phi) = 1
    private static BigInteger generateEncryptKey(BigInteger phi) {
        SecureRandom random = new SecureRandom();
        BigInteger e;
        do {
            // Generate random 16–bit e for simplicity (or use fixed 65537)
            e = new BigInteger(phi.bitLength() - 1, random);
        } while (!Utils.gcd(e, phi).equals(BigInteger.ONE) || e.compareTo(BigInteger.ONE) <= 0);
        return e;
    }

    // Find d such that e * d ≡ 1 (mod phi)
    private static BigInteger generateDecryptKey(BigInteger e, BigInteger phi) {
        return Utils.modMulInverse(e, phi);
    }

    // Generate random RSA key pair using two random primes p, q
    public static KeyPair generateRandomKeyPair(int bitLength) {
        // Generate two large primes p and q
        BigInteger p = PrimeGenerator.generatePrime(bitLength / 2);
        BigInteger q = PrimeGenerator.generatePrime(bitLength / 2);

        // Ensure p != q
        while (p.equals(q)) {
            q = PrimeGenerator.generatePrime(bitLength / 2);
        }

        // Compute n = p * q
        BigInteger n = p.multiply(q);

        // Compute φ(n) = (p - 1)(q - 1)
        BigInteger phi = (p.subtract(BigInteger.ONE)).multiply(q.subtract(BigInteger.ONE));

        // Generate e and d
        BigInteger e = generateEncryptKey(phi);
        BigInteger d = generateDecryptKey(e, phi);

        return new KeyPair(e, d, n);
    }

    // Getters
    public BigInteger getEncryptKey() {
        return encryptKey;
    }

    public BigInteger getDecryptKey() {
        return decryptKey;
    }

    public BigInteger getModulus() {
        return modulus;
    }
}
