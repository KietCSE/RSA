package com.example;

import java.math.BigInteger;
import java.security.KeyPair;
import java.security.KeyPairGeneratorSpi;
import java.security.SecureRandom;
import java.lang.reflect.Method;

public class MyRSAKeyPairGeneratorSpi extends KeyPairGeneratorSpi {
    private int keySize = 1024; // Default

    @Override
    public void initialize(int keysize, SecureRandom random) {
        this.keySize = keysize;
    }

    @Override
    public KeyPair generateKeyPair() {
        try {
            // CALLING ORIGINAL FUNCTION: KeyPair.generateRandomKeyPair(int, BigInteger)
            Class<?> keyPairClass = Class.forName("KeyPair");
            Method generateMethod = keyPairClass.getMethod("generateRandomKeyPair", int.class, BigInteger.class);

            // Invoke: KeyPair.generateRandomKeyPair(keySize, null)
            Object customKeyPair = generateMethod.invoke(null, keySize, null);

            // Get getters via reflection
            Method getModulus = keyPairClass.getMethod("getModulus");
            Method getEncryptKey = keyPairClass.getMethod("getEncryptKey");
            Method getDecryptKey = keyPairClass.getMethod("getDecryptKey");

            BigInteger n = (BigInteger) getModulus.invoke(customKeyPair);
            BigInteger e = (BigInteger) getEncryptKey.invoke(customKeyPair);
            BigInteger d = (BigInteger) getDecryptKey.invoke(customKeyPair);

            // Wrap in JCA keys
            MyRSAPublicKey pubKey = new MyRSAPublicKey(n, e);
            MyRSAPrivateKey privKey = new MyRSAPrivateKey(n, d);

            return new KeyPair(pubKey, privKey);

        } catch (Exception ex) {
            throw new RuntimeException("Failed to generate key pair via reflection", ex);
        }
    }
}
