package com.immortalcrab.warehouse.persistence;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.MessageFormat;
import org.javatuples.Pair;
import org.slf4j.Logger;

public class WarehouseInteractions {

    static Pair<Double, Integer> requestExistancePerPresentation(final Integer productId,
            final Integer presentationId, final Integer warehouseId,
            Connection conn, Logger logger) throws SQLException, Exception {

        MessageFormat mf = new MessageFormat(
                String.format("SELECT exis, decimales FROM ( "
                        + " SELECT ( "
                        + " inv_exi_pres.inicial::double precision + inv_exi_pres.entradas::double precision "
                        + " - inv_exi_pres.salidas::double precision - inv_exi_pres.reservado::double precision "
                        + " ) AS exis, inv_prod_unidades.decimales FROM inv_exi_pres JOIN inv_prod ON inv_prod.id = inv_exi_pres.inv_prod_id "
                        + " JOIN inv_prod_unidades ON inv_prod_unidades.id = inv_prod.unidad_id WHERE inv_exi_pres.inv_alm_id = {0} "
                        + " AND inv_exi_pres.inv_prod_id = {1} AND inv_exi_pres.inv_prod_presentacion_id = {2} )"
                        + " AS sbt WHERE exis > 0 LIMIT 1")
        );

        String sqlQuery = mf.format(new Object[]{warehouseId, productId, presentationId});

        logger.info(sqlQuery);

        Pair<Double, Integer> rp = null;

        {
            try (PreparedStatement stmt = conn.prepareStatement(sqlQuery)) {
                ResultSet rs = stmt.executeQuery();
                if (rs.next()) {
                    rp = new Pair<>(rs.getDouble("exis"), rs.getInt("decimales"));
                } else {
                    throw new Exception("There is not data retrieved");
                }
            } finally {
                conn.close();
            }
        }

        return rp;
    }
}
