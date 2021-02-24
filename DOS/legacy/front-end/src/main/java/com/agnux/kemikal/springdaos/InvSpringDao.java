package com.agnux.kemikal.springdaos;

import com.agnux.common.helpers.StringHelper;
import com.agnux.common.helpers.TimeHelper;
import com.agnux.kemikal.interfacedaos.InvInterfaceDao;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;


public class InvSpringDao implements InvInterfaceDao{
    private static final Logger log  = Logger.getLogger(InvSpringDao.class.getName());
    
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
        System.out.println("Validacion Productos:"+sql_to_query);
        log.log(Level.INFO, "Ejecutando query de {0}", sql_to_query);

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
        log.log(Level.INFO, "Ejecutando query de {0}", sql_to_query);
        System.out.println("Ejacutando Guardar Producto:"+sql_to_query);
        //int update = this.getJdbcTemplate().queryForInt(sql_to_query);
        String valor_retorno="";
        Map<String, Object> update = this.getJdbcTemplate().queryForMap(sql_to_query);

        valor_retorno = update.get("gral_adm_catalogos").toString();

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
    public String selectFunctionForApp_MovimientosInventario(String campos_data, String extra_data_array) {
        String sql_to_query = "select * from inv_adm_movimientos('"+campos_data+"',array["+extra_data_array+"]);";
        //log.log(Level.INFO, "Ejecutando query de {0}", sql_to_query);
        //System.out.println("getInvMovimientos:"+sql_to_query);
        //int update = this.getJdbcTemplate().queryForInt(sql_to_query);
        String valor_retorno="";
        Map<String, Object> update = this.getJdbcTemplate().queryForMap(sql_to_query);

        valor_retorno = update.get("inv_adm_movimientos").toString();

        return valor_retorno;
    }


    
    @Override
    public ArrayList<HashMap<String, String>> getInvParametros(Integer idEmp) {
	String sql_query = "SELECT control_exis_pres FROM gral_emp WHERE id="+idEmp+";";
        ArrayList<HashMap<String, String>> hm_par = (ArrayList<HashMap<String, String>>) this.jdbcTemplate.query(
            sql_query,
            new Object[]{}, new RowMapper() {
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    HashMap<String, String> row = new HashMap<String, String>();
                    row.put("exis_pres",String.valueOf(rs.getBoolean("control_exis_pres")));
                    return row;
                }
            }
        );
        return hm_par;
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

    
    
    
    //Obtiene todas las tasas de Retención del Iva
    @Override
    public ArrayList<HashMap<String, String>> getTasasRetencionIva(Integer idEmp, Integer idSuc) {
        String sql_to_query="";
        if(idSuc>0){
            //Filtrar por sucursal
            sql_to_query = "SELECT id, titulo, tasa FROM gral_imptos_ret  WHERE borrado_logico=false AND gral_emp_id="+idEmp+" AND gral_suc_id="+idSuc+";";
        }else{
            //No filtrar por sucursal
            sql_to_query = "SELECT id, titulo, tasa FROM gral_imptos_ret  WHERE borrado_logico=false AND gral_emp_id="+idEmp+";";
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
    
    

    //llamada al Procedimiento de Reportes de Inventario
    //éste trabaja utilizando el numero de Aplicativo.
    //Para cada aplicativo Recibe diferentes parametros y devuelve diferente Resultado
    @Override
    public ArrayList<HashMap<String, String>> selectFunctionForInvReporte(Integer id_app, String campos_data) {
        ArrayList<HashMap<String, String>> data = new ArrayList<HashMap<String, String>>();
        String sql_to_query = "";

        if(id_app==125){
            sql_to_query = "select * from inv_reporte('"+campos_data+"')as foo(producto_id integer, codigo character varying, descripcion character varying, unidad character varying, presentacion_id integer, presentacion character varying, orden_compra character varying, factura_prov character varying, moneda character varying, costo_adic double precision, costo double precision, tipo_cambio double precision, moneda_id integer, costo_importacion double precision, costo_directo double precision, costo_referencia double precision, precio_minimo double precision, moneda_pm character varying  ) ORDER BY descripcion;";
            //System.out.println("InvReporte: "+sql_to_query);

            ArrayList<HashMap<String, String>> hm125 = (ArrayList<HashMap<String, String>>) this.jdbcTemplate.query(
                sql_to_query,
                new Object[]{}, new RowMapper(){
                    @Override
                    public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                        HashMap<String, String> row = new HashMap<String, String>();
                        row.put("producto_id",String.valueOf(rs.getInt("producto_id")));
                        row.put("codigo",rs.getString("codigo"));
                        row.put("descripcion",rs.getString("descripcion"));
                        row.put("unidad",rs.getString("unidad"));
                        row.put("presentacion_id",String.valueOf(rs.getInt("presentacion_id")));
                        row.put("presentacion",rs.getString("presentacion"));
                        row.put("orden_compra",rs.getString("orden_compra"));
                        row.put("factura_prov",rs.getString("factura_prov"));
                        row.put("moneda",rs.getString("moneda"));
                        row.put("costo_adic",StringHelper.roundDouble(rs.getDouble("costo_adic"),2));
                        row.put("costo",StringHelper.roundDouble(rs.getDouble("costo"),2));
                        row.put("tipo_cambio",StringHelper.roundDouble(rs.getDouble("tipo_cambio"),4));
                        row.put("costo_importacion",StringHelper.roundDouble(rs.getDouble("costo_importacion"),2));
                        row.put("costo_directo",StringHelper.roundDouble(rs.getDouble("costo_directo"),2));
                        row.put("costo_referencia",StringHelper.roundDouble(rs.getDouble("costo_referencia"),2));
                        row.put("precio_minimo",StringHelper.roundDouble(rs.getDouble("precio_minimo"),2));
                        row.put("moneda_pm",rs.getString("moneda_pm"));
                        return row;
                    }
                }
            );
            data=hm125;
        }


        //este es para el reporte de Actualizacion de Precios
        if(id_app==126){
            sql_to_query = "select * from inv_reporte('"+campos_data+"')as foo("
                    + "codigo character varying,"
                    + "descripcion character varying,"
                    + "unidad character varying,"
                    + "presentacion character varying,"
                    + "moneda character varying,"
                    + "precio_minimo double precision,"
                    + "precio_1 double precision,"
                    + "precio_2 double precision,"
                    + "precio_3 double precision,"
                    + "precio_4 double precision,"
                    + "precio_5 double precision,"
                    + "precio_6 double precision,"
                    + "precio_7 double precision,"
                    + "precio_8 double precision,"
                    + "precio_9 double precision,"
                    + "precio_10 double precision,"
                    + "mon1 character varying,"
                    + "mon2 character varying,"
                    + "mon3 character varying,"
                    + "mon4 character varying,"
                    + "mon5 character varying,"
                    + "mon6 character varying,"
                    + "mon7 character varying,"
                    + "mon8 character varying,"
                    + "mon9 character varying,"
                    + "mon10 character varying) ORDER BY descripcion;";
            //System.out.println("InvReporte: "+sql_to_query);

            ArrayList<HashMap<String, String>> hm126 = (ArrayList<HashMap<String, String>>) this.jdbcTemplate.query(
                sql_to_query,
                new Object[]{}, new RowMapper(){
                    @Override
                    public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                        HashMap<String, String> row = new HashMap<String, String>();

                        row.put("codigo",rs.getString("codigo"));
                        row.put("descripcion",rs.getString("descripcion"));
                        row.put("unidad",rs.getString("unidad"));
                        row.put("presentacion",rs.getString("presentacion"));
                        row.put("moneda",rs.getString("moneda"));
                        row.put("precio_minimo",StringHelper.roundDouble(rs.getDouble("precio_minimo"),2));
                        row.put("precio_1",StringHelper.roundDouble(rs.getDouble("precio_1"),2));
                        row.put("precio_2",StringHelper.roundDouble(rs.getDouble("precio_2"),2));
                        row.put("precio_3",StringHelper.roundDouble(rs.getDouble("precio_3"),2));
                        row.put("precio_4",StringHelper.roundDouble(rs.getDouble("precio_4"),2));
                        row.put("precio_5",StringHelper.roundDouble(rs.getDouble("precio_5"),2));
                        row.put("precio_6",StringHelper.roundDouble(rs.getDouble("precio_6"),2));
                        row.put("precio_7",StringHelper.roundDouble(rs.getDouble("precio_7"),2));
                        row.put("precio_8",StringHelper.roundDouble(rs.getDouble("precio_8"),2));
                        row.put("precio_9",StringHelper.roundDouble(rs.getDouble("precio_9"),2));
                        row.put("precio_10",StringHelper.roundDouble(rs.getDouble("precio_10"),2));
                        row.put("mon1",rs.getString("mon1"));
                        row.put("mon2",rs.getString("mon2"));
                        row.put("mon3",rs.getString("mon3"));
                        row.put("mon4",rs.getString("mon4"));
                        row.put("mon5",rs.getString("mon5"));
                        row.put("mon6",rs.getString("mon6"));
                        row.put("mon7",rs.getString("mon7"));
                        row.put("mon8",rs.getString("mon8"));
                        row.put("mon9",rs.getString("mon9"));
                        row.put("mon10",rs.getString("mon10"));
                        return row;
                    }
                }
            );
            data=hm126;
        }


        //este es para el Reporte de Existencias en Inventario
        if(id_app==133){
            sql_to_query = "select * from inv_reporte('"+campos_data+"')as foo(id integer, valor_minimo double precision, valor_maximo double precision, punto_reorden double precision, almacen character varying, familia character varying, grupo character varying, linea character varying, codigo_producto character varying, descripcion character varying, unidad_medida character varying, existencias double precision, costo_unitario double precision, costo_total double precision, moneda_id integer,simbolo_moneda character varying ) ORDER BY descripcion ASC;";
            //System.out.println("InvReporte: "+sql_to_query);

            ArrayList<HashMap<String, String>> hm133 = (ArrayList<HashMap<String, String>>) this.jdbcTemplate.query(
                sql_to_query,
                new Object[]{}, new RowMapper(){
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
                        row.put("existencias",StringHelper.roundDouble(rs.getDouble("existencias"),2));
                        row.put("costo_unitario",StringHelper.roundDouble(rs.getDouble("costo_unitario"),2));
                        row.put("costo_total",StringHelper.roundDouble(rs.getDouble("costo_total"),2));
                        row.put("moneda_id",String.valueOf(rs.getInt("moneda_id")));
                        row.put("simbolo_moneda",rs.getString("simbolo_moneda"));
                        return row;
                    }
                }
            );
            data=hm133;
        }

        
        

        //Reporte de Existencias por Presentaciones
        if(id_app==139){
            sql_to_query = "select * from inv_reporte('"+campos_data+"')as foo(codigo character varying, descripcion character varying, unidad character varying, no_dec integer, idpres integer, presentacion character varying, exis_pres double precision, exis_uni double precision ) ORDER BY descripcion ASC;";
            //System.out.println("InvExiPres: "+sql_to_query);

            ArrayList<HashMap<String, String>> hm139 = (ArrayList<HashMap<String, String>>) this.jdbcTemplate.query(
                sql_to_query,
                new Object[]{}, new RowMapper(){
                    @Override
                    public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                        HashMap<String, String> row = new HashMap<String, String>();
                        row.put("codigo",rs.getString("codigo"));
                        row.put("descripcion",rs.getString("descripcion"));
                        row.put("unidad",rs.getString("unidad"));
                        row.put("no_dec",String.valueOf(rs.getInt("no_dec")));
                        row.put("idpres",rs.getString("idpres"));
                        row.put("presentacion",rs.getString("presentacion"));
                        row.put("exis_pres",StringHelper.roundDouble(rs.getDouble("exis_pres"),rs.getInt("no_dec")));
                        row.put("exis_uni",StringHelper.roundDouble(rs.getDouble("exis_uni"),rs.getInt("no_dec")));
                        return row;
                    }
                }
            );
            data=hm139;
        }

        
        
        
        
        //Obtiene lista de productos para descargar formato, para la aplicacion de Carga de Inventario Fisico
        if(id_app==178){
            
            String tr[] = campos_data.split("___");
                
            String tipoReporte = tr[9];
            
            //Formato para carga de Inventario
            if(tipoReporte.equals("1")){
                
                sql_to_query = "select * from inv_reporte('"+campos_data+"')as foo("
                                            + "id_prod integer,"
                                            +"codigo_producto character varying, "
                                            +"descripcion_producto character varying, "
                                            +"unidad character varying, "
                                            +"tipo_producto character varying, "
                                            +"no_almacen integer,"
                                            +"almacen character varying, "
                                            +"familia character varying, "
                                            +"subfamilia character varying, "
                                            +"linea character varying, "
                                            +"marca character varying, "
                                            +"existencia double precision "
                                        +") ORDER BY no_almacen, codigo_producto ASC;";


                //System.out.println("InvReporte: "+sql_to_query);

                ArrayList<HashMap<String, String>> hm178 = (ArrayList<HashMap<String, String>>) this.jdbcTemplate.query(
                    sql_to_query,
                    new Object[]{}, new RowMapper(){
                        @Override
                        public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                            HashMap<String, String> row = new HashMap<String, String>();
                            row.put("id_prod",String.valueOf(rs.getInt("id_prod")));
                            row.put("codigo_producto",rs.getString("codigo_producto"));
                            row.put("descripcion_producto",rs.getString("descripcion_producto"));
                            row.put("unidad",rs.getString("unidad"));
                            row.put("tipo_producto",rs.getString("tipo_producto"));
                            row.put("no_almacen",String.valueOf(rs.getInt("no_almacen")));
                            row.put("almacen",rs.getString("almacen"));
                            row.put("familia",rs.getString("familia"));
                            row.put("subfamilia",rs.getString("subfamilia"));
                            row.put("linea",rs.getString("linea"));
                            row.put("marca",rs.getString("marca"));
                            row.put("existencia",StringHelper.roundDouble(rs.getDouble("existencia"),4));
                            
                            return row;
                        }
                    }
                );
                data=hm178;
            }
            
                
            
            //Formato para carga de LOTES
            if(tipoReporte.equals("2")){
                
                sql_to_query = "select * from inv_reporte('"+campos_data+"')as foo(no_prod integer, codigo_producto character varying, descripcion_producto character varying, unidad character varying, no_almacen integer, almacen character varying, lote_interno character varying, lote_proveedor character varying, existencia double precision, tipo_producto character varying, familia character varying, subfamilia character varying, linea character varying, marca character varying ) ORDER BY no_almacen, codigo_producto ASC;";

                //System.out.println("InvReporte: "+sql_to_query);

                ArrayList<HashMap<String, String>> hm178 = (ArrayList<HashMap<String, String>>) this.jdbcTemplate.query(
                    sql_to_query,
                    new Object[]{}, new RowMapper(){
                        @Override
                        public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                            HashMap<String, String> row = new HashMap<String, String>();
                            row.put("no_prod",String.valueOf(rs.getInt("no_prod")));
                            row.put("codigo_producto",rs.getString("codigo_producto"));
                            row.put("descripcion_producto",rs.getString("descripcion_producto"));
                            row.put("unidad",rs.getString("unidad"));
                            row.put("no_almacen",String.valueOf(rs.getInt("no_almacen")));
                            row.put("almacen",rs.getString("almacen"));
                            row.put("lote_interno",rs.getString("lote_interno"));
                            row.put("lote_proveedor",rs.getString("lote_proveedor"));
                            row.put("existencia",StringHelper.roundDouble(rs.getDouble("existencia"),4));
                            row.put("tipo_producto",rs.getString("tipo_producto"));
                            row.put("familia",rs.getString("familia"));
                            row.put("subfamilia",rs.getString("subfamilia"));
                            row.put("linea",rs.getString("linea"));
                            row.put("marca",rs.getString("marca"));
                            
                            return row;
                        }
                    }
                );
                data=hm178;
            }
        }

        
        
        return data;
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
    public ArrayList<HashMap<String, String>> getAllTiposMovimientoInventario(Integer id_empresa) {
        String sql_to_query = "SELECT id, titulo FROM inv_mov_tipos order by titulo;";;

        ArrayList<HashMap<String, String>> pais = (ArrayList<HashMap<String, String>>) this.jdbcTemplate.query(
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




    @Override
    public ArrayList<HashMap<String, Object>> getProductos_PaginaGrid(String data_string, int offset, int pageSize, String orderBy, String asc) {
        String sql_busqueda = "SELECT id FROM gral_bus_catalogos(?) AS foo (id integer)";

	String sql_to_query = "SELECT inv_prod.id, "
                                    + "inv_prod.sku, "
                                    + "(CASE WHEN inv_prod_unidades.titulo IS NULL THEN '' ELSE inv_prod_unidades.titulo END) as unidad, "
                                    + "inv_prod.descripcion, "
                                    + "inv_prod_tipos.titulo as tipo "
                            + "FROM inv_prod "
                            + "LEFT JOIN inv_prod_tipos ON inv_prod_tipos.id= inv_prod.tipo_de_producto_id "
                            + "LEFT JOIN inv_prod_unidades ON inv_prod_unidades.id=inv_prod.unidad_id "
                            +"JOIN ("+sql_busqueda+") as subt on subt.id=inv_prod.id "
                            +"order by "+orderBy+" "+asc+" limit ? OFFSET ?";

        //System.out.println("Busqueda GetPage: "+sql_to_query);
        ArrayList<HashMap<String, Object>> hm = (ArrayList<HashMap<String, Object>>) this.jdbcTemplate.query(
            sql_to_query,
            new Object[]{data_string, new Integer(pageSize),new Integer(offset)}, new RowMapper() {
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    HashMap<String, Object> row = new HashMap<String, Object>();
                    row.put("id",rs.getInt("id"));
                    row.put("sku",rs.getString("sku"));
                    row.put("unidad",rs.getString("unidad"));
                    row.put("descripcion",rs.getString("descripcion"));
                    row.put("tipo",rs.getString("tipo"));
                    return row;
                }
            }
        );
        return hm;
    }





    //obtiene detalles de un producto en especifico
    @Override
    public ArrayList<HashMap<String, String>> getProducto_Datos(Integer id_producto) {
        String sql_query=""
        + "SELECT "
                + "inv_prod.id, "
                + "inv_prod.sku, "
                + "inv_prod.descripcion, "
                + "inv_prod.codigo_barras, "
                + "inv_prod.tentrega, "
                + "inv_prod.inv_clas_id, "
                + "inv_prod.inv_stock_clasif_id, "
                + "inv_prod.estatus, "
                + "inv_prod.inv_prod_familia_id, "
                + "inv_prod.subfamilia_id, "
                + "inv_prod.inv_prod_grupo_id, "
                + "inv_prod.ieps, "
                + "(CASE WHEN meta_impuesto='exento' THEN 0 WHEN meta_impuesto='iva_1' THEN 1 WHEN meta_impuesto='tasa_cero' THEN 2 ELSE 0 END) AS meta_impuesto, "
                + "inv_prod.gral_impto_id, "
                + "inv_prod.inv_prod_linea_id, "
                + "inv_prod.inv_mar_id, "
                + "inv_prod.tipo_de_producto_id, "
                + "inv_prod.inv_seccion_id, "
                + "inv_prod.unidad_id, "
                + "inv_prod.requiere_numero_serie, "
                + "inv_prod.requiere_numero_lote, "
                + "inv_prod.requiere_pedimento, "
                + "inv_prod.permitir_stock, "
                + "inv_prod.venta_moneda_extranjera,"
                + "inv_prod.compra_moneda_extranjera, "
                + "inv_prod.requiere_nom, "
                + "inv_prod.cxp_prov_id,"
                + "cxp_prov.razon_social as proveedor, "
                + "inv_prod.densidad, "
                + "inv_prod.valor_maximo, "
                + "inv_prod.valor_minimo, "
                + "inv_prod.punto_reorden, "
                + "inv_prod.descripcion_corta, "
                + "inv_prod.descripcion_larga, "
                + "inv_prod.archivo_img, "
                + "inv_prod.archivo_pdf,"
                + "inv_prod.inv_prod_presentacion_id AS presentacion_id, "
                + "inv_prod.flete,"
                + "inv_prod.no_clie, "
                + "inv_prod.gral_mon_id as mon_id,"
                + "inv_prod.gral_imptos_ret_id as impto_ret_id, "
                + "get_clave_cfdi_claveprodserv(inv_prod.cfdi_prodserv_id) as clave_cfdi_claveprodserv "
        + "FROM inv_prod  "
        + "LEFT JOIN cxp_prov ON cxp_prov.id=inv_prod.cxp_prov_id "
        + "WHERE inv_prod.id=?;";

	System.out.println("getProducto_Datos: "+sql_query);
        ArrayList<HashMap<String, String>> hm_producto = (ArrayList<HashMap<String, String>>) this.jdbcTemplate.query(
            sql_query,
            new Object[]{new Integer(id_producto)}, new RowMapper() {
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    HashMap<String, String> row = new HashMap<String, String>();
                    row.put("id",rs.getString("id"));
                    row.put("sku",rs.getString("sku"));
                    row.put("descripcion",rs.getString("descripcion"));
                    row.put("codigo_barras",rs.getString("codigo_barras"));
                    row.put("tentrega",String.valueOf(rs.getInt("tentrega")));
                    row.put("inv_clas_id",String.valueOf(rs.getInt("inv_clas_id")));
                    row.put("inv_stock_clasif_id",String.valueOf(rs.getInt("inv_stock_clasif_id")));
                    row.put("inv_prod_familia_id",String.valueOf(rs.getInt("inv_prod_familia_id")));
                    row.put("subfamilia_id",String.valueOf(rs.getInt("subfamilia_id")));
                    row.put("inv_prod_grupo_id",String.valueOf(rs.getInt("inv_prod_grupo_id")));
                    row.put("ieps",String.valueOf(rs.getInt("ieps")));
                    row.put("meta_impuesto",String.valueOf(rs.getInt("meta_impuesto")));
                    row.put("id_impuesto",rs.getString("gral_impto_id"));
                    row.put("inv_prod_linea_id",String.valueOf(rs.getInt("inv_prod_linea_id")));
                    row.put("inv_mar_id",String.valueOf(rs.getInt("inv_mar_id")));
                    row.put("tipo_de_producto_id",String.valueOf(rs.getInt("tipo_de_producto_id")));
                    row.put("inv_seccion_id",String.valueOf(rs.getInt("inv_seccion_id")));
                    row.put("unidad_id",String.valueOf(rs.getInt("unidad_id")));
                    row.put("cxp_prov_id",String.valueOf(rs.getInt("cxp_prov_id")));
                    row.put("requiere_numero_serie",String.valueOf(rs.getBoolean("requiere_numero_serie")));
                    row.put("requiere_numero_lote",String.valueOf(rs.getBoolean("requiere_numero_lote")));
                    row.put("requiere_pedimento",String.valueOf(rs.getBoolean("requiere_pedimento")));
                    row.put("permitir_stock",String.valueOf(rs.getBoolean("permitir_stock")));
                    row.put("venta_moneda_extranjera",String.valueOf(rs.getBoolean("venta_moneda_extranjera")));
                    row.put("compra_moneda_extranjera",String.valueOf(rs.getBoolean("compra_moneda_extranjera")));
                    row.put("requiere_nom",String.valueOf(rs.getBoolean("requiere_nom")));
                    row.put("estatus",String.valueOf(rs.getBoolean("estatus")));
                    row.put("proveedor",rs.getString("proveedor"));
                    row.put("densidad",StringHelper.roundDouble(rs.getString("densidad"),4));
                    row.put("valor_maximo",StringHelper.roundDouble(rs.getDouble("valor_maximo"),2));
                    row.put("valor_minimo",StringHelper.roundDouble(rs.getDouble("valor_minimo"),2));
                    row.put("punto_reorden",StringHelper.roundDouble(rs.getDouble("punto_reorden"),2));
                    row.put("descripcion_corta",rs.getString("descripcion_corta"));
                    row.put("descripcion_larga",rs.getString("descripcion_larga"));
                    row.put("archivo_img",rs.getString("archivo_img"));
                    row.put("archivo_pdf",rs.getString("archivo_pdf"));
                    row.put("presentacion_id",String.valueOf(rs.getInt("presentacion_id")));
                    row.put("flete",String.valueOf(rs.getBoolean("flete")));
                    row.put("no_clie",rs.getString("no_clie"));
                    row.put("mon_id",String.valueOf(rs.getInt("mon_id")));
                    row.put("impto_ret_id",String.valueOf(rs.getInt("impto_ret_id")));
                    row.put("clave_cfdi_claveprodserv",String.valueOf(rs.getInt("clave_cfdi_claveprodserv")));
                                        
                    return row;
                }
            }
        );
        return hm_producto;
    }




    //obtiene datos de Configuracion de Cuentas Contables para Productos
    @Override
    public ArrayList<HashMap<String, String>> getProducto_DatosContabilidad(Integer id_producto) {
        String sql_query=""
                + "SELECT "
                        + "inv_prod.id, "
                        + "(CASE WHEN tbl_cta_gasto.id IS NULL THEN 0 ELSE tbl_cta_gasto.id END) AS gas_id_cta, "
                        + "(CASE WHEN tbl_cta_gasto.cta IS NULL OR tbl_cta_gasto.cta=0 THEN '' ELSE tbl_cta_gasto.cta::character varying END) AS gas_cta, "
                        + "(CASE WHEN tbl_cta_gasto.subcta IS NULL OR tbl_cta_gasto.subcta=0 THEN '' ELSE tbl_cta_gasto.subcta::character varying END) AS gas_subcta, "
                        + "(CASE WHEN tbl_cta_gasto.ssubcta IS NULL OR tbl_cta_gasto.ssubcta=0 THEN '' ELSE tbl_cta_gasto.ssubcta::character varying END) AS gas_ssubcta, "
                        + "(CASE WHEN tbl_cta_gasto.sssubcta IS NULL OR tbl_cta_gasto.sssubcta=0 THEN '' ELSE tbl_cta_gasto.sssubcta::character varying END) AS gas_sssubcta,"
                        + "(CASE WHEN tbl_cta_gasto.ssssubcta IS NULL OR tbl_cta_gasto.ssssubcta=0 THEN '' ELSE tbl_cta_gasto.ssssubcta::character varying END) AS gas_ssssubcta, "
                        + "(CASE WHEN tbl_cta_gasto.descripcion IS NULL OR tbl_cta_gasto.descripcion='' THEN  (CASE WHEN tbl_cta_gasto.descripcion_ing IS NULL OR tbl_cta_gasto.descripcion_ing='' THEN  (CASE WHEN tbl_cta_gasto.descripcion_otr IS NULL OR tbl_cta_gasto.descripcion_otr='' THEN '' ELSE tbl_cta_gasto.descripcion_otr END) ELSE tbl_cta_gasto.descripcion_ing END )  ELSE tbl_cta_gasto.descripcion END ) AS gas_descripcion, "
                        + "(CASE WHEN tbl_cta_costo_vent.id IS NULL THEN 0 ELSE  tbl_cta_costo_vent.id END ) AS costvent_id_cta, "
                        + "(CASE WHEN tbl_cta_costo_vent.cta IS NULL OR tbl_cta_costo_vent.cta=0 THEN '' ELSE tbl_cta_costo_vent.cta::character varying END ) AS costvent_cta, "
                        + "(CASE WHEN tbl_cta_costo_vent.subcta IS NULL OR tbl_cta_costo_vent.subcta=0 THEN '' ELSE tbl_cta_costo_vent.subcta::character varying END ) AS costvent_subcta, "
                        + "(CASE WHEN tbl_cta_costo_vent.ssubcta IS NULL OR tbl_cta_costo_vent.ssubcta=0 THEN '' ELSE tbl_cta_costo_vent.ssubcta::character varying END )  AS costvent_ssubcta, "
                        + "(CASE WHEN tbl_cta_costo_vent.sssubcta IS NULL OR tbl_cta_costo_vent.sssubcta=0 THEN '' ELSE tbl_cta_costo_vent.sssubcta::character varying END ) AS costvent_sssubcta,"
                        + "(CASE WHEN tbl_cta_costo_vent.ssssubcta IS NULL OR tbl_cta_costo_vent.ssssubcta=0 THEN '' ELSE tbl_cta_costo_vent.ssssubcta::character varying END) AS costvent_ssssubcta, "
                        + "(CASE WHEN tbl_cta_costo_vent.descripcion IS NULL OR tbl_cta_costo_vent.descripcion='' THEN  (CASE WHEN tbl_cta_costo_vent.descripcion_ing IS NULL OR tbl_cta_costo_vent.descripcion_ing='' THEN  (CASE WHEN tbl_cta_costo_vent.descripcion_otr IS NULL OR tbl_cta_costo_vent.descripcion_otr='' THEN '' ELSE tbl_cta_costo_vent.descripcion_otr END) ELSE tbl_cta_costo_vent.descripcion_ing END )  ELSE tbl_cta_costo_vent.descripcion END ) AS costvent_descripcion, "
                        + "(CASE WHEN tbl_cta_venta.id IS NULL THEN 0 ELSE tbl_cta_venta.id END) AS vent_id_cta, "
                        + "(CASE WHEN tbl_cta_venta.cta IS NULL OR tbl_cta_venta.cta=0 THEN '' ELSE tbl_cta_venta.cta::character varying END) AS vent_cta, "
                        + "(CASE WHEN tbl_cta_venta.subcta IS NULL OR tbl_cta_venta.subcta=0 THEN '' ELSE tbl_cta_venta.subcta::character varying END) AS vent_subcta, "
                        + "(CASE WHEN tbl_cta_venta.ssubcta IS NULL OR tbl_cta_venta.ssubcta=0 THEN '' ELSE tbl_cta_venta.ssubcta::character varying END) AS vent_ssubcta, "
                        + "(CASE WHEN tbl_cta_venta.sssubcta IS NULL OR tbl_cta_venta.sssubcta=0 THEN '' ELSE tbl_cta_venta.sssubcta::character varying END) AS vent_sssubcta,"
                        + "(CASE WHEN tbl_cta_venta.ssssubcta IS NULL OR tbl_cta_venta.ssssubcta=0 THEN '' ELSE tbl_cta_venta.ssssubcta::character varying END) AS vent_ssssubcta, "
                        + "(CASE WHEN tbl_cta_venta.descripcion IS NULL OR tbl_cta_venta.descripcion='' THEN  (CASE WHEN tbl_cta_venta.descripcion_ing IS NULL OR tbl_cta_venta.descripcion_ing='' THEN  (CASE WHEN tbl_cta_venta.descripcion_otr IS NULL OR tbl_cta_venta.descripcion_otr='' THEN '' ELSE tbl_cta_venta.descripcion_otr END) ELSE tbl_cta_venta.descripcion_ing END )  ELSE tbl_cta_venta.descripcion END ) AS vent_descripcion "
                + "FROM inv_prod  "
                + "LEFT JOIN ctb_cta AS tbl_cta_gasto ON tbl_cta_gasto.id=inv_prod.ctb_cta_id_gasto "
                + "LEFT JOIN ctb_cta AS tbl_cta_costo_vent ON tbl_cta_costo_vent.id=inv_prod.ctb_cta_id_costo_venta "
                + "LEFT JOIN ctb_cta AS tbl_cta_venta ON tbl_cta_venta.id=inv_prod.ctb_cta_id_venta "
                + "WHERE inv_prod.id=?";

	//System.out.println("getProducto_DatosContabilidad: "+sql_query);
        ArrayList<HashMap<String, String>> contab = (ArrayList<HashMap<String, String>>) this.jdbcTemplate.query(
            sql_query,
            new Object[]{new Integer(id_producto)}, new RowMapper() {
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    HashMap<String, String> row = new HashMap<String, String>();
                    row.put("id",rs.getString("id"));
                    row.put("gas_id_cta",rs.getString("gas_id_cta"));
                    row.put("gas_cta",rs.getString("gas_cta"));
                    row.put("gas_subcta",rs.getString("gas_subcta"));
                    row.put("gas_ssubcta",rs.getString("gas_ssubcta"));
                    row.put("gas_sssubcta",rs.getString("gas_sssubcta"));
                    row.put("gas_ssssubcta",rs.getString("gas_ssssubcta"));
                    row.put("gas_descripcion",rs.getString("gas_descripcion"));

                    row.put("costvent_id_cta",rs.getString("costvent_id_cta"));
                    row.put("costvent_cta",rs.getString("costvent_cta"));
                    row.put("costvent_subcta",rs.getString("costvent_subcta"));
                    row.put("costvent_ssubcta",rs.getString("costvent_ssubcta"));
                    row.put("costvent_sssubcta",rs.getString("costvent_sssubcta"));
                    row.put("costvent_ssssubcta",rs.getString("costvent_ssssubcta"));
                    row.put("costvent_descripcion",rs.getString("costvent_descripcion"));

                    row.put("vent_id_cta",rs.getString("vent_id_cta"));
                    row.put("vent_cta",rs.getString("vent_cta"));
                    row.put("vent_subcta",rs.getString("vent_subcta"));
                    row.put("vent_ssubcta",rs.getString("vent_ssubcta"));
                    row.put("vent_sssubcta",rs.getString("vent_sssubcta"));
                    row.put("vent_ssssubcta",rs.getString("vent_ssssubcta"));
                    row.put("vent_descripcion",rs.getString("vent_descripcion"));

                    return row;
                }
            }
        );
        return contab;
    }


    @Override
    public ArrayList<String[]> getClaveProdServSuggestions(String searchTerm) {

        String sql_query;

        if (searchTerm.matches("[0-9]+")) {
            sql_query = "SELECT clave, descripcion "
                       + " FROM cfdi_claveprodserv "
                       + "WHERE clave like '%" + searchTerm + "%' "
                       + "ORDER BY id ASC "
                       + "LIMIT 50";
        } else {
            sql_query = "SELECT clave, descripcion "
                       + " FROM cfdi_claveprodserv "
                       + "WHERE descripcion ilike '%" + searchTerm + "%' "
                       + "ORDER BY id ASC "
                       + "LIMIT 50";
        }

        ArrayList<String[]> al = (ArrayList<String[]>) this.jdbcTemplate.query(
            sql_query,
            new Object[]{},
            new RowMapper() {
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    String[] arr = {rs.getString("clave"), rs.getString("descripcion")};
                    return arr;
                }
            }
        );
        return al;
    }


    //obtiene tipos de productos
    @Override
    public ArrayList<HashMap<String, String>> getProducto_Tipos() {
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

    
    
    
    //Tipo de producto inventariable 
    @Override
    public ArrayList<HashMap<String, String>> getProducto_TiposInventariable() {
	String sql_query = "SELECT DISTINCT id,titulo FROM inv_prod_tipos WHERE borrado_logico=false and id!=3 and id!=4 order by titulo ASC;";
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
    


    //obtiene lineas de productos
    @Override
    public ArrayList<HashMap<String, String>> getProducto_Lineas(Integer id_empresa) {
	String sql_query = "select id,titulo from inv_prod_lineas where borrado_logico=false AND gral_emp_id="+ id_empresa +" order by titulo;";
        //System.out.println("Buscando lineas:"+sql_query);

        ArrayList<HashMap<String, String>> hm_lineas = (ArrayList<HashMap<String, String>>) this.jdbcTemplate.query(
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
        return hm_lineas;
    }




    //obtiene marcas de productos
    @Override
    public ArrayList<HashMap<String, String>> getProducto_Marcas(Integer id_empresa) {
	String sql_query = "SELECT id, titulo FROM inv_mar WHERE borrado_logico=false AND gral_emp_id="+ id_empresa +" order by titulo;";

        ArrayList<HashMap<String, String>> hm_lineas = (ArrayList<HashMap<String, String>>) this.jdbcTemplate.query(
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
        return hm_lineas;
    }



    //obtiene grupos de productos
    @Override
    public ArrayList<HashMap<String, String>> getProducto_Grupos(Integer id_empresa) {
	String sql_query = "SELECT id, titulo FROM inv_prod_grupos WHERE borrado_logico=false AND gral_emp_id="+ id_empresa +" order by titulo;";

        ArrayList<HashMap<String, String>> hm_lineas = (ArrayList<HashMap<String, String>>) this.jdbcTemplate.query(
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
        return hm_lineas;
    }


    //Obtener todas las subfamilias, para select de catalogo de productos
    @Override
    public ArrayList<HashMap<String, String>> getProducto_Subfamilias(Integer id_empresa) {
        String sql_query = "SELECT id, titulo, descripcion FROM inv_prod_familias WHERE borrado_logico=FALSE AND id != identificador_familia_padre AND gral_emp_id="+id_empresa +" order by titulo;";
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




    //Obtener todas las clasificaciones stock para el catalogo de productos
    @Override
    public ArrayList<HashMap<String, String>> getProducto_ClasificacionStock(Integer id_empresa) {
        String sql_query = "SELECT id, titulo FROM inv_stock_clasificaciones WHERE borrado_logico=FALSE AND gral_emp_id="+id_empresa +" order by titulo;";
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




    //Obtener todas las Clases para el catalogo de productos
    @Override
    public ArrayList<HashMap<String, String>> getProducto_Clases(Integer id_empresa) {
        String sql_query = "SELECT id, titulo FROM inv_clas WHERE borrado_logico=FALSE AND gral_emp_id="+id_empresa +" order by titulo;";
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



    //obtiene los ingredientes de un producto en especifico
    @Override
    public ArrayList<HashMap<String, String>> getProducto_Ingredientes(Integer id_producto) {
	String sql_query = "SELECT  inv_kit.producto_elemento_id as producto_ingrediente_id, "
                                + "inv_prod.sku, "
                                + "inv_prod.descripcion as titulo, "
                                + " inv_kit.cantidad,"
                                + " inv_prod_unidades.decimales "
                            + "FROM  inv_kit "
                            + "JOIN inv_prod on inv_prod.id =  inv_kit.producto_elemento_id "
                            + "JOIN inv_prod_unidades ON inv_prod_unidades.id=inv_prod.unidad_id"
                            + " WHERE  inv_kit.producto_kit_id="+id_producto;
        //System.out.println("sql_query: "+sql_query);
        ArrayList<HashMap<String, String>> hm_ingrediente = (ArrayList<HashMap<String, String>>) this.jdbcTemplate.query(
            sql_query,
            new Object[]{}, new RowMapper() {
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    HashMap<String, String> row = new HashMap<String, String>();
                    row.put("producto_ingrediente_id",rs.getString("producto_ingrediente_id"));
                    row.put("sku",rs.getString("sku"));
                    row.put("titulo",rs.getString("titulo"));
                    row.put("cantidad",StringHelper.roundDouble(rs.getDouble("cantidad"),rs.getInt("decimales")));
                    row.put("decimales",String.valueOf(rs.getInt("decimales")));
                    return row;
                }
            }
        );
        return hm_ingrediente;
    }
    
    
    //Obtiene todas las presentaciones asignadas a un producto en especifico
    @Override
    public ArrayList<HashMap<String, String>> getProducto_PresentacionesON(Integer id_producto) {
        String sql_query = "SELECT id,titulo, cantidad FROM inv_prod_presentaciones WHERE id IN (SELECT presentacion_id FROM  inv_prod_pres_x_prod WHERE producto_id = "+id_producto+") order by titulo;";
        
        ArrayList<HashMap<String, String>> hm_pres_on = (ArrayList<HashMap<String, String>>) this.jdbcTemplate.query(
            sql_query,
            new Object[]{}, new RowMapper() {
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    HashMap<String, String> row = new HashMap<String, String>();
                    row.put("id",rs.getString("id"));
                    row.put("titulo",rs.getString("titulo"));
                    row.put("equiv",StringHelper.roundDouble(rs.getString("cantidad"),2));
                    return row;
                }
            }
        );
        return hm_pres_on;
    }




    @Override
    public ArrayList<HashMap<String, String>> getProducto_CuentasMayor(Integer id_empresa) {
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



    //obtiene las unidades
    @Override
    public ArrayList<HashMap<String, String>> getProducto_Unidades() {
	String sql_query = "SELECT id,titulo,decimales from inv_prod_unidades where borrado_logico=false order by titulo;";

        ArrayList<HashMap<String, String>> hm_unidades = (ArrayList<HashMap<String, String>>) this.jdbcTemplate.query(
            sql_query,
            new Object[]{}, new RowMapper() {
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    HashMap<String, String> row = new HashMap<String, String>();
                    row.put("id",rs.getString("id"));
                    row.put("titulo",rs.getString("titulo"));
                    row.put("no_dec",String.valueOf(rs.getInt("decimales")));
                    return row;
                }
            }
        );
        return hm_unidades;
    }




    //obtiene las presentaciones de un producto
    //cuando id=0, obtiene todas las presentaciones
    //cuando id es diferente de 0, obtiene las presentaciones no asignadas a un producto en especifico
    @Override
    public ArrayList<HashMap<String, String>> getProducto_Presentaciones(Integer id_producto) {
        String sql_query="";
        if(id_producto != 0){
            sql_query = "SELECT id,titulo,(CASE WHEN cantidad IS NULL THEN 0 ELSE cantidad END) AS cantidad FROM inv_prod_presentaciones WHERE id NOT IN (SELECT presentacion_id FROM  inv_prod_pres_x_prod WHERE producto_id = "+id_producto+") order by titulo;";
        }else{
            sql_query = "SELECT id,titulo, (CASE WHEN cantidad IS NULL THEN 0 ELSE cantidad END) AS cantidad FROM inv_prod_presentaciones WHERE borrado_logico=FALSE order by titulo;";
        }
        //System.out.println("sql_query: "+sql_query);
        ArrayList<HashMap<String, String>> hm_pres= (ArrayList<HashMap<String, String>>) this.jdbcTemplate.query(
            sql_query,
            new Object[]{}, new RowMapper() {
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    HashMap<String, String> row = new HashMap<String, String>();
                    row.put("id",rs.getString("id"));
                    row.put("titulo",rs.getString("titulo"));
                    row.put("equiv",StringHelper.roundDouble(rs.getString("cantidad"),2));
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




    //metodo para el buscador de cuentas contables
    @Override
    public ArrayList<HashMap<String, String>> getProducto_CuentasContables(Integer cta_mayor, Integer detalle, String clasifica, String cta, String scta, String sscta, String ssscta, String sssscta, String descripcion, Integer id_empresa) {

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


        //System.out.println("sql_query: "+sql_query);

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
    public ArrayList<HashMap<String, Object>> getEntradas_PaginaGrid(String data_string, int offset, int pageSize, String orderBy, String asc) {
        String sql_busqueda = "SELECT id FROM gral_bus_catalogos(?) AS foo (id integer)";

	String sql_to_query = "SELECT com_fac.id, "
				+"com_fac.no_entrada as folio, "
				+"cxp_prov.razon_social as proveedor, "
				+"(CASE WHEN com_fac.orden_de_compra IS NULL THEN '' ELSE com_fac.orden_de_compra END ) AS orden_compra, "
                                +"(CASE WHEN com_fac.tipo_documento=1 THEN 'Factura' ELSE 'Remisi&oacute;n' END ) AS tipo_doc, "
				+"com_fac.factura, "
				+"to_char(com_fac.factura_fecha_expedicion,'dd/mm/yyyy') as fecha_factura, "
				+"to_char(com_fac.momento_creacion,'dd/mm/yyyy') as fecha_entrada, "
                                +"(CASE WHEN com_fac.cancelacion=FALSE THEN '' ELSE 'CANCELADO' END) AS estado "
			+"FROM com_fac "
			+"LEFT JOIN cxp_prov on cxp_prov.id=com_fac.proveedor_id "
                        +"JOIN ("+sql_busqueda+") as subt on subt.id=com_fac.id "
                        +"ORDER BY "+orderBy+" "+asc+" LIMIT ? OFFSET ?";

        //System.out.println("Busqueda GetPage: "+sql_to_query);

        //log.log(Level.INFO, "Ejecutando query de {0}", sql_to_query);
        ArrayList<HashMap<String, Object>> hm = (ArrayList<HashMap<String, Object>>) this.jdbcTemplate.query(
            sql_to_query,
            new Object[]{data_string,new Integer(pageSize),new Integer(offset)}, new RowMapper() {
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    HashMap<String, Object> row = new HashMap<String, Object>();
                    row.put("id",rs.getString("id"));
                    row.put("folio",rs.getString("folio"));
                    row.put("proveedor",rs.getString("proveedor"));
                    row.put("orden_compra",rs.getString("orden_compra"));
                    row.put("tipo_doc",rs.getString("tipo_doc"));
                    row.put("factura",rs.getString("factura"));
                    row.put("fecha_factura",rs.getString("fecha_factura"));
                    row.put("fecha_entrada",rs.getString("fecha_entrada"));
                    row.put("estado",rs.getString("estado"));
                    return row;
                }
            }
        );
        return hm;
    }




    @Override
    public ArrayList<HashMap<String, String>> getEntrada_Datos(Integer id) {
        String sql_to_query = ""
        + "SELECT com_fac.id,"
                +"com_fac.no_entrada,"
                +"com_fac.proveedor_id,"
                +"com_fac.factura,"
                +"com_fac.factura_fecha_expedicion AS expedicion,"
                +"to_char(com_fac.factura_fecha_expedicion,'dd/mm/yyyy') AS fecha_fac,"//este es solo para el pdf
                +"to_char(com_fac.momento_creacion,'dd/mm/yyyy') AS fecha_entrada,"
                +"com_fac.moneda_id,"
                + "gral_mon.descripcion AS moneda,"
                + "gral_mon.descripcion_abr AS moneda_abr,"
                + "gral_mon.simbolo AS moneda_simbolo, "
                +"com_fac.tipo_de_cambio as tipo_cambio,"
                +"com_fac.fletera_id,"
                +"com_fac.numero_guia,"
                +"com_fac.orden_de_compra as orden_compra,"
                +"com_fac.observaciones,"
                +"com_fac.flete,"
                +"(CASE WHEN com_fac.cancelacion=FALSE THEN '' ELSE 'CANCELADO' END) AS estado, "
                +"com_fac.inv_alm_id as almacen_destino,"
                +"com_fac.tipo_documento, "
                + "com_fac.subtotal, "
                + "com_fac.iva, "
                + "com_fac.retencion, "
                + "com_fac.total, "
                + "com_fac.monto_ieps, "
                + "com_fac.lab_destino "
        +"FROM com_fac "
        + "LEFT JOIN gral_mon ON gral_mon.id=com_fac.moneda_id "
        +"where com_fac.id="+ id + ";";
        
        //System.out.println("sql_to_query: "+sql_to_query);
        ArrayList<HashMap<String, String>> hm_datos_entrada = (ArrayList<HashMap<String, String>>) this.jdbcTemplate.query(
            sql_to_query,
            new Object[]{}, new RowMapper(){
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    HashMap<String, String> row = new HashMap<String, String>();
                    row.put("id",rs.getString("id"));
                    row.put("no_entrada",rs.getString("no_entrada"));
                    row.put("proveedor_id",rs.getString("proveedor_id"));
                    row.put("factura",rs.getString("factura"));
                    row.put("expedicion",rs.getString("expedicion"));
                    row.put("fecha_fac",rs.getString("fecha_fac"));
                    row.put("fecha_entrada",rs.getString("fecha_entrada"));
                    row.put("moneda_id",rs.getString("moneda_id"));
                    row.put("moneda",rs.getString("moneda"));
                    row.put("moneda_abr",rs.getString("moneda_abr"));
                    row.put("moneda_simbolo",rs.getString("moneda_simbolo"));
                    row.put("tipo_cambio",StringHelper.roundDouble(rs.getString("tipo_cambio"),4));
                    row.put("fletera_id",rs.getString("fletera_id"));
                    row.put("numero_guia",rs.getString("numero_guia"));
                    row.put("orden_compra",rs.getString("orden_compra"));
                    row.put("observaciones",rs.getString("observaciones"));
                    row.put("flete",StringHelper.roundDouble(rs.getString("flete"),2));
                    row.put("estado",rs.getString("estado"));
                    row.put("almacen_destino",rs.getString("almacen_destino"));
                    row.put("tipo_documento",rs.getString("tipo_documento"));
                    row.put("subtotal",StringHelper.roundDouble(rs.getString("subtotal"),2));
                    row.put("iva",StringHelper.roundDouble(rs.getString("iva"),2));
                    row.put("retencion",StringHelper.roundDouble(rs.getString("retencion"),2));
                    row.put("total",StringHelper.roundDouble(rs.getString("total"),2));
                    row.put("monto_ieps",StringHelper.roundDouble(rs.getString("monto_ieps"),2));
                    row.put("lab_destino",String.valueOf(rs.getBoolean("lab_destino")));
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
                                    + "cxp_prov.folio AS no_proveedor, "
                                    + "cxp_prov.razon_social, "
                                    + "cxp_prov.calle||' '||cxp_prov.numero||', '||cxp_prov.colonia||', '||(case when gral_mun.titulo is null then '' else gral_mun.titulo end)||', '||(case when gral_edo.titulo is null then '' else gral_edo.titulo end)||', '||(case when gral_pais.titulo is null then '' else gral_pais.titulo end)||' C.P. '||cxp_prov.cp as direccion, "
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
                            + "left JOIN gral_pais ON gral_pais.id = cxp_prov.pais_id "
                            + "left JOIN gral_edo ON gral_edo.id = cxp_prov.estado_id "
                            + "left JOIN gral_mun ON gral_mun.id = cxp_prov.municipio_id "
                            + "WHERE cxp_prov.id="+ id_proveedor;

        //System.out.println("sql_to_query:"+sql_to_query);
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



    @Override
    public ArrayList<HashMap<String, String>> getEntradas_DatosGrid(Integer id) {
        String sql_to_query = ""
        + "SELECT "
            + "com_fac_detalle.producto_id, "
            + "inv_prod.sku, "
            + "inv_prod.descripcion AS titulo, "
            + "(CASE WHEN com_fac_detalle.gral_ieps_id>0 THEN ' - IEPS '||(round((com_fac_detalle.valor_ieps * 100::double precision)::numeric,2))||'%' ELSE '' END) AS etiqueta_ieps,"
            + "inv_prod_unidades.titulo as unidad, "
            + "inv_prod_presentaciones.id as id_presentacion, "
            + "inv_prod_presentaciones.titulo as presentacion, "
            + "com_fac_detalle.costo_unitario as costo, "
            + "com_fac_detalle.cantidad, "
            + "(com_fac_detalle.costo_unitario * com_fac_detalle.cantidad) as importe, "
            + "com_fac_detalle.tipo_de_impuesto_sobre_partida as tipo_impuesto, "
            + "com_fac_detalle.valor_imp, "
            + "inv_prod_unidades.decimales,"
            + "com_fac_detalle.gral_ieps_id,"
            + "com_fac_detalle.valor_ieps,"
            + "(CASE WHEN com_fac_detalle.gral_ieps_id>0 THEN ((com_fac_detalle.costo_unitario * com_fac_detalle.cantidad) * com_fac_detalle.valor_ieps) ELSE 0 END) AS importe_ieps, "
            + "(CASE WHEN com_orden_compra.folio IS NULL THEN ' ' ELSE com_orden_compra.folio END) AS folio_oc, "
            + "(CASE WHEN com_fac_oc.com_oc_det_id IS NULL THEN 0 ELSE com_fac_oc.com_oc_det_id END) AS id_det_oc "
        + "FROM com_fac_detalle "
        + "LEFT JOIN inv_prod on inv_prod.id = com_fac_detalle.producto_id "
        + "LEFT JOIN inv_prod_unidades on inv_prod_unidades.id = inv_prod.unidad_id "
        + "LEFT JOIN inv_prod_presentaciones on inv_prod_presentaciones.id = com_fac_detalle.presentacion_id "
        + "LEFT JOIN com_fac_oc ON com_fac_oc.com_fac_det_id=com_fac_detalle.id "
        + "LEFT JOIN com_orden_compra ON com_orden_compra.id=com_fac_oc.com_oc_id "
        + "WHERE com_fac_detalle.com_fac_id="+ id + " order by com_fac_detalle.id;";
        
        //log.log(Level.INFO, "Ejecutando query de {0}", sql_to_query);
        ArrayList<HashMap<String, String>> hm_datos_entrada = (ArrayList<HashMap<String, String>>) this.jdbcTemplate.query(
            sql_to_query,
            new Object[]{}, new RowMapper(){
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    HashMap<String, String> row = new HashMap<String, String>();
                    row.put("producto_id",rs.getString("producto_id"));
                    row.put("sku",rs.getString("sku"));
                    row.put("titulo",rs.getString("titulo"));
                    row.put("unidad",rs.getString("unidad"));
                    row.put("id_presentacion",rs.getString("id_presentacion"));
                    row.put("presentacion",rs.getString("presentacion"));
                    row.put("cantidad",StringHelper.roundDouble(rs.getString("cantidad"),Integer.parseInt(rs.getString("decimales"))));
                    row.put("costo",StringHelper.roundDouble(rs.getString("costo"),2));
                    row.put("importe",StringHelper.roundDouble(rs.getString("importe"),2));
                    row.put("tipo_impuesto",rs.getString("tipo_impuesto"));
                    row.put("valor_imp",StringHelper.roundDouble(rs.getString("valor_imp"),2));
                    row.put("decimales",rs.getString("decimales"));
                    row.put("ieps_id",rs.getString("gral_ieps_id"));
                    row.put("valor_ieps",StringHelper.roundDouble(rs.getString("valor_ieps"),4));
                    row.put("importe_ieps",StringHelper.roundDouble(rs.getString("importe_ieps"),4));
                    row.put("etiqueta_ieps",rs.getString("etiqueta_ieps"));
                    row.put("folio_oc",rs.getString("folio_oc"));
                    row.put("id_det_oc",rs.getString("id_det_oc"));
                    return row;
                }
            }
        );
        return hm_datos_entrada;
    }





    //OBTIENE DATOS DE LA ORDEN DE COMPRA
    @Override
    public ArrayList<HashMap<String, String>> getEntradas_DatosOrdenCompra(String orden_compra, Integer id_empresa) {
        String sql_to_query = ""
        + "SELECT "
            + "com_orden_compra.id,"
            + "com_orden_compra.proveedor_id,"
            + "cxp_prov.rfc, "
            + "cxp_prov.folio AS no_proveedor, "
            + "cxp_prov.razon_social, "
            + "cxp_prov.proveedortipo_id,"
            + "com_orden_compra.moneda_id,"
            + "com_orden_compra.tipo_cambio,"
            + "com_orden_compra.subtotal, "
            + "com_orden_compra.impuesto,"
            + "com_orden_compra.total, "
            + "com_orden_compra.lab_destino "
        + "FROM com_orden_compra "
        + "JOIN cxp_prov ON cxp_prov.id=com_orden_compra.proveedor_id "
        + "WHERE com_orden_compra.folio='"+ orden_compra + "'  "
        + "AND com_orden_compra.gral_emp_id="+ id_empresa + ""
        + "AND status IN (1,3);";
        //STATUS 0=Orden Generada, 1=Autorizado, 2=Cancelado, 3=Surtido Parcial, 4=Surtido Completo
        
        //log.log(Level.INFO, "Ejecutando query de {0}", sql_to_query);
        ArrayList<HashMap<String, String>> hm_datos_entrada = (ArrayList<HashMap<String, String>>) this.jdbcTemplate.query(
            sql_to_query,
            new Object[]{}, new RowMapper(){
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    HashMap<String, String> row = new HashMap<String, String>();
                    row.put("id",String.valueOf(rs.getInt("id")));
                    row.put("proveedor_id",String.valueOf(rs.getInt("proveedor_id")));
                    row.put("no_proveedor",rs.getString("no_proveedor"));
                    row.put("rfc",rs.getString("rfc"));
                    row.put("razon_social",rs.getString("razon_social"));
                    row.put("proveedortipo_id",String.valueOf(rs.getInt("proveedortipo_id")));
                    row.put("moneda_id",String.valueOf(rs.getInt("moneda_id")));
                    row.put("tc",StringHelper.roundDouble(rs.getString("tipo_cambio"),4));
                    row.put("subtotal",StringHelper.roundDouble(rs.getString("subtotal"),2));
                    row.put("impuesto",StringHelper.roundDouble(rs.getString("impuesto"),2));
                    row.put("total",StringHelper.roundDouble(rs.getString("total"),2));
                    row.put("lab_destino",String.valueOf(rs.getBoolean("lab_destino")));
                    return row;
                }
            }
        );
        return hm_datos_entrada;
    }




    //OBTENER DETALLES DE LA ORDEN DE COMPRA
    @Override
    public ArrayList<HashMap<String, String>> getEntradas_DetallesOrdenCompra(Integer id_orden_compra) {
        String sql_to_query = ""
        + "SELECT "
            + "com_orden_compra_detalle.com_orden_compra_id AS id_oc,"
            + "com_orden_compra_detalle.id AS id_det_oc,"
            + "com_orden_compra_detalle.inv_prod_id AS producto_id,"
            + "inv_prod.sku, "
            + "inv_prod.descripcion AS titulo, "
            + "inv_prod_unidades.titulo AS unidad, "
            + "inv_prod_presentaciones.id AS id_presentacion, "
            + "inv_prod_presentaciones.titulo AS presentacion, "
            + "com_orden_compra_detalle.precio_unitario AS costo, "
            + "com_orden_compra_detalle.cantidad - (CASE WHEN com_orden_compra_detalle.cant_surtido IS NULL THEN 0 ELSE com_orden_compra_detalle.cant_surtido END) AS cantidad, "
            + "(com_orden_compra_detalle.precio_unitario * com_orden_compra_detalle.cantidad) AS importe, "
            + "com_orden_compra_detalle.gral_imp_id AS tipo_impuesto, "
            + "com_orden_compra_detalle.valor_imp, "
            + "inv_prod_unidades.decimales, "
            + "(CASE WHEN inv_prod.ieps=0 THEN 0 ELSE gral_ieps.id END) AS ieps_id, "
            + "(CASE WHEN inv_prod.ieps=0 THEN 0 ELSE gral_ieps.tasa END) AS ieps_tasa, "
            + "(CASE WHEN com_orden_compra_detalle.cant_surtido IS NULL THEN 0 ELSE com_orden_compra_detalle.cant_surtido END) AS cant_surtido "
        + "FROM com_orden_compra_detalle "
        + "LEFT JOIN inv_prod on inv_prod.id=com_orden_compra_detalle.inv_prod_id "
        + "LEFT JOIN inv_prod_unidades on inv_prod_unidades.id=inv_prod.unidad_id "
        + "LEFT JOIN inv_prod_presentaciones on inv_prod_presentaciones.id=com_orden_compra_detalle.presentacion_id "
        + "LEFT JOIN gral_ieps ON gral_ieps.id=inv_prod.ieps "
        + "WHERE com_orden_compra_detalle.com_orden_compra_id="+ id_orden_compra +" "
        + "AND com_orden_compra_detalle.surtir=true "
        + "AND com_orden_compra_detalle.estatus IN (0,1) "
        + "AND (com_orden_compra_detalle.cantidad - (CASE WHEN com_orden_compra_detalle.cant_surtido IS NULL THEN 0 ELSE com_orden_compra_detalle.cant_surtido END))>0.0001 "
        + "order by com_orden_compra_detalle.id;";
        
        //0=Sin Estatus, 1=Parcial(Surtido Parcial), 2=Surtido(Surtido Completo), 3=Cancelado
        
        //System.out.println("sql_to_query: "+sql_to_query);
        //log.log(Level.INFO, "Ejecutando query de {0}", sql_to_query);
        ArrayList<HashMap<String, String>> hm = (ArrayList<HashMap<String, String>>) this.jdbcTemplate.query(
            sql_to_query,
            new Object[]{}, new RowMapper(){
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    HashMap<String, String> row = new HashMap<String, String>();
                    row.put("id_oc",String.valueOf(rs.getInt("id_oc")));
                    row.put("id_det_oc",String.valueOf(rs.getInt("id_det_oc")));
                    row.put("producto_id",String.valueOf(rs.getInt("producto_id")));
                    row.put("sku",rs.getString("sku"));
                    row.put("titulo",rs.getString("titulo"));
                    row.put("unidad",rs.getString("unidad"));
                    row.put("id_presentacion",String.valueOf(rs.getInt("id_presentacion")));
                    row.put("presentacion",rs.getString("presentacion"));
                    row.put("costo",StringHelper.roundDouble(rs.getString("costo"),2));
                    row.put("cantidad",StringHelper.roundDouble(rs.getString("cantidad"),2));
                    row.put("importe",StringHelper.roundDouble(rs.getString("importe"),4));
                    row.put("tipo_impuesto",String.valueOf(rs.getInt("tipo_impuesto")));
                    row.put("valor_imp",StringHelper.roundDouble(rs.getString("valor_imp"),2));
                    row.put("decimales",rs.getString("decimales"));
                    row.put("cant_surtido",StringHelper.roundDouble(rs.getString("cant_surtido"),2));
                    row.put("ieps_id",String.valueOf(rs.getInt("ieps_id")));
                    row.put("ieps_tasa",StringHelper.roundDouble(rs.getString("ieps_tasa"),2));
                    return row;
                }
            }
        );
        return hm;
    }






    @Override
    public ArrayList<HashMap<String, String>> getMonedas() {
        String sql_to_query = "SELECT id, descripcion, descripcion_abr FROM  gral_mon WHERE borrado_logico=FALSE AND compras=TRUE ORDER BY id ASC;";
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


    //este metodo solo se utiliza en el Catalogo de Listas de Precios
    @Override
    public ArrayList<HashMap<String, String>> getMonedas2() {
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

    //se utiliza en catalogo  de productos y entradas de mercancias
    @Override
    public ArrayList<HashMap<String, String>> getEntradas_Impuestos() {
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




    @Override
    public ArrayList<HashMap<String, String>> getEntradas_TasaFletes() {
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
    public ArrayList<HashMap<String, String>> geteEntradas_Fleteras(Integer id_empresa, Integer id_sucursal) {
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




    //obtiene los almacenes de la empresa indicada
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




    //obtiene los almacenes de la empresa, sin incluir informacion de Sucursal y Empresa.
    //se utiliza en la opcion editar de Traspasos
    @Override
    public ArrayList<HashMap<String, String>> getAlmacenes2(Integer id_empresa) {
	String sql_query = ""
                + "SELECT DISTINCT "
                    + "inv_alm.id, "
                    + "inv_alm.titulo "
                + "FROM inv_alm  "
                + "JOIN inv_suc_alm ON inv_suc_alm.almacen_id = inv_alm.id "
                + "JOIN gral_suc ON gral_suc.id = inv_suc_alm.sucursal_id "
                + "WHERE gral_suc.empresa_id="+id_empresa+" AND inv_alm.borrado_logico=FALSE;";
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

    
    
    //Obtiene almacenes de la sucursal especificada.
    //Se utiliza en la Carga de Inventario Fisico
    @Override
    public ArrayList<HashMap<String, String>> getAlmacenesSucursal(Integer id_empresa, Integer id_sucursal) {
	String sql_query = ""
                + "SELECT DISTINCT "
                    + "inv_alm.id, "
                    + "inv_alm.id||' '||inv_alm.titulo AS titulo "
                + "FROM inv_alm  "
                + "JOIN inv_suc_alm ON inv_suc_alm.almacen_id = inv_alm.id "
                + "JOIN gral_suc ON gral_suc.id = inv_suc_alm.sucursal_id "
                + "WHERE gral_suc.empresa_id=? AND gral_suc.id=? AND inv_alm.borrado_logico=FALSE;";
        ArrayList<HashMap<String, String>> hm_alm = (ArrayList<HashMap<String, String>>) this.jdbcTemplate.query(
            sql_query,
            new Object[]{new Integer(id_empresa), new Integer(id_sucursal)}, new RowMapper() {
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


    //obtiene datos para el buscador de proveedores
    @Override
    public ArrayList<HashMap<String, String>> getBuscadorProveedores(String rfc, String noProveedor, String razon_social, Integer id_empresa) {
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

        String sql_to_query = "SELECT DISTINCT  cxp_prov.id, "
                                + "cxp_prov.rfc, "
                                + "cxp_prov.folio AS no_proveedor, "
                                + "cxp_prov.razon_social, "
                                + "cxp_prov.calle||' '||cxp_prov.numero||', '|| cxp_prov.colonia||', '||(CASE WHEN gral_mun.titulo IS NULL THEN '' ELSE gral_mun.titulo END)||', '||(CASE WHEN gral_edo.titulo IS NULL THEN '' ELSE gral_edo.titulo END)||', '||(CASE WHEN gral_pais.titulo IS NULL THEN '' ELSE gral_pais.titulo END) ||' C.P. '||cxp_prov.cp as direccion, "
                                + "cxp_prov.proveedortipo_id,"
                                + "cxp_prov.moneda_id, "
                                + "cxp_prov.impuesto AS impuesto_id,"
                                + "(CASE WHEN gral_imptos.iva_1 is null THEN 0 ELSE gral_imptos.iva_1 END) AS valor_impuesto "
                            + "FROM cxp_prov "
                            + "JOIN gral_pais ON gral_pais.id = cxp_prov.pais_id "
                            + "JOIN gral_edo ON gral_edo.id = cxp_prov.estado_id "
                            + "JOIN gral_mun ON gral_mun.id = cxp_prov.municipio_id  "
                            + "LEFT JOIN gral_imptos ON gral_imptos.id=cxp_prov.impuesto "
                            + "WHERE empresa_id="+id_empresa+" AND cxp_prov.borrado_logico = false "+where;

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
                    row.put("impuesto_id",String.valueOf(rs.getInt("impuesto_id")));
                    row.put("valor_impuesto",StringHelper.roundDouble(String.valueOf(rs.getDouble("valor_impuesto")),2));
                    return row;
                }
            }
        );
        return hm_datos_proveedor;
    }


    //buscar datos por Numero de Proveedor
    @Override
    public ArrayList<HashMap<String, String>> getDatosProveedorByNoProv(String noProveedor, Integer id_empresa) {

        String sql_to_query = "SELECT DISTINCT  cxp_prov.id, "
                                + "cxp_prov.rfc, "
                                + "cxp_prov.folio AS no_proveedor, "
                                + "cxp_prov.razon_social, "
                                + "cxp_prov.calle||' '||cxp_prov.numero||', '|| cxp_prov.colonia||', '||(CASE WHEN gral_mun.titulo IS NULL THEN '' ELSE gral_mun.titulo END)||', '||(CASE WHEN gral_edo.titulo IS NULL THEN '' ELSE gral_edo.titulo END)||', '||(CASE WHEN gral_pais.titulo IS NULL THEN '' ELSE gral_pais.titulo END) ||' C.P. '||cxp_prov.cp as direccion, "
                                + "cxp_prov.proveedortipo_id,"
                                + "cxp_prov.moneda_id, "
                                + "cxp_prov.impuesto AS impuesto_id,"
                                + "(CASE WHEN gral_imptos.iva_1 is null THEN 0 ELSE gral_imptos.iva_1 END) AS valor_impuesto "
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
                    row.put("impuesto_id",String.valueOf(rs.getInt("impuesto_id")));
                    row.put("valor_impuesto",StringHelper.roundDouble(String.valueOf(rs.getDouble("valor_impuesto")),2));
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


    //Busca datos de un producto en especifico a partir del codigo
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
        //log.log(Level.INFO, "Ejecutando query de {0}", sql_to_query);

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



    @Override
    public ArrayList<HashMap<String, String>> getEntradas_PresentacionesProducto(String sku, Integer id_empresa) {
        String sql_to_query = "SELECT "
                                +"inv_prod.id,"
                                +"inv_prod.sku,"
                                +"inv_prod.descripcion as titulo,"
                                +"inv_prod_unidades.titulo as unidad,"
                                +"inv_prod_presentaciones.id as id_presentacion,"
                                +"inv_prod_presentaciones.titulo as presentacion, "
                                +"(CASE WHEN inv_prod.ieps=0 THEN 0 ELSE gral_ieps.id END) AS ieps_id, "
                                +"(CASE WHEN inv_prod.ieps=0 THEN 0 ELSE gral_ieps.tasa END) AS ieps_tasa "
                        +"FROM inv_prod "
                        +"LEFT JOIN inv_prod_unidades on inv_prod_unidades.id = inv_prod.unidad_id "
                        +"LEFT JOIN inv_prod_pres_x_prod on inv_prod_pres_x_prod.producto_id = inv_prod.id "
                        +"LEFT JOIN inv_prod_presentaciones on inv_prod_presentaciones.id = inv_prod_pres_x_prod.presentacion_id "
                        +"LEFT JOIN gral_ieps ON gral_ieps.id=inv_prod.ieps "
                        +"where inv_prod.sku ILIKE '"+sku+"' "
                        + "AND inv_prod.borrado_logico=false "
                        + "AND inv_prod.empresa_id="+id_empresa;

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
                    row.put("ieps_tasa",StringHelper.roundDouble(rs.getString("ieps_tasa"),2));
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













/*
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
*/




    //obtiene existencia del almacen de la empresa indicada
    @Override
    public ArrayList<HashMap<String, String>> getDatos_ReporteExistencias(Integer id_isuario,Integer id_almacen, String codigo_producto, String descripcion,Integer tipo ) {
	String sql_query = "select * from inv_reporte_existencias("+id_isuario+","+id_almacen+",'%"+codigo_producto+"%','%"+descripcion+"%',"+tipo+") as foo("
                                        + "id integer,"
                                        +"valor_minimo double precision, "
                                        +"valor_maximo double precision,"
                                        +"punto_reorden double precision, "
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
                    row.put("existencias",StringHelper.roundDouble(rs.getDouble("existencias"),2));
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




    //AQUI EMPIEZA CATALOGO DE ALMACENES
   //obtiene los tipos de almacen
    @Override
    public ArrayList<HashMap<String, String>> getAlmacennes_TiposAlmacen() {
        String sql_query = "SELECT id, titulo FROM inv_alm_tipos;";
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
    public ArrayList<HashMap<String, Object>> getAlmacenes_PaginaGrid(String data_string, int offset, int pageSize, String orderBy, String asc) {
        String sql_busqueda = "SELECT id FROM gral_bus_catalogos(?) AS foo (id integer)";

	String sql_to_query = "SELECT inv_alm.id, "
                                    +"inv_alm.titulo, "
                                    +"inv_alm_tipos.titulo as tipo, "
                                    +"inv_alm.reporteo, "
                                    +"inv_alm.ventas, "
                                    +"inv_alm.compras, "
                                    +"inv_alm.traspaso, "
                                    +"inv_alm.reabastecimiento, "
                                    +"inv_alm.garantias, "
                                    +"inv_alm.consignacion, "
                                    +"inv_alm.recepcion_mat, "
                                    +"inv_alm.explosion_mat "
                            +"FROM inv_alm "
                            +"JOIN inv_alm_tipos ON inv_alm_tipos.id=inv_alm.almacen_tipo_id "
                            +"JOIN ("+sql_busqueda+") as subt on subt.id=inv_alm.id "
                            +"ORDER BY "+orderBy+" "+asc+" LIMIT ? OFFSET ?";

        //System.out.println("Busqueda GetPage: "+sql_to_query);
        //System.out.println("data_string: "+data_string);
        //System.out.println("offset: "+offset+"        pageSize: "+pageSize+"        orderBy: "+orderBy);

        //log.log(Level.INFO, "Ejecutando query de {0}", sql_to_query);
        ArrayList<HashMap<String, Object>> hm = (ArrayList<HashMap<String, Object>>) this.jdbcTemplate.query(
            sql_to_query,
            new Object[]{new String(data_string),new Integer(pageSize),new Integer(offset)}, new RowMapper() {
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    HashMap<String, Object> row = new HashMap<String, Object>();
                    row.put("id",String.valueOf(rs.getInt("id")));
                    row.put("titulo",rs.getString("titulo"));
                    row.put("tipo",rs.getString("tipo"));

                    if(rs.getBoolean("reporteo")){
                        row.put("reporteo","<input type='checkbox' checked disabled name='rep'>");
                    }else{
                        row.put("reporteo","<input type='checkbox' disabled name='rep'>");
                    }

                    if(rs.getBoolean("ventas")){
                        row.put("ventas","<input type='checkbox' checked disabled name='vent'>");
                    }else{
                        row.put("ventas","<input type='checkbox' disabled name='vent'>");
                    }

                    if(rs.getBoolean("compras")){
                        row.put("compras","<input type='checkbox' checked disabled name='comp'>");
                    }else{
                        row.put("compras","<input type='checkbox' disabled name='comp'>");
                    }

                    if(rs.getBoolean("traspaso")){
                        row.put("traspaso","<input type='checkbox' checked disabled name='tras'>");
                    }else{
                        row.put("traspaso","<input type='checkbox' disabled name='tras'>");
                    }

                    if(rs.getBoolean("reabastecimiento")){
                        row.put("reabastecimiento","<input type='checkbox' checked disabled name='rea'>");
                    }else{
                        row.put("reabastecimiento","<input type='checkbox' disabled name='rea'>");
                    }

                    if(rs.getBoolean("garantias")){
                        row.put("garantias","<input type='checkbox' checked disabled name='gar'>");
                    }else{
                        row.put("garantias","<input type='checkbox' disabled name='gar'>");
                    }

                    if(rs.getBoolean("consignacion")){
                        row.put("consignacion","<input type='checkbox' checked disabled name='cons'>");
                    }else{
                        row.put("consignacion","<input type='checkbox' disabled name='cons'>");
                    }

                    if(rs.getBoolean("recepcion_mat")){
                        row.put("recepcion_mat","<input type='checkbox' checked disabled name='remat'>");
                    }else{
                        row.put("recepcion_mat","<input type='checkbox' disabled name='remat'>");
                    }

                    if(rs.getBoolean("explosion_mat")){
                        row.put("explosion_mat","<input type='checkbox' checked disabled name='exmat'>");
                    }else{
                        row.put("explosion_mat","<input type='checkbox' disabled name='exmat'>");
                    }
                    return row;
                }
            }
        );
        return hm;
    }


   //obtiene datos del almacen seleccionado
    @Override
    public ArrayList<HashMap<String, String>> getAlmacenes_Datos(Integer id) {
        String sql_query = "SELECT id, "
                                +"titulo, "
                                +"almacen_tipo_id, "
                                + "calle, "
                                + "numero, "
                                + "colonia, "
                                + "codigo_postal, "
                                + "gral_pais_id, "
                                + "gral_edo_id, "
                                + "gral_mun_id, "
                                + "responsable, "
                                + "responsable_email, "
                                + "responsable_puesto, "
                                + "tel_1_ext, "
                                + "tel_2_ext, "
                                + "tel_2, "
                                + "tel_1, "
                                +"reporteo, "
                                +"ventas, "
                                +"compras, "
                                +"traspaso, "
                                +"reabastecimiento, "
                                +"garantias, "
                                +"consignacion, "
                                +"recepcion_mat, "
                                +"explosion_mat "
                        +"FROM inv_alm "
                        +"WHERE id="+id;
        ArrayList<HashMap<String, String>> hm = (ArrayList<HashMap<String, String>>) this.jdbcTemplate.query(
            sql_query,
            new Object[]{}, new RowMapper() {
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    HashMap<String, String> row = new HashMap<String, String>();
                    row.put("id",String.valueOf(rs.getInt("id")));
                    row.put("titulo",rs.getString("titulo"));
                    row.put("almacen_tipo_id",String.valueOf(rs.getInt("almacen_tipo_id")));
                    row.put("calle",rs.getString("calle"));
                    row.put("numero",rs.getString("numero"));
                    row.put("colonia",rs.getString("colonia"));
                    row.put("codigo_postal",rs.getString("codigo_postal"));
                    row.put("gral_pais_id",String.valueOf(rs.getInt("gral_pais_id")));
                    row.put("gral_edo_id",String.valueOf(rs.getInt("gral_edo_id")));
                    row.put("gral_mun_id",String.valueOf(rs.getInt("gral_mun_id")));
                    row.put("responsable",rs.getString("responsable"));
                    row.put("responsable_email",rs.getString("responsable_email"));
                    row.put("responsable_puesto",rs.getString("responsable_puesto"));
                    row.put("tel_1_ext",rs.getString("tel_1_ext"));
                    row.put("tel_2_ext",rs.getString("tel_2_ext"));
                    row.put("tel_2",rs.getString("tel_2"));
                    row.put("tel_1",rs.getString("tel_1"));
                    row.put("reporteo",String.valueOf(rs.getBoolean("reporteo")));
                    row.put("ventas",String.valueOf(rs.getBoolean("ventas")));
                    row.put("compras",String.valueOf(rs.getBoolean("compras")));
                    row.put("traspaso",String.valueOf(rs.getBoolean("traspaso")));
                    row.put("reabastecimiento",String.valueOf(rs.getBoolean("reabastecimiento")));
                    row.put("garantias",String.valueOf(rs.getBoolean("garantias")));
                    row.put("consignacion",String.valueOf(rs.getBoolean("consignacion")));
                    row.put("recepcion_mat",String.valueOf(rs.getBoolean("recepcion_mat")));
                    row.put("explosion_mat",String.valueOf(rs.getBoolean("explosion_mat")));
                    return row;
                }
            }
        );
        return hm;
    }




    //obtiene las sucursales
    //cuando id_almacen=0, obtiene todas las sucursales de la empresa indicada
    //cuando id_almacen es diferente de 0, obtiene las sucursales no asignadas al almacen
    @Override
    public ArrayList<HashMap<String, String>> getAlmacenes_Sucursales(Integer id_almacen, Integer id_empresa) {
        String sql_query="";

        if(id_almacen != 0){
            //aqui obtiene solo las sucursales NO asignadas al almacen
            sql_query = "SELECT id, titulo FROM gral_suc WHERE empresa_id="+id_empresa+" AND id NOT IN ( "
                            + "SELECT inv_suc_alm.sucursal_id "
                            + "FROM inv_suc_alm "
                            + "JOIN gral_suc ON gral_suc.id = inv_suc_alm.sucursal_id "
                            + "WHERE gral_suc.empresa_id="+id_empresa+"  AND inv_suc_alm.almacen_id="+id_almacen+" "
                    + ") ORDER BY titulo;";
        }else{
            //obtiene todas las sucursales
            sql_query = "SELECT id, titulo FROM gral_suc WHERE empresa_id="+id_empresa+" ORDER BY titulo;";
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




    //obtiene las sucursales asignadas a un almacen en especifico
    @Override
    public ArrayList<HashMap<String, String>> getAlmacenes_SucursalesON(Integer id_almacen, Integer id_empresa) {
        String sql_query="";
        //aqui obtiene solo las sucursales no asignadas al almacen
        sql_query = "SELECT id, titulo FROM gral_suc WHERE empresa_id="+id_empresa+" AND id IN ( "
                        + "SELECT inv_suc_alm.sucursal_id "
                        + "FROM inv_suc_alm "
                        + "JOIN gral_suc ON gral_suc.id = inv_suc_alm.sucursal_id "
                        + "WHERE gral_suc.empresa_id="+id_empresa+"  AND inv_suc_alm.almacen_id="+id_almacen+" "
                + ") ORDER BY titulo;";

        ArrayList<HashMap<String, String>> hm_pres_on = (ArrayList<HashMap<String, String>>) this.jdbcTemplate.query(
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
        return hm_pres_on;
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
        /*
        if(tipo_reporte==1){
            orderBy = " producto";
        }
        if(tipo_reporte==2){
            orderBy = " cxp_prov.razon_social";
        }
        */
        if(tipo_reporte==1 || tipo_reporte==3){
            orderBy = " inv_prod_unidades.titulo_abr,producto asc,com_fac.factura_fecha_expedicion asc";
        }
        if(tipo_reporte==2 || tipo_reporte==4){
            //orderBy = " cxp_prov.razon_social,inv_prod_unidades.titulo_abr,,erp_prealmacen_entradas.momento_creacion asc";
            orderBy  = "inv_prod_unidades.titulo_abr,cxp_prov.razon_social asc,com_fac.factura_fecha_expedicion asc ";
        }

        String sql_to_query = ""
        + "SELECT  "
                + "cxp_prov.folio AS clave_proveedor, "
                + "cxp_prov.razon_social AS proveedor, "
                + "com_fac.factura, "
                + "to_char(com_fac.momento_creacion,'dd/mm/yyyy') AS fecha, "
                + "inv_prod.sku AS codigo_producto, "
                + "inv_prod.descripcion AS producto, "
                + "inv_prod_unidades.titulo_abr AS unidad, "
                + "com_fac_detalle.cantidad, "
                + "com_fac_detalle.costo_unitario, "
                + "com_fac.tipo_de_cambio AS tipo_cambio, "
                + "gral_mon.descripcion_abr AS moneda, "
                + "(CASE WHEN com_fac.moneda_id=1 THEN  "
                        + "com_fac_detalle.costo_unitario * com_fac_detalle.cantidad  "
                + "ELSE  "
                        + "com_fac_detalle.cantidad * (com_fac_detalle.costo_unitario * com_fac.tipo_de_cambio) "
                + "END) AS compra_neta_mn "
        + "FROM com_fac "
        + "JOIN com_fac_detalle ON com_fac_detalle.com_fac_id=com_fac.id "
        + "JOIN cxp_prov ON cxp_prov.id=com_fac.proveedor_id "
        + "JOIN inv_prod ON inv_prod.id=com_fac_detalle.producto_id "
        + "JOIN inv_prod_unidades ON inv_prod_unidades.id=inv_prod.unidad_id "
        + "JOIN gral_mon ON gral_mon.id=com_fac.moneda_id "
        + "WHERE com_fac.empresa_id="+id_empresa+" "
        + "AND com_fac.borrado_logico=FALSE "
        + "AND com_fac.cancelacion=FALSE "
        + "AND (to_char(com_fac.momento_creacion,'yyyymmdd')::INTEGER BETWEEN to_char('"+fecha_inicial+"'::timestamp with time zone,'yyyymmdd')::INTEGER AND to_char('"+fecha_final+"'::timestamp with time zone,'yyyymmdd')::INTEGER) "
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
        String sql_query = "SELECT id, titulo, descripcion, inv_prod_tipo_id FROM inv_prod_familias WHERE borrado_logico=FALSE AND id="+id_familia;
        ArrayList<HashMap<String, String>> hm = (ArrayList<HashMap<String, String>>) this.jdbcTemplate.query(
            sql_query,
            new Object[]{}, new RowMapper() {
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    HashMap<String, String> row = new HashMap<String, String>();
                    row.put("id",String.valueOf(rs.getInt("id")));
                    row.put("titulo",rs.getString("titulo"));
                    row.put("descripcion",rs.getString("descripcion"));
                    row.put("inv_prod_tipo_id",String.valueOf(rs.getInt("inv_prod_tipo_id")));
                    return row;
                }
            }
        );
        return hm;
    }


    //add por paco
    @Override
    public ArrayList<HashMap<String, String>> getInvProdFamilias_TiposProd() {
        String sql_query = "SELECT id, titulo FROM inv_prod_tipos WHERE borrado_logico=FALSE";
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
        String sql_query = "SELECT  id, titulo, descripcion, identificador_familia_padre, inv_prod_tipo_id FROM inv_prod_familias WHERE id="+id_subfamilia;
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
                    row.put("inv_prod_tipo_id",rs.getString("inv_prod_tipo_id"));
                    return row;
                }
            }
        );
        return hm;
    }





    //Obtener todas las subfamilias, para select de catalogo de productos
    @Override
    public ArrayList<HashMap<String, String>> getProducto_Subfamilias(Integer id_empresa, String familia_id) {
        String sql_query = "SELECT  id, titulo, descripcion FROM inv_prod_familias WHERE borrado_logico=FALSE AND id != identificador_familia_padre AND gral_emp_id="+id_empresa+" and identificador_familia_padre="+familia_id;
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



    //Obtener todas las familias
    //se utiliza en catalogo de Productos y catalogo de Subfamilias
    @Override
    public ArrayList<HashMap<String, String>> getInvProdSubFamiliasByTipoProd(Integer id_empresa,String prod_tipo) {
        String sql_query = "SELECT  id, titulo, descripcion FROM inv_prod_familias WHERE borrado_logico=FALSE AND id=identificador_familia_padre AND gral_emp_id="+id_empresa+" and inv_prod_tipo_id="+prod_tipo;
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

	String sql_to_query = "SELECT "
                + "inv_pre.id "
                + ", inv_prod.sku "
                + ", inv_prod.descripcion "
                + ", (CASE WHEN inv_prod_presentaciones.titulo IS NULL THEN '' ELSE inv_prod_presentaciones.titulo END ) AS pres "
                + ", inv_pre.precio_1"
                + ", inv_pre.precio_2"
                + ", inv_pre.precio_3 "
                + ", inv_pre.precio_4 "
                + ", inv_pre.precio_5 "
                + ", inv_pre.precio_6 "
                + ", inv_pre.precio_7 "
                + ", inv_pre.precio_8 "
                + ", inv_pre.precio_9 "
                + ", inv_pre.precio_10 "
                + "FROM inv_pre "
                + "LEFT JOIN inv_prod_presentaciones ON inv_prod_presentaciones.id=inv_pre.inv_prod_presentacion_id "
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
                    row.put("pres",rs.getString("pres"));
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
                    row.put("presentacion_id",String.valueOf(rs.getInt("inv_prod_presentacion_id")));
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

                    row.put("id_mon1",String.valueOf(rs.getInt("gral_mon_id_pre1")));
                    row.put("id_mon2",String.valueOf(rs.getInt("gral_mon_id_pre2")));
                    row.put("id_mon3",String.valueOf(rs.getInt("gral_mon_id_pre3")));
                    row.put("id_mon4",String.valueOf(rs.getInt("gral_mon_id_pre4")));
                    row.put("id_mon5",String.valueOf(rs.getInt("gral_mon_id_pre5")));
                    row.put("id_mon6",String.valueOf(rs.getInt("gral_mon_id_pre6")));
                    row.put("id_mon7",String.valueOf(rs.getInt("gral_mon_id_pre7")));
                    row.put("id_mon8",String.valueOf(rs.getInt("gral_mon_id_pre8")));
                    row.put("id_mon9",String.valueOf(rs.getInt("gral_mon_id_pre9")));
                    row.put("id_mon10",String.valueOf(rs.getInt("gral_mon_id_pre10")));

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


    /*
    Éste método solo obtiene la moneda de la lista de precio.
    Si la lista de precio no tiene moneda, por default le asigna el 1
     */
     @Override
     public ArrayList<HashMap<String, String>> getInvPre_MonedaListas(Integer id_empresa) {
     String sql_to_query = ""
             + "SELECT "
                 + "(CASE WHEN gral_mon_id_pre1=0 OR gral_mon_id_pre1 IS NULL THEN 1 ELSE gral_mon_id_pre1 END) AS moneda_lista1,"
                 + "(CASE WHEN gral_mon_id_pre2=0 OR gral_mon_id_pre2 IS NULL THEN 1 ELSE gral_mon_id_pre2 END) AS moneda_lista2,"
                 + "(CASE WHEN gral_mon_id_pre3=0 OR gral_mon_id_pre3 IS NULL THEN 1 ELSE gral_mon_id_pre3 END) AS moneda_lista3,"
                 + "(CASE WHEN gral_mon_id_pre4=0 OR gral_mon_id_pre4 IS NULL THEN 1 ELSE gral_mon_id_pre4 END) AS moneda_lista4,"
                 + "(CASE WHEN gral_mon_id_pre5=0 OR gral_mon_id_pre5 IS NULL THEN 1 ELSE gral_mon_id_pre5 END) AS moneda_lista5,"
                 + "(CASE WHEN gral_mon_id_pre6=0 OR gral_mon_id_pre6 IS NULL THEN 1 ELSE gral_mon_id_pre6 END) AS moneda_lista6,"
                 + "(CASE WHEN gral_mon_id_pre7=0 OR gral_mon_id_pre7 IS NULL THEN 1 ELSE gral_mon_id_pre7 END) AS moneda_lista7,"
                 + "(CASE WHEN gral_mon_id_pre8=0 OR gral_mon_id_pre8 IS NULL THEN 1 ELSE gral_mon_id_pre8 END) AS moneda_lista8,"
                 + "(CASE WHEN gral_mon_id_pre9=0 OR gral_mon_id_pre9 IS NULL THEN 1 ELSE gral_mon_id_pre9 END) AS moneda_lista9,"
                 + "(CASE WHEN gral_mon_id_pre10=0 OR gral_mon_id_pre10 IS NULL THEN 1 ELSE gral_mon_id_pre10 END) AS moneda_lista10 "
             + "FROM inv_pre "
             + "WHERE gral_emp_id="+id_empresa+" AND borrado_logico=FALSE LIMIT 1;";

        ArrayList<HashMap<String, String>> hm = (ArrayList<HashMap<String, String>>) this.jdbcTemplate.query(
            sql_to_query,
            new Object[]{}, new RowMapper(){
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    HashMap<String, String> row = new HashMap<String, String>();
                    row.put("moneda_lista1",String.valueOf(rs.getInt("moneda_lista1")));
                    row.put("moneda_lista2",String.valueOf(rs.getInt("moneda_lista2")));
                    row.put("moneda_lista3",String.valueOf(rs.getInt("moneda_lista3")));
                    row.put("moneda_lista4",String.valueOf(rs.getInt("moneda_lista4")));
                    row.put("moneda_lista5",String.valueOf(rs.getInt("moneda_lista5")));
                    row.put("moneda_lista6",String.valueOf(rs.getInt("moneda_lista6")));
                    row.put("moneda_lista7",String.valueOf(rs.getInt("moneda_lista7")));
                    row.put("moneda_lista8",String.valueOf(rs.getInt("moneda_lista8")));
                    row.put("moneda_lista9",String.valueOf(rs.getInt("moneda_lista9")));
                    row.put("moneda_lista10",String.valueOf(rs.getInt("moneda_lista10")));
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







    //traer sucursales
     @Override
    public ArrayList<HashMap<String, String>> getSucursales(Integer id_empresa){

        String sql_to_query = "SELECT id, titulo AS sucursal, empresa_id FROM gral_suc WHERE empresa_id="+id_empresa+";";

        ArrayList<HashMap<String, String>> sucursales = (ArrayList<HashMap<String, String>>) this.jdbcTemplate.query(
            sql_to_query,
            new Object[]{}, new RowMapper(){
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    HashMap<String, String> row = new HashMap<String, String>();
                    row.put("id",String.valueOf(rs.getInt("id")));
                    row.put("sucursal",rs.getString("sucursal"));
                    row.put("emp_id",String.valueOf(rs.getInt("empresa_id")));
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
                                + "(CASE WHEN opse.estatus=0 THEN 'Sin estatus'  "
                                        + "WHEN opse.estatus=1 THEN 'Enterado'  "
                                        + "WHEN opse.estatus=2 THEN 'En Proceso'  "
                                        + "WHEN opse.estatus=3 THEN 'Listo'  "
                                        + "WHEN opse.estatus=4 THEN 'Cancelado'  "
                                + "ELSE '' END) AS estatus, "
                                + "to_char(opse.momento_creacion,'dd/mm/yyyy') AS momento_creacion, "
                                + "(CASE WHEN inv_alm.titulo IS NULL THEN '' ELSE inv_alm.titulo END) AS almacen "
                            + "FROM inv_ord_subensamble AS opse "
                            + "LEFT JOIN inv_alm ON inv_alm.id=opse.inv_alm_id "
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
                    row.put("momento_creacion",String.valueOf(rs.getString("momento_creacion")));
                    row.put("almacen",rs.getString("almacen"));
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
                            + "inv_proceso.proceso_flujo_id,"
                            + "inv_ord_subensamble.inv_alm_id AS id_almacen "
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
                    row.put("id_almacen",String.valueOf(rs.getInt("id_almacen")));
                    return row;
                }
            }
        );
        return hm;
    }




    //obtiene detalles del la orden pre-subensamble
    @Override
    public ArrayList<HashMap<String, String>> getInvDetalleOrdPreSuben(String id) {
        String sql_query = ""
        + "select iop.id, "
            + "iop.cantidad, "
            + "iop.inv_prod_presentacion_id AS presentacion_id,"
            + "inv_prod.sku,"
            + "inv_prod.densidad,"
            + "(CASE WHEN inv_prod_unidades.decimales IS NULL THEN 0 ELSE inv_prod_unidades.decimales END) AS no_dec  "
        + "FROM inv_ord_subensamble_detalle AS iop "
        + "JOIN inv_prod ON inv_prod.id=iop.inv_prod_id_subensamble "
        + "JOIN inv_prod_unidades ON inv_prod_unidades.id=inv_prod.unidad_id  "
        + "where iop.inv_ord_subensamble_id=? order by iop.id;";
        
        //System.out.println("getInvDetalleOrdPreSuben: "+sql_query);
        ArrayList<HashMap<String, String>> hm = (ArrayList<HashMap<String, String>>) this.jdbcTemplate.query(
            sql_query,
            new Object[]{new Integer(id)}, new RowMapper() {
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    HashMap<String, String> row = new HashMap<String, String>();
                    row.put("id",String.valueOf(rs.getInt("id")));
                    row.put("sku",rs.getString("sku"));
                    row.put("densidad",StringHelper.roundDouble(rs.getDouble("densidad"), 4));
                    row.put("presentacion_id",String.valueOf(rs.getInt("presentacion_id")));
                    row.put("cantidad",StringHelper.roundDouble(rs.getDouble("cantidad"), rs.getInt("no_dec")));
                    return row;
                }
            }
        );
        return hm;
    }
    
    
    
    
    @Override
    public ArrayList<HashMap<String, String>> getInvOrdPreSubenDatosProductos(String sku, Integer id_empresa) {
        String sql_to_query = ""
        + "SELECT inv_prod.id, "
            + "inv_prod.sku, "
            + "inv_prod.descripcion, "
            + "inv_prod.densidad, "
            + "inv_prod_unidades.titulo, "
            + "(CASE WHEN inv_prod.inv_prod_presentacion_id IS NULL THEN 0 ELSE inv_prod.inv_prod_presentacion_id END) AS id_pres_def,"
            + "(CASE WHEN inv_prod_unidades.decimales IS NULL THEN 0 ELSE inv_prod_unidades.decimales END) AS no_dec "
        + "FROM inv_prod  "
        + "JOIN inv_prod_unidades ON inv_prod_unidades.id=inv_prod.unidad_id "
        + "WHERE inv_prod.sku='"+sku+"' AND inv_prod.tipo_de_producto_id IN (1,2,3,8);";
        /*
        Solo se hace busqueda de estos tipos de productos Formulados
        1;"Prod. Terminado"
        2;"Prod. Intermedio"
        3;"Kit"
        8;"Prod. en Desarrollo"
        */
        
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
                    row.put("densidad",StringHelper.roundDouble(rs.getDouble("densidad"),4));
                    row.put("titulo",rs.getString("titulo"));
                    row.put("id_pres_def",String.valueOf(rs.getInt("id_pres_def")));
                    row.put("no_dec",String.valueOf(rs.getInt("no_dec")));
                    return row;
                }
            }
        );
        return hm_datos_productos;
    }
    
    
    
    @Override
    public ArrayList<HashMap<String, String>> getInvOrdPreSubenDatosComProd(String sku, Integer id_empresa) {
        String sql_to_query = ""
        + "SELECT inv_prod.id, "
            + "inv_prod.sku, "
            + "inv_prod.descripcion, "
            + "inv_prod_unidades.titulo AS utitulo, "
            + "inv_prod.densidad,"
            + "(inv_prod.densidad * tmp.cantidad::double precision) as densidad_promedio, "
            + "tmp.cantidad,"
            + "inv_prod.inv_prod_presentacion_id AS id_pres_def,"
            + "(CASE WHEN inv_prod_unidades.decimales IS NULL THEN 0 ELSE inv_prod_unidades.decimales END) AS no_dec,"
            + "(CASE WHEN inv_prod_presentaciones.titulo IS NULL THEN '' ELSE inv_prod_presentaciones.titulo END) AS presentacion "
        + "FROM inv_prod "
        + "JOIN ( "
                + "SELECT inv_kit.producto_elemento_id, inv_kit.cantidad "
                + "FROM (SELECT id FROM inv_prod WHERE upper(inv_prod.sku)='"+sku.trim().toUpperCase()+"' and empresa_id="+id_empresa+") as tmp1 "
                + "JOIN inv_kit ON tmp1.id=inv_kit.producto_kit_id "
        + ") as tmp ON tmp.producto_elemento_id=inv_prod.id "
        + "JOIN inv_prod_unidades ON inv_prod_unidades.id=inv_prod.unidad_id "
        + "LEFT JOIN inv_prod_presentaciones ON inv_prod_presentaciones.id=inv_prod.inv_prod_presentacion_id;";
        
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
                    row.put("presentacion",rs.getString("presentacion"));
                    row.put("densidad",StringHelper.roundDouble(rs.getDouble("densidad"),4));
                    row.put("densidad_promedio",StringHelper.roundDouble(rs.getDouble("densidad_promedio"),4));
                    row.put("cantidad",StringHelper.roundDouble(rs.getDouble("cantidad"),rs.getInt("no_dec")));
                    row.put("id_pres_def_comp",String.valueOf(rs.getInt("id_pres_def")));
                    row.put("no_dec",String.valueOf(rs.getInt("no_dec")));
                    return row;
                }
            }
        );
        return hm_datos_productos;
    }
    


    @Override
    public HashMap<String, String> getInvOrdPreSuben_IdAlmacenProd(Integer id_empresa) {
        HashMap<String, String> data = new HashMap<String, String>();

        String sql_to_query = "SELECT inv_alm_id FROM pro_par WHERE gral_emp_id="+id_empresa+";";

        Map<String, Object> map = this.getJdbcTemplate().queryForMap(sql_to_query);
        data.put("id_alm_prod",map.get("inv_alm_id").toString());
        return data;
    }




    //Metodos para proceso InvOrdSuben
    @Override
    public ArrayList<HashMap<String, Object>> getInvOrdSubenGrid(String data_string, int offset, int pageSize, String orderBy, String asc) {
        String sql_busqueda = "select id from gral_bus_catalogos(?) as foo (id integer)";

	String sql_to_query = ""
                + "select "
                    + "inv_ord_subensamble.id, "
                    + "inv_ord_subensamble.folio, "
                    + "to_char(inv_ord_subensamble.momento_creacion,'dd/mm/yyyy') AS momento_creacion, "
                    + "inv_ord_subensamble.estatus,"
                    + "(CASE WHEN inv_alm.titulo IS NULL THEN '' ELSE inv_alm.titulo END) AS almacen "
                + "FROM inv_ord_subensamble "
                + "LEFT JOIN inv_alm ON inv_alm.id=inv_ord_subensamble.inv_alm_id "
                + "JOIN ("+sql_busqueda+") AS sbt ON sbt.id=inv_ord_subensamble.id "
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
                    row.put("momento_creacion",String.valueOf(rs.getString("momento_creacion")));
                    row.put("almacen",rs.getString("almacen"));
                    return row;
                }
            }
        );
        return hm;
    }



    //obtiene datos del InvCom
    @Override
    public ArrayList<HashMap<String, String>> getInvOrdSuben_Datos(String id) {
        String sql_query = "select id, folio, comentarios,estatus, to_char(momento_creacion,'dd/mm/yyyy') as fecha from inv_ord_subensamble where id="+id;

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
                    row.put("fecha",String.valueOf(rs.getString("fecha")));
                    return row;
                }
            }
        );
        return hm;
    }



    //obtiene datos del InvCom
    @Override
    public ArrayList<HashMap<String, String>> getInvDetalleOrdSuben(String id) {
        String sql_query = ""
                + "select "
                    + "iop.id,"
                    + "iop.cantidad,"
                    + "inv_prod.sku,"
                    + "inv_prod.descripcion, "
                    + "inv_prod_unidades.titulo AS unidad,"
                    + "(CASE WHEN inv_prod_presentaciones.titulo IS NULL THEN '' ELSE inv_prod_presentaciones.titulo END) AS presentacion,"
                    + "(CASE WHEN inv_prod_unidades.decimales IS NULL THEN 0 ELSE inv_prod_unidades.decimales END) AS no_dec "
                + "FROM inv_ord_subensamble_detalle AS iop "
                + "JOIN inv_prod ON inv_prod.id=iop.inv_prod_id_subensamble "
                + "JOIN inv_prod_unidades ON inv_prod_unidades.id=inv_prod.unidad_id "
                + "LEFT JOIN inv_prod_presentaciones ON inv_prod_presentaciones.id=iop.inv_prod_presentacion_id "
                + "WHERE iop.inv_ord_subensamble_id="+id;

        System.out.println("getInvDetalleOrdSuben: "+sql_query);

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
                    row.put("no_dec",String.valueOf(rs.getInt("no_dec")));
                    row.put("presentacion",rs.getString("presentacion"));
                    row.put("cantidad",StringHelper.roundDouble(rs.getDouble("cantidad"), rs.getInt("no_dec")));
                    return row;
                }
            }
        );
        return hm;
    }



    @Override
    public ArrayList<HashMap<String, String>> getInvOrdPreSubenDatosComponentesPorProducto(String id) {

        String sql_to_query = ""
                + "SELECT "
                    + "inv_prod.id,"
                    + "inv_prod.sku, "
                    + "inv_prod.descripcion, "
                    + "inv_prod_unidades.titulo AS utitulo, "
                    + "subt2.cantidad,"
                    + "(CASE WHEN inv_prod_presentaciones.titulo IS NULL THEN '' ELSE inv_prod_presentaciones.titulo END) AS presentacion,"
                    + "(CASE WHEN inv_prod_unidades.decimales IS NULL THEN 0 ELSE inv_prod_unidades.decimales END) AS no_dec "
                + "FROM ("
                        + "SELECT inv_kit.producto_elemento_id,"
                            + "sum(subt1.cantidad * inv_kit.cantidad) AS cantidad "
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
                + "LEFT JOIN inv_prod_presentaciones ON inv_prod_presentaciones.id=inv_prod.inv_prod_presentacion_id "
                + "JOIN inv_prod_unidades ON inv_prod_unidades.id=inv_prod.unidad_id; ";
                
        //log.log(Level.INFO, "Ejecutando query de {0}", sql_to_query);
        System.out.println("getInvOrdPreSubenDatosComponentesPorProducto: "+ sql_to_query);
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
                    row.put("pres_comp",rs.getString("presentacion"));
                    row.put("no_dec",String.valueOf(rs.getInt("no_dec")));
                    row.put("cantidad",StringHelper.roundDouble(rs.getDouble("cantidad"),rs.getInt("no_dec")));
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






    //catalogo de Presentaciones de Productos
    @Override
    public ArrayList<HashMap<String, Object>> getPresentaciones_PaginaGrid(String data_string, int offset, int pageSize, String orderBy, String asc) {
        String sql_busqueda = "select id from gral_bus_catalogos(?) as foo (id integer)";

	String sql_to_query = "SELECT inv_prod_presentaciones.id,inv_prod_presentaciones.titulo as descripcion, "
                + "  (case when inv_prod_presentaciones.cantidad is null then 0 else inv_prod_presentaciones.cantidad end )as cantidad "
                                +"FROM inv_prod_presentaciones "
                                +"JOIN ("+sql_busqueda+") AS sbt ON sbt.id = inv_prod_presentaciones.id "
                                +"WHERE inv_prod_presentaciones.borrado_logico=false  "
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
                    row.put("cantidad",StringHelper.roundDouble(rs.getString("cantidad"),2));
                    return row;
                }
            }
        );
        return hm;
    }



    @Override
    public ArrayList<HashMap<String, String>> getPresentaciones_Datos(Integer id_Presentacion) {
        String sql_to_query = "SELECT inv_prod_presentaciones.id,inv_prod_presentaciones.titulo as descripcion,inv_prod_presentaciones.cantidad  FROM inv_prod_presentaciones WHERE id ="+id_Presentacion;

        ArrayList<HashMap<String, String>> datos_presentaciones = (ArrayList<HashMap<String, String>>) this.jdbcTemplate.query(
            sql_to_query,
            new Object[]{}, new RowMapper(){
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    HashMap<String, String> row = new HashMap<String, String>();
                    row.put("id",String.valueOf(rs.getInt("id")));
                    row.put("descripcion",rs.getString("descripcion"));
                    row.put("cantidad",StringHelper.roundDouble(rs.getString("cantidad"),2));
                    return row;
                }
            }
        );
        return datos_presentaciones;
    }







    //:::::CATALOGO DE FORMULAS::::::::::
    //obtiene tipos de productos
    @Override
    public ArrayList<HashMap<String, String>> getProducto_Tipos_para_formulas(Integer buscador_producto) {
        String cadena_where="";
        if(buscador_producto==1){
         cadena_where ="and inv_prod_tipos.id in (1,2) ";
        }
        if(buscador_producto==2){
         cadena_where ="and inv_prod_tipos.id in (7,2,1) ";
        }
        if(buscador_producto==3){
         cadena_where ="and inv_prod_tipos.id in (1,2) ";
        }

	String sql_query = "SELECT DISTINCT id,titulo  "
                            + " FROM inv_prod_tipos  "
                            +"  WHERE borrado_logico=false "
                            +cadena_where+ "order by inv_prod_tipos.titulo;";

           //System.out.println("sql_query: "+sql_query);
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


    //busca producto en especifico para agregar a la lista de productos elemento en el catalogo de formulas
    @Override
    public ArrayList<HashMap<String, String>> getFormulas_sku(String sku, Integer id_usuario) {

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
                            + "WHERE inv_prod.empresa_id = "+id_empresa+" AND inv_prod.sku='"+sku+"';";
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







    //catalogo de formulas de Productos
    @Override
    public ArrayList<HashMap<String, Object>> getFormulas_PaginaGrid(String data_string, int offset, int pageSize, String orderBy, String asc) {
        //String sql_busqueda = "select id from gral_bus_catalogos(?) as foo (id integer)";

        String sql_to_query = "SELECT distinct "
                                +" inv_formulas.inv_prod_id_master||'---'||inv_formulas.nivel as id, "
                                +" inv_prod.sku as codigo, "
                                +" inv_prod.descripcion, "
                                +" inv_prod_unidades.titulo_abr as unidad,"
                                +" inv_prod_tipos.titulo as tipo_producto,  "
                                +" inv_formulas.nivel  "
                                +" from inv_prod   "
                                +" join inv_formulas on inv_formulas.inv_prod_id_master=inv_prod.id  "
                                +" join  inv_prod_unidades on inv_prod_unidades.id=inv_prod.unidad_id  "
                                +" join inv_prod_tipos on inv_prod_tipos.id=inv_prod.tipo_de_producto_id  "
                               // +" order by nivel desc  ";
                                +" order by "+orderBy+" "+asc+" limit ? OFFSET ? ";

        //System.out.println("Busqueda GetPage: "+sql_to_query);

        ArrayList<HashMap<String, Object>> hm = (ArrayList<HashMap<String, Object>>) this.jdbcTemplate.query(
            sql_to_query,
            new Object[]{new Integer(pageSize),new Integer(offset)}, new RowMapper() {
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    HashMap<String, Object> row = new HashMap<String, Object>();
                    row.put("id",rs.getString("id"));
                    row.put("codigo",rs.getString("codigo"));
                    row.put("descripcion",rs.getString("descripcion"));
                    row.put("unidad",rs.getString("unidad"));
                    row.put("tipo_producto",rs.getString("tipo_producto"));
                    row.put("nivel",String.valueOf(rs.getInt("nivel")));

                    return row;
                }
            }
        );
        return hm;
    }


    @Override
    public ArrayList<HashMap<String, String>> getFormulas_Datos(String id_formula, String id_nivel) {
        String sql_to_query =    " SELECT "
                                +"  max(inv_formulas.nivel)as numero_pasos, "
                                +"  '"+id_nivel+"'::integer nivel_paso_actual, "
                                +"  '"+id_formula+"'::integer id_formula, "
                                +"  '"+id_formula+"'::integer id_producto_master, "
                                +"  inv_prod.sku as codigo, "
                                +"   inv_prod.descripcion , "
                                +"   inv_prod_tipos.titulo as tipo_producto,  "
                                +"   inv_prod_unidades.titulo_abr as unidad  "
                                +"   from inv_prod  "
                                +"   join inv_formulas on inv_formulas.inv_prod_id_master=inv_prod.id  "
                                +"   join  inv_prod_unidades on inv_prod_unidades.id=inv_prod.unidad_id  "
                                +"  join inv_prod_tipos on inv_prod_tipos.id=inv_prod.tipo_de_producto_id  "
                                +"  WHERE inv_formulas.inv_prod_id_master = "+id_formula
                                +"  GROUP BY inv_prod.sku, inv_prod.descripcion,inv_prod_tipos.titulo,inv_prod_unidades.titulo_abr ";

        //System.out.println("segundo query"+sql_to_query);
        ArrayList<HashMap<String, String>> datos_formulas = (ArrayList<HashMap<String, String>>) this.jdbcTemplate.query(
            sql_to_query,
            new Object[]{}, new RowMapper(){
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    HashMap<String, String> row = new HashMap<String, String>();
                    row.put("numero_pasos",String.valueOf(rs.getInt("numero_pasos")));
                    row.put("nivel_paso_actual",String.valueOf(rs.getInt("nivel_paso_actual")));
                    row.put("id_formula",rs.getString("id_formula"));
                    row.put("id_producto_master",rs.getString("id_producto_master"));
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
    //:::::::::::TERMINA CATALOGO DE FORMULAS::::::::::::::::::::::::


    //:::::::::::CONSULTA QUE RETORNA EL GRID DE FORMULAS DE ACUERDO A SU PASO::::::::::::::::::::::::
    @Override
    public ArrayList<HashMap<String, String>> getFormulas_DatosMinigrid(String id_tabla_formula, String id_nivel) {
        String sql_query = " SELECT "
                            + " inv_prod_id_master,  inv_prod_id,  nivel "
                            + " FROM  inv_formulas where inv_formulas.id="+id_tabla_formula;
        //System.out.println("Primer  query??"+sql_query);

         String sql_to_query = " SELECT  "
                                + " inv_formulas.producto_elemento_id, "
                                + " inv_prod.sku AS codigo, "
                                + " inv_prod.descripcion, "
                                + " inv_formulas.cantidad, "
                                + " inv_formulas.nivel "
                                + " FROM inv_formulas "
                                + " JOIN inv_prod ON inv_prod.id=inv_formulas.producto_elemento_id  "
                                + "WHERE "
                                + " inv_formulas.inv_prod_id_master ="+id_tabla_formula
                                //+ " inv_formulas.inv_prod_id ="+id_tabla_formula
                                + " and inv_formulas.nivel ="+id_nivel+" "
                                + " ORDER BY inv_formulas.id ASC;";
                            //.get("inv_prod_id_master").toString();
        //System.out.println("Checar este query??"+sql_to_query);
        ArrayList<HashMap<String, String>> datos_formulas_minigrid = (ArrayList<HashMap<String, String>>) this.jdbcTemplate.query(
            sql_to_query,
            new Object[]{}, new RowMapper(){
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    HashMap<String, String> row = new HashMap<String, String>();
                    //row.put("id_inv_formulas",String.valueOf(rs.getInt("id")));
                    row.put("producto_elemento_id",String.valueOf(rs.getInt("producto_elemento_id")));
                    row.put("codigo",rs.getString("codigo"));
                    row.put("descripcion",rs.getString("descripcion"));
                    row.put("cantidad",StringHelper.roundDouble(rs.getString("cantidad"),4));
                    row.put("nivel",rs.getString("nivel"));

                    return row;
                }
            }
        );
        return datos_formulas_minigrid;
    }

    //::::::::::::::::::::::RETORNANDO EL PRODUCTO SALIENTE ( ID_PRODUCTO_SALIENTE ,CODIGO , DESCRIPCION ,  )
        @Override
    public ArrayList<HashMap<String, String>> getFormulas_DatosProductoSaliente(String id_tabla_formula, String id_nivel) {

         String sql_to_query = " SELECT inv_prod_id,    inv_prod.sku as codigo,   inv_prod.descripcion "
                              + " FROM  inv_formulas   "
                              + " JOIN  inv_prod ON inv_prod.id=inv_formulas.inv_prod_id "
                              + " where inv_formulas.inv_prod_id_master="+id_tabla_formula+" and inv_formulas.nivel="+id_nivel+" limit 1";
                              //.get("inv_prod_id_master").toString();
        //System.out.println("Checar este query??"+sql_to_query);
        ArrayList<HashMap<String, String>> datos_formulas_minigrid = (ArrayList<HashMap<String, String>>) this.jdbcTemplate.query(
            sql_to_query,
            new Object[]{}, new RowMapper(){
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    HashMap<String, String> row = new HashMap<String, String>();
                    //row.put("id_inv_formulas",String.valueOf(rs.getInt("id")));
                    row.put("inv_prod_id",String.valueOf(rs.getInt("inv_prod_id")));
                    row.put("codigo",rs.getString("codigo"));
                    row.put("descripcion",rs.getString("descripcion"));




                    return row;
                }
            }
        );
        return datos_formulas_minigrid;
    }
    //:::. FIN DEL  RETORNO DE DATOS  PARA EL PRODUCTO SALIENTE



    //metodo para reporte de kits
    @Override
    public ArrayList<HashMap<String, String>> getKits(Integer id_empresa,Integer id_usuario,String codigo, String descripcion) {
        //System.out.println("Codigo: "+codigo+"Descripcion: "+descripcion);
        String   cadena_where="";
        if(!codigo.equals("")){
          cadena_where=" and inv_prod.sku = '"+codigo +"'";
        }

        if(descripcion !=""){
          cadena_where=" and inv_prod.descripcion ilike '"+descripcion +"'";
        }

        String sql_to_query =""
                + "SELECT  "
                         +"codigo, "
                         +"producto_kit, "
                         +"min(cantidad_de_kits)as cantidad_de_kits "
                         +"FROM( "
                         +"SELECT  *, "
                         +"("
                                + "CASE WHEN cantidad_existencia =0 THEN 0  ELSE round(cantidad_existencia /cantidad)  END)AS cantidad_de_kits "
                                +"FROM (  SELECT   "
                                        + "inv_prod.sku AS codigo, "
                                        +"inv_prod.descripcion AS producto_kit, "
                                        +"inv_calculo_existencia_producto(inv_kit.producto_elemento_id,'"+id_usuario+"')AS cantidad_existencia, "
                                        +"inv_kit.cantidad  "
                                     +"FROM inv_kit  "
                                     +"JOIN inv_prod on inv_prod.id=inv_kit.producto_kit_id  "
                                     +"JOIN inv_prod_tipos on inv_prod_tipos.id=inv_prod.tipo_de_producto_id  "
                                     +"WHERE inv_prod_tipos.id=3 AND inv_prod.empresa_id=" +id_empresa
                                     +cadena_where
                                +")AS sbt1  order by codigo,producto_kit asc "
                         +")AS sbt2 "
                         +" group by codigo,producto_kit ";


       //System.out.println("query cuantos kits puedo formar:"+ sql_to_query);

        ArrayList<HashMap<String, String>> hm = (ArrayList<HashMap<String, String>>) this.jdbcTemplate.query(
                sql_to_query,
            new Object[]{}, new RowMapper() {
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    HashMap<String, String> row = new HashMap<String, String>();
                    row.put("codigo",rs.getString("codigo"));
                    row.put("producto_kit",rs.getString("producto_kit"));
                    row.put("cantidad_de_kits",String.valueOf(rs.getDouble("cantidad_de_kits")));

                    return row;
                }
            }
        );
        return hm;
    }



    //:::::::::::::::para pdf de formulas hecho por Paco Mora::::::::::::::::::
    @Override
    public ArrayList<HashMap<String, Object>> getInv_ListaProductosFormulaPdf(Integer formula_id) {
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

        //System.out.println("Obtiene datos pdf ruta: "+sql_query);
        ArrayList<HashMap<String, Object>> hm_grid = (ArrayList<HashMap<String, Object>>) this.jdbcTemplate.query(
            sql_query,
            new Object[]{}, new RowMapper() {
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    HashMap<String, Object> row = new HashMap<String, Object>();
                    row.put("producto_elemento_id",String.valueOf(rs.getInt("producto_elemento_id")));
                    row.put("descripcion",rs.getString("descripcion"));
                    row.put("sku",rs.getString("sku"));
                    row.put("cantidad",StringHelper.roundDouble(rs.getString("cantidad"),2));
                    row.put("tipo",rs.getString("tipo"));
                    row.put("id_tipo",rs.getInt("id_tipo"));
                    row.put("unidad","  "+rs.getString("unidad"));
                    if(String.valueOf(rs.getInt("id_tipo")).equals("2") || String.valueOf(rs.getInt("id_tipo")).equals("1")){
                        row.put("adicionales",getInv_ListaProductosFormulaIntermedioPdf(String.valueOf(rs.getInt("producto_elemento_id"))));
                    }else{
                        row.put("adicionales",rs.getInt("id_tipo"));
                    }

                    return row;
                }
            }
        );
        return hm_grid;
    }

    //metodo para obtener los componentes de la formula de tipo intermedio
    private ArrayList<HashMap<String, String>> getInv_ListaProductosFormulaIntermedioPdf(String formula_id) {

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

        //System.out.println("Obtiene datos pdf ruta: "+sql_query);
        ArrayList<HashMap<String, String>> hm_grid = (ArrayList<HashMap<String, String>>) this.jdbcTemplate.query(
            sql_query,
            new Object[]{}, new RowMapper() {
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    HashMap<String, String> row = new HashMap<String, String>();
                    row.put("producto_elemento_id",String.valueOf(rs.getInt("producto_elemento_id")));
                    row.put("descripcion","  "+rs.getString("descripcion"));
                    row.put("sku","  "+rs.getString("sku"));
                    row.put("unidad","  "+rs.getString("unidad"));
                    row.put("cantidad",StringHelper.roundDouble(rs.getString("cantidad"),2));
                    row.put("tipo","  "+rs.getString("tipo"));
                    row.put("id_tipo",rs.getString(rs.getInt("id_tipo")));
                    row.put("adicionales",rs.getString(rs.getInt("id_tipo")));

                    return row;
                }
            }
        );
        return hm_grid;
    }


    //metoodo para pdf de formulas
    @Override
    public HashMap<String, String> getInv_DatosFormulaPdf(Integer formula_id) {

        HashMap<String, String> datos = new HashMap<String, String>();

        String sql_query = "select inv_prod.descripcion,inv_formulas.nivel, inv_formulas.inv_prod_id_master, "
                + "inv_prod.sku from inv_formulas join inv_prod on inv_prod.id=inv_formulas.inv_prod_id_master where "
                + "inv_prod_id_master="+formula_id+" limit 1";

        //System.out.println("DATOS PARA EL PDF:"+sql_query);
        Map<String, Object> map = this.getJdbcTemplate().queryForMap(sql_query);

        datos.put("folio", map.get("inv_prod_id_master").toString());
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
    public ArrayList<HashMap<String, String>> getInv_DatosFormulaEspecificacionesPdf(Integer formula_id) {

        HashMap<String, String> datos = new HashMap<String, String>();
        ArrayList<HashMap<String, String>> hm_grid = new ArrayList<HashMap<String, String>>();

        String sql_busqueda = "select count(pro_proc_esp.id) as cantidad from pro_subproceso_prod join pro_proc_esp on pro_proc_esp.pro_subproceso_prod_id=pro_subproceso_prod.id where "
                + "pro_subproceso_prod.inv_prod_id="+formula_id+" limit 1";
        //esto es para revisar que exista el registro
        int rowCount = this.getJdbcTemplate().queryForInt(sql_busqueda);

        //System.out.println("DATOS PARA EL PDF:"+sql_busqueda);

        //si rowCount es mayor que cero si se encontro registro y extraemos el valor
        if (rowCount > 0){

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


            //System.out.println("Obtiene datos pdf ruta: "+sql_query);
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
                        row.put("fineza_inicial", String.valueOf(rs.getInt("fineza_inicial")));
                        row.put("viscosidads_inicial", String.valueOf(rs.getInt("viscosidads_inicial")));
                        row.put("viscosidadku_inicial", String.valueOf(rs.getDouble("viscosidadku_inicial")));
                        row.put("viscosidadcps_inicial", String.valueOf(rs.getInt("viscosidadcps_inicial")));
                        row.put("densidad_inicial", String.valueOf(rs.getDouble("densidad_inicial")));
                        row.put("volatiles_inicial", String.valueOf(rs.getDouble("volatiles_inicial")));
                        row.put("hidrogeno_inicial", String.valueOf(rs.getDouble("hidrogeno_inicial")));
                        row.put("cubriente_inicial", String.valueOf(rs.getDouble("cubriente_inicial")));
                        row.put("tono_inicial", String.valueOf(rs.getDouble("tono_inicial")));
                        row.put("brillo_inicial", String.valueOf(rs.getDouble("brillo_inicial")));
                        row.put("dureza_inicial", rs.getString("dureza_inicial"));
                        row.put("adherencia_inicial", String.valueOf(rs.getDouble("adherencia_inicial")));

                        row.put("fineza_final", String.valueOf(rs.getInt("fineza_final")));
                        row.put("viscosidads_final", String.valueOf(rs.getInt("viscosidads_final")));
                        row.put("viscosidadku_final", String.valueOf(rs.getDouble("viscosidadku_final")));
                        row.put("viscosidadcps_final", String.valueOf(rs.getInt("viscosidadcps_final")));
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
    //termina metodos para el catalogo de formulas



    //metodo para obtener los procedidmientos de la formula de tipo intermedio
    public ArrayList<HashMap<String, String>> getInv_DatosFormulaProcedidmientoPdf(Integer formula_id) {

        HashMap<String, String> datos = new HashMap<String, String>();
        ArrayList<HashMap<String, String>> hm_grid = new ArrayList<HashMap<String, String>>();

        String sql_busqueda = "select count(pro_proc_procedimiento.id) as cantidad from "
                + "pro_subproceso_prod join pro_proc_procedimiento on pro_proc_procedimiento.pro_subproceso_prod_id=pro_subproceso_prod.id "
                + "where pro_subproceso_prod.inv_prod_id="+formula_id+" limit 1";

        //esto es para revisar que exista el registro
        int rowCount = this.getJdbcTemplate().queryForInt(sql_busqueda);

        //System.out.println("DATOS PARA EL PDF:"+sql_busqueda);

        //si rowCount es mayor que cero si se encontro registro y extraemos el valor
        if (rowCount > 0){
            String sql_query = "select pro_proc_procedimiento.id, pro_proc_procedimiento.posicion, pro_proc_procedimiento.descripcion, "
                    + "pro_subprocesos.titulo from pro_subproceso_prod join pro_proc_procedimiento on "
                    + "pro_proc_procedimiento.pro_subproceso_prod_id=pro_subproceso_prod.id join pro_subprocesos on "
                    + "pro_subproceso_prod.pro_subprocesos_id=pro_subprocesos.id where pro_subproceso_prod.inv_prod_id="+formula_id+" order by "
                    + "pro_subprocesos.id,pro_proc_procedimiento.posicion";

            //System.out.println("Obtiene datos pdf ruta: "+sql_query);
            hm_grid = (ArrayList<HashMap<String, String>>) this.jdbcTemplate.query(
                sql_query,
                new Object[]{}, new RowMapper() {
                    @Override
                    public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                        HashMap<String, String> row = new HashMap<String, String>();
                        row.put("id",String.valueOf(rs.getInt("id")));
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



    /***Metodos para Orden de Entrada******/
    @Override
    public ArrayList<HashMap<String, Object>> getInvOrdenEntrada_PaginaGrid(String data_string, int offset, int pageSize, String orderBy, String asc) {
        String sql_busqueda = "SELECT id FROM gral_bus_catalogos(?) AS foo (id integer)";

	String sql_to_query = "SELECT inv_oent.id, "
				+"inv_oent.folio, "
                                + "(CASE WHEN inv_oent.tipo_documento=1 AND cxp_prov.razon_social IS NOT NULL THEN cxp_prov.razon_social "
                                      + "WHEN inv_oent.tipo_documento=2 AND cxp_prov.razon_social IS NOT NULL THEN cxp_prov.razon_social "
                                      + "WHEN inv_oent.tipo_documento=3 OR inv_oent.tipo_documento=4 THEN inv_mov_tipos.descripcion "
                                + "ELSE '' END) AS origen, "
				+"(case when inv_oent.orden_de_compra IS NULL THEN '' ELSE inv_oent.orden_de_compra END ) AS orden_compra, "
				+"inv_oent.folio_documento, "
				+"to_char(inv_oent.fecha_exp::timestamp with time zone,'dd/mm/yyyy') as fecha_documento, "
				+"to_char(inv_oent.momento_creacion,'dd/mm/yyyy') as fecha_entrada, "
                                +"(CASE WHEN inv_oent.cancelacion=FALSE THEN '' ELSE 'CANCELADO' END) AS estado "
			+"FROM inv_oent "
			+"LEFT JOIN cxp_prov on cxp_prov.id=inv_oent.cxp_prov_id "
                        +"LEFT JOIN inv_mov_tipos ON inv_mov_tipos.id=inv_oent.inv_mov_tipo_id "
                        +"JOIN ("+sql_busqueda+") as subt on subt.id=inv_oent.id "
                        +"ORDER BY "+orderBy+" "+asc+" LIMIT ? OFFSET ?";
        //System.out.println("Busqueda GetPage: "+sql_to_query);
        //System.out.println("data_string: "+data_string);

        //log.log(Level.INFO, "Ejecutando query de {0}", sql_to_query);
        ArrayList<HashMap<String, Object>> hm = (ArrayList<HashMap<String, Object>>) this.jdbcTemplate.query(
            sql_to_query,
            new Object[]{new String(data_string),new Integer(pageSize),new Integer(offset)}, new RowMapper() {
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    HashMap<String, Object> row = new HashMap<String, Object>();
                    row.put("id",rs.getString("id"));
                    row.put("folio",rs.getString("folio"));
                    row.put("origen",rs.getString("origen"));
                    row.put("orden_compra",rs.getString("orden_compra"));
                    row.put("folio_doc",rs.getString("folio_documento"));
                    row.put("fecha_doc",rs.getString("fecha_documento"));
                    row.put("fecha_entrada",rs.getString("fecha_entrada"));
                    row.put("estado",rs.getString("estado"));
                    return row;
                }
            }
        );
        return hm;
    }


    @Override
    public ArrayList<HashMap<String, String>> getInvOrdenEntrada_Datos(Integer id) {
        String sql_to_query = ""
                + "SELECT "
                    + "inv_oent.id, "
                    + "inv_oent.folio, "
                    + "inv_oent.inv_mov_tipo_id AS tipo_movimiento_id, "
                    + "inv_oent.cxp_prov_id AS id_prov, "
                    + "(CASE WHEN cxp_prov.rfc IS NULL THEN '' ELSE cxp_prov.rfc END) AS rfc, "
                    + "(CASE WHEN cxp_prov.razon_social IS NULL THEN '' ELSE cxp_prov.razon_social END) AS proveedor, "
                    + "(CASE WHEN cxp_prov.proveedortipo_id IS NULL THEN 0 ELSE cxp_prov.proveedortipo_id END ) AS proveedortipo_id, "
                    + "inv_oent.folio_documento, "
                    + "inv_oent.fecha_exp AS fecha_documento, "
                    + "to_char(inv_oent.momento_creacion,'dd-mm-yyyy') AS fecha_entrada, "
                    + "inv_oent.moneda_id, "
                    + "inv_oent.tipo_de_cambio AS tipo_cambio, "
                    + "inv_oent.numero_guia, "
                    + "inv_oent.orden_de_compra AS orden_compra, "
                    + "inv_oent.observaciones, "
                    + "(CASE WHEN inv_oent.cancelacion=FALSE THEN '' ELSE 'CANCELADO' END) AS cancelado,  "
                    + "inv_oent.inv_alm_id, "
                    + "inv_oent.tipo_documento,  "
                    + "inv_oent.monto_flete, "
                    + "inv_oent.subtotal,"
                    + "inv_oent.monto_ieps,  "
                    + "inv_oent.monto_iva,  "
                    + "inv_oent.monto_retencion, "
                    + "inv_oent.monto_total, "
                    + "inv_oent.tasa_iva, "
                    + "inv_oent.tasa_retencion, "
                    + "inv_oent.estatus "
                + "FROM inv_oent "
                + "LEFT JOIN cxp_prov ON cxp_prov.id=inv_oent.cxp_prov_id "
                + "WHERE inv_oent.id=?;";

        //log.log(Level.INFO, "Ejecutando query de {0}", sql_to_query);
        ArrayList<HashMap<String, String>> hm = (ArrayList<HashMap<String, String>>) this.jdbcTemplate.query(
            sql_to_query,
            new Object[]{new Integer(id)}, new RowMapper(){
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    HashMap<String, String> row = new HashMap<String, String>();
                    row.put("id",String.valueOf(rs.getInt("id")));
                    row.put("estatus",String.valueOf(rs.getInt("estatus")));
                    row.put("folio",rs.getString("folio"));
                    row.put("tipo_movimiento_id",String.valueOf(rs.getInt("tipo_movimiento_id")));
                    row.put("proveedor_id",String.valueOf(rs.getInt("id_prov")));
                    row.put("id_tipo_prov",String.valueOf(rs.getInt("proveedortipo_id")));
                    row.put("rfc_proveedor",rs.getString("rfc"));
                    row.put("nombre_proveedor",rs.getString("proveedor"));
                    row.put("folio_doc",rs.getString("folio_documento"));
                    row.put("fecha_doc",rs.getString("fecha_documento"));
                    row.put("fecha_entrada",rs.getString("fecha_entrada"));
                    row.put("id_moneda",rs.getString("moneda_id"));
                    row.put("tipo_cambio",StringHelper.roundDouble(rs.getString("tipo_cambio"),4));
                    row.put("numero_guia",rs.getString("numero_guia"));
                    row.put("orden_compra",rs.getString("orden_compra"));
                    row.put("observaciones",rs.getString("observaciones"));
                    row.put("cancelado",rs.getString("cancelado"));
                    row.put("id_alm_destino",String.valueOf(rs.getInt("inv_alm_id")));
                    row.put("tipo_doc",rs.getString("tipo_documento"));
                    row.put("flete",StringHelper.roundDouble(rs.getString("monto_flete"),2));
                    row.put("subtotal",StringHelper.roundDouble(rs.getString("subtotal"),2));
                    row.put("ieps",StringHelper.roundDouble(rs.getString("monto_ieps"),2));
                    row.put("iva",StringHelper.roundDouble(rs.getString("monto_iva"),2));
                    row.put("retencion",StringHelper.roundDouble(rs.getString("monto_retencion"),2));
                    row.put("total",StringHelper.roundDouble(rs.getString("monto_total"),2));
                    row.put("tasa_iva",StringHelper.roundDouble(rs.getString("tasa_iva"),2));
                    row.put("tasa_retencion",StringHelper.roundDouble(rs.getString("tasa_retencion"),2));
                    return row;
                }
            }
        );
        return hm;
    }


    @Override
    public ArrayList<HashMap<String, String>> getInvOrdenEntrada_DatosGrid(Integer id) {
                String sql_to_query = ""
                        + "SELECT "
                            + "inv_oent_detalle.id AS id_detalle,"
                            + "inv_oent_detalle.inv_prod_id, "
                            + "inv_prod.sku, "
                            + "inv_prod.descripcion, "
                            + "inv_prod_unidades.titulo as unidad, "
                            + "(CASE WHEN inv_prod_presentaciones.id IS NULL THEN 0 ELSE inv_prod_presentaciones.id END) AS id_presentacion, "
                            + "(CASE WHEN inv_prod_presentaciones.titulo IS NULL THEN '' ELSE inv_prod_presentaciones.titulo END) AS presentacion, "
                            + "inv_oent_detalle.costo_unitario, "
                            + "inv_oent_detalle.cantidad, "
                            + "inv_oent_detalle.cantidad_rec, "
                            + "(inv_oent_detalle.costo_unitario * inv_oent_detalle.cantidad) as importe, "
                            + "inv_oent_detalle.gral_imp_id, "
                            + "inv_oent_detalle.valor_imp, "
                            + "inv_prod_unidades.decimales, "
                            + "inv_prod.requiere_numero_serie, "
                            + "inv_prod.requiere_numero_lote, "
                            + "inv_prod.requiere_pedimento, "
                            + "inv_prod.requiere_nom "
                        + "FROM inv_oent_detalle "
                        + "LEFT JOIN inv_prod on inv_prod.id = inv_oent_detalle.inv_prod_id "
                        + "LEFT JOIN inv_prod_unidades on inv_prod_unidades.id = inv_prod.unidad_id "
                        + "LEFT JOIN inv_prod_presentaciones on inv_prod_presentaciones.id = inv_oent_detalle.inv_prod_presentacion_id "
                        + "WHERE inv_oent_detalle.inv_oent_id=? "
                        + "ORDER BY inv_oent_detalle.inv_oent_id;";

        //log.log(Level.INFO, "Ejecutando query de {0}", sql_to_query);
        ArrayList<HashMap<String, String>> hm = (ArrayList<HashMap<String, String>>) this.jdbcTemplate.query(
            sql_to_query,
            new Object[]{new Integer(id)}, new RowMapper(){
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    HashMap<String, String> row = new HashMap<String, String>();
                    row.put("id_detalle",String.valueOf(rs.getInt("id_detalle")));
                    row.put("producto_id",String.valueOf(rs.getInt("inv_prod_id")));
                    row.put("codigo",rs.getString("sku"));
                    row.put("titulo",rs.getString("descripcion"));
                    row.put("unidad",rs.getString("unidad"));
                    row.put("id_presentacion",rs.getString("id_presentacion"));
                    row.put("presentacion",rs.getString("presentacion"));
                    row.put("costo_unitario",StringHelper.roundDouble(rs.getString("costo_unitario"),2));
                    row.put("cantidad",StringHelper.roundDouble(rs.getString("cantidad"),2));
                    row.put("cant_rec",StringHelper.roundDouble(rs.getString("cantidad_rec"),2));
                    row.put("importe",StringHelper.roundDouble(rs.getString("importe"),2));
                    row.put("id_impuesto",rs.getString("gral_imp_id"));
                    row.put("valor_imp",StringHelper.roundDouble(rs.getString("valor_imp"),2));
                    row.put("decimales",rs.getString("decimales"));
                    row.put("req_serie",String.valueOf(rs.getBoolean("requiere_numero_serie")));
                    row.put("req_lote",String.valueOf(rs.getBoolean("requiere_numero_lote")));
                    row.put("req_pedimento",String.valueOf(rs.getBoolean("requiere_pedimento")));
                    row.put("req_nom",String.valueOf(rs.getBoolean("requiere_nom")));
                    return row;
                }
            }
        );
        return hm;
    }



    @Override
    public ArrayList<HashMap<String, String>> getInvOrdenEntrada_DatosGridLotes(Integer id) {
                String sql_to_query = ""
                        + "SELECT "
                            + "inv_oent_detalle.id AS id_detalle_oent,"
                            + "inv_lote.id AS id_lote, "
                            + "inv_lote.inv_prod_id AS inv_prod_id_lote, "
                            + "inv_lote.inv_alm_id AS inv_alm_id_lote, "
                            + "inv_lote.lote_int, "
                            + "(CASE WHEN inv_lote.lote_prov IS NULL THEN '' ELSE inv_lote.lote_prov END) AS lote_prov_lote, "
                            + "inv_lote.inicial AS cantidad_lote, "
                            + "(CASE WHEN inv_lote.pedimento='' OR inv_lote.pedimento IS NULL THEN ' ' ELSE inv_lote.pedimento END) AS pedimento,"
                            +"(CASE WHEN to_char(inv_lote.caducidad,'yyyymmdd') = '29991231' THEN ''::character varying ELSE inv_lote.caducidad::character varying END) AS caducidad "
                        + "FROM inv_oent_detalle "
                        + "JOIN inv_lote ON inv_lote.inv_oent_detalle_id=inv_oent_detalle.id  "
                        + "JOIN inv_prod on inv_prod.id = inv_lote.inv_prod_id  "
                        + "WHERE inv_oent_detalle.inv_oent_id=? "
                        + "ORDER BY inv_lote.id;";

        //System.out.println(sql_to_query);
        //System.out.println("id: "+id);
        ArrayList<HashMap<String, String>> hm = (ArrayList<HashMap<String, String>>) this.jdbcTemplate.query(
            sql_to_query,
            new Object[]{ new Integer(id) }, new RowMapper(){
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    HashMap<String, String> row = new HashMap<String, String>();
                    row.put("id_detalle_oent",String.valueOf(rs.getInt("id_detalle_oent")));
                    row.put("id_lote",String.valueOf(rs.getInt("id_lote")));
                    row.put("inv_prod_id_lote",String.valueOf(rs.getInt("inv_prod_id_lote")));
                    row.put("inv_alm_id_lote",String.valueOf(rs.getInt("inv_alm_id_lote")));
                    row.put("lote_int",rs.getString("lote_int"));
                    row.put("lote_prov_lote",rs.getString("lote_prov_lote"));
                    row.put("cantidad_lote",StringHelper.roundDouble(rs.getString("cantidad_lote"),2));
                    row.put("ped_lote",rs.getString("pedimento"));
                    row.put("cad_lote",rs.getString("caducidad"));
                    return row;
                }
            }
        );
        return hm;
    }


    @Override
    public HashMap<String, String> getInvOrdenEntrada_Datos_PDF(Integer id) {
        HashMap<String, String> data = new HashMap<String, String>();

        String sql_to_query = "select  inv_oent.id,"
                                    + "inv_oent.folio,"
                                    +" to_char(inv_oent.momento_creacion,'dd/mm/yyyy') AS fecha_orden_entrada,"
                                    +" (CASE WHEN inv_oent.orden_de_compra IS NULL OR inv_oent.orden_de_compra='' THEN '-' ELSE inv_oent.orden_de_compra END) AS orden_compra,    "
                                    +" (CASE WHEN inv_oent.cxp_prov_id IS NULL THEN 0 ELSE inv_oent.cxp_prov_id END ) AS id_prov,"
                                    +" inv_oent.moneda_id,"
                                    + "gral_mon.descripcion AS moneda,"
                                    + "gral_mon.descripcion_abr AS moneda_abr,"
                                    + "gral_mon.simbolo AS moneda_simbolo, "
                                    +" inv_oent.tipo_de_cambio,"
                                    +" inv_oent.observaciones,"
                                    + "(CASE WHEN inv_oent.tipo_documento=1 AND cxp_prov.razon_social IS NOT NULL THEN 'PROVEEDOR' "
                                          + "WHEN inv_oent.tipo_documento=2 AND cxp_prov.razon_social IS NOT NULL THEN 'PROVEEDOR' "
                                          + "WHEN inv_oent.tipo_documento=3 THEN 'AJUSTE' "
                                          + "WHEN inv_oent.tipo_documento=4 THEN 'PRODUCCION' "
                                    + "ELSE '' END) AS origen, "
                                    + "(CASE WHEN inv_oent.tipo_documento=1 AND cxp_prov.razon_social IS NOT NULL THEN cxp_prov.razon_social "
                                          + "WHEN inv_oent.tipo_documento=2 AND cxp_prov.razon_social IS NOT NULL THEN cxp_prov.razon_social "
                                          + "WHEN inv_oent.tipo_documento=3 OR inv_oent.tipo_documento=4 THEN inv_mov_tipos.descripcion "
                                    + "ELSE '' END) AS proveedor_tipo_movimiento,"
                                    + "inv_oent.monto_flete, "
                                    + "inv_oent.subtotal,  "
                                    + "inv_oent.monto_ieps, "
                                    + "inv_oent.monto_iva,  "
                                    + "inv_oent.monto_retencion, "
                                    + "inv_oent.monto_total,"
                                    + "gral_empleados.nombre_pila||' '||gral_empleados.apellido_paterno||' '||gral_empleados.apellido_materno AS nombre_usuario_elaboro "
                                +" FROM inv_oent  "
                                + "LEFT JOIN cxp_prov on cxp_prov.id=inv_oent.cxp_prov_id   "
                                + "LEFT JOIN inv_mov_tipos ON inv_mov_tipos.id=inv_oent.inv_mov_tipo_id "
                                + "JOIN gral_usr ON gral_usr.id=inv_oent.gral_usr_id_actualizacion "
                                + "LEFT JOIN gral_empleados ON gral_empleados.id = gral_usr.gral_empleados_id "
                                + "LEFT JOIN gral_mon ON gral_mon.id=inv_oent.moneda_id "
                                + "WHERE inv_oent.id="+id+";";
        //System.out.println("Header PDF OENT::::"+sql_to_query);

        Map<String, Object> map = this.getJdbcTemplate().queryForMap(sql_to_query);
        data.put("id",String.valueOf(map.get("id")));
        data.put("folio",map.get("folio").toString());
        data.put("fecha_orden_entrada",map.get("fecha_orden_entrada").toString());
        data.put("orden_compra",map.get("orden_compra").toString());
        data.put("proveedor_id",map.get("id_prov").toString());
        data.put("moneda_id",map.get("moneda_id").toString());
        data.put("moneda",map.get("moneda").toString());
        data.put("moneda_abr",map.get("moneda_abr").toString());
        data.put("moneda_simbolo",map.get("moneda_simbolo").toString());
        data.put("observaciones",map.get("observaciones").toString());
        data.put("origen",map.get("origen").toString());
        data.put("proveedor_tipo_movimiento",map.get("proveedor_tipo_movimiento").toString());
        data.put("flete",StringHelper.roundDouble(map.get("monto_flete").toString(),2));
        data.put("subtotal",StringHelper.roundDouble(map.get("subtotal").toString(),2));
        data.put("monto_ieps",StringHelper.roundDouble(map.get("monto_ieps").toString(),2));
        data.put("iva",StringHelper.roundDouble(map.get("monto_iva").toString(),2));
        data.put("retencion",StringHelper.roundDouble(map.get("monto_retencion").toString(),2));
        data.put("total",StringHelper.roundDouble(map.get("monto_total").toString(),2));
        data.put("nombre_usuario_elaboro",map.get("nombre_usuario_elaboro").toString());
        return data;
    }

    /***Termina Metodos para Orden de Entrada******/


    /************************************************************************************
     *INICIA METODOS PARA ORDENES DE SALIDA
     *************************************************************************************/

    //metodo  para el grid y paginado
    @Override
    public ArrayList<HashMap<String, Object>> getInvOrdenSalida_PaginaGrid(String data_string, int offset, int pageSize, String orderBy, String asc) {
        String sql_busqueda = "SELECT id FROM gral_bus_catalogos(?) AS foo (id integer)";

	String sql_to_query = "SELECT inv_osal.id, "
				+"inv_osal.folio, "
                                +"(CASE WHEN inv_osal.momento_confirmacion IS NULL THEN '' ELSE to_char(inv_osal.momento_confirmacion,'dd/mm/yyyy') END ) AS fecha_salida, "
                                + "(CASE WHEN inv_osal.tipo_documento=1 AND cxc_clie.razon_social IS NOT NULL THEN cxc_clie.razon_social "
                                      + "WHEN inv_osal.tipo_documento=2 AND cxc_clie.razon_social IS NOT NULL THEN cxc_clie.razon_social "
                                      + "WHEN inv_osal.tipo_documento=3 OR inv_osal.tipo_documento=4 THEN inv_mov_tipos.descripcion "
                                      + "WHEN inv_osal.tipo_documento=5 THEN cxp_prov.razon_social "
                                + "ELSE '' END) AS origen, "
				+"inv_osal.orden_compra, "
				+"(CASE WHEN pro_ordenprod_invosal.inv_osal_id IS NULL THEN inv_osal.folio_documento ELSE pro_orden_prod.folio END) AS folio_documento, "
				+"to_char(inv_osal.fecha_exp,'dd/mm/yyyy') AS fecha_documento, "
                                +"(CASE WHEN inv_osal.cancelacion=FALSE THEN '' ELSE 'CANCELADO' END) AS estado "
			+"FROM inv_osal "
			+"LEFT JOIN cxc_clie ON cxc_clie.id = inv_osal.cxc_clie_id "
                        +"LEFT JOIN cxp_prov ON cxp_prov.id=inv_osal.cxp_prov_id "
                        +"LEFT JOIN inv_mov_tipos ON inv_mov_tipos.id=inv_osal.inv_mov_tipo_id "
                        +"LEFT JOIN pro_ordenprod_invosal ON pro_ordenprod_invosal.inv_osal_id=inv_osal.id "
                        +"LEFT JOIN pro_orden_prod ON pro_orden_prod.id=pro_ordenprod_invosal.pro_orden_prod_id "
                        +"JOIN ("+sql_busqueda+") as subt on subt.id=inv_osal.id "
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
                    row.put("fecha_sal",rs.getString("fecha_salida"));
                    row.put("origen",rs.getString("origen"));
                    row.put("oc",rs.getString("orden_compra"));
                    row.put("folio_doc",rs.getString("folio_documento"));
                    row.put("fecha_doc",rs.getString("fecha_documento"));
                    row.put("estado",rs.getString("estado"));
                    return row;
                }
            }
        );
        return hm;
    }




    @Override
    public ArrayList<HashMap<String, String>> getInvOrdenSalida_Datos(Integer id) {
        String sql_to_query = ""
                + "SELECT "
                    + "inv_osal.id,"
                    + "inv_osal.folio,"
                    +"(CASE WHEN inv_osal.momento_confirmacion IS NULL THEN '' ELSE to_char(inv_osal.momento_confirmacion,'dd/mm/yyyy') END ) AS fecha_salida, "
                    + "inv_osal.inv_mov_tipo_id AS tipo_movimiento_id, "
                    + "inv_osal.estatus AS estado,"
                    + "inv_osal.tipo_documento AS tipo_doc,"
                    + "inv_osal.folio_documento AS folio_doc,"
                    + "inv_osal.fecha_exp AS fecha_doc,"
                    + "inv_osal.cxp_prov_id AS id_prov,"
                    + "(CASE WHEN inv_osal.cxp_prov_id > 0 THEN  'Proveedor' ELSE 'Cliente' END ) AS origen_salida, "
                    + "(CASE WHEN inv_osal.cxp_prov_id > 0 THEN cxp_prov.id ELSE  (CASE WHEN inv_osal.cxc_clie_id IS NULL THEN 0 ELSE inv_osal.cxc_clie_id END) END) AS id_cliente,"
                    + "(CASE WHEN inv_osal.cxp_prov_id > 0 THEN cxp_prov.razon_social ELSE (CASE WHEN cxc_clie.razon_social IS NULL THEN '' ELSE cxc_clie.razon_social END ) END) AS razon_cliente,"
                    + "inv_osal.inv_alm_id AS id_almacen,"
                    + "inv_osal.subtotal,"
                    + "inv_osal.monto_ieps,"
                    + "inv_osal.monto_iva,"
                    + "inv_osal.monto_retencion,"
                    + "inv_osal.monto_total,"
                    + "inv_osal.folio_pedido,"
                    + "inv_osal.orden_compra,"
                    + "inv_osal.moneda_id,"
                    + "inv_osal.tipo_cambio,"
                    + "inv_osal.observaciones,"
                    + "inv_osal.cancelacion "
                + "FROM inv_osal "
                + "LEFT JOIN cxc_clie ON cxc_clie.id = inv_osal.cxc_clie_id "
                + "LEFT JOIN cxp_prov ON cxp_prov.id = inv_osal.cxp_prov_id "
                + "WHERE inv_osal.id="+ id + ";";

        //log.log(Level.INFO, "Ejecutando query de {0}", sql_to_query);
        ArrayList<HashMap<String, String>> hm = (ArrayList<HashMap<String, String>>) this.jdbcTemplate.query(
            sql_to_query,
            new Object[]{}, new RowMapper(){
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    HashMap<String, String> row = new HashMap<String, String>();
                    row.put("id",String.valueOf(rs.getInt("id")));
                    row.put("folio",rs.getString("folio"));
                    row.put("estado",String.valueOf(rs.getInt("estado")));
                    row.put("tipo_doc",String.valueOf(rs.getInt("tipo_doc")));
                    row.put("folio_doc",rs.getString("folio_doc"));
                    row.put("fecha_doc",rs.getString("fecha_doc"));
                    row.put("id_prov",String.valueOf(rs.getInt("id_prov")));
                    row.put("id_cliente",String.valueOf(rs.getInt("id_cliente")));
                    row.put("razon_cliente",rs.getString("razon_cliente"));
                    row.put("origen_salida",rs.getString("origen_salida"));
                    row.put("id_almacen",String.valueOf(rs.getInt("id_almacen")));
                    row.put("subtotal",StringHelper.roundDouble(rs.getString("subtotal"),2));
                    row.put("ieps",StringHelper.roundDouble(rs.getString("monto_ieps"),2));
                    row.put("iva",StringHelper.roundDouble(rs.getString("monto_iva"),2));
                    row.put("retencion",StringHelper.roundDouble(rs.getString("monto_retencion"),2));
                    row.put("total",StringHelper.roundDouble(rs.getString("monto_total"),2));
                    row.put("tipo_cambio",StringHelper.roundDouble(rs.getString("tipo_cambio"),4));
                    row.put("moneda_id",String.valueOf(rs.getInt("moneda_id")));
                    row.put("folio_pedido",rs.getString("folio_pedido"));
                    row.put("orden_compra",rs.getString("orden_compra"));
                    row.put("observaciones",rs.getString("observaciones"));
                    row.put("cancelacion",String.valueOf(rs.getBoolean("cancelacion")));
                    row.put("tipo_movimiento_id",String.valueOf(rs.getInt("tipo_movimiento_id")));

                    return row;
                }
            }
        );
        return hm;
    }



    @Override
    public ArrayList<HashMap<String, String>> getInvOrdenSalida_DatosGrid(Integer id) {
                String sql_to_query = ""
                        + "SELECT "
                            + "inv_osal_detalle.id AS id_detalle,"
                            + "inv_osal_detalle.inv_prod_id, "
                            + "inv_prod.sku, "
                            + "inv_prod.descripcion, "
                            + "inv_prod_unidades.titulo as unidad, "
                            + "(CASE WHEN inv_prod_presentaciones.id IS NULL THEN 0 ELSE inv_prod_presentaciones.id END) AS id_presentacion, "
                            + "(CASE WHEN inv_prod_presentaciones.titulo IS NULL THEN '' ELSE inv_prod_presentaciones.titulo END) AS presentacion, "
                            + "inv_osal_detalle.precio_unitario, "
                            + "inv_osal_detalle.cantidad, "
                            + "inv_osal_detalle.cantidad_sur, "
                            + "(inv_osal_detalle.precio_unitario * inv_osal_detalle.cantidad) as importe, "
                            + "inv_prod.requiere_numero_serie, "
                            + "inv_prod.requiere_numero_lote, "
                            + "inv_prod.requiere_pedimento, "
                            + "inv_prod.requiere_nom "
                        + "FROM inv_osal_detalle "
                        + "LEFT JOIN inv_prod on inv_prod.id = inv_osal_detalle.inv_prod_id "
                        + "LEFT JOIN inv_prod_unidades on inv_prod_unidades.id = inv_osal_detalle.inv_prod_unidad_id "
                        + "LEFT JOIN inv_prod_presentaciones on inv_prod_presentaciones.id = inv_osal_detalle.inv_prod_presentacion_id "
                        + "WHERE inv_osal_detalle.inv_osal_id=? "
                        + "ORDER BY inv_osal_detalle.id;";

        //log.log(Level.INFO, "Ejecutando query de {0}", sql_to_query);
        ArrayList<HashMap<String, String>> hm = (ArrayList<HashMap<String, String>>) this.jdbcTemplate.query(
            sql_to_query,
            new Object[]{new Integer(id)}, new RowMapper(){
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    HashMap<String, String> row = new HashMap<String, String>();
                    row.put("id_detalle_osal",String.valueOf(rs.getInt("id_detalle")));
                    row.put("producto_id",String.valueOf(rs.getInt("inv_prod_id")));
                    row.put("codigo",rs.getString("sku"));
                    row.put("titulo",rs.getString("descripcion"));
                    row.put("unidad",rs.getString("unidad"));
                    row.put("id_presentacion",rs.getString("id_presentacion"));
                    row.put("presentacion",rs.getString("presentacion"));
                    row.put("precio_unitario",StringHelper.roundDouble(rs.getString("precio_unitario"),2));
                    row.put("cant_fac",StringHelper.roundDouble(rs.getString("cantidad"),4));
                    row.put("cant_sur",StringHelper.roundDouble(rs.getString("cantidad_sur"),4));
                    row.put("importe",StringHelper.roundDouble(rs.getString("importe"),2));
                    row.put("req_serie",String.valueOf(rs.getBoolean("requiere_numero_serie")));
                    row.put("req_lote",String.valueOf(rs.getBoolean("requiere_numero_lote")));
                    row.put("req_pedimento",String.valueOf(rs.getBoolean("requiere_pedimento")));
                    row.put("req_nom",String.valueOf(rs.getBoolean("requiere_nom")));
                    return row;
                }
            }
        );
        return hm;
    }



    //obtiene datos de un lota para agregar al grid
    @Override
    public ArrayList<HashMap<String, String>> getInvOrdenSalida_DatosLote(String no_lote, Integer id_producto, Integer id_almacen) {
        String sql_to_query = ""
                + "SELECT * FROM ( "
                    + "SELECT  "
                    + "inv_lote.id AS id_lote, "
                    + "inv_lote.lote_int, "
                    + "(inicial - salidas + entradas - reservado) AS exis_lote, "
                    + "(CASE WHEN to_char(inv_lote.caducidad,'yyyymmdd') = '29991231' THEN ''::character varying ELSE to_char(inv_lote.caducidad,'dd/mm/yyyy') END) AS caducidad, "
                    + "(CASE WHEN inv_lote.pedimento='' OR inv_lote.pedimento IS NULL THEN ' ' ELSE inv_lote.pedimento END) AS pedimento "
                    + "FROM inv_lote "
                    + "WHERE lote_int='"+no_lote+"' AND inv_lote.inv_prod_id="+id_producto+" AND inv_lote.inv_alm_id="+id_almacen+" "
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
                    row.put("exis_lote",StringHelper.roundDouble(rs.getString("exis_lote"),4));
                    row.put("caducidad",rs.getString("caducidad"));
                    row.put("pedimento",rs.getString("pedimento"));
                    return row;
                }
            }
        );
        return hm;
    }



    @Override
    public ArrayList<HashMap<String, String>> getInvOrdenSalida_DatosGridLotes(Integer id) {
        String sql_to_query = ""
                + "SELECT "
                    + "inv_lote_detalle.inv_osal_detalle_id AS id_osal_detalle, "
                    + "inv_lote_detalle.id AS id_lote_detalle, "
                    + "inv_lote.inv_alm_id AS id_almacen, "
                    + "inv_lote.inv_prod_id AS id_producto, "
                    + "inv_lote.lote_int, "
                    + "inv_lote_detalle.cantidad_sal, "
                    + "(CASE WHEN inv_lote.pedimento='' OR inv_lote.pedimento IS NULL THEN ' ' ELSE inv_lote.pedimento END) AS pedimento, "
                    + "(CASE WHEN to_char(inv_lote.caducidad,'yyyymmdd') = '29991231' THEN ''::character varying ELSE to_char(inv_lote.caducidad,'dd/mm/yyyy') END) AS caducidad  "
                + "FROM inv_osal_detalle "
                + "JOIN inv_lote_detalle ON inv_lote_detalle.inv_osal_detalle_id=inv_osal_detalle.id "
                + "JOIN inv_lote ON inv_lote.id=inv_lote_detalle.inv_lote_id "
                + "WHERE inv_osal_detalle.inv_osal_id=? "
                + "ORDER BY inv_lote.id;";
        
        //System.out.println(sql_to_query);
        //System.out.println("id: "+id);
        ArrayList<HashMap<String, String>> hm = (ArrayList<HashMap<String, String>>) this.jdbcTemplate.query(
            sql_to_query,
            new Object[]{ new Integer(id) }, new RowMapper(){
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    HashMap<String, String> row = new HashMap<String, String>();
                    row.put("id_osal_detalle",String.valueOf(rs.getInt("id_osal_detalle")));
                    row.put("id_lote_detalle",String.valueOf(rs.getInt("id_lote_detalle")));
                    row.put("id_producto",String.valueOf(rs.getInt("id_producto")));
                    row.put("id_almacen",String.valueOf(rs.getInt("id_almacen")));
                    row.put("lote_int",rs.getString("lote_int"));
                    row.put("cantidad_sal",StringHelper.roundDouble(rs.getString("cantidad_sal"),4));
                    row.put("ped_lote",rs.getString("pedimento"));
                    row.put("cad_lote",rs.getString("caducidad"));
                    return row;
                }
            }
        );
        return hm;
    }



    @Override
    public HashMap<String, String> getInvOrdenSalida_Datos_PDF(Integer id) {
        HashMap<String, String> data = new HashMap<String, String>();

        String sql_to_query = "SELECT inv_osal.id,  "
                                +"inv_osal.folio, "
                                +"inv_osal.moneda_id, "
                                +"inv_osal.observaciones, "
                                +"to_char(inv_osal.fecha_exp,'dd/mm/yyyy')as fecha_expedicion, "
                                +"(CASE WHEN inv_osal.orden_compra IS NULL OR inv_osal.orden_compra ='' THEN '-' else inv_osal.orden_compra END) AS  orden_compra,"
                                +"(CASE WHEN cxc_clie.id IS NULL THEN 0 ELSE cxc_clie.id END ) AS  cliente_id,"
                                +"(CASE WHEN inv_osal.momento_confirmacion IS NULL THEN '' ELSE to_char(inv_osal.momento_confirmacion,'dd/mm/yyyy') END ) AS fecha_salida, "
                                + "(CASE WHEN inv_osal.tipo_documento=1 AND cxc_clie.razon_social IS NOT NULL THEN 'CLIENTE' "
                                      + "WHEN inv_osal.tipo_documento=2 AND cxc_clie.razon_social IS NOT NULL THEN 'CLIENTE' "
                                      + "WHEN inv_osal.tipo_documento=3 THEN 'AJUSTE' "
                                      + "WHEN inv_osal.tipo_documento=4 THEN 'REQUISICION' "
                                      + "WHEN inv_osal.tipo_documento=5 THEN 'PROVEEDOR' "
                                + "ELSE '' END) AS origen, "
                                + "(CASE WHEN inv_osal.tipo_documento=1 AND cxc_clie.razon_social IS NOT NULL THEN cxc_clie.razon_social "
                                      + "WHEN inv_osal.tipo_documento=2 AND cxc_clie.razon_social IS NOT NULL THEN cxc_clie.razon_social "
                                      + "WHEN inv_osal.tipo_documento=3 OR inv_osal.tipo_documento=4 THEN inv_mov_tipos.descripcion "
                                      + "WHEN inv_osal.tipo_documento=5 THEN cxp_prov.razon_social||'\n\n'||inv_mov_tipos.descripcion "
                                + "ELSE '' END) AS proveedor_tipo_movimiento,"
                                + "inv_osal.subtotal,"
                                + "inv_osal.monto_ieps,"
                                + "inv_osal.monto_iva,"
                                + "inv_osal.monto_retencion,"
                                + "inv_osal.monto_total, "
                                + "(CASE WHEN gral_empleados.nombre_pila IS NULL THEN '' ELSE gral_empleados.nombre_pila||' '||gral_empleados.apellido_paterno||' '||gral_empleados.apellido_materno END )  AS nombre_usuario_elaboro "
                            +"FROM  inv_osal "
                            +"LEFT JOIN cxc_clie on cxc_clie.id=inv_osal.cxc_clie_id "
                            +"LEFT JOIN cxp_prov ON cxp_prov.id = inv_osal.cxp_prov_id "
                            +"LEFT JOIN inv_mov_tipos ON inv_mov_tipos.id=inv_osal.inv_mov_tipo_id "
                            +"LEFT JOIN gral_usr ON gral_usr.id=inv_osal.gral_usr_id_confirmacion "
                            +"LEFT JOIN gral_empleados ON gral_empleados.id = gral_usr.gral_empleados_id "
                            +"where inv_osal.id="+id+";";

        //System.out.println("Header PDF::::"+sql_to_query);

        Map<String, Object> map = this.getJdbcTemplate().queryForMap(sql_to_query);
        data.put("id",String.valueOf(map.get("id")));
        data.put("folio",map.get("folio").toString());
        data.put("moneda_id",map.get("moneda_id").toString());
        data.put("observaciones",map.get("observaciones").toString());
        data.put("fecha_expedicion",map.get("fecha_expedicion").toString());
        data.put("origen",map.get("origen").toString());
        data.put("proveedor_tipo_movimiento",map.get("proveedor_tipo_movimiento").toString());
        data.put("orden_compra",map.get("orden_compra").toString());
        data.put("cliente_id",map.get("cliente_id").toString());
        data.put("subtotal",StringHelper.roundDouble(map.get("subtotal").toString(),2));
        data.put("monto_ieps",StringHelper.roundDouble(map.get("monto_ieps").toString(),2));
        data.put("iva",StringHelper.roundDouble(map.get("monto_iva").toString(),2));
        data.put("retencion",StringHelper.roundDouble(map.get("monto_retencion").toString(),2));
        data.put("total",StringHelper.roundDouble(map.get("monto_total").toString(),2));
        data.put("nombre_usuario_elaboro",map.get("nombre_usuario_elaboro").toString());
        return data;
    }







    @Override
    public ArrayList<HashMap<String, String>> getEntradas_DatosCliente(Integer id_cliente) {
        String sql_to_query = "SELECT DISTINCT  cxc_clie.id, "
                                +" cxc_clie.rfc,  "
                                +" cxc_clie.razon_social,  "
                                +" cxc_clie.calle||' '||cxc_clie.numero||', '||cxc_clie.colonia||', '||gral_mun.titulo||', '||gral_edo.titulo||', '||gral_pais.titulo ||' C.P. '||cxc_clie.cp as direccion,   "
                                +" cxc_clie.calle,  "
                                +" cxc_clie.numero,  "
                                +" cxc_clie.colonia,  "
                                +" gral_mun.titulo AS municipio,  "
                                +" gral_edo.titulo AS estado,  "
                                +" gral_pais.titulo AS pais,  "
                                +" cxc_clie.telefono1,  "
                                +" cxc_clie.cp, "
                                +" cxc_clie.clienttipo_id  "
                                +" FROM cxc_clie  "
                                +" JOIN gral_pais ON gral_pais.id = cxc_clie.pais_id   "
                                +" JOIN gral_edo ON gral_edo.id = cxc_clie.estado_id   "
                                +" JOIN gral_mun ON gral_mun.id = cxc_clie.municipio_id "
                                + "WHERE cxc_clie.id="+ id_cliente;

        //log.log(Level.INFO, "Ejecutando query de {0}", sql_to_query);
        ArrayList<HashMap<String, String>> hm_datos_cliente = (ArrayList<HashMap<String, String>>) this.jdbcTemplate.query(
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
                    row.put("telefono",rs.getString("telefono1"));
                    row.put("clienttipo_id",rs.getString("clienttipo_id"));
                    return row;
                }
            }
        );
        return hm_datos_cliente;
    }


     //TERMINA METODOS PARA ORDENES DE SALIDA
     //*************************************************************************************




    //INICIA PROCEDIMIENTOS PARA AJUSTES DE INVENTARIO
    //este buscador es solo para Ajustes de Inventario
    @Override
    public ArrayList<HashMap<String, String>> getInvAjustes_BuscadorProductos(String sku, String tipo, String descripcion, Integer id_empresa, Integer id_almacen, Integer ano_actual) {
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
                    + "inv_prod.id, "
                    + "inv_prod.sku, "
                    + "inv_prod.descripcion,  "
                    + "inv_prod.unidad_id, "
                    + "inv_prod_unidades.titulo AS unidad, "
                    + "inv_prod_tipos.titulo AS tipo,"
                    + "inv_prod_unidades.decimales "
                + "FROM inv_prod "
                + "JOIN inv_exi ON inv_exi.inv_prod_id=inv_prod.id "
                + "LEFT JOIN inv_prod_tipos ON inv_prod_tipos.id=inv_prod.tipo_de_producto_id "
                + "LEFT JOIN inv_prod_unidades ON inv_prod_unidades.id=inv_prod.unidad_id  "
                + "WHERE inv_prod.empresa_id="+id_empresa+" "
                + "AND inv_prod.borrado_logico=FALSE AND inv_exi.inv_alm_id="+id_almacen+" "
                + "AND inv_exi.ano="+ano_actual+" "+where+" "
                + "ORDER BY inv_prod.descripcion;";
        //log.log(Level.INFO, "Ejecutando query de {0}", sql_to_query);

        ArrayList<HashMap<String, String>> hm_datos_productos = (ArrayList<HashMap<String, String>>) this.jdbcTemplate.query(
            sql_to_query,
            new Object[]{}, new RowMapper(){
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    HashMap<String, String> row = new HashMap<String, String>();
                    row.put("id",String.valueOf(rs.getInt("id")));
                    row.put("codigo",rs.getString("sku"));
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



    //busca datos de un producto para agregar al grid de ajustes
    @Override
    public ArrayList<HashMap<String, String>> getInvAjustes_DatosProducto(String sku, Integer id_empresa, Integer id_almacen, Integer ano_actual) {

        String sql_to_query = ""
                + "SELECT "
                    + "inv_prod.id AS id_producto, "
                    + "inv_prod.sku, "
                    + "inv_prod.descripcion,"
                    + "inv_prod_unidades.titulo AS unidad, "
                    + "inv_prod_unidades.decimales AS no_dec, "
                    + "inv_exi.inv_alm_id AS id_almacen, "
                    + "(inv_exi.exi_inicial - inv_exi.transito - inv_exi.reservado + inv_exi.entradas_1 + inv_exi.entradas_2 + inv_exi.entradas_3 + inv_exi.entradas_4 + inv_exi.entradas_5 + inv_exi.entradas_6 + inv_exi.entradas_7 + inv_exi.entradas_8 + inv_exi.entradas_9 + inv_exi.entradas_10 + inv_exi.entradas_11 + inv_exi.entradas_12 - inv_exi.salidas_1 - inv_exi.salidas_2 - inv_exi.salidas_3 - inv_exi.salidas_4 - inv_exi.salidas_5 - inv_exi.salidas_6 - inv_exi.salidas_7 - inv_exi.salidas_8 - inv_exi.salidas_9 - inv_exi.salidas_10 - inv_exi.salidas_11 - inv_exi.salidas_12) AS existencia "
                + "FROM inv_prod "
                + "JOIN inv_exi ON inv_exi.inv_prod_id=inv_prod.id "
                + "LEFT JOIN inv_prod_tipos ON inv_prod_tipos.id=inv_prod.tipo_de_producto_id "
                + "LEFT JOIN inv_prod_unidades ON inv_prod_unidades.id=inv_prod.unidad_id  "
                + "WHERE inv_prod.empresa_id="+id_empresa+" "
                + "AND inv_prod.borrado_logico=FALSE AND inv_exi.inv_alm_id="+id_almacen+" "
                + "AND inv_exi.ano="+ano_actual+" AND inv_prod.sku='"+sku+"' "
                + "ORDER BY inv_prod.descripcion;";
        //log.log(Level.INFO, "Ejecutando query de {0}", sql_to_query);

        ArrayList<HashMap<String, String>> hm = (ArrayList<HashMap<String, String>>) this.jdbcTemplate.query(
            sql_to_query,
            new Object[]{}, new RowMapper(){
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    HashMap<String, String> row = new HashMap<String, String>();
                    row.put("id_producto",String.valueOf(rs.getInt("id_producto")));
                    row.put("codigo",rs.getString("sku"));
                    row.put("descripcion",rs.getString("descripcion"));
                    row.put("unidad",rs.getString("unidad"));
                    row.put("no_dec",String.valueOf(rs.getInt("no_dec")));
                    row.put("id_almacen",String.valueOf(rs.getInt("id_almacen")));
                    row.put("existencia",StringHelper.roundDouble(rs.getString("existencia"),2));
                    return row;
                }
            }
        );
        return hm;
    }



    //obtiene los tipos de Movimietos para Ajustes
    @Override
    public ArrayList<HashMap<String, String>> getInvAjustes_TiposMovimiento(Integer id_empresa) {
        String sql_to_query = "SELECT id, titulo, grupo, tipo_costo FROM inv_mov_tipos WHERE borrado_logico=FALSE AND ajuste=TRUE ORDER BY titulo;";

        //log.log(Level.INFO, "Ejecutando query de {0}", sql_to_query);
        ArrayList<HashMap<String, String>> hm = (ArrayList<HashMap<String, String>>) this.jdbcTemplate.query(
            sql_to_query,
            new Object[]{}, new RowMapper(){
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    HashMap<String, String> row = new HashMap<String, String>();
                    row.put("id",String.valueOf(rs.getInt("id")));
                    row.put("titulo",rs.getString("titulo"));
                    row.put("grupo",String.valueOf(rs.getInt("grupo")));
                    row.put("tipo_costo",String.valueOf(rs.getInt("tipo_costo")));
                    return row;
                }
            }
        );
        return hm;
    }





    //obtiene costo promedio actual de un producto
    @Override
    public ArrayList<HashMap<String, String>> getInvAjustes_CostoPromedioActual(Integer id_prod, String fecha_actual) {
        String sql_to_query = "SELECT inv_obtiene_costo_promedio_actual as costo_promedio_actual FROM inv_obtiene_costo_promedio_actual("+id_prod+", '"+fecha_actual+"'::timestamp with time zone);";

        //System.out.println("Obteniendo costo promedio:"+sql_to_query);
        ArrayList<HashMap<String, String>> hm = (ArrayList<HashMap<String, String>>) this.jdbcTemplate.query(
            sql_to_query,
            new Object[]{}, new RowMapper(){
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    HashMap<String, String> row = new HashMap<String, String>();
                    row.put("costo_promedio",StringHelper.roundDouble(rs.getString("costo_promedio_actual"), 2));
                    return row;
                }
            }
        );
        return hm;
    }






    @Override
    public ArrayList<HashMap<String, Object>> getInvAjustes_PaginaGrid(String data_string, int offset, int pageSize, String orderBy, String asc) {
        String sql_busqueda = "SELECT id FROM gral_bus_catalogos(?) AS foo (id integer)";

	String sql_to_query = ""
                + "SELECT DISTINCT inv_mov.id, "
                    + "inv_mov.referencia AS folio_ajuste, "
                    + "(CASE WHEN inv_mov_tipos.grupo=0 THEN almdest.titulo WHEN inv_mov_tipos.grupo=2 THEN almorig.titulo ELSE '' END) AS almacen,"
                    + "inv_mov_tipos.titulo AS tipo_movimiento, "
                    + "to_char(inv_mov.fecha_mov,'dd/mm/yyyy') AS fecha_ajuste "
                + "FROM inv_mov "
                + "JOIN inv_mov_tipos ON inv_mov_tipos.id=inv_mov.inv_mov_tipo_id "
                + "JOIN inv_mov_detalle ON inv_mov_detalle.inv_mov_id=inv_mov.id "
                + "LEFT JOIN inv_alm AS almorig ON almorig.id=inv_mov_detalle.alm_origen_id "
                + "LEFT JOIN inv_alm AS almdest ON almdest.id=inv_mov_detalle.alm_destino_id "
                +"JOIN ("+sql_busqueda+") as subt on subt.id=inv_mov.id "
                +"ORDER BY "+orderBy+" "+asc+" LIMIT ? OFFSET ?";

        //System.out.println("Busqueda GetPage: "+sql_to_query);

        ArrayList<HashMap<String, Object>> hm = (ArrayList<HashMap<String, Object>>) this.jdbcTemplate.query(
            sql_to_query,
            new Object[]{new String(data_string),new Integer(pageSize),new Integer(offset)}, new RowMapper() {
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    HashMap<String, Object> row = new HashMap<String, Object>();
                    row.put("id",rs.getInt("id"));
                    row.put("folio_ajuste",rs.getString("folio_ajuste"));
                    row.put("almacen",rs.getString("almacen"));
                    row.put("tipo_movimiento",rs.getString("tipo_movimiento"));
                    row.put("fecha_ajuste",rs.getString("fecha_ajuste"));
                    return row;
                }
            }
        );
        return hm;
    }






    @Override
    public ArrayList<HashMap<String, String>> getInvAjustes_Datos(Integer id) {
        String sql_to_query = ""
                + "SELECT DISTINCT "
                    + "inv_mov.id, "
                    + "inv_mov.referencia AS folio_ajuste, "
                    + "inv_mov.inv_mov_tipo_id, "
                    + "inv_mov_tipos.grupo as tipo_ajuste, "
                    + "(CASE WHEN inv_mov_tipos.grupo=0 THEN inv_mov_detalle.alm_destino_id ELSE inv_mov_detalle.alm_origen_id END) AS id_almacen, "
                    + "to_char(inv_mov.fecha_mov,'yyyy-mm-dd') AS fecha_ajuste, "
                    + "inv_mov.observacion "
                + "FROM inv_mov  "
                + "JOIN inv_mov_detalle ON inv_mov_detalle.inv_mov_id=inv_mov.id "
                + "JOIN inv_mov_tipos ON inv_mov_tipos.id=inv_mov.inv_mov_tipo_id "
                + "WHERE inv_mov.id="+ id + ";";

        //Grupo: 0=Entradas, 2=Salidas
        //log.log(Level.INFO, "Ejecutando query de {0}", sql_to_query);
        ArrayList<HashMap<String, String>> hm = (ArrayList<HashMap<String, String>>) this.jdbcTemplate.query(
            sql_to_query,
            new Object[]{}, new RowMapper(){
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    HashMap<String, String> row = new HashMap<String, String>();
                    row.put("id",String.valueOf(rs.getInt("id")));
                    row.put("folio_ajuste",rs.getString("folio_ajuste"));
                    row.put("tipo_ajuste",String.valueOf(rs.getInt("tipo_ajuste")));
                    row.put("id_almacen",String.valueOf(rs.getInt("id_almacen")));
                    row.put("inv_mov_tipo_id",String.valueOf(rs.getInt("inv_mov_tipo_id")));
                    row.put("fecha_ajuste",rs.getString("fecha_ajuste"));
                    row.put("observacion",rs.getString("observacion"));
                    return row;
                }
            }
        );
        return hm;
    }




    @Override
    public ArrayList<HashMap<String, String>> getInvAjustes_DatosGrid(Integer id, String fecha, Integer id_almacen ) {

        String f[]=fecha.split("-");
        Integer mes_actual= Integer.parseInt(f[1]);
        Integer ano_actual= Integer.parseInt(f[0]);

        //genera formula para calcular existencia en el mes de la fecha que recibimos como parametro a este metodo
        Integer incrementa=1;
        String cadena_existencia= "(exi_inicial  ";
	while( incrementa <= mes_actual ){
            cadena_existencia +=" + entradas_"+incrementa+" - salidas_"+incrementa;
            incrementa ++;
        }
	cadena_existencia+=") AS  existencia ";
        
        
        String sql_to_query = ""
                + "SELECT inv_mov_detalle.producto_id, "
                    + "inv_exi.inv_alm_id AS  id_almacen, "
                    + "inv_prod.sku AS codigo, "
                    + "inv_prod.descripcion, "
                    + "inv_prod.unidad_id, "
                    + "inv_prod_unidades.titulo AS unidad, "
                    + "inv_prod_unidades.decimales AS no_dec, "
                    + "(CASE WHEN inv_mov_detalle.inv_prod_presentacion_id IS NULL THEN 0 ELSE inv_mov_detalle.inv_prod_presentacion_id END) AS idPres,"
                    + "(CASE WHEN inv_prod_presentaciones.id IS NULL THEN 0 ELSE inv_prod_presentaciones.cantidad END ) AS cantEqiv, "
                    + "(CASE WHEN inv_prod_presentaciones.titulo IS NULL THEN '' ELSE inv_prod_presentaciones.titulo END ) AS presentacion, "
                    + "inv_obtiene_costo_promedio_actual(inv_mov_detalle.producto_id,'"+fecha+"'::timestamp with time zone) AS costo_promedio, "
                    + "inv_mov_detalle.cantidad, "
                    + "(CASE WHEN inv_prod_presentaciones.id IS NULL THEN 0 ELSE (inv_mov_detalle.cantidad::double precision/inv_prod_presentaciones.cantidad::double precision) END ) AS cantPres, "
                    + "inv_mov_detalle.costo, "
                    + "(inv_mov_detalle.cantidad * inv_mov_detalle.costo::double precision) as costo_partida, "
                    + cadena_existencia
                + " FROM inv_mov_detalle "
                + "JOIN inv_prod ON inv_prod.id=inv_mov_detalle.producto_id "
                + "JOIN inv_prod_unidades ON inv_prod_unidades.id=inv_prod.unidad_id "
                + "LEFT JOIN inv_prod_presentaciones ON inv_prod_presentaciones.id=inv_mov_detalle.inv_prod_presentacion_id "
                + "JOIN inv_exi ON inv_exi.inv_prod_id=inv_prod.id "
                + "WHERE inv_mov_detalle.inv_mov_id=? "
                + "AND inv_exi.ano="+ano_actual+" "
                + "AND inv_exi.inv_alm_id="+id_almacen;

        //System.out.println(sql_to_query);
        //System.out.println("id: "+id);
        ArrayList<HashMap<String, String>> hm = (ArrayList<HashMap<String, String>>) this.jdbcTemplate.query(
            sql_to_query,
            new Object[]{ new Integer(id) }, new RowMapper(){
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    HashMap<String, String> row = new HashMap<String, String>();
                    row.put("producto_id",String.valueOf(rs.getInt("producto_id")));
                    row.put("id_almacen",String.valueOf(rs.getInt("id_almacen")));
                    row.put("codigo",rs.getString("codigo"));
                    row.put("descripcion",rs.getString("descripcion"));
                    row.put("unidad_id",String.valueOf(rs.getInt("unidad_id")));
                    row.put("unidad",rs.getString("unidad"));
                    row.put("no_dec",String.valueOf(rs.getInt("no_dec")));
                    row.put("costo_promedio",StringHelper.roundDouble(rs.getString("costo_promedio"),2));
                    row.put("cant_ajuste",StringHelper.roundDouble(rs.getString("cantidad"),rs.getInt("no_dec")));
                    row.put("costo_ajuste",StringHelper.roundDouble(rs.getString("costo"),2));
                    row.put("costo_partida",StringHelper.roundDouble(rs.getString("costo_partida"),2));
                    row.put("existencia",StringHelper.roundDouble(rs.getString("existencia"),rs.getInt("no_dec")));
                    row.put("idPres",String.valueOf(rs.getInt("idPres")));
                    row.put("cantEqiv",String.valueOf(rs.getInt("cantEqiv")));
                    row.put("cantPres",StringHelper.roundDouble(rs.getString("cantPres"),rs.getInt("no_dec")));
                    row.put("presentacion",rs.getString("presentacion"));
                    
                    return row;
                }
            }
        );
        return hm;
    }





    @Override
    public HashMap<String, String> getInvAjustes_DatosPDF(Integer id) {
        HashMap<String, String> data = new HashMap<String, String>();

        String sql_to_query = ""
                + "SELECT "
                    + "sbt.folio_ajuste, "
                    + "sbt.tipo_movimiento,  "
                    + "sbt.fecha_ajuste, "
                    + "sbt.fecha, "
                    + "sbt.observacion, "
                    + "inv_alm.titulo AS almacen, "
                    + "inv_alm.id AS id_almacen,"
                    + "nombre_usuario "
                    + "FROM ( "
                        + "SELECT DISTINCT  "
                        + "inv_mov.referencia AS folio_ajuste,  "
                        + "inv_mov_tipos.titulo AS tipo_movimiento, "
                        + "(CASE WHEN inv_mov_tipos.grupo=0 THEN inv_mov_detalle.alm_destino_id ELSE inv_mov_detalle.alm_origen_id END) AS id_almacen, "
                        + "to_char(inv_mov.fecha_mov,'dd/mm/yyyy') AS fecha_ajuste,  "
                        + "to_char(inv_mov.fecha_mov,'yyyy-mm-dd') AS fecha,  "
                        + "inv_mov.observacion,"
                        + "gral_empleados.nombre_pila||' '||gral_empleados.apellido_paterno||' '||gral_empleados.apellido_materno AS nombre_usuario "
                        + "FROM inv_mov   "
                        + "JOIN inv_mov_detalle ON inv_mov_detalle.inv_mov_id=inv_mov.id  "
                        + "JOIN inv_mov_tipos ON inv_mov_tipos.id=inv_mov.inv_mov_tipo_id  "
                        + "JOIN gral_usr ON gral_usr.id=inv_mov.gral_usr_id "
                        + "LEFT JOIN gral_empleados ON gral_empleados.id = gral_usr.gral_empleados_id "
                        + "WHERE inv_mov.id="+ id + " "
                + ") AS sbt "
                + "JOIN inv_alm ON inv_alm.id=sbt.id_almacen;";



        //System.out.println("Header PDF::::"+sql_to_query);

        Map<String, Object> map = this.getJdbcTemplate().queryForMap(sql_to_query);
        data.put("id",String.valueOf(map.get("id")));
        data.put("folio_ajuste",map.get("folio_ajuste").toString());
        data.put("tipo_movimiento",map.get("tipo_movimiento").toString());
        data.put("observacion",map.get("observacion").toString());
        data.put("fecha_ajuste",map.get("fecha_ajuste").toString());
        data.put("almacen",map.get("almacen").toString());
        //la fecha y id_almacen es para utilizarlo en el buscador del listado de productos
        data.put("fecha",map.get("fecha").toString());
        data.put("id_almacen",String.valueOf(map.get("id_almacen")));
        data.put("nombre_usuario",map.get("nombre_usuario").toString());
        return data;
    }

    //TERMINA METODOS DE APLICATIVO DE AJUSTES DE INVETARIO
    //***********************************************************************************************************************************



    //***********************************************************************************************************************************
    //ESTO ES PARA PRODUCTOS EQUIVALENTES.
    @Override
    public ArrayList<HashMap<String, String>> getProductoEquivalente_Datos(Integer id) {
        String sql_to_query = ""
                + "SELECT "
                    + "inv_lote_detalle.inv_osal_detalle_id AS id_osal_detalle, "
                    + "inv_lote_detalle.id AS id_lote_detalle, "
                    + "inv_lote.inv_alm_id AS id_almacen, "
                    + "inv_lote.inv_prod_id AS id_producto, "
                    + "inv_lote.lote_int, "
                    + "inv_lote_detalle.cantidad_sal, "
                    + "(CASE WHEN inv_lote.pedimento='' OR inv_lote.pedimento IS NULL THEN ' ' ELSE inv_lote.pedimento END) AS pedimento, "
                    + "(CASE WHEN to_char(inv_lote.caducidad,'yyyymmdd') = '29991231' THEN ''::character varying ELSE inv_lote.caducidad::character varying END) AS caducidad  "
                + "FROM inv_osal_detalle "
                + "JOIN inv_lote_detalle ON inv_lote_detalle.inv_osal_detalle_id=inv_osal_detalle.id "
                + "JOIN inv_lote ON inv_lote.id=inv_lote_detalle.inv_lote_id "
                + "WHERE inv_osal_detalle.inv_osal_id=? "
                + "ORDER BY inv_lote.id;";

        //System.out.println(sql_to_query);
        ArrayList<HashMap<String, String>> hm = (ArrayList<HashMap<String, String>>) this.jdbcTemplate.query(
            sql_to_query,
            new Object[]{ new Integer(id) }, new RowMapper(){
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    HashMap<String, String> row = new HashMap<String, String>();
                    row.put("id_osal_detalle",String.valueOf(rs.getInt("id_osal_detalle")));
                    row.put("id_lote_detalle",String.valueOf(rs.getInt("id_lote_detalle")));
                    row.put("id_producto",String.valueOf(rs.getInt("id_producto")));
                    row.put("id_almacen",String.valueOf(rs.getInt("id_almacen")));
                    row.put("lote_int",rs.getString("lote_int"));
                    row.put("cantidad_sal",StringHelper.roundDouble(rs.getString("cantidad_sal"),2));
                    row.put("ped_lote",rs.getString("pedimento"));
                    row.put("cad_lote",rs.getString("caducidad"));
                    return row;
                }
            }
        );
        return hm;
    }



    //catalogo  de Productos equivalentes
    @Override
    public ArrayList<HashMap<String, Object>> getProductosEquivalentes_PaginaGrid(String data_string, int offset, int pageSize, String orderBy, String asc) {
        String sql_busqueda = "select id from gral_bus_catalogos(?) as foo (id integer)";

        String sql_to_query = " SELECT distinct inv_prod_equiv.inv_prod_id as id, "
                            + " inv_prod.sku as codigo, "
                            + " inv_prod.descripcion "
                            + " FROM inv_prod_equiv "
                            + " JOIN inv_prod ON inv_prod.id=inv_prod_equiv.inv_prod_id "
                            + " JOIN ("+sql_busqueda+") as subt on subt.id=inv_prod.id "
                            + " ORDER BY "+orderBy+" "+asc+" LIMIT ? OFFSET ?";

        //System.out.println("Busqueda GetPage este es el queryyyyyyyyyyyyyyyyyyyyyyyyy: "+sql_to_query);

        ArrayList<HashMap<String, Object>> hm = (ArrayList<HashMap<String, Object>>) this.jdbcTemplate.query(
            sql_to_query,
            new Object[]{new String(data_string),new Integer(pageSize),new Integer(offset)}, new RowMapper() {
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    HashMap<String, Object> row = new HashMap<String, Object>();
                    row.put("id",rs.getString("id"));
                    row.put("codigo",rs.getString("codigo"));
                    row.put("descripcion",rs.getString("descripcion"));
                    return row;
                }
            }
        );
        return hm;
    }

    //busca producto en especifico para agregar a la lista de productos elemento en el catalogo de formulas
    @Override
    public ArrayList<HashMap<String, String>> getProductos_sku(String sku, Integer id_usuario) {

        String sql_to_query = " SELECT gral_suc.empresa_id FROM gral_usr_suc "
                            + " JOIN gral_suc ON gral_suc.id = gral_usr_suc.gral_suc_id "
                            + " WHERE gral_usr_suc.gral_usr_id = "+id_usuario;

        int id_empresa = this.getJdbcTemplate().queryForInt(sql_to_query);

        String sql_query = "SELECT inv_prod.id, "
                                + "inv_prod.sku, "
                                + "inv_prod.descripcion "
                                //+ "inv_prod_unidades.decimales  "
                            + "FROM inv_prod "
                            //+ "JOIN inv_prod_unidades ON inv_prod_unidades.id=inv_prod.unidad_id "
                            + "WHERE inv_prod.empresa_id = "+id_empresa+" AND inv_prod.sku='"+sku+"';";
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
                    //row.put("decimales",String.valueOf(rs.getInt("decimales")));
                    return row;
                }
            }
        );
        return hm_sku;
    }


    //obtiene tipos de productos
    @Override
    public ArrayList<HashMap<String, String>> getProducto_Tipos_para_productos_equivalentes(String titulo_producto) {
        String cadena_where="";
        if(!titulo_producto.equals("")){
         cadena_where ="and inv_prod_tipos.titulo ilike '"+titulo_producto+"' ";
        }

	String sql_query =    " SELECT DISTINCT id,titulo  "
                            + " FROM inv_prod_tipos  "
                            + " WHERE borrado_logico=false "
                            + " and inv_prod_tipos.id != 3 and inv_prod_tipos.id != 4  "
                            +cadena_where+ "  order by inv_prod_tipos.titulo;";

        // System.out.println("sql_query: "+sql_query);
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
    public ArrayList<HashMap<String, String>> getProductosEquivalentes_Datos(Integer id) {
        String sql_to_query =     " SELECT inv_prod_equiv.id, "
                                + " inv_prod_equiv.inv_prod_id as id_producto, "
                                + " inv_prod.sku as codigo, "
                                + " inv_prod_tipos.titulo as tipo_producto, "
                                + " inv_prod.descripcion "
                                + " from inv_prod_equiv "
                                + " join inv_prod on inv_prod.id=inv_prod_equiv.inv_prod_id "
                                + " join inv_prod_tipos on inv_prod_tipos.id=inv_prod.tipo_de_producto_id "
                                + " WHERE inv_prod_equiv.inv_prod_id = "+id;

        //System.out.println("grid de productos equivalentes"+sql_to_query);
        ArrayList<HashMap<String, String>> datos_productosequivalentes = (ArrayList<HashMap<String, String>>) this.jdbcTemplate.query(
            sql_to_query,
            new Object[]{}, new RowMapper(){
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    HashMap<String, String> row = new HashMap<String, String>();
                    row.put("id",String.valueOf(rs.getInt("id")));
                    row.put("id_producto",String.valueOf(rs.getInt("id_producto")));
                    row.put("codigo",rs.getString("codigo"));
                    row.put("tipo_producto",rs.getString("tipo_producto"));
                    row.put("descripcion",rs.getString("descripcion"));

                    return row;
                }
            }
        );
        return datos_productosequivalentes;
    }




    @Override
    public ArrayList<HashMap<String, String>> getProductosEquivalentes_DatosMinigrid(String id) {
        String sql_query = " SELECT inv_prod_id,  inv_prod_id_equiv FROM  inv_prod_equiv where inv_prod_equiv.id="+id;

         String sql_to_query = " SELECT inv_prod_equiv.inv_prod_id_equiv, "
                            +  " inv_prod.sku AS codigo, "
                            +  " inv_prod.descripcion, "
                            +  " inv_prod_equiv.observaciones"
                            +  " FROM inv_prod_equiv JOIN inv_prod "
                            +  " ON inv_prod.id=inv_prod_equiv.inv_prod_id_equiv "
                            +  " WHERE "
                            +  " inv_prod_equiv.inv_prod_id ="+id
                            +  " ORDER BY inv_prod_equiv.id ASC;";
        //System.out.println("Checar este query??"+sql_to_query);
        ArrayList<HashMap<String, String>> datos_formulas_minigrid = (ArrayList<HashMap<String, String>>) this.jdbcTemplate.query(
            sql_to_query,
            new Object[]{}, new RowMapper(){
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    HashMap<String, String> row = new HashMap<String, String>();
                    //row.put("id_inv_formulas",String.valueOf(rs.getInt("id")));
                    row.put("inv_prod_id_equiv",String.valueOf(rs.getInt("inv_prod_id_equiv")));
                    row.put("codigo",rs.getString("codigo"));
                    row.put("descripcion",rs.getString("descripcion"));
                    row.put("observaciones",rs.getString("observaciones"));

                    return row;
                }
            }
        );
        return datos_formulas_minigrid;
    }

    @Override
    public ArrayList<HashMap<String, String>> getBuscadorProductosEquivalentes(String sku, String tipo, String descripcion, Integer id_empresa, String id_prod) {
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

        if(!id_prod.equals("")){
		where +=" AND inv_prod.id != '"+id_prod+"'";
	}

        String sql_to_query = ""
                                + " SELECT "
				+ " inv_prod.id,"
				+ " inv_prod.sku,"
                                + " inv_prod.descripcion, "
                                + " inv_prod.unidad_id, "
                                + " inv_prod_unidades.titulo AS unidad, "
				+ " inv_prod_tipos.titulo AS tipo,"
                                + " inv_prod_unidades.decimales "
                                + " FROM inv_prod "
                                + " LEFT JOIN inv_prod_tipos ON inv_prod_tipos.id=inv_prod.tipo_de_producto_id "
                                + " LEFT JOIN inv_prod_unidades ON inv_prod_unidades.id=inv_prod.unidad_id "
                                + " WHERE inv_prod.empresa_id="+id_empresa+" AND inv_prod.borrado_logico=false "+where+" ORDER BY inv_prod.descripcion;";
        //log.log(Level.INFO, "Ejecutando query de {0}", sql_to_query);
        //System.out.println("query del getBuscadorProductosEquivalentes "+sql_to_query);
        //System.out.println("sku: "+sku+"___tipo:"+ tipo + "___descripcion__"+ descripcion+ "___id_empresa__"+id_empresa+"__id_producto_principal:__"+ id_prod);
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

    //TERMINA PRODUCTOS EQUIVALENTES
    //***********************************************************************************************************************************


    //***********************************************************************************************************************************
    //METODOS PARA APLICATIVO IMPRESION DE ETIQUETAS
    //Buscador de entradas
    @Override
    public ArrayList<HashMap<String, String>> getBuscadorEntradas(String folio, String fecha_inicial, String fecha_final, Integer tipo_origen, Integer id_empresa) {
        String where = "";
        String sql_to_query = " ";

        if(tipo_origen == 1){
            if(!folio.equals("")){
                where=" AND inv_oent.folio ilike '%"+folio+"%'";
            }

            if(!fecha_inicial.equals("") && !fecha_final.equals("")){
                 where="AND (to_char(inv_oent.momento_creacion,'yyyymmdd')::INTEGER BETWEEN to_char('"+fecha_inicial+"'::timestamp with time zone,'yyyymmdd')::INTEGER AND to_char('"+fecha_final+"'::timestamp with time zone,'yyyymmdd')::INTEGER) ";
            }
        }

        if(tipo_origen == 2){
            if(!folio.equals("")){
                where="    AND inv_oent.folio_documento ilike '%"+folio+"%'";
            }

            if(!fecha_inicial.equals("") && !fecha_final.equals("")){
                 where="     AND (to_char(inv_oent.momento_creacion,'yyyymmdd')::INTEGER BETWEEN to_char('"+fecha_inicial+"'::timestamp with time zone,'yyyymmdd')::INTEGER AND to_char('"+fecha_final+"'::timestamp with time zone,'yyyymmdd')::INTEGER) ";
            }
        }

        if(tipo_origen == 3){
            if(!folio.equals("")){
                where="    AND inv_osal.folio_documento ilike '%"+folio+"%'";
            }

            if(!fecha_inicial.equals("") && !fecha_final.equals("")){
                 where="   AND (to_char(inv_osal.momento_creacion,'yyyymmdd')::INTEGER BETWEEN to_char('"+fecha_inicial+"'::timestamp with time zone,'yyyymmdd')::INTEGER AND to_char('"+fecha_final+"'::timestamp with time zone,'yyyymmdd')::INTEGER) ";
            }
        }

	if(tipo_origen == 1){
            sql_to_query = " "
                    + "SELECT "
                        + "inv_oent.id as entrada_id, "
                        + "inv_oent.folio,  "
                        + "(case when cxp_prov.id is null then '0' else  cxp_prov.id  end) as proveedor_id, "
                        + "(case when cxp_prov.razon_social is null then '' else  cxp_prov.razon_social end) AS proveedor, "
                        + "(case when inv_oent.orden_de_compra  is null then '' else inv_oent.orden_de_compra  end ) as orden_compra, "
                        + "inv_oent.folio_documento, "
                        + "to_char(inv_oent.fecha_exp::timestamp with time zone,'dd-mm-yyyy') as fecha_documento,  "
                        + "to_char(inv_oent.momento_creacion,'dd-mm-yyyy') as momento_creacion  "


                    + "FROM inv_oent  "
                    + "LEFT JOIN cxp_prov on cxp_prov.id=inv_oent.cxp_prov_id  "
                    + "WHERE inv_oent.gral_emp_id="+id_empresa+" AND inv_oent.borrado_logico=false "+where+" "
                    + "ORDER BY folio DESC ";
                    //System.out.println("esto el del pluguin entradas:::"+sql_to_query);
        }



        if(tipo_origen == 2){
            sql_to_query = " "

                +"  select "
                +"  inv_oent.folio, "
                +"  to_char(inv_oent.momento_creacion,'yyyy-mm-dd') as momento_creacion ,  "
                +"  inv_oent.id as entrada_id, "
                +"  0::integer as proveedor_id, "
                +"  ' '::character varying as proveedor, "
                +"  (case when inv_oent.orden_de_compra is null then '' else inv_oent.orden_de_compra  end) as orden_compra, "
                +"  inv_oent.folio_documento, "
                +"  to_char(inv_oent.momento_creacion,'yyyy-mm-dd') as fecha_documento "
                +"  from inv_oent "
                +"  join inv_mov_tipos on inv_mov_tipos.id = inv_oent.inv_mov_tipo_id    "

                +"  where inv_mov_tipos.id=10  and inv_oent.gral_emp_id="+id_empresa  + where;

            //System.out.println("CARGANDO DATOS PARA PRODUCCION:::"+sql_to_query);
        }



        if(tipo_origen == 3){
        sql_to_query = " SELECT   "
                        +"   inv_osal.folio,   "
                        +"   to_char(inv_osal.momento_creacion,'yyyy-mm-dd')as momento_creacion,    "
                        +"   0::integer  as entrada_id,   "
                        +"   0::integer  as proveedor_id,  "
                        +"   ''::character varying  AS proveedor,  "
                        +"   (case when inv_osal.orden_compra is null or inv_osal.orden_compra ='' then '' else inv_osal.orden_compra end)  as orden_compra,  "
                        +"   inv_osal.fecha_exp  as fecha_documento ,   "
                        +"   inv_osal.folio_documento             "
                        +"   from inv_osal                                                      "
                        +"   join inv_mov_tipos on inv_mov_tipos.id = inv_osal.inv_mov_tipo_id   "
                        +"   join inv_osal_detalle on inv_osal_detalle.inv_osal_id=inv_osal.id        "
                        +"   join inv_lote_detalle on inv_lote_detalle.inv_osal_detalle_id = inv_osal_detalle.id        "
                        +"   join inv_lote on inv_lote.id=inv_lote_detalle.inv_lote_id        "
                        +"   where inv_mov_tipos.id=11 and inv_osal.tipo_documento=4 "+where;
        }


        ArrayList<HashMap<String, String>> hm_datos = (ArrayList<HashMap<String, String>>) this.jdbcTemplate.query(
            sql_to_query,
            new Object[]{}, new RowMapper(){
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    HashMap<String, String> row = new HashMap<String, String>();
                    row.put("entrada_id",String.valueOf(rs.getInt("entrada_id")));
                    row.put("folio",rs.getString("folio"));
                    row.put("proveedor_id",String.valueOf(rs.getInt("proveedor_id")));
                    row.put("proveedor",rs.getString("proveedor"));
                    row.put("orden_compra",rs.getString("orden_compra"));
                    row.put("folio_documento",rs.getString("folio_documento"));
                    row.put("fecha_documento",rs.getString("fecha_documento"));
                    row.put("momento_creacion",rs.getString("momento_creacion"));
                    //row.put("tipo_orden",rs.getString("tipo_orden"));

                    return row;
                }
            }
        );
        return hm_datos;
    }//FIN del buscador de Entradas





    //RETORNANDO DATOS PARA CARGAR EL GRID DE IMPRESION DE ETIQUETAS  (ENTRADAS, PRODUCCION Y REQUISICION)
    @Override
    public ArrayList<HashMap<String, String>> getgridEntradas(String folio, String fecha_inicial, String fecha_final,Integer tipo_origen,Integer id_empresa) {
        String where = "";
        String sql_to_query ="";

        if(tipo_origen == 1){
                if(!folio.equals("")){
                    where=" AND inv_oent.folio ilike '"+folio+"'";
                }

                sql_to_query =" SELECT  "
                            +"  inv_oent.id as entrada_id,  "
                            +"  inv_lote.id AS lote_id,  "
                            +"  inv_lote.inv_prod_id as producto_id, "
                            +"  inv_lote.lote_int, "
                            +"  inv_lote.lote_prov, "
                            +"  inv_lote.inv_prod_id,  "
                            +"  inv_prod.sku AS codigo,  "
                            +"  inv_prod.descripcion AS producto,  "
                            +"  inv_prod.tipo_de_producto_id , "
                            +"  ''::character varying  AS folio,  "
                            +"  ''::character varying  AS momento_creacion,  "
                            +"  ''::character varying  AS tipo_orden,  "
                            +"  0::double precision  AS cantidad_produccion    "
                            +" FROM inv_oent  "
                            +" JOIN inv_oent_detalle ON inv_oent_detalle.inv_oent_id=inv_oent.id  "
                            +" JOIN inv_lote ON inv_lote.inv_oent_detalle_id=inv_oent_detalle.id  "
                            +" JOIN inv_prod ON inv_prod.id=inv_oent_detalle.inv_prod_id  "
                            + "WHERE inv_oent.gral_emp_id="+id_empresa+" AND inv_oent.borrado_logico=false "+where+" ";
                            //WHERE inv_oent.id=2 ";
            //System.out.println("informacion par el GRID DEL PRIMER PLUGUIN::: tipo_origen:  "+tipo_origen+":ENTRADAS   "+"QUERY: "+sql_to_query);
        }

        if(tipo_origen == 2){
            if(!folio.equals("")){
                    where=" AND op.folio ilike '"+folio+"'";
            }

            sql_to_query =" SELECT  distinct  op.folio,  "
                        +"  to_char(op.momento_creacion,'yyyy-mm-dd') as momento_creacion, "
                        +"  ''::character varying  as tipo_orden,   "
                        +"  0::integer as entrada_id,       "
                        +"  0::integer as lote_id,      "
                        +"   inv_prod.id as producto_id, "
                        +"   ''::character varying as lote_int, "
                        +"   ''::character varying as lote_prov, "
                        +"   inv_prod.id as inv_prod_id, "
                        +"   inv_prod.sku as codigo,      "
                        +"   inv_prod.descripcion as producto, "
                        +"  inv_prod.tipo_de_producto_id , "
                        +"   opd.cantidad as cantidad_produccion  "
                        +"   from pro_orden_prod AS op "
                        +"   join pro_orden_prod_det as opd ON opd.pro_orden_prod_id=op.id  "
                        +"    join inv_prod on inv_prod.id=opd.inv_prod_id where op.gral_emp_id="+id_empresa+""+where+" ";


            //System.out.println("informacion para el GRID DEL PRIMER PLUGUIN::: tipo_origen:  "+tipo_origen +":PRODUCCION   "+"QUERY: "+sql_to_query);
        }

        if(tipo_origen == 3){

                if(!folio.equals("")){
                    where=" AND inv_osal.folio_documento ilike '"+folio+"'";
                }

            sql_to_query =" SELECT  "
                        //+"  inv_osal.folio_documento as folio,  "
                        +"   pro_orden_prod.folio,  "
                        +"  to_char(inv_osal.momento_creacion,'yyyy-mm-dd')as momento_creacion,  "
                        +"  ''::character varying as tipo_orden,  "
                        +"  0::integer as entrada_id,    "
                        +"  inv_lote.id  as lote_id,    "
                        +"  inv_osal_detalle.inv_prod_id as producto_id,  "
                        +"  inv_lote.lote_int,   "
                        +"  ''::character varying as lote_prov,   "
                        +"  inv_prod.id as inv_prod_id,      "
                        +"  inv_prod.sku as codigo, "
                        +"  inv_prod.descripcion as producto,  "
                        +"  inv_prod.tipo_de_producto_id , "
                        +"  inv_lote_detalle.cantidad_sal  AS cantidad_produccion    "
                        +"  FROM inv_osal  "
                        +"  JOIN pro_ordenprod_invosal on pro_ordenprod_invosal.inv_osal_id = inv_osal.id "
                        +"  JOIN pro_orden_prod on pro_orden_prod.id = pro_ordenprod_invosal.pro_orden_prod_id "
                        +"  JOIN  inv_mov_tipos on inv_mov_tipos.id = inv_osal.inv_mov_tipo_id  "
                        +"  JOIN  inv_osal_detalle on inv_osal_detalle.inv_osal_id=inv_osal.id  "
                        +"  JOIN  inv_prod on inv_prod.id = inv_osal_detalle.inv_prod_id  "
                        +"  JOIN  inv_lote_detalle on inv_lote_detalle.inv_osal_detalle_id = inv_osal_detalle.id  "
                        +"  JOIN  inv_lote on inv_lote.id=inv_lote_detalle.inv_lote_id  "
                        +"  WHERE inv_mov_tipos.id=11 AND inv_osal.tipo_documento=4  "+where+" ";

            //System.out.println("informacion para el GRID DEL PRIMER PLUGUIN::: tipo_origen:  "+tipo_origen+":REQUISICIONES   "+"QUERY: "+sql_to_query);
        }


        ArrayList<HashMap<String, String>> hm_grid_entradas = (ArrayList<HashMap<String, String>>) this.jdbcTemplate.query(
            sql_to_query,
            new Object[]{}, new RowMapper(){
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    HashMap<String, String> row = new HashMap<String, String>();
                    row.put("id_entrada",String.valueOf(rs.getInt("entrada_id")));
                    row.put("id_lote",String.valueOf(rs.getInt("lote_id")));
                    row.put("inv_prod_id",String.valueOf(rs.getInt("producto_id")));
                    row.put("tipo_de_producto_id",String.valueOf(rs.getInt("tipo_de_producto_id")));

                    row.put("lote_interno",rs.getString("lote_int"));
                    row.put("lote_proveedor",rs.getString("lote_prov"));
                    row.put("codigo",rs.getString("codigo"));
                    row.put("descripcion",rs.getString("producto"));
                    row.put("folio",rs.getString("folio"));
                    row.put("momento_creacion",rs.getString("momento_creacion"));
                    row.put("tipo_orden",rs.getString("tipo_orden"));
                    row.put("cantidad_produccion",rs.getString("cantidad_produccion"));
                    return row;
                }
            }
        );
        return hm_grid_entradas;
    }//FIN del que carga los datos del grid del primer pluguin cuando entra a la parte de nuevo

    //cargando el grid cuando le danclick en editar
    @Override
    public ArrayList<HashMap<String, String>> getEtiquetas_Datos_header(Integer id_etiqueta) {
         String sql_to_query = "SELECT inv_etiquetas.id,inv_etiquetas.folio_origen,inv_etiquetas.folio,inv_etiquetas.tipo_origen "
                + "FROM inv_etiquetas "
                + "WHERE id ="+id_etiqueta;
        //System.out.println("Datos para el header del pluguin:   "+sql_to_query);
        ArrayList<HashMap<String, String>> datos_grid_etiquetas = (ArrayList<HashMap<String, String>>) this.jdbcTemplate.query(
            sql_to_query,
            new Object[]{}, new RowMapper(){
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    HashMap<String, String> row = new HashMap<String, String>();
                    row.put("id",String.valueOf(rs.getInt("id")));
                    row.put("folio_origen",rs.getString("folio_origen"));
                    row.put("folio",rs.getString("folio"));
                    row.put("tipo_origen",rs.getString("tipo_origen"));


                    return row;
                }
            }
        );
        return datos_grid_etiquetas;
    }


    @Override
    public ArrayList<HashMap<String, String>> getEtiquetas_Datos_grid(Integer id_etiqueta,Integer tipo_origen) {
       String sql_to_query="";
       String l="";
        String t_o="";
       if(tipo_origen == 1){
            l="";
       }
       if(tipo_origen == 2){
            l="left  ";
            t_o="Produccion";
       }

              sql_to_query = "select  inv_etiquetas.id as etiqueta_id,  "
                +"  inv_etiquetas_detalle.id  as etiqueta_detalle_id, "
                + "  inv_etiquetas_detalle.inv_etiquetas_id,   "
                +"  inv_etiquetas_detalle.cantidad,  "
                +"  inv_etiquetas_detalle.lote_interno,  "
                +"  (case when inv_prod.sku is null or inv_prod.sku ='' then '"+t_o+"' else inv_prod.sku end )  as codigo,  "
                +"  (case when inv_prod.descripcion is null or inv_prod.descripcion ='' then '"+t_o+"' else inv_prod.descripcion end ) as descripcion, "
                +"  inv_etiquetas_detalle. inv_prod_id, "
                +"  inv_etiquetas_detalle.inv_lote_id,  "
                +"  inv_prod.tipo_de_producto_id, "
                +"  inv_etiquetas_detalle.inv_etiqueta_medidas_id, "
                +"  inv_etiqueta_medidas.titulo AS medidas, "
                +"  (case when inv_etiquetas_detalle.cantidad_produccion is null then 0 else inv_etiquetas_detalle.cantidad_produccion end ) as  cantidad_produccion "


                +"  from inv_etiquetas_detalle  "
                +" "+l +  "join  inv_etiquetas  on inv_etiquetas.id=inv_etiquetas_detalle.inv_etiquetas_id "
                +" "+l +  "join inv_prod on inv_prod.id=inv_etiquetas_detalle.inv_prod_id   "
                +" "+l +  "join inv_etiqueta_medidas on inv_etiqueta_medidas.id= inv_etiquetas_detalle. inv_etiqueta_medidas_id  "
                +"  WHERE inv_etiquetas_detalle.inv_etiquetas_id="+id_etiqueta+" and inv_etiquetas.tipo_origen="+tipo_origen;



         //System.out.println("DATOS PARA EL CUANDO EL TIPO ORIGEN ES:"+tipo_origen+"QUERY::"+sql_to_query);
        ArrayList<HashMap<String, String>> datos_grid_etiquetas = (ArrayList<HashMap<String, String>>) this.jdbcTemplate.query(
            sql_to_query,
            new Object[]{}, new RowMapper(){
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    HashMap<String, String> row = new HashMap<String, String>();
                    row.put("id",String.valueOf(rs.getInt("etiqueta_id")));
                    row.put("etiqueta_detalle_id",String.valueOf(rs.getInt("etiqueta_detalle_id")));
                    row.put("inv_etiqueta_medidas_id",String.valueOf(rs.getInt("inv_etiqueta_medidas_id")));
                    row.put("medidas",rs.getString("medidas"));
                    row.put("cantidad_produccion",rs.getString("cantidad_produccion"));
                    row.put("cantidad",rs.getString("cantidad"));
                    row.put("lote_interno",rs.getString("lote_interno"));
                    row.put("codigo",rs.getString("codigo"));
                    row.put("descripcion",rs.getString("descripcion"));
                    row.put("inv_prod_id",String.valueOf(rs.getInt("inv_prod_id")));
                    row.put("tipo_de_producto_id",String.valueOf(rs.getInt("tipo_de_producto_id")));
                    row.put("inv_lote_id",String.valueOf(rs.getInt("inv_lote_id")));
                    row.put("inv_etiquetas_id",String.valueOf(rs.getInt("inv_etiquetas_id")));



                    return row;
                }
            }
        );
        return datos_grid_etiquetas;
    }
    //fin del cargado de datos en editar





    //RETORNANDO DATOS PARA LOS PDFS DE ETIQUETAS..
    @Override
    public ArrayList<HashMap<String, String>> getEtiquetas_Entrada(Integer etiqueta_id,Integer tipo_origen,Integer id_empresa) {
        String sql_to_query="";
        String l="";
        if(tipo_origen == 2){
            l="LEFT   ";
        }

            sql_to_query ="SELECT  "
                        +"   inv_etiquetas.id,  "
                        +"   (CASE WHEN inv_etiquetas.tipo_origen=1 THEN  inv_prod.descripcion "
                        +"   WHEN inv_etiquetas.tipo_origen=2 THEN  'PRODUCCION' "
                        +"   WHEN inv_etiquetas.tipo_origen=3 THEN  inv_prod.descripcion "
                        +"   ELSE '' END) AS descripcion, "
                        +"   inv_prod.sku as codigo,  "
                        +"   to_char(inv_lote.caducidad,'yyyy-mm-dd')as caducidad,  "
                        +"   inv_etiquetas_detalle.lote_interno,  "
                        +"   inv_etiquetas_detalle.lote_interno as lote_interno_codigo "
                        +"   FROM inv_etiquetas_detalle  "
                        +"   "+l+" JOIN inv_etiquetas on inv_etiquetas.id=inv_etiquetas_detalle.inv_etiquetas_id  "
                        +"   "+l+" JOIN inv_prod on inv_prod.id=inv_etiquetas_detalle.inv_prod_id  "
                        +"   "+l+" JOIN inv_lote on inv_lote.id=inv_etiquetas_detalle.inv_lote_id  "
                        +"   WHERE inv_etiquetas_detalle.inv_etiquetas_id="+etiqueta_id+"    "
                        +"   AND inv_etiquetas.gral_emp_id="+id_empresa+" AND inv_etiquetas.borrado_logico=false AND inv_etiquetas.tipo_origen ="+tipo_origen;


            //System.out.println("informacion par el PDF::: tipo_origen:  "+tipo_origen+":PRODUCCION   "+"QUERY: "+sql_to_query);

        ArrayList<HashMap<String, String>> hm_etiquetas = (ArrayList<HashMap<String, String>>) this.jdbcTemplate.query(
            sql_to_query,
            new Object[]{}, new RowMapper(){
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    HashMap<String, String> row = new HashMap<String, String>();
                    row.put("id_etiqueta",String.valueOf(rs.getInt("id")));
                    row.put("codigo",rs.getString("codigo"));
                    row.put("nombre_producto",rs.getString("descripcion"));
                    row.put("fecha",rs.getString("caducidad"));
                    row.put("lote_interno_codigo",rs.getString("lote_interno_codigo"));
                    row.put("lote_interno",rs.getString("lote_interno"));


                    return row;
                }
            }
        );
        return hm_etiquetas;
    }
    //FIN DEL RETORNO DE  DATOS PARA LOS PDFS DE ETIQUETAS

    @Override
    public ArrayList<HashMap<String, Object>> getEtiquetas_PaginaGrid(String data_string, int offset, int pageSize, String orderBy, String asc) {
        String sql_busqueda = "select id from gral_bus_catalogos(?) as foo (id integer)";

            String sql_to_query = "select  inv_etiquetas.id, "
                            +"  inv_etiquetas.folio , "
                            +"  inv_etiquetas.folio_origen, "
                            +"  (case  "
                            +"  when inv_etiquetas.tipo_origen = 1 then "
                            +"  'Entradas'  "
                            +"  when inv_etiquetas.tipo_origen = 2 then "
                            +"  'Produccion' else "
                            +"  'Requisicion' end ) as tipo_origen "
                            +"   from  inv_etiquetas   "
                            +"JOIN ("+sql_busqueda+") AS sbt ON sbt.id = inv_etiquetas.id "
                            +"WHERE inv_etiquetas.borrado_logico=false  "
                            +"order by "+orderBy+" "+asc+" limit ? OFFSET ?";


        //System.out.println("Busqueda Grid::::::: "+sql_to_query);
        ArrayList<HashMap<String, Object>> hm = (ArrayList<HashMap<String, Object>>) this.jdbcTemplate.query(
            sql_to_query,
            new Object[]{new String(data_string), new Integer(pageSize),new Integer(offset)}, new RowMapper() {
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    HashMap<String, Object> row = new HashMap<String, Object>();
                    row.put("id",rs.getInt("id"));
                    row.put("folio",rs.getString("folio"));
                    row.put("folio_origen",rs.getString("folio_origen"));
                    row.put("tipo_origen",rs.getString("tipo_origen"));

                    return row;
                }
            }
        );
        return hm;
    }


    //metodo para crear los  xml de produccion
    @Override
    public LinkedHashMap<String, Object> getDatosEtiquetaLote_produccion(String lote_interno, Integer id_etiqueta, Integer id_medida_etiqueta) {
        LinkedHashMap<String, Object> data = new LinkedHashMap<String, Object>();


        String sql_to_query =" select  "
                            +"  ''::character varying nombre_cliente,   "
                            +"  inv_etiquetas_detalle.lote_interno as lote_produccion,   "
                            +"  inv_etiqueta_medidas.largo as etiqueta_largo,   "
                            +"  inv_etiqueta_medidas.alto as etiqueta_alto,   "
                            +"  inv_etiqueta_medidas.modelo_impresora,   "
                            +"  inv_etiquetas_detalle.inv_lote_id,   "
                            +"  inv_etiquetas_detalle.lote_interno,   "
                            +"  inv_prod.sku as  producto_codigo,   "
                            +"  inv_prod.descripcion as  producto_nombre,   "
                            +"  inv_etiquetas_detalle.cantidad_produccion as  producto_cantidad,   "
                            +"  inv_prod_unidades.titulo_abr as producto_unidad,   "
                            +"  ''::character varying as  caducidad_fecha   "
                            +"  from inv_etiquetas_detalle   "
                            +"  join inv_prod on inv_prod.id=inv_etiquetas_detalle.inv_prod_id    "
                            +"  join inv_etiqueta_medidas on inv_etiqueta_medidas.id=inv_etiquetas_detalle. inv_etiqueta_medidas_id    "
                            +"  join inv_prod_unidades on inv_prod_unidades.id=inv_prod.unidad_id    "
                            +"  where   inv_etiquetas_detalle.lote_interno ilike '"+lote_interno+"' and inv_etiquetas_detalle.inv_etiquetas_id= "+ id_etiqueta ;

        //log.log(Level.INFO, "Ejecutando query de {0}", sql_to_query);
        //System.out.println("Imprimiendo el query que trae la produccion o requisicion a imprimir:::   "+sql_to_query);

        Map<String, Object> map = this.getJdbcTemplate().queryForMap(sql_to_query);
        data.put("etiqueta_largo",StringHelper.roundDouble(String.valueOf(map.get("etiqueta_largo")),2));
        data.put("etiqueta_alto",StringHelper.roundDouble(String.valueOf(map.get("etiqueta_alto")),2));
        data.put("modelo_impresora",String.valueOf(map.get("modelo_impresora")));
        data.put("producto_codigo",String.valueOf(map.get("producto_codigo")));
        data.put("producto_nombre",String.valueOf(map.get("producto_nombre")));
        data.put("producto_cantidad",String.valueOf(map.get("producto_cantidad")));
        data.put("producto_unidad",String.valueOf(map.get("producto_unidad")));
        data.put("lote_interno",String.valueOf(map.get("lote_interno")));
        data.put("lote_proveedor",String.valueOf(map.get("lote_proveedor")));
        data.put("caducidad_fecha",String.valueOf(map.get("caducidad_fecha")));
        data.put("nombre_cliente",String.valueOf(map.get("nombre_cliente")));

        data.put("lote_produccion",String.valueOf(map.get("lote_produccion")));

        return data;
    }




    @Override
    public LinkedHashMap<String, Object> getDatosEtiquetaLote_requisicion(String id_etiquetas_detalle) {
        LinkedHashMap<String, Object> data = new LinkedHashMap<String, Object>();


        String sql_to_query =" select "

	                    +"  ''::character varying nombre_cliente, "
                            +"  inv_etiquetas.folio_origen as lote_produccion, "
                            +"  inv_etiqueta_medidas.largo as etiqueta_largo, "
                            +"  inv_etiqueta_medidas.alto as etiqueta_alto, "
                            +"  inv_etiqueta_medidas.modelo_impresora, "
                            +"  inv_etiquetas_detalle.inv_lote_id, "
                            +"  inv_etiquetas_detalle.lote_interno, "
                            +"  inv_prod.sku as  producto_codigo, "
                            +"  inv_prod.descripcion as  producto_nombre, "
                            +"  inv_etiquetas_detalle.cantidad_produccion as  producto_cantidad, "
                            +"  inv_prod_unidades.titulo_abr as producto_unidad, "
                            +"  ''::character varying as  caducidad_fecha "
                    +"  from inv_etiquetas_detalle "
                    +"  join inv_etiquetas on inv_etiquetas.id=inv_etiquetas_detalle.inv_etiquetas_id "
                    +"  join inv_prod on inv_prod.id=inv_etiquetas_detalle.inv_prod_id  "
                    +"  join inv_etiqueta_medidas on inv_etiqueta_medidas.id=inv_etiquetas_detalle. inv_etiqueta_medidas_id  "
                    +"  join inv_prod_unidades on inv_prod_unidades.id=inv_prod.unidad_id       "
                    +" where   inv_etiquetas_detalle.id="+id_etiquetas_detalle;

                    //+"  join pro_orden_prod on pro_orden_prod.folio=inv_etiquetas_detalle.lote_interno where   inv_etiquetas_detalle.inv_etiquetas_id="+etiqueta_id +" and  gral_emp_id="+ empresa_id;

        //log.log(Level.INFO, "Ejecutando query de {0}", sql_to_query);
        //System.out.println("Imprimiendo el query que trae la produccion o requisicion a imprimir:::   "+sql_to_query);

        Map<String, Object> map = this.getJdbcTemplate().queryForMap(sql_to_query);
        data.put("etiqueta_largo",StringHelper.roundDouble(String.valueOf(map.get("etiqueta_largo")),2));
        data.put("etiqueta_alto",StringHelper.roundDouble(String.valueOf(map.get("etiqueta_alto")),2));
        data.put("modelo_impresora",String.valueOf(map.get("modelo_impresora")));
        data.put("producto_codigo",String.valueOf(map.get("producto_codigo")));
        data.put("producto_nombre",String.valueOf(map.get("producto_nombre")));
        data.put("producto_cantidad",String.valueOf(map.get("producto_cantidad")));
        data.put("producto_unidad",String.valueOf(map.get("producto_unidad")));
        data.put("lote_interno",String.valueOf(map.get("lote_interno")));
        data.put("lote_proveedor",String.valueOf(map.get("lote_proveedor")));
        data.put("caducidad_fecha",String.valueOf(map.get("caducidad_fecha")));
        data.put("nombre_cliente",String.valueOf(map.get("nombre_cliente")));

        data.put("lote_produccion",String.valueOf(map.get("lote_produccion")));

        return data;
    }

    //TERMINA PRODUCTOS IMPRESION DE ETIQUETAS
    //***********************************************************************************************************************************






    //***********************************************************************************************************************************
    //METODOS PARA APLICATIVO DEVOLUCIONES DE MERCANCIAS
    @Override
    public ArrayList<HashMap<String, Object>> getInvOrdenDev_PaginaGrid(String data_string, int offset, int pageSize, String orderBy, String asc) {
        String sql_busqueda = "SELECT id FROM gral_bus_catalogos(?) AS foo (id integer)";

	String sql_to_query = ""
                + "SELECT "
                    + "inv_odev.id, "
                    + "inv_odev.folio, "
                    + "to_char(inv_odev.momento_creacion,'dd/mm/yyyy') AS fecha_dev, "
                    + "cxc_clie.razon_social AS cliente, "
                    + "inv_odev.folio_documento AS factura, "
                    + "to_char(inv_odev.fecha_exp,'dd/mm/yyyy') AS fecha_fac "
                + "FROM inv_odev  "
                + "LEFT JOIN cxc_clie ON cxc_clie.id = inv_odev.cxc_clie_id  "
                +"JOIN ("+sql_busqueda+") as subt on subt.id=inv_odev.id "
                +"ORDER BY "+orderBy+" "+asc+" LIMIT ? OFFSET ?";

        //System.out.println("Busqueda GetPage: "+sql_to_query);

        //log.log(Level.INFO, "Ejecutando query de {0}", sql_to_query);
        ArrayList<HashMap<String, Object>> hm = (ArrayList<HashMap<String, Object>>) this.jdbcTemplate.query(
            sql_to_query,
            new Object[]{new String(data_string),new Integer(pageSize),new Integer(offset)}, new RowMapper() {
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    HashMap<String, Object> row = new HashMap<String, Object>();
                    row.put("id",rs.getInt("id"));
                    row.put("folio",rs.getString("folio"));
                    row.put("fecha_dev",rs.getString("fecha_dev"));
                    row.put("cliente",rs.getString("cliente"));
                    row.put("factura",rs.getString("factura"));
                    row.put("fecha_fac",rs.getString("fecha_fac"));
                    return row;
                }
            }
        );
        return hm;
    }
    
    
    @Override
    public ArrayList<HashMap<String, String>> getInvOrdenDev_Datos(Integer id) {
        String sql_to_query = ""
                + "SELECT "
                    + "inv_odev.id,"
                    + "inv_odev.folio,"
                    + "inv_odev.estatus AS estado,"
                    + "inv_odev.inv_mov_tipo_id AS tipo_mov_id,"
                    + "inv_odev.tipo_documento AS tipo_doc,"
                    + "inv_odev.folio_documento AS folio_doc,"
                    + "inv_odev.fecha_exp AS fecha_doc,"
                    + "inv_odev.cxc_clie_id AS clie_id,"
                    + "cxc_clie.numero_control AS no_clie,"
                    + "cxc_clie.razon_social AS cliente,"
                    + "cxc_clie.rfc,"
                    + "inv_odev.inv_alm_id AS alm_dest_id,"
                    + "inv_odev.moneda_id,"
                    + "gral_mon.descripcion AS moneda,"
                    + "gral_mon.simbolo AS moneda_simbolo,"
                    + "inv_odev.observaciones,"
                    + "(CASE WHEN inv_odev.folio_ncto IS NULL THEN '' ELSE inv_odev.folio_ncto END) AS folio_ncto,"
                    + "(CASE WHEN inv_odev.momento_confirmacion IS NULL THEN '' ELSE to_char(inv_odev.momento_confirmacion,'yyyy-mm-dd') END ) AS fecha_confirmacion, "
                    + "(CASE WHEN inv_odev.momento_creacion IS NULL THEN '' ELSE to_char(inv_odev.momento_creacion,'yyyy-mm-dd') END ) AS fecha_mov,"
                    + "inv_odev.cancelacion "
                + "FROM inv_odev "
                + "JOIN cxc_clie ON cxc_clie.id=inv_odev.cxc_clie_id "
                + "JOIN gral_mon ON gral_mon.id=inv_odev.moneda_id "
                + "WHERE inv_odev.id="+ id + ";";
       // System.out.println("OrdenDev: "+sql_to_query);
        //log.log(Level.INFO, "Ejecutando query de {0}", sql_to_query);
        ArrayList<HashMap<String, String>> hm = (ArrayList<HashMap<String, String>>) this.jdbcTemplate.query(
            sql_to_query,
            new Object[]{}, new RowMapper(){
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    HashMap<String, String> row = new HashMap<String, String>();
                    row.put("id",String.valueOf(rs.getInt("id")));
                    row.put("folio",rs.getString("folio"));
                    row.put("fecha_mov",rs.getString("fecha_mov"));
                    row.put("fecha_confirmacion",rs.getString("fecha_confirmacion"));
                    row.put("estado",String.valueOf(rs.getInt("estado")));
                    row.put("tipo_mov_id",String.valueOf(rs.getInt("tipo_mov_id")));
                    row.put("tipo_doc",String.valueOf(rs.getInt("tipo_doc")));
                    row.put("folio_doc",rs.getString("folio_doc"));
                    row.put("fecha_doc",rs.getString("fecha_doc"));
                    row.put("clie_id",String.valueOf(rs.getInt("clie_id")));
                    row.put("no_clie",rs.getString("no_clie"));
                    row.put("cliente",rs.getString("cliente"));
                    row.put("rfc",rs.getString("rfc"));
                    row.put("alm_dest_id",String.valueOf(rs.getInt("alm_dest_id")));
                    row.put("moneda_id",String.valueOf(rs.getInt("moneda_id")));
                    row.put("observaciones",rs.getString("observaciones"));
                    row.put("folio_ncto",String.valueOf(rs.getString("folio_ncto").trim()));
                    row.put("cancelacion",String.valueOf(rs.getBoolean("cancelacion")));
                    row.put("moneda",rs.getString("moneda"));
                    row.put("moneda_simbolo",rs.getString("moneda_simbolo"));
                    return row;
                }
            }
        );
        return hm;
    }
    
    
    @Override
    public ArrayList<HashMap<String, String>> getInvOrdenDev_DatosGridNcto(String serieFolioNcto, Integer idClie) {
                String sql_to_query = ""
                + "SELECT "
                    + "fac_nota_credito_det.id AS id_det, "
                    + "fac_nota_credito_det.fac_nota_credito_id AS ncto_id,"
                    + "fac_nota_credito_det.inv_prod_id AS prod_id, "
                    + "inv_prod.sku AS codigo, "
                    + "inv_prod.descripcion AS nombre_producto, "
                    + "inv_prod_unidades.titulo AS unidad, "
                    + "fac_nota_credito_det.inv_prod_presentacion_id AS pres_id, "
                    + "inv_prod_presentaciones.titulo AS presentacion, "
                    + "fac_nota_credito_det.cant_fac, "
                    + "fac_nota_credito_det.cantidad AS cant_dev, "
                    + "fac_nota_credito_det.precio_unitario AS precio_u, "
                    + "(fac_nota_credito_det.cantidad::double precision * fac_nota_credito_det.precio_unitario::double precision) AS importe,"
                    + "fac_nota_credito_det.gral_imptos_id AS impto_id, "
                    + "fac_nota_credito_det.valor_imp "
                + "FROM fac_nota_credito_det  "
                + "JOIN fac_nota_credito ON fac_nota_credito.id=fac_nota_credito_det.fac_nota_credito_id "
                + "JOIN inv_prod ON inv_prod.id=fac_nota_credito_det.inv_prod_id "
                + "JOIN inv_prod_unidades ON inv_prod_unidades.id=fac_nota_credito_det.inv_prod_unidad_id "
                + "JOIN inv_prod_presentaciones ON inv_prod_presentaciones.id=fac_nota_credito_det.inv_prod_presentacion_id "
                + "WHERE UPPER(fac_nota_credito.serie_folio)=? AND fac_nota_credito.cxc_clie_id=?;";
                
        //System.out.println("DatosNCTO_Odev: "+sql_to_query);
        
        //log.log(Level.INFO, "Ejecutando query de {0}", sql_to_query);
        ArrayList<HashMap<String, String>> hm = (ArrayList<HashMap<String, String>>) this.jdbcTemplate.query(
            sql_to_query,
            new Object[]{new String(serieFolioNcto), new Integer(idClie)}, new RowMapper(){
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    HashMap<String, String> row = new HashMap<String, String>();
                    row.put("id_det",String.valueOf(rs.getInt("id_det")));
                    row.put("ncto_id",String.valueOf(rs.getInt("ncto_id")));
                    row.put("prod_id",String.valueOf(rs.getInt("prod_id")));
                    row.put("codigo",rs.getString("codigo"));
                    row.put("nombre_producto",rs.getString("nombre_producto"));
                    row.put("unidad",rs.getString("unidad"));
                    row.put("pres_id",rs.getString("pres_id"));
                    row.put("presentacion",rs.getString("presentacion"));
                    row.put("cant_fac",StringHelper.roundDouble(rs.getString("cant_fac"),4));
                    row.put("cant_dev",StringHelper.roundDouble(rs.getString("cant_dev"),4));
                    row.put("precio_u",StringHelper.roundDouble(rs.getString("precio_u"),2));
                    row.put("importe",StringHelper.roundDouble(rs.getString("importe"),2));
                    row.put("impto_id",String.valueOf(rs.getInt("impto_id")));
                    row.put("valor_imp",StringHelper.roundDouble(rs.getString("valor_imp"),4));
                    
                    return row;
                }
            }
        );
        return hm;
    }
    
    
    
    @Override
    public ArrayList<HashMap<String, String>> getInvOrdenDev_DatosGridOsal(Integer tipoDoc, String folioDoc, Integer clienteId) {
                String sql_to_query = ""
                        + "SELECT "
                            + "inv_osal_detalle.id AS id_det,"
                            + "inv_osal_detalle.inv_prod_id AS prod_id, "
                            + "inv_prod.sku AS codigo, "
                            + "inv_prod.descripcion AS nombre_producto, "
                            + "inv_prod_unidades.titulo AS unidad, "
                            + "(CASE WHEN inv_prod_presentaciones.id IS NULL THEN 0 ELSE inv_prod_presentaciones.id END) AS pres_id, "
                            + "(CASE WHEN inv_prod_presentaciones.titulo IS NULL THEN '' ELSE inv_prod_presentaciones.titulo END) AS presentacion, "
                            + "inv_osal_detalle.cantidad AS cant_fac, "
                            + "inv_osal_detalle.cantidad AS cant_dev, "
                            + "inv_osal_detalle.precio_unitario AS precio_u, "
                            + "(inv_osal_detalle.precio_unitario * inv_osal_detalle.cantidad) AS importe "
                        + "FROM inv_osal_detalle "
                        + "JOIN inv_osal ON inv_osal.id=inv_osal_detalle.inv_osal_id "
                        + "LEFT JOIN inv_prod on inv_prod.id = inv_osal_detalle.inv_prod_id "
                        + "LEFT JOIN inv_prod_unidades on inv_prod_unidades.id = inv_osal_detalle.inv_prod_unidad_id "
                        + "LEFT JOIN inv_prod_presentaciones on inv_prod_presentaciones.id = inv_osal_detalle.inv_prod_presentacion_id "
                        + "WHERE inv_osal.tipo_documento=? AND inv_osal.folio_documento=? AND inv_osal.cxc_clie_id=? "
                        + "ORDER BY inv_osal_detalle.id;";
                
        //System.out.println("DatosOsal: "+sql_to_query);
        //log.log(Level.INFO, "Ejecutando query de {0}", sql_to_query);
        ArrayList<HashMap<String, String>> hm = (ArrayList<HashMap<String, String>>) this.jdbcTemplate.query(
            sql_to_query,
            new Object[]{new Integer(tipoDoc), new String(folioDoc), new Integer(clienteId)}, new RowMapper(){
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    HashMap<String, String> row = new HashMap<String, String>();
                    row.put("id_det",String.valueOf(rs.getInt("id_det")));
                    row.put("prod_id",String.valueOf(rs.getInt("prod_id")));
                    row.put("codigo",rs.getString("codigo"));
                    row.put("nombre_producto",rs.getString("nombre_producto"));
                    row.put("unidad",rs.getString("unidad"));
                    row.put("pres_id",rs.getString("pres_id"));
                    row.put("presentacion",rs.getString("presentacion"));
                    row.put("cant_fac",StringHelper.roundDouble(rs.getString("cant_fac"),4));
                    row.put("cant_dev",StringHelper.roundDouble(rs.getString("cant_dev"),4));
                    row.put("precio_u",StringHelper.roundDouble(rs.getString("precio_u"),2));
                    row.put("importe",StringHelper.roundDouble(rs.getString("importe"),2));
                    return row;
                }
            }
        );
        return hm;
    }
    
    
    
    
    @Override
    public ArrayList<HashMap<String, String>> getInvOrdenDev_DatosGridLotes(Integer id) {
                String sql_to_query = ""
                + "SELECT "
                    + "inv_odev_detalle.id AS id_det,"
                    + "inv_osal_detalle.inv_prod_id AS prod_id,"
                    + "inv_osal_detalle.inv_prod_presentacion_id AS pres_id,"
                    + "inv_odev_detalle.inv_lote_id AS lote_id,"
                    + "inv_lote.lote_int,"
                    + "inv_odev_detalle.cant_fac_lote,"
                    + "inv_odev_detalle.cant_dev_lote,"
                    + "(CASE WHEN to_char(inv_lote.caducidad,'yyyymmdd') = '29991231' THEN ''::character varying ELSE to_char(inv_lote.caducidad,'dd/mm/yyyy') END) AS caducidad, "
                    + "(CASE WHEN inv_lote.pedimento='' OR inv_lote.pedimento IS NULL THEN ' ' ELSE inv_lote.pedimento END) AS pedimento "
                + "FROM inv_odev_detalle "
                + "JOIN inv_osal_detalle ON inv_osal_detalle.id=inv_odev_detalle.inv_osal_detalle_id "
                + "JOIN inv_lote ON inv_lote.id=inv_odev_detalle.inv_lote_id "
                + "WHERE inv_odev_detalle.inv_odev_id=?;";
          
        System.out.println("LotesOdev: "+sql_to_query);
        //log.log(Level.INFO, "Ejecutando query de {0}", sql_to_query);
        ArrayList<HashMap<String, String>> hm = (ArrayList<HashMap<String, String>>) this.jdbcTemplate.query(
            sql_to_query,
            new Object[]{new Integer(id)}, new RowMapper(){
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    HashMap<String, String> row = new HashMap<String, String>();
                    row.put("id_det",String.valueOf(rs.getInt("id_det")));
                    row.put("prod_id",String.valueOf(rs.getInt("prod_id")));
                    row.put("pres_id",String.valueOf(rs.getInt("pres_id")));
                    row.put("lote_id",String.valueOf(rs.getInt("lote_id")));
                    row.put("lote_int",rs.getString("lote_int"));
                    row.put("cant_fac_lote",StringHelper.roundDouble(rs.getString("cant_fac_lote"),4));
                    row.put("cant_dev_lote",StringHelper.roundDouble(rs.getString("cant_dev_lote"),4));
                    row.put("caducidad",rs.getString("caducidad"));
                    row.put("pedimento",rs.getString("pedimento"));
                    return row;
                }
            }
        );
        return hm;
    }

    
    
    
    
    //TERMINA APLICATIVO DEVOLUCIONES DE MERCANCIAS
    //***********************************************************************************************************************************
    @Override
    public ArrayList<HashMap<String, String>> getReporteExistenciasLotes_MedidasEtiquetas() {
         String sql_to_query = "SELECT id,titulo FROM inv_etiqueta_medidas;";
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



    //obtiene existencia del almacen por Lotes
    @Override
    public ArrayList<HashMap<String, String>> getReporteExistenciasLotes_Datos(Integer id_almacen, String codigo_producto, String descripcion,Integer tipo, String lote_interno ) {
        String cadena_where="";
        String orderBy="";

        if(tipo == 2){
            cadena_where += " WHERE existencia > 0";
        }

        if(tipo == 3){
            cadena_where += " WHERE existencia < 0";
        }

	String sql_query = ""
                + "SELECT "
                    + "id_lote, "
                    + "lote_int,"
                    + "lote_prov,"
                    + "existencia,"
                    + "codigo,"
                    + "descripcion,"
                    + "unidad_medida,"
                    + "id_medida_etiqueta,"
                    + "id_tipo_producto,"
                    + "fecha_entrada "
                    + "FROM( "
                        + "SELECT  "
                            + "inv_lote.id AS id_lote, "
                            + "inv_lote.lote_int, "
                            + "inv_lote.lote_prov, "
                            + "(inv_lote.inicial - inv_lote.salidas + inv_lote.entradas - inv_lote.reservado) AS  existencia, "
                            + "(CASE WHEN inv_prod.tipo_de_producto_id=7 THEN 1 ELSE 2 END) AS id_medida_etiqueta,"
                            + "inv_prod.tipo_de_producto_id AS id_tipo_producto, "
                            + "inv_prod.sku AS codigo, "
                            + "inv_prod.descripcion, "
                            + "inv_prod_unidades.titulo AS unidad_medida,"
                            + "to_char(inv_lote.momento_creacion,'dd/mm/yyyy') AS fecha_entrada "
                        + "FROM inv_lote "
                        + "JOIN inv_prod ON  inv_prod.id = inv_lote.inv_prod_id "
                        + "JOIN inv_prod_unidades ON inv_prod_unidades.id = inv_prod.unidad_id "
                        + "WHERE inv_lote.inv_alm_id="+id_almacen+" "
                        + "AND inv_prod.sku ILIKE '%"+codigo_producto+"%' "
                        + "AND inv_prod.descripcion ILIKE '%"+descripcion+"%' "
                        + "AND inv_lote.lote_int ILIKE '%"+lote_interno+"%' "
                + ") AS sbt "
                + cadena_where +" "
                + "ORDER BY descripcion,id_lote;";

        //System.out.println("sql_query: "+ sql_query);

        ArrayList<HashMap<String, String>> hm_exis = (ArrayList<HashMap<String, String>>) this.jdbcTemplate.query(
            sql_query,
            new Object[]{}, new RowMapper() {
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    HashMap<String, String> row = new HashMap<String, String>();
                    row.put("id_lote",rs.getString("id_lote"));
                    row.put("lote_int",rs.getString("lote_int"));
                    row.put("lote_prov",rs.getString("lote_prov"));
                    row.put("existencia",StringHelper.roundDouble(rs.getDouble("existencia"),4));
                    row.put("codigo",rs.getString("codigo"));
                    row.put("descripcion",rs.getString("descripcion"));
                    row.put("unidad_medida",rs.getString("unidad_medida"));
                    row.put("id_medida_etiqueta",rs.getString("id_medida_etiqueta"));
                    row.put("id_tipo_producto",rs.getString("id_tipo_producto"));
                    row.put("fecha_entrada",rs.getString("fecha_entrada"));
                    return row;
                }
            }
        );
        return hm_exis;
    }






    @Override
    public LinkedHashMap<String, Object> getDatosEtiquetaLote(Integer id_lote, Integer tipo_producto, Integer id_medida_etiqueta) {
        LinkedHashMap<String, Object> data = new LinkedHashMap<String, Object>();
        String sql_to_query = "";
        String nombre_cliente="";

        //tipo=1 Normal o Terminado
        //tipo=2 Subensable o Formulacion o Intermedio
        //tipo=3 Kit
        //tipo=4 Servicios
        //tipo=5 Refacciones
        //tipo=6 Accesorios
        //tipo=7 Materia Prima
        //tipo=8 Prod. en Desarrollo

        if(tipo_producto==1 || tipo_producto==2 || tipo_producto==8){
            //Buscar el tipo de entrada desde donde se generó el Lote
            sql_to_query = ""
                    + "SELECT "
                        + "inv_oent.tipo_documento, "
                        + "inv_oent.folio_documento,"
                        + "(CASE WHEN pro_orden_prod.pro_preorden_prod_id IS NULL THEN 0 ELSE pro_orden_prod.pro_preorden_prod_id END) AS id_preorden, "
                        + "(CASE WHEN pro_orden_prod.pro_orden_tipos_id IS NULL THEN 0 ELSE pro_orden_prod.pro_orden_tipos_id END) AS id_tipo_orden, "
                        + "(CASE WHEN pro_orden_prod.pro_orden_tipos_id IS NULL THEN '' ELSE pro_orden_tipos.titulo END) AS tipo_orden_produccion "
                    + "FROM inv_lote  "
                    + "JOIN inv_oent_detalle ON inv_oent_detalle.id=inv_lote.inv_oent_detalle_id  "
                    + "JOIN inv_oent ON inv_oent.id=inv_oent_detalle.inv_oent_id  "
                    + "LEFT JOIN pro_orden_prod ON pro_orden_prod.folio=inv_oent.folio_documento "
                    + "LEFT JOIN pro_orden_tipos ON pro_orden_tipos.id=pro_orden_prod.pro_orden_tipos_id "
                    + "WHERE inv_lote.id="+id_lote+";";

            Map<String, Object> mapEnt = this.getJdbcTemplate().queryForMap(sql_to_query);
            //System.out.println("sql_to_query:"+sql_to_query);

            //tipo_documento 4=Produccion
            if(Integer.parseInt(mapEnt.get("tipo_documento").toString()) == 4){
                //inicializar variable
                 sql_to_query="";

                 //id_tipo_orden 1=Pedido
                 if(Integer.parseInt(mapEnt.get("id_tipo_orden").toString()) == 1){

                    //Buscar cliente a partir del Folio del id de la preorden
                    sql_to_query = ""
                            + "SELECT DISTINCT (CASE WHEN cxc_clie.razon_social IS NULL THEN '' ELSE cxc_clie.razon_social END ) AS cliente "
                            + "FROM pro_preorden_prod_det "
                            + "LEFT JOIN poc_pedidos ON poc_pedidos.id=pro_preorden_prod_det.poc_pedidos_id  "
                            + "LEFT JOIN cxc_clie ON cxc_clie.id=poc_pedidos.cxc_clie_id  "
                            + "WHERE pro_preorden_prod_det.pro_preorden_prod_id="+Integer.parseInt(mapEnt.get("id_preorden").toString()) +";";
                            //System.out.println("sql_to_query2:"+sql_to_query);
                    Map<String, Object> mapClient = this.getJdbcTemplate().queryForMap(sql_to_query);

                    nombre_cliente= String.valueOf(mapClient.get("cliente")).toUpperCase();
                 }else{
                     nombre_cliente= String.valueOf(mapEnt.get("tipo_orden_produccion")).toUpperCase();
                 }
            }else{
                nombre_cliente="";
            }
        }else{

            sql_to_query = ""
            +"select razon_social from cxp_prov "
            + "where id=(select cxp_prov_id from inv_oent where id=(select inv_oent_id from inv_oent_detalle "
            + "where id=(select inv_oent_detalle_id from inv_lote where id="+id_lote+" limit 1 ) limit 1)  limit 1) limit 1";

            //System.out.println("razon_social::::"+sql_to_query);

            Map<String, Object> mapClient = this.getJdbcTemplate().queryForMap(sql_to_query);

            nombre_cliente= String.valueOf(mapClient.get("razon_social")).toUpperCase();

        }


        //inicializar variable

        sql_to_query = ""
                + "SELECT inv_prod.sku AS producto_codigo, inv_prod.descripcion AS producto_nombre, inv_lote.lote_int AS lote_interno, "
                + "(CASE WHEN inv_lote.lote_prov IS NULL THEN '' ELSE inv_lote.lote_prov END) AS lote_proveedor, "
                + "(CASE WHEN inv_lote.caducidad = '2999-12-31'::date THEN '' ELSE to_char(inv_lote.caducidad::timestamp with time zone,"
                + "'dd/mm/yyyy') END ) AS caducidad_fecha, ''::character varying AS nombre_cliente, inv_prod_unidades.titulo_abr, "
                + "inv_lote.inicial as cantidad  FROM inv_lote "
                + "JOIN inv_prod ON  inv_prod.id=inv_lote.inv_prod_id "
                + "join inv_prod_unidades on inv_prod_unidades.id=inv_prod.unidad_id "
                + "WHERE inv_lote.id="+id_lote+";";

        //System.out.println("Datos Lote::::"+sql_to_query);
        Map<String, Object> map = this.getJdbcTemplate().queryForMap(sql_to_query);

        data.put("producto_codigo",String.valueOf(map.get("producto_codigo")));
        data.put("producto_nombre",String.valueOf(map.get("producto_nombre")));
        data.put("lote_interno",String.valueOf(map.get("lote_interno")));
        data.put("lote_proveedor",String.valueOf(map.get("lote_proveedor")));
        data.put("caducidad_fecha",String.valueOf(map.get("caducidad_fecha")));
        data.put("nombre_cliente",nombre_cliente);
        data.put("producto_unidad",String.valueOf(map.get("titulo_abr")));
        data.put("producto_cantidad",StringHelper.roundDouble(String.valueOf(map.get("cantidad")),2));


        //inicializar variable
        sql_to_query="";


        //obtener datos de la medida de la etiqueta
        sql_to_query = "SELECT * FROM inv_etiqueta_medidas WHERE id="+id_medida_etiqueta+";";
        Map<String, Object> map2 = this.getJdbcTemplate().queryForMap(sql_to_query);

        data.put("etiqueta_largo",StringHelper.roundDouble(String.valueOf(map2.get("largo")),2));
        data.put("etiqueta_alto",StringHelper.roundDouble(String.valueOf(map2.get("alto")),2));
        data.put("modelo_impresora",String.valueOf(map2.get("modelo_impresora")));


        return data;
    }




    //**************************************************************************************************************************
    //INICIA METODOS DE APLICATIVO INVTRASPASOS
    //**************************************************************************************************************************
    @Override
    public ArrayList<HashMap<String, Object>> getInvTraspasos_PaginaGrid(String data_string, int offset, int pageSize, String orderBy, String asc) {
        String sql_busqueda = "SELECT id FROM gral_bus_catalogos(?) AS foo (id integer)";

	String sql_to_query = ""
                + "SELECT DISTINCT "
                    + "inv_tras.id, "
                    + "inv_tras.folio AS folio_traspaso, "
                    + "alm_ori.titulo AS alm_origen, "
                    + "alm_dest.titulo AS alm_destino, "
                    + "to_char(inv_tras.fecha_traspaso::timestamp with time zone, 'dd/mm/yyyy') AS fecha_traspaso "
                + "FROM inv_tras "
                + "LEFT JOIN inv_alm AS alm_ori ON alm_ori.id=inv_tras.inv_alm_id_origen "
                + "LEFT JOIN inv_alm AS alm_dest ON alm_dest.id=inv_tras.inv_alm_id_destino "
                +"JOIN ("+sql_busqueda+") as subt on subt.id=inv_tras.id "
                +"ORDER BY "+orderBy+" "+asc+" LIMIT ? OFFSET ?";

        //System.out.println("Busqueda GetPage: "+sql_to_query);
        //System.out.println("data_string: "+data_string);
        //log.log(Level.INFO, "Ejecutando query de {0}", sql_to_query);
        ArrayList<HashMap<String, Object>> hm = (ArrayList<HashMap<String, Object>>) this.jdbcTemplate.query(
            sql_to_query,
            new Object[]{new String(data_string),new Integer(pageSize),new Integer(offset)}, new RowMapper() {
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    HashMap<String, Object> row = new HashMap<String, Object>();
                    row.put("id",rs.getInt("id"));
                    row.put("folio_traspaso",rs.getString("folio_traspaso"));
                    row.put("alm_origen",rs.getString("alm_origen"));
                    row.put("alm_destino",rs.getString("alm_destino"));
                    row.put("fecha_traspaso",rs.getString("fecha_traspaso"));
                    return row;
                }
            }
        );
        return hm;
    }



    //busca datos de un producto para agregar al grid de Traspasos
    @Override
    public ArrayList<HashMap<String, String>> getInvTraspasos_DatosProducto(String sku, Integer id_empresa, Integer id_almacen, Integer ano_actual) {

        String sql_to_query = ""
                + "SELECT "
                    + "inv_prod.id AS id_producto, "
                    + "inv_prod.sku, "
                    + "inv_prod.descripcion,"
                    + "inv_prod.densidad,"
                    + "inv_prod_unidades.titulo AS unidad, "
                    + "inv_prod_unidades.decimales AS no_dec,  "
                    + "inv_exi.inv_alm_id AS id_almacen, "
                    + "(inv_exi.exi_inicial - inv_exi.transito - inv_exi.reservado + inv_exi.entradas_1 + inv_exi.entradas_2 + inv_exi.entradas_3 + inv_exi.entradas_4 + inv_exi.entradas_5 + inv_exi.entradas_6 + inv_exi.entradas_7 + inv_exi.entradas_8 + inv_exi.entradas_9 + inv_exi.entradas_10 + inv_exi.entradas_11 + inv_exi.entradas_12 - inv_exi.salidas_1 - inv_exi.salidas_2 - inv_exi.salidas_3 - inv_exi.salidas_4 - inv_exi.salidas_5 - inv_exi.salidas_6 - inv_exi.salidas_7 - inv_exi.salidas_8 - inv_exi.salidas_9 - inv_exi.salidas_10 - inv_exi.salidas_11 - inv_exi.salidas_12) AS existencia "
                + "FROM inv_prod "
                + "JOIN inv_exi ON inv_exi.inv_prod_id=inv_prod.id "
                + "LEFT JOIN inv_prod_tipos ON inv_prod_tipos.id=inv_prod.tipo_de_producto_id "
                + "LEFT JOIN inv_prod_unidades ON inv_prod_unidades.id=inv_prod.unidad_id  "
                + "WHERE inv_prod.empresa_id="+id_empresa+" "
                + "AND inv_prod.borrado_logico=FALSE "
                + "AND inv_exi.inv_alm_id="+id_almacen+" "
                + "AND inv_exi.ano="+ano_actual+" "
                + "AND inv_prod.sku='"+sku.toUpperCase()+"' "
                + "ORDER BY inv_prod.descripcion LIMIT 1;";
        //System.out.println("sql_to_query: "+sql_to_query);

        ArrayList<HashMap<String, String>> hm = (ArrayList<HashMap<String, String>>) this.jdbcTemplate.query(
            sql_to_query,
            new Object[]{}, new RowMapper(){
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    HashMap<String, String> row = new HashMap<String, String>();
                    row.put("id_producto",String.valueOf(rs.getInt("id_producto")));
                    row.put("codigo",rs.getString("sku"));
                    row.put("descripcion",rs.getString("descripcion"));
                    row.put("unidad",rs.getString("unidad"));
                    row.put("no_dec",String.valueOf(rs.getInt("no_dec")));
                    row.put("id_almacen",String.valueOf(rs.getInt("id_almacen")));
                    row.put("existencia",StringHelper.roundDouble(rs.getString("existencia"),rs.getInt("no_dec")));
                    row.put("densidad",StringHelper.roundDouble(String.valueOf(rs.getDouble("densidad")), rs.getInt("no_dec")));
                    
                    return row;
                }
            }
        );
        return hm;
    }


    
    //obtiene las existencias de la presentacion de un Producto en un almacen en especifico
    @Override
    public ArrayList<HashMap<String, String>> getInvTraspasos_ExistenciaPresentacion(Integer id_prod, Integer id_pres, Integer id_alm) {
        String sql_query = "SELECT exis, decimales FROM (SELECT (inv_exi_pres.inicial::double precision + inv_exi_pres.entradas::double precision - inv_exi_pres.salidas::double precision - inv_exi_pres.reservado::double precision) AS exis, inv_prod_unidades.decimales FROM inv_exi_pres JOIN inv_prod ON inv_prod.id=inv_exi_pres.inv_prod_id JOIN inv_prod_unidades ON inv_prod_unidades.id=inv_prod.unidad_id WHERE inv_exi_pres.inv_alm_id=? AND inv_exi_pres.inv_prod_id=? AND inv_exi_pres.inv_prod_presentacion_id=?)AS sbt WHERE exis>0;";
        System.out.println("getExisPres: "+sql_query);
        ArrayList<HashMap<String, String>> hm = (ArrayList<HashMap<String, String>>) this.jdbcTemplate.query(
            sql_query,
            new Object[]{new Integer(id_alm), new Integer(id_prod), new Integer(id_pres)}, new RowMapper() {
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    HashMap<String, String> row = new HashMap<String, String>();
                    row.put("no_dec",String.valueOf(rs.getInt("decimales")));
                    row.put("exis",StringHelper.roundDouble(rs.getString("exis"),rs.getInt("decimales")));
                    return row;
                }
            }
        );
        return hm;
    }
    
    
    

    @Override
    public ArrayList<HashMap<String, String>> getInvTraspasos_Datos(Integer id) {
        String sql_to_query = ""
                + "SELECT "
                    + "id,"
                    + "folio,"
                    + "fecha_traspaso,"
                    + "gral_suc_id_origen,"
                    + "inv_alm_id_origen,"
                    + "gral_suc_id_destino,"
                    + "inv_alm_id_destino,"
                    + "observaciones,"
                    + "cancelacion "
                + "FROM inv_tras "
                + "WHERE id=?;";
        //log.log(Level.INFO, "Ejecutando query de {0}", sql_to_query);

        ArrayList<HashMap<String, String>> hm = (ArrayList<HashMap<String, String>>) this.jdbcTemplate.query(
            sql_to_query,
            new Object[]{new Integer(id)}, new RowMapper(){
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    HashMap<String, String> row = new HashMap<String, String>();
                    row.put("id_traspaso",String.valueOf(rs.getInt("id")));
                    row.put("folio",rs.getString("folio"));
                    row.put("fecha",rs.getString("fecha_traspaso"));
                    row.put("observaciones",rs.getString("observaciones"));
                    row.put("suc_origen",String.valueOf(rs.getInt("gral_suc_id_origen")));
                    row.put("alm_origen",String.valueOf(rs.getInt("inv_alm_id_origen")));
                    row.put("suc_destino",String.valueOf(rs.getInt("gral_suc_id_destino")));
                    row.put("alm_destino",String.valueOf(rs.getInt("inv_alm_id_destino")));
                    row.put("cancelado",String.valueOf(rs.getBoolean("cancelacion")));

                    return row;
                }
            }
        );
        return hm;
    }

    /*
    @Override
    public ArrayList<HashMap<String, String>> getInvTraspasos_DatosGrid(Integer id, Integer id_almacen_origen) {
        String sql_to_query = ""
                + "SELECT "
                    + "inv_prod.id AS id_producto, "
                    + "inv_prod.sku AS codigo, "
                    + "inv_prod.descripcion, "
                    + "inv_prod_unidades.titulo AS unidad,  "
                    + "(inv_exi.exi_inicial - inv_exi.transito - inv_exi.reservado + inv_exi.entradas_1 + inv_exi.entradas_2 + inv_exi.entradas_3 + inv_exi.entradas_4 + inv_exi.entradas_5 + inv_exi.entradas_6 + inv_exi.entradas_7 + inv_exi.entradas_8 + inv_exi.entradas_9 + inv_exi.entradas_10 + inv_exi.entradas_11 + inv_exi.entradas_12 - inv_exi.salidas_1 - inv_exi.salidas_2 - inv_exi.salidas_3 - inv_exi.salidas_4 - inv_exi.salidas_5 - inv_exi.salidas_6 - inv_exi.salidas_7 - inv_exi.salidas_8 - inv_exi.salidas_9 - inv_exi.salidas_10 - inv_exi.salidas_11 - inv_exi.salidas_12) AS existencia, "
                    + "inv_tras_det.cantidad_tras AS cant_traspaso "
                + "FROM inv_tras_det "
                + "JOIN inv_prod ON inv_prod.id=inv_tras_det.inv_prod_id "
                + "JOIN inv_exi ON inv_exi.inv_prod_id=inv_prod.id "
                + "JOIN inv_prod_unidades ON inv_prod_unidades.id=inv_prod.unidad_id "
                + "WHERE inv_tras_det.inv_tras_id=? AND inv_exi.inv_alm_id=? "
                + "ORDER BY inv_tras_det.id;";
        //log.log(Level.INFO, "Ejecutando query de {0}", sql_to_query);

        ArrayList<HashMap<String, String>> hm = (ArrayList<HashMap<String, String>>) this.jdbcTemplate.query(
            sql_to_query,
            new Object[]{new Integer(id), new Integer(id_almacen_origen)}, new RowMapper(){
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    HashMap<String, String> row = new HashMap<String, String>();
                    row.put("id_producto",String.valueOf(rs.getInt("id_producto")));
                    row.put("codigo",rs.getString("codigo"));
                    row.put("descripcion",rs.getString("descripcion"));
                    row.put("unidad",rs.getString("unidad"));
                    row.put("existencia",StringHelper.roundDouble(rs.getDouble("existencia"),4));
                    row.put("cant_traspaso",StringHelper.roundDouble(rs.getDouble("cant_traspaso"),4));
                    return row;
                }
            }
        );
        return hm;
    }
     *
     */
    @Override
    public ArrayList<HashMap<String, String>> getInvTraspasos_DatosGrid(Integer id, Integer id_almacen_origen) {
        String sql_to_query = ""
                + "SELECT "
                    + "inv_prod.id AS id_producto, "
                    + "inv_prod.sku AS codigo, "
                    + "inv_prod.descripcion, "
                    + "inv_prod.densidad, "
                    + "inv_prod_unidades.titulo AS unidad,  "
                    + "inv_prod_unidades.decimales AS no_dec,  "
                    + "(inv_exi.exi_inicial - inv_exi.transito - inv_exi.reservado + inv_exi.entradas_1 + inv_exi.entradas_2 + inv_exi.entradas_3 + inv_exi.entradas_4 + inv_exi.entradas_5 + inv_exi.entradas_6 + inv_exi.entradas_7 + inv_exi.entradas_8 + inv_exi.entradas_9 + inv_exi.entradas_10 + inv_exi.entradas_11 + inv_exi.entradas_12 - inv_exi.salidas_1 - inv_exi.salidas_2 - inv_exi.salidas_3 - inv_exi.salidas_4 - inv_exi.salidas_5 - inv_exi.salidas_6 - inv_exi.salidas_7 - inv_exi.salidas_8 - inv_exi.salidas_9 - inv_exi.salidas_10 - inv_exi.salidas_11 - inv_exi.salidas_12) AS existencia, "
                    + "inv_tras_det.cantidad_tras AS cant_traspaso,"
                    + "inv_tras_det.inv_prod_presentacion_id AS presentacion_id "
                + "FROM inv_tras_det "
                + "JOIN inv_prod ON inv_prod.id=inv_tras_det.inv_prod_id "
                + "JOIN inv_exi ON inv_exi.inv_prod_id=inv_prod.id "
                + "JOIN inv_prod_unidades ON inv_prod_unidades.id=inv_prod.unidad_id "
                + "WHERE inv_tras_det.inv_tras_id=? AND inv_exi.inv_alm_id=? AND inv_exi.ano="+TimeHelper.getFechaActualY()+" "
                + "ORDER BY inv_tras_det.id;";
        //log.log(Level.INFO, "Ejecutando query de {0}", sql_to_query);
        //System.out.println(sql_to_query);
        
        ArrayList<HashMap<String, String>> hm = (ArrayList<HashMap<String, String>>) this.jdbcTemplate.query(
            sql_to_query,
            new Object[]{new Integer(id), new Integer(id_almacen_origen)}, new RowMapper(){
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    HashMap<String, String> row = new HashMap<String, String>();
                    row.put("id_producto",String.valueOf(rs.getInt("id_producto")));
                    row.put("codigo",rs.getString("codigo"));
                    row.put("descripcion",rs.getString("descripcion"));
                    row.put("unidad",rs.getString("unidad"));
                    row.put("no_dec",String.valueOf(rs.getInt("no_dec")));
                    row.put("existencia",StringHelper.roundDouble(rs.getDouble("existencia"),rs.getInt("no_dec")));
                    row.put("cant_traspaso",StringHelper.roundDouble(rs.getDouble("cant_traspaso"),rs.getInt("no_dec")));
                    row.put("densidad",StringHelper.roundDouble(String.valueOf(rs.getDouble("densidad")), rs.getInt("no_dec")));
                    row.put("presentacion_id",String.valueOf(rs.getInt("presentacion_id")));
                    return row;
                }
            }
        );
        return hm;
    }
    
    
    
    
    @Override
    public HashMap<String, String> getInvTraspasos_DatosPDF(Integer id) {
        HashMap<String, String> data = new HashMap<String, String>();

        String sql_to_query = ""
                + "SELECT "
                    + "inv_tras.folio AS folio_traspaso, "
                    + "alm_ori.titulo AS alm_origen, "
                    + "alm_dest.titulo AS alm_destino, "
                    + "to_char(inv_tras.fecha_traspaso::timestamp with time zone, 'dd/mm/yyyy') AS fecha_traspaso,"
                    + "inv_tras.observaciones,"
                    + "inv_tras.cancelacion, "
                    + "gral_empleados.nombre_pila||' '||gral_empleados.apellido_paterno||' '||gral_empleados.apellido_materno AS nombre_usuario "
                + "FROM inv_tras "
                + "LEFT JOIN inv_alm AS alm_ori ON alm_ori.id=inv_tras.inv_alm_id_origen "
                + "LEFT JOIN inv_alm AS alm_dest ON alm_dest.id=inv_tras.inv_alm_id_destino "
                + "JOIN gral_usr ON gral_usr.id=inv_tras.gral_usr_id_creacion "
                + "LEFT JOIN gral_empleados ON gral_empleados.id = gral_usr.gral_empleados_id "
                + "WHERE inv_tras.id="+id+";";

        //System.out.println("Header PDF Traspaso::::"+sql_to_query);

        Map<String, Object> map = this.getJdbcTemplate().queryForMap(sql_to_query);
        data.put("folio_traspaso",map.get("folio_traspaso").toString());
        data.put("alm_origen",map.get("alm_origen").toString());
        data.put("alm_destino",map.get("alm_destino").toString());
        data.put("fecha_traspaso",map.get("fecha_traspaso").toString());
        data.put("observaciones",map.get("observaciones").toString());
        data.put("cancelacion",map.get("cancelacion").toString());
        data.put("nombre_usuario",String.valueOf(map.get("nombre_usuario")));
        return data;
    }


    @Override
    public ArrayList<HashMap<String, String>> getInvTraspasos_DatosGridPDF(Integer id_traspaso) {
        String sql_to_query = ""
                + "SELECT "
                    + "inv_prod.id AS id_producto, "
                    + "inv_prod.sku AS codigo, "
                    + "inv_prod.descripcion, "
                    + "inv_prod_unidades.titulo AS unidad,  "
                    + "inv_prod_unidades.decimales AS no_dec,  "
                    + "inv_tras_det.cantidad_tras AS cant_traspaso "
                + "FROM inv_tras_det "
                + "JOIN inv_prod ON inv_prod.id=inv_tras_det.inv_prod_id "
                + "JOIN inv_prod_unidades ON inv_prod_unidades.id=inv_prod.unidad_id "
                + "WHERE inv_tras_det.inv_tras_id=? "
                + "ORDER BY inv_tras_det.id;";
        //log.log(Level.INFO, "Ejecutando query de {0}", sql_to_query);

        ArrayList<HashMap<String, String>> hm = (ArrayList<HashMap<String, String>>) this.jdbcTemplate.query(
            sql_to_query,
            new Object[]{new Integer(id_traspaso)}, new RowMapper(){
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    HashMap<String, String> row = new HashMap<String, String>();
                    row.put("id_producto",String.valueOf(rs.getInt("id_producto")));
                    row.put("codigo",rs.getString("codigo"));
                    row.put("descripcion",rs.getString("descripcion"));
                    row.put("unidad",rs.getString("unidad"));
                    row.put("no_dec",String.valueOf(rs.getInt("no_dec")));
                    row.put("cant_traspaso",StringHelper.roundDouble(rs.getDouble("cant_traspaso"),rs.getInt("no_dec")));
                    return row;
                }
            }
        );
        return hm;
    }

    //TERMINA METODOS INVTRASPASOS
    //**************************************************************************************************************************




    //**************************************************************************************************************************
    //INICIA METODOS DE APLICATIVO ORDENES DE TRASPASO
    //**************************************************************************************************************************
    @Override
    public ArrayList<HashMap<String, Object>> getInvoOrdenTras_PaginaGrid(String data_string, int offset, int pageSize, String orderBy, String asc) {
        String sql_busqueda = "SELECT id FROM gral_bus_catalogos(?) AS foo (id integer)";

	String sql_to_query = ""
                + "SELECT DISTINCT "
                    + "inv_otras.id, "
                    + "inv_otras.folio, "
                    + "alm_ori.titulo AS alm_origen, "
                    + "alm_dest.titulo AS alm_destino, "
                    + "to_char(inv_otras.fecha::timestamp with time zone, 'dd/mm/yyyy') AS fecha "
                + "FROM inv_otras "
                + "LEFT JOIN inv_alm AS alm_ori ON alm_ori.id=inv_otras.inv_alm_id_origen "
                + "LEFT JOIN inv_alm AS alm_dest ON alm_dest.id=inv_otras.inv_alm_id_destino "
                +"JOIN ("+sql_busqueda+") as subt on subt.id=inv_otras.id "
                +"ORDER BY "+orderBy+" "+asc+" LIMIT ? OFFSET ?";

        //System.out.println("Busqueda GetPage: "+sql_to_query);
        //System.out.println("data_string: "+data_string);
        //log.log(Level.INFO, "Ejecutando query de {0}", sql_to_query);
        ArrayList<HashMap<String, Object>> hm = (ArrayList<HashMap<String, Object>>) this.jdbcTemplate.query(
            sql_to_query,
            new Object[]{new String(data_string),new Integer(pageSize),new Integer(offset)}, new RowMapper() {
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    HashMap<String, Object> row = new HashMap<String, Object>();
                    row.put("id",rs.getInt("id"));
                    row.put("folio",rs.getString("folio"));
                    row.put("alm_origen",rs.getString("alm_origen"));
                    row.put("alm_destino",rs.getString("alm_destino"));
                    row.put("fecha",rs.getString("fecha"));
                    return row;
                }
            }
        );
        return hm;
    }




    @Override
    public ArrayList<HashMap<String, String>> getInvoOrdenTras_Datos(Integer id) {
        String sql_to_query = ""
                + "SELECT "
                    + "id,"
                    + "folio,"
                    + "estatus, "
                    + "fecha,"
                    + "gral_suc_id_origen,"
                    + "inv_alm_id_origen,"
                    + "gral_suc_id_destino,"
                    + "inv_alm_id_destino,"
                    + "observaciones,"
                    + "cancelacion "
                + "FROM inv_otras "
                + "WHERE id=?;";
        //log.log(Level.INFO, "Ejecutando query de {0}", sql_to_query);

        ArrayList<HashMap<String, String>> hm = (ArrayList<HashMap<String, String>>) this.jdbcTemplate.query(
            sql_to_query,
            new Object[]{new Integer(id)}, new RowMapper(){
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    HashMap<String, String> row = new HashMap<String, String>();
                    row.put("id",String.valueOf(rs.getInt("id")));
                    row.put("folio",rs.getString("folio"));
                    row.put("estatus",rs.getString("estatus"));
                    row.put("fecha",rs.getString("fecha"));
                    row.put("observaciones",rs.getString("observaciones"));
                    row.put("suc_origen",String.valueOf(rs.getInt("gral_suc_id_origen")));
                    row.put("alm_origen",String.valueOf(rs.getInt("inv_alm_id_origen")));
                    row.put("suc_destino",String.valueOf(rs.getInt("gral_suc_id_destino")));
                    row.put("alm_destino",String.valueOf(rs.getInt("inv_alm_id_destino")));
                    row.put("cancelado",String.valueOf(rs.getBoolean("cancelacion")));

                    return row;
                }
            }
        );
        return hm;
    }



    @Override
    public ArrayList<HashMap<String, String>> getInvoOrdenTras_DatosGrid(Integer id) {
        String sql_to_query = ""
                + "SELECT "
                    + "inv_otras_det.id AS id_partida, "
                    + "inv_prod.id AS id_producto, "
                    + "inv_prod.sku AS codigo, "
                    + "inv_prod.densidad, "
                    + "inv_prod.descripcion, "
                    + "inv_prod_unidades.titulo AS unidad,  "
                    + "inv_prod_unidades.decimales AS no_dec,  "
                    + "inv_otras_det.cantidad_tras AS cant_traspaso,"
                    + "(CASE WHEN inv_otras_det.inv_prod_presentacion_id > 0 THEN (inv_otras_det.cantidad_tras::double precision / inv_prod_presentaciones.cantidad::double precision) ELSE 0 END) AS cant_pres,"
                    + "(CASE WHEN inv_prod_presentaciones.id IS NULL THEN 0 ELSE inv_prod_presentaciones.id END) AS idPres,"
                    + "(CASE WHEN inv_prod_presentaciones.id IS NULL THEN 0 ELSE inv_prod_presentaciones.cantidad END) AS cant_equiv, "
                    + "(CASE WHEN inv_prod_presentaciones.titulo IS NULL THEN '' ELSE inv_prod_presentaciones.titulo END) AS presentacion "
                + "FROM inv_otras_det "
                + "JOIN inv_prod ON inv_prod.id=inv_otras_det.inv_prod_id "
                + "JOIN inv_prod_unidades ON inv_prod_unidades.id=inv_prod.unidad_id "
                + "LEFT JOIN inv_prod_presentaciones ON inv_prod_presentaciones.id=inv_otras_det.inv_prod_presentacion_id "
                + "WHERE inv_otras_det.inv_otras_id=? "
                + "ORDER BY inv_otras_det.id;";
        System.out.println(sql_to_query);

        ArrayList<HashMap<String, String>> hm = (ArrayList<HashMap<String, String>>) this.jdbcTemplate.query(
            sql_to_query,
            new Object[]{new Integer(id)}, new RowMapper(){
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    HashMap<String, String> row = new HashMap<String, String>();
                    row.put("id_partida",String.valueOf(rs.getInt("id_partida")));
                    row.put("id_producto",String.valueOf(rs.getInt("id_producto")));
                    row.put("codigo",rs.getString("codigo"));
                    row.put("descripcion",rs.getString("descripcion"));
                    row.put("unidad",rs.getString("unidad"));
                    row.put("no_dec",String.valueOf(rs.getInt("no_dec")));
                    row.put("cant_traspaso",StringHelper.roundDouble(rs.getDouble("cant_traspaso"),rs.getInt("no_dec")));
                    row.put("cant_pres",StringHelper.roundDouble(rs.getDouble("cant_pres"),rs.getInt("no_dec")));
                    row.put("densidad",StringHelper.roundDouble(rs.getDouble("densidad"),rs.getInt("no_dec")));
                    row.put("idPres",String.valueOf(rs.getInt("idPres")));
                    row.put("presentacion",rs.getString("presentacion"));
                    row.put("cantEquiv",String.valueOf(rs.getInt("cant_equiv")));
                    return row;
                }
            }
        );
        return hm;
    }
    
    
    
    @Override
    public ArrayList<HashMap<String, String>> getInvoOrdenTras_GridLotes(Integer id) {
        String sql_to_query = ""
                + "SELECT DISTINCT "
                    + "inv_otras_det.id AS id_partida, "
                    + "inv_lote.lote_int,"
                    + "inv_lote_mov_det.cantidad AS cant_traspaso, "
                    + "inv_prod_unidades.decimales AS no_dec, "
                    + "(CASE WHEN inv_otras_det.inv_prod_presentacion_id > 0 THEN (inv_lote_mov_det.cantidad::double precision / inv_prod_presentaciones.cantidad::double precision) ELSE 0 END) AS cant_pres,"
                    + "(CASE WHEN inv_prod_presentaciones.id IS NULL THEN 0 ELSE inv_prod_presentaciones.id END) AS idPres,"
                    + "(CASE WHEN inv_prod_presentaciones.titulo IS NULL THEN '' ELSE inv_prod_presentaciones.titulo END) AS presentacion, "
                    + "(CASE WHEN inv_prod_presentaciones.id IS NULL THEN 0 ELSE inv_prod_presentaciones.cantidad END) AS cant_equiv "
                + "FROM inv_otras_det "
                + "JOIN inv_prod ON inv_prod.id=inv_otras_det.inv_prod_id "
                + "JOIN inv_prod_unidades ON inv_prod_unidades.id=inv_prod.unidad_id "
                + "JOIN inv_lote_mov_det ON inv_lote_mov_det.referencia_det_id=inv_otras_det.id "
                + "JOIN inv_lote ON inv_lote.id=inv_lote_mov_det.inv_lote_id "
                + "LEFT JOIN inv_prod_presentaciones ON inv_prod_presentaciones.id=inv_otras_det.inv_prod_presentacion_id "
                + "WHERE inv_otras_det.inv_otras_id=? "
                + "ORDER BY inv_otras_det.id;";

        System.out.println(sql_to_query);

        ArrayList<HashMap<String, String>> hm = (ArrayList<HashMap<String, String>>) this.jdbcTemplate.query(
            sql_to_query,
            new Object[]{new Integer(id)}, new RowMapper(){
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    HashMap<String, String> row = new HashMap<String, String>();
                    row.put("id_partida",String.valueOf(rs.getInt("id_partida")));
                    row.put("lote_int",rs.getString("lote_int"));
                    row.put("no_dec",String.valueOf(rs.getInt("no_dec")));
                    row.put("cant_traspaso",StringHelper.roundDouble(rs.getDouble("cant_traspaso"),rs.getInt("no_dec")));
                    row.put("cant_pres",StringHelper.roundDouble(rs.getDouble("cant_pres"),rs.getInt("no_dec")));
                    row.put("idPres",String.valueOf(rs.getInt("idPres")));
                    row.put("presentacion",rs.getString("presentacion"));
                    row.put("cantEquiv",String.valueOf(rs.getInt("cant_equiv")));
                    return row;
                }
            }
        );
        return hm;
    }





    @Override
    public HashMap<String, String> getInvOrdenTras_DatosPDF(Integer id) {
        HashMap<String, String> data = new HashMap<String, String>();
        
        String sql_to_query = ""
                + "SELECT  "
                    + "inv_otras.folio, "
                    + "alm_ori.titulo AS alm_origen, "
                    + "alm_dest.titulo AS alm_destino, "
                    + "to_char(inv_otras.fecha::timestamp with time zone, 'dd/mm/yyyy') AS fecha,"
                    + "inv_otras.observaciones,"
                    + "inv_otras.cancelacion, "
                    + "gral_empleados.nombre_pila||' '||gral_empleados.apellido_paterno||' '||gral_empleados.apellido_materno AS nombre_usuario  "
                + "FROM inv_otras  "
                + "LEFT JOIN inv_alm AS alm_ori ON alm_ori.id=inv_otras.inv_alm_id_origen  "
                + "LEFT JOIN inv_alm AS alm_dest ON alm_dest.id=inv_otras.inv_alm_id_destino "
                + "JOIN gral_usr ON gral_usr.id=inv_otras.gral_usr_id_creacion  "
                + "LEFT JOIN gral_empleados ON gral_empleados.id = gral_usr.gral_empleados_id  "
                + "WHERE inv_otras.id="+id+";";

        //System.out.println("Header PDF Orden Traspaso::::"+sql_to_query);

        Map<String, Object> map = this.getJdbcTemplate().queryForMap(sql_to_query);
        data.put("folio",map.get("folio").toString());
        data.put("alm_origen",map.get("alm_origen").toString());
        data.put("alm_destino",map.get("alm_destino").toString());
        data.put("fecha",map.get("fecha").toString());
        data.put("observaciones",map.get("observaciones").toString());
        data.put("cancelacion",map.get("cancelacion").toString());
        data.put("nombre_usuario",String.valueOf(map.get("nombre_usuario")));
        return data;
    }



    //--Métodos para Aplicativo Control de Costos---------------------------------------------------------------------------------
    //metodo  para el grid y paginado
    @Override
    public ArrayList<HashMap<String, Object>> getInvControlCostos_PaginaGrid(String data_string, int offset, int pageSize, String orderBy, String asc) {

        if(orderBy.equals("id")) orderBy="descripcion";

        String sql_to_query = "select * from inv_reporte('"+data_string+"')as foo(producto_id integer, codigo character varying, descripcion character varying, unidad character varying, presentacion_id integer, presentacion character varying, orden_compra character varying, factura_prov character varying, moneda character varying, costo_adic double precision, costo double precision, tipo_cambio double precision, moneda_id integer, costo_importacion double precision, costo_directo double precision, costo_referencia double precision, precio_minimo double precision, moneda_pm character varying  ) ORDER BY "+orderBy+" "+asc+" LIMIT ? OFFSET ?;";
        //System.out.println("ControlCostos_PaginaGrid: "+sql_to_query);

        ArrayList<HashMap<String, Object>> hm125 = (ArrayList<HashMap<String, Object>>) this.jdbcTemplate.query(
            sql_to_query,
            new Object[]{new Integer(pageSize),new Integer(offset)}, new RowMapper(){
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    HashMap<String, Object> row = new HashMap<String, Object>();
                    //row.put("producto_id",String.valueOf(rs.getInt("producto_id")));
                    row.put("codigo",rs.getString("codigo"));
                    row.put("descripcion",rs.getString("descripcion"));
                    row.put("unidad",rs.getString("unidad"));
                    //row.put("presentacion_id",String.valueOf(rs.getInt("presentacion_id")));
                    row.put("presentacion",rs.getString("presentacion"));
                    row.put("orden_compra",rs.getString("orden_compra"));
                    row.put("factura_prov",rs.getString("factura_prov"));
                    row.put("moneda",rs.getString("moneda"));
                    row.put("costo_adic",StringHelper.roundDouble(rs.getDouble("costo_adic"),2));
                    row.put("costo",StringHelper.roundDouble(rs.getDouble("costo"),2));
                    row.put("tipo_cambio",StringHelper.roundDouble(rs.getDouble("tipo_cambio"),4));
                    row.put("costo_importacion",StringHelper.roundDouble(rs.getDouble("costo_importacion"),2));
                    row.put("costo_directo",StringHelper.roundDouble(rs.getDouble("costo_directo"),2));
                    row.put("costo_referencia",StringHelper.roundDouble(rs.getDouble("costo_referencia"),2));
                    row.put("precio_minimo",StringHelper.roundDouble(rs.getDouble("precio_minimo"),2));
                    row.put("moneda_pm",rs.getString("moneda_pm"));
                    return row;
                }
            }
        );
        return hm125;
    }





    @Override
    public ArrayList<HashMap<String, String>> getBuscadorProductosParaControlCostos(String marca, String familia, String subfamilia, String sku, String tipo, String descripcion, Integer id_empresa) {
        String where = "";

	if(!marca.equals("0")){
            where+=" AND inv_prod.inv_mar_id="+marca+" ";
	}

	if(!familia.equals("0")){
            where+=" AND inv_prod.inv_prod_familia_id="+familia+" ";
	}

	if(!subfamilia.equals("0")){
            where+=" AND inv_prod.subfamilia_id="+subfamilia+" ";
	}

	if(!sku.equals("")){
            where+=" AND inv_prod.sku ilike '%"+sku+"%'";
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




    //esto es para cargar el select de años del Buscador en el paginado
    @Override
    public ArrayList<HashMap<String, String>>  getInvControlCostos_Anios() {
        ArrayList<HashMap<String, String>> anios = new ArrayList<HashMap<String, String>>();

        Calendar c1 = Calendar.getInstance();
        Integer annio = c1.get(Calendar.YEAR);//obtiene el año actual

        for(int i=0; i<2; i++) {
            HashMap<String, String> row = new HashMap<String, String>();
            row.put("valor",String.valueOf((annio-i)));
            anios.add(i, row);
        }
        return anios;
    }



    //-----Termina Métodos para el Aplicativo Control de Costos----------------------------------------------------------------


    //--Métodos para Aplicativo Actualizador de Precios---------------------------------------------------------------------------------
    //metodo  para el grid y paginado
    @Override
    public ArrayList<HashMap<String, Object>> getInvActualizaPrecio_PaginaGrid(String data_string, int offset, int pageSize, String orderBy, String asc) {
        
        if(orderBy.equals("id")) orderBy="descripcion";
        
        String sql_to_query = "select * from inv_reporte('"+data_string+"')as foo(prod_id integer, codigo character varying, descripcion character varying, unidad character varying, pres_id integer, presentacion character varying, moneda_id integer, moneda character varying, tc double precision, precio_minimo double precision) ORDER BY "+orderBy+" "+asc+" LIMIT ? OFFSET ?;";
        //System.out.println("getInvActualizaPrecio: "+sql_to_query);
        
        ArrayList<HashMap<String, Object>> hm125 = (ArrayList<HashMap<String, Object>>) this.jdbcTemplate.query(
            sql_to_query,
            new Object[]{new Integer(pageSize),new Integer(offset)}, new RowMapper(){
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    HashMap<String, Object> row = new HashMap<String, Object>();
                    row.put("prod_id",String.valueOf(rs.getInt("prod_id")));
                    row.put("codigo",rs.getString("codigo"));
                    row.put("descripcion",rs.getString("descripcion"));
                    row.put("unidad",rs.getString("unidad"));
                    row.put("pres_id",String.valueOf(rs.getInt("pres_id")));
                    row.put("presentacion",rs.getString("presentacion"));
                    row.put("moneda_id",String.valueOf(rs.getInt("moneda_id")));
                    row.put("moneda",rs.getString("moneda"));
                    row.put("tc",StringHelper.roundDouble(rs.getDouble("tc"),2));
                    row.put("precio_minimo",StringHelper.roundDouble(rs.getDouble("precio_minimo"),2));
                    return row;
                }
            }
        );
        return hm125;
    }
    //------------termina metodos para aplicativo Actualizador de Precios
    
    
    
    //Metodo que extrae losmovimientos
    @Override
    public ArrayList<HashMap<String, String>> getMovimientos(Integer tipo_movimiento,Integer id_almacen,String codigo, String descripcion,String fecha_inicial,String fecha_final ,Integer id_empresa, Integer id_usuario) {
        String sql_to_query = "select * from inv_Reporte_movimientos("+tipo_movimiento+","+id_almacen+","+id_empresa+",'"+fecha_inicial+"', '"+fecha_final+"' ,"+id_usuario+",'"+codigo+"','"+descripcion+"'  )   "
	+"  as foo(  "
		+"  codigo character varying,  "
		+"  descripcion character varying,  "
                +"  unidad character varying,  "
		+"  existencia double precision,  "
		+"  referencia character varying,  "
		+"  tipo_movimiento character varying,  "
		+"  fecha_movimiento character varying,  "
		+"  sucursal character varying,  "
		+"  almacen character varying,  "
		+"  cantidad double precision,  "
                +"  existencia_actual double precision,  "
		+"  costo double precision  "
	+"  );";
        
        System.out.println("Ejecutando query: "+ sql_to_query);
        
        ArrayList<HashMap<String, String>> hm_datos_existencias = (ArrayList<HashMap<String, String>>)
        this.jdbcTemplate.query(sql_to_query,
            new Object[]{}, new RowMapper(){
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    HashMap<String, String> row = new HashMap<String, String>();
                    row.put("codigo",rs.getString("codigo"));
                    row.put("descripcion",rs.getString("descripcion"));
                    row.put("unidad",rs.getString("unidad"));
                    row.put("existencia",StringHelper.roundDouble(rs.getString("existencia"),2));
                    row.put("referencia",rs.getString("referencia"));
                    row.put("tipo_movimiento",rs.getString("tipo_movimiento"));
                    row.put("fecha_movimiento",rs.getString("fecha_movimiento"));
                    row.put("sucursal",rs.getString("sucursal"));
                    row.put("almacen",rs.getString("almacen"));
                    row.put("cantidad",StringHelper.roundDouble(rs.getDouble("cantidad"),2));
                    row.put("costo",StringHelper.roundDouble(rs.getDouble("costo"),2));
                    row.put("existencia_actual",StringHelper.roundDouble(rs.getDouble("existencia_actual"),2));
                    return row;
                }
            }
        );
        return hm_datos_existencias;
    }
    
    
    
    //**************************************************************************************************************************
    //INICIA METODOS DE APLICATIVO DE CAPTURA DE COSTOS
    //**************************************************************************************************************************
    //Obtiene datos para el Grid solo costos Ultimos
    @Override
    public ArrayList<HashMap<String, Object>> getInvCapturaCosto_PaginaGrid(String data_string, int offset, int pageSize, String orderBy, String asc) {
        String sql_busqueda = "SELECT id FROM gral_bus_catalogos(?) AS foo (id integer)";
        String cad[] = data_string.split("___");
        String mesActual=cad[6];
        
	String sql_to_query = ""
        + "SELECT "
            + "inv_prod_cost_prom.id, "
            + "inv_prod.sku AS codigo,"
            + "inv_prod.descripcion,"
            + "(CASE WHEN gral_mon.descripcion_abr IS NULL THEN '' ELSE gral_mon.descripcion_abr END) AS moneda,"
            + "inv_prod_cost_prom.tipo_cambio_"+mesActual+" AS tc,"
            + "inv_prod_cost_prom.costo_ultimo_"+mesActual+" AS costo,"
            + "(CASE WHEN inv_prod_cost_prom.actualizacion_"+mesActual+" IS NULL THEN '' ELSE to_char(inv_prod_cost_prom.actualizacion_"+mesActual+",'dd/mm/yyyy') END) AS fecha "
        + "FROM inv_prod_cost_prom "
        + "JOIN inv_prod ON inv_prod.id=inv_prod_cost_prom.inv_prod_id "
        + "LEFT JOIN gral_mon ON gral_mon.id=inv_prod_cost_prom.gral_mon_id_"+mesActual+" "
        +"JOIN ("+sql_busqueda+") as subt on subt.id=inv_prod_cost_prom.id "
        +"ORDER BY "+orderBy+" "+asc+" LIMIT ? OFFSET ?";
        
        //System.out.println("Busqueda GetPage: "+sql_to_query);
        //System.out.println("data_string: "+data_string);
        ArrayList<HashMap<String, Object>> hm = (ArrayList<HashMap<String, Object>>) this.jdbcTemplate.query(
            sql_to_query,
            new Object[]{data_string,new Integer(pageSize),new Integer(offset)}, new RowMapper() {
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    HashMap<String, Object> row = new HashMap<String, Object>();
                    row.put("id",rs.getInt("id"));
                    row.put("codigo",rs.getString("codigo"));
                    row.put("descripcion",rs.getString("descripcion"));
                    row.put("moneda",rs.getString("moneda"));
                    row.put("tc",StringHelper.roundDouble(rs.getString("tc"),4));
                    row.put("costo",StringHelper.roundDouble(rs.getString("costo"),2));
                    row.put("fecha",rs.getString("fecha"));
                    return row;
                }
            }
        );
        return hm;
    }
    
    
    //Obtiene datos para el Grid con Costos de Refrencia
    @Override
    public ArrayList<HashMap<String, Object>> getInvCapturaCosto_PaginaGrid2(String data_string, int offset, int pageSize, String orderBy, String asc) {
        String sql_busqueda = "SELECT id FROM gral_bus_catalogos(?) AS foo (id integer)";
        String cad[] = data_string.split("___");
        String mesActual=cad[6];
        
	String sql_to_query = ""
        + "SELECT "
            + "sbt.id, "
            + "codigo,"
            + "descripcion,"
            + "moneda,"
            + "presentacion,"
            + "tc, "
            + "costo,"
            + "(CASE WHEN sbt.costo > 0 THEN (sbt.costo * sbt.igi) ELSE 0 END) as igi, "
            //+ "(CASE WHEN sbt.costo > 0 THEN (sbt.costo * sbt.gi) ELSE 0 END) as gi,"
            + "(CASE WHEN sbt.costo > 0 THEN ((sbt.costo + (sbt.costo * sbt.igi)) * sbt.gi) ELSE 0 END) as gi,"
            + "ca,"
            //+ "(CASE WHEN sbt.costo > 0 THEN (sbt.costo + sbt.ca + (sbt.costo * sbt.igi) + (sbt.costo * sbt.gi)) ELSE 0 END) AS cit,"
            + "(CASE WHEN sbt.costo > 0 THEN (sbt.costo + sbt.ca + (sbt.costo * sbt.igi) + ((sbt.costo + (sbt.costo * sbt.igi)) * sbt.gi)) ELSE 0 END) AS cit,"
            //+ "(CASE WHEN sbt.costo > 0 THEN (sbt.costo + sbt.ca + (sbt.costo * sbt.igi) + (sbt.costo * sbt.gi))/(1 - sbt.pmin) ELSE 0 END) AS pmin,"
            + "(CASE WHEN sbt.costo > 0 THEN (sbt.costo + sbt.ca + (sbt.costo * sbt.igi) + ((sbt.costo + (sbt.costo * sbt.igi)) * sbt.gi))/(1 - sbt.pmin) ELSE 0 END) AS pmin,"
            + "fecha "
        + "FROM ("
            + "SELECT inv_prod_cost_prom.id, "
            + "inv_prod.sku AS codigo,"
            + "inv_prod.descripcion,"
            + "(CASE WHEN gral_mon.descripcion_abr IS NULL THEN '' ELSE gral_mon.descripcion_abr END) AS moneda,"
            + "tbl_pres.titulo AS presentacion,"
            + "inv_prod_cost_prom.tipo_cambio_"+mesActual+" AS tc,"
            + "inv_prod_cost_prom.costo_ultimo_"+mesActual+" AS costo,"
            + "(CASE WHEN sbt_costos.costo_imp_"+mesActual+" IS NULL THEN 0 ELSE (sbt_costos.costo_imp_"+mesActual+"/100)::double precision END ) AS igi,  "
            + "(CASE WHEN sbt_costos.costo_dir_"+mesActual+" IS NULL THEN 0 ELSE (sbt_costos.costo_dir_"+mesActual+"/100)::double precision END ) AS gi, "
            + "(CASE WHEN sbt_costos.costo_adic_"+mesActual+" IS NULL THEN 0 ELSE sbt_costos.costo_adic_"+mesActual+" END ) AS ca, "
            + "(CASE WHEN sbt_costos.precio_min_"+mesActual+" IS NULL THEN 0 ELSE (sbt_costos.precio_min_"+mesActual+"/100)::double precision END ) AS pmin,"
            + "(CASE WHEN inv_prod_cost_prom.actualizacion_"+mesActual+" IS NULL THEN '' ELSE to_char(inv_prod_cost_prom.actualizacion_"+mesActual+",'dd/mm/yyyy') END) AS fecha "
            + "FROM inv_prod_cost_prom "
            + "JOIN inv_prod ON inv_prod.id=inv_prod_cost_prom.inv_prod_id "
            + "JOIN inv_prod_pres_x_prod ON inv_prod_pres_x_prod.producto_id=inv_prod.id "
            + "JOIN inv_prod_presentaciones AS tbl_pres ON tbl_pres.id=inv_prod_pres_x_prod.presentacion_id "
            + "LEFT JOIN ("
                + "SELECT distinct ano,inv_prod_id,inv_prod_presentacion_id,costo_imp_1,costo_dir_1,precio_min_1,actualizacion_1,usr_id_actualiza_1,costo_imp_2,costo_dir_2,precio_min_2,actualizacion_2,usr_id_actualiza_2,costo_imp_3,costo_dir_3,precio_min_3,actualizacion_3,usr_id_actualiza_3,costo_imp_4,costo_dir_4,precio_min_4,actualizacion_4,usr_id_actualiza_4,costo_imp_5,costo_dir_5,precio_min_5,actualizacion_5,usr_id_actualiza_5,costo_imp_6,costo_dir_6,precio_min_6,actualizacion_6,usr_id_actualiza_6,costo_imp_7,costo_dir_7,precio_min_7,actualizacion_7,usr_id_actualiza_7,costo_imp_8,costo_dir_8,precio_min_8,actualizacion_8,usr_id_actualiza_8,costo_imp_9,costo_dir_9,precio_min_9,actualizacion_9,usr_id_actualiza_9,costo_imp_10,costo_dir_10,precio_min_10,actualizacion_10,usr_id_actualiza_10,costo_imp_11,costo_dir_11,precio_min_11,actualizacion_11,usr_id_actualiza_11,costo_imp_12,costo_dir_12,precio_min_12,actualizacion_12,usr_id_actualiza_12,gral_emp_id,costo_adic_1,costo_adic_2,costo_adic_3,costo_adic_4,costo_adic_5,costo_adic_6,costo_adic_7,costo_adic_8,costo_adic_9,costo_adic_10, costo_adic_11, costo_adic_12 FROM inv_prod_costos "
            + ") AS sbt_costos ON (sbt_costos.inv_prod_id=inv_prod.id AND sbt_costos.inv_prod_presentacion_id=tbl_pres.id AND sbt_costos.ano=EXTRACT(YEAR FROM now())::integer ) "
            + "LEFT JOIN gral_mon ON gral_mon.id=inv_prod_cost_prom.gral_mon_id_"+mesActual+" "
        + ") AS sbt "
        +"JOIN ("+sql_busqueda+") as subt on subt.id=sbt.id "
        +"ORDER BY "+orderBy+" "+asc+" LIMIT ? OFFSET ?";
        
        //System.out.println("Busqueda GetPage: "+sql_to_query);
        //System.out.println("data_string: "+data_string);
        ArrayList<HashMap<String, Object>> hm = (ArrayList<HashMap<String, Object>>) this.jdbcTemplate.query(
            sql_to_query,
            new Object[]{data_string,new Integer(pageSize),new Integer(offset)}, new RowMapper() {
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    HashMap<String, Object> row = new HashMap<String, Object>();
                    row.put("id",rs.getInt("id"));
                    row.put("codigo",rs.getString("codigo"));
                    row.put("descripcion",rs.getString("descripcion"));
                    row.put("presentacion",rs.getString("presentacion"));
                    row.put("moneda",rs.getString("moneda"));
                    row.put("tc",StringHelper.roundDouble(rs.getString("tc"),4));
                    row.put("costo",StringHelper.roundDouble(rs.getString("costo"),2));
                    row.put("igi",StringHelper.roundDouble(rs.getString("igi"),2));
                    row.put("gi",StringHelper.roundDouble(rs.getString("gi"),2));
                    row.put("ca",StringHelper.roundDouble(rs.getString("ca"),2));
                    row.put("cit",StringHelper.roundDouble(rs.getString("cit"),2));
                    row.put("pmin",StringHelper.roundDouble(rs.getString("pmin"),2));
                    row.put("fecha",rs.getString("fecha"));
                    return row;
                }
            }
        );
        return hm;
    }
    
    
    //Obtiene el costo de un producto al seleccionar editar un registro del grid
    @Override
    public ArrayList<HashMap<String, String>> getInvCapturaCosto_CostoProducto(Integer id, Integer anoActual, Integer mesActual, String captura_costo_ref) {
        String sql_to_query = "";
        if(captura_costo_ref.equals("false")){
            sql_to_query = ""
            + "SELECT "
                + "(CASE WHEN inv_prod_cost_prom.id IS NULL THEN 0 ELSE inv_prod_cost_prom.id END) AS id_reg,"
                + "inv_prod.id AS id_prod, "
                + "inv_prod.sku AS codigo, "
                + "inv_prod.descripcion,"
                + "(CASE WHEN inv_prod_unidades.titulo IS NULL THEN '' ELSE inv_prod_unidades.titulo END) AS unidad, "
                + "(CASE WHEN inv_prod_unidades.decimales IS NULL THEN 0 ELSE inv_prod_unidades.decimales END) AS no_dec, "
                + "(CASE WHEN inv_prod_cost_prom.id IS NULL THEN 0 ELSE inv_prod_cost_prom.costo_ultimo_"+mesActual+" END) AS costo, "
                + "(CASE WHEN inv_prod_cost_prom.id IS NULL THEN 1 ELSE (CASE WHEN inv_prod_cost_prom.gral_mon_id_"+mesActual+"=0 THEN 1 ELSE inv_prod_cost_prom.gral_mon_id_"+mesActual+" END) END) AS idMon,"
                + "(CASE WHEN inv_prod_cost_prom.id IS NULL THEN 1 ELSE (CASE WHEN inv_prod_cost_prom.tipo_cambio_"+mesActual+"=0 THEN 1 ELSE inv_prod_cost_prom.tipo_cambio_"+mesActual+" END) END) AS tc,"
                + "0::integer AS id_pres,"
                + "''::character varying AS presentacion,"
                + "0::double precision AS igi,  "
                + "0::double precision AS gi, "
                + "0::double precision AS ca, "
                + "0::double precision AS margen_pmin,"
                + "0::double precision AS cit,"
                + "0::double precision AS pmin "
            + "FROM inv_prod "
            + "LEFT JOIN inv_prod_cost_prom ON (inv_prod_cost_prom.inv_prod_id=inv_prod.id AND inv_prod_cost_prom.ano="+anoActual+") "
            + "LEFT JOIN inv_prod_unidades ON inv_prod_unidades.id=inv_prod.unidad_id  "
            + "WHERE inv_prod_cost_prom.id="+id+" "
            + "ORDER BY inv_prod.descripcion LIMIT 1;";
            
        }else{
                 
            sql_to_query = ""
            + "SELECT "
                + "id_reg,"
                + "id_prod, "
                + "codigo, "
                + "descripcion,"
                + "unidad, "
                + "no_dec, "
                + "idMon,"
                + "tc, "
                + "costo, "
                + "id_pres,"
                + "presentacion,"
                + "igi,  "
                + "gi, "
                + "ca, "
                + "margen_pmin,"
                //+ "(CASE WHEN sbt.costo > 0 THEN (sbt.costo + sbt.ca + (sbt.costo * (CASE WHEN sbt.igi=0 THEN 0 ELSE (sbt.igi/100)::double precision END)) + (sbt.costo * (CASE WHEN sbt.gi=0 THEN 0 ELSE (sbt.gi/100)::double precision END))) ELSE 0 END) AS cit,"
                + "(CASE WHEN sbt.costo > 0 THEN (sbt.costo + sbt.ca + (sbt.costo * (CASE WHEN sbt.igi=0 THEN 0 ELSE (sbt.igi/100)::double precision END)) + ((sbt.costo + (sbt.costo * (CASE WHEN sbt.igi=0 THEN 0 ELSE (sbt.igi/100)::double precision END))) * (CASE WHEN sbt.gi=0 THEN 0 ELSE (sbt.gi/100)::double precision END))) ELSE 0 END) AS cit,"
                //+ "(CASE WHEN sbt.costo > 0 THEN (sbt.costo + sbt.ca + (sbt.costo * (CASE WHEN sbt.igi=0 THEN 0 ELSE (sbt.igi/100)::double precision END)) + (sbt.costo * (CASE WHEN sbt.gi=0 THEN 0 ELSE (sbt.gi/100)::double precision END)))/(1 - (CASE WHEN sbt.margen_pmin=0 THEN 0 ELSE (sbt.margen_pmin/100)::double precision END)) ELSE 0 END) AS pmin  "
                + "(CASE WHEN sbt.costo > 0 THEN (sbt.costo + sbt.ca + (sbt.costo * (CASE WHEN sbt.igi=0 THEN 0 ELSE (sbt.igi/100)::double precision END)) + ((sbt.costo + (sbt.costo * (CASE WHEN sbt.igi=0 THEN 0 ELSE (sbt.igi/100)::double precision END))) * (CASE WHEN sbt.gi=0 THEN 0 ELSE (sbt.gi/100)::double precision END)))/(1 - (CASE WHEN sbt.margen_pmin=0 THEN 0 ELSE (sbt.margen_pmin/100)::double precision END)) ELSE 0 END) AS pmin  "
            + "FROM ("
                + "SELECT "
                    + "(CASE WHEN inv_prod_cost_prom.id IS NULL THEN 0 ELSE inv_prod_cost_prom.id END) AS id_reg,"
                    + "inv_prod.id AS id_prod, "
                    + "inv_prod.sku AS codigo, "
                    + "inv_prod.descripcion,"
                    + "(CASE WHEN inv_prod_unidades.titulo IS NULL THEN '' ELSE inv_prod_unidades.titulo END) AS unidad, "
                    + "(CASE WHEN inv_prod_unidades.decimales IS NULL THEN 0 ELSE inv_prod_unidades.decimales END) AS no_dec, "
                    + "(CASE WHEN inv_prod_cost_prom.id IS NULL THEN 0 ELSE inv_prod_cost_prom.costo_ultimo_"+mesActual+" END) AS costo, "
                    + "(CASE WHEN inv_prod_cost_prom.id IS NULL THEN 1 ELSE (CASE WHEN inv_prod_cost_prom.gral_mon_id_"+mesActual+"=0 THEN 1 ELSE inv_prod_cost_prom.gral_mon_id_"+mesActual+" END) END) AS idMon,"
                    + "(CASE WHEN inv_prod_cost_prom.id IS NULL THEN 1 ELSE (CASE WHEN inv_prod_cost_prom.tipo_cambio_"+mesActual+"=0 THEN 1 ELSE inv_prod_cost_prom.tipo_cambio_"+mesActual+" END) END) AS tc, "
                    + "tbl_pres.id AS id_pres,"
                    + "tbl_pres.titulo AS presentacion,"
                    + "(CASE WHEN sbt_costos.costo_imp_"+mesActual+" IS NULL THEN 0 ELSE sbt_costos.costo_imp_"+mesActual+" END ) AS igi,  "
                    + "(CASE WHEN sbt_costos.costo_dir_"+mesActual+" IS NULL THEN 0 ELSE sbt_costos.costo_dir_"+mesActual+" END ) AS gi, "
                    + "(CASE WHEN sbt_costos.costo_adic_"+mesActual+" IS NULL THEN 0 ELSE sbt_costos.costo_adic_"+mesActual+" END ) AS ca, "
                    + "(CASE WHEN sbt_costos.precio_min_"+mesActual+" IS NULL THEN 0 ELSE sbt_costos.precio_min_"+mesActual+" END ) AS margen_pmin "
                + "FROM inv_prod "
                + "LEFT JOIN inv_prod_cost_prom ON (inv_prod_cost_prom.inv_prod_id=inv_prod.id AND inv_prod_cost_prom.ano="+anoActual+") "
                + "LEFT JOIN inv_prod_unidades ON inv_prod_unidades.id=inv_prod.unidad_id  "
                + "JOIN inv_prod_pres_x_prod ON inv_prod_pres_x_prod.producto_id=inv_prod.id "
                + "JOIN inv_prod_presentaciones AS tbl_pres ON tbl_pres.id=inv_prod_pres_x_prod.presentacion_id "
                + "LEFT JOIN ("
                    + "SELECT distinct ano,inv_prod_id,inv_prod_presentacion_id,costo_imp_1,costo_dir_1,precio_min_1,actualizacion_1,usr_id_actualiza_1,costo_imp_2,costo_dir_2,precio_min_2,actualizacion_2,usr_id_actualiza_2,costo_imp_3,costo_dir_3,precio_min_3,actualizacion_3,usr_id_actualiza_3,costo_imp_4,costo_dir_4,precio_min_4,actualizacion_4,usr_id_actualiza_4,costo_imp_5,costo_dir_5,precio_min_5,actualizacion_5,usr_id_actualiza_5,costo_imp_6,costo_dir_6,precio_min_6,actualizacion_6,usr_id_actualiza_6,costo_imp_7,costo_dir_7,precio_min_7,actualizacion_7,usr_id_actualiza_7,costo_imp_8,costo_dir_8,precio_min_8,actualizacion_8,usr_id_actualiza_8,costo_imp_9,costo_dir_9,precio_min_9,actualizacion_9,usr_id_actualiza_9,costo_imp_10,costo_dir_10,precio_min_10,actualizacion_10,usr_id_actualiza_10,costo_imp_11,costo_dir_11,precio_min_11,actualizacion_11,usr_id_actualiza_11,costo_imp_12,costo_dir_12,precio_min_12,actualizacion_12,usr_id_actualiza_12,gral_emp_id,costo_adic_1,costo_adic_2,costo_adic_3,costo_adic_4,costo_adic_5,costo_adic_6,costo_adic_7,costo_adic_8,costo_adic_9,costo_adic_10, costo_adic_11, costo_adic_12 FROM inv_prod_costos "
                + ") AS sbt_costos ON (sbt_costos.inv_prod_id=inv_prod.id AND sbt_costos.inv_prod_presentacion_id=tbl_pres.id AND sbt_costos.ano="+anoActual+") "
                + "WHERE inv_prod_cost_prom.id="+id+" "
            + ") AS sbt "
            + "ORDER BY sbt.descripcion;";
        }
        
        //mSystem.out.println("costoProd: "+sql_to_query);
        
        ArrayList<HashMap<String, String>> hm = (ArrayList<HashMap<String, String>>) this.jdbcTemplate.query(
            sql_to_query,
            new Object[]{}, new RowMapper(){
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    HashMap<String, String> row = new HashMap<String, String>();
                    row.put("id_reg",String.valueOf(rs.getInt("id_reg")));
                    row.put("id_prod",String.valueOf(rs.getInt("id_prod")));
                    row.put("codigo",rs.getString("codigo"));
                    row.put("descripcion",rs.getString("descripcion"));
                    row.put("unidad",rs.getString("unidad"));
                    row.put("no_dec",String.valueOf(rs.getInt("no_dec")));
                    row.put("costo_ultimo",StringHelper.roundDouble(rs.getString("costo"),2));
                    row.put("idMon",String.valueOf(rs.getInt("idMon")));
                    row.put("tc",StringHelper.roundDouble(rs.getString("tc"),4));
                    row.put("id_pres",String.valueOf(rs.getInt("id_pres")));
                    row.put("presentacion",rs.getString("presentacion"));
                    row.put("igi",StringHelper.roundDouble(rs.getString("igi"),2));
                    row.put("gi",StringHelper.roundDouble(rs.getString("gi"),2));
                    row.put("ca",StringHelper.roundDouble(rs.getString("ca"),2));
                    row.put("margen_pmin",StringHelper.roundDouble(rs.getString("margen_pmin"),2));
                    row.put("cit",StringHelper.roundDouble(rs.getString("cit"),2));
                    row.put("pmin",StringHelper.roundDouble(rs.getString("pmin"),2));
                    return row;
                }
            }
        );
        return hm;
    }
    
    
    
    
    
    //Busca datos de un producto para agregar al grid de Captura de Costos
    @Override
    public ArrayList<HashMap<String, String>> getInvCapturaCosto_DatosProducto(String sku, Integer idEmp, Integer mesActual, Integer anoActual, String captura_costo_ref) {
        String sql_to_query = "";
        
        if(captura_costo_ref.equals("false")){
            sql_to_query = ""
            + "SELECT "
                + "(CASE WHEN inv_prod_cost_prom.id IS NULL THEN 0 ELSE inv_prod_cost_prom.id END) AS id_reg,"
                + "inv_prod.id AS id_prod, "
                + "inv_prod.sku AS codigo, "
                + "inv_prod.descripcion,"
                + "(CASE WHEN inv_prod_unidades.titulo IS NULL THEN '' ELSE inv_prod_unidades.titulo END) AS unidad, "
                + "(CASE WHEN inv_prod_unidades.decimales IS NULL THEN 0 ELSE inv_prod_unidades.decimales END) AS no_dec, "
                + "(CASE WHEN inv_prod_cost_prom.id IS NULL THEN 0 ELSE inv_prod_cost_prom.costo_ultimo_"+mesActual+" END) AS costo, "
                + "(CASE WHEN inv_prod_cost_prom.id IS NULL THEN 1 ELSE (CASE WHEN inv_prod_cost_prom.gral_mon_id_"+mesActual+"=0 THEN 1 ELSE inv_prod_cost_prom.gral_mon_id_"+mesActual+" END) END) AS idMon,"
                + "(CASE WHEN inv_prod_cost_prom.id IS NULL THEN 1 ELSE (CASE WHEN inv_prod_cost_prom.tipo_cambio_"+mesActual+"=0 THEN 1 ELSE inv_prod_cost_prom.tipo_cambio_"+mesActual+" END) END) AS tc,"
                + "0::integer AS id_pres,"
                + "''::character varying AS presentacion,"
                + "0::double precision AS igi,  "
                + "0::double precision AS gi, "
                + "0::double precision AS ca, "
                + "0::double precision AS margen_pmin,"
                + "0::double precision AS cit,"
                + "0::double precision AS pmin  "
            + "FROM inv_prod "
            + "LEFT JOIN inv_prod_cost_prom ON (inv_prod_cost_prom.inv_prod_id=inv_prod.id AND inv_prod_cost_prom.ano="+anoActual+") "
            + "LEFT JOIN inv_prod_unidades ON inv_prod_unidades.id=inv_prod.unidad_id  "
            + "WHERE inv_prod.sku='"+sku+"' AND inv_prod.empresa_id="+idEmp+" AND inv_prod.borrado_logico=FALSE "
            + "ORDER BY inv_prod.descripcion;";
        }else{
            sql_to_query = ""
            + "SELECT "
                + "id_reg,"
                + "id_prod, "
                + "codigo, "
                + "descripcion,"
                + "unidad, "
                + "no_dec, "
                + "idMon,"
                + "tc, "
                + "costo, "
                + "id_pres,"
                + "presentacion,"
                + "igi,  "
                + "gi, "
                + "ca, "
                + "margen_pmin,"
                + "(CASE WHEN sbt.costo > 0 THEN (sbt.costo + sbt.ca + (sbt.costo * (CASE WHEN sbt.igi=0 THEN 0 ELSE (sbt.igi/100)::double precision END)) + (sbt.costo * (CASE WHEN sbt.gi=0 THEN 0 ELSE (sbt.gi/100)::double precision END))) ELSE 0 END) AS cit,"
                + "(CASE WHEN sbt.costo > 0 THEN (sbt.costo + sbt.ca + (sbt.costo * (CASE WHEN sbt.igi=0 THEN 0 ELSE (sbt.igi/100)::double precision END)) + (sbt.costo * (CASE WHEN sbt.gi=0 THEN 0 ELSE (sbt.gi/100)::double precision END)))/(1 - (CASE WHEN sbt.margen_pmin=0 THEN 0 ELSE (sbt.margen_pmin/100)::double precision END)) ELSE 0 END) AS pmin  "
            + "FROM ("
                + "SELECT "
                    + "(CASE WHEN inv_prod_cost_prom.id IS NULL THEN 0 ELSE inv_prod_cost_prom.id END) AS id_reg,"
                    + "inv_prod.id AS id_prod, "
                    + "inv_prod.sku AS codigo, "
                    + "inv_prod.descripcion,"
                    + "(CASE WHEN inv_prod_unidades.titulo IS NULL THEN '' ELSE inv_prod_unidades.titulo END) AS unidad, "
                    + "(CASE WHEN inv_prod_unidades.decimales IS NULL THEN 0 ELSE inv_prod_unidades.decimales END) AS no_dec, "
                    + "(CASE WHEN inv_prod_cost_prom.id IS NULL THEN 1 ELSE (CASE WHEN inv_prod_cost_prom.gral_mon_id_"+mesActual+"=0 THEN 1 ELSE inv_prod_cost_prom.gral_mon_id_"+mesActual+" END) END) AS idMon,"
                    + "(CASE WHEN inv_prod_cost_prom.id IS NULL THEN 1 ELSE (CASE WHEN inv_prod_cost_prom.tipo_cambio_"+mesActual+"=0 THEN 1 ELSE inv_prod_cost_prom.tipo_cambio_"+mesActual+" END) END) AS tc, "
                    + "(CASE WHEN inv_prod_cost_prom.id IS NULL THEN 0 ELSE inv_prod_cost_prom.costo_ultimo_"+mesActual+" END) AS costo, "
                    + "tbl_pres.id AS id_pres,"
                    + "tbl_pres.titulo AS presentacion,"
                    + "(CASE WHEN sbt_costos.costo_imp_"+mesActual+" IS NULL THEN 0 ELSE sbt_costos.costo_imp_"+mesActual+" END ) AS igi,  "
                    + "(CASE WHEN sbt_costos.costo_dir_"+mesActual+" IS NULL THEN 0 ELSE sbt_costos.costo_dir_"+mesActual+" END ) AS gi, "
                    + "(CASE WHEN sbt_costos.costo_adic_"+mesActual+" IS NULL THEN 0 ELSE sbt_costos.costo_adic_"+mesActual+" END ) AS ca, "
                    + "(CASE WHEN sbt_costos.precio_min_"+mesActual+" IS NULL THEN 0 ELSE sbt_costos.precio_min_"+mesActual+" END ) AS margen_pmin "
                + "FROM inv_prod "
                + "LEFT JOIN inv_prod_cost_prom ON (inv_prod_cost_prom.inv_prod_id=inv_prod.id AND inv_prod_cost_prom.ano="+anoActual+") "
                + "LEFT JOIN inv_prod_unidades ON inv_prod_unidades.id=inv_prod.unidad_id "
                + "JOIN inv_prod_pres_x_prod ON inv_prod_pres_x_prod.producto_id=inv_prod.id "
                + "JOIN inv_prod_presentaciones AS tbl_pres ON tbl_pres.id=inv_prod_pres_x_prod.presentacion_id "
                + "LEFT JOIN ("
                    + "SELECT distinct ano,inv_prod_id,inv_prod_presentacion_id,costo_imp_1,costo_dir_1,precio_min_1,actualizacion_1,usr_id_actualiza_1,costo_imp_2,costo_dir_2,precio_min_2,actualizacion_2,usr_id_actualiza_2,costo_imp_3,costo_dir_3,precio_min_3,actualizacion_3,usr_id_actualiza_3,costo_imp_4,costo_dir_4,precio_min_4,actualizacion_4,usr_id_actualiza_4,costo_imp_5,costo_dir_5,precio_min_5,actualizacion_5,usr_id_actualiza_5,costo_imp_6,costo_dir_6,precio_min_6,actualizacion_6,usr_id_actualiza_6,costo_imp_7,costo_dir_7,precio_min_7,actualizacion_7,usr_id_actualiza_7,costo_imp_8,costo_dir_8,precio_min_8,actualizacion_8,usr_id_actualiza_8,costo_imp_9,costo_dir_9,precio_min_9,actualizacion_9,usr_id_actualiza_9,costo_imp_10,costo_dir_10,precio_min_10,actualizacion_10,usr_id_actualiza_10,costo_imp_11,costo_dir_11,precio_min_11,actualizacion_11,usr_id_actualiza_11,costo_imp_12,costo_dir_12,precio_min_12,actualizacion_12,usr_id_actualiza_12,gral_emp_id,costo_adic_1,costo_adic_2,costo_adic_3,costo_adic_4,costo_adic_5,costo_adic_6,costo_adic_7,costo_adic_8,costo_adic_9,costo_adic_10, costo_adic_11, costo_adic_12 FROM inv_prod_costos "
                + ") AS sbt_costos ON (sbt_costos.inv_prod_id=inv_prod.id AND sbt_costos.inv_prod_presentacion_id=tbl_pres.id AND sbt_costos.ano="+anoActual+") "
                + "WHERE inv_prod.sku='"+sku+"' AND inv_prod.empresa_id="+idEmp+" AND inv_prod.borrado_logico=FALSE "
            + ") AS sbt "
            + "ORDER BY sbt.descripcion;";
        }

        //System.out.println("datosProd: "+sql_to_query);
        
        ArrayList<HashMap<String, String>> hm = (ArrayList<HashMap<String, String>>) this.jdbcTemplate.query(
            sql_to_query,
            new Object[]{}, new RowMapper(){
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    HashMap<String, String> row = new HashMap<String, String>();
                    row.put("id_reg",String.valueOf(rs.getInt("id_reg")));
                    row.put("id_prod",String.valueOf(rs.getInt("id_prod")));
                    row.put("codigo",rs.getString("codigo"));
                    row.put("descripcion",rs.getString("descripcion"));
                    row.put("unidad",rs.getString("unidad"));
                    row.put("no_dec",String.valueOf(rs.getInt("no_dec")));
                    row.put("costo_ultimo",StringHelper.roundDouble(rs.getString("costo"),2));
                    row.put("idMon",String.valueOf(rs.getInt("idMon")));
                    row.put("tc",StringHelper.roundDouble(rs.getString("tc"),4));
                    row.put("id_pres",String.valueOf(rs.getInt("id_pres")));
                    row.put("presentacion",rs.getString("presentacion"));
                    row.put("igi",StringHelper.roundDouble(rs.getString("igi"),2));
                    row.put("gi",StringHelper.roundDouble(rs.getString("gi"),2));
                    row.put("ca",StringHelper.roundDouble(rs.getString("ca"),2));
                    row.put("margen_pmin",StringHelper.roundDouble(rs.getString("margen_pmin"),2));
                    row.put("cit",StringHelper.roundDouble(rs.getString("cit"),2));
                    row.put("pmin",StringHelper.roundDouble(rs.getString("pmin"),2));
                    return row;
                }
            }
        );
        return hm;
    }
    
    
    //Obtiene Parametros de Compras
    @Override
    public HashMap<String, String> getCom_Par(Integer idEmp, Integer idSuc) {
        String sql_to_query = "SELECT * FROM com_par WHERE gral_emp_id=? AND gral_suc_id=?;";
        //System.out.println("Validacion:"+sql_to_query);
        
        HashMap<String, String> hm = (HashMap<String, String>) this.jdbcTemplate.queryForObject(
            sql_to_query,
            new Object[]{new Integer(idEmp), new Integer (idSuc)}, new RowMapper() {
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    HashMap<String, String> row = new HashMap<String, String>();
                    row.put("inv_alm_id",String.valueOf(rs.getInt("inv_alm_id")));
                    row.put("formato_oc",String.valueOf(rs.getInt("formato_oc")));
                    row.put("captura_costo_ref",String.valueOf(rs.getBoolean("captura_costo_ref")).toLowerCase().trim());
                    row.put("mostrar_lab", String.valueOf(rs.getBoolean("mostrar_lab")));
                    row.put("texto_lab", rs.getString("texto_lab"));
                    return row;
                }
            }
        );
        return hm;
    }
    //Termina metodos de Captura de Costos
    
    
    
    
    
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
    
    @Override
    public int getInsertInvExiTmp(String tipo, String data_string) {
        int row=0;
        String insertSql = "";
        
        try{
            String param[] = data_string.split("___");
            
            
            if(tipo.equals("1")){
                //Cargar en la tabla INV_EXI_TMP
                insertSql = "INSERT INTO inv_exi_tmp (emp_id, prod_id, codigo, alm_id, exi, fecha) VALUES(?, ?, ?, ?, ?, now());";
                //System.out.println("insertSql: "+insertSql);

                // define query arguments
                Object[] params = new Object[] { new Integer(param[0]), new Integer(param[1]), new String(param[2]), new Integer(param[3]), new Double(param[4]) };

                // define SQL types of the arguments
                int[] types = new int[] { Types.SMALLINT, Types.BIGINT, Types.VARCHAR, Types.SMALLINT, Types.DOUBLE };

                // execute insert query to insert the data
                // return number of row / rows processed by the executed query
                row = this.getJdbcTemplate().update(insertSql, params, types);
            }

            if(tipo.equals("2")){
                
                insertSql = "INSERT INTO inv_lote_tmp (emp_id, prod_id, codigo, alm_id, lote_int, lote_prov, exi, fecha) VALUES(?, ?, ?, ?, ?, ?, ?, now());";
                //System.out.println("insertSql: "+insertSql);
                
                // define query arguments
                Object[] params = new Object[] { new Integer(param[0]), new Integer(param[1]), new String(param[2]), new Integer(param[3]), new String(param[4]), new String(param[5]), new Double(param[6]) };

                // define SQL types of the arguments
                int[] types = new int[] { Types.SMALLINT, Types.BIGINT, Types.VARCHAR, Types.SMALLINT, Types.VARCHAR, Types.VARCHAR, Types.DOUBLE };

                // execute insert query to insert the data
                // return number of row / rows processed by the executed query
                row = this.getJdbcTemplate().update(insertSql, params, types);
            }
            
            //System.out.println(row + " row inserted.");
        } catch (Exception e) {
            System.out.println("ERROR: "+e.getMessage());
            row=0;
        }
        
        return row;
    }

    
    
    
    @Override
    public int getDeleteFromInvExiTmp(String tipo, Integer id_emp) {
        int row=0;
        String updateSql="";
        
        try{
            if(tipo.equals("1")){
                updateSql = "DELETE FROM inv_exi_tmp WHERE emp_id=?;";
            }
            if(tipo.equals("2")){
                updateSql = "DELETE FROM inv_lote_tmp WHERE emp_id=?;";
            }
            
            //System.out.println("updateSql: "+updateSql);
            
            // define query arguments
            Object[] params = new Object[] { new Integer(id_emp)};
            
            // define SQL types of the arguments
            int[] types = new int[] { Types.SMALLINT };
            
            // execute insert query to insert the data
            // return number of row / rows processed by the executed query
            row = this.getJdbcTemplate().update(updateSql, params, types);
            
            //System.out.println(row + " row inserted.");
        } catch (Exception e) {
            System.out.println("ERROR: "+e.getMessage());
            row=0;
        }
        
        return row;
    }
    
    
    //Ejecutar procedimiento para actualizar los datos de la tabla inv_exi
    @Override
    public String getUpdateInvExi(Integer usuario_id, Integer empresa_id, Integer sucursal_id, Integer tipo) {
        String sql_to_query = "select * from inv_carga_inventario_fisico("+usuario_id+","+empresa_id+","+sucursal_id+","+tipo+");";
        
        String valor_retorno="";
        
        try{
            Map<String, Object> update = this.getJdbcTemplate().queryForMap(sql_to_query);
            valor_retorno = update.get("inv_carga_inventario_fisico").toString();
        }catch (RuntimeException ex) {
            //System.out.println(ex.getMessage());
            valor_retorno = " "+String.valueOf(ex.getMessage().replace("\n", "___"));
            //System.out.println("valor_retorno: "+valor_retorno);
        }
        
        return valor_retorno;
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

	String sql_query = ""
        + "SELECT "
            +"sbt.id,"
            +"sbt.numero_control,"
            +"sbt.rfc,"
            +"sbt.razon_social,"
            //+"sbt.direccion,"
            +"sbt.moneda_id,"
            +"gral_mon.descripcion as moneda "
        +"FROM("
            + "SELECT cxc_clie.id,"
                +"cxc_clie.numero_control,"
                +"cxc_clie.rfc, "
                +"cxc_clie.razon_social,"
                //+"cxc_clie.calle||' '||cxc_clie.numero||', '||cxc_clie.colonia||', '||gral_mun.titulo||', '||gral_edo.titulo||', '||gral_pais.titulo||' C.P. '||cxc_clie.cp as direccion, "
                +"cxc_clie.moneda as moneda_id "
            +"FROM cxc_clie "
            + "JOIN gral_pais ON gral_pais.id = cxc_clie.pais_id "
            + "JOIN gral_edo ON gral_edo.id = cxc_clie.estado_id "
            + "JOIN gral_mun ON gral_mun.id = cxc_clie.municipio_id "
            +" WHERE empresa_id ="+id_empresa+"  "
            +" AND cxc_clie.borrado_logico=false  "+where+" "
        +") AS sbt "
        +"LEFT JOIN gral_mon on gral_mon.id = sbt.moneda_id ORDER BY sbt.id;";
        //System.out.println("BuscarCliente: "+sql_query);
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
                    //row.put("direccion",rs.getString("direccion"));
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
            //+"sbt.direccion,"
            +"sbt.moneda_id,"
            +"gral_mon.descripcion as moneda "
        +"FROM("
            + "SELECT cxc_clie.id,"
                +"cxc_clie.numero_control,"
                +"cxc_clie.rfc, "
                +"cxc_clie.razon_social,"
                //+"cxc_clie.calle||' '||cxc_clie.numero||', '||cxc_clie.colonia||', '||gral_mun.titulo||', '||gral_edo.titulo||', '||gral_pais.titulo||' C.P. '||cxc_clie.cp as direccion, "
                +"cxc_clie.moneda AS moneda_id "
            +"FROM cxc_clie "
            + "JOIN gral_pais ON gral_pais.id = cxc_clie.pais_id "
            + "JOIN gral_edo ON gral_edo.id = cxc_clie.estado_id "
            + "JOIN gral_mun ON gral_mun.id = cxc_clie.municipio_id "
            +" WHERE empresa_id ="+id_empresa+"  "
            +" AND cxc_clie.borrado_logico=false  "+where+" "
            + "AND  cxc_clie.numero_control='"+no_control.toUpperCase()+"'"
        +") AS sbt "
        +"LEFT JOIN gral_mon on gral_mon.id = sbt.moneda_id ORDER BY sbt.id LIMIT 1;";

        //System.out.println("getDatosCliente: "+sql_query);

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
                    //row.put("direccion",rs.getString("direccion"));
                    row.put("moneda_id",rs.getString("moneda_id"));
                    row.put("moneda",rs.getString("moneda"));
                    return row;
                }
            }
        );
        return hm_cli;
    }
    
    
    
    
    /**********************************************************************************************
    METODOS PARA ORDENES DE PRODUCCION DE SUBENSAMBLE VERSION 2
    ESTA VERSION CONTEMPLA EL USO DE DENSIDADES PARA CONVERSIONES DE KILOS A LITROS Y VICEVERSA
    **********************************************************************************************/
    //update inv_ord_subensamble_detalle set inv_prod_unidad_id=(select inv_prod_unidades.id from inv_prod_unidades where inv_prod_unidades.titulo ilike '%KILO%' limit 1);
    //update inv_ord_subensamble_detalle set densidad=(select inv_prod.densidad from inv_prod where inv_prod.id=inv_ord_subensamble_detalle.inv_prod_id_subensamble limit 1);

    
    //Ontiene el detalle de la orden de pre-subensamble
    @Override
    public ArrayList<HashMap<String, String>> getInvOrdPreSuben2_Detalle(String id) {
        String sql_query = ""
        + "select "
            + "iop.id, "
            + "iop.cantidad, "
            + "(iop.cantidad/iop.densidad::double precision) as cantidad_l, "
            + "iop.inv_prod_presentacion_id AS presentacion_id,"
            + "iop.densidad,"
            + "inv_prod.id as prod_id,"
            + "inv_prod.sku, "
            + "inv_prod.descripcion, "
            + "inv_prod_unidades.titulo as unidad, "
            + "(case when inv_prod_unidades.decimales IS NULL then 0 else inv_prod_unidades.decimales end) AS no_dec,"
            + "(CASE WHEN inv_prod_presentaciones.titulo IS NULL THEN '' ELSE inv_prod_presentaciones.titulo END) AS presentacion "
        + "from inv_ord_subensamble_detalle AS iop "
        + "join inv_prod ON inv_prod.id=iop.inv_prod_id_subensamble "
        + "left join inv_prod_unidades on inv_prod_unidades.id=iop.inv_prod_unidad_id  "
        + "left join inv_prod_presentaciones on inv_prod_presentaciones.id=iop.inv_prod_presentacion_id "
        + "where iop.inv_ord_subensamble_id=? order by iop.id;";
        
        //System.out.println("Identificador: "+id);
        //System.out.println("getInvOrdPreSuben2_Detalle: "+sql_query);
        ArrayList<HashMap<String, String>> hm = (ArrayList<HashMap<String, String>>) this.jdbcTemplate.query(
            sql_query,
            new Object[]{new Integer(id)}, new RowMapper() {
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    HashMap<String, String> row = new HashMap<String, String>();
                    row.put("id",String.valueOf(rs.getInt("id")));
                    row.put("presentacion_id",String.valueOf(rs.getInt("presentacion_id")));
                    row.put("prod_id",String.valueOf(rs.getInt("prod_id")));
                    row.put("sku",rs.getString("sku"));
                    row.put("descripcion",rs.getString("descripcion"));
                    row.put("unidad",rs.getString("unidad"));
                    row.put("presentacion",rs.getString("presentacion"));
                    row.put("densidad",StringHelper.roundDouble(rs.getDouble("densidad"), 4));
                    row.put("cantidad",StringHelper.roundDouble(rs.getDouble("cantidad"), rs.getInt("no_dec")));
                    row.put("cantidad_l",StringHelper.roundDouble(rs.getDouble("cantidad_l"), rs.getInt("no_dec")));
                    row.put("no_dec",String.valueOf(rs.getInt("no_dec")));
                    return row;
                }
            }
        );
        return hm;
    }
    
    @Override
    public ArrayList<HashMap<String, String>> getInvOrdPreSuben2_DatosFormula(Integer id_detalle) {
        String sql_to_query = ""
        + "select "
            + "inv_prod.id, "
            + "inv_prod.sku, "
            + "inv_prod.descripcion, "
            + "inv_prod_unidades.titulo AS utitulo, "
            + "inv_prod.densidad, "
            + "(inv_prod.densidad * iosd_formula.cantidad_kg::double precision) as densidad_promedio, "
            + "iosd_formula.cantidad_kg, "
            + "inv_prod.inv_prod_presentacion_id AS id_pres_def, "
            + "(CASE WHEN inv_prod_unidades.decimales IS NULL THEN 0 ELSE inv_prod_unidades.decimales END) AS no_dec, "
            + "(CASE WHEN inv_prod_presentaciones.titulo IS NULL THEN '' ELSE inv_prod_presentaciones.titulo END) AS presentacion "
        + "from inv_ord_subensamble_detalle_formula as iosd_formula "
        + "join inv_prod on inv_prod.id=iosd_formula.inv_prod_id "
        + "join inv_prod_unidades ON inv_prod_unidades.id=inv_prod.unidad_id "
        + "left join inv_prod_presentaciones ON inv_prod_presentaciones.id=iosd_formula.inv_prod_presentacion_id "
        + "where iosd_formula.inv_ord_subensamble_detalle_id=? order by iosd_formula.id;";
        
        //System.out.println("id_detalle: "+id_detalle);
        //System.out.println(sql_to_query);
        
        ArrayList<HashMap<String, String>> hm_datos_productos = (ArrayList<HashMap<String, String>>) this.jdbcTemplate.query(
            sql_to_query,
            new Object[]{new Integer(id_detalle)}, new RowMapper(){
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    HashMap<String, String> row = new HashMap<String, String>();
                    row.put("id",String.valueOf(rs.getInt("id")));
                    row.put("sku",rs.getString("sku"));
                    row.put("descripcion",rs.getString("descripcion"));
                    row.put("utitulo",rs.getString("utitulo"));
                    row.put("presentacion",rs.getString("presentacion"));
                    row.put("densidad",StringHelper.roundDouble(rs.getDouble("densidad"),4));
                    row.put("densidad_promedio",StringHelper.roundDouble(rs.getDouble("densidad_promedio"),4));
                    row.put("cantidad",StringHelper.roundDouble(rs.getDouble("cantidad_kg"),rs.getInt("no_dec")));
                    row.put("id_pres_def_comp",String.valueOf(rs.getInt("id_pres_def")));
                    row.put("no_dec",String.valueOf(rs.getInt("no_dec")));
                    return row;
                }
            }
        );
        return hm_datos_productos;
    }
    
    
    @Override
    public ArrayList<HashMap<String, String>> getInvOrdSubensamble2_DatosFormula(String id) {
        
        String sql_to_query = ""
        + "select "
            + "sbt2.id, "
            + "sbt2.sku, "
            + "sbt2.descripcion, "
            + "sbt2.utitulo, "
            + "sbt2.presentacion,"
            + "sbt2.no_dec, "
            + "sbt2.densidad, "
            + "sbt2.densidad_promedio, "
            + "sbt2.cant_mp_kg,"
            //+ "(case when sbt2.utitulo ilike 'KILO%' then sbt2.cant_mp_kg else sbt2.cant_mp_kg/sbt2.densidad::double precision end) as cant_mp_lt "
            + "(sbt2.cant_mp_kg/sbt2.densidad::double precision) as cant_mp_lt "
        + "from("
            + "select sbt.id,sbt.sku,sbt.descripcion,sbt.utitulo,sbt.presentacion,sbt.no_dec, sbt.densidad, sbt.densidad_promedio, sum(sbt.cant_mp_kg) as cant_mp_kg "
            + "from( "
                + "SELECT  "
                    + "inv_prod.id, "
                    + "inv_prod.sku,  "
                    + "inv_prod.descripcion,  "
                    + "inv_prod_unidades.titulo AS utitulo, "
                    + "(CASE WHEN inv_prod_presentaciones.titulo IS NULL THEN '' ELSE inv_prod_presentaciones.titulo END) AS presentacion,"
                    + "(CASE WHEN inv_prod_unidades.decimales IS NULL THEN 0 ELSE inv_prod_unidades.decimales END) AS no_dec,"
                    + "iosd_formula.densidad,"
                    + "(iosd_formula.densidad * iosd_formula.cantidad_kg::double precision) as densidad_promedio,"
                    + "(iosd.cantidad * iosd_formula.cantidad_kg) as cant_mp_kg  "
                + "FROM inv_ord_subensamble_detalle as iosd "
                + "join inv_ord_subensamble_detalle_formula as iosd_formula on iosd_formula.inv_ord_subensamble_detalle_id=iosd.id "
                + "join inv_prod on inv_prod.id=iosd_formula.inv_prod_id "
                + "left join inv_prod_presentaciones on inv_prod_presentaciones.id=iosd_formula.inv_prod_presentacion_id "
                + "join inv_prod_unidades on inv_prod_unidades.id=inv_prod.unidad_id "
                + "where iosd.inv_ord_subensamble_id=? "
            + ") as sbt "
            + "group by sbt.id,sbt.sku,sbt.descripcion,sbt.utitulo,sbt.presentacion,sbt.no_dec,sbt.densidad,sbt.densidad_promedio "
        + ") as sbt2 "
        + "order by sbt2.sku;";
        
        //log.log(Level.INFO, "Ejecutando query de {0}", sql_to_query);
        //System.out.println("id: "+ id);
        //System.out.println("getInvOrdSubensamble2_DatosFormula: "+ sql_to_query);
        ArrayList<HashMap<String, String>> hm_datos_productos = (ArrayList<HashMap<String, String>>) this.jdbcTemplate.query(
            sql_to_query,
            new Object[]{new Integer(id)}, new RowMapper(){
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    HashMap<String, String> row = new HashMap<String, String>();
                    row.put("id",String.valueOf(rs.getInt("id")));
                    row.put("sku",rs.getString("sku"));
                    row.put("descripcion",rs.getString("descripcion"));
                    row.put("utitulo",rs.getString("utitulo"));
                    row.put("pres_comp",rs.getString("presentacion"));
                    row.put("no_dec",String.valueOf(rs.getInt("no_dec")));
                    row.put("cantidad",StringHelper.roundDouble(rs.getDouble("cant_mp_kg"),rs.getInt("no_dec")));
                    row.put("cant_mp_lt",StringHelper.roundDouble(rs.getDouble("cant_mp_lt"),rs.getInt("no_dec")));
                    row.put("densidad",StringHelper.roundDouble(rs.getDouble("densidad"),4));
                    row.put("densidad_promedio",StringHelper.roundDouble(rs.getDouble("densidad_promedio"),4));
                    return row;
                }
            }
        );
        return hm_datos_productos;
    }

    
    
    @Override
    public ArrayList<HashMap<String, Object>> getInvOrdenSalidaEtiqueta_PaginaGrid(String data_string, int offset, int pageSize, String orderBy, String asc) {
        
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
            new Object[]{new String(data_string),new Integer(pageSize),new Integer(offset)}, new RowMapper() {
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
    
    @Override
    public ArrayList<HashMap<String, Object>> AgentesDeVentas(Integer id_empresa, Integer id_sucursal) {
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
    
    
    
    //Obtiene  los datos de la Factura
    @Override
    public ArrayList<HashMap<String, Object>> getInvOrdenSalidaEtiqueta_Datos(Integer id_factura) {
        
        String sql_query = ""
        + "select fac_docs.id,"
            +"fac_docs.folio_pedido,"
            +"fac_docs.serie_folio,"
            +"(case when fac_docs.orden_compra is null then '' else fac_docs.orden_compra end) as orden_compra,"
            +"fac_docs.moneda_id,"
            +"to_char(fac_docs.momento_creacion,'yyyy-mm-dd') AS fecha_exp, "
            +"cxc_clie.id AS cliente_id,"
            +"cxc_clie.rfc,"
            +"cxc_clie.razon_social,"
            +"(case when inv_fac.observaciones is null then '' else inv_fac.observaciones end) as observaciones "
        +"from fac_docs "
        +"left join inv_fac ON inv_fac.fac_doc_id=fac_docs.id "
        +"left join cxc_clie ON cxc_clie.id=fac_docs.cxc_clie_id "
        +"where fac_docs.id=? ";
        
        ArrayList<HashMap<String, Object>> hm = (ArrayList<HashMap<String, Object>>) this.jdbcTemplate.query(
            sql_query,  
            new Object[]{new Integer(id_factura)}, new RowMapper() {
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    HashMap<String, Object> row = new HashMap<String, Object>();
                    row.put("id",rs.getInt("id"));
                    row.put("serie_folio",rs.getString("serie_folio"));
                    row.put("fecha_exp",rs.getString("fecha_exp"));
                    row.put("orden_compra",rs.getString("orden_compra"));
                    row.put("folio_pedido",rs.getString("folio_pedido"));
                    row.put("moneda_id",rs.getInt("moneda_id"));
                    row.put("observaciones",rs.getString("observaciones"));
                    row.put("cliente_id",rs.getInt("cliente_id"));
                    row.put("rfc",rs.getString("rfc"));
                    row.put("razon_social",rs.getString("razon_social"));
                    
                    return row;
                }
            }
        );
        return hm;
    }
    
    
    //Obtiene el listado de conceptos de la factura. El parametro seleccionado es para filtrar solo seleccionados para imprimir o todos segun se requiera.
    @Override
    public ArrayList<HashMap<String, Object>> getInvOrdenSalidaEtiqueta_DatosGrid(Integer id, boolean seleccionado) {
        String cadena_join = "";
        if(seleccionado){
            //Obtener solo los registros marcados para impresion
            cadena_join = " join inv_fac_etiqueta on (inv_fac_etiqueta.fac_doc_det_id=fac_docs_detalles.id and inv_fac_etiqueta.imprimir=true) ";
        }else{
            cadena_join = " left join inv_fac_etiqueta on inv_fac_etiqueta.fac_doc_det_id=fac_docs_detalles.id ";
        }
        
        String sql_query = ""
        + "select "
            + "fac_docs_detalles.id as iddet, "
            + "fac_docs_detalles.inv_prod_id as prod_id, "
            + "inv_prod.sku AS codigo, "
            + "inv_prod.descripcion AS titulo, "
            + "(case when inv_prod_unidades.titulo is null then '' else inv_prod_unidades.titulo end) AS unidad, "
            + "(case when inv_prod_unidades.decimales is null then 0 else inv_prod_unidades.decimales end) AS decimales, "
            + "(case when inv_prod_presentaciones.id is null then 0 else inv_prod_presentaciones.id end) AS pres_id, "
            + "(case when inv_prod_presentaciones.titulo is null then '' else inv_prod_presentaciones.titulo end) AS pres, "
            + "fac_docs_detalles.precio_unitario,"
            + "(case when inv_fac_etiqueta.cantidad is null then fac_docs_detalles.cantidad else inv_fac_etiqueta.cantidad end) as cantidad,"
            + "(case when inv_fac_etiqueta.orden_compra is null then '' else inv_fac_etiqueta.orden_compra end) as orden_compra,"
            + "(case when inv_fac_etiqueta.lote is null then '' else inv_fac_etiqueta.lote end) as lote,"
            + "(case when inv_fac_etiqueta.caducidad is null then '' else inv_fac_etiqueta.caducidad::character varying end) as caducidad,"
            + "(case when inv_fac_etiqueta.codigo2 is null then '' else inv_fac_etiqueta.codigo2 end) as codigo2,"
            + "(case when inv_fac_etiqueta.imprimir is null then false else inv_fac_etiqueta.imprimir end) as seleccionado "
        + "from fac_docs_detalles "
        + "left join inv_prod on inv_prod.id=fac_docs_detalles.inv_prod_id "
        + "left join inv_prod_unidades on inv_prod_unidades.id=fac_docs_detalles.inv_prod_unidad_id "
        + "left join inv_prod_presentaciones on inv_prod_presentaciones.id=fac_docs_detalles.inv_prod_presentacion_id "
        + cadena_join
        + "where fac_docs_detalles.fac_doc_id=? order by fac_docs_detalles.id;";
        
        //System.out.println("DatosGrid: "+sql_query);
        //System.out.println("id: "+id);
        ArrayList<HashMap<String, Object>> hm_grid = (ArrayList<HashMap<String, Object>>) this.jdbcTemplate.query(
            sql_query,  
            new Object[]{new Integer(id)}, new RowMapper() {
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    HashMap<String, Object> row = new HashMap<String, Object>();
                    row.put("iddet",rs.getString("iddet"));
                    row.put("prod_id",rs.getString("prod_id"));
                    row.put("codigo",rs.getString("codigo"));
                    row.put("titulo",rs.getString("titulo"));
                    row.put("unidad",rs.getString("unidad"));
                    row.put("pres_id",rs.getString("pres_id"));
                    row.put("pres",rs.getString("pres"));
                    row.put("precio_unitario",StringHelper.roundDouble(rs.getDouble("precio_unitario"),4) );
                    row.put("cantidad",StringHelper.roundDouble( rs.getString("cantidad"), rs.getInt("decimales") ));
                    row.put("orden_compra",rs.getString("orden_compra"));
                    row.put("lote",rs.getString("lote"));
                    row.put("caducidad",rs.getString("caducidad"));
                    row.put("codigo2",rs.getString("codigo2"));
                    row.put("seleccionado",rs.getBoolean("seleccionado"));
                    
                    return row;
                }
            }
        );
        return hm_grid;
    }
    
    
    
    
    //Metodos para aplicativo de Etiquetas de Entradas
    @Override
    public ArrayList<HashMap<String, String>> getInvEtiquetasEntrada_DatosGrid(Integer id) {
        String sql_to_query = ""
        + "SELECT "
            + "inv_oent_detalle.id AS id_detalle,"
            + "inv_oent_detalle.inv_prod_id, "
            + "inv_prod.sku, "
            + "inv_prod.descripcion, "
            + "inv_prod_unidades.titulo as unidad, "
            + "(CASE WHEN inv_prod_presentaciones.titulo IS NULL THEN '' ELSE inv_prod_presentaciones.titulo END) AS presentacion, "
            + "inv_oent_detalle.costo_unitario as costo_u, "
            + "inv_oent_detalle.cantidad, "
            + "inv_oent_detalle.cantidad_rec, "
            + "(inv_oent_detalle.costo_unitario * inv_oent_detalle.cantidad) as importe, "
            + "inv_prod_unidades.decimales as no_dec,"
            + "inv_lote.lote_int, "
            + "(CASE WHEN inv_lote.lote_prov IS NULL THEN '' ELSE inv_lote.lote_prov END) AS lote_prov "
            + "inv_lote.inicial AS cant_lote, "
            + "(CASE WHEN inv_lote.pedimento='' OR inv_lote.pedimento IS NULL THEN ' ' ELSE inv_lote.pedimento END) AS pedimento,"
            +"(CASE WHEN to_char(inv_lote.caducidad,'yyyymmdd') = '29991231' THEN ''::character varying ELSE inv_lote.caducidad::character varying END) AS caducidad "
        + "from inv_oent_detalle "
        + "left join inv_lote ON inv_lote.inv_oent_detalle_id=inv_oent_detalle.id "
        + "join inv_prod on inv_prod.id = inv_oent_detalle.inv_prod_id "
        + "left join inv_prod_unidades on inv_prod_unidades.id = inv_prod.unidad_id "
        + "left join inv_prod_presentaciones on inv_prod_presentaciones.id = inv_oent_detalle.inv_prod_presentacion_id "
        + "where inv_oent_detalle.inv_oent_id=? "
        + "order by inv_oent_detalle.inv_oent_id;";
        
        System.out.println("id= "+id);
        log.log(Level.INFO, "Ejecutando query de {0}", sql_to_query);
        ArrayList<HashMap<String, String>> hm = (ArrayList<HashMap<String, String>>) this.jdbcTemplate.query(
            sql_to_query,
            new Object[]{new Integer(id)}, new RowMapper(){
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    HashMap<String, String> row = new HashMap<String, String>();
                    row.put("id_detalle",String.valueOf(rs.getInt("id_detalle")));
                    row.put("producto_id",String.valueOf(rs.getInt("inv_prod_id")));
                    row.put("codigo",rs.getString("sku"));
                    row.put("titulo",rs.getString("descripcion"));
                    row.put("unidad",rs.getString("unidad"));
                    row.put("presentacion",rs.getString("presentacion"));
                    row.put("costo_u",StringHelper.roundDouble(rs.getString("costo_u"),2));
                    row.put("cantidad",StringHelper.roundDouble(rs.getString("cantidad"),2));
                    row.put("cant_rec",StringHelper.roundDouble(rs.getString("cantidad_rec"),2));
                    row.put("importe",StringHelper.roundDouble(rs.getString("importe"),2));
                    row.put("no_dec",rs.getString("no_dec"));
                    row.put("lote_int",rs.getString("lote_int"));
                    row.put("lote_prov",rs.getString("lote_prov"));
                    row.put("cant_lote",StringHelper.roundDouble(rs.getString("cant_lote"),2));
                    row.put("ped_lote",rs.getString("pedimento"));
                    row.put("cad_lote",rs.getString("caducidad"));
                    return row;
                }
            }
        );
        return hm;
    }


    /*
    @Override
    public ArrayList<HashMap<String, String>> getInvEtiquetasEntrada_DatosGridLotes(Integer id) {
                String sql_to_query = ""
                        + "SELECT "
                            + "inv_oent_detalle.id AS id_detalle_oent,"
                            + "inv_lote.id AS id_lote, "
                            + "inv_lote.inv_prod_id AS inv_prod_id_lote, "
                            + "inv_lote.inv_alm_id AS inv_alm_id_lote, "
                            + "inv_lote.lote_int, "
                            + "(CASE WHEN inv_lote.lote_prov IS NULL THEN '' ELSE inv_lote.lote_prov END) AS lote_prov_lote, "
                            + "inv_lote.inicial AS cantidad_lote, "
                            + "(CASE WHEN inv_lote.pedimento='' OR inv_lote.pedimento IS NULL THEN ' ' ELSE inv_lote.pedimento END) AS pedimento,"
                            +"(CASE WHEN to_char(inv_lote.caducidad,'yyyymmdd') = '29991231' THEN ''::character varying ELSE inv_lote.caducidad::character varying END) AS caducidad "
                        + "FROM inv_oent_detalle "
                        + "JOIN inv_lote ON inv_lote.inv_oent_detalle_id=inv_oent_detalle.id  "
                        + "JOIN inv_prod on inv_prod.id = inv_lote.inv_prod_id  "
                        + "WHERE inv_oent_detalle.inv_oent_id=? "
                        + "ORDER BY inv_lote.id;";

        //System.out.println(sql_to_query);
        //System.out.println("id: "+id);
        ArrayList<HashMap<String, String>> hm = (ArrayList<HashMap<String, String>>) this.jdbcTemplate.query(
            sql_to_query,
            new Object[]{ new Integer(id) }, new RowMapper(){
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    HashMap<String, String> row = new HashMap<String, String>();
                    row.put("id_detalle_oent",String.valueOf(rs.getInt("id_detalle_oent")));
                    row.put("id_lote",String.valueOf(rs.getInt("id_lote")));
                    row.put("inv_prod_id_lote",String.valueOf(rs.getInt("inv_prod_id_lote")));
                    row.put("inv_alm_id_lote",String.valueOf(rs.getInt("inv_alm_id_lote")));
                    row.put("lote_int",rs.getString("lote_int"));
                    row.put("lote_prov_lote",rs.getString("lote_prov_lote"));
                    row.put("cantidad_lote",StringHelper.roundDouble(rs.getString("cantidad_lote"),2));
                    row.put("ped_lote",rs.getString("pedimento"));
                    row.put("cad_lote",rs.getString("caducidad"));
                    return row;
                }
            }
        );
        return hm;
    }
    */
    
    
}
