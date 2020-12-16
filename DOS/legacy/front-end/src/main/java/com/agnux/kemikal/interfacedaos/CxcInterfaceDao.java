package com.agnux.kemikal.interfacedaos;

import java.util.ArrayList;
import java.util.HashMap;

public interface CxcInterfaceDao {
    public HashMap<String, String> selectFunctionValidateAaplicativo(String data, Integer idApp, String extra_data_array);
    public String selectFunctionForThisApp(String campos_data, String extra_data_array);
    public String selectFunctionForCxcAdmProcesos(String campos_data, String extra_data_array);
    public int countAll(String data_string);

    public ArrayList<HashMap<String, Object>> getPaises();
    public ArrayList<HashMap<String, Object>> getEntidadesForThisPais(String id_pais);
    public ArrayList<HashMap<String, Object>> getLocalidadesForThisEntidad(String id_pais,String id_entidad);
    public ArrayList<HashMap<String, Object>> getMonedas();
    public ArrayList<HashMap<String, Object>> getImpuestos();
    
    //catalogo de clientes
    public ArrayList<HashMap<String, Object>> getClientes_PaginaGrid(String data_string, int offset, int pageSize, String orderBy , String asc);
    public ArrayList<HashMap<String, Object>> getCliente_Datos(Integer id);
    public ArrayList<HashMap<String, Object>> getCliente_DirConsignacion(Integer id_cliente);
    public HashMap<String, String> getCliente_ValidaDirConsignacion(String data_string);
    public ArrayList<HashMap<String, Object>> getCliente_Tipos();
    public ArrayList<HashMap<String, Object>> getCliente_Vendedores(Integer id_empresa);
    public ArrayList<HashMap<String, Object>> getCliente_Condiciones();
    public ArrayList<HashMap<String, Object>> getCliente_Zonas();
    public ArrayList<HashMap<String, Object>> getCliente_Grupos();
    public ArrayList<HashMap<String, Object>> getCliente_Clasificacion1();
    public ArrayList<HashMap<String, Object>> getCliente_Clasificacion2();
    public ArrayList<HashMap<String, Object>> getCliente_Clasificacion3();
    public ArrayList<HashMap<String, Object>> getCliente_InicioCredito();
    public ArrayList<HashMap<String, Object>> getCliente_TiposEmbarque();
    public ArrayList<HashMap<String, Object>> getMetodosPago(Integer empresaId);
    public ArrayList<HashMap<String, Object>> getCliente_CuentasMayor(Integer id_empresa);
    public ArrayList<HashMap<String, String>> getCliente_CuentasContables(Integer cta_mayor, Integer detalle, String clasifica, String cta, String scta, String sscta, String ssscta, String sssscta, String descripcion, Integer id_empresa );
    public ArrayList<HashMap<String, Object>> getCliente_DatosContabilidad(Integer id);

    //carteras
    public ArrayList<HashMap<String, Object>> getCartera_PaginaGrid(String data_string, int offset, int pageSize, String orderBy , String asc);
    public ArrayList<HashMap<String, Object>> getCartera_TipoMovimiento();
    public ArrayList<HashMap<String, Object>> getCartera_FormasPago();
    public ArrayList<HashMap<String, Object>> getBancos(Integer id_empresa);
    public ArrayList<HashMap<String, Object>> getTipoCambioActual();
    public ArrayList<HashMap<String, Object>> getCartera_BancosEmpresa(Integer id_empresa);
    public ArrayList<HashMap<String, Object>> getBuscadorClientes(String cadena, Integer filtro,Integer id_empresa, Integer id_sucursal);
    public ArrayList<HashMap<String, Object>> getDatosClienteByNoCliente(String no_control, Integer id_empresa, Integer id_sucursal);
    public ArrayList<HashMap<String, Object>> getCartera_CtaBanco(Integer id_moneda, Integer id_banco);
    public ArrayList<HashMap<String, Object>> getCartera_BancosXMoneda(Integer id_moneda, Integer id_empresa);
    public ArrayList<HashMap<String, Object>> getCartera_SumaAnticiposMN(Integer id_cliente);
    public ArrayList<HashMap<String, Object>> getCartera_SumaAnticiposUSD(Integer id_cliente);
    public ArrayList<HashMap<String, Object>> getCartera_Anticipos(Integer id_cliente);
    public ArrayList<HashMap<String, Object>> getCartera_Facturas(Integer id_cliente);
    public ArrayList<HashMap<String, String>> getCartera_FacturasCancelar(String num_transaccion,String factura, Integer id_cliente);
    public ArrayList<HashMap<String, Object>> getCartera_FacturasTransaccion(String num_transaccion);
    public ArrayList<HashMap<String, String>> getCartera_NumerosDeTransaccionCliente(Integer id_cliente);
    public ArrayList<HashMap<String, String>> getCartera_DatosReporteEdoCta(Integer tipo_reporte,Integer id_cliente, Integer id_moneda, String fecha_corte, Integer id_empresa, Integer id_agente);
    public ArrayList<HashMap<String, String>> getCartera_DatosReporteDepositos(String fecha_inicial, String fecha_final, Integer id_empresa);
    public ArrayList<HashMap<String, String>> getCartera_PagosAplicados(Integer id_pago, Integer id_empresa);
    public ArrayList<HashMap<String, String>> getCartera_PagosDatosHeader(Integer id_pago, Integer id_empresa);


    //reporte de cobranza por agente
    public ArrayList<HashMap<String, String>> getAgentes(Integer id_empresa);
    public ArrayList<HashMap<String, String>> getCartera_DatosReporteCobranzaAgente(Integer id_agente,String fecha_inicial,String fecha_final,Double monto_inicial,Double monto_final, Integer tipo_comision,Integer id_empresa);
    public ArrayList<HashMap<String, String>> getCartera_DatosReporteVentaxAgente(Integer id_agente,String fecha_inicial,String fecha_final, Integer id_empresa);

    //atalogo de agentes
    public ArrayList<HashMap<String, Object>> getAgente_PaginaGrid(String data_string, int offset, int pageSize, String orderBy , String asc);
    public ArrayList<HashMap<String, String>> getUsuarios(Integer id_empresa, Integer usuario_agente);
    public ArrayList<HashMap<String, String>> getAgente_Datos(Integer id_agente);
    public ArrayList<HashMap<String, String>> getAgente_Regiones();

    //reporte de pronostico de Cobranza
    public ArrayList<HashMap<String, String>> getPronosticoDeCobranza(String num_semanas, String opcion_seleccionada, Integer id_empresa);

    //reporte de ventas netas por cliente
    public ArrayList<HashMap<String, String>> getVentasNetasxCliente(String fecha_inicial, String fecha_final, Integer id_empresa);

    //reporte de venta netas por producto factura
    //reporte de venta netas por producto factura
    //public ArrayList<HashMap<String, String>> getVentasNetasProductoFactura(Integer tipo_reporte,String cliente, String producto, String fecha_inicial, String fecha_final, Integer id_empresa,Integer id_linea, Integer id_marca, Integer id_familia, Integer id_subfamilia,Integer tipo_costo);
    //public ArrayList<HashMap<String, String>> getVentasNetasProductoFactura(Integer tipo_reporte,String cliente, String producto, String fecha_inicial, String fecha_final, Integer id_empresa,Integer id_linea, Integer id_marca, Integer id_familia, Integer id_subfamilia,Integer tipo_costo,Integer id_agente);
    public ArrayList<HashMap<String, String>> getVentasNetasProductoFactura(Integer tipo_reporte, String cliente,String producto, String fecha_inicial, String fecha_final,Integer id_empresa,Integer id_linea,Integer  id_marca, Integer id_familia,Integer id_subfamilia,Integer tipo_costo, Integer id_agente, Integer segmentoId, Integer mercadoId);
    public ArrayList<HashMap<String, String>> getProductoTipos();
    public ArrayList<HashMap<String, Object>> getProductoTiposV2();
    public ArrayList<HashMap<String, String>> getLineas(Integer empresaId);
    public ArrayList<HashMap<String, String>> getMarcas(Integer empresaId);
    public ArrayList<HashMap<String, String>> getFamilias(Integer empresaId);
    public ArrayList<HashMap<String, String>> getSubfamilias(Integer id_familia);
    public ArrayList<HashMap<String, String>> getBuscadorProductos(String sku, String tipo, String descripcion, Integer id_empresa);

    //catalogo de Clientes Clasificacion 1
    public ArrayList<HashMap<String, Object>> getClientsClasif1_PaginaGrid(String data_string, int offset, int pageSize, String orderBy , String asc);
    public ArrayList<HashMap<String, String>> getClientsClasif1_Datos(Integer id);


    //catalogo de Clientes Clasificacion 2
    public ArrayList<HashMap<String, Object>> getClientsClasif2_PaginaGrid(String data_string, int offset, int pageSize, String orderBy , String asc);
    public ArrayList<HashMap<String, String>> getClientsClasif2_Datos(Integer id);


    //catalogo de Clientes Clasificacion 3
    public ArrayList<HashMap<String, Object>> getClientsClasif3_PaginaGrid(String data_string, int offset, int pageSize, String orderBy , String asc);
    public ArrayList<HashMap<String, String>> getClientsClasif3_Datos(Integer id);


    //catalogo de Zonas de Clientes
    public ArrayList<HashMap<String, Object>> getclientsZonas_PaginaGrid(String data_string, int offset, int pageSize, String orderBy , String asc);
    public ArrayList<HashMap<String, String>> getClientsZonas_Datos(Integer id);


    //catalogo de grupos de Clientes
    public ArrayList<HashMap<String, Object>> getClientsGrupos_PaginaGrid(String data_string, int offset, int pageSize, String orderBy , String asc);
    public ArrayList<HashMap<String, String>> getClientsGrupos_Datos(Integer id);


    //catalogo de tipo de movimientos de Clientes
    public ArrayList<HashMap<String, Object>> getClientstMovimientos_PaginaGrid(String data_string, int offset, int pageSize, String orderBy , String asc);
    public ArrayList<HashMap<String, Object>> getClientstMovimientos_Datos(Integer id);

    public ArrayList<HashMap<String, String>> getDatos_ReporteAntiguedadSaldos(Integer tipo, String cliente, Integer id_empresa);


    public ArrayList<HashMap<String,String>> getEstadisticaVentas(Integer mes_in,Integer mes_fin, Integer anio, Integer id_empresa);

    //reporte de estadistico de ventas por producto
    public ArrayList<HashMap<String,String>> getEstadisticaVentasProducto(Integer mes_in,Integer mes_fin,Integer tipo_producto, Integer familia,Integer subfamilia,Integer id_empresa, Integer anio);
    public ArrayList<HashMap<String,String>> getFamilias(Integer tipo_producto, Integer id_empresa);
    public ArrayList<HashMap<String,String>> getSubFamilias(Integer familia_id);

       //reporte de estadistico de ventas por unidades
    public ArrayList<HashMap<String,String>> getEstadisticaVentasUnidades(Integer mes_in,Integer mes_fin,Integer tipo_producto, Integer familia,Integer subfamilia,Integer id_empresa, Integer anio);
    public ArrayList<HashMap<String,String>> getFamilias2(Integer tipo_producto, Integer id_empresa);
    public ArrayList<HashMap<String,String>> getSubFamilias2(Integer familia_id);

    
    //Aplicativo de programacion de rutas
    public ArrayList<HashMap<String,String>>getProgramacionPagos_FacturasRevision(Integer cliente,String dia_fecha,Integer empresa_id);
    public ArrayList<HashMap<String,String>>getProgramacionPagos_FacturasCobro(Integer cliente,String dia_fecha,Integer empresa_id);
    public ArrayList<HashMap<String, Object>> getProgramacionPagos_PaginaGrid(String data_string, int offset, int pageSize, String orderBy , String asc);
    public ArrayList<HashMap<String, String>> getProgramacionPagos_Datos(Integer id);
    public ArrayList<HashMap<String, String>> getProgramacionPagos_Facturas(Integer id);


    //reporte Cobranza Diaria
    public ArrayList<HashMap<String, String>> getCobranzaDiaria(String fecha_inicial, String fecha_final,Integer cliente, Integer id_empresa);

    //reporte Anticipos no Autorizados
    public ArrayList<HashMap<String, String>> getAnticiposnoAplicados(String fecha_inicial, String fecha_final, Integer cliente, Integer id_empresa);

    //catalogo de direcciones fiscales de clientes
    public ArrayList<HashMap<String, Object>> getClientsDf_PaginaGrid(String data_string, int offset, int pageSize, String orderBy , String asc);
    public ArrayList<HashMap<String, Object>> getClientsDf_Datos(Integer id);


    public ArrayList<HashMap<String, String>> getListaClientes(Integer empresa_id, Integer agente_id );

    public ArrayList<HashMap<String, Object>> getClientsAntCancel_PaginaGrid(String data_string, int offset, int pageSize, String orderBy, String asc);
    public ArrayList<HashMap<String, Object>> getClientsAntCancel_DatosAnticipo(Integer id);
    
    //Metodo para verificar si un usuario es Administrador
    public HashMap<String, String> getUserRolAdmin(Integer id_user);
    //Metodo para verificar si un usuario es Vendedor
    public HashMap<String, String> getUserRolAgenteVenta(Integer id_user);
    
    //Catalogo de Remitentes
    public ArrayList<HashMap<String, Object>> getClientsRemiten_PaginaGrid(String data_string, int offset, int pageSize, String orderBy , String asc);
    public ArrayList<HashMap<String, Object>> getClientsRemiten_Datos(Integer id);
    
    //Catalogo de Destinatarios
    public ArrayList<HashMap<String, Object>> getClientsDest_PaginaGrid(String data_string, int offset, int pageSize, String orderBy , String asc);
    public ArrayList<HashMap<String, Object>> getClientsDest_Datos(Integer id);
    public ArrayList<HashMap<String, Object>> getBuscadorServiciosAdicionales(String sku, String descripcion, Integer id_empresa);
    
    //Catalogo de Agentes Aduanales
    public ArrayList<HashMap<String, Object>> getClientsAgenAduanal_PaginaGrid(String data_string, int offset, int pageSize, String orderBy , String asc);
    public ArrayList<HashMap<String, Object>> getClientsAgenAduanal_Datos(Integer id);
    
    //Aplicativo de Asignacion de Remitentes a Clientes
    public ArrayList<HashMap<String, Object>> getClientsAsignaRem_PaginaGrid(String data_string, int offset, int pageSize, String orderBy, String asc);
    public ArrayList<HashMap<String, Object>> getClientsAsignaRem_Datos(Integer id);
    public ArrayList<HashMap<String, Object>> getClientsAsignaRem_RemitentesAsignados(Integer id);
    public ArrayList<HashMap<String, Object>> getBuscadorRemitentes(String cadena, Integer filtro, Integer id_empresa, Integer id_sucursal);
    public ArrayList<HashMap<String, Object>> getDatosClienteByNoRemitente(String no_control, Integer id_empresa, Integer id_sucursal);
    
    //Aplicativo de Asignacion de Destinatarios a Clientes
    public ArrayList<HashMap<String, Object>> getClientsAsignaDest_PaginaGrid(String data_string, int offset, int pageSize, String orderBy, String asc);
    public ArrayList<HashMap<String, Object>> getClientsAsignaDest_Datos(Integer id);
    public ArrayList<HashMap<String, Object>> getClientsAsignaDest_DestinatariosAsignados(Integer id);
    public ArrayList<HashMap<String, Object>> getBuscadorDestinatarios(String cadena, Integer filtro, Integer id_empresa, Integer id_sucursal);
    public ArrayList<HashMap<String, Object>> getDatosByNoDestinatario(String no_control, Integer id_empresa, Integer id_sucursal);
    
    //Aplicativo de Asignacion de Agentes Aduanales a Clientes
    public ArrayList<HashMap<String, Object>> getClientsAsignaAgenA_PaginaGrid(String data_string, int offset, int pageSize, String orderBy, String asc);
    public ArrayList<HashMap<String, Object>> getClientsAsignaAgenA_Datos(Integer id);
    public ArrayList<HashMap<String, Object>> getClientsAsignaAgenA_Asignados(Integer id);
    public ArrayList<HashMap<String, Object>> getBuscadorAgentesAduanales(String cadena, Integer filtro, Integer id_empresa, Integer id_sucursal);
    public ArrayList<HashMap<String, Object>> getDatosByNoAgenteAduanal(String no_control, Integer id_empresa, Integer id_sucursal);
    
    //Calcular años a mostrar en el reporte de Saldo Por mes
    public ArrayList<HashMap<String, Object>> getCxc_AnioReporteSaldoMensual();
    public ArrayList<HashMap<String, String>> getCxc_DatosReporteSaldoMensual(Integer tipo_reporte, String cliente, String fecha_corte,Integer id_empresa);
    
    
    public ArrayList<HashMap<String,String>> getComparativoVentas(String cliente, Integer anio_ini, Integer anio_fin, Integer id_emp);
    public ArrayList<HashMap<String, String>> getComparativoVentasProducto(String producto, Integer anio_inicial, Integer anio_final,Integer tipo_producto, Integer familia,Integer subfamilia,Integer id_empresa);
    
    //Reporte de Anual de ventas por cliente
    public ArrayList<HashMap<String,String>> getVentasAnualesCliente(Integer anio, Integer id_empresa);
    
    //Catalogo de Descuentos de Clientes
    public ArrayList<HashMap<String, Object>> getClientstDescuentos_PaginaGrid(String data_string, int offset, int pageSize, String orderBy , String asc);
    public ArrayList<HashMap<String, Object>> getClientstDescuentos_Datos(Integer id);
    
    //Reporte de Ieps Cobrado por factura
    public ArrayList<HashMap<String, String>> getDatosReporteIepsCobrado(ArrayList<HashMap<String, String>> listaIeps, String ciente, String finicial, String ffinal, Integer id_empresa);
    public ArrayList<HashMap<String, String>> getListaIeps(Integer id_empresa);
    
    //Reporte de Ieps Cobrado por Cliente
    public ArrayList<HashMap<String, String>> getDatosCxcReporteIepsCobradoPorCliente(ArrayList<HashMap<String, String>> listaIeps, String ciente, String finicial, String ffinal, Integer id_empresa);

    public String q_serie_folio(Integer id_usuario);
}
