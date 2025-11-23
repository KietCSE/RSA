import java.math.BigInteger;

public class RSAPrimeVerifier {

    // Probability for primality test (1 - 2^-100 error chance)
    private static final int PRIME_CERTAINTY = 100;

    // Minimum difference between p and q in bits
    private static final int MIN_BIT_DIFF_VALUE = 50;

    public static boolean verifyPrimeForRSA(BigInteger p, BigInteger q, BigInteger e) {
        // 1. Must be distinct
        if (p.equals(q)) {
            return false;
        }

        // 2. Must be prime
        if (!p.isProbablePrime(PRIME_CERTAINTY)) {
            return false;
        }
        if (!q.isProbablePrime(PRIME_CERTAINTY)) {
            return false;
        }

        // 3. Ensure p and q are not too close in value
        BigInteger diff = p.subtract(q).abs();
        if (diff.bitLength() < MIN_BIT_DIFF_VALUE) {
            return false;
        }

        // 4. Basic smoothness check for p-1 and q-1
        // (Ensures (p−1) and (q−1) have some large factors)
        if (isWeakSmooth(p.subtract(BigInteger.ONE))) {
            return false;
        }
        if (isWeakSmooth(q.subtract(BigInteger.ONE))) {
            return false;
        }

        return true;
    }

    private static boolean isWeakSmooth(BigInteger n) {
        BigInteger limit = BigInteger.valueOf(1_000_000); // up to 1 million
        BigInteger temp = n;

        for (BigInteger i = BigInteger.TWO; i.compareTo(limit) <= 0; i = i.add(BigInteger.ONE)) {

            while (temp.mod(i).equals(BigInteger.ZERO)) {
                temp = temp.divide(i);
            }
        }

        // If after removing all small factors we are left with 1 → too smooth
        return temp.equals(BigInteger.ONE);
    }
}
