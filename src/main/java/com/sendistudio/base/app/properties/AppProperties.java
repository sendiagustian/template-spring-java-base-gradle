package com.sendistudio.base.app.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@ConfigurationProperties("application")
public class AppProperties {

    private String version;
    private String name;
    private String desc;
    private Contact contact;

    @Getter
    @Setter
    public static class Contact {
        private String name;
        private String email;
    }
}
