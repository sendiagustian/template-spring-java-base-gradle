package com.sendistudio.base;

import javax.sql.DataSource;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import com.sendistudio.base.app.properties.ServerProperties;

@ActiveProfiles("local")
@SpringBootTest(classes = ServerPropertiesTest.TestApplication.class)
public class ServerPropertiesTest {

    @Autowired
    private ServerProperties server;

    @MockitoBean
    private DataSource dataSource;

    @Test
    void testServer() {
        ServerProperties.Local local = server.getLocal();
        ServerProperties.Dev dev = server.getDev();
        ServerProperties.Prod prod = server.getProd();

        System.out.println("LOCAL ENV");
        System.out.println(local.getHost());
        System.out.println(local.getAddress());
        System.out.println(local.getPort());

        System.out.println("DEV ENV");
        System.out.println(dev.getHost());
        System.out.println(dev.getAddress());
        System.out.println(dev.getPort());

        System.out.println("PROD ENV");
        System.out.println(prod.getHost());
        System.out.println(prod.getAddress());
        System.out.println(prod.getPort());

        Assertions.assertNotNull(local);
        Assertions.assertNotNull(dev);
        Assertions.assertNotNull(prod);
    }

    @SpringBootApplication
    @EnableConfigurationProperties({ ServerProperties.class })
    public static class TestApplication {

    }
}
