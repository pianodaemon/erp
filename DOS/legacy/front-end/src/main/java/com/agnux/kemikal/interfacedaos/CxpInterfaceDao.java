package com.agnux.kemikal.interfacedaos;

import java.util.ArrayList;
import java.util.HashMap;


public interface CxpInterfaceDao {
    public int countAll(String data_string);
    public HashMap<String, String> selectFunctionValidateAaplicativo(String data, Integer idApp, String extra_data_array);
    public String selectFunctionForThisApp(String campos_data, String extra_data_array);
    public String selectFunctionForCxpAdmProcesos(String campos_data, String extra_data_array);
    public ArrayList<HashMap<String, String>> getBuscadorProveedores(String rfc, String email, String razon_social, Integer id_empresa);
    public ArrayList<HashMap<String, String>> getDatosProveedorByNoProv(String noProveedor, Integer id_empresa);
    public ArrayList<HashMap<String, String>> getPaises();
    public ArrayList<HashMap<String, String>> getEntidadesForThisPais(String id_pais);
    public ArrayList<HashMap<String, String>> getLocalidadesForThisEntidad(String id_pais,String id_entidad);
    public ArrayList<HashMap<String, String>> getMonedas();
    public ArrayList<HashMap<String, String>> getImpuestos();
    public ArrayList<HashMap<String, String>> getValoriva(Integer id_sucursal);
    public ArrayList<HashMap<String, String>> getTasaFletes();
    public ArrayList<HashMap<String, String>> getTasaRetencionIsr(Integer IdEmpresa);
    public ArrayList<HashMap<String, String>> getFleteras(Integer id_empresa, Integer id_sucursal);
    public ArrayList<HashMap<String, String>> getIeps(Integer idEmp, Integer idSuc);
    
    //catalogo de proveedores
    public ArrayList<HashMap<String, Object>> getProveedor_PaginaGrid(String data_string,int offset, int pageSize, String orderBy , String asc);
    public ArrayList<HashMap<String, String>> getProveedor_Datos(Integer idProveedor);
    public ArrayList<HashMap<String, String>> getProveedor_Contactos(Integer idProveedor);
    public ArrayList<HashMap<String, String>> getProveedor_Tipos();
    public ArrayList<HashMap<String, String>> getProveedor_TiemposEntrega();
    public ArrayList<HashMap<String, String>> getProveedor_Zonas();
    public ArrayList<HashMap<String, String>> getProveedor_Grupos();
    public ArrayList<HashMap<String, String>> getProveedor_Clasificacion1(Integer id_empresa);
    public ArrayList<HashMap<String, String>> getProveedor_Clasificacion2(Integer id_empresa);
    public ArrayList<HashMap<String, String>> getProveedor_Clasificacion3(Integer id_empresa);
    public ArrayList<HashMap<String, String>> getProveedor_InicioCredito();
    public ArrayList<HashMap<String, String>> getProveedor_TiposEmbarque();
    public ArrayList<HashMap<String, String>> getProveedor_CuentasMayor(Integer id_empresa);
    public ArrayList<HashMap<String, String>> getProveedor_CuentasContables(Integer cta_mayor, Integer detalle, String clasifica, String cta, String scta, String sscta, String ssscta, String sssscta, String descripcion, Integer id_empresa);
    public ArrayList<HashMap<String, String>> getProveedor_DatosContabilidad(Integer idProveedor);
    
    //catalogo de Proveedores Clasificacion 1
    public ArrayList<HashMap<String, Object>> getProveedoresClasif1_PaginaGrid(String data_string, int offset, int pageSize, String orderBy , String asc);
    public ArrayList<HashMap<String, String>> getProveedoresClasif1_Datos(Integer id);
    
    //catalogo de Proveedores Clasificacion 2
    public ArrayList<HashMap<String, Object>> getProveedoresClasif2_PaginaGrid(String data_string, int offset, int pageSize, String orderBy , String asc);
    public ArrayList<HashMap<String, String>> getProveedoresClasif2_Datos(Integer id);
    
    //catalogo de Proveedores Clasificacion 3
    public ArrayList<HashMap<String, Object>> getProveedoresClasif3_PaginaGrid(String data_string, int offset, int pageSize, String orderBy , String asc);
    public ArrayList<HashMap<String, String>> getProveedoresClasif3_Datos(Integer id);
    
    //catalogo de Zonas de Proveedores
    public ArrayList<HashMap<String, Object>> getProveedoresZonas_PaginaGrid(String data_string, int offset, int pageSize, String orderBy , String asc);
    public ArrayList<HashMap<String, String>> getProveedoresZonas_Datos(Integer id);
    
    //catalogo de grupos de Proveedores
    public ArrayList<HashMap<String, Object>> getProveedoresGrupos_PaginaGrid(String data_string, int offset, int pageSize, String orderBy , String asc);
    public ArrayList<HashMap<String, String>> getProveedoresGrupos_Datos(Integer id);
    
    //facturas de proveedores
    public ArrayList<HashMap<String, Object>> getProvFacturas_PaginaGrid(String data_string,int offset, int pageSize, String orderBy , String asc);
    public ArrayList<HashMap<String, String>> getProvFacturas_Datos(Integer id);
    public ArrayList<HashMap<String, String>> getProvFacturas_DatosProveedor(Integer id_proveedor);
    public ArrayList<HashMap<String, String>> getProvFacturas_DatosGrid(Integer id);
    public ArrayList<HashMap<String, String>> getProvFacturas_BuscaRemisiones(String folio_remision, String folio_entrada, String proveedor, Integer id_empresa, Integer id_sucursal);
    public ArrayList<HashMap<String, String>> getProvFacturas_DiasCredito();
    public ArrayList<HashMap<String, String>> getProvFacturas_DatosRemision(Integer id);
    public ArrayList<HashMap<String, String>> getProvFacturas_DatosGridRemision(Integer id);
    public ArrayList<HashMap<String, String>> getProvFacturas_BuscadorProveedores(String rfc, String email, String razon_social, Integer id_empresa);
    public ArrayList<HashMap<String, String>> getProvFacturas_TipoCambio(String fecha);
    public ArrayList<HashMap<String, String>> getProvFacturas_TiposCancelacion();
    
    //catalogo de tipo de movimientos de proveedores
    public ArrayList<HashMap<String, Object>> getProveedorestMovimientos_PaginaGrid(String data_string, int offset, int pageSize, String orderBy , String asc);
    public ArrayList<HashMap<String, String>> getProveedorestMovimientos_Datos(Integer id);
    
    //Reporte de Antiguedad de Saldos de Cuentas por Pagar
    public ArrayList<HashMap<String, String>> getDatos_ReporteAntiguedadSaldosCxP(String proveedor, Integer id_empresa);
    
    //aplicativo pagos a proveedores
    public ArrayList<HashMap<String, Object>> getProveedoresPagos_PaginaGrid(String data_string, int offset, int pageSize, String orderBy , String asc);
    public ArrayList<HashMap<String, String>> getProveedoresPagos_Datos(Integer id_pago);
    public ArrayList<HashMap<String, String>> getProveedoresPagos_TipoMovimiento();
    public ArrayList<HashMap<String, String>> getProveedoresPagos_TiposMovTesoreria(Integer id_empresa);
    public ArrayList<HashMap<String, String>> getProveedoresPagos_FormasPago();
    public ArrayList<HashMap<String, String>> getBancos(Integer id_empresa);
    public ArrayList<HashMap<String, String>> getProveedoresPagos_Conceptos(Integer id_empresa);
    public ArrayList<HashMap<String, String>> getTipoCambioActual();
    public ArrayList<HashMap<String, String>> getProveedoresPagos_Chequeras(Integer id_moneda, Integer id_banco);
    public ArrayList<HashMap<String, String>> getProveedoresPagos_Facturas(Integer id_proveedor);
    public ArrayList<HashMap<String, String>> getProveedoresPagos_Detalles(Integer id_pago);
    public ArrayList<HashMap<String, String>> getProveedoresPagos_PagosAplicados(Integer id_pago, Integer id_proveedor);
    
    //catalogo de direcciones de proveedores
    public ArrayList<HashMap<String, Object>> getProveedoresDirecciones_PaginaGrid(String data_string, int offset, int pageSize, String orderBy , String asc);
    public ArrayList<HashMap<String, String>> getProveedoresDirecciones_Datos(Integer id);
    
    //aplicativo pagos a proveedores
    public ArrayList<HashMap<String, Object>> getProveedoresAnticipos_PaginaGrid(String data_string, int offset, int pageSize, String orderBy , String asc);
    public ArrayList<HashMap<String, String>> getProveedoresAnticipos_Datos(Integer id_anticipo);
    public ArrayList<HashMap<String, String>> getProveedoresAnticipos_FormasPago();
    public ArrayList<HashMap<String, String>> getProveedoresAnticipos_Aplicados(Integer id_anticipo, Integer id_proveedor);
    
    //Aplicativo Parametros de Anticipos proveedor
    public ArrayList<HashMap<String, String>> getSucursales(Integer id_empresa);
    public ArrayList<HashMap<String, Object>> getProvParamAnticipos_PaginaGrid(String data_string, int offset, int pageSize, String orderBy , String asc);
    public ArrayList<HashMap<String, String>> getProvParamAnticipos_Datos(Integer id);
    public ArrayList<HashMap<String, String>> getProvParamAnticipos_SucConsecutivo(Integer id_empresa, Integer id_sucursal);
    public ArrayList<HashMap<String, String>> getProvParamAnticipos_TiposMovimiento(Integer id_empresa, Integer grupo, Integer naturaleza, String referenciado);
    
    //reporte de programacio de Pagos 
    public ArrayList<HashMap<String, String>> getProgramacionPagos(String num_semanas, String opcion_seleccionada, Integer id_empresa);
    
    //ESTO VA EN EL CXP INTERFACE DAO
    public ArrayList<HashMap<String, String>> getProveedor_DatosReporteEdoCta(Integer tipo_reporte, String proveedor, String fecha_corte,Integer id_empresa);
    
    //Para reporte de Saldo Mensual de Proveedores
    public ArrayList<HashMap<String, String>> getProveedor_DatosReporteSaldoMensual(Integer tipo_reporte,String proveedor, String fecha_corte,Integer id_empresa);
    public ArrayList<HashMap<String, Object>>  getProveedor_AnioReporteSaldoMensual();
    
    //metodos para Notas de Credito Proveedores
    public ArrayList<HashMap<String, String>> getProvNotasCredito_Facturas(Integer id_proveedor);
    public ArrayList<HashMap<String, String>> getProvNotasCredito_Impuesto(Integer id_proveedor);
    public ArrayList<HashMap<String, Object>> getProvNotasCredito_PaginaGrid(String data_string, int offset, int pageSize, String orderBy , String asc);
    public ArrayList<HashMap<String, String>> getProvNotasCredito_Datos(Integer id);
    public HashMap<String, String> getProvNotasCredito_DatosPDF(Integer id);
    
    //Metodos para Reporte de Proveedores
    public ArrayList<HashMap<String, String>> getListaProveedores(String folio,String razon_proveedor,Integer empresa_id);
    
    
    public ArrayList<HashMap<String, String>> getDatosReporteIepsPagado(ArrayList<HashMap<String, String>> listaIeps, String ciente, String finicial, String ffinal, Integer id_empresa);
    public ArrayList<HashMap<String, String>> getListaIeps(Integer id_empresa);
    
    //Metodos para Reporte Pagos Diarios
    public ArrayList<HashMap<String, String>> getPagosDiaria(String fecha_inicial, String fecha_final, String proveedor, Integer tipo_prov, Integer id_empresa);
    
    
    
    public ArrayList<HashMap<String, String>> getBuscadorProductos(String sku, String tipo, String descripcion, Integer id_empresa);
    public ArrayList<HashMap<String, String>> getEntradas_PresentacionesProducto(String sku, Integer id_empresa);
    
    
    //Metodo para grid de Facturas de proveedores version 2 para logistik
    public ArrayList<HashMap<String, Object>> getCxpFacturas2_PaginaGrid(String data_string, int offset, int pageSize, String orderBy, String asc);
    public ArrayList<HashMap<String, String>> getCxpFacturas2_Datos(Integer id);
    public ArrayList<HashMap<String, String>> getCxpFacturas2_DatosGrid(Integer id);
}
