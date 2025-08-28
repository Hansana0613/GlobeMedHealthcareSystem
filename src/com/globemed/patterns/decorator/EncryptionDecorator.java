/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.globemed.patterns.decorator;

import com.globemed.utils.SecurityUtils;
import java.util.Base64;

/**
 *
 * @author Hansana
 */
public class EncryptionDecorator<T> extends SecurityDecorator<T> {

    private String encryptionKey;

    public EncryptionDecorator(SecureService<T> service, String encryptionKey) {
        super(service, "Encryption");
        this.encryptionKey = encryptionKey;
    }

    @Override
    public T execute(T data) throws SecurityException {
        try {
            // Encrypt data before processing
            T encryptedData = encryptData(data);
            logAccess("ENCRYPTION", "Data encrypted before processing");

            // Process encrypted data
            T processedData = super.execute(encryptedData);

            // Decrypt result before returning
            T decryptedResult = decryptData(processedData);
            logAccess("DECRYPTION", "Data decrypted after processing");

            return decryptedResult;

        } catch (Exception e) {
            logAccess("ENCRYPTION_ERROR", "Encryption/decryption failed: " + e.getMessage());
            throw new SecurityException("Encryption error: " + e.getMessage());
        }
    }

    @SuppressWarnings("unchecked")
    private T encryptData(T data) {
        if (data instanceof String) {
            String encryptedString = SecurityUtils.encrypt((String) data);
            return (T) encryptedString;
        }
        // For other data types, convert to string, encrypt, then convert back
        // This is a simplified approach for demonstration
        return data;
    }

    @SuppressWarnings("unchecked")
    private T decryptData(T data) {
        if (data instanceof String) {
            String decryptedString = SecurityUtils.decrypt((String) data);
            return (T) decryptedString;
        }
        return data;
    }
}
