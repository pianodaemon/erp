package com.agnux.kemikal.springdaos;

import com.agnux.common.helpers.StringHelper;
import com.agnux.kemikal.interfacedaos.EnvInterfaceDao;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;


public class EnvSpringDao implements EnvInterfaceDao{
    private JdbcTemplate jdbcTemplate;

    public JdbcTemplate getJdbcTemplate() {
        return jdbcTemplate;
    }
    
    public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }
    
    //metodos  de uso general
    @Override
    public int countAll(String data_string) {
        String sql_busqueda = "select id from env_bus_aplicativos('"+data_string+"') as foo (id integer)";
        String sql_to_query = "select count(id)::int as total from ("+sql_busqueda+") as subt";
        System.out.println("Validacion:"+sql_busqueda);
        int rowCount = this.getJdbcTemplate().queryForInt(sql_to_query);
        return rowCount;
    }
    
    
    @Override
    public HashMap<String, String> selectFunctionValidateAplicativo(String data, String extra_data_array) {
        String sql_to_query = "select env_validaciones from env_validaciones('"+data+"',array["+extra_data_array+"]);";
        System.out.println("Validacion:"+sql_to_query);

        HashMap<String, String> hm = (HashMap<String, String>) this.jdbcTemplate.queryForObject(
            sql_to_query,
            new Object[]{}, new RowMapper() {
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    HashMap<String, String> row = new HashMap<String, String>();
                    row.put("success",rs.getString("env_validaciones"));
                    return row;
                }
            }
        );
        return hm;
    }
    
    
    @Override
    public String selectFunctionForThisApp(String campos_data, String extra_data_array) {
        String sql_to_query = "select * from env_adm_procesos('"+campos_data+"',array["+extra_data_array+"]);";
        System.out.println("Validacion:"+sql_to_query);
        
        String valor_retorno="";
        Map<String, Object> update = this.getJdbcTemplate().queryForMap(sql_to_query);
        valor_retorno = update.get("env_adm_procesos").toString();
        return valor_retorno;
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
    
    
    //busca datos de un producto en especifico a partir del codigo
    @Override
    public ArrayList<HashMap<String, String>> getDataProductBySku(String codigo, Integer id_empresa) {
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
                + "AND inv_prod.borrado_logico=false "
                + "AND inv_prod.sku='"+codigo.toUpperCase()+"' "
                + "LIMIT 1;";
        
        ArrayList<HashMap<String, String>> hm = (ArrayList<HashMap<String, String>>) this.jdbcTemplate.query(
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
        return hm;
    }
    
    
    //obtiene tipos de productos
    @Override
    public ArrayList<HashMap<String, String>> getProductoTipos() {
	String sql_query = "SELECT DISTINCT id,titulo FROM inv_prod_tipos WHERE borrado_logico=false order by titulo ASC;";
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
    public ArrayList<HashMap<String, String>> getProductoPresentaciones(Integer id_producto, Integer id_empresa) {
        String sql_query="";
        if(id_producto != 0){
            sql_query = "SELECT id,titulo, cantidad FROM inv_prod_presentaciones WHERE id NOT IN (SELECT presentacion_id FROM  inv_prod_pres_x_prod WHERE producto_id = "+id_producto+") order by titulo;";
        }else{
            sql_query = "SELECT id,titulo, cantidad FROM inv_prod_presentaciones WHERE borrado_logico=FALSE order by titulo;";
        }
        
        ArrayList<HashMap<String, String>> hm_pres= (ArrayList<HashMap<String, String>>) this.jdbcTemplate.query(
            sql_query,
            new Object[]{}, new RowMapper() {
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    HashMap<String, String> row = new HashMap<String, String>();
                    row.put("id",rs.getString("id"));
                    row.put("titulo",rs.getString("titulo"));
                    row.put("equivalencia",StringHelper.roundDouble(rs.getString("cantidad"),2));
                    return row;
                }
            }
        );
        return hm_pres;
    }
    
    
    //obtiene las presentaciones Asignadas a un producto en especifico
    @Override
    public ArrayList<HashMap<String, String>> getProductoPresentacionesON(Integer id_producto) {
        String sql_query = "SELECT id,titulo, cantidad FROM inv_prod_presentaciones WHERE id IN (SELECT presentacion_id FROM  inv_prod_pres_x_prod WHERE producto_id = "+id_producto+") order by titulo;";
        
        ArrayList<HashMap<String, String>> hm_pres_on = (ArrayList<HashMap<String, String>>) this.jdbcTemplate.query(
            sql_query,
            new Object[]{}, new RowMapper() {
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    HashMap<String, String> row = new HashMap<String, String>();
                    row.put("id",rs.getString("id"));
                    row.put("titulo",rs.getString("titulo"));
                    row.put("equivalencia",StringHelper.roundDouble(rs.getString("cantidad"),2));
                    return row;
                }
            }
        );
        return hm_pres_on;
    }
    
    
    //obtiene estatus en los que se puede encontrar el proceso de envasado o reenvasado
    @Override
    public ArrayList<HashMap<String, String>> getEstatus() {
        String sql_query = "SELECT DISTINCT id,titulo FROM env_estatus order by id;";
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


    //Obtiene los almacenes de una sucursal
    @Override
    public ArrayList<HashMap<String, String>> getAlmacenes(Integer id_empresa, Integer id_sucursal) {
	String sql_query = ""
                + "SELECT DISTINCT "
                    + "inv_alm.id, "
                    + "inv_alm.titulo "
                + "FROM inv_alm  "
                + "JOIN inv_suc_alm ON inv_suc_alm.almacen_id = inv_alm.id "
                + "JOIN gral_suc ON gral_suc.id = inv_suc_alm.sucursal_id "
                + "WHERE gral_suc.empresa_id="+id_empresa+" "
                + "AND gral_suc.id="+id_sucursal+" "
                + "AND inv_alm.borrado_logico=FALSE;";
        ArrayList<HashMap<String, String>> hm_alm = (ArrayList<HashMap<String, String>>) this.jdbcTemplate.query(
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
        return hm_alm;
    }


    //obtiene los Envases configurados para este producto
    @Override
    public ArrayList<HashMap<String, String>> getEnvasesPorProducto(Integer idProd) {
        String sql_query = "SELECT DISTINCT env_conf.id AS id_conf_env,tbl_pres.id,tbl_pres.titulo,tbl_pres.cantidad FROM env_conf JOIN inv_prod_presentaciones AS tbl_pres ON tbl_pres.id=env_conf.inv_prod_presentacion_id  WHERE env_conf.inv_prod_id="+idProd+" AND env_conf.borrado_logico=FALSE;";
        
        ArrayList<HashMap<String, String>> hm = (ArrayList<HashMap<String, String>>) this.jdbcTemplate.query(
            sql_query,
            new Object[]{}, new RowMapper() {
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    HashMap<String, String> row = new HashMap<String, String>();
                    row.put("id_env",String.valueOf(rs.getInt("id_conf_env")));
                    row.put("id",String.valueOf(rs.getInt("id")));
                    row.put("titulo",rs.getString("titulo"));
                    row.put("equivalencia",StringHelper.roundDouble(rs.getString("cantidad"),2));
                    return row;
                }
            }
        );
        
        return hm;
    }
    
    
    
    
    /***************************************************************************
    * METODOS PARA CONFIGURACION DE ENVASADO
    ***************************************************************************/
    //Este metodo es para obtener datos del Grid y Paginado
    @Override
    public ArrayList<HashMap<String, Object>> getEnvConf_PaginaGrid(String data_string, int offset, int pageSize, String orderBy, String asc) {
        String sql_busqueda = "select id from env_bus_aplicativos(?) as foo (id integer)";
	String sql_to_query = ""
                + "SELECT DISTINCT "
                    + "env_conf.id,"
                    + "inv_prod.sku AS codigo,"
                    + "inv_prod.descripcion,"
                    + "inv_prod_unidades.titulo AS unidad,"
                    + "inv_prod_presentaciones.titulo AS presentacion "
                + "FROM env_conf "
                + "JOIN inv_prod ON inv_prod.id=env_conf.inv_prod_id "
                + "JOIN inv_prod_unidades ON inv_prod_unidades.id=inv_prod.unidad_id "
                + "JOIN inv_prod_presentaciones ON inv_prod_presentaciones.id=env_conf.inv_prod_presentacion_id "
                + "JOIN ("+sql_busqueda+") as subt on subt.id=env_conf.id "
                + "order by "+orderBy+" "+asc+" limit ? OFFSET ?";
        
        //System.out.println("data_string: "+data_string);
        ArrayList<HashMap<String, Object>> hm = (ArrayList<HashMap<String, Object>>) this.jdbcTemplate.query(
            sql_to_query,
            new Object[]{new String(data_string),new Integer(pageSize),new Integer(offset)}, new RowMapper() {
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    HashMap<String, Object> row = new HashMap<String, Object>();
                    row.put("id",rs.getInt("id"));
                    row.put("codigo",rs.getString("codigo"));
                    row.put("descripcion",rs.getString("descripcion"));
                    row.put("unidad",rs.getString("unidad"));
                    row.put("presentacion",rs.getString("presentacion"));
                    return row;
                }
            }
        );
        return hm;
    }
    
    
    
    @Override
    public ArrayList<HashMap<String, String>> getEnvConf_Datos(Integer id) {
        String sql_to_query = ""
                + "SELECT "
                    + "env_conf.id,"
                    + "env_conf.inv_prod_id,"
                    + "env_conf.inv_prod_presentacion_id,"
                    + "inv_prod.sku AS codigo,"
                    + "inv_prod.descripcion,"
                    + "inv_prod_unidades.titulo AS unidad "
                + "FROM env_conf "
                + "JOIN inv_prod ON inv_prod.id=env_conf.inv_prod_id "
                + "JOIN inv_prod_unidades ON inv_prod_unidades.id=inv_prod.unidad_id "
                + "WHERE env_conf.id=?;";
        
        ArrayList<HashMap<String, String>> hm = (ArrayList<HashMap<String, String>>) this.jdbcTemplate.query(
            sql_to_query,
            new Object[]{new Integer(id)}, new RowMapper(){
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    HashMap<String, String> row = new HashMap<String, String>();
                    row.put("id",String.valueOf(rs.getInt("id")));
                    row.put("producto_id",String.valueOf(rs.getInt("inv_prod_id")));
                    row.put("presentacion_id",String.valueOf(rs.getInt("inv_prod_presentacion_id")));
                    row.put("codigo",rs.getString("codigo"));
                    row.put("descripcion",rs.getString("descripcion"));
                    row.put("unidad",rs.getString("unidad"));
                    return row;
                }
            }
        );
        return hm;
    }

    @Override
    public ArrayList<HashMap<String, String>> getEnvConf_DatosGrid(Integer id) {
        String sql_to_query = ""
                + "SELECT "
                    + "env_conf_det.id AS iddet, "
                    + "env_conf_det.inv_prod_id AS id_prod, "
                    + "inv_prod.sku AS codigo, "
                    + "inv_prod.descripcion, "
                    + "inv_prod_unidades.titulo AS unidad, "
                    + "inv_prod_unidades.decimales AS precision, "
                    + "env_conf_det.cantidad AS cant "
                + "FROM env_conf_det "
                + "JOIN inv_prod ON inv_prod.id=env_conf_det.inv_prod_id "
                + "JOIN inv_prod_unidades ON inv_prod_unidades.id=inv_prod.unidad_id "
                + "WHERE env_conf_det.env_conf_id=?;";
        
        ArrayList<HashMap<String, String>> hm = (ArrayList<HashMap<String, String>>) this.jdbcTemplate.query(
            sql_to_query,
            new Object[]{new Integer(id)}, new RowMapper(){
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    HashMap<String, String> row = new HashMap<String, String>();
                    row.put("iddet",String.valueOf(rs.getInt("iddet")));
                    row.put("id_prod",String.valueOf(rs.getInt("id_prod")));
                    row.put("codigo",rs.getString("codigo"));
                    row.put("descripcion",rs.getString("descripcion"));
                    row.put("unidad",rs.getString("unidad"));
                    row.put("precision",String.valueOf(rs.getInt("precision")));
                    row.put("cant",StringHelper.roundDouble(rs.getString("cant"),rs.getInt("precision")));
                    return row;
                }
            }
        );
        return hm;
    }
    /*TERMINA METODOS PARA CONFIGURACION DE ENVASADO **************************/
    
    
    
    /***************************************************************************
    * METODOS PARA APLICATIVO DE RE-ENVASADO
    ***************************************************************************/
    //Este metodo es para obtener datos del Grid y Paginado
    @Override
    public ArrayList<HashMap<String, Object>> getReEenv_PaginaGrid(String data_string, int offset, int pageSize, String orderBy, String asc) {
        String sql_busqueda = "select id from env_bus_aplicativos(?) as foo (id integer)";
	String sql_to_query = ""
                + "SELECT "
                    + "env_reenv.id,"
                    + "env_reenv.folio,"
                    + "inv_alm.titulo AS almacen,"
                    + "inv_prod.sku AS codigo,"
                    + "inv_prod.descripcion,"
                    + "inv_prod_presentaciones.titulo AS  presentacion,"
                    + "(CASE WHEN gral_empleados.nombre_pila IS NULL THEN '' ELSE gral_empleados.nombre_pila END)||' '||(CASE WHEN gral_empleados.apellido_paterno IS NULL THEN '' ELSE gral_empleados.apellido_paterno END)||' '||(CASE WHEN gral_empleados.apellido_materno IS NULL THEN '' ELSE gral_empleados.apellido_materno END) AS empleado,"
                    + "to_char(env_reenv.fecha::timestamp with time zone, 'dd/mm/yyyy') AS fecha,"
                    + "env_reenv.hora_inicio AS hora,"
                    + "env_estatus.titulo AS status "
                + "FROM env_reenv "
                + "JOIN inv_alm ON inv_alm.id=env_reenv.inv_alm_id "
                + "JOIN inv_prod ON inv_prod.id=env_reenv.inv_prod_id "
                + "JOIN inv_prod_presentaciones ON inv_prod_presentaciones.id=env_reenv.inv_prod_presentacion_id "
                + "JOIN gral_empleados ON gral_empleados.id=env_reenv.gral_empleado_id "
                + "JOIN env_estatus ON env_estatus.id=env_reenv.env_estatus_id "
                + "JOIN ("+sql_busqueda+") as subt on subt.id=env_reenv.id "
                + "order by "+orderBy+" "+asc+" limit ? OFFSET ?";
        
        ArrayList<HashMap<String, Object>> hm = (ArrayList<HashMap<String, Object>>) this.jdbcTemplate.query(
            sql_to_query,
            new Object[]{new String(data_string),new Integer(pageSize),new Integer(offset)}, new RowMapper() {
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    HashMap<String, Object> row = new HashMap<String, Object>();
                    row.put("id",rs.getInt("id"));
                    row.put("folio",rs.getString("folio"));
                    row.put("almacen",rs.getString("almacen"));
                    row.put("codigo",rs.getString("codigo"));
                    row.put("descripcion",rs.getString("descripcion"));
                    row.put("presentacion",rs.getString("presentacion"));
                    row.put("empleado",rs.getString("empleado"));
                    row.put("fecha",rs.getString("fecha"));
                    row.put("hora",rs.getTime("hora"));
                    row.put("status",rs.getString("status"));
                    return row;
                }
            }
        );
        return hm;
    }
    
    
    
    @Override
    public ArrayList<HashMap<String, String>> getReEenv_Datos(Integer id) {
        String sql_to_query = ""
                + "SELECT "
                    + "env_reenv.id,"
                    + "env_reenv.folio,"
                    + "env_reenv.inv_prod_id AS producto_id,"
                    + "inv_prod.sku AS codigo,"
                    + "inv_prod.descripcion,"
                    + "inv_prod_unidades.titulo AS unidad,"
                    + "inv_prod_unidades.decimales AS no_dec,"
                    + "env_reenv.inv_prod_presentacion_id AS presentacion_id,"
                    + "env_reenv.inv_alm_id AS  almacen_id,"
                    + "env_reenv.existencia,"
                    + "env_reenv.fecha,"
                    + "env_reenv.hora_inicio,"
                    + "env_reenv.env_estatus_id AS estado_id, "
                    + "env_reenv.gral_empleado_id AS empleado_id "
                + "FROM env_reenv  "
                + "JOIN inv_prod ON inv_prod.id=env_reenv.inv_prod_id "
                + "JOIN inv_prod_unidades ON inv_prod_unidades.id=inv_prod.unidad_id "
                + "WHERE env_reenv.id=?;";
        
        ArrayList<HashMap<String, String>> hm = (ArrayList<HashMap<String, String>>) this.jdbcTemplate.query(
            sql_to_query,
            new Object[]{new Integer(id)}, new RowMapper(){
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    HashMap<String, String> row = new HashMap<String, String>();
                    row.put("id",String.valueOf(rs.getInt("id")));
                    row.put("folio",rs.getString("folio"));
                    row.put("producto_id",String.valueOf(rs.getInt("producto_id")));
                    row.put("codigo",rs.getString("codigo"));
                    row.put("descripcion",rs.getString("descripcion"));
                    row.put("unidad",rs.getString("unidad"));
                    row.put("no_dec",String.valueOf(rs.getInt("no_dec")));
                    row.put("presentacion_id",String.valueOf(rs.getInt("presentacion_id")));
                    row.put("almacen_id",String.valueOf(rs.getInt("almacen_id")));
                    row.put("existencia",StringHelper.roundDouble(rs.getString("existencia"),rs.getInt("no_dec")));
                    row.put("fecha",String.valueOf(rs.getDate("fecha")));
                    row.put("hora",String.valueOf(rs.getTime("hora_inicio")));
                    row.put("estado_id",String.valueOf(rs.getInt("estado_id")));
                    row.put("empleado_id",String.valueOf(rs.getInt("empleado_id")));
                    return row;
                }
            }
        );
        return hm;
    }
    
    
    
    @Override
    public ArrayList<HashMap<String, String>> getReEenv_DatosGrid(Integer id, Integer noDec) {
        final Integer noDecimales = noDec;
        String sql_to_query = ""
                + "SELECT "
                    + "env_reenv_det.id AS iddet, "
                    + "env_reenv_det.env_conf_id, "
                    + "env_reenv_det.inv_alm_id, "
                    + "env_reenv_det.inv_alm_id_env AS alm_id_env, "
                    + "env_conf.inv_prod_presentacion_id AS pres_id, "
                    + "env_reenv_det.cantidad "
                + "FROM env_reenv_det "
                + "JOIN env_conf ON env_conf.id=env_reenv_det.env_conf_id "
                + "WHERE env_reenv_det.env_reenv_id=?;";
        
        ArrayList<HashMap<String, String>> hm = (ArrayList<HashMap<String, String>>) this.jdbcTemplate.query(
            sql_to_query,
            new Object[]{new Integer(id)}, new RowMapper(){
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    HashMap<String, String> row = new HashMap<String, String>();
                    row.put("iddet",String.valueOf(rs.getInt("iddet")));
                    row.put("env_conf_id",String.valueOf(rs.getInt("env_conf_id")));
                    row.put("inv_alm_id",String.valueOf(rs.getInt("inv_alm_id")));
                    row.put("alm_id_env",String.valueOf(rs.getInt("alm_id_env")));
                    row.put("pres_id",String.valueOf(rs.getInt("pres_id")));
                    row.put("cantidad",StringHelper.roundDouble(rs.getString("cantidad"),noDecimales));
                    return row;
                }
            }
        );
        return hm;
    }
    
    
    
    //obtiene los empleados del almacen
    @Override
    public ArrayList<HashMap<String, String>> getReEenv_Empleados(Integer id_empresa) {
        String sql_query = ""
                + "SELECT "
                    + "gral_empleados.id, "
                    + "(CASE WHEN gral_empleados.clave  IS NULL THEN '' ELSE gral_empleados.clave END)||'  '||(CASE WHEN gral_empleados.nombre_pila IS NULL THEN '' ELSE gral_empleados.nombre_pila END)||' '||(CASE WHEN gral_empleados.apellido_paterno IS NULL THEN '' ELSE gral_empleados.apellido_paterno END)||' '||(CASE WHEN gral_empleados.apellido_materno IS NULL THEN '' ELSE gral_empleados.apellido_materno END) AS nombre_empleado "
                + "FROM gral_empleados "
                + "JOIN gral_puestos ON gral_puestos.id=gral_empleados.gral_puesto_id "
                + "WHERE gral_empleados.gral_emp_id="+id_empresa+" "
                + "AND gral_puestos.titulo ILIKE '%ALMACEN%';";
        ArrayList<HashMap<String, String>> hm = (ArrayList<HashMap<String, String>>) this.jdbcTemplate.query(
            sql_query,
            new Object[]{}, new RowMapper() {
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    HashMap<String, String> row = new HashMap<String, String>();
                    row.put("id",String.valueOf(rs.getInt("id")));
                    row.put("nombre_empleado",rs.getString("nombre_empleado"));
                    return row;
                }
            }
        );
        return hm;
    }
    
    
    
    //obtiene las existencias de la presentacion de un Producto en un almacen en especifico
    @Override
    public ArrayList<HashMap<String, String>> getReEenv_Existencias(Integer id_prod, Integer id_pres, Integer id_alm) {
        String sql_query = "SELECT exis, decimales FROM (SELECT (inv_exi_pres.inicial::double precision + inv_exi_pres.entradas::double precision - inv_exi_pres.salidas::double precision - inv_exi_pres.reservado::double precision) AS exis, inv_prod_unidades.decimales FROM inv_exi_pres JOIN inv_prod ON inv_prod.id=inv_exi_pres.inv_prod_id JOIN inv_prod_unidades ON inv_prod_unidades.id=inv_prod.unidad_id WHERE inv_exi_pres.inv_alm_id=? AND inv_exi_pres.inv_prod_id=? AND inv_exi_pres.inv_prod_presentacion_id=?)AS sbt WHERE exis>0;";
        //System.out.println("getExis: "+sql_query);
        //System.out.println("id_prod:"+id_prod+"    id_pres:"+id_pres+"     id_alm:"+id_alm);
        ArrayList<HashMap<String, String>> hm = (ArrayList<HashMap<String, String>>) this.jdbcTemplate.query(
            sql_query,
            new Object[]{new Integer(id_alm), new Integer(id_prod), new Integer(id_pres)}, new RowMapper() {
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    HashMap<String, String> row = new HashMap<String, String>();
                    row.put("decimales",String.valueOf(rs.getInt("decimales")));
                    row.put("exis",StringHelper.roundDouble(rs.getString("exis"),rs.getInt("decimales")));
                    return row;
                }
            }
        );
        return hm;
    }
    
    
    
    @Override
    public HashMap<String, String> getReport_Reenvasado_Header(Integer empresa_id,Integer id_env) {
        String sql_query = " "
                + "SELECT "
                    + "env_reenv.id, "
                    +"    inv_alm.id   as almacen_id,  "
                    +"    inv_alm.titulo as almacen, "
                    +"    inv_prod_unidades.id as unidad_id, "
                    +"    inv_prod_unidades.titulo as unidad, "
                    +"    inv_prod_presentaciones.id as     presentacion_id, "
                    +"    inv_prod_presentaciones.titulo as presentacion,  "
                    +"    env_reenv.folio,  "
                    +"    inv_prod.sku as codigo, "
                    +"    inv_prod.descripcion,  "
                    +"    env_reenv.existencia as existencia_presentacion,  "
                    +"    env_reenv.existencia * inv_prod_presentaciones.cantidad as existencia_unidad,  "
                    +"    env_reenv.fecha,  "
                    +"    env_reenv.hora_inicio,  "
                    +"    env_reenv.env_estatus_id,   "
                    +"    env_estatus.titulo as estado , "
                    +"    env_reenv.gral_empleado_id,  "
                    +"    gral_empleados.nombre_pila||'  '||gral_empleados.apellido_paterno||'  '||gral_empleados.apellido_materno as nombre_empleado,"
                    + "inv_prod_unidades.decimales AS no_dec "
                +"    FROM env_reenv "
                +"    join inv_prod on inv_prod.id = env_reenv.inv_prod_id "
                +"    join inv_prod_presentaciones  on inv_prod_presentaciones.id = env_reenv.inv_prod_presentacion_id  "
                +"    join inv_alm on inv_alm.id = env_reenv.inv_alm_id  "
                +"    join inv_prod_unidades on inv_prod_unidades.id=inv_prod.unidad_id  "
                +"    join env_estatus on env_estatus.id = env_reenv.env_estatus_id "
                +"    join gral_empleados on gral_empleados.id=env_reenv.gral_empleado_id  "
                +"    where env_reenv.gral_emp_id=? and env_reenv.id=?";

        //System.out.println("Reenvasado_header : "+sql_query);


        HashMap<String, String> hm = (HashMap<String, String>) this.jdbcTemplate.queryForObject(
            sql_query,
            new Object[]{new Integer(empresa_id),new Integer(id_env)}, new RowMapper() {
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    HashMap<String, String> row = new HashMap<String, String>();
                    row.put("env_reenv_id",String.valueOf(rs.getInt("id")));
                    row.put("almacen_id",String.valueOf(rs.getInt("almacen_id")));
                    row.put("almacen",rs.getString("almacen"));
                    row.put("unidad_id",String.valueOf(rs.getInt("unidad_id")));
                    row.put("unidad",rs.getString("unidad"));
                    row.put("estado",rs.getString("estado"));
                    row.put("existencia_presentacion",StringHelper.roundDouble(String.valueOf(rs.getDouble("existencia_presentacion")), rs.getInt("no_dec")));
                    row.put("existencia_unidad",StringHelper.roundDouble(String.valueOf(rs.getDouble("existencia_unidad")), rs.getInt("no_dec")));
                    row.put("presentacion_id",String.valueOf(rs.getInt("presentacion_id")));
                    row.put("presentacion",rs.getString("presentacion"));
                    row.put("folio",rs.getString("folio"));
                    row.put("codigo",rs.getString("codigo"));
                    row.put("descripcion",rs.getString("descripcion"));
                    row.put("fecha",rs.getString("fecha"));
                    row.put("hora_inicio",String.valueOf(rs.getTime("hora_inicio")));
                    row.put("gral_empleado_id",String.valueOf(rs.getInt("gral_empleado_id")));
                    row.put("nombre_empleado",rs.getString("nombre_empleado"));
                    return row;
                }
            }
        );
        return hm;
    }


    @Override
    public ArrayList<HashMap<String, String>> getReport_Reenvasado_grid(Integer empresa_id,Integer id_env) {
        String sql_query = "SELECT  "
                        +"    env_reenv_det.id ,   "
                        +"    alm_origen.id as almacen_id,    "
                        +"    env_reenv_det.inv_alm_id,    "
                        +"    alm_origen.titulo as almacen_origen,    "
                        +"    inv_alm.titulo as almacen_destino,    "
                        +"    inv_prod_presentaciones.titulo as presentacion,    "
                        +"    env_reenv_det.cantidad as cantidad_presentacion ,   "
                        +"    inv_prod_unidades.id as unidad_id,    "
                        +"    inv_prod_unidades.titulo as unidad,    "
                        +"    env_reenv_det.cantidad * inv_prod_presentaciones.cantidad as cantidad_unidad,"
                        + "inv_prod_unidades.decimales AS no_dec "
                        +"    FROM env_reenv_det "
                        +"    join env_conf on  env_conf.id = env_reenv_det.env_conf_id     "
                        +"    join inv_prod on inv_prod.id= env_conf.inv_prod_id    "
                        +"    join inv_prod_presentaciones on inv_prod_presentaciones.id =env_conf.inv_prod_presentacion_id    "
                        +"    join inv_alm  as alm_origen on  alm_origen.id=env_reenv_det.inv_alm_id_env     "
                        +"    join inv_alm  on inv_alm.id=env_reenv_det.inv_alm_id     "
                        +"    join inv_prod_unidades on inv_prod_unidades.id=inv_prod.unidad_id      "
                        +"    where env_conf.gral_emp_id =? and env_reenv_det.env_reenv_id=?";

        //System.out.println("id_de_Empresa : "+empresa_id);
        //System.out.println("Grid reenvasado: "+sql_query);
        ArrayList<HashMap<String, String>> hm = (ArrayList<HashMap<String, String>>) this.jdbcTemplate.query(
            sql_query,
            new Object[]{new Integer(empresa_id),new Integer(id_env)}, new RowMapper() {
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    HashMap<String, String> row = new HashMap<String, String>();
                    row.put("env_reenv_det_id",String.valueOf(rs.getInt("id")));
                    row.put("almacen_id",String.valueOf(rs.getInt("almacen_id")));
                    row.put("almacen_origen",rs.getString("almacen_origen"));
                    row.put("almacen_destino",rs.getString("almacen_destino"));
                    row.put("presentacion_destino",rs.getString("presentacion"));
                    row.put("unidad_destino",rs.getString("unidad"));
                    row.put("cantidad_presentacion",StringHelper.roundDouble(String.valueOf(rs.getDouble("cantidad_presentacion")), rs.getInt("no_dec")));
                    row.put("cantidad_unidad",StringHelper.roundDouble(String.valueOf(rs.getDouble("cantidad_unidad")), rs.getInt("no_dec")));
                    
                    return row;
                }
            }
        );
        return hm;
    }
    /*TERMINA METODOS PARA EL PROCESO DE RE-ENVASADO **************************/
    
    
    
    /***************************************************************************
    * METODOS PARA APLICATIVO DE ENVASADO
    ***************************************************************************/
    //Este metodo es para obtener datos del Grid y Paginado
    @Override
    public ArrayList<HashMap<String, Object>> getEnvProceso_PaginaGrid(String data_string, int offset, int pageSize, String orderBy, String asc) {
        String sql_busqueda = "select id from env_bus_aplicativos(?) as foo (id integer)";
	
        
        String sql_to_query = "select env.id,env.folio, env.cantidad ,env_estatus.titulo as estatus, "
                + "inv_prod.sku, inv_prod.descripcion" 
                + ", pres.titulo as presentacion, inv_prod_unidades.decimales " 
                + "  from ("+sql_busqueda+") as subt JOIN "
                + " env_env as env ON env.id=subt.id " 
                + " JOIN env_estatus ON env_estatus.id=env.env_estatus_id " 
                + "JOIN inv_prod ON inv_prod.id=env.inv_prod_id " 
                + "JOIN inv_prod_unidades ON inv_prod_unidades.id=inv_prod.unidad_id " 
                + "JOIN inv_prod_presentaciones as pres ON pres.id=env.inv_prod_presentaciones_id  "
                + "order by "+orderBy+" "+asc+" limit ? OFFSET ?";
        
        
        //System.out.println("data_string: "+sql_to_query);
        
        ArrayList<HashMap<String, Object>> hm = (ArrayList<HashMap<String, Object>>) this.jdbcTemplate.query(
            sql_to_query,
            new Object[]{new String(data_string),new Integer(pageSize),new Integer(offset)}, new RowMapper() {
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    HashMap<String, Object> row = new HashMap<String, Object>();
                    row.put("id",rs.getInt("id"));
                    row.put("folio",rs.getString("folio"));
                    row.put("sku",rs.getString("sku"));
                    row.put("descripcion",rs.getString("descripcion"));
                    row.put("cantidad",StringHelper.roundDouble(String.valueOf(rs.getDouble("cantidad")), rs.getInt("decimales")));
                    row.put("estatus",rs.getString("estatus"));
                    row.put("presentacion",rs.getString("presentacion"));
                    
                    return row;
                }
            }
        );
        return hm;
    }
    
    
    /*Contenido de la orden de envasado seleccionda*/
    @Override
    public ArrayList<HashMap<String, String>> getEnvProceso_Datos(Integer id) {
        
        String sql_to_query = ""
            +"select env.id, env.inv_prod_id,env.cantidad, env.merma, env.inv_prod_presentaciones_id, " 
            +"env.pro_equipos_id,env.gral_empleados_id, env.fecha, env.hora, env.pro_orden_prod_id, " 
            +"env.env_estatus_id, env.inv_alm_id, env.folio, env.folio_op, env.equipo, env.operador ,inv_prod.sku, inv_prod.descripcion, unid.titulo as unidad, unid.decimales  from (" 
            +"select id, inv_prod_id,cantidad, merma, inv_prod_presentaciones_id, " 
            +"pro_equipos_id,gral_empleados_id,fecha, hora, pro_orden_prod_id, " 
            +"env_estatus_id, inv_alm_id, folio," 
            +"(select folio from pro_orden_prod where id=env_env.pro_orden_prod_id) as folio_op, " 
            +"(select titulo from pro_equipos where id=env_env.pro_equipos_id)  as equipo, " 
            +"(select nombre_pila||' '||apellido_paterno||' '||apellido_materno from gral_empleados where id=env_env.gral_empleados_id) as operador " 
            +"from env_env where id="+id+" " 
            +") as env join inv_prod on inv_prod.id=env.inv_prod_id " 
            +"join inv_prod_unidades as unid on unid.id=inv_prod.unidad_id";
            
        //System.out.println("sql_to_query: "+sql_to_query);
        //System.out.println("id: "+id);
        
        ArrayList<HashMap<String, String>> hm = (ArrayList<HashMap<String, String>>) this.jdbcTemplate.query(
            sql_to_query,
            new Object[]{}, new RowMapper(){
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    
                    HashMap<String, String> row = new HashMap<String, String>();
                    row.put("id",String.valueOf(rs.getInt("id")));
                    row.put("inv_prod_id",String.valueOf(rs.getInt("inv_prod_id")));
                    row.put("cantidad",StringHelper.roundDouble(rs.getDouble("cantidad"),rs.getInt("decimales")));
                    row.put("merma",StringHelper.roundDouble(rs.getDouble("merma"),rs.getInt("decimales")));
                    row.put("inv_prod_presentaciones_id",String.valueOf(rs.getInt("inv_prod_presentaciones_id")));
                    row.put("pro_equipos_id",String.valueOf(rs.getInt("pro_equipos_id")));
                    row.put("gral_empleados_id",String.valueOf(rs.getInt("gral_empleados_id")));
                    row.put("fecha",String.valueOf(rs.getDate("fecha")));
                    row.put("hora",String.valueOf(rs.getTime("hora")));
                    row.put("pro_orden_prod_id",String.valueOf(rs.getInt("pro_orden_prod_id")));
                    row.put("env_estatus_id",String.valueOf(rs.getInt("env_estatus_id")));
                    row.put("inv_alm_id",String.valueOf(rs.getInt("inv_alm_id")));
                    row.put("folio",rs.getString("folio"));
                    row.put("folio_op",rs.getString("folio_op"));
                    row.put("equipo",rs.getString("equipo"));
                    row.put("operador",rs.getString("operador"));
                    row.put("sku",rs.getString("sku"));
                    row.put("descripcion",rs.getString("descripcion"));
                    row.put("unidad",rs.getString("unidad"));
                    row.put("decimales",String.valueOf(rs.getInt("decimales")));
                    
                    return row;
                }
            }
        );
        return hm;
    }
    
    
    /*Obtiene el detalle de la orden de envasado seleecionada*/
    @Override
    public ArrayList<HashMap<String, String>> getEnvProcesoDetalle_Datos(Integer id) {
        
        String sql_to_query = ""
            +"select id, env_env_id, env_conf_id, cantidad, inv_alm_id, inv_alm_id_env "
            +"from env_env_det where env_env_id="+id;
            
        //System.out.println("sql_to_query: "+sql_to_query);
        //System.out.println("id: "+id);
        
        ArrayList<HashMap<String, String>> hm = (ArrayList<HashMap<String, String>>) this.jdbcTemplate.query(
            sql_to_query,
            new Object[]{
            }, new RowMapper(){
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    
                    HashMap<String, String> row = new HashMap<String, String>();
                    row.put("id",String.valueOf(rs.getInt("id")));
                    row.put("env_env_id",String.valueOf(rs.getInt("env_env_id")));
                    row.put("env_conf_id",String.valueOf(rs.getInt("env_conf_id")));
                    row.put("cantidad",StringHelper.roundDouble(rs.getDouble("cantidad"),4));
                    row.put("inv_alm_id",String.valueOf(rs.getInt("inv_alm_id")));
                    row.put("inv_alm_id_env",String.valueOf(rs.getInt("inv_alm_id_env")));
                    return row;
                }
            }
        );
        return hm;
    }
    
    
    //###### TERMINA METODOS PARA PROCESO DE ENVASADO ############################################
    @Override
    public ArrayList<HashMap<String, String>> getBuscadorOrdenProduccion(String folio, String sku, String descripcion, Integer id_empresa) {
        String where = "";
	if(!folio.equals("")){
		folio="'%"+folio+"%'";
	}else{
            folio="'%%'";
        }
        
        if(!sku.equals("")){
		sku="'%"+sku+"%'";
	}else{
            sku="'%%'";
        }
        
        if(!descripcion.equals("")){
		descripcion="'%"+descripcion+"%'";
	}else{
            descripcion="'%%'";
        }
        
        String sql_to_query = "select distinct id_op, folio, det_op.cantidad,producto.sku, producto.descripcion, producto.producto_id, producto.unidad_id , " +
            "producto.unidad, (select inv_alm_id from pro_par where gral_emp_id=1 limit 1) as inv_alm_id,producto.inv_prod_presentacion_id, producto.decimales from (" +
            "select pro_orden_prod.id as id_op, pro_orden_prod.folio from pro_orden_prod WHERE pro_orden_prod.folio ilike "+folio+" " +
            "AND gral_emp_id="+id_empresa+") as ordenprod join pro_orden_prod_det as det_op on det_op.pro_orden_prod_id=ordenprod.id_op  join ( " +
            "select productos.sku, productos.descripcion, productos.id as producto_id, unidad_id , inv_unidad.titulo as unidad," +
            "productos.inv_prod_presentacion_id, inv_unidad.decimales  from (" +
            "select sku, descripcion, id, unidad_id, inv_prod_presentacion_id from inv_prod where sku ilike "+sku+" AND descripcion ilike "+descripcion+" " +
            "AND empresa_id="+id_empresa+" AND tipo_de_producto_id in (1, 2)) as productos JOIN inv_prod_unidades as inv_unidad on " +
            "inv_unidad.id=productos.unidad_id) as producto on producto.producto_id=det_op.inv_prod_id " +
            "order by folio, producto.sku";
         //System.out.println("sql_to_query: "+sql_to_query);
                
        //log.log(Level.INFO, "Ejecutando query de {0}", sql_to_query);
        //distinct id_op, folio, det_op.cantidad,producto.sku, producto.descripcion,producto.producto_id, producto.unidad_id , producto.unidad
        ArrayList<HashMap<String, String>> hm_datos_productos = (ArrayList<HashMap<String, String>>) this.jdbcTemplate.query(
            sql_to_query,
            new Object[]{}, new RowMapper(){
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    HashMap<String, String> row = new HashMap<String, String>();
                    row.put("id_op",String.valueOf(rs.getInt("id_op")));
                    row.put("producto_id",String.valueOf(rs.getInt("producto_id")));
                    row.put("unidad_id",String.valueOf(rs.getInt("unidad_id")));
                    row.put("inv_alm_id",String.valueOf(rs.getInt("inv_alm_id")));
                    row.put("inv_prod_presentacion_id",String.valueOf(rs.getInt("inv_prod_presentacion_id")));
                    row.put("folio",rs.getString("folio"));
                    row.put("sku",rs.getString("sku"));
                    row.put("descripcion",rs.getString("descripcion"));
                    row.put("unidad",rs.getString("unidad"));
                    row.put("cantidad",StringHelper.roundDouble(rs.getDouble("cantidad"),rs.getInt("decimales")));
                    row.put("decimales",String.valueOf(rs.getInt("decimales")));
                    
                    return row;
                    
                }
            }
        );
        
        return hm_datos_productos;
        
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
    
 //obtiene las existencias de la presentacion de un Producto en un almacen en especifico
    @Override
    public ArrayList<HashMap<String, String>> getEnv_ExistenciasConf(Integer id_prod, Integer id_pres, Integer id_alm) {
        //String sql_query = "SELECT exis, decimales FROM (SELECT (inv_exi_pres.cantidad::double precision - inv_exi_pres.reservado::double precision) AS exis, inv_prod_unidades.decimales FROM inv_exi_pres JOIN inv_prod ON inv_prod.id=inv_exi_pres.inv_prod_id JOIN inv_prod_unidades ON inv_prod_unidades.id=inv_prod.unidad_id WHERE inv_exi_pres.inv_alm_id=? AND inv_exi_pres.inv_prod_id=? AND inv_exi_pres.inv_prod_presentacion_id=?)AS sbt WHERE exis>0;";
        
        String sql_query = "select conf.inv_prod_id,conf.cantidad," +
                "(CASE WHEN inv_exi_pres.cantidad is null THEN 0 ELSE inv_exi_pres.cantidad END) as cantidad_exist," +
                "inv_prod.sku, inv_prod.descripcion  from (" +
                "select env_conf_det.inv_prod_id, env_conf_det.cantidad from (" +
                "select id from env_conf where inv_prod_id="+id_prod+" AND inv_prod_presentacion_id="+id_pres+" limit 1 " +
                ") as conf_1 " +
                "JOIN env_conf_det on env_conf_det.env_conf_id=conf_1.id ) as conf " +
                "LEFT JOIN inv_exi_pres on (inv_exi_pres.inv_prod_id=conf.inv_prod_id " +
                "AND inv_exi_pres.inv_alm_id="+id_alm+" )" +
                "join inv_prod on inv_prod.id=conf.inv_prod_id";
        
        //System.out.println("getExis: "+sql_query);
        ArrayList<HashMap<String, String>> hm = (ArrayList<HashMap<String, String>>) this.jdbcTemplate.query(
            sql_query,
            new Object[]{}, new RowMapper() {
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    HashMap<String, String> row = new HashMap<String, String>();
                    row.put("inv_prod_id",String.valueOf(rs.getInt("inv_prod_id")));
                    row.put("cantidad",StringHelper.roundDouble(rs.getString("cantidad"),4));
                    row.put("cantidad_exist",StringHelper.roundDouble(rs.getString("cantidad_exist"),4));
                    row.put("sku",rs.getString("sku"));
                    row.put("descripcion",rs.getString("descripcion"));
                    return row;
                }
            }
        );
        return hm;
    }
    
    /*TERMINA METODOS PARA EL PROCESO DE ENVASADO **************************/
    
    
    @Override
    public HashMap<String, String> getReport_Envasado_Header(Integer empresa_id,Integer id_env) {
        String sql_query = " SELECT "
+"    env_env.folio, "
+"    env_estatus.titulo as estado, "
+"    env_env.fecha, "
+"    env_env.hora, "
+"    pro_orden_prod.folio as folio_o_produccion, "
+"    inv_prod.sku as codigo, "
+"    inv_prod.descripcion, "
+"    inv_alm.titulo as almacen, "
+"    inv_prod_presentaciones.titulo as presentacion, "
+"    inv_prod_unidades.titulo as unidad, "
+"    env_env.existencia existencia_presentacion , "
+"    env_env.existencia * inv_prod_presentaciones.cantidad as cantidad_unidad_existente, "
+"    pro_equipos.titulo as equipo, "
+"    gral_empleados.nombre_pila||'  '||          gral_empleados.apellido_paterno||'  '|| gral_empleados.apellido_materno as nombre_operador,  "
+"    env_env.merma "
+"    FROM env_env     "
+"    join inv_prod on  inv_prod.id = env_env.inv_prod_id     "
+"    join gral_empleados  on gral_empleados.id=env_env.gral_empleados_id     "
+"    join inv_prod_presentaciones on inv_prod_presentaciones.id = env_env.inv_prod_presentaciones_id     "
+"    join inv_alm on inv_alm.id=  env_env.inv_alm_id  "
+"     join pro_orden_prod on pro_orden_prod.id=env_env.pro_orden_prod_id  "
+"     join pro_equipos on pro_equipos.id=env_env.pro_equipos_id  "
+"     join inv_prod_unidades on inv_prod_unidades.id=inv_prod.unidad_id  "
+"     join env_estatus on env_estatus.id = env_env.env_estatus_id  "

+"    where env_env.gral_emp_id=? and env_env.id=?";

        //System.out.println("Envasado_header : "+sql_query);


        HashMap<String, String> hm = (HashMap<String, String>) this.jdbcTemplate.queryForObject(
            sql_query,
            new Object[]{new Integer(empresa_id),new Integer(id_env)}, new RowMapper() {
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    HashMap<String, String> row = new HashMap<String, String>();

                    row.put("folio",rs.getString("folio"));
                    row.put("estado",rs.getString("estado"));
                    row.put("fecha",rs.getString("fecha"));
                    row.put("hora",String.valueOf(rs.getTime("hora")));
                    row.put("folio_o_produccion",rs.getString("folio_o_produccion"));
                    row.put("codigo_producto",rs.getString("codigo"));
                    row.put("descripcion_producto",rs.getString("descripcion"));
                    row.put("almacen",rs.getString("almacen"));
                    row.put("presentacion",rs.getString("presentacion"));
                    row.put("presentacion_existente",String.valueOf(rs.getDouble("existencia_presentacion")));
                    row.put("unidad",rs.getString("unidad"));
                    row.put("unidad_existente",String.valueOf(rs.getDouble("cantidad_unidad_existente")));
                    row.put("equipo",rs.getString("equipo"));
                    row.put("nombre_operador",rs.getString("nombre_operador"));
                    row.put("merma",String.valueOf(rs.getDouble("merma")));

                    return row;
                }
            }
        );
        return hm;
    }
    
    
    @Override
    public ArrayList<HashMap<String, String>> getReport_Envasado_grid(Integer empresa_id,Integer id_env) {
        String sql_query = "SELECT   "
+"    alm_origen.titulo as almacen_origen,"
+"    inv_alm.titulo as almacen_destino, "
+"    inv_prod_presentaciones.titulo as presentacion,  "
+"    env_env_det.cantidad as cantidad_presentacion , "

+"    inv_prod_unidades.titulo as unidad,  "
+"    env_env_det.cantidad * inv_prod_presentaciones.cantidad as cantidad_unidad   "
+"    FROM env_env_det          "
+"    join env_conf on env_conf.id = env_env_det.env_conf_id "
+"    join inv_prod on inv_prod.id =env_conf.inv_prod_id  "
+"    join inv_prod_presentaciones on inv_prod_presentaciones.id=env_conf.inv_prod_presentacion_id  "
+"    join inv_alm  as alm_origen on  alm_origen.id=env_env_det.inv_alm_id_env                 "
+"    join inv_alm  on inv_alm.id=env_env_det.inv_alm_id  "
+"    join inv_prod_unidades on inv_prod_unidades.id=inv_prod.unidad_id "

+"    WHERE env_conf.gral_emp_id =? and env_env_det.env_env_id=?";

        //System.out.println("id_de_Empresa : "+empresa_id);
        //System.out.println("Grid Envasado: "+sql_query);
        ArrayList<HashMap<String, String>> hm = (ArrayList<HashMap<String, String>>) this.jdbcTemplate.query(
            sql_query,
            new Object[]{new Integer(empresa_id),new Integer(id_env)}, new RowMapper() {
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    HashMap<String, String> row = new HashMap<String, String>();
                    //row.put("env_reenv_det_id",String.valueOf(rs.getInt("id")));
                    row.put("almacen_origen",rs.getString("almacen_origen"));
                    row.put("almacen_destino",rs.getString("almacen_destino"));
                    row.put("presentacion_destino",rs.getString("presentacion"));
                    row.put("unidad_destino",rs.getString("unidad"));
                    row.put("cantidad_presentacion",String.valueOf(rs.getDouble("cantidad_presentacion")));
                    row.put("cantidad_unidad",String.valueOf(rs.getDouble("cantidad_unidad")));

                    return row;
                }
            }
        );
        return hm;
    }
    
    
}
