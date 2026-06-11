package com.sendistudio.base.app.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "database")
public class DatabaseProperties {

    private Env local = new Env();
    private Env dev = new Env();
    private Env prod = new Env();

    @Getter
    @Setter
    public static class Env {
        private String driver;
        private String url;
        private String username;
        private String password;
    }
}
