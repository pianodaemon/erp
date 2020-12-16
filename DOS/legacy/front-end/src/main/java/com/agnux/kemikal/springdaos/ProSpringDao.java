/**
 *
 * @author fmora
 * fmora@agnux.com
 * 27/07/2012
 */
package com.agnux.kemikal.springdaos;

import com.agnux.common.helpers.StringHelper;
import com.agnux.kemikal.interfacedaos.ProInterfaceDao;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;


/**
 *
 *
 */

public class ProSpringDao implements ProInterfaceDao{
    private JdbcTemplate jdbcTemplate;

    public JdbcTemplate getJdbcTemplate() {
        return jdbcTemplate;
    }

    public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }


    @Override
    public HashMap<String, String> selectFunctionValidateAaplicativo(String data, Integer idApp, String extra_data_array){
        String sql_to_query = "select erp_fn_validaciones_por_aplicativo from erp_fn_validaciones_por_aplicativo('"+data+"',"+idApp+",array["+extra_data_array+"]);";
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



    @Override
    public String selectFunctionForThisApp(String campos_data, String extra_data_array) {
        String sql_to_query = "select * from pro_adm_procesos('"+campos_data+"',array["+extra_data_array+"]);";
        //log.log(Level.INFO, "Ejecutando query de {0}", sql_to_query);
        System.out.println("Ejacutando pro_adm_procesos:"+sql_to_query);
        
        //int update = this.getJdbcTemplate().queryForInt(sql_to_query);
        String valor_retorno="";

        Map<String, Object> update = this.getJdbcTemplate().queryForMap(sql_to_query);

        valor_retorno = update.get("pro_adm_procesos").toString();

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
    public String selectFunctionForApp_Produccion(String campos_data, String extra_data_array) {
        String sql_to_query = "select * from pro_adm_procesos('"+campos_data+"',array["+extra_data_array+"]);";
        //log.log(Level.INFO, "Ejecutando query de {0}", sql_to_query);
        System.out.println("Ejacutando pro_adm_procesos:"+sql_to_query);
        //int update = this.getJdbcTemplate().queryForInt(sql_to_query);
        String valor_retorno="";

        Map<String, Object> update = this.getJdbcTemplate().queryForMap(sql_to_query);

        valor_retorno = update.get("pro_adm_procesos").toString();

        return valor_retorno;
    }


    @Override
    public HashMap<String, String> getDocumentoByName(String doc, Integer id_empresa, Integer id_sucursal) {

        String sql_to_query = "select * from pro_docs_calidad where borrado_logico=false and titulo ilike '"+doc+"' and gral_emp_id="+id_empresa+" and gral_suc_id="+id_sucursal;

        //log.log(Level.INFO, "Ejecutando query de {0}", sql_to_query);

        HashMap<String, String> hm = (HashMap<String, String>) this.jdbcTemplate.queryForObject(
            sql_to_query,
            new Object[]{}, new RowMapper() {
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    HashMap<String, String> row = new HashMap<String, String>();
                    row.put("id",String.valueOf(rs.getInt("id")));
                    row.put("titulo",rs.getString("titulo"));
                    row.put("archivo",rs.getString("archivo"));

                    return row;
                }
            }
        );

        return hm;
    }


    //catalogo de formulas de Productos
    @Override
    public ArrayList<HashMap<String, String>> getFormulas_PaginaGrid(String data_string, int offset, int pageSize, String orderBy, String asc) {

        String sql_busqueda = "SELECT id FROM gral_bus_catalogos('"+data_string+"') AS foo (id integer)";

	String sql_to_query = "SELECT pro_estruc.id, inv_prod.sku as codigo, inv_prod.descripcion, pro_estruc.version "
                            + "FROM pro_estruc JOIN ("+sql_busqueda+") as subt on subt.id=pro_estruc.id "
                + "JOIN inv_prod ON inv_prod.id=pro_estruc.inv_prod_id  "
                            +"order by "+orderBy+" "+asc+" limit ? OFFSET ?";

        //System.out.println("Busqueda GetPage: "+sql_to_query+"   "+data_string);

        ArrayList<HashMap<String, String>> hm = (ArrayList<HashMap<String, String>>) this.jdbcTemplate.query(
            sql_to_query,
            new Object[]{new Integer(pageSize),new Integer(offset)}, new RowMapper() {
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    HashMap<String, String> row = new HashMap<String, String>();
                    row.put("id",String.valueOf(rs.getInt("id")));
                    row.put("codigo",rs.getString("codigo"));
                    row.put("descripcion",rs.getString("descripcion"));
                    row.put("version",String.valueOf(rs.getInt("version")));

                    return row;
                }
            }
        );
        return hm;
    }


    @Override
    public ArrayList<HashMap<String, String>> getFormulasByProd(String id_prod,Integer emp_id) {
        //String sql_to_query = "SELECT DISTINCT cve_pais ,pais_ent FROM municipios;";
        /*String sql_to_query = "select inv_formulas.nivel, inv_prod.sku, inv_prod.descripcion, inv_formulas.inv_prod_id_master from "
                + "inv_formulas join inv_prod on inv_prod.id=inv_formulas.inv_prod_id_master where inv_prod_id="+id_prod+" group by "
                + "inv_formulas.nivel, inv_prod.sku, inv_prod.descripcion, inv_formulas.inv_prod_id_master";
        */

        String sql_to_query = "select inv_prod.sku, inv_prod.descripcion,pro_estruc.inv_prod_id, pro_estruc.id, pro_estruc.version  "
                + "from pro_estruc join inv_prod on inv_prod.id=pro_estruc.inv_prod_id "
                + "where pro_estruc.inv_prod_id="+id_prod+" and pro_estruc.borrado_logico=false and pro_estruc.gral_emp_id="+emp_id;

        ArrayList<HashMap<String, String>> docs = (ArrayList<HashMap<String, String>>) this.jdbcTemplate.query(
            sql_to_query,
            new Object[]{}, new RowMapper(){
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    HashMap<String, String> row = new HashMap<String, String>();
                    row.put("id",String.valueOf(rs.getInt("id")));
                    row.put("inv_prod_id",String.valueOf(rs.getInt("inv_prod_id")));
                    row.put("sku",rs.getString("sku"));
                    row.put("descripcion",rs.getString("descripcion"));
                    row.put("version",String.valueOf(rs.getInt("version")));
                    return row;
                }
            }
        );
        return docs;
    }


    //obtiene los datos del grid de formulas de Productos en produccion
    @Override
    public ArrayList<HashMap<String, Object>> getFormulasLaboratorio_PaginaGrid(String data_string, int offset, int pageSize, String orderBy, String asc) {

        String sql_busqueda = "SELECT id FROM gral_bus_catalogos('"+data_string+"') AS foo (id integer)";

	String sql_to_query = "SELECT pro_estruc.id, inv_prod.sku as codigo, inv_prod.descripcion, pro_estruc.version, inv_prod_unidades.titulo as unidad "
                            + "FROM pro_estruc JOIN ("+sql_busqueda+") as subt on subt.id=pro_estruc.id "
                + "JOIN inv_prod ON inv_prod.id=pro_estruc.inv_prod_id  "
                + "left join inv_prod_unidades on inv_prod_unidades.id=inv_prod.unidad_id "
                            +"order by "+orderBy+" "+asc+" limit ? OFFSET ?";

        //System.out.println("Busqueda GetPage: "+sql_to_query+"   "+data_string);

        ArrayList<HashMap<String, Object>> hm = (ArrayList<HashMap<String, Object>>) this.jdbcTemplate.query(
            sql_to_query,
            new Object[]{new Integer(pageSize),new Integer(offset)}, new RowMapper() {
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    HashMap<String, Object> row = new HashMap<String, Object>();
                    row.put("id",String.valueOf(rs.getInt("id")));
                    row.put("codigo",rs.getString("codigo"));
                    row.put("descripcion",rs.getString("descripcion"));
                    row.put("unidad",rs.getString("unidad"));
                    row.put("version",String.valueOf(rs.getInt("version")));

                    return row;
                }
            }
        );
        return hm;
    }


    //obtiene los datos de una formula de laboratorio
    @Override
    public ArrayList<HashMap<String, String>> getFormulaLaboratorio_Datos(String id_formula) {

        String sql_to_query =    " select pro_estruc.id, pro_estruc.inv_prod_id,inv_prod.sku as codigo, inv_prod.descripcion, "
                + "inv_prod_tipos.id as tipo_producto_id, inv_prod_tipos.titulo as tipo_producto,inv_prod_unidades.titulo_abr as unidad, "
                + "pro_estruc.pro_estruc_id, pro_estruc.version from pro_estruc join inv_prod on "
                + "inv_prod.id=pro_estruc.inv_prod_id join  inv_prod_unidades on inv_prod_unidades.id=inv_prod.unidad_id join inv_prod_tipos "
                + "on inv_prod_tipos.id=inv_prod.tipo_de_producto_id where pro_estruc.id="+id_formula;

        //System.out.println("segundo query"+sql_to_query);
        ArrayList<HashMap<String, String>> datos_formulas = (ArrayList<HashMap<String, String>>) this.jdbcTemplate.query(
            sql_to_query,
            new Object[]{}, new RowMapper(){
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    HashMap<String, String> row = new HashMap<String, String>();
                    row.put("id",String.valueOf(rs.getInt("id")));
                    row.put("pro_estruc_id",String.valueOf(rs.getInt("pro_estruc_id")));
                    row.put("inv_prod_id",String.valueOf(rs.getInt("inv_prod_id")));
                    row.put("codigo",rs.getString("codigo"));
                    row.put("descripcion",rs.getString("descripcion"));
                    row.put("tipo_producto",rs.getString("tipo_producto"));
                    row.put("tipo_producto_id",String.valueOf(rs.getInt("tipo_producto_id")));
                    row.put("unidad",rs.getString("unidad"));
                    row.put("version",String.valueOf(rs.getInt("version")));

                    return row;
                }
            }
        );
        return datos_formulas;
    }



    //:::::::::::CONSULTA QUE RETORNA EL GRID DE FORMULAS EN DESARROLLO DE ACUERDO A SU PASO::::::::::::::::::::::::
    @Override
    public ArrayList<HashMap<String, String>> getFormulaLaboratorio_DatosMinigrid(String id_tabla_formula, String id_nivel) {
        /*
         String sql_to_query = "select pro_estruc_det.id,inv_prod.sku AS codigo,  inv_prod.descripcion, pro_estruc_det.nivel, "
                 + "pro_estruc_det.elemento, pro_estruc_det.inv_prod_id, pro_estruc_det.cantidad, "
                + "(select version from pro_estruc where pro_estruc_id=(select pro_estruc_id from pro_estruc where id="+id_tabla_formula+") "
                + "order by version desc limit 1) as version  from pro_estruc_det JOIN "
                 + "inv_prod ON inv_prod.id=pro_estruc_det.inv_prod_id where pro_estruc_id="+id_tabla_formula+" and pro_estruc_det.nivel="+id_nivel+" "
                 + "order by pro_estruc_det.elemento asc";
         */

        String sql_to_query = "select pro_estruc_det.id,inv_prod.sku AS codigo,  inv_prod.descripcion, pro_estruc_det.nivel, pro_estruc_det.elemento, "
                + "pro_estruc_det.inv_prod_id, pro_estruc_det.cantidad, (select version from pro_estruc where id=723 and version="+id_tabla_formula+") as version  from pro_estruc_det "
                + "JOIN inv_prod ON inv_prod.id=pro_estruc_det.inv_prod_id where pro_estruc_id="+id_tabla_formula+" and pro_estruc_det.nivel="+id_nivel+" "
                + "order by pro_estruc_det.elemento asc";

        //.get("inv_prod_id_master").toString();
        //System.out.println("Checar este query??"+sql_to_query);
        ArrayList<HashMap<String, String>> datos_formulas_minigrid = (ArrayList<HashMap<String, String>>) this.jdbcTemplate.query(
            sql_to_query,
            new Object[]{}, new RowMapper(){
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    HashMap<String, String> row = new HashMap<String, String>();

                    row.put("id",String.valueOf(rs.getInt("id")));
                    row.put("inv_prod_id",String.valueOf(rs.getInt("inv_prod_id")));
                    row.put("codigo",rs.getString("codigo"));
                    row.put("descripcion",rs.getString("descripcion"));
                    row.put("cantidad",StringHelper.roundDouble(rs.getString("cantidad"),4));
                    row.put("nivel",String.valueOf(rs.getInt("nivel")));
                    row.put("elemento",String.valueOf(rs.getInt("elemento")));
                    row.put("version",String.valueOf(rs.getInt("version")));

                    return row;
                }
            }
        );
        return datos_formulas_minigrid;
    }



    @Override
    public ArrayList<HashMap<String, String>> getDocumentos(String cadena) {
        //String sql_to_query = "SELECT DISTINCT cve_pais ,pais_ent FROM municipios;";
        String sql_to_query = "select id, titulo from pro_docs_calidad where borrado_logico=false and titulo ilike '"+cadena+"' limit 10";

        ArrayList<HashMap<String, String>> docs = (ArrayList<HashMap<String, String>>) this.jdbcTemplate.query(
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
        return docs;
    }


    @Override
    public ArrayList<HashMap<String, String>> getProOrdenOperariosDisponibles(String cadena, Integer id_empresa) {
        //String sql_to_query = "SELECT DISTINCT cve_pais ,pais_ent FROM municipios;";
        String sql_to_query = "select emp_tmp.id, emp_tmp.nombre_pila||' '||emp_tmp.apellido_paterno||' '||emp_tmp.apellido_materno as titulo from ("
                + "select id from gral_puestos where titulo ilike '%operad%' and gral_emp_id="+id_empresa+"  and borrado_logico=false ) as pu_tmp "
                + "join "
                + "gral_empleados as emp_tmp on emp_tmp.gral_puesto_id=pu_tmp.id  "
                + "where emp_tmp.nombre_pila||' '||emp_tmp.apellido_paterno||' '||emp_tmp.apellido_materno ilike '"+cadena+"' and "
                + "emp_tmp.gral_emp_id="+id_empresa+"  and emp_tmp.borrado_logico=false  order by "
                + "emp_tmp.nombre_pila||' '||emp_tmp.apellido_paterno||' '||emp_tmp.apellido_materno  limit 10";

        ArrayList<HashMap<String, String>> docs = (ArrayList<HashMap<String, String>>) this.jdbcTemplate.query(
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
        return docs;
    }



    @Override
    public ArrayList<HashMap<String, String>> getProOrdenEquipoDisponible(String cadena, Integer id_empresa) {
        //String sql_to_query = "SELECT DISTINCT cve_pais ,pais_ent FROM municipios;";
        String sql_to_query = "select id, titulo from pro_equipos where titulo ilike '"+cadena+"' and gral_emp_id="+id_empresa+" and borrado_logico=false;";

        ArrayList<HashMap<String, String>> docs = (ArrayList<HashMap<String, String>>) this.jdbcTemplate.query(
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
        return docs;
    }

    @Override
    public ArrayList<HashMap<String, String>> getProOrdenEquipoAdicionalDisponible(String cadena, Integer id_empresa) {
        //String sql_to_query = "SELECT DISTINCT cve_pais ,pais_ent FROM municipios;";
        String sql_to_query = "select id, titulo from pro_equipos_adic where titulo ilike '"+cadena+"' and gral_emp_id="+id_empresa+" and borrado_logico=false;";

        ArrayList<HashMap<String, String>> docs = (ArrayList<HashMap<String, String>>) this.jdbcTemplate.query(
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
        return docs;
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
        //System.out.println(sql_to_query);
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


    //obtiene tipos de productos
    @Override
    public ArrayList<HashMap<String, String>> getProducto_Tipos() {
	String sql_query = "SELECT DISTINCT id ,titulo FROM inv_prod_tipos WHERE borrado_logico=false and id in (2,1,8, 7) order by id;";
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


    //obtiene las presentaciones de un producto
    //cuando id=0, obtiene todas las presentaciones
    //cuando id es diferente de 0, obtiene las presentaciones no asignadas a un producto en especifico
    @Override
    public ArrayList<HashMap<String, String>> getProducto_Presentaciones(Integer id_producto) {
        String sql_query="";
        if(id_producto != 0){
            sql_query = "SELECT id,titulo FROM inv_prod_presentaciones WHERE id NOT IN (SELECT presentacion_id FROM  inv_prod_pres_x_prod WHERE producto_id = "+id_producto+") order by titulo;";
        }else{
            sql_query = "SELECT id,titulo FROM inv_prod_presentaciones order by titulo;";
        }

        ArrayList<HashMap<String, String>> hm_pres= (ArrayList<HashMap<String, String>>) this.jdbcTemplate.query(
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
        return hm_pres;
    }



    //busca producto en especifico
    @Override
    public ArrayList<HashMap<String, String>> getProducto_sku(String sku, Integer id_usuario) {

        String sql_to_query = "	SELECT gral_suc.empresa_id FROM gral_usr_suc "
                            + "JOIN gral_suc ON gral_suc.id = gral_usr_suc.gral_suc_id "
                            + "WHERE gral_usr_suc.gral_usr_id = "+id_usuario;

        int id_empresa = this.getJdbcTemplate().queryForInt(sql_to_query);

        String sql_query = "SELECT inv_prod.id, "
                                + "inv_prod.sku, "
                                + "inv_prod.descripcion,"
                                + "inv_prod_unidades.decimales  "
                            + "FROM inv_prod "
                            + "JOIN inv_prod_unidades ON inv_prod_unidades.id=inv_prod.unidad_id "
                            + "WHERE inv_prod.empresa_id = "+id_empresa+" AND inv_prod.sku='"+sku+"' AND inv_prod.tipo_de_producto_id != 2;";
        //System.out.println("Obteniendo datos sku:"+sql_query);

        ArrayList<HashMap<String, String>> hm_sku = (ArrayList<HashMap<String, String>>) this.jdbcTemplate.query(
            sql_query,
            new Object[]{}, new RowMapper() {
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    HashMap<String, String> row = new HashMap<String, String>();
                    row.put("id",rs.getString("id"));
                    row.put("sku",rs.getString("sku"));
                    row.put("descripcion",rs.getString("descripcion"));
                    row.put("decimales",String.valueOf(rs.getInt("decimales")));
                    return row;
                }
            }
        );
        return hm_sku;
    }




    @Override
    public ArrayList<HashMap<String, Object>> getProcesos_PaginaGrid(String data_string, int offset, int pageSize, String orderBy, String asc) {
        String sql_busqueda = "SELECT id FROM gral_bus_catalogos(?) AS foo (id integer)";

	String sql_to_query = "select pro_procesos.id, pro_procesos.titulo,pro_procesos.folio, inv_prod.descripcion as accesor_producto from pro_procesos "
                + "join inv_prod on inv_prod.id=pro_procesos.inv_prod_id JOIN ("+sql_busqueda+") as subt on subt.id=pro_procesos.id "
                        +"ORDER BY "+orderBy+" "+asc+" LIMIT ? OFFSET ?";

        //System.out.println("Busqueda GetPage: "+sql_to_query);

        //log.log(Level.INFO, "Ejecutando query de {0}", sql_to_query);
        ArrayList<HashMap<String, Object>> hm = (ArrayList<HashMap<String, Object>>) this.jdbcTemplate.query(
            sql_to_query,
            new Object[]{new String(data_string),new Integer(pageSize),new Integer(offset)}, new RowMapper() {
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    HashMap<String, Object> row = new HashMap<String, Object>();
                    row.put("id",rs.getString("id"));
                    row.put("titulo",rs.getString("titulo"));
                    row.put("folio",rs.getString("folio"));
                    row.put("accesor_producto",rs.getString("accesor_producto"));

                    return row;
                }
            }
        );
        return hm;
    }




    @Override
    public ArrayList<HashMap<String, String>> getProProceso_Datos(Integer id) {
        String sql_to_query = ""
                + "select pro_procesos.id, "
                    + "pro_procesos.titulo, "
                    + "pro_procesos.inv_prod_id,"
                    + "pro_procesos.dias_caducidad, "
                    + "inv_prod.sku, "
                    + "inv_prod.descripcion "
                + "from pro_procesos "
                + "join inv_prod on inv_prod.id=pro_procesos.inv_prod_id  "
                + "where pro_procesos.id="+ id + ";";

        //log.log(Level.INFO, "Ejecutando query de {0}", sql_to_query);
        ArrayList<HashMap<String, String>> hm_datos_entrada = (ArrayList<HashMap<String, String>>) this.jdbcTemplate.query(
            sql_to_query,
            new Object[]{}, new RowMapper(){
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    HashMap<String, String> row = new HashMap<String, String>();
                    row.put("id",rs.getString("id"));
                    row.put("titulo",rs.getString("titulo"));
                    row.put("inv_prod_id",rs.getString("inv_prod_id"));
                    row.put("sku",rs.getString("sku"));
                    row.put("descripcion",rs.getString("descripcion"));
                    row.put("dias_caducidad",String.valueOf(rs.getInt("dias_caducidad")));
                    return row;
                }
            }
        );
        return hm_datos_entrada;
    }



    @Override
    public ArrayList<HashMap<String, String>> getEntradas_DatosProveedor(Integer id_proveedor) {
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
                                    + "cxp_prov.telefono1 AS telefono, "
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
    public ArrayList<HashMap<String, String>> getProveedor_Contacto(Integer idProveedor) {
        ArrayList<HashMap<String, String>> contact = (ArrayList<HashMap<String, String>>) this.jdbcTemplate.query(
            "SELECT id,contacto FROM cxp_prov_contactos WHERE proveedor_id = ?",
            new Object[]{new Integer(idProveedor)}, new RowMapper() {
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    HashMap<String, String> row = new HashMap<String, String>();
                    row.put("id",String.valueOf(rs.getInt("id")));
                    row.put("contacto",rs.getString("contacto"));
                    return row;
                }
            }
        );
        return contact;
    }


    //:::::::::::CONSULTA QUE RETORNA EL GRID DE FORMULAS DE ACUERDO A SU PASO::::::::::::::::::::::::
    @Override
    public ArrayList<HashMap<String, String>> getFormula_DatosMinigrid(String id_tabla_formula, String id_nivel) {

         String sql_to_query = ""
                 + "select "
                     + "pro_estruc_det.id,inv_prod.sku AS codigo,  "
                     + "inv_prod.descripcion, "
                     + "pro_estruc_det.nivel, "
                     + "pro_estruc_det.elemento, "
                     + "pro_estruc_det.inv_prod_id, "
                     + "pro_estruc_det.cantidad  "
                 + "from pro_estruc_det "
                 + "JOIN inv_prod ON inv_prod.id=pro_estruc_det.inv_prod_id "
                 + "where pro_estruc_id="+id_tabla_formula+" and pro_estruc_det.nivel="+id_nivel+" "
                 + "order by pro_estruc_det.elemento asc";

                            //.get("inv_prod_id_master").toString();
        //System.out.println("Checar este query??"+sql_to_query);
        ArrayList<HashMap<String, String>> datos_formulas_minigrid = (ArrayList<HashMap<String, String>>) this.jdbcTemplate.query(
            sql_to_query,
            new Object[]{}, new RowMapper(){
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    HashMap<String, String> row = new HashMap<String, String>();

                    row.put("id",String.valueOf(rs.getInt("id")));
                    row.put("inv_prod_id",String.valueOf(rs.getInt("inv_prod_id")));
                    row.put("codigo",rs.getString("codigo"));
                    row.put("descripcion",rs.getString("descripcion"));
                    row.put("cantidad",StringHelper.roundDouble(rs.getString("cantidad"),4));
                    row.put("nivel",String.valueOf(rs.getInt("nivel")));
                    row.put("elemento",String.valueOf(rs.getInt("elemento")));

                    return row;
                }
            }
        );
        return datos_formulas_minigrid;
    }

    @Override
    public ArrayList<HashMap<String, String>> getFormula_DatosProductoSaliente(String id_tabla_formula, String id_nivel) {

        String sql_to_query = "select pro_estruc_det.id,inv_prod.sku AS codigo,  inv_prod.descripcion, pro_estruc_det.inv_prod_id_salida "
                + "from pro_estruc_det JOIN inv_prod ON inv_prod.id=pro_estruc_det.inv_prod_id_salida where pro_estruc_id="+id_tabla_formula+" "
                + "and pro_estruc_det.nivel="+id_nivel+" limit 1";


        //System.out.println("Checar este query??"+sql_to_query);
        ArrayList<HashMap<String, String>> datos_formulas_minigrid = (ArrayList<HashMap<String, String>>) this.jdbcTemplate.query(
            sql_to_query,
            new Object[]{}, new RowMapper(){
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    HashMap<String, String> row = new HashMap<String, String>();
                    row.put("id",String.valueOf(rs.getInt("id")));
                    row.put("inv_prod_id_salida",String.valueOf(rs.getInt("inv_prod_id_salida")));
                    row.put("codigo",rs.getString("codigo"));
                    row.put("descripcion",rs.getString("descripcion"));

                    return row;
                }
            }
        );
        return datos_formulas_minigrid;
    }


    @Override
    public ArrayList<HashMap<String, String>> getFormula_Datos(String id_formula) {

        String sql_to_query =    " select pro_estruc.id, pro_estruc.inv_prod_id,inv_prod.sku as codigo, inv_prod.descripcion, "
                + "inv_prod_tipos.titulo as tipo_producto,inv_prod_unidades.titulo_abr as unidad from pro_estruc join inv_prod on "
                + "inv_prod.id=pro_estruc.inv_prod_id join  inv_prod_unidades on inv_prod_unidades.id=inv_prod.unidad_id join inv_prod_tipos "
                + "on inv_prod_tipos.id=inv_prod.tipo_de_producto_id where pro_estruc.id="+id_formula;

        //System.out.println("segundo query"+sql_to_query);
        ArrayList<HashMap<String, String>> datos_formulas = (ArrayList<HashMap<String, String>>) this.jdbcTemplate.query(
            sql_to_query,
            new Object[]{}, new RowMapper(){
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    HashMap<String, String> row = new HashMap<String, String>();

                    row.put("id",String.valueOf(rs.getInt("id")));
                    row.put("inv_prod_id",String.valueOf(rs.getInt("inv_prod_id")));
                    row.put("codigo",rs.getString("codigo"));
                    row.put("descripcion",rs.getString("descripcion"));
                    row.put("tipo_producto",rs.getString("tipo_producto"));
                    row.put("unidad",rs.getString("unidad"));

                    return row;
                }
            }
        );
        return datos_formulas;
    }



    //:::::::::::::::para pdf de formulas hecho por Paco Mora::::::::::::::::::
    @Override
    public ArrayList<HashMap<String, Object>> getPro_ListaProductosFormulaPdf(Integer formula_id, String tc, String anoActual, String mesActual) {
        final String tipo_cambio = tc;
        final String ano = anoActual;
        //Convertimos  a Integer para elimiar un cero si viene con el mes por ejemplo(01=Enero)
        final String mes = String.valueOf(Integer.parseInt(mesActual));
        /*
        String sql_query = "select pro_estruc_det.inv_prod_id,(CASE WHEN inv_prod_tipos.id=1 THEN 'PT' WHEN  inv_prod_tipos.id=2 THEN 'PI' "
                + "WHEN  inv_prod_tipos.id=7  THEN 'MP' ELSE inv_prod.descripcion END) as tipo, inv_prod.sku, inv_prod.descripcion, "
                + "pro_estruc_det.cantidad, pro_estruc_det.elemento, inv_prod_tipos.id as id_tipo, inv_prod_unidades.titulo as unidad, pro_estruc_det.pro_estruc_id "
                + "from pro_estruc_det join inv_prod on inv_prod.id=pro_estruc_det.inv_prod_id join inv_prod_tipos on "
                + "inv_prod_tipos.id=inv_prod.tipo_de_producto_id join inv_prod_unidades on inv_prod_unidades.id=inv_prod.unidad_id where "
                + "pro_estruc_det.pro_estruc_id="+formula_id+" order by pro_estruc_det.elemento";
         
         * 
        String sql_query = "select pro_estruc_det.inv_prod_id,"
                + "(CASE WHEN inv_prod_tipos.id=1 THEN 'PT' WHEN  inv_prod_tipos.id=2 THEN 'PI' WHEN  inv_prod_tipos.id=7  THEN 'MP' ELSE inv_prod.descripcion END) as tipo, "
                + "inv_prod.sku, inv_prod.descripcion, pro_estruc_det.cantidad, pro_estruc_det.elemento, "
                + "inv_prod_tipos.id as id_tipo, inv_prod_unidades.titulo as unidad, pro_estruc_det.pro_estruc_id, "
                + "pro_estruc.id as estruct_id from pro_estruc_det join inv_prod on "
                + "inv_prod.id=pro_estruc_det.inv_prod_id "
                + "join inv_prod_tipos on inv_prod_tipos.id=inv_prod.tipo_de_producto_id "
                + "join inv_prod_unidades on "
                + "inv_prod_unidades.id=inv_prod.unidad_id "
                + "left join pro_estruc on pro_estruc.inv_prod_id=pro_estruc_det.inv_prod_id "
                + "where pro_estruc_det.pro_estruc_id="+formula_id+" order by pro_estruc_det.elemento";
        */
        
        String sql_query = ""
                + "SELECT "
                        + "inv_prod_id,"
                        + "tipo, "
                        + "sku, "
                        + "descripcion, "
                        + "cantidad, "
                        + "elemento, "
                        + "id_tipo, "
                        + "unidad, "
                        + "pro_estruc_id, "
                        + "estruct_id,"
                        + "cant_unidad,"
                        //+ "costo_unitario,"
                        + "(CASE  WHEN upper(unidad) ILIKE '%KILO%' THEN "
                            + "costo_unitario "
                        + "ELSE "
                            + "(CASE WHEN densidad=0 THEN costo_unitario ELSE (costo_unitario / densidad::double precision) END) "
                        + "END) AS costo_unitario,"
                        + "(cant_unidad::double precision * costo_unitario::double precision) AS costo_importe "
                + "FROM( "
                    + "select "
                        + "pro_estruc_det.inv_prod_id,"
                        + "(CASE WHEN inv_prod_tipos.id=1 THEN 'PT' WHEN  inv_prod_tipos.id=2 THEN 'PI' WHEN  inv_prod_tipos.id=7  THEN 'MP' ELSE inv_prod.descripcion END) as tipo, "
                        + "inv_prod.sku, "
                        + "inv_prod.descripcion, "
                        + "pro_estruc_det.cantidad, "
                        + "pro_estruc_det.elemento, "
                        + "inv_prod_tipos.id as id_tipo, "
                        + "inv_prod_unidades.titulo as unidad, "
                        + "inv_prod.densidad, "
                        + "pro_estruc_det.pro_estruc_id, "
                        + "pro_estruc.id as estruct_id,"
                        + "(CASE  WHEN upper(inv_prod_unidades.titulo) ILIKE '%KILO%' THEN pro_estruc_det.cantidad ELSE (CASE WHEN inv_prod.densidad=0 THEN pro_estruc_det.cantidad ELSE (pro_estruc_det.cantidad::double precision / inv_prod.densidad::double precision) END) END) AS cant_unidad,"
                        + "(CASE WHEN inv_costo.gral_mon_id_"+mes+" IS NULL THEN (CASE WHEN inv_costo.costo_ultimo_"+mes+" IS NULL THEN 0 ELSE inv_costo.costo_ultimo_"+mes+" END) ELSE (CASE WHEN inv_costo.gral_mon_id_"+mes+"<>1 THEN (CASE WHEN inv_costo.costo_ultimo_"+mes+" IS NULL THEN 0 ELSE (inv_costo.costo_ultimo_"+mes+"::double precision * "+ tipo_cambio +"::double precision) END) ELSE (CASE WHEN inv_costo.costo_ultimo_"+mes+" IS NULL THEN 0 ELSE inv_costo.costo_ultimo_"+mes+" END) END) END) AS costo_unitario "
                    + "from pro_estruc_det "
                    + "join inv_prod on inv_prod.id=pro_estruc_det.inv_prod_id "
                    + "join inv_prod_tipos on inv_prod_tipos.id=inv_prod.tipo_de_producto_id  "
                    + "join inv_prod_unidades on inv_prod_unidades.id=inv_prod.unidad_id "
                    + "left join pro_estruc on pro_estruc.inv_prod_id=pro_estruc_det.inv_prod_id "
                    + "LEFT JOIN inv_prod_cost_prom AS inv_costo ON (inv_costo.inv_prod_id=inv_prod.id AND inv_costo.ano="+ano+") "
                    + "where pro_estruc_det.pro_estruc_id="+formula_id+" "
                + ") AS sbt order by elemento;";
        
        System.out.println("DatosFormula: "+sql_query);
        ArrayList<HashMap<String, Object>> hm_grid = (ArrayList<HashMap<String, Object>>) this.jdbcTemplate.query(
            sql_query,
            new Object[]{}, new RowMapper() {
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    HashMap<String, Object> row = new HashMap<String, Object>();
                    row.put("producto_elemento_id",String.valueOf(rs.getInt("inv_prod_id")));
                    row.put("descripcion",rs.getString("descripcion"));
                    row.put("sku",rs.getString("sku"));
                    row.put("cantidad",StringHelper.roundDouble(rs.getString("cantidad"),2));
                    row.put("tipo",rs.getString("tipo"));
                    row.put("id_tipo",rs.getInt("id_tipo"));
                    row.put("unidad","  "+rs.getString("unidad"));
                    if(String.valueOf(rs.getInt("id_tipo")).equals("2") || String.valueOf(rs.getInt("id_tipo")).equals("1")){
                        row.put("adicionales",getInv_ListaProductosFormulaIntermedioPdf(String.valueOf(rs.getInt("estruct_id")), tipo_cambio, ano, mes, StringHelper.roundDouble(rs.getString("cantidad"),4)));
                    }else{
                        row.put("adicionales",rs.getInt("id_tipo"));
                    }
                    
                    row.put("cant_unidad",StringHelper.roundDouble(rs.getString("cant_unidad"),4));
                    row.put("costo_unitario",StringHelper.roundDouble(rs.getString("costo_unitario"),2));
                    row.put("costo_importe",StringHelper.roundDouble(rs.getString("costo_importe"),2));
                    
                    return row;
                }
            }
        );
        return hm_grid;
    }
    
    //metodo para obtener los componentes de la formula de tipo intermedio
    private ArrayList<HashMap<String, String>> getInv_ListaProductosFormulaIntermedioPdf(String formula_id, String tipo_cambio, String ano, String mes, String cantFormula) {

        /*
        String sql_query = ""
                + "select "
                + "inv_formulas.producto_elemento_id,"
                + "(CASE WHEN inv_prod_tipos.id=1 THEN 'PT' WHEN  inv_prod_tipos.id=2 THEN 'PI' WHEN  inv_prod_tipos.id=7  THEN 'MP' ELSE inv_prod.descripcion END) as tipo, "
                + "inv_prod.sku, "
                + "inv_prod.descripcion, "
                + "inv_formulas.cantidad, "
                + "inv_formulas.nivel, "
                //+ "inv_prod_tipos.titulo as tipo, "
                + "inv_prod_tipos.id as id_tipo, "
                + "inv_prod_unidades.titulo as unidad "
                + "from inv_formulas "
                + "join inv_prod on inv_prod.id=inv_formulas.producto_elemento_id "
                + "join inv_prod_tipos on inv_prod_tipos.id=inv_prod.tipo_de_producto_id "
                + "join inv_prod_unidades on inv_prod_unidades.id=inv_prod.unidad_id "
                + "where inv_prod_id_master="+formula_id+" order by inv_formulas.id desc";
         * 
         * 
        String sql_query = "select pro_estruc_det.inv_prod_id,(CASE WHEN inv_prod_tipos.id=1 THEN 'PT' WHEN  inv_prod_tipos.id=2 THEN 'PI' "
                + "WHEN  inv_prod_tipos.id=7  THEN 'MP' ELSE inv_prod.descripcion END) as tipo, inv_prod.sku, inv_prod.descripcion, "
                + "pro_estruc_det.cantidad, pro_estruc_det.elemento, inv_prod_tipos.id as id_tipo, inv_prod_unidades.titulo as unidad, pro_estruc_det.pro_estruc_id "
                + "from pro_estruc_det join inv_prod on inv_prod.id=pro_estruc_det.inv_prod_id join inv_prod_tipos on "
                + "inv_prod_tipos.id=inv_prod.tipo_de_producto_id join inv_prod_unidades on inv_prod_unidades.id=inv_prod.unidad_id where "
                + "pro_estruc_det.pro_estruc_id="+formula_id+" order by pro_estruc_det.elemento";

        */
        

        String sql_query=""
                + "SELECT "
                    + "inv_prod_id, "
                    + "tipo,"
                    + "sku,"
                    + "descripcion,"
                    //+ "cantidad,"
                    + "((cantidad::double precision / 100) * "+cantFormula+"::double precision) AS cantidad,"
                    + "elemento,"
                    + "id_tipo,"
                    + "unidad,"
                    + "pro_estruc_id,"
                    + "((cant_unidad::double precision / 100) * "+cantFormula+"::double precision) AS cant_unidad,"
                    + "(CASE  WHEN upper(unidad) ILIKE '%KILO%' THEN "
                        + "costo_unitario "
                    + "ELSE "
                        + "(CASE WHEN densidad=0 THEN costo_unitario ELSE (costo_unitario / densidad::double precision) END) "
                    + "END) AS costo_unitario,"
                    + "(((cant_unidad::double precision / 100) * "+cantFormula+"::double precision) * costo_unitario::double precision) AS costo_importe "
                    + "FROM( "
                        + "select  "
                            + "pro_estruc_det.inv_prod_id,"
                            + "(CASE WHEN inv_prod_tipos.id=1 THEN 'PT' WHEN  inv_prod_tipos.id=2 THEN 'PI' WHEN  inv_prod_tipos.id=7  THEN 'MP' ELSE inv_prod.descripcion END) as tipo, "
                            + "inv_prod.sku, "
                            + "inv_prod.descripcion, "
                            + "pro_estruc_det.cantidad, "
                            + "pro_estruc_det.elemento, "
                            + "inv_prod_tipos.id as id_tipo, "
                            + "inv_prod_unidades.titulo AS unidad, "
                            + "inv_prod.densidad, "
                            + "pro_estruc_det.pro_estruc_id,"
                            + "(CASE  WHEN upper(inv_prod_unidades.titulo) ILIKE '%KILO%' THEN pro_estruc_det.cantidad ELSE (CASE WHEN inv_prod.densidad=0 THEN  pro_estruc_det.cantidad ELSE (pro_estruc_det.cantidad::double precision / inv_prod.densidad::double precision) END) END) AS cant_unidad, "
                            + "(CASE WHEN inv_costo.gral_mon_id_"+mes+" IS NULL THEN (CASE WHEN inv_costo.costo_ultimo_"+mes+" IS NULL THEN 0 ELSE inv_costo.costo_ultimo_"+mes+" END) ELSE (CASE WHEN inv_costo.gral_mon_id_"+mes+"<>1 THEN (CASE WHEN inv_costo.costo_ultimo_"+mes+" IS NULL THEN 0 ELSE (inv_costo.costo_ultimo_"+mes+"::double precision * "+ tipo_cambio +"::double precision) END) ELSE (CASE WHEN inv_costo.costo_ultimo_"+mes+" IS NULL THEN 0 ELSE inv_costo.costo_ultimo_"+mes+" END) END) END) AS costo_unitario "
                        + "from pro_estruc_det "
                        + "join inv_prod on inv_prod.id=pro_estruc_det.inv_prod_id "
                        + "join inv_prod_tipos on inv_prod_tipos.id=inv_prod.tipo_de_producto_id "
                        + "join inv_prod_unidades on inv_prod_unidades.id=inv_prod.unidad_id  "
                        + "LEFT JOIN inv_prod_cost_prom AS inv_costo ON (inv_costo.inv_prod_id=inv_prod.id AND inv_costo.ano="+ano+") "
                        + "where pro_estruc_det.pro_estruc_id="+formula_id+" "
                + ") AS sbt order by elemento;";
        
        
        //System.out.println("DatosIntermedio: "+sql_query);
        ArrayList<HashMap<String, String>> hm_grid = (ArrayList<HashMap<String, String>>) this.jdbcTemplate.query(
            sql_query,
            new Object[]{}, new RowMapper() {
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    HashMap<String, String> row = new HashMap<String, String>();
                    /*
                    row.put("producto_elemento_id",String.valueOf(rs.getInt("inv_prod_id")));
                    row.put("descripcion",rs.getString("descripcion"));
                    row.put("sku",rs.getString("sku"));
                    row.put("cantidad",StringHelper.roundDouble(rs.getString("cantidad"),2));
                    row.put("tipo",rs.getString("tipo"));
                    row.put("id_tipo",rs.getInt("id_tipo"));
                    row.put("unidad","  "+rs.getString("unidad"));
                    if(String.valueOf(rs.getInt("id_tipo")).equals("2") || String.valueOf(rs.getInt("id_tipo")).equals("1")){
                        row.put("adicionales",getInv_ListaProductosFormulaIntermedioPdf(String.valueOf(rs.getInt("pro_estruc_id"))));
                    }else{
                        row.put("adicionales",rs.getInt("id_tipo"));
                    }
                    */
                    row.put("producto_elemento_id",String.valueOf(rs.getInt("inv_prod_id")));
                    row.put("descripcion","  "+rs.getString("descripcion"));
                    row.put("sku","  "+rs.getString("sku"));
                    row.put("unidad","  "+rs.getString("unidad"));
                    row.put("cantidad",StringHelper.roundDouble(rs.getString("cantidad"),4));
                    row.put("tipo","  "+rs.getString("tipo"));
                    row.put("id_tipo",rs.getString(rs.getInt("id_tipo")));
                    row.put("adicionales",rs.getString(rs.getInt("id_tipo")));
                    
                    row.put("cant_unidad",StringHelper.roundDouble(rs.getString("cant_unidad"),4));
                    row.put("costo_unitario",StringHelper.roundDouble(rs.getString("costo_unitario"),2));
                    row.put("costo_importe",StringHelper.roundDouble(rs.getString("costo_importe"),2));
                    return row;
                }
            }
        );
        return hm_grid;
    }

    //metoodo para pdf de formulas
    @Override
    public HashMap<String, String> getPro_DatosFormulaPdf(Integer formula_id) {

        HashMap<String, String> datos = new HashMap<String, String>();

        String sql_query = "select inv_prod.descripcion, pro_estruc.inv_prod_id, inv_prod.sku from pro_estruc join inv_prod on "
                + "inv_prod.id=pro_estruc.inv_prod_id where pro_estruc.id="+formula_id+" ";

        //System.out.println("DATOS PARA EL PDF:"+sql_query);
        Map<String, Object> map = this.getJdbcTemplate().queryForMap(sql_query);

        datos.put("folio", map.get("inv_prod_id").toString());
        datos.put("fecha", "");
        datos.put("nombre_mes", "" );
        datos.put("descripcion", map.get("descripcion").toString() );
        datos.put("sku", map.get("sku").toString() );
        datos.put("clave_vehiculo", "" );
        datos.put("hora_salida", "" );
        datos.put("hora_llegada", "" );

        return datos;
    }


    //metoodo para especificaciones pdf
    @Override
    public ArrayList<HashMap<String, String>> getPro_DatosFormulaEspecificacionesPdf(Integer formula_id) {

        HashMap<String, String> datos = new HashMap<String, String>();
        ArrayList<HashMap<String, String>> hm_grid = new ArrayList<HashMap<String, String>>();

        String sql_busqueda = ""
                + "select count(pro_proc_esp.id) as cantidad "
                + "from pro_subproceso_prod "
                + "join pro_proc_esp on pro_proc_esp.pro_subproceso_prod_id=pro_subproceso_prod.id "
                + "where pro_subproceso_prod.pro_estruc_id="+formula_id+" limit 1";
        
        //System.out.println("buscarEspecificaciones: "+sql_busqueda);
        
        //esto es para revisar que exista el registro
        int rowCount = this.getJdbcTemplate().queryForInt(sql_busqueda);
        
        //System.out.println("DATOS PARA EL PDF:"+sql_busqueda);
        
        //si rowCount es mayor que cero si se encontro registro y extraemos el valor
        if (rowCount > 0){
           /*
            String sql_query = "select pro_proc_esp.id, pro_proc_esp.fineza_inicial, pro_proc_esp.viscosidads_inicial, pro_proc_esp.viscosidadku_inicial,"
                    + "pro_proc_esp.viscosidadcps_inicial, pro_proc_esp.densidad_inicial, pro_proc_esp.volatiles_inicial, pro_proc_esp.hidrogeno_inicial, "
                    + "pro_proc_esp.cubriente_inicial, pro_proc_esp.tono_inicial, pro_proc_esp.brillo_inicial, pro_proc_esp.dureza_inicial, "
                    + "pro_proc_esp.adherencia_inicial, pro_proc_esp.fineza_final, pro_proc_esp.viscosidads_final, pro_proc_esp.viscosidadku_final, "
                    + "pro_proc_esp.viscosidadcps_final, pro_proc_esp.densidad_final, pro_proc_esp.volatiles_final, pro_proc_esp.hidrogeno_final,"
                    + "pro_proc_esp.cubriente_final, pro_proc_esp.tono_final, pro_proc_esp.brillo_final, pro_proc_esp.dureza_final, pro_proc_esp.adherencia_final,"
                    + "pro_subprocesos.titulo  "
                    + "from pro_subproceso_prod join pro_proc_esp on pro_proc_esp.pro_subproceso_prod_id=pro_subproceso_prod.id "
                    + "join pro_subprocesos on pro_subproceso_prod.pro_subprocesos_id=pro_subprocesos.id where "
                            + "pro_subproceso_prod.inv_prod_id="+formula_id+" order by pro_subprocesos.id";
              */

            String sql_query = "select pro_proc_esp.id, pro_proc_esp.fineza_inicial, pro_proc_esp.viscosidads_inicial, "
                    + "pro_proc_esp.viscosidadku_inicial,pro_proc_esp.viscosidadcps_inicial, pro_proc_esp.densidad_inicial, "
                    + "pro_proc_esp.volatiles_inicial, pro_proc_esp.hidrogeno_inicial, pro_proc_esp.cubriente_inicial, "
                    + "pro_proc_esp.tono_inicial, pro_proc_esp.brillo_inicial, pro_proc_esp.dureza_inicial, pro_proc_esp.adherencia_inicial, "
                    + "pro_proc_esp.fineza_final, pro_proc_esp.viscosidads_final, pro_proc_esp.viscosidadku_final, pro_proc_esp.viscosidadcps_final, "
                    + "pro_proc_esp.densidad_final, pro_proc_esp.volatiles_final, pro_proc_esp.hidrogeno_final,pro_proc_esp.cubriente_final, "
                    + "pro_proc_esp.tono_final, pro_proc_esp.brillo_final, pro_proc_esp.dureza_final, pro_proc_esp.adherencia_final,"
                    + "pro_subprocesos.titulo from pro_subproceso_prod join pro_proc_esp on "
                    + "pro_proc_esp.pro_subproceso_prod_id=pro_subproceso_prod.id join pro_subprocesos on "
                    + "pro_subproceso_prod.pro_subprocesos_id=pro_subprocesos.id where pro_subproceso_prod.pro_estruc_id="+formula_id+" order by "
                    + "pro_proc_esp.id, pro_subprocesos.id";

            //System.out.println("getEspecificaciones: "+sql_query);
            hm_grid = (ArrayList<HashMap<String, String>>) this.jdbcTemplate.query(
                sql_query,
                new Object[]{}, new RowMapper() {
                    @Override
                    public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                        HashMap<String, String> row = new HashMap<String, String>();
                        /*
                        row.put("id",String.valueOf(rs.getInt("id")));
                        row.put("posicion",String.valueOf(rs.getInt("posicion")));
                        row.put("descripcion","  "+rs.getString("descripcion"));
                        */

                        row.put("id", String.valueOf(rs.getInt("id")));
                        row.put("fineza_inicial", String.valueOf(rs.getDouble("fineza_inicial")));
                        row.put("viscosidads_inicial", String.valueOf(rs.getDouble("viscosidads_inicial")));
                        row.put("viscosidadku_inicial", String.valueOf(rs.getDouble("viscosidadku_inicial")));
                        row.put("viscosidadcps_inicial", String.valueOf(rs.getDouble("viscosidadcps_inicial")));
                        row.put("densidad_inicial", String.valueOf(rs.getDouble("densidad_inicial")));
                        row.put("volatiles_inicial", String.valueOf(rs.getDouble("volatiles_inicial")));
                        row.put("hidrogeno_inicial", String.valueOf(rs.getDouble("hidrogeno_inicial")));
                        row.put("cubriente_inicial", String.valueOf(rs.getDouble("cubriente_inicial")));
                        row.put("tono_inicial", String.valueOf(rs.getDouble("tono_inicial")));
                        row.put("brillo_inicial", String.valueOf(rs.getDouble("brillo_inicial")));
                        row.put("dureza_inicial", rs.getString("dureza_inicial"));
                        row.put("adherencia_inicial", String.valueOf(rs.getDouble("adherencia_inicial")));
                        
                        row.put("fineza_final", String.valueOf(rs.getDouble("fineza_final")));
                        row.put("viscosidads_final", String.valueOf(rs.getDouble("viscosidads_final")));
                        row.put("viscosidadku_final", String.valueOf(rs.getDouble("viscosidadku_final")));
                        row.put("viscosidadcps_final", String.valueOf(rs.getDouble("viscosidadcps_final")));
                        row.put("densidad_final", String.valueOf(rs.getDouble("densidad_final")));
                        row.put("volatiles_final", String.valueOf(rs.getDouble("volatiles_final")));
                        row.put("hidrogeno_final", String.valueOf(rs.getDouble("hidrogeno_final")));
                        row.put("cubriente_final", String.valueOf(rs.getDouble("cubriente_final")));
                        row.put("tono_final", String.valueOf(rs.getDouble("tono_final")));
                        row.put("brillo_final", String.valueOf(rs.getDouble("brillo_final")));
                        row.put("dureza_final", rs.getString("dureza_final"));
                        row.put("adherencia_final", String.valueOf(rs.getDouble("adherencia_final")));

                        row.put("titulo", rs.getString("titulo"));

                        return row;
                    }
                }
            );


        }else{
            HashMap<String, String> row = new HashMap<String, String>();
            row.put("id","0");
            hm_grid.add(datos);
        }

        return hm_grid;
    }



    //metodo para obtener los procedidmientos de la formula de tipo intermedio
    public ArrayList<HashMap<String, String>> getPro_DatosFormulaProcedidmientoPdf(Integer formula_id) {

        HashMap<String, String> datos = new HashMap<String, String>();
        ArrayList<HashMap<String, String>> hm_grid = new ArrayList<HashMap<String, String>>();

        String sql_busqueda = ""
                + "select count(pro_proc_procedimiento.id) as cantidad from "
                + "pro_subproceso_prod join pro_proc_procedimiento on pro_proc_procedimiento.pro_subproceso_prod_id=pro_subproceso_prod.id "
                + "where pro_subproceso_prod.pro_estruc_id="+formula_id+" limit 1";

        //esto es para revisar que exista el registro
        int rowCount = this.getJdbcTemplate().queryForInt(sql_busqueda);

        //System.out.println("DATOS PARA EL PDF:"+sql_busqueda);

        //si rowCount es mayor que cero si se encontro registro y extraemos el valor
        if (rowCount > 0){

            /*
            String sql_query = "select pro_proc_procedimiento.id, pro_proc_procedimiento.posicion, pro_proc_procedimiento.descripcion, "
                    + "pro_subprocesos.titulo from pro_subproceso_prod join pro_proc_procedimiento on "
                    + "pro_proc_procedimiento.pro_subproceso_prod_id=pro_subproceso_prod.id join pro_subprocesos on "
                    + "pro_subproceso_prod.pro_subprocesos_id=pro_subprocesos.id where pro_subproceso_prod.pro_estruc_id="+formula_id+" "
                    + "order by pro_subprocesos.id,pro_proc_procedimiento.posicion";
            */
            String sql_query = ""
            + "select "
                + "pro_proc_procedimiento.id,"
                + "pro_subproceso_prod.id as pro_subp_prod_id, "
                + "pro_proc_procedimiento.posicion, "
                + "pro_proc_procedimiento.descripcion, "
                + "pro_subprocesos.titulo "
            + "from pro_subproceso_prod join pro_proc_procedimiento on pro_proc_procedimiento.pro_subproceso_prod_id=pro_subproceso_prod.id "
            + "join pro_subprocesos on pro_subproceso_prod.pro_subprocesos_id=pro_subprocesos.id "
            + "where pro_subproceso_prod.pro_estruc_id="+formula_id+" "
            + "order by pro_subproceso_prod.id,pro_subprocesos.id,pro_proc_procedimiento.posicion";

            //System.out.println("Obtiene datos pdf ruta: "+sql_query);
            hm_grid = (ArrayList<HashMap<String, String>>) this.jdbcTemplate.query(
                sql_query,
                new Object[]{}, new RowMapper() {
                    @Override
                    public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                        HashMap<String, String> row = new HashMap<String, String>();
                        row.put("id",String.valueOf(rs.getInt("id")));
                        row.put("pro_subp_prod_id",String.valueOf(rs.getInt("pro_subp_prod_id")));
                        row.put("posicion",String.valueOf(rs.getInt("posicion")));
                        row.put("descripcion","  "+rs.getString("descripcion"));
                        row.put("titulo","  "+rs.getString("titulo"));
                        return row;
                    }
                }
            );
        }else{
            HashMap<String, String> row = new HashMap<String, String>();
            row.put("id","0");
            hm_grid.add(datos);
        }

        return hm_grid;
    }



    @Override
    public ArrayList<HashMap<String, String>> getProSubprocesoProd(Integer id) {

        String sql_to_query = "select spprod.id, spprod.pro_subprocesos_id, spprod.inv_prod_id, spprod.nivel, spprod.pro_tipo_equipo_id, "
                + "spprod.documento_calidad, spprod.pro_procesos_id, spprod.pro_estruc_id from pro_subproceso_prod as spprod where pro_procesos_id="+id+" order "
                + "by spprod.nivel;";

        //log.log(Level.INFO, "Ejecutando query de {0}", sql_to_query);
        ArrayList<HashMap<String, String>> hm_monedas = (ArrayList<HashMap<String, String>>) this.jdbcTemplate.query(
            sql_to_query,
            new Object[]{}, new RowMapper(){
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    HashMap<String, String> row = new HashMap<String, String>();
                    row.put("id",String.valueOf(rs.getInt("id")));
                    row.put("pro_subprocesos_id",String.valueOf(rs.getInt("pro_subprocesos_id")));
                    row.put("inv_prod_id",String.valueOf(rs.getInt("inv_prod_id")));
                    row.put("nivel",String.valueOf(rs.getInt("nivel")));
                    row.put("pro_tipo_equipo_id",String.valueOf(rs.getInt("pro_tipo_equipo_id")));
                    row.put("documento_calidad",rs.getString("documento_calidad"));
                    row.put("pro_procesos_id",String.valueOf(rs.getInt("pro_procesos_id")));
                    row.put("pro_estruc_id",String.valueOf(rs.getInt("pro_estruc_id")));
                    return row;
                }
            }
        );
        return hm_monedas;

    }

    @Override
    public ArrayList<HashMap<String, String>> getAllProSubprocesoProcedimiento(Integer id) {

        String sql_to_query = "select id, posicion, descripcion,inv_prod_id, pro_subproceso_prod_id  from pro_proc_procedimiento where pro_proceso_id="+id;

        //log.log(Level.INFO, "Ejecutando query de {0}", sql_to_query);
        ArrayList<HashMap<String, String>> hm_monedas = (ArrayList<HashMap<String, String>>) this.jdbcTemplate.query(
            sql_to_query,
            new Object[]{}, new RowMapper(){
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    HashMap<String, String> row = new HashMap<String, String>();
                    row.put("id",String.valueOf(rs.getInt("id")));
                    row.put("posicion",String.valueOf(rs.getInt("posicion")));
                    row.put("descripcion",StringHelper.replaceStringMacro("macrocoma", ",", rs.getString("descripcion")));
                    row.put("inv_prod_id",String.valueOf(rs.getInt("inv_prod_id")));
                    row.put("pro_subproceso_prod_id",String.valueOf(rs.getInt("pro_subproceso_prod_id")));

                    return row;
                }
            }
        );
        return hm_monedas;

    }

    @Override
    public ArrayList<HashMap<String, String>> getAllProSubprocesoEspecificaciones(Integer id) {

        String sql_to_query = "select id,fineza_inicial,viscosidads_inicial,viscosidadku_inicial,viscosidadcps_inicial,densidad_inicial,volatiles_inicial,"
                + "hidrogeno_inicial,cubriente_inicial,tono_inicial,brillo_inicial,dureza_inicial,adherencia_inicial,"
                +" fineza_final,viscosidads_final,viscosidadku_final,viscosidadcps_final,densidad_final,volatiles_final,"
                + "hidrogeno_final,cubriente_final,tono_final,brillo_final,dureza_final,adherencia_final, "
                + "pro_subproceso_prod_id,  pro_instrumentos_fineza, pro_instrumentos_viscosidad1, pro_instrumentos_viscosidad2,"
                + "pro_instrumentos_viscosidad3, pro_instrumentos_densidad, pro_instrumentos_volatil, pro_instrumentos_cubriente,"
                + "pro_instrumentos_tono, pro_instrumentos_brillo, pro_instrumentos_dureza, pro_instrumentos_adherencia, "
                + "pro_instrumentos_hidrogeno from pro_proc_esp where pro_proceso_id="+id;
        
        System.out.println("getSubprocesoEspecificaciones: "+sql_to_query);
        
        //log.log(Level.INFO, "Ejecutando query de {0}", sql_to_query);
        ArrayList<HashMap<String, String>> hm_monedas = (ArrayList<HashMap<String, String>>) this.jdbcTemplate.query(
            sql_to_query,
            new Object[]{}, new RowMapper(){
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    HashMap<String, String> row = new HashMap<String, String>();
                    row.put("id",String.valueOf(rs.getInt("id")));
                    row.put("fineza_inicial",String.valueOf(rs.getDouble("fineza_inicial")));
                    row.put("viscosidads_inicial",String.valueOf(rs.getDouble("viscosidads_inicial")));
                    row.put("viscosidadku_inicial",String.valueOf(rs.getDouble("viscosidadku_inicial")));
                    row.put("viscosidadcps_inicial",String.valueOf(rs.getDouble("viscosidadcps_inicial")));
                    row.put("densidad_inicial",String.valueOf(rs.getDouble("densidad_inicial")));
                    row.put("volatiles_inicial",String.valueOf(rs.getDouble("volatiles_inicial")));
                    row.put("hidrogeno_inicial",String.valueOf(rs.getDouble("hidrogeno_inicial")));
                    row.put("cubriente_inicial",String.valueOf(rs.getDouble("cubriente_inicial")));
                    row.put("tono_inicial",String.valueOf(rs.getDouble("tono_inicial")));
                    row.put("brillo_inicial",String.valueOf(rs.getDouble("brillo_inicial")));
                    row.put("dureza_inicial",rs.getString("dureza_inicial"));
                    row.put("adherencia_inicial",String.valueOf(rs.getDouble("adherencia_inicial")));

                    row.put("fineza_final",String.valueOf(rs.getDouble("fineza_final")));
                    row.put("viscosidads_final",String.valueOf(rs.getDouble("viscosidads_final")));
                    row.put("viscosidadku_final",String.valueOf(rs.getDouble("viscosidadku_final")));
                    row.put("viscosidadcps_final",String.valueOf(rs.getDouble("viscosidadcps_final")));
                    row.put("densidad_final",String.valueOf(rs.getDouble("densidad_final")));
                    row.put("volatiles_final",String.valueOf(rs.getDouble("volatiles_final")));
                    row.put("hidrogeno_final",String.valueOf(rs.getDouble("hidrogeno_final")));
                    row.put("cubriente_final",String.valueOf(rs.getDouble("cubriente_final")));
                    row.put("tono_final",String.valueOf(rs.getDouble("tono_final")));
                    row.put("brillo_final",String.valueOf(rs.getDouble("brillo_final")));
                    row.put("dureza_final",rs.getString("dureza_final"));
                    row.put("adherencia_final",String.valueOf(rs.getDouble("adherencia_final")));
                    row.put("pro_subproceso_prod_id",String.valueOf(rs.getInt("pro_subproceso_prod_id")));

                    //put para los instrumentos
                    row.put("pro_instrumentos_fineza",String.valueOf(rs.getInt("pro_instrumentos_fineza")));
                    row.put("pro_instrumentos_viscosidad1",String.valueOf(rs.getInt("pro_instrumentos_viscosidad1")));
                    row.put("pro_instrumentos_viscosidad2",String.valueOf(rs.getInt("pro_instrumentos_viscosidad2")));
                    row.put("pro_instrumentos_viscosidad3",String.valueOf(rs.getInt("pro_instrumentos_viscosidad3")));
                    row.put("pro_instrumentos_densidad",String.valueOf(rs.getInt("pro_instrumentos_densidad")));
                    row.put("pro_instrumentos_volatil",String.valueOf(rs.getInt("pro_instrumentos_volatil")));
                    row.put("pro_instrumentos_cubriente",String.valueOf(rs.getInt("pro_instrumentos_cubriente")));
                    row.put("pro_instrumentos_tono",String.valueOf(rs.getInt("pro_instrumentos_tono")));
                    row.put("pro_instrumentos_brillo",String.valueOf(rs.getInt("pro_instrumentos_brillo")));
                    row.put("pro_instrumentos_dureza",String.valueOf(rs.getInt("pro_instrumentos_dureza")));
                    row.put("pro_instrumentos_adherencia",String.valueOf(rs.getInt("pro_instrumentos_adherencia")));
                    row.put("pro_instrumentos_hidrogeno",String.valueOf(rs.getInt("pro_instrumentos_hidrogeno")));


                    return row;
                }
            }
        );
        return hm_monedas;

    }






    //obtiene las maquinas de la empresa indicada
    @Override
    public ArrayList<HashMap<String, String>> getTiposEquipos(Integer id_empresa) {
	String sql_query = "select id ,titulo from pro_tipo_equipo where gral_emp_id="+id_empresa+" and borrado_logico=FALSE;";
        ArrayList<HashMap<String, String>> hm_alm = (ArrayList<HashMap<String, String>>) this.jdbcTemplate.query(
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
        return hm_alm;
    }


    //obtiene los subprocesos de la empresa indicada
    @Override
    public ArrayList<HashMap<String, String>> getSubProcesos(Integer id_empresa) {
	String sql_query = "select id ,titulo from pro_subprocesos where gral_emp_id="+id_empresa+" AND borrado_logico=FALSE;";
        ArrayList<HashMap<String, String>> hm_alm = (ArrayList<HashMap<String, String>>) this.jdbcTemplate.query(
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
        return hm_alm;
    }


    //obtiene los instrumentos de medicion
    @Override
    public ArrayList<HashMap<String, String>> getInstrumentos(Integer id_empresa) {
	String sql_query = "select id ,titulo from pro_instrumentos where gral_emp_id="+id_empresa+" and borrado_logico=FALSE;";
        ArrayList<HashMap<String, String>> hm_alm = (ArrayList<HashMap<String, String>>) this.jdbcTemplate.query(
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
        return hm_alm;
    }

    //busca producto en especifico para agregar a la lista de productos elemento en el catalogo de formulas
    @Override
    public ArrayList<HashMap<String, String>> getProProductoPorSku(String sku, Integer id_empresa) {

        String sql_query = "SELECT inv_prod.id, inv_prod.sku, inv_prod.descripcion,inv_pu.decimales,inv_prod.densidad, "
                + "inv_pu.titulo as unidad, inv_pu.id as unidad_id "
                + "FROM inv_prod JOIN inv_prod_unidades as inv_pu ON inv_pu.id=inv_prod.unidad_id "
                + "WHERE inv_prod.borrado_logico=false AND inv_prod.empresa_id="+id_empresa+" AND inv_prod.sku ilike '"+sku+"';";

        System.out.println("Obteniendo datos sku:"+sql_query);

        ArrayList<HashMap<String, String>> hm_sku = (ArrayList<HashMap<String, String>>) this.jdbcTemplate.query(
            sql_query,
            new Object[]{}, new RowMapper() {
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    HashMap<String, String> row = new HashMap<String, String>();
                    row.put("id",rs.getString("id"));
                    row.put("sku",rs.getString("sku"));
                    row.put("descripcion",rs.getString("descripcion"));
                    row.put("decimales",String.valueOf(rs.getInt("decimales")));
                    row.put("unidad",rs.getString("unidad"));
                    row.put("unidad_id",rs.getString("unidad_id"));
                    row.put("densidad",StringHelper.roundDouble(rs.getString("densidad"),4));
                    return row;
                }
            }
        );
        return hm_sku;
    }


    //busca producto en especifico para agregar a la lista de productos elemento en el catalogo de formulas
    @Override
    public ArrayList<HashMap<String, String>> getProSubprocesosPorProductoSku(String sku, Integer id_empresa, String id_formula,String version) {

        String sql_query = "";

        if(id_formula.equals("0")){
            //Sql para ordendes diferentes de productos en desarrollo
            sql_query = "select prod_tmp.id as inv_prod_id, pro_subproceso_prod.id as pro_subproceso_prod_id,pro_subprocesos.id as "
            + "pro_subprocesos_id, pro_subprocesos.titulo as pro_subprocesos_titulo, pro_subproceso_prod.pro_estruc_id from (select id from inv_prod where sku "
            + "ilike '"+sku+"' and empresa_id="+id_empresa+" and borrado_logico=false limit 1 ) "
            + "as prod_tmp join pro_procesos on pro_procesos.inv_prod_id=prod_tmp.id join pro_subproceso_prod "
            + "on pro_subproceso_prod.pro_procesos_id=pro_procesos.id "
            + "join pro_subprocesos on pro_subproceso_prod.pro_subprocesos_id=pro_subprocesos.id "
            + "where pro_procesos.gral_emp_id="+id_empresa+" and pro_procesos.borrado_logico=false;";
        }else{
            //Sql para ordendes de productos en desarrollo
            sql_query = "select prod_tmp.id as inv_prod_id, sumbprod.id as pro_subproceso_prod_id,pro_subprocesos.id as pro_subprocesos_id, "
                    + "pro_subprocesos.titulo as pro_subprocesos_titulo, sumbprod.pro_estruc_id from( "
                    + "select id from inv_prod where sku ilike '"+sku+"' and empresa_id="+id_empresa+" and borrado_logico=false limit 1 "
                    + ") as prod_tmp join "
                    + "(select * from pro_subproceso_prod where pro_estruc_id="+id_formula+") as sumbprod "
                    + "on sumbprod.inv_prod_id=prod_tmp.id "
                    + "join pro_subprocesos on pro_subprocesos.id=sumbprod.pro_subprocesos_id";
        }

        System.out.println("Obteniendo datos sku:"+sql_query);

        ArrayList<HashMap<String, String>> hm_sku = (ArrayList<HashMap<String, String>>) this.jdbcTemplate.query(
            sql_query,
            new Object[]{}, new RowMapper() {
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    HashMap<String, String> row = new HashMap<String, String>();
                    row.put("inv_prod_id",rs.getString("inv_prod_id"));
                    row.put("pro_subproceso_prod_id",rs.getString("pro_subproceso_prod_id"));
                    row.put("pro_subprocesos_id",rs.getString("pro_subprocesos_id"));
                    row.put("pro_subprocesos_titulo",rs.getString("pro_subprocesos_titulo"));
                    row.put("pro_estruc_id",rs.getString("pro_estruc_id"));
                    return row;
                }
            }
        );
        return hm_sku;
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
                                +"inv_prod_tipos.id AS tipo_id,"
                                + "inv_prod_unidades.decimales "
		+"FROM inv_prod "
                + "LEFT JOIN (select id, titulo from inv_prod_tipos where id in (2, 1,8,7 )) as inv_prod_tipos ON inv_prod_tipos.id=inv_prod.tipo_de_producto_id "
                + "LEFT JOIN inv_prod_unidades ON inv_prod_unidades.id=inv_prod.unidad_id "
                + "WHERE inv_prod.empresa_id="+id_empresa+" AND inv_prod.borrado_logico=false "+where+" ORDER BY inv_prod.descripcion limit 50;";

        //System.out.println("Ejecutando query de: "+ sql_to_query);

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
                    row.put("tipo_id",String.valueOf(rs.getInt("tipo_id")));
                    row.put("decimales",String.valueOf(rs.getInt("decimales")));
                    return row;
                }
            }
        );
        return hm_datos_productos;
    }


    @Override
    public ArrayList<HashMap<String, String>> getBuscadorVersionesFormulas(String sku, String descripcion, Integer id_empresa) {

        String where = "";
	if(!sku.equals("")){
		where=" AND inv_prod.sku ilike '%"+sku+"%'";
	}

	if(!descripcion.equals("")){
		where +=" AND inv_prod.descripcion ilike '%"+descripcion+"%'";
	}

        String sql_to_query = "select pro_estruc.id, prod_tmp.id as inv_prod_id, prod_tmp.sku, prod_tmp.descripcion, pro_estruc.version, "
                + "pro_estruc.pro_estruc_id from ( select * from inv_prod where inv_prod.empresa_id="+id_empresa+" AND inv_prod.borrado_logico=false "+where+") as "
                + "prod_tmp join pro_estruc on pro_estruc.inv_prod_id=prod_tmp.id "
                + "where pro_estruc.tipo_formula=2 ";

        //System.out.println("Ejecutando query de: "+ sql_to_query);

        ArrayList<HashMap<String, String>> hm_datos_productos = (ArrayList<HashMap<String, String>>) this.jdbcTemplate.query(
            sql_to_query,
            new Object[]{}, new RowMapper(){
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    HashMap<String, String> row = new HashMap<String, String>();
                    row.put("id",String.valueOf(rs.getInt("id")));
                    row.put("inv_prod_id",String.valueOf(rs.getInt("inv_prod_id")));
                    row.put("sku",rs.getString("sku"));
                    row.put("descripcion",rs.getString("descripcion"));
                    row.put("version",String.valueOf(rs.getInt("version")));
                    return row;
                }
            }
        );
        return hm_datos_productos;
    }

    @Override
    public ArrayList<HashMap<String, String>> getBuscadorEquivalentes(String id_producto, Integer id_empresa,Integer id_user) {

        String sql_to_query = "select inv_prod.id, inv_prod.sku,inv_prod.descripcion,inv_prod.unidad_id,"
                + "inv_prod_unidades.titulo AS unidad,inv_prod_tipos.titulo AS tipo, inv_prod_unidades.decimales,  "
                + "( SELECT inv_calculo_existencia_producto AS existencia FROM inv_calculo_existencia_producto(2,false, "
                + "inv_prod.id, "+id_user+", (select inv_alm_id from pro_par where gral_emp_id="+id_empresa+" limit 1)) ) as existencia "

                + "from (selecT inv_prod_id_equiv from inv_prod_equiv where inv_prod_id="+id_producto+" ) as equiv "
                + "join inv_prod on inv_prod.id=equiv.inv_prod_id_equiv "
                + "join inv_prod_tipos ON inv_prod_tipos.id=inv_prod.tipo_de_producto_id "
                + "LEFT JOIN inv_prod_unidades ON inv_prod_unidades.id=inv_prod.unidad_id";

        //System.out.println("Ejecutando query de: "+ sql_to_query);

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
                    row.put("existencia",String.valueOf(StringHelper.roundDouble(rs.getDouble("existencia"), 4)));
                    return row;
                }
            }
        );
        return hm_datos_productos;
    }

    @Override
    public ArrayList<HashMap<String, String>> getRequisicionOP(String id_orden, Integer id_empresa,Integer id_user) {

        String sql_to_query = "selecT det_mov.id,det_mov.cantidad,det_mov.inv_prod_id, inv_prod.sku, inv_prod.descripcion, "
                + "( SELECT inv_calculo_existencia_producto AS existencia FROM inv_calculo_existencia_producto(2,false, "
                + "det_mov.inv_prod_id, "+id_user+", (select inv_alm_id from pro_par where gral_emp_id="+id_empresa+" limit 1)) ) as existencia,"
                + " det_mov.elemento, det_mov.pro_orden_prod_det_id, det_mov.pro_subprocesos_id  "
                + "from (select id from pro_orden_prod_det where pro_orden_prod_id="+id_orden+") as tmp_det_prod "
                + "join pro_orden_detalle_mov as det_mov on det_mov.pro_orden_prod_det_id=tmp_det_prod.id "
                + "left join inv_prod on inv_prod.id=det_mov.inv_prod_id";

        //System.out.println("Ejecutando query de: "+ sql_to_query);

        ArrayList<HashMap<String, String>> hm_datos_productos = (ArrayList<HashMap<String, String>>) this.jdbcTemplate.query(
            sql_to_query,
            new Object[]{}, new RowMapper(){
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    HashMap<String, String> row = new HashMap<String, String>();
                    row.put("id",String.valueOf(rs.getInt("id")));
                    row.put("sku",rs.getString("sku"));
                    row.put("descripcion",rs.getString("descripcion"));
                    row.put("inv_prod_id",String.valueOf(rs.getInt("inv_prod_id")));
                    row.put("cantidad",String.valueOf(rs.getDouble("cantidad")));
                    row.put("existencia",String.valueOf(StringHelper.roundDouble(String.valueOf(rs.getDouble("existencia")), 2)));
                    row.put("elemento",String.valueOf(rs.getInt("elemento")));
                    row.put("pro_orden_prod_det_id",String.valueOf(rs.getInt("pro_orden_prod_det_id")));
                    row.put("pro_subprocesos_id",String.valueOf(rs.getInt("pro_subprocesos_id")));

                    return row;
                }
            }
        );
        return hm_datos_productos;
    }


    @Override
    public ArrayList<HashMap<String, String>> getBuscadorFormulacionesParaProcedidmientos(String sku,  String descripcion, Integer id_empresa,Integer subproceso) {
        String where = "";
	if(!sku.equals("")){
            where=" AND inv_prod.sku ilike '%"+sku+"%'";
	}

	if(!descripcion.equals("")){
		where +=" AND inv_prod.descripcion ilike '%"+descripcion+"%'";
	}

     /*
        String sql_to_query = "select tmp1.id, inv_formulas.nivel, inv_prod.sku, inv_prod.descripcion, inv_formulas.inv_prod_id_master from "
                + "inv_formulas join inv_prod on inv_prod.id=inv_formulas.inv_prod_id_master join "
                + "(select id, inv_prod_id from pro_subproceso_prod where pro_subprocesos_id="+subproceso+") as tmp1 on "
                + "tmp1.inv_prod_id=inv_formulas.inv_prod_id_master where "
                + " inv_prod.empresa_id="+id_empresa+" "+where+" group by "
                + " inv_formulas.nivel, inv_prod.sku, inv_prod.descripcion, inv_formulas.inv_prod_id_master, tmp1.id order by inv_prod.descripcion";

    */
        String sql_to_query = "select tmp1.id, 1::integer as nivel, inv_prod.sku, inv_prod.descripcion, pro_estruc.inv_prod_id from pro_estruc "
                + "join inv_prod on inv_prod.id=pro_estruc.inv_prod_id join ("
                + "select id, inv_prod_id from pro_subproceso_prod where pro_subprocesos_id="+subproceso+" "
                + ") as tmp1 on tmp1.inv_prod_id=pro_estruc.inv_prod_id "
                + "where  inv_prod.empresa_id="+id_empresa+" "+where+"  group by  "
                        + "inv_prod.sku, inv_prod.descripcion, pro_estruc.inv_prod_id, tmp1.id order by inv_prod.descripcion";
        //subproceso
        //System.out.println("Ejecutando query de: "+ sql_to_query);

        ArrayList<HashMap<String, String>> hm_datos_productos = (ArrayList<HashMap<String, String>>) this.jdbcTemplate.query(
            sql_to_query,
            new Object[]{}, new RowMapper(){
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    HashMap<String, String> row = new HashMap<String, String>();
                    row.put("id",String.valueOf(rs.getInt("id")));
                    row.put("sku",rs.getString("sku"));
                    row.put("descripcion",rs.getString("descripcion"));
                    row.put("nivel",String.valueOf(rs.getInt("nivel")));

                    return row;
                }
            }
        );
        return hm_datos_productos;
    }


    @Override
    public ArrayList<HashMap<String, String>> getProcedimientosPorFormulacion(Integer id_subp_prod, Integer id_empresa) {

        String sql_to_query = "select posicion, descripcion from pro_proc_procedimiento where pro_subproceso_prod_id="+id_subp_prod+" order by posicion";


        //log.log(Level.INFO, "Ejecutando query de {0}", sql_to_query);

        ArrayList<HashMap<String, String>> hm_datos_retorno = (ArrayList<HashMap<String, String>>) this.jdbcTemplate.query(
            sql_to_query,
            new Object[]{}, new RowMapper(){
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    HashMap<String, String> row = new HashMap<String, String>();
                    row.put("posicion",String.valueOf(rs.getInt("posicion")));
                    row.put("descripcion",rs.getString("descripcion"));

                    return row;
                }
            }
        );
        return hm_datos_retorno;
    }

    @Override
    public ArrayList<HashMap<String, String>> getEntradas_PresentacionesProducto(String sku) {
        String sql_to_query = "SELECT "
                                +"inv_prod.id,"
                                +"inv_prod.sku,"
                                +"inv_prod.descripcion as titulo,"
                                +"inv_prod_unidades.titulo as unidad,"
                                +"inv_prod_presentaciones.id as id_presentacion,"
                                +"inv_prod_presentaciones.titulo as presentacion "
                        +"FROM inv_prod "
                        +"LEFT JOIN inv_prod_unidades on inv_prod_unidades.id = inv_prod.unidad_id "
                        +"LEFT JOIN inv_prod_pres_x_prod on inv_prod_pres_x_prod.producto_id = inv_prod.id "
                        +"LEFT JOIN inv_prod_presentaciones on inv_prod_presentaciones.id = inv_prod_pres_x_prod.presentacion_id "
                        +"where inv_prod.sku ILIKE '"+sku+"'";

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
                    return row;
                }
            }
        );
        return hm_presentaciones;
    }

    //obtiene el tipo de cambio de la fecha indicada
    @Override
    public ArrayList<HashMap<String, String>> getEntradas_TipoCambio(String fecha) {
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









    //obtiene  tipos de traspaso
    @Override
    public ArrayList<HashMap<String, String>> getTraspaso_Tipos() {
        String sql_to_query = "SELECT id,titulo FROM erp_tipos_de_traspaso WHERE borrado_logico = false ORDER BY titulo;";
        //log.log(Level.INFO, "Ejecutando query de {0}", sql_to_query);
        ArrayList<HashMap<String, String>> hm_tipos = (ArrayList<HashMap<String, String>>) this.jdbcTemplate.query(
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
        return hm_tipos;
    }




    @Override
    public ArrayList<HashMap<String, Object>> getTraspaso_PaginaGrid(String data_string, int offset, int pageSize, String orderBy, String asc) {
        String sql_busqueda = "SELECT id FROM gral_bus_catalogos(?) AS foo (id integer)";

	String sql_to_query = "SELECT erp_historico_traspasos.id, "
                                +"erp_historico_traspasos.folio, "
                                +"erp_tipos_de_traspaso.titulo as tipo, "
                                +"inv_alm.titulo AS almacen, "
                                +"to_char(erp_historico_traspasos.momento_creacion,'yyyy-mm-dd') as fecha "
                        +"FROM erp_historico_traspasos "
                        +"LEFT JOIN erp_tipos_de_traspaso ON erp_tipos_de_traspaso.id=erp_historico_traspasos.tipo "
                        +"LEFT JOIN inv_alm ON inv_alm.id=erp_historico_traspasos.almacen_id "
                        +"JOIN ("+sql_busqueda+") as subt on subt.id=erp_historico_traspasos.id "
                        +"ORDER BY "+orderBy+" "+asc+" LIMIT ? OFFSET ?";

        //System.out.println("Busqueda GetPage: "+sql_to_query);

        //log.log(Level.INFO, "Ejecutando query de {0}", sql_to_query);
        ArrayList<HashMap<String, Object>> hm = (ArrayList<HashMap<String, Object>>) this.jdbcTemplate.query(
            sql_to_query,
            new Object[]{new String(data_string),new Integer(pageSize),new Integer(offset)}, new RowMapper() {
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    HashMap<String, Object> row = new HashMap<String, Object>();
                    row.put("id",rs.getString("id"));
                    row.put("folio",rs.getString("folio"));
                    row.put("tipo",rs.getString("tipo"));
                    row.put("almacen",rs.getString("almacen"));
                    row.put("fecha",rs.getString("fecha"));
                    return row;
                }
            }
        );
        return hm;
    }




    @Override
    public ArrayList<HashMap<String, String>> getTraspaso_Datos(Integer id_traspaso) {
        String sql_to_query = "SELECT erp_historico_traspasos.id, "
                                + "erp_historico_traspasos.tipo, "
                                + "erp_prealmacen_entradas.no_entrada, "
                                + "erp_historico_traspasos.almacen_id, "
                                + "erp_historico_traspasos.observaciones, "
                                + "erp_prealmacen_entradas.factura, "
                                + "erp_prealmacen_entradas.factura_fecha_expedicion, "
                                + "erp_prealmacen_entradas.orden_de_compra, "
                                + "erp_prealmacen_entradas.numero_guia "
                            + "FROM erp_historico_traspasos "
                            + "LEFT JOIN erp_prealmacen_entradas ON erp_prealmacen_entradas.id = erp_historico_traspasos.prealmacen_entrada_id "
                            + "WHERE erp_historico_traspasos.id="+id_traspaso+";";

        ArrayList<HashMap<String, String>> hm_traspaso = (ArrayList<HashMap<String, String>>) this.jdbcTemplate.query(
            sql_to_query,
            new Object[]{}, new RowMapper(){
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    HashMap<String, String> row = new HashMap<String, String>();
                    row.put("folio",String.valueOf(rs.getInt("id")));
                    row.put("tipo",String.valueOf(rs.getInt("tipo")));
                    row.put("no_entrada",rs.getString("no_entrada"));
                    row.put("almacen_id",String.valueOf(rs.getInt("almacen_id")));
                    row.put("observaciones",rs.getString("observaciones"));
                    row.put("factura",rs.getString("factura"));
                    row.put("factura_fecha_expedicion",rs.getString("factura_fecha_expedicion"));
                    row.put("orden_de_compra",rs.getString("orden_de_compra"));
                    row.put("numero_guia",rs.getString("numero_guia"));
                    return row;
                }
            }
        );
        return hm_traspaso;
    }





    //obtiene datos para el grid de traspasos
    @Override
    public ArrayList<HashMap<String, String>> getTraspaso_DatosGrid(Integer id_traspaso) {
        String sql_to_query = "SELECT inv_prod.sku, "
                                + "(CASE WHEN (inv_prod.titulo_es = '' OR inv_prod.titulo_es IS NULL ) THEN inv_prod.titulo_en ELSE inv_prod.titulo_es END) as titulo, "
                                + "inv_prod_unidades.titulo as unidad, "
                                + "inv_prod_presentaciones.titulo as presentacion, "
                                + "erp_historico_traspasos_detalles.cantidad, "
                                + "erp_historico_traspasos_detalles.almacen_destino_id, "
                                + "erp_historico_traspasos_detalles.ubicacion_anaquel, "
                                + "erp_historico_traspasos_detalles.ubicacion_columna, "
                                + "erp_historico_traspasos_detalles.ubicacion_fila, "
                                + "erp_historico_traspasos_detalles.disponible "
                            + "FROM erp_historico_traspasos_detalles "
                            + "LEFT JOIN inv_prod ON inv_prod.id = erp_historico_traspasos_detalles.producto_id "
                            + "LEFT JOIN inv_prod_unidades ON inv_prod_unidades.id = inv_prod.unidad_id "
                            + "LEFT JOIN inv_prod_presentaciones ON inv_prod_presentaciones.id=erp_historico_traspasos_detalles.presentacion_id "
                            + "WHERE  erp_historico_traspasos_detalles.historico_traspaso_id="+id_traspaso+";";

        ArrayList<HashMap<String, String>> hm_grid = (ArrayList<HashMap<String, String>>) this.jdbcTemplate.query(
            sql_to_query,
            new Object[]{}, new RowMapper(){
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    HashMap<String, String> row = new HashMap<String, String>();
                    row.put("sku",rs.getString("sku"));
                    row.put("titulo",rs.getString("titulo"));
                    row.put("unidad",rs.getString("unidad"));
                    row.put("presentacion",rs.getString("presentacion"));
                    row.put("cantidad",rs.getString("cantidad"));
                    row.put("almacen_destino_id",String.valueOf(rs.getInt("almacen_destino_id")));
                    row.put("ubicacion_anaquel",rs.getString("ubicacion_anaquel"));
                    row.put("ubicacion_columna",rs.getString("ubicacion_columna"));
                    row.put("ubicacion_fila",rs.getString("ubicacion_fila"));
                    row.put("disponible",rs.getString("disponible"));
                    return row;
                }
            }
        );
        return hm_grid;
    }





    //obtiene los almacenes de la empresa indicada
    @Override
    public ArrayList<HashMap<String, String>> getDatos_ReporteExistencias(Integer id_isuario,Integer id_almacen, String codigo_producto, String descripcion,Integer tipo ) {
	String sql_query = "select * from inv_reporte_existencias("+id_isuario+","+id_almacen+",'%"+codigo_producto+"%','%"+descripcion+"%',"+tipo+") as foo("
                                        + "id integer, "
                                        +"almacen character varying, "
					+"familia character varying, "
					+"grupo character varying, "
					+"linea character varying, "
					+"codigo_producto character varying, "
					+"descripcion character varying, "
					+"unidad_medida character varying, "
					+"existencias double precision, "
					+"costo_unitario double precision, "
					+"costo_total double precision "
					+") ORDER BY descripcion ASC;";

        //System.out.println("sql_query: "+ sql_query);

        ArrayList<HashMap<String, String>> hm_exis = (ArrayList<HashMap<String, String>>) this.jdbcTemplate.query(
            sql_query,
            new Object[]{}, new RowMapper() {
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    HashMap<String, String> row = new HashMap<String, String>();
                    row.put("almacen",rs.getString("almacen"));
                    row.put("familia",rs.getString("familia"));
                    row.put("grupo",rs.getString("grupo"));
                    row.put("linea",rs.getString("linea"));
                    row.put("codigo_producto",rs.getString("codigo_producto"));
                    row.put("descripcion",rs.getString("descripcion"));
                    row.put("unidad_medida",rs.getString("unidad_medida"));
                    row.put("existencias",StringHelper.roundDouble(rs.getDouble("existencias"),4));
                    row.put("costo_unitario",StringHelper.roundDouble(rs.getDouble("costo_unitario"),2));
                    row.put("costo_total",StringHelper.roundDouble(rs.getDouble("costo_total"),2));
                    return row;
                }
            }
        );
        return hm_exis;
    }




    //Metodos para catalogo tipod de movimiento
    @Override
    public ArrayList<HashMap<String, Object>> getTipoMovimientosInventaioGrid(String data_string, int offset, int pageSize, String orderBy, String asc) {
        String sql_busqueda = "select id from gral_bus_catalogos(?) as foo (id integer)";

	String sql_to_query = "SELECT inv_mov_tipos.id,inv_mov_tipos.titulo,inv_mov_tipos.descripcion "
                            + "FROM inv_mov_tipos "
                            + "JOIN ("+sql_busqueda+") AS sbt ON sbt.id = inv_mov_tipos.id "
                            +"order by "+orderBy+" "+asc+" limit ? OFFSET ?";
        //System.out.println("getTipoMovimientosInventaioGrid: "+sql_to_query+"    "+data_string);

        ArrayList<HashMap<String, Object>> hm = (ArrayList<HashMap<String, Object>>) this.jdbcTemplate.query(
            sql_to_query,
            new Object[]{new String(data_string), new Integer(pageSize),new Integer(offset)}, new RowMapper() {
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


   //obtiene datos del tipo de movimiento actual
    @Override
    public ArrayList<HashMap<String, String>> getTipoMovInv_Datos(Integer id) {
        String sql_query = "SELECT * FROM inv_mov_tipos WHERE id = ? and borrado_logico=false";
        ArrayList<HashMap<String, String>> hm = (ArrayList<HashMap<String, String>>) this.jdbcTemplate.query(
            sql_query,
            new Object[]{new Integer(id)}, new RowMapper() {
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    HashMap<String, String> row = new HashMap<String, String>();
                    row.put("id",String.valueOf(rs.getInt("id")));
                    row.put("titulo",rs.getString("titulo"));
                    row.put("descripcion",rs.getString("descripcion"));
                    row.put("ajuste",String.valueOf(rs.getBoolean("ajuste")));
                    row.put("grupo",String.valueOf(rs.getInt("grupo")));
                    row.put("afecta_compres",String.valueOf(rs.getBoolean("afecta_compras")));
                    row.put("afecta_ventas",String.valueOf(rs.getBoolean("afecta_ventas")));
                    row.put("considera_consumo",String.valueOf(rs.getBoolean("considera_consumo")));
                    row.put("tipo_costo",String.valueOf(rs.getInt("tipo_costo")));
                    return row;
                }
            }
        );
        return hm;
    }





    //Metodos para catalogo invsecciones
    @Override
    public ArrayList<HashMap<String, Object>> getInvSeccionesGrid(String data_string, int offset, int pageSize, String orderBy, String asc) {
        String sql_busqueda = "select id from gral_bus_catalogos(?) as foo (id integer)";

	String sql_to_query = "SELECT inv_secciones.id,inv_secciones.titulo,inv_secciones.descripcion,inv_secciones.activa "
                            + "FROM inv_secciones "
                            + "JOIN ("+sql_busqueda+") AS sbt ON sbt.id = inv_secciones.id "
                            +"order by "+orderBy+" "+asc+" limit ? OFFSET ?";
        //System.out.println("getInvSeccionesGrid: "+sql_to_query+"    "+data_string);

        ArrayList<HashMap<String, Object>> hm = (ArrayList<HashMap<String, Object>>) this.jdbcTemplate.query(
            sql_to_query,
            new Object[]{new String(data_string), new Integer(pageSize),new Integer(offset)}, new RowMapper() {
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    HashMap<String, Object> row = new HashMap<String, Object>();
                    row.put("id",rs.getInt("id"));
                    row.put("titulo",rs.getString("titulo"));
                    row.put("descripcion",rs.getString("descripcion"));
                    row.put("activa",String.valueOf(rs.getBoolean("activa")) == "true" ? "Activa" : "Inactiva" );
                    return row;
                }
            }
        );
        return hm;
    }

    //obtiene datos del tipo de movimiento actual
    @Override
    public ArrayList<HashMap<String, String>> getInvSecciones_Datos(Integer id) {
        String sql_query = "SELECT id,titulo, descripcion, activa FROM inv_secciones WHERE id = ? and borrado_logico=false";
        ArrayList<HashMap<String, String>> hm = (ArrayList<HashMap<String, String>>) this.jdbcTemplate.query(
            sql_query,
            new Object[]{new Integer(id)}, new RowMapper() {
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    HashMap<String, String> row = new HashMap<String, String>();
                    row.put("id",String.valueOf(rs.getInt("id")));
                    row.put("titulo",rs.getString("titulo"));
                    row.put("descripcion",rs.getString("descripcion"));
                    row.put("activa",String.valueOf(rs.getBoolean("activa")));
                    return row;
                }
            }
        );
        return hm;
    }





    //catalogo de marcas de Productos
    @Override
    public ArrayList<HashMap<String, Object>> getMarcas_PaginaGrid(String data_string, int offset, int pageSize, String orderBy, String asc) {
        String sql_busqueda = "select id from gral_bus_catalogos(?) as foo (id integer)";

	String sql_to_query = "SELECT (CASE WHEN inv_mar.estatus=false THEN 'INACTIVO' ELSE 'ACTIVO' END) AS estatus,inv_mar.id,inv_mar.titulo as descripcion "
                                +"FROM inv_mar "
                                +"JOIN ("+sql_busqueda+") AS sbt ON sbt.id = inv_mar.id "
                                +"WHERE inv_mar.borrado_logico=false  "
                                +"order by "+orderBy+" "+asc+" limit ? OFFSET ?";

        //System.out.println("Busqueda GetPage: "+sql_to_query);
        ArrayList<HashMap<String, Object>> hm = (ArrayList<HashMap<String, Object>>) this.jdbcTemplate.query(
            sql_to_query,
            new Object[]{new String(data_string), new Integer(pageSize),new Integer(offset)}, new RowMapper() {
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    HashMap<String, Object> row = new HashMap<String, Object>();
                    row.put("id",rs.getInt("id"));
                    row.put("descripcion",rs.getString("descripcion"));
                    row.put("estatus",rs.getString("estatus"));

                    return row;
                }
            }
        );
        return hm;
    }


    @Override
    public ArrayList<HashMap<String, String>> getMarcas_Datos(Integer id_marca) {
        String sql_to_query = "SELECT inv_mar.url,inv_mar.id,inv_mar.titulo as descripcion,(CASE  WHEN inv_mar.estatus =false THEN 'Inactivo' ELSE 'Activo' END) AS estatus  FROM inv_mar WHERE id ="+id_marca;

        ArrayList<HashMap<String, String>> datos_marcas = (ArrayList<HashMap<String, String>>) this.jdbcTemplate.query(
            sql_to_query,
            new Object[]{}, new RowMapper(){
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    HashMap<String, String> row = new HashMap<String, String>();
                    row.put("id",String.valueOf(rs.getInt("id")));
                    row.put("descripcion",rs.getString("descripcion"));
                    row.put("estatus",rs.getString("estatus"));
                    row.put("url",rs.getString("url"));

                    return row;
                }
            }
        );
        return datos_marcas;
    }







    //Metodos para catalogo InvProdLineas_Datos
    @Override
    public ArrayList<HashMap<String, Object>> getInvProdLineas_Grid(String data_string, int offset, int pageSize, String orderBy, String asc) {
        String sql_busqueda = "select id from gral_bus_catalogos(?) as foo (id integer)";

	String sql_to_query = "SELECT inv_prod_lineas.id,inv_prod_lineas.titulo,inv_prod_lineas.descripcion,inv_prod_lineas.titulo as accesor_seccion "
                            + "FROM inv_prod_lineas "
                            + "JOIN ("+sql_busqueda+") AS sbt ON sbt.id = inv_prod_lineas.id "
                            +" left join inv_secciones on inv_secciones.id=inv_prod_lineas.inv_seccion_id order by "+orderBy+" "+asc+" limit ? OFFSET ?";
        //System.out.println("getInvSeccionesGrid: "+sql_to_query+"    "+data_string);

        ArrayList<HashMap<String, Object>> hm = (ArrayList<HashMap<String, Object>>) this.jdbcTemplate.query(
            sql_to_query,
            new Object[]{new String(data_string), new Integer(pageSize),new Integer(offset)}, new RowMapper() {
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    HashMap<String, Object> row = new HashMap<String, Object>();
                    row.put("id",rs.getInt("id"));
                    row.put("titulo",rs.getString("titulo"));
                    row.put("descripcion",rs.getString("descripcion"));
                    row.put("accesor_seccion",rs.getString("accesor_seccion"));
                    return row;
                }
            }
        );
        return hm;
    }


    //obtiene datos de InvProdLineas actual
    @Override
    public ArrayList<HashMap<String, String>> getInvProdLineas_Datos(Integer id) {
        String sql_query = "SELECT id, titulo, descripcion,inv_seccion_id FROM inv_prod_lineas WHERE id = ? and borrado_logico=false";
        ArrayList<HashMap<String, String>> hm = (ArrayList<HashMap<String, String>>) this.jdbcTemplate.query(
            sql_query,
            new Object[]{new Integer(id)}, new RowMapper() {
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    HashMap<String, String> row = new HashMap<String, String>();
                    row.put("id",String.valueOf(rs.getInt("id")));
                    row.put("titulo",rs.getString("titulo"));
                    row.put("descripcion",rs.getString("descripcion"));
                    row.put("inv_seccion_id",String.valueOf(rs.getInt("inv_seccion_id")));
                    return row;
                }
            }
        );
        return hm;
    }

    //obtiene datos de las secciones
    //se utiliza en catalogo de lineas y catalogo de productos
    @Override
    public ArrayList<HashMap<String, String>> getInvProdLineas_Secciones( Integer id_empresa) {
        String sql_to_query = "SELECT id,titulo FROM inv_secciones WHERE borrado_logico=false AND gral_emp_id="+id_empresa+" ORDER BY titulo;";
        //log.log(Level.INFO, "Ejecutando query de {0}", sql_to_query);
        ArrayList<HashMap<String, String>> hm_tipos = (ArrayList<HashMap<String, String>>) this.jdbcTemplate.query(
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
        return hm_tipos;
    }

    //obtiene datos de las marcas
    @Override
    public ArrayList<HashMap<String, String>> getInvProdLineas_Marcas() {
        String sql_to_query = "SELECT id,titulo FROM inv_mar WHERE borrado_logico = false ORDER BY titulo;";
        //log.log(Level.INFO, "Ejecutando query de {0}", sql_to_query);
        ArrayList<HashMap<String, String>> hm_tipos = (ArrayList<HashMap<String, String>>) this.jdbcTemplate.query(
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
        return hm_tipos;
    }

    //obtiene datos lienas marcas
    @Override
    public ArrayList<HashMap<String, String>> getInvProdLineas_LM(Integer id_linea) {
        String sql_to_query = "SELECT id,inv_mar_id,inv_prod_linea_id FROM inv_lm WHERE inv_prod_linea_id="+id_linea;
        //log.log(Level.INFO, "Ejecutando query de {0}", sql_to_query);
        ArrayList<HashMap<String, String>> hm_tipos = (ArrayList<HashMap<String, String>>) this.jdbcTemplate.query(
            sql_to_query,
            new Object[]{}, new RowMapper(){
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    HashMap<String, String> row = new HashMap<String, String>();
                    row.put("id",String.valueOf(rs.getInt("id")));
                    row.put("inv_mar_id",String.valueOf(rs.getInt("inv_mar_id")));
                    row.put("inv_prod_linea_id",String.valueOf(rs.getInt("inv_prod_linea_id")));
                    return row;
                }
            }
        );
        return hm_tipos;
    }












    //obtiene datos para el reporte de Compras Netas por Producto
    @Override
    public ArrayList<HashMap<String, String>> getDatos_ReporteComprasNetasProducto(Integer tipo_reporte, String proveedor, String producto,String fecha_inicial,String fecha_final, Integer id_empresa) {
        String orderBy="";

        if(tipo_reporte==1){
            orderBy = " producto";
        }
        if(tipo_reporte==2){
            orderBy = " cxp_prov.razon_social";
        }

        String sql_to_query = ""
        + "SELECT  "
                + "cxp_prov.folio AS clave_proveedor, "
                + "cxp_prov.razon_social AS proveedor, "
                + "erp_prealmacen_entradas.factura, "
                + "to_char(erp_prealmacen_entradas.momento_creacion,'dd/mm/yyyy') AS fecha, "
                + "inv_prod.sku AS codigo_producto, "
                + "inv_prod.descripcion AS producto, "
                + "inv_prod_unidades.titulo_abr AS unidad, "
                + "erp_prealmacen_entradas_detalle.cantidad, "
                + "erp_prealmacen_entradas_detalle.costo_unitario, "
                + "erp_prealmacen_entradas.tipo_de_cambio AS tipo_cambio, "
                + "erp_monedas.descripcion_abr AS moneda, "
                + "(CASE WHEN erp_prealmacen_entradas.moneda_id=1 THEN  "
                        + "erp_prealmacen_entradas_detalle.costo_unitario * erp_prealmacen_entradas_detalle.cantidad  "
                + "ELSE  "
                        + "erp_prealmacen_entradas_detalle.cantidad * (erp_prealmacen_entradas_detalle.costo_unitario * erp_prealmacen_entradas.tipo_de_cambio) "
                + "END) AS compra_neta_mn "
        + "FROM erp_prealmacen_entradas "
        + "JOIN erp_prealmacen_entradas_detalle ON erp_prealmacen_entradas_detalle.prealmacen_entrada_id=erp_prealmacen_entradas.id "
        + "JOIN cxp_prov ON cxp_prov.id=erp_prealmacen_entradas.proveedor_id "
        + "JOIN inv_prod ON inv_prod.id=erp_prealmacen_entradas_detalle.producto_id "
        + "JOIN inv_prod_unidades ON inv_prod_unidades.id=inv_prod.unidad_id "
        + "JOIN erp_monedas ON erp_monedas.id=erp_prealmacen_entradas.moneda_id "
        + "WHERE erp_prealmacen_entradas.empresa_id="+id_empresa+" "
        + "AND erp_prealmacen_entradas.borrado_logico=FALSE "
        + "AND erp_prealmacen_entradas.cancelacion=FALSE "
        + "AND (to_char(erp_prealmacen_entradas.momento_creacion,'yyyymmdd')::INTEGER BETWEEN to_char('"+fecha_inicial+"'::timestamp with time zone,'yyyymmdd')::INTEGER AND to_char('"+fecha_final+"'::timestamp with time zone,'yyyymmdd')::INTEGER) "
        + "AND inv_prod.descripcion ILIKE '%"+producto+"%' "
        + "AND cxp_prov.razon_social ILIKE '%"+proveedor+"%' "
        + "ORDER BY "+orderBy;

        //System.out.println("Busqueda_compras_producto: "+sql_to_query);
        ArrayList<HashMap<String, String>> hm_datos_productos = (ArrayList<HashMap<String, String>>) this.jdbcTemplate.query(
            sql_to_query,
            new Object[]{}, new RowMapper(){
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    HashMap<String, String> row = new HashMap<String, String>();
                    row.put("clave_proveedor",rs.getString("clave_proveedor"));
                    row.put("proveedor",rs.getString("proveedor"));
                    row.put("factura",rs.getString("factura"));
                    row.put("fecha",rs.getString("fecha"));
                    row.put("codigo_producto",rs.getString("codigo_producto"));
                    row.put("producto",rs.getString("producto"));
                    row.put("unidad",rs.getString("unidad"));
                    row.put("cantidad",StringHelper.roundDouble(rs.getString("cantidad"),2));
                    row.put("costo_unitario",StringHelper.roundDouble(rs.getString("costo_unitario"),2));
                    row.put("tipo_cambio",StringHelper.roundDouble(rs.getString("tipo_cambio"),4));
                    row.put("moneda",rs.getString("moneda"));
                    row.put("compra_neta_mn",StringHelper.roundDouble(rs.getString("compra_neta_mn"),2));
                    return row;
                }
            }
        );
        return hm_datos_productos;
    }






    //Metodos para catalogo Productos Familias
    @Override
    public ArrayList<HashMap<String, Object>> getInvProdFamilias_PaginaGrid(String data_string, int offset, int pageSize, String orderBy, String asc) {
        String sql_busqueda = "select id from gral_bus_catalogos(?) as foo (id integer)";

	String sql_to_query = "SELECT inv_prod_familias.id, "
                                + "inv_prod_familias.titulo,"
                                + "inv_prod_familias.descripcion "
                            + "FROM inv_prod_familias "
                            + "JOIN ("+sql_busqueda+") AS sbt ON sbt.id = inv_prod_familias.id "
                            +" order by "+orderBy+" "+asc+" limit ? OFFSET ?";
        //System.out.println("getInvSeccionesGrid: "+sql_to_query+"    "+data_string);

        ArrayList<HashMap<String, Object>> hm = (ArrayList<HashMap<String, Object>>) this.jdbcTemplate.query(
            sql_to_query,
            new Object[]{new String(data_string), new Integer(pageSize),new Integer(offset)}, new RowMapper() {
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
    public ArrayList<HashMap<String, String>> getInvProdFamilias_Datos(Integer id_familia) {
        String sql_query = "SELECT id, titulo, descripcion FROM inv_prod_familias WHERE borrado_logico=FALSE AND id="+id_familia;
        ArrayList<HashMap<String, String>> hm = (ArrayList<HashMap<String, String>>) this.jdbcTemplate.query(
            sql_query,
            new Object[]{}, new RowMapper() {
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










    //Metodos para catalogo Productos Subfamilias
    @Override
    public ArrayList<HashMap<String, Object>> getInvProdSubFamilias_PaginaGrid(String data_string, int offset, int pageSize, String orderBy, String asc) {
        String sql_busqueda = "select id from gral_bus_catalogos(?) as foo (id integer)";

	String sql_to_query = "SELECT inv_prod_familias.id, "
                                    + "inv_prod_familias.titulo as subfamilia, "
                                    + "inv_prod_familias.descripcion, "
                                    + "sbt.titulo AS familia "
                            + "FROM inv_prod_familias  "
                            + "JOIN ( "
                                    + "SELECT id, identificador_familia_padre, titulo  "
                                    + "FROM inv_prod_familias  "
                                    + "WHERE borrado_logico=FALSE AND id=identificador_familia_padre "
                            + ") AS sbt ON sbt.identificador_familia_padre=inv_prod_familias.identificador_familia_padre "
                            + "JOIN ("+sql_busqueda+") AS sbt2 ON sbt2.id = inv_prod_familias.id "
                            +" order by "+orderBy+" "+asc+" limit ? OFFSET ?";

        //System.out.println("getInvSeccionesGrid: "+sql_to_query+"    "+data_string);

        ArrayList<HashMap<String, Object>> hm = (ArrayList<HashMap<String, Object>>) this.jdbcTemplate.query(
            sql_to_query,
            new Object[]{new String(data_string), new Integer(pageSize),new Integer(offset)}, new RowMapper() {
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    HashMap<String, Object> row = new HashMap<String, Object>();
                    row.put("id",rs.getInt("id"));
                    row.put("subfamilia",rs.getString("subfamilia"));
                    row.put("descripcion",rs.getString("descripcion"));
                    row.put("familia",rs.getString("familia"));
                    return row;
                }
            }
        );
        return hm;
    }


    //Obtener todas las familias
    //se utiliza en catalogo de Productos y catalogo de Subfamilias
    @Override
    public ArrayList<HashMap<String, String>> getInvProdSubFamilias_Familias(Integer id_empresa) {
        String sql_query = "SELECT  id, titulo, descripcion FROM inv_prod_familias WHERE borrado_logico=FALSE AND id=identificador_familia_padre AND gral_emp_id="+id_empresa;
        ArrayList<HashMap<String, String>> hm = (ArrayList<HashMap<String, String>>) this.jdbcTemplate.query(
            sql_query,
            new Object[]{}, new RowMapper() {
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


    //Obtener datos de la subfamilia
    @Override
    public ArrayList<HashMap<String, String>> getInvProdSubFamilias_Datos(Integer id_subfamilia) {
        String sql_query = "SELECT  id, titulo, descripcion, identificador_familia_padre FROM inv_prod_familias WHERE id="+id_subfamilia;
        ArrayList<HashMap<String, String>> hm = (ArrayList<HashMap<String, String>>) this.jdbcTemplate.query(
            sql_query,
            new Object[]{}, new RowMapper() {
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    HashMap<String, String> row = new HashMap<String, String>();
                    row.put("id",String.valueOf(rs.getInt("id")));
                    row.put("titulo",rs.getString("titulo"));
                    row.put("descripcion",rs.getString("descripcion"));
                    row.put("identificador_familia_padre",rs.getString("identificador_familia_padre"));
                    return row;
                }
            }
        );
        return hm;
    }




    //catalogo de plazas
    @Override
    public ArrayList<HashMap<String, Object>> getPlazas_PaginaGrid(String data_string, int offset, int pageSize, String orderBy, String asc) {
        String sql_busqueda = "select id from gral_bus_catalogos(?) as foo (id integer)";

	String sql_to_query =
                                "SELECT gral_plazas.id "
                                +",gral_plazas.titulo as plaza "
                                +",gral_plazas.descripcion as nombre "
                                +",inv_zonas.titulo as zona "
                                +",(CASE WHEN gral_plazas.estatus=false "
                                +"THEN 'INACTIVO' ELSE 'ACTIVO' END) AS estatus "
                                +"FROM gral_plazas "
                                +"JOIN inv_zonas on inv_zonas.id= gral_plazas.inv_zonas_id "
                                +"JOIN ("+sql_busqueda+") AS sbt ON sbt.id = gral_plazas.id "
                                +"where gral_plazas.borrado_logico =false "
                                +"order by "+orderBy+" "+asc+" limit ? OFFSET ?";

        //System.out.println("Busqueda GetPage: "+sql_to_query);
        ArrayList<HashMap<String, Object>> hm = (ArrayList<HashMap<String, Object>>) this.jdbcTemplate.query(
            sql_to_query,
            new Object[]{new String(data_string), new Integer(pageSize),new Integer(offset)}, new RowMapper() {
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    HashMap<String, Object> row = new HashMap<String, Object>();
                    row.put("id",rs.getInt("id"));
                    row.put("plaza",rs.getString("plaza"));
                    row.put("nombre",rs.getString("nombre"));
                    row.put("zona",rs.getString("zona"));
                   row.put("estatus",rs.getString("estatus"));
                    return row;
                }
            }
        );
        return hm;
    }


     @Override
        public ArrayList<HashMap<String, String>> getPlazas_Datos(Integer id_plaza) {
        String sql_to_query = "SELECT "
                                    +" gral_plazas.id, "
                                    +" gral_plazas.titulo as plaza, "
                                    +" gral_plazas.descripcion as nombre, "
                                    +" inv_zonas.titulo as zona, "
                                    +"(CASE WHEN gral_plazas.estatus=false "
                                    +" THEN 'INACTIVO' ELSE 'ACTIVO' END) AS estatus "
                                    +" from gral_plazas "
                                    +" join inv_zonas on inv_zonas.id= gral_plazas.inv_zonas_id "
                                  +" WHERE gral_plazas.id ="+id_plaza;

        ArrayList<HashMap<String, String>> datos_plazas = (ArrayList<HashMap<String, String>>) this.jdbcTemplate.query(
            sql_to_query,
            new Object[]{}, new RowMapper(){
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    HashMap<String, String> row = new HashMap<String, String>();
                    row.put("id",String.valueOf(rs.getInt("id")));
                    row.put("plaza",rs.getString("plaza"));
                    row.put("nombre",rs.getString("nombre"));
                    row.put("estatus",rs.getString("estatus"));
                    row.put("zona",rs.getString("zona"));
                    return row;

                }
            }
        );

        return datos_plazas;
    }


    @Override
    public ArrayList<HashMap<String, Object>> getZonas_PaginaGrid(String data_string, int offset, int pageSize, String orderBy, String asc) {
        String sql_busqueda = "select id from gral_bus_catalogos(?) as foo (id integer)";

	String sql_to_query = "SELECT inv_zonas.id "
                               +",inv_zonas.titulo as zona "
                               +",inv_zonas.descripcion "
                               +",(CASE WHEN inv_zonas.estatus=false "
                               +" THEN 'INACTIVO' ELSE 'ACTIVO' END) AS estatus "
                               +"FROM inv_zonas "
                               +"JOIN ("+sql_busqueda+") AS sbt ON sbt.id = inv_zonas.id "
                                +"where inv_zonas.borrado_logico =false "
                               +"order by "+orderBy+" "+asc+" limit ? OFFSET ?";

        //System.out.println("Busqueda GetPage: "+sql_to_query);
        ArrayList<HashMap<String, Object>> hm = (ArrayList<HashMap<String, Object>>) this.jdbcTemplate.query(
            sql_to_query,
            new Object[]{new String(data_string), new Integer(pageSize),new Integer(offset)}, new RowMapper() {
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    HashMap<String, Object> row = new HashMap<String, Object>();
                    row.put("id",rs.getInt("id"));
                    row.put("zona",rs.getString("zona"));
                    row.put("descripcion",rs.getString("descripcion"));
                    row.put("estatus",rs.getString("estatus"));

                    return row;
                }
            }
        );
        return hm;
    }


     @Override
    public ArrayList<HashMap<String, String>> getZonas_Datos(Integer id_zona) {
        String sql_to_query = "SELECT inv_zonas.titulo,inv_zonas.id,inv_zonas.descripcion,(CASE  WHEN inv_zonas.borrado_logico =false THEN 'false' ELSE 'true' END) AS estatus  FROM inv_zonas WHERE id ="+id_zona;

        ArrayList<HashMap<String, String>> datos_zonas = (ArrayList<HashMap<String, String>>) this.jdbcTemplate.query(
            sql_to_query,
            new Object[]{}, new RowMapper(){
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    HashMap<String, String> row = new HashMap<String, String>();
                    row.put("id",String.valueOf(rs.getInt("id")));
                    row.put("titulo",rs.getString("titulo"));
                    row.put("estatus",rs.getString("estatus"));
                    row.put("descripcion",rs.getString("descripcion"));

                    return row;
                }
            }
        );
        return datos_zonas;
    }

    @Override
    public ArrayList<HashMap<String, String>> getPlazas_zonas() {
        String sql_to_query = "SELECT id, titulo FROM  inv_zonas WHERE borrado_logico=FALSE ORDER BY id ASC;";
        //log.log(Level.INFO, "Ejecutando query de {0}", sql_to_query);
        ArrayList<HashMap<String, String>> hm_zonas = (ArrayList<HashMap<String, String>>) this.jdbcTemplate.query(
            sql_to_query,
            new Object[]{}, new RowMapper(){
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    HashMap<String, Object> row = new HashMap<String, Object>();
                    row.put("id",rs.getInt("id"));
                    row.put("descripcion",rs.getString("titulo"));
                    return row;
                }
            }
        );
        return hm_zonas;
    }




    //catalogo de producto grupos
   @Override
    public ArrayList<HashMap<String, Object>> getProductoGrupos_PaginaGrid(String data_string, int offset, int pageSize, String orderBy, String asc) {
        String sql_busqueda = "select id from gral_bus_catalogos(?) as foo (id integer)";

	String sql_to_query = "SELECT inv_prod_grupos.id "
                               +",inv_prod_grupos.titulo as grupo  "
                               +",inv_prod_grupos.descripcion "

                               +"FROM inv_prod_grupos "
                               +"JOIN ("+sql_busqueda+") AS sbt ON sbt.id = inv_prod_grupos.id "
                               +"order by "+orderBy+" "+asc+" limit ? OFFSET ?";

        //System.out.println("Busqueda GetPage: "+sql_to_query);
        ArrayList<HashMap<String, Object>> hm = (ArrayList<HashMap<String, Object>>) this.jdbcTemplate.query(
            sql_to_query,
            new Object[]{new String(data_string), new Integer(pageSize),new Integer(offset)}, new RowMapper() {
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    HashMap<String, Object> row = new HashMap<String, Object>();
                    row.put("id",rs.getInt("id"));
                    row.put("grupo",rs.getString("grupo"));
                    row.put("descripcion",rs.getString("descripcion"));

                    return row;
                }
            }
        );
        return hm;
    }


     @Override
     public ArrayList<HashMap<String, String>> getProductosGrupos_Datos(Integer id_grupoproducto) {
     String sql_to_query = "SELECT inv_prod_grupos.id,inv_prod_grupos.titulo as grupo,inv_prod_grupos.descripcion  FROM inv_prod_grupos WHERE id ="+id_grupoproducto;

        ArrayList<HashMap<String, String>> datos_grupoproducto = (ArrayList<HashMap<String, String>>) this.jdbcTemplate.query(
            sql_to_query,
            new Object[]{}, new RowMapper(){
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    HashMap<String, String> row = new HashMap<String, String>();
                    row.put("id",String.valueOf(rs.getInt("id")));
                    row.put("grupo",rs.getString("grupo"));
                    row.put("descripcion",rs.getString("descripcion"));

                    return row;
                }
            }
        );
        return datos_grupoproducto;
    }





    //Metodos para catalogo InvPre
    @Override
    public ArrayList<HashMap<String, Object>> getInvPreGrid(String data_string, int offset, int pageSize, String orderBy, String asc) {
        String sql_busqueda = "select id from gral_bus_catalogos(?) as foo (id integer)";

	String sql_to_query = "SELECT inv_pre.id,inv_prod.sku ,inv_prod.descripcion, inv_pre.precio_1,inv_pre.precio_2,inv_pre.precio_3 "
                            + "FROM inv_pre "
                            + "JOIN ("+sql_busqueda+") AS sbt ON sbt.id = inv_pre.id "
                            +" join inv_prod on inv_prod.id=inv_pre.inv_prod_id "
                            +"order by "+orderBy+" "+asc+" limit ? OFFSET ?";


        ArrayList<HashMap<String, Object>> hm = (ArrayList<HashMap<String, Object>>) this.jdbcTemplate.query(
            sql_to_query,
            new Object[]{new String(data_string), new Integer(pageSize),new Integer(offset)}, new RowMapper() {
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    HashMap<String, Object> row = new HashMap<String, Object>();
                    row.put("id",rs.getInt("id"));
                    row.put("sku",rs.getString("sku"));
                    row.put("titulo_es",rs.getString("descripcion"));
                    row.put("precio_1",StringHelper.roundDouble(rs.getDouble("precio_1"), 2));
                    row.put("precio_2",StringHelper.roundDouble(rs.getDouble("precio_2"), 2));
                    row.put("precio_3",StringHelper.roundDouble(rs.getDouble("precio_3"), 2));

                    return row;
                }
            }
        );
        return hm;
    }


    //obtiene datos del tipo de movimiento actual
    @Override
    public ArrayList<HashMap<String, String>> getInvPre_Datos(Integer id) {
        String sql_query = "SELECT inv_pre.*, "
                                + "inv_prod.sku, "
                                + "inv_prod.descripcion as titulo,"
                                + "inv_prod_unidades.titulo as utitulo "
                        + "FROM inv_pre "
                        + "join inv_prod on inv_prod.id=inv_pre.inv_prod_id "
                        + "join inv_prod_unidades on inv_prod_unidades.id=inv_prod.unidad_id  "
                        + " WHERE inv_pre.id = ? and inv_pre.borrado_logico=false";

        ArrayList<HashMap<String, String>> hm = (ArrayList<HashMap<String, String>>) this.jdbcTemplate.query(
            sql_query,
            new Object[]{new Integer(id)}, new RowMapper() {
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    HashMap<String, String> row = new HashMap<String, String>();
                    row.put("id",String.valueOf(rs.getInt("id")));
                    row.put("inv_prod_id",String.valueOf(rs.getInt("inv_prod_id")));
                    row.put("sku",rs.getString("sku"));
                    row.put("titulo",rs.getString("titulo"));
                    row.put("utitulo",rs.getString("utitulo"));
                    row.put("precio_1",StringHelper.roundDouble(rs.getDouble("precio_1"), 2));
                    row.put("precio_2",StringHelper.roundDouble(rs.getDouble("precio_2"), 2));
                    row.put("precio_3",StringHelper.roundDouble(rs.getDouble("precio_3"), 2));
                    row.put("precio_4",StringHelper.roundDouble(rs.getDouble("precio_4"), 2));
                    row.put("precio_5",StringHelper.roundDouble(rs.getDouble("precio_5"), 2));
                    row.put("precio_6",StringHelper.roundDouble(rs.getDouble("precio_6"), 2));
                    row.put("precio_7",StringHelper.roundDouble(rs.getDouble("precio_7"), 2));
                    row.put("precio_8",StringHelper.roundDouble(rs.getDouble("precio_8"), 2));
                    row.put("precio_9",StringHelper.roundDouble(rs.getDouble("precio_9"), 2));
                    row.put("precio_10",StringHelper.roundDouble(rs.getDouble("precio_10"), 2));
                    row.put("descuento_1",StringHelper.roundDouble(rs.getDouble("descuento_1"), 2));
                    row.put("descuento_2",StringHelper.roundDouble(rs.getDouble("descuento_2"), 2));
                    row.put("descuento_3",StringHelper.roundDouble(rs.getDouble("descuento_3"), 2));
                    row.put("descuento_4",StringHelper.roundDouble(rs.getDouble("descuento_4"), 2));
                    row.put("descuento_5",StringHelper.roundDouble(rs.getDouble("descuento_5"), 2));
                    row.put("descuento_6",StringHelper.roundDouble(rs.getDouble("descuento_6"), 2));
                    row.put("descuento_7",StringHelper.roundDouble(rs.getDouble("descuento_7"), 2));
                    row.put("descuento_8",StringHelper.roundDouble(rs.getDouble("descuento_8"), 2));
                    row.put("descuento_9",StringHelper.roundDouble(rs.getDouble("descuento_9"), 2));
                    row.put("descuento_10",StringHelper.roundDouble(rs.getDouble("descuento_10"), 2));
                    row.put("base_precio_1",String.valueOf(rs.getInt("base_precio_1")));
                    row.put("base_precio_2",String.valueOf(rs.getInt("base_precio_2")));
                    row.put("base_precio_3",String.valueOf(rs.getInt("base_precio_3")));
                    row.put("base_precio_4",String.valueOf(rs.getInt("base_precio_4")));
                    row.put("base_precio_5",String.valueOf(rs.getInt("base_precio_5")));
                    row.put("base_precio_6",String.valueOf(rs.getInt("base_precio_6")));
                    row.put("base_precio_7",String.valueOf(rs.getInt("base_precio_7")));
                    row.put("base_precio_8",String.valueOf(rs.getInt("base_precio_8")));
                    row.put("base_precio_9",String.valueOf(rs.getInt("base_precio_9")));
                    row.put("base_precio_10",String.valueOf(rs.getInt("base_precio_10")));
                    row.put("default_precio_1",StringHelper.roundDouble(rs.getDouble("default_precio_1"), 2));
                    row.put("default_precio_2",StringHelper.roundDouble(rs.getDouble("default_precio_2"), 2));
                    row.put("default_precio_3",StringHelper.roundDouble(rs.getDouble("default_precio_3"), 2));
                    row.put("default_precio_4",StringHelper.roundDouble(rs.getDouble("default_precio_4"), 2));
                    row.put("default_precio_5",StringHelper.roundDouble(rs.getDouble("default_precio_5"), 2));
                    row.put("default_precio_6",StringHelper.roundDouble(rs.getDouble("default_precio_6"), 2));
                    row.put("default_precio_7",StringHelper.roundDouble(rs.getDouble("default_precio_7"), 2));
                    row.put("default_precio_8",StringHelper.roundDouble(rs.getDouble("default_precio_8"), 2));
                    row.put("default_precio_9",StringHelper.roundDouble(rs.getDouble("default_precio_9"), 2));
                    row.put("default_precio_10",StringHelper.roundDouble(rs.getDouble("default_precio_10"), 2));
                    row.put("operacion_precio_1",String.valueOf(rs.getInt("operacion_precio_1")));
                    row.put("operacion_precio_2",String.valueOf(rs.getInt("operacion_precio_2")));
                    row.put("operacion_precio_3",String.valueOf(rs.getInt("operacion_precio_3")));
                    row.put("operacion_precio_4",String.valueOf(rs.getInt("operacion_precio_4")));
                    row.put("operacion_precio_5",String.valueOf(rs.getInt("operacion_precio_5")));
                    row.put("operacion_precio_6",String.valueOf(rs.getInt("operacion_precio_6")));
                    row.put("operacion_precio_7",String.valueOf(rs.getInt("operacion_precio_7")));
                    row.put("operacion_precio_8",String.valueOf(rs.getInt("operacion_precio_8")));
                    row.put("operacion_precio_9",String.valueOf(rs.getInt("operacion_precio_9")));
                    row.put("operacion_precio_10",String.valueOf(rs.getInt("operacion_precio_10")));
                    row.put("calculo_precio_1",String.valueOf(rs.getInt("calculo_precio_1")));
                    row.put("calculo_precio_2",String.valueOf(rs.getInt("calculo_precio_2")));
                    row.put("calculo_precio_3",String.valueOf(rs.getInt("calculo_precio_3")));
                    row.put("calculo_precio_4",String.valueOf(rs.getInt("calculo_precio_4")));
                    row.put("calculo_precio_5",String.valueOf(rs.getInt("calculo_precio_5")));
                    row.put("calculo_precio_6",String.valueOf(rs.getInt("calculo_precio_6")));
                    row.put("calculo_precio_7",String.valueOf(rs.getInt("calculo_precio_7")));
                    row.put("calculo_precio_8",String.valueOf(rs.getInt("calculo_precio_8")));
                    row.put("calculo_precio_9",String.valueOf(rs.getInt("calculo_precio_9")));
                    row.put("calculo_precio_10",String.valueOf(rs.getInt("calculo_precio_10")));
                    row.put("redondeo_precio_1",String.valueOf(rs.getInt("redondeo_precio_1")));
                    row.put("redondeo_precio_2",String.valueOf(rs.getInt("redondeo_precio_2")));
                    row.put("redondeo_precio_3",String.valueOf(rs.getInt("redondeo_precio_3")));
                    row.put("redondeo_precio_4",String.valueOf(rs.getInt("redondeo_precio_4")));
                    row.put("redondeo_precio_5",String.valueOf(rs.getInt("redondeo_precio_5")));
                    row.put("redondeo_precio_6",String.valueOf(rs.getInt("redondeo_precio_6")));
                    row.put("redondeo_precio_7",String.valueOf(rs.getInt("redondeo_precio_7")));
                    row.put("redondeo_precio_8",String.valueOf(rs.getInt("redondeo_precio_8")));
                    row.put("redondeo_precio_9",String.valueOf(rs.getInt("redondeo_precio_9")));
                    row.put("redondeo_precio_10",String.valueOf(rs.getInt("redondeo_precio_10")));
                    return row;
                }
            }
        );
        return hm;
    }



     //catalogo de comisiones de articulos
    //Metodos para catalogo InvCom
    @Override
    public ArrayList<HashMap<String, Object>> getInvComGrid(String data_string, int offset, int pageSize, String orderBy, String asc) {
        String sql_busqueda = "select id from gral_bus_catalogos(?) as foo (id integer)";

	String sql_to_query = "SELECT inv_com.id,inv_prod.sku ,inv_prod.descripcion, inv_com.nivel,inv_com.escala, inv_com.limite_inferior,inv_com.limite_superior,inv_com.comision "
                            + "FROM inv_com "
                            + "JOIN ("+sql_busqueda+") AS sbt ON sbt.id = inv_com.id "
                            +" join inv_prod on inv_prod.id=inv_com.inv_prod_id "
                            +"order by "+orderBy+" "+asc+" limit ? OFFSET ?";

        ArrayList<HashMap<String, Object>> hm = (ArrayList<HashMap<String, Object>>) this.jdbcTemplate.query(
            sql_to_query,
            new Object[]{new String(data_string), new Integer(pageSize),new Integer(offset)}, new RowMapper() {
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    HashMap<String, Object> row = new HashMap<String, Object>();
                    row.put("id",rs.getInt("id"));
                    row.put("sku",rs.getString("sku"));
                    row.put("descripcion",rs.getString("descripcion"));
                    row.put("nivel",rs.getInt("nivel"));
                    row.put("escala",rs.getInt("escala"));
                    row.put("limite_inferior",StringHelper.roundDouble(rs.getDouble("limite_inferior"), 2));
                    row.put("limite_superior",StringHelper.roundDouble(rs.getDouble("limite_superior"), 2));
                    row.put("comision",StringHelper.roundDouble(rs.getDouble("comision"), 2));
                    return row;
                }
            }
        );
        return hm;
    }

     //obtiene datos del InvCom
    @Override
    public ArrayList<HashMap<String, String>> getInvCom_Datos(Integer id) {
        String sql_query = "SELECT inv_com.*, inv_prod.sku, inv_prod.descripcion as titulo FROM "
                + "inv_com join inv_prod on inv_prod.id=inv_com.inv_prod_id "
                + " WHERE inv_com.id = ? and inv_com.borrado_logico=false";
        ArrayList<HashMap<String, String>> hm = (ArrayList<HashMap<String, String>>) this.jdbcTemplate.query(
            sql_query,
            new Object[]{new Integer(id)}, new RowMapper() {
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    HashMap<String, String> row = new HashMap<String, String>();
                    row.put("id",String.valueOf(rs.getInt("id")));
                    row.put("inv_prod_id",String.valueOf(rs.getInt("inv_prod_id")));
                    row.put("sku",rs.getString("sku"));
                    row.put("titulo",rs.getString("titulo"));
                    row.put("escala",String.valueOf(rs.getInt("escala")));
                    row.put("nivel",String.valueOf(rs.getInt("nivel")));
                    row.put("limite_inferior",StringHelper.roundDouble(rs.getDouble("limite_inferior"), 2));
                    row.put("limite_superior",StringHelper.roundDouble(rs.getDouble("limite_superior"), 2));
                    row.put("comision",StringHelper.roundDouble(rs.getDouble("comision"), 2));
                    row.put("comision_valor",StringHelper.roundDouble(rs.getDouble("comision_valor"), 2));

                    return row;
                }
            }
        );
        return hm;
    }




    //catalogo de Invetario clasificaciones de Stock
    @Override
    public ArrayList<HashMap<String, Object>> getStock_PaginaGrid(String data_string, int offset, int pageSize, String orderBy, String asc) {
        String sql_busqueda = "select id from gral_bus_catalogos(?) as foo (id integer)";

	String sql_to_query =  "select inv_stock_clasificaciones.id,titulo as clasificacion, descripcion from inv_stock_clasificaciones "

                               +"JOIN ("+sql_busqueda+") AS sbt ON sbt.id = inv_stock_clasificaciones.id "
                                +"where inv_stock_clasificaciones.borrado_logico =false "
                               +"order by "+orderBy+" "+asc+" limit ? OFFSET ?";


        //System.out.println("informacion que carga el grid de stock    : "+sql_to_query);
        ArrayList<HashMap<String, Object>> hm = (ArrayList<HashMap<String, Object>>) this.jdbcTemplate.query(
            sql_to_query,
            new Object[]{new String(data_string), new Integer(pageSize),new Integer(offset)}, new RowMapper() {
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    HashMap<String, Object> row = new HashMap<String, Object>();
                    row.put("id",rs.getInt("id"));
                    row.put("clasificacion",rs.getString("clasificacion"));
                    row.put("descripcion",rs.getString("descripcion"));

                    return row;
                }
            }
        );
        return hm;
    }


     @Override
    public ArrayList<HashMap<String, String>> getStock_Datos(Integer id_stock) {
        String sql_to_query = "select inv_stock_clasificaciones.id,titulo as clasificacion, descripcion from inv_stock_clasificaciones WHERE id ="+id_stock;

        ArrayList<HashMap<String, String>> datos_stock = (ArrayList<HashMap<String, String>>) this.jdbcTemplate.query(
            sql_to_query,
            new Object[]{}, new RowMapper(){
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    HashMap<String, String> row = new HashMap<String, String>();
                    row.put("id",String.valueOf(rs.getInt("id")));
                    row.put("clasificacion",rs.getString("clasificacion"));
                    row.put("descripcion",rs.getString("descripcion"));

                    return row;
                }
            }
        );
        return datos_stock;
    }






    //catalogo inv clasificaciones
    @Override
    public ArrayList<HashMap<String, Object>> getInvClas_PaginaGrid(String data_string, int offset, int pageSize, String orderBy, String asc) {
        String sql_busqueda = "select id from gral_bus_catalogos(?) as foo (id integer)";

        String sql_to_query =  "select inv_clas.id, "
                            +" inv_clas.titulo as clasificacion, "
                            +" inv_clas.descripcion, "
                            +" inv_clas.stock_seguridad, "
                            +" inv_clas.factor_maximo  "
                            +" from inv_clas "
                            +"JOIN ("+sql_busqueda+") AS sbt ON sbt.id = inv_clas.id "
                            +"where inv_clas.borrado_logico =false "
                            +"order by "+orderBy+" "+asc+" limit ? OFFSET ?";


        //System.out.println("informacion que carga el grid de inv_clas    : "+sql_to_query);
        ArrayList<HashMap<String, Object>> hm = (ArrayList<HashMap<String, Object>>) this.jdbcTemplate.query(
            sql_to_query,
            new Object[]{new String(data_string), new Integer(pageSize),new Integer(offset)}, new RowMapper() {
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    HashMap<String, Object> row = new HashMap<String, Object>();
                    row.put("id",rs.getInt("id"));
                    row.put("clasificacion",rs.getString("clasificacion"));
                    row.put("descripcion",rs.getString("descripcion"));
                    row.put("stock_seguridad",rs.getDouble("stock_seguridad"));
                    row.put("factor_maximo",rs.getDouble("factor_maximo"));

                    return row;
                }
            }
        );
        return hm;
    }


     @Override
    public ArrayList<HashMap<String, String>> getInvClas_Datos(Integer id_invclas) {
        String sql_to_query = "select inv_clas.id, "
                            +" inv_clas.titulo as clasificacion, "
                            +" inv_clas.descripcion, "
                            +" inv_clas.stock_seguridad, "
                            +" inv_clas.factor_maximo  "
                            +" from inv_clas  WHERE id ="+id_invclas;

        ArrayList<HashMap<String, String>> hm = (ArrayList<HashMap<String, String>>) this.jdbcTemplate.query(
            sql_to_query,
            new Object[]{}, new RowMapper(){
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    HashMap<String, String> row = new HashMap<String, String>();
                    row.put("id",String.valueOf(rs.getInt("id")));
                    row.put("clasificacion",rs.getString("clasificacion"));
                    row.put("descripcion",rs.getString("descripcion"));
                    row.put("stock_seguridad",StringHelper.roundDouble(rs.getString("stock_seguridad"),2));
                    row.put("factor_maximo",StringHelper.roundDouble(rs.getString("factor_maximo"),2));

                    return row;
                }
            }
        );
        return hm;
    }




    //Asignacion de sucursales
    //traer sucursales
     @Override
    public ArrayList<HashMap<String, String>> getSucursales(Integer id_empresa){

        String sql_to_query = "select gral_suc.id,"
                                +" gral_suc.titulo as sucursal "
                                +"from gral_suc where empresa_id="+id_empresa;

        ArrayList<HashMap<String, String>> sucursales = (ArrayList<HashMap<String, String>>) this.jdbcTemplate.query(
            sql_to_query,
            new Object[]{}, new RowMapper(){
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    HashMap<String, String> row = new HashMap<String, String>();
                    row.put("id",String.valueOf(rs.getInt("id")));
                    row.put("sucursal",rs.getString("sucursal"));

                    return row;
                }
            }
        );
        return sucursales;
    }

     //traer plazas
     @Override
    public ArrayList<HashMap<String, String>> getPlazas(Integer id_empresa){

        String sql_to_query = "select gral_plazas.id,"
                                +" gral_plazas.titulo as plaza "
                                +"from gral_plazas "
                                +"where gral_plazas.borrado_logico=false and empresa_id="+id_empresa;

        ArrayList<HashMap<String, String>> plazas = (ArrayList<HashMap<String, String>>) this.jdbcTemplate.query(
            sql_to_query,
            new Object[]{}, new RowMapper(){
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    HashMap<String, String> row = new HashMap<String, String>();
                    row.put("id",String.valueOf(rs.getInt("id")));
                    row.put("plaza",rs.getString("plaza"));

                    return row;
                }
            }
        );
        return plazas;
    }

      @Override
    public ArrayList<HashMap<String, String>> getPlazasAsignadas(Integer id_empresa,Integer id_sucursal){

        String sql_to_query = "SELECT gral_plazas.id,gral_plazas.titulo as plaza "
                                +"FROM gral_plazas "
                                +"LEFT JOIN gral_suc_pza ON gral_suc_pza.plaza_id=gral_plazas.id "
                                +"WHERE gral_suc_pza.sucursal_id="+id_sucursal;
        //System.out.println("Plazas Asignadas: "+sql_to_query);
        ArrayList<HashMap<String, String>> plazasAsig = (ArrayList<HashMap<String, String>>) this.jdbcTemplate.query(
            sql_to_query,
            new Object[]{}, new RowMapper(){
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    HashMap<String, String> row = new HashMap<String, String>();
                    row.put("id",String.valueOf(rs.getInt("id")));
                    row.put("plaza",rs.getString("plaza"));

                    return row;
                }
            }
        );
        return plazasAsig;
    }

       @Override
    public ArrayList<HashMap<String, String>> getPlazasNoAsignadas(Integer id_empresa,Integer id_sucursal){

        String sql_to_query = "SELECT gral_plazas.id,gral_plazas.titulo as plaza "
                                +"FROM gral_plazas "
                                +"WHERE borrado_logico=false AND empresa_id=4 "
                                +"AND  id NOT IN ( "
                                +"SELECT gral_plazas.id "
                                +"FROM gral_plazas "
                                +"LEFT JOIN gral_suc_pza ON gral_suc_pza.plaza_id=gral_plazas.id "
                                +"WHERE gral_suc_pza.sucursal_id="+id_sucursal
                                +") ";
        //System.out.println("Plazas No Asignadas: "+sql_to_query);
        ArrayList<HashMap<String, String>> plazasNoAsig = (ArrayList<HashMap<String, String>>) this.jdbcTemplate.query(
            sql_to_query,
            new Object[]{}, new RowMapper(){
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    HashMap<String, String> row = new HashMap<String, String>();
                    row.put("id",String.valueOf(rs.getInt("id")));
                    row.put("plaza",rs.getString("plaza"));

                    return row;
                }
            }
        );
        return plazasNoAsig;
    }




    //Metodos para catalogo InvPreOfe
    @Override
    public ArrayList<HashMap<String, Object>> getInvPreOfeGrid(String data_string, int offset, int pageSize, String orderBy, String asc) {
        String sql_busqueda = "select id from gral_bus_catalogos(?) as foo (id integer)";

	String sql_to_query = "SELECT inv_pre_ofe.id,"
                                + "inv_prod.sku,"
                                + "inv_prod.descripcion, "
                                + "inv_pre_ofe.precio_oferta,"
                                + "inv_pre_ofe.fecha_inicial, "
                                + "inv_pre_ofe.fecha_final "
                            + "FROM inv_pre_ofe "
                            + "JOIN ("+sql_busqueda+") AS sbt ON sbt.id = inv_pre_ofe.id "
                            +" join inv_prod on inv_prod.id=inv_pre_ofe.inv_prod_id "
                            +"order by "+orderBy+" "+asc+" limit ? OFFSET ?";
        //System.out.println("getInvSeccionesGrid: "+sql_to_query+"    "+data_string);

        ArrayList<HashMap<String, Object>> hm = (ArrayList<HashMap<String, Object>>) this.jdbcTemplate.query(
            sql_to_query,
            new Object[]{new String(data_string), new Integer(pageSize),new Integer(offset)}, new RowMapper() {
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    HashMap<String, Object> row = new HashMap<String, Object>();
                    row.put("id",rs.getInt("id"));
                    row.put("sku",rs.getString("sku"));
                    row.put("descripcion",rs.getString("descripcion"));
                    row.put("precio_oferta",StringHelper.roundDouble(rs.getDouble("precio_oferta"), 2));
                    row.put("fecha_inicial",String.valueOf(rs.getDate("fecha_inicial")));
                    row.put("fecha_final",String.valueOf(rs.getDate("fecha_final")));
                    return row;
                }
            }
        );
        return hm;
    }

    //obtiene datos del InvCom
    @Override
    public ArrayList<HashMap<String, String>> getInvPreOfe_Datos(Integer id) {
        String sql_query = "SELECT inv_pre_ofe.*,inv_prod.sku, inv_prod.descripcion as titulo FROM "
                + "inv_pre_ofe join inv_prod on inv_prod.id=inv_pre_ofe.inv_prod_id "
                + " WHERE inv_pre_ofe.id = ? and inv_pre_ofe.borrado_logico=false";
        ArrayList<HashMap<String, String>> hm = (ArrayList<HashMap<String, String>>) this.jdbcTemplate.query(
            sql_query,
            new Object[]{new Integer(id)}, new RowMapper() {
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    HashMap<String, String> row = new HashMap<String, String>();
                    row.put("id",String.valueOf(rs.getInt("id")));
                    row.put("inv_prod_id",String.valueOf(rs.getInt("inv_prod_id")));
                    row.put("sku",rs.getString("sku"));
                    row.put("titulo",rs.getString("titulo"));
                    row.put("precio_oferta",String.valueOf(rs.getInt("precio_oferta")));
                    row.put("descto_max",String.valueOf(rs.getInt("descto_max")));
                    row.put("criterio_oferta",String.valueOf(rs.getBoolean("criterio_oferta")));
                    row.put("precio_lista_1",String.valueOf(rs.getBoolean("precio_lista_1")));
                    row.put("precio_lista_2",String.valueOf(rs.getBoolean("precio_lista_2")));
                    row.put("precio_lista_3",String.valueOf(rs.getBoolean("precio_lista_3")));
                    row.put("precio_lista_4",String.valueOf(rs.getBoolean("precio_lista_4")));
                    row.put("precio_lista_5",String.valueOf(rs.getBoolean("precio_lista_5")));
                    row.put("precio_lista_6",String.valueOf(rs.getBoolean("precio_lista_6")));
                    row.put("precio_lista_7",String.valueOf(rs.getBoolean("precio_lista_7")));
                    row.put("precio_lista_8",String.valueOf(rs.getBoolean("precio_lista_8")));
                    row.put("precio_lista_9",String.valueOf(rs.getBoolean("precio_lista_9")));
                    row.put("precio_lista_10",String.valueOf(rs.getBoolean("precio_lista_10")));
                    row.put("tipo_descto_precio",String.valueOf(rs.getBoolean("tipo_descto_precio")));
                    row.put("fecha_inicial",String.valueOf(rs.getDate("fecha_inicial")));
                    row.put("fecha_final",String.valueOf(rs.getDate("fecha_final")));
                    return row;
                }
            }
        );
        return hm;
    }




    //metodos para aplicativo orden pre-subensamble
    @Override
    public ArrayList<HashMap<String, Object>> getInvOrdPreSubenGrid(String data_string, int offset, int pageSize, String orderBy, String asc) {
        String sql_busqueda = "select id from gral_bus_catalogos(?) as foo (id integer)";

	String sql_to_query = "select opse.id, "
                                + "opse.folio, "
                                + "(CASE WHEN opse.estatus=0 THEN ''  "
                                        + "WHEN opse.estatus=1 THEN 'Enterado'  "
                                        + "WHEN opse.estatus=2 THEN 'En Proceso'  "
                                        + "WHEN opse.estatus=3 THEN 'Listo'  "
                                        + "WHEN opse.estatus=4 THEN 'Cancelado'  "
                                + "ELSE '' END) AS estatus, "
                                + "opse.momento_creacion "
                                + "FROM inv_ord_subensamble as opse "
                            + "JOIN ("+sql_busqueda+") AS sbt ON sbt.id=opse.id "
                            + "order by "+orderBy+" "+asc+" LIMIT ? OFFSET ? ";

        //System.out.println("getInvOrdPreSubenGrid: "+sql_to_query+"    "+data_string);
        ArrayList<HashMap<String, Object>> hm = (ArrayList<HashMap<String, Object>>) this.jdbcTemplate.query(
            sql_to_query,
            new Object[]{new String(data_string), new Integer(pageSize),new Integer(offset)}, new RowMapper() {
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    HashMap<String, Object> row = new HashMap<String, Object>();
                    row.put("id",String.valueOf(rs.getInt("id")));
                    row.put("folio",rs.getString("folio"));
                    row.put("estatus",rs.getString("estatus"));
                    row.put("momento_creacion",String.valueOf(rs.getDate("momento_creacion")));
                    return row;
                }
            }
        );
        return hm;
    }




    //obtiene datos del pre-subensamble
    @Override
    public ArrayList<HashMap<String, String>> getInvOrdPreSuben_Datos(String id) {
        String sql_query = "SELECT inv_ord_subensamble.id,"
                            + "inv_ord_subensamble.folio,"
                            + "inv_ord_subensamble.estatus,"
                            + "inv_ord_subensamble.comentarios,"
                            + "inv_ord_subensamble.momento_creacion,"
                            + "inv_proceso.proceso_flujo_id "
                            + "FROM inv_ord_subensamble "
                            + "JOIN inv_proceso on inv_proceso.id=inv_ord_subensamble.proceso_id "
                            + "WHERE inv_ord_subensamble.id="+id;

        //System.out.println("getInvOrdPreSuben_Datos: "+sql_query);
        ArrayList<HashMap<String, String>> hm = (ArrayList<HashMap<String, String>>) this.jdbcTemplate.query(
            sql_query,
            new Object[]{}, new RowMapper() {
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    HashMap<String, String> row = new HashMap<String, String>();
                    row.put("id",String.valueOf(rs.getInt("id")));
                    row.put("folio",rs.getString("folio"));
                    row.put("estatus",String.valueOf(rs.getInt("estatus")));
                    row.put("proceso_flujo_id",String.valueOf(rs.getInt("proceso_flujo_id")));
                    row.put("comentarios",rs.getString("comentarios"));
                    row.put("fecha",String.valueOf(rs.getDate("momento_creacion")));
                    return row;
                }
            }
        );
        return hm;
    }




    //obtiene detalles del la orden pre-subensamble
    @Override
    public ArrayList<HashMap<String, String>> getInvDetalleOrdPreSuben(String id) {
        String sql_query = "select iop.id, "
                                + "iop.cantidad, "
                                + "inv_prod.sku  "
                            + "FROM inv_ord_subensamble_detalle as iop "
                            + "JOIN inv_prod on inv_prod.id=iop.inv_prod_id_subensamble  "
                            + "where iop.inv_ord_subensamble_id="+id;

        //System.out.println("getInvDetalleOrdPreSuben: "+sql_query);
        ArrayList<HashMap<String, String>> hm = (ArrayList<HashMap<String, String>>) this.jdbcTemplate.query(
            sql_query,
            new Object[]{}, new RowMapper() {
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    HashMap<String, String> row = new HashMap<String, String>();
                    row.put("id",String.valueOf(rs.getInt("id")));
                    row.put("sku",rs.getString("sku"));
                    row.put("cantidad",StringHelper.roundDouble(rs.getDouble("cantidad"), 2));
                    return row;
                }
            }
        );
        return hm;
    }




    @Override
    public ArrayList<HashMap<String, String>> getInvOrdPreSubenDatosProductos(String sku, Integer id_empresa) {
        String sql_to_query = "SELECT inv_prod.id, "
                                + "inv_prod.sku, "
                                + "inv_prod.descripcion, "
                                + "inv_prod_unidades.titulo "
                                + "FROM inv_prod  "
                            + "JOIN inv_prod_unidades on inv_prod_unidades.id=inv_prod.unidad_id "
                            + "WHERE inv_prod.sku ILIKE '"+sku+"'";

        //System.out.println(sql_to_query);
        ArrayList<HashMap<String, String>> hm_datos_productos = (ArrayList<HashMap<String, String>>) this.jdbcTemplate.query(
            sql_to_query,
            new Object[]{}, new RowMapper(){
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    HashMap<String, String> row = new HashMap<String, String>();
                    row.put("id",String.valueOf(rs.getInt("id")));
                    row.put("sku",rs.getString("sku"));
                    row.put("descripcion",rs.getString("descripcion"));
                    row.put("titulo",rs.getString("titulo"));
                    return row;
                }
            }
        );
        return hm_datos_productos;
    }



    @Override
    public ArrayList<HashMap<String, String>> getInvOrdPreSubenDatosComProd(String sku) {
        String sql_to_query = ""
                + "SELECT inv_prod.id, "
                    + "inv_prod.sku, "
                    + "inv_prod.descripcion, "
                    + "inv_prod_unidades.titulo as utitulo, "
                    + "tmp.cantidad "
                + "FROM inv_prod "
                + "JOIN ( "
                        + "SELECT inv_kit.producto_elemento_id, "
                            + "inv_kit.cantidad "
                        + "FROM ( SELECT id FROM inv_prod WHERE inv_prod.sku ilike '"+sku+"') as tmp1 "
                        + "JOIN inv_kit ON tmp1.id=inv_kit.producto_kit_id "
                + ") as tmp ON tmp.producto_elemento_id=inv_prod.id "
                + "JOIN inv_prod_unidades ON inv_prod_unidades.id=inv_prod.unidad_id";

        //System.out.println(sql_to_query);
        ArrayList<HashMap<String, String>> hm_datos_productos = (ArrayList<HashMap<String, String>>) this.jdbcTemplate.query(
            sql_to_query,
            new Object[]{}, new RowMapper(){
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    HashMap<String, String> row = new HashMap<String, String>();
                    row.put("id",String.valueOf(rs.getInt("id")));
                    row.put("sku",rs.getString("sku"));
                    row.put("descripcion",rs.getString("descripcion"));
                    row.put("utitulo",rs.getString("utitulo"));
                    row.put("cantidad",StringHelper.roundDouble(rs.getDouble("cantidad"),2));
                    return row;
                }
            }
        );
        return hm_datos_productos;
    }




    //Metodos para proceso InvOrdSuben
    @Override
    public ArrayList<HashMap<String, Object>> getInvOrdSubenGrid(String data_string, int offset, int pageSize, String orderBy, String asc) {
        String sql_busqueda = "select id from gral_bus_catalogos(?) as foo (id integer)";

	String sql_to_query = "select inv_ord_subensamble.id , inv_ord_subensamble.folio, inv_ord_subensamble.momento_creacion, inv_ord_subensamble.estatus from inv_ord_subensamble "
                            + "JOIN ("+sql_busqueda+") AS sbt ON sbt.id = inv_ord_subensamble.id "
                + "order by "+orderBy+" "+asc+" limit ? OFFSET ? ";

        //System.out.println("getInvOrdSubenGrid: "+sql_to_query+"    "+data_string);

        ArrayList<HashMap<String, Object>> hm = (ArrayList<HashMap<String, Object>>) this.jdbcTemplate.query(
            sql_to_query,
            new Object[]{new String(data_string), new Integer(pageSize),new Integer(offset)}, new RowMapper() {
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    HashMap<String, Object> row = new HashMap<String, Object>();
                    row.put("id",String.valueOf(rs.getInt("id")));
                    row.put("estatus",String.valueOf(rs.getInt("estatus")).equals("1") ? "Enterado" : (String.valueOf(rs.getInt("estatus")).equals("2") ? "En proceso" : (String.valueOf(rs.getInt("estatus")).equals("3") ? "Listo" : "Sin estatus" ))    );
                    row.put("folio",rs.getString("folio"));
                    row.put("momento_creacion",String.valueOf(rs.getDate("momento_creacion")));
                    return row;
                }
            }
        );
        return hm;
    }



    //obtiene datos del InvCom
    @Override
    public ArrayList<HashMap<String, String>> getInvOrdSuben_Datos(String id) {
        String sql_query = "select * from inv_ord_subensamble where id="+id;

        //System.out.println("getInvOrdSuben_Datos: "+sql_query);

        ArrayList<HashMap<String, String>> hm = (ArrayList<HashMap<String, String>>) this.jdbcTemplate.query(
            sql_query,
            new Object[]{}, new RowMapper() {
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {

                    HashMap<String, String> row = new HashMap<String, String>();
                    row.put("id",String.valueOf(rs.getInt("id")));
                    row.put("folio",rs.getString("folio"));
                    row.put("comentarios",rs.getString("comentarios"));
                    row.put("estatus",String.valueOf(rs.getInt("estatus")));
                    row.put("fecha",String.valueOf(rs.getDate("momento_creacion")));
                    //row.put("comoponente", getInvOrdPreSubenDatosComponentesPorProducto(String.valueOf(rs.getString("sku"))));

                    return row;
                }
            }
        );
        return hm;
    }



    //obtiene datos del InvCom
    @Override
    public ArrayList<HashMap<String, String>> getInvDetalleOrdSuben(String id) {
        String sql_query = "select iop.id, "
                + "iop.cantidad,"
                + "inv_prod.sku,"
                + "inv_prod.descripcion, "
                + "inv_prod_unidades.titulo as unidad  "
                + "FROM inv_ord_subensamble_detalle as iop "
                + "JOIN inv_prod ON inv_prod.id=iop.inv_prod_id_subensamble "
                + "JOIN inv_prod_unidades ON inv_prod_unidades.id=inv_prod.unidad_id "
                + "WHERE iop.inv_ord_subensamble_id="+id;

        //System.out.println("getInvDetalleOrdSuben: "+sql_query);

        ArrayList<HashMap<String, String>> hm = (ArrayList<HashMap<String, String>>) this.jdbcTemplate.query(
            sql_query,
            new Object[]{}, new RowMapper() {
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {

                    HashMap<String, String> row = new HashMap<String, String>();
                    row.put("id",String.valueOf(rs.getInt("id")));
                    row.put("sku",rs.getString("sku"));
                    row.put("descripcion",rs.getString("descripcion"));
                    row.put("unidad",rs.getString("unidad"));
                    row.put("cantidad",StringHelper.roundDouble(rs.getDouble("cantidad"), 2));

                    return row;
                }
            }
        );
        return hm;
    }



    @Override
    public ArrayList<HashMap<String, String>> getInvOrdPreSubenDatosComponentesPorProducto(String id) {

        String sql_to_query = "SELECT inv_prod.id,"
                    + "inv_prod.sku, "
                    + "inv_prod.descripcion, "
                    + "inv_prod_unidades.titulo as utitulo, "
                    + "cantidad "
                + "FROM ("
                        + "SELECT inv_kit.producto_elemento_id,"
                            + "sum(subt1.cantidad * inv_kit.cantidad) as cantidad "
                        + "FROM ("
                                + "SELECT sum(cantidad) as cantidad, "
                                    + "inv_prod_id_subensamble "
                                + "FROM inv_ord_subensamble_detalle "
                                + "WHERE inv_ord_subensamble_id="+id+" "
                                + "GROUP BY cantidad, inv_prod_id_subensamble "
                        + ") AS subt1 "
                        + "JOIN inv_kit on subt1.inv_prod_id_subensamble=inv_kit.producto_kit_id "
                        + "GROUP BY inv_kit.producto_elemento_id "
                + ") AS subt2 "
                + "JOIN inv_prod ON subt2.producto_elemento_id=inv_prod.id "
                + "JOIN inv_prod_unidades on inv_prod_unidades.id=inv_prod.unidad_id; ";

        //log.log(Level.INFO, "Ejecutando query de {0}", sql_to_query);
        //System.out.println(sql_to_query);
        ArrayList<HashMap<String, String>> hm_datos_productos = (ArrayList<HashMap<String, String>>) this.jdbcTemplate.query(
            sql_to_query,
            new Object[]{}, new RowMapper(){
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    HashMap<String, String> row = new HashMap<String, String>();
                    row.put("id",String.valueOf(rs.getInt("id")));
                    row.put("sku",rs.getString("sku"));
                    row.put("descripcion",rs.getString("descripcion"));
                    row.put("utitulo",rs.getString("utitulo"));
                    row.put("cantidad",StringHelper.roundDouble(rs.getDouble("cantidad"),2));
                    return row;
                }
            }
        );
        return hm_datos_productos;
    }



    //Catalogo de Unidades de Medida
    @Override
    public ArrayList<HashMap<String, Object>> getUnidades_PaginaGrid(String data_string, int offset, int pageSize, String orderBy, String asc) {
        String sql_busqueda = "select id from gral_bus_catalogos(?) as foo (id integer)";

	String sql_to_query =  "SELECT inv_prod_unidades.id "
                               +",inv_prod_unidades.titulo_abr"
                               +",inv_prod_unidades.titulo as descripcion "
                               +",inv_prod_unidades.decimales "
                               +"FROM inv_prod_unidades "
                               +"JOIN ("+sql_busqueda+") AS sbt ON sbt.id = inv_prod_unidades.id "
                                +"where inv_prod_unidades.borrado_logico =false "
                               +"order by "+orderBy+" "+asc+" limit ? OFFSET ?";
        //System.out.println("Busqueda GetPage: "+sql_to_query);
        ArrayList<HashMap<String, Object>> hm = (ArrayList<HashMap<String, Object>>) this.jdbcTemplate.query(
            sql_to_query,
            new Object[]{new String(data_string), new Integer(pageSize),new Integer(offset)}, new RowMapper() {
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    HashMap<String, Object> row = new HashMap<String, Object>();
                    row.put("id",rs.getInt("id"));
                    row.put("titulo_abr",rs.getString("titulo_abr"));
                    row.put("descripcion",rs.getString("descripcion"));
                    row.put("decimales",rs.getString("decimales"));

                    return row;
                }
            }
        );
        return hm;
    }


     @Override
    public ArrayList<HashMap<String, String>> getUnidades_Datos(Integer id_zona) {
        String sql_to_query = "SELECT  inv_prod_unidades.id,inv_prod_unidades.titulo_abr as unidad,inv_prod_unidades.titulo as descripcion,inv_prod_unidades.decimales FROM inv_prod_unidades WHERE id ="+id_zona;

        ArrayList<HashMap<String, String>> datos_unidades = (ArrayList<HashMap<String, String>>) this.jdbcTemplate.query(
            sql_to_query,
            new Object[]{}, new RowMapper(){
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    HashMap<String, String> row = new HashMap<String, String>();
                    row.put("id",String.valueOf(rs.getInt("id")));
                    row.put("unidad",rs.getString("unidad"));
                    row.put("decimales",rs.getString("decimales"));
                    row.put("descripcion",rs.getString("descripcion"));
                    return row;
                }
            }
        );
        return datos_unidades;
    }




    /*para obtener las formulas que contienen el producto seleccionado*/
     //para obtener las formulas que contienen el producto seleccionado
    @Override
    public ArrayList<HashMap<String, String>> getPro_FormulasProductos(String codigo,String descipcion, Integer id_empresa) {

        String sql_to_query = "select distinct pro_estruc.id,pro_estruc_det.inv_prod_id, tpm2.sku,  tpm2.descripcion, "
                + "pro_estruc_det.inv_prod_id_salida,tpm3.sku as sku_salida,  tpm3.descripcion as descripcion_salida from "
                + "pro_estruc join pro_estruc_det on pro_estruc_det.pro_estruc_id=pro_estruc.id join "
                + "(select id, sku, descripcion from inv_prod where sku ilike '"+codigo+"' and descripcion ilike '"+descipcion+"' and borrado_logico=false "
                + "and empresa_id="+id_empresa+") as tpm2 on tpm2.id=pro_estruc_det.inv_prod_id left join "
                + "("
                + "select inv_prod_id_salida, sku, descripcion from inv_prod join pro_estruc_det on inv_prod.id=pro_estruc_det.inv_prod_id_salida "
                + "where inv_prod.borrado_logico=false and inv_prod.empresa_id="+id_empresa+") "
                + "as tpm3 on tpm3.inv_prod_id_salida=pro_estruc_det.inv_prod_id_salida where pro_estruc.gral_emp_id="+id_empresa+""
                + "order by tpm2.sku, tpm3.sku";


        //System.out.println("Buscando facturas: "+sql_to_query);
        ArrayList<HashMap<String, String>> hm_facturas = (ArrayList<HashMap<String, String>>) this.jdbcTemplate.query(
            sql_to_query,
            new Object[]{}, new RowMapper(){
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    HashMap<String, String> row = new HashMap<String, String>();
                    row.put("id",String.valueOf(rs.getInt("id")));
                    row.put("inv_prod_id",String.valueOf(rs.getInt("inv_prod_id")));
                    row.put("sku",rs.getString("sku"));
                    row.put("descripcion",rs.getString("descripcion"));
                    row.put("inv_prod_id_salida",String.valueOf(rs.getInt("inv_prod_id_salida")));
                    row.put("sku_salida",rs.getString("sku_salida"));
                    row.put("descripcion_salida",rs.getString("descripcion_salida"));
                    return row;
                }
            }
        );
        return hm_facturas;
    }


    /*trae todas las peordenes de produccion de la empresa*/
    @Override
    public ArrayList<HashMap<String, Object>> getProPreorden_PaginaGrid(String data_string, int offset, int pageSize, String orderBy, String asc) {
        String sql_busqueda = "SELECT id FROM gral_bus_catalogos('"+data_string+"') AS foo (id integer)";

	String sql_to_query = "select pro_preorden_prod.id, pro_preorden_prod.folio, pro_preorden_prod.pro_orden_tipos_id,pro_preorden_prod.estatus , pro_preorden_prod.momento_creacion,pro_orden_tipos.titulo as accesor_tipo  from pro_preorden_prod join "
                + " ("+sql_busqueda+") as subt on subt.id=pro_preorden_prod.id join pro_orden_tipos on pro_orden_tipos.id=pro_preorden_prod.pro_orden_tipos_id "
                        +"ORDER BY "+orderBy+" "+asc+" LIMIT ? OFFSET ?";

        //System.out.println("Busqueda GetPage: "+sql_to_query);

        //log.log(Level.INFO, "Ejecutando query de {0}", sql_to_query);
        ArrayList<HashMap<String, Object>> hm = (ArrayList<HashMap<String, Object>>) this.jdbcTemplate.query(
            sql_to_query,
            new Object[]{new Integer(pageSize),new Integer(offset)}, new RowMapper() {
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    HashMap<String, Object> row = new HashMap<String, Object>();
                    row.put("id",rs.getString("id"));
                    row.put("accesor_tipo",rs.getString("accesor_tipo"));
                    row.put("folio",rs.getString("folio"));
                    row.put("momento_creacion",String.valueOf(rs.getString("momento_creacion")).substring(0, 10));
                    row.put("confirmado",String.valueOf(rs.getInt("estatus")).equals("1") ? "Sin Confirmar" : String.valueOf(rs.getInt("estatus")).equals("2") ? "Confirmado" : "Cancelado" );

                    return row;
                }
            }
        );
        return hm;
    }

    /*trae todos los tipos de ordenes de produccion*/
    @Override
    public ArrayList<HashMap<String, String>> getProOrdenTipos(Integer empresa_id){
        String sql_to_query = "select id, titulo from pro_orden_tipos where borrado_logico=false and gral_emp_id="+empresa_id;

        ArrayList<HashMap<String, String>> datos_unidades = (ArrayList<HashMap<String, String>>) this.jdbcTemplate.query(
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
        return datos_unidades;
    }

    @Override
    public ArrayList<HashMap<String, String>> getProPreorden_Datos(Integer id) {
        String sql_to_query = "select * from pro_preorden_prod where id="+ id + ";";

        //log.log(Level.INFO, "Ejecutando query de {0}", sql_to_query);
        ArrayList<HashMap<String, String>> hm_datos_entrada = (ArrayList<HashMap<String, String>>) this.jdbcTemplate.query(
            sql_to_query,
            new Object[]{}, new RowMapper(){
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    HashMap<String, String> row = new HashMap<String, String>();
                    row.put("id",String.valueOf(rs.getInt("id")));
                    row.put("pro_orden_tipos_id",String.valueOf(rs.getInt("pro_orden_tipos_id")));
                    row.put("momento_creacion",String.valueOf(rs.getString("momento_creacion")).substring(0,19));
                    row.put("folio",rs.getString("folio"));
                    row.put("observaciones",rs.getString("observaciones"));
                    row.put("confirmado",String.valueOf(rs.getBoolean("confirmado")));

                    return row;
                }
            }
        );
        return hm_datos_entrada;
    }

    @Override
    public ArrayList<HashMap<String, String>> getProPreorden_Detalle(Integer id) {
        /*
        String sql_to_query = "select podt.id, podt.inv_prod_presentaciones_id,podt.inv_prod_id, inv_prod.sku,inv_prod_presentaciones.titulo as presentacion, "
                + "inv_prod.descripcion, podt.cantidad, podt.poc_pedidos_id  from pro_preorden_prod_det as podt join inv_prod on "
                + "inv_prod.id=podt.inv_prod_id join inv_prod_presentaciones on inv_prod_presentaciones.id=podt.inv_prod_presentaciones_id "
                + "where pro_preorden_prod_id="+id;
        */
        String sql_to_query = "select podt.id, podt.inv_prod_presentaciones_id,podt.inv_prod_id, inv_prod.sku,"
                + "inv_prod_presentaciones.titulo as presentacion,inv_prod.descripcion, podt.cantidad, podt.poc_pedidos_id, "
                + "inv_prod.unidad_id, un.titulo as unidad, inv_prod.densidad from pro_preorden_prod_det as podt "
                + "join inv_prod on inv_prod.id=podt.inv_prod_id join inv_prod_unidades as un on un.id=inv_prod.unidad_id "
                + "join inv_prod_presentaciones on inv_prod_presentaciones.id=podt.inv_prod_presentaciones_id "
                + "where pro_preorden_prod_id="+id;

        //log.log(Level.INFO, "Ejecutando query de {0}", sql_to_query);
        ArrayList<HashMap<String, String>> hm_datos_entrada = (ArrayList<HashMap<String, String>>) this.jdbcTemplate.query(
            sql_to_query,
            new Object[]{}, new RowMapper(){
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    HashMap<String, String> row = new HashMap<String, String>();
                    row.put("id",String.valueOf(rs.getInt("id")));
                    row.put("inv_prod_presentaciones_id",String.valueOf(rs.getInt("inv_prod_presentaciones_id")));
                    row.put("inv_prod_id",String.valueOf(rs.getInt("inv_prod_id")));
                    row.put("sku",rs.getString("sku"));
                    row.put("presentacion",rs.getString("presentacion"));
                    row.put("unidad",rs.getString("unidad"));
                    row.put("unidad_id",String.valueOf(rs.getInt("unidad_id")));
                    row.put("descripcion",rs.getString("descripcion"));
                    row.put("cantidad",StringHelper.roundDouble(String.valueOf(rs.getDouble("cantidad")),4));
                    row.put("poc_pedidos_id",String.valueOf(rs.getInt("poc_pedidos_id")));
                    row.put("densidad",StringHelper.roundDouble(String.valueOf(rs.getDouble("densidad")),4));

                    return row;
                }
            }
        );
        return hm_datos_entrada;
    }


    @Override
    public ArrayList<HashMap<String, String>> getBuscadorPedidos(String folio,String proveedor,Integer id_empresa) {
        String where = "";


        //String sql_to_query = "select poc_pedidos.id,poc_pedidos.cxc_clie_id, poc_pedidos.folio, tmp_cli.numero_control, tmp_cli.rfc, "
        //        + "tmp_cli.razon_social, poc_pedidos.momento_creacion  from poc_pedidos join (select erp_proceso.id from erp_proceso where empresa_id="+id_empresa+" and proceso_flujo_id=4) "
        //        + "as tmp_proceso on tmp_proceso.id=poc_pedidos.proceso_id join "
        //        + "(select id, razon_social, numero_control, rfc from cxc_clie where razon_social ilike '"+proveedor+"' and borrado_logico=false and "
        //        + "empresa_id="+id_empresa+") as tmp_cli on tmp_cli.id=poc_pedidos.cxc_clie_id where folio ilike '"+folio+"'  limit 50;";

        String sql_to_query = "select distinct poc_pedidos.id,poc_pedidos.cxc_clie_id, poc_pedidos.folio, tmp_cli.numero_control, tmp_cli.rfc, "
                + "tmp_cli.razon_social, poc_pedidos.momento_creacion, poc_pedidos_detalle.backorder from poc_pedidos join "
                + "(select erp_proceso.id from erp_proceso where empresa_id="+id_empresa+" and proceso_flujo_id=2) as tmp_proceso on "
                + "(tmp_proceso.id=poc_pedidos.proceso_id and poc_pedidos.cancelado=false) join (select id, razon_social, numero_control, "
                + "rfc from cxc_clie where razon_social ilike '"+proveedor+"' and borrado_logico=false and empresa_id=1) as tmp_cli on "
                + "tmp_cli.id=poc_pedidos.cxc_clie_id join poc_pedidos_detalle on (poc_pedidos_detalle.poc_pedido_id=poc_pedidos.id "
                + "AND poc_pedidos_detalle.backorder is true) where poc_pedidos.folio ilike '"+folio+"'  limit 50;";


        //System.out.println("Ejecutando query de: "+ sql_to_query);

        ArrayList<HashMap<String, String>> hm_datos_productos = (ArrayList<HashMap<String, String>>) this.jdbcTemplate.query(
            sql_to_query,
            new Object[]{}, new RowMapper(){
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    HashMap<String, String> row = new HashMap<String, String>();
                    row.put("id",String.valueOf(rs.getInt("id")));
                    row.put("cxc_clie_id",String.valueOf(rs.getInt("cxc_clie_id")));
                    row.put("folio",rs.getString("folio"));
                    row.put("numero_control",rs.getString("numero_control"));
                    row.put("rfc",rs.getString("rfc"));
                    row.put("razon_social",rs.getString("razon_social"));
                    row.put("momento_creacion",String.valueOf(rs.getString("momento_creacion")).substring(0, 19));
                    return row;
                }
            }
        );
        return hm_datos_productos;
    }


    private Double select_existencias_por_producto(String inv_prod_id, Integer gral_user_id) {
        Double valor_retorno=0.0;
        String sql_to_query = "select inv_calculo_existencia_producto from inv_calculo_existencia_producto(2, true, "+inv_prod_id+", "+gral_user_id+");";
        //log.log(Level.INFO, "Ejecutando query de {0}", sql_to_query);
        //System.out.println("Ejacutando Guardar:"+sql_to_query);
        //int update = this.getJdbcTemplate().queryForInt(sql_to_query);

        Map<String, Object> retorno = this.getJdbcTemplate().queryForMap(sql_to_query);

       valor_retorno = Double.parseDouble(retorno.get("inv_calculo_existencia_producto").toString());

        return valor_retorno;
    }

    @Override
    public ArrayList<HashMap<String, String>> getProductosPedidoSeleccionado(String id, Integer id_usuario) {
        String where = "";

        final Integer usuario_id=id_usuario;

        //String sql_to_query = "select cantidad, inv_prod_id,inv_prod.sku as codigo, inv_prod.descripcion,inv_prod_presentaciones.titulo as presentacion, "
        //        + "presentacion_id, precio_unitario, gral_imp_id, valor_imp, gral_imptos.descripcion as impuesto from poc_pedidos_detalle join  "
        //        + "inv_prod on inv_prod.id=poc_pedidos_detalle.inv_prod_id left join gral_imptos on gral_imptos.id=poc_pedidos_detalle.gral_imp_id "
        //        + "join inv_prod_presentaciones on inv_prod_presentaciones.id=poc_pedidos_detalle.presentacion_id "
        //        + "where poc_pedidos_detalle.poc_pedido_id="+id;

        String sql_to_query = "select poc_ped_bo.cantidad, poc_ped_bo.inv_prod_id,inv_prod.sku as codigo, inv_prod.descripcion,"
                + "inv_prod_presentaciones.titulo as presentacion, presentacion_id, precio_unitario, gral_imp_id, valor_imp, "
                + "gral_imptos.descripcion as impuesto, poc_ped_bo.id as bo_id, inv_prod.unidad_id, un.titulo as unidad, inv_prod.densidad "
                + "from poc_pedidos_detalle join poc_ped_bo on (poc_ped_bo.poc_ped_detalle_id=poc_pedidos_detalle.id AND poc_pedidos_detalle.poc_pedido_id="+id+" "
                + "AND poc_pedidos_detalle.backorder=true AND poc_ped_bo.estatus=0) join inv_prod on inv_prod.id=poc_pedidos_detalle.inv_prod_id "
                + "join inv_prod_unidades as un on un.id=inv_prod.unidad_id "
                + "left join gral_imptos on gral_imptos.id=poc_pedidos_detalle.gral_imp_id "
                + "join inv_prod_presentaciones on inv_prod_presentaciones.id=poc_pedidos_detalle.presentacion_id";

        //System.out.println("getProductosPedido: "+ sql_to_query);

        ArrayList<HashMap<String, String>> hm_datos_productos = (ArrayList<HashMap<String, String>>) this.jdbcTemplate.query(
            sql_to_query,
            new Object[]{}, new RowMapper(){
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    HashMap<String, String> row = new HashMap<String, String>();
                    //if(select_existencias_por_producto(String.valueOf(rs.getInt("inv_prod_id")), usuario_id) <= rs.getDouble("cantidad") ){

                        row.put("presentacion_id",String.valueOf(rs.getInt("presentacion_id")));
                        row.put("inv_prod_id",String.valueOf(rs.getInt("inv_prod_id")));
                        row.put("gral_imp_id",String.valueOf(rs.getInt("gral_imp_id")));
                        row.put("codigo",rs.getString("codigo"));
                        row.put("descripcion",rs.getString("descripcion"));
                        row.put("presentacion",rs.getString("presentacion"));
                        row.put("impuesto",rs.getString("impuesto"));
                        row.put("unidad",rs.getString("unidad"));
                        row.put("unidad_id",String.valueOf(rs.getInt("unidad_id")));
                        row.put("cantidad",StringHelper.roundDouble(String.valueOf(rs.getDouble("cantidad")),4));
                        row.put("densidad",StringHelper.roundDouble(String.valueOf(rs.getDouble("densidad")),4));

                        row.put("precio_unitario",String.valueOf(rs.getDouble("precio_unitario")));
                        row.put("valor_imp",String.valueOf(rs.getDouble("valor_imp")));

                        getPro_update_pro_backorder(String.valueOf(rs.getInt("bo_id")), "1");
                    //}
                    return row;
                }
            }
        );
        return hm_datos_productos;
    }





    /*Orden de produccion*/
    /*trae todas las ordenes de produccion de la empresa*/
    @Override
    public ArrayList<HashMap<String, Object>> getProOrden_PaginaGrid(String data_string, int offset, int pageSize, String orderBy, String asc) {
        String sql_busqueda = "SELECT id FROM gral_bus_catalogos('"+data_string+"') AS foo (id integer)";

	String sql_to_query = "select pro_orden_prod.id, pro_orden_prod.folio,pro_orden_prod.lote, pro_orden_prod.pro_orden_tipos_id, "
                + "pro_orden_prod.fecha_elavorar,pro_orden_tipos.titulo as accesor_tipo, pro_proceso_flujo.titulo as proceso, "
                + "(select inv_prod.sku from pro_orden_prod_det join inv_prod on pro_orden_prod_det.inv_prod_id=inv_prod.id "
                + "where pro_orden_prod_det.pro_orden_prod_id=pro_orden_prod.id limit 1) as sku "
                + "from pro_orden_prod join "
                + " ("+sql_busqueda+") as subt on subt.id=pro_orden_prod.id join pro_orden_tipos on pro_orden_tipos.id=pro_orden_prod.pro_orden_tipos_id "
                + "join pro_proceso on pro_proceso.id=pro_orden_prod.pro_proceso_id "
                + "join pro_proceso_flujo on pro_proceso_flujo.id=pro_proceso.pro_proceso_flujo_id "
                        +"ORDER BY "+orderBy+" "+asc+" LIMIT ? OFFSET ?";

        //System.out.println("Busqueda GetPage: "+sql_to_query);

        //log.log(Level.INFO, "Ejecutando query de {0}", sql_to_query);
        ArrayList<HashMap<String, Object>> hm = (ArrayList<HashMap<String, Object>>) this.jdbcTemplate.query(
            sql_to_query,
            new Object[]{new Integer(pageSize),new Integer(offset)}, new RowMapper() {
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    HashMap<String, Object> row = new HashMap<String, Object>();
                    row.put("id",rs.getString("id"));
                    row.put("accesor_tipo",rs.getString("accesor_tipo"));
                    row.put("folio",rs.getString("folio"));
                    row.put("fecha_elavorar",String.valueOf(rs.getString("fecha_elavorar")));
                    row.put("lote",rs.getString("lote"));
                    row.put("proceso",rs.getString("proceso"));
                    row.put("sku",rs.getString("sku"));
                    return row;
                }
            }
        );
        return hm;
    }


    @Override
    public ArrayList<HashMap<String, String>> getProOrden_Datos(Integer id) {
        String sql_to_query = ""
                + "select "
                    + "pro_orden_prod.*, "
                    + "pro_proceso.pro_proceso_flujo_id, "
                    + "pro_proceso_flujo.titulo as flujo,"
                    + "pro_orden_prod.costo_ultimo  "
                + "from pro_orden_prod "
                + "join pro_proceso on pro_proceso.id=pro_orden_prod.pro_proceso_id "
                + "join pro_proceso_flujo on pro_proceso_flujo.id=pro_proceso.pro_proceso_flujo_id "
                + "where pro_orden_prod.id="+id+";";

        //log.log(Level.INFO, "Ejecutando query de {0}", sql_to_query);
        ArrayList<HashMap<String, String>> hm_datos_entrada = (ArrayList<HashMap<String, String>>) this.jdbcTemplate.query(
            sql_to_query,
            new Object[]{}, new RowMapper(){
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    HashMap<String, String> row = new HashMap<String, String>();
                    row.put("id",String.valueOf(rs.getInt("id")));
                    row.put("pro_proceso_id",String.valueOf(rs.getInt("pro_proceso_id")));
                    row.put("pro_orden_tipos_id",String.valueOf(rs.getInt("pro_orden_tipos_id")));
                    row.put("fecha_elavorar",StringHelper.isNullString(String.valueOf(rs.getString("fecha_elavorar"))));
                    row.put("folio",rs.getString("folio"));
                    row.put("lote",rs.getString("lote"));
                    row.put("observaciones",rs.getString("observaciones"));
                    row.put("flujo",rs.getString("flujo"));
                    row.put("pro_proceso_flujo_id",String.valueOf(rs.getInt("pro_proceso_flujo_id")));
                    row.put("pro_estruc_id",String.valueOf(rs.getInt("pro_estruc_id")));
                    row.put("costo_ultimo",StringHelper.roundDouble(String.valueOf(rs.getDouble("costo_ultimo")),4));
                    row.put("solicitante",rs.getString("solicitante"));
                    row.put("vendedor",rs.getString("vendedor"));
                    
                    row.put("status_calidad",String.valueOf(rs.getInt("status_calidad")));
                    row.put("comentarios_calidad",rs.getString("comentarios_calidad"));
                    
                    return row;
                }
            }
        );
        return hm_datos_entrada;
    }

    @Override
    public ArrayList<HashMap<String, String>> getProOrden_Detalle(Integer id) {

        String sql_to_query = ""
                + "select "
                    + "tmp_det_orden.id,"
                    + "tmp_det_orden.pro_orden_prod_id, "
                    + "tmp_det_orden.cantidad, "
                    + "tmp_det_orden.num_lote, "
                    + "tmp_det_orden.pro_subprocesos_id, "
                    + "pro_subprocesos.titulo as subproceso, "
                    + "tmp_det_orden.inv_prod_id,"
                    + "inv_prod.sku, "
                    + "inv_prod.descripcion, "
                    + "inv_prod.tipo_de_producto_id AS tipo_prod_id, "
                    + "tmp_det_orden.gral_empleados_id, "
                    + "gral_empleados.nombre_pila||' '||gral_empleados.apellido_paterno||' '||gral_empleados.apellido_materno as empleado, "
                    + "tmp_det_orden.pro_equipos_id, "
                    + "pro_equipos.titulo as equipo, "
                    + "tmp_det_orden.pro_equipos_adic_id, "
                    + "pro_equipos_adic.titulo as eq_adicional, "
                    + "inv_unid.titulo as unidad, "
                    + "inv_prod.unidad_id, "
                    + "inv_prod.densidad "
                + "from "
                + "(select id, pro_orden_prod_id,num_lote, cantidad,pro_subprocesos_id, inv_prod_id,gral_empleados_id, pro_equipos_id, "
                + "pro_equipos_adic_id  from pro_orden_prod_det where pro_orden_prod_id="+id+" "
                + ") as tmp_det_orden  "
                + "left join pro_subprocesos on  pro_subprocesos.id=tmp_det_orden.pro_subprocesos_id "
                + "left join inv_prod on inv_prod.id=tmp_det_orden.inv_prod_id "
                + "left JOIN inv_prod_unidades as inv_unid ON inv_unid.id=inv_prod.unidad_id "
                + "left join gral_empleados on gral_empleados.id=tmp_det_orden.gral_empleados_id "
                + "left join pro_equipos on pro_equipos.id=tmp_det_orden.pro_equipos_id "
                + "left join pro_equipos_adic on pro_equipos_adic.id=tmp_det_orden.pro_equipos_adic_id";

        //System.out.println("Ejecutando query de: "+ sql_to_query);

        ArrayList<HashMap<String, String>> hm_datos_entrada = (ArrayList<HashMap<String, String>>) this.jdbcTemplate.query(
            sql_to_query,
            new Object[]{}, new RowMapper(){
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    HashMap<String, String> row = new HashMap<String, String>();
                    row.put("id",String.valueOf(rs.getInt("id")));
                    row.put("pro_orden_prod_id",String.valueOf(rs.getInt("pro_orden_prod_id")));
                    row.put("num_lote",rs.getString("num_lote"));
                    row.put("pro_subprocesos_id",String.valueOf(rs.getInt("pro_subprocesos_id")));
                    row.put("subproceso",rs.getString("subproceso"));
                    row.put("inv_prod_id",String.valueOf(rs.getInt("inv_prod_id")));
                    row.put("tipo_prod_id",String.valueOf(rs.getInt("tipo_prod_id")));
                    row.put("sku",rs.getString("sku"));
                    row.put("descripcion",rs.getString("descripcion"));
                    row.put("gral_empleados_id",StringHelper.isNullString(String.valueOf(rs.getInt("gral_empleados_id"))));
                    row.put("empleado", StringHelper.isNullString(String.valueOf(rs.getString("empleado"))));
                    row.put("pro_equipos_id",StringHelper.isNullString(String.valueOf(rs.getInt("pro_equipos_id"))));
                    row.put("equipo",StringHelper.isNullString(String.valueOf(rs.getString("equipo"))));
                    row.put("pro_equipos_adic_id",StringHelper.isNullString(String.valueOf(rs.getInt("pro_equipos_adic_id"))));
                    row.put("eq_adicional",StringHelper.isNullString(String.valueOf(rs.getString("eq_adicional"))));
                    row.put("cantidad",StringHelper.roundDouble(String.valueOf(rs.getDouble("cantidad")),4));
                    row.put("densidad",StringHelper.roundDouble(String.valueOf(rs.getDouble("densidad")),4));
                    row.put("unidad_id",String.valueOf(rs.getInt("unidad_id")));
                    row.put("unidad",rs.getString("unidad"));

                    return row;
                }
            }
        );
        return hm_datos_entrada;
    }

    @Override
    public ArrayList<HashMap<String, String>> getProOrden_EspecificacionesDetalle(Integer id) {

        String sql_to_query = ""
            + "select "
                + "tmp_det_orden.id,"
                + "tmp_det_orden.pro_orden_prod_id, "
                + "tmp_det_orden.cantidad, "
                + "tmp_det_orden.num_lote,"
                + "tmp_det_orden.pro_subprocesos_id,"
                + "pro_subprocesos.titulo as subproceso, "
                + "tmp_det_orden.inv_prod_id,"
                //+ "inv_prod.sku, "
                //+ "inv_prod.descripcion, "
                + "tmp_det_orden.prod_codigo as sku, "
                + "tmp_det_orden.prod_titulo as descripcion, "
                + "inv_prod.tipo_de_producto_id AS tipo_prod_id, "
                + "esp.id as id_esp, "
                + "(CASE WHEN esp.fineza_inicial is null THEN -1 ELSE esp.fineza_inicial END) as fineza_inicial, "
                + "(CASE WHEN esp.viscosidads_inicial is null THEN -1 ELSE esp.viscosidads_inicial END) as viscosidads_inicial, "
                + "(CASE WHEN esp.viscosidadku_inicial is null THEN -1 ELSE esp.viscosidadku_inicial END) as viscosidadku_inicial, "
                + "(CASE WHEN esp.viscosidadcps_inicial is null THEN -1 ELSE esp.viscosidadcps_inicial END) as viscosidadcps_inicial, "
                + "(CASE WHEN esp.densidad_inicial is null THEN -1 ELSE esp.densidad_inicial END) as densidad_inicial,"
                + "(CASE WHEN esp.volatiles_inicial is null THEN -1 ELSE esp.volatiles_inicial END) as volatiles_inicial, "
                + "(CASE WHEN esp.hidrogeno_inicial is null THEN -1 ELSE esp.hidrogeno_inicial END) as hidrogeno_inicial, "
                + "(CASE WHEN esp.cubriente_inicial is null THEN -1 ELSE esp.cubriente_inicial END) as cubriente_inicial, "
                + "(CASE WHEN esp.tono_inicial is null THEN -1 ELSE esp.tono_inicial END) as tono_inicial, "
                + "(CASE WHEN esp.brillo_inicial is null THEN -1 ELSE esp.brillo_inicial END) as brillo_inicial,"
                + "(CASE WHEN esp.dureza_inicial is null THEN 'N.A.' ELSE esp.dureza_inicial END) as dureza_inicial, "
                + "(CASE WHEN esp.adherencia_inicial is null THEN -1 ELSE esp.adherencia_inicial END) as adherencia_inicial, "
                + "esp.pro_instrumentos_fineza,"
                + "esp.pro_instrumentos_viscosidad1,"
                + "esp.pro_instrumentos_viscosidad2,"
                + "esp.pro_instrumentos_viscosidad3,"
                + "esp.pro_instrumentos_densidad,"
                + "esp.pro_instrumentos_volatil,"
                + "esp.pro_instrumentos_cubriente,"
                + "esp.pro_instrumentos_tono,"
                + "esp.pro_instrumentos_brillo,"
                + "esp.pro_instrumentos_dureza,"
                + "esp.pro_instrumentos_adherencia,"
                + "esp.pro_instrumentos_hidrogeno,"
                + "inv_unid.titulo as unidad, "
                + "tmp_det_orden.unidad_id, "
                + "tmp_det_orden.densidad, "
                + "("
                    + "select count(inv_osal.id) as cantidad from ( select inv_osal_id from pro_ordenprod_invosal where pro_orden_prod_id="+id+" ) as tmp_ord_prod "
                    + "join inv_osal on inv_osal.id=tmp_ord_prod.inv_osal_id "
                    + "where inv_osal.estatus=1 OR inv_osal.estatus=2 "
                + ") as cantidad_salida "
            + "from ("
                + "select id, "
                    + "pro_orden_prod_id,"
                    + "num_lote, "
                    + "cantidad,"
                    + "pro_subprocesos_id, "
                    + "inv_prod_id,"
                    + "gral_empleados_id, "
                    + "pro_equipos_id, "
                    + "pro_equipos_adic_id, "
                    + "unidad_id,"
                    + "densidad,"
                    + "prod_codigo, "
                    + "prod_titulo "
                + "from pro_orden_prod_det "
                + "where pro_orden_prod_id="+id+" "
            + ") as tmp_det_orden "
            + "left join pro_subprocesos on  pro_subprocesos.id=tmp_det_orden.pro_subprocesos_id "
            + "left join inv_prod on inv_prod.id=tmp_det_orden.inv_prod_id "
            + "left JOIN inv_prod_unidades as inv_unid ON inv_unid.id=tmp_det_orden.unidad_id "
            + "left join pro_orden_prod_subp_esp as esp on esp.pro_orden_prod_det_id=tmp_det_orden.id;";
        
        System.out.println("getEspecificacionesDetalle: "+ sql_to_query);
        
        ArrayList<HashMap<String, String>> hm_datos_entrada = (ArrayList<HashMap<String, String>>) this.jdbcTemplate.query(
            sql_to_query,
            new Object[]{}, new RowMapper(){
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    HashMap<String, String> row = new HashMap<String, String>();

                    row.put("id",String.valueOf(rs.getInt("id")));
                    row.put("cantidad_salida",String.valueOf(rs.getInt("cantidad_salida")));
                    row.put("pro_orden_prod_id",String.valueOf(rs.getInt("pro_orden_prod_id")));
                    row.put("cantidad",String.valueOf(rs.getDouble("cantidad")));
                    row.put("num_lote",rs.getString("num_lote"));
                    row.put("pro_subprocesos_id",String.valueOf(rs.getInt("pro_subprocesos_id")));
                    row.put("subproceso",rs.getString("subproceso"));
                    row.put("inv_prod_id",String.valueOf(rs.getInt("inv_prod_id")));
                    row.put("sku",rs.getString("sku"));
                    row.put("descripcion",rs.getString("descripcion"));
                    row.put("tipo_prod_id",String.valueOf(rs.getInt("tipo_prod_id")));
                    
                    row.put("id_esp",String.valueOf(rs.getInt("id_esp")));
                    row.put("fineza_inicial",String.valueOf(rs.getInt("fineza_inicial")));
                    row.put("viscosidads_inicial",String.valueOf(rs.getInt("viscosidads_inicial")));
                    row.put("viscosidadku_inicial",String.valueOf(rs.getDouble("viscosidadku_inicial")));
                    row.put("viscosidadcps_inicial",String.valueOf(rs.getInt("viscosidadcps_inicial")));
                    row.put("densidad_inicial",String.valueOf(rs.getDouble("densidad_inicial")));
                    row.put("volatiles_inicial",String.valueOf(rs.getDouble("volatiles_inicial")));
                    row.put("hidrogeno_inicial",String.valueOf(rs.getDouble("hidrogeno_inicial")));
                    row.put("cubriente_inicial",String.valueOf(rs.getDouble("cubriente_inicial")));
                    row.put("tono_inicial",String.valueOf(rs.getDouble("tono_inicial")));
                    row.put("brillo_inicial",String.valueOf(rs.getDouble("brillo_inicial")));
                    row.put("dureza_inicial",rs.getString("dureza_inicial"));
                    row.put("adherencia_inicial",String.valueOf(rs.getDouble("adherencia_inicial")));

                    /*
                    row.put("fineza_final",String.valueOf(rs.getInt("fineza_final")));
                    row.put("viscosidads_final",String.valueOf(rs.getInt("viscosidads_final")));
                    row.put("viscosidadku_final",String.valueOf(rs.getDouble("viscosidadku_final")));
                    row.put("viscosidadcps_final",String.valueOf(rs.getInt("viscosidadcps_final")));
                    row.put("densidad_final",String.valueOf(rs.getDouble("densidad_final")));
                    row.put("volatiles_final",String.valueOf(rs.getDouble("volatiles_final")));
                    row.put("hidrogeno_final",String.valueOf(rs.getDouble("hidrogeno_final")));
                    row.put("cubriente_final",String.valueOf(rs.getDouble("cubriente_final")));
                    row.put("tono_final",String.valueOf(rs.getDouble("tono_final")));
                    row.put("brillo_final",String.valueOf(rs.getDouble("brillo_final")));
                    row.put("dureza_final",rs.getString("dureza_final"));
                    row.put("adherencia_final",String.valueOf(rs.getDouble("adherencia_final")));
                    */
                    row.put("pro_instrumentos_fineza",String.valueOf(rs.getInt("pro_instrumentos_fineza")));
                    row.put("pro_instrumentos_viscosidad1",String.valueOf(rs.getInt("pro_instrumentos_viscosidad1")));
                    row.put("pro_instrumentos_viscosidad2",String.valueOf(rs.getInt("pro_instrumentos_viscosidad2")));
                    row.put("pro_instrumentos_viscosidad3",String.valueOf(rs.getInt("pro_instrumentos_viscosidad3")));
                    row.put("pro_instrumentos_densidad",String.valueOf(rs.getInt("pro_instrumentos_densidad")));
                    row.put("pro_instrumentos_volatil",String.valueOf(rs.getInt("pro_instrumentos_volatil")));
                    row.put("pro_instrumentos_cubriente",String.valueOf(rs.getInt("pro_instrumentos_cubriente")));
                    row.put("pro_instrumentos_tono",String.valueOf(rs.getInt("pro_instrumentos_tono")));
                    row.put("pro_instrumentos_brillo",String.valueOf(rs.getInt("pro_instrumentos_brillo")));
                    row.put("pro_instrumentos_dureza",String.valueOf(rs.getInt("pro_instrumentos_dureza")));
                    row.put("pro_instrumentos_adherencia",String.valueOf(rs.getInt("pro_instrumentos_adherencia")));
                    row.put("pro_instrumentos_hidrogeno",String.valueOf(rs.getInt("pro_instrumentos_hidrogeno")));


                    row.put("cantidad",StringHelper.roundDouble(String.valueOf(rs.getDouble("cantidad")),4));
                    row.put("densidad",StringHelper.roundDouble(String.valueOf(rs.getDouble("densidad")),4));
                    row.put("unidad_id",String.valueOf(rs.getInt("unidad_id")));
                    row.put("unidad",rs.getString("unidad"));

                    return row;
                }
            }
        );
        return hm_datos_entrada;
    }

    @Override
    public String getExistenciaAlmacenesPorProducts(String almacen_id, String id_producto, String id_usuario) {

        String sql_to_query = "SELECT inv_calculo_existencia_producto AS existencia FROM "
                + "inv_calculo_existencia_producto(1,false, "+id_producto+","+id_usuario+", "+almacen_id+")";
        String valor_retorno="0";
        //System.out.println(sql_to_query);
        valor_retorno = StringHelper.roundDouble((String )this.getJdbcTemplate().queryForObject(sql_to_query, new Object[]{}, String.class) , 4);
        return valor_retorno;
    }

    /*rae los productos de los que esta compuesto un roducto teminado*/
    @Override
    public ArrayList<HashMap<String, String>> getProElementosProducto(String id_producto, String id_orden, String id_subproceso) {
        /*
        String sql_to_query = "selecT * from pro_get_detalle_orden_produccion("+id_producto+","+id_orden+","+id_subproceso+", 0)  as "
                + "foo(id integer, inv_prod_id integer, sku character varying,descripcion character varying, requiere_numero_lote boolean "
                + ",cantidad_adicional double precision,id_reg_det integer, cantidad double precision,elemento integer, "
                + "lote character varying, inv_osal_id integer) order by elemento;";
        */
        
        String sql_to_query = "selecT * from pro_get_detalle_orden_produccionv2("+id_producto+","+id_orden+","+id_subproceso+", 0)  as "
                + "foo("
                    + "id integer, "
                    + "inv_prod_id integer, "
                    + "sku character varying,"
                    + "descripcion character varying, "
                    + "requiere_numero_lote boolean "
                    + ",cantidad_adicional double precision,"
                    + "id_reg_det integer, "
                    + "cantidad double precision,"
                    + "elemento integer, "
                    + "lote character varying, "
                    + "inv_osal_id integer, "
                    + "inv_alm_id integer, "
                    + "gral_suc_id integer, "
                    + "agregado boolean,"
                    + "cantidad_usada double precision, "
                    + "guardado boolean"
                + ") order by elemento;";

        //and tmp_salida.cantidad_tmp=tmp_det.cantidad // se quito, por que no mostraba los lotes
        System.out.println("ProElementos: "+ sql_to_query);

        ArrayList<HashMap<String, String>> hm_datos_entrada = (ArrayList<HashMap<String, String>>) this.jdbcTemplate.query(
            sql_to_query,
            new Object[]{}, new RowMapper(){
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {

                    HashMap<String, String> row = new HashMap<String, String>();

                    row.put("id",String.valueOf(rs.getInt("id")));
                    row.put("inv_prod_id",String.valueOf(rs.getInt("inv_prod_id")));
                    row.put("cantidad",StringHelper.roundDouble(String.valueOf(rs.getDouble("cantidad")),4));//String.valueOf(rs.getDouble("cantidad")));
                    row.put("sku",rs.getString("sku"));
                    row.put("descripcion",rs.getString("descripcion"));
                    row.put("requiere_numero_lote",String.valueOf(rs.getBoolean("requiere_numero_lote")));
                    //row.put("cantidad",String.valueOf(rs.getDouble("cantidad")));
                    row.put("cantidad_adicional",String.valueOf(rs.getDouble("cantidad_adicional")));
                    row.put("num_lote",rs.getString("lote"));
                    //row.put("num_lote",rs.getString("num_lote"));
                    row.put("id_reg_det",String.valueOf(rs.getInt("id_reg_det")));
                    row.put("inv_osal_id",String.valueOf(rs.getInt("inv_osal_id")));
                    row.put("inv_alm_id",String.valueOf(rs.getInt("inv_alm_id")));
                    row.put("gral_suc_id",String.valueOf(rs.getInt("gral_suc_id")));
                    row.put("agregado",String.valueOf(rs.getBoolean("agregado")));
                    row.put("cantidad_usada",String.valueOf(rs.getDouble("cantidad_usada")));
                    row.put("guardado",String.valueOf(rs.getBoolean("guardado")));

                    return row;
                }
            }
        );
        return hm_datos_entrada;
    }



    //catalogos chekes
    //CATALOGO DE TIPOS DE EQUIPOS
    @Override
    public ArrayList<HashMap<String, Object>> getProTipoEquipo_PaginaGrid(String data_string, int offset, int pageSize, String orderBy, String asc) {
        String sql_busqueda = "select id from gral_bus_catalogos(?) as foo (id integer)";

        String sql_to_query = "SELECT pro_tipo_equipo.id , pro_tipo_equipo.titulo "
                +"FROM pro_tipo_equipo "
                +"JOIN ("+sql_busqueda+") AS sbt ON sbt.id = pro_tipo_equipo.id "
                +"WHERE pro_tipo_equipo.borrado_logico = false "
                +"order by "+orderBy+" "+asc+" limit ? OFFSET ?";

// ¬† ¬† ¬† ¬†System.out.println("Busqueda GetPage: "+sql_to_query+" "+data_string+" "+ offset +" "+ pageSize);
// ¬† ¬† ¬† ¬†System.out.println("esto es el query ¬†: ¬†"+sql_to_query);
        ArrayList<HashMap<String, Object>> hm = (ArrayList<HashMap<String, Object>>) this.jdbcTemplate.query(
                sql_to_query,
                new Object[]{new String(data_string), new Integer(pageSize),new Integer(offset)}, new RowMapper() {
                    @Override
                    public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                        HashMap<String, Object> row = new HashMap<String, Object>();
                        row.put("id",String.valueOf(rs.getInt("id")));
                        row.put("tipoequipo",rs.getString("titulo"));
                        return row;
                    }
                }
                );
        return hm;
    }


    @Override
    public ArrayList<HashMap<String, String>> getProTipoEquipo_Datos(Integer id) {
        String sql_to_query = "SELECT pro_tipo_equipo.id,pro_tipo_equipo.titulo FROM pro_tipo_equipo WHERE id="+id;

        ArrayList<HashMap<String, String>> hm = (ArrayList<HashMap<String, String>>) this.jdbcTemplate.query(
                sql_to_query,
                new Object[]{}, new RowMapper(){
                    @Override
                    public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                        HashMap<String, String> row = new HashMap<String, String>();
                        row.put("id",String.valueOf(rs.getInt("id")));
                        row.put("tipoequipo",rs.getString("titulo"));
                        return row;
                    }
                }
                );
        return hm;
    }


    @Override
    public String selectFunctionForThisApp2(String campos_data, String extra_data_array) {
        String sql_to_query = "select * from gral_adm_catalogos('"+campos_data+"',array["+extra_data_array+"]);";

        String valor_retorno="";
        Map<String, Object> update = this.getJdbcTemplate().queryForMap(sql_to_query);
        valor_retorno = update.get("gral_adm_catalogos").toString();
        return valor_retorno;
    }
    //termina catalogos chekes


    //obtiene datos de un lota para agregar al grid
    @Override
    public ArrayList<HashMap<String, String>> getProOrdenProd_DatosLote(String no_lote, Integer id_producto, Integer id_usuario) {
        String sql_to_query = ""
                + "SELECT * FROM ( "
                    + "SELECT  "
                    + "inv_lote.id AS id_lote, "
                    + "inv_lote.lote_int, "
                    + "(inicial - salidas + entradas - reservado) AS exis_lote, "
                    + "(CASE WHEN to_char(inv_lote.caducidad,'yyyymmdd') = '29991231' THEN ''::character varying ELSE inv_lote.caducidad::character varying END) AS caducidad, "
                    + "(CASE WHEN inv_lote.pedimento='' OR inv_lote.pedimento IS NULL THEN ' ' ELSE inv_lote.pedimento END) AS pedimento "
                    + "FROM inv_lote "
                    + "WHERE lote_int='"+no_lote+"' AND inv_lote.inv_prod_id="+id_producto+" "
                + ") AS sbt "
                + "WHERE exis_lote > 0;";

        //log.log(Level.INFO, "Ejecutando query de {0}", sql_to_query);
        ArrayList<HashMap<String, String>> hm = (ArrayList<HashMap<String, String>>) this.jdbcTemplate.query(
            sql_to_query,
            new Object[]{}, new RowMapper(){
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    HashMap<String, String> row = new HashMap<String, String>();
                    row.put("id_lote",String.valueOf(rs.getInt("id_lote")));
                    row.put("lote_int",rs.getString("lote_int"));
                    row.put("exis_lote",StringHelper.roundDouble(rs.getString("exis_lote"),2));
                    row.put("caducidad",rs.getString("caducidad"));
                    row.put("pedimento",rs.getString("pedimento"));
                    return row;
                }
            }
        );
        return hm;
    }

    //obtiene especificaciones de la configuracion de el producto seleccionado




    //obtiene datos de los productos de la formula para produccion
    private ArrayList<HashMap<String, String>> getOrdenProdFormulaProducto(Integer id_orden, Integer id_producto) {
        /*
        String sql_to_query = "selecT * from pro_get_detalle_orden_produccion("+id_producto+","+id_orden+",1, 0)  as "
                + "foo(id integer, inv_prod_id integer, sku character varying,descripcion character varying, requiere_numero_lote boolean "
                + ",cantidad_adicional double precision,id_reg_det integer, cantidad double precision,elemento integer, "
                + "lote character varying, inv_osal_id integer) order by elemento";
        */
        String sql_to_query = "select * from pro_get_detalle_orden_produccionv2("+id_producto+","+id_orden+",1, 0)  as "
                + "foo(id integer, inv_prod_id integer, sku character varying,descripcion character varying, requiere_numero_lote boolean "
                + ",cantidad_adicional double precision,id_reg_det integer, cantidad double precision,elemento integer, "
                + "lote character varying, inv_osal_id integer, inv_alm_id integer, gral_suc_id integer, agregado boolean, "
                + "cantidad_usada double precision, guardado boolean) order by elemento";

        //System.out.println("OrdenProdFormula: "+sql_to_query);
        //log.log(Level.INFO, "Ejecutando query de {0}", sql_to_query);
        ArrayList<HashMap<String, String>> hm = (ArrayList<HashMap<String, String>>) this.jdbcTemplate.query(
            sql_to_query,
            new Object[]{}, new RowMapper(){
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    HashMap<String, String> row = new HashMap<String, String>();
                    row.put("inv_prod_id",String.valueOf(rs.getInt("inv_prod_id")));
                    row.put("sku",rs.getString("sku"));
                    row.put("descripcion",rs.getString("descripcion"));
                    row.put("elemento",String.valueOf(rs.getInt("elemento")));
                    row.put("cantidad",StringHelper.roundDouble(String.valueOf(rs.getDouble("cantidad")),4));
                    row.put("cantidad_adicional",StringHelper.roundDouble(String.valueOf(rs.getDouble("cantidad_adicional")),4));
                    row.put("lote",rs.getString("lote"));
                    row.put("agregado",String.valueOf(rs.getBoolean("agregado")));
                    row.put("cantidad_usada",String.valueOf(rs.getDouble("cantidad_usada")));
                    row.put("guardado",String.valueOf(rs.getBoolean("guardado")));
                    return row;
                }
            }
        );
        return hm;
    }

    //obtiene datos de los productos de la formula para produccion
    private ArrayList<HashMap<String, String>> getOrdenProdEspecificacoinesProduccionSubproceso(String id,String id_subproceso, String orden_prod_id, String id_empresa){
        String sql_to_query = ""
                + "select esp.* from ("
                    + "select "
                        + "opd.id, "
                        + "opd.pro_orden_prod_id, "
                        + "opd.num_lote, "
                        + "opd.cantidad,"
                        + "opd.pro_subprocesos_id, "
                        + "opd.inv_prod_id, "
                        + "pro_subprocesos.titulo as subproceso "
                    + "from pro_orden_prod_det as opd "
                    + "join pro_subprocesos on pro_subprocesos.id=opd.pro_subprocesos_id "
                    + "where opd.pro_orden_prod_id="+orden_prod_id+" and opd.pro_subprocesos_id="+id_subproceso+""
                + ") as tmp_det_orden "
                + "left join pro_orden_prod_subp_esp as esp on esp.pro_orden_prod_det_id=tmp_det_orden.id;";
        
        System.out.println("getOrdenProdEspecificacoinesProduccionSubproceso: "+sql_to_query);
        //System.out.println("esto es el query ¬†: ¬†"+sql_to_query);
        ArrayList<HashMap<String, String>> hm_especificaciones = (ArrayList<HashMap<String, String>>) this.jdbcTemplate.query(
            sql_to_query,
            new Object[]{}, new RowMapper(){
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    HashMap<String, String> row = new HashMap<String, String>();

                    row.put("id",String.valueOf(rs.getInt("id")));
                    row.put("pro_subprocesos_id",String.valueOf(rs.getInt("pro_subprocesos_id")));
                    row.put("inv_prod_id",String.valueOf(rs.getInt("inv_prod_id")));
                    row.put("nivel_form",String.valueOf(rs.getInt("nivel_form")));

                    row.put("fineza_inicial",String.valueOf(rs.getInt("fineza_inicial")));
                    row.put("viscosidads_inicial",String.valueOf(rs.getInt("viscosidads_inicial")));
                    row.put("viscosidadku_inicial",String.valueOf(rs.getDouble("viscosidadku_inicial")));
                    row.put("viscosidadcps_inicial",String.valueOf(rs.getInt("viscosidadcps_inicial")));
                    row.put("densidad_inicial",String.valueOf(rs.getDouble("densidad_inicial")));
                    row.put("volatiles_inicial",String.valueOf(rs.getDouble("volatiles_inicial")));
                    row.put("hidrogeno_inicial",String.valueOf(rs.getDouble("hidrogeno_inicial")));
                    row.put("cubriente_inicial",String.valueOf(rs.getDouble("cubriente_inicial")));
                    row.put("tono_inicial",String.valueOf(rs.getDouble("tono_inicial")));
                    row.put("brillo_inicial",String.valueOf(rs.getDouble("brillo_inicial")));
                    row.put("dureza_inicial",rs.getString("dureza_inicial"));
                    row.put("adherencia_inicial",String.valueOf(rs.getDouble("adherencia_inicial")));
                    
                    row.put("fineza_final",String.valueOf(rs.getInt("fineza_final")));
                    row.put("viscosidads_final",String.valueOf(rs.getInt("viscosidads_final")));
                    row.put("viscosidadku_final",String.valueOf(rs.getDouble("viscosidadku_final")));
                    row.put("viscosidadcps_final",String.valueOf(rs.getInt("viscosidadcps_final")));
                    row.put("densidad_final",String.valueOf(rs.getDouble("densidad_final")));
                    row.put("volatiles_final",String.valueOf(rs.getDouble("volatiles_final")));
                    row.put("hidrogeno_final",String.valueOf(rs.getDouble("hidrogeno_final")));
                    row.put("cubriente_final",String.valueOf(rs.getDouble("cubriente_final")));
                    row.put("tono_final",String.valueOf(rs.getDouble("tono_final")));
                    row.put("brillo_final",String.valueOf(rs.getDouble("brillo_final")));
                    row.put("dureza_final",rs.getString("dureza_final"));
                    row.put("adherencia_final",String.valueOf(rs.getDouble("adherencia_final")));

                    return row;
                }
            }
        );
        return hm_especificaciones;
    }

    //obtiene datos de los productos de la formula para produccion
    //private ArrayList<HashMap<String, String>> getOrdenProdEspecificacoinesEstandarSubproceso(String producto_id,String id_subproceso,String id_empresa) {
    private ArrayList<HashMap<String, String>> getOrdenProdEspecificacoinesEstandarSubproceso(String orden_prod_id,String id_subproceso,String id_empresa) {
        /*
        String sql_to_query2 = "select pro_proc_esp.*, "
                + "CASE WHEN pro_instrumentos_fineza < 0 THEN '' ELSE (select titulo from pro_instrumentos where id=pro_instrumentos_fineza) END as inst_fineza,"
                + "CASE WHEN pro_instrumentos_viscosidad1 < 0 THEN '' ELSE (select titulo from pro_instrumentos where id=pro_instrumentos_viscosidad1) END as inst_viscosidad1,"
                + "CASE WHEN pro_instrumentos_viscosidad2 < 0 THEN '' ELSE (select titulo from pro_instrumentos where id=pro_instrumentos_viscosidad2) END as inst_viscosidad2,"
                + "CASE WHEN pro_instrumentos_viscosidad3 < 0 THEN '' ELSE (select titulo from pro_instrumentos where id=pro_instrumentos_viscosidad3) END as inst_viscosidad3,"
                + "CASE WHEN pro_instrumentos_densidad < 0 THEN '' ELSE (select titulo from pro_instrumentos where id=pro_instrumentos_densidad) END as inst_densidad,"
                + "CASE WHEN pro_instrumentos_volatil < 0 THEN '' ELSE (select titulo from pro_instrumentos where id=pro_instrumentos_volatil) END as inst_volatil,"
                + "CASE WHEN pro_instrumentos_cubriente < 0 THEN '' ELSE (select titulo from pro_instrumentos where id=pro_instrumentos_cubriente) END as inst_cubriente,"
                + "CASE WHEN pro_instrumentos_tono < 0 THEN '' ELSE (select titulo from pro_instrumentos where id=pro_instrumentos_tono) END as inst_tono,"
                + "CASE WHEN pro_instrumentos_brillo < 0 THEN '' ELSE (select titulo from pro_instrumentos where id=pro_instrumentos_brillo) END as inst_brillo,"
                + "CASE WHEN pro_instrumentos_dureza < 0 THEN '' ELSE (select titulo from pro_instrumentos where id=pro_instrumentos_dureza) END as inst_dureza,"
                + "CASE WHEN pro_instrumentos_adherencia < 0 THEN '' ELSE (select titulo from pro_instrumentos where id=pro_instrumentos_adherencia) END as inst_adherencia,"
                + "CASE WHEN pro_instrumentos_hidrogeno < 0 THEN '' ELSE (select titulo from pro_instrumentos where id=pro_instrumentos_hidrogeno) END as inst_hidrogeno "
                + "from (select id from pro_procesos where gral_emp_id="+id_empresa+" and inv_prod_id="+producto_id+") as conf "
                + "join pro_subproceso_prod as subp_conf on subp_conf.pro_procesos_id=conf.id join pro_proc_esp on pro_proc_esp.pro_subproceso_prod_id=subp_conf.id "
                + "where subp_conf.pro_subprocesos_id="+id_subproceso;
        */
        
        String sql_to_query=""
        + "select "
            + "esp.estandar_fineza_inicial, "
            + "esp.estandar_viscosidads_inicial, "
            + "esp.estandar_viscosidadku_inicial, "
            + "esp.estandar_viscosidadcps_inicial, "
            + "esp.estandar_densidad_inicial, "
            + "esp.estandar_volatiles_inicial, "
            + "esp.estandar_hidrogeno_inicial, "
            + "esp.estandar_cubriente_inicial, "
            + "esp.estandar_tono_inicial, "
            + "esp.estandar_brillo_inicial, "
            + "esp.estandar_dureza_inicial, "
            + "esp.estandar_adherencia_inicial, "
            + "esp.estandar_fineza_final, "
            + "esp.estandar_viscosidads_final, "
            + "esp.estandar_viscosidadku_final, "
            + "esp.estandar_viscosidadcps_final, "
            + "esp.estandar_densidad_final, "
            + "esp.estandar_volatiles_final, "
            + "esp.estandar_hidrogeno_final, "
            + "esp.estandar_cubriente_final, "
            + "esp.estandar_tono_final, "
            + "esp.estandar_brillo_final, "
            + "esp.estandar_dureza_final, "
            + "esp.estandar_adherencia_final, "
            + "CASE WHEN esp.pro_instrumentos_fineza < 0 THEN '' ELSE (select titulo from pro_instrumentos where id=esp.pro_instrumentos_fineza) END as inst_fineza, "
            + "CASE WHEN esp.pro_instrumentos_viscosidad1 < 0 THEN '' ELSE (select titulo from pro_instrumentos where id=esp.pro_instrumentos_viscosidad1) END as inst_viscosidad1, "
            + "CASE WHEN esp.pro_instrumentos_viscosidad2 < 0 THEN '' ELSE (select titulo from pro_instrumentos where id=esp.pro_instrumentos_viscosidad2) END as inst_viscosidad2, "
            + "CASE WHEN esp.pro_instrumentos_viscosidad3 < 0 THEN '' ELSE (select titulo from pro_instrumentos where id=esp.pro_instrumentos_viscosidad3) END as inst_viscosidad3, "
            + "CASE WHEN esp.pro_instrumentos_densidad < 0 THEN '' ELSE (select titulo from pro_instrumentos where id=esp.pro_instrumentos_densidad) END as inst_densidad, "
            + "CASE WHEN esp.pro_instrumentos_volatil < 0 THEN '' ELSE (select titulo from pro_instrumentos where id=esp.pro_instrumentos_volatil) END as inst_volatil, "
            + "CASE WHEN esp.pro_instrumentos_cubriente < 0 THEN '' ELSE (select titulo from pro_instrumentos where id=esp.pro_instrumentos_cubriente) END as inst_cubriente, "
            + "CASE WHEN esp.pro_instrumentos_tono < 0 THEN '' ELSE (select titulo from pro_instrumentos where id=esp.pro_instrumentos_tono) END as inst_tono, "
            + "CASE WHEN esp.pro_instrumentos_brillo < 0 THEN '' ELSE (select titulo from pro_instrumentos where id=esp.pro_instrumentos_brillo) END as inst_brillo, "
            + "CASE WHEN esp.pro_instrumentos_dureza < 0 THEN '' ELSE (select titulo from pro_instrumentos where id=esp.pro_instrumentos_dureza) END as inst_dureza, "
            + "CASE WHEN esp.pro_instrumentos_adherencia < 0 THEN '' ELSE (select titulo from pro_instrumentos where id=esp.pro_instrumentos_adherencia) END as inst_adherencia, "
            + "CASE WHEN esp.pro_instrumentos_hidrogeno < 0 THEN '' ELSE (select titulo from pro_instrumentos where id=esp.pro_instrumentos_hidrogeno) END as inst_hidrogeno "
        + "from ("
                + "select opd.id "
                + "from pro_orden_prod_det as opd "
                //+ "join pro_subprocesos on pro_subprocesos.id=opd.pro_subprocesos_id "
                //+ "where opd.pro_orden_prod_id=4055 and opd.pro_subprocesos_id=3"
                + "where opd.pro_orden_prod_id="+orden_prod_id+" and opd.pro_subprocesos_id="+id_subproceso+""
        + ") as tmp_det_orden "
        + "left join pro_orden_prod_subp_esp as esp on esp.pro_orden_prod_det_id=tmp_det_orden.id;";
        
        System.out.println("getOrdenProdEspecificacoinesEstandarSubproceso: "+sql_to_query);
        ArrayList<HashMap<String, String>> hm_especificaciones = (ArrayList<HashMap<String, String>>) this.jdbcTemplate.query(
            sql_to_query,
            new Object[]{}, new RowMapper(){
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    HashMap<String, String> row = new HashMap<String, String>();
                    //row.put("id",String.valueOf(rs.getInt("id")));
                    row.put("fineza_inicial",String.valueOf(rs.getInt("estandar_fineza_inicial")));
                    row.put("viscosidads_inicial",String.valueOf(rs.getInt("estandar_viscosidads_inicial")));
                    row.put("viscosidadku_inicial",String.valueOf(rs.getDouble("estandar_viscosidadku_inicial")));
                    row.put("viscosidadcps_inicial",String.valueOf(rs.getInt("estandar_viscosidadcps_inicial")));
                    row.put("densidad_inicial",String.valueOf(rs.getDouble("estandar_densidad_inicial")));
                    row.put("volatiles_inicial",String.valueOf(rs.getDouble("estandar_volatiles_inicial")));
                    row.put("hidrogeno_inicial",String.valueOf(rs.getDouble("estandar_hidrogeno_inicial")));
                    row.put("cubriente_inicial",String.valueOf(rs.getDouble("estandar_cubriente_inicial")));
                    row.put("tono_inicial",String.valueOf(rs.getDouble("estandar_tono_inicial")));
                    row.put("brillo_inicial",String.valueOf(rs.getDouble("estandar_brillo_inicial")));
                    row.put("dureza_inicial",rs.getString("estandar_dureza_inicial"));
                    row.put("adherencia_inicial",String.valueOf(rs.getDouble("estandar_adherencia_inicial")));
                    row.put("fineza_final",String.valueOf(rs.getInt("estandar_fineza_final")));
                    row.put("viscosidads_final",String.valueOf(rs.getInt("estandar_viscosidads_final")));
                    row.put("viscosidadku_final",String.valueOf(rs.getDouble("estandar_viscosidadku_final")));
                    row.put("viscosidadcps_final",String.valueOf(rs.getInt("estandar_viscosidadcps_final")));
                    row.put("densidad_final",String.valueOf(rs.getDouble("estandar_densidad_final")));
                    row.put("volatiles_final",String.valueOf(rs.getDouble("estandar_volatiles_final")));
                    row.put("hidrogeno_final",String.valueOf(rs.getDouble("estandar_hidrogeno_final")));
                    row.put("cubriente_final",String.valueOf(rs.getDouble("estandar_cubriente_final")));
                    row.put("tono_final",String.valueOf(rs.getDouble("estandar_tono_final")));
                    row.put("brillo_final",String.valueOf(rs.getDouble("estandar_brillo_final")));
                    row.put("dureza_final",rs.getString("estandar_dureza_final"));
                    row.put("adherencia_final",String.valueOf(rs.getDouble("estandar_adherencia_final")));
                    
                    //put para los instrumentos
                    row.put("inst_fineza",String.valueOf(rs.getString("inst_fineza")));
                    row.put("inst_viscosidad1",String.valueOf(rs.getString("inst_viscosidad1")));
                    row.put("inst_viscosidad2",String.valueOf(rs.getString("inst_viscosidad2")));
                    row.put("inst_viscosidad3",String.valueOf(rs.getString("inst_viscosidad3")));
                    row.put("inst_densidad",String.valueOf(rs.getString("inst_densidad")));
                    row.put("inst_volatil",String.valueOf(rs.getString("inst_volatil")));
                    row.put("inst_cubriente",String.valueOf(rs.getString("inst_cubriente")));
                    row.put("inst_tono",String.valueOf(rs.getString("inst_tono")));
                    row.put("inst_brillo",String.valueOf(rs.getString("inst_brillo")));
                    row.put("inst_dureza",String.valueOf(rs.getString("inst_dureza")));
                    row.put("inst_adherencia",String.valueOf(rs.getString("inst_adherencia")));
                    row.put("inst_hidrogeno",String.valueOf(rs.getString("inst_hidrogeno")));
                    
                    return row;
                }
            }
        );
        return hm_especificaciones;
    }

    private ArrayList<HashMap<String, Object>> getOrdenProdSubprocesosProducto(String produccion_id, String empresa_id){
        /*
        String sql_to_query = "select opd.id, opd.pro_orden_prod_id,opd.num_lote, opd.cantidad,opd.pro_subprocesos_id, opd.inv_prod_id,"
                + "pro_subprocesos.titulo as subproceso from pro_orden_prod_det as opd "
                + "join pro_subprocesos on pro_subprocesos.id=opd.pro_subprocesos_id "
                + "where opd.pro_orden_prod_id="+produccion_id;
       */
        String sql_to_query = ""
        + "select "
                + "opd.id, "
                + "opd.pro_orden_prod_id, "
                + "opd.num_lote, "
                + "opd.cantidad,"
                + "opd.pro_subprocesos_id, "
                + "opd.inv_prod_id,"
                + "pro_subprocesos.titulo as subproceso, "
                + "pro_equipos.titulo as equipo, "
                + "pro_equipos_adic.titulo as equipo_adic, "
                + "gral_empleados.nombre_pila||' '||gral_empleados.apellido_paterno||' '||gral_empleados.apellido_materno as empleado "
        + "from pro_orden_prod_det as opd join pro_subprocesos on pro_subprocesos.id=opd.pro_subprocesos_id "
        + "left join gral_empleados on gral_empleados.id=opd.gral_empleados_id "
        + "left join pro_equipos on pro_equipos.id=opd.pro_equipos_id "
        + "left join pro_equipos_adic on pro_equipos_adic.id=opd.pro_equipos_adic_id "
        + "where opd.pro_orden_prod_id="+produccion_id;

        final String id_empresa = empresa_id;
        System.out.println("getOrdenProdSubprocesosProducto: "+sql_to_query);
        //log.log(Level.INFO, "Ejecutando query de {0}", sql_to_query);
        ArrayList<HashMap<String, Object>> hm = (ArrayList<HashMap<String, Object>>) this.jdbcTemplate.query(
            sql_to_query,
            new Object[]{}, new RowMapper(){
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {

                    HashMap<String, Object> row = new HashMap<String, Object>();
                    row.put("id",String.valueOf(rs.getInt("id")));
                    row.put("pro_orden_prod_id",String.valueOf(rs.getInt("pro_orden_prod_id")));
                    row.put("inv_prod_id",String.valueOf(rs.getInt("inv_prod_id")));
                    row.put("pro_subprocesos_id",String.valueOf(rs.getInt("pro_subprocesos_id")));
                    row.put("num_lote",rs.getString("num_lote"));
                    row.put("subproceso",rs.getString("subproceso"));
                    row.put("cantidad",StringHelper.roundDouble(String.valueOf(rs.getDouble("cantidad")),4));
                    row.put("equipo_adic",rs.getString("equipo_adic"));
                    row.put("equipo",rs.getString("equipo"));
                    row.put("empleado",rs.getString("empleado"));

                    //String producto_id,String id_subproceso,String id_empresa
                    //row.put("especificaciones_estandar",getOrdenProdEspecificacoinesEstandarSubproceso(String.valueOf(rs.getInt("inv_prod_id")),String.valueOf(rs.getInt("pro_subprocesos_id")), id_empresa));
                    row.put("especificaciones_estandar",getOrdenProdEspecificacoinesEstandarSubproceso(String.valueOf(rs.getInt("pro_orden_prod_id")),String.valueOf(rs.getInt("pro_subprocesos_id")), id_empresa));
                    row.put("especificaciones_produccion",getOrdenProdEspecificacoinesProduccionSubproceso(String.valueOf(rs.getInt("id")), String.valueOf(rs.getInt("pro_subprocesos_id")), String.valueOf(rs.getInt("pro_orden_prod_id")), id_empresa));

                    return row;

                }
            }
        );
        return hm;
    }

    private String selectEstatusProcesoOP(String id_proceso) {
        String sql_to_query = "select pro_proceso_flujo_id from pro_proceso where id="+id_proceso;
        //log.log(Level.INFO, "Ejecutando query de {0}", sql_to_query);
        //System.out.println("Ejacutando Guardar:"+sql_to_query);
        //int update = this.getJdbcTemplate().queryForInt(sql_to_query);
        String valor_retorno="";
        Map<String, Object> update = this.getJdbcTemplate().queryForMap(sql_to_query);

        valor_retorno = update.get("pro_proceso_flujo_id").toString();

        return valor_retorno;
    }

    private String get_id_estructura_by_inv_prod_id(String id_producto) {
        String sql_to_query = "select id from pro_estruc where inv_prod_id="+id_producto+" limit 1";
        //log.log(Level.INFO, "Ejecutando query de {0}", sql_to_query);
        //System.out.println("Ejacutando Guardar:"+sql_to_query);
        //int update = this.getJdbcTemplate().queryForInt(sql_to_query);
        String valor_retorno="";
        Map<String, Object> select = this.getJdbcTemplate().queryForMap(sql_to_query);

        valor_retorno = select.get("id").toString();

        return valor_retorno;
    }


    //metodo para obtener los numeros de lote, de cada produto producido, por la orden de produccion
    private String getLoteProd_OrdenPOroduccion(String folio_op, String inv_mov_tipo,String inv_prod_id,String tipo_doc ) {
        
        String valor_retorno = "0";

        String sql_busqueda = "select count (inv_lote.lote_int) as lote from "
                + "(select id from inv_oent where inv_mov_tipo_id="+inv_mov_tipo+"  AND tipo_documento="+tipo_doc+" AND folio_documento ilike '"+folio_op+"' ) as oent join "
                + "inv_oent_detalle on inv_oent_detalle.inv_oent_id=oent.id AND inv_oent_detalle.inv_prod_id="+inv_prod_id+" "
                + "join inv_lote on inv_lote.inv_oent_detalle_id=inv_oent_detalle.id";

        //esto es para revisar que exista el registro
        int rowCount = this.getJdbcTemplate().queryForInt(sql_busqueda);

        //System.out.println("DATOS PARA EL PDF:"+sql_busqueda);

        //si rowCount es mayor que cero si se encontro registro y extraemos el valor
        if (rowCount > 0){
            
            String sql_to_query = ""
                    + "select inv_lote.lote_int "
                    + "from (select id from inv_oent where inv_mov_tipo_id="+inv_mov_tipo+"  AND tipo_documento="+tipo_doc+" AND folio_documento ilike '"+folio_op+"' ) as oent "
                    + "join inv_oent_detalle on inv_oent_detalle.inv_oent_id=oent.id AND inv_oent_detalle.inv_prod_id="+inv_prod_id+" "
                    + "join inv_lote on inv_lote.inv_oent_detalle_id=inv_oent_detalle.id";
            
            //log.log(Level.INFO, "Ejecutando query de {0}", sql_to_query);
            //System.out.println("Ejacutando Guardar:"+sql_to_query);
            //int update = this.getJdbcTemplate().queryForInt(sql_to_query);

            Map<String, Object> update = this.getJdbcTemplate().queryForMap(sql_to_query);

            valor_retorno = update.get("lote_int").toString();
            
        }else{
            valor_retorno="0";
        }

        return valor_retorno;
    }
    
    //    select pro_estruc_det.inv_prod_id, pro_estruc_det.cantidad, pro_estruc_det.elemento
    //from (select id from pro_estruc where inv_prod_id=312) as formula join
    //pro_estruc_det on pro_estruc_det.pro_estruc_id=formula.id order by pro_estruc_det.elemento
    
    //obtiene datos de un lota para agregar al grid
    @Override
    public ArrayList<HashMap<String, Object>> getPro_DatosOrdenProduccionPdf(String produccion_id, String proceso_id) {
        
        final String proceso = this.selectEstatusProcesoOP(proceso_id);
        
        String sql_query = ""
                + "select distinct op.id,"
                    + "op.folio,"
                    + "op.gral_emp_id, "
                    + "op.fecha_elavorar,"
                    + "opd.cantidad,"
                    + "opd.inv_prod_id, "
                    //+ "inv_prod.sku, "
                    //+ "inv_prod.descripcion,"
                    + "opd.prod_codigo as sku, "
                    + "opd.prod_titulo as descripcion,"
                    + "inv_prod.tipo_de_producto_id AS tipo_prod_id, "
                    + "inv_prod_unidades.titulo as unidad, "
                    + "(CASE WHEN inv_prod_unidades.titulo ~* 'KILO*' THEN (opd.cantidad / opd.densidad) ELSE (opd.cantidad * opd.densidad) END) as cantidad_tmp, "
                    + "(CASE WHEN inv_prod_unidades.titulo ~* 'KILO*' THEN 'LITRO' ELSE 'KILO' END) as unidad_tmp,"
                    + " opd.unidad_id, "
                    + "opd.densidad, "
                    + "op.fecha_elavorar, "
                    + "op.hora_elavorar,"
                    + "op.observaciones,"
                    + "(CASE WHEN pro_estruc.version IS NULL THEN 0 ELSE pro_estruc.version END) AS version "
                + "from pro_orden_prod as op "
                + "join pro_orden_prod_det as opd on opd.pro_orden_prod_id=op.id "
                + "join inv_prod on inv_prod.id=opd.inv_prod_id "
                + "left join inv_prod_unidades on inv_prod_unidades.id=opd.unidad_id "
                + "left join pro_estruc on pro_estruc.id=op.pro_estruc_id "
                + "where op.id="+produccion_id+";";

        System.out.println("getPro_DatosOrdenProduccionPdf: "+sql_query);
        //log.log(Level.INFO, "Ejecutando query de {0}", sql_to_query);
        ArrayList<HashMap<String, Object>> hm = (ArrayList<HashMap<String, Object>>) this.jdbcTemplate.query(
            sql_query,
            new Object[]{}, new RowMapper(){
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    HashMap<String, Object> row = new HashMap<String, Object>();
                    row.put("id",String.valueOf(rs.getInt("id")));
                    row.put("inv_prod_id",String.valueOf(rs.getInt("inv_prod_id")));
                    row.put("cantidad",StringHelper.roundDouble(rs.getDouble("cantidad"),4));
                    row.put("cantidad_tmp",StringHelper.roundDouble(rs.getDouble("cantidad_tmp"),4));
                    row.put("densidad",StringHelper.roundDouble(rs.getDouble("densidad"),4));
                    row.put("folio",rs.getString("folio"));
                    row.put("sku",rs.getString("sku"));
                    row.put("tipo_prod_id",String.valueOf(rs.getInt("tipo_prod_id")));
                    row.put("observaciones",rs.getString("observaciones"));
                    
                    row.put("unidad_id",String.valueOf(rs.getInt("unidad_id")));
                    row.put("gral_emp_id",String.valueOf(rs.getInt("gral_emp_id")));
                    
                    row.put("descripcion",rs.getString("descripcion"));
                    row.put("unidad",rs.getString("unidad"));
                    row.put("unidad_tmp",rs.getString("unidad_tmp"));
                    row.put("fecha_elavorar",rs.getString("fecha_elavorar"));
                    row.put("version",String.valueOf(rs.getInt("version")));
                    
                    row.put("formula",getOrdenProdFormulaProducto(rs.getInt("id"), rs.getInt("inv_prod_id")));
                    
                    row.put("subprocesos",getOrdenProdSubprocesosProducto(String.valueOf(rs.getInt("id")), String.valueOf(rs.getInt("gral_emp_id"))));
                    row.put("lista_procedimiento",getPro_DatosFormulaProcedidmientoPdf(Integer.parseInt(get_id_estructura_by_inv_prod_id(String.valueOf(rs.getInt("inv_prod_id"))))));
                    
                    //ArrayList<HashMap<String, String>>
                    //get_id_estructura_by_inv_prod_id

                    //getPro_DatosFormulaProcedidmientoPdf

                    //getPro_DatosOrdenProduccionPdf

                    String lote = "";
                    if(proceso.equals("4")){
                             //getLoteProd_OrdenPOroduccion(String folio_op,String inv_mov_tipo,String inv_prod_id,String tipo_doc )
                        lote = getLoteProd_OrdenPOroduccion(String.valueOf(rs.getString("folio")), "10",String.valueOf(rs.getInt("inv_prod_id")),"4" );
                    }else{
                        lote = "";
                    }
                    
                    row.put("lote",lote);
                    
                    return row;
                }
            }
        );
        return hm;
    }


    //obtiene datos de un lota para agregar al grid
    @Override
    public ArrayList<HashMap<String, Object>> getPro_DatosPreOrdenProduccionPdf(String preorden_id) {

        String sql_query = "select popd.cantidad,popd.inv_prod_id, "
                + "inv_prod.sku, inv_prod.descripcion,inv_prod_unidades.titulo as unidad, inv_prod.unidad_id, inv_prod.densidad "
                + " from (select id, folio from pro_preorden_prod where id="+preorden_id+") as pop "
                + "join pro_preorden_prod_det as popd  on popd.pro_preorden_prod_id=pop.id "
                + "join inv_prod on inv_prod.id=popd.inv_prod_id left join inv_prod_unidades on inv_prod_unidades.id=inv_prod.unidad_id ";

        //System.out.println("esto es el query ¬†: ¬†"+sql_query);
        //log.log(Level.INFO, "Ejecutando query de {0}", sql_to_query);
        ArrayList<HashMap<String, Object>> hm = (ArrayList<HashMap<String, Object>>) this.jdbcTemplate.query(
            sql_query,
            new Object[]{}, new RowMapper(){
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    HashMap<String, Object> row = new HashMap<String, Object>();
                    row.put("inv_prod_id",String.valueOf(rs.getInt("inv_prod_id")));
                    row.put("cantidad",StringHelper.roundDouble(rs.getDouble("cantidad"),4));
                    row.put("densidad",StringHelper.roundDouble(rs.getDouble("densidad"),4));
                    row.put("sku",rs.getString("sku"));
                    row.put("unidad_id",String.valueOf(rs.getInt("unidad_id")));
                    row.put("descripcion",rs.getString("descripcion"));
                    row.put("unidad",rs.getString("unidad"));

                    return row;
                }
            }
        );
        return hm;
    }


    //actualiza backorder a estatus 1
    private void getPro_update_pro_backorder(String id, String status) {
        this.getJdbcTemplate().execute("UPDATE poc_ped_bo SET estatus="+status+" WHERE id="+id);
    }


    @Override
    public ArrayList<HashMap<String, String>> getInstrumentosMedicion_Datos(Integer id) {
        String sql_to_query = "SELECT id,titulo FROM pro_instrumentos WHERE id="+id;
        ArrayList<HashMap<String, String>> dato_datos = (ArrayList<HashMap<String, String>>) this.jdbcTemplate.query(
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
        return dato_datos;
    }


    // catalogo de instrumentos de medicion SpringDao
    @Override
    public ArrayList<HashMap<String, Object>> getInstrumentosMedicion_PaginaGrid(String data_string, int offset, int pageSize, String orderBy, String asc, Integer id_empresa) {
        String sql_busqueda = "select id from gral_bus_catalogos(?) as foo (id integer)";

	String sql_to_query = "SELECT pro_instrumentos.id, pro_instrumentos.titulo "
                                +"FROM pro_instrumentos "
                                +"JOIN ("+sql_busqueda+") AS sbt ON sbt.id = pro_instrumentos.id "
                                +"WHERE pro_instrumentos.borrado_logico=false "
                                +"and pro_instrumentos.gral_emp_id= " +id_empresa
                                +"order by "+orderBy+" "+asc+" limit ? OFFSET ? ";


        ArrayList<HashMap<String, Object>> hm = (ArrayList<HashMap<String, Object>>) this.jdbcTemplate.query(
            sql_to_query,
            new Object[]{new String(data_string), new Integer(pageSize),new Integer(offset)}, new RowMapper() {
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

    //-------------------------------------------fin catalogo de instrumentos de medicion----------------------------------------
    @Override
    public ArrayList<HashMap<String, String>> getAlmacenes(Integer id_empresa) {
	String sql_query = "SELECT "
                            + "inv_alm.id, "
                            + "inv_alm.titulo, "
                            + "gral_suc.id AS suc_id,"
                            + "gral_suc.empresa_id AS emp_id "
                        + "FROM inv_alm "
                        + "JOIN inv_suc_alm ON inv_suc_alm.almacen_id = inv_alm.id "
                        + "JOIN gral_suc ON gral_suc.id = inv_suc_alm.sucursal_id  "
                        + "WHERE gral_suc.empresa_id="+id_empresa+" AND inv_alm.borrado_logico=FALSE;";
        ArrayList<HashMap<String, String>> hm_alm = (ArrayList<HashMap<String, String>>) this.jdbcTemplate.query(
            sql_query,
            new Object[]{}, new RowMapper() {
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    HashMap<String, String> row = new HashMap<String, String>();
                    row.put("id",String.valueOf(rs.getInt("id")));
                    row.put("titulo",rs.getString("titulo"));
                    row.put("suc_id",String.valueOf(rs.getInt("suc_id")));
                    row.put("emp_id",String.valueOf(rs.getInt("emp_id")));
                    return row;
                }
            }
        );
        return hm_alm;
    }

    //traer sucursales





    //Lista lad formulas de un sku de un producto en desarrollo
     @Override
    public ArrayList<HashMap<String, String>> getVersionesFormulasPorCodigoProducto(String sku, String tipo, Integer id_empresa) {

        String sql_to_query = "select pro_estruc.id, pro_estruc.inv_prod_id, prod_tmp.sku, prod_tmp.descripcion, pro_estruc.version, prod_tmp.densidad from "
                + "(select id, sku, descripcion, densidad from inv_prod where sku ilike '"+sku+"' AND borrado_logico=false AND empresa_id="+id_empresa+" ) as prod_tmp "
                + "JOIN pro_estruc on prod_tmp.id=pro_estruc.inv_prod_id"
                + " where pro_estruc.borrado_logico=false AND gral_emp_id="+id_empresa+" ";

        //System.out.println("getVersionesFormulas: "+ sql_to_query);

        ArrayList<HashMap<String, String>> hm_datos_productos = (ArrayList<HashMap<String, String>>) this.jdbcTemplate.query(
            sql_to_query,
            new Object[]{}, new RowMapper(){
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    HashMap<String, String> row = new HashMap<String, String>();
                    row.put("id",String.valueOf(rs.getInt("id")));
                    row.put("inv_prod_id",String.valueOf(rs.getInt("inv_prod_id")));
                    row.put("sku",rs.getString("sku"));
                    row.put("descripcion",rs.getString("descripcion"));
                    row.put("version",String.valueOf(rs.getInt("version")));
                    row.put("densidad",String.valueOf(StringHelper.roundDouble(rs.getDouble("densidad"),4)));
                    
                    return row;
                }
            }
        );
        return hm_datos_productos;
    }
     
     
     @Override
    public ArrayList<HashMap<String, String>> getProductosFormula(String id_formula) {
         
        String sql_to_query = "select det_form.*,inv_prod.sku, inv_prod.descripcion, inv_prod.densidad, inv_prod.unidad_id,"
                + "inv_prod_unidades.titulo, "
                + "(SELECT inv_calculo_existencia_producto AS existencia FROM inv_calculo_existencia_producto(2,false, det_form.inv_prod_id, 1, 1)) as existencia from ("
                + "select elemento, inv_prod_id, cantidad from pro_estruc_det where pro_estruc_id="+id_formula+" "
                + ") as det_form join "
                + "inv_prod on inv_prod.id=det_form.inv_prod_id "
                + "join inv_prod_unidades on inv_prod_unidades.id=inv_prod.unidad_id "
                + "order by elemento";

        //System.out.println("Ejecutando query de: "+ sql_to_query);

        ArrayList<HashMap<String, String>> hm_datos_productos = (ArrayList<HashMap<String, String>>) this.jdbcTemplate.query(
            sql_to_query,
            new Object[]{}, new RowMapper(){
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    HashMap<String, String> row = new HashMap<String, String>();
                    row.put("inv_prod_id",String.valueOf(rs.getInt("inv_prod_id")));
                    row.put("elemento",String.valueOf(rs.getInt("elemento")));
                    row.put("cantidad",String.valueOf(StringHelper.roundDouble(rs.getDouble("cantidad"), 4)));
                    row.put("densidad",String.valueOf(StringHelper.roundDouble(rs.getDouble("densidad"), 4)));
                    row.put("existencia",String.valueOf(StringHelper.roundDouble(rs.getDouble("existencia"), 4)));
                    row.put("sku",rs.getString("sku"));
                    row.put("descripcion",rs.getString("descripcion"));
                    row.put("unidad_id",String.valueOf(rs.getInt("unidad_id")));
                    row.put("titulo",rs.getString("titulo"));

                    return row;
                }
            }
        );
        return hm_datos_productos;
    }
     
     
     
     
     
     
    //Datos del Producto
     @Override
    public ArrayList<HashMap<String, String>> getProductoFormulaPdfSimulacion(Integer id_formula) {

        String sql_to_query = ""
                + "SELECT "
                    + "pro_estruc.id AS id_formula, "
                    + "pro_estruc.inv_prod_id,  "
                    + "pro_estruc.version,  "
                    + "inv_prod.sku,  "
                    + "inv_prod.descripcion,  "
                    + "inv_prod.densidad  "
                + "FROM pro_estruc  "
                + "JOIN inv_prod ON inv_prod.id=pro_estruc.inv_prod_id "
                + "WHERE pro_estruc.borrado_logico=false "
                + "AND pro_estruc.id="+id_formula+"";

        //System.out.println("getVersionesFormulas: "+ sql_to_query);

        ArrayList<HashMap<String, String>> hm_datos_productos = (ArrayList<HashMap<String, String>>) this.jdbcTemplate.query(
            sql_to_query,
            new Object[]{}, new RowMapper(){
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    HashMap<String, String> row = new HashMap<String, String>();
                    row.put("id_formula",String.valueOf(rs.getInt("id_formula")));
                    row.put("inv_prod_id",String.valueOf(rs.getInt("inv_prod_id")));
                    row.put("sku",rs.getString("sku"));
                    row.put("descripcion",rs.getString("descripcion"));
                    row.put("version",String.valueOf(rs.getInt("version")));
                    row.put("densidad",String.valueOf(StringHelper.roundDouble(rs.getInt("densidad"),4)));

                    return row;
                }
            }
        );
        return hm_datos_productos;
    }
     
     
     
     
    //Obtiene el titulo del tipo de simulacion
    @Override
    public ArrayList<HashMap<String, String>> getProOrdenTipoById(Integer id_tipo){
        String sql_to_query = "select id, titulo from pro_orden_tipos where id="+id_tipo;
        
        ArrayList<HashMap<String, String>> datos_unidades = (ArrayList<HashMap<String, String>>) this.jdbcTemplate.query(
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
        return datos_unidades;
    }
     
     
    //Obtener el Nombre del Empleado a partir de su ID
    @Override
    public HashMap<String, String> getNombreEmpleadoById(Integer id_empleado){
        String sql_to_query = "SELECT id, (CASE WHEN nombre_pila IS NULL THEN '' ELSE nombre_pila END)||' '||(CASE WHEN apellido_paterno IS NULL THEN '' ELSE apellido_paterno END)||' '||(CASE WHEN apellido_materno IS NULL THEN '' ELSE apellido_materno END) AS nombre_usuario FROM gral_empleados WHERE id="+id_empleado+";";
        
        //log.log(Level.INFO, "Ejecutando query de {0}", sql_to_query);
        HashMap<String, String> hm = (HashMap<String, String>) this.jdbcTemplate.queryForObject(
            sql_to_query, 
            new Object[]{}, new RowMapper() {
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    HashMap<String, String> row = new HashMap<String, String>();
                    row.put("id",String.valueOf(rs.getInt("id")));
                    row.put("nombre_usuario",rs.getString("nombre_usuario"));
                    return row;
                }
            }
        );
        
        return hm;
    }
     
     
     
     
     
     
     
   //REportes  de produccion
     @Override
    public ArrayList<HashMap<String, String>> getProduccion(String fecha_inicial, String fecha_final,String sku,String descripcion_sku,Integer id_equipo,Integer id_operario,Integer tipo_reporte, Integer id_empresa) {
         String cadena_AND="";
         if(sku != ""){
            cadena_AND=" AND inv_prod.sku ilike '"+sku+"'";

         }

        if(descripcion_sku != ""){

           cadena_AND+=" AND inv_prod.descripcion ilike '"+descripcion_sku+"'";
       }


        String sql_to_query = " SELECT  "
        +"   pro_orden_prod.folio as folio_orden,   "
        +"   pro_orden_prod.fecha_elavorar as fecha_elaboracion,   "
        +"   pro_orden_prod.lote as numero_lote,   "
        +"   (CASE WHEN inv_prod_unidades.titulo like 'LITR%' then pro_orden_prod_det.cantidad * inv_prod.densidad ELSE pro_orden_prod_det.cantidad end)AS cantidad_kg, "
        +"   pro_orden_prod_det.pro_subprocesos_id,  "
        +"   pro_subprocesos.titulo as subproceso,   "
        +"   pro_orden_prod_det.gral_empleados_id,   "
        +"   gral_empleados.nombre_pila||' '||gral_empleados.apellido_paterno||' '||gral_empleados.apellido_materno as nombre_empleado,  "
        +"   pro_orden_prod_det.pro_equipos_id,   "
        +"   pro_equipos.titulo as nombre_equipo,  "
        +"   pro_orden_prod_det.inv_prod_id,   "
        +"   inv_prod.sku as codigo,   "
        +"   inv_prod.descripcion ,   "
        +"   inv_prod.densidad,  "
        +"   inv_prod_unidades.titulo as  unidad"

        +" FROM pro_orden_prod  "
        +" join pro_orden_tipos on pro_orden_tipos.id = pro_orden_prod.pro_orden_tipos_id  "
        +" join pro_orden_prod_det on pro_orden_prod_det.pro_orden_prod_id = pro_orden_prod.id  "
        +" join inv_prod on inv_prod.id=pro_orden_prod_det.inv_prod_id  "
        +" join pro_subprocesos on pro_subprocesos.id=pro_orden_prod_det.pro_subprocesos_id  "
        +" join gral_empleados on gral_empleados.id=pro_orden_prod_det.gral_empleados_id    "
        +" join pro_equipos on pro_equipos.id=pro_orden_prod_det.pro_equipos_id   "
        +" join  inv_prod_unidades on inv_prod_unidades.id= pro_orden_prod_det.unidad_id "
        +" WHERE pro_orden_prod.gral_emp_id="+id_empresa+" "
        +" "+cadena_AND
        +" AND pro_orden_prod.fecha_elavorar between '"+fecha_inicial+"' AND '"+fecha_final+"' ORDER BY numero_lote asc";

        //System.out.println("DATOS del registro de Produccion :  "+sql_to_query);
        //log.log(Level.INFO, "Ejecutando query de {0}", sql_to_query);
        ArrayList<HashMap<String, String>> hm = (ArrayList<HashMap<String, String>>) this.jdbcTemplate.query(
            sql_to_query,
            new Object[]{}, new RowMapper(){
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    HashMap<String, String> row = new HashMap<String, String>();
                    row.put("folio_orden",rs.getString("folio_orden")  );
                    row.put("numero_lote",rs.getString("numero_lote")  );
                    row.put("codigo",rs.getString("codigo")  );
                    row.put("descripcion",rs.getString("descripcion")  );
                    row.put("cantidad",rs.getString("cantidad_kg")  );
                    row.put("pro_subprocesos_id",rs.getString("pro_subprocesos_id")  );
                    row.put("subproceso",rs.getString("subproceso")  );
                    row.put("pro_equipos_id",rs.getString("pro_equipos_id")  );
                    row.put("nombre_equipo",rs.getString("nombre_equipo")  );
                    row.put("gral_empleados_id",rs.getString("gral_empleados_id")  );
                    row.put("nombre_operador",rs.getString("nombre_empleado")  );


                    row.put("fecha_elaboracion",rs.getString("fecha_elaboracion")  );

                    row.put("inv_prod_id",rs.getString("inv_prod_id")  );
                    row.put("densidad",rs.getString("densidad")  );
                    row.put("unidad",rs.getString("unidad")  );
                return row;
                }
            }
        );
        return hm;
    }

    @Override
    public ArrayList<HashMap<String, String>> getProduccion_por_producto(String fecha_inicial, String fecha_final,String sku,String descripcion_sku,Integer id_equipo,Integer id_operario,Integer tipo_reporte, Integer id_empresa) {
         String cadena_AND="";
         if(sku != ""){
            cadena_AND=" AND inv_prod.sku ilike '"+sku+"'";

         }

        if(descripcion_sku != ""){

           cadena_AND+=" AND inv_prod.descripcion ilike '"+descripcion_sku+"'";
       }


        String sql_to_query = " SELECT  "
        +"   pro_orden_prod.folio as folio_orden,   "
        +"   pro_orden_prod.fecha_elavorar as fecha_elaboracion,   "
        +"   pro_orden_prod.lote as numero_lote,   "
        +"   (CASE WHEN inv_prod_unidades.titulo like 'LITR%' then pro_orden_prod_det.cantidad * inv_prod.densidad ELSE pro_orden_prod_det.cantidad end)AS cantidad_kg, "
        +"   pro_orden_prod_det.pro_subprocesos_id,  "
        +"   pro_subprocesos.titulo as subproceso,   "
        //+"   pro_orden_prod_det.gral_empleados_id,   "
        //+"   gral_empleados.nombre_pila||' '||gral_empleados.apellido_paterno||' '||gral_empleados.apellido_materno as nombre_empleado,  "
        //+"   pro_orden_prod_det.pro_equipos_id,   "
        //+"   pro_equipos.titulo as nombre_equipo,  "
        +"   pro_orden_prod_det.inv_prod_id,   "
        +"   inv_prod.sku as codigo,   "
        +"   inv_prod.descripcion ,   "
        +"   inv_prod.densidad,  "
        +"   inv_prod_unidades.titulo as  unidad"

        +" FROM pro_orden_prod  "
        +" join pro_orden_tipos on pro_orden_tipos.id = pro_orden_prod.pro_orden_tipos_id  "
        +" join pro_orden_prod_det on pro_orden_prod_det.pro_orden_prod_id = pro_orden_prod.id  "
        +" join inv_prod on inv_prod.id=pro_orden_prod_det.inv_prod_id  "
        +" join pro_subprocesos on pro_subprocesos.id=pro_orden_prod_det.pro_subprocesos_id  "
        //+" join gral_empleados on gral_empleados.id=pro_orden_prod_det.gral_empleados_id    "
        //+" join pro_equipos on pro_equipos.id=pro_orden_prod_det.pro_equipos_id   "
        +" join  inv_prod_unidades on inv_prod_unidades.id= pro_orden_prod_det.unidad_id "
        +" WHERE pro_orden_prod.gral_emp_id="+id_empresa+" "
        +" "+cadena_AND+" "
        +" AND pro_orden_prod.fecha_elavorar between '"+fecha_inicial+"' AND '"+fecha_final+"' ORDER BY codigo asc";

        //System.out.println("DATOS   Produccio por productos :  "+sql_to_query);
        //log.log(Level.INFO, "Ejecutando query de {0}", sql_to_query);
        ArrayList<HashMap<String, String>> hm = (ArrayList<HashMap<String, String>>) this.jdbcTemplate.query(
            sql_to_query,
            new Object[]{}, new RowMapper(){
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    HashMap<String, String> row = new HashMap<String, String>();
                    row.put("folio_orden",rs.getString("folio_orden")  );
                    row.put("numero_lote",rs.getString("numero_lote")  );
                    row.put("codigo",rs.getString("codigo")  );
                    row.put("descripcion",rs.getString("descripcion")  );
                    row.put("cantidad",rs.getString("cantidad_kg")  );
                    row.put("pro_subprocesos_id",rs.getString("pro_subprocesos_id")  );
                    row.put("subproceso",rs.getString("subproceso")  );
                    //row.put("pro_equipos_id",rs.getString("pro_equipos_id")  );
                    //row.put("nombre_equipo",rs.getString("nombre_equipo")  );
                    //row.put("gral/_empleados_id",rs.getString("gral_empleados_id")  );
                    //row.put("nombre_operador",rs.getString("nombre_empleado")  );


                    row.put("fecha_elaboracion",rs.getString("fecha_elaboracion")  );

                    row.put("inv_prod_id",rs.getString("inv_prod_id")  );
                    row.put("densidad",rs.getString("densidad")  );
                    row.put("unidad",rs.getString("unidad")  );
                return row;
                }
            }
        );
        return hm;
    }


      @Override
    public ArrayList<HashMap<String, String>> getProduccion_por_equipo(String fecha_inicial, String fecha_final,String sku,String descripcion_sku,Integer id_equipo,Integer id_operario,Integer tipo_reporte, Integer id_empresa) {
         String cadena_AND="";
         if(sku != ""){
            cadena_AND=" AND inv_prod.sku ilike '"+sku+"'";

         }

        if(descripcion_sku != ""){

           cadena_AND+=" AND inv_prod.descripcion ilike '"+descripcion_sku+"'";
       }
        if(id_equipo != 0){

           cadena_AND+=" AND  pro_orden_prod_det.pro_equipos_id="+id_equipo+"  ";
       }


        String sql_to_query = " SELECT  "
        +"   pro_orden_prod.folio as folio_orden,   "
        +"   pro_orden_prod.fecha_elavorar as fecha_elaboracion,   "
        +"   pro_orden_prod.lote as numero_lote,   "
        +"   (CASE WHEN inv_prod_unidades.titulo like 'LITR%' then pro_orden_prod_det.cantidad * inv_prod.densidad ELSE pro_orden_prod_det.cantidad end)AS cantidad_kg, "
        +"   pro_orden_prod_det.pro_subprocesos_id,  "
        +"   pro_subprocesos.titulo as subproceso,   "
        //+"   pro_orden_prod_det.gral_empleados_id,   "
        //+"   gral_empleados.nombre_pila||' '||gral_empleados.apellido_paterno||' '||gral_empleados.apellido_materno as nombre_empleado,  "
        +"   pro_orden_prod_det.pro_equipos_id,   "
        +"   pro_equipos.titulo as nombre_equipo,  "
        +"   pro_orden_prod_det.inv_prod_id,   "
        +"   inv_prod.sku as codigo,   "
        +"   inv_prod.descripcion ,   "
        +"   inv_prod.densidad,  "
        +"   inv_prod_unidades.titulo as  unidad"

        +" FROM pro_orden_prod  "
        +" join pro_orden_tipos on pro_orden_tipos.id = pro_orden_prod.pro_orden_tipos_id  "
        +" join pro_orden_prod_det on pro_orden_prod_det.pro_orden_prod_id = pro_orden_prod.id  "
        +" join inv_prod on inv_prod.id=pro_orden_prod_det.inv_prod_id  "
        +" join pro_subprocesos on pro_subprocesos.id=pro_orden_prod_det.pro_subprocesos_id  "
        +" join gral_empleados on gral_empleados.id=pro_orden_prod_det.gral_empleados_id    "
        +" join pro_equipos on pro_equipos.id=pro_orden_prod_det.pro_equipos_id   "
        +" join  inv_prod_unidades on inv_prod_unidades.id= pro_orden_prod_det.unidad_id "
        +" WHERE pro_orden_prod.gral_emp_id="+id_empresa+" "
        +" "+cadena_AND+" "
        +" AND pro_orden_prod.fecha_elavorar between '"+fecha_inicial+"' AND '"+fecha_final+"' ORDER BY nombre_equipo asc";

        //System.out.println("DATOS del registro de Produccion :  "+sql_to_query);
        //log.log(Level.INFO, "Ejecutando query de {0}", sql_to_query);
        ArrayList<HashMap<String, String>> hm = (ArrayList<HashMap<String, String>>) this.jdbcTemplate.query(
            sql_to_query,
            new Object[]{}, new RowMapper(){
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    HashMap<String, String> row = new HashMap<String, String>();
                    row.put("folio_orden",rs.getString("folio_orden")  );
                    row.put("numero_lote",rs.getString("numero_lote")  );
                    row.put("codigo",rs.getString("codigo")  );
                    row.put("descripcion",rs.getString("descripcion")  );
                    row.put("cantidad",rs.getString("cantidad_kg")  );
                    row.put("pro_subprocesos_id",rs.getString("pro_subprocesos_id")  );
                    row.put("subproceso",rs.getString("subproceso")  );
                    row.put("pro_equipos_id",rs.getString("pro_equipos_id")  );
                    row.put("nombre_equipo",rs.getString("nombre_equipo")  );
  //                  row.put("gral_empleados_id",rs.getString("gral_empleados_id")  );
  //                  row.put("nombre_operador",rs.getString("nombre_empleado")  );


                    row.put("fecha_elaboracion",rs.getString("fecha_elaboracion")  );

                    row.put("inv_prod_id",rs.getString("inv_prod_id")  );
                    row.put("densidad",rs.getString("densidad")  );
                    row.put("unidad",rs.getString("unidad")  );
                return row;
                }
            }
        );
        return hm;
    }

        @Override
    public ArrayList<HashMap<String, String>> getProduccion_por_operario(String fecha_inicial, String fecha_final,String sku,String descripcion_sku,Integer id_equipo,Integer id_operario,Integer tipo_reporte, Integer id_empresa) {
         String cadena_AND="";
         if(sku != ""){
            cadena_AND=" AND inv_prod.sku ilike '"+sku+"'";
         }

        if(descripcion_sku != ""){

           cadena_AND+=" AND inv_prod.descripcion ilike '"+descripcion_sku+"'";
       }
        if(id_operario != 0){

           cadena_AND+=" AND  pro_orden_prod_det.gral_empleados_id="+id_operario+"  ";
       }


        String sql_to_query = " SELECT  "
        +"   pro_orden_prod.folio as folio_orden,   "
        +"   pro_orden_prod.fecha_elavorar as fecha_elaboracion,   "
        +"   pro_orden_prod.lote as numero_lote,   "
        +"   (CASE WHEN inv_prod_unidades.titulo like 'LITR%' then pro_orden_prod_det.cantidad * inv_prod.densidad ELSE pro_orden_prod_det.cantidad end)AS cantidad_kg, "
        +"   pro_orden_prod_det.pro_subprocesos_id,  "
        +"   pro_subprocesos.titulo as subproceso,   "
        +"   pro_orden_prod_det.gral_empleados_id,   "
        +"   gral_empleados.nombre_pila||' '||gral_empleados.apellido_paterno||' '||gral_empleados.apellido_materno as nombre_empleado,  "
        //+"   pro_orden_prod_det.pro_equipos_id,   "
        //+"   pro_equipos.titulo as nombre_equipo,  "
        +"   pro_orden_prod_det.inv_prod_id,   "
        +"   inv_prod.sku as codigo,   "
        +"   inv_prod.descripcion ,   "
        +"   inv_prod.densidad,  "
        +"   inv_prod_unidades.titulo as  unidad"

        +" FROM pro_orden_prod  "
        +" join pro_orden_tipos on pro_orden_tipos.id = pro_orden_prod.pro_orden_tipos_id  "
        +" join pro_orden_prod_det on pro_orden_prod_det.pro_orden_prod_id = pro_orden_prod.id  "
        +" join inv_prod on inv_prod.id=pro_orden_prod_det.inv_prod_id  "
        +" join pro_subprocesos on pro_subprocesos.id=pro_orden_prod_det.pro_subprocesos_id  "
        +" join gral_empleados on gral_empleados.id=pro_orden_prod_det.gral_empleados_id    "
        //+" join pro_equipos on pro_equipos.id=pro_orden_prod_det.pro_equipos_id   "
        +" join  inv_prod_unidades on inv_prod_unidades.id= pro_orden_prod_det.unidad_id "
        +" WHERE pro_orden_prod.gral_emp_id="+id_empresa+" "
        +" "+cadena_AND+" "
        +" AND pro_orden_prod.fecha_elavorar between '"+fecha_inicial+"' AND '"+fecha_final+"' ORDER BY nombre_empleado asc";

        //System.out.println("DATOS del registro de Produccion :  "+sql_to_query);
        //log.log(Level.INFO, "Ejecutando query de {0}", sql_to_query);
        ArrayList<HashMap<String, String>> hm = (ArrayList<HashMap<String, String>>) this.jdbcTemplate.query(
            sql_to_query,
            new Object[]{}, new RowMapper(){
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    HashMap<String, String> row = new HashMap<String, String>();
                    row.put("folio_orden",rs.getString("folio_orden")  );
                    row.put("numero_lote",rs.getString("numero_lote")  );
                    row.put("codigo",rs.getString("codigo")  );
                    row.put("descripcion",rs.getString("descripcion")  );
                    row.put("cantidad",rs.getString("cantidad_kg")  );
                    row.put("pro_subprocesos_id",rs.getString("pro_subprocesos_id")  );
                    row.put("subproceso",rs.getString("subproceso")  );
//                    row.put("pro_equipos_id",rs.getString("pro_equipos_id")  );
//                    row.put("nombre_equipo",rs.getString("nombre_equipo")  );
                    row.put("gral_empleados_id",rs.getString("gral_empleados_id")  );
                    row.put("nombre_operador",rs.getString("nombre_empleado")  );


                    row.put("fecha_elaboracion",rs.getString("fecha_elaboracion")  );

                    row.put("inv_prod_id",rs.getString("inv_prod_id")  );
                    row.put("densidad",rs.getString("densidad")  );
                    row.put("unidad",rs.getString("unidad")  );
                return row;
                }
            }
        );
        return hm;
    }




     //obtiene tipos de productos
    @Override
    public ArrayList<HashMap<String, String>> getProducto_Tipos_produccion() {
	String sql_query = "SELECT DISTINCT id,titulo FROM inv_prod_tipos WHERE borrado_logico=false and id in (1,2) order by titulo ASC;";
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
    public ArrayList<HashMap<String, String>> getBuscadorProductos_produccion(String sku, String tipo, String descripcion, Integer id_empresa) {
        String where = "";

        if(!tipo.equals('0')){
		where=" AND inv_prod_tipos.id ="+tipo;
	}
	if(!sku.equals("")){
		where=" AND inv_prod.sku ilike '%"+sku+"%'";
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

    //tipos de equipos de produccion use     getTiposEquipos  linea(1209)   getProOrdenEquipoDisponible(titulo, id_empresa)
    //obtiene los Operarios
    @Override
    public ArrayList<HashMap<String, String>> getOperarios(Integer id_empresa) {
	String sql_query = "select emp_tmp.id, (emp_tmp.nombre_pila||' '||emp_tmp.apellido_paterno||' '||emp_tmp.apellido_materno  )as nombre_operario  "
                            +" from ( "
                            +" select id from gral_puestos where gral_emp_id="+id_empresa+" and borrado_logico=false "
                            +" ) as pu_tmp  "
                            +" join gral_empleados as emp_tmp on emp_tmp.gral_puesto_id=pu_tmp.id  order by nombre_operario ";
                //select id ,titulo from pro_tipo_equipo where gral_emp_id="+id_empresa+" and borrado_logico=FALSE;";
        ArrayList<HashMap<String, String>> hm_alm = (ArrayList<HashMap<String, String>>) this.jdbcTemplate.query(
            sql_query,
            new Object[]{}, new RowMapper() {
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    HashMap<String, String> row = new HashMap<String, String>();
                    row.put("id",rs.getString("id"));
                    row.put("nombre_operario",rs.getString("nombre_operario"));
                    return row;
                }
            }
        );
        return hm_alm;
    }

    


    @Override
    public ArrayList<HashMap<String, String>> getReporteEnvasado_Datos(Integer id_empresa) {
	String sql_query = "select  "
+"    1 ::integer  as id,  "
+"    'columna 1'::character varying  as columna_1,  "
+"    'columna 2'::character varying  as columna_2,  "
+"    'columna 3'::character varying  as columna_3,  "
+"    'columna 4'::character varying  as columna_4,  "
+"    'columna 5'::character varying  as columna_5,  "
+"    'columna 6'::character varying  as columna_6,  "
+"    'columna 7'::character varying  as columna_7,  "
+"    'columna 8'::character varying  as columna_8,  "
+"    'columna 9'::character varying  as columna_9,  "
+"    'columna 10'::character varying  as columna_10,  "
+"    'columna 11'::character varying  as columna_11,  "
+"    'columna 12'::character varying  as columna_12  "

+"    from inv_prod limit 50  ";
                //select id ,titulo from pro_tipo_equipo where gral_emp_id="+id_empresa+" and borrado_logico=FALSE;";
        ArrayList<HashMap<String, String>> hm_alm = (ArrayList<HashMap<String, String>>) this.jdbcTemplate.query(
            sql_query,
            new Object[]{}, new RowMapper() {
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    HashMap<String, String> row = new HashMap<String, String>();
                    row.put("id",rs.getString("id"));
                    row.put("columna_1",rs.getString("columna_1"));
                    row.put("columna_2",rs.getString("columna_2"));
                    row.put("columna_3",rs.getString("columna_3"));
                    row.put("columna_4",rs.getString("columna_4"));
                    row.put("columna_5",rs.getString("columna_5"));
                    row.put("columna_6",rs.getString("columna_6"));
                    row.put("columna_7",rs.getString("columna_7"));
                    row.put("columna_8",rs.getString("columna_8"));
                    row.put("columna_9",rs.getString("columna_9"));
                    row.put("columna_10",rs.getString("columna_10"));
                    row.put("columna_11",rs.getString("columna_11"));
                    row.put("columna_12",rs.getString("columna_12"));
                    return row;
                }
            }
        );
        return hm_alm;
    }

    @Override
    public ArrayList<HashMap<String, String>> getReportReenvasado_Datos(Integer id_empresa) {
	String sql_query = "select  "
+"    1 ::integer  as id,  "
+"    'columna 1'::character varying  as columna_1,  "
+"    'columna 2'::character varying  as columna_2,  "
+"    'columna 3'::character varying  as columna_3,  "
+"    'columna 4'::character varying  as columna_4,  "
+"    'columna 5'::character varying  as columna_5,  "
+"    'columna 6'::character varying  as columna_6,  "
+"    'columna 7'::character varying  as columna_7,  "
+"    'columna 8'::character varying  as columna_8,  "
+"    'columna 9'::character varying  as columna_9,  "
+"    'columna 10'::character varying  as columna_10,  "
+"    'columna 11'::character varying  as columna_11,  "
+"    'columna 12'::character varying  as columna_12  "

+"    from inv_prod limit 50  ";
                //select id ,titulo from pro_tipo_equipo where gral_emp_id="+id_empresa+" and borrado_logico=FALSE;";
        ArrayList<HashMap<String, String>> hm_alm = (ArrayList<HashMap<String, String>>) this.jdbcTemplate.query(
            sql_query,
            new Object[]{}, new RowMapper() {
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    HashMap<String, String> row = new HashMap<String, String>();
                    row.put("id",rs.getString("id"));
                    row.put("columna_1",rs.getString("columna_1"));
                    row.put("columna_2",rs.getString("columna_2"));
                    row.put("columna_3",rs.getString("columna_3"));
                    row.put("columna_4",rs.getString("columna_4"));
                    row.put("columna_5",rs.getString("columna_5"));
                    row.put("columna_6",rs.getString("columna_6"));
                    row.put("columna_7",rs.getString("columna_7"));
                    row.put("columna_8",rs.getString("columna_8"));
                    row.put("columna_9",rs.getString("columna_9"));
                    row.put("columna_10",rs.getString("columna_10"));
                    row.put("columna_11",rs.getString("columna_11"));
                    row.put("columna_12",rs.getString("columna_12"));
                    return row;
                }
            }
        );
        return hm_alm;
    }
   //Fin de los metos para reportes de produccion
    
    
    
    
    
    //Inicio de los metodos por el catalogo de equipos adicionales
    @Override
    public ArrayList<HashMap<String, Object>> getEquipoAdicional_PaginaGrid(String data_string, int offset, int pageSize, String orderBy, String asc) {
        String sql_busqueda = "SELECT id FROM gral_bus_catalogos(?) AS foo (id integer)";

	String sql_to_query = "SELECT pro_equipos_adic.id, "
                                +"pro_equipos_adic.titulo, "
                                +"pro_equipos_adic.titulo_corto "
                        +"FROM pro_equipos_adic "
                        +"JOIN ("+sql_busqueda+") as subt on subt.id=pro_equipos_adic.id "
                        +"ORDER BY "+orderBy+" "+asc+" LIMIT ? OFFSET ?";

        //System.out.println("Busqueda GetPage: "+sql_to_query+"  data_string"+data_string );

        //log.log(Level.INFO, "Ejecutando query de {0}", sql_to_query);
        ArrayList<HashMap<String, Object>> hm = (ArrayList<HashMap<String, Object>>) this.jdbcTemplate.query(
            sql_to_query,
            new Object[]{new String(data_string),new Integer(pageSize),new Integer(offset)}, new RowMapper() {
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    HashMap<String, Object> row = new HashMap<String, Object>();
                    row.put("id",String.valueOf(rs.getInt("id")));
                    row.put("titulo",rs.getString("titulo"));
                    row.put("titulo_corto",rs.getString("titulo_corto"));
                    return row;
                }
            }
        );
        return hm;
    }
    
    
     //obtiene los datos de un equipo adicional
    @Override
    public ArrayList<HashMap<String, String>> getProEquipoAdicional_Datos(Integer id) {

        String sql_to_query =    " select pro_equipos_adic.id, pro_equipos_adic.titulo,pro_equipos_adic.titulo_corto from pro_equipos_adic "
                + "where pro_equipos_adic.id=" + id;
                

        //System.out.println("segundo query"+sql_to_query);
        ArrayList<HashMap<String, String>> datos_formulas = (ArrayList<HashMap<String, String>>) this.jdbcTemplate.query(
            sql_to_query,
            new Object[]{}, new RowMapper(){
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    HashMap<String, String> row = new HashMap<String, String>();
                    row.put("id",String.valueOf(rs.getInt("id")));
                    row.put("titulo",String.valueOf(rs.getString("titulo")));
                    row.put("titulo_corto",String.valueOf(rs.getString("titulo_corto")));
                  
                    return row;
                }
            }
        );
        return datos_formulas;
    }
    
    
    
    
//Inicio de los metodos por el catalogo de equipos 
    @Override
    public ArrayList<HashMap<String, Object>> getProEquipo_PaginaGrid(String data_string, int offset, int pageSize, String orderBy, String asc) {
        String sql_busqueda = "SELECT id FROM gral_bus_catalogos(?) AS foo (id integer)";

	String sql_to_query = "SELECT pro_equipos.id, "
                                +"pro_equipos.titulo, "
                                +"pro_equipos.titulo_corto, "
                                +"pro_equipos.pro_tipo_equipo_id "
                        +"FROM pro_equipos "
                        +"JOIN ("+sql_busqueda+") as subt on subt.id=pro_equipos.id "
                        +"ORDER BY "+orderBy+" "+asc+" LIMIT ? OFFSET ?";

        //System.out.println("Busqueda GetPage: "+sql_to_query+"  data_string"+data_string );

        //log.log(Level.INFO, "Ejecutando query de {0}", sql_to_query);
        ArrayList<HashMap<String, Object>> hm = (ArrayList<HashMap<String, Object>>) this.jdbcTemplate.query(
            sql_to_query,
            new Object[]{new String(data_string),new Integer(pageSize),new Integer(offset)}, new RowMapper() {
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    HashMap<String, Object> row = new HashMap<String, Object>();
                    row.put("id",String.valueOf(rs.getInt("id")));
                    row.put("titulo",rs.getString("titulo"));
                    row.put("titulo_corto",rs.getString("titulo_corto"));
                    row.put("pro_tipo_equipo_id",rs.getString("pro_tipo_equipo_id"));
                    return row;
                }
            }
        );
        return hm;
    }
    
    
     //obtiene los datos de un equipos 
    @Override
    public ArrayList<HashMap<String, String>> getProEquipo_Datos(Integer id) {

        String sql_to_query =    " select pro_equipos.id, pro_equipos.titulo,pro_equipos.titulo_corto, pro_equipos.pro_tipo_equipo_id from pro_equipos "
                + "where pro_equipos.id="+ id;
                
        //System.out.println("segundo query"+sql_to_query);
        ArrayList<HashMap<String, String>> datos_formulas = (ArrayList<HashMap<String, String>>) this.jdbcTemplate.query(
            sql_to_query,
            new Object[]{}, new RowMapper(){
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    HashMap<String, String> row = new HashMap<String, String>();
                    row.put("id",String.valueOf(rs.getInt("id")));
                    row.put("titulo",String.valueOf(rs.getString("titulo")));
                    row.put("titulo_corto",String.valueOf(rs.getString("titulo_corto")));
                    row.put("pro_tipo_equipo_id",String.valueOf(rs.getString("pro_tipo_equipo_id")));
                    return row;
                }
            }
        );
        return datos_formulas;
    }
    
    
    
     //Metodo para obtener todos los Tipos de Equipos
    @Override
    public ArrayList<HashMap<String, String>> getProEquipo_Tipos(Integer idEmp) {
        String sql_to_query="SELECT id,titulo FROM pro_tipo_equipo WHERE borrado_logico=FALSE AND gral_emp_id=?;";
        
        System.out.println("segundo query"+sql_to_query);
        ArrayList<HashMap<String, String>> hmTipos = (ArrayList<HashMap<String, String>>) this.jdbcTemplate.query(
            sql_to_query,
            new Object[]{new Integer(idEmp)}, new RowMapper(){
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    HashMap<String, String> row = new HashMap<String, String>();
                    row.put("id",String.valueOf(rs.getInt("id")));
                    row.put("titulo",String.valueOf(rs.getString("titulo")));
                    return row;
                }
            }
        );
        return hmTipos;
    }
    
    
    
    
    
    
    
    //Inicia Pdf de Estructura final de diseño o adecuacion
    //obtiene datos de materia prima que se utilizo en una orden de produccion de un producto tipo laboratorio con su version correspondiente
    @Override
    public ArrayList<HashMap<String, Object>> getPro_DatosOrdenProduccionLabVersionPdf( String produccion_id ) {
        
        
        String sql_query = "selecT * from pro_get_detalle_orden_laboratorio(0,"+produccion_id+",0) " +
            "as foo(inv_prod_id integer,sku character varying,descripcion character varying, " +
            "cantidad_usada double precision, " +
            "cantidad_mp double precision, cantidad_porciento double precision, cantidad_total double precision, " +
            "version integer, "+ 
            "fineza_inicial character varying, " +
            "viscosidads_inicial character varying, " +
            "viscosidadku_inicial character varying, " +
            "viscosidadcps_inicial character varying, " +
            "densidad_inicial character varying, " +
            "volatiles_inicial character varying, " +
            "hidrogeno_inicial character varying, " +
            "cubriente_inicial character varying, " +
            "tono_inicial character varying, " +
            "brillo_inicial character varying, " +
            "dureza_inicial character varying, " +
            "adherencia_inicial character varying) order by version, sku;";
        
        //System.out.println("esto es el query ¬†: ¬†"+sql_query);
        //log.log(Level.INFO, "Ejecutando query de {0}", sql_to_query);
        ArrayList<HashMap<String, Object>> hm = (ArrayList<HashMap<String, Object>>) this.jdbcTemplate.query(
            sql_query,
            new Object[]{}, new RowMapper(){
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    
                    HashMap<String, Object> row = new HashMap<String, Object>();
                    row.put("inv_prod_id",String.valueOf(rs.getInt("inv_prod_id")));
                    row.put("sku",rs.getString("sku"));
                    row.put("descripcion",rs.getString("descripcion"));
                    
                    row.put("cantidad_usada",StringHelper.roundDouble(rs.getDouble("cantidad_usada"),4));
                    row.put("cantidad_mp",StringHelper.roundDouble(rs.getDouble("cantidad_mp"),4));
                    row.put("cantidad_porciento",StringHelper.roundDouble(rs.getDouble("cantidad_porciento"),4));
                    row.put("cantidad_total",StringHelper.roundDouble(rs.getDouble("cantidad_total"),4));
                    row.put("version",StringHelper.roundDouble(rs.getDouble("version"),4));
                    
                    row.put("fineza_inicial",rs.getString("fineza_inicial"));
                    row.put("viscosidads_inicial",rs.getString("viscosidads_inicial"));
                    row.put("viscosidadku_inicial",rs.getString("viscosidadku_inicial"));
                    row.put("viscosidadcps_inicial",rs.getString("viscosidadcps_inicial"));
                    row.put("densidad_inicial",rs.getString("densidad_inicial"));
                    row.put("volatiles_inicial",rs.getString("volatiles_inicial"));
                    row.put("hidrogeno_inicial",rs.getString("hidrogeno_inicial"));
                    row.put("cubriente_inicial",rs.getString("cubriente_inicial"));
                    row.put("tono_inicial",rs.getString("tono_inicial"));
                    row.put("brillo_inicial",rs.getString("brillo_inicial"));
                    row.put("dureza_inicial",rs.getString("dureza_inicial"));
                    row.put("adherencia_inicial",rs.getString("adherencia_inicial"));
                    
                    return row;
                }
            }
        );
        return hm;
    }
    
    
    
    //obtiene datos de materia prima que se utilizo en una orden de produccion de un producto tipo laboratorio
    @Override
    public ArrayList<HashMap<String, Object>> getPro_DatosOrdenProduccionLabPdf( String produccion_id ) {
        
        String sql_query = "selecT sku, inv_prod_id, descripcion from pro_get_detalle_orden_laboratorio(0,"+produccion_id+",0) " +
            "as foo(inv_prod_id integer,sku character varying,descripcion character varying,cantidad_usada double precision," +
            "cantidad_mp double precision, cantidad_porciento double precision, cantidad_total double precision, " +
            "version integer, "+ 
            "fineza_inicial character varying, " +
            "viscosidads_inicial character varying, " +
            "viscosidadku_inicial character varying, " +
            "viscosidadcps_inicial character varying, " +
            "densidad_inicial character varying, " +
            "volatiles_inicial character varying, " +
            "hidrogeno_inicial character varying, " +
            "cubriente_inicial character varying, " +
            "tono_inicial character varying, " +
            "brillo_inicial character varying, " +
            "dureza_inicial character varying, " +
            "adherencia_inicial character varying ) " +
            "group by sku, inv_prod_id, descripcion " +
            "order by sku;";
        System.out.println("esto es el query ¬†: ¬†"+sql_query);
        //log.log(Level.INFO, "Ejecutando query de {0}", sql_to_query);
        ArrayList<HashMap<String, Object>> hm = (ArrayList<HashMap<String, Object>>) this.jdbcTemplate.query(
            sql_query,
            new Object[]{}, new RowMapper(){
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    
                    HashMap<String, Object> row = new HashMap<String, Object>();
                    row.put("inv_prod_id",String.valueOf(rs.getInt("inv_prod_id")));
                    row.put("sku",rs.getString("sku"));
                    row.put("descripcion",rs.getString("descripcion"));
                    
                    return row;
                }
            }
        );
        return hm;
    }
    
    
    
    @Override
    public ArrayList<HashMap<String, String>> getPro_ReporteCalidad(String campos_data) {
        String sql_to_query = new String();
        
        sql_to_query = "select * from pro_reporte_calidad('"+campos_data+"')as foo("
                                    +"id integer,"
                                    +"subproceso character varying,"
                                    +"lote character varying,"
                                    +"fecha character varying, "
                                    +"fineza character varying, "
                                    +"viscosidad character varying, "
                                    +"densidad character varying, "
                                    +"pc character varying, "
                                    +"de character varying, "
                                    +"brillo character varying, "
                                    +"dureza character varying, "
                                    +"nv character varying, "
                                    +"ph character varying, "
                                    +"adhesion character varying, "
                                    +"mp_deshabasto character varying, "
                                    +"mp_contratipo character varying, "
                                    +"mp_agregados character varying, "
                                    +"observ character varying, "
                                    +"comentarios character varying, "
                                    +"codigo character varying, "
                                    +"descripcion character varying, "
                                    +"cantk double precision, "
                                    +"cantl double precision "
                                +") ORDER BY id;";
        //System.out.println("ReporteCalidad: "+sql_to_query);
        
        ArrayList<HashMap<String, String>> data = (ArrayList<HashMap<String, String>>) this.jdbcTemplate.query(
            sql_to_query,
            new Object[]{}, new RowMapper(){
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    HashMap<String, String> row = new HashMap<String, String>();
                    row.put("id",rs.getString("id"));
                    row.put("subproceso",rs.getString("subproceso"));
                    row.put("lote",rs.getString("lote"));
                    row.put("fecha",rs.getString("fecha"));
                    if(rs.getString("fineza").equals("")){ row.put("fineza",rs.getString("fineza")); }else{ row.put("fineza",StringHelper.roundDouble(rs.getDouble("fineza"),2)); }
                    if(rs.getString("viscosidad").equals("")){ row.put("viscosidad",rs.getString("viscosidad")); }else{ row.put("viscosidad",StringHelper.roundDouble(rs.getDouble("viscosidad"),2)); }
                    if(rs.getString("densidad").equals("")){ row.put("densidad",rs.getString("densidad")); }else{ row.put("densidad",StringHelper.roundDouble(rs.getDouble("densidad"),2)); }
                    if(rs.getString("pc").equals("")){ row.put("pc",rs.getString("pc")); }else{ row.put("pc",StringHelper.roundDouble(rs.getDouble("pc"),2)); }
                    if(rs.getString("de").equals("")){ row.put("de",rs.getString("de")); }else{ row.put("de",StringHelper.roundDouble(rs.getDouble("de"),2)); }
                    if(rs.getString("brillo").equals("")){ row.put("brillo",rs.getString("brillo")); }else{ row.put("brillo",StringHelper.roundDouble(rs.getDouble("brillo"),2)); }
                    row.put("dureza",rs.getString("dureza"));
                    if(rs.getString("nv").equals("")){ row.put("nv",rs.getString("nv")); }else{ row.put("nv",StringHelper.roundDouble(rs.getDouble("nv"),2)); }
                    if(rs.getString("ph").equals("")){ row.put("ph",rs.getString("ph")); }else{ row.put("ph",StringHelper.roundDouble(rs.getDouble("ph"),2)); }
                    if(rs.getString("adhesion").equals("")){ row.put("adhesion",rs.getString("adhesion")); }else{ row.put("adhesion",StringHelper.roundDouble(rs.getDouble("adhesion"),2)); }
                    
                    row.put("mp_deshabasto",rs.getString("mp_deshabasto"));
                    row.put("mp_contratipo",rs.getString("mp_contratipo"));
                    row.put("mp_agregados",rs.getString("mp_agregados"));
                    row.put("observ",rs.getString("observ"));
                    row.put("comentarios",rs.getString("comentarios"));
                    row.put("codigo",rs.getString("codigo"));
                    row.put("descripcion",rs.getString("descripcion"));
                    row.put("cantk",StringHelper.roundDouble(rs.getDouble("cantk"),4));
                    row.put("cantl",StringHelper.roundDouble(rs.getDouble("cantl"),4));
                    return row;
                }
            }
        );
        
        
        return data;
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
        
        valorRetorno.put("valor_tc", valor);
        
        return valorRetorno;
    }
    
}
