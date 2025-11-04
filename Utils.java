import java.math.BigInteger;

public class Utils {

    // Return the greatest common divisor of a and b
    public static BigInteger gcd(BigInteger a, BigInteger b) {
        // TODO
        return a;
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
