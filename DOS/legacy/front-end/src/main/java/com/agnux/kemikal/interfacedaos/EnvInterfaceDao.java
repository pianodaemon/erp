package com.agnux.kemikal.interfacedaos;

import java.util.ArrayList;
import java.util.HashMap;


public interface EnvInterfaceDao {
    //metodos  de uso general
    public int countAll(String data_string);
    public HashMap<String, String> selectFunctionValidateAplicativo(String data, String extra_data_array);
    public String selectFunctionForThisApp(String campos_data, String extra_data_array);
    //public String selectFunctionForEnvAdmProcesos(String campos_data, String extra_data_array);
    
    public ArrayList<HashMap<String, String>> getBuscadorProductos(String sku, String tipo, String descripcion,Integer id_empresa);
    public ArrayList<HashMap<String, String>> getDataProductBySku(String codigo, Integer id_empresa);
    public ArrayList<HashMap<String, String>> getProductoTipos();
    public ArrayList<HashMap<String, String>> getProductoPresentaciones(Integer id_producto, Integer id_empresa);
    public ArrayList<HashMap<String, String>> getProductoPresentacionesON(Integer id_producto);
    public ArrayList<HashMap<String, String>> getEstatus();
    public ArrayList<HashMap<String, String>> getAlmacenes(Integer id_empresa, Integer id_sucursal);
    public ArrayList<HashMap<String, String>> getEnvasesPorProducto(Integer idProd);
    
    
    //metodos para Configuracion de Envasado
    public ArrayList<HashMap<String, Object>> getEnvConf_PaginaGrid(String data_string,int offset, int pageSize, String orderBy , String asc);
    public ArrayList<HashMap<String, String>> getEnvConf_Datos(Integer id);
    public ArrayList<HashMap<String, String>> getEnvConf_DatosGrid(Integer id);
    
    
    //metodos para Aplicativo de Re-Envasado
    public ArrayList<HashMap<String, Object>> getReEenv_PaginaGrid(String data_string,int offset, int pageSize, String orderBy , String asc);
    public ArrayList<HashMap<String, String>> getReEenv_Datos(Integer id);
    public ArrayList<HashMap<String, String>> getReEenv_DatosGrid(Integer id, Integer noDec);
    public ArrayList<HashMap<String, String>> getReEenv_Empleados(Integer id_empresa);
    public ArrayList<HashMap<String, String>> getReEenv_Existencias(Integer id_prod, Integer id_pres, Integer id_alm);
    public HashMap<String, String> getReport_Reenvasado_Header(Integer id_empresa,Integer id_env);
    public ArrayList<HashMap<String, String>> getReport_Reenvasado_grid(Integer id_empresa,Integer id_env);

    //metodos para Proceso de envasado
    public ArrayList<HashMap<String, Object>> getEnvProceso_PaginaGrid(String data_string,int offset, int pageSize, String orderBy , String asc);
    public ArrayList<HashMap<String, String>> getEnvProceso_Datos(Integer id_producto);
    public ArrayList<HashMap<String, String>> getEnvProcesoDetalle_Datos(Integer id_producto);
    public ArrayList<HashMap<String, String>> getBuscadorOrdenProduccion(String folio, String sku, String descripcion, Integer id_empresa);
    public ArrayList<HashMap<String, String>> getProOrdenOperariosDisponibles(String cadena, Integer id_empresa);
    public ArrayList<HashMap<String, String>> getProOrdenEquipoDisponible(String cadena, Integer id_empresa);
    public ArrayList<HashMap<String, String>> getEnv_ExistenciasConf(Integer id_prod, Integer id_pres, Integer id_alm);
    
    
    public HashMap<String, String> getReport_Envasado_Header(Integer empresa_id,Integer id_env);
    public ArrayList<HashMap<String, String>> getReport_Envasado_grid(Integer empresa_id,Integer id_env);
}
