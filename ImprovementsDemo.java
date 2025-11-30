import java.math.BigInteger;
import java.util.Scanner;

class ImprovementsDemo {
    // Minimum practical bit length for RSA
    private static final int MIN_BIT_LENGTH = 1024;

    public static void main(String[] args) {
        final String RED = "\u001B[31m";
        final String RESET = "\u001B[0m";

        Scanner scanner = new Scanner(System.in);

        System.out.println("======================================");
        System.out.println("     RSA IMPROVEMENTS DEMO");
        System.out.println("======================================");

        int modulusBitLength = 0;
        BigInteger message = BigInteger.ZERO;

        // 1) GET BIT-LENGTH --------------------------------------------------------
        while (true) {
            System.out.print("Enter modulus bit-length (positive integer, e.g., 1024, 2048): ");
            String input = scanner.nextLine().trim();
            try {
                modulusBitLength = Integer.parseInt(input);
                if (modulusBitLength < MIN_BIT_LENGTH) {
                    System.out.println(
                            RED + "Error: bit-length should be at least " + MIN_BIT_LENGTH + " for security." + RESET);
                    continue;
                }
                break;
            } catch (NumberFormatException ex) {
                System.out.println(RED + "Error: bit-length must be a valid integer." + RESET);
            }
        }

        // 2) GET MESSAGE ------------------------------
        while (true) {
            System.out.print("Enter message (positive integer): ");
            String input = scanner.nextLine().trim();

            try {
                message = new BigInteger(input);

                if (message.compareTo(BigInteger.ZERO) <= 0) {
                    System.out.println(RED + "Error: message must be a positive integer." + RESET);
                    continue;
                }

                if (message.bitLength() >= modulusBitLength) {
                    System.out.println(RED + "Error: message bit-length (" + message.bitLength() +
                            ") must be smaller than the modulus bit-length (" + modulusBitLength + ")." + RESET);
                    continue;
                }

                break;
            } catch (Exception ex) {
                System.out.println(RED + "Error: invalid BigInteger for message." + RESET);
            }
        }

        // Create RSAUtils instance for encryption/decryption operations
        RSAUtils rsaUtils = new RSAUtils();

        // --- 1. Strong Key Generation ---
        System.out.println("\n[1] Generating Strong Key Pair (Higher Certainty, Checks)...");
        long startKeyGen = System.currentTimeMillis();
        KeyPair strongKeyPair = KeyPair.generateStrongKeyPair(modulusBitLength);
        long endKeyGen = System.currentTimeMillis();
        System.out.println("Strong Key Pair Generated in " + (endKeyGen - startKeyGen) + " ms.");
        System.out.println(strongKeyPair.toString());

        // --- 2. OAEP Encryption ---
        System.out.println("\n[2] OAEP Encryption (Security against Chosen Ciphertext Attack)");
        System.out.println("Encrypting the same message twice with OAEP...");

        BigInteger oaepCipher1 = rsaUtils.encryptOAEP(message, strongKeyPair.getEncryptKey(),
                strongKeyPair.getModulus());
        BigInteger oaepCipher2 = rsaUtils.encryptOAEP(message, strongKeyPair.getEncryptKey(),
                strongKeyPair.getModulus());

        System.out.println("Ciphertext 1: "
                + oaepCipher1.toString().substring(0, Math.min(50, oaepCipher1.toString().length())) + "...");
        System.out.println("Ciphertext 2: "
                + oaepCipher2.toString().substring(0, Math.min(50, oaepCipher2.toString().length())) + "...");

        if (!oaepCipher1.equals(oaepCipher2)) {
            System.out.println("SUCCESS: Ciphertexts are different! (Random padding active)");
        } else {
            System.out.println("FAILURE: Ciphertexts are identical!");
        }

        BigInteger oaepDecrypted = rsaUtils.decryptOAEP(oaepCipher1, strongKeyPair.getDecryptKey(),
                strongKeyPair.getModulus());
        System.out.println("Decrypted OAEP: " + oaepDecrypted);
        System.out.println("Match: " + message.equals(oaepDecrypted));

        // --- 3. CRT Decryption Speed Test ---
        System.out.println("\n[3] CRT Decryption Speed Test");
        System.out.println("Using a standard encrypted message for comparison...");

        // Create a standard encrypted message for speed testing
        BigInteger testCipher = rsaUtils.encrypt(message, strongKeyPair.getEncryptKey(),
                strongKeyPair.getModulus());

        int iterations = 1000;
        System.out.println("Running " + iterations + " decryptions...");

        // Standard
        long startStd = System.currentTimeMillis();
        for (int i = 0; i < iterations; i++) {
            rsaUtils.decrypt(testCipher, strongKeyPair.getDecryptKey(), strongKeyPair.getModulus());
        }
        long endStd = System.currentTimeMillis();
        long timeStd = endStd - startStd;

        // CRT
        long startCRT = System.currentTimeMillis();
        for (int i = 0; i < iterations; i++) {
            rsaUtils.decryptCRT(testCipher, strongKeyPair);
        }
        long endCRT = System.currentTimeMillis();
        long timeCRT = endCRT - startCRT;

        System.out.println("Standard Decryption Time: " + timeStd + " ms");
        System.out.println("CRT Decryption Time:      " + timeCRT + " ms");
        if (timeCRT < timeStd) {
            System.out.printf("Speedup: %.2fx faster\n", (double) timeStd / timeCRT);
        } else {
            System.out.println(
                    "Note: CRT might be slower for small keys or due to overhead in this Java implementation.");
        }

        scanner.close();
    }
}
