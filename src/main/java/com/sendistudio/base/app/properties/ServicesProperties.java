package com.sendistudio.base.app.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Validated
@ConfigurationProperties(prefix = "sendistudio.services")
public class ServicesProperties {
    
    private Profile local;
    private Profile dev;
    private Profile prod;

    @Getter
    @Setter
    public static class Profile {
        private ServiceEndpoint storage;
        private ServiceEndpoint anyService;
    }

    @Getter
    @Setter
    public static class ServiceEndpoint {
        private String baseUrl;
        private String uploadUrl;
    }
}
