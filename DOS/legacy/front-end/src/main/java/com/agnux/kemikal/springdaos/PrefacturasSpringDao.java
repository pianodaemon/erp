package com.agnux.kemikal.springdaos;

import com.agnux.common.helpers.StringHelper;
import com.agnux.kemikal.interfacedaos.PrefacturasInterfaceDao;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;


public class PrefacturasSpringDao implements PrefacturasInterfaceDao{
    
    private JdbcTemplate jdbcTemplate;
    
    public JdbcTemplate getJdbcTemplate() {
        return jdbcTemplate;
    }
    
    public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }
    
    
    @Override
    public HashMap<String, String> selectFunctionValidateAaplicativo(String data, Integer idApp, String string_array) {
        String sql_to_query = "select erp_fn_validaciones_por_aplicativo from erp_fn_validaciones_por_aplicativo('"+data+"',"+idApp+",array["+string_array+"]);";
        
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
    
    
    
    @Override
    public String selectFunctionForThisApp(String campos_data, String extra_data_array) {
        //String sql_to_query = "select * from gral_adm_catalogos('"+campos_data+"',array["+extra_data_array+"]);";
        String sql_to_query = "select * from fac_adm_procesos('"+campos_data+"',array["+extra_data_array+"]);";
        
        //System.out.println("Ejacutando Guardar:"+sql_to_query);
        //int update = this.getJdbcTemplate().queryForInt(sql_to_query);
        //return update;
        String valor_retorno="";
        Map<String, Object> update = this.getJdbcTemplate().queryForMap(sql_to_query);
        
        valor_retorno = update.get("fac_adm_procesos").toString();
        
        return valor_retorno;
    }
    
    
    
    
    @Override
    public int countAll(String data_string) {
        String sql_busqueda = "select id from gral_bus_catalogos('"+data_string+"') as foo (id integer)";
        String sql_to_query = "select count(id)::int as total from ("+sql_busqueda+") as subt";
        
        int rowCount = this.getJdbcTemplate().queryForInt(sql_to_query);
        return rowCount;
    }
    
    
    
    
    //Obtiene los parametros de facturación
    @Override
    public ArrayList<HashMap<String, Object>> getFac_Parametros(Integer idSuc) {
	String sql_query = "SELECT * FROM fac_par WHERE gral_suc_id="+idSuc+" LIMIT 1;";
        ArrayList<HashMap<String, Object>> hm_alm = (ArrayList<HashMap<String, Object>>) this.jdbcTemplate.query(
            sql_query,
            new Object[]{}, new RowMapper() {
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    HashMap<String, Object> row = new HashMap<String, Object>();
                    row.put("validaPresPedido",String.valueOf(rs.getBoolean("validar_pres_pedido")));
                    row.put("incluye_adenda", String.valueOf(rs.getBoolean("incluye_adenda")).toLowerCase());
                    return row;
                }
            }
        );
        return hm_alm;
    }
    
    
    
    
    @Override
    public ArrayList<HashMap<String, Object>> getPrefacturas__PaginaGrid(String data_string, int offset, int pageSize, String orderBy, String asc) {
        
        String sql_busqueda = "select id from gral_bus_catalogos(?) as foo (id integer)";
        
	String sql_to_query = "SELECT * FROM ( "
                +"SELECT DISTINCT  "
                        +"erp_prefacturas.id, "
                        +"cxc_clie.razon_social as cliente, "
                        +"erp_prefacturas.total, "
                        +"gral_mon.descripcion_abr AS denominacion,   "
                        +"(CASE WHEN erp_prefacturas.folio_pedido IS NULL THEN '' ELSE erp_prefacturas.folio_pedido END ) AS folio_pedido, "
                        +"(CASE WHEN erp_prefacturas.orden_compra IS NULL THEN '' ELSE erp_prefacturas.orden_compra END) AS oc, "
                        +"(CASE WHEN erp_proceso.proceso_flujo_id=2 THEN '' ELSE erp_proceso_flujo.titulo END) as estado, "
                        +"to_char(erp_prefacturas.momento_creacion,'dd/mm/yyyy') as fecha_creacion, "
                        +"(CASE WHEN erp_prefacturas.refacturar=FALSE THEN 1 ELSE 0 END) refacturar "
                +"FROM erp_prefacturas  "
                +"LEFT JOIN erp_proceso on erp_proceso.id = erp_prefacturas.proceso_id  "
                +"LEFT JOIN fac_docs on fac_docs.proceso_id = erp_proceso.id  "
                +"LEFT JOIN erp_proceso_flujo on erp_proceso_flujo.id = erp_proceso.proceso_flujo_id  "
                +"LEFT JOIN cxc_clie on cxc_clie.id = erp_prefacturas.cliente_id  "
                +"LEFT JOIN gral_mon ON gral_mon.id=erp_prefacturas.moneda_id "
                +"JOIN ("+sql_busqueda+") as subt on subt.id=erp_prefacturas.id "
        +") AS sbt2 "
        +"order by "+orderBy+" "+asc+" limit ? OFFSET ?";
        
        //System.out.println("Busqueda GetPage: "+sql_to_query);
        //System.out.println("data_string: "+data_string);
        //System.out.println(" offset:"+offset+ " pageSize: "+pageSize+" orderBy:"+orderBy+" asc:"+asc);
        //log.log(Level.INFO, "Ejecutando query de {0}", sql_to_query);
        ArrayList<HashMap<String, Object>> hm = (ArrayList<HashMap<String, Object>>) this.jdbcTemplate.query(
            sql_to_query, 
            new Object[]{new String(data_string),new Integer(pageSize),new Integer(offset)}, new RowMapper() {
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    HashMap<String, Object> row = new HashMap<String, Object>();
                    row.put("id",rs.getInt("id"));
                    row.put("cliente",rs.getString("cliente"));
                    row.put("total",StringHelper.AgregaComas(StringHelper.roundDouble(rs.getString("total"),2)));
                    row.put("denominacion",rs.getString("denominacion"));
                    row.put("folio_pedido",rs.getString("folio_pedido"));
                    row.put("oc",rs.getString("oc"));
                    row.put("estado",rs.getString("estado"));
                    row.put("fecha_creacion",rs.getString("fecha_creacion"));
                    return row;
                }
            }
        );
        return hm;
    }
    
    
    
    //obtiene los almacenes de la empresa indicada
    @Override
    public ArrayList<HashMap<String, Object>> getAlmacenes(Integer id_empresa) {
	String sql_query = "SELECT DISTINCT inv_alm.id, inv_alm.titulo "
                        + "FROM inv_alm " 
                        + "JOIN inv_suc_alm ON inv_suc_alm.almacen_id = inv_alm.id "
                        + "JOIN gral_suc ON gral_suc.id = inv_suc_alm.sucursal_id  "
                        + "WHERE empresa_id="+id_empresa+" AND inv_alm.borrado_logico=FALSE;";
        ArrayList<HashMap<String, Object>> hm_alm = (ArrayList<HashMap<String, Object>>) this.jdbcTemplate.query(
            sql_query,
            new Object[]{}, new RowMapper() {
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
    
    
    
    
    //Obtiene  los datos de la Prefactura
    @Override
    public ArrayList<HashMap<String, Object>> getPrefactura_Datos(Integer id_prefactura) {
        
        String sql_query = ""
        + "SELECT erp_prefacturas.id, "
            + "erp_prefacturas.folio_pedido, "
            + "erp_proceso.proceso_flujo_id, "
            + "erp_prefacturas.moneda_id, "
            + "gral_mon.descripcion as moneda, "
            + "gral_mon.iso_4217_anterior AS mon_iso4217_anterior, "
            + "erp_prefacturas.observaciones, "
            + "cxc_clie.id as cliente_id, "
            + "cxc_clie.numero_control, "
            + "cxc_clie.razon_social, "
            + "(CASE WHEN cxc_clie.rfc IS NULL THEN '' ELSE cxc_clie.rfc END) AS rfc, "
            + "(CASE WHEN cxc_clie.email IS NULL THEN '' ELSE cxc_clie.email END) AS email, "
            + "cxc_clie.cxc_clie_tipo_adenda_id AS adenda_id, "
            + "cxc_clie.empresa_immex, "
            + "erp_prefacturas.cxc_clie_df_id, "
            + "(CASE WHEN erp_prefacturas.cxc_clie_df_id > 1 THEN "
                + "sbtdf.calle||' '||sbtdf.numero_interior||' '||sbtdf.numero_exterior||', '||sbtdf.colonia||', '||sbtdf.municipio||', '||sbtdf.estado||', '||sbtdf.pais||' C.P. '||sbtdf.cp "
            + "ELSE "
                + "cxc_clie.calle||' '||cxc_clie.numero||', '||cxc_clie.colonia||', '||gral_mun.titulo||', '||gral_edo.titulo||', '||gral_pais.titulo||' C.P. '||cxc_clie.cp "
            + "END ) AS direccion,"
            + "cxc_clie.cta_pago_mn, "
            + "cxc_clie.cta_pago_usd, "
            + "erp_prefacturas.subtotal, "
            + "erp_prefacturas.impuesto, "
            + "erp_prefacturas.total,  "
            + "erp_prefacturas.tipo_cambio, "
            + "erp_prefacturas.empleado_id,  "
            + "erp_prefacturas.terminos_id,  "
            + "erp_prefacturas.orden_compra,  "
            + "CASE WHEN erp_prefacturas.refacturar=TRUE THEN 'true' ELSE 'false' END AS refacturar, "
            + "erp_prefacturas.fac_metodos_pago_id,  "
            + "erp_prefacturas.no_cuenta, "
            + "erp_prefacturas.tasa_retencion_immex, "
            + "erp_prefacturas.tipo_documento,"
            + "erp_prefacturas.inv_alm_id, "
            + "erp_prefacturas.ctb_tmov_id as tmov_id, "
            + "(CASE WHEN erp_prefacturas.monto_descto>0 THEN true ELSE false END) AS pdescto, "
            + "erp_prefacturas.monto_descto, "
            + "(CASE WHEN erp_prefacturas.monto_descto>0 THEN (CASE WHEN erp_prefacturas.motivo_descto IS NULL THEN '' ELSE erp_prefacturas.motivo_descto END) ELSE '' END) AS mdescto "
        + "FROM erp_prefacturas  "
        + "LEFT JOIN erp_proceso ON erp_proceso.id = erp_prefacturas.proceso_id  "
        + "LEFT JOIN gral_mon ON gral_mon.id = erp_prefacturas.moneda_id  "
        + "LEFT JOIN cxc_clie ON cxc_clie.id=erp_prefacturas.cliente_id  "
        + "LEFT JOIN gral_pais ON gral_pais.id = cxc_clie.pais_id  "
        + "LEFT JOIN gral_edo ON gral_edo.id = cxc_clie.estado_id  "
        + "LEFT JOIN gral_mun ON gral_mun.id = cxc_clie.municipio_id  "
        + "LEFT JOIN (SELECT cxc_clie_df.id, (CASE WHEN cxc_clie_df.calle IS NULL THEN '' ELSE cxc_clie_df.calle END) AS calle, (CASE WHEN cxc_clie_df.numero_interior IS NULL THEN '' ELSE (CASE WHEN cxc_clie_df.numero_interior IS NULL OR cxc_clie_df.numero_interior='' THEN '' ELSE 'NO.INT.'||cxc_clie_df.numero_interior END)  END) AS numero_interior, (CASE WHEN cxc_clie_df.numero_exterior IS NULL THEN '' ELSE (CASE WHEN cxc_clie_df.numero_exterior IS NULL OR cxc_clie_df.numero_exterior='' THEN '' ELSE 'NO.EXT.'||cxc_clie_df.numero_exterior END )  END) AS numero_exterior, (CASE WHEN cxc_clie_df.colonia IS NULL THEN '' ELSE cxc_clie_df.colonia END) AS colonia,(CASE WHEN gral_mun.id IS NULL OR gral_mun.id=0 THEN '' ELSE gral_mun.titulo END) AS municipio,(CASE WHEN gral_edo.id IS NULL OR gral_edo.id=0 THEN '' ELSE gral_edo.titulo END) AS estado,(CASE WHEN gral_pais.id IS NULL OR gral_pais.id=0 THEN '' ELSE gral_pais.titulo END) AS pais,(CASE WHEN cxc_clie_df.cp IS NULL THEN '' ELSE cxc_clie_df.cp END) AS cp  FROM cxc_clie_df LEFT JOIN gral_pais ON gral_pais.id = cxc_clie_df.gral_pais_id LEFT JOIN gral_edo ON gral_edo.id = cxc_clie_df.gral_edo_id LEFT JOIN gral_mun ON gral_mun.id = cxc_clie_df.gral_mun_id ) AS sbtdf ON sbtdf.id = erp_prefacturas.cxc_clie_df_id "
        + "WHERE erp_prefacturas.id=? "; 
        
        ArrayList<HashMap<String, Object>> hm = (ArrayList<HashMap<String, Object>>) this.jdbcTemplate.query(
            sql_query,  
            new Object[]{new Integer(id_prefactura)}, new RowMapper() {
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    HashMap<String, Object> row = new HashMap<>();
                    row.put("id",String.valueOf(rs.getInt("id")));
                    row.put("folio_pedido",rs.getString("folio_pedido"));
                    row.put("proceso_flujo_id",rs.getInt("proceso_flujo_id"));
                    row.put("moneda_id",rs.getString("moneda_id"));
                    row.put("moneda",rs.getString("moneda"));
                    row.put("moneda2",rs.getString("mon_iso4217_anterior"));
                    row.put("observaciones",rs.getString("observaciones"));
                    row.put("cliente_id",rs.getString("cliente_id"));
                    row.put("numero_control",rs.getString("numero_control"));
                    row.put("razon_social",rs.getString("razon_social"));
                    row.put("rfc",rs.getString("rfc"));
                    row.put("email",rs.getString("email"));
                    row.put("adenda_id",String.valueOf(rs.getInt("adenda_id")));
                    row.put("df_id",String.valueOf(rs.getInt("cxc_clie_df_id")));
                    row.put("direccion",rs.getString("direccion"));
                    row.put("subtotal",StringHelper.roundDouble(rs.getDouble("subtotal"),2));
                    row.put("impuesto",StringHelper.roundDouble(rs.getDouble("impuesto"),2));
                    row.put("total",StringHelper.roundDouble(rs.getDouble("total"),2));
                    row.put("tipo_cambio",StringHelper.roundDouble(rs.getDouble("tipo_cambio"),4));
                    row.put("empleado_id",rs.getString("empleado_id"));
                    row.put("terminos_id",rs.getString("terminos_id"));
                    row.put("orden_compra",rs.getString("orden_compra"));
                    row.put("refacturar",String.valueOf(rs.getBoolean("refacturar")));
                    row.put("fac_metodos_pago_id",String.valueOf(rs.getInt("fac_metodos_pago_id")));
                    row.put("no_cuenta",rs.getString("no_cuenta"));
                    row.put("cta_pago_mn",rs.getString("cta_pago_mn"));
                    row.put("cta_pago_usd",rs.getString("cta_pago_usd"));
                    row.put("empresa_immex",String.valueOf(rs.getBoolean("empresa_immex")));
                    row.put("tasa_retencion_immex",StringHelper.roundDouble(rs.getDouble("tasa_retencion_immex"),2));
                    row.put("tipo_documento",String.valueOf(rs.getInt("tipo_documento")));
                    row.put("id_almacen",rs.getInt("inv_alm_id"));
                    row.put("tmov_id",rs.getInt("tmov_id"));
                    row.put("pdescto",String.valueOf(rs.getBoolean("pdescto")));
                    row.put("monto_descto",StringHelper.roundDouble(rs.getDouble("monto_descto"),2));
                    row.put("mdescto",rs.getString("mdescto"));
                    return row;
                }
            }
        );
        return hm;
    }
    
    
    
    //obtiene los datos del grid de la prefactura
    @Override
    public ArrayList<HashMap<String, Object>> getPrefactura_DatosGrid(Integer id_prefactura) {
        
        String sql_query = ""
        + "SELECT erp_prefacturas_detalles.id as id_detalle,"
            + "erp_prefacturas_detalles.producto_id,"
            + "inv_prod.sku,"
            + "inv_prod.descripcion as titulo,"
            + "(CASE WHEN inv_prod_unidades.id IS NULL THEN 0 ELSE inv_prod_unidades.id END) AS unidad_id,"
            + "(CASE WHEN inv_prod_unidades.titulo IS NULL THEN '' ELSE inv_prod_unidades.titulo END) as unidad,"
            + "(CASE WHEN inv_prod_unidades.decimales IS NULL THEN 0 ELSE inv_prod_unidades.decimales END) AS decimales,"
            + "(CASE WHEN inv_prod_presentaciones.id IS NULL THEN 0 ELSE inv_prod_presentaciones.id END) as id_presentacion,"
            + "(CASE WHEN inv_prod_presentaciones.titulo IS NULL THEN '' ELSE inv_prod_presentaciones.titulo END) AS presentacion,"
            + "erp_prefacturas_detalles.cantidad AS cant_pedido,"
            + "erp_prefacturas_detalles.cant_facturado AS cant_facturado,"
            + "(erp_prefacturas_detalles.cantidad::double precision - erp_prefacturas_detalles.cant_facturado::double precision) AS cant_pendiente,"
            + "erp_prefacturas_detalles.facturado,"
            + "erp_prefacturas_detalles.precio_unitario,"
            + "(erp_prefacturas_detalles.cantidad * erp_prefacturas_detalles.precio_unitario) AS importe, "
            + "erp_prefacturas_detalles.tipo_impuesto_id,"
            + "erp_prefacturas_detalles.valor_imp, "
            + "erp_prefacturas_detalles.gral_ieps_id,"
            + "(erp_prefacturas_detalles.valor_ieps * 100) AS valor_ieps, "
            + "gral_mon.descripcion as moneda, "
            + "erp_prefacturas_detalles.descto, "
            + "erp_prefacturas_detalles.gral_imptos_ret_id as ret_id,"
            + "(erp_prefacturas_detalles.tasa_ret * 100) AS ret_tasa, "
            + "erp_prefacturas_detalles.inv_prod_alias_id "
        + "FROM erp_prefacturas "
        + "JOIN erp_prefacturas_detalles on erp_prefacturas_detalles.prefacturas_id=erp_prefacturas.id "
        + "LEFT JOIN gral_mon on gral_mon.id = erp_prefacturas.moneda_id "
        + "LEFT JOIN inv_prod on inv_prod.id = erp_prefacturas_detalles.producto_id "
        + "LEFT JOIN inv_prod_unidades on inv_prod_unidades.id = erp_prefacturas_detalles.inv_prod_unidad_id "
        + "LEFT JOIN inv_prod_presentaciones on inv_prod_presentaciones.id = erp_prefacturas_detalles.presentacion_id "
        + "WHERE erp_prefacturas.id="+id_prefactura;

        ArrayList<HashMap<String, Object>> grid = (ArrayList<HashMap<String, Object>>) this.jdbcTemplate.query(
            sql_query,  
            new Object[] {},
            new RowMapper() {

                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    HashMap<String, Object> row = new HashMap<>();
                    row.put("id_detalle",rs.getInt("id_detalle"));
                    row.put("producto_id",rs.getString("producto_id"));
                    row.put("sku",rs.getString("sku"));
                    row.put("titulo",rs.getString("titulo"));
                    row.put("unidad_id",rs.getString("unidad_id"));
                    row.put("unidad",rs.getString("unidad"));
                    row.put("no_dec",rs.getInt("decimales"));
                    row.put("id_presentacion",rs.getString("id_presentacion"));
                    row.put("presentacion",rs.getString("presentacion"));
                    row.put("cant_pedido",StringHelper.roundDouble( rs.getString("cant_pedido"), rs.getInt("decimales") ));
                    row.put("cant_facturado",StringHelper.roundDouble( rs.getString("cant_facturado"), rs.getInt("decimales") ));
                    row.put("cant_pendiente",StringHelper.roundDouble( rs.getString("cant_pendiente"), rs.getInt("decimales") ));
                    row.put("facturado",String.valueOf(rs.getBoolean("facturado")));
                    row.put("precio_unitario",StringHelper.roundDouble(rs.getDouble("precio_unitario"),4) );
                    row.put("importe",StringHelper.roundDouble(rs.getDouble("importe"),2) );
                    row.put("moneda",rs.getString("moneda"));
                    row.put("tipo_impuesto_id",rs.getInt("tipo_impuesto_id"));
                    row.put("valor_imp",StringHelper.roundDouble(rs.getDouble("valor_imp"),2) );
                    row.put("costo_prom","0" );
                    row.put("ieps_id",String.valueOf(rs.getInt("gral_ieps_id")));
                    row.put("valor_ieps",StringHelper.roundDouble(rs.getString("valor_ieps"),2));
                    row.put("descto",StringHelper.roundDouble(rs.getDouble("descto"),4) );
                    row.put("ret_id",String.valueOf(rs.getInt("ret_id")));
                    row.put("ret_tasa",StringHelper.roundDouble(rs.getString("ret_tasa"),2));
                    row.put("inv_prod_alias_id", rs.getInt("inv_prod_alias_id"));
                    
                    return row;
                }
            }
        );

        for (HashMap<String, Object> p : grid) {

            ArrayList<HashMap<String, Object>> alias = (ArrayList<HashMap<String, Object>>) this.jdbcTemplate.query(
                "SELECT alias_id, descripcion FROM inv_prod_alias WHERE producto_id = ?;",
                new Object[] {Integer.parseInt((String) p.get("producto_id"))},
                new RowMapper() {

                    @Override
                    public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                        HashMap<String, Object> row = new HashMap<>();
                        row.put("alias_id",    rs.getInt("alias_id"));
                        row.put("descripcion", rs.getString("descripcion"));
                        return row;
                    }
                }
            );

            boolean haceMatch = false;

            for (HashMap<String, Object> a : alias) {

                if (a.get("alias_id") == p.get("inv_prod_alias_id")) {
                    p.put("inv_prod_alias", a.get("descripcion"));
                    haceMatch = true;
                }
            }

            if (alias.isEmpty() || !haceMatch) {
                p.put("inv_prod_alias", "");
            }
        }

        return grid;
    }
    
    
    
    
    
    //Obtener datos para la Adenda
    @Override
    public ArrayList<HashMap<String, Object>> getPrefactura_DatosAdenda(Integer id_prefactura) {
        String sql_query = "SELECT * FROM fac_docs_adenda WHERE prefactura_id="+id_prefactura+";";
        
        //System.out.println("Obtiene datos grid prefactura: "+sql_query);
        ArrayList<HashMap<String, Object>> hm_grid = (ArrayList<HashMap<String, Object>>) this.jdbcTemplate.query(
            sql_query,  
            new Object[]{}, new RowMapper() {
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    HashMap<String, Object> row = new HashMap<String, Object>();
                    row.put("id_adenda",String.valueOf(rs.getInt("id")));
                    row.put("valor1",String.valueOf(rs.getString("valor1")));
                    row.put("valor2",String.valueOf(rs.getString("valor2")));
                    row.put("valor3",String.valueOf(rs.getString("valor3")).toLowerCase());
                    row.put("valor4",String.valueOf(rs.getString("valor4")));
                    row.put("valor5",String.valueOf(rs.getString("valor5")));
                    row.put("valor6",String.valueOf(rs.getString("valor6")));
                    row.put("valor7",String.valueOf(rs.getString("valor7")));
                    row.put("valor8",String.valueOf(rs.getString("valor8")));
                    return row;
                }
            }
        );
        return hm_grid;
    }
    
    
    
    
    
    //Obtener las presentaciones de cada producto de la Prefactura
    @Override
    public ArrayList<HashMap<String, Object>> getPrefactura_PresPorProd(Integer id_prefactura) {
        String sql_query = ""
                + "SELECT DISTINCT "
                    + "prefac_det.producto_id, "
                    + "inv_prod_pres_x_prod.presentacion_id, "
                    + "inv_prod_presentaciones.titulo AS presentacion "
                + "FROM erp_prefacturas_detalles AS prefac_det "
                + "JOIN inv_prod_pres_x_prod ON inv_prod_pres_x_prod.producto_id=prefac_det.producto_id "
                + "JOIN inv_prod_presentaciones ON inv_prod_presentaciones.id=inv_prod_pres_x_prod.presentacion_id "
                + "WHERE prefac_det.prefacturas_id="+id_prefactura+" "
                + "ORDER BY prefac_det.producto_id;";
        
        //System.out.println("Obtiene datos grid prefactura: "+sql_query);
        ArrayList<HashMap<String, Object>> hm_grid = (ArrayList<HashMap<String, Object>>) this.jdbcTemplate.query(
            sql_query,  
            new Object[]{}, new RowMapper() {
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    HashMap<String, Object> row = new HashMap<String, Object>();
                    row.put("producto_id",rs.getInt("producto_id"));
                    row.put("presentacion_id",rs.getInt("presentacion_id"));
                    row.put("presentacion",rs.getString("presentacion"));
                    return row;
                }
            }
        );
        return hm_grid;
    }
    
    
    
    //Obtener la presentaciones de los Productos de la Remisión
    @Override
    public ArrayList<HashMap<String, Object>> getPresPorProdRemision(Integer id_remision) {
        String sql_query = ""
                + "SELECT DISTINCT "
                + "rem_det.inv_prod_id AS producto_id, "
                + "inv_prod_pres_x_prod.presentacion_id, "
                + "inv_prod_presentaciones.titulo AS presentacion "
                + "FROM fac_rems_detalles AS rem_det "
                + "JOIN inv_prod_pres_x_prod ON inv_prod_pres_x_prod.producto_id=rem_det.inv_prod_id "
                + "JOIN inv_prod_presentaciones ON inv_prod_presentaciones.id=inv_prod_pres_x_prod.presentacion_id  "
                + "WHERE rem_det.fac_rems_id="+id_remision+" "
                + "ORDER BY rem_det.inv_prod_id;";
        
        //System.out.println("Obtiene datos grid prefactura: "+sql_query);
        ArrayList<HashMap<String, Object>> hm_grid = (ArrayList<HashMap<String, Object>>) this.jdbcTemplate.query(
            sql_query,  
            new Object[]{}, new RowMapper() {
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    HashMap<String, Object> row = new HashMap<String, Object>();
                    row.put("producto_id",rs.getInt("producto_id"));
                    row.put("presentacion_id",rs.getInt("presentacion_id"));
                    row.put("presentacion",rs.getString("presentacion"));
                    return row;
                }
            }
        );
        return hm_grid;
    }
    
    
    
    //Obtiene todas la monedas
    @Override
    public ArrayList<HashMap<String, Object>> getMonedas() {
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
    public ArrayList<HashMap<String, Object>> getVendedores(Integer id_empresa, Integer id_sucursal) {
        String sql_to_query = ""
        + "SELECT cxc_agen.id, cxc_agen.nombre AS nombre_vendedor FROM cxc_agen JOIN gral_usr_suc ON gral_usr_suc.gral_usr_id=cxc_agen.gral_usr_id JOIN gral_suc ON gral_suc.id=gral_usr_suc.gral_suc_id "
        +"WHERE gral_suc.empresa_id="+id_empresa+" ORDER BY cxc_agen.id;";
        
        //log.log(Level.INFO, "Ejecutando query de {0}", sql_to_query);
        ArrayList<HashMap<String, Object>> hm_vendedor = (ArrayList<HashMap<String, Object>>) this.jdbcTemplate.query(
            sql_to_query,
            new Object[]{}, new RowMapper(){
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    HashMap<String, Object> row = new HashMap<String, Object>();
                    row.put("id",rs.getString("id"));
                    row.put("nombre_vendedor",rs.getString("nombre_vendedor"));
                    return row;
                }
            }
        );
        return hm_vendedor;
    }
    
    
    @Override
    public ArrayList<HashMap<String, Object>> getCondiciones() {
        String sql_to_query = "SELECT id,descripcion FROM cxc_clie_credias WHERE borrado_logico=FALSE;";
        //log.log(Level.INFO, "Ejecutando query de {0}", sql_to_query);
        ArrayList<HashMap<String, Object>> hm_termino = (ArrayList<HashMap<String, Object>>) this.jdbcTemplate.query(
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
        return hm_termino;
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
    public ArrayList<HashMap<String, Integer>>  getAnioInforme() {
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
    
    
    
    //buscador de clientes de una sucursal
    @Override
    public ArrayList<HashMap<String, Object>> get_buscador_clientes(String cadena, Integer filtro,Integer id_empresa, Integer id_sucursal) {
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
        
        ArrayList<HashMap<String, Object>> hm_cli = (ArrayList<HashMap<String, Object>>) this.jdbcTemplate.query(
            sql_query,  
            new Object[]{}, new RowMapper() {
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    HashMap<String, Object> row = new HashMap<String, Object>();
                    row.put("id",rs.getInt("id"));
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
    
    
    
    //buscador de clientes
    @Override
    public ArrayList<HashMap<String, Object>> getDatosClienteByNoCliente(String no_control,  Integer id_empresa, Integer id_sucursal) {
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
            +" WHERE empresa_id ="+id_empresa+"  AND sucursal_id="+id_sucursal
            + " AND cxc_clie.borrado_logico=false  AND cxc_clie.numero_control='"+no_control.toUpperCase()+"'"
        +") AS sbt "
        +"LEFT JOIN gral_mon on gral_mon.id = sbt.moneda_id;";
        
        ArrayList<HashMap<String, Object>> hm_cli = (ArrayList<HashMap<String, Object>>) this.jdbcTemplate.query(
            sql_query,  
            new Object[]{}, new RowMapper() {
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    HashMap<String, Object> row = new HashMap<String, Object>();
                    row.put("id",rs.getInt("id"));
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
    
    
    
    
    //obtiene tipos de productos
    @Override
    public ArrayList<HashMap<String, String>> getProductoTipos() {
	String sql_query = "SELECT DISTINCT id ,titulo FROM inv_prod_tipos WHERE borrado_logico=false order by id;";
        ArrayList<HashMap<String, String>> hm_tp = (ArrayList<HashMap<String, String>>) this.jdbcTemplate.query(
            sql_query,
            new Object[]{}, new RowMapper() {
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    HashMap<String, String> row = new HashMap<String, String>();
                    row.put("id",rs.getString("id"));
                    row.put("titulo",rs.getString("titulo"));
                    return row;
                }
            }
        );
        
        return hm_tp;
    }
    
    
    //buscador de productos para una empresa
    @Override
    public ArrayList<HashMap<String, String>> getBuscadorProductos(String sku, String tipo, String descripcion, Integer id_empresa) {
        String where = "";
	if(!sku.equals("")){
		where=" AND inv_prod.sku ilike '%"+sku+"%'";
	}
	if(!tipo.equals("0")){
		where +=" AND inv_prod.tipo_de_producto_id="+tipo;
	}
	if(!descripcion.equals("")){
		where +=" AND inv_prod.descripcion ilike '%"+descripcion+"%'";
	}
        
        String sql_to_query = ""
        + "SELECT "
            +"inv_prod.id,"
            +"inv_prod.sku,"
            +"inv_prod.descripcion, "
            +"inv_prod.unidad_id, "
            +"inv_prod_unidades.titulo AS unidad, "
            +"inv_prod_tipos.titulo AS tipo,"
            +"inv_prod_unidades.decimales "
        +"FROM inv_prod "
        +"LEFT JOIN inv_prod_tipos ON inv_prod_tipos.id=inv_prod.tipo_de_producto_id "
        +"LEFT JOIN inv_prod_unidades ON inv_prod_unidades.id=inv_prod.unidad_id "
        +"WHERE inv_prod.empresa_id="+id_empresa+" AND inv_prod.borrado_logico=false "+where+" ORDER BY inv_prod.descripcion;";
        //log.log(Level.INFO, "Ejecutando query de {0}", sql_to_query);
        
        ArrayList<HashMap<String, String>> hm_datos_productos = (ArrayList<HashMap<String, String>>) this.jdbcTemplate.query(
            sql_to_query,
            new Object[]{}, new RowMapper(){
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    HashMap<String, String> row = new HashMap<String, String>();
                    row.put("id",String.valueOf(rs.getInt("id")));
                    row.put("sku",rs.getString("sku"));
                    row.put("descripcion",rs.getString("descripcion"));
                    row.put("unidad_id",String.valueOf(rs.getInt("unidad_id")));
                    row.put("unidad",rs.getString("unidad"));
                    row.put("tipo",rs.getString("tipo"));
                    row.put("decimales",String.valueOf(rs.getInt("decimales")));
                    return row;
                }
            }
        );
        return hm_datos_productos;
    }
    
    
    //obtener las presentaciones de un producto para de una empresa
    @Override
    public ArrayList<HashMap<String, Object>> get_presentaciones_producto(String sku, Integer id_empresa) {
	String sql_query = ""
        + "SELECT "
            +"inv_prod.id,"
            +"inv_prod.sku,"
            +"inv_prod.descripcion AS titulo,"
            +"(CASE WHEN inv_prod_unidades.titulo IS NULL THEN '' ELSE inv_prod_unidades.titulo END) AS unidad,"
            +"(CASE WHEN inv_prod_presentaciones.id IS NULL THEN 0 ELSE inv_prod_presentaciones.id END) AS id_presentacion,"
            +"(CASE WHEN inv_prod_presentaciones.titulo IS NULL THEN '' ELSE inv_prod_presentaciones.titulo END) AS presentacion, "
            +"(CASE WHEN inv_prod_unidades.decimales IS NULL THEN 0 ELSE inv_prod_unidades.decimales END) AS  decimales "
        +"FROM inv_prod "
        +"LEFT JOIN inv_prod_unidades on inv_prod_unidades.id = inv_prod.unidad_id "
        +"LEFT JOIN inv_prod_pres_x_prod on inv_prod_pres_x_prod.producto_id = inv_prod.id "
        +"LEFT JOIN inv_prod_presentaciones on inv_prod_presentaciones.id = inv_prod_pres_x_prod.presentacion_id "
        +"WHERE  empresa_id = "+id_empresa+" AND inv_prod.sku ILIKE '"+sku+"';";
        
        ArrayList<HashMap<String, Object>> hm = (ArrayList<HashMap<String, Object>>) this.jdbcTemplate.query(
            sql_query,  
            new Object[]{}, new RowMapper() {
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    HashMap<String, Object> row = new HashMap<String, Object>();
                    row.put("id",rs.getInt("id"));
                    row.put("sku",rs.getString("sku"));
                    row.put("titulo",rs.getString("titulo"));
                    row.put("unidad",rs.getString("unidad"));
                    row.put("id_presentacion",rs.getInt("id_presentacion"));
                    row.put("presentacion",rs.getString("presentacion"));
                    row.put("decimales",rs.getString("decimales"));
                    
                    return row;
                }
            }
        );
        return hm;
    }
    
    
    
    
    //obtener lel precio unitario del producto si ya  ha sido cotizado con anterioridad para un cliente en especifico
    @Override
    public HashMap<String, Object> getPrecioUnitario(Integer id_cliente, Integer id_producto, Integer id_pres,Integer id_empresa, Integer id_sucursal ) {
        HashMap<String, Object> pu2 = new HashMap<String, Object>();
        
        String sql_to_query = ""
        + "SELECT count(erp_cotizacions_detalles.precio_unitario) "
        + "FROM erp_cotizacions "
        + "JOIN erp_cotizacions_detalles ON erp_cotizacions_detalles.cotizacions_id = erp_cotizacions.id "
        + "WHERE erp_cotizacions.cliente_id = "+id_cliente
        + " AND erp_cotizacions.empresa_id ="+id_empresa
        + " AND erp_cotizacions.sucursal_id ="+id_sucursal
        + " AND erp_cotizacions.status_id=1 "
        + " AND erp_cotizacions_detalles.producto_id = "+id_producto
        + " AND erp_cotizacions_detalles.presentacion_id = "+id_pres;
        
        int rowCount = this.getJdbcTemplate().queryForInt(sql_to_query);
        
        if(rowCount > 0){
            String sql_query = ""
            + "SELECT erp_cotizacions_detalles.precio_unitario,erp_cotizacions_detalles.moneda_id "
            + "FROM erp_cotizacions "
            + "JOIN erp_cotizacions_detalles ON erp_cotizacions_detalles.cotizacions_id = erp_cotizacions.id "
            + "WHERE erp_cotizacions.cliente_id = "+id_cliente
            + " AND erp_cotizacions.empresa_id ="+id_empresa
            + " AND erp_cotizacions.sucursal_id ="+id_sucursal
            + " AND erp_cotizacions.status_id = 1 "
            + " AND erp_cotizacions_detalles.producto_id = "+id_producto
            + " AND erp_cotizacions_detalles.presentacion_id = "+id_pres
            + " ORDER BY erp_cotizacions_detalles.momento_creacion DESC LIMIT 1;";
            
            //System.out.println("Obtiene precio_unitario:"+ sql_query);
            
            Map<String, Object> prec_uni = this.getJdbcTemplate().queryForMap(sql_query);
            pu2=(HashMap<String, Object>) prec_uni;
            //System.out.println("Si hay precio");
        }else{
            pu2.put("precio_unitario", "");
            pu2.put("moneda_id", "");
            //System.out.println("No hay precio");
        }
        
        return pu2;
    }
    
    
    
    @Override
    public ArrayList<HashMap<String, String>> getRemisionesCliente(Integer id_cliente) {
	String sql_query = "SELECT * FROM ("
                                + "SELECT fac_rems.id, "
                                        + "fac_rems.folio, "
                                        + "fac_rems.total AS monto_remision,"
                                        + "fac_rems.moneda_id,"
                                        + "fac_rems.inv_alm_id AS id_almacen,"
                                        + "gral_mon.descripcion_abr AS moneda,"
                                        + "to_char(fac_rems.momento_creacion,'dd/mm/yyyy') AS fecha_remision, "
                                        + "fac_rems_docs.fac_rem_id "
                                + "FROM fac_rems "
                                + "JOIN gral_mon ON gral_mon.id = fac_rems.moneda_id "
                                + "LEFT JOIN fac_rems_docs ON fac_rems_docs.fac_rem_id=fac_rems.id "
                                + "WHERE fac_rems.facturado=false "
                                + "AND fac_rems.cancelado=false "
                                + "AND fac_rems.cxc_clie_id="+id_cliente +" "
                            + " ) AS sbt where fac_rem_id is null; ";
        
        ArrayList<HashMap<String, String>> hm = (ArrayList<HashMap<String, String>>) this.jdbcTemplate.query(
            sql_query,  
            new Object[]{}, new RowMapper() {
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    HashMap<String, String> row = new HashMap<String, String>();
                    row.put("id",String.valueOf(rs.getInt("id")));
                    row.put("id_almacen",String.valueOf(rs.getInt("id_almacen")));
                    row.put("folio",rs.getString("folio"));
                    row.put("monto_remision",StringHelper.roundDouble(rs.getDouble("monto_remision"), 2));
                    row.put("moneda_id",String.valueOf(rs.getInt("moneda_id")));
                    row.put("moneda",rs.getString("moneda"));
                    row.put("fecha_remision",rs.getString("fecha_remision"));
                    
                    return row;
                }
            }
        );
        return hm;
    }
    
    
    //obtiene los detalles de la remision seleccionada
    @Override
    public ArrayList<HashMap<String, Object>> getDetallesRemision(Integer id_remision, String permitir_descuento) {
        
        String sql_query = ""
        + "SELECT "
                + "fac_rems_detalles.fac_rems_id AS id_remision,"
                + "fac_rems_detalles.id as id_detalle,"
                + "fac_rems_detalles.inv_prod_id AS producto_id,"
                + "inv_prod.sku,"
                + "inv_prod.descripcion as titulo,"
                + "(CASE WHEN inv_prod_unidades.id IS NULL THEN 0 ELSE inv_prod_unidades.id END) AS unidad_id,"
                + "(CASE WHEN inv_prod_unidades.titulo IS NULL THEN '' ELSE inv_prod_unidades.titulo END) as unidad,"
                + "(CASE WHEN inv_prod_unidades.decimales IS NULL THEN 0 ELSE inv_prod_unidades.decimales END) AS decimales,"
                + "(CASE WHEN inv_prod_presentaciones.id IS NULL THEN 0 ELSE inv_prod_presentaciones.id END) AS id_presentacion,"
                + "(CASE WHEN inv_prod_presentaciones.titulo IS NULL THEN '' ELSE inv_prod_presentaciones.titulo END) AS presentacion,"
                + "fac_rems_detalles.cantidad,"
                + "fac_rems_detalles.precio_unitario,"
                + "(fac_rems_detalles.cantidad * fac_rems_detalles.precio_unitario) AS importe, "
                + "fac_rems_detalles.gral_imp_id,"
                + "fac_rems_detalles.valor_imp,"
                + "fac_rems_detalles.costo_promedio, "
                + "fac_rems_detalles.gral_ieps_id,"
                + "(fac_rems_detalles.valor_ieps * 100) AS valor_ieps, "
                + "(CASE WHEN "+ permitir_descuento +"::boolean=true THEN (CASE WHEN fac_rems_detalles.descto IS NULL THEN 0 ELSE fac_rems_detalles.descto END) ELSE 0 END) AS descto,"
                + "fac_rems_detalles.gral_imptos_ret_id as ret_id,"
                + "(fac_rems_detalles.tasa_ret * 100) AS ret_tasa "
         + "FROM fac_rems_detalles "
         + "LEFT JOIN inv_prod on inv_prod.id = fac_rems_detalles.inv_prod_id "
         + "LEFT JOIN inv_prod_unidades on inv_prod_unidades.id = fac_rems_detalles.inv_prod_unidad_id "
         + "LEFT JOIN inv_prod_presentaciones on inv_prod_presentaciones.id = fac_rems_detalles.inv_prod_presentacion_id "
         + "WHERE fac_rems_detalles.fac_rems_id="+id_remision;
        
        //System.out.println("Obtiene datos grid prefactura: "+sql_query);
        ArrayList<HashMap<String, Object>> hm_grid = (ArrayList<HashMap<String, Object>>) this.jdbcTemplate.query(
            sql_query,  
            new Object[]{}, new RowMapper() {
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    HashMap<String, Object> row = new HashMap<String, Object>();
                    row.put("id_remision",rs.getInt("id_remision"));
                    row.put("id_detalle",rs.getInt("id_detalle"));
                    row.put("producto_id",rs.getString("producto_id"));
                    row.put("codigo",rs.getString("sku"));
                    row.put("titulo",rs.getString("titulo"));
                    row.put("unidad_id",rs.getInt("unidad_id"));
                    row.put("unidad",rs.getString("unidad"));
                    row.put("id_presentacion",rs.getString("id_presentacion"));
                    row.put("presentacion",rs.getString("presentacion"));
                    row.put("cantidad",StringHelper.roundDouble( rs.getString("cantidad"), rs.getInt("decimales") ));
                    row.put("precio_unitario",StringHelper.roundDouble(rs.getDouble("precio_unitario"),4) );
                    row.put("importe",StringHelper.roundDouble(rs.getDouble("importe"),4) );
                    row.put("gral_imp_id",rs.getInt("gral_imp_id"));
                    row.put("valor_imp",StringHelper.roundDouble(rs.getDouble("valor_imp"),2) );
                    row.put("costo_prom",StringHelper.roundDouble(rs.getDouble("costo_promedio"),2) );
                    
                    row.put("ieps_id",String.valueOf(rs.getInt("gral_ieps_id")));
                    row.put("valor_ieps",StringHelper.roundDouble(rs.getString("valor_ieps"),2));
                    row.put("descto",StringHelper.roundDouble(rs.getDouble("descto"),4) );
                    row.put("ret_id",String.valueOf(rs.getInt("ret_id")));
                    row.put("ret_tasa",StringHelper.roundDouble(rs.getString("ret_tasa"),2));
                    
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
    public ArrayList<HashMap<String, Object>> getDatosRemision(Integer id_remision) {
	String sql_query = ""
        + "SELECT "
            + "fac_rems.folio AS folio_remision,"
            + "fac_rems.folio_pedido,"
            + "fac_rems.orden_compra,"
            + "gral_mon.iso_4217_anterior AS mon_iso4217_anterior, "
            + "fac_rems.fac_metodos_pago_id,"
            + "fac_rems.cxc_clie_df_id,"
            + "(CASE WHEN fac_rems.cxc_clie_df_id > 1 THEN "
                + "sbtdf.calle||' '||sbtdf.numero_interior||' '||sbtdf.numero_exterior||', '||sbtdf.colonia||', '||sbtdf.municipio||', '||sbtdf.estado||', '||sbtdf.pais||' C.P. '||sbtdf.cp "
            + "ELSE "
                + "cxc_clie.calle||' '||cxc_clie.numero||', '||cxc_clie.colonia||', '||gral_mun.titulo||', '||gral_edo.titulo||', '||gral_pais.titulo||' C.P. '||cxc_clie.cp "
            + "END ) AS direccion,"
            + "(CASE WHEN fac_rems.fac_metodos_pago_id=2 OR fac_rems.fac_metodos_pago_id=3 THEN "
                        + "fac_rems.no_cuenta "
                + "ELSE "
                        + "(CASE WHEN fac_rems.moneda_id=1 THEN cxc_clie.cta_pago_mn "
                                + "WHEN fac_rems.moneda_id=2 THEN cxc_clie.cta_pago_usd "
                        + "ELSE '' " 
                        + "END ) "
            + "END) AS no_cuenta,"
            + "cxc_clie.cxc_clie_tipo_adenda_id AS adenda_id, "
            + "(CASE WHEN fac_rems.monto_descto>0 THEN true ELSE false END) AS pdescto, "
            + "(CASE WHEN fac_rems.motivo_descto IS NULL THEN '' ELSE fac_rems.motivo_descto END) AS mdescto "
        + "FROM fac_rems "
        + "JOIN cxc_clie ON cxc_clie.id = fac_rems.cxc_clie_id "
        + "LEFT JOIN gral_pais ON gral_pais.id = cxc_clie.pais_id "
        + "LEFT JOIN gral_edo ON gral_edo.id = cxc_clie.estado_id "
        + "LEFT JOIN gral_mun ON gral_mun.id = cxc_clie.municipio_id "
        + "LEFT JOIN (SELECT cxc_clie_df.id, (CASE WHEN cxc_clie_df.calle IS NULL THEN '' ELSE cxc_clie_df.calle END) AS calle, (CASE WHEN cxc_clie_df.numero_interior IS NULL THEN '' ELSE (CASE WHEN cxc_clie_df.numero_interior IS NULL OR cxc_clie_df.numero_interior='' THEN '' ELSE 'NO.INT.'||cxc_clie_df.numero_interior END)  END) AS numero_interior, (CASE WHEN cxc_clie_df.numero_exterior IS NULL THEN '' ELSE (CASE WHEN cxc_clie_df.numero_exterior IS NULL OR cxc_clie_df.numero_exterior='' THEN '' ELSE 'NO.EXT.'||cxc_clie_df.numero_exterior END )  END) AS numero_exterior, (CASE WHEN cxc_clie_df.colonia IS NULL THEN '' ELSE cxc_clie_df.colonia END) AS colonia,(CASE WHEN gral_mun.id IS NULL OR gral_mun.id=0 THEN '' ELSE gral_mun.titulo END) AS municipio,(CASE WHEN gral_edo.id IS NULL OR gral_edo.id=0 THEN '' ELSE gral_edo.titulo END) AS estado,(CASE WHEN gral_pais.id IS NULL OR gral_pais.id=0 THEN '' ELSE gral_pais.titulo END) AS pais,(CASE WHEN cxc_clie_df.cp IS NULL THEN '' ELSE cxc_clie_df.cp END) AS cp  FROM cxc_clie_df LEFT JOIN gral_pais ON gral_pais.id = cxc_clie_df.gral_pais_id LEFT JOIN gral_edo ON gral_edo.id = cxc_clie_df.gral_edo_id LEFT JOIN gral_mun ON gral_mun.id = cxc_clie_df.gral_mun_id ) AS sbtdf ON sbtdf.id = fac_rems.cxc_clie_df_id "
        + "LEFT JOIN gral_mon ON gral_mon.id = fac_rems.moneda_id  "
        + "WHERE fac_rems.id="+id_remision;
        
        ArrayList<HashMap<String, Object>> hm = (ArrayList<HashMap<String, Object>>) this.jdbcTemplate.query(
            sql_query,  
            new Object[]{}, new RowMapper() {
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    HashMap<String, Object> row = new HashMap<String, Object>();
                    row.put("folio_remision",rs.getString("folio_remision"));
                    row.put("folio_pedido",rs.getString("folio_pedido"));
                    row.put("orden_compra",rs.getString("orden_compra"));
                    row.put("df_id",String.valueOf(rs.getInt("cxc_clie_df_id")));
                    row.put("direccion",rs.getString("direccion"));
                    row.put("fac_metodos_pago_id",String.valueOf(rs.getInt("fac_metodos_pago_id")));
                    row.put("no_cuenta",rs.getString("no_cuenta"));
                    row.put("adenda_id",String.valueOf(rs.getInt("adenda_id")));
                    row.put("moneda2",rs.getString("mon_iso4217_anterior"));
                    row.put("pdescto",String.valueOf(rs.getBoolean("pdescto")));
                    row.put("mdescto",rs.getString("mdescto"));
                    return row;
                }
            }
        );
        return hm;
    }
    
}
