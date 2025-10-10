import java.math.BigInteger;

public class RSAEncryptor {

    // encrypt message (message < n)
    public static BigInteger encrypt(BigInteger message, BigInteger e, BigInteger n) {
        return message.modPow(e, n);
    }

    // decrypt cipher
    public static BigInteger decrypt(BigInteger cipher, BigInteger d, BigInteger n) {
        return cipher.modPow(d, n);
    }
}
