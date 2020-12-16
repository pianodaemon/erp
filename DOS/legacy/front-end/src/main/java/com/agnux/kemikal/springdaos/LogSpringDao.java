package com.agnux.kemikal.springdaos;


import com.agnux.common.helpers.StringHelper;
import com.agnux.kemikal.interfacedaos.LogInterfaceDao;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.*;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;


public class LogSpringDao implements LogInterfaceDao{
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
    public String selectFunctionForLogAdmProcesos(String campos_data, String extra_data_array) {
        String sql_to_query = "select * from log_adm_procesos('"+campos_data+"',array["+extra_data_array+"]);";
        
        //System.out.println("sql_to_query: "+sql_to_query);
        
        String valor_retorno="";
        Map<String, Object> update = this.getJdbcTemplate().queryForMap(sql_to_query);
        valor_retorno = update.get("log_adm_procesos").toString();
        return valor_retorno;
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
    public int countAll(String data_string) {
        String sql_busqueda = "select id from gral_bus_catalogos('"+data_string+"') as foo (id integer)";
        String sql_to_query = "select count(id)::int as total from ("+sql_busqueda+") as subt";
        
        int rowCount = this.getJdbcTemplate().queryForInt(sql_to_query);
        return rowCount;
    }
    
    
    
    
    
    
    
         
    //Metodo que obtiene datos para el grid de Asignacion de Rutas
    @Override
    public ArrayList<HashMap<String, Object>> getRutas_PaginaGrid(String data_string, int offset, int pageSize, String orderBy, String asc) {
        String sql_busqueda = "select id from gral_bus_catalogos(?) as foo (id integer)";
        
	String sql_to_query = ""
        + "SELECT "
            + "log_rutas.id, "
            + "log_rutas.folio, "
            + "log_vehiculos.marca || '  '||log_vehiculos.numero_economico as vehiculo, "
            + "log_choferes.nombre|| '  '||log_choferes.apellido_paterno|| '  '||log_choferes.apellido_materno as nombre_chofer "
        + "from log_rutas "
        + "join log_choferes on log_choferes.id=log_rutas.log_chofer_id "
        + "join log_vehiculos on log_vehiculos.id=log_rutas.log_vehiculo_id "
        + "JOIN ("+sql_busqueda+") AS sbt on sbt.id = log_rutas.id "
        + "WHERE log_rutas.borrado_logico=false  "
        + "order by "+orderBy+" "+asc+" limit ? OFFSET ?";
        
        //System.out.println("IMPRIMIENDO EL GRID DE RUTAS checar??: "+sql_to_query);
        ArrayList<HashMap<String, Object>> hm = (ArrayList<HashMap<String, Object>>) this.jdbcTemplate.query(
            sql_to_query, 
            new Object[]{data_string, new Integer(pageSize),new Integer(offset)}, new RowMapper() {
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    HashMap<String, Object> row = new HashMap<String, Object>();
                    row.put("id",rs.getInt("id"));
                    row.put("folio",rs.getString("folio"));
                    row.put("vehiculo",rs.getString("vehiculo"));
                    row.put("nombre_chofer",rs.getString("nombre_chofer"));
                   
                    return row;
                }
            }
        );
        return hm; 
    }

    
    
    
    
    @Override
    public ArrayList<HashMap<String, String>> getchoferes(Integer id_empresa) {
        String sql_to_query = "SELECT log_choferes.id,clave,nombre||'  '||apellido_paterno||'  '||apellido_materno  as nombre_chofer FROM log_choferes "
                + "join gral_emp on gral_emp.id = log_choferes.gral_emp_id "
                + " where log_choferes.gral_emp_id="+id_empresa
                + "  ORDER BY  nombre_chofer ASC;";
        //log.log(Level.INFO, "Ejecutando query de {0}", sql_to_query);
        //System.out.println("IMPRIMIENDO LOS CHOFERES"+sql_to_query);
        ArrayList<HashMap<String, String>> hm_choferes = (ArrayList<HashMap<String, String>>) this.jdbcTemplate.query(
            sql_to_query,
            new Object[]{}, new RowMapper(){
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    HashMap<String, String> row = new HashMap<String, String>();
                    row.put("id",String.valueOf(rs.getInt("id")));
                    row.put("nombre_chofer",rs.getString("nombre_chofer"));
                    
                    return row;
                }
            }
        );
        return hm_choferes;
    }
     
     
     
    @Override
    public ArrayList<HashMap<String, String>> getvehiculo(Integer id_empresa) {
         String sql_to_query = "SELECT log_vehiculos.id, log_vehiculos.marca ||' ' ||numero_economico AS  vehiculo "
                                + "FROM log_vehiculos "
                              + "join gral_emp on gral_emp.id = log_vehiculos.gral_emp_id "
                              + "WHERE log_vehiculos.borrado_logico=FALSE AND log_vehiculos.gral_emp_id="+id_empresa
                              + " ORDER BY  log_vehiculos.marca ASC;";
        //log.log(Level.INFO, "Ejecutando query de {0}", sql_to_query);
        //System.out.println("OBTENIENDO LOS VEHICULOS"+sql_to_query);
        ArrayList<HashMap<String, String>> hm_vehiculo = (ArrayList<HashMap<String, String>>) this.jdbcTemplate.query(
            sql_to_query,
            new Object[]{}, new RowMapper(){
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    HashMap<String, String> row = new HashMap<String, String>();
                    row.put("id",String.valueOf(rs.getInt("id")));
                    row.put("vehiculo",rs.getString("vehiculo"));
                    return row;
                }
            }
        );
        return hm_vehiculo;
    }
    
    
    
    
    
    //obtiene las facturas  que seran enviadas a la ruta
    @Override
    public ArrayList<HashMap<String, String>> getFacturas_entrega_mercancia(Integer id_empresa, String fecha_inicial,String fecha_final, String factura, Integer tipo_busqueda) {
        String where = "";
        String[] fi ;
        String[] ff ;
        
        if(tipo_busqueda==1){
            fi = fecha_inicial.split("-");
            ff = fecha_final.split("-");
            
            fecha_inicial=fi[0]+fi[1]+fi[2];
            fecha_final=ff[0]+ff[1]+ff[2];
            
            where = " AND to_char(fac_docs.momento_creacion,'yyyymmdd')::integer BETWEEN "+fecha_inicial+" AND "+fecha_final+" "
                    + " AND fac_docs.enviar_ruta=TRUE ";
        }
        
        if(tipo_busqueda==2){
            where = " AND fac_docs.serie_folio='"+factura+"' ";
        }
        
        String sql_to_query = ""
                + "SELECT "
                      +"inv_prod.id as id_invprod,fac_docs.id as id_fac_docs, fac_docs.serie_folio as factura,"
                      +"to_char(fac_docs.momento_creacion,'dd-mm-yyyy') as fecha_factura,"
                      +"cxc_clie.razon_social as cliente,"
                      +"inv_prod.sku as codigo,"
                      +"fac_docs_detalles.cantidad,"
                      +"fac_docs_detalles.precio_unitario,"
                      +"inv_prod.descripcion,"
                      +"(fac_docs_detalles.cantidad * fac_docs_detalles.precio_unitario ) as importe "
                  +"FROM  fac_docs "
                  +"join fac_docs_detalles on fac_docs_detalles.fac_doc_id= fac_docs.id "
                  +"join inv_prod on inv_prod.id =fac_docs_detalles.inv_prod_id "
                  +"join cxc_clie on cxc_clie.id=  fac_docs.cxc_clie_id    "
                  +"join erp_proceso on erp_proceso.id=fac_docs.proceso_id  "
                  +"where fac_docs.cancelado=FALSE "
                + "AND erp_proceso.empresa_id="+id_empresa+" "+where;
                
                
        //log.log(Level.INFO, "Ejecutando query de {0}", sql_to_query);
        //System.out.println("Obteniendo facturas a entregar:: "+sql_to_query);
        ArrayList<HashMap<String, String>> hm_facturas_entregar = (ArrayList<HashMap<String, String>>) this.jdbcTemplate.query(
            sql_to_query,
            new Object[]{}, new RowMapper(){
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    HashMap<String, String> row = new HashMap<String, String>();
                    row.put("id_invprod",String.valueOf(rs.getInt("id_invprod")));
                    row.put("id_fac_docs",String.valueOf(rs.getInt("id_fac_docs")));
                    row.put("factura",rs.getString("factura"));
                    row.put("fecha_factura",rs.getString("fecha_factura"));
                    row.put("cliente",rs.getString("cliente"));
                    row.put("codigo",rs.getString("codigo"));
                    row.put("cantidad",StringHelper.roundDouble(rs.getString("cantidad"),2));
                    row.put("precio_unitario",StringHelper.roundDouble(rs.getString("precio_unitario"),2));
                    row.put("descripcion",rs.getString("descripcion"));
                    row.put("importe",StringHelper.roundDouble(rs.getString("importe"),2));
                    
                    return row;
                }
            }
        );
        return hm_facturas_entregar;
    }
    
    
    
    
    @Override
    public ArrayList<HashMap<String, String>> getFacturas_fac_rev_cobro_detalle(Integer id_empresa, String folio_fac_rev_cobro) {
        
        String sql_to_query = ""
            + "SELECT  "
                + "cxc_fac_rev_cob.folio as folio_programacion, "
                +"cxc_fac_rev_cob_detalle.id AS fac_rev_cob_detalle_id,  "
                +"erp_h_facturas.id AS erp_h_fac_id, "
                +"erp_h_facturas.serie_folio as factura, "
                +"to_char(erp_h_facturas.momento_facturacion,'dd/mm/yyyy') as fecha_factura,  "
                +"cxc_clie.razon_social AS cliente,  "
                +"erp_h_facturas.saldo_factura, "
                + "(case when  cxc_fac_rev_cob_detalle.revision_cobro='R' THEN 'REVISION' WHEN  cxc_fac_rev_cob_detalle.revision_cobro='C' THEN 'COBRO' else '' end) AS revision_cobro, "
                +"erp_h_facturas.estatus_revision  "
            +"FROM cxc_fac_rev_cob_detalle "
            +"JOIN erp_h_facturas on erp_h_facturas.id=cxc_fac_rev_cob_detalle.erp_h_facturas_id "
            +"JOIN cxc_clie on cxc_clie.id=erp_h_facturas.cliente_id  "
            +"join cxc_fac_rev_cob on cxc_fac_rev_cob.id=cxc_fac_rev_cob_detalle.cxc_fac_rev_cob_id "
            +"WHERE cxc_fac_rev_cob.folio='"+folio_fac_rev_cobro+"' "
            +"AND erp_h_facturas.empresa_id="+id_empresa+" "
            +"AND erp_h_facturas.enviado=FALSE "
            + "order by factura asc"; 
            
        //log.log(Level.INFO, "Ejecutando query de {0}", sql_to_query);
        //System.out.println("Obteniendo fac_rev_cobro:::   "+sql_to_query);
        ArrayList<HashMap<String, String>> hm_facturas_rev_cobro = (ArrayList<HashMap<String, String>>) this.jdbcTemplate.query(
            sql_to_query,
            new Object[]{}, new RowMapper(){
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    HashMap<String, String> row = new HashMap<String, String>();
                    row.put("folio_programacion",rs.getString("folio_programacion"));
                    row.put("fac_rev_cob_detalle_id",String.valueOf(rs.getInt("fac_rev_cob_detalle_id")));
                    row.put("erp_h_fac_id",String.valueOf(rs.getInt("erp_h_fac_id")));
                    row.put("factura",rs.getString("factura"));
                    row.put("fecha_factura",rs.getString("fecha_factura"));
                    row.put("cliente",rs.getString("cliente"));
                    row.put("saldo_factura",rs.getString("saldo_factura"));
                    row.put("revision_cobro",rs.getString("revision_cobro"));
                    row.put("estatus_revision",rs.getString("estatus_revision"));
                    return row;
                }
            }
        );
        return hm_facturas_rev_cobro;
    }
    
    
    
    
    
    //obtiene datos de la ruta para ver detalles y editar
    @Override
    public ArrayList<HashMap<String, String>> getdatos_editar_header(Integer id) {
        String sql_to_query = ""
                + "SELECT  "
                    + "log_vehiculos.id as id_vehiculo,  "
                    + "log_vehiculos.marca,  "
                    + "log_vehiculos.numero_economico, "
                    + "log_choferes.id as id_chofer,  "
                    + "log_choferes.nombre||' '||log_choferes.apellido_paterno||' '||  log_choferes.apellido_materno as nombre_chofer,  "
                    + "log_rutas.id as id_ruta,  "
                    + "log_rutas.folio, "
                    + "log_rutas.confirmado "
                + "FROM log_rutas "
                + "join log_choferes on log_choferes.id=log_rutas.log_chofer_id "
                + "join log_vehiculos on log_vehiculos.id=log_rutas.log_vehiculo_id "
                + "where log_rutas.id="+id;
        
        //log.log(Level.INFO, "Ejecutando query de {0}", sql_to_query);
        //System.out.println("Onteniedo datos header:::   "+sql_to_query);
        ArrayList<HashMap<String, String>> hm_header = (ArrayList<HashMap<String, String>>) this.jdbcTemplate.query(
            sql_to_query,
            new Object[]{}, new RowMapper(){
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    HashMap<String, String> row = new HashMap<String, String>();
                    row.put("id_vehiculo",String.valueOf(rs.getInt("id_vehiculo")));
                    row.put("marca",rs.getString("marca"));
                    row.put("numero_economico",rs.getString("numero_economico"));
                    row.put("id_chofer",String.valueOf(rs.getInt("id_chofer")));
                    row.put("nombre_chofer",rs.getString("nombre_chofer"));
                    row.put("id_ruta",String.valueOf(rs.getInt("id_ruta")));
                    row.put("folio",rs.getString("folio"));
                    row.put("confirmado",String.valueOf(rs.getBoolean("confirmado")));
                    
                    return row;
                }
            }
        );
        return hm_header;
    }
    
    
    
    
    
    @Override
    public ArrayList<HashMap<String, String>> getdatos_editar_minigridRutas(Integer id_empresa,Integer id_ruta) {
             
                String sql_to_query = ""
                        + "SELECT "
                            + "log_rutas_detalle.id AS id_detalle,"
                            + "inv_prod.id as id_invprod, fac_docs.id as id_fac_docs,  "
                            + "fac_docs.serie_folio as factura, "
                            + "to_char(fac_docs.momento_creacion,'dd/mm/yyyy') as fecha_factura, "
                            + "cxc_clie.razon_social as cliente, "
                            + "inv_prod.sku as codigo, "
                            + "fac_docs_detalles.cantidad, fac_docs_detalles.precio_unitario, "
                            + "inv_prod.descripcion, "
                            + "(fac_docs_detalles.cantidad * fac_docs_detalles.precio_unitario ) as importe,  (case when fac_docs_detalles.enviado=TRUE THEN log_rutas_detalle.envase else '' end) AS envase, "
                            + "fac_docs_detalles.enviado "
                        + "FROM log_rutas_detalle "
                        + "join fac_docs on fac_docs.id=log_rutas_detalle.fac_docs_id  "
                        + "join fac_docs_detalles on fac_docs_detalles.fac_doc_id= fac_docs.id "
                        + "join inv_prod on inv_prod.id=log_rutas_detalle.inv_prod_id  "
                        + "join cxc_clie on cxc_clie.id=fac_docs.cxc_clie_id  "
                        + "AND fac_docs.cancelado= false and log_rutas_detalle.log_ruta_id="+id_ruta+" "
                        + "AND log_rutas_detalle.inv_prod_id=fac_docs_detalles.inv_prod_id "
                        + "ORDER BY log_rutas_detalle.id";
                
        //log.log(Level.INFO, "Ejecutando query de {0}", sql_to_query);
        //System.out.println("Obteniendo Asignadas a Ruta :::"+ sql_to_query);
        ArrayList<HashMap<String, String>> hm_header = (ArrayList<HashMap<String, String>>) this.jdbcTemplate.query(
            sql_to_query,
            new Object[]{}, new RowMapper(){
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    HashMap<String, String> row = new HashMap<String, String>();
                    row.put("id_detalle",String.valueOf(rs.getInt("id_detalle")));
                    row.put("id_invprod",String.valueOf(rs.getInt("id_invprod")));
                    row.put("id_fac_docs",String.valueOf(rs.getInt("id_fac_docs")));
                    row.put("factura",rs.getString("factura"));
                    row.put("fecha_factura",rs.getString("fecha_factura"));
                    row.put("cliente",rs.getString("cliente"));
                    row.put("codigo",rs.getString("codigo"));
                    row.put("cantidad",rs.getString("cantidad"));
                    row.put("precio_unitario",rs.getString("precio_unitario"));
                    row.put("descripcion",rs.getString("descripcion"));
                    row.put("importe",rs.getString("importe"));
                    row.put("envase",rs.getString("envase"));
                    row.put("enviado",String.valueOf(rs.getBoolean("enviado")));
                    
                    return row;
                }
            }
        );
        return hm_header;
    }
    
    
    @Override
    public ArrayList<HashMap<String, String>> getdatos_editar_minigridFRC(Integer id_ruta) {
        
        String sql_to_query = ""
                + "SELECT "
                    + "cxc_fac_rev_cob.folio as folio_programacion, "
                    + "log_rutas_detalle_cobro.id AS id_detalle,"
                    + "cxc_fac_rev_cob_detalle.id AS fac_rev_cob_detalle_id,  "
                    + "erp_h_facturas.id AS erp_h_fac_id, "
                    + "erp_h_facturas.serie_folio as factura, "
                    + "to_char(erp_h_facturas.momento_facturacion,'dd/mm/yyyy') as fecha_factura,  "
                    + "cxc_clie.razon_social AS cliente,  "
                    + "erp_h_facturas.saldo_factura,  "
                    + "(case when  erp_h_facturas.estatus_revision=1 THEN 'REVISION' WHEN  erp_h_facturas.estatus_revision=2 THEN 'COBRO' else '' end) AS revision_cobro, "
                    + "erp_h_facturas.enviado "
                + "FROM log_rutas_detalle_cobro  "
                + "JOIN cxc_fac_rev_cob_detalle on cxc_fac_rev_cob_detalle.id=log_rutas_detalle_cobro.cxc_fac_rev_cob_detalle_id   "
                + "JOIN erp_h_facturas on erp_h_facturas.id=cxc_fac_rev_cob_detalle.erp_h_facturas_id "
                + "JOIN cxc_clie on cxc_clie.id=erp_h_facturas.cliente_id  "
                + "join cxc_fac_rev_cob on cxc_fac_rev_cob.id=cxc_fac_rev_cob_detalle.cxc_fac_rev_cob_id  "
                + "WHERE log_rutas_detalle_cobro.log_ruta_id="+id_ruta+" "
                + "ORDER BY log_rutas_detalle_cobro.id;";
        
        //log.log(Level.INFO, "Ejecutando query de {0}", sql_to_query);
        //System.out.println("Obteniendo datos de facturas para enviar a revision y cobro :::"+ sql_to_query);
        ArrayList<HashMap<String, String>> hm_header = (ArrayList<HashMap<String, String>>) this.jdbcTemplate.query(
            sql_to_query,
            new Object[]{}, new RowMapper(){
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    HashMap<String, String> row = new HashMap<String, String>();
                    row.put("folio_programacion",rs.getString("folio_programacion"));
                    row.put("id_detalle",String.valueOf(rs.getInt("id_detalle")));
                    row.put("fac_rev_cob_detalle_id",String.valueOf(rs.getInt("fac_rev_cob_detalle_id")));
                    row.put("erp_h_fac_id",String.valueOf(rs.getInt("erp_h_fac_id")));
                    row.put("factura",rs.getString("factura"));
                    row.put("fecha_factura",rs.getString("fecha_factura"));
                    row.put("cliente",rs.getString("cliente"));
                    row.put("saldo_factura",rs.getString("saldo_factura"));
                    row.put("revision_cobro",rs.getString("revision_cobro"));
                    row.put("enviado",String.valueOf(rs.getBoolean("enviado")));
                    return row;
                }
            }
        );
        return hm_header;
    }
    
    
    
    
    
    //metoodo para pdf de ruta
    @Override
    public HashMap<String, String> getRuta_DatosPdf(Integer id_ruta) {
        
        HashMap<String, String> datos = new HashMap<String, String>();
        
        String sql_query = ""
                + "SELECT "
                    + "folio,"
                    + "fecha,"
                    + "(case when num_mes=1 THEN 'Ene'	WHEN num_mes=2 THEN 'Feb' WHEN num_mes=3 THEN 'Mar' WHEN num_mes=4 THEN 'Abr' WHEN num_mes=5 THEN 'May' WHEN num_mes=6 THEN 'Jun' WHEN num_mes=7 THEN 'Jul' WHEN num_mes=8 THEN 'Ago' WHEN num_mes=9 THEN 'Sep' WHEN num_mes=10 THEN 'Oct' WHEN num_mes=11 THEN 'Nov' WHEN num_mes=12 THEN 'Dic' else '' END ) AS nombre_mes, "
                    + "clave_chofer, "
                    + "nombre_chofer, "
                    + "marca_vehiculo "
                + "FROM ( "
                    + "SELECT  "
                        + "log_rutas.folio, "
                        + "to_char(log_rutas.momento_creacion,'dd-mm-yyyy') AS fecha, "
                        + "EXTRACT(MONTH FROM log_rutas.momento_creacion) AS num_mes, "
                        + "log_choferes.clave AS clave_chofer,"
                        + "log_choferes.nombre||' '||log_choferes.apellido_paterno||' '||log_choferes.apellido_materno AS nombre_chofer, "
                        + "log_vehiculos.marca AS marca_vehiculo "
                    + "FROM log_rutas "
                    + "JOIN log_choferes on log_choferes.id=log_rutas.log_chofer_id "
                    + "JOIN log_vehiculos on log_vehiculos.id=log_rutas.log_vehiculo_id "
                    + "WHERE log_rutas.id="+id_ruta
                + ")AS sbt";
        
        //System.out.println("DATOS PARA EL PDF:"+sql_query);
        Map<String, Object> map = this.getJdbcTemplate().queryForMap(sql_query);
        
        datos.put("folio", map.get("folio").toString());
        datos.put("fecha", map.get("fecha").toString());
        datos.put("nombre_mes", map.get("nombre_mes").toString() );
        datos.put("clave_chofer", map.get("clave_chofer").toString() );
        datos.put("nombre_chofer", map.get("nombre_chofer").toString() );
        datos.put("clave_vehiculo", "" );
        datos.put("marca_vehiculo", map.get("marca_vehiculo").toString() );
        datos.put("hora_salida", "" );
        datos.put("hora_llegada", "" );
        
        return datos;
    }
    
    
    
    
    
    @Override
    public ArrayList<HashMap<String, String>> getRuta_ListaFacturasPdf(Integer id_ruta) {
        String sql_query = ""
            + "SELECT * "
            + "FROM ("
                + "SELECT 'Material'::character varying  AS tipo,"
                    + "1::integer as numero_tipo, "
                    + "fac_docs.serie_folio AS factura,"
                    + "cxc_clie.numero_control AS no_cliente,"
                    + "cxc_clie.razon_social AS cliente,"
                    + "fac_docs_detalles.cantidad,"
                    + "inv_prod_unidades.titulo AS unidad,"
                    + "inv_prod.descripcion,"
                    + "(fac_docs_detalles.cantidad*fac_docs_detalles.precio_unitario) AS importe,"
                    + "log_rutas_detalle.envase, "
                    + "gral_mon.id AS moneda_id, "
                    + "gral_mon.simbolo AS moneda "
                + "FROM log_rutas_detalle "
                + "JOIN fac_docs on fac_docs.id=log_rutas_detalle.fac_docs_id "
                + "JOIN fac_docs_detalles on fac_docs_detalles.fac_doc_id=fac_docs.id "
                + "JOIN inv_prod on inv_prod.id=log_rutas_detalle.inv_prod_id "
                + "JOIN inv_prod_unidades on inv_prod_unidades.id=inv_prod.unidad_id "
                + "JOIN cxc_clie on cxc_clie.id=fac_docs.cxc_clie_id "
                + "JOIN gral_mon on gral_mon.id=fac_docs.moneda_id "
                + "WHERE fac_docs_detalles.inv_prod_id=log_rutas_detalle.inv_prod_id AND log_rutas_detalle.log_ruta_id="+id_ruta+" "
                + " "
                + "UNION "
                + " "
                + "SELECT (case when cxc_fac_rev_cob_detalle.revision_cobro='R' THEN 'Revision' else 'Cobro' end) AS tipo, "
                    + "2::integer as numero_tipo, "
                    + "erp_h_facturas.serie_folio AS factura, "
                    + "cxc_clie.numero_control AS no_cliente, "
                    + "cxc_clie.razon_social AS cliente, "
                    + "0::double precision as cantidad, "
                    + "''::character varying AS unidad, "
                    + "''::character varying AS descripcion, "
                    + "erp_h_facturas.saldo_factura AS importe, "
                    + "''::character varying AS envase, "
                    + "gral_mon.id AS moneda_id, "
                    + "gral_mon.simbolo AS moneda "
                + "FROM log_rutas_detalle_cobro "
                + "JOIN cxc_fac_rev_cob_detalle on cxc_fac_rev_cob_detalle.id=log_rutas_detalle_cobro.cxc_fac_rev_cob_detalle_id "
                + "JOIN erp_h_facturas on erp_h_facturas.id=cxc_fac_rev_cob_detalle.erp_h_facturas_id "
                + "JOIN cxc_clie on cxc_clie.id=erp_h_facturas.cliente_id "
                + "JOIN gral_mon on gral_mon.id=erp_h_facturas.moneda_id "
                + "WHERE log_rutas_detalle_cobro.log_ruta_id="+id_ruta +" "
            + ") AS sbt "
            + "ORDER BY numero_tipo, factura";
        
        //System.out.println("Obtiene datos pdf ruta: "+sql_query);
        ArrayList<HashMap<String, String>> hm_grid = (ArrayList<HashMap<String, String>>) this.jdbcTemplate.query(
            sql_query,  
            new Object[]{}, new RowMapper() {
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    HashMap<String, String> row = new HashMap<String, String>();
                    row.put("tipo",rs.getString("tipo"));
                    row.put("factura",rs.getString("factura"));
                    row.put("no_cliente",rs.getString("no_cliente"));
                    row.put("cliente",rs.getString("cliente"));
                    
                    row.put("cantidad",StringHelper.roundDouble(rs.getString("cantidad"),2));
                    
                    row.put("unidad",rs.getString("unidad"));
                    row.put("descripcion",rs.getString("descripcion"));
                    row.put("importe",StringHelper.roundDouble(rs.getDouble("importe"),2) );
                    row.put("envase",rs.getString("envase"));
                    row.put("moneda_id",String.valueOf(rs.getInt("moneda_id")));
                    row.put("moneda",rs.getString("moneda"));
                    row.put("aprobado","");
                    row.put("entregado","");
                    
                    return row;
                }
            }
        );
        return hm_grid;
    }
    
    
    
    
    //Obtiene datos de la Unidad(Vehiculo)
    @Override
    public ArrayList<HashMap<String, String>> getUnidades_Datos(Integer id) {
        String sql_to_query = ""
        + "select "
            + "log_vehiculos.id, "
            + "log_vehiculos.folio, "
            + "log_vehiculos.anio, "
            + "log_vehiculos.color, "
            + "log_vehiculos.placa, "
            + "log_vehiculos.numero_economico, "
            + "log_vehiculos.numero_serie, "
            + "log_vehiculos.cap_volumen, "
            + "log_vehiculos.cap_peso, "
            + "log_vehiculos.comentarios, "
            + "log_vehiculos.log_vehiculo_tipo_id, "
            + "log_vehiculos.log_vehiculo_clase_id, "
            + "log_vehiculos.log_vehiculo_marca_id, "
            + "log_vehiculos.log_vehiculo_tipo_placa_id, "
            + "log_vehiculos.log_vehiculo_tipo_caja_id, "
            + "log_vehiculos.log_vehiculo_tipo_rodada_id, "
            + "log_vehiculos.clasificacion2, "
            + "(case when cxp_prov.id is null then 0 else cxp_prov.id end) as prov_id, "
            + "(case when cxp_prov.id is null then '' else cxp_prov.folio end) as no_prov, "
            + "(case when cxp_prov.id is null then '' else cxp_prov.razon_social end) as proveedor, "
            + "(case when log_choferes.id is null then 0 else log_choferes.id end) as operador_id, "
            + "(case when log_choferes.id is null then '' else log_choferes.clave end) as no_operador, "
            + "(case when log_choferes.id is null then '' else (case when nombre is null then '' else nombre end)||' '||(case when apellido_paterno is null then  '' else apellido_paterno end)||' '||(case when apellido_materno is null then '' else apellido_materno end) end) as operador "
        + "from log_vehiculos "
        + "left join cxp_prov on cxp_prov.id=log_vehiculos.cxp_prov_id "
        + "left join log_choferes on log_choferes.id=log_vehiculos.log_chofer_id "
        + "where log_vehiculos.id=?";
        
        ArrayList<HashMap<String, String>> dato_vehiculo = (ArrayList<HashMap<String, String>>) this.jdbcTemplate.query(
            sql_to_query,
            new Object[]{new Integer(id)}, new RowMapper(){
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    HashMap<String, String> row = new HashMap<String, String>();
                    row.put("id",String.valueOf(rs.getInt("id")));
                    row.put("folio",rs.getString("folio"));
                    row.put("anio",rs.getString("anio"));
                    row.put("color",rs.getString("color"));
                    row.put("placa",rs.getString("placa"));
                    row.put("numero_economico",rs.getString("numero_economico"));
                    row.put("numero_serie",rs.getString("numero_serie"));
                    row.put("cap_volumen",StringHelper.roundDouble(rs.getString("cap_volumen"),3));
                    row.put("cap_peso",StringHelper.roundDouble(rs.getString("cap_peso"),3));
                    row.put("comentarios",rs.getString("comentarios"));
                    row.put("tipo_id",String.valueOf(rs.getInt("log_vehiculo_tipo_id")));
                    row.put("clase_id",String.valueOf(rs.getInt("log_vehiculo_clase_id")));
                    row.put("marca_id",String.valueOf(rs.getInt("log_vehiculo_marca_id")));
                    row.put("tplaca_id",String.valueOf(rs.getInt("log_vehiculo_tipo_placa_id")));
                    row.put("tcaja_id",String.valueOf(rs.getInt("log_vehiculo_tipo_caja_id")));
                    row.put("trodada_id",String.valueOf(rs.getInt("log_vehiculo_tipo_rodada_id")));
                    row.put("clasificacion2",String.valueOf(rs.getInt("clasificacion2")));
                    row.put("prov_id",String.valueOf(rs.getInt("prov_id")));
                    row.put("no_prov",rs.getString("no_prov"));
                    row.put("proveedor",rs.getString("proveedor"));
                    row.put("operador_id",String.valueOf(rs.getInt("operador_id")));
                    row.put("no_operador",rs.getString("no_operador"));
                    row.put("operador",rs.getString("operador"));
                    return row;
                }
            }
        );
        return dato_vehiculo;
    }
    
    
    
    @Override
    public ArrayList<HashMap<String, Object>> getUnidades_PaginaGrid(String data_string, int offset, int pageSize, String orderBy, String asc) {
        String sql_busqueda = "select id from gral_bus_catalogos(?) as foo (id integer)";
        
	String sql_to_query = ""
        + "SELECT "
            + "log_vehiculos.id, "
            + "log_vehiculos.folio, "
            + "(case when log_vehiculo_marca.id is null then '' else log_vehiculo_marca.titulo end) as marca, "
            + "log_vehiculos.anio, "
            + "(case when log_vehiculo_tipo_caja.id is null then '' else log_vehiculo_tipo_caja.titulo end ) as tipo_caja, "
            + "(case when log_vehiculo_tipo.id is null then '' else log_vehiculo_tipo.titulo end ) as tipo_unidad, "
            + "(case when log_vehiculo_clase.id is null then '' else log_vehiculo_clase.titulo end) as clase, "
            + "log_vehiculos.cap_volumen, "
            + "log_vehiculos.cap_peso,"
            + "(case when cxp_prov.id is null then '' else cxp_prov.razon_social end) as transportista "
        +"FROM log_vehiculos "
        + "left join log_vehiculo_marca on log_vehiculo_marca.id=log_vehiculos.log_vehiculo_marca_id "
        + "left join log_vehiculo_tipo_caja on log_vehiculo_tipo_caja.id=log_vehiculos.log_vehiculo_tipo_caja_id "
        + "left join log_vehiculo_tipo on log_vehiculo_tipo.id=log_vehiculos.log_vehiculo_tipo_id "
        + "left join log_vehiculo_clase on log_vehiculo_clase.id=log_vehiculos.log_vehiculo_clase_id "
        + "left join cxp_prov on cxp_prov.id=log_vehiculos.cxp_prov_id "
        +"JOIN ("+sql_busqueda+") AS sbt on sbt.id = log_vehiculos.id "
        +"WHERE log_vehiculos.borrado_logico=false "
        +"order by "+orderBy+" "+asc+" limit ? OFFSET ?";
        
        //System.out.println("Busqueda GetPage: "+sql_to_query+" "+data_string+" "+ offset +" "+ pageSize);
        //System.out.println("esto es el query  :  "+sql_to_query);
        ArrayList<HashMap<String, Object>> hm = (ArrayList<HashMap<String, Object>>) this.jdbcTemplate.query(
            sql_to_query, 
            new Object[]{data_string, new Integer(pageSize),new Integer(offset)}, new RowMapper() {
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    HashMap<String, Object> row = new HashMap<String, Object>();
                    row.put("id",rs.getInt("id"));
                    row.put("folio",rs.getString("folio"));
                    row.put("marca",rs.getString("marca"));
                    row.put("anio",rs.getString("anio"));
                    row.put("tipo_unidad",rs.getString("tipo_unidad"));
                    row.put("tipo_caja",rs.getString("tipo_caja"));
                    row.put("clase",rs.getString("clase"));
                    row.put("cap_volumen",StringHelper.roundDouble(rs.getString("cap_volumen"),3));
                    row.put("cap_peso",StringHelper.roundDouble(rs.getString("cap_peso"),3));
                    row.put("transportista",rs.getString("transportista"));
                    return row;
                }
            }
        );
        return hm; 
    }

    
    
    
    
    //Obtiene todas las marcas de unidades de la empresa
    @Override
    public ArrayList<HashMap<String, Object>> getUnidades_Marcas(Integer idEmp) {
        
        String sql_to_query = "SELECT distinct id, titulo FROM log_vehiculo_marca WHERE gral_emp_id=? AND borrado_logico=false;"; 
        ArrayList<HashMap<String, Object>> hm = (ArrayList<HashMap<String, Object>>) this.jdbcTemplate.query(
            sql_to_query,
            new Object[]{new Integer(idEmp)}, new RowMapper(){
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
    
    
    //Obtiene Tipos de unidades de la empresa
    @Override
    public ArrayList<HashMap<String, Object>> getUnidades_Tipos(Integer idEmp) {
        
        String sql_to_query = "SELECT distinct id, titulo FROM log_vehiculo_tipo WHERE gral_emp_id=? AND borrado_logico=false order by titulo;"; 
        ArrayList<HashMap<String, Object>> hm = (ArrayList<HashMap<String, Object>>) this.jdbcTemplate.query(
            sql_to_query,
            new Object[]{new Integer(idEmp)}, new RowMapper(){
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
    
    
    
    //Obtiene las Clases deunidades de la empresa
    @Override
    public ArrayList<HashMap<String, Object>> getUnidades_Clases(Integer idEmp) {
        
        String sql_to_query = "SELECT distinct id, titulo FROM log_vehiculo_clase WHERE gral_emp_id=? AND borrado_logico=false order by titulo;"; 
        ArrayList<HashMap<String, Object>> hm = (ArrayList<HashMap<String, Object>>) this.jdbcTemplate.query(
            sql_to_query,
            new Object[]{new Integer(idEmp)}, new RowMapper(){
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
    
    
    
    //Obtiene los Tipos de placas de la empresa
    @Override
    public ArrayList<HashMap<String, Object>> getUnidades_TiposPlaca(Integer idEmp) {
        
        String sql_to_query = "SELECT distinct id, titulo FROM log_vehiculo_tipo_placa WHERE gral_emp_id=? AND borrado_logico=false;"; 
        ArrayList<HashMap<String, Object>> hm = (ArrayList<HashMap<String, Object>>) this.jdbcTemplate.query(
            sql_to_query,
            new Object[]{new Integer(idEmp)}, new RowMapper(){
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
    
    
    //Obtiene los Tipos de Rodada para las unidades de la empresa
    @Override
    public ArrayList<HashMap<String, Object>> getUnidades_TiposRodada(Integer idEmp) {
        
        String sql_to_query = "SELECT distinct id, titulo FROM log_vehiculo_tipo_rodada WHERE gral_emp_id=? AND borrado_logico=false order by titulo;"; 
        ArrayList<HashMap<String, Object>> hm = (ArrayList<HashMap<String, Object>>) this.jdbcTemplate.query(
            sql_to_query,
            new Object[]{new Integer(idEmp)}, new RowMapper(){
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
    
    
    
    //Obtiene los Tipos de Caja para las unidades de la empresa
    @Override
    public ArrayList<HashMap<String, Object>> getUnidades_TiposCaja(Integer idEmp) {
        
        String sql_to_query = "SELECT distinct id, titulo FROM log_vehiculo_tipo_caja WHERE gral_emp_id=? AND borrado_logico=false order by titulo;"; 
        ArrayList<HashMap<String, Object>> hm = (ArrayList<HashMap<String, Object>>) this.jdbcTemplate.query(
            sql_to_query,
            new Object[]{new Integer(idEmp)}, new RowMapper(){
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
    
    
    
    //Calcular numero de años a mostrar en formulario de Unidades(Vehiculos)
    @Override
    public ArrayList<HashMap<String, Object>>  getUnidades_AniosUnidad() {
        ArrayList<HashMap<String, Object>> anios = new ArrayList<HashMap<String, Object>>();
        
        Calendar c1 = Calendar.getInstance();
        Integer annio = c1.get(Calendar.YEAR);//obtiene el año actual
        
        for(int i=0; i<40; i++) {
            HashMap<String, Object> row = new HashMap<String, Object>();
            row.put("valor",(annio-i));
            anios.add(i, row);
        }
        return anios;
    }
    
    
    
    
    //obtiene datos para el buscador de proveedores
    @Override
    public ArrayList<HashMap<String, Object>> getBuscadorProveedores(String rfc, String no_proveedor, String razon_social, String transportista, Integer id_empresa) {
        String where = "";
	if(!rfc.equals("")){
            where +=" AND cxp_prov.rfc ILIKE '%"+rfc.toUpperCase()+"%'";
	}
        
	if(!no_proveedor.equals("")){
            where +=" AND cxp_prov.folio ILIKE '%"+no_proveedor.toUpperCase()+"%'";
	}
        
	if(!razon_social.equals("")){
            where +=" AND (cxp_prov.razon_social ilike '%"+razon_social.toUpperCase()+"%' OR cxp_prov.clave_comercial ilike '%"+razon_social.toUpperCase()+"%')";
	}
        
        if(transportista.toLowerCase().trim().equals("true")){
            where +=" AND cxp_prov.transportista=true";
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
                + "(case when gral_imptos.iva_1 is null then 0 else gral_imptos.iva_1 end) AS valor_impuesto "
            + "FROM cxp_prov "
            + "JOIN gral_pais on gral_pais.id = cxp_prov.pais_id "
            + "JOIN gral_edo on gral_edo.id = cxp_prov.estado_id "
            + "JOIN gral_mun on gral_mun.id = cxp_prov.municipio_id  "
            + "left join gral_imptos on gral_imptos.id=cxp_prov.impuesto "
            + "WHERE empresa_id=? AND cxp_prov.borrado_logico = false "+ where +";";
        
        //log.log(Level.INFO, "Ejecutando query de {0}", sql_to_query);
        
        ArrayList<HashMap<String, Object>> hm_datos_proveedor = (ArrayList<HashMap<String, Object>>) this.jdbcTemplate.query(
            sql_to_query,
            new Object[]{new Integer(id_empresa)}, new RowMapper(){
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    HashMap<String, Object> row = new HashMap<String, Object>();
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
    
    
    
    //Obtiene datos del Proveedor a partir del Número de Proveedor
    @Override
    public ArrayList<HashMap<String, Object>> getDatosProveedorByNoProv(String numeroProveedor, String transportista, Integer id_empresa) {
        String where = "";
        
        if(transportista.toLowerCase().trim().equals("true")){
            where +=" AND cxp_prov.transportista=true";
        }
        
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
                    + "(case when gral_imptos.iva_1 is null then 0 else gral_imptos.iva_1 end) AS valor_impuesto "
                + "FROM cxp_prov "
                + "JOIN gral_pais on gral_pais.id = cxp_prov.pais_id "
                + "JOIN gral_edo on gral_edo.id = cxp_prov.estado_id "
                + "JOIN gral_mun on gral_mun.id = cxp_prov.municipio_id  "
                + "left join gral_imptos on gral_imptos.id=cxp_prov.impuesto "
                + "WHERE empresa_id=? "+ where +" AND cxp_prov.borrado_logico=false AND cxp_prov.folio='"+numeroProveedor.toUpperCase()+"';";
        
        //log.log(Level.INFO, "Ejecutando query de {0}", sql_to_query);
        
        ArrayList<HashMap<String, Object>> hm = (ArrayList<HashMap<String, Object>>) this.jdbcTemplate.query(
            sql_to_query,
            new Object[]{new Integer(id_empresa)}, new RowMapper(){
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    HashMap<String, Object> row = new HashMap<String, Object>();
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
    
    
    
    //Buscador de Operadores(Choferes)
    @Override
    public ArrayList<HashMap<String, Object>> getBuscadorOperadores(String no_operador, String nombre, Integer id_proveedor, Integer id_empresa, Integer id_sucursal) {
        String where="";
        if(id_sucursal!=0){
            where = " AND sbt.gral_suc_id="+id_sucursal;
        }
        
        if(id_proveedor!=0){
            where = " AND sbt.cxp_prov_id="+id_proveedor;
        }
        
	String sql_query = ""
        + "SELECT * FROM ( "
            + "SELECT  id, clave, (case when nombre is null then '' else nombre end)||' '||(case when apellido_paterno is null then '' else apellido_paterno end)||' '||(case when apellido_materno is null then '' else apellido_materno end) AS nombre, cxp_prov_id, gral_emp_id, gral_suc_id, borrado_logico "
            + "FROM log_choferes"
        + ") AS sbt "
        + "WHERE sbt.clave ILIKE '%"+no_operador+"%' "
        + "AND replace(upper(sbt.nombre),' ', '') ilike replace('%"+nombre.toUpperCase()+"%', ' ', '')"
        + "AND sbt.gral_emp_id=? AND sbt.borrado_logico=false "+where+";";
        
        System.out.println("getBuscadorOperadores: "+sql_query);
        ArrayList<HashMap<String, Object>> hm = (ArrayList<HashMap<String, Object>>) this.jdbcTemplate.query(
            sql_query,
            new Object[]{new Integer(id_empresa)}, new RowMapper() {
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    HashMap<String, Object> row = new HashMap<String, Object>();
                    row.put("id",String.valueOf(rs.getInt("id")));
                    row.put("clave",rs.getString("clave"));
                    row.put("nombre",rs.getString("nombre"));
                    row.put("prov_id",rs.getInt("cxp_prov_id"));
                    return row;
                }
            }
        );
        return hm;
    }
    
    
    //obtener datos del Operador a partir de la clave
    @Override
    public ArrayList<HashMap<String, Object>> getDatosOperadorByNo(String no_operador, Integer id_proveedor, Integer id_empresa, Integer id_sucursal) {
        String where="";
        if(id_sucursal!=0){
            where = " AND sbt.gral_suc_id="+id_sucursal;
        }
        
        if(id_proveedor!=0){
            where = " AND sbt.cxp_prov_id="+id_proveedor;
        }
        
	String sql_query = ""
        + "SELECT * FROM ( "
            + "SELECT  id, clave, (case when nombre is null then '' else nombre end)||' '||(case when apellido_paterno is null then '' else apellido_paterno end)||' '||(case when apellido_materno is null then '' else apellido_materno end) AS nombre, cxp_prov_id, gral_emp_id, gral_suc_id, borrado_logico "
            + "FROM log_choferes"
        + ") AS sbt "
        + "WHERE upper(sbt.clave)='"+no_operador.toUpperCase().trim()+"' AND sbt.gral_emp_id=? AND sbt.borrado_logico=false "+where+" LIMIT 1;";
        
        //System.out.println("getBuscadorOperador: "+sql_query);
        ArrayList<HashMap<String, Object>> hm = (ArrayList<HashMap<String, Object>>) this.jdbcTemplate.query(
            sql_query,
            new Object[]{new Integer(id_empresa)}, new RowMapper() {
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    HashMap<String, Object> row = new HashMap<String, Object>();
                    row.put("id",rs.getInt("id"));
                    row.put("clave",rs.getString("clave"));
                    row.put("nombre",rs.getString("nombre"));
                    row.put("prov_id",rs.getInt("cxp_prov_id"));
                    return row;
                }
            }
        );
        return hm;    
    }
    
    
    
    
    
    
    //Obtiene datos para el gri del Catalogo de Operadores
    @Override
    public ArrayList<HashMap<String, Object>> getOperadores_PaginaGrid(String data_string, int offset, int pageSize, String orderBy, String asc) {
        String sql_busqueda = "select id from gral_bus_catalogos(?) as foo (id integer)";
        
        String sql_to_query = ""
                + "SELECT log_choferes.id, "
                + "log_choferes.clave as numero_control, "
                + "log_choferes.nombre  || ' ' || case when log_choferes.apellido_paterno is null then '' else log_choferes.apellido_paterno END || ' ' || case when log_choferes.apellido_materno is null then '' else log_choferes.apellido_materno END AS nombre,"
                + "(case when cxp_prov.id is null then '' else cxp_prov.razon_social end) as transportista "
                + "FROM log_choferes "
                + "left join cxp_prov on cxp_prov.id=log_choferes.cxp_prov_id "                       
                +"JOIN ("+sql_busqueda+") AS sbt on sbt.id = log_choferes.id "
                +"WHERE log_choferes.borrado_logico=false "
                +"order by "+orderBy+" "+asc+" limit ? OFFSET ?";
        
        //System.out.println("Busqueda GetPage: "+sql_to_query+" "+data_string+" "+ offset +" "+ pageSize);
        //System.out.println("esto es el query  :  "+sql_to_query);
        ArrayList<HashMap<String, Object>> hm = (ArrayList<HashMap<String, Object>>) this.jdbcTemplate.query(
            sql_to_query, 
            new Object[]{data_string, new Integer(pageSize),new Integer(offset)}, new RowMapper() {
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    HashMap<String, Object> row = new HashMap<String, Object>();
                    row.put("id",rs.getInt("id"));
                    row.put("numero_control",rs.getString("numero_control"));
                    row.put("nombre",rs.getString("nombre"));
                    row.put("transportista",rs.getString("transportista"));
                   
                    return row;
                }
            }
        );
        return hm; 
    }

    


    ///Obtiene datos de operadores
    @Override
    public ArrayList<HashMap<String, String>> getOperadores_Datos(Integer id) {
        
        String sql_to_query = "SELECT id,clave,nombre,apellido_paterno,apellido_materno, cxp_prov_id as trans_id FROM log_choferes WHERE id="+id;
        
        ArrayList<HashMap<String, String>> dato_operador = (ArrayList<HashMap<String, String>>) this.jdbcTemplate.query(
            sql_to_query,
            new Object[]{}, new RowMapper(){
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    HashMap<String, String> row = new HashMap<String, String>();
                    row.put("id",String.valueOf(rs.getInt("id")));
                    row.put("clave",rs.getString("clave"));
                    row.put("nombre",rs.getString("nombre"));
                    row.put("apellido_paterno",rs.getString("apellido_paterno"));
                    row.put("apellido_materno",rs.getString("apellido_materno"));
                    row.put("trans_id",String.valueOf(rs.getInt("trans_id")));
                    return row;
                }
            }
        );
        return dato_operador;
    }
    
    
    
    
    
    //----------------------------------------------------------------------------
    //METODOS PARA CARGA DE DOCUMENTOS
    //----------------------------------------------------------------------------
    
    
    //Metodo que obtiene datos para el grid de Administrador de Viajes
    @Override
    public ArrayList<HashMap<String, Object>> getLogCargaDoc_PaginaGrid(String data_string, int offset, int pageSize, String orderBy, String asc) {
        String sql_busqueda = "select id from gral_bus_catalogos(?) as foo (id integer)";
        
	String sql_to_query = ""
        + "SELECT "
            + "log_doc.id, "
            + "log_doc.folio, "
            + "to_char(log_doc.fecha_carga::timestamp with time zone, 'dd/mm/yyyy') as fecha,  "
            + "cxc_clie.razon_social as cliente, "
            + "inv_alm.titulo as almacen "
        + "FROM log_doc "
        + "JOIN cxc_clie ON cxc_clie.id=log_doc.cxc_clie_id "
        + "JOIN inv_alm ON inv_alm.id=log_doc.inv_alm_id " 
        +"JOIN ("+sql_busqueda+") AS sbt on sbt.id=log_doc.id "
        +"order by "+orderBy+" "+asc+" limit ? OFFSET ?";
        
        //System.out.println("Paginado Viajes: "+sql_to_query);
        ArrayList<HashMap<String, Object>> hm = (ArrayList<HashMap<String, Object>>) this.jdbcTemplate.query(
            sql_to_query, 
            new Object[]{data_string, new Integer(pageSize),new Integer(offset)}, new RowMapper() {
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    HashMap<String, Object> row = new HashMap<String, Object>();
                    row.put("id",rs.getInt("id"));
                    row.put("folio",rs.getString("folio"));
                    row.put("fecha",rs.getString("fecha"));
                    row.put("cliente",rs.getString("cliente"));
                    row.put("almacen",rs.getString("almacen"));
                    return row;
                }
            }
        );
        return hm; 
    }
    
    
    
    //Obtiene datos de la carga de documento
    @Override
    public ArrayList<HashMap<String, Object>> getLogCargaDoc_Datos(Integer id) {
        String sql_to_query = ""
        + "select "
            + "log_doc.id,"
            + "log_doc.folio, "
            + "log_doc.observaciones, "
            + "to_char(log_doc.fecha_carga::timestamp with time zone, 'yyyy-mm-dd') as fecha_carga, "
            + "log_doc.inv_alm_id as alm_id, "
            + "cxc_clie.id as clie_id, "
            + "cxc_clie.numero_control as no_clie, "
            + "cxc_clie.razon_social as nombre_clie,"
            + "log_doc.gral_suc_id as suc_id "
        + "from log_doc "
        + "join cxc_clie on cxc_clie.id=log_doc.cxc_clie_id  "
        + "where log_doc.id=?";
        
        ArrayList<HashMap<String, Object>> hm = (ArrayList<HashMap<String, Object>>) this.jdbcTemplate.query(
            sql_to_query,
            new Object[]{ new Integer(id)}, new RowMapper(){
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    HashMap<String, Object> row = new HashMap<String, Object>();
                    row.put("id",rs.getInt("id"));
                    row.put("folio",rs.getString("folio"));
                    row.put("observaciones",rs.getString("observaciones"));
                    row.put("fecha_carga",rs.getString("fecha_carga"));
                    row.put("alm_id",rs.getInt("alm_id"));
                    row.put("clie_id",rs.getInt("clie_id"));
                    row.put("no_clie",rs.getString("no_clie"));
                    row.put("nombre_clie",rs.getString("nombre_clie"));
                    row.put("suc_id",rs.getInt("suc_id"));
                    return row;
                }
            }
        );
        return hm;
    }
    
    
    //Obtiene datos de unidades y Rutas 
    @Override
    public ArrayList<HashMap<String, Object>> getLogCargaDoc_UnidadesRutas(Integer id) {
        String sql_to_query = ""
        + "select log_doc_vehiculo.id as iddet,log_doc_vehiculo.log_vehiculo_id as id_uni, log_vehiculos.folio as no_uni, (case when log_vehiculo_marca.id is null then '' else log_vehiculo_marca.titulo end) as marca_unidad, (case when log_vehiculo_tipo.id is null then 0 else log_vehiculo_tipo.id end) as t_unidad_id, (case when log_vehiculo_tipo.id is null then '' else log_vehiculo_tipo.titulo end) as t_unidad, (case when log_vehiculo_clase.id is null then 0 else log_vehiculo_clase.id end) as clase_id, (case when log_vehiculo_clase.id is null then '' else log_vehiculo_clase.titulo end) as clase_unidad, (case when log_ruta.id is null then 0 else log_ruta.id end) as ruta_id, (case when log_ruta.id is null then '' else log_ruta.folio end) as no_ruta, (case when log_ruta.id is null then '' else log_ruta.titulo end) as titulo_ruta, (case when log_ruta.id is null then 0 else log_ruta.km end) as km_ruta, log_doc_vehiculo.costo as costo_ruta, log_doc_vehiculo.cant_uni,log_doc_vehiculo.peso,log_doc_vehiculo.volumen "
        + "from log_doc_vehiculo "
        + "join log_vehiculos on log_vehiculos.id=log_doc_vehiculo.log_vehiculo_id left join log_vehiculo_tipo on log_vehiculo_tipo.id=log_doc_vehiculo.log_vehiculo_tipo_id  left join log_vehiculo_marca on log_vehiculo_marca.id=log_vehiculos.log_vehiculo_marca_id left join log_vehiculo_clase on log_vehiculo_clase.id=log_vehiculos.log_vehiculo_clase_id left join log_ruta on log_ruta.id=log_doc_vehiculo.log_ruta_id "
        + "where log_doc_vehiculo.log_doc_id=?";
        
        ArrayList<HashMap<String, Object>> hm = (ArrayList<HashMap<String, Object>>) this.jdbcTemplate.query(
            sql_to_query,
            new Object[]{ new Integer(id)}, new RowMapper(){
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    HashMap<String, Object> row = new HashMap<String, Object>();
                    row.put("iddet",rs.getInt("iddet"));
                    row.put("id_uni",rs.getInt("id_uni"));
                    row.put("no_uni",rs.getString("no_uni"));
                    row.put("marca_unidad",rs.getString("marca_unidad"));
                    row.put("t_unidad_id",rs.getInt("t_unidad_id"));
                    row.put("t_unidad",rs.getString("t_unidad"));
                    row.put("clase_id",rs.getInt("clase_id"));
                    row.put("clase_unidad",rs.getString("clase_unidad"));
                    row.put("ruta_id",rs.getInt("ruta_id"));
                    row.put("no_ruta",rs.getString("no_ruta"));
                    row.put("titulo_ruta",rs.getString("titulo_ruta"));
                    row.put("km_ruta",StringHelper.roundDouble(rs.getString("km_ruta"),2));
                    row.put("costo_ruta",StringHelper.roundDouble(rs.getString("costo_ruta"),2));
                    row.put("cant_uni",StringHelper.roundDouble(rs.getString("cant_uni"),2));
                    row.put("peso",StringHelper.roundDouble(rs.getString("peso"),3));
                    row.put("volumen",StringHelper.roundDouble(rs.getString("volumen"),3));
                    return row;
                }
            }
        );
        return hm;
    }
    
    
    @Override
    public HashMap<String, String> getLogCargaDoc_DatosViajePdf(Integer id, Integer id_vehiculo) {
        
        HashMap<String, String> datos = new HashMap<String, String>();
        
        String sql_to_query = ""
        + "SELECT "
            + "log_doc.id, "
            + "log_doc.folio, "
            + "log_doc.fecha_carga as fecha, "
            + "'00:00'::character varying as hora,  "
            + "log_doc.gral_suc_id AS suc_id, "
            + "gral_suc.titulo AS nom_suc, "
            + "log_doc_vehiculo.log_vehiculo_id AS vehiculo_id, "
            + "log_vehiculos.folio AS no_unidad,  "
            + "(case when log_vehiculo_marca.id is null then '' else log_vehiculo_marca.titulo end) as unidad,  "
            + "log_doc_vehiculo.no_economico,  "
            + "log_doc_vehiculo.placas,  "
            + "(case when log_vehiculo_clase.id is null then '' else log_vehiculo_clase.titulo end) AS tipo,  "
            + "(case when log_choferes.clave is null then '' else log_choferes.clave end) AS no_operador, "
            + "(case when log_choferes.nombre is null then '' else log_choferes.nombre end) || ' ' || (case when log_choferes.apellido_paterno is null then '' else log_choferes.apellido_paterno end) || ' ' || case when log_choferes.apellido_materno is null then '' else log_choferes.apellido_materno END AS operador,  "
            + "log_doc.observaciones, "
            + "0::integer as status, "
            + "(case when log_ruta.id is null then 0 else log_ruta.id end) as ruta_id,"
            + "(case when log_ruta.id is null then '' else log_ruta.folio end) as no_ruta,"
            + "(case when log_ruta.id is null then '' else log_ruta.titulo end) as titulo_ruta,"
            + "(case when log_ruta_tipo.id is null then '' else log_ruta_tipo.titulo end) as tipo_ruta,"
            + "(case when log_ruta.id is null then 0 else log_ruta.km end) as km_ruta,"
            //+ "(case when log_ruta.id is null then 0 else log_ruta_tipo_unidad.costo end) as costo_ruta, "
            + "log_doc_vehiculo.costo as costo_ruta, "
            + "inv_alm.titulo as titulo_almacen, "
            + "(case when cxp_prov.id is null then '' else cxp_prov.razon_social end) as tranportista_proveedor "
        + "from log_doc "
        + "join log_doc_vehiculo on log_doc_vehiculo.log_doc_id=log_doc.id "
        + "join log_vehiculos on log_vehiculos.id=log_doc_vehiculo.log_vehiculo_id "
        + "left join log_choferes on log_choferes.id=log_doc_vehiculo.log_chofer_id "
        + "left join log_vehiculo_clase on log_vehiculo_clase.id=log_doc_vehiculo.log_vehiculo_clase_id "
        + "left join log_vehiculo_marca on log_vehiculo_marca.id=log_vehiculos.log_vehiculo_marca_id "
        + "left join gral_suc on gral_suc.id=log_doc.gral_suc_id "
        + "left join log_ruta on log_ruta.id=log_doc_vehiculo.log_ruta_id  "
        + "left join log_ruta_tipo_unidad on (log_ruta_tipo_unidad.log_ruta_id=log_ruta.id and log_ruta_tipo_unidad.log_vehiculo_tipo_id=log_vehiculos.log_vehiculo_tipo_id) "
        + "left join log_ruta_tipo on log_ruta_tipo.id=log_ruta.log_ruta_tipo_id "
        + "left join inv_alm on inv_alm.id=log_doc.inv_alm_id "
        + "left join cxp_prov on cxp_prov.id=log_vehiculos.cxp_prov_id "
        + "WHERE log_doc.id="+id+" and log_vehiculos.id="+id_vehiculo+";";
        
        //System.out.println("DATOS PARA EL PDF:"+sql_to_query);
        Map<String, Object> map = this.getJdbcTemplate().queryForMap(sql_to_query);
       
        //Recorremos el Map con un Iterador
        Iterator it = map.keySet().iterator();
        while(it.hasNext()){
            String key = String.valueOf(it.next());
            if(!key.equals("km_ruta") && !key.equals("costo_ruta")){
                datos.put(key, String.valueOf(map.get(key)));
            }else{
                datos.put(key, StringHelper.roundDouble(String.valueOf(map.get(key)),2));
            }
        }
       
       return datos;
    }
    
    
    
    
      //Obtener el detalle del viaje
    @Override
    public ArrayList<HashMap<String, String>> getLogCargaDoc_ListaPdf(Integer id) {
        String sql_to_query = ""
        + "SELECT "
            + "cxc_clie.numero_control AS no_clie, "
            + "substr(upper(cxc_clie.razon_social),1,12) AS clie, "
            + "log_doc_ped.id AS det_id, "
            + "log_doc_carga.id AS cga_id, "
            + "log_doc_ped.id AS ped_id, "
            + "log_doc_carga.no_carga, "
            + "log_doc_carga.fecha_entrega as f_entrega, "
            + "log_doc_ped.no_pedido, "
            + "(case when log_doc_ped_fac.no_facura is null then '' else log_doc_ped_fac.no_facura end) as no_facura, "
            + "cxc_destinatarios.id AS id_dest, "
            + "cxc_destinatarios.folio_ext AS no_dest, "
            + "cxc_destinatarios.razon_social AS nombre_dest, "
            + "''::character varying AS firma, "
            + "''::character varying AS sello, "
            + "''::character varying AS efectivo, "
            + "''::character varying AS cheque, "
            + "gral_mun.id AS mun_id, "
            + "upper(gral_mun.titulo) AS municipio, "
            + "log_doc_ped.log_status_id AS status_ped, "
            + "(case when log_status.id is null then '' else log_status.titulo end) as status_det, "
            + "sum(log_doc_ped_det.cantidad) as cant_uni, "
            + "sum(log_doc_ped_det.peso) AS peso, "
            + "sum(log_doc_ped_det.volumen) AS volumen "
        + "FROM log_doc "
        + "JOIN log_doc_carga on log_doc_carga.log_doc_id=log_doc.id "
        + "JOIN log_doc_ped on log_doc_ped.log_doc_carga_id=log_doc_carga.id "
        + "JOIN log_doc_ped_det on log_doc_ped_det.log_doc_ped_id=log_doc_ped.id "
        + "left join log_doc_ped_fac on log_doc_ped_fac.log_doc_ped_id=log_doc_ped.id "
        + "JOIN cxc_clie on cxc_clie.id=log_doc.cxc_clie_id "
        + "JOIN cxc_destinatarios on cxc_destinatarios.id=log_doc_ped.cxc_dest_id "
        + "JOIN gral_mun on gral_mun.id=cxc_destinatarios.gral_mun_id "
        + "left join log_status on log_status.id=log_doc_ped.log_status_id "
        + "WHERE log_doc_carga.log_doc_id=? "
        + "group by cxc_clie.numero_control, cxc_clie.razon_social, log_doc_carga.id, log_doc_ped.id, log_doc_carga.no_carga, log_doc_carga.fecha_entrega, log_doc_ped.no_pedido, log_doc_ped_fac.no_facura, cxc_destinatarios.id, cxc_destinatarios.folio_ext, cxc_destinatarios.razon_social, gral_mun.id, gral_mun.titulo, log_doc_ped.log_status_id, log_status.id  "
        + "order by log_doc_ped.id;";
        
        System.out.println("id: "+id);
        System.out.println("DATOS PARA EL PDFLISTA:"+sql_to_query);
       
        ArrayList<HashMap<String, String>> arrayHm = (ArrayList<HashMap<String, String>>) this.jdbcTemplate.query(
            sql_to_query,
            new Object[]{new Integer(id)}, new RowMapper(){
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    HashMap<String, String> row = new HashMap<String, String>();
                    row.put("no_clie",rs.getString("no_clie"));
                    row.put("clie",rs.getString("clie"));
                    row.put("det_id",rs.getString("det_id"));
                    row.put("cga_id",rs.getString("cga_id"));
                    row.put("f_entrega",rs.getString("f_entrega"));
                    row.put("ped_id",rs.getString("ped_id"));
                    row.put("no_carga",rs.getString("no_carga"));
                    row.put("no_pedido",rs.getString("no_pedido"));
                    row.put("no_facura",rs.getString("no_facura"));
                    row.put("id_dest",rs.getString("id_dest"));
                    row.put("no_dest",rs.getString("no_dest"));
                    row.put("nombre_dest",rs.getString("nombre_dest"));
                    row.put("firma",rs.getString("firma"));
                    row.put("sello",rs.getString("sello"));
                    row.put("cheque",rs.getString("cheque"));
                    row.put("efectivo",rs.getString("efectivo"));
                    row.put("mun_id",rs.getString("mun_id"));
                    row.put("municipio",rs.getString("municipio"));
                    row.put("status_ped",rs.getString("status_ped"));
                    row.put("status_det",rs.getString("status_det"));
                    row.put("cant_uni",StringHelper.roundDouble(rs.getString("cant_uni"),3));
                    row.put("peso",StringHelper.roundDouble(rs.getString("peso"),3));
                    row.put("volumen",StringHelper.roundDouble(rs.getString("volumen"),3));
                    return row;
                }
            }
        );
        
        //Tratar partidas
        ArrayList<HashMap<String, String>> tratado = new ArrayList<HashMap<String, String>>();
        HashMap<String,String> fila=null;
        Integer det_id_actual=0;
        String facturas_pedido="";
        String key_no_fac="no_facura";
        String key_no_fac2="nofacturas";
        int cont_fac=0;
        
        System.out.println("--Tratar partidas antes de mandar al PDF del VIAJE---------------------------");
        for( HashMap<String,String> i : arrayHm ){
            fila = new HashMap<String,String>();
            fila = i;
            //System.out.println("det_id="+fila.get("det_id")+" | "+"ped_id="+fila.get("ped_id")+" | "+"no_pedido="+fila.get("no_pedido")+" | "+"no_facura="+fila.get("no_facura")+" | "+"nombre_dest="+fila.get("nombre_dest")+" | "+"cant_uni="+fila.get("cant_uni")+" | "+"peso="+fila.get("peso")+" | "+"volumen="+fila.get("volumen"));
            
            if(!String.valueOf(det_id_actual).equals(String.valueOf(fila.get("det_id")))){
                
                //Tomar el det_id de la fila actual
                det_id_actual = Integer.valueOf(fila.get("det_id"));
                
                //Obtener todas las facturs del pedido y agregarla en una cadena separada por comas
                facturas_pedido="";
                
                //Contador de facturas del pedido
                cont_fac=0;
                for( HashMap<String,String> i2 : arrayHm ){
                    //if(det_id_actual==Integer.valueOf(i2.get("det_id"))){
                    if(String.valueOf(det_id_actual).equals(String.valueOf(i2.get("det_id")))){
                        facturas_pedido += i2.get(key_no_fac)+",";
                        cont_fac++;
                    }
                }
                
                //Crar indice con el numero de facturas del pedido
                fila.put(key_no_fac2,String.valueOf(cont_fac));
                
                if(!facturas_pedido.equals("")){
                    //Esto es para eliminar la ultima coma de la cadena
                    facturas_pedido = facturas_pedido.substring(0, facturas_pedido.length()-1);
                }
                /*
                //Eliminar el atributo del hashmap
                if(fila.containsKey(key_no_fac)){
                    fila.remove(key_no_fac);
                }
                */
                
                //Asignar valor con todas las facturas del pedido
                //fila.put(key_no_fac, facturas_pedido);
                fila.put("no_fac", facturas_pedido);
                
                //System.out.println("det_id="+fila.get("det_id")+" | "+"ped_id="+fila.get("ped_id")+" | "+"no_pedido="+fila.get("no_pedido")+" | "+"no_facura="+fila.get("no_facura")+" | "+"nombre_dest="+fila.get("nombre_dest")+" | "+"cant_uni="+fila.get("cant_uni")+" | "+"peso="+fila.get("peso")+" | "+"volumen="+fila.get("volumen"));
                
                //Agregar fila i al arreglo solo una vez si se repite el pedido por la cantidad de facturas
                tratado.add(fila);
            }
        }
        /*
        System.out.println("Array tratado");
        for( HashMap<String,String> i : tratado ){
            System.out.println("det_id="+i.get("det_id")+" | "+"ped_id="+i.get("ped_id")+" | "+"no_pedido="+i.get("no_pedido")+" | "+"no_fac="+i.get("no_fac")+" | "+"nombre_dest="+i.get("nombre_dest")+" | "+"cant_uni="+i.get("cant_uni")+" | "+"peso="+i.get("peso")+" | "+"volumen="+i.get("volumen"));
        }*/
        return tratado;
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
            + "JOIN gral_pais on gral_pais.id = cxc_clie.pais_id "
            + "JOIN gral_edo on gral_edo.id = cxc_clie.estado_id "
            + "JOIN gral_mun on gral_mun.id = cxc_clie.municipio_id "
            +" WHERE empresa_id ="+id_empresa+"  "
            +" AND cxc_clie.borrado_logico=false  "+where+" "
        +") AS sbt "
        +"left join gral_mon on gral_mon.id = sbt.moneda_id ORDER BY sbt.id;";
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
            + "JOIN gral_pais on gral_pais.id = cxc_clie.pais_id "
            + "JOIN gral_edo on gral_edo.id = cxc_clie.estado_id "
            + "JOIN gral_mun on gral_mun.id = cxc_clie.municipio_id "
            +" WHERE empresa_id ="+id_empresa+"  "
            +" AND cxc_clie.borrado_logico=false  "+where+" "
            + "AND  cxc_clie.numero_control='"+no_control.toUpperCase()+"'"
        +") AS sbt "
        +"left join gral_mon on gral_mon.id = sbt.moneda_id ORDER BY sbt.id LIMIT 1;";

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
    
    
    
    
    //Obtiene almacenes de la sucursal especificada.
    //Se utiliza en la Carga de Inventario Fisico
    @Override
    public ArrayList<HashMap<String, String>> getAlmacenesSucursal(Integer id_empresa, Integer id_sucursal) {
	String sql_query = ""
                + "SELECT DISTINCT "
                    + "inv_alm.id, "
                    + "inv_alm.id||' '||inv_alm.titulo AS titulo "
                + "FROM inv_alm  "
                + "JOIN inv_suc_alm on inv_suc_alm.almacen_id = inv_alm.id "
                + "JOIN gral_suc on gral_suc.id = inv_suc_alm.sucursal_id "
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
    
    
    
    //Elimina el contenido de esta tabla de la empresa y sucursal indicada en los parametros.
    @Override
    public int getDeleteFromLogCargaDocTmp(Integer id_emp, Integer id_suc) {
        int row=0;
        String updateSql="";
        
        try{
            updateSql = "DELETE FROM log_carga_doc_tmp WHERE gral_emp_id=? AND gral_suc_id=?;";
            
            //System.out.println("updateSql: "+updateSql);
            
            // define query arguments
            Object[] params = new Object[] { new Integer(id_emp),new Integer(id_suc)};
            
            // define SQL types of the arguments
            int[] types = new int[] { Types.SMALLINT, Types.SMALLINT };
            
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
    
    
    
    //Carga la tabla temporal con los datos del Documento
    @Override
    public HashMap<String, String> getInsertLogCargaDocTmp(String data_string) {
        HashMap<String, String> retorno = new HashMap<String, String>();
        
        int row=0;
        String msj="";
        int rowCountClieId=0;
        int rowCountProdId=0;
        String insertSql = "";
        boolean cargar_registro=false;
        
        String param[] = data_string.split("___");
        
        
        if(param[8].trim().equals("")){
            retorno.put("destinatario", "false___El Cliente Destinatario no tiene numero de cotrol. Revise el archivo.");
            cargar_registro=false;
        }else{
            retorno.put("destinatario", "true___ .");
            cargar_registro=true;
        }
        /*
        rowCountClieId = this.getJdbcTemplate().queryForInt("select count(id) from cxc_destinatarios where upper(trim(folio_ext))='"+param[8]+"' and borrado_logico=false and gral_emp_id="+param[0]+";");
        if(rowCountClieId<=0){
            retorno.put("destinatario", "false___Se agrego este Destinatario al catalogo. ");
        }else{
            retorno.put("destinatario", "true___ .");
        }
        */
        
        /*
        rowCountPoblacionId = this.getJdbcTemplate().queryForInt("select count(id) from gral_mun where upper(trim(titulo))='"+param[10]+"'");
        if(rowCountPoblacionId<=0){
            retorno.put("poblacion", "false___No se encontro la Poblacion. ");
            cargar_registro = false;
        }else{
            retorno.put("poblacion", "true___ .");
        }
        */
        
        if(param[11].trim().equals("")){
            retorno.put("producto", "false___El producto no tiene c&oacute;digo. Revise el archivo.");
            cargar_registro=false;
        }else{
            retorno.put("producto", "true___ .");
            cargar_registro=true;
        }
        
        /*
        rowCountProdId = this.getJdbcTemplate().queryForInt("select count(id) from inv_prod where upper(trim(sku))='"+param[11]+"' and borrado_logico=false and empresa_id="+param[0]+";");
        if(rowCountProdId<=0){
            insertSql = "";
            
            this.getJdbcTemplate().update(msj);
            
            retorno.put("producto", "false___Se agrego este producto al catalogo. ");
        }else{
            retorno.put("producto", "true___ .");
        }
        */
        
        /*
        rowCountUnidadId = this.getJdbcTemplate().queryForInt("select count(id) from inv_prod_unidades where upper(trim(titulo_abr))='"+param[14]+"' and borrado_logico=false;");
        if(rowCountUnidadId<=0){
            retorno.put("unidad", "false___No se encontro la Unidad de Medida en el catalogo. ");
            cargar_registro = false;
        }else{
            retorno.put("unidad", "true___ .");
        }
        */
        
        if(cargar_registro){
            try{
                insertSql = "";
                
                //Cargar en la tabla INV_EXI_TMP
                insertSql = "INSERT INTO log_carga_doc_tmp(gral_emp_id,gral_suc_id,user_id,alm_id,no_carga,no_pedido,tipo_entrega,fecha_entrega, cliente_id, no_dest,nombre_dest,poblacion_dest,codigo_prod,descripcion_prod,cantidad,unidad,peso,volumen,no_fac,monto_fac,fecha_carga,estatus) "
                        + "VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,true);";
                //System.out.println("insertSql: "+insertSql);
                
                // define query arguments
                Object[] params = new Object[] { new Integer(param[0]),new Integer(param[1]),new Integer(param[2]),new Integer(param[3]),param[4],param[5],param[6],param[7], new Integer(param[20]),param[8],param[9],param[10],param[11],param[12],param[13],param[14],param[15],param[16],param[17],param[18],param[19]};
                
                // define SQL types of the arguments
                int[] types = new int[] {Types.SMALLINT,Types.SMALLINT,Types.SMALLINT,Types.SMALLINT,Types.VARCHAR,Types.VARCHAR,Types.VARCHAR,Types.DATE, Types.INTEGER, Types.VARCHAR,Types.VARCHAR,Types.VARCHAR,Types.VARCHAR,Types.VARCHAR,Types.VARCHAR,Types.VARCHAR,Types.VARCHAR,Types.VARCHAR,Types.VARCHAR,Types.VARCHAR,Types.DATE};
                
                // execute insert query to insert the data
                // return number of row / rows processed by the executed query
                row = this.getJdbcTemplate().update(insertSql, params, types);
                
                msj = "true___ .";
                //System.out.println(row + " row inserted.");
            } catch (Exception e) {
                msj =  "false___No se cargo el registro debido a errores internos["+e.getMessage()+"]. Intente nuevamente.";
            }
        }else{
            msj = "false___No se cargo el registro.";
        }
        
        retorno.put("cargado", msj);
        return retorno;
    }
    
    
    
    
    //Carga la tabla temporal con los datos del Documento
    //Este metodo se utiliza en la carga de Archivo para Entradas al Almacen
    @Override
    public HashMap<String, String> getInsertLogEntradasAlmacenTmp(String data_string) {
        HashMap<String, String> retorno = new HashMap<String, String>();
        
        String msj="";
        int row=0;
        String insertSql = "";
        boolean cargar_registro=false;
        
        String param[] = data_string.split("___");
        
        
        //no_carga
        param[4] = (param[4]==null)?"":param[4];
        //no_pedido
        param[5] = (param[5]==null)?"":param[5];
        //tipo_entrega
        param[6] = (param[6]==null)?"":param[6];
        //fecha_entrega
        if(param[7].trim().equals("") || param[7]==null){
            param[7]="2999-12-31";
        }
        
        //no_cliente
        param[8] = (param[8]==null)?"0":param[8];
        //nombre_cliente
        param[9] = (param[9]==null)?"":param[9];
        //poblacion_cliente
        param[10] = (param[10]==null)?"":param[10];
        
        //codigo_producto
        if(param[11].trim().equals("")){
            retorno.put("producto", "false___El producto no tiene c&oacute;digo. Revise el archivo.");
            cargar_registro=false;
        }else{
            retorno.put("producto", "true___ .");
            cargar_registro=true;
        }
        
        //descripcion_producto
        param[12] = (param[12]==null)?"":param[12];
        
        //Cantidad
        if(param[13].trim().equals("") || param[13]==null){
            param[13]="0";
        }
        
        //unidad_medida
        param[14] = (param[14]==null)?"":param[14];
        
        //Peso
        if(param[15].trim().equals("") || param[15]==null){
            param[15]="0";
        }
        
        //Volumen
        if(param[16].trim().equals("") || param[16]==null){
            param[16]="0";
        }
        
        //no_factura
        param[17] = (param[17]==null)?"":param[17];
        //monto_factura
        param[18] = (param[18]==null)?"":param[18];
        //fecha
        param[19] = (param[19]==null)?"":param[19];
        
        //id_cliente
        if(param[20].trim().equals("") || param[20]==null){
            param[20]="0";
        }
        
        //System.out.println("id_emp="+param[0]+"|id_suc="+param[1]+"|id_usuario="+param[2]+"|almacen="+param[3]+"|no_carga="+param[4]+"|no_pedido="+param[5]+"|tipo_entrega="+param[6]+"|fecha_entrega="+param[7]+"|no_cliente="+param[8]+"|nombre_cliente="+param[9]+"|poblacion_cliente="+param[10]+"|codigo_producto="+param[11]+"|descripcion_producto="+param[12]+"|Cantidad="+param[13]+"|unidad_medida="+param[14]+"|Peso="+param[15]+"|Volumen="+param[16]+"|no_factura="+param[17]+"|monto_factura="+param[18]+"|fecha="+param[19]+"|id_cliente="+param[20]);
        
        
        if(cargar_registro){
            try{
                insertSql = "";
                
                //Cargar en la tabla INV_EXI_TMP
                insertSql = "INSERT INTO log_carga_doc_tmp(gral_emp_id,gral_suc_id,user_id,alm_id,no_carga,no_pedido,tipo_entrega,fecha_entrega, cliente_id, no_dest,nombre_dest,poblacion_dest,codigo_prod,descripcion_prod,cantidad,unidad,peso,volumen,no_fac,monto_fac,fecha_carga,estatus) "
                        + "VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,true);";
                //System.out.println("insertSql: "+insertSql);
                
                // define query arguments
                Object[] params = new Object[] { new Integer(param[0]),new Integer(param[1]),new Integer(param[2]),new Integer(param[3]),param[4],param[5],param[6],param[7], new Integer(param[20]),param[8],param[9],param[10],param[11],param[12],param[13],param[14],param[15],param[16],param[17],param[18],param[19]};
                
                // define SQL types of the arguments
                int[] types = new int[] {Types.SMALLINT,Types.SMALLINT,Types.SMALLINT,Types.SMALLINT,Types.VARCHAR,Types.VARCHAR,Types.VARCHAR,Types.DATE, Types.INTEGER, Types.VARCHAR,Types.VARCHAR,Types.VARCHAR,Types.VARCHAR,Types.VARCHAR,Types.VARCHAR,Types.VARCHAR,Types.VARCHAR,Types.VARCHAR,Types.VARCHAR,Types.VARCHAR,Types.DATE};
                
                // execute insert query to insert the data
                // return number of row / rows processed by the executed query
                row = this.getJdbcTemplate().update(insertSql, params, types);
                
                msj = "true___ .";
                //System.out.println(row + " row inserted.");
            } catch (Exception e) {
                msj =  "false___No se cargo el registro debido a errores internos["+e.getMessage()+"]. Intente nuevamente.";
            }
        }else{
            msj = "false___No se cargo el registro.";
        }
        
        retorno.put("cargado", msj);
        return retorno;
    }
    
    
    
    //Verifica que el documento con  las cargas no ha sido dado de alta anteriormente
    @Override
    public int getVerificarDocumento(Integer id_emp, Integer id_clie, String no_carga) {
        int rowCount = this.getJdbcTemplate().queryForInt("select count(log_doc_carga.id) from log_doc join log_doc_carga on (log_doc_carga.log_doc_id=log_doc.id and log_doc_carga.no_carga='"+no_carga+"') where log_doc.gral_emp_id="+id_emp+" and log_doc.cxc_clie_id="+id_clie+";");
        
        return rowCount;
    }
    
    //LLamada al procedimiento que carga las tablas relacionadas al documento y actualiza inventario
    @Override
    public String getUpdateDocInvExi(String campos_data, String extra_data_array) {
        //String sql_to_query = "select * from log_carga_documentos("+usuario_id+","+empresa_id+","+sucursal_id+","+id_cliente+");";
        String sql_to_query = "select * from log_carga_documentos('"+campos_data+"',array["+extra_data_array+"]);";
        
        System.out.println("sql_to_query: "+sql_to_query);
        
        String valor_retorno="";
        Map<String, Object> update = this.getJdbcTemplate().queryForMap(sql_to_query);
        
        valor_retorno = update.get("log_carga_documentos").toString();
        
        return valor_retorno;
    }
    
    //TERMINA METODOS DE CARGA DE DOCUMENTOS------------------------------------
    
    
    
    
    
    
    
    //Metodos para el administrador de vijes
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
    
    
    //Obtiene las sucursales de la empresa indicada
    @Override
    public ArrayList<HashMap<String, Object>> getSucursales(Integer idEmp) {
        
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
    
    
    
    
    //Obtiene las sucursales de la empresa indicada
    @Override
    public ArrayList<HashMap<String, Object>> getTransportistas(Integer idEmp, Integer idSuc) {
        String sql_to_query = "";
        if(idSuc>0){
            //Obtener transportistas de la sucursal
            sql_to_query = "select distinct id, folio, razon_social as titulo from cxp_prov where empresa_id=? and sucursal_id="+idSuc+" and transportista=true and borrado_logico=false order by razon_social;";
        }else{
            //obtener todos los transportistas de la empresa sin importar la sucursal
            sql_to_query = "select distinct id, folio, razon_social as titulo from cxp_prov where empresa_id=? and transportista=true and borrado_logico=false order by razon_social;";
        }
        
        ArrayList<HashMap<String, Object>> hm_facturas = (ArrayList<HashMap<String, Object>>) this.jdbcTemplate.query(
            sql_to_query,
            new Object[]{new Integer(idEmp)}, new RowMapper(){
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    HashMap<String, Object> row = new HashMap<String, Object>();
                    row.put("id",String.valueOf(rs.getInt("id")));
                    row.put("folio",rs.getString("folio"));
                    row.put("titulo",rs.getString("titulo"));
                    return row;
                }
            }
        );
        return hm_facturas;
    }
    
    
    
    
    //Buscador de Unidades(Vehiculos)
    @Override
    public ArrayList<HashMap<String, Object>> getBuscadorUnidades(String no_unidad, String marca, Integer asigna_tipo_unidad, Integer tipo_unidad_id, Integer id_empresa, Integer id_sucursal) {
        String where="";
        if(id_sucursal!=0){
            where = " and log_vehiculos.gral_suc_id="+id_sucursal;
        }
        
        //asigna_tipo_unidad 1=Manual, 2=Automatico
        if(asigna_tipo_unidad==2){
            where +=" and log_vehiculos.log_vehiculo_tipo_id="+tipo_unidad_id;
        }
        
	String sql_query = ""
        + "select "
            + "log_vehiculos.id,"
            + "log_vehiculos.folio, "
            + "log_vehiculos.numero_economico as no_eco,"
            + "(case when log_vehiculo_marca.id is null then '' else log_vehiculo_marca.titulo end) as marca,"
            + "(case when log_vehiculo_clase.id is null then '' else log_vehiculo_clase.titulo end) as clase_unidad,"
            + "(case when log_vehiculo_tipo.id is null then 0 else log_vehiculo_tipo.id end) as t_unidad_id,"
            + "(case when log_vehiculo_tipo.id is null then '' else log_vehiculo_tipo.titulo end) as tipo_unidad,"
            + "log_vehiculos.placa,"
            + "log_vehiculos.cap_volumen,"
            + "log_vehiculos.cap_peso,"
            + "(case when log_choferes.id is null then '' else log_choferes.clave end) AS no_operador,"
            + "(case when log_choferes.id is null then '' else ((case when log_choferes.nombre is null then '' else log_choferes.nombre||' ' end)||(case when log_choferes.apellido_paterno is null then '' else log_choferes.apellido_paterno||' ' end)||(case when log_choferes.apellido_materno is null then '' else log_choferes.apellido_materno end)) end) AS operador "
        + "from log_vehiculos "
        + "join log_vehiculo_tipo on log_vehiculo_tipo.id=log_vehiculos.log_vehiculo_tipo_id "
        + "left join log_vehiculo_marca on (log_vehiculo_marca.id=log_vehiculos.log_vehiculo_marca_id  and log_vehiculo_marca.titulo ilike ?) "
        + "left join log_vehiculo_clase on log_vehiculo_clase.id=log_vehiculos.log_vehiculo_clase_id "
        + "left join log_choferes on log_choferes.id=log_vehiculos.log_chofer_id "
        + "where log_vehiculos.folio ilike ? and log_vehiculos.gral_emp_id=? and log_vehiculos.borrado_logico=false "+where+";";
        
        //System.out.println("marca: "+marca+"  no_unidad="+no_unidad+"  id_empresa="+id_empresa);
        //System.out.println("getBuscadorUnidades: "+sql_query);
        ArrayList<HashMap<String, Object>> hm = (ArrayList<HashMap<String, Object>>) this.jdbcTemplate.query(
            sql_query,
            new Object[]{marca, no_unidad, new Integer(id_empresa)}, new RowMapper() {
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    HashMap<String, Object> row = new HashMap<String, Object>();
                    row.put("id",String.valueOf(rs.getInt("id")));
                    row.put("folio",rs.getString("folio"));
                    row.put("no_eco",rs.getString("no_eco"));
                    row.put("marca",rs.getString("marca"));
                    row.put("clase_unidad",rs.getString("clase_unidad"));
                    row.put("t_unidad_id",String.valueOf(rs.getInt("t_unidad_id")));
                    row.put("tipo_unidad",rs.getString("tipo_unidad"));
                    row.put("placa",rs.getString("placa"));
                    row.put("cap_volumen",StringHelper.roundDouble(rs.getString("cap_volumen"),3));
                    row.put("cap_peso",StringHelper.roundDouble(rs.getString("cap_peso"),3));
                    row.put("no_operador",rs.getString("no_operador"));
                    row.put("operador",rs.getString("operador"));
                    return row;
                }
            }
        );
        return hm;
    }
    
    
    //Obtener datos de la Unidad a partir del Numero de Economico
    @Override
    public ArrayList<HashMap<String, Object>> getDatosUnidadByNoUnidad(String no_unidad, Integer asigna_tipo_unidad, Integer tipo_unidad_id, Integer id_empresa, Integer id_sucursal) {
        
        String where="";
        if(id_sucursal!=0){
            where +=" and log_vehiculos.gral_suc_id="+id_sucursal;
        }
        
        //asigna_tipo_unidad 1=Manual, 2=Automatico
        if(asigna_tipo_unidad==2){
            where +=" and log_vehiculos.log_vehiculo_tipo_id="+tipo_unidad_id;
        }
        
        String sql_query = ""
        + "select "
            + "log_vehiculos.id,"
            + "log_vehiculos.folio, "
            + "log_vehiculos.numero_economico as no_eco,"
            + "(case when log_vehiculo_marca.id is null then '' else log_vehiculo_marca.titulo end) as marca,"
            + "(case when log_vehiculo_clase.id is null then 0 else log_vehiculo_clase.id end) as clase_id,"
            + "(case when log_vehiculo_clase.id is null then '' else log_vehiculo_clase.titulo end) as clase_unidad,"
            + "(case when log_vehiculo_tipo.id is null then 0 else log_vehiculo_tipo.id end) as t_unidad_id,"
            + "(case when log_vehiculo_tipo.id is null then '' else log_vehiculo_tipo.titulo end) as tipo_unidad,"
            + "log_vehiculos.placa,"
            + "log_vehiculos.cap_volumen,"
            + "log_vehiculos.cap_peso,"
            + "(case when log_choferes.id is null then '' else log_choferes.clave end) AS no_operador,"
            + "(case when log_choferes.id is null then '' else ((case when log_choferes.nombre is null then '' else log_choferes.nombre||' ' end)||(case when log_choferes.apellido_paterno is null then '' else log_choferes.apellido_paterno||' ' end)||(case when log_choferes.apellido_materno is null then '' else log_choferes.apellido_materno end)) end) AS operador "
        + "from log_vehiculos "
        + "join log_vehiculo_tipo on log_vehiculo_tipo.id=log_vehiculos.log_vehiculo_tipo_id "
        + "left join log_vehiculo_marca on log_vehiculo_marca.id=log_vehiculos.log_vehiculo_marca_id "
        + "left join log_vehiculo_clase on log_vehiculo_clase.id=log_vehiculos.log_vehiculo_clase_id "
        + "left join log_choferes on log_choferes.id=log_vehiculos.log_chofer_id "
        + "where upper(log_vehiculos.folio)=? and log_vehiculos.gral_emp_id=? and log_vehiculos.borrado_logico=false "+where+" limit 1;";
        //System.out.println("getDatosVehiculo: "+sql_query);
        
        ArrayList<HashMap<String, Object>> hm = (ArrayList<HashMap<String, Object>>) this.jdbcTemplate.query(
            sql_query,
            new Object[]{no_unidad, new Integer(id_empresa)}, new RowMapper() {
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    HashMap<String, Object> row = new HashMap<String, Object>();
                    row.put("id",String.valueOf(rs.getInt("id")));
                    row.put("folio",rs.getString("folio"));
                    row.put("no_eco",rs.getString("no_eco"));
                    row.put("marca",rs.getString("marca"));
                    row.put("clase_id",String.valueOf(rs.getInt("clase_id")));
                    row.put("clase_unidad",rs.getString("clase_unidad"));
                    row.put("t_unidad_id",String.valueOf(rs.getInt("t_unidad_id")));
                    row.put("tipo_unidad",rs.getString("tipo_unidad"));
                    row.put("placa",rs.getString("placa"));
                    row.put("cap_volumen",StringHelper.roundDouble(rs.getString("cap_volumen"),3));
                    row.put("cap_peso",StringHelper.roundDouble(rs.getString("cap_peso"),3));
                    row.put("no_operador",rs.getString("no_operador"));
                    row.put("operador",rs.getString("operador"));
                    return row;
                }
            }
        );
        return hm;
    }
    
    
    
    
    //Obtiene todas las cargas pendientes de entregar de acuerdo a los filtros indicados
    @Override
    public ArrayList<HashMap<String, Object>> getLogAdmViaje_CargasPendientes(Integer id_empresa, Integer id_suc_user, String no_clie, String fecha_carga, String no_carga, String no_ped, String no_dest, String dest, String poblacion, Integer id_alm, Integer tipo_de_distribucion) {
        String where="";
        if(id_suc_user!=0){
            where +=" and log_doc.gral_suc_id="+id_suc_user;
        }
        
        //no_clie, no_carga, no_ped, no_dest, dest, poblacion
        if(!no_clie.trim().equals("")){
            where +=" and cxc_clie.numero_control ilike '%"+no_clie.toUpperCase()+"%'";
        }
        
        if(!fecha_carga.trim().equals("")){
            where +=" and log_doc.fecha_carga='"+fecha_carga+"'::timestamp with time zone";
        }
        
        if(!no_carga.trim().equals("")){
            where +=" and log_doc_carga.no_carga ilike '%"+no_carga.toUpperCase()+"%'";
        }
        
        if(!no_ped.trim().equals("")){
            where +=" and log_doc_ped.no_pedido ilike '%"+no_ped.toUpperCase()+"%'";
        }
        
        if(!no_dest.trim().equals("")){
            where +=" and cxc_destinatarios.folio_ext ilike '%"+no_dest.toUpperCase()+"%'";
        }
        
        if(!dest.trim().equals("")){
            where +=" and cxc_destinatarios.razon_social ilike '%"+dest.toUpperCase()+"%'";
        }
        
        if(!poblacion.trim().equals("")){
            where +=" and gral_mun.titulo ilike '%"+poblacion.toUpperCase()+"%'";
        }
        
        if(id_alm>0){
            where +=" and log_doc.inv_alm_id="+id_alm;
        }
        
        
        String sql_to_query = ""
        + "select "
            + "cxc_clie.numero_control as no_clie,"
            + "substr(upper(cxc_clie.razon_social),1,12) as clie,"
            + "log_doc.id as doc_id, "
            + "(case when log_doc.fecha_carga is null then '' else to_char(log_doc.fecha_carga::timestamp with time zone, 'dd-mm-yyyy') end) as fecha_carga,"
            + "log_doc_carga.id as cga_id, "
            + "log_doc_ped.id as ped_id, "
            + "log_doc_carga.no_carga,"
            + "log_doc_carga.fecha_entrega, "
            + "log_doc_ped.no_pedido, "
            + "cxc_destinatarios.id as id_dest,"
            + "cxc_destinatarios.folio_ext as no_dest, "
            + "cxc_destinatarios.razon_social as nombre_dest, "
            + "cxc_destinatarios.solicitar_firma as firma, "
            + "cxc_destinatarios.solicitar_sello as sello, "
            + "cxc_destinatarios.solicitar_efectivo as efectivo, "
            + "cxc_destinatarios.solicitar_cheque as cheque, "
            + "(case when inv_prod.id is null then 0 else cxc_destinatarios.inv_prod_id end) as serv_id,"
            + "(case when inv_prod.id is null then 0 else cxc_destinatarios.costo_serv end) as serv_costo,"
            + "(case when inv_prod.id is null then '' else inv_prod.sku end) as serv_codigo,"
            + "(case when inv_prod.id is null then '' else inv_prod.descripcion end) as serv_desc,"
            + "(case when um.id is null then '' else um.titulo end) as serv_um,"
            + "gral_mun.id as mun_id,"
            + "(case when gral_mun.id is null then '' else upper(gral_mun.titulo) end) as municipio, "
            + "(case when log_status.id is null then '' else log_status.titulo end) as status_ped,"
            + "sum(log_doc_ped_det.cantidad) as cant_uni, "
            + "sum(log_doc_ped_det.peso) as peso, "
            + "sum(log_doc_ped_det.volumen) as volumen,"
            
            + "(case when tbl_tarifa.log_tarifa_tipo_id is null then 0 else tbl_tarifa.log_tarifa_clase_id end) as t_clase_id, "
            + "(case when tbl_tarifa.log_tarifa_tipo_id is null then 0 else tbl_tarifa.log_tarifa_tipo_id end)as t_tipo_id, "
            + "(case when tbl_tarifa.log_tarifa_tipo_id is null then 0 else tbl_tarifa.operacion end) as operacion, "
            + "(case when tbl_tarifa.log_tarifa_tipo_id is null then 0 else tbl_tarifa.base end) as base, "
            + "(case when tbl_tarifa.log_tarifa_tipo_id is null then 0 else tbl_tarifa.precio end) as precio_u,"
            + "(case when tbl_tarifa.log_tarifa_tipo_id is null then '' else tbl_tarifa.clase end) as t_clase,"
            + "(case when tbl_tarifa.log_tarifa_tipo_id is null then '' else tbl_tarifa.tipo_tarifa end) as t_tipo,"
            
            + "(case when tbl_tarifa.log_tarifa_tipo_id is null then 0 else "
                + "(case when tbl_tarifa.operacion=1 then "
                    + "(case when tbl_tarifa.base=1 then (1 * tbl_tarifa.precio::double precision) "
                        + "when tbl_tarifa.base=2 then ((sum(log_doc_ped_det.peso)/1000) * tbl_tarifa.precio::double precision) "
                        + "when tbl_tarifa.base=3 then (sum(log_doc_ped_det.volumen) * tbl_tarifa.precio::double precision) "
                    + "else 0 end) "
                + "else 0 end)"
            + "end) as precio,"
            + "log_doc_ped.log_tarifa_clase_id as tarifa_clase_id "
        + "from log_doc "
        + "join log_doc_carga on log_doc_carga.log_doc_id=log_doc.id "
        + "join log_doc_ped on (log_doc_ped.log_doc_carga_id=log_doc_carga.id and log_doc_ped.log_status_id=0)"
        + "join log_doc_ped_det on log_doc_ped_det.log_doc_ped_id=log_doc_ped.id "
        + "join cxc_clie on cxc_clie.id=log_doc.cxc_clie_id "
        + "left join cxc_destinatarios on cxc_destinatarios.id=log_doc_ped.cxc_dest_id  "
        + "left join inv_prod on inv_prod.id=cxc_destinatarios.inv_prod_id  "
        + "left join inv_prod_unidades as um on um.id=inv_prod.unidad_id "
        + "left join gral_mun on gral_mun.id=cxc_destinatarios.gral_mun_id  "
        + "left join log_status on log_status.id=log_doc_ped.log_status_id  "
        + "left join log_tarifa_clie on (log_tarifa_clie.cxc_clie_id=cxc_clie.id and log_tarifa_clie.log_tarifa_clase_id=log_doc_ped.log_tarifa_clase_id) "
        + "left join ("
                + "select log_tarifario_venta.cxc_clie_id, log_tarifa_tipo.log_tarifa_clase_id, log_tarifario_venta_det.log_tarifa_tipo_id, log_tarifario_venta.gral_mun_id, log_tarifa_tipo.operacion, log_tarifa_tipo.base, log_tarifario_venta_det.valor as precio, upper(log_tarifa_clase.titulo) as clase, upper(log_tarifa_tipo.titulo) as tipo_tarifa "
                + "from log_tarifario_venta "
                + "join log_tarifario_venta_det on (log_tarifario_venta_det.log_tarifario_venta_id=log_tarifario_venta.id) "
                + "join log_tarifa_tipo on log_tarifa_tipo.id=log_tarifario_venta_det.log_tarifa_tipo_id "
                + "join log_tarifa_clase on log_tarifa_clase.id=log_tarifa_tipo.log_tarifa_clase_id "
        + ") as tbl_tarifa on (tbl_tarifa.cxc_clie_id=log_tarifa_clie.cxc_clie_id and tbl_tarifa.log_tarifa_clase_id=log_tarifa_clie.log_tarifa_clase_id and tbl_tarifa.log_tarifa_tipo_id=log_tarifa_clie.log_tarifa_tipo_id and tbl_tarifa.gral_mun_id=gral_mun.id ) "
        + "where log_doc.gral_emp_id=? "+ where +" "
        + "group by cxc_clie.numero_control, cxc_clie.razon_social, log_doc.id, log_doc_carga.id, log_doc_ped.id, log_doc_carga.no_carga, log_doc_ped.no_pedido, cxc_destinatarios.id, cxc_destinatarios.folio, cxc_destinatarios.razon_social, gral_mun.id, gral_mun.titulo, inv_prod.id, um.id, log_status.id, tbl_tarifa.log_tarifa_clase_id, tbl_tarifa.log_tarifa_tipo_id, tbl_tarifa.clase, tbl_tarifa.tipo_tarifa, tbl_tarifa.operacion, tbl_tarifa.base, tbl_tarifa.precio  "
        + "order by cxc_destinatarios.razon_social;";
        
        //log_tarifa_tipo.operacion IS '1=Multiplicacion';
        //log_tarifa_tipo.base IS 'Utilizar Multiplicador 1=1(El numero uno), 2=Peso en toneladas, 3=Cantidad de Metros Cubicos';
        
        //System.out.println("Pendientes: "+sql_to_query);
        
        ArrayList<HashMap<String, Object>> hm = (ArrayList<HashMap<String, Object>>) this.jdbcTemplate.query(
            sql_to_query,
            new Object[]{new Integer(id_empresa)}, new RowMapper(){
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    HashMap<String, Object> row = new HashMap<String, Object>();
                    row.put("no_clie",rs.getString("no_clie"));
                    row.put("clie",rs.getString("clie"));
                    row.put("doc_id",rs.getInt("doc_id"));
                    row.put("cga_id",rs.getInt("cga_id"));
                    row.put("f_carga",rs.getString("fecha_carga"));
                    row.put("f_entrega",rs.getString("fecha_entrega"));
                    row.put("ped_id",rs.getInt("ped_id"));
                    row.put("no_carga",rs.getString("no_carga"));
                    row.put("no_pedido",rs.getString("no_pedido"));
                    row.put("id_dest",rs.getInt("id_dest"));
                    row.put("no_dest",rs.getString("no_dest"));
                    row.put("nombre_dest",rs.getString("nombre_dest"));
                    row.put("firma",rs.getBoolean("firma"));
                    row.put("sello",rs.getBoolean("sello"));
                    row.put("efectivo",rs.getBoolean("efectivo"));
                    row.put("cheque",rs.getBoolean("cheque"));
                    row.put("serv",rs.getInt("serv_id")+"&|&"+rs.getString("serv_codigo")+"&|&"+rs.getString("serv_desc")+"&|&"+rs.getString("serv_um")+"&|&"+StringHelper.roundDouble(rs.getString("serv_costo"),2));
                    
                    /*
                    row.put("serv_id",rs.getInt("serv_id"));
                    row.put("serv_codigo",rs.getString("serv_codigo"));
                    row.put("serv_desc",rs.getString("serv_desc"));
                    row.put("serv_um",rs.getString("serv_um"));
                    row.put("serv_costo",StringHelper.roundDouble(rs.getString("serv_costo"),2));
                    */
                    
                    row.put("mun_id",rs.getInt("mun_id"));
                    row.put("municipio",rs.getString("municipio"));
                    row.put("status_ped",rs.getString("status_ped"));
                    //row.put("status_ped","");
                    row.put("cant_uni",StringHelper.roundDouble(rs.getString("cant_uni"),2));
                    row.put("peso",StringHelper.roundDouble(rs.getString("peso"),3));
                    row.put("volumen",StringHelper.roundDouble(rs.getString("volumen"),3));
                    
                    row.put("t_clase_id",rs.getInt("t_clase_id"));
                    row.put("t_tipo_id",rs.getInt("t_tipo_id"));
                    row.put("t_clase",rs.getString("t_clase"));
                    row.put("t_tipo",rs.getString("t_tipo"));
                    
                    if(rs.getInt("tarifa_clase_id")==5){
                        row.put("precio",StringHelper.roundDouble(0,2));
                    }else{
                        row.put("precio",StringHelper.roundDouble(rs.getString("precio"),2));
                    }
                    
                    row.put("tarifa_clase_id",rs.getInt("tarifa_clase_id"));
                    return row;
                }
            }
        );
        return hm;
    }
    
    
    
    
    //Obtener y formar cadena con las facturas del pedido seleccionado
    @Override
    public String getLogAdmViaje_CadenaFacturasDelPedido(Integer id_ped) {
        String cadena_retorno = "";
        String sql_to_query = "select no_facura from log_doc_ped_fac where id=?;";
        ArrayList<HashMap<String, Object>> arrayHm = (ArrayList<HashMap<String, Object>>) this.jdbcTemplate.query(
            sql_to_query,
            new Object[]{new Integer(id_ped)}, new RowMapper(){
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    HashMap<String, Object> row = new HashMap<String, Object>();
                    row.put("no_facura",rs.getString("no_facura"));
                    return row;
                }
            }
        );
        
        //Agregar en una cadena todas las facturas
        for( HashMap<String,Object> i : arrayHm ){
            cadena_retorno += String.valueOf(i.get("no_facura"))+",";
        }
        
        if(!cadena_retorno.equals("")){
            //Esto es para eliminar la ultima coma de la cadena
            cadena_retorno = cadena_retorno.substring(0, cadena_retorno.length()-1);
        }
                
        return cadena_retorno;
    }
    
    
    
    
    //Obtiene detalles del pedido
    @Override
    public ArrayList<HashMap<String, Object>> getLogAdmViaje_DetallePedido(Integer id_ped) {
        String sql_to_query = ""
        + "select "
            + "log_doc_ped_det.id as id_det, "
            + "(case when inv_prod.id is null then 0 else inv_prod.id end) as id_prod,"
            + "inv_prod.sku AS codigo_prod,"
            + "inv_prod.descripcion AS titulo_prod,"
            + "log_doc_ped_det.cantidad,"
            + "(case when inv_prod_unidades.id is null then 0 else inv_prod_unidades.id end) AS unidad_id,"
            + "(case when inv_prod_unidades.id is null then '' else inv_prod_unidades.titulo_abr end) AS unidad,"
            + "log_doc_ped_det.peso,"
            + "log_doc_ped_det.volumen,"
            + "log_doc_ped_det.log_status_id as estatus,"
            + "(case when log_doc_ped_det_dev.id is null then 0 else 1 end) as status_r, "
            + "(case when log_doc_ped_det_dev.id is null then 0 else log_doc_ped_det_dev.log_tipo_rechazo_id end) as tr_id, "
            + "(case when log_doc_ped_det_dev.id is null then 0 else log_doc_ped_det_dev.cantidad end) as cant_r,"
            + "(case when log_doc_ped_det_dev.id is null then 0 else log_doc_ped_det_dev.cargo end) as cargo_r "
        + "from log_doc_ped_det "
        + "join inv_prod on inv_prod.id=log_doc_ped_det.inv_prod_id "
        + "left join inv_prod_unidades on inv_prod_unidades.id=log_doc_ped_det.inv_prod_unidad_id "
        + "left join log_doc_ped_det_dev on (log_doc_ped_det_dev.log_doc_ped_det_id=log_doc_ped_det.id and log_doc_ped_det_dev.tipo=1) "
        + "WHERE log_doc_ped_det.log_doc_ped_id=? ORDER BY log_doc_ped_det.id;";
        
        ArrayList<HashMap<String, Object>> hm = (ArrayList<HashMap<String, Object>>) this.jdbcTemplate.query(
            sql_to_query,
            new Object[]{new Integer(id_ped)}, new RowMapper(){
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    HashMap<String, Object> row = new HashMap<String, Object>();
                    row.put("id_det",rs.getInt("id_det"));
                    row.put("id_prod",rs.getInt("id_prod"));
                    row.put("codigo_prod",rs.getString("codigo_prod"));
                    row.put("titulo_prod",rs.getString("titulo_prod"));
                    row.put("cantidad",StringHelper.roundDouble(rs.getString("cantidad"),2));
                    row.put("unidad_id",rs.getInt("unidad_id"));
                    row.put("unidad",rs.getString("unidad"));
                    row.put("peso",StringHelper.roundDouble(rs.getString("peso"),3));
                    row.put("volumen",StringHelper.roundDouble(rs.getString("volumen"),3));
                    row.put("estatus",rs.getInt("estatus"));
                    
                    row.put("status_r",rs.getInt("status_r"));
                    row.put("tr_id",rs.getInt("tr_id"));
                    row.put("cant_r",StringHelper.roundDouble(rs.getString("cant_r"),2));
                    row.put("cargo_r",StringHelper.roundDouble(rs.getString("cargo_r"),2));
                    return row;
                }
            }
        );
        return hm;
    }
    
    
    
    //Metodo que obtiene datos para el grid de Administrador de Viajes
    @Override
    public ArrayList<HashMap<String, Object>> getLogAdmViaje_PaginaGrid(String data_string, int offset, int pageSize, String orderBy, String asc) {
        String sql_busqueda = "select id from gral_bus_catalogos(?) as foo (id integer)";
        
	String sql_to_query = ""
        + "SELECT "
            + "log_viaje.id, "
            + "log_viaje.folio, "
            + "to_char(log_viaje.fecha::timestamp with time zone, 'dd/mm/yyyy') as fecha, "
            + "(case when log_choferes.nombre is null then '' else log_choferes.nombre end) ||' '|| (case when log_choferes.apellido_paterno is null then '' else log_choferes.apellido_paterno end) ||' '|| case when log_choferes.apellido_materno is null then '' else log_choferes.apellido_materno	END AS operador,  "
            + "(case when log_vehiculo_marca.id is null then '' else log_vehiculo_marca.titulo end) AS marca_unidad, "
            + "(case when log_vehiculo_clase.id is null then '' else log_vehiculo_clase.titulo end) AS clase, "
            + "(case when log_status.id is null then '' else log_status.titulo end) AS status_viaje,"
            + "(case when inv_alm.id is null then '' else inv_alm.titulo end) AS almacen "
        + "FROM log_viaje "
        + "left join log_choferes on log_choferes.id=log_viaje.log_chofer_id "
        + "left join log_vehiculos on log_vehiculos.id=log_viaje.log_vehiculo_id "
        + "left join log_vehiculo_clase on log_vehiculo_clase.id=log_viaje.log_vehiculo_clase_id " 
        + "left join log_vehiculo_marca on log_vehiculo_marca.id=log_vehiculos.log_vehiculo_marca_id " 
        + "left join log_status on log_status.id=log_viaje.log_status_id " 
        + "left join inv_alm on inv_alm.id=log_viaje.inv_alm_id "
        +"JOIN ("+sql_busqueda+") AS sbt on sbt.id=log_viaje.id "
        +"order by "+orderBy+" "+asc+" limit ? OFFSET ?";
        
        //System.out.println("Paginado Viajes: "+sql_to_query);
        ArrayList<HashMap<String, Object>> hm = (ArrayList<HashMap<String, Object>>) this.jdbcTemplate.query(
            sql_to_query, 
            new Object[]{data_string, new Integer(pageSize),new Integer(offset)}, new RowMapper() {
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    HashMap<String, Object> row = new HashMap<String, Object>();
                    row.put("id",rs.getInt("id"));
                    row.put("folio",rs.getString("folio"));
                    row.put("fecha",rs.getString("fecha"));
                    row.put("operador",rs.getString("operador"));
                    row.put("marca_unidad",rs.getString("marca_unidad"));
                    row.put("clase",rs.getString("clase"));
                    row.put("status_viaje",rs.getString("status_viaje"));
                    row.put("almacen",rs.getString("almacen"));
                    return row;
                }
            }
        );
        return hm; 
    }
    
    
    
    
    
    //Obtener los datos de un Viaje en especifico
    @Override
    public ArrayList<HashMap<String, Object>> getLoAdmViaje_Datos(Integer id) {
        String sql_to_query = ""
        + "SELECT "
            + "log_viaje.id,"
            + "log_viaje.folio,"
            + "log_viaje.fecha,"
            + "(case when EXTRACT(HOUR FROM log_viaje.hora)=0 AND EXTRACT(MINUTE FROM log_viaje.hora)=0 THEN '00:00' else (lpad(EXTRACT(HOUR FROM log_viaje.hora)::character varying, 2, '0')||':'||lpad(EXTRACT(MINUTE FROM log_viaje.hora)::character varying, 2, '0'))END) AS hora, "
            + "log_viaje.gral_suc_id AS suc_id,"
            + "log_viaje.log_vehiculo_id AS vehiculo_id,"
            + "log_vehiculos.folio AS no_unidad, "
            + "(case when log_vehiculo_marca.id is null then '' else log_vehiculo_marca.titulo end) as unidad, "
            + "(case when log_vehiculo_tipo.id is null then 0 else log_vehiculo_tipo.id end) as t_unidad_id,"
            + "(case when log_vehiculo_tipo.id is null then '' else log_vehiculo_tipo.titulo end) as tipo_unidad,"
            + "log_viaje.no_economico, "
            + "log_viaje.placas, "
            + "log_vehiculos.cap_volumen,"
            + "log_vehiculos.cap_peso,"
            + "(case when log_vehiculo_clase.id is null then '' else log_vehiculo_clase.titulo end) AS clase, "
            + "log_choferes.clave AS no_operador,"
            + "(case when log_choferes.nombre is null then '' else log_choferes.nombre end) || (case when log_choferes.apellido_paterno is null then '' else ' '||log_choferes.apellido_paterno end) || case when log_choferes.apellido_materno is null then '' else ' '||log_choferes.apellido_materno END AS operador, "
            + "log_viaje.observaciones, "
            + "log_viaje.log_status_id as status,"
            + "log_viaje.inv_alm_id as alm_id,"
            + "log_viaje.log_tipo_distribucion_id as tipo_dist_id, "
            + "(case when log_ruta.id is null then 0 else log_ruta.id end) as ruta_id,"
            + "(case when log_ruta.id is null then '' else log_ruta.folio end) as no_ruta,"
            + "(case when log_ruta.id is null then '' else log_ruta.titulo end) as titulo_ruta,"
            + "(case when log_ruta_tipo.id is null then '' else log_ruta_tipo.titulo end) as tipo_ruta,"
            + "(case when log_ruta.id is null then 0 else log_ruta.km end) as km_ruta,"
            //+ "(case when log_ruta.id is null then 0 else log_ruta_tipo_unidad.costo end) as costo_ruta "
            + "(case when log_viaje.costo_ruta=0 then (case when log_ruta_tipo_unidad.costo is null then 0 else log_ruta_tipo_unidad.costo end) else log_viaje.costo_ruta end) as costo_ruta,"
            + "log_viaje.tipo_costeo "
        + "from log_viaje "
        + "left join log_choferes on log_choferes.id=log_viaje.log_chofer_id "
        + "join log_vehiculos on log_vehiculos.id=log_viaje.log_vehiculo_id "
        + "left join log_vehiculo_tipo on log_vehiculo_tipo.id=log_vehiculos.log_vehiculo_tipo_id "
        + "left join log_vehiculo_marca on log_vehiculo_marca.id=log_vehiculos.log_vehiculo_marca_id "
        + "left join log_vehiculo_clase on log_vehiculo_clase.id=log_viaje.log_vehiculo_clase_id " 
        + "left join log_ruta on log_ruta.id=log_viaje.log_ruta_id "
        + "left join log_ruta_tipo_unidad on (log_ruta_tipo_unidad.log_ruta_id=log_ruta.id and log_ruta_tipo_unidad.log_vehiculo_tipo_id=log_vehiculos.log_vehiculo_tipo_id) "
        + "left join log_ruta_tipo on log_ruta_tipo.id=log_ruta.log_ruta_tipo_id "
        + "where log_viaje.id=? order by log_viaje.id;";
        
        ArrayList<HashMap<String, Object>> hm = (ArrayList<HashMap<String, Object>>) this.jdbcTemplate.query(
            sql_to_query,
            new Object[]{new Integer(id)}, new RowMapper(){
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    HashMap<String, Object> row = new HashMap<String, Object>();
                    row.put("id",rs.getInt("id"));
                    row.put("folio",rs.getString("folio"));
                    row.put("fecha",rs.getDate("fecha"));
                    row.put("hora",rs.getString("hora"));
                    row.put("suc_id",rs.getInt("suc_id"));
                    row.put("vehiculo_id",rs.getInt("vehiculo_id"));
                    row.put("no_unidad",rs.getString("no_unidad"));
                    row.put("unidad",rs.getString("unidad"));
                    row.put("t_unidad_id",rs.getInt("t_unidad_id"));
                    row.put("tipo_unidad",rs.getString("tipo_unidad"));
                    row.put("no_economico",rs.getString("no_economico"));
                    row.put("placas",rs.getString("placas"));
                    row.put("clase",rs.getString("clase"));
                    row.put("cap_volumen",StringHelper.roundDouble(rs.getString("cap_volumen"),3));
                    row.put("cap_peso",StringHelper.roundDouble(rs.getString("cap_peso"),3));
                    row.put("operador",rs.getString("operador"));
                    row.put("no_operador",rs.getString("no_operador"));
                    row.put("observaciones",rs.getString("observaciones"));
                    row.put("status",rs.getInt("status"));
                    row.put("alm_id",rs.getInt("alm_id"));
                    row.put("tipo_dist_id",rs.getInt("tipo_dist_id"));
                    row.put("tipo_costeo",rs.getInt("tipo_costeo"));
                    
                    row.put("ruta_id",rs.getString("ruta_id"));
                    row.put("no_ruta",rs.getString("no_ruta"));
                    row.put("titulo_ruta",rs.getString("titulo_ruta"));
                    row.put("tipo_ruta",rs.getString("tipo_ruta"));
                    row.put("km_ruta",StringHelper.roundDouble(rs.getString("km_ruta"),2));
                    row.put("costo_ruta",StringHelper.roundDouble(rs.getString("costo_ruta"),2));
                    
                    return row;
                }
            }
        );
        return hm;
    }
    
    
    
    
    //Obtener el detalle del viaje
    @Override
    public ArrayList<HashMap<String, Object>> getLoAdmViaje_DatosGrid(Integer id) {
        String sql_to_query = ""
        + "SELECT "
            + "cxc_clie.numero_control AS no_clie,"
            + "substr(upper(cxc_clie.razon_social),1,12) AS clie,"
            + "log_viaje_det.id AS det_id,"
            + "log_doc_carga.id AS cga_id,"
            + "(case when log_doc.fecha_carga is null then '' else to_char(log_doc.fecha_carga::timestamp with time zone, 'dd-mm-yyyy') end) as fecha_carga,"
            + "log_doc_ped.id AS ped_id,"
            + "log_doc_carga.no_carga,"
            + "log_doc_carga.fecha_entrega,"
            + "log_doc_ped.no_pedido,"
            + "cxc_destinatarios.id AS id_dest,"
            + "cxc_destinatarios.folio_ext AS no_dest,"
            + "cxc_destinatarios.razon_social AS nombre_dest,"
            + "log_viaje_det.solicitar_firma AS firma,"
            + "log_viaje_det.solicitar_sello AS sello,"
            + "log_viaje_det.solicitar_efectivo AS efectivo,"
            + "log_viaje_det.solicitar_cheque AS cheque,"
            + "(case when inv_prod.id is null then 0 else cxc_destinatarios.inv_prod_id end) as serv_id,"
            + "(case when inv_prod.id is null then 0 else cxc_destinatarios.costo_serv end) as serv_costo,"
            + "(case when inv_prod.id is null then '' else inv_prod.sku end) as serv_codigo,"
            + "(case when inv_prod.id is null then '' else inv_prod.descripcion end) as serv_desc,"
            + "(case when um.id is null then '' else um.titulo end) as serv_um,"
                
            + "(case when log_viaje_det.log_tarifa_clase_id=5 then log_viaje_det.log_tarifa_clase_id else (case when log_viaje_det.log_status_id=0 then (case when tbl_tarifa.log_tarifa_tipo_id is null then 0 else tbl_tarifa.log_tarifa_clase_id end) else log_viaje_det.log_tarifa_clase_id end) end) as t_clase_id,"
            + "(case when log_viaje_det.log_status_id=0 then (case when tbl_tarifa.log_tarifa_tipo_id is null then 0 else tbl_tarifa.log_tarifa_tipo_id end) else log_viaje_det.log_tarifa_tipo_id end) as t_tipo_id,"   
            + "(case when tbl_tarifa.log_tarifa_tipo_id is null then '' else tbl_tarifa.clase end) as t_clase,"
            + "(case when tbl_tarifa.log_tarifa_tipo_id is null then '' else tbl_tarifa.tipo_tarifa end) as t_tipo,"
            
            + "(case when log_viaje_det.log_status_id=0 then "
                + "(case when tbl_tarifa.log_tarifa_tipo_id is null then 0 else "
                    + "(case when tbl_tarifa.operacion=1 then "
                        + "(case when tbl_tarifa.base=1 then (1 * tbl_tarifa.precio::double precision) "
                            + "when tbl_tarifa.base=2 then ((sum(log_doc_ped_det.peso)/1000) * tbl_tarifa.precio::double precision) "
                            + "when tbl_tarifa.base=3 then (sum(log_doc_ped_det.volumen) * tbl_tarifa.precio::double precision) "
                        + "else 0 end) "
                    + "else 0 end)"
                + "end) "
            + "else log_viaje_det.precio_tarifa_venta end) as precio_venta, "
            
            + "gral_mun.id AS mun_id,"
            + "upper(gral_mun.titulo) AS municipio,"
            + "log_doc_ped.log_status_id AS status_ped,"
            //+ "(case when log_viaje_det.status=0 then '' when log_viaje_det.status=1 then 'Enviado' else '' end) as status_det,"
            + "(case when log_status.id is null then '' else log_status.titulo end) as status_det, "
            + "sum(log_doc_ped_det.cantidad) as cant_uni, "
            + "sum(log_doc_ped_det.peso) AS peso,"
            + "sum(log_doc_ped_det.volumen) AS volumen,"
            + "log_viaje_det.cant_tar,"
            + "log_viaje_det.cant_car "
        + "FROM log_viaje_det "
        + "JOIN log_doc_carga on log_doc_carga.id=log_viaje_det.log_doc_carga_id "
        + "join log_doc on log_doc.id=log_doc_carga.log_doc_id "
        + "JOIN log_doc_ped on log_doc_ped.id=log_viaje_det.log_doc_ped_id  "
        + "JOIN log_doc_ped_det on log_doc_ped_det.log_doc_ped_id=log_doc_ped.id "
        + "JOIN cxc_clie on cxc_clie.id=log_viaje_det.cxc_clie_id "
        + "JOIN cxc_destinatarios on cxc_destinatarios.id=log_doc_ped.cxc_dest_id  "
        + "left join inv_prod on inv_prod.id=cxc_destinatarios.inv_prod_id  "
        + "left join inv_prod_unidades as um on um.id=inv_prod.unidad_id "
        + "JOIN gral_mun on gral_mun.id=log_viaje_det.gral_mun_id  "
        + "left join log_status on log_status.id=log_viaje_det.log_status_id  "
        
        + "left join log_tarifa_clie on (log_tarifa_clie.cxc_clie_id=cxc_clie.id and log_tarifa_clie.log_tarifa_clase_id=log_doc_ped.log_tarifa_clase_id) "
        + "left join (select log_tarifario_venta.cxc_clie_id, log_tarifa_tipo.log_tarifa_clase_id, log_tarifario_venta_det.log_tarifa_tipo_id, log_tarifario_venta.gral_mun_id, log_tarifa_tipo.operacion, log_tarifa_tipo.base, log_tarifario_venta_det.valor as precio, upper(log_tarifa_clase.titulo) as clase, upper(log_tarifa_tipo.titulo) as tipo_tarifa from log_tarifario_venta join log_tarifario_venta_det on (log_tarifario_venta_det.log_tarifario_venta_id=log_tarifario_venta.id) join log_tarifa_tipo on log_tarifa_tipo.id=log_tarifario_venta_det.log_tarifa_tipo_id join log_tarifa_clase on log_tarifa_clase.id=log_tarifa_tipo.log_tarifa_clase_id) as tbl_tarifa on (tbl_tarifa.cxc_clie_id=log_tarifa_clie.cxc_clie_id and tbl_tarifa.log_tarifa_clase_id=log_tarifa_clie.log_tarifa_clase_id and tbl_tarifa.log_tarifa_tipo_id=log_tarifa_clie.log_tarifa_tipo_id and tbl_tarifa.gral_mun_id=gral_mun.id ) "
        
        + "WHERE log_viaje_det.log_viaje_id=? "
        + "group by cxc_clie.numero_control,cxc_clie.razon_social,log_viaje_det.id,log_doc_carga.id,log_doc.fecha_carga,log_doc_ped.id,log_doc_carga.no_carga,log_doc_carga.fecha_entrega, log_doc_ped.no_pedido, cxc_destinatarios.id,cxc_destinatarios.folio_ext,cxc_destinatarios.razon_social,log_viaje_det.solicitar_firma,log_viaje_det.solicitar_sello,log_viaje_det.solicitar_efectivo, inv_prod.id, um.id, gral_mun.id,gral_mun.titulo,log_doc_ped.log_status_id, log_status.id,  tbl_tarifa.log_tarifa_clase_id, tbl_tarifa.log_tarifa_tipo_id, tbl_tarifa.clase, tbl_tarifa.tipo_tarifa, tbl_tarifa.operacion, tbl_tarifa.base, tbl_tarifa.precio  "
        + "order by log_viaje_det.id";
        
        //System.out.println("sql_to_query: "+sql_to_query);
        
        ArrayList<HashMap<String, Object>> hm = (ArrayList<HashMap<String, Object>>) this.jdbcTemplate.query(
            sql_to_query,
            new Object[]{new Integer(id)}, new RowMapper(){
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    HashMap<String, Object> row = new HashMap<String, Object>();
                    row.put("no_clie",rs.getString("no_clie"));
                    row.put("clie",rs.getString("clie"));
                    row.put("det_id",rs.getInt("det_id"));
                    row.put("cga_id",rs.getInt("cga_id"));
                    row.put("f_carga",rs.getString("fecha_carga"));
                    row.put("f_entrega",rs.getString("fecha_entrega"));
                    row.put("ped_id",rs.getInt("ped_id"));
                    row.put("no_carga",rs.getString("no_carga"));
                    row.put("no_pedido",rs.getString("no_pedido"));
                    row.put("id_dest",rs.getInt("id_dest"));
                    row.put("no_dest",rs.getString("no_dest"));
                    row.put("nombre_dest",rs.getString("nombre_dest"));
                    row.put("firma",rs.getBoolean("firma"));
                    row.put("sello",rs.getBoolean("sello"));
                    row.put("efectivo",rs.getBoolean("efectivo"));
                    row.put("cheque",rs.getBoolean("cheque"));
                    row.put("mun_id",rs.getInt("mun_id"));
                    row.put("municipio",rs.getString("municipio"));
                    row.put("status_ped",rs.getString("status_ped"));
                    row.put("status_det",rs.getString("status_det"));
                    row.put("cant_uni",StringHelper.roundDouble(rs.getString("cant_uni"),2));
                    row.put("peso",StringHelper.roundDouble(rs.getString("peso"),3));
                    row.put("volumen",StringHelper.roundDouble(rs.getString("volumen"),3));
                    row.put("serv",rs.getInt("serv_id")+"&|&"+rs.getString("serv_codigo")+"&|&"+rs.getString("serv_desc")+"&|&"+rs.getString("serv_um")+"&|&"+StringHelper.roundDouble(rs.getString("serv_costo"),2));
                    
                    row.put("t_clase_id",rs.getInt("t_clase_id"));
                    row.put("t_tipo_id",rs.getInt("t_tipo_id"));
                    row.put("t_clase",rs.getString("t_clase"));
                    row.put("t_tipo",rs.getString("t_tipo"));
                    
                    //Para la tarifa clase 5, se calcula al final el precio de acuerdo a la sumatoria en volumen de todos los pedidos de un destinatario
                    if(rs.getInt("t_clase_id")==5){
                        row.put("precio_venta",StringHelper.roundDouble(0,2));
                    }else{
                        row.put("precio_venta",StringHelper.roundDouble(rs.getString("precio_venta"),2));
                    }
                    
                    row.put("cant_tar",StringHelper.roundDouble(rs.getString("cant_tar"),2));
                    row.put("cant_car",StringHelper.roundDouble(rs.getString("cant_car"),2));
                    return row;
                }
            }
        );
        return hm;
    }
    
    
    //Obtener servicios Adicionales agregados al viaje
    @Override
    public ArrayList<HashMap<String, Object>> getLoAdmViaje_Adicionales(Integer id) {
        String sql_to_query = ""
        + "select "
            + "log_viaje_serv_adic.id as id_reg,"
            + "inv_prod.id as id_prod,"
            + "inv_prod.sku as codigo, "
            + "inv_prod.descripcion, "
            + "(case when inv_prod_unidades.titulo is null then '' else inv_prod_unidades.titulo end) as unidad,"
            + "log_viaje_serv_adic.precio,"
            + "log_viaje_serv_adic.log_doc_ped_id as ped_id "
        + "from log_viaje_serv_adic "
        + "join inv_prod on inv_prod.id=log_viaje_serv_adic.inv_prod_id "
        + "left join inv_prod_unidades on inv_prod_unidades.id=log_viaje_serv_adic.inv_prod_unidad_id "
        + "where log_viaje_serv_adic.log_viaje_id=? order by log_viaje_serv_adic.id";
        
        //System.out.println("grid de productos equivalentes"+sql_to_query);
        ArrayList<HashMap<String, Object>> hm = (ArrayList<HashMap<String, Object>>) this.jdbcTemplate.query(
            sql_to_query,
            new Object[]{new Integer(id)}, new RowMapper(){
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    HashMap<String, Object> row = new HashMap<String, Object>();
                    row.put("id_reg",rs.getInt("id_reg"));
                    row.put("id_prod",rs.getInt("id_prod"));
                    row.put("codigo",rs.getString("codigo"));
                    row.put("descripcion",rs.getString("descripcion"));
                    row.put("unidad",rs.getString("unidad"));
                    row.put("precio",StringHelper.roundDouble(rs.getString("precio"),2));
                    row.put("ped_id",rs.getInt("ped_id"));
                    return row;
                }
            }
        );
        return hm;
    }
    
    
    //Obtener todos los tipos de recchazos
    @Override
    public ArrayList<HashMap<String, Object>> getLogAdmViaje_TiposDeRechazo(Integer id) {
        String sql_to_query = "select id, titulo from log_tipo_rechazo where borrado_logico=false and gral_emp_id=? order by titulo;";
        
        //System.out.println("grid de productos equivalentes"+sql_to_query);
        ArrayList<HashMap<String, Object>> hm = (ArrayList<HashMap<String, Object>>) this.jdbcTemplate.query(
            sql_to_query,
            new Object[]{new Integer(id)}, new RowMapper(){
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
    
    //Obtener todos los tipos de Distribucion
    @Override
    public ArrayList<HashMap<String, Object>> getLogAdmViaje_TiposDeDistribucion() {
        String sql_to_query = "select id, titulo from log_tipo_distribucion where borrado_logico=false order by titulo;";
        
        //System.out.println("grid de productos equivalentes"+sql_to_query);
        ArrayList<HashMap<String, Object>> hm = (ArrayList<HashMap<String, Object>>) this.jdbcTemplate.query(
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
        return hm;
    }
    
    
    
    
    //Obtener Costo
    @Override
    public HashMap<String, Object> getLoAdmViaje_CantidadPorUnidadDeMEdida(Integer id_empresa, String no_clie, String no_carga, String no_pedido, String unidad_medida) {
        String sql_to_query = ""
        + "select (case when cantidad is null then 0 else cantidad end) as cantidad from ("
            + "select sum(log_doc_ped_det.cantidad) as cantidad "
            + "from log_doc "
            + "join log_doc_carga on (log_doc_carga.log_doc_id=log_doc.id and trim(log_doc_carga.no_carga)=?) "
            + "join log_doc_ped on (log_doc_ped.log_doc_carga_id=log_doc_carga.id and trim(log_doc_ped.no_pedido)=?) "
            + "join log_doc_ped_det on log_doc_ped_det.log_doc_ped_id=log_doc_ped.id  "
            + "join inv_prod on inv_prod.id=log_doc_ped_det.inv_prod_id  "
            + "join inv_prod_unidades on (inv_prod_unidades.id=log_doc_ped_det.inv_prod_unidad_id and inv_prod_unidades.titulo ilike '"+unidad_medida+"%') "
            + "join cxc_clie on (cxc_clie.id=log_doc.cxc_clie_id and trim(cxc_clie.numero_control)=?) "
            + "where log_doc.gral_emp_id=?"
        + ") as sbt;";
        
        System.out.println("getLoAdmViaje_Costos: "+sql_to_query);
        HashMap<String, Object> hm_return = (HashMap<String, Object>) this.jdbcTemplate.queryForObject(
            sql_to_query, 
            new Object[]{no_carga, no_pedido, no_clie, new Integer(id_empresa)}, new RowMapper() {
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    HashMap<String, Object> row = new HashMap<String, Object>();
                    row.put("cantidad",StringHelper.roundDouble(rs.getString("cantidad"),2));
                    return row;
                }
            }
        );
        
        return hm_return;
    }
    
    
    
    
    
    
    //Obtener el tipo de Unidad a Utilizar de acuerdo a la Cantidad de Tarimas o Cajas
    @Override
    public ArrayList<HashMap<String, Object>> getLoAdmViaje_TipoDeUnidad(Integer id_empresa, String unidad_medida, String cantidad) {
        String sql_to_query = "";
        
        if(unidad_medida.equals("TARIMA")){
            sql_to_query = "select id,titulo from log_vehiculo_tipo where gral_emp_id=? and borrado_logico=false and ("+cantidad+" between tarima_inicio and tarima_fin) ;";
        }else{
            if(unidad_medida.equals("CARTON")){
                sql_to_query = "select id,titulo from log_vehiculo_tipo where gral_emp_id=? and borrado_logico=false and ("+cantidad+" between carton_inicio and carton_fin);";
            }
        }
        
        System.out.println("getLoAdmViaje_TipoDeUnidad: "+sql_to_query);
        ArrayList<HashMap<String, Object>> hm_return = (ArrayList<HashMap<String, Object>>) this.jdbcTemplate.query(
            sql_to_query,
            new Object[]{new Integer(id_empresa)}, new RowMapper(){
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    HashMap<String, Object> row = new HashMap<String, Object>();
                    row.put("id",rs.getInt("id"));
                    row.put("titulo",rs.getString("titulo"));
                    return row;
                }
            }
        );
        
        return hm_return;
    }
    
    
    
    
    
    
    
    //Obtener el Costo de acuerdo al Tipo de Distribucion, Tipo de Unidad y Cantidades por unidad de Medida
    @Override
    public HashMap<String, Object> getLoAdmViaje_Costos(Integer id_empresa, String tipo_dist, Integer tipo_de_unidad_id, String unidad_medida, String cantidad) {
        /*
        1=DIRECTO
        2=HOT SHOT
        3=LTL
        4=EXPRES
         */
        
        String sql_to_query = ""
        + "select (case when cantidad is null then 0 else cantidad end) as cantidad from ("
            + "select sum(log_doc_ped_det.cantidad) as cantidad "
            + "from log_doc "
            + "join log_doc_carga on (log_doc_carga.log_doc_id=log_doc.id and trim(log_doc_carga.no_carga)=?) "
            + "join log_doc_ped on (log_doc_ped.log_doc_carga_id=log_doc_carga.id and trim(log_doc_ped.no_pedido)=?) "
            + "join log_doc_ped_det on log_doc_ped_det.log_doc_ped_id=log_doc_ped.id  "
            + "join inv_prod on inv_prod.id=log_doc_ped_det.inv_prod_id  "
            + "join inv_prod_unidades on (inv_prod_unidades.id=log_doc_ped_det.inv_prod_unidad_id and inv_prod_unidades.titulo ilike '"+unidad_medida+"%') "
            + "join cxc_clie on (cxc_clie.id=log_doc.cxc_clie_id and trim(cxc_clie.numero_control)=?) "
            + "where log_doc.gral_emp_id=?"
        + ") as sbt;";
        
        System.out.println("getLoAdmViaje_Costos: "+sql_to_query);
        HashMap<String, Object> hm_return = (HashMap<String, Object>) this.jdbcTemplate.queryForObject(
            sql_to_query, 
            new Object[]{new Integer(id_empresa)}, new RowMapper() {
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    HashMap<String, Object> row = new HashMap<String, Object>();
                    row.put("cantidad",StringHelper.roundDouble(rs.getString("cantidad"),2));
                    return row;
                }
            }
        );
        
        return hm_return;
    }
    
    
    
    
    
    
    
    
    //Obtener el precio de venta dependiendo del rango de volumen
    @Override
    public HashMap<String, Object> getLoAdmViaje_PrecioVenta(String clase_tarifa_id, String volumen) {
        HashMap<String, Object> hm_return = new HashMap<String, Object>();
        
        String sql_to_query = "SELECT count(log_tarifa_clie.id) as exis  FROM log_tarifa_clie "
                + "join log_tarifario_venta on log_tarifario_venta.cxc_clie_id=log_tarifa_clie.cxc_clie_id "
                + "join log_tarifario_venta_det on (log_tarifario_venta_det.log_tarifario_venta_id=log_tarifario_venta.id and log_tarifario_venta_det.log_tarifa_tipo_id=log_tarifa_clie.log_tarifa_tipo_id and (now()::date between log_tarifario_venta_det.fecha_inicio and log_tarifario_venta_det.fecha_fin))  "
                + "join log_tarifario_venta_det_rango on (log_tarifario_venta_det_rango.log_tarifario_venta_det_id=log_tarifario_venta_det.id and ("+volumen+"::double precision>valor1 and "+volumen+"::double precision<=valor2)) "
                + "where log_tarifa_clie.log_tarifa_clase_id=?;";
        //System.out.println("Validacion:"+sql_to_query);
        //log.log(Level.INFO, "Ejecutando query de {0}", sql_to_query);
        
        HashMap<String, Object> hm = (HashMap<String, Object>) this.jdbcTemplate.queryForObject(
            sql_to_query, 
            new Object[]{new Integer(clase_tarifa_id)}, new RowMapper() {
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    HashMap<String, Object> row = new HashMap<String, Object>();
                    row.put("exis",rs.getString("exis"));
                    return row;
                }
            }
        );
        
        
        if(Integer.valueOf(hm.get("exis").toString())>0){
            String sql_to_query2 = ""
            + "SELECT "
                + "log_tarifa_clie.cxc_clie_id,  "
                + "log_tarifa_clie.log_tarifa_clase_id as t_clase_id, "
                + "log_tarifa_clie.log_tarifa_tipo_id  as t_tipo_id, "
                + "(case when log_tarifa_clase.id is null then '' else log_tarifa_clase.titulo end) as t_clase,"
                + "(case when log_tarifa_tipo.id is null then '' else log_tarifa_tipo.titulo end) as t_tipo, "
                + "log_tarifario_venta_det_rango.titulo, "
                + "log_tarifario_venta_det_rango.valor1, "
                + "log_tarifario_venta_det_rango.valor2, "
                + "(log_tarifario_venta_det_rango.precio * "+volumen+"::double precision) as precio_total, "
                + "log_tarifario_venta_det_rango.precio as precio_venta "
            + "FROM log_tarifa_clie "
            + "join log_tarifario_venta on log_tarifario_venta.cxc_clie_id=log_tarifa_clie.cxc_clie_id "
            + "join log_tarifario_venta_det on (log_tarifario_venta_det.log_tarifario_venta_id=log_tarifario_venta.id and log_tarifario_venta_det.log_tarifa_tipo_id=log_tarifa_clie.log_tarifa_tipo_id and (now()::date between log_tarifario_venta_det.fecha_inicio and log_tarifario_venta_det.fecha_fin))  "
            + "join log_tarifario_venta_det_rango on (log_tarifario_venta_det_rango.log_tarifario_venta_det_id=log_tarifario_venta_det.id and ("+volumen+"::double precision>valor1 and "+volumen+"::double precision<=valor2)) "
            + "left join log_tarifa_clase on log_tarifa_clase.id=log_tarifa_clie.log_tarifa_clase_id "
            + "left join log_tarifa_tipo on log_tarifa_tipo.id=log_tarifa_clie.log_tarifa_tipo_id "
            + "where log_tarifa_clie.log_tarifa_clase_id=? limit 1;";
            
            //System.out.println("clase_tarifa_id: "+clase_tarifa_id+"     volumen: "+volumen);
            //System.out.println("sql_to_query2: "+sql_to_query2);
            
            HashMap<String, Object> hm2 = (HashMap<String, Object>) this.jdbcTemplate.queryForObject(
                sql_to_query2, 
                new Object[]{new Integer(clase_tarifa_id)}, new RowMapper() {
                    @Override
                    public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                        HashMap<String, Object> row = new HashMap<String, Object>();
                        row.put("t_clase_id",rs.getInt("t_clase_id"));
                        row.put("t_tipo_id",rs.getInt("t_tipo_id"));
                        row.put("t_clase",rs.getString("t_clase"));
                        row.put("t_tipo",rs.getString("t_tipo"));
                        row.put("precio_total",StringHelper.roundDouble(rs.getString("precio_total"),2));
                        row.put("precio_venta",StringHelper.roundDouble(rs.getString("precio_venta"),2));
                        return row;
                    }
                }
            );
            
            hm_return=hm2;
        }else{
            HashMap<String, Object> row = new HashMap<String, Object>();
            row.put("t_clase_id","0");
            row.put("t_tipo_id","0");
            row.put("t_clase","");
            row.put("t_tipo","");
            row.put("precio_venta",StringHelper.roundDouble(0,2));
            
            hm_return=row;
        }
        
        System.out.println("t_clase_id:"+hm_return.get("t_clase_id")+" | t_tipo_id:"+hm_return.get("t_tipo_id") +" | volumen:"+volumen+" | precio_venta:"+hm_return.get("precio_venta"));
        
        return hm_return;
    }
    
    
    
 //Método para datos del pdf de viaje
    @Override
    public HashMap<String, String> getLoAdmViaje_DatosPdf(Integer id) {
        
        HashMap<String, String> datos = new HashMap<String, String>();
        
        String sql_to_query = ""
        + "SELECT "
            + "log_viaje.id,"
            + "log_viaje.folio,"
            + "log_viaje.fecha,"
            + "(case when EXTRACT(HOUR FROM log_viaje.hora)=0 AND EXTRACT(MINUTE FROM log_viaje.hora)=0 THEN '00:00' else (lpad(EXTRACT(HOUR FROM log_viaje.hora)::character varying, 2, '0')||':'||lpad(EXTRACT(MINUTE FROM log_viaje.hora)::character varying, 2, '0'))END) AS hora, "
            + "log_viaje.gral_suc_id AS suc_id,"
            + "gral_suc.titulo AS nom_suc,"
            + "log_viaje.log_vehiculo_id AS vehiculo_id,"
            + "log_vehiculos.folio AS no_unidad, "
            + "(case when log_vehiculo_marca.id is null then '' else log_vehiculo_marca.titulo end) as unidad, "
            + "log_viaje.no_economico, "
            + "log_viaje.placas, "
            + "(case when log_vehiculo_clase.id is null then '' else log_vehiculo_clase.titulo end) AS tipo, "
            + "log_choferes.clave AS no_operador,"
            + "(case when log_choferes.nombre is null then '' else log_choferes.nombre end) || ' ' || (case when log_choferes.apellido_paterno is null then '' else log_choferes.apellido_paterno end) || ' ' || case when log_choferes.apellido_materno is null then '' else log_choferes.apellido_materno END AS operador, "
            + "log_viaje.observaciones, "
            + "log_viaje.log_status_id as status, "
            + "(case when log_ruta.id is null then 0 else log_ruta.id end) as ruta_id,"
            + "(case when log_ruta.id is null then '' else log_ruta.folio end) as no_ruta,"
            + "(case when log_ruta.id is null then '' else log_ruta.titulo end) as titulo_ruta,"
            + "(case when log_ruta_tipo.id is null then '' else log_ruta_tipo.titulo end) as tipo_ruta,"
            + "(case when log_ruta.id is null then 0 else log_ruta.km end) as km_ruta,"
            //+ "(case when log_ruta.id is null then 0 else log_ruta_tipo_unidad.costo end) as costo_ruta, "
            + "(case when log_viaje.costo_ruta=0 then (case when log_ruta_tipo_unidad.costo is null then 0 else log_ruta_tipo_unidad.costo end) else log_viaje.costo_ruta end) as costo_ruta, "
            + "inv_alm.titulo as titulo_almacen, "
            + "(case when cxp_prov.id is null then '' else cxp_prov.razon_social end) as tranportista_proveedor "
        + "from log_viaje "
        + "left join log_choferes on log_choferes.id=log_viaje.log_chofer_id "
        + "join log_vehiculos on log_vehiculos.id=log_viaje.log_vehiculo_id "
        + "left join log_vehiculo_clase on log_vehiculo_clase.id=log_viaje.log_vehiculo_clase_id "
        + "left join log_vehiculo_marca on log_vehiculo_marca.id=log_vehiculos.log_vehiculo_marca_id "
        + "left join gral_suc on gral_suc.id=log_viaje.gral_suc_id "     
        + "left join log_ruta on log_ruta.id=log_viaje.log_ruta_id "
        + "left join log_ruta_tipo_unidad on (log_ruta_tipo_unidad.log_ruta_id=log_ruta.id and log_ruta_tipo_unidad.log_vehiculo_tipo_id=log_vehiculos.log_vehiculo_tipo_id) "
        + "left join log_ruta_tipo on log_ruta_tipo.id=log_ruta.log_ruta_tipo_id "    
        + "left join inv_alm on inv_alm.id=log_viaje.inv_alm_id "
        + "left join cxp_prov on cxp_prov.id=log_vehiculos.cxp_prov_id "
        + "WHERE log_viaje.id="+id+";";
        
       //System.out.println("DATOS PARA EL PDF:"+sql_to_query);
       Map<String, Object> map = this.getJdbcTemplate().queryForMap(sql_to_query);
       datos.put("id",map.get("id").toString());
       datos.put("folio",map.get("folio").toString());
       datos.put("fecha",map.get("fecha").toString());
       datos.put("hora",map.get("hora").toString());
       datos.put("suc_id",map.get("suc_id").toString());
       datos.put("nom_suc",map.get("nom_suc").toString());
       datos.put("vehiculo_id",map.get("vehiculo_id").toString());
       datos.put("no_unidad",map.get("no_unidad").toString());
       datos.put("unidad",map.get("unidad").toString());
       datos.put("no_economico",map.get("no_economico").toString());
       datos.put("placas",map.get("placas").toString());
       datos.put("tipo",map.get("tipo").toString());
       datos.put("operador",map.get("operador").toString());
       datos.put("no_operador",map.get("no_operador").toString());
       datos.put("observaciones",map.get("observaciones").toString());
       datos.put("status",map.get("status").toString());
       
       datos.put("ruta_id",map.get("ruta_id").toString());
       datos.put("no_ruta",map.get("no_ruta").toString());
       datos.put("titulo_ruta",map.get("titulo_ruta").toString());
       datos.put("tipo_ruta",map.get("tipo_ruta").toString());
       datos.put("km_ruta",StringHelper.roundDouble(map.get("km_ruta").toString(),2));
       datos.put("costo_ruta",StringHelper.roundDouble(map.get("costo_ruta").toString(),2));
       datos.put("titulo_almacen",map.get("titulo_almacen").toString());
       datos.put("tranportista_proveedor",map.get("tranportista_proveedor").toString());
        
       return datos;
    }
    
    
    
      //Obtener el detalle del viaje
    @Override
    public ArrayList<HashMap<String, String>> getLoAdmViaje_ListaPdf(Integer id) {
        String sql_to_query = ""
        + "SELECT "
            + "cxc_clie.numero_control AS no_clie,"
            + "substr(upper(cxc_clie.razon_social),1,12) AS clie,"
            + "log_viaje_det.id AS det_id,"
            + "log_doc_carga.id AS cga_id,"
            + "log_doc_ped.id AS ped_id,"
            + "log_doc_carga.no_carga,"
            + "log_doc_carga.fecha_entrega as f_entrega,"
            + "log_doc_ped.no_pedido,"
            + "(case when log_doc_ped_fac.no_facura is null then '' else log_doc_ped_fac.no_facura end) as no_facura,"
            + "cxc_destinatarios.id AS id_dest,"
            + "cxc_destinatarios.folio_ext AS no_dest,"
            + "cxc_destinatarios.razon_social AS nombre_dest,"
            +"(CASE WHEN log_viaje_det.solicitar_firma=TRUE THEN  'SI' ELSE '' END) AS firma,"
            +"(CASE WHEN log_viaje_det.solicitar_sello=TRUE THEN  'SI' ELSE '' END) AS sello,"
            +"(CASE WHEN log_viaje_det.solicitar_efectivo=TRUE THEN  'SI' ELSE '' END) AS efectivo,"
            +"(CASE WHEN log_viaje_det.solicitar_cheque=TRUE THEN  'SI' ELSE '' END) AS cheque,"
            + "gral_mun.id AS mun_id,"
            + "upper(gral_mun.titulo) AS municipio,"
            + "log_doc_ped.log_status_id AS status_ped,"
            + "(case when log_status.id is null then '' else log_status.titulo end) as status_det,"
            + "sum(log_doc_ped_det.cantidad) as cant_uni, "
            + "sum(log_doc_ped_det.peso) AS peso,"
            + "sum(log_doc_ped_det.volumen) AS volumen "
        + "FROM log_viaje_det "
        + "JOIN log_doc_carga on log_doc_carga.id=log_viaje_det.log_doc_carga_id "
        + "JOIN log_doc_ped on log_doc_ped.id=log_viaje_det.log_doc_ped_id  "
        + "JOIN log_doc_ped_det on log_doc_ped_det.log_doc_ped_id=log_doc_ped.id "
        + "left join log_doc_ped_fac on log_doc_ped_fac.log_doc_ped_id=log_doc_ped.id "
        + "JOIN cxc_clie on cxc_clie.id=log_viaje_det.cxc_clie_id "
        + "JOIN cxc_destinatarios on cxc_destinatarios.id=log_doc_ped.cxc_dest_id  "
        + "JOIN gral_mun on gral_mun.id=log_viaje_det.gral_mun_id  "
        + "left join log_status on log_status.id=log_viaje_det.log_status_id "
        + "WHERE log_viaje_det.log_viaje_id=? "
        + "group by cxc_clie.numero_control,cxc_clie.razon_social,log_viaje_det.id,log_doc_carga.id,log_doc_ped.id,log_doc_carga.no_carga,log_doc_carga.fecha_entrega, log_doc_ped.no_pedido, log_doc_ped_fac.no_facura, cxc_destinatarios.id,cxc_destinatarios.folio_ext,cxc_destinatarios.razon_social,log_viaje_det.solicitar_firma,log_viaje_det.solicitar_sello,log_viaje_det.solicitar_efectivo,gral_mun.id,gral_mun.titulo,log_doc_ped.log_status_id, log_status.id  "
        + "order by log_viaje_det.id";
        
        System.out.println("id: "+id);
        //System.out.println("DATOS PARA EL PDFLISTA:"+sql_to_query);
       
        ArrayList<HashMap<String, String>> arrayHm = (ArrayList<HashMap<String, String>>) this.jdbcTemplate.query(
            sql_to_query,
            new Object[]{new Integer(id)}, new RowMapper(){
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    HashMap<String, String> row = new HashMap<String, String>();
                    row.put("no_clie",rs.getString("no_clie"));
                    row.put("clie",rs.getString("clie"));
                    row.put("det_id",rs.getString("det_id"));
                    row.put("cga_id",rs.getString("cga_id"));
                    row.put("f_entrega",rs.getString("f_entrega"));
                    row.put("ped_id",rs.getString("ped_id"));
                    row.put("no_carga",rs.getString("no_carga"));
                    row.put("no_pedido",rs.getString("no_pedido"));
                    row.put("no_facura",rs.getString("no_facura"));
                    row.put("id_dest",rs.getString("id_dest"));
                    row.put("no_dest",rs.getString("no_dest"));
                    row.put("nombre_dest",rs.getString("nombre_dest"));
                    row.put("firma",rs.getString("firma"));
                    row.put("sello",rs.getString("sello"));
                    row.put("cheque",rs.getString("cheque"));
                    row.put("efectivo",rs.getString("efectivo"));
                    row.put("mun_id",rs.getString("mun_id"));
                    row.put("municipio",rs.getString("municipio"));
                    row.put("status_ped",rs.getString("status_ped"));
                    row.put("status_det",rs.getString("status_det"));
                    row.put("cant_uni",StringHelper.roundDouble(rs.getString("cant_uni"),3));
                    row.put("peso",StringHelper.roundDouble(rs.getString("peso"),3));
                    row.put("volumen",StringHelper.roundDouble(rs.getString("volumen"),3));
                    return row;
                }
            }
        );
        
        //Tratar partidas
        ArrayList<HashMap<String, String>> tratado = new ArrayList<HashMap<String, String>>();
        HashMap<String,String> fila=null;
        Integer det_id_actual=0;
        String facturas_pedido="";
        String key_no_fac="no_facura";
        String key_no_fac2="nofacturas";
        int cont_fac=0;
        
        System.out.println("--Tratar partidas antes de mandar al PDF del VIAJE---------------------------");
        for( HashMap<String,String> i : arrayHm ){
            fila = new HashMap<String,String>();
            fila = i;
            //System.out.println("det_id="+fila.get("det_id")+" | "+"ped_id="+fila.get("ped_id")+" | "+"no_pedido="+fila.get("no_pedido")+" | "+"no_facura="+fila.get("no_facura")+" | "+"nombre_dest="+fila.get("nombre_dest")+" | "+"cant_uni="+fila.get("cant_uni")+" | "+"peso="+fila.get("peso")+" | "+"volumen="+fila.get("volumen"));
            
            //if(det_id_actual!=Integer.valueOf(fila.get("det_id"))){
            if(!String.valueOf(det_id_actual).equals(String.valueOf(fila.get("det_id")))){
                //Tomar el det_id de la fila actual
                det_id_actual = Integer.valueOf(fila.get("det_id"));
                
                //Obtener todas las facturs del pedido y agregarla en una cadena separada por comas
                facturas_pedido="";
                
                //Contador de facturas del pedido
                cont_fac=0;
                for( HashMap<String,String> i2 : arrayHm ){
                    //if(det_id_actual==Integer.valueOf(i2.get("det_id"))){
                    if(String.valueOf(det_id_actual).equals(String.valueOf(i2.get("det_id")))){
                        facturas_pedido += i2.get(key_no_fac)+",";
                        cont_fac++;
                    }
                }
                
                //Crar indice con el numero de facturas del pedido
                fila.put(key_no_fac2,String.valueOf(cont_fac));
                
                if(!facturas_pedido.equals("")){
                    //Esto es para eliminar la ultima coma de la cadena
                    facturas_pedido = facturas_pedido.substring(0, facturas_pedido.length()-1);
                }
                /*
                //Eliminar el atributo del hashmap
                if(fila.containsKey(key_no_fac)){
                    fila.remove(key_no_fac);
                }
                */
                
                //Asignar valor con todas las facturas del pedido
                //fila.put(key_no_fac, facturas_pedido);
                fila.put("no_fac", facturas_pedido);
                
                //System.out.println("det_id="+fila.get("det_id")+" | "+"ped_id="+fila.get("ped_id")+" | "+"no_pedido="+fila.get("no_pedido")+" | "+"no_facura="+fila.get("no_facura")+" | "+"nombre_dest="+fila.get("nombre_dest")+" | "+"cant_uni="+fila.get("cant_uni")+" | "+"peso="+fila.get("peso")+" | "+"volumen="+fila.get("volumen"));
                
                //Agregar fila i al arreglo solo una vez si se repite el pedido por la cantidad de facturas
                tratado.add(fila);
            }
        }
        /*
        System.out.println("Array tratado");
        for( HashMap<String,String> i : tratado ){
            System.out.println("det_id="+i.get("det_id")+" | "+"ped_id="+i.get("ped_id")+" | "+"no_pedido="+i.get("no_pedido")+" | "+"no_fac="+i.get("no_fac")+" | "+"nombre_dest="+i.get("nombre_dest")+" | "+"cant_uni="+i.get("cant_uni")+" | "+"peso="+i.get("peso")+" | "+"volumen="+i.get("volumen"));
        }*/
        return tratado;
    }
    
    
    
    
    
    //Catalogo de Servicios Adicionales
    @Override
    public ArrayList<HashMap<String, Object>> getServAdic_PaginaGrid(String data_string, int offset, int pageSize, String orderBy, String asc) {
        String sql_busqueda = "select id from gral_bus_catalogos(?) as foo (id integer)";
        
	String sql_to_query = ""
        + "select log_serv_adic.id, "
                + "inv_prod.sku, "
                + "(case when inv_prod_unidades.titulo is null then '' else inv_prod_unidades.titulo end) as unidad, "
                + "inv_prod.descripcion, "
                + "inv_prod_tipos.titulo as tipo "
        + "FROM log_serv_adic "
        + "join inv_prod on inv_prod.id=log_serv_adic.inv_prod_id "
        + "left join inv_prod_tipos on inv_prod_tipos.id= inv_prod.tipo_de_producto_id "
        + "left join inv_prod_unidades on inv_prod_unidades.id=inv_prod.unidad_id "
        +"join ("+sql_busqueda+") as subt on subt.id=log_serv_adic.id "
        +"order by "+orderBy+" "+asc+" limit ? offset ?";

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
    
    
    
    
    @Override
    public ArrayList<HashMap<String, Object>> getServAdic_Datos(Integer id) {
        String sql_to_query = ""
        + "select "
            + "log_serv_adic.id as id_reg,"
            + "inv_prod.id as id_prod,"
            + "inv_prod.sku as codigo, "
            + "inv_prod.descripcion, "
            + "(case when inv_prod_unidades.titulo is null then '' else inv_prod_unidades.titulo end) as unidad "
        + "from log_serv_adic "
        + "join inv_prod on (inv_prod.id=log_serv_adic.inv_prod_id and inv_prod.borrado_logico=false) "
        + "left join inv_prod_unidades on inv_prod_unidades.id=inv_prod.unidad_id "
        + "where log_serv_adic.id=?";
        
        //System.out.println("grid de productos equivalentes"+sql_to_query);
        ArrayList<HashMap<String, Object>> hm = (ArrayList<HashMap<String, Object>>) this.jdbcTemplate.query(
            sql_to_query,
            new Object[]{new Integer(id)}, new RowMapper(){
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    HashMap<String, Object> row = new HashMap<String, Object>();
                    row.put("id_reg",rs.getInt("id_reg"));
                    row.put("id_prod",rs.getInt("id_prod"));
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
    public ArrayList<HashMap<String, Object>> getBuscadorProductos(String no_cliente, String sku, String tipo, String descripcion, Integer id_empresa) {
        String where = "";
	if(!sku.equals("")){
            where=" and inv_prod.sku ilike '%"+sku+"%'";
	}
        
	if(!tipo.equals("0")){
            where +=" and inv_prod.tipo_de_producto_id="+tipo;
	}
        
	if(!descripcion.equals("")){
            where +=" and inv_prod.descripcion ilike '%"+descripcion+"%'";
	}
        
	if(!no_cliente.equals("")){
            where +=" and upper(trim(inv_prod.no_clie))='"+no_cliente.trim().toUpperCase()+"'";
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
        +"from inv_prod "
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
    
    

    //Busca datos de un producto en especifico a partir del codigo
    @Override
    public ArrayList<HashMap<String, Object>> getDataProductBySku(String no_cliente, String codigo, String tipo, Integer id_empresa) {
        String where = "";
        
	if(!tipo.equals("0")){
            where +=" and inv_prod.tipo_de_producto_id="+tipo;
	}
        
	if(!no_cliente.equals("")){
            where +=" and upper(trim(inv_prod.no_clie))='"+no_cliente.trim().toUpperCase()+"'";
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
        +"from inv_prod "
        + "left join inv_prod_tipos on inv_prod_tipos.id=inv_prod.tipo_de_producto_id "
        + "left join inv_prod_unidades on inv_prod_unidades.id=inv_prod.unidad_id "
        + "where inv_prod.empresa_id=? and inv_prod.borrado_logico=false and inv_prod.sku=?  "+where+" limit 1;";
        //log.log(Level.INFO, "Ejecutando query de {0}", sql_to_query);

        ArrayList<HashMap<String, Object>> hm = (ArrayList<HashMap<String, Object>>) this.jdbcTemplate.query(
            sql_to_query,
            new Object[]{new Integer(id_empresa), codigo.toUpperCase()}, new RowMapper(){
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    HashMap<String, Object> row = new HashMap<String, Object>();
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

    
    
    //Obtiene tipos de productos
    @Override
    public ArrayList<HashMap<String, Object>> getProducto_Tipos() {
	String sql_query = "SELECT DISTINCT id,titulo FROM inv_prod_tipos WHERE borrado_logico=false order by titulo ASC;";
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
    
    
    
    //Busca datos de un Servicio adicional en especifico a partir del codigo
    @Override
    public ArrayList<HashMap<String, Object>> getDataServicioAdicional(String codigo, Integer id_empresa) {
        String where = "";
        
        String sql_to_query = ""
        + "SELECT "
            + "inv_prod.id,"
            + "inv_prod.sku,"
            + "inv_prod.descripcion, "
            + "inv_prod.unidad_id, "
            + "inv_prod_unidades.titulo AS unidad, "
            + "inv_prod_tipos.titulo AS tipo,"
            + "inv_prod_unidades.decimales "
        +"FROM log_serv_adic "
        + "join inv_prod on inv_prod.id=log_serv_adic.inv_prod_id "
        + "left join inv_prod_tipos on inv_prod_tipos.id=inv_prod.tipo_de_producto_id "
        + "left join inv_prod_unidades on inv_prod_unidades.id=inv_prod.unidad_id "
        + "where inv_prod.empresa_id=? and inv_prod.borrado_logico=false and inv_prod.sku=?  "+where+" limit 1;";
        //log.log(Level.INFO, "Ejecutando query de {0}", sql_to_query);
        
        ArrayList<HashMap<String, Object>> hm = (ArrayList<HashMap<String, Object>>) this.jdbcTemplate.query(
            sql_to_query,
            new Object[]{new Integer(id_empresa), codigo.toUpperCase()}, new RowMapper(){
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    HashMap<String, Object> row = new HashMap<String, Object>();
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

    
    
    
    //Buscador de Rutas sin incluir tipo de Unidad
    @Override
    public ArrayList<HashMap<String, Object>> getBuscadorRutas1(String no_ruta, String nombre_ruta, String poblacion, Integer id_empresa, Integer id_sucursal) {
        String where = "";
	if(!no_ruta.trim().equals("")){
            where=" and log_ruta.folio ilike '%"+no_ruta.trim()+"%'";
	}
        
	if(!nombre_ruta.trim().equals("")){
            where +=" and log_ruta.titulo ilike '%"+ nombre_ruta.trim() +"%'";
	}
        
	if(!poblacion.trim().equals("")){
            where +=" and gral_mun.titulo ilike '%"+ poblacion.trim() +"%'";
	}
        
        String sql_to_query = ""
        + "select distinct "
            + "log_ruta.folio,"
            + "log_ruta.titulo as titulo_ruta,"
            + "log_ruta.km,"
            + "(case when log_ruta_tipo.id is null then '' else log_ruta_tipo.titulo end) as tipo "
        + "from log_ruta "
        + "join log_ruta_mun on log_ruta_mun.log_ruta_id=log_ruta.id  "
        + "join gral_mun on gral_mun.id=log_ruta_mun.gral_mun_id "
        + "left join log_ruta_tipo on log_ruta_tipo.id=log_ruta.log_ruta_tipo_id "
        + "where log_ruta.gral_emp_id=? and log_ruta.gral_suc_id=? and log_ruta.borrado_logico=false "+where+" order by log_ruta.titulo;";
        
        //log.log(Level.INFO, "Ejecutando query de {0}", sql_to_query);
        //System.out.println("sql_to_query: "+sql_to_query);

        ArrayList<HashMap<String, Object>> hm = (ArrayList<HashMap<String, Object>>) this.jdbcTemplate.query(
            sql_to_query,
            new Object[]{new Integer(id_empresa), new Integer(id_sucursal)}, new RowMapper(){
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    HashMap<String, Object> row = new HashMap<String, Object>();
                    row.put("folio",rs.getString("folio"));
                    row.put("titulo_ruta",rs.getString("titulo_ruta"));
                    row.put("km",StringHelper.roundDouble(rs.getString("km"),2));
                    row.put("tipo",rs.getString("tipo"));
                    return row;
                }
            }
        );
        return hm;
    }
    
    
    
    //Obtener datos de una ruta en especifico sin incluir datos de tipo de unidad y costo
    @Override
    public ArrayList<HashMap<String, Object>> getDatosRuta_x_NoRuta1(String no_ruta, Integer id_empresa, Integer id_sucursal) {
        String where = "";
        
        String sql_to_query = ""
        + "select distinct "
            + "log_ruta.id,"
            + "log_ruta.folio,"
            + "log_ruta.titulo as titulo_ruta,"
            + "(case when log_ruta_tipo.id is null then '' else log_ruta_tipo.titulo end) as tipo_ruta,"
            + "log_ruta.km "
        + "from log_ruta "
        + "left join log_ruta_tipo on log_ruta_tipo.id=log_ruta.log_ruta_tipo_id "
        + "where log_ruta.folio=? and log_ruta.gral_emp_id=? and log_ruta.gral_suc_id=? and log_ruta.borrado_logico=false "+where+" order by log_ruta.titulo;";
        
        //log.log(Level.INFO, "Ejecutando query de {0}", sql_to_query);
        //System.out.println("sql_to_query: "+sql_to_query);

        ArrayList<HashMap<String, Object>> hm = (ArrayList<HashMap<String, Object>>) this.jdbcTemplate.query(
            sql_to_query,
            new Object[]{no_ruta, new Integer(id_empresa), new Integer(id_sucursal)}, new RowMapper(){
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    HashMap<String, Object> row = new HashMap<String, Object>();
                    row.put("id",rs.getString("id"));
                    row.put("folio",rs.getString("folio"));
                    row.put("titulo_ruta",rs.getString("titulo_ruta"));
                    row.put("tipo_ruta",rs.getString("tipo_ruta"));
                    row.put("km",StringHelper.roundDouble(rs.getString("km"),2));
                    return row;
                }
            }
        );
        return hm;
    }
    
    
    
    
    
    
    //Buscador de Rutas
    @Override
    public ArrayList<HashMap<String, Object>> getBuscadorRutas(String no_ruta, String nombre_ruta, String poblacion, Integer tipo_unidad, Integer id_empresa, Integer id_sucursal) {
        String where = "";
	if(!no_ruta.trim().equals("")){
            where=" and log_ruta.folio ilike '%"+no_ruta.trim()+"%'";
	}
        
	if(!nombre_ruta.trim().equals("")){
            where +=" and log_ruta.titulo ilike '%"+ nombre_ruta.trim() +"%'";
	}
        
	if(!poblacion.trim().equals("")){
            where +=" and gral_mun.titulo ilike '%"+ poblacion.trim() +"%'";
	}
        
	if(tipo_unidad>0){
            where +=" and log_ruta_tipo_unidad.log_vehiculo_tipo_id="+ tipo_unidad +"";
	}
        
        String sql_to_query = ""
        + "select distinct "
            + "log_ruta.folio,"
            + "log_ruta.titulo as titulo_ruta,"
            + "log_ruta.km,"
            + "(case when log_ruta_tipo.id is null then '' else log_ruta_tipo.titulo end) as tipo,"
            + "log_ruta_tipo_unidad.costo "
        + "from log_ruta "
        + "join log_ruta_tipo_unidad on log_ruta_tipo_unidad.log_ruta_id=log_ruta.id "
        + "join log_ruta_mun on log_ruta_mun.log_ruta_id=log_ruta.id  "
        + "join gral_mun on gral_mun.id=log_ruta_mun.gral_mun_id "
        + "left join log_ruta_tipo on log_ruta_tipo.id=log_ruta.log_ruta_tipo_id "
        + "where log_ruta.gral_emp_id=? and log_ruta.gral_suc_id=? and log_ruta.borrado_logico=false "+where+" order by log_ruta.titulo;";
        
        //log.log(Level.INFO, "Ejecutando query de {0}", sql_to_query);
        //System.out.println("sql_to_query: "+sql_to_query);

        ArrayList<HashMap<String, Object>> hm = (ArrayList<HashMap<String, Object>>) this.jdbcTemplate.query(
            sql_to_query,
            new Object[]{new Integer(id_empresa), new Integer(id_sucursal)}, new RowMapper(){
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    HashMap<String, Object> row = new HashMap<String, Object>();
                    row.put("folio",rs.getString("folio"));
                    row.put("titulo_ruta",rs.getString("titulo_ruta"));
                    row.put("km",StringHelper.roundDouble(rs.getString("km"),2));
                    row.put("tipo",rs.getString("tipo"));
                    row.put("costo",StringHelper.roundDouble(rs.getString("costo"),2));
                    return row;
                }
            }
        );
        return hm;
    }
    

    
    //Obtener datos de una ruta en especifico
    @Override
    public ArrayList<HashMap<String, Object>> getDatosRuta_x_NoRuta(String no_ruta, Integer tipo_unidad, String tipo_distribucion, Integer id_empresa, Integer id_sucursal) {
        String where = "";
        
	if(tipo_unidad>0){
            where +=" and log_ruta_tipo_unidad.log_vehiculo_tipo_id="+ tipo_unidad +"";
	}
        
        String sql_to_query = ""
        + "select distinct "
            + "log_ruta.id,"
            + "log_ruta.folio,"
            + "log_ruta.titulo as titulo_ruta,"
            + "(case when log_ruta_tipo.id is null then '' else log_ruta_tipo.titulo end) as tipo_ruta,"
            + "log_ruta.km,"
            + "log_ruta_tipo_unidad.costo "
        + "from log_ruta "
        + "join log_ruta_tipo_unidad on log_ruta_tipo_unidad.log_ruta_id=log_ruta.id "
        //+ "join log_ruta_mun on log_ruta_mun.log_ruta_id=log_ruta.id  "
        //+ "join gral_mun on gral_mun.id=log_ruta_mun.gral_mun_id "
        + "left join log_ruta_tipo on log_ruta_tipo.id=log_ruta.log_ruta_tipo_id "
        + "where log_ruta.folio=? and log_ruta.gral_emp_id=? and log_ruta.gral_suc_id=? and log_ruta.borrado_logico=false "+where+" order by log_ruta.titulo;";
        
        //log.log(Level.INFO, "Ejecutando query de {0}", sql_to_query);
        //System.out.println("sql_to_query: "+sql_to_query);
        
        ArrayList<HashMap<String, Object>> hm = (ArrayList<HashMap<String, Object>>) this.jdbcTemplate.query(
            sql_to_query,
            new Object[]{no_ruta, new Integer(id_empresa), new Integer(id_sucursal)}, new RowMapper(){
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    HashMap<String, Object> row = new HashMap<String, Object>();
                    row.put("id",rs.getString("id"));
                    row.put("folio",rs.getString("folio"));
                    row.put("titulo_ruta",rs.getString("titulo_ruta"));
                    row.put("tipo_ruta",rs.getString("tipo_ruta"));
                    row.put("km",StringHelper.roundDouble(rs.getString("km"),2));
                    row.put("costo",StringHelper.roundDouble(rs.getString("costo"),2));
                    return row;
                }
            }
        );
        return hm;
    }
    
    
    
    
    
    
    //Obtener datos de una ruta en especifico.
    //Obtiene el costo de acuerdo al tipo de distribucion, tipo de unidad de transporte y cantidad de Unidades
    @Override
    public ArrayList<HashMap<String, Object>> getDatosRuta_x_NoRuta2(String no_ruta, Integer tipo_de_unidad_id, String tipo_dist, Integer id_empresa, Integer id_sucursal) {
        String where = "";
        
        /*
        1;"DIRECTO"
        2;"HOT SHOT"
        3;"LTL"
        4;"EXPRES"
         */
	if(tipo_de_unidad_id>0){
            where +=" and log_ruta_tipo_unidad.log_vehiculo_tipo_id="+ tipo_de_unidad_id +"";
	}
        
        String sql_to_query = ""
        + "select distinct "
            + "log_ruta.id,"
            + "log_ruta.folio,"
            + "log_ruta.titulo as titulo_ruta,"
            + "(case when log_ruta_tipo.id is null then '' else log_ruta_tipo.titulo end) as tipo_ruta,"
            + "log_ruta.km,"
            //+ "log_ruta_tipo_unidad.costo,"
            + "(case when "+tipo_dist+"=1 then log_ruta_tipo_unidad.costo when "+tipo_dist+"=2 then log_ruta_tipo_unidad.costo_hot_shot when "+tipo_dist+"=3 then log_ruta_tipo_unidad.costo when "+tipo_dist+"=4 then log_ruta_tipo_unidad.costo else log_ruta_tipo_unidad.costo end) as costo "
        + "from log_ruta "
        + "join log_ruta_tipo_unidad on log_ruta_tipo_unidad.log_ruta_id=log_ruta.id "
        //+ "join log_ruta_mun on log_ruta_mun.log_ruta_id=log_ruta.id  "
        //+ "join gral_mun on gral_mun.id=log_ruta_mun.gral_mun_id "
        + "left join log_ruta_tipo on log_ruta_tipo.id=log_ruta.log_ruta_tipo_id "
        + "where log_ruta.folio=? "
        + "and log_ruta.gral_emp_id=? "
        + "and log_ruta.gral_suc_id=? "
        + "and log_ruta.borrado_logico=false "+where+" "
        + "order by log_ruta.titulo;";
        
        //log.log(Level.INFO, "Ejecutando query de {0}", sql_to_query);
        //System.out.println("sql_to_query: "+sql_to_query);
        
        ArrayList<HashMap<String, Object>> hm = (ArrayList<HashMap<String, Object>>) this.jdbcTemplate.query(
            sql_to_query,
            new Object[]{no_ruta, new Integer(id_empresa), new Integer(id_sucursal)}, new RowMapper(){
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    HashMap<String, Object> row = new HashMap<String, Object>();
                    row.put("id",rs.getString("id"));
                    row.put("folio",rs.getString("folio"));
                    row.put("titulo_ruta",rs.getString("titulo_ruta"));
                    row.put("tipo_ruta",rs.getString("tipo_ruta"));
                    row.put("km",StringHelper.roundDouble(rs.getString("km"),2));
                    row.put("costo",StringHelper.roundDouble(rs.getString("costo"),2));
                    return row;
                }
            }
        );
        return hm;
    }
    
    
    
    
    
    
    
    
    //Obtener las poblaciones de una ruta en especifico
    @Override
    public ArrayList<HashMap<String, Object>> getDatosRuta_Poblaciones(Integer id_ruta) {
        String where = "";
        
        String sql_to_query = ""
        + "select "
            + "gral_mun.id, "
            + "(gral_mun.titulo||''||(case when gral_edo.abreviacion is null then '' else ', '||gral_edo.abreviacion end)||''||(case when gral_pais.abreviacion is null then '' else ', '||gral_pais.abreviacion end)) as poblacion "
        + "from log_ruta_mun "
        + "join gral_mun on gral_mun.id=log_ruta_mun.gral_mun_id "
        + "left join gral_edo on gral_edo.id=gral_mun.estado_id "
        + "left join gral_pais on gral_pais.id=gral_edo.pais_id "
        + "where log_ruta_mun.log_ruta_id=? order by log_ruta_mun.id;";
        
        //log.log(Level.INFO, "Ejecutando query de {0}", sql_to_query);
        //System.out.println("getPoblacionesRuta: "+sql_to_query);
        ArrayList<HashMap<String, Object>> hm = (ArrayList<HashMap<String, Object>>) this.jdbcTemplate.query(
            sql_to_query,
            new Object[]{new Integer(id_ruta)}, new RowMapper(){
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    HashMap<String, Object> row = new HashMap<String, Object>();
                    row.put("id",rs.getString("id"));
                    row.put("poblacion",rs.getString("poblacion"));
                    return row;
                }
            }
        );
        return hm;
    }
    
    
    
    
    //Obtener las poblaciones de una ruta en especifico
    @Override
    public ArrayList<HashMap<String, Object>> getDatosRuta_CostosPorPoblacion(String ids_mun, Integer tipo_de_unidad_id, Integer id_empresa, Integer id_sucursal) {
        String where = "";
        
        String sql_to_query = ""
        + "select distinct "
                + "log_ruta_mun.gral_mun_id as mun_id,"
                + "log_ruta.km,"
                + "log_ruta_tipo_unidad.costo,"
                + "log_ruta_tipo_unidad.costo_hot_shot,"
                + "log_ruta_tipo_unidad.costo_extra_reparto_local,"
                + "log_ruta_tipo_unidad.costo_extra_reparto_foraneo,"
                + "log_ruta_tipo_unidad.costo_estadia "
            + "from log_ruta "
            + "join log_ruta_tipo_unidad on (log_ruta_tipo_unidad.log_ruta_id=log_ruta.id and log_ruta_tipo_unidad.log_vehiculo_tipo_id=?) "
            + "join log_ruta_mun on (log_ruta_mun.log_ruta_id=log_ruta.id and log_ruta_mun.gral_mun_id in ("+ids_mun+")) "
            + "where log_ruta.gral_emp_id=? "
            + "and log_ruta.gral_suc_id=? "
            + "and log_ruta.borrado_logico=false "
            + "order by log_ruta_mun.gral_mun_id;";
        
        //log.log(Level.INFO, "Ejecutando query de {0}", sql_to_query);
        System.out.println("tipo_de_unidad_id="+tipo_de_unidad_id+" | ids_mun="+ids_mun+" | emp="+id_empresa+" | suc="+id_sucursal);
        System.out.println("getCostosPorPoblacion: "+sql_to_query);
        ArrayList<HashMap<String, Object>> hm = (ArrayList<HashMap<String, Object>>) this.jdbcTemplate.query(
            sql_to_query,
            new Object[]{new Integer(tipo_de_unidad_id), new Integer(id_empresa), new Integer(id_sucursal)}, new RowMapper(){
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    HashMap<String, Object> row = new HashMap<String, Object>();
                    row.put("mun_id",rs.getString("mun_id"));
                    row.put("costo",StringHelper.roundDouble(rs.getString("costo"),2));
                    row.put("costo_hot_shot",StringHelper.roundDouble(rs.getString("costo_hot_shot"),2));
                    row.put("costo_extra_reparto_local",StringHelper.roundDouble(rs.getString("costo_extra_reparto_local"),2));
                    row.put("costo_extra_reparto_foraneo",StringHelper.roundDouble(rs.getString("costo_extra_reparto_foraneo"),2));
                    row.put("costo_estadia",StringHelper.roundDouble(rs.getString("costo_estadia"),2));
                    return row;
                }
            }
        );
        return hm;
    }
    
    
    
    
    
    //Obtiene datos de Vehiculo Marca
    @Override
    public ArrayList<HashMap<String, String>> getVehiculoMarca_Datos(Integer id) {
        
        String sql_to_query = "SELECT id,titulo FROM log_vehiculo_marca WHERE id="+id;
        
        ArrayList<HashMap<String, String>> dato_vehiculomarca = (ArrayList<HashMap<String, String>>) this.jdbcTemplate.query(
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
        return dato_vehiculomarca;
    }
    
    
    //Obtiene datos para el grid del Catalogo de Vehiculo Marca
    @Override
    public ArrayList<HashMap<String, Object>> getVehiculoMarca_PaginaGrid(String data_string, int offset, int pageSize, String orderBy, String asc) {
        String sql_busqueda = "select id from gral_bus_catalogos(?) as foo (id integer)";
        
	String sql_to_query = "SELECT log_vehiculo_marca.id, log_vehiculo_marca.titulo "                              
                                +"FROM log_vehiculo_marca "                        
                                +"JOIN ("+sql_busqueda+") AS sbt on sbt.id = log_vehiculo_marca.id "
                                +"WHERE log_vehiculo_marca.borrado_logico=false "
                                +"order by "+orderBy+" "+asc+" limit ? OFFSET ?";
        
        //System.out.println("Busqueda GetPage: "+sql_to_query+" "+data_string+" "+ offset +" "+ pageSize);
        //System.out.println("esto es el query  :  "+sql_to_query);
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
    
    
     //Obtiene datos de Vehiculo Tipo Rodada
    @Override
    public ArrayList<HashMap<String, String>> getVehiculoTipoRodada_Datos(Integer id) {
        
        String sql_to_query = "SELECT id,titulo FROM log_vehiculo_tipo_rodada WHERE id="+id;
        
        ArrayList<HashMap<String, String>> dato_vehiculotiporodada = (ArrayList<HashMap<String, String>>) this.jdbcTemplate.query(
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
        return dato_vehiculotiporodada;
    }
    
    
    //Obtiene datos para el grid del Catalogo de Vehiculo Tipo Rodada
    @Override
    public ArrayList<HashMap<String, Object>> getVehiculoTipoRodada_PaginaGrid(String data_string, int offset, int pageSize, String orderBy, String asc) {
        String sql_busqueda = "select id from gral_bus_catalogos(?) as foo (id integer)";
        
	String sql_to_query = "SELECT log_vehiculo_tipo_rodada.id, log_vehiculo_tipo_rodada.titulo "                              
                                +"FROM log_vehiculo_tipo_rodada "                        
                                +"JOIN ("+sql_busqueda+") AS sbt on sbt.id = log_vehiculo_tipo_rodada.id "
                                +"WHERE log_vehiculo_tipo_rodada.borrado_logico=false "
                                +"order by "+orderBy+" "+asc+" limit ? OFFSET ?";
        
        //System.out.println("Busqueda GetPage: "+sql_to_query+" "+data_string+" "+ offset +" "+ pageSize);
        //System.out.println("esto es el query  :  "+sql_to_query);
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

    //Obtiene datos de Vehiculo Tipo Caja
    @Override
    public ArrayList<HashMap<String, String>> getVehiculoTipoCaja_Datos(Integer id) {
        String sql_to_query = "SELECT id,titulo FROM log_vehiculo_tipo_caja WHERE id="+id;
        
        ArrayList<HashMap<String, String>> dato_vehiculotipocaja = (ArrayList<HashMap<String, String>>) this.jdbcTemplate.query(
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
        return dato_vehiculotipocaja;
    }
    
    
    //Obtiene datos para el grid del Catalogo de Vehiculo Tipo Caja
    @Override
    public ArrayList<HashMap<String, Object>> getVehiculoTipoCaja_PaginaGrid(String data_string, int offset, int pageSize, String orderBy, String asc) {
        String sql_busqueda = "select id from gral_bus_catalogos(?) as foo (id integer)";
        
	String sql_to_query = "SELECT log_vehiculo_tipo_caja.id, log_vehiculo_tipo_caja.titulo "                              
                                +"FROM log_vehiculo_tipo_caja "                        
                                +"JOIN ("+sql_busqueda+") AS sbt on sbt.id = log_vehiculo_tipo_caja.id "
                                +"WHERE log_vehiculo_tipo_caja.borrado_logico=false "
                                +"order by "+orderBy+" "+asc+" limit ? OFFSET ?";
        
        //System.out.println("Busqueda GetPage: "+sql_to_query+" "+data_string+" "+ offset +" "+ pageSize);
        //System.out.println("esto es el query  :  "+sql_to_query);
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
    
    
    
    
    /*
     * CATALOGO DE TIPOS DE UNIDADES
     * 
     */
    //Obtiene datos de Vehiculo Tipo Unidades
    @Override
    public ArrayList<HashMap<String, String>> getVehiculoTipoUnidades_Datos(Integer id) {
        String sql_to_query = "SELECT id,titulo,volumen_inicio,volumen_fin,kg_inicio,kg_fin, carton_inicio, carton_fin, tarima_inicio, tarima_fin FROM log_vehiculo_tipo WHERE id="+id;
        
        ArrayList<HashMap<String, String>> hm = (ArrayList<HashMap<String, String>>) this.jdbcTemplate.query(
            sql_to_query,
            new Object[]{}, new RowMapper(){
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    HashMap<String, String> row = new HashMap<String, String>();
                    row.put("id",String.valueOf(rs.getInt("id")));
                    row.put("titulo",rs.getString("titulo"));
                    row.put("volumen_inicio",StringHelper.roundDouble(rs.getDouble("volumen_inicio"),2));
                    row.put("volumen_fin",StringHelper.roundDouble(rs.getDouble("volumen_fin"),2));
                    row.put("kg_inicio",StringHelper.roundDouble(rs.getDouble("kg_inicio"),2));
                    row.put("kg_fin",StringHelper.roundDouble(rs.getDouble("kg_fin"),2));
                    row.put("carton_inicio",StringHelper.roundDouble(rs.getDouble("carton_inicio"),2));
                    row.put("carton_fin",StringHelper.roundDouble(rs.getDouble("carton_fin"),2));
                    row.put("tarima_inicio",StringHelper.roundDouble(rs.getDouble("tarima_inicio"),2));
                    row.put("tarima_fin",StringHelper.roundDouble(rs.getDouble("tarima_fin"),2));
                    return row;
                }
            }
        );
        return hm;
    }

    //Obtiene datos para el grid del Catalogo de Vehiculo Tipo Unidades
    @Override
    public ArrayList<HashMap<String, Object>> getVehiculoTipoUnidades_PaginaGrid(String data_string, int offset, int pageSize, String orderBy, String asc) {
        String sql_busqueda = "select id from gral_bus_catalogos(?) as foo (id integer)";
        
	String sql_to_query = ""
        + "SELECT log_vehiculo_tipo.id, log_vehiculo_tipo.titulo, log_vehiculo_tipo.volumen_inicio, log_vehiculo_tipo.volumen_fin, log_vehiculo_tipo.kg_inicio, log_vehiculo_tipo.kg_fin, log_vehiculo_tipo.carton_inicio, log_vehiculo_tipo.carton_fin, log_vehiculo_tipo.tarima_inicio, log_vehiculo_tipo.tarima_fin "                              
        +"FROM log_vehiculo_tipo "                        
        +"JOIN ("+sql_busqueda+") AS sbt ON sbt.id = log_vehiculo_tipo.id "
        +"WHERE log_vehiculo_tipo.borrado_logico=false "
        +"order by "+orderBy+" "+asc+" limit ? OFFSET ?";
  
        //System.out.println("Busqueda GetPage: "+sql_to_query+" "+data_string+" "+ offset +" "+ pageSize);
        //System.out.println("esto es el query  :  "+sql_to_query);
        ArrayList<HashMap<String, Object>> hm = (ArrayList<HashMap<String, Object>>) this.jdbcTemplate.query(
            sql_to_query, 
            new Object[]{data_string, new Integer(pageSize),new Integer(offset)}, new RowMapper() {
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    HashMap<String, Object> row = new HashMap<String, Object>();
                    row.put("id",rs.getInt("id"));
                    row.put("titulo",rs.getString("titulo"));
                    row.put("volumen_inicio",StringHelper.roundDouble(rs.getDouble("volumen_inicio"),2));
                    row.put("volumen_fin",StringHelper.roundDouble(rs.getDouble("volumen_fin"),2));
                    row.put("kg_inicio",StringHelper.roundDouble(rs.getDouble("kg_inicio"),2));
                    row.put("kg_fin",StringHelper.roundDouble(rs.getDouble("kg_fin"),2));
                    row.put("carton_inicio",StringHelper.roundDouble(rs.getDouble("carton_inicio"),2));
                    row.put("carton_fin",StringHelper.roundDouble(rs.getDouble("carton_fin"),2));
                    row.put("tarima_inicio",StringHelper.roundDouble(rs.getDouble("tarima_inicio"),2));
                    row.put("tarima_fin",StringHelper.roundDouble(rs.getDouble("tarima_fin"),2));
                    return row;
                }
            }
        );
        return hm; 
    }
    
    
    //Obtiene las unidades de medida 
    @Override
    public ArrayList<HashMap<String, Object>> getUnidadesDeMedida() {
	String sql_query = "SELECT id,titulo,decimales from inv_prod_unidades where borrado_logico=false order by titulo;";

        ArrayList<HashMap<String, Object>> hm_unidades = (ArrayList<HashMap<String, Object>>) this.jdbcTemplate.query(
            sql_query,
            new Object[]{}, new RowMapper() {
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    HashMap<String, Object> row = new HashMap<String, Object>();
                    row.put("id",rs.getString("id"));
                    row.put("titulo",rs.getString("titulo"));
                    row.put("no_dec",String.valueOf(rs.getInt("decimales")));
                    return row;
                }
            }
        );
        return hm_unidades;
    }
    
    
    
    
    
    
    
    //APLICATIVO DE CAPTURA DE FACTURAS
    //Metodos pa el aplicativo de Captura de Facturas por Numero de Carga
    @Override
    public ArrayList<HashMap<String, Object>> getLogRegCarga_PaginaGrid(String data_string, int offset, int pageSize, String orderBy, String asc) {
        String sql_busqueda = "select id from gral_bus_catalogos(?) as foo (id integer)";
        
	String sql_to_query = ""
        + "SELECT "
            + "log_doc_carga.id, "
            + "log_doc_carga.no_carga, "
            + "log_doc_carga.folio, "
            + "to_char(log_doc.fecha_carga::timestamp with time zone, 'dd/mm/yyyy') as fecha_carga, "
            + "(case when cxc_clie.id is null then '' else cxc_clie.razon_social end) AS cliente, "
            + "(case when log_status.id is null then '' else log_status.titulo end) as status, "
            + "(case when inv_alm.id is null then '' else inv_alm.titulo end) AS almacen "
        + "FROM log_doc_carga "
        + "join log_doc on log_doc.id=log_doc_carga.log_doc_id "
        + "join cxc_clie on cxc_clie.id=log_doc.cxc_clie_id "
        + "left join log_status on log_status.id=log_doc_carga.log_status_id  "
        + "left join inv_alm on inv_alm.id=log_doc.inv_alm_id "
        +"JOIN ("+sql_busqueda+") AS sbt on sbt.id=log_doc_carga.id "
        +"order by "+orderBy+" "+asc+" limit ? OFFSET ?";
        
        //System.out.println("Paginado Viajes: "+sql_to_query);
        ArrayList<HashMap<String, Object>> hm = (ArrayList<HashMap<String, Object>>) this.jdbcTemplate.query(
            sql_to_query, 
            new Object[]{data_string, new Integer(pageSize),new Integer(offset)}, new RowMapper() {
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    HashMap<String, Object> row = new HashMap<String, Object>();
                    row.put("id",rs.getInt("id"));
                    row.put("no_carga",rs.getString("no_carga"));
                    row.put("folio",rs.getString("folio"));
                    row.put("fecha_carga",rs.getString("fecha_carga"));
                    row.put("cliente",rs.getString("cliente"));
                    row.put("status",rs.getString("status"));
                    row.put("almacen",rs.getString("almacen"));
                    return row;
                }
            }
        );
        return hm; 
    }
    
    
    
    //Obtiene datos de Vehiculo Tipo Caja
    @Override
    public ArrayList<HashMap<String, Object>> getLogRegCarga_Datos(Integer id) {
        String sql_to_query = ""
        + "select "
            + "log_doc_carga.id, "
            + "log_doc_carga.folio, "
            + "log_doc_carga.no_carga, "
            + "(case when log_doc_carga.fecha_entrega is null then '' else log_doc_carga.fecha_entrega::character varying end) as fecha_entrega, "
            + "log_doc.inv_alm_id as alm_id, "
            + "cxc_clie.id as clie_id, "
            + "cxc_clie.numero_control as no_clie, "
            + "cxc_clie.razon_social as nombre_clie,"
            + "log_doc_carga.observaciones as observ,"
            + "log_doc_carga.log_status_id as stat_cga_id,"
            + "(case when log_status.id is null then '' else log_status.titulo end) as status_cga,"
            + "log_doc_carga.afecta_inv "
        + "from log_doc_carga "
        + "join log_doc on log_doc.id=log_doc_carga.log_doc_id  "
        + "join cxc_clie on cxc_clie.id=log_doc.cxc_clie_id  "
        + "left join log_status on log_status.id=log_doc_carga.log_status_id  "
        + "where log_doc_carga.id=?";
        
        ArrayList<HashMap<String, Object>> hm = (ArrayList<HashMap<String, Object>>) this.jdbcTemplate.query(
            sql_to_query,
            new Object[]{ new Integer(id)}, new RowMapper(){
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    HashMap<String, Object> row = new HashMap<String, Object>();
                    row.put("id",rs.getInt("id"));
                    row.put("folio",rs.getString("folio"));
                    row.put("no_carga",rs.getString("no_carga"));
                    row.put("fecha_entrega",rs.getString("fecha_entrega"));
                    row.put("alm_id",rs.getInt("alm_id"));
                    row.put("clie_id",rs.getInt("clie_id"));
                    row.put("no_clie",rs.getString("no_clie"));
                    row.put("nombre_clie",rs.getString("nombre_clie"));
                    row.put("observ",rs.getString("observ"));
                    row.put("stat_cga_id",rs.getInt("stat_cga_id"));
                    row.put("status_cga",rs.getString("status_cga"));
                    row.put("afecta_inv",rs.getBoolean("afecta_inv"));
                    return row;
                }
            }
        );
        return hm;
    }
    
    
    
    //Obtiene todos los pedidos de la carga seleccionada
    @Override
    public ArrayList<HashMap<String, Object>> getLogRegCarga_DatosGrid(Integer id) {
        String sql_to_query = ""
        + "select "
            + "log_doc_ped.id as id_ped,"
            + "log_doc_ped.no_pedido,"
            + "(case when log_doc_ped_fac.id is null then 0 else log_doc_ped_fac.id end) as id_ped_fac,"
            + "(case when log_doc_ped_fac.no_facura is null then '' else log_doc_ped_fac.no_facura end) as no_facura,"
            + "(case when log_doc_ped_fac.total_factura is null then 0 else log_doc_ped_fac.total_factura end) as total_fac,"
            + "cxc_destinatarios.id as id_dest,"
            + "cxc_destinatarios.folio as no_dest,"
            + "cxc_destinatarios.razon_social as nombre_dest,"
            + "(case when gral_mun.id is null then '' else gral_mun.titulo end) as poblacion,"
            + "log_doc_ped.log_status_id as status_id,"
            + "(case when log_status.id is null then '' else log_status.titulo end) as status_ped, "
            + "log_doc_ped.log_tarifa_clase_id as tclas_id,"
            //+ "(case when log_doc_ped.estatus=0 then '' when log_doc_ped.estatus=1 then 'Enviado' else '' end) as status_ped,"
            + "sum((case when log_doc_ped_det.cantidad is null then 0 else log_doc_ped_det.cantidad end)) as cant_uni, "
            + "sum((case when log_doc_ped_det.peso is null then 0 else log_doc_ped_det.peso end)) AS peso,"
            + "sum((case when log_doc_ped_det.volumen is null then 0 else log_doc_ped_det.volumen end)) AS volumen "
        + "from log_doc_ped "
        + "left join log_doc_ped_det on log_doc_ped_det.log_doc_ped_id=log_doc_ped.id "
        + "left join log_doc_ped_fac on log_doc_ped_fac.log_doc_ped_id=log_doc_ped.id "
        + "join cxc_destinatarios on cxc_destinatarios.id=log_doc_ped.cxc_dest_id "
        + "left join gral_mun on gral_mun.id=cxc_destinatarios.gral_mun_id "
        + "left join log_status on log_status.id=log_doc_ped.log_status_id  "
        + "where log_doc_ped.log_doc_carga_id=? "
        + "group by log_doc_ped.id, log_doc_ped.no_pedido, log_doc_ped_fac.id, log_doc_ped_fac.no_facura, cxc_destinatarios.id, cxc_destinatarios.folio, cxc_destinatarios.razon_social, gral_mun.id, log_doc_ped.log_status_id, log_status.id, log_doc_ped.log_tarifa_tipo_id "
        + "order by log_doc_ped.id;";
        
        //System.out.println("DatosGrid: "+sql_to_query);
        //System.out.println("id: "+id);
        ArrayList<HashMap<String, Object>> hm = (ArrayList<HashMap<String, Object>>) this.jdbcTemplate.query(
            sql_to_query,
            new Object[]{ new Integer(id)}, new RowMapper(){
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    HashMap<String, Object> row = new HashMap<String, Object>();
                    row.put("id_ped",rs.getInt("id_ped"));
                    row.put("no_pedido",rs.getString("no_pedido"));
                    row.put("id_ped_fac",rs.getInt("id_ped_fac"));
                    row.put("no_facura",rs.getString("no_facura"));
                    row.put("total_fac",StringHelper.roundDouble(rs.getString("total_fac"),2));
                    row.put("id_dest",rs.getInt("id_dest"));
                    row.put("no_dest",rs.getString("no_dest"));
                    row.put("nombre_dest",rs.getString("nombre_dest"));
                    row.put("poblacion",rs.getString("poblacion"));
                    row.put("status_id",rs.getInt("status_id"));
                    row.put("status_ped",rs.getString("status_ped"));
                    row.put("tclas_id",rs.getInt("tclas_id"));
                    row.put("cant_uni",StringHelper.roundDouble(rs.getString("cant_uni"),2));
                    row.put("peso",StringHelper.roundDouble(rs.getString("peso"),3));
                    row.put("volumen",StringHelper.roundDouble(rs.getString("volumen"),3));
                    return row;
                }
            }
        );
        return hm;
    }
    
    //Tratar los datos antes de mostrar en el grid de pedidos de la carga
    @Override
    public ArrayList<HashMap<String, Object>> tratar_datos_grid(ArrayList<HashMap<String, Object>> partidas) {
        ArrayList<HashMap<String, Object>> tratado = new ArrayList<HashMap<String, Object>>();
        HashMap<String,Object> fila=null;
        Integer id_ped_actual=0;
        
        System.out.println("--Tratar pedidos antes de mandar a la vista---------------------------");
        for( HashMap<String,Object> i : partidas ){
            fila = new HashMap<String,Object>();
            
            if(id_ped_actual!=Integer.parseInt(i.get("id_ped").toString())){
                id_ped_actual = Integer.parseInt(i.get("id_ped").toString());
                
                fila.put("tipo","PAR");
                fila.put("id_ped",id_ped_actual);
                fila.put("no_pedido",i.get("no_pedido").toString());
                fila.put("id_ped_fac",i.get("id_ped_fac"));
                fila.put("no_facura",i.get("no_facura").toString());
                fila.put("total_fac",i.get("total_fac").toString());
                fila.put("id_dest",Integer.parseInt(i.get("id_dest").toString()));
                fila.put("no_dest",i.get("no_dest").toString());
                fila.put("nombre_dest",i.get("nombre_dest").toString());
                fila.put("poblacion",i.get("poblacion").toString());
                fila.put("status_id",Integer.parseInt(i.get("status_id").toString()));
                fila.put("status_ped",i.get("status_ped").toString());
                fila.put("tclas_id",i.get("tclas_id"));
                fila.put("cant_uni",StringHelper.roundDouble(i.get("cant_uni").toString(),2));
                fila.put("peso",StringHelper.roundDouble(i.get("peso").toString(),3));
                fila.put("volumen",StringHelper.roundDouble(i.get("volumen").toString(),3));
            }else{
                fila.put("tipo","FAC");
                fila.put("id_ped",id_ped_actual);
                fila.put("no_pedido","");
                fila.put("id_ped_fac",i.get("id_ped_fac"));
                fila.put("no_facura",i.get("no_facura").toString());
                fila.put("total_fac",i.get("total_fac").toString());
                fila.put("id_dest",Integer.parseInt(i.get("id_dest").toString()));
                fila.put("no_dest","");
                fila.put("nombre_dest","");
                fila.put("poblacion","");
                fila.put("status_id",Integer.parseInt(i.get("status_id").toString()));
                fila.put("status_ped","");
                fila.put("tclas_id",i.get("tclas_id"));
                fila.put("cant_uni","");
                fila.put("peso","");
                fila.put("volumen","");
            }
            
            //Agregar fila al arreglo
            tratado.add(fila);
        }
        
        return tratado;
    }
    
    
    
    
    
    //Obtiene detalles del pedido
    @Override
    public ArrayList<HashMap<String, Object>> getLogRegCarga_ClaseTarifa() {
        //String sql_to_query = "select log_tarifa_tipo.id, upper(log_tarifa_clase.titulo||'-'||log_tarifa_tipo.titulo) as tarifa from log_tarifa_tipo  join log_tarifa_clase on log_tarifa_clase.id=log_tarifa_tipo.log_tarifa_clase_id where log_tarifa_tipo.gral_emp_id=? and log_tarifa_tipo.borrado_logico=false order by log_tarifa_clase.titulo, log_tarifa_tipo.titulo;";
        String sql_to_query = "select id, titulo from log_tarifa_clase where borrado_logico=false;";
        
        ArrayList<HashMap<String, Object>> hm = (ArrayList<HashMap<String, Object>>) this.jdbcTemplate.query(
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
        return hm;
    }
    
    
    
    
    //Obtiene detalles del pedido
    @Override
    public ArrayList<HashMap<String, Object>> getLogRegCarga_DetallePedido(Integer id_ped) {
        String sql_to_query = ""
        + "SELECT "
            + "log_doc_ped_det.id as id_det, "
            + "(case when inv_prod.id is null then 0 else inv_prod.id end) as id_prod,"
            + "inv_prod.sku AS codigo_prod,"
            + "inv_prod.descripcion AS titulo_prod,"
            + "log_doc_ped_det.cantidad,"
            //+ "(case when inv_prod_unidades.id is null or inv_prod_unidades.id=0 then (case when uni_prod.id is null then 0 else uni_prod.id end) else inv_prod_unidades.id end) AS unidad_id,"
            //+ "(case when inv_prod_unidades.id is null then (case when uni_prod.id is null then 0 else uni_prod.id end) else (case when inv_prod_unidades.id=0 then (case when uni_prod.id is null then 0 else uni_prod.id end) else inv_prod_unidades.id  end) end) AS unidad_id,
            + "(case when inv_prod_unidades.id is null then 0 else inv_prod_unidades.id end) AS unidad_id,"
            + "(case when inv_prod_unidades.id is null then '' else inv_prod_unidades.titulo_abr end) AS unidad,"
            + "log_doc_ped_det.peso,"
            + "log_doc_ped_det.volumen,"
            + "log_doc_ped_det.log_status_id as estatus "
        + "FROM log_doc_ped_det "
        + "JOIN inv_prod on inv_prod.id=log_doc_ped_det.inv_prod_id "
        + "left join inv_prod_unidades on inv_prod_unidades.id=log_doc_ped_det.inv_prod_unidad_id "
        + "left join inv_prod_unidades as uni_prod on uni_prod.id=inv_prod.unidad_id "
        + "WHERE log_doc_ped_det.log_doc_ped_id=? ORDER BY log_doc_ped_det.id;";
        
        System.out.println("id_ped: "+id_ped);
        //System.out.println("getDetallePedido: "+sql_to_query);
        ArrayList<HashMap<String, Object>> hm = (ArrayList<HashMap<String, Object>>) this.jdbcTemplate.query(
            sql_to_query,
            new Object[]{new Integer(id_ped)}, new RowMapper(){
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    HashMap<String, Object> row = new HashMap<String, Object>();
                    row.put("id_det",rs.getInt("id_det"));
                    row.put("id_prod",rs.getInt("id_prod"));
                    row.put("codigo_prod",rs.getString("codigo_prod"));
                    row.put("titulo_prod",rs.getString("titulo_prod"));
                    row.put("cantidad",StringHelper.roundDouble(rs.getString("cantidad"),2));
                    row.put("unidad_id",rs.getInt("unidad_id"));
                    row.put("unidad",rs.getString("unidad"));
                    row.put("peso",StringHelper.roundDouble(rs.getString("peso"),3));
                    row.put("volumen",StringHelper.roundDouble(rs.getString("volumen"),3));
                    row.put("estatus",rs.getInt("estatus"));
                    return row;
                }
            }
        );
        return hm;
    }
    
    
    
    //Verifica el estatus del pedido
    @Override
    public HashMap<String, Object> getLogRegCarga_VerificaStatusPedido(Integer id_ped) {
        String sql_to_query = "SELECT count(id) as count FROM log_doc_ped WHERE id=? and log_status_id>0;";
        //System.out.println("Validacion:"+sql_to_query);
        //log.log(Level.INFO, "Ejecutando query de {0}", sql_to_query);
        
        HashMap<String, Object> hm = (HashMap<String, Object>) this.jdbcTemplate.queryForObject(
            sql_to_query, 
            new Object[]{new Integer(id_ped)}, new RowMapper() {
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    HashMap<String, Object> row = new HashMap<String, Object>();
                    row.put("count",rs.getInt("count"));
                    return row;
                }
            }
        );
        return hm;
    }
    
    //Verifica el estatus de un producto del pedido
    @Override
    public HashMap<String, Object> getLogRegCarga_VerificaStatusPartida(Integer id_reg) {
        String sql_to_query = "SELECT count(id) as count FROM log_doc_ped_det WHERE id=? and log_status_id>0;";
        //System.out.println("Validacion:"+sql_to_query);
        //log.log(Level.INFO, "Ejecutando query de {0}", sql_to_query);
        
        HashMap<String, Object> hm = (HashMap<String, Object>) this.jdbcTemplate.queryForObject(
            sql_to_query, 
            new Object[]{new Integer(id_reg)}, new RowMapper() {
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    HashMap<String, Object> row = new HashMap<String, Object>();
                    row.put("count",rs.getInt("count"));
                    return row;
                }
            }
        );
        return hm;
    }
    
    
    
    
    //Obtiene todos los pedidos de la carga seleccionada
    @Override
    public ArrayList<HashMap<String, Object>> getLogPar(Integer id_emp, Integer id_suc) {
        String sql_to_query = "select * from log_par where gral_emp_id=? and gral_suc_id=? limit 1;";
        
        ArrayList<HashMap<String, Object>> hm = (ArrayList<HashMap<String, Object>>) this.jdbcTemplate.query(
            sql_to_query,
            new Object[]{new Integer(id_emp), new Integer(id_suc)}, new RowMapper(){
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    HashMap<String, Object> row = new HashMap<String, Object>();
                    row.put("alm_id",rs.getInt("inv_alm_id_default"));
                    row.put("alm_id_rechazo",rs.getInt("inv_alm_id_rechazo"));
                    row.put("asignar_tipo_unidad",rs.getInt("asignar_tipo_unidad"));
                    row.put("tipo_costeo",rs.getInt("tipo_costeo"));
                    row.put("afecta_inv_reg_carga",rs.getBoolean("afecta_inv_reg_carga"));
                    return row;
                }
            }
        );
        return hm;
    }
    
    
    
    //Buscador de Destinatarios
    @Override
    public ArrayList<HashMap<String, Object>> getBuscadorDestinatarios(String cadena, Integer filtro, Integer cliente_id, Integer id_empresa, Integer id_sucursal) {
        String where="";
        String where2="";
        
	if(filtro == 1){
            where=" and cxc_destinatarios.folio ilike '%"+cadena+"%'";
	}
	if(filtro == 2){
            where=" and cxc_destinatarios.rfc ilike '%"+cadena+"%'";
	}
	if(filtro == 3){
            where=" and cxc_destinatarios.razon_social ilike '%"+cadena+"%'";
	}
        if(id_sucursal==0){
            where +="";
        }else{
            where +=" and cxc_destinatarios.gral_suc_id="+id_sucursal;
        }
        
        if(cliente_id>0){
            where2 += "and cxc_clie_dest.cxc_clie_id="+cliente_id;
        }
        
	String sql_query = ""
        + "SELECT "
            + "id,"
            + "folio,"
            + "folio_ext,"
            + "razon_social,"
            + "rfc,"
            + "(case when dest.id is null then '' else dest.calle||' '||dest.no_int||' '||dest.no_ext||', '||dest.colonia||', '||dest.municipio||', '||dest.estado||', '||dest.pais||' C.P. '||dest.cp end) AS dir,"
            + "municipio "
        + "FROM("
                + "SELECT cxc_destinatarios.id, "
                    + "cxc_destinatarios.folio,"
                    + "cxc_destinatarios.folio_ext,"
                    + "cxc_destinatarios.razon_social,"
                    + "cxc_destinatarios.rfc,"
                    + "(case when cxc_destinatarios.calle is null then '' else cxc_destinatarios.calle end) AS calle, "
                    + "(case when cxc_destinatarios.no_int is null then '' else (case when cxc_destinatarios.no_int IS NULL OR cxc_destinatarios.no_int='' THEN '' else 'NO.INT.'||cxc_destinatarios.no_int end)  end) AS no_int, "
                    + "(case when cxc_destinatarios.no_ext is null then '' else (case when cxc_destinatarios.no_ext IS NULL OR cxc_destinatarios.no_ext='' THEN '' else 'NO.EXT.'||cxc_destinatarios.no_ext end)  end) AS no_ext, "
                    + "(case when cxc_destinatarios.colonia is null then '' else cxc_destinatarios.colonia end) AS colonia,(case when gral_mun.id IS NULL OR gral_mun.id=0 THEN '' else gral_mun.titulo end) AS municipio,"
                    + "(case when gral_edo.id IS NULL OR gral_edo.id=0 THEN '' else gral_edo.titulo end) AS estado,(case when gral_pais.id IS NULL OR gral_pais.id=0 THEN '' else gral_pais.titulo end) AS pais,"
                    + "(case when cxc_destinatarios.cp is null then '' else cxc_destinatarios.cp end) AS cp "
                + "FROM cxc_destinatarios "
                + "left join gral_pais on gral_pais.id = cxc_destinatarios.gral_pais_id "
                + "left join gral_edo on gral_edo.id = cxc_destinatarios.gral_edo_id "
                + "left join gral_mun on gral_mun.id = cxc_destinatarios.gral_mun_id  "
                + "join cxc_clie_dest on (cxc_clie_dest.cxc_destinatario_id=cxc_destinatarios.id "+ where2 +") "
                + "WHERE cxc_destinatarios.gral_emp_id=? AND cxc_destinatarios.borrado_logico=false  "+where+" "
        + ") AS dest ORDER BY id limit 100;";
        
        //System.out.println("BuscarDest: "+sql_query);
        ArrayList<HashMap<String, Object>> hm_dest = (ArrayList<HashMap<String, Object>>) this.jdbcTemplate.query(
            sql_query,
            new Object[]{new Integer(id_empresa)}, new RowMapper() {
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    HashMap<String, Object> row = new HashMap<String, Object>();
                    row.put("id",rs.getInt("id"));
                    row.put("folio",rs.getString("folio"));
                    row.put("folio_ext",rs.getString("folio_ext"));
                    row.put("rfc",rs.getString("rfc"));
                    row.put("razon_social",rs.getString("razon_social"));
                    row.put("poblacion",rs.getString("municipio"));
                    return row;
                }
            }
        );
        return hm_dest;
    }
    
    //Obtener datos del Destinatario a partir del Numero de Control
    @Override
    public ArrayList<HashMap<String, Object>> getDatosByNoDestinatario(String no_control, Integer cliente_id, Integer id_empresa, Integer id_sucursal) {
        
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
            + "folio_ext,"
            + "razon_social,"
            + "rfc,"
            + "(case when dest.id is null then '' else dest.calle||' '||dest.no_int||' '||dest.no_ext||', '||dest.colonia||', '||dest.municipio||', '||dest.estado||', '||dest.pais||' C.P. '||dest.cp end) AS dir,"
            + "municipio "
        + "FROM("
                + "SELECT cxc_destinatarios.id, "
                    + "cxc_destinatarios.folio,"
                    + "cxc_destinatarios.folio_ext,"
                    + "cxc_destinatarios.razon_social,"
                    + "cxc_destinatarios.rfc,"
                    + "(case when cxc_destinatarios.calle is null then '' else cxc_destinatarios.calle end) AS calle, "
                    + "(case when cxc_destinatarios.no_int is null then '' else (case when cxc_destinatarios.no_int IS NULL OR cxc_destinatarios.no_int='' THEN '' else 'NO.INT.'||cxc_destinatarios.no_int end)  end) AS no_int, "
                    + "(case when cxc_destinatarios.no_ext is null then '' else (case when cxc_destinatarios.no_ext IS NULL OR cxc_destinatarios.no_ext='' THEN '' else 'NO.EXT.'||cxc_destinatarios.no_ext end)  end) AS no_ext, "
                    + "(case when cxc_destinatarios.colonia is null then '' else cxc_destinatarios.colonia end) AS colonia,(case when gral_mun.id IS NULL OR gral_mun.id=0 THEN '' else gral_mun.titulo end) AS municipio,"
                    + "(case when gral_edo.id IS NULL OR gral_edo.id=0 THEN '' else gral_edo.titulo end) AS estado,(case when gral_pais.id IS NULL OR gral_pais.id=0 THEN '' else gral_pais.titulo end) AS pais,"
                    + "(case when cxc_destinatarios.cp is null then '' else cxc_destinatarios.cp end) AS cp "
                + "FROM cxc_destinatarios "
                + "left join gral_pais on gral_pais.id = cxc_destinatarios.gral_pais_id "
                + "left join gral_edo on gral_edo.id = cxc_destinatarios.gral_edo_id "
                + "left join gral_mun on gral_mun.id = cxc_destinatarios.gral_mun_id  "
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
                    row.put("folio_ext",rs.getString("folio_ext"));
                    row.put("rfc",rs.getString("rfc"));
                    row.put("razon_social",rs.getString("razon_social"));
                    row.put("dir",rs.getString("dir"));
                    row.put("poblacion",rs.getString("municipio"));
                    return row;
                }
            }
        );
        return hm_dest;
    }

    
    
    
    
    
    //METODOS PARA EL CATALOGO DE TARIFARIO************************************
    
    @Override
    public ArrayList<HashMap<String, Object>> getPaises() {
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
    
    
    
        @Override
    public ArrayList<HashMap<String, Object>> getEntidades() {
        String sql_to_query = "SELECT id as cve_ent, titulo as nom_ent FROM gral_edo order by nom_ent;";
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
    public ArrayList<HashMap<String, Object>> getLocalidades() {
        String sql_to_query = "SELECT id as cve_mun, titulo as nom_mun FROM gral_mun  order by nom_mun;";

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
    
    
    
    //Obtiene tipos de Ruta
    @Override
    public ArrayList<HashMap<String, Object>> getRutaTipo(Integer id_empresa) {
	String sql_query = "SELECT id,titulo FROM log_ruta_tipo WHERE borrado_logico=false order by titulo ASC;";
        ArrayList<HashMap<String, Object>> hm_rtipo = (ArrayList<HashMap<String, Object>>) this.jdbcTemplate.query(
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
        
        return hm_rtipo;
    }
    
    
    //obtiene datos de la ruta para ver detalles y editar
    @Override
    public ArrayList<HashMap<String, Object>> getDatosRuta_editar_header(Integer id) {
        String sql_to_query = ""
                + "SELECT  "
                    + "log_ruta_tipo.id as id_tipo,  "
                    + "log_ruta_tipo.titulo as titulo,  "
                    + "log_ruta.titulo as nombreruta,  "
                    + "log_ruta.id as id_ruta,  "
                    + "log_ruta.folio, "
                    + "log_ruta.km "
                + "FROM log_ruta "
                + "join log_ruta_tipo on log_ruta_tipo.id=log_ruta.log_ruta_tipo_id "
                + "where log_ruta.id="+id;
        
        //log.log(Level.INFO, "Ejecutando query de {0}", sql_to_query);
        //System.out.println("Onteniedo datos Ruta header:::   "+sql_to_query);
        ArrayList<HashMap<String, Object>> hm_header = (ArrayList<HashMap<String, Object>>) this.jdbcTemplate.query(
            sql_to_query,
            new Object[]{}, new RowMapper(){
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    HashMap<String, Object> row = new HashMap<String, Object>();
                    row.put("id_tipo",String.valueOf(rs.getInt("id_tipo")));
                    row.put("titulo",rs.getString("titulo"));
                    row.put("nombreruta",rs.getString("nombreruta"));
                    row.put("id_ruta",String.valueOf(rs.getInt("id_ruta")));
                    row.put("folio",rs.getString("folio"));
                    row.put("km",String.valueOf(rs.getInt("km")));
                    
                    return row;
                }
            }
        );
        return hm_header;
    }
    
    
    
    //metodo que obtiene datos para el grid de Ruta
    @Override
    public ArrayList<HashMap<String, Object>> getRuta_PaginaGrid(String data_string, int offset, int pageSize, String orderBy, String asc) {
        String sql_busqueda = "select id from gral_bus_catalogos(?) as foo (id integer)";
        
	String sql_to_query = "SELECT  log_ruta.id , log_ruta.folio, log_ruta.titulo as nombreruta, "
                              +"  log_ruta_tipo.titulo, "
                              +"  log_ruta.km "
                              +"  FROM log_ruta "
                              +"  JOIN  log_ruta_tipo on log_ruta_tipo.id=log_ruta.log_ruta_tipo_id "
                              +"JOIN ("+sql_busqueda+") AS sbt ON sbt.id = log_ruta.id "
                              +"WHERE log_ruta.borrado_logico=false  "
                              +"order by "+orderBy+" "+asc+" limit ? OFFSET ?";
        //System.out.println("IMPRIMIENDO EL GRID DE RUTA: "+sql_to_query);
        ArrayList<HashMap<String, Object>> hm = (ArrayList<HashMap<String, Object>>) this.jdbcTemplate.query(
            sql_to_query, 
            new Object[]{data_string, new Integer(pageSize),new Integer(offset)}, new RowMapper() {
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    HashMap<String, Object> row = new HashMap<String, Object>();
                    row.put("id",rs.getInt("id"));
                    row.put("folio",rs.getString("folio"));
                    row.put("nombreruta",rs.getString("nombreruta"));
                    row.put("titulo",rs.getString("titulo"));
                    row.put("km",rs.getString("km"));
                   
                    return row;
                }
            }
        );
        return hm; 
    }
    
    
    //Obtiene Tipos de  Unidades
    @Override
    public ArrayList<HashMap<String, Object>> getRutaTipoUnidades(Integer id_empresa) {
	String sql_query = "SELECT id,titulo,volumen_inicio,volumen_fin,kg_inicio,kg_fin FROM log_vehiculo_tipo WHERE borrado_logico=false and log_vehiculo_tipo.gral_emp_id=? order by titulo ASC;";
        ArrayList<HashMap<String, Object>> hm_rtipo = (ArrayList<HashMap<String, Object>>) this.jdbcTemplate.query(
            sql_query,
            new Object[]{id_empresa}, new RowMapper() {
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    HashMap<String, String> row = new HashMap<String, String>();
                    row.put("id",String.valueOf(rs.getInt("id")));
                    row.put("titulo",rs.getString("titulo"));
                    row.put("volumen_inicio",StringHelper.roundDouble(rs.getDouble("volumen_inicio"),2));
                    row.put("volumen_fin",StringHelper.roundDouble(rs.getDouble("volumen_fin"),2));
                    row.put("kg_inicio",StringHelper.roundDouble(rs.getDouble("kg_inicio"),2));
                    row.put("kg_fin",StringHelper.roundDouble(rs.getDouble("kg_fin"),2));
                    return row;
                }
            }
        );
        return hm_rtipo;
    }
    
    @Override
    public ArrayList<HashMap<String, Object>> Datos_editar_minigridpoblaciones(Integer id) {
             
                String sql_to_query = ""
                        + "SELECT "
                        + "log_ruta_mun.id AS id_reg,"
                        + "log_ruta_mun.gral_mun_id as id_mun,"
                        + "gral_mun.titulo AS nom_mun, "
                        + "gral_mun.estado_id AS estado_id, "
                        + "gral_mun.pais_id AS pais_id "
                        + "FROM log_ruta_mun "
                        + "JOIN gral_mun on gral_mun.id=log_ruta_mun.gral_mun_id "
                        + "JOIN log_ruta on log_ruta.id=log_ruta_mun.log_ruta_id "
                        + "WHERE log_ruta.id="+id+" "
                        + "ORDER BY log_ruta.id";
                
        //log.log(Level.INFO, "Ejecutando query de {0}", sql_to_query);
        //System.out.println("Obteniendo minigrid_poblaciones :::"+ sql_to_query);
        ArrayList<HashMap<String, Object>> hm_header = (ArrayList<HashMap<String, Object>>) this.jdbcTemplate.query(
            sql_to_query,
            new Object[]{}, new RowMapper(){
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    HashMap<String, String> row = new HashMap<String, String>();
                    row.put("id_reg",String.valueOf(rs.getInt("id_reg")));
                    row.put("id_mun",String.valueOf(rs.getInt("id_mun")));
                    row.put("estado_id",String.valueOf(rs.getInt("estado_id")));
                    row.put("pais_id",String.valueOf(rs.getInt("pais_id")));
                    row.put("nom_mun",rs.getString("nom_mun"));
                    return row;
                }
            }
        );
        return hm_header;
    }
    
    
    
    @Override
    public ArrayList<HashMap<String, Object>> Datos_editar_minigridtiposunidad(Integer id_empresa,Integer id) {
             
        String sql_to_query = ""
        + "SELECT "
                + "(case when log_ruta_tipo_unidad.id is null then 0 else log_ruta_tipo_unidad.id end) as id_reg, "
                + "log_vehiculo_tipo.id AS id_tipounidad, " 
                + "log_vehiculo_tipo.titulo AS nom_vehiculo, " 
                + "log_vehiculo_tipo.volumen_inicio AS volumen_inicio, " 
                + "log_vehiculo_tipo.volumen_fin AS volumen_fin, " 
                + "log_vehiculo_tipo.kg_inicio AS kg_inicio , "
                + "log_vehiculo_tipo.kg_fin AS kg_fin, " 
                + "log_vehiculo_tipo.carton_inicio AS carton_ini, " 
                + "log_vehiculo_tipo.carton_fin AS carton_fin, " 
                + "log_vehiculo_tipo.tarima_inicio AS tarima_ini, " 
                + "log_vehiculo_tipo.tarima_fin AS tarima_fin, " 
                + "(case when log_ruta_tipo_unidad.costo is null then 0 else log_ruta_tipo_unidad.costo end) AS costo,"
                + "(case when log_ruta_tipo_unidad.costo_hot_shot is null then 0 else log_ruta_tipo_unidad.costo_hot_shot end) AS costo_hs, "
                + "(case when log_ruta_tipo_unidad.costo_extra_reparto_local is null then 0 else log_ruta_tipo_unidad.costo_extra_reparto_local end) AS costo_exl, "
                + "(case when log_ruta_tipo_unidad.costo_extra_reparto_foraneo is null then 0 else log_ruta_tipo_unidad.costo_extra_reparto_foraneo end) AS costo_exf, "
                + "(case when log_ruta_tipo_unidad.costo_estadia is null then 0 else log_ruta_tipo_unidad.costo_estadia end) AS costo_estadia "
        + "FROM log_vehiculo_tipo "
        + "left join  log_ruta_tipo_unidad on (log_ruta_tipo_unidad.log_vehiculo_tipo_id=log_vehiculo_tipo.id and log_ruta_tipo_unidad.log_ruta_id="+id+") "
        + "WHERE borrado_logico=false and log_vehiculo_tipo.gral_emp_id ="+id_empresa+" "
        + "ORDER BY log_ruta_tipo_unidad.id ";
        
        //log.log(Level.INFO, "Ejecutando query de {0}", sql_to_query);
        //System.out.println("Obteniendo minigrid_tiposunidad :::"+ sql_to_query);
        ArrayList<HashMap<String, Object>> hm_header = (ArrayList<HashMap<String, Object>>) this.jdbcTemplate.query(
            sql_to_query,
            new Object[]{}, new RowMapper(){
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    HashMap<String, String> row = new HashMap<String, String>();
                    row.put("id_reg",String.valueOf(rs.getInt("id_reg")));
                    row.put("id_tipounidad",String.valueOf(rs.getInt("id_tipounidad")));
                    row.put("nom_vehiculo",rs.getString("nom_vehiculo"));
                    row.put("volumen_inicio",StringHelper.roundDouble(rs.getDouble("volumen_inicio"),2));
                    row.put("volumen_fin",StringHelper.roundDouble(rs.getDouble("volumen_fin"),2));
                    row.put("kg_inicio",StringHelper.roundDouble(rs.getDouble("kg_inicio"),2));
                    row.put("kg_fin",StringHelper.roundDouble(rs.getDouble("kg_fin"),2));
                    row.put("carton_ini",StringHelper.roundDouble(rs.getDouble("carton_ini"),2));
                    row.put("carton_fin",StringHelper.roundDouble(rs.getDouble("carton_fin"),2));
                    row.put("tarima_ini",StringHelper.roundDouble(rs.getDouble("tarima_ini"),2));
                    row.put("tarima_fin",StringHelper.roundDouble(rs.getDouble("tarima_fin"),2));
                    row.put("costo",StringHelper.roundDouble(rs.getDouble("costo"),2));
                    row.put("costo_hs",StringHelper.roundDouble(rs.getDouble("costo_hs"),2));
                    row.put("costo_exl",StringHelper.roundDouble(rs.getDouble("costo_exl"),2));
                    row.put("costo_exf",StringHelper.roundDouble(rs.getDouble("costo_exf"),2));
                    row.put("costo_estadia",StringHelper.roundDouble(rs.getDouble("costo_estadia"),2));
                    
                    return row;
                }
            }
        );
        return hm_header;
    }
    //TERMINA METODOS DEL TARIFARIO********************************************
    
    
    
    /*
     * CATALOGO DE TIPOS DE RECHAZO
     */
    //Obtiene datos para el grid del Catalogo de Tipo de Rechazo
    @Override
    public ArrayList<HashMap<String, Object>> getTipoRechazo_PaginaGrid(String data_string, int offset, int pageSize, String orderBy, String asc) {
        String sql_busqueda = "select id from gral_bus_catalogos(?) as foo (id integer)";
        
	String sql_to_query = "SELECT log_tipo_rechazo.id, log_tipo_rechazo.titulo "                              
                                +"FROM log_tipo_rechazo "                        
                                +"JOIN ("+sql_busqueda+") AS sbt on sbt.id = log_tipo_rechazo.id "
                                +"WHERE log_tipo_rechazo.borrado_logico=false "
                                +"order by "+orderBy+" "+asc+" limit ? OFFSET ?";
        
        //System.out.println("Busqueda GetPage: "+sql_to_query+" "+data_string+" "+ offset +" "+ pageSize);
        //System.out.println("esto es el query  :  "+sql_to_query);
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

    
    
    //Obtiene datos de Tipo de Rechazo
    @Override
    public ArrayList<HashMap<String, String>> getTipoRechazo_Datos(Integer id) {
        
        String sql_to_query = "SELECT id,titulo FROM log_tipo_rechazo WHERE id="+id;
        
        ArrayList<HashMap<String, String>> dato_tiporechazo = (ArrayList<HashMap<String, String>>) this.jdbcTemplate.query(
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
        return dato_tiporechazo;
    }
    
    
    
    
    
    
    
    
    //Obtener el detalle para el programa de envidencias
    @Override
    public ArrayList<HashMap<String, Object>> getLoEvidencias_DatosGrid(Integer id) {
        String sql_to_query = ""
        + "select "
            + "cxc_clie.numero_control AS no_clie,"
            + "substr(upper(cxc_clie.razon_social),1,12) AS clie,"
            + "log_viaje_det.id AS det_id,"
            + "log_doc_carga.id AS cga_id,"
            + "log_doc_ped.id AS ped_id,"
            + "log_doc_carga.no_carga,"
            + "log_doc_carga.fecha_entrega,"
            + "log_doc_ped.no_pedido,"
            + "cxc_destinatarios.id AS id_dest,"
            + "cxc_destinatarios.folio_ext AS no_dest,"
            + "cxc_destinatarios.razon_social AS nombre_dest,"
            + "gral_mun.id AS mun_id,"
            + "upper(gral_mun.titulo) AS municipio,"
            + "log_doc_ped.log_status_id AS status_ped,"
            + "(case when log_status.id is null then '' else log_status.titulo end) as status_det, "
            + "log_viaje_det.solicitar_firma AS firma,"
            + "log_viaje_det.solicitar_sello AS sello,"
            + "log_viaje_det.solicitar_efectivo AS efectivo,"
            + "log_viaje_det.solicitar_cheque AS cheque,"
            
            + "(case when log_doc_ped_fac.id is null then 0 else log_doc_ped_fac.id end) as id_ped_fac,"
            + "(case when log_doc_ped_fac.no_facura is null then '' else log_doc_ped_fac.no_facura end) as no_facura,"
            + "(case when log_doc_ped_fac.total_factura is null then 0 else log_doc_ped_fac.total_factura end) as monto_fac,"
            + "(case when log_doc_ped_fac.id is null then false else log_doc_ped_fac.firma end) as evid_firma,"
            + "(case when log_doc_ped_fac.id is null then false else log_doc_ped_fac.sello end) as evid_sello,"
            + "(case when log_doc_ped_fac.id is null then false else log_doc_ped_fac.cheque end) as evid_cheque,"
            + "(case when log_doc_ped_fac.id is null then false else log_doc_ped_fac.efectivo end) as evid_efectivo,"
            + "(case when log_doc_ped_fac.id is null then '' else log_doc_ped_fac.no_cheque end) as evid_noche,"
            + "(case when log_doc_ped_fac.id is null then 0 else log_doc_ped_fac.cantidad end) as evid_cant,"
            + "(case when log_doc_ped_fac.id is null then '' else (case when log_doc_ped_fac.fecha_evidencia is null then '' else to_char(log_doc_ped_fac.fecha_evidencia, 'yyyy-mm-dd') end) end) as evid_fecha,"
            + "(case when log_doc_ped_fac.id is null then 0 else 1 end) as exis_fac, "
            + "(case when log_doc_ped_fac.id is null then 0 else log_doc_ped_fac.log_status_id end) as status_id_fac "
        + "from log_viaje_det "
        + "join log_doc_carga on log_doc_carga.id=log_viaje_det.log_doc_carga_id "
        + "join log_doc_ped on log_doc_ped.id=log_viaje_det.log_doc_ped_id  "
        + "join cxc_clie on cxc_clie.id=log_viaje_det.cxc_clie_id "
        + "join cxc_destinatarios on cxc_destinatarios.id=log_doc_ped.cxc_dest_id  "
        + "join gral_mun on gral_mun.id=log_viaje_det.gral_mun_id  "
        + "left join log_doc_ped_fac on log_doc_ped_fac.log_doc_ped_id=log_doc_ped.id "
        + "left join log_status on log_status.id=log_viaje_det.log_status_id "
        + "where log_viaje_det.log_viaje_id=? "
        + "order by log_viaje_det.id;";
        
        //System.out.println("id: "+ id);
        //System.out.print("sql_to_query: "+sql_to_query);
        
        ArrayList<HashMap<String, Object>> hm = (ArrayList<HashMap<String, Object>>) this.jdbcTemplate.query(
            sql_to_query,
            new Object[]{new Integer(id)}, new RowMapper(){
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    HashMap<String, Object> row = new HashMap<String, Object>();
                    row.put("no_clie",rs.getString("no_clie"));
                    row.put("clie",rs.getString("clie"));
                    row.put("det_id",rs.getInt("det_id"));
                    row.put("cga_id",rs.getInt("cga_id"));
                    row.put("f_entrega",rs.getString("fecha_entrega"));
                    row.put("ped_id",rs.getInt("ped_id"));
                    row.put("no_carga",rs.getString("no_carga"));
                    row.put("no_pedido",rs.getString("no_pedido"));
                    row.put("id_dest",rs.getInt("id_dest"));
                    row.put("no_dest",rs.getString("no_dest"));
                    row.put("nombre_dest",rs.getString("nombre_dest"));
                    row.put("mun_id",rs.getInt("mun_id"));
                    row.put("municipio",rs.getString("municipio"));
                    row.put("status_ped",rs.getString("status_ped"));
                    row.put("status_det",rs.getString("status_det"));
                    
                    row.put("firma",rs.getBoolean("firma"));
                    row.put("sello",rs.getBoolean("sello"));
                    row.put("efectivo",rs.getBoolean("efectivo"));
                    row.put("cheque",rs.getBoolean("cheque"));
                    
                    row.put("id_ped_fac",rs.getInt("id_ped_fac"));
                    row.put("no_facura",rs.getString("no_facura"));
                    row.put("monto_fac",StringHelper.roundDouble(rs.getString("monto_fac"),2));
                    row.put("evid_firma",rs.getBoolean("evid_firma"));
                    row.put("evid_sello",rs.getBoolean("evid_sello"));
                    row.put("evid_cheque",rs.getBoolean("evid_cheque"));
                    row.put("evid_efectivo",rs.getBoolean("evid_efectivo"));
                    row.put("evid_noche",rs.getString("evid_noche"));
                    row.put("evid_cant",StringHelper.roundDouble(rs.getString("evid_cant"),2));
                    row.put("evid_fecha",rs.getString("evid_fecha"));
                    row.put("exis_fac",rs.getInt("exis_fac"));
                    row.put("status_id_fac",rs.getInt("status_id_fac"));
                    return row;
                }
            }
        );
        return hm;
    }
    
    
    
    //Reporte de Viajes
    
    @Override
    public ArrayList<HashMap<String, String>> getRepLogAdmViajes(String fecha_inicial, String fecha_final, String cliente, String ruta, String clase, Integer id_empresa) {

    String where="";

    if(!cliente.equals("")){
        where = "  and cxc_clie.razon_social ilike '%" +cliente +"%'";
    }
    
    if(!ruta.equals("")){
        where = "  and log_ruta.titulo ilike '%"+ ruta +"%'";
    }
    
    
    if(!clase.trim().equals("0")){
        where +=" and log_vehiculo_clase.id="+clase+" ";
    }
    
    
    System.out.println("where="+where);
     /*
    String sql_to_query = ""
    + "select "
        + "log_viaje.folio, "
        + "to_char(log_viaje.fecha::timestamp with time zone, 'dd/mm/yyyy') as fecha, "
        + "(case when log_ruta.titulo is null then '' else log_ruta.titulo end) as ruta,"
        + "(case when log_choferes.nombre is null then '' else log_choferes.nombre end) ||' '|| (case when log_choferes.apellido_paterno is null then '' else log_choferes.apellido_paterno end) ||' '|| case when log_choferes.apellido_materno is null then '' else log_choferes.apellido_materno END AS operador, "
        + "(case when cxp_prov.id is null then '' else cxp_prov.razon_social end) as tranportista_proveedor,"
        + "sum(log_doc_ped_det.cantidad) as cant_uni, "
        + "sum(log_doc_ped_det.peso) AS peso,"
        + "(case when log_vehiculo_tipo.titulo is null then '' else log_vehiculo_tipo.titulo end) as tipo_unidad,"
        + "(case when log_ruta_tipo_unidad.costo is null then 0 else log_ruta_tipo_unidad.costo end) as costo_viaje,"
        + "(case when cxc_clie.razon_social is null then '' else cxc_clie.razon_social end) as cliente "
    + "from log_viaje "
    + "join log_viaje_det on log_viaje_det.log_viaje_id=log_viaje.id "
    + "join log_doc_ped on log_doc_ped.id=log_viaje_det.log_doc_ped_id "
    + "join log_doc_ped_det on log_doc_ped_det.log_doc_ped_id=log_doc_ped.id "
    + "join gral_mun on gral_mun.id=log_viaje_det.gral_mun_id "
    + "join cxc_clie on cxc_clie.id=log_viaje_det.cxc_clie_id "
    + "left join log_ruta on log_ruta.id=log_viaje.log_ruta_id  "
    + "left join log_choferes on log_choferes.id=log_viaje.log_chofer_id "
    + "left join log_vehiculos on log_vehiculos.id=log_viaje.log_vehiculo_id "
    + "left join log_vehiculo_tipo on log_vehiculo_tipo.id=log_vehiculos.log_vehiculo_tipo_id "
    + "left join log_ruta_tipo_unidad on (log_ruta_tipo_unidad.log_ruta_id=log_ruta.id and log_ruta_tipo_unidad.log_vehiculo_tipo_id=log_vehiculos.log_vehiculo_tipo_id) "
    + "left join cxp_prov on cxp_prov.id=log_vehiculos.cxp_prov_id  "
    + "where log_viaje.borrado_logico=false and log_viaje.log_status_id>=2 and log_ruta.gral_emp_id=? "+ where +" "
    +" and (to_char(log_viaje.fecha,'yyyymmdd')::integer BETWEEN to_char('"+fecha_inicial+"'::timestamp with time zone,'yyyymmdd')::integer AND to_char('"+fecha_final+"'::timestamp with time zone,'yyyymmdd')::integer) "
    + "group by log_viaje.folio, log_viaje.fecha, log_ruta.titulo, log_choferes.nombre, log_choferes.apellido_paterno, log_choferes.apellido_materno, cxp_prov.id, log_vehiculo_tipo.titulo, log_ruta_tipo_unidad.costo, cxc_clie.razon_social "
    + "order by log_viaje.fecha";
    */
    
    
    
    String sql_to_query = ""
    + "select * from ("
        + "select * from ("
            + "select "
                + "1::integer as tipo,"    
                + "log_viaje.folio, "
                + "to_char(log_viaje.fecha::timestamp with time zone, 'dd/mm/yyyy') as fecha, "
                + "log_viaje.fecha as fecha_registro,"
                + "(case when log_ruta.titulo is null then '' else log_ruta.titulo end) as ruta,"
                + "(case when log_choferes.nombre is null then '' else log_choferes.nombre end) ||' '|| (case when log_choferes.apellido_paterno is null then '' else log_choferes.apellido_paterno end) ||' '|| case when log_choferes.apellido_materno is null then '' else log_choferes.apellido_materno END AS operador, "
                + "(case when cxp_prov.id is null then '' else cxp_prov.razon_social end) as tranportista_proveedor,"
                + "sum(log_doc_ped_det.cantidad) as cant_uni, "
                + "sum(log_doc_ped_det.peso) AS peso,"
                + "(case when log_vehiculo_tipo.titulo is null then '' else log_vehiculo_tipo.titulo end) as tipo_unidad,"
                + "(case when log_vehiculo_clase.titulo is null then '' else log_vehiculo_clase.titulo end) as clase,"
                //+ "(case when log_ruta_tipo_unidad.costo is null then 0 else log_ruta_tipo_unidad.costo end) as costo_viaje,"
                + "(case when log_viaje.costo_ruta=0 then (case when log_ruta_tipo_unidad.costo is null then 0 else log_ruta_tipo_unidad.costo end) else log_viaje.costo_ruta end) as costo_viaje, "
                + "(case when cxc_clie.razon_social is null then '' else cxc_clie.razon_social end) as cliente "
            + "from log_viaje "
            + "join log_viaje_det on log_viaje_det.log_viaje_id=log_viaje.id "
            + "join log_doc_ped on log_doc_ped.id=log_viaje_det.log_doc_ped_id "
            + "join log_doc_ped_det on log_doc_ped_det.log_doc_ped_id=log_doc_ped.id "
            + "join cxc_clie on cxc_clie.id=log_viaje_det.cxc_clie_id "
            + "left join log_ruta on log_ruta.id=log_viaje.log_ruta_id  "
            + "left join log_choferes on log_choferes.id=log_viaje.log_chofer_id "
            + "left join log_vehiculos on log_vehiculos.id=log_viaje.log_vehiculo_id "
            + "left join log_vehiculo_clase on log_vehiculo_clase.id=log_vehiculos.log_vehiculo_clase_id "
            + "left join log_vehiculo_tipo on log_vehiculo_tipo.id=log_vehiculos.log_vehiculo_tipo_id "
            + "left join log_ruta_tipo_unidad on (log_ruta_tipo_unidad.log_ruta_id=log_ruta.id and log_ruta_tipo_unidad.log_vehiculo_tipo_id=log_vehiculos.log_vehiculo_tipo_id) "
            + "left join cxp_prov on cxp_prov.id=log_vehiculos.cxp_prov_id  "
            + "where log_viaje.borrado_logico=false and log_viaje.log_status_id in(2,3,4,5) and log_ruta.gral_emp_id=? "+ where +" "
            +" and (to_char(log_viaje.fecha,'yyyymmdd')::integer BETWEEN to_char('"+fecha_inicial+"'::timestamp with time zone,'yyyymmdd')::integer AND to_char('"+fecha_final+"'::timestamp with time zone,'yyyymmdd')::integer) "
            + "group by log_viaje.folio, log_viaje.fecha, log_ruta.titulo, log_choferes.nombre, log_choferes.apellido_paterno, log_choferes.apellido_materno, cxp_prov.id, log_vehiculo_tipo.titulo, log_vehiculo_clase.titulo, log_viaje.costo_ruta, log_ruta_tipo_unidad.costo, cxc_clie.razon_social  "
            + "order by log_viaje.fecha"
        + ") as sbt1 "
        + "UNION "
        + "select * from ("
            + "select "
                + "2::integer as tipo, "
                + "log_doc.folio,  "
                + "to_char(log_doc.fecha_carga::timestamp with time zone, 'dd/mm/yyyy') as fecha,  "
                + "log_doc.fecha_carga as fecha_registro, "
                + "(case when log_ruta.titulo is null then '' else log_ruta.titulo end) as ruta, "
                + "(case when log_choferes.nombre is null then '' else log_choferes.nombre end) ||' '|| (case when log_choferes.apellido_paterno is null then '' else log_choferes.apellido_paterno end) ||' '|| case when log_choferes.apellido_materno is null then '' else log_choferes.apellido_materno END AS operador, "
                + "(case when cxp_prov.id is null then '' else cxp_prov.razon_social end) as tranportista_proveedor, "
                + "log_doc_vehiculo.cant_uni, "
                + "log_doc_vehiculo.peso AS peso, "
                + "(case when log_vehiculo_tipo.titulo is null then '' else log_vehiculo_tipo.titulo end) as tipo_unidad, "
                + "(case when log_vehiculo_clase.titulo is null then '' else log_vehiculo_clase.titulo end) as clase, "
                + "(case when log_doc_vehiculo.costo is null then 0 else log_doc_vehiculo.costo end) as costo_viaje, "
                + "(case when cxc_clie.razon_social is null then '' else cxc_clie.razon_social end) as cliente  "
            + "from log_doc   "
            + "left join log_doc_vehiculo on log_doc_vehiculo.log_doc_id=log_doc.id  "
            + "join cxc_clie on cxc_clie.id=log_doc.cxc_clie_id  "
            + "left join log_ruta on log_ruta.id=log_doc_vehiculo.log_ruta_id  "
            + "left join log_choferes on log_choferes.id=log_doc_vehiculo.log_chofer_id "
            + "left join log_vehiculos on log_vehiculos.id=log_doc_vehiculo.log_vehiculo_id "
            + "left join log_vehiculo_clase on log_vehiculo_clase.id=log_vehiculos.log_vehiculo_clase_id "
            + "left join log_vehiculo_tipo on log_vehiculo_tipo.id=log_vehiculos.log_vehiculo_tipo_id "
            + "left join log_ruta_tipo_unidad on (log_ruta_tipo_unidad.log_ruta_id=log_ruta.id and log_ruta_tipo_unidad.log_vehiculo_tipo_id=log_vehiculos.log_vehiculo_tipo_id) "
            + "left join cxp_prov on cxp_prov.id=log_vehiculos.cxp_prov_id  "
            //+ "where log_doc.gral_emp_id=? and log_doc.tipo in(1,2) "+ where +" "
            + "where log_doc.gral_emp_id=? and log_doc.tipo in(1) "+ where +" "
            + "and (to_char(log_doc.fecha_carga,'yyyymmdd')::integer BETWEEN to_char('"+fecha_inicial+"'::timestamp with time zone,'yyyymmdd')::integer AND to_char('"+fecha_final+"'::timestamp with time zone,'yyyymmdd')::integer) "
            + "order by log_doc.fecha_carga"
        + ") as sbt2 "
    + ") as sbt3 order by fecha_registro;";
    
    
    //System.out.println("getRepLogAdmViajes: "+sql_to_query);
    ArrayList<HashMap<String, String>> hm_facturas = (ArrayList<HashMap<String, String>>) this.jdbcTemplate.query(
        sql_to_query,
        new Object[]{new Integer(id_empresa), new Integer(id_empresa)}, new RowMapper(){
            @Override
            public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                HashMap<String, String> row = new HashMap<String, String>();
                //row.put("id",rs.getString("id"));
                row.put("folio",rs.getString("folio"));
                row.put("fecha",rs.getString("fecha"));
                row.put("ruta",rs.getString("ruta"));
                row.put("operador",rs.getString("operador"));
                row.put("tranportista_proveedor",rs.getString("tranportista_proveedor"));
                row.put("cant_uni",StringHelper.roundDouble(rs.getDouble("cant_uni"), 2));
                row.put("peso",StringHelper.roundDouble(rs.getDouble("peso"), 3));
                row.put("tipo_unidad",rs.getString("tipo_unidad"));
                row.put("clase",rs.getString("clase"));
                row.put("costo_viaje",StringHelper.roundDouble(rs.getDouble("costo_viaje"), 2));
                row.put("cliente",rs.getString("cliente"));
                return row;
            }
        }
    );
    return hm_facturas;
}
        
        
    //Buscador de Rutas
    @Override
    public ArrayList<HashMap<String, Object>> getBuscador_Rutas(String no_ruta, String nombre_ruta, String poblacion, Integer id_empresa, Integer id_sucursal) {
        String where = "";
	if(!no_ruta.trim().equals("")){
            where=" and log_ruta.folio ilike '%"+no_ruta.trim()+"%'";
	}
        
	if(!nombre_ruta.trim().equals("")){
            where +=" and log_ruta.titulo ilike '%"+ nombre_ruta.trim() +"%'";
	}
        
	if(!poblacion.trim().equals("")){
            where +=" and gral_mun.titulo ilike '%"+ poblacion.trim() +"%'";
	}
        
	/*if(tipo_unidad>0){
            where +=" and log_ruta_tipo_unidad.log_vehiculo_tipo_id="+ tipo_unidad +"";
	}*/
        
        String sql_to_query = ""
        + "select distinct "
            + "log_ruta.id,"
            + "log_ruta.folio,"
            + "log_ruta.titulo as titulo_ruta,"
            + "log_ruta.km "
            //+ "log_ruta_tipo_unidad.costo "
        + "from log_ruta "
        //+ "join log_ruta_tipo_unidad on log_ruta_tipo_unidad.log_ruta_id=log_ruta.id "
        + "join log_ruta_mun on log_ruta_mun.log_ruta_id=log_ruta.id  "
        + "join gral_mun on gral_mun.id=log_ruta_mun.gral_mun_id "
        + "where log_ruta.gral_emp_id=? and log_ruta.gral_suc_id=? and log_ruta.borrado_logico=false "+where+" order by log_ruta.titulo;";
        
        //log.log(Level.INFO, "Ejecutando query de {0}", sql_to_query);
        //System.out.println("sql_to_query: "+sql_to_query);

        ArrayList<HashMap<String, Object>> hm = (ArrayList<HashMap<String, Object>>) this.jdbcTemplate.query(
            sql_to_query,
            new Object[]{new Integer(id_empresa), new Integer(id_sucursal)}, new RowMapper(){
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    HashMap<String, Object> row = new HashMap<String, Object>();
                    row.put("id",rs.getString("id"));
                    row.put("folio",rs.getString("folio"));
                    row.put("titulo_ruta",rs.getString("titulo_ruta"));
                    row.put("km",StringHelper.roundDouble(rs.getString("km"),2));
                    //row.put("costo",StringHelper.roundDouble(rs.getString("costo"),2));
                    return row;
                }
            }
        );
        return hm;
    }
    
    
    
    
    //Obtiene Tipos de  Tarifas
    //Solo se obtienen los tipos de tarifa 1=por poblacion
    @Override
    public ArrayList<HashMap<String, Object>> getTarifario_Tipos(Integer id_emp) {
	String sql_query = ""
        + "select "
            + "log_tarifa_clase.titulo as tarifa_clase, "
            + "log_tarifa_tipo.id as t_tarifa_id, "
            + "log_tarifa_tipo.titulo as tarifa_tipo,"
            + "(case when log_vehiculo_tipo.titulo is null then '' else log_vehiculo_tipo.titulo end) as tipo_unidad "
        + "from log_tarifa_tipo "
        + "join log_tarifa_clase on log_tarifa_clase.id=log_tarifa_tipo.log_tarifa_clase_id "
        + "left join log_vehiculo_tipo on (log_vehiculo_tipo.id=log_tarifa_tipo.log_vehiculo_tipo_id and log_vehiculo_tipo.borrado_logico=false)"
        + "where log_tarifa_tipo.borrado_logico=false and log_tarifa_tipo.gral_emp_id=? and log_tarifa_tipo.tipo=1 ORDER BY log_tarifa_clase.titulo, log_tarifa_tipo.id, log_vehiculo_tipo.titulo;";
        
        ArrayList<HashMap<String, Object>> hm_rtipo = (ArrayList<HashMap<String, Object>>) this.jdbcTemplate.query(
            sql_query,
            new Object[]{new Integer(id_emp)}, new RowMapper() {
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    HashMap<String, Object> row = new HashMap<String, Object>();
                    row.put("tarifa_clase",rs.getString("tarifa_clase"));
                    row.put("t_tarifa_id",rs.getInt("t_tarifa_id"));
                    row.put("tarifa_tipo",rs.getString("tarifa_tipo"));
                    row.put("tipo_unidad",rs.getString("tipo_unidad"));
                    return row;
                }
            }
        );
        return hm_rtipo;
    }
    
    
    
    //metodo que obtiene datos para el grid de Ruta
    @Override
    public ArrayList<HashMap<String, Object>> getTarifarioVenta_PaginaGrid(String data_string, int offset, int pageSize, String orderBy, String asc) {
        String sql_busqueda = "select id from gral_bus_catalogos(?) as foo (id integer)";
        
	String sql_to_query = ""
        + "select "
            + "log_tarifario_venta.id,"
            + "upper(gral_mun.titulo) as poblacion, "
            + "upper(gral_edo.titulo) as entidad, "
            + "cxc_clie.razon_social as cliente "
        + "from log_tarifario_venta  "
        + "join cxc_clie on cxc_clie.id=log_tarifario_venta.cxc_clie_id "
        + "join gral_mun on gral_mun.id=log_tarifario_venta.gral_mun_id  "
        + "join gral_edo on gral_edo.id=gral_mun.estado_id  "
        + "JOIN ("+sql_busqueda+") AS sbt ON sbt.id = log_tarifario_venta.id "
        + "order by "+orderBy+" "+asc+" limit ? OFFSET ?";
        
        //System.out.println("IMPRIMIENDO EL GRID DE RUTA: "+sql_to_query);
        ArrayList<HashMap<String, Object>> hm = (ArrayList<HashMap<String, Object>>) this.jdbcTemplate.query(
            sql_to_query, 
            new Object[]{data_string, new Integer(pageSize),new Integer(offset)}, new RowMapper() {
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    HashMap<String, Object> row = new HashMap<String, Object>();
                    row.put("id",rs.getInt("id"));
                    row.put("poblacion",rs.getString("poblacion"));
                    row.put("entidad",rs.getString("entidad"));
                    row.put("cliente",rs.getString("cliente"));
                    return row;
                }
            }
        );
        return hm; 
    }
    
    
    
    //Obtiene datos del Header del registro de tarifa por cliente-ruta
    @Override
    public ArrayList<HashMap<String, Object>> getTarifarioVenta_Datos(Integer id) {
	String sql_query = ""
        + "SELECT "
            + "log_tarifario_venta.id, "
            + "log_tarifario_venta.cxc_clie_id as clie_id, "
            + "cxc_clie.numero_control as clie_no, "
            + "cxc_clie.razon_social as clie, "
            + "log_tarifario_venta.gral_mun_id as mun_id, "
            + "log_tarifario_venta.gral_edo_id as edo_id, "
            + "log_tarifario_venta.gral_pais_id as pais_id "
        + "FROM log_tarifario_venta "
        + "join cxc_clie on cxc_clie.id=log_tarifario_venta.cxc_clie_id "
        + "where log_tarifario_venta.id=?;";
        
        //System.out.println("identificador: "+id);
        //System.out.println("sql_query: "+sql_query);
        
        ArrayList<HashMap<String, Object>> hm_rtipo = (ArrayList<HashMap<String, Object>>) this.jdbcTemplate.query(
            sql_query,
            new Object[]{new Integer(id)}, new RowMapper() {
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    HashMap<String, Object> row = new HashMap<String, Object>();
                    row.put("id",rs.getInt("id"));
                    row.put("clie_id",rs.getInt("clie_id"));
                    row.put("clie_no",rs.getString("clie_no"));
                    row.put("clie",rs.getString("clie"));
                    row.put("mun_id",rs.getInt("mun_id"));
                    row.put("edo_id",rs.getInt("edo_id"));
                    row.put("pais_id",rs.getInt("pais_id"));
                    return row;
                }
            }
        );
        return hm_rtipo;
    }
    
    //Obtiene la lista de tipos de Trifas y las tarifas por cliente-ruta
    @Override
    public ArrayList<HashMap<String, Object>> getTarifarioVenta_DatosGrid(Integer id, Integer id_emp) {
	String sql_query = ""
        + "select "
            + "(case when log_tarifario_venta_det.id is null then 0 else log_tarifario_venta_det.id end) as id_det,"
            + "log_tarifa_clase.titulo as tarifa_clase, "
            + "log_tarifa_tipo.id as t_tarifa_id, "
            + "log_tarifa_tipo.titulo as tarifa_tipo, "
            + "(case when log_vehiculo_tipo.titulo is null then '' else log_vehiculo_tipo.titulo end) as tipo_unidad, "
            + "(case when log_tarifario_venta_det.valor is null then 0 else log_tarifario_venta_det.valor end) as precio,"
            + "log_tarifario_venta_det.log_vehiculo_tipo_id as tipo_unidad_id  "
        + "from log_tarifa_tipo  "
        + "left join log_tarifario_venta_det on (log_tarifario_venta_det.log_tarifa_tipo_id=log_tarifa_tipo.id and log_tarifario_venta_det.log_tarifario_venta_id=?) "
        + "join log_tarifa_clase on log_tarifa_clase.id=log_tarifa_tipo.log_tarifa_clase_id  "
        + "left join log_vehiculo_tipo on (log_vehiculo_tipo.id=log_tarifa_tipo.log_vehiculo_tipo_id and log_vehiculo_tipo.borrado_logico=false) "
        + "where log_tarifa_tipo.borrado_logico=false and log_tarifa_tipo.gral_emp_id=?  "
        + "ORDER BY log_tarifario_venta_det.id, log_tarifa_clase.titulo, log_tarifa_tipo.id, log_vehiculo_tipo.titulo;";
        
        ArrayList<HashMap<String, Object>> hm_rtipo = (ArrayList<HashMap<String, Object>>) this.jdbcTemplate.query(
            sql_query,
            new Object[]{new Integer(id), new Integer(id_emp)}, new RowMapper() {
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    HashMap<String, Object> row = new HashMap<String, Object>();
                    row.put("id_det",rs.getInt("id_det"));
                    row.put("tarifa_clase",rs.getString("tarifa_clase"));
                    row.put("t_tarifa_id",rs.getInt("t_tarifa_id"));
                    row.put("tarifa_tipo",rs.getString("tarifa_tipo"));
                    row.put("tipo_unidad",rs.getString("tipo_unidad"));
                    row.put("precio",StringHelper.roundDouble(rs.getString("precio"), 2));
                    row.put("tipo_unidad_id",rs.getInt("tipo_unidad_id"));
                    return row;
                }
            }
        );
        return hm_rtipo;
    }
    
    
    
    
    
    //Tarifario de venta POR RANGO
    @Override
    public ArrayList<HashMap<String, Object>> getTarifarioVentaPorRango_PaginaGrid(String data_string, int offset, int pageSize, String orderBy, String asc) {
        String sql_busqueda = "select id from gral_bus_catalogos(?) as foo (id integer)";
        
	String sql_to_query = ""
        + "select  "
            + "log_tarifario_venta.id,"
            + "upper(log_tarifa_clase.titulo||'-'||log_tarifa_tipo.titulo) as tipo_tarifa, "
            + "(case when log_tarifario_venta_det.fecha_inicio is null then '' else to_char(log_tarifario_venta_det.fecha_inicio::timestamp with time zone, 'dd/mm/yyyy') end)||' - '||(case when log_tarifario_venta_det.fecha_fin is null then '' else to_char(log_tarifario_venta_det.fecha_fin::timestamp with time zone, 'dd/mm/yyyy') end) AS vigencia, "
            + "cxc_clie.razon_social as cliente  "
        + "from log_tarifario_venta "
        + "join log_tarifario_venta_det on log_tarifario_venta_det.log_tarifario_venta_id=log_tarifario_venta.id  "
        + "join log_tarifa_tipo on (log_tarifa_tipo.id=log_tarifario_venta_det.log_tarifa_tipo_id  and log_tarifa_tipo.tipo=2)  "
        + "join log_tarifa_clase on log_tarifa_clase.id=log_tarifa_tipo.log_tarifa_clase_id  "
        + "join cxc_clie on cxc_clie.id=log_tarifario_venta.cxc_clie_id   "
        + "JOIN ("+sql_busqueda+") AS sbt ON sbt.id = log_tarifario_venta.id "
        + "order by "+orderBy+" "+asc+" limit ? OFFSET ?";
        
        //System.out.println("IMPRIMIENDO EL GRID DE RUTA: "+sql_to_query);
        ArrayList<HashMap<String, Object>> hm = (ArrayList<HashMap<String, Object>>) this.jdbcTemplate.query(
            sql_to_query, 
            new Object[]{data_string, new Integer(pageSize),new Integer(offset)}, new RowMapper() {
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    HashMap<String, Object> row = new HashMap<String, Object>();
                    row.put("id",rs.getInt("id"));
                    row.put("tipo_tarifa",rs.getString("tipo_tarifa"));
                    row.put("vigencia",rs.getString("vigencia"));
                    row.put("cliente",rs.getString("cliente"));
                    return row;
                }
            }
        );
        return hm; 
    }
    
    
    
    //Obtiene los tipos de tarifa
    //Solo se obtienen los tipos de tarifa 2=por Rango
    @Override
    public ArrayList<HashMap<String, Object>> getLog_TiposTarifasVentaPorRango(Integer id_emp) {
        String sql_to_query = "select log_tarifa_tipo.id, upper(log_tarifa_clase.titulo||'-'||log_tarifa_tipo.titulo) as titulo from log_tarifa_tipo join log_tarifa_clase on log_tarifa_clase.id=log_tarifa_tipo.log_tarifa_clase_id where log_tarifa_tipo.gral_emp_id=? and log_tarifa_tipo.borrado_logico=false and log_tarifa_tipo.tipo=2 order by log_tarifa_clase.titulo, log_tarifa_tipo.titulo;";
        
        ArrayList<HashMap<String, Object>> hm = (ArrayList<HashMap<String, Object>>) this.jdbcTemplate.query(
            sql_to_query,
            new Object[]{new Integer(id_emp)}, new RowMapper(){
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
    
    
    
    
    
    //Obtiene datos del Header del registro de tarifa por cliente por rango
    @Override
    public ArrayList<HashMap<String, Object>> getTarifarioVentaPorRango_Datos(Integer id) {
	String sql_query = ""
        + "SELECT "
            + "log_tarifario_venta.id, "
            + "log_tarifario_venta.cxc_clie_id as clie_id, "
            + "cxc_clie.numero_control as clie_no, "
            + "cxc_clie.razon_social as clie, "
            + "log_tarifario_venta_det.log_tarifa_tipo_id as tt_id, "
            + "log_tarifario_venta_det.fecha_inicio::character varying as fecha_inicio, "
            + "log_tarifario_venta_det.fecha_fin::character varying as fecha_fin "
        + "FROM log_tarifario_venta "
        + "join log_tarifario_venta_det on log_tarifario_venta_det.log_tarifario_venta_id=log_tarifario_venta.id  "
        + "join cxc_clie on cxc_clie.id=log_tarifario_venta.cxc_clie_id "
        + "where log_tarifario_venta.id=? limit 1;";
  
        ArrayList<HashMap<String, Object>> hm_rtipo = (ArrayList<HashMap<String, Object>>) this.jdbcTemplate.query(
            sql_query,
            new Object[]{new Integer(id)}, new RowMapper() {
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    HashMap<String, Object> row = new HashMap<String, Object>();
                    row.put("id",rs.getInt("id"));
                    row.put("clie_id",rs.getInt("clie_id"));
                    row.put("clie_no",rs.getString("clie_no"));
                    row.put("clie",rs.getString("clie"));
                    row.put("tt_id",rs.getInt("tt_id"));
                    
                    row.put("fecha_inicio",rs.getString("fecha_inicio"));
                    row.put("fecha_fin",rs.getString("fecha_fin"));
                    
                    return row;
                }
            }
        );
        return hm_rtipo;
    }
    
    //Obtiene la lista de tipos de Trifas y las tarifas por cliente-ruta
    @Override
    public ArrayList<HashMap<String, Object>> getTarifarioVentaPorRango_DatosGrid(Integer id) {
	String sql_query = "select distinct tvdet_rango.id as id_det, tvdet_rango.titulo as titulo_rango,tvdet_rango.valor1,tvdet_rango.valor2,tvdet_rango.precio "
                + "from log_tarifario_venta_det_rango as tvdet_rango "
                + "join log_tarifario_venta_det as tvdet on tvdet.id=tvdet_rango.log_tarifario_venta_det_id "
                + "where tvdet.log_tarifario_venta_id=? order by tvdet_rango.id;";
        
        //System.out.println("TarifarioVentaPorRango_DatosGrid:  "+sql_query);
        ArrayList<HashMap<String, Object>> hm_rtipo = (ArrayList<HashMap<String, Object>>) this.jdbcTemplate.query(
            sql_query,
            new Object[]{new Integer(id)}, new RowMapper() {
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    HashMap<String, Object> row = new HashMap<String, Object>();
                    row.put("id_det",rs.getInt("id_det"));
                    row.put("titulo_rango",rs.getString("titulo_rango"));
                    row.put("valor1",StringHelper.roundDouble(rs.getString("valor1"), 2));
                    row.put("valor2",StringHelper.roundDouble(rs.getString("valor2"), 2));
                    row.put("precio",StringHelper.roundDouble(rs.getString("precio"), 2));
                    return row;
                }
            }
        );
        return hm_rtipo;
    }
    
    
    
    
    //Metodo que obtiene datos para el grid del Programa de Entradas al Almacen
    @Override
    public ArrayList<HashMap<String, Object>> getLogEntradaAlmacen_PaginaGrid(String data_string, int offset, int pageSize, String orderBy, String asc) {
        String sql_busqueda = "select id from gral_bus_catalogos(?) as foo (id integer)";
        
	String sql_to_query = ""
        + "SELECT "
            + "log_entrada.id, "
            + "log_entrada.folio, "
            + "to_char(log_entrada.fecha::timestamp with time zone, 'dd/mm/yyyy') as fecha,  "
            + "cxc_clie.razon_social as cliente, "
            + "inv_alm.titulo as almacen "
        + "FROM log_entrada "
        + "JOIN cxc_clie ON cxc_clie.id=log_entrada.cxc_clie_id "
        + "JOIN inv_alm ON inv_alm.id=log_entrada.inv_alm_id " 
        +"JOIN ("+sql_busqueda+") AS sbt on sbt.id=log_entrada.id "
        +"order by "+orderBy+" "+asc+" limit ? OFFSET ?";
        
        //System.out.println("Paginado Viajes: "+sql_to_query);
        ArrayList<HashMap<String, Object>> hm = (ArrayList<HashMap<String, Object>>) this.jdbcTemplate.query(
            sql_to_query, 
            new Object[]{data_string, new Integer(pageSize),new Integer(offset)}, new RowMapper() {
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    HashMap<String, Object> row = new HashMap<String, Object>();
                    row.put("id",rs.getInt("id"));
                    row.put("folio",rs.getString("folio"));
                    row.put("fecha",rs.getString("fecha"));
                    row.put("cliente",rs.getString("cliente"));
                    row.put("almacen",rs.getString("almacen"));
                    return row;
                }
            }
        );
        return hm; 
    }
    
    
    
    //Obtiene datos de la Entrada al Almacen
    @Override
    public ArrayList<HashMap<String, Object>> getLogEntradaAlmacen_Datos(Integer id) {
        String sql_to_query = ""
        + "select "
            + "log_entrada.id,"
            + "log_entrada.folio, "
            + "log_entrada.observaciones, "
            + "to_char(log_entrada.fecha::timestamp with time zone, 'yyyy-mm-dd') as fecha, "
            + "log_entrada.inv_alm_id as alm_id, "
            + "cxc_clie.id as clie_id, "
            + "cxc_clie.numero_control as no_clie, "
            + "cxc_clie.razon_social as nombre_clie,"
            + "log_entrada.gral_suc_id as suc_id,"
            + "log_entrada.log_tipo_distribucion_id as tipo_viaje_id, "
            + "log_entrada.log_vehiculo_id as vehiculo_id, "
            + "log_entrada.log_vehiculo_tipo_id as vehiculo_tipo_id, "
            + "(case when log_vehiculos.id is null then '' else log_vehiculos.folio end) as no_unidad,"
            + "(case when log_vehiculo_clase.id is null then '' else log_vehiculo_clase.titulo end) as clase,"
            + "(case when log_vehiculo_marca.id is null then '' else log_vehiculo_marca.titulo end) as marca,"
            + "log_entrada.no_economico, "
            + "log_entrada.placas, "
            + "log_entrada.log_ruta_id as ruta_id, "
            + "log_entrada.costo_ruta, "
            + "(case when log_ruta.id is null then '' else log_ruta.folio end) as no_ruta,"
            + "(case when log_ruta.id is null then '' else log_ruta.titulo end) as titulo_ruta,"
            + "(case when log_ruta.id is null then 0 else log_ruta.km end) as km_ruta,"
            + "log_entrada.tipo_costeo "
        + "from log_entrada "
        + "join cxc_clie on cxc_clie.id=log_entrada.cxc_clie_id  "
        + "left join log_vehiculos on log_vehiculos.id=log_entrada.log_vehiculo_id  "
        + "left join log_vehiculo_clase on log_vehiculo_clase.id=log_entrada.log_vehiculo_clase_id  "
        + "left join log_vehiculo_marca on log_vehiculo_marca.id=log_entrada.log_vehiculo_marca_id  "
        + "left join log_ruta on log_ruta.id=log_entrada.log_ruta_id  "
        + "where log_entrada.id=?";
        
        //System.out.println("id="+id);
        //System.out.println("sql_to_query="+sql_to_query);
        ArrayList<HashMap<String, Object>> hm = (ArrayList<HashMap<String, Object>>) this.jdbcTemplate.query(
            sql_to_query,
            new Object[]{ new Integer(id)}, new RowMapper(){
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    HashMap<String, Object> row = new HashMap<String, Object>();
                    row.put("id",rs.getInt("id"));
                    row.put("folio",rs.getString("folio"));
                    row.put("observaciones",rs.getString("observaciones"));
                    row.put("fecha",rs.getString("fecha"));
                    row.put("alm_id",rs.getInt("alm_id"));
                    row.put("clie_id",rs.getInt("clie_id"));
                    row.put("no_clie",rs.getString("no_clie"));
                    row.put("nombre_clie",rs.getString("nombre_clie"));
                    row.put("suc_id",rs.getInt("suc_id"));
                    row.put("tipo_viaje_id",rs.getInt("tipo_viaje_id"));
                    row.put("vehiculo_id",rs.getInt("vehiculo_id"));
                    row.put("vehiculo_tipo_id",rs.getInt("vehiculo_tipo_id"));
                    row.put("no_unidad",rs.getString("no_unidad"));
                    row.put("clase",rs.getString("clase"));
                    row.put("marca",rs.getString("marca"));
                    row.put("no_economico",rs.getString("no_economico"));
                    row.put("placas",rs.getString("placas"));
                    row.put("ruta_id",rs.getInt("ruta_id"));
                    row.put("costo_ruta",StringHelper.roundDouble(rs.getString("costo_ruta"),2));
                    row.put("no_ruta",rs.getString("no_ruta"));
                    row.put("titulo_ruta",rs.getString("titulo_ruta"));
                    row.put("km_ruta",rs.getString("km_ruta"));
                    row.put("tipo_costeo",rs.getInt("tipo_costeo"));
          
                    return row;
                }
            }
        );
        return hm;
    }
    
    //Obtener el detalle de la entrada
    @Override
    public ArrayList<HashMap<String, Object>> getLogEntradaAlmacen_Detalle(Integer id) {
	String sql_query = ""
        + "select "
            + "log_entrada_det.id,"
            + "inv_prod.sku as codigo,"
            + "inv_prod.descripcion,"
            + "(case when inv_prod_unidades.id is null then '' else inv_prod_unidades.titulo end) as unidad,"
            + "cantidad,"
            + "peso,"
            + "volumen "
        + "from log_entrada_det  "
        + "join inv_prod on inv_prod.id=log_entrada_det.inv_prod_id "
        + "left join inv_prod_unidades on inv_prod_unidades.id=log_entrada_det.inv_prod_unidad_id  "
        + "where log_entrada_det.log_entrada_id=?;";
        
        //System.out.println("identificador: "+id);
        //System.out.println("sql_query: "+sql_query);
        
        ArrayList<HashMap<String, Object>> hm_rtipo = (ArrayList<HashMap<String, Object>>) this.jdbcTemplate.query(
            sql_query,
            new Object[]{new Integer(id)}, new RowMapper() {
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    HashMap<String, Object> row = new HashMap<String, Object>();
                    row.put("id",rs.getInt("id"));
                    row.put("codigo",rs.getString("codigo"));
                    row.put("descripcion",rs.getString("descripcion"));
                    row.put("unidad",rs.getString("unidad"));
                    row.put("cantidad",StringHelper.roundDouble(rs.getString("cantidad"),2));
                    row.put("peso",StringHelper.roundDouble(rs.getString("peso"),2));
                    row.put("volumen",StringHelper.roundDouble(rs.getString("volumen"),2));
                    
                    return row;
                }
            }
        );
        return hm_rtipo;
    }
    
    
    
    //Obtiene datos de la Entrada al Almacen
    @Override
    public HashMap<String, Object> getLogEntradaAlmacen_DatosPdf(Integer id) {
        String sql_to_query = ""
        + "select "
            + "log_entrada.folio, "
            + "log_entrada.observaciones, "
            + "to_char(log_entrada.fecha::timestamp with time zone, 'dd/mm/yyyy') as fecha, "
            + "gral_suc.titulo as sucursal,"
            + "inv_alm.titulo as almacen,"
            + "cxc_clie.numero_control as no_clie, "
            + "cxc_clie.razon_social as nombre_clie,"
            + "log_tipo_distribucion.titulo as tipo_viaje, "
            + "log_vehiculo_tipo.titulo as tipo_unidad, "
            + "(case when log_vehiculos.id is null then '' else log_vehiculos.folio end) as no_unidad,"
            + "(case when log_vehiculo_clase.id is null then '' else log_vehiculo_clase.titulo end) as clase,"
                + "(case when log_vehiculo_tipo_rodada.id is null then '' else log_vehiculo_tipo_rodada.titulo end) as tipo_rodada,"
            + "(case when log_vehiculo_marca.id is null then '' else log_vehiculo_marca.titulo end) as marca,"
            + "log_entrada.no_economico, "
            + "log_entrada.placas, "
            + "log_entrada.costo_ruta, "
            + "(case when log_ruta.id is null then '' else log_ruta.folio end) as no_ruta,"
            + "(case when log_ruta.id is null then '' else log_ruta.titulo end) as titulo_ruta,"
            + "(case when log_ruta.id is null then 0 else log_ruta.km end) as km_ruta,"
            + "(case when cxp_prov.id is null then '' else cxp_prov.razon_social end) as transportista,"
            + "(case when log_choferes.id is null then '' else log_choferes.nombre||' '||log_choferes.apellido_paterno||' '||log_choferes.apellido_materno end) as chofer "
        + "from log_entrada "
        + "join cxc_clie on cxc_clie.id=log_entrada.cxc_clie_id "
        + "join gral_suc on gral_suc.id=log_entrada.gral_suc_id "
        + "join inv_alm on inv_alm.id=log_entrada.inv_alm_id "
        + "join log_tipo_distribucion on log_tipo_distribucion.id=log_entrada.log_tipo_distribucion_id "
        + "join log_vehiculo_tipo on log_vehiculo_tipo.id=log_entrada.log_vehiculo_tipo_id "
        + "left join log_vehiculos on log_vehiculos.id=log_entrada.log_vehiculo_id  "
        + "left join log_vehiculo_clase on log_vehiculo_clase.id=log_entrada.log_vehiculo_clase_id "
        + "left join log_vehiculo_marca on log_vehiculo_marca.id=log_entrada.log_vehiculo_marca_id "
        + "left join log_vehiculo_tipo_rodada on log_vehiculo_tipo_rodada.id=log_vehiculos.log_vehiculo_tipo_rodada_id "
        + "left join log_ruta on log_ruta.id=log_entrada.log_ruta_id  "
        + "left join cxp_prov on cxp_prov.id=log_vehiculos.cxp_prov_id  "
        + "left join log_choferes on log_choferes.id=log_vehiculos.log_chofer_id  "
        + "where log_entrada.id=?";
                
        //System.out.println("id="+id);
        //System.out.println("sql_to_query="+sql_to_query);
        
        HashMap<String, Object> hm_return = (HashMap<String, Object>) this.jdbcTemplate.queryForObject(
            sql_to_query, 
            new Object[]{new Integer(id)}, new RowMapper() {
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    HashMap<String, Object> row = new HashMap<String, Object>();
                    row.put("folio",rs.getString("folio"));
                    row.put("observaciones",rs.getString("observaciones"));
                    row.put("fecha",rs.getString("fecha"));
                    row.put("sucursal",rs.getString("sucursal"));
                    row.put("almacen",rs.getString("almacen"));
                    row.put("no_clie",rs.getString("no_clie"));
                    row.put("nombre_clie",rs.getString("nombre_clie"));
                    row.put("tipo_viaje",rs.getString("tipo_viaje"));
                    row.put("tipo_unidad",rs.getString("tipo_unidad"));
                    row.put("no_unidad",rs.getString("no_unidad"));
                    row.put("clase",rs.getString("clase"));
                    row.put("tipo_rodada",rs.getString("tipo_rodada"));
                    row.put("marca",rs.getString("marca"));
                    row.put("no_economico",rs.getString("no_economico"));
                    row.put("placas",rs.getString("placas"));
                    row.put("costo_ruta",StringHelper.roundDouble(rs.getString("costo_ruta"),2));
                    row.put("no_ruta",rs.getString("no_ruta"));
                    row.put("titulo_ruta",rs.getString("titulo_ruta"));
                    row.put("km_ruta",rs.getString("km_ruta"));
                    
                    row.put("transportista",rs.getString("transportista"));
                    row.put("chofer",rs.getString("chofer"));
          
                    return row;
                }
            }
        );
        
        return hm_return;
    }
    
    
    
}
