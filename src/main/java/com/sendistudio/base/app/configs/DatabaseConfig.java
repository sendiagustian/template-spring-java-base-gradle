package com.sendistudio.base.app.configs;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import javax.sql.DataSource;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

import com.sendistudio.base.app.properties.DatabaseEngine;
import com.sendistudio.base.app.properties.DatabaseProperties;
import com.sendistudio.base.app.properties.DatabaseProperties.Env;
import com.zaxxer.hikari.HikariDataSource;

import lombok.RequiredArgsConstructor;

/*
 * Database Configuration Class
 * Engine is selected per profile via `engine:` in properties/database.yaml
 * (default: SQLite on local). The matching JDBC driver must be included at
 * build time via `dbEngines` in gradle.properties or -PdbEngines=...
 */
@Configuration
@RequiredArgsConstructor
public class DatabaseConfig {

    private final Environment env;

    private final DatabaseProperties databaseProperties;

    private Env resolveConfig() {
        String activeProfile = env.getActiveProfiles().length > 0 ? env.getActiveProfiles()[0] : "local";

        return switch (activeProfile) {
            case "dev"  -> databaseProperties.getDev();
            case "prod" -> databaseProperties.getProd();
            default     -> databaseProperties.getLocal();
        };
    }

    @Bean
    DatabaseEngine databaseEngine() {
        DatabaseEngine engine = databaseProperties.getEngine();
        if (engine == null) {
            throw new IllegalStateException(
                    "Missing 'database.engine' in properties/database.yaml. "
                            + "Set one of: sqlite, postgresql, mysql, oracle");
        }
        return engine;
    }

    @Bean
    DataSource dataSource(DatabaseEngine engine) {
        Env config = resolveConfig();

        try {
            Class.forName(engine.getDriverClass());
        } catch (ClassNotFoundException e) {
            throw new IllegalStateException(
                    "JDBC driver for " + engine + " is not on the classpath. "
                            + "Rebuild with -PdbEngines=" + engine.name().toLowerCase()
                            + " or add it to dbEngines in gradle.properties",
                    e);
        }

        if (engine == DatabaseEngine.SQLITE) {
            ensureSqliteDirectoryExists(config.getUrl());
        }

        HikariDataSource dataSource = new HikariDataSource();
        dataSource.setDriverClassName(engine.getDriverClass());
        dataSource.setJdbcUrl(config.getUrl());

        if (config.getUsername() != null && !config.getUsername().isBlank()) {
            dataSource.setUsername(config.getUsername());
        }
        if (config.getPassword() != null && !config.getPassword().isBlank()) {
            dataSource.setPassword(config.getPassword());
        }

        dataSource.setMaximumPoolSize(engine.getMaxPoolSize());
        dataSource.setMinimumIdle(engine.getMinIdle());

        dataSource.setConnectionTimeout(5_000);
        dataSource.setIdleTimeout(300_000);
        dataSource.setMaxLifetime(1_800_000);
        dataSource.setPoolName("AppConnectionPool");

        return dataSource;
    }

    // The SQLite driver creates the db file but not its parent directories
    private void ensureSqliteDirectoryExists(String jdbcUrl) {
        String path = jdbcUrl.replaceFirst("^jdbc:sqlite:", "");
        if (path.isBlank() || path.startsWith(":memory:") || path.startsWith("file::memory:")) {
            return;
        }

        Path parent = Path.of(path).toAbsolutePath().getParent();
        if (parent == null) {
            return;
        }

        try {
            Files.createDirectories(parent);
        } catch (IOException e) {
            throw new IllegalStateException("Failed to create SQLite data directory: " + parent, e);
        }
    }
}
