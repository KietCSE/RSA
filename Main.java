import java.math.BigInteger;
import java.util.Scanner;

class Main {
    // Minimum practical bit length for RSA
    private static final int MIN_BIT_LENGTH = 1024;

    public static void main(String[] args) {
        final String RED = "\u001B[31m";
        final String RESET = "\u001B[0m";

        Scanner scanner = new Scanner(System.in);

        System.out.println("======================================");
        System.out.println("          RSA Key Generator");
        System.out.println("======================================");

        int modulusBitLength = 0; // Renamed for clarity (length of N)
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
        long startTime = System.currentTimeMillis();

        System.out.println("\n--- Generated RSA Key Pair ---");
        KeyPair keyPair = KeyPair.generateStrongKeyPair(modulusBitLength);
        System.out.println(keyPair.toString());

        // Create RSAUtils instance for encryption/decryption operations
        RSAUtils rsaUtils = new RSAUtils();

        // 5) ENCRYPT ---------------------------------------------------------------
        BigInteger cipher = rsaUtils.encryptOAEP(message, keyPair.getEncryptKey(), keyPair.getModulus());
        System.out.println("\nEncrypted message: " + cipher);

        // 6) DECRYPT ---------------------------------------------------------------
        BigInteger decrypted = rsaUtils.decryptOAEP_CRT(cipher, keyPair);
        System.out.println("Decrypted message: " + decrypted);

        // 7) COMPARE ---------------------------------------------------------------
        System.out.println("\nMessage matches after decrypt: " + message.equals(decrypted));

        long endTime = System.currentTimeMillis();
        long duration = (endTime - startTime);
        System.out.println("\nDuration: " + duration + " ms");

        scanner.close();
    }
}