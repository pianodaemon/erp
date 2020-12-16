/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.agnux.kemikal.springdaos;

/**
 * 28/08/2012
 * @author valentin.vale8490@gmail.com
 */


import com.agnux.common.helpers.StringHelper;
import com.agnux.kemikal.interfacedaos.ComInterfaceDao;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
//public class ComSpringDao  implements ComInterfaceDao {
public class ComSpringDao  implements ComInterfaceDao {
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
        String sql_to_query = "select * from com_adm_procesos('"+campos_data+"',array["+extra_data_array+"]);";
        
        //System.out.println("Ejacutando Guardar:"+sql_to_query);
        //int update = this.getJdbcTemplate().queryForInt(sql_to_query);
        //return update;
        String valor_retorno="";
        Map<String, Object> update = this.getJdbcTemplate().queryForMap(sql_to_query);
        
        valor_retorno = update.get("com_adm_procesos").toString();
        
        return valor_retorno;
    }
    
    
    @Override
    public int countAll(String data_string) {
        String sql_busqueda = "select id from gral_bus_catalogos('"+data_string+"') as foo (id integer)";
        String sql_to_query = "select count(id)::int as total from ("+sql_busqueda+") as subt";
        
        int rowCount = this.getJdbcTemplate().queryForInt(sql_to_query);
        return rowCount;
    }
    
    
    
    
    
    //obtiene valor del impuesto. retorna 0.16
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
            sql_to_query,
            new Object[]{new Integer(id_sucursal)}, new RowMapper(){
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
     
     
    
    //obtiene todos los impuestos
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
    
    
    
    
    
    
    //obtiene los almacenes de la empresa indicada
    @Override
    public ArrayList<HashMap<String, String>> getAlmacenes(Integer id_empresa) {
	String sql_query = "SELECT inv_alm.id, inv_alm.titulo "
                        + "FROM inv_alm " 
                        + "JOIN inv_suc_alm ON inv_suc_alm.almacen_id = inv_alm.id "
                        + "JOIN gral_suc ON gral_suc.id = inv_suc_alm.sucursal_id  "
                        + "WHERE empresa_id="+id_empresa+" AND inv_alm.borrado_logico=FALSE;";
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
    
    
    
    
    //obtiene el tipo de cambio actual
    @Override
    public Double getTipoCambioActual() {
        //System.out.println("FECHA ACTUAL: "+TimeHelper.getFechaActualYMD2());
        String sql_to_query = "SELECT valor AS tipo_cambio FROM erp_monedavers WHERE momento_creacion<=now() AND moneda_id=2 ORDER BY momento_creacion DESC LIMIT 1;";
        Map<String, Object> tipo_cambio = this.getJdbcTemplate().queryForMap(sql_to_query);
        Double valor_tipo_cambio = Double.parseDouble(StringHelper.roundDouble(tipo_cambio.get("tipo_cambio").toString(),4));
        
        return valor_tipo_cambio;
    }
    
    
    
    //obtiene el tipo de cambio de la fecha indicada
    @Override
    public ArrayList<HashMap<String, String>> getTipoCambioPorMoneda(Integer id_moneda, String fecha) {
        String sql_to_query = "SELECT valor "
                + "FROM erp_monedavers "
                + "WHERE to_char(momento_creacion,'yyyymmdd')::integer <= to_char('"+fecha+"'::timestamp with time zone,'yyyymmdd')::integer "
                + "AND moneda_id="+id_moneda+" ORDER BY momento_creacion DESC LIMIT 1;";
        //System.out.println("Obteniendo TC: "+sql_to_query);
        
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
    
        
    
    
    //obtiene datos del header del pedido
    @Override
    public ArrayList<HashMap<String, String>> getComOrdenCompra_Datos(Integer id_orden_compra) {
        String sql_query = ""
        + "SELECT  "
            + "com_orden_compra.id, "
            + "cxp_prov.rfc, "
            + "cxp_prov.folio AS no_proveedor, "
            + "com_orden_compra.folio,  "
            + "com_orden_compra.cancelado,  "
            + "com_orden_compra.observaciones, "
            + "cxp_prov.id as proveedor_id, "
            + "cxp_prov.razon_social, "
            + "cxp_prov.proveedortipo_id, "
            + "cxp_prov.calle||' '||cxp_prov.numero||', '||cxp_prov.colonia||', '||gral_mun.titulo||', '||gral_edo.titulo||', '||gral_pais.titulo||' C.P. '||cxp_prov.cp AS direccion, "
            + "com_orden_compra.moneda_id, "
            + "gral_mon.descripcion_abr AS moneda, "
            + "com_orden_compra.tipo_cambio, "
            + "com_orden_compra.grupo, "
            //+ "comorden_compra.cxp_prov_credias_id, "
            + "cxp_prov_credias.id  as  cxp_prov_credias_id ,"
            + "cxp_prov_credias.descripcion AS termino, "
            + "com_orden_compra.consignado_a, "
            + "com_orden_compra.tipo_embarque_id as tipo_embarque_id, "
            + "cxp_prov_tipos_embarque.titulo AS    tipo_embarque,  "
            + "com_orden_compra.status,"
            + "com_orden_compra.subtotal,"
            + "com_orden_compra.impuesto,"
            + "com_orden_compra.total,"
            + "(CASE WHEN com_orden_compra.fecha_entrega IS NULL THEN '' ELSE to_char(com_orden_compra.fecha_entrega, 'yyyy-mm-dd') END) AS fecha_entrega, "
            + "com_orden_compra.anexa_documentos,"
            + "com_orden_compra.tipo_orden_compra,"
            + "com_orden_compra.lab_destino as lab_dest "
        + "from com_orden_compra "
        + "join cxp_prov on cxp_prov.id=com_orden_compra.proveedor_id "
        + "join gral_mun on gral_mun.id=cxp_prov .municipio_id "
        + "join gral_edo on gral_edo.id=cxp_prov .estado_id "
        + "join gral_pais on  gral_pais.id=cxp_prov .pais_id "
        + "join gral_mon on gral_mon.id= com_orden_compra.moneda_id "
        + "left join cxp_prov_credias on cxp_prov_credias.id=com_orden_compra.cxp_prov_credias_id "
        + "join cxp_prov_tipos_embarque on cxp_prov_tipos_embarque.id =com_orden_compra.tipo_embarque_id "
        + "WHERE com_orden_compra.id="+id_orden_compra;
       //System.out.println("getComOrdenCompra_Datos::::"+sql_query); 
        ArrayList<HashMap<String, String>> hm = (ArrayList<HashMap<String, String>>) this.jdbcTemplate.query(
            sql_query,  
            new Object[]{}, new RowMapper() {
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    HashMap<String, String> row = new HashMap<String, String>();
                    row.put("id",String.valueOf(rs.getInt("id")));
                    row.put("rfc",rs.getString("rfc"));
                    row.put("no_proveedor",rs.getString("no_proveedor"));
                    row.put("folio",rs.getString("folio"));
                    row.put("cancelado",rs.getString("cancelado"));
                    row.put("observaciones",rs.getString("observaciones"));
                    row.put("proveedor_id",String.valueOf(rs.getInt("proveedor_id")));
                    row.put("razon_social",rs.getString("razon_social"));
                    row.put("prov_tipo_id",String.valueOf(rs.getInt("proveedortipo_id")));
                    row.put("direccion",rs.getString("direccion"));
                    row.put("moneda_id",rs.getString("moneda_id"));
                    row.put("moneda",rs.getString("moneda"));
                    row.put("tipo_cambio",StringHelper.roundDouble(rs.getDouble("tipo_cambio"),4));
                    row.put("grupo",rs.getString("grupo"));
                    row.put("cxp_prov_credias_id",rs.getString("cxp_prov_credias_id"));
                    row.put("termino",rs.getString("termino"));
                    row.put("consignado_a",rs.getString("consignado_a"));
                    row.put("tipo_embarque_id",rs.getString("tipo_embarque_id"));
                    row.put("tipo_embarque",rs.getString("tipo_embarque"));
                    row.put("status",String.valueOf(rs.getInt("status")));
                    row.put("subtotal",StringHelper.roundDouble(rs.getDouble("subtotal"),2));
                    row.put("impuesto",StringHelper.roundDouble(rs.getDouble("impuesto"),2));
                    row.put("total",StringHelper.roundDouble(rs.getDouble("total"),2));
                    row.put("fecha_entrega",rs.getString("fecha_entrega"));
                    row.put("anexar_doc",String.valueOf(rs.getBoolean("anexa_documentos")));
                    row.put("tipo_oc",String.valueOf(rs.getInt("tipo_orden_compra")));
                    row.put("lab_dest",String.valueOf(rs.getBoolean("lab_dest")));
                    return row;
                }
            }
        );
        return hm;
    } 
    
    
    
    
    
    

     
    @Override
    public ArrayList<HashMap<String, String>> getComOrdenCompra_DatosGrid(Integer id_orden_compra) {
        String sql_query = ""
                    + "SELECT com_orden_compra_detalle.id as id_detalle,"
                    + "com_orden_compra_detalle.inv_prod_id,"
                    + "inv_prod.sku AS codigo,"
                    + "inv_prod.descripcion AS titulo,"
                    + "(CASE WHEN inv_prod_unidades.titulo IS NULL THEN '' ELSE inv_prod_unidades.titulo END) as unidad,"
                    + "(CASE WHEN inv_prod_unidades.decimales IS NULL THEN 0 ELSE inv_prod_unidades.decimales END) AS decimales,"
                    + "(CASE WHEN inv_prod_presentaciones.id IS NULL THEN 0 ELSE inv_prod_presentaciones.id END) as id_presentacion,"
                    + "(CASE WHEN inv_prod_presentaciones.titulo IS NULL THEN '' ELSE inv_prod_presentaciones.titulo END) as presentacion,"
                    + "com_orden_compra_detalle.cantidad,"
                    + "com_orden_compra_detalle.precio_unitario,"
                    + "(com_orden_compra_detalle.cantidad * com_orden_compra_detalle.precio_unitario) AS importe, "
                    + "com_orden_compra_detalle.gral_imp_id,"
                    + "com_orden_compra_detalle.valor_imp,"
                    + "com_orden_compra_detalle.cant_surtido AS cant_rec,"
                    + "(CASE WHEN (com_orden_compra_detalle.cantidad::double precision - com_orden_compra_detalle.cant_surtido)<=0.0001 THEN 0 ELSE (com_orden_compra_detalle.cantidad::double precision - com_orden_compra_detalle.cant_surtido) END) AS cant_pen, "
                    + "(CASE WHEN com_orden_compra_detalle.estatus=1 THEN 'PARCIAL' WHEN com_orden_compra_detalle.estatus=2 THEN 'COMPLETO' WHEN com_orden_compra_detalle.estatus=3 THEN 'CANCELADO' ELSE '' END) AS pstatus, "
                    + "com_orden_compra_detalle.estatus, "
                    + "(CASE WHEN com_oc_req.id IS NULL THEN '' ELSE com_oc_req.folio END) as folio_req, "
                    + "(CASE WHEN com_oc_requisicion.id IS NULL THEN 0 ELSE com_oc_requisicion.com_oc_req_id END) as req_id,  "
                    + "(CASE WHEN com_oc_requisicion.id IS NULL THEN 0 ELSE com_oc_requisicion.com_oc_req_det_id END) as req_det_id "
                + "FROM com_orden_compra_detalle "
                + "LEFT JOIN inv_prod on inv_prod.id = com_orden_compra_detalle.inv_prod_id "
                + "LEFT JOIN inv_prod_unidades on inv_prod_unidades.id = inv_prod.unidad_id "
                + "LEFT JOIN inv_prod_presentaciones on inv_prod_presentaciones.id = com_orden_compra_detalle.presentacion_id "
                + "LEFT JOIN com_oc_requisicion on com_oc_requisicion.com_orden_compra_det_id=com_orden_compra_detalle.id "
                + "LEFT JOIN com_oc_req on com_oc_req.id=com_oc_requisicion.com_oc_req_id "
                + "WHERE com_orden_compra_detalle.com_orden_compra_id="+id_orden_compra+" "
                + "order by com_orden_compra_detalle.id;";
        //0=Sin Estatus, 1=Parcial(Surtido Parcial), 2=Surtido(Surtido Completo), 3=Cancelado
        
        //System.out.println("Obtiene datos grid OC: "+sql_query);
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
                    //row.put("cantidad",StringHelper.roundDouble( rs.getString("cantidad"), rs.getInt("decimales") ));
                    row.put("cantidad",StringHelper.roundDouble( rs.getString("cantidad"), 2 ));
                    row.put("precio_unitario",StringHelper.roundDouble(rs.getDouble("precio_unitario"),4) );
                    row.put("importe",StringHelper.roundDouble(rs.getDouble("importe"),4) );
                    row.put("gral_imp_id",String.valueOf(rs.getInt("gral_imp_id")));
                    row.put("valor_imp",StringHelper.roundDouble(rs.getDouble("valor_imp"),2) );
                    row.put("cant_rec",StringHelper.roundDouble(rs.getDouble("cant_rec"),2) );
                    row.put("cant_pen",StringHelper.roundDouble(rs.getDouble("cant_pen"),2) );
                    row.put("pstatus",rs.getString("pstatus"));
                    row.put("estatus",rs.getString("estatus"));
                    row.put("folio_req",rs.getString("folio_req"));
                    row.put("req_id",String.valueOf(rs.getInt("req_id")));
                    row.put("req_det_id",String.valueOf(rs.getInt("req_det_id")));
                    return row;
                }
            }
        );
        return hm_grid;
    }
    
    
    
    
    
    //Obtiene datos del header de la Requisicion
    @Override
    public ArrayList<HashMap<String, String>> getComOrdenCompra_DatosRequisicion(String folio_req, Integer id_empresa) {
        String sql_query = ""
        + "SELECT "
            + "com_oc_req.id, "
            + "com_oc_req.folio, "
            + "com_oc_req.observaciones, "
            + "to_char(com_oc_req.fecha_compromiso,'yyyy-mm-dd') AS fecha_compromiso , "
            + "com_oc_req.status, "
            + "com_oc_req.gral_empleado_id,  "
            + "com_oc_req.gral_depto_id "
        + "from com_oc_req "
        +"WHERE com_oc_req.folio=? AND com_oc_req.gral_emp_id=? AND com_oc_req.status=0 AND com_oc_req.cancelado=false;";
       //System.out.println("ESto es del header de Requisicion de orden de compra::::"+sql_query); 
        ArrayList<HashMap<String, String>> hm = (ArrayList<HashMap<String, String>>) this.jdbcTemplate.query(
            sql_query,  
            new Object[]{folio_req, new Integer(id_empresa)}, new RowMapper() {
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    HashMap<String, String> row = new HashMap<String, String>();
                    row.put("id",String.valueOf(rs.getInt("id")));
                    row.put("folio",rs.getString("folio"));
                    row.put("fecha_compromiso",rs.getString("fecha_compromiso"));
                    row.put("observaciones",rs.getString("observaciones"));
                    row.put("empleado_id",String.valueOf(rs.getInt("gral_empleado_id")));
                    row.put("depto_id",String.valueOf(rs.getInt("gral_depto_id")));
                    return row;
                }
            }
        );
        return hm;
    } 
    
    
    
    
    
    //Obtiene detalles de la Requisicion
    @Override
    public ArrayList<HashMap<String, String>> getComOrdenCompra_DetallesRequisicion(Integer id_requisicion) {
        String sql_query = ""
        +" SELECT "
            + "com_oc_req_detalle.id as id_detalle,"
            +" com_oc_req_detalle.inv_prod_id,"
            +" inv_prod.sku AS codigo,"
            +" inv_prod.descripcion AS titulo,"
            +" (CASE WHEN inv_prod_unidades.titulo IS NULL THEN '' ELSE inv_prod_unidades.titulo END) as unidad,"
            +" (CASE WHEN inv_prod_unidades.decimales IS NULL THEN 0 ELSE inv_prod_unidades.decimales END) AS decimales,"
            +" (CASE WHEN inv_prod_presentaciones.id IS NULL THEN 0 ELSE inv_prod_presentaciones.id END) as id_presentacion,"
            +" (CASE WHEN inv_prod_presentaciones.titulo IS NULL THEN 'sin_presentacion' ELSE inv_prod_presentaciones.titulo END) as presentacion,"
            +" com_oc_req_detalle.cantidad ,   "
            +" com_oc_req_detalle.status "
        +" FROM com_oc_req_detalle "
        +" LEFT JOIN inv_prod on inv_prod.id = com_oc_req_detalle.inv_prod_id "
        +" LEFT JOIN inv_prod_unidades on inv_prod_unidades.id = inv_prod.unidad_id "
        +" LEFT JOIN inv_prod_presentaciones on inv_prod_presentaciones.id = com_oc_req_detalle.presentacion_id "
        +" WHERE com_oc_req_detalle.com_oc_req_id=? order by com_oc_req_detalle.id;";

        //System.out.println("Obtiene datos grid requisiciones: "+sql_query);
        ArrayList<HashMap<String, String>> hm_grid = (ArrayList<HashMap<String, String>>) this.jdbcTemplate.query(
            sql_query,  
            new Object[]{new Integer(id_requisicion)}, new RowMapper() {
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
                    row.put("cantidad",StringHelper.roundDouble( rs.getString("cantidad"),2 ));
                    row.put("decimales",String.valueOf(rs.getInt("decimales")));
                    return row;
                }
            }
        );
        return hm_grid;
    }
    
    
    
    
    
   @Override
    public ArrayList<HashMap<String, String>> getViaEnvarque() {
        //String sql_to_query = "SELECT cxp_prov.id,cxp_prov_credias.descripcion FROM cxp_prov join cxp_prov_credias on cxp_prov_credias.id=cxp_prov.dias_credito_id WHERE cxp_prov.estatus=TRUE";
        String sql_to_query = "SELECT id,titulo as tipo_embarque  FROM cxp_prov_tipos_embarque ";
        ArrayList<HashMap<String, String>> hm = (ArrayList<HashMap<String, String>>) this.jdbcTemplate.query(
            sql_to_query,
            new Object[]{}, new RowMapper(){
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    HashMap<String, String> row = new HashMap<String, String>();
                    row.put("id",String.valueOf(rs.getInt("id"))  );
                    row.put("tipo_embarque",rs.getString("tipo_embarque"));
                    return row;
                }
            }
        );
        return hm;
    }
   
   @Override
    public ArrayList<HashMap<String, String>> getCondicionesDePago() {
        //String sql_to_query = "SELECT cxp_prov.id,cxp_prov_credias.descripcion FROM cxp_prov join cxp_prov_credias on cxp_prov_credias.id=cxp_prov.dias_credito_id WHERE cxp_prov.estatus=TRUE";
        String sql_to_query = "SELECT id,descripcion FROM cxp_prov_credias;";
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
    public ArrayList<HashMap<String, String>> getPresentacionesProducto(String sku, Integer id_empresa) {
	String sql_query = ""
        + "SELECT "
                +"inv_prod.id,"
                +"inv_prod.sku,"
                +"inv_prod.descripcion AS titulo,"
                +"(CASE WHEN inv_prod_unidades.titulo IS NULL THEN '' ELSE inv_prod_unidades.titulo END) AS unidad,"
                +"(CASE WHEN inv_prod_presentaciones.id IS NULL THEN 0 ELSE inv_prod_presentaciones.id END) AS id_presentacion,"
                +"(CASE WHEN inv_prod_presentaciones.titulo IS NULL THEN '' ELSE inv_prod_presentaciones.titulo END) AS presentacion, "
                +"(CASE WHEN inv_prod_unidades.decimales IS NULL THEN 0 ELSE inv_prod_unidades.decimales END) AS  decimales,"
                +"(CASE WHEN inv_prod_cost_prom.id IS NULL THEN 0 ELSE (CASE WHEN EXTRACT(MONTH FROM now())=1 THEN inv_prod_cost_prom.costo_ultimo_1 WHEN EXTRACT(MONTH FROM now())=2 THEN inv_prod_cost_prom.costo_ultimo_2 WHEN EXTRACT(MONTH FROM now())=3 THEN inv_prod_cost_prom.costo_ultimo_3 WHEN EXTRACT(MONTH FROM now())=4 THEN inv_prod_cost_prom.costo_ultimo_4 WHEN EXTRACT(MONTH FROM now())=5 THEN inv_prod_cost_prom.costo_ultimo_5 WHEN EXTRACT(MONTH FROM now())=6 THEN inv_prod_cost_prom.costo_ultimo_6 WHEN EXTRACT(MONTH FROM now())=7 THEN inv_prod_cost_prom.costo_ultimo_7 WHEN EXTRACT(MONTH FROM now())=8 THEN inv_prod_cost_prom.costo_ultimo_8 WHEN EXTRACT(MONTH FROM now())=9 THEN inv_prod_cost_prom.costo_ultimo_9 WHEN EXTRACT(MONTH FROM now())=10 THEN inv_prod_cost_prom.costo_ultimo_10 WHEN EXTRACT(MONTH FROM now())=11 THEN inv_prod_cost_prom.costo_ultimo_11 WHEN EXTRACT(MONTH FROM now())=12 THEN inv_prod_cost_prom.costo_ultimo_12 ELSE 0 END) END) AS costo_ultimo,"
                +"(CASE WHEN inv_prod_cost_prom.id IS NULL THEN 1 ELSE (CASE WHEN EXTRACT(MONTH FROM now())=1 THEN inv_prod_cost_prom.gral_mon_id_1 WHEN EXTRACT(MONTH FROM now())=2 THEN inv_prod_cost_prom.gral_mon_id_2 WHEN EXTRACT(MONTH FROM now())=3 THEN inv_prod_cost_prom.gral_mon_id_3 WHEN EXTRACT(MONTH FROM now())=4 THEN inv_prod_cost_prom.gral_mon_id_4 WHEN EXTRACT(MONTH FROM now())=5 THEN inv_prod_cost_prom.gral_mon_id_5 WHEN EXTRACT(MONTH FROM now())=6 THEN inv_prod_cost_prom.gral_mon_id_6 WHEN EXTRACT(MONTH FROM now())=7 THEN inv_prod_cost_prom.gral_mon_id_7 WHEN EXTRACT(MONTH FROM now())=8 THEN inv_prod_cost_prom.gral_mon_id_8 WHEN EXTRACT(MONTH FROM now())=9 THEN inv_prod_cost_prom.gral_mon_id_9 WHEN EXTRACT(MONTH FROM now())=10 THEN inv_prod_cost_prom.gral_mon_id_10 WHEN EXTRACT(MONTH FROM now())=11 THEN inv_prod_cost_prom.gral_mon_id_11 WHEN EXTRACT(MONTH FROM now())=12 THEN inv_prod_cost_prom.gral_mon_id_12 ELSE 0 END) END) AS mon_id "
        +"FROM inv_prod "
        +"LEFT JOIN inv_prod_cost_prom ON (inv_prod_cost_prom.inv_prod_id=inv_prod.id AND inv_prod_cost_prom.ano=EXTRACT(YEAR FROM now())) "
        +"LEFT JOIN inv_prod_unidades on inv_prod_unidades.id = inv_prod.unidad_id "
        +"LEFT JOIN inv_prod_pres_x_prod on inv_prod_pres_x_prod.producto_id = inv_prod.id "
        +"LEFT JOIN inv_prod_presentaciones on inv_prod_presentaciones.id = inv_prod_pres_x_prod.presentacion_id "
        +"WHERE empresa_id=? AND inv_prod.borrado_logico=false AND inv_prod.sku ILIKE '"+sku+"';";
        
        ArrayList<HashMap<String, String>> hm = (ArrayList<HashMap<String, String>>) this.jdbcTemplate.query(
            sql_query,  
            new Object[]{new Integer(id_empresa)}, new RowMapper() {
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    HashMap<String, String> row = new HashMap<String, String>();
                    row.put("id",String.valueOf(rs.getInt("id")));
                    row.put("sku",rs.getString("sku"));
                    row.put("titulo",rs.getString("titulo"));
                    row.put("unidad",rs.getString("unidad"));
                    row.put("id_presentacion",String.valueOf(rs.getInt("id_presentacion")));
                    row.put("presentacion",rs.getString("presentacion"));
                    row.put("decimales",rs.getString("decimales"));
                    row.put("cu",StringHelper.roundDouble(rs.getString("costo_ultimo"),2));
                    row.put("mon_id",rs.getString("mon_id"));
                    return row;
                }
            }
        );
        return hm;
    }
    
    
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
     
     
     
     
///este query agrealo en orden compra y autorizacion para que no deje el espacio en blanco en el grid
@Override
    public ArrayList<HashMap<String, Object>> getComOrdenCompra_PaginaGrid(String data_string, int offset, int pageSize, String orderBy, String asc) {
        String sql_busqueda = "select id from gral_bus_catalogos(?) as foo (id integer)";
                    String sql_to_query = ""
                            + "SELECT  "
                        +" com_orden_compra.id,   "
                        +" com_orden_compra.folio,   "
                        +" cxp_prov.razon_social as proveedor,  "
                        +" (CASE WHEN com_orden_compra.status=0 THEN 'ORDEN GENERADA' WHEN com_orden_compra.status=1 THEN 'AUTORIZADO' WHEN com_orden_compra.status=2 THEN 'CANCELADO' WHEN com_orden_compra.status=3 THEN 'SURTIDO PARCIAL' WHEN com_orden_compra.status=4 THEN 'SURTIDO COMPLETO' ELSE '' END ) AS estado, "
                        +" gral_mon.descripcion_abr AS denominacion,  "
                        + "to_char(com_orden_compra.momento_creacion,'dd/mm/yyyy')as momento_creacion, "
                        + "com_orden_compra.total "
                    +" FROM com_orden_compra  "
                    +" JOIN cxp_prov on cxp_prov.id = com_orden_compra.proveedor_id   "
                    +" JOIN gral_mon ON gral_mon.id=com_orden_compra.moneda_id   "
                    +" JOIN com_proceso on com_proceso.id = com_orden_compra.com_proceso_id   "
                    +" JOIN com_proceso_flujo on com_proceso_flujo.id = com_proceso.com_proceso_flujo_id "
                    +" JOIN ("+sql_busqueda+") as subt on subt.id=com_orden_compra.id "
                    + "order by "+orderBy+" "+asc+" limit ? OFFSET ?";
        
        //System.out.println("getComOrdenCompra_PaginaGrid: "+sql_to_query);
        ArrayList<HashMap<String, Object>> hm = (ArrayList<HashMap<String, Object>>) this.jdbcTemplate.query(
            sql_to_query, 
            new Object[]{data_string,new Integer(pageSize),new Integer(offset)}, new RowMapper() {
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    HashMap<String, Object> row = new HashMap<String, Object>();
                    row.put("id",rs.getInt("id"));
                    row.put("folio",rs.getString("folio"));
                    row.put("proveedor",rs.getString("proveedor"));
                    row.put("denominacion",rs.getString("denominacion"));
                    row.put("estado",rs.getString("estado"));
                    row.put("momento_creacion",rs.getString("momento_creacion"));
                    row.put("total",StringHelper.AgregaComas(StringHelper.roundDouble(String.valueOf(rs.getDouble("total")),2)));
                    
                    return row;
                }
            }
        );
        return hm;
    }
    
     
    
    
    
    //obtiene datos para el buscador de proveedores
    @Override
    public ArrayList<HashMap<String, String>> getBuscadorProveedores(String rfc, String no_proveedor, String razon_social, Integer id_empresa) {
        String where = "";
	if(!rfc.equals("")){
            where=" AND cxp_prov.rfc ILIKE '%"+rfc.toUpperCase()+"%'";
	}
        
	if(!no_proveedor.equals("")){
            where +=" AND cxp_prov.folio ILIKE '%"+no_proveedor.toUpperCase()+"%'";
	}
        
	if(!razon_social.equals("")){
            where +=" AND (cxp_prov.razon_social ilike '%"+razon_social.toUpperCase()+"%' OR cxp_prov.clave_comercial ilike '%"+razon_social.toUpperCase()+"%')";
	}
        
        String sql_to_query = ""
            + "SELECT DISTINCT  cxp_prov.id, "
                + "cxp_prov.folio AS numero_proveedor, "
                + "cxp_prov.rfc, "
                + "cxp_prov.razon_social, "
                + "cxp_prov.calle||' '||cxp_prov.numero||', '||cxp_prov.colonia||', '||gral_mun.titulo||', '||gral_edo.titulo||', '||gral_pais.titulo ||' C.P. '||cxp_prov.cp as direccion, "
                + "cxp_prov.proveedortipo_id,  "
                + "cxp_prov.descuento,  "
                + "cxp_prov.dias_credito_id as id_dias_credito,  "
                + "cxp_prov.cxp_prov_tipo_embarque_id as id_tipo_embarque,  "
                + "cxp_prov.credito_a_partir as comienzo_de_credito, "
                + "cxp_prov.limite_credito, "
                + "cxp_prov.moneda_id, "
                + "cxp_prov.impuesto AS impuesto_id,"
                + "(CASE WHEN gral_imptos.iva_1 is null THEN 0 ELSE gral_imptos.iva_1 END) AS valor_impuesto "
            + "FROM cxp_prov "
            + "JOIN gral_pais ON gral_pais.id = cxp_prov.pais_id "
            + "JOIN gral_edo ON gral_edo.id = cxp_prov.estado_id "
            + "JOIN gral_mun ON gral_mun.id = cxp_prov.municipio_id  "
            + "LEFT JOIN gral_imptos ON gral_imptos.id=cxp_prov.impuesto "
            + "WHERE empresa_id="+id_empresa+" AND cxp_prov.borrado_logico = false "+where+" "
            + "ORDER BY cxp_prov.razon_social;";
        
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
                    row.put("descuento",StringHelper.roundDouble(String.valueOf(rs.getDouble("descuento")),2));
                    row.put("limite_de_credito",StringHelper.roundDouble(String.valueOf(rs.getDouble("limite_credito")),2));
                    row.put("id_dias_credito",String.valueOf(rs.getInt("id_dias_credito")));
                    row.put("id_tipo_embarque",String.valueOf(rs.getInt("id_tipo_embarque")));
                    row.put("comienzo_de_credito",String.valueOf(rs.getInt("comienzo_de_credito")));
                    row.put("impuesto_id",String.valueOf(rs.getInt("impuesto_id")));
                    row.put("valor_impuesto",StringHelper.roundDouble(String.valueOf(rs.getDouble("valor_impuesto")),2));
                    return row;
                }
            }
        );
        return hm_datos_proveedor;  
    }
    
    
    
    //obtiene datos del Proveedor a partir del Número de Proveedor
    @Override
    public ArrayList<HashMap<String, String>> getDatosProveedorByNoProv(String numeroProveedor, Integer id_empresa) {
        
        String sql_to_query = ""
                + "SELECT DISTINCT  "
                    + "cxp_prov.id, "
                    + "cxp_prov.folio AS numero_proveedor, "
                    + "cxp_prov.rfc, "
                    + "cxp_prov.razon_social, "
                    + "cxp_prov.calle||' '||cxp_prov.numero||', '||cxp_prov.colonia||', '||gral_mun.titulo||', '||gral_edo.titulo||', '||gral_pais.titulo ||' C.P. '||cxp_prov.cp as direccion, "
                    + "cxp_prov.proveedortipo_id,  "
                    + "cxp_prov.descuento,  "
                    + "cxp_prov.dias_credito_id as id_dias_credito,  "
                    + "cxp_prov.cxp_prov_tipo_embarque_id as id_tipo_embarque,  "
                    + "cxp_prov.credito_a_partir as comienzo_de_credito, "
                    + "cxp_prov.limite_credito, "
                    + "cxp_prov.moneda_id,"
                    + "cxp_prov.impuesto AS impuesto_id,"
                    + "(CASE WHEN gral_imptos.iva_1 is null THEN 0 ELSE gral_imptos.iva_1 END) AS valor_impuesto "
                + "FROM cxp_prov "
                + "JOIN gral_pais ON gral_pais.id = cxp_prov.pais_id "
                + "JOIN gral_edo ON gral_edo.id = cxp_prov.estado_id "
                + "JOIN gral_mun ON gral_mun.id = cxp_prov.municipio_id  "
                + "LEFT JOIN gral_imptos ON gral_imptos.id=cxp_prov.impuesto "
                + "WHERE empresa_id="+id_empresa+" "
                + "AND cxp_prov.borrado_logico=false "
                + "AND cxp_prov.folio='"+numeroProveedor.toUpperCase()+"';";
        
        //log.log(Level.INFO, "Ejecutando query de {0}", sql_to_query);
        
        ArrayList<HashMap<String, String>> hm = (ArrayList<HashMap<String, String>>) this.jdbcTemplate.query(
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
                    row.put("descuento",StringHelper.roundDouble(String.valueOf(rs.getDouble("descuento")),2));
                    row.put("limite_de_credito",StringHelper.roundDouble(String.valueOf(rs.getDouble("limite_credito")),2));
                    row.put("id_dias_credito",String.valueOf(rs.getInt("id_dias_credito")));
                    row.put("id_tipo_embarque",String.valueOf(rs.getInt("id_tipo_embarque")));
                    row.put("comienzo_de_credito",String.valueOf(rs.getInt("comienzo_de_credito")));
                    row.put("impuesto_id",String.valueOf(rs.getInt("impuesto_id")));
                    row.put("valor_impuesto",StringHelper.roundDouble(String.valueOf(rs.getDouble("valor_impuesto")),2));
                    return row;
                }
            }
        );
        return hm;  
    }
    
    
    
    
    
    
    //ESTO ES PARA LOS DATOS DEL PDF DE ORDEN DE COMPRA
    @Override
    public HashMap<String, String> getDatosPDFOrdenCompra(Integer id_ordenCompra){
        HashMap<String, String> mappdf = new HashMap<String, String>();
        String sql_query = ""
                + "SELECT "
                    + "com_orden_compra.id, "
                    + " com_orden_compra.grupo, "
                    + " to_char(com_orden_compra.momento_creacion,'dd/mm/yyyy HH24:MI') as fecha, "
                    + " com_orden_compra.folio, "
                    + " com_orden_compra.consignado_a, "
                    + " cxp_prov_tipos_embarque.titulo AS tipo_embarque, "
                    + " cxp_prov.razon_social, cxp_prov.correo_electronico as correo_prov, "
                    + " cxp_prov.rfc, "
                    + " cxp_prov.calle||' #'||cxp_prov.numero AS direccion, " 
                    + " cxp_prov.colonia||', '||gral_mun.titulo||', '||gral_edo.titulo AS colonia, "
                    + "'C.P. '||cxp_prov.cp||' TEL. '||cxp_prov.telefono1||' EXT. '||cxp_prov.extension1 AS cpytel, "
                    + " cxp_prov.folio as no_provedor, "
                    + " cxp_prov.telefono1 as tel_proveedor, "
                    + " cxp_prov.vent_contacto as representante, "
                    + " cxp_prov_credias.descripcion as condiciones_pago, "
                    + " gral_mon.descripcion_abr AS moneda, "
                    + " gral_mon.descripcion AS moneda_titulo, "
                    + " gral_mon.descripcion_abr AS moneda_simbolo, "
                    + " com_orden_compra.subtotal,"
                    + " com_orden_compra.impuesto, "
                    + " com_orden_compra.total,"
                    + " tblEmpCrea.nombre_pila||' '||tblEmpCrea.apellido_paterno||' '||tblEmpCrea.apellido_materno AS user_elabora, "
                    + " tblEmpAutoriza.nombre_pila||' '||tblEmpAutoriza.apellido_paterno||' '||tblEmpAutoriza.apellido_materno AS user_autoriza, "
                    + " com_orden_compra.observaciones, "
                    + "(CASE WHEN com_orden_compra.fecha_entrega IS NULL THEN '' ELSE to_char(com_orden_compra.fecha_entrega, 'dd/mm/yyyy') END) AS fecha_entrega, "
                    + "com_orden_compra.anexa_documentos, "
                    + "(CASE WHEN com_orden_compra.status=2 THEN 'CANCELADO' ELSE '' END ) AS estado_oc "
                + " FROM com_orden_compra "
                + " JOIN cxp_prov ON cxp_prov.id=com_orden_compra.proveedor_id "
                + " JOIN gral_mun ON gral_mun.id=cxp_prov.municipio_id "
                + " JOIN gral_edo ON gral_edo.id=cxp_prov.estado_id "
                + " JOIN gral_pais ON  gral_pais.id=cxp_prov.pais_id "
                + " JOIN gral_mon ON gral_mon.id= com_orden_compra.moneda_id left "
                + " JOIN cxp_prov_credias ON cxp_prov_credias.id=com_orden_compra.cxp_prov_credias_id "
                + " JOIN cxp_prov_tipos_embarque ON cxp_prov_tipos_embarque.id=com_orden_compra.tipo_embarque_id "
                + " LEFT JOIN gral_usr AS tblUserCrea ON tblUserCrea.id=com_orden_compra.gral_usr_id_creacion  "
                + " LEFT JOIN gral_empleados AS tblEmpCrea ON tblEmpCrea.id = tblUserCrea.gral_empleados_id  "
                + " LEFT JOIN gral_usr AS tblUserAutoriza ON tblUserAutoriza.id=com_orden_compra.gral_usr_id_autoriza  "
                + " LEFT JOIN gral_empleados AS tblEmpAutoriza ON tblEmpAutoriza.id = tblUserCrea.gral_empleados_id  "
                + " WHERE com_orden_compra.id="+id_ordenCompra;
        
        //System.out.println("getDatosPDFOrdenCompra:"+sql_query);
        Map<String, Object> mapdatosquery = this.getJdbcTemplate().queryForMap(sql_query);
        mappdf.put("id_ordencompra", mapdatosquery.get("id").toString());
        mappdf.put("grupo", mapdatosquery.get("grupo").toString());
        mappdf.put("fecha", mapdatosquery.get("fecha").toString());
        mappdf.put("folio", mapdatosquery.get("folio").toString());
        mappdf.put("consignado_a", mapdatosquery.get("consignado_a").toString());
        mappdf.put("tipo_embarque", mapdatosquery.get("tipo_embarque").toString());
        mappdf.put("correo_prov", mapdatosquery.get("correo_prov").toString());
        mappdf.put("razon_social_prov", mapdatosquery.get("razon_social").toString());
        mappdf.put("rfc", mapdatosquery.get("rfc").toString());
        mappdf.put("direccion", mapdatosquery.get("direccion").toString());
        mappdf.put("no_provedor", mapdatosquery.get("no_provedor").toString());
        mappdf.put("tel_proveedor", mapdatosquery.get("tel_proveedor").toString());
        mappdf.put("representante", mapdatosquery.get("representante").toString());
        mappdf.put("condiciones_pago", mapdatosquery.get("condiciones_pago").toString());
        mappdf.put("moneda", mapdatosquery.get("moneda").toString());
        mappdf.put("moneda_simbolo", mapdatosquery.get("moneda_simbolo").toString());
        mappdf.put("cpytel", mapdatosquery.get("cpytel").toString());
        mappdf.put("colonia", mapdatosquery.get("colonia").toString());
        mappdf.put("subtotal", StringHelper.roundDouble(mapdatosquery.get("subtotal").toString(),2));
        mappdf.put("impuesto", StringHelper.roundDouble(mapdatosquery.get("impuesto").toString(),2));
        mappdf.put("total", StringHelper.roundDouble(mapdatosquery.get("total").toString(),2));
        mappdf.put("user_elabora", mapdatosquery.get("user_elabora").toString());
        mappdf.put("user_autoriza", mapdatosquery.get("user_autoriza").toString());
        mappdf.put("observaciones", mapdatosquery.get("observaciones").toString());
        mappdf.put("fecha_entrega", mapdatosquery.get("fecha_entrega").toString());
        mappdf.put("anexar_doc", String.valueOf(mapdatosquery.get("anexa_documentos")));
        mappdf.put("estado_oc", String.valueOf(mapdatosquery.get("estado_oc")));
        return mappdf;
    }
    
    
    
    //este es el Query para cargar los detalles de la compra
    @Override
    public ArrayList<HashMap<String, String>> getconceptosOrdenCompra(Integer id_ordenCompra){
    String sql_query = " SELECT inv_prod.sku as articulo, "
                            + " com_orden_compra_detalle.cantidad, "
                            + " inv_prod.descripcion, "
                            + " inv_prod_unidades.titulo as unit, "
                            + " com_orden_compra_detalle.precio_unitario, "
                            + " gral_mon.descripcion_abr AS moneda, "
                            + " gral_mon.descripcion AS moneda_titulo, "
                            + " gral_mon.descripcion_abr AS moneda_simbolo "
                        + " from com_orden_compra "
                        + " join gral_mon on gral_mon.id= com_orden_compra.moneda_id  "
                        + " left join com_orden_compra_detalle on com_orden_compra_detalle.com_orden_compra_id = com_orden_compra.id "
                        + " join inv_prod on inv_prod.id=com_orden_compra_detalle.inv_prod_id "
                        + " join inv_prod_unidades on inv_prod_unidades.id = inv_prod.unidad_id "
                        + " WHERE com_orden_compra.id="+id_ordenCompra+" "
            + "ORDER BY com_orden_compra_detalle.id;";
    
        ArrayList<HashMap<String, String>> hm = (ArrayList<HashMap<String, String>>) this.jdbcTemplate.query(
            sql_query,
            new Object[]{}, new RowMapper(){
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    HashMap<String, String> row = new HashMap<String, String>();
                    row.put("articulo",rs.getString("articulo"));
                    row.put("cantidad",StringHelper.roundDouble(rs.getDouble("cantidad"),2));
                    row.put("descripcion",rs.getString("descripcion"));
                    row.put("unit",rs.getString("unit"));
                    row.put("precio_unitario",StringHelper.roundDouble(rs.getDouble("precio_unitario"),2));
                    row.put("moneda",rs.getString("moneda"));
                    row.put("moneda_simbolo",rs.getString("moneda_simbolo"));
                    return row;
                }
            }
        );
        return hm; 
    }
    
    
    
    
    //**************************************************************************************************************
    //METODOS PARA DEVOLUCIONES DE MERCANCIAS A PROVEEDORES
    //**************************************************************************************************************
    @Override
    public ArrayList<HashMap<String, Object>> getComFacDevolucion_PaginaGrid(String data_string, int offset, int pageSize, String orderBy, String asc) {
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
        //System.out.println("data_string: "+data_string);
        //System.out.println("Busqueda GetPage: "+sql_to_query);
        ArrayList<HashMap<String, Object>> hm = (ArrayList<HashMap<String, Object>>) this.jdbcTemplate.query(
            sql_to_query, 
            new Object[]{data_string,new Integer(pageSize),new Integer(offset)}, new RowMapper() {
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
    public ArrayList<HashMap<String, String>> getComFacDevolucion_Facturas(Integer id_proveedor) {
	String sql_query = ""
                + "SELECT "
                    + "cxp_facturas.id AS id_fac_cxp, "
                    + "cxp_facturas.serie_folio AS factura, "
                    + "cxp_facturas.monto_total AS monto_factura, "
                    + "cxp_facturas.total_pagos AS pago_aplicado, "
                    + "cxp_facturas.total_notas_creditos AS nc_aplicado, "
                    + "cxp_facturas.saldo_factura, "
                    + "cxp_facturas.moneda_id, "
                    + "(CASE WHEN cxp_facturas.orden_compra IS NULL THEN '' ELSE cxp_facturas.orden_compra END) AS orden_compra, "
                    + "gral_mon.descripcion_abr AS moneda, "
                    + "to_char(cxp_facturas.fecha_factura,'dd/mm/yyyy') AS fecha_factura "
                + "FROM cxp_facturas "
                + "JOIN gral_mon ON gral_mon.id=cxp_facturas.moneda_id "
                + "WHERE cxp_facturas.cxc_prov_id="+id_proveedor+" "
                + "AND cxp_facturas.pagado=FALSE "
                + "AND cxp_facturas.cancelacion=FALSE "
                + "AND cxp_facturas.tipo_factura_proveedor=1;";
        
        ArrayList<HashMap<String, String>> hm = (ArrayList<HashMap<String, String>>) this.jdbcTemplate.query(
            sql_query,  
            new Object[]{}, new RowMapper() {
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    HashMap<String, String> row = new HashMap<String, String>();
                    row.put("id_fac",String.valueOf(rs.getInt("id_fac_cxp")));
                    row.put("factura",rs.getString("factura"));
                    row.put("monto_factura",StringHelper.roundDouble(rs.getDouble("monto_factura"), 2));
                    row.put("saldo_factura",StringHelper.roundDouble(rs.getDouble("saldo_factura"), 2));
                    row.put("pago_aplicado",StringHelper.roundDouble(rs.getDouble("pago_aplicado"), 2));
                    row.put("nc_aplicado",StringHelper.roundDouble(rs.getDouble("nc_aplicado"), 2));
                    row.put("moneda_id",String.valueOf(rs.getInt("moneda_id")));
                    row.put("moneda",rs.getString("moneda"));
                    row.put("fecha_factura",rs.getString("fecha_factura"));
                    row.put("orden_compra",rs.getString("orden_compra"));
                    return row;
                }
            }
        );
        return hm;
    }
    
    
    
    
    
    //obtiene valor del impuesto del asignado al proveedor
    @Override
    public ArrayList<HashMap<String, String>> getComFacDevolucion_Impuesto(Integer id_proveedor) {
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
    
    
    //obtener las partidas de la Factura de Compra
    @Override
    public ArrayList<HashMap<String, String>> getComFacDevolucion_Partidas(Integer id_proveedor, String factura) {
        String sql_to_query = ""
                + "SELECT "
                    + "com_fac_detalle.id AS id_detalle,"
                    + "com_fac.inv_alm_id AS id_almacen,"
                    + "com_fac_detalle.producto_id, "
                    + "inv_prod.sku, "
                    + "inv_prod.descripcion AS titulo, "
                    + "inv_prod_unidades.titulo AS unidad, "
                    + "inv_prod_presentaciones.id AS pres_id, "
                    + "inv_prod_presentaciones.titulo AS presentacion, "
                    + "com_fac_detalle.costo_unitario AS costo, "
                    + "com_fac_detalle.cantidad, "
                    + "(com_fac_detalle.costo_unitario * com_fac_detalle.cantidad) AS importe, "
                    + "com_fac_detalle.tipo_de_impuesto_sobre_partida AS id_impuesto, "
                    + "com_fac_detalle.cantidad_devolucion AS cantidad_devuelto, "
                    + "com_fac_detalle.gral_ieps_id,"
                    + "(com_fac_detalle.valor_ieps * 100 ) AS valor_ieps "
                + "FROM com_fac "
                + "JOIN com_fac_detalle ON com_fac_detalle.com_fac_id=com_fac.id "
                + "LEFT JOIN inv_prod on inv_prod.id = com_fac_detalle.producto_id  "
                + "LEFT JOIN inv_prod_unidades on inv_prod_unidades.id = inv_prod.unidad_id  "
                + "LEFT JOIN inv_prod_presentaciones on inv_prod_presentaciones.id = com_fac_detalle.presentacion_id "
                + "WHERE com_fac.proveedor_id="+id_proveedor+" AND com_fac.factura='"+factura+"';";
                                
        //log.log(Level.INFO, "Ejecutando query de {0}", sql_to_query);
        ArrayList<HashMap<String, String>> hm = (ArrayList<HashMap<String, String>>) this.jdbcTemplate.query(
            sql_to_query,
            new Object[]{}, new RowMapper(){
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    HashMap<String, String> row = new HashMap<String, String>();
                    row.put("id_detalle",String.valueOf(rs.getInt("id_detalle")));
                    row.put("id_almacen",String.valueOf(rs.getInt("id_almacen")));
                    row.put("producto_id",String.valueOf(rs.getInt("producto_id")));
                    row.put("sku",rs.getString("sku"));
                    row.put("titulo",rs.getString("titulo"));
                    row.put("unidad",rs.getString("unidad"));
                    row.put("pres_id",String.valueOf(rs.getInt("pres_id")));
                    row.put("presentacion",rs.getString("presentacion"));
                    row.put("costo_unitario",StringHelper.roundDouble(rs.getString("costo"),2));
                    row.put("cantidad_fac",StringHelper.roundDouble(rs.getString("cantidad"),2));
                    row.put("importe",StringHelper.roundDouble(rs.getString("importe"),2));
                    row.put("id_impuesto",String.valueOf(rs.getInt("id_impuesto")));
                    row.put("cantidad_devuelto",StringHelper.roundDouble(rs.getString("cantidad_devuelto"),2));
                    row.put("ieps_id",rs.getString("gral_ieps_id"));
                    row.put("valor_ieps",StringHelper.roundDouble(rs.getString("valor_ieps"),2));
                    return row;
                }
            }
        );
        return hm;  
    }
    
    
    
    
    @Override
    public ArrayList<HashMap<String, String>> getComFacDevolucion_Datos(Integer id_nota_credito) {
        String sql_to_query = " "
                    + "SELECT cxp_nota_credito.id,"
                        + "cxp_nota_credito.folio,"
                        + "cxp_nota_credito.tipo,"
                        + "cxp_nota_credito.inv_mov_tipo_id AS id_tipo_mov,"
                        + "cxp_nota_credito.inv_alm_id AS id_almacen,"
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
                        + "cxp_nota_credito.monto_ieps,"
                        + "cxp_nota_credito.impuesto,"
                        + "cxp_nota_credito.total,"
                        + "cxp_nota_credito.cancelado,"
                        + "cxp_nota_credito.factura,"
                        + "to_char(cxp_facturas.fecha_factura,'dd/mm/yyyy') AS fecha_factura,"
                        + "cxp_facturas.monto_total AS cantidad_factura,"
                        + "cxp_facturas.total_pagos,"
                        + "cxp_facturas.total_notas_creditos,"
                        + "cxp_facturas.saldo_factura, "
                        + "cxp_facturas.orden_compra, "
                        + "cxp_facturas.moneda_id AS id_moneda_fac "
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
                    row.put("id_tipo_mov",String.valueOf(rs.getInt("id_tipo_mov")));
                    row.put("id_almacen",String.valueOf(rs.getInt("id_almacen")));
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
                    row.put("id_moneda_fac",String.valueOf(rs.getInt("id_moneda_fac")));
                    row.put("orden_compra",rs.getString("orden_compra"));
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
    
    
    
    
    //obtener detalles de la devolucion
    @Override
    public ArrayList<HashMap<String, String>> getComFacDevolucion_DatosGrid(Integer id_nota_credito) {
        String sql_to_query = ""
                + "SELECT "
                    + "com_fac_detalle.id AS id_detalle, "
                    + "com_fac_detalle.producto_id, "
                    + "inv_prod.sku, "
                    + "inv_prod.descripcion AS titulo, "
                    + "inv_prod_unidades.titulo AS unidad, "
                    + "(CASE WHEN inv_prod_presentaciones.id IS NULL THEN 0 ELSE inv_prod_presentaciones.id END) AS pres_id, "
                    + "(CASE WHEN inv_prod_presentaciones.id IS NULL THEN '' ELSE inv_prod_presentaciones.titulo END) AS presentacion, "
                    + "com_fac_detalle.costo_unitario AS costo, "
                    + "com_fac_detalle.cantidad, "
                    + "(com_fac_detalle.costo_unitario * com_fac_detalle.cantidad) AS importe, "
                    + "com_fac_detalle.tipo_de_impuesto_sobre_partida AS id_impuesto, "
                    + "com_fac_detalle.cantidad_devolucion AS cantidad_devuelto, "
                    + "com_fac_detalle_dev.cant_dev AS cantidad_devolucion, "
                    + "com_fac_detalle_dev.tasa_imp, "
                    + "com_fac_detalle_dev.gral_ieps_id,"
                    + "(com_fac_detalle_dev.valor_ieps * 100) AS valor_ieps,"
                    + "(CASE WHEN com_fac_detalle_dev.gral_ieps_id>0 THEN ((com_fac_detalle.costo_unitario * com_fac_detalle_dev.cant_dev) * com_fac_detalle_dev.valor_ieps) ELSE 0 END) AS importe_ieps "
                + "FROM com_fac_detalle_dev "
                + "JOIN com_fac_detalle ON com_fac_detalle.id=com_fac_detalle_dev.id_com_fac_detalle "
                + "LEFT JOIN inv_prod on inv_prod.id = com_fac_detalle.producto_id   "
                + "LEFT JOIN inv_prod_unidades on inv_prod_unidades.id = inv_prod.unidad_id  "
                + "LEFT JOIN inv_prod_presentaciones on inv_prod_presentaciones.id = com_fac_detalle_dev.presentacion_id "
                + "WHERE com_fac_detalle_dev.id_cxp_nota_credito=?;";
        
        //log.log(Level.INFO, "Ejecutando query de {0}", sql_to_query);
        ArrayList<HashMap<String, String>> hm = (ArrayList<HashMap<String, String>>) this.jdbcTemplate.query(
            sql_to_query,
            new Object[]{new Integer(id_nota_credito)}, new RowMapper(){
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    HashMap<String, String> row = new HashMap<String, String>();
                    row.put("id_detalle",String.valueOf(rs.getInt("id_detalle")));
                    row.put("producto_id",String.valueOf(rs.getInt("producto_id")));
                    row.put("sku",rs.getString("sku"));
                    row.put("titulo",rs.getString("titulo"));
                    row.put("unidad",rs.getString("unidad"));
                    row.put("pres_id",String.valueOf(rs.getInt("pres_id")));
                    row.put("presentacion",rs.getString("presentacion"));
                    row.put("costo_unitario",StringHelper.roundDouble(rs.getString("costo"),2));
                    row.put("cantidad_fac",StringHelper.roundDouble(rs.getString("cantidad"),2));
                    row.put("importe",StringHelper.roundDouble(rs.getString("importe"),2));
                    row.put("id_impuesto",String.valueOf(rs.getInt("id_impuesto")));
                    row.put("cantidad_devuelto",StringHelper.roundDouble(rs.getString("cantidad_devuelto"),2));
                    row.put("cantidad_devolucion",StringHelper.roundDouble(rs.getString("cantidad_devolucion"),2));
                    row.put("tasa_imp",StringHelper.roundDouble(rs.getString("tasa_imp"),2));
                    row.put("id_ieps",String.valueOf(rs.getInt("gral_ieps_id")));
                    row.put("valor_ieps",StringHelper.roundDouble(rs.getString("valor_ieps"),4));
                    row.put("importe_ieps",StringHelper.roundDouble(rs.getString("importe_ieps"),4));
                    return row;
                }
            }
        );
        return hm;  
    }
    
    
    
    
    @Override
    public HashMap<String, String> getComFacDevolucion_DatosPDF(Integer id) {
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
        data.put("nota_serie_folio",map.get("nota_credito").toString());
        data.put("nota_tipo",map.get("tipo").toString());
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

    


    
    @Override
    public ArrayList<HashMap<String, String>> getComFacDevolucion_TipoMovimientoSalida(Integer id_empresa) {
	String sql_query = "SELECT id, titulo FROM  inv_mov_tipos WHERE grupo=2 AND afecta_compras=TRUE;";
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
    
    
    
    
    //TERMINA METODOS PARA DEVOLUCIONES DE MERCANCIAS A PROVEEDORES
    //**************************************************************************************************************


//metodos para La REQUISISION 


@Override
	public ArrayList<HashMap<String, Object>> getCom_requisicion_PaginaGrid(String data_string, int offset, int pageSize, String orderBy, String asc) {
	String sql_busqueda = "select id from gral_bus_catalogos(?) as foo (id integer)";
                String sql_to_query = ""
                + "SELECT  "
                    +"com_oc_req.id,   "
                    +"com_oc_req.folio,   "
                    +"(CASE WHEN com_oc_req.status=0 then 'REQUISICION GENERADA' when com_oc_req.status=1 then 'AUTORIZADO' else 'CANCELADO'end ) as estado, "
                    +"to_char(com_oc_req.momento_creacion,'dd/mm/yyyy')as momento_creacion,"
                    +"to_char(com_oc_req.fecha_compromiso,'dd/mm/yyyy')as fecha_compromiso, "
                    +"(CASE WHEN gral_empleados.id IS NULL THEN '' ELSE gral_empleados.nombre_pila ||' '||gral_empleados.apellido_paterno||' '||gral_empleados.apellido_materno END ) as solicitado_por "
                +" FROM com_oc_req "
                +"LEFT JOIN gral_empleados ON gral_empleados.id=com_oc_req.gral_empleado_id "
                +" JOIN ("+sql_busqueda+") as subt on subt.id=com_oc_req.id "
                + "order by "+orderBy+" "+asc+" limit ? OFFSET ?";
                
        	//System.out.println("datos del grid principal: "+sql_to_query);
		ArrayList<HashMap<String, Object>> hm = (ArrayList<HashMap<String, Object>>) this.jdbcTemplate.query(
		    sql_to_query, 
		    new Object[]{data_string,new Integer(pageSize),new Integer(offset)}, new RowMapper() {
		        @Override
		        public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
		            HashMap<String, Object> row = new HashMap<String, Object>();
		            row.put("id",rs.getInt("id"));
		            row.put("folio",rs.getString("folio"));
		            row.put("estado",rs.getString("estado"));
		            row.put("momento_creacion",rs.getString("momento_creacion"));
                            row.put("fecha_compromiso",rs.getString("fecha_compromiso"));
                            row.put("solicitado_por",rs.getString("solicitado_por").toUpperCase());
		            
		            return row;
		        }
		    }
		);
		return hm;
	}

        ///////////////////////////7777777777777777////////////
        @Override
	public ArrayList<HashMap<String, String>> getCom_requisicion_Datos(Integer id_requisicion) {
        String sql_query = "    "
                    + "SELECT      "
                    + " com_oc_req.id, "
                    + " com_oc_req.folio,  "
                    + " com_oc_req.cancelado,  "
                    + " com_oc_req.observaciones, "
                    + " to_char(com_oc_req.momento_creacion,'yyyy-mm-dd') AS fecha_creacion , "
                    + " to_char(com_oc_req.fecha_compromiso,'yyyy-mm-dd') AS fecha_compromiso , "
                    + " com_oc_req.status, "
                    +"(CASE WHEN gral_empleados.nombre_pila IS NULL THEN '' ELSE gral_empleados.nombre_pila END)||' '||  "
                    +"(CASE WHEN gral_empleados.apellido_paterno IS NULL THEN '' ELSE gral_empleados.apellido_paterno END)||' '||   "
                    +"(CASE WHEN gral_empleados.apellido_materno IS NULL THEN '' ELSE gral_empleados.apellido_materno END) AS nombre_usuario,"
                    + "com_oc_req.gral_empleado_id,  "
                    + "com_oc_req.gral_depto_id,  "
                    + "com_oc_req.folio_pedido, "
                    + "com_oc_req.tipo "
                    + " from com_oc_req "
                    +"JOIN gral_usr ON gral_usr.id=com_oc_req.gral_usr_id_creacion "
                    +"LEFT JOIN gral_empleados ON gral_empleados.id=gral_usr.gral_empleados_id "
                    +"WHERE com_oc_req.id= "+id_requisicion;
       //System.out.println("ESto es del header de Requisicion de orden de compra::::"+sql_query); 
        ArrayList<HashMap<String, String>> hm = (ArrayList<HashMap<String, String>>) this.jdbcTemplate.query(
            sql_query,  
            new Object[]{}, new RowMapper() {
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    HashMap<String, String> row = new HashMap<String, String>();
                    row.put("id",String.valueOf(rs.getInt("id")));
                    row.put("folio",rs.getString("folio"));
                    row.put("fecha_creacion",rs.getString("fecha_creacion"));
                    row.put("fecha_compromiso",rs.getString("fecha_compromiso"));
                    row.put("observaciones",rs.getString("observaciones"));
                    row.put("cancelado",rs.getString("cancelado"));
                    row.put("status",String.valueOf(rs.getInt("status")));
                    row.put("nombre_usuario",rs.getString("nombre_usuario"));
                    row.put("empleado_id",String.valueOf(rs.getInt("gral_empleado_id")));
                    row.put("depto_id",String.valueOf(rs.getInt("gral_depto_id")));
                    row.put("pedido",rs.getString("folio_pedido"));
                    row.put("tipo",String.valueOf(rs.getInt("tipo")));
                    return row;
                }
            }
        );
        return hm;
    } 

        
        
    ///////////////////////////////////////////////////////////////////////
    @Override
    public ArrayList<HashMap<String, String>> getCom_requisicion_DatosGrid(Integer id_requisicion) {
    String sql_query = ""
                +" SELECT com_oc_req_detalle.id as id_detalle,"
                +" com_oc_req_detalle.inv_prod_id,"
                +" inv_prod.sku AS codigo,"
                +" inv_prod.descripcion AS titulo,"
                +" (CASE WHEN inv_prod_unidades.titulo IS NULL THEN '' ELSE inv_prod_unidades.titulo END) as unidad,"
                +" (CASE WHEN inv_prod_unidades.decimales IS NULL THEN 0 ELSE inv_prod_unidades.decimales END) AS decimales,"
                +" (CASE WHEN inv_prod_presentaciones.id IS NULL THEN 0 ELSE inv_prod_presentaciones.id END) as id_presentacion,"
                +" (CASE WHEN inv_prod_presentaciones.titulo IS NULL THEN 'sin_presentacion' ELSE inv_prod_presentaciones.titulo END) as presentacion,"
                +" com_oc_req_detalle.cantidad ,   "
                +" com_oc_req_detalle.status"
                +" FROM com_oc_req_detalle "
                +" LEFT JOIN inv_prod on inv_prod.id = com_oc_req_detalle.inv_prod_id "
                +" LEFT JOIN inv_prod_unidades on inv_prod_unidades.id = inv_prod.unidad_id "
                +" LEFT JOIN inv_prod_presentaciones on inv_prod_presentaciones.id = com_oc_req_detalle.presentacion_id "
                +" WHERE com_oc_req_detalle.com_oc_req_id="+id_requisicion;

            //System.out.println("Obtiene datos grid requisiciones: "+sql_query);
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

                        row.put("status",rs.getString("status"));


                        return row;
                    }
                }
            );
        return hm_grid;
    }






    @Override
    public ArrayList<HashMap<String, Object>> getEmpleados(Integer id_empresa) {

        String sql_to_query = "select id, gral_empleados.nombre_pila ||' '||gral_empleados.apellido_paterno||' '||gral_empleados.apellido_materno as nombre, gral_depto_id  "
                + "from gral_empleados where borrado_logico=false and gral_emp_id=? order by gral_empleados.nombre_pila;";
        
        ArrayList<HashMap<String, Object>> escolaridad = (ArrayList<HashMap<String, Object>>) this.jdbcTemplate.query(
            sql_to_query,
            new Object[]{ new Integer(id_empresa)}, new RowMapper(){
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    HashMap<String, Object> row = new HashMap<String, Object>();
                    row.put("id",rs.getInt("id"));
                    row.put("nombre",rs.getString("nombre"));
                    row.put("depto_id",rs.getInt("gral_depto_id"));
                    return row;
                }
            }
        );
        return escolaridad;
    }

    
    //Obtiene todos los departamentos de la empresa
    @Override
    public ArrayList<HashMap<String, Object>> getDepartamentos(Integer id_empresa) {
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




//////////////////////////////////////////////////////

        //Metodos para convertir las Requisiciones en ORden de Compra.
        @Override
	public ArrayList<HashMap<String, Object>> getCom_oc_req_PaginaGrid(String data_string, int offset, int pageSize, String orderBy, String asc) {
	String sql_busqueda = "select id from gral_bus_catalogos(?) as foo (id integer)";
                    String sql_to_query = "SELECT  "
                    +" com_orden_compra.id,   "
                    +" com_orden_compra.folio,   "
                    +" cxp_prov.razon_social as proveedor,  "
                    +" (CASE WHEN com_orden_compra.status=0 then 'ORDEN GENERADA' " 
	            +" when com_orden_compra.status=1 then 'AUTORIZADO' "
                    +" else 'CANCELADO'end ) as estado, "
                    +" (CASE WHEN com_orden_compra.moneda_id=1 THEN 'M.N.'   "
                    +" ELSE gral_mon.descripcion_abr END) as denominacion,  "
                    + "to_char(com_orden_compra.momento_creacion,'yyyy/mm/dd')as momento_creacion, "
                    + "com_orden_compra.total "
                    +" FROM com_orden_compra  "
                    +" JOIN ("+sql_busqueda+") as subt on subt.id=com_orden_compra.id "
                    +" JOIN cxp_prov on cxp_prov.id = com_orden_compra.proveedor_id   "
                    +" JOIN gral_mon ON gral_mon.id=com_orden_compra.moneda_id  "
                    +" JOIN com_proceso on com_proceso.id = com_orden_compra.com_proceso_id   "
                    +" JOIN com_proceso_flujo on com_proceso_flujo.id = com_proceso.com_proceso_flujo_id  where com_orden_compra.tipo_orden_compra = 1"
                    + "order by "+orderBy+" "+asc+" limit ? OFFSET ?";
                    
		//System.out.println("datos del grid principal: "+sql_to_query);
		ArrayList<HashMap<String, Object>> hm = (ArrayList<HashMap<String, Object>>) this.jdbcTemplate.query(
		    sql_to_query, 
		    new Object[]{data_string,new Integer(pageSize),new Integer(offset)}, new RowMapper() {
		        @Override
		        public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
		            HashMap<String, Object> row = new HashMap<String, Object>();
		            row.put("id",rs.getInt("id"));
		            row.put("folio",rs.getString("folio"));
		            row.put("proveedor",rs.getString("proveedor"));
		            row.put("denominacion",rs.getString("denominacion"));
		            row.put("estado",rs.getString("estado"));
		            row.put("momento_creacion",rs.getString("momento_creacion"));
		            row.put("total",StringHelper.roundDouble(String.valueOf(rs.getDouble("total")),2));
		            
		            return row;
		        }
		    }
		);
		return hm;
	}
    
    
	@Override
	public ArrayList<HashMap<String, String>> getCom_oc_req_Datos(Integer id_com_oc_req) {
	String sql_query = ""
                    + "SELECT  "
                    + "com_orden_compra.id, "
                    + "cxp_prov.rfc, "
                    + "com_orden_compra.folio,  "
                    + "com_orden_compra.cancelado,  "
                    + "com_orden_compra.observaciones, "
                    + "cxp_prov.id as proveedor_id, "
                    + "cxp_prov.razon_social, "
                    + "cxp_prov.calle||' '||cxp_prov.numero||', '||cxp_prov.colonia||', '||gral_mun.titulo||', '||gral_edo.titulo||', '||gral_pais.titulo||' C.P. '||cxp_prov.cp AS direccion, "
                    + "com_orden_compra.moneda_id, "
                    + "gral_mon.descripcion_abr AS moneda, "
                    + "com_orden_compra.tipo_cambio, "
                    + "com_orden_compra.grupo, "
		    + "cxp_prov_credias.id  as  cxp_prov_credias_id ,"
                    + "cxp_prov_credias.descripcion AS termino, "
	            + "com_orden_compra.consignado_a, "
                    + "com_orden_compra.tipo_embarque_id as tipo_embarque_id, "
                    + "cxp_prov_tipos_embarque.titulo AS    tipo_embarque,  "
                    + "com_orden_compra.status  "
		    + "from com_orden_compra "
                    + "join cxp_prov on cxp_prov.id=com_orden_compra.proveedor_id "
                    + "join gral_mun on gral_mun.id=cxp_prov .municipio_id "
                    + "join gral_edo on gral_edo.id=cxp_prov .estado_id "
                    + "join gral_pais on  gral_pais.id=cxp_prov .pais_id "
                    + "join gral_mon on gral_mon.id= com_orden_compra.moneda_id "
                    + "left join cxp_prov_credias on cxp_prov_credias.id=com_orden_compra.cxp_prov_credias_id "
                    + "join cxp_prov_tipos_embarque on cxp_prov_tipos_embarque.id =com_orden_compra.tipo_embarque_id "
                    + "WHERE com_orden_compra.id="+id_com_oc_req;
       
		//System.out.println("ESto es header de com_oc_req cuandoedita::::"+sql_query); 
		ArrayList<HashMap<String, String>> hm = (ArrayList<HashMap<String, String>>) this.jdbcTemplate.query(
		    sql_query,  
		    new Object[]{}, new RowMapper() {
		        @Override
		        public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
		            HashMap<String, String> row = new HashMap<String, String>();
		            row.put("id",String.valueOf(rs.getInt("id")));
		            row.put("rfc",rs.getString("rfc"));
		            row.put("folio",rs.getString("folio"));
		            row.put("cancelado",rs.getString("cancelado"));
		            row.put("observaciones",rs.getString("observaciones"));
		            row.put("proveedor_id",String.valueOf(rs.getInt("proveedor_id")));
		            row.put("razon_social",rs.getString("razon_social"));
		            row.put("direccion",rs.getString("direccion"));
		            row.put("moneda_id",rs.getString("moneda_id"));
		            row.put("moneda",rs.getString("moneda"));
		            row.put("tipo_cambio",StringHelper.roundDouble(rs.getDouble("tipo_cambio"),4));
		            row.put("grupo",rs.getString("grupo"));
		            row.put("cxp_prov_credias_id",rs.getString("cxp_prov_credias_id"));
		            row.put("termino",rs.getString("termino"));
		            row.put("consignado_a",rs.getString("consignado_a"));
		            row.put("tipo_embarque_id",rs.getString("tipo_embarque_id"));
		            row.put("tipo_embarque",rs.getString("tipo_embarque"));
		            row.put("status",String.valueOf(rs.getInt("status")));
		            return row;
			    }
		     }
		);
        	return hm;
	} 
    
        
    ////
    @Override
    public ArrayList<HashMap<String, String>> getCom_oc_req_Grid() {
    String sql_query = "SELECT  "
            +"  com_oc_req_detalle.inv_prod_id, "
            +"  0::integer presentacion_id, "
            +"  ''::character varying presentacion, "
            +"  sum(com_oc_req_detalle.cantidad)as cantidad, "
            +"  inv_prod.sku AS codigo, "
            +"  inv_prod.descripcion , "
            +"  (CASE WHEN inv_prod_unidades.titulo IS NULL THEN '' ELSE inv_prod_unidades.titulo END) as unidad, "
            +"  (CASE WHEN inv_prod_unidades.decimales IS NULL THEN 0 ELSE inv_prod_unidades.decimales END) AS decimales "
            +"  FROM com_oc_req_detalle  "
            +"  JOIN com_oc_req on  com_oc_req.id= com_oc_req_detalle.com_oc_req_id "
            +"  LEFT JOIN inv_prod on inv_prod.id = com_oc_req_detalle.inv_prod_id  "
            +"  LEFT JOIN inv_prod_unidades on inv_prod_unidades.id = inv_prod.unidad_id  "
            +"  LEFT JOIN inv_prod_presentaciones on inv_prod_presentaciones.id = com_oc_req_detalle.presentacion_id "
            +"  where com_oc_req_detalle.status=false and com_oc_req.cancelado =false and com_oc_req.status=0  "
            +"  GROUP BY com_oc_req_detalle.inv_prod_id,inv_prod.sku,inv_prod.descripcion,inv_prod_unidades.titulo,inv_prod_unidades.decimales";
                    
      // System.out.println("ESto es el grid de requisiciones a autorizar a orden de compra<:::"+sql_query); 
        ArrayList<HashMap<String, String>> hm = (ArrayList<HashMap<String, String>>) this.jdbcTemplate.query(
            sql_query,  
            new Object[]{}, new RowMapper() {
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    HashMap<String, String> row = new HashMap<String, String>();
                    row.put("inv_prod_id",String.valueOf(rs.getInt("inv_prod_id")));
                    row.put("presentacion_id",String.valueOf(rs.getInt("presentacion_id")));
                    row.put("presentacion",rs.getString("presentacion"));
                    row.put("cantidad",rs.getString("cantidad"));
                    row.put("codigo",rs.getString("codigo"));
                    row.put("titulo",rs.getString("descripcion"));
                    row.put("unidad",rs.getString("unidad"));
                    row.put("decimales",rs.getString("decimales"));
                    return row;
                }
            }
        );
        return hm;
    } 


	@Override
    public ArrayList<HashMap<String, String>> getCom_oc_req_DatosGrid( Integer id_com_oc_req) {
    
        String sql_query = ""
                    + "SELECT com_orden_compra_detalle.id as id_detalle,"
                    + "com_orden_compra_detalle.inv_prod_id,"
                    + "inv_prod.sku AS codigo,"
                    + "inv_prod.descripcion AS titulo,"
                    + "(CASE WHEN inv_prod_unidades.titulo IS NULL THEN '' ELSE inv_prod_unidades.titulo END) as unidad,"
                    + "(CASE WHEN inv_prod_unidades.decimales IS NULL THEN 0 ELSE inv_prod_unidades.decimales END) AS decimales,"
                    + "(CASE WHEN inv_prod_presentaciones.id IS NULL THEN 0 ELSE inv_prod_presentaciones.id END) as id_presentacion,"
                    + "(CASE WHEN inv_prod_presentaciones.titulo IS NULL THEN '' ELSE inv_prod_presentaciones.titulo END) as presentacion,"
                    + "com_orden_compra_detalle.cantidad,"
                    + "com_orden_compra_detalle.precio_unitario,"
                    + "(com_orden_compra_detalle.cantidad * com_orden_compra_detalle.precio_unitario) AS importe, "
                    + "com_orden_compra_detalle.gral_imp_id,"
                    + "com_orden_compra_detalle.valor_imp "
                + "FROM com_orden_compra_detalle "
                + "LEFT JOIN inv_prod on inv_prod.id = com_orden_compra_detalle.inv_prod_id "
                + "LEFT JOIN inv_prod_unidades on inv_prod_unidades.id = inv_prod.unidad_id "
                + "LEFT JOIN inv_prod_presentaciones on inv_prod_presentaciones.id = com_orden_compra_detalle.presentacion_id "
                + "WHERE com_orden_compra_detalle.com_orden_compra_id="+id_com_oc_req;
        
        //System.out.println("Obtiene datos grid requisiciones cuando edita: "+sql_query);
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
                    //row.put("cantidad",StringHelper.roundDouble( rs.getString("cantidad"), rs.getInt("decimales") ));
                    row.put("cantidad",StringHelper.roundDouble( rs.getString("cantidad"), 2 ));
                    row.put("precio_unitario",StringHelper.roundDouble(rs.getDouble("precio_unitario"),4) );
                    row.put("importe",StringHelper.roundDouble(rs.getDouble("importe"),2) );
                    
                    row.put("gral_imp_id",String.valueOf(rs.getInt("gral_imp_id")));
                    row.put("valor_imp",StringHelper.roundDouble(rs.getDouble("valor_imp"),2) );
                    return row;
                }
            }
        );
        return hm_grid;
    }
    


     //Estadistico de Compras
    //************************************************************************************++
    @Override
    public ArrayList<HashMap<String, String>> getEstadisticoCompras(Integer tipo_reporte, String proveedor, String producto, String fecha_inicial, String fecha_final, Integer id_empresa) {
         String sql_to_query = "select * from repestadisticocompras("+tipo_reporte+",'"+proveedor+"','"+producto+"','"+fecha_inicial+"','"+fecha_final+"',"+id_empresa+") as foo( "
                                + " folio character varying, "        
                                + " razon_social character varying, "
                                + " codigo character varying, "
                                + " producto character varying, "
                                + " factura character varying , "
                                + " unidad character varying , "
                                + " cantidad double precision, "
                                + " precio_unitario double precision, "
                                + " moneda text, "
                                + " tipo_cambio double precision, " 
                                + " total_pesos double precision,  fecha_factura text); ";

       //System.out.println("getEstadisticoCompras:"+ sql_to_query);
          
        ArrayList<HashMap<String, String>> hm = (ArrayList<HashMap<String, String>>) this.jdbcTemplate.query(
                sql_to_query, 
            new Object[]{}, new RowMapper() {
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    HashMap<String, String> row = new HashMap<String, String>();
                   
                    row.put("folio",rs.getString("folio"));
                    row.put("razon_social",rs.getString("razon_social"));
                    row.put("codigo",rs.getString("codigo"));
                    row.put("producto",rs.getString("producto"));
                    row.put("factura",rs.getString("factura"));
                    row.put("unidad",rs.getString("unidad"));
                    row.put("cantidad",rs.getString("cantidad"));
                    row.put("precio_unitario",rs.getString("precio_unitario"));
                    row.put("moneda",rs.getString("moneda"));
                    row.put("tipo_cambio",rs.getString("tipo_cambio"));
                    row.put("total_pesos",rs.getString("total_pesos"));
                    row.put("fecha_factura",rs.getString("fecha_factura"));
                    
                    return row;
                }
            }
        );
        return hm; 
    }
     // TERMINA EL REPORTE DE ESTADISTICO DE COMPRAS
    //************************************************************************************************************+++++

    @Override
    public HashMap<String, String> getCom_Requisicion_DatosPDF(Integer id_requisicion) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    
    //Metodos para Reporte de Dias de Entrag de OC
    @Override
    public ArrayList<HashMap<String, String>> getOC_DiasEntrega(Integer tipo_reporte, String proveedor, String f_inicial, String f_final, Integer id_empresa) {
        String cadena_where="";
        
        if(tipo_reporte==1){
            cadena_where+=" AND cxp_prov.razon_social ILIKE '%"+proveedor+"%'";
        }
        
        
        String sql_query = ""
        + "SELECT "
            + "com_fac.orden_de_compra AS oc,"
            + "inv_prod.sku AS codigo,"
            + "inv_prod.descripcion,"
            + "com_fac_detalle.cantidad,"
            + "cxp_prov.razon_social AS proveedor,"
            + "to_char(com_orden_compra.momento_creacion,'dd/mm/yyyy') AS fecha_oc,"
            + "to_char(com_fac.momento_creacion,'dd/mm/yyyy') AS fecha_recepcion,"
            + "(com_fac.momento_creacion::date - com_orden_compra.momento_creacion::date) AS dias_promedio "
        + "FROM com_fac  "
        + "JOIN com_fac_detalle ON com_fac_detalle.com_fac_id=com_fac.id "
        + "JOIN inv_prod ON inv_prod.id=com_fac_detalle.producto_id "
        + "JOIN cxp_prov ON cxp_prov.id=com_fac.proveedor_id "
        + "JOIN com_orden_compra ON upper(com_orden_compra.folio)=upper(com_fac.orden_de_compra) "
        + "WHERE com_fac.empresa_id=? "
        + "AND com_fac.proveedor_id=com_orden_compra.proveedor_id "
        + "AND com_fac.cancelacion=FALSE "+cadena_where+" "
        + "AND (to_char(com_fac.momento_creacion,'yyyymmdd') BETWEEN  to_char('"+f_inicial+"'::timestamp with time zone,'yyyymmdd') AND to_char('"+f_final+"'::timestamp with time zone,'yyyymmdd')) "
        + "ORDER BY com_fac.momento_creacion;";
        
        //System.out.println("sql_query: "+sql_query);
        ArrayList<HashMap<String, String>> hm = (ArrayList<HashMap<String, String>>) this.jdbcTemplate.query(
            sql_query,  
            new Object[]{new Integer (id_empresa)}, new RowMapper() {
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    HashMap<String, String> row = new HashMap<String, String>();
                    row.put("oc",rs.getString("oc"));
                    row.put("codigo",rs.getString("codigo"));
                    row.put("descripcion",rs.getString("descripcion"));
                    row.put("cantidad",StringHelper.roundDouble(rs.getDouble("cantidad"), 2));
                    row.put("proveedor",rs.getString("proveedor"));
                    row.put("fecha_oc",rs.getString("fecha_oc"));
                    row.put("fecha_recepcion",rs.getString("fecha_recepcion"));
                    row.put("dias_promedio",rs.getString("dias_promedio"));
                    return row;
                }
            }
        );
        return hm;
    }
    
    
    //Obtiene los parametros de compras
    @Override
    public HashMap<String, String> getCom_Parametros(Integer id_emp, Integer id_suc) {
        HashMap<String, String> mapDatos = new HashMap<String, String>();
        String sql_query = "SELECT * FROM com_par WHERE gral_emp_id="+id_emp+" AND gral_suc_id="+id_suc+";";
        
        Map<String, Object> map = this.getJdbcTemplate().queryForMap(sql_query);
        mapDatos.put("gral_suc_id", String.valueOf(map.get("gral_suc_id")));
        mapDatos.put("inv_alm_id", String.valueOf(map.get("inv_alm_id")));
        mapDatos.put("formato_oc", String.valueOf(map.get("formato_oc")));
        mapDatos.put("mostrar_lab", String.valueOf(map.get("mostrar_lab")));
        mapDatos.put("texto_lab", String.valueOf(map.get("texto_lab")));
        
        return mapDatos;
    }
    
    
    
    //Metodos para Reporte de BackOrder
    @Override
    public ArrayList<HashMap<String, String>> getCom_DatosRepBackOrder(Integer tipo, String oc, String codigo_producto, String descripcion, String proveedor, String finicial, String ffinal, Integer id_empresa) {
        String cadena_where="";
        String order_by="";
        
        //Orden de Compra
        if(tipo==1){
            order_by += " ORDER BY com_orden_compra.id, cxp_prov.razon_social ";
        }
        
        //Producto
        if(tipo==2){
            order_by += " ORDER BY inv_prod.descripcion, com_orden_compra.id ";
        }
        
        //Proveedor
        if(tipo==3){
            order_by += " ORDER BY cxp_prov.razon_social, com_orden_compra.id ";
        }
        
        if(!oc.equals("")){
            cadena_where += " AND com_orden_compra.folio ILIKE '%"+ oc +"%'";
        }
        
        if(!codigo_producto.equals("")){
            cadena_where += " AND inv_prod.sku ILIKE '%"+ codigo_producto +"%'";
        }
        
        if(!descripcion.equals("")){
            cadena_where += " AND inv_prod.descripcion ILIKE '%"+ descripcion +"%'";
        }
        
        if(!proveedor.equals("")){
            cadena_where += " AND cxp_prov.razon_social ILIKE '%"+ proveedor +"%'";
        }
        
        
        String sql_query = ""
        + "SELECT "
            + "com_orden_compra.folio AS no_oc,"
            + "to_char(com_orden_compra.momento_creacion,'dd/mm/yyyy') AS fecha_oc,"
            + "cxp_prov.razon_social AS proveedor,"
            + "inv_prod.sku AS codigo,"
            + "inv_prod.descripcion,"
            + "(CASE WHEN inv_prod_unidades.titulo IS NULL THEN '' ELSE inv_prod_unidades.titulo END) as unidad,"
            + "(CASE WHEN inv_prod_presentaciones.titulo IS NULL THEN '' ELSE inv_prod_presentaciones.titulo END) as presentacion,"
            + "com_orden_compra_detalle.cantidad,"
            + "com_orden_compra_detalle.precio_unitario,"
            + "(com_orden_compra_detalle.cantidad * com_orden_compra_detalle.precio_unitario) AS importe, "
            + "com_orden_compra_detalle.cant_surtido AS cant_rec,"
            + "(CASE WHEN (com_orden_compra_detalle.cantidad::double precision - com_orden_compra_detalle.cant_surtido)<=0.0001 THEN 0 ELSE (com_orden_compra_detalle.cantidad::double precision - com_orden_compra_detalle.cant_surtido) END) AS cant_pen, "
            + "(CASE WHEN com_orden_compra_detalle.estatus=1 THEN 'PARCIAL' WHEN com_orden_compra_detalle.estatus=2 THEN 'COMPLETO' WHEN com_orden_compra_detalle.estatus=3 THEN 'CANCELADO' ELSE '' END) AS pstatus "
        + "FROM com_orden_compra "
        + "JOIN com_orden_compra_detalle ON com_orden_compra_detalle.com_orden_compra_id=com_orden_compra.id "
        + "LEFT JOIN inv_prod on inv_prod.id = com_orden_compra_detalle.inv_prod_id  "
        + "LEFT JOIN inv_prod_unidades on inv_prod_unidades.id = inv_prod.unidad_id  "
        + "LEFT JOIN inv_prod_presentaciones on inv_prod_presentaciones.id = com_orden_compra_detalle.presentacion_id  "
        + "JOIN cxp_prov ON cxp_prov.id=com_orden_compra.proveedor_id "
        + "WHERE com_orden_compra.gral_emp_id=? "
        + "AND com_orden_compra_detalle.surtir=TRUE "
        + "AND com_orden_compra_detalle.estatus IN (0,1) "
        + "AND (to_char(com_orden_compra.momento_creacion,'yyyymmdd') BETWEEN  to_char('"+finicial+"'::timestamp with time zone,'yyyymmdd') AND to_char('"+ffinal+"'::timestamp with time zone,'yyyymmdd')) "
        + ""+cadena_where+" "
        + ""+order_by+";";
        
        //System.out.println("sql_query: "+sql_query);
        ArrayList<HashMap<String, String>> hm = (ArrayList<HashMap<String, String>>) this.jdbcTemplate.query(
            sql_query,  
            new Object[]{new Integer (id_empresa)}, new RowMapper() {
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    HashMap<String, String> row = new HashMap<String, String>();
                    row.put("no_oc",rs.getString("no_oc"));
                    row.put("fecha_oc",rs.getString("fecha_oc"));
                    row.put("proveedor",rs.getString("proveedor"));
                    row.put("codigo",rs.getString("codigo"));
                    row.put("descripcion",rs.getString("descripcion"));
                    row.put("unidad",rs.getString("unidad"));
                    row.put("presentacion",rs.getString("presentacion"));
                    row.put("cantidad",StringHelper.roundDouble(rs.getDouble("cantidad"), 2));
                    row.put("cant_rec",StringHelper.roundDouble(rs.getDouble("cant_rec"), 2));
                    row.put("cant_pen",StringHelper.roundDouble(rs.getDouble("cant_pen"), 2));
                    row.put("precio_unitario",StringHelper.roundDouble(rs.getDouble("precio_unitario"), 4));
                    row.put("importe",StringHelper.roundDouble(rs.getDouble("importe"), 4));
                    row.put("pstatus",rs.getString("pstatus"));
                    return row;
                }
            }
        );
        return hm;
    }
    
    
}
