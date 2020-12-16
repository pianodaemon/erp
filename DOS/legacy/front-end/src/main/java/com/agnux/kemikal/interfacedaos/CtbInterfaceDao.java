package com.agnux.kemikal.interfacedaos;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;


public interface CtbInterfaceDao {
    public HashMap<String, String> selectFunctionValidateAaplicativo(String data, Integer idApp, String extra_data_array);
    public String selectFunctionForThisApp(String campos_data, String extra_data_array);
    public String selectFunctionForCtbAdmProcesos(String campos_data, String extra_data_array);
    public int countAll(String data_string);
    
    public ArrayList<HashMap<String, Object>> getCentroCostos_PaginaGrid(String data_string, int offset, int pageSize, String orderBy , String asc);
    public ArrayList<HashMap<String, String>> getCentroCosto_Datos(Integer id);
    
    
    public ArrayList<HashMap<String, Object>> getTipoPolizas_PaginaGrid(String data_string, int offset, int pageSize, String orderBy , String asc);
    public ArrayList<HashMap<String, String>> getTipoPoliza_Datos(Integer id);
    public ArrayList<HashMap<String, String>> getTipoPoliza_Grupos();
    
    public ArrayList<HashMap<String, Object>> getConceptosContables_PaginaGrid(String data_string, int offset, int pageSize, String orderBy , String asc);
    public ArrayList<HashMap<String, String>> getConceptoContable_Datos(Integer id);
    
    public ArrayList<HashMap<String, Object>> getCuentasMayor_PaginaGrid(String data_string, int offset, int pageSize, String orderBy , String asc);
    public ArrayList<HashMap<String, String>> getCuentasMayor_Clases();
    public ArrayList<HashMap<String, String>> getCuentaDeMayor_Datos(Integer id);
    
    
    //metodos para catalogo de cuentas contables 
    public ArrayList<HashMap<String, Object>> getCuentasContables_PaginaGrid(String data_string, int offset, int pageSize, String orderBy, String asc);
    public ArrayList<HashMap<String, Object>> getCuentasContables_Datos(Integer id);
    public ArrayList<HashMap<String, Object>> getCuentasContables_CuentasMayor(Integer id_empresa);
    
    public ArrayList<HashMap<String, Object>> getCtb_Aplicaciones(Integer tipo);
    
    //Medotdos para reporte de Auxiliar de Cuentas
    public ArrayList<HashMap<String, Object>> getCtbRepAuxCtas_Anios();
    public ArrayList<HashMap<String, Object>> getCtbRepAuxCtas_Ctas(Integer nivel, String cta, String scta, String sscta, String ssscta, Integer id_empresa);
    public ArrayList<HashMap<String, String>> getCtbRepAuxCtas_Datos(String data_string);
    
    //Obtiene las sucursales de la empresa
    public ArrayList<HashMap<String, Object>> getCtb_Sucursales(Integer idEmp);
    
    
    
    
    //Métodos para reporte de Auxiliar de Movimientos de Cuentas
    public ArrayList<HashMap<String, Object>> getCtbRepAuxMovCtas_Anios();
    public ArrayList<HashMap<String, Object>> getCtbRepAuxMovCtas_Ctas(Integer nivel, String cta, String scta, String sscta, String ssscta, Integer id_empresa);
    public ArrayList<HashMap<String, String>> getCtbRepAuxMovCtas_Datos(String data_string);
    
    //Métodos para reporte de Balance General
    public ArrayList<HashMap<String, Object>> getCtbRepBalanceGral_Anios();
    public ArrayList<HashMap<String, Object>> getCtbRepBalanceGral_Ctas(Integer nivel, String cta, String scta, String sscta, String ssscta, Integer id_empresa);
    public ArrayList<HashMap<String, String>> getCtbRepBalanceGral_Datos(String data_string);
    
    
    //Metodos para reporte de Balanza de Comprobación
    public ArrayList<HashMap<String, Object>> getCtbRepBalanzaComp_Anios();
    public ArrayList<HashMap<String, Object>> getCtbRepBalanzaComp_Ctas(Integer nivel, String cta, String scta, String sscta, String ssscta, Integer id_empresa);
    public ArrayList<LinkedHashMap<String, String>> getCtbRepBalanzaComp_Datos(String data_string);
    
    //Metodos para reporte de Estado de Resultados
    public ArrayList<HashMap<String, Object>> getCtbRepEstadoResult_Anios();
    public ArrayList<HashMap<String, Object>> getCtbRepEstadoResult_Ctas(Integer nivel, String cta, String scta, String sscta, String ssscta, Integer id_empresa);
    public ArrayList<HashMap<String, String>> getCtbRepEstadoResult_Datos(String data_string);
    
    //Metodos para reporte de Polizas Contables
    public ArrayList<HashMap<String, Object>> getCtbRepPolizasCont_Anios();
    public ArrayList<HashMap<String, Object>> getCtbRepPolizasCont_Ctas(Integer nivel, String cta, String scta, String sscta, String ssscta, Integer id_empresa);
    public ArrayList<HashMap<String, String>> getCtbRepPolizasCont_Datos(String data_string);
    
     //Metodos para reporte de Estado de Resultados Anual
    public ArrayList<HashMap<String, Object>> getCtbRepEstadoResultAnual_Anios();
    public ArrayList<HashMap<String, Object>> getCtbRepEstadoResultAnual_Ctas(Integer nivel, String cta, String scta, String sscta, String ssscta, Integer id_empresa);
    public ArrayList<HashMap<String, String>> getCtbRepEstadoResultAnual_Datos(String data_string);
    
    //Metodos para Reporte de Libro Mayor
    public ArrayList<HashMap<String, Object>> getCtbRepLibroMayor_Anios();
    public ArrayList<HashMap<String, Object>> getCtbRepLibroMayor_Ctas(Integer nivel, String cta, String scta, String sscta, String ssscta, Integer id_empresa);
    public ArrayList<HashMap<String, String>> getCtbRepLibroMayor_Datos(String data_string);
    
    
    
    //Metodos para Aplicativo de Polizas Contables
    public ArrayList<HashMap<String, Object>> getPolizasContables_PaginaGrid(String data_string, int offset, int pageSize, String orderBy , String asc);
    public ArrayList<HashMap<String, Object>> getPolizasContables_Datos(Integer id);
    public ArrayList<HashMap<String, Object>> getPolizasContables_DatosGrid(Integer poliza_id);
    public ArrayList<HashMap<String, Object>> getMonedas();
    public ArrayList<HashMap<String, Object>> getPolizasContables_TiposPolizas(Integer id_empresa);
    public ArrayList<HashMap<String, Object>> getPolizasContables_Conceptos(Integer id_empresa);
    public ArrayList<HashMap<String, Object>> getPolizasContables_CentrosCostos(Integer id_empresa, Integer id_sucursal);
    public ArrayList<HashMap<String, Object>> getPolizasContables_CuentasMayor(Integer id_empresa);
    public ArrayList<HashMap<String, String>> getPolizasContables_CuentasContables(Integer clase_cta_mayor, Integer clasificacion, Integer detalle, String clasifica, String cta, String scta, String sscta, String ssscta, String sssscta, String descripcion, Integer id_empresa);
    //Estos años se utilizan para crear y modificar polizas
    public ArrayList<HashMap<String, Object>> getPolizasContables_Anios(Integer id_empresa);
    //Estos años solo se utilizan para consulta de polizas
    public ArrayList<HashMap<String, Object>> getPolizasContables_Anios2();
    public Integer getUserRolAdmin(Integer id_user);
    public ArrayList<HashMap<String, Object>> getPolizasContables_TiposDeMovimiento(Integer id_empresa);
    
    public ArrayList<HashMap<String, Object>> getDatosCuentaContable(Integer detalle, String cta, String scta, String sscta, String ssscta, String sssscta, Integer id_empresa, Integer id_sucursal);
    
    
    //Metodos para Configuracion de Parametros de Contabilidad
    public ArrayList<HashMap<String, Object>> getCtbPar_PaginaGrid(String data_string,int offset, int pageSize, String orderBy , String asc);
    public ArrayList<HashMap<String, Object>> getCtbPar_Datos(Integer id_factura);
    public ArrayList<HashMap<String, Object>> getCtbPar_Almacenes(Integer id_emp, Integer id_suc);
    
    //Obtiene  parametros de contabilidad
    public HashMap<String, Object> getCtb_Parametros(Integer id_emp, Integer id_suc);
    
    //Metodos para el Aplicativo de definicion de asientos contables.
    public ArrayList<HashMap<String, Object>> getCtbDefinicionAsientos_PaginaGrid(String data_string, int offset, int pageSize, String orderBy, String asc);
    public ArrayList<HashMap<String, Object>> getCtbDefinicionAsientos_Datos(Integer id);
    public ArrayList<HashMap<String, Object>> getCtbDefinicionAsientos_DatosGrid(Integer id);
    public ArrayList<Object> getCtbDefinicionAsientos_Campos();
    
    //Metodo para busqueda de movimientos para la Generacion de Polizas
    public ArrayList<HashMap<String, Object>> getCtbGeneraPolizas_busquedaDatos(String data_string);
    
    //Buscador de cuentas agrupadoras del sat
    public ArrayList<HashMap<String, Object>>  getBuscadorCuentasAgrupadorasSat(String codigo, String descripcion);
    public ArrayList<HashMap<String, Object>>  getDataCtaSat(String codigo);
    
    //Obtiene los datos de las cuentas contables para el xml
    public ArrayList<LinkedHashMap<String,String>> getCtbXml_CuentasContables(String data_string);
}
