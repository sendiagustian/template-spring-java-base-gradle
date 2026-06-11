package com.sendistudio.base.app.utils;

import org.jasypt.encryption.StringEncryptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

@Component
public class EncryptUtil {

    @Autowired
    Environment env;

    @Autowired
    private StringEncryptor stringEncryptor;

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
