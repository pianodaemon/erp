package com.maxima.reports.dal.rdbms;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;

import org.springframework.jdbc.core.JdbcTemplate;

public class SalesDAO {

    public static ArrayList<HashMap<String, String>> getVentasNetasxCliente(
            final JdbcTemplate jdbcTemplate,
            final String fecha_inicial,
            final String fecha_final,
            final int id_empresa) {

        String sql_to_query = " SELECT * "
                + " from  (select distinct numero_control,  "
                + " cliente, "
                + " pesos,porcentaje, "
                + " sum(totalporfactura)as Tventa_neta    "
                + " from  (  "
                + " SELECT  cxc_clie.numero_control,  "
                + " cxc_clie.razon_social AS cliente, "
                + " '$'::character varying as pesos,  "
                + " (CASE WHEN fac_docs.moneda_id=1 THEN fac_docs.subtotal * 1 ELSE fac_docs.subtotal * fac_docs.tipo_cambio END) AS totalporfactura,  "
                + " '%'::character varying as porcentaje   "
                + " FROM fac_docs     "
                + " JOIN erp_proceso on erp_proceso.id=fac_docs.proceso_id  "
                + " JOIN cxc_clie ON cxc_clie.id = fac_docs.cxc_clie_id     "
                + " JOIN gral_mon ON gral_mon.id = fac_docs.moneda_id  "
                + " WHERE fac_docs.cancelado = false    "
                + " AND erp_proceso.empresa_id = " + id_empresa + " "
                + " AND to_char(fac_docs.momento_creacion,'yyyymmdd')   "
                + " between to_char('" + fecha_inicial + "'::timestamp with time zone,'yyyymmdd')  "
                + " and to_char('" + fecha_final + "'::timestamp with time zone,'yyyymmdd')  "
                + " order by numero_control asc   "
                + " ) as sbt    "
                + " GROUP BY sbt.numero_control,  "
                + " sbt.cliente,  "
                + " sbt.pesos,  "
                + " sbt.porcentaje  "
                + " ORDER BY sbt.numero_control asc "
                + " )as tb3  "
                + " order by tventa_neta desc ";

        return (ArrayList<HashMap<String, String>>) jdbcTemplate.query(
                sql_to_query,
                new Object[]{}, (ResultSet rs, int rowNum) -> {
                    HashMap<String, String> row = new HashMap<>();

                    row.put("numero_control", rs.getString("numero_control"));
                    row.put("cliente", rs.getString("cliente"));
                    row.put("Tventa_neta", rs.getString("Tventa_neta"));
                    row.put("pesos", rs.getString("pesos"));
                    row.put("porcentaje", rs.getString("porcentaje"));
                    row.put("numero_control", rs.getString("numero_control"));

                    return row;
                });
    }
}
