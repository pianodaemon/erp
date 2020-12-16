package com.agnux.kemikal.interfacedaos;


import java.util.ArrayList;
import java.util.HashMap;


public interface ComInterfaceDao {
 public HashMap<String, String> selectFunctionValidateAaplicativo(String data, Integer idApp, String extra_data_array);
    public String selectFunctionForThisApp(String campos_data, String extra_data_array);
    public int countAll(String data_string);
    
    public ArrayList<HashMap<String, String>> getValoriva(Integer id_sucursal);
    public Double getTipoCambioActual();
    public ArrayList<HashMap<String, String>> getTipoCambioPorMoneda(Integer id_moneda, String fecha);
    public ArrayList<HashMap<String, String>> getImpuestos();
    public ArrayList<HashMap<String, String>> getMonedas();
    public ArrayList<HashMap<String, String>> getAlmacenes(Integer id_empresa);
    public HashMap<String, String> getCom_Parametros(Integer id_emp, Integer id_suc);
    
    
    public ArrayList<HashMap<String, Object>> getComOrdenCompra_PaginaGrid(String data_string,int offset, int pageSize, String orderBy , String asc);
    public ArrayList<HashMap<String, String>> getComOrdenCompra_Datos(Integer id_orden_compra);
    public ArrayList<HashMap<String, String>> getComOrdenCompra_DatosGrid(Integer id_pedido);
    public ArrayList<HashMap<String, String>> getComOrdenCompra_DatosRequisicion(String folio_req, Integer id_empresa);
    public ArrayList<HashMap<String, String>> getComOrdenCompra_DetallesRequisicion(Integer id_requisicion);
    public ArrayList<HashMap<String, String>> getCondicionesDePago();
    public ArrayList<HashMap<String, String>> getProductoTipos();
    public ArrayList<HashMap<String, String>> getBuscadorProductos(String sku, String tipo, String descripcion, Integer id_empresa);
    public ArrayList<HashMap<String, String>> getPresentacionesProducto(String sku, Integer id_empresa);
    public ArrayList<HashMap<String, String>> getBuscadorProveedores(String rfc, String email, String razon_social, Integer id_empresa);
    public ArrayList<HashMap<String, String>> getDatosProveedorByNoProv(String numeroProveedor, Integer id_empresa);
    public ArrayList<HashMap<String, String>> getViaEnvarque();
    
    
    //Esto es para los datos del pdf de orden de compra
    public HashMap<String, String> getDatosPDFOrdenCompra(Integer id_ordenCompra);
    //Esto es para cargar el detalle de la compra en el PDF
    public ArrayList<HashMap<String, String>> getconceptosOrdenCompra(Integer id_ordenCompra);

    //METODOS PARA DEVOLUCIONES DE MERCACIA A PROVEEDORES
    //metodos para Notas de Credito Proveedores
    public ArrayList<HashMap<String, String>> getComFacDevolucion_Facturas(Integer id_proveedor);
    public ArrayList<HashMap<String, String>> getComFacDevolucion_Partidas(Integer id_proveedor, String factura);
    public ArrayList<HashMap<String, String>> getComFacDevolucion_Impuesto(Integer id_proveedor);
    public ArrayList<HashMap<String, Object>> getComFacDevolucion_PaginaGrid(String data_string, int offset, int pageSize, String orderBy , String asc);
    public ArrayList<HashMap<String, String>> getComFacDevolucion_Datos(Integer id);
    public ArrayList<HashMap<String, String>> getComFacDevolucion_DatosGrid(Integer id_nota_credito);
    public ArrayList<HashMap<String, String>> getComFacDevolucion_TipoMovimientoSalida(Integer id_empresa);
    public HashMap<String, String> getComFacDevolucion_DatosPDF(Integer id);
 
    //metodos para La REQUISISION 
    public ArrayList<HashMap<String, Object>> getCom_requisicion_PaginaGrid(String data_string,int offset, int pageSize, String orderBy , String asc);
    public ArrayList<HashMap<String, String>> getCom_requisicion_Datos(Integer id_requisicion);
    public ArrayList<HashMap<String, String>> getCom_requisicion_DatosGrid(Integer id_requisicion);
    public ArrayList<HashMap<String, Object>> getEmpleados(Integer id_empresa);
    public ArrayList<HashMap<String, Object>> getDepartamentos(Integer id_empresa);
    
    
    // Metodos   requisiciones a orden de compra.
    public ArrayList<HashMap<String, Object>> getCom_oc_req_PaginaGrid(String data_string,int offset, int pageSize, String orderBy , String asc);
    public ArrayList<HashMap<String, String>> getCom_oc_req_Datos(Integer id_requisicion);
    public ArrayList<HashMap<String, String>> getCom_oc_req_DatosGrid(Integer id_requisicion);
    public ArrayList<HashMap<String, String>> getCom_oc_req_Grid();
    
    //Metodos para el reporte de estadistico de compras
    public ArrayList<HashMap<String, String>> getEstadisticoCompras(Integer tipo_reporte, String proveedor, String producto, String fecha_inicial, String fecha_final, Integer id_empresa);

    public HashMap<String, String> getCom_Requisicion_DatosPDF(Integer id_requisicion);
    
    //Reporte de Dias de entrega OC
    public ArrayList<HashMap<String, String>> getOC_DiasEntrega(Integer tipo_reporte,String proveedor, String f_inicial,String f_final,Integer id_empresa);
    
    //Metodo para el reporte de BackOrder
    public ArrayList<HashMap<String, String>> getCom_DatosRepBackOrder(Integer tipo, String oc, String codigo_producto, String descripcion, String proveedor, String finicial, String ffinal, Integer id_empresa);
}
