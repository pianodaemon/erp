package com.agnux.kemikal.springdaos;


import com.agnux.common.helpers.StringHelper;
import com.agnux.kemikal.interfacedaos.CxcInterfaceDao;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;


public class CxcSpringDao implements CxcInterfaceDao{
    private JdbcTemplate jdbcTemplate;

    public JdbcTemplate getJdbcTemplate() {
        return jdbcTemplate;
    }

    public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public String q_serie_folio(final Integer usr_id) {
        String SQL = "select fac_cfds_conf_folios.serie as serie, "
                + "fac_cfds_conf_folios.folio_actual::character varying as folio "
                + "FROM gral_suc AS SUC "
                + "LEFT JOIN fac_cfds_conf ON fac_cfds_conf.gral_suc_id = SUC.id "
                + "LEFT JOIN fac_cfds_conf_folios ON fac_cfds_conf_folios.fac_cfds_conf_id = fac_cfds_conf.id "
                + "LEFT JOIN gral_usr_suc AS USR_SUC ON USR_SUC.gral_suc_id = SUC.id "
                + "WHERE fac_cfds_conf_folios.proposito = \'PAG\' "
                + "AND USR_SUC.gral_usr_id=" + usr_id;
        Logger.getLogger(FacturasSpringDao.class.getName()).log(Level.INFO, SQL);
        Map<String, Object> map_iva = this.getJdbcTemplate().queryForMap(SQL);
        return (map_iva.get("serie").toString() + map_iva.get("folio").toString());
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
        //System.out.println(sql_to_query);
        String valor_retorno="";
        Map<String, Object> update = this.getJdbcTemplate().queryForMap(sql_to_query);
        valor_retorno = update.get("gral_adm_catalogos").toString();
        return valor_retorno;
    }


    //metodo que utiliza el procedimiento cxc_adm_procesos
    @Override
    public String selectFunctionForCxcAdmProcesos(String campos_data, String extra_data_array) {
        String sql_to_query = "select * from cxc_adm_procesos('"+campos_data+"',array["+extra_data_array+"]);";

        String valor_retorno="";
        Map<String, Object> update = this.getJdbcTemplate().queryForMap(sql_to_query);
        valor_retorno = update.get("cxc_adm_procesos").toString();
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
    public ArrayList<HashMap<String, Object>> getClientes_PaginaGrid(String data_string, int offset, int pageSize, String orderBy, String asc) {
        String sql_busqueda = "select id from gral_bus_catalogos(?) as foo (id integer)";

	String sql_to_query = "SELECT "
				+"cxc_clie.id, "
				+"cxc_clie.numero_control, "
				+"cxc_clie.razon_social, "
				+"cxc_clie.rfc, "
				+"cxc_clie_clases.titulo AS tipo_cliente, "
                                +"(CASE WHEN cxc_clie.telefono1='' OR cxc_clie.telefono1 IS NULL THEN cxc_clie.telefono2 ELSE cxc_clie.telefono1 END) AS tel "
			+"FROM cxc_clie "
			+"LEFT JOIN cxc_clie_clases ON cxc_clie_clases.id = cxc_clie.clienttipo_id "
                        +"JOIN ("+sql_busqueda+") as subt on subt.id=cxc_clie.id "
                        +"order by "+orderBy+" "+asc+" limit ? OFFSET ?";

        //System.out.println("Busqueda GetPage: "+sql_to_query);

        //log.log(Level.INFO, "Ejecutando query de {0}", sql_to_query);
        ArrayList<HashMap<String, Object>> hm = (ArrayList<HashMap<String, Object>>) this.jdbcTemplate.query(
            sql_to_query,
            new Object[]{data_string, new Integer(pageSize),new Integer(offset)}, new RowMapper() {
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    HashMap<String, Object> row = new HashMap<String, Object>();
                    row.put("id",rs.getInt("id"));
                    row.put("numero_control",rs.getString("numero_control"));
                    row.put("razon_social",rs.getString("razon_social"));
                    row.put("rfc",rs.getString("rfc"));
                    row.put("tipo_cliente",rs.getString("tipo_cliente"));
                    row.put("tel",rs.getString("tel"));
                    return row;
                }
            }
        );
        return hm;
    }




    @Override
    public ArrayList<HashMap<String, Object>> getCliente_Datos(Integer id) {

        String sql_query = ""
                + "SELECT "
                    +"cxc_clie.id as id_cliente, "
                    +"cxc_clie.numero_control, "
                    +"cxc_clie.rfc, "
                    +"cxc_clie.curp, "
                    +"cxc_clie.razon_social, "
                    +"cxc_clie.clave_comercial, "
                    +"cxc_clie.calle, "
                    +"cxc_clie.numero, "
                    +"cxc_clie.entre_calles, "
                    +"cxc_clie.numero_exterior, "
                    +"cxc_clie.colonia, "
                    +"cxc_clie.cp, "
                    +"cxc_clie.pais_id, "
                    +"cxc_clie.estado_id, "
                    +"cxc_clie.municipio_id, "
                    +"cxc_clie.localidad_alternativa, "
                    +"cxc_clie.telefono1, "
                    +"cxc_clie.extension1, "
                    +"cxc_clie.fax, "
                    +"cxc_clie.telefono2, "
                    +"cxc_clie.extension2, "
                    +"cxc_clie.email, "
                    +"cxc_clie.cxc_agen_id, "
                    +"cxc_clie.contacto, "
                    +"cxc_clie.zona_id, "
                    +"cxc_clie.cxc_clie_grupo_id, "
                    +"cxc_clie.clienttipo_id, "
                    +"cxc_clie.clasif_1, "
                    +"cxc_clie.clasif_2, "
                    +"cxc_clie.clasif_3, "
                    +"cxc_clie.moneda, "
                    +"(CASE WHEN cxc_clie.filial=TRUE THEN 'true' ELSE 'false' END) AS filial, "
                    +"(CASE WHEN cxc_clie.estatus=TRUE THEN 'true' ELSE 'false' END) AS estatus, "
                    +"cxc_clie.gral_imp_id, "
                    +"cxc_clie.limite_credito, "
                    +"cxc_clie.dias_credito_id, "
                    +"(CASE WHEN cxc_clie.credito_suspendido=TRUE THEN 'true' ELSE 'false' END) AS credito_suspendido, "
                    +"cxc_clie.credito_a_partir, "
                    +"cxc_clie.cxp_prov_tipo_embarque_id, "
                    +"cxc_clie.dias_caducidad_cotizacion, "
                    +"cxc_clie.condiciones, "
                    +"cxc_clie.observaciones, "
                    +"cxc_clie.contacto_compras_nombre, "
                    +"cxc_clie.contacto_compras_puesto, "
                    +"cxc_clie.contacto_compras_calle, "
                    +"cxc_clie.contacto_compras_numero, "
                    +"cxc_clie.contacto_compras_colonia, "
                    +"cxc_clie.contacto_compras_cp, "
                    +"cxc_clie.contacto_compras_entre_calles, "
                    +"cxc_clie.contacto_compras_pais_id, "
                    +"cxc_clie.contacto_compras_estado_id, "
                    +"cxc_clie.contacto_compras_municipio_id, "
                    +"cxc_clie.contacto_compras_telefono1, "
                    +"cxc_clie.contacto_compras_extension1, "
                    +"cxc_clie.contacto_compras_fax, "
                    +"cxc_clie.contacto_compras_telefono2, "
                    +"cxc_clie.contacto_compras_extension2, "
                    +"cxc_clie.contacto_compras_email, "
                    +"cxc_clie.contacto_pagos_nombre, "
                    +"cxc_clie.contacto_pagos_puesto, "
                    +"cxc_clie.contacto_pagos_calle, "
                    +"cxc_clie.contacto_pagos_numero, "
                    +"cxc_clie.contacto_pagos_colonia, "
                    +"cxc_clie.contacto_pagos_cp, "
                    +"cxc_clie.contacto_pagos_entre_calles, "
                    +"cxc_clie.contacto_pagos_pais_id, "
                    +"cxc_clie.contacto_pagos_estado_id, "
                    +"cxc_clie.contacto_pagos_municipio_id, "
                    +"cxc_clie.contacto_pagos_telefono1, "
                    +"cxc_clie.contacto_pagos_extension1, "
                    +"cxc_clie.contacto_pagos_fax, "
                    +"cxc_clie.contacto_pagos_telefono2, "
                    +"cxc_clie.contacto_pagos_extension2, "
                    +"cxc_clie.contacto_pagos_email, "
                    +"cxc_clie.empresa_immex, "
                    +"cxc_clie.tasa_ret_immex, "
                    +"cxc_clie.dia_revision, "
                    +"cxc_clie.dia_pago, "
                    +"cxc_clie.cta_pago_mn, "
                    +"cxc_clie.cta_pago_usd,"
                    +"(CASE WHEN cxc_clie.lista_precio IS NULL THEN 0 ELSE cxc_clie.lista_precio END) AS lista_precio,"
                    +"cxc_clie.fac_metodos_pago_id "
            +"FROM cxc_clie "
            +"WHERE cxc_clie.borrado_logico=false AND cxc_clie.id=?;";

        //System.out.println("Ejecutando getCliente_Datos:"+ sql_query);
        //System.out.println("IdCliente: "+id);

        ArrayList<HashMap<String, Object>> cliente = (ArrayList<HashMap<String, Object>>) this.jdbcTemplate.query(
            sql_query,
            new Object[]{new Integer(id)}, new RowMapper() {
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    HashMap<String, Object> row = new HashMap<String, Object>();
                    row.put("id_cliente",rs.getInt("id_cliente"));
                    row.put("numero_control",rs.getString("numero_control"));
                    row.put("rfc",rs.getString("rfc"));
                    row.put("curp",rs.getString("curp"));
                    row.put("razon_social",rs.getString("razon_social"));
                    row.put("clave_comercial",rs.getString("clave_comercial"));
                    row.put("calle",rs.getString("calle"));
                    row.put("numero",rs.getString("numero"));
                    row.put("entre_calles",rs.getString("entre_calles"));
                    row.put("numero_exterior",rs.getString("numero_exterior"));
                    row.put("colonia",rs.getString("colonia"));
                    row.put("cp",rs.getString("cp"));
                    row.put("pais_id",rs.getString("pais_id"));
                    row.put("estado_id",rs.getString("estado_id"));
                    row.put("municipio_id",rs.getString("municipio_id"));
                    row.put("localidad_alternativa",rs.getString("localidad_alternativa"));
                    row.put("telefono1",rs.getString("telefono1"));
                    row.put("extension1",rs.getString("extension1"));
                    row.put("fax",rs.getString("fax"));
                    row.put("telefono2",rs.getString("telefono2"));
                    row.put("extension2",rs.getString("extension2"));
                    row.put("email",rs.getString("email"));
                    row.put("cxc_agen_id",rs.getString("cxc_agen_id"));
                    row.put("contacto",rs.getString("contacto"));
                    row.put("zona_id",rs.getString("zona_id"));
                    row.put("cxc_clie_grupo_id",rs.getString("cxc_clie_grupo_id"));
                    row.put("clienttipo_id",rs.getString("clienttipo_id"));
                    row.put("clasif_1",rs.getString("clasif_1"));
                    row.put("clasif_2",rs.getString("clasif_2"));
                    row.put("clasif_3",rs.getString("clasif_3"));
                    row.put("moneda",rs.getInt("moneda"));
                    row.put("filial",rs.getString("filial"));
                    row.put("estatus",rs.getString("estatus"));
                    row.put("gral_imp_id",rs.getString("gral_imp_id"));
                    row.put("limite_credito",StringHelper.roundDouble(rs.getString("limite_credito"),2));
                    row.put("dias_credito_id",rs.getString("dias_credito_id"));
                    row.put("credito_suspendido",rs.getString("credito_suspendido"));
                    row.put("credito_a_partir",rs.getString("credito_a_partir"));
                    row.put("cxp_prov_tipo_embarque_id",rs.getString("cxp_prov_tipo_embarque_id"));
                    row.put("dias_caducidad_cotizacion",rs.getString("dias_caducidad_cotizacion"));
                    row.put("condiciones",rs.getString("condiciones"));
                    row.put("observaciones",rs.getString("observaciones"));
                    row.put("contacto_compras_nombre",rs.getString("contacto_compras_nombre"));
                    row.put("contacto_compras_puesto",rs.getString("contacto_compras_puesto"));
                    row.put("contacto_compras_calle",rs.getString("contacto_compras_calle"));
                    row.put("contacto_compras_numero",rs.getString("contacto_compras_numero"));
                    row.put("contacto_compras_colonia",rs.getString("contacto_compras_colonia"));
                    row.put("contacto_compras_cp",rs.getString("contacto_compras_cp"));
                    row.put("contacto_compras_entre_calles",rs.getString("contacto_compras_entre_calles"));
                    row.put("contacto_compras_pais_id",rs.getString("contacto_compras_pais_id"));
                    row.put("contacto_compras_estado_id",rs.getString("contacto_compras_estado_id"));
                    row.put("contacto_compras_municipio_id",rs.getString("contacto_compras_municipio_id"));
                    row.put("contacto_compras_telefono1",rs.getString("contacto_compras_telefono1"));
                    row.put("contacto_compras_extension1",rs.getString("contacto_compras_extension1"));
                    row.put("contacto_compras_fax",rs.getString("contacto_compras_fax"));
                    row.put("contacto_compras_telefono2",rs.getString("contacto_compras_telefono2"));
                    row.put("contacto_compras_extension2",rs.getString("contacto_compras_extension2"));
                    row.put("contacto_compras_email",rs.getString("contacto_compras_email"));
                    row.put("contacto_pagos_nombre",rs.getString("contacto_pagos_nombre"));
                    row.put("contacto_pagos_puesto",rs.getString("contacto_pagos_puesto"));
                    row.put("contacto_pagos_calle",rs.getString("contacto_pagos_calle"));
                    row.put("contacto_pagos_numero",rs.getString("contacto_pagos_numero"));
                    row.put("contacto_pagos_colonia",rs.getString("contacto_pagos_colonia"));
                    row.put("contacto_pagos_cp",rs.getString("contacto_pagos_cp"));
                    row.put("contacto_pagos_entre_calles",rs.getString("contacto_pagos_entre_calles"));
                    row.put("contacto_pagos_pais_id",rs.getString("contacto_pagos_pais_id"));
                    row.put("contacto_pagos_estado_id",rs.getString("contacto_pagos_estado_id"));
                    row.put("contacto_pagos_municipio_id",rs.getString("contacto_pagos_municipio_id"));
                    row.put("contacto_pagos_telefono1",rs.getString("contacto_pagos_telefono1"));
                    row.put("contacto_pagos_extension1",rs.getString("contacto_pagos_extension1"));
                    row.put("contacto_pagos_fax",rs.getString("contacto_pagos_fax"));
                    row.put("contacto_pagos_telefono2",rs.getString("contacto_pagos_telefono2"));
                    row.put("contacto_pagos_extension2",rs.getString("contacto_pagos_extension2"));
                    row.put("contacto_pagos_email",rs.getString("contacto_pagos_email"));
                    row.put("empresa_immex",String.valueOf(rs.getBoolean("empresa_immex")));
                    row.put("tasa_ret_immex",StringHelper.roundDouble(rs.getDouble("tasa_ret_immex"),2));
                    row.put("dia_revision",String.valueOf(rs.getInt("dia_revision")));
                    row.put("dia_pago",String.valueOf(rs.getInt("dia_pago")));
                    row.put("cta_pago_mn",rs.getString("cta_pago_mn"));
                    row.put("cta_pago_usd",rs.getString("cta_pago_usd"));
                    row.put("lista_precio",String.valueOf(rs.getInt("lista_precio")));
                    row.put("metodo_pago_id",String.valueOf(rs.getInt("fac_metodos_pago_id")));

                    return row;
                }
            }
        );
        return cliente;
    }





    //obtiene datos de configuracion de Cuentas Contables
    @Override
    public ArrayList<HashMap<String, Object>> getCliente_DatosContabilidad(Integer id) {

        String sql_query = ""
                + "SELECT "
                    +"cxc_clie.id as id_cliente, "
                    + "(CASE WHEN tbl_cta_activo.id IS NULL THEN 0 ELSE tbl_cta_activo.id END) AS ac_id_cta, "
                    + "(CASE WHEN tbl_cta_activo.cta IS NULL OR tbl_cta_activo.cta=0 THEN '' ELSE tbl_cta_activo.cta::character varying END) AS ac_cta, "
                    + "(CASE WHEN tbl_cta_activo.subcta IS NULL OR tbl_cta_activo.subcta=0 THEN '' ELSE tbl_cta_activo.subcta::character varying END) AS ac_subcta, "
                    + "(CASE WHEN tbl_cta_activo.ssubcta IS NULL OR tbl_cta_activo.ssubcta=0 THEN '' ELSE tbl_cta_activo.ssubcta::character varying END) AS ac_ssubcta, "
                    + "(CASE WHEN tbl_cta_activo.sssubcta IS NULL OR tbl_cta_activo.sssubcta=0 THEN '' ELSE tbl_cta_activo.sssubcta::character varying END) AS ac_sssubcta,"
                    + "(CASE WHEN tbl_cta_activo.ssssubcta IS NULL OR tbl_cta_activo.ssssubcta=0 THEN '' ELSE tbl_cta_activo.ssssubcta::character varying END) AS ac_ssssubcta, "
                    + "(CASE WHEN tbl_cta_activo.descripcion IS NULL OR tbl_cta_activo.descripcion='' THEN  (CASE WHEN tbl_cta_activo.descripcion_ing IS NULL OR tbl_cta_activo.descripcion_ing='' THEN  tbl_cta_activo.descripcion_otr ELSE tbl_cta_activo.descripcion_ing END )  ELSE tbl_cta_activo.descripcion END ) AS ac_descripcion, "
                    + "(CASE WHEN tbl_cta_ingreso.id IS NULL THEN 0 ELSE  tbl_cta_ingreso.id END ) AS ing_id_cta, "
                    + "(CASE WHEN tbl_cta_ingreso.cta IS NULL OR tbl_cta_ingreso.cta=0 THEN '' ELSE tbl_cta_ingreso.cta::character varying END ) AS ing_cta, "
                    + "(CASE WHEN tbl_cta_ingreso.subcta IS NULL OR tbl_cta_ingreso.subcta=0 THEN '' ELSE tbl_cta_ingreso.subcta::character varying END ) AS ing_subcta, "
                    + "(CASE WHEN tbl_cta_ingreso.ssubcta IS NULL OR tbl_cta_ingreso.ssubcta=0 THEN '' ELSE tbl_cta_ingreso.ssubcta::character varying END )  AS ing_ssubcta, "
                    + "(CASE WHEN tbl_cta_ingreso.sssubcta IS NULL OR tbl_cta_ingreso.sssubcta=0 THEN '' ELSE tbl_cta_ingreso.sssubcta::character varying END ) AS ing_sssubcta,"
                    + "(CASE WHEN tbl_cta_ingreso.ssssubcta IS NULL OR tbl_cta_ingreso.ssssubcta=0 THEN '' ELSE tbl_cta_ingreso.ssssubcta::character varying END) AS ing_ssssubcta, "
                    + "(CASE WHEN tbl_cta_ingreso.descripcion IS NULL OR tbl_cta_ingreso.descripcion='' THEN  (CASE WHEN tbl_cta_ingreso.descripcion_ing IS NULL OR tbl_cta_ingreso.descripcion_ing='' THEN  tbl_cta_ingreso.descripcion_otr ELSE tbl_cta_ingreso.descripcion_ing END )  ELSE tbl_cta_ingreso.descripcion END ) AS ing_descripcion, "
                    + "(CASE WHEN tbl_cta_ietu.id IS NULL THEN 0 ELSE tbl_cta_ietu.id END) AS ietu_id_cta, "
                    + "(CASE WHEN tbl_cta_ietu.cta IS NULL OR tbl_cta_ietu.cta=0 THEN '' ELSE tbl_cta_ietu.cta::character varying END) AS ietu_cta, "
                    + "(CASE WHEN tbl_cta_ietu.subcta IS NULL OR tbl_cta_ietu.subcta=0 THEN '' ELSE tbl_cta_ietu.subcta::character varying END) AS ietu_subcta, "
                    + "(CASE WHEN tbl_cta_ietu.ssubcta IS NULL OR tbl_cta_ietu.ssubcta=0 THEN '' ELSE tbl_cta_ietu.ssubcta::character varying END) AS ietu_ssubcta, "
                    + "(CASE WHEN tbl_cta_ietu.sssubcta IS NULL OR tbl_cta_ietu.sssubcta=0 THEN '' ELSE tbl_cta_ietu.sssubcta::character varying END) AS ietu_sssubcta,"
                    + "(CASE WHEN tbl_cta_ietu.ssssubcta IS NULL OR tbl_cta_ietu.ssssubcta=0 THEN '' ELSE tbl_cta_ietu.ssssubcta::character varying END) AS ietu_ssssubcta, "
                    + "(CASE WHEN tbl_cta_ietu.descripcion IS NULL OR tbl_cta_ietu.descripcion='' THEN  (CASE WHEN tbl_cta_ietu.descripcion_ing IS NULL OR tbl_cta_ietu.descripcion_ing='' THEN  tbl_cta_ietu.descripcion_otr ELSE tbl_cta_ietu.descripcion_ing END )  ELSE tbl_cta_ietu.descripcion END ) AS ietu_descripcion, "
                    + "(CASE WHEN tbl_cta_comp.id IS NULL THEN 0 ELSE tbl_cta_comp.id END) AS comp_id_cta, "
                    + "(CASE WHEN tbl_cta_comp.cta IS NULL OR tbl_cta_comp.cta=0 THEN '' ELSE tbl_cta_comp.cta::character varying END) AS comp_cta, "
                    + "(CASE WHEN tbl_cta_comp.subcta IS NULL OR tbl_cta_comp.subcta=0 THEN '' ELSE tbl_cta_comp.subcta::character varying END) AS comp_subcta, "
                    + "(CASE WHEN tbl_cta_comp.ssubcta IS NULL OR tbl_cta_comp.ssubcta=0 THEN '' ELSE tbl_cta_comp.ssubcta::character varying END) AS comp_ssubcta, "
                    + "(CASE WHEN tbl_cta_comp.sssubcta IS NULL OR tbl_cta_comp.sssubcta=0 THEN '' ELSE tbl_cta_comp.sssubcta::character varying END) AS comp_sssubcta,"
                    + "(CASE WHEN tbl_cta_comp.ssssubcta IS NULL OR tbl_cta_comp.ssssubcta=0 THEN '' ELSE tbl_cta_comp.ssssubcta::character varying END) AS comp_ssssubcta, "
                    + "(CASE WHEN tbl_cta_comp.descripcion IS NULL OR tbl_cta_comp.descripcion='' THEN  (CASE WHEN tbl_cta_comp.descripcion_ing IS NULL OR tbl_cta_comp.descripcion_ing='' THEN  tbl_cta_comp.descripcion_otr ELSE tbl_cta_comp.descripcion_ing END )  ELSE tbl_cta_comp.descripcion END ) AS comp_descripcion, "
                    + "(CASE WHEN tbl_cta_ac_comp.id IS NULL THEN 0 ELSE tbl_cta_ac_comp.id END) AS ac_comp_id_cta, "
                    + "(CASE WHEN tbl_cta_ac_comp.cta IS NULL OR tbl_cta_ac_comp.cta=0 THEN '' ELSE tbl_cta_ac_comp.cta::character varying END) AS ac_comp_cta, "
                    + "(CASE WHEN tbl_cta_ac_comp.subcta IS NULL OR tbl_cta_ac_comp.subcta=0 THEN '' ELSE tbl_cta_ac_comp.subcta::character varying END) AS ac_comp_subcta, "
                    + "(CASE WHEN tbl_cta_ac_comp.ssubcta IS NULL OR tbl_cta_ac_comp.ssubcta=0 THEN '' ELSE tbl_cta_ac_comp.ssubcta::character varying END) AS ac_comp_ssubcta, "
                    + "(CASE WHEN tbl_cta_ac_comp.sssubcta IS NULL OR tbl_cta_ac_comp.sssubcta=0 THEN '' ELSE tbl_cta_ac_comp.sssubcta::character varying END) AS ac_comp_sssubcta,"
                    + "(CASE WHEN tbl_cta_ac_comp.ssssubcta IS NULL OR tbl_cta_ac_comp.ssssubcta=0 THEN '' ELSE tbl_cta_ac_comp.ssssubcta::character varying END ) AS ac_comp_ssssubcta, "
                    + "(CASE WHEN tbl_cta_ac_comp.descripcion IS NULL OR tbl_cta_ac_comp.descripcion='' THEN  (CASE WHEN tbl_cta_ac_comp.descripcion_ing IS NULL OR tbl_cta_ac_comp.descripcion_ing='' THEN  tbl_cta_ac_comp.descripcion_otr ELSE tbl_cta_ac_comp.descripcion_ing END )  ELSE tbl_cta_ac_comp.descripcion END ) AS ac_comp_descripcion "
            +"FROM cxc_clie "
            +"LEFT JOIN ctb_cta AS tbl_cta_activo ON tbl_cta_activo.id=cxc_clie.ctb_cta_id_activo "
            +"LEFT JOIN ctb_cta AS tbl_cta_ingreso ON tbl_cta_ingreso.id=cxc_clie.ctb_cta_id_ingreso "
            +"LEFT JOIN ctb_cta AS tbl_cta_ietu ON tbl_cta_ietu.id=cxc_clie.ctb_cta_id_ietu "
            +"LEFT JOIN ctb_cta AS tbl_cta_comp ON tbl_cta_comp.id=cxc_clie.ctb_cta_id_comple "
            +"LEFT JOIN ctb_cta AS tbl_cta_ac_comp ON tbl_cta_ac_comp.id=cxc_clie.ctb_cta_id_activo_comple "
            +"WHERE cxc_clie.borrado_logico=false AND cxc_clie.id=?;";

        //System.out.println("getCliente_DatosContabilidad: "+ sql_query);
        ArrayList<HashMap<String, Object>> contab = (ArrayList<HashMap<String, Object>>) this.jdbcTemplate.query(
            sql_query,
            new Object[]{new Integer(id)}, new RowMapper() {
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    HashMap<String, Object> row = new HashMap<String, Object>();
                    row.put("id_cliente",rs.getInt("id_cliente"));

                    row.put("ac_id_cta",rs.getString("ac_id_cta"));
                    row.put("ac_cta",rs.getString("ac_cta"));
                    row.put("ac_subcta",rs.getString("ac_subcta"));
                    row.put("ac_ssubcta",rs.getString("ac_ssubcta"));
                    row.put("ac_sssubcta",rs.getString("ac_sssubcta"));
                    row.put("ac_ssssubcta",rs.getString("ac_ssssubcta"));
                    row.put("ac_descripcion",rs.getString("ac_descripcion"));

                    row.put("ing_id_cta",rs.getString("ing_id_cta"));
                    row.put("ing_cta",rs.getString("ing_cta"));
                    row.put("ing_subcta",rs.getString("ing_subcta"));
                    row.put("ing_ssubcta",rs.getString("ing_ssubcta"));
                    row.put("ing_sssubcta",rs.getString("ing_sssubcta"));
                    row.put("ing_ssssubcta",rs.getString("ing_ssssubcta"));
                    row.put("ing_descripcion",rs.getString("ing_descripcion"));

                    row.put("ietu_id_cta",rs.getString("ietu_id_cta"));
                    row.put("ietu_cta",rs.getString("ietu_cta"));
                    row.put("ietu_subcta",rs.getString("ietu_subcta"));
                    row.put("ietu_ssubcta",rs.getString("ietu_ssubcta"));
                    row.put("ietu_sssubcta",rs.getString("ietu_sssubcta"));
                    row.put("ietu_ssssubcta",rs.getString("ietu_ssssubcta"));
                    row.put("ietu_descripcion",rs.getString("ietu_descripcion"));

                    row.put("comp_id_cta",rs.getString("comp_id_cta"));
                    row.put("comp_cta",rs.getString("comp_cta"));
                    row.put("comp_subcta",rs.getString("comp_subcta"));
                    row.put("comp_ssubcta",rs.getString("comp_ssubcta"));
                    row.put("comp_sssubcta",rs.getString("comp_sssubcta"));
                    row.put("comp_ssssubcta",rs.getString("comp_ssssubcta"));
                    row.put("comp_descripcion",rs.getString("comp_descripcion"));

                    row.put("ac_comp_id_cta",rs.getString("ac_comp_id_cta"));
                    row.put("ac_comp_cta",rs.getString("ac_comp_cta"));
                    row.put("ac_comp_subcta",rs.getString("ac_comp_subcta"));
                    row.put("ac_comp_ssubcta",rs.getString("ac_comp_ssubcta"));
                    row.put("ac_comp_sssubcta",rs.getString("ac_comp_sssubcta"));
                    row.put("ac_comp_ssssubcta",rs.getString("ac_comp_ssssubcta"));
                    row.put("ac_comp_descripcion",rs.getString("ac_comp_descripcion"));

                    return row;
                }
            }
        );
        return contab;
    }





    @Override
    public ArrayList<HashMap<String, Object>> getPaises() {
        //String sql_to_query = "SELECT DISTINCT cve_pais ,pais_ent FROM municipios;";
        String sql_to_query = "SELECT DISTINCT id as cve_pais, titulo as pais_ent FROM gral_pais;";

        ArrayList<HashMap<String, Object>> pais = (ArrayList<HashMap<String, Object>>) this.jdbcTemplate.query(
            sql_to_query,
            new Object[]{}, new RowMapper(){
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    HashMap<String, Object> row = new HashMap<String, Object>();
                    row.put("cve_pais",rs.getString("cve_pais"));
                    row.put("pais_ent",rs.getString("pais_ent"));
                    return row;
                }
            }
        );
        return pais;
    }



    @Override
    public ArrayList<HashMap<String, Object>> getEntidadesForThisPais(String id_pais) {
        //String sql_to_query = "SELECT DISTINCT cve_ent ,nom_ent FROM municipios where cve_pais='"+id_pais+"' order by nom_ent;";
        String sql_to_query = "SELECT id as cve_ent, titulo as nom_ent FROM gral_edo WHERE pais_id="+id_pais+" order by nom_ent;";
        //log.log(Level.INFO, "Ejecutando query de {0}", sql_to_query);
        ArrayList<HashMap<String, Object>> hm = (ArrayList<HashMap<String, Object>>) this.jdbcTemplate.query(
            sql_to_query,
            new Object[]{}, new RowMapper(){
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    HashMap<String, Object> row = new HashMap<String, Object>();
                    row.put("cve_ent",rs.getString("cve_ent"));
                    row.put("nom_ent",rs.getString("nom_ent"));
                    return row;
                }
            }
        );
        return hm;
    }



    @Override
    public ArrayList<HashMap<String, Object>> getLocalidadesForThisEntidad(String id_pais, String id_entidad) {
        //String sql_to_query = "SELECT DISTINCT cve_mun ,nom_mun FROM municipios where cve_ent='"+id_entidad+"' and cve_pais='"+id_pais+"' order by nom_mun;";
        String sql_to_query = "SELECT id as cve_mun, titulo as nom_mun FROM gral_mun WHERE estado_id="+id_entidad+" and pais_id="+id_pais+" order by nom_mun;";

        //System.out.println("Ejecutando query loc_for_this_entidad: "+sql_to_query);

        ArrayList<HashMap<String, Object>> hm = (ArrayList<HashMap<String, Object>>) this.jdbcTemplate.query(
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




    //obtiene  tosas la direcciones de consignacion del cliente
    @Override
    public ArrayList<HashMap<String, Object>> getCliente_DirConsignacion(Integer id_cliente) {

        String sql_query = "SELECT erp_clients_consignacions.calle, "
                                + "erp_clients_consignacions.numero, "
                                + "erp_clients_consignacions.colonia, "
                                + "gral_pais.id as id_pais, "
                                + "gral_pais.titulo as pais, "
                                + "gral_edo.id as id_entidad, "
                                + "gral_edo.titulo as entidad, "
                                + "gral_mun.id as id_localidad, "
                                + "gral_mun.titulo as localidad, "
                                + "erp_clients_consignacions.cp, "
                                + "erp_clients_consignacions.localidad_alternativa,  "
                                + "erp_clients_consignacions.telefono,  "
                                + "erp_clients_consignacions.fax  "
                        + "FROM erp_clients_consignacions "
                        + "JOIN gral_pais ON gral_pais.id =  erp_clients_consignacions.pais_id "
                        + "JOIN gral_edo ON gral_edo.id =  erp_clients_consignacions.estado_id "
                        + "JOIN gral_mun ON gral_mun.id =  erp_clients_consignacions.municipio_id "
                        + "WHERE cliente_id = ? ";

        //System.out.println("Ejecutando query_dir_consignacion: "+sql_query);
        ArrayList<HashMap<String, Object>> direcciones = (ArrayList<HashMap<String, Object>>) this.jdbcTemplate.query(
            sql_query,
            new Object[]{new Integer(id_cliente)}, new RowMapper() {
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    HashMap<String, Object> row = new HashMap<String, Object>();
                    row.put("calle",rs.getString("calle"));
                    row.put("numero",rs.getString("numero"));
                    row.put("colonia",rs.getString("colonia"));
                    row.put("id_pais",rs.getString("id_pais"));
                    row.put("pais",rs.getString("pais"));
                    row.put("id_entidad",rs.getString("id_entidad"));
                    row.put("entidad",rs.getString("entidad"));
                    row.put("id_localidad",rs.getString("id_localidad"));
                    row.put("localidad",rs.getString("localidad"));
                    row.put("cp",rs.getString("cp"));
                    row.put("localidad_alternativa",rs.getString("localidad_alternativa"));
                    row.put("telefono",rs.getString("telefono"));
                    row.put("fax",rs.getString("fax"));
                    return row;
                }
            }
        );
        return direcciones;
    }




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



    //obtiene los tipos de cliente
    @Override
    public ArrayList<HashMap<String, Object>> getCliente_Tipos() {
        String sql_to_query = "select id, titulo from cxc_clie_clases order by id asc;";
        //log.log(Level.INFO, "Ejecutando query de {0}", sql_to_query);
        ArrayList<HashMap<String, Object>> hm_tclient = (ArrayList<HashMap<String, Object>>) this.jdbcTemplate.query(
            sql_to_query,
            new Object[]{}, new RowMapper(){
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    HashMap<String, Object> row = new HashMap<String, Object>();
                    row.put("id",rs.getInt("id"));
                    row.put("titulo",rs.getString("titulo"));
                    return row;
                }
            }
        );
        return hm_tclient;
    }





    @Override
    public HashMap<String, String> getCliente_ValidaDirConsignacion(String data_string) {
        String sql_to_query = "select * from erp_fn_validaciones_dir_consignacion_cliente('"+data_string+"');";
        //System.out.println("Validacion:"+sql_to_query);

        HashMap<String, String> hm = (HashMap<String, String>) this.jdbcTemplate.queryForObject(
            sql_to_query,
            new Object[]{}, new RowMapper() {
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    HashMap<String, String> row = new HashMap<String, String>();
                    row.put("success",rs.getString("erp_fn_validaciones_dir_consignacion_cliente"));
                    return row;
                }
            }
        );
        return hm;
    }




    @Override
    public ArrayList<HashMap<String, Object>> getCliente_Vendedores(Integer id_empresa) {
        //String sql_to_query = "SELECT id,nombre_pila||' '||apellido_paterno||' '||apellido_materno AS nombre_vendedor FROM erp_empleados WHERE borrado_logico=FALSE  AND vendedor=TRUE;";
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
    public ArrayList<HashMap<String, Object>> getCliente_Condiciones() {
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
    public ArrayList<HashMap<String, Object>> getCliente_Zonas() {
        String sql_to_query = "SELECT id, titulo AS nombre_zona FROM cxc_clie_zonas WHERE borrado_logico=FALSE ORDER BY id;";
        //log.log(Level.INFO, "Ejecutando query de {0}", sql_to_query);
        ArrayList<HashMap<String, Object>> zonas = (ArrayList<HashMap<String, Object>>) this.jdbcTemplate.query(
            sql_to_query,
            new Object[]{}, new RowMapper(){
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    HashMap<String, Object> row = new HashMap<String, Object>();
                    row.put("id",rs.getInt("id"));
                    row.put("nombre_zona",rs.getString("nombre_zona"));
                    return row;
                }
            }
        );
        return zonas;
    }



    @Override
    public ArrayList<HashMap<String, Object>> getCliente_Grupos() {
        String sql_to_query = "SELECT id, titulo AS nombre_grupo FROM cxc_clie_grupos WHERE borrado_logico=FALSE ORDER BY id;";
        //log.log(Level.INFO, "Ejecutando query de {0}", sql_to_query);
        ArrayList<HashMap<String, Object>> grupos = (ArrayList<HashMap<String, Object>>) this.jdbcTemplate.query(
            sql_to_query,
            new Object[]{}, new RowMapper(){
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    HashMap<String, Object> row = new HashMap<String, Object>();
                    row.put("id",rs.getInt("id"));
                    row.put("nombre_grupo",rs.getString("nombre_grupo"));
                    return row;
                }
            }
        );
        return grupos;
    }



    @Override
    public ArrayList<HashMap<String, Object>> getCliente_Clasificacion1() {
        String sql_to_query = "SELECT id, titulo AS clasificacion1 FROM cxc_clie_clas1 ORDER BY id;";
        //log.log(Level.INFO, "Ejecutando query de {0}", sql_to_query);
        ArrayList<HashMap<String, Object>> clasif1 = (ArrayList<HashMap<String, Object>>) this.jdbcTemplate.query(
            sql_to_query,
            new Object[]{}, new RowMapper(){
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    HashMap<String, Object> row = new HashMap<String, Object>();
                    row.put("id",rs.getInt("id"));
                    row.put("clasificacion1",rs.getString("clasificacion1"));
                    return row;
                }
            }
        );
        return clasif1;
    }

    @Override
    public ArrayList<HashMap<String, Object>> getCliente_Clasificacion2() {
        String sql_to_query = "SELECT id, titulo AS clasificacion2 FROM cxc_clie_clas2 ORDER BY id;";
        //log.log(Level.INFO, "Ejecutando query de {0}", sql_to_query);
        ArrayList<HashMap<String, Object>> clasif2 = (ArrayList<HashMap<String, Object>>) this.jdbcTemplate.query(
            sql_to_query,
            new Object[]{}, new RowMapper(){
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    HashMap<String, Object> row = new HashMap<String, Object>();
                    row.put("id",rs.getInt("id"));
                    row.put("clasificacion2",rs.getString("clasificacion2"));
                    return row;
                }
            }
        );
        return clasif2;
    }

    @Override
    public ArrayList<HashMap<String, Object>> getCliente_Clasificacion3() {
        String sql_to_query = "SELECT id, titulo AS clasificacion3 FROM cxc_clie_clas3 ORDER BY id;";
        //log.log(Level.INFO, "Ejecutando query de {0}", sql_to_query);
        ArrayList<HashMap<String, Object>> clasif3 = (ArrayList<HashMap<String, Object>>) this.jdbcTemplate.query(
            sql_to_query,
            new Object[]{}, new RowMapper(){
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    HashMap<String, Object> row = new HashMap<String, Object>();
                    row.put("id",rs.getInt("id"));
                    row.put("clasificacion3",rs.getString("clasificacion3"));
                    return row;
                }
            }
        );
        return clasif3;
    }



    @Override
    public ArrayList<HashMap<String, Object>> getImpuestos() {
        String sql_to_query = "SELECT id, descripcion, iva_1 FROM gral_imptos WHERE borrado_logico=FALSE;";
        //log.log(Level.INFO, "Ejecutando query de {0}", sql_to_query);
        ArrayList<HashMap<String, Object>> hm_ivas = (ArrayList<HashMap<String, Object>>) this.jdbcTemplate.query(
            sql_to_query,
            new Object[]{}, new RowMapper(){
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    HashMap<String, Object> row = new HashMap<String, Object>();
                    row.put("id",String.valueOf(rs.getInt("id")));
                    row.put("descripcion",rs.getString("descripcion"));
                    row.put("iva_1",StringHelper.roundDouble(rs.getString("iva_1"),2));
                    return row;
                }
            }
        );
        return hm_ivas;
    }


    @Override
    public ArrayList<HashMap<String, Object>> getCliente_InicioCredito() {
        String sql_to_query = "SELECT id, titulo FROM cxc_clie_creapar ORDER BY id;";
        //log.log(Level.INFO, "Ejecutando query de {0}", sql_to_query);
        ArrayList<HashMap<String, Object>> hm = (ArrayList<HashMap<String, Object>>) this.jdbcTemplate.query(
            sql_to_query,
            new Object[]{}, new RowMapper(){
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    HashMap<String, Object> row = new HashMap<String, Object>();
                    row.put("id",String.valueOf(rs.getInt("id")));
                    row.put("titulo",rs.getString("titulo"));
                    return row;
                }
            }
        );
        return hm;
    }


    @Override
    public ArrayList<HashMap<String, Object>> getCliente_TiposEmbarque() {
        String sql_to_query = "SELECT id, titulo FROM cxc_clie_tipos_embarque ORDER BY id;";
        //log.log(Level.INFO, "Ejecutando query de {0}", sql_to_query);
        ArrayList<HashMap<String, Object>> hm = (ArrayList<HashMap<String, Object>>) this.jdbcTemplate.query(
            sql_to_query,
            new Object[]{}, new RowMapper(){
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    HashMap<String, Object> row = new HashMap<String, Object>();
                    row.put("id",String.valueOf(rs.getInt("id")));
                    row.put("titulo",rs.getString("titulo"));
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
    public ArrayList<HashMap<String, Object>> getCliente_CuentasMayor(Integer id_empresa) {
        String sql_query = "SELECT id, titulo FROM ctb_may_clases ORDER BY id;";
        ArrayList<HashMap<String, Object>> hm = (ArrayList<HashMap<String, Object>>) this.jdbcTemplate.query(
            sql_query,
            new Object[]{}, new RowMapper() {
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    HashMap<String, Object> row = new HashMap<String, Object>();
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
    public ArrayList<HashMap<String, String>> getCliente_CuentasContables(Integer cta_mayor, Integer detalle, String clasifica, String cta, String scta, String sscta, String ssscta, String sssscta, String descripcion, Integer id_empresa) {

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


        System.out.println("getCliente_CuentasContables: "+sql_query);

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









    @Override
    public ArrayList<HashMap<String, Object>> getCartera_PaginaGrid(String data_string, int offset, int pageSize, String orderBy, String asc) {

        String sql_busqueda = "select DISTINCT id from gral_bus_catalogos(?) as foo (id integer)";

	String sql_to_query = ""
                + "SELECT erp_pagos.id,  "
                    +"erp_pagos.numero_transaccion,  "
                    +"erp_pagos.monto_pago AS total,  "
                    +"cxc_clie.razon_social AS cliente,  "
                    +"erp_pagos_formas.titulo AS forma_pago,  "
                    +"gral_mon.descripcion_abr AS moneda,  "
                    +"to_char(erp_pagos.fecha_deposito::timestamp with time zone,'dd/mm/yyyy') AS fecha_deposito, "
                    +"(CASE WHEN tbl_pag_det.pago_id IS NULL THEN 'CANCELADO' ELSE '' END) AS estado "
                +"FROM erp_pagos  "
                +"JOIN cxc_clie ON cxc_clie.id=erp_pagos.cliente_id  "
                +"JOIN erp_pagos_formas ON erp_pagos_formas.id=erp_pagos.forma_pago_id  "
                +"JOIN gral_mon ON gral_mon.id=erp_pagos.moneda_id   "
                +"LEFT JOIN (SELECT DISTINCT pago_id FROM erp_pagos_detalles WHERE cancelacion=FALSE) AS tbl_pag_det ON tbl_pag_det.pago_id=erp_pagos.id "
                +"JOIN ("+sql_busqueda+") as subt on subt.id=erp_pagos.id "
                +"order by "+orderBy+" "+asc+" limit ? OFFSET ?";

        //System.out.println("Busqueda GetPage: "+sql_to_query);
        //System.out.println("data_string: "+data_string+ "\noffset:"+offset+ " pageSize: "+pageSize+" orderBy:"+orderBy+" asc:"+asc);
        //log.log(Level.INFO, "Ejecutando query de {0}", sql_to_query);
        ArrayList<HashMap<String, Object>> hm = (ArrayList<HashMap<String, Object>>) this.jdbcTemplate.query(
            sql_to_query,
            new Object[]{data_string,new Integer(pageSize),new Integer(offset)}, new RowMapper() {
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    HashMap<String, Object> row = new HashMap<String, Object>();
                    row.put("id",rs.getInt("id"));
                    row.put("numero_transaccion",rs.getString("numero_transaccion"));
                    row.put("total",StringHelper.AgregaComas(StringHelper.roundDouble(rs.getString("total"),2)));
                    row.put("cliente",rs.getString("cliente"));
                    row.put("forma_pago",rs.getString("forma_pago"));
                    row.put("fecha_deposito",rs.getString("fecha_deposito"));
                    row.put("moneda",rs.getString("moneda"));
                    row.put("estado",rs.getString("estado"));
                    return row;
                }
            }
        );
        return hm;

    }



    @Override
    public ArrayList<HashMap<String, Object>> getCartera_TipoMovimiento() {
        String sql_to_query = "SELECT id, titulo FROM erp_pagos_tipo_movimiento WHERE borrado_logico=false ORDER BY id ASC;";
        //log.log(Level.INFO, "Ejecutando query de {0}", sql_to_query);
        ArrayList<HashMap<String, Object>> hm_tm = (ArrayList<HashMap<String, Object>>) this.jdbcTemplate.query(
            sql_to_query,
            new Object[]{}, new RowMapper(){
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    HashMap<String, Object> row = new HashMap<String, Object>();
                    row.put("id",rs.getInt("id"));
                    row.put("titulo",rs.getString("titulo"));
                    return row;
                }
            }
        );
        return hm_tm;
    }



    @Override
    public ArrayList<HashMap<String, Object>> getCartera_FormasPago() {
        String sql_to_query = "SELECT id, titulo FROM  erp_pagos_formas WHERE borrado_logico=false ORDER BY id ASC;";
        ArrayList<HashMap<String, Object>> hm_forma_pago = (ArrayList<HashMap<String, Object>>) this.jdbcTemplate.query(
            sql_to_query,
            new Object[]{}, new RowMapper(){
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    HashMap<String, Object> row = new HashMap<String, Object>();
                    row.put("id",rs.getInt("id"));
                    row.put("titulo",rs.getString("titulo"));
                    return row;
                }
            }
        );
        return hm_forma_pago;
    }




    @Override
    public ArrayList<HashMap<String, Object>> getBancos(Integer id_empresa) {
        String sql_to_query = ""
                + "select "
                    + "distinct id, "
                    + "titulo "
                    + "from tes_ban "
                +"where borrado_logico=false and gral_emp_id="+id_empresa
                +" order by titulo ASC;";

        ArrayList<HashMap<String, Object>> hm_bancos = (ArrayList<HashMap<String, Object>>) this.jdbcTemplate.query(
            sql_to_query,
            new Object[]{}, new RowMapper(){
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    HashMap<String, Object> row = new HashMap<String, Object>();
                    row.put("id",rs.getInt("id"));
                    row.put("titulo",rs.getString("titulo"));
                    return row;
                }
            }
        );
        return hm_bancos;
    }


    @Override
    public ArrayList<HashMap<String, Object>> getCartera_BancosEmpresa(Integer id_empresa) {
        String sql_to_query = ""
                + "SELECT distinct id, "
                        + "titulo "
                + "FROM tes_ban "
                +"WHERE borrado_logico=false AND gral_emp_id="+id_empresa
                +" ORDER BY titulo ASC;";
        System.out.println("Obtiene bancos kemikal: "+sql_to_query);
        ArrayList<HashMap<String, Object>> hm_bancos_friser = (ArrayList<HashMap<String, Object>>) this.jdbcTemplate.query(
            sql_to_query,
            new Object[]{}, new RowMapper(){
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    HashMap<String, Object> row = new HashMap<String, Object>();
                    row.put("id",rs.getInt("id"));
                    row.put("titulo",rs.getString("titulo"));
                    return row;
                }
            }
        );
        return hm_bancos_friser;
    }


    @Override
    public ArrayList<HashMap<String, Object>> getTipoCambioActual() {
        String sql_to_query = "Select valor from erp_monedavers where moneda_id=2 order by momento_creacion DESC limit 1;";
        //System.out.println("Buscando cuentas: "+sql_to_query);
        ArrayList<HashMap<String, Object>> hm_tipoCambio = (ArrayList<HashMap<String, Object>>) this.jdbcTemplate.query(
            sql_to_query,
            new Object[]{}, new RowMapper(){
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    HashMap<String, Object> row = new HashMap<String, Object>();
                    row.put("valor_tipo_cambio",StringHelper.roundDouble(rs.getString("valor"),4));
                    return row;
                }
            }
        );
        return hm_tipoCambio;
    }




    @Override
    public ArrayList<HashMap<String, Object>> getBuscadorClientes(String cadena, Integer filtro, Integer id_empresa, Integer id_sucursal) {
        String where="";

	if(filtro == 1){
		where=" AND cxc_clie.numero_control ilike '%"+cadena+"%'";
	}
	if(filtro == 2){
		where=" AND cxc_clie.rfc ilike '%"+cadena+"%'";
	}
	if(filtro == 3){
		where=" AND cxc_clie.razon_social ilike '%"+cadena+"%'";
	}

	if(filtro == 4){
		where=" AND cxc_clie.curp ilike '%"+cadena+"%'";
	}
	if(filtro == 5){
		where=" AND cxc_clie.alias ilike '%"+cadena+"%'";
	}

        if(id_sucursal==0){
            where +="";
        }else{
            where +=" AND sucursal_id="+id_sucursal;
        }

	String sql_query = "SELECT "
                                    +"sbt.id,"
                                    +"sbt.numero_control,"
                                    +"sbt.rfc,"
                                    +"sbt.razon_social,"
                                    +"sbt.direccion,"
                                    +"sbt.moneda_id,"
                                    +"gral_mon.descripcion as moneda "
                            +"FROM(SELECT cxc_clie.id,"
                                            +"cxc_clie.numero_control,"
                                            +"cxc_clie.rfc, "
                                            +"cxc_clie.razon_social,"
                                            +"cxc_clie.calle||' '||cxc_clie.numero||', '||cxc_clie.colonia||', '||gral_mun.titulo||', '||gral_edo.titulo||', '||gral_pais.titulo||' C.P. '||cxc_clie.cp as direccion, "
                                            +"cxc_clie.moneda as moneda_id "
                                    +"FROM cxc_clie "
                                    + "JOIN gral_pais ON gral_pais.id = cxc_clie.pais_id "
                                    + "JOIN gral_edo ON gral_edo.id = cxc_clie.estado_id "
                                    + "JOIN gral_mun ON gral_mun.id = cxc_clie.municipio_id "
                                    +" WHERE empresa_id ="+id_empresa+"  "
                                    +" AND cxc_clie.borrado_logico=false  "+where+" "
                            +") AS sbt "
                            +"LEFT JOIN gral_mon on gral_mon.id = sbt.moneda_id ORDER BY sbt.id;";
        System.out.println("BuscarCliente: "+sql_query);
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

                    return row;
                }
            }
        );
        return hm_cli;
    }


    //obtener datos del cliente a partir del Numero de Control
    @Override
    public ArrayList<HashMap<String, Object>> getDatosClienteByNoCliente(String no_control, Integer id_empresa, Integer id_sucursal) {

        String where="";
        if(id_sucursal==0){
            where +="";
        }else{
            where +=" AND sucursal_id="+id_sucursal;
        }

	String sql_query = ""
                + "SELECT "
                        +"sbt.id,"
                        +"sbt.numero_control,"
                        +"sbt.rfc,"
                        +"sbt.razon_social,"
                        +"sbt.direccion,"
                        +"sbt.moneda_id,"
                        +"gral_mon.descripcion as moneda "
                +"FROM(SELECT cxc_clie.id,"
                                +"cxc_clie.numero_control,"
                                +"cxc_clie.rfc, "
                                +"cxc_clie.razon_social,"
                                +"cxc_clie.calle||' '||cxc_clie.numero||', '||cxc_clie.colonia||', '||gral_mun.titulo||', '||gral_edo.titulo||', '||gral_pais.titulo||' C.P. '||cxc_clie.cp as direccion, "
                                +"cxc_clie.moneda as moneda_id "
                        +"FROM cxc_clie "
                        + "JOIN gral_pais ON gral_pais.id = cxc_clie.pais_id "
                        + "JOIN gral_edo ON gral_edo.id = cxc_clie.estado_id "
                        + "JOIN gral_mun ON gral_mun.id = cxc_clie.municipio_id "
                        +" WHERE empresa_id ="+id_empresa+"  "
                        +" AND cxc_clie.borrado_logico=false  "+where+" "
                        + "AND  cxc_clie.numero_control='"+no_control.toUpperCase()+"'"
                +") AS sbt "
                +"LEFT JOIN gral_mon on gral_mon.id = sbt.moneda_id ORDER BY sbt.id LIMIT 1;";

        System.out.println("getDatosCliente: "+sql_query);

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

                    return row;
                }
            }
        );
        return hm_cli;
    }




    @Override
    public ArrayList<HashMap<String, Object>> getCartera_CtaBanco(Integer id_moneda, Integer id_banco) {
        //String sql_to_query = "SELECT id ,titulo FROM erp_cuentas where banco_id="+id_banco;
        //String sql_to_query = "Select distinct id,titulo from erp_cuentas where banco_id="+id_banco+" AND moneda_id="+id_moneda;
        String sql_to_query = "SELECT distinct id,titulo FROM tes_che WHERE borrado_logico=FALSE AND  tes_ban_id="+id_banco+" AND moneda_id="+id_moneda;
        System.out.println("Buscando Chequeras: "+sql_to_query);
        ArrayList<HashMap<String, Object>> hm_cuentas = (ArrayList<HashMap<String, Object>>) this.jdbcTemplate.query(
            sql_to_query,
            new Object[]{}, new RowMapper(){
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    HashMap<String, Object> row = new HashMap<String, Object>();
                    row.put("id",rs.getInt("id"));
                    row.put("titulo",rs.getString("titulo"));
                    return row;
                }
            }
        );
        return hm_cuentas;
    }



    @Override
    public ArrayList<HashMap<String, Object>> getCartera_BancosXMoneda(Integer id_moneda,Integer id_empresa) {
        String sql_to_query = "SELECT distinct id, titulo FROM tes_ban "
                +"WHERE borrado_logico=false AND gral_emp_id="+id_empresa
                +" ORDER BY titulo ASC;";
        //System.out.println("Buscando cuentas: "+sql_to_query);
        ArrayList<HashMap<String, Object>> hm_cm = (ArrayList<HashMap<String, Object>>) this.jdbcTemplate.query(
            sql_to_query,
            new Object[]{}, new RowMapper(){
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    HashMap<String, Object> row = new HashMap<String, Object>();
                    row.put("id",rs.getInt("id"));
                    row.put("titulo",rs.getString("titulo"));
                    return row;
                }
            }
        );
        return hm_cm;
    }





    //obtiene la suma de los anticipos en pesos
    @Override
    public ArrayList<HashMap<String, Object>> getCartera_SumaAnticiposMN(Integer id_cliente) {
        String sql_to_query = "SELECT (CASE WHEN suma IS NULL THEN 0 ELSE suma END) AS suma_mn "
                        + "FROM (SELECT sum(anticipo_actual) as suma "
                        + "FROM cxc_ant "
                        + "WHERE cliente_id="+id_cliente+" AND moneda_id=1 AND borrado_logico=false) AS stbla;";
        
        //System.out.println("Buscando cuentas: "+sql_to_query);
        ArrayList<HashMap<String, Object>> hm_mn = (ArrayList<HashMap<String, Object>>) this.jdbcTemplate.query(
            sql_to_query,
            new Object[]{}, new RowMapper(){
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    HashMap<String, Object> row = new HashMap<String, Object>();
                    row.put("suma_mn",rs.getDouble("suma_mn"));
                    return row;
                }
            }
        );
        return hm_mn;
    }
    
    
    //obtiene la suma de los anticipos en dolares
    @Override
    public ArrayList<HashMap<String, Object>> getCartera_SumaAnticiposUSD(Integer id_cliente) {
        String sql_to_query = "SELECT (CASE WHEN suma IS NULL THEN 0 ELSE suma END) AS suma_usd "
                + "FROM (SELECT sum(anticipo_actual) as suma "
                + "FROM cxc_ant "
                + "WHERE cliente_id="+id_cliente+" AND moneda_id=2 AND borrado_logico=false) AS stbla;";
        //System.out.println("Buscando cuentas: "+sql_to_query);
        ArrayList<HashMap<String, Object>> hm_usd = (ArrayList<HashMap<String, Object>>) this.jdbcTemplate.query(
            sql_to_query,
            new Object[]{}, new RowMapper(){
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    HashMap<String, Object> row = new HashMap<String, Object>();
                    row.put("suma_usd",rs.getDouble("suma_usd"));
                    return row;
                }
            }
        );
        return hm_usd;
    }
    
    
    
    
    @Override
    public ArrayList<HashMap<String, Object>> getCartera_Anticipos(Integer id_cliente) {
        String sql_to_query = "SELECT (CASE WHEN moneda_id=1 THEN 'M.N.' ELSE 'USD' END ) AS denominacion, "
                                    + "id, "
                                    + "numero_transaccion, "
                                    + "anticipo_actual "
                            + "FROM cxc_ant "
                            + "WHERE cliente_id="+id_cliente+" "
                            + "AND borrado_logico=FALSE "
                            + "AND cancelado=FALSE "
                            + "ORDER BY moneda_id;";
        //System.out.println("Buscando cuentas: "+sql_to_query);
        ArrayList<HashMap<String, Object>> hm_anticipos = (ArrayList<HashMap<String, Object>>) this.jdbcTemplate.query(
            sql_to_query,
            new Object[]{}, new RowMapper(){
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    HashMap<String, Object> row = new HashMap<String, Object>();
                    row.put("id",rs.getInt("id"));
                    row.put("denominacion",rs.getString("denominacion"));
                    row.put("numero_transaccion",rs.getString("numero_transaccion"));
                    row.put("anticipo_actual",rs.getDouble("anticipo_actual"));
                    return row;
                }
            }
        );
        return hm_anticipos;
    }



    @Override
    public ArrayList<HashMap<String, Object>> getCartera_Facturas(Integer id_cliente) {
        //ArrayList<HashMap<String, Object>>  segmento_trabajando = new ArrayList<HashMap<String, Object>>();

        String sql_to_query = ""
                + "SELECT "
                        + "erp_h_facturas.serie_folio as numero_factura, "
                        + "gral_mon.descripcion_abr AS denominacion_factura, "
                        + "erp_h_facturas.monto_total as monto_factura, "
                        + "(erp_h_facturas.total_pagos + erp_h_facturas.total_notas_creditos) AS monto_pagado, "
                        + "erp_h_facturas.saldo_factura, "
                        + "to_char(erp_h_facturas.momento_facturacion,'dd/mm/yyyy') AS fecha_facturacion, "
                        + "(CASE WHEN erp_h_facturas.fecha_ultimo_pago IS NULL THEN 'Sin efectuar' ELSE to_char(erp_h_facturas.fecha_ultimo_pago::timestamp with time zone,'dd/mm/yyyy') END) AS fecha_ultimo_pago "
                    + "FROM erp_h_facturas  "
                    + "JOIN gral_mon ON gral_mon.id = erp_h_facturas.moneda_id "
                    + "WHERE erp_h_facturas.cliente_id="+id_cliente
                    + "AND erp_h_facturas.cancelacion=FALSE "
                    + "AND erp_h_facturas.pagado=FALSE "
                    + "ORDER BY erp_h_facturas.id;";

        System.out.println("getCartera_Facturas:: "+sql_to_query);
        ArrayList<HashMap<String, Object>> facturas = (ArrayList<HashMap<String, Object>>) this.jdbcTemplate.query(
            sql_to_query,
            new Object[]{}, new RowMapper(){
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    HashMap<String, Object> row = new HashMap<String, Object>();
                    row.put("numero_factura",rs.getString("numero_factura"));
                    row.put("denominacion_factura",rs.getString("denominacion_factura"));
                    row.put("monto_factura",StringHelper.roundDouble(rs.getDouble("monto_factura"),2));
                    row.put("monto_pagado",StringHelper.roundDouble(rs.getDouble("monto_pagado"),2));
                    row.put("fecha_facturacion",rs.getString("fecha_facturacion"));
                    row.put("fecha_ultimo_pago",rs.getString("fecha_ultimo_pago"));

                    return row;

                }
            }
        );

        return facturas;
    }



    //obtiene las facturas para el buscador de facturas a cancelar
    @Override
    public ArrayList<HashMap<String, String>> getCartera_FacturasCancelar(String num_transaccion,String factura, Integer id_cliente) {
        String cadWhere="";
        if(num_transaccion.equals("")==false) {
            cadWhere += " AND erp_pagos.numero_transaccion = "+num_transaccion;
        }

        String sql_to_query = "SELECT DISTINCT erp_pagos.id, "
                                    + "erp_pagos.numero_transaccion, "
                                    + "erp_pagos.observaciones "
                            + "FROM erp_pagos  "
                            + "JOIN erp_pagos_detalles ON erp_pagos_detalles.pago_id = erp_pagos.id  "
                            + "WHERE erp_pagos.cliente_id="+id_cliente + " " + cadWhere
                            + " AND erp_pagos_detalles.serie_folio ILIKE '%"+factura+"%'  "
                            + " AND erp_pagos_detalles.cancelacion = FALSE; ";

        System.out.println("Obteniendo datos para el buscador de facturas a cancelar: "+sql_to_query);
        ArrayList<HashMap<String, Object>> hm_fact_cancel = (ArrayList<HashMap<String, Object>>) this.jdbcTemplate.query(
            sql_to_query,
            new Object[]{}, new RowMapper(){
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    HashMap<String, Object> row = new HashMap<String, Object>();
                    row.put("id",rs.getString("id"));
                    row.put("numero_transaccion",rs.getString("numero_transaccion"));
                    row.put("observaciones",rs.getString("observaciones"));
                    return row;
                }
            }
        );


        ArrayList<HashMap<String, String>> hm_retorno = new ArrayList<HashMap<String, String>>();

        for (int x=0; x<=hm_fact_cancel.size()-1;x++){
            HashMap<String,Object> registro = hm_fact_cancel.get(x);
            HashMap<String,String> facturas = ObtieneFacturasTransaccion(Integer.parseInt(registro.get("id").toString()));
            HashMap<String, String> reg = new HashMap<String, String>();

            reg.put("numero_transaccion",registro.get("numero_transaccion").toString());
            reg.put("observaciones",registro.get("observaciones").toString());
            reg.put("serie_folio",facturas.get("facturas").toString());
            reg.put("momento_creacion",facturas.get("momento_pago").toString());

            hm_retorno.add(reg);
        }

        return hm_retorno;
    }

    //obtiene las facturas de una transaccion en especifico
    private HashMap<String, String> ObtieneFacturasTransaccion(Integer id_pago) {
        String sql_to_query = ""
                + "SELECT "
                    + "serie_folio,"
                    + "to_char(momento_pago,'yyyy-mm-dd HH24:MI:SS') as momento_pago "
                + "FROM erp_pagos_detalles "
                + "WHERE cancelacion=false "
                +" AND pago_id="+id_pago+";";
        //System.out.println("Obtiene facturas de la transaccion:"+sql_to_query);
        String facturas="";
        String momento_pago="";
        HashMap<String, String> retorno = new HashMap<String, String>();
        //System.out.println("Buscando cuentas: "+sql_to_query);
        ArrayList<HashMap<String, Object>> hm_trans = (ArrayList<HashMap<String, Object>>) this.jdbcTemplate.query(
            sql_to_query,
            new Object[]{}, new RowMapper(){
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    HashMap<String, Object> row = new HashMap<String, Object>();
                    row.put("serie_folio",rs.getString("serie_folio"));
                    row.put("momento_pago",rs.getString("momento_pago"));
                    return row;
                }
            }
        );

        for (int x=0; x<=hm_trans.size()-1;x++){
            HashMap<String,Object> registro = hm_trans.get(x);
            facturas = facturas + registro.get("serie_folio").toString()+",";
            momento_pago = registro.get("momento_pago").toString();
        }

        retorno.put("facturas",facturas.substring(0,facturas.length()-1));
        retorno.put("momento_pago",momento_pago);
        return retorno;
    }




    //obtiene facturas de un numero de transaccion en especifico
    //para el grid de cancelacion
    @Override
    public ArrayList<HashMap<String, Object>> getCartera_FacturasTransaccion(String num_transaccion) {
        String sql_to_query = "SELECT erp_pagos_detalles.serie_folio, "
                                    +"erp_h_facturas.monto_total, "
                                    +"erp_pagos_detalles.cantidad, "
                                    +"gral_mon.descripcion_abr AS denominacion, "
                                    +"erp_h_facturas.momento_facturacion, "
                                    +"erp_pagos_detalles.momento_pago, "
                                    +"erp_pagos.numero_transaccion "
                            +"FROM erp_pagos "
                            +"JOIN erp_pagos_detalles ON erp_pagos_detalles.pago_id=erp_pagos.id "
                            +"JOIN erp_h_facturas ON erp_h_facturas.serie_folio = erp_pagos_detalles.serie_folio "
                            +"JOIN gral_mon ON gral_mon.id = erp_h_facturas.moneda_id "
                            +"WHERE erp_pagos_detalles.cancelacion=FALSE "
                            + "AND erp_pagos.numero_transaccion = "+num_transaccion+";";

        System.out.println("getCartera_FacturasTransaccion:: "+sql_to_query);
        ArrayList<HashMap<String, Object>> hm_fact = (ArrayList<HashMap<String, Object>>) this.jdbcTemplate.query(
            sql_to_query,
            new Object[]{}, new RowMapper(){
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    HashMap<String, Object> row = new HashMap<String, Object>();
                    row.put("serie_folio",rs.getString("serie_folio"));
                    row.put("monto_total",StringHelper.roundDouble(rs.getString("monto_total"), 2));
                    row.put("cantidad",rs.getString("cantidad"));
                    row.put("denominacion",rs.getString("denominacion"));
                    row.put("momento_facturacion",rs.getString("momento_facturacion"));
                    row.put("momento_pago",rs.getString("momento_pago"));
                    row.put("numero_transaccion",rs.getString("numero_transaccion"));
                    return row;
                }
            }
        );
        return hm_fact;
    }



    //obtiene los 50 ultimos numeros de transaccion de un cliente
    //para el grid de cancelacion
    @Override
    public ArrayList<HashMap<String, String>> getCartera_NumerosDeTransaccionCliente(Integer id_cliente) {
        String sql_to_query = ""
                + "SELECT DISTINCT "
                    + "erp_pagos.id,"
                    + "erp_pagos.numero_transaccion "
                + "FROM erp_pagos "
                + "JOIN erp_pagos_detalles ON erp_pagos_detalles.pago_id=erp_pagos.id  "
                + "WHERE erp_pagos.cliente_id="+id_cliente+" AND erp_pagos_detalles.cancelacion=FALSE "
                + "ORDER BY erp_pagos.id LIMIT 50;";

        System.out.println("Buscando numeros de transaccion del cliente: "+sql_to_query);
        ArrayList<HashMap<String, Object>> hm = (ArrayList<HashMap<String, Object>>) this.jdbcTemplate.query(
            sql_to_query,
            new Object[]{}, new RowMapper(){
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    HashMap<String, Object> row = new HashMap<String, Object>();
                    row.put("numero_transaccion",rs.getString("numero_transaccion"));
                    row.put("id",rs.getInt("id"));
                    return row;
                }
            }
        );

        ArrayList<HashMap<String, String>> hm_retorno = new ArrayList<HashMap<String, String>>();

        for (int x=0; x<=hm.size()-1;x++){
            HashMap<String,Object> registro = hm.get(x);
            HashMap<String,String> facturas = ObtieneFacturasTransaccion(Integer.parseInt(registro.get("id").toString()));
            HashMap<String, String> reg = new HashMap<String, String>();

            reg.put("numero_transaccion", registro.get("numero_transaccion").toString());
            reg.put("facturas", facturas.get("facturas").toString());
            reg.put("momento_pago", facturas.get("momento_pago").toString());
            hm_retorno.add(reg);
        }
        return hm_retorno;
    }



    @Override
    public ArrayList<HashMap<String, String>> getCartera_DatosReporteEdoCta(Integer tipo_reporte,Integer id_cliente, Integer id_moneda, String fecha_corte,Integer id_empresa,Integer id_agente) {

        String cadena_where="";
        if(tipo_reporte==1){
            cadena_where=" AND erp_h_facturas.cliente_id = "+id_cliente;
        }
        if(tipo_reporte==2){
            cadena_where=" AND erp_h_facturas.cxc_agen_id = "+id_agente;
        }

        String sql_to_query = ""
        + "SELECT cliente, "
                + "denominacion, "
                + "serie_folio,  "
                + "orden_compra, "
                + "fecha_facturacion,  "
                + "monto_total,  "
                + "(CASE WHEN importe_pagado IS NULL THEN 0 ELSE importe_pagado END ) AS importe_pagado,  "
                + "(CASE WHEN ultimo_pago IS NULL THEN '/  /' ELSE ultimo_pago END) AS ultimo_pago,  "
                + "round((monto_total-(CASE WHEN importe_pagado IS NULL THEN 0 ELSE importe_pagado END ))::numeric,2) as saldo_factura  "
                + "FROM (  "
                        + "SELECT cxc_clie.razon_social as cliente,  "
                                + "gral_mon.descripcion_abr as denominacion,  "
                                + "erp_h_facturas.serie_folio,  "
                                + "erp_h_facturas.orden_compra, "
                                + "to_char(erp_h_facturas.momento_facturacion,'dd/mm/yyyy')as fecha_facturacion,  "
                                + "erp_h_facturas.monto_total,  "
                                + "(erp_h_facturas.total_pagos + erp_h_facturas.total_notas_creditos) AS importe_pagado, "
                                + "to_char(erp_h_facturas.fecha_ultimo_pago,'dd/mm/yyyy') as ultimo_pago,  "
                                + "erp_h_facturas.moneda_id "
                        + "FROM erp_h_facturas  "
                        + "JOIN cxc_clie ON cxc_clie.id = erp_h_facturas.cliente_id "
                        + "JOIN gral_mon ON gral_mon.id = erp_h_facturas.moneda_id "
                        + "WHERE erp_h_facturas.pagado=FALSE  "
                        + "AND erp_h_facturas.cancelacion=FALSE   "+ cadena_where +" "
                        + "AND erp_h_facturas.empresa_id="+ id_empresa + " "
                        + "AND erp_h_facturas.moneda_id ="+ id_moneda
        + ") as sbt  "
        + "ORDER BY sbt.moneda_id, sbt.cliente,sbt.serie_folio;";




        System.out.println("getCartera_DatosReporteEdoCta  : "+sql_to_query);
        ArrayList<HashMap<String, String>> hm_facturas = (ArrayList<HashMap<String, String>>) this.jdbcTemplate.query(
            sql_to_query,
            new Object[]{}, new RowMapper(){
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    HashMap<String, String> row = new HashMap<String, String>();
                    row.put("cliente",rs.getString("cliente"));
                    row.put("denominacion",rs.getString("denominacion"));
                    row.put("serie_folio",rs.getString("serie_folio"));
                    row.put("orden_compra",rs.getString("orden_compra"));
                    row.put("fecha_facturacion",rs.getString("fecha_facturacion"));
                    row.put("monto_total",StringHelper.roundDouble(rs.getDouble("monto_total"), 2));
                    row.put("importe_pagado",StringHelper.roundDouble(rs.getDouble("importe_pagado"), 2));
                    row.put("ultimo_pago",rs.getString("ultimo_pago"));
                    row.put("saldo_factura",StringHelper.roundDouble(rs.getDouble("saldo_factura"), 2));
                    return row;
                }
            }
        );
        return hm_facturas;
    }







    @Override
    public ArrayList<HashMap<String, String>> getCartera_DatosReporteDepositos(String fecha_inicial, String fecha_final, Integer id_empresa) {
        String sql_to_query = ""
            + "SELECT "
                    + "banco, "
                    + "no_cuenta, "
                    + "moneda_cuenta, "
                    + "fecha_deposito, "
                    + "sum(efectivo) as efectivo, "
                    + "sum(cheque) as cheque, "
                    + "sum(transferencia) as transferencia, "
                    + "sum(tarjeta) as tarjeta, "
                    + "sum(otro) as otro, "
                    + "sum(efectivo + cheque + transferencia + tarjeta + otro) as total "
            + "FROM ("
                    + "SELECT banco, "
                            + "no_cuenta, moneda_cuenta, "
                            + "to_char(fecha_deposito,'dd/mm/yyyy') as fecha_deposito, "
                            + "to_char(fecha_deposito,'yyyymmdd') as fecha_ordenamiento, "
                            + "(CASE WHEN forma_pago='Efectivo' THEN monto_deposito ELSE 0 END) as efectivo, "
                            + "(CASE WHEN forma_pago='Cheque' THEN monto_deposito ELSE 0 END) as cheque, "
                            + "(CASE WHEN forma_pago='Transferencia' THEN monto_deposito ELSE 0 END) as transferencia, "
                            + "(CASE WHEN forma_pago='Tarjeta' THEN monto_deposito ELSE 0 END) as tarjeta, "
                            + "(CASE WHEN forma_pago='otro' THEN monto_deposito ELSE 0 END) as otro "
                    + "FROM ("
                            + "SELECT 	erp_pagos.id , "
                                + "erp_pagos.banco_id, "
                                + "erp_pagos.bancokemikal_id, "
                                + "tes_ban.titulo as banco, "
                                + "erp_pagos.numerocuenta_id, "
                                + "tes_che.titulo as no_cuenta, "
                                + "tes_che.moneda_id as moneda_id_cuenta, "
                                + "gral_mon.descripcion_abr as moneda_cuenta, "
                                + "erp_pagos.fecha_deposito, "
                                + "erp_pagos.forma_pago_id, "
                                + "erp_pagos_formas.titulo as forma_pago, "
                                + "erp_pagos.moneda_id AS moneda_id_pago, "
                                + "erp_pagos.tipo_cambio, "
                                + "erp_pagos.monto_pago, "
                                + "erp_pagos.empresa_id, "
                                + "sum( "
                                    + "(CASE WHEN erp_pagos_detalles.fac_moneda_id=2 THEN "
                                        + "(CASE WHEN tes_che.moneda_id=1 THEN erp_pagos_detalles.cantidad*erp_pagos.tipo_cambio ELSE erp_pagos_detalles.cantidad END) "
                                    + "ELSE  "
                                        + "(CASE WHEN tes_che.moneda_id=2 THEN erp_pagos_detalles.cantidad/erp_pagos.tipo_cambio ELSE erp_pagos_detalles.cantidad END) "
                                    + "END) "
                                + ") as monto_deposito "
                            + "FROM erp_pagos "
                            + "JOIN erp_pagos_detalles ON erp_pagos_detalles.pago_id=erp_pagos.id "
                            + "JOIN tes_ban ON tes_ban.id=erp_pagos.bancokemikal_id::integer "
                            + "JOIN tes_che ON tes_che.id=erp_pagos.numerocuenta_id "
                            + "JOIN gral_mon ON  gral_mon.id=tes_che.moneda_id "
                            + "JOIN erp_pagos_formas ON  erp_pagos_formas.id=erp_pagos.forma_pago_id "
                            + "WHERE erp_pagos_detalles.cancelacion=false "
                            + "GROUP BY erp_pagos.id,banco_id, bancokemikal_id, banco, numerocuenta_id, no_cuenta, moneda_id_cuenta, moneda_cuenta, fecha_deposito, forma_pago_id, forma_pago, moneda_id_pago, tipo_cambio, monto_pago, empresa_id "
                            + "ORDER BY erp_pagos.id "
                    + ") AS sbt "
                    + "WHERE sbt.empresa_id="+id_empresa+" AND (to_char(sbt.fecha_deposito,'yyyymmdd') BETWEEN to_char('"+fecha_inicial+"'::timestamp with time zone,'yyyymmdd') AND to_char('"+fecha_final+"'::timestamp with time zone,'yyyymmdd')) "
                + ") as sbt2 "
                + "group by banco,moneda_cuenta,no_cuenta,fecha_deposito,fecha_ordenamiento "
                + "order by banco,no_cuenta,fecha_ordenamiento;";

        System.out.println("Buscando depositos: "+sql_to_query);
        ArrayList<HashMap<String, String>> hm_depositos = (ArrayList<HashMap<String, String>>) this.jdbcTemplate.query(
            sql_to_query,
            new Object[]{}, new RowMapper(){
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    HashMap<String, String> row = new HashMap<String, String>();
                    row.put("banco",rs.getString("banco"));
                    row.put("no_cuenta",rs.getString("no_cuenta"));
                    row.put("moneda_cuenta",rs.getString("moneda_cuenta"));
                    row.put("fecha_deposito",rs.getString("fecha_deposito"));
                    row.put("efectivo",StringHelper.roundDouble(rs.getDouble("efectivo"), 2));
                    row.put("cheque",StringHelper.roundDouble(rs.getDouble("cheque"), 2));
                    row.put("transferencia",StringHelper.roundDouble(rs.getDouble("transferencia"), 2));
                    row.put("tarjeta",StringHelper.roundDouble(rs.getDouble("tarjeta"), 2));
                    row.put("otro",StringHelper.roundDouble(rs.getDouble("otro"), 2));
                    row.put("total",StringHelper.roundDouble(rs.getDouble("total"), 2));
                    return row;
                }
            }
        );
        return hm_depositos;
    }





    @Override
    public ArrayList<HashMap<String, String>> getCartera_PagosDatosHeader(Integer id_pago, Integer id_empresa) {
            String sql_to_query = ""
                    + "SELECT cxc_clie.razon_social AS cliente, "
                        + "erp_pagos.numero_transaccion, "
                        + "to_char(erp_pagos.fecha_deposito,'dd/mm/yyyy') AS fecha_deposito,  "
                        + "erp_pagos.moneda_id as moneda_pago_id,  "
                        + "gral_mon.descripcion AS moneda_pago, "
                        + "gral_mon.simbolo AS simbolo_moneda_pago,  "
                        + "erp_pagos.monto_pago,  "
                        + "erp_pagos.tipo_cambio,  "
                        + "tes_ban.titulo AS banco,  "
                        + "tes_che.titulo AS no_cuenta_deposito, "
                        + "erp_pagos_formas.titulo AS forma_pago,  "
                        + "(CASE WHEN erp_pagos.forma_pago_id=2 THEN erp_pagos.numero_cheque ELSE erp_pagos.referencia END ) AS cheque_referencia "
                    + "FROM erp_pagos "
                    + "LEFT JOIN tes_ban ON tes_ban.id=erp_pagos.bancokemikal_id::integer  "
                    + "LEFT JOIN gral_mon ON gral_mon.id=erp_pagos.moneda_id  "
                    + "LEFT JOIN erp_pagos_formas ON erp_pagos_formas.id=erp_pagos.forma_pago_id  "
                    + "LEFT JOIN cxc_clie ON cxc_clie.id=erp_pagos.cliente_id  "
                    + "LEFT JOIN tes_che ON tes_che.id=erp_pagos.numerocuenta_id "
                    + "WHERE erp_pagos.id="+ id_pago;

        //System.out.println("Buscando datos para reporte aplicacion de pago: "+sql_to_query);
        ArrayList<HashMap<String, String>> hm = (ArrayList<HashMap<String, String>>) this.jdbcTemplate.query(
            sql_to_query,
            new Object[]{}, new RowMapper(){
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    HashMap<String, String> row = new HashMap<String, String>();
                    row.put("cliente",rs.getString("cliente"));
                    row.put("numero_transaccion",rs.getString("numero_transaccion"));
                    row.put("fecha_deposito",rs.getString("fecha_deposito"));
                    row.put("moneda_pago_id",rs.getString("moneda_pago_id"));
                    row.put("moneda_pago",rs.getString("moneda_pago"));
                    row.put("simbolo_moneda_pago",rs.getString("simbolo_moneda_pago"));
                    row.put("monto_pago",StringHelper.roundDouble(rs.getString("monto_pago"),2));
                    row.put("tipo_cambio",StringHelper.roundDouble(rs.getString("tipo_cambio"),4));
                    row.put("banco_deposito",rs.getString("banco"));
                    row.put("no_cuenta_deposito",rs.getString("no_cuenta_deposito"));
                    row.put("forma_pago",rs.getString("forma_pago"));
                    row.put("cheque_referencia",rs.getString("cheque_referencia"));
                    return row;
                }
            }
        );
        return hm;
    }





    @Override
    public ArrayList<HashMap<String, String>> getCartera_PagosAplicados(Integer id_pago, Integer id_empresa) {
            String sql_to_query = ""
                + "SELECT "
                    + "erp_pagos.moneda_id as moneda_fac_id,  "
                    + "gral_mon.descripcion AS moneda_fac,  "
                    + "gral_mon.simbolo AS simbolo_moneda_fac,  "
                    + "erp_pagos_detalles.serie_folio,  "
                    + "to_char(erp_h_facturas.momento_facturacion,'dd/mm/yyyy') AS fecha_factura,  "
                    + "erp_pagos_detalles.cantidad AS monto_aplicado,  "
                    + "(CASE WHEN (erp_pagos.moneda_id=1) THEN   "
                        + "(CASE WHEN (erp_h_facturas.moneda_id=2) THEN erp_pagos_detalles.cantidad * erp_pagos.tipo_cambio  ELSE erp_pagos_detalles.cantidad  END)   "
                    + "ELSE  "
                        + "(CASE WHEN (erp_h_facturas.moneda_id=1) THEN  erp_pagos_detalles.cantidad / erp_pagos.tipo_cambio ELSE erp_pagos_detalles.cantidad END)   "
                    + "END) AS monto_aplicado_mn, "
                    + "(CASE WHEN erp_pagos_detalles.cancelacion=TRUE THEN 'CANCELADO' ELSE '' END) AS estado  "
                + "FROM erp_pagos  "
                + "LEFT JOIN erp_pagos_detalles ON erp_pagos_detalles.pago_id=erp_pagos.id  "
                + "LEFT JOIN erp_h_facturas ON erp_h_facturas.serie_folio=erp_pagos_detalles.serie_folio  "
                + "LEFT JOIN gral_mon ON gral_mon.id=erp_h_facturas.moneda_id  "
                + "WHERE erp_pagos.id="+ id_pago;

        //System.out.println("Buscando datos para reporte aplicacion de pago: "+sql_to_query);
        ArrayList<HashMap<String, String>> hm = (ArrayList<HashMap<String, String>>) this.jdbcTemplate.query(
            sql_to_query,
            new Object[]{}, new RowMapper(){
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    HashMap<String, String> row = new HashMap<String, String>();
                    row.put("moneda_fac_id",rs.getString("moneda_fac_id"));
                    row.put("moneda_fac",rs.getString("moneda_fac"));
                    row.put("simbolo_moneda_fac",rs.getString("simbolo_moneda_fac"));
                    row.put("serie_folio",rs.getString("serie_folio"));
                    row.put("fecha_factura",rs.getString("fecha_factura"));
                    row.put("monto_aplicado",StringHelper.roundDouble(rs.getDouble("monto_aplicado"), 2));
                    row.put("monto_aplicado_mn",StringHelper.roundDouble(rs.getDouble("monto_aplicado_mn"), 2));
                    row.put("estado",rs.getString("estado"));
                    return row;
                }
            }
        );
        return hm;
    }




    //obtiene todos los agentes de la empresa
    @Override
    public ArrayList<HashMap<String, String>> getAgentes(Integer id_empresa) {
        String sql_to_query = "SELECT cxc_agen.id,  "
                                        +"cxc_agen.nombre AS nombre_agente "
                                +"FROM cxc_agen "
                                +"JOIN gral_usr_suc ON gral_usr_suc.gral_usr_id=cxc_agen.gral_usr_id "
                                +"JOIN gral_suc ON gral_suc.id=gral_usr_suc.gral_suc_id "
                                +"WHERE gral_suc.empresa_id="+id_empresa+" ORDER BY cxc_agen.id;";

        //log.log(Level.INFO, "Ejecutando query de {0}", sql_to_query);
        ArrayList<HashMap<String, String>> hm_vendedor = (ArrayList<HashMap<String, String>>) this.jdbcTemplate.query(
            sql_to_query,
            new Object[]{}, new RowMapper(){
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
    public ArrayList<HashMap<String, String>> getCartera_DatosReporteCobranzaAgente(Integer id_agente,String fecha_inicial, String fecha_final,Double monto_inicial,Double monto_final,Integer tipo_comision, Integer id_empresa) {
        String where="";
        String and_montos ="";
        String coma="";
        String coma1="";
        String monto="";
        String cadena_case ="";
        if(id_agente!=0){
            where=" AND erp_h_facturas.cxc_agen_id="+id_agente;
            coma1=",";
        }
        //if(monto_inicial >= 0 && monto_final !=0){
        if (tipo_comision==2){
            and_montos="and erp_h_facturas.subtotal >="+monto_inicial+" and erp_h_facturas.subtotal<="+monto_final+"";
            coma=",";
            monto="sbt.monto_tope_comision, "
                +"sbt.monto_tope_comision2, "
                +"sbt.monto_tope_comision3,  "
                +" sbt.comision1_monto, "
                +" sbt.comision2_monto, "
                +" sbt.comision3_monto, "
                +" (case when sbt.comision1_monto > 0 or sbt.comision1_monto != null   "
                +" and  sbt.comision2_monto > 0 or sbt.comision2_monto != null    "
                +" and sbt.comision3_monto > 0 or sbt.comision3_monto != null THEN sbt.comision3_monto  "
                +" ELSE 0.0 end )comision_por_monto  ";
            cadena_case="(case when "+monto_inicial+" >=erp_h_facturas.subtotal or  erp_h_facturas.subtotal<= "+monto_final+" then erp_h_facturas.subtotal * (cxc_agen.monto_tope_comision / 100::double precision) else 0.0 end )as comision1_monto, "
                         +"(case when "+monto_inicial+" >=erp_h_facturas.subtotal or  erp_h_facturas.subtotal<= "+monto_final+" then erp_h_facturas.subtotal * (cxc_agen.monto_tope_comision2 / 100::double precision) else 0.0 end )as comision2_monto, "
                         +"(case when "+monto_inicial+" >=erp_h_facturas.subtotal or  erp_h_facturas.subtotal<= "+monto_final+" then erp_h_facturas.subtotal * (cxc_agen.monto_tope_comision3 / 100::double precision) else 0.0 end )as comision3_monto ";

        }else{
        monto=" 0.0:: double precision  as comision_por_monto  ";
        }

        String sql_to_query = " "
             + "SELECT sbt.numero_agente, "
                 +"sbt.nombre_agente, "
                 +"sbt.serie_folio, "
                 +"sbt.fecha_factura, "
                 +"sbt.cliente, "
                 +"fecha_pago, "
                 +"sbt.numero_dias_pago, "
                 +"sbt.moneda_factura, "
                 +"sbt.subtotal, "
                 +"(CASE WHEN sbt.numero_dias_pago<=sbt.dias_tope_comision then sbt.comision  "
                      +"WHEN  sbt.numero_dias_pago > sbt.dias_tope_comision  AND sbt.numero_dias_pago <= sbt.dias_tope_comision2  THEN  sbt.comision2  "
                      +"WHEN  sbt.numero_dias_pago> sbt.dias_tope_comision2  AND  sbt.numero_dias_pago <= sbt.dias_tope_comision3  THEN  sbt.comision3  "
                      +"ELSE 0.0  "
                      +"END )AS comision_por_fecha, "

                      +"(case when sbt.numero_dias_pago <= sbt.dias_tope_comision THEN sbt.subtotal * (sbt.comision / 100::double precision)  "
                      +"when  sbt.numero_dias_pago > sbt.dias_tope_comision  AND sbt.numero_dias_pago <= sbt.dias_tope_comision2 THEN  sbt.subtotal * (sbt.comision2::double precision / 100::double precision)  "
                      +"when  sbt.numero_dias_pago > sbt.dias_tope_comision2  AND sbt.numero_dias_pago <= sbt.dias_tope_comision3 THEN sbt.subtotal * (sbt.comision3::double precision / 100::double precision)  "
                      +"ELSE  0.0   "
                  +"end )  AS total_comision_por_fecha, "
                
                +""+monto
                
              +"  from ( "
                      +"SELECT  "
                          +"cxc_agen.id AS numero_agente,  "
                          +"cxc_agen.nombre AS nombre_agente,  "
                          +"erp_h_facturas.serie_folio,  "
                          +"to_char(erp_h_facturas.momento_facturacion,'dd/mm/yyyy') AS fecha_factura,  "
                          +"cxc_clie.razon_social as cliente,  "
                          +"to_char(erp_h_facturas.fecha_ultimo_pago,'dd/mm/yyyy')AS fecha_pago ,   "
                          +"(select to_date(erp_h_facturas.fecha_ultimo_pago::text,'yyyy-mm-dd') - to_date(erp_h_facturas.momento_facturacion::text,'yyyy-mm-dd')) AS numero_dias_pago,  "
                          +"gral_mon.descripcion_abr as moneda_factura, "
                          //+"(CASE WHEN gral_mon.id=1 THEN erp_h_facturas.subtotal ELSE (erp_h_facturas.subtotal * erp_h_facturas.tipo_cambio) END ) AS valor_mn , "
                          +"cxc_agen.dias_tope_comision, "
                          +"cxc_agen.dias_tope_comision2, "
                          +"cxc_agen.dias_tope_comision3, "
                          +"cxc_agen.monto_tope_comision, "
                          +"cxc_agen.monto_tope_comision2, "
                          +"cxc_agen.monto_tope_comision3, "
                          +"cxc_agen.comision, "
                          +"cxc_agen.comision2, "
                          +"cxc_agen.comision3, "
                          +"erp_h_facturas.subtotal, "
                          +"erp_h_facturas.tipo_cambio, "
                          +"to_date(erp_h_facturas.momento_actualizacion::text,'yyyy-mm-dd') as momento_actualizacion, "
                          +"to_date(erp_h_facturas.momento_facturacion::text,'yyyy-mm-dd') as momento_creacion,  "
                          +"gral_mon.id as id_moneda "+coma+""
                          +""+cadena_case
                      +"FROM erp_h_facturas "
                      +"JOIN cxc_clie ON cxc_clie.id = erp_h_facturas.cliente_id "
                      +"JOIN cxc_agen ON cxc_agen.id= erp_h_facturas.cxc_agen_id "
                      +"JOIN gral_mon ON gral_mon.id = erp_h_facturas.moneda_id  "
                      +"WHERE erp_h_facturas.pagado=true "
                      +"AND erp_h_facturas.cancelacion=false "
                      +""+where+" "
                      +"AND to_char(erp_h_facturas.fecha_ultimo_pago,'yyyymmdd')::integer between to_char('"+fecha_inicial+"'::timestamp with time zone,'yyyymmdd')::integer and to_char('"+fecha_final+"'::timestamp with time zone,'yyyymmdd')::integer  "
                      +"AND erp_h_facturas.empresa_id="+id_empresa
                      +"  " +and_montos
                + "  order by nombre_agente asc,moneda_factura, numero_dias_pago asc "//order by nombre_agente asc,moneda_factura, serie_folio asc
              +")AS sbt order by sbt.numero_agente asc, sbt.moneda_factura asc";

        
	 System.out.println("DatosReporteCobranzaAgente:: "+sql_to_query);
        ArrayList<HashMap<String, String>> hm_cobranza_agente = (ArrayList<HashMap<String, String>>) this.jdbcTemplate.query(
            sql_to_query,
            new Object[]{}, new RowMapper(){
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    //Integer monto_inicial=0;
                    //Integer monto_final=0;
                    HashMap<String, String> row = new HashMap<String, String>();
                    row.put("numero_agente",rs.getString("numero_agente"));
                    row.put("nombre_agente",rs.getString("nombre_agente"));
                    row.put("serie_folio",rs.getString("serie_folio"));
                    row.put("fecha_factura",rs.getString("fecha_factura"));
                    row.put("cliente",rs.getString("cliente"));
                    row.put("fecha_pago",rs.getString("fecha_pago"));
                    row.put("numero_dias_pago",rs.getString("numero_dias_pago"));
                    row.put("moneda_factura",rs.getString("moneda_factura"));
                    //row.put("valor_mn",StringHelper.roundDouble(rs.getDouble("valor_mn"), 2));
                    row.put("subtotal",StringHelper.roundDouble(rs.getDouble("subtotal"), 2));
                    row.put("comision_por_fecha",rs.getString("comision_por_fecha"));
                    row.put("total_comision_por_fecha",StringHelper.roundDouble(rs.getDouble("total_comision_por_fecha"), 2));
                    //if(monto_inicial != 0 && monto_final !=0){
                    //row.put("monto_tope_comision",rs.getString("monto_tope_comision"));
                    //row.put("monto_tope_comision2",rs.getString("monto_tope_comision2"));
                    //row.put("monto_tope_comision3",rs.getString("monto_tope_comision3"));
                    row.put("comision_por_monto",rs.getString("comision_por_monto"));
                    //}

                    return row;
                }
            }
        );
        return hm_cobranza_agente;
    }



    @Override
    public ArrayList<HashMap<String, String>> getCartera_DatosReporteVentaxAgente(Integer id_agente, String fecha_inicial, String fecha_final, Integer id_empresa) {

        String where="";

        if(id_agente!=0){
            where=" AND fac_docs.cxc_agen_id="+id_agente;
        }

            String sql_to_query = ""
                      + " SELECT  cxc_agen.id AS numero_agente,   "
                      + " cxc_agen.nombre AS nombre_agente,  "
                      + " fac_docs.serie_folio,  "
                      + " to_char(fac_docs.momento_creacion,'dd/mm/yyyy') AS fecha_factura,  "
                      + " cxc_clie.razon_social as cliente, "
                      + " fac_docs.subtotal as importe, "
                      + " fac_docs.impuesto as iva, "
                      + " fac_docs.total, "
                      + "(case when fac_docs.moneda_id=1 then 'M.N.' ELSE 'USD' END) as moneda_factura"
                      + " FROM fac_docs "
                      + " JOIN cxc_clie ON cxc_clie.id = fac_docs.cxc_clie_id "
                      + " JOIN cxc_agen ON cxc_agen.id=  fac_docs.cxc_agen_id "
                      + " JOIN gral_mon ON gral_mon.id = fac_docs.moneda_id "
                      + " JOIN erp_proceso ON erp_proceso.id= proceso_id "
                      + " WHERE to_char(fac_docs.momento_creacion,'yyyymmdd')::integer  between to_char('"+fecha_inicial+"'::timestamp with time zone,'yyyymmdd')::integer and to_char('"+fecha_final+"'::timestamp with time zone,'yyyymmdd')::integer  "
                      + " AND fac_docs.cancelado=FALSE "
                      + " AND erp_proceso.empresa_id="+id_empresa+" "+where+" "
                    + " order by nombre_agente asc,moneda_factura,serie_folio asc";// by numero_agente,moneda_factura asc ";
        System.out.println("DatosReporteVentaxAgente:: "+sql_to_query);
        ArrayList<HashMap<String, String>> hm_venta_agente = (ArrayList<HashMap<String, String>>) this.jdbcTemplate.query(
            sql_to_query,
            new Object[]{}, new RowMapper(){
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    HashMap<String, String> row = new HashMap<String, String>();
                    row.put("numero_agente",rs.getString("numero_agente"));
                    row.put("nombre_agente",rs.getString("nombre_agente"));
                    row.put("serie_folio",rs.getString("serie_folio"));
                    row.put("fecha_factura",rs.getString("fecha_factura"));
                    row.put("cliente",rs.getString("cliente"));
                    row.put("importe",rs.getString("importe"));
                    row.put("iva",rs.getString("iva"));
                    row.put("total",StringHelper.roundDouble(rs.getDouble("total"), 2));
                    row.put("moneda_factura",rs.getString("moneda_factura"));
                    return row;
                }
            }
        );
        return hm_venta_agente;
    }








    @Override
    public ArrayList<HashMap<String, Object>> getAgente_PaginaGrid(String data_string, int offset, int pageSize, String orderBy, String asc) {
        String sql_busqueda = "select id from gral_bus_catalogos(?) as foo (id integer)";

	String sql_to_query = "SELECT cxc_agen.id, "
                                +"gral_usr.username AS usuario,  "
                                +"cxc_agen.nombre, "
                                +"gral_reg.titulo AS region "
                        +"FROM cxc_agen "
                        +"JOIN gral_usr ON gral_usr.id=cxc_agen.gral_usr_id "
                        +"JOIN gral_reg ON gral_reg.id=cxc_agen.gral_reg_id "
                        +"JOIN ("+sql_busqueda+") AS sbt ON sbt.id = cxc_agen.id "
                        +"order by "+orderBy+" "+asc+" limit ? OFFSET ?";

        //System.out.println("Busqueda GetPage: "+sql_to_query);
        ArrayList<HashMap<String, Object>> hm = (ArrayList<HashMap<String, Object>>) this.jdbcTemplate.query(
            sql_to_query,
            new Object[]{new String(data_string), new Integer(pageSize),new Integer(offset)}, new RowMapper() {
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    HashMap<String, Object> row = new HashMap<String, Object>();
                    row.put("id",rs.getInt("id"));
                    row.put("usuario",rs.getString("usuario"));
                    row.put("nombre",rs.getString("nombre"));
                    row.put("region",rs.getString("region"));
                    return row;
                }
            }
        );
        return hm;
    }





    @Override
    public ArrayList<HashMap<String, String>> getUsuarios(Integer id_empresa, Integer usuario_agente) {

        String where_usuario="";
        if(usuario_agente != 0 ){
            where_usuario = " OR gral_usr.id ="+usuario_agente;
        }

        String sql_to_query = "SELECT gral_usr.id, "
                    + "gral_usr.username,"
                    + "gral_usr.nombre_pila||' '||gral_usr.apellido_paterno||' '||gral_usr.apellido_materno AS nombre_usuario "
                + "FROM gral_usr_suc 	"
                + "JOIN gral_suc ON gral_suc.id = gral_usr_suc.gral_suc_id "
                + "JOIN gral_usr ON gral_usr.id = gral_usr_suc.gral_usr_id "
                + "WHERE gral_usr.id NOT IN ( SELECT gral_usr_id FROM  cxc_agen WHERE borrado_logico=FALSE) "
                + "AND borrado_logico=FALSE AND gral_suc.empresa_id="+id_empresa+" "+where_usuario;

        //System.out.println(sql_to_query);

        ArrayList<HashMap<String, String>> hm_users = (ArrayList<HashMap<String, String>>) this.jdbcTemplate.query(
            sql_to_query,
            new Object[]{}, new RowMapper(){
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    HashMap<String, String> row = new HashMap<String, String>();
                    row.put("id",String.valueOf(rs.getInt("id")));
                    row.put("username",rs.getString("username"));
                    row.put("nombre_usuario",rs.getString("nombre_usuario"));
                    return row;
                }
            }
        );
        return hm_users;
    }



    @Override
    public ArrayList<HashMap<String, String>> getAgente_Datos(Integer id_agente) {
        String sql_to_query = "SELECT id, nombre, comision, gral_reg_id, gral_usr_id FROM cxc_agen WHERE id ="+id_agente;

        ArrayList<HashMap<String, String>> hm_users = (ArrayList<HashMap<String, String>>) this.jdbcTemplate.query(
            sql_to_query,
            new Object[]{}, new RowMapper(){
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    HashMap<String, String> row = new HashMap<String, String>();
                    row.put("id",String.valueOf(rs.getInt("id")));
                    row.put("nombre",rs.getString("nombre"));
                    row.put("comision",StringHelper.roundDouble(rs.getDouble("comision"),2));
                    row.put("gral_reg_id",String.valueOf(rs.getInt("gral_reg_id")));
                    row.put("gral_usr_id",String.valueOf(rs.getInt("gral_usr_id")));
                    return row;
                }
            }
        );
        return hm_users;
    }



    @Override
    public ArrayList<HashMap<String, String>> getAgente_Regiones() {
        String sql_to_query = "SELECT id, titulo FROM gral_reg WHERE borrado_logico=FALSE;";

        ArrayList<HashMap<String, String>> hm_reg = (ArrayList<HashMap<String, String>>) this.jdbcTemplate.query(
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
        return hm_reg;
    }



    @Override
    public ArrayList<HashMap<String, String>> getPronosticoDeCobranza(String num_semanas, String opcion_seleccionada, Integer id_empresa) {
        String sql_to_query = "select * from repPronostico_semanas_proximas('"+num_semanas+"',"+id_empresa+") as foo( "
                                + " numero_control character varying, "
                                + " cliente character varying, "
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
                   row.put("numero_control",rs.getString("numero_control"));
                    row.put("cliente",rs.getString("cliente"));
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





    @Override
    public ArrayList<HashMap<String, String>> getVentasNetasxCliente(String fecha_inicial, String fecha_final,Integer id_empresa) {
           String sql_to_query = " SELECT * "
                                + " from  (select distinct numero_control,  "
                                + " cliente, "
                                + " pesos,porcentaje, "
                                + " sum(totalporfactura)as Tventa_neta    "
                                + " from  (  "
                                        +  " SELECT  cxc_clie.numero_control,  "
                                        + " cxc_clie.razon_social AS cliente, "
                                        + " '$'::character varying as pesos,  "
                                        + " (CASE WHEN fac_docs.moneda_id=1 THEN fac_docs.subtotal * 1 ELSE fac_docs.subtotal * fac_docs.tipo_cambio END) AS totalporfactura,  "
                                        + " '%'::character varying as porcentaje   "
                                        + " FROM fac_docs     "
                                        + " JOIN erp_proceso on erp_proceso.id=fac_docs.proceso_id  "
                                        + " JOIN cxc_clie ON cxc_clie.id = fac_docs.cxc_clie_id     "
                                        + " JOIN gral_mon ON gral_mon.id = fac_docs.moneda_id  "
                                        + " WHERE fac_docs.cancelado = false    "
                                        + " AND erp_proceso.empresa_id = "+id_empresa+" "
                                        + " AND to_char(fac_docs.momento_creacion,'yyyymmdd')   "
                                        + " between to_char('"+fecha_inicial+"'::timestamp with time zone,'yyyymmdd')  "
                                        + " and to_char('"+fecha_final+"'::timestamp with time zone,'yyyymmdd')  "
                                        + " order by numero_control asc   "
                                +  " ) as sbt    "
                                + " GROUP BY sbt.numero_control,  "
                                + " sbt.cliente,  "
                                + " sbt.pesos,  "
                                + " sbt.porcentaje  "
                                + " ORDER BY sbt.numero_control asc "
                                + " )as tb3  "
                                    + " order by tventa_neta desc ";

       System.out.println("sql_to_query:"+ sql_to_query);

        ArrayList<HashMap<String, String>> hm = (ArrayList<HashMap<String, String>>) this.jdbcTemplate.query(
                sql_to_query,
            new Object[]{}, new RowMapper() {
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    HashMap<String, String> row = new HashMap<String, String>();
                    row.put("numero_control",rs.getString("numero_control"));
                    row.put("cliente",rs.getString("cliente"));
                    row.put("Tventa_neta",rs.getString("Tventa_neta"));
                    row.put("pesos",rs.getString("pesos"));
                    row.put("porcentaje",rs.getString("porcentaje"));
                    row.put("numero_control",rs.getString("numero_control"));
                    return row;
                }
            }
        );
        return hm;
    }




    //Reporte de Ventas Netas por Producto
    //Reporte Comercial para Agentes de Ventas
    @Override
    public ArrayList<HashMap<String, String>> getVentasNetasProductoFactura(Integer tipo_reporte, String cliente,String producto, String fecha_inicial, String fecha_final,Integer id_empresa,Integer id_linea,Integer  id_marca, Integer id_familia,Integer id_subfamilia,Integer tipo_costo, Integer id_agente, Integer segmentoId, Integer mercadoId) {
    String sql_to_query = "select * from repventasnetasproductofactura("+tipo_reporte+",'"+cliente+"','"+producto+"','"+fecha_inicial+"','"+fecha_final+"',"+id_empresa+","+id_linea+","+id_marca+","+id_familia+","+id_subfamilia+","+tipo_costo+","+id_agente+","+segmentoId+","+mercadoId+") as foo( "
                                    + " numero_control character varying, "
                                    + " razon_social character varying, "
                                    + " codigo character varying, "
                                    + " producto character varying, "
                                    + " factura character varying , "
                                    + " unidad character varying , "
                                    + " cantidad double precision, "
                                    + " precio_unitario double precision, "
                                    + " moneda text, "
                                    + " tipo_cambio double precision,  "
                                    + " venta_pesos double precision,  "
                                    + " costo double precision,  "
                                    + " fecha_factura text,"
                                    + " momento_creacion timestamp with time zone,"
                                    + "id_presentacion Integer, "
                                    + "presentacion character varying"
                                    + "); ";

       //System.out.println("getVentasNetasProductoFactura:"+ sql_to_query);

        ArrayList<HashMap<String, String>> hm = (ArrayList<HashMap<String, String>>) this.jdbcTemplate.query(
                sql_to_query,
            new Object[]{}, new RowMapper() {
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    HashMap<String, String> row = new HashMap<String, String>();

                    row.put("numero_control",rs.getString("numero_control"));
                    row.put("razon_social",rs.getString("razon_social"));
                    row.put("codigo",rs.getString("codigo"));
                    row.put("producto",rs.getString("producto"));
                    row.put("factura",rs.getString("factura"));
                    row.put("unidad",rs.getString("unidad"));
                    row.put("cantidad",StringHelper.roundDouble(rs.getString("cantidad"),2));
                    row.put("precio_unitario",StringHelper.roundDouble(rs.getString("precio_unitario"),2));
                    row.put("moneda",rs.getString("moneda"));
                    row.put("tipo_cambio",StringHelper.roundDouble(rs.getString("tipo_cambio"),4));
                    row.put("venta_pesos",StringHelper.roundDouble(rs.getString("venta_pesos"),2));
                    row.put("costo",StringHelper.roundDouble(rs.getString("costo"),2));
                    row.put("fecha_factura",rs.getString("fecha_factura"));

                    row.put("id_presentacion",rs.getString("id_presentacion"));
                    row.put("presentacion",rs.getString("presentacion"));

                    return row;
                }
            }
        );
        return hm;
    }



    //obtiene tipos de productos
    @Override
    public ArrayList<HashMap<String, String>> getProductoTipos() {
	String sql_query = "SELECT DISTINCT id,titulo FROM inv_prod_tipos WHERE borrado_logico=false order by id;";
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
    
    //obtiene tipos de productos(Retorna String Object)
    @Override
    public ArrayList<HashMap<String, Object>> getProductoTiposV2() {
	String sql_query = "SELECT DISTINCT id,titulo FROM inv_prod_tipos WHERE borrado_logico=false order by id;";
        ArrayList<HashMap<String, Object>> hm_tp = (ArrayList<HashMap<String, Object>>) this.jdbcTemplate.query(
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

        return hm_tp;
    }
    
    //obtiene las lineas de los  productos
    @Override
    public ArrayList<HashMap<String, String>> getLineas(Integer empresaId) {
        String sql_query = "SELECT DISTINCT id,titulo FROM inv_prod_lineas WHERE borrado_logico=false AND gral_emp_id=? order by titulo;";
        ArrayList<HashMap<String, String>> lineas = (ArrayList<HashMap<String, String>>) this.jdbcTemplate.query(
            sql_query,
            new Object[]{new Integer(empresaId)}, new RowMapper() {
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    HashMap<String, String> row = new HashMap<String, String>();
                    row.put("id",rs.getString("id"));
                    row.put("titulo",rs.getString("titulo"));
                    return row;
                }
            }
        );

        return lineas;
    }

    //obtiene las marcas de los  productos
    @Override
    public ArrayList<HashMap<String, String>> getMarcas(Integer empresaId) {
        String sql_query = "SELECT DISTINCT id,titulo FROM inv_mar WHERE borrado_logico=false and gral_emp_id=? order by titulo;";
        ArrayList<HashMap<String, String>> marcas = (ArrayList<HashMap<String, String>>) this.jdbcTemplate.query(
            sql_query,
            new Object[]{new Integer(empresaId)}, new RowMapper() {
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    HashMap<String, String> row = new HashMap<String, String>();
                    row.put("id",rs.getString("id"));
                    row.put("titulo",rs.getString("titulo"));
                    return row;
                }
            }
        );

        return marcas;
    }


    //obtiene las familias de los produtos
    @Override
    public ArrayList<HashMap<String, String>> getFamilias(Integer empresaId) {
        String sql_query = "SELECT distinct id,titulo FROM inv_prod_familias WHERE borrado_logico=false AND id=identificador_familia_padre AND gral_emp_id=? order by titulo;";
        ArrayList<HashMap<String, String>> familias = (ArrayList<HashMap<String, String>>) this.jdbcTemplate.query(
            sql_query,
            new Object[]{new Integer(empresaId)}, new RowMapper() {
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    HashMap<String, String> row = new HashMap<String, String>();
                    row.put("id",rs.getString("id"));
                    row.put("titulo",rs.getString("titulo"));
                    return row;
                }
            }
        );

        return familias;
    }



    //obtiene las subfamilias de los produtos
    @Override
    public ArrayList<HashMap<String, String>> getSubfamilias(Integer id_familia) {
        String sql_query = ""
            + "select id,identificador_familia_padre,titulo from( "
                +"  select "
                +"  id, "
                +"  identificador_familia_padre, "
                +"  titulo, descripcion, "
                +"  borrado_logico "
                +"  from inv_prod_familias "
                +"  where identificador_familia_padre="+id_familia
            +"  )as sbt "
            +"  where  sbt.id  != sbt.identificador_familia_padre";
            System.out.println("cargando subfamilias:   "+ sql_query);
        ArrayList<HashMap<String, String>> subfamilias = (ArrayList<HashMap<String, String>>) this.jdbcTemplate.query(
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

        return subfamilias;
    }







    //buscador de productos
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
                    return row;
                }
            }
        );
        return hm_datos_productos;
    }






    @Override
    public ArrayList<HashMap<String, Object>> getClientsClasif1_PaginaGrid(String data_string, int offset, int pageSize, String orderBy, String asc) {
        String sql_busqueda = "select id from gral_bus_catalogos(?) as foo (id integer)";

	String sql_to_query = "SELECT cxc_clie_clas1.id, cxc_clie_clas1.titulo FROM cxc_clie_clas1 "
                        +"JOIN ("+sql_busqueda+") AS sbt ON sbt.id = cxc_clie_clas1.id "
                        +"order by "+orderBy+" "+asc+" limit ? OFFSET ?";

        //System.out.println("Busqueda GetPage: "+sql_to_query);
        ArrayList<HashMap<String, Object>> hm = (ArrayList<HashMap<String, Object>>) this.jdbcTemplate.query(
            sql_to_query,
            new Object[]{data_string, new Integer(pageSize),new Integer(offset)}, new RowMapper() {
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
    public ArrayList<HashMap<String, String>> getClientsClasif1_Datos(Integer id) {
        String sql_query = "SELECT id, titulo FROM cxc_clie_clas1 WHERE id = ?;";
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


    @Override
    public ArrayList<HashMap<String, Object>> getClientsClasif2_PaginaGrid(String data_string, int offset, int pageSize, String orderBy, String asc) {
        String sql_busqueda = "select id from gral_bus_catalogos(?) as foo (id integer)";

	String sql_to_query = "SELECT cxc_clie_clas2.id, cxc_clie_clas2.titulo FROM cxc_clie_clas2 "
                        +"JOIN ("+sql_busqueda+") AS sbt ON sbt.id = cxc_clie_clas2.id "
                        +"order by "+orderBy+" "+asc+" limit ? OFFSET ?";

        //System.out.println("Busqueda GetPage: "+sql_to_query);
        ArrayList<HashMap<String, Object>> hm = (ArrayList<HashMap<String, Object>>) this.jdbcTemplate.query(
            sql_to_query,
            new Object[]{data_string, new Integer(pageSize),new Integer(offset)}, new RowMapper() {
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
    public ArrayList<HashMap<String, String>> getClientsClasif2_Datos(Integer id) {
        String sql_query = "SELECT id, titulo FROM cxc_clie_clas2 WHERE id = ?;";
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


    @Override
    public ArrayList<HashMap<String, Object>> getClientsClasif3_PaginaGrid(String data_string, int offset, int pageSize, String orderBy, String asc) {
        String sql_busqueda = "select id from gral_bus_catalogos(?) as foo (id integer)";

	String sql_to_query = "SELECT cxc_clie_clas3.id, cxc_clie_clas3.titulo FROM cxc_clie_clas3 "
                        +"JOIN ("+sql_busqueda+") AS sbt ON sbt.id = cxc_clie_clas3.id "
                        +"order by "+orderBy+" "+asc+" limit ? OFFSET ?";

        //System.out.println("Busqueda GetPage: "+sql_to_query);
        ArrayList<HashMap<String, Object>> hm = (ArrayList<HashMap<String, Object>>) this.jdbcTemplate.query(
            sql_to_query,
            new Object[]{data_string, new Integer(pageSize),new Integer(offset)}, new RowMapper() {
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
    public ArrayList<HashMap<String, String>> getClientsClasif3_Datos(Integer id) {
        String sql_query = "SELECT id, titulo FROM cxc_clie_clas3 WHERE id = ?;";
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


	@Override
    public ArrayList<HashMap<String, Object>> getclientsZonas_PaginaGrid(String data_string, int offset, int pageSize, String orderBy, String asc) {
        //throw new UnsupportedOperationException("Not supported yet.");
        String sql_busqueda = "select id from gral_bus_catalogos(?) as foo (id integer)";

	String sql_to_query = "SELECT cxc_clie_zonas.id, cxc_clie_zonas.titulo FROM cxc_clie_zonas "
                        +"JOIN ("+sql_busqueda+") AS sbt ON sbt.id = cxc_clie_zonas.id where cxc_clie_zonas.borrado_logico = false or cxc_clie_zonas.borrado_logico is null  "
                        +"order by "+orderBy+" "+asc+" limit ? OFFSET ?";

        //System.out.println("Busqueda GetPage: "+sql_to_query);
        ArrayList<HashMap<String, Object>> hm = (ArrayList<HashMap<String, Object>>) this.jdbcTemplate.query(
            sql_to_query,
            new Object[]{data_string, new Integer(pageSize),new Integer(offset)}, new RowMapper() {
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
    public ArrayList<HashMap<String, String>> getClientsZonas_Datos(Integer id) {
        //throw new UnsupportedOperationException("Not supported yet.");
        String sql_query = "SELECT id, titulo FROM cxc_clie_zonas WHERE id = ?;";
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



    @Override
    public ArrayList<HashMap<String, Object>> getClientsGrupos_PaginaGrid(String data_string, int offset, int pageSize, String orderBy, String asc) {
        //throw new UnsupportedOperationException("Not supported yet.");
        String sql_busqueda = "select id from gral_bus_catalogos(?) as foo (id integer)";

	String sql_to_query = "SELECT cxc_clie_grupos.id, cxc_clie_grupos.titulo FROM cxc_clie_grupos "
                        +"JOIN ("+sql_busqueda+") AS sbt ON sbt.id = cxc_clie_grupos.id where cxc_clie_grupos.borrado_logico = false  "
                        +"order by "+orderBy+" "+asc+"  limit ? OFFSET ?";

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
    public ArrayList<HashMap<String, String>> getClientsGrupos_Datos(Integer id) {
        //throw new UnsupportedOperationException("Not supported yet.");
        String sql_query = "SELECT id, titulo FROM cxc_clie_grupos WHERE id = ?;";
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



    @Override
    public ArrayList<HashMap<String, Object>> getClientstMovimientos_PaginaGrid(String data_string, int offset, int pageSize, String orderBy, String asc) {
        //throw new UnsupportedOperationException("Not supported yet.");
        String sql_busqueda = "select id from gral_bus_catalogos(?) as foo (id integer)";

	String sql_to_query = "SELECT cxc_mov_tipos.id, cxc_mov_tipos.titulo,cxc_mov_tipos.descripcion,gral_mon.descripcion_abr as moneda "
                               + " FROM cxc_mov_tipos "
                               + " JOIN gral_mon on gral_mon.id= cxc_mov_tipos.moneda_id "
                        +"JOIN ("+sql_busqueda+") AS sbt ON sbt.id = cxc_mov_tipos.id "
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
                    row.put("descripcion",rs.getString("descripcion"));
                    row.put("moneda",rs.getString("moneda"));
                    return row;
                }
            }
        );
        return hm;
    }



    @Override
    public ArrayList<HashMap<String, Object>> getClientstMovimientos_Datos(Integer id) {
        String sql_query = "SELECT id, titulo,descripcion,moneda_id   FROM cxc_mov_tipos WHERE id =" +id;
        System.out.print(sql_query);
        ArrayList<HashMap<String, Object>> hm = (ArrayList<HashMap<String, Object>>) this.jdbcTemplate.query(
            sql_query,
            new Object[]{}, new RowMapper() {
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
    public ArrayList<HashMap<String, String>> getDatos_ReporteAntiguedadSaldos(Integer tipo, String cliente, Integer id_empresa) {
        String where_cliente="";

        if(tipo == 1){
            where_cliente = " AND cxc_clie.razon_social ILIKE '%"+cliente+"%'";
        }


        String sql_to_query = ""
            + "SELECT cliente,"
                    + "clave_cliente,"
                    + "factura,"
                    + "(CASE WHEN (saldo_factura > 0) THEN simbolo_moneda ELSE '' END) AS moneda_saldo_factura, "
                    + "saldo_factura,"
                    + "monto_factura,"
                    + "moneda_factura,"
                    + "simbolo_moneda,"
                    + "fecha_facturacion,"
                    + "fecha_vencimiento,"
                    + "(CASE WHEN (por_vencer > 0) THEN simbolo_moneda ELSE '' END) AS moneda_por_vencer, "
                    + "por_vencer,"
                    + "(CASE WHEN (menor_igual_15 > 0) THEN simbolo_moneda ELSE '' END) AS moneda_menor_igual_15, "
                    + "menor_igual_15,"
                    + "(CASE WHEN (menor_igual_30 > 0) THEN simbolo_moneda ELSE '' END) AS moneda_menor_igual_30, "
                    + "menor_igual_30,"
                    + "(CASE WHEN (menor_igual_45 > 0) THEN simbolo_moneda ELSE '' END) AS moneda_menor_igual_45, "
                    + "menor_igual_45,"
                    + "(CASE WHEN (menor_igual_60 > 0) THEN simbolo_moneda ELSE '' END) AS moneda_menor_igual_60, "
                    + "menor_igual_60,"
                    + "(CASE WHEN (menor_igual_90 > 0) THEN simbolo_moneda ELSE '' END) AS moneda_menor_igual_90, "
                    + "menor_igual_90,"
                    + "(CASE WHEN (mayor_90 > 0) THEN simbolo_moneda ELSE '' END) AS moneda_mayor_90, "
                    + "mayor_90 "
            + "FROM ( "
                + "SELECT cliente,"
                    + "clave_cliente,"
                    + "factura,"
                    + "saldo_factura,"
                    + "monto_factura,"
                    + "moneda_factura,"
                    + "simbolo_moneda,"
                    + "fecha_facturacion,"
                    + "fecha_vencimiento,"
                    + "dias_vencidos,"
                    + "(CASE WHEN (dias_vencidos<0) then saldo_factura ELSE 0 END) AS por_vencer,"
                    + "(CASE WHEN (dias_vencidos>=0 AND dias_vencidos<=15) then saldo_factura ELSE 0 END) AS menor_igual_15,"
                    + "(CASE WHEN (dias_vencidos>=16 AND dias_vencidos<=30) then saldo_factura ELSE 0 END) AS menor_igual_30,"
                    + "(CASE WHEN (dias_vencidos>=31 AND dias_vencidos<=45) then saldo_factura ELSE 0 END) AS menor_igual_45,"
                    + "(CASE WHEN (dias_vencidos>=46 AND dias_vencidos<=60) then saldo_factura ELSE 0 END) AS menor_igual_60,"
                    + "(CASE WHEN (dias_vencidos>=61 AND dias_vencidos<=90) then saldo_factura ELSE 0 END) AS menor_igual_90,"
                    + "(CASE WHEN (dias_vencidos>=91) then saldo_factura ELSE 0 END) AS mayor_90 "
                    + "FROM ( "
                        + "SELECT cxc_clie.numero_control AS clave_cliente, "
                            + "cxc_clie.razon_social AS cliente,"
                            + "erp_h_facturas.serie_folio AS factura,"
                            + "erp_h_facturas.monto_total AS monto_factura,"
                            + "gral_mon.descripcion_abr AS moneda_factura,"
                            + "gral_mon.simbolo AS simbolo_moneda,"
                            + "to_char(erp_h_facturas.momento_facturacion,'dd/mm/yyyy') AS fecha_facturacion,"
                            + "to_char(erp_h_facturas.fecha_vencimiento,'dd/mm/yyyy') as fecha_vencimiento,"
                            + "NOW()::DATE-erp_h_facturas.fecha_vencimiento::DATE  AS dias_vencidos,"
                            + "erp_h_facturas.saldo_factura "
                        + "FROM erp_h_facturas "
                        + "JOIN cxc_clie ON cxc_clie.id = erp_h_facturas.cliente_id "
                        + "JOIN gral_mon ON gral_mon.id = erp_h_facturas.moneda_id "
                        + "WHERE erp_h_facturas.cancelacion = FALSE "
                        + "AND erp_h_facturas.pagado = FALSE "
                        + "AND erp_h_facturas.empresa_id="+id_empresa +" "+ where_cliente
                + ") AS sbt "
        + ") AS sbt2 "
                + "ORDER BY cliente, moneda_factura asc,factura";//cliente,moneda_factura;";

        System.out.println("getDatos_ReporteAntiguedadSaldos: "+sql_to_query);

        ArrayList<HashMap<String, String>> hm = (ArrayList<HashMap<String, String>>) this.jdbcTemplate.query(
                sql_to_query,
            new Object[]{}, new RowMapper() {
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    HashMap<String, String> row = new HashMap<String, String>();
                    row.put("clave_cliente",rs.getString("clave_cliente"));
                    row.put("cliente",rs.getString("cliente"));
                    row.put("factura",rs.getString("factura"));
                    row.put("moneda_saldo_factura",rs.getString("moneda_saldo_factura"));
                    row.put("saldo_factura",StringHelper.roundDouble(rs.getDouble("saldo_factura"),2));
                    row.put("moneda_factura",rs.getString("moneda_factura"));
                    row.put("simbolo_moneda",rs.getString("simbolo_moneda"));
                    row.put("fecha_facturacion",rs.getString("fecha_facturacion"));
                    row.put("fecha_vencimiento",rs.getString("fecha_vencimiento"));
                    row.put("moneda_por_vencer",rs.getString("moneda_por_vencer"));
                    row.put("por_vencer",StringHelper.roundDouble(rs.getDouble("por_vencer"),2));
                    row.put("moneda_menor_igual_15",rs.getString("moneda_menor_igual_15"));
                    row.put("menor_igual_15",StringHelper.roundDouble(rs.getDouble("menor_igual_15"),2));
                    row.put("moneda_menor_igual_30",rs.getString("moneda_menor_igual_30"));
                    row.put("menor_igual_30",StringHelper.roundDouble(rs.getDouble("menor_igual_30"),2));
                    row.put("moneda_menor_igual_45",rs.getString("moneda_menor_igual_45"));
                    row.put("menor_igual_45",StringHelper.roundDouble(rs.getDouble("menor_igual_45"),2));
                    row.put("moneda_menor_igual_60",rs.getString("moneda_menor_igual_60"));
                    row.put("menor_igual_60",StringHelper.roundDouble(rs.getDouble("menor_igual_60"),2));
                    row.put("moneda_menor_igual_90",rs.getString("moneda_menor_igual_90"));
                    row.put("menor_igual_90",StringHelper.roundDouble(rs.getDouble("menor_igual_90"),2));
                    row.put("moneda_mayor_90",rs.getString("moneda_mayor_90"));
                    row.put("mayor_90",StringHelper.roundDouble(rs.getDouble("mayor_90"),2));
                    return row;
                }
            }
        );
        return hm;
    }





    // obtiene el SQL de estadisticas anuales de ventas
    @Override
    public ArrayList<HashMap<String, String>> getEstadisticaVentas(Integer mes_in,Integer mes_fin, Integer anio, Integer id_empresa) {

        String where="";

       String sql_to_query=""+"SELECT razon_social, "
                                    +"sum(enero) as enero, "
                                    +"sum(febrero) as febrero, "
                                    +"sum(marzo) as marzo, "
                                    +"sum(abril) as abril, "
                                    +"sum(mayo) as mayo, "
                                    +"sum(junio) as junio, "
                                    +"sum(julio) as julio, "
                                    +"sum(agosto) as agosto, "
                                    +"sum(septiembre) as septiembre, "
                                    +"sum(octubre) as octubre, "
                                    +"sum(noviembre) as noviembre, "
                                    +"sum(diciembre) as diciembre, "
                                    +"sum(enero)+sum(febrero)+sum(marzo)+sum(abril)+sum(mayo)+sum(junio)+sum(julio)+sum(agosto)+sum(septiembre)+sum(octubre)+sum(noviembre)+sum(diciembre)  as suma_total "
                            +"FROM( SELECT razon_social, "
                                        +"(CASE WHEN mes=1 THEN subtotal ELSE 0 END) AS enero, "
                                        +"(CASE WHEN mes=2 THEN subtotal ELSE 0 END) AS febrero, "
                                        +"(CASE WHEN mes=3 THEN subtotal ELSE 0 END) AS marzo, "
                                        +"(CASE WHEN mes=4 THEN subtotal ELSE 0 END) AS abril, "
                                        +"(CASE WHEN mes=5 THEN subtotal ELSE 0 END) AS mayo, "
                                        +"(CASE WHEN mes=6 THEN subtotal ELSE 0 END) AS junio, "
                                        +"(CASE WHEN mes=7 THEN subtotal ELSE 0 END) AS julio, "
                                        +"(CASE WHEN mes=8 THEN subtotal ELSE 0 END) AS agosto, "
                                        +"(CASE WHEN mes=9 THEN subtotal ELSE 0 END) AS septiembre, "
                                        +"(CASE WHEN mes=10 THEN subtotal ELSE 0 END) AS octubre, "
                                        +"(CASE WHEN mes=11 THEN subtotal ELSE 0 END) AS noviembre, "
                                        +"(CASE WHEN mes=12 THEN subtotal ELSE 0 END) AS diciembre, "
                                        +"subtotal "
                                    +"FROM ("
                                            +"SELECT cxc_clie.razon_social, "
                                            +"EXTRACT(MONTH FROM fac_docs.momento_creacion) as mes, "
                                            +"(CASE WHEN fac_docs.moneda_id=1 THEN fac_docs.subtotal ELSE (fac_docs.tipo_cambio * fac_docs.subtotal) END) as subtotal "
                                            +"FROM fac_docs "
                                            +"join cxc_clie on cxc_clie.id=fac_docs.cxc_clie_id "
                                            +"JOIN erp_proceso on erp_proceso.id=fac_docs.proceso_id "
                                            +"WHERE erp_proceso.empresa_id ="+id_empresa + " "
                                            +"AND fac_docs.cancelado=false "
                                            +"AND EXTRACT(MONTH FROM fac_docs.momento_creacion) between "+mes_in+ " and "+mes_fin+ " "
                                            + "AND EXTRACT(YEAR  FROM fac_docs.momento_creacion)="+anio+" "
                                            //+ "AND EXTRACT(YEAR  FROM fac_docs.momento_creacion)=EXTRACT(YEAR FROM now()) "
                                    +") AS sbt "
                            +")as sbt2 "
                            +"GROUP BY razon_social ";
        System.out.println("Generando Consulta Estadisticas:"+sql_to_query+"");

        ArrayList<HashMap<String, String>> hm_facturas = (ArrayList<HashMap<String, String>>) this.jdbcTemplate.query(
            sql_to_query,
            new Object[]{}, new RowMapper(){
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    HashMap<String, String> row = new HashMap<String, String>();
                    row.put("razon_social",rs.getString("razon_social"));
                    row.put("enero",StringHelper.roundDouble(rs.getString("enero"),2));
                    row.put("febrero",StringHelper.roundDouble(rs.getString("febrero"),2));
                    row.put("marzo",StringHelper.roundDouble(rs.getString("marzo"),2));
                    row.put("abril",StringHelper.roundDouble(rs.getString("abril"),2));
                    row.put("mayo",StringHelper.roundDouble(rs.getString("mayo"),2));
                    row.put("junio",StringHelper.roundDouble(rs.getString("junio"),2));
                    row.put("julio",StringHelper.roundDouble(rs.getString("julio"),2));
                    row.put("agosto",StringHelper.roundDouble(rs.getString("agosto"),2));
                    row.put("septiembre",StringHelper.roundDouble(rs.getString("septiembre"),2));
                    row.put("octubre",StringHelper.roundDouble(rs.getString("octubre"),2));
                    row.put("noviembre",StringHelper.roundDouble(rs.getString("noviembre"),2));
                    row.put("diciembre",StringHelper.roundDouble(rs.getString("diciembre"),2));
                    row.put("suma_total",StringHelper.roundDouble(rs.getDouble("suma_total"), 2));

                    return row;
                }
            }
        );
        return hm_facturas;
    }


    
    
    
    
    
    
    
    
    
    
    
    //Obtiene comparativo de Ventas
 //Obtiene el SQL de /Reporte Comparativo de Ventas Anual por Cliente
  @Override
    public ArrayList<HashMap<String, String>> getComparativoVentas(String cliente, Integer anio_inicial, Integer anio_final, Integer id_empresa) {
		
        String where="";
        Integer mes_uno =1;
        Integer mes_dos =2;
        Integer mes_tres =3;
        Integer mes_cuatro =4;
        Integer mes_cinco =5;
        Integer mes_seis =6;
        Integer mes_siete =7;
        Integer mes_ocho =8;
        Integer mes_nueve =9;
        Integer mes_diez =10;
        Integer mes_once =11;
        Integer mes_doce =12;
     
      String sql_to_query=""+"SELECT razon_social, "
                            +"SUM((CASE WHEN fac_docs.moneda_id=1 THEN fac_docs.subtotal ELSE (fac_docs.tipo_cambio*fac_docs.subtotal) END)*(1-ABS(SIGN(to_char(fac_docs.momento_creacion,'mmyyyy')::integer-"+mes_uno+""+anio_inicial+")))) as enero,"
                            +"SUM((CASE WHEN fac_docs.moneda_id=1 THEN fac_docs.subtotal ELSE (fac_docs.tipo_cambio*fac_docs.subtotal) END)*(1-ABS(SIGN(to_char(fac_docs.momento_creacion,'mmyyyy')::integer-"+mes_uno+""+anio_final+")))) as enero2,"
                            +"SUM((CASE WHEN fac_docs.moneda_id=1 THEN fac_docs.subtotal ELSE (fac_docs.tipo_cambio*fac_docs.subtotal) END)*(1-ABS(SIGN(to_char(fac_docs.momento_creacion,'mmyyyy')::integer-"+mes_dos+""+anio_inicial+")))) as febrero,"
                            +"SUM((CASE WHEN fac_docs.moneda_id=1 THEN fac_docs.subtotal ELSE (fac_docs.tipo_cambio*fac_docs.subtotal) END)*(1-ABS(SIGN(to_char(fac_docs.momento_creacion,'mmyyyy')::integer-"+mes_dos+""+anio_final+")))) as febrero2,"
                            +"SUM((CASE WHEN fac_docs.moneda_id=1 THEN fac_docs.subtotal ELSE (fac_docs.tipo_cambio*fac_docs.subtotal) END)*(1-ABS(SIGN(to_char(fac_docs.momento_creacion,'mmyyyy')::integer-"+mes_tres+""+anio_inicial+")))) as marzo,"
                            +"SUM((CASE WHEN fac_docs.moneda_id=1 THEN fac_docs.subtotal ELSE (fac_docs.tipo_cambio*fac_docs.subtotal) END)*(1-ABS(SIGN(to_char(fac_docs.momento_creacion,'mmyyyy')::integer-"+mes_tres+""+anio_final+")))) as marzo2,"
                            +"SUM((CASE WHEN fac_docs.moneda_id=1 THEN fac_docs.subtotal ELSE (fac_docs.tipo_cambio*fac_docs.subtotal) END)*(1-ABS(SIGN(to_char(fac_docs.momento_creacion,'mmyyyy')::integer-"+mes_cuatro+""+anio_inicial+")))) as abril,"
                            +"SUM((CASE WHEN fac_docs.moneda_id=1 THEN fac_docs.subtotal ELSE (fac_docs.tipo_cambio*fac_docs.subtotal) END)*(1-ABS(SIGN(to_char(fac_docs.momento_creacion,'mmyyyy')::integer-"+mes_cuatro+""+anio_final+")))) as abril2,"
                            +"SUM((CASE WHEN fac_docs.moneda_id=1 THEN fac_docs.subtotal ELSE (fac_docs.tipo_cambio*fac_docs.subtotal) END)*(1-ABS(SIGN(to_char(fac_docs.momento_creacion,'mmyyyy')::integer-"+mes_cinco+""+anio_inicial+")))) as mayo,"
                            +"SUM((CASE WHEN fac_docs.moneda_id=1 THEN fac_docs.subtotal ELSE (fac_docs.tipo_cambio*fac_docs.subtotal) END)*(1-ABS(SIGN(to_char(fac_docs.momento_creacion,'mmyyyy')::integer-"+mes_cinco+""+anio_final+")))) as mayo2,"
                            +"SUM((CASE WHEN fac_docs.moneda_id=1 THEN fac_docs.subtotal ELSE (fac_docs.tipo_cambio*fac_docs.subtotal) END)*(1-ABS(SIGN(to_char(fac_docs.momento_creacion,'mmyyyy')::integer-"+mes_seis+""+anio_inicial+")))) as junio,"
                            +"SUM((CASE WHEN fac_docs.moneda_id=1 THEN fac_docs.subtotal ELSE (fac_docs.tipo_cambio*fac_docs.subtotal) END)*(1-ABS(SIGN(to_char(fac_docs.momento_creacion,'mmyyyy')::integer-"+mes_seis+""+anio_final+")))) as junio2,"
                            +"SUM((CASE WHEN fac_docs.moneda_id=1 THEN fac_docs.subtotal ELSE (fac_docs.tipo_cambio*fac_docs.subtotal) END)*(1-ABS(SIGN(to_char(fac_docs.momento_creacion,'mmyyyy')::integer-"+mes_siete+""+anio_inicial+")))) as julio,"
                            +"SUM((CASE WHEN fac_docs.moneda_id=1 THEN fac_docs.subtotal ELSE (fac_docs.tipo_cambio*fac_docs.subtotal) END)*(1-ABS(SIGN(to_char(fac_docs.momento_creacion,'mmyyyy')::integer-"+mes_siete+""+anio_final+")))) as julio2,"
                            +"SUM((CASE WHEN fac_docs.moneda_id=1 THEN fac_docs.subtotal ELSE (fac_docs.tipo_cambio*fac_docs.subtotal) END)*(1-ABS(SIGN(to_char(fac_docs.momento_creacion,'mmyyyy')::integer-"+mes_ocho+""+anio_inicial+")))) as agosto,"
                            +"SUM((CASE WHEN fac_docs.moneda_id=1 THEN fac_docs.subtotal ELSE (fac_docs.tipo_cambio*fac_docs.subtotal) END)*(1-ABS(SIGN(to_char(fac_docs.momento_creacion,'mmyyyy')::integer-"+mes_ocho+""+anio_final+")))) as agosto2,"
                            +"SUM((CASE WHEN fac_docs.moneda_id=1 THEN fac_docs.subtotal ELSE (fac_docs.tipo_cambio*fac_docs.subtotal) END)*(1-ABS(SIGN(to_char(fac_docs.momento_creacion,'mmyyyy')::integer-"+mes_nueve+""+anio_inicial+")))) as septiembre,"
                            +"SUM((CASE WHEN fac_docs.moneda_id=1 THEN fac_docs.subtotal ELSE (fac_docs.tipo_cambio*fac_docs.subtotal) END)*(1-ABS(SIGN(to_char(fac_docs.momento_creacion,'mmyyyy')::integer-"+mes_nueve+""+anio_final+")))) as septiembre2,"
                            +"SUM((CASE WHEN fac_docs.moneda_id=1 THEN fac_docs.subtotal ELSE (fac_docs.tipo_cambio*fac_docs.subtotal) END)*(1-ABS(SIGN(to_char(fac_docs.momento_creacion,'mmyyyy')::integer-"+mes_diez+""+anio_inicial+")))) as octubre,"
                            +"SUM((CASE WHEN fac_docs.moneda_id=1 THEN fac_docs.subtotal ELSE (fac_docs.tipo_cambio*fac_docs.subtotal) END)*(1-ABS(SIGN(to_char(fac_docs.momento_creacion,'mmyyyy')::integer-"+mes_diez+""+anio_final+")))) as octubre2,"
                            +"SUM((CASE WHEN fac_docs.moneda_id=1 THEN fac_docs.subtotal ELSE (fac_docs.tipo_cambio*fac_docs.subtotal) END)*(1-ABS(SIGN(to_char(fac_docs.momento_creacion,'mmyyyy')::integer-"+mes_once+""+anio_inicial+")))) as noviembre,"
                            +"SUM((CASE WHEN fac_docs.moneda_id=1 THEN fac_docs.subtotal ELSE (fac_docs.tipo_cambio*fac_docs.subtotal) END)*(1-ABS(SIGN(to_char(fac_docs.momento_creacion,'mmyyyy')::integer-"+mes_once+""+anio_final+")))) as noviembre2,"
                            +"SUM((CASE WHEN fac_docs.moneda_id=1 THEN fac_docs.subtotal ELSE (fac_docs.tipo_cambio*fac_docs.subtotal) END)*(1-ABS(SIGN(to_char(fac_docs.momento_creacion,'mmyyyy')::integer-"+mes_doce+""+anio_inicial+")))) as diciembre,"
                            +"SUM((CASE WHEN fac_docs.moneda_id=1 THEN fac_docs.subtotal ELSE (fac_docs.tipo_cambio*fac_docs.subtotal) END)*(1-ABS(SIGN(to_char(fac_docs.momento_creacion,'mmyyyy')::integer-"+mes_doce+""+anio_final+")))) as diciembre2,"
                            +"SUM((CASE WHEN fac_docs.moneda_id=1 THEN fac_docs.subtotal ELSE (fac_docs.tipo_cambio*fac_docs.subtotal) END)*(1-ABS(SIGN(to_char(fac_docs.momento_creacion,'mmyyyy')::integer-"+mes_uno+""+anio_inicial+"))))+"
                            +"SUM((CASE WHEN fac_docs.moneda_id=1 THEN fac_docs.subtotal ELSE (fac_docs.tipo_cambio*fac_docs.subtotal) END)*(1-ABS(SIGN(to_char(fac_docs.momento_creacion,'mmyyyy')::integer-"+mes_dos+""+anio_inicial+"))))+"
                            +"SUM((CASE WHEN fac_docs.moneda_id=1 THEN fac_docs.subtotal ELSE (fac_docs.tipo_cambio*fac_docs.subtotal) END)*(1-ABS(SIGN(to_char(fac_docs.momento_creacion,'mmyyyy')::integer-"+mes_tres+""+anio_inicial+"))))+"
                            +"SUM((CASE WHEN fac_docs.moneda_id=1 THEN fac_docs.subtotal ELSE (fac_docs.tipo_cambio*fac_docs.subtotal) END)*(1-ABS(SIGN(to_char(fac_docs.momento_creacion,'mmyyyy')::integer-"+mes_cuatro+""+anio_inicial+"))))+"
                            +"SUM((CASE WHEN fac_docs.moneda_id=1 THEN fac_docs.subtotal ELSE (fac_docs.tipo_cambio*fac_docs.subtotal) END)*(1-ABS(SIGN(to_char(fac_docs.momento_creacion,'mmyyyy')::integer-"+mes_cinco+""+anio_inicial+"))))+"
                            +"SUM((CASE WHEN fac_docs.moneda_id=1 THEN fac_docs.subtotal ELSE (fac_docs.tipo_cambio*fac_docs.subtotal) END)*(1-ABS(SIGN(to_char(fac_docs.momento_creacion,'mmyyyy')::integer-"+mes_seis+""+anio_inicial+"))))+"
                            +"SUM((CASE WHEN fac_docs.moneda_id=1 THEN fac_docs.subtotal ELSE (fac_docs.tipo_cambio*fac_docs.subtotal) END)*(1-ABS(SIGN(to_char(fac_docs.momento_creacion,'mmyyyy')::integer-"+mes_siete+""+anio_inicial+"))))+"
                            +"SUM((CASE WHEN fac_docs.moneda_id=1 THEN fac_docs.subtotal ELSE (fac_docs.tipo_cambio*fac_docs.subtotal) END)*(1-ABS(SIGN(to_char(fac_docs.momento_creacion,'mmyyyy')::integer-"+mes_ocho+""+anio_inicial+"))))+"
                            +"SUM((CASE WHEN fac_docs.moneda_id=1 THEN fac_docs.subtotal ELSE (fac_docs.tipo_cambio*fac_docs.subtotal) END)*(1-ABS(SIGN(to_char(fac_docs.momento_creacion,'mmyyyy')::integer-"+mes_nueve+""+anio_inicial+"))))+"
                            +"SUM((CASE WHEN fac_docs.moneda_id=1 THEN fac_docs.subtotal ELSE (fac_docs.tipo_cambio*fac_docs.subtotal) END)*(1-ABS(SIGN(to_char(fac_docs.momento_creacion,'mmyyyy')::integer-"+mes_diez+""+anio_inicial+"))))+"
                            +"SUM((CASE WHEN fac_docs.moneda_id=1 THEN fac_docs.subtotal ELSE (fac_docs.tipo_cambio*fac_docs.subtotal) END)*(1-ABS(SIGN(to_char(fac_docs.momento_creacion,'mmyyyy')::integer-"+mes_once+""+anio_inicial+"))))+"
                            +"SUM((CASE WHEN fac_docs.moneda_id=1 THEN fac_docs.subtotal ELSE (fac_docs.tipo_cambio*fac_docs.subtotal) END)*(1-ABS(SIGN(to_char(fac_docs.momento_creacion,'mmyyyy')::integer-"+mes_doce+""+anio_inicial+")))) as aniouno,"
                            +"SUM((CASE WHEN fac_docs.moneda_id=1 THEN fac_docs.subtotal ELSE (fac_docs.tipo_cambio*fac_docs.subtotal) END)*(1-ABS(SIGN(to_char(fac_docs.momento_creacion,'mmyyyy')::integer-"+mes_uno+""+anio_final+"))))+"
                            +"SUM((CASE WHEN fac_docs.moneda_id=1 THEN fac_docs.subtotal ELSE (fac_docs.tipo_cambio*fac_docs.subtotal) END)*(1-ABS(SIGN(to_char(fac_docs.momento_creacion,'mmyyyy')::integer-"+mes_dos+""+anio_final+"))))+"
                            +"SUM((CASE WHEN fac_docs.moneda_id=1 THEN fac_docs.subtotal ELSE (fac_docs.tipo_cambio*fac_docs.subtotal) END)*(1-ABS(SIGN(to_char(fac_docs.momento_creacion,'mmyyyy')::integer-"+mes_tres+""+anio_final+"))))+"
                            +"SUM((CASE WHEN fac_docs.moneda_id=1 THEN fac_docs.subtotal ELSE (fac_docs.tipo_cambio*fac_docs.subtotal) END)*(1-ABS(SIGN(to_char(fac_docs.momento_creacion,'mmyyyy')::integer-"+mes_cuatro+""+anio_final+"))))+"
                            +"SUM((CASE WHEN fac_docs.moneda_id=1 THEN fac_docs.subtotal ELSE (fac_docs.tipo_cambio*fac_docs.subtotal) END)*(1-ABS(SIGN(to_char(fac_docs.momento_creacion,'mmyyyy')::integer-"+mes_cinco+""+anio_final+"))))+"
                            +"SUM((CASE WHEN fac_docs.moneda_id=1 THEN fac_docs.subtotal ELSE (fac_docs.tipo_cambio*fac_docs.subtotal) END)*(1-ABS(SIGN(to_char(fac_docs.momento_creacion,'mmyyyy')::integer-"+mes_seis+""+anio_final+"))))+"
                            +"SUM((CASE WHEN fac_docs.moneda_id=1 THEN fac_docs.subtotal ELSE (fac_docs.tipo_cambio*fac_docs.subtotal) END)*(1-ABS(SIGN(to_char(fac_docs.momento_creacion,'mmyyyy')::integer-"+mes_siete+""+anio_final+"))))+"
                            +"SUM((CASE WHEN fac_docs.moneda_id=1 THEN fac_docs.subtotal ELSE (fac_docs.tipo_cambio*fac_docs.subtotal) END)*(1-ABS(SIGN(to_char(fac_docs.momento_creacion,'mmyyyy')::integer-"+mes_ocho+""+anio_final+"))))+"
                            +"SUM((CASE WHEN fac_docs.moneda_id=1 THEN fac_docs.subtotal ELSE (fac_docs.tipo_cambio*fac_docs.subtotal) END)*(1-ABS(SIGN(to_char(fac_docs.momento_creacion,'mmyyyy')::integer-"+mes_nueve+""+anio_final+"))))+"
                            +"SUM((CASE WHEN fac_docs.moneda_id=1 THEN fac_docs.subtotal ELSE (fac_docs.tipo_cambio*fac_docs.subtotal) END)*(1-ABS(SIGN(to_char(fac_docs.momento_creacion,'mmyyyy')::integer-"+mes_diez+""+anio_final+"))))+"
                            +"SUM((CASE WHEN fac_docs.moneda_id=1 THEN fac_docs.subtotal ELSE (fac_docs.tipo_cambio*fac_docs.subtotal) END)*(1-ABS(SIGN(to_char(fac_docs.momento_creacion,'mmyyyy')::integer-"+mes_once+""+anio_final+"))))+"
                            +"SUM((CASE WHEN fac_docs.moneda_id=1 THEN fac_docs.subtotal ELSE (fac_docs.tipo_cambio*fac_docs.subtotal) END)*(1-ABS(SIGN(to_char(fac_docs.momento_creacion,'mmyyyy')::integer-"+mes_doce+""+anio_final+")))) as aniodos " 
                            +"FROM fac_docs "
                            +"join cxc_clie on cxc_clie.id=fac_docs.cxc_clie_id "
                            +"JOIN erp_proceso on erp_proceso.id=fac_docs.proceso_id "
                            +"WHERE erp_proceso.empresa_id ="+id_empresa + " "
                            + "AND cxc_clie.razon_social ILIKE '%"+cliente+"%' "
                            +"AND fac_docs.cancelado=false "
                            +"AND EXTRACT(YEAR  FROM fac_docs.momento_creacion) between "+anio_inicial+ " and "+anio_final+ " "
                            +"GROUP BY razon_social ";
        //System.out.println("Generando Consulta Comparacion:"+sql_to_query+"");
        ArrayList<HashMap<String, String>> hm_facturas = (ArrayList<HashMap<String, String>>) this.jdbcTemplate.query(
            sql_to_query,
            new Object[]{}, new RowMapper(){
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    HashMap<String, String> row = new HashMap<String, String>();
                    row.put("razon_social",rs.getString("razon_social"));
                    row.put("enero",StringHelper.roundDouble(rs.getString("enero"),2));
                    row.put("febrero",StringHelper.roundDouble(rs.getString("febrero"),2));
                    row.put("marzo",StringHelper.roundDouble(rs.getString("marzo"),2));
                    row.put("abril",StringHelper.roundDouble(rs.getString("abril"),2));
                    row.put("mayo",StringHelper.roundDouble(rs.getString("mayo"),2));
                    row.put("junio",StringHelper.roundDouble(rs.getString("junio"),2));
                    row.put("julio",StringHelper.roundDouble(rs.getString("julio"),2));
                    row.put("agosto",StringHelper.roundDouble(rs.getString("agosto"),2));
                    row.put("septiembre",StringHelper.roundDouble(rs.getString("septiembre"),2));
                    row.put("octubre",StringHelper.roundDouble(rs.getString("octubre"),2));
                    row.put("noviembre",StringHelper.roundDouble(rs.getString("noviembre"),2));
                    row.put("diciembre",StringHelper.roundDouble(rs.getString("diciembre"),2));
                    row.put("aniouno",StringHelper.roundDouble(rs.getDouble("aniouno"), 2));
              
                    row.put("enero2",StringHelper.roundDouble(rs.getString("enero2"),2));
                    row.put("febrero2",StringHelper.roundDouble(rs.getString("febrero2"),2));
                    row.put("marzo2",StringHelper.roundDouble(rs.getString("marzo2"),2));
                    row.put("abril2",StringHelper.roundDouble(rs.getString("abril2"),2));
                    row.put("mayo2",StringHelper.roundDouble(rs.getString("mayo2"),2));
                    row.put("junio2",StringHelper.roundDouble(rs.getString("junio2"),2));
                    row.put("julio2",StringHelper.roundDouble(rs.getString("julio2"),2));
                    row.put("agosto2",StringHelper.roundDouble(rs.getString("agosto2"),2));
                    row.put("septiembre2",StringHelper.roundDouble(rs.getString("septiembre2"),2));
                    row.put("octubre2",StringHelper.roundDouble(rs.getString("octubre2"),2));
                    row.put("noviembre2",StringHelper.roundDouble(rs.getString("noviembre2"),2));
                    row.put("diciembre2",StringHelper.roundDouble(rs.getString("diciembre2"),2));
                    row.put("aniodos",StringHelper.roundDouble(rs.getDouble("aniodos"), 2));
                    return row;
                }
            }
        );
        return hm_facturas;
    }
    //Términa Comparativo de Ventas Anual por Cliente

    
    
    //obtiene el SQL de Comparativo anuales de ventas por Producto
    @Override
    public ArrayList<HashMap<String, String>> getComparativoVentasProducto(String producto, Integer anio_inicial, Integer anio_final,Integer tipo_producto, Integer familia,Integer subfamilia,Integer id_empresa) {
        String where="";
    

        if(tipo_producto != 0)
        
        {
            where=" AND inv_prod.tipo_de_producto_id="+tipo_producto+" ";
        }

        if(familia != 0){
            where=" AND inv_prod.inv_prod_familia_id="+familia+" ";
        }

        if(subfamilia != 0){
            where=" AND inv_prod.subfamilia_id="+subfamilia+" ";
        }

       String sql_to_query=""+"SELECT inv_prod.descripcion, "
                                +"SUM((CASE WHEN fac_docs.moneda_id=1 THEN fac_docs_detalles.cantidad*fac_docs_detalles.precio_unitario ELSE fac_docs.tipo_cambio*(fac_docs_detalles.cantidad*fac_docs_detalles.precio_unitario) END)*(1-ABS(SIGN(to_char(fac_docs.momento_creacion,'mmyyyy')::integer-"+1+""+anio_inicial+")))) as enero,"
                                +"SUM((CASE WHEN fac_docs.moneda_id=1 THEN fac_docs_detalles.cantidad*fac_docs_detalles.precio_unitario ELSE fac_docs.tipo_cambio*(fac_docs_detalles.cantidad*fac_docs_detalles.precio_unitario) END)*(1-ABS(SIGN(to_char(fac_docs.momento_creacion,'mmyyyy')::integer-"+1+""+anio_final+")))) as enero2,"
                                +"SUM((CASE WHEN fac_docs.moneda_id=1 THEN fac_docs_detalles.cantidad*fac_docs_detalles.precio_unitario ELSE fac_docs.tipo_cambio*(fac_docs_detalles.cantidad*fac_docs_detalles.precio_unitario) END)*(1-ABS(SIGN(to_char(fac_docs.momento_creacion,'mmyyyy')::integer-"+2+""+anio_inicial+")))) as febrero,"
                                +"SUM((CASE WHEN fac_docs.moneda_id=1 THEN fac_docs_detalles.cantidad*fac_docs_detalles.precio_unitario ELSE fac_docs.tipo_cambio*(fac_docs_detalles.cantidad*fac_docs_detalles.precio_unitario) END)*(1-ABS(SIGN(to_char(fac_docs.momento_creacion,'mmyyyy')::integer-"+2+""+anio_final+")))) as febrero2,"
                                +"SUM((CASE WHEN fac_docs.moneda_id=1 THEN fac_docs_detalles.cantidad*fac_docs_detalles.precio_unitario ELSE fac_docs.tipo_cambio*(fac_docs_detalles.cantidad*fac_docs_detalles.precio_unitario) END)*(1-ABS(SIGN(to_char(fac_docs.momento_creacion,'mmyyyy')::integer-"+3+""+anio_inicial+")))) as marzo,"
                                +"SUM((CASE WHEN fac_docs.moneda_id=1 THEN fac_docs_detalles.cantidad*fac_docs_detalles.precio_unitario ELSE fac_docs.tipo_cambio*(fac_docs_detalles.cantidad*fac_docs_detalles.precio_unitario) END)*(1-ABS(SIGN(to_char(fac_docs.momento_creacion,'mmyyyy')::integer-"+3+""+anio_final+")))) as marzo2,"
                                +"SUM((CASE WHEN fac_docs.moneda_id=1 THEN fac_docs_detalles.cantidad*fac_docs_detalles.precio_unitario ELSE fac_docs.tipo_cambio*(fac_docs_detalles.cantidad*fac_docs_detalles.precio_unitario) END)*(1-ABS(SIGN(to_char(fac_docs.momento_creacion,'mmyyyy')::integer-"+4+""+anio_inicial+")))) as abril,"
                                +"SUM((CASE WHEN fac_docs.moneda_id=1 THEN fac_docs_detalles.cantidad*fac_docs_detalles.precio_unitario ELSE fac_docs.tipo_cambio*(fac_docs_detalles.cantidad*fac_docs_detalles.precio_unitario) END)*(1-ABS(SIGN(to_char(fac_docs.momento_creacion,'mmyyyy')::integer-"+4+""+anio_final+")))) as abril2,"
                                +"SUM((CASE WHEN fac_docs.moneda_id=1 THEN fac_docs_detalles.cantidad*fac_docs_detalles.precio_unitario ELSE fac_docs.tipo_cambio*(fac_docs_detalles.cantidad*fac_docs_detalles.precio_unitario) END)*(1-ABS(SIGN(to_char(fac_docs.momento_creacion,'mmyyyy')::integer-"+5+""+anio_inicial+")))) as mayo,"
                                +"SUM((CASE WHEN fac_docs.moneda_id=1 THEN fac_docs_detalles.cantidad*fac_docs_detalles.precio_unitario ELSE fac_docs.tipo_cambio*(fac_docs_detalles.cantidad*fac_docs_detalles.precio_unitario) END)*(1-ABS(SIGN(to_char(fac_docs.momento_creacion,'mmyyyy')::integer-"+5+""+anio_final+")))) as mayo2, "
                                +"SUM((CASE WHEN fac_docs.moneda_id=1 THEN fac_docs_detalles.cantidad*fac_docs_detalles.precio_unitario ELSE fac_docs.tipo_cambio*(fac_docs_detalles.cantidad*fac_docs_detalles.precio_unitario) END)*(1-ABS(SIGN(to_char(fac_docs.momento_creacion,'mmyyyy')::integer-"+6+""+anio_inicial+")))) as junio,"
                                +"SUM((CASE WHEN fac_docs.moneda_id=1 THEN fac_docs_detalles.cantidad*fac_docs_detalles.precio_unitario ELSE fac_docs.tipo_cambio*(fac_docs_detalles.cantidad*fac_docs_detalles.precio_unitario) END)*(1-ABS(SIGN(to_char(fac_docs.momento_creacion,'mmyyyy')::integer-"+6+""+anio_final+")))) as junio2,"
                                +"SUM((CASE WHEN fac_docs.moneda_id=1 THEN fac_docs_detalles.cantidad*fac_docs_detalles.precio_unitario ELSE fac_docs.tipo_cambio*(fac_docs_detalles.cantidad*fac_docs_detalles.precio_unitario) END)*(1-ABS(SIGN(to_char(fac_docs.momento_creacion,'mmyyyy')::integer-"+7+""+anio_inicial+")))) as julio,"
                                +"SUM((CASE WHEN fac_docs.moneda_id=1 THEN fac_docs_detalles.cantidad*fac_docs_detalles.precio_unitario ELSE fac_docs.tipo_cambio*(fac_docs_detalles.cantidad*fac_docs_detalles.precio_unitario) END)*(1-ABS(SIGN(to_char(fac_docs.momento_creacion,'mmyyyy')::integer-"+7+""+anio_final+")))) as julio2,"
                                +"SUM((CASE WHEN fac_docs.moneda_id=1 THEN fac_docs_detalles.cantidad*fac_docs_detalles.precio_unitario ELSE fac_docs.tipo_cambio*(fac_docs_detalles.cantidad*fac_docs_detalles.precio_unitario) END)*(1-ABS(SIGN(to_char(fac_docs.momento_creacion,'mmyyyy')::integer-"+8+""+anio_inicial+")))) as agosto,"
                                +"SUM((CASE WHEN fac_docs.moneda_id=1 THEN fac_docs_detalles.cantidad*fac_docs_detalles.precio_unitario ELSE fac_docs.tipo_cambio*(fac_docs_detalles.cantidad*fac_docs_detalles.precio_unitario) END)*(1-ABS(SIGN(to_char(fac_docs.momento_creacion,'mmyyyy')::integer-"+8+""+anio_final+")))) as agosto2,"
                                +"SUM((CASE WHEN fac_docs.moneda_id=1 THEN fac_docs_detalles.cantidad*fac_docs_detalles.precio_unitario ELSE fac_docs.tipo_cambio*(fac_docs_detalles.cantidad*fac_docs_detalles.precio_unitario) END)*(1-ABS(SIGN(to_char(fac_docs.momento_creacion,'mmyyyy')::integer-"+9+""+anio_inicial+")))) as septiembre,"
                                +"SUM((CASE WHEN fac_docs.moneda_id=1 THEN fac_docs_detalles.cantidad*fac_docs_detalles.precio_unitario ELSE fac_docs.tipo_cambio*(fac_docs_detalles.cantidad*fac_docs_detalles.precio_unitario) END)*(1-ABS(SIGN(to_char(fac_docs.momento_creacion,'mmyyyy')::integer-"+9+""+anio_final+")))) as septiembre2,"
                                +"SUM((CASE WHEN fac_docs.moneda_id=1 THEN fac_docs_detalles.cantidad*fac_docs_detalles.precio_unitario ELSE fac_docs.tipo_cambio*(fac_docs_detalles.cantidad*fac_docs_detalles.precio_unitario) END)*(1-ABS(SIGN(to_char(fac_docs.momento_creacion,'mmyyyy')::integer-"+10+""+anio_inicial+")))) as octubre,"
                                +"SUM((CASE WHEN fac_docs.moneda_id=1 THEN fac_docs_detalles.cantidad*fac_docs_detalles.precio_unitario ELSE fac_docs.tipo_cambio*(fac_docs_detalles.cantidad*fac_docs_detalles.precio_unitario) END)*(1-ABS(SIGN(to_char(fac_docs.momento_creacion,'mmyyyy')::integer-"+10+""+anio_final+")))) as octubre2,"
                                +"SUM((CASE WHEN fac_docs.moneda_id=1 THEN fac_docs_detalles.cantidad*fac_docs_detalles.precio_unitario ELSE fac_docs.tipo_cambio*(fac_docs_detalles.cantidad*fac_docs_detalles.precio_unitario) END)*(1-ABS(SIGN(to_char(fac_docs.momento_creacion,'mmyyyy')::integer-"+11+""+anio_inicial+")))) as noviembre,"
                                +"SUM((CASE WHEN fac_docs.moneda_id=1 THEN fac_docs_detalles.cantidad*fac_docs_detalles.precio_unitario ELSE fac_docs.tipo_cambio*(fac_docs_detalles.cantidad*fac_docs_detalles.precio_unitario) END)*(1-ABS(SIGN(to_char(fac_docs.momento_creacion,'mmyyyy')::integer-"+11+""+anio_final+")))) as noviembre2,"
                                +"SUM((CASE WHEN fac_docs.moneda_id=1 THEN fac_docs_detalles.cantidad*fac_docs_detalles.precio_unitario ELSE fac_docs.tipo_cambio*(fac_docs_detalles.cantidad*fac_docs_detalles.precio_unitario) END)*(1-ABS(SIGN(to_char(fac_docs.momento_creacion,'mmyyyy')::integer-"+12+""+anio_inicial+")))) as diciembre,"
                                +"SUM((CASE WHEN fac_docs.moneda_id=1 THEN fac_docs_detalles.cantidad*fac_docs_detalles.precio_unitario ELSE fac_docs.tipo_cambio*(fac_docs_detalles.cantidad*fac_docs_detalles.precio_unitario) END)*(1-ABS(SIGN(to_char(fac_docs.momento_creacion,'mmyyyy')::integer-"+12+""+anio_final+")))) as diciembre2,"
                                +"SUM((CASE WHEN fac_docs.moneda_id=1 THEN fac_docs_detalles.cantidad*fac_docs_detalles.precio_unitario ELSE fac_docs.tipo_cambio*(fac_docs_detalles.cantidad*fac_docs_detalles.precio_unitario) END)*(1-ABS(SIGN(to_char(fac_docs.momento_creacion,'mmyyyy')::integer-"+1+""+anio_inicial+"))))+"
                                +"SUM((CASE WHEN fac_docs.moneda_id=1 THEN fac_docs_detalles.cantidad*fac_docs_detalles.precio_unitario ELSE fac_docs.tipo_cambio*(fac_docs_detalles.cantidad*fac_docs_detalles.precio_unitario) END)*(1-ABS(SIGN(to_char(fac_docs.momento_creacion,'mmyyyy')::integer-"+2+""+anio_inicial+"))))+"
                                +"SUM((CASE WHEN fac_docs.moneda_id=1 THEN fac_docs_detalles.cantidad*fac_docs_detalles.precio_unitario ELSE fac_docs.tipo_cambio*(fac_docs_detalles.cantidad*fac_docs_detalles.precio_unitario) END)*(1-ABS(SIGN(to_char(fac_docs.momento_creacion,'mmyyyy')::integer-"+3+""+anio_inicial+"))))+"
                                +"SUM((CASE WHEN fac_docs.moneda_id=1 THEN fac_docs_detalles.cantidad*fac_docs_detalles.precio_unitario ELSE fac_docs.tipo_cambio*(fac_docs_detalles.cantidad*fac_docs_detalles.precio_unitario) END)*(1-ABS(SIGN(to_char(fac_docs.momento_creacion,'mmyyyy')::integer-"+4+""+anio_inicial+"))))+"
                                +"SUM((CASE WHEN fac_docs.moneda_id=1 THEN fac_docs_detalles.cantidad*fac_docs_detalles.precio_unitario ELSE fac_docs.tipo_cambio*(fac_docs_detalles.cantidad*fac_docs_detalles.precio_unitario) END)*(1-ABS(SIGN(to_char(fac_docs.momento_creacion,'mmyyyy')::integer-"+5+""+anio_inicial+"))))+"
                                +"SUM((CASE WHEN fac_docs.moneda_id=1 THEN fac_docs_detalles.cantidad*fac_docs_detalles.precio_unitario ELSE fac_docs.tipo_cambio*(fac_docs_detalles.cantidad*fac_docs_detalles.precio_unitario) END)*(1-ABS(SIGN(to_char(fac_docs.momento_creacion,'mmyyyy')::integer-"+6+""+anio_inicial+"))))+"
                                +"SUM((CASE WHEN fac_docs.moneda_id=1 THEN fac_docs_detalles.cantidad*fac_docs_detalles.precio_unitario ELSE fac_docs.tipo_cambio*(fac_docs_detalles.cantidad*fac_docs_detalles.precio_unitario) END)*(1-ABS(SIGN(to_char(fac_docs.momento_creacion,'mmyyyy')::integer-"+7+""+anio_inicial+"))))+"
                                +"SUM((CASE WHEN fac_docs.moneda_id=1 THEN fac_docs_detalles.cantidad*fac_docs_detalles.precio_unitario ELSE fac_docs.tipo_cambio*(fac_docs_detalles.cantidad*fac_docs_detalles.precio_unitario) END)*(1-ABS(SIGN(to_char(fac_docs.momento_creacion,'mmyyyy')::integer-"+8+""+anio_inicial+"))))+"
                                +"SUM((CASE WHEN fac_docs.moneda_id=1 THEN fac_docs_detalles.cantidad*fac_docs_detalles.precio_unitario ELSE fac_docs.tipo_cambio*(fac_docs_detalles.cantidad*fac_docs_detalles.precio_unitario) END)*(1-ABS(SIGN(to_char(fac_docs.momento_creacion,'mmyyyy')::integer-"+9+""+anio_inicial+"))))+"
                                +"SUM((CASE WHEN fac_docs.moneda_id=1 THEN fac_docs_detalles.cantidad*fac_docs_detalles.precio_unitario ELSE fac_docs.tipo_cambio*(fac_docs_detalles.cantidad*fac_docs_detalles.precio_unitario) END)*(1-ABS(SIGN(to_char(fac_docs.momento_creacion,'mmyyyy')::integer-"+10+""+anio_inicial+"))))+"
                                +"SUM((CASE WHEN fac_docs.moneda_id=1 THEN fac_docs_detalles.cantidad*fac_docs_detalles.precio_unitario ELSE fac_docs.tipo_cambio*(fac_docs_detalles.cantidad*fac_docs_detalles.precio_unitario) END)*(1-ABS(SIGN(to_char(fac_docs.momento_creacion,'mmyyyy')::integer-"+11+""+anio_inicial+"))))+"
                                +"SUM((CASE WHEN fac_docs.moneda_id=1 THEN fac_docs_detalles.cantidad*fac_docs_detalles.precio_unitario ELSE fac_docs.tipo_cambio*(fac_docs_detalles.cantidad*fac_docs_detalles.precio_unitario) END)*(1-ABS(SIGN(to_char(fac_docs.momento_creacion,'mmyyyy')::integer-"+12+""+anio_inicial+")))) as aniouno,"
                                +"SUM((CASE WHEN fac_docs.moneda_id=1 THEN fac_docs_detalles.cantidad*fac_docs_detalles.precio_unitario ELSE fac_docs.tipo_cambio*(fac_docs_detalles.cantidad*fac_docs_detalles.precio_unitario) END)*(1-ABS(SIGN(to_char(fac_docs.momento_creacion,'mmyyyy')::integer-"+1+""+anio_final+"))))+"
                                +"SUM((CASE WHEN fac_docs.moneda_id=1 THEN fac_docs_detalles.cantidad*fac_docs_detalles.precio_unitario ELSE fac_docs.tipo_cambio*(fac_docs_detalles.cantidad*fac_docs_detalles.precio_unitario) END)*(1-ABS(SIGN(to_char(fac_docs.momento_creacion,'mmyyyy')::integer-"+2+""+anio_final+"))))+"
                                +"SUM((CASE WHEN fac_docs.moneda_id=1 THEN fac_docs_detalles.cantidad*fac_docs_detalles.precio_unitario ELSE fac_docs.tipo_cambio*(fac_docs_detalles.cantidad*fac_docs_detalles.precio_unitario) END)*(1-ABS(SIGN(to_char(fac_docs.momento_creacion,'mmyyyy')::integer-"+3+""+anio_final+"))))+"
                                +"SUM((CASE WHEN fac_docs.moneda_id=1 THEN fac_docs_detalles.cantidad*fac_docs_detalles.precio_unitario ELSE fac_docs.tipo_cambio*(fac_docs_detalles.cantidad*fac_docs_detalles.precio_unitario) END)*(1-ABS(SIGN(to_char(fac_docs.momento_creacion,'mmyyyy')::integer-"+4+""+anio_final+"))))+"
                                +"SUM((CASE WHEN fac_docs.moneda_id=1 THEN fac_docs_detalles.cantidad*fac_docs_detalles.precio_unitario ELSE fac_docs.tipo_cambio*(fac_docs_detalles.cantidad*fac_docs_detalles.precio_unitario) END)*(1-ABS(SIGN(to_char(fac_docs.momento_creacion,'mmyyyy')::integer-"+5+""+anio_final+"))))+"
                                +"SUM((CASE WHEN fac_docs.moneda_id=1 THEN fac_docs_detalles.cantidad*fac_docs_detalles.precio_unitario ELSE fac_docs.tipo_cambio*(fac_docs_detalles.cantidad*fac_docs_detalles.precio_unitario) END)*(1-ABS(SIGN(to_char(fac_docs.momento_creacion,'mmyyyy')::integer-"+6+""+anio_final+"))))+"
                                +"SUM((CASE WHEN fac_docs.moneda_id=1 THEN fac_docs_detalles.cantidad*fac_docs_detalles.precio_unitario ELSE fac_docs.tipo_cambio*(fac_docs_detalles.cantidad*fac_docs_detalles.precio_unitario) END)*(1-ABS(SIGN(to_char(fac_docs.momento_creacion,'mmyyyy')::integer-"+7+""+anio_final+"))))+"
                                +"SUM((CASE WHEN fac_docs.moneda_id=1 THEN fac_docs_detalles.cantidad*fac_docs_detalles.precio_unitario ELSE fac_docs.tipo_cambio*(fac_docs_detalles.cantidad*fac_docs_detalles.precio_unitario) END)*(1-ABS(SIGN(to_char(fac_docs.momento_creacion,'mmyyyy')::integer-"+8+""+anio_final+"))))+"
                                +"SUM((CASE WHEN fac_docs.moneda_id=1 THEN fac_docs_detalles.cantidad*fac_docs_detalles.precio_unitario ELSE fac_docs.tipo_cambio*(fac_docs_detalles.cantidad*fac_docs_detalles.precio_unitario) END)*(1-ABS(SIGN(to_char(fac_docs.momento_creacion,'mmyyyy')::integer-"+9+""+anio_final+"))))+"
                                +"SUM((CASE WHEN fac_docs.moneda_id=1 THEN fac_docs_detalles.cantidad*fac_docs_detalles.precio_unitario ELSE fac_docs.tipo_cambio*(fac_docs_detalles.cantidad*fac_docs_detalles.precio_unitario) END)*(1-ABS(SIGN(to_char(fac_docs.momento_creacion,'mmyyyy')::integer-"+10+""+anio_final+"))))+"
                                +"SUM((CASE WHEN fac_docs.moneda_id=1 THEN fac_docs_detalles.cantidad*fac_docs_detalles.precio_unitario ELSE fac_docs.tipo_cambio*(fac_docs_detalles.cantidad*fac_docs_detalles.precio_unitario) END)*(1-ABS(SIGN(to_char(fac_docs.momento_creacion,'mmyyyy')::integer-"+11+""+anio_final+"))))+"
                                +"SUM((CASE WHEN fac_docs.moneda_id=1 THEN fac_docs_detalles.cantidad*fac_docs_detalles.precio_unitario ELSE fac_docs.tipo_cambio*(fac_docs_detalles.cantidad*fac_docs_detalles.precio_unitario) END)*(1-ABS(SIGN(to_char(fac_docs.momento_creacion,'mmyyyy')::integer-"+12+""+anio_final+")))) as aniodos "
                                +"FROM fac_docs "
                                +"join cxc_clie on cxc_clie.id=fac_docs.cxc_clie_id "
                                +"JOIN erp_proceso on erp_proceso.id=fac_docs.proceso_id "
                                +"JOIN fac_docs_detalles on fac_docs_detalles.fac_doc_id=fac_docs.id "
                                +"LEFT JOIN inv_prod on inv_prod.id= fac_docs_detalles.inv_prod_id "
                                +"LEFT JOIN inv_prod_familias on inv_prod_familias.id=inv_prod.inv_prod_familia_id "
                                +"WHERE erp_proceso.empresa_id ="+id_empresa + " "
                                +"AND inv_prod.descripcion ILIKE '%"+producto+"%' "
                                //+ "AND cxc_clie.razon_social ILIKE '%"+cliente+"%' "
                                +"AND fac_docs.cancelado=false "
                                +"AND EXTRACT(YEAR  FROM fac_docs.momento_creacion) between "+anio_inicial+ " and "+anio_final+ " "
                                //+ "AND EXTRACT(YEAR  FROM fac_docs.momento_creacion)=EXTRACT(YEAR FROM now()) "
                                +where+" "
                                +"GROUP BY inv_prod.descripcion ";

        System.out.println("Generando Consulta Comparativoproducto:"+sql_to_query+"");

        ArrayList<HashMap<String, String>> hm_facturas = (ArrayList<HashMap<String, String>>) this.jdbcTemplate.query(
            sql_to_query,
            new Object[]{}, new RowMapper(){
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    HashMap<String, String> row = new HashMap<String, String>();
                    row.put("descripcion",rs.getString("descripcion"));
                    row.put("enero",StringHelper.roundDouble(rs.getString("enero"),2));
                    row.put("febrero",StringHelper.roundDouble(rs.getString("febrero"),2));
                    row.put("marzo",StringHelper.roundDouble(rs.getString("marzo"),2));
                    row.put("abril",StringHelper.roundDouble(rs.getString("abril"),2));
                    row.put("mayo",StringHelper.roundDouble(rs.getString("mayo"),2));
                    row.put("junio",StringHelper.roundDouble(rs.getString("junio"),2));
                    row.put("julio",StringHelper.roundDouble(rs.getString("julio"),2));
                    row.put("agosto",StringHelper.roundDouble(rs.getString("agosto"),2));
                    row.put("septiembre",StringHelper.roundDouble(rs.getString("septiembre"),2));
                    row.put("octubre",StringHelper.roundDouble(rs.getString("octubre"),2));
                    row.put("noviembre",StringHelper.roundDouble(rs.getString("noviembre"),2));
                    row.put("diciembre",StringHelper.roundDouble(rs.getString("diciembre"),2));
                    row.put("aniouno",StringHelper.roundDouble(rs.getDouble("aniouno"), 2));
              
                    row.put("enero2",StringHelper.roundDouble(rs.getString("enero2"),2));
                    row.put("febrero2",StringHelper.roundDouble(rs.getString("febrero2"),2));
                    row.put("marzo2",StringHelper.roundDouble(rs.getString("marzo2"),2));
                    row.put("abril2",StringHelper.roundDouble(rs.getString("abril2"),2));
                    row.put("mayo2",StringHelper.roundDouble(rs.getString("mayo2"),2));
                    row.put("junio2",StringHelper.roundDouble(rs.getString("junio2"),2));
                    row.put("julio2",StringHelper.roundDouble(rs.getString("julio2"),2));
                    row.put("agosto2",StringHelper.roundDouble(rs.getString("agosto2"),2));
                    row.put("septiembre2",StringHelper.roundDouble(rs.getString("septiembre2"),2));
                    row.put("octubre2",StringHelper.roundDouble(rs.getString("octubre2"),2));
                    row.put("noviembre2",StringHelper.roundDouble(rs.getString("noviembre2"),2));
                    row.put("diciembre2",StringHelper.roundDouble(rs.getString("diciembre2"),2));
                    row.put("aniodos",StringHelper.roundDouble(rs.getDouble("aniodos"), 2));

                    return row;
                }
            }
        );
        return hm_facturas;
    }
    
    // obtiene el SQL de estadisticas anuales de ventas
    @Override
    public ArrayList<HashMap<String, String>> getEstadisticaVentasProducto(Integer mes_in,Integer mes_fin,Integer tipo_producto, Integer familia,Integer subfamilia,Integer id_empresa, Integer anio) {
        String where="";

        if(tipo_producto != 0){
            where=" AND inv_prod.tipo_de_producto_id="+tipo_producto+" ";
        }

        if(familia != 0){
            where=" AND inv_prod.inv_prod_familia_id="+familia+" ";
        }

        if(subfamilia != 0){
            where=" AND inv_prod.subfamilia_id="+subfamilia+" ";
        }

       String sql_to_query=""+"SELECT descripcion, "
                                    +"sum(enero) as enero, "
                                    +"sum(febrero) as febrero, "
                                    +"sum(marzo) as marzo, "
                                    +"sum(abril) as abril, "
                                    +"sum(mayo) as mayo, "
                                    +"sum(junio) as junio, "
                                    +"sum(julio) as julio, "
                                    +"sum(agosto) as agosto, "
                                    +"sum(septiembre) as septiembre, "
                                    +"sum(octubre) as octubre, "
                                    +"sum(noviembre) as noviembre, "
                                    +"sum(diciembre) as diciembre, "
                                    +"sum(enero)+sum(febrero)+sum(marzo)+sum(abril)+sum(mayo)+sum(junio)+sum(julio)+sum(agosto)+sum(septiembre)+sum(octubre)+sum(noviembre)+sum(diciembre)  as suma_total "
                            +"FROM( SELECT descripcion, "
                                        +"(CASE WHEN mes=1 THEN subtotal ELSE 0 END) AS enero, "
                                        +"(CASE WHEN mes=2 THEN subtotal ELSE 0 END) AS febrero, "
                                        +"(CASE WHEN mes=3 THEN subtotal ELSE 0 END) AS marzo, "
                                        +"(CASE WHEN mes=4 THEN subtotal ELSE 0 END) AS abril, "
                                        +"(CASE WHEN mes=5 THEN subtotal ELSE 0 END) AS mayo, "
                                        +"(CASE WHEN mes=6 THEN subtotal ELSE 0 END) AS junio, "
                                        +"(CASE WHEN mes=7 THEN subtotal ELSE 0 END) AS julio, "
                                        +"(CASE WHEN mes=8 THEN subtotal ELSE 0 END) AS agosto, "
                                        +"(CASE WHEN mes=9 THEN subtotal ELSE 0 END) AS septiembre, "
                                        +"(CASE WHEN mes=10 THEN subtotal ELSE 0 END) AS octubre, "
                                        +"(CASE WHEN mes=11 THEN subtotal ELSE 0 END) AS noviembre, "
                                        +"(CASE WHEN mes=12 THEN subtotal ELSE 0 END) AS diciembre, "
                                        +"subtotal "
                                    +"FROM ("
                                            +"SELECT inv_prod.descripcion, "
                                                +"EXTRACT(MONTH FROM fac_docs.momento_creacion) as mes, "
                                                +"(CASE WHEN fac_docs.moneda_id=1 THEN fac_docs_detalles.cantidad*fac_docs_detalles.precio_unitario ELSE fac_docs.tipo_cambio*(fac_docs_detalles.cantidad*fac_docs_detalles.precio_unitario) END) as subtotal  "
                                            +"FROM fac_docs "
                                            +"join cxc_clie on cxc_clie.id=fac_docs.cxc_clie_id "
                                            +"JOIN erp_proceso on erp_proceso.id=fac_docs.proceso_id "
                                            +"JOIN fac_docs_detalles on fac_docs_detalles.fac_doc_id=fac_docs.id "
                                            +"LEFT JOIN inv_prod on inv_prod.id= fac_docs_detalles.inv_prod_id "
                                            +"LEFT JOIN inv_prod_familias on inv_prod_familias.id=inv_prod.inv_prod_familia_id "
                                            +"WHERE erp_proceso.empresa_id ="+id_empresa + " "
                                            +"AND fac_docs.cancelado=false "
                                            +"AND EXTRACT(MONTH FROM fac_docs.momento_creacion) between "+mes_in+ " and "+mes_fin+ " "
                                            + "AND EXTRACT(YEAR  FROM fac_docs.momento_creacion)="+anio+" "
                                            //+ "AND EXTRACT(YEAR  FROM fac_docs.momento_creacion)=EXTRACT(YEAR FROM now()) "
                                            +where+" "
                                    +") AS sbt "
                            +")as sbt2 "
                            +"GROUP BY descripcion ";

        //System.out.println("Generando Consulta Estadisticas:"+sql_to_query+"");

        ArrayList<HashMap<String, String>> hm_facturas = (ArrayList<HashMap<String, String>>) this.jdbcTemplate.query(
            sql_to_query,
            new Object[]{}, new RowMapper(){
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    HashMap<String, String> row = new HashMap<String, String>();
                    row.put("descripcion",rs.getString("descripcion"));
                    row.put("enero",StringHelper.roundDouble(rs.getString("enero"),2));
                    row.put("febrero",StringHelper.roundDouble(rs.getString("febrero"),2));
                    row.put("marzo",StringHelper.roundDouble(rs.getString("marzo"),2));
                    row.put("abril",StringHelper.roundDouble(rs.getString("abril"),2));
                    row.put("mayo",StringHelper.roundDouble(rs.getString("mayo"),2));
                    row.put("junio",StringHelper.roundDouble(rs.getString("junio"),2));
                    row.put("julio",StringHelper.roundDouble(rs.getString("julio"),2));
                    row.put("agosto",StringHelper.roundDouble(rs.getString("agosto"),2));
                    row.put("septiembre",StringHelper.roundDouble(rs.getString("septiembre"),2));
                    row.put("octubre",StringHelper.roundDouble(rs.getString("octubre"),2));
                    row.put("noviembre",StringHelper.roundDouble(rs.getString("noviembre"),2));
                    row.put("diciembre",StringHelper.roundDouble(rs.getString("diciembre"),2));
                    row.put("suma_total",StringHelper.roundDouble(rs.getDouble("suma_total"), 2));

                    return row;
                }
            }
        );
        return hm_facturas;
    }

   
    
    
   //alimenta al select de familias

    @Override
    public ArrayList<HashMap<String, String>> getFamilias(Integer tipo_producto,Integer id_empresa){
        String sql_to_query =""
                + "SELECT "
                    + "inv_prod_familias.id, "
                    + "inv_prod_familias.descripcion "
                +"FROM inv_prod_familias "
                +"WHERE inv_prod_familias.inv_prod_tipo_id="+tipo_producto+" "
                + "AND inv_prod_familias.gral_emp_id="+id_empresa+" "
                + "AND inv_prod_familias.id=inv_prod_familias.identificador_familia_padre ";

        ArrayList<HashMap<String,String>> html_familia =(ArrayList<HashMap<String,String>>) this.jdbcTemplate.query(
            sql_to_query,new Object[]{},new RowMapper(){
             @Override
             public Object mapRow(ResultSet rs,int rowNum) throws SQLException{
                 HashMap<String, String> row = new HashMap<String, String>();
                    row.put("id",String.valueOf(rs.getInt("id")));
                    row.put("descripcion",String.valueOf(rs.getString("descripcion")));
                    return row;
             }
            }
        );

        return html_familia;
    }

    @Override
    public ArrayList<HashMap<String, String>> getSubFamilias(Integer familia_id){
        String sql_to_query =""
                + "SELECT "
                    + "inv_prod_familias.id, "
                    + "inv_prod_familias.descripcion "
                +"FROM inv_prod_familias "
                +"WHERE inv_prod_familias.identificador_familia_padre="+familia_id+" "
                +"AND inv_prod_familias.id != inv_prod_familias.identificador_familia_padre;";

        ArrayList<HashMap<String,String>> html_subfamilia =(ArrayList<HashMap<String,String>>) this.jdbcTemplate.query(
            sql_to_query,new Object[]{},new RowMapper(){
             @Override
             public Object mapRow(ResultSet rs,int rowNum) throws SQLException{
                 HashMap<String, String> row = new HashMap<String, String>();
                    row.put("id",String.valueOf(rs.getInt("id")));
                    row.put("descripcion",String.valueOf(rs.getString("descripcion")));
                    return row;
             }
            }
        );

        return html_subfamilia;

    }


 // obtiene el SQL de estadisticas anuales de ventas por unidades
    @Override
    public ArrayList<HashMap<String, String>> getEstadisticaVentasUnidades(Integer mes_in,Integer mes_fin,Integer tipo_producto, Integer familia,Integer subfamilia,Integer id_empresa, Integer anio) {
        String where="";

        if(tipo_producto != 0){
            where=" AND inv_prod.tipo_de_producto_id="+tipo_producto+" ";
        }

        if(familia != 0){
            where=" AND inv_prod.inv_prod_familia_id="+familia+" ";
        }

        if(subfamilia != 0){
            where=" AND inv_prod.subfamilia_id="+subfamilia+" ";
        }

       String sql_to_query=""+"SELECT descripcion, "
                                    +"sum(enero) as enero, "
                                    +"sum(febrero) as febrero, "
                                    +"sum(marzo) as marzo, "
                                    +"sum(abril) as abril, "
                                    +"sum(mayo) as mayo, "
                                    +"sum(junio) as junio, "
                                    +"sum(julio) as julio, "
                                    +"sum(agosto) as agosto, "
                                    +"sum(septiembre) as septiembre, "
                                    +"sum(octubre) as octubre, "
                                    +"sum(noviembre) as noviembre, "
                                    +"sum(diciembre) as diciembre, "
                                    +"sum(enero)+sum(febrero)+sum(marzo)+sum(abril)+sum(mayo)+sum(junio)+sum(julio)+sum(agosto)+sum(septiembre)+sum(octubre)+sum(noviembre)+sum(diciembre)  as suma_total "
                            +"FROM( SELECT descripcion, "
                                        +"(CASE WHEN mes=1 THEN subtotal ELSE 0 END) AS enero, "
                                        +"(CASE WHEN mes=2 THEN subtotal ELSE 0 END) AS febrero, "
                                        +"(CASE WHEN mes=3 THEN subtotal ELSE 0 END) AS marzo, "
                                        +"(CASE WHEN mes=4 THEN subtotal ELSE 0 END) AS abril, "
                                        +"(CASE WHEN mes=5 THEN subtotal ELSE 0 END) AS mayo, "
                                        +"(CASE WHEN mes=6 THEN subtotal ELSE 0 END) AS junio, "
                                        +"(CASE WHEN mes=7 THEN subtotal ELSE 0 END) AS julio, "
                                        +"(CASE WHEN mes=8 THEN subtotal ELSE 0 END) AS agosto, "
                                        +"(CASE WHEN mes=9 THEN subtotal ELSE 0 END) AS septiembre, "
                                        +"(CASE WHEN mes=10 THEN subtotal ELSE 0 END) AS octubre, "
                                        +"(CASE WHEN mes=11 THEN subtotal ELSE 0 END) AS noviembre, "
                                        +"(CASE WHEN mes=12 THEN subtotal ELSE 0 END) AS diciembre, "
                                        +"subtotal "
                                    +"FROM ("
                                            +"SELECT inv_prod.descripcion, "
                                                +"EXTRACT(MONTH FROM fac_docs.momento_creacion) as mes, "
                                                +"(CASE WHEN fac_docs.moneda_id=1 THEN fac_docs_detalles.cantidad*1 ELSE (fac_docs_detalles.cantidad*1) END) as subtotal  "
                                            +"FROM fac_docs "
                                            +"join cxc_clie on cxc_clie.id=fac_docs.cxc_clie_id "
                                            +"JOIN erp_proceso on erp_proceso.id=fac_docs.proceso_id "
                                            +"JOIN fac_docs_detalles on fac_docs_detalles.fac_doc_id=fac_docs.id "
                                            +"LEFT JOIN inv_prod on inv_prod.id= fac_docs_detalles.inv_prod_id "
                                            +"LEFT JOIN inv_prod_familias on inv_prod_familias.id=inv_prod.inv_prod_familia_id "
                                            +"WHERE erp_proceso.empresa_id ="+id_empresa + " "
                                            +"AND fac_docs.cancelado=false "
                                            +"AND EXTRACT(MONTH FROM fac_docs.momento_creacion) between "+mes_in+ " and "+mes_fin+ " "
                                            + "AND EXTRACT(YEAR  FROM fac_docs.momento_creacion)="+anio+" "
                                            //+ "AND EXTRACT(YEAR  FROM fac_docs.momento_creacion)=EXTRACT(YEAR FROM now()) "
                                            +where+" "
                                    +") AS sbt "
                            +")as sbt2 "
                            +"GROUP BY descripcion ORDER BY descripcion";

        //System.out.println("Generando Consulta Estadisticas:"+sql_to_query+"");

        ArrayList<HashMap<String, String>> hm_facturas = (ArrayList<HashMap<String, String>>) this.jdbcTemplate.query(
            sql_to_query,
            new Object[]{}, new RowMapper(){
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    HashMap<String, String> row = new HashMap<String, String>();
                    row.put("descripcion",rs.getString("descripcion"));
                    row.put("enero",StringHelper.roundDouble(rs.getString("enero"),2));
                    row.put("febrero",StringHelper.roundDouble(rs.getString("febrero"),2));
                    row.put("marzo",StringHelper.roundDouble(rs.getString("marzo"),2));
                    row.put("abril",StringHelper.roundDouble(rs.getString("abril"),2));
                    row.put("mayo",StringHelper.roundDouble(rs.getString("mayo"),2));
                    row.put("junio",StringHelper.roundDouble(rs.getString("junio"),2));
                    row.put("julio",StringHelper.roundDouble(rs.getString("julio"),2));
                    row.put("agosto",StringHelper.roundDouble(rs.getString("agosto"),2));
                    row.put("septiembre",StringHelper.roundDouble(rs.getString("septiembre"),2));
                    row.put("octubre",StringHelper.roundDouble(rs.getString("octubre"),2));
                    row.put("noviembre",StringHelper.roundDouble(rs.getString("noviembre"),2));
                    row.put("diciembre",StringHelper.roundDouble(rs.getString("diciembre"),2));
                    row.put("suma_total",StringHelper.roundDouble(rs.getDouble("suma_total"), 2));

                    return row;
                }
            }
        );
        return hm_facturas;
    }

     //alimenta al select de familias

    @Override
    public ArrayList<HashMap<String, String>> getFamilias2(Integer tipo_producto,Integer id_empresa){
        String sql_to_query =""
                + "SELECT "
                    + "inv_prod_familias.id, "
                    + "inv_prod_familias.descripcion "
                +"FROM inv_prod_familias "
                +"WHERE inv_prod_familias.inv_prod_tipo_id="+tipo_producto+" "
                + "AND inv_prod_familias.gral_emp_id="+id_empresa+" "
                + "AND inv_prod_familias.id=inv_prod_familias.identificador_familia_padre ";

        ArrayList<HashMap<String,String>> html_familia2 =(ArrayList<HashMap<String,String>>) this.jdbcTemplate.query(
            sql_to_query,new Object[]{},new RowMapper(){
             @Override
             public Object mapRow(ResultSet rs,int rowNum) throws SQLException{
                 HashMap<String, String> row = new HashMap<String, String>();
                    row.put("id",String.valueOf(rs.getInt("id")));
                    row.put("descripcion",String.valueOf(rs.getString("descripcion")));
                    return row;
             }
            }
        );

        return html_familia2;
    }

    @Override
    public ArrayList<HashMap<String, String>> getSubFamilias2(Integer familia_id){
        String sql_to_query =""
                + "SELECT "
                    + "inv_prod_familias.id, "
                    + "inv_prod_familias.descripcion "
                +"FROM inv_prod_familias "
                +"WHERE inv_prod_familias.identificador_familia_padre="+familia_id+" "
                +"AND inv_prod_familias.id != inv_prod_familias.identificador_familia_padre;";

        ArrayList<HashMap<String,String>> html_subfamilia2 =(ArrayList<HashMap<String,String>>) this.jdbcTemplate.query(
            sql_to_query,new Object[]{},new RowMapper(){
             @Override
             public Object mapRow(ResultSet rs,int rowNum) throws SQLException{
                 HashMap<String, String> row = new HashMap<String, String>();
                    row.put("id",String.valueOf(rs.getInt("id")));
                    row.put("descripcion",String.valueOf(rs.getString("descripcion")));
                    return row;
             }
            }
        );

        return html_subfamilia2;

    }

    
    
    





    //alimenta al grid de facturas a revision
    @Override
    public ArrayList<HashMap<String,String>> getProgramacionPagos_FacturasRevision(Integer id_cliente,String fecha,Integer id_empresa){
        String where = "";
	if(id_cliente!=0){
		where=" AND cxc_clie.id="+id_cliente;
	}

        String sql_to_query=""
                + "SELECT "
                    + "erp_h_facturas.id AS id_erp_h_fac,"
                    + "erp_h_facturas.serie_folio as factura, "
                    + "to_char(erp_h_facturas.momento_facturacion,'dd/mm/yyyy') as fecha_factura, "
                    + "cxc_clie.razon_social AS cliente, "
                    + "erp_h_facturas.saldo_factura,"
                    + "'R'::character(1) AS revision_cobro "
                + "FROM cxc_clie "
                + "join erp_h_facturas on erp_h_facturas.cliente_id = cxc_clie.id "
                + "WHERE erp_h_facturas.empresa_id="+id_empresa+" "
                + "AND erp_h_facturas.saldo_factura > 0 "
                + "AND erp_h_facturas.estatus_revision=0 "
                + "AND erp_h_facturas.cancelacion=false "
                + "AND cxc_clie.dia_revision = to_char('"+fecha+"'::DATE,'d')::smallint "+where+" "
                + "ORDER BY cxc_clie.razon_social, erp_h_facturas.momento_facturacion ";

        System.out.println("Obtiene fac revision:"+sql_to_query);

        ArrayList<HashMap<String,String>> facturas =(ArrayList<HashMap<String,String>>) this.jdbcTemplate.query(
            sql_to_query,new Object[]{},new RowMapper(){
             @Override
             public Object mapRow(ResultSet rs,int rowNum) throws SQLException{
                 HashMap<String, String> row = new HashMap<String, String>();
                    row.put("id_erp_h_fac",String.valueOf(rs.getInt("id_erp_h_fac")));
                    row.put("factura",rs.getString("factura"));
                    row.put("fecha_factura",rs.getString("fecha_factura"));
                    row.put("cliente",rs.getString("cliente"));
                    row.put("saldo_factura",StringHelper.roundDouble(rs.getString("saldo_factura"),2));
                    row.put("revision_cobro",rs.getString("revision_cobro"));
                    return row;
             }
            }
        );

        return facturas;
    };



    @Override
    public ArrayList<HashMap<String, String>> getProgramacionPagos_FacturasCobro(Integer id_cliente, String fecha, Integer id_empresa) {
        String where = "";
	if(id_cliente != 0){
		where=" AND cxc_clie.id="+id_cliente;
	}

        String sql_to_query=""
                + "SELECT "
                    + "erp_h_facturas.id AS id_erp_h_fac,"
                    + "erp_h_facturas.serie_folio as factura, "
                    + "to_char(erp_h_facturas.momento_facturacion,'dd/mm/yyyy') as fecha_factura, "
                    + "cxc_clie.razon_social AS cliente, "
                    + "erp_h_facturas.saldo_factura,"
                    + "'C'::character(1) AS revision_cobro "
                + "FROM cxc_clie "
                + "join erp_h_facturas on erp_h_facturas.cliente_id = cxc_clie.id "
                + "WHERE erp_h_facturas.empresa_id="+id_empresa+" "
                + "AND erp_h_facturas.saldo_factura > 0 "
                + "AND erp_h_facturas.estatus_revision=2 "
                + "AND cxc_clie.dia_pago = to_char('"+fecha+"'::DATE,'d')::smallint "+where+" "
                + "AND to_char(erp_h_facturas.fecha_vencimiento, 'yyyymmdd')::integer <= to_char('"+fecha+"'::timestamp with time zone,'yyyymmdd')::integer "
                + "ORDER BY cxc_clie.razon_social, erp_h_facturas.momento_facturacion ";

        System.out.println("Obtiene fac cobro:"+sql_to_query);

        ArrayList<HashMap<String,String>> facturas =(ArrayList<HashMap<String,String>>) this.jdbcTemplate.query(
            sql_to_query,new Object[]{},new RowMapper(){
             @Override
             public Object mapRow(ResultSet rs,int rowNum) throws SQLException{
                 HashMap<String, String> row = new HashMap<String, String>();
                    row.put("id_erp_h_fac",String.valueOf(rs.getInt("id_erp_h_fac")));
                    row.put("factura",rs.getString("factura"));
                    row.put("fecha_factura",rs.getString("fecha_factura"));
                    row.put("cliente",rs.getString("cliente"));
                    row.put("saldo_factura",StringHelper.roundDouble(rs.getString("saldo_factura"),2));
                    row.put("revision_cobro",rs.getString("revision_cobro"));
                    return row;
             }
            }
        );

        return facturas;
    }




    @Override
    public ArrayList<HashMap<String, Object>> getProgramacionPagos_PaginaGrid(String data_string, int offset, int pageSize, String orderBy, String asc) {
        String sql_busqueda = "select id from gral_bus_catalogos(?) as foo (id integer)";

	String sql_to_query = ""
                + "SELECT distinct cxc_fac_rev_cob.id, "
                        + "cxc_fac_rev_cob.folio, "
                        + "to_char(cxc_fac_rev_cob.fecha_proceso,'dd/mm/yyyy') AS fecha "
                        + "FROM cxc_fac_rev_cob "
                +"JOIN ("+sql_busqueda+") AS sbt ON sbt.id = cxc_fac_rev_cob.id "
                +"order by "+orderBy+" "+asc+" limit ? OFFSET ?";

        //System.out.println("Busqueda GetPage: "+sql_to_query);
        ArrayList<HashMap<String, Object>> hm = (ArrayList<HashMap<String, Object>>) this.jdbcTemplate.query(
            sql_to_query,
            new Object[]{new String(data_string), new Integer(pageSize),new Integer(offset)}, new RowMapper() {
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    HashMap<String, Object> row = new HashMap<String, Object>();
                    row.put("id",rs.getInt("id"));
                    row.put("folio",rs.getString("folio"));
                    row.put("fecha",rs.getString("fecha"));
                    return row;
                }
            }
        );
        return hm;
    }


    @Override
    public ArrayList<HashMap<String, String>> getProgramacionPagos_Datos(Integer id) {
        String sql_query = ""
                + "SELECT "
                    + "id,"
                    + "folio, "
                    + "to_char(fecha_proceso,'yyyy-mm-dd') as fecha_proceso "
                + "FROM cxc_fac_rev_cob WHERE id =" +id;

        System.out.print(sql_query);

        ArrayList<HashMap<String, String>> hm = (ArrayList<HashMap<String, String>>) this.jdbcTemplate.query(
            sql_query,
            new Object[]{}, new RowMapper() {
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    HashMap<String, String> row = new HashMap<String, String>();
                    row.put("id",String.valueOf(rs.getInt("id")));
                    row.put("folio",rs.getString("folio"));
                    row.put("fecha_proceso",rs.getString("fecha_proceso"));

                    return row;
                }
            }
        );
        return hm;
    }




    @Override
    public ArrayList<HashMap<String, String>> getProgramacionPagos_Facturas(Integer id) {
        String sql_query = ""
                + "SELECT id_detalle,"
                    + "id_erp_h_fac,"
                    + "factura,"
                    + "fecha_factura,"
                    + "cliente,"
                    + "saldo_factura,"
                    + "revision_cobro,"
                    + "estatus_revision, "
                    + "estatus_revision, "
                    + "(CASE WHEN revision_cobro='R' AND estatus_revision=1 OR estatus_revision=2 THEN 'checked'  "
                    + "WHEN revision_cobro='C' AND estatus_revision=2 THEN 'checked' ELSE '' END) AS seleccionado,	"
                    + "actualizado "
                + "FROM ( "
                    + "SELECT "
                        + "cxc_fac_rev_cob_detalle.id AS id_detalle, "
                        + "erp_h_facturas.id AS id_erp_h_fac, "
                        + "erp_h_facturas.serie_folio as factura, "
                        + "to_char(erp_h_facturas.momento_facturacion,'dd/mm/yyyy') as fecha_factura, "
                        + "cxc_clie.razon_social AS cliente, "
                        + "erp_h_facturas.saldo_factura, "
                        + "cxc_fac_rev_cob_detalle.revision_cobro, "
                        + "erp_h_facturas.estatus_revision, "
                        + "cxc_fac_rev_cob.actualizado "
                    + "FROM cxc_fac_rev_cob "
                    + "JOIN cxc_fac_rev_cob_detalle ON cxc_fac_rev_cob_detalle.cxc_fac_rev_cob_id=cxc_fac_rev_cob.id "
                    + "JOIN erp_h_facturas ON erp_h_facturas.id=cxc_fac_rev_cob_detalle.erp_h_facturas_id "
                    + "JOIN cxc_clie ON cxc_clie.id=erp_h_facturas.cliente_id "
                    + "WHERE cxc_fac_rev_cob_detalle.cxc_fac_rev_cob_id=" +id +" "
                + ") AS sbt;";

        System.out.print(sql_query);

        ArrayList<HashMap<String, String>> hm = (ArrayList<HashMap<String, String>>) this.jdbcTemplate.query(
            sql_query,
            new Object[]{}, new RowMapper() {
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    HashMap<String, String> row = new HashMap<String, String>();
                    row.put("id_detalle",String.valueOf(rs.getInt("id_detalle")));
                    row.put("id_erp_h_fac",String.valueOf(rs.getInt("id_erp_h_fac")));
                    row.put("factura",rs.getString("factura"));
                    row.put("fecha_factura",rs.getString("fecha_factura"));
                    row.put("cliente",rs.getString("cliente"));
                    row.put("saldo_factura",StringHelper.roundDouble(rs.getString("saldo_factura"),2));
                    row.put("revision_cobro",rs.getString("revision_cobro"));
                    row.put("seleccionado",rs.getString("seleccionado"));
                    row.put("actualizado",String.valueOf(rs.getBoolean("actualizado")));

                    return row;
                }
            }
        );
        return hm;
    }




    //reporte de cobrabza diaria
    @Override
    public ArrayList<HashMap<String, String>> getCobranzaDiaria(String fecha_inicial, String fecha_final,Integer cliente,Integer id_empresa) {
        String where="";

        if(cliente!=0){
            where = "  and erp_pagos.cliente_id=" +cliente;
        }

        String sql_to_query = ""
                + "SELECT   "
                        +" erp_h_facturas.serie_folio AS factura, "
                        +" to_char(erp_h_facturas.momento_facturacion,'dd/mm/yyyy') AS fecha_factura, "
                        +" erp_pagos.cliente_id AS id_cliente, "
                        +" cxc_clie.razon_social AS cliente,  "
                        +" gral_mon.id as id_moneda_fac, "
                        +" gral_mon.simbolo AS simbolo_moneda_fac,  "
                        +" gral_mon.descripcion_abr AS moneda_fac, "
                        +" erp_h_facturas.monto_total AS monto_factura, "
                        +" gral_mon.simbolo AS simbolo_moneda_aplicado,  "
                        +" erp_pagos_detalles.cantidad AS pago_aplicado, "
                        +" to_char(erp_pagos.fecha_deposito,'dd/mm/yyyy') AS fecha_pago, "
                        +" gra_mon_pago.id as id_moneda_pago, "
                        +" gra_mon_pago.simbolo AS simbolo_moneda_pago, "
                        +" gra_mon_pago.descripcion_abr AS moneda_pago, "
                        +" (CASE WHEN erp_h_facturas.moneda_id=1 AND erp_pagos.moneda_id=1 THEN erp_pagos_detalles.cantidad "
                        +" WHEN erp_h_facturas.moneda_id=2 AND erp_pagos.moneda_id=2 THEN erp_pagos_detalles.cantidad "
                        +" WHEN erp_h_facturas.moneda_id=1 AND erp_pagos.moneda_id=2 THEN erp_pagos_detalles.cantidad/erp_pagos.tipo_cambio "
                        +" WHEN erp_h_facturas.moneda_id=2 AND erp_pagos.moneda_id=1 THEN erp_pagos_detalles.cantidad*erp_pagos.tipo_cambio "
                        +" ELSE erp_pagos_detalles.cantidad "
                        +" END ) AS monto_pago "
                    +" FROM erp_pagos "
                    +" JOIN erp_pagos_detalles ON erp_pagos_detalles.pago_id= erp_pagos.id "
                    +" JOIN erp_h_facturas ON erp_h_facturas.serie_folio = erp_pagos_detalles.serie_folio  "
                    +" JOIN cxc_clie  ON cxc_clie.id= erp_pagos.cliente_id "
                    +" JOIN gral_mon ON gral_mon.id = erp_h_facturas.moneda_id "
                    +" JOIN gral_mon AS gra_mon_pago ON gra_mon_pago.id = erp_pagos.moneda_id "
                    +" WHERE erp_pagos.empresa_id=" +id_empresa + " "
                    + "AND erp_pagos_detalles.cancelacion=FALSE "+where
                    +" AND (to_char(erp_pagos.fecha_deposito,'yyyymmdd')::integer BETWEEN to_char('"+fecha_inicial+"'::timestamp with time zone,'yyyymmdd')::integer AND to_char('"+fecha_final+"'::timestamp with time zone,'yyyymmdd')::integer) "
                    +" ORDER BY cxc_clie.razon_social, erp_pagos.fecha_deposito;";

        //System.out.println("getCobranzaDiaria:"+ sql_to_query);

        ArrayList<HashMap<String, String>> hm = (ArrayList<HashMap<String, String>>) this.jdbcTemplate.query(
                sql_to_query,
            new Object[]{}, new RowMapper() {
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    HashMap<String, String> row = new HashMap<String, String>();
                    row.put("factura",rs.getString("factura"));
                    row.put("fecha_factura",rs.getString("fecha_factura"));
                    row.put("id_cliente",String.valueOf(rs.getInt("id_cliente")));
                    row.put("cliente",rs.getString("cliente"));
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



    //reporte Anticipos no Autorizados
    @Override
    public ArrayList<HashMap<String, String>> getAnticiposnoAplicados(String fecha_inicial, String fecha_final, Integer cliente, Integer id_empresa) {
        String where="";

        if(cliente!=0){
            where = "  and cxc_ant.cliente_id=" +cliente;
        }

        String sql_to_query = ""
                + "select "
                    + "cxc_ant.cliente_id, "
                    + "to_char(cxc_ant.fecha_anticipo_usuario,'yyyy-mm-dd') as fecha_anticipo, "
                    + "cxc_clie.razon_social as cliente, "
                    + "cxc_ant.anticipo_inicial, "
                    + "cxc_ant.anticipo_actual, "
                    + "cxc_ant.observaciones "
                + "from cxc_ant "
                + "join cxc_clie on cxc_clie.id = cxc_ant.cliente_id "
                + "where cxc_ant.empresa_id= "+id_empresa+" "
                + "and cxc_ant.cancelado=false "
                + "and cxc_ant.borrado_logico=false "
                + ""+where+" "
                + "and to_char(cxc_ant.fecha_anticipo_usuario,'yyyymmdd'):: integer BETWEEN to_char('"+fecha_inicial+"'::timestamp with time zone,'yyyymmdd')::integer and to_char('"+fecha_final+"'::timestamp with time zone,'yyyymmdd')::integer "
                + "ORDER BY cxc_ant.fecha_anticipo_usuario ";


        ArrayList<HashMap<String, String>> hm = (ArrayList<HashMap<String, String>>) this.jdbcTemplate.query(
                sql_to_query,
            new Object[]{}, new RowMapper() {
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    HashMap<String, String> row = new HashMap<String, String>();
                    row.put("fecha_anticipo",rs.getString("fecha_anticipo"));
                    row.put("cliente_id",String.valueOf(rs.getInt("cliente_id")));
                    row.put("cliente",rs.getString("cliente"));

                    row.put("anticipo_inicial", StringHelper.roundDouble(rs.getString("anticipo_inicial"), 2));

                    row.put("anticipo_actual", StringHelper.roundDouble(rs.getString("anticipo_actual"), 2));

                    row.put("observaciones",rs.getString("observaciones"));

                    return row;
                }
            }
        );
        return hm;
    }


    //**********************************************************************************************************************
    //METODOS PARA CATALOGO DE DIRECCIONES FISCALES DE CLIENTES
    @Override
    public ArrayList<HashMap<String, Object>> getClientsDf_PaginaGrid(String data_string, int offset, int pageSize, String orderBy, String asc) {
        String sql_busqueda = "select id from gral_bus_catalogos(?) as foo (id integer)";

	String sql_to_query = ""
                + "SELECT sbt1.id,"
                    + "sbt1.cliente,"
                    + "sbt1.rfc,"
                    + "sbt1.calle||' '||sbt1.numero_interior||' '||sbt1.numero_exterior||', '||sbt1.colonia||', '||sbt1.municipio||', '||sbt1.estado||', '||sbt1.pais||', C.P.'||sbt1.cp AS direccion, "
                    + "sbt1.tel "
                + "FROM ("
                    + "SELECT "
                        + "cxc_clie_df.id, "
                        + "cxc_clie.razon_social AS cliente, "
                        + "cxc_clie.rfc,"
                        + "(CASE WHEN cxc_clie_df.calle IS NULL THEN '' ELSE cxc_clie_df.calle END) AS calle,"
                        + "(CASE WHEN cxc_clie_df.numero_interior IS NULL THEN '' ELSE 'NO.INT. '||cxc_clie_df.numero_interior END) AS numero_interior,"
                        + "(CASE WHEN cxc_clie_df.numero_exterior IS NULL THEN '' ELSE 'NO.EXT. '||cxc_clie_df.numero_exterior END) AS numero_exterior,"
                        + "(CASE WHEN cxc_clie_df.colonia IS NULL THEN '' ELSE cxc_clie_df.colonia END) AS colonia,"
                        + "(CASE WHEN gral_mun.id IS NULL OR gral_mun.id=0 THEN '' ELSE gral_mun.titulo END) AS municipio,"
                        + "(CASE WHEN gral_edo.id IS NULL OR gral_edo.id=0 THEN '' ELSE gral_edo.titulo END) AS estado,"
                        + "(CASE WHEN gral_pais.id IS NULL OR gral_pais.id=0 THEN '' ELSE gral_pais.titulo END) AS pais,"
                        + "(CASE WHEN cxc_clie_df.cp IS NULL THEN '' ELSE cxc_clie_df.cp END) AS cp,"
                        + "(CASE WHEN cxc_clie_df.telefono1 IS NULL OR cxc_clie_df.telefono1='' THEN cxc_clie_df.telefono2 ELSE cxc_clie_df.telefono1 END) AS tel "
                    + "FROM cxc_clie_df "
                    + "JOIN cxc_clie ON cxc_clie.id = cxc_clie_df.cxc_clie_id "
                    + "LEFT JOIN gral_pais ON gral_pais.id = cxc_clie_df.gral_pais_id "
                    + "LEFT JOIN gral_edo ON gral_edo.id = cxc_clie_df.gral_edo_id "
                    + "LEFT JOIN gral_mun ON gral_mun.id = cxc_clie_df.gral_mun_id "
                + ") as sbt1 "
                +"JOIN ("+sql_busqueda+") as subt on subt.id=sbt1.id "
                +"order by "+orderBy+" "+asc+" limit ? OFFSET ?";

        //System.out.println("Busqueda GetPage: "+sql_to_query);

        //log.log(Level.INFO, "Ejecutando query de {0}", sql_to_query);
        ArrayList<HashMap<String, Object>> hm = (ArrayList<HashMap<String, Object>>) this.jdbcTemplate.query(
            sql_to_query,
            new Object[]{new String(data_string), new Integer(pageSize),new Integer(offset)}, new RowMapper() {
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    HashMap<String, Object> row = new HashMap<String, Object>();
                    row.put("id",rs.getInt("id"));
                    row.put("cliente",rs.getString("cliente"));
                    row.put("rfc",rs.getString("rfc"));
                    row.put("direccion",rs.getString("direccion"));
                    row.put("tel",rs.getString("tel"));
                    return row;
                }
            }
        );
        return hm;
    }


    @Override
    public ArrayList<HashMap<String, Object>> getClientsDf_Datos(Integer id) {
        String sql_query = ""
                + "SELECT "
                    + "cxc_clie_df.id AS identificador,"
                    + "cxc_clie_df.cxc_clie_id AS id_cliente,"
                    + "cxc_clie.rfc,"
                    + "cxc_clie.numero_control,"
                    + "cxc_clie.razon_social AS cliente,"
                    + "cxc_clie_df.calle,"
                    + "cxc_clie_df.numero_interior,"
                    + "cxc_clie_df.numero_exterior,"
                    + "cxc_clie_df.entre_calles,"
                    + "cxc_clie_df.colonia,"
                    + "cxc_clie_df.cp,"
                    + "cxc_clie_df.gral_pais_id AS pais_id,"
                    + "cxc_clie_df.gral_edo_id AS estado_id,"
                    + "cxc_clie_df.gral_mun_id AS municipio_id,"
                    + "cxc_clie_df.telefono1,"
                    + "cxc_clie_df.extension1,"
                    + "cxc_clie_df.telefono2,"
                    + "cxc_clie_df.extension2,"
                    + "cxc_clie_df.fax,"
                    + "cxc_clie_df.email,"
                    + "cxc_clie_df.contacto "
                + "FROM cxc_clie_df "
                + "JOIN cxc_clie ON cxc_clie.id=cxc_clie_df.cxc_clie_id "
                + "WHERE cxc_clie_df.id=? AND cxc_clie_df.borrado_logico=false;";

        System.out.println("Ejecutando getClientsDf_Datos:"+ sql_query);
        System.out.println("identificador: "+id);

        ArrayList<HashMap<String, Object>> df = (ArrayList<HashMap<String, Object>>) this.jdbcTemplate.query(
            sql_query,
            new Object[]{new Integer(id)}, new RowMapper() {
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    HashMap<String, Object> row = new HashMap<String, Object>();
                    row.put("identificador",rs.getInt("identificador"));
                    row.put("id_cliente",rs.getInt("id_cliente"));
                    row.put("numero_control",rs.getString("numero_control"));
                    row.put("rfc",rs.getString("rfc"));
                    row.put("cliente",rs.getString("cliente"));
                    row.put("calle",rs.getString("calle"));
                    row.put("numero_interior",rs.getString("numero_interior"));
                    row.put("numero_exterior",rs.getString("numero_exterior"));
                    row.put("entre_calles",rs.getString("entre_calles"));
                    row.put("colonia",rs.getString("colonia"));
                    row.put("cp",rs.getString("cp"));
                    row.put("pais_id",rs.getString("pais_id"));
                    row.put("estado_id",rs.getString("estado_id"));
                    row.put("municipio_id",rs.getString("municipio_id"));
                    row.put("telefono1",rs.getString("telefono1"));
                    row.put("extension1",rs.getString("extension1"));
                    row.put("fax",rs.getString("fax"));
                    row.put("telefono2",rs.getString("telefono2"));
                    row.put("extension2",rs.getString("extension2"));
                    row.put("email",rs.getString("email"));
                    row.put("contacto",rs.getString("contacto"));
                    return row;
                }
            }
        );
        return df;
    }
    //AQUI TERMINA METODOS PARA CATALOGO DE DIRECCIONES FISCALES DE CLIENTES
    //**********************************************************************************************************************
    
    
    @Override
    public ArrayList<HashMap<String, String>> getListaClientes(Integer empresa_id , Integer agente_id) {
        String cadena_where="";
        if (agente_id != 0){
        cadena_where="and  cxc_clie.cxc_agen_id="+agente_id;
        }
        String sql_query = " select cxc_clie.id,   "
                        +"   cxc_clie.numero_control,   "
                        +"   cxc_clie.rfc,   "
                        +"   cxc_clie.curp,   "
                        +"   cxc_clie.razon_social,   "
                        +"   cxc_clie.telefono1||',   '||telefono2 as telefonos,   "
                        +"   cxc_clie.fax,   "
                        +"   cxc_clie.email,   "
                        +"   cxc_clie.calle||',  #'||cxc_clie.numero||', '||cxc_clie.colonia||', '|| gral_mun.titulo||' C.P.'||cxc_clie.cp||', '||gral_edo.titulo||', '||gral_pais.titulo as direccion_cliente   "

                        +"   from cxc_clie    "
                        +"   join cxc_agen on cxc_agen.id = cxc_clie.cxc_agen_id   "
                        +"   join gral_pais on gral_pais.id=cxc_clie.pais_id   "
                        +"   join gral_edo on gral_edo.id=cxc_clie.estado_id   "
                        +"   join gral_mun on gral_mun.id=cxc_clie.municipio_id   "
                        +"   where  cxc_clie.empresa_id=" +empresa_id+" "+cadena_where;

        //System.out.print(sql_query);

        ArrayList<HashMap<String, String>> hm = (ArrayList<HashMap<String, String>>) this.jdbcTemplate.query(
            sql_query,
            new Object[]{}, new RowMapper() {
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    HashMap<String, String> row = new HashMap<String, String>();
                    row.put("id",String.valueOf(rs.getInt("id")));
                    row.put("numero_control",rs.getString("numero_control"));
                    row.put("rfc",rs.getString("rfc"));
                    row.put("curp",rs.getString("curp"));
                    row.put("razon_social",rs.getString("razon_social"));
                    row.put("telefonos",rs.getString("telefonos"));
                    row.put("fax",rs.getString("fax"));
                    row.put("email",rs.getString("email"));
                    row.put("direccion_cliente",rs.getString("direccion_cliente"));

                    return row;
                }
            }
        );
        return hm;
    }


    
    
    @Override
    public ArrayList<HashMap<String, Object>> getClientsAntCancel_PaginaGrid(String data_string, int offset, int pageSize, String orderBy, String asc) {
        String sql_busqueda = "select id from gral_bus_catalogos(?) as foo (id integer)";

	String sql_to_query = ""
                + "SELECT "
                    + "cxc_ant.id, "
                    + "cxc_ant.numero_transaccion, "
                    + "cxc_ant.anticipo_inicial AS monto, "
                    + "gral_mon.descripcion_abr AS moneda, "
                    + "cxc_clie.razon_social AS cliente, "
                    + "to_char(cxc_ant.fecha_anticipo_usuario,'dd/mm/yyyy') AS fecha, "
                    + "(CASE WHEN cxc_ant.cancelado=TRUE THEN 'CANCELADO' ELSE (CASE WHEN cxc_ant.borrado_logico=TRUE THEN 'APLICADO' ELSE '' END) END) AS estado "
                    //+ "cxc_ant.observaciones AS observacion "
                + "FROM cxc_ant  "
                + "JOIN cxc_clie ON cxc_clie.id=cxc_ant.cliente_id "
                + "JOIN gral_mon ON gral_mon.id=cxc_ant.moneda_id "
                +"JOIN ("+sql_busqueda+") AS sbt ON sbt.id = cxc_ant.id "
                +"order by "+orderBy+" "+asc+" limit ? OFFSET ?";
        
        //System.out.println("Busqueda GetPage: "+sql_to_query);
        ArrayList<HashMap<String, Object>> hm = (ArrayList<HashMap<String, Object>>) this.jdbcTemplate.query(
            sql_to_query,
            new Object[]{new String(data_string), new Integer(pageSize),new Integer(offset)}, new RowMapper() {
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    HashMap<String, Object> row = new HashMap<String, Object>();
                    row.put("id",rs.getInt("id"));
                    row.put("numero_transaccion",rs.getString("numero_transaccion"));
                    row.put("monto",StringHelper.roundDouble(rs.getString("monto"),2));
                    row.put("moneda",rs.getString("moneda"));
                    row.put("cliente",rs.getString("cliente"));
                    row.put("fecha",rs.getString("fecha"));
                    row.put("estado",rs.getString("estado"));
                    //row.put("observacion",rs.getString("observacion"));
                    return row;
                }
            }
        );
        return hm;
    }
    
    
    
    
    @Override
    public ArrayList<HashMap<String, Object>> getClientsAntCancel_DatosAnticipo(Integer id) {
        String sql_query = ""
                + "SELECT "
                    + "cxc_ant.id, "
                    + "cxc_ant.numero_transaccion, "
                    + "cxc_ant.anticipo_inicial AS monto, "
                    + "to_char(cxc_ant.fecha_anticipo_usuario,'yyyy-mm-dd') AS fecha, "
                    + "cxc_ant.observaciones, "
                    + "cxc_clie.numero_control AS no_cliente, "
                    + "cxc_clie.razon_social AS cliente, "
                    + "cxc_ant.moneda_id, "
                    + "cxc_ant.cancelado "
                + "FROM cxc_ant "
                + "JOIN cxc_clie ON cxc_clie.id=cxc_ant.cliente_id "
                + "WHERE cxc_ant.id="+id;
        
        //System.out.print(sql_query);

        ArrayList<HashMap<String, Object>> hm = (ArrayList<HashMap<String, Object>>) this.jdbcTemplate.query(
            sql_query,
            new Object[]{}, new RowMapper() {
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    HashMap<String, Object> row = new HashMap<String, Object>();
                    row.put("id",String.valueOf(rs.getInt("id")));
                    row.put("numero_transaccion",rs.getString("numero_transaccion"));
                    row.put("monto",StringHelper.roundDouble(rs.getString("monto"),2));
                    row.put("fecha",rs.getString("fecha"));
                    row.put("observaciones",rs.getString("observaciones"));
                    row.put("no_cliente",rs.getString("no_cliente"));
                    row.put("cliente",rs.getString("cliente"));
                    row.put("moneda_id",String.valueOf(rs.getInt("moneda_id")));
                    row.put("cancelado",String.valueOf(rs.getBoolean("cancelado")));
                    return row;
                }
            }
        );
        return hm;
    }

    
    //Verificar si el usuario es Administrador
    @Override
    public HashMap<String, String> getUserRolAdmin(Integer id_user) {
        HashMap<String, String> data = new HashMap<String, String>();

        //verificar si el usuario tiene  rol de ADMINISTTRADOR
        //si exis es mayor que cero, el usuario si es ADMINISTRADOR
        String sql_to_query = "SELECT count(gral_usr_id) AS exis_rol_admin FROM gral_usr_rol WHERE gral_usr_id="+id_user+" AND gral_rol_id=1;";

        Map<String, Object> map = this.getJdbcTemplate().queryForMap(sql_to_query);

        data.put("exis_rol_admin",map.get("exis_rol_admin").toString());

        return data;
    }
    
    //Verificar si el usuario es un Agente de Ventas
    @Override
    public HashMap<String, String> getUserRolAgenteVenta(Integer id_user) {
        HashMap<String, String> data = new HashMap<String, String>();
        
        //verificar si el usuario es un Vendedor
        //si rol_agente_venta es mayor que cero, el usuario si es Agente de Ventas
        String sql_to_query = "SELECT count(gral_usr_id) AS rol_agente_venta FROM cxc_agen WHERE gral_usr_id="+id_user+" AND borrado_logico=false;";
        
        Map<String, Object> map = this.getJdbcTemplate().queryForMap(sql_to_query);
        
        data.put("rol_agente_venta",map.get("rol_agente_venta").toString());
        
        return data;
    }
    

    
    
    //**********************************************************************************************************************
    //METODOS PARA CATALOGO DE REMITENTES
    @Override
    public ArrayList<HashMap<String, Object>> getClientsRemiten_PaginaGrid(String data_string, int offset, int pageSize, String orderBy, String asc) {
        String sql_busqueda = "select id from gral_bus_catalogos(?) as foo (id integer)";
        
	String sql_to_query = ""
                + "SELECT cxc_remitentes.id,cxc_remitentes.folio,cxc_remitentes.rfc,cxc_remitentes.razon_social AS remitente,"
                    + "(CASE WHEN trim(cxc_remitentes.tel1)='' OR cxc_remitentes.tel1 IS NULL THEN tel2 ELSE cxc_remitentes.tel1||''||(CASE WHEN cxc_remitentes.tel2='' OR cxc_remitentes.tel2 IS NULL THEN '' ELSE ','||cxc_remitentes.tel2 END ) END ) AS tel,"
                    + "(CASE WHEN cxc_remitentes.tipo=1 THEN 'NACIONAL' WHEN cxc_remitentes.tipo=2 THEN 'EXTRANJERO' ELSE '' END) AS tipo "
                + "FROM cxc_remitentes "
                +"JOIN ("+sql_busqueda+") as subt on subt.id=cxc_remitentes.id "
                +"order by "+orderBy+" "+asc+" limit ? OFFSET ?";

        //System.out.println("Busqueda GetPage: "+sql_to_query);

        //log.log(Level.INFO, "Ejecutando query de {0}", sql_to_query);
        ArrayList<HashMap<String, Object>> hm = (ArrayList<HashMap<String, Object>>) this.jdbcTemplate.query(
            sql_to_query,
            new Object[]{new String(data_string), new Integer(pageSize),new Integer(offset)}, new RowMapper() {
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    HashMap<String, Object> row = new HashMap<String, Object>();
                    row.put("id",rs.getInt("id"));
                    row.put("folio",rs.getString("folio"));
                    row.put("rfc",rs.getString("rfc"));
                    row.put("remitente",rs.getString("remitente"));
                    row.put("tel",rs.getString("tel"));
                    row.put("tipo",rs.getString("tipo"));
                    return row;
                }
            }
        );
        return hm;
    }


    @Override
    public ArrayList<HashMap<String, Object>> getClientsRemiten_Datos(Integer id) {
        String sql_query = ""
                + "SELECT id,folio,rfc,razon_social AS remitente,tipo,calle,no_int,no_ext,colonia,gral_mun_id,gral_edo_id,gral_pais_id,cp,(CASE WHEN tel1 IS NULL THEN '' ELSE tel1 END) AS tel1,(CASE WHEN tel2 IS NULL THEN '' ELSE tel2 END) AS tel2,ext,email "
                + "FROM cxc_remitentes "
                + "WHERE id=?";

        //System.out.println("Ejecutando getRemitente:"+ sql_query);
        //System.out.println("identificador: "+id);

        ArrayList<HashMap<String, Object>> df = (ArrayList<HashMap<String, Object>>) this.jdbcTemplate.query(
            sql_query,
            new Object[]{new Integer(id)}, new RowMapper() {
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    HashMap<String, Object> row = new HashMap<String, Object>();
                    row.put("identificador",rs.getInt("id"));
                    row.put("folio",rs.getString("folio"));
                    row.put("rfc",rs.getString("rfc"));
                    row.put("remitente",rs.getString("remitente"));
                    row.put("tipo",rs.getInt("tipo"));
                    row.put("calle",rs.getString("calle"));
                    row.put("no_int",rs.getString("no_int"));
                    row.put("no_ext",rs.getString("no_ext"));
                    row.put("colonia",rs.getString("colonia"));
                    row.put("cp",rs.getString("cp"));
                    row.put("pais_id",rs.getString("gral_pais_id"));
                    row.put("estado_id",rs.getString("gral_edo_id"));
                    row.put("municipio_id",rs.getString("gral_mun_id"));
                    row.put("telefono1",rs.getString("tel1"));
                    row.put("telefono2",rs.getString("tel2"));
                    row.put("extension",rs.getString("ext"));
                    row.put("email",rs.getString("email"));
                    return row;
                }
            }
        );
        return df;
    }
    //AQUI TERMINA METODOS PARA CATALOGO DE REMITENTES
    //**********************************************************************************************************************
    
    
    //**********************************************************************************************************************
    //METODOS PARA CATALOGO DE DESTINATARIOS
    @Override
    public ArrayList<HashMap<String, Object>> getClientsDest_PaginaGrid(String data_string, int offset, int pageSize, String orderBy, String asc) {
        String sql_busqueda = "select id from gral_bus_catalogos(?) as foo (id integer)";
        
	String sql_to_query = ""
                + "SELECT cxc_destinatarios.id,cxc_destinatarios.folio, cxc_destinatarios.folio_ext, cxc_destinatarios.rfc,cxc_destinatarios.razon_social AS destinatario,"
                    + "(CASE WHEN trim(cxc_destinatarios.tel1)='' OR cxc_destinatarios.tel1 IS NULL THEN tel2 ELSE cxc_destinatarios.tel1||''||(CASE WHEN cxc_destinatarios.tel2='' OR cxc_destinatarios.tel2 IS NULL THEN '' ELSE ','||cxc_destinatarios.tel2 END ) END ) AS tel,"
                    + "(CASE WHEN cxc_destinatarios.tipo=1 THEN 'NACIONAL' WHEN cxc_destinatarios.tipo=2 THEN 'EXTRANJERO' ELSE '' END) AS tipo,"
                    + "(case when cxc_clie.id is null then '' else cxc_clie.clave_comercial end) as cliente "
                + "FROM cxc_destinatarios "
                + "left join cxc_clie_dest on cxc_clie_dest.cxc_destinatario_id=cxc_destinatarios.id "
                + "left join cxc_clie on cxc_clie.id=cxc_clie_dest.cxc_clie_id "
                +"JOIN ("+sql_busqueda+") as subt on subt.id=cxc_destinatarios.id "
                +"order by "+orderBy+" "+asc+" limit ? OFFSET ?";

        //System.out.println("Busqueda GetPage: "+sql_to_query);

        //log.log(Level.INFO, "Ejecutando query de {0}", sql_to_query);
        ArrayList<HashMap<String, Object>> hm = (ArrayList<HashMap<String, Object>>) this.jdbcTemplate.query(
            sql_to_query,
            new Object[]{data_string, new Integer(pageSize),new Integer(offset)}, new RowMapper() {
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    HashMap<String, Object> row = new HashMap<String, Object>();
                    row.put("id",rs.getInt("id"));
                    row.put("folio",rs.getString("folio"));
                    row.put("folio_ext",rs.getString("folio_ext"));
                    row.put("rfc",rs.getString("rfc"));
                    row.put("destinatario",rs.getString("destinatario"));
                    row.put("tel",rs.getString("tel"));
                    row.put("tipo",rs.getString("tipo"));
                    row.put("cliente",rs.getString("cliente"));
                    return row;
                }
            }
        );
        return hm;
    }
    
    
    @Override
    public ArrayList<HashMap<String, Object>> getClientsDest_Datos(Integer id) {
        String sql_query = ""
        + "select cxc_destinatarios.id,cxc_destinatarios.folio, cxc_destinatarios.folio_ext,cxc_destinatarios.rfc,cxc_destinatarios.razon_social AS destinatario,cxc_destinatarios.tipo,cxc_destinatarios.calle, cxc_destinatarios.no_int, cxc_destinatarios.no_ext, cxc_destinatarios.colonia, cxc_destinatarios.gral_mun_id, cxc_destinatarios.gral_edo_id, cxc_destinatarios.gral_pais_id, cxc_destinatarios.cp,(CASE WHEN cxc_destinatarios.tel1 IS NULL THEN '' ELSE cxc_destinatarios.tel1 END) AS tel1,(CASE WHEN cxc_destinatarios.tel2 IS NULL THEN '' ELSE cxc_destinatarios.tel2 END) AS tel2, cxc_destinatarios.ext, cxc_destinatarios.email, cxc_destinatarios.solicitar_firma, cxc_destinatarios.solicitar_sello, cxc_destinatarios.solicitar_efectivo, cxc_destinatarios.solicitar_cheque, "
                + "(case when cxc_clie.id is null then 0 else cxc_clie.id end) as cliente_id,"
                + "(case when cxc_clie.id is null then '' else cxc_clie.numero_control end) as no_control,"
                + "(case when cxc_clie.id is null then '' else cxc_clie.razon_social end) as cliente,"
                + "cxc_destinatarios.inv_prod_id as serv_id,"
                + "cxc_destinatarios.costo_serv as serv_costo "
        + "from cxc_destinatarios "
        + "left join cxc_clie_dest on cxc_clie_dest.cxc_destinatario_id=cxc_destinatarios.id "
        + "left join cxc_clie on cxc_clie.id=cxc_clie_dest.cxc_clie_id "
        + "where cxc_destinatarios.id=? order by cxc_destinatarios.id desc limit 1";
        
        //System.out.println("Ejecutando getDestinatario:"+ sql_query);
        //System.out.println("identificador: "+id);

        ArrayList<HashMap<String, Object>> df = (ArrayList<HashMap<String, Object>>) this.jdbcTemplate.query(
            sql_query,
            new Object[]{new Integer(id)}, new RowMapper() {
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    HashMap<String, Object> row = new HashMap<String, Object>();
                    row.put("identificador",rs.getInt("id"));
                    row.put("folio",rs.getString("folio"));
                    row.put("f_ext",rs.getString("folio_ext"));
                    row.put("rfc",rs.getString("rfc"));
                    row.put("destinatario",rs.getString("destinatario"));
                    row.put("tipo",rs.getInt("tipo"));
                    row.put("calle",rs.getString("calle"));
                    row.put("no_int",rs.getString("no_int"));
                    row.put("no_ext",rs.getString("no_ext"));
                    row.put("colonia",rs.getString("colonia"));
                    row.put("cp",rs.getString("cp"));
                    row.put("pais_id",rs.getString("gral_pais_id"));
                    row.put("estado_id",rs.getString("gral_edo_id"));
                    row.put("municipio_id",rs.getString("gral_mun_id"));
                    row.put("telefono1",rs.getString("tel1"));
                    row.put("telefono2",rs.getString("tel2"));
                    row.put("extension",rs.getString("ext"));
                    row.put("email",rs.getString("email"));
                    row.put("sfirma",rs.getBoolean("solicitar_firma"));
                    row.put("ssello",rs.getBoolean("solicitar_sello"));
                    row.put("sefectivo",rs.getBoolean("solicitar_efectivo"));
                    row.put("scheque",rs.getBoolean("solicitar_cheque"));
                    
                    row.put("cliente_id",rs.getInt("cliente_id"));
                    row.put("no_control",rs.getString("no_control"));
                    row.put("cliente",rs.getString("cliente"));
                    row.put("serv_id",rs.getInt("serv_id"));
                    row.put("serv_costo",StringHelper.roundDouble(rs.getString("serv_costo"),2));
                    return row;
                }
            }
        );
        return df;
    }
    
    
    //Buscador de Servicios Adicionales
    @Override
    public ArrayList<HashMap<String, Object>> getBuscadorServiciosAdicionales(String sku, String descripcion, Integer id_empresa) {
        String where = "";
	if(!sku.equals("")){
            where=" and inv_prod.sku ilike '%"+sku+"%'";
	}
        
	if(!descripcion.equals("")){
            where +=" and inv_prod.descripcion ilike '%"+descripcion+"%'";
	}
        
        String sql_to_query = ""
        + "select "
            +"inv_prod.id,"
            +"inv_prod.sku,"
            +"inv_prod.descripcion, "
            + "inv_prod.unidad_id, "
            + "inv_prod_unidades.titulo AS unidad, "
            +"inv_prod_tipos.titulo AS tipo,"
            + "inv_prod_unidades.decimales "
        +"from log_serv_adic "
        + "join inv_prod on inv_prod.id=log_serv_adic.inv_prod_id "
        + "left join inv_prod_tipos on inv_prod_tipos.id=inv_prod.tipo_de_producto_id "
        + "left join inv_prod_unidades on inv_prod_unidades.id=inv_prod.unidad_id "
        + "where inv_prod.empresa_id=? and inv_prod.borrado_logico=false "+where+" order by inv_prod.descripcion;";
        //log.log(Level.INFO, "Ejecutando query de {0}", sql_to_query);
        
        //System.out.println("sql_to_query: "+sql_to_query);

        ArrayList<HashMap<String, Object>> hm_datos_productos = (ArrayList<HashMap<String, Object>>) this.jdbcTemplate.query(
            sql_to_query,
            new Object[]{new Integer(id_empresa)}, new RowMapper(){
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    HashMap<String, Object> row = new HashMap<String, Object>();
                    row.put("id",rs.getInt("id"));
                    row.put("sku",rs.getString("sku"));
                    row.put("descripcion",rs.getString("descripcion"));
                    row.put("unidad_id",rs.getInt("unidad_id"));
                    row.put("unidad",rs.getString("unidad"));
                    row.put("tipo",rs.getString("tipo"));
                    row.put("decimales",rs.getInt("decimales"));
                    return row;
                }
            }
        );
        return hm_datos_productos;
    }
    
    //AQUI TERMINA METODOS PARA CATALOGO DE DESTINATARIOS
    //**********************************************************************************************************************
    
    
    
    //**********************************************************************************************************************
    //METODOS PARA CATALOGO DE AGENTES ADUANALES
    @Override
    public ArrayList<HashMap<String, Object>> getClientsAgenAduanal_PaginaGrid(String data_string, int offset, int pageSize, String orderBy, String asc) {
        String sql_busqueda = "select id from gral_bus_catalogos(?) as foo (id integer)";
        
	String sql_to_query = ""
                + "SELECT cxc_agentes_aduanales.id,cxc_agentes_aduanales.folio,cxc_agentes_aduanales.razon_social AS agente_a,"
                    + "(CASE WHEN trim(cxc_agentes_aduanales.tel1)='' OR cxc_agentes_aduanales.tel1 IS NULL THEN tel2 ELSE cxc_agentes_aduanales.tel1||''||(CASE WHEN cxc_agentes_aduanales.tel2='' OR cxc_agentes_aduanales.tel2 IS NULL THEN '' ELSE ','||cxc_agentes_aduanales.tel2 END ) END ) AS tel,"
                    + "(CASE WHEN cxc_agentes_aduanales.tipo=1 THEN 'NACIONAL' WHEN cxc_agentes_aduanales.tipo=2 THEN 'EXTRANJERO' ELSE '' END) AS tipo "
                + "FROM cxc_agentes_aduanales "
                +"JOIN ("+sql_busqueda+") as subt on subt.id=cxc_agentes_aduanales.id "
                +"order by "+orderBy+" "+asc+" limit ? OFFSET ?";

        //System.out.println("Busqueda GetPage: "+sql_to_query);

        //log.log(Level.INFO, "Ejecutando query de {0}", sql_to_query);
        ArrayList<HashMap<String, Object>> hm = (ArrayList<HashMap<String, Object>>) this.jdbcTemplate.query(
            sql_to_query,
            new Object[]{new String(data_string), new Integer(pageSize),new Integer(offset)}, new RowMapper() {
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    HashMap<String, Object> row = new HashMap<String, Object>();
                    row.put("id",rs.getInt("id"));
                    row.put("folio",rs.getString("folio"));
                    row.put("agente_a",rs.getString("agente_a"));
                    row.put("tel",rs.getString("tel"));
                    row.put("tipo",rs.getString("tipo"));
                    return row;
                }
            }
        );
        return hm;
    }
    
    
    @Override
    public ArrayList<HashMap<String, Object>> getClientsAgenAduanal_Datos(Integer id) {
        String sql_query = ""
                + "SELECT id,folio,razon_social AS agente_a,tipo,calle,no_int,no_ext,colonia,gral_mun_id,gral_edo_id,gral_pais_id,cp,(CASE WHEN tel1 IS NULL THEN '' ELSE tel1 END) AS tel1,(CASE WHEN tel2 IS NULL THEN '' ELSE tel2 END) AS tel2,ext,email "
                + "FROM cxc_agentes_aduanales "
                + "WHERE id=?";
        
        //System.out.println("Ejecutando getAgenteAduanal:"+ sql_query);
        //System.out.println("identificador: "+id);
        
        ArrayList<HashMap<String, Object>> df = (ArrayList<HashMap<String, Object>>) this.jdbcTemplate.query(
            sql_query,
            new Object[]{new Integer(id)}, new RowMapper() {
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    HashMap<String, Object> row = new HashMap<String, Object>();
                    row.put("identificador",rs.getInt("id"));
                    row.put("folio",rs.getString("folio"));
                    row.put("agente_a",rs.getString("agente_a"));
                    row.put("tipo",rs.getInt("tipo"));
                    row.put("calle",rs.getString("calle"));
                    row.put("no_int",rs.getString("no_int"));
                    row.put("no_ext",rs.getString("no_ext"));
                    row.put("colonia",rs.getString("colonia"));
                    row.put("cp",rs.getString("cp"));
                    row.put("pais_id",rs.getString("gral_pais_id"));
                    row.put("estado_id",rs.getString("gral_edo_id"));
                    row.put("municipio_id",rs.getString("gral_mun_id"));
                    row.put("telefono1",rs.getString("tel1"));
                    row.put("telefono2",rs.getString("tel2"));
                    row.put("extension",rs.getString("ext"));
                    row.put("email",rs.getString("email"));
                    return row;
                }
            }
        );
        return df;
    }
    //AQUI TERMINA METODOS PARA CATALOGO DE AGENTES ADUANALES
    //**********************************************************************************************************************
    
    
    //--------------------------------------------------------------------------------------------------------------------
    //ASIGNACION DE REMITENTES A CLIENTES
    @Override
    public ArrayList<HashMap<String, Object>> getClientsAsignaRem_PaginaGrid(String data_string, int offset, int pageSize, String orderBy, String asc) {
        String sql_busqueda = "select id from gral_bus_catalogos(?) as foo (id integer)";
        
	String sql_to_query = "SELECT "
				+"cxc_clie.id, "
				+"cxc_clie.numero_control, "
				+"cxc_clie.razon_social, "
				+"cxc_clie.rfc "
			+"FROM cxc_clie "
                        +"JOIN ("+sql_busqueda+") as subt on subt.id=cxc_clie.id "
                        +"order by "+orderBy+" "+asc+" limit ? OFFSET ?";
        
        //System.out.println("Busqueda GetPage: "+sql_to_query);

        //log.log(Level.INFO, "Ejecutando query de {0}", sql_to_query);
        ArrayList<HashMap<String, Object>> hm = (ArrayList<HashMap<String, Object>>) this.jdbcTemplate.query(
            sql_to_query,
            new Object[]{new String(data_string), new Integer(pageSize),new Integer(offset)}, new RowMapper() {
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    HashMap<String, Object> row = new HashMap<String, Object>();
                    row.put("id",rs.getInt("id"));
                    row.put("numero_control",rs.getString("numero_control"));
                    row.put("razon_social",rs.getString("razon_social"));
                    row.put("rfc",rs.getString("rfc"));
                    return row;
                }
            }
        );
        return hm;
    }
    
    

    @Override
    public ArrayList<HashMap<String, Object>> getClientsAsignaRem_Datos(Integer id) {
        String sql_query = "SELECT  id, numero_control, razon_social FROM cxc_clie WHERE id=? LIMIT 1;";
        
        ArrayList<HashMap<String, Object>> hmcli = (ArrayList<HashMap<String, Object>>) this.jdbcTemplate.query(
            sql_query,
            new Object[]{new Integer(id)}, new RowMapper() {
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    HashMap<String, Object> row = new HashMap<String, Object>();
                    row.put("identificador",rs.getInt("id"));
                    row.put("numero_control",rs.getString("numero_control"));
                    row.put("razon_social",rs.getString("razon_social"));
                    return row;
                }
            }
        );
        return hmcli;
    }
    
    
    //Obtiene los remitentes asignados a un cliente en especifico.
    @Override
    public ArrayList<HashMap<String, Object>> getClientsAsignaRem_RemitentesAsignados(Integer id) {
        String sql_query = ""
                + "SELECT "
                    + "cxc_clie_remitente.id AS iddet,"
                    + "cxc_clie_remitente.cxc_clie_id AS clie_id,"
                    + "cxc_clie_remitente.cxc_remitente_id AS rem_id,"
                    + "cxc_remitentes.folio AS no_control,"
                    + "cxc_remitentes.razon_social AS nombre,"
                    + "cxc_remitentes.rfc "
                + "FROM cxc_clie_remitente "
                + "JOIN cxc_remitentes ON cxc_remitentes.id=cxc_clie_remitente.cxc_remitente_id "
                + "WHERE cxc_clie_remitente.cxc_clie_id=?";
        
        ArrayList<HashMap<String, Object>> hmrem = (ArrayList<HashMap<String, Object>>) this.jdbcTemplate.query(
            sql_query,
            new Object[]{new Integer(id)}, new RowMapper() {
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    HashMap<String, Object> row = new HashMap<String, Object>();
                    row.put("iddet",rs.getInt("iddet"));
                    row.put("clie_id",rs.getInt("clie_id"));
                    row.put("rem_id",rs.getInt("rem_id"));
                    row.put("no_control",rs.getString("no_control"));
                    row.put("nombre",rs.getString("nombre"));
                    row.put("rfc",rs.getString("rfc"));
                    return row;
                }
            }
        );
        return hmrem;
    }
    
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
        + "SELECT cxc_remitentes.id,cxc_remitentes.folio,cxc_remitentes.rfc, cxc_remitentes.razon_social, cxc_remitentes.tipo "
        +"FROM cxc_remitentes "
        +" WHERE cxc_remitentes.gral_emp_id ="+id_empresa+"  "
        +" AND cxc_remitentes.borrado_logico=false  "+where+" "
        + "ORDER BY id limit 100;";
        
        System.out.println("BuscarRemitente: "+sql_query);
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
                    row.put("tipo",rs.getInt("tipo"));
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
        + "SELECT cxc_remitentes.id,cxc_remitentes.folio,cxc_remitentes.rfc, cxc_remitentes.razon_social, cxc_remitentes.tipo "
        +"FROM cxc_remitentes "
        +" WHERE cxc_remitentes.gral_emp_id ="+id_empresa+"  "
        +" AND cxc_remitentes.borrado_logico=false  "+where+" "
        + "AND cxc_remitentes.folio='"+no_control.toUpperCase()+"'"
        + "ORDER BY id limit 1;";
        
        System.out.println("getDatosRemitente: "+sql_query);

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
                    row.put("tipo",rs.getInt("tipo"));
                    return row;
                }
            }
        );
        return hm_rem;
    }

    //TERMINA ASIGNACION DE REMITENTES A CLIENTES
    //--------------------------------------------------------------------------------------------------------------------
    

    
    
    //--------------------------------------------------------------------------------------------------------------------
    //ASIGNACION DE DESTINATARIOS A CLIENTES
    @Override
    public ArrayList<HashMap<String, Object>> getClientsAsignaDest_PaginaGrid(String data_string, int offset, int pageSize, String orderBy, String asc) {
        String sql_busqueda = "select id from gral_bus_catalogos(?) as foo (id integer)";
        
	String sql_to_query = "SELECT "
				+"cxc_clie.id, "
				+"cxc_clie.numero_control, "
				+"cxc_clie.razon_social, "
				+"cxc_clie.rfc "
			+"FROM cxc_clie "
                        +"JOIN ("+sql_busqueda+") as subt on subt.id=cxc_clie.id "
                        +"order by "+orderBy+" "+asc+" limit ? OFFSET ?";
        
        //System.out.println("Busqueda GetPage: "+sql_to_query);

        //log.log(Level.INFO, "Ejecutando query de {0}", sql_to_query);
        ArrayList<HashMap<String, Object>> hm = (ArrayList<HashMap<String, Object>>) this.jdbcTemplate.query(
            sql_to_query,
            new Object[]{new String(data_string), new Integer(pageSize),new Integer(offset)}, new RowMapper() {
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    HashMap<String, Object> row = new HashMap<String, Object>();
                    row.put("id",rs.getInt("id"));
                    row.put("numero_control",rs.getString("numero_control"));
                    row.put("razon_social",rs.getString("razon_social"));
                    row.put("rfc",rs.getString("rfc"));
                    return row;
                }
            }
        );
        return hm;
    }
    
    
    
    @Override
    public ArrayList<HashMap<String, Object>> getClientsAsignaDest_Datos(Integer id) {
        String sql_query = "SELECT  id, numero_control, razon_social FROM cxc_clie WHERE id=? LIMIT 1;";
        
        ArrayList<HashMap<String, Object>> hmcli = (ArrayList<HashMap<String, Object>>) this.jdbcTemplate.query(
            sql_query,
            new Object[]{new Integer(id)}, new RowMapper() {
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    HashMap<String, Object> row = new HashMap<String, Object>();
                    row.put("identificador",rs.getInt("id"));
                    row.put("numero_control",rs.getString("numero_control"));
                    row.put("razon_social",rs.getString("razon_social"));
                    return row;
                }
            }
        );
        return hmcli;
    }
    
    
    //Obtiene los Destinatarios asignados a un cliente en especifico.
    @Override
    public ArrayList<HashMap<String, Object>> getClientsAsignaDest_DestinatariosAsignados(Integer id) {
        String sql_query = ""
                + "SELECT "
                    + "cxc_clie_dest.id AS iddet,"
                    + "cxc_clie_dest.cxc_clie_id AS clie_id,"
                    + "cxc_clie_dest.cxc_destinatario_id AS dest_id,"
                    + "cxc_destinatarios.folio AS no_control,"
                    + "cxc_destinatarios.razon_social AS nombre,"
                    + "cxc_destinatarios.rfc "
                + "FROM cxc_clie_dest "
                + "JOIN cxc_destinatarios ON cxc_destinatarios.id=cxc_clie_dest.cxc_destinatario_id "
                + "WHERE cxc_clie_dest.cxc_clie_id=?";
        
        ArrayList<HashMap<String, Object>> hmrem = (ArrayList<HashMap<String, Object>>) this.jdbcTemplate.query(
            sql_query,
            new Object[]{new Integer(id)}, new RowMapper() {
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    HashMap<String, Object> row = new HashMap<String, Object>();
                    row.put("iddet",rs.getInt("iddet"));
                    row.put("clie_id",rs.getInt("clie_id"));
                    row.put("dest_id",rs.getInt("dest_id"));
                    row.put("no_control",rs.getString("no_control"));
                    row.put("nombre",rs.getString("nombre"));
                    row.put("rfc",rs.getString("rfc"));
                    return row;
                }
            }
        );
        return hmrem;
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
        + "SELECT cxc_destinatarios.id,cxc_destinatarios.folio,cxc_destinatarios.rfc, cxc_destinatarios.razon_social, cxc_destinatarios.tipo "
        +"FROM cxc_destinatarios "
        +" WHERE cxc_destinatarios.gral_emp_id ="+id_empresa+"  "
        +" AND cxc_destinatarios.borrado_logico=false  "+where+" "
        + "ORDER BY id limit 100;";
        
        System.out.println("BuscarDest: "+sql_query);
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
                    row.put("tipo",rs.getInt("tipo"));
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
        + "SELECT cxc_destinatarios.id,cxc_destinatarios.folio,cxc_destinatarios.rfc, cxc_destinatarios.razon_social, cxc_destinatarios.tipo "
        +"FROM cxc_destinatarios "
        +" WHERE cxc_destinatarios.gral_emp_id ="+id_empresa+"  "
        +" AND cxc_destinatarios.borrado_logico=false  "+where+" "
        + "AND cxc_destinatarios.folio='"+no_control.toUpperCase()+"'"
        + "ORDER BY id limit 1;";
        
        System.out.println("getDatosDest: "+sql_query);
        
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
                    row.put("tipo",rs.getInt("tipo"));
                    return row;
                }
            }
        );
        return hm_dest;
    }
    //TERMINA ASIGNACION DE DESTINATARIOS A CLIENTES
    //--------------------------------------------------------------------------------------------------------------------
    
    
    //--------------------------------------------------------------------------------------------------------------------
    //ASIGNACION DE AGENTES ADUANALES A CLIENTES
    @Override
    public ArrayList<HashMap<String, Object>> getClientsAsignaAgenA_PaginaGrid(String data_string, int offset, int pageSize, String orderBy, String asc) {
        String sql_busqueda = "select id from gral_bus_catalogos(?) as foo (id integer)";
        
	String sql_to_query = "SELECT "
				+"cxc_clie.id, "
				+"cxc_clie.numero_control, "
				+"cxc_clie.razon_social, "
				+"cxc_clie.rfc "
			+"FROM cxc_clie "
                        +"JOIN ("+sql_busqueda+") as subt on subt.id=cxc_clie.id "
                        +"order by "+orderBy+" "+asc+" limit ? OFFSET ?";
        
        //System.out.println("Busqueda GetPage: "+sql_to_query);

        //log.log(Level.INFO, "Ejecutando query de {0}", sql_to_query);
        ArrayList<HashMap<String, Object>> hm = (ArrayList<HashMap<String, Object>>) this.jdbcTemplate.query(
            sql_to_query,
            new Object[]{new String(data_string), new Integer(pageSize),new Integer(offset)}, new RowMapper() {
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    HashMap<String, Object> row = new HashMap<String, Object>();
                    row.put("id",rs.getInt("id"));
                    row.put("numero_control",rs.getString("numero_control"));
                    row.put("razon_social",rs.getString("razon_social"));
                    row.put("rfc",rs.getString("rfc"));
                    return row;
                }
            }
        );
        return hm;
    }
    
    
    
    @Override
    public ArrayList<HashMap<String, Object>> getClientsAsignaAgenA_Datos(Integer id) {
        String sql_query = "SELECT  id, numero_control, razon_social FROM cxc_clie WHERE id=? LIMIT 1;";
        
        ArrayList<HashMap<String, Object>> hmcli = (ArrayList<HashMap<String, Object>>) this.jdbcTemplate.query(
            sql_query,
            new Object[]{new Integer(id)}, new RowMapper() {
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    HashMap<String, Object> row = new HashMap<String, Object>();
                    row.put("identificador",rs.getInt("id"));
                    row.put("numero_control",rs.getString("numero_control"));
                    row.put("razon_social",rs.getString("razon_social"));
                    return row;
                }
            }
        );
        return hmcli;
    }
    
    
    //Obtiene los Agentes Aduanales asignados a un cliente en especifico.
    @Override
    public ArrayList<HashMap<String, Object>> getClientsAsignaAgenA_Asignados(Integer id) {
        String sql_query = ""
                + "SELECT "
                    + "cxc_clie_agen_aduanal.id AS iddet,"
                    + "cxc_clie_agen_aduanal.cxc_clie_id AS clie_id,"
                    + "cxc_clie_agen_aduanal.cxc_agente_aduanal_id AS agena_id,"
                    + "cxc_agentes_aduanales.folio AS no_control,"
                    + "cxc_agentes_aduanales.razon_social AS nombre,"
                    + "cxc_agentes_aduanales.rfc "
                + "FROM cxc_clie_agen_aduanal "
                + "JOIN cxc_agentes_aduanales ON cxc_agentes_aduanales.id=cxc_clie_agen_aduanal.cxc_agente_aduanal_id "
                + "WHERE cxc_clie_agen_aduanal.cxc_clie_id=?";
        
        ArrayList<HashMap<String, Object>> hmrem = (ArrayList<HashMap<String, Object>>) this.jdbcTemplate.query(
            sql_query,
            new Object[]{new Integer(id)}, new RowMapper() {
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    HashMap<String, Object> row = new HashMap<String, Object>();
                    row.put("iddet",rs.getInt("iddet"));
                    row.put("clie_id",rs.getInt("clie_id"));
                    row.put("agena_id",rs.getInt("agena_id"));
                    row.put("no_control",rs.getString("no_control"));
                    row.put("nombre",rs.getString("nombre"));
                    row.put("rfc",rs.getString("rfc"));
                    return row;
                }
            }
        );
        return hmrem;
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
        
        System.out.println("BuscarAgenA: "+sql_query);
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
        + "AND cxc_agentes_aduanales.folio='"+no_control.toUpperCase()+"'"
        + "ORDER BY id limit 1;";
        
        System.out.println("getDatosAgenA: "+sql_query);
        
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
    //TERMINA ASIGNACION DE AGENTES ADUANALES A CLIENTES
    //--------------------------------------------------------------------------------------------------------------------
    
    
    //Medotdos para reporte de saldo Mensual------------------------------------------------------------------------------
    //Calcular años a mostrar en el reporte de Saldo Mansual
    @Override
    public ArrayList<HashMap<String, Object>>  getCxc_AnioReporteSaldoMensual() {
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
    

    //Este es el query para el reporte de Saldo Mensual de CXP
    @Override
    public ArrayList<HashMap<String, String>> getCxc_DatosReporteSaldoMensual(Integer tipo_reporte,String cliente, String fecha_corte,Integer id_empresa) {
        
        String sql_to_query = "select * from cxc_reporte_saldo_mensual(?,?,?,?) as foo(id_cliente integer, cliente character varying, serie_folio character varying, fecha_factura character varying, moneda_id integer, moneda_abr character varying, moneda_simbolo character varying, orden_compra character varying, monto_factura double precision, importe_pagado double precision, saldo_factura double precision) ORDER BY cliente, moneda_id, serie_folio;"; 
        
        //System.out.println("CxC_DatosReporteSaldoMensual:: "+sql_to_query);
        ArrayList<HashMap<String, String>> hm_facturas = (ArrayList<HashMap<String, String>>) this.jdbcTemplate.query(
            sql_to_query,
            new Object[]{new Integer(tipo_reporte), new String (cliente), new String(fecha_corte), new Integer(id_empresa)}, new RowMapper(){
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    HashMap<String, String> row = new HashMap<String, String>();
                    row.put("cliente",rs.getString("cliente"));
                    row.put("moneda_abr",rs.getString("moneda_abr"));
                    row.put("moneda_simbolo",rs.getString("moneda_simbolo"));
                    row.put("serie_folio",rs.getString("serie_folio"));
                    row.put("moneda_id",rs.getString("moneda_id"));
                    row.put("orden_compra",rs.getString("orden_compra"));
                    row.put("fecha_factura",rs.getString("fecha_factura"));
                    row.put("monto_factura",StringHelper.roundDouble(rs.getDouble("monto_factura"), 2));
                    row.put("importe_pagado",StringHelper.roundDouble(rs.getDouble("importe_pagado"), 2));
                    row.put("ultimo_pago","&nbsp;&nbsp;&nbsp;&nbsp;/&nbsp;&nbsp;&nbsp;&nbsp;/&nbsp;&nbsp;&nbsp;&nbsp;");
                    row.put("saldo_factura",StringHelper.roundDouble(rs.getDouble("saldo_factura"), 2));
                    return row;
                }
            }
        );
        return hm_facturas;
    }
    //Termina Metodo para reporte de Saldo Mensual
    
    
    
    //Obtiene el SQL Consulta Reporte Ventas Anuales por Cliente
    @Override
    public ArrayList<HashMap<String, String>> getVentasAnualesCliente(Integer anio, Integer id_empresa) {

        String where="";
       String sql_to_query=""+"SELECT razon_social, "
                                    +"sum(enero) as enero, "
                                    +"sum(febrero) as febrero, "
                                    +"sum(marzo) as marzo, "
                                    +"sum(abril) as abril, "
                                    +"sum(mayo) as mayo, "
                                    +"sum(junio) as junio, "
                                    +"sum(julio) as julio, "
                                    +"sum(agosto) as agosto, "
                                    +"sum(septiembre) as septiembre, "
                                    +"sum(octubre) as octubre, "
                                    +"sum(noviembre) as noviembre, "
                                    +"sum(diciembre) as diciembre, "
                                    +"sum(enero)+sum(febrero)+sum(marzo)+sum(abril)+sum(mayo)+sum(junio)+sum(julio)+sum(agosto)+sum(septiembre)+sum(octubre)+sum(noviembre)+sum(diciembre)  as suma_total, "
                                    +"sum(enero1) as enero1, "
                                    +"sum(febrero2) as febrero2, "
                                    +"sum(marzo3) as marzo3, "
                                    +"sum(abril4) as abril4, "
                                    +"sum(mayo5) as mayo5, "
                                    +"sum(junio6) as junio6, "
                                    +"sum(julio7) as julio7, "
                                    +"sum(agosto8) as agosto8, "
                                    +"sum(septiembre9) as septiembre9, "
                                    +"sum(octubre10) as octubre10, "
                                    +"sum(noviembre11) as noviembre11, "
                                    +"sum(diciembre12) as diciembre12, "
                                    +"sum(enero1)+sum(febrero2)+sum(marzo3)+sum(abril4)+sum(mayo5)+sum(junio6)+sum(julio7)+sum(agosto8)+sum(septiembre9)+sum(octubre10)+sum(noviembre11)+sum(diciembre12)  as suma_total2, "
                                    +"sum(enero11) as enero11, "
                                    +"sum(febrero12) as febrero12, "
                                    +"sum(marzo13) as marzo13, "
                                    +"sum(abril14) as abril14, "
                                    +"sum(mayo15) as mayo15, "
                                    +"sum(junio16) as junio16, "
                                    +"sum(julio17) as julio17, "
                                    +"sum(agosto18) as agosto18, "
                                    +"sum(septiembre19) as septiembre19, "
                                    +"sum(octubre110) as octubre110, "
                                    +"sum(noviembre111) as noviembre111, "
                                    +"sum(diciembre112) as diciembre112, "
                                    +"sum(enero11)+sum(febrero12)+sum(marzo13)+sum(abril14)+sum(mayo15)+sum(junio16)+sum(julio17)+sum(agosto18)+sum(septiembre19)+sum(octubre110)+sum(noviembre111)+sum(diciembre112)  as suma_total3 "
                                    +"FROM( SELECT razon_social, "
                                        +"(CASE WHEN mes=1 THEN importe_partida_mn ELSE 0 END) AS enero, "
                                        +"(CASE WHEN mes=2 THEN importe_partida_mn ELSE 0 END) AS febrero, "
                                        +"(CASE WHEN mes=3 THEN importe_partida_mn ELSE 0 END) AS marzo, "
                                        +"(CASE WHEN mes=4 THEN importe_partida_mn ELSE 0 END) AS abril, "
                                        +"(CASE WHEN mes=5 THEN importe_partida_mn ELSE 0 END) AS mayo, "
                                        +"(CASE WHEN mes=6 THEN importe_partida_mn ELSE 0 END) AS junio, "
                                        +"(CASE WHEN mes=7 THEN importe_partida_mn ELSE 0 END) AS julio, "
                                        +"(CASE WHEN mes=8 THEN importe_partida_mn ELSE 0 END) AS agosto, "
                                        +"(CASE WHEN mes=9 THEN importe_partida_mn ELSE 0 END) AS septiembre, "
                                        +"(CASE WHEN mes=10 THEN importe_partida_mn ELSE 0 END) AS octubre, "
                                        +"(CASE WHEN mes=11 THEN importe_partida_mn ELSE 0 END) AS noviembre, "
                                        +"(CASE WHEN mes=12 THEN importe_partida_mn ELSE 0 END) AS diciembre, "
                                        +"importe_partida_mn, "
                                        +"(CASE WHEN mes=1 THEN cantidad_partida ELSE 0 END) AS enero1, "
                                        +"(CASE WHEN mes=2 THEN cantidad_partida ELSE 0 END) AS febrero2, "
                                        +"(CASE WHEN mes=3 THEN cantidad_partida ELSE 0 END) AS marzo3, "
                                        +"(CASE WHEN mes=4 THEN cantidad_partida ELSE 0 END) AS abril4, "
                                        +"(CASE WHEN mes=5 THEN cantidad_partida ELSE 0 END) AS mayo5, "
                                        +"(CASE WHEN mes=6 THEN cantidad_partida ELSE 0 END) AS junio6, "
                                        +"(CASE WHEN mes=7 THEN cantidad_partida ELSE 0 END) AS julio7, "
                                        +"(CASE WHEN mes=8 THEN cantidad_partida ELSE 0 END) AS agosto8, "
                                        +"(CASE WHEN mes=9 THEN cantidad_partida ELSE 0 END) AS septiembre9, "
                                        +"(CASE WHEN mes=10 THEN cantidad_partida ELSE 0 END) AS octubre10, "
                                        +"(CASE WHEN mes=11 THEN cantidad_partida ELSE 0 END) AS noviembre11, "
                                        +"(CASE WHEN mes=12 THEN cantidad_partida ELSE 0 END) AS diciembre12, "
                                        +"cantidad_partida, "
                                        +"(CASE WHEN mes=1 THEN (1-costo_mn/precio_unitario_partida) ELSE 0 END) AS enero11, "
                                        +"(CASE WHEN mes=2 THEN (1-costo_mn/precio_unitario_partida) ELSE 0 END) AS febrero12, "
                                        +"(CASE WHEN mes=3 THEN (1-costo_mn/precio_unitario_partida) ELSE 0 END) AS marzo13, "
                                        +"(CASE WHEN mes=4 THEN (1-costo_mn/precio_unitario_partida) ELSE 0 END) AS abril14, "
                                        +"(CASE WHEN mes=5 THEN (1-costo_mn/precio_unitario_partida) ELSE 0 END) AS mayo15, "
                                        +"(CASE WHEN mes=6 THEN (1-costo_mn/precio_unitario_partida) ELSE 0 END) AS junio16, "
                                        +"(CASE WHEN mes=7 THEN (1-costo_mn/precio_unitario_partida) ELSE 0 END) AS julio17, "
                                        +"(CASE WHEN mes=8 THEN (1-costo_mn/precio_unitario_partida) ELSE 0 END) AS agosto18, "
                                        +"(CASE WHEN mes=9 THEN (1-costo_mn/precio_unitario_partida) ELSE 0 END) AS septiembre19, "
                                        +"(CASE WHEN mes=10 THEN (1-costo_mn/precio_unitario_partida) ELSE 0 END) AS octubre110, "
                                        +"(CASE WHEN mes=11 THEN (1-costo_mn/precio_unitario_partida) ELSE 0 END) AS noviembre111, "
                                        +"(CASE WHEN mes=12 THEN (1-costo_mn/precio_unitario_partida) ELSE 0 END) AS diciembre112, "
                                        +"costo_mn "
                                    +"FROM ("
                                            +"SELECT EXTRACT(YEAR FROM fac_docs.momento_creacion) AS anio, "
               + "EXTRACT(MONTH FROM fac_docs.momento_creacion) AS mes, "
               + "cxc_clie.razon_social as razon_social, "
               + "fac_docs.serie_folio AS factura, "
               + "gral_mon.descripcion_abr AS moneda_factura, "
               + "gral_mon.simbolo AS moneda_simbolo, "
               + "fac_docs.tipo_cambio, "
               + "fac_docs.momento_creacion AS momento_facturacion, "
               + "fac_docs.subtotal AS subtotal_factura, "
               + "(CASE WHEN gral_mon.id=1 THEN fac_docs.subtotal ELSE (fac_docs.subtotal * fac_docs.tipo_cambio) END) AS subtotal_mn, "
               + "to_date(fac_docs.momento_creacion::text,'yyyy-mm-dd') AS momento_creacion, "
               + "inv_prod.sku AS codigo_prod, "
               + "inv_prod.descripcion AS descripcion_prod, "
               + "fac_docs_detalles.cantidad AS cantidad_partida, "
               + "(CASE WHEN gral_mon.id=1 THEN fac_docs_detalles.precio_unitario ELSE (fac_docs_detalles.precio_unitario * fac_docs.tipo_cambio) END) AS precio_unitario_partida, "
               + "(CASE WHEN gral_mon.id=1 THEN (fac_docs_detalles.cantidad * fac_docs_detalles.precio_unitario) ELSE ((fac_docs_detalles.cantidad * fac_docs_detalles.precio_unitario) * fac_docs.tipo_cambio) END) AS importe_partida_mn, "
               + "(CASE  "
               + "WHEN EXTRACT(MONTH FROM fac_docs.momento_creacion)::integer=1 THEN (CASE WHEN inv_prod_cost_prom.costo_ultimo_1 IS NULL THEN 0 ELSE ((CASE WHEN inv_prod_cost_prom.gral_mon_id_1=1 THEN inv_prod_cost_prom.costo_ultimo_1 ELSE inv_prod_cost_prom.costo_ultimo_1 * inv_prod_cost_prom.tipo_cambio_1 END) + (CASE WHEN sbt_costos.costo_adic_1 IS NULL THEN 0 ELSE (CASE WHEN inv_prod_cost_prom.gral_mon_id_1=1 THEN sbt_costos.costo_adic_1 ELSE sbt_costos.costo_adic_1 * inv_prod_cost_prom.tipo_cambio_1 END) END) + ((CASE WHEN inv_prod_cost_prom.gral_mon_id_1=1 THEN inv_prod_cost_prom.costo_ultimo_1 ELSE inv_prod_cost_prom.costo_ultimo_1 * inv_prod_cost_prom.tipo_cambio_1 END) *  ((CASE WHEN sbt_costos.costo_imp_1 IS NULL THEN 0 ELSE sbt_costos.costo_imp_1 END) /100)) + ((CASE WHEN inv_prod_cost_prom.gral_mon_id_1=1 THEN inv_prod_cost_prom.costo_ultimo_1 ELSE inv_prod_cost_prom.costo_ultimo_1 * inv_prod_cost_prom.tipo_cambio_1 END) *  ((CASE WHEN sbt_costos.costo_dir_1 IS NULL THEN 0 ELSE sbt_costos.costo_dir_1 END) /100))) END) "
               + "WHEN EXTRACT(MONTH FROM fac_docs.momento_creacion)::integer=2 THEN (CASE WHEN inv_prod_cost_prom.costo_ultimo_2 IS NULL THEN 0 ELSE ((CASE WHEN inv_prod_cost_prom.gral_mon_id_2=1 THEN inv_prod_cost_prom.costo_ultimo_2 ELSE inv_prod_cost_prom.costo_ultimo_2 * inv_prod_cost_prom.tipo_cambio_2 END) + (CASE WHEN sbt_costos.costo_adic_2 IS NULL THEN 0 ELSE (CASE WHEN inv_prod_cost_prom.gral_mon_id_2=1 THEN sbt_costos.costo_adic_2 ELSE sbt_costos.costo_adic_2 * inv_prod_cost_prom.tipo_cambio_2 END) END) + ((CASE WHEN inv_prod_cost_prom.gral_mon_id_2=1 THEN inv_prod_cost_prom.costo_ultimo_2 ELSE inv_prod_cost_prom.costo_ultimo_2 * inv_prod_cost_prom.tipo_cambio_2 END) *  ((CASE WHEN sbt_costos.costo_imp_2 IS NULL THEN 0 ELSE sbt_costos.costo_imp_2 END) /100)) + ((CASE WHEN inv_prod_cost_prom.gral_mon_id_2=1 THEN inv_prod_cost_prom.costo_ultimo_2 ELSE inv_prod_cost_prom.costo_ultimo_2 * inv_prod_cost_prom.tipo_cambio_2 END) *  ((CASE WHEN sbt_costos.costo_dir_2 IS NULL THEN 0 ELSE sbt_costos.costo_dir_2 END) /100))) END) "
               + "WHEN EXTRACT(MONTH FROM fac_docs.momento_creacion)::integer=3 THEN (CASE WHEN inv_prod_cost_prom.costo_ultimo_3 IS NULL THEN 0 ELSE ((CASE WHEN inv_prod_cost_prom.gral_mon_id_3=1 THEN inv_prod_cost_prom.costo_ultimo_3 ELSE inv_prod_cost_prom.costo_ultimo_3 * inv_prod_cost_prom.tipo_cambio_3 END) + (CASE WHEN sbt_costos.costo_adic_3 IS NULL THEN 0 ELSE (CASE WHEN inv_prod_cost_prom.gral_mon_id_3=1 THEN sbt_costos.costo_adic_3 ELSE sbt_costos.costo_adic_3 * inv_prod_cost_prom.tipo_cambio_3 END) END) + ((CASE WHEN inv_prod_cost_prom.gral_mon_id_3=1 THEN inv_prod_cost_prom.costo_ultimo_3 ELSE inv_prod_cost_prom.costo_ultimo_3 * inv_prod_cost_prom.tipo_cambio_3 END) *  ((CASE WHEN sbt_costos.costo_imp_3 IS NULL THEN 0 ELSE sbt_costos.costo_imp_3 END) /100)) + ((CASE WHEN inv_prod_cost_prom.gral_mon_id_3=1 THEN inv_prod_cost_prom.costo_ultimo_3 ELSE inv_prod_cost_prom.costo_ultimo_3 * inv_prod_cost_prom.tipo_cambio_3 END) *  ((CASE WHEN sbt_costos.costo_dir_3 IS NULL THEN 0 ELSE sbt_costos.costo_dir_3 END) /100))) END) "
               + "WHEN EXTRACT(MONTH FROM fac_docs.momento_creacion)::integer=4 THEN (CASE WHEN inv_prod_cost_prom.costo_ultimo_4 IS NULL THEN 0 ELSE ((CASE WHEN inv_prod_cost_prom.gral_mon_id_4=1 THEN inv_prod_cost_prom.costo_ultimo_4 ELSE inv_prod_cost_prom.costo_ultimo_4 * inv_prod_cost_prom.tipo_cambio_4 END) + (CASE WHEN sbt_costos.costo_adic_4 IS NULL THEN 0 ELSE (CASE WHEN inv_prod_cost_prom.gral_mon_id_4=1 THEN sbt_costos.costo_adic_4 ELSE sbt_costos.costo_adic_4 * inv_prod_cost_prom.tipo_cambio_4 END) END) + ((CASE WHEN inv_prod_cost_prom.gral_mon_id_4=1 THEN inv_prod_cost_prom.costo_ultimo_4 ELSE inv_prod_cost_prom.costo_ultimo_4 * inv_prod_cost_prom.tipo_cambio_4 END) *  ((CASE WHEN sbt_costos.costo_imp_4 IS NULL THEN 0 ELSE sbt_costos.costo_imp_4 END) /100)) + ((CASE WHEN inv_prod_cost_prom.gral_mon_id_4=1 THEN inv_prod_cost_prom.costo_ultimo_4 ELSE inv_prod_cost_prom.costo_ultimo_4 * inv_prod_cost_prom.tipo_cambio_4 END) *  ((CASE WHEN sbt_costos.costo_dir_4 IS NULL THEN 0 ELSE sbt_costos.costo_dir_4 END) /100))) END) "
               + "WHEN EXTRACT(MONTH FROM fac_docs.momento_creacion)::integer=5 THEN (CASE WHEN inv_prod_cost_prom.costo_ultimo_5 IS NULL THEN 0 ELSE ((CASE WHEN inv_prod_cost_prom.gral_mon_id_5=1 THEN inv_prod_cost_prom.costo_ultimo_5 ELSE inv_prod_cost_prom.costo_ultimo_5 * inv_prod_cost_prom.tipo_cambio_5 END) + (CASE WHEN sbt_costos.costo_adic_5 IS NULL THEN 0 ELSE (CASE WHEN inv_prod_cost_prom.gral_mon_id_5=1 THEN sbt_costos.costo_adic_5 ELSE sbt_costos.costo_adic_5 * inv_prod_cost_prom.tipo_cambio_5 END) END) + ((CASE WHEN inv_prod_cost_prom.gral_mon_id_5=1 THEN inv_prod_cost_prom.costo_ultimo_5 ELSE inv_prod_cost_prom.costo_ultimo_5 * inv_prod_cost_prom.tipo_cambio_5 END) *  ((CASE WHEN sbt_costos.costo_imp_5 IS NULL THEN 0 ELSE sbt_costos.costo_imp_5 END) /100)) + ((CASE WHEN inv_prod_cost_prom.gral_mon_id_5=1 THEN inv_prod_cost_prom.costo_ultimo_5 ELSE inv_prod_cost_prom.costo_ultimo_5 * inv_prod_cost_prom.tipo_cambio_5 END) *  ((CASE WHEN sbt_costos.costo_dir_5 IS NULL THEN 0 ELSE sbt_costos.costo_dir_5 END) /100))) END) "
               + "WHEN EXTRACT(MONTH FROM fac_docs.momento_creacion)::integer=6 THEN (CASE WHEN inv_prod_cost_prom.costo_ultimo_6 IS NULL THEN 0 ELSE ((CASE WHEN inv_prod_cost_prom.gral_mon_id_6=1 THEN inv_prod_cost_prom.costo_ultimo_6 ELSE inv_prod_cost_prom.costo_ultimo_6 * inv_prod_cost_prom.tipo_cambio_6 END) + (CASE WHEN sbt_costos.costo_adic_6 IS NULL THEN 0 ELSE (CASE WHEN inv_prod_cost_prom.gral_mon_id_6=1 THEN sbt_costos.costo_adic_6 ELSE sbt_costos.costo_adic_6 * inv_prod_cost_prom.tipo_cambio_6 END) END) + ((CASE WHEN inv_prod_cost_prom.gral_mon_id_6=1 THEN inv_prod_cost_prom.costo_ultimo_6 ELSE inv_prod_cost_prom.costo_ultimo_6 * inv_prod_cost_prom.tipo_cambio_6 END) *  ((CASE WHEN sbt_costos.costo_imp_6 IS NULL THEN 0 ELSE sbt_costos.costo_imp_6 END) /100)) + ((CASE WHEN inv_prod_cost_prom.gral_mon_id_6=1 THEN inv_prod_cost_prom.costo_ultimo_6 ELSE inv_prod_cost_prom.costo_ultimo_6 * inv_prod_cost_prom.tipo_cambio_6 END) *  ((CASE WHEN sbt_costos.costo_dir_6 IS NULL THEN 0 ELSE sbt_costos.costo_dir_6 END) /100))) END) "
               + "WHEN EXTRACT(MONTH FROM fac_docs.momento_creacion)::integer=7 THEN (CASE WHEN inv_prod_cost_prom.costo_ultimo_7 IS NULL THEN 0 ELSE ((CASE WHEN inv_prod_cost_prom.gral_mon_id_7=1 THEN inv_prod_cost_prom.costo_ultimo_7 ELSE inv_prod_cost_prom.costo_ultimo_7 * inv_prod_cost_prom.tipo_cambio_7 END) + (CASE WHEN sbt_costos.costo_adic_7 IS NULL THEN 0 ELSE (CASE WHEN inv_prod_cost_prom.gral_mon_id_7=1 THEN sbt_costos.costo_adic_7 ELSE sbt_costos.costo_adic_7 * inv_prod_cost_prom.tipo_cambio_7 END) END) + ((CASE WHEN inv_prod_cost_prom.gral_mon_id_7=1 THEN inv_prod_cost_prom.costo_ultimo_7 ELSE inv_prod_cost_prom.costo_ultimo_7 * inv_prod_cost_prom.tipo_cambio_7 END) *  ((CASE WHEN sbt_costos.costo_imp_7 IS NULL THEN 0 ELSE sbt_costos.costo_imp_7 END) /100)) + ((CASE WHEN inv_prod_cost_prom.gral_mon_id_7=1 THEN inv_prod_cost_prom.costo_ultimo_7 ELSE inv_prod_cost_prom.costo_ultimo_7 * inv_prod_cost_prom.tipo_cambio_7 END) *  ((CASE WHEN sbt_costos.costo_dir_7 IS NULL THEN 0 ELSE sbt_costos.costo_dir_7 END) /100))) END) "
               + "WHEN EXTRACT(MONTH FROM fac_docs.momento_creacion)::integer=8 THEN (CASE WHEN inv_prod_cost_prom.costo_ultimo_8 IS NULL THEN 0 ELSE ((CASE WHEN inv_prod_cost_prom.gral_mon_id_8=1 THEN inv_prod_cost_prom.costo_ultimo_8 ELSE inv_prod_cost_prom.costo_ultimo_8 * inv_prod_cost_prom.tipo_cambio_8 END) + (CASE WHEN sbt_costos.costo_adic_8 IS NULL THEN 0 ELSE (CASE WHEN inv_prod_cost_prom.gral_mon_id_8=1 THEN sbt_costos.costo_adic_8 ELSE sbt_costos.costo_adic_8 * inv_prod_cost_prom.tipo_cambio_8 END) END) + ((CASE WHEN inv_prod_cost_prom.gral_mon_id_8=1 THEN inv_prod_cost_prom.costo_ultimo_8 ELSE inv_prod_cost_prom.costo_ultimo_8 * inv_prod_cost_prom.tipo_cambio_8 END) *  ((CASE WHEN sbt_costos.costo_imp_8 IS NULL THEN 0 ELSE sbt_costos.costo_imp_8 END) /100)) + ((CASE WHEN inv_prod_cost_prom.gral_mon_id_8=1 THEN inv_prod_cost_prom.costo_ultimo_8 ELSE inv_prod_cost_prom.costo_ultimo_8 * inv_prod_cost_prom.tipo_cambio_8 END) *  ((CASE WHEN sbt_costos.costo_dir_8 IS NULL THEN 0 ELSE sbt_costos.costo_dir_8 END) /100))) END) "
               + "WHEN EXTRACT(MONTH FROM fac_docs.momento_creacion)::integer=9 THEN (CASE WHEN inv_prod_cost_prom.costo_ultimo_9 IS NULL THEN 0 ELSE ((CASE WHEN inv_prod_cost_prom.gral_mon_id_9=1 THEN inv_prod_cost_prom.costo_ultimo_9 ELSE inv_prod_cost_prom.costo_ultimo_9 * inv_prod_cost_prom.tipo_cambio_9 END) + (CASE WHEN sbt_costos.costo_adic_9 IS NULL THEN 0 ELSE (CASE WHEN inv_prod_cost_prom.gral_mon_id_9=1 THEN sbt_costos.costo_adic_9 ELSE sbt_costos.costo_adic_9 * inv_prod_cost_prom.tipo_cambio_9 END) END) + ((CASE WHEN inv_prod_cost_prom.gral_mon_id_9=1 THEN inv_prod_cost_prom.costo_ultimo_9 ELSE inv_prod_cost_prom.costo_ultimo_9 * inv_prod_cost_prom.tipo_cambio_9 END) *  ((CASE WHEN sbt_costos.costo_imp_9 IS NULL THEN 0 ELSE sbt_costos.costo_imp_9 END) /100)) + ((CASE WHEN inv_prod_cost_prom.gral_mon_id_9=1 THEN inv_prod_cost_prom.costo_ultimo_9 ELSE inv_prod_cost_prom.costo_ultimo_9 * inv_prod_cost_prom.tipo_cambio_9 END) *  ((CASE WHEN sbt_costos.costo_dir_9 IS NULL THEN 0 ELSE sbt_costos.costo_dir_9 END) /100))) END) "
               + "WHEN EXTRACT(MONTH FROM fac_docs.momento_creacion)::integer=10 THEN (CASE WHEN inv_prod_cost_prom.costo_ultimo_10 IS NULL THEN 0 ELSE ((CASE WHEN inv_prod_cost_prom.gral_mon_id_10=1 THEN inv_prod_cost_prom.costo_ultimo_10 ELSE inv_prod_cost_prom.costo_ultimo_10 * inv_prod_cost_prom.tipo_cambio_10 END) + (CASE WHEN sbt_costos.costo_adic_10 IS NULL THEN 0 ELSE (CASE WHEN inv_prod_cost_prom.gral_mon_id_10=1 THEN sbt_costos.costo_adic_10 ELSE sbt_costos.costo_adic_10 * inv_prod_cost_prom.tipo_cambio_10 END) END) + ((CASE WHEN inv_prod_cost_prom.gral_mon_id_10=1 THEN inv_prod_cost_prom.costo_ultimo_10 ELSE inv_prod_cost_prom.costo_ultimo_10 * inv_prod_cost_prom.tipo_cambio_10 END) * ((CASE WHEN sbt_costos.costo_imp_10 IS NULL THEN 0 ELSE sbt_costos.costo_imp_10 END) /100)) + ((CASE WHEN inv_prod_cost_prom.gral_mon_id_10=1 THEN inv_prod_cost_prom.costo_ultimo_10 ELSE inv_prod_cost_prom.costo_ultimo_10 * inv_prod_cost_prom.tipo_cambio_10 END) *  ((CASE WHEN sbt_costos.costo_dir_10 IS NULL THEN 0 ELSE sbt_costos.costo_dir_10 END) /100))) END) "
               + "WHEN EXTRACT(MONTH FROM fac_docs.momento_creacion)::integer=11 THEN (CASE WHEN inv_prod_cost_prom.costo_ultimo_11 IS NULL THEN 0 ELSE ((CASE WHEN inv_prod_cost_prom.gral_mon_id_11=1 THEN inv_prod_cost_prom.costo_ultimo_11 ELSE inv_prod_cost_prom.costo_ultimo_11 * inv_prod_cost_prom.tipo_cambio_11 END) + (CASE WHEN sbt_costos.costo_adic_11 IS NULL THEN 0 ELSE (CASE WHEN inv_prod_cost_prom.gral_mon_id_11=1 THEN sbt_costos.costo_adic_11 ELSE sbt_costos.costo_adic_11 * inv_prod_cost_prom.tipo_cambio_11 END) END) + ((CASE WHEN inv_prod_cost_prom.gral_mon_id_11=1 THEN inv_prod_cost_prom.costo_ultimo_11 ELSE inv_prod_cost_prom.costo_ultimo_11 * inv_prod_cost_prom.tipo_cambio_11 END) * ((CASE WHEN sbt_costos.costo_imp_11 IS NULL THEN 0 ELSE sbt_costos.costo_imp_11 END) /100)) + ((CASE WHEN inv_prod_cost_prom.gral_mon_id_11=1 THEN inv_prod_cost_prom.costo_ultimo_11 ELSE inv_prod_cost_prom.costo_ultimo_11 * inv_prod_cost_prom.tipo_cambio_11 END) *  ((CASE WHEN sbt_costos.costo_dir_11 IS NULL THEN 0 ELSE sbt_costos.costo_dir_11 END) /100))) END) "
               + "WHEN EXTRACT(MONTH FROM fac_docs.momento_creacion)::integer=12 THEN (CASE WHEN inv_prod_cost_prom.costo_ultimo_12 IS NULL THEN 0 ELSE ((CASE WHEN inv_prod_cost_prom.gral_mon_id_12=1 THEN inv_prod_cost_prom.costo_ultimo_12 ELSE inv_prod_cost_prom.costo_ultimo_12 * inv_prod_cost_prom.tipo_cambio_12 END) + (CASE WHEN sbt_costos.costo_adic_12 IS NULL THEN 0 ELSE (CASE WHEN inv_prod_cost_prom.gral_mon_id_12=1 THEN sbt_costos.costo_adic_12 ELSE sbt_costos.costo_adic_12 * inv_prod_cost_prom.tipo_cambio_12 END) END) + ((CASE WHEN inv_prod_cost_prom.gral_mon_id_12=1 THEN inv_prod_cost_prom.costo_ultimo_12 ELSE inv_prod_cost_prom.costo_ultimo_12 * inv_prod_cost_prom.tipo_cambio_12 END) * ((CASE WHEN sbt_costos.costo_imp_12 IS NULL THEN 0 ELSE sbt_costos.costo_imp_12 END) /100)) + ((CASE WHEN inv_prod_cost_prom.gral_mon_id_12=1 THEN inv_prod_cost_prom.costo_ultimo_12 ELSE inv_prod_cost_prom.costo_ultimo_12 * inv_prod_cost_prom.tipo_cambio_12 END) *  ((CASE WHEN sbt_costos.costo_dir_12 IS NULL THEN 0 ELSE sbt_costos.costo_dir_12 END) /100))) END) "
               + "ELSE 0 END) * fac_docs_detalles.cantidad  AS costo_mn "
               + "FROM fac_docs "
               + "JOIN fac_docs_detalles ON fac_docs_detalles.fac_doc_id=fac_docs.id "
               + "JOIN erp_proceso ON erp_proceso.id=fac_docs.proceso_id "
    + "JOIN inv_prod ON inv_prod.id=fac_docs_detalles.inv_prod_id "
    + "JOIN cxc_clie ON cxc_clie.id=fac_docs.cxc_clie_id "
    + "JOIN gral_mon ON gral_mon.id=fac_docs.moneda_id "
    + "LEFT JOIN ("
    + "SELECT distinct ano,inv_prod_id,inv_prod_presentacion_id,costo_imp_1,costo_dir_1,precio_min_1,actualizacion_1,usr_id_actualiza_1,costo_imp_2,costo_dir_2,precio_min_2,actualizacion_2,usr_id_actualiza_2,costo_imp_3,costo_dir_3,precio_min_3,actualizacion_3,usr_id_actualiza_3,costo_imp_4,costo_dir_4,precio_min_4,actualizacion_4,usr_id_actualiza_4,costo_imp_5,costo_dir_5,precio_min_5,actualizacion_5,usr_id_actualiza_5,costo_imp_6,costo_dir_6,precio_min_6,actualizacion_6,usr_id_actualiza_6,costo_imp_7,costo_dir_7,precio_min_7,actualizacion_7,usr_id_actualiza_7,costo_imp_8,costo_dir_8,precio_min_8,actualizacion_8,usr_id_actualiza_8,costo_imp_9,costo_dir_9,precio_min_9,actualizacion_9,usr_id_actualiza_9,costo_imp_10,costo_dir_10,precio_min_10,actualizacion_10,usr_id_actualiza_10,costo_imp_11,costo_dir_11,precio_min_11,actualizacion_11,usr_id_actualiza_11,costo_imp_12,costo_dir_12,precio_min_12,actualizacion_12,usr_id_actualiza_12,gral_emp_id,costo_adic_1,costo_adic_2,costo_adic_3,costo_adic_4,costo_adic_5,costo_adic_6,costo_adic_7,costo_adic_8,costo_adic_9,costo_adic_10, costo_adic_11, costo_adic_12 FROM inv_prod_costos "
    + " ) AS sbt_costos ON (sbt_costos.inv_prod_id=fac_docs_detalles.inv_prod_id AND sbt_costos.inv_prod_presentacion_id=fac_docs_detalles.inv_prod_presentacion_id AND sbt_costos.ano=EXTRACT(YEAR FROM fac_docs.momento_creacion)::integer ) "
    + "LEFT JOIN inv_prod_cost_prom ON (inv_prod_cost_prom.inv_prod_id=inv_prod.id AND inv_prod_cost_prom.ano=EXTRACT(YEAR FROM fac_docs.momento_creacion)::integer) "
    + "WHERE erp_proceso.empresa_id="+id_empresa+" AND fac_docs.cancelado=false AND EXTRACT(YEAR  FROM fac_docs.momento_creacion)="+anio+" "
    + ") AS sbt "
    +")as sbt2 "
    +"GROUP BY razon_social ";
        //System.out.println("Generando Consulta Reporte Ventas Anuales por Cliente:"+sql_to_query+"");

        ArrayList<HashMap<String, String>> hm_facturas = (ArrayList<HashMap<String, String>>) this.jdbcTemplate.query(
            sql_to_query,
            new Object[]{}, new RowMapper(){
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    HashMap<String, String> row = new HashMap<String, String>();
                    row.put("razon_social",rs.getString("razon_social"));
                    row.put("enero",StringHelper.roundDouble(rs.getString("enero"),2));
                    row.put("febrero",StringHelper.roundDouble(rs.getString("febrero"),2));
                    row.put("marzo",StringHelper.roundDouble(rs.getString("marzo"),2));
                    row.put("abril",StringHelper.roundDouble(rs.getString("abril"),2));
                    row.put("mayo",StringHelper.roundDouble(rs.getString("mayo"),2));
                    row.put("junio",StringHelper.roundDouble(rs.getString("junio"),2));
                    row.put("julio",StringHelper.roundDouble(rs.getString("julio"),2));
                    row.put("agosto",StringHelper.roundDouble(rs.getString("agosto"),2));
                    row.put("septiembre",StringHelper.roundDouble(rs.getString("septiembre"),2));
                    row.put("octubre",StringHelper.roundDouble(rs.getString("octubre"),2));
                    row.put("noviembre",StringHelper.roundDouble(rs.getString("noviembre"),2));
                    row.put("diciembre",StringHelper.roundDouble(rs.getString("diciembre"),2));
                    row.put("suma_total",StringHelper.roundDouble(rs.getDouble("suma_total"), 2));
                    row.put("enero1",StringHelper.roundDouble(rs.getString("enero1"),2));
                    row.put("febrero2",StringHelper.roundDouble(rs.getString("febrero2"),2));
                    row.put("marzo3",StringHelper.roundDouble(rs.getString("marzo3"),2));
                    row.put("abril4",StringHelper.roundDouble(rs.getString("abril4"),2));
                    row.put("mayo5",StringHelper.roundDouble(rs.getString("mayo5"),2));
                    row.put("junio6",StringHelper.roundDouble(rs.getString("junio6"),2));
                    row.put("julio7",StringHelper.roundDouble(rs.getString("julio7"),2));
                    row.put("agosto8",StringHelper.roundDouble(rs.getString("agosto8"),2));
                    row.put("septiembre9",StringHelper.roundDouble(rs.getString("septiembre9"),2));
                    row.put("octubre10",StringHelper.roundDouble(rs.getString("octubre10"),2));
                    row.put("noviembre11",StringHelper.roundDouble(rs.getString("noviembre11"),2));
                    row.put("diciembre12",StringHelper.roundDouble(rs.getString("diciembre12"),2));
                    row.put("suma_total2",StringHelper.roundDouble(rs.getDouble("suma_total2"), 2));
                    row.put("enero11",StringHelper.roundDouble(rs.getString("enero11"),2));
                    row.put("febrero12",StringHelper.roundDouble(rs.getString("febrero12"),2));
                    row.put("marzo13",StringHelper.roundDouble(rs.getString("marzo13"),2));
                    row.put("abril14",StringHelper.roundDouble(rs.getString("abril14"),2));
                    row.put("mayo15",StringHelper.roundDouble(rs.getString("mayo15"),2));
                    row.put("junio16",StringHelper.roundDouble(rs.getString("junio16"),2));
                    row.put("julio17",StringHelper.roundDouble(rs.getString("julio17"),2));
                    row.put("agosto18",StringHelper.roundDouble(rs.getString("agosto18"),2));
                    row.put("septiembre19",StringHelper.roundDouble(rs.getString("septiembre19"),2));
                    row.put("octubre110",StringHelper.roundDouble(rs.getString("octubre110"),2));
                    row.put("noviembre111",StringHelper.roundDouble(rs.getString("noviembre111"),2));
                    row.put("diciembre112",StringHelper.roundDouble(rs.getString("diciembre112"),2));
                    row.put("suma_total3",StringHelper.roundDouble(rs.getDouble("suma_total3"), 2));

                    return row;
                }
            }
        );
        return hm_facturas;
    }
    
    
    
//Metodo Descuentos de clientes para editar 
      @Override
    public ArrayList<HashMap<String, Object>> getClientstDescuentos_PaginaGrid(String data_string, int offset, int pageSize, String orderBy, String asc) {
        //throw new UnsupportedOperationException("Not supported yet.");
        String sql_busqueda = "select id from gral_bus_catalogos(?) as foo (id integer)";

	String sql_to_query = "SELECT cxc_clie_descto.id, cxc_clie.razon_social as cliente,cxc_clie_descto.tipo,cxc_clie_descto.valor "
                               + " FROM cxc_clie_descto "
                               + " JOIN cxc_clie on cxc_clie.id= cxc_clie_descto.cxc_clie_id "
                        +"JOIN ("+sql_busqueda+") AS sbt ON sbt.id = cxc_clie_descto.id "
                        +"order by "+orderBy+" "+asc+" limit ? OFFSET ?";

        //System.out.println("Busqueda GetPage: "+sql_to_query);
        ArrayList<HashMap<String, Object>> hm = (ArrayList<HashMap<String, Object>>) this.jdbcTemplate.query(
            sql_to_query,
            new Object[]{new String(data_string), new Integer(pageSize),new Integer(offset)}, new RowMapper() {
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    HashMap<String, Object> row = new HashMap<String, Object>();
                    row.put("id",rs.getInt("id"));
                    row.put("cliente",rs.getString("cliente"));
                    row.put("tipo",rs.getString("tipo"));
                    row.put("valor",StringHelper.roundDouble(rs.getString("valor"),4));
                    return row;
                }
            }
        );
        return hm;
    }
     //Termina Metodo Descuentos de clientes para editar 



    //Obtiene los Descuentos asignados a los clientes.
    @Override
    public ArrayList<HashMap<String, Object>> getClientstDescuentos_Datos(Integer id) {
        String sql_query = "SELECT cxc_clie_descto.id, cxc_clie.numero_control,cxc_clie.razon_social,cxc_clie_descto.cxc_clie_id,cxc_clie_descto.tipo,cxc_clie_descto.valor "
                + " FROM cxc_clie_descto "
                +" LEFT JOIN cxc_clie on cxc_clie.id= cxc_clie_descto.cxc_clie_id "
                + "WHERE cxc_clie_descto.id =" +id;
        //System.out.print(sql_query);
        ArrayList<HashMap<String, Object>> hm = (ArrayList<HashMap<String, Object>>) this.jdbcTemplate.query(
            sql_query,
            new Object[]{}, new RowMapper() {
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    HashMap<String, String> row = new HashMap<String, String>();
                    row.put("id",String.valueOf(rs.getInt("id")));
                    row.put("cxc_clie_id",rs.getString("cxc_clie_id"));
                    row.put("numero_control",rs.getString("numero_control"));
                    row.put("razon_social",rs.getString("razon_social"));
                    row.put("tipo",rs.getString("tipo"));
                    row.put("valor",StringHelper.roundDouble(rs.getString("valor"),4));
                    
                    return row;
                }
            }
        );
        return hm;
    }
    //Termina Metodo Descuentos asignados a los clientes.
    
    
    @Override
    public ArrayList<HashMap<String, String>> getDatosReporteIepsCobrado(ArrayList<HashMap<String, String>> listaIeps, String ciente, String finicial, String ffinal, Integer id_empresa) {
        final ArrayList<HashMap<String, String>> tiposIeps = listaIeps;
        String condiciones="";
        String campos1="";
        String campos2="";
        
        if(!ciente.trim().equals("")){
            condiciones += condiciones + " and cxc_clie.razon_social ilike '%"+ciente+"%'";
        }
        
        //Crear nombres de campos dinamicamente
        for( HashMap<String,String> i : tiposIeps ){
            campos1 += ",sum(ieps"+i.get("id")+") as ieps"+i.get("id")+" ";
            campos2 += ",(CASE WHEN fac_det.gral_ieps_id="+i.get("id")+" THEN (CASE WHEN fac_det.gral_ieps_id>0 THEN ((fac_det.cantidad * (fac_det.precio_unitario * fac_docs.tipo_cambio)) * fac_det.valor_ieps) ELSE 0 END) ELSE 0 END) AS ieps"+i.get("id")+" ";
        }
        
        String sql_to_query = ""
        + "SELECT "
            + "fecha_ultimo_pago"
            + ",fecha_pago"
            + ",cliente"
            + ",fecha "
            + ",factura "
            + ",moneda_fac "
            + ",subtotal "
            + ",retencion"
            + ",iva"
            + ",total"
            + campos1
        + "from ( "
            + "select "
                + "erp_h_facturas.fecha_ultimo_pago"
                + ",cxc_clie.razon_social as cliente"
                + ",to_char(erp_h_facturas.fecha_ultimo_pago, 'dd/mm/yyyy') as fecha_pago"
                + ",to_char(fac_docs.momento_creacion, 'dd/mm/yyyy') as fecha"
                + ",fac_docs.serie_folio as factura"
                + ",gral_mon.descripcion_abr as moneda_fac "
                + ",(fac_docs.subtotal * fac_docs.tipo_cambio) as subtotal"
                + ",(fac_docs.monto_retencion * fac_docs.tipo_cambio) as retencion"
                + ",(fac_docs.impuesto * fac_docs.tipo_cambio) as iva"
                + ",(fac_docs.total * fac_docs.tipo_cambio) as total"
                + campos2 
            + "from fac_docs "
            + "join fac_docs_detalles as fac_det on fac_det.fac_doc_id=fac_docs.id "
            + "join erp_h_facturas on (erp_h_facturas.serie_folio=fac_docs.serie_folio and erp_h_facturas.cliente_id=fac_docs.cxc_clie_id and erp_h_facturas.pagado=true and erp_h_facturas.cancelacion=false) "
            + "join cxc_clie on cxc_clie.id=fac_docs.cxc_clie_id "
            + "join gral_mon on gral_mon.id=fac_docs.moneda_id "
            + "where fac_det.gral_ieps_id>0 and cxc_clie.borrado_logico=false and cxc_clie.empresa_id=? "+condiciones +" "
            +" AND (to_char(erp_h_facturas.fecha_ultimo_pago,'yyyymmdd')::integer BETWEEN to_char('"+finicial+"'::timestamp with time zone,'yyyymmdd')::integer AND to_char('"+ffinal+"'::timestamp with time zone,'yyyymmdd')::integer) "
        + ") as sbt "
        + "group by fecha_ultimo_pago, cliente,fecha_pago,fecha, factura, moneda_fac, subtotal,retencion,iva,total "
        + "order by fecha_ultimo_pago"; 
        
        System.out.println("getDatosReporteIepsCobrado:: "+sql_to_query);
        
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
        
        String sql_to_query = "select id, titulo, tasa from gral_ieps where gral_emp_id=? and borrado_logico=false order by tasa;"; 
        
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
    
    
    
    @Override
    public ArrayList<HashMap<String, String>> getDatosCxcReporteIepsCobradoPorCliente(ArrayList<HashMap<String, String>> listaIeps, String ciente, String finicial, String ffinal, Integer id_empresa) {
        final ArrayList<HashMap<String, String>> tiposIeps = listaIeps;
        String condiciones="";
        String campos1="";
        String campos2="";
        
        if(!ciente.trim().equals("")){
            condiciones += condiciones + " and cxc_clie.razon_social ilike '%"+ciente+"%'";
        }
        
        //Crear nombres de campos dinamicamente
        for( HashMap<String,String> i : tiposIeps ){
            campos1 += ", sum(ieps"+i.get("id")+") as ieps"+i.get("id")+" ";
            campos2 += ", (CASE WHEN fac_det.gral_ieps_id="+i.get("id")+" THEN (CASE WHEN fac_det.gral_ieps_id>0 THEN ((fac_det.cantidad * (fac_det.precio_unitario * fac_docs.tipo_cambio)) * fac_det.valor_ieps) ELSE 0 END) ELSE 0 END) AS ieps"+i.get("id")+" ";
        }
        
        String sql_to_query = ""
        + "SELECT "
            + "no_cliente"
            + ", cliente"
            + ", sum(subtotal) as subtotal"
            + ", sum(retencion) as retencion"
            + ", sum(iva) as iva"
            + ", sum(total) as total"
            + campos1
        + "from ( "
            + "SELECT "
                + "no_cliente"
                + ", cliente"
                + ", serie_folio"
                + ", subtotal "
                + ", retencion"
                + ", iva"
                + ", total"
                + campos1
            + "from ( "
                + "select "
                    + "cxc_clie.numero_control as no_cliente"
                    + ",cxc_clie.razon_social as cliente"
                    + ",erp_h_facturas.serie_folio"
                    + ",(fac_docs.subtotal * fac_docs.tipo_cambio) as subtotal"
                    + ",(fac_docs.monto_retencion * fac_docs.tipo_cambio) as retencion"
                    + ",(fac_docs.impuesto * fac_docs.tipo_cambio) as iva"
                    + ",(fac_docs.total * fac_docs.tipo_cambio) as total"
                    + campos2 
                + "from fac_docs "
                + "join fac_docs_detalles as fac_det on fac_det.fac_doc_id=fac_docs.id "
                + "join erp_h_facturas on (erp_h_facturas.serie_folio=fac_docs.serie_folio and erp_h_facturas.cliente_id=fac_docs.cxc_clie_id and erp_h_facturas.pagado=true and erp_h_facturas.cancelacion=false) "
                + "join cxc_clie on cxc_clie.id=fac_docs.cxc_clie_id "
                + "join gral_mon on gral_mon.id=fac_docs.moneda_id "
                + "where fac_det.gral_ieps_id>0 and cxc_clie.borrado_logico=false and cxc_clie.empresa_id=? "+condiciones +" AND (to_char(erp_h_facturas.fecha_ultimo_pago,'yyyymmdd')::integer BETWEEN to_char('"+finicial+"'::timestamp with time zone,'yyyymmdd')::integer AND to_char('"+ffinal+"'::timestamp with time zone,'yyyymmdd')::integer) "
            + ") as sbt "
            + "group by no_cliente, cliente, serie_folio, subtotal,retencion,iva,total "
        + ") as sbt2 "
        + "group by no_cliente,cliente "
        + "order by cliente;"; 
        
        System.out.println("getDatosReporteIepsCobradoPorCliente:: "+sql_to_query);
        
        ArrayList<HashMap<String, String>> arraydata = (ArrayList<HashMap<String, String>>) this.jdbcTemplate.query(
            sql_to_query,
            new Object[]{new Integer(id_empresa)}, new RowMapper(){
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    HashMap<String, String> row = new HashMap<String, String>();
                    
                    row.put("cliente",rs.getString("cliente"));
                    row.put("moneda_simbolo_subtotal","$");
                    row.put("subtotal",StringHelper.AgregaComas(StringHelper.roundDouble(rs.getDouble("subtotal"), 2)));
                    row.put("moneda_simbolo_retencion","$");
                    row.put("retencion",StringHelper.AgregaComas(StringHelper.roundDouble(rs.getDouble("retencion"), 2)));
                    row.put("moneda_simbolo_iva","$");
                    row.put("iva",StringHelper.AgregaComas(StringHelper.roundDouble(rs.getDouble("iva"), 2)));
                    row.put("moneda_simbolo_total","$");
                    row.put("total",StringHelper.AgregaComas(StringHelper.roundDouble(rs.getDouble("total"), 2)));
                    
                    //Crear nombres de campos dinamicamente(diferentes tasas de ieps)
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
    
    
}
