package com.sendistudio.base.app.configs;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

import com.zaxxer.hikari.HikariDataSource;

import com.sendistudio.base.app.properties.DatabaseProperties;
import com.sendistudio.base.app.utils.EncryptUtil;

/*
    * Database Configuration Class
*/
@Configuration
public class DatabaseConfig {

    @Autowired
    private Environment env;

    @Autowired
    private DatabaseProperties databaseProperties;

    @Autowired
    private EncryptUtil encryptUtil;

    @Bean
    DataSource dataSource() {
        HikariDataSource dataSource = new HikariDataSource();

        String activeProfile = env.getActiveProfiles().length > 0 ? env.getActiveProfiles()[0] : "local";

        // Set PostgreSQL driver
        dataSource.setDriverClassName("org.postgresql.Driver");

        // Set PostgreSQL connection
        if ("dev".equals(activeProfile)) {
            dataSource.setJdbcUrl("jdbc:postgresql://" + databaseProperties.getDev().getHost() + ":"
                    + databaseProperties.getDev().getPort() + "/" + databaseProperties.getDev().getName());
            dataSource.setUsername(encryptUtil.decryptCredential(databaseProperties.getDev().getUser()));
            dataSource.setPassword(encryptUtil.decryptCredential(databaseProperties.getDev().getPass()));
        } else if ("prod".equals(activeProfile)) {
            dataSource.setJdbcUrl("jdbc:postgresql://" + databaseProperties.getProd().getHost() + ":"
                    + databaseProperties.getProd().getPort() + "/" + databaseProperties.getProd().getName());
            dataSource.setUsername(encryptUtil.decryptCredential(databaseProperties.getProd().getUser()));
            dataSource.setPassword(encryptUtil.decryptCredential(databaseProperties.getProd().getPass()));
        } else {
            dataSource.setJdbcUrl("jdbc:postgresql://" + databaseProperties.getLocal().getHost() + ":"
                    + databaseProperties.getLocal().getPort() + "/" + databaseProperties.getLocal().getName());
            dataSource.setUsername(encryptUtil.decryptCredential(databaseProperties.getLocal().getUser()));
            dataSource.setPassword(encryptUtil.decryptCredential(databaseProperties.getLocal().getPass()));
        }

        // Additional HikariCP settings
        dataSource.setMaximumPoolSize(10); // Set max pool size
        dataSource.setMinimumIdle(5); // Minimum idle connections
        dataSource.setIdleTimeout(10000); // Connection idle timeout (10s)
        dataSource.setConnectionTimeout(5000); // Timeout to get connection (5s)
        dataSource.setMaxLifetime(1800000); // Maximum lifetime of a connection (30m)
        dataSource.setLeakDetectionThreshold(60000); // Leak detection threshold (60s)

        // Additional settings
        dataSource.setPoolName("MyAppConnectionDatabasePool");
        dataSource.setConnectionTestQuery("SELECT 1");
        dataSource.addDataSourceProperty("cachePrepStmts", "true");
        dataSource.addDataSourceProperty("prepStmtCacheSize", "250");
        dataSource.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
        dataSource.addDataSourceProperty("useServerPrepStmts", "true");
        dataSource.addDataSourceProperty("useLocalSessionState", "true");
        dataSource.addDataSourceProperty("rewriteBatchedStatements", "true");
        dataSource.addDataSourceProperty("cacheResultSetMetadata", "true");
        dataSource.addDataSourceProperty("elideSetAutoCommits", "true");
        dataSource.addDataSourceProperty("maintainTimeStats", "false");

        return dataSource;
    }
}
