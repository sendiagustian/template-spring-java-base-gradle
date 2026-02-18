package com.sendistudio.base.app.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Validated
@ConfigurationProperties(prefix = "sendistudio.third-party")
public class ThirdPartyProperties {
    private Xendit xendit;

    @Getter
    @Setter
    public static class Xendit {
        private String apiKeyDev;
        private String apiKeyProd;
    }
}
