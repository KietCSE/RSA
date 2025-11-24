package com.example;

import java.security.Provider;

public class MyRSAProvider extends Provider {
    private static final long serialVersionUID = 1L;

    public MyRSAProvider() {
        super("MyRSAProvider", 1.0, "Custom RSA Provider wrapping existing implementation");

        // Register KeyPairGenerator
        put("KeyPairGenerator.RSA", "com.example.MyRSAKeyPairGeneratorSpi");

        // Register Cipher
        put("Cipher.RSA", "com.example.MyRSACipherSpi");
    }
}
