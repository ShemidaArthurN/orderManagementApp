package com.example.orderManagement.keyGen;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;

public class HMACKeyGenerator {
    public static void main(String[] args) throws Exception {
        // Create a KeyGenerator for HMAC using SHA-256
        KeyGenerator keyGenerator = KeyGenerator.getInstance("HmacSHA256");
        keyGenerator.init(256); // Set the key size to 256 bits

        // Generate the secret key
        SecretKey secretKey = keyGenerator.generateKey();

        // Print the key in Base64 format
        String encodedKey = java.util.Base64.getEncoder().encodeToString(secretKey.getEncoded());
        System.out.println("Generated Secret Key: " + encodedKey);
    }
}
