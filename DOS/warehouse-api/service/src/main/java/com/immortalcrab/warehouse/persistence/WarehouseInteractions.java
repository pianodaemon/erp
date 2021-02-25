package com.immortalcrab.warehouse.persistence;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.javatuples.Pair;
import org.slf4j.Logger;

public class WarehouseInteractions {

    public static Pair<Double, Integer> requestExistancePerPresentation(final Integer productId,
            final Integer presentationId, final Integer warehouseId,
            Connection conn, Logger logger) throws SQLException, Exception {

        String sqlQuery = String.format("SELECT exis, decimales FROM ( "
                + " SELECT ( "
                + " inv_exi_pres.inicial::double precision + inv_exi_pres.entradas::double precision "
                + " - inv_exi_pres.salidas::double precision - inv_exi_pres.reservado::double precision "
                + " ) AS exis, inv_prod_unidades.decimales FROM inv_exi_pres JOIN inv_prod ON inv_prod.id = inv_exi_pres.inv_prod_id "
                + " JOIN inv_prod_unidades ON inv_prod_unidades.id = inv_prod.unidad_id WHERE inv_exi_pres.inv_alm_id = %d "
                + " AND inv_exi_pres.inv_prod_id = %d AND inv_exi_pres.inv_prod_presentacion_id = %d )"
                + " AS sbt WHERE exis > 0 LIMIT 1", warehouseId, productId, presentationId);

        logger.info(sqlQuery);

        Pair<Double, Integer> rp = null;

        {
            try (PreparedStatement stmt = conn.prepareStatement(sqlQuery)) {
                ResultSet rs = stmt.executeQuery();
                rp = rs.next() ? new Pair<>(rs.getDouble("exis"), rs.getInt("decimales")) : new Pair<>(new Double(0), 0);
            } finally {
                conn.close();
            }
        }

        return rp;
    }
}
