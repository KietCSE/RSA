import java.math.BigInteger;
import java.security.SecureRandom;

public class KeyPair {
    // p and q are 2 generated primes, together with e to calculate d
    private BigInteger p;
    private BigInteger q;
    // e (public exponent)
    private BigInteger encryptKey;
    // d (private exponent)
    private BigInteger decryptKey;
    // n (modulus)
    private BigInteger modulus;

    private KeyPair(BigInteger p, BigInteger q, BigInteger e, BigInteger d, BigInteger n) {
        this.p = p;
        this.q = q;
        this.encryptKey = e;
        this.decryptKey = d;
        this.modulus = n;
    }

    // Find public key e such that gcd(e, phi) = 1
    private static BigInteger generateEncryptKey(BigInteger phi, BigInteger e) {
        // If user provides a custom e
        if (e != null) {
            // Conditions: 1 < e < phi AND gcd(e, phi) = 1
            if (e.compareTo(BigInteger.ONE) <= 0 || e.compareTo(phi) >= 0) {
                throw new IllegalArgumentException("Public exponent e must satisfy 1 < e < phi.");
            }

            if (!Utils.gcd(e, phi).equals(BigInteger.ONE)) {
                throw new IllegalArgumentException("Public exponent e is not coprime with phi.");
            }

            return e; // Valid user-provided exponent
        }

        // Otherwise: Auto-generate a valid random e
        SecureRandom random = new SecureRandom();
        BigInteger generatedE;
        do {
            generatedE = new BigInteger(phi.bitLength() - 1, random);
        } while (!Utils.gcd(generatedE, phi).equals(BigInteger.ONE)
                || generatedE.compareTo(BigInteger.ONE) <= 0);

        return generatedE;
    }

    // Find d such that e * d ≡ 1 (mod phi)
    private static BigInteger generateDecryptKey(BigInteger e, BigInteger phi) {
        return Utils.modMulInverse(e, phi);
    }

    // Generate random RSA key pair using two random primes p, q
    public static KeyPair generateRandomKeyPair(int bitLength, BigInteger choosenE) {
        BigInteger p;
        BigInteger q;
        BigInteger e;
        BigInteger phi;
        BigInteger n;
        do {
            // Generate two large primes p and q
            p = PrimeGenerator.generatePrime(bitLength / 2);
            q = PrimeGenerator.generatePrime(bitLength / 2);

            // Ensure p != q
            while (p.equals(q)) {
                q = PrimeGenerator.generatePrime(bitLength / 2);
            }

            // Compute n = p * q
            n = p.multiply(q);

            // Compute φ(n) = (p - 1)(q - 1)
            phi = (p.subtract(BigInteger.ONE)).multiply(q.subtract(BigInteger.ONE));

            // Generate e
            e = generateEncryptKey(phi, choosenE);
        } while (!RSAPrimeVerifier.verifyPrimeForRSA(p, q, e));

        // Generate d
        BigInteger d = generateDecryptKey(e, phi);

        return new KeyPair(p, q, e, d, n);
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

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("KeyPair {\n");
        sb.append("  p           = ").append(p).append(",\n");
        sb.append("  q           = ").append(q).append(",\n");
        sb.append("  modulus(n)  = ").append(modulus).append(",\n");
        sb.append("  encryptKey  = ").append(encryptKey).append(",\n");
        sb.append("  decryptKey  = ").append(decryptKey).append(",\n");
        sb.append("\n");
        sb.append("  Public Key  = (").append(encryptKey).append(", ").append(modulus).append("),\n");
        sb.append("  Private Key = (").append(decryptKey).append(", ").append(modulus).append(")\n");
        sb.append("}");
        return sb.toString();
    }
}
