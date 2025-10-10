import java.math.BigInteger;

public class KeyPair {
    // e
    private BigInteger encryptKey;
    // d
    private BigInteger decryptKey;
    // n
    private BigInteger modulus;

    public KeyPair(BigInteger p, BigInteger q) {
        BigInteger n = p.multiply(q);
        BigInteger phi = (p.subtract(BigInteger.ONE).multiply(q.subtract(BigInteger.ONE)));

        this.modulus = n;
        this.encryptKey = generateEncryptKey(phi);
        this.decryptKey = generateDecryptKey(this.encryptKey, phi);
    }

    // find public key with gcd(e,ø(n))=1
    private BigInteger generateEncryptKey(BigInteger phi) {
        return new BigInteger("65537");
    }

    // get decrypt key with e.d=1 mod ø(n) and 0≤d≤ ø(n)
    private BigInteger generateDecryptKey(BigInteger e, BigInteger phi) {
        BigInteger d = e.modInverse(phi);
        return d;
    }

    // generate random key pair
    // n = p.q
    // ø(n)=(p-1)(q-1)
    // e = getEncryptKey(ø(n))
    // d = getDecryptKey(e, ø(n))
    // return (e, d, n)
    public static KeyPair generateRandomKeyPair(BigInteger p, BigInteger q) {
        // BigInteger n = p.multiply(q);
        // BigInteger phi =
        // (p.subtract(BigInteger.ONE).multiply(q.subtract(BigInteger.ONE)));

        return null;
    }

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
