package com.sendistudio.base;

import javax.sql.DataSource;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.env.Environment;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import com.sendistudio.base.app.properties.DatabaseProperties;
import com.sendistudio.base.app.utils.EncryptUtil;

@SpringBootTest
@ActiveProfiles("local")
// @ActiveProfiles("dev")
// @ActiveProfiles("prod")
public class EncryptDatabaseTest {

    @MockitoBean
    private DataSource dataSource;

    @Autowired
    Environment env;

    @Autowired
    DatabaseProperties databaseProperties;

    @Autowired
    EncryptUtil encryptUtil;

    @Test
    void testEncrypt() {
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
        String usernameEncrypted = encryptUtil.encryptCredential(username);
        String passwordEncrypted = encryptUtil.encryptCredential(password);

        System.out.println("Username: " + username);
        System.out.println("Password: " + password);

        System.out.println("Username Encrypted: " + usernameEncrypted);
        System.out.println("Password Encrypted: " + passwordEncrypted);
    }

    @SpringBootApplication
    @EnableConfigurationProperties(DatabaseProperties.class)
    public static class TestApplication {

    }
}
