/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.agnux.kemikal.springdaos;

//import org.springframework
import com.agnux.common.helpers.StringHelper;
import java.util.Map;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import com.agnux.kemikal.interfacedaos.CotizacionesInterfaceDao;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;

/**
 *
 * @author pianodaemon
 */
public class CotizacionesSpringDao implements CotizacionesInterfaceDao {
    
    private JdbcTemplate jdbcTemplate;
    
    public JdbcTemplate getJdbcTemplate() {
        return jdbcTemplate;
    }
    
    public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }
    
    //---Aqui van variables para datos  de la empresa para generar pdf
    private String emp_razonSocial;
    private String emp_rfc;
    private String emp_calle;
    private String emp_numero;
    private String emp_colonia;
    private String emp_municipio;
    private String emp_entidad;
    private String emp_pais;
    private String emp_cp;
    
    
    //variables para datos de la cotizacion
    private String folio;
    private String cliente_id;
    private String observaciones;
    private String subtotal;
    private String impuesto;
    private String total;
    private String fecha_cotizacion;
    private String moneda;
    private String moneda_id;
    //lista de productos de la cotizacion
    private ArrayList<LinkedHashMap<String,String>> listaConceptos = new ArrayList<LinkedHashMap<String,String>>();
    
    
    
    //variables para datos del cliente
    private String client_rfc;
    private String client_razon_social;
    private String client_calle;
    private String client_numero;
    private String client_colonia;
    private String client_localidad;
    private String client_entidad;
    private String client_pais;
    private String client_cp;
    private String client_telefono;
    private String client_contacto;




    
    
    
    
    //geter y seter para variables de datos de la empresa
    @Override
    public String getEmp_Pais() {
        return emp_pais;
    }
    
    public void setEmp_Pais(String pais) {
        this.emp_pais = pais;
    }


    @Override
    public String getEmp_Calle() {
        return emp_calle;
    }
    
    public void setEmp_Calle(String calle) {
        this.emp_calle = calle;
    }
    
    @Override
    public String getEmp_Colonia() {
        return emp_colonia;
    }
    
    public void setEmp_Colonia(String colonia) {
        this.emp_colonia = colonia;
    }

    @Override
    public String getEmp_Cp() {
        return emp_cp;
    }
    
    public void setEmp_Cp(String cp) {
        this.emp_cp = cp;
    }
    
    @Override
    public String getEmp_Entidad() {
        return emp_entidad;
    }
    
    public void setEmp_Entidad(String entidad) {
        this.emp_entidad = entidad;
    }
    
    @Override
    public String getEmp_Municipio() {
        return emp_municipio;
    }
    
    public void setEmp_Municipio(String municipio) {
        this.emp_municipio = municipio;
    }
    
    @Override
    public String getEmp_Numero() {
        return emp_numero;
    }
    
    public void setEmp_Numero(String numero) {
        this.emp_numero = numero;
    }
    
    @Override
    public String getEmp_RazonSocial() {
        return emp_razonSocial;
    }
    
    public void setEmp_RazonSocial(String razonSocial) {
        this.emp_razonSocial = razonSocial;
    }
    
    @Override
    public String getEmp_Rfc() {
        return emp_rfc;
    }
    
    public void setEmp_Rfc(String rfc) {
        this.emp_rfc = rfc;
    }
    //-----termina geter y seter para variables de datos de la empresa
    
    
    
    //aqui comienza geter y serter para variables de la cotizacion
    @Override
    public String getFolio() {
        return folio;
    }
    
    public void setFolio(String folio) {
        this.folio = folio;
    }
    
    @Override
    public String getImpuesto() {
        return impuesto;
    }
    
    public void setImpuesto(String impuesto) {
        this.impuesto = impuesto;
    }
    
    @Override
    public ArrayList<LinkedHashMap<String, String>> getListaConceptos() {
        return listaConceptos;
    }
    
    public void setListaConceptos(ArrayList<LinkedHashMap<String, String>> listaConceptos) {
        this.listaConceptos = listaConceptos;
    }
    
    @Override
    public String getObservaciones() {
        return observaciones;
    }
    
    public void setObservaciones(String observaciones) {
        this.observaciones = observaciones;
    }
    
    @Override
    public String getSubtotal() {
        return subtotal;
    }
    
    public void setSubtotal(String subtotal) {
        this.subtotal = subtotal;
    }
    
    @Override
    public String getTotal() {
        return total;
    }
    
    public void setTotal(String total) {
        this.total = total;
    }

    @Override
    public String getFecha_cotizacion() {
        return fecha_cotizacion;
    }

    public void setFecha_cotizacion(String fecha_cotizacion) {
        this.fecha_cotizacion = fecha_cotizacion;
    }
          
    @Override
    public String getMoneda() {
        return moneda;
    }

    public void setMoneda(String moneda) {
        this.moneda = moneda;
    }

    @Override
    public String getMoneda_id() {
        return moneda_id;
    }

    public void setMoneda_id(String moneda_id) {
        this.moneda_id = moneda_id;
    }
    public String getCliente_id() {
        return cliente_id;
    }
    
    public void setCliente_id(String cliente_id) {
        this.cliente_id = cliente_id;
    }
    //aqui termina geter y seter para variables de la cotizacion
    
    
    
    //comienza geter y seter para variables del cliente
    @Override
    public String getClient_calle() {
        return client_calle;
    }
    
    public void setClient_calle(String client_calle) {
        this.client_calle = client_calle;
    }
    
    @Override
    public String getClient_colonia() {
        return client_colonia;
    }
    
    public void setClient_colonia(String client_colonia) {
        this.client_colonia = client_colonia;
    }
    
    @Override
    public String getClient_cp() {
        return client_cp;
    }
    
    public void setClient_cp(String client_cp) {
        this.client_cp = client_cp;
    }
    
    @Override
    public String getClient_entidad() {
        return client_entidad;
    }
    
    public void setClient_entidad(String client_entidad) {
        this.client_entidad = client_entidad;
    }
    
    @Override
    public String getClient_localidad() {
        return client_localidad;
    }
    
    public void setClient_localidad(String client_localidad) {
        this.client_localidad = client_localidad;
    }
    
    @Override
    public String getClient_numero() {
        return client_numero;
    }
    
    public void setClient_numero(String client_numero) {
        this.client_numero = client_numero;
    }
    
    @Override
    public String getClient_pais() {
        return client_pais;
    }
    
    public void setClient_pais(String client_pais) {
        this.client_pais = client_pais;
    }
    
    @Override
    public String getClient_razon_social() {
        return client_razon_social;
    }
    
    public void setClient_razon_social(String client_razon_social) {
        this.client_razon_social = client_razon_social;
    }
    
    @Override
    public String getClient_rfc() {
        return client_rfc;
    }
    
    public void setClient_rfc(String client_rfc) {
        this.client_rfc = client_rfc;
    }
    
    @Override
    public String getClient_telefono() {
        return client_telefono;
    }
    
    public void setClient_telefono(String client_telefono) {
        this.client_telefono = client_telefono;
    }
    
    public String getClient_contacto() {
        return client_contacto;
    }
    
    public void setClient_contacto(String client_contacto) {
        this.client_contacto = client_contacto;
    }
    
    //termina geter y seter para variables del cliente
    
    @Override
    public ArrayList<HashMap<String, Object>> getPage(String folio,String cliente,String fecha_inicial,String fecha_final, int offset, int pageSize, String orderBy, String asc) {
        
        String sql_busqueda = "select id from erp_fn_buscador_cotizacions(?,?,?,?) as foo (id integer)";
        
	String sql_to_query = "SELECT DISTINCT "
                +"erp_cotizacions.id,"
                +"erp_cotizacions.folio,"
                +"cxc_clie.razon_social as cliente,"
                +"erp_cotizacions_status.titulo as situacion,"
                +"to_char(erp_cotizacions.momento_creacion,'dd-mm-yyyy') as fecha_creacion "
        +"FROM erp_cotizacions "
        +"LEFT JOIN erp_cotizacions_status ON erp_cotizacions_status.id = erp_cotizacions.status_id "
        +"LEFT JOIN cxc_clie on cxc_clie.id = erp_cotizacions.cliente_id "
        +"JOIN ("+sql_busqueda+") as subt on subt.id=erp_cotizacions.id "
        +"order by "+orderBy+" "+asc+" limit ? OFFSET ?";
        
        //log.log(Level.INFO, "Ejecutando query de {0}", sql_to_query);
        ArrayList<HashMap<String, Object>> hm = (ArrayList<HashMap<String, Object>>) this.jdbcTemplate.query(
            sql_to_query, 
            new Object[]{new String(folio),new String(cliente),new String(fecha_inicial),new String(fecha_final),new Integer(pageSize),new Integer(offset)}, new RowMapper() {
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    HashMap<String, Object> row = new HashMap<String, Object>();
                    row.put("id",rs.getInt("id"));
                    row.put("folio",rs.getString("folio"));
                    row.put("cliente",rs.getString("cliente"));
                    row.put("situacion",rs.getString("situacion"));
                    row.put("fecha",rs.getString("fecha_creacion"));
                    return row;
                }
            }
        );
        return hm;   
    }
    
    
    
    
    
    @Override
    public int countAll(String folio,String cliente,String fecha_inicial,String fecha_final) {
        String sql_busqueda = "select id from erp_fn_buscador_cotizacions('"+folio+"','"+cliente+"','"+fecha_inicial+"','"+fecha_final+"') as foo (id integer)";
        String sql_to_query = "select count(id) as total from ("+sql_busqueda+") as subt";
        
        int rowCount = this.getJdbcTemplate().queryForInt(sql_to_query);
        return rowCount;
    }
    
    
    
    //obtiene  los datos de la cotizacion
    @Override
    public ArrayList<HashMap<String, Object>> getCotizacion(Integer id_cotizacion) {
        
        String sql_query = "SELECT erp_cotizacions.id,"
                                +"erp_cotizacions.status_id,"
                                +"tblclient.moneda as moneda_id,"
                                +"gral_mon.descripcion as moneda,"
                                +"erp_cotizacions.observaciones,"
                                +"tblclient.id as cliente_id,"
                                +"tblclient.numero_control,"
                                +"tblclient.rfc,"
                                +"tblclient.razon_social,"
                                +"tblclient.direccion "
                        +"FROM erp_cotizacions "
                        +"LEFT JOIN ( "
                                    + "SELECT cxc_clie.id, "
                                        + "cxc_clie.rfc, "
                                        + "cxc_clie.razon_social, "
                                        + "cxc_clie.moneda, "
                                        + "cxc_clie.numero_control, "
                                        + "cxc_clie.calle||' '||cxc_clie.numero||', '||cxc_clie.colonia||', '||gral_mun.titulo||', '||gral_edo.titulo||', '||gral_pais.titulo||' C.P. '||cxc_clie.cp as direccion "
                                    + "FROM cxc_clie "
                                    + "JOIN gral_pais ON gral_pais.id = cxc_clie.pais_id "
                                    + "JOIN gral_edo ON gral_edo.id = cxc_clie.estado_id "
                                    + "JOIN gral_mun ON gral_mun.id = cxc_clie.municipio_id "
                        +") AS tblclient ON tblclient.id = erp_cotizacions.cliente_id "
                        +"LEFT JOIN  gral_mon on gral_mon.id = tblclient.moneda  "
                        +"WHERE erp_cotizacions.id = ? ";
                        
        //System.out.println("Obteniendo datos de la cotizacion: "+sql_query);
        ArrayList<HashMap<String, Object>> hm_cotizacion = (ArrayList<HashMap<String, Object>>) this.jdbcTemplate.query(
            sql_query,  
            new Object[]{new Integer(id_cotizacion)}, new RowMapper() {
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    HashMap<String, Object> row = new HashMap<String, Object>();
                    row.put("id",rs.getInt("id"));
                    row.put("status_id",rs.getString("status_id"));
                    row.put("moneda_id",rs.getString("moneda_id"));
                    row.put("moneda",rs.getString("moneda"));
                    row.put("observaciones",rs.getString("observaciones"));
                    row.put("cliente_id",rs.getString("cliente_id"));
                    row.put("rfc",rs.getString("rfc"));
                    row.put("numero_control",rs.getString("numero_control"));
                    row.put("razon_social",rs.getString("razon_social"));
                    row.put("direccion",rs.getString("direccion"));
                    
                    return row;
                }
            }
        );
        return hm_cotizacion;
    }
    
    
    
    //obtiene los datos del grid de la cotizacion
    @Override
    public ArrayList<HashMap<String, Object>> getDatosGrid(Integer id_cotizacion) {
        
        String sql_query = "SELECT erp_cotizacions_detalles.id as id_detalle,"
                                +"erp_cotizacions_detalles.producto_id,"
                                +"inv_prod.sku,"
                                +"inv_prod.descripcion as titulo,"
                                +"inv_prod_unidades.titulo as unidad,"
                                +"inv_prod_presentaciones.id as id_presentacion,"
                                +"inv_prod_presentaciones.titulo as presentacion,"
                                +"erp_cotizacions_detalles.cantidad,"
                                +"erp_cotizacions_detalles.precio_unitario,"
                                +"erp_cotizacions_detalles.moneda_id,"
                                +"(erp_cotizacions_detalles.cantidad * erp_cotizacions_detalles.precio_unitario) AS importe "
                        +"FROM erp_cotizacions "
                        + "JOIN erp_cotizacions_detalles on erp_cotizacions_detalles.cotizacions_id=erp_cotizacions.id "
                        +"LEFT JOIN inv_prod on inv_prod.id = erp_cotizacions_detalles.producto_id "
                        +"LEFT JOIN inv_prod_unidades on inv_prod_unidades.id = inv_prod.unidad_id "
                        +"LEFT JOIN inv_prod_presentaciones on inv_prod_presentaciones.id = erp_cotizacions_detalles.presentacion_id "
                        + "WHERE erp_cotizacions.id = ? ORDER BY erp_cotizacions_detalles.id";
        
        ArrayList<HashMap<String, Object>> hm_grid = (ArrayList<HashMap<String, Object>>) this.jdbcTemplate.query(
            sql_query,  
            new Object[]{new Integer(id_cotizacion)}, new RowMapper() {
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    HashMap<String, Object> row = new HashMap<String, Object>();
                    row.put("id_detalle",rs.getInt("id_detalle"));
                    row.put("producto_id",rs.getString("producto_id"));
                    row.put("sku",rs.getString("sku"));
                    row.put("titulo",rs.getString("titulo"));
                    row.put("unidad",rs.getString("unidad"));
                    row.put("id_presentacion",rs.getString("id_presentacion"));
                    row.put("presentacion",rs.getString("presentacion"));
                    row.put("cantidad",rs.getString("cantidad"));
                    row.put("precio_unitario",StringHelper.roundDouble(rs.getDouble("precio_unitario"),2));
                    row.put("moneda_id",rs.getInt("moneda_id"));
                    row.put("importe",StringHelper.roundDouble(rs.getDouble("importe"),2));
                    
                    return row;
                }
            }
        );
        return hm_grid;
    }
    
    //obtiene valor del impuesto
    @Override
    public ArrayList<HashMap<String, Object>> getValoriva() {
        /*
        String sql_to_query = "SELECT erp_ivatipos.id as id_impuesto,erp_ivatipos.valor AS valor_impuesto FROM erp_ivatipos "
			+"JOIN (SELECT valor as id_impuesto  FROM erp_parametros_generales  WHERE variable = 'tipo_impuesto') AS tipo_impuesto ON tipo_impuesto.id_impuesto::integer = erp_ivatipos.id;";
        */
        String sql_to_query = "SELECT id AS id_impuesto, iva_1 AS valor_impuesto FROM gral_imptos WHERE borrado_logico=FALSE AND id=1";
        //log.log(Level.INFO, "Ejecutando query de {0}", sql_to_query);
        ArrayList<HashMap<String, Object>> hm_valoriva = (ArrayList<HashMap<String, Object>>) this.jdbcTemplate.query(
            sql_to_query,
            new Object[]{}, new RowMapper(){
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    HashMap<String, Object> row = new HashMap<String, Object>();
                    row.put("id_impuesto",rs.getString("id_impuesto"));
                    row.put("valor_impuesto",rs.getString("valor_impuesto"));
                    return row;
                }
            }
        );
        return hm_valoriva;
    }
    
    //obtiene todas la monedas
    @Override
    public ArrayList<HashMap<String, Object>> getMonedas() {
        String sql_to_query = "SELECT id, descripcion_abr FROM  gral_mon where borrado_logico=FALSE AND ventas=TRUE ORDER BY id ASC;";
        //log.log(Level.INFO, "Ejecutando query de {0}", sql_to_query);
        ArrayList<HashMap<String, Object>> hm_monedas = (ArrayList<HashMap<String, Object>>) this.jdbcTemplate.query(
            sql_to_query,
            new Object[]{}, new RowMapper(){
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    HashMap<String, Object> row = new HashMap<String, Object>();
                    row.put("id",rs.getInt("id"));
                    row.put("descripcion",rs.getString("descripcion_abr"));
                    return row;
                }
            }
        );
        return hm_monedas;
    }
    
    
    
    //buscador de clientes
    @Override
    public ArrayList<HashMap<String, Object>> get_buscador_clientes(String cadena, Integer filtro, Integer id_empresa, Integer id_sucursal) {
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
	
	String sql_query = "SELECT "
                                    +"sbt.id,"
                                    +"sbt.numero_control,"
                                    +"sbt.rfc,"
                                    +"sbt.razon_social,"
                                    +"sbt.direccion,"
                                    +"sbt.moneda_id,"
                                    +"gral_mon.descripcion as moneda, "
                                    +"sbt.contacto "
                            +"FROM(SELECT cxc_clie.id,"
                                            +"cxc_clie.numero_control,"
                                            +"cxc_clie.rfc, "
                                            +"cxc_clie.razon_social,"
                                            +"cxc_clie.calle||' '||cxc_clie.numero||', '||cxc_clie.colonia||', '||gral_mun.titulo||', '||gral_edo.titulo||', '||gral_pais.titulo||' C.P. '||cxc_clie.cp as direccion, "
                                            +"cxc_clie.moneda as moneda_id, "
                                            +"cxc_clie.contacto "
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
                    row.put("contacto",rs.getString("contacto"));
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
        
    
    //buscador de inv_prod_presentaciones del producto
    @Override
    public ArrayList<HashMap<String, Object>> get_presentaciones_producto(String sku) {
	
	String sql_query = "SELECT "
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
                        +"where inv_prod.sku ILIKE '"+sku+"';";
        
        ArrayList<HashMap<String, Object>> hm_cli = (ArrayList<HashMap<String, Object>>) this.jdbcTemplate.query(
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
                    
                    return row;
                }
            }
        );
        return hm_cli;
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
    public int selectFunctionForThisApp(Integer id_cotizacion, String data, String accion, String string_array) {
        String sql_to_query = "select * from erp_fn_aplicativo_cotizacions_acciones("+id_cotizacion+",'"+data+"','"+accion+"',array["+string_array+"]);";
        //System.out.println("Guarda cotizacion:"+sql_to_query);
        int update = this.getJdbcTemplate().queryForInt(sql_to_query);
        
        return update;
    }
    
    
    
    
    
    
    @Override
    public void getDatosEmpresaPdf(HashMap<String, String> datosEmpresa) {
        this.setEmp_RazonSocial(datosEmpresa.get("emp_razon_social"));
        this.setEmp_Rfc(datosEmpresa.get("emp_rfc"));
        this.setEmp_Calle(datosEmpresa.get("emp_calle"));
        this.setEmp_Numero(datosEmpresa.get("emp_no_exterior"));
        this.setEmp_Colonia(datosEmpresa.get("emp_colonia"));
        this.setEmp_Cp(datosEmpresa.get("emp_cp"));
        this.setEmp_Pais(datosEmpresa.get("emp_pais"));
        this.setEmp_Entidad(datosEmpresa.get("emp_estado"));
        this.setEmp_Municipio(datosEmpresa.get("emp_municipio"));
    }
    
    
    
    
    
    @Override
    public void getDatosCotizacionPdf(Integer id_cotizacion) {
        
        //obtener datos de la cotizacion
	String sql_query_cotizacion = "SELECT erp_cotizacions.folio, "+
                            "erp_cotizacions.cliente_id, "+
                            "erp_cotizacions.observaciones, "+
                            //"erp_cotizacions.subtotal, "+
                            //"erp_cotizacions.impuesto, "+
                            //"erp_cotizacions.total, "+
                            //"erp_cotizacions.moneda_id, "+
                            //"gral_mon.descripcion as moneda, "+
                            "to_char((CASE WHEN erp_cotizacions.momento_actualizacion IS NULL THEN erp_cotizacions.momento_creacion ELSE erp_cotizacions.momento_actualizacion END),'yyyy-mm-dd') as fecha_cotizacion "+
                    "FROM erp_cotizacions  "+
                    //"left JOIN gral_mon on gral_mon.id = erp_cotizacions.moneda_id "+
                    "WHERE  erp_cotizacions.id =" + id_cotizacion + " limit 1";
        
        //System.out.println("sql_query_cotizacion: "+sql_query_cotizacion);
        
        Map<String, Object> map_dat_cot = this.getJdbcTemplate().queryForMap(sql_query_cotizacion);
        
        this.setFolio(map_dat_cot.get("folio").toString());
        this.setCliente_id(map_dat_cot.get("cliente_id").toString());
        this.setObservaciones(map_dat_cot.get("observaciones").toString());
        //this.setSubtotal(map_dat_cot.get("subtotal").toString());
        //this.setImpuesto(map_dat_cot.get("impuesto").toString());
        //this.setTotal(map_dat_cot.get("total").toString());
        //this.setMoneda_id(map_dat_cot.get("moneda_id").toString());
        //this.setMoneda(map_dat_cot.get("moneda").toString());
        this.setFecha_cotizacion(map_dat_cot.get("fecha_cotizacion").toString());
        
        //System.out.println("sql_query_cotizacion: "+sql_query_cotizacion);
        
        
        //obtener datos del cliente
	String sql_query_cliente = "SELECT cxc_clie.rfc, "+
                            "cxc_clie.razon_social, "+
                            "cxc_clie.calle, "+
                            "cxc_clie.numero, "+
                            "cxc_clie.colonia, "+
                            "gral_mun.titulo as localidad, "+
                            "gral_edo.titulo as entidad, "+
                            "gral_pais.titulo as pais, "+
                            "cxc_clie.cp, "+
                            "cxc_clie.telefono1, "+
                            "(CASE WHEN cxc_clie.contacto IS NULL THEN '' ELSE cxc_clie.contacto END ) AS  contacto "+
                    "FROM cxc_clie "+
                    "JOIN gral_pais ON gral_pais.id = cxc_clie.pais_id "+
                    "JOIN gral_edo ON gral_edo.id = cxc_clie.estado_id "+
                    "JOIN gral_mun ON gral_mun.id = cxc_clie.municipio_id "+
                    "WHERE cxc_clie.borrado_logico=false AND cxc_clie.id = "+ this.getCliente_id() +" limit 1";
        
        Map<String, Object> map_client = this.getJdbcTemplate().queryForMap(sql_query_cliente);
        
        this.setClient_calle(map_client.get("calle").toString());
        this.setClient_colonia(map_client.get("colonia").toString());
        this.setClient_cp(map_client.get("cp").toString());
        this.setClient_entidad(map_client.get("entidad").toString());
        this.setClient_localidad(map_client.get("localidad").toString());
        this.setClient_numero(map_client.get("numero").toString());
        this.setClient_pais(map_client.get("pais").toString());
        this.setClient_razon_social(map_client.get("razon_social").toString());
        this.setClient_rfc(map_client.get("rfc").toString());
        this.setClient_telefono(map_client.get("telefono1").toString());
        this.setClient_contacto(map_client.get("contacto").toString());
        
        
        //obtener lista de conceptos de la cotizacion
	String sql_query_conceptos = "SELECT  inv_prod.sku, "+
                            "inv_prod.descripcion as titulo, "+
                            "inv_prod_unidades.titulo_abr as unidad, "+
                            "inv_prod_presentaciones.titulo as presentacion, "+
                            "erp_cotizacions_detalles.cantidad, "+
                            "erp_cotizacions_detalles.precio_unitario, "+
                            "gral_mon.descripcion_abr as moneda, "+
                            "(erp_cotizacions_detalles.cantidad * erp_cotizacions_detalles.precio_unitario) AS importe "+
                    "FROM erp_cotizacions_detalles "+
                    "LEFT JOIN inv_prod on inv_prod.id = erp_cotizacions_detalles.producto_id "+
                    "LEFT JOIN inv_prod_unidades on inv_prod_unidades.id = inv_prod.unidad_id "+
                    "LEFT JOIN inv_prod_presentaciones on inv_prod_presentaciones.id = erp_cotizacions_detalles.presentacion_id "+
                    "LEFT JOIN gral_mon on gral_mon.id = erp_cotizacions_detalles.moneda_id "+
                    "WHERE erp_cotizacions_detalles.cotizacions_id = "+ id_cotizacion + "" +
                    "ORDER BY erp_cotizacions_detalles.id";
        
        //System.out.println("sql_query_conceptos: "+sql_query_conceptos);
        
        ArrayList< LinkedHashMap<String,String>> array_listacon = (ArrayList< LinkedHashMap<String,String>>) this.jdbcTemplate.query(
            sql_query_conceptos,
            new Object[]{}, new RowMapper() {
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    //HashMap<String, Object> row = new HashMap<String, Object>();
                    LinkedHashMap<String,String> listcon = new LinkedHashMap<String,String>();
                    listcon.put("sku",rs.getString("sku"));
                    listcon.put("titulo",rs.getString("titulo"));
                    listcon.put("unidad",rs.getString("unidad"));
                    listcon.put("presentacion",rs.getString("presentacion"));
                    listcon.put("cantidad",rs.getString("cantidad"));
                    listcon.put("precio_unitario",StringHelper.roundDouble(rs.getDouble("precio_unitario"),2));
                    listcon.put("importe",StringHelper.roundDouble(rs.getDouble("importe"),2));
                    listcon.put("moneda",rs.getString("moneda"));
                    return listcon;
                }
            }
        );
        
        //ArrayList<LinkedHashMap<String,String>>listacon = getListaConceptos();
        //listacon.add(array_listacon);
        this.setListaConceptos(array_listacon);
        
    }
    
    
    @Override
    public void getDatosCotizacionDescripcionPdf(Integer id_cotizacion) {
        
        //obtener datos de la cotizacion
	String sql_query_cotizacion = "SELECT erp_cotizacions.folio, "+
                            "erp_cotizacions.cliente_id, "+
                            "erp_cotizacions.observaciones, "+
                            //"erp_cotizacions.subtotal, "+
                            //"erp_cotizacions.impuesto, "+
                            //"erp_cotizacions.total, "+
                            //"erp_cotizacions.moneda_id, "+
                            //"gral_mon.descripcion as moneda, "+
                            "to_char((CASE WHEN erp_cotizacions.momento_actualizacion IS NULL THEN erp_cotizacions.momento_creacion ELSE erp_cotizacions.momento_actualizacion END),'yyyy-mm-dd') as fecha_cotizacion "+
                    "FROM erp_cotizacions  "+
                    //"left JOIN gral_mon on gral_mon.id = erp_cotizacions.moneda_id "+
                    "WHERE  erp_cotizacions.id =" + id_cotizacion + " limit 1";
        
        //System.out.println("sql_query_cotizacion: "+sql_query_cotizacion);
        
        Map<String, Object> map_dat_cot = this.getJdbcTemplate().queryForMap(sql_query_cotizacion);
        
        this.setFolio(map_dat_cot.get("folio").toString());
        this.setCliente_id(map_dat_cot.get("cliente_id").toString());
        this.setObservaciones(map_dat_cot.get("observaciones").toString());
        //this.setSubtotal(map_dat_cot.get("subtotal").toString());
        //this.setImpuesto(map_dat_cot.get("impuesto").toString());
        //this.setTotal(map_dat_cot.get("total").toString());
        //this.setMoneda_id(map_dat_cot.get("moneda_id").toString());
        //this.setMoneda(map_dat_cot.get("moneda").toString());
        this.setFecha_cotizacion(map_dat_cot.get("fecha_cotizacion").toString());
        
        //System.out.println("sql_query_cotizacion: "+sql_query_cotizacion);
        
        
        //obtener datos del cliente
	String sql_query_cliente = "SELECT cxc_clie.rfc, "+
                            "cxc_clie.razon_social, "+
                            "cxc_clie.calle, "+
                            "cxc_clie.numero, "+
                            "cxc_clie.colonia, "+
                            "gral_mun.titulo as localidad, "+
                            "gral_edo.titulo as entidad, "+
                            "gral_pais.titulo as pais, "+
                            "cxc_clie.cp, "+
                            "cxc_clie.telefono1, "+
                            "(CASE WHEN cxc_clie.contacto IS NULL THEN '' ELSE cxc_clie.contacto END ) AS  contacto "+
                    "FROM cxc_clie "+
                    "JOIN gral_pais ON gral_pais.id = cxc_clie.pais_id "+
                    "JOIN gral_edo ON gral_edo.id = cxc_clie.estado_id "+
                    "JOIN gral_mun ON gral_mun.id = cxc_clie.municipio_id "+
                    "WHERE cxc_clie.borrado_logico=false AND cxc_clie.id = "+ this.getCliente_id() +" limit 1";
        
        Map<String, Object> map_client = this.getJdbcTemplate().queryForMap(sql_query_cliente);
        
        this.setClient_calle(map_client.get("calle").toString());
        this.setClient_colonia(map_client.get("colonia").toString());
        this.setClient_cp(map_client.get("cp").toString());
        this.setClient_entidad(map_client.get("entidad").toString());
        this.setClient_localidad(map_client.get("localidad").toString());
        this.setClient_numero(map_client.get("numero").toString());
        this.setClient_pais(map_client.get("pais").toString());
        this.setClient_razon_social(map_client.get("razon_social").toString());
        this.setClient_rfc(map_client.get("rfc").toString());
        this.setClient_telefono(map_client.get("telefono1").toString());
        this.setClient_contacto(map_client.get("contacto").toString());
        
        
        //obtener lista de conceptos de la cotizacion
	String sql_query_conceptos = "SELECT  inv_prod.sku, "+
                            "inv_mar.titulo, "+
                            
                            "inv_prod_unidades.titulo_abr as unidad, "+ 
                            "inv_prod.archivo_img as imagen, "+ 
                            "(case when inv_prod.descripcion_corta is null then '' else inv_prod.descripcion_corta end) as descripcion, "+
                            "inv_prod_presentaciones.titulo as presentacion, "+
                            "erp_cotizacions_detalles.cantidad, "+
                            "erp_cotizacions_detalles.precio_unitario, "+
                            "gral_mon.descripcion_abr as moneda, "+
                            "(erp_cotizacions_detalles.cantidad * erp_cotizacions_detalles.precio_unitario) AS importe "+
                    "FROM erp_cotizacions_detalles "+
                    "LEFT JOIN inv_prod on inv_prod.id = erp_cotizacions_detalles.producto_id "+
                    "LEFT JOIN inv_prod_unidades on inv_prod_unidades.id = inv_prod.unidad_id "+ 
                    "LEFT JOIN inv_mar on inv_mar.id = inv_prod.inv_mar_id "+
                    "LEFT JOIN inv_prod_presentaciones on inv_prod_presentaciones.id = erp_cotizacions_detalles.presentacion_id "+
                    "LEFT JOIN gral_mon on gral_mon.id = erp_cotizacions_detalles.moneda_id "+
                    "WHERE erp_cotizacions_detalles.cotizacions_id = "+ id_cotizacion + "" +
                    "ORDER BY erp_cotizacions_detalles.id";
        
        System.out.println("Imprime con Descripcion:___ "+sql_query_conceptos);
        
        ArrayList< LinkedHashMap<String,String>> array_listacon = (ArrayList< LinkedHashMap<String,String>>) this.jdbcTemplate.query(
            sql_query_conceptos,
            new Object[]{}, new RowMapper() {
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    //HashMap<String, Object> row = new HashMap<String, Object>();
                    LinkedHashMap<String,String> listcon = new LinkedHashMap<String,String>();
                    listcon.put("sku",rs.getString("sku"));
                    listcon.put("titulo",rs.getString("titulo"));
                    listcon.put("imagen",rs.getString("imagen"));
                    listcon.put("unidad",rs.getString("unidad"));
                    listcon.put("presentacion",rs.getString("presentacion"));
                    listcon.put("cantidad",rs.getString("cantidad"));
                    listcon.put("precio_unitario",StringHelper.roundDouble(rs.getDouble("precio_unitario"),2));
                    listcon.put("importe",StringHelper.roundDouble(rs.getDouble("importe"),2));
                    listcon.put("moneda",rs.getString("moneda"));
                    listcon.put("descripcion",rs.getString("descripcion"));
                    return listcon;
                }
            }
        );
        
        //ArrayList<LinkedHashMap<String,String>>listacon = getListaConceptos();
        //listacon.add(array_listacon);
        this.setListaConceptos(array_listacon);
        
    }
    
    

    
    

    
}