package com.sendistudio.base.app.utils;

import org.jasypt.encryption.StringEncryptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import com.sendistudio.base.app.properties.DatabaseProperties;

@Component
public class EncryptUtil {

    @Autowired
    Environment env;

    @Autowired
    DatabaseProperties databaseProperties;

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

    public void encryptDatabaseCredential() {
        String activeProfile = env.getActiveProfiles().length > 0 ? env.getActiveProfiles()[0] : "local";

        String username;
        String password;

        if (activeProfile.equals("dev")) {
            username = databaseProperties.getDev().getUser();
            password = databaseProperties.getDev().getPass();
        } else if (activeProfile.equals("prod")) {
            username = databaseProperties.getProd().getUser();
            password = databaseProperties.getProd().getPass();
        } else {
            username = databaseProperties.getLocal().getUser();
            password = databaseProperties.getLocal().getPass();
        }

        // Mengenkripsi password
        String usernameEncrypted = encryptCredential(username);
        String passwordEncrypted = encryptCredential(password);

        System.out.println("Username: " + username);
        System.out.println("Password: " + password);

        System.out.println("Username Encrypted: " + usernameEncrypted);
        System.out.println("Password Encrypted: " + passwordEncrypted);
    }
}
