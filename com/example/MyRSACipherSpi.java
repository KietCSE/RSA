package com.example;

import java.math.BigInteger;
import java.security.*;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.AlgorithmParameterSpec;
import javax.crypto.*;
import java.lang.reflect.Method;

public class MyRSACipherSpi extends CipherSpi {
    private int mode;
    private Key key;
    private BigInteger modulus;
    private BigInteger exponent;

    @Override
    protected void engineSetMode(String mode) throws NoSuchAlgorithmException {
        // Support ECB or None (RSA technically doesn't use ECB in the block cipher
        // sense, but JCA requires it)
        if (!mode.equalsIgnoreCase("ECB") && !mode.equalsIgnoreCase("None")) {
            throw new NoSuchAlgorithmException("Unsupported mode: " + mode);
        }
    }

    @Override
    protected void engineSetPadding(String padding) throws NoSuchPaddingException {
        // For simplicity in this wrapper, we ignore padding or assume NoPadding/PKCS1
        // The underlying implementation handles raw BigInteger math.
        // Real implementation would handle padding bytes here.
    }

    @Override
    protected int engineGetBlockSize() {
        return 0; // RSA is not a block cipher
    }

    @Override
    protected int engineGetOutputSize(int inputLen) {
        // Rough estimate
        return (modulus != null) ? (modulus.bitLength() + 7) / 8 : inputLen;
    }

    @Override
    protected byte[] engineGetIV() {
        return null; // No IV for RSA
    }

    @Override
    protected AlgorithmParameters engineGetParameters() {
        return null;
    }

    @Override
    protected void engineInit(int opmode, Key key, SecureRandom random) throws InvalidKeyException {
        this.mode = opmode;
        this.key = key;

        if (key instanceof RSAPublicKey) {
            this.modulus = ((RSAPublicKey) key).getModulus();
            this.exponent = ((RSAPublicKey) key).getPublicExponent();
        } else if (key instanceof RSAPrivateKey) {
            this.modulus = ((RSAPrivateKey) key).getModulus();
            this.exponent = ((RSAPrivateKey) key).getPrivateExponent();
        } else {
            throw new InvalidKeyException("Unsupported key type");
        }
    }

    @Override
    protected void engineInit(int opmode, Key key, AlgorithmParameterSpec params, SecureRandom random)
            throws InvalidKeyException, InvalidAlgorithmParameterException {
        engineInit(opmode, key, random);
    }

    @Override
    protected void engineInit(int opmode, Key key, AlgorithmParameters params, SecureRandom random)
            throws InvalidKeyException, InvalidAlgorithmParameterException {
        engineInit(opmode, key, random);
    }

    @Override
    protected byte[] engineUpdate(byte[] input, int inputOffset, int inputLen) {
        // RSA is usually done in one go (doFinal), but if update is called, we should
        // buffer.
        // For this simple wrapper, we'll throw exception or return null and expect
        // doFinal to have data.
        // A robust implementation would buffer data here.
        if (inputLen > 0) {
            throw new UnsupportedOperationException("Update not supported, please use doFinal with all data");
        }
        return null;
    }

    @Override
    protected int engineUpdate(byte[] input, int inputOffset, int inputLen, byte[] output, int outputOffset)
            throws ShortBufferException {
        throw new UnsupportedOperationException("Update not supported");
    }

    @Override
    protected byte[] engineDoFinal(byte[] input, int inputOffset, int inputLen)
            throws IllegalBlockSizeException, BadPaddingException {

        byte[] data = new byte[inputLen];
        System.arraycopy(input, inputOffset, data, 0, inputLen);

        // Convert input bytes to BigInteger (positive)
        BigInteger message = new BigInteger(1, data);

        try {
            Class<?> rsaUtilsClass = Class.forName("RSAUtils");
            BigInteger result;

            if (mode == Cipher.ENCRYPT_MODE) {
                // CALLING ORIGINAL FUNCTION: RSAUtils.encrypt(message, e, n)
                Method encryptMethod = rsaUtilsClass.getMethod("encrypt", BigInteger.class, BigInteger.class,
                        BigInteger.class);
                result = (BigInteger) encryptMethod.invoke(null, message, exponent, modulus);
            } else if (mode == Cipher.DECRYPT_MODE) {
                // CALLING ORIGINAL FUNCTION: RSAUtils.decrypt(cipher, d, n)
                Method decryptMethod = rsaUtilsClass.getMethod("decrypt", BigInteger.class, BigInteger.class,
                        BigInteger.class);
                result = (BigInteger) decryptMethod.invoke(null, message, exponent, modulus);
            } else {
                throw new IllegalStateException("Cipher not initialized");
            }

            // Convert BigInteger result back to bytes
            byte[] resBytes = result.toByteArray();

            // Strip leading zero byte if present (BigInteger sign bit)
            if (resBytes[0] == 0 && resBytes.length > 1) {
                byte[] tmp = new byte[resBytes.length - 1];
                System.arraycopy(resBytes, 1, tmp, 0, tmp.length);
                return tmp;
            }
            return resBytes;

        } catch (Exception e) {
            throw new IllegalBlockSizeException("Operation failed: " + e.getMessage());
        }
    }

    @Override
    protected int engineDoFinal(byte[] input, int inputOffset, int inputLen, byte[] output, int outputOffset)
            throws ShortBufferException, IllegalBlockSizeException, BadPaddingException {
        byte[] res = engineDoFinal(input, inputOffset, inputLen);
        if (output.length - outputOffset < res.length) {
            throw new ShortBufferException("Output buffer too small");
        }
        System.arraycopy(res, 0, output, outputOffset, res.length);
        return res.length;
    }
}
