package com.agnux.kemikal.springdaos;

import com.agnux.cfd.v2.Base64Coder;
import com.agnux.common.helpers.StringHelper;
import com.agnux.kemikal.controllers.PotCatCusorder;
import com.agnux.kemikal.interfacedaos.PocInterfaceDao;
import java.sql.ResultSet;
import java.sql.Types;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Date;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;


public class PocSpringDao implements PocInterfaceDao{
    private JdbcTemplate jdbcTemplate;

    public JdbcTemplate getJdbcTemplate() {
        return jdbcTemplate;
    }

    public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }
    
    @Override
    public HashMap<String, String> poc_val_cusorder(Integer usr_id, String curr_val, String date_lim, Integer pay_met, String account, String matrix)
    {
        String sql_to_query = "select poc_val_cusorder from poc_val_cusorder(" + usr_id + ",'" + curr_val + "','" + date_lim  + "'," + pay_met  + ",'" + account + "',array["+matrix+"]);";
        System.out.println("Validacion:"+sql_to_query);
        
        HashMap<String, String> hm = (HashMap<String, String>) this.jdbcTemplate.queryForObject(
            sql_to_query,
            new Object[]{}, new RowMapper() {
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    HashMap<String, String> row = new HashMap<String, String>();
                    row.put("success",rs.getString("poc_val_cusorder"));
                    return row;
                }
            }
        );
        return hm;
    }
    
    @Override
    public String poc_cat_cusorder(PotCatCusorder pc) {
        String valor_retorno="";
        Map<String, Object> update = this.getJdbcTemplate().queryForMap(pc.conform_cat_store());

        valor_retorno = update.get("poc_cat_cusorder").toString();

        return valor_retorno;
    }
    
    @Override
    public HashMap<String, String> selectFunctionValidateAaplicativo(String data, Integer idApp, String string_array) {
        String sql_to_query = "select erp_fn_validaciones_por_aplicativo from erp_fn_validaciones_por_aplicativo('"+data+"',"+idApp+",array["+string_array+"]);";
        //System.out.println("Validacion:"+sql_to_query);
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
    
    @Override
    public String selectFunctionForThisApp(String campos_data, String extra_data_array) {
        String sql_to_query = "select * from poc_adm_procesos('"+campos_data+"',array["+extra_data_array+"]);";
        
        //System.out.println("Ejacutando Guardar:"+sql_to_query);
        //int update = this.getJdbcTemplate().queryForInt(sql_to_query);
        //return update;
        String valor_retorno="";
        Map<String, Object> update = this.getJdbcTemplate().queryForMap(sql_to_query);

        valor_retorno = update.get("poc_adm_procesos").toString();

        return valor_retorno;
    }


    @Override
    public int countAll(String data_string) {
        String sql_busqueda = "select id from gral_bus_catalogos('"+data_string+"') as foo (id integer)";
        String sql_to_query = "select count(id)::int as total from ("+sql_busqueda+") as subt";

        int rowCount = this.getJdbcTemplate().queryForInt(sql_to_query);
        return rowCount;
    }

    @Override
    public ArrayList<HashMap<String, Object>> getPocPedidos_PaginaGrid(String data_string, int offset, int pageSize, String orderBy, String asc) {
        String sql_busqueda = "select id from gral_bus_catalogos(?) as foo (id integer)";
	String sql_to_query = "SELECT DISTINCT  "
                                    +"poc_pedidos.id, "
                                    +"poc_pedidos.folio, "
                                    +"cxc_clie.razon_social as cliente, "
                                    +"poc_pedidos.total, "
                                    +"gral_mon.descripcion_abr AS denominacion, "
                                    +"(CASE WHEN poc_pedidos.cancelado=TRUE THEN 'CANCELADO' ELSE erp_proceso_flujo.titulo END) as estado, "
                                    +"to_char(poc_pedidos.momento_creacion,'dd/mm/yyyy') as fecha_creacion,"
                                    + "gral_suc.titulo AS suc "
                            +"FROM poc_pedidos "
                            +"LEFT JOIN erp_proceso on erp_proceso.id = poc_pedidos.proceso_id "
                            +"LEFT JOIN fac_docs on fac_docs.proceso_id = erp_proceso.id "
                            +"LEFT JOIN erp_proceso_flujo on erp_proceso_flujo.id = erp_proceso.proceso_flujo_id "
                            +"LEFT JOIN cxc_clie on cxc_clie.id = poc_pedidos.cxc_clie_id "
                            +"LEFT JOIN gral_mon ON gral_mon.id=poc_pedidos.moneda_id "
                            + "LEFT JOIN gral_suc ON gral_suc.id=erp_proceso.sucursal_id "
                            +"JOIN ("+sql_busqueda+") as subt on subt.id=poc_pedidos.id "
                            + "order by "+orderBy+" "+asc+" limit ? OFFSET ?";

        //System.out.println("data_string: "+data_string);
        ArrayList<HashMap<String, Object>> hm = (ArrayList<HashMap<String, Object>>) this.jdbcTemplate.query(
            sql_to_query,
            new Object[]{data_string,new Integer(pageSize),new Integer(offset)}, new RowMapper() {
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    HashMap<String, Object> row = new HashMap<String, Object>();
                    row.put("id",rs.getInt("id"));
                    row.put("folio",rs.getString("folio"));
                    row.put("cliente",rs.getString("cliente"));
                    row.put("total",StringHelper.AgregaComas(StringHelper.roundDouble(rs.getString("total"),2)));
                    row.put("denominacion",rs.getString("denominacion"));
                    row.put("estado",rs.getString("estado"));
                    row.put("fecha_creacion",rs.getString("fecha_creacion"));
                    row.put("suc",rs.getString("suc"));
                    return row;
                }
            }
        );
        return hm;
    }




    //obtiene datos del header del pedido
    @Override
    public ArrayList<HashMap<String, String>> getPocPedido_Datos(Integer id_pedido) {
        String sql_query = ""
        + "SELECT poc_pedidos.id,"
                + "poc_pedidos.folio,"
                + "erp_proceso.proceso_flujo_id,"
                + "poc_pedidos.moneda_id,"                
                + "poc_pedidos.cfdi_usos_id,"
                + "poc_pedidos.cfdi_metodo_id,"                
                + "gral_mon.descripcion AS moneda,"
                + "poc_pedidos.observaciones,"
                + "cxc_clie.id AS cliente_id,"
                + "cxc_clie.numero_control,"
                + "cxc_clie.razon_social,"
                + "poc_pedidos.cxc_clie_df_id, "
                + "(CASE WHEN poc_pedidos.cxc_clie_df_id > 1 THEN "
                    + "sbtdf.calle||' '||sbtdf.numero_interior||' '||sbtdf.numero_exterior||', '||sbtdf.colonia||', '||sbtdf.municipio||', '||sbtdf.estado||', '||sbtdf.pais||' C.P. '||sbtdf.cp "
                + "ELSE "
                    + "cxc_clie.calle||' '||cxc_clie.numero||', '||cxc_clie.colonia||', '||gral_mun.titulo||', '||gral_edo.titulo||', '||gral_pais.titulo||' C.P. '||cxc_clie.cp "
                + "END ) AS direccion,"
                + "poc_pedidos.monto_descto,"
                + "poc_pedidos.subtotal,"
                + "poc_pedidos.monto_ieps,"
                + "poc_pedidos.impuesto,"
                + "poc_pedidos.monto_retencion,"
                + "poc_pedidos.total,"
                + "poc_pedidos.tipo_cambio,"
                + "poc_pedidos.cxc_agen_id,"
                + "poc_pedidos.cxp_prov_credias_id,"
                + "poc_pedidos.orden_compra, "
                + "poc_pedidos.fecha_compromiso,"
                + "poc_pedidos.lugar_entrega,"
                + "poc_pedidos.transporte,"
                + "poc_pedidos.cancelado,"
                + "poc_pedidos.tasa_retencion_immex,"
                + "poc_pedidos.tipo_documento, "
                + "poc_pedidos.fac_metodos_pago_id AS metodo_pago_id,"
                + "poc_pedidos.no_cuenta, "
                + "poc_pedidos.enviar_ruta, "
                + "cxc_clie.cta_pago_mn, "
                + "cxc_clie.cta_pago_usd,"
                + "cxc_clie.lista_precio,"
                + "poc_pedidos.enviar_obser_fac,"
                + "poc_pedidos.flete,"
                + "(CASE WHEN poc_pedidos.monto_descto>0 THEN true ELSE false END) AS pdescto, "
                + "(CASE WHEN poc_pedidos.monto_descto>0 THEN (CASE WHEN poc_pedidos.motivo_descto IS NULL THEN '' ELSE poc_pedidos.motivo_descto END) ELSE '' END) AS mdescto, "
                + "(CASE WHEN poc_pedidos.monto_descto IS NULL THEN 0 ELSE poc_pedidos.porcentaje_descto END) AS porcentaje_descto,"
                + "(CASE WHEN poc_pedidos.folio_cot IS NULL THEN ' ' ELSE (CASE WHEN poc_pedidos.folio_cot='' THEN ' ' ELSE poc_pedidos.folio_cot END) END) AS folio_cot "
        + "FROM poc_pedidos "
        + "LEFT JOIN erp_proceso ON erp_proceso.id = poc_pedidos.proceso_id "
        + "LEFT JOIN gral_mon ON gral_mon.id = poc_pedidos.moneda_id "
        + "LEFT JOIN cxc_clie ON cxc_clie.id=poc_pedidos.cxc_clie_id "
        + "LEFT JOIN gral_pais ON gral_pais.id = cxc_clie.pais_id "
        + "LEFT JOIN gral_edo ON gral_edo.id = cxc_clie.estado_id "
        + "LEFT JOIN gral_mun ON gral_mun.id = cxc_clie.municipio_id "
        + "LEFT JOIN (SELECT cxc_clie_df.id, (CASE WHEN cxc_clie_df.calle IS NULL THEN '' ELSE cxc_clie_df.calle END) AS calle, (CASE WHEN cxc_clie_df.numero_interior IS NULL THEN '' ELSE (CASE WHEN cxc_clie_df.numero_interior IS NULL OR cxc_clie_df.numero_interior='' THEN '' ELSE 'NO.INT.'||cxc_clie_df.numero_interior END)  END) AS numero_interior, (CASE WHEN cxc_clie_df.numero_exterior IS NULL THEN '' ELSE (CASE WHEN cxc_clie_df.numero_exterior IS NULL OR cxc_clie_df.numero_exterior='' THEN '' ELSE 'NO.EXT.'||cxc_clie_df.numero_exterior END )  END) AS numero_exterior, (CASE WHEN cxc_clie_df.colonia IS NULL THEN '' ELSE cxc_clie_df.colonia END) AS colonia,(CASE WHEN gral_mun.id IS NULL OR gral_mun.id=0 THEN '' ELSE gral_mun.titulo END) AS municipio,(CASE WHEN gral_edo.id IS NULL OR gral_edo.id=0 THEN '' ELSE gral_edo.titulo END) AS estado,(CASE WHEN gral_pais.id IS NULL OR gral_pais.id=0 THEN '' ELSE gral_pais.titulo END) AS pais,(CASE WHEN cxc_clie_df.cp IS NULL THEN '' ELSE cxc_clie_df.cp END) AS cp  FROM cxc_clie_df LEFT JOIN gral_pais ON gral_pais.id = cxc_clie_df.gral_pais_id LEFT JOIN gral_edo ON gral_edo.id = cxc_clie_df.gral_edo_id LEFT JOIN gral_mun ON gral_mun.id = cxc_clie_df.gral_mun_id ) AS sbtdf ON sbtdf.id = poc_pedidos.cxc_clie_df_id "
        + "WHERE poc_pedidos.id="+id_pedido;
        
        //System.err.println("PEDIDO_DATOS: "+sql_query);
        
        ArrayList<HashMap<String, String>> hm = (ArrayList<HashMap<String, String>>) this.jdbcTemplate.query(
            sql_query,
            new Object[]{}, new RowMapper() {
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    HashMap<String, String> row = new HashMap<String, String>();
                    row.put("id",String.valueOf(rs.getInt("id")));
                    row.put("tipo_documento",String.valueOf(rs.getInt("tipo_documento")));
                    row.put("folio",rs.getString("folio"));
                    row.put("proceso_flujo_id",String.valueOf(rs.getInt("proceso_flujo_id")));
                    row.put("moneda_id",rs.getString("moneda_id"));
                    row.put("cfdi_usos_id",rs.getString("cfdi_usos_id"));
                    row.put("cfdi_metodo_id",rs.getString("cfdi_metodo_id"));
                    row.put("moneda",rs.getString("moneda"));
                    row.put("observaciones",rs.getString("observaciones"));
                    row.put("cliente_id",rs.getString("cliente_id"));
                    row.put("numero_control",rs.getString("numero_control"));
                    row.put("razon_social",rs.getString("razon_social"));
                    row.put("df_id",String.valueOf(rs.getInt("cxc_clie_df_id")));
                    row.put("direccion",rs.getString("direccion"));
                    row.put("monto_descto",StringHelper.roundDouble(rs.getDouble("monto_descto"),2));
                    row.put("subtotal",StringHelper.roundDouble(rs.getDouble("subtotal"),2));
                    row.put("monto_ieps",StringHelper.roundDouble(rs.getDouble("monto_ieps"),2));
                    row.put("impuesto",StringHelper.roundDouble(rs.getDouble("impuesto"),2));
                    row.put("retencion",StringHelper.roundDouble(rs.getDouble("monto_retencion"),2));
                    row.put("total",StringHelper.roundDouble(rs.getDouble("total"),2));
                    row.put("tipo_cambio",StringHelper.roundDouble(rs.getDouble("tipo_cambio"),4));
                    row.put("cxc_agen_id",rs.getString("cxc_agen_id"));
                    row.put("cxp_prov_credias_id",rs.getString("cxp_prov_credias_id"));
                    row.put("orden_compra",rs.getString("orden_compra"));
                    row.put("fecha_compromiso",String.valueOf(rs.getDate("fecha_compromiso")));
                    row.put("lugar_entrega",rs.getString("lugar_entrega"));
                    row.put("transporte",rs.getString("transporte"));
                    row.put("cancelado",String.valueOf(rs.getBoolean("cancelado")));
                    row.put("tasa_retencion_immex",StringHelper.roundDouble(rs.getDouble("tasa_retencion_immex"),2));
                    row.put("metodo_pago_id",String.valueOf(rs.getInt("metodo_pago_id")));
                    row.put("no_cuenta",rs.getString("no_cuenta"));
                    row.put("enviar_ruta",String.valueOf(rs.getBoolean("enviar_ruta")));
                    row.put("cta_pago_mn",rs.getString("cta_pago_mn"));
                    row.put("cta_pago_usd",rs.getString("cta_pago_usd"));
                    row.put("lista_precio",rs.getString("lista_precio"));
                    row.put("enviar_obser",String.valueOf(rs.getBoolean("enviar_obser_fac")));
                    row.put("flete",String.valueOf(rs.getBoolean("flete")));
                    row.put("pdescto",String.valueOf(rs.getBoolean("pdescto")));
                    row.put("mdescto",rs.getString("mdescto"));
                    row.put("porcentaje_descto",StringHelper.roundDouble(rs.getDouble("porcentaje_descto"),4));
                    row.put("folio_cot",rs.getString("folio_cot"));
                    return row;
                }
            }
        );
        return hm;
    }
    
    
    
    @Override
    public ArrayList<HashMap<String, String>> getPocPedido_DatosGrid(Integer id_pedido) {
        String sql_query = ""
        + "SELECT poc_pedidos_detalle.id as id_detalle,"
            + "poc_pedidos_detalle.inv_prod_id,"
            + "inv_prod.sku AS codigo,"
            + "inv_prod.descripcion AS titulo,"
            + "(CASE WHEN poc_pedidos_detalle.gral_ieps_id>0 THEN ' - IEPS '||(round((poc_pedidos_detalle.valor_ieps * 100::double precision)::numeric,2))||'%' ELSE '' END) AS etiqueta_ieps,"
            + "poc_pedidos_detalle.inv_prod_unidad_id, "
            + "(CASE WHEN inv_prod_unidades.titulo IS NULL THEN '' ELSE inv_prod_unidades.titulo END) as unidad,"
            + "(CASE WHEN inv_prod_unidades.decimales IS NULL THEN 0 ELSE inv_prod_unidades.decimales END) AS no_dec,"
            + "(CASE WHEN inv_prod_presentaciones.id IS NULL THEN 0 ELSE inv_prod_presentaciones.id END) as id_presentacion,"
            + "(CASE WHEN inv_prod_presentaciones.titulo IS NULL THEN '' ELSE inv_prod_presentaciones.titulo END) as presentacion,"
            + "poc_pedidos_detalle.cantidad,"
            + "poc_pedidos_detalle.precio_unitario,"
            + "(poc_pedidos_detalle.cantidad * poc_pedidos_detalle.precio_unitario) AS importe, "
            + "(CASE WHEN poc_pedidos_detalle.gral_ieps_id>0 THEN ((poc_pedidos_detalle.cantidad * poc_pedidos_detalle.precio_unitario) * poc_pedidos_detalle.valor_ieps) ELSE 0 END) AS importe_ieps,"
            + "poc_pedidos_detalle.gral_imp_id,"
            + "poc_pedidos_detalle.valor_imp,"
            + "poc_pedidos_detalle.gral_ieps_id,"
            + "(poc_pedidos_detalle.valor_ieps * 100) AS valor_ieps, "
                
            + "poc_pedidos_detalle.gral_imptos_ret_id as ret_id,"
            + "(poc_pedidos_detalle.tasa_ret * 100) AS ret_tasa, "
                
            + "(CASE WHEN poc_pedidos_detalle.backorder=TRUE OR poc_pedidos_detalle.requisicion=TRUE THEN 'checked' ELSE '' END) AS valor_check, "
            + "(CASE WHEN poc_pedidos_detalle.backorder=TRUE THEN 1 ELSE 0 END) AS valor_selecionado, "
            + "(poc_pedidos_detalle.cantidad - poc_pedidos_detalle.reservado) AS cant_produccion, "
            + "(CASE WHEN poc_pedidos_detalle.descto IS NULL THEN 0 ELSE poc_pedidos_detalle.descto END) AS descto,"
            + "(CASE WHEN poc_ped_cot.id IS NULL THEN 0 ELSE poc_ped_cot.poc_cot_id END) as id_cot, "
            + "(CASE WHEN poc_ped_cot.id IS NULL THEN 0 ELSE poc_ped_cot.poc_cot_det_id END) as id_cot_det, "
            + "(case when poc_pedidos_detalle.autorizado=true then 1 else 0 end) as status_aut,"
            + "poc_pedidos_detalle.precio_aut,"
            + "poc_pedidos_detalle.gral_usr_id_aut,"
            + "poc_pedidos_detalle.requiere_aut "
        + "FROM poc_pedidos_detalle "
        + "LEFT JOIN inv_prod on inv_prod.id = poc_pedidos_detalle.inv_prod_id "
        + "LEFT JOIN inv_prod_unidades on inv_prod_unidades.id = poc_pedidos_detalle.inv_prod_unidad_id "
        + "LEFT JOIN inv_prod_presentaciones on inv_prod_presentaciones.id = poc_pedidos_detalle.presentacion_id "
        + "LEFT JOIN poc_ped_cot on (poc_ped_cot.poc_ped_id = poc_pedidos_detalle.poc_pedido_id and poc_ped_cot.poc_ped_det_id=poc_pedidos_detalle.id) "
        + "WHERE poc_pedidos_detalle.poc_pedido_id=? ORDER BY poc_pedidos_detalle.id;";
        
        //System.out.println("Obtiene datos grid prefactura: "+sql_query);
        ArrayList<HashMap<String, String>> hm_grid = (ArrayList<HashMap<String, String>>) this.jdbcTemplate.query(
            sql_query,
            new Object[]{new Integer(id_pedido)}, new RowMapper() {
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    HashMap<String, String> row = new HashMap<String, String>();
                    row.put("id_detalle",String.valueOf(rs.getInt("id_detalle")));
                    row.put("inv_prod_id",String.valueOf(rs.getInt("inv_prod_id")));
                    row.put("codigo",rs.getString("codigo"));
                    row.put("titulo",rs.getString("titulo"));
                    row.put("unidad_id",String.valueOf(rs.getInt("inv_prod_unidad_id")));
                    
                    row.put("unidad",rs.getString("unidad"));
                    row.put("no_dec",String.valueOf(rs.getInt("no_dec")));
                    row.put("id_presentacion",String.valueOf(rs.getInt("id_presentacion")));
                    row.put("presentacion",rs.getString("presentacion").toUpperCase());
                    row.put("cantidad",StringHelper.roundDouble( rs.getString("cantidad"), rs.getInt("no_dec") ));
                    row.put("precio_unitario",StringHelper.roundDouble(rs.getDouble("precio_unitario"),4) );
                    row.put("importe",StringHelper.roundDouble(rs.getDouble("importe"),4) );
                    row.put("gral_imp_id",String.valueOf(rs.getInt("gral_imp_id")));
                    row.put("valor_imp",StringHelper.roundDouble(rs.getDouble("valor_imp"),2));
                    
                    row.put("valor_check",rs.getString("valor_check"));
                    row.put("valor_selecionado",String.valueOf(rs.getInt("valor_selecionado")));
                    row.put("cant_produccion",StringHelper.roundDouble(rs.getDouble("cant_produccion"), rs.getInt("no_dec") ) );
                    
                    row.put("ieps_id",String.valueOf(rs.getInt("gral_ieps_id")));
                    row.put("valor_ieps",StringHelper.roundDouble(rs.getString("valor_ieps"),2));
                    row.put("ret_id",String.valueOf(rs.getInt("ret_id")));
                    row.put("ret_tasa",StringHelper.roundDouble(rs.getString("ret_tasa"),2));
                    
                    //Valores para el PDF
                    row.put("etiqueta_ieps",rs.getString("etiqueta_ieps"));
                    row.put("importe_ieps",StringHelper.roundDouble(rs.getDouble("importe_ieps"),2) );
                    
                    row.put("descto",StringHelper.roundDouble(rs.getDouble("descto"),4) );
                    
                    row.put("id_cot",String.valueOf(rs.getInt("id_cot")));
                    row.put("id_cot_det",String.valueOf(rs.getInt("id_cot_det")));
                    
                    row.put("status_aut",String.valueOf(rs.getInt("status_aut"))+"&&&"+StringHelper.roundDouble(rs.getDouble("precio_aut"),4)+"&&&"+Base64Coder.encodeString(String.valueOf(rs.getInt("gral_usr_id_aut"))));
                    row.put("requiere_aut",String.valueOf(rs.getBoolean("requiere_aut")));
                    
                    return row;
                }
            }
        );
        return hm_grid;
    }


    
    
    //obtine datos de la cotizacion
    @Override
    public ArrayList<HashMap<String, String>> getPocPedido_DatosCotizacion(String folio_cotizacion, Integer id_empresa) {
        String sql_query = ""
        + "SELECT "
            + "poc_cot.id as id_cot,"
            + "poc_cot.folio,"
            + "erp_proceso.proceso_flujo_id,"
            + "poc_cot.gral_mon_id AS moneda_id,"
            + "gral_mon.descripcion AS moneda,"
            + "poc_cot.observaciones,"
            + "cxc_clie.id AS cliente_id,"
            + "cxc_clie.numero_control,"
            + "cxc_clie.razon_social,"
            + "(CASE WHEN tbldf.cxc_clie_id IS NULL THEN false ELSE true END ) AS tiene_dir_fiscal,"
            +"cxc_clie.calle||' '||cxc_clie.numero||', '||cxc_clie.colonia||', '||gral_mun.titulo||', '||gral_edo.titulo||', '||gral_pais.titulo||' C.P. '||cxc_clie.cp as direccion, "
            + "0::double precision AS monto_descto,"
            + "poc_cot.subtotal,"
            + "0::double precision AS monto_ieps,"
            + "poc_cot.impuesto,"
            + "0::double precision AS monto_retencion,"
            + "poc_cot.total,"
            + "poc_cot.tipo_cambio,"
            + "poc_cot.tc_usd,"
            + "poc_cot.cxc_agen_id,"
            + "cxc_clie.dias_credito_id,"
            + "cxc_clie.credito_suspendido, "
            + "cxc_clie.empresa_immex,"
            + "cxc_clie.tasa_ret_immex,"
            + "cxc_clie.fac_metodos_pago_id AS metodo_pago_id,"
            + "''::character varying  AS no_cuenta, "
            + "false::boolean AS enviar_ruta, "
            + "cxc_clie.cta_pago_mn, "
            + "cxc_clie.cta_pago_usd,"
            + "cxc_clie.lista_precio,"
            + "false::boolean AS enviar_obser_fac,"
            + "false::boolean AS flete,"
            + "true::boolean pdescto, "
            + "0::double precision AS mdescto, "
            + "0::double precision AS porcentaje_descto "
        + "FROM poc_cot JOIN poc_cot_clie ON poc_cot_clie.poc_cot_id=poc_cot.id "
        + "LEFT JOIN erp_proceso ON erp_proceso.id = poc_cot.proceso_id  "
        + "LEFT JOIN gral_mon ON gral_mon.id = poc_cot.gral_mon_id  "
        + "JOIN cxc_clie ON cxc_clie.id=poc_cot_clie.cxc_clie_id "
        + "JOIN gral_pais ON gral_pais.id = cxc_clie.pais_id "
        + "JOIN gral_edo ON gral_edo.id = cxc_clie.estado_id "
        + "JOIN gral_mun ON gral_mun.id = cxc_clie.municipio_id "
        + "LEFT JOIN (SELECT DISTINCT cxc_clie_id FROM cxc_clie_df WHERE borrado_logico=false) AS tbldf ON tbldf.cxc_clie_id=cxc_clie.id "
        + "WHERE poc_cot.folio=? AND erp_proceso.empresa_id=?";
        
        //System.out.println("Obteniendo datos de la cotizacion: "+sql_query);
        ArrayList<HashMap<String, String>> hm = (ArrayList<HashMap<String, String>>) this.jdbcTemplate.query(
            sql_query,
            new Object[]{folio_cotizacion, new Integer(id_empresa)}, new RowMapper() {
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    HashMap<String, String> row = new HashMap<String, String>();
                    row.put("id_cot",String.valueOf(rs.getInt("id_cot")));
                    row.put("folio",rs.getString("folio"));
                    row.put("proceso_flujo_id",String.valueOf(rs.getInt("proceso_flujo_id")));
                    row.put("moneda_id",rs.getString("moneda_id"));
                    row.put("moneda",rs.getString("moneda"));
                    row.put("observaciones",rs.getString("observaciones"));
                    row.put("cliente_id",rs.getString("cliente_id"));
                    row.put("numero_control",rs.getString("numero_control"));
                    row.put("razon_social",rs.getString("razon_social"));
                    row.put("direccion",rs.getString("direccion"));
                    row.put("monto_descto",StringHelper.roundDouble(rs.getDouble("monto_descto"),2));
                    row.put("subtotal",StringHelper.roundDouble(rs.getDouble("subtotal"),2));
                    row.put("monto_ieps",StringHelper.roundDouble(rs.getDouble("monto_ieps"),2));
                    row.put("impuesto",StringHelper.roundDouble(rs.getDouble("impuesto"),2));
                    row.put("retencion",StringHelper.roundDouble(rs.getDouble("monto_retencion"),2));
                    row.put("total",StringHelper.roundDouble(rs.getDouble("total"),2));
                    row.put("tipo_cambio",StringHelper.roundDouble(rs.getDouble("tipo_cambio"),4));
                    row.put("tc_usd",StringHelper.roundDouble(rs.getDouble("tc_usd"),4));
                    row.put("cxc_agen_id",rs.getString("cxc_agen_id"));
                    row.put("dias_credito_id",rs.getString("dias_credito_id"));
                    row.put("credito_suspendido",String.valueOf(rs.getBoolean("credito_suspendido")));
                    row.put("tiene_df",String.valueOf(rs.getBoolean("tiene_dir_fiscal")));
                    row.put("empresa_immex",String.valueOf(rs.getBoolean("empresa_immex")));
                    row.put("tasa_retencion_immex",StringHelper.roundDouble(rs.getDouble("tasa_ret_immex"),2));
                    row.put("metodo_pago_id",String.valueOf(rs.getInt("metodo_pago_id")));
                    row.put("no_cuenta",rs.getString("no_cuenta"));
                    row.put("enviar_ruta",String.valueOf(rs.getBoolean("enviar_ruta")));
                    
                    row.put("cta_pago_mn",rs.getString("cta_pago_mn"));
                    row.put("cta_pago_usd",rs.getString("cta_pago_usd"));
                    row.put("lista_precio",rs.getString("lista_precio"));
                    row.put("enviar_obser",String.valueOf(rs.getBoolean("enviar_obser_fac")));
                    row.put("flete",String.valueOf(rs.getBoolean("flete")));
                    row.put("pdescto",String.valueOf(rs.getBoolean("pdescto")));
                    row.put("mdescto",rs.getString("mdescto"));
                    row.put("porcentaje_descto",StringHelper.roundDouble(rs.getDouble("porcentaje_descto"),4));
                    return row;
                }
            }
        );
        return hm;
    }
    
    
    
    
    
    
    
    //obtine datos del cliente
    @Override
    public ArrayList<HashMap<String, String>> getPocPedido_DatosCotizacionGrid(String id_cot) {
        String sql_query = ""
        + "SELECT "
            + "poc_cot_detalle.id AS id_det,"
            + "inv_prod.id,"
            + "inv_prod.sku,"
            + "inv_prod.descripcion AS titulo,"
            + "poc_cot_detalle.gral_impto_id AS iva_id,"
            + "poc_cot_detalle.valor_imp AS valor_impto_prod,"
            + "(CASE WHEN inv_prod.ieps=0 THEN 0 ELSE gral_ieps.id END) AS ieps_id, "
            + "(CASE WHEN inv_prod.ieps=0 THEN 0 ELSE gral_ieps.tasa END) AS ieps_tasa, "
            + "inv_prod.unidad_id,"
            + "(CASE WHEN inv_prod_unidades.titulo IS NULL THEN '' ELSE inv_prod_unidades.titulo END) AS unidad,"
            + "(CASE WHEN inv_prod_presentaciones.id IS NULL THEN 0 ELSE inv_prod_presentaciones.id END) AS id_presentacion,"
            + "(CASE WHEN inv_prod_presentaciones.titulo IS NULL THEN '' ELSE inv_prod_presentaciones.titulo END) AS presentacion, "
            + "(CASE WHEN inv_prod_unidades.decimales IS NULL THEN 0 ELSE inv_prod_unidades.decimales END) AS  decimales,  "
            + "poc_cot_detalle.cantidad,"
            + "poc_cot_detalle.precio_unitario AS precio, "
            + "(case when poc_cot_detalle.autorizado=true then 1 else 0 end) as status_aut,"
            + "poc_cot_detalle.precio_aut,"
            + "poc_cot_detalle.gral_usr_id_aut,"
            + "poc_cot_detalle.requiere_aut "
        + "FROM poc_cot_detalle  "
        + "LEFT JOIN inv_prod on inv_prod.id = poc_cot_detalle.inv_prod_id "
        + "LEFT JOIN inv_prod_unidades on inv_prod_unidades.id = poc_cot_detalle.inv_prod_unidad_id "
        + "LEFT JOIN inv_prod_presentaciones on inv_prod_presentaciones.id = poc_cot_detalle.inv_presentacion_id "
        + "LEFT JOIN gral_ieps ON gral_ieps.id=inv_prod.ieps "
        + "WHERE poc_cot_detalle.poc_cot_id=? ORDER BY poc_cot_detalle.id;";

        //System.out.println("Obteniendo datos de la cotizacion: "+sql_query);
        ArrayList<HashMap<String, String>> hm = (ArrayList<HashMap<String, String>>) this.jdbcTemplate.query(
            sql_query,
            new Object[]{new Integer(id_cot)}, new RowMapper() {
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    HashMap<String, String> row = new HashMap<String, String>();
                    row.put("id_det",String.valueOf(rs.getInt("id_det")));
                    row.put("id",String.valueOf(rs.getInt("id")));
                    row.put("sku",rs.getString("sku"));
                    row.put("titulo",rs.getString("titulo"));
                    row.put("iva_id",String.valueOf(rs.getInt("iva_id")));
                    row.put("valor_impto_prod",StringHelper.roundDouble(rs.getDouble("valor_impto_prod"),2));
                    row.put("ieps_id",String.valueOf(rs.getInt("ieps_id")));
                    row.put("ieps_tasa",StringHelper.roundDouble(rs.getDouble("ieps_tasa"),4));
                    row.put("unidad_id",String.valueOf(rs.getInt("unidad_id")));
                    row.put("unidad",rs.getString("unidad"));
                    row.put("id_presentacion",String.valueOf(rs.getInt("id_presentacion")));
                    row.put("presentacion",rs.getString("presentacion"));
                    row.put("decimales",StringHelper.roundDouble(rs.getDouble("decimales"),2));
                    
                    row.put("cantidad",StringHelper.roundDouble(rs.getDouble("cantidad"),4));
                    row.put("precio",StringHelper.roundDouble(rs.getDouble("precio"),4));
                    
                    row.put("status_aut",String.valueOf(rs.getInt("status_aut"))+"&&&"+StringHelper.roundDouble(rs.getDouble("precio_aut"),4)+"&&&"+Base64Coder.encodeString(String.valueOf(rs.getInt("gral_usr_id_aut"))));
                    row.put("requiere_aut",String.valueOf(rs.getBoolean("requiere_aut")));
                    
                    return row;
                }
            }
        );
        return hm;
    }
    
    
    
    
    
    
    
    
    
    

   @Override
    public HashMap<String, String> getDatosPDF(Integer id_pedido) {
        HashMap<String, String> mappdf = new HashMap<String, String>();

        String sql_query = ""
        + "SELECT poc_pedidos.id,"
                + "poc_pedidos.folio,"
                + "erp_proceso.proceso_flujo_id,"
                + "poc_pedidos.moneda_id,"
                + "gral_mon.descripcion as moneda,"
                + "gral_mon.simbolo AS simbolo_moneda,"
                + "poc_pedidos.observaciones,"
                + "cxc_clie.id as cliente_id,"
                + "cxc_clie.numero_control,"
                + "cxc_clie.razon_social,"
                + "(CASE WHEN poc_pedidos.cxc_clie_df_id > 1 THEN sbtdf.calle ELSE cxc_clie.calle END ) AS calle,"
                + "(CASE WHEN poc_pedidos.cxc_clie_df_id > 1 THEN (sbtdf.numero_interior||' '||sbtdf.numero_exterior) ELSE cxc_clie.numero END ) AS numero,"
                + "(CASE WHEN poc_pedidos.cxc_clie_df_id > 1 THEN sbtdf.colonia ELSE cxc_clie.colonia END ) AS colonia,"
                + "(CASE WHEN poc_pedidos.cxc_clie_df_id > 1 THEN sbtdf.municipio ELSE gral_mun.titulo END ) AS municipio,"
                + "(CASE WHEN poc_pedidos.cxc_clie_df_id > 1 THEN sbtdf.estado ELSE gral_edo.titulo END ) AS Estado,"
                + "(CASE WHEN poc_pedidos.cxc_clie_df_id > 1 THEN sbtdf.pais ELSE gral_pais.titulo END ) AS pais,"
                + "(CASE WHEN poc_pedidos.cxc_clie_df_id > 1 THEN sbtdf.cp ELSE cxc_clie.cp END ) AS cp,"
                + "cxc_clie.rfc AS rfc,"
                + "cxc_clie.telefono1 AS telefono, "
                + "poc_pedidos.subtotal, "
                + "poc_pedidos.monto_ieps, "
                + "poc_pedidos.impuesto, "
                + "poc_pedidos.total,"
                + "poc_pedidos.tipo_cambio, "
                + "poc_pedidos.monto_retencion, "
                + "poc_pedidos.cxc_agen_id,"
                + "poc_pedidos.cxp_prov_credias_id,"
                + "poc_pedidos.orden_compra, "
                + "to_char(poc_pedidos.fecha_compromiso::timestamp with time zone,'dd/mm/yyyy') AS fecha_compromiso, "
                + "poc_pedidos.lugar_entrega, "
                + "poc_pedidos.transporte, "
                + "poc_pedidos.cancelado, "
                + "poc_pedidos.tasa_retencion_immex, "
                + "poc_pedidos.observaciones,"
                + "to_char(poc_pedidos.momento_creacion,'dd/mm/yyyy HH24:MI') AS fecha_expedicion, "
                + "poc_pedidos.gral_usr_id_autoriza, "
                + "(case when gral_empleados.id is null then '' else (CASE WHEN poc_pedidos.gral_usr_id_autoriza=0 THEN '' ELSE gral_empleados.nombre_pila||' ' ||gral_empleados.apellido_paterno||' ' ||gral_empleados.apellido_materno END) end) AS nombre_autorizo_pedido,  "
                + "(case when cxc_agen.nombre is null then '' else cxc_agen.nombre  end) AS nombre_agente,  "
                + "poc_pedidos.cancelado,"
                + "poc_pedidos.flete "
        + "FROM poc_pedidos "
        + "LEFT JOIN erp_proceso ON erp_proceso.id = poc_pedidos.proceso_id "
        + "LEFT JOIN gral_mon ON gral_mon.id = poc_pedidos.moneda_id "
        + "LEFT JOIN cxc_clie ON cxc_clie.id=poc_pedidos.cxc_clie_id "
        + "LEFT JOIN gral_pais ON gral_pais.id = cxc_clie.pais_id "
        + "LEFT JOIN gral_edo ON gral_edo.id = cxc_clie.estado_id "
        + "LEFT JOIN gral_mun ON gral_mun.id = cxc_clie.municipio_id "
        + "LEFT JOIN gral_usr ON gral_usr.id = poc_pedidos.gral_usr_id_autoriza "
        + "LEFT JOIN gral_empleados ON gral_empleados.id = gral_usr.gral_empleados_id  "
        + "LEFT JOIN cxc_agen ON cxc_agen.id = poc_pedidos.cxc_agen_id "
        + "LEFT JOIN (SELECT cxc_clie_df.id, (CASE WHEN cxc_clie_df.calle IS NULL THEN '' ELSE cxc_clie_df.calle END) AS calle, (CASE WHEN cxc_clie_df.numero_interior IS NULL THEN '' ELSE (CASE WHEN cxc_clie_df.numero_interior IS NULL OR cxc_clie_df.numero_interior='' THEN '' ELSE 'NO.INT.'||cxc_clie_df.numero_interior END)  END) AS numero_interior, (CASE WHEN cxc_clie_df.numero_exterior IS NULL THEN '' ELSE (CASE WHEN cxc_clie_df.numero_exterior IS NULL OR cxc_clie_df.numero_exterior='' THEN '' ELSE 'NO.EXT.'||cxc_clie_df.numero_exterior END )  END) AS numero_exterior, (CASE WHEN cxc_clie_df.colonia IS NULL THEN '' ELSE cxc_clie_df.colonia END) AS colonia,(CASE WHEN gral_mun.id IS NULL OR gral_mun.id=0 THEN '' ELSE gral_mun.titulo END) AS municipio,(CASE WHEN gral_edo.id IS NULL OR gral_edo.id=0 THEN '' ELSE gral_edo.titulo END) AS estado,(CASE WHEN gral_pais.id IS NULL OR gral_pais.id=0 THEN '' ELSE gral_pais.titulo END) AS pais,(CASE WHEN cxc_clie_df.cp IS NULL THEN '' ELSE cxc_clie_df.cp END) AS cp  FROM cxc_clie_df LEFT JOIN gral_pais ON gral_pais.id = cxc_clie_df.gral_pais_id LEFT JOIN gral_edo ON gral_edo.id = cxc_clie_df.gral_edo_id LEFT JOIN gral_mun ON gral_mun.id = cxc_clie_df.gral_mun_id ) AS sbtdf ON sbtdf.id = poc_pedidos.cxc_clie_df_id "
        + "WHERE poc_pedidos.id="+id_pedido;

        //System.out.println("DatosPdfPedido:"+sql_query);
        Map<String, Object> mapdatosquery = this.getJdbcTemplate().queryForMap(sql_query);

        mappdf.put("pedido_id", mapdatosquery.get("id").toString());
        mappdf.put("folio", mapdatosquery.get("folio").toString());
        mappdf.put("proceso_flujo_id", mapdatosquery.get("proceso_flujo_id").toString());
        mappdf.put("moneda_id", mapdatosquery.get("moneda_id").toString() );
        mappdf.put("moneda", mapdatosquery.get("moneda").toString() );
        mappdf.put("simbolo_moneda", mapdatosquery.get("simbolo_moneda").toString() );
        mappdf.put("observaciones", mapdatosquery.get("observaciones").toString() );
        mappdf.put("cliente_id", mapdatosquery.get("cliente_id").toString() );
        mappdf.put("numero_control", mapdatosquery.get("numero_control").toString() );
        mappdf.put("razon_social", mapdatosquery.get("razon_social").toString() );
        
        mappdf.put("calle", mapdatosquery.get("calle").toString() );
        mappdf.put("numero", mapdatosquery.get("numero").toString() );
        mappdf.put("colonia", mapdatosquery.get("colonia").toString() );
        mappdf.put("municipio", mapdatosquery.get("municipio").toString() );
        mappdf.put("Estado", mapdatosquery.get("Estado").toString() );
        mappdf.put("pais", mapdatosquery.get("pais").toString() );
        mappdf.put("cp", mapdatosquery.get("cp").toString() );
        mappdf.put("rfc", mapdatosquery.get("rfc").toString() );
        mappdf.put("telefono", mapdatosquery.get("telefono").toString() );

        mappdf.put("observaciones", mapdatosquery.get("observaciones").toString() );
        mappdf.put("monto_retencion", mapdatosquery.get("monto_retencion").toString() );
        mappdf.put("nombre_autorizo_pedido", mapdatosquery.get("nombre_autorizo_pedido").toString() );
        mappdf.put("nombre_agente", mapdatosquery.get("nombre_agente").toString() );
        
        //mappdf.put("direccion", mapdatosquery.get("direccion").toString() );
        mappdf.put("subtotal", StringHelper.roundDouble(mapdatosquery.get("subtotal").toString(),2) );
        mappdf.put("monto_ieps", StringHelper.roundDouble(mapdatosquery.get("monto_ieps").toString(),2) );
        mappdf.put("impuesto", StringHelper.roundDouble(mapdatosquery.get("impuesto").toString(),2) );
        mappdf.put("total", StringHelper.roundDouble(mapdatosquery.get("total").toString(),2) );
        mappdf.put("tipo_cambio", StringHelper.roundDouble(mapdatosquery.get("tipo_cambio").toString(),2) );
        mappdf.put("cxc_agen_id", mapdatosquery.get("cxc_agen_id").toString() );
        mappdf.put("cxp_prov_credias_id", mapdatosquery.get("cxp_prov_credias_id").toString() );
        mappdf.put("orden_compra", mapdatosquery.get("orden_compra").toString() );
        mappdf.put("fecha_compromiso", mapdatosquery.get("fecha_compromiso").toString() );
        mappdf.put("lugar_entrega", mapdatosquery.get("lugar_entrega").toString() );
        mappdf.put("transporte", mapdatosquery.get("transporte").toString() );
        mappdf.put("cancelado", mapdatosquery.get("cancelado").toString() );
        mappdf.put("tasa_retencion_immex", mapdatosquery.get("tasa_retencion_immex").toString() );
        mappdf.put("fecha_expedicion", mapdatosquery.get("fecha_expedicion").toString() );
        mappdf.put("cancelado", mapdatosquery.get("cancelado").toString() );
        mappdf.put("flete", mapdatosquery.get("flete").toString() );

        return mappdf;
    }
   
   
   
   
    //Obtiene datos para el pedido cuando la empresa es transportista
    @Override
    public ArrayList<HashMap<String, String>> getPocPedido_DatosTrans(Integer id_pedido) {
        String sql_query = ""
        + "SELECT ped_trans.documentador,"
            + "ped_trans.valor_declarado,"
            + "ped_trans.tipo_viaje,"
            + "ped_trans.remolque1,"
            + "ped_trans.remolque2,"
            + "ped_trans.no_operador,"
            + "ped_trans.nombre_operador,"
            + "ped_trans.gral_mun_id_orig AS mun_id_orig,"
            + "ped_trans.gral_edo_id_orig AS edo_id_orig,"
            + "ped_trans.gral_pais_id_orig AS pais_id_orig,"
            + "ped_trans.gral_mun_id_dest AS mun_id_dest,"
            + "ped_trans.gral_edo_id_dest AS edo_id_dest,"
            + "ped_trans.gral_pais_id_dest AS pais_id_dest,"
            + "ped_trans.trans_observaciones,"
            + "(CASE WHEN log_vehiculos.id IS NULL THEN 0 ELSE log_vehiculos.id END) AS vehiculo_id,"
            + "(CASE WHEN log_vehiculos.id IS NULL THEN '' ELSE log_vehiculos.numero_economico END) AS vehiculo_no,"
            + "(CASE WHEN log_vehiculo_marca.id IS NULL THEN '' ELSE log_vehiculo_marca.titulo END) AS vehiculo_marca,"
            + "(CASE WHEN agen_a.id IS NULL THEN 0 ELSE agen_a.id END) AS agena_id,"
            + "(CASE WHEN agen_a.id IS NULL THEN '' ELSE agen_a.folio END) AS agena_no,"
            + "(CASE WHEN agen_a.id IS NULL THEN '' ELSE agen_a.razon_social END) AS agena_nombre,"
            + "(CASE WHEN rem.id IS NULL THEN 0 ELSE rem.id END) AS rem_id,"
            + "(CASE WHEN rem.id IS NULL THEN '' ELSE rem.folio END) AS rem_no,"
            + "(CASE WHEN rem.id IS NULL THEN '' ELSE rem.razon_social END) AS rem_nombre,"
            + "(CASE WHEN rem.id IS NULL THEN '' ELSE rem.calle||' '||rem.no_int||' '||rem.no_ext||', '||rem.colonia||', '||rem.municipio||', '||rem.estado||', '||rem.pais||' C.P. '||rem.cp END) AS rem_dir,"
            + "ped_trans.rem_dir_alterna,"
            + "(CASE WHEN dest.id IS NULL THEN 0 ELSE dest.id END) AS dest_id,"
            + "(CASE WHEN dest.id IS NULL THEN '' ELSE dest.folio END) AS dest_no,"
            + "(CASE WHEN dest.id IS NULL THEN '' ELSE dest.razon_social END) AS dest_nombre,"
            + "(CASE WHEN dest.id IS NULL THEN '' ELSE dest.calle||' '||dest.no_int||' '||dest.no_ext||', '||dest.colonia||', '||dest.municipio||', '||dest.estado||', '||dest.pais||' C.P. '||dest.cp END) AS dest_dir,"
            + "ped_trans.dest_dir_alterna "
        + "FROM poc_ped_trans AS ped_trans "
        + "LEFT JOIN (SELECT cxc_remitentes.id, cxc_remitentes.folio,cxc_remitentes.razon_social,(CASE WHEN cxc_remitentes.calle IS NULL THEN '' ELSE cxc_remitentes.calle END) AS calle, (CASE WHEN cxc_remitentes.no_int IS NULL THEN '' ELSE (CASE WHEN cxc_remitentes.no_int IS NULL OR cxc_remitentes.no_int='' THEN '' ELSE 'NO.INT.'||cxc_remitentes.no_int END)  END) AS no_int, (CASE WHEN cxc_remitentes.no_ext IS NULL THEN '' ELSE (CASE WHEN cxc_remitentes.no_ext IS NULL OR cxc_remitentes.no_ext='' THEN '' ELSE 'NO.EXT.'||cxc_remitentes.no_ext END)  END) AS no_ext, (CASE WHEN cxc_remitentes.colonia IS NULL THEN '' ELSE cxc_remitentes.colonia END) AS colonia,(CASE WHEN gral_mun.id IS NULL OR gral_mun.id=0 THEN '' ELSE gral_mun.titulo END) AS municipio,(CASE WHEN gral_edo.id IS NULL OR gral_edo.id=0 THEN '' ELSE gral_edo.titulo END) AS estado,(CASE WHEN gral_pais.id IS NULL OR gral_pais.id=0 THEN '' ELSE gral_pais.titulo END) AS pais,(CASE WHEN cxc_remitentes.cp IS NULL THEN '' ELSE cxc_remitentes.cp END) AS cp FROM cxc_remitentes LEFT JOIN gral_pais ON gral_pais.id = cxc_remitentes.gral_pais_id LEFT JOIN gral_edo ON gral_edo.id = cxc_remitentes.gral_edo_id LEFT JOIN gral_mun ON gral_mun.id = cxc_remitentes.gral_mun_id) AS rem ON rem.id=ped_trans.cxc_remitente_id  "
        + "LEFT JOIN (SELECT cxc_destinatarios.id, cxc_destinatarios.folio,cxc_destinatarios.razon_social,(CASE WHEN cxc_destinatarios.calle IS NULL THEN '' ELSE cxc_destinatarios.calle END) AS calle, (CASE WHEN cxc_destinatarios.no_int IS NULL THEN '' ELSE (CASE WHEN cxc_destinatarios.no_int IS NULL OR cxc_destinatarios.no_int='' THEN '' ELSE 'NO.INT.'||cxc_destinatarios.no_int END)  END) AS no_int, (CASE WHEN cxc_destinatarios.no_ext IS NULL THEN '' ELSE (CASE WHEN cxc_destinatarios.no_ext IS NULL OR cxc_destinatarios.no_ext='' THEN '' ELSE 'NO.EXT.'||cxc_destinatarios.no_ext END)  END) AS no_ext, (CASE WHEN cxc_destinatarios.colonia IS NULL THEN '' ELSE cxc_destinatarios.colonia END) AS colonia,(CASE WHEN gral_mun.id IS NULL OR gral_mun.id=0 THEN '' ELSE gral_mun.titulo END) AS municipio,(CASE WHEN gral_edo.id IS NULL OR gral_edo.id=0 THEN '' ELSE gral_edo.titulo END) AS estado,(CASE WHEN gral_pais.id IS NULL OR gral_pais.id=0 THEN '' ELSE gral_pais.titulo END) AS pais,(CASE WHEN cxc_destinatarios.cp IS NULL THEN '' ELSE cxc_destinatarios.cp END) AS cp FROM cxc_destinatarios LEFT JOIN gral_pais ON gral_pais.id = cxc_destinatarios.gral_pais_id LEFT JOIN gral_edo ON gral_edo.id = cxc_destinatarios.gral_edo_id LEFT JOIN gral_mun ON gral_mun.id = cxc_destinatarios.gral_mun_id ) AS dest ON dest.id=ped_trans.cxc_destinatario_id "
        + "LEFT JOIN cxc_agentes_aduanales AS agen_a ON agen_a.id=ped_trans.cxc_agente_aduanal_id "
        + "LEFT JOIN log_vehiculos ON log_vehiculos.id=ped_trans.log_vehiculo_id "
        + "LEFT JOIN log_vehiculo_marca ON log_vehiculo_marca.id=log_vehiculos.log_vehiculo_marca_id "
        + "WHERE ped_trans.poc_pedido_id=?;";
        
        ArrayList<HashMap<String, String>> hm = (ArrayList<HashMap<String, String>>) this.jdbcTemplate.query(
            sql_query,
            new Object[]{new Integer(id_pedido)}, new RowMapper() {
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    HashMap<String, String> row = new HashMap<String, String>();
                    row.put("documentador",rs.getString("documentador"));
                    row.put("valor_declarado",rs.getString("valor_declarado"));
                    row.put("tipo_viaje",String.valueOf(rs.getInt("tipo_viaje")));
                    row.put("remolque1",rs.getString("remolque1"));
                    row.put("remolque2",rs.getString("remolque2"));
                    row.put("no_operador",rs.getString("no_operador"));
                    row.put("nombre_operador",rs.getString("nombre_operador"));
                    row.put("mun_id_orig",String.valueOf(rs.getInt("mun_id_orig")));
                    row.put("edo_id_orig",String.valueOf(rs.getInt("edo_id_orig")));
                    row.put("pais_id_orig",String.valueOf(rs.getInt("pais_id_orig")));
                    row.put("mun_id_dest",String.valueOf(rs.getInt("mun_id_dest")));
                    row.put("edo_id_dest",String.valueOf(rs.getInt("edo_id_dest")));
                    row.put("pais_id_dest",String.valueOf(rs.getInt("pais_id_dest")));
                    row.put("trans_observaciones",rs.getString("trans_observaciones"));
                    row.put("vehiculo_id",String.valueOf(rs.getInt("vehiculo_id")));
                    row.put("vehiculo_no",rs.getString("vehiculo_no"));
                    row.put("vehiculo_marca",rs.getString("vehiculo_marca"));
                    row.put("agena_id",String.valueOf(rs.getInt("agena_id")));
                    row.put("agena_no",rs.getString("agena_no"));
                    row.put("agena_nombre",rs.getString("agena_nombre"));
                    row.put("rem_id",String.valueOf(rs.getInt("rem_id")));
                    row.put("rem_no",rs.getString("rem_no"));
                    row.put("rem_nombre",rs.getString("rem_nombre"));
                    row.put("rem_dir",rs.getString("rem_dir"));
                    row.put("rem_dir_alterna",rs.getString("rem_dir_alterna"));
                    row.put("dest_id",String.valueOf(rs.getInt("dest_id")));
                    row.put("dest_no",rs.getString("dest_no"));
                    row.put("dest_nombre",rs.getString("dest_nombre"));
                    row.put("dest_dir",rs.getString("dest_dir"));
                    row.put("dest_dir_alterna",rs.getString("dest_dir_alterna"));
                    return row;
                }
            }
        );
        return hm;
    }
   
   
   
   
   
    @Override
    public ArrayList<HashMap<String, String>> getPaises() {
        String sql_to_query = "SELECT DISTINCT id as cve_pais, titulo as pais_ent FROM gral_pais;";
        
        ArrayList<HashMap<String, String>> pais = (ArrayList<HashMap<String, String>>) this.jdbcTemplate.query(
            sql_to_query,
            new Object[]{}, new RowMapper(){
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    HashMap<String, String> row = new HashMap<String, String>();
                    row.put("cve_pais",rs.getString("cve_pais"));
                    row.put("pais_ent",rs.getString("pais_ent"));
                    return row;
                }
            }
        );
        return pais;
    }
    
    @Override
    public ArrayList<HashMap<String, String>> getEntidadesForThisPais(String id_pais) {
        String sql_to_query = "SELECT id as cve_ent, titulo as nom_ent FROM gral_edo WHERE pais_id="+id_pais+" order by nom_ent;";
        //log.log(Level.INFO, "Ejecutando query de {0}", sql_to_query);
        ArrayList<HashMap<String, String>> hm = (ArrayList<HashMap<String, String>>) this.jdbcTemplate.query(
            sql_to_query,
            new Object[]{}, new RowMapper(){
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    HashMap<String, String> row = new HashMap<String, String>();
                    row.put("cve_ent",rs.getString("cve_ent"));
                    row.put("nom_ent",rs.getString("nom_ent"));
                    return row;
                }
            }
        );
        return hm;
    }



    @Override
    public ArrayList<HashMap<String, String>> getLocalidadesForThisEntidad(String id_pais, String id_entidad) {
        String sql_to_query = "SELECT id as cve_mun, titulo as nom_mun FROM gral_mun WHERE estado_id="+id_entidad+" and pais_id="+id_pais+" order by nom_mun;";

        //System.out.println("Ejecutando query loc_for_this_entidad: "+sql_to_query);

        ArrayList<HashMap<String, String>> hm = (ArrayList<HashMap<String, String>>) this.jdbcTemplate.query(
            sql_to_query,
            new Object[]{}, new RowMapper(){
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    HashMap<String, Object> row = new HashMap<String, Object>();
                    row.put("cve_mun",rs.getString("cve_mun"));
                    row.put("nom_mun",rs.getString("nom_mun"));
                    return row;
                }
            }
        );
        return hm;
    }
   
   
   
   
   
   


    @Override
    public ArrayList<HashMap<String, String>> getPocPedido_Almacenes(Integer id_sucursal) {
        String sql_to_query = "SELECT inv_alm.id, inv_alm.titulo FROM fac_par JOIN inv_alm ON inv_alm.id=fac_par.inv_alm_id WHERE gral_suc_id="+id_sucursal+";";
        //log.log(Level.INFO, "Ejecutando query de {0}", sql_to_query);
        ArrayList<HashMap<String, String>> hm_monedas = (ArrayList<HashMap<String, String>>) this.jdbcTemplate.query(
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
        return hm_monedas;
    }



    @Override
    public HashMap<String, String> getPocPedido_Parametros(Integer id_emp, Integer id_suc) {
        HashMap<String, String> mapDatos = new HashMap<String, String>();
        String sql_query = "SELECT * FROM fac_par WHERE gral_emp_id="+id_emp+" AND gral_suc_id="+id_suc+";";
        
        Map<String, Object> map = this.getJdbcTemplate().queryForMap(sql_query);

        mapDatos.put("gral_suc_id", String.valueOf(map.get("gral_suc_id")));
        mapDatos.put("gral_suc_id_consecutivo", String.valueOf(map.get("gral_suc_id_consecutivo")));
        mapDatos.put("cxc_mov_tipo_id", String.valueOf(map.get("cxc_mov_tipo_id")));
        mapDatos.put("inv_alm_id", String.valueOf(map.get("inv_alm_id")));
        mapDatos.put("gral_emp_id", String.valueOf(map.get("gral_emp_id")));
        mapDatos.put("formato_pedido", String.valueOf(map.get("formato_pedido")));
        mapDatos.put("permitir_pedido", String.valueOf(map.get("permitir_pedido")));
        mapDatos.put("permitir_remision", String.valueOf(map.get("permitir_remision")));
        mapDatos.put("permitir_cambio_almacen", String.valueOf(map.get("permitir_cambio_almacen")));
        mapDatos.put("permitir_servicios", String.valueOf(map.get("permitir_servicios")));
        mapDatos.put("permitir_articulos", String.valueOf(map.get("permitir_articulos")));
        mapDatos.put("permitir_kits", String.valueOf(map.get("permitir_kits")));
        mapDatos.put("cambiar_unidad_medida", String.valueOf(map.get("cambiar_unidad_medida")));
        mapDatos.put("permitir_descto", String.valueOf(map.get("permitir_descto")));
        mapDatos.put("permitir_req", String.valueOf(map.get("permitir_req_com")));
        return mapDatos;
    }





    //obtiene direcciones fiscales del cliente, esta dirección es la que se utilizará para facturar el pedido
    //si el cliente no tiene Direcciones Fisacles registradas en la tabla cxc_cliedf,
    //nunca ejecutará esta busqueda porque tomará por default la dirección que está en cxc_clie
    @Override
    public ArrayList<HashMap<String, String>> getPocPedido_DireccionesFiscalesCliente(Integer id_cliente) {
        String sql_to_query = ""
                + "SELECT  "
                    + "sbt1.tipo_dir, "
                    + "sbt1.id_cliente, "
                    + "sbt1.id_df, "
                    + "sbt1.calle||' '||sbt1.numero_interior||' '||sbt1.numero_exterior||', '||sbt1.colonia||', '||sbt1.municipio||', '||sbt1.estado||', '||sbt1.pais||', C.P.'||sbt1.cp AS direccion_fiscal "
                + "FROM ( "
                    + "SELECT "
                    + "sbt_dir.tipo_dir, "
                    + "sbt_dir.id_cliente, "
                    + "sbt_dir.id_df, "
                    + "(CASE WHEN sbt_dir.calle IS NULL THEN '' ELSE sbt_dir.calle END) AS calle, "
                    + "(CASE WHEN sbt_dir.numero_interior IS NULL THEN '' ELSE (CASE WHEN sbt_dir.numero_interior IS NULL OR sbt_dir.numero_interior='' THEN '' ELSE 'NO.INT.'||sbt_dir.numero_interior END)  END) AS numero_interior, "
                    + "(CASE WHEN sbt_dir.numero_exterior IS NULL THEN '' ELSE (CASE WHEN sbt_dir.numero_exterior IS NULL OR sbt_dir.numero_exterior='' THEN '' ELSE 'NO.EXT.'||sbt_dir.numero_exterior END )  END) AS numero_exterior, "
                    + "(CASE WHEN sbt_dir.colonia IS NULL THEN '' ELSE sbt_dir.colonia END) AS colonia, "
                    + "(CASE WHEN gral_mun.id IS NULL OR gral_mun.id=0 THEN '' ELSE gral_mun.titulo END) AS municipio, "
                    + "(CASE WHEN gral_edo.id IS NULL OR gral_edo.id=0 THEN '' ELSE gral_edo.titulo END) AS estado, "
                    + "(CASE WHEN gral_pais.id IS NULL OR gral_pais.id=0 THEN '' ELSE gral_pais.titulo END) AS pais, "
                    + "(CASE WHEN sbt_dir.cp IS NULL THEN '' ELSE sbt_dir.cp END) AS cp "
                    + "FROM ( "
                        + "SELECT  'DEFAULT'::character varying AS tipo_dir, cxc_clie.id AS id_cliente, 0::integer AS id_df, cxc_clie.calle, cxc_clie.numero AS numero_interior, cxc_clie.numero_exterior, cxc_clie.colonia, cxc_clie.cp AS cp, cxc_clie.pais_id, cxc_clie.estado_id, cxc_clie.municipio_id "
                        + "FROM cxc_clie  WHERE cxc_clie.id=? "

                        + "UNION "

                        + "SELECT 'DIRFISCAL'::character varying AS tipo_dir, cxc_clie_df.cxc_clie_id AS id_cliente, cxc_clie_df.id AS id_df,  cxc_clie_df.calle, cxc_clie_df.numero_interior, cxc_clie_df.numero_exterior, cxc_clie_df.colonia, cxc_clie_df.cp AS cp, cxc_clie_df.gral_pais_id AS pais_id, cxc_clie_df.gral_edo_id AS estado_id, cxc_clie_df.gral_mun_id AS municipio_id FROM cxc_clie_df  "
                        + "WHERE cxc_clie_df.borrado_logico=false AND cxc_clie_df.cxc_clie_id=? "
                    + ")AS sbt_dir "
                    + "LEFT JOIN gral_pais ON gral_pais.id = sbt_dir.pais_id  "
                    + "LEFT JOIN gral_edo ON gral_edo.id = sbt_dir.estado_id  "
                    + "LEFT JOIN gral_mun ON gral_mun.id = sbt_dir.municipio_id "
                + ") AS sbt1 "
                + "ORDER BY sbt1.tipo_dir;";

        //System.out.println("DireccionesFiscalesCliente: "+sql_to_query);

        ArrayList<HashMap<String, String>> dir = (ArrayList<HashMap<String, String>>) this.jdbcTemplate.query(
            sql_to_query,
            new Object[]{new Integer(id_cliente), new Integer(id_cliente)}, new RowMapper(){
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    HashMap<String, String> row = new HashMap<String, String>();
                    row.put("tipo_dir",rs.getString("tipo_dir"));
                    row.put("id_cliente",String.valueOf(rs.getInt("id_cliente")));
                    row.put("id_df",String.valueOf(rs.getInt("id_df")));
                    row.put("direccion_fiscal",rs.getString("direccion_fiscal"));
                    return row;
                }
            }
        );
        return dir;
    }



    @Override
    public ArrayList<HashMap<String, String>> getMonedas() {
        String sql_to_query = "SELECT id, descripcion, descripcion_abr FROM  gral_mon WHERE borrado_logico=FALSE AND ventas=TRUE ORDER BY id ASC;";
        //log.log(Level.INFO, "Ejecutando query de {0}", sql_to_query);
        ArrayList<HashMap<String, String>> hm_monedas = (ArrayList<HashMap<String, String>>) this.jdbcTemplate.query(
            sql_to_query,
            new Object[]{}, new RowMapper(){
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    HashMap<String, String> row = new HashMap<String, String>();
                    row.put("id",String.valueOf(rs.getInt("id")));
                    row.put("descripcion",rs.getString("descripcion"));
                    row.put("descripcion_abr",rs.getString("descripcion_abr"));
                    return row;
                }
            }
        );
        return hm_monedas;
    }
    
    
    
    //Obtener agentes de Ventas
    @Override
    public ArrayList<HashMap<String, String>> getAgentes(Integer id_empresa, Integer id_sucursal, boolean obtener_todos) {
        String sql_to_query = "";
        
        if(obtener_todos){
            //Obtener todos. Esta es para que los registros historicos de pedidos y cotizaciones muestre los nombres de los agentes que ya no estan vigentes.
            sql_to_query = "SELECT cxc_agen.id, cxc_agen.nombre AS nombre_agente FROM cxc_agen JOIN gral_usr_suc ON gral_usr_suc.gral_usr_id=cxc_agen.gral_usr_id JOIN gral_suc ON gral_suc.id=gral_usr_suc.gral_suc_id WHERE gral_suc.empresa_id=? ORDER BY cxc_agen.id;";
        }else{
            //Obtener solo los NO eliminados. Esta es para cuando se está creando un nuevo pedido o cotización, no debe mostrar los ya eliminados.
            sql_to_query = "SELECT cxc_agen.id, cxc_agen.nombre AS nombre_agente FROM cxc_agen JOIN gral_usr_suc ON gral_usr_suc.gral_usr_id=cxc_agen.gral_usr_id JOIN gral_suc ON gral_suc.id=gral_usr_suc.gral_suc_id WHERE cxc_agen.borrado_logico=false and gral_suc.empresa_id=? ORDER BY cxc_agen.id;";
        }
        //System.out.println("Obtener agentes:"+sql_to_query);

        ArrayList<HashMap<String, String>> hm_vendedor = (ArrayList<HashMap<String, String>>) this.jdbcTemplate.query(
            sql_to_query,
            new Object[]{new Integer(id_empresa)}, new RowMapper(){
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    HashMap<String, String> row = new HashMap<String, String>();
                    row.put("id",rs.getString("id")  );
                    row.put("nombre_agente",rs.getString("nombre_agente"));
                    return row;
                }
            }
        );
        return hm_vendedor;
    }


    @Override
    public ArrayList<HashMap<String, String>> getCondicionesDePago() {
        String sql_to_query = "SELECT id,descripcion FROM cxc_clie_credias WHERE borrado_logico=FALSE;";
        ArrayList<HashMap<String, String>> hm = (ArrayList<HashMap<String, String>>) this.jdbcTemplate.query(
            sql_to_query,
            new Object[]{}, new RowMapper(){
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    HashMap<String, String> row = new HashMap<String, String>();
                    row.put("id",String.valueOf(rs.getInt("id"))  );
                    row.put("descripcion",rs.getString("descripcion"));
                    return row;
                }
            }
        );
        return hm;
    }


 // Uso CFDI 
      @Override
    public ArrayList<HashMap<String, String>> getUsos() {
        String sql_to_query = "SELECT id,numero_control FROM cfdi_usos;";
        ArrayList<HashMap<String, String>> hm = (ArrayList<HashMap<String, String>>) this.jdbcTemplate.query(
            sql_to_query,
            new Object[]{}, new RowMapper(){
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    HashMap<String, String> row = new HashMap<String, String>();
                    row.put("id",String.valueOf(rs.getInt("id"))  );
                    row.put("numero_control",rs.getString("numero_control"));
             //       row.put("descripcion",rs.getString("descripcion"));
                    return row;
                }
            }
        );
        return hm;
    }
    
   // Metodo de Pago CFDI
    @Override
    public ArrayList<HashMap<String, String>> getMetodos() {
        String sql_to_query = "SELECT id,clave FROM cfdi_metodos_pago;";
        ArrayList<HashMap<String, String>> hm = (ArrayList<HashMap<String, String>>) this.jdbcTemplate.query(
            sql_to_query,
            new Object[]{}, new RowMapper(){
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    HashMap<String, String> row = new HashMap<String, String>();
                    row.put("id",String.valueOf(rs.getInt("id"))  );
                    row.put("clave",rs.getString("clave"));
                    return row;
                }
            }
        );
        return hm;
    }
    
    //obtiene el tipo de cambio actual
    @Override
    public Double getTipoCambioActual() {
        //System.out.println("FECHA ACTUAL: "+TimeHelper.getFechaActualYMD2());
        String sql_to_query = "SELECT valor AS tipo_cambio FROM erp_monedavers WHERE momento_creacion<=now() AND moneda_id=2 ORDER BY momento_creacion DESC LIMIT 1;";
        Map<String, Object> tipo_cambio = this.getJdbcTemplate().queryForMap(sql_to_query);
        Double valor_tipo_cambio = Double.parseDouble(StringHelper.roundDouble(tipo_cambio.get("tipo_cambio").toString(),4));

        return valor_tipo_cambio;
    }

    //obtiene el tipo de cambio actual por Id de la Moneda Seleccionada
    @Override
    public HashMap<String, String> getTipoCambioActualPorIdMoneda(Integer idMoneda) {
        HashMap<String, String> valorRetorno = new HashMap<String, String>();
        String valor="0.0000";

        String sql_busqueda = "select count(valor) FROM (SELECT valor FROM erp_monedavers WHERE momento_creacion<=now() AND moneda_id="+idMoneda+" ORDER BY momento_creacion DESC LIMIT 1) AS sbt;";
        int rowCount = this.getJdbcTemplate().queryForInt(sql_busqueda);

        //System.out.println(sql_busqueda);

        if(rowCount > 0){
            String sql_to_query = "SELECT valor FROM erp_monedavers WHERE momento_creacion<=now() AND moneda_id="+idMoneda+" ORDER BY momento_creacion DESC LIMIT 1;";
            Map<String, Object> tipo_cambio = this.getJdbcTemplate().queryForMap(sql_to_query);
            valor = StringHelper.roundDouble(tipo_cambio.get("valor").toString(),4);
        }
        
        valorRetorno.put("valor", valor);
        
        return valorRetorno;
    }
    
    
    //obtiene valor del impuesto de la sucursal
    @Override
    public ArrayList<HashMap<String, String>> getValoriva(Integer id_sucursal) {
        String sql_to_query = ""
        + "SELECT "
            + "(CASE WHEN impto_suc.id IS NULL THEN 0 ELSE impto_suc.id END) AS id_impuesto, "
            + "(CASE WHEN impto_suc.id IS NULL THEN 0 ELSE impto_suc.iva_1 END) AS valor_impuesto,"
            + "(CASE WHEN impto_emp.id IS NULL THEN 0 ELSE impto_emp.id END) AS id_impto_emp, "
            + "(CASE WHEN impto_emp.id IS NULL THEN 0 ELSE impto_emp.iva_1 END) AS valor_impto_emp "
        + "FROM gral_suc  "
        + "JOIN gral_emp ON gral_emp.id=gral_suc.empresa_id "
        + "JOIN gral_imptos AS impto_suc ON (impto_suc.id=gral_suc.gral_impto_id AND impto_suc.borrado_logico=FALSE) "
        + "JOIN gral_imptos AS impto_emp ON (impto_emp.id=gral_emp.gral_impto_id AND impto_emp.borrado_logico=FALSE) "
        + "WHERE gral_suc.id=?;";
        
        //log.log(Level.INFO, "Ejecutando query de {0}", sql_to_query);
        ArrayList<HashMap<String, String>> hm_valoriva = (ArrayList<HashMap<String, String>>) this.jdbcTemplate.query(
            sql_to_query,
            new Object[]{new Integer(id_sucursal)}, new RowMapper(){
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    HashMap<String, String> row = new HashMap<String, String>();
                    row.put("id_impuesto",String.valueOf(rs.getInt("id_impuesto")));
                    row.put("valor_impuesto",StringHelper.roundDouble(rs.getString("valor_impuesto"),2));
                    row.put("id_impto_emp",String.valueOf(rs.getInt("id_impto_emp")));
                    row.put("valor_impto_emp",StringHelper.roundDouble(rs.getString("valor_impto_emp"),2));
                    return row;
                }
            }
        );
        return hm_valoriva;
    }


    
    //obtiene valor del Impuesto en Especifico a partir del Id
    @Override
    public ArrayList<HashMap<String, String>> getValorivaById(Integer idImpto) {
        String sql_to_query = ""
                + "SELECT "
                    + "(CASE WHEN gral_imptos.id IS NULL THEN 0 ELSE gral_imptos.id END) AS id_impuesto, "
                    + "(CASE WHEN gral_imptos.id IS NULL THEN 0 ELSE gral_imptos.iva_1 END) AS valor_impuesto "
                + "FROM gral_imptos "
                + "WHERE gral_imptos.id=? AND gral_imptos.borrado_logico=FALSE;";
        
        //log.log(Level.INFO, "Ejecutando query de {0}", sql_to_query);
        ArrayList<HashMap<String, String>> hm_valoriva = (ArrayList<HashMap<String, String>>) this.jdbcTemplate.query(
            sql_to_query,
            new Object[]{new Integer(idImpto)}, new RowMapper(){
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    HashMap<String, String> row = new HashMap<String, String>();
                    row.put("id_impuesto",String.valueOf(rs.getInt("id_impuesto")));
                    row.put("valor_impuesto",StringHelper.roundDouble(rs.getString("valor_impuesto"),2));
                    return row;
                }
            }
        );
        return hm_valoriva;
    }
    
    
    

    //buscador de clientes
    @Override
    public ArrayList<HashMap<String, String>> getBuscadorClientes(String cadena, Integer filtro, Integer id_empresa, Integer id_sucursal, String permite_descto) {
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
            +"sbt.id, "
            +"sbt.numero_control, "
            +"sbt.rfc, "
            +"sbt.razon_social, "
            +"sbt.direccion, "
            +"sbt.moneda_id, "
            +"gral_mon.descripcion as moneda, "
            +"sbt.cxc_agen_id, "
            +"sbt.terminos_id, "
            +"sbt.empresa_immex, "
            +"sbt.tasa_ret_immex, "
            +"sbt.cta_pago_mn, "
            +"sbt.cta_pago_usd, "
            +"sbt.lista_precio, "
            +"sbt.metodo_pago_id, "
            +"tiene_dir_fiscal,"
            +"sbt.contacto,"
            +"sbt.credito_suspendido, "
            +"sbt.pdescto, "
            +"sbt.vdescto "
        +"FROM("
            + "SELECT cxc_clie.id, "
                +"cxc_clie.numero_control, "
                +"cxc_clie.rfc, "
                +"cxc_clie.razon_social,"
                +"cxc_clie.calle||' '||cxc_clie.numero||', '||cxc_clie.colonia||', '||gral_mun.titulo||', '||gral_edo.titulo||', '||gral_pais.titulo||' C.P. '||cxc_clie.cp as direccion, "
                +"cxc_clie.moneda as moneda_id, "
                +"cxc_clie.cxc_agen_id, "
                +"cxc_clie.contacto, "
                +"cxc_clie.dias_credito_id AS terminos_id, "
                +"cxc_clie.empresa_immex, "
                +"(CASE WHEN cxc_clie.tasa_ret_immex IS NULL THEN 0 ELSE cxc_clie.tasa_ret_immex/100 END) AS tasa_ret_immex, "
                + "cxc_clie.cta_pago_mn,"
                + "cxc_clie.cta_pago_usd,  "
                + "cxc_clie.lista_precio, "
                + "cxc_clie.fac_metodos_pago_id AS metodo_pago_id, "
                + "(CASE WHEN tbldf.cxc_clie_id IS NULL THEN false ELSE true END ) AS tiene_dir_fiscal,"
                + "cxc_clie.credito_suspendido, "
                + ""+permite_descto.toLowerCase()+"::character varying AS pdescto, "
                + "(CASE WHEN lower("+permite_descto+"::character varying)='true' THEN (CASE WHEN cxc_clie_descto.id IS NULL THEN 0 ELSE cxc_clie_descto.valor END) ELSE 0 END) AS vdescto "
            +"FROM cxc_clie "
            + "LEFT JOIN (SELECT DISTINCT cxc_clie_id FROM cxc_clie_df WHERE borrado_logico=false) AS tbldf ON tbldf.cxc_clie_id=cxc_clie.id "
            + "JOIN gral_pais ON gral_pais.id = cxc_clie.pais_id "
            + "JOIN gral_edo ON gral_edo.id = cxc_clie.estado_id "
            + "JOIN gral_mun ON gral_mun.id = cxc_clie.municipio_id "
            + "LEFT JOIN cxc_clie_descto ON cxc_clie_descto.cxc_clie_id=cxc_clie.id "

            //+" WHERE empresa_id ="+id_empresa+"  AND sucursal_id="+id_sucursal
            +" WHERE empresa_id ="+id_empresa+" "
            + " AND cxc_clie.borrado_logico=false  "+where+" "
        +") AS sbt "
        +"LEFT JOIN gral_mon on gral_mon.id = sbt.moneda_id ";

        //System.out.println("BuscadorClientes: "+sql_query);

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
                    row.put("lista_precio",rs.getString("lista_precio"));
                    row.put("metodo_pago_id",String.valueOf(rs.getInt("metodo_pago_id")));
                    row.put("tiene_dir_fiscal",String.valueOf(rs.getBoolean("tiene_dir_fiscal")));
                    row.put("contacto",rs.getString("contacto"));
                    row.put("credito_suspendido",String.valueOf(rs.getBoolean("credito_suspendido")));
                    row.put("pdescto",rs.getString("pdescto"));
                    row.put("vdescto",StringHelper.roundDouble(String.valueOf(rs.getDouble("vdescto")),4));
                    
                    return row;
                }
            }
        );
        return hm_cli;
    }



    //buscador de clientes
    @Override
    public ArrayList<HashMap<String, String>> getDatosClienteByNoCliente(String no_control,  Integer id_empresa, Integer id_sucursal, String permite_descto) {
	String sql_query = ""
                + "SELECT "
                    +"sbt.id, "
                    +"sbt.numero_control, "
                    +"sbt.rfc, "
                    +"sbt.razon_social, "
                    +"sbt.direccion, "
                    +"sbt.moneda_id, "
                    +"gral_mon.descripcion as moneda, "
                    +"sbt.cxc_agen_id, "
                    +"sbt.terminos_id, "
                    +"sbt.empresa_immex, "
                    +"sbt.tasa_ret_immex, "
                    +"sbt.cta_pago_mn, "
                    +"sbt.cta_pago_usd, "
                    +"sbt.lista_precio, "
                    +"sbt.metodo_pago_id, "
                    +"tiene_dir_fiscal,"
                    +"sbt.contacto,"
                    +"sbt.credito_suspendido, "
                    +"sbt.pdescto, "
                    +"sbt.vdescto "
                +"FROM("
                    + "SELECT cxc_clie.id, "
                        +"cxc_clie.numero_control, "
                        +"cxc_clie.rfc, "
                        +"cxc_clie.razon_social,"
                        +"cxc_clie.calle||' '||cxc_clie.numero||', '||cxc_clie.colonia||', '||gral_mun.titulo||', '||gral_edo.titulo||', '||gral_pais.titulo||' C.P. '||cxc_clie.cp as direccion, "
                        +"cxc_clie.moneda as moneda_id, "
                        +"cxc_clie.cxc_agen_id, "
                        +"cxc_clie.contacto, "
                        +"cxc_clie.dias_credito_id AS terminos_id, "
                        +"cxc_clie.empresa_immex, "
                        +"(CASE WHEN cxc_clie.tasa_ret_immex IS NULL THEN 0 ELSE cxc_clie.tasa_ret_immex/100 END) AS tasa_ret_immex, "
                        + "cxc_clie.cta_pago_mn,"
                        + "cxc_clie.cta_pago_usd,  "
                        + "cxc_clie.lista_precio, "
                        + "cxc_clie.fac_metodos_pago_id AS metodo_pago_id, "
                        + "(CASE WHEN tbldf.cxc_clie_id IS NULL THEN false ELSE true END ) AS tiene_dir_fiscal,"
                        + "cxc_clie.credito_suspendido,"
                        + ""+permite_descto.toLowerCase()+"::character varying AS pdescto, "
                        + "(CASE WHEN lower("+permite_descto+"::character varying)='true' THEN (CASE WHEN cxc_clie_descto.id IS NULL THEN 0 ELSE cxc_clie_descto.valor END) ELSE 0 END) AS vdescto "
                    +"FROM cxc_clie "
                    + "LEFT JOIN (SELECT DISTINCT cxc_clie_id FROM cxc_clie_df WHERE borrado_logico=false) AS tbldf ON tbldf.cxc_clie_id=cxc_clie.id "
                    + "JOIN gral_pais ON gral_pais.id = cxc_clie.pais_id "
                    + "JOIN gral_edo ON gral_edo.id = cxc_clie.estado_id "
                    + "JOIN gral_mun ON gral_mun.id = cxc_clie.municipio_id "
                    + "LEFT JOIN cxc_clie_descto ON cxc_clie_descto.cxc_clie_id=cxc_clie.id "

                    //+" WHERE empresa_id ="+id_empresa+"  AND sucursal_id="+id_sucursal
                    +" WHERE empresa_id ="+id_empresa+" "
                    + " AND cxc_clie.borrado_logico=false "
                    + " AND cxc_clie.numero_control='"+no_control.toUpperCase().trim()+"'"
                +") AS sbt "
                +"LEFT JOIN gral_mon on gral_mon.id = sbt.moneda_id LIMIT 1;";

        //System.out.println("getDatosClienteByNoCliente: "+sql_query);

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
                    row.put("lista_precio",rs.getString("lista_precio"));
                    row.put("metodo_pago_id",String.valueOf(rs.getInt("metodo_pago_id")));
                    row.put("tiene_dir_fiscal",String.valueOf(rs.getBoolean("tiene_dir_fiscal")));
                    row.put("contacto",rs.getString("contacto"));
                    row.put("credito_suspendido",String.valueOf(rs.getBoolean("credito_suspendido")));
                    row.put("pdescto",rs.getString("pdescto"));
                    row.put("vdescto",StringHelper.roundDouble(String.valueOf(rs.getDouble("vdescto")),4));
                    return row;
                }
            }
        );
        return hm_cli;
    }
    
 
    
    
    //Buscador de Prospectos
    //Se utiliza en cotizaciones, cuando el proyecto incluye modulo de CRM.
    //Varios de los campos se les asigna un valor por default, solo es para que sea igual que el de clientes
    //porque se utiliza en la misma ventana de busqueda
    @Override
    public ArrayList<HashMap<String, String>> getBuscadorProspectos(String cadena, Integer filtro, Integer id_empresa, Integer id_sucursal) {
        String where="";
	if(filtro == 1){
            where=" AND crm_prospectos.numero_control ilike '%"+cadena.toUpperCase()+"%'";
	}
	if(filtro == 2){
            where=" AND crm_prospectos.rfc ilike '%"+cadena.toUpperCase()+"%'";
	}
	if(filtro == 3){
            where=" AND crm_prospectos.razon_social ilike '%"+cadena.toUpperCase()+"%'";
	}
        
	String sql_query = ""
                + "SELECT "
                    + "crm_prospectos.id, "
                    + "crm_prospectos.numero_control, "
                    + "(CASE WHEN crm_prospectos.rfc='' OR crm_prospectos.rfc IS NULL THEN 'XXX000000000' ELSE crm_prospectos.rfc END) AS rfc, "
                    + "crm_prospectos.razon_social,"
                    + "crm_prospectos.calle||' '||crm_prospectos.numero||', '||crm_prospectos.colonia||', '||gral_mun.titulo||', '||gral_edo.titulo||', '||gral_pais.titulo||' C.P. '||crm_prospectos.cp as direccion, "
                    + "0::integer AS moneda_id, "
                    + "''::character varying AS moneda,"
                    + "0::integer AS cxc_agen_id, "
                    + "0::integer AS terminos_id, "
                    + "false AS empresa_immex, "
                    + "0::double precision AS tasa_ret_immex, "
                    + "''::character varying cta_pago_mn,"
                    + "''::character varying cta_pago_usd,  "
                    + "0::integer AS lista_precio, "
                    + "0::integer AS metodo_pago_id, "
                    + "false AS tiene_dir_fiscal,"
                    + "crm_prospectos.contacto "
                + "FROM crm_prospectos "
                + "LEFT JOIN gral_pais ON gral_pais.id=crm_prospectos.pais_id "
                + "LEFT JOIN gral_edo ON gral_edo.id=crm_prospectos.estado_id "
                + "LEFT JOIN gral_mun ON gral_mun.id=crm_prospectos.municipio_id  "
                + "WHERE crm_prospectos.gral_emp_id="+id_empresa+" "
                + "AND crm_prospectos.borrado_logico=false  "+where+";";

        //System.out.println("BuscadorProspectos: "+sql_query);

        ArrayList<HashMap<String, String>> hm = (ArrayList<HashMap<String, String>>) this.jdbcTemplate.query(
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
                    row.put("lista_precio",rs.getString("lista_precio"));
                    row.put("metodo_pago_id",String.valueOf(rs.getInt("metodo_pago_id")));
                    row.put("tiene_dir_fiscal",String.valueOf(rs.getBoolean("tiene_dir_fiscal")));
                    row.put("contacto",rs.getString("contacto"));

                    return row;
                }
            }
        );
        return hm;
    }




    @Override
    public ArrayList<HashMap<String, String>> getDatosProspectoByNoControl(String no_control, Integer id_empresa, Integer id_sucursal) {
	String sql_query = ""
                + "SELECT "
                    + "crm_prospectos.id, "
                    + "crm_prospectos.numero_control, "
                    + "crm_prospectos.rfc, "
                    + "crm_prospectos.razon_social,"
                    + "crm_prospectos.calle||' '||crm_prospectos.numero||', '||crm_prospectos.colonia||', '||gral_mun.titulo||', '||gral_edo.titulo||', '||gral_pais.titulo||' C.P. '||crm_prospectos.cp as direccion, "
                    + "0::integer AS moneda_id, "
                    + "''::character varying AS moneda,"
                    + "0::integer AS cxc_agen_id, "
                    + "0::integer AS terminos_id, "
                    + "false AS empresa_immex, "
                    + "0::double precision AS tasa_ret_immex, "
                    + "''::character varying cta_pago_mn,"
                    + "''::character varying cta_pago_usd,  "
                    + "0::integer AS lista_precio, "
                    + "0::integer AS metodo_pago_id, "
                    + "false AS tiene_dir_fiscal,"
                    + "crm_prospectos.contacto "
                + "FROM crm_prospectos "
                + "JOIN gral_pais ON gral_pais.id=crm_prospectos.pais_id "
                + "JOIN gral_edo ON gral_edo.id=crm_prospectos.estado_id "
                + "JOIN gral_mun ON gral_mun.id=crm_prospectos.municipio_id  "
                + "WHERE crm_prospectos.gral_emp_id="+id_empresa+" "
                + "AND crm_prospectos.borrado_logico=false AND crm_prospectos.numero_control='"+no_control.toUpperCase().trim()+"';";

        //System.out.println("getDatosProspecto: "+sql_query);

        ArrayList<HashMap<String, String>> hm = (ArrayList<HashMap<String, String>>) this.jdbcTemplate.query(
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
                    row.put("lista_precio",rs.getString("lista_precio"));
                    row.put("metodo_pago_id",String.valueOf(rs.getInt("metodo_pago_id")));
                    row.put("tiene_dir_fiscal",String.valueOf(rs.getBoolean("tiene_dir_fiscal")));
                    row.put("contacto",rs.getString("contacto"));

                    return row;
                }
            }
        );
        return hm;
    }




    //buscador de tipos de producto
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
                            +"inv_prod.id, "
                            +"inv_prod.sku, "
                            +"inv_prod.descripcion, "
                            + "inv_prod.unidad_id, "
                            + "inv_prod_unidades.titulo AS unidad, "
                            +"inv_prod_tipos.titulo AS tipo, "
                            + "inv_prod_unidades.decimales "
		+"FROM inv_prod "
                + "LEFT JOIN inv_prod_tipos ON inv_prod_tipos.id=inv_prod.tipo_de_producto_id "
                + "LEFT JOIN inv_prod_unidades ON inv_prod_unidades.id=inv_prod.unidad_id "
                + "WHERE inv_prod.empresa_id="+id_empresa+" AND inv_prod.borrado_logico=false "+where+" ORDER BY inv_prod.descripcion;";
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
                    //row.put("precio",rs.getString("precio"));
                    return row;
                }
            }
        );
        return hm_datos_productos;
    }
    
    
    
    @Override
    public ArrayList<HashMap<String, String>> getPresentacionesProducto(String sku,String lista_precio, Integer id_empresa) {
        String sql_query = "";
        String precio = "";
        
        if(lista_precio.equals("0")){
            precio=" 0::double precision AS precio,"
                    + "0::integer  AS id_moneda,"
                    + "0::double precision AS tc,"
                    + "'1'::character varying  AS exis_prod_lp ";
        }else{
            precio=" (CASE WHEN inv_pre.precio_"+lista_precio+" IS NULL THEN 0 ELSE inv_pre.precio_"+lista_precio+" END ) AS precio,"
                    + "(CASE WHEN inv_pre.gral_mon_id_pre"+lista_precio+" IS NULL THEN 0 ELSE inv_pre.gral_mon_id_pre"+lista_precio+" END ) AS id_moneda,"
                    + "(CASE WHEN inv_pre.gral_mon_id_pre1 IS NULL THEN 0 ELSE (SELECT valor FROM erp_monedavers WHERE momento_creacion<=now() AND moneda_id=inv_pre.gral_mon_id_pre"+lista_precio+" ORDER BY momento_creacion DESC LIMIT 1) END ) AS tc, "
                    + " (CASE WHEN inv_pre.precio_"+lista_precio+" IS NULL THEN 'El producto con &eacute;sta presentaci&oacute;n no se encuentra en el cat&aacute;logo de Listas de Precios.\nEs necesario asignarle un precio.' ELSE '1' END ) AS exis_prod_lp ";
        }
        
        sql_query = ""
        + "SELECT "
                +"inv_prod.id,"
                +"inv_prod.sku,"
                +"inv_prod.descripcion AS titulo,"
                +"inv_prod.gral_impto_id AS id_impto_prod,"
                +"inv_prod.unidad_id,"                
                +"(CASE WHEN inv_prod.gral_impto_id=0 THEN 0 ELSE gral_imptos.iva_1 END) AS valor_impto_prod, "
                +"(CASE WHEN inv_prod.ieps=0 THEN 0 ELSE gral_ieps.id END) AS ieps_id, "
                +"(CASE WHEN inv_prod.ieps=0 THEN 0 ELSE gral_ieps.tasa END) AS ieps_tasa, "
                +"(CASE WHEN inv_prod.gral_imptos_ret_id=0 THEN 0 ELSE gral_imptos_ret.id END) AS ret_id, "
                +"(CASE WHEN inv_prod.gral_imptos_ret_id=0 THEN 0 ELSE gral_imptos_ret.tasa END) AS ret_tasa, "
                +"(CASE WHEN inv_prod.descripcion_larga IS NULL THEN '' ELSE inv_prod.descripcion_larga END) AS descripcion_larga,"
                +"(CASE WHEN inv_prod.archivo_img='' THEN '' ELSE inv_prod.archivo_img END) AS archivo_img,"
                +"(CASE WHEN inv_prod_unidades.titulo IS NULL THEN '' ELSE inv_prod_unidades.titulo END) AS unidad,"
                +"(CASE WHEN inv_prod_presentaciones.id IS NULL THEN 0 ELSE inv_prod_presentaciones.id END) AS id_presentacion,"
                +"(CASE WHEN inv_prod_presentaciones.titulo IS NULL THEN '' ELSE inv_prod_presentaciones.titulo END) AS presentacion, "
                +"(CASE WHEN inv_prod_unidades.decimales IS NULL THEN 0 ELSE inv_prod_unidades.decimales END) AS  decimales, "
                +precio+" "
        +"FROM inv_prod "
        +"LEFT JOIN inv_prod_unidades on inv_prod_unidades.id = inv_prod.unidad_id "
        +"LEFT JOIN inv_prod_pres_x_prod on inv_prod_pres_x_prod.producto_id = inv_prod.id "
        +"LEFT JOIN inv_prod_presentaciones on inv_prod_presentaciones.id = inv_prod_pres_x_prod.presentacion_id "
        +"LEFT JOIN gral_imptos ON gral_imptos.id=inv_prod.gral_impto_id "
        +"LEFT JOIN gral_ieps ON gral_ieps.id=inv_prod.ieps "
        +"LEFT JOIN gral_imptos_ret ON gral_imptos_ret.id=inv_prod.gral_imptos_ret_id "
        +"LEFT JOIN inv_pre ON (inv_pre.inv_prod_id=inv_prod.id AND inv_pre.inv_prod_presentacion_id=inv_prod_pres_x_prod.presentacion_id AND inv_pre.borrado_logico=false) "
        +"WHERE  inv_prod.empresa_id = "+id_empresa+" AND inv_prod.sku ILIKE '"+sku+"' AND inv_prod.borrado_logico=false;";
        
        //System.out.println("getPresentacionesProducto: "+sql_query);
        
        ArrayList<HashMap<String, String>> hm = (ArrayList<HashMap<String, String>>) this.jdbcTemplate.query(
            sql_query,
            new Object[]{}, new RowMapper() {
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    HashMap<String, String> row = new HashMap<String, String>();
                    row.put("id",String.valueOf(rs.getInt("id")));
                    row.put("id_impto_prod",String.valueOf(rs.getInt("id_impto_prod")));
                    row.put("valor_impto_prod",StringHelper.roundDouble(rs.getString("valor_impto_prod"),2));
                    row.put("ieps_id",String.valueOf(rs.getInt("ieps_id")));
                    row.put("ieps_tasa",StringHelper.roundDouble(rs.getString("ieps_tasa"),2));
                    row.put("ret_id",String.valueOf(rs.getInt("ret_id")));
                    row.put("ret_tasa",StringHelper.roundDouble(rs.getString("ret_tasa"),2));
                    row.put("sku",rs.getString("sku"));
                    row.put("titulo",rs.getString("titulo"));
                    row.put("descripcion_larga",rs.getString("descripcion_larga"));
                    row.put("archivo_img",rs.getString("archivo_img"));
                    row.put("unidad_id",String.valueOf(rs.getInt("unidad_id")));
                    row.put("unidad",rs.getString("unidad"));
                    row.put("id_presentacion",String.valueOf(rs.getInt("id_presentacion")));
                    row.put("presentacion",rs.getString("presentacion"));
                    row.put("decimales",rs.getString("decimales"));
                    row.put("precio",StringHelper.roundDouble(rs.getString("precio"),4));
                    row.put("exis_prod_lp",rs.getString("exis_prod_lp"));
                    row.put("id_moneda",String.valueOf(rs.getInt("id_moneda")));
                    row.put("tc",StringHelper.roundDouble(rs.getString("tc"),4));
                    return row;
                }
            }
        );
        
        return hm;
    }
    
    
    //Buscador de Unidades(Vehiculos)
    @Override
    public ArrayList<HashMap<String, String>> getBuscadorUnidades(String no_eco, String marca, Integer id_empresa, Integer id_sucursal) {
        String where="";
        if(id_sucursal!=0){
            where = "AND gral_suc_id="+id_sucursal;
        }
        
	String sql_query = "SELECT id,numero_economico,marca FROM log_vehiculos WHERE numero_economico ILIKE '%"+no_eco+"%' AND marca ILIKE '%"+marca+"%' AND gral_emp_id="+id_empresa+" AND borrado_logico=false "+where+";";
        
        //System.out.println("getBuscadorUnidades: "+sql_query);
        ArrayList<HashMap<String, String>> hm = (ArrayList<HashMap<String, String>>) this.jdbcTemplate.query(
            sql_query,
            new Object[]{}, new RowMapper() {
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    HashMap<String, String> row = new HashMap<String, String>();
                    row.put("id",String.valueOf(rs.getInt("id")));
                    row.put("numero_economico",rs.getString("numero_economico"));
                    row.put("marca",rs.getString("marca"));
                    return row;
                }
            }
        );
        return hm;
    }
    
    
    //obtener datos de la Unidad a partir del Numero de Economico
    @Override
    public ArrayList<HashMap<String, String>> getDatosUnidadByNoEco(String no_eco, Integer id_empresa, Integer id_sucursal) {
        
        String where="";
        if(id_sucursal!=0){
            where +=" AND gral_suc_id="+id_sucursal;
        }
        
        String sql_query = "SELECT id,numero_economico,marca FROM log_vehiculos WHERE upper(numero_economico)='"+no_eco.toUpperCase().trim()+"' AND gral_emp_id="+id_empresa+" AND borrado_logico=false "+where+" LIMIT 1;";
        //System.out.println("getDatosVehiculo: "+sql_query);
        
        ArrayList<HashMap<String, String>> hm = (ArrayList<HashMap<String, String>>) this.jdbcTemplate.query(
            sql_query,
            new Object[]{}, new RowMapper() {
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    HashMap<String, String> row = new HashMap<String, String>();
                    row.put("id",String.valueOf(rs.getInt("id")));
                    row.put("numero_economico",rs.getString("numero_economico"));
                    row.put("marca",rs.getString("marca"));
                    return row;
                }
            }
        );
        return hm;
    }
    
    
    //Buscador de Operadores(Choferes)
    @Override
    public ArrayList<HashMap<String, String>> getBuscadorOperadores(String no_operador, String nombre, Integer id_empresa, Integer id_sucursal) {
        String where="";
        if(id_sucursal!=0){
            where = "AND sbt.gral_suc_id="+id_sucursal;
        }
        
	String sql_query = ""
                + "SELECT * FROM ( "
                    + "SELECT  id, clave, (CASE WHEN nombre IS NULL THEN '' ELSE nombre END)||' '||(CASE WHEN apellido_paterno IS NULL THEN '' ELSE apellido_paterno END)||' '||(CASE WHEN apellido_materno IS NULL THEN '' ELSE apellido_materno END) AS nombre, gral_emp_id, gral_suc_id, borrado_logico "
                    + "FROM log_choferes"
                + ") AS sbt "
                + "WHERE sbt.clave ILIKE '%"+no_operador+"%' AND sbt.nombre ILIKE '%"+nombre+"%' AND sbt.gral_emp_id="+id_empresa+" AND sbt.borrado_logico=false "+where+";";
        
        //System.out.println("getBuscadorUnidades: "+sql_query);
        ArrayList<HashMap<String, String>> hm = (ArrayList<HashMap<String, String>>) this.jdbcTemplate.query(
            sql_query,
            new Object[]{}, new RowMapper() {
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    HashMap<String, String> row = new HashMap<String, String>();
                    row.put("id",String.valueOf(rs.getInt("id")));
                    row.put("clave",rs.getString("clave"));
                    row.put("nombre",rs.getString("nombre"));
                    return row;
                }
            }
        );
        return hm;
    }
    
    
    //obtener datos del Operador a partir de la clave
    @Override
    public ArrayList<HashMap<String, String>> getDatosOperadorByNo(String no_operador, Integer id_empresa, Integer id_sucursal) {
        String where="";
        if(id_sucursal!=0){
            where = "AND sbt.gral_suc_id="+id_sucursal;
        }
        
	String sql_query = ""
                + "SELECT * FROM ( "
                    + "SELECT  id, clave, (CASE WHEN nombre IS NULL THEN '' ELSE nombre END)||' '||(CASE WHEN apellido_paterno IS NULL THEN '' ELSE apellido_paterno END)||' '||(CASE WHEN apellido_materno IS NULL THEN '' ELSE apellido_materno END) AS nombre, gral_emp_id, gral_suc_id, borrado_logico "
                    + "FROM log_choferes"
                + ") AS sbt "
                + "WHERE upper(sbt.clave)='"+no_operador.toUpperCase().trim()+"' AND sbt.gral_emp_id="+id_empresa+" AND sbt.borrado_logico=false "+where+" LIMIT 1;";
        
        //System.out.println("getBuscadorUnidades: "+sql_query);
        ArrayList<HashMap<String, String>> hm = (ArrayList<HashMap<String, String>>) this.jdbcTemplate.query(
            sql_query,
            new Object[]{}, new RowMapper() {
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    HashMap<String, String> row = new HashMap<String, String>();
                    row.put("id",String.valueOf(rs.getInt("id")));
                    row.put("clave",rs.getString("clave"));
                    row.put("nombre",rs.getString("nombre"));
                    return row;
                }
            }
        );
        return hm;    
    }
    
    
    //Buscador de Agentes Aduanales
    @Override
    public ArrayList<HashMap<String, Object>> getBuscadorAgentesAduanales(String cadena, Integer filtro, Integer id_empresa, Integer id_sucursal) {
        String where="";
        
	if(filtro == 1){
            where=" AND cxc_agentes_aduanales.folio ilike '%"+cadena+"%'";
	}
        /*
	if(filtro == 2){
            where=" AND cxc_agentes_aduanales.rfc ilike '%"+cadena+"%'";
	}
        */
	if(filtro == 3){
            where=" AND cxc_agentes_aduanales.razon_social ilike '%"+cadena+"%'";
	}
        if(id_sucursal==0){
            where +="";
        }else{
            where +=" AND cxc_agentes_aduanales.gral_suc_id="+id_sucursal;
        }
        
	String sql_query = ""
        + "SELECT cxc_agentes_aduanales.id,cxc_agentes_aduanales.folio, cxc_agentes_aduanales.razon_social, cxc_agentes_aduanales.tipo "
        +"FROM cxc_agentes_aduanales "
        +" WHERE cxc_agentes_aduanales.gral_emp_id ="+id_empresa+"  "
        +" AND cxc_agentes_aduanales.borrado_logico=false  "+where+" "
        + "ORDER BY id limit 100;";
        
        //System.out.println("BuscarAgenA: "+sql_query);
        ArrayList<HashMap<String, Object>> hm_dest = (ArrayList<HashMap<String, Object>>) this.jdbcTemplate.query(
            sql_query,
            new Object[]{}, new RowMapper() {
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    HashMap<String, Object> row = new HashMap<String, Object>();
                    row.put("id",rs.getInt("id"));
                    row.put("folio",rs.getString("folio"));
                    //row.put("rfc",rs.getString("rfc"));
                    row.put("razon_social",rs.getString("razon_social"));
                    row.put("tipo",rs.getInt("tipo"));
                    return row;
                }
            }
        );
        return hm_dest;
    }
    
    //Obtener datos del Agente Aduanal a partir del Numero de Control
    @Override
    public ArrayList<HashMap<String, Object>> getDatosByNoAgenteAduanal(String no_control, Integer id_empresa, Integer id_sucursal) {
        
        String where="";
        if(id_sucursal==0){
            where +="";
        }else{
            where +=" AND cxc_agentes_aduanales.gral_suc_id="+id_sucursal;
        }
        
	String sql_query = ""
        + "SELECT cxc_agentes_aduanales.id,cxc_agentes_aduanales.folio, cxc_agentes_aduanales.razon_social, cxc_agentes_aduanales.tipo "
        +"FROM cxc_agentes_aduanales "
        +" WHERE cxc_agentes_aduanales.gral_emp_id ="+id_empresa+"  "
        +" AND cxc_agentes_aduanales.borrado_logico=false  "+where+" "
        + "AND cxc_agentes_aduanales.folio='"+no_control.toUpperCase().trim()+"'"
        + "ORDER BY id limit 1;";
        
        //System.out.println("getDatosAgenA: "+sql_query);
        
        ArrayList<HashMap<String, Object>> hm_dest = (ArrayList<HashMap<String, Object>>) this.jdbcTemplate.query(
            sql_query,
            new Object[]{}, new RowMapper() {
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    HashMap<String, Object> row = new HashMap<String, Object>();
                    row.put("id",rs.getInt("id"));
                    row.put("folio",rs.getString("folio"));
                    row.put("razon_social",rs.getString("razon_social"));
                    row.put("tipo",rs.getInt("tipo"));
                    return row;
                }
            }
        );
        return hm_dest;
    }
    
    //Buscador de Remitentes
    @Override
    public ArrayList<HashMap<String, Object>> getBuscadorRemitentes(String cadena, Integer filtro, Integer id_empresa, Integer id_sucursal) {
        String where="";
        
	if(filtro == 1){
            where=" AND cxc_remitentes.folio ilike '%"+cadena+"%'";
	}
	if(filtro == 2){
            where=" AND cxc_remitentes.rfc ilike '%"+cadena+"%'";
	}
	if(filtro == 3){
            where=" AND cxc_remitentes.razon_social ilike '%"+cadena+"%'";
	}
        if(id_sucursal==0){
            where +="";
        }else{
            where +=" AND cxc_remitentes.gral_suc_id="+id_sucursal;
        }
        
	String sql_query = ""
        + "SELECT "
            + "id,"
            + "folio,"
            + "razon_social,"
            + "rfc,"
            + "(CASE WHEN rem.id IS NULL THEN '' ELSE rem.calle||' '||rem.no_int||' '||rem.no_ext||', '||rem.colonia||', '||rem.municipio||', '||rem.estado||', '||rem.pais||' C.P. '||rem.cp END) AS dir "
        + "FROM("
                + "SELECT cxc_remitentes.id, "
                    + "cxc_remitentes.folio,"
                    + "cxc_remitentes.razon_social,"
                    + "cxc_remitentes.rfc,"
                    + "(CASE WHEN cxc_remitentes.calle IS NULL THEN '' ELSE cxc_remitentes.calle END) AS calle, "
                    + "(CASE WHEN cxc_remitentes.no_int IS NULL THEN '' ELSE (CASE WHEN cxc_remitentes.no_int IS NULL OR cxc_remitentes.no_int='' THEN '' ELSE 'NO.INT.'||cxc_remitentes.no_int END)  END) AS no_int, "
                    + "(CASE WHEN cxc_remitentes.no_ext IS NULL THEN '' ELSE (CASE WHEN cxc_remitentes.no_ext IS NULL OR cxc_remitentes.no_ext='' THEN '' ELSE 'NO.EXT.'||cxc_remitentes.no_ext END)  END) AS no_ext, "
                    + "(CASE WHEN cxc_remitentes.colonia IS NULL THEN '' ELSE cxc_remitentes.colonia END) AS colonia,(CASE WHEN gral_mun.id IS NULL OR gral_mun.id=0 THEN '' ELSE gral_mun.titulo END) AS municipio,"
                    + "(CASE WHEN gral_edo.id IS NULL OR gral_edo.id=0 THEN '' ELSE gral_edo.titulo END) AS estado,(CASE WHEN gral_pais.id IS NULL OR gral_pais.id=0 THEN '' ELSE gral_pais.titulo END) AS pais,"
                    + "(CASE WHEN cxc_remitentes.cp IS NULL THEN '' ELSE cxc_remitentes.cp END) AS cp "
                + "FROM cxc_remitentes "
                + "LEFT JOIN gral_pais ON gral_pais.id = cxc_remitentes.gral_pais_id "
                + "LEFT JOIN gral_edo ON gral_edo.id = cxc_remitentes.gral_edo_id "
                + "LEFT JOIN gral_mun ON gral_mun.id = cxc_remitentes.gral_mun_id "
                +" WHERE cxc_remitentes.gral_emp_id ="+id_empresa+"  "
                +" AND cxc_remitentes.borrado_logico=false  "+where+" "
        + ") AS rem ORDER BY id limit 100;";
        
        //System.out.println("BuscarRemitente: "+sql_query);
        ArrayList<HashMap<String, Object>> hm_rem = (ArrayList<HashMap<String, Object>>) this.jdbcTemplate.query(
            sql_query,
            new Object[]{}, new RowMapper() {
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    HashMap<String, Object> row = new HashMap<String, Object>();
                    row.put("id",rs.getInt("id"));
                    row.put("folio",rs.getString("folio"));
                    row.put("rfc",rs.getString("rfc"));
                    row.put("razon_social",rs.getString("razon_social"));
                    row.put("dir",rs.getString("dir"));
                    return row;
                }
            }
        );
        return hm_rem;
    }
    
    //Obtener datos del Remitente a partir del Numero de Control
    @Override
    public ArrayList<HashMap<String, Object>> getDatosClienteByNoRemitente(String no_control, Integer id_empresa, Integer id_sucursal) {

        String where="";
        if(id_sucursal==0){
            where +="";
        }else{
            where +=" AND cxc_remitentes.gral_suc_id="+id_sucursal;
        }
        
	String sql_query = ""
        + "SELECT "
            + "id,"
            + "folio,"
            + "razon_social,"
            + "rfc,"
            + "(CASE WHEN rem.id IS NULL THEN '' ELSE rem.calle||' '||rem.no_int||' '||rem.no_ext||', '||rem.colonia||', '||rem.municipio||', '||rem.estado||', '||rem.pais||' C.P. '||rem.cp END) AS dir "
        + "FROM("
                + "SELECT cxc_remitentes.id, "
                    + "cxc_remitentes.folio,"
                    + "cxc_remitentes.razon_social,"
                    + "cxc_remitentes.rfc,"
                    + "(CASE WHEN cxc_remitentes.calle IS NULL THEN '' ELSE cxc_remitentes.calle END) AS calle, "
                    + "(CASE WHEN cxc_remitentes.no_int IS NULL THEN '' ELSE (CASE WHEN cxc_remitentes.no_int IS NULL OR cxc_remitentes.no_int='' THEN '' ELSE 'NO.INT.'||cxc_remitentes.no_int END)  END) AS no_int, "
                    + "(CASE WHEN cxc_remitentes.no_ext IS NULL THEN '' ELSE (CASE WHEN cxc_remitentes.no_ext IS NULL OR cxc_remitentes.no_ext='' THEN '' ELSE 'NO.EXT.'||cxc_remitentes.no_ext END)  END) AS no_ext, "
                    + "(CASE WHEN cxc_remitentes.colonia IS NULL THEN '' ELSE cxc_remitentes.colonia END) AS colonia,(CASE WHEN gral_mun.id IS NULL OR gral_mun.id=0 THEN '' ELSE gral_mun.titulo END) AS municipio,"
                    + "(CASE WHEN gral_edo.id IS NULL OR gral_edo.id=0 THEN '' ELSE gral_edo.titulo END) AS estado,(CASE WHEN gral_pais.id IS NULL OR gral_pais.id=0 THEN '' ELSE gral_pais.titulo END) AS pais,"
                    + "(CASE WHEN cxc_remitentes.cp IS NULL THEN '' ELSE cxc_remitentes.cp END) AS cp "
                + "FROM cxc_remitentes "
                + "LEFT JOIN gral_pais ON gral_pais.id = cxc_remitentes.gral_pais_id "
                + "LEFT JOIN gral_edo ON gral_edo.id = cxc_remitentes.gral_edo_id "
                + "LEFT JOIN gral_mun ON gral_mun.id = cxc_remitentes.gral_mun_id "
                +" WHERE cxc_remitentes.gral_emp_id ="+id_empresa+"  "
                +" AND cxc_remitentes.borrado_logico=false  "+where+" "
                + "AND cxc_remitentes.folio='"+no_control.toUpperCase().trim()+"'"
        + ") AS rem ORDER BY id limit 1;";
        
        
        //System.out.println("getDatosRemitente: "+sql_query);

        ArrayList<HashMap<String, Object>> hm_rem = (ArrayList<HashMap<String, Object>>) this.jdbcTemplate.query(
            sql_query,
            new Object[]{}, new RowMapper() {
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    HashMap<String, Object> row = new HashMap<String, Object>();
                    row.put("id",rs.getInt("id"));
                    row.put("folio",rs.getString("folio"));
                    row.put("rfc",rs.getString("rfc"));
                    row.put("razon_social",rs.getString("razon_social"));
                    row.put("dir",rs.getString("dir"));
                    return row;
                }
            }
        );
        return hm_rem;
    }
    
    
    
    //Buscador de Destinatarios
    @Override
    public ArrayList<HashMap<String, Object>> getBuscadorDestinatarios(String cadena, Integer filtro, Integer id_empresa, Integer id_sucursal) {
        String where="";
        
	if(filtro == 1){
            where=" AND cxc_destinatarios.folio ilike '%"+cadena+"%'";
	}
	if(filtro == 2){
            where=" AND cxc_destinatarios.rfc ilike '%"+cadena+"%'";
	}
	if(filtro == 3){
            where=" AND cxc_destinatarios.razon_social ilike '%"+cadena+"%'";
	}
        if(id_sucursal==0){
            where +="";
        }else{
            where +=" AND cxc_destinatarios.gral_suc_id="+id_sucursal;
        }
        
	String sql_query = ""
        + "SELECT "
            + "id,"
            + "folio,"
            + "razon_social,"
            + "rfc,"
            + "(CASE WHEN dest.id IS NULL THEN '' ELSE dest.calle||' '||dest.no_int||' '||dest.no_ext||', '||dest.colonia||', '||dest.municipio||', '||dest.estado||', '||dest.pais||' C.P. '||dest.cp END) AS dir  "
        + "FROM("
                + "SELECT cxc_destinatarios.id, "
                    + "cxc_destinatarios.folio,"
                    + "cxc_destinatarios.razon_social,"
                    + "cxc_destinatarios.rfc,"
                    + "(CASE WHEN cxc_destinatarios.calle IS NULL THEN '' ELSE cxc_destinatarios.calle END) AS calle, "
                    + "(CASE WHEN cxc_destinatarios.no_int IS NULL THEN '' ELSE (CASE WHEN cxc_destinatarios.no_int IS NULL OR cxc_destinatarios.no_int='' THEN '' ELSE 'NO.INT.'||cxc_destinatarios.no_int END)  END) AS no_int, "
                    + "(CASE WHEN cxc_destinatarios.no_ext IS NULL THEN '' ELSE (CASE WHEN cxc_destinatarios.no_ext IS NULL OR cxc_destinatarios.no_ext='' THEN '' ELSE 'NO.EXT.'||cxc_destinatarios.no_ext END)  END) AS no_ext, "
                    + "(CASE WHEN cxc_destinatarios.colonia IS NULL THEN '' ELSE cxc_destinatarios.colonia END) AS colonia,(CASE WHEN gral_mun.id IS NULL OR gral_mun.id=0 THEN '' ELSE gral_mun.titulo END) AS municipio,"
                    + "(CASE WHEN gral_edo.id IS NULL OR gral_edo.id=0 THEN '' ELSE gral_edo.titulo END) AS estado,(CASE WHEN gral_pais.id IS NULL OR gral_pais.id=0 THEN '' ELSE gral_pais.titulo END) AS pais,"
                    + "(CASE WHEN cxc_destinatarios.cp IS NULL THEN '' ELSE cxc_destinatarios.cp END) AS cp "
                + "FROM cxc_destinatarios "
                + "LEFT JOIN gral_pais ON gral_pais.id = cxc_destinatarios.gral_pais_id "
                + "LEFT JOIN gral_edo ON gral_edo.id = cxc_destinatarios.gral_edo_id "
                + "LEFT JOIN gral_mun ON gral_mun.id = cxc_destinatarios.gral_mun_id  "
                +" WHERE cxc_destinatarios.gral_emp_id ="+id_empresa+"  "
                +" AND cxc_destinatarios.borrado_logico=false  "+where+" "
        + ") AS dest ORDER BY id limit 100;";
        
        //System.out.println("BuscarDest: "+sql_query);
        ArrayList<HashMap<String, Object>> hm_dest = (ArrayList<HashMap<String, Object>>) this.jdbcTemplate.query(
            sql_query,
            new Object[]{}, new RowMapper() {
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    HashMap<String, Object> row = new HashMap<String, Object>();
                    row.put("id",rs.getInt("id"));
                    row.put("folio",rs.getString("folio"));
                    row.put("rfc",rs.getString("rfc"));
                    row.put("razon_social",rs.getString("razon_social"));
                    row.put("dir",rs.getString("dir"));
                    return row;
                }
            }
        );
        return hm_dest;
    }
    
    //Obtener datos del Destinatario a partir del Numero de Control
    @Override
    public ArrayList<HashMap<String, Object>> getDatosByNoDestinatario(String no_control, Integer id_empresa, Integer id_sucursal) {
        
        String where="";
        if(id_sucursal==0){
            where +="";
        }else{
            where +=" AND cxc_destinatarios.gral_suc_id="+id_sucursal;
        }
        
	String sql_query = ""
        + "SELECT "
            + "id,"
            + "folio,"
            + "razon_social,"
            + "rfc,"
            + "(CASE WHEN dest.id IS NULL THEN '' ELSE dest.calle||' '||dest.no_int||' '||dest.no_ext||', '||dest.colonia||', '||dest.municipio||', '||dest.estado||', '||dest.pais||' C.P. '||dest.cp END) AS dir  "
        + "FROM("
                + "SELECT cxc_destinatarios.id, "
                    + "cxc_destinatarios.folio,"
                    + "cxc_destinatarios.razon_social,"
                    + "cxc_destinatarios.rfc,"
                    + "(CASE WHEN cxc_destinatarios.calle IS NULL THEN '' ELSE cxc_destinatarios.calle END) AS calle, "
                    + "(CASE WHEN cxc_destinatarios.no_int IS NULL THEN '' ELSE (CASE WHEN cxc_destinatarios.no_int IS NULL OR cxc_destinatarios.no_int='' THEN '' ELSE 'NO.INT.'||cxc_destinatarios.no_int END)  END) AS no_int, "
                    + "(CASE WHEN cxc_destinatarios.no_ext IS NULL THEN '' ELSE (CASE WHEN cxc_destinatarios.no_ext IS NULL OR cxc_destinatarios.no_ext='' THEN '' ELSE 'NO.EXT.'||cxc_destinatarios.no_ext END)  END) AS no_ext, "
                    + "(CASE WHEN cxc_destinatarios.colonia IS NULL THEN '' ELSE cxc_destinatarios.colonia END) AS colonia,(CASE WHEN gral_mun.id IS NULL OR gral_mun.id=0 THEN '' ELSE gral_mun.titulo END) AS municipio,"
                    + "(CASE WHEN gral_edo.id IS NULL OR gral_edo.id=0 THEN '' ELSE gral_edo.titulo END) AS estado,(CASE WHEN gral_pais.id IS NULL OR gral_pais.id=0 THEN '' ELSE gral_pais.titulo END) AS pais,"
                    + "(CASE WHEN cxc_destinatarios.cp IS NULL THEN '' ELSE cxc_destinatarios.cp END) AS cp "
                + "FROM cxc_destinatarios "
                + "LEFT JOIN gral_pais ON gral_pais.id = cxc_destinatarios.gral_pais_id "
                + "LEFT JOIN gral_edo ON gral_edo.id = cxc_destinatarios.gral_edo_id "
                + "LEFT JOIN gral_mun ON gral_mun.id = cxc_destinatarios.gral_mun_id  "
                +" WHERE cxc_destinatarios.gral_emp_id ="+id_empresa+"  "
                +" AND cxc_destinatarios.borrado_logico=false  "+where+" "
                + "AND cxc_destinatarios.folio='"+no_control.toUpperCase().trim()+"'"
        + ") AS dest ORDER BY id limit 1;";
        
        //System.out.println("getDatosDest: "+sql_query);
        
        ArrayList<HashMap<String, Object>> hm_dest = (ArrayList<HashMap<String, Object>>) this.jdbcTemplate.query(
            sql_query,
            new Object[]{}, new RowMapper() {
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    HashMap<String, Object> row = new HashMap<String, Object>();
                    row.put("id",rs.getInt("id"));
                    row.put("folio",rs.getString("folio"));
                    row.put("rfc",rs.getString("rfc"));
                    row.put("razon_social",rs.getString("razon_social"));
                    row.put("dir",rs.getString("dir"));
                    return row;
                }
            }
        );
        return hm_dest;
    }
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    //obtener el tipo de Cliente
    @Override
    public int getTipoClient(Integer idClient) {
        int rowType = 0;
        int exis = this.getJdbcTemplate().queryForInt("SELECT count(id) AS exis FROM cxc_clie WHERE id="+idClient+";");
        
        if(exis>0){
            rowType = this.getJdbcTemplate().queryForInt("SELECT (CASE WHEN clienttipo_id IS NULL THEN 0 ELSE clienttipo_id END) AS tipo_cliente FROM cxc_clie WHERE id="+idClient+";");
        }
        
        return rowType;
    }
    
    
    
    @Override
    public ArrayList<HashMap<String, String>> getVerificarImpuesto(Integer idSuc, Integer idCliente, ArrayList<HashMap<String, String>> ArrayPres) {
        ArrayList<HashMap<String, String>> ArrayHmPres = new ArrayList<HashMap<String, String>>();
        ArrayList<HashMap<String, String>> ArrayImpto = new ArrayList<HashMap<String, String>>();
        String id_impto_suc="0";
        String valor_impto_suc="0.00";
        String id_impto_emp="0";
        Integer tipo_cliente = getTipoClient(idCliente);
        String id_impto_clie="0";
        String valor_impto_clie="0.00";
        
        if(tipo_cliente==2){
            //si el cliente es extranjero, hay que obtener el valor del iva tasa cero
            //idImpuesto=4 Exento 0%
            ArrayImpto = getValorivaById(4);
            id_impto_clie = ArrayImpto.get(0).get("id_impuesto");
            valor_impto_clie = ArrayImpto.get(0).get("valor_impuesto");
        }else{
            ArrayImpto = getValoriva(idSuc);
            if(ArrayImpto.size()>0){
                id_impto_suc = ArrayImpto.get(0).get("id_impuesto");
                valor_impto_suc = ArrayImpto.get(0).get("valor_impuesto");
                id_impto_emp = ArrayImpto.get(0).get("id_impto_emp");
                //valor_impto_emp = ArrayImpto.get(0).get("valor_impto_emp");
            }
        }
        
        Iterator it = ArrayPres.iterator();
        while(it.hasNext()){
            HashMap<String,String> map = (HashMap<String,String>)it.next();
            HashMap<String, String> rowmap = new HashMap<String, String>();
            Integer idImptoProd = Integer.parseInt(map.get("id_impto_prod"));
            
            if(tipo_cliente==2){
                rowmap.put("id_impto", id_impto_clie);
                rowmap.put("valor_impto", valor_impto_clie);
            }else{
                if(idImptoProd!=0){
                    if((Integer.parseInt(id_impto_emp) != idImptoProd)){
                        //si el impuesto del producto es Diferente al Impuesto General de la empresa,
                        //tomar impuesto del producto
                        rowmap.put("id_impto", map.get("id_impto_prod"));
                        rowmap.put("valor_impto", map.get("valor_impto_prod"));
                    }else{
                        //aqui entra si el Impuesto General de la Empresa es igual al impuesto del producto.
                        //tomar impuesto de la sucursal
                        rowmap.put("id_impto", id_impto_suc);
                        rowmap.put("valor_impto", valor_impto_suc);
                    }
                }else{
                    //si el impuesto del Producto es igual a cero,
                    //tomar impuesto de la sucursal
                    rowmap.put("id_impto", id_impto_suc);
                    rowmap.put("valor_impto", valor_impto_suc);
                }
            }
            
            rowmap.put("id",  map.get("id"));
            rowmap.put("sku", map.get("sku"));
            rowmap.put("titulo", map.get("titulo"));
            rowmap.put("descripcion_larga", map.get("descripcion_larga"));
            rowmap.put("archivo_img", map.get("archivo_img"));
            rowmap.put("unidad_id", map.get("unidad_id"));
            rowmap.put("unidad", map.get("unidad"));
            rowmap.put("id_presentacion", map.get("id_presentacion"));
            rowmap.put("presentacion", map.get("presentacion"));
            rowmap.put("decimales", map.get("decimales"));
            rowmap.put("precio", map.get("precio"));
            rowmap.put("exis_prod_lp",map.get("exis_prod_lp"));
            rowmap.put("id_moneda", map.get("id_moneda"));
            rowmap.put("tc", map.get("tc"));
            rowmap.put("ieps_id", map.get("ieps_id"));
            rowmap.put("ieps_tasa", map.get("ieps_tasa"));
            rowmap.put("ret_id", map.get("ret_id"));
            rowmap.put("ret_tasa", map.get("ret_tasa"));
            //System.out.println("id:"+rowmap.get("id")+"|sku:"+rowmap.get("sku")+"|titulo:"+rowmap.get("titulo")+"|unidad:"+rowmap.get("unidad")+"|idPres:"+rowmap.get("id_presentacion")+"|Pres:"+rowmap.get("presentacion")+"|noDec:"+rowmap.get("decimales")+"|precio:"+rowmap.get("precio")+"|exisLp:"+rowmap.get("exis_prod_lp")+"|idMon:"+rowmap.get("id_moneda")+"|tc:"+rowmap.get("tc")+"|idImpto:"+rowmap.get("id_impto")+"|valImpto:"+rowmap.get("valor_impto"));
            
            ArrayHmPres.add(rowmap);
        }
        
        return ArrayHmPres;
    }
    
    
    
    

    @Override
    public ArrayList<HashMap<String, Object>> getRemisiones_PaginaGrid(String data_string, int offset, int pageSize, String orderBy, String asc) {
        String sql_busqueda = "select id from gral_bus_catalogos(?) as foo (id integer)";
	String sql_to_query = "SELECT DISTINCT  "
                                +"fac_rems.id, "
                                +"fac_rems.folio, "
                                +"cxc_clie.razon_social as cliente, "
                                +"fac_rems.total, "
                                +"gral_mon.descripcion_abr AS denominacion, "
                                +"fac_rems.folio_pedido, "
                                +"(CASE WHEN fac_rems.cancelado=TRUE THEN 'CANCELADO' "
                                + "WHEN fac_rems.facturado=TRUE THEN 'FACTURADO' "
                                + "WHEN fac_rems.estatus=1 THEN 'PAGADO' "
                                + "ELSE "
                                    + " (CASE WHEN erp_proceso_flujo.titulo IS NULL THEN '' ELSE erp_proceso_flujo.titulo END) "
                                + "END) as estado, "
                                +"to_char(fac_rems.momento_creacion,'dd/mm/yyyy') as fecha_creacion "
                            + "FROM fac_rems "
                            //+"LEFT JOIN erp_proceso on erp_proceso.id = fac_rems.proceso_id "
                            //+"LEFT JOIN fac_docs on fac_docs.proceso_id = erp_proceso.id "
                            //+"LEFT JOIN erp_proceso_flujo on erp_proceso_flujo.id = erp_proceso.proceso_flujo_id "
                            + "LEFT JOIN cxc_clie on cxc_clie.id = fac_rems.cxc_clie_id "
                            + "LEFT JOIN gral_mon ON gral_mon.id=fac_rems.moneda_id "
                            + "LEFT JOIN fac_rems_docs ON fac_rems_docs.fac_rem_id=fac_rems.id "
                            + "LEFT JOIN erp_proceso as proc_rem ON proc_rem.id=fac_rems_docs.erp_proceso_id "
                            + "LEFT JOIN erp_proceso_flujo on erp_proceso_flujo.id = proc_rem.proceso_flujo_id "
                
                            + "JOIN ("+sql_busqueda+") as subt on subt.id=fac_rems.id "
                            + "order by "+orderBy+" "+asc+" limit ? OFFSET ?";

        //System.out.println("sql_to_query: "+sql_to_query);
        //System.out.println("data_string: "+data_string);
        ArrayList<HashMap<String, Object>> hm = (ArrayList<HashMap<String, Object>>) this.jdbcTemplate.query(
            sql_to_query,
            new Object[]{new String(data_string),new Integer(pageSize),new Integer(offset)}, new RowMapper() {
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    HashMap<String, Object> row = new HashMap<String, Object>();
                    row.put("id",rs.getInt("id"));
                    row.put("folio",rs.getString("folio"));
                    row.put("cliente",rs.getString("cliente"));
                    row.put("total",StringHelper.AgregaComas(StringHelper.roundDouble(rs.getString("total"),2)));
                    row.put("denominacion",rs.getString("denominacion"));
                    row.put("folio_pedido",rs.getString("folio_pedido"));
                    row.put("estado",rs.getString("estado"));
                    row.put("fecha_creacion",rs.getString("fecha_creacion"));
                    return row;
                }
            }
        );
        return hm;
    }




    @Override
    public ArrayList<HashMap<String, String>> getMetodosPago(Integer empresaId) {
        String sql_to_query = "SELECT id, (case when clave_sat<>'' then clave_sat||' - ' else '' end)||titulo as titulo FROM fac_metodos_pago WHERE borrado_logico=false and gral_emp_id=?;";
        //log.log(Level.INFO, "Ejecutando query de {0}", sql_to_query);
        ArrayList<HashMap<String, String>> hm = (ArrayList<HashMap<String, String>>) this.jdbcTemplate.query(
            sql_to_query,
            new Object[]{new Integer(empresaId)}, new RowMapper(){
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    HashMap<String, String> row = new HashMap<String, String>();
                    row.put("id",String.valueOf(rs.getInt("id"))  );
                    row.put("titulo",rs.getString("titulo"));
                    return row;
                }
            }
        );
        return hm;
    }



    //obtiene datos de la remision para visualizar al consultar
    @Override
    public ArrayList<HashMap<String, String>> getRemisiones_Datos(Integer id_remision) {
        String sql_query = ""
                + "SELECT fac_rems.id, "
                        + "fac_rems.folio, "
                        + "fac_rems.folio_pedido, "
                        + "erp_proceso.proceso_flujo_id, "
                        + "fac_rems.moneda_id, "
                        + "gral_mon.descripcion as moneda, "
                        + "fac_rems.observaciones, "
                        + "cxc_clie.id as cliente_id, "
                        + "cxc_clie.numero_control, "
                        + "cxc_clie.razon_social, "
                        + "cxc_clie.empresa_immex, "
                        + "fac_rems.subtotal, "
                        + "fac_rems.impuesto, "
                        + "fac_rems.total,  "
                        + "fac_rems.monto_descto, "
                        + "fac_rems.monto_ieps, "
                        + "fac_rems.monto_retencion,  "
                        + "fac_rems.tipo_cambio, "
                        + "fac_rems.cxc_agen_id,  "
                        + "fac_rems.cxc_clie_credias_id,  "
                        + "fac_rems.orden_compra,  "
                        + "fac_rems.cancelado,"
                        + "fac_rems.facturado,"
                        + "fac_rems.fac_metodos_pago_id,  "
                        + "fac_rems.no_cuenta, "
                        + "fac_rems.tasa_retencion_immex, "
                        + "(CASE WHEN (select count(id) from fac_rems_docs where fac_rem_id="+id_remision+") = 0 AND fac_rems.cancelado is false AND  fac_rems.estatus=0 THEN 0 ELSE 1 END) as estatus "//agregado por paco, por el boton de pagar
                + "FROM fac_rems  "
                + "LEFT JOIN erp_proceso ON erp_proceso.id = fac_rems.proceso_id  "
                + "LEFT JOIN gral_mon ON gral_mon.id = fac_rems.moneda_id  "
                + "LEFT JOIN cxc_clie ON cxc_clie.id=fac_rems.cxc_clie_id   "
                + "WHERE fac_rems.id="+id_remision;

        ArrayList<HashMap<String, String>> hm = (ArrayList<HashMap<String, String>>) this.jdbcTemplate.query(
            sql_query,
            new Object[]{}, new RowMapper() {
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    HashMap<String, String> row = new HashMap<String, String>();
                    row.put("id",String.valueOf(rs.getInt("id")));
                    row.put("folio",rs.getString("folio"));
                    row.put("folio_pedido",rs.getString("folio_pedido"));
                    row.put("proceso_flujo_id",String.valueOf(rs.getInt("proceso_flujo_id")));
                    row.put("moneda_id",rs.getString("moneda_id"));
                    row.put("moneda",rs.getString("moneda"));
                    row.put("observaciones",rs.getString("observaciones"));
                    row.put("cliente_id",rs.getString("cliente_id"));
                    row.put("numero_control",rs.getString("numero_control"));
                    row.put("razon_social",rs.getString("razon_social"));
                    row.put("subtotal",StringHelper.roundDouble(rs.getDouble("subtotal"),2));
                    row.put("impuesto",StringHelper.roundDouble(rs.getDouble("impuesto"),2));
                    row.put("total",StringHelper.roundDouble(rs.getDouble("total"),2));
                    row.put("monto_descto",StringHelper.roundDouble(rs.getDouble("monto_descto"),2));
                    row.put("monto_ieps",StringHelper.roundDouble(rs.getDouble("monto_ieps"),2));
                    row.put("monto_retencion",StringHelper.roundDouble(rs.getDouble("monto_retencion"),2));
                    row.put("tipo_cambio",StringHelper.roundDouble(rs.getDouble("tipo_cambio"),4));
                    row.put("cxc_agen_id",rs.getString("cxc_agen_id"));
                    row.put("terminos_id",rs.getString("cxc_clie_credias_id"));
                    row.put("orden_compra",rs.getString("orden_compra"));
                    row.put("cancelado",String.valueOf(rs.getBoolean("cancelado")));
                    row.put("facturado",String.valueOf(rs.getBoolean("facturado")));
                    row.put("fac_metodos_pago_id",String.valueOf(rs.getInt("fac_metodos_pago_id")));
                    row.put("no_cuenta",rs.getString("no_cuenta"));
                    row.put("empresa_immex",String.valueOf(rs.getBoolean("empresa_immex")));
                    row.put("tasa_retencion_immex",StringHelper.roundDouble(rs.getDouble("tasa_retencion_immex"),2));
                    row.put("estatus",String.valueOf(rs.getInt("estatus")));
                    return row;
                }
            }
        );
        return hm;
    }


    @Override
    public ArrayList<HashMap<String, String>> getRemisiones_DatosGrid(Integer id_remision) {
        String sql_query = ""
                + "SELECT fac_rems_detalles.id as id_detalle,"
                        + "fac_rems_detalles.inv_prod_id,"
                        + "inv_prod.sku AS codigo,"
                        + "inv_prod.descripcion AS titulo,"
                        + "(CASE WHEN inv_prod_unidades.titulo IS NULL THEN '' ELSE inv_prod_unidades.titulo END) as unidad,"
                        + "(CASE WHEN inv_prod_unidades.decimales IS NULL THEN 0 ELSE inv_prod_unidades.decimales END) AS decimales,"
                        + "(CASE WHEN inv_prod_presentaciones.id IS NULL THEN 0 ELSE inv_prod_presentaciones.id END) as id_presentacion,"
                        + "(CASE WHEN inv_prod_presentaciones.titulo IS NULL THEN '' ELSE inv_prod_presentaciones.titulo END) as presentacion,"
                        + "fac_rems_detalles.cantidad,"
                        + "fac_rems_detalles.precio_unitario,"
                        + "(fac_rems_detalles.cantidad * fac_rems_detalles.precio_unitario) AS importe, "
                        + "fac_rems_detalles.gral_imp_id,"
                        + "fac_rems_detalles.valor_imp, "
                        + "fac_rems_detalles.gral_ieps_id AS id_ieps,"
                        + "(fac_rems_detalles.valor_ieps * 100::double precision) AS tasa_ieps, "
                        + "((fac_rems_detalles.cantidad * fac_rems_detalles.precio_unitario) * fac_rems_detalles.valor_ieps) AS importe_ieps "
                + "FROM fac_rems_detalles "
                + "LEFT JOIN inv_prod on inv_prod.id = fac_rems_detalles.inv_prod_id "
                + "LEFT JOIN inv_prod_unidades on inv_prod_unidades.id = fac_rems_detalles.inv_prod_unidad_id "
                + "LEFT JOIN inv_prod_presentaciones on inv_prod_presentaciones.id = fac_rems_detalles.inv_prod_presentacion_id "
                + "WHERE fac_rems_detalles.fac_rems_id="+id_remision;

        //System.out.println("Obtiene datos grid prefactura: "+sql_query);
        ArrayList<HashMap<String, String>> hm_grid = (ArrayList<HashMap<String, String>>) this.jdbcTemplate.query(
            sql_query,
            new Object[]{}, new RowMapper() {
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    HashMap<String, String> row = new HashMap<String, String>();
                    row.put("id_detalle",String.valueOf(rs.getInt("id_detalle")));
                    row.put("inv_prod_id",String.valueOf(rs.getInt("inv_prod_id")));
                    row.put("codigo",rs.getString("codigo"));
                    row.put("titulo",rs.getString("titulo"));
                    row.put("unidad",rs.getString("unidad"));
                    row.put("id_presentacion",String.valueOf(rs.getInt("id_presentacion")));
                    row.put("presentacion",rs.getString("presentacion"));
                    row.put("cantidad",StringHelper.roundDouble( rs.getString("cantidad"), 2 ));
                    row.put("precio_unitario",StringHelper.roundDouble(rs.getDouble("precio_unitario"),4) );
                    row.put("importe",StringHelper.roundDouble(rs.getDouble("importe"),4) );
                    row.put("gral_imp_id",String.valueOf(rs.getInt("gral_imp_id")));
                    row.put("valor_imp",StringHelper.roundDouble(rs.getDouble("valor_imp"),4) );
                    row.put("id_ieps",String.valueOf(rs.getInt("id_ieps")));
                    row.put("tasa_ieps",StringHelper.roundDouble(rs.getDouble("tasa_ieps"),2) );
                    row.put("importe_ieps",StringHelper.roundDouble(rs.getDouble("importe_ieps"),4) );
                    return row;
                }
            }
        );
        return hm_grid;
    }





    @Override
    public HashMap<String, String> getRemisiones_DatosPdf(Integer id_remision) {
        HashMap<String, String> datos = new HashMap<String, String>();
        String sql_to_query = ""
                + "SELECT "
                        + "fac_rems.id AS id_remision,"
                        + "fac_rems.folio,"
                        + "fac_rems.subtotal,"
                        + "fac_rems.impuesto,"
                        + "fac_rems.monto_retencion,"
                        + "fac_rems.total,"
                        + "fac_rems.tipo_cambio,"
                        + "fac_rems.no_cuenta,"
                        + "fac_rems.orden_compra,"
                        + "fac_rems.observaciones,"
                        + "to_char(fac_rems.momento_creacion,'dd/mm/yyyy') AS fecha_remision,"
                        + "gral_mon.simbolo AS simbolo_moneda,"
                        + "gral_mon.descripcion_abr AS moneda_abr,"
                        + "gral_mon.descripcion AS titulo_moneda,"
                        + "cxc_clie_credias.dias AS dias_credito,"
                        + "cxc_clie.razon_social AS cliente,"
                        + "cxc_clie.rfc AS cliente_rfc, "
                        + "(CASE WHEN fac_rems.cxc_clie_df_id > 1 THEN sbtdf.calle ELSE cxc_clie.calle END ) AS cliente_calle,"
                        + "(CASE WHEN fac_rems.cxc_clie_df_id > 1 THEN (sbtdf.numero_interior||' '||sbtdf.numero_exterior) ELSE cxc_clie.numero END ) AS cliente_numero,"
                        + "(CASE WHEN fac_rems.cxc_clie_df_id > 1 THEN sbtdf.colonia ELSE cxc_clie.colonia END ) AS cliente_colonia,"
                        + "(CASE WHEN fac_rems.cxc_clie_df_id > 1 THEN sbtdf.municipio ELSE gral_mun.titulo END ) AS cliente_municipio,"
                        + "(CASE WHEN fac_rems.cxc_clie_df_id > 1 THEN sbtdf.estado ELSE gral_edo.titulo END ) AS cliente_estado,"
                        + "(CASE WHEN fac_rems.cxc_clie_df_id > 1 THEN sbtdf.pais ELSE gral_pais.titulo END ) AS cliente_pais,"
                        + "(CASE WHEN fac_rems.cxc_clie_df_id > 1 THEN sbtdf.cp ELSE cxc_clie.cp END ) AS cliente_cp,"
                        + "cxc_clie.telefono1 AS cliente_telefono,"
                        + "cxc_agen.nombre AS vendedor, "
                        + "(CASE WHEN fac_rems.cancelado=TRUE THEN 'REMISION CANCELADA' ELSE 'NO' END) AS cancelado,"
                        + "fac_rems.monto_ieps "
                + "FROM fac_rems "
                + "JOIN gral_mon ON gral_mon.id=fac_rems.moneda_id "
                + "JOIN cxc_clie ON cxc_clie.id=fac_rems.cxc_clie_id "
                + "JOIN gral_pais ON gral_pais.id = cxc_clie.pais_id "
                + "JOIN gral_edo ON gral_edo.id = cxc_clie.estado_id "
                + "JOIN gral_mun ON gral_mun.id = cxc_clie.municipio_id "
                + "JOIN cxc_clie_credias ON cxc_clie_credias.id = fac_rems.cxc_clie_credias_id "
                + "JOIN cxc_agen ON cxc_agen.id=fac_rems.cxc_agen_id "
                + "LEFT JOIN (SELECT cxc_clie_df.id, (CASE WHEN cxc_clie_df.calle IS NULL THEN '' ELSE cxc_clie_df.calle END) AS calle, (CASE WHEN cxc_clie_df.numero_interior IS NULL THEN '' ELSE (CASE WHEN cxc_clie_df.numero_interior IS NULL OR cxc_clie_df.numero_interior='' THEN '' ELSE 'NO.INT.'||cxc_clie_df.numero_interior END)  END) AS numero_interior, (CASE WHEN cxc_clie_df.numero_exterior IS NULL THEN '' ELSE (CASE WHEN cxc_clie_df.numero_exterior IS NULL OR cxc_clie_df.numero_exterior='' THEN '' ELSE 'NO.EXT.'||cxc_clie_df.numero_exterior END )  END) AS numero_exterior, (CASE WHEN cxc_clie_df.colonia IS NULL THEN '' ELSE cxc_clie_df.colonia END) AS colonia,(CASE WHEN gral_mun.id IS NULL OR gral_mun.id=0 THEN '' ELSE gral_mun.titulo END) AS municipio,(CASE WHEN gral_edo.id IS NULL OR gral_edo.id=0 THEN '' ELSE gral_edo.titulo END) AS estado,(CASE WHEN gral_pais.id IS NULL OR gral_pais.id=0 THEN '' ELSE gral_pais.titulo END) AS pais,(CASE WHEN cxc_clie_df.cp IS NULL THEN '' ELSE cxc_clie_df.cp END) AS cp  FROM cxc_clie_df LEFT JOIN gral_pais ON gral_pais.id = cxc_clie_df.gral_pais_id LEFT JOIN gral_edo ON gral_edo.id = cxc_clie_df.gral_edo_id LEFT JOIN gral_mun ON gral_mun.id = cxc_clie_df.gral_mun_id ) AS sbtdf ON sbtdf.id = fac_rems.cxc_clie_df_id "
                + "WHERE fac_rems.id="+id_remision;

        Map<String, Object> hm = this.getJdbcTemplate().queryForMap(sql_to_query);

        datos.put("id_remision", hm.get("id_remision").toString());
        datos.put("folio", hm.get("folio").toString());
        datos.put("subtotal", StringHelper.roundDouble(hm.get("subtotal").toString(),2));
        datos.put("monto_ieps", StringHelper.roundDouble(hm.get("monto_ieps").toString(),2));
        datos.put("impuesto", StringHelper.roundDouble(hm.get("impuesto").toString(),2));
        datos.put("monto_retencion", StringHelper.roundDouble(hm.get("monto_retencion").toString(),2));
        datos.put("total", StringHelper.roundDouble(hm.get("total").toString(),2));
        datos.put("tipo_cambio", StringHelper.roundDouble(hm.get("tipo_cambio").toString(),4));
        datos.put("no_cuenta", hm.get("no_cuenta").toString());
        datos.put("orden_compra", hm.get("orden_compra").toString());
        datos.put("observaciones", hm.get("observaciones").toString());
        datos.put("fecha_remision", hm.get("fecha_remision").toString());
        datos.put("simbolo_moneda", hm.get("simbolo_moneda").toString());
        datos.put("moneda_abr", hm.get("moneda_abr").toString());
        datos.put("titulo_moneda", hm.get("titulo_moneda").toString());
        datos.put("dias_credito", hm.get("dias_credito").toString());
        datos.put("cliente", hm.get("cliente").toString());
        datos.put("cliente_rfc", hm.get("cliente_rfc").toString());
        datos.put("cliente_calle", hm.get("cliente_calle").toString());
        datos.put("cliente_numero", hm.get("cliente_numero").toString());
        datos.put("cliente_colonia", hm.get("cliente_colonia").toString());
        datos.put("cliente_municipio", hm.get("cliente_municipio").toString());
        datos.put("cliente_estado", hm.get("cliente_estado").toString());
        datos.put("cliente_pais", hm.get("cliente_pais").toString());
        datos.put("cliente_cp", hm.get("cliente_cp").toString());
        datos.put("vendedor", hm.get("vendedor").toString());
        datos.put("cliente_telefono", hm.get("cliente_telefono").toString());
        datos.put("cancelado", hm.get("cancelado").toString());
        return datos;
    }



    @Override
    public ArrayList<HashMap<String, String>> getRemisiones_ConceptosPdf(Integer id_remision, String rfc_empresa) {
        final String rfc = rfc_empresa;
	String sql_query = ""
                + "SELECT "
                        + "inv_prod.sku AS codigo,"
                        + "inv_prod.descripcion,"
                        + "(CASE WHEN fac_rems_detalles.gral_ieps_id>0 THEN ' - IEPS '||(round((fac_rems_detalles.valor_ieps * 100::double precision)::numeric,2))||'%' ELSE '' END) AS etiqueta_ieps,"
                        + "(CASE WHEN inv_prod_unidades.titulo IS NULL THEN '' ELSE inv_prod_unidades.titulo END) as unidad,"
                        + "(CASE WHEN inv_prod_presentaciones.titulo IS NULL THEN '' ELSE inv_prod_presentaciones.titulo END) AS presentacion,"
                        + "fac_rems_detalles.cantidad,"
                        + "fac_rems_detalles.precio_unitario,"
                        + "(fac_rems_detalles.cantidad * fac_rems_detalles.precio_unitario) AS importe, "
                        + "(CASE WHEN fac_rems_detalles.gral_ieps_id>0 THEN ((fac_rems_detalles.cantidad * fac_rems_detalles.precio_unitario) * fac_rems_detalles.valor_ieps) ELSE 0 END) AS importe_ieps "
                + "FROM fac_rems_detalles "
                + "LEFT JOIN inv_prod on inv_prod.id = fac_rems_detalles.inv_prod_id "
                + "LEFT JOIN inv_prod_unidades on inv_prod_unidades.id = fac_rems_detalles.inv_prod_unidad_id "
                + "LEFT JOIN inv_prod_presentaciones on inv_prod_presentaciones.id = fac_rems_detalles.inv_prod_presentacion_id "
                + "WHERE fac_rems_detalles.fac_rems_id= "+id_remision;

        ArrayList<HashMap<String, String>> hm = (ArrayList<HashMap<String, String>>) this.jdbcTemplate.query(
            sql_query,
            new Object[]{}, new RowMapper() {
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    HashMap<String, String> row = new HashMap<String, String>();
                    row.put("codigo",rs.getString("codigo"));
                    row.put("descripcion",rs.getString("descripcion"));
                    //row.put("unidad",rs.getString("unidad"));
                    //row.put("presentacion",rs.getString("presentacion"));
                    
                    if( rfc.equals("PIS850531CS4") ){
                        row.put("unidad",StringHelper.normalizaString(StringHelper.remueve_tildes(rs.getString("presentacion"))));
                    }else{
                        row.put("unidad",StringHelper.normalizaString(StringHelper.remueve_tildes(rs.getString("unidad"))));
                    }
                    
                    row.put("cantidad",StringHelper.roundDouble(String.valueOf(rs.getDouble("cantidad")),2));
                    row.put("precio_unitario",StringHelper.roundDouble(String.valueOf(rs.getDouble("precio_unitario")),2));
                    row.put("importe",StringHelper.roundDouble(String.valueOf(rs.getDouble("importe")),4));
                    
                    row.put("etiqueta_ieps",rs.getString("etiqueta_ieps"));
                    row.put("importe_ieps",StringHelper.roundDouble(rs.getDouble("importe_ieps"),4) );
                    
                    return row;
                }
            }
        );
        return hm;
    }





   //obtiene lista de peidos de un periodo
    @Override
    public ArrayList<HashMap<String, String>> getReportePedidos(Integer opcion, Integer agente, String cliente, String fecha_inicial, String fecha_final,Integer id_empresa) {
        String where="";

        if (opcion!=0){
            where+=" AND erp_proceso.proceso_flujo_id="+opcion;
        }

        if (agente!=0){
            where+=" AND poc_pedidos.cxc_agen_id="+agente;
        }

        if (!cliente.equals("")){
            where+=" AND cxc_clie.razon_social ILIKE '%"+cliente+"%'";
        }

        String sql_to_query = ""
                + "SELECT "
                        + "folio,"
                        + "orden_compra,"
                        + "fecha_factura,"
                        + "cliente,"
                        + "moneda_factura,"
                        + "simbolo_moneda,"
                        + "subtotal,"
                        + "(subtotal*tipo_cambio) AS subtotal_mn, "
                        + "monto_ieps,"
                        + "(monto_ieps*tipo_cambio) AS monto_ieps_mn, "
                        + "impuesto,"
                        + "impuesto*tipo_cambio AS impuesto_mn, "
                        + "total,"
                        + "total*tipo_cambio AS total_mn "
                + "FROM ( "
                        + "SELECT  "
                                + "poc_pedidos.id, "
                                + "poc_pedidos.folio, "
                                + "(CASE WHEN poc_pedidos.orden_compra IS NULL THEN '' ELSE poc_pedidos.orden_compra END) AS orden_compra,  "
                                + "to_char(poc_pedidos.momento_creacion,'dd/mm/yyyy') as fecha_factura, "
                                + "(CASE WHEN poc_pedidos.cancelado=FALSE THEN cxc_clie.razon_social ELSE 'CANCELADA' END) AS cliente, "
                                + "poc_pedidos.moneda_id, "
                                + "gral_mon.descripcion_abr AS moneda_factura, "
                                + "gral_mon.simbolo AS simbolo_moneda, "
                                + "(CASE WHEN poc_pedidos.cancelado=FALSE THEN poc_pedidos.subtotal ELSE 0.0 END) AS subtotal,  "
                                + "(CASE WHEN poc_pedidos.cancelado=FALSE THEN poc_pedidos.monto_ieps ELSE 0.0 END) AS monto_ieps,  "
                                + "(CASE WHEN poc_pedidos.cancelado=FALSE THEN poc_pedidos.impuesto ELSE 0.0 END) AS impuesto, "
                                + "(CASE WHEN poc_pedidos.cancelado=FALSE THEN poc_pedidos.total ELSE 0.0 END) AS total,  "
                                + "(CASE WHEN poc_pedidos.moneda_id=1 THEN 1 ELSE poc_pedidos.tipo_cambio END) AS tipo_cambio   "
                        + "FROM poc_pedidos "
                        + "JOIN erp_proceso ON erp_proceso.id=poc_pedidos.proceso_id "
                        + "JOIN cxc_clie ON cxc_clie.id = poc_pedidos.cxc_clie_id   "
                        + "JOIN gral_mon ON gral_mon.id = poc_pedidos.moneda_id  "
                        + "WHERE erp_proceso.empresa_id="+id_empresa+" "+where+" "
                        + "AND (to_char(poc_pedidos.momento_creacion,'yyyymmdd')::integer BETWEEN  to_char('"+fecha_inicial+"'::timestamp with time zone,'yyyymmdd')::integer AND to_char('"+fecha_final+"'::timestamp with time zone,'yyyymmdd')::integer) "
                + ") AS sbt "
                + "ORDER BY id";

        //System.out.println("Buscando facturas: "+sql_to_query);
        ArrayList<HashMap<String, String>> hm_facturas = (ArrayList<HashMap<String, String>>) this.jdbcTemplate.query(
            sql_to_query,
            new Object[]{}, new RowMapper(){
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    HashMap<String, String> row = new HashMap<String, String>();
                    row.put("folio",rs.getString("folio"));
                    row.put("orden_compra",rs.getString("orden_compra"));
                    row.put("fecha_factura",rs.getString("fecha_factura"));
                    row.put("cliente",rs.getString("cliente"));
                    row.put("moneda_factura",rs.getString("moneda_factura"));
                    row.put("simbolo_moneda",rs.getString("simbolo_moneda"));
                    row.put("subtotal",StringHelper.roundDouble(rs.getString("subtotal"),2));
                    row.put("subtotal_mn",StringHelper.roundDouble(rs.getString("subtotal_mn"),2));
                    row.put("monto_ieps",StringHelper.roundDouble(rs.getString("monto_ieps"),2));
                    row.put("monto_ieps_mn",StringHelper.roundDouble(rs.getString("monto_ieps_mn"),2));
                    row.put("impuesto",StringHelper.roundDouble(rs.getString("impuesto"),2));
                    row.put("impuesto_mn",StringHelper.roundDouble(rs.getString("impuesto_mn"),2));
                    row.put("total",StringHelper.roundDouble(rs.getString("total"),2));
                    row.put("total_mn",StringHelper.roundDouble(rs.getString("total_mn"),2));
                    return row;
                }
            }
        );
        return hm_facturas;
    }

    //alimenta el select de los agentes en reporte de pedidos

    @Override
    public ArrayList<HashMap<String, String>> getAgente(Integer id_empresa) {
        String sql_to_query = "SELECT cxc_agen.id, cxc_agen.nombre "
                               +"FROM cxc_agen "
                               +"join gral_usr on gral_usr.id = cxc_agen.gral_usr_id "
                               +"join gral_usr_suc on gral_usr_suc.gral_usr_id= gral_usr.id "
                               +"join gral_suc on gral_suc.id =gral_usr_suc.gral_suc_id "
                               +"WHERE gral_suc.empresa_id ="+id_empresa;


        ArrayList<HashMap<String,String>> hmtl_agente =(ArrayList<HashMap<String,String>>) this.jdbcTemplate.query(
            sql_to_query,new Object[]{},new RowMapper(){

             @Override
             public Object mapRow(ResultSet rs,int rowNum) throws SQLException{
                 HashMap<String, String > row = new HashMap<String, String>();
                    row.put("id",String.valueOf(rs.getInt("id")));
                    row.put("nombre",String.valueOf(rs.getString("nombre")));
                    return row;
             }
            }
        );

        return hmtl_agente;

    }
    @Override
    public ArrayList<HashMap<String, String>> getEstadoPedido() {
        String sql_to_query = "SELECT erp_proceso_flujo.titulo, "
                              +"erp_proceso_flujo.id "
                              +"FROM erp_proceso_flujo "
                              +"WHERE erp_proceso_flujo.id in(2,3,4,5)"
                              +"ORDER BY titulo";
        ArrayList<HashMap<String,String>> hmtl_agente =(ArrayList<HashMap<String,String>>) this.jdbcTemplate.query(
            sql_to_query,new Object[]{},new RowMapper(){

             @Override
             public Object mapRow(ResultSet rs,int rowNum) throws SQLException{
                 HashMap<String, String > row = new HashMap<String, String>();
                    row.put("id",String.valueOf(rs.getInt("id")));
                    row.put("titulo",String.valueOf(rs.getString("titulo")));
                    return row;
             }
            }
        );

        return hmtl_agente;

    }
    
    
    
    
    
    //metodo para reporte de articulos reservados
    @Override
    public ArrayList<HashMap<String, String>> getReporteArticulosReservados(Integer id_empresa,Integer id_usuario, String folio_pedido, String codigo, String descripcion) {
        //System.out.println("Codigo: "+codigo+"Descripcion: "+descripcion);
        String   cadena_where="";
        if(!codigo.equals("")){
            cadena_where=" AND inv_prod.sku ILIKE '%"+codigo.toUpperCase() +"%'";
        }
        
        if(!descripcion.equals("")){
            cadena_where=cadena_where+" AND inv_prod.descripcion ILIKE '%"+descripcion.toUpperCase() +"%'";
        }
        
        if(!folio_pedido.equals("")){
            cadena_where=cadena_where+" AND poc_pedidos.folio ILIKE '%"+folio_pedido.toUpperCase()+"%'";
        }
        
       String sql_to_query =""
       + "SELECT  "
           + "poc_pedidos.id as id_pedido, "
           + "poc_pedidos.folio as pedido, "
           + "to_char(poc_pedidos.momento_creacion,'dd/mm/yyyy') as fecha, "
           + "cxc_clie.razon_social as cliente,  "
           + "(CASE WHEN uni_prod.id<>uni_venta.id THEN  "
               + "(CASE WHEN uni_venta.titulo ~* 'LITRO*' THEN  "
                   + "(poc_pedidos_detalle.reservado / (CASE WHEN inv_prod.densidad=0 THEN 1 ELSE inv_prod.densidad END)) "
               + "ELSE  "
                   + "(CASE WHEN uni_venta.titulo ~* 'KILO*' THEN  "
                        + "(poc_pedidos_detalle.reservado * (CASE WHEN inv_prod.densidad=0 THEN 1 ELSE inv_prod.densidad END)) "
                   + "ELSE  "
                        + "poc_pedidos_detalle.reservado "
                   + "END) "
               + "END) "
           + "ELSE "
                + "poc_pedidos_detalle.reservado	 "
           + "END ) AS cantidad, "
           //+ "poc_pedidos_detalle.reservado AS cantidad,  "
           + "(case when poc_pedidos.moneda_id=1 then  poc_pedidos_detalle.precio_unitario  else (poc_pedidos_detalle.precio_unitario * tipo_cambio  )end ) as precio_unitario,  "
           + "poc_pedidos.moneda_id, "
           + "(CASE WHEN uni_prod.id<>uni_venta.id THEN (CASE WHEN uni_venta.titulo ~* 'LITRO*' THEN (poc_pedidos_detalle.reservado / (CASE WHEN inv_prod.densidad=0 THEN 1 ELSE inv_prod.densidad END))ELSE (CASE WHEN uni_venta.titulo ~* 'KILO*' THEN (poc_pedidos_detalle.reservado * (CASE WHEN inv_prod.densidad=0 THEN 1 ELSE inv_prod.densidad END)) ELSE poc_pedidos_detalle.reservado END) END) ELSE poc_pedidos_detalle.reservado END) * poc_pedidos_detalle.precio_unitario as importe_sin_checar_tipo_cambio,  "
           //+ "(case when poc_pedidos.moneda_id=1 then  poc_pedidos_detalle.reservado * poc_pedidos_detalle.precio_unitario else ((poc_pedidos_detalle.precio_unitario * tipo_cambio )*  poc_pedidos_detalle.reservado )  end) as importe, "
           + "(case when poc_pedidos.moneda_id=1 then  "
                + "(poc_pedidos_detalle.precio_unitario * (CASE WHEN uni_prod.id<>uni_venta.id THEN (CASE WHEN uni_venta.titulo ~* 'LITRO*' THEN (poc_pedidos_detalle.reservado / (CASE WHEN inv_prod.densidad=0 THEN 1 ELSE inv_prod.densidad END))ELSE (CASE WHEN uni_venta.titulo ~* 'KILO*' THEN (poc_pedidos_detalle.reservado * (CASE WHEN inv_prod.densidad=0 THEN 1 ELSE inv_prod.densidad END)) ELSE poc_pedidos_detalle.reservado END) END) ELSE poc_pedidos_detalle.reservado END)) "
           + "else "
                + "((poc_pedidos_detalle.precio_unitario * tipo_cambio ) *  (CASE WHEN uni_prod.id<>uni_venta.id THEN (CASE WHEN uni_venta.titulo ~* 'LITRO*' THEN (poc_pedidos_detalle.reservado / (CASE WHEN inv_prod.densidad=0 THEN 1 ELSE inv_prod.densidad END))ELSE (CASE WHEN uni_venta.titulo ~* 'KILO*' THEN (poc_pedidos_detalle.reservado * (CASE WHEN inv_prod.densidad=0 THEN 1 ELSE inv_prod.densidad END)) ELSE poc_pedidos_detalle.reservado END) END) ELSE poc_pedidos_detalle.reservado END) )  "
           + "end) as importe,"
           + "inv_prod.sku, "
           + "inv_prod.descripcion, "
           + "(CASE WHEN uni_venta.decimales IS NULL THEN 0 ELSE uni_venta.decimales END) AS no_dec "
       + "FROM poc_pedidos_detalle "
       + "join inv_prod on inv_prod.id = poc_pedidos_detalle.inv_prod_id  "
       + "join poc_pedidos on poc_pedidos.id =  poc_pedidos_detalle.poc_pedido_id  "
       + "join cxc_clie on cxc_clie.id = poc_pedidos.cxc_clie_id "
       + "join erp_proceso on erp_proceso.id = poc_pedidos.proceso_id  "
       + "join inv_prod_unidades AS uni_prod on uni_prod.id=inv_prod.unidad_id "
       + "join inv_prod_unidades AS uni_venta on uni_venta.id=poc_pedidos_detalle.inv_prod_unidad_id "
       + "WHERE poc_pedidos.cancelado=false "
       + "AND erp_proceso.proceso_flujo_id IN (2,4,7,8) "
       + "AND erp_proceso.empresa_id= "+id_empresa +" "+cadena_where+" "
       + "AND poc_pedidos_detalle.reservado>0 "
       + "order by inv_prod.descripcion asc, poc_pedidos.id desc ";
       
       //System.out.println("ArticulosReservados: "+ sql_to_query);
       
        ArrayList<HashMap<String, String>> hm = (ArrayList<HashMap<String, String>>) this.jdbcTemplate.query(
                sql_to_query,
            new Object[]{}, new RowMapper() {
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    HashMap<String, String> row = new HashMap<String, String>();
                    row.put("id_pedido",String.valueOf(rs.getInt("id_pedido")));
                    row.put("pedido",rs.getString("pedido"));
                    row.put("fecha",rs.getString("fecha"));
                    row.put("cliente",rs.getString("cliente"));
                    row.put("no_dec",String.valueOf(rs.getInt("no_dec")));
                    row.put("cantidad",StringHelper.roundDouble(rs.getDouble("cantidad"),rs.getInt("no_dec")));
                    row.put("precio_unitario",StringHelper.roundDouble(rs.getDouble("precio_unitario"),4));
                    row.put("importe",StringHelper.roundDouble(rs.getDouble("importe"),4));
                    row.put("sku",rs.getString("sku"));
                    row.put("descripcion",rs.getString("descripcion"));
                    
                    return row;
                }
            }
        );
        return hm;
    }

    @Override
    public ArrayList<HashMap<String, String>> getListaPrecio(Integer lista_precio) {
        String sql_query="SELECT "
                        +"gral_mon_id_pre"+lista_precio+" as moneda_id "
                        + "FROM inv_pre "
                        + " LIMIT 1 ";

        //System.out.println("Resultado de la Moneda de lista: "+sql_query);
        ArrayList<HashMap<String, String>> hm = (ArrayList<HashMap<String, String>>) this.jdbcTemplate.query(
                sql_query,
            new Object[]{}, new RowMapper() {
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    HashMap<String, String> row = new HashMap<String, String>();
                    row.put("moneda_id",String.valueOf(rs.getInt("moneda_id")));
                    return row;
                }
            }
        );
        return hm;
    }

    @Override
    public ArrayList<HashMap<String, Object>> getCotizacion_PaginaGrid(String data_string, int offset, int pageSize, String orderBy, String asc) {
        String sql_to_query="";

        String sql_busqueda = "select id from gral_bus_catalogos(?) as foo (id integer)";

        String cad[]=data_string.split("___");

        if (cad[6].equals("1")){
            sql_to_query = "SELECT DISTINCT "
                    +"poc_cot.id,"
                    +"poc_cot.folio,"
                    + "(CASE WHEN poc_cot.tipo=1 THEN 'CLIENTE' ELSE 'PROSPECTO' END ) as tipo, "
                    +"cxc_clie.razon_social AS cliente,"
                    +"(CASE WHEN poc_cot.fecha IS NULL THEN to_char(poc_cot.momento_creacion,'dd/mm/yyyy') ELSE to_char(poc_cot.fecha::timestamp with time zone,'dd/mm/yyyy') END) AS fecha_creacion,"
                    + "(CASE WHEN poc_cot.fecha IS NULL THEN to_char((to_char(poc_cot.momento_creacion,'yyyy-mm-dd')::date+dias_vigencia)::timestamp with time zone,'dd/mm/yyyy') ELSE to_char((fecha+dias_vigencia)::timestamp with time zone,'dd/mm/yyyy') END ) AS fecha_vencimiento, "
                    + "(CASE WHEN cxc_agen.nombre IS NULL THEN '' ELSE cxc_agen.nombre END ) AS nombre_agente "
            +"FROM poc_cot "
            +"JOIN poc_cot_clie ON poc_cot_clie.poc_cot_id=poc_cot.id  "
            +"JOIN cxc_clie ON cxc_clie.id =  poc_cot_clie.cxc_clie_id  "
            +"LEFT JOIN cxc_agen ON cxc_agen.id=poc_cot.cxc_agen_id  "
            +"JOIN ("+sql_busqueda+") as subt on subt.id=poc_cot.id "
            +"order by "+orderBy+" "+asc+" limit ? OFFSET ?";
        }else{
            if (cad[7].equals("false")){
                sql_to_query = "SELECT DISTINCT "
                        +"poc_cot.id,"
                        +"poc_cot.folio,"
                        + "(CASE WHEN poc_cot.tipo=1 THEN 'CLIENTE' ELSE 'PROSPECTO' END ) as tipo, "
                        +"cxc_clie.razon_social AS cliente,"
                        +"(CASE WHEN poc_cot.fecha IS NULL THEN to_char(poc_cot.momento_creacion,'dd/mm/yyyy') ELSE to_char(poc_cot.fecha::timestamp with time zone,'dd/mm/yyyy') END) AS fecha_creacion, "
                        + "(CASE WHEN poc_cot.fecha IS NULL THEN to_char((to_char(poc_cot.momento_creacion,'yyyy-mm-dd')::date+dias_vigencia)::timestamp with time zone,'dd/mm/yyyy') ELSE to_char((fecha+dias_vigencia)::timestamp with time zone,'dd/mm/yyyy') END ) AS fecha_vencimiento, "
                        + "(CASE WHEN cxc_agen.nombre IS NULL THEN '' ELSE cxc_agen.nombre END ) AS nombre_agente "
                +"FROM poc_cot "
                +"JOIN poc_cot_clie ON poc_cot_clie.poc_cot_id=poc_cot.id  "
                +"JOIN cxc_clie ON cxc_clie.id =  poc_cot_clie.cxc_clie_id  "
                +"LEFT JOIN cxc_agen ON cxc_agen.id=poc_cot.cxc_agen_id  "
                +"JOIN ("+sql_busqueda+") as subt on subt.id=poc_cot.id "
                +"order by "+orderBy+" "+asc+" limit ? OFFSET ?";
            }else{
                sql_to_query = "SELECT DISTINCT "
                        +"poc_cot.id,"
                        +"poc_cot.folio,"
                        + "(CASE WHEN poc_cot.tipo=1 THEN 'CLIENTE' ELSE 'PROSPECTO' END ) as tipo, "
                        +"(CASE WHEN poc_cot.tipo=1 THEN cxc_clie.razon_social ELSE crm_prospectos.razon_social END) AS cliente,"
                        +"(CASE WHEN poc_cot.fecha IS NULL THEN to_char(poc_cot.momento_creacion,'dd/mm/yyyy') ELSE to_char(poc_cot.fecha::timestamp with time zone,'dd/mm/yyyy') END) AS fecha_creacion, "
                        + "(CASE WHEN poc_cot.fecha IS NULL THEN to_char((to_char(poc_cot.momento_creacion,'yyyy-mm-dd')::date+dias_vigencia)::timestamp with time zone,'dd/mm/yyyy') ELSE to_char((fecha+dias_vigencia)::timestamp with time zone,'dd/mm/yyyy') END ) AS fecha_vencimiento, "
                        + "(CASE WHEN cxc_agen.nombre IS NULL THEN '' ELSE cxc_agen.nombre END ) AS nombre_agente "
                +"FROM poc_cot "
                +"LEFT JOIN poc_cot_clie ON poc_cot_clie.poc_cot_id=poc_cot.id  "
                +"LEFT JOIN cxc_clie ON cxc_clie.id=poc_cot_clie.cxc_clie_id  "
                +"LEFT JOIN poc_cot_prospecto ON poc_cot_prospecto.poc_cot_id=poc_cot.id  "
                +"LEFT JOIN crm_prospectos ON crm_prospectos.id=poc_cot_prospecto.crm_prospecto_id "
                +"LEFT JOIN cxc_agen ON cxc_agen.id=poc_cot.cxc_agen_id  "
                +"JOIN ("+sql_busqueda+") as subt on subt.id=poc_cot.id "
                +"order by "+orderBy+" "+asc+" limit ? OFFSET ?";
            }
        }


        //System.out.println("data_string: "+data_string);
        //System.out.println("PaginaGrid: "+sql_to_query);
        ArrayList<HashMap<String, Object>> hm = (ArrayList<HashMap<String, Object>>) this.jdbcTemplate.query(
            sql_to_query,
            new Object[]{new String(data_string),new Integer(pageSize),new Integer(offset)}, new RowMapper() {
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    HashMap<String, Object> row = new HashMap<String, Object>();
                    row.put("id",rs.getInt("id"));
                    row.put("folio",rs.getString("folio"));
                    row.put("tipo",rs.getString("tipo"));
                    row.put("cliente",rs.getString("cliente"));
                    row.put("fecha",rs.getString("fecha_creacion"));
                    row.put("fecha_vencimiento",rs.getString("fecha_vencimiento"));
                    row.put("nombre_agente",rs.getString("nombre_agente"));
                    return row;
                }
            }
        );
        return hm;
    }


    //obtiene datos de la cotizacion
    @Override
    public ArrayList<HashMap<String, String>> getCotizacion_Datos(Integer id) {
        String sql_query = ""
                + "SELECT "
                    + "poc_cot.id, "
                    + "poc_cot.folio, "
                    + "poc_cot.tipo, "
                    + "poc_cot.observaciones, "
                    + "proceso_id, "
                    + "poc_cot.incluye_img_desc, "
                    //+ "(CASE WHEN poc_cot.gral_mon_id=1 THEN 1 ELSE poc_cot.tipo_cambio END) AS tipo_cambio,"
                    + "poc_cot.tipo_cambio,"
                    + "poc_cot.gral_mon_id, "
                    + "gral_mon.descripcion_abr AS moneda_abr,"
                    + "poc_cot.cxc_agen_id, "
                    + "(CASE WHEN poc_cot.fecha IS NULL THEN to_char(poc_cot.momento_creacion,'yyyy-mm-dd') ELSE to_char(poc_cot.fecha::timestamp with time zone,'yyyy-mm-dd') END) AS fecha,"
                    + "(CASE WHEN gral_empleados.nombre_pila IS NULL THEN '' ELSE  gral_empleados.nombre_pila END)||' '||(CASE WHEN gral_empleados.apellido_paterno IS NULL THEN '' ELSE  gral_empleados.apellido_paterno END)||' '||(CASE WHEN gral_empleados.apellido_materno IS NULL THEN '' ELSE  gral_empleados.apellido_materno END) AS nombre_usuario,"
                    + "(CASE WHEN gral_puestos.titulo IS NULL THEN '' ELSE gral_puestos.titulo END) AS puesto_usuario, "
                    + "(CASE WHEN gral_empleados.correo_empresa IS NULL THEN '' ELSE gral_empleados.correo_empresa END) AS correo_agente,"
                    + "poc_cot.subtotal, "
                    + "poc_cot.impuesto,"
                    + "poc_cot.total,"
                    + "poc_cot.incluye_iva,"
                    + "poc_cot.dias_vigencia,"
                    + "(fecha+dias_vigencia) AS fecha_vencimiento,"
                    + "(CASE WHEN (fecha+dias_vigencia)::timestamp with time zone<=now() THEN true ELSE false END) AS vencido, "
                    + "poc_cot.tc_usd "
                + "FROM poc_cot "
                + "LEFT JOIN gral_usr ON gral_usr.id=poc_cot.gral_usr_id_creacion "
                + "LEFT JOIN  gral_empleados ON gral_empleados.id=gral_usr.gral_empleados_id "
                + "LEFT JOIN  gral_puestos ON gral_puestos.id=gral_empleados.gral_puesto_id "
                + "JOIN gral_mon ON gral_mon.id=poc_cot.gral_mon_id"
                + " WHERE poc_cot.id=? ";
        //poc_cot.fecha + poc_cot.dias_vigencia AS fecha_vencimiento
        //System.out.println("getCotizacion: "+sql_query);
        ArrayList<HashMap<String, String>> hm_cotizacion = (ArrayList<HashMap<String, String>>) this.jdbcTemplate.query(
            sql_query,
            new Object[]{new Integer(id)}, new RowMapper() {
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    HashMap<String, String> row = new HashMap<String, String>();
                    row.put("id",String.valueOf(rs.getInt("id")));
                    row.put("tipo",String.valueOf(rs.getInt("tipo")));
                    row.put("moneda_id",String.valueOf(rs.getInt("gral_mon_id")));
                    row.put("monedaAbr",rs.getString("moneda_abr"));
                    row.put("proceso_id",String.valueOf(rs.getInt("proceso_id")));
                    row.put("folio",rs.getString("folio"));
                    row.put("observaciones",rs.getString("observaciones"));
                    row.put("img_desc",String.valueOf(rs.getBoolean("incluye_img_desc")));
                    row.put("tipo_cambio",StringHelper.roundDouble(rs.getString("tipo_cambio"),4));
                    row.put("tc_usd",StringHelper.roundDouble(rs.getString("tc_usd"),4));
                    row.put("fecha",rs.getString("fecha"));
                    row.put("agente_id",String.valueOf(rs.getInt("cxc_agen_id")));
                    row.put("nombre_usuario",rs.getString("nombre_usuario"));
                    row.put("puesto_usuario",rs.getString("puesto_usuario"));
                    row.put("correo_agente",rs.getString("correo_agente"));
                    row.put("subtotal",StringHelper.roundDouble(rs.getString("subtotal"),2));
                    row.put("impuesto",StringHelper.roundDouble(rs.getString("impuesto"),2));
                    row.put("total",StringHelper.roundDouble(rs.getString("total"),2));
                    row.put("dias_vigencia",String.valueOf(rs.getInt("dias_vigencia")));
                    row.put("incluye_iva",String.valueOf(rs.getBoolean("incluye_iva")));
                    
                    row.put("fecha_vencimiento",rs.getString("fecha_vencimiento"));
                    row.put("vencido",String.valueOf(rs.getBoolean("vencido")));
                    return row;
                }
            }
        );
        return hm_cotizacion;
    }
    
    //Obtine datos del cliente
    @Override
    public ArrayList<HashMap<String, String>> getCotizacion_DatosCliente(Integer id) {
        String sql_query = ""
        + "SELECT cxc_clie.id AS cliente_id, "
                + "cxc_clie.rfc, "
                + "cxc_clie.razon_social, "
                + "cxc_clie.moneda, "
                + "cxc_clie.numero_control, "
                + "cxc_clie.contacto, "
                + "cxc_clie.lista_precio,"
                + "cxc_clie.calle||' '||cxc_clie.numero||', '||cxc_clie.colonia||', '||gral_mun.titulo||', '||gral_edo.titulo||', '||gral_pais.titulo||' C.P. '||cxc_clie.cp as direccion, "
                + "cxc_clie.calle,"
                + "cxc_clie.numero,"
                + "cxc_clie.colonia,"
                + "gral_mun.titulo AS municipio,"
                + "gral_edo.titulo AS estado,"
                + "gral_pais.titulo AS pais,"
                + "cxc_clie.cp,"
                + "(CASE WHEN cxc_clie.telefono1='' THEN cxc_clie.telefono2 ELSE cxc_clie.telefono1 END) AS telefono "
            + "FROM poc_cot_clie "
            + "JOIN cxc_clie ON cxc_clie.id=poc_cot_clie.cxc_clie_id "
            + "JOIN gral_pais ON gral_pais.id = cxc_clie.pais_id "
            + "JOIN gral_edo ON gral_edo.id = cxc_clie.estado_id "
            + "JOIN gral_mun ON gral_mun.id = cxc_clie.municipio_id "
            + "WHERE poc_cot_clie.poc_cot_id=?;";
        
        //System.out.println("Obteniendo datos de la cotizacion: "+sql_query);
        ArrayList<HashMap<String, String>> hm = (ArrayList<HashMap<String, String>>) this.jdbcTemplate.query(
            sql_query,
            new Object[]{new Integer(id)}, new RowMapper() {
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    HashMap<String, String> row = new HashMap<String, String>();
                    row.put("cliente_id",String.valueOf(rs.getInt("cliente_id")));
                    row.put("rfc",rs.getString("rfc"));
                    row.put("numero_control",rs.getString("numero_control"));
                    row.put("razon_social",rs.getString("razon_social"));
                    row.put("direccion",rs.getString("direccion"));
                    row.put("contacto",rs.getString("contacto"));
                    row.put("lista_precio",String.valueOf(rs.getInt("lista_precio")));
                    row.put("calle",rs.getString("calle"));
                    row.put("numero",rs.getString("numero"));
                    row.put("colonia",rs.getString("colonia"));
                    row.put("municipio",rs.getString("municipio"));
                    row.put("estado",rs.getString("estado"));
                    row.put("pais",rs.getString("pais"));
                    row.put("cp",rs.getString("cp"));
                    row.put("telefono",rs.getString("telefono"));
                    return row;
                }
            }
        );
        return hm;
    }
    
    
    //obtiene datos del prospecto
    @Override
    public ArrayList<HashMap<String, String>> getCotizacion_DatosProspecto(Integer id) {
        String sql_query = ""
        + "SELECT crm_prospectos.id as prospecto_id, "
                + "crm_prospectos.rfc, "
                + "crm_prospectos.razon_social, "
                + "1::integer AS moneda, "
                + "crm_prospectos.numero_control, "
                + "crm_prospectos.contacto, "
                + "0::integer AS lista_precio,"
                + "crm_prospectos.calle||' '||crm_prospectos.numero||', '||crm_prospectos.colonia||', '||gral_mun.titulo||', '||gral_edo.titulo||', '||gral_pais.titulo||' C.P. '||crm_prospectos.cp as direccion, "
                + "crm_prospectos.calle,"
                + "crm_prospectos.numero,"
                + "crm_prospectos.colonia,"
                + "gral_mun.titulo AS municipio,"
                + "gral_edo.titulo AS estado,"
                + "gral_pais.titulo AS pais,"
                + "crm_prospectos.cp,"
                + "(CASE WHEN crm_prospectos.telefono1='' THEN crm_prospectos.telefono2 ELSE crm_prospectos.telefono1 END) AS telefono "
            + "FROM poc_cot_prospecto "
            + "JOIN crm_prospectos ON crm_prospectos.id=poc_cot_prospecto.crm_prospecto_id "
            + "JOIN gral_pais ON gral_pais.id = crm_prospectos.pais_id "
            + "JOIN gral_edo ON gral_edo.id = crm_prospectos.estado_id "
            + "JOIN gral_mun ON gral_mun.id = crm_prospectos.municipio_id "
            + "WHERE poc_cot_prospecto.poc_cot_id=?;";
        
        //System.out.println("Obteniendo datos de la cotizacion: "+sql_query);
        ArrayList<HashMap<String, String>> hm = (ArrayList<HashMap<String, String>>) this.jdbcTemplate.query(
            sql_query,
            new Object[]{new Integer(id)}, new RowMapper() {
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    HashMap<String, String> row = new HashMap<String, String>();
                    row.put("cliente_id",String.valueOf(rs.getInt("prospecto_id")));
                    row.put("rfc",rs.getString("rfc"));
                    row.put("numero_control",rs.getString("numero_control"));
                    row.put("razon_social",rs.getString("razon_social"));
                    row.put("direccion",rs.getString("direccion"));
                    row.put("contacto",rs.getString("contacto"));
                    row.put("lista_precio",String.valueOf(rs.getInt("lista_precio")));
                    row.put("calle",rs.getString("calle"));
                    row.put("numero",rs.getString("numero"));
                    row.put("colonia",rs.getString("colonia"));
                    row.put("municipio",rs.getString("municipio"));
                    row.put("estado",rs.getString("estado"));
                    row.put("pais",rs.getString("pais"));
                    row.put("cp",rs.getString("cp"));
                    row.put("telefono",rs.getString("telefono"));
                    
                    return row;
                }
            }
        );
        return hm;
    }
    


    @Override
    public ArrayList<HashMap<String, String>> getCotizacion_DatosGrid(Integer id) {
        String sql_query = ""
        + "SELECT  "
            + "poc_cot_detalle.id as id_detalle, "
            + "poc_cot_detalle.inv_prod_id as producto_id, "
            + "inv_prod.sku as codigo, "
            + "inv_prod.descripcion as producto, "
            + "(CASE WHEN inv_prod.descripcion_larga IS NULL THEN '' ELSE inv_prod.descripcion_larga END) AS descripcion_larga, "
            + "(CASE WHEN inv_prod.archivo_img='' THEN '' ELSE inv_prod.archivo_img END) AS archivo_img, "
            + "(CASE WHEN poc_cot_detalle.inv_prod_unidad_id=0 THEN inv_prod_unidades.id ELSE poc_cot_detalle.inv_prod_unidad_id END ) AS unidad_id, "
            + "inv_prod_unidades.titulo as unidad, "
            + "inv_prod_presentaciones.id as presentacion_id, "
            + "inv_prod_presentaciones.titulo as presentacion, "
            + "poc_cot_detalle.cantidad, "
            + "poc_cot_detalle.precio_unitario, "
            + "poc_cot_detalle.gral_mon_id as moneda_id, "
            + "gral_mon.descripcion_abr AS moneda_abr, "
            + "(poc_cot_detalle.cantidad * poc_cot_detalle.precio_unitario) AS importe,"
            + "poc_cot_detalle.gral_impto_id as id_imp,"
            + "poc_cot_detalle.valor_imp,"
            + "(case when poc_cot_detalle.autorizado=true then 1 else 0 end) as status_aut,"
            + "poc_cot_detalle.precio_aut,"
            + "poc_cot_detalle.gral_usr_id_aut,"
            + "poc_cot_detalle.requiere_aut "
        + "FROM poc_cot_detalle  "
        + "LEFT JOIN inv_prod on inv_prod.id = poc_cot_detalle.inv_prod_id  "
        + "LEFT JOIN inv_prod_unidades on inv_prod_unidades.id = poc_cot_detalle.inv_prod_unidad_id  "
        + "LEFT JOIN inv_prod_presentaciones on inv_prod_presentaciones.id = poc_cot_detalle.inv_presentacion_id  "
        + "LEFT JOIN gral_mon on gral_mon.id = poc_cot_detalle.gral_mon_id  "
        + "WHERE poc_cot_detalle.poc_cot_id= ? "
        + "ORDER BY poc_cot_detalle.id";
        
        //System.out.println("sql_query: "+sql_query);
        
        ArrayList<HashMap<String, String>> hm_grid = (ArrayList<HashMap<String, String>>) this.jdbcTemplate.query(
            sql_query,
            new Object[]{new Integer(id)}, new RowMapper() {
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    HashMap<String, String> row = new HashMap<String, String>();
                    row.put("id_detalle",String.valueOf(rs.getInt("id_detalle")));
                    row.put("producto_id",String.valueOf(rs.getInt("producto_id")));
                    row.put("codigo",rs.getString("codigo"));
                    row.put("producto",rs.getString("producto"));
                    row.put("descripcion_larga",rs.getString("descripcion_larga"));
                    row.put("archivo_img",rs.getString("archivo_img"));
                    row.put("unidad_id",String.valueOf(rs.getInt("unidad_id")));
                    row.put("unidad",rs.getString("unidad"));
                    row.put("presentacion_id",rs.getString("presentacion_id"));
                    row.put("presentacion",rs.getString("presentacion"));
                    row.put("cantidad",StringHelper.roundDouble(rs.getString("cantidad"),2));
                    row.put("precio_unitario",StringHelper.roundDouble(rs.getDouble("precio_unitario"),4));
                    row.put("moneda_id",String.valueOf(rs.getInt("moneda_id")));
                    row.put("moneda_abr",rs.getString("moneda_abr"));
                    row.put("importe",StringHelper.roundDouble(rs.getDouble("importe"),4));
                    row.put("id_imp",String.valueOf(rs.getInt("id_imp")));
                    row.put("valor_imp",StringHelper.roundDouble(rs.getDouble("valor_imp"),4));
                    
                    row.put("status_aut",String.valueOf(rs.getInt("status_aut"))+"&&&"+StringHelper.roundDouble(rs.getDouble("precio_aut"),4)+"&&&"+Base64Coder.encodeString(String.valueOf(rs.getInt("gral_usr_id_aut"))));
                    row.put("requiere_aut",String.valueOf(rs.getBoolean("requiere_aut")));
                    return row;
                }
            }
        );
        return hm_grid;
    }


    //obtiene las condiciones comerciales que se mostraran en el pdf de la cotizacion
    @Override
    public ArrayList<HashMap<String, String>> getCotizacion_CondicionesComerciales(Integer id_emp) {
        String sql_query="SELECT id, descripcion FROM poc_cot_condiciones_com WHERE gral_emp_id="+id_emp+" AND borrado_logico=false ORDER BY id;";

        //System.out.println("getCondicionesComerciales: "+sql_query);
        ArrayList<HashMap<String, String>> hm = (ArrayList<HashMap<String, String>>) this.jdbcTemplate.query(
                sql_query,
            new Object[]{}, new RowMapper() {
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    HashMap<String, String> row = new HashMap<String, String>();
                    row.put("id",String.valueOf(rs.getInt("id")));
                    row.put("descripcion",rs.getString("descripcion"));
                    return row;
                }
            }
        );
        return hm;
    }


    //obtiene las politicas de pago para la cotizacion
    @Override
    public ArrayList<HashMap<String, String>> getCotizacion_PolitizasPago(Integer id_emp) {
        String sql_query="SELECT id, descripcion FROM poc_cot_politicas_pago WHERE gral_emp_id="+id_emp+" AND borrado_logico=false ORDER BY id;";

        //System.out.println("getPoliticasPago: "+sql_query);
        ArrayList<HashMap<String, String>> hm = (ArrayList<HashMap<String, String>>) this.jdbcTemplate.query(
                sql_query,
            new Object[]{}, new RowMapper() {
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    HashMap<String, String> row = new HashMap<String, String>();
                    row.put("id",String.valueOf(rs.getInt("id")));
                    row.put("descripcion",rs.getString("descripcion"));
                    return row;
                }
            }
        );
        return hm;
    }



    //obtiene los INCOTERMS para mostrarlas a la cotizacion
    //los option del select se forman desde el query
    @Override
    public ArrayList<HashMap<String, String>> getCotizacion_Incoterms(Integer id_emp, Integer id_cot) {
        String sql_query=""
                + "SELECT "
                    + "id,"
                    + "titulo,"
                    + "(CASE WHEN cot_id IS NOT NULL THEN true ELSE false END )AS mostrar_pdf, "
                    + "(CASE WHEN cot_id IS NOT NULL THEN '<option value=\"'||id||'\" selected=\"yes\">'||titulo||'</option>' ELSE '<option value=\"'||id||'\">'||titulo||'</option>' END )AS opcion_select "
                + "FROM("
                    + "SELECT "
                        + "incoterms.id,"
                        + "(CASE WHEN incoterms.nombre IS NULL THEN '' ELSE incoterms.nombre END)||' - '||(CASE WHEN incoterms.descripcion_esp IS NULL THEN '' ELSE incoterms.descripcion_esp END) AS titulo,"
                        + "incotermxcot.poc_cot_id AS cot_id "
                    + "FROM poc_cot_incoterms AS incoterms "
                    + "LEFT JOIN poc_cot_incoterm_x_cot AS incotermxcot ON (incotermxcot.poc_cot_incoterms_id=incoterms.id AND incotermxcot.poc_cot_id="+id_cot+") "
                    + "WHERE incoterms.borrado_logico=FALSE AND incoterms.gral_emp_id="+id_emp+""
                + ") AS sbt;";

        //System.out.println("getIncoterms: "+sql_query);
        ArrayList<HashMap<String, String>> hm = (ArrayList<HashMap<String, String>>) this.jdbcTemplate.query(
                sql_query,
            new Object[]{}, new RowMapper() {
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    HashMap<String, String> row = new HashMap<String, String>();
                    row.put("id",String.valueOf(rs.getInt("id")));
                    row.put("titulo",rs.getString("titulo"));
                    row.put("mostrar_pdf",String.valueOf(rs.getBoolean("mostrar_pdf")));
                    row.put("opcion_select",rs.getString("opcion_select"));
                    return row;
                }
            }
        );
        return hm;
    }



    @Override
    public HashMap<String, String> getUserRol(Integer id_user) {
        HashMap<String, String> data = new HashMap<String, String>();

        //verificar si el usuario tiene  rol de ADMINISTTRADOR
        //si exis es mayor que cero, el usuario si es ADMINISTRADOR
        String sql_to_query = "SELECT count(gral_usr_id) AS exis_rol_admin FROM gral_usr_rol WHERE gral_usr_id="+id_user+" AND gral_rol_id=1;";

        Map<String, Object> map = this.getJdbcTemplate().queryForMap(sql_to_query);

        data.put("exis_rol_admin",map.get("exis_rol_admin").toString().toUpperCase());

        return data;
    }


    //obtener SALUDO
    @Override
    public HashMap<String, String> getCotizacion_Saludo(Integer id_empresa) {
        HashMap<String, String> retorno = new HashMap<String, String>();
        String sql_busqueda = "SELECT count(id) FROM poc_cot_saludo_despedida WHERE tipo='SALUDO' AND status=true AND gral_emp_id="+id_empresa;

        int rowCount = this.getJdbcTemplate().queryForInt(sql_busqueda);

        if(rowCount > 0){
            String sql_to_query = "SELECT titulo FROM poc_cot_saludo_despedida WHERE tipo='SALUDO' AND status=true AND gral_emp_id="+id_empresa;
            HashMap<String, String> hm = (HashMap<String, String>) this.jdbcTemplate.queryForObject(
                sql_to_query,
                new Object[]{}, new RowMapper() {
                    @Override
                    public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                        HashMap<String, String> row = new HashMap<String, String>();
                        row.put("saludo",rs.getString("titulo"));
                        return row;
                    }
                }
            );

            retorno=hm;
        }else{
            retorno.put("saludo", "");
        }

        return retorno;
    }

    //obtener DESPEDIDA
    @Override
    public HashMap<String, String> getCotizacion_Despedida(Integer id_empresa) {
        HashMap<String, String> retorno = new HashMap<String, String>();
        String sql_busqueda = "SELECT count(id) FROM poc_cot_saludo_despedida WHERE tipo='DESPEDIDA' AND status=true AND gral_emp_id="+id_empresa;

        int rowCount = this.getJdbcTemplate().queryForInt(sql_busqueda);

        if(rowCount > 0){
            String sql_to_query = "SELECT titulo FROM poc_cot_saludo_despedida WHERE tipo='DESPEDIDA' AND status=true AND gral_emp_id="+id_empresa;
            HashMap<String, String> hm = (HashMap<String, String>) this.jdbcTemplate.queryForObject(
                sql_to_query,
                new Object[]{}, new RowMapper() {
                    @Override
                    public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                        HashMap<String, String> row = new HashMap<String, String>();
                        row.put("despedida",rs.getString("titulo"));
                        return row;
                    }
                }
            );

            retorno=hm;
        }else{
            retorno.put("despedida", "");
        }

        return retorno;
    }




    //metodos para Actualizador de Saludo y Despedida para Cotizaciones-----------------------------------------------------------------
    @Override
    public ArrayList<HashMap<String, Object>> getCotizacionSaludoDespedida_PaginaGrid(String data_string, int offset, int pageSize, String orderBy, String asc) {
        String sql_busqueda = "select id from gral_bus_catalogos(?) as foo (id integer)";
	String sql_to_query = ""
                + "SELECT "
                    + "poc_cot_saludo_despedida.id, "
                    + "poc_cot_saludo_despedida.tipo, "
                    + "poc_cot_saludo_despedida.titulo, "
                    + "(CASE WHEN status=TRUE THEN 'Activo' ELSE 'Inactivo' END ) AS status "
                + "FROM poc_cot_saludo_despedida "
                + "JOIN ("+sql_busqueda+") as subt on subt.id=poc_cot_saludo_despedida.id "
                + "order by "+orderBy+" "+asc+" limit ? OFFSET ?";

        //System.out.println("data_string: "+data_string);
        ArrayList<HashMap<String, Object>> hm = (ArrayList<HashMap<String, Object>>) this.jdbcTemplate.query(
            sql_to_query,
            new Object[]{new String(data_string),new Integer(pageSize),new Integer(offset)}, new RowMapper() {
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    HashMap<String, Object> row = new HashMap<String, Object>();
                    row.put("id",rs.getInt("id"));
                    row.put("tipo",rs.getString("tipo"));
                    row.put("titulo",rs.getString("titulo"));
                    row.put("status",rs.getString("status"));
                    return row;
                }
            }
        );
        return hm;
    }



    //obtiene datos del saludo ó despedida
    @Override
    public ArrayList<HashMap<String, String>> getCotizacionSaludoDespedida_Datos(Integer id) {
        String sql_query = ""
                + "SELECT "
                    + "id, "
                    + "tipo, "
                    + "titulo,"
                    + "(CASE WHEN status=TRUE THEN 1 ELSE 2 END ) AS status "
                + "FROM poc_cot_saludo_despedida "
                + " WHERE poc_cot_saludo_despedida.id=? ";

        ArrayList<HashMap<String, String>> hm = (ArrayList<HashMap<String, String>>) this.jdbcTemplate.query(
            sql_query,
            new Object[]{new Integer(id)}, new RowMapper() {
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    HashMap<String, String> row = new HashMap<String, String>();
                    row.put("id",String.valueOf(rs.getInt("id")));
                    row.put("tipo",rs.getString("tipo"));
                    row.put("titulo",rs.getString("titulo"));
                    row.put("status",String.valueOf(rs.getInt("status")));
                    return row;
                }
            }
        );
        return hm;
    }


    //metodos para Catalogo de Incoterms-----------------------------------------------------------------
    @Override
    public ArrayList<HashMap<String, Object>> getCotIncoterms_PaginaGrid(String data_string, int offset, int pageSize, String orderBy, String asc) {
        String sql_busqueda = "select id from gral_bus_catalogos(?) as foo (id integer)";
	String sql_to_query = ""
                + "SELECT "
                    + "poc_cot_incoterms.id, "
                    + "poc_cot_incoterms.nombre, "
                    + "poc_cot_incoterms.descripcion_esp AS descripcion "
                + "FROM poc_cot_incoterms "
                + "JOIN ("+sql_busqueda+") as subt on subt.id=poc_cot_incoterms.id "
                + "order by "+orderBy+" "+asc+" limit ? OFFSET ?";

        //System.out.println("data_string: "+data_string);
        ArrayList<HashMap<String, Object>> hm = (ArrayList<HashMap<String, Object>>) this.jdbcTemplate.query(
            sql_to_query,
            new Object[]{new String(data_string),new Integer(pageSize),new Integer(offset)}, new RowMapper() {
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    HashMap<String, Object> row = new HashMap<String, Object>();
                    row.put("id",rs.getInt("id"));
                    row.put("nombre",rs.getString("nombre"));
                    row.put("descripcion",rs.getString("descripcion"));
                    return row;
                }
            }
        );
        return hm;
    }



    //obtiene datos de un Incoterm
    @Override
    public ArrayList<HashMap<String, String>> getCotIncoterms_Datos(Integer id) {
        String sql_query = "SELECT id,nombre,descripcion_esp,descripcion_ing FROM poc_cot_incoterms WHERE id=?;";

        ArrayList<HashMap<String, String>> hm = (ArrayList<HashMap<String, String>>) this.jdbcTemplate.query(
            sql_query,
            new Object[]{new Integer(id)}, new RowMapper() {
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    HashMap<String, String> row = new HashMap<String, String>();
                    row.put("id",String.valueOf(rs.getInt("id")));
                    row.put("nombre",rs.getString("nombre"));
                    row.put("descripcion_esp",rs.getString("descripcion_esp"));
                    row.put("descripcion_ing",rs.getString("descripcion_ing"));
                    return row;
                }
            }
        );
        return hm;
    }



//Grid de politicas de Pago
    @Override
    public ArrayList<HashMap<String, Object>> getCotPoliticas_de_Pago_PaginaGrid(String data_string, int offset, int pageSize, String orderBy, String asc) {
        String sql_busqueda = "select id from gral_bus_catalogos(?) as foo (id integer)";
	String sql_to_query = ""
                + "SELECT "
                    + "poc_cot_politicas_pago.id, "
                    + "poc_cot_politicas_pago.descripcion, "
                    + "poc_cot_politicas_pago.borrado_logico, "
                    + "poc_cot_politicas_pago.gral_emp_id, "
                    + "poc_cot_politicas_pago.gral_suc_id "

                + "FROM poc_cot_politicas_pago "
                + "JOIN ("+sql_busqueda+") as subt on subt.id=poc_cot_politicas_pago.id "
                + "order by "+orderBy+" "+asc+" limit ? OFFSET ?";

        //System.out.println("sql Busqueda : "+sql_busqueda);
        //System.out.println("Query para el Grid : "+sql_to_query);
        ArrayList<HashMap<String, Object>> hm = (ArrayList<HashMap<String, Object>>) this.jdbcTemplate.query(
            sql_to_query,
            new Object[]{new String(data_string),new Integer(pageSize),new Integer(offset)}, new RowMapper() {
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    HashMap<String, Object> row = new HashMap<String, Object>();
                    row.put("id",rs.getInt("id"));
                    row.put("descripcion",rs.getString("descripcion"));
                    row.put("borrado_logico",rs.getString("borrado_logico"));
                    row.put("empresa_id",rs.getString("gral_emp_id"));
                    row.put("sucursal_id",rs.getString("gral_suc_id"));

                    return row;
                }
            }
        );
        return hm;
    }
     //obtiene las politicas de pago.
    @Override
    public ArrayList<HashMap<String, String>> getCotPoliticas_de_Pago_Datos(Integer id) {
        String sql_query = "SELECT id,descripcion,borrado_logico,gral_emp_id,gral_suc_id FROM poc_cot_politicas_pago WHERE id=?;";

        ArrayList<HashMap<String, String>> hm = (ArrayList<HashMap<String, String>>) this.jdbcTemplate.query(
            sql_query,
            new Object[]{new Integer(id)}, new RowMapper() {
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    HashMap<String, String> row = new HashMap<String, String>();
                    row.put("id",String.valueOf(rs.getInt("id")));
                    row.put("descripcion",rs.getString("descripcion"));
                    row.put("borrado_logico",rs.getString("borrado_logico"));
                    row.put("empresa_id",rs.getString("gral_emp_id"));
                    row.put("sucursal_id",rs.getString("gral_suc_id"));
                    return row;
                }
            }
        );
        return hm;
    }

//////////////////////////7

//Grid de Condiciones de Venta
    @Override
    public ArrayList<HashMap<String, Object>> getCotCondiciones_comerciales_PaginaGrid(String data_string, int offset, int pageSize, String orderBy, String asc) {
        String sql_busqueda = "select id from gral_bus_catalogos(?) as foo (id integer)";
	String sql_to_query = ""
                + "SELECT "
                    + "poc_cot_condiciones_com.id, "
                    + "poc_cot_condiciones_com.descripcion, "
                    + "poc_cot_condiciones_com.borrado_logico, "
                    + "poc_cot_condiciones_com.gral_emp_id, "
                    + "poc_cot_condiciones_com.gral_suc_id "

                + "FROM poc_cot_condiciones_com "
                + "JOIN ("+sql_busqueda+") as subt on subt.id=poc_cot_condiciones_com.id "
                + "order by "+orderBy+" "+asc+" limit ? OFFSET ?";

        //System.out.println("sql Busqueda  data string: "+data_string);
        //System.out.println("Query para el Grid : "+sql_to_query);
        ArrayList<HashMap<String, Object>> hm = (ArrayList<HashMap<String, Object>>) this.jdbcTemplate.query(
            sql_to_query,
            new Object[]{new String(data_string),new Integer(pageSize),new Integer(offset)}, new RowMapper() {
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    HashMap<String, Object> row = new HashMap<String, Object>();
                    row.put("id",rs.getInt("id"));
                    row.put("descripcion",rs.getString("descripcion"));
                    row.put("borrado_logico",rs.getString("borrado_logico"));
                    row.put("empresa_id",rs.getString("gral_emp_id"));
                    row.put("sucursal_id",rs.getString("gral_suc_id"));

                    return row;
                }
            }
        );
        return hm;
    }
     //obtiene las condiciones de Venta.
    @Override
    public ArrayList<HashMap<String, String>> getCotCondiciones_Comerciales_Datos(Integer id) {
        String sql_query = "SELECT id,descripcion,borrado_logico,gral_emp_id,gral_suc_id FROM poc_cot_condiciones_com WHERE id=?;";

        ArrayList<HashMap<String, String>> hm = (ArrayList<HashMap<String, String>>) this.jdbcTemplate.query(
            sql_query,
            new Object[]{new Integer(id)}, new RowMapper() {
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    HashMap<String, String> row = new HashMap<String, String>();
                    row.put("id",String.valueOf(rs.getInt("id")));
                    row.put("descripcion",rs.getString("descripcion"));
                    row.put("borrado_logico",rs.getString("borrado_logico"));
                    row.put("empresa_id",rs.getString("gral_emp_id"));
                    row.put("sucursal_id",rs.getString("gral_suc_id"));
                    return row;
                }
            }
        );
        return hm;
    }
    
    
    
     //Obtiene las unidades de medida de los productos
    @Override
    public ArrayList<HashMap<String, String>> getUnidadesMedida() {
        String sql_query = "SELECT id,titulo,titulo_abr,decimales FROM inv_prod_unidades WHERE borrado_logico=FALSE;";
        
        ArrayList<HashMap<String, String>> hm = (ArrayList<HashMap<String, String>>) this.jdbcTemplate.query(
            sql_query,
            new Object[]{}, new RowMapper() {
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    HashMap<String, String> row = new HashMap<String, String>();
                    row.put("id",String.valueOf(rs.getInt("id")));
                    row.put("titulo",rs.getString("titulo"));
                    row.put("titulo_abr",rs.getString("titulo_abr"));
                    row.put("decimales",String.valueOf(rs.getInt("decimales")));
                    return row;
                }
            }
        );
        return hm;
    }
    
    
    
    //Obtiene todos los impuestos del ieps(Impuesto Especial sobre Productos y Servicios)
    @Override
    public ArrayList<HashMap<String, String>> getIeps(Integer idEmp, Integer idSuc) {
        String sql_to_query="";
        if(idSuc>0){
            //Filtrar por sucursal
            sql_to_query = "SELECT id, titulo, tasa FROM gral_ieps  WHERE borrado_logico=false AND gral_emp_id="+idEmp+" AND gral_suc_id="+idSuc+";";
        }else{
            //No filtrar por sucursal
            sql_to_query = "SELECT id, titulo, tasa FROM gral_ieps  WHERE borrado_logico=false AND gral_emp_id="+idEmp+";";
        }
        
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
    
    
    
    //Valida el usuario para autorizacion de precios
    @Override
    public HashMap<String, Object> getValidarUser(String username, String password, String id_suc) {
        HashMap<String, Object> data = new HashMap<String, Object>();
        
        //Verificar si el usuario tiene  rol de ROLE_AUTH_PRECIO
        String sql_to_query = "select count(gral_usr.id) as exis from gral_usr join gral_usr_rol on gral_usr_rol.gral_usr_id=gral_usr.id join gral_rol on (gral_rol.id=gral_usr_rol.gral_rol_id and gral_rol.authority='ROLE_AUTH_PRECIO') where gral_usr.username='"+username+"' and gral_usr.password='"+password+"' and gral_usr.enabled=true;";
        Map<String, Object> map = this.getJdbcTemplate().queryForMap(sql_to_query);
        
        if(Integer.parseInt(String.valueOf(map.get("exis")))>0){
            sql_to_query = "select gral_usr.id as ident from gral_usr where gral_usr.username='"+username+"' and gral_usr.password='"+password+"' and gral_usr.enabled=true limit 1;";
            Map<String, Object> map2 = this.getJdbcTemplate().queryForMap(sql_to_query);
            data.put("ident",Base64Coder.encodeString(map2.get("ident").toString()));
            data.put("success","true");
        }else{
            data.put("ident","");
            data.put("success","false");
        }
        
        return data;
    }
    
    
    //Verificar si la cotizacion no tiene pendiente autorizacion de precios
    @Override
    public HashMap<String, Object> getVerificarCotizacion(String folio_cotizacion, Integer id_suc) {
        HashMap<String, Object> data = new HashMap<String, Object>();
        
        //Verificar si el usuario tiene  rol de ROLE_AUTH_PRECIO
        String sql_to_query = "select count(poc_cot.id) as exis from poc_cot join erp_proceso on (erp_proceso.id=poc_cot.proceso_id and erp_proceso.sucursal_id="+id_suc+") where poc_cot.folio='"+folio_cotizacion+"';";
        Map<String, Object> map = this.getJdbcTemplate().queryForMap(sql_to_query);
        
        if(Integer.parseInt(String.valueOf(map.get("exis")))>0){
            sql_to_query = "select count(poc_cot_detalle.id) as count from poc_cot join erp_proceso on (erp_proceso.id=poc_cot.proceso_id and erp_proceso.sucursal_id="+id_suc+") join poc_cot_detalle on (poc_cot_detalle.poc_cot_id=poc_cot.id and poc_cot_detalle.requiere_aut=true and poc_cot_detalle.autorizado=false) where poc_cot.folio='"+folio_cotizacion+"';";
            Map<String, Object> map2 = this.getJdbcTemplate().queryForMap(sql_to_query);
            
            if(Integer.parseInt(String.valueOf(map2.get("count")))>0){
                data.put("success","false");
                data.put("msj","La cotizaci&oacute;n tiene partidas con precios pendientes de autorizar.<br>Es necesario regresar a la cotizaci&oacute;n y pedir la autorizaci&oacute;n de precios.");
            }else{
                data.put("success","true");
                data.put("msj","");
            }
            data.put("exis_cot","true");
        }else{
            data.put("exis_cot","false");
            data.put("success","false");
            data.put("msj","La cotizaci&oacute;n no existe.");
        }
        
        return data;
    }
    
    //NLE: Insertar/Actualizar Registro Remisión IMSS
    @Override
    public HashMap<String, String> setRemisionIMSS(String data_string, int app_selected) {
        int row=0;
        HashMap<String, String> success = new HashMap<String, String>();
        String strSql = "";
        
        try{
            String param[] = data_string.split("___");
            String id = param[0]!=null?param[0].trim():"";
            
            if(id.isEmpty()){
                //Insertar
                StringHelper sh = new StringHelper();
                Date fecha1 = sh.parseDate(param[5]);
                Date fecha2 = sh.parseDate(param[6]);
                strSql = "INSERT INTO erp_remisiones_imss (numero_contrato, folio_imss, cliente, importe, fecha_expedicion, fecha_pago, id_status, doc1, doc2, doc3, doc4, doc5, doc6, doc7, doc8, doc9, doc10, usuario_id, momento_actualizacion) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, now());";
                System.out.println("strSql: "+strSql);

                // define query arguments
                Object[] params = new Object[] { new String(param[1]), new String(param[2]), new String(param[3]), new Double(param[4]), fecha1, fecha2, new Integer(param[7]), new String(param[8]), new String(param[9]), new String(param[10]), new String(param[11]), new String(param[12]), new String(param[13]), new String(param[14]), new String(param[15]), new String(param[16]), new String(param[17]), new Integer(param[18]) };

                // define SQL types of the arguments
                int[] types = new int[] { Types.VARCHAR, Types.VARCHAR, Types.VARCHAR, Types.DOUBLE, Types.DATE, Types.DATE, Types.INTEGER, Types.VARCHAR, Types.VARCHAR, Types.VARCHAR, Types.VARCHAR, Types.VARCHAR, Types.VARCHAR, Types.VARCHAR, Types.VARCHAR, Types.VARCHAR, Types.VARCHAR, Types.INTEGER };

                // execute insert query to insert the data
                // return number of row / rows processed by the executed query
                row = this.getJdbcTemplate().update(strSql, params, types);
                
                success.put("sucess", "true");
                
            }else if(!id.isEmpty()){
                
                strSql = "UPDATE erp_remisiones_imss SET numero_contrato=?, folio_imss=?, cliente=?, importe=?, fecha_expedicion=?, fecha_pago=?, id_status=?, doc1=?, doc2=?, doc3=?, doc4=?, doc5=?, doc6=?, doc7=?, doc8=?, doc9=?, doc10=?, usuario_id=?, momento_actualizacion=now() WHERE id=?;";
                System.out.println("strSql: "+strSql);
                
                // define query arguments
                Object[] params = new Object[] { new String(param[1]), new String(param[2]), new String(param[3]), new Double(param[4]), StringHelper.parseDate(param[5]), StringHelper.parseDate(param[6]), new Integer(param[7]), new String(param[8]), new String(param[9]), new String(param[10]), new String(param[11]), new String(param[12]), new String(param[13]), new String(param[14]), new String(param[15]), new String(param[16]), new String(param[17]), new Integer(param[18]), new Integer(param[0]) };

                // define SQL types of the arguments
                int[] types = new int[] { Types.VARCHAR, Types.VARCHAR, Types.VARCHAR, Types.DOUBLE, Types.DATE, Types.DATE, Types.INTEGER, Types.VARCHAR, Types.VARCHAR, Types.VARCHAR, Types.VARCHAR, Types.VARCHAR, Types.VARCHAR, Types.VARCHAR, Types.VARCHAR, Types.VARCHAR, Types.VARCHAR, Types.INTEGER, Types.INTEGER };

                // execute insert query to insert the data
                // return number of row / rows processed by the executed query
                row = this.getJdbcTemplate().update(strSql, params, types);
                
                success.put("sucess", "true");
            }
            
            //System.out.println(row + " row inserted.");
        } catch (Exception e) {
            System.out.println("ERROR: "+e.getMessage());
            row=0;
            success.put("sucess", "false");
        }
        
        return success;
    }

    @Override
    public HashMap<String, String> logicDeleteRemisionIMSS(String data_string, int app_selected) {
        int row=0;
        HashMap<String, String> success = new HashMap<String, String>();
        String strSql = "";
        
        try{
            String param[] = data_string.split("___");
            String id = param[0]!=null?param[0].trim():"";
            
            if(!id.isEmpty()){
                System.out.println("=====Entrando a Borrado Lógico=====");
                strSql = "UPDATE erp_remisiones_imss SET borrado_logico=true WHERE id=?;";
                System.out.println("Borrado Lógico de Contra Recibo IMMS id="+param[3]);
                System.out.println("strSql: "+strSql);
                
                // define query arguments
                Object[] params = new Object[] { new Integer(param[3]) };

                // define SQL types of the arguments
                int[] types = new int[] { Types.INTEGER };

                // execute insert query to insert the data
                // return number of row / rows processed by the executed query
                row = this.getJdbcTemplate().update(strSql, params, types);
                
                success.put("sucess", "true");
            }
            
            //System.out.println(row + " row inserted.");
        } catch (Exception e) {
            System.out.println("ERROR: "+e.getMessage());
            row=0;
            success.put("sucess", "false");
        }
        
        return success;
    }

    //NLE: Obtiene los Estatus de Remisiones de IMSS
    @Override
    public ArrayList<HashMap<String, String>> getStatusRemisionIMSS() {
	String sql_query = "SELECT id, descripcion FROM erp_status_remisiones_imss order by id;";
        ArrayList<HashMap<String, String>> hm = (ArrayList<HashMap<String, String>>) this.jdbcTemplate.query(
            sql_query,
            new Object[]{}, new RowMapper() {
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    HashMap<String, String> row = new HashMap<String, String>();
                    row.put("id",rs.getString("id"));
                    row.put("descripcion",rs.getString("descripcion"));
                    return row;
                }
            }
        );
        return hm;
    }

    @Override
    public ArrayList<HashMap<String, Object>> getRemisionesIMSS_PaginaGrid(String data_string, int offset, int pageSize, String orderBy, String asc) {
        
        System.out.println("Entrando a getCotizacion_PaginaGrid...");
        System.out.println("data_string="+data_string);
        System.out.println("offset="+offset);
        System.out.println("pageSize="+pageSize);
        System.out.println("orderBy="+orderBy);
        System.out.println("asc="+asc);   
        String cad[]=data_string.split("___");
        
        System.out.println("cad[].length"+cad.length);
        for (int i=0;i<cad.length;i++) {
            System.out.println("cad["+i+"]="+cad[i]);
            cad[i]=cad[i].replace("'","");
        }

        System.out.println("app_selected    cad[0]="+cad[0]);
        System.out.println("id_usuario      cad[1]="+cad[1]);
        System.out.println("folio           cad[2]="+cad[2]);
        System.out.println("cliente         cad[3]="+cad[3]);
        System.out.println("fecha_inicial   cad[4]="+cad[4]);
        System.out.println("fecha_final     cad[5]="+cad[5]);
        System.out.println("tipo            cad[6]="+cad[6]);
        System.out.println("incluye_crm     cad[7]="+cad[7]);
        System.out.println("folioIMSS       cad[8]="+cad[8]);
        System.out.println("numContrato     cad[9]="+cad[9]);
        System.out.println("status          cad[10]="+cad[10]);
        //System.out.println("cad[10]="+cad[10]);
        
        String sql_to_query="";
        //String sql_busqueda = "select id from gral_bus_catalogos(?) as foo (id integer)";
        String sql_busqueda = "";
        sql_busqueda += cad[2].isEmpty()?"":"AND erp_remisiones_imss.id::character varying ILIKE '%"+cad[2]+"%' ";            
        sql_busqueda += cad[3].isEmpty()?"":"AND erp_remisiones_imss.cliente ILIKE '%"+cad[3]+"%' ";
        sql_busqueda += cad[4].isEmpty()?"":"AND erp_remisiones_imss.fecha_expedicion::date = '"+cad[4]+"' ";
        sql_busqueda += cad[5].isEmpty()?"":"AND erp_remisiones_imss.fecha_pago::date = '"+cad[5]+"' ";
        sql_busqueda += cad[8].isEmpty()?"":"AND erp_remisiones_imss.folio_imss ILIKE '%"+cad[8]+"%' ";
        sql_busqueda += cad[9].isEmpty()?"":"AND erp_remisiones_imss.numero_contrato ILIKE '%"+cad[9]+"%' ";
        sql_busqueda += cad[10].isEmpty()?"":"AND erp_status_remisiones_imss.id::character varying ILIKE '%"+cad[10]+"%' ";
        System.out.println("sql_busqueda="+sql_busqueda);

        if (cad[6].equals("1")){
            sql_to_query = "SELECT DISTINCT erp_remisiones_imss.id, erp_remisiones_imss.cliente, erp_remisiones_imss.fecha_expedicion, erp_remisiones_imss.fecha_pago, erp_remisiones_imss.folio_imss, erp_remisiones_imss.numero_contrato, erp_status_remisiones_imss.id, erp_status_remisiones_imss.descripcion, erp_remisiones_imss.doc1, erp_remisiones_imss.doc2, erp_remisiones_imss.doc3, erp_remisiones_imss.doc4, erp_remisiones_imss.doc5, erp_remisiones_imss.doc6, erp_remisiones_imss.doc7, erp_remisiones_imss.doc8, erp_remisiones_imss.doc9, erp_remisiones_imss.doc10 "
                    +"FROM erp_remisiones_imss, erp_status_remisiones_imss "
                    +"WHERE erp_status_remisiones_imss.id=erp_remisiones_imss.id_status AND erp_remisiones_imss.borrado_logico=false "
                    +sql_busqueda+" "
                    +"order by "+orderBy+" "+asc+" limit "+pageSize+" OFFSET "+offset;
        }else{
            /*if (cad[7].equals("false")){
            }else{
            }*/
        }
        
        System.out.println("data_string: "+data_string);
        System.out.println("PaginaGrid: "+sql_to_query);
        ArrayList<HashMap<String, Object>> hm = (ArrayList<HashMap<String, Object>>) this.jdbcTemplate.query(
            sql_to_query,
            //new Object[]{new String(data_string),new Integer(pageSize),new Integer(offset)}, new RowMapper() {
                new Object[]{}, new RowMapper() {
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    HashMap<String, Object> row = new HashMap<String, Object>();
                    row.put("id",rs.getInt("id"));
                    row.put("folio",rs.getInt("id"));
                    row.put("folio_imss",rs.getString("folio_imss"));
                    //row.put("tipo",rs.getString("tipo"));
                    row.put("cliente",rs.getString("cliente"));
                    row.put("numero_contrato",rs.getString("numero_contrato"));
                    row.put("fecha_expedicion",rs.getString("fecha_expedicion"));
                    row.put("fecha_pago",rs.getString("fecha_pago"));
                    //row.put("nombre_agente",rs.getString("nombre_agente"));
                    row.put("descripcion",rs.getString("descripcion"));
                    return row;
                }
            }
        );
        return hm;
    }

    //obtiene las condiciones comerciales que se mostraran en el pdf de la cotizacion
    @Override
    public HashMap<String, String> getFormRemisionIMSS(Integer identificador) {
        String  sql_query = "SELECT DISTINCT erp_remisiones_imss.id, erp_remisiones_imss.cliente, erp_remisiones_imss.fecha_expedicion, erp_remisiones_imss.fecha_pago, erp_remisiones_imss.folio_imss, erp_remisiones_imss.importe, erp_remisiones_imss.numero_contrato, erp_status_remisiones_imss.id as id_status, erp_status_remisiones_imss.descripcion, erp_remisiones_imss.doc1, erp_remisiones_imss.doc2, erp_remisiones_imss.doc3, erp_remisiones_imss.doc4, erp_remisiones_imss.doc5, erp_remisiones_imss.doc6, erp_remisiones_imss.doc7, erp_remisiones_imss.doc8, erp_remisiones_imss.doc9, erp_remisiones_imss.doc10 ";
                sql_query += "FROM erp_remisiones_imss, erp_status_remisiones_imss ";
                sql_query +="WHERE erp_status_remisiones_imss.id=erp_remisiones_imss.id_status AND erp_remisiones_imss.id="+identificador+" AND erp_remisiones_imss.borrado_logico=false ";
                System.out.println("sql_query="+sql_query);
        HashMap<String, String> hm = (HashMap<String, String>) this.jdbcTemplate.queryForObject(
                sql_query,
            new Object[]{}, new RowMapper() {
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    HashMap<String, String> row = new HashMap<String, String>();
                    row.put("id",String.valueOf(rs.getInt("id")));
                    row.put("cliente",String.valueOf(rs.getString("cliente")));
                    row.put("fecha_expedicion",String.valueOf(rs.getDate("fecha_expedicion")));
                    row.put("fecha_pago",String.valueOf(rs.getDate("fecha_pago")));
                    row.put("folio_imss",String.valueOf(rs.getString("folio_imss")));
                    row.put("numero_contrato",String.valueOf(rs.getString("numero_contrato")));
                    row.put("id_status",String.valueOf(rs.getString("id_status")));
                    row.put("importe",String.valueOf(rs.getString("importe")));
                    row.put("descripcion",String.valueOf(rs.getString("descripcion")));
                    row.put("doc1",String.valueOf(rs.getString("doc1")));
                    row.put("doc2",String.valueOf(rs.getString("doc2")));
                    row.put("doc3",String.valueOf(rs.getString("doc3")));
                    row.put("doc4",String.valueOf(rs.getString("doc4")));
                    row.put("doc5",String.valueOf(rs.getString("doc5")));
                    row.put("doc6",String.valueOf(rs.getString("doc6")));
                    row.put("doc7",String.valueOf(rs.getString("doc7")));
                    row.put("doc8",String.valueOf(rs.getString("doc8")));
                    row.put("doc9",String.valueOf(rs.getString("doc9")));
                    row.put("doc10",String.valueOf(rs.getString("doc10")));
                    return row;
                }
            }
        );
        return hm;
    }
}
