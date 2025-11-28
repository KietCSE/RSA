import java.math.BigInteger;
import java.security.SecureRandom;

public class PrimeGenerator {

    private static final int CERTAINTY = 10; // Number of Miller-Rabin rounds (higher = more accurate)

    private static final int[] SMALL_PRIMES = { 2, 3, 5, 7, 11, 13, 17, 19, 23, 29, 31, 37, 41, 43, 47, 53 };

    // Generate a probable prime number with the specified bit length using default
    // certainty
    public static BigInteger generatePrime(int bitLength) {
        return generatePrime(bitLength, CERTAINTY);
    }

    /**
     * Generates a probable prime number with the specified bit length and
     * certainty.
     * 
     * @param bitLength The bit length of the prime to generate.
     * @param certainty The number of Miller-Rabin rounds to perform. Higher = more
     *                  secure.
     * @return A probable prime BigInteger.
     */
    public static BigInteger generatePrime(int bitLength, int certainty) {
        SecureRandom random = new SecureRandom();

        while (true) {
            // Generate a random odd number of the given bit length
            BigInteger candidate = new BigInteger(bitLength, random).setBit(bitLength - 1).setBit(0);

            // Test primality with custom certainty
            if (isProbablePrime(candidate, certainty)) {
                return candidate;
            }
        }
    }

    // Miller-Rabin primality test
    private static boolean isProbablePrime(BigInteger n, int millerRabinRounds) {
        // Handle simple cases
        if (n.compareTo(BigInteger.TWO) < 0)
            return false;
        if (n.equals(BigInteger.TWO) || n.equals(BigInteger.valueOf(3)))
            return true;
        if (n.mod(BigInteger.TWO).equals(BigInteger.ZERO))
            return false;
        if (!n.testBit(0))
            return false;

        // check small primes first
        for (int p : SMALL_PRIMES) {
            BigInteger bigP = BigInteger.valueOf(p);
            if (n.equals(bigP))
                return true;
            if (n.mod(bigP).equals(BigInteger.ZERO))
                return false;
        }

        // Write n - 1 as 2 ^ k * q with q odd
        BigInteger q = n.subtract(BigInteger.ONE);
        int k = 0;
        while (q.mod(BigInteger.TWO).equals(BigInteger.ZERO)) {
            q = q.divide(BigInteger.TWO);
            k++;
        }

        // Perform millerRabinRounds of testing
        SecureRandom random = new SecureRandom();
        for (int i = 0; i < millerRabinRounds; i++) {
            BigInteger a = uniformRandom(BigInteger.TWO, n.subtract(BigInteger.TWO), random);
            BigInteger x = Utils.modPow(a, q, n);

            if (x.equals(BigInteger.ONE))
                continue;

            boolean continueOuter = false;
            for (int j = 0; j < k; j++) {
                x = Utils.modPow(x, BigInteger.TWO, n);
                if (x.equals(n.subtract(BigInteger.ONE))) {
                    continueOuter = true;
                    break;
                }
            }

            if (continueOuter)
                continue;

            // Composite
            return false;
        }

        return true; // Probably prime
    }

    // Generate a random BigInteger in the range [min, max]
    private static BigInteger uniformRandom(BigInteger min, BigInteger max, SecureRandom random) {
        BigInteger range = max.subtract(min).add(BigInteger.ONE);
        int bitLength = range.bitLength();
        BigInteger result;
        do {
            result = new BigInteger(bitLength, random);
        } while (result.compareTo(range) >= 0);
        return result.add(min);
    }
}
