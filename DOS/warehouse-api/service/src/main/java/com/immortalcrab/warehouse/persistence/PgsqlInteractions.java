package com.immortalcrab.warehouse.persistence;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.NoSuchElementException;
import org.javatuples.Pair;
import org.slf4j.Logger;

public class PgsqlInteractions {

    public static Pair<Double, Integer> existancePerPresentation(final Integer productId,
            final Integer presentationId, final Integer warehouseId,
            Logger logger) throws SQLException, NoSuchElementException {

        String sqlQuery = String.format("SELECT exis, decimales FROM ( "
                + " SELECT ( "
                + " inv_exi_pres.inicial::double precision + inv_exi_pres.entradas::double precision "
                + " - inv_exi_pres.salidas::double precision - inv_exi_pres.reservado::double precision "
                + " ) AS exis, inv_prod_unidades.decimales FROM inv_exi_pres JOIN inv_prod ON inv_prod.id = inv_exi_pres.inv_prod_id "
                + " JOIN inv_prod_unidades ON inv_prod_unidades.id = inv_prod.unidad_id WHERE inv_exi_pres.inv_alm_id = %d "
                + " AND inv_exi_pres.inv_prod_id = %d AND inv_exi_pres.inv_prod_presentacion_id = %d )"
                + " AS sbt WHERE exis > 0 LIMIT 1", warehouseId, productId, presentationId);

        Connection conn = PgsqlConnPool.getInstance().getConnection();

        Pair<Double, Integer> rp = null;

        {
            logger.info(sqlQuery);
            try (PreparedStatement stmt = conn.prepareStatement(sqlQuery)) {
                ResultSet rs = stmt.executeQuery();
                if (rs.next()) {
                    rp = new Pair<>(rs.getDouble("exis"), rs.getInt("decimales"));
                } else {
                    throw new NoSuchElementException("Presentation not found");
                }
            } finally {
                conn.close();
            }
        }

        return rp;
    }

    public static ArrayList<Pair<Integer, String>> getWarehouses(
            final Integer empresaId,
            Logger logger) throws SQLException, NoSuchElementException {

        String sqlQuery = String.format(
                "SELECT DISTINCT inv_alm.id, inv_alm.titulo "
               +  "FROM inv_alm "
               +  "JOIN inv_suc_alm ON inv_suc_alm.almacen_id = inv_alm.id "
               +  "JOIN gral_suc ON gral_suc.id = inv_suc_alm.sucursal_id "
               + "WHERE gral_suc.empresa_id = %d AND inv_alm.borrado_logico = FALSE;", empresaId);

        Connection conn = PgsqlConnPool.getInstance().getConnection();

        ArrayList<Pair<Integer, String>> al = new ArrayList<>();

        {
            logger.info(sqlQuery);
            try (PreparedStatement stmt = conn.prepareStatement(sqlQuery)) {
                ResultSet rs = stmt.executeQuery();

                if (rs.next()) {
                    do {
                        al.add(new Pair<>(rs.getInt("id"), rs.getString("titulo")));
                    } while (rs.next());

                } else {
                    throw new NoSuchElementException("Warehouses not found");
                }

            } finally {
                conn.close();
            }
        }

        return al;
    }
}
