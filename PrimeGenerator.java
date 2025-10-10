import java.math.BigInteger;
import java.util.Random;

public class PrimeGenerator {

    // public static Boolean millerRabinTest(BigInteger n, BigInteger numOfRound) {
    // if (n % 2 == 0)
    // return false;
    // return false;
    // }

    // Generate a prime number with the specified bit length
    public static BigInteger generatePrime(int bitLength) {
        /*
         * In book procedure for picking a prime number:
         * 1. Pick an odd integer n at ramdom
         * 2. Pick an integer a < n at random
         * 3. Perform the probabilistic primality test (e.g. Miller-Rabin), with a as a
         * param.
         * If n fails, reject the current value n and go to step 1.
         * 4. If n has passed a sufficient number of tests, accept n;
         * Else, go to step 2.
         */
        // Generate an prime number with certainty of 0.9990234375
        return new BigInteger(bitLength, 10, new Random());
    }

    // public static void main(String[] args) {
    // System.out.println(generatePrime(2048).toString());
    // }
}
