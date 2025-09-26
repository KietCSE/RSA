import java.math.BigInteger;

public class KeyPair {
    // e
    private BigInteger encryptKey;
    // d
    private BigInteger decryptKey;
    // n
    private BigInteger modulus;

    // find public key with gcd(e,ø(n))=1
    private BigInteger generateEncryptKey(BigInteger phi) {
        return null;
    }

    // get decrypt key with e.d=1 mod ø(n) and 0≤d≤ ø(n)
    private BigInteger generateDecryptKey(BigInteger e, BigInteger phi) {
        return null;
    }

    // generate random key pair
    // n = p.q
    // ø(n)=(p-1)(q-1)
    // e = getEncryptKey(ø(n))
    // d = getDecryptKey(e, ø(n))
    // return (e, d, n)
    public static KeyPair generateRandomKeyPair(BigInteger p, BigInteger q) {
        // TODO
        return null;
    }
}
