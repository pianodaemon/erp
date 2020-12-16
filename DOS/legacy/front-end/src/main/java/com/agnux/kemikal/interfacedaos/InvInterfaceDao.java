package com.agnux.kemikal.interfacedaos;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;

public interface InvInterfaceDao {
    public HashMap<String, String> selectFunctionValidateAaplicativo(String data, Integer idApp, String extra_data_array);
    public String selectFunctionForThisApp(String campos_data, String extra_data_array);
    public int countAll(String data_string);
    public ArrayList<HashMap<String, String>> getInvParametros(Integer idEmp);
    //Obtiene todos los impuestos del ieps(Impuesto Especial sobre Productos y Servicios)
    public ArrayList<HashMap<String, String>> getIeps(Integer idEmp, Integer idSuc);
    public ArrayList<HashMap<String, String>> getTasasRetencionIva(Integer idEmp, Integer idSuc);
    
    public String selectFunctionForApp_MovimientosInventario(String campos_data, String extra_data_array);
    public ArrayList<HashMap<String, String>> getBuscadorProductos(String sku, String tipo, String descripcion,Integer id_empresa);
    public ArrayList<HashMap<String, String>> getDataProductBySku(String codigo, Integer id_empresa);
    
    public ArrayList<HashMap<String, String>> selectFunctionForInvReporte(Integer id_app, String campos_data);
    
    public ArrayList<HashMap<String, String>> getBuscadorProveedores(String rfc, String email, String razon_social, Integer id_empresa);
    public ArrayList<HashMap<String, String>> getDatosProveedorByNoProv(String noProveedor, Integer id_empresa);
    public ArrayList<HashMap<String, String>> getSucursales(Integer id_empresa);
    public ArrayList<HashMap<String, String>> getAlmacenes(Integer id_empresa);
    //Se utiliza en traspasos, reporte de existencias, reporte de existencias por Lote, Facturas Compras(Entradas Mercnacias)
    public ArrayList<HashMap<String, String>> getAlmacenes2(Integer id_empresa);
    //Obtiene almacenes de la sucursal especificada.
    public ArrayList<HashMap<String, String>> getAlmacenesSucursal(Integer id_empresa, Integer id_sucursal);
    public ArrayList<HashMap<String, String>> getMonedas();
    public ArrayList<HashMap<String, String>> getMonedas2();//solo se utiliza en listas de precios
    public ArrayList<HashMap<String, String>> getAllTiposMovimientoInventario(Integer id_empresa);
    
    public ArrayList<HashMap<String, String>> getPaises();
    public ArrayList<HashMap<String, String>> getEntidadesForThisPais(String id_pais);
    public ArrayList<HashMap<String, String>> getLocalidadesForThisEntidad(String id_pais,String id_entidad);
    
    
    //catalogo de productos
    public ArrayList<HashMap<String, Object>> getProductos_PaginaGrid(String data_string, int offset, int pageSize, String orderBy , String asc);
    public ArrayList<HashMap<String, String>> getProducto_Datos(Integer id_producto);
    public ArrayList<HashMap<String, String>> getProducto_Lineas(Integer id_empresa);
    public ArrayList<HashMap<String, String>> getProducto_Grupos(Integer id_empresa);
    public ArrayList<HashMap<String, String>> getProducto_Marcas(Integer id_empresa);
    public ArrayList<HashMap<String, String>> getProducto_Subfamilias(Integer id_empresa);
    public ArrayList<HashMap<String, String>> getProducto_ClasificacionStock(Integer id_empresa);
    public ArrayList<HashMap<String, String>> getProducto_Clases(Integer id_empresa);
    public ArrayList<HashMap<String, String>> getProducto_Tipos();
    public ArrayList<HashMap<String, String>> getProducto_TiposInventariable();
    public ArrayList<HashMap<String, String>> getProducto_Ingredientes(Integer id_producto);
    public ArrayList<HashMap<String, String>> getProducto_PresentacionesON(Integer id_producto);
    public ArrayList<HashMap<String, String>> getProducto_Unidades();
    public ArrayList<HashMap<String, String>> getProducto_CuentasMayor(Integer id_empresa);
    public ArrayList<HashMap<String, String>> getProducto_Presentaciones(Integer id_producto);
    public ArrayList<HashMap<String, String>> getProducto_sku(String sku,Integer id_usuario);
    public ArrayList<HashMap<String, String>> getProducto_Subfamilias(Integer id_empresa,String familia_id);
    public ArrayList<HashMap<String, String>> getProducto_CuentasContables(Integer cta_mayor, Integer detalle, String clasifica, String cta, String scta, String sscta, String ssscta, String sssscta, String descripcion, Integer id_empresa );
    public ArrayList<HashMap<String, String>> getProducto_DatosContabilidad(Integer id_producto);
    
    
    
    //entradas de mercancia
    public ArrayList<HashMap<String, Object>> getEntradas_PaginaGrid(String data_string, int offset, int pageSize, String orderBy , String asc);
    public ArrayList<HashMap<String, String>> geteEntradas_Fleteras(Integer id_empresa, Integer id_sucursal );
    public ArrayList<HashMap<String, String>> getEntrada_Datos(Integer id);
    public ArrayList<HashMap<String, String>> getEntradas_DatosProveedor(Integer id_proveedor);
    public ArrayList<HashMap<String, String>> getEntradas_DatosGrid(Integer id);
    public ArrayList<HashMap<String, String>> getEntradas_Impuestos();//catalogo  de productos y entradas de mercancias
    public ArrayList<HashMap<String, String>> getEntradas_TasaFletes();
    public ArrayList<HashMap<String, String>> getEntradas_PresentacionesProducto(String sku, Integer id_empresa);
    public ArrayList<HashMap<String, String>> getEntradas_TipoCambio(String fecha);
    public ArrayList<HashMap<String, String>> getEntradas_DatosOrdenCompra(String orden_compra, Integer id_empresa);
    public ArrayList<HashMap<String, String>> getEntradas_DetallesOrdenCompra(Integer id_orden_compra);
    public ArrayList<HashMap<String, String>> getProveedor_Contacto(Integer idProveedor);
    
    /*
    //Traspaso de mercancia
    public ArrayList<HashMap<String, String>> getTraspaso_Tipos();
    public ArrayList<HashMap<String, Object>> getTraspaso_PaginaGrid(String data_string, int offset, int pageSize, String orderBy , String asc);
    public ArrayList<HashMap<String, String>> getTraspaso_Datos(Integer id_traspaso);
    public ArrayList<HashMap<String, String>> getTraspaso_DatosGrid(Integer id_traspaso);
    */
    
    //reporte de existencias en invetario
    public ArrayList<HashMap<String, String>> getDatos_ReporteExistencias(Integer id_isuario, Integer id_almacen, String codigo_producto, String descripcion,Integer tipo);
    
    
    //catalogo tipos de movimiento
    public ArrayList<HashMap<String, Object>> getTipoMovimientosInventaioGrid(String data_string, int offset, int pageSize, String orderBy , String asc);
    public ArrayList<HashMap<String, String>> getTipoMovInv_Datos(Integer id);
    
    
    //catalogo de almacenes
    public ArrayList<HashMap<String, String>> getAlmacennes_TiposAlmacen();
    public ArrayList<HashMap<String, Object>> getAlmacenes_PaginaGrid(String data_string, int offset, int pageSize, String orderBy , String asc);
    public ArrayList<HashMap<String, String>> getAlmacenes_Datos(Integer id);
    public ArrayList<HashMap<String, String>> getAlmacenes_Sucursales(Integer id_almacen, Integer id_empresa);
    public ArrayList<HashMap<String, String>> getAlmacenes_SucursalesON(Integer id_almacen, Integer id_empresa);
    
    
    //catalogo tipos de invsecciones
    public ArrayList<HashMap<String, Object>> getInvSeccionesGrid(String data_string, int offset, int pageSize, String orderBy , String asc);
    public ArrayList<HashMap<String, String>> getInvSecciones_Datos(Integer id);
    
    //catalogo de inventario Marcas
     public ArrayList<HashMap<String, Object>> getMarcas_PaginaGrid(String data_string, int offset, int pageSize, String orderBy , String asc);
     public ArrayList<HashMap<String, String>> getMarcas_Datos(Integer id_agente);
     
    //catalogo de InvLineas
    public ArrayList<HashMap<String, Object>> getInvProdLineas_Grid(String data_string, int offset, int pageSize, String orderBy , String asc);
    public ArrayList<HashMap<String, String>> getInvProdLineas_Datos(Integer id);
    public ArrayList<HashMap<String, String>> getInvProdLineas_Secciones(Integer id_empresa);
    public ArrayList<HashMap<String, String>> getInvProdLineas_Marcas();
    public ArrayList<HashMap<String, String>> getInvProdLineas_LM(Integer id);
    
    //catalogo de Familias de Productos
    public ArrayList<HashMap<String, Object>> getInvProdFamilias_PaginaGrid(String data_string, int offset, int pageSize, String orderBy , String asc);
    public ArrayList<HashMap<String, String>> getInvProdFamilias_Datos(Integer id_familia);
    public ArrayList<HashMap<String, String>> getInvProdFamilias_TiposProd();//add por paco
    
    
    
    //catalogo de subFamilias de Productos
    public ArrayList<HashMap<String, Object>> getInvProdSubFamilias_PaginaGrid(String data_string, int offset, int pageSize, String orderBy , String asc);
    //se utiliza en catalogo de Productos y catalogo de Subfamilias
    public ArrayList<HashMap<String, String>> getInvProdSubFamilias_Familias(Integer id_empresa);
    public ArrayList<HashMap<String, String>> getInvProdSubFamilias_Datos(Integer id_subfamilia);
    public ArrayList<HashMap<String, String>> getInvProdSubFamiliasByTipoProd(Integer id_empresa, String prod_tipo);
    
    
    //Reporte de Compras Netas Producto
    public ArrayList<HashMap<String, String>> getDatos_ReporteComprasNetasProducto(Integer tipo_reporte, String proveedor, String producto,String fecha_inicial,String fecha_final, Integer id_empresa);
    
    
    //catalogo de inventario Zonas
    public ArrayList<HashMap<String, Object>> getZonas_PaginaGrid(String data_string, int offset, int pageSize, String orderBy , String asc);
    public ArrayList<HashMap<String, String>> getZonas_Datos(Integer id_agente);
    
    
    //catalogo de inventario Producto plazas
    public ArrayList<HashMap<String, Object>> getPlazas_PaginaGrid(String data_string, int offset, int pageSize, String orderBy , String asc);
    public ArrayList<HashMap<String, String>> getPlazas_Datos(Integer id_agente);
    public ArrayList<HashMap<String, String>> getPlazas_zonas();
    
    
    //catalogo de inventario Producto grupos
    public ArrayList<HashMap<String, Object>> getProductoGrupos_PaginaGrid(String data_string, int offset, int pageSize, String orderBy , String asc);
    public ArrayList<HashMap<String, String>> getProductosGrupos_Datos(Integer id_producto);
    
    
    //catalogo de inventario clasificaciones
    public ArrayList<HashMap<String, Object>> getInvClas_PaginaGrid(String data_string, int offset, int pageSize, String orderBy , String asc);
    public ArrayList<HashMap<String, String>> getInvClas_Datos(Integer id_unidad);
    
    //catalogo de clasificaion de  stock
    public ArrayList<HashMap<String, Object>> getStock_PaginaGrid(String data_string, int offset, int pageSize, String orderBy , String asc);
    public ArrayList<HashMap<String, String>> getStock_Datos(Integer id_unidad);
    
    
    //catalogo tipos de invcom(invcom)
    public ArrayList<HashMap<String, Object>> getInvComGrid(String data_string, int offset, int pageSize, String orderBy , String asc);
    public ArrayList<HashMap<String, String>> getInvCom_Datos(Integer id);
    
    
    //catalogo tipos de invpre(inveprecios)
    public ArrayList<HashMap<String, Object>> getInvPreGrid(String data_string, int offset, int pageSize, String orderBy , String asc);
    public ArrayList<HashMap<String, String>> getInvPre_Datos(Integer id);
    public ArrayList<HashMap<String, String>> getInvPre_MonedaListas(Integer id_empresa);
    
    
    //extrae todas la ssucursale para asignarlas a una zona
    public ArrayList<HashMap<String, String>> getPlazas(Integer id_empresa);
    public ArrayList<HashMap<String, String>> getPlazasAsignadas(Integer id_empresa,Integer id_sucursal);
    public ArrayList<HashMap<String, String>> getPlazasNoAsignadas(Integer id_empresa,Integer id_sucursal);
    
    //catalogo tipos de invpreofe(Catalogo de promociones)
    public ArrayList<HashMap<String, Object>> getInvPreOfeGrid(String data_string, int offset, int pageSize, String orderBy , String asc);
    public ArrayList<HashMap<String, String>> getInvPreOfe_Datos(Integer id);
    
    
    //catalogo InvOrdPreSuben(Catalogo de InvOrden PreSubensamble)
    public ArrayList<HashMap<String, Object>> getInvOrdPreSubenGrid(String data_string, int offset, int pageSize, String orderBy , String asc);
    public ArrayList<HashMap<String, String>> getInvOrdPreSuben_Datos(String id);
    public ArrayList<HashMap<String, String>> getInvDetalleOrdPreSuben(String id);
    public ArrayList<HashMap<String, String>> getInvOrdPreSubenDatosProductos(String sku,Integer id_empresa);
    public ArrayList<HashMap<String, String>> getInvOrdPreSubenDatosComProd(String sku, Integer id_empresa);
    public HashMap<String, String> getInvOrdPreSuben_IdAlmacenProd(Integer id_empresa);
    
    //catalogo de InvOrdSuben(Orden Subensamble)
    public ArrayList<HashMap<String, Object>> getInvOrdSubenGrid(String data_string, int offset, int pageSize, String orderBy , String asc);
    public ArrayList<HashMap<String, String>> getInvOrdSuben_Datos(String id);
    public ArrayList<HashMap<String, String>> getInvDetalleOrdSuben(String id);
    public ArrayList<HashMap<String, String>> getInvOrdPreSubenDatosComponentesPorProducto(String id);
    
    
    //catalogo de inventario Unidades
    public ArrayList<HashMap<String, Object>> getUnidades_PaginaGrid(String data_string, int offset, int pageSize, String orderBy , String asc);
    public ArrayList<HashMap<String, String>> getUnidades_Datos(Integer id_unidad);
    
    //catalogo de inventario Presentaciones
    public ArrayList<HashMap<String, Object>> getPresentaciones_PaginaGrid(String data_string, int offset, int pageSize, String orderBy , String asc);
    public ArrayList<HashMap<String, String>> getPresentaciones_Datos(Integer id_agente);
    
    //catalogo de formulas
    public ArrayList<HashMap<String, Object>> getFormulas_PaginaGrid(String data_string, int offset, int pageSize, String orderBy , String asc);
    public ArrayList<HashMap<String, String>> getFormulas_Datos(String id_formula, String id_nivel);
    public ArrayList<HashMap<String, String>> getFormulas_DatosMinigrid(String id_formula, String id_nivel);
    public ArrayList<HashMap<String, String>> getFormulas_DatosProductoSaliente(String id_formula , String id_nivel);
    
    public ArrayList<HashMap<String, String>> getProducto_Tipos_para_formulas(Integer buscador_producto);
    ArrayList<HashMap<String, String>> getFormulas_sku(String sku, Integer id_usuario);
    
    //reporte de kits que se puedn formar de acuerdo a la existencia
    public ArrayList<HashMap<String, String>> getKits( Integer id_empresa, Integer id_usuario,String codigo, String descripcion);
    
    
    
    //Para pdf de formulas hecho por paco
    public ArrayList<HashMap<String, Object>> getInv_ListaProductosFormulaPdf(Integer formula_id);
    public HashMap<String, String> getInv_DatosFormulaPdf(Integer formula_id);
    public ArrayList<HashMap<String, String>> getInv_DatosFormulaEspecificacionesPdf(Integer formula_id);
    
    public ArrayList<HashMap<String, String>> getInv_DatosFormulaProcedidmientoPdf(Integer formula_id);
    
    
    
    //Ordenes de Entrada
    public ArrayList<HashMap<String, Object>> getInvOrdenEntrada_PaginaGrid(String data_string, int offset, int pageSize, String orderBy , String asc);
    public ArrayList<HashMap<String, String>> getInvOrdenEntrada_Datos(Integer id);
    public ArrayList<HashMap<String, String>> getInvOrdenEntrada_DatosGrid(Integer id);
    public ArrayList<HashMap<String, String>> getInvOrdenEntrada_DatosGridLotes(Integer id);
    public HashMap<String, String> getInvOrdenEntrada_Datos_PDF(Integer id);
    
    
    //Ordenes de Salida
    public ArrayList<HashMap<String, Object>> getInvOrdenSalida_PaginaGrid(String data_string, int offset, int pageSize, String orderBy, String asc);
    public ArrayList<HashMap<String, String>> getInvOrdenSalida_Datos(Integer id);
    public ArrayList<HashMap<String, String>> getInvOrdenSalida_DatosGrid(Integer id);
    //este metodo se utiliza en Ordenes de Salida Y Ordenes de Traspaso
    public ArrayList<HashMap<String, String>> getInvOrdenSalida_DatosLote(String no_lote, Integer id_producto, Integer id_usuario);
    public ArrayList<HashMap<String, String>> getInvOrdenSalida_DatosGridLotes(Integer id);
    public HashMap<String, String> getInvOrdenSalida_Datos_PDF(Integer id);
    public ArrayList<HashMap<String, String>> getEntradas_DatosCliente(Integer id_cliente);
    
    
    //Ajustes de Inventario
    public ArrayList<HashMap<String, Object>> getInvAjustes_PaginaGrid(String data_string, int offset, int pageSize, String orderBy, String asc);
    public ArrayList<HashMap<String, String>> getInvAjustes_Datos(Integer id);
    public ArrayList<HashMap<String, String>> getInvAjustes_DatosGrid(Integer id, String fecha, Integer id_almacen);
    //este buscador de Productos es personalizado para ajustes
    public ArrayList<HashMap<String, String>> getInvAjustes_BuscadorProductos(String sku, String tipo, String descripcion,Integer id_empresa, Integer id_almacen, Integer ano_actual);
    public ArrayList<HashMap<String, String>> getInvAjustes_DatosProducto(String sku,Integer id_empresa, Integer id_almacen, Integer ano_actual);
    public ArrayList<HashMap<String, String>> getInvAjustes_TiposMovimiento(Integer id_empresa);
    public ArrayList<HashMap<String, String>> getInvAjustes_CostoPromedioActual(Integer id_prod, String fecha_actual);
    public HashMap<String, String> getInvAjustes_DatosPDF(Integer id);
    
    
    //PRODUCTOS EQUIVALENTES    INV INTERFACE
    public ArrayList<HashMap<String, String>> getProductoEquivalente_Datos(Integer id);
    public ArrayList<HashMap<String, Object>> getProductosEquivalentes_PaginaGrid(String data_string, int offset, int pageSize, String orderBy, String asc);
    public ArrayList<HashMap<String, String>> getProductos_sku(String sku, Integer id_usuario);
    public ArrayList<HashMap<String, String>> getProducto_Tipos_para_productos_equivalentes(String buscador_producto);
    public ArrayList<HashMap<String, String>> getProductosEquivalentes_Datos(Integer id);
    public ArrayList<HashMap<String, String>> getProductosEquivalentes_DatosMinigrid(String id);
    public ArrayList<HashMap<String, String>> getBuscadorProductosEquivalentes(String sku, String tipo, String descripcion, Integer id_empresa, String id_prod);
    
    
    //Catalogoo de Impresion de Etiquetas.
    public ArrayList<HashMap<String, String>> getBuscadorEntradas(String folio, String fecha_inicial, String fecha_final,Integer tipo_origen,Integer id_empresa);
    public ArrayList<HashMap<String, String>> getgridEntradas(String folio, String fecha_inicial, String fecha_final,Integer tipo_origen,Integer id_empresa);
    public ArrayList<HashMap<String, String>> getEtiquetas_Entrada(Integer id_etiqueta,Integer tipo_origen,Integer id_empresa);
    public ArrayList<HashMap<String, Object>> getEtiquetas_PaginaGrid(String data_string, int offset, int pageSize, String orderBy , String asc);
    public ArrayList<HashMap<String, String>> getEtiquetas_Datos_grid(Integer id_etiqueta, Integer tipo_origen);
    public ArrayList<HashMap<String, String>> getEtiquetas_Datos_header(Integer id);
    public LinkedHashMap<String,Object> getDatosEtiquetaLote_produccion(String lote_interno, Integer id_etiquetas, Integer id_medida_etiqueta);
    public LinkedHashMap<String,Object> getDatosEtiquetaLote_requisicion(String id_etiquetas_detalle);
    
    
    //metodos para Ordenes de Devolucion
    public ArrayList<HashMap<String, Object>> getInvOrdenDev_PaginaGrid(String data_string, int offset, int pageSize, String orderBy, String asc);
    public ArrayList<HashMap<String, String>> getInvOrdenDev_Datos(Integer id);
    public ArrayList<HashMap<String, String>> getInvOrdenDev_DatosGridNcto(String serieFolioNcto, Integer idClie);
    public ArrayList<HashMap<String, String>> getInvOrdenDev_DatosGridOsal(Integer tipoDoc, String folioDoc, Integer clienteId);
    public ArrayList<HashMap<String, String>> getInvOrdenDev_DatosGridLotes(Integer id);
    
    
    
    //METODO PARA EL REPORTE DE EXISTENCIAS EN LOTES
    public ArrayList<HashMap<String, String>> getReporteExistenciasLotes_Datos(Integer id_almacen, String codigo_producto, String descripcion,Integer tipo, String lote_interno );
    public ArrayList<HashMap<String, String>> getReporteExistenciasLotes_MedidasEtiquetas();
    public LinkedHashMap<String, Object> getDatosEtiquetaLote(Integer id_lote, Integer tipo_producto, Integer id_medida_etiqueta);
    
    
    //METODOS PARA TRASPASOS
    public ArrayList<HashMap<String, String>> getInvTraspasos_DatosProducto(String sku,Integer id_empresa, Integer id_almacen, Integer ano_actual);
    public ArrayList<HashMap<String, String>> getInvTraspasos_ExistenciaPresentacion(Integer id_prod, Integer id_pres, Integer id_alm);
    public ArrayList<HashMap<String, Object>> getInvTraspasos_PaginaGrid(String data_string, int offset, int pageSize, String orderBy, String asc);
    public ArrayList<HashMap<String, String>> getInvTraspasos_Datos(Integer id);
    public ArrayList<HashMap<String, String>> getInvTraspasos_DatosGrid(Integer id, Integer id_almacen_origen);
    public HashMap<String, String> getInvTraspasos_DatosPDF(Integer id);
    public ArrayList<HashMap<String, String>> getInvTraspasos_DatosGridPDF(Integer id_traspaso);
    
    
    //METODOS PARA ORDENES DE TRASPASO
    public ArrayList<HashMap<String, Object>> getInvoOrdenTras_PaginaGrid(String data_string, int offset, int pageSize, String orderBy, String asc);
    public ArrayList<HashMap<String, String>> getInvoOrdenTras_Datos(Integer id);
    public ArrayList<HashMap<String, String>> getInvoOrdenTras_DatosGrid(Integer id);
    public ArrayList<HashMap<String, String>> getInvoOrdenTras_GridLotes(Integer id);
    public HashMap<String, String> getInvOrdenTras_DatosPDF(Integer id);
    
    
    //Metodos para Aplicativo de Control de Costos
    public ArrayList<HashMap<String, Object>> getInvControlCostos_PaginaGrid(String data_string, int offset, int pageSize, String orderBy, String asc);
    public ArrayList<HashMap<String, String>> getBuscadorProductosParaControlCostos(String marca, String familia, String subfamilia, String sku, String tipo, String descripcion,Integer id_empresa);
    public ArrayList<HashMap<String, String>>  getInvControlCostos_Anios();
    
    
    public ArrayList<HashMap<String, Object>> getInvActualizaPrecio_PaginaGrid(String data_string, int offset, int pageSize, String orderBy, String asc);
    
    //reporte inv movimientos
    public ArrayList<HashMap<String, String>> getMovimientos(Integer id_tipo_movimiento, Integer id_alamacen,String codigo, String descripcion,String fecha_inicial,String fecha_final,Integer id_empresa, Integer id_usuario);
    
    
    //Aplicativo de Captura de Costos
    //Obtiene solo costos ultimos
    public ArrayList<HashMap<String, Object>> getInvCapturaCosto_PaginaGrid(String data_string, int offset, int pageSize, String orderBy, String asc);
    //Obtiene Costos de Referencia
    public ArrayList<HashMap<String, Object>> getInvCapturaCosto_PaginaGrid2(String data_string, int offset, int pageSize, String orderBy, String asc);
    public ArrayList<HashMap<String, String>> getInvCapturaCosto_DatosProducto(String sku, Integer idEmp, Integer mesActual, Integer anoActual, String captura_costo_ref);
    public ArrayList<HashMap<String, String>> getInvCapturaCosto_CostoProducto(Integer id, Integer ano_actual, Integer mes_actual, String captura_costo_ref);
    public HashMap<String, String> getCom_Par(Integer idEmp, Integer idSuc);
    
    
    //Metodo para verificar si un usuario es Administrador
    public HashMap<String, String> getUserRolAdmin(Integer id_user);
    public HashMap<String, String> getUserRolAgenteVenta(Integer id_user);
    
    public int getInsertInvExiTmp(String tipo, String data_string);
    public int getDeleteFromInvExiTmp(String tipo, Integer empresa_id);
    public String getUpdateInvExi(Integer usuario_id, Integer empresa_id, Integer sucursal_id, Integer tipo);
    
    //Carga de Documentos
    public ArrayList<HashMap<String, Object>> getBuscadorClientes(String cadena, Integer filtro, Integer id_empresa, Integer id_sucursal);
    public ArrayList<HashMap<String, Object>> getDatosClienteByNoCliente(String no_control, Integer id_empresa, Integer id_sucursal);
    
    //Metodos para Ordenes de produccion de subensamble version 2
    public ArrayList<HashMap<String, String>> getInvOrdPreSuben2_Detalle(String id);
    public ArrayList<HashMap<String, String>> getInvOrdPreSuben2_DatosFormula(Integer id_detalle);
    public ArrayList<HashMap<String, String>> getInvOrdSubensamble2_DatosFormula(String id);
    
    //APLICATIVO DE ORDENES DE SALIDA CON IMPRESION DE ETIQUETAS
    public ArrayList<HashMap<String, Object>> getInvOrdenSalidaEtiqueta_PaginaGrid(String data_string, int offset, int pageSize, String orderBy, String asc);
    public ArrayList<HashMap<String, Object>> getInvOrdenSalidaEtiqueta_Datos(Integer id_factura);
    public ArrayList<HashMap<String, Object>> getInvOrdenSalidaEtiqueta_DatosGrid(Integer id_factura, boolean seleccionado);
    public ArrayList<HashMap<String, Object>> AgentesDeVentas(Integer id_empresa, Integer id_sucursal);
    
    //Metodos para aplicativo de Etiquetas de Entradas
    public ArrayList<HashMap<String, String>> getInvEtiquetasEntrada_DatosGrid(Integer id);
}
