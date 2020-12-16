package com.agnux.kemikal.interfacedaos;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;


public interface FacturasInterfaceDao {
    public int countAll(String data_string);
    public String selectFunctionForFacAdmProcesos(String campos_data, String extra_data_array);
    public HashMap<String, String> selectFunctionValidateAaplicativo(String data, Integer idApp, String extra_data_array);
    public HashMap<String,String> getFac_Parametros(Integer id_emp, Integer id_suc);
    
    public ArrayList<HashMap<String, Object>> getFacturas_PaginaGrid(String data_string,int offset, int pageSize, String orderBy , String asc);
    public ArrayList<HashMap<String, Object>> getFactura_Datos(Integer id_factura);
    public ArrayList<HashMap<String, Object>> getFactura_DatosGrid(Integer id_factura);
    public ArrayList<HashMap<String, Object>> getFactura_DatosAdenda(Integer id_factura);
    public ArrayList<HashMap<String, Object>> getFactura_Monedas();
    public ArrayList<HashMap<String, Object>> getFactura_Agentes(Integer id_empresa, Integer id_sucursal);
    public ArrayList<HashMap<String, Object>> getFactura_DiasDeCredito();
    public ArrayList<HashMap<String, Object>> getMetodosPago(Integer empresaId);
    public ArrayList<HashMap<String, Integer>> getFactura_AnioInforme();
    
    public String getSerieFolioFactura(Integer id_factura, Integer idEmp);
    public String q_serie_folio(final Integer usr_id);
    public String getSerieFolioFacturaByIdPrefactura(Integer id_prefactura, Integer idEmp);
    public Integer getIdPrefacturaByIdFactura(Integer id_factura);
    public String getRefIdFactura(Integer id_factura, Integer idEmp);
    public String getRefIdByIdPrefactura(Integer id_prefactura, Integer idEmp);
    
    public Double getTipoCambioActual();
    public ArrayList<HashMap<String, Object>> getValoriva(Integer id_sucursal);
    
    public String getTipoFacturacion(Integer idEmp);
    public String getNoPacFacturacion(Integer idEmp);
    public String getAmbienteFacturacion(Integer idEmp);
    public HashMap<String, String> getParametrosEmpresa(Integer idEmp);
    
    //Obtiene los namespaces para el xml de la Factura o Nomina
    public ArrayList<LinkedHashMap<String,String>> getDataXml_Namespaces(String tipo);
    
    //para bean facturador
    public HashMap<String,String> getDataFacturaXml(Integer id_prefactura);
    public String getFechaComprobante();
    public ArrayList<LinkedHashMap<String, String>> getListaConceptosFacturaXml(Integer id_prefactura);
    public ArrayList<LinkedHashMap<String, String>> getImpuestosRetenidosFacturaXml(ArrayList<LinkedHashMap<String,String>> conceptos);
    public ArrayList<LinkedHashMap<String, String>> getImpuestosTrasladadosFacturaXml(Integer id_sucursal, ArrayList<LinkedHashMap<String,String>> conceptos, ArrayList<HashMap<String, String>> ieps, ArrayList<HashMap<String, String>> ivas);
    public LinkedHashMap<String, String> getDatosExtrasFacturaXml(String id_prefactura, String tipo_cambio_vista, String id_usuario, String id_moneda, Integer id_empresa, Integer id_sucursal, String refacturar, Integer app_selected, String command_selected, String extra_data_array);
    
    public LinkedHashMap<String, String> getDatosExtrasCfdi(Integer id_factura);
    public ArrayList<LinkedHashMap<String, String>> getListaConceptosCfdi(Integer id_factura, String rfcEmisor);
    //public ArrayList<LinkedHashMap<String, String>> getImpuestosTrasladadosCfdi(Integer id_factura);
    public ArrayList<LinkedHashMap<String, String>> getImpuestosTrasladadosCfdi(Integer id_factura, Integer id_sucursal);
    public ArrayList<LinkedHashMap<String, String>> getImpuestosRetenidosCfdi(Integer id_factura);
    public ArrayList<String> getLeyendasEspecialesCfdi(Integer id_empresa);
    
    
    public ArrayList<LinkedHashMap<String, String>> getListaConceptosXmlCfdiTf(Integer id_prefactura, String permitir_descuento);
    
    //Actualizar campo de salidas de archivos en fac_docs
    //public Boolean update_fac_docs_salidas(String serieFolio, String nombre_archivo);
    //public String verifica_fac_docs_salidas(Integer id_factura);
    
    public ArrayList<HashMap<String, String>> getListaConceptosPdfCfd(String serieFolio);
    public HashMap<String, String> getDatosExtrasPdfCfd(String serieFolio, String proposito, String cadena_original, String sello_digital, Integer id_sucursal);
    
    
    
    
    
    public ArrayList<HashMap<String, String>> getTiposCancelacion();
    
    public void fnSalvaDatosFacturas(String rfc_receptor,
                        String serie_factura,
                        String folio_factura,
                        String no_probacion,
                        String total,
                        String tot_imp_trasladados,
                        String edo_comprobante,
                        String xml_file_name,
                        String fecha,
                        String razon_social_receptor,
                        String tipo_comprobante,
                        String proposito,
                        String ano_probacion ,
                        String cadena_conceptos,
                        String cadena_imp_trasladados,
                        String cadena_imp_retenidos,
                        Integer prefactura_id,
                        Integer id_usuario,
                        Integer id_moneda,
                        String tipo_cambio,
                        String refacturar,
                        String regimen_fiscal,
                        String metodo_pago,
                        String num_cuenta,
                        String lugar_de_expedicion);
    //public void modificaFlujoPrefactura(String sql_update);
    
    public String formar_cadena_conceptos(ArrayList<LinkedHashMap<String,String>> concepts);
    public String formar_cadena_traslados(String cantidad_lana_iva,String tasa_iva);
    public String formar_cadena_retenidos(String cantidad_lana_iva,String tasa_iva);
    
    
    //reporte de comprobantes por mes para hacienda(reporte txt)
    public ArrayList<HashMap<String, Object>> getComprobantesActividadPorMes(String year,String month,Integer id_empresa);
    
    //metodo para el Reporte de Facturacion
    public ArrayList<HashMap<String, String>> getDatosReporteFacturacion(Integer opcion, String factura, String cliente, String fecha_inicial, String fecha_final, Integer id_empresa);
    
    
    //Reporte de Remisiones creado el 28/06/2012 //variable estatus agregado por paco
    public ArrayList<HashMap<String, String>> getDatosReporteRemision(Integer opcion, String remision, String cliente, String fecha_inicial, String fecha_final, Integer id_empresa, Integer estatus);
    public ArrayList<HashMap<String, String>> getBuscadorClientes(String cadena, Integer filtro,Integer id_empresa, Integer id_sucursal);
    public ArrayList<HashMap<String, String>> getDatosClienteByNoCliente(String no_control, Integer id_empresa, Integer id_sucursal);
    
    
    //Reporte de Remisiones facturadas creado el 21/07/2012  por vale8490
    public ArrayList<HashMap<String, String>> getDatosReporteRemision_facturada(Integer opcion, String remision, String cliente, String fecha_inicial, String fecha_final, Integer id_empresa);
    
    
    //metodos para notas de Credito
    public ArrayList<HashMap<String, Object>> getNotasCredito_PaginaGrid(String data_string,int offset, int pageSize, String orderBy , String asc);
    public ArrayList<HashMap<String, Object>> getNotasCredito_Datos(Integer id_cliente);
    public ArrayList<HashMap<String, String>> getNotasCredito_FacturasCliente(Integer id_cliente, String serie_folio);
    public ArrayList<HashMap<String, String>> getNotasCredito_DatosFactura(Integer id_cliente, String serie_folio);
    
    //metodos para xml nota de credito
    public HashMap<String,String> getNotaCreditoCfd_Cfdi_Datos(Integer id_nota_credito);//este metodo se utiliza para Nota de Credito CFD y CFDI
    public ArrayList<LinkedHashMap<String,String>> getNotaCreditoCfd_ListaConceptosXml(Integer id_nota_credito);
    public ArrayList<LinkedHashMap<String,String>> getNotaCreditoCfd_CfdiTf_ImpuestosRetenidosXml();
    public ArrayList<LinkedHashMap<String,String>> getNotaCreditoCfd_CfdiTf_ImpuestosTrasladadosXml(Integer id_sucursal);
    public LinkedHashMap<String,String> getNotaCreditoCfd_DatosExtrasXml(Integer id_nota_credito, String tipo_cambio,String id_usuario,String moneda_id, Integer id_empresa, Integer id_sucursal, Integer app_selected, String command_selected, String extra_data_array, String fac_saldado);
    public String getSerieFolioNotaCredito(Integer id_nota_credito);
    public String getRefIdNotaCredito(Integer id_nota_credito);
    public ArrayList<HashMap<String, String>> getNotaCreditoCfd_ListaConceptosPdf(String serieFolio);
    public HashMap<String, String> getNotaCreditoCfd_DatosExtrasPdf(String serieFolio, String proposito, String cadena_original, String sello_digital, Integer id_sucursal, Integer id_empresa);
    
    
    //para txt de Nota de Credito cfdi
    public ArrayList<LinkedHashMap<String, String>> getNotaCreditoCfdi_ListaConceptos(Integer id_nota_credito);
    public LinkedHashMap<String, String> getNotaCreditoCfdi_DatosExtras(Integer id_nota_credito, String serie, String folio);
    public ArrayList<LinkedHashMap<String,String>> getNotaCreditoCfdi_ImpuestosTrasladados(Integer id_nota_credito);
    public ArrayList<LinkedHashMap<String,String>> getNotaCreditoCfdi_ImpuestosRetenidos(Integer id_nota_credito);
    
    //este metodo es para buscar si la factura seleccionada ya tiene asociada una Nota de Credito que se haya generado desde Devoluciones
    public ArrayList<HashMap<String, Object>> getFacDevoluciones_DatosNotaCredito(String factura, String idCliente);
    
    
    //public ArrayList<HashMap<String, String>> get_buscador_clientes(String cadena, Integer filtro, Integer id_empresa, Integer id_sucursal);
    //public ArrayList<HashMap<String, String>> getBuscadorClientes(String cadena, Integer filtro,Integer id_empresa, Integer id_sucursal);
    
    
    /*Add by jpakoery, for nota de credito tf*/
    public ArrayList<LinkedHashMap<String, String>> getNotaCreditoCfdiTf_ListaConceptosXml(Integer idNotaCredito);
    public ArrayList<LinkedHashMap<String, String>> getNotaCreditoCfdiTf_ConceptosParaImpuestosXml(Integer idNotaCredito);
    
    
    public LinkedHashMap<String, Object> getDatosAdenda(Integer tipoDoc, Integer noAdenda, HashMap<String,String> dataFactura, Integer identificador, String serieFolio, Integer id_emp);
    public int buscarAdendaFactura(Integer idNotaCredito);
    public int getStatusAdendaFactura(Integer id_tipo_addenda, Integer id_fac);
    
    public ArrayList<HashMap<String, String>> getIeps(Integer idEmp);
    public ArrayList<HashMap<String, String>> getIvas();
    
    
    
    
    
    //METODOS PARA FACTURACION DE NOMINA
    public ArrayList<HashMap<String, Object>> getFacNomina_PaginaGrid(String data_string,int offset, int pageSize, String orderBy , String asc);
    public ArrayList<HashMap<String, Object>> getFacNomina_Datos(Integer id);
    public ArrayList<HashMap<String, Object>> getFacNomina_Grid(Integer id);
    public ArrayList<HashMap<String, Object>> getFacNomina_DataNomina(Integer id_reg, Integer id_empleado);
    public ArrayList<HashMap<String, Object>> getFacNomina_HorasExtras(Integer id_nom_det);
    public ArrayList<HashMap<String, Object>> getFacNomina_Incapacidades(Integer id_nom_det);
    //Obtener id de fac_nomina_det para permitir editar el registro cuando es nuevo, ya que los registros se crean con fac_nomina
    public ArrayList<HashMap<String, Object>> getFacNomina_IdNomimaDet(Integer id, Integer id_empleado);
    
    
    public HashMap<String, Object> getFacNomina_DatosEmisor(Integer id_emp);
    public ArrayList<HashMap<String,Object>>getFacNomina_Parametros(Integer idEmp,Integer idSuc);
    public HashMap<String,Object>getFacNomina_LeyendaReciboNomina(Integer idEmp,Integer idSuc);
    public ArrayList<HashMap<String, Object>> getFacNomina_Empleados(Integer id_emp, Integer periodicidad_id);
    //Obtiene la siguiente secuencia para el id de la tabla fac_nomina
    public int getIdSeqFacNomina();
    public ArrayList<HashMap<String, Object>> getFacNomina_PeriodicidadPago(Integer idEmpresa);
    public ArrayList<HashMap<String, Object>> getFacNomina_PeriodosPorTipo(Integer tipo, Integer idEmpresa, Integer identificador);
    public ArrayList<HashMap<String, Object>> getFacNomina_RegimenContratacion();
    public ArrayList<HashMap<String, Object>> getFacNomina_TiposContrato();
    public ArrayList<HashMap<String, Object>> getFacNomina_TiposJornada();
    public ArrayList<HashMap<String, Object>> getFacNomina_RiesgosPuesto();
    public ArrayList<HashMap<String, Object>> getFacNomina_TiposHoraExtra();
    public ArrayList<HashMap<String, Object>> getFacNomina_TiposIncapacidad();
    
    public ArrayList<HashMap<String, Object>> getFacNomina_Bancos(Integer idEmpresa);
    public ArrayList<HashMap<String, Object>> getFacNomina_ISR(Integer id_empresa);
    public ArrayList<HashMap<String, Object>> getFacNomina_Puestos(Integer id_empresa);
    public ArrayList<HashMap<String, Object>> getFacNomina_Departamentos(Integer id_empresa);
    public ArrayList<HashMap<String, Object>> getFacNomina_Percepciones(Integer tipo, Integer id_reg, Integer IdEmpleado, Integer idEmpresa);
    public ArrayList<HashMap<String, Object>> getFacNomina_Deducciones(Integer tipo, Integer id_reg, Integer IdEmpleado, Integer idEmpresa);
    
    public ArrayList<HashMap<String, Object>> getFacNomina_DataEmpleado(Integer id_empleado);
    public ArrayList<HashMap<String, Object>> getFacNomina_DataPeriodo(Integer id_periodo, Integer idEmpresa);
    
    public HashMap<String, String> getFacNomina_RefId(Integer id);
    
    //Obtener registros para generar CFDI de Nomina
    public ArrayList<HashMap<String, Object>> getFacNomina_Registros(Integer id);
    //::::::METODOS QUE OBTIENEN DATOS PARA EL XML DE LA NOMINA CFDI::::::::::::
    public ArrayList<LinkedHashMap<String, String>> getFacNomina_ConceptosXml(Integer id, Integer id_empleado);
    public ArrayList<LinkedHashMap<String, String>> getFacNomina_ImpuestosRetenidosXml(Integer id, Integer id_empleado);
    public HashMap<String, String> getFacNomina_DataXml(Integer id, Integer id_empleado);
    public ArrayList<LinkedHashMap<String,String>> getFacNomina_PercepcionesXml(Integer id);
    public ArrayList<LinkedHashMap<String,String>> getFacNomina_DeduccionesXml(Integer id);
    public ArrayList<LinkedHashMap<String,String>> getFacNomina_IncapacidadesXml(Integer id);
    public ArrayList<LinkedHashMap<String,String>> getFacNomina_HorasExtrasXml(Integer id);
    //::::::TERMINA METODOS QUE OBTIENEN DATOS PARA EL XML DE LA NOMINA CFDI::::
    
    
    //Metodos para enviar email
    public ArrayList<HashMap<String, String>> getEmailEnvio(Integer id_emp,  Integer id_suc);
    public ArrayList<HashMap<String, String>> getEmailCopiaOculta(Integer id_emp,  Integer id_suc);
    
    
    //Metodos para Configuracion de Parametros de Facturacion
    public ArrayList<HashMap<String, Object>> getFacPar_PaginaGrid(String data_string,int offset, int pageSize, String orderBy , String asc);
    public ArrayList<HashMap<String, Object>> getFacPar_Datos(Integer id_factura);
    public ArrayList<HashMap<String, Object>> getFacPar_Almacenes(Integer id_emp, Integer id_suc);
    
    //Obtiene los tipos de movimientos de contabilidad
    public ArrayList<HashMap<String, Object>> getCtb_TiposDeMovimiento(Integer id_empresa, Integer appId);
    
}
