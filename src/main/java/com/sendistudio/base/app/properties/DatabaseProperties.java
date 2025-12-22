package com.sendistudio.base.app.properties;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Validated
@ConfigurationProperties(prefix = "sendistudio.database")
public class DatabaseProperties {

    @NotNull
    @Value("${sendistudio.jasypt.encryptor.secret}")
    private String secret;

    @NotNull
    private Local local;

    @NotNull
    private Dev dev;

    @NotNull
    private Prod prod;

    @Getter
    @Setter
    public static class Local {
        @NotBlank
        private String host;

        @NotBlank
        private String port;

        @NotBlank
        private String user;

        @NotBlank
        private String pass;

        @NotBlank
        private String name;
    }

    @Getter
    @Setter
    public static class Dev {
        @NotBlank
        private String host;

        @NotBlank
        private String port;

        @NotBlank
        private String user;

        @NotBlank
        private String pass;

        @NotBlank
        private String name;
    }

    @Getter
    @Setter
    public static class Prod {
        @NotBlank
        private String host;

        @NotBlank
        private String port;

        @NotBlank
        private String user;

        @NotBlank
        private String pass;

        @NotBlank
        private String name;
    }
}
