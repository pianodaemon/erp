package com.immortalcrab.warehouse.persistence;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Map;
import org.apache.commons.dbcp2.BasicDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class PgsqlConnPool {

    private BasicDataSource ds;

    private BasicDataSource configureConnectionPool() {

        Map<String, String> envConf = System.getenv();

        String jdbcUrl = String.format("jdbc:postgresql://%s:%s/%s",
                envConf.getOrDefault("POSTGRES_HOST", "rdbms_dos"),
                envConf.getOrDefault("POSTGRES_PORT", "5432"),
                envConf.getOrDefault("POSTGRES_DB", "erp"));

        this.logger.info("Setting up connection pool with {}", jdbcUrl);

        BasicDataSource dataSource = new BasicDataSource();
        dataSource.setDriverClassName("org.postgresql.Driver");
        dataSource.setUrl(jdbcUrl);
        dataSource.setUsername(envConf.getOrDefault("POSTGRES_USER", "postgres"));
        dataSource.setPassword(envConf.getOrDefault("POSTGRES_PASSWORD", "postgres"));
        dataSource.setMinIdle(5);
        dataSource.setMaxIdle(10);
        dataSource.setMaxOpenPreparedStatements(100);
        dataSource.setTestWhileIdle(true);
        dataSource.setValidationQuery("SELECT 1 as test");

        return dataSource;
    }

    private PgsqlConnPool() {
        this.ds = this.configureConnectionPool();
    }

    public Connection getConnection() throws SQLException {
        return ds.getConnection();
    }

    public static synchronized PgsqlConnPool getInstance() {

        if (PgsqlConnPool.INSTANCE == null) {

            PgsqlConnPool.INSTANCE = new PgsqlConnPool();
        }
        return PgsqlConnPool.INSTANCE;
    }

    private static PgsqlConnPool INSTANCE;
    private final Logger logger = LoggerFactory.getLogger(PgsqlConnPool.class);
}
