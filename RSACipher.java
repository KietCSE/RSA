import java.math.BigInteger;

/**
 * Interface defining RSA encryption and decryption operations.
 * This provides a clear contract for RSA cipher implementations,
 * helping users understand which methods are available.
 */
public interface RSACipher {

    // ============================================================
    // BASIC RSA OPERATIONS (Standard)
    // ============================================================

    /**
     * Basic RSA encryption: c = m^e mod n
     * 
     * @param message The message to encrypt (must be < n)
     * @param e       The public exponent (encrypt key)
     * @param n       The modulus
     * @return The encrypted ciphertext
     */
    BigInteger encrypt(BigInteger message, BigInteger e, BigInteger n);

    /**
     * Basic RSA decryption: m = c^d mod n
     * 
     * @param cipher The ciphertext to decrypt (must be < n)
     * @param d      The private exponent (decrypt key)
     * @param n      The modulus
     * @return The decrypted message
     */
    BigInteger decrypt(BigInteger cipher, BigInteger d, BigInteger n);

    // ============================================================
    // SECURE RSA OPERATIONS (OAEP Padding)
    // ============================================================

    /**
     * Secure RSA encryption with OAEP padding.
     * OAEP (Optimal Asymmetric Encryption Padding) provides security against
     * chosen ciphertext attacks and ensures randomness in encryption.
     * 
     * @param message The message to encrypt
     * @param e       The public exponent
     * @param n       The modulus
     * @return The encrypted ciphertext
     */
    BigInteger encryptOAEP(BigInteger message, BigInteger e, BigInteger n);

    /**
     * Secure RSA decryption with OAEP unpadding.
     * 
     * @param cipher The ciphertext to decrypt
     * @param d      The private exponent
     * @param n      The modulus
     * @return The original message
     */
    BigInteger decryptOAEP(BigInteger cipher, BigInteger d, BigInteger n);

    // ============================================================
    // OPTIMIZED RSA OPERATIONS (CRT)
    // ============================================================

    /**
     * Fast RSA decryption using Chinese Remainder Theorem (CRT).
     * CRT decryption is approximately 4x faster than standard decryption
     * by computing modulo p and q separately and combining results.
     * 
     * @param cipher  The ciphertext to decrypt
     * @param keyPair The KeyPair containing p, q, d, n
     * @return The decrypted message
     */
    BigInteger decryptCRT(BigInteger cipher, KeyPair keyPair);

    /**
     * Combined fast and secure decryption: CRT for speed + OAEP for security.
     * This is the RECOMMENDED method for production use, offering both
     * performance optimization and security against attacks.
     * 
     * @param cipher  The ciphertext to decrypt
     * @param keyPair The KeyPair containing necessary private key components
     * @return The original message
     */
    BigInteger decryptOAEP_CRT(BigInteger cipher, KeyPair keyPair);
}
