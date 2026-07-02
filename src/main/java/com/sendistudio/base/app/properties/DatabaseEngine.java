package com.sendistudio.base.app.properties;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * Supported database engines. Bound from `engine:` in properties/database.yaml
 * (case-insensitive). Adding a new engine: one constant here + one entry in
 * `supportedDrivers` in build.gradle.kts.
 */
@Getter
@RequiredArgsConstructor
public enum DatabaseEngine {
    SQLITE("org.sqlite.JDBC", 1, 1), // SQLite supports only 1 concurrent writer
    POSTGRESQL("org.postgresql.Driver", 10, 2),
    MYSQL("com.mysql.cj.jdbc.Driver", 10, 2),
    ORACLE("oracle.jdbc.OracleDriver", 10, 2);

    private final String driverClass;
    private final int maxPoolSize;
    private final int minIdle;
}
