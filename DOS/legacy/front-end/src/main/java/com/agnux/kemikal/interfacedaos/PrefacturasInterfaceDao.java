/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.agnux.kemikal.interfacedaos;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author pianodaemon
 */


public interface PrefacturasInterfaceDao {
    //public int selectFunctionForThisApp(Integer id_prefactura, String data, String accion,String string_array);
    //public HashMap<String, String> selectFunctionValidateAaplicativo(String data, Integer idApp, String string_array);
    //public ArrayList<HashMap<String, Object>> getPage(String factura,String cliente,String fecha_inicial,String fecha_final,int offset, int pageSize, String orderBy , String asc);
    //public int countAll(String factura,String cliente,String fecha_inicial,String fecha_final);
    
    public HashMap<String, String> selectFunctionValidateAaplicativo(String data, Integer idApp, String extra_data_array);
    public String selectFunctionForThisApp(String campos_data, String extra_data_array);
    public int countAll(String data_string);
    //public ArrayList<HashMap<String, String>> getBuscadorProductos(String sku, String titulo, String descripcion,Integer id_usuario);
    public ArrayList<HashMap<String, Object>> getAlmacenes(Integer id_empresa);
    public ArrayList<HashMap<String, Object>> getFac_Parametros(Integer idSuc);
    
    
    public ArrayList<HashMap<String, Object>> getPrefacturas__PaginaGrid(String data_string,int offset, int pageSize, String orderBy , String asc);
    public ArrayList<HashMap<String, Object>> getPrefactura_Datos(Integer id_prefactura);
    public ArrayList<HashMap<String, Object>> getPrefactura_DatosGrid(Integer id_prefactura);
    public ArrayList<HashMap<String, Object>> getPrefactura_DatosAdenda(Integer id_prefactura);
    public ArrayList<HashMap<String, Object>> getPrefactura_PresPorProd(Integer id_prefactura);
    public ArrayList<HashMap<String, Object>> getMonedas();
    public ArrayList<HashMap<String, Object>> getDatosRemision(Integer id_remision);
    public ArrayList<HashMap<String, Object>> getDetallesRemision(Integer id_remision, String permitir_descuento);
    public ArrayList<HashMap<String, String>> getRemisionesCliente(Integer id_cliente);
    public ArrayList<HashMap<String, Object>> getPresPorProdRemision(Integer id_remision);
    
    public ArrayList<HashMap<String, Object>> getVendedores(Integer id_empresa, Integer id_sucursal);
    public ArrayList<HashMap<String, Object>> getCondiciones();
    public ArrayList<HashMap<String, Object>> getMetodosPago(Integer empresaId);
    public ArrayList<HashMap<String, Integer>> getAnioInforme();
    public ArrayList<HashMap<String, Object>> get_buscador_clientes(String cadena, Integer filtro, Integer id_empresa, Integer id_sucursal);
    public ArrayList<HashMap<String, Object>> getDatosClienteByNoCliente(String no_control,  Integer id_empresa, Integer id_sucursal);
    public ArrayList<HashMap<String, String>> getProductoTipos();
    public ArrayList<HashMap<String, String>> getBuscadorProductos(String sku, String tipo, String descripcion, Integer id_empresa);
    public ArrayList<HashMap<String, Object>> get_presentaciones_producto(String sku, Integer id_empresa);
    public HashMap<String, Object> getPrecioUnitario(Integer id_cliente, Integer id_producto, Integer id_pres,Integer id_empresa, Integer id_sucursal);
    
}