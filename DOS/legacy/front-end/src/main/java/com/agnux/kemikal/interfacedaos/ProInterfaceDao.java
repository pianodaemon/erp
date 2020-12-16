/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.agnux.kemikal.interfacedaos;

import java.util.ArrayList;
import java.util.HashMap;

/**
 *
 * @author agnux
 */

public interface ProInterfaceDao {
    public HashMap<String, String> selectFunctionValidateAaplicativo(String data, Integer idApp, String extra_data_array);
    public String selectFunctionForThisApp(String campos_data, String extra_data_array);
    public int countAll(String data_string);

    public String selectFunctionForApp_Produccion(String campos_data, String extra_data_array);
    public ArrayList<HashMap<String, String>> getBuscadorPedidos(String folio,String proveedor,Integer id_empresa);
    public ArrayList<HashMap<String, String>> getBuscadorProductos(String sku, String tipo, String descripcion,Integer id_empresa);
    public ArrayList<HashMap<String, String>> getProductosPedidoSeleccionado(String id_pedido, Integer id_usuario);

    public ArrayList<HashMap<String, String>> getBuscadorEquivalentes(String id_producto,Integer id_empresa, Integer user_id);

    public ArrayList<HashMap<String, String>> getRequisicionOP(String id_orden,Integer id_empresa, Integer user_id);

    public ArrayList<HashMap<String, String>> getBuscadorFormulacionesParaProcedidmientos(String sku, String descripcion,Integer id_empresa,Integer subproceso);

    public ArrayList<HashMap<String, String>> getProcedimientosPorFormulacion(Integer id_producto,Integer id_empresa);

    public ArrayList<HashMap<String, String>> getProProductoPorSku(String sku, Integer id_empresa);
    public ArrayList<HashMap<String, String>> getProSubprocesosPorProductoSku(String sku, Integer id_empresa ,String id_formula,String version);

    public ArrayList<HashMap<String, String>> getSubProcesos(Integer id_empresa);
    public ArrayList<HashMap<String, String>> getTiposEquipos(Integer id_empresa);
    public ArrayList<HashMap<String, String>> getDocumentos(String cadena);
    public ArrayList<HashMap<String, String>> getInstrumentos(Integer id_empresa);

    public ArrayList<HashMap<String, String>> getFormulasByProd(String id_prod,Integer emp_id);//add por paco

    public HashMap<String, String> getDocumentoByName(String doc, Integer id_empresa, Integer id_sucursal);

    public ArrayList<HashMap<String, String>> getPaises();
    public ArrayList<HashMap<String, String>> getEntidadesForThisPais(String id_pais);
    public ArrayList<HashMap<String, String>> getLocalidadesForThisEntidad(String id_pais,String id_entidad);


    /*catalogo de formulas*/
    public ArrayList<HashMap<String, String>> getFormula_Datos(String id_formula);
    public ArrayList<HashMap<String, String>> getFormula_DatosMinigrid(String id_formula, String id_nivel);
    public ArrayList<HashMap<String, String>> getFormula_DatosProductoSaliente(String id_tabla_formula, String id_nivel);
    public ArrayList<HashMap<String, String>> getFormulas_PaginaGrid(String data_string, int offset, int pageSize, String orderBy, String asc);


    /*Catalogo para formulas en desarrollo*/
    public ArrayList<HashMap<String, String>> getFormulaLaboratorio_Datos(String id_formula);
    public ArrayList<HashMap<String, String>> getFormulaLaboratorio_DatosMinigrid(String id_formula, String id_nivel);
    public ArrayList<HashMap<String, Object>> getFormulasLaboratorio_PaginaGrid(String data_string, int offset, int pageSize, String orderBy, String asc);

    /*Buscador de formulas en desarrollo*/
    public ArrayList<HashMap<String, String>> getBuscadorVersionesFormulas(String sku, String descripcion,Integer id_empresa);

    /*pdf de formulas*/
    public ArrayList<HashMap<String, Object>> getPro_ListaProductosFormulaPdf(Integer formula_id, String tc, String anoActual, String mesActual);
    public HashMap<String, String> getPro_DatosFormulaPdf(Integer formula_id);
    public ArrayList<HashMap<String, String>> getPro_DatosFormulaEspecificacionesPdf(Integer formula_id);
    public ArrayList<HashMap<String, String>> getPro_DatosFormulaProcedidmientoPdf(Integer formula_id);


    /*actulizador de productos en las formulas*/
    public ArrayList<HashMap<String, String>> getPro_FormulasProductos(String codigo,String descipcion, Integer id_empresa);


    //catalogo de productos
    public ArrayList<HashMap<String, String>> getProducto_Tipos();
    public ArrayList<HashMap<String, String>> getProducto_Presentaciones(Integer id_producto);
    public ArrayList<HashMap<String, String>> getProducto_sku(String sku,Integer id_usuario);


    //configuracion produccion
    public ArrayList<HashMap<String, Object>> getProcesos_PaginaGrid(String data_string, int offset, int pageSize, String orderBy , String asc);
    public ArrayList<HashMap<String, String>> getProProceso_Datos(Integer id);
    public ArrayList<HashMap<String, String>> getEntradas_DatosProveedor(Integer id_proveedor);

    public ArrayList<HashMap<String, String>> getProSubprocesoProd(Integer id);
    public ArrayList<HashMap<String, String>> getAllProSubprocesoProcedimiento(Integer id);//catalogo  de productos y entradas de mercancias
    public ArrayList<HashMap<String, String>> getAllProSubprocesoEspecificaciones(Integer id);

    public ArrayList<HashMap<String, String>> getEntradas_PresentacionesProducto(String sku);
    public ArrayList<HashMap<String, String>> getEntradas_TipoCambio(String fecha);
    public ArrayList<HashMap<String, String>> getProveedor_Contacto(Integer idProveedor);



    //preorden produccion
    public ArrayList<HashMap<String, Object>> getProPreorden_PaginaGrid(String data_string, int offset, int pageSize, String orderBy , String asc);
    public ArrayList<HashMap<String, String>> getProOrdenTipos(Integer emp_id);
    public ArrayList<HashMap<String, String>> getProPreorden_Datos(Integer id);
    public ArrayList<HashMap<String, String>> getProPreorden_Detalle(Integer id);


    //Orden produccion
    public ArrayList<HashMap<String, Object>> getProOrden_PaginaGrid(String data_string, int offset, int pageSize, String orderBy , String asc);
    public ArrayList<HashMap<String, String>> getProOrdenOperariosDisponibles(String cadena, Integer id_empresa);
    public ArrayList<HashMap<String, String>> getProOrdenEquipoDisponible(String cadena, Integer id_empresa);
    public ArrayList<HashMap<String, String>> getProOrdenEquipoAdicionalDisponible(String cadena, Integer id_empresa);
    public ArrayList<HashMap<String, String>> getProOrden_Datos(Integer id);
    public ArrayList<HashMap<String, String>> getProOrden_Detalle(Integer id);
    public ArrayList<HashMap<String, String>> getProOrden_EspecificacionesDetalle(Integer id);
    public ArrayList<HashMap<String, String>> getProElementosProducto(String producto_id, String orden_id, String id_subproceso);
    public String getExistenciaAlmacenesPorProducts(String almacen_id, String id_producto, String id_usuario);


    public ArrayList<HashMap<String, String>> getProOrdenProd_DatosLote(String no_lote, Integer id_producto, Integer id_usuario);


    /*pdf de orden de produccion*/
    public ArrayList<HashMap<String, Object>> getPro_DatosOrdenProduccionPdf(String formula_id,String proceso_id);

    /*pdf de orden de preordenproduccion*/
    public ArrayList<HashMap<String, Object>> getPro_DatosPreOrdenProduccionPdf(String formula_id);
    //public HashMap<String, String> getPro_DatosFormulaPdf(Integer formula_id);
    //public ArrayList<HashMap<String, String>> getPro_DatosFormulaEspecificacionesPdf(Integer formula_id);
    //public ArrayList<HashMap<String, String>> getPro_DatosFormulaProcedidmientoPdf(Integer formula_id);



    //catalogo de instrumentos de medicion  InterfaceDao
    public ArrayList<HashMap<String, String>> getInstrumentosMedicion_Datos(Integer id);
    public ArrayList<HashMap<String, Object>> getInstrumentosMedicion_PaginaGrid(String data_string, int offset, int pageSize, String orderBy, String asc,Integer id_empresa);




    //catalogos de Ezequiel
    //catalogo de inventario tipo de equipos
    public ArrayList<HashMap<String, Object>> getProTipoEquipo_PaginaGrid(String data_string, int offset, int pageSize, String orderBy , String asc);
    public ArrayList<HashMap<String, String>> getProTipoEquipo_Datos(Integer id_unidad);

    public String selectFunctionForThisApp2(String campos_data, String extra_data_array);
    //terminana catalogos de Ezequiel


    /*de aqui para adelante, no los estoy usando*/
    //Traspaso de mercancia
    public ArrayList<HashMap<String, String>> getTraspaso_Tipos();
    public ArrayList<HashMap<String, Object>> getTraspaso_PaginaGrid(String data_string, int offset, int pageSize, String orderBy , String asc);
    public ArrayList<HashMap<String, String>> getTraspaso_Datos(Integer id_traspaso);
    public ArrayList<HashMap<String, String>> getTraspaso_DatosGrid(Integer id_traspaso);

    //reporte de existencias en invetario
    public ArrayList<HashMap<String, String>> getDatos_ReporteExistencias(Integer id_isuario, Integer id_almacen, String codigo_producto, String descripcion,Integer tipo);


    //catalogo tipos de movimiento
    public ArrayList<HashMap<String, Object>> getTipoMovimientosInventaioGrid(String data_string, int offset, int pageSize, String orderBy , String asc);
    public ArrayList<HashMap<String, String>> getTipoMovInv_Datos(Integer id);

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


    //catalogo de subFamilias de Productos
    public ArrayList<HashMap<String, Object>> getInvProdSubFamilias_PaginaGrid(String data_string, int offset, int pageSize, String orderBy , String asc);
    //se utiliza en catalogo de Productos y catalogo de Subfamilias
    public ArrayList<HashMap<String, String>> getInvProdSubFamilias_Familias(Integer id_empresa);
    public ArrayList<HashMap<String, String>> getInvProdSubFamilias_Datos(Integer id_subfamilia);


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


    //extrae todas la ssucursale para asignarlas a una zona
    public ArrayList<HashMap<String, String>> getSucursales(Integer id_empresa);
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
    public ArrayList<HashMap<String, String>> getInvOrdPreSubenDatosComProd(String sku);

    //catalogo de InvOrdSuben(Orden Subensamble)
    public ArrayList<HashMap<String, Object>> getInvOrdSubenGrid(String data_string, int offset, int pageSize, String orderBy , String asc);
    public ArrayList<HashMap<String, String>> getInvOrdSuben_Datos(String id);
    public ArrayList<HashMap<String, String>> getInvDetalleOrdSuben(String id);
    public ArrayList<HashMap<String, String>> getInvOrdPreSubenDatosComponentesPorProducto(String id);


    //catalogo de inventario Unidades
    public ArrayList<HashMap<String, Object>> getUnidades_PaginaGrid(String data_string, int offset, int pageSize, String orderBy , String asc);
    public ArrayList<HashMap<String, String>> getUnidades_Datos(Integer id_unidad);

    public ArrayList<HashMap<String, String>> getAlmacenes(Integer id_empresa);

    public ArrayList<HashMap<String, String>> getVersionesFormulasPorCodigoProducto(String sku, String tipo, Integer id_empresa);


    //Para siimulacion de produccioon
    public ArrayList<HashMap<String, String>> getProductosFormula(String id_formula);
    //Obtiene los datos de un producto formulado
    public ArrayList<HashMap<String, String>> getProductoFormulaPdfSimulacion(Integer id_formula);
    
    //obtiene el titulo del tipo de simulacion
    public ArrayList<HashMap<String, String>> getProOrdenTipoById(Integer id_tipo);
    
    //Obtiene el nombre de un empleaod de acuerdo a  su id
    public HashMap<String, String> getNombreEmpleadoById(Integer id_empleado);

    //Reportes de produccion
    public ArrayList<HashMap<String, String>> getProduccion(String fecha_inicial, String fecha_final,String sku,String sku_descripcion,Integer equipo,Integer operario,Integer tipo_reporte,Integer id_empresa);
    public ArrayList<HashMap<String, String>> getProduccion_por_producto(String fecha_inicial, String fecha_final,String sku,String sku_descripcion,Integer equipo,Integer operario,Integer tipo_reporte,Integer id_empresa);
    public ArrayList<HashMap<String, String>> getProduccion_por_equipo(String fecha_inicial, String fecha_final,String sku,String sku_descripcion,Integer equipo,Integer operario,Integer tipo_reporte,Integer id_empresa);
    public ArrayList<HashMap<String, String>> getProduccion_por_operario(String fecha_inicial, String fecha_final,String sku,String sku_descripcion,Integer equipo,Integer operario,Integer tipo_reporte,Integer id_empresa);

    public ArrayList<HashMap<String, String>> getProducto_Tipos_produccion();
    public ArrayList<HashMap<String, String>> getBuscadorProductos_produccion(String sku,String tipo,String descripcion,Integer id_empresa);

    public ArrayList<HashMap<String, String>> getOperarios(Integer id_empresa);


    
    public ArrayList<HashMap<String, String>> getReporteEnvasado_Datos(Integer id_empresa);
    public ArrayList<HashMap<String, String>> getReportReenvasado_Datos(Integer id_empresa);
    //fin de los reportes de produccion
    
    
    //Inicia Catalogo de Equipos Adicionales
    public ArrayList<HashMap<String, Object>> getEquipoAdicional_PaginaGrid(String data_string, int offset, int pageSize, String orderBy , String asc);
    public ArrayList<HashMap<String, String>> getProEquipoAdicional_Datos(Integer id);
    
    //Inicia Catalogo de Equipos 
    public ArrayList<HashMap<String, Object>> getProEquipo_PaginaGrid(String data_string, int offset, int pageSize, String orderBy , String asc);
    public ArrayList<HashMap<String, String>> getProEquipo_Datos(Integer id);
    public ArrayList<HashMap<String, String>> getProEquipo_Tipos(Integer idEmp);
    
    
    //Inicia Pdf de Estructura final de diseño o adecuacion
    public ArrayList<HashMap<String, Object>> getPro_DatosOrdenProduccionLabVersionPdf( String produccion_id );
    public ArrayList<HashMap<String, Object>> getPro_DatosOrdenProduccionLabPdf( String produccion_id );
    
    //Reporte de Calidad
    public ArrayList<HashMap<String, String>> getPro_ReporteCalidad(String campos_data);
    
    //Obtiene el tipo de cambio actual
    public HashMap<String, String> getTipoCambioActualPorIdMoneda(Integer idMoneda);
    
}
