package com.example;

import java.math.BigInteger;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;

class MyRSAPrivateKey implements RSAPrivateKey {
    private static final long serialVersionUID = 1L;
    private final BigInteger modulus;
    private final BigInteger privateExponent;

    public MyRSAPrivateKey(BigInteger modulus, BigInteger privateExponent) {
        this.modulus = modulus;
        this.privateExponent = privateExponent;
    }

    @Override
    public String getAlgorithm() {
        return "RSA";
    }

    @Override
    public String getFormat() {
        return "PKCS#8"; // Standard format for private keys
    }

    @Override
    public byte[] getEncoded() {
        return null; // Not implemented for this simple example
    }

    @Override
    public BigInteger getModulus() {
        return modulus;
    }

    @Override
    public BigInteger getPrivateExponent() {
        return privateExponent;
    }
}

class MyRSAPublicKey implements RSAPublicKey {
    private static final long serialVersionUID = 1L;
    private final BigInteger modulus;
    private final BigInteger publicExponent;

    public MyRSAPublicKey(BigInteger modulus, BigInteger publicExponent) {
        this.modulus = modulus;
        this.publicExponent = publicExponent;
    }

    @Override
    public String getAlgorithm() {
        return "RSA";
    }

    @Override
    public String getFormat() {
        return "X.509"; // Standard format for public keys
    }

    @Override
    public byte[] getEncoded() {
        return null; // Not implemented for this simple example
    }

    @Override
    public BigInteger getModulus() {
        return modulus;
    }

    @Override
    public BigInteger getPublicExponent() {
        return publicExponent;
    }
}
