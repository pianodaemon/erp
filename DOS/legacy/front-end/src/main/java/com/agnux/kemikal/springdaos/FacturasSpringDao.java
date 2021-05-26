/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.agnux.kemikal.springdaos;

import com.agnux.common.helpers.StringHelper;
import com.agnux.common.helpers.TimeHelper;
import com.agnux.common.helpers.n2t;
import com.agnux.kemikal.interfacedaos.FacturasInterfaceDao;
import java.math.BigInteger;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

/**
 *
 * @author Noé Martinez
 * gpmarsan@gmail.com
 * 
 */

public class FacturasSpringDao implements FacturasInterfaceDao{
    private JdbcTemplate jdbcTemplate;
    private String fechaComprobante;
    private String subtotalConDescuento;
    private String montoDescuento;
    private String subTotal;
    private String impuestoTrasladado;
    private String impuestoRetenido;
    private String tasaRetencion;
    private String total;
    
    public JdbcTemplate getJdbcTemplate() {
        return jdbcTemplate;
    }
    
    public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }
    
    public String getMontoDescuento() {
        return montoDescuento;
    }

    public void setMontoDescuento(String montoDescuento) {
        this.montoDescuento = montoDescuento;
    }
    
    public String getSubTotal() {
        return subTotal;
    }
    
    public void setSubTotal(String subTotal) {
        this.subTotal = subTotal;
    }
    
    public String getSubtotalConDescuento() {
        return subtotalConDescuento;
    }

    public void setSubtotalConDescuento(String subtotalConDescuento) {
        this.subtotalConDescuento = subtotalConDescuento;
    }
    
    public String getImpuestoTrasladado() {
        return impuestoTrasladado;
    }
    public void setImpuestoTrasladado(String impuesto) {
        this.impuestoTrasladado = impuesto;
    }
    
    public String getImpuestoRetenido() {
        return impuestoRetenido;
    }

    public void setImpuestoRetenido(String impuestoRetenido) {
        this.impuestoRetenido = impuestoRetenido;
    }
    
    public String getTasaRetencion() {
        return tasaRetencion;
    }

    public void setTasaRetencion(String tasaRetencion) {
        this.tasaRetencion = tasaRetencion;
    }
    
    
    public String getTotal() {
        return total;
    }
    
    public void setTotal(String total) {
        this.total = total;
    }
    
    public void setFechaComprobante(String fechaComprobante) {
        this.fechaComprobante = fechaComprobante;
    }
    
    @Override
    public String getFechaComprobante() {
        return fechaComprobante;
    }
    
    
    @Override
    public int countAll(String data_string) {
        String sql_busqueda = "select id from gral_bus_catalogos('"+data_string+"') as foo (id integer)";
        String sql_to_query = "select count(id)::int as total from ("+sql_busqueda+") as subt";
        
        int rowCount = this.getJdbcTemplate().queryForInt(sql_to_query);
        return rowCount;
    }
    
    
    @Override
    public String q_serie_folio(final Integer usr_id) {
        String SQL = "select fac_cfds_conf_folios.serie as serie, " +
            "fac_cfds_conf_folios.folio_actual::character varying as folio " +
            "FROM gral_suc AS SUC " +
            "LEFT JOIN fac_cfds_conf ON fac_cfds_conf.gral_suc_id = SUC.id " +
            "LEFT JOIN fac_cfds_conf_folios ON fac_cfds_conf_folios.fac_cfds_conf_id = fac_cfds_conf.id " +
            "LEFT JOIN gral_usr_suc AS USR_SUC ON USR_SUC.gral_suc_id = SUC.id " +
            "WHERE fac_cfds_conf_folios.proposito = \'FAC\' " +
            "AND USR_SUC.gral_usr_id=" + usr_id;
        Logger.getLogger(FacturasSpringDao.class.getName()).log(Level.INFO, SQL);
        Map<String, Object> map_iva = this.getJdbcTemplate().queryForMap(SQL);
        return (map_iva.get("serie").toString() + map_iva.get("folio").toString());
    }
    
    
    @Override
    public HashMap<String, String> selectFunctionValidateAaplicativo(String data, Integer idApp, String string_array) {
        String sql_to_query = "select erp_fn_validaciones_por_aplicativo from erp_fn_validaciones_por_aplicativo('"+data+"',"+idApp+",array["+string_array+"]);";
        System.out.println("Validacion:"+sql_to_query);
        //log.log(Level.INFO, "Ejecutando query de {0}", sql_to_query);
        
        HashMap<String, String> hm = (HashMap<String, String>) this.jdbcTemplate.queryForObject(
            sql_to_query, 
            new Object[]{}, new RowMapper() {
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    HashMap<String, String> row = new HashMap<String, String>();
                    row.put("success",rs.getString("erp_fn_validaciones_por_aplicativo"));
                    return row;
                }
            }
        );
        return hm;
    }
    
    
    
    //Ejecuta procesos relacionados a facturacion
    @Override
    public String selectFunctionForFacAdmProcesos(String campos_data, String extra_data_array) {
        String sql_to_query = "select * from fac_adm_procesos('"+campos_data+"',array["+extra_data_array+"]);";
        
        //System.out.println("selectFunctionForFacAdmProcesos: "+sql_to_query);
        
        String valor_retorno="";
        Map<String, Object> update = this.getJdbcTemplate().queryForMap(sql_to_query);
        valor_retorno = update.get("fac_adm_procesos").toString();
        return valor_retorno;
    }
    
    
    
    
    @Override
    public HashMap<String, String> getFac_Parametros(Integer id_emp, Integer id_suc) {
        HashMap<String, String> mapDatos = new HashMap<String, String>();
        String sql_query = "SELECT * FROM fac_par WHERE gral_emp_id="+id_emp+" AND gral_suc_id="+id_suc+" limit 1;";
        //System.out.println("sql_query: "+sql_query);
        
        Map<String, Object> map = this.getJdbcTemplate().queryForMap(sql_query);
        
        mapDatos.put("gral_suc_id", String.valueOf(map.get("gral_suc_id")));
        mapDatos.put("gral_suc_id_consecutivo", String.valueOf(map.get("gral_suc_id_consecutivo")));
        mapDatos.put("cxc_mov_tipo_id", String.valueOf(map.get("cxc_mov_tipo_id")));
        mapDatos.put("inv_alm_id", String.valueOf(map.get("inv_alm_id")));
        mapDatos.put("gral_emp_id", String.valueOf(map.get("gral_emp_id")));
        mapDatos.put("formato_pedido", String.valueOf(map.get("formato_pedido")));
        mapDatos.put("formato_factura", String.valueOf(map.get("formato_factura")));
        mapDatos.put("permitir_pedido", String.valueOf(map.get("permitir_pedido")));
        mapDatos.put("permitir_remision", String.valueOf(map.get("permitir_remision")));
        mapDatos.put("permitir_cambio_almacen", String.valueOf(map.get("permitir_cambio_almacen")));
        mapDatos.put("permitir_servicios", String.valueOf(map.get("permitir_servicios")));
        mapDatos.put("permitir_articulos", String.valueOf(map.get("permitir_articulos")));
        mapDatos.put("permitir_kits", String.valueOf(map.get("permitir_kits")));
        mapDatos.put("incluye_adenda", String.valueOf(map.get("incluye_adenda")).toLowerCase());
        return mapDatos;
    }
    
    
    @Override
    public ArrayList<HashMap<String, Object>> getFacturas_PaginaGrid(String data_string, int offset, int pageSize, String orderBy, String asc) {
        
        String sql_busqueda = "select id from gral_bus_catalogos(?) as foo (id integer)";
        
	String sql_to_query = "SELECT DISTINCT "
                                    +"fac_docs.id, "
                                    +"fac_docs.serie_folio, "
                                    +"cxc_clie.razon_social as cliente, "
                                    +"fac_docs.total, "
                                    +"gral_mon.descripcion_abr AS moneda, "
                                    +"to_char(fac_docs.momento_creacion,'dd/mm/yyyy') AS fecha_facturacion, "
                                    +"to_char(fac_docs.fecha_vencimiento,'dd/mm/yyyy') AS fecha_venc, "
                                    +"(CASE WHEN fac_docs.folio_pedido IS NULL THEN '' ELSE fac_docs.folio_pedido END ) AS folio_pedido, "
                                    +"(CASE WHEN fac_docs.orden_compra IS NULL THEN '' ELSE fac_docs.orden_compra END) AS oc, "
                                    +"(CASE WHEN fac_docs.cancelado=FALSE THEN (CASE WHEN erp_h_facturas.pagado=TRUE THEN 'PAGADO' ELSE '' END) ELSE 'CANCELADO' END) AS estado, "
                                    +"(CASE WHEN fac_docs.cancelado=FALSE THEN (CASE WHEN erp_h_facturas.pagado=TRUE THEN to_char(fecha_ultimo_pago::timestamp with time zone,'dd/mm/yyyy') ELSE '' END) ELSE '' END) AS fecha_pago "
                            +"FROM fac_docs  "
                            +"JOIN erp_proceso on erp_proceso.id=fac_docs.proceso_id  "
                            +"LEFT JOIN cxc_clie on cxc_clie.id=fac_docs.cxc_clie_id  "
                            +"LEFT JOIN gral_mon ON gral_mon.id=fac_docs.moneda_id  "
                            +"LEFT JOIN erp_h_facturas ON erp_h_facturas.serie_folio=fac_docs.serie_folio "
        +"JOIN ("+sql_busqueda+") as subt on subt.id=fac_docs.id "
        +"order by "+orderBy+" "+asc+" limit ? OFFSET ?";
        
        //System.out.println("Busqueda GetPage: "+sql_to_query);
        //System.out.println("cliente: "+cliente+ "fecha_inicial:"+fecha_inicial+" fecha_final: "+fecha_final+ " offset:"+offset+ " pageSize: "+pageSize+" orderBy:"+orderBy+" asc:"+asc);
        //log.log(Level.INFO, "Ejecutando query de {0}", sql_to_query);
        ArrayList<HashMap<String, Object>> hm = (ArrayList<HashMap<String, Object>>) this.jdbcTemplate.query(
            sql_to_query, 
            new Object[]{data_string,new Integer(pageSize),new Integer(offset)}, new RowMapper() {
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    HashMap<String, Object> row = new HashMap<String, Object>();
                    row.put("id",rs.getInt("id"));
                    row.put("serie_folio",rs.getString("serie_folio"));
                    row.put("cliente",rs.getString("cliente"));
                    row.put("total",StringHelper.AgregaComas(StringHelper.roundDouble(rs.getString("total"),2)));
                    row.put("moneda",rs.getString("moneda"));
                    row.put("fecha_facturacion",rs.getString("fecha_facturacion"));
                    row.put("fecha_venc",rs.getString("fecha_venc"));
                    row.put("folio_pedido",rs.getString("folio_pedido"));
                    row.put("oc",rs.getString("oc"));
                    row.put("estado",rs.getString("estado"));
                    row.put("fecha_pago",rs.getString("fecha_pago"));
                    
                    if(rs.getString("estado").toUpperCase().equals("CANCELADO")){
                        row.put("accion","<td><INPUT TYPE=\"button\" classs=\"cancel\" id=\"cancel_"+rs.getInt("id")+"\" value=\"Cancelar\" disabled=\"true\" style=\"width:65px; height:15px; font-weight:bold;\"></td>");
                    }else{
                        row.put("accion","<td><INPUT TYPE=\"button\" classs=\"cancel\" id=\"cancel_"+rs.getInt("id")+"\" value=\"Cancelar\" style=\"width:65px; height:15px; font-weight:bold;\"></td>");
                    }
                    return row;
                }
            }
        );
        return hm;
    }
    
    
    //obtiene  los datos de la Factura
    @Override
    public ArrayList<HashMap<String, Object>> getFactura_Datos(Integer id_factura) {
        
        String sql_query = ""
        + "SELECT fac_docs.id,"
            +"fac_docs.folio_pedido,"
            +"fac_docs.serie_folio,"
            +"to_char(fac_docs.momento_creacion,'yyyy-mm-dd') AS fecha,"
            +"(case when fac_docs.momento_cancelacion is null then '' else to_char(fac_docs.momento_cancelacion,'dd/mm/yyyy') end) as fecha_can,"
            +"fac_docs.moneda_id,"
            + "(case when gral_mon.id is null then '' else gral_mon.iso_4217 end) as moneda_iso_4217, "
            +"fac_docs.observaciones,"
            +"cxc_clie.id AS cliente_id,"
            +"cxc_clie.rfc,"
            +"cxc_clie.razon_social,"
            + "(CASE WHEN cxc_clie.email IS NULL THEN '' ELSE cxc_clie.email END) AS email, "
            + "fac_docs.cxc_clie_df_id,"
            + "(CASE WHEN fac_docs.cxc_clie_df_id > 1 THEN "
                + "sbtdf.calle||' '||sbtdf.numero_interior||' '||sbtdf.numero_exterior||', '||sbtdf.colonia||', '||sbtdf.municipio||', '||sbtdf.estado||', '||sbtdf.pais||' C.P. '||sbtdf.cp "
            + "ELSE "
                + "cxc_clie.calle||' '||cxc_clie.numero||', '||cxc_clie.colonia||', '||gral_mun.titulo||', '||gral_edo.titulo||', '||gral_pais.titulo||' C.P. '||cxc_clie.cp "
            + "END ) AS direccion,"
            + "cxc_clie.cxc_clie_tipo_adenda_id as t_adenda_id,"
            + "fac_docs.subtotal,"
            + "fac_docs.monto_ieps,"
            + "fac_docs.impuesto,"
            + "fac_docs.total,"
            + "fac_docs.monto_retencion,"
            + "fac_docs.tipo_cambio,"
            + "(CASE WHEN fac_docs.cancelado=FALSE THEN '' ELSE 'CANCELADO' END) AS estado,"
            + "fac_docs.cxc_agen_id,"
            + "fac_docs.terminos_id,"
            + "fac_docs.orden_compra,"
            + "fac_docs.fac_metodos_pago_id,"
            + "fac_docs.no_cuenta, "
            + "(cxc_clie.tasa_ret_immex::double precision/100) AS tasa_ret_immex,"
            + "erp_h_facturas.saldo_factura, "
            + "fac_docs.monto_descto, "
            + "(CASE WHEN fac_docs.subtotal_sin_descto IS NULL THEN 0 ELSE fac_docs.subtotal_sin_descto END) AS subtotal_sin_descto, "
            + "(CASE WHEN fac_docs.monto_descto>0 THEN fac_docs.motivo_descto ELSE '' END) AS motivo_descto,"
            + "fac_docs.cancelado,"
            + "fac_docs.fac_docs_tipo_cancelacion_id as tipo_cancel,"
            + "fac_docs.ctb_tmov_id_cancelacion as tmovid_cancel,"
            + "fac_docs.motivo_cancelacion as motivo_cancel,"
            + "fac_docs.contra_recibo_id "
        +"FROM fac_docs "
        +"JOIN erp_h_facturas ON erp_h_facturas.serie_folio=fac_docs.serie_folio "
        +"LEFT JOIN cxc_clie ON cxc_clie.id=fac_docs.cxc_clie_id "
        +"LEFT JOIN gral_mon ON gral_mon.id = fac_docs.moneda_id "
        +"LEFT JOIN gral_pais ON gral_pais.id = cxc_clie.pais_id "
        +"LEFT JOIN gral_edo ON gral_edo.id = cxc_clie.estado_id "
        +"LEFT JOIN gral_mun ON gral_mun.id = cxc_clie.municipio_id "
        +"LEFT JOIN (SELECT cxc_clie_df.id, (CASE WHEN cxc_clie_df.calle IS NULL THEN '' ELSE cxc_clie_df.calle END) AS calle, (CASE WHEN cxc_clie_df.numero_interior IS NULL THEN '' ELSE (CASE WHEN cxc_clie_df.numero_interior IS NULL OR cxc_clie_df.numero_interior='' THEN '' ELSE 'NO.INT.'||cxc_clie_df.numero_interior END)  END) AS numero_interior, (CASE WHEN cxc_clie_df.numero_exterior IS NULL THEN '' ELSE (CASE WHEN cxc_clie_df.numero_exterior IS NULL OR cxc_clie_df.numero_exterior='' THEN '' ELSE 'NO.EXT.'||cxc_clie_df.numero_exterior END )  END) AS numero_exterior, (CASE WHEN cxc_clie_df.colonia IS NULL THEN '' ELSE cxc_clie_df.colonia END) AS colonia,(CASE WHEN gral_mun.id IS NULL OR gral_mun.id=0 THEN '' ELSE gral_mun.titulo END) AS municipio,(CASE WHEN gral_edo.id IS NULL OR gral_edo.id=0 THEN '' ELSE gral_edo.titulo END) AS estado,(CASE WHEN gral_pais.id IS NULL OR gral_pais.id=0 THEN '' ELSE gral_pais.titulo END) AS pais,(CASE WHEN cxc_clie_df.cp IS NULL THEN '' ELSE cxc_clie_df.cp END) AS cp  FROM cxc_clie_df LEFT JOIN gral_pais ON gral_pais.id = cxc_clie_df.gral_pais_id LEFT JOIN gral_edo ON gral_edo.id = cxc_clie_df.gral_edo_id LEFT JOIN gral_mun ON gral_mun.id = cxc_clie_df.gral_mun_id ) AS sbtdf ON sbtdf.id = fac_docs.cxc_clie_df_id "
        +"WHERE fac_docs.id=? ";
        
        ArrayList<HashMap<String, Object>> hm = (ArrayList<HashMap<String, Object>>) this.jdbcTemplate.query(
            sql_query,  
            new Object[]{new Integer(id_factura)}, new RowMapper() {
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    HashMap<String, Object> row = new HashMap<>();
                    row.put("id",rs.getInt("id"));
                    row.put("serie_folio",rs.getString("serie_folio"));
                    row.put("fecha",rs.getString("fecha"));
                    row.put("folio_pedido",rs.getString("folio_pedido"));
                    row.put("moneda_id",rs.getInt("moneda_id"));
                    row.put("moneda_4217",rs.getString("moneda_iso_4217"));
                    row.put("observaciones",rs.getString("observaciones"));
                    row.put("cliente_id",rs.getInt("cliente_id"));
                    row.put("rfc",rs.getString("rfc"));
                    row.put("razon_social",rs.getString("razon_social"));
                    row.put("email",rs.getString("email"));
                    row.put("df_id",rs.getInt("cxc_clie_df_id"));
                    row.put("direccion",rs.getString("direccion"));
                    row.put("t_adenda_id",rs.getInt("t_adenda_id"));
                    row.put("subtotal",StringHelper.roundDouble(rs.getDouble("subtotal"),2));
                    row.put("impuesto",StringHelper.roundDouble(rs.getDouble("impuesto"),2));
                    row.put("monto_retencion",StringHelper.roundDouble(rs.getDouble("monto_retencion"),2));
                    row.put("total",StringHelper.roundDouble(rs.getDouble("total"),2));
                    row.put("tipo_cambio",StringHelper.roundDouble(rs.getDouble("tipo_cambio"),4));
                    row.put("estado",rs.getString("estado"));
                    row.put("cxc_agen_id",rs.getInt("cxc_agen_id"));
                    row.put("terminos_id",rs.getInt("terminos_id"));
                    row.put("orden_compra",rs.getString("orden_compra"));
                    row.put("fac_metodos_pago_id",String.valueOf(rs.getInt("fac_metodos_pago_id")));
                    row.put("no_cuenta",rs.getString("no_cuenta"));
                    row.put("tasa_ret_immex",StringHelper.roundDouble(rs.getDouble("tasa_ret_immex"),2));
                    row.put("saldo_fac",StringHelper.roundDouble(rs.getDouble("saldo_factura"),2));
                    row.put("monto_ieps",StringHelper.roundDouble(rs.getDouble("monto_ieps"),2));
                    row.put("monto_descto",StringHelper.roundDouble(rs.getDouble("monto_descto"),2));
                    row.put("subtotal_sin_descto",StringHelper.roundDouble(rs.getDouble("subtotal_sin_descto"),2));
                    row.put("motivo_descto",rs.getString("motivo_descto"));
                    row.put("cancelado",rs.getBoolean("cancelado"));
                    row.put("fecha_can",rs.getString("fecha_can"));
                    row.put("tipo_cancel",rs.getInt("tipo_cancel"));
                    row.put("tmovid_cancel",rs.getInt("tmovid_cancel"));
                    row.put("motivo_cancel",rs.getString("motivo_cancel"));
                    row.put("contra_recibo_id",rs.getInt("contra_recibo_id"));
                    return row;
                }
            }
        );
        return hm;
    }
    
    
    //obtiene el listado de conceptos de la factura
    @Override
    public ArrayList<HashMap<String, Object>> getFactura_DatosGrid(Integer id_factura) {
        String sql_query = "SELECT fac_docs_detalles.inv_prod_id, "
                +"inv_prod.sku  AS codigo_producto, "
                +"inv_prod.descripcion AS titulo, "
                +"(CASE WHEN inv_prod_unidades.titulo IS NULL THEN '' ELSE inv_prod_unidades.titulo END) AS unidad, "
                +"(CASE WHEN inv_prod_unidades.decimales IS NULL THEN 0 ELSE inv_prod_unidades.decimales END) AS decimales, "
                +"(CASE WHEN inv_prod_presentaciones.id IS NULL THEN 0 ELSE inv_prod_presentaciones.id END) AS id_presentacion, "
                +"(CASE WHEN inv_prod_presentaciones.titulo IS NULL THEN '' ELSE inv_prod_presentaciones.titulo END) AS presentacion, "
                +"fac_docs_detalles.cantidad, "
                +"fac_docs_detalles.precio_unitario, "
                +"(fac_docs_detalles.cantidad * fac_docs_detalles.precio_unitario) AS importe,"
                + "fac_docs_detalles.gral_imptos_id, "
                + "fac_docs_detalles.valor_imp, "
                + "fac_docs_detalles.cantidad_devolucion, "
                + "fac_docs_detalles.gral_ieps_id AS id_ieps,"
                + "(fac_docs_detalles.valor_ieps * 100::double precision) AS tasa_ieps, "
                + "((fac_docs_detalles.cantidad * fac_docs_detalles.precio_unitario) * fac_docs_detalles.valor_ieps) AS importe_ieps "
        +"FROM fac_docs_detalles "
        +"LEFT JOIN inv_prod on inv_prod.id = fac_docs_detalles.inv_prod_id  "
        +"LEFT JOIN inv_prod_unidades on inv_prod_unidades.id = fac_docs_detalles.inv_prod_unidad_id  "
        +"LEFT JOIN inv_prod_presentaciones on inv_prod_presentaciones.id = fac_docs_detalles.inv_prod_presentacion_id  "
        +"WHERE fac_docs_detalles.fac_doc_id = ? ORDER BY fac_docs_detalles.id;";
        
        //System.out.println("Obtiene datos grid FACTURA: "+sql_query);
        //System.out.println("id_factura: "+id_factura);
        ArrayList<HashMap<String, Object>> hm_grid = (ArrayList<HashMap<String, Object>>) this.jdbcTemplate.query(
            sql_query,  
            new Object[]{new Integer(id_factura)}, new RowMapper() {
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    HashMap<String, Object> row = new HashMap<String, Object>();
                    row.put("inv_prod_id",rs.getString("inv_prod_id"));
                    row.put("codigo_producto",rs.getString("codigo_producto"));
                    row.put("titulo",rs.getString("titulo"));
                    row.put("unidad",rs.getString("unidad"));
                    row.put("id_presentacion",rs.getString("id_presentacion"));
                    row.put("presentacion",rs.getString("presentacion"));
                    row.put("cantidad",StringHelper.roundDouble( rs.getString("cantidad"), rs.getInt("decimales") ));
                    row.put("precio_unitario",StringHelper.roundDouble(rs.getDouble("precio_unitario"),4) );
                    row.put("importe",StringHelper.roundDouble(rs.getDouble("importe"),2) );
                    row.put("id_impto",rs.getString("gral_imptos_id"));
                    row.put("tasa_iva",StringHelper.roundDouble(rs.getDouble("valor_imp"),2) );
                    row.put("cant_dev",StringHelper.roundDouble(rs.getDouble("cantidad_devolucion"),2) );
                    row.put("id_ieps",String.valueOf(rs.getInt("id_ieps")));
                    row.put("tasa_ieps",StringHelper.roundDouble(rs.getDouble("tasa_ieps"),2) );
                    row.put("importe_ieps",StringHelper.roundDouble(rs.getDouble("importe_ieps"),4) );
                    return row;
                }
            }
        );
        return hm_grid;
    }
    
    
    
    //Obtener datos para la Adenda
    @Override
    public ArrayList<HashMap<String, Object>> getFactura_DatosAdenda(Integer id_factura) {
        String sql_query = "SELECT * FROM fac_docs_adenda WHERE fac_docs_id="+id_factura+";";
        
        //System.out.println("Obtiene datos grid prefactura: "+sql_query);
        ArrayList<HashMap<String, Object>> hm_grid = (ArrayList<HashMap<String, Object>>) this.jdbcTemplate.query(
            sql_query, 
            new Object[]{}, new RowMapper() {
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    HashMap<String, Object> row = new HashMap<String, Object>();
                    row.put("id_adenda",rs.getInt("id"));
                    row.put("generado",rs.getString("generado"));
                    row.put("valor1",rs.getString("valor1"));
                    row.put("valor2",rs.getString("valor2"));
                    row.put("valor3",rs.getString("valor3").toLowerCase());
                    row.put("valor4",rs.getString("valor4"));
                    row.put("valor5",rs.getString("valor5"));
                    row.put("valor6",rs.getString("valor6"));
                    row.put("valor7",rs.getString("valor7"));
                    row.put("valor8",rs.getString("valor8"));
                    return row;
                }
            }
        );
        return hm_grid;
    }
    
    
    
    //obtiene todas la monedas
    @Override
    public ArrayList<HashMap<String, Object>> getFactura_Monedas() {
        String sql_to_query = "SELECT id, descripcion FROM  gral_mon WHERE borrado_logico=FALSE AND ventas=TRUE ORDER BY id ASC;";
        //log.log(Level.INFO, "Ejecutando query de {0}", sql_to_query);
        ArrayList<HashMap<String, Object>> hm_monedas = (ArrayList<HashMap<String, Object>>) this.jdbcTemplate.query(
            sql_to_query,
            new Object[]{}, new RowMapper(){
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    HashMap<String, Object> row = new HashMap<String, Object>();
                    row.put("id",rs.getInt("id"));
                    row.put("descripcion",rs.getString("descripcion"));
                    return row;
                }
            }
        );
        return hm_monedas;
    }
    
    
    
    @Override
    public ArrayList<HashMap<String, Object>> getFactura_Agentes(Integer id_empresa, Integer id_sucursal) {
        //String sql_to_query = "SELECT id,nombre_pila||' '||apellido_paterno||' '||apellido_materno AS nombre_vendedor FROM erp_empleados WHERE borrado_logico=FALSE AND vendedor=TRUE AND empresa_id="+id_empresa+" AND sucursal_id="+id_sucursal;
        
        String sql_to_query = "SELECT cxc_agen.id,  "
                                        +"cxc_agen.nombre AS nombre_vendedor "
                                +"FROM cxc_agen "
                                +"JOIN gral_usr_suc ON gral_usr_suc.gral_usr_id=cxc_agen.gral_usr_id "
                                +"JOIN gral_suc ON gral_suc.id=gral_usr_suc.gral_suc_id "
                                +"WHERE gral_suc.empresa_id="+id_empresa+" ORDER BY cxc_agen.id;";
        
        //log.log(Level.INFO, "Ejecutando query de {0}", sql_to_query);
        ArrayList<HashMap<String, Object>> hm_vendedor = (ArrayList<HashMap<String, Object>>) this.jdbcTemplate.query(
            sql_to_query,
            new Object[]{}, new RowMapper(){
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    HashMap<String, Object> row = new HashMap<String, Object>();
                    row.put("id",rs.getString("id")  );
                    row.put("nombre_vendedor",rs.getString("nombre_vendedor"));
                    return row;
                }
            }
        );
        return hm_vendedor;
    }
    
    
    
    @Override
    public ArrayList<HashMap<String, Object>> getFactura_DiasDeCredito() {
        String sql_to_query = "SELECT id,descripcion FROM cxc_clie_credias WHERE borrado_logico=FALSE;";
        //log.log(Level.INFO, "Ejecutando query de {0}", sql_to_query);
        ArrayList<HashMap<String, Object>> hm = (ArrayList<HashMap<String, Object>>) this.jdbcTemplate.query(
            sql_to_query,
            new Object[]{}, new RowMapper(){
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    HashMap<String, Object> row = new HashMap<String, Object>();
                    row.put("id",rs.getString("id")  );
                    row.put("descripcion",rs.getString("descripcion"));
                    return row;
                }
            }
        );
        return hm;
    }
    
    
    @Override
    public ArrayList<HashMap<String, Object>> getMetodosPago(Integer empresaId) {
        String sql_to_query = "SELECT id, (case when clave_sat<>'' then clave_sat||' ' else '' end)||titulo as titulo FROM fac_metodos_pago WHERE borrado_logico=false and gral_emp_id=?;";
        //log.log(Level.INFO, "Ejecutando query de {0}", sql_to_query);
        ArrayList<HashMap<String, Object>> hm = (ArrayList<HashMap<String, Object>>) this.jdbcTemplate.query(
            sql_to_query,
            new Object[]{new Integer(empresaId)}, new RowMapper(){
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    HashMap<String, Object> row = new HashMap<String, Object>();
                    row.put("id",String.valueOf(rs.getInt("id"))  );
                    row.put("titulo",rs.getString("titulo"));
                    return row;
                }
            }
        );
        return hm;
    }
    
    
    @Override
    public ArrayList<HashMap<String, Integer>>  getFactura_AnioInforme() {
        ArrayList<HashMap<String, Integer>> anios = new ArrayList<HashMap<String, Integer>>();
        
        Calendar c1 = Calendar.getInstance();
        Integer annio = c1.get(Calendar.YEAR);//obtiene el año actual
        
        for(int i=0; i<15; i++) {
            HashMap<String, Integer> row = new HashMap<String, Integer>();
            row.put("valor",(annio-i));
            anios.add(i, row);
        }
        return anios;
    }
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    //obtiene el tipo de cambio actual
    //se utiliza en prefacturas y facturas
    @Override
    public Double getTipoCambioActual() {
        //System.out.println("FECHA ACTUAL: "+TimeHelper.getFechaActualYMD2());
        String sql_to_query = "SELECT valor AS tipo_cambio FROM erp_monedavers WHERE momento_creacion<=now() AND moneda_id=2 ORDER BY momento_creacion DESC LIMIT 1;";
        Map<String, Object> tipo_cambio = this.getJdbcTemplate().queryForMap(sql_to_query);
        Double valor_tipo_cambio = Double.parseDouble(StringHelper.roundDouble(tipo_cambio.get("tipo_cambio").toString(),4));
        
        return valor_tipo_cambio;
    }
    
    
    
    //obtiene valor del impuesto. retorna 0.16 o 0.11
    @Override
    public ArrayList<HashMap<String, Object>> getValoriva(Integer id_sucursal) {
        String sql_to_query = ""
                + "SELECT "
                    + "gral_imptos.id AS id_impuesto, "
                    + "gral_imptos.iva_1 AS valor_impuesto "
                + "FROM gral_suc "
                + "JOIN gral_imptos ON gral_imptos.id=gral_suc.gral_impto_id "
                + "WHERE gral_imptos.borrado_logico=FALSE AND gral_suc.id=?";
        
        //log.log(Level.INFO, "Ejecutando query de {0}", sql_to_query);
        ArrayList<HashMap<String, Object>> hm_valoriva = (ArrayList<HashMap<String, Object>>) this.jdbcTemplate.query(
            sql_to_query, new Object[]{new Integer(id_sucursal)}, new RowMapper(){
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    HashMap<String, Object> row = new HashMap<String, Object>();
                    row.put("id_impuesto",rs.getString("id_impuesto"));
                    row.put("valor_impuesto",StringHelper.roundDouble(rs.getString("valor_impuesto"),2));
                    return row;
                }
            }
        );
        return hm_valoriva;
    }
    
    //obtiene tasa actual del iva de la sucursal. Retorna la tasa 16% u 11%
    private String getTasaIva(Integer id_sucursal) {
        //System.out.println("FECHA ACTUAL: "+TimeHelper.getFechaActualYMD2());
        String sql_to_query = "SELECT "
                + "gral_imptos.iva_1*100 as valor  "
                + "FROM gral_suc "
                + "JOIN gral_imptos ON gral_imptos.id=gral_suc.gral_impto_id "
                + "WHERE gral_imptos.borrado_logico=FALSE AND gral_suc.id="+id_sucursal;
        
        Map<String, Object> map_iva = this.getJdbcTemplate().queryForMap(sql_to_query);
        String valor_iva = StringHelper.roundDouble(map_iva.get("valor").toString(),2);
        return valor_iva;
    }
    
    
    //extrae los datos para crear el informe mensual
    @Override
    public ArrayList<HashMap<String, Object>> getComprobantesActividadPorMes(String year,String month,Integer id_empresa){       
        String sql_to_query = "SELECT DISTINCT * FROM fac_cfds WHERE 1=1 AND (momento_expedicion ILIKE '" + year + "-" + month + "%' OR momento_cancelacion::character varying ILIKE '" + year + "-" + month + "%')  AND empresa_id="+ id_empresa+ " ORDER BY id;";
        
        //System.out.println("SQL comprobantes por mes: "+sql_to_query);
        
        ArrayList<HashMap<String, Object>> mn = (ArrayList<HashMap<String, Object>>) this.jdbcTemplate.query(
            sql_to_query,
            new Object[]{}, new RowMapper(){
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    HashMap<String, Object> row = new HashMap<String, Object>();
                    
                    row.put("pedimento",rs.getString("pedimento"));
                    row.put("numero_de_aprobacion",rs.getString("numero_de_aprobacion"));
                    row.put("estado_del_comprobante",rs.getString("estado_del_comprobante"));
                    row.put("fecha_de_pedimento",rs.getString("fecha_de_pedimento"));
                    row.put("aduana",rs.getString("aduana"));
                    row.put("anoaprovacion",rs.getString("anoaprovacion"));
                    row.put("monto_de_la_operacion",rs.getString("monto_de_la_operacion"));
                    row.put("monto_del_impuesto",rs.getString("monto_del_impuesto"));
                    row.put("momento_expedicion",rs.getString("momento_expedicion"));
                    row.put("folio_del_comprobante_fiscal",rs.getString("folio_del_comprobante_fiscal"));
                    row.put("rfc_cliente",rs.getString("rfc_cliente"));
                    row.put("serie",rs.getString("serie"));
                    row.put("efecto_de_comprobante",rs.getString("tipo_comprobante"));//se toma el valor de tipo_comprobante
                    
                    return row;
                }
            }
        );
        return mn;
    }
    
    
    
    
    
    @Override
    public ArrayList<LinkedHashMap<String, String>> getDataXml_Namespaces(String tipo) {
        //tipo='fac', tipo='fac_nomina'
        String sql_to_query = "SELECT (CASE WHEN key_xmlns IS NULL THEN '' ELSE key_xmlns END) AS key_xmlns, (CASE WHEN xmlns IS NULL THEN '' ELSE xmlns END) AS xmlns, (CASE WHEN schemalocation IS NULL THEN '' ELSE schemalocation END) AS schemalocation FROM fac_namespaces WHERE derogado=false AND "+tipo+"=true;";
        //System.out.println("getNamesPaces: "+sql_to_query);
        ArrayList<LinkedHashMap<String, String>> datos = (ArrayList<LinkedHashMap<String, String>>) this.jdbcTemplate.query(
            sql_to_query,
            new Object[]{}, new RowMapper(){
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    LinkedHashMap<String, String> row = new LinkedHashMap<String, String>();
                    row.put("key_xmlns",rs.getString("key_xmlns"));
                    row.put("xmlns",rs.getString("xmlns"));
                    row.put("schemalocation",rs.getString("schemalocation"));
                    return row;
                }
            }
        );
        return datos;
    }
    
    
    
    
    
    
    //obtiene datos para la factura(CFD, CFDI, CFDTF)
    @Override
    public HashMap<String, String> getDataFacturaXml(Integer id_prefactura) {
        HashMap<String, String> data = new HashMap<String, String>();
        
        //obtener id del cliente
        String sql_to_query = ""
                + "SELECT erp_prefacturas.cliente_id, "
                        + "fac_metodos_pago.clave_sat AS metodo_pago, "
                        //+ "fac_metodos_pago.titulo AS metodo_pago_titulo, "
                        + "fac_metodos_pago.clave_sat AS metodo_pago_titulo, "
                        + "(CASE WHEN fac_metodos_pago.id=7 THEN 'NO APLICA' ELSE erp_prefacturas.no_cuenta END ) AS no_cuenta, "
                        + "cxc_clie_credias.descripcion AS condicion_pago,"
                        + "gral_mon.iso_4217 AS moneda, "
                        + "gral_mon.iso_4217_anterior AS moneda2, "
                        + "gral_mon.simbolo AS simbolo_moneda, "
                        + "erp_prefacturas.tipo_cambio, "
                        + "cxc_clie.numero_control, "
                        + "cxc_clie.razon_social, "
                        + "cxc_clie.rfc, "
                        + "cxc_clie.cxc_clie_tipo_adenda_id AS adenda_id, "
                        + "erp_prefacturas.orden_compra, "
                        + "(CASE WHEN cxc_clie.localidad_alternativa IS NULL THEN '' ELSE cxc_clie.localidad_alternativa END) AS localidad_alternativa, "
                        + "(CASE WHEN erp_prefacturas.cxc_clie_df_id > 1 THEN sbtdf.calle ELSE cxc_clie.calle END ) AS calle,"
                        + "(CASE WHEN erp_prefacturas.cxc_clie_df_id > 1 THEN sbtdf.numero_interior ELSE (CASE WHEN cxc_clie.numero='' OR cxc_clie.numero IS NULL THEN '' ELSE cxc_clie.numero END)  END ) AS numero_interior,"
                        + "(CASE WHEN erp_prefacturas.cxc_clie_df_id > 1 THEN sbtdf.numero_exterior ELSE (CASE WHEN cxc_clie.numero_exterior='' OR cxc_clie.numero_exterior IS NULL THEN '' ELSE cxc_clie.numero_exterior END) END ) AS numero_exterior,"
                        + "(CASE WHEN erp_prefacturas.cxc_clie_df_id > 1 THEN sbtdf.colonia ELSE cxc_clie.colonia END ) AS colonia,"
                        + "(CASE WHEN erp_prefacturas.cxc_clie_df_id > 1 THEN sbtdf.municipio ELSE gral_mun.titulo END ) AS municipio,"
                        + "(CASE WHEN erp_prefacturas.cxc_clie_df_id > 1 THEN sbtdf.estado ELSE gral_edo.titulo END ) AS estado,"
                        + "(CASE WHEN erp_prefacturas.cxc_clie_df_id > 1 THEN sbtdf.pais ELSE gral_pais.titulo END ) AS pais,"
                        + "(CASE WHEN erp_prefacturas.cxc_clie_df_id > 1 THEN sbtdf.cp ELSE cxc_clie.cp END ) AS cp, "
                        + "(CASE WHEN erp_prefacturas.monto_descto>0 THEN true ELSE false END) AS pdescto, "
                        + "(CASE WHEN erp_prefacturas.monto_descto>0 THEN (CASE WHEN erp_prefacturas.motivo_descto IS NULL THEN '' ELSE erp_prefacturas.motivo_descto END) ELSE '' END) AS mdescto "
                + "FROM erp_prefacturas  "
                + "LEFT JOIN cxc_clie ON cxc_clie.id=erp_prefacturas.cliente_id "
                + "LEFT JOIN fac_metodos_pago ON fac_metodos_pago.id=erp_prefacturas.fac_metodos_pago_id "
                + "LEFT JOIN cxc_clie_credias ON cxc_clie_credias.id=erp_prefacturas.terminos_id "
                + "LEFT JOIN gral_mon ON gral_mon.id=erp_prefacturas.moneda_id "
                + "JOIN gral_pais ON gral_pais.id = cxc_clie.pais_id "
                + "JOIN gral_edo ON gral_edo.id = cxc_clie.estado_id "
                + "JOIN gral_mun ON gral_mun.id = cxc_clie.municipio_id "
                + "LEFT JOIN (SELECT cxc_clie_df.id, (CASE WHEN cxc_clie_df.calle IS NULL THEN '' ELSE cxc_clie_df.calle END) AS calle, (CASE WHEN cxc_clie_df.numero_interior IS NULL THEN '' ELSE (CASE WHEN cxc_clie_df.numero_interior IS NULL OR cxc_clie_df.numero_interior='' THEN '' ELSE 'NO.INT.'||cxc_clie_df.numero_interior END)  END) AS numero_interior, (CASE WHEN cxc_clie_df.numero_exterior IS NULL THEN '' ELSE (CASE WHEN cxc_clie_df.numero_exterior IS NULL OR cxc_clie_df.numero_exterior='' THEN '' ELSE 'NO.EXT.'||cxc_clie_df.numero_exterior END )  END) AS numero_exterior, (CASE WHEN cxc_clie_df.colonia IS NULL THEN '' ELSE cxc_clie_df.colonia END) AS colonia,(CASE WHEN gral_mun.id IS NULL OR gral_mun.id=0 THEN '' ELSE gral_mun.titulo END) AS municipio,(CASE WHEN gral_edo.id IS NULL OR gral_edo.id=0 THEN '' ELSE gral_edo.titulo END) AS estado,(CASE WHEN gral_pais.id IS NULL OR gral_pais.id=0 THEN '' ELSE gral_pais.titulo END) AS pais,(CASE WHEN cxc_clie_df.cp IS NULL THEN '' ELSE cxc_clie_df.cp END) AS cp  FROM cxc_clie_df LEFT JOIN gral_pais ON gral_pais.id = cxc_clie_df.gral_pais_id LEFT JOIN gral_edo ON gral_edo.id = cxc_clie_df.gral_edo_id LEFT JOIN gral_mun ON gral_mun.id = cxc_clie_df.gral_mun_id ) AS sbtdf ON sbtdf.id = erp_prefacturas.cxc_clie_df_id "
                + "WHERE erp_prefacturas.id="+id_prefactura;
                
        Map<String, Object> map = this.getJdbcTemplate().queryForMap(sql_to_query);
        
        int id_cliente = Integer.parseInt(map.get("cliente_id").toString());
        
        String fecha = TimeHelper.getFechaActualYMDH();
        String[] fecha_hora = fecha.split(" ");
        //formato fecha: 2011-03-01T00:00:00
        this.setFechaComprobante(fecha_hora[0]+"T"+fecha_hora[1]);//este solo se utiliza en pdfcfd
        
        data.put("comprobante_attr_fecha",fecha_hora[0]+"T"+fecha_hora[1]);
        data.put("comprobante_attr_condicionesdepago",map.get("condicion_pago").toString().toUpperCase());
        data.put("comprobante_attr_formadepago","PAGO EN UNA SOLA EXIBICION");
        if(map.get("pdescto").toString().trim().toLowerCase().equals("true")){
            data.put("comprobante_attr_motivodescuento",map.get("mdescto").toString().toUpperCase());
            data.put("comprobante_attr_descuento",this.getMontoDescuento());
            data.put("subtotal_con_descuento",this.getSubtotalConDescuento());
            //Este es para el PDF cuando incluye descuento
            data.put("subtotal_sin_descuento",this.getSubTotal());
        }else{
            data.put("comprobante_attr_motivodescuento","");
            data.put("comprobante_attr_descuento","0.00");
            data.put("subtotal_con_descuento","0.00");
            data.put("subtotal_sin_descuento","0.00");
        }
        data.put("comprobante_attr_subtotal",this.getSubTotal());
        data.put("comprobante_attr_total",this.getTotal());
        data.put("comprobante_attr_moneda",map.get("moneda").toString().toUpperCase());
        
        //Este campo es utilizado para la adenda de Quimiproductos
        data.put("moneda2",map.get("moneda2").toString().toUpperCase());
        data.put("orden_compra",map.get("orden_compra").toString().toUpperCase());
        
        data.put("comprobante_attr_simbolo_moneda",map.get("simbolo_moneda").toString().toUpperCase());
        data.put("comprobante_attr_tc",StringHelper.roundDouble(map.get("tipo_cambio").toString(), 4));
        data.put("comprobante_attr_metododepago",map.get("metodo_pago").toString().toUpperCase());
        data.put("comprobante_attr_metodopagotitulo",map.get("metodo_pago_titulo").toString().toUpperCase());
        
        String no_cta ="";
        if (!map.get("no_cuenta").toString().equals("null") && !map.get("no_cuenta").toString().equals("")){
            no_cta=map.get("no_cuenta").toString();
        }
        data.put("comprobante_attr_numerocuenta",no_cta);
        
        String numero_ext="";
        if(map.get("numero_exterior").toString().equals("'")){
            numero_ext = "";
        }else{
            numero_ext = map.get("numero_exterior").toString();
        }
        
        String numero_int="";
        if(map.get("numero_interior").toString().equals("'")){
            numero_int = "";
        }else{
            numero_int = map.get("numero_interior").toString();
        }
        
        //datos del cliente
        data.put("comprobante_receptor_attr_nombre",map.get("razon_social").toString());
        data.put("comprobante_receptor_attr_rfc",map.get("rfc").toString());
        data.put("comprobante_receptor_domicilio_attr_calle",map.get("calle").toString());
        data.put("comprobante_receptor_domicilio_attr_noexterior",numero_ext);
        data.put("comprobante_receptor_domicilio_attr_nointerior",numero_int);
        data.put("comprobante_receptor_domicilio_attr_colonia",map.get("colonia").toString());
        data.put("comprobante_receptor_domicilio_attr_localidad",map.get("localidad_alternativa").toString());
        data.put("comprobante_receptor_domicilio_attr_referencia","");
        data.put("comprobante_receptor_domicilio_attr_municipio",map.get("municipio").toString());
        data.put("comprobante_receptor_domicilio_attr_estado",map.get("estado").toString());
        data.put("comprobante_receptor_domicilio_attr_pais",map.get("pais").toString());
        data.put("comprobante_receptor_domicilio_attr_codigopostal",map.get("cp").toString());
        data.put("adenda_id",map.get("adenda_id").toString());
        
        //Este solo se utiliza en el pdfcfd y cfdi
        data.put("numero_control",map.get("numero_control").toString());
        
        //Campo que indica si se permite descuento
        data.put("pdescto",map.get("pdescto").toString());
        
        return data;
    }
    
    
    
    
    //este se utiliza en: 
    //xml de Factura CFD y Nota de Credito CFD
    public void calcula_Totales_e_Impuestos(ArrayList<LinkedHashMap<String, String>> conceptos) throws SQLException{
        Double sumaImporte = 0.0;
        Double sumaImpuesto = 0.0;
        Double sumaImporteIeps = 0.0;
        Double tasa_retencion=0.0;
        Double monto_retencion=0.0;
        Double montoTotal=0.0;
        Double sumaDescuento=0.0;
        Double sumaSubtotalConDescuento=0.0;
        Double sumaRetencionesDePartidas=0.0;
        
        for (int x=0; x<=conceptos.size()-1;x++){
            LinkedHashMap<String,String> con = conceptos.get(x);
            sumaDescuento = sumaDescuento + Double.parseDouble(StringHelper.roundDouble(con.get("importe_del_descto"),4));
            sumaSubtotalConDescuento = sumaSubtotalConDescuento + Double.parseDouble(StringHelper.roundDouble(con.get("importe_con_descto"),4));
            
            sumaImporte = sumaImporte + Double.parseDouble(StringHelper.roundDouble(con.get("importe"),4));
            sumaImporteIeps = sumaImporteIeps + Double.parseDouble(StringHelper.roundDouble(con.get("importe_ieps"),4));
            sumaImpuesto = sumaImpuesto + Double.parseDouble(StringHelper.roundDouble(con.get("importe_impuesto"),4));
            tasa_retencion = Double.parseDouble(StringHelper.roundDouble(con.get("tasa_retencion"),2));
            
            sumaRetencionesDePartidas = sumaRetencionesDePartidas + Double.parseDouble(StringHelper.roundDouble(con.get("importe_ret"),4));
        }
        
        //System.out.println("Canculando Totales de la Factura");
        if(sumaDescuento>0){
            monto_retencion = sumaSubtotalConDescuento * tasa_retencion;
            montoTotal = sumaSubtotalConDescuento + sumaImporteIeps + sumaImpuesto - monto_retencion;
        }else{
            monto_retencion = sumaImporte * tasa_retencion;
            montoTotal = sumaImporte + sumaImporteIeps + sumaImpuesto - monto_retencion;
        }
        
        //Sumar el acumulado de retencion de las partidas
        monto_retencion = monto_retencion + sumaRetencionesDePartidas;
        
        this.setSubtotalConDescuento(StringHelper.roundDouble(sumaSubtotalConDescuento,2));
        this.setMontoDescuento(StringHelper.roundDouble(sumaDescuento,2));
        this.setSubTotal(StringHelper.roundDouble(sumaImporte,2));
        this.setImpuestoTrasladado(StringHelper.roundDouble(sumaImpuesto,2));
        this.setImpuestoRetenido(StringHelper.roundDouble(monto_retencion,2));
        this.setTasaRetencion(StringHelper.roundDouble(tasa_retencion,2));
        this.setTotal(StringHelper.roundDouble(montoTotal,2));
        
        //System.out.println("tasa_retencion: "+ tasa_retencion);
        //System.out.println("Subtotal: "+ this.getSubTotal()+"      Trasladado: "+ sumaImpuesto+ "      Retenido: "+ monto_retencion+ "      Total: "+ this.getTotal());
    }
    
    
    
    
    
    //Obtiene la lista de conceptos para la factura para CFD
    @Override
    public ArrayList<LinkedHashMap<String, String>> getListaConceptosFacturaXml(Integer id_prefactura) {
        String sql_query = "SELECT sku,"
                                + "titulo_producto,"
                                + "unidad,"
                                + "cantidad,"
                                + "(CASE WHEN moneda_id=1 THEN precio_unitario ELSE precio_unitario * tipo_cambio END) AS precio_unitario,"
                                + "(CASE WHEN moneda_id=1 THEN importe ELSE importe * tipo_cambio END) AS importe,"
                                + "(CASE WHEN moneda_id=1 THEN importe * valor_imp ELSE (importe * tipo_cambio) * valor_imp END) AS importe_impuesto,"
                                + "id_impto,"
                                + "tasa_impuesto,"
                                + "valor_imp,"
                                + "tasa_retencion_immex,"
                                + "moneda_id,"
                                + "nombre_moneda,"
                                + "moneda_abr,"
                                + "simbolo_moneda,"
                                + "tipo_cambio "
                        + "FROM( "
                                + "SELECT inv_prod.sku,"
                                        + "inv_prod.descripcion AS titulo_producto,"
                                        + "(CASE WHEN inv_prod_unidades.titulo IS NULL THEN '' ELSE inv_prod_unidades.titulo END) as unidad,"
                                        + "erp_prefacturas_detalles.cant_facturar AS cantidad,"
                                        + "erp_prefacturas_detalles.precio_unitario,"
                                        + "(erp_prefacturas_detalles.cant_facturar * erp_prefacturas_detalles.precio_unitario) AS importe, "
                                        + "erp_prefacturas_detalles.tipo_impuesto_id AS id_impto,"
                                        + "erp_prefacturas_detalles.valor_imp,"
                                        + "(erp_prefacturas_detalles.valor_imp * 100::double precision) AS tasa_impuesto,"
                                        + "erp_prefacturas.tasa_retencion_immex,"
                                        + "erp_prefacturas.moneda_id,"
                                        + "gral_mon.descripcion AS nombre_moneda,"
                                        + "gral_mon.descripcion_abr AS moneda_abr,"
                                        + "gral_mon.simbolo AS simbolo_moneda,"
                                        + "erp_prefacturas.tipo_cambio "
                                + "FROM erp_prefacturas "
                                + "JOIN erp_prefacturas_detalles on erp_prefacturas_detalles.prefacturas_id=erp_prefacturas.id "
                                + "JOIN gral_mon on gral_mon.id=erp_prefacturas.moneda_id "
                                + "LEFT JOIN inv_prod on inv_prod.id = erp_prefacturas_detalles.producto_id "
                                + "LEFT JOIN inv_prod_unidades on inv_prod_unidades.id = erp_prefacturas_detalles.inv_prod_unidad_id "
                                + "WHERE erp_prefacturas_detalles.prefacturas_id="+id_prefactura+" "
                        + ") AS sbt";
        
        //System.out.println(sql_query);
        //System.out.println("Obteniendo lista de conceptos: "+sql_query);
        
        //System.out.println("noIdentificacion "+" | descripcion      "+" | cant"+" | precio_uni"+" | importe"+" | importe_imp"+" | valor_imp"+" | tasa_ret"  );
        
        ArrayList<LinkedHashMap<String, String>> hm_conceptos = (ArrayList<LinkedHashMap<String, String>>) this.jdbcTemplate.query(
            sql_query,
            new Object[]{}, new RowMapper() {
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    LinkedHashMap<String, String> row = new LinkedHashMap<String, String>();
                    //row = aplicar_tipo_cambio_ListaConceptosFactura(rs);
                    row.put("noIdentificacion",StringHelper.normalizaString(StringHelper.remueve_tildes(rs.getString("sku"))));
                    row.put("descripcion",StringHelper.normalizaString(StringHelper.remueve_tildes(rs.getString("titulo_producto"))));
                    row.put("unidad",StringHelper.normalizaString(StringHelper.remueve_tildes(rs.getString("unidad"))));
                    row.put("cantidad",StringHelper.roundDouble(rs.getString("cantidad"),2));
                    row.put("valorUnitario",StringHelper.roundDouble(rs.getDouble("precio_unitario"),4) );
                    row.put("importe",StringHelper.roundDouble(rs.getDouble("importe"),4) );
                    row.put("id_impto",String.valueOf(rs.getInt("id_impto")));
                    row.put("tasa_impuesto",StringHelper.roundDouble(rs.getDouble("tasa_impuesto"),2) );
                    row.put("valor_imp",StringHelper.roundDouble(rs.getDouble("valor_imp"),2) );
                    row.put("importe_impuesto",StringHelper.roundDouble(rs.getDouble("importe_impuesto"),4) );
                    row.put("numero_aduana","");
                    row.put("fecha_aduana","");
                    row.put("aduana_aduana","");
                    row.put("moneda_id",String.valueOf(rs.getInt("moneda_id")));
                    row.put("nombre_moneda",rs.getString("nombre_moneda"));
                    row.put("moneda_abr",rs.getString("moneda_abr"));
                    row.put("simbolo_moneda",rs.getString("simbolo_moneda"));
                    row.put("tipo_cambio",StringHelper.roundDouble(rs.getDouble("tipo_cambio"),4) );
                    row.put("tasa_retencion",StringHelper.roundDouble(rs.getDouble("tasa_retencion_immex"),2) );
                    
                    row.put("descto", "0");
                    row.put("precio_unitario_con_descto", "0");
                    row.put("importe_del_descto", "0");
                    row.put("importe_con_descto", "0");
                    //System.out.println(row.get("noIdentificacion")+"   "+row.get("descripcion")+"   "+row.get("cantidad")+"   "+row.get("valorUnitario")+"   "+row.get("importe")+"   "+row.get("importe_impuesto")+"   "+row.get("valor_imp")+"   "+row.get("tasa_retencion"));
                    
                    return row;
                }
            }
        );
        
        try {
            calcula_Totales_e_Impuestos(hm_conceptos);
        } catch (SQLException ex) {
            Logger.getLogger(FacturasSpringDao.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return hm_conceptos;
    }
    
    
    
    //Éste método se utiliza para CFD y CFDI con Timbre Fiscal
    @Override
    public ArrayList<LinkedHashMap<String, String>> getImpuestosRetenidosFacturaXml(ArrayList<LinkedHashMap<String,String>> conceptos) {
        ArrayList<LinkedHashMap<String, String>>  impuestos = new ArrayList<LinkedHashMap<String, String>>();
        LinkedHashMap<String,String> impuesto = new LinkedHashMap<String,String>();
        
        impuesto.put("impuesto", "IVA");
        impuesto.put("importe", this.getImpuestoRetenido());
        impuesto.put("tasa", this.getTasaRetencion());
        
        impuestos.add(impuesto);
        
        return impuestos;
    }
    
    
    
    
    //Éste método se utiliza para CFD y CFDI con Timbre Fiscal, Devoluciones(Nota de Credito CFDI)
    @Override
    public ArrayList<LinkedHashMap<String, String>> getImpuestosTrasladadosFacturaXml(Integer id_sucursal, ArrayList<LinkedHashMap<String,String>> conceptos, ArrayList<HashMap<String, String>> ieps, ArrayList<HashMap<String, String>> ivas) {
        ArrayList<LinkedHashMap<String, String>>  impuestos = new ArrayList<LinkedHashMap<String, String>>();
        LinkedHashMap<String,String> impuesto;
        
        /*
        Double sumaImpuesto1=0.0;//1;"IVA 16%";0.16
        Double sumaImpuesto2=0.0;//2;"IVA TASA 0";0
        Double sumaImpuesto3=0.0;//3;"IVA 11%";0.11
        Double sumaImpuesto4=0.0;//4;"EXENTO";0
        Double sumaImpuesto5=0.0;//4;"EXENTO";0
        String tasaImpuesto1="0.00";
        String tasaImpuesto2="0.00";
        String tasaImpuesto3="0.00";
        String tasaImpuesto5="0.00";
        boolean impuesto1=false;
        boolean impuesto2=false;
        boolean impuesto3=false;
        boolean impuesto5=false;
        
        for (int x=0; x<=conceptos.size()-1;x++){
            LinkedHashMap<String,String> con = conceptos.get(x);
            Integer idImpuestoProd = Integer.parseInt(con.get("id_impto"));
            Double importeImpuestoProd = Double.parseDouble(StringHelper.roundDouble(con.get("importe_impuesto"),2));
            String tasa = con.get("tasa_impuesto");
            
            if(idImpuestoProd==1){
                sumaImpuesto1 = sumaImpuesto1 + importeImpuestoProd;
                tasaImpuesto1 = tasa;
                impuesto1=true;
            }
            
            if(idImpuestoProd==2 || idImpuestoProd==4){
                sumaImpuesto2 = sumaImpuesto2 + importeImpuestoProd;
                tasaImpuesto2 = tasa;
                impuesto2=true;
            }
            
            if(idImpuestoProd==3){
                sumaImpuesto3 = sumaImpuesto3 + importeImpuestoProd;
                tasaImpuesto3 = tasa;
                impuesto3=true;
            }
            
            //esto por si agregan un impuesto mas
            if(idImpuestoProd==5){
                sumaImpuesto5 = sumaImpuesto5 + importeImpuestoProd;
                tasaImpuesto5 = tasa;
                impuesto5=true;
            }
            //System.out.println("idImpuestoProd:"+idImpuestoProd+" | tasa:"+tasa+" | importe:"+importeImpuestoProd);
        }
        
        if(impuesto1 == true){
            impuesto = new LinkedHashMap<String,String>();
            impuesto.put("impuesto", "IVA");
            impuesto.put("importe", StringHelper.roundDouble(sumaImpuesto1,2));
            impuesto.put("tasa", tasaImpuesto1);
            impuestos.add(impuesto);
            //System.out.println("impuesto1:IVA | tasa:"+tasaImpuesto1+" | importe:"+sumaImpuesto1);
        }
        
        if(impuesto2 == true){
            impuesto = new LinkedHashMap<String,String>();
            impuesto.put("impuesto", "IVA");
            impuesto.put("importe", StringHelper.roundDouble(sumaImpuesto2,2));
            impuesto.put("tasa", tasaImpuesto2);
            impuestos.add(impuesto);
            //System.out.println("impuesto2:IVA | tasa:"+tasaImpuesto2+" | importe:"+sumaImpuesto2);
        }
        
        if(impuesto3 == true){
            impuesto = new LinkedHashMap<String,String>();
            impuesto.put("impuesto", "IVA");
            impuesto.put("importe", StringHelper.roundDouble(sumaImpuesto3,2));
            impuesto.put("tasa", tasaImpuesto3);
            impuestos.add(impuesto);
            //System.out.println("impuesto3:IVA | tasa:"+tasaImpuesto3+" | importe:"+sumaImpuesto3);
        }
        
        //esto por si agregan un impuesto mas
        if(impuesto5 == true){
            impuesto = new LinkedHashMap<String,String>();
            impuesto.put("impuesto", "IVA");
            impuesto.put("importe", StringHelper.roundDouble(sumaImpuesto5,2));
            impuesto.put("tasa", tasaImpuesto5);
            impuestos.add(impuesto);
            //System.out.println("impuesto5:IVA | tasa:"+tasaImpuesto5+" | importe:"+sumaImpuesto5);
        }
        */
            
        
        //Sumar importes por TASA del IVA
        for (int x=0; x<=ivas.size()-1;x++){
            HashMap<String,String> item_iva = ivas.get(x);
            Integer idIva = Integer.parseInt(item_iva.get("id"));
            Integer idIvaConcepto=0;
            Double sumaImporteIva=0.0;
            String tasaIva="";
            
            for (int y=0; y<=conceptos.size()-1;y++){
                LinkedHashMap<String,String> item_con = conceptos.get(y);
                
                if(Integer.parseInt(item_con.get("id_impto"))==idIva){
                    idIvaConcepto = Integer.parseInt(item_con.get("id_impto"));
                    tasaIva = item_con.get("tasa_impuesto");
                    sumaImporteIva = sumaImporteIva + Double.parseDouble(StringHelper.roundDouble(item_con.get("importe_impuesto"),4));
                    //System.out.println(""+idIva+"="+idIvaConcepto+" | tasaIva:"+tasaIva+" | importeIva:"+StringHelper.roundDouble(item_con.get("importe_impuesto"),4));
                }
            }
            
            //Si el id del IEPS en el concepto mayor que cero, quere decir que si incluye IEPS
            if(idIvaConcepto>0){
                impuesto = new LinkedHashMap<String,String>();
                impuesto.put("impuesto", "IVA");
                impuesto.put("importe", StringHelper.roundDouble(sumaImporteIva,2));
                impuesto.put("tasa", tasaIva);
                impuestos.add(impuesto);
                //System.out.println("impuesto:IVA | tasa:"+tasaIva+" | importe:"+StringHelper.roundDouble(sumaImporteIva,2));
            }
        }
        
        
        
        
        //Sumar importes por TASA del IEPS
        for (int x=0; x<=ieps.size()-1;x++){
            HashMap<String,String> item_ieps = ieps.get(x);
            Integer idIeps = Integer.parseInt(item_ieps.get("id"));
            Integer idIepsConcepto=0;
            Double sumaImporteIeps=0.0;
            String tasaIeps="";
            
            for (int y=0; y<=conceptos.size()-1;y++){
                LinkedHashMap<String,String> item_con = conceptos.get(y);
                
                if(Integer.parseInt(item_con.get("id_ieps"))==idIeps){
                    idIepsConcepto = Integer.parseInt(item_con.get("id_ieps"));
                    tasaIeps = item_con.get("tasa_ieps");
                    sumaImporteIeps = sumaImporteIeps + Double.parseDouble(StringHelper.roundDouble(item_con.get("importe_ieps"),4));
                    System.out.println(""+idIeps+"="+idIepsConcepto+" | tasaIeps:"+tasaIeps+" | importeIeps:"+StringHelper.roundDouble(item_con.get("importe_ieps"),4));
                }
            }
            
            //Si el id del IEPS en el concepto mayor que cero, quere decir que si incluye IEPS
            if(idIepsConcepto>0){
                impuesto = new LinkedHashMap<String,String>();
                impuesto.put("impuesto", "IEPS");
                impuesto.put("importe", StringHelper.roundDouble(sumaImporteIeps,2));
                impuesto.put("tasa", tasaIeps);
                impuestos.add(impuesto);
                System.out.println("impuesto:IEPS | tasa:"+tasaIeps+" | importe:"+StringHelper.roundDouble(sumaImporteIeps,2));
            }
        }
        
        return impuestos;
    }
    
    
    
    
    
    
    //obtiene datos extras para la factura
    @Override
    public LinkedHashMap<String, String> getDatosExtrasFacturaXml(String id_prefactura, String tipo_cambio_vista, String id_usuario, String id_moneda, Integer id_empresa, Integer id_sucursal,  String refacturar, Integer app_selected, String command_selected, String extra_data_array) {
        LinkedHashMap<String,String> datosExtras = new LinkedHashMap<String,String>();
        //estos son requeridos para cfd
        datosExtras.put("prefactura_id", id_prefactura);
        datosExtras.put("tipo_cambio", tipo_cambio_vista);
        datosExtras.put("moneda_id", id_moneda);
        datosExtras.put("usuario_id", id_usuario);
        datosExtras.put("empresa_id", String.valueOf(id_empresa));
        datosExtras.put("sucursal_id", String.valueOf(id_sucursal));
        datosExtras.put("refacturar", refacturar);
        datosExtras.put("app_selected", String.valueOf(app_selected));
        datosExtras.put("command_selected", command_selected);
        datosExtras.put("extra_data_array", extra_data_array);
        
        return datosExtras;
    }
    
    
    
    //obtiene datos extras cfdi
    @Override
    public LinkedHashMap<String, String> getDatosExtrasCfdi(Integer id_factura) {
        LinkedHashMap<String,String> datosExtras = new LinkedHashMap<String,String>();
        String monto_factura="";
        String id_moneda="";
        String denom="";
        String denominacion = "";
        String cantidad_letras="";
        
        //obtener id del cliente
        String sql_to_query = ""
                + "SELECT translate(fac_docs.serie_folio,'0123456789 ','') AS serie,"
                        + "translate(fac_docs.serie_folio,'ABCDEFGHIJKLMNÑOPQRSTUVWXYZ abcdefghijklmnñopqrstuvwxyz','') AS folio,"
                        + "(CASE WHEN fac_docs.orden_compra='' THEN '-' ELSE fac_docs.orden_compra END) AS orden_compra,"
                        + "fac_docs.moneda_id, "
                        + "gral_mon.descripcion_abr AS simbolo_moneda, "
                        + "gral_mon.iso_4217 AS nombre_moneda,"
                        + "fac_docs.tipo_cambio,"
                        + "(CASE WHEN fac_docs.cxc_agen_id=0 THEN '' ELSE cxc_agen.nombre END ) AS clave_agente, "
                        + "fac_docs.subtotal as subtotal_conceptos, "
                        + "fac_docs.impuesto, "
                        + "fac_docs.total as monto_total, "
                        + "fac_docs.observaciones "
                + "FROM fac_docs "
                + "JOIN gral_mon ON gral_mon.id=fac_docs.moneda_id "
                + "JOIN cxc_agen ON cxc_agen.id=fac_docs.cxc_agen_id "
                + "WHERE  fac_docs.id="+id_factura+";";
        
        //System.out.println("DatosExtrasCfdi: "+sql_to_query);
        
        Map<String, Object> map = this.getJdbcTemplate().queryForMap(sql_to_query);
        
        
        
        //estos son requeridos para cfdi
        datosExtras.put("serie", map.get("serie").toString());
        datosExtras.put("folio", map.get("folio").toString());
        datosExtras.put("orden_compra", map.get("orden_compra").toString());
        datosExtras.put("clave_agente", map.get("clave_agente").toString());
        datosExtras.put("nombre_moneda", map.get("nombre_moneda").toString());
        datosExtras.put("tipo_cambio", StringHelper.roundDouble(map.get("tipo_cambio").toString(),4));
        datosExtras.put("subtotal_conceptos", StringHelper.roundDouble(map.get("subtotal_conceptos").toString(),2));
        datosExtras.put("monto_total", StringHelper.roundDouble(map.get("monto_total").toString(),2));
        datosExtras.put("observaciones", map.get("observaciones").toString());
        
        monto_factura = StringHelper.roundDouble(map.get("monto_total").toString(),2);
        id_moneda = map.get("moneda_id").toString();
        
        BigInteger num = new BigInteger(monto_factura.split("\\.")[0]);
        n2t cal = new n2t();
        String centavos = monto_factura.substring(monto_factura.indexOf(".")+1);
        String numero = cal.convertirLetras(num);
        
        //convertir a mayuscula la primera letra de la cadena
        String numeroMay = numero.substring(0, 1).toUpperCase() + numero.substring(1, numero.length());
        
        denom = map.get("simbolo_moneda").toString();
        
        if(centavos.equals(num.toString())){
            centavos="00";
        }
        
        if(id_moneda.equals("1")){
            denominacion = "pesos";
        }
        
        if(id_moneda.equals("2")){
            denominacion = "dolares";
        }
        
        cantidad_letras=numeroMay + " " + denominacion + ", " +centavos+"/100 "+ denom;
        datosExtras.put("monto_total_texto", cantidad_letras.toUpperCase());
        
        return datosExtras;
    }
    
    
    
    
    
    //Obtiene la lista de conceptos para cfdi con Buzon Fiscal
    @Override
    public ArrayList<LinkedHashMap<String, String>> getListaConceptosCfdi(Integer id_factura, String rfcEmisor) {
        
        final String rfc = rfcEmisor;
        
        String sql_query = ""
        + "SELECT inv_prod.sku,"
            +"inv_prod.descripcion,"
            +"(CASE WHEN inv_prod_unidades.titulo IS NULL THEN '' ELSE inv_prod_unidades.titulo END) as unidad,"
            +"(CASE WHEN inv_prod_presentaciones.titulo IS NULL THEN '' ELSE inv_prod_presentaciones.titulo END) AS presentacion, "
            +"fac_docs_detalles.cantidad,"
            +"fac_docs_detalles.precio_unitario,"
            +"(fac_docs_detalles.cantidad * fac_docs_detalles.precio_unitario) AS importe, "
            +"fac_docs.moneda_id AS moneda_factura "
        +"FROM fac_docs "
        +"JOIN fac_docs_detalles on fac_docs_detalles.fac_doc_id=fac_docs.id "
        +"LEFT JOIN inv_prod on inv_prod.id = fac_docs_detalles.inv_prod_id "
        +"LEFT JOIN inv_prod_unidades on inv_prod_unidades.id = fac_docs_detalles.inv_prod_unidad_id "
        +"LEFT JOIN inv_prod_presentaciones ON inv_prod_presentaciones.id=fac_docs_detalles.inv_prod_presentacion_id "
        +"WHERE fac_docs.id="+id_factura+";";
        
        //System.out.println("Obteniendo lista de conceptos para cfdi: "+sql_query);
        ArrayList<LinkedHashMap<String, String>> hm_conceptos = (ArrayList<LinkedHashMap<String, String>>) this.jdbcTemplate.query(
            sql_query,
            new Object[]{}, new RowMapper() {
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    LinkedHashMap<String, String> row = new LinkedHashMap<String, String>();
                    row.put("valorUnitario",StringHelper.roundDouble(rs.getDouble("precio_unitario"),2) );
                    row.put("importe",StringHelper.roundDouble(rs.getDouble("importe"),2) );
                    row.put("noIdentificacion",StringHelper.normalizaString(StringHelper.remueve_tildes(rs.getString("sku"))));
                    row.put("descripcion",StringHelper.normalizaString(StringHelper.remueve_tildes(rs.getString("descripcion"))));
                    /*
                    if( rfc.equals("PIS850531CS4") ){
                        row.put("unidad",StringHelper.normalizaString(StringHelper.remueve_tildes(rs.getString("presentacion"))));
                    }else{
                        row.put("unidad",StringHelper.normalizaString(StringHelper.remueve_tildes(rs.getString("unidad"))));
                    }
                    */
                    row.put("unidad",StringHelper.normalizaString(StringHelper.remueve_tildes(rs.getString("unidad"))));
                    
                    row.put("cantidad",StringHelper.roundDouble(rs.getString("cantidad"),2));
                    row.put("numero_aduana","");
                    row.put("fecha_aduana","");
                    row.put("aduana_aduana","");
                    row.put("moneda_factura",rs.getString("moneda_factura"));
                    return row;
                }
            }
        );
        return hm_conceptos;
    }
    
    
    
    @Override
    public ArrayList<LinkedHashMap<String, String>> getImpuestosTrasladadosCfdi(Integer id_factura, Integer id_sucursal) {
        String sql_to_query = "SELECT impuesto FROM fac_docs WHERE  id="+id_factura+" AND impuesto >0 AND impuesto IS NOT NULL;";
        
        final Integer id_suc = id_sucursal;
        
        ArrayList<LinkedHashMap<String, String>> tras = (ArrayList<LinkedHashMap<String, String>>) this.jdbcTemplate.query(
            sql_to_query,new Object[]{}, new RowMapper(){
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    HashMap<String, String> row = new HashMap<String, String>();
                    row.put("impuesto","IVA");
                    row.put("importe",StringHelper.roundDouble(rs.getString("impuesto"),2));
                    row.put("tasa", getTasaIva(id_suc) );
                    return row;
                }
            }
        );
        return tras;
    }
    
    /*
    @Override
    public ArrayList<LinkedHashMap<String, String>> getImpuestosTrasladadosCfdi(Integer id_factura) {
        String sql_to_query = ""
                + "SELECT id_impto, valor_imp, sum(importe) AS importe "
                + "FROM ( "
                    + "SELECT  "
                        + "(CASE WHEN gral_imptos_id=4 THEN 2 ELSE gral_imptos_id END) as id_impto, "
                        + "valor_imp, "
                        + "((cantidad * precio_unitario) * valor_imp) AS importe "
                    + "FROM fac_docs_detalles  "
                    + "WHERE fac_doc_id="+id_factura+" "
                + ") AS sbt "
                + "GROUP BY id_impto, valor_imp;";
        
        ArrayList<LinkedHashMap<String, String>> tras = (ArrayList<LinkedHashMap<String, String>>) this.jdbcTemplate.query(
            sql_to_query,new Object[]{}, new RowMapper(){
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    HashMap<String, String> row = new HashMap<String, String>();
                    row.put("impuesto","IVA");
                    row.put("importe",StringHelper.roundDouble(rs.getString("importe"),2));
                    row.put("tasa", StringHelper.roundDouble(rs.getString("valor_imp"),2) );
                    return row;
                }
            }
        );
        return tras;
    }
    */
    
    
    
    
    @Override
    public ArrayList<LinkedHashMap<String, String>> getImpuestosRetenidosCfdi(Integer id_factura) {
        String sql_to_query = "SELECT monto_retencion, tasa_retencion_immex as tasa FROM fac_docs WHERE id="+id_factura+"  AND monto_retencion >0 AND monto_retencion IS NOT NULL;";
        
        ArrayList<LinkedHashMap<String, String>> ret = (ArrayList<LinkedHashMap<String, String>>) this.jdbcTemplate.query(
            sql_to_query,
            new Object[]{}, new RowMapper(){
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    HashMap<String, String> row = new HashMap<String, String>();
                    row.put("impuesto","IVA");
                    row.put("importe",StringHelper.roundDouble(rs.getString("monto_retencion"),2));
                    row.put("tasa", StringHelper.roundDouble(rs.getString("tasa"),2));
                    return row;
                }
            }
        );
        return ret;
    }
    
    
    
    
    @Override
    public ArrayList<String> getLeyendasEspecialesCfdi(Integer id_empresa) {
        final ArrayList<String> retorno = new ArrayList<String>();
        String sql_to_query = "SELECT leyenda FROM gral_emp_leyenda WHERE gral_emp_id="+id_empresa+";";
        //System.out.println("getLeyendasEspecialesCfdi:"+sql_to_query);
        
        ArrayList<HashMap<String, String>> ret = (ArrayList<HashMap<String, String>>) this.jdbcTemplate.query(
            sql_to_query,
            new Object[]{}, new RowMapper(){
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    HashMap<String, String> row = new HashMap<String, String>();
                    row.put("leyenda",rs.getString("leyenda"));
                    retorno.add(rs.getString("leyenda"));
                    return row;
                }
            }
        );
        return retorno;
    }
    
    
    //***************************************************************************************************************************
    //Comienza métodos específicos para Facturación CFDI TIMBRE FISCAL
    //***************************************************************************************************************************
    //obtiene la lista de conceptos para la factura
    @Override
    public ArrayList<LinkedHashMap<String, String>> getListaConceptosXmlCfdiTf(Integer id_prefactura, String permitir_descuento) {
        ArrayList<LinkedHashMap<String, String>> retorno = new ArrayList<LinkedHashMap<String, String>>();
        String sql_query = ""
        + "SELECT "
                + "inv_prod.sku,"
                + "inv_prod.descripcion,"
                + "(CASE WHEN erp_prefacturas_detalles.gral_ieps_id>0 THEN '. IEPS '||(round((erp_prefacturas_detalles.valor_ieps * 100::double precision)::numeric,2)::double precision) ELSE '' END) AS etiqueta_ieps,"
                + "(CASE WHEN inv_prod_unidades.titulo IS NULL THEN '' ELSE inv_prod_unidades.titulo END) AS unidad,"
                + "erp_prefacturas_detalles.cant_facturar AS cantidad,"
                + "erp_prefacturas_detalles.precio_unitario,"
                + "(CASE WHEN "+ permitir_descuento +"::boolean=true THEN (CASE WHEN erp_prefacturas_detalles.descto IS NULL THEN erp_prefacturas_detalles.precio_unitario ELSE (CASE WHEN erp_prefacturas_detalles.descto>0 THEN (erp_prefacturas_detalles.precio_unitario - (erp_prefacturas_detalles.precio_unitario * (erp_prefacturas_detalles.descto::double precision/100))) ELSE erp_prefacturas_detalles.precio_unitario END) END) ELSE erp_prefacturas_detalles.precio_unitario END) AS precio_unitario_con_descto, "
                + "(erp_prefacturas_detalles.cant_facturar * erp_prefacturas_detalles.precio_unitario) AS importe, "
                + "(CASE WHEN "+ permitir_descuento +"::boolean=true THEN (CASE WHEN erp_prefacturas_detalles.descto IS NULL THEN (erp_prefacturas_detalles.cant_facturar * erp_prefacturas_detalles.precio_unitario) ELSE (CASE WHEN erp_prefacturas_detalles.descto>0 THEN ((erp_prefacturas_detalles.cant_facturar * erp_prefacturas_detalles.precio_unitario) - ((erp_prefacturas_detalles.cant_facturar * erp_prefacturas_detalles.precio_unitario) * (erp_prefacturas_detalles.descto::double precision/100))) ELSE (erp_prefacturas_detalles.cant_facturar * erp_prefacturas_detalles.precio_unitario) END) END) ELSE (erp_prefacturas_detalles.cant_facturar * erp_prefacturas_detalles.precio_unitario) END) AS importe_con_descto, "
                + "(CASE WHEN "+ permitir_descuento +"::boolean=true THEN (CASE WHEN erp_prefacturas_detalles.descto IS NULL THEN 0 ELSE (CASE WHEN erp_prefacturas_detalles.descto>0 THEN ((erp_prefacturas_detalles.cant_facturar * erp_prefacturas_detalles.precio_unitario) * (erp_prefacturas_detalles.descto::double precision/100)) ELSE 0 END) END) ELSE 0 END) AS importe_del_descto, "
                + "erp_prefacturas_detalles.gral_ieps_id AS id_ieps,"
                + "(erp_prefacturas_detalles.valor_ieps * 100::double precision) AS tasa_ieps, "
                //+ "((erp_prefacturas_detalles.cant_facturar * erp_prefacturas_detalles.precio_unitario) * erp_prefacturas_detalles.valor_ieps) AS importe_ieps, "
                + "((erp_prefacturas_detalles.cant_facturar * (CASE WHEN "+ permitir_descuento +"::boolean=true THEN (CASE WHEN erp_prefacturas_detalles.descto IS NULL THEN erp_prefacturas_detalles.precio_unitario ELSE (CASE WHEN erp_prefacturas_detalles.descto>0 THEN (erp_prefacturas_detalles.precio_unitario - (erp_prefacturas_detalles.precio_unitario * (erp_prefacturas_detalles.descto::double precision/100))) ELSE erp_prefacturas_detalles.precio_unitario END) END) ELSE erp_prefacturas_detalles.precio_unitario END)) * erp_prefacturas_detalles.valor_ieps) AS importe_ieps, "
                
                + "(erp_prefacturas_detalles.tasa_ret * 100::double precision) AS tasa_ret, "
                + "((erp_prefacturas_detalles.cant_facturar * (CASE WHEN "+ permitir_descuento +"::boolean=true THEN (CASE WHEN erp_prefacturas_detalles.descto IS NULL THEN erp_prefacturas_detalles.precio_unitario ELSE (CASE WHEN erp_prefacturas_detalles.descto>0 THEN (erp_prefacturas_detalles.precio_unitario - (erp_prefacturas_detalles.precio_unitario * (erp_prefacturas_detalles.descto::double precision/100))) ELSE erp_prefacturas_detalles.precio_unitario END) END) ELSE erp_prefacturas_detalles.precio_unitario END)) * erp_prefacturas_detalles.tasa_ret) AS importe_ret, "
                
                + "erp_prefacturas_detalles.tipo_impuesto_id AS id_impto,"
                + "(erp_prefacturas_detalles.valor_imp * 100::double precision) AS tasa_impuesto,"
                //+ "(((erp_prefacturas_detalles.cant_facturar * erp_prefacturas_detalles.precio_unitario) + ((erp_prefacturas_detalles.cant_facturar * erp_prefacturas_detalles.precio_unitario) * erp_prefacturas_detalles.valor_ieps)) * erp_prefacturas_detalles.valor_imp) AS importe_impuesto, "
                + "(((erp_prefacturas_detalles.cant_facturar * (CASE WHEN "+ permitir_descuento +"::boolean=true THEN (CASE WHEN erp_prefacturas_detalles.descto IS NULL THEN erp_prefacturas_detalles.precio_unitario ELSE (CASE WHEN erp_prefacturas_detalles.descto>0 THEN (erp_prefacturas_detalles.precio_unitario - (erp_prefacturas_detalles.precio_unitario * (erp_prefacturas_detalles.descto::double precision/100))) ELSE erp_prefacturas_detalles.precio_unitario END) END) ELSE erp_prefacturas_detalles.precio_unitario END)) + ((erp_prefacturas_detalles.cant_facturar * (CASE WHEN "+ permitir_descuento +"::boolean=true THEN (CASE WHEN erp_prefacturas_detalles.descto IS NULL THEN erp_prefacturas_detalles.precio_unitario ELSE (CASE WHEN erp_prefacturas_detalles.descto>0 THEN (erp_prefacturas_detalles.precio_unitario - (erp_prefacturas_detalles.precio_unitario * (erp_prefacturas_detalles.descto::double precision/100))) ELSE erp_prefacturas_detalles.precio_unitario END) END) ELSE erp_prefacturas_detalles.precio_unitario END)) * erp_prefacturas_detalles.valor_ieps)) * erp_prefacturas_detalles.valor_imp) AS importe_impuesto, "
                + "erp_prefacturas_detalles.valor_imp,"
                + "erp_prefacturas.tasa_retencion_immex, "
                + "(CASE WHEN "+ permitir_descuento +"::boolean=true THEN (CASE WHEN erp_prefacturas_detalles.descto IS NULL THEN 0 ELSE erp_prefacturas_detalles.descto END) ELSE 0 END) AS descto "
        + "FROM erp_prefacturas "
        + "JOIN erp_prefacturas_detalles on erp_prefacturas_detalles.prefacturas_id=erp_prefacturas.id "
        + "JOIN gral_mon on gral_mon.id=erp_prefacturas.moneda_id "
        + "LEFT JOIN inv_prod on inv_prod.id = erp_prefacturas_detalles.producto_id "
        + "LEFT JOIN inv_prod_unidades on inv_prod_unidades.id = erp_prefacturas_detalles.inv_prod_unidad_id "
        + "WHERE erp_prefacturas_detalles.prefacturas_id="+id_prefactura+";";
        
        System.out.println(sql_query);
        //System.out.println("getListaConceptosXmlCfdiTimbreFiscal: "+sql_query);
        
        //System.out.println("noIdentificacion "+" | descripcion      "+" | cant"+" | precio_uni"+" | importe"+" | importe_imp"+" | valor_imp"+" | tasa_ret"  );
        
        ArrayList<LinkedHashMap<String, String>> hm_conceptos = (ArrayList<LinkedHashMap<String, String>>) this.jdbcTemplate.query(
            sql_query,
            new Object[]{}, new RowMapper() {
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    LinkedHashMap<String, String> row = new LinkedHashMap<String, String>();
                    row.put("noIdentificacion",StringHelper.normalizaString(StringHelper.remueve_tildes(rs.getString("sku"))));
                    row.put("descripcion",StringHelper.normalizaString(StringHelper.remueve_tildes(rs.getString("descripcion"))));
                    row.put("unidad",StringHelper.normalizaString(StringHelper.remueve_tildes(rs.getString("unidad"))));
                    row.put("cantidad",StringHelper.roundDouble(rs.getString("cantidad"),2));
                    row.put("valorUnitario",StringHelper.roundDouble(rs.getDouble("precio_unitario"),4) );
                    row.put("importe",StringHelper.roundDouble(rs.getDouble("importe"),4) );
                    row.put("importe_impuesto",StringHelper.roundDouble(rs.getDouble("importe_impuesto"),4) );
                    row.put("numero_aduana","");
                    row.put("fecha_aduana","");
                    row.put("aduana_aduana","");
                    row.put("id_impto",String.valueOf(rs.getInt("id_impto")));
                    row.put("tasa_impuesto",StringHelper.roundDouble(rs.getDouble("tasa_impuesto"),2) );
                    row.put("valor_imp",StringHelper.roundDouble(rs.getDouble("valor_imp"),2) );
                    row.put("tasa_retencion",StringHelper.roundDouble(rs.getDouble("tasa_retencion_immex"),2) );
                    row.put("id_ieps",String.valueOf(rs.getInt("id_ieps")));
                    row.put("tasa_ieps",StringHelper.roundDouble(rs.getDouble("tasa_ieps"),2) );
                    row.put("importe_ieps",StringHelper.roundDouble(rs.getDouble("importe_ieps"),4) );
                    row.put("tasa_ret",StringHelper.roundDouble(rs.getDouble("tasa_ret"),2) );
                    row.put("importe_ret",StringHelper.roundDouble(rs.getDouble("importe_ret"),4) );
                    
                    row.put("etiqueta_ieps",rs.getString("etiqueta_ieps"));
                    row.put("descto",StringHelper.roundDouble(rs.getDouble("descto"),4) );
                    row.put("precio_unitario_con_descto",StringHelper.roundDouble(rs.getDouble("precio_unitario_con_descto"),4) );
                    row.put("importe_del_descto",StringHelper.roundDouble(rs.getDouble("importe_del_descto"),4) );
                    row.put("importe_con_descto",StringHelper.roundDouble(rs.getDouble("importe_con_descto"),4) );
                    
                    //System.out.println(row.get("noIdentificacion")+"   "+row.get("descripcion")+"   "+row.get("cantidad")+"   "+row.get("valorUnitario")+"   "+row.get("importe")+"   "+row.get("importe_impuesto")+"   "+row.get("valor_imp")+"   "+row.get("tasa_retencion"));
                    
                    return row;
                }
            }
        );
        
        try {
            calcula_Totales_e_Impuestos(hm_conceptos);
        } catch (SQLException ex) {
            Logger.getLogger(FacturasSpringDao.class.getName()).log(Level.SEVERE, null, ex);
        }
        /*
        if(permitir_descuento.equals("true")){
            try {
                retorno = tratar_conceptos_con_descuento(hm_conceptos);
            } catch (SQLException ex) {
                Logger.getLogger(FacturasSpringDao.class.getName()).log(Level.SEVERE, null, ex);
            }
        }else{
            retorno = hm_conceptos;
        }
        */
        return hm_conceptos;
    }
    
    
    
    //Termina métodos específicos para Facturación CFDI TIMBRE FISCAL
    //***************************************************************************************************************************

    
    
    
    
    /*
    //Tratar conceptos cuando incluye descuento, solo es para enviar el importe con descuento
    public ArrayList<LinkedHashMap<String, String>> tratar_conceptos_con_descuento(ArrayList<LinkedHashMap<String, String>> conceptos) throws SQLException{
        ArrayList<LinkedHashMap<String, String>> tratado = new ArrayList<LinkedHashMap<String, String>>();
        LinkedHashMap<String,String> fila;
        
        System.out.println("--TRATANDO CONCEPTOS CON DESCUENTO---------------------------");
        for( LinkedHashMap<String,String> i : conceptos ){
            fila = new LinkedHashMap<String,String>();
            
            fila.put("noIdentificacion",i.get("noIdentificacion"));
            fila.put("descripcion",i.get("descripcion"));
            fila.put("unidad",i.get("unidad"));
            fila.put("cantidad",i.get("cantidad"));
            fila.put("valorUnitario",i.get("valorUnitario"));
            fila.put("importe",i.get("importe"));
            fila.put("importe_impuesto",i.get("importe_impuesto"));
            fila.put("numero_aduana",i.get("numero_aduana"));
            fila.put("fecha_aduana",i.get("fecha_aduana"));
            fila.put("aduana_aduana",i.get("aduana_aduana"));
            fila.put("id_impto", i.get("id_impto"));
            fila.put("tasa_impuesto", i.get("tasa_impuesto"));
            fila.put("valor_imp", i.get("valor_imp"));
            fila.put("tasa_retencion", i.get("tasa_retencion"));
            fila.put("id_ieps", i.get("id_ieps"));
            fila.put("tasa_ieps", i.get("tasa_ieps"));
            fila.put("importe_ieps", i.get("importe_ieps"));
            fila.put("etiqueta_ieps", i.get("etiqueta_ieps"));
            fila.put("descto", i.get("descto"));
            fila.put("importe_del_descto", i.get("importe_del_descto"));
            fila.put("importe_con_descto", i.get("importe_con_descto"));
            tratado.add(fila);
        }
        
        return tratado;
    }
    */
    
    /*
    //ejecuta procesos relacionados a facturacion
    @Override
    public Boolean update_fac_docs_salidas(String serie_folio, String nombre_archivo){
        String sql_to_query = "UPDATE fac_docs SET salida=TRUE, nombre_archivo='"+nombre_archivo+".xml' WHERE serie_folio ='"+serie_folio+"' returning salida;";
        Map<String, Object> update = this.getJdbcTemplate().queryForMap(sql_to_query);
        
        Boolean valor_retorno= Boolean.parseBoolean(update.get("salida").toString());
        return valor_retorno;
    }
    */
    
    
    
    
    
    
    
    //Obtiene la lista de conceptos  de la factura para el pdfCfd
    @Override
    public ArrayList<HashMap<String, String>> getListaConceptosPdfCfd(String serieFolio) {
        
        String sql_query = ""
        + "SELECT "
            + "fac_docs_detalles.id as id_detalle,"
            + "fac_docs_detalles.inv_prod_id AS producto_id,"
            + "inv_prod.sku,"
            + "inv_prod.descripcion as titulo,"
            + "(CASE WHEN fac_docs_detalles.gral_ieps_id>0 THEN ' - IEPS '||(round((fac_docs_detalles.valor_ieps * 100::double precision)::numeric,2))||'%' ELSE '' END) AS etiqueta_ieps,"
            + "(CASE WHEN inv_prod_unidades.titulo IS NULL THEN '' ELSE inv_prod_unidades.titulo END) AS unidad,"
            + "(CASE WHEN inv_prod_unidades.decimales IS NULL THEN 0 ELSE inv_prod_unidades.decimales END) AS decimales,"
            + "(CASE WHEN inv_prod_presentaciones.id IS NULL THEN 0 ELSE inv_prod_presentaciones.id END) AS id_presentacion,"
            + "(CASE WHEN inv_prod_presentaciones.titulo IS NULL THEN '' ELSE inv_prod_presentaciones.titulo END) AS presentacion,"
            + "fac_docs_detalles.cantidad,"
            + "fac_docs_detalles.precio_unitario,"
            + "(fac_docs_detalles.cantidad * fac_docs_detalles.precio_unitario) AS importe, "
            + "(CASE WHEN fac_docs_detalles.gral_ieps_id>0 THEN ((fac_docs_detalles.cantidad * fac_docs_detalles.precio_unitario) * fac_docs_detalles.valor_ieps) ELSE 0 END) AS importe_ieps,"
            + "gral_mon.descripcion as moneda,"
            + "gral_mon.simbolo AS simbolo_moneda "
        + "FROM fac_docs "
        + "JOIN fac_docs_detalles on fac_docs_detalles.fac_doc_id=fac_docs.id "
        + "LEFT JOIN gral_mon on gral_mon.id = fac_docs.moneda_id "
        + "LEFT JOIN inv_prod on inv_prod.id = fac_docs_detalles.inv_prod_id "
        + "LEFT JOIN inv_prod_unidades on inv_prod_unidades.id = fac_docs_detalles.inv_prod_unidad_id "
        + "LEFT JOIN inv_prod_presentaciones on inv_prod_presentaciones.id = fac_docs_detalles.inv_prod_presentacion_id "
        + "WHERE fac_docs.serie_folio='"+serieFolio+"';";
        
        
        //System.out.println("Obtiene lista conceptos pdf: "+sql_query);
        ArrayList<HashMap<String, String>> hm_grid = (ArrayList<HashMap<String, String>>) this.jdbcTemplate.query(
            sql_query,  
            new Object[]{}, new RowMapper() {
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    HashMap<String, Object> row = new HashMap<String, Object>();
                    row.put("id_detalle",rs.getInt("id_detalle"));
                    row.put("producto_id",rs.getString("producto_id"));
                    row.put("sku",rs.getString("sku"));
                    row.put("titulo",rs.getString("titulo"));
                    row.put("numero_lote","");
                    
                    row.put("unidad",rs.getString("unidad"));
                    row.put("id_presentacion",rs.getString("id_presentacion"));
                    row.put("presentacion",rs.getString("presentacion"));
                    row.put("cantidad",StringHelper.roundDouble( rs.getString("cantidad"),2 ));
                    row.put("precio_unitario",StringHelper.roundDouble(rs.getDouble("precio_unitario"),4) );
                    row.put("importe",StringHelper.roundDouble(rs.getDouble("importe"),2) );
                    row.put("moneda",rs.getString("moneda"));
                    row.put("simbolo_moneda",rs.getString("simbolo_moneda"));
                    row.put("denominacion","");
                    
                    row.put("etiqueta_ieps",rs.getString("etiqueta_ieps"));
                    row.put("importe_ieps",StringHelper.roundDouble(rs.getDouble("importe_ieps"),2) );
                    
                    
                    /*
                    System.out.println(rs.getString("moneda")+"  "
                            + ""+rs.getString("sku")+"  "
                            + "    "+StringHelper.roundDouble(rs.getDouble("precio_unitario"),4)
                            + "    "+StringHelper.roundDouble(rs.getDouble("importe"),2)
                            
                            );
                    */
                    return row;
                }
            }
        );
        return hm_grid;
    }
    
    
    
    
    
    
    @Override
    public HashMap<String, String> getDatosExtrasPdfCfd(String serieFolio, String proposito, String cadena_original, String sello_digital, Integer id_sucursal) {
        HashMap<String, String> extras = new HashMap<String, String>();
        ArrayList<HashMap<String, Object>> valorIva = new ArrayList<HashMap<String, Object>>();
        
        valorIva= getValoriva(id_sucursal);//obtiene el valor del iva
        
        //obtener datos del vendedor y terminos de pago
        String sql_to_query = ""
        + "SELECT "
            + "fac_docs.subtotal, "
            + "fac_docs.monto_descto, "
            + "(CASE WHEN fac_docs.subtotal_sin_descto IS NULL THEN 0 ELSE fac_docs.subtotal_sin_descto END) AS subtotal_sin_descto, "
            + "(CASE WHEN fac_docs.monto_descto>0 THEN fac_docs.motivo_descto ELSE '' END) AS motivo_descto, "
            + "fac_docs.monto_ieps,"
            + "fac_docs.impuesto, "
            + "fac_docs.monto_retencion, "
            + "fac_docs.total, "
            + "(CASE WHEN fac_docs.fecha_vencimiento IS NULL THEN '' ELSE to_char(fac_docs.fecha_vencimiento,'dd/mm/yyyy') END) AS fecha_vencimiento, "
            + "fac_docs.orden_compra, "
            + "fac_docs.orden_compra AS folio_pedido, "
            + "fac_docs.observaciones, "
            + "cxc_agen.nombre AS nombre_vendedor, "
            + "cxc_clie_credias.descripcion AS terminos,"
            + "cxc_clie_credias.dias, "
            + "gral_mon.descripcion AS nombre_moneda,"
            + "gral_mon.descripcion_abr AS moneda_abr,"
            + "(CASE WHEN fac_docs.ref_id='' THEN fac_docs.serie_folio ELSE fac_docs.ref_id END) AS ref_id "
        + "FROM fac_docs  "
        + "LEFT JOIN cxc_clie_credias ON cxc_clie_credias.id = fac_docs.terminos_id "
        + "LEFT JOIN gral_mon on gral_mon.id = fac_docs.moneda_id "
        + "JOIN cxc_agen ON cxc_agen.id =  fac_docs.cxc_agen_id  "
        + "WHERE fac_docs.serie_folio='"+serieFolio+"';";
        
        Map<String, Object> mapVendedorCondiciones = this.getJdbcTemplate().queryForMap(sql_to_query);
        extras.put("subtotal", StringHelper.roundDouble(mapVendedorCondiciones.get("subtotal").toString(),2));
        extras.put("monto_ieps", StringHelper.roundDouble(mapVendedorCondiciones.get("monto_ieps").toString(),2));
        extras.put("impuesto", StringHelper.roundDouble(mapVendedorCondiciones.get("impuesto").toString(),2));
        extras.put("monto_retencion", StringHelper.roundDouble(mapVendedorCondiciones.get("monto_retencion").toString(),2));
        extras.put("total", StringHelper.roundDouble(mapVendedorCondiciones.get("total").toString(),2));
        extras.put("nombre_moneda", mapVendedorCondiciones.get("nombre_moneda").toString());
        extras.put("moneda_abr", mapVendedorCondiciones.get("moneda_abr").toString());
        
        extras.put("monto_descto", StringHelper.roundDouble(mapVendedorCondiciones.get("monto_descto").toString(),2));
        extras.put("subtotal_sin_descto", StringHelper.roundDouble(mapVendedorCondiciones.get("subtotal_sin_descto").toString(),2));
        extras.put("motivo_descto", mapVendedorCondiciones.get("motivo_descto").toString());
        
        extras.put("nombre_vendedor", mapVendedorCondiciones.get("nombre_vendedor").toString());
        extras.put("terminos", mapVendedorCondiciones.get("terminos").toString());
        extras.put("dias", mapVendedorCondiciones.get("dias").toString());
        extras.put("orden_compra", mapVendedorCondiciones.get("orden_compra").toString() );
        extras.put("folio_pedido", mapVendedorCondiciones.get("folio_pedido").toString() );
        extras.put("observaciones", mapVendedorCondiciones.get("observaciones").toString() );
        extras.put("fecha_vencimiento", mapVendedorCondiciones.get("fecha_vencimiento").toString() );
        
        extras.put("proposito", proposito);
        extras.put("cadena_original", cadena_original);
        extras.put("sello_digital", sello_digital);
        extras.put("serieFolio", serieFolio);
        extras.put("valor_iva", StringHelper.roundDouble(valorIva.get(0).get("valor_impuesto").toString(),2));
        extras.put("fecha_comprobante", getFechaComprobante());
        extras.put("refId", mapVendedorCondiciones.get("ref_id").toString());
        
        return extras;
    }
    
    
    
    
    //Obtener el tipo de Facturacion
    @Override
    public String getTipoFacturacion(Integer idEmp) {
        String valor_retorno="";
        String sql_query = "SELECT tipo_facturacion FROM gral_emp WHERE id="+idEmp+";";
        
        //System.out.println("Obtiene tipo de facturacion: "+ sql_query);
        Map<String, Object> tipo = this.getJdbcTemplate().queryForMap(sql_query);
        valor_retorno= tipo.get("tipo_facturacion").toString();
        
        return valor_retorno;
    }
    
    
    //Obtener el numero del PAC para el Timbrado de la Factura
    @Override
    public String getNoPacFacturacion(Integer idEmp) {
        String valor_retorno="";
        String sql_query = "SELECT pac_facturacion FROM gral_emp WHERE id="+idEmp+";";
        
        //System.out.println("Obtiene tipo de facturacion: "+ sql_query);
        Map<String, Object> tipo = this.getJdbcTemplate().queryForMap(sql_query);
        valor_retorno= tipo.get("pac_facturacion").toString();
        
        return valor_retorno;
    }
    
    
    //Ambiente de Facturacion PRUEBAS ó PRODUCCION, solo aplica para Facturacion por Timbre FIscal(cfditf)
    @Override
    public String getAmbienteFacturacion(Integer idEmp) {
        String valor_retorno="";
        String sql_query = "SELECT (CASE WHEN ambiente_facturacion=true THEN 'produccion' ELSE 'prueba' END) AS ambiente_facturacion FROM gral_emp WHERE id="+idEmp+";";
        
        //System.out.println("Obtiene tipo de facturacion: "+ sql_query);
        Map<String, Object> tipo = this.getJdbcTemplate().queryForMap(sql_query);
        valor_retorno= tipo.get("ambiente_facturacion").toString();
        
        return valor_retorno;
    }
    
    
    
    
    @Override
    public HashMap<String, String> getParametrosEmpresa(Integer idEmp) {
        HashMap<String, String> data = new HashMap<String, String>();
        
        //Obtener configuraciones a nivel empresa
        String sql_to_query = "SELECT tipo_facturacion, pac_facturacion, (CASE WHEN ambiente_facturacion=true THEN 'produccion' ELSE 'prueba' END) AS ambiente_facturacion FROM gral_emp WHERE id="+idEmp+";";
        Map<String, Object> map = this.getJdbcTemplate().queryForMap(sql_to_query);
        data.put("tipo_facturacion", String.valueOf(map.get("tipo_facturacion")));
        data.put("pac_facturacion", String.valueOf(map.get("pac_facturacion")));
        data.put("ambiente_facturacion", String.valueOf(map.get("ambiente_facturacion")));
        
        return data;
    }
    
    
    
    
    /*
    //verificar si ya se generó el xml y pdf de la factura en el buzon fiscal
    @Override
    public String verifica_fac_docs_salidas(Integer id_factura){
        String sql_to_query = "SELECT salida FROM fac_docs WHERE id ="+id_factura;
        Map<String, Object> salida = this.getJdbcTemplate().queryForMap(sql_to_query);
        String valor_retorno= salida.get("salida").toString();
        return valor_retorno;
    }
    */
    
    
    
    @Override
    public String getSerieFolioFactura(Integer id_factura, Integer idEmp) {
        String sql_to_query="";
        
        //obtener tipo de facturacion
        String tipo_facturacion = getTipoFacturacion(idEmp);
        if(tipo_facturacion.equals("cfd")){
            //para facturacion tipo CFD
            sql_to_query = "SELECT split_part(fac_cfds.nombre_archivo, '.', 1) AS nombre_archivo FROM fac_docs  JOIN erp_proceso ON erp_proceso.id=fac_docs.proceso_id JOIN  fac_cfds ON fac_cfds.proceso_id= erp_proceso.id WHERE fac_docs.id="+id_factura+" ORDER BY fac_cfds.id DESC LIMIT 1;";
        }
        
        if(tipo_facturacion.equals("cfdi")){
            //para facturacion tipo CFDI Buzon Fiscal
            sql_to_query = "SELECT serie_folio AS nombre_archivo FROM fac_docs WHERE id ="+id_factura+";";
        }
        
        if(tipo_facturacion.equals("cfditf")){
            //para facturacion tipo CFDI Buzon Fiscal
            sql_to_query = "SELECT fac_docs.serie_folio AS nombre_archivo FROM fac_docs JOIN erp_proceso ON erp_proceso.id=fac_docs.proceso_id WHERE fac_docs.id="+id_factura+" AND erp_proceso.empresa_id="+idEmp+" LIMIT 1;";
        }
        
        //System.out.println(sql_to_query);
        Map<String, Object> map_iva = this.getJdbcTemplate().queryForMap(sql_to_query);
        String serie_folio = map_iva.get("nombre_archivo").toString();
        return serie_folio;
    }
    
    
    
    @Override
    public String getRefIdFactura(Integer id_factura, Integer idEmp) {
        String sql_to_query="";
        
        //obtener tipo de facturacion
        String tipo_facturacion = getTipoFacturacion(idEmp);
        if(tipo_facturacion.equals("cfditf")){
            //para facturacion tipo CFDI Buzon Fiscal
            sql_to_query = "SELECT (CASE WHEN fac_docs.ref_id='' THEN fac_docs.serie_folio ELSE fac_docs.ref_id END) AS ref_id FROM fac_docs JOIN erp_proceso ON erp_proceso.id=fac_docs.proceso_id WHERE fac_docs.id="+id_factura+" AND erp_proceso.empresa_id="+idEmp+" LIMIT 1;";
        }
        
        //System.out.println(sql_to_query);
        Map<String, Object> map_iva = this.getJdbcTemplate().queryForMap(sql_to_query);
        String ref_id = map_iva.get("ref_id").toString();
        return ref_id;
    }
    
    
    @Override
    public String getRefIdByIdPrefactura(Integer id_prefactura, Integer idEmp) {
        String sql_to_query="";
        
        //obtener tipo de facturacion
        String tipo_facturacion = getTipoFacturacion(idEmp);
        if(tipo_facturacion.equals("cfditf")){
            //para facturacion tipo CFDI Buzon Fiscal
            sql_to_query = "SELECT (CASE WHEN fac_docs.ref_id='' THEN fac_docs.serie_folio ELSE fac_docs.ref_id END) AS ref_id FROM erp_prefacturas  JOIN  fac_docs ON fac_docs.proceso_id=erp_prefacturas.proceso_id WHERE erp_prefacturas.id="+id_prefactura+" AND fac_docs.cancelado=false ORDER BY fac_docs.id DESC LIMIT 1;";
        }
        
        //System.out.println(sql_to_query);
        Map<String, Object> map_iva = this.getJdbcTemplate().queryForMap(sql_to_query);
        String ref_id = map_iva.get("ref_id").toString();
        return ref_id;
    }

    
    
    @Override
    public String getSerieFolioFacturaByIdPrefactura(Integer id_prefactura, Integer idEmp) {
        String sql_to_query="";
        
        //obtener tipo de facturacion
        String tipo_facturacion = getTipoFacturacion(idEmp);
        
        if(tipo_facturacion.equals("cfd")){
            //para facturacion tipo CFD
            sql_to_query = "SELECT split_part(fac_cfds.nombre_archivo, '.', 1) AS serie_folio FROM erp_prefacturas  JOIN erp_proceso ON erp_proceso.id=erp_prefacturas.proceso_id JOIN  fac_cfds ON fac_cfds.proceso_id= erp_proceso.id WHERE erp_prefacturas.id="+id_prefactura+" ORDER BY fac_cfds.id DESC LIMIT 1;";
        }
        
        if(tipo_facturacion.equals("cfdi")){
            //para facturacion tipo CFDI Timbre Fiscal
            sql_to_query = "SELECT fac_docs.serie_folio FROM erp_prefacturas  JOIN  fac_docs ON fac_docs.proceso_id=erp_prefacturas.proceso_id WHERE erp_prefacturas.id="+id_prefactura+" AND fac_docs.cancelado=false ORDER BY fac_docs.id DESC LIMIT 1;";
        }
        
        if(tipo_facturacion.equals("cfditf")){
            //para facturacion tipo CFDI Timbre Fiscal
            sql_to_query = "SELECT fac_docs.serie_folio FROM erp_prefacturas  JOIN  fac_docs ON fac_docs.proceso_id=erp_prefacturas.proceso_id WHERE erp_prefacturas.id="+id_prefactura+" AND fac_docs.cancelado=false ORDER BY fac_docs.id DESC LIMIT 1;";
        }
        
        //System.out.println("GetSerieFolio:"+sql_to_query);
        Map<String, Object> map_iva = this.getJdbcTemplate().queryForMap(sql_to_query);
        String serie_folio = map_iva.get("serie_folio").toString();
        return serie_folio;
    }
    
    
    
    @Override
    public Integer getIdPrefacturaByIdFactura(Integer id_factura) {
        String sql_to_query="";
        
        sql_to_query = "SELECT erp_prefacturas.id AS id_prefactura FROM fac_docs JOIN erp_prefacturas ON erp_prefacturas.proceso_id=fac_docs.proceso_id WHERE fac_docs.id="+id_factura+" LIMIT 1;";
        
        //System.out.println(sql_to_query);
        Map<String, Object> map = this.getJdbcTemplate().queryForMap(sql_to_query);
        Integer id_prefactura = Integer.parseInt(map.get("id_prefactura").toString());
        return id_prefactura;
    }
    
    
    
    @Override
    public ArrayList<HashMap<String, String>> getTiposCancelacion() {
        String sql_to_query = "SELECT id, titulo FROM fac_docs_tipos_cancelacion WHERE borrado_logico=false ORDER BY id;";
        //log.log(Level.INFO, "Ejecutando query de {0}", sql_to_query);
        ArrayList<HashMap<String, String>> hm = (ArrayList<HashMap<String, String>>) this.jdbcTemplate.query(
            sql_to_query,
            new Object[]{}, new RowMapper(){
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    HashMap<String, String> row = new HashMap<String, String>();
                    row.put("id",String.valueOf(rs.getInt("id")));
                    row.put("titulo",rs.getString("titulo"));
                    return row;
                }
            }
        );
        return hm;
    }
    
    
    
    
    
    //forma una cadena con todos los conceptos de la factura
    @Override
    public String formar_cadena_conceptos(ArrayList<LinkedHashMap<String,String>> concepts){
            
        String valor_retorno = new String();
        
        if(concepts.size() > 0){
            for (HashMap<String,String> concept : concepts ){
                String precio_unitario = String.valueOf(concept.get("valorUnitario"));
                String importe = String.valueOf(concept.get("importe") );
                String concepto = concept.get("noIdentificacion") + "+&+" + concept.get("cantidad") + "+&+" + concept.get("descripcion").replace("'", "\"") + "+&+" + precio_unitario + "+&+" + concept.get("unidad") + "+&+" + importe;
                valor_retorno += concepto + "$$$";
            }
            return valor_retorno.substring(0, valor_retorno.length() - 3);
        }
        return " ";
    }
    
    
    
    @Override
    public String formar_cadena_traslados(String cantidad_lana_iva,String tasa_iva){
        String valor_retorno = new String();
        double tasa = Double.parseDouble(tasa_iva);
        valor_retorno = cantidad_lana_iva+"+&+IVA+&+"+String.valueOf(tasa);
        return valor_retorno;
    }
    
    
    @Override
    public String formar_cadena_retenidos(String cantidad_lana_iva,String tasa_iva){
        String valor_retorno = new String();
        double tasa = Double.parseDouble(tasa_iva);
        valor_retorno = cantidad_lana_iva+"+&+IVA+&+"+String.valueOf(tasa);
        return valor_retorno;
    }
    
    
    
    
    
    //guarda los datos en la tabla fac_cfds
    @Override
    public void fnSalvaDatosFacturas(String rfc_receptor,
                        String serie_factura,
                        String folio_factura,
                        String no_probacion,
                        String total,
                        String tot_imp_trasladados,
                        String edo_comprobante,
                        String xml_file_name,
                        String fecha,
                        String razon_social_receptor,
                        String tipo_comprobante,
                        String proposito,
                        String ano_probacion ,
                        String cadena_conceptos,
                        String cadena_imp_trasladados,
                        String cadena_imp_retenidos,
                        Integer prefactura_id,
                        Integer id_usuario,
                        Integer id_moneda,
                        String tipo_cambio,
                        String refacturar,
                        String regimen_fiscal,
                        String metodo_pago,
                        String num_cuenta,
                        String lugar_de_expedicion
                       ) {
        
        String sql_insert = "select erp_fn_salva_datos_factura from erp_fn_salva_datos_factura('"+ rfc_receptor +"','" + 
                                serie_factura +"','" + 
                                folio_factura +"','" + 
                                no_probacion +"'," + 
                                total +"," + 
                                tot_imp_trasladados +",'" + 
                                edo_comprobante +"','" + 
                                xml_file_name +"','" + 
                                fecha +"','" + 
                                razon_social_receptor +"','" + 
                                tipo_comprobante +"','" + 
                                proposito +"','" + 
                                ano_probacion +"','" + 
                                cadena_conceptos +"','" + 
                                cadena_imp_trasladados +"','" + 
                                cadena_imp_retenidos +"'," + 
                                prefactura_id +"," + 
                                id_usuario +"," + 
                                id_moneda +"," + 
                                tipo_cambio+",'"+
                                refacturar+"','"+
                                regimen_fiscal+"','"+
                                metodo_pago+"','"+
                                num_cuenta+"','"+
                                lugar_de_expedicion +"')";
        
        //System.out.println("Iniciando:"+sql_insert);
        System.out.println("Iniciando salvar datos factura");
        Map<String, Object> map_salva = this.getJdbcTemplate().queryForMap(sql_insert);
        
        String salvado = map_salva.get("erp_fn_salva_datos_factura").toString();
        if(salvado.equals("true")){
            //System.out.println("Datos salvados en fac_cfds: "+sql_insert);
        }else{
            //System.out.println("Datos NO salvados en fac_cfds: "+sql_insert);
        }
    }
    
    
    
    
    
    
    //obtiene lista de facturas de un periodo
    @Override
    public ArrayList<HashMap<String, String>> getDatosReporteFacturacion(Integer opcion, String factura, String cliente, String fecha_inicial, String fecha_final, Integer id_empresa) {
        
        String where="";
        
        //opcion = 1, Ventas Totales(Ventas a filiales y  y No filiales)
        //ventas a filiales
        if (opcion==2){
            where=" AND cxc_clie.filial=TRUE";
        }
        
        //ventas Netas(vantas a No Filiales)
        if (opcion==3){
            where=" AND cxc_clie.filial=FALSE";
        }
        
        String sql_to_query = ""
                + "SELECT "
                            + "fac_docs.id, "
                            + "fac_docs.serie_folio, "
                            + "fac_docs.orden_compra, "
                            + "to_char(fac_docs.momento_creacion,'dd/mm/yyyy') as fecha_factura,"
                            + "(CASE WHEN fac_docs.cancelado=FALSE THEN cxc_clie.razon_social ELSE 'CANCELADA' END) AS cliente, "
                            + "gral_mon.id AS id_moneda, "
                            + "gral_mon.descripcion_abr AS moneda_factura, "
                            + "gral_mon.simbolo AS simbolo_moneda, "
                            + "(CASE WHEN fac_docs.cancelado=FALSE THEN fac_docs.subtotal ELSE 0.0 END) AS subtotal, "
                            + "(CASE WHEN fac_docs.cancelado=FALSE THEN fac_docs.subtotal*tipo_cambio ELSE 0.0 END) AS subtotal_mn, "
                            + "(CASE WHEN fac_docs.cancelado=FALSE THEN fac_docs.monto_ieps ELSE 0.0 END) AS monto_ieps, "
                            + "(CASE WHEN fac_docs.cancelado=FALSE THEN fac_docs.monto_ieps*tipo_cambio ELSE 0.0 END) AS monto_ieps_mn, "
                            + "(CASE WHEN fac_docs.cancelado=FALSE THEN fac_docs.impuesto ELSE 0.0 END) AS impuesto, "
                            + "(CASE WHEN fac_docs.cancelado=FALSE THEN fac_docs.impuesto*tipo_cambio ELSE 0.0 END) AS impuesto_mn, "
                            + "(CASE WHEN fac_docs.cancelado=FALSE THEN fac_docs.total ELSE 0.0 END) AS total, "
                            + "(CASE WHEN fac_docs.cancelado=FALSE THEN fac_docs.total*tipo_cambio ELSE 0.0 END) AS total_mn,  "
                            + "(CASE WHEN fac_docs.moneda_id=1 THEN 1 ELSE fac_docs.tipo_cambio END) AS tipo_cambio  "
                    + "FROM fac_docs   "
                    + "JOIN erp_proceso ON erp_proceso.id=fac_docs.proceso_id  "
                    + "JOIN cxc_clie ON cxc_clie.id = fac_docs.cxc_clie_id   "
                    + "JOIN gral_mon ON gral_mon.id = fac_docs.moneda_id  "
                    + "WHERE erp_proceso.empresa_id ="+id_empresa + "  "
                    + "AND fac_docs.serie_folio ILIKE '"+factura+"' "
                    + "AND cxc_clie.razon_social ILIKE '"+cliente+"'  "+where+" "
                    + "AND (to_char(fac_docs.momento_creacion,'yyyymmdd') BETWEEN  to_char('"+fecha_inicial+"'::timestamp with time zone,'yyyymmdd') AND to_char('"+fecha_final+"'::timestamp with time zone,'yyyymmdd')) "
                    + "ORDER BY fac_docs.id;";
            
        //System.out.println("ReporteFacturacion:: "+sql_to_query);
        ArrayList<HashMap<String, String>> hm_facturas = (ArrayList<HashMap<String, String>>) this.jdbcTemplate.query(
            sql_to_query,
            new Object[]{}, new RowMapper(){
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    HashMap<String, String> row = new HashMap<String, String>();
                    row.put("id",String.valueOf(rs.getInt("id")));
                    row.put("serie_folio",rs.getString("serie_folio"));
                    row.put("orden_compra",rs.getString("orden_compra"));
                    row.put("fecha_factura",rs.getString("fecha_factura"));
                    row.put("cliente",rs.getString("cliente"));
                    row.put("id_moneda",rs.getString("id_moneda"));
                    row.put("moneda_factura",rs.getString("moneda_factura"));
                    row.put("simbolo_moneda",rs.getString("simbolo_moneda"));
                    row.put("subtotal",StringHelper.roundDouble(rs.getDouble("subtotal"), 2));
                    row.put("subtotal_mn",StringHelper.roundDouble(rs.getDouble("subtotal_mn"), 2));
                    row.put("impuesto",StringHelper.roundDouble(rs.getDouble("impuesto"), 2));
                    row.put("impuesto_mn",StringHelper.roundDouble(rs.getDouble("impuesto_mn"), 2));
                    row.put("total",StringHelper.roundDouble(rs.getDouble("total"), 2));
                    row.put("total_mn",StringHelper.roundDouble(rs.getDouble("total_mn"), 2));
                    row.put("monto_ieps",StringHelper.roundDouble(rs.getDouble("monto_ieps"), 2));
                    row.put("monto_ieps_mn",StringHelper.roundDouble(rs.getDouble("monto_ieps_mn"), 2));
                    return row;
                }
            }
        );
        return hm_facturas;
    }
    
    
    
    
    
    
    
    //obtiene datos para el Reporte de Remisiones
    //variable estatus agregado por paco
    @Override
    public ArrayList<HashMap<String, String>> getDatosReporteRemision(Integer opcion, String remision, String cliente, String fecha_inicial, String fecha_final, Integer id_empresa, Integer estatus) {
        
        String where="";
        //opcion = 1, Ventas Totales(Ventas a filiales y  y No filiales)
        //ventas a filiales
        if (opcion==2){
            where=" AND cxc_clie.filial=TRUE ";
        }
        
        //ventas Netas(vantas a No Filiales)
        if (opcion==3){
            where=" AND cxc_clie.filial=FALSE ";
        }
        
        if (estatus==0 || estatus==1){
            where+=" AND fac_rems.estatus="+estatus+" AND fac_rems.facturado=FALSE AND fac_rems.cancelado=FALSE ";
        }
        
        if (estatus==2){
            where+=" AND fac_rems.facturado=TRUE AND fac_rems.cancelado=FALSE";
        }

        if (estatus==3){
            where+=" AND fac_rems.cancelado=TRUE";
        }
        
        String sql_to_query = ""
                + "SELECT fac_rems.folio, "
                            + "fac_rems.orden_compra, "
                            + "to_char(fac_rems.momento_creacion,'dd/mm/yyyy') as fecha_remision,"
                            + "(CASE WHEN fac_rems.cancelado=FALSE THEN cxc_clie.razon_social ELSE 'CANCELADA' END) AS cliente, "
                            + "fac_rems.moneda_id, "
                            + "gral_mon.simbolo AS moneda_simbolo, "
                            + "gral_mon.descripcion_abr AS moneda_remision, "
                            + "(CASE WHEN fac_rems.cancelado=FALSE THEN fac_rems.subtotal ELSE 0.0 END) AS subtotal, "
                            + "(CASE WHEN fac_rems.cancelado=FALSE THEN fac_rems.subtotal*tipo_cambio ELSE 0.0 END) AS subtotal_mn, "
                            + "(CASE WHEN fac_rems.cancelado=FALSE THEN fac_rems.monto_ieps ELSE 0.0 END) AS monto_ieps, "
                            + "(CASE WHEN fac_rems.cancelado=FALSE THEN fac_rems.monto_ieps*tipo_cambio ELSE 0.0 END) AS monto_ieps_mn, "
                            + "(CASE WHEN fac_rems.cancelado=FALSE THEN fac_rems.impuesto ELSE 0.0 END) AS impuesto, "
                            + "(CASE WHEN fac_rems.cancelado=FALSE THEN fac_rems.impuesto*tipo_cambio ELSE 0.0 END) AS impuesto_mn, "
                            + "(CASE WHEN fac_rems.cancelado=FALSE THEN fac_rems.total ELSE 0.0 END) AS total, "
                            + "(CASE WHEN fac_rems.cancelado=FALSE THEN fac_rems.total*tipo_cambio ELSE 0.0 END) AS total_mn,  "
                            + "(CASE WHEN fac_rems.moneda_id=1 THEN 1 ELSE fac_rems.tipo_cambio END) AS tipo_cambio  "
                    + "FROM fac_rems   "
                    + "JOIN erp_proceso ON erp_proceso.id=fac_rems.proceso_id  "
                    + "JOIN cxc_clie ON cxc_clie.id = fac_rems.cxc_clie_id   "
                    + "JOIN gral_mon ON gral_mon.id = fac_rems.moneda_id  "
                    + "WHERE erp_proceso.empresa_id ="+id_empresa + "  "
                    + "AND fac_rems.folio ILIKE '"+remision+"' " 
                    + "AND cxc_clie.razon_social ILIKE '"+cliente+"'  "+where+" "
                    + "AND (to_char(fac_rems.momento_creacion,'yyyymmdd') BETWEEN  to_char('"+fecha_inicial+"'::timestamp with time zone,'yyyymmdd') AND to_char('"+fecha_final+"'::timestamp with time zone,'yyyymmdd')) "
                    + "ORDER BY fac_rems.id;";
            
        //System.out.println("getDatosReporteRemision:: "+sql_to_query);
        ArrayList<HashMap<String, String>> hm_remisiones = (ArrayList<HashMap<String, String>>) this.jdbcTemplate.query(
            sql_to_query,
            new Object[]{}, new RowMapper(){
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    HashMap<String, String> row = new HashMap<String, String>();
                    row.put("remision",rs.getString("folio"));
                    row.put("orden_compra",rs.getString("orden_compra"));
                    row.put("fecha_remision",rs.getString("fecha_remision"));
                    row.put("cliente",rs.getString("cliente"));
                    row.put("moneda_id",String.valueOf(rs.getInt("moneda_id")));
                    row.put("moneda_simbolo",rs.getString("moneda_simbolo"));
                    row.put("moneda_remision",rs.getString("moneda_remision"));
                    row.put("subtotal",StringHelper.roundDouble(rs.getDouble("subtotal"), 2));
                    row.put("subtotal_mn",StringHelper.roundDouble(rs.getDouble("subtotal_mn"), 2));
                    row.put("impuesto",StringHelper.roundDouble(rs.getDouble("impuesto"), 2));
                    row.put("impuesto_mn",StringHelper.roundDouble(rs.getDouble("impuesto_mn"), 2));
                    row.put("total",StringHelper.roundDouble(rs.getDouble("total"), 2));
                    row.put("total_mn",StringHelper.roundDouble(rs.getDouble("total_mn"), 2));
                    row.put("monto_ieps",StringHelper.roundDouble(rs.getDouble("monto_ieps"), 2));
                    row.put("monto_ieps_mn",StringHelper.roundDouble(rs.getDouble("monto_ieps_mn"), 2));
                    return row;
                }
            }
        );
        return hm_remisiones;
    }    
    
    
    
    //obtiene datos para el Reporte de Remisiones faturadas
    @Override
    public ArrayList<HashMap<String, String>> getDatosReporteRemision_facturada(Integer opcion, String remision, String cliente, String fecha_inicial, String fecha_final, Integer id_empresa) {
        
        String where="";
        
        //remision facturada
        if (opcion==1){
            where=" AND fac_rems.facturado=TRUE";
        }
        
        //remision no facturada
        if (opcion==2){
            where=" AND fac_rems.facturado=FALSE";
        }
        
        
        String sql_to_query = ""
                + "SELECT  "
                    + "(CASE WHEN tbl_fac.serie_folio IS NULL THEN '' ELSE tbl_fac.serie_folio END ) AS factura,  "
                    + "fac_rems.folio as remision,  "
                    + "to_char (fac_rems.momento_creacion,'dd/mm/yyyy' ) as fecha_remision_facturada,  "
                    + "cxc_clie.razon_social as cliente, (CASE WHEN fac_rems.cancelado=FALSE THEN fac_rems.subtotal ELSE 0.0 END) AS monto, "
                    + "(CASE WHEN fac_rems.cancelado=FALSE THEN fac_rems.subtotal*fac_rems.tipo_cambio ELSE 0.0 END) AS monto_mn, "
                    + "(CASE WHEN fac_rems.cancelado=FALSE THEN fac_rems.monto_ieps ELSE 0.0 END) AS monto_ieps, "
                    + "(CASE WHEN fac_rems.cancelado=FALSE THEN fac_rems.monto_ieps*tipo_cambio ELSE 0.0 END) AS monto_ieps_mn, "
                    + "(CASE WHEN fac_rems.cancelado=FALSE THEN fac_rems.impuesto ELSE 0.0 END) AS iva, "
                    + "(CASE WHEN fac_rems.cancelado=FALSE THEN fac_rems.impuesto*fac_rems.tipo_cambio ELSE 0.0 END) AS impuesto_mn, "
                    + "(CASE WHEN fac_rems.cancelado=FALSE THEN fac_rems.total ELSE 0.0 END) AS total, "
                    + "(CASE WHEN fac_rems.cancelado=FALSE THEN fac_rems.total*fac_rems.tipo_cambio ELSE 0.0 END) AS total_mn,   "
                    + "(CASE WHEN fac_rems.moneda_id=1 THEN 'M.N.' else 'USD' end) as moneda_remision_facturada, "
                    + "(CASE WHEN fac_rems.cancelado=FALSE THEN fac_rems.impuesto ELSE 0.0 END) AS impuesto,  "
                    + "fac_rems.moneda_id, "
                    + "gral_mon.descripcion_abr AS moneda_abr, "
                    + "gral_mon.simbolo AS moneda_simbolo "
                + "FROM fac_rems   "
                + "LEFT JOIN erp_proceso ON erp_proceso.id=fac_rems.proceso_id   "
                + "LEFT JOIN cxc_clie ON cxc_clie.id=fac_rems.cxc_clie_id   "
                + "LEFT JOIN ( SELECT fac_rems_docs.fac_rem_id,fac_docs.serie_folio  FROM fac_docs LEFT JOIN fac_rems_docs  ON fac_rems_docs.erp_proceso_id=fac_docs.proceso_id LEFT JOIN erp_proceso ON erp_proceso.id=fac_rems_docs.erp_proceso_id WHERE erp_proceso.empresa_id="+id_empresa+" ) AS tbl_fac ON tbl_fac.fac_rem_id=fac_rems.id  "
                + "LEFT JOIN gral_mon ON gral_mon.id=fac_rems.moneda_id "
                +" WHERE erp_proceso.empresa_id ="+id_empresa+"  "
                + "AND fac_rems.folio ILIKE '"+remision+"'  "
                + "AND cxc_clie.razon_social ILIKE '"+cliente+"' "+where+" "
                + "AND (to_char(fac_rems.momento_creacion,'yyyymmdd')::integer BETWEEN to_char('"+fecha_inicial+"'::timestamp with time zone,'yyyymmdd')::integer  AND to_char('"+fecha_final+"'::timestamp with time zone,'yyyymmdd')::integer ) "
                + "ORDER BY fac_rems.id; ";
        
        //System.out.println("getDatosReporteRemision_facturada:: "+sql_to_query);
        ArrayList<HashMap<String, String>> hm_remisiones = (ArrayList<HashMap<String, String>>) this.jdbcTemplate.query(
            sql_to_query,
            new Object[]{}, new RowMapper(){
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    HashMap<String, String> row = new HashMap<String, String>();
                    row.put("factura",rs.getString("factura"));
                    row.put("remision",rs.getString("remision"));
                    row.put("fecha_remision_facturada",rs.getString("fecha_remision_facturada"));
                    row.put("cliente",rs.getString("cliente"));
                    row.put("moneda_remision_facturada",rs.getString("moneda_remision_facturada"));
                    row.put("monto",StringHelper.roundDouble(rs.getDouble("monto"), 2));
                    row.put("monto_mn",StringHelper.roundDouble(rs.getDouble("monto_mn"), 2));
                    row.put("impuesto",StringHelper.roundDouble(rs.getDouble("iva"), 2));
                    row.put("impuesto_mn",StringHelper.roundDouble(rs.getDouble("impuesto_mn"), 2));
                    row.put("total",StringHelper.roundDouble(rs.getDouble("total"), 2));
                    row.put("total_mn",StringHelper.roundDouble(rs.getDouble("total_mn"), 2));
                    row.put("moneda_id",String.valueOf(rs.getInt("moneda_id")));
                    row.put("moneda_abr",rs.getString("moneda_abr"));
                    row.put("moneda_simbolo",rs.getString("moneda_simbolo"));
                    row.put("monto_ieps",StringHelper.roundDouble(rs.getDouble("monto_ieps"), 2));
                    row.put("monto_ieps_mn",StringHelper.roundDouble(rs.getDouble("monto_ieps_mn"), 2));
                    return row;
                }
            }
        );
        return hm_remisiones;
    }
    
    
    
    
    
    
    @Override
    public ArrayList<HashMap<String, Object>> getNotasCredito_PaginaGrid(String data_string, int offset, int pageSize, String orderBy, String asc) {
        
        String sql_busqueda = "select id from gral_bus_catalogos(?) as foo (id integer)";
        
	String sql_to_query = ""
                + "SELECT DISTINCT "
                    + "fac_nota_credito.id, "
                    + "fac_nota_credito.serie_folio,"
                    + "cxc_clie.razon_social as cliente,"
                    + "fac_nota_credito.total, "
                    + "to_char(fac_nota_credito.momento_creacion,'dd/mm/yyyy') AS fecha_expedicion,"
                    + "gral_mon.descripcion_abr AS moneda, "
                    + "fac_nota_credito.serie_folio_factura AS factura, "
                    + "(CASE WHEN fac_nota_credito.cancelado=FALSE THEN '' ELSE 'CANCELADO' END) AS estado "
                + "FROM fac_nota_credito "
                + "LEFT JOIN cxc_clie on cxc_clie.id = fac_nota_credito.cxc_clie_id  "
                + "LEFT JOIN gral_mon ON gral_mon.id=fac_nota_credito.moneda_id  "
                +"JOIN ("+sql_busqueda+") as subt on subt.id=fac_nota_credito.id "
                +"order by "+orderBy+" "+asc+" limit ? OFFSET ?";
        
        //System.out.println("Busqueda GetPage: "+sql_to_query);
        //System.out.println("data_string: "+data_string);
        //System.out.println("cliente: "+cliente+ "fecha_inicial:"+fecha_inicial+" fecha_final: "+fecha_final+ " offset:"+offset+ " pageSize: "+pageSize+" orderBy:"+orderBy+" asc:"+asc);
        //log.log(Level.INFO, "Ejecutando query de {0}", sql_to_query);
        ArrayList<HashMap<String, Object>> hm = (ArrayList<HashMap<String, Object>>) this.jdbcTemplate.query(
            sql_to_query,
            new Object[]{data_string,new Integer(pageSize),new Integer(offset)}, new RowMapper() {
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    HashMap<String, Object> row = new HashMap<String, Object>();
                    row.put("id",rs.getInt("id"));
                    row.put("folio",rs.getString("serie_folio"));
                    row.put("cliente",rs.getString("cliente"));
                    row.put("total",StringHelper.AgregaComas(StringHelper.roundDouble(rs.getString("total"),2)));
                    row.put("fecha_expedicion",rs.getString("fecha_expedicion"));
                    row.put("factura",rs.getString("factura"));
                    row.put("moneda",rs.getString("moneda"));
                    row.put("estado",rs.getString("estado"));
                    
                    return row;
                }
            }
        );
        return hm;
    }
    
    
    
    
    //Obtiene los datos de la Nota de Credito
    @Override
    public ArrayList<HashMap<String, Object>> getNotasCredito_Datos(Integer id_nota_credito) {
	String sql_query = ""
        + "SELECT "
            + "fac_nota_credito.id,"
            + "fac_nota_credito.serie_folio,"
            + "(case when fac_nota_credito.momento_expedicion is null then '' else to_char(fac_nota_credito.momento_expedicion,'dd/mm/yyyy') end) AS fecha_exp,"
            + "fac_nota_credito.ctb_tmov_id as tmov_id,"
            + "fac_nota_credito.ctb_tmov_id_cancelacion as tmov_id_cancel,"
            + "fac_nota_credito.cxc_clie_id,"
            + "cxc_clie.numero_control AS no_cliente,"
            + "cxc_clie.razon_social,"
            + "fac_nota_credito.cxc_clie_df_id,"
            + "cxc_clie.empresa_immex,"
            + "fac_nota_credito.cxc_agen_id,"
            + "fac_nota_credito.moneda_id,"
            + "fac_nota_credito.valor_impuesto,"
            + "fac_nota_credito.tasa_retencion_immex,"
            + "fac_nota_credito.tipo_cambio,"
            + "fac_nota_credito.subtotal AS importe,"
            + "fac_nota_credito.impuesto AS importe_iva,"
            + "fac_nota_credito.monto_retencion AS importe_retencion,"
            + "fac_nota_credito.total AS monto_total,"
            + "fac_nota_credito.concepto,"
            + "fac_nota_credito.observaciones,"
            + "fac_nota_credito.serie_folio_factura as factura,"
            + "erp_h_facturas.moneda_id as id_moneda_factura, "
            + "fac_nota_credito.cancelado,"
            + "fac_nota_credito.motivo_cancelacion as motivo_cancel,"
            + "erp_h_facturas.monto_total AS monto_factura, "
            + "erp_h_facturas.saldo_factura, "
            + "to_char(erp_h_facturas.momento_facturacion,'dd/mm/yyyy') AS fecha_factura,"
            + "fac_nota_credito.monto_ieps "
        + "FROM fac_nota_credito "
        + "JOIN cxc_clie ON cxc_clie.id = fac_nota_credito.cxc_clie_id "
        + "JOIN erp_h_facturas ON erp_h_facturas.serie_folio = fac_nota_credito.serie_folio_factura "
        + "WHERE fac_nota_credito.id=?;";
        
        ArrayList<HashMap<String, Object>> hm = (ArrayList<HashMap<String, Object>>) this.jdbcTemplate.query(
            sql_query,  
            new Object[]{new Integer(id_nota_credito)}, new RowMapper() {
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    HashMap<String, Object> row = new HashMap<String, Object>();
                    row.put("id",String.valueOf(rs.getInt("id")));
                    row.put("serie_folio",rs.getString("serie_folio"));
                    row.put("fecha_exp",rs.getString("fecha_exp"));
                    row.put("tmov_id",rs.getInt("tmov_id"));
                    row.put("tmov_id_cancel",rs.getInt("tmov_id_cancel"));
                    row.put("cancelado",rs.getBoolean("cancelado"));
                    row.put("motivo_cancel",rs.getString("motivo_cancel"));
                    row.put("cxc_clie_id",rs.getInt("cxc_clie_id"));
                    row.put("cxc_agen_id",String.valueOf(rs.getInt("cxc_agen_id")));
                    row.put("moneda_id",rs.getInt("moneda_id"));
                    row.put("no_cliente",rs.getString("no_cliente"));
                    row.put("razon_social",rs.getString("razon_social"));
                    row.put("df_id",String.valueOf(rs.getInt("cxc_clie_df_id")));
                    row.put("concepto",rs.getString("concepto"));
                    row.put("observaciones",rs.getString("observaciones"));
                    row.put("empresa_immex",String.valueOf(rs.getBoolean("empresa_immex")));
                    row.put("valor_impuesto",StringHelper.roundDouble(rs.getDouble("valor_impuesto"), 2));
                    row.put("tasa_retencion_immex",StringHelper.roundDouble(rs.getDouble("tasa_retencion_immex"), 2));
                    row.put("tipo_cambio",StringHelper.roundDouble(rs.getDouble("tipo_cambio"), 4));
                    row.put("importe",StringHelper.roundDouble(rs.getDouble("importe"), 2));
                    row.put("monto_ieps",StringHelper.roundDouble(rs.getDouble("monto_ieps"), 2));
                    row.put("importe_iva",StringHelper.roundDouble(rs.getDouble("importe_iva"), 2));
                    row.put("importe_retencion",StringHelper.roundDouble(rs.getDouble("importe_retencion"), 2));
                    row.put("monto_total",StringHelper.roundDouble(rs.getDouble("monto_total"), 2));
                    row.put("factura",rs.getString("factura"));
                    row.put("id_moneda_factura",String.valueOf(rs.getInt("id_moneda_factura")));
                    row.put("monto_factura",StringHelper.roundDouble(rs.getDouble("monto_factura"), 2));
                    row.put("saldo_factura",StringHelper.roundDouble(rs.getDouble("saldo_factura"), 2));
                    row.put("fecha_factura",rs.getString("fecha_factura"));
                    
                    return row;
                }
            }
        );
        return hm;
    }
    
    
    
    
    
    //buscador de clientes, se utiliza en notas de credito
    //buscador de clientes de una sucursal
    @Override
    public ArrayList<HashMap<String, String>> getBuscadorClientes(String cadena, Integer filtro,Integer id_empresa, Integer id_sucursal) {
        String where="";
	if(filtro == 1){
            where=" AND cxc_clie.numero_control ilike '%"+cadena.toUpperCase()+"%'";
	}
        
	if(filtro == 2){
            where=" AND cxc_clie.rfc ilike '%"+cadena.toUpperCase()+"%'";
	}
        
	if(filtro == 3){
            where=" AND cxc_clie.razon_social ilike '%"+cadena.toUpperCase()+"%'";
	}
        
	if(filtro == 4){
            where=" AND cxc_clie.curp ilike '%"+cadena.toUpperCase()+"%'";
	}
        
	if(filtro == 5){
            where=" AND cxc_clie.alias ilike '%"+cadena.toUpperCase()+"%'";
	}
	
	String sql_query = "SELECT "
                                    +"sbt.id,"
                                    +"sbt.numero_control,"
                                    +"sbt.rfc,"
                                    +"sbt.razon_social,"
                                    +"sbt.direccion,"
                                    +"sbt.moneda_id,"
                                    +"gral_mon.descripcion as moneda, "
                                    +"sbt.cxc_agen_id,"
                                    +"sbt.terminos_id, "
                                    +"sbt.empresa_immex, "
                                    +"sbt.tasa_ret_immex, "
                                    +"sbt.cta_pago_mn, "
                                    +"sbt.cta_pago_usd "
                            +"FROM(SELECT cxc_clie.id,"
                                            +"cxc_clie.numero_control,"
                                            +"cxc_clie.rfc, "
                                            +"cxc_clie.razon_social,"
                                            +"cxc_clie.calle||' '||cxc_clie.numero||', '||cxc_clie.colonia||', '||gral_mun.titulo||', '||gral_edo.titulo||', '||gral_pais.titulo||' C.P. '||cxc_clie.cp as direccion, "
                                            +"cxc_clie.moneda as moneda_id, "
                                            +"cxc_clie.cxc_agen_id, "
                                            +"cxc_clie.dias_credito_id AS terminos_id, "
                                            +"cxc_clie.empresa_immex, "
                                            +"(CASE WHEN cxc_clie.tasa_ret_immex IS NULL THEN 0 ELSE cxc_clie.tasa_ret_immex/100 END) AS tasa_ret_immex, "
                                            + "cxc_clie.cta_pago_mn,"
                                            + "cxc_clie.cta_pago_usd  "
                                    +"FROM cxc_clie "
                                    + "JOIN gral_pais ON gral_pais.id = cxc_clie.pais_id "
                                    + "JOIN gral_edo ON gral_edo.id = cxc_clie.estado_id "
                                    + "JOIN gral_mun ON gral_mun.id = cxc_clie.municipio_id "
                                    +" WHERE empresa_id ="+id_empresa+"  AND sucursal_id="+id_sucursal
                                    + " AND cxc_clie.borrado_logico=false  "+where+" "
                            +") AS sbt "
                            +"LEFT JOIN gral_mon on gral_mon.id = sbt.moneda_id;";
        
        ArrayList<HashMap<String, String>> hm_cli = (ArrayList<HashMap<String, String>>) this.jdbcTemplate.query(
            sql_query,  
            new Object[]{}, new RowMapper() {
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    HashMap<String, String> row = new HashMap<String, String>();
                    row.put("id",String.valueOf(rs.getInt("id")));
                    row.put("numero_control",rs.getString("numero_control"));
                    row.put("rfc",rs.getString("rfc"));
                    row.put("razon_social",rs.getString("razon_social"));
                    row.put("direccion",rs.getString("direccion"));
                    row.put("moneda_id",rs.getString("moneda_id"));
                    row.put("moneda",rs.getString("moneda"));
                    row.put("cxc_agen_id",rs.getString("cxc_agen_id"));
                    row.put("terminos_id",rs.getString("terminos_id"));
                    row.put("empresa_immex",String.valueOf(rs.getBoolean("empresa_immex")));
                    row.put("tasa_ret_immex",StringHelper.roundDouble(String.valueOf(rs.getDouble("tasa_ret_immex")),2));
                    row.put("cta_pago_mn",rs.getString("cta_pago_mn"));
                    row.put("cta_pago_usd",rs.getString("cta_pago_usd"));
                    
                    return row;
                }
            }
        );
        return hm_cli;
    }
    
    
    //obtener datos del cliente a partir del Numero de Control
    @Override
    public ArrayList<HashMap<String, String>> getDatosClienteByNoCliente(String no_control, Integer id_empresa, Integer id_sucursal) {
        
        String sql_query = ""
                + "SELECT "
                        +"sbt.id,"
                        +"sbt.numero_control,"
                        +"sbt.rfc,"
                        +"sbt.razon_social,"
                        +"sbt.direccion,"
                        +"sbt.moneda_id,"
                        +"gral_mon.descripcion as moneda, "
                        +"sbt.cxc_agen_id,"
                        +"sbt.terminos_id, "
                        +"sbt.empresa_immex, "
                        +"sbt.tasa_ret_immex, "
                        +"sbt.cta_pago_mn, "
                        +"sbt.cta_pago_usd "
                +"FROM("
                    + "SELECT cxc_clie.id,"
                        +"cxc_clie.numero_control,"
                        +"cxc_clie.rfc, "
                        +"cxc_clie.razon_social,"
                        +"cxc_clie.calle||' '||cxc_clie.numero||', '||cxc_clie.colonia||', '||gral_mun.titulo||', '||gral_edo.titulo||', '||gral_pais.titulo||' C.P. '||cxc_clie.cp as direccion, "
                        +"cxc_clie.moneda as moneda_id, "
                        +"cxc_clie.cxc_agen_id, "
                        +"cxc_clie.dias_credito_id AS terminos_id, "
                        +"cxc_clie.empresa_immex, "
                        +"(CASE WHEN cxc_clie.tasa_ret_immex IS NULL THEN 0 ELSE cxc_clie.tasa_ret_immex/100 END) AS tasa_ret_immex, "
                        + "cxc_clie.cta_pago_mn,"
                        + "cxc_clie.cta_pago_usd  "
                    +"FROM cxc_clie "
                    + "JOIN gral_pais ON gral_pais.id = cxc_clie.pais_id "
                    + "JOIN gral_edo ON gral_edo.id = cxc_clie.estado_id "
                    + "JOIN gral_mun ON gral_mun.id = cxc_clie.municipio_id "
                    +" WHERE empresa_id ="+id_empresa+"  AND sucursal_id="+id_sucursal+" "
                    + "AND cxc_clie.borrado_logico=false   "
                    + "AND cxc_clie.numero_control='"+no_control.toUpperCase()+"'"
                +") AS sbt "
                +"LEFT JOIN gral_mon ON gral_mon.id=sbt.moneda_id LIMIT 1;";

        //System.out.println("getDatosCliente: "+sql_query);
        
        ArrayList<HashMap<String, String>> hm_cli = (ArrayList<HashMap<String, String>>) this.jdbcTemplate.query(
            sql_query,
            new Object[]{}, new RowMapper() {
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    HashMap<String, String> row = new HashMap<String, String>();
                    row.put("id",String.valueOf(rs.getInt("id")));
                    row.put("numero_control",rs.getString("numero_control"));
                    row.put("rfc",rs.getString("rfc"));
                    row.put("razon_social",rs.getString("razon_social"));
                    row.put("direccion",rs.getString("direccion"));
                    row.put("moneda_id",rs.getString("moneda_id"));
                    row.put("moneda",rs.getString("moneda"));
                    row.put("cxc_agen_id",rs.getString("cxc_agen_id"));
                    row.put("terminos_id",rs.getString("terminos_id"));
                    row.put("empresa_immex",String.valueOf(rs.getBoolean("empresa_immex")));
                    row.put("tasa_ret_immex",StringHelper.roundDouble(String.valueOf(rs.getDouble("tasa_ret_immex")),2));
                    row.put("cta_pago_mn",rs.getString("cta_pago_mn"));
                    row.put("cta_pago_usd",rs.getString("cta_pago_usd"));
                    
                    return row;
                }
            }
        );
        return hm_cli;
    }
    
    
    //obtiene las facturas del cliente con saldo pendiente de pago para Notas de Credito
    @Override
    public ArrayList<HashMap<String, String>> getNotasCredito_FacturasCliente(Integer id_cliente, String serie_folio) {
	String sql_query = ""
                + "SELECT "
                    + "erp_h_facturas.serie_folio AS factura, "
                    + "erp_h_facturas.monto_total AS monto_factura, "
                    + "erp_h_facturas.saldo_factura, "
                    + "erp_h_facturas.moneda_id, "
                    + "gral_mon.descripcion_abr AS moneda, "
                    + "erp_h_facturas.cxc_agen_id,"
                    + "to_char(erp_h_facturas.momento_facturacion,'dd/mm/yyyy') AS fecha_factura "
                + "FROM erp_h_facturas "
                + "JOIN gral_mon ON gral_mon.id=erp_h_facturas.moneda_id "
                + "WHERE erp_h_facturas.cliente_id="+id_cliente+" "
                + "AND erp_h_facturas.pagado=FALSE "
                + "AND erp_h_facturas.cancelacion=FALSE "
                + "AND erp_h_facturas.serie_folio ILIKE '%"+serie_folio.toUpperCase()+"%';";
        
        ArrayList<HashMap<String, String>> hm = (ArrayList<HashMap<String, String>>) this.jdbcTemplate.query(
            sql_query,  
            new Object[]{}, new RowMapper() {
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    HashMap<String, String> row = new HashMap<String, String>();
                    row.put("factura",rs.getString("factura"));
                    row.put("monto_factura",StringHelper.roundDouble(rs.getDouble("monto_factura"), 2));
                    row.put("saldo_factura",StringHelper.roundDouble(rs.getDouble("saldo_factura"), 2));
                    row.put("moneda_id",String.valueOf(rs.getInt("moneda_id")));
                    row.put("moneda",rs.getString("moneda"));
                    row.put("cxc_agen_id",String.valueOf(rs.getInt("cxc_agen_id")));
                    row.put("fecha_factura",rs.getString("fecha_factura"));
                    return row;
                }
            }
        );
        return hm;
    }
    
    
    //obtiene datos de una Factura en Especifico a partir del Serie y Folio
    //Dicha factura debe terner saldo pendiente de pago para Notas de Credito
    @Override
    public ArrayList<HashMap<String, String>> getNotasCredito_DatosFactura(Integer id_cliente, String serie_folio) {
	String sql_query = ""
                + "SELECT "
                    + "erp_h_facturas.serie_folio AS factura, "
                    + "erp_h_facturas.monto_total AS monto_factura, "
                    + "erp_h_facturas.saldo_factura, "
                    + "erp_h_facturas.moneda_id, "
                    + "gral_mon.descripcion_abr AS moneda, "
                    + "erp_h_facturas.cxc_agen_id,"
                    + "to_char(erp_h_facturas.momento_facturacion,'dd/mm/yyyy') AS fecha_factura "
                + "FROM erp_h_facturas "
                + "JOIN gral_mon ON gral_mon.id=erp_h_facturas.moneda_id "
                + "WHERE erp_h_facturas.cliente_id="+id_cliente+" "
                + "AND erp_h_facturas.serie_folio='"+serie_folio.toUpperCase()+"' "
                + "AND erp_h_facturas.pagado=FALSE "
                + "AND erp_h_facturas.cancelacion=FALSE LIMIT 1;";
        
        ArrayList<HashMap<String, String>> hm = (ArrayList<HashMap<String, String>>) this.jdbcTemplate.query(
            sql_query,  
            new Object[]{}, new RowMapper() {
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    HashMap<String, String> row = new HashMap<String, String>();
                    row.put("factura",rs.getString("factura"));
                    row.put("monto_factura",StringHelper.roundDouble(rs.getDouble("monto_factura"), 2));
                    row.put("saldo_factura",StringHelper.roundDouble(rs.getDouble("saldo_factura"), 2));
                    row.put("moneda_id",String.valueOf(rs.getInt("moneda_id")));
                    row.put("moneda",rs.getString("moneda"));
                    row.put("cxc_agen_id",String.valueOf(rs.getInt("cxc_agen_id")));
                    row.put("fecha_factura",rs.getString("fecha_factura"));
                    return row;
                }
            }
        );
        return hm;
    }
    
    
    
    
    //obtiene la lista de conceptos de la nota de credito
    @Override
    public ArrayList<LinkedHashMap<String, String>> getNotaCreditoCfd_ListaConceptosXml(Integer id_nota_credito) {
        String sql_query = ""
                + "SELECT "
                    + "'1'::character varying AS no_identificacion,"
                    + "1::double precision AS cantidad,"
                    + "concepto AS descripcion, "
                    + "'No aplica'::character varying AS unidad, "
                    + "(CASE WHEN moneda_id=1 THEN subtotal ELSE subtotal*tipo_cambio END) AS valor_unitario, "
                    + "(CASE WHEN moneda_id=1 THEN subtotal ELSE subtotal*tipo_cambio END) AS importe, "
                    + "(CASE WHEN moneda_id=1 THEN impuesto ELSE impuesto*tipo_cambio END) AS importe_impuesto, "
                    + "tasa_retencion_immex AS tasa_retencion, "
                    + "''::character varying AS numero_aduana, "
                    + "''::character varying AS fecha_aduana, "
                    + "''::character varying AS aduana_aduana "
                    + "FROM fac_nota_credito "
                + "WHERE id="+id_nota_credito;
                
        //System.out.println(sql_query);
        //System.out.println("Obteniendo lista de conceptos: "+sql_query);
        
        ArrayList<LinkedHashMap<String, String>> hm_conceptos = (ArrayList<LinkedHashMap<String, String>>) this.jdbcTemplate.query(
            sql_query,
            new Object[]{}, new RowMapper() {
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    LinkedHashMap<String, String> row = new LinkedHashMap<String, String>();
                    row.put("noIdentificacion",StringHelper.normalizaString(StringHelper.remueve_tildes(rs.getString("no_identificacion"))));
                    row.put("cantidad",rs.getString("cantidad"));
                    row.put("descripcion",StringHelper.normalizaString(StringHelper.remueve_tildes(rs.getString("descripcion"))));
                    row.put("unidad",StringHelper.normalizaString(StringHelper.remueve_tildes(rs.getString("unidad"))));
                    row.put("valorUnitario",StringHelper.roundDouble(rs.getDouble("valor_unitario"),2) );
                    row.put("importe",StringHelper.roundDouble(rs.getDouble("importe"),2) );
                    row.put("importe_impuesto",StringHelper.roundDouble(rs.getDouble("importe_impuesto"),2) );
                    row.put("tasa_retencion",StringHelper.roundDouble(rs.getDouble("tasa_retencion"),2) );
                    row.put("numero_aduana","");
                    row.put("fecha_aduana","");
                    row.put("aduana_aduana","");
                    
                    row.put("descto","0" );
                    row.put("precio_unitario_con_descto", "0");
                    row.put("importe_del_descto", "0");
                    row.put("importe_con_descto", "0");
                    return row;
                }
            }
        );
        
        try {
            calcula_Totales_e_Impuestos(hm_conceptos);
        } catch (SQLException ex) {
            Logger.getLogger(FacturasSpringDao.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return hm_conceptos;
    }
    
    
    
    
    
    
    @Override
    public ArrayList<LinkedHashMap<String, String>> getNotaCreditoCfd_CfdiTf_ImpuestosRetenidosXml() {
        ArrayList<LinkedHashMap<String, String>>  impuestos = new ArrayList<LinkedHashMap<String, String>>();
        LinkedHashMap<String,String> impuesto = new LinkedHashMap<String,String>();
        impuesto.put("impuesto", "IVA");
        impuesto.put("importe", this.getImpuestoRetenido());
        impuesto.put("tasa", this.getTasaRetencion());
        impuestos.add(impuesto);
        
        return impuestos;
    }
    
    
    @Override
    public ArrayList<LinkedHashMap<String, String>> getNotaCreditoCfd_CfdiTf_ImpuestosTrasladadosXml(Integer id_sucursal) {
        ArrayList<LinkedHashMap<String, String>>  impuestos = new ArrayList<LinkedHashMap<String, String>>();
        LinkedHashMap<String,String> impuesto = new LinkedHashMap<String,String>();
        impuesto.put("impuesto", "IVA");
        impuesto.put("importe", this.getImpuestoTrasladado());
        impuesto.put("tasa", this.getTasaIva(id_sucursal));
        impuestos.add(impuesto);
        
        return impuestos;
    }
    
    
    
    
    
    //este metodo se utiliza para Nota de Credito CFD y CFDI
    @Override
    public HashMap<String, String> getNotaCreditoCfd_Cfdi_Datos(Integer id_nota_credito) {
        HashMap<String, String> data = new HashMap<String, String>();
        
        //obtener id del cliente
        String sql_to_query = ""
                + "SELECT "
                    + "fac_nota_credito.cxc_clie_id, "
                    + "'99'::character varying AS metodo_pago, "
                    //+ "'OTROS'::character varying AS metodo_pago_titulo, "
                    + "'99'::character varying AS metodo_pago_titulo, "
                    + "''::character varying AS no_cuenta, "
                    + "'No aplica'::character varying AS condicion_pago, "
                    + "fac_nota_credito.subtotal, "
                    + "fac_nota_credito.serie_folio_factura, "
                    + "gral_mon.descripcion_abr AS moneda_abr, "
                    + "gral_mon.simbolo AS simbolo_moneda,"
                    + "gral_mon.iso_4217 AS moneda_iso,"
                    + "gral_mon.iso_4217_anterior AS moneda2, "
                    + "gral_mon.descripcion AS moneda_titulo,"
                    + "cxc_clie.numero_control, "
                    + "cxc_clie.razon_social, "
                    + "cxc_clie.rfc, "
                    + "cxc_clie.cxc_clie_tipo_adenda_id AS adenda_id, "
                    + "(CASE WHEN cxc_clie.localidad_alternativa IS NULL THEN '' ELSE cxc_clie.localidad_alternativa END) AS localidad_alternativa, "
                    + "(CASE WHEN fac_nota_credito.cxc_clie_df_id > 1 THEN sbtdf.calle ELSE cxc_clie.calle END ) AS calle,"
                    + "(CASE WHEN fac_nota_credito.cxc_clie_df_id > 1 THEN sbtdf.numero_interior ELSE (CASE WHEN cxc_clie.numero='' OR cxc_clie.numero IS NULL THEN cxc_clie.numero_exterior ELSE cxc_clie.numero END)  END ) AS numero_interior,"
                    + "(CASE WHEN fac_nota_credito.cxc_clie_df_id > 1 THEN sbtdf.numero_exterior ELSE (CASE WHEN cxc_clie.numero_exterior='' OR cxc_clie.numero_exterior IS NULL THEN cxc_clie.numero ELSE cxc_clie.numero_exterior END) END ) AS numero_exterior,"
                    + "(CASE WHEN fac_nota_credito.cxc_clie_df_id > 1 THEN sbtdf.colonia ELSE cxc_clie.colonia END ) AS colonia,"
                    + "(CASE WHEN fac_nota_credito.cxc_clie_df_id > 1 THEN sbtdf.municipio ELSE gral_mun.titulo END ) AS municipio,"
                    + "(CASE WHEN fac_nota_credito.cxc_clie_df_id > 1 THEN sbtdf.estado ELSE gral_edo.titulo END ) AS estado,"
                    + "(CASE WHEN fac_nota_credito.cxc_clie_df_id > 1 THEN sbtdf.pais ELSE gral_pais.titulo END ) AS pais,"
                    + "(CASE WHEN fac_nota_credito.cxc_clie_df_id > 1 THEN sbtdf.cp ELSE cxc_clie.cp END ) AS cp "
                + "FROM fac_nota_credito  "
                + "LEFT JOIN cxc_clie ON cxc_clie.id=fac_nota_credito.cxc_clie_id "
                + "JOIN gral_pais ON gral_pais.id = cxc_clie.pais_id "
                + "JOIN gral_edo ON gral_edo.id = cxc_clie.estado_id "
                + "JOIN gral_mun ON gral_mun.id = cxc_clie.municipio_id "
                + "JOIN gral_mon ON gral_mon.id=fac_nota_credito.moneda_id "
                + "LEFT JOIN (SELECT cxc_clie_df.id, (CASE WHEN cxc_clie_df.calle IS NULL THEN '' ELSE cxc_clie_df.calle END) AS calle, (CASE WHEN cxc_clie_df.numero_interior IS NULL THEN '' ELSE (CASE WHEN cxc_clie_df.numero_interior IS NULL OR cxc_clie_df.numero_interior='' THEN '' ELSE 'NO.INT.'||cxc_clie_df.numero_interior END)  END) AS numero_interior, (CASE WHEN cxc_clie_df.numero_exterior IS NULL THEN '' ELSE (CASE WHEN cxc_clie_df.numero_exterior IS NULL OR cxc_clie_df.numero_exterior='' THEN '' ELSE 'NO.EXT.'||cxc_clie_df.numero_exterior END )  END) AS numero_exterior, (CASE WHEN cxc_clie_df.colonia IS NULL THEN '' ELSE cxc_clie_df.colonia END) AS colonia,(CASE WHEN gral_mun.id IS NULL OR gral_mun.id=0 THEN '' ELSE gral_mun.titulo END) AS municipio,(CASE WHEN gral_edo.id IS NULL OR gral_edo.id=0 THEN '' ELSE gral_edo.titulo END) AS estado,(CASE WHEN gral_pais.id IS NULL OR gral_pais.id=0 THEN '' ELSE gral_pais.titulo END) AS pais,(CASE WHEN cxc_clie_df.cp IS NULL THEN '' ELSE cxc_clie_df.cp END) AS cp  FROM cxc_clie_df LEFT JOIN gral_pais ON gral_pais.id = cxc_clie_df.gral_pais_id LEFT JOIN gral_edo ON gral_edo.id = cxc_clie_df.gral_edo_id LEFT JOIN gral_mun ON gral_mun.id = cxc_clie_df.gral_mun_id ) AS sbtdf ON sbtdf.id = fac_nota_credito.cxc_clie_df_id "
                + "WHERE fac_nota_credito.id="+id_nota_credito;
        
        //System.out.println("sql_to_query"+sql_to_query);
        
        Map<String, Object> map = this.getJdbcTemplate().queryForMap(sql_to_query);
        
        //int id_cliente = Integer.parseInt(map.get("cxc_clie_id").toString());
        
        String fecha = TimeHelper.getFechaActualYMDH();
        String[] fecha_hora = fecha.split(" ");
        //formato fecha: 2011-03-01T00:00:00
        this.setFechaComprobante(fecha_hora[0]+"T"+fecha_hora[1]);//este solo se utiliza en pdfcfd
        
        data.put("comprobante_attr_fecha",fecha_hora[0]+"T"+fecha_hora[1]);
        data.put("comprobante_attr_condicionesdepago",map.get("condicion_pago").toString().toUpperCase());
        data.put("comprobante_attr_formadepago","PAGO EN UNA SOLA EXIBICION");
        data.put("comprobante_attr_motivodescuento","");
        data.put("comprobante_attr_descuento","0.00");
        data.put("subtotal_con_descuento","0.00");
        data.put("subtotal_sin_descuento","0.00");//Este es para el pdf cuando incluye descuento
        data.put("comprobante_attr_subtotal",map.get("subtotal").toString().toUpperCase());
        data.put("comprobante_attr_total",this.getTotal());
        data.put("comprobante_attr_metododepago",map.get("metodo_pago").toString().toUpperCase());
        data.put("comprobante_attr_metododepagotitulo",map.get("metodo_pago_titulo").toString().toUpperCase());
        data.put("comprobante_attr_simbolo_moneda",map.get("simbolo_moneda").toString().toUpperCase());
        data.put("moneda_abr",map.get("moneda_abr").toString().toUpperCase());
        data.put("nombre_moneda",map.get("moneda_titulo").toString().toUpperCase());
        data.put("comprobante_attr_moneda",map.get("moneda_iso").toString().toUpperCase());        
        
        String no_cta ="";
        if (!map.get("no_cuenta").toString().equals("null") && !map.get("no_cuenta").toString().equals("")){
            no_cta=map.get("no_cuenta").toString();
        }
        data.put("comprobante_attr_numerocuenta",no_cta);
        
        String no_ext="";
        String no_int="";
        
        if(map.get("numero_exterior").toString().equals("'")){
            no_ext="";
        }else{
            no_ext=map.get("numero_exterior").toString();
        }
        
        if(map.get("numero_interior").toString().equals("'")){
            no_int="";
        }else{
            no_int=map.get("numero_interior").toString();
        }
        
        //datos del cliente
        data.put("comprobante_receptor_attr_nombre",map.get("razon_social").toString());
        data.put("comprobante_receptor_attr_rfc",map.get("rfc").toString());
        data.put("comprobante_receptor_domicilio_attr_pais",map.get("pais").toString());
        data.put("comprobante_receptor_domicilio_attr_calle",map.get("calle").toString());
        data.put("comprobante_receptor_domicilio_attr_noexterior",no_ext);
        data.put("comprobante_receptor_domicilio_attr_nointerior",no_int);
        data.put("comprobante_receptor_domicilio_attr_colonia",map.get("colonia").toString());
        data.put("comprobante_receptor_domicilio_attr_localidad",map.get("localidad_alternativa").toString());
        data.put("comprobante_receptor_domicilio_attr_referencia","");
        data.put("comprobante_receptor_domicilio_attr_municipio",map.get("municipio").toString());
        data.put("comprobante_receptor_domicilio_attr_estado",map.get("estado").toString());
        data.put("comprobante_receptor_domicilio_attr_codigopostal",map.get("cp").toString());
        data.put("adenda_id",String.valueOf(map.get("adenda_id")));
        
        //este solo se utiliza en el pdfcfd y cfdi
        data.put("numero_control",map.get("numero_control").toString());
        
        
        
        if(Integer.parseInt(String.valueOf(map.get("adenda_id")))==1){
            //Este campo es utilizado para la adenda de Femsa-Quimiproductos
            data.put("moneda2",map.get("moneda2").toString().toUpperCase());
            Map<String, Object> map2 = this.getJdbcTemplate().queryForMap("select orden_compra from fac_docs where serie_folio='"+String.valueOf(map.get("serie_folio_factura"))+"' AND cxc_clie_id="+String.valueOf(map.get("cxc_clie_id")));
            data.put("orden_compra",String.valueOf(map2.get("orden_compra")).toUpperCase());
        }
        
        return data;
    }
    
    
    
    
    @Override
    public LinkedHashMap<String, String> getNotaCreditoCfd_DatosExtrasXml(Integer id_nota_credito, String tipo_cambio,String id_usuario,String moneda_id, Integer id_empresa, Integer id_sucursal, Integer app_selected, String command_selected, String extra_data_array, String fac_saldado) {
        
        LinkedHashMap<String,String> datosExtras = new LinkedHashMap<String,String>();
        //estos son requeridos para cfd
        datosExtras.put("tipo_cambio", tipo_cambio);
        datosExtras.put("moneda_id", moneda_id);
        datosExtras.put("usuario_id", id_usuario);
        datosExtras.put("empresa_id", String.valueOf(id_empresa));
        datosExtras.put("sucursal_id", String.valueOf(id_sucursal));
        datosExtras.put("id_nota_credito", String.valueOf(id_nota_credito));
        datosExtras.put("app_selected", String.valueOf(app_selected));
        datosExtras.put("command_selected", command_selected);
        datosExtras.put("extra_data_array", extra_data_array);
        datosExtras.put("fac_saldado", fac_saldado);
        return datosExtras;
    }
    
    
    @Override
    public String getSerieFolioNotaCredito(Integer id_nota_credito) {
        String sql_to_query = "SELECT serie_folio FROM fac_nota_credito WHERE id="+id_nota_credito+" LIMIT 1;";
        //System.out.println("GetSerieFolioNotaCredito:"+sql_to_query);
        Map<String, Object> map_iva = this.getJdbcTemplate().queryForMap(sql_to_query);
        String serie_folio = map_iva.get("serie_folio").toString();
        return serie_folio;
    }
    
    @Override
    public String getRefIdNotaCredito(Integer id_nota_credito) {
        String sql_to_query = "SELECT (CASE WHEN ref_id='' THEN serie_folio ELSE ref_id END) AS ref_id FROM fac_nota_credito WHERE id="+id_nota_credito+" LIMIT 1;";
        //System.out.println("GetSerieFolioNotaCredito:"+sql_to_query);
        Map<String, Object> map_iva = this.getJdbcTemplate().queryForMap(sql_to_query);
        String serie_folio = map_iva.get("ref_id").toString();
        return serie_folio;
    }
    
    
    
    //obtiene la lista de conceptos  de la factura para el pdfCfd
    @Override
    public ArrayList<HashMap<String, String>> getNotaCreditoCfd_ListaConceptosPdf(String serieFolio) {
        
        String sql_query = ""
                + "SELECT "
                    + "''::character varying AS sku, "
                    + "fac_nota_credito.concepto AS titulo, "
                    + "''::character varying AS numero_lote, "
                    + "1::double precision AS cantidad, "
                    + "''::character varying AS unidad, "
                    + "gral_mon.descripcion as moneda, "
                    + "fac_nota_credito.subtotal AS precio_unitario, "
                    + "fac_nota_credito.subtotal AS importe,"
                    + "''::character varying AS etiqueta_ieps "
                + " "
                + "FROM fac_nota_credito "
                + "LEFT JOIN gral_mon ON gral_mon.id = fac_nota_credito.moneda_id "
                + "WHERE fac_nota_credito.serie_folio='"+serieFolio+"';";
        
        //System.out.println("Obtiene lista conceptos pdf: "+sql_query);
        ArrayList<HashMap<String, String>> hm_grid = (ArrayList<HashMap<String, String>>) this.jdbcTemplate.query(
            sql_query,  
            new Object[]{}, new RowMapper() {
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    HashMap<String, Object> row = new HashMap<String, Object>();
                    row.put("sku",rs.getString("sku"));
                    row.put("titulo",rs.getString("titulo"));
                    row.put("numero_lote","");
                    row.put("unidad",rs.getString("unidad"));
                    row.put("cantidad",StringHelper.roundDouble( rs.getString("cantidad"),2 ));
                    row.put("precio_unitario",StringHelper.roundDouble(rs.getDouble("precio_unitario"),4) );
                    row.put("importe",StringHelper.roundDouble(rs.getDouble("importe"),2) );
                    row.put("moneda",rs.getString("moneda"));
                    row.put("denominacion","");
                    row.put("etiqueta_ieps",rs.getString("etiqueta_ieps"));
                    /*
                    System.out.println(rs.getString("moneda")+"  "
                            + ""+rs.getString("sku")+"  "
                            + "    "+StringHelper.roundDouble(rs.getDouble("precio_unitario"),4)
                            + "    "+StringHelper.roundDouble(rs.getDouble("importe"),2)
                            
                            );
                    */
                    return row;
                }
            }
        );
        return hm_grid;
    }
    
    
    
    
    @Override
    public HashMap<String, String> getNotaCreditoCfd_DatosExtrasPdf(String serieFolio, String proposito, String cadena_original, String sello_digital, Integer id_sucursal, Integer id_empresa) {
        HashMap<String, String> extras = new HashMap<String, String>();
        ArrayList<HashMap<String, Object>> valorIva = new ArrayList<HashMap<String, Object>>();
        
        //Obtiene el valor del iva de la Sucursal
        valorIva= getValoriva(id_sucursal);
        
        //obtener datos del vendedor y terminos de pago
        String sql_to_query = ""
        + "SELECT "
            + "fac_nota_credito.subtotal, "
            + "fac_nota_credito.monto_ieps,"
            + "fac_nota_credito.impuesto, "
            + "fac_nota_credito.monto_retencion, "
            + "fac_nota_credito.total, "
            + "to_char(fac_nota_credito.momento_expedicion,'yyyy-mm-dd') AS fecha_comprobante, "
            + "fac_nota_credito.observaciones, "
            + "cxc_agen.nombre AS nombre_vendedor, "
            + "gral_mon.descripcion AS nombre_moneda,"
            + "gral_mon.descripcion_abr AS moneda_abr,"
            + "(CASE WHEN fac_nota_credito.ref_id='' THEN fac_nota_credito.serie_folio ELSE fac_nota_credito.ref_id END) AS ref_id "
        + "FROM fac_nota_credito  "
        + "LEFT JOIN gral_mon on gral_mon.id = fac_nota_credito.moneda_id "
        + "JOIN cxc_agen ON cxc_agen.id =  fac_nota_credito.cxc_agen_id  "
        + "WHERE fac_nota_credito.serie_folio='"+serieFolio+"' AND fac_nota_credito.gral_emp_id="+id_empresa+";";
        
        Map<String, Object> mapVendedorCondiciones = this.getJdbcTemplate().queryForMap(sql_to_query);
        extras.put("subtotal", StringHelper.roundDouble(mapVendedorCondiciones.get("subtotal").toString(),2));
        extras.put("monto_ieps", StringHelper.roundDouble(mapVendedorCondiciones.get("monto_ieps").toString(),2));
        extras.put("impuesto", StringHelper.roundDouble(mapVendedorCondiciones.get("impuesto").toString(),2));
        extras.put("monto_retencion", StringHelper.roundDouble(mapVendedorCondiciones.get("monto_retencion").toString(),2));
        extras.put("total", StringHelper.roundDouble(mapVendedorCondiciones.get("total").toString(),2));
        extras.put("nombre_moneda", mapVendedorCondiciones.get("nombre_moneda").toString());
        extras.put("moneda_abr", mapVendedorCondiciones.get("moneda_abr").toString());
        extras.put("nombre_vendedor", mapVendedorCondiciones.get("nombre_vendedor").toString());
        extras.put("observaciones", mapVendedorCondiciones.get("observaciones").toString() );
        //extras.put("fecha_comprobante", mapVendedorCondiciones.get("fecha_comprobante").toString() );
        extras.put("terminos", "");
        extras.put("dias", "0");
        extras.put("orden_compra", "" );
        extras.put("folio_pedido", "" );
        extras.put("fecha_vencimiento", "" );
        extras.put("proposito", proposito);
        extras.put("cadena_original", cadena_original);
        extras.put("sello_digital", sello_digital);
        extras.put("serieFolio", serieFolio);
        extras.put("refId", mapVendedorCondiciones.get("ref_id").toString());
        
        extras.put("monto_descto", "0.00");
        extras.put("subtotal_sin_descto", "0.00");
        extras.put("motivo_descto", "");
        
        return extras;
    }
    
    
    
    
    
    
    //obtiene la lista de conceptos para la NOTA DE CREDITO CFDI y CFDITF
    @Override
    public ArrayList<LinkedHashMap<String, String>> getNotaCreditoCfdi_ListaConceptos(Integer id_nota_credito) {
        
        String sql_query = ""
                + "SELECT "
                    + "'1'::character varying AS no_identificacion, "
                    + "1::double precision AS cantidad, "
                    + "concepto AS descripcion, "
                    + "'No aplica'::character varying AS unidad, "
                    + "subtotal  AS valor_unitario, "
                    + "subtotal AS importe "
                + "FROM fac_nota_credito "
                + "WHERE id="+id_nota_credito+";";
        
        //System.out.println("Obteniendo lista de conceptos para cfdi: "+sql_query);
        ArrayList<LinkedHashMap<String, String>> hm_conceptos = (ArrayList<LinkedHashMap<String, String>>) this.jdbcTemplate.query(
            sql_query,
            new Object[]{}, new RowMapper() {
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    LinkedHashMap<String, String> row = new LinkedHashMap<String, String>();
                    row.put("noIdentificacion",StringHelper.normalizaString(StringHelper.remueve_tildes(rs.getString("no_identificacion"))));
                    row.put("cantidad",StringHelper.roundDouble(rs.getString("cantidad"),2));
                    row.put("descripcion",StringHelper.normalizaString(StringHelper.remueve_tildes(rs.getString("descripcion"))));
                    row.put("unidad",StringHelper.normalizaString(StringHelper.remueve_tildes(rs.getString("unidad"))));
                    row.put("valorUnitario",StringHelper.roundDouble(rs.getDouble("valor_unitario"),2) );
                    row.put("importe",StringHelper.roundDouble(rs.getDouble("importe"),2) );
                    return row;
                }
            }
        );
        return hm_conceptos;
    }
    
    
    
    
    //obtiene datos extras para la NOTA DE CREDITO CFDI 
    @Override
    public LinkedHashMap<String, String> getNotaCreditoCfdi_DatosExtras(Integer id_nota_credito, String serie, String folio) {
        LinkedHashMap<String,String> datosExtras = new LinkedHashMap<String,String>();
        String monto_factura="";
        String id_moneda="";
        String denom="";
        String denominacion = "";
        String cantidad_letras="";
        
        //obtener id del cliente
        String sql_to_query = ""
                + "SELECT "
                    + "fac_nota_credito.moneda_id, "
                    + "gral_mon.descripcion_abr AS moneda_abr, "
                    + "gral_mon.iso_4217 AS nombre_moneda,"
                    + "gral_mon.simbolo AS simbolo_moneda, "
                    + "fac_nota_credito.tipo_cambio, "
                    + "(CASE WHEN fac_nota_credito.cxc_agen_id=0 THEN '' ELSE cxc_agen.nombre END ) AS clave_agente, "
                    + "fac_nota_credito.subtotal as subtotal_conceptos, "
                    + "fac_nota_credito.impuesto, "
                    + "fac_nota_credito.total as monto_total, "
                    + "fac_nota_credito.observaciones, "
                    + "(CASE WHEN fac_nota_credito.moneda_id=1 THEN 1 ELSE fac_nota_credito.tipo_cambio END) AS tipo_cambio "
                + "FROM fac_nota_credito "
                + "JOIN gral_mon ON gral_mon.id=fac_nota_credito.moneda_id "
                + "JOIN cxc_agen ON cxc_agen.id=fac_nota_credito.cxc_agen_id "
                + "WHERE  fac_nota_credito.id="+id_nota_credito+";";
        
        //System.out.println("sql_to_query: "+sql_to_query);
        
        Map<String, Object> map = this.getJdbcTemplate().queryForMap(sql_to_query);
        
        //estos son requeridos para cfdi
        datosExtras.put("serie", serie);
        datosExtras.put("folio", folio);
        datosExtras.put("orden_compra", "");
        datosExtras.put("clave_agente", map.get("clave_agente").toString());
        datosExtras.put("nombre_moneda", map.get("nombre_moneda").toString());
        datosExtras.put("simbolo_moneda", map.get("simbolo_moneda").toString());
        datosExtras.put("moneda_abr", map.get("moneda_abr").toString());
        datosExtras.put("tipo_cambio", StringHelper.roundDouble(map.get("tipo_cambio").toString(),4));
        datosExtras.put("subtotal_conceptos", StringHelper.roundDouble(map.get("subtotal_conceptos").toString(),2));
        datosExtras.put("monto_total", StringHelper.roundDouble(map.get("monto_total").toString(),2));
        datosExtras.put("observaciones", map.get("observaciones").toString());
        
        monto_factura = StringHelper.roundDouble(map.get("monto_total").toString(),2);
        id_moneda = map.get("moneda_id").toString();
        
        BigInteger num = new BigInteger(monto_factura.split("\\.")[0]);
        n2t cal = new n2t();
        String centavos = monto_factura.substring(monto_factura.indexOf(".")+1);
        String numero = cal.convertirLetras(num);
        
        //convertir a mayuscula la primera letra de la cadena
        String numeroMay = numero.substring(0, 1).toUpperCase() + numero.substring(1, numero.length());
        
        denom = map.get("moneda_abr").toString();
        
        if(centavos.equals(num.toString())){
            centavos="00";
        }
        
        if(id_moneda.equals("1")){
            denominacion = "pesos";
        }
        
        if(id_moneda.equals("2")){
            denominacion = "dolares";
        }
        
        cantidad_letras=numeroMay + " " + denominacion + ", " +centavos+"/100 "+ denom;
        datosExtras.put("monto_total_texto", cantidad_letras.toUpperCase());
        
        return datosExtras;
    }
    
    
    
    //Obtiene los impuestos trasladados para la NOTA DE CREDITO CFDI 
    @Override
    public ArrayList<LinkedHashMap<String, String>> getNotaCreditoCfdi_ImpuestosTrasladados(Integer id_nota_credito) {
        String sql_to_query = "SELECT impuesto,valor_impuesto FROM fac_nota_credito WHERE  id="+id_nota_credito+" AND impuesto >0 AND impuesto IS NOT NULL;";
        //System.out.println("sql_to_query imp tras:"+sql_to_query);
        
        ArrayList<LinkedHashMap<String, String>> tras = (ArrayList<LinkedHashMap<String, String>>) this.jdbcTemplate.query(
            sql_to_query,
            new Object[]{}, new RowMapper(){
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    LinkedHashMap<String, String> row = new LinkedHashMap<String, String>();
                    row.put("impuesto","IVA");
                    row.put("importe",StringHelper.roundDouble(rs.getString("impuesto"),2));
                    row.put("tasa", StringHelper.roundDouble(rs.getString("valor_impuesto"),2));
                    return row;
                }
            }
        );
        return tras;
    }
    
    
    
    
    
    @Override
    public ArrayList<LinkedHashMap<String, String>> getNotaCreditoCfdi_ImpuestosRetenidos(Integer id_nota_credito) {
        String sql_to_query = "SELECT monto_retencion, tasa_retencion_immex as tasa FROM fac_nota_credito WHERE id="+id_nota_credito+"  AND monto_retencion >0 AND monto_retencion IS NOT NULL;";
        
        //System.out.println("sql_to_query retenidos:"+sql_to_query);
        ArrayList<LinkedHashMap<String, String>> ret = (ArrayList<LinkedHashMap<String, String>>) this.jdbcTemplate.query(
            sql_to_query,
            new Object[]{}, new RowMapper(){
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    LinkedHashMap<String, String> row = new LinkedHashMap<String, String>();
                    row.put("impuesto","IVA");
                    row.put("importe",StringHelper.roundDouble(rs.getString("monto_retencion"),2));
                    row.put("tasa", StringHelper.roundDouble(rs.getString("tasa"),2));
                    return row;
                }
            }
        );
        return ret;
    }
    
    
    
    
    @Override
    public ArrayList<HashMap<String, Object>> getFacDevoluciones_DatosNotaCredito(String factura, String idCliente) {
        String sql_to_query = ""
        + "SELECT "
            + "serie_folio AS folio_nota, "
            + "(case when momento_expedicion is null then '' else to_char(momento_expedicion,'yyyy-mm-dd') end) as fecha_nc,"
            + "subtotal AS subtotal_nota, "
            + "impuesto AS impuesto_nota, "
            + "monto_retencion AS monto_ret_nota, "
            + "total AS total_nota, "
            + "tipo_cambio AS tc_nota,"
            + "monto_ieps,"
            + "concepto,"
            + "ctb_tmov_id as tmov_id "
        + "FROM fac_nota_credito "
        + "WHERE serie_folio_factura='"+factura+"' AND cxc_clie_id="+idCliente+" AND gral_app_id_creacion=76;";
        
        ArrayList<HashMap<String, Object>> ret = (ArrayList<HashMap<String, Object>>) this.jdbcTemplate.query(
            sql_to_query,
            new Object[]{}, new RowMapper(){
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    HashMap<String, Object> row = new HashMap<String, Object>();
                    row.put("folio_nota",rs.getString("folio_nota"));
                    row.put("fecha_nc",rs.getString("fecha_nc"));
                    row.put("tmov_id",rs.getInt("tmov_id"));
                    row.put("concepto_nota",rs.getString("concepto"));
                    row.put("subtotal_nota",StringHelper.roundDouble(rs.getString("subtotal_nota"),2));
                    row.put("monto_ieps_nota",StringHelper.roundDouble(rs.getString("monto_ieps"),2));
                    row.put("impuesto_nota",StringHelper.roundDouble(rs.getString("impuesto_nota"),2));
                    row.put("monto_ret_nota",StringHelper.roundDouble(rs.getString("monto_ret_nota"),2));
                    row.put("total_nota",StringHelper.roundDouble(rs.getString("total_nota"),2));
                    row.put("tc_nota",StringHelper.roundDouble(rs.getString("tc_nota"),4));
                    return row;
                }
            }
        );
        return ret;
    }
    
    
    
    
    @Override
    public ArrayList<LinkedHashMap<String, String>> getNotaCreditoCfdiTf_ListaConceptosXml(Integer idNotaCredito) {
        String sql_query = ""
                + "SELECT "
                    + "'1'::character varying AS no_identificacion,"
                    + "1::double precision AS cantidad,"
                    + "concepto AS descripcion, "
                    + "'No aplica'::character varying AS unidad, "
                    + "subtotal as valor_unitario,"
                    + "subtotal as importe,"
                    + "impuesto as importe_impuesto,"
                    + "monto_ieps AS importe_ieps,"
                    + "tasa_retencion_immex AS tasa_retencion, "
                    + "''::character varying AS numero_aduana, "
                    + "''::character varying AS fecha_aduana, "
                    + "''::character varying AS aduana_aduana "
                + "FROM fac_nota_credito "
                + "WHERE id="+idNotaCredito;
                
                
        
        //System.out.println("NC: "+sql_query);
        //System.out.println("getListaConceptosXmlCfdiTimbreFiscal: "+sql_query);
        
        //System.out.println("noIdentificacion "+" | descripcion      "+" | cant"+" | precio_uni"+" | importe"+" | importe_imp"+" | valor_imp"+" | tasa_ret"  );
        
        ArrayList<LinkedHashMap<String, String>> hm_conceptos = (ArrayList<LinkedHashMap<String, String>>) this.jdbcTemplate.query(
            sql_query,
            new Object[]{}, new RowMapper() {
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    LinkedHashMap<String, String> row = new LinkedHashMap<String, String>();
                    row.put("noIdentificacion",StringHelper.normalizaString(StringHelper.remueve_tildes(rs.getString("no_identificacion"))));
                    row.put("cantidad",rs.getString("cantidad"));
                    row.put("descripcion",StringHelper.normalizaString(StringHelper.remueve_tildes(rs.getString("descripcion"))));
                    row.put("unidad",StringHelper.normalizaString(StringHelper.remueve_tildes(rs.getString("unidad"))));
                    row.put("valorUnitario",StringHelper.roundDouble(rs.getDouble("valor_unitario"),2) );
                    row.put("importe",StringHelper.roundDouble(rs.getDouble("importe"),2) );
                    row.put("importe_impuesto",StringHelper.roundDouble(rs.getDouble("importe_impuesto"),2) );
                    row.put("importe_ieps",StringHelper.roundDouble(rs.getDouble("importe_ieps"),2) );
                    row.put("tasa_retencion",StringHelper.roundDouble(rs.getDouble("tasa_retencion"),2) );
                    row.put("numero_aduana","");
                    row.put("fecha_aduana","");
                    row.put("aduana_aduana","");
                    
                    row.put("descto", "0");
                    row.put("precio_unitario_con_descto", "0");
                    row.put("importe_del_descto", "0");
                    row.put("importe_con_descto", "0");
                    
                    row.put("tasa_ret","0");
                    row.put("importe_ret","0");
                    return row;
                }
            }
        );
        
        try {
            calcula_Totales_e_Impuestos(hm_conceptos);
        } catch (SQLException ex) {
            Logger.getLogger(FacturasSpringDao.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return hm_conceptos;
    }
    
    
    
    
    
    /*
     Este metodo obtiene datos de las partidas de la Devolucion.
     Estos datos solo se utilizan para sacar Importes de IVAs y IEPS
     */
    @Override
    public ArrayList<LinkedHashMap<String, String>> getNotaCreditoCfdiTf_ConceptosParaImpuestosXml(Integer idNotaCredito) {
        String sql_query = ""
        + "SELECT "
            + "inv_prod_id,"
            + "cantidad,"
            + "precio_unitario,"
            + "(cantidad * precio_unitario) AS importe,"
            + "gral_ieps_id AS id_ieps,"
            + "(valor_ieps::double precision * 100) AS tasa_ieps,"
            + "(cantidad * precio_unitario) * valor_ieps AS importe_ieps,"
            + "gral_imptos_id AS id_iva,"
            + "(valor_imp::double precision * 100) AS tasa_iva,"
            + "((cantidad * precio_unitario) + ((cantidad * precio_unitario) * valor_ieps)) * valor_imp::double precision  AS importe_iva "
        + "FROM fac_nota_credito_det "
        + "WHERE fac_nota_credito_id="+idNotaCredito;
        
        //System.out.println(sql_query);
        
        ArrayList<LinkedHashMap<String, String>> hm_conceptos = (ArrayList<LinkedHashMap<String, String>>) this.jdbcTemplate.query(
            sql_query,
            new Object[]{}, new RowMapper() {
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    LinkedHashMap<String, String> row = new LinkedHashMap<String, String>();
                    row.put("inv_prod_id",String.valueOf(rs.getInt("inv_prod_id")));
                    row.put("cantidad",rs.getString("cantidad"));
                    row.put("valorUnitario",StringHelper.roundDouble(rs.getDouble("precio_unitario"),2) );
                    row.put("importe",StringHelper.roundDouble(rs.getDouble("importe"),2) );
                    row.put("id_ieps",String.valueOf(rs.getInt("id_ieps")));
                    row.put("tasa_ieps",StringHelper.roundDouble(rs.getDouble("tasa_ieps"),2) );
                    row.put("importe_ieps",StringHelper.roundDouble(rs.getDouble("importe_ieps"),4) );
                    row.put("id_impto",String.valueOf(rs.getInt("id_iva")));
                    row.put("tasa_impuesto",StringHelper.roundDouble(rs.getDouble("tasa_iva"),2) );
                    row.put("importe_impuesto",StringHelper.roundDouble(rs.getDouble("importe_iva"),4) );
                    return row;
                }
            }
        );
        
        return hm_conceptos;
    }
    
    
    
    
    
    
    
    
    
    
    
    
    //Obtener datos para la Adenda
    @Override
    public LinkedHashMap<String, Object> getDatosAdenda(Integer tipoDoc, Integer noAdenda, HashMap<String,String> dataFactura, Integer identificador, String serieFolio, Integer id_emp) {
        LinkedHashMap<String, Object> data = new LinkedHashMap<String, Object>();
        String sql_query = "select * from cxc_clie_adenda_datos where cxc_clie_adenda_tipo="+noAdenda+" and gral_emp_id="+id_emp;
        String sql_datos_adenda = "";
        
        //Adenda FEMSA QUIMIPRODUCTOS
        if(noAdenda==1){
            /*
            //Agregar el tipo de DOCUMENTO:
             * 1=Factura, 
             * 2=Consignacion, 
             * 3=Retenciones(Honorarios, Arrendamientos, Fletes), 
             * 8=Nota de Cargo, 
             * 9=Nota de Credito
             */
            data.put("claseDoc", tipoDoc);
            
            List<Map<String, Object>> rows = getJdbcTemplate().queryForList(sql_query);
            for (Map row : rows) {
                /*noVersAdd, noSociedad, noProveedor, noSocio*/
                String campo = String.valueOf(row.get("campo"));
                String valor = String.valueOf(row.get("valor"));
                data.put(campo, valor);
            }
            
            
            if(tipoDoc==1 || tipoDoc==2){
                sql_datos_adenda = "SELECT fac_docs_adenda.* FROM fac_docs_adenda JOIN fac_docs ON fac_docs.id=fac_docs_adenda.fac_docs_id  WHERE fac_docs.serie_folio='"+serieFolio+"' AND fac_docs_adenda.prefactura_id="+identificador+" LIMIT 1;";
            }
            
            if(tipoDoc==9){
                sql_datos_adenda = ""
                        + "SELECT fac_docs_adenda.* FROM fac_docs_adenda "
                        + "JOIN fac_docs ON fac_docs.id=fac_docs_adenda.fac_docs_id "
                        + "JOIN fac_nota_credito ON (fac_nota_credito.serie_folio_factura=fac_docs.serie_folio AND fac_nota_credito.cxc_clie_id=fac_docs.cxc_clie_id) "
                        + "WHERE fac_nota_credito.serie_folio='"+serieFolio+"' AND fac_nota_credito.id="+identificador+" LIMIT 1;";
            }
            
            System.out.println("sql_datos_adenda: "+sql_datos_adenda);
            
            /*valor1=NoEntrada, valor2=NoRemision, valor3=Consignacion, valor4=CentroCostos, valor5=FechaInicio, valor6=FechaFin, valor7=Orden Compra, valor8=Moneda*/
            
            if(tipoDoc==1){
                //Factura
                //Obtener datos de la Adenda
                Map<String, Object> map = this.getJdbcTemplate().queryForMap(sql_datos_adenda);
                
                data.put("noEntrada", String.valueOf(map.get("valor1")));
                data.put("noRemision", String.valueOf(map.get("valor2")));
                data.put("noPedido", String.valueOf(map.get("valor7")));
                data.put("moneda", String.valueOf(map.get("valor8")));
                data.put("centro", "");
                data.put("iniPerLiq", "");
                data.put("finPerLiq", "");
                data.put("retencion1", "");
                data.put("retencion2", "");
            }
            
            if(tipoDoc==2){
                //Consignacion
                //Obtener datos de la Adenda
                Map<String, Object> map = this.getJdbcTemplate().queryForMap(sql_datos_adenda);
                
                String fechaIni=String.valueOf(map.get("valor5")).split("-")[2]+"."+String.valueOf(map.get("valor5")).split("-")[1]+"."+String.valueOf(map.get("valor5")).split("-")[0];
                String fechaFin=String.valueOf(map.get("valor6")).split("-")[2]+"."+String.valueOf(map.get("valor6")).split("-")[1]+"."+String.valueOf(map.get("valor6")).split("-")[0];
                
                data.put("noEntrada", "");
                data.put("noRemision", "");
                data.put("centro", String.valueOf(map.get("valor4")));
                data.put("iniPerLiq", fechaIni);
                data.put("finPerLiq", fechaFin);
                data.put("noPedido", String.valueOf(map.get("valor7")));
                data.put("moneda", String.valueOf(map.get("valor8")));
                data.put("retencion1", "");
                data.put("retencion2", "");
            }
            
            if(tipoDoc==9){
                //Nota de Credito
                
                //Buscar si la Factura ligada a la Nota de credito Incluye Adenda
                int exis = this.buscarAdendaFactura(identificador);
                
                //Si es mayor que cero si Incluye Adenda
                if(exis>0){
                    //Obtener datos de la Adenda
                    Map<String, Object> map = this.getJdbcTemplate().queryForMap(sql_datos_adenda);
                    
                    data.put("noEntrada", "");
                    data.put("noRemision", "");
                    data.put("centro", "");
                    data.put("iniPerLiq", "");
                    data.put("finPerLiq", "");
                    data.put("noPedido", String.valueOf(map.get("valor7")));
                    data.put("moneda", String.valueOf(dataFactura.get("moneda2")));
                    data.put("retencion1", "");
                    data.put("retencion2", "");
                }else{
                    data.put("noEntrada", "");
                    data.put("noRemision", "");
                    data.put("centro", "");
                    data.put("iniPerLiq", "");
                    data.put("finPerLiq", "");
                    data.put("noPedido", "");
                    data.put("moneda", String.valueOf(dataFactura.get("moneda2")));
                    data.put("retencion1", "");
                    data.put("retencion2", "");
                }
            }
            
            data.put("email", dataFactura.get("emailEmisor"));
        }
        
        
        //Addenda SUNCHEMICAL SA DE CV
        if(noAdenda==2){
            //Factura
            if(tipoDoc==1){
                sql_datos_adenda = "SELECT fac_docs_adenda.* FROM fac_docs_adenda JOIN fac_docs ON fac_docs.id=fac_docs_adenda.fac_docs_id  WHERE fac_docs.serie_folio='"+serieFolio+"' AND fac_docs_adenda.prefactura_id="+identificador+" LIMIT 1;";
            }
            
            //Nota de credito
            if(tipoDoc==9){
                sql_datos_adenda = ""
                        + "SELECT fac_docs_adenda.* FROM fac_docs_adenda "
                        + "JOIN fac_docs ON fac_docs.id=fac_docs_adenda.fac_docs_id "
                        + "JOIN fac_nota_credito ON (fac_nota_credito.serie_folio_factura=fac_docs.serie_folio AND fac_nota_credito.cxc_clie_id=fac_docs.cxc_clie_id) "
                        + "WHERE fac_nota_credito.serie_folio='"+serieFolio+"' AND fac_nota_credito.id="+identificador+" LIMIT 1;";
            }
            
            //System.out.println("sql_datos_adenda: "+sql_datos_adenda);
            //Obtener datos de la Adenda
            Map<String, Object> map = this.getJdbcTemplate().queryForMap(sql_datos_adenda);
            
            data.put("PO_NUMBER", String.valueOf(map.get("valor1")));
        }
        
        //Addenda COMEX
        if(noAdenda==3){
            //Factura
            if(tipoDoc==1){
                sql_datos_adenda = "SELECT fac_docs_adenda.* FROM fac_docs_adenda WHERE fac_docs_adenda.fac_docs_id="+identificador+" and generado=false LIMIT 1;";
                //Tipo Adenda=3(valor1=Orden Compra, valor2=Email-Emisor, valor3=Moneda, valor4=TC, valor5=Subtotal, valor6=Total).
            }
            
            //Nota de credito
            if(tipoDoc==9){
                sql_datos_adenda = ""
                        + "SELECT fac_docs_adenda.* FROM fac_docs_adenda "
                        + "JOIN fac_docs ON fac_docs.id=fac_docs_adenda.fac_docs_id "
                        + "JOIN fac_nota_credito ON (fac_nota_credito.serie_folio_factura=fac_docs.serie_folio AND fac_nota_credito.cxc_clie_id=fac_docs.cxc_clie_id) "
                        + "WHERE fac_nota_credito.serie_folio='"+serieFolio+"' AND fac_nota_credito.id="+identificador+" LIMIT 1;";
            }
            
            //System.out.println("sql_datos_adenda: "+sql_datos_adenda);
            //Obtener datos de la Adenda
            Map<String, Object> map = this.getJdbcTemplate().queryForMap(sql_datos_adenda);
            
            data.put("type", "");
            data.put("contentVersion", "");
            data.put("documentStructureVersion", "");
            data.put("documentStatus", "");
            data.put("DeliveryDate", "");
            data.put("referenceIdentification", map.get("valor1"));
            data.put("email_emisor", map.get("valor2"));
            data.put("currencyISOCode", map.get("valor3"));
            data.put("rateOfChange", map.get("valor4"));
            data.put("baseAmount", map.get("valor5"));
            data.put("payableAmount", map.get("valor6"));
        }
        
        
        return data;
    }
    
    
    //Verificar si la factura ligada a la Nota de Credito Incluye Adenda
    @Override
    public int getStatusAdendaFactura(Integer id_tipo_addenda, Integer id_fac) {
        String sql_to_query = "SELECT count(id) FROM fac_docs_adenda where fac_docs_id="+id_fac+" and cxc_clie_adenda_tipo_id="+id_tipo_addenda+" and generado=true;";
        
        System.out.println("sql_to_query: "+sql_to_query);
        
        int rowCount = this.getJdbcTemplate().queryForInt(sql_to_query);
        
        return rowCount;
    }
    
    
    //Verificar si la factura ligada a la Nota de Credito Incluye Adenda
    @Override
    public int buscarAdendaFactura(Integer idNotaCredito) {
        String sql_to_query = "SELECT count(fac_docs_adenda.id) FROM fac_docs_adenda JOIN fac_docs ON fac_docs.id=fac_docs_adenda.fac_docs_id JOIN fac_nota_credito ON (fac_nota_credito.serie_folio_factura=fac_docs.serie_folio AND fac_nota_credito.cxc_clie_id=fac_docs.cxc_clie_id) WHERE fac_nota_credito.id="+idNotaCredito;
        
        int rowCount = this.getJdbcTemplate().queryForInt(sql_to_query);
        
        return rowCount;
    }
    
    
    //Obtiene todos los impuestos del ieps(Impuesto Especial sobre Productos y Servicios)
    @Override
    public ArrayList<HashMap<String, String>> getIeps(Integer idEmp) {
        String sql_to_query = "SELECT id, titulo, tasa FROM gral_ieps  WHERE borrado_logico=false AND gral_emp_id="+idEmp+";";
        
        //log.log(Level.INFO, "Ejecutando query de {0}", sql_to_query);
        ArrayList<HashMap<String, String>> hm_ieps = (ArrayList<HashMap<String, String>>) this.jdbcTemplate.query(
            sql_to_query,
            new Object[]{}, new RowMapper(){
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    HashMap<String, String> row = new HashMap<String, String>();
                    row.put("id",String.valueOf(rs.getInt("id")));
                    row.put("titulo",rs.getString("titulo"));
                    row.put("tasa",StringHelper.roundDouble(rs.getString("tasa"),2));
                    return row;
                }
            }
        );
        return hm_ieps;
    }
    
    
    
    //Obtiene todos los impuestos del IVA(Impuesto al Valor Agregado)
    @Override
    public ArrayList<HashMap<String, String>> getIvas() {
        String sql_to_query = "SELECT id, descripcion AS titulo, iva_1 AS tasa FROM gral_imptos  WHERE borrado_logico=false;";
        
        //log.log(Level.INFO, "Ejecutando query de {0}", sql_to_query);
        ArrayList<HashMap<String, String>> hm_ieps = (ArrayList<HashMap<String, String>>) this.jdbcTemplate.query(
            sql_to_query,
            new Object[]{}, new RowMapper(){
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    HashMap<String, String> row = new HashMap<String, String>();
                    row.put("id",String.valueOf(rs.getInt("id")));
                    row.put("titulo",rs.getString("titulo"));
                    row.put("tasa",StringHelper.roundDouble(rs.getString("tasa"),2));
                    return row;
                }
            }
        );
        return hm_ieps;
    }
    
    
    //Obtiene los datos para al Paginado y el Grid
    @Override
    public ArrayList<HashMap<String, Object>> getFacNomina_PaginaGrid(String data_string, int offset, int pageSize, String orderBy, String asc) {
        String sql_busqueda = "select id from gral_bus_catalogos(?) as foo (id integer)";

	String sql_to_query = ""
                + "SELECT "
                    + "fac_nomina.id,"
                    + "nom_periodos_conf.prefijo||nom_periodos_conf_det.folio AS no_periodo,"
                    + "nom_periodos_conf_det.titulo AS periodo,"
                    + "to_char(fac_nomina.fecha_pago::timestamp with time zone, 'dd/mm/yyyy') AS fecha_pago,"
                    + "nom_periodicidad_pago.titulo AS tipo,"
                    + "to_char(fac_nomina.momento_creacion::timestamp with time zone, 'dd/mm/yyyy') AS fecha_creacion "
                + "FROM fac_nomina "
                + "JOIN nom_periodicidad_pago ON nom_periodicidad_pago.id=fac_nomina.nom_periodicidad_pago_id "
                + "JOIN nom_periodos_conf_det ON nom_periodos_conf_det.id=fac_nomina.nom_periodos_conf_det_id  "
                + "JOIN nom_periodos_conf ON nom_periodos_conf.id=nom_periodos_conf_det.nom_periodos_conf_id  "
                +"JOIN ("+sql_busqueda+") as subt on subt.id=fac_nomina.id "
                +"order by "+orderBy+" "+asc+" limit ? OFFSET ? ";

        System.out.println("Busqueda GetPage: "+sql_to_query);
        
        //log.log(Level.INFO, "Ejecutando query de {0}", sql_to_query);
        ArrayList<HashMap<String, Object>> hm = (ArrayList<HashMap<String, Object>>) this.jdbcTemplate.query(
            sql_to_query,
            new Object[]{new String(data_string), new Integer(pageSize),new Integer(offset)}, new RowMapper() {
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    HashMap<String, Object> row = new HashMap<String, Object>();
                    row.put("id",rs.getInt("id"));
                    row.put("no_periodo",rs.getString("no_periodo"));
                    row.put("periodo",rs.getString("periodo"));
                    row.put("fecha_pago",rs.getString("fecha_pago"));
                    row.put("tipo",rs.getString("tipo"));
                    row.put("fecha_creacion",rs.getString("fecha_creacion"));
                    
                    return row;
                }
            }
        );
        return hm;
    }
    
    @Override
    public ArrayList<HashMap<String, Object>> getFacNomina_Datos(Integer id) {
        String sql_query = ""
        + "SELECT "
            + "fac_nomina.id,"
            + "gral_emp.titulo AS emisor_nombre,"
            + "gral_emp.rfc AS emisor_rfc,"
            + "(CASE WHEN gral_emp.regimen_fiscal IS NULL THEN '' ELSE gral_emp.regimen_fiscal END) AS emisor_reg_fis,"
            + "gral_emp.calle||' '||(CASE WHEN gral_emp.numero_exterior IS NULL THEN '' ELSE (CASE WHEN gral_emp.numero_exterior='' THEN '' ELSE 'NO. EXT. '||gral_emp.numero_exterior END) END) ||' '||(CASE WHEN gral_emp.numero_interior IS NULL THEN '' ELSE (CASE WHEN gral_emp.numero_interior='' THEN '' ELSE 'NO. INT. '||gral_emp.numero_interior END) END) ||', '||gral_emp.colonia||', '||(CASE WHEN gral_mun.titulo IS NULL THEN '' ELSE gral_mun.titulo END)||', '||(CASE WHEN gral_edo.titulo IS NULL THEN '' ELSE gral_edo.titulo END)||', '||(CASE WHEN gral_pais.titulo IS NULL THEN '' ELSE gral_pais.titulo END)||' C.P. '||gral_emp.cp AS emisor_dir,"
            + "fac_nomina.tipo_comprobante,"
            + "fac_nomina.forma_pago,"
            + "fac_nomina.tipo_cambio,"
            + "fac_nomina.no_cuenta,"
            + "fac_nomina.fecha_pago,"
            + "fac_nomina.fac_metodos_pago_id AS metodo_pago_id,"
            + "fac_nomina.gral_mon_id AS mon_id,"
            + "fac_nomina.nom_periodicidad_pago_id AS periodicidad_pago_id,"
            + "fac_nomina.nom_periodos_conf_det_id AS no_periodo_id,"
            + "fac_nomina.status "
        + "FROM fac_nomina "
        + "JOIN gral_emp ON gral_emp.id=fac_nomina.gral_emp_id "
        + "JOIN gral_pais ON gral_pais.id=gral_emp.pais_id "
        + "JOIN gral_edo ON gral_edo.id=gral_emp.estado_id "
        + "JOIN gral_mun ON gral_mun.id=gral_emp.municipio_id "
        + "WHERE fac_nomina.id=?;";
        
        System.out.println("Ejecutando query:"+ sql_query);
        //System.out.println("Identificador: "+id);
        
        ArrayList<HashMap<String, Object>> hm = (ArrayList<HashMap<String, Object>>) this.jdbcTemplate.query(
            sql_query,
            new Object[]{new Integer(id)}, new RowMapper() {
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    HashMap<String, Object> row = new HashMap<String, Object>();
                    row.put("id",rs.getInt("id"));
                    row.put("emisor_nombre",rs.getString("emisor_nombre"));
                    row.put("emisor_rfc",rs.getString("emisor_rfc"));
                    row.put("emisor_reg_fis",rs.getString("emisor_reg_fis"));
                    row.put("emisor_dir",rs.getString("emisor_dir"));
                    row.put("tipo_comprobante",rs.getString("tipo_comprobante"));
                    row.put("forma_pago",rs.getString("forma_pago"));
                    row.put("tipo_cambio",StringHelper.roundDouble(rs.getDouble("tipo_cambio"),4));
                    row.put("no_cuenta",rs.getString("no_cuenta"));
                    row.put("fecha_pago",rs.getString("fecha_pago"));
                    row.put("metodo_pago_id",rs.getString("metodo_pago_id"));
                    row.put("mon_id",rs.getString("mon_id"));
                    row.put("periodicidad_pago_id",String.valueOf(rs.getInt("periodicidad_pago_id")));
                    row.put("no_periodo_id",rs.getString("no_periodo_id"));
                    row.put("status",String.valueOf(rs.getInt("status")));
                    return row;
                }
            }
        );
        return hm;
    }
    
    //Obtiene la lista de empleados para editar Nomina
    @Override
    public ArrayList<HashMap<String, Object>> getFacNomina_Grid(Integer id) {
        String sql_to_query=""
        + "SELECT "
            + "id_reg, "
            + "empleado_id, "
            + "nombre, "
            + "sum_percep,"
            + "sbt_deduc, "
            + "(sum_percep - sbt_deduc) AS total_pago,"
            + "facturado,"
            + "serie_folio,"
            + "cancelado "
        + "FROM ("
            + "SELECT "
                + "fac_nomina_det.id AS id_reg, "
                + "fac_nomina_det.gral_empleado_id AS empleado_id, "
                + "(CASE WHEN gral_empleados.clave IS NULL THEN lpad('',4,' ') ELSE lpad(gral_empleados.clave,4,'0') END)||' '||(CASE WHEN gral_empleados.nombre_pila IS NULL THEN '' ELSE gral_empleados.nombre_pila END)||' '||(CASE WHEN gral_empleados.apellido_paterno IS NULL THEN '' ELSE gral_empleados.apellido_paterno END)||' '||(CASE WHEN gral_empleados.apellido_materno IS NULL THEN '' ELSE gral_empleados.apellido_materno END) AS nombre, "
                + "(CASE WHEN sbt_percep.fac_nomina_det_id IS NULL THEN 0 ELSE sbt_percep.sum_percep END) AS sum_percep,"
                + "(CASE WHEN sbt_deduc.fac_nomina_det_id IS NULL THEN 0 ELSE sbt_deduc.sum_deduc END) AS sbt_deduc,"
                + "fac_nomina_det.facturado,"
                + "(CASE WHEN fac_nomina_det.serie IS NULL THEN '' ELSE fac_nomina_det.serie END)||(CASE WHEN fac_nomina_det.folio IS NULL THEN '' ELSE fac_nomina_det.folio END) AS serie_folio,"
                + "(CASE WHEN fac_nomina_det.cancelado IS NULL THEN false ELSE fac_nomina_det.cancelado END) AS cancelado "
            + "FROM fac_nomina_det "
            + "LEFT JOIN (SELECT fac_nomina_det_id, sum(gravado + excento) AS sum_percep FROM fac_nomina_det_percep GROUP BY fac_nomina_det_id) AS sbt_percep ON sbt_percep.fac_nomina_det_id=fac_nomina_det.id "
            + "LEFT JOIN (SELECT fac_nomina_det_id, sum(gravado + excento) AS sum_deduc FROM fac_nomina_det_deduc GROUP BY fac_nomina_det_id) AS sbt_deduc ON sbt_deduc.fac_nomina_det_id=fac_nomina_det.id "
            + "JOIN gral_empleados ON gral_empleados.id=fac_nomina_det.gral_empleado_id "
            + "WHERE fac_nomina_det.fac_nomina_id=? "
        + ") AS sbt ORDER BY id_reg;";
        
        ArrayList<HashMap<String,Object>>hm=(ArrayList<HashMap<String,Object>>)this.jdbcTemplate.query(
            sql_to_query,
            new Object[]{new Integer(id)},new RowMapper(){
                @Override
                public Object mapRow(ResultSet rs,int rowNum)throws SQLException{
                 HashMap<String,Object>row=new HashMap<String,Object>();
                 row.put("id_reg",rs.getString("id_reg"));
                 row.put("empleado_id",rs.getString("empleado_id"));
                 row.put("nombre",rs.getString("nombre"));
                 row.put("total_percep",StringHelper.roundDouble(rs.getDouble("sum_percep"),2));
                 row.put("total_deduc",StringHelper.roundDouble(rs.getDouble("sbt_deduc"),2));
                 row.put("total_pago",StringHelper.roundDouble(rs.getDouble("total_pago"),2));
                 row.put("facturado",String.valueOf(rs.getBoolean("facturado")).toLowerCase());
                 row.put("no_nom",rs.getString("serie_folio"));
                 row.put("cancelado",String.valueOf(rs.getBoolean("cancelado")).toLowerCase());
                 return row;
                }
            }
        );
        return hm;
    }
    
    
    
    
    //Obtener registros para generar Nomina CFDI
    @Override
    public ArrayList<HashMap<String, Object>> getFacNomina_IdNomimaDet(Integer id, Integer id_empleado) {
        String sql_to_query="SELECT id AS id_reg FROM fac_nomina_det WHERE fac_nomina_id=? AND gral_empleado_id=? ORDER BY id LIMIT 1;";
        ArrayList<HashMap<String,Object>>hm=(ArrayList<HashMap<String,Object>>)this.jdbcTemplate.query(
            sql_to_query,
            new Object[]{new Integer(id), new Integer(id_empleado)},new RowMapper(){
                @Override
                public Object mapRow(ResultSet rs,int rowNum)throws SQLException{
                 HashMap<String,Object>row=new HashMap<String,Object>();
                 row.put("id_reg",rs.getInt("id_reg"));
                 return row;
                }
            }
        );
        return hm;
    }
    
    
    
    
    
    @Override
    public HashMap<String, Object> getFacNomina_DatosEmisor(Integer id_emp) {
        HashMap<String, Object> mapDatos = new HashMap<String, Object>();
        
        String sql_to_query = ""
        + "SELECT "
            + "gral_emp.titulo,"
            + "gral_emp.rfc,"
            + "(CASE WHEN gral_emp.calle IS NULL THEN '' ELSE gral_emp.calle END) AS calle, "
            + "(CASE WHEN gral_emp.colonia IS NULL THEN '' ELSE gral_emp.colonia END) AS colonia, "
            + "(CASE WHEN gral_emp.numero_interior IS NULL THEN '' ELSE gral_emp.numero_interior END) AS numero_interior,  "
            + "(CASE WHEN gral_emp.numero_exterior IS NULL THEN '' ELSE gral_emp.numero_exterior END) AS numero_exterior, "
            + "(CASE WHEN gral_pais.titulo IS NULL THEN '' ELSE gral_pais.titulo END) AS pais, "
            + "(CASE WHEN gral_edo.titulo IS NULL THEN '' ELSE gral_edo.titulo END) AS estado, "
            + "(CASE WHEN gral_mun.titulo IS NULL THEN '' ELSE gral_mun.titulo END) AS municipio, "
            + "gral_emp.cp,"
            + "gral_emp.telefono,"
            + "(CASE WHEN gral_emp.regimen_fiscal IS NULL THEN '' ELSE gral_emp.regimen_fiscal END) AS regimen_fiscal, "
            + "(CASE WHEN gral_emp.pagina_web IS NULL THEN '' ELSE gral_emp.pagina_web END) AS pagina_web,"
            + "gral_emp.calle||' '||(CASE WHEN gral_emp.numero_exterior IS NULL THEN '' ELSE (CASE WHEN gral_emp.numero_exterior='' THEN '' ELSE 'NO. EXT. '||gral_emp.numero_exterior END) END) ||' '||(CASE WHEN gral_emp.numero_interior IS NULL THEN '' ELSE (CASE WHEN gral_emp.numero_interior='' THEN '' ELSE 'NO. INT. '||gral_emp.numero_interior END) END) ||', '||gral_emp.colonia||', '||(CASE WHEN gral_mun.titulo IS NULL THEN '' ELSE gral_mun.titulo END)||', '||(CASE WHEN gral_edo.titulo IS NULL THEN '' ELSE gral_edo.titulo END)||', '||(CASE WHEN gral_pais.titulo IS NULL THEN '' ELSE gral_pais.titulo END)||' C.P. '||gral_emp.cp AS direccion  "
        + "FROM gral_emp "
        + "JOIN gral_pais ON gral_pais.id=gral_emp.pais_id "
        + "JOIN gral_edo ON gral_edo.id=gral_emp.estado_id "
        + "JOIN gral_mun ON gral_mun.id=gral_emp.municipio_id "
        + "WHERE gral_emp.id="+id_emp+";";
        
        System.out.println("getDatosEmp: "+sql_to_query);
        
        Map<String, Object> map = this.getJdbcTemplate().queryForMap(sql_to_query);
        mapDatos.put("emp_razon_social", String.valueOf(map.get("titulo")));
        mapDatos.put("emp_rfc", String.valueOf(map.get("rfc")));
        mapDatos.put("emp_calle", String.valueOf(map.get("calle")));
        mapDatos.put("emp_no_interior", String.valueOf(map.get("numero_interior")));
        mapDatos.put("emp_no_exterior", String.valueOf(map.get("numero_exterior")));
        mapDatos.put("emp_colonia", String.valueOf(map.get("colonia")));
        mapDatos.put("emp_pais", String.valueOf(map.get("pais")));
        mapDatos.put("emp_estado", String.valueOf(map.get("estado")));
        mapDatos.put("emp_municipio", String.valueOf(map.get("municipio")));
        mapDatos.put("emp_cp", String.valueOf(map.get("cp")));
        mapDatos.put("emp_tel", String.valueOf(map.get("telefono")));
        mapDatos.put("emp_regimen_fiscal", String.valueOf(map.get("regimen_fiscal")));
        mapDatos.put("emp_pagina_web", String.valueOf(map.get("pagina_web")));
        mapDatos.put("emp_direccion", String.valueOf(map.get("direccion")));
        
        return mapDatos;
    }
    
    
    //Obtiene la Periodicidad del Pago
    @Override
    public ArrayList<HashMap<String, Object>> getFacNomina_PeriodicidadPago(Integer idEmp) {
        String sql_to_query="SELECT id, titulo FROM nom_periodicidad_pago WHERE activo=true AND gral_emp_id=? AND borrado_logico=false ORDER BY id;";
        ArrayList<HashMap<String,Object>>hm=(ArrayList<HashMap<String,Object>>)this.jdbcTemplate.query(
            sql_to_query,
            new Object[]{new Integer(idEmp)},new RowMapper(){
                @Override
                public Object mapRow(ResultSet rs,int rowNum)throws SQLException{
                 HashMap<String,Object>row=new HashMap<String,Object>();
                 row.put("id",rs.getString("id"));
                 row.put("titulo",rs.getString("titulo"));
                 return row;
                }
            }
        );
        return hm;
    }
    
    
    
    //Obtiene los Periodos de Pago Configurados por Tipo de Periodicidad
    @Override
    public ArrayList<HashMap<String, Object>> getFacNomina_PeriodosPorTipo(Integer tipo, Integer idEmp, Integer identificador) {
        String sql_to_query="";
        
        if(identificador>0){
            sql_to_query="SELECT nom_periodos_conf_det.id,(CASE WHEN nom_periodos_conf.prefijo IS NULL THEN '' ELSE (CASE WHEN nom_periodos_conf.prefijo='' THEN '' ELSE nom_periodos_conf.prefijo||'-' END) END)||nom_periodos_conf_det.folio||' '||nom_periodos_conf_det.titulo AS periodo,(CASE WHEN nom_periodos_conf_det.fecha_ini IS NULL THEN '' ELSE nom_periodos_conf_det.fecha_ini::character varying END) AS fecha_ini,(CASE WHEN nom_periodos_conf_det.fecha_fin IS NULL THEN '' ELSE nom_periodos_conf_det.fecha_fin::character varying END) AS fecha_fin, nom_periodos_conf_det.estatus FROM nom_periodos_conf JOIN nom_periodos_conf_det ON nom_periodos_conf_det.nom_periodos_conf_id=nom_periodos_conf.id JOIN fac_nomina ON fac_nomina.nom_periodos_conf_det_id=nom_periodos_conf_det.id WHERE nom_periodos_conf.gral_emp_id=? AND nom_periodos_conf.nom_periodicidad_pago_id=? AND fac_nomina.id="+identificador+" ORDER BY nom_periodos_conf_det.id;";
        }else{
            sql_to_query="SELECT nom_periodos_conf_det.id,(CASE WHEN nom_periodos_conf.prefijo IS NULL THEN '' ELSE (CASE WHEN nom_periodos_conf.prefijo='' THEN '' ELSE nom_periodos_conf.prefijo||'-' END) END)||nom_periodos_conf_det.folio||' '||nom_periodos_conf_det.titulo AS periodo,(CASE WHEN nom_periodos_conf_det.fecha_ini IS NULL THEN '' ELSE nom_periodos_conf_det.fecha_ini::character varying END) AS fecha_ini,(CASE WHEN nom_periodos_conf_det.fecha_fin IS NULL THEN '' ELSE nom_periodos_conf_det.fecha_fin::character varying END) AS fecha_fin, nom_periodos_conf_det.estatus FROM nom_periodos_conf JOIN nom_periodos_conf_det ON nom_periodos_conf_det.nom_periodos_conf_id=nom_periodos_conf.id WHERE nom_periodos_conf.borrado_logico=false AND nom_periodos_conf.gral_emp_id=? AND nom_periodos_conf.nom_periodicidad_pago_id=? AND nom_periodos_conf_det.estatus=false ORDER BY nom_periodos_conf_det.id;";
        }
        
        ArrayList<HashMap<String,Object>>hm=(ArrayList<HashMap<String,Object>>)this.jdbcTemplate.query(
            sql_to_query,
            new Object[]{new Integer(idEmp), new Integer(tipo)},new RowMapper(){
                @Override
                public Object mapRow(ResultSet rs,int rowNum)throws SQLException{
                 HashMap<String,Object>row=new HashMap<String,Object>();
                 row.put("id",rs.getString("id"));
                 row.put("periodo",rs.getString("periodo"));
                 row.put("fecha_ini",rs.getString("fecha_ini"));
                 row.put("fecha_fin",rs.getString("fecha_fin"));
                 row.put("status",String.valueOf(rs.getBoolean("estatus")));
                 return row;
                }
            }
        );
        return hm;
    }
    
    
    //Obtiene los parametros default para la nomina 
    @Override
    public ArrayList<HashMap<String, Object>> getFacNomina_Parametros(Integer idEmp,Integer idSuc) {
        String sql_to_query="SELECT tipo_comprobante, forma_pago, no_cuenta_pago,gral_mon_id, (CASE WHEN gral_mon_id=0 THEN 0 ELSE (CASE WHEN gral_mon_id=1 THEN 1 ELSE (SELECT erp_monedavers.valor FROM erp_monedavers WHERE erp_monedavers.momento_creacion<=now() AND erp_monedavers.moneda_id=gral_mon_id ORDER BY erp_monedavers.momento_creacion DESC LIMIT 1) END) END) AS tc, gral_isr_id, (CASE WHEN motivo_descuento IS NULL THEN '' ELSE motivo_descuento END) AS motivo_descuento, concepto_unidad FROM fac_nomina_par WHERE gral_emp_id=? AND gral_suc_id=?;";
        ArrayList<HashMap<String,Object>>hm=(ArrayList<HashMap<String,Object>>)this.jdbcTemplate.query(
            sql_to_query,
            new Object[]{new Integer(idEmp), new Integer(idSuc)},new RowMapper(){
                @Override
                public Object mapRow(ResultSet rs,int rowNum)throws SQLException{
                 HashMap<String,Object>row=new HashMap<String,Object>();
                 row.put("tipo_comprobante",rs.getString("tipo_comprobante"));
                 row.put("forma_pago",rs.getString("forma_pago"));
                 row.put("no_cuenta_pago",rs.getString("no_cuenta_pago"));
                 row.put("mon_id",rs.getString("gral_mon_id"));
                 row.put("tc",StringHelper.roundDouble(rs.getString("tc"),4));
                 row.put("isr_id",rs.getString("gral_isr_id"));
                 row.put("motivo_descuento",rs.getString("motivo_descuento"));
                 row.put("concepto_unidad",rs.getString("concepto_unidad"));
                 return row;
                }
            }
        );
        return hm;
    }
    
    //Obtiene la Leyenda que debe ir en el Recibo de la Nomina
    @Override
    public HashMap<String, Object> getFacNomina_LeyendaReciboNomina(Integer idEmp,Integer idSuc) {
        String sql_to_query="SELECT (CASE WHEN leyenda IS NULL THEN '' ELSE upper(leyenda) END) AS leyenda_nomina FROM fac_nomina_par WHERE gral_emp_id=? AND gral_suc_id=?;";
        
        HashMap<String, Object> hm = (HashMap<String, Object>) this.jdbcTemplate.queryForObject(
            sql_to_query, 
            new Object[]{new Integer(idEmp), new Integer(idSuc)}, new RowMapper() {
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    HashMap<String, Object> row = new HashMap<String, Object>();
                    row.put("leyenda_nomina",String.valueOf(rs.getString("leyenda_nomina")));
                    return row;
                }
            }
        );
        return hm;
    }
    
    //Obtiene obtiene la lista de empleados
    @Override
    public ArrayList<HashMap<String, Object>> getFacNomina_Empleados(Integer idEmp, Integer periodicidad_id) {
        String sql_to_query="SELECT id,(CASE WHEN clave IS NULL THEN lpad('',4,' ') ELSE lpad(clave,4,'0') END)||' '||(CASE WHEN nombre_pila IS NULL THEN '' ELSE nombre_pila END)||' '||(CASE WHEN apellido_paterno IS NULL THEN '' ELSE apellido_paterno END)||' '||(CASE WHEN apellido_materno IS NULL THEN '' ELSE apellido_materno END) AS nombre FROM gral_empleados WHERE gral_emp_id=? AND nom_periodicidad_pago_id=? AND genera_nomina=true ORDER BY id;";
        ArrayList<HashMap<String,Object>>hm=(ArrayList<HashMap<String,Object>>)this.jdbcTemplate.query(
            sql_to_query,
            new Object[]{new Integer(idEmp), new Integer(periodicidad_id)},new RowMapper(){
                @Override
                public Object mapRow(ResultSet rs,int rowNum)throws SQLException{
                 HashMap<String,Object>row=new HashMap<String,Object>();
                 row.put("id",rs.getString("id"));
                 //Este dato se va en cero porque es un nuevo proceso de nomina
                 row.put("id_reg","0");
                 row.put("nombre",rs.getString("nombre"));
                 return row;
                }
            }
        );
        return hm;
    }
    
    
    //Obtiene la siguiente secuencia para el id de la tabla fac_nomina
    @Override
    public int getIdSeqFacNomina() {
        int idSeq = this.getJdbcTemplate().queryForInt("select nextval('fac_nomina_id_seq');");
        return idSeq;
    }
    
    //Obtiene los regimenes de contratacion
    @Override
    public ArrayList<HashMap<String, Object>> getFacNomina_RegimenContratacion() {
        String sql_to_query="SELECT id, (case when clave is null then '' else clave end)||' '||titulo AS titulo FROM nom_regimen_contratacion WHERE activo=true ORDER BY id;";
        ArrayList<HashMap<String,Object>>hm=(ArrayList<HashMap<String,Object>>)this.jdbcTemplate.query(
            sql_to_query,
            new Object[]{},new RowMapper(){
                @Override
                public Object mapRow(ResultSet rs,int rowNum)throws SQLException{
                 HashMap<String,Object>row=new HashMap<String,Object>();
                 row.put("id",rs.getString("id"));
                 row.put("titulo",rs.getString("titulo"));
                 return row;
                }
            }
        );
        return hm;
    }
    
    
    //Obtiene los tipos de contrato
    @Override
    public ArrayList<HashMap<String, Object>> getFacNomina_TiposContrato() {
        String sql_to_query="SELECT id, titulo FROM nom_tipo_contrato WHERE activo=true ORDER BY id;";
        ArrayList<HashMap<String,Object>>hm=(ArrayList<HashMap<String,Object>>)this.jdbcTemplate.query(
            sql_to_query,
            new Object[]{},new RowMapper(){
                @Override
                public Object mapRow(ResultSet rs,int rowNum)throws SQLException{
                 HashMap<String,Object>row=new HashMap<String,Object>();
                 row.put("id",rs.getString("id"));
                 row.put("titulo",rs.getString("titulo"));
                 return row;
                }
            }
        );
        return hm;
    }
    
    
    //Obtiene los Tipos de Jornada Laboral
    @Override
    public ArrayList<HashMap<String, Object>> getFacNomina_TiposJornada() {
        String sql_to_query="SELECT id, titulo FROM nom_tipo_jornada WHERE activo=true ORDER BY id;";
        ArrayList<HashMap<String,Object>>hm=(ArrayList<HashMap<String,Object>>)this.jdbcTemplate.query(
            sql_to_query,
            new Object[]{},new RowMapper(){
                @Override
                public Object mapRow(ResultSet rs,int rowNum)throws SQLException{
                 HashMap<String,Object>row=new HashMap<String,Object>();
                 row.put("id",rs.getString("id"));
                 row.put("titulo",rs.getString("titulo"));
                 return row;
                }
            }
        );
        return hm;
    }
    
    
    //Obtiene los tipos de Riesgos de Puestos
    @Override
    public ArrayList<HashMap<String, Object>> getFacNomina_RiesgosPuesto() {
        String sql_to_query="SELECT id, titulo FROM nom_riesgo_puesto WHERE activo=true ORDER BY id;";
        ArrayList<HashMap<String,Object>>hm=(ArrayList<HashMap<String,Object>>)this.jdbcTemplate.query(
            sql_to_query,
            new Object[]{},new RowMapper(){
                @Override
                public Object mapRow(ResultSet rs,int rowNum)throws SQLException{
                 HashMap<String,Object>row=new HashMap<String,Object>();
                 row.put("id",rs.getString("id"));
                 row.put("titulo",rs.getString("titulo"));
                 return row;
                }
            }
        );
        return hm;
    }
    
    
    //Obtiene los tipos de Horas Extra
    @Override
    public ArrayList<HashMap<String, Object>> getFacNomina_TiposHoraExtra() {
        String sql_to_query="SELECT id, titulo FROM nom_tipo_hrs_extra WHERE activo=true ORDER BY id;";
        ArrayList<HashMap<String,Object>>hm=(ArrayList<HashMap<String,Object>>)this.jdbcTemplate.query(
            sql_to_query,
            new Object[]{},new RowMapper(){
                @Override
                public Object mapRow(ResultSet rs,int rowNum)throws SQLException{
                 HashMap<String,Object>row=new HashMap<String,Object>();
                 row.put("id",rs.getString("id"));
                 row.put("titulo",rs.getString("titulo"));
                 return row;
                }
            }
        );
        return hm;
    }
    
    //Obtiene los tipos de Incapacidad
    @Override
    public ArrayList<HashMap<String, Object>> getFacNomina_TiposIncapacidad() {
        String sql_to_query="SELECT id, clave, (CASE WHEN clave IS NULL THEN '' ELSE clave||' ' END)||titulo AS titulo FROM nom_tipo_incapacidad WHERE activo=true ORDER BY id;";
        ArrayList<HashMap<String,Object>>hm=(ArrayList<HashMap<String,Object>>)this.jdbcTemplate.query(
            sql_to_query,
            new Object[]{},new RowMapper(){
                @Override
                public Object mapRow(ResultSet rs,int rowNum)throws SQLException{
                 HashMap<String,Object>row=new HashMap<String,Object>();
                 row.put("id",rs.getString("id"));
                 row.put("clave",rs.getString("clave"));
                 row.put("titulo",rs.getString("titulo"));
                 return row;
                }
            }
        );
        return hm;
    }
    
    
    //Obtiene los Bancos de la empresa
    @Override
    public ArrayList<HashMap<String, Object>> getFacNomina_Bancos(Integer idEmpresa) {
        String sql_to_query="SELECT id, (case when clave is null then '' else (case when clave<>'' then clave||' ' else '' end) end)||titulo AS titulo FROM tes_ban WHERE gral_emp_id=? AND borrado_logico=false ORDER BY titulo;";
        ArrayList<HashMap<String,Object>>hm=(ArrayList<HashMap<String,Object>>)this.jdbcTemplate.query(
            sql_to_query,
            new Object[]{new Integer (idEmpresa)},new RowMapper(){
                @Override
                public Object mapRow(ResultSet rs,int rowNum)throws SQLException{
                 HashMap<String,Object>row=new HashMap<String,Object>();
                 row.put("id",rs.getString("id"));
                 row.put("titulo",rs.getString("titulo"));
                 return row;
                }
            }
        );
        return hm;
    }
    
    //Obtiene la lista de puestos
    @Override
    public ArrayList<HashMap<String, Object>> getFacNomina_Puestos(Integer id_empresa) {
        String sql_to_query = "select id,titulo from gral_puestos  where gral_emp_id="+id_empresa+" order by titulo";
        ArrayList<HashMap<String, Object>> religion = (ArrayList<HashMap<String, Object>>) this.jdbcTemplate.query(
            sql_to_query,
            new Object[]{}, new RowMapper(){
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    HashMap<String, Object> row = new HashMap<String, Object>();
                    row.put("id",rs.getString("id"));
                    row.put("titulo",rs.getString("titulo"));
                    return row;
                }
            }
        );
        return religion;
    }
    
    
    //Obtiene la lista de puestos
    @Override
    public ArrayList<HashMap<String, Object>> getFacNomina_ISR(Integer id_empresa) {
        String sql_to_query = "select id,titulo, tasa from gral_isr  where borrado_logico=false AND gral_emp_id=? order by titulo";
        ArrayList<HashMap<String, Object>> religion = (ArrayList<HashMap<String, Object>>) this.jdbcTemplate.query(
            sql_to_query,
            new Object[]{new Integer(id_empresa)}, new RowMapper(){
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    HashMap<String, Object> row = new HashMap<String, Object>();
                    row.put("id",rs.getString("id"));
                    row.put("titulo",rs.getString("titulo"));
                    row.put("tasa",StringHelper.roundDouble(rs.getString("tasa"), 2));
                    return row;
                }
            }
        );
        return religion;
    }
    
    
    //Obtiene todos los departamentos de la empresa
    @Override
    public ArrayList<HashMap<String, Object>> getFacNomina_Departamentos(Integer id_empresa) {
        String sql_to_query = "SELECT id, titulo FROM gral_deptos WHERE borrado_logico=false AND vigente=true AND gral_emp_id=? order by titulo";
        
        ArrayList<HashMap<String, Object>> hm = (ArrayList<HashMap<String, Object>>) this.jdbcTemplate.query(
            sql_to_query,
            new Object[]{new Integer(id_empresa)}, new RowMapper(){
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    HashMap<String, Object> row = new HashMap<String, Object>();
                    row.put("id",rs.getString("id"));
                    row.put("titulo",rs.getString("titulo"));
                    return row;
                }
            }
        );
        return hm;
    }
    
    
    //Obtiene todas las Percepciones disponibles
    @Override
    public ArrayList<HashMap<String, Object>> getFacNomina_Percepciones(Integer tipo, Integer id_reg, Integer IdEmpleado, Integer idEmpresa) {
        String sql_to_query="";
        
        if(tipo==1){
            //Query para obtener todas las percepciones de la Empresa
            sql_to_query=""
            + "SELECT "
                + "nom_percep.id, "
                + "(case when nom_percep_tipo.clave is null then '' else (case when nom_percep_tipo.clave<>'' then nom_percep_tipo.clave||' ' else '' end) end)||upper(nom_percep_tipo.titulo) AS tipo_percep,"
                + "(case when nom_percep.clave is null then '' else (case when nom_percep.clave<>'' then nom_percep.clave||' ' else '' end) end)||upper(nom_percep.titulo) AS percepcion, "
                + "0::double precision AS gravado,"
                + "0::double precision excento  "
            + "FROM nom_percep "
            + "JOIN nom_percep_tipo ON nom_percep_tipo.id=nom_percep.nom_percep_tipo_id "
            + "WHERE nom_percep.gral_emp_id="+idEmpresa+" AND nom_percep.activo=true AND nom_percep.borrado_logico=false ORDER BY nom_percep.id;";            
        }
        
        if(tipo==2){
            if(IdEmpleado>0){
                //Query para obtener las percepciones asignadas a un empleado desde el Catalogo de Empleados
                sql_to_query=""
                + "SELECT "
                    + "nom_percep.id, "
                    + "(case when nom_percep_tipo.clave is null then '' else (case when nom_percep_tipo.clave<>'' then nom_percep_tipo.clave||' ' else '' end) end)||upper(nom_percep_tipo.titulo) AS tipo_percep,"
                    + "(case when nom_percep.clave is null then '' else (case when nom_percep.clave<>'' then nom_percep.clave||' ' else '' end) end)||upper(nom_percep.titulo) AS percepcion, "
                    + "0::double precision AS gravado,"
                    + "0::double precision excento  "
                + "FROM nom_percep "
                + "JOIN nom_percep_tipo ON nom_percep_tipo.id=nom_percep.nom_percep_tipo_id "
                + "JOIN gral_empleado_percep ON (gral_empleado_percep.nom_percep_id=nom_percep.id AND gral_empleado_percep.gral_empleado_id="+IdEmpleado+") "
                + "WHERE nom_percep.gral_emp_id="+idEmpresa+" AND nom_percep.activo=true AND nom_percep.borrado_logico=false ORDER BY nom_percep.id;";
            }
        }
        
        if(tipo==3){
            //Obtener las Percepciones configuradas para la Nomina de Empleado de un periodo en especifico
            sql_to_query=""
            + "SELECT "
                + "nom_percep.id, "
                + "(case when nom_percep_tipo.clave is null then '' else (case when nom_percep_tipo.clave<>'' then nom_percep_tipo.clave||' ' else '' end) end)||upper(nom_percep_tipo.titulo) AS tipo_percep,"
                + "(case when nom_percep.clave is null then '' else (case when nom_percep.clave<>'' then nom_percep.clave||' ' else '' end) end)||upper(nom_percep.titulo) AS percepcion,"
                + "(CASE WHEN fac_nomina_det_percep.gravado IS NULL THEN 0 ELSE fac_nomina_det_percep.gravado END) AS gravado,"
                + "(CASE WHEN fac_nomina_det_percep.excento IS NULL THEN 0 ELSE fac_nomina_det_percep.excento END) AS excento  "
            + "FROM fac_nomina_det_percep "
            + "JOIN nom_percep ON nom_percep.id=fac_nomina_det_percep.nom_percep_id "
            + "JOIN nom_percep_tipo ON nom_percep_tipo.id=nom_percep.nom_percep_tipo_id  "
            + "WHERE fac_nomina_det_percep.fac_nomina_det_id="+id_reg+" ORDER BY fac_nomina_det_percep.id;";
        }
        
        System.out.println("QueryPercepciones: "+sql_to_query);
        ArrayList<HashMap<String,Object>>hm=(ArrayList<HashMap<String,Object>>)this.jdbcTemplate.query(
            sql_to_query,
            new Object[]{},new RowMapper(){
                @Override
                public Object mapRow(ResultSet rs,int rowNum)throws SQLException{
                 HashMap<String,Object>row=new HashMap<String,Object>();
                 row.put("id",rs.getString("id"));
                 row.put("tipo_percep",rs.getString("tipo_percep"));
                 row.put("percepcion",rs.getString("percepcion"));
                 row.put("m_gravado",StringHelper.roundDouble(rs.getDouble("gravado"), 2));
                 row.put("m_excento",StringHelper.roundDouble(rs.getDouble("excento"), 2));
                 return row;
                }
            }
        );
        return hm;
    }
    
    
    //Obtiene todas las Percepciones disponibles
    @Override
    public ArrayList<HashMap<String, Object>> getFacNomina_Deducciones(Integer tipo, Integer id_reg, Integer IdEmpleado, Integer idEmpresa) {
        String sql_to_query="";
        
        if(tipo==1){
            //Query para obtener todas las Deducciones de la Empresa
            sql_to_query=""
            + "SELECT "
                + "nom_deduc.id, "
                + "nom_deduc_tipo.id AS nom_deduc_id, "
                + "(case when nom_deduc_tipo.clave is null then '' else (case when nom_deduc_tipo.clave<>'' then nom_deduc_tipo.clave||' ' else '' end) end)||upper(nom_deduc_tipo.titulo) AS tipo_deduc,"
                + "(case when nom_deduc.clave is null then '' else (case when nom_deduc.clave<>'' then nom_deduc.clave||' ' else '' end) end)||upper(nom_deduc.titulo) AS deduccion,  "
                + "0::double precision AS gravado,"
                + "0::double precision excento  "
            + "FROM nom_deduc "
            + "JOIN nom_deduc_tipo ON nom_deduc_tipo.id=nom_deduc.nom_deduc_tipo_id "
            + "WHERE nom_deduc.gral_emp_id="+idEmpresa+" AND nom_deduc.activo=true AND nom_deduc.borrado_logico=false ORDER BY nom_deduc.id;";
        }
        
        if(tipo==2){
            if(IdEmpleado>0){
                //Query para obtener las Deducciones asignadas a un empleado desde el Catalogo de Empleados
                sql_to_query=""
                + "SELECT "
                    + "nom_deduc.id, "
                    + "nom_deduc_tipo.id AS nom_deduc_id, "
                    + "(case when nom_deduc_tipo.clave is null then '' else (case when nom_deduc_tipo.clave<>'' then nom_deduc_tipo.clave||' ' else '' end) end)||upper(nom_deduc_tipo.titulo) AS tipo_deduc,"
                    + "(case when nom_deduc.clave is null then '' else (case when nom_deduc.clave<>'' then nom_deduc.clave||' ' else '' end) end)||upper(nom_deduc.titulo) AS deduccion,"
                    + "0::double precision AS gravado,"
                    + "0::double precision excento  "
                + "FROM nom_deduc "
                + "JOIN nom_deduc_tipo ON nom_deduc_tipo.id=nom_deduc.nom_deduc_tipo_id "
                + "JOIN gral_empleado_deduc ON (gral_empleado_deduc.nom_deduc_id=nom_deduc.id AND gral_empleado_deduc.gral_empleado_id="+IdEmpleado+") "
                + "WHERE nom_deduc.gral_emp_id="+idEmpresa+" AND nom_deduc.activo=true AND nom_deduc.borrado_logico=false ORDER BY nom_deduc.id;";
            }
        }
        
        if(tipo==3){
            sql_to_query=""
            + "SELECT "
                + "nom_deduc.id, "
                + "nom_deduc_tipo.id AS nom_deduc_id, "
                + "(case when nom_deduc_tipo.clave is null then '' else (case when nom_deduc_tipo.clave<>'' then nom_deduc_tipo.clave||' ' else '' end) end)||upper(nom_deduc_tipo.titulo) AS tipo_deduc,"
                + "(case when nom_deduc.clave is null then '' else (case when nom_deduc.clave<>'' then nom_deduc.clave||' ' else '' end) end)||upper(nom_deduc.titulo) AS deduccion, "
                + "(CASE WHEN fac_nomina_det_deduc.gravado IS NULL THEN 0 ELSE fac_nomina_det_deduc.gravado END) AS gravado,"
                + "(CASE WHEN fac_nomina_det_deduc.excento IS NULL THEN 0 ELSE fac_nomina_det_deduc.excento END) AS excento "
            + "FROM fac_nomina_det_deduc "
            + "JOIN nom_deduc ON nom_deduc.id=fac_nomina_det_deduc.nom_deduc_id "
            + "JOIN nom_deduc_tipo ON nom_deduc_tipo.id=nom_deduc.nom_deduc_tipo_id "
            + "WHERE fac_nomina_det_deduc.fac_nomina_det_id="+id_reg+" ORDER BY fac_nomina_det_deduc.id;";
        }
        
        System.out.println("QueryDeducciones: "+sql_to_query);
        ArrayList<HashMap<String,Object>>hm=(ArrayList<HashMap<String,Object>>)this.jdbcTemplate.query(
            sql_to_query,
            new Object[]{},new RowMapper(){
                @Override 
                public Object mapRow(ResultSet rs,int rowNum)throws SQLException{
                 HashMap<String,Object>row=new HashMap<String,Object>();
                 row.put("id",rs.getString("id"));
                 row.put("tipo_deduc_id",rs.getString("nom_deduc_id"));
                 row.put("tipo_deduc",rs.getString("tipo_deduc"));
                 row.put("deduccion",rs.getString("deduccion"));
                 row.put("m_gravado",StringHelper.roundDouble(rs.getDouble("gravado"), 2));
                 row.put("m_excento",StringHelper.roundDouble(rs.getDouble("excento"), 2));
                 return row;
                }
            }
        );
        return hm;
    }
    
    
   //Obtiene datos del Empleado para la Nomina cuando es Nuevo
    @Override
    public ArrayList<HashMap<String, Object>> getFacNomina_DataEmpleado(Integer id_empleado) {
        String sql_query = ""
        + "SELECT "
            +"gral_empleados.id AS empleado_id, "
            +"gral_empleados.clave,"
            +"gral_empleados.rfc,"
            +"(CASE WHEN nombre_pila IS NULL THEN '' ELSE nombre_pila END)||' '||(CASE WHEN apellido_paterno IS NULL THEN '' ELSE apellido_paterno END)||' '||(CASE WHEN apellido_materno IS NULL THEN '' ELSE apellido_materno END) AS empleado, "
            +"gral_empleados.imss, "
            +"gral_empleados.infonavit, "
            +"gral_empleados.curp, "
            +"gral_empleados.rfc, "
            +"to_char(gral_empleados.fecha_nacimiento,'yyyy-mm-dd') AS fecha_nacimiento, "
            +"to_char(gral_empleados.fecha_ingreso,'yyyy-mm-dd') AS fecha_ingreso, "
            +"gral_empleados.gral_depto_id AS depto_id, "
            +"gral_empleados.gral_puesto_id, "
            +"gral_empleados.telefono, "
            +"gral_empleados.correo_personal, "
            +"gral_empleados.gral_pais_id, "
            +"gral_empleados.gral_edo_id, "
            +"gral_empleados.gral_mun_id, "
            +"gral_empleados.calle, "
            +"gral_empleados.numero, "
            +"gral_empleados.colonia, "
            +"gral_empleados.cp, "
            +"gral_empleados.no_int, "
            +"gral_empleados.nom_regimen_contratacion_id AS regimen_id, "
            +"gral_empleados.nom_periodicidad_pago_id AS periodo_pago_id, "
            +"gral_empleados.nom_riesgo_puesto_id AS riesgo_id, "
            +"gral_empleados.nom_tipo_contrato_id AS tipo_contrato_id, "
            +"gral_empleados.nom_tipo_jornada_id AS tipo_jornada_id, "
            +"gral_empleados.tes_ban_id AS banco_id, "
            +"gral_empleados.clabe,"
            +"gral_empleados.salario_base,"
            +"gral_empleados.salario_integrado AS salario_int, "
            +"gral_empleados.registro_patronal AS reg_patronal "
        +"FROM gral_empleados "
        +"WHERE gral_empleados.borrado_logico=false AND gral_empleados.id=?;";
        
        System.out.println("Ejecutando query getEmpleado:"+ sql_query);
        System.out.println("Obteniendo datos del empleado: "+id_empleado);
        
        ArrayList<HashMap<String, Object>> empleado = (ArrayList<HashMap<String, Object>>) this.jdbcTemplate.query(
            sql_query,
            new Object[]{new Integer(id_empleado)}, new RowMapper() {
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    HashMap<String, Object> row = new HashMap<String, Object>();
                    row.put("empleado_id",rs.getInt("empleado_id"));
                    row.put("clave",rs.getString("clave"));
                    row.put("rfc",rs.getString("rfc"));
                    row.put("empleado",rs.getString("empleado"));
                    row.put("imss",rs.getString("imss"));
                    row.put("infonavit",rs.getString("infonavit"));
                    row.put("curp",rs.getString("curp"));
                    row.put("rfc",rs.getString("rfc"));
                    row.put("fecha_nacimiento",rs.getString("fecha_nacimiento"));
                    row.put("fecha_ingreso",rs.getString("fecha_ingreso"));
                    row.put("depto_id",String.valueOf(rs.getInt("depto_id")));
                    row.put("puesto_id",rs.getString("gral_puesto_id"));
                    row.put("telefono",rs.getString("telefono"));
                    row.put("correo_personal",rs.getString("correo_personal"));
                    row.put("gral_pais_id",rs.getString("gral_pais_id"));
                    row.put("gral_edo_id",rs.getString("gral_edo_id"));
                    row.put("gral_mun_id",rs.getString("gral_mun_id"));
                    row.put("calle",rs.getString("calle"));
                    row.put("numero",rs.getString("numero"));
                    row.put("colonia",rs.getString("colonia"));
                    row.put("cp",rs.getString("cp"));
                    
                    row.put("no_int",rs.getString("no_int"));
                    row.put("regimen_id",String.valueOf(rs.getInt("regimen_id")));
                    row.put("periodo_pago_id",String.valueOf(rs.getInt("periodo_pago_id")));
                    row.put("riesgo_id",String.valueOf(rs.getInt("riesgo_id")));
                    row.put("tipo_contrato_id",String.valueOf(rs.getInt("tipo_contrato_id")));
                    row.put("tipo_jornada_id",String.valueOf(rs.getInt("tipo_jornada_id")));
                    row.put("banco_id",String.valueOf(rs.getInt("banco_id")));
                    row.put("clabe",rs.getString("clabe"));
                    row.put("salario_base",StringHelper.roundDouble(rs.getDouble("salario_base"),2));
                    row.put("salario_int",StringHelper.roundDouble(rs.getDouble("salario_int"),2));
                    row.put("reg_patronal",rs.getString("reg_patronal"));
                    row.put("validado","false");
                    return row;
                }
            }
        );
        return empleado;
    }
    
    
    //Obtiene los datos de un Periodo en especifico
    @Override
    public ArrayList<HashMap<String, Object>> getFacNomina_DataPeriodo(Integer id_periodo, Integer idEmpresa) {
        String sql_to_query=""
        + "SELECT "
            + "nom_periodos_conf_det.id,"
            + "nom_periodos_conf_det.titulo AS periodo,"
            + "(CASE WHEN nom_periodos_conf_det.fecha_ini IS NULL THEN '' ELSE nom_periodos_conf_det.fecha_ini::character varying END) AS fecha_ini,"
            + "(CASE WHEN nom_periodos_conf_det.fecha_fin IS NULL THEN '' ELSE nom_periodos_conf_det.fecha_fin::character varying END) AS fecha_fin "
        + "FROM nom_periodos_conf "
        + "JOIN nom_periodos_conf_det ON nom_periodos_conf_det.nom_periodos_conf_id=nom_periodos_conf.id "
        + "WHERE nom_periodos_conf.gral_emp_id=? AND nom_periodos_conf_det.id=? ORDER BY nom_periodos_conf_det.id;";
        ArrayList<HashMap<String,Object>>hm=(ArrayList<HashMap<String,Object>>)this.jdbcTemplate.query(
            sql_to_query,
            new Object[]{new Integer(idEmpresa), new Integer(id_periodo)},new RowMapper(){
                @Override
                public Object mapRow(ResultSet rs,int rowNum)throws SQLException{
                 HashMap<String,Object>row=new HashMap<String,Object>();
                 row.put("id",rs.getString("id"));
                 row.put("periodo",rs.getString("periodo"));
                 row.put("fecha_ini",rs.getString("fecha_ini"));
                 row.put("fecha_fin",rs.getString("fecha_fin"));
                 return row;
                }
            }
        );
        return hm;
    }
    
    
    
    //Obtiene los datos de la Nomina de un Empleado al Editar
    @Override
    public ArrayList<HashMap<String, Object>> getFacNomina_DataNomina(Integer id_reg, Integer id_empleado) {
        String sql_to_query=""
        + "SELECT "
            + "id,"
            + "fac_nomina_id AS fac_nom_id,"
            + "gral_empleado_id AS empleado_id,"
            + "no_empleado,"
            + "rfc,"
            + "nombre,"
            + "curp,"
            + "gral_depto_id AS depto_id,"
            + "gral_puesto_id AS puesto_id,"
            + "(CASE WHEN fecha_contrato IS NULL THEN '' ELSE fecha_contrato::character varying END) AS fecha_contrato,"
            + "antiguedad,"
            + "nom_regimen_contratacion_id AS regimen_id,"
            + "nom_tipo_contrato_id AS tipo_contrato_id,"
            + "nom_tipo_jornada_id AS tipo_jornada_id,"
            + "nom_periodicidad_pago_id AS periodicidad_id,"
            + "clabe,"
            + "tes_ban_id AS banco_id,"
            + "nom_riesgo_puesto_id AS riesgo_id,"
            + "imss,"
            + "reg_patronal,"
            + "salario_base,"
            + "salario_integrado,"
            + "(CASE WHEN fecha_ini_pago IS NULL THEN '' ELSE fecha_ini_pago::character varying END) AS f_ini_pago,"
            + "(CASE WHEN fecha_fin_pago IS NULL THEN '' ELSE fecha_fin_pago::character varying END) AS f_fin_pago,"
            + "no_dias_pago,"
            + "concepto_descripcion,"
            + "concepto_unidad,"
            + "concepto_cantidad,"
            + "concepto_valor_unitario,"
            + "concepto_importe,"
            + "descuento,"
            + "motivo_descuento,"
            + "gral_isr_id AS isr_id,"
            + "importe_retencion,"
            + "comp_subtotal,"
            + "comp_descuento,"
            + "comp_retencion,"
            + "comp_total,"
            + "percep_total_gravado,"
            + "percep_total_excento,"
            + "deduc_total_gravado,"
            + "deduc_total_excento,"
            + "validado "
        + "FROM fac_nomina_det "
        + "WHERE id=? AND gral_empleado_id=?;";
        ArrayList<HashMap<String,Object>>hm=(ArrayList<HashMap<String,Object>>)this.jdbcTemplate.query(
            sql_to_query,
            new Object[]{new Integer(id_reg), new Integer(id_empleado)},new RowMapper(){
                @Override
                public Object mapRow(ResultSet rs,int rowNum)throws SQLException{
                HashMap<String,Object>row=new HashMap<String,Object>();
                row.put("id",rs.getInt("id"));
                row.put("fac_nom_id",rs.getInt("fac_nom_id"));
                row.put("empleado_id",rs.getInt("empleado_id"));
                row.put("no_empleado",rs.getString("no_empleado"));
                row.put("rfc",rs.getString("rfc"));
                row.put("nombre",rs.getString("nombre"));
                row.put("curp",rs.getString("curp"));
                row.put("depto_id",rs.getInt("depto_id"));
                row.put("puesto_id",rs.getInt("puesto_id"));
                row.put("fecha_contrato",rs.getString("fecha_contrato"));
                row.put("antiguedad",rs.getString("antiguedad"));
                row.put("regimen_id",rs.getInt("regimen_id"));
                row.put("tipo_contrato_id",rs.getInt("tipo_contrato_id"));
                row.put("tipo_jornada_id",rs.getInt("tipo_jornada_id"));
                row.put("periodicidad_id",rs.getInt("periodicidad_id"));
                row.put("clabe",rs.getString("clabe"));
                row.put("banco_id",rs.getInt("banco_id"));
                row.put("riesgo_id",rs.getInt("riesgo_id"));
                row.put("imss",rs.getString("imss"));
                row.put("reg_patronal",rs.getString("reg_patronal"));
                row.put("salario_base",StringHelper.roundDouble(rs.getString("salario_base"),2));
                row.put("salario_integrado",StringHelper.roundDouble(rs.getString("salario_integrado"),2));
                row.put("f_ini_pago",rs.getString("f_ini_pago"));
                row.put("f_fin_pago",rs.getString("f_fin_pago"));
                row.put("no_dias_pago",rs.getString("no_dias_pago"));
                row.put("concepto_descripcion",rs.getString("concepto_descripcion"));
                row.put("concepto_unidad",rs.getString("concepto_unidad"));
                row.put("concepto_cantidad",StringHelper.roundDouble(rs.getString("concepto_cantidad"),2));
                row.put("concepto_valor_unitario",StringHelper.roundDouble(rs.getString("concepto_valor_unitario"),2));
                row.put("concepto_importe",StringHelper.roundDouble(rs.getString("concepto_importe"),2));
                row.put("descuento",StringHelper.roundDouble(rs.getString("descuento"),2));
                row.put("motivo_descuento",rs.getString("motivo_descuento"));
                row.put("isr_id",rs.getInt("isr_id"));
                row.put("importe_retencion",StringHelper.roundDouble(rs.getString("importe_retencion"),2));
                row.put("comp_subtotal",StringHelper.roundDouble(rs.getString("comp_subtotal"),2));
                row.put("comp_descuento",StringHelper.roundDouble(rs.getString("comp_descuento"),2));
                row.put("comp_retencion",StringHelper.roundDouble(rs.getString("comp_retencion"),2));
                row.put("comp_total",StringHelper.roundDouble(rs.getString("comp_total"),2));
                row.put("percep_total_gravado",StringHelper.roundDouble(rs.getString("percep_total_gravado"),2));
                row.put("percep_total_excento",StringHelper.roundDouble(rs.getString("percep_total_excento"),2));
                row.put("deduc_total_gravado",StringHelper.roundDouble(rs.getString("deduc_total_gravado"),2));
                row.put("deduc_total_excento",StringHelper.roundDouble(rs.getString("deduc_total_excento"),2));
                row.put("validado",String.valueOf(rs.getBoolean("validado")).toLowerCase());
                 return row;
                }
            }
        );
        return hm;
    }
    
    
    
    //Obtiene las Horas Extras de un empleado de un Periodo especifico
    @Override
    public ArrayList<HashMap<String, Object>> getFacNomina_HorasExtras(Integer id_nom_det) {
        String sql_to_query="";
        
        sql_to_query="SELECT id,nom_tipo_hrs_extra_id AS tipo_he_id, no_dias, no_hrs, importe FROM fac_nomina_det_hrs_extra WHERE fac_nomina_det_id=? ORDER BY id;";
        
        System.out.println("QueryHorasExtras: "+sql_to_query);
        ArrayList<HashMap<String,Object>>hm=(ArrayList<HashMap<String,Object>>)this.jdbcTemplate.query(
            sql_to_query,
            new Object[]{new Integer(id_nom_det)},new RowMapper(){
                @Override 
                public Object mapRow(ResultSet rs,int rowNum)throws SQLException{
                 HashMap<String,Object>row=new HashMap<String,Object>();
                 row.put("id",rs.getInt("id"));
                 row.put("tipo_he_id",rs.getInt("tipo_he_id"));
                 row.put("no_dias",StringHelper.roundDouble(rs.getDouble("no_dias"), 2));
                 row.put("no_hrs",StringHelper.roundDouble(rs.getDouble("no_hrs"), 2));
                 row.put("importe",StringHelper.roundDouble(rs.getDouble("importe"), 2));
                 return row;
                }
            }
        );
        return hm;
    }
    
    
    
    //Obtiene las Horas Extras de un empleado de un Periodo especifico
    @Override
    public ArrayList<HashMap<String, Object>> getFacNomina_Incapacidades(Integer id_nom_det) {
        String sql_to_query="SELECT id, nom_tipo_incapacidad_id AS tipo_incapa_id, no_dias, importe FROM fac_nomina_det_incapa WHERE fac_nomina_det_id=? ORDER BY id;";
        
        System.out.println("QueryIncapacidades: "+sql_to_query);
        ArrayList<HashMap<String,Object>>hm=(ArrayList<HashMap<String,Object>>)this.jdbcTemplate.query(
            sql_to_query,
            new Object[]{new Integer(id_nom_det)},new RowMapper(){
                @Override 
                public Object mapRow(ResultSet rs,int rowNum)throws SQLException{
                 HashMap<String,Object>row=new HashMap<String,Object>();
                 row.put("id",rs.getInt("id"));
                 row.put("tipo_incapa_id",rs.getInt("tipo_incapa_id"));
                 row.put("no_dias",StringHelper.roundDouble(rs.getDouble("no_dias"), 2));
                 row.put("importe",StringHelper.roundDouble(rs.getDouble("importe"), 2));
                 return row;
                }
            }
        );
        return hm;
    }
    
    
    
    
    
    //Obtener registros para generar Nomina CFDI
    @Override
    public ArrayList<HashMap<String, Object>> getFacNomina_Registros(Integer id) {
        String sql_to_query="SELECT fac_nomina_det.id AS id_reg, fac_nomina_det.gral_empleado_id AS empleado_id FROM fac_nomina_det WHERE fac_nomina_det.fac_nomina_id=? AND validado=true AND facturado=false ORDER BY id;";
        ArrayList<HashMap<String,Object>>hm=(ArrayList<HashMap<String,Object>>)this.jdbcTemplate.query(
            sql_to_query,
            new Object[]{new Integer(id)},new RowMapper(){
                @Override
                public Object mapRow(ResultSet rs,int rowNum)throws SQLException{
                 HashMap<String,Object>row=new HashMap<String,Object>();
                 row.put("id_reg",rs.getInt("id_reg"));
                 row.put("empleado_id",rs.getInt("empleado_id"));
                 return row;
                }
            }
        );
        return hm;
    }
    
    
    //Obtiene la lista de conceptos para la CFDI de NOMINA
    @Override
    public ArrayList<LinkedHashMap<String, String>> getFacNomina_ConceptosXml(Integer id, Integer id_empleado) {
        String sql_query = "SELECT ''::character varying AS no_identificacion, concepto_descripcion, concepto_unidad, concepto_cantidad, concepto_valor_unitario, concepto_importe FROM fac_nomina_det WHERE id=? AND gral_empleado_id=?;";
        //System.out.println("ConceptosXml: "+sql_query);
        
        ArrayList<LinkedHashMap<String, String>> hm_conceptos = (ArrayList<LinkedHashMap<String, String>>) this.jdbcTemplate.query(
            sql_query,
            new Object[]{new Integer(id), new Integer(id_empleado)}, new RowMapper() {
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    LinkedHashMap<String, String> row = new LinkedHashMap<String, String>();
                    row.put("noIdentificacion",StringHelper.normalizaString(StringHelper.remueve_tildes(rs.getString("no_identificacion"))));
                    row.put("descripcion",StringHelper.normalizaString(StringHelper.remueve_tildes(rs.getString("concepto_descripcion"))));
                    row.put("unidad",StringHelper.normalizaString(StringHelper.remueve_tildes(rs.getString("concepto_unidad"))));
                    row.put("cantidad",StringHelper.roundDouble(rs.getString("concepto_cantidad"),2));
                    row.put("valorUnitario",StringHelper.roundDouble(rs.getDouble("concepto_valor_unitario"),4) );
                    row.put("importe",StringHelper.roundDouble(rs.getDouble("concepto_importe"),4) ); 
                    return row;
                }
            }
        );
        
        return hm_conceptos;
    }
    
    
    //Obtiene los impuestos retenidos de la nomina
    @Override
    public ArrayList<LinkedHashMap<String, String>> getFacNomina_ImpuestosRetenidosXml(Integer id, Integer id_empleado) {
        String sql_query = "SELECT gral_isr.titulo AS impuesto, fac_nomina_det.importe_retencion FROM fac_nomina_det JOIN gral_isr ON gral_isr.id=fac_nomina_det.gral_isr_id WHERE fac_nomina_det.id=? AND fac_nomina_det.gral_empleado_id=?;";
        //System.out.println("ConceptosXml: "+sql_query);
        ArrayList<LinkedHashMap<String, String>> hm = (ArrayList<LinkedHashMap<String, String>>) this.jdbcTemplate.query(
            sql_query,
            new Object[]{new Integer(id), new Integer(id_empleado)}, new RowMapper() {
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    LinkedHashMap<String, String> row = new LinkedHashMap<String, String>();
                    row.put("impuesto",StringHelper.normalizaString(StringHelper.remueve_tildes(rs.getString("impuesto"))));
                    row.put("importe",StringHelper.roundDouble(rs.getDouble("importe_retencion"),2) );
                    return row;
                }
            }
        );
        return hm;
    }
    
    
    
    //Obtener datos para el Comprobante, Receptor y Complemento de Nomina
    @Override
    public HashMap<String, String> getFacNomina_DataXml(Integer id, Integer id_empleado) {
        String sql_to_query = ""
        + "SELECT "
            + "fac_nomina_det.gral_empleado_id, "
            + "(CASE WHEN fac_metodos_pago.clave_sat IS NULL THEN '' ELSE fac_metodos_pago.clave_sat END) AS metodo_pago, "
            + "fac_nomina.forma_pago, "
            + "fac_nomina.fecha_pago::character varying AS fecha_pago,"
            + "gral_mon.iso_4217 AS moneda, "
            + "gral_mon.iso_4217_anterior AS moneda2, "
            + "gral_mon.simbolo AS simbolo_moneda, "
            + "gral_mon.descripcion_abr AS moneda_abr,"
            + "fac_nomina.tipo_cambio, "
            + "fac_nomina_det.no_empleado, "
            + "fac_nomina_det.nombre, "
            + "fac_nomina_det.rfc, "
            + "fac_nomina_det.curp, "
            + "(CASE WHEN gral_empleados.calle IS NULL THEN '' ELSE gral_empleados.calle END ) AS calle,"
            + "(CASE WHEN gral_empleados.numero IS NULL THEN '' ELSE gral_empleados.numero END ) AS numero_exterior,"
            + "(CASE WHEN gral_empleados.no_int IS NULL THEN '' ELSE gral_empleados.no_int END ) AS numero_interior,"
            + "(CASE WHEN gral_empleados.colonia IS NULL THEN '' ELSE gral_empleados.colonia END ) AS colonia,"
            + "(CASE WHEN gral_empleados.calle IS NULL THEN '' ELSE gral_empleados.calle END ) AS calle,"
            + "(CASE WHEN gral_empleados.cp IS NULL THEN '' ELSE gral_empleados.cp END ) AS cp,"
            + "(CASE WHEN gral_mun.titulo IS NULL THEN '' ELSE gral_mun.titulo END ) AS municipio,"
            + "(CASE WHEN gral_edo.titulo IS NULL THEN '' ELSE gral_edo.titulo END ) AS estado,"
            + "(CASE WHEN gral_pais.titulo IS NULL THEN '' ELSE gral_pais.titulo END ) AS pais,"
            + "(CASE WHEN gral_deptos.titulo IS NULL THEN '' ELSE gral_deptos.titulo END ) AS departamento, "
            + "(CASE WHEN gral_puestos.titulo IS NULL THEN '' ELSE gral_puestos.titulo END ) AS puesto,"
            + "(CASE WHEN fac_nomina_det.fecha_contrato IS NULL THEN '' ELSE fac_nomina_det.fecha_contrato::character varying END ) AS fecha_contrato,"
            + "(CASE WHEN (fac_nomina_det.antiguedad IS NOT NULL AND fac_nomina_det.antiguedad <>0) THEN fac_nomina_det.antiguedad::character varying ELSE '' END ) AS antiguedad,"
            + "(CASE WHEN nom_regimen_contratacion.clave IS NULL THEN '' ELSE nom_regimen_contratacion.clave END ) AS regimen_contratacion,"
            + "(CASE WHEN nom_regimen_contratacion.titulo IS NULL THEN '' ELSE nom_regimen_contratacion.titulo END ) AS regimen_contratacion_titulo,"
            
            + "(CASE WHEN nom_tipo_contrato.titulo IS NULL THEN '' ELSE nom_tipo_contrato.titulo END ) AS tipo_contrato,"
            + "(CASE WHEN nom_tipo_jornada.titulo IS NULL THEN '' ELSE nom_tipo_jornada.titulo END ) AS tipo_jornada,"
            + "(CASE WHEN nom_periodicidad_pago.titulo IS NULL THEN '' ELSE nom_periodicidad_pago.titulo END ) AS periodicidad_pago,"
            + "(CASE WHEN fac_nomina_det.clabe IS NULL THEN '' ELSE fac_nomina_det.clabe::character varying END ) AS clabe,"
            + "(CASE WHEN tes_ban.descripcion IS NULL THEN '' ELSE tes_ban.descripcion END ) AS nombre_banco,"
            + "(CASE WHEN tes_ban.clave IS NULL THEN '' ELSE tes_ban.clave END ) AS banco,"
            + "(CASE WHEN nom_riesgo_puesto.clave IS NULL THEN '' ELSE nom_riesgo_puesto.clave END ) AS riesgo_puesto,"
            + "(CASE WHEN nom_riesgo_puesto.titulo IS NULL THEN '' ELSE nom_riesgo_puesto.titulo END ) AS riesgo_puesto_titulo,"
                
            + "(CASE WHEN fac_nomina_det.imss IS NULL THEN '' ELSE fac_nomina_det.imss::character varying END ) AS imss,"
            + "(CASE WHEN fac_nomina_det.reg_patronal IS NULL THEN '' ELSE fac_nomina_det.reg_patronal::character varying END ) AS reg_patronal,"
            + "(CASE WHEN fac_nomina_det.salario_base IS NULL THEN '' ELSE fac_nomina_det.salario_base::character varying END ) AS salario_base,"
            + "(CASE WHEN fac_nomina_det.salario_integrado IS NULL THEN '' ELSE fac_nomina_det.salario_integrado::character varying END ) AS salario_integrado,"
            + "(CASE WHEN fac_nomina_det.fecha_ini_pago IS NULL THEN '' ELSE fac_nomina_det.fecha_ini_pago::character varying END ) AS fecha_ini_pago,"
            + "(CASE WHEN fac_nomina_det.fecha_fin_pago IS NULL THEN '' ELSE fac_nomina_det.fecha_fin_pago::character varying END ) AS fecha_fin_pago,"
            + "(CASE WHEN fac_nomina_det.no_dias_pago IS NULL THEN '' ELSE fac_nomina_det.no_dias_pago::character varying END ) AS no_dias_pago,"
            + "(CASE WHEN fac_nomina_det.descuento IS NULL THEN '' ELSE fac_nomina_det.descuento::character varying END ) AS descuento,"
            + "(CASE WHEN fac_nomina_det.motivo_descuento IS NULL THEN '' ELSE fac_nomina_det.motivo_descuento::character varying END ) AS motivo_descuento,"
            + "(CASE WHEN fac_nomina_det.comp_subtotal IS NULL THEN '0' ELSE fac_nomina_det.comp_subtotal::character varying END ) AS comp_subtotal,"
            + "(CASE WHEN fac_nomina_det.comp_descuento IS NULL THEN '0' ELSE fac_nomina_det.comp_descuento::character varying END ) AS comp_descuento,"
            + "(CASE WHEN fac_nomina_det.comp_retencion IS NULL THEN '0' ELSE fac_nomina_det.comp_retencion::character varying END ) AS comp_retencion,"
            + "(CASE WHEN fac_nomina_det.comp_total IS NULL THEN '0' ELSE fac_nomina_det.comp_total::character varying END ) AS comp_total,"
            + "(CASE WHEN fac_nomina_det.percep_total_gravado IS NULL THEN '0' ELSE fac_nomina_det.percep_total_gravado::character varying END ) AS percep_total_gravado,"
            + "(CASE WHEN fac_nomina_det.percep_total_excento IS NULL THEN '0' ELSE fac_nomina_det.percep_total_excento::character varying END ) AS percep_total_excento,"
            + "(CASE WHEN fac_nomina_det.deduc_total_gravado IS NULL THEN '0' ELSE fac_nomina_det.deduc_total_gravado::character varying END ) AS deduc_total_gravado,"
            + "(CASE WHEN fac_nomina_det.deduc_total_excento IS NULL THEN '0' ELSE fac_nomina_det.deduc_total_excento::character varying END ) AS deduc_total_excento,"
            + "(CASE WHEN fac_nomina_det.cancelado IS NULL THEN false ELSE fac_nomina_det.cancelado END ) AS cancelado "
        + "FROM fac_nomina_det "
        + "JOIN fac_nomina ON fac_nomina.id=fac_nomina_det.fac_nomina_id "
        + "LEFT JOIN gral_empleados ON gral_empleados.id=fac_nomina_det.gral_empleado_id "
        + "LEFT JOIN fac_metodos_pago ON fac_metodos_pago.id=fac_nomina.fac_metodos_pago_id "
        + "LEFT JOIN gral_mon ON gral_mon.id=fac_nomina.gral_mon_id "
        + "LEFT JOIN gral_pais ON gral_pais.id = gral_empleados.gral_pais_id "
        + "LEFT JOIN gral_edo ON gral_edo.id = gral_empleados.gral_edo_id "
        + "LEFT JOIN gral_mun ON gral_mun.id = gral_empleados.gral_mun_id "
        + "LEFT JOIN gral_deptos ON gral_deptos.id = fac_nomina_det.gral_depto_id "
        + "LEFT JOIN gral_puestos ON gral_puestos.id = fac_nomina_det.gral_puesto_id "
        + "LEFT JOIN nom_regimen_contratacion ON nom_regimen_contratacion.id = fac_nomina_det.nom_regimen_contratacion_id "
        + "LEFT JOIN nom_tipo_contrato ON nom_tipo_contrato.id = fac_nomina_det.nom_tipo_contrato_id "
        + "LEFT JOIN nom_tipo_jornada ON nom_tipo_jornada.id = fac_nomina_det.nom_tipo_jornada_id "
        + "LEFT JOIN nom_periodicidad_pago ON nom_periodicidad_pago.id = fac_nomina_det.nom_periodicidad_pago_id "
        + "LEFT JOIN tes_ban ON tes_ban.id = fac_nomina_det.tes_ban_id "
        + "LEFT JOIN nom_riesgo_puesto ON nom_riesgo_puesto.id = fac_nomina_det.nom_riesgo_puesto_id  "
        + "WHERE fac_nomina_det.id=? AND fac_nomina_det.gral_empleado_id=?;";
        
        HashMap<String, String> hm = (HashMap<String, String>) this.jdbcTemplate.queryForObject(
            sql_to_query, 
            new Object[]{new Integer(id), new Integer(id_empleado)}, new RowMapper() {
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    HashMap<String, String> row = new HashMap<String, String>();
                    row.put("comprobante_attr_condicionesdepago","");
                    row.put("comprobante_attr_formadepago",rs.getString("forma_pago"));
                    row.put("comprobante_attr_descuento",StringHelper.roundDouble(rs.getString("comp_descuento"),2));
                    row.put("comprobante_attr_motivodescuento",rs.getString("motivo_descuento"));
                    row.put("comprobante_attr_subtotal", StringHelper.roundDouble(rs.getString("comp_subtotal"),2));
                    row.put("comprobante_attr_retencion", StringHelper.roundDouble(rs.getString("comp_retencion"),2));
                    row.put("comprobante_attr_total",StringHelper.roundDouble(rs.getString("comp_total"),2));
                    row.put("comprobante_attr_moneda",rs.getString("moneda"));
                    
                    row.put("comprobante_attr_simbolo_moneda",rs.getString("simbolo_moneda"));
                    row.put("comprobante_attr_tc",StringHelper.roundDouble(rs.getString("tipo_cambio"), 4));
                    row.put("comprobante_attr_metododepago",rs.getString("metodo_pago").toUpperCase());
                    row.put("comprobante_attr_numerocuenta","");
                    
                    //Datos del Empleado
                    row.put("numero_control",rs.getString("no_empleado"));
                    row.put("comprobante_receptor_attr_nombre",rs.getString("nombre"));
                    row.put("comprobante_receptor_attr_rfc",rs.getString("rfc"));
                    row.put("comprobante_receptor_domicilio_attr_calle",rs.getString("calle"));
                    row.put("comprobante_receptor_domicilio_attr_noexterior",rs.getString("numero_exterior"));
                    row.put("comprobante_receptor_domicilio_attr_nointerior",rs.getString("numero_interior"));
                    row.put("comprobante_receptor_domicilio_attr_colonia",rs.getString("colonia"));
                    row.put("comprobante_receptor_domicilio_attr_localidad","");
                    row.put("comprobante_receptor_domicilio_attr_referencia","");
                    row.put("comprobante_receptor_domicilio_attr_municipio",rs.getString("municipio"));
                    row.put("comprobante_receptor_domicilio_attr_estado",rs.getString("estado"));
                    row.put("comprobante_receptor_domicilio_attr_pais",rs.getString("pais"));
                    row.put("comprobante_receptor_domicilio_attr_codigopostal",rs.getString("cp"));
                    
                    row.put("comprobante_receptor_attr_curp",rs.getString("curp"));
                    row.put("comprobante_attr_depto",rs.getString("departamento"));
                    row.put("comprobante_attr_puesto",rs.getString("puesto"));
                    row.put("comprobante_attr_fecha_contrato",rs.getString("fecha_contrato"));
                    row.put("comprobante_attr_fecha_antiguedad",rs.getString("antiguedad"));
                    row.put("comprobante_attr_regimen_contratacion",rs.getString("regimen_contratacion"));
                    row.put("comprobante_attr_tipo_contrato",rs.getString("tipo_contrato"));
                    row.put("comprobante_attr_tipo_jornada",rs.getString("tipo_jornada"));
                    row.put("comprobante_attr_periodicidad_pago",rs.getString("periodicidad_pago"));
                    row.put("comprobante_attr_clabe",rs.getString("clabe"));
                    row.put("comprobante_attr_banco",rs.getString("banco"));
                    row.put("comprobante_attr_riesgo_puesto",rs.getString("riesgo_puesto"));
                    row.put("comprobante_attr_imss",rs.getString("imss"));
                    row.put("comprobante_attr_reg_patronal",rs.getString("reg_patronal"));
                    row.put("comprobante_attr_salario_base",StringHelper.roundDouble(rs.getString("salario_base"),2));
                    row.put("comprobante_attr_salario_integrado",StringHelper.roundDouble(rs.getString("salario_integrado"),2));
                    row.put("comprobante_attr_fecha_fecha_pago",rs.getString("fecha_pago"));
                    row.put("comprobante_attr_fecha_ini_pago",rs.getString("fecha_ini_pago"));
                    row.put("comprobante_attr_fecha_fin_pago",rs.getString("fecha_fin_pago"));
                    row.put("comprobante_attr_no_dias_pago",rs.getString("no_dias_pago"));
                    row.put("comprobante_attr_percep_total_gravado",StringHelper.roundDouble(rs.getString("percep_total_gravado"),2));
                    row.put("comprobante_attr_percep_total_excento",StringHelper.roundDouble(rs.getString("percep_total_excento"),2));
                    row.put("comprobante_attr_deduc_total_gravado",StringHelper.roundDouble(rs.getString("deduc_total_gravado"),2));
                    row.put("comprobante_attr_deduc_total_excento",StringHelper.roundDouble(rs.getString("deduc_total_excento"),2));
                    
                    
                    row.put("adenda_id","0");
                    //Este campo es utilizado para la adenda de Quimiproductos
                    row.put("moneda2",rs.getString("moneda2"));
                    row.put("orden_compra","");
                    row.put("comprobante_attr_simbolo_moneda_abr",rs.getString("moneda_abr"));
                    row.put("nombre_banco",rs.getString("nombre_banco"));
                    row.put("riesgo_puesto_titulo",rs.getString("riesgo_puesto_titulo"));
                    row.put("comprobante_cancelado",String.valueOf(rs.getBoolean("cancelado")).toLowerCase());
                    row.put("regimen_contratacion_titulo",String.valueOf(rs.getString("regimen_contratacion_titulo")));
                    return row;
                }
            }
        );
        
        return hm;
    }

    
    
    
    //Obtiene las Percepciones del empleado para la Nomina de un Periodo especifico
    @Override
    public ArrayList<LinkedHashMap<String,String>> getFacNomina_PercepcionesXml(Integer id) {
        String sql_to_query="";
        sql_to_query="SELECT (case when nom_percep_tipo.clave is null then '' else nom_percep_tipo.clave end) tipo_percepcion,(case when nom_percep.clave is null then '' else nom_percep.clave end) AS clave,(case when nom_percep.titulo is null then '' else nom_percep.titulo end) AS percepcion, (CASE WHEN fac_nomina_det_percep.gravado IS NULL THEN 0 ELSE fac_nomina_det_percep.gravado END) AS gravado, (CASE WHEN fac_nomina_det_percep.excento IS NULL THEN 0 ELSE fac_nomina_det_percep.excento END) AS excento FROM fac_nomina_det_percep JOIN nom_percep ON nom_percep.id=fac_nomina_det_percep.nom_percep_id JOIN nom_percep_tipo ON nom_percep_tipo.id=nom_percep.nom_percep_tipo_id WHERE fac_nomina_det_percep.fac_nomina_det_id=? ORDER BY fac_nomina_det_percep.id;";
        
        System.out.println("QueryPercepcionesXml: "+sql_to_query);
        ArrayList<LinkedHashMap<String,String>>hm=(ArrayList<LinkedHashMap<String,String>>)this.jdbcTemplate.query(
            sql_to_query,
            new Object[]{new Integer(id)},new RowMapper(){
                @Override
                public Object mapRow(ResultSet rs,int rowNum)throws SQLException{
                 LinkedHashMap<String,String>row=new LinkedHashMap<String,String>();
                 row.put("TipoPercepcion",rs.getString("tipo_percepcion"));
                 row.put("Clave",rs.getString("clave"));
                 row.put("Concepto",rs.getString("percepcion"));
                 row.put("ImporteGravado",StringHelper.roundDouble(rs.getDouble("gravado"), 2));
                 row.put("ImporteExento",StringHelper.roundDouble(rs.getDouble("excento"), 2));
                 return row;
                }
            }
        );
        return hm;
    }
    
    
    
    //Obtiene las Deducciones del empleado para la Nomina de un Periodo especifico
    @Override
    public ArrayList<LinkedHashMap<String,String>> getFacNomina_DeduccionesXml(Integer id) {
        String sql_to_query="";
        sql_to_query="SELECT (case when nom_deduc_tipo.clave is null then '' else nom_deduc_tipo.clave end) tipo_deduccion,(case when nom_deduc.clave is null then '' else nom_deduc.clave end) AS clave,(case when nom_deduc.titulo is null then '' else nom_deduc.titulo end) AS deduccion,(CASE WHEN fac_nomina_det_deduc.gravado IS NULL THEN 0 ELSE fac_nomina_det_deduc.gravado END) AS gravado,(CASE WHEN fac_nomina_det_deduc.excento IS NULL THEN 0 ELSE fac_nomina_det_deduc.excento END) AS excento FROM fac_nomina_det_deduc JOIN nom_deduc ON nom_deduc.id=fac_nomina_det_deduc.nom_deduc_id JOIN nom_deduc_tipo ON nom_deduc_tipo.id=nom_deduc.nom_deduc_tipo_id WHERE fac_nomina_det_deduc.fac_nomina_det_id=? ORDER BY fac_nomina_det_deduc.id;";
        
        System.out.println("QueryDeduccionesXml: "+sql_to_query);
        ArrayList<LinkedHashMap<String,String>>hm=(ArrayList<LinkedHashMap<String,String>>)this.jdbcTemplate.query(
            sql_to_query,
            new Object[]{new Integer(id)},new RowMapper(){
                @Override
                public Object mapRow(ResultSet rs,int rowNum)throws SQLException{
                 LinkedHashMap<String,String>row=new LinkedHashMap<String,String>();
                 row.put("TipoDeduccion",rs.getString("tipo_deduccion"));
                 row.put("Clave",rs.getString("clave"));
                 row.put("Concepto",rs.getString("deduccion"));
                 row.put("ImporteGravado",StringHelper.roundDouble(rs.getDouble("gravado"), 2));
                 row.put("ImporteExento",StringHelper.roundDouble(rs.getDouble("excento"), 2));
                 return row;
                }
            }
        );
        return hm;
    }
    
    //Obtiene las Incapacidades del empleado para la Nomina de un Periodo especifico
    @Override
    public ArrayList<LinkedHashMap<String,String>> getFacNomina_IncapacidadesXml(Integer id) {
        String sql_to_query="";
        sql_to_query="SELECT (CASE WHEN nom_tipo_incapacidad.clave IS NULL THEN '' ELSE nom_tipo_incapacidad.clave END) AS tipo_incapacidad, nom_tipo_incapacidad.titulo AS titulo_tipo_incapacidad, fac_nomina_det_incapa.no_dias, fac_nomina_det_incapa.importe FROM fac_nomina_det_incapa JOIN nom_tipo_incapacidad ON nom_tipo_incapacidad.id=fac_nomina_det_incapa.nom_tipo_incapacidad_id WHERE fac_nomina_det_incapa.fac_nomina_det_id=? ORDER BY fac_nomina_det_incapa.id;";
        
        System.out.println("QueryIncapacidadesXml: "+sql_to_query);
        ArrayList<LinkedHashMap<String,String>>hm=(ArrayList<LinkedHashMap<String,String>>)this.jdbcTemplate.query(
            sql_to_query,
            new Object[]{new Integer(id)},new RowMapper(){
                @Override
                public Object mapRow(ResultSet rs,int rowNum)throws SQLException{
                 LinkedHashMap<String,String>row=new LinkedHashMap<String,String>();
                 row.put("DiasIncapacidad",rs.getString("no_dias"));
                 row.put("TipoIncapacidad",rs.getString("tipo_incapacidad"));
                 row.put("Descuento",StringHelper.roundDouble(rs.getDouble("importe"), 2));
                 //Este solo se utiliza para el pdf
                 row.put("titulo_tipo_incapacidad",rs.getString("titulo_tipo_incapacidad"));
                 return row;
                }
            }
        );
        return hm;
    }
    
    
    //Obtiene las Horas Extras del empleado para la Nomina de un Periodo especifico
    @Override
    public ArrayList<LinkedHashMap<String,String>> getFacNomina_HorasExtrasXml(Integer id) {
        String sql_to_query="SELECT nom_tipo_hrs_extra.titulo AS tipo_he, tbl_he.no_dias::integer AS no_dias, tbl_he.no_hrs::integer AS no_hrs, tbl_he.importe FROM fac_nomina_det_hrs_extra AS tbl_he JOIN nom_tipo_hrs_extra ON nom_tipo_hrs_extra.id=nom_tipo_hrs_extra_id WHERE tbl_he.fac_nomina_det_id=? ORDER BY tbl_he.id;";
        
        System.out.println("QueryHorasExtrasXml: "+sql_to_query);
        ArrayList<LinkedHashMap<String,String>>hm=(ArrayList<LinkedHashMap<String,String>>)this.jdbcTemplate.query(
            sql_to_query,
            new Object[]{new Integer(id)},new RowMapper(){
                @Override
                public Object mapRow(ResultSet rs,int rowNum)throws SQLException{
                 LinkedHashMap<String,String>row=new LinkedHashMap<String,String>();
                 row.put("TipoHoras",rs.getString("tipo_he"));
                 row.put("Dias",String.valueOf(rs.getInt("no_dias")));
                 row.put("HorasExtra",String.valueOf(rs.getInt("no_hrs")));
                 row.put("ImportePagado",StringHelper.roundDouble(rs.getDouble("importe"), 2));
                 return row;
                }
            }
        );
        return hm;
    }    
    
    
    
    //Obtener ref_id(nombre del archivo)
    @Override
    public HashMap<String, String> getFacNomina_RefId(Integer id) {
        String sql_to_query = "SELECT ref_id, (CASE WHEN serie IS NULL THEN '' ELSE serie END)||(CASE WHEN folio IS NULL THEN '' ELSE folio END) AS serie_folio FROM fac_nomina_det WHERE id=? AND facturado=true;";
        
        HashMap<String, String> hm = (HashMap<String, String>) this.jdbcTemplate.queryForObject(
            sql_to_query, 
            new Object[]{new Integer(id)}, new RowMapper() {
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    HashMap<String, String> row = new HashMap<String, String>();
                    row.put("ref_id",rs.getString("ref_id"));
                    row.put("serie_folio",rs.getString("serie_folio"));
                    return row;
                }
            }
        );
        return hm;
    }
    
    
    @Override
    public ArrayList<HashMap<String, String>> getEmailEnvio(Integer id_emp, Integer id_suc) {
        String sql_to_query = ""
                + "SELECT "
                    + "gral_emails.email, "
                    + "gral_emails.passwd, "
                    + "(CASE WHEN gral_emails.port IS NULL THEN '' ELSE gral_emails.port END) AS port, "
                    + "(CASE WHEN gral_emails.host IS NULL THEN '' ELSE gral_emails.host END) AS host "
                + "FROM fac_par "
                + "JOIN gral_emails ON (gral_emails.id=fac_par.gral_emails_id_envio AND gral_emails.gral_emp_id=fac_par.gral_emp_id AND gral_emails.gral_suc_id=fac_par.gral_suc_id AND gral_emails.borrado_logico=false)"
                + "WHERE fac_par.gral_emp_id=? AND fac_par.gral_suc_id=? AND fac_par.borrado_logico=false LIMIT 1;";
        
        //log.log(Level.INFO, "Ejecutando query de {0}", sql_to_query);
        ArrayList<HashMap<String, String>> hm = (ArrayList<HashMap<String, String>>) this.jdbcTemplate.query(
            sql_to_query,
            new Object[]{new Integer(id_emp), new Integer(id_suc)}, new RowMapper(){
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    HashMap<String, String> row = new HashMap<String, String>();
                    row.put("email",rs.getString("email"));
                    row.put("passwd",rs.getString("passwd"));
                    row.put("port",rs.getString("port"));
                    row.put("host",rs.getString("host"));
                    return row;
                }
            }
        );
        return hm;
    }

    @Override
    public ArrayList<HashMap<String, String>> getEmailCopiaOculta(Integer id_emp, Integer id_suc) {
        String sql_to_query = "SELECT gral_emails.email FROM fac_par "
                + "JOIN gral_emails ON (gral_emails.id=fac_par.gral_emails_id_cco AND gral_emails.gral_emp_id=fac_par.gral_emp_id AND gral_emails.gral_suc_id=fac_par.gral_suc_id AND gral_emails.borrado_logico=false)"
                + "WHERE fac_par.gral_emp_id=? AND fac_par.gral_suc_id=? AND fac_par.borrado_logico=false LIMIT 1;";
        
        ArrayList<HashMap<String, String>> hm = (ArrayList<HashMap<String, String>>) this.jdbcTemplate.query(
            sql_to_query,
            new Object[]{new Integer(id_emp), new Integer(id_suc)}, new RowMapper(){
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    HashMap<String, String> row = new HashMap<String, String>();
                    row.put("email",rs.getString("email"));
                    return row;
                }
            }
        );
        return hm;
    }

    @Override
    public ArrayList<HashMap<String, Object>> getFacPar_PaginaGrid(String data_string, int offset, int pageSize, String orderBy, String asc) {
        
        String sql_busqueda = "select id from gral_bus_catalogos(?) as foo (id integer)";
        
	String sql_to_query = ""
        + "SELECT DISTINCT fac_par.id,fac_par.gral_suc_id AS suc_id, (case when gral_suc.clave is null then '' else gral_suc.clave end) as clave, gral_suc.titulo AS sucursal FROM fac_par  "
        + "JOIN gral_suc ON gral_suc.id=fac_par.gral_suc_id "
        + "JOIN ("+sql_busqueda+") as subt on subt.id=fac_par.id "
        + "order by "+orderBy+" "+asc+" limit ? OFFSET ?";
        
        //System.out.println("Busqueda GetPage: "+sql_to_query);
        //System.out.println("cliente: "+cliente+ "fecha_inicial:"+fecha_inicial+" fecha_final: "+fecha_final+ " offset:"+offset+ " pageSize: "+pageSize+" orderBy:"+orderBy+" asc:"+asc);
        //log.log(Level.INFO, "Ejecutando query de {0}", sql_to_query);
        ArrayList<HashMap<String, Object>> hm = (ArrayList<HashMap<String, Object>>) this.jdbcTemplate.query(
            sql_to_query, 
            new Object[]{data_string,new Integer(pageSize),new Integer(offset)}, new RowMapper() {
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    HashMap<String, Object> row = new HashMap<String, Object>();
                    row.put("id",rs.getInt("id"));
                    row.put("suc_id",rs.getInt("suc_id"));
                    row.put("clave",rs.getString("clave"));
                    row.put("sucursal",rs.getString("sucursal"));
                    return row;
                }
            }
        );
        return hm;
    }
    
    
    @Override
    public ArrayList<HashMap<String, Object>> getFacPar_Datos(Integer id) {
        String sql_to_query="SELECT fac_par.id, gral_suc.id AS id_suc, gral_suc.titulo AS sucursal, fac_par.inv_alm_id AS alm_id_venta, (CASE WHEN fac_par.gral_emails_id_envio IS NULL THEN 0 ELSE fac_par.gral_emails_id_envio END) AS id_correo_envio, (CASE WHEN fac_par.gral_emails_id_cco IS NULL THEN 0 ELSE fac_par.gral_emails_id_cco END) AS id_correo_cco, (CASE WHEN fac_par.validar_pres_pedido IS NULL THEN false ELSE fac_par.validar_pres_pedido END) AS valida_exi, (CASE WHEN fac_par.formato_pedido IS NULL THEN 0 ELSE fac_par.formato_pedido END) AS formato_pedido, (CASE WHEN emaile.email IS NULL THEN '' ELSE emaile.email END) AS email_envio, (CASE WHEN emaile.passwd IS NULL THEN '' ELSE emaile.passwd END) AS passwd_envio, (CASE WHEN emaile.port IS NULL THEN '' ELSE emaile.port END) AS port_envio, (CASE WHEN emaile.host IS NULL THEN '' ELSE emaile.host END) AS host_envio, (CASE WHEN emailcco.email IS NULL THEN '' ELSE emailcco.email END) AS email_cco FROM fac_par JOIN gral_suc ON gral_suc.id=fac_par.gral_suc_id LEFT JOIN gral_emails AS emaile ON (emaile.id=fac_par.gral_emails_id_envio AND emaile.gral_emp_id=fac_par.gral_emp_id AND emaile.gral_suc_id=fac_par.gral_suc_id AND emaile.borrado_logico=false) LEFT JOIN gral_emails AS emailcco ON (emailcco.id=fac_par.gral_emails_id_cco AND emailcco.gral_emp_id=fac_par.gral_emp_id AND emailcco.gral_suc_id=fac_par.gral_suc_id AND emailcco.borrado_logico=false) WHERE fac_par.id=?;";
        ArrayList<HashMap<String,Object>>hm=(ArrayList<HashMap<String,Object>>)this.jdbcTemplate.query(
            sql_to_query,
            new Object[]{new Integer(id)},new RowMapper(){
                @Override
                public Object mapRow(ResultSet rs,int rowNum)throws SQLException{
                 HashMap<String,Object>row=new HashMap<String,Object>();
                 row.put("id",rs.getString("id"));
                 row.put("id_suc",rs.getString("id_suc"));
                 row.put("sucursal",rs.getString("sucursal"));
                 row.put("correo_e_id",rs.getString("id_correo_envio"));
                 row.put("correo_cco_id",rs.getString("id_correo_cco"));
                 row.put("alm_id_venta",rs.getString("alm_id_venta"));
                 row.put("valida_exi",String.valueOf(rs.getBoolean("valida_exi")));
                 row.put("formato_pedido",rs.getString("formato_pedido"));
                 row.put("email_envio",rs.getString("email_envio"));
                 row.put("passwd_envio",rs.getString("passwd_envio"));
                 row.put("port_envio",rs.getString("port_envio"));
                 row.put("host_envio",rs.getString("host_envio"));
                 row.put("email_cco",rs.getString("email_cco"));
                 return row;
                }
            }
        );
        return hm;
    }
    
    
    //obtiene los almacenes de la empresa indicada
    @Override
    public ArrayList<HashMap<String, Object>> getFacPar_Almacenes(Integer id_emp, Integer id_suc) {
	String sql_query = "SELECT DISTINCT inv_alm.id, inv_alm.titulo FROM inv_alm JOIN inv_suc_alm ON (inv_suc_alm.almacen_id = inv_alm.id AND inv_alm.borrado_logico=FALSE) JOIN gral_suc ON gral_suc.id=inv_suc_alm.sucursal_id WHERE gral_suc.empresa_id=? AND gral_suc.id=?;";
        ArrayList<HashMap<String, Object>> hm_alm = (ArrayList<HashMap<String, Object>>) this.jdbcTemplate.query(
            sql_query,
            new Object[]{new Integer(id_emp), new Integer(id_suc)}, new RowMapper() {
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    HashMap<String, Object> row = new HashMap<String, Object>();
                    row.put("id",rs.getString("id"));
                    row.put("titulo",rs.getString("titulo"));
                    return row;
                }
            }
        );
        return hm_alm;
    }
    
    
    @Override
    public ArrayList<HashMap<String, Object>> getCtb_TiposDeMovimiento(Integer id_empresa, Integer appId) {
        
        String sql_query = ""
        + "select ctb_tmov.id, ctb_tmov.titulo "
        + "from ctb_tmov "
        + "join ctb_app on (ctb_app.id=ctb_tmov.ctb_app_id and ctb_app.gral_app_id=?)"
        + "where ctb_tmov.borrado_logico=false and ctb_tmov.gral_emp_id=?;";
        
        //System.out.println("Ctb_Temov: "+sql_query);
        
        ArrayList<HashMap<String, Object>> hm = (ArrayList<HashMap<String, Object>>) this.jdbcTemplate.query(
            sql_query,
            new Object[]{new Integer(appId), new Integer(id_empresa)}, new RowMapper() {
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    HashMap<String, Object> row = new HashMap<String, Object>();
                    row.put("id",String.valueOf(rs.getInt("id")));
                    row.put("titulo",String.valueOf(rs.getString("titulo")));
                    return row;
                }
            }
        );
        return hm;
    }
    
    
    
}