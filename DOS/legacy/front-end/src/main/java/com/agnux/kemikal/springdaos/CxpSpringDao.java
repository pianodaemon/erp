/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.agnux.kemikal.springdaos;

import com.agnux.common.helpers.StringHelper;
import com.agnux.kemikal.interfacedaos.CxpInterfaceDao;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

/**
 *
 * @author agnux
 */

public class CxpSpringDao implements CxpInterfaceDao{
    private JdbcTemplate jdbcTemplate;

    public JdbcTemplate getJdbcTemplate() {
        return jdbcTemplate;
    }
    
    public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }
    
    
    @Override
    public int countAll(String data_string) {
        String sql_busqueda = "select id from gral_bus_catalogos('"+data_string+"') as foo (id integer)";
        String sql_to_query = "select count(id)::int as total from ("+sql_busqueda+") as subt";
        
        int rowCount = this.getJdbcTemplate().queryForInt(sql_to_query);
        return rowCount;
    }
    
    
    @Override
    public ArrayList<HashMap<String, Object>> getProveedor_PaginaGrid(String data_string, int offset, int pageSize, String orderBy, String asc) {
        String sql_busqueda = "select id from gral_bus_catalogos(?) as foo (id integer)";
        String sql_to_query = ""
                        + "select cxp_prov.id, "
                        + "cxp_prov.folio,"
                        + "cxp_prov.razon_social, "
                        +"cxp_prov.correo_electronico, "
                        +"cxp_prov.rfc, "
                        +"cxp_prov.clave_comercial AS  titulo "
                +"from cxp_prov "
                +"JOIN ("+sql_busqueda+") as subt on subt.id=cxp_prov.id "
                + "order by "+orderBy+" "+asc+" limit ? OFFSET ?";
        
        //System.out.println("data_string: "+data_string);
        //System.out.println("sql_to_query: "+sql_to_query);
        
        ArrayList<HashMap<String, Object>> hm = (ArrayList<HashMap<String, Object>>) this.jdbcTemplate.query(
            sql_to_query, 
            new Object[]{data_string,new Integer(pageSize),new Integer(offset)}, new RowMapper() {
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    HashMap<String, Object> row = new HashMap<String, Object>();
                    row.put("id",String.valueOf(rs.getInt("id")));
                    row.put("folio",rs.getString("folio"));
                    row.put("razon_social",rs.getString("razon_social"));
                    row.put("correo_electronico",rs.getString("correo_electronico"));
                    row.put("rfc",rs.getString("rfc"));
                    row.put("titulo",rs.getString("titulo"));
                    return row;
                }
            }
        );
        return hm;   
    }
    
    
    
    @Override
    public HashMap<String, String> selectFunctionValidateAaplicativo(String data, Integer idApp, String extra_data_array) {
        String sql_to_query = "select erp_fn_validaciones_por_aplicativo from erp_fn_validaciones_por_aplicativo('"+data+"',"+idApp+",array["+extra_data_array+"]);";
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
        String sql_to_query = "select * from gral_adm_catalogos('"+campos_data+"',array["+extra_data_array+"]);";
        
        String valor_retorno="";
        Map<String, Object> update = this.getJdbcTemplate().queryForMap(sql_to_query);
        valor_retorno = update.get("gral_adm_catalogos").toString();
        return valor_retorno;
    }
    
    
    @Override
    public String selectFunctionForCxpAdmProcesos(String campos_data, String extra_data_array) {
        String sql_to_query = "select * from cxp_adm_procesos('"+campos_data+"',array["+extra_data_array+"]);";
        //System.out.println("sql_procedimiento: "+sql_to_query);
        String valor_retorno="";
        Map<String, Object> update = this.getJdbcTemplate().queryForMap(sql_to_query);
        valor_retorno = update.get("cxp_adm_procesos").toString();
        return valor_retorno;
    }
    
    
    
    
    //obtiene datos para el buscador de proveedores
    @Override
    public ArrayList<HashMap<String, String>> getBuscadorProveedores(String rfc, String email, String razon_social, Integer id_empresa) {
        String where = "";
	if(rfc.equals("")==false){
            where=" AND cxp_prov.rfc ILIKE '%"+rfc+"%'";
	}
        
	if(email.equals("")==false){
            where +=" AND cxp_prov.correo_electronico ILIKE '%"+email+"%'";
	}
        
	if(razon_social.equals("")==false ){
            where +=" AND (cxp_prov.razon_social ilike '%"+razon_social+"%' OR cxp_prov.clave_comercial ilike '%"+razon_social+"%')";
	}
        
        String sql_to_query = "SELECT DISTINCT  cxp_prov.id, "
                                + "cxp_prov.folio AS numero_proveedor, "
                                + "cxp_prov.rfc, "
                                + "cxp_prov.razon_social, "
                                + "cxp_prov.calle||' '||cxp_prov.numero||', '||cxp_prov.colonia||', '||gral_mun.titulo||', '||gral_edo.titulo||', '||gral_pais.titulo ||' C.P. '||cxp_prov.cp as direccion, "
                                + "cxp_prov.proveedortipo_id,  "
                                + "cxp_prov.moneda_id "
                            + "FROM cxp_prov "
                            + "JOIN gral_pais ON gral_pais.id = cxp_prov.pais_id "
                            + "JOIN gral_edo ON gral_edo.id = cxp_prov.estado_id "
                            + "JOIN gral_mun ON gral_mun.id = cxp_prov.municipio_id  "
                            + "WHERE empresa_id="+id_empresa+" AND cxp_prov.borrado_logico = false "+where;
        
        //log.log(Level.INFO, "Ejecutando query de {0}", sql_to_query);
        
        ArrayList<HashMap<String, String>> hm_datos_proveedor = (ArrayList<HashMap<String, String>>) this.jdbcTemplate.query(
            sql_to_query,
            new Object[]{}, new RowMapper(){
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    HashMap<String, String> row = new HashMap<String, String>();
                    row.put("id",String.valueOf(rs.getInt("id")));
                    row.put("numero_proveedor",rs.getString("numero_proveedor"));
                    row.put("rfc",rs.getString("rfc"));
                    row.put("razon_social",rs.getString("razon_social"));
                    row.put("direccion",rs.getString("direccion"));
                    row.put("proveedortipo_id",String.valueOf(rs.getInt("proveedortipo_id")));
                    row.put("moneda_id",String.valueOf(rs.getInt("moneda_id")));
                    return row;
                }
            }
        );
        return hm_datos_proveedor;  
    }
    
    
    
    
    
    @Override
    public ArrayList<HashMap<String, String>> getProveedor_Datos(Integer idProveedor) {
        ArrayList<HashMap<String, String>> prov = (ArrayList<HashMap<String, String>>) this.jdbcTemplate.query(
            "SELECT id, "
                    + "folio, "
                    + "rfc, "
                    + "curp, "
                    + "razon_social, "
                    + "clave_comercial, "
                    + "calle, "
                    + "numero, "
                    + "colonia, "
                    + "cp, "
                    + "entre_calles, "
                    + "pais_id, "
                    + "estado_id, "
                    + "municipio_id, "
                    + "localidad_alternativa, "
                    + "telefono1, "
                    + "extension1, "
                    + "fax, "
                    + "telefono2, "
                    + "extension2, "
                    + "correo_electronico, "
                    + "web_site, "
                    + "impuesto, "
                    + "cxp_prov_zona_id, "
                    + "grupo_id, "
                    + "proveedortipo_id, "
                    + "clasif_1, "
                    + "clasif_2, "
                    + "clasif_3, "
                    + "moneda_id, "
                    + "tiempo_entrega_id, "
                    + "(CASE WHEN estatus=TRUE THEN 'true' ELSE 'false' END) AS estatus, "
                    + "limite_credito, "
                    + "dias_credito_id, "
                    + "descuento, "
                    + "credito_a_partir, "
                    + "cxp_prov_tipo_embarque_id, "
                    + "(CASE WHEN flete_pagado=TRUE THEN 'true' ELSE 'false' END) AS flete_pagado, "
                    + "condiciones, "
                    + "observaciones, "
                    + "vent_contacto, "
                    + "vent_puesto, "
                    + "vent_calle, "
                    + "vent_numero, "
                    + "vent_colonia, "
                    + "vent_cp, "
                    + "vent_entre_calles, "
                    + "vent_pais_id, "
                    + "vent_estado_id, "
                    + "vent_municipio_id, "
                    + "vent_telefono1, "
                    + "vent_extension1, "
                    + "vent_fax, "
                    + "vent_telefono2, "
                    + "vent_extension2, "
                    + "vent_email, "
                    + "cob_contacto, "
                    + "cob_puesto, "
                    + "cob_calle, "
                    + "cob_numero, "
                    + "cob_colonia, "
                    + "cob_cp, "
                    + "cob_entre_calles, "
                    + "cob_pais_id, "
                    + "cob_estado_id, "
                    + "cob_municipio_id, "
                    + "cob_telefono1, "
                    + "cob_extension1, "
                    + "cob_fax, "
                    + "cob_telefono2, "
                    + "cob_extension2, "
                    + "cob_email, "
                    + "comentarios,"
                    + "transportista "
            + "FROM cxp_prov where id = ?",  
            new Object[]{new Integer(idProveedor)}, new RowMapper() {
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    HashMap<String, String> row = new HashMap<String, String>();
                    row.put("id",String.valueOf(rs.getInt("id")));
                    row.put("folio",rs.getString("folio"));
                    row.put("rfc",rs.getString("rfc"));
                    row.put("curp",rs.getString("curp"));
                    row.put("razon_social",rs.getString("razon_social"));
                    row.put("clave_comercial",rs.getString("clave_comercial"));
                    row.put("calle",rs.getString("calle"));
                    row.put("numero",rs.getString("numero"));
                    row.put("colonia",rs.getString("colonia"));
                    row.put("cp",rs.getString("cp"));
                    row.put("entre_calles",rs.getString("entre_calles"));
                    row.put("pais_id",rs.getString("pais_id"));
                    row.put("estado_id",rs.getString("estado_id"));
                    row.put("municipio_id",rs.getString("municipio_id"));
                    row.put("localidad_alternativa",rs.getString("localidad_alternativa"));
                    row.put("telefono1",rs.getString("telefono1"));
                    row.put("extension1",rs.getString("extension1"));
                    row.put("fax",rs.getString("fax"));
                    row.put("telefono2",rs.getString("telefono2"));
                    row.put("extension2",rs.getString("extension2"));
                    row.put("correo_electronico",rs.getString("correo_electronico"));
                    row.put("web_site",rs.getString("web_site"));
                    row.put("impuesto",rs.getString("impuesto"));
                    row.put("cxp_prov_zona_id",rs.getString("cxp_prov_zona_id"));
                    row.put("grupo_id",rs.getString("grupo_id"));
                    row.put("proveedortipo_id",rs.getString("proveedortipo_id"));
                    row.put("clasif_1",rs.getString("clasif_1"));
                    row.put("clasif_2",rs.getString("clasif_2"));
                    row.put("clasif_3",rs.getString("clasif_3"));
                    row.put("moneda_id",rs.getString("moneda_id"));
                    row.put("tiempo_entrega_id",rs.getString("tiempo_entrega_id"));
                    row.put("estatus",rs.getString("estatus"));
                    row.put("limite_credito",StringHelper.roundDouble(rs.getString("limite_credito"),2));
                    row.put("dias_credito_id",rs.getString("dias_credito_id"));
                    row.put("descuento",StringHelper.roundDouble(rs.getString("descuento"),2));
                    row.put("credito_a_partir",rs.getString("credito_a_partir"));
                    row.put("cxp_prov_tipo_embarque_id",rs.getString("cxp_prov_tipo_embarque_id"));
                    row.put("flete_pagado",rs.getString("flete_pagado"));
                    row.put("condiciones",rs.getString("condiciones"));
                    row.put("observaciones",rs.getString("observaciones"));
                    row.put("vent_contacto",rs.getString("vent_contacto"));
                    row.put("vent_puesto",rs.getString("vent_puesto"));
                    row.put("vent_calle",rs.getString("vent_calle"));
                    row.put("vent_numero",rs.getString("vent_numero"));
                    row.put("vent_colonia",rs.getString("vent_colonia"));
                    row.put("vent_cp",rs.getString("vent_cp"));
                    row.put("vent_entre_calles",rs.getString("vent_entre_calles"));
                    row.put("vent_pais_id",rs.getString("vent_pais_id"));
                    row.put("vent_estado_id",rs.getString("vent_estado_id"));
                    row.put("vent_municipio_id",rs.getString("vent_municipio_id"));
                    row.put("vent_telefono1",rs.getString("vent_telefono1"));
                    row.put("vent_extension1",rs.getString("vent_extension1"));
                    row.put("vent_fax",rs.getString("vent_fax"));
                    row.put("vent_telefono2",rs.getString("vent_telefono2"));
                    row.put("vent_extension2",rs.getString("vent_extension2"));
                    row.put("vent_email",rs.getString("vent_email"));
                    row.put("cob_contacto",rs.getString("cob_contacto"));
                    row.put("cob_puesto",rs.getString("cob_puesto"));
                    row.put("cob_calle",rs.getString("cob_calle"));
                    row.put("cob_numero",rs.getString("cob_numero"));
                    row.put("cob_colonia",rs.getString("cob_colonia"));
                    row.put("cob_cp",rs.getString("cob_cp"));
                    row.put("cob_entre_calles",rs.getString("cob_entre_calles"));
                    row.put("cob_pais_id",rs.getString("cob_pais_id"));
                    row.put("cob_estado_id",rs.getString("cob_estado_id"));
                    row.put("cob_municipio_id",rs.getString("cob_municipio_id"));
                    row.put("cob_telefono1",rs.getString("cob_telefono1"));
                    row.put("cob_extension1",rs.getString("cob_extension1"));
                    row.put("cob_fax",rs.getString("cob_fax"));
                    row.put("cob_telefono2",rs.getString("cob_telefono2"));
                    row.put("cob_extension2",rs.getString("cob_extension2"));
                    row.put("cob_email",rs.getString("cob_email"));
                    row.put("comentarios",rs.getString("comentarios"));
                    row.put("transportista",String.valueOf(rs.getBoolean("transportista")));
                    return row;
                }
            }
        );
        return prov;
    }
    
    
    
    //obtiene datos de configuracion de Cuentas Contables para Proveedores
    @Override
    public ArrayList<HashMap<String, String>> getProveedor_DatosContabilidad(Integer idProveedor) {
        String sql_query=""
                + "SELECT "
                    + "cxp_prov.id, "
                    + "(CASE WHEN tbl_cta_pasivo.id IS NULL THEN 0 ELSE tbl_cta_pasivo.id END) AS pasivo_id_cta, "
                    + "(CASE WHEN tbl_cta_pasivo.cta IS NULL OR tbl_cta_pasivo.cta=0 THEN '' ELSE tbl_cta_pasivo.cta::character varying END) AS pasivo_cta, "
                    + "(CASE WHEN tbl_cta_pasivo.subcta IS NULL OR tbl_cta_pasivo.subcta=0 THEN '' ELSE tbl_cta_pasivo.subcta::character varying END) AS pasivo_subcta, "
                    + "(CASE WHEN tbl_cta_pasivo.ssubcta IS NULL OR tbl_cta_pasivo.ssubcta=0 THEN '' ELSE tbl_cta_pasivo.ssubcta::character varying END) AS pasivo_ssubcta, "
                    + "(CASE WHEN tbl_cta_pasivo.sssubcta IS NULL OR tbl_cta_pasivo.sssubcta=0 THEN '' ELSE tbl_cta_pasivo.sssubcta::character varying END) AS pasivo_sssubcta,"
                    + "(CASE WHEN tbl_cta_pasivo.ssssubcta IS NULL OR tbl_cta_pasivo.ssssubcta=0 THEN '' ELSE tbl_cta_pasivo.ssssubcta::character varying END) AS pasivo_ssssubcta, "
                    + "(CASE WHEN tbl_cta_pasivo.descripcion IS NULL OR tbl_cta_pasivo.descripcion='' THEN  (CASE WHEN tbl_cta_pasivo.descripcion_ing IS NULL OR tbl_cta_pasivo.descripcion_ing='' THEN  (CASE WHEN tbl_cta_pasivo.descripcion_otr IS NULL OR tbl_cta_pasivo.descripcion_otr='' THEN '' ELSE tbl_cta_pasivo.descripcion_otr END) ELSE tbl_cta_pasivo.descripcion_ing END )  ELSE tbl_cta_pasivo.descripcion END ) AS pasivo_descripcion, "
                    + "(CASE WHEN tbl_cta_egreso.id IS NULL THEN 0 ELSE  tbl_cta_egreso.id END ) AS egreso_id_cta, "
                    + "(CASE WHEN tbl_cta_egreso.cta IS NULL OR tbl_cta_egreso.cta=0 THEN '' ELSE tbl_cta_egreso.cta::character varying END ) AS egreso_cta, "
                    + "(CASE WHEN tbl_cta_egreso.subcta IS NULL OR tbl_cta_egreso.subcta=0 THEN '' ELSE tbl_cta_egreso.subcta::character varying END ) AS egreso_subcta, "
                    + "(CASE WHEN tbl_cta_egreso.ssubcta IS NULL OR tbl_cta_egreso.ssubcta=0 THEN '' ELSE tbl_cta_egreso.ssubcta::character varying END )  AS egreso_ssubcta, "
                    + "(CASE WHEN tbl_cta_egreso.sssubcta IS NULL OR tbl_cta_egreso.sssubcta=0 THEN '' ELSE tbl_cta_egreso.sssubcta::character varying END ) AS egreso_sssubcta,"
                    + "(CASE WHEN tbl_cta_egreso.ssssubcta IS NULL OR tbl_cta_egreso.ssssubcta=0 THEN '' ELSE tbl_cta_egreso.ssssubcta::character varying END) AS egreso_ssssubcta,  "
                    + "(CASE WHEN tbl_cta_egreso.descripcion IS NULL OR tbl_cta_egreso.descripcion='' THEN  (CASE WHEN tbl_cta_egreso.descripcion_ing IS NULL OR tbl_cta_egreso.descripcion_ing='' THEN  (CASE WHEN tbl_cta_egreso.descripcion_otr IS NULL OR tbl_cta_egreso.descripcion_otr='' THEN '' ELSE tbl_cta_egreso.descripcion_otr END) ELSE tbl_cta_egreso.descripcion_ing END )  ELSE tbl_cta_egreso.descripcion END ) AS egreso_descripcion,  "
                    + "(CASE WHEN tbl_cta_ietu.id IS NULL THEN 0 ELSE tbl_cta_ietu.id END) AS ietu_id_cta,  "
                    + "(CASE WHEN tbl_cta_ietu.cta IS NULL OR tbl_cta_ietu.cta=0 THEN '' ELSE tbl_cta_ietu.cta::character varying END) AS ietu_cta,  "
                    + "(CASE WHEN tbl_cta_ietu.subcta IS NULL OR tbl_cta_ietu.subcta=0 THEN '' ELSE tbl_cta_ietu.subcta::character varying END) AS ietu_subcta,  "
                    + "(CASE WHEN tbl_cta_ietu.ssubcta IS NULL OR tbl_cta_ietu.ssubcta=0 THEN '' ELSE tbl_cta_ietu.ssubcta::character varying END) AS ietu_ssubcta,  "
                    + "(CASE WHEN tbl_cta_ietu.sssubcta IS NULL OR tbl_cta_ietu.sssubcta=0 THEN '' ELSE tbl_cta_ietu.sssubcta::character varying END) AS ietu_sssubcta, "
                    + "(CASE WHEN tbl_cta_ietu.ssssubcta IS NULL OR tbl_cta_ietu.ssssubcta=0 THEN '' ELSE tbl_cta_ietu.ssssubcta::character varying END) AS ietu_ssssubcta,  "
                    + "(CASE WHEN tbl_cta_ietu.descripcion IS NULL OR tbl_cta_ietu.descripcion='' THEN  (CASE WHEN tbl_cta_ietu.descripcion_ing IS NULL OR tbl_cta_ietu.descripcion_ing='' THEN  (CASE WHEN tbl_cta_ietu.descripcion_otr IS NULL OR tbl_cta_ietu.descripcion_otr='' THEN '' ELSE tbl_cta_ietu.descripcion_otr END) ELSE tbl_cta_ietu.descripcion_ing END )  ELSE tbl_cta_ietu.descripcion END ) AS ietu_descripcion, "
                    + "(CASE WHEN tbl_cta_complement.id IS NULL THEN 0 ELSE tbl_cta_complement.id END) AS complement_id_cta,  "
                    + "(CASE WHEN tbl_cta_complement.cta IS NULL OR tbl_cta_complement.cta=0 THEN '' ELSE tbl_cta_complement.cta::character varying END) AS complement_cta,  "
                    + "(CASE WHEN tbl_cta_complement.subcta IS NULL OR tbl_cta_complement.subcta=0 THEN '' ELSE tbl_cta_complement.subcta::character varying END) AS complement_subcta,  "
                    + "(CASE WHEN tbl_cta_complement.ssubcta IS NULL OR tbl_cta_complement.ssubcta=0 THEN '' ELSE tbl_cta_complement.ssubcta::character varying END) AS complement_ssubcta, "
                    + "(CASE WHEN tbl_cta_complement.sssubcta IS NULL OR tbl_cta_complement.sssubcta=0 THEN '' ELSE tbl_cta_complement.sssubcta::character varying END) AS complement_sssubcta, "
                    + "(CASE WHEN tbl_cta_complement.ssssubcta IS NULL OR tbl_cta_complement.ssssubcta=0 THEN '' ELSE tbl_cta_complement.ssssubcta::character varying END) AS complement_ssssubcta, "
                    + "(CASE WHEN tbl_cta_complement.descripcion IS NULL OR tbl_cta_complement.descripcion='' THEN  (CASE WHEN tbl_cta_complement.descripcion_ing IS NULL OR tbl_cta_complement.descripcion_ing='' THEN  (CASE WHEN tbl_cta_complement.descripcion_otr IS NULL OR tbl_cta_complement.descripcion_otr='' THEN '' ELSE tbl_cta_complement.descripcion_otr END) ELSE tbl_cta_complement.descripcion_ing END )  ELSE tbl_cta_complement.descripcion END ) AS complement_descripcion, "
                    + "(CASE WHEN tbl_cta_pc.id IS NULL THEN 0 ELSE tbl_cta_pc.id END) AS pc_id_cta,  "
                    + "(CASE WHEN tbl_cta_pc.cta IS NULL OR tbl_cta_pc.cta=0 THEN '' ELSE tbl_cta_pc.cta::character varying END) AS pc_cta, "
                    + "(CASE WHEN tbl_cta_pc.subcta IS NULL OR tbl_cta_pc.subcta=0 THEN '' ELSE tbl_cta_pc.subcta::character varying END) AS pc_subcta, "
                    + "(CASE WHEN tbl_cta_pc.ssubcta IS NULL OR tbl_cta_pc.ssubcta=0 THEN '' ELSE tbl_cta_pc.ssubcta::character varying END) AS pc_ssubcta, "
                    + "(CASE WHEN tbl_cta_pc.sssubcta IS NULL OR tbl_cta_pc.sssubcta=0 THEN '' ELSE tbl_cta_pc.sssubcta::character varying END) AS pc_sssubcta, "
                    + "(CASE WHEN tbl_cta_pc.ssssubcta IS NULL OR tbl_cta_pc.ssssubcta=0 THEN '' ELSE tbl_cta_pc.ssssubcta::character varying END) AS pc_ssssubcta, "
                    + "(CASE WHEN tbl_cta_pc.descripcion IS NULL OR tbl_cta_pc.descripcion='' THEN  (CASE WHEN tbl_cta_pc.descripcion_ing IS NULL OR tbl_cta_pc.descripcion_ing='' THEN  (CASE WHEN tbl_cta_pc.descripcion_otr IS NULL OR tbl_cta_pc.descripcion_otr='' THEN '' ELSE tbl_cta_pc.descripcion_otr END) ELSE tbl_cta_pc.descripcion_ing END )  ELSE tbl_cta_pc.descripcion END ) AS pc_descripcion  "
                + "FROM cxp_prov  "
                + "LEFT JOIN ctb_cta AS tbl_cta_pasivo ON tbl_cta_pasivo.id=cxp_prov.ctb_cta_id_pasivo "
                + "LEFT JOIN ctb_cta AS tbl_cta_egreso ON tbl_cta_egreso.id=cxp_prov.ctb_cta_id_egreso  "
                + "LEFT JOIN ctb_cta AS tbl_cta_ietu ON tbl_cta_ietu.id=cxp_prov.ctb_cta_id_ietu  "
                + "LEFT JOIN ctb_cta AS tbl_cta_complement ON tbl_cta_complement.id=cxp_prov.ctb_cta_id_comple "
                + "LEFT JOIN ctb_cta AS tbl_cta_pc ON tbl_cta_pc.id=cxp_prov.ctb_cta_id_pasivo_comple "
                + "WHERE cxp_prov.id=?;";
        
        //System.out.println("getProveedor_DatosContabilidad: "+ sql_query);
        
        ArrayList<HashMap<String, String>> contab = (ArrayList<HashMap<String, String>>) this.jdbcTemplate.query(
                sql_query,
            new Object[]{new Integer(idProveedor)}, new RowMapper() {
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    HashMap<String, String> row = new HashMap<String, String>();
                    row.put("id",String.valueOf(rs.getInt("id")));
                    row.put("pasivo_id_cta",rs.getString("pasivo_id_cta"));
                    row.put("pasivo_cta",rs.getString("pasivo_cta"));
                    row.put("pasivo_subcta",rs.getString("pasivo_subcta"));
                    row.put("pasivo_ssubcta",rs.getString("pasivo_ssubcta"));
                    row.put("pasivo_sssubcta",rs.getString("pasivo_sssubcta"));
                    row.put("pasivo_ssssubcta",rs.getString("pasivo_ssssubcta"));
                    row.put("pasivo_descripcion",rs.getString("pasivo_descripcion"));
                    
                    row.put("egreso_id_cta",rs.getString("egreso_id_cta"));
                    row.put("egreso_cta",rs.getString("egreso_cta"));
                    row.put("egreso_subcta",rs.getString("egreso_subcta"));
                    row.put("egreso_ssubcta",rs.getString("egreso_ssubcta"));
                    row.put("egreso_sssubcta",rs.getString("egreso_sssubcta"));
                    row.put("egreso_ssssubcta",rs.getString("egreso_ssssubcta"));
                    row.put("egreso_descripcion",rs.getString("egreso_descripcion"));
                    
                    row.put("ietu_id_cta",rs.getString("ietu_id_cta"));
                    row.put("ietu_cta",rs.getString("ietu_cta"));
                    row.put("ietu_subcta",rs.getString("ietu_subcta"));
                    row.put("ietu_ssubcta",rs.getString("ietu_ssubcta"));
                    row.put("ietu_sssubcta",rs.getString("ietu_sssubcta"));
                    row.put("ietu_ssssubcta",rs.getString("ietu_ssssubcta"));
                    row.put("ietu_descripcion",rs.getString("ietu_descripcion"));
                    
                    row.put("complement_id_cta",rs.getString("complement_id_cta"));
                    row.put("complement_cta",rs.getString("complement_cta"));
                    row.put("complement_subcta",rs.getString("complement_subcta"));
                    row.put("complement_ssubcta",rs.getString("complement_ssubcta"));
                    row.put("complement_sssubcta",rs.getString("complement_sssubcta"));
                    row.put("complement_ssssubcta",rs.getString("complement_ssssubcta"));
                    row.put("complement_descripcion",rs.getString("complement_descripcion"));
                    
                    row.put("pc_id_cta",rs.getString("pc_id_cta"));
                    row.put("pc_cta",rs.getString("pc_cta"));
                    row.put("pc_subcta",rs.getString("pc_subcta"));
                    row.put("pc_ssubcta",rs.getString("pc_ssubcta"));
                    row.put("pc_sssubcta",rs.getString("pc_sssubcta"));
                    row.put("pc_ssssubcta",rs.getString("pc_ssssubcta"));
                    row.put("pc_descripcion",rs.getString("pc_descripcion"));
                    
                    return row;
                }
            }
        );
        return contab;
    }
    
    
    
    
    
    @Override
    public ArrayList<HashMap<String, String>> getProveedor_Contactos(Integer idProveedor) {
        ArrayList<HashMap<String, String>> contact = (ArrayList<HashMap<String, String>>) this.jdbcTemplate.query(
            "SELECT id,contacto,telefono,email FROM cxp_prov_contactos WHERE proveedor_id = ?",
            new Object[]{new Integer(idProveedor)}, new RowMapper() {
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    HashMap<String, String> row = new HashMap<String, String>();
                    row.put("id",String.valueOf(rs.getInt("id")));
                    row.put("contacto",rs.getString("contacto"));
                    row.put("telefono",rs.getString("telefono"));
                    row.put("email",rs.getString("email"));
                    return row;
                }
            }
        );
        return contact;
    }
    
    
    @Override
    public ArrayList<HashMap<String, String>> getPaises() {
        //String sql_to_query = "SELECT DISTINCT cve_pais ,pais_ent FROM municipios;";
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
        //String sql_to_query = "SELECT DISTINCT cve_ent ,nom_ent FROM municipios where cve_pais='"+id_pais+"' order by nom_ent;";
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
        //String sql_to_query = "SELECT DISTINCT cve_mun ,nom_mun FROM municipios where cve_ent='"+id_entidad+"' and cve_pais='"+id_pais+"' order by nom_mun;";
        String sql_to_query = "SELECT id as cve_mun, titulo as nom_mun FROM gral_mun WHERE estado_id="+id_entidad+" and pais_id="+id_pais+" order by nom_mun;";
        
        //System.out.println("Ejecutando query loc_for_this_entidad: "+sql_to_query);
        ArrayList<HashMap<String, String>> hm = (ArrayList<HashMap<String, String>>) this.jdbcTemplate.query(
            sql_to_query,
            new Object[]{}, new RowMapper(){
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    HashMap<String, String> row = new HashMap<String, String>();
                    row.put("cve_mun",rs.getString("cve_mun"));
                    row.put("nom_mun",rs.getString("nom_mun"));
                    return row;
                }
            }
        );
        return hm;
    }
    
    
    
    @Override
     public ArrayList<HashMap<String, String>> getProveedor_Tipos() {
        String sql_to_query = "SELECT id ,titulo FROM cxp_prov_clases;";
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
    
    
    
    @Override
    public ArrayList<HashMap<String, String>> getMonedas() {
        String sql_to_query = "SELECT id, descripcion FROM  gral_mon WHERE borrado_logico=FALSE AND compras=TRUE ORDER BY id ASC;";
        //log.log(Level.INFO, "Ejecutando query de {0}", sql_to_query);
        ArrayList<HashMap<String, String>> hm_monedas = (ArrayList<HashMap<String, String>>) this.jdbcTemplate.query(
            sql_to_query,
            new Object[]{}, new RowMapper(){
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    HashMap<String, String> row = new HashMap<String, String>();
                    row.put("id",String.valueOf(rs.getInt("id")));
                    row.put("descripcion",rs.getString("descripcion"));
                    return row;
                }
            }
        );
        return hm_monedas;
    }
    
    
    
    
    @Override
    public ArrayList<HashMap<String, String>> getProveedor_TiemposEntrega() {
        String sql_to_query = "SELECT id, descripcion FROM erp_tiempos_entrega;";
        //log.log(Level.INFO, "Ejecutando query de {0}", sql_to_query);
        ArrayList<HashMap<String, String>> tiempos_entrega = (ArrayList<HashMap<String, String>>) this.jdbcTemplate.query(
            sql_to_query,
            new Object[]{}, new RowMapper(){
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    HashMap<String, String> row = new HashMap<String, String>();
                    row.put("id",String.valueOf(rs.getInt("id")));
                    row.put("descripcion",rs.getString("descripcion"));
                    return row;
                }
            }
        );
        return tiempos_entrega;
    }
    
    
    @Override
    public ArrayList<HashMap<String, String>> getProveedor_Zonas() {
        String sql_to_query = "SELECT id, titulo AS nombre_zona FROM cxp_prov_zonas ORDER BY id;";
        //log.log(Level.INFO, "Ejecutando query de {0}", sql_to_query);
        ArrayList<HashMap<String, String>> zonas = (ArrayList<HashMap<String, String>>) this.jdbcTemplate.query(
            sql_to_query,
            new Object[]{}, new RowMapper(){
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    HashMap<String, String> row = new HashMap<String, String>();
                    row.put("id",String.valueOf(rs.getInt("id")));
                    row.put("nombre_zona",rs.getString("nombre_zona"));
                    return row;
                }
            }
        );
        return zonas;
    }
    
    
    
    @Override
    public ArrayList<HashMap<String, String>> getProveedor_Grupos() {
        String sql_to_query = "SELECT id, titulo AS nombre_grupo FROM cxp_prov_grupos WHERE borrado_logico=FALSE ORDER BY id ;";
        //log.log(Level.INFO, "Ejecutando query de {0}", sql_to_query);
        ArrayList<HashMap<String, String>> grupos = (ArrayList<HashMap<String, String>>) this.jdbcTemplate.query(
            sql_to_query,
            new Object[]{}, new RowMapper(){
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    HashMap<String, String> row = new HashMap<String, String>();
                    row.put("id",String.valueOf(rs.getInt("id")));
                    row.put("nombre_grupo",rs.getString("nombre_grupo"));
                    return row;
                }
            }
        );
        return grupos;
    }
    
    
    
    @Override
    public ArrayList<HashMap<String, String>> getProveedor_Clasificacion1(Integer id_empresa) {
        String sql_to_query = "SELECT id, titulo AS clasificacion1 FROM cxp_prov_clas1 WHERE borrado_logico=false AND gral_emp_id="+id_empresa+" ORDER BY id;";
        //log.log(Level.INFO, "Ejecutando query de {0}", sql_to_query);
        ArrayList<HashMap<String, String>> clasif1 = (ArrayList<HashMap<String, String>>) this.jdbcTemplate.query(
            sql_to_query,
            new Object[]{}, new RowMapper(){
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    HashMap<String, String> row = new HashMap<String, String>();
                    row.put("id",String.valueOf(rs.getInt("id")));
                    row.put("clasificacion1",rs.getString("clasificacion1"));
                    return row;
                }
            }
        );
        return clasif1;
    }
    
    @Override
    public ArrayList<HashMap<String, String>> getProveedor_Clasificacion2(Integer id_empresa) {
        String sql_to_query = "SELECT id, titulo AS clasificacion2 FROM cxp_prov_clas2 WHERE borrado_logico=false AND gral_emp_id="+id_empresa+" ORDER BY id;";
        //log.log(Level.INFO, "Ejecutando query de {0}", sql_to_query);
        ArrayList<HashMap<String, String>> clasif2 = (ArrayList<HashMap<String, String>>) this.jdbcTemplate.query(
            sql_to_query,
            new Object[]{}, new RowMapper(){
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    HashMap<String, String> row = new HashMap<String, String>();
                    row.put("id",String.valueOf(rs.getInt("id")));
                    row.put("clasificacion2",rs.getString("clasificacion2"));
                    return row;
                }
            }
        );
        return clasif2;
    }
    
    @Override
    public ArrayList<HashMap<String, String>> getProveedor_Clasificacion3(Integer id_empresa) {
        String sql_to_query = "SELECT id, titulo AS clasificacion3 FROM cxp_prov_clas3 WHERE borrado_logico=false AND gral_emp_id="+id_empresa+" ORDER BY id;";
        //log.log(Level.INFO, "Ejecutando query de {0}", sql_to_query);
        ArrayList<HashMap<String, String>> clasif3 = (ArrayList<HashMap<String, String>>) this.jdbcTemplate.query(
            sql_to_query,
            new Object[]{}, new RowMapper(){
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    HashMap<String, String> row = new HashMap<String, String>();
                    row.put("id",String.valueOf(rs.getInt("id")));
                    row.put("clasificacion3",rs.getString("clasificacion3"));
                    return row;
                }
            }
        );
        return clasif3;
    }
    

    @Override
    public ArrayList<HashMap<String, String>> getProveedor_InicioCredito() {
        String sql_to_query = "SELECT id, titulo FROM cxp_prov_creapar ORDER BY id;";
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
    
    
    @Override
    public ArrayList<HashMap<String, String>> getProveedor_TiposEmbarque() {
        String sql_to_query = "SELECT id, titulo FROM cxp_prov_tipos_embarque ORDER BY id;";
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
    
    
    
    
    @Override
    public ArrayList<HashMap<String, String>> getProveedor_CuentasMayor(Integer id_empresa) {
        String sql_query = "SELECT id, titulo FROM ctb_may_clases ORDER BY id;";
        ArrayList<HashMap<String, String>> hm = (ArrayList<HashMap<String, String>>) this.jdbcTemplate.query(
            sql_query,  
            new Object[]{}, new RowMapper() {
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
    
    
    
    
    //metodo para el buscador de cuentas contables
    @Override
    public ArrayList<HashMap<String, String>> getProveedor_CuentasContables(Integer cta_mayor, Integer detalle, String clasifica, String cta, String scta, String sscta, String ssscta, String sssscta, String descripcion, Integer id_empresa) {
        
        String where="";
	if(cta_mayor != 0){
            where+=" AND ctb_cta.cta_mayor="+cta_mayor+" ";
	}
	if(!clasifica.equals("")){
            where+=" AND ctb_cta.clasifica="+clasifica+" ";
	}
        
	if(!cta.equals("")){
            where+=" AND ctb_cta.cta="+cta+" ";
	}
        
	if(!scta.equals("")){
            where+=" AND ctb_cta.subcta="+scta+" ";
	}
        
	if(!sscta.equals("")){
            where+=" AND ctb_cta.ssubcta="+sscta+" ";
	}
        
	if(!ssscta.equals("")){
            where+=" AND ctb_cta.sssubcta="+ssscta+" ";
	}
        
	if(!sssscta.equals("")){
            where+=" AND ctb_cta.ssssubcta="+sssscta+" ";
	}
        
	if(!descripcion.equals("")){
            where+=" AND ctb_cta.ssssubcta ilike '%"+descripcion+"%'";
	}
        
        String sql_query = ""
                + "SELECT DISTINCT "
                    + "ctb_cta.id, "
                    + "ctb_cta.cta_mayor, "
                    + "ctb_cta.clasifica, "
                    + "ctb_cta.cta, "
                    + "ctb_cta.subcta, "
                    + "ctb_cta.ssubcta, "
                    + "ctb_cta.sssubcta,"
                    + "ctb_cta.ssssubcta, "
                    + "(CASE 	WHEN nivel_cta=1 THEN rpad(ctb_cta.cta::character varying, 4, '0')   "
                    + "WHEN ctb_cta.nivel_cta=2 THEN '&nbsp;&nbsp;&nbsp;'||rpad(ctb_cta.cta::character varying, 4, '0')||'-'||lpad(ctb_cta.subcta::character varying, 4, '0') "
                    + "WHEN ctb_cta.nivel_cta=3 THEN '&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;'||rpad(ctb_cta.cta::character varying, 4, '0')||'-'||lpad(ctb_cta.subcta::character varying, 4, '0')||'-'||lpad(ctb_cta.ssubcta::character varying, 4, '0') "
                    + "WHEN ctb_cta.nivel_cta=4 THEN '&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;'||rpad(ctb_cta.cta::character varying, 4, '0')||'-'||lpad(ctb_cta.subcta::character varying, 4, '0')||'-'||lpad(ctb_cta.ssubcta::character varying, 4, '0')||'-'||lpad(ctb_cta.sssubcta::character varying, 4, '0') "
                    + "WHEN ctb_cta.nivel_cta=5 THEN '&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;'||rpad(ctb_cta.cta::character varying, 4, '0')||'-'||lpad(ctb_cta.subcta::character varying, 4, '0')||'-'||lpad(ctb_cta.ssubcta::character varying, 4, '0')||'-'||lpad(ctb_cta.sssubcta::character varying, 4, '0')||'-'||lpad(ctb_cta.ssssubcta::character varying, 4, '0') "
                    + "ELSE '' "
                    + "END ) AS cuenta, "
                    + "(CASE WHEN ctb_cta.descripcion IS NULL OR ctb_cta.descripcion='' THEN  (CASE WHEN ctb_cta.descripcion_ing IS NULL OR ctb_cta.descripcion_ing='' THEN  ctb_cta.descripcion_otr ELSE ctb_cta.descripcion_ing END )  ELSE descripcion END ) AS descripcion, "
                    + "(CASE WHEN ctb_cta.detalle=0 THEN 'NO' WHEN ctb_cta.detalle=1 THEN 'SI' ELSE '' END) AS detalle, "
                    + "ctb_cta.nivel_cta "
                + "FROM ctb_cta "
                + "WHERE ctb_cta.borrado_logico=false  "
                + "AND ctb_cta.gral_emp_id=? AND ctb_cta.detalle=? "+ where +" "
                + "ORDER BY ctb_cta.id;";
        
        
        //System.out.println("getProveedor_CuentasContables: "+sql_query);
        
        ArrayList<HashMap<String, String>> hm = (ArrayList<HashMap<String, String>>) this.jdbcTemplate.query(
            sql_query,
            new Object[]{new Integer(id_empresa), new Integer(detalle)}, new RowMapper() {
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    HashMap<String, String> row = new HashMap<String, String>();
                    row.put("id",String.valueOf(rs.getInt("id")));
                    row.put("m",String.valueOf(rs.getInt("cta_mayor")));
                    row.put("c",String.valueOf(rs.getInt("clasifica")));
                    row.put("cta",String.valueOf(rs.getInt("cta")));
                    row.put("subcta",String.valueOf(rs.getInt("subcta")));
                    row.put("ssubcta",String.valueOf(rs.getInt("ssubcta")));
                    row.put("sssubcta",String.valueOf(rs.getInt("sssubcta")));
                    row.put("ssssubcta",String.valueOf(rs.getInt("ssssubcta")));
                    row.put("cuenta",rs.getString("cuenta"));
                    row.put("descripcion",rs.getString("descripcion"));
                    row.put("detalle",rs.getString("detalle"));
                    row.put("nivel_cta",String.valueOf(rs.getInt("nivel_cta")));
                    return row;
                }
            }
        );
        return hm;
    }
    
    
    
    
    
    
    //catalogo de Proveedores Clasificacion 1
    @Override
    public ArrayList<HashMap<String, Object>> getProveedoresClasif1_PaginaGrid(String data_string, int offset, int pageSize, String orderBy, String asc) {
        //throw new UnsupportedOperationException("Not supported yet.");
        String sql_busqueda = "select id from gral_bus_catalogos(?) as foo (id integer)";
        
        String sql_to_query = "SELECT cxp_prov_clas1.id, cxp_prov_clas1.titulo FROM cxp_prov_clas1 "
                        +"JOIN ("+sql_busqueda+") AS sbt ON sbt.id = cxp_prov_clas1.id "
                        +"order by "+orderBy+" "+asc+" limit ? OFFSET ?";
        
        //System.out.println("Busqueda GetPage: "+sql_to_query);
        ArrayList<HashMap<String, Object>> hm = (ArrayList<HashMap<String, Object>>) this.jdbcTemplate.query(
            sql_to_query, 
            new Object[]{new String(data_string), new Integer(pageSize),new Integer(offset)}, new RowMapper() {
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    HashMap<String, Object> row = new HashMap<String, Object>();
                    row.put("id",rs.getInt("id"));
                    row.put("titulo",rs.getString("titulo"));
                    return row;
                }
            }
        );
        return hm; 
    }

    @Override
    public ArrayList<HashMap<String, String>> getProveedoresClasif1_Datos(Integer id) {
        //throw new UnsupportedOperationException("Not supported yet.");
        String sql_query = "SELECT id, titulo FROM cxp_prov_clas1 WHERE id = ?;";
        ArrayList<HashMap<String, String>> hm = (ArrayList<HashMap<String, String>>) this.jdbcTemplate.query(
            sql_query,  
            new Object[]{new Integer(id)}, new RowMapper() {
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
    
    
    
    //catalogo de Proveedores Clasificacion 2
    @Override
    public ArrayList<HashMap<String, Object>> getProveedoresClasif2_PaginaGrid(String data_string, int offset, int pageSize, String orderBy, String asc) {
        //throw new UnsupportedOperationException("Not supported yet.");
        String sql_busqueda = "select id from gral_bus_catalogos(?) as foo (id integer)";
        
        String sql_to_query = "SELECT cxp_prov_clas2.id, cxp_prov_clas2.titulo FROM cxp_prov_clas2 "
                        +"JOIN ("+sql_busqueda+") AS sbt ON sbt.id = cxp_prov_clas2.id "
                        +"order by "+orderBy+" "+asc+" limit ? OFFSET ?";
        
        //System.out.println("Busqueda GetPage: "+sql_to_query);
        ArrayList<HashMap<String, Object>> hm = (ArrayList<HashMap<String, Object>>) this.jdbcTemplate.query(
            sql_to_query, 
            new Object[]{new String(data_string), new Integer(pageSize),new Integer(offset)}, new RowMapper() {
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    HashMap<String, Object> row = new HashMap<String, Object>();
                    row.put("id",rs.getInt("id"));
                    row.put("titulo",rs.getString("titulo"));
                    return row;
                }
            }
        );
        return hm; 
    }

    @Override
    public ArrayList<HashMap<String, String>> getProveedoresClasif2_Datos(Integer id) {
        //throw new UnsupportedOperationException("Not supported yet.");
        String sql_query = "SELECT id, titulo FROM cxp_prov_clas2 WHERE id = ?;";
        ArrayList<HashMap<String, String>> hm = (ArrayList<HashMap<String, String>>) this.jdbcTemplate.query(
            sql_query,  
            new Object[]{new Integer(id)}, new RowMapper() {
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
    
    
    
    
    //catalogo de Proveedores Clasificacion 2
    @Override
    public ArrayList<HashMap<String, Object>> getProveedoresClasif3_PaginaGrid(String data_string, int offset, int pageSize, String orderBy, String asc) {
        //throw new UnsupportedOperationException("Not supported yet.");
        String sql_busqueda = "select id from gral_bus_catalogos(?) as foo (id integer)";
        
        String sql_to_query = "SELECT cxp_prov_clas3.id, cxp_prov_clas3.titulo FROM cxp_prov_clas3 "
                        +"JOIN ("+sql_busqueda+") AS sbt ON sbt.id = cxp_prov_clas3.id "
                        +"order by "+orderBy+" "+asc+" limit ? OFFSET ?";
        
        //System.out.println("Busqueda GetPage: "+sql_to_query);
        ArrayList<HashMap<String, Object>> hm = (ArrayList<HashMap<String, Object>>) this.jdbcTemplate.query(
            sql_to_query, 
            new Object[]{new String(data_string), new Integer(pageSize),new Integer(offset)}, new RowMapper() {
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    HashMap<String, Object> row = new HashMap<String, Object>();
                    row.put("id",rs.getInt("id"));
                    row.put("titulo",rs.getString("titulo"));
                    return row;
                }
            }
        );
        return hm; 
    }

    @Override
    public ArrayList<HashMap<String, String>> getProveedoresClasif3_Datos(Integer id) {
        //throw new UnsupportedOperationException("Not supported yet.");
        String sql_query = "SELECT id, titulo FROM cxp_prov_clas3 WHERE id = ?;";
        ArrayList<HashMap<String, String>> hm = (ArrayList<HashMap<String, String>>) this.jdbcTemplate.query(
            sql_query,  
            new Object[]{new Integer(id)}, new RowMapper() {
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
    
    
    //catalogo de zonas de proveedores
    @Override
    public ArrayList<HashMap<String, Object>> getProveedoresZonas_PaginaGrid(String data_string, int offset, int pageSize, String orderBy, String asc) {
        //throw new UnsupportedOperationException("Not supported yet.");
        String sql_busqueda = "select id from gral_bus_catalogos(?) as foo (id integer)";
        
	String sql_to_query = "SELECT cxp_prov_zonas.id, cxp_prov_zonas.titulo FROM cxp_prov_zonas "
                        +"JOIN ("+sql_busqueda+") AS sbt ON sbt.id = cxp_prov_zonas.id "
                        +"order by "+orderBy+" "+asc+" limit ? OFFSET ?";
        
        //System.out.println("Busqueda GetPage: "+sql_to_query);
        ArrayList<HashMap<String, Object>> hm = (ArrayList<HashMap<String, Object>>) this.jdbcTemplate.query(
            sql_to_query, 
            new Object[]{new String(data_string), new Integer(pageSize),new Integer(offset)}, new RowMapper() {
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    HashMap<String, Object> row = new HashMap<String, Object>();
                    row.put("id",rs.getInt("id"));
                    row.put("titulo",rs.getString("titulo"));
                    return row;
                }
            }
        );
        return hm; 
    }

    @Override
    public ArrayList<HashMap<String, String>> getProveedoresZonas_Datos(Integer id) {
        //throw new UnsupportedOperationException("Not supported yet.");
        String sql_query = "SELECT id, titulo FROM cxp_prov_zonas WHERE id = ?;";
        ArrayList<HashMap<String, String>> hm = (ArrayList<HashMap<String, String>>) this.jdbcTemplate.query(
            sql_query,  
            new Object[]{new Integer(id)}, new RowMapper() {
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

    
    
    //catalogo de grupos de proveedores
    @Override
    public ArrayList<HashMap<String, Object>> getProveedoresGrupos_PaginaGrid(String data_string, int offset, int pageSize, String orderBy, String asc) {
        String sql_busqueda = "select id from gral_bus_catalogos(?) as foo (id integer)";
        
	String sql_to_query = "SELECT cxp_prov_grupos.id, cxp_prov_grupos.titulo FROM cxp_prov_grupos "
                        +"JOIN ("+sql_busqueda+") AS sbt ON sbt.id = cxp_prov_grupos.id "
                        +"order by "+orderBy+" "+asc+" limit ? OFFSET ?";
        
        //System.out.println("Busqueda GetPage: "+sql_to_query);
        ArrayList<HashMap<String, Object>> hm = (ArrayList<HashMap<String, Object>>) this.jdbcTemplate.query(
            sql_to_query, 
            new Object[]{new String(data_string), new Integer(pageSize),new Integer(offset)}, new RowMapper() {
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    HashMap<String, Object> row = new HashMap<String, Object>();
                    row.put("id",rs.getInt("id"));
                    row.put("titulo",rs.getString("titulo"));
                    return row;
                }
            }
        );
        return hm; 
    }

    @Override
    public ArrayList<HashMap<String, String>> getProveedoresGrupos_Datos(Integer id) {
        String sql_query = "SELECT id, titulo FROM cxp_prov_grupos WHERE id = ?;";
        ArrayList<HashMap<String, String>> hm = (ArrayList<HashMap<String, String>>) this.jdbcTemplate.query(
            sql_query,  
            new Object[]{new Integer(id)}, new RowMapper() {
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
    
    
    
    
    
    
    //metodos para aplicativo de facturas de proveedores
    @Override
    public ArrayList<HashMap<String, Object>> getProvFacturas_PaginaGrid(String data_string, int offset, int pageSize, String orderBy, String asc) {
        String sql_busqueda = "select id from gral_bus_catalogos(?) as foo (id integer)";
        String sql_to_query = "SELECT cxp_facturas.id, "
                                + "cxp_facturas.serie_folio AS factura, "
                                + "(case when cxp_facturas.tipo_factura_proveedor=1 then 'COMPRAS' when cxp_facturas.tipo_factura_proveedor=2 then 'SERVICIOS U HONORARIOS' when cxp_facturas.tipo_factura_proveedor=3 then 'OTROS INSUMOS' when cxp_facturas.tipo_factura_proveedor=4 then 'FLETES' else '' end) as tipo,"
                                + "cxp_prov.razon_social AS proveedor, "
                                + "gral_mon.descripcion_abr AS moneda, "
                                + "cxp_facturas.monto_total AS total, "
                                + "to_char(cxp_facturas.fecha_factura,'dd/mm/yyyy') AS fecha_factura, "
                                + "(CASE WHEN cxp_facturas.cancelacion=FALSE THEN '' ELSE 'CANCELADO' END) AS estado " 
                        + "FROM cxp_facturas "
                        + "JOIN cxp_prov ON cxp_prov.id=cxp_facturas.cxc_prov_id "
                        + "JOIN gral_mon ON gral_mon.id=cxp_facturas.moneda_id " 
                        +"JOIN ("+sql_busqueda+") as subt on subt.id=cxp_facturas.id "
                        + "ORDER  BY "+orderBy+" "+asc+" LIMIT ? OFFSET ?";
                        
        //System.out.println("sql_to_query: "+sql_to_query);
        
        ArrayList<HashMap<String, Object>> hm = (ArrayList<HashMap<String, Object>>) this.jdbcTemplate.query(
            sql_to_query, 
            new Object[]{data_string,new Integer(pageSize),new Integer(offset)}, new RowMapper() {
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    HashMap<String, Object> row = new HashMap<String, Object>();
                    row.put("id",String.valueOf(rs.getInt("id")));
                    row.put("factura",rs.getString("factura"));
                    row.put("tipo",rs.getString("tipo"));
                    row.put("proveedor",rs.getString("proveedor"));
                    row.put("moneda",rs.getString("moneda"));
                    row.put("total",StringHelper.AgregaComas(StringHelper.roundDouble(rs.getString("total"),2)));
                    row.put("fecha_factura",rs.getString("fecha_factura"));
                    row.put("estado",rs.getString("estado"));
                    return row;
                }
            }
        );
        return hm; 
    }
    
    
    
    
    @Override
    public ArrayList<HashMap<String, String>> getImpuestos() {
        String sql_to_query = "SELECT id, descripcion, iva_1 FROM gral_imptos WHERE borrado_logico=FALSE;";
        //log.log(Level.INFO, "Ejecutando query de {0}", sql_to_query);
        ArrayList<HashMap<String, String>> hm_ivas = (ArrayList<HashMap<String, String>>) this.jdbcTemplate.query(
            sql_to_query,
            new Object[]{}, new RowMapper(){
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    HashMap<String, String> row = new HashMap<String, String>();
                    row.put("id",String.valueOf(rs.getInt("id")));
                    row.put("descripcion",rs.getString("descripcion"));
                    row.put("iva_1",StringHelper.roundDouble(rs.getString("iva_1"),2));
                    return row;
                }
            }
        );
        return hm_ivas;
    }
    
    
    
    
    //obtiene valor del impuesto. retorna 0.16 o 0.11
    @Override
    public ArrayList<HashMap<String, String>> getValoriva(Integer id_sucursal) {
        String sql_to_query = ""
                + "SELECT "
                    + "gral_imptos.id AS id_impuesto, "
                    + "gral_imptos.iva_1 AS valor_impuesto "
                + "FROM gral_suc "
                + "JOIN gral_imptos ON gral_imptos.id=gral_suc.gral_impto_id "
                + "WHERE gral_imptos.borrado_logico=FALSE AND gral_suc.id=?";
        
        //log.log(Level.INFO, "Ejecutando query de {0}", sql_to_query);
        ArrayList<HashMap<String, String>> hm_valoriva = (ArrayList<HashMap<String, String>>) this.jdbcTemplate.query(
            sql_to_query, new Object[]{new Integer(id_sucursal)}, new RowMapper(){
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    HashMap<String, String> row = new HashMap<String, String>();
                    row.put("id_impuesto",rs.getString("id_impuesto"));
                    row.put("valor_impuesto",StringHelper.roundDouble(rs.getString("valor_impuesto"),2));
                    return row;
                }
            }
        );
        return hm_valoriva;
    }
    
    
    
    
    
    
    
    @Override
    public ArrayList<HashMap<String, String>> getTasaFletes() {
        String sql_to_query = "SELECT DISTINCT valor FROM erp_parametros_generales WHERE variable = 'tasa_retencion_fletes' LIMIT 1;";
        //log.log(Level.INFO, "Ejecutando query de {0}", sql_to_query);
        ArrayList<HashMap<String, String>> hm_tasa_fletes = (ArrayList<HashMap<String, String>>) this.jdbcTemplate.query(
            sql_to_query,
            new Object[]{}, new RowMapper(){
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    HashMap<String, String> row = new HashMap<String, String>();
                    row.put("valor",StringHelper.roundDouble(rs.getString("valor"),2));
                    return row;
                }
            }
        );
        return hm_tasa_fletes;
    }
    
    
    
    @Override
    public ArrayList<HashMap<String, String>> getTasaRetencionIsr(Integer IdEmpresa) {
        String sql_to_query = "SELECT DISTINCT tasa_retencion FROM gral_emp WHERE id=? LIMIT 1;";
        //log.log(Level.INFO, "Ejecutando query de {0}", sql_to_query);
        ArrayList<HashMap<String, String>> hm_tasa_ret_isr = (ArrayList<HashMap<String, String>>) this.jdbcTemplate.query(
            sql_to_query,
            new Object[]{new Integer(IdEmpresa)}, new RowMapper(){
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    HashMap<String, String> row = new HashMap<String, String>();
                    row.put("tasa_ret_isr",StringHelper.roundDouble(rs.getString("tasa_retencion"),2));
                    return row;
                }
            }
        );
        return hm_tasa_ret_isr;
    }
    
    
    @Override
    public ArrayList<HashMap<String, String>> getFleteras(Integer id_empresa, Integer id_sucursal) {
        String sql_to_query = "SELECT id,razon_social FROM cxp_prov_fleteras WHERE borrado_logico = false"
                + " AND empresa_id="+id_empresa+" AND sucursal_id="+id_sucursal+";";
        //log.log(Level.INFO, "Ejecutando query de {0}", sql_to_query);
        ArrayList<HashMap<String, String>> hm_fleteras = (ArrayList<HashMap<String, String>>) this.jdbcTemplate.query(
            sql_to_query,
            new Object[]{}, new RowMapper(){
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    HashMap<String, String> row = new HashMap<String, String>();
                    row.put("id",String.valueOf(rs.getInt("id")));
                    row.put("razon_social",rs.getString("razon_social"));
                    return row;
                }
            }
        );
        return hm_fleteras;
    }
    
    
    @Override
    public ArrayList<HashMap<String, String>> getProvFacturas_Datos(Integer id) {
        String sql_to_query = ""
        + "SELECT cxp_facturas.id, "
            + "cxp_facturas.cxc_prov_id, "
            + "cxp_facturas.serie_folio as factura, "
            + "cxp_facturas.flete, "
            + "cxp_facturas.subtotal, "
            + "cxp_facturas.iva, "
            + "cxp_facturas.retencion, "
            + "cxp_facturas.monto_total as total, "
            + "cxp_facturas.moneda_id, "
            + "cxp_facturas.tipo_cambio, "
            + "to_char(cxp_facturas.fecha_factura,'yyyy-mm-dd' ) as fecha_factura,"
            + "(CASE WHEN cxp_facturas.cancelacion=FALSE THEN 0 ELSE 1 END) AS cancelado, "
            + "cxp_facturas.empresa_id, "
            + "cxp_facturas.numero_guia, "
            + "cxp_facturas.orden_compra, "
            + "cxp_facturas.observaciones, "
            + "cxp_facturas.fletera_id, "
            + "cxp_facturas.dias_credito_id, "
            + "cxp_facturas.tipo_factura_proveedor, "
            + "(CASE WHEN cxp_facturas.cancelacion=FALSE THEN '' ELSE 'CANCELADO' END) AS estado, " 
            + "cxp_prov.rfc, "
            + "cxp_prov.razon_social, "
            + "cxp_prov.folio as no_prov, "
            /*
            + "cxp_prov.calle||' '||cxp_prov.numero||', '||cxp_prov.colonia||', '||gral_mun.titulo||', '||gral_edo.titulo||', '||gral_pais.titulo ||' C.P. '||cxp_prov.cp as direccion, "
            + "cxp_prov.calle,"
            + "cxp_prov.numero,"
            + "cxp_prov.colonia,"
            + "gral_mun.titulo AS municipio,"
            + "gral_edo.titulo AS estado,"
            + "gral_pais.titulo AS pais,"
            + "cxp_prov.telefono1 as telefono, "
            + "cxp_prov.cp, "
            */
            + "cxp_prov.proveedortipo_id,"
            + "cxp_prov.impuesto as iva_id "
        + "FROM cxp_facturas "
        + "left join cxp_prov on cxp_prov.id=cxp_facturas.cxc_prov_id "
        + "JOIN gral_pais ON gral_pais.id=cxp_prov.pais_id "
        + "JOIN gral_edo ON gral_edo.id=cxp_prov.estado_id "
        + "JOIN gral_mun ON gral_mun.id=cxp_prov.municipio_id "
        +"WHERE cxp_facturas.id="+ id + ";";
        
        //log.log(Level.INFO, "Ejecutando query de {0}", sql_to_query);
        ArrayList<HashMap<String, String>> hm_datos_entrada = (ArrayList<HashMap<String, String>>) this.jdbcTemplate.query(
            sql_to_query,
            new Object[]{}, new RowMapper(){
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    HashMap<String, String> row = new HashMap<String, String>();
                    row.put("id",String.valueOf(rs.getInt("id")));
                    row.put("empresa_id",String.valueOf(rs.getInt("empresa_id")));
                    row.put("fletera_id",String.valueOf(rs.getInt("fletera_id")));
                    row.put("dias_credito_id",String.valueOf(rs.getInt("dias_credito_id")));
                    row.put("tipo_factura_proveedor",String.valueOf(rs.getInt("tipo_factura_proveedor")));
                    row.put("factura",rs.getString("factura"));
                    row.put("moneda_id",rs.getString("moneda_id"));
                    row.put("fecha_factura",rs.getString("fecha_factura"));
                    row.put("numero_guia",rs.getString("numero_guia"));
                    row.put("orden_compra",rs.getString("orden_compra"));
                    row.put("observaciones",rs.getString("observaciones"));
                    row.put("tipo_cambio",StringHelper.roundDouble(rs.getString("tipo_cambio"),4));
                    row.put("flete",StringHelper.roundDouble(rs.getString("flete"),2));
                    row.put("subtotal",StringHelper.roundDouble(rs.getString("subtotal"),2));
                    row.put("iva",StringHelper.roundDouble(rs.getString("iva"),2));
                    row.put("retencion",StringHelper.roundDouble(rs.getString("retencion"),2));
                    row.put("total",StringHelper.roundDouble(rs.getString("total"),2));
                    row.put("estado",rs.getString("estado"));
                    row.put("cancelado",rs.getString("cancelado"));
                    
                    row.put("cxc_prov_id",String.valueOf(rs.getInt("cxc_prov_id")));
                    row.put("rfc",rs.getString("rfc"));
                    row.put("razon_social",rs.getString("razon_social"));
                    row.put("no_prov",rs.getString("no_prov"));
                    row.put("proveedortipo_id",rs.getString("proveedortipo_id"));
                    row.put("iva_id",rs.getString("iva_id"));
                    return row;
                }
            }
        );
        return hm_datos_entrada; 
    }
    
    
    @Override
    public ArrayList<HashMap<String, String>> getProvFacturas_DatosProveedor(Integer id_proveedor) {
        String sql_to_query = "SELECT DISTINCT  cxp_prov.id, "
                                    + "cxp_prov.rfc, "
                                    + "cxp_prov.razon_social, "
                                    + "cxp_prov.calle||' '||cxp_prov.numero||', '||cxp_prov.colonia||', '||gral_mun.titulo||', '||gral_edo.titulo||', '||gral_pais.titulo ||' C.P. '||cxp_prov.cp as direccion, "
                                    + "cxp_prov.calle,"
                                    + "cxp_prov.numero,"
                                    + "cxp_prov.colonia,"
                                    + "gral_mun.titulo AS municipio,"
                                    + "gral_edo.titulo AS estado,"
                                    + "gral_pais.titulo AS pais,"
                                    + "cxp_prov.telefono1 as telefono, "
                                    + "cxp_prov.cp, "
                                    + "cxp_prov.proveedortipo_id  "
                            + "FROM cxp_prov "
                            + "JOIN gral_pais ON gral_pais.id = cxp_prov.pais_id "
                            + "JOIN gral_edo ON gral_edo.id = cxp_prov.estado_id "
                            + "JOIN gral_mun ON gral_mun.id = cxp_prov.municipio_id "
                            + "WHERE cxp_prov.id="+ id_proveedor;
        
        //log.log(Level.INFO, "Ejecutando query de {0}", sql_to_query);
        ArrayList<HashMap<String, String>> hm_datos_proveedor = (ArrayList<HashMap<String, String>>) this.jdbcTemplate.query(
            sql_to_query,
            new Object[]{}, new RowMapper(){
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    HashMap<String, String> row = new HashMap<String, String>();
                    row.put("id",String.valueOf(rs.getInt("id")));
                    row.put("rfc",rs.getString("rfc"));
                    row.put("razon_social",rs.getString("razon_social"));
                    row.put("rfc",rs.getString("rfc"));
                    row.put("calle",rs.getString("calle"));
                    row.put("numero",rs.getString("numero"));
                    row.put("colonia",rs.getString("colonia"));
                    row.put("municipio",rs.getString("municipio"));
                    row.put("estado",rs.getString("estado"));
                    row.put("pais",rs.getString("pais"));
                    row.put("cp",rs.getString("cp"));
                    row.put("telefono",rs.getString("telefono"));
                    row.put("proveedortipo_id",rs.getString("proveedortipo_id"));
                    return row;
                }
            }
        );
        return hm_datos_proveedor;
    }
    
    
    
    @Override
    public ArrayList<HashMap<String, String>> getProvFacturas_DatosGrid(Integer id) {
        String sql_to_query = ""
        + "SELECT "
            + "cxp_fac_det.id as det_id, "
            + "cxp_fac_det.codigo_producto, "
            + "cxp_fac_det.descripcion, "
            + "cxp_fac_det.unidad_medida, "
            + "cxp_fac_det.presentacion, "
            + "cxp_fac_det.cantidad, "
            + "cxp_fac_det.costo_unitario, "
            + "(cxp_fac_det.cantidad * cxp_fac_det.costo_unitario) AS importe, "
            + "cxp_fac_det.gral_imp_id, "
            + "cxp_fac_det.valor_imp, "
            + "(((cxp_fac_det.cantidad * cxp_fac_det.costo_unitario::double precision) + (CASE WHEN cxp_fac_det.gral_ieps_id>0 THEN ((cxp_fac_det.costo_unitario * cxp_fac_det.cantidad::double precision) * cxp_fac_det.valor_ieps::double precision) ELSE 0 END)) * cxp_fac_det.valor_imp) as iva_importe, "
            + "cxp_fac_det.gral_ieps_id,"
            + "cxp_fac_det.valor_ieps,"
            + "(CASE WHEN cxp_fac_det.gral_ieps_id>0 THEN ((cxp_fac_det.costo_unitario * cxp_fac_det.cantidad::double precision) * cxp_fac_det.valor_ieps::double precision) ELSE 0 END) AS importe_ieps "
        + "FROM cxp_facturas_detalle as cxp_fac_det "
        + "WHERE cxp_fac_det.cxp_facturas_id="+ id + " ORDER BY cxp_fac_det.id;";
        
        //log.log(Level.INFO, "Ejecutando query de {0}", sql_to_query);
        ArrayList<HashMap<String, String>> hm_datos_entrada = (ArrayList<HashMap<String, String>>) this.jdbcTemplate.query(
            sql_to_query,
            new Object[]{}, new RowMapper(){
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    HashMap<String, String> row = new HashMap<String, String>();
                    row.put("det_id",rs.getString("det_id"));
                    row.put("codigo_producto",rs.getString("codigo_producto"));
                    row.put("descripcion",rs.getString("descripcion"));
                    row.put("unidad_medida",rs.getString("unidad_medida"));
                    row.put("presentacion",rs.getString("presentacion"));
                    row.put("cantidad",StringHelper.roundDouble(rs.getString("cantidad"),4));
                    row.put("costo_unitario",StringHelper.roundDouble(rs.getString("costo_unitario"),4));
                    row.put("importe",StringHelper.roundDouble(rs.getString("importe"),4));
                    row.put("ieps_id",rs.getString("gral_ieps_id"));
                    row.put("valor_ieps",StringHelper.roundDouble(rs.getString("valor_ieps"),4));
                    row.put("importe_ieps",StringHelper.roundDouble(rs.getString("importe_ieps"),4));
                    row.put("gral_imp_id",rs.getString("gral_imp_id"));
                    row.put("valor_imp",StringHelper.roundDouble(rs.getString("valor_imp"),2));
                    row.put("iva_importe",StringHelper.roundDouble(rs.getString("iva_importe"),4));

                    return row;
                }
            }
        );
        return hm_datos_entrada;  
    }
    
    
    
    @Override
    public ArrayList<HashMap<String, String>> getProvFacturas_BuscaRemisiones(String folio_remision, String folio_entrada, String proveedor, Integer id_empresa, Integer id_sucursal) {
        String where="";
	if(folio_remision.equals("") == false){
            where=" AND com_fac.factura ILIKE '%"+folio_remision+"%'";
	}
	if(folio_entrada.equals("") == false){
            where=" AND com_fac.no_entrada ILIKE '%"+folio_entrada+"%'";
	}
	if(proveedor.equals("") == false){
            where=" AND cxp_prov.razon_social ILIKE '%"+proveedor+"%'";
	}
	
	String sql_query = "SELECT com_fac.id, "
                    + "com_fac.factura as folio_remision, "
                    + "to_char(com_fac.factura_fecha_expedicion,'dd/mm/yyyy') AS fecha_remision, "
                    + "cxp_prov.razon_social AS proveedor "
                + "FROM com_fac "
                + "JOIN cxp_prov ON cxp_prov.id=com_fac.proveedor_id "
                + "WHERE com_fac.tipo_documento=2 "
                + "AND com_fac.borrado_logico=FALSE "
                + "AND com_fac.cancelacion=FALSE "
                + "AND com_fac.empresa_id="+id_empresa+"  "
                + "AND com_fac.sucursal_id="+id_sucursal+"  "+where;
        
        ArrayList<HashMap<String, String>> hm = (ArrayList<HashMap<String, String>>) this.jdbcTemplate.query(
            sql_query,  
            new Object[]{}, new RowMapper() {
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    HashMap<String, String> row = new HashMap<String, String>();
                    row.put("id",String.valueOf(rs.getInt("id")));
                    row.put("folio_remision",rs.getString("folio_remision"));
                    row.put("fecha_remision",rs.getString("fecha_remision"));
                    row.put("proveedor",rs.getString("proveedor"));
                    return row;
                }
            }
        );
        return hm;
    }
    
    
    @Override
    public ArrayList<HashMap<String, String>> getProvFacturas_DiasCredito() {
        String sql_to_query = "SELECT id,descripcion FROM cxp_prov_credias ORDER BY id;";
        //log.log(Level.INFO, "Ejecutando query de {0}", sql_to_query);
        ArrayList<HashMap<String, String>> hm_fleteras = (ArrayList<HashMap<String, String>>) this.jdbcTemplate.query(
            sql_to_query,
            new Object[]{}, new RowMapper(){
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    HashMap<String, String> row = new HashMap<String, String>();
                    row.put("id",String.valueOf(rs.getInt("id")));
                    row.put("descripcion",rs.getString("descripcion"));
                    return row;
                }
            }
        );
        return hm_fleteras;
    }
    
    
    
    
    
    @Override
    public ArrayList<HashMap<String, String>> getProvFacturas_DatosRemision(Integer id) {
        String sql_to_query = "SELECT com_fac.id, "
                                    + "com_fac.proveedor_id, "
                                    + "com_fac.factura  AS folio_remision, "
                                    + "com_fac.factura_fecha_expedicion AS fecha_remision, "
                                    + "com_fac.moneda_id, "
                                    + "com_fac.tipo_de_cambio as tipo_cambio, "
                                    + "com_fac.fletera_id, "
                                    + "com_fac.numero_guia, "
                                    + "com_fac.orden_de_compra as orden_compra, "
                                    + "com_fac.observaciones, "
                                    + "com_fac.flete, "
                                    + "com_fac.subtotal, "
                                    + "com_fac.iva, "
                                    + "com_fac.retencion, "
                                    + "com_fac.total, "
                                    + "cxp_prov.dias_credito_id  "
                                + "FROM com_fac  "
                                + "LEFT JOIN cxp_prov ON cxp_prov.id=com_fac.proveedor_id "
                                + "where com_fac.id="+ id + ";";
        
        //log.log(Level.INFO, "Ejecutando query de {0}", sql_to_query);
        ArrayList<HashMap<String, String>> hm_datos_entrada = (ArrayList<HashMap<String, String>>) this.jdbcTemplate.query(
            sql_to_query,
            new Object[]{}, new RowMapper(){
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    HashMap<String, String> row = new HashMap<String, String>();
                    row.put("id",String.valueOf(rs.getInt("id")));
                    row.put("proveedor_id",String.valueOf(rs.getInt("proveedor_id")));
                    row.put("folio_remision",rs.getString("folio_remision"));
                    row.put("fecha_remision",rs.getString("fecha_remision"));
                    row.put("moneda_id",String.valueOf(rs.getInt("moneda_id")));
                    row.put("fletera_id",rs.getString("fletera_id"));
                    row.put("numero_guia",rs.getString("numero_guia"));
                    row.put("orden_compra",rs.getString("orden_compra"));
                    row.put("observaciones",rs.getString("observaciones"));
                    row.put("tipo_cambio",StringHelper.roundDouble(rs.getString("tipo_cambio"),4));
                    row.put("flete",StringHelper.roundDouble(rs.getString("flete"),2));
                    row.put("subtotal",StringHelper.roundDouble(rs.getString("subtotal"),2));
                    row.put("iva",StringHelper.roundDouble(rs.getString("iva"),2));
                    row.put("retencion",StringHelper.roundDouble(rs.getString("retencion"),2));
                    row.put("total",StringHelper.roundDouble(rs.getString("total"),2));
                    row.put("dias_credito_id",String.valueOf(rs.getInt("dias_credito_id")));
                    return row;
                }
            }
        );
        return hm_datos_entrada; 
    }
    
    
    
    //Lista de productos de la remision
    @Override
    public ArrayList<HashMap<String, String>> getProvFacturas_DatosGridRemision(Integer id) {
        String sql_to_query = ""
        + "SELECT inv_prod.sku AS codigo_producto, "
                + "inv_prod.descripcion, "
                + "inv_prod_unidades.titulo AS unidad_medida, "
                + "inv_prod_presentaciones.titulo AS presentacion, "
                + "com_fac_detalle.cantidad, "
                + "com_fac_detalle.costo_unitario, "
                + "(com_fac_detalle.cantidad * com_fac_detalle.costo_unitario) AS importe, "
                + "com_fac_detalle.tipo_de_impuesto_sobre_partida AS tipo_impuesto, "
                + "com_fac_detalle.valor_imp, "
                + "com_fac_detalle.gral_ieps_id,"
                + "com_fac_detalle.valor_ieps,"
                + "(CASE WHEN com_fac_detalle.gral_ieps_id>0 THEN ((com_fac_detalle.costo_unitario * com_fac_detalle.cantidad) * com_fac_detalle.valor_ieps) ELSE 0 END) AS importe_ieps "
        + "FROM com_fac_detalle  "
        + "LEFT JOIN inv_prod ON inv_prod.id=com_fac_detalle.producto_id "
        + "LEFT JOIN inv_prod_unidades ON inv_prod_unidades.id=inv_prod.unidad_id "
        + "LEFT JOIN inv_prod_presentaciones ON inv_prod_presentaciones.id=com_fac_detalle.presentacion_id "
        + "WHERE com_fac_detalle.com_fac_id="+ id + " ORDER BY com_fac_detalle.id;";
        
        //log.log(Level.INFO, "Ejecutando query de {0}", sql_to_query);
        ArrayList<HashMap<String, String>> hm_datos_entrada = (ArrayList<HashMap<String, String>>) this.jdbcTemplate.query(
            sql_to_query,
            new Object[]{}, new RowMapper(){
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    HashMap<String, String> row = new HashMap<String, String>();
                    row.put("codigo_producto",rs.getString("codigo_producto"));
                    row.put("descripcion",rs.getString("descripcion"));
                    row.put("unidad_medida",rs.getString("unidad_medida"));
                    row.put("presentacion",rs.getString("presentacion"));
                    row.put("tipo_impuesto",rs.getString("tipo_impuesto"));
                    row.put("cantidad",StringHelper.roundDouble(rs.getString("cantidad"),2));
                    row.put("costo_unitario",StringHelper.roundDouble(rs.getString("costo_unitario"),2));
                    row.put("importe",StringHelper.roundDouble(rs.getString("importe"),2));
                    row.put("valor_imp",StringHelper.roundDouble(rs.getString("valor_imp"),2));
                    
                    row.put("ieps_id",rs.getString("gral_ieps_id"));
                    row.put("valor_ieps",StringHelper.roundDouble(rs.getString("valor_ieps"),4));
                    row.put("importe_ieps",StringHelper.roundDouble(rs.getString("importe_ieps"),4));
                    
                    return row;
                }
            }
        );
        return hm_datos_entrada;  
    }
    
    
    
    //obtiene datos para el buscador de proveedores
    @Override
    public ArrayList<HashMap<String, String>> getProvFacturas_BuscadorProveedores(String rfc, String noProveedor, String razon_social, Integer id_empresa) {
        String where = "";
        
	if(!rfc.equals("")){
            where=" AND cxp_prov.rfc ILIKE '%"+rfc.toUpperCase()+"%'";
	}

	if(!noProveedor.equals("")){
            where +=" AND cxp_prov.folio ILIKE '%"+noProveedor.toUpperCase()+"%'";
	}

	if(!razon_social.equals("")){
            where +=" AND (cxp_prov.razon_social ilike '%"+razon_social.toUpperCase()+"%' OR cxp_prov.clave_comercial ilike '%"+razon_social.toUpperCase()+"%')";
	}

        String sql_to_query = ""
        + "SELECT DISTINCT  "
            + "cxp_prov.id, "
            + "cxp_prov.rfc, "
            + "cxp_prov.folio AS no_proveedor, "
            + "cxp_prov.razon_social, "
            + "cxp_prov.calle||' '||cxp_prov.numero||', '|| cxp_prov.colonia||', '||(CASE WHEN gral_mun.titulo IS NULL THEN '' ELSE gral_mun.titulo END)||', '||(CASE WHEN gral_edo.titulo IS NULL THEN '' ELSE gral_edo.titulo END)||', '||(CASE WHEN gral_pais.titulo IS NULL THEN '' ELSE gral_pais.titulo END) ||' C.P. '||cxp_prov.cp as direccion, "
            + "cxp_prov.proveedortipo_id,"
            + "cxp_prov.dias_credito_id,  "
            + "cxp_prov.moneda_id, "
            + "cxp_prov.impuesto AS iva_id,"
            + "(CASE WHEN gral_imptos.iva_1 is null THEN 0 ELSE gral_imptos.iva_1 END) AS iva_tasa "
        + "FROM cxp_prov "
        + "JOIN gral_pais ON gral_pais.id = cxp_prov.pais_id "
        + "JOIN gral_edo ON gral_edo.id = cxp_prov.estado_id "
        + "JOIN gral_mun ON gral_mun.id = cxp_prov.municipio_id  "
        + "LEFT JOIN gral_imptos ON gral_imptos.id=cxp_prov.impuesto "
        + "WHERE empresa_id="+id_empresa+" AND cxp_prov.borrado_logico=false "+where;
        //log.log(Level.INFO, "Ejecutando query de {0}", sql_to_query);
        
        ArrayList<HashMap<String, String>> hm_datos_proveedor = (ArrayList<HashMap<String, String>>) this.jdbcTemplate.query(
            sql_to_query,
            new Object[]{}, new RowMapper(){
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    HashMap<String, String> row = new HashMap<String, String>();
                    row.put("id",String.valueOf(rs.getInt("id")));
                    row.put("no_proveedor",rs.getString("no_proveedor"));
                    row.put("rfc",rs.getString("rfc"));
                    row.put("razon_social",rs.getString("razon_social"));
                    row.put("direccion",rs.getString("direccion"));
                    row.put("proveedortipo_id",rs.getString("proveedortipo_id"));
                    row.put("dias_credito_id",String.valueOf(rs.getInt("dias_credito_id")));
                    row.put("moneda_id",String.valueOf(rs.getInt("moneda_id")));
                    row.put("iva_id",String.valueOf(rs.getInt("iva_id")));
                    row.put("iva_tasa",StringHelper.roundDouble(String.valueOf(rs.getDouble("iva_tasa")),2));
                    return row;
                }
            }
        );
        return hm_datos_proveedor;  
    }
    
    
    
    //buscar datos por Numero de Proveedor
    @Override
    public ArrayList<HashMap<String, String>> getDatosProveedorByNoProv(String noProveedor, Integer id_empresa) {
        
        String sql_to_query = ""
        + "SELECT DISTINCT  "
            + "cxp_prov.id, "
            + "cxp_prov.rfc, "
            + "cxp_prov.folio AS no_proveedor, "
            + "cxp_prov.razon_social, "
            + "cxp_prov.calle||' '||cxp_prov.numero||', '|| cxp_prov.colonia||', '||(CASE WHEN gral_mun.titulo IS NULL THEN '' ELSE gral_mun.titulo END)||', '||(CASE WHEN gral_edo.titulo IS NULL THEN '' ELSE gral_edo.titulo END)||', '||(CASE WHEN gral_pais.titulo IS NULL THEN '' ELSE gral_pais.titulo END) ||' C.P. '||cxp_prov.cp as direccion, "
            + "cxp_prov.proveedortipo_id,"
            + "cxp_prov.dias_credito_id,  "
            + "cxp_prov.moneda_id,"
            + "cxp_prov.impuesto AS iva_id,"
            + "(CASE WHEN gral_imptos.iva_1 is null THEN 0 ELSE gral_imptos.iva_1 END) AS iva_tasa "
        + "FROM cxp_prov "
        + "LEFT JOIN gral_pais ON gral_pais.id = cxp_prov.pais_id "
        + "LEFT JOIN gral_edo ON gral_edo.id = cxp_prov.estado_id "
        + "LEFT JOIN gral_mun ON gral_mun.id = cxp_prov.municipio_id  "
        + "LEFT JOIN gral_imptos ON gral_imptos.id=cxp_prov.impuesto "
        + "WHERE empresa_id="+id_empresa+" "
        + "AND cxp_prov.borrado_logico=false "
        + "AND cxp_prov.folio='"+noProveedor.toUpperCase()+"' LIMIT 1;";

        //log.log(Level.INFO, "Ejecutando query de {0}", sql_to_query);
        ArrayList<HashMap<String, String>> hm = (ArrayList<HashMap<String, String>>) this.jdbcTemplate.query(
            sql_to_query,
            new Object[]{}, new RowMapper(){
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    HashMap<String, String> row = new HashMap<String, String>();
                    row.put("id",String.valueOf(rs.getInt("id")));
                    row.put("no_proveedor",rs.getString("no_proveedor"));
                    row.put("rfc",rs.getString("rfc"));
                    row.put("razon_social",rs.getString("razon_social"));
                    row.put("direccion",rs.getString("direccion"));
                    row.put("proveedortipo_id",rs.getString("proveedortipo_id"));
                    row.put("dias_credito_id",String.valueOf(rs.getInt("dias_credito_id")));
                    row.put("moneda_id",String.valueOf(rs.getInt("moneda_id")));
                    row.put("iva_id",String.valueOf(rs.getInt("iva_id")));
                    row.put("iva_tasa",StringHelper.roundDouble(String.valueOf(rs.getDouble("iva_tasa")),2));
                    return row;
                }
            }
        );
        return hm;
    }
    
    
    //obtiene el tipo de cambio de la fecha indicada
    @Override
    public ArrayList<HashMap<String, String>> getProvFacturas_TipoCambio(String fecha) {
        String sql_to_query = "SELECT valor FROM erp_monedavers WHERE  to_char(momento_creacion,'yyyy-mm-dd')<='"+fecha+"' AND moneda_id=2 ORDER BY momento_creacion DESC LIMIT 1;";
        
        //log.log(Level.INFO, "Ejecutando query de {0}", sql_to_query);
        ArrayList<HashMap<String, String>> hm_tc = (ArrayList<HashMap<String, String>>) this.jdbcTemplate.query(
            sql_to_query,
            new Object[]{}, new RowMapper(){
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    HashMap<String, String> row = new HashMap<String, String>();
                    row.put("valor",StringHelper.roundDouble(rs.getString("valor"),4));
                    return row;
                }
            }
        );
        return hm_tc;  
    }
    
    
    
    @Override
    public ArrayList<HashMap<String, String>> getProvFacturas_TiposCancelacion() {
        String sql_to_query = "SELECT id, titulo FROM cxp_facturas_tipos_cancelacion WHERE borrado_logico=false ORDER BY id;";
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
    
    
    
    
    
    @Override
    public ArrayList<HashMap<String, Object>> getProveedorestMovimientos_PaginaGrid(String data_string, int offset, int pageSize, String orderBy, String asc) {
        //throw new UnsupportedOperationException("Not supported yet.");
        String sql_busqueda = "select id from gral_bus_catalogos(?) as foo (id integer)";
        
	String sql_to_query = "SELECT cxp_mov_tipos.id, cxp_mov_tipos.titulo,cxp_mov_tipos.descripcion,gral_mon.descripcion_abr as moneda "
                               + " FROM cxp_mov_tipos "
                               + " JOIN gral_mon on gral_mon.id= cxp_mov_tipos.moneda_id "
                        +"JOIN ("+sql_busqueda+") AS sbt ON sbt.id = cxp_mov_tipos.id "
                        +"order by "+orderBy+" "+asc+" limit ? OFFSET ?";
        
        System.out.println("Busqueda GetPage: "+sql_to_query);
        ArrayList<HashMap<String, Object>> hm = (ArrayList<HashMap<String, Object>>) this.jdbcTemplate.query(
            sql_to_query, 
            new Object[]{new String(data_string), new Integer(pageSize),new Integer(offset)}, new RowMapper() {
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    HashMap<String, Object> row = new HashMap<String, Object>();
                    row.put("id",rs.getInt("id"));
                    row.put("titulo",rs.getString("titulo"));
                    row.put("descripcion",rs.getString("descripcion"));
                    row.put("moneda",rs.getString("moneda"));
                    return row;
                }
            }
        );
        return hm; 
    }

    @Override
    public ArrayList<HashMap<String, String>> getProveedorestMovimientos_Datos(Integer id) {
        //throw new UnsupportedOperationException("Not supported yet.");
        String sql_query = "SELECT id, titulo,descripcion,moneda_id  FROM cxp_mov_tipos WHERE id = ?;";
        ArrayList<HashMap<String, String>> hm = (ArrayList<HashMap<String, String>>) this.jdbcTemplate.query(
            sql_query,  
            new Object[]{new Integer(id)}, new RowMapper() {
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    HashMap<String, String> row = new HashMap<String, String>();
                    row.put("id",String.valueOf(rs.getInt("id")));
                    row.put("titulo",rs.getString("titulo"));
                    row.put("descripcion",rs.getString("descripcion"));
                    row.put("id_moneda",rs.getString("moneda_id"));
                    return row;
                }
            }
        );
        return hm;
    }
    
    
    
    @Override
    public ArrayList<HashMap<String, String>> getDatos_ReporteAntiguedadSaldosCxP(String proveedor, Integer id_empresa) {
        String sql_to_query = ""
                + "SELECT  "
                        + "clave_proveedor,  "
                        + "proveedor,  "
                        + "serie_folio,  "
                        + "moneda_factura,  "
                        + "simbolo_moneda,  "
                        + "fecha_factura,  "
                        + "fecha_vencimiento,  "
                        + "(CASE WHEN (por_vencer > 0) THEN simbolo_moneda ELSE '' END) AS moneda_por_vencer,  "
                        + "por_vencer,  "
                        + "(CASE WHEN (de_1_a_30_dias > 0) THEN simbolo_moneda ELSE '' END) AS moneda_1_a_30_dias,  "
                        + "de_1_a_30_dias, "
                        + "(CASE WHEN (de_31_a_60_dias > 0) THEN simbolo_moneda ELSE '' END) AS moneda_31_a_60_dias,  "
                        + "de_31_a_60_dias, "
                        + "(CASE WHEN (de_61_dias_en_adelante > 0) THEN simbolo_moneda ELSE '' END) AS moneda_61_dias_en_adelante, "
                        + "de_61_dias_en_adelante "
                + "FROM ("
                        + "SELECT "
                                + "clave_proveedor, "
                                + "proveedor, "
                                + "serie_folio, "
                                + "moneda_factura, "
                                + "simbolo_moneda, "
                                + "fecha_factura, "
                                + "fecha_vencimiento, "
                                + "dias_vencidos, "
                                + "(CASE WHEN (dias_vencidos <1) THEN saldo_factura ELSE 0 END) AS por_vencer, "
                                + "(CASE WHEN (dias_vencidos >=1 and dias_vencidos <=30) THEN saldo_factura ELSE 0 END) AS de_1_a_30_dias, "
                                + "(CASE WHEN (dias_vencidos >=31 and dias_vencidos <=60) THEN saldo_factura ELSE 0 END) AS de_31_a_60_dias, "
                                + "(CASE WHEN (dias_vencidos >=61) THEN saldo_factura ELSE 0 END) AS de_61_dias_en_adelante "
                        + "FROM ( "
                                + "SELECT  "
                                        + "cxp_prov.folio AS clave_proveedor, "
                                        + "cxp_prov.razon_social AS proveedor, "
                                        + "cxp_facturas.serie_folio, "
                                        + "gral_mon.descripcion_abr AS moneda_factura, "
                                        + "gral_mon.simbolo AS simbolo_moneda, "
                                        + "to_char(cxp_facturas.fecha_factura,'dd/mm/yyyy') as fecha_factura, "
                                        + "to_char(cxp_facturas.fecha_vencimiento,'dd/mm/yyyy') as fecha_vencimiento, "
                                        + "cxp_facturas.saldo_factura, "
                                        + "NOW()::DATE-cxp_facturas.fecha_vencimiento::DATE  AS dias_vencidos "
                                + "FROM cxp_facturas "
                                + "LEFT JOIN cxp_prov ON cxp_prov.id=cxp_facturas.cxc_prov_id "
                                + "JOIN gral_mon ON gral_mon.id=cxp_facturas.moneda_id "
                                + "WHERE cxp_facturas.pagado=FALSE  "
                                + "AND cxp_facturas.cancelacion=FALSE "
                                + "AND cxp_facturas.empresa_id="+id_empresa
                                + " AND cxp_prov.razon_social ILIKE '%"+proveedor+"%' "
                        + ") AS sbt "
                + ") AS sbt2 "
                + "ORDER BY clave_proveedor, moneda_factura, serie_folio";
        
        System.out.println("sql_to_query: "+sql_to_query);
        
        ArrayList<HashMap<String, String>> hm = (ArrayList<HashMap<String, String>>) this.jdbcTemplate.query(
                sql_to_query, 
            new Object[]{}, new RowMapper() {
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    HashMap<String, String> row = new HashMap<String, String>();
                    row.put("clave_proveedor",rs.getString("clave_proveedor"));
                    row.put("proveedor",rs.getString("proveedor"));
                    row.put("serie_folio",rs.getString("serie_folio"));
                    row.put("moneda_factura",rs.getString("moneda_factura"));
                    row.put("simbolo_moneda",rs.getString("simbolo_moneda"));
                    row.put("fecha_factura",rs.getString("fecha_factura"));
                    row.put("fecha_vencimiento",rs.getString("fecha_vencimiento"));
                    row.put("moneda_por_vencer",rs.getString("moneda_por_vencer"));
                    row.put("por_vencer",StringHelper.roundDouble(rs.getDouble("por_vencer"),2));
                    row.put("moneda_1_a_30_dias",rs.getString("moneda_1_a_30_dias"));
                    row.put("de_1_a_30_dias",StringHelper.roundDouble(rs.getDouble("de_1_a_30_dias"),2));
                    row.put("moneda_31_a_60_dias",rs.getString("moneda_31_a_60_dias"));
                    row.put("de_31_a_60_dias",StringHelper.roundDouble(rs.getDouble("de_31_a_60_dias"),2));
                    row.put("moneda_61_dias_en_adelante",rs.getString("moneda_61_dias_en_adelante"));
                    row.put("de_61_dias_en_adelante",StringHelper.roundDouble(rs.getDouble("de_61_dias_en_adelante"),2));
                    return row;
                }
            }
        );
        return hm; 
    }
    
    
    
    
    //aplicativo pagos a proveedores
    @Override
    public ArrayList<HashMap<String, Object>> getProveedoresPagos_PaginaGrid(String data_string, int offset, int pageSize, String orderBy, String asc) {
        String sql_busqueda = "select DISTINCT id from gral_bus_catalogos(?) as foo (id integer)";
        
	String sql_to_query = "SELECT cxp_pagos.id, "
                                    +"cxp_pagos.numero_transaccion, "
                                    +"cxp_pagos.monto_pago AS total, "
                                    +"cxp_prov.razon_social, "
                                    +"tes_mov_tipos.titulo AS forma_pago, "
                                    +"gral_mon.descripcion_abr AS moneda, "
                                    +"(CASE WHEN cxp_pagos.cancelacion=FALSE THEN '' ELSE 'CANCELADO' END) AS estado, "
                                    +"to_char(cxp_pagos.fecha_pago::timestamp with time zone,'dd/mm/yyyy') AS fecha_pago "
                            +"FROM cxp_pagos "
                            +"JOIN cxp_prov ON cxp_prov.id=cxp_pagos.cxp_prov_id "
                            +"JOIN tes_mov_tipos ON tes_mov_tipos.id=cxp_pagos.tes_mov_tipo_id "
                            +"JOIN gral_mon ON gral_mon.id=cxp_pagos.moneda_id " 
                            +"JOIN ("+sql_busqueda+") as subt on subt.id=cxp_pagos.id "
                            +"order by "+orderBy+" "+asc+" limit ? OFFSET ?";
        
        //System.out.println("Busqueda GetPage: "+sql_to_query);
        ArrayList<HashMap<String, Object>> hm = (ArrayList<HashMap<String, Object>>) this.jdbcTemplate.query(
            sql_to_query, 
            new Object[]{new String(data_string),new Integer(pageSize),new Integer(offset)}, new RowMapper() {
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    HashMap<String, Object> row = new HashMap<String, Object>();
                    row.put("id",String.valueOf(rs.getInt("id")));
                    row.put("numero_transaccion",rs.getString("numero_transaccion"));
                    row.put("total",StringHelper.AgregaComas(StringHelper.roundDouble(rs.getString("total"),2)));
                    row.put("razon_social",rs.getString("razon_social"));
                    row.put("forma_pago",rs.getString("forma_pago"));
                    row.put("moneda",rs.getString("moneda"));
                    row.put("estado",rs.getString("estado"));
                    row.put("fecha_pago",rs.getString("fecha_pago"));
                    return row;
                }
            }
        );
        return hm;
        
    }
    
    
    
    
    
    
    
    
    
    @Override
    public ArrayList<HashMap<String, String>> getProveedoresPagos_TipoMovimiento() {
        String sql_to_query = "SELECT id, titulo FROM cxp_pagos_tipo_movimiento WHERE borrado_logico=false ORDER BY id ASC;";
        //log.log(Level.INFO, "Ejecutando query de {0}", sql_to_query);
        ArrayList<HashMap<String, String>> hm_tm = (ArrayList<HashMap<String, String>>) this.jdbcTemplate.query(
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
        return hm_tm;
    }
    
    
    //este no se esta  utilizando
    @Override
    public ArrayList<HashMap<String, String>> getProveedoresPagos_TiposMovTesoreria(Integer id_empresa) {
        String sql_to_query = "SELECT id, titulo FROM  tes_mov_tipos WHERE borrado_logico=false  AND gral_emp_id="+id_empresa+" ORDER BY id ASC;";
        ArrayList<HashMap<String, String>> hm_forma_pago = (ArrayList<HashMap<String, String>>) this.jdbcTemplate.query(
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
        return hm_forma_pago;
    }
    
    
    
    @Override
    public ArrayList<HashMap<String, String>> getProveedoresPagos_FormasPago() {
        String sql_to_query = "SELECT id, titulo FROM  cxp_pagos_formas WHERE borrado_logico=false ORDER BY id ASC;";
        ArrayList<HashMap<String, String>> hm_forma_pago = (ArrayList<HashMap<String, String>>) this.jdbcTemplate.query(
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
        return hm_forma_pago;
    }
    
    
    
    @Override
    public ArrayList<HashMap<String, String>> getBancos(Integer id_empresa) {
        String sql_to_query = "SELECT distinct id, titulo FROM tes_ban "
                +"WHERE borrado_logico=false AND gral_emp_id="+id_empresa
                +" ORDER BY titulo ASC;";
                
        ArrayList<HashMap<String, String>> hm_bancos = (ArrayList<HashMap<String, String>>) this.jdbcTemplate.query(
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
        return hm_bancos;
    }
    
    
    
    @Override
    public ArrayList<HashMap<String, String>> getTipoCambioActual() {
        String sql_to_query = "Select valor from erp_monedavers where moneda_id=2 order by momento_creacion DESC limit 1;";
        //System.out.println("Buscando cuentas: "+sql_to_query);
        ArrayList<HashMap<String, String>> hm_tipoCambio = (ArrayList<HashMap<String, String>>) this.jdbcTemplate.query(
            sql_to_query,
            new Object[]{}, new RowMapper(){
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    HashMap<String, String> row = new HashMap<String, String>();
                    row.put("valor_tipo_cambio",StringHelper.roundDouble(rs.getString("valor"),4));
                    return row;
                }
            }
        );
        return hm_tipoCambio;
    }
    
    
    
    //obtiene los conceptos Bancarios
    @Override
    public ArrayList<HashMap<String, String>> getProveedoresPagos_Conceptos(Integer id_empresa) {
        String sql_to_query = "SELECT id, titulo FROM tes_con WHERE gral_emp_id="+id_empresa+" AND borrado_logico=FALSE;";
        //System.out.println("Buscando conceptos: "+sql_to_query);
        ArrayList<HashMap<String, String>> conceptos = (ArrayList<HashMap<String, String>>) this.jdbcTemplate.query(
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
        return conceptos;
    }
    
    
    //ontiene chequera de acuerdo al banco y la moneda
    @Override
    public ArrayList<HashMap<String, String>> getProveedoresPagos_Chequeras(Integer id_moneda, Integer id_banco) {
        String sql_to_query = "SELECT distinct id,titulo FROM tes_che WHERE borrado_logico=FALSE AND  tes_ban_id="+id_banco+" AND moneda_id="+id_moneda;
        //System.out.println("Buscando Chequeras: "+sql_to_query);
        ArrayList<HashMap<String, String>> hm_cuentas = (ArrayList<HashMap<String, String>>) this.jdbcTemplate.query(
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
        return hm_cuentas;
    }
    
    
    
    
    @Override
    public ArrayList<HashMap<String, String>> getProveedoresPagos_Facturas(Integer id_proveedor) {
        String sql_to_query = " "
                    + "SELECT "
                            + "cxp_facturas.serie_folio AS numero_factura,"
                            + "gral_mon.descripcion_abr AS denominacion_factura,"
                            + "cxp_facturas.monto_total AS monto_factura, "
                            + "(cxp_facturas.total_pagos + cxp_facturas.total_notas_creditos) AS monto_pagado, "
                            + "cxp_facturas.saldo_factura,"
                            + "to_char(cxp_facturas.fecha_factura,'dd/mm/yyyy') AS fecha_facturacion,"
                            + "(CASE WHEN cxp_facturas.fecha_ultimo_pago IS NULL THEN 'Sin efectuar' ELSE to_char(cxp_facturas.fecha_ultimo_pago,'dd/mm/yyyy') END) AS fecha_ultimo_pago "
                    + "FROM cxp_facturas "
                    + "JOIN gral_mon ON gral_mon.id=cxp_facturas.moneda_id "
                    + "WHERE cxp_facturas.cancelacion=FALSE "
                    + "AND cxp_facturas.pagado=FALSE "
                    + "AND cxc_prov_id ="+id_proveedor+" "
                    + "ORDER BY cxp_facturas.fecha_factura,cxp_facturas.serie_folio;";
        
        
        //System.out.println("Buscando facturas: "+sql_to_query);
        ArrayList<HashMap<String, String>> facturas = (ArrayList<HashMap<String, String>>) this.jdbcTemplate.query(
            sql_to_query,
            new Object[]{}, new RowMapper(){
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    HashMap<String, String> row = new HashMap<String, String>();
                    row.put("numero_factura",rs.getString("numero_factura"));
                    row.put("denominacion_factura",rs.getString("denominacion_factura"));
                    row.put("monto_factura",StringHelper.roundDouble(rs.getDouble("monto_factura"),2));
                    row.put("monto_pagado",StringHelper.roundDouble(rs.getDouble("monto_pagado"),2));
                    row.put("saldo_factura",StringHelper.roundDouble(rs.getDouble("saldo_factura"),2));
                    row.put("fecha_facturacion",rs.getString("fecha_facturacion"));
                    row.put("fecha_ultimo_pago",rs.getString("fecha_ultimo_pago"));
                    
                    return row;
                    
                }
            }
        );
        return facturas;
    }
    
    
    
    
    @Override
    public ArrayList<HashMap<String, String>> getProveedoresPagos_Datos(Integer id_pago) {
        String sql_to_query = " "
                    + "SELECT  "
                        + "cxp_pagos.id, "
                        + "cxp_pagos.numero_transaccion, "
                        + "cxp_pagos.cxp_prov_id, "
                        + "cxp_prov.folio AS no_proveedor, "
                        + "cxp_prov.rfc, "
                        + "cxp_prov.razon_social, "
                        + "cxp_pagos.moneda_id, "
                        + "cxp_pagos.tes_mov_tipo_id, "
                        + "cxp_pagos.fecha_pago, "
                        + "cxp_pagos.tipo_cambio, "
                        + "cxp_pagos.tes_ban_id, "
                        + "cxp_pagos.tes_che_id, "
                        + "(CASE WHEN tes_che.titulo IS NULL THEN '' ELSE tes_che.titulo END) AS no_chequera, "
                        + "cxp_pagos.numero_cheque, "
                        + "cxp_pagos.referencia, "
                        + "cxp_pagos.numero_tarjeta, "
                        + "cxp_pagos.monto_pago, "
                        + "cxp_pagos.cancelacion, "
                        + "cxp_pagos.observaciones,"
                        + "tes_mov.tes_con_id "
                + "FROM cxp_pagos "
                + "LEFT JOIN tes_mov ON tes_mov.id=cxp_pagos.tes_mov_id "
                + "JOIN cxp_prov ON cxp_prov.id=cxp_pagos.cxp_prov_id "
                + "LEFT JOIN tes_che ON tes_che.id=cxp_pagos.tes_che_id "
                + "WHERE cxp_pagos.id="+id_pago;
        
        //System.out.println("Buscando datos del Pago: "+sql_to_query);
        ArrayList<HashMap<String, String>> datos = (ArrayList<HashMap<String, String>>) this.jdbcTemplate.query(
            sql_to_query,
            new Object[]{}, new RowMapper(){
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    HashMap<String, String> row = new HashMap<String, String>();
                    row.put("id",String.valueOf(rs.getInt("id")));
                    row.put("numero_transaccion",rs.getString("numero_transaccion"));
                    row.put("cxp_prov_id",String.valueOf(rs.getInt("cxp_prov_id")));
                    row.put("no_proveedor",rs.getString("no_proveedor"));
                    row.put("rfc",rs.getString("rfc"));
                    row.put("razon_social",rs.getString("razon_social"));
                    row.put("moneda_id",String.valueOf(rs.getInt("moneda_id")));
                    row.put("tes_mov_tipo_id",String.valueOf(rs.getInt("tes_mov_tipo_id")));
                    row.put("fecha_pago",rs.getString("fecha_pago"));
                    row.put("tipo_cambio",StringHelper.roundDouble(rs.getDouble("tipo_cambio"),4));
                    row.put("tes_ban_id",String.valueOf(rs.getInt("tes_ban_id")));
                    row.put("tes_che_id",String.valueOf(rs.getInt("tes_che_id")));
                    row.put("no_chequera",rs.getString("no_chequera"));
                    row.put("numero_cheque",rs.getString("numero_cheque"));
                    row.put("referencia",rs.getString("referencia"));
                    row.put("numero_tarjeta",rs.getString("numero_tarjeta"));
                    row.put("monto_pago",StringHelper.roundDouble(rs.getDouble("monto_pago"),2));
                    row.put("cancelacion",String.valueOf(rs.getBoolean("cancelacion")));
                    row.put("observaciones",rs.getString("observaciones"));
                    row.put("tes_con_id",String.valueOf(rs.getInt("tes_con_id")));
                    return row;
                }
            }
        );
        return datos;
    }
    
    
    @Override
    public ArrayList<HashMap<String, String>> getProveedoresPagos_Detalles(Integer id_pago) {
        String sql_to_query = " "
                    + "SELECT "
                        + "cxp_pagos_detalles.serie_folio AS numero_factura,"
                        + "gral_mon.descripcion_abr AS denominacion_factura,"
                        + "cxp_facturas.monto_total AS monto_factura, "
                        + "(cxp_facturas.total_pagos + cxp_facturas.total_notas_creditos) AS monto_pagado, "
                        + "cxp_facturas.saldo_factura, "
                        + "cxp_pagos_detalles.cantidad AS cantidad_pago,"
                        + "cxp_pagos_detalles.tipo_cambio,"
                        + "to_char(cxp_facturas.fecha_factura,'dd/mm/yyyy') AS fecha_facturacion,"
                        + "(CASE WHEN cxp_facturas.fecha_ultimo_pago IS NULL THEN 'Sin efectuar' ELSE to_char(cxp_facturas.fecha_ultimo_pago,'dd/mm/yyyy') END) AS fecha_ultimo_pago "
                    + "FROM cxp_pagos_detalles "
                    + "JOIN cxp_facturas ON cxp_facturas.serie_folio=cxp_pagos_detalles.serie_folio "
                    + "JOIN gral_mon ON gral_mon.id=cxp_facturas.moneda_id "
                    + "WHERE cxp_pagos_detalles.cxp_pago_id="+id_pago+" "
                    + "ORDER BY cxp_facturas.fecha_factura, cxp_pagos_detalles.serie_folio;";
        
        
        //System.out.println("Buscando detalles del Pago: "+sql_to_query);
        ArrayList<HashMap<String, String>> detalles = (ArrayList<HashMap<String, String>>) this.jdbcTemplate.query(
            sql_to_query,
            new Object[]{}, new RowMapper(){
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    HashMap<String, String> row = new HashMap<String, String>();
                    row.put("numero_factura",rs.getString("numero_factura"));
                    row.put("denominacion_factura",rs.getString("denominacion_factura"));
                    row.put("monto_factura",StringHelper.roundDouble(rs.getDouble("monto_factura"),2));
                    row.put("monto_pagado",StringHelper.roundDouble(rs.getDouble("monto_pagado"),2));
                    row.put("saldo_factura",StringHelper.roundDouble(rs.getDouble("saldo_factura"),2));
                    row.put("cantidad_pago",StringHelper.roundDouble(rs.getDouble("cantidad_pago"),2));
                    row.put("tipo_cambio",StringHelper.roundDouble(rs.getDouble("tipo_cambio"),4));
                    row.put("fecha_facturacion",rs.getString("fecha_facturacion"));
                    row.put("fecha_ultimo_pago",rs.getString("fecha_ultimo_pago"));
                    return row;
                    
                }
            }
        );
        
        return detalles;
    }
    
    
    
    @Override
    public ArrayList<HashMap<String, String>> getProveedoresPagos_PagosAplicados(Integer id_pago, Integer id_proveedor) {
            String sql_to_query = ""
            + "SELECT cxp_prov.razon_social AS proveedor,"
                    + "cxp_pagos.numero_transaccion,"
                    + "to_char(cxp_pagos.fecha_pago::timestamp with time zone,'dd/mm/yyyy') AS fecha_pago,"
                    + "gral_mon.descripcion AS moneda_pago,"
                    + "gral_mon.simbolo AS simbolo_moneda_pago,"
                    + "cxp_pagos.monto_pago,"
                    + "tes_ban.titulo AS banco,"
                    + "tes_mov_tipos.titulo AS forma_pago,"
                    + "(CASE WHEN cxp_pagos.tes_mov_tipo_id=2 THEN cxp_pagos.numero_cheque ELSE cxp_pagos.referencia END ) AS cheque_referencia,"
                    + "cxp_pagos_detalles.serie_folio,"
                    + "cxp_fac.fecha_factura,"
                    + "cxp_fac.simbolo_moneda_factura,"
                    + "cxp_pagos_detalles.cantidad AS monto_aplicado, "
                    + "(CASE WHEN (cxp_pagos.moneda_id=1) THEN  "
                            //+ "(CASE WHEN (cxp_fac.moneda_id_factura=2) THEN cxp_pagos_detalles.cantidad * cxp_pagos_detalles.tipo_cambio  ELSE cxp_pagos_detalles.cantidad  END) "
                            + "(CASE WHEN (cxp_fac.moneda_id_factura!=1) THEN cxp_pagos_detalles.cantidad * cxp_pagos_detalles.tipo_cambio  ELSE cxp_pagos_detalles.cantidad  END) "
                    + "ELSE  "
                            + "(CASE WHEN (cxp_fac.moneda_id_factura=1) THEN  cxp_pagos_detalles.cantidad / cxp_pagos_detalles.tipo_cambio ELSE cxp_pagos_detalles.cantidad END) "
                    + "END) AS monto_aplicado_factura, "
                    + "cxp_pagos_detalles.tipo_cambio "
            + "FROM cxp_pagos "
            + "JOIN cxp_pagos_detalles ON cxp_pagos_detalles.cxp_pago_id=cxp_pagos.id "
            + "JOIN (	SELECT cxp_facturas.serie_folio, "
                            + "to_char(cxp_facturas.fecha_factura,'dd/mm/yyyy') AS fecha_factura, "
                            + "cxp_facturas.moneda_id AS moneda_id_factura, "
                            + "gral_mon.simbolo AS simbolo_moneda_factura "
                    + "FROM cxp_facturas LEFT JOIN gral_mon ON gral_mon.id=cxp_facturas.moneda_id "
                    + "WHERE cxp_facturas.cxc_prov_id="+id_proveedor +" "
            + ") AS cxp_fac ON cxp_fac.serie_folio = cxp_pagos_detalles.serie_folio "
            + "JOIN cxp_prov ON cxp_prov.id=cxp_pagos.cxp_prov_id "
            + "JOIN gral_mon ON gral_mon.id=cxp_pagos.moneda_id "
            + "LEFT JOIN tes_ban ON tes_ban.id=cxp_pagos.tes_ban_id "
            + "JOIN tes_mov_tipos ON tes_mov_tipos.id=cxp_pagos.tes_mov_tipo_id "
            + "WHERE cxp_pagos.id="+ id_pago;
            
        //System.out.println("Buscando datos para reporte aplicacion de pago: "+sql_to_query);
        ArrayList<HashMap<String, String>> hm = (ArrayList<HashMap<String, String>>) this.jdbcTemplate.query(
            sql_to_query,
            new Object[]{}, new RowMapper(){
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    HashMap<String, String> row = new HashMap<String, String>();
                    row.put("proveedor",rs.getString("proveedor"));
                    row.put("numero_transaccion",rs.getString("numero_transaccion"));
                    row.put("fecha_pago",rs.getString("fecha_pago"));
                    row.put("moneda_pago",rs.getString("moneda_pago"));
                    row.put("simbolo_moneda_pago",rs.getString("simbolo_moneda_pago"));
                    row.put("banco",rs.getString("banco"));
                    row.put("forma_pago",rs.getString("forma_pago"));
                    row.put("cheque_referencia",rs.getString("cheque_referencia"));
                    row.put("monto_pago",StringHelper.roundDouble(rs.getString("monto_pago"),2));
                    row.put("tipo_cambio",StringHelper.roundDouble(rs.getString("tipo_cambio"),4));
                    row.put("serie_folio",rs.getString("serie_folio"));
                    row.put("fecha_factura",rs.getString("fecha_factura"));
                    row.put("simbolo_moneda_factura",rs.getString("simbolo_moneda_factura"));
                    row.put("monto_aplicado",StringHelper.roundDouble(rs.getDouble("monto_aplicado"), 2));
                    row.put("monto_aplicado_factura",StringHelper.roundDouble(rs.getDouble("monto_aplicado_factura"), 2));
                    return row;
                }
            }
        );
        return hm; 
    }
    
    
    
    
    //catalogo de direcciones de proveedores
    @Override
    public ArrayList<HashMap<String, Object>> getProveedoresDirecciones_PaginaGrid(String data_string, int offset, int pageSize, String orderBy, String asc) {
        String sql_busqueda = "select id from gral_bus_catalogos(?) as foo (id integer)";        
                
	String sql_to_query = "select  cxp_prov_dir.id, "
                                +"cxp_prov.razon_social, "
                                +"'Calle:'||cxp_prov_dir.calle||'  Num.int.:'||(case when cxp_prov_dir.numero_interior='' then 'S/N' else  cxp_prov_dir.numero_interior end) ||'  Num.ext.:'||cxp_prov_dir.numero_exterior||'  Colonia:'||cxp_prov_dir.colonia||'  Municipio.:  '||gral_mun.titulo||'  C.P.:'||cxp_prov_dir.cp ||'  Estado.:'||gral_edo.titulo||'  Pais.:'||gral_pais.titulo as Domicilio, "
                                +"'Tel 1:  '|| cxp_prov_dir.telefono1||'  Extencion 1:  '||(case when cxp_prov_dir.extension1='' then 'S/E' else  cxp_prov_dir.extension1 end)|| 'Telefono 2:  '|| (case when cxp_prov_dir.telefono2='' then 'S/T' else  cxp_prov_dir.telefono2 end)|| '   Extencionn 2:  '||(case when cxp_prov_dir.extension2='' then 'S/E' else  cxp_prov_dir.extension2 end) as Telefonos, "
                                +"cxp_prov_dir.entre_calles "
                                +"from cxp_prov_dir "
                                +"join cxp_prov on cxp_prov.id= cxp_prov_dir.proveedor_id "
                                +"left join gral_pais on gral_pais.id=cxp_prov_dir.pais_id "
                                +"left join gral_edo on gral_edo.id=cxp_prov_dir.estado_id "
                                +"left join gral_mun on gral_mun.id= cxp_prov_dir.municipio_id "                                
                                +" JOIN ("+sql_busqueda+") AS sbt ON sbt.id = cxp_prov_dir.id " 
                                +"where cxp_prov_dir.borrado_logico=false  "
                                +" order by "+orderBy+" "+asc+" limit ? OFFSET ? ";
        
        //System.out.println("Datos para el Grid: "+sql_to_query);
        ArrayList<HashMap<String, Object>> hm = (ArrayList<HashMap<String, Object>>) this.jdbcTemplate.query(
            sql_to_query, 
            new Object[]{new String(data_string), new Integer(pageSize),new Integer(offset)}, new RowMapper() {
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    HashMap<String, Object> row = new HashMap<String, Object>();
                   row.put("id",rs.getInt("id"));
                   row.put("proveedor",rs.getString("razon_social"));
                   row.put("direccion",rs.getString("Domicilio"));
                   row.put("telefonos",rs.getString("Telefonos"));
                    row.put("calles",rs.getString("entre_calles"));
                    return row;
                }
            }
        );
        return hm; 
    }

    @Override
    public ArrayList<HashMap<String, String>> getProveedoresDirecciones_Datos(Integer id) {
        String sql_query = "select  cxp_prov_dir.id, "
                            +"cxp_prov_dir.proveedor_id, "
                            +"cxp_prov.razon_social, "
                            +"cxp_prov_dir.calle, "
                            +"cxp_prov_dir.numero_interior, "
                            +"cxp_prov_dir.numero_exterior, "
                            +"cxp_prov_dir.colonia, "
                            +"cxp_prov_dir.municipio_id ,"
                            +"gral_mun.titulo as municipio, "
                            +"cxp_prov_dir.cp, "
                            +"cxp_prov_dir.estado_id , " 
                            +"gral_edo.titulo as estado, " 
                            +"gral_pais.titulo as pais, "
                            +"cxp_prov_dir.pais_id, "
                            +"cxp_prov_dir.telefono1, "
                            +"cxp_prov_dir.extension1, "
                            +"cxp_prov_dir.telefono2, "
                            +"cxp_prov_dir.extension2, "
                            +"cxp_prov_dir.entre_calles "
                            +"from cxp_prov_dir "
                            +"join cxp_prov on cxp_prov.id= cxp_prov_dir.proveedor_id "
                            +"left join gral_pais on gral_pais.id=cxp_prov_dir.pais_id "
                            +"left join gral_edo on gral_edo.id=cxp_prov_dir.estado_id "
                            +"left join gral_mun on gral_mun.id= cxp_prov_dir.municipio_id "
                            +"where cxp_prov_dir.borrado_logico=false and cxp_prov_dir.id=?";
       
        //System.out.println(sql_query);
        
        ArrayList<HashMap<String, String>> hm = (ArrayList<HashMap<String, String>>) this.jdbcTemplate.query(
            sql_query,  
            new Object[]{new Integer(id)}, new RowMapper() {
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    HashMap<String, String> row = new HashMap<String, String>();
                    row.put("id",String.valueOf(rs.getInt("id")));
                    row.put("proveedor_id",String.valueOf(rs.getInt("proveedor_id")));
                    row.put("razon_social",rs.getString("razon_social"));
                    row.put("calle",rs.getString("calle"));
                    row.put("numero_interior",rs.getString("numero_interior"));
                    row.put("numero_exterior",rs.getString("numero_exterior"));
                    row.put("colonia",rs.getString("colonia"));
                    row.put("municipio",rs.getString("municipio"));
                    row.put("municipio_id",String.valueOf(rs.getInt("municipio_id")));
                    row.put("cp",rs.getString("cp"));
                     row.put("estado_id",String.valueOf(rs.getInt("estado_id")));
                    row.put("estado",rs.getString("estado"));
                    row.put("pais",rs.getString("pais"));
                    row.put("pais_id",String.valueOf(rs.getInt("pais_id")));
                    row.put("telefono1",rs.getString("telefono1"));
                    row.put("extension1",rs.getString("extension1"));
                    row.put("telefono2",rs.getString("telefono2"));
                    row.put("extension2",rs.getString("extension2"));
                    row.put("entre_calles",rs.getString("entre_calles"));
                    
                    return row;
                }
            }
        );
        return hm;
    }
    
    
    
    //aplicativo Anticipos a proveedores
    @Override
    public ArrayList<HashMap<String, Object>> getProveedoresAnticipos_PaginaGrid(String data_string, int offset, int pageSize, String orderBy, String asc) {
        String sql_busqueda = "select DISTINCT id from gral_bus_catalogos(?) as foo (id integer)";
        
	String sql_to_query = "SELECT cxp_ant.id, "
                                    +"cxp_ant.folio, "
                                    +"cxp_ant.cantidad AS total, "
                                    +"cxp_prov.razon_social, "
                                    +"tes_mov_tipos.titulo AS forma_pago, "
                                    +"gral_mon.descripcion_abr AS moneda, "
                                    +"(CASE WHEN cxp_ant.cancelado=FALSE THEN '' ELSE 'CANCELADO' END) AS estado, "
                                    +"to_char(cxp_ant.fecha_anticipo,'dd-mm-yyyy') AS fecha_anticipo "
                            +"FROM cxp_ant "
                            +"JOIN cxp_prov ON cxp_prov.id=cxp_ant.cxp_prov_id "
                            //+"JOIN tes_mov_tipos ON tes_mov_tipos.id=cxp_ant.cxp_mov_tipo_id "
                            +"JOIN tes_mov_tipos ON tes_mov_tipos.id=cxp_ant.tes_mov_tipo_id "
                            +"JOIN gral_mon ON gral_mon.id=cxp_ant.moneda_id " 
                            +"JOIN ("+sql_busqueda+") as subt on subt.id=cxp_ant.id "
                            +"order by "+orderBy+" "+asc+" limit ? OFFSET ?";
        
        System.out.println("sql_busqueda: "+sql_busqueda);
        System.out.println("ProveedoresAnticipos_PaginaGrid:: "+sql_to_query);
        ArrayList<HashMap<String, Object>> hm = (ArrayList<HashMap<String, Object>>) this.jdbcTemplate.query(
            sql_to_query, 
            new Object[]{new String(data_string),new Integer(pageSize),new Integer(offset)}, new RowMapper() {
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    HashMap<String, Object> row = new HashMap<String, Object>();
                    row.put("id",String.valueOf(rs.getInt("id")));
                    row.put("folio",rs.getString("folio"));
                    row.put("total",StringHelper.AgregaComas(StringHelper.roundDouble(rs.getString("total"),2)));
                    row.put("razon_social",rs.getString("razon_social"));
                    row.put("forma_pago",rs.getString("forma_pago"));
                    row.put("moneda",rs.getString("moneda"));
                    row.put("estado",rs.getString("estado"));
                    row.put("fecha_anticipo",rs.getString("fecha_anticipo"));
                    return row;
                }
            }
        );
        return hm;
    }
    
    
    
    
    @Override
    public ArrayList<HashMap<String, String>> getProveedoresAnticipos_FormasPago() {
        String sql_to_query = "SELECT id, titulo FROM  cxp_pagos_formas WHERE id !=1 AND borrado_logico=false ORDER BY id ASC;";
        ArrayList<HashMap<String, String>> hm_forma_pago = (ArrayList<HashMap<String, String>>) this.jdbcTemplate.query(
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
        return hm_forma_pago;
    }
    
    
    
    
    @Override
    public ArrayList<HashMap<String, String>> getProveedoresAnticipos_Datos(Integer id_anticipo) {
        String sql_to_query = " "
                    + "SELECT   "
                            + "cxp_ant.id,  "
                            + "cxp_ant.folio,  "
                            + "cxp_ant.cxp_prov_id,  "
                            + "cxp_prov.razon_social,  "
                            + "cxp_ant.moneda_id,  "
                            + "to_char(cxp_ant.fecha_anticipo,'yyyy-mm-dd') AS fecha_anticipo,  "
                            + "cxp_ant.tipo_cambio,  "
                            + "cxp_ant.tes_mov_tipo_id, "
                            + "cxp_ant.tes_ban_id, "
                            + "tes_mov.tes_con_id, "
                            + "cxp_ant.tes_che_id, "
                            + "(CASE WHEN tes_che.titulo IS NULL THEN '' ELSE tes_che.titulo END) AS no_chequera, "
                            + "cxp_ant.ref_num AS numero_cheque, "
                            + "cxp_ant.referencia, "
                            + "cxp_ant.cantidad AS monto_anticipo, "
                            + "cxp_ant.cancelado, "
                            + "cxp_ant.observaciones "
                    + "FROM cxp_ant "
                    + "LEFT JOIN tes_mov ON tes_mov.id=cxp_ant.tes_mov_id "
                    + "JOIN cxp_prov ON cxp_prov.id=cxp_ant.cxp_prov_id "
                    + "LEFT JOIN tes_che ON tes_che.id=cxp_ant.tes_che_id "
                    + "WHERE cxp_ant.id="+id_anticipo;
        
        System.out.println("Buscando datos del Anticipo: "+sql_to_query);
        ArrayList<HashMap<String, String>> datos = (ArrayList<HashMap<String, String>>) this.jdbcTemplate.query(
            sql_to_query,
            new Object[]{}, new RowMapper(){
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    HashMap<String, String> row = new HashMap<String, String>();
                    row.put("id",String.valueOf(rs.getInt("id")));
                    row.put("folio",rs.getString("folio"));
                    row.put("cxp_prov_id",String.valueOf(rs.getInt("cxp_prov_id")));
                    row.put("razon_social",rs.getString("razon_social"));
                    row.put("moneda_id",String.valueOf(rs.getInt("moneda_id")));
                    row.put("fecha_anticipo",rs.getString("fecha_anticipo"));
                    row.put("tipo_cambio",StringHelper.roundDouble(rs.getDouble("tipo_cambio"),4));
                    row.put("tes_mov_tipo_id",String.valueOf(rs.getInt("tes_mov_tipo_id")));
                    row.put("tes_ban_id",String.valueOf(rs.getInt("tes_ban_id")));
                    row.put("tes_che_id",String.valueOf(rs.getInt("tes_che_id")));
                    row.put("tes_con_id",String.valueOf(rs.getInt("tes_con_id")));
                    row.put("no_chequera",rs.getString("no_chequera"));
                    row.put("numero_cheque",rs.getString("numero_cheque"));
                    row.put("referencia",rs.getString("referencia"));
                    row.put("monto_anticipo",StringHelper.roundDouble(rs.getDouble("monto_anticipo"),2));
                    row.put("cancelado",String.valueOf(rs.getBoolean("cancelado")));
                    row.put("observaciones",rs.getString("observaciones"));
                    return row;
                }
            }
        );
        return datos;
    }
    
    
    
    
    
	//obtiene Anticipos los Aplicados a proveedores
    @Override
    public ArrayList<HashMap<String, String>> getProveedoresAnticipos_Aplicados(Integer id_anticipo, Integer id_proveedor) {
            String sql_to_query = ""
            + "SELECT cxp_prov.razon_social AS proveedor,"
                    + "cxp_ant.folio,"
                    + "to_char(cxp_ant.fecha_anticipo::timestamp with time zone,'dd/mm/yyyy') AS fecha_anticipo,"
                    + "gral_mon.descripcion AS moneda_anticipo,"
                    + "gral_mon.simbolo AS simbolo_moneda_anticipo,"
                    + "cxp_ant.cantidad AS monto_anticipo ,"
                    + "tes_ban.titulo AS banco,"
                    + "tes_mov_tipos.titulo AS forma_pago, "
                    + "cxp_ant.tipo_cambio AS tipo_cambio, "
                   // + "(CASE WHEN cxp_ant.tes_mov_tipo_id=2 THEN cxp_ant.ref_num::integer ELSE cxp_ant.referencia::character varying END ) AS cheque_referencia, "
                    + "(CASE WHEN (cxp_ant.moneda_id=1) THEN  "
                            //+ "(CASE WHEN (cxp_ant.moneda_id=2) THEN cxp_ant.cantidad * cxp_ant.tipo_cambio  ELSE cxp_ant.cantidad  END) "
                            + "(CASE WHEN (cxp_ant.moneda_id!=1) THEN cxp_ant.cantidad * cxp_ant.tipo_cambio  ELSE cxp_ant.cantidad  END) "
                   + "ELSE  "
                            + "(CASE WHEN (cxp_ant.moneda_id=1) THEN  cxp_ant.cantidad / cxp_ant.tipo_cambio ELSE cxp_ant.cantidad END) "
                   + "END) AS monto_anticipo_proveedor "
                   // + "cxp_ant_detalles.tipo_cambio "
            + "FROM cxp_ant "
            + "JOIN cxp_prov ON cxp_prov.id=cxp_ant.cxp_prov_id "
            + "JOIN gral_mon ON gral_mon.id=cxp_ant.moneda_id "
            + "LEFT JOIN tes_ban ON tes_ban.id=cxp_ant.tes_ban_id "
            + "JOIN tes_mov_tipos ON tes_mov_tipos.id=cxp_ant.tes_mov_tipo_id "
            + "WHERE cxp_ant.id="+ id_anticipo;
            
        //System.out.println("Buscando datos para reporte aplicacion de pago: "+sql_to_query);
        ArrayList<HashMap<String, String>> hm = (ArrayList<HashMap<String, String>>) this.jdbcTemplate.query(
            sql_to_query,
            new Object[]{}, new RowMapper(){
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    HashMap<String, String> row = new HashMap<String, String>();
                    row.put("proveedor",rs.getString("proveedor"));
                    row.put("folio",rs.getString("folio"));
                    row.put("fecha_anticipo",rs.getString("fecha_anticipo"));
                    row.put("moneda_anticipo",rs.getString("moneda_anticipo"));
                    row.put("simbolo_moneda_anticipo",rs.getString("simbolo_moneda_anticipo"));
                    row.put("banco",rs.getString("banco"));
                    row.put("forma_pago",rs.getString("forma_pago"));
                    //row.put("cheque_referencia",rs.getString("cheque_referencia"));
                    row.put("monto_anticipo",StringHelper.roundDouble(rs.getString("monto_anticipo"),2));
                    row.put("tipo_cambio",StringHelper.roundDouble(rs.getString("tipo_cambio"),4));
                    row.put("monto_anticipo_proveedor",StringHelper.roundDouble(rs.getDouble("monto_anticipo_proveedor"), 2));
                    return row;
                }
            }
        );
        return hm; 
    }

    
    
    
    
    
    
    //aplicativo Parametros de Anticipos de proveedor
    @Override
    public ArrayList<HashMap<String, Object>> getProvParamAnticipos_PaginaGrid(String data_string, int offset, int pageSize, String orderBy, String asc) {
        String sql_busqueda = "select DISTINCT id from gral_bus_catalogos(?) as foo (id integer)";
        
	String sql_to_query = "SELECT cxp_ant_par.id, "
                                    +"gral_suc.titulo AS sucursal,"
                                    +"(SELECT titulo FROM cxp_mov_tipos WHERE id=cxp_ant_par.cxp_mov_tipo_id) cxp_mov_anticipo, "
                                    +"(SELECT titulo FROM cxp_mov_tipos WHERE id=cxp_ant_par.cxp_mov_tipo_id_apl_ant) cxp_mov_apl_ant, "
                                    +"(SELECT titulo FROM cxp_mov_tipos WHERE id=cxp_ant_par.cxp_mov_tipo_id_apl_fac) cxp_mov_apl_fac, "
                                    +"(SELECT titulo FROM cxp_mov_tipos WHERE id=cxp_ant_par.cxp_mov_tipo_id_can) cxp_mov_can, "
                                    +"(CASE WHEN cxp_ant_par.incluye_iva=FALSE THEN 'No' ELSE 'Si' END) AS requiere_iva "
                            +"FROM cxp_ant_par "
                            +"JOIN gral_suc ON gral_suc.id=cxp_ant_par.gral_suc_id "
                            +"JOIN ("+sql_busqueda+") as subt on subt.id=cxp_ant_par.id "
                            +"order by "+orderBy+" "+asc+" limit ? OFFSET ?";
        
        //System.out.println("data_string: "+data_string);
        //System.out.println("Busqueda GetPage: "+sql_to_query);
        ArrayList<HashMap<String, Object>> hm = (ArrayList<HashMap<String, Object>>) this.jdbcTemplate.query(
            sql_to_query, 
            new Object[]{new String(data_string),new Integer(pageSize),new Integer(offset)}, new RowMapper() {
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    HashMap<String, Object> row = new HashMap<String, Object>();
                    row.put("id",String.valueOf(rs.getInt("id")));
                    row.put("sucursal",rs.getString("sucursal"));
                    row.put("cxp_mov_anticipo",rs.getString("cxp_mov_anticipo"));
                    row.put("cxp_mov_apl_ant",rs.getString("cxp_mov_apl_ant"));
                    row.put("cxp_mov_apl_fac",rs.getString("cxp_mov_apl_fac"));
                    row.put("cxp_mov_can",rs.getString("cxp_mov_can"));
                    row.put("requiere_iva",rs.getString("requiere_iva"));
                    return row;
                }
            }
        );
        return hm;
    }
    
    
    
    
    //obtiene las sucursales de la empresa
    @Override
    public ArrayList<HashMap<String, String>> getSucursales(Integer id_empresa) {
        String sql_query="";
        
        sql_query = "SELECT id, titulo FROM gral_suc WHERE borrado_logico=false AND empresa_id="+id_empresa+" ORDER BY titulo;";
        ArrayList<HashMap<String, String>> hm_suc= (ArrayList<HashMap<String, String>>) this.jdbcTemplate.query(
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
        return hm_suc;
    }
    
    
    //obtiene tipos de movimientos de acuerdo a los paremetros
    @Override
    public ArrayList<HashMap<String, String>> getProvParamAnticipos_TiposMovimiento(Integer id_empresa, Integer grupo, Integer naturaleza, String referenciado) {
        String sql_query="";
        
        sql_query = "SELECT id, descripcion as titulo FROM cxp_mov_tipos WHERE gral_emp_id="+id_empresa+" AND grupo="+grupo+" AND naturaleza="+naturaleza+" AND referenciado="+referenciado+" ORDER BY titulo;";
        
        ArrayList<HashMap<String, String>> hm_suc= (ArrayList<HashMap<String, String>>) this.jdbcTemplate.query(
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
        return hm_suc;
    }
    
    
    //obtiene los ids id de las sucursales diferente a la sucursal actual en donde se encuentra logueado el usuario
    @Override
    public ArrayList<HashMap<String, String>> getProvParamAnticipos_SucConsecutivo(Integer id_empresa, Integer id_sucursal) {
        String sql_query="";
        sql_query = "SELECT id, titulo FROM gral_suc WHERE borrado_logico=false AND empresa_id="+id_empresa+" AND id !="+id_sucursal+" ORDER BY titulo;";        
        ArrayList<HashMap<String, String>> hm = (ArrayList<HashMap<String, String>>) this.jdbcTemplate.query(
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
        return hm;
    }
    
    
    
    //obtiene los datos del parametro en especifico
    @Override
    public ArrayList<HashMap<String, String>> getProvParamAnticipos_Datos(Integer id) {
        String sql_query="";
        sql_query = "SELECT "
                            + "id, "
                            + "gral_suc_id, "
                            + "cxp_mov_tipo_id, "
                            + "cxp_mov_tipo_id_apl_ant, "
                            + "cxp_mov_tipo_id_apl_fac, "
                            + "cxp_mov_tipo_id_can, "
                            + "incluye_iva, "
                            + "oc_requerida, "
                            + "gral_suc_id_consecutivo "
                    + "FROM cxp_ant_par "
                    + "WHERE id="+id;
        
        ArrayList<HashMap<String, String>> hm_suc= (ArrayList<HashMap<String, String>>) this.jdbcTemplate.query(
            sql_query,
            new Object[]{}, new RowMapper() {
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    HashMap<String, String> row = new HashMap<String, String>();
                    row.put("id",String.valueOf(rs.getInt("id")));
                    row.put("gral_suc_id",String.valueOf(rs.getInt("gral_suc_id")));
                    row.put("cxp_mov_tipo_id",String.valueOf(rs.getInt("cxp_mov_tipo_id")));
                    row.put("cxp_mov_tipo_id_apl_ant",String.valueOf(rs.getInt("cxp_mov_tipo_id_apl_ant")));
                    row.put("cxp_mov_tipo_id_apl_fac",String.valueOf(rs.getInt("cxp_mov_tipo_id_apl_fac")));
                    row.put("cxp_mov_tipo_id_can",String.valueOf(rs.getInt("cxp_mov_tipo_id_can")));
                    row.put("incluye_iva",String.valueOf(rs.getBoolean("incluye_iva")));
                    row.put("oc_requerida",String.valueOf(rs.getBoolean("oc_requerida")));
                    row.put("gral_suc_id_consecutivo",String.valueOf(rs.getInt("gral_suc_id_consecutivo")));
                    return row;
                }
            }
        );
        return hm_suc;
    }
    
    
    
    
    @Override
    public ArrayList<HashMap<String, String>> getProgramacionPagos(String num_semanas, String opcion_seleccionada, Integer id_empresa) {
        String sql_to_query = "select * from repprogramacionPagos_semanas_proximas('"+num_semanas+"',"+id_empresa+") as foo( "
                                
                                + " proveedor character varying, "
                                + " factura character varying , "
                                + " moneda_factura character varying, "
                                + " pesos character varying , "
                                + " semana_actual double precision, "
                                + " dia_semana_actual double precision, "
                                + " fecha_vencimiento date, "
                                + " semana_vencimiento double precision, "
                                + " dia_semana_vencimiento double precision, "
                                + " lunes double precision, "
                                + " martes double precision, "
                                + " miercoles double precision, "
                                + " jueves double precision, "
                                + " viernes double precision, "
                                + " lunes_proximo text, "
                                + " viernes_proximo text, "
                                + " total double precision ); ";
        
        System.out.println("sql_to_query:"+ sql_to_query);
        
        ArrayList<HashMap<String, String>> hm = (ArrayList<HashMap<String, String>>) this.jdbcTemplate.query(
                sql_to_query, 
            new Object[]{}, new RowMapper() {
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    HashMap<String, String> row = new HashMap<String, String>();
                  
                    row.put("proveedor",rs.getString("proveedor"));
                    row.put("factura",rs.getString("factura"));
                    row.put("pesos",rs.getString("pesos"));
                    row.put("lunes",rs.getString("lunes"));
                    row.put("martes",rs.getString("martes"));
                    row.put("miercoles",rs.getString("miercoles"));
                    row.put("jueves",rs.getString("jueves"));
                    row.put("viernes",rs.getString("viernes"));
                    row.put("lunes_proximo",rs.getString("lunes_proximo"));
                    row.put("viernes_proximo",rs.getString("viernes_proximo"));
                    row.put("total",rs.getString("total"));
                    //System.out.print(row);
                    return row;
                }
            }
        );
        return hm; 
    }
    
    
    
    
    
    
    //ESTO VA EN EL CXP SPRING DAO ES LO UNICO QUE AGREGUÉ
    @Override
    public ArrayList<HashMap<String, String>> getProveedor_DatosReporteEdoCta(Integer tipo_reporte,String proveedor, String fecha_corte,Integer id_empresa) {
        
        String where_proveedor="";
        if(tipo_reporte==1){
            where_proveedor=" AND cxp_prov.razon_social ilike '%"+proveedor+"%'";
        }
        
        String sql_to_query = " "
                + "SELECT  "
                    + "cxp_facturas.serie_folio, "
                    + "cxp_facturas.moneda_id,"
                    + "to_char(cxp_facturas.fecha_factura,'dd/mm/yyyy')as fecha_factura, "
                    + "cxp_facturas.orden_compra, "
                    + "cxp_facturas.monto_total, "
                    + "(cxp_facturas.total_pagos + cxp_facturas.total_notas_creditos) AS importe_pagado, "
                    + "(CASE WHEN cxp_facturas.fecha_ultimo_pago is null THEN '&nbsp;&nbsp;&nbsp;&nbsp;/&nbsp;&nbsp;&nbsp;&nbsp;/&nbsp;&nbsp;&nbsp;&nbsp;'  ELSE  to_char(cxp_facturas.fecha_ultimo_pago,'dd/mm/yyyy') END) AS fecha_ultimo_pago, "
                    + "cxp_facturas.saldo_factura, "
                    + "gral_mon.descripcion_abr, "
                    + "gral_mon.simbolo AS moneda_simbolo, "
                    + "cxp_prov.id, "
                    + "cxp_prov.razon_social "
                + "FROM cxp_facturas "
                + "JOIN cxp_prov on cxp_prov.id = cxp_facturas.cxc_prov_id "
                + "JOIN gral_mon on gral_mon.id = cxp_facturas.moneda_id "
                + "WHERE cxp_facturas.pagado=FALSE "
                + "AND cxp_facturas.cancelacion=FALSE   "+ where_proveedor +" "
                + "AND cxp_facturas.empresa_id="+ id_empresa + " "
                //+ "AND cxp_facturas.moneda_id ="+ id_moneda
                + "ORDER BY cxp_prov.id, cxp_facturas.moneda_id, cxp_facturas.fecha_factura, cxp_facturas.serie_folio;"; 
        
        System.out.println("Proveedor_DatosReporteEdoCta:: "+sql_to_query);
        ArrayList<HashMap<String, String>> hm_facturas = (ArrayList<HashMap<String, String>>) this.jdbcTemplate.query(
            sql_to_query,
            new Object[]{}, new RowMapper(){
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    HashMap<String, String> row = new HashMap<String, String>();
                    row.put("proveedor",rs.getString("razon_social"));
                    row.put("denominacion",rs.getString("descripcion_abr"));
                    row.put("moneda_simbolo",rs.getString("moneda_simbolo"));
                    row.put("serie_folio",rs.getString("serie_folio"));
                    row.put("moneda_id",rs.getString("moneda_id"));
                    row.put("orden_compra",rs.getString("orden_compra"));
                    row.put("fecha_facturacion",rs.getString("fecha_factura"));
                    row.put("monto_total",StringHelper.roundDouble(rs.getDouble("monto_total"), 2));
                    row.put("importe_pagado",StringHelper.roundDouble(rs.getDouble("importe_pagado"), 2));
                    row.put("ultimo_pago",rs.getString("fecha_ultimo_pago"));
                    row.put("saldo_factura",StringHelper.roundDouble(rs.getDouble("saldo_factura"), 2));
                    return row;
                }
            }
        );
        return hm_facturas;
    }
    
    
    
    
    //Este es el query para el reporte de Saldo Mensual de CXP
    @Override
    public ArrayList<HashMap<String, String>> getProveedor_DatosReporteSaldoMensual(Integer tipo_reporte,String proveedor, String fecha_corte,Integer id_empresa) {
        
        String sql_to_query = " "
                + "select * from cxp_reporte_saldo_mensual(?,?,?,?) as foo("
                + "id_proveedor integer, "
                + "proveedor character varying, "
                + "serie_folio character varying, "
                + "fecha_factura character varying, "
                + "moneda_id integer, "
                + "moneda_abr character varying, "
                + "moneda_simbolo character varying, "
                + "orden_compra character varying, "
                + "monto_factura double precision, "
                + "importe_pagado double precision, "
                + "saldo_factura double precision);"; 
        
        System.out.println("Proveedor_DatosReporteSaldoMensual:: "+sql_to_query);
        ArrayList<HashMap<String, String>> hm_facturas = (ArrayList<HashMap<String, String>>) this.jdbcTemplate.query(
            sql_to_query,
            new Object[]{new Integer(tipo_reporte), new String (proveedor), new String(fecha_corte), new Integer(id_empresa)}, new RowMapper(){
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    HashMap<String, String> row = new HashMap<String, String>();
                    row.put("proveedor",rs.getString("proveedor"));
                    row.put("denominacion",rs.getString("moneda_abr"));
                    row.put("moneda_simbolo",rs.getString("moneda_simbolo"));
                    row.put("serie_folio",rs.getString("serie_folio"));
                    row.put("moneda_id",rs.getString("moneda_id"));
                    row.put("orden_compra",rs.getString("orden_compra"));
                    row.put("fecha_facturacion",rs.getString("fecha_factura"));
                    row.put("monto_total",StringHelper.roundDouble(rs.getDouble("monto_factura"), 2));
                    row.put("importe_pagado",StringHelper.roundDouble(rs.getDouble("importe_pagado"), 2));
                    row.put("ultimo_pago","&nbsp;&nbsp;&nbsp;&nbsp;/&nbsp;&nbsp;&nbsp;&nbsp;/&nbsp;&nbsp;&nbsp;&nbsp;");
                    row.put("saldo_factura",StringHelper.roundDouble(rs.getDouble("saldo_factura"), 2));
                    return row;
                }
            }
        );
        return hm_facturas;
    }
    
    
    //Calcular años a mostrar en el reporte
    @Override
    public ArrayList<HashMap<String, Object>>  getProveedor_AnioReporteSaldoMensual() {
        ArrayList<HashMap<String, Object>> anios = new ArrayList<HashMap<String, Object>>();
        
        Calendar c1 = Calendar.getInstance();
        Integer annio = c1.get(Calendar.YEAR);//obtiene el año actual
        
        for(int i=0; i<5; i++) {
            HashMap<String, Object> row = new HashMap<String, Object>();
            row.put("valor",(annio-i));
            anios.add(i, row);
        }
        return anios;
    }
    
    
    
    
    //**********************************************************************************************************************************
    //METODOS PARA NOTAS DE CREDITO PROVEEDORES
    //aplicativo pagos a proveedores
    @Override
    public ArrayList<HashMap<String, Object>> getProvNotasCredito_PaginaGrid(String data_string, int offset, int pageSize, String orderBy, String asc) {
        String sql_busqueda = "select DISTINCT id from gral_bus_catalogos(?) as foo (id integer)";
        
	String sql_to_query = "SELECT cxp_nota_credito.id, "
                    + "cxp_nota_credito.folio, "
                    + "cxp_nota_credito.serie_folio AS nc, "
                    + "cxp_prov.razon_social AS  proveedor, "
                    + "cxp_nota_credito.total, "
                    + "to_char(cxp_nota_credito.fecha_expedicion::timestamp with time zone,'dd/mm/yyyy') AS fecha_expedicion, "
                    + "gral_mon.descripcion_abr AS moneda, "
                    + "cxp_nota_credito.factura, "
                    + "(CASE WHEN cxp_nota_credito.cancelado=FALSE THEN '' ELSE 'CANCELADO' END) AS estado "
                + "FROM cxp_nota_credito "
                + "LEFT JOIN cxp_prov ON cxp_prov.id=cxp_nota_credito.cxp_prov_id "
                + "LEFT JOIN gral_mon ON gral_mon.id=cxp_nota_credito.moneda_id " 
                +"JOIN ("+sql_busqueda+") as subt on subt.id=cxp_nota_credito.id "
                +"order by "+orderBy+" "+asc+" limit ? OFFSET ?";
        
        //System.out.println("Busqueda GetPage: "+sql_to_query);
        ArrayList<HashMap<String, Object>> hm = (ArrayList<HashMap<String, Object>>) this.jdbcTemplate.query(
            sql_to_query, 
            new Object[]{new String(data_string),new Integer(pageSize),new Integer(offset)}, new RowMapper() {
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    HashMap<String, Object> row = new HashMap<String, Object>();
                    row.put("id",String.valueOf(rs.getInt("id")));
                    row.put("folio",rs.getString("folio"));
                    row.put("nc",rs.getString("nc"));
                    row.put("proveedor",rs.getString("proveedor"));
                    row.put("total",StringHelper.AgregaComas(StringHelper.roundDouble(rs.getString("total"),2)));
                    row.put("fecha_expedicion",rs.getString("fecha_expedicion"));
                    row.put("moneda",rs.getString("moneda"));
                    row.put("factura",rs.getString("factura"));
                    row.put("estado",rs.getString("estado"));
                    return row;
                }
            }
        );
        return hm;
        
    }
    
    
    
    //obtiene las facturas del proveedor con saldo pendiente de pago para aplicar Nota de Credito
    @Override
    public ArrayList<HashMap<String, String>> getProvNotasCredito_Facturas(Integer id_proveedor) {
	String sql_query = ""
                + "SELECT "
                    + "cxp_facturas.serie_folio AS factura, "
                    + "cxp_facturas.monto_total AS monto_factura, "
                    + "cxp_facturas.total_pagos AS pago_aplicado, "
                    + "cxp_facturas.total_notas_creditos AS nc_aplicado, "
                    + "cxp_facturas.saldo_factura, "
                    + "cxp_facturas.moneda_id, "
                    + "gral_mon.descripcion_abr AS moneda, "
                    + "to_char(cxp_facturas.fecha_factura,'dd/mm/yyyy') AS fecha_factura "
                + "FROM cxp_facturas "
                + "JOIN gral_mon ON gral_mon.id=cxp_facturas.moneda_id "
                + "WHERE cxp_facturas.cxc_prov_id="+id_proveedor+" "
                + "AND cxp_facturas.pagado=FALSE "
                + "AND cxp_facturas.cancelacion=FALSE;";
        
        ArrayList<HashMap<String, String>> hm = (ArrayList<HashMap<String, String>>) this.jdbcTemplate.query(
            sql_query,  
            new Object[]{}, new RowMapper() {
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    HashMap<String, String> row = new HashMap<String, String>();
                    row.put("factura",rs.getString("factura"));
                    row.put("monto_factura",StringHelper.roundDouble(rs.getDouble("monto_factura"), 2));
                    row.put("saldo_factura",StringHelper.roundDouble(rs.getDouble("saldo_factura"), 2));
                    row.put("pago_aplicado",StringHelper.roundDouble(rs.getDouble("pago_aplicado"), 2));
                    row.put("nc_aplicado",StringHelper.roundDouble(rs.getDouble("nc_aplicado"), 2));
                    row.put("moneda_id",String.valueOf(rs.getInt("moneda_id")));
                    row.put("moneda",rs.getString("moneda"));
                    row.put("fecha_factura",rs.getString("fecha_factura"));
                    return row;
                }
            }
        );
        return hm;
    }
    
    
    
    
    
    //obtiene valor del impuesto del asignado al proveedor
    @Override
    public ArrayList<HashMap<String, String>> getProvNotasCredito_Impuesto(Integer id_proveedor) {
        String sql_to_query = ""
                + "SELECT "
                    + "cxp_prov.impuesto AS id_impuesto,"
                    + "(CASE WHEN cxp_prov.proveedortipo_id=2 THEN 0 ELSE (CASE WHEN cxp_prov.impuesto=0 THEN 0 ELSE gral_imptos.iva_1 END) END ) AS valor_impuesto "
                + "FROM cxp_prov "
                + "LEFT JOIN gral_imptos ON gral_imptos.id=cxp_prov.impuesto "
                + "WHERE cxp_prov.id=?";
        
        //System.out.println(sql_to_query);
        ArrayList<HashMap<String, String>> hm_valoriva = (ArrayList<HashMap<String, String>>) this.jdbcTemplate.query(
            sql_to_query, new Object[]{new Integer(id_proveedor)}, new RowMapper(){
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
    
    
    
    
    @Override
    public ArrayList<HashMap<String, String>> getProvNotasCredito_Datos(Integer id_nota_credito) {
        String sql_to_query = " "
                    + "SELECT cxp_nota_credito.id,"
                        + "cxp_nota_credito.folio,"
                        + "cxp_nota_credito.tipo,"
                        + "cxp_prov.id AS id_proveedor,"
                        + "cxp_prov.folio AS numero_proveedor,"
                        + "cxp_prov.razon_social AS proveedor,"
                        + "cxp_nota_credito.observaciones,"
                        + "cxp_nota_credito.serie_folio AS nota_credito,"
                        + "cxp_nota_credito.fecha_expedicion,"
                        + "cxp_nota_credito.moneda_id,"
                        + "cxp_nota_credito.tipo_cambio,"
                        + "cxp_nota_credito.concepto,"
                        + "cxp_nota_credito.subtotal,"
                        + "cxp_nota_credito.impuesto,"
                        + "cxp_nota_credito.monto_ieps,"
                        + "cxp_nota_credito.total,"
                        + "cxp_nota_credito.cancelado,"
                        + "cxp_nota_credito.factura,"
                        + "to_char(cxp_facturas.fecha_factura,'dd/mm/yyyy') AS fecha_factura,"
                        + "cxp_facturas.monto_total AS cantidad_factura,"
                        + "cxp_facturas.total_pagos,"
                        + "cxp_facturas.total_notas_creditos,"
                        + "cxp_facturas.saldo_factura "
                + "FROM cxp_nota_credito "
                + "LEFT JOIN cxp_prov ON cxp_prov.id=cxp_nota_credito.cxp_prov_id "
                + "LEFT JOIN cxp_facturas ON cxp_facturas.serie_folio=cxp_nota_credito.factura "
                + "WHERE cxp_nota_credito.id=? AND cxp_nota_credito.cxp_prov_id=cxp_facturas.cxc_prov_id;";
        
        //System.out.println("Buscando datos Nota credito: "+sql_to_query);
        ArrayList<HashMap<String, String>> datos = (ArrayList<HashMap<String, String>>) this.jdbcTemplate.query(
            sql_to_query,
            new Object[]{new Integer(id_nota_credito)}, new RowMapper(){
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    HashMap<String, String> row = new HashMap<String, String>();
                    row.put("id",String.valueOf(rs.getInt("id")));
                    row.put("tipo",String.valueOf(rs.getInt("tipo")));
                    row.put("folio",rs.getString("folio"));
                    row.put("id_proveedor",String.valueOf(rs.getInt("id_proveedor")));
                    row.put("numero_proveedor",rs.getString("numero_proveedor"));
                    row.put("proveedor",rs.getString("proveedor"));
                    row.put("observaciones",rs.getString("observaciones"));
                    row.put("nota_credito",rs.getString("nota_credito"));
                    row.put("fecha_expedicion",rs.getString("fecha_expedicion"));
                    row.put("moneda_id",String.valueOf(rs.getInt("moneda_id")));
                    row.put("tipo_cambio",StringHelper.roundDouble(rs.getDouble("tipo_cambio"),4));
                    row.put("concepto",rs.getString("concepto"));
                    row.put("subtotal",StringHelper.roundDouble(rs.getDouble("subtotal"),2));
                    row.put("ieps_nota",StringHelper.roundDouble(rs.getDouble("monto_ieps"),2));
                    row.put("impuesto",StringHelper.roundDouble(rs.getDouble("impuesto"),2));
                    row.put("total",StringHelper.roundDouble(rs.getDouble("total"),2));
                    row.put("cancelado",String.valueOf(rs.getBoolean("cancelado")));
                    row.put("factura",rs.getString("factura"));
                    row.put("fecha_factura",rs.getString("fecha_factura"));
                    row.put("cantidad_factura",StringHelper.roundDouble(rs.getDouble("cantidad_factura"),2));
                    row.put("total_pagos",StringHelper.roundDouble(rs.getDouble("total_pagos"),2));
                    row.put("total_notas_creditos",StringHelper.roundDouble(rs.getDouble("total_notas_creditos"),2));
                    row.put("saldo_factura",StringHelper.roundDouble(rs.getDouble("saldo_factura"),2));
                    return row;
                }
            }
        );
        return datos;
    }
    
    
    
    
    
    @Override
    public HashMap<String, String> getProvNotasCredito_DatosPDF(Integer id) {
        HashMap<String, String> data = new HashMap<String, String>();
        
        String sql_to_query = ""
                + "SELECT cxp_nota_credito.id,"
                    + "(CASE WHEN cxp_nota_credito.tipo=1 THEN 'BONIFICACION' "
                        + "WHEN cxp_nota_credito.tipo=2 THEN 'DESCUENTO' "
                        + "WHEN cxp_nota_credito.tipo=3 THEN 'DEVOLUCION DE MERCANCIA' "
                    + "ELSE '' END) AS tipo, "
                    + "cxp_nota_credito.folio,"
                    + "cxp_nota_credito.serie_folio AS nota_credito,"
                    + "cxp_nota_credito.concepto,"
                    + "to_char(cxp_nota_credito.fecha_expedicion::timestamp with time zone,'dd/mm/yyyy') AS fecha_expedicion,"
                    + "gral_mon.descripcion AS moneda,"
                    + "gral_mon.descripcion_abr AS moneda_abr,"
                    + "gral_mon.simbolo AS simbolo_moneda,"
                    + "cxp_nota_credito.observaciones,"
                    + "cxp_nota_credito.tipo_cambio,"
                    + "cxp_nota_credito.subtotal,"
                    + "cxp_nota_credito.monto_ieps,"
                    + "cxp_nota_credito.impuesto,"
                    + "cxp_nota_credito.total, "
                    + "cxp_prov.folio AS numero_proveedor,"
                    + "cxp_prov.razon_social AS proveedor,"
                    + "cxp_prov.rfc AS rfc_proveedor,"
                    + "cxp_prov.calle,"
                    + "cxp_prov.numero,"
                    + "cxp_prov.colonia,"
                    + "cxp_prov.cp,"
                    + "gral_mun.titulo AS municipio,"
                    + "gral_edo.titulo AS estado,"
                    + "gral_pais.titulo AS pais "
                + "FROM cxp_nota_credito "
                + "LEFT JOIN cxp_prov ON cxp_prov.id=cxp_nota_credito.cxp_prov_id "
                + "LEFT JOIN gral_mun ON gral_mun.id=cxp_prov.municipio_id "
                + "LEFT JOIN gral_edo ON gral_edo.id=cxp_prov.estado_id "
                + "LEFT JOIN gral_pais ON gral_pais.id=cxp_prov.pais_id "
                + "LEFT JOIN gral_mon ON gral_mon.id=cxp_nota_credito.moneda_id "
                + "WHERE cxp_nota_credito.id="+id+";";
        
        //System.out.println("Datos PDF NC::::"+sql_to_query);
        
        Map<String, Object> map = this.getJdbcTemplate().queryForMap(sql_to_query);
        data.put("nota_folio_registro",map.get("folio").toString());
        data.put("nota_tipo",map.get("tipo").toString());
        data.put("nota_serie_folio",map.get("nota_credito").toString());
        data.put("nota_concepto",map.get("concepto").toString());
        data.put("nota_fecha_exp",map.get("fecha_expedicion").toString());
        data.put("nota_moneda",map.get("moneda").toString());
        data.put("nota_moneda_abr",map.get("moneda_abr").toString());
        data.put("nota_simbolo_moneda",map.get("simbolo_moneda").toString());
        data.put("nota_observaciones",map.get("observaciones").toString());
        data.put("nota_tipo_cambio",StringHelper.roundDouble(map.get("tipo_cambio").toString(),2));
        data.put("nota_subtotal",StringHelper.roundDouble(map.get("subtotal").toString(),2));
        data.put("monto_ieps",StringHelper.roundDouble(map.get("monto_ieps").toString(),2));
        data.put("nota_impuesto",StringHelper.roundDouble(map.get("impuesto").toString(),2));
        data.put("nota_total",StringHelper.roundDouble(map.get("total").toString(),2));
        data.put("prov_folio",map.get("numero_proveedor").toString());
        data.put("prov_razon_social",map.get("proveedor").toString());
        data.put("prov_rfc",map.get("rfc_proveedor").toString());
        data.put("prov_calle",map.get("calle").toString());
        data.put("prov_numero",map.get("numero").toString());
        data.put("prov_colonia",map.get("colonia").toString());
        data.put("prov_cp",map.get("cp").toString());
        data.put("prov_municipio",map.get("municipio").toString());
        data.put("prov_estado",map.get("estado").toString());
        data.put("prov_pais",map.get("pais").toString());
        
        return data;
    }

    
    //TERMINA METODOS PARA NOTAS DE CREDITO A PROVEEDORES
    //**********************************************************************************************************************************

    
    
    //Reporte de Proveedores
    @Override
    public ArrayList<HashMap<String, String>> getListaProveedores(String folio,String razon_proveedor,Integer empresa_id) {

            String sql_query = "SELECT cxp_prov.id, "
                    + "cxp_prov.folio, "
                    + "cxp_prov.razon_social, "
                    + "cxp_prov.rfc,"
                    + "cxp_prov.calle||',  #'||cxp_prov.numero||', '||cxp_prov.colonia||', '|| gral_mun.titulo||' C.P.'||cxp_prov.cp||', '||gral_edo.abreviacion||', '||gral_pais.abreviacion as direccion_proveedor,"
                    + "(telefono1 || ', ' || telefono2) AS telefonos, "
                    + "correo_electronico, "
                    + "to_char(cxp_prov.momento_creacion,'DD/MM/YYYY')as momento_creacion "
                    + "FROM cxp_prov "
                        +"   join gral_pais on gral_pais.id=cxp_prov.pais_id   "
                        +"   join gral_edo on gral_edo.id=cxp_prov.estado_id   "
                        +"   join gral_mun on gral_mun.id=cxp_prov.municipio_id   "
                        +"   where "
                     + " cxp_prov.folio ILIKE '%"+folio+"%' AND cxp_prov.razon_social ILIKE '%"+razon_proveedor+"%' "
                    + "AND cxp_prov.borrado_logico = false AND cxp_prov.empresa_id=" +empresa_id+""
                    + "ORDER BY cxp_prov.razon_social";
          
        ArrayList<HashMap<String, String>> hm = (ArrayList<HashMap<String, String>>) this.jdbcTemplate.query(
            sql_query,
            new Object[]{}, new RowMapper() {
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    HashMap<String, String> row = new HashMap<String, String>();
                    row.put("id",String.valueOf(rs.getInt("id")));
                    row.put("folio",rs.getString("folio"));
                    row.put("razon_social",rs.getString("razon_social"));
                    row.put("rfc",rs.getString("rfc"));
                    row.put("direccion_proveedor",rs.getString("direccion_proveedor"));
                    row.put("telefonos",rs.getString("telefonos"));
                    row.put("correo_electronico",rs.getString("correo_electronico"));
                    row.put("momento_creacion",rs.getString("momento_creacion"));

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
    
    
    
    
    @Override
    public ArrayList<HashMap<String, String>> getDatosReporteIepsPagado(ArrayList<HashMap<String, String>> listaIeps, String proveedor, String finicial, String ffinal, Integer id_empresa) {
        final ArrayList<HashMap<String, String>> tiposIeps = listaIeps;
        String condiciones="";
        String campos1="";
        String campos2="";
        
        if(!proveedor.trim().equals("")){
            condiciones += condiciones + " and cxp_prov.razon_social ilike '%"+proveedor+"%'";
        }
        
        //Crear nombres de campos dinamicamente
        for( HashMap<String,String> i : tiposIeps ){
            campos1 += ",sum(ieps"+i.get("id")+") as ieps"+i.get("id")+" ";
            campos2 += ",(CASE WHEN fac_det.gral_ieps_id="+i.get("id")+" THEN (CASE WHEN fac_det.gral_ieps_id>0 THEN ((fac_det.cantidad * (fac_det.costo_unitario * (CASE WHEN cxp_facturas.moneda_id=1 THEN 1 ELSE cxp_facturas.tipo_cambio END))) * fac_det.valor_ieps) ELSE 0 END) ELSE 0 END) AS ieps"+i.get("id")+" ";
        }
        
        String sql_to_query = ""
        + "SELECT "
            + "fecha_ultimo_pago"
            + ",fecha_pago"
            + ",proveedor"
            + ",fecha "
            + ",factura "
            + ",moneda_fac "
            + ",subtotal "
            + ",retencion"
            + ",iva"
            + ",total"
            + campos1 +" "
        + "from ( "
            + "select "
                + "cxp_facturas.fecha_ultimo_pago"
                + ",cxp_prov.razon_social as proveedor"
                + ",to_char(cxp_facturas.fecha_ultimo_pago, 'dd/mm/yyyy') as fecha_pago "
                + ",to_char(cxp_facturas.fecha_factura, 'dd/mm/yyyy') as fecha"
                + ",cxp_facturas.serie_folio as factura "
                + ",gral_mon.descripcion_abr as moneda_fac "
                + ",(cxp_facturas.subtotal * (CASE WHEN cxp_facturas.moneda_id=1 THEN 1 ELSE cxp_facturas.tipo_cambio END)) as subtotal"
                + ",(cxp_facturas.retencion * (CASE WHEN cxp_facturas.moneda_id=1 THEN 1 ELSE cxp_facturas.tipo_cambio END)) as retencion"
                + ",(cxp_facturas.iva * (CASE WHEN cxp_facturas.moneda_id=1 THEN 1 ELSE cxp_facturas.tipo_cambio END)) as iva"
                + ",(cxp_facturas.monto_total * (CASE WHEN cxp_facturas.moneda_id=1 THEN 1 ELSE cxp_facturas.tipo_cambio END)) as total"
                + campos2 +" "
            + "from cxp_facturas "
            + "join cxp_facturas_detalle as fac_det on fac_det.cxp_facturas_id=cxp_facturas.id  "
            + "join cxp_prov on cxp_prov.id=cxp_facturas.cxc_prov_id "
            + "join gral_mon on gral_mon.id=cxp_facturas.moneda_id "
            + "where fac_det.gral_ieps_id>0 and cxp_prov.borrado_logico=false and cxp_prov.empresa_id=? "+condiciones +" "
            + "AND (to_char(cxp_facturas.fecha_ultimo_pago,'yyyymmdd')::integer BETWEEN to_char('"+finicial+"'::timestamp with time zone,'yyyymmdd')::integer AND to_char('"+ffinal+"'::timestamp with time zone,'yyyymmdd')::integer) "
        + ") as sbt "
        + "group by fecha_ultimo_pago, proveedor,fecha_pago,fecha, factura, moneda_fac, subtotal,retencion,iva,total "
        + "order by fecha_ultimo_pago";

        
        
        
        
        //System.out.println("getDatosReporteIepPagado:: "+sql_to_query);
        
        ArrayList<HashMap<String, String>> arraydata = (ArrayList<HashMap<String, String>>) this.jdbcTemplate.query(
            sql_to_query,
            new Object[]{new Integer(id_empresa)}, new RowMapper(){
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    HashMap<String, String> row = new HashMap<String, String>();
                    
                    row.put("fecha_pago",rs.getString("fecha_pago"));
                    row.put("fecha",rs.getString("fecha"));
                    row.put("factura",rs.getString("factura"));
                    row.put("moneda_fac",rs.getString("moneda_fac"));
                    row.put("moneda_simbolo_subtotal","$");
                    row.put("subtotal",StringHelper.AgregaComas(StringHelper.roundDouble(rs.getDouble("subtotal"), 2)));
                    row.put("moneda_simbolo_retencion","$");
                    row.put("retencion",StringHelper.AgregaComas(StringHelper.roundDouble(rs.getDouble("retencion"), 2)));
                    row.put("moneda_simbolo_iva","$");
                    row.put("iva",StringHelper.AgregaComas(StringHelper.roundDouble(rs.getDouble("iva"), 2)));
                    row.put("moneda_simbolo_total","$");
                    row.put("total",StringHelper.AgregaComas(StringHelper.roundDouble(rs.getDouble("total"), 2)));
                    
                    //Crear nombres de campos dinamicamente
                    for( HashMap<String,String> i : tiposIeps ){
                        row.put("moneda_simbolo_ieps"+i.get("id"),"$");
                        row.put("ieps"+i.get("id"),StringHelper.AgregaComas(StringHelper.roundDouble(rs.getDouble("ieps"+i.get("id")), 2)));
                    }
                    return row;
                }
            }
        );
        return arraydata;
    }
    
    
    @Override
    public ArrayList<HashMap<String, String>> getListaIeps(Integer id_empresa) {
        
        String sql_to_query = "select id, titulo, tasa from gral_ieps where gral_emp_id=? and borrado_logico=false;"; 
        
        //System.out.println("CxC_DatosReporteSaldoMensual:: "+sql_to_query);
        ArrayList<HashMap<String, String>> arraydata = (ArrayList<HashMap<String, String>>) this.jdbcTemplate.query(
            sql_to_query,
            new Object[]{new Integer(id_empresa)}, new RowMapper(){
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    HashMap<String, String> row = new HashMap<String, String>();
                    row.put("id",rs.getString("id"));
                    row.put("titulo",rs.getString("titulo"));
                    row.put("tasa",StringHelper.roundDouble(rs.getDouble("tasa"), 2));
                    return row;
                }
            }
        );
        return arraydata;
    }
    
    
    
    //Reporte de Pagos Diarios
    @Override
    public ArrayList<HashMap<String, String>> getPagosDiaria(String fecha_inicial, String fecha_final,String proveedor, Integer tipo_prov, Integer id_empresa) {
        String where="";

        if(!proveedor.trim().equals("")){
            where = " and cxp_prov.razon_social ilike '%"+ proveedor +"%'";
        }
        
        if(tipo_prov>0){
            if(tipo_prov==1){
                //Filtro para facturas de compras(Materias primas)
                where = " and cxp_facturas.tipo_factura_proveedor="+tipo_prov+" ";
            }else{
                if(tipo_prov==2){
                    //Filtro para proveedores de Otros servicios diferentes a compras de materias primas
                    where = " and cxp_facturas.tipo_factura_proveedor!=1 ";
                }
            }
        }
        
        String sql_to_query = ""
        + "SELECT "  
            +" cxp_facturas.serie_folio AS factura, "
            +" to_char(cxp_facturas.fecha_factura,'dd/mm/yyyy') AS fecha_factura," 
            +" cxp_pagos.cxp_prov_id AS id_proveedor, " 
            +" cxp_prov.razon_social AS proveedor, "
            +" gral_mon_fac.id as id_moneda_fac, "
            +" gral_mon_fac.simbolo AS simbolo_moneda_fac, " 
            +" gral_mon_fac.descripcion_abr AS moneda_fac, "
            +" cxp_facturas.monto_total AS monto_factura, " 
            +" gral_mon_fac.simbolo AS simbolo_moneda_aplicado, "   
            +" cxp_pagos_detalles.cantidad AS pago_aplicado, "
            +" to_char(cxp_pagos.fecha_pago,'dd/mm/yyyy') AS fecha_pago, "
            +" gral_mon_pago.id as id_moneda_pago, "
            +" gral_mon_pago.simbolo AS simbolo_moneda_pago, "  
            +" gral_mon_pago.descripcion_abr AS moneda_pago,"
            +" (case when cxp_facturas.moneda_id=cxp_pagos.moneda_id then cxp_pagos_detalles.cantidad else (case when cxp_facturas.moneda_id=1 then cxp_pagos_detalles.cantidad/cxp_pagos.tipo_cambio else cxp_pagos_detalles.cantidad*cxp_pagos.tipo_cambio end) end) AS monto_pago "
        +" FROM cxp_pagos "
        +" JOIN cxp_pagos_detalles ON cxp_pagos_detalles.cxp_pago_id=cxp_pagos.id "
        +" JOIN cxp_facturas ON (cxp_facturas.serie_folio=cxp_pagos_detalles.serie_folio AND cxp_facturas.cxc_prov_id=cxp_pagos.cxp_prov_id) "
        +" jOIN cxp_prov  ON cxp_prov.id=cxp_pagos.cxp_prov_id "
        +" JOIN gral_mon as gral_mon_fac ON gral_mon_fac.id=cxp_facturas.moneda_id "
        +" JOIN gral_mon AS gral_mon_pago ON gral_mon_pago.id=cxp_pagos.moneda_id "
        +" WHERE cxp_pagos.gral_emp_id=" +id_empresa + " "
        +" AND cxp_pagos_detalles.cancelacion=FALSE "+where
        +" AND (to_char(cxp_pagos.fecha_pago,'yyyymmdd')::integer BETWEEN to_char('"+fecha_inicial+"'::timestamp with time zone,'yyyymmdd')::integer AND to_char('"+fecha_final+"'::timestamp with time zone,'yyyymmdd')::integer) "
        +" ORDER BY cxp_prov.razon_social, cxp_pagos.fecha_pago;";
        
        //System.out.println("getPagosDiaria:"+ sql_to_query);

        ArrayList<HashMap<String, String>> hm = (ArrayList<HashMap<String, String>>) this.jdbcTemplate.query(
                sql_to_query,
            new Object[]{}, new RowMapper() {
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    HashMap<String, String> row = new HashMap<String, String>();
                    row.put("factura",rs.getString("factura"));
                    row.put("fecha_factura",rs.getString("fecha_factura"));
                    row.put("id_proveedor",String.valueOf(rs.getInt("id_proveedor")));
                    row.put("proveedor",rs.getString("proveedor"));
                    row.put("id_moneda_fac",String.valueOf(rs.getInt("id_moneda_fac")));
                    row.put("simbolo_moneda_fac",rs.getString("simbolo_moneda_fac"));
                    row.put("moneda_fac",rs.getString("moneda_fac"));
                    row.put("monto_factura", StringHelper.roundDouble(rs.getString("monto_factura"), 2));
                    row.put("simbolo_moneda_aplicado",rs.getString("simbolo_moneda_aplicado"));
                    row.put("pago_aplicado", StringHelper.roundDouble(rs.getString("pago_aplicado"), 2));
                    row.put("fecha_pago",rs.getString("fecha_pago"));
                    row.put("id_moneda_pago",String.valueOf(rs.getInt("id_moneda_pago")));
                    row.put("simbolo_moneda_pago",rs.getString("simbolo_moneda_pago"));
                    row.put("moneda_pago",rs.getString("moneda_pago"));
                    row.put("monto_pago", StringHelper.roundDouble(rs.getString("monto_pago"), 2));
                    return row;
                }
            }
        );
        return hm;
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
                    +"inv_prod.id,"
                    +"inv_prod.sku,"
                    +"inv_prod.descripcion, "
                    + "inv_prod.unidad_id, "
                    + "inv_prod_unidades.titulo AS unidad, "
                    +"inv_prod_tipos.titulo AS tipo,"
                    + "inv_prod_unidades.decimales "
		+"FROM inv_prod "
                + "LEFT JOIN inv_prod_tipos ON inv_prod_tipos.id=inv_prod.tipo_de_producto_id "
                + "LEFT JOIN inv_prod_unidades ON inv_prod_unidades.id=inv_prod.unidad_id "
                + "WHERE inv_prod.empresa_id="+id_empresa+" "
                + "AND inv_prod.borrado_logico=false "+where+" "
                + "ORDER BY inv_prod.descripcion;";
        //log.log(Level.INFO, "Ejecutando query de {0}", sql_to_query);
        
        //System.out.println("sql_to_query: "+sql_to_query);

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

    
    

    @Override
    public ArrayList<HashMap<String, String>> getEntradas_PresentacionesProducto(String sku, Integer id_empresa) {
        String sql_to_query = ""
        + "SELECT "
            +"inv_prod.id,"
            +"inv_prod.sku,"
            +"inv_prod.descripcion as titulo,"
            +"inv_prod_unidades.titulo as unidad,"
            +"inv_prod_presentaciones.id as id_presentacion,"
            +"inv_prod_presentaciones.titulo as presentacion, "
            +"(CASE WHEN inv_prod.ieps=0 THEN 0 ELSE gral_ieps.id END) AS ieps_id, "
            +"(CASE WHEN inv_prod.ieps=0 THEN 0 ELSE (gral_ieps.tasa::double precision/100) END) AS ieps_tasa, "
            +"(CASE WHEN inv_prod.gral_imptos_ret_id=0 THEN 0 ELSE gral_imptos_ret.id END) AS ret_id, "
            +"(CASE WHEN inv_prod.gral_imptos_ret_id=0 THEN 0 ELSE (gral_imptos_ret.tasa::double precision/100) END) AS ret_tasa "
        +"FROM inv_prod "
        +"LEFT JOIN inv_prod_unidades on inv_prod_unidades.id = inv_prod.unidad_id "
        +"LEFT JOIN inv_prod_pres_x_prod on inv_prod_pres_x_prod.producto_id = inv_prod.id "
        +"LEFT JOIN inv_prod_presentaciones on inv_prod_presentaciones.id = inv_prod_pres_x_prod.presentacion_id "
        +"LEFT JOIN gral_ieps ON gral_ieps.id=inv_prod.ieps "
        +"LEFT JOIN gral_imptos_ret ON gral_imptos_ret.id=inv_prod.gral_imptos_ret_id "
        +"where inv_prod.sku ILIKE '"+sku+"' AND inv_prod.borrado_logico=false AND inv_prod.empresa_id="+id_empresa;

        //log.log(Level.INFO, "Ejecutando query de {0}", sql_to_query);

        ArrayList<HashMap<String, String>> hm_presentaciones = (ArrayList<HashMap<String, String>>) this.jdbcTemplate.query(
            sql_to_query,
            new Object[]{}, new RowMapper(){
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    HashMap<String, String> row = new HashMap<String, String>();
                    row.put("id",String.valueOf(rs.getInt("id")));
                    row.put("sku",rs.getString("sku"));
                    row.put("titulo",rs.getString("titulo"));
                    row.put("unidad",rs.getString("unidad"));
                    row.put("id_presentacion",rs.getString("id_presentacion"));
                    row.put("presentacion",rs.getString("presentacion"));
                    row.put("ieps_id",rs.getString("ieps_id"));
                    row.put("ieps_tasa",StringHelper.roundDouble(rs.getString("ieps_tasa"),4));
                    row.put("ret_id",String.valueOf(rs.getInt("ret_id")));
                    row.put("ret_tasa",StringHelper.roundDouble(rs.getString("ret_tasa"),4));
                    return row;
                }
            }
        );
        return hm_presentaciones;
    }

    
    
    
        
    //metodos para aplicativo de facturas de proveedores
    @Override
    public ArrayList<HashMap<String, Object>> getCxpFacturas2_PaginaGrid(String data_string, int offset, int pageSize, String orderBy, String asc) {
        String sql_busqueda = "select id from gral_bus_catalogos(?) as foo (id integer)";
        String sql_to_query = ""
        + "SELECT cxp_facturas.id, "
            + "cxp_facturas.serie_folio AS factura, "
            + "(case when cxp_facturas.tipo_factura_proveedor=1 then 'COMPRAS' when cxp_facturas.tipo_factura_proveedor=2 then 'SERVICIOS U HONORARIOS' when cxp_facturas.tipo_factura_proveedor=3 then 'OTROS INSUMOS' when cxp_facturas.tipo_factura_proveedor=4 then 'FLETES' else '' end) as tipo,"
            + "cxp_prov.razon_social AS proveedor, "
            + "gral_mon.descripcion_abr AS moneda, "
            + "cxp_facturas.monto_total AS total, "
            + "to_char(cxp_facturas.fecha_factura,'dd/mm/yyyy') AS fecha_factura, "
            //+ "(CASE WHEN cxp_facturas.cancelacion=FALSE THEN '' ELSE 'CANCELADO' END) AS estado " 
            + "(CASE WHEN cxp_facturas.estatus=0 THEN '' WHEN cxp_facturas.estatus=1 THEN 'PAGO PARCIAL' WHEN cxp_facturas.estatus=2 THEN 'PAGADO' WHEN cxp_facturas.estatus=3 THEN 'CANCELADO' ELSE 'CANCELADO' END) AS estado " 
        + "FROM cxp_facturas "
        + "JOIN cxp_prov ON cxp_prov.id=cxp_facturas.cxc_prov_id "
        + "JOIN gral_mon ON gral_mon.id=cxp_facturas.moneda_id " 
        +"JOIN ("+sql_busqueda+") as subt on subt.id=cxp_facturas.id "
        + "ORDER  BY "+orderBy+" "+asc+" LIMIT ? OFFSET ?";
                
        //-- 0=Nuevo, 1=Pago Parcial, 2=Pagado, 3=Cancelado
        //System.out.println("sql_to_query: "+sql_to_query);
        
        ArrayList<HashMap<String, Object>> hm = (ArrayList<HashMap<String, Object>>) this.jdbcTemplate.query(
            sql_to_query, 
            new Object[]{data_string,new Integer(pageSize),new Integer(offset)}, new RowMapper() {
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    HashMap<String, Object> row = new HashMap<String, Object>();
                    row.put("id",String.valueOf(rs.getInt("id")));
                    row.put("factura",rs.getString("factura"));
                    row.put("tipo",rs.getString("tipo"));
                    row.put("proveedor",rs.getString("proveedor"));
                    row.put("moneda",rs.getString("moneda"));
                    row.put("total",StringHelper.AgregaComas(StringHelper.roundDouble(rs.getString("total"),2)));
                    row.put("fecha_factura",rs.getString("fecha_factura"));
                    row.put("estado",rs.getString("estado"));
                    return row;
                }
            }
        );
        return hm; 
    }
    
    
    
    @Override
    public ArrayList<HashMap<String, String>> getCxpFacturas2_Datos(Integer id) {
        String sql_to_query = ""
        + "SELECT cxp_facturas.id, "
            + "cxp_facturas.cxc_prov_id, "
            + "cxp_facturas.serie_folio as factura, "
            + "cxp_facturas.flete, "
            + "cxp_facturas.subtotal, "
            + "cxp_facturas.iva, "
            + "cxp_facturas.retencion, "
            + "cxp_facturas.monto_total as total, "
            + "cxp_facturas.moneda_id, "
            + "cxp_facturas.tipo_cambio, "
            + "to_char(cxp_facturas.fecha_factura,'yyyy-mm-dd' ) as fecha_factura,"
            + "(CASE WHEN cxp_facturas.cancelacion=FALSE THEN 0 ELSE 1 END) AS cancelado, "
            + "cxp_facturas.empresa_id, "
            + "cxp_facturas.numero_guia, "
            + "cxp_facturas.orden_compra, "
            + "cxp_facturas.observaciones, "
            + "cxp_facturas.fletera_id, "
            + "cxp_facturas.dias_credito_id, "
            + "cxp_facturas.tipo_factura_proveedor, "
            + "(CASE WHEN cxp_facturas.cancelacion=FALSE THEN '' ELSE 'CANCELADO' END) AS estado,"
            + "cxp_facturas.estatus, " 
            + "cxp_prov.rfc, "
            + "cxp_prov.razon_social, "
            + "cxp_prov.folio as no_prov, "
            /*
            + "cxp_prov.calle||' '||cxp_prov.numero||', '||cxp_prov.colonia||', '||gral_mun.titulo||', '||gral_edo.titulo||', '||gral_pais.titulo ||' C.P. '||cxp_prov.cp as direccion, "
            + "cxp_prov.calle,"
            + "cxp_prov.numero,"
            + "cxp_prov.colonia,"
            + "gral_mun.titulo AS municipio,"
            + "gral_edo.titulo AS estado,"
            + "gral_pais.titulo AS pais,"
            + "cxp_prov.telefono1 as telefono, "
            + "cxp_prov.cp, "
            */
            + "cxp_prov.proveedortipo_id,"
            + "cxp_prov.impuesto as iva_id "
        + "FROM cxp_facturas "
        + "left join cxp_prov on cxp_prov.id=cxp_facturas.cxc_prov_id "
        + "JOIN gral_pais ON gral_pais.id=cxp_prov.pais_id "
        + "JOIN gral_edo ON gral_edo.id=cxp_prov.estado_id "
        + "JOIN gral_mun ON gral_mun.id=cxp_prov.municipio_id "
        +"WHERE cxp_facturas.id="+ id + ";";
        
        //log.log(Level.INFO, "Ejecutando query de {0}", sql_to_query);
        ArrayList<HashMap<String, String>> hm_datos_entrada = (ArrayList<HashMap<String, String>>) this.jdbcTemplate.query(
            sql_to_query,
            new Object[]{}, new RowMapper(){
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    HashMap<String, String> row = new HashMap<String, String>();
                    row.put("id",String.valueOf(rs.getInt("id")));
                    row.put("empresa_id",String.valueOf(rs.getInt("empresa_id")));
                    row.put("fletera_id",String.valueOf(rs.getInt("fletera_id")));
                    row.put("dias_credito_id",String.valueOf(rs.getInt("dias_credito_id")));
                    row.put("tipo_factura_proveedor",String.valueOf(rs.getInt("tipo_factura_proveedor")));
                    row.put("factura",rs.getString("factura"));
                    row.put("moneda_id",rs.getString("moneda_id"));
                    row.put("fecha_factura",rs.getString("fecha_factura"));
                    row.put("numero_guia",rs.getString("numero_guia"));
                    row.put("orden_compra",rs.getString("orden_compra"));
                    row.put("observaciones",rs.getString("observaciones"));
                    row.put("tipo_cambio",StringHelper.roundDouble(rs.getString("tipo_cambio"),4));
                    row.put("flete",StringHelper.roundDouble(rs.getString("flete"),2));
                    row.put("subtotal",StringHelper.roundDouble(rs.getString("subtotal"),2));
                    row.put("iva",StringHelper.roundDouble(rs.getString("iva"),2));
                    row.put("retencion",StringHelper.roundDouble(rs.getString("retencion"),2));
                    row.put("total",StringHelper.roundDouble(rs.getString("total"),2));
                    row.put("estado",rs.getString("estado"));
                    row.put("estatus",rs.getString("estatus"));
                    row.put("cancelado",rs.getString("cancelado"));
                    
                    row.put("cxc_prov_id",String.valueOf(rs.getInt("cxc_prov_id")));
                    row.put("rfc",rs.getString("rfc"));
                    row.put("razon_social",rs.getString("razon_social"));
                    row.put("no_prov",rs.getString("no_prov"));
                    row.put("proveedortipo_id",rs.getString("proveedortipo_id"));
                    row.put("iva_id",rs.getString("iva_id"));
                    return row;
                }
            }
        );
        return hm_datos_entrada; 
    }
    
    

    @Override
    public ArrayList<HashMap<String, String>> getCxpFacturas2_DatosGrid(Integer id) {
        String sql_to_query = ""
        + "SELECT "
            + "cxp_fac_det.id as det_id, "
            + "cxp_fac_det.inv_prod_id as prod_id, "
            + "cxp_fac_det.inv_prod_pres_id as pres_id, "
            + "cxp_fac_det.codigo_producto, "
            + "cxp_fac_det.descripcion, "
            + "cxp_fac_det.unidad_medida, "
            + "cxp_fac_det.presentacion, "
            + "cxp_fac_det.cantidad, "
            + "cxp_fac_det.costo_unitario, "
            + "(cxp_fac_det.cantidad * cxp_fac_det.costo_unitario) AS importe, "
            + "cxp_fac_det.gral_imp_id, "
            + "cxp_fac_det.valor_imp, "
            + "(((cxp_fac_det.cantidad * cxp_fac_det.costo_unitario::double precision) + (CASE WHEN cxp_fac_det.gral_ieps_id>0 THEN ((cxp_fac_det.costo_unitario * cxp_fac_det.cantidad::double precision) * cxp_fac_det.valor_ieps::double precision) ELSE 0 END)) * cxp_fac_det.valor_imp) as iva_importe, "
            + "cxp_fac_det.gral_ieps_id,"
            + "cxp_fac_det.valor_ieps,"
            + "(CASE WHEN cxp_fac_det.gral_ieps_id>0 THEN ((cxp_fac_det.costo_unitario * cxp_fac_det.cantidad::double precision) * cxp_fac_det.valor_ieps::double precision) ELSE 0 END) AS importe_ieps, "
            + "cxp_fac_det.gral_imptos_ret_id as ret_id,"
            + "cxp_fac_det.tasa_ret as ret_tasa,"
            + "(CASE WHEN cxp_fac_det.gral_imptos_ret_id>0 THEN ((cxp_fac_det.costo_unitario * cxp_fac_det.cantidad::double precision) * cxp_fac_det.tasa_ret::double precision) ELSE 0 END) AS ret_importe "
        + "FROM cxp_facturas_detalle as cxp_fac_det "
        + "WHERE cxp_fac_det.cxp_facturas_id="+ id + " ORDER BY cxp_fac_det.id;";
        
        //log.log(Level.INFO, "Ejecutando query de {0}", sql_to_query);
        ArrayList<HashMap<String, String>> hm_datos_entrada = (ArrayList<HashMap<String, String>>) this.jdbcTemplate.query(
            sql_to_query,
            new Object[]{}, new RowMapper(){
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    HashMap<String, String> row = new HashMap<String, String>();
                    row.put("det_id",rs.getString("det_id"));
                    row.put("prod_id",rs.getString("prod_id"));
                    row.put("pres_id",rs.getString("pres_id"));
                    row.put("codigo_producto",rs.getString("codigo_producto"));
                    row.put("descripcion",rs.getString("descripcion"));
                    row.put("unidad_medida",rs.getString("unidad_medida"));
                    row.put("presentacion",rs.getString("presentacion"));
                    row.put("cantidad",StringHelper.roundDouble(rs.getString("cantidad"),4));
                    row.put("costo_unitario",StringHelper.roundDouble(rs.getString("costo_unitario"),4));
                    row.put("importe",StringHelper.roundDouble(rs.getString("importe"),4));
                    row.put("ieps_id",rs.getString("gral_ieps_id"));
                    row.put("valor_ieps",StringHelper.roundDouble(rs.getString("valor_ieps"),4));
                    row.put("importe_ieps",StringHelper.roundDouble(rs.getString("importe_ieps"),4));
                    row.put("gral_imp_id",rs.getString("gral_imp_id"));
                    row.put("valor_imp",StringHelper.roundDouble(rs.getString("valor_imp"),2));
                    row.put("iva_importe",StringHelper.roundDouble(rs.getString("iva_importe"),4));
                    row.put("ret_id",String.valueOf(rs.getInt("ret_id")));
                    row.put("ret_tasa",StringHelper.roundDouble(rs.getString("ret_tasa"),4));
                    row.put("ret_importe",StringHelper.roundDouble(rs.getString("ret_importe"),4));
                    return row;
                }
            }
        );
        return hm_datos_entrada;  
    }
    
    
}
