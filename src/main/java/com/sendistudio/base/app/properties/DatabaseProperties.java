package com.sendistudio.base.app.properties;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Validated
@ConfigurationProperties(prefix = "sendistudio.database")
public class DatabaseProperties {

    @Value("${sendistudio.secret.secretEncryptor}")
    private String secretEncryptor;

    @Value("${sendistudio.secret.secretToken}")
    private String secretToken;

    @Value("${sendistudio.secret.secretRefresh}")
    private String secretRefresh;

    @Value("${sendistudio.secret.secretForgotPassword}")
    private String secretForgotPassword;

    private Local local;

    private Dev dev;

    private Prod prod;

    @Getter
    @Setter
    public static class Local {
        private String host;

        private String port;

        private String user;

        private String pass;

        private String name;
    }

    @Getter
    @Setter
    public static class Dev {
        private String host;

        private String port;

        private String user;

        private String pass;

        private String name;
    }

    @Getter
    @Setter
    public static class Prod {
        private String host;

        private String port;

        private String user;

        private String pass;

        private String name;
    }
}
