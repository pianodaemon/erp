package com.agnux.kemikal.interfacedaos;


import com.agnux.kemikal.controllers.PotCatCusorder;
import java.util.ArrayList;
import java.util.HashMap;


public interface PocInterfaceDao{
    public ArrayList<HashMap<String, String>> getUsos();
    public ArrayList<HashMap<String, String>> getMetodos();
    public HashMap<String, String> selectFunctionValidateAaplicativo(String data, Integer idApp, String extra_data_array);
    public String selectFunctionForThisApp(String campos_data, String extra_data_array);
    public int countAll(String data_string);

    public ArrayList<HashMap<String, String>> getBuscadorClientes(String cadena, Integer filtro, Integer id_empresa, Integer id_sucursal, String permite_descto);
    public ArrayList<HashMap<String, String>> getDatosClienteByNoCliente(String no_control,  Integer id_empresa, Integer id_sucursal, String permite_descto);
    public int getTipoClient(Integer idClient);//obtiene el tipo de cliente
    public ArrayList<HashMap<String, String>> getBuscadorProspectos(String cadena, Integer filtro, Integer id_empresa, Integer id_sucursal);
    public ArrayList<HashMap<String, String>> getDatosProspectoByNoControl(String no_control, Integer id_empresa, Integer id_sucursal);
    public ArrayList<HashMap<String, String>> getBuscadorProductos(String sku, String tipo, String descripcion, Integer id_empresa);
    public ArrayList<HashMap<String, String>> getProductoTipos();
    public ArrayList<HashMap<String, String>> getPresentacionesProducto(String sku,String lista_precio, Integer id_empresa);
    public ArrayList<HashMap<String, String>> getBuscadorUnidades(String no_eco, String marca, Integer id_empresa, Integer id_sucursal);
    public ArrayList<HashMap<String, String>> getDatosUnidadByNoEco(String no_economico, Integer id_empresa, Integer id_sucursal);
    public ArrayList<HashMap<String, String>> getBuscadorOperadores(String no_operador, String nombre, Integer id_empresa, Integer id_sucursal);
    public ArrayList<HashMap<String, String>> getDatosOperadorByNo(String no_operador, Integer id_empresa, Integer id_sucursal);
    public ArrayList<HashMap<String, Object>> getBuscadorAgentesAduanales(String cadena, Integer filtro, Integer id_empresa, Integer id_sucursal);
    public ArrayList<HashMap<String, Object>> getDatosByNoAgenteAduanal(String no_control, Integer id_empresa, Integer id_sucursal);
    public ArrayList<HashMap<String, Object>> getBuscadorRemitentes(String cadena, Integer filtro, Integer id_empresa, Integer id_sucursal);
    public ArrayList<HashMap<String, Object>> getDatosClienteByNoRemitente(String no_control, Integer id_empresa, Integer id_sucursal);
    public ArrayList<HashMap<String, Object>> getDatosByNoDestinatario(String no_control, Integer id_empresa, Integer id_sucursal);
    public ArrayList<HashMap<String, Object>> getBuscadorDestinatarios(String cadena, Integer filtro, Integer id_empresa, Integer id_sucursal);
    public ArrayList<HashMap<String, String>> getUnidadesMedida();
    //Obtiene todos los impuestos del ieps(Impuesto Especial sobre Productos y Servicios)
    public ArrayList<HashMap<String, String>> getIeps(Integer idEmp, Integer idSuc);
    
    public ArrayList<HashMap<String, String>> getVerificarImpuesto(Integer idSuc, Integer idTipoClient, ArrayList<HashMap<String, String>> productos);
    public ArrayList<HashMap<String, String>> getMonedas();
    public ArrayList<HashMap<String, String>> getAgentes(Integer id_empresa, Integer id_sucursal, boolean obtener_todos);
    public ArrayList<HashMap<String, String>> getCondicionesDePago();
    public Double getTipoCambioActual();
    public HashMap<String, String> getTipoCambioActualPorIdMoneda(Integer idMoneda);
    
    public ArrayList<HashMap<String, String>> getValoriva(Integer id_sucursal);
    public ArrayList<HashMap<String, String>> getValorivaById(Integer idImpto);

    //Metodos para aplicativo pedidos y autorizacion de pedidos
    public ArrayList<HashMap<String, Object>> getPocPedidos_PaginaGrid(String data_string,int offset, int pageSize, String orderBy , String asc);
    public ArrayList<HashMap<String, String>> getPocPedido_Datos(Integer id_pedido);
    public ArrayList<HashMap<String, String>> getPocPedido_DatosGrid(Integer id_pedido);
    public ArrayList<HashMap<String, String>> getPocPedido_DatosCotizacion(String folio_cotizacion, Integer id_empresa);
    public ArrayList<HashMap<String, String>> getPocPedido_DatosCotizacionGrid(String id_cot);
    
    public ArrayList<HashMap<String, String>> getPocPedido_Almacenes(Integer id_sucursal);
    public HashMap<String, String>  getPocPedido_Parametros(Integer id_emp, Integer id_suc);
    public ArrayList<HashMap<String, String>> getPocPedido_DireccionesFiscalesCliente(Integer id_cliente);
    public HashMap<String, String> getDatosPDF(Integer id_pedido);
    
    //Estos metodos se utilizan para obtener datos de transportista para el pedido
    public ArrayList<HashMap<String, String>> getPocPedido_DatosTrans(Integer id_pedido);
    public ArrayList<HashMap<String, String>> getPaises();
    public ArrayList<HashMap<String, String>> getEntidadesForThisPais(String id_pais);
    public ArrayList<HashMap<String, String>> getLocalidadesForThisEntidad(String id_pais,String id_entidad);
    
    
    //metodos para aplicativo Remisiones de CLientes
    public ArrayList<HashMap<String, Object>> getRemisiones_PaginaGrid(String data_string,int offset, int pageSize, String orderBy , String asc);
    public ArrayList<HashMap<String, String>> getRemisiones_Datos(Integer id_remision);
    public ArrayList<HashMap<String, String>> getRemisiones_DatosGrid(Integer id_remision);
    public ArrayList<HashMap<String, String>> getMetodosPago(Integer empresaId);

    public HashMap<String, String> getRemisiones_DatosPdf(Integer id_remision);
    public ArrayList<HashMap<String, String>> getRemisiones_ConceptosPdf(Integer id_remision, String rfc_empresa);

    //NLE: Métodos para Remisiones IMSS
    public ArrayList<HashMap<String, String>> getStatusRemisionIMSS();
    public HashMap<String, String> setRemisionIMSS(String data_string, int app_selected);
    public HashMap<String, String> logicDeleteRemisionIMSS(String data_string, int app_selected);
    public ArrayList<HashMap<String, Object>> getRemisionesIMSS_PaginaGrid(String data_string, int offset, int pageSize, String orderBy, String asc);
    public HashMap<String, String> getFormRemisionIMSS(Integer id_remision);

    //metodos para generar reporte Pedidos
    public ArrayList<HashMap<String,String>>getReportePedidos(Integer opcion, Integer agente, String cliente, String fecha_inicial, String fecha_final,Integer id_empresa);
    //metodo para alimentar el select de agentes
    public ArrayList<HashMap<String,String>> getAgente(Integer id_empresa);
    //metodo para alimentar el select de los estados de los pedidos
    public ArrayList<HashMap<String,String>> getEstadoPedido();
    
    //reporte de Articulos Reservados   pocDao(Proceso Comercial).
    public ArrayList<HashMap<String, String>> getReporteArticulosReservados( Integer id_empresa, Integer id_usuario, String folio_pedido, String codigo, String descripcion);

    //trae la lista de precios
    public ArrayList<HashMap<String, String>> getListaPrecio(Integer lista_precio);


    //metodos para aplicativo de Cotizaciones
    public ArrayList<HashMap<String, Object>> getCotizacion_PaginaGrid(String data_string,int offset, int pageSize, String orderBy , String asc);
    public ArrayList<HashMap<String, String>> getCotizacion_Datos(Integer id_cot);
    public ArrayList<HashMap<String, String>> getCotizacion_DatosCliente(Integer id_cot);
    public ArrayList<HashMap<String, String>> getCotizacion_DatosProspecto(Integer id_cot);
    public ArrayList<HashMap<String, String>> getCotizacion_DatosGrid(Integer id_cot);
    public ArrayList<HashMap<String, String>> getCotizacion_CondicionesComerciales(Integer id_emp);
    public ArrayList<HashMap<String, String>> getCotizacion_PolitizasPago(Integer id_emp);
    public ArrayList<HashMap<String, String>> getCotizacion_Incoterms(Integer id_empresa, Integer id_cot);
    public HashMap<String, String> getUserRol(Integer id_user);
    public HashMap<String, String> getCotizacion_Saludo(Integer id_empresa);
    public HashMap<String, String> getCotizacion_Despedida(Integer id_empresa);


    //aplicativo actualizador de Saludo y Despedida para Cotizaciones
    public ArrayList<HashMap<String, Object>> getCotizacionSaludoDespedida_PaginaGrid(String data_string, int offset, int pageSize, String orderBy, String asc);
    public ArrayList<HashMap<String, String>> getCotizacionSaludoDespedida_Datos(Integer id);


    //metodos para catalogo de incoterms para cotizaciones
    public ArrayList<HashMap<String, Object>> getCotIncoterms_PaginaGrid(String data_string,int offset, int pageSize, String orderBy , String asc);
    public ArrayList<HashMap<String, String>> getCotIncoterms_Datos(Integer id);


    //Catalogo de politicas de Pago
    public ArrayList<HashMap<String, Object>> getCotPoliticas_de_Pago_PaginaGrid(String data_string, int offset, int pageSize, String orderBy, String asc);
    public ArrayList<HashMap<String, String>> getCotPoliticas_de_Pago_Datos(Integer id);

    //Catalogo de condiciones de Venta
    public ArrayList<HashMap<String, Object>> getCotCondiciones_comerciales_PaginaGrid(String data_string, int offset, int pageSize, String orderBy, String asc);
    public ArrayList<HashMap<String, String>> getCotCondiciones_Comerciales_Datos(Integer id);
    
    //Validar usuario que autoriza
    public HashMap<String, Object> getValidarUser(String username, String password, String id_suc);
    
    //Verifica si la Cotizacion no tiene precios pendientes de utorizar
    public HashMap<String, Object> getVerificarCotizacion(String folio_cotizacion, Integer id_suc);

    public HashMap<String, String> poc_val_cusorder(Integer usr_id, String curr_val, String date_lim, Integer pay_met, String account, String matrix);

    public String poc_cat_cusorder(PotCatCusorder pc);
}
