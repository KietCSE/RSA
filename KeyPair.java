import java.math.BigInteger;

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

    // // Find public key e such that gcd(e, phi) = 1
    // private static BigInteger generateEncryptKey(BigInteger phi, BigInteger e) {
    // // If user provides a custom e
    // if (e != null) {
    // // Conditions: 1 < e < phi AND gcd(e, phi) = 1
    // if (e.compareTo(BigInteger.ONE) <= 0 || e.compareTo(phi) >= 0) {
    // throw new IllegalArgumentException("Public exponent e must satisfy 1 < e <
    // phi.");
    // }

    // if (!Utils.gcd(e, phi).equals(BigInteger.ONE)) {
    // throw new IllegalArgumentException("Public exponent e is not coprime with
    // phi.");
    // }

    // return e; // Valid user-provided exponent
    // }

    // // Otherwise: Auto-generate a valid random e
    // SecureRandom random = new SecureRandom();
    // BigInteger generatedE;
    // do {
    // generatedE = new BigInteger(phi.bitLength() - 1, random);
    // } while (!Utils.gcd(generatedE, phi).equals(BigInteger.ONE)
    // || generatedE.compareTo(BigInteger.ONE) <= 0);

    // return generatedE;
    // }

    // Find d such that e * d ≡ 1 (mod phi)
    private static BigInteger generateDecryptKey(BigInteger e, BigInteger phi) {
        return Utils.modMulInverse(e, phi);
    }

    // Generate random RSA key pair using two random primes p, q
    public static KeyPair generateRandomKeyPair(int bitLength) {
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
            e = BigInteger.valueOf(65537);

        } while (!RSAPrimeVerifier.verifyPrimeForRSA(p, q) || !Utils.gcd(e, phi).equals(BigInteger.ONE));

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

    public BigInteger getP() {
        return p;
    }

    public BigInteger getQ() {
        return q;
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

    /**
     * Generates a "Strong" RSA Key Pair with enhanced security checks.
     * 
     * @param bitLength Total bit length of the modulus n (e.g., 2048).
     * @return A new KeyPair instance meeting strong security criteria.
     */
    public static KeyPair generateStrongKeyPair(int bitLength) {
        BigInteger p;
        BigInteger q;
        BigInteger e;
        BigInteger phi;
        BigInteger n;

        int strongCertainty = 40;

        do {
            System.out.println("Generating strong key pair...");
            // 1. Generate two large primes p and q with higher certainty

            p = PrimeGenerator.generatePrime(bitLength / 2, strongCertainty);
            q = PrimeGenerator.generatePrime(bitLength / 2, strongCertainty);

            // 2. Ensure p != q and difference is large enough
            // |p - q| should be large to prevent Fermat factorization
            BigInteger diff = p.subtract(q).abs();
            BigInteger minDiff = BigInteger.ONE.shiftLeft((bitLength / 2) - 100); // Heuristic: diff > 2^(len/2 - 100)

            while (p.equals(q) || diff.compareTo(minDiff) < 0) {
                q = PrimeGenerator.generatePrime(bitLength / 2, strongCertainty);
                diff = p.subtract(q).abs();
            }

            // 3. Compute n = p * q
            n = p.multiply(q);

            // 4. Compute φ(n) = (p - 1)(q - 1)
            phi = (p.subtract(BigInteger.ONE)).multiply(q.subtract(BigInteger.ONE));

            // 5. Generate/Verify e
            e = BigInteger.valueOf(65537);

        } while (!RSAPrimeVerifier.verifyPrimeForRSA(p, q) || !Utils.gcd(e, phi).equals(BigInteger.ONE));

        // Generate d
        BigInteger d = generateDecryptKey(e, phi);

        return new KeyPair(p, q, e, d, n);
    }
}
