/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
*/

package com.agnux.kemikal.controllers;

import com.agnux.cfd.v2.Base64Coder;
import com.agnux.common.helpers.FileHelper;
import com.agnux.common.helpers.StringHelper;
import com.agnux.common.helpers.TimeHelper;
import com.agnux.common.obj.DataPost;
import com.agnux.common.obj.ResourceProject;
import com.agnux.common.obj.UserSessionData;
import com.agnux.kemikal.interfacedaos.GralInterfaceDao;
import com.agnux.kemikal.interfacedaos.HomeInterfaceDao;
import com.agnux.kemikal.interfacedaos.ProInterfaceDao;
import com.agnux.kemikal.reportes.PdfReporteFormulas;
import com.itextpdf.text.DocumentException;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.ui.Model;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttributes;

/**
 *
 * @author agnux
 * En esta clase se utiliza una macro "macrocoma", para sistituir las comas en la lista de procedidmientos 
 * 
 */
@Controller
@SessionAttributes({"user"})
@RequestMapping("/proconfigproduccion/")
public class ProConfigProduccionController {
    private static final Logger log  = Logger.getLogger(ProConfigProduccionController.class.getName());
    ResourceProject resource = new ResourceProject();
    
    
    @Autowired
    @Qualifier("daoPro")
    private ProInterfaceDao daoPro;

    
    public ProInterfaceDao getProDao() {
        return daoPro;
    }
    
    @Autowired
    @Qualifier("daoHome")
    private HomeInterfaceDao HomeDao;
    
    public HomeInterfaceDao getHomeDao() {
        return HomeDao;
    }
    
    @Autowired
    @Qualifier("daoGral")
    private GralInterfaceDao gralDao;
    
    public GralInterfaceDao getGralDao() {
        return gralDao;
    }
    
    @RequestMapping(value="/startup.agnux")
    public ModelAndView startUp(HttpServletRequest request, HttpServletResponse response, 
            @ModelAttribute("user") UserSessionData user)
            throws ServletException, IOException {
        
        log.log(Level.INFO, "Ejecutando starUp de {0}", ProConfigProduccionController.class.getName());
        LinkedHashMap<String,String> infoConstruccionTabla = new LinkedHashMap<String,String>();
        
        //infoConstruccionTabla.put("id", "Acciones:90");
        infoConstruccionTabla.put("id", "Acciones:70");
        infoConstruccionTabla.put("folio", "Folio:100");
        infoConstruccionTabla.put("titulo", "Configuraci&acute;on:300");
        infoConstruccionTabla.put("accesor_producto", "Producto:300");
        
        ModelAndView x = new ModelAndView("proconfigproduccion/startup", "title", "Configuraci&oacute;n de Producci&oacute;n");
        
        x = x.addObject("layoutheader", resource.getLayoutheader());
        x = x.addObject("layoutmenu", resource.getLayoutmenu());
        x = x.addObject("layoutfooter", resource.getLayoutfooter());
        x = x.addObject("grid", resource.generaGrid(infoConstruccionTabla));
        x = x.addObject("url", resource.getUrl(request));
        x = x.addObject("username", user.getUserName());
        x = x.addObject("empresa", user.getRazonSocialEmpresa());
        x = x.addObject("sucursal", user.getSucursal());
        
        String userId = String.valueOf(user.getUserId());
        
        String codificado = Base64Coder.encodeString(userId);
        
        //id de usuario codificado
        x = x.addObject("iu", codificado);
        
        return x;
    }
    
    
    
    
    @RequestMapping(value="/get_all_proformulaciones.json", method = RequestMethod.POST)
    public @ResponseBody HashMap<String,ArrayList<HashMap<String, Object>>> getProFormulacionesJson(
           @RequestParam(value="orderby", required=true) String orderby,
           @RequestParam(value="desc", required=true) String desc,
           @RequestParam(value="items_por_pag", required=true) int items_por_pag,
           @RequestParam(value="pag_start", required=true) int pag_start,
           @RequestParam(value="display_pag", required=true) String display_pag,
           @RequestParam(value="input_json", required=true) String input_json,
           @RequestParam(value="cadena_busqueda", required=true) String cadena_busqueda,
           @RequestParam(value="iu", required=true) String id_user_cod,
           Model modcel) {
        
        HashMap<String,ArrayList<HashMap<String, Object>>> jsonretorno = new HashMap<String,ArrayList<HashMap<String, Object>>>();
        HashMap<String,String> has_busqueda = StringHelper.convert2hash(StringHelper.ascii2string(cadena_busqueda));
        
        //aplicativo Entradas de mercancias
        Integer app_selected = 67;
        
        //decodificar id de usuario
        Integer id_usuario = Integer.parseInt(Base64Coder.decodeString(id_user_cod));
        
        //variables para el buscador
        String folio_proceso = "%"+StringHelper.isNullString(String.valueOf(has_busqueda.get("folio_proceso")))+"%";
        String descripcion_proceso = "%"+StringHelper.isNullString(String.valueOf(has_busqueda.get("descripcion_proceso")))+"%";
        String sku_producto = "%"+StringHelper.isNullString(String.valueOf(has_busqueda.get("sku_producto")))+"%";
        String descripcion_producto = "%"+StringHelper.isNullString(String.valueOf(has_busqueda.get("descripcion_producto")))+"%";
        
        
        String data_string = app_selected+"___"+id_usuario+"___"+folio_proceso+"___"+descripcion_proceso+"___"+sku_producto+"___"+descripcion_producto;
        
        //obtiene total de registros en base de datos, con los parametros de busqueda
        int total_items = this.getProDao().countAll(data_string);
        
        //calcula el total de paginas
        int total_pags = resource.calculaTotalPag(total_items,items_por_pag);
        
        //variables que necesita el datagrid, para no tener que hacer uno por cada aplicativo
        DataPost dataforpos = new DataPost(orderby, desc, items_por_pag, pag_start, display_pag, input_json, cadena_busqueda,total_items,total_pags,id_user_cod);
        
        int offset = resource.__get_inicio_offset(items_por_pag, pag_start);
        
        //obtiene los registros para el grid, de acuerdo a los parametros de busqueda
        jsonretorno.put("Data", this.getProDao().getProcesos_PaginaGrid(data_string, offset, items_por_pag, orderby, desc));
        
        //obtiene el hash para los datos que necesita el datagrid
        jsonretorno.put("DataForGrid", dataforpos.formaHashForPos(dataforpos));
        
        return jsonretorno;
    }
    
    //obtiene datos para el autopletar
    @RequestMapping(value="/get_doc_xml.json", method = RequestMethod.POST)
    public @ResponseBody HashMap<String, String> getDocXmlJson(
           @RequestParam(value="doc", required=true) String titulo,
           @RequestParam(value="iu", required=true) String id_user,
           Model modcel) {
        HashMap<String, String> userDat = new HashMap<String, String>();
        HashMap<String, String> doc_name = new HashMap<String, String>();
        
        Integer id_usuario = Integer.parseInt(Base64Coder.decodeString(id_user));
        userDat = this.getHomeDao().getUserById(id_usuario);
        
        Integer id_empresa = Integer.parseInt(userDat.get("empresa_id"));
        Integer id_sucursal = Integer.parseInt(userDat.get("sucursal_id"));
        
        doc_name = this.getProDao().getDocumentoByName(titulo, id_empresa, id_sucursal);
        
        doc_name.put("file_string", FileHelper.stringFromFile("url_resource"+doc_name.get("archivo")));
        
        return doc_name;
    }
    
    //obtiene datos para el autopletar
    @RequestMapping(value="/get_formulas_for_this_producto.json", method = RequestMethod.POST)
    public @ResponseBody ArrayList<HashMap<String, String>> getProFormulasForThisProductoJson(
           @RequestParam(value="id_prod", required=true) String id_prod,
           @RequestParam(value="iu", required=true) String id_user_cod,
           Model modcel) {
        
        HashMap<String, String> userDat = new HashMap<String, String>();
        ArrayList<HashMap<String, String>> arrayTiposDoc = new ArrayList<HashMap<String, String>>();
        //HashMap<String, String> cadenaLineas = new HashMap<String, String>();
        
        Integer id_usuario = Integer.parseInt(Base64Coder.decodeString(id_user_cod));
        userDat = this.getHomeDao().getUserById(id_usuario);
        
        Integer id_empresa = Integer.parseInt(userDat.get("empresa_id"));
        Integer id_sucursal = Integer.parseInt(userDat.get("sucursal_id"));
        
        //decodificar id de usuario
        //Integer id_usuario = Integer.parseInt(Base64Coder.decodeString(id_user_cod));
        arrayTiposDoc=this.getProDao().getFormulasByProd(id_prod, id_empresa);
        
        return arrayTiposDoc;
    }
    
    //obtiene datos para el autopletar
    @RequestMapping(value="/get_autocomplete_for_documentos.json", method = RequestMethod.POST)
    public @ResponseBody ArrayList<HashMap<String, String>> getProFormulacionesJson(
           @RequestParam(value="cadena", required=true) String cadena,
           @RequestParam(value="iu", required=true) String id_user_cod,
           Model modcel) {
        
        ArrayList<HashMap<String, String>> arrayTiposDoc = new ArrayList<HashMap<String, String>>();
        //HashMap<String, String> cadenaLineas = new HashMap<String, String>();
        
        //decodificar id de usuario
        //Integer id_usuario = Integer.parseInt(Base64Coder.decodeString(id_user_cod));
        cadena = "%"+cadena+"%";
        arrayTiposDoc=this.getProDao().getDocumentos(cadena);
        
        return arrayTiposDoc;
    }
    
    
    
    //obtiene lineas de producto y datos para el buscador
    @RequestMapping(method = RequestMethod.POST, value="/getProductoTipos.json")
    public @ResponseBody HashMap<String,ArrayList<HashMap<String, String>>> getDataLineasJson(
            @RequestParam(value="iu", required=true) String id_user_cod,
            Model model
            ) {
        
        HashMap<String,ArrayList<HashMap<String, String>>> jsonretorno = new HashMap<String,ArrayList<HashMap<String, String>>>();
        //ArrayList<HashMap<String, String>> arrayLineas = new ArrayList<HashMap<String, String>>();
        ArrayList<HashMap<String, String>> arrayTiposProducto = new ArrayList<HashMap<String, String>>();
        //HashMap<String, String> cadenaLineas = new HashMap<String, String>();
        
        //decodificar id de usuario
        Integer id_usuario = Integer.parseInt(Base64Coder.decodeString(id_user_cod));
        
        arrayTiposProducto=this.getProDao().getProducto_Tipos();
        
        //cadenaLineas.put("cad_lineas", genera_treeview( this.getInvDao().getProducto_Lineas() ));
        //arrayLineas.add(cadenaLineas);
        //jsonretorno.put("Lines",arrayLineas);
        jsonretorno.put("prodTipos", arrayTiposProducto);
        
        return jsonretorno;
    }
    
    
    
    
    @RequestMapping(method = RequestMethod.POST, value="/get_datos_configuracion_produccion.json")
    public @ResponseBody HashMap<String,ArrayList<HashMap<String, String>>> get_datos_configuracion_produccionJson(
            @RequestParam(value="id_proceso", required=true) Integer id,
            @RequestParam(value="iu", required=true) String id_user,
            Model model
            ) {
        
        log.log(Level.INFO, "Ejecutando get_datos_configuracion_produccionJson de {0}", ProConfigProduccionController.class.getName());
        HashMap<String,ArrayList<HashMap<String, String>>> jsonretorno = new HashMap<String,ArrayList<HashMap<String, String>>>();
        ArrayList<HashMap<String, String>> datosProceso = new ArrayList<HashMap<String, String>>();
        ArrayList<HashMap<String, String>> datosSubProProd = new ArrayList<HashMap<String, String>>();
        ArrayList<HashMap<String, String>> datosSubProProcedimientos = new ArrayList<HashMap<String, String>>();
        ArrayList<HashMap<String, String>> datosSubProEspecificaciones = new ArrayList<HashMap<String, String>>();
        HashMap<String, String> userDat = new HashMap<String, String>();
        
        //decodificar id de usuario
        Integer id_usuario = Integer.parseInt(Base64Coder.decodeString(id_user));
        userDat = this.getHomeDao().getUserById(id_usuario);
        
        Integer id_empresa = Integer.parseInt(userDat.get("empresa_id"));
        Integer id_sucursal = Integer.parseInt(userDat.get("sucursal_id"));
        
        if( id != 0 ){
            datosProceso = this.getProDao().getProProceso_Datos(id);
            //System.out.println("Id de proveedor: "+datosEntrada.get(0).get("proveedor_id").toString());
            datosSubProProd = this.getProDao().getProSubprocesoProd(id);
            datosSubProProcedimientos = this.getProDao().getAllProSubprocesoProcedimiento(id);
            datosSubProEspecificaciones = this.getProDao().getAllProSubprocesoEspecificaciones(id);
            //datosGrid = this.getInvDao().getEntradas_DatosGrid(id);
        }
        
        //Para el buscador de formulas
        Integer app_selected = 69;
        String descripcion = "%%";
        String sku = "%%";
        
        String data_string = app_selected+"___"+id_usuario+"___"+sku+"___"+descripcion+"___"+id;
        
        //jsonretorno.put("Data", this.getProDao().getFormulas_PaginaGrid(data_string, 0, 10, "id", "asc"));
        
        jsonretorno.put("getAllFormulas", this.getProDao().getFormulas_PaginaGrid(data_string, 0, 10, "id", "asc"));
        jsonretorno.put("Proceso", datosProceso);
        jsonretorno.put("datosSubProEspecificaciones", datosSubProEspecificaciones);
        jsonretorno.put("datosSubProProcedimientos", datosSubProProcedimientos);
        jsonretorno.put("datosSubProProd", datosSubProProd);
        jsonretorno.put("SubProcesos", this.getProDao().getSubProcesos(id_empresa));
        jsonretorno.put("Maquinas", this.getProDao().getTiposEquipos(id_empresa));
        jsonretorno.put("Instrumentos", this.getProDao().getInstrumentos(id_empresa));
        
        return jsonretorno;
    }
    
/*
    //obtiene los proveedores para el buscador
    @RequestMapping(method = RequestMethod.POST, value="/get_proveedores.json")
    //public @ResponseBody HashMap<java.lang.String,java.lang.Object> getProveedorJson(
    public @ResponseBody HashMap<String,ArrayList<HashMap<String, String>>> getProveedoresJson(
            @RequestParam(value="rfc", required=true) String rfc,
            @RequestParam(value="email", required=true) String email,
            @RequestParam(value="nombre", required=true) String nombre,
            @RequestParam(value="iu", required=true) String id_user,
            Model model
            ) {
        
        log.log(Level.INFO, "Ejecutando getProveedoresJson de {0}", ProConfigProduccionController.class.getName());
        HashMap<String,ArrayList<HashMap<String, String>>> jsonretorno = new HashMap<String,ArrayList<HashMap<String, String>>>();
        ArrayList<HashMap<String, String>> proveedores = new ArrayList<HashMap<String, String>>();
        HashMap<String, String> userDat = new HashMap<String, String>();
        //decodificar id de usuario
        Integer id_usuario = Integer.parseInt(Base64Coder.decodeString(id_user));
        userDat = this.getHomeDao().getUserById(id_usuario);
        
        Integer id_empresa = Integer.parseInt(userDat.get("empresa_id"));
        
        proveedores = this.getInvDao().getBuscadorProveedores(rfc, email, nombre,id_empresa);
        
        jsonretorno.put("proveedores", proveedores);
        
        return jsonretorno;
    }
    */
    
    //obtiene los productos para el buscador
    @RequestMapping(method = RequestMethod.POST, value="/get_buscador_productos.json")
    public @ResponseBody HashMap<String,ArrayList<HashMap<String, String>>> getProductosJson(
            @RequestParam(value="sku", required=true) String sku,
            @RequestParam(value="tipo", required=true) String tipo,
            @RequestParam(value="descripcion", required=true) String descripcion,
            @RequestParam(value="iu", required=true) String id_user,
            Model model
            ) {
        
        log.log(Level.INFO, "Ejecutando getProductosJson de {0}", ProConfigProduccionController.class.getName());
        HashMap<String,ArrayList<HashMap<String, String>>> jsonretorno = new HashMap<String,ArrayList<HashMap<String, String>>>();
        ArrayList<HashMap<String, String>> productos = new ArrayList<HashMap<String, String>>();
        HashMap<String, String> userDat = new HashMap<String, String>();
        //decodificar id de usuario
        Integer id_usuario = Integer.parseInt(Base64Coder.decodeString(id_user));
        userDat = this.getHomeDao().getUserById(id_usuario);
        
        Integer id_empresa = Integer.parseInt(userDat.get("empresa_id"));
        
        productos = this.getProDao().getBuscadorProductos(sku, tipo, descripcion, id_empresa);
        
        jsonretorno.put("productos", productos);
        
        return jsonretorno;
    }
    
    
    //obtiene los formulaciones para obtener su procedimiento
    @RequestMapping(method = RequestMethod.POST, value="/get_buscador_formulaciones_para_obtener_proced.json")
    public @ResponseBody HashMap<String,ArrayList<HashMap<String, String>>> getBuscadorFormulacionesParaProcedimientosJson(
            @RequestParam(value="sku", required=true) String sku,
            @RequestParam(value="descripcion", required=true) String descripcion,
            @RequestParam(value="subproceso", required=true) Integer subproceso,
            @RequestParam(value="iu", required=true) String id_user,
            Model model
            ) {
        
        log.log(Level.INFO, "Ejecutando getProductosJson de {0}", ProConfigProduccionController.class.getName());
        HashMap<String,ArrayList<HashMap<String, String>>> jsonretorno = new HashMap<String,ArrayList<HashMap<String, String>>>();
        ArrayList<HashMap<String, String>> productos = new ArrayList<HashMap<String, String>>();
        HashMap<String, String> userDat = new HashMap<String, String>();
        //decodificar id de usuario
        Integer id_usuario = Integer.parseInt(Base64Coder.decodeString(id_user));
        userDat = this.getHomeDao().getUserById(id_usuario);
        
        Integer id_empresa = Integer.parseInt(userDat.get("empresa_id"));
        
        productos = this.getProDao().getBuscadorFormulacionesParaProcedidmientos(sku, descripcion, id_empresa, subproceso);
        
        jsonretorno.put("Formulas", productos);
        
        return jsonretorno;
    }
    
    
    //Busca un sku en especifico
    @RequestMapping(method = RequestMethod.POST, value="/get_busca_sku_prod.json")
    public @ResponseBody HashMap<String,ArrayList<HashMap<String, String>>> getBuscaSkuProdJson(
            @RequestParam(value="sku", required=true) String sku,
            @RequestParam(value="iu", required=true) String id_user,
            Model model
            ) {
        HashMap<String, String> userDat = new HashMap<String, String>();
        
        //decodificar id de usuario
        Integer id_usuario = Integer.parseInt(Base64Coder.decodeString(id_user));
        Integer app_selected = 69; //aplicativo asignacion de Rutas
        
        userDat = this.getHomeDao().getUserById(id_usuario);
        Integer id_empresa = Integer.parseInt(userDat.get("empresa_id"));
        
        HashMap<String,ArrayList<HashMap<String, String>>> jsonretorno = new HashMap<String,ArrayList<HashMap<String, String>>>();
        jsonretorno.put("Sku", this.daoPro.getProProductoPorSku(sku.toUpperCase(), id_empresa));
        
        return jsonretorno;
    }
    
    //obtiene los productos para el buscador
    @RequestMapping(method = RequestMethod.POST, value="/get_procedimientos_por_formulacion.json")
    public @ResponseBody HashMap<String,ArrayList<HashMap<String, String>>> getProcedimientosPorFormulacionJson(
            @RequestParam(value="id", required=true) Integer id,
            @RequestParam(value="iu", required=true) String id_user,
            Model model
            ) {
        
        log.log(Level.INFO, "Ejecutando getProductosJson de {0}", ProConfigProduccionController.class.getName());
        HashMap<String,ArrayList<HashMap<String, String>>> jsonretorno = new HashMap<String,ArrayList<HashMap<String, String>>>();
        ArrayList<HashMap<String, String>> productos = new ArrayList<HashMap<String, String>>();
        HashMap<String, String> userDat = new HashMap<String, String>();
        //decodificar id de usuario
        Integer id_usuario = Integer.parseInt(Base64Coder.decodeString(id_user));
        userDat = this.getHomeDao().getUserById(id_usuario);
        
        Integer id_empresa = Integer.parseInt(userDat.get("empresa_id"));
        
        productos = this.getProDao().getProcedimientosPorFormulacion(id,  id_empresa);
        
        jsonretorno.put("Formulas", productos);
        
        return jsonretorno;
    }
    
    /*
    //obtiene los presentaciones del producto seleccionado
    @RequestMapping(method = RequestMethod.POST, value="/get_presentaciones_producto.json")
    //public @ResponseBody HashMap<java.lang.String,java.lang.Object> getProveedorJson(
    public @ResponseBody HashMap<String,ArrayList<HashMap<String, String>>> getPresentacionesproductoJson(
            @RequestParam(value="sku", required=true) String sku,
            Model model
            ) {
        
        log.log(Level.INFO, "Ejecutando getPresentacionesproductoJson de {0}", ProConfigProduccionController.class.getName());
        HashMap<String,ArrayList<HashMap<String, String>>> jsonretorno = new HashMap<String,ArrayList<HashMap<String, String>>>();
        ArrayList<HashMap<String, String>> Presentaciones = new ArrayList<HashMap<String, String>>();
        
        Presentaciones = this.getInvDao().getEntradas_PresentacionesProducto(sku);
        
        jsonretorno.put("Presentaciones", Presentaciones);
        
        return jsonretorno;
    }
    
    
    
    
    //obtiene los productos para el buscador
    @RequestMapping(method = RequestMethod.POST, value="/obtener_tipo_cambio.json")
    //public @ResponseBody HashMap<java.lang.String,java.lang.Object> getProveedorJson(
    public @ResponseBody HashMap<String,ArrayList<HashMap<String, String>>> getTipocambioJson(
            @RequestParam(value="fecha", required=true) String fecha,
            Model model
            ) {
        
        log.log(Level.INFO, "Ejecutando getTipocambioJson de {0}", ProConfigProduccionController.class.getName());
        HashMap<String,ArrayList<HashMap<String, String>>> jsonretorno = new HashMap<String,ArrayList<HashMap<String, String>>>();
        ArrayList<HashMap<String, String>> tc = new ArrayList<HashMap<String, String>>();
        
        tc = this.getInvDao().getEntradas_TipoCambio(fecha);
        
        jsonretorno.put("tipoCambio", tc);
        
        return jsonretorno;
    }
    
    */
    
    
    @RequestMapping(method = RequestMethod.POST, value="/getFormula.json")
    public @ResponseBody HashMap<String,ArrayList<HashMap<String, String>>> getFormulasJson(
            @RequestParam(value="id", required=true) String id,
            @RequestParam(value="iu", required=true) String id_user_cod,
            //@RequestParam(value="numero_buscador", required=true) Integer numero_buscador,
            Model model
            ){
        
        log.log(Level.INFO, "Ejecutando getFormulasjson de {0}", InvFormulasController.class.getName());
        HashMap<String,ArrayList<HashMap<String, String>>> jsonretorno = new HashMap<String,ArrayList<HashMap<String, String>>>();
        HashMap<String, String> userDat = new HashMap<String, String>();
       
        ArrayList<HashMap<String, String>> datosFormulas = new ArrayList<HashMap<String, String>>(); 
        ArrayList<HashMap<String, String>> datosFormulasMinigrid = new ArrayList<HashMap<String, String>>(); 
        ArrayList<HashMap<String, String>> datosFormulasProductoSaliente = new ArrayList<HashMap<String, String>>(); 
        ArrayList<HashMap<String, String>> extra = new ArrayList<HashMap<String, String>>(); 
        HashMap<String, String> tipoCambio = new HashMap<String, String>();
        //ArrayList<HashMap<String, String>> tiposProducto = new ArrayList<HashMap<String, String>>();
        //ArrayList<HashMap<String, String>> unidades = new ArrayList<HashMap<String, String>>();
        
        //decodificar id de usuario
        Integer id_usuario = Integer.parseInt(Base64Coder.decodeString(id_user_cod));
        userDat = this.getHomeDao().getUserById(id_usuario);
        
       // Integer id_empresa = Integer.parseInt(userDat.get("empresa_id"));
       
        if(!id.equals("0")){
            datosFormulas = this.getProDao().getFormula_Datos(id);
            datosFormulasMinigrid = this.getProDao().getFormula_DatosMinigrid(id, "1");
            datosFormulasProductoSaliente = this.getProDao().getFormula_DatosProductoSaliente(id, "1");
        }
        
        //Moneda para obtener el tipo de cambio por deault es USD
        Integer idMoneda=2;
        tipoCambio = this.getProDao().getTipoCambioActualPorIdMoneda(idMoneda);
        extra.add(tipoCambio);
        
        //estos aray list seretornan a la vista al momento de click en nuevo   y los retorna en un HashMap<String,ArrayList<HashMap<String, String>>> jsonretorno
        //tiposProducto = this.getInvDao().getProducto_Tipos_para_formulas(numero_buscador);
        //unidades = this.getInvDao().getProducto_Unidades_para_formulas();
        
        
       jsonretorno.put("Formulas", datosFormulas);
       jsonretorno.put("Formulas_DatosMinigrid", datosFormulasMinigrid);
       jsonretorno.put("Formulas_DatosProductoSaliente", datosFormulasProductoSaliente);
       jsonretorno.put("Extra", extra);
        // jsonretorno.put("ProdTipos", tiposProducto);
        //jsonretorno.put("Unidades",unidades);
        //        jsonretorno.put("Regiones", regiones);
       
        return jsonretorno;
    }
    
    //crear y editar una  formula
    @RequestMapping(method = RequestMethod.POST, value="/editFormulas.json")
    public @ResponseBody HashMap<String, String> editFormulasJson(
        //codigo_master	ACI207
        @RequestParam(value="codigo_master", required=true) String codigo_master,
        //codigo_producto_minigrid
        @RequestParam(value="codigo_producto_minigrid", required=true) String codigo_producto_minigrid,    
        //codigo_producto_saliente	RES1653
        @RequestParam(value="codigo_producto_saliente", required=true) String codigo_producto_saliente,
        //descr_producto_minigrid	
        @RequestParam(value="descr_producto_minigrid", required=true) String descr_producto_minigrid,
        //descr_producto_saliente	AC-318
        @RequestParam(value="descr_producto_saliente", required=true) String descr_producto_saliente,
        //descripcion_master	A.D.B.S. ACIDO DODECIL BENCEN SULFONICO
        @RequestParam(value="descripcion_master", required=true) String descripcion_master,
        //id_prod_master	56
        @RequestParam(value="id_prod_master", required=true) String id_prod_master,
        //id_prod_saliente	1389
        @RequestParam(value="id_prod_saliente", required=true) String inv_prod_id,
        //identificador	0
        @RequestParam(value="identificador", required=true) Integer id,
        //numero_pasos	1
        @RequestParam(value="numero_pasos", required=true) String nivel,
        //paso_actual	1
        @RequestParam(value="paso_actual", required=true) String paso_actual,
        //select_prodtipo	Normal
        @RequestParam(value="select_prodtipo", required=true) String select_prodtipo,
        //select_unidad	Kilogramo
        @RequestParam(value="select_unidad", required=true) String select_unidad,
        //eliminar	56
        @RequestParam(value="eliminar", required=true) String[] eliminado,
        //id del reg	56
        @RequestParam(value="id_reg", required=true) String[] id_reg,
        //id_prod_entrante	56
        @RequestParam(value="id_prod_entrante", required=true) String[] producto_elemento_id,
        @RequestParam(value="id_prod_componente", required=true) String[] id_prod_componente,
        //cantidad	0.8
        @RequestParam(value="cantidad", required=false) String[] cantidad,
        //cantidad	0.8
        @RequestParam(value="posicion", required=false) String[] posicion,
        //total_porcentaje	0.8
        @RequestParam(value="total_porcentaje", required=true) String total_porcentaje,
        Model model,@ModelAttribute("user") UserSessionData user
    ) {
        
        HashMap<String, String> jsonretorno = new HashMap<String, String>();
        HashMap<String, String> succes = new HashMap<String, String>();
        Integer app_selected = 69;//catalogo de agentes
        String command_selected = "new";
        Integer id_usuario= user.getUserId();//variable para el id  del usuario
        //String extra_data_array = "'sin datos'";
        String actualizo = "0";
        int no_partida = 0;
        
        String arreglo[];
            arreglo = new String[eliminado.length];
            
            for(int i=0; i<eliminado.length; i++) {
                if(Integer.parseInt(eliminado[i]) != 0){
                    no_partida++;//si no esta eliminado incrementa el contador de partidas
                }
                
                arreglo[i]= "'"+id_prod_componente[i] +"___" + cantidad[i]+"___" + posicion[i]+"___" + eliminado[i]+"___" + no_partida+"___" + id_reg[i]+"'";
                //System.out.println(arreglo[i]);
            }
            
            //serializar el arreglo
            String extra_data_array = StringUtils.join(arreglo, ",");
        if( id==0 ){
            command_selected = "new";
        }else{
            command_selected = "edit";
        }
        
        
        Integer contador=0;
        if(paso_actual != nivel){
           nivel=paso_actual;
         contador=contador+1;
        }else{
          nivel=paso_actual;  
        }
        
        
        String data_string = app_selected+"___"+command_selected+"___"+id_usuario+"___"+id+"___"+id_prod_master+"___"+inv_prod_id+"___"+nivel;
        
        succes = this.getProDao().selectFunctionValidateAaplicativo(data_string,app_selected,extra_data_array);
        
        log.log(Level.INFO, "despues de validacion {0}", String.valueOf(succes.get("success")));
        if( String.valueOf(succes.get("success")).equals("true") ){
            actualizo = this.getProDao().selectFunctionForApp_Produccion(data_string, extra_data_array);
        }
        
        jsonretorno.put("success",String.valueOf(succes.get("success")));
        
        log.log(Level.INFO, "Salida json {0}", String.valueOf(jsonretorno.get("success")));
        return jsonretorno;
    }
    
    //edicion y nuevo
    @RequestMapping(method = RequestMethod.POST, value="/edit.json")
    public @ResponseBody HashMap<String, String> editJson(
            @RequestParam(value="id_proceso", required=true) Integer id_proceso,
            @RequestParam(value="titulo", required=true) String titulo,
            @RequestParam(value="id_producto", required=true) String id_producto,
            @RequestParam(value="dias_caducidad", required=true) String dias_caducidad,
            
            @RequestParam(value="especificaciones", required=true) String[] especificaciones,
            @RequestParam(value="procediemientos", required=true) String[] procediemientos,
            @RequestParam(value="id_subproceso_grid", required=true) String[] id_subproceso_grid,
            @RequestParam(value="id_master", required=true) String[] id_master,
            @RequestParam(value="nivel_grid", required=true) String[] nivel_grid,
            @RequestParam(value="eliminado", required=true) String[] eliminado,
            @RequestParam(value="tipo_maquina_grid", required=true) String[] tipo_maquina_grid,
            @RequestParam(value="doc_calidad_grid", required=true) String[] doc_calidad_grid,
            @RequestParam(value="empleados_grid", required=false) String[] empleados_grid,
            @RequestParam(value="metadata_grid", required=false) String[] metadata_grid,
            @RequestParam(value="id_formula", required=false) String[] id_formula,
            @RequestParam(value="id_reg", required=false) String[] id_reg,
            @ModelAttribute("user") UserSessionData user,
            Model model
            ) {
            
            HashMap<String, String> jsonretorno = new HashMap<String, String>();
            HashMap<String, String> succes = new HashMap<String, String>();
            int no_partida = 0;
            String extra_data_array = null;
            String arreglo[];
            arreglo = new String[eliminado.length];
            String actualizo = "0";
            
            Integer app_selected = 67;
            String command_selected = "new";
            Integer id_usuario= user.getUserId();//variable para el id  del usuario
            
            /*
            especificaciones
            procediemientos
            id_subproceso_grid
            id_master
            nivel_grid
            eliminado
            tipo_maquina_grid
            doc_calidad_grid
            empleados_grid
            metadata_grid
            */
            
            int total_tr = id_subproceso_grid.length;
            if(total_tr > 0){
                for(int i=0; i<eliminado.length; i++) {
                    if(Integer.parseInt(eliminado[i]) != 0){
                        no_partida++;//si no esta eliminado incrementa el contador de partidas
                    }
                    
                    String tmp_cadena = "'"+no_partida+"___"+especificaciones[i]+"___"+procediemientos[i]+"___"+id_subproceso_grid[i]+"___"+id_master[i]+
                            "___"+nivel_grid[i]+"___"+eliminado[i]+"___"+tipo_maquina_grid[i]+"___"+doc_calidad_grid[i]+"___"+id_reg[i]+"___"+id_formula[i]+"'";
                    
                    //tmp_cadena = StringHelper.addMacroString("macrocoma", "\\,", tmp_cadena);
                    //System.out.println("data_string: "+tmp_cadena);
                    arreglo[i]= tmp_cadena;
                }
                //serializar el arreglo
                extra_data_array = StringUtils.join(arreglo, ",");
            }else{
                extra_data_array ="'sin datos'";
            }
            
            if( id_proceso==0 ){
                command_selected = "new";
            }else{
                command_selected = "edit";
            }
            
            
            
            String data_string = app_selected+"___"+command_selected+"___"+id_usuario+"___"+id_proceso+"___"+titulo.toUpperCase()+"___"+id_producto+"___"+dias_caducidad;
            System.out.println("data_string: "+data_string);
            
            //succes = this.getEdao().selectFunctionValidateAaplicativo(data_string,9,string_array);
            succes = this.getProDao().selectFunctionValidateAaplicativo(data_string,app_selected,extra_data_array);
            
            //log.log(Level.INFO, "despues de validacion {0}", String.valueOf(succes.get("success")));
            
            if( String.valueOf(succes.get("success")).equals("true") ){
                actualizo = this.getProDao().selectFunctionForApp_Produccion(data_string, extra_data_array);
            }
            
            jsonretorno.put("success",String.valueOf(succes.get("success")));
            
            log.log(Level.INFO, "Salida json {0}", String.valueOf(jsonretorno.get("success")));
        return jsonretorno;
    }
    
    
/*  
     * //este no se utiliza en entradas
    //cambiar a borrado logico un registro
    @RequestMapping(method = RequestMethod.POST, value="/logicDelete.json")
    public @ResponseBody HashMap<String, String> logicDeleteJson(
            @RequestParam(value="id_entrada", required=true) Integer id_entrada,
            @RequestParam(value="iu", required=true) String id_user,
            Model model
            ) {
        
        System.out.println("Borrado logico una entrada de mercancia");
        //decodificar id de usuario
        Integer id_usuario = Integer.parseInt(Base64Coder.decodeString(id_user));
        
        Integer app_selected = 9;
        String command_selected = "delete";
        String extra_data_array = "'sin datos'";
        
        String data_string = app_selected+"___"+command_selected+"___"+id_usuario+"___"+id_entrada;
        
        HashMap<String, String> jsonretorno = new HashMap<String, String>();
        
        jsonretorno.put("success",String.valueOf( this.getInvDao().selectFunctionForApp_MovimientosInventario(data_string,extra_data_array)) );
        
        return jsonretorno;
    }
  */  
    
    
    
    /*
    //cancelacion de facturas
    @RequestMapping(method = RequestMethod.POST, value="/cancelar_entrada.json")
    public @ResponseBody HashMap<String, String> getCancelarFactura(
            @RequestParam(value="id_entrada", required=true) Integer id_entrada,
            @RequestParam(value="motivo", required=true) String motivo_cancelacion,
            @RequestParam(value="iu", required=true) String id_user,
            Model model
            ) {
        
        HashMap<String, String> jsonretorno = new HashMap<String, String>();
        System.out.println("Cancelacion de entrada de mercancia");
        //decodificar id de usuario
        Integer id_usuario = Integer.parseInt(Base64Coder.decodeString(id_user));
        
        Integer app_selected = 67;
        String command_selected = "cancelacion";
        String extra_data_array = "'sin datos'";
        
        String data_string = app_selected+"___"+command_selected+"___"+id_usuario+"___"+id_entrada+"___"+motivo_cancelacion.toUpperCase();
        

        
        jsonretorno.put("success",String.valueOf( this.getInvDao().selectFunctionForApp_MovimientosInventario(data_string,extra_data_array)) );
        
        return jsonretorno;
    }
    
    
    @RequestMapping(value = "/get_genera_pdf_entrada/{id_entrada}/{iu}/out.json", method = RequestMethod.GET ) 
    public ModelAndView getGeneraPdfEntradaJson(
                @PathVariable("id_entrada") Integer id_entrada,
                @PathVariable("iu") String id_user,
                HttpServletRequest request, 
                HttpServletResponse response, 
                Model model)
            throws ServletException, IOException, URISyntaxException, DocumentException {
        
        HashMap<String, String> userDat = new HashMap<String, String>();
        ArrayList<HashMap<String, String>> datosEntrada = new ArrayList<HashMap<String, String>>();
        ArrayList<HashMap<String, String>> datosProveedor = new ArrayList<HashMap<String, String>>();
        ArrayList<HashMap<String, String>> proveedorContactos = new ArrayList<HashMap<String, String>>();
        HashMap<String, String> datos_empresa = new HashMap<String, String>();
        HashMap<String, String> datos_entrada = new HashMap<String, String>();
        HashMap<String, String> datos_proveedor = new HashMap<String, String>();
        ArrayList<HashMap<String, String>> lista_productos = new ArrayList<HashMap<String, String>>();
        
        System.out.println("Generando PDF de Entrada de Mercancia");
        
        //decodificar id de usuario
        Integer id_usuario = Integer.parseInt(Base64Coder.decodeString(id_user));
        
        userDat = this.getHomeDao().getUserById(id_usuario);
        Integer id_empresa = Integer.parseInt(userDat.get("empresa_id"));
        
        
        String razon_social_empresa = this.getGralDao().getRazonSocialEmpresaEmisora(id_empresa);
        
        //obtener el directorio temporal
        String dir_tmp = this.getGralDao().getTmpDir();
        
        
        String[] array_company = razon_social_empresa.split(" ");
        String company_name= array_company[0].toLowerCase();
        String ruta_imagen = this.getGralDao().getImagesDir() +"logo_"+ company_name +".png";
        
        
        File file_dir_tmp = new File(dir_tmp);
        System.out.println("Directorio temporal: "+file_dir_tmp.getCanonicalPath());
        
        
        String file_name = "entrada_"+company_name+".pdf";
        //ruta de archivo de salida
        String fileout = file_dir_tmp +"/"+  file_name;
        
        datos_empresa.put("emp_razon_social", razon_social_empresa);
        datos_empresa.put("emp_rfc", this.getGralDao().getRfcEmpresaEmisora(id_empresa));
        datos_empresa.put("emp_calle", this.getGralDao().getCalleDomicilioFiscalEmpresaEmisora(id_empresa));
        datos_empresa.put("emp_no_exterior", this.getGralDao().getNoExteriorDomicilioFiscalEmpresaEmisora(id_empresa));
        datos_empresa.put("emp_colonia", this.getGralDao().getColoniaDomicilioFiscalEmpresaEmisora(id_empresa));
        datos_empresa.put("emp_pais", this.getGralDao().getPaisDomicilioFiscalEmpresaEmisora(id_empresa));
        datos_empresa.put("emp_estado", this.getGralDao().getEstadoDomicilioFiscalEmpresaEmisora(id_empresa));
        datos_empresa.put("emp_municipio", this.getGralDao().getMunicipioDomicilioFiscalEmpresaEmisora(id_empresa));
        datos_empresa.put("emp_cp", this.getGralDao().getCpDomicilioFiscalEmpresaEmisora(id_empresa));
        
        
        datosEntrada = this.getInvDao().getEntrada_Datos(id_entrada);
        
        datos_entrada.put("folio_entrada", datosEntrada.get(0).get("no_entrada"));
        datos_entrada.put("fecha_entrada", datosEntrada.get(0).get("fecha_entrada"));
        datos_entrada.put("factura", datosEntrada.get(0).get("factura"));
        datos_entrada.put("fecha_factura", datosEntrada.get(0).get("expedicion"));
        datos_entrada.put("orden_compra", datosEntrada.get(0).get("orden_compra"));
        datos_entrada.put("observaciones", datosEntrada.get(0).get("observaciones"));
        datos_entrada.put("flete", datosEntrada.get(0).get("flete"));
        datos_entrada.put("subtotal", datosEntrada.get(0).get("subtotal"));
        datos_entrada.put("iva", datosEntrada.get(0).get("iva"));
        datos_entrada.put("retencion", datosEntrada.get(0).get("retencion"));
        datos_entrada.put("total", datosEntrada.get(0).get("total"));
        datos_entrada.put("moneda_id", datosEntrada.get(0).get("moneda_id"));
        if(datosEntrada.get(0).get("estado").equals("")){
            datos_entrada.put("estado", "0");
        }else{
            datos_entrada.put("estado", datosEntrada.get(0).get("estado"));
        }
        
        datosProveedor = this.getInvDao().getEntradas_DatosProveedor(Integer.parseInt(datosEntrada.get(0).get("proveedor_id")));
        proveedorContactos = this.getInvDao().getProveedor_Contacto(Integer.parseInt(datosEntrada.get(0).get("proveedor_id")));
        
        datos_proveedor.put("prov_razon_social", datosProveedor.get(0).get("razon_social"));
        datos_proveedor.put("prov_rfc", datosProveedor.get(0).get("rfc"));
        datos_proveedor.put("prov_calle", datosProveedor.get(0).get("calle"));
        datos_proveedor.put("prov_numero", datosProveedor.get(0).get("numero"));
        datos_proveedor.put("prov_colonia", datosProveedor.get(0).get("colonia"));
        datos_proveedor.put("prov_municipio", datosProveedor.get(0).get("municipio"));
        datos_proveedor.put("prov_estado", datosProveedor.get(0).get("estado"));
        datos_proveedor.put("prov_pais", datosProveedor.get(0).get("pais"));
        datos_proveedor.put("prov_cp", datosProveedor.get(0).get("cp"));
        datos_proveedor.put("prov_telefono", datosProveedor.get(0).get("telefono"));
        
        if(proveedorContactos.size()<=0){
            datos_proveedor.put("prov_contacto", "");
        }else{
            datos_proveedor.put("prov_contacto", proveedorContactos.get(0).get("contacto"));
        }
        
        
        
        //obtiene el listado de productos para el pdf
        lista_productos = this.getInvDao().getEntradas_DatosGrid(id_entrada);
        
        //instancia a la clase que construye el pdf del reporte de facturas
        pdfEntradas ent = new pdfEntradas(datos_empresa, datos_entrada, datos_proveedor,lista_productos, fileout, ruta_imagen);
        
        
        System.out.println("Recuperando archivo: " + fileout);
        File file = new File(fileout);
        int size = (int) file.length(); // Tama√±o del archivo
        BufferedInputStream bis = new BufferedInputStream(new FileInputStream(file));
        response.setBufferSize(size);
        response.setContentLength(size);
        response.setContentType("application/pdf");
        response.setHeader("Content-Disposition","attachment; filename=\"" + file.getCanonicalPath() +"\"");
        FileCopyUtils.copy(bis, response.getOutputStream());  	
        response.flushBuffer();
        
        return null;
        
    } 

    
            */
    
    //localhost:8080/com.mycompany_Kemikal_war_1.0-SNAPSHOT/controllers/logasignarutas/getPdfRuta/1/NQ==/out.json
    //Genera pdf de formulacion de
    @RequestMapping(value = "/getPdfFormula/{id}/{stock}/{costear}/{tc}/{iu}/out.json", method = RequestMethod.GET ) 
    public ModelAndView getGeneraPdfFacturacionJson(
                @PathVariable("id") Integer id_formula,
                @PathVariable("stock") String stock,
                @PathVariable("costear") String costear,
                @PathVariable("tc") String tc,
                @PathVariable("iu") String id_user,
                HttpServletRequest request, 
                HttpServletResponse response, 
                Model model)
            throws ServletException, IOException, URISyntaxException, DocumentException {
        
        HashMap<String, String> userDat = new HashMap<String, String>();
        HashMap<String, String> datos= new HashMap<String, String>();
        ArrayList<HashMap<String, String>> especificaciones= new ArrayList<HashMap<String, String>>();
        HashMap<String, String> datosEncabezadoPie= new HashMap<String, String>();
        ArrayList<HashMap<String, Object>> lista_productos = new ArrayList<HashMap<String, Object>>();
        ArrayList<HashMap<String, String>> lista_procedimiento = new ArrayList<HashMap<String, String>>();
        
            
        //decodificar id de usuario
        Integer id_usuario = Integer.parseInt(Base64Coder.decodeString(id_user));
        Integer app_selected = 69; //aplicativo asignacion de Rutas
        
        userDat = this.getHomeDao().getUserById(id_usuario);
        Integer id_empresa = Integer.parseInt(userDat.get("empresa_id"));
        String rfc_empresa=this.getGralDao().getRfcEmpresaEmisora(id_empresa);
        String razon_social_empresa = this.getGralDao().getRazonSocialEmpresaEmisora(id_empresa);
        
        datosEncabezadoPie.put("nombre_empresa_emisora", razon_social_empresa);
        datosEncabezadoPie.put("titulo_reporte", this.getGralDao().getTituloReporte(id_empresa, app_selected));
        datosEncabezadoPie.put("codigo1", this.getGralDao().getCodigo1Iso(id_empresa, app_selected));
        datosEncabezadoPie.put("codigo2", this.getGralDao().getCodigo2Iso(id_empresa, app_selected));
        
        //obtener el directorio temporal
        String dir_tmp = this.getGralDao().getTmpDir();
        
        File file_dir_tmp = new File(dir_tmp);
        System.out.println("Directorio temporal: "+file_dir_tmp.getCanonicalPath());
        
        String[] arregloFecha = TimeHelper.getFechaActualYMDH().split("-");
        String anoActual = arregloFecha[0];
        String mesActual = arregloFecha[1];
        
        //obtiene las facturas del periodo indicado
        lista_productos = this.getProDao().getPro_ListaProductosFormulaPdf(id_formula, tc, anoActual, mesActual);
        datos = this.getProDao().getPro_DatosFormulaPdf(id_formula);
        especificaciones = this.getProDao().getPro_DatosFormulaEspecificacionesPdf(id_formula);
        lista_procedimiento = this.getProDao().getPro_DatosFormulaProcedidmientoPdf(id_formula);
        
        datos.put("fecha", TimeHelper.getFechaActualYMDH());
        
        
        String file_name = "FORMULA_"+rfc_empresa+"_"+datos.get("folio") +".pdf";
        //ruta de archivo de salida
        String fileout = file_dir_tmp +"/"+  file_name;
        
        //instancia a la clase que construye el pdf del reporte de facturas
        PdfReporteFormulas pdf = new PdfReporteFormulas(datosEncabezadoPie, fileout,lista_productos,datos, especificaciones, stock, costear, tc, lista_procedimiento);
        
        System.out.println("Recuperando archivo: " + fileout);
        File file = new File(fileout);
        if (file.exists()){
            int size = (int) file.length(); // Tama√±o del archivo
            BufferedInputStream bis = new BufferedInputStream(new FileInputStream(file));
            response.setBufferSize(size);
            response.setContentLength(size);
            response.setContentType("text/plain");
            response.setHeader("Content-Disposition","attachment; filename=\"" + file.getCanonicalPath() +"\"");
            FileCopyUtils.copy(bis, response.getOutputStream());  	
            response.flushBuffer();
        }
        
        return null;
    }
    
    
    
    //cambia el estatus del borrado logico
    @RequestMapping(method = RequestMethod.POST, value="/logicDeleteFormula.json")
    public @ResponseBody HashMap<String, String> logicDeleteJson(
            @RequestParam(value="id", required=true) Integer id,
            @RequestParam(value="proceso_id", required=true) String proceso_id,
            @RequestParam(value="iu", required=true) String id_user,
            Model model
        ) {
        
        HashMap<String, String> jsonretorno = new HashMap<String, String>();
        //decodificar id de usuario
        Integer id_usuario = Integer.parseInt(Base64Coder.decodeString(id_user));
        
        Integer app_selected = 69;
        String command_selected = "delete";
        String extra_data_array = "'sin datos'";
        String data_string = app_selected+"___"+command_selected+"___"+id_usuario+"___"+id+"___"+proceso_id;
        
        System.out.println("Ejecutando borrado logico formulas: "+data_string);
        jsonretorno.put("success",String.valueOf( this.getProDao().selectFunctionForApp_Produccion(data_string,extra_data_array)) );
        
        return jsonretorno;
    }
    
    
}