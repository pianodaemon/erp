package com.agnux.kemikal.springdaos;

import com.agnux.common.helpers.StringHelper;
import com.agnux.kemikal.interfacedaos.CtbInterfaceDao;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;


public class CtbSpringDao implements CtbInterfaceDao{
    private JdbcTemplate jdbcTemplate;

    public JdbcTemplate getJdbcTemplate() {
        return jdbcTemplate;
    }

    public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }
    
    
    
    @Override
    public HashMap<String, String> selectFunctionValidateAaplicativo(String data, Integer idApp, String extra_data_array) {
        String sql_to_query = "select erp_fn_validaciones_por_aplicativo from erp_fn_validaciones_por_aplicativo('"+data+"',"+idApp+",array["+extra_data_array+"]);";

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
    public String selectFunctionForCtbAdmProcesos(String campos_data, String extra_data_array) {
        String sql_to_query = "select * from ctb_adm_procesos('"+campos_data+"',array["+extra_data_array+"]);";
        //System.out.println(sql_to_query);
        
        String valor_retorno="";
        Map<String, Object> update = this.getJdbcTemplate().queryForMap(sql_to_query);
        valor_retorno = update.get("ctb_adm_procesos").toString();
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
    public ArrayList<HashMap<String, Object>> getCentroCostos_PaginaGrid(String data_string, int offset, int pageSize, String orderBy, String asc) {
        String sql_busqueda = "select id from gral_bus_catalogos(?) as foo (id integer)";
        
	String sql_to_query = "SELECT   ctb_cc.id, ctb_cc.titulo, ctb_cc.descripcion FROM ctb_cc "
                        +"JOIN ("+sql_busqueda+") AS sbt ON sbt.id = ctb_cc.id "
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
                    row.put("descripcion",rs.getString("descripcion"));
                    return row;
                }
            }
        );
        return hm; 
    }
    
    
    
    
    @Override
    public ArrayList<HashMap<String, String>> getCentroCosto_Datos(Integer id) {
        String sql_query = "SELECT id,titulo,descripcion FROM ctb_cc WHERE id = ?;";
        ArrayList<HashMap<String, String>> hm = (ArrayList<HashMap<String, String>>) this.jdbcTemplate.query(
            sql_query,  
            new Object[]{new Integer(id)}, new RowMapper() {
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    HashMap<String, String> row = new HashMap<String, String>();
                    row.put("id",String.valueOf(rs.getInt("id")));
                    row.put("titulo",rs.getString("titulo"));
                    row.put("descripcion",rs.getString("descripcion"));
                    return row;
                }
            }
        );
        return hm;
    }
    
    
    
    
    
    
    @Override
    public ArrayList<HashMap<String, Object>> getTipoPolizas_PaginaGrid(String data_string, int offset, int pageSize, String orderBy, String asc) {
        String sql_busqueda = "select id from gral_bus_catalogos(?) as foo (id integer)";
        
	String sql_to_query = "SELECT ctb_tpol.id,ctb_tpol.tipo,ctb_tpol.titulo, ctb_tpol_grupos.titulo as grupo "
                            + "FROM ctb_tpol "
                            + "JOIN ctb_tpol_grupos ON ctb_tpol_grupos.id = ctb_tpol.ctb_tpol_grupo_id "
                            +"JOIN ("+sql_busqueda+") AS sbt ON sbt.id = ctb_tpol.id "
                            +"order by "+orderBy+" "+asc+" limit ? OFFSET ?";
        
        //System.out.println("getTipoPolizas_PaginaGrid: "+sql_to_query);
        
        ArrayList<HashMap<String, Object>> hm = (ArrayList<HashMap<String, Object>>) this.jdbcTemplate.query(
            sql_to_query, 
            new Object[]{data_string, new Integer(pageSize),new Integer(offset)}, new RowMapper() {
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    HashMap<String, Object> row = new HashMap<String, Object>();
                    row.put("id",rs.getInt("id"));
                    row.put("tipo",rs.getInt("tipo"));
                    row.put("titulo",rs.getString("titulo"));
                    row.put("grupo",rs.getString("grupo"));
                    return row;
                }
            }
        );
        return hm; 
    }
    
    
    
    
    @Override
    public ArrayList<HashMap<String, String>> getTipoPoliza_Grupos() {
        String sql_query = "SELECT id, titulo FROM ctb_tpol_grupos ORDER BY id;";
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
    
    
    
    
    //obtiene datos de la poliza actual
    @Override
    public ArrayList<HashMap<String, String>> getTipoPoliza_Datos(Integer id) {
        String sql_query = "SELECT id,titulo, tipo, ctb_tpol_grupo_id AS grupo_id FROM ctb_tpol WHERE id = ?;";
        ArrayList<HashMap<String, String>> hm = (ArrayList<HashMap<String, String>>) this.jdbcTemplate.query(
            sql_query,  
            new Object[]{new Integer(id)}, new RowMapper() {
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    HashMap<String, String> row = new HashMap<String, String>();
                    row.put("id",String.valueOf(rs.getInt("id")));
                    row.put("titulo",rs.getString("titulo"));
                    row.put("tipo",rs.getString("tipo"));
                    row.put("grupo_id",String.valueOf(rs.getInt("grupo_id")));
                    return row;
                }
            }
        );
        return hm;
    }
    
    
    
    
    
    
    
    @Override
    public ArrayList<HashMap<String, Object>> getConceptosContables_PaginaGrid(String data_string, int offset, int pageSize, String orderBy, String asc) {
        String sql_busqueda = "select id from gral_bus_catalogos(?) as foo (id integer)";
        
	String sql_to_query = "SELECT ctb_con.id, ctb_con.titulo, ctb_con.descripcion FROM ctb_con "
                        +"JOIN ("+sql_busqueda+") AS sbt ON sbt.id = ctb_con.id "
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
                    row.put("descripcion",rs.getString("descripcion"));
                    return row;
                }
            }
        );
        return hm; 
    }
    
    
    
    
    @Override
    public ArrayList<HashMap<String, String>> getConceptoContable_Datos(Integer id) {
        String sql_query = "SELECT id,titulo,descripcion FROM ctb_con WHERE id = ?;";
        ArrayList<HashMap<String, String>> hm = (ArrayList<HashMap<String, String>>) this.jdbcTemplate.query(
            sql_query,  
            new Object[]{new Integer(id)}, new RowMapper() {
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    HashMap<String, String> row = new HashMap<String, String>();
                    row.put("id",String.valueOf(rs.getInt("id")));
                    row.put("titulo",rs.getString("titulo"));
                    row.put("descripcion",rs.getString("descripcion"));
                    return row;
                }
            }
        );
        return hm;
    }
    
    

    @Override
    public ArrayList<HashMap<String, Object>> getCuentasMayor_PaginaGrid(String data_string, int offset, int pageSize, String orderBy, String asc) {
        String sql_busqueda = "select id from gral_bus_catalogos(?) as foo (id integer)";
        
	String sql_to_query = "SELECT ctb_may.id,ctb_may.ctb_may_clase_id, ctb_may.clasificacion, "
                                +"(CASE WHEN ctb_may.descripcion IS NULL OR ctb_may.descripcion='' THEN  (CASE WHEN ctb_may.descripcion_ing IS NULL OR ctb_may.descripcion_ing='' THEN ctb_may.descripcion_otr ELSE ctb_may.descripcion_ing END) ELSE ctb_may.descripcion END) AS descripcion, "
                                +"'Si'::character varying AS ligada_a_cuenta "
                        +"FROM ctb_may "
                        +"JOIN ctb_may_clases ON ctb_may_clases.id=ctb_may.ctb_may_clase_id "
                        +"JOIN ("+sql_busqueda+") AS sbt ON sbt.id = ctb_may.id "
                        +"order by "+orderBy+" "+asc+" limit ? OFFSET ?";
        
        //System.out.println("Busqueda GetPage: "+sql_to_query);
        ArrayList<HashMap<String, Object>> hm = (ArrayList<HashMap<String, Object>>) this.jdbcTemplate.query(
            sql_to_query, 
            new Object[]{data_string, new Integer(pageSize),new Integer(offset)}, new RowMapper() {
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    HashMap<String, Object> row = new HashMap<String, Object>();
                    row.put("id",rs.getInt("id"));
                    row.put("ctb_may_clase_id",rs.getString("ctb_may_clase_id"));
                    row.put("clasificacion",rs.getString("clasificacion"));
                    row.put("descripcion",rs.getString("descripcion"));
                    row.put("ligada_a_cuenta",rs.getString("ligada_a_cuenta"));
                    return row;
                }
            }
        );
        return hm; 
    }
    
    
    @Override
    public ArrayList<HashMap<String, String>> getCuentasMayor_Clases() {
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

    
    
    
    @Override
    public ArrayList<HashMap<String, String>> getCuentaDeMayor_Datos(Integer id) {
        String sql_query = "SELECT id, ctb_may_clase_id, clasificacion , "
                +"(CASE WHEN ctb_may.descripcion IS NULL OR ctb_may.descripcion='' THEN  (CASE WHEN ctb_may.descripcion_ing IS NULL OR ctb_may.descripcion_ing='' THEN ctb_may.descripcion_otr ELSE ctb_may.descripcion_ing END) ELSE ctb_may.descripcion END) AS predeterminada, "
                + "descripcion, descripcion_ing, descripcion_otr FROM ctb_may WHERE id = ?;";
        ArrayList<HashMap<String, String>> hm = (ArrayList<HashMap<String, String>>) this.jdbcTemplate.query(
            sql_query,  
            new Object[]{new Integer(id)}, new RowMapper() {
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    HashMap<String, String> row = new HashMap<String, String>();
                    row.put("id",String.valueOf(rs.getInt("id")));
                    row.put("ctb_may_clase_id",rs.getString("ctb_may_clase_id"));
                    row.put("clasificacion",rs.getString("clasificacion"));
                    row.put("predeterminada",rs.getString("predeterminada"));
                    row.put("descripcion",rs.getString("descripcion"));
                    row.put("descripcion_ing",rs.getString("descripcion_ing"));
                    row.put("descripcion_otr",rs.getString("descripcion_otr"));
                    return row;
                }
            }
        );
        return hm;
    }
    
    //**********************************************************************************
    //METODOS PARA CATALOGO DE CUENTAS CONTABLES
    //**********************************************************************************
    @Override
    public ArrayList<HashMap<String, Object>> getCuentasContables_PaginaGrid(String data_string, int offset, int pageSize, String orderBy, String asc) {
        String sql_busqueda = "select id from gral_bus_catalogos(?) as foo (id integer)";
        
        if(orderBy.equals("id")){
            orderBy="cta_mayor, clasifica, cta";
        }
        
	String sql_to_query = ""
        + "SELECT "
            + "ctb_cta.id,"
            + "cta_mayor AS m, "
            + "clasifica AS c, "
            + "(CASE 	WHEN nivel_cta=1 THEN lpad(cta::character varying, 4, '0')  "
                + "WHEN nivel_cta=2 THEN '&nbsp;&nbsp;'||lpad(cta::character varying, 4, '0')||'-'||lpad(subcta::character varying, 4, '0') "
                + "WHEN nivel_cta=3 THEN '&nbsp;&nbsp;&nbsp;&nbsp;'||lpad(cta::character varying, 4, '0')||'-'||lpad(subcta::character varying, 4, '0')||'-'||lpad(ssubcta::character varying, 4, '0')   "
                + "WHEN nivel_cta=4 THEN '&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;'||lpad(cta::character varying, 4, '0')||'-'||lpad(subcta::character varying, 4, '0')||'-'||lpad(ssubcta::character varying, 4, '0')||'-'||lpad(sssubcta::character varying, 4, '0') "
                + "WHEN nivel_cta=5 THEN '&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;'||lpad(cta::character varying, 4, '0')||'-'||lpad(subcta::character varying, 4, '0')||'-'||lpad(ssubcta::character varying, 4, '0')||'-'||lpad(sssubcta::character varying, 4, '0')||'-'||lpad(ssssubcta::character varying, 4, '0') "
                + "ELSE '' "
                + "END ) AS cuenta, "
            + "(CASE WHEN detalle=0 THEN 'NO' WHEN detalle=1 THEN 'SI' ELSE '' END) AS detalle, "
            + "(CASE WHEN descripcion IS NULL OR descripcion='' THEN  (CASE WHEN descripcion_ing IS NULL OR descripcion_ing='' THEN  descripcion_otr ELSE descripcion_ing END )  ELSE descripcion END ) AS descripcion, "
            + "nivel_cta AS nivel, "
            + "(CASE WHEN estatus=1 THEN 'ACTIVA' WHEN estatus=2 THEN 'INACTIVA' ELSE '' END) AS estatus "
        + "FROM ctb_cta "
        + "JOIN ("+sql_busqueda+") AS sbt ON sbt.id=ctb_cta.id "
        + "order by "+orderBy+" "+asc+" limit ? OFFSET ?";
        
        //System.out.println("Busqueda GetPage: "+sql_to_query);
        ArrayList<HashMap<String, Object>> hm = (ArrayList<HashMap<String, Object>>) this.jdbcTemplate.query(
            sql_to_query, 
            new Object[]{data_string, new Integer(pageSize),new Integer(offset)}, new RowMapper() {
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    HashMap<String, Object> row = new HashMap<String, Object>();
                    row.put("id",rs.getInt("id"));
                    row.put("m",rs.getString("m"));
                    row.put("c",rs.getString("c"));
                    row.put("cuenta",rs.getString("cuenta"));
                    row.put("detalle",rs.getString("detalle"));
                    row.put("descripcion",rs.getString("descripcion"));
                    row.put("nivel",rs.getString("nivel"));
                    row.put("estatus",rs.getString("estatus"));
                    return row;
                }
            }
        );
        return hm; 
    }
    
    

    @Override
    public ArrayList<HashMap<String, Object>> getCuentasContables_Datos(Integer id) {
        String sql_query = ""
                + "SELECT "
                    + "ctb_cta.id, "
                    + "(CASE WHEN ctb_cta.cta=0 THEN '' ELSE ctb_cta.cta::character varying END) AS cta, "
                    + "(CASE WHEN ctb_cta.subcta=0 THEN '' ELSE ctb_cta.subcta::character varying END) AS subcta, "
                    + "(CASE WHEN ctb_cta.ssubcta=0 THEN '' ELSE ctb_cta.ssubcta::character varying END) AS ssubcta, "
                    + "(CASE WHEN ctb_cta.sssubcta=0 THEN '' ELSE ctb_cta.sssubcta::character varying END) AS sssubcta, "
                    + "(CASE WHEN ctb_cta.ssssubcta=0 THEN '' ELSE ctb_cta.ssssubcta::character varying END) AS ssssubcta, "
                    + "ctb_cta.cta_mayor, "
                    + "ctb_cta.clasifica, "
                    + "ctb_cta.detalle, "
                    + "ctb_cta.descripcion, "
                    + "ctb_cta.descripcion_ing, "
                    + "ctb_cta.descripcion_otr, "
                    + "ctb_cta.nivel_cta, "
                    + "ctb_cta.consolida, "
                    + "ctb_cta.estatus,"
                    + "ctb_cta.ctb_cc_id as cc_id,"
                    + "ctb_cta.gral_suc_id as suc_id,"
                    + "ctb_cta.nivel,"
                    + "ctb_cta.tipo,"
                    + "ctb_cta.naturaleza,"
                    + "ctb_cta.ctb_app_id as agrupa_id,"
                    + "(case when ctb_cta_sat.id is null then 0 else ctb_cta_sat.id end) as ctasat_id,"
                    + "(case when ctb_cta_sat.codigo is null then '' else ctb_cta_sat.codigo end) as codigo_sat,"
                    + "(case when ctb_cta_sat.nombre is null then '' else ctb_cta_sat.nombre end) as nombre_cta_sat "
                + "FROM ctb_cta "
                + "left join ctb_cta_sat on ctb_cta_sat.id=ctb_cta.ctb_cta_sat_id "
                + "WHERE ctb_cta.id=?;";
        ArrayList<HashMap<String, Object>> hm = (ArrayList<HashMap<String, Object>>) this.jdbcTemplate.query(
            sql_query,  
            new Object[]{new Integer(id)}, new RowMapper() {
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    HashMap<String, Object> row = new HashMap<String, Object>();
                    row.put("id",rs.getInt("id"));
                    row.put("cta",rs.getString("cta"));
                    row.put("subcta",rs.getString("subcta"));
                    row.put("ssubcta",rs.getString("ssubcta"));
                    row.put("sssubcta",rs.getString("sssubcta"));
                    row.put("ssssubcta",rs.getString("ssssubcta"));
                    row.put("cta_mayor",String.valueOf(rs.getInt("cta_mayor")));
                    row.put("clasifica",String.valueOf(rs.getInt("clasifica")));
                    row.put("detalle",String.valueOf(rs.getInt("detalle")));
                    row.put("descripcion",rs.getString("descripcion"));
                    row.put("descripcion_ing",rs.getString("descripcion_ing"));
                    row.put("descripcion_otr",rs.getString("descripcion_otr"));
                    row.put("nivel_cta",String.valueOf(rs.getInt("nivel_cta")));
                    row.put("estatus",String.valueOf(rs.getInt("estatus")));
                    row.put("cc_id",rs.getInt("cc_id"));
                    row.put("suc_id",rs.getInt("suc_id"));
                    row.put("nivel",rs.getInt("nivel"));
                    row.put("tipo",rs.getInt("tipo"));
                    row.put("naturaleza",rs.getInt("naturaleza"));
                    row.put("agrupa_id",rs.getInt("agrupa_id"));
                    
                    row.put("ctasat_id",rs.getInt("ctasat_id"));
                    row.put("codigo_sat",rs.getString("codigo_sat"));
                    row.put("nombre_cta_sat",rs.getString("nombre_cta_sat"));
                    return row;
                }
            }
        );
        return hm;
    }
    
    @Override
    public ArrayList<HashMap<String, Object>> getCtb_Aplicaciones(Integer tipo) {
        String sql_query = "";
        
        //Para programa de Cuentas contables
        if(tipo==1){
            sql_query = "select id,titulo from ctb_app where estatus=true and ctb_cta=true order by titulo;";
        }
        
        //Para programa de Definicion de asientos
        if(tipo==2){
            sql_query = "select id,titulo from ctb_app where estatus=true and ctb_tmov=true order by titulo;";
        }
        
        ArrayList<HashMap<String, Object>> hm = (ArrayList<HashMap<String, Object>>) this.jdbcTemplate.query(
            sql_query,  
            new Object[]{}, new RowMapper() {
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    HashMap<String, Object> row = new HashMap<String, Object>();
                    row.put("id", rs.getInt("id"));
                    row.put("titulo",rs.getString("titulo"));
                    return row;
                }
            }
        );
        return hm;
    }
    
    
    
    
    
    
    @Override
    public ArrayList<HashMap<String, Object>> getCuentasContables_CuentasMayor(Integer id_empresa) {
        String sql_query = ""
                + "SELECT "
                    + "id, "
                    + "ctb_may_clase_id AS cta_mayor, "
                    + "clasificacion, "
                    + "(CASE WHEN descripcion IS NULL OR descripcion='' THEN (CASE WHEN descripcion_ing IS NULL OR descripcion_ing='' THEN descripcion_ing ELSE descripcion_otr END) ELSE descripcion END) AS descripcion "
                + "FROM ctb_may "
                + "WHERE borrado_logico=false "
                + "AND empresa_id=?;";
        ArrayList<HashMap<String, Object>> hm = (ArrayList<HashMap<String, Object>>) this.jdbcTemplate.query(
            sql_query,
            new Object[]{new Integer(id_empresa)}, new RowMapper() {
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    HashMap<String, Object> row = new HashMap<String, Object>();
                    row.put("id",String.valueOf(rs.getInt("id")));
                    row.put("cta_mayor",String.valueOf(rs.getInt("cta_mayor")));
                    row.put("clasificacion",String.valueOf(rs.getInt("clasificacion")));
                    row.put("descripcion",rs.getString("descripcion"));
                    return row;
                }
            }
        );
        return hm;
    }
    //TERMINA METODOS PARA CATALOGO DE CUENTAS CONTABLES
    //**********************************************************************************
    
    
    
    //Medotdos para reporte de Auxiliar de Cuentas------------------------------------------------------------------------------
    //Calcular años a mostrar en el reporte de Auxiliar de Cuentas
    @Override
    public ArrayList<HashMap<String, Object>>  getCtbRepAuxCtas_Anios() {
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
    
    
    //Obtener las Subcuentas del Nivel que se le indique
    @Override
    public ArrayList<HashMap<String, Object>> getCtbRepAuxCtas_Ctas(Integer nivel, String cta, String scta, String sscta, String ssscta, Integer id_empresa) {
        String sql_query="";
        
        switch(nivel) {
            case 1: 
                sql_query = "SELECT lpad(cta::character varying, 4, '0') AS cta,descripcion FROM ( SELECT DISTINCT cta, (CASE WHEN subcta=0 THEN descripcion ELSE '' END) AS descripcion FROM ctb_cta WHERE gral_emp_id=?  AND borrado_logico=FALSE AND estatus=1 ORDER BY cta ) AS sbt WHERE trim(descripcion)<>'' ORDER BY cta;";
                break;
            case 2: 
                sql_query = "SELECT lpad(cta::character varying, 4, '0') AS cta, descripcion FROM (SELECT DISTINCT subcta AS cta, (CASE WHEN ssubcta=0 THEN descripcion ELSE '' END) AS descripcion FROM ctb_cta WHERE gral_emp_id=? AND cta="+cta+"  AND borrado_logico=FALSE AND estatus=1 ORDER BY subcta ) AS sbt WHERE descripcion<>'' ORDER BY cta;";
                break;
            case 3: 
                sql_query = "SELECT lpad(cta::character varying, 4, '0') AS cta, descripcion FROM (SELECT DISTINCT ssubcta AS cta, (CASE WHEN sssubcta=0 THEN descripcion ELSE '' END) AS descripcion FROM ctb_cta WHERE gral_emp_id=? AND cta="+cta+" AND subcta="+scta+" AND borrado_logico=FALSE AND estatus=1 ORDER BY ssubcta ) AS sbt WHERE descripcion<>'' ORDER BY cta;";
                break;
            case 4: 
                sql_query = "SELECT lpad(cta::character varying, 4, '0') AS cta, descripcion FROM (SELECT DISTINCT sssubcta AS cta, (CASE WHEN ssssubcta=0 THEN descripcion ELSE '' END) AS descripcion FROM ctb_cta WHERE gral_emp_id=? AND cta="+cta+" AND subcta="+scta+" AND ssubcta="+sscta+"  AND borrado_logico=FALSE AND estatus=1 ORDER BY sssubcta ) AS sbt WHERE descripcion<>'' ORDER BY cta;";
                break;
            case 5: 
                sql_query = "SELECT lpad(cta::character varying, 4, '0') AS cta, descripcion FROM (SELECT DISTINCT ssssubcta AS cta, descripcion FROM ctb_cta WHERE gral_emp_id=? AND cta="+cta+" AND subcta="+scta+" AND ssubcta="+sscta+" AND sssubcta="+ssscta+"  AND borrado_logico=FALSE AND estatus=1 ORDER BY ssssubcta ) AS sbt WHERE descripcion<>'' ORDER BY cta;";
                break;
        }
        
        System.out.println("getCtasNivel "+nivel+": "+sql_query);
        ArrayList<HashMap<String, Object>> hm = (ArrayList<HashMap<String, Object>>) this.jdbcTemplate.query(
            sql_query,
            new Object[]{new Integer(id_empresa)}, new RowMapper() {
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    HashMap<String, Object> row = new HashMap<String, Object>();
                    row.put("cta",rs.getString("cta"));
                    row.put("descripcion",rs.getString("descripcion"));
                    return row;
                }
            }
        );
        return hm;
    }
    
    
    
    @Override
    public ArrayList<HashMap<String, String>> getCtbRepAuxCtas_Datos(String data_string) {
        
        String sql_to_query = "select * from ctb_reporte(?) as foo(cuenta character varying, descripcion character varying, saldo_inicial character varying, debe character varying, haber character varying, saldo_final character varying);"; 
        System.out.println("data_string: "+data_string);
        System.out.println("Ctb_DatosRepAuxCtas:: "+sql_to_query);
        ArrayList<HashMap<String, String>> hm_facturas = (ArrayList<HashMap<String, String>>) this.jdbcTemplate.query(
            sql_to_query,
            new Object[]{data_string}, new RowMapper(){
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    HashMap<String, String> row = new HashMap<String, String>();
                    row.put("cuenta",rs.getString("cuenta"));
                    row.put("descripcion",rs.getString("descripcion"));
                    row.put("saldo_inicial",rs.getString("saldo_inicial"));
                    row.put("debe",rs.getString("debe"));
                    row.put("haber",rs.getString("haber"));
                    row.put("saldo_final",rs.getString("saldo_final"));
                    return row;
                }
            }
        );
        return hm_facturas;
    }
    
    
    //Obtiene las sucursales de la empresa indicada
    @Override
    public ArrayList<HashMap<String, Object>> getCtb_Sucursales(Integer idEmp) {
        
        String sql_to_query = "SELECT distinct id, titulo FROM gral_suc WHERE empresa_id=? AND borrado_logico=false;"; 
        ArrayList<HashMap<String, Object>> hm_facturas = (ArrayList<HashMap<String, Object>>) this.jdbcTemplate.query(
            sql_to_query,
            new Object[]{new Integer(idEmp)}, new RowMapper(){
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    HashMap<String, Object> row = new HashMap<String, Object>();
                    row.put("id",String.valueOf(rs.getInt("id")));
                    row.put("titulo",rs.getString("titulo"));
                    return row;
                }
            }
        );
        return hm_facturas;
    }
    
    
    
  //Medotdos para reporte de Auxiliar de Movimientos de Cuentas------------------------------------------------------------------------------
    //Calcular años a mostrar en el reporte de Auxiliar de Movimientos de Cuentas
    @Override 
    public ArrayList<HashMap<String, Object>>  getCtbRepAuxMovCtas_Anios() {
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
    
    
    //Obtener las Subcuentas del Nivel que se le indique
    @Override
    public ArrayList<HashMap<String, Object>> getCtbRepAuxMovCtas_Ctas(Integer nivel, String cta, String scta, String sscta, String ssscta, Integer id_empresa) {
        String sql_query="";
        
       switch(nivel) {
            case 1: 
                sql_query = "SELECT lpad(cta::character varying, 4, '0') AS cta,descripcion FROM ( SELECT DISTINCT cta, (CASE WHEN subcta=0 THEN descripcion ELSE '' END) AS descripcion FROM ctb_cta WHERE gral_emp_id=?  AND borrado_logico=FALSE AND estatus=1 ORDER BY cta ) AS sbt WHERE trim(descripcion)<>'' ORDER BY cta;";
                break;
            case 2: 
                sql_query = "SELECT lpad(cta::character varying, 4, '0') AS cta, descripcion FROM (SELECT DISTINCT subcta AS cta, (CASE WHEN ssubcta=0 THEN descripcion ELSE '' END) AS descripcion FROM ctb_cta WHERE gral_emp_id=? AND cta="+cta+"  AND borrado_logico=FALSE AND estatus=1 ORDER BY subcta ) AS sbt WHERE descripcion<>'' ORDER BY cta;";
                break;
            case 3: 
                sql_query = "SELECT lpad(cta::character varying, 4, '0') AS cta, descripcion FROM (SELECT DISTINCT ssubcta AS cta, (CASE WHEN sssubcta=0 THEN descripcion ELSE '' END) AS descripcion FROM ctb_cta WHERE gral_emp_id=? AND cta="+cta+" AND subcta="+scta+" AND borrado_logico=FALSE AND estatus=1 ORDER BY ssubcta ) AS sbt WHERE descripcion<>'' ORDER BY cta;";
                break;
            case 4: 
                sql_query = "SELECT lpad(cta::character varying, 4, '0') AS cta, descripcion FROM (SELECT DISTINCT sssubcta AS cta, (CASE WHEN ssssubcta=0 THEN descripcion ELSE '' END) AS descripcion FROM ctb_cta WHERE gral_emp_id=? AND cta="+cta+" AND subcta="+scta+" AND ssubcta="+sscta+"  AND borrado_logico=FALSE AND estatus=1 ORDER BY sssubcta ) AS sbt WHERE descripcion<>'' ORDER BY cta;";
                break;
            case 5: 
                sql_query = "SELECT lpad(cta::character varying, 4, '0') AS cta, descripcion FROM (SELECT DISTINCT ssssubcta AS cta, descripcion FROM ctb_cta WHERE gral_emp_id=? AND cta="+cta+" AND subcta="+scta+" AND ssubcta="+sscta+" AND sssubcta="+ssscta+"  AND borrado_logico=FALSE AND estatus=1 ORDER BY ssssubcta ) AS sbt WHERE descripcion<>'' ORDER BY cta;";
                break;
        }
        
        System.out.println("getCtasNivel "+nivel+": "+sql_query);
        ArrayList<HashMap<String, Object>> hm = (ArrayList<HashMap<String, Object>>) this.jdbcTemplate.query(
            sql_query,
            new Object[]{new Integer(id_empresa)}, new RowMapper() {
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    HashMap<String, Object> row = new HashMap<String, Object>();
                    row.put("cta",rs.getString("cta"));
                    row.put("descripcion",rs.getString("descripcion"));
                    return row;
                }
            }
        );
        return hm;
    }
    
    
    
    @Override
    public ArrayList<HashMap<String, String>> getCtbRepAuxMovCtas_Datos(String data_string) {
        String sql_to_query="";
        /*
        ArrayList<HashMap<String, String>>datos = new ArrayList<HashMap<String, String>>();
        
        
		tipo_reg character varying,
		suc character varying,
		cta character varying,
		descrip_cta character varying,
		poliza character varying,
		orig character varying,
		tipo_pol character varying,
		fecha character varying,
		referencia character varying,
		cc character varying,
		descrip_mov character varying,
		saldo_ini character varying,
		debe character varying,
		haber character varying,
		saldo_fin character varying
         
        System.out.println("data_string: "+data_string);
          HashMap<String, String> row = new HashMap<String, String>();
                    row.put("suc", "11");
                    row.put("nombrepol", "11-02-0001-0002");
                    row.put("nombrech", "HSBC México 4021408133");
                    row.put("saldoinicial", "1744006.48");
                    row.put("sucursal", "9898");
                    row.put("poliza", "5504");
                    row.put("origen", "Ban");
                    row.put("tipopoliza", "Egreso");
                    row.put("fechas", "28/12/2013");
                    row.put("cheque", "38200");
                    row.put("cc", "pruebas1");
                    row.put("descripcion", "Ch. 38200 Traspaso A Hsbc 4021408133");
                    row.put("debe", "2002000.00");
                    row.put("debe2", "2002000.00");
                    row.put("haber", "0.00");
                    row.put("saldo", "3748006.00");
                    datos.add(row);
                    
        
        return datos;
        */
        
        
        
        sql_to_query = "select * from ctb_reporte(?) as foo(tipo_reg character varying, suc character varying, cta character varying, descrip_cta character varying, poliza character varying, orig character varying, tipo_pol character varying, fecha character varying, ref character varying, cc character varying, descrip_mov character varying, saldo_ini character varying, debe character varying, haber character varying, saldo_fin character varying);"; 
        System.out.println("data_string: "+data_string);
        System.out.println("Ctb_DatosRepAuxMovCtas:: "+sql_to_query);
        ArrayList<HashMap<String, String>> datos = (ArrayList<HashMap<String, String>>) this.jdbcTemplate.query(
            sql_to_query,
            new Object[]{data_string}, new RowMapper(){
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    HashMap<String, String> row = new HashMap<String, String>();
                    row.put("tipo_reg",rs.getString("tipo_reg"));
                    row.put("suc",rs.getString("suc"));
                    row.put("cta",rs.getString("cta"));
                    row.put("descrip_cta",rs.getString("descrip_cta"));
                    row.put("poliza",rs.getString("poliza"));
                    row.put("orig",rs.getString("orig"));
                    row.put("tipo_pol",rs.getString("tipo_pol"));
                    row.put("fecha",rs.getString("fecha"));
                    row.put("ref",rs.getString("ref"));
                    row.put("cc",rs.getString("cc"));
                    row.put("descrip_mov",rs.getString("descrip_mov"));
                    row.put("saldo_ini",(rs.getString("saldo_ini").trim().equals(""))?rs.getString("saldo_ini"):StringHelper.roundDouble(rs.getString("saldo_ini"),2));
                    row.put("debe",(rs.getString("debe").trim().equals(""))?rs.getString("debe"):StringHelper.roundDouble(rs.getString("debe"),2));
                    row.put("haber",(rs.getString("haber").trim().equals(""))?rs.getString("haber"):StringHelper.roundDouble(rs.getString("haber"),2));
                    row.put("saldo_fin",(rs.getString("saldo_fin").trim().equals(""))?rs.getString("saldo_fin"):StringHelper.roundDouble(rs.getString("saldo_fin"),2));
                    return row;
                }
            }
        );
        return datos;
    }
    
    
    
    
    
    //Métodos para reporte de Balance General------------------------------------------------------------------------------
    //Calcular años a mostrar en el reporte de Balance General
    @Override
    public ArrayList<HashMap<String, Object>>  getCtbRepBalanceGral_Anios() {
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
    
    
    //Obtener las Subcuentas del Nivel que se le indique
    @Override
    public ArrayList<HashMap<String, Object>> getCtbRepBalanceGral_Ctas(Integer nivel, String cta, String scta, String sscta, String ssscta, Integer id_empresa) {
        String sql_query="";
        
        switch(nivel) {
            case 1: 
                sql_query = "SELECT lpad(cta::character varying, 4, '0') AS cta,descripcion FROM ( SELECT DISTINCT cta, (CASE WHEN subcta=0 THEN descripcion ELSE '' END) AS descripcion FROM ctb_cta WHERE gral_emp_id=?  AND borrado_logico=FALSE AND estatus=1 ORDER BY cta ) AS sbt WHERE trim(descripcion)<>'' ORDER BY cta;";
                break;
            case 2: 
                sql_query = "SELECT lpad(cta::character varying, 4, '0') AS cta, descripcion FROM (SELECT DISTINCT subcta AS cta, (CASE WHEN ssubcta=0 THEN descripcion ELSE '' END) AS descripcion FROM ctb_cta WHERE gral_emp_id=? AND cta="+cta+"  AND borrado_logico=FALSE AND estatus=1 ORDER BY subcta ) AS sbt WHERE descripcion<>'' ORDER BY cta;";
                break;
            case 3: 
                sql_query = "SELECT lpad(cta::character varying, 4, '0') AS cta, descripcion FROM (SELECT DISTINCT ssubcta AS cta, (CASE WHEN sssubcta=0 THEN descripcion ELSE '' END) AS descripcion FROM ctb_cta WHERE gral_emp_id=? AND cta="+cta+" AND subcta="+scta+" AND borrado_logico=FALSE AND estatus=1 ORDER BY ssubcta ) AS sbt WHERE descripcion<>'' ORDER BY cta;";
                break;
            case 4: 
                sql_query = "SELECT lpad(cta::character varying, 4, '0') AS cta, descripcion FROM (SELECT DISTINCT sssubcta AS cta, (CASE WHEN ssssubcta=0 THEN descripcion ELSE '' END) AS descripcion FROM ctb_cta WHERE gral_emp_id=? AND cta="+cta+" AND subcta="+scta+" AND ssubcta="+sscta+"  AND borrado_logico=FALSE AND estatus=1 ORDER BY sssubcta ) AS sbt WHERE descripcion<>'' ORDER BY cta;";
                break;
            case 5: 
                sql_query = "SELECT lpad(cta::character varying, 4, '0') AS cta, descripcion FROM (SELECT DISTINCT ssssubcta AS cta, descripcion FROM ctb_cta WHERE gral_emp_id=? AND cta="+cta+" AND subcta="+scta+" AND ssubcta="+sscta+" AND sssubcta="+ssscta+"  AND borrado_logico=FALSE AND estatus=1 ORDER BY ssssubcta ) AS sbt WHERE descripcion<>'' ORDER BY cta;";
                break;
        }
        
        System.out.println("getCtasNivel "+nivel+": "+sql_query);
        ArrayList<HashMap<String, Object>> hm = (ArrayList<HashMap<String, Object>>) this.jdbcTemplate.query(
            sql_query,
            new Object[]{new Integer(id_empresa)}, new RowMapper() {
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    HashMap<String, Object> row = new HashMap<String, Object>();
                    row.put("cta",rs.getString("cta"));
                    row.put("descripcion",rs.getString("descripcion"));
                    return row;
                }
            }
        );
        return hm;
    }
    
    
    
    @Override
    public ArrayList<HashMap<String, String>> getCtbRepBalanceGral_Datos(String data_string) {
        
        String sql_to_query = "select * from ctb_reporte(?) as foo(tipo_reg integer, mayor integer, clasifica integer, descripcion character varying, saldo_fin character varying);"; 
        //System.out.println("data_string: "+data_string);
        //System.out.println("Ctb_DatosBalanceGeneral:: "+sql_to_query);
        ArrayList<HashMap<String, String>> hm_facturas = (ArrayList<HashMap<String, String>>) this.jdbcTemplate.query(
            sql_to_query,
            new Object[]{data_string}, new RowMapper(){
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    HashMap<String, String> row = new HashMap<String, String>();
                    row.put("tipo_reg",rs.getString("tipo_reg"));
                    row.put("mayor",rs.getString("mayor"));
                    row.put("clasifica",rs.getString("clasifica"));
                    row.put("descripcion",rs.getString("descripcion"));
                    row.put("saldo_fin",(rs.getString("saldo_fin").trim().equals(""))?rs.getString("saldo_fin"):StringHelper.roundDouble(rs.getString("saldo_fin"),2));
                    return row;
                }
            }
        );
        return hm_facturas;
    }
    
    
    
    //Métodos para reporte de Balanza de Comprobacion------------------------------------------------------------------------------
    //Calcular años a mostrar en el reporte de Balanza de Comprobacion
    @Override
    public ArrayList<HashMap<String, Object>>  getCtbRepBalanzaComp_Anios() {
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
    
    
    //Obtener las Subcuentas del Nivel que se le indique
    @Override
    public ArrayList<HashMap<String, Object>> getCtbRepBalanzaComp_Ctas(Integer nivel, String cta, String scta, String sscta, String ssscta, Integer id_empresa) {
        String sql_query="";
        
        switch(nivel) {
            case 1: 
                sql_query = "SELECT lpad(cta::character varying, 4, '0') AS cta,descripcion FROM ( SELECT DISTINCT cta, (CASE WHEN subcta=0 THEN descripcion ELSE '' END) AS descripcion FROM ctb_cta WHERE gral_emp_id=?  AND borrado_logico=FALSE AND estatus=1 ORDER BY cta ) AS sbt WHERE trim(descripcion)<>'' ORDER BY cta;";
                break;
            case 2: 
                sql_query = "SELECT lpad(cta::character varying, 4, '0') AS cta, descripcion FROM (SELECT DISTINCT subcta AS cta, (CASE WHEN ssubcta=0 THEN descripcion ELSE '' END) AS descripcion FROM ctb_cta WHERE gral_emp_id=? AND cta="+cta+"  AND borrado_logico=FALSE AND estatus=1 ORDER BY subcta ) AS sbt WHERE descripcion<>'' ORDER BY cta;";
                break;
            case 3: 
                sql_query = "SELECT lpad(cta::character varying, 4, '0') AS cta, descripcion FROM (SELECT DISTINCT ssubcta AS cta, (CASE WHEN sssubcta=0 THEN descripcion ELSE '' END) AS descripcion FROM ctb_cta WHERE gral_emp_id=? AND cta="+cta+" AND subcta="+scta+" AND borrado_logico=FALSE AND estatus=1 ORDER BY ssubcta ) AS sbt WHERE descripcion<>'' ORDER BY cta;";
                break;
            case 4: 
                sql_query = "SELECT lpad(cta::character varying, 4, '0') AS cta, descripcion FROM (SELECT DISTINCT sssubcta AS cta, (CASE WHEN ssssubcta=0 THEN descripcion ELSE '' END) AS descripcion FROM ctb_cta WHERE gral_emp_id=? AND cta="+cta+" AND subcta="+scta+" AND ssubcta="+sscta+"  AND borrado_logico=FALSE AND estatus=1 ORDER BY sssubcta ) AS sbt WHERE descripcion<>'' ORDER BY cta;";
                break;
            case 5: 
                sql_query = "SELECT lpad(cta::character varying, 4, '0') AS cta, descripcion FROM (SELECT DISTINCT ssssubcta AS cta, descripcion FROM ctb_cta WHERE gral_emp_id=? AND cta="+cta+" AND subcta="+scta+" AND ssubcta="+sscta+" AND sssubcta="+ssscta+"  AND borrado_logico=FALSE AND estatus=1 ORDER BY ssssubcta ) AS sbt WHERE descripcion<>'' ORDER BY cta;";
                break;
        }
        
        //System.out.println("getCtasNivel "+nivel+": "+sql_query);
        ArrayList<HashMap<String, Object>> hm = (ArrayList<HashMap<String, Object>>) this.jdbcTemplate.query(
            sql_query,
            new Object[]{new Integer(id_empresa)}, new RowMapper() {
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    HashMap<String, Object> row = new HashMap<String, Object>();
                    row.put("cta",rs.getString("cta"));
                    row.put("descripcion",rs.getString("descripcion"));
                    return row;
                }
            }
        );
        return hm;
    }
    
    
    
    @Override
    public ArrayList<LinkedHashMap<String, String>> getCtbRepBalanzaComp_Datos(String data_string) {
        
        String sql_to_query = "select * from ctb_reporte(?) as foo(nivel integer,  cuenta1 character varying, cuenta character varying, descripcion character varying, saldo_inicial character varying, debe character varying, haber character varying, saldo_final character varying);"; 
        System.out.println("data_string: "+data_string);
        System.out.println("CtbRepBalanzaComp:: "+sql_to_query);
        ArrayList<LinkedHashMap<String, String>> hm_facturas = (ArrayList<LinkedHashMap<String, String>>) this.jdbcTemplate.query(
            sql_to_query,
            new Object[]{data_string}, new RowMapper(){
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    LinkedHashMap<String, String> row = new LinkedHashMap<String, String>();
                    row.put("nivel",rs.getString("nivel"));
                    row.put("numCta",rs.getString("cuenta1"));
                    row.put("cuenta",rs.getString("cuenta"));
                    row.put("descripcion",rs.getString("descripcion"));
                    row.put("saldo_inicial",(rs.getString("saldo_inicial").trim().equals(""))?rs.getString("saldo_inicial"):StringHelper.roundDouble(rs.getString("saldo_inicial"),2));
                    row.put("debe",(rs.getString("debe").trim().equals(""))?rs.getString("debe"):StringHelper.roundDouble(rs.getString("debe"),2));
                    row.put("haber",(rs.getString("haber").trim().equals(""))?rs.getString("haber"):StringHelper.roundDouble(rs.getString("haber"),2));
                    row.put("saldo_final",(rs.getString("saldo_final").trim().equals(""))?rs.getString("saldo_final"):StringHelper.roundDouble(rs.getString("saldo_final"),2));
                    return row;
                }
            }
        );
        return hm_facturas;
    }
   
    
    
    //Métodos para reporte de Estado de Resultados------------------------------------------------------------------------------
    //Calcular años a mostrar en el reporte de Estado de Resultados
    @Override
    public ArrayList<HashMap<String, Object>>  getCtbRepEstadoResult_Anios() {
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
    
    
    //Obtener las Subcuentas del Nivel que se le indique
    @Override
    public ArrayList<HashMap<String, Object>> getCtbRepEstadoResult_Ctas(Integer nivel, String cta, String scta, String sscta, String ssscta, Integer id_empresa) {
        String sql_query="";
        
        switch(nivel) {
            case 1: 
                sql_query = "SELECT lpad(cta::character varying, 4, '0') AS cta,descripcion FROM ( SELECT DISTINCT cta, (CASE WHEN subcta=0 THEN descripcion ELSE '' END) AS descripcion FROM ctb_cta WHERE gral_emp_id=?  AND borrado_logico=FALSE AND estatus=1 ORDER BY cta ) AS sbt WHERE trim(descripcion)<>'' ORDER BY cta;";
                break;
            case 2: 
                sql_query = "SELECT lpad(cta::character varying, 4, '0') AS cta, descripcion FROM (SELECT DISTINCT subcta AS cta, (CASE WHEN ssubcta=0 THEN descripcion ELSE '' END) AS descripcion FROM ctb_cta WHERE gral_emp_id=? AND cta="+cta+"  AND borrado_logico=FALSE AND estatus=1 ORDER BY subcta ) AS sbt WHERE descripcion<>'' ORDER BY cta;";
                break;
            case 3: 
                sql_query = "SELECT lpad(cta::character varying, 4, '0') AS cta, descripcion FROM (SELECT DISTINCT ssubcta AS cta, (CASE WHEN sssubcta=0 THEN descripcion ELSE '' END) AS descripcion FROM ctb_cta WHERE gral_emp_id=? AND cta="+cta+" AND subcta="+scta+" AND borrado_logico=FALSE AND estatus=1 ORDER BY ssubcta ) AS sbt WHERE descripcion<>'' ORDER BY cta;";
                break;
            case 4: 
                sql_query = "SELECT lpad(cta::character varying, 4, '0') AS cta, descripcion FROM (SELECT DISTINCT sssubcta AS cta, (CASE WHEN ssssubcta=0 THEN descripcion ELSE '' END) AS descripcion FROM ctb_cta WHERE gral_emp_id=? AND cta="+cta+" AND subcta="+scta+" AND ssubcta="+sscta+"  AND borrado_logico=FALSE AND estatus=1 ORDER BY sssubcta ) AS sbt WHERE descripcion<>'' ORDER BY cta;";
                break;
            case 5: 
                sql_query = "SELECT lpad(cta::character varying, 4, '0') AS cta, descripcion FROM (SELECT DISTINCT ssssubcta AS cta, descripcion FROM ctb_cta WHERE gral_emp_id=? AND cta="+cta+" AND subcta="+scta+" AND ssubcta="+sscta+" AND sssubcta="+ssscta+"  AND borrado_logico=FALSE AND estatus=1 ORDER BY ssssubcta ) AS sbt WHERE descripcion<>'' ORDER BY cta;";
                break;
        }
        
        //System.out.println("getCtasNivel "+nivel+": "+sql_query);
        ArrayList<HashMap<String, Object>> hm = (ArrayList<HashMap<String, Object>>) this.jdbcTemplate.query(
            sql_query,
            new Object[]{new Integer(id_empresa)}, new RowMapper() {
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    HashMap<String, Object> row = new HashMap<String, Object>();
                    row.put("cta",rs.getString("cta"));
                    row.put("descripcion",rs.getString("descripcion"));
                    return row;
                }
            }
        );
        return hm;
    }
    
    
    
    @Override
    public ArrayList<HashMap<String, String>> getCtbRepEstadoResult_Datos(String data_string) {
        
        String sql_to_query = "select * from ctb_reporte(?) as foo(cuenta character varying, descripcion character varying, saldo_inicial character varying, debe character varying, haber character varying, saldo_final character varying);"; 
        //System.out.println("data_string: "+data_string);
        //System.out.println("Ctb_DatosRepAuxCtas:: "+sql_to_query);
        ArrayList<HashMap<String, String>> hm_facturas = (ArrayList<HashMap<String, String>>) this.jdbcTemplate.query(
            sql_to_query,
            new Object[]{data_string}, new RowMapper(){
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    HashMap<String, String> row = new HashMap<String, String>();
                    row.put("cuenta",rs.getString("cuenta"));
                    row.put("descripcion",rs.getString("descripcion"));
                    row.put("saldo_inicial",rs.getString("saldo_inicial"));
                    row.put("debe",rs.getString("debe"));
                    row.put("haber",rs.getString("haber"));
                    row.put("saldo_final",rs.getString("saldo_final"));
                    return row;
                }
            }
        );
        return hm_facturas;
    }
   
    
    //Métodos para reporte de Pólizas Contables------------------------------------------------------------------------------
    //Calcular años a mostrar en el Reporte de Pólizas Contables
    @Override
    public ArrayList<HashMap<String, Object>>  getCtbRepPolizasCont_Anios() {
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
    
    
    //Obtener las Subcuentas del Nivel que se le indique
    @Override
    public ArrayList<HashMap<String, Object>> getCtbRepPolizasCont_Ctas(Integer nivel, String cta, String scta, String sscta, String ssscta, Integer id_empresa) {
        String sql_query="";
        
        switch(nivel) {
            case 1: 
                sql_query = "SELECT lpad(cta::character varying, 4, '0') AS cta,descripcion FROM ( SELECT DISTINCT cta, (CASE WHEN subcta=0 THEN descripcion ELSE '' END) AS descripcion FROM ctb_cta WHERE gral_emp_id=?  AND borrado_logico=FALSE AND estatus=1 ORDER BY cta ) AS sbt WHERE trim(descripcion)<>'' ORDER BY cta;";
                break;
            case 2: 
                sql_query = "SELECT lpad(cta::character varying, 4, '0') AS cta, descripcion FROM (SELECT DISTINCT subcta AS cta, (CASE WHEN ssubcta=0 THEN descripcion ELSE '' END) AS descripcion FROM ctb_cta WHERE gral_emp_id=? AND cta="+cta+"  AND borrado_logico=FALSE AND estatus=1 ORDER BY subcta ) AS sbt WHERE descripcion<>'' ORDER BY cta;";
                break;
            case 3: 
                sql_query = "SELECT lpad(cta::character varying, 4, '0') AS cta, descripcion FROM (SELECT DISTINCT ssubcta AS cta, (CASE WHEN sssubcta=0 THEN descripcion ELSE '' END) AS descripcion FROM ctb_cta WHERE gral_emp_id=? AND cta="+cta+" AND subcta="+scta+" AND borrado_logico=FALSE AND estatus=1 ORDER BY ssubcta ) AS sbt WHERE descripcion<>'' ORDER BY cta;";
                break;
            case 4: 
                sql_query = "SELECT lpad(cta::character varying, 4, '0') AS cta, descripcion FROM (SELECT DISTINCT sssubcta AS cta, (CASE WHEN ssssubcta=0 THEN descripcion ELSE '' END) AS descripcion FROM ctb_cta WHERE gral_emp_id=? AND cta="+cta+" AND subcta="+scta+" AND ssubcta="+sscta+"  AND borrado_logico=FALSE AND estatus=1 ORDER BY sssubcta ) AS sbt WHERE descripcion<>'' ORDER BY cta;";
                break;
            case 5: 
                sql_query = "SELECT lpad(cta::character varying, 4, '0') AS cta, descripcion FROM (SELECT DISTINCT ssssubcta AS cta, descripcion FROM ctb_cta WHERE gral_emp_id=? AND cta="+cta+" AND subcta="+scta+" AND ssubcta="+sscta+" AND sssubcta="+ssscta+"  AND borrado_logico=FALSE AND estatus=1 ORDER BY ssssubcta ) AS sbt WHERE descripcion<>'' ORDER BY cta;";
                break;
        }
        
        //System.out.println("getCtasNivel "+nivel+": "+sql_query);
        ArrayList<HashMap<String, Object>> hm = (ArrayList<HashMap<String, Object>>) this.jdbcTemplate.query(
            sql_query,
            new Object[]{new Integer(id_empresa)}, new RowMapper() {
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    HashMap<String, Object> row = new HashMap<String, Object>();
                    row.put("cta",rs.getString("cta"));
                    row.put("descripcion",rs.getString("descripcion"));
                    return row;
                }
            }
        );
        return hm;
    }
    
    
    
    @Override
    public ArrayList<HashMap<String, String>> getCtbRepPolizasCont_Datos(String data_string) {
        
        String sql_to_query = "select * from ctb_reporte(?) as foo(cuenta character varying, descripcion character varying, saldo_inicial character varying, debe character varying, haber character varying, saldo_final character varying);"; 
        //System.out.println("data_string: "+data_string);
        //System.out.println("Ctb_DatosRepAuxCtas:: "+sql_to_query);
        ArrayList<HashMap<String, String>> hm_facturas = (ArrayList<HashMap<String, String>>) this.jdbcTemplate.query(
            sql_to_query,
            new Object[]{data_string}, new RowMapper(){
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    HashMap<String, String> row = new HashMap<String, String>();
                    row.put("cuenta",rs.getString("cuenta"));
                    row.put("descripcion",rs.getString("descripcion"));
                    row.put("saldo_inicial",rs.getString("saldo_inicial"));
                    row.put("debe",rs.getString("debe"));
                    row.put("haber",rs.getString("haber"));
                    row.put("saldo_final",rs.getString("saldo_final"));
                    return row;
                }
            }
        );
        return hm_facturas;
    }
    
    
    
     //Métodos para reporte de Estado de Resultados Anual------------------------------------------------------------------------------
    //Calcular años a mostrar en el reporte de Estado de Resultados Anual
    @Override
    public ArrayList<HashMap<String, Object>>  getCtbRepEstadoResultAnual_Anios() {
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
    
    
    //Obtener las Subcuentas del Nivel que se le indique
    @Override
    public ArrayList<HashMap<String, Object>> getCtbRepEstadoResultAnual_Ctas(Integer nivel, String cta, String scta, String sscta, String ssscta, Integer id_empresa) {
        String sql_query="";
        
        switch(nivel) {
            case 1: 
                sql_query = "SELECT lpad(cta::character varying, 4, '0') AS cta,descripcion FROM ( SELECT DISTINCT cta, (CASE WHEN subcta=0 THEN descripcion ELSE '' END) AS descripcion FROM ctb_cta WHERE gral_emp_id=?  AND borrado_logico=FALSE AND estatus=1 ORDER BY cta ) AS sbt WHERE trim(descripcion)<>'' ORDER BY cta;";
                break;
            case 2: 
                sql_query = "SELECT lpad(cta::character varying, 4, '0') AS cta, descripcion FROM (SELECT DISTINCT subcta AS cta, (CASE WHEN ssubcta=0 THEN descripcion ELSE '' END) AS descripcion FROM ctb_cta WHERE gral_emp_id=? AND cta="+cta+"  AND borrado_logico=FALSE AND estatus=1 ORDER BY subcta ) AS sbt WHERE descripcion<>'' ORDER BY cta;";
                break;
            case 3: 
                sql_query = "SELECT lpad(cta::character varying, 4, '0') AS cta, descripcion FROM (SELECT DISTINCT ssubcta AS cta, (CASE WHEN sssubcta=0 THEN descripcion ELSE '' END) AS descripcion FROM ctb_cta WHERE gral_emp_id=? AND cta="+cta+" AND subcta="+scta+" AND borrado_logico=FALSE AND estatus=1 ORDER BY ssubcta ) AS sbt WHERE descripcion<>'' ORDER BY cta;";
                break;
            case 4: 
                sql_query = "SELECT lpad(cta::character varying, 4, '0') AS cta, descripcion FROM (SELECT DISTINCT sssubcta AS cta, (CASE WHEN ssssubcta=0 THEN descripcion ELSE '' END) AS descripcion FROM ctb_cta WHERE gral_emp_id=? AND cta="+cta+" AND subcta="+scta+" AND ssubcta="+sscta+"  AND borrado_logico=FALSE AND estatus=1 ORDER BY sssubcta ) AS sbt WHERE descripcion<>'' ORDER BY cta;";
                break;
            case 5: 
                sql_query = "SELECT lpad(cta::character varying, 4, '0') AS cta, descripcion FROM (SELECT DISTINCT ssssubcta AS cta, descripcion FROM ctb_cta WHERE gral_emp_id=? AND cta="+cta+" AND subcta="+scta+" AND ssubcta="+sscta+" AND sssubcta="+ssscta+"  AND borrado_logico=FALSE AND estatus=1 ORDER BY ssssubcta ) AS sbt WHERE descripcion<>'' ORDER BY cta;";
                break;
        }
        
        //System.out.println("getCtasNivel "+nivel+": "+sql_query);
        ArrayList<HashMap<String, Object>> hm = (ArrayList<HashMap<String, Object>>) this.jdbcTemplate.query(
            sql_query,
            new Object[]{new Integer(id_empresa)}, new RowMapper() {
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    HashMap<String, Object> row = new HashMap<String, Object>();
                    row.put("cta",rs.getString("cta"));
                    row.put("descripcion",rs.getString("descripcion"));
                    return row;
                }
            }
        );
        return hm;
    }
    
    
    
    @Override
    public ArrayList<HashMap<String, String>> getCtbRepEstadoResultAnual_Datos(String data_string) {
        
        String sql_to_query = "select * from ctb_reporte(?) as foo(cuenta character varying, descripcion character varying, saldo_inicial character varying, debe character varying, haber character varying, saldo_final character varying);"; 
        //System.out.println("data_string: "+data_string);
        //System.out.println("Ctb_DatosRepAuxCtas:: "+sql_to_query);
        ArrayList<HashMap<String, String>> hm_facturas = (ArrayList<HashMap<String, String>>) this.jdbcTemplate.query(
            sql_to_query,
            new Object[]{data_string}, new RowMapper(){
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    HashMap<String, String> row = new HashMap<String, String>();
                    row.put("cuenta",rs.getString("cuenta"));
                    row.put("descripcion",rs.getString("descripcion"));
                    row.put("saldo_inicial",rs.getString("saldo_inicial"));
                    row.put("debe",rs.getString("debe"));
                    row.put("haber",rs.getString("haber"));
                    row.put("saldo_final",rs.getString("saldo_final"));
                    return row;
                }
            }
        );
        return hm_facturas;
    }
    
    
    
    //Métodos para reporte de Libro Mayor------------------------------------------------------------------------------
    //Calcular años a mostrar en el reporte de Libro Mayor
    @Override
    public ArrayList<HashMap<String, Object>>  getCtbRepLibroMayor_Anios() {
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
    
    
    //Obtener las Subcuentas del Nivel que se le indique
    @Override
    public ArrayList<HashMap<String, Object>> getCtbRepLibroMayor_Ctas(Integer nivel, String cta, String scta, String sscta, String ssscta, Integer id_empresa) {
        String sql_query="";
        
        switch(nivel) {
            case 1: 
                sql_query = "SELECT lpad(cta::character varying, 4, '0') AS cta,descripcion FROM ( SELECT DISTINCT cta, (CASE WHEN subcta=0 THEN descripcion ELSE '' END) AS descripcion FROM ctb_cta WHERE gral_emp_id=?  AND borrado_logico=FALSE AND estatus=1 ORDER BY cta ) AS sbt WHERE trim(descripcion)<>'' ORDER BY cta;";
                break;
            case 2: 
                sql_query = "SELECT lpad(cta::character varying, 4, '0') AS cta, descripcion FROM (SELECT DISTINCT subcta AS cta, (CASE WHEN ssubcta=0 THEN descripcion ELSE '' END) AS descripcion FROM ctb_cta WHERE gral_emp_id=? AND cta="+cta+"  AND borrado_logico=FALSE AND estatus=1 ORDER BY subcta ) AS sbt WHERE descripcion<>'' ORDER BY cta;";
                break;
            case 3: 
                sql_query = "SELECT lpad(cta::character varying, 4, '0') AS cta, descripcion FROM (SELECT DISTINCT ssubcta AS cta, (CASE WHEN sssubcta=0 THEN descripcion ELSE '' END) AS descripcion FROM ctb_cta WHERE gral_emp_id=? AND cta="+cta+" AND subcta="+scta+" AND borrado_logico=FALSE AND estatus=1 ORDER BY ssubcta ) AS sbt WHERE descripcion<>'' ORDER BY cta;";
                break;
            case 4: 
                sql_query = "SELECT lpad(cta::character varying, 4, '0') AS cta, descripcion FROM (SELECT DISTINCT sssubcta AS cta, (CASE WHEN ssssubcta=0 THEN descripcion ELSE '' END) AS descripcion FROM ctb_cta WHERE gral_emp_id=? AND cta="+cta+" AND subcta="+scta+" AND ssubcta="+sscta+"  AND borrado_logico=FALSE AND estatus=1 ORDER BY sssubcta ) AS sbt WHERE descripcion<>'' ORDER BY cta;";
                break;
            case 5: 
                sql_query = "SELECT lpad(cta::character varying, 4, '0') AS cta, descripcion FROM (SELECT DISTINCT ssssubcta AS cta, descripcion FROM ctb_cta WHERE gral_emp_id=? AND cta="+cta+" AND subcta="+scta+" AND ssubcta="+sscta+" AND sssubcta="+ssscta+"  AND borrado_logico=FALSE AND estatus=1 ORDER BY ssssubcta ) AS sbt WHERE descripcion<>'' ORDER BY cta;";
                break;
        }
        
        //System.out.println("getCtasNivel "+nivel+": "+sql_query);
        ArrayList<HashMap<String, Object>> hm = (ArrayList<HashMap<String, Object>>) this.jdbcTemplate.query(
            sql_query,
            new Object[]{new Integer(id_empresa)}, new RowMapper() {
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    HashMap<String, Object> row = new HashMap<String, Object>();
                    row.put("cta",rs.getString("cta"));
                    row.put("descripcion",rs.getString("descripcion"));
                    return row;
                }
            }
        );
        return hm;
    }
    
    
    
    @Override
    public ArrayList<HashMap<String, String>> getCtbRepLibroMayor_Datos(String data_string) {
        
        String sql_to_query = "select * from ctb_reporte(?) as foo(cuenta character varying, descripcion character varying, saldo_inicial character varying, debe character varying, haber character varying, saldo_final character varying);"; 
        //System.out.println("data_string: "+data_string);
        //System.out.println("Ctb_DatosRepAuxCtas:: "+sql_to_query);
        ArrayList<HashMap<String, String>> hm_facturas = (ArrayList<HashMap<String, String>>) this.jdbcTemplate.query(
            sql_to_query,
            new Object[]{data_string}, new RowMapper(){
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    HashMap<String, String> row = new HashMap<String, String>();
                    row.put("cuenta",rs.getString("cuenta"));
                    row.put("descripcion",rs.getString("descripcion"));
                    row.put("saldo_inicial",rs.getString("saldo_inicial"));
                    row.put("debe",rs.getString("debe"));
                    row.put("haber",rs.getString("haber"));
                    row.put("saldo_final",rs.getString("saldo_final"));
                    return row;
                }
            }
        );
        return hm_facturas;
    }
    
    
    
    
    
    
    //METODOS PARA APLICATIVO DE POLIZAS CONTABLES
    @Override
    public ArrayList<HashMap<String, Object>> getPolizasContables_PaginaGrid(String data_string, int offset, int pageSize, String orderBy, String asc) {
        String sql_busqueda = "select id from gral_bus_catalogos(?) as foo (id integer)";
        
	String sql_to_query = ""
        + "select sbt1.id, poliza,tipo,concepto, fecha, moneda, status,sum(debe) as debe, sum(haber) as haber "
        + "from ("
            + "select ctb_pol.fecha::date as fecha1, ctb_pol.id, ctb_pol.poliza, ctb_pol.tipo, ctb_pol.concepto, (case when ctb_pol.fecha is null then '' else to_char(ctb_pol.fecha,'dd/mm/yyyy') end) as fecha, ctb_pol.moneda, (CASE WHEN ctb_pol.status=1 THEN 'No afectada' WHEN ctb_pol.status=2 THEN 'Afectada' WHEN ctb_pol.status=3 THEN 'Cancelada' ELSE '' END) AS status, (case when ctb_pol_mov.tipo=1 then ctb_pol_mov.cantidad else 0 end) as debe,(case when ctb_pol_mov.tipo=2 then ctb_pol_mov.cantidad else 0 end) as haber "
            + "from ctb_pol "
            + "join ctb_pol_mov on ctb_pol_mov.ctb_pol_id=ctb_pol.id"
        + ") as sbt1 "
        + "JOIN ("+sql_busqueda+") AS sbt ON sbt.id = sbt1.id "
        + "group by sbt1.id, sbt1.fecha1, sbt1.poliza,sbt1.tipo,sbt1.concepto, sbt1.fecha, sbt1.moneda, sbt1.status "
        + "order by "+orderBy+" "+asc+" limit ? OFFSET ?";
        
        System.out.println("data_string: "+data_string);
        System.out.println("sql_busqueda: "+sql_busqueda);
        System.out.println("sql_to_query: "+sql_to_query);
        ArrayList<HashMap<String, Object>> hm = (ArrayList<HashMap<String, Object>>) this.jdbcTemplate.query(
            sql_to_query, 
            new Object[]{data_string, new Integer(pageSize),new Integer(offset)}, new RowMapper() {
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    HashMap<String, Object> row = new HashMap<String, Object>();
                    row.put("id",rs.getInt("id"));
                    row.put("poliza",rs.getString("poliza"));
                    row.put("tipo",rs.getString("tipo"));
                    row.put("concepto",rs.getString("concepto"));
                    row.put("fecha",rs.getString("fecha"));
                    row.put("moneda",rs.getString("moneda"));
                    row.put("debe",StringHelper.AgregaComas(StringHelper.roundDouble(rs.getString("debe"),2)));
                    row.put("haber",StringHelper.AgregaComas(StringHelper.roundDouble(rs.getString("haber"),2)));
                    row.put("status",rs.getString("status"));
                    return row;
                }
            }
        );
        return hm; 
    }
    
    
    
    @Override
    public ArrayList<HashMap<String, Object>> getPolizasContables_Datos(Integer id) {
        String sql_query = ""
        + "SELECT "
            + "ctb_pol.id, "
            + "ctb_pol.gral_suc_id as suc_id, "
            + "ctb_pol.ano as anio, "
            + "ctb_pol.mes, "
            + "ctb_pol.poliza as no_poliza, "
            + "ctb_pol.ctb_tpol_id as tpol_id, "
            + "ctb_pol.tipo, "
            + "ctb_pol.ctb_con_id as con_id, "
            + "ctb_pol.concepto, "
            + "ctb_pol.gral_mon_id as mon_id, "
            + "ctb_pol.moneda, "
            + "ctb_pol.status, "
            + "ctb_pol.modulo_origen as mod_id, "
            + "ctb_pol.gral_usr_id_cap as user_cap_id, "
            + "to_char(ctb_pol.fecha,'yyyy-mm-dd') AS fecha,"
            + "ctb_pol.descripcion, "
            + "ctb_pol.referencia "
        + "FROM ctb_pol  "
        + "where ctb_pol.borrado_logico=false AND ctb_pol.id=?;";
        
        //System.out.println(sql_query);
        
        ArrayList<HashMap<String, Object>> hm = (ArrayList<HashMap<String, Object>>) this.jdbcTemplate.query(
            sql_query,  
            new Object[]{new Integer(id)}, new RowMapper() {
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    HashMap<String, Object> row = new HashMap<String, Object>();
                    row.put("id",String.valueOf(rs.getInt("id")));
                    row.put("suc_id",String.valueOf(rs.getInt("suc_id")));
                    row.put("anio",String.valueOf(rs.getInt("anio")));
                    row.put("mes",String.valueOf(rs.getInt("mes")));
                    row.put("no_poliza",String.valueOf(rs.getString("no_poliza")));
                    row.put("tpol_id",String.valueOf(rs.getInt("tpol_id")));
                    row.put("con_id",String.valueOf(rs.getInt("con_id")));
                    row.put("mon_id",String.valueOf(rs.getInt("mon_id")));
                    row.put("status",String.valueOf(rs.getInt("status")));
                    row.put("mod_id",String.valueOf(rs.getInt("mod_id")));
                    row.put("user_cap_id",String.valueOf(rs.getInt("user_cap_id")));
                    row.put("fecha",rs.getString("fecha"));
                    row.put("descripcion",rs.getString("descripcion"));
                    row.put("referencia",rs.getString("referencia"));
                    return row;
                }
            }
        );
        return hm;
    }
    
    
    
    
    
    @Override
    public ArrayList<HashMap<String, Object>> getPolizasContables_DatosGrid(Integer poliza_id) {
        String sql_query = ""
        + "select "
            + "ctb_pol_mov.id as id_det,"
            + "ctb_pol_mov.ctb_tmov_id as tmov_id,"
            + "ctb_pol_mov.ctb_cc_id as cc_id,"
            + "ctb_pol_mov.ctb_cta_id as cta_id,"
            + "ctb_pol_mov.cta,"
            + "ctb_cta.descripcion,"
            + "(case when ctb_pol_mov.tipo=1 then ctb_pol_mov.cantidad else 0 end) as debe,"
            + "(case when ctb_pol_mov.tipo=2 then ctb_pol_mov.cantidad else 0 end) as haber "
        + "from ctb_pol_mov "
        + "join ctb_cta on ctb_cta.id=ctb_pol_mov.ctb_cta_id "
        + "where ctb_pol_mov.ctb_pol_id=?;";
        
        //System.out.println(sql_query);
        ArrayList<HashMap<String, Object>> hm = (ArrayList<HashMap<String, Object>>) this.jdbcTemplate.query(
            sql_query,  
            new Object[]{new Integer(poliza_id)}, new RowMapper() {
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    HashMap<String, Object> row = new HashMap<String, Object>();
                    row.put("id_det",String.valueOf(rs.getInt("id_det")));
                    row.put("tmov_id",String.valueOf(rs.getInt("tmov_id")));
                    row.put("cc_id",String.valueOf(rs.getInt("cc_id")));
                    row.put("cta_id",String.valueOf(rs.getInt("cta_id")));
                    row.put("cta",String.valueOf(rs.getString("cta")));
                    row.put("descripcion",String.valueOf(rs.getString("descripcion")));
                    row.put("debe",StringHelper.roundDouble(rs.getString("debe"),2));
                    row.put("haber",StringHelper.roundDouble(rs.getString("haber"),2));
                    return row;
                }
            }
        );
        return hm;
    }
    
    
    
    
    
    @Override
    public ArrayList<HashMap<String, Object>> getMonedas() {
        String sql_to_query = "SELECT id, descripcion, descripcion_abr FROM  gral_mon WHERE borrado_logico=FALSE AND ventas=TRUE ORDER BY id ASC;";
        //log.log(Level.INFO, "Ejecutando query de {0}", sql_to_query);
        ArrayList<HashMap<String, Object>> hm_monedas = (ArrayList<HashMap<String, Object>>) this.jdbcTemplate.query(
            sql_to_query,
            new Object[]{}, new RowMapper(){
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    HashMap<String, Object> row = new HashMap<String, Object>();
                    row.put("id",String.valueOf(rs.getInt("id")));
                    row.put("descripcion",rs.getString("descripcion"));
                    row.put("descripcion_abr",rs.getString("descripcion_abr"));
                    return row;
                }
            }
        );
        return hm_monedas;
    }

    @Override
    public ArrayList<HashMap<String, Object>> getPolizasContables_TiposPolizas(Integer id_empresa) {
        String sql_to_query = "select id, titulo from ctb_tpol where borrado_logico=false and empresa_id=?;";
        //log.log(Level.INFO, "Ejecutando query de {0}", sql_to_query);
        ArrayList<HashMap<String, Object>> hm = (ArrayList<HashMap<String, Object>>) this.jdbcTemplate.query(
            sql_to_query,
            new Object[]{new Integer(id_empresa)}, new RowMapper(){
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
    public ArrayList<HashMap<String, Object>> getPolizasContables_Conceptos(Integer id_empresa) {
        String sql_to_query = "select id, titulo from ctb_con where borrado_logico=false and empresa_id=?;";
        //log.log(Level.INFO, "Ejecutando query de {0}", sql_to_query);
        ArrayList<HashMap<String, Object>> hm = (ArrayList<HashMap<String, Object>>) this.jdbcTemplate.query(
            sql_to_query,
            new Object[]{new Integer(id_empresa)}, new RowMapper(){
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
    
    
    //Obtener la lista de centros de costo
    @Override
    public ArrayList<HashMap<String, Object>> getPolizasContables_CentrosCostos(Integer id_empresa, Integer id_sucursal) {
        String sql_to_query = "";
        
        if(id_sucursal<=0){
            //Busca en todas la empresa
            sql_to_query = "select id, titulo from ctb_cc where borrado_logico=false and empresa_id=?;";
        }else{
            //Busca solo de la sucursal
            sql_to_query = "select id, titulo from ctb_cc where borrado_logico=false and empresa_id=? and gral_suc_id="+id_sucursal+";";
        }
        
        //log.log(Level.INFO, "Ejecutando query de {0}", sql_to_query);
        ArrayList<HashMap<String, Object>> hm = (ArrayList<HashMap<String, Object>>) this.jdbcTemplate.query(
            sql_to_query,
            new Object[]{new Integer(id_empresa)}, new RowMapper(){
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
    public ArrayList<HashMap<String, Object>> getPolizasContables_CuentasMayor(Integer id_empresa) {
        String sql_query = ""
                + "SELECT "
                    + "id, "
                    + "ctb_may_clase_id AS cta_mayor, "
                    + "clasificacion, "
                    + "'('||ctb_may_clase_id||','||clasificacion||') '||(CASE WHEN descripcion IS NULL OR descripcion='' THEN (CASE WHEN descripcion_ing IS NULL OR descripcion_ing='' THEN descripcion_ing ELSE descripcion_otr END) ELSE descripcion END) AS descripcion "
                + "FROM ctb_may "
                + "WHERE borrado_logico=false "
                + "AND empresa_id=?;";
        ArrayList<HashMap<String, Object>> hm = (ArrayList<HashMap<String, Object>>) this.jdbcTemplate.query(
            sql_query,
            new Object[]{new Integer(id_empresa)}, new RowMapper() {
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    HashMap<String, Object> row = new HashMap<String, Object>();
                    row.put("id",String.valueOf(rs.getInt("id")));
                    row.put("cta_mayor",String.valueOf(rs.getInt("cta_mayor")));
                    row.put("clasificacion",String.valueOf(rs.getInt("clasificacion")));
                    row.put("descripcion",rs.getString("descripcion"));
                    return row;
                }
            }
        );
        return hm;
    }
    
    
    
    
    @Override
    public ArrayList<HashMap<String, Object>> getPolizasContables_TiposDeMovimiento(Integer id_empresa) {
        
        String sql_query = "SELECT id, titulo FROM ctb_tmov WHERE borrado_logico=false AND gral_emp_id=?;";
        
        ArrayList<HashMap<String, Object>> hm = (ArrayList<HashMap<String, Object>>) this.jdbcTemplate.query(
            sql_query,
            new Object[]{new Integer(id_empresa)}, new RowMapper() {
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
    
    
    
    
    
    
    //Metodo para el buscador de cuentas contables
    @Override
    public ArrayList<HashMap<String, String>> getPolizasContables_CuentasContables(Integer clase_cta_mayor, Integer clasificacion, Integer detalle, String clasifica, String cta, String scta, String sscta, String ssscta, String sssscta, String descripcion, Integer id_empresa) {
        
        String where="";
        
        if(detalle>0){
            where+=" AND ctb_cta.detalle="+detalle+" ";
        }
        
	if(clase_cta_mayor != 0){
            where+=" AND ctb_cta.cta_mayor="+clase_cta_mayor+" ";
	}
        
	if(clasificacion != 0){
            where+=" AND ctb_cta.clasifica="+clasificacion+" ";
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
            where+=" AND ctb_cta.descripcion ilike '%"+descripcion+"%'";
	}
        
        String sql_query = ""
        + "SELECT DISTINCT "
            + "ctb_cta.id, "
            + "ctb_cta.cta_mayor, "
            + "ctb_cta.clasifica, "
            + "(case when ctb_cta.cta=0 then '' else ctb_cta.cta::character varying end) as cta,"
            + "(case when ctb_cta.subcta=0 then '' else ctb_cta.subcta::character varying end) as subcta,"
            + "(case when ctb_cta.ssubcta=0 then '' else ctb_cta.ssubcta::character varying end) as ssubcta,"
            + "(case when ctb_cta.sssubcta=0 then '' else ctb_cta.sssubcta::character varying end) as sssubcta,"
            + "(case when ctb_cta.ssssubcta=0 then '' else ctb_cta.ssssubcta::character varying end) as ssssubcta, "
            + "(CASE WHEN nivel_cta=1 THEN lpad(ctb_cta.cta::character varying, 4, '0')   "
            + "WHEN ctb_cta.nivel_cta=2 THEN '&nbsp;&nbsp;&nbsp;'||lpad(ctb_cta.cta::character varying, 4, '0')||'-'||lpad(ctb_cta.subcta::character varying, 4, '0') "
            + "WHEN ctb_cta.nivel_cta=3 THEN '&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;'||lpad(ctb_cta.cta::character varying, 4, '0')||'-'||lpad(ctb_cta.subcta::character varying, 4, '0')||'-'||lpad(ctb_cta.ssubcta::character varying, 4, '0') "
            + "WHEN ctb_cta.nivel_cta=4 THEN '&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;'||lpad(ctb_cta.cta::character varying, 4, '0')||'-'||lpad(ctb_cta.subcta::character varying, 4, '0')||'-'||lpad(ctb_cta.ssubcta::character varying, 4, '0')||'-'||lpad(ctb_cta.sssubcta::character varying, 4, '0') "
            + "WHEN ctb_cta.nivel_cta=5 THEN '&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;'||lpad(ctb_cta.cta::character varying, 4, '0')||'-'||lpad(ctb_cta.subcta::character varying, 4, '0')||'-'||lpad(ctb_cta.ssubcta::character varying, 4, '0')||'-'||lpad(ctb_cta.sssubcta::character varying, 4, '0')||'-'||lpad(ctb_cta.ssssubcta::character varying, 4, '0') "
            + "ELSE '' "
            + "END ) AS cuenta, "
            + "(CASE WHEN ctb_cta.descripcion IS NULL OR ctb_cta.descripcion='' THEN  (CASE WHEN ctb_cta.descripcion_ing IS NULL OR ctb_cta.descripcion_ing='' THEN  ctb_cta.descripcion_otr ELSE ctb_cta.descripcion_ing END )  ELSE descripcion END ) AS descripcion, "
            + "(CASE WHEN ctb_cta.detalle=0 THEN 'NO' WHEN ctb_cta.detalle=1 THEN 'SI' ELSE '' END) AS detalle, "
            + "ctb_cta.nivel_cta, "
            + "ctb_cta.ctb_cc_id "
        + "FROM ctb_cta "
        + "WHERE ctb_cta.borrado_logico=false AND ctb_cta.gral_emp_id=? "+ where +" "
        + "ORDER BY ctb_cta.id;";
        
        //System.out.println("sql_query: "+sql_query);
        //System.out.println("detalle: "+detalle);

        ArrayList<HashMap<String, String>> hm = (ArrayList<HashMap<String, String>>) this.jdbcTemplate.query(
            sql_query,
            new Object[]{new Integer(id_empresa)}, new RowMapper() {
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    HashMap<String, String> row = new HashMap<String, String>();
                    row.put("id",String.valueOf(rs.getInt("id")));
                    row.put("m",String.valueOf(rs.getInt("cta_mayor")));
                    row.put("c",String.valueOf(rs.getInt("clasifica")));
                    row.put("cta",rs.getString("cta"));
                    row.put("subcta",rs.getString("subcta"));
                    row.put("ssubcta",rs.getString("ssubcta"));
                    row.put("sssubcta",rs.getString("sssubcta"));
                    row.put("ssssubcta",rs.getString("ssssubcta"));
                    row.put("cuenta",rs.getString("cuenta"));
                    row.put("descripcion",rs.getString("descripcion"));
                    row.put("detalle",rs.getString("detalle"));
                    row.put("nivel_cta",String.valueOf(rs.getInt("nivel_cta")));
                    row.put("cc_id",String.valueOf(rs.getInt("ctb_cc_id")));
                    return row;
                }
            }
        );
        return hm;
    }
    
    
    
    
    
    //Metodo para obtener los datos de una cuenta contable en especifico.
    @Override
    public ArrayList<HashMap<String, Object>> getDatosCuentaContable(Integer detalle, String cta, String scta, String sscta, String ssscta, String sssscta, Integer id_empresa, Integer id_sucursal) {
        
        String where="";
        
	if(id_sucursal>0){
            where+=" AND ctb_cta.gral_suc_id="+id_sucursal+" ";
	}
        
	if(detalle>0){
            where+=" AND ctb_cta.detalle="+detalle+" ";
	}
        
        /*
	if(!cta.equals("")){
            where+=" AND ctb_cta.cta="+cta.trim()+" ";
	}
        */
	if(!scta.trim().equals("")){
            where+=" AND ctb_cta.subcta="+scta.trim()+" ";
	}
        
	if(!sscta.trim().equals("")){
            where+=" AND ctb_cta.ssubcta="+sscta.trim()+" ";
	}
        
	if(!ssscta.equals("")){
            where+=" AND ctb_cta.sssubcta="+ssscta.trim()+" ";
	}
        
	if(!sssscta.equals("")){
            where+=" AND ctb_cta.ssssubcta="+sssscta.trim()+" ";
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
            + "(CASE WHEN nivel_cta=1 THEN lpad(ctb_cta.cta::character varying, 4, '0')   "
            + "WHEN ctb_cta.nivel_cta=2 THEN lpad(ctb_cta.cta::character varying, 4, '0')||'-'||lpad(ctb_cta.subcta::character varying, 4, '0') "
            + "WHEN ctb_cta.nivel_cta=3 THEN lpad(ctb_cta.cta::character varying, 4, '0')||'-'||lpad(ctb_cta.subcta::character varying, 4, '0')||'-'||lpad(ctb_cta.ssubcta::character varying, 4, '0') "
            + "WHEN ctb_cta.nivel_cta=4 THEN lpad(ctb_cta.cta::character varying, 4, '0')||'-'||lpad(ctb_cta.subcta::character varying, 4, '0')||'-'||lpad(ctb_cta.ssubcta::character varying, 4, '0')||'-'||lpad(ctb_cta.sssubcta::character varying, 4, '0') "
            + "WHEN ctb_cta.nivel_cta=5 THEN lpad(ctb_cta.cta::character varying, 4, '0')||'-'||lpad(ctb_cta.subcta::character varying, 4, '0')||'-'||lpad(ctb_cta.ssubcta::character varying, 4, '0')||'-'||lpad(ctb_cta.sssubcta::character varying, 4, '0')||'-'||lpad(ctb_cta.ssssubcta::character varying, 4, '0') "
            + "ELSE '' "
            + "END ) AS cuenta, "
            + "(CASE WHEN ctb_cta.descripcion IS NULL OR ctb_cta.descripcion='' THEN  (CASE WHEN ctb_cta.descripcion_ing IS NULL OR ctb_cta.descripcion_ing='' THEN  ctb_cta.descripcion_otr ELSE ctb_cta.descripcion_ing END )  ELSE descripcion END ) AS descripcion, "
            + "(CASE WHEN ctb_cta.detalle=0 THEN 'NO' WHEN ctb_cta.detalle=1 THEN 'SI' ELSE '' END) AS detalle, "
            + "ctb_cta.nivel_cta,"
            + "ctb_cta.ctb_cc_id "
        + "FROM ctb_cta "
        + "WHERE ctb_cta.borrado_logico=false "
        + "AND ctb_cta.gral_emp_id=? and ctb_cta.cta="+cta.trim()+" "+ where +" "
        + "ORDER BY ctb_cta.id;";
        
        //System.out.println("sql_query: "+sql_query);
        //System.out.println("cta:"+cta+"\nscta:"+scta+"\nsscta:"+sscta+"\nssscta:"+ssscta+"\nsssscta:"+sssscta);
        
        ArrayList<HashMap<String, Object>> hm = (ArrayList<HashMap<String, Object>>) this.jdbcTemplate.query(
            sql_query,
            new Object[]{new Integer(id_empresa)}, new RowMapper() {
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    HashMap<String, Object> row = new HashMap<String, Object>();
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
                    row.put("cc_id",String.valueOf(rs.getInt("ctb_cc_id")));
                    return row;
                }
            }
        );
        return hm;
    }
    
    
    
    
    

    //Obtener los años activos para mostrar en Polizas contables
    //Estos años se utilizan para crear y modificar polizas
    @Override
    public ArrayList<HashMap<String, Object>>  getPolizasContables_Anios(Integer id_empresa) {
        String sql_query = "SELECT anio as valor FROM ctb_pol_anios WHERE gral_emp_id=? and cerrado=false;";
        
        ArrayList<HashMap<String, Object>> anios = (ArrayList<HashMap<String, Object>>) this.jdbcTemplate.query(
            sql_query,
            new Object[]{new Integer(id_empresa)}, new RowMapper() {
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    HashMap<String, Object> row = new HashMap<String, Object>();
                    row.put("valor",rs.getInt("valor"));
                    return row;
                }
            }
        );
        return anios;
    }
    
    
    /*
    Calcular años a mostrar en Polizas contables
    Estos años solo se utilizan para consulta de polizas
    */
    @Override
    public ArrayList<HashMap<String, Object>>  getPolizasContables_Anios2() {
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
    
    
    
    
    //Verificar si el usuario es Administrador
    @Override
    public Integer getUserRolAdmin(Integer id_user) {
        HashMap<String, Object> data = new HashMap<String, Object>();
        Integer velor_retorno=0;
        
        //verificar si el usuario tiene  rol de ADMINISTTRADOR
        //si exis es mayor que cero, el usuario si es ADMINISTRADOR
        String sql_to_query = "SELECT count(gral_usr_id) AS exis_rol_admin FROM gral_usr_rol WHERE gral_usr_id="+id_user+" AND gral_rol_id=1;";
        
        Map<String, Object> map = this.getJdbcTemplate().queryForMap(sql_to_query);
        
        velor_retorno = Integer.valueOf(map.get("exis_rol_admin").toString());
        
        return velor_retorno;
    }
    
    
    
    //METODOS PARA CONFIGURACION DE PARAMETROS DE CONTABILIDAD
    @Override
    public ArrayList<HashMap<String, Object>> getCtbPar_PaginaGrid(String data_string, int offset, int pageSize, String orderBy, String asc) {
        
        String sql_busqueda = "select id from gral_bus_catalogos(?) as foo (id integer)";
        
	String sql_to_query = ""
        + "SELECT DISTINCT "
            + "ctb_par.id,"
            + "(case when gral_suc.clave is null then '' else gral_suc.clave end) as clave, "
            + "ctb_par.gral_suc_id AS suc_id, "
            + "gral_suc.titulo AS sucursal "
        + "FROM ctb_par  "
        + "JOIN gral_suc ON gral_suc.id=ctb_par.gral_suc_id "
        + "JOIN ("+sql_busqueda+") as subt on subt.id=ctb_par.id "
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
    public ArrayList<HashMap<String, Object>> getCtbPar_Datos(Integer id) {
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
    public ArrayList<HashMap<String, Object>> getCtbPar_Almacenes(Integer id_emp, Integer id_suc) {
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
    public HashMap<String, Object> getCtb_Parametros(Integer id_emp, Integer id_suc) {
        HashMap<String, Object> mapDatos = new HashMap<String, Object>();
        String sql_query = "SELECT * FROM ctb_par WHERE gral_emp_id="+id_emp+" AND gral_suc_id="+id_suc+" and borrado_logico=false;";
        
        Map<String, Object> map = this.getJdbcTemplate().queryForMap(sql_query);

        mapDatos.put("suc_id_cons", map.get("gral_suc_id_cons"));
        mapDatos.put("mes_actual", map.get("mes_actual"));
        mapDatos.put("anio_actual", map.get("anio_actual"));
        
        return mapDatos;
    }
    
    
    
    //METODOS PARA APLICATIVO DE DEFINICION DE ASIENTOS CONTABLES
    @Override
    public ArrayList<HashMap<String, Object>> getCtbDefinicionAsientos_PaginaGrid(String data_string, int offset, int pageSize, String orderBy, String asc) {
        String sql_busqueda = "select id from gral_bus_catalogos(?) as foo (id integer)";
        
	String sql_to_query = ""
        + "select "
            + "ctb_tmov.id,"
            + "ctb_tmov.folio,"
            + "ctb_tmov.titulo as nombre,"
            + "(case when ctb_tpol.titulo is null then '' else ctb_tpol.titulo end) as tipo "
        + "from ctb_tmov "
        + "left join ctb_tpol on ctb_tpol.id=ctb_tmov.ctb_tpol_id "
        + "JOIN ("+sql_busqueda+") AS sbt ON sbt.id=ctb_tmov.id "
        + "order by "+orderBy+" "+asc+" limit ? OFFSET ?";
        
        //System.out.println("data_string: "+data_string);
        //System.out.println("sql_busqueda: "+sql_busqueda);
        //System.out.println("sql_to_query: "+sql_to_query);
        ArrayList<HashMap<String, Object>> hm = (ArrayList<HashMap<String, Object>>) this.jdbcTemplate.query(
            sql_to_query, 
            new Object[]{data_string, new Integer(pageSize),new Integer(offset)}, new RowMapper() {
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    HashMap<String, Object> row = new HashMap<String, Object>();
                    row.put("id",rs.getInt("id"));
                    row.put("folio",rs.getString("folio"));
                    row.put("nombre",rs.getString("nombre"));
                    row.put("tipo",rs.getString("tipo"));
                    return row;
                }
            }
        );
        return hm; 
    }
    
    
    
    
    @Override
    public ArrayList<HashMap<String, Object>> getCtbDefinicionAsientos_Datos(Integer id) {
        String sql_query = "select id,folio,titulo,fecha,ctb_tpol_id as tpol_id,pol_num, ctb_app_id as app_id from ctb_tmov where id=?;";
        
        //System.out.println(sql_query);
        ArrayList<HashMap<String, Object>> hm = (ArrayList<HashMap<String, Object>>) this.jdbcTemplate.query(
            sql_query, 
            new Object[]{new Integer(id)}, new RowMapper() {
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    HashMap<String, Object> row = new HashMap<String, Object>();
                    row.put("id",String.valueOf(rs.getInt("id")));
                    row.put("folio",rs.getString("folio"));
                    row.put("titulo",rs.getString("titulo"));
                    row.put("fecha",rs.getInt("fecha"));
                    row.put("tpol_id",rs.getInt("tpol_id"));
                    row.put("pol_num",rs.getInt("pol_num"));
                    row.put("app_id",rs.getInt("app_id"));
                    return row;
                }
            }
        );
        return hm;
    }
    
    
    @Override
    public ArrayList<HashMap<String, Object>> getCtbDefinicionAsientos_DatosGrid(Integer id) {
        String sql_query = ""
        + "select "
            + "ctb_tmov_det.id as id_det,"
            + "ctb_tmov_det.ctb_cta_id as cta_id,"
            + "(CASE WHEN nivel_cta=1 THEN lpad(ctb_cta.cta::character varying, 4, '0') "
            + "WHEN ctb_cta.nivel_cta=2 THEN lpad(ctb_cta.cta::character varying, 4, '0')||'-'||lpad(ctb_cta.subcta::character varying, 4, '0') "
            + "WHEN ctb_cta.nivel_cta=3 THEN lpad(ctb_cta.cta::character varying, 4, '0')||'-'||lpad(ctb_cta.subcta::character varying, 4, '0')||'-'||lpad(ctb_cta.ssubcta::character varying, 4, '0') "
            + "WHEN ctb_cta.nivel_cta=4 THEN lpad(ctb_cta.cta::character varying, 4, '0')||'-'||lpad(ctb_cta.subcta::character varying, 4, '0')||'-'||lpad(ctb_cta.ssubcta::character varying, 4, '0')||'-'||lpad(ctb_cta.sssubcta::character varying, 4, '0') "
            + "WHEN ctb_cta.nivel_cta=5 THEN lpad(ctb_cta.cta::character varying, 4, '0')||'-'||lpad(ctb_cta.subcta::character varying, 4, '0')||'-'||lpad(ctb_cta.ssubcta::character varying, 4, '0')||'-'||lpad(ctb_cta.sssubcta::character varying, 4, '0')||'-'||lpad(ctb_cta.ssssubcta::character varying, 4, '0') "
            + "ELSE '' "
            + "END) AS cuenta, "
            + "ctb_cta.descripcion,"
            + "ctb_tmov_det.mov_tipo,"
            + "ctb_tmov_det.detalle, "
            + "ctb_tmov_det.campo "
        + "from ctb_tmov_det "
        + "join ctb_cta on ctb_cta.id=ctb_tmov_det.ctb_cta_id "
        + "where ctb_tmov_det.ctb_tmov_id=?;";
        
        //System.out.println(sql_query);
        ArrayList<HashMap<String, Object>> hm = (ArrayList<HashMap<String, Object>>) this.jdbcTemplate.query(
            sql_query,  
            new Object[]{new Integer(id)}, new RowMapper() {
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    HashMap<String, Object> row = new HashMap<String, Object>();
                    row.put("id_det",rs.getInt("id_det"));
                    row.put("cta_id",rs.getInt("cta_id"));
                    row.put("cuenta",rs.getString("cuenta"));
                    row.put("descripcion",rs.getString("descripcion"));
                    row.put("mov_tipo",rs.getInt("mov_tipo"));
                    row.put("detalle",rs.getBoolean("detalle"));
                    row.put("campo",rs.getInt("campo"));
                    return row;
                }
            }
        );
        return hm;
    }
    
    
    //Arma lista de Campos que contiene valores para utilizar en la generación de pólizas
    @Override
    public ArrayList<Object> getCtbDefinicionAsientos_Campos() {
        ArrayList<Object> arreglo = new ArrayList<Object>();
        HashMap<String, Object> row = null;
        
        row = new HashMap<String, Object>();
        row.put("id",1);
        row.put("titulo","Subtotal");
        arreglo.add(row);
        
        row = new HashMap<String, Object>();
        row.put("id",2);
        row.put("titulo","Total");
        arreglo.add(row);
        
        row = new HashMap<String, Object>();
        row.put("id",3);
        row.put("titulo","I.V.A.");
        arreglo.add(row);
        
        row = new HashMap<String, Object>();
        row.put("id",4);
        row.put("titulo","Retención I.V.A.");
        arreglo.add(row);
        
        row = new HashMap<String, Object>();
        row.put("id",5);
        row.put("titulo","I.E.P.S.");
        arreglo.add(row);
        
        row = new HashMap<String, Object>();
        row.put("id",6);
        row.put("titulo","I.S.R.");
        arreglo.add(row);
        
        return arreglo;
    }
    
    
    
    //Metodo para busqueda de movimientos para la Generacion de Polizas
    @Override
    public ArrayList<HashMap<String, Object>> getCtbGeneraPolizas_busquedaDatos(String data_string) {
        
        String sql_to_query = "select * from ctb_reporte(?) as foo(fecha date, ref_id integer, referencia character varying, descripcion character varying, total character varying, tmov_id integer, tipo_mov character varying, app_id integer) order by tmov_id, fecha;"; 
        //System.out.println("data_string: "+data_string);
        //System.out.println("CtbGeneraPoliza:: "+sql_to_query);
        ArrayList<HashMap<String, Object>> hm = (ArrayList<HashMap<String, Object>>) this.jdbcTemplate.query(
            sql_to_query,
            new Object[]{data_string}, new RowMapper(){
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    HashMap<String, Object> row = new HashMap<String, Object>();
                    row.put("fecha",rs.getString("fecha"));
                    row.put("referencia",rs.getString("referencia"));
                    row.put("descripcion",rs.getString("descripcion"));
                    row.put("total",StringHelper.roundDouble(rs.getString("total"),2));
                    row.put("tipo_mov",rs.getString("tipo_mov"));
                    return row;
                }
            }
        );
        return hm;
    }
    
    
    //Buscador de cuentas agrupadoras del sat
    @Override
    public ArrayList<HashMap<String, Object>>  getBuscadorCuentasAgrupadorasSat(String codigo, String descripcion) {
        String sql_query = "SELECT id,nivel,codigo,nombre FROM ctb_cta_sat WHERE borrado_logico=false and codigo ilike '%"+codigo+"%' and nombre ilike '%"+descripcion+"%';";
        
        ArrayList<HashMap<String, Object>> anios = (ArrayList<HashMap<String, Object>>) this.jdbcTemplate.query(
            sql_query,
            new Object[]{}, new RowMapper() {
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    HashMap<String, Object> row = new HashMap<String, Object>();
                    row.put("id",rs.getInt("id"));
                    row.put("nivel",rs.getInt("nivel"));
                    row.put("codigo",rs.getString("codigo"));
                    row.put("nombre",rs.getString("nombre"));
                    return row;
                }
            }
        );
        return anios;
    }
    
    
    //Buscador datos de una cuenta especifica
    @Override
    public ArrayList<HashMap<String, Object>>  getDataCtaSat(String codigo) {
        String sql_query = "SELECT id,nivel,codigo,nombre FROM ctb_cta_sat WHERE borrado_logico=false and upper(trim(codigo))='"+codigo.toUpperCase().trim()+"';";
        
        ArrayList<HashMap<String, Object>> anios = (ArrayList<HashMap<String, Object>>) this.jdbcTemplate.query(
            sql_query,
            new Object[]{}, new RowMapper() {
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    HashMap<String, Object> row = new HashMap<String, Object>();
                    row.put("id",rs.getInt("id"));
                    row.put("nivel",rs.getInt("nivel"));
                    row.put("codigo",rs.getString("codigo"));
                    row.put("nombre",rs.getString("nombre"));
                    return row;
                }
            }
        );
        return anios;
    }
    
    
    //Obtiene los datos de las cuentas contables para el xml
    @Override
    public ArrayList<LinkedHashMap<String,String>> getCtbXml_CuentasContables(String data_string) {
        
        String sql_to_query = "select * from ctb_reporte(?) as foo(codagrup character varying,numcta text,subctade text,descripcion character varying,natur text,nivel character varying);"; 
        //System.out.println("data_string: "+data_string);
        //System.out.println("getCtbXml_CuentasContables= "+sql_to_query);
        ArrayList<LinkedHashMap<String,String>> hm_facturas = (ArrayList<LinkedHashMap<String,String>>) this.jdbcTemplate.query(
            sql_to_query,
            new Object[]{data_string}, new RowMapper(){
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    LinkedHashMap<String, String> row = new LinkedHashMap<String, String>();
                    row.put("codAgrup",rs.getString("codagrup"));
                    row.put("numCta",rs.getString("numcta"));
                    row.put("subCtaDe",rs.getString("subctade"));
                    row.put("desc",rs.getString("descripcion"));
                    row.put("natur",rs.getString("natur"));
                    row.put("nivel",rs.getString("nivel"));
                    
                    return row;
                }
            }
        );
        return hm_facturas;
    }
    
}
