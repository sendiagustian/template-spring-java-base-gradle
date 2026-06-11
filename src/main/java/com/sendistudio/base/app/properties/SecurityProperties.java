package com.sendistudio.base.app.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@ConfigurationProperties("sendistudio.security")
public class SecurityProperties {

    private Jwt jwt = new Jwt();

    @Getter
    @Setter
    public static class Jwt {
        private String secretToken;
        private long accessTokenExpiry = 3_600_000L;
        private long refreshTokenExpiry = 86_400_000L;
    }
}
