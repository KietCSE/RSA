import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

public class RSAUtils implements RSACipher {

    // Encrypt message (message < n)
    @Override
    public BigInteger encrypt(BigInteger message, BigInteger e, BigInteger n) {
        if (message.signum() == -1) {
            throw new IllegalArgumentException("Message must be non-negative.");
        }
        if (message.compareTo(n) >= 0) {
            throw new IllegalArgumentException("Message must be less than modulus n.");
        }
        return Utils.modPow(message, e, n);
    }

    // Decrypt cipher (cipher < n)
    @Override
    public BigInteger decrypt(BigInteger cipher, BigInteger d, BigInteger n) {
        if (cipher.signum() == -1) {
            throw new IllegalArgumentException("Ciphertext must be non-negative.");
        }
        if (cipher.compareTo(n) >= 0) {
            throw new IllegalArgumentException("Ciphertext must be less than modulus n.");
        }
        return Utils.modPow(cipher, d, n);
    }

    // ===============================================================================================
    // IMPROVEMENT 1: OAEP (Optimal Asymmetric Encryption Padding)
    // ===============================================================================================

    private static final int HASH_LEN = 32; // SHA-256 output length in bytes

    /**
     * Encrypts a message using RSA with OAEP padding.
     * Generate EM = 0x00 || maskedSeed || maskedDB with length = modular length in
     * bytes. Then encrypt EM using RSA encryption
     * 
     * @param message The message to encrypt (as a BigInteger).
     * @param e       The public exponent (encrypt key)
     * @param n       The modulus.
     * @return The encrypted ciphertext.
     */
    @Override
    public BigInteger encryptOAEP(BigInteger message, BigInteger e, BigInteger n) {
        try {
            int k = (n.bitLength() + 7) / 8; // Modulus length in bytes
            byte[] mBytes = message.toByteArray();

            // Handle sign byte if present (BigInteger.toByteArray() might add a leading 0
            // byte for positive numbers, we need to remove it)
            if (mBytes[0] == 0 && mBytes.length > 1) {
                byte[] temp = new byte[mBytes.length - 1];
                System.arraycopy(mBytes, 1, temp, 0, temp.length);
                mBytes = temp;
            }

            int maxMsgLen = k - 2 * HASH_LEN - 2;
            if (mBytes.length > maxMsgLen) {
                throw new IllegalArgumentException(String.format(
                        "Message too long for OAEP. Max allowed: %d bytes, Actual: %d bytes. " +
                                "Key size: %d bits (%d bytes), OAEP Overhead: %d bytes. " +
                                "Try increasing key size to at least 1024 bits.",
                        maxMsgLen, mBytes.length, n.bitLength(), k, 2 * HASH_LEN + 2));
            }

            // OAEP Padding
            // DB = lHash || PS || 0x01 || M
            // lHash = Hash(L), where L is empty string for basic OAEP
            byte[] lHash = hash(new byte[0]);
            int psLen = k - mBytes.length - 2 * HASH_LEN - 2;
            byte[] ps = new byte[psLen]; // Zero padding

            byte[] db = new byte[k - HASH_LEN - 1];
            System.arraycopy(lHash, 0, db, 0, HASH_LEN);
            System.arraycopy(ps, 0, db, HASH_LEN, psLen);
            db[HASH_LEN + psLen] = 0x01;
            System.arraycopy(mBytes, 0, db, HASH_LEN + psLen + 1, mBytes.length);

            // Generate random seed
            // Độ dài của maskedSeed LUÔN LUÔN bằng độ dài đầu ra của hàm Hash.
            byte[] seed = new byte[HASH_LEN];
            new SecureRandom().nextBytes(seed);

            // dbMask = MGF(seed, k - hLen - 1)
            byte[] dbMask = mgf1(seed, k - HASH_LEN - 1);

            // maskedDB = DB XOR dbMask
            byte[] maskedDB = Utils.xor(db, dbMask);

            // seedMask = MGF(maskedDB, hLen)
            byte[] seedMask = mgf1(maskedDB, HASH_LEN);

            // maskedSeed = seed XOR seedMask
            byte[] maskedSeed = Utils.xor(seed, seedMask);

            // EM = 0x00 || maskedSeed || maskedDB
            // maskedSeed_length = hash_length
            byte[] em = new byte[k];
            em[0] = 0x00;
            System.arraycopy(maskedSeed, 0, em, 1, HASH_LEN);
            System.arraycopy(maskedDB, 0, em, 1 + HASH_LEN, maskedDB.length);

            BigInteger mEncoded = new BigInteger(1, em);

            return Utils.modPow(mEncoded, e, n);

        } catch (Exception ex) {
            throw new RuntimeException("OAEP Encryption failed", ex);
        }
    }

    /**
     * Decrypts a ciphertext using RSA with OAEP unpadding.
     * 
     * @param cipher The ciphertext to decrypt.
     * @param d      The private exponent.
     * @param n      The modulus.
     * @return The original message.
     */
    @Override
    public BigInteger decryptOAEP(BigInteger cipher, BigInteger d, BigInteger n) {
        // 1. Standard RSA Decryption
        BigInteger encoded = Utils.modPow(cipher, d, n);

        // 2. OAEP Unpadding
        return decodeOAEP(encoded, n);
    }

    // ===============================================================================================
    // IMPROVEMENT 2: CRT (Chinese Remainder Theorem)
    // ===============================================================================================

    /**
     * Decrypts a ciphertext using RSA with CRT (Chinese Remainder Theorem).
     * 
     * @param cipher  The ciphertext.
     * @param keyPair The KeyPair containing p, q, d, n.
     * @return The decrypted message.
     */
    @Override
    public BigInteger decryptCRT(BigInteger cipher, KeyPair keyPair) {
        BigInteger p = keyPair.getP();
        BigInteger q = keyPair.getQ();
        BigInteger d = keyPair.getDecryptKey();

        if (p == null || q == null) {
            throw new IllegalArgumentException("CRT decryption requires p and q in KeyPair.");
        }

        // Precompute CRT parameters (in a real app, these should be cached in KeyPair)
        BigInteger dP = d.mod(p.subtract(BigInteger.ONE));
        BigInteger dQ = d.mod(q.subtract(BigInteger.ONE));
        BigInteger qInv = Utils.modMulInverse(q, p);

        // m1 = c^dP mod p
        BigInteger m1 = Utils.modPow(cipher, dP, p);
        // m2 = c^dQ mod q
        BigInteger m2 = Utils.modPow(cipher, dQ, q);

        // h = qInv * (m1 - m2) mod p
        BigInteger h = m1.subtract(m2).multiply(qInv).mod(p);
        if (h.signum() < 0) {
            h = h.add(p);
        }

        // m = m2 + h * q
        return m2.add(h.multiply(q));
    }

    /**
     * Decrypts using both CRT (for speed) and OAEP (for security).
     */
    @Override
    public BigInteger decryptOAEP_CRT(BigInteger cipher, KeyPair keyPair) {
        // 1. CRT Decryption
        BigInteger encoded = decryptCRT(cipher, keyPair);

        // 2. OAEP Unpadding
        return decodeOAEP(encoded, keyPair.getModulus());
    }

    private static BigInteger decodeOAEP(BigInteger encoded, BigInteger n) {
        try {
            int k = (n.bitLength() + 7) / 8;
            byte[] em = encoded.toByteArray();

            // Pad with leading zeros if necessary to match length k
            // remove unuseful leading zeros of BigInteger
            if (em.length < k) {
                byte[] temp = new byte[k];
                System.arraycopy(em, 0, temp, k - em.length, em.length);
                em = temp;
            } else if (em.length > k) {
                // Should not happen if encoded < n, but handle sign byte
                if (em[0] == 0 && em.length == k + 1) {
                    byte[] temp = new byte[k];
                    System.arraycopy(em, 1, temp, 0, k);
                    em = temp;
                }
            }

            // Check first byte is 0x00
            if (em[0] != 0x00) {
                throw new IllegalArgumentException("Invalid OAEP padding.");
            }

            byte[] maskedSeed = new byte[HASH_LEN];
            System.arraycopy(em, 1, maskedSeed, 0, HASH_LEN);

            byte[] maskedDB = new byte[k - HASH_LEN - 1];
            System.arraycopy(em, 1 + HASH_LEN, maskedDB, 0, maskedDB.length);

            byte[] seedMask = mgf1(maskedDB, HASH_LEN);
            byte[] seed = Utils.xor(maskedSeed, seedMask);

            byte[] dbMask = mgf1(seed, k - HASH_LEN - 1);
            byte[] db = Utils.xor(maskedDB, dbMask);

            // Verify lHash
            byte[] lHash = hash(new byte[0]);
            for (int i = 0; i < HASH_LEN; i++) {
                if (db[i] != lHash[i]) {
                    throw new IllegalArgumentException("OAEP decoding failed: Hash mismatch");
                }
            }

            // DB = lHash || PS || 0x01 || M
            // Find 0x01 byte
            int index = HASH_LEN;
            while (index < db.length && db[index] == 0) {
                index++;
            }
            if (index >= db.length || db[index] != 0x01) {
                throw new IllegalArgumentException("OAEP decoding failed: Padding pattern not found");
            }

            // Extract message
            int mLen = db.length - index - 1;
            byte[] mBytes = new byte[mLen];
            System.arraycopy(db, index + 1, mBytes, 0, mLen);

            return new BigInteger(1, mBytes);

        } catch (Exception ex) {
            throw new RuntimeException("OAEP Decryption failed", ex);
        }
    }

    /**
     * MGF1 (Mask Generation Function 1) is a key derivation function that generates
     * a mask from a seed using a hash function.
     * Hash many times to generate submask from a seed and concatenate them to get a
     * mask with enough bit length
     * 
     * @param seed   The seed to generate the mask from.
     * @param length The length of the mask to generate.
     * @return The generated mask.
     */
    private static byte[] mgf1(byte[] seed, int length) throws NoSuchAlgorithmException {
        byte[] mask = new byte[length];
        byte[] counter = new byte[4];
        MessageDigest md = MessageDigest.getInstance("SHA-256");

        int hLen = HASH_LEN;
        int count = 0;

        for (int i = 0; i < (length + hLen - 1) / hLen; i++) {
            // C = I2OSP(counter, 4)
            // 4 byte big endian
            counter[0] = (byte) (i >>> 24); // get byte 1
            counter[1] = (byte) (i >>> 16); // get byte 2
            counter[2] = (byte) (i >>> 8); // get byte 3
            counter[3] = (byte) i; // get byte 4

            // digest = SHA-256( seed || counter )
            md.update(seed);
            md.update(counter);
            byte[] digest = md.digest();

            // copy byte to mask
            int len = Math.min(hLen, length - count);
            System.arraycopy(digest, 0, mask, count, len);
            count += len;
        }
        return mask;
    }

    // private static byte[] xor(byte[] a, byte[] b) {
    // byte[] result = new byte[a.length];
    // for (int i = 0; i < a.length; i++) {
    // result[i] = (byte) (a[i] ^ b[i]);
    // }
    // return result;
    // }

    private static byte[] hash(byte[] input) throws NoSuchAlgorithmException {
        return MessageDigest.getInstance("SHA-256").digest(input);
    }

    // EXTENDED OPERATIONS (String & byte[])

    @Override
    public BigInteger encrypt(String message, BigInteger e, BigInteger n) {
        return encrypt(message.getBytes(), e, n);
    }

    @Override
    public BigInteger encrypt(byte[] message, BigInteger e, BigInteger n) {
        return encrypt(new BigInteger(1, message), e, n);
    }

    @Override
    public String decryptToString(BigInteger cipher, BigInteger d, BigInteger n) {
        return new String(decryptToBytes(cipher, d, n));
    }

    @Override
    public byte[] decryptToBytes(BigInteger cipher, BigInteger d, BigInteger n) {
        byte[] bytes = decrypt(cipher, d, n).toByteArray();
        // Remove leading sign byte if present (BigInteger might add it)
        if (bytes[0] == 0 && bytes.length > 1) {
            byte[] temp = new byte[bytes.length - 1];
            System.arraycopy(bytes, 1, temp, 0, temp.length);
            return temp;
        }
        return bytes;
    }

    @Override
    public BigInteger encryptOAEP(String message, BigInteger e, BigInteger n) {
        // convert string to byte array
        return encryptOAEP(message.getBytes(), e, n);
    }

    @Override
    public BigInteger encryptOAEP(byte[] message, BigInteger e, BigInteger n) {
        // make sure biginteger is always positive
        return encryptOAEP(new BigInteger(1, message), e, n);
    }

    @Override
    public String decryptOAEPToString(BigInteger cipher, BigInteger d, BigInteger n) {
        return new String(decryptOAEPToBytes(cipher, d, n));
    }

    @Override
    public byte[] decryptOAEPToBytes(BigInteger cipher, BigInteger d, BigInteger n) {
        byte[] bytes = decryptOAEP(cipher, d, n).toByteArray();
        // Remove leading sign byte if present
        if (bytes[0] == 0 && bytes.length > 1) {
            byte[] temp = new byte[bytes.length - 1];
            System.arraycopy(bytes, 1, temp, 0, temp.length);
            return temp;
        }
        return bytes;
    }

    @Override
    public String decryptCRTToString(BigInteger cipher, KeyPair keyPair) {
        return new String(decryptCRTToBytes(cipher, keyPair));
    }

    @Override
    public byte[] decryptCRTToBytes(BigInteger cipher, KeyPair keyPair) {
        byte[] bytes = decryptCRT(cipher, keyPair).toByteArray();
        // Remove leading sign byte if present
        if (bytes[0] == 0 && bytes.length > 1) {
            byte[] temp = new byte[bytes.length - 1];
            System.arraycopy(bytes, 1, temp, 0, temp.length);
            return temp;
        }
        return bytes;
    }

    @Override
    public String decryptOAEP_CRTToString(BigInteger cipher, KeyPair keyPair) {
        return new String(decryptOAEP_CRTToBytes(cipher, keyPair));
    }

    @Override
    public byte[] decryptOAEP_CRTToBytes(BigInteger cipher, KeyPair keyPair) {
        byte[] bytes = decryptOAEP_CRT(cipher, keyPair).toByteArray();
        // Remove leading sign byte if present
        if (bytes[0] == 0 && bytes.length > 1) {
            byte[] temp = new byte[bytes.length - 1];
            System.arraycopy(bytes, 1, temp, 0, temp.length);
            return temp;
        }
        return bytes;
    }
}
