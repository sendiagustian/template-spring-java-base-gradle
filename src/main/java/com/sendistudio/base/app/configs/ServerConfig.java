package com.sendistudio.base.app.configs;

import java.net.InetAddress;
import java.net.UnknownHostException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.server.ConfigurableWebServerFactory;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.scheduling.annotation.Async;

import com.sendistudio.base.app.properties.ServerProperties;

/*
    * Server Configuration Class
*/
@Configuration
public class ServerConfig implements WebServerFactoryCustomizer<ConfigurableWebServerFactory> {

    @Autowired
    Environment env;

    @Autowired
    ServerProperties server;

    @Override
    @Async
    public void customize(ConfigurableWebServerFactory factory) {
        String activeProfile = env.getActiveProfiles().length > 0 ? env.getActiveProfiles()[0] : "local";

        if ("dev".equals(activeProfile)) {
            try {
                factory.setPort(Integer.parseInt(server.getDev().getPort()));
                factory.setAddress(InetAddress.getByName(server.getDev().getAddress()));
            } catch (UnknownHostException e) {
                e.printStackTrace();
            }
        } else if ("prod".equals(activeProfile)) {
            try {
                factory.setPort(Integer.parseInt(server.getProd().getPort()));
                factory.setAddress(InetAddress.getByName(server.getProd().getAddress()));
            } catch (UnknownHostException e) {
                e.printStackTrace();
            }
        } else {
            try {
                factory.setPort(Integer.parseInt(server.getLocal().getPort()));
                factory.setAddress(InetAddress.getByName(server.getLocal().getAddress()));
            } catch (UnknownHostException e) {
                e.printStackTrace();
            }
        }
    }
}