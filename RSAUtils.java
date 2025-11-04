import java.math.BigInteger;

public class RSAUtils {

    // Encrypt message (message < n)
    public static BigInteger encrypt(BigInteger message, BigInteger e, BigInteger n) {
        if (message.signum() == -1) {
            throw new IllegalArgumentException("Message must be non-negative.");
        }
        if (message.compareTo(n) >= 0) {
            throw new IllegalArgumentException("Message must be less than modulus n.");
        }
        return Utils.modPow(message, e, n);
    }

    // Decrypt cipher (cipher < n)
    public static BigInteger decrypt(BigInteger cipher, BigInteger d, BigInteger n) {
        if (cipher.signum() == -1) {
            throw new IllegalArgumentException("Ciphertext must be non-negative.");
        }
        if (cipher.compareTo(n) >= 0) {
            throw new IllegalArgumentException("Ciphertext must be less than modulus n.");
        }
        return Utils.modPow(cipher, d, n);
    }
}
