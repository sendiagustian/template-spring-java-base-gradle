package com.sendistudio.base.app.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@ConfigurationProperties(prefix = "sendistudio.server")
public class ServerProperties {
    private Local local;
    private Dev dev;
    private Prod prod;

    @Getter
    @Setter
    public static class Local {
        private String address;
        private String host;
        private String port;
    }

    @Getter
    @Setter
    public static class Dev {
        private String address;
        private String host;
        private String port;
    }

    @Getter
    @Setter
    public static class Prod {
        private String address;
        private String host;
        private String port;
    }
}
