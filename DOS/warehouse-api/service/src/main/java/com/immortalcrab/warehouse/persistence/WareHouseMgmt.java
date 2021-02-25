package com.immortalcrab.warehouse.persistence;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Map;
import org.apache.commons.dbcp2.BasicDataSource;
import java.text.MessageFormat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WareHouseMgmt {

    int counter;
    private BasicDataSource ds;

    void requestExistancePerPresentation(final Integer productId,
            final Integer presentationId, final Integer warehouseId) {

        MessageFormat mf = new MessageFormat(
                String.format("SELECT exis, decimales FROM ( "
                        + " SELECT ( "
                        + " inv_exi_pres.inicial::double precision + inv_exi_pres.entradas::double precision "
                        + " - inv_exi_pres.salidas::double precision - inv_exi_pres.reservado::double precision "
                        + ") AS exis, inv_prod_unidades.decimales FROM inv_exi_pres JOIN inv_prod ON inv_prod.id=inv_exi_pres.inv_prod_id "
                        + " JOIN inv_prod_unidades ON inv_prod_unidades.id=inv_prod.unidad_id WHERE inv_exi_pres.inv_alm_id=? "
                        + " AND inv_exi_pres.inv_prod_id=? AND inv_exi_pres.inv_prod_presentacion_id=?)AS sbt WHERE exis>0 ")
        );

        String sqlQuery = mf.format(new Object[]{warehouseId, productId, presentationId});

        this.logger.info(sqlQuery);
    }

    private BasicDataSource configureConnectionPool() {

        Map<String, String> envConf = System.getenv();
        String urlBody = String.format("jdbc:postgresql://{0}:{1}/{2}");
        MessageFormat mf = new MessageFormat(urlBody);
        String jdbcUrl = mf.format(new Object[]{
            envConf.getOrDefault("POSTGRES_HOST", "rdbms_dos"),
            envConf.getOrDefault("POSTGRES_PORT", "5432"),
            envConf.getOrDefault("POSTGRES_DB", "erp")});

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

    private WareHouseMgmt() {
        this.counter = 0;
        this.ds = this.configureConnectionPool();
    }

    public int up() {
        return ++this.counter;
    }

    private Connection getConnection() throws SQLException {
        return ds.getConnection();
    }

    public static synchronized WareHouseMgmt getInstance() {

        if (WareHouseMgmt.INSTANCE == null) {

            WareHouseMgmt.INSTANCE = new WareHouseMgmt();
        }
        return WareHouseMgmt.INSTANCE;
    }

    private static WareHouseMgmt INSTANCE;
    private final Logger logger = LoggerFactory.getLogger(WareHouseMgmt.class);
}
