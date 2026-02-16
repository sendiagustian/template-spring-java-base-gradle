package com.sendistudio.base.app.configs;

import java.util.List;
import java.util.ArrayList;
import java.util.Comparator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

import com.sendistudio.base.app.properties.AppProperties;
import com.sendistudio.base.app.properties.ServerProperties;
import com.sendistudio.base.constants.ScalarTagConst;
import com.sendistudio.base.constants.types.ScalarTagType;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import io.swagger.v3.oas.models.tags.Tag;

@Configuration
public class ScalarConfig {

    @Autowired
    Environment env;

    @Autowired
    AppProperties appProperties;

    @Autowired
    ServerProperties serverProperties;

    @Autowired
    ScalarTagConst scalarTagConst;

    @Bean
    OpenAPI customOpenAPI() {

        String activeProfile = env.getActiveProfiles().length > 0 ? env.getActiveProfiles()[0] : "local";
        String allowedHost;

        if ("dev".equals(activeProfile)) {
            allowedHost = serverProperties.getDev().getHost() + ":" + serverProperties.getDev().getPort();
        } else if ("prod".equals(activeProfile)) {
            allowedHost = serverProperties.getProd().getHost();
        } else {
            allowedHost = serverProperties.getLocal().getHost() + ":" + serverProperties.getLocal().getPort();
        }

        Server server = new Server();
        server.setUrl(allowedHost);

        Contact contact = new Contact();
        contact.setEmail(appProperties.getContact().getEmail());
        contact.setName(appProperties.getContact().getName());

        License mitLicense = new License().name("MIT License").url("https://choosealicense.com/licenses/mit/");

        Info info = new Info().title(appProperties.getName()).version(appProperties.getVersion()).contact(contact).description(appProperties.getDesc()).license(mitLicense);

        List<ScalarTagType> tagName = new ArrayList<>(scalarTagConst.getAllTags());
        tagName.sort(Comparator.comparingInt(ScalarTagType::getOrder));

        List<Tag> tags = new ArrayList<>();
        for (ScalarTagType tag : tagName) {
            tags.add(new Tag().name(tag.getName()).description(tag.getDescription()));
        }

        return new OpenAPI().info(info).tags(tags).servers(List.of(server));
    }
}
