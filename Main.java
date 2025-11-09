public class Main {
    public static void main(String[] args) {
        int bitLength = 2048; // RSA key size (modulus)
        KeyPair keyPair = KeyPair.generateRandomKeyPair(bitLength);

        System.out.println("Public key (e): " + keyPair.getEncryptKey());
        System.out.println("Private key (d): " + keyPair.getDecryptKey());
        System.out.println("Modulus (n): " + keyPair.getModulus());
    }
}
