package com.agnux.kemikal.interfacedaos;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;


public interface CotizacionesInterfaceDao {
    
    public ArrayList<HashMap<String, Object>> getCotizacion(Integer id_cotizacion);
    public ArrayList<HashMap<String, Object>> getDatosGrid(Integer id_cotizacion);
    public ArrayList<HashMap<String, Object>> getValoriva();
    public ArrayList<HashMap<String, Object>> get_buscador_clientes(String cadena, Integer filtro, Integer id_empresa, Integer id_sucursal);
    public ArrayList<HashMap<String, String>> getProductoTipos();
    public ArrayList<HashMap<String, String>> getBuscadorProductos(String sku, String tipo, String descripcion,Integer id_empresa);
    public ArrayList<HashMap<String, Object>> get_presentaciones_producto(String sku);
    public ArrayList<HashMap<String, Object>> getMonedas();
    public void getDatosEmpresaPdf(HashMap<String, String> datosEmpresa);
    public void getDatosCotizacionPdf(Integer id_cotizacion);
    public int selectFunctionForThisApp(Integer id_cotizacion, String data, String accion,String string_array);
    public HashMap<String, String> selectFunctionValidateAaplicativo(String data, Integer idApp, String string_array);
    public ArrayList<HashMap<String, Object>> getPage(String folio,String cliente,String fecha_inicial,String fecha_final,int offset, int pageSize, String orderBy , String asc);
    public int countAll(String folio,String cliente,String fecha_inicial,String fecha_final);
    
    public String getFolio();
    
    public String getEmp_Municipio();

    public String getEmp_Entidad();
    
    public String getFecha_cotizacion();
    
    public String getEmp_Calle();
    
    public String getEmp_Numero();
    
    public String getEmp_Colonia();
    
    public String getEmp_Pais();
    
    public String getEmp_Cp();
    
    public String getEmp_Rfc();
    
    public String getClient_razon_social();
    
    public String getClient_calle();
    
    public String getClient_numero();
    
    public String getClient_colonia();
    
    public String getClient_localidad();
    
    public String getClient_entidad();
    
    public String getClient_pais();
    
    public String getClient_cp();
    
    public String getClient_telefono();
    
    public String getClient_contacto();
    
    public String getClient_rfc();
    
    public ArrayList<LinkedHashMap<String, String>> getListaConceptos();
    
    public String getSubtotal();
    
    public String getImpuesto();
    
    public String getTotal();
    
    public String getMoneda_id();
    
    public String getMoneda();

    public String getObservaciones();

    public String getEmp_RazonSocial();
    
    public void getDatosCotizacionDescripcionPdf(Integer id_cotizacion);
}
