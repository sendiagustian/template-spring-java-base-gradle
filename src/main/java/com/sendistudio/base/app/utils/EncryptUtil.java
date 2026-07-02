package com.sendistudio.base.app.utils;

import org.jasypt.encryption.StringEncryptor;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class EncryptUtil {

    private final StringEncryptor stringEncryptor;

    public String encryptCredential(String input) {
        String encrypted = stringEncryptor.encrypt(input);
        return encrypted;
    }

    public String decryptCredential(String input) {
        // Mendekripsi password
        String decrypted = stringEncryptor.decrypt(input);
        return decrypted;
    }
}
