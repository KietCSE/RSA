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

    public static void testModMulInverse(int numberOfTest, int bitLengthOfE, int bitLengthOfPhi) {
        int i;

        for (i = 0; i < numberOfTest; i++) {
            BigInteger a = new BigInteger(bitLengthOfE, 100, new Random());
            BigInteger phi = new BigInteger(bitLengthOfPhi, new Random());

            try {
                BigInteger myB = Utils.modMulInverse(a, phi);
                BigInteger correctB = a.modInverse(phi);

                if (!myB.equals(correctB)) {
                    System.out.println("There an error while finding the value b such that" + a.toString() + " * b mod "
                            + phi.toString() + " = 1");
                    System.out.println("myB is " + myB.toString());
                    System.out.println("correctB is " + correctB.toString());
                }
            } catch (Exception e) {
                System.out.println(
                        "There is no value for b that satisfy " + a.toString() + "* b mod " + phi.toString() + " = 1");
            }
        }

        System.out.println("Number of test passed: " + i);
    }
}
