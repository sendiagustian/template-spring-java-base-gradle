package com.sendistudio.base.app.configs;

import javax.sql.DataSource;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

import com.sendistudio.base.app.properties.DatabaseProperties;
import com.sendistudio.base.app.properties.DatabaseProperties.Env;
import com.zaxxer.hikari.HikariDataSource;

import lombok.RequiredArgsConstructor;

/*
 * Database Configuration Class
 * Default: SQLite (local). Swap driver + url in database.yaml for PostgreSQL/MySQL.
 */
@Configuration
@RequiredArgsConstructor
public class DatabaseConfig {

    private final Environment env;

    private final DatabaseProperties databaseProperties;

    @Bean
    DataSource dataSource() {
        String activeProfile = env.getActiveProfiles().length > 0 ? env.getActiveProfiles()[0] : "local";

        Env config = switch (activeProfile) {
            case "dev"  -> databaseProperties.getDev();
            case "prod" -> databaseProperties.getProd();
            default     -> databaseProperties.getLocal();
        };

        HikariDataSource dataSource = new HikariDataSource();
        dataSource.setDriverClassName(config.getDriver());
        dataSource.setJdbcUrl(config.getUrl());

        if (config.getUsername() != null && !config.getUsername().isBlank()) {
            dataSource.setUsername(config.getUsername());
        }
        if (config.getPassword() != null && !config.getPassword().isBlank()) {
            dataSource.setPassword(config.getPassword());
        }

        boolean isSQLite = config.getDriver() != null && config.getDriver().toLowerCase().contains("sqlite");
        if (isSQLite) {
            // SQLite supports only 1 concurrent writer
            dataSource.setMaximumPoolSize(1);
            dataSource.setMinimumIdle(1);
        } else {
            dataSource.setMaximumPoolSize(10);
            dataSource.setMinimumIdle(2);
        }

        dataSource.setConnectionTimeout(5_000);
        dataSource.setIdleTimeout(300_000);
        dataSource.setMaxLifetime(1_800_000);
        dataSource.setConnectionTestQuery("SELECT 1");
        dataSource.setPoolName("AppConnectionPool");

        return dataSource;
    }
}
