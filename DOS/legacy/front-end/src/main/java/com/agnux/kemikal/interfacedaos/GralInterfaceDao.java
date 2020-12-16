package com.agnux.kemikal.interfacedaos;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public interface GralInterfaceDao {
    
    public String getJvmTmpDir();
    public String getProdImgDir();
    public String getProdPdfDir();
    
    public String getImagesDir();
    public String getNombreFactura(Integer id_pago);
    public String getSslDir();
    public String getCfdEmitidosDir();
    public String getCfdiTimbreEmitidosDir();
    public String getCfdiTimbreCanceladosDir();
    public String getCfdiTimbreJarWsDir();
    public String getCfdiSolicitudesDir();
    public String getXslDir();
    public String getXsdDir();
    public String getTmpDir();
    public String getZebraDir();
    public String getZebraInDir();
    public String getZebraOutDir();
    public String getZebraProcessingDir();
    
    public String getEmpresa_IncluyeModContable(Integer id_empresa);
    public String getEmpresa_NivelCta(Integer id_empresa);
    
    public String getRazonSocialEmpresaEmisora(Integer id_empresa);
    public String getRfcEmpresaEmisora(Integer id_empresa);
    public String getRegimenFiscalEmpresaEmisora(Integer id_empresa);
    public String getEmailSucursal(Integer id_sucursal);
    
    public String getCertificadoEmpresaEmisora(Integer id_empresa, Integer id_sucursal);
    public String getNoCertificadoEmpresaEmisora(Integer id_empresa, Integer id_sucursal);
    public String getFicheroLlavePrivada(Integer id_empresa, Integer id_sucursal);
    public String getPasswordLlavePrivada(Integer id_empresa, Integer id_sucursal);
    public String getFicheroXsl(Integer id_empresa, Integer id_sucursal);
    public String getFicheroXslCuentasContables(Integer id_empresa, Integer id_sucursal);
    public String getFicheroXslBalanzaComprobacion(Integer id_empresa, Integer id_sucursal);
    public String getFicheroXslTimbre(Integer id_empresa, Integer id_sucursal);
    public String getFicheroXsdCfdi(Integer id_empresa, Integer id_sucursal);
    public String getFicheroXsdXmlCuentasContables(Integer id_empresa, Integer id_sucursal);
    public String getUrlFicheroWsdlTimbradoCfdi(Integer id_empresa, Integer id_sucursal);
    public String getJavaVmDir(Integer id_empresa, Integer id_sucursal);
    public String getFicheroPfxTimbradoCfdi(Integer id_empresa, Integer id_sucursal);
    public String getPasswdFicheroPfxTimbradoCfdi(Integer id_empresa, Integer id_sucursal);
    public String getJavaRutaCacerts(Integer id_empresa, Integer id_sucursal);
    public String getUserContrato(Integer id_empresa, Integer id_sucursal);
    public String getPasswordUserContrato(Integer id_empresa, Integer id_sucursal);
    
    
    
    public String getFolioFactura(Integer id_empresa, Integer id_sucursal);
    public String getFolioNotaCredito(Integer id_empresa, Integer id_sucursal);
    public String getFolioNotaCargo(Integer id_empresa, Integer id_sucursal);
    public String getSerieNotaCargo(Integer id_empresa, Integer id_sucursal);
    public String getAnoAprobacionNotaCargo(Integer id_empresa, Integer id_sucursal);
    public String getNoAprobacionNotaCargo(Integer id_empresa, Integer id_sucursal);
    public String getNoAprobacionNotaCredito(Integer id_empresa, Integer id_sucursal);
    public String getSerieNotaCredito(Integer id_empresa, Integer id_sucursal);
    public String getAnoAprobacionNotaCredito(Integer id_empresa, Integer id_sucursal);
    public String getSerieFactura(Integer id_empresa, Integer id_sucursal);
    public String getAnoAprobacionFactura(Integer id_empresa, Integer id_sucursal);
    public String getNoAprobacionFactura(Integer id_empresa, Integer id_sucursal);
    
    public void actualizarFolioFactura(Integer id_empresa, Integer id_sucursal);
    public void actualizarFolioNotaCredito(Integer id_empresa, Integer id_sucursal);
    public void actualizarFolioNotaCargo(Integer id_empresa, Integer id_sucursal);
    
    public String getSerieFacNomina(Integer id_empresa, Integer id_sucursal);
    public String getFolioFacNomina(Integer id_empresa, Integer id_sucursal);
    public String getNoIdEmpresa(Integer id_empresa);
            
    public String getCalleDomicilioFiscalEmpresaEmisora(Integer id_empresa);
    public String getCpDomicilioFiscalEmpresaEmisora(Integer id_empresa);
    public String getColoniaDomicilioFiscalEmpresaEmisora(Integer id_empresa);
    public String getPaisDomicilioFiscalEmpresaEmisora(Integer id_empresa);
    public String getEstadoDomicilioFiscalEmpresaEmisora(Integer id_empresa);
    public String getMunicipioDomicilioFiscalEmpresaEmisora(Integer id_empresa);
    public String getLocalidadDomicilioFiscalEmpresaEmisora(Integer id_empresa);
    public String getNoExteriorDomicilioFiscalEmpresaEmisora(Integer id_empresa);
    public String getNoInteriorDomicilioFiscalEmpresaEmisora(Integer id_empresa);
    public String getReferenciaDomicilioFiscalEmpresaEmisora(Integer id_empresa);
    public String getTelefonoEmpresaEmisora(Integer id_empresa);
    public String getPaginaWebEmpresaEmisora(Integer id_empresa);
    
    public String getPaisSucursalEmisora(Integer id_sucursal);
    public String getEstadoSucursalEmisora(Integer id_sucursal);
    public String getMunicipioSucursalEmisora(Integer id_sucursal);
    
    /*
    Obtiene todos los datos de la Empresa Emisora. 
    Esto es como alternativa para no utilizar los metodos que obtiene estos datos uno por uno.
    */
    public HashMap<String, String> getEmisor_Datos(Integer id_emp);
    
    
    //obtiene codigo1 para formato controlado por iso
    public String getCodigo1Iso(Integer id_empresa, Integer id_app);
    
    //obtiene codigo2 para formato controlado por iso
    public String getCodigo2Iso(Integer id_empresa, Integer id_app);
    
    //obtiene titulo del reporte
    public String getTituloReporte(Integer id_empresa, Integer id_app);
    
    
    
    //metodos  de uso general
    public int countAll(String data_string);
    public HashMap<String, String> selectFunctionValidateAaplicativo(String data, Integer idApp, String extra_data_array);
    public String selectFunctionForThisApp(String campos_data, String extra_data_array);
    
    //para el catalogo de Puestos
    public ArrayList<HashMap<String, String>> getPuesto_Datos(Integer id);
    public ArrayList<HashMap<String, Object>> getPuestos_PaginaGrid(String data_string, int offset, int pageSize, String orderBy, String asc);
    
    
    //catalogo de empleados
    public ArrayList<HashMap<String, Object>> getEmpleados_PaginaGrid(String data_string, int offset, int pageSize, String orderBy , String asc);
    public ArrayList<HashMap<String, Object>> getEmpleados_Datos(Integer id);
    public ArrayList<HashMap<String, Object>> getPaises();
    public ArrayList<HashMap<String, Object>> getEntidadesForThisPais(String id_pais);
    public ArrayList<HashMap<String, Object>> getLocalidadesForThisEntidad(String id_pais,String id_entidad);
    public ArrayList<HashMap<String,Object>>getEscolaridad(Integer id_empresa);
    public ArrayList<HashMap<String,Object>>getGeneroSexual();
    public ArrayList<HashMap<String,Object>>getEdoCivil();
    public ArrayList<HashMap<String,Object>>getReligion(Integer id_religion);
    public ArrayList<HashMap<String,Object>>getTiposangre(Integer id_enpresa);
    public ArrayList<HashMap<String,Object>>getPuestoForCategoria(String id_puesto);
    public ArrayList<HashMap<String,Object>>getSucursal(Integer id_empresa);
    public ArrayList<HashMap<String,Object>>getPuesto(Integer id_empresa);
    public ArrayList<HashMap<String,Object>> getDepartamentos(Integer id_empresa);
    public ArrayList<HashMap<String,Object>>getRoles();
    public ArrayList<HashMap<String,Object>>getRolsEdit(Integer id_usuario);
    public ArrayList<HashMap<String,Object>>getUsuario(Integer id_usuario);
    public ArrayList<HashMap<String,Object>>getRegion();
    
    public ArrayList<HashMap<String,Object>>getEmpleados_RegimenContratacion();
    public ArrayList<HashMap<String,Object>>getEmpleados_TiposContrato();
    public ArrayList<HashMap<String,Object>>getEmpleados_TiposJornada();
    public ArrayList<HashMap<String,Object>>getEmpleados_PeriodicidadPago(Integer idEmpresa);
    public ArrayList<HashMap<String,Object>>getEmpleados_RiesgosPuesto();
    public ArrayList<HashMap<String,Object>>getEmpleados_Bancos(Integer idEmp);
    public ArrayList<HashMap<String,Object>>getEmpleados_Percepciones(Integer IdEmpleado, Integer idEmpresa);
    public ArrayList<HashMap<String,Object>>getEmpleados_Deducciones(Integer IdEmpleado, Integer idEmpresa);
    
    
    
    
    
    
    
    
    
    
    //esto es para cat de escolaridades
    public ArrayList<HashMap<String, String>> getEscolaridad_Datos(Integer id);                                                         
    public ArrayList<HashMap<String, Object>> getEscolaridad_PaginaGrid(String data_string, int offset, int pageSize, String orderBy, String asc);
    
    //Esto es para cat de religiones
    public ArrayList<HashMap<String, String>> getReligion_Datos(Integer id);                                                         
    public ArrayList<HashMap<String, Object>> getReligion_PaginaGrid(String data_string, int offset, int pageSize, String orderBy, String asc);
    
    //Esto es para cat sangre Tipos
    public ArrayList<HashMap<String, String>> getTipoSangre_Datos(Integer id);                                                         
    public ArrayList<HashMap<String, Object>> getTipoSangre_PaginaGrid(String data_string, int offset, int pageSize, String orderBy, String asc);
    
    //esto es para cat categoras
    public ArrayList<HashMap<String, String>> getCateg_Datos(Integer id);
    public ArrayList<HashMap<String, Object>> getCateg_PaginaGrid(String data_string, int offset, int pageSize, String orderBy, String asc);
    public ArrayList<HashMap<String, String>> getPuestos(Integer id_empresa); 
    
    //catalogo de inventario gral departamentos
    public ArrayList<HashMap<String, Object>> getGralDeptos_PaginaGrid(String data_string, int offset, int pageSize, String orderBy , String asc);
    public ArrayList<HashMap<String, String>> getGralDeptos_Datos(Integer id_unidad);
    
    
    //Esto es para cat de turnos por departamento
    public ArrayList<HashMap<String, String>> getTurnos(Integer id);
    public ArrayList<HashMap<String, Object>> getTurnos_PaginaGrid(String data_string, int offset, int pageSize, String orderBy, String asc);
    public ArrayList<HashMap<String, String>> getDeptos(); 
    
    //esto es para cat Dias no Laborables
    public ArrayList<HashMap<String, String>> getDiasNoLaborables(Integer id);
    public ArrayList<HashMap<String, Object>> getDiasNoLaborables_PaginaGrid(String data_string, int offset, int pageSize, String orderBy, String asc);
    
    //Estos son para email y password de compras
    public String geteMailPurchasingEmpresaEmisora(Integer id_empresa);
    public String getPasswordeMailPurchasingEmpresaEmisora(Integer id_empresa);
    
    //Actualizador de codigo ISO
    public ArrayList<HashMap<String, String>> getCodigos_Datos(Integer id);
    public ArrayList<HashMap<String, String>> getTitulo_Datos(Integer id);
    public ArrayList<HashMap<String, Object>> getCodigos_PaginaGrid(String data_string, int offset, int pageSize, String orderBy, String asc,Integer id_empresa);
    
    //Actualizador de Tipos de Cambio.
    public ArrayList<HashMap<String, String>> getTiposdeCambio();
    public ArrayList<HashMap<String, Object>> getTipocambio_PaginaGrid(String data_string, int offset, int items_por_pag, String orderby, String desc);
    public ArrayList<HashMap<String, String>> gettipoCambio_Datos(String erpmonedavers_id);                                         
    
    /*Descarga de ficha tecnica*/
    public ArrayList<HashMap<String, Object>> getFichaTecnica_PaginaGrid(String data_string, int offset, int pageSize, String orderBy, String asc,Integer id_empresa);
    public String getCodigoProductoById(String id_producto);
    
    //Obtiene el nombre del empleado a partir del id del usuario
    public String getNombreEmpleadoByIdUser(Integer id_user);
    
    //Aplicativo de Cambio de Contraseña de Usuario
    public ArrayList<HashMap<String, Object>> GralUserEdit_PaginaGrid(String data_string, int offset, int pageSize, String orderBy, String asc);
    public ArrayList<HashMap<String, String>> GralUserEdit_Datos(Integer id);  
    
    //Para el Cátalogo de Ieps
    public ArrayList<HashMap<String, String>> getIeps_Datos(Integer id);
    public ArrayList<HashMap<String, Object>> getIeps_PaginaGrid(String data_string, int offset, int pageSize, String orderBy, String asc);
    
    //Para el Cátalogo de Percepciones
    public ArrayList<HashMap<String, Object>> getPercepciones_Datos(Integer id);
    public ArrayList<HashMap<String, Object>> getPercepciones_PaginaGrid(String data_string, int offset, int pageSize, String orderBy, String asc);
    public ArrayList<HashMap<String,Object>>getPercepciones_Tipos(Integer id_empresa);
    
     //Para el Cátalogo de Deducciones
    public ArrayList<HashMap<String, Object>> getDeducciones_Datos(Integer id);
    public ArrayList<HashMap<String, Object>> getDeducciones_PaginaGrid(String data_string, int offset, int pageSize, String orderBy, String asc);
    public ArrayList<HashMap<String,Object>>getDeducciones_Tipos(Integer id_empresa);
    
    //Para el Cátalogo de Periodicidad de Pago
    public ArrayList<HashMap<String, Object>> getPeriodicidadPago_Datos(Integer id);
    public ArrayList<HashMap<String, Object>> getPeriodicidadPago_PaginaGrid(String data_string, int offset, int pageSize, String orderBy, String asc);
    
    //Para el Cátalogo de Configuracion de Periodos de Pago
    public ArrayList<HashMap<String, Object>> getConfigPeriodosPago_Datos(Integer id);
    public ArrayList<HashMap<String, Object>> getConfigPeriodosPago_PaginaGrid(String data_string, int offset, int pageSize, String orderBy, String asc);
    public ArrayList<HashMap<String,Object>> getPeriodicidad_Tipos(Integer id_empresa, Integer id_usuario);
    public ArrayList<HashMap<String, Object>> getConfigPeriodosPago_Grid(Integer id);
    //Calcular años a mostrar
    public ArrayList<HashMap<String, Object>> getConfigPeriodosPago_Anios();
    
    public Map<String, Object> getTipoCambio_Url(Integer id_empresa);
    
    //Metodos para catálogo de Iva Trasladado
    public ArrayList<HashMap<String, Object>> getImpTras_Datos(Integer id);
    public ArrayList<HashMap<String, Object>> getImpTras_PaginaGrid(String data_string, int offset, int pageSize, String orderBy, String asc);
    public ArrayList<HashMap<String, Object>> getImpTras_DatosContabilidad(Integer id);
    
    //Metodos para catálogo de Iva Retenido
    public ArrayList<HashMap<String, Object>> getImpRet_Datos(Integer id);
    public ArrayList<HashMap<String, Object>> getImpRet_PaginaGrid(String data_string, int offset, int pageSize, String orderBy, String asc);
    public ArrayList<HashMap<String, Object>> getImpRet_DatosContabilidad(Integer id);
    
    //Catalogo de Métodos de Pago
    public ArrayList<HashMap<String, Object>> getMetodosDePago_PaginaGrid(String data_string, int offset, int pageSize, String orderBy, String asc);
    public ArrayList<HashMap<String, Object>> getMetodosDePago_Datos(Integer id);
}
