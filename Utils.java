import java.math.BigInteger;

public class Utils {

    /**
     * Computes the Greatest Common Divisor (GCD) of two BigIntegers using
     * the recursive Euclidean algorithm.
     * 
     * @param a the first number (non-negative BigInteger)
     * @param b the second number (non-negative BigInteger)
     * @return the greatest common divisor of {@code a} and {@code b}
     */
    public static BigInteger gcd(BigInteger a, BigInteger b) {
        if (b == BigInteger.ZERO) {
            return a;
        } else {
            return gcd(b, a.mod(b));
        }
    }

    private static BigInteger[] extendedGCD(BigInteger a, BigInteger b) {
        if (b == BigInteger.ZERO) {
            return new BigInteger[] { a, BigInteger.ONE, BigInteger.ZERO };
        } else {
            BigInteger[] vals = extendedGCD(b, a.mod(b));
            BigInteger d = vals[0];
            BigInteger x1 = vals[2];
            BigInteger y1 = vals[1].subtract((a.divide(b)).multiply(vals[2]));
            return new BigInteger[] { d, x1, y1 };
        }
    }

    /**
     * Computes the Modular Multiplicative Inverse of {@code a} modulo {@code phi}.
     * If the modular inverse does not exist (i.e., gcd(a, phi) ≠ 1),
     * this function returns {@code BigInteger.ZERO}.
     *
     * @param a   the number for which to find the modular inverse
     * @param phi the modulus
     * @return the modular inverse of {@code a} modulo {@code phi},
     *         or {@code BigInteger.ZERO} if no inverse exists
     */
    public static BigInteger modMulInverse(BigInteger a, BigInteger phi) {
        BigInteger[] vals = extendedGCD(a, phi);
        BigInteger d = vals[0];
        BigInteger x = vals[1];
        if (!d.equals(BigInteger.ONE)) {
            throw new IllegalArgumentException("No modular inverse exists for " + a + " and " + phi);
        } else {
            return (x.mod(phi).add(phi)).mod(phi);
        }
    }

    public static BigInteger modPow(BigInteger base, BigInteger exp, BigInteger mod) {
        BigInteger result = BigInteger.ONE;
        for (int i = exp.bitLength() - 1; i >= 0; i--) {
            result = (result.mod(mod).multiply(result.mod(mod))).mod(mod);
            if (exp.testBit(i)) {
                result = (result.mod(mod).multiply(base.mod(mod))).mod(mod);
            }
        }
        return result;
    }
    // OTHER METHODS SUSH AS power, mode,...
    // need to recofirm with the lecturer to be sure what function need to be
    // implemented
}
