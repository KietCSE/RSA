import java.math.BigInteger;
import java.util.Random;

public class Test {
    public static void testModPow(int numberOfTest, int bitLength) {
        int i;
        for (i = 0; i < numberOfTest; i++) {
            BigInteger base = new BigInteger(bitLength, new Random());
            BigInteger exp = new BigInteger(bitLength, new Random());
            BigInteger mod = new BigInteger(bitLength, new Random());

            if (Utils.modPow(base, exp, mod).compareTo(base.modPow(exp, mod)) != 0) {
                System.out.println("Number of test passed: " + i);
                System.out.println("error at: base = " + base.toString() + ", exp = " +
                        exp.toString() + ", mod = "
                        + mod.toString());
                System.out.println("My modpow:" + Utils.modPow(base, exp, mod));
                System.out.println("BigInteger modpow:" + base.modPow(exp, mod));
                break;
            }
        }
        System.out.println("Number of test passed: " + i);
    }
}
