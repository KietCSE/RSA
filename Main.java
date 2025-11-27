import java.math.BigInteger;
import java.util.Scanner;

class Main {
    // Minimum practical bit length for RSA
    private static final int MIN_BIT_LENGTH = 512;

    public static void main(String[] args) {
        final String RED = "\u001B[31m";
        final String RESET = "\u001B[0m";

        Scanner scanner = new Scanner(System.in);

        System.out.println("======================================");
        System.out.println("          RSA Key Generator");
        System.out.println("======================================");

        int modulusBitLength = 0; // Renamed for clarity (length of N)
        BigInteger eValue = BigInteger.ZERO;
        BigInteger message = BigInteger.ZERO;

        // 1) GET BIT-LENGTH --------------------------------------------------------
        while (true) {
            System.out.print("Enter modulus bit-length (positive integer, e.g., 512, 1024): ");
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

        // 2) GET e-VALUE -----------------------------------------------------------
        // Note: The key generation loop will re-prompt this if needed
        while (true) {
            System.out.print("Enter e-value (-1 for auto, or a prime number): ");
            String input = scanner.nextLine().trim();

            try {
                eValue = new BigInteger(input);

                if (eValue.equals(BigInteger.valueOf(-1))) {
                    // OK: auto-generate
                    break;
                }

                if (eValue.compareTo(BigInteger.ONE) <= 0) {
                    System.out.println(RED + "Error: e-value must be > 1 or -1 for auto-generation." + RESET);
                    continue;
                }

                // Check primality
                if (!eValue.isProbablePrime(50)) {
                    System.out.println(RED + "Error: e-value must be prime (or -1)." + RESET);
                    continue;
                }

                break;
            } catch (Exception ex) {
                System.out.println(RED + "Error: invalid BigInteger for e-value." + RESET);
            }
        }

        // 3) GET MESSAGE ------------------------------
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

        // Done collecting inputs
        System.out.println("\nGenerating RSA Key Pair...");
        KeyPair keyPair = null;

        // 4) KEY GENERATION (Robust loop with re-prompt for e-value) ----------------
        while (keyPair == null) {
            try {
                if (eValue.equals(BigInteger.valueOf(-1)))
                    eValue = null;
                keyPair = KeyPair.generateRandomKeyPair(modulusBitLength, eValue);
            } catch (Exception ex) {
                System.out.println(RED + "Key Generation Error: " + ex.getMessage() + RESET);

                // Re-prompt for a valid e-value only
                System.out.println("Please re-enter a compatible e-value.");
                boolean validE = false;
                while (!validE) {
                    System.out.print("Enter e-value (-1 for auto, or a prime number): ");
                    String input = scanner.nextLine().trim();
                    try {
                        BigInteger tempE = new BigInteger(input);

                        // Check if the input is -1 OR (a prime > 1)
                        if (tempE.equals(BigInteger.valueOf(-1)) ||
                                (tempE.compareTo(BigInteger.ONE) > 0 && tempE.isProbablePrime(50))) {
                            eValue = tempE;
                            validE = true;
                        } else {
                            System.out.println(RED + "Invalid e-value. Must be -1 or a prime > 1. Try again." + RESET);
                        }
                    } catch (Exception ignored) {
                        System.out.println(RED + "Invalid input. Try again." + RESET);
                    }
                }
            }
        }

        System.out.println("\n--- Generated RSA Key Pair ---");
        System.out.println(keyPair.toString());

        // 5) ENCRYPT ---------------------------------------------------------------
        BigInteger cipher = RSAUtils.encrypt(message, keyPair.getEncryptKey(), keyPair.getModulus());
        System.out.println("\nEncrypted message: " + cipher);

        // 6) DECRYPT ---------------------------------------------------------------
        BigInteger decrypted = RSAUtils.decrypt(cipher, keyPair.getDecryptKey(), keyPair.getModulus());
        System.out.println("Decrypted message: " + decrypted);

        // 7) COMPARE ---------------------------------------------------------------
        System.out.println("\nMessage matches after decrypt: " + message.equals(decrypted));

        // ===============================================================================================
        // DEMONSTRATION OF IMPROVEMENTS
        // ===============================================================================================

        System.out.println("\n======================================");
        System.out.println("       IMPROVEMENTS DEMO");
        System.out.println("======================================");

        // --- 1. Strong Key Generation ---
        System.out.println("\n[1] Generating Strong Key Pair (Higher Certainty, Checks)...");
        long startKeyGen = System.currentTimeMillis();
        KeyPair strongKeyPair = KeyPair.generateStrongKeyPair(modulusBitLength, eValue);
        long endKeyGen = System.currentTimeMillis();
        System.out.println("Strong Key Pair Generated in " + (endKeyGen - startKeyGen) + " ms.");
        System.out.println("Public Key: (" + strongKeyPair.getEncryptKey() + ", ...)");

        // --- 2. OAEP Encryption ---
        System.out.println("\n[2] OAEP Encryption (Security against Chosen Ciphertext Attack)");
        System.out.println("Encrypting the same message twice with OAEP...");

        BigInteger oaepCipher1 = RSAUtils.encryptOAEP(message, strongKeyPair.getEncryptKey(),
                strongKeyPair.getModulus());
        BigInteger oaepCipher2 = RSAUtils.encryptOAEP(message, strongKeyPair.getEncryptKey(),
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

        BigInteger oaepDecrypted = RSAUtils.decryptOAEP(oaepCipher1, strongKeyPair.getDecryptKey(),
                strongKeyPair.getModulus());
        System.out.println("Decrypted OAEP: " + oaepDecrypted);
        System.out.println("Match: " + message.equals(oaepDecrypted));

        // --- 3. CRT Decryption Speed Test ---
        System.out.println("\n[3] CRT Decryption Speed Test");
        // Warmup
        for (int i = 0; i < 100; i++)
            RSAUtils.decrypt(cipher, keyPair.getDecryptKey(), keyPair.getModulus());

        int iterations = 1000;
        System.out.println("Running " + iterations + " decryptions...");

        // Standard
        long startStd = System.currentTimeMillis();
        for (int i = 0; i < iterations; i++) {
            RSAUtils.decrypt(cipher, strongKeyPair.getDecryptKey(), strongKeyPair.getModulus());
        }
        long endStd = System.currentTimeMillis();
        long timeStd = endStd - startStd;

        // CRT
        long startCRT = System.currentTimeMillis();
        for (int i = 0; i < iterations; i++) {
            RSAUtils.decryptCRT(cipher, strongKeyPair);
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