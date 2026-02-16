package com.sendistudio.base.properties;

import javax.sql.DataSource;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import com.sendistudio.base.app.properties.DatabaseProperties;

@ActiveProfiles("local")
@SpringBootTest(classes = DatabasePropertiesTest.TestApplication.class)
public class DatabasePropertiesTest {

    @Autowired
    private DatabaseProperties database;

    @MockitoBean
    private DataSource dataSource;

    @Test
    void testDatabase() {
        String secret = database.getSecretEncryptor();
        DatabaseProperties.Local local = database.getLocal();
        DatabaseProperties.Dev dev = database.getDev();
        DatabaseProperties.Prod prod = database.getProd();

        Assertions.assertNotNull(secret);
        Assertions.assertNotNull(local);
        Assertions.assertNotNull(dev);
        Assertions.assertNotNull(prod);
    }

    @SpringBootApplication
    @EnableConfigurationProperties({ DatabaseProperties.class })
    public static class TestApplication {

    }
}
