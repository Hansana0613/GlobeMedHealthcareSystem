/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.globemed.utils;

import java.util.Base64;

/**
 *
 * @author Hansana
 */
public class SecurityUtils {

    // Simple encryption for demonstration (use proper encryption in production)
    public static String encrypt(String data) {
        if (data == null || data.isEmpty()) {
            return data;
        }

        try {
            // Simple Base64 encoding for demo (use AES in production)
            return Base64.getEncoder().encodeToString(data.getBytes("UTF-8"));
        } catch (Exception e) {
            System.err.println("Encryption error: " + e.getMessage());
            return data; // Return original if encryption fails
        }
    }

    public static String decrypt(String encryptedData) {
        if (encryptedData == null || encryptedData.isEmpty()) {
            return encryptedData;
        }

        try {
            // Simple Base64 decoding for demo
            return new String(Base64.getDecoder().decode(encryptedData), "UTF-8");
        } catch (Exception e) {
            System.err.println("Decryption error: " + e.getMessage());
            return encryptedData; // Return original if decryption fails
        }
    }
}
