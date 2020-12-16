/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.agnux.kemikal.controllers;



import com.agnux.cfd.v2.Base64Coder;
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
 * @author Vale Santos
 */
@Controller
@SessionAttributes({"user"})
@RequestMapping("/proformulasdesarrollo/")
public class ProFormulasDesarrolloController {
    ResourceProject resource = new ResourceProject();
    private static final Logger log  = Logger.getLogger(ProFormulasDesarrolloController.class.getName());
    
    
    @Autowired
    @Qualifier("daoPro")
    private ProInterfaceDao daoPro;
    
    public ProInterfaceDao getProDao() {
        return daoPro;
    }
    
    @Autowired
    @Qualifier("daoHome")   //permite controlar usuarios que entren
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
     @ModelAttribute("user") UserSessionData user)throws ServletException, IOException {
        
        log.log(Level.INFO, "Ejecutando starUp de {0}", ProFormulasDesarrolloController.class.getName());
        LinkedHashMap<String,String> infoConstruccionTabla = new LinkedHashMap<String,String>();
        
        
        infoConstruccionTabla.put("id", "Acciones:90");
        infoConstruccionTabla.put("codigo", "C&oacute;digo:130");
        infoConstruccionTabla.put("descripcion", "Descripci&oacute;n:130");
        infoConstruccionTabla.put("unidad", "Unidad:130");
        //infoConstruccionTabla.put("tipo_producto", "Tipo producto:130");
        infoConstruccionTabla.put("version", "Nivel:130");
       
        
        ModelAndView x = new ModelAndView("proformulasdesarrollo/startup", "title", "Cat&aacute;logo de F&oacute;rmulas en Desarrollo");
        
        x = x.addObject("layoutheader", resource.getLayoutheader());
        x = x.addObject("layoutmenu", resource.getLayoutmenu());
        x = x.addObject("layoutfooter", resource.getLayoutfooter());
        x = x.addObject("grid", resource.generaGrid(infoConstruccionTabla));
       
        x = x.addObject("url", resource.getUrl(request));
        x = x.addObject("username", user.getUserName());
        x = x.addObject("empresa", user.getRazonSocialEmpresa());
        x = x.addObject("sucursal", user.getSucursal());
       
        String userId = String.valueOf(user.getUserId());
        
        //codificar id de usuario
        String codificado = Base64Coder.encodeString(userId);
       
        //decodificar id de usuario
        String decodificado = Base64Coder.decodeString(codificado);
        
        //id de usuario codificado
        x = x.addObject("iu", codificado);
     
        return x;
    }
    
    
     @RequestMapping(value="/getAllFormulasEnDesarrollo.json", method = RequestMethod.POST)
    public @ResponseBody HashMap<String,ArrayList<HashMap<String, Object>>> getAllFormulasJson(
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
        
        //aplicativo catalogo de formulas en desarrollo
        Integer app_selected = 108;
        
        //decodificar id de usuario
        Integer id_usuario = Integer.parseInt(Base64Coder.decodeString(id_user_cod));
        
        //variables para el buscador
        String descripcion = "%"+StringHelper.isNullString(String.valueOf(has_busqueda.get("descripcion_buscador")))+"%";
        String sku =  "%"+StringHelper.isNullString(String.valueOf(has_busqueda.get("sku_buscador")))+"%";
        
        //String data_string = app_selected+"___"+id_usuario+"___"+descripcion;
        String data_string = app_selected+"___"+id_usuario+"___"+sku+"___"+descripcion;
        
        
        //obtiene total de registros en base de datos, con los parametros de busqueda
        int total_items = this.getProDao().countAll(data_string);
        
        
        //calcula el total de paginas
        int total_pags = resource.calculaTotalPag(total_items,items_por_pag);
        
        
        //variables que necesita el datagrid, para no tener que hacer uno por cada aplicativo
        DataPost dataforpos = new DataPost(orderby, desc, items_por_pag, pag_start, display_pag, input_json, cadena_busqueda,total_items,total_pags, id_user_cod);
        
        int offset = resource.__get_inicio_offset(items_por_pag, pag_start);
        
        //obtiene los registros para el grid, de acuerdo a los parametros de busqueda
        jsonretorno.put("Data", this.getProDao().getFormulasLaboratorio_PaginaGrid(data_string, offset, items_por_pag, orderby, desc));
        //obtiene el hash para los datos que necesita el datagrid
        jsonretorno.put("DataForGrid", dataforpos.formaHashForPos(dataforpos));
        
        return jsonretorno;
    }
     
     
     @RequestMapping(method = RequestMethod.POST, value="/getFormulaDesarrollo.json")
    public @ResponseBody HashMap<String,ArrayList<HashMap<String, String>>> getFormulasJson(
            @RequestParam(value="id", required=true) String id,
            @RequestParam(value="iu", required=true) String id_user_cod,
            //@RequestParam(value="numero_buscador", required=true) Integer numero_buscador,
            Model model
            ){
        
        log.log(Level.INFO, "Ejecutando getFormulasjson de {0}", ProFormulasDesarrolloController.class.getName());
        HashMap<String,ArrayList<HashMap<String, String>>> jsonretorno = new HashMap<String,ArrayList<HashMap<String, String>>>();
        HashMap<String, String> userDat = new HashMap<String, String>();
        
        ArrayList<HashMap<String, String>> datosFormulas = new ArrayList<HashMap<String, String>>(); 
        ArrayList<HashMap<String, String>> datosFormulasMinigrid = new ArrayList<HashMap<String, String>>(); 
        ArrayList<HashMap<String, String>> datosFormulasProductoSaliente = new ArrayList<HashMap<String, String>>(); 
        
        ArrayList<HashMap<String, String>> tipos_productos = new ArrayList<HashMap<String, String>>(); 
        //ArrayList<HashMap<String, String>> tiposProducto = new ArrayList<HashMap<String, String>>();
        //ArrayList<HashMap<String, String>> unidades = new ArrayList<HashMap<String, String>>();
        
        //decodificar id de usuario
        Integer id_usuario = Integer.parseInt(Base64Coder.decodeString(id_user_cod));
        userDat = this.getHomeDao().getUserById(id_usuario);
        
        
        if(!id.equals("0")){
            
            datosFormulas = this.getProDao().getFormulaLaboratorio_Datos(id);
            datosFormulasMinigrid = this.getProDao().getFormulaLaboratorio_DatosMinigrid(id, "1");
            
            datosFormulasProductoSaliente = this.getProDao().getFormula_DatosProductoSaliente(id, "1");
            
        }
        tipos_productos = this.getProDao().getProducto_Tipos();
        //estos aray list seretornan a la vista al momento de click en nuevo   y los retorna en un HashMap<String,ArrayList<HashMap<String, String>>> jsonretorno
        //tiposProducto = this.getInvDao().getProducto_Tipos_para_formulas(numero_buscador);
        //unidades = this.getInvDao().getProducto_Unidades_para_formulas();
        
        
       jsonretorno.put("Formulas", datosFormulas);
       jsonretorno.put("Formulas_DatosMinigrid", datosFormulasMinigrid);
       jsonretorno.put("Formulas_DatosProductoSaliente", datosFormulasProductoSaliente);
       jsonretorno.put("prodTipos", tipos_productos);
       
        return jsonretorno;
    }
     
     
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
    
     //obtiene tipos de productos y datos para el buscador
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
    @RequestMapping(method = RequestMethod.POST, value="/get_buscador_versione_formulasdesarrollo.json")
    public @ResponseBody HashMap<String,ArrayList<HashMap<String, String>>> getBuscadorVersioneFormulasDesarrolloJson(
            @RequestParam(value="sku", required=true) String sku,
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
        
        productos = this.getProDao().getBuscadorVersionesFormulas(sku, descripcion, id_empresa);
        
        jsonretorno.put("formulas", productos);
        
        return jsonretorno;
    }
    
    
    //obtiene los productos para el buscador
    @RequestMapping(method = RequestMethod.POST, value="/get_productos_formuladesarrollo.json")
    public @ResponseBody HashMap<String,ArrayList<HashMap<String, String>>> getProductosFormulasDesarrolloJson(
            @RequestParam(value="id_formuladesarrollo", required=true) String id_formuladesarrollo,
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
        
        productos = this.getProDao().getFormulaLaboratorio_DatosMinigrid(id_formuladesarrollo, "1");
        
        jsonretorno.put("productos", productos);
        
        return jsonretorno;
    }
    
    
    //crear y editar una  formula
    @RequestMapping(method = RequestMethod.POST, value="/edit.json")
    public @ResponseBody HashMap<String, String> editJson(
            //identificador	0
        @RequestParam(value="identificador", required=true) Integer id,
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
        //prodtipo_id      guarda el id de el tipo de producto
        @RequestParam(value="select_prodtipo", required=true) String prodtipo_id,
        //id_prod_saliente	1389
        @RequestParam(value="id_prod_saliente", required=true) String inv_prod_id,
        //numero_pasos	1
        @RequestParam(value="numero_pasos", required=true) String nivel,
        //paso_actual	1
        @RequestParam(value="paso_actual", required=true) String paso_actual,
        //select_prodtipo	Normal
        @RequestParam(value="select_prodtipo", required=true) String select_prodtipo,
        //select_unidad	Kilogramo
        @RequestParam(value="select_unidad", required=true) String select_unidad,
        //numero_pasos	1
        @RequestParam(value="version", required=true) String version,
        //numero_pasos	1
        @RequestParam(value="pro_config_prod_pertenece_id", required=true) String pro_config_prod_pertenece_id,
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
        Integer app_selected = 108;//catalogo de agentes
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
                
                arreglo[i]= "'"+id_prod_componente[i] +"___" + cantidad[i]+"___" + posicion[i]+"___" + 
                        eliminado[i]+"___" + no_partida+"___" + id_reg[i]+"'";
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
        
        //                                                                                              5
        String data_string = app_selected+"___"+command_selected+"___"+id_usuario+"___"+id+"___"+id_prod_master+"___"+
                //      6           7                   8                           9               10
                inv_prod_id+"___"+nivel+"___"+pro_config_prod_pertenece_id+"___"+version+"___"+prodtipo_id;
        
        succes = this.getProDao().selectFunctionValidateAaplicativo(data_string,app_selected,extra_data_array);
        
        log.log(Level.INFO, "despues de validacion {0}", String.valueOf(succes.get("success")));
        if( String.valueOf(succes.get("success")).equals("true") ){
            actualizo = this.getProDao().selectFunctionForApp_Produccion(data_string, extra_data_array);
        }
        
        jsonretorno.put("success",String.valueOf(succes.get("success")));
        
        log.log(Level.INFO, "Salida json {0}", String.valueOf(jsonretorno.get("success")));
        return jsonretorno;
    }
    
    
    //cambia el estatus del borrado logico
    @RequestMapping(method = RequestMethod.POST, value="/logicDelete.json")
    public @ResponseBody HashMap<String, String> logicDeleteJson(
            @RequestParam(value="id", required=true) Integer id,
            @RequestParam(value="iu", required=true) String id_user,
            Model model
            ) {
        
        HashMap<String, String> jsonretorno = new HashMap<String, String>();
        //decodificar id de usuario
        Integer id_usuario = Integer.parseInt(Base64Coder.decodeString(id_user));
        
        Integer app_selected = 108;
        String command_selected = "delete";
        String extra_data_array = "'sin datos'";
        String data_string = app_selected+"___"+command_selected+"___"+id_usuario+"___"+id;
        
        System.out.println("Ejecutando borrado logico formulas");
        jsonretorno.put("success",String.valueOf( this.getProDao().selectFunctionForThisApp(data_string,extra_data_array)) );
        
        return jsonretorno;
    }
    
    /*
    //Buscador de de productos
    @RequestMapping(method = RequestMethod.POST, value="/get_buscador_productos_ingredientes.json")
    public @ResponseBody HashMap<String,ArrayList<HashMap<String, String>>> getBuscadorProductosIngredientesJson(
            @RequestParam(value="sku", required=true) String sku,
            @RequestParam(value="tipo", required=true) String tipo,
            @RequestParam(value="descripcion", required=true) String descripcion,
            @RequestParam(value="iu", required=true) String id_user,
            Model model
            ) {
        
        System.out.println("Busqueda de producto");
        
        HashMap<String,ArrayList<HashMap<String, String>>> jsonretorno = new HashMap<String,ArrayList<HashMap<String, String>>>();
        HashMap<String, String> userDat = new HashMap<String, String>();
        //decodificar id de usuario
        Integer id_usuario = Integer.parseInt(Base64Coder.decodeString(id_user));
        userDat = this.getHomeDao().getUserById(id_usuario);
        
        Integer id_empresa = Integer.parseInt(userDat.get("empresa_id"));
        
        jsonretorno.put("Productos", this.getProDao().getBuscadorProductos(sku, tipo, descripcion,id_empresa));
        
        return jsonretorno;
    }
    //fin del buscador de productos
    
    
    //localhost:8080/com.mycompany_Kemikal_war_1.0-SNAPSHOT/controllers/logasignarutas/getPdfRuta/1/NQ==/out.json
    //Genera pdf de formulacion de
    @RequestMapping(value = "/getPdfFormula/{id}/{stock}/{iu}/out.json", method = RequestMethod.GET ) 
    public ModelAndView getGeneraPdfFacturacionJson(
                @PathVariable("id") Integer id_formula,
                @PathVariable("stock") String stock,
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
        
        //obtiene las facturas del periodo indicado
        lista_productos = this.getProDao().getInv_ListaProductosFormulaPdf(id_formula);
        datos = this.getProDao().getInv_DatosFormulaPdf(id_formula);
        especificaciones = this.getProDao().getInv_DatosFormulaEspecificacionesPdf(id_formula);
        lista_procedimiento = this.getProDao().getInv_DatosFormulaProcedidmientoPdf(id_formula);
        
        datos.put("fecha", TimeHelper.getFechaActualYMDH());
        
        
        String file_name = "FORMULA_"+rfc_empresa+"_"+datos.get("folio") +".pdf";
        //ruta de archivo de salida
        String fileout = file_dir_tmp +"/"+  file_name;
        
        //instancia a la clase que construye el pdf del reporte de facturas
        PdfReporteFormulas pdf = new PdfReporteFormulas(datosEncabezadoPie, fileout,lista_productos,datos, especificaciones, stock, lista_procedimiento);
        
        System.out.println("Recuperando archivo: " + fileout);
        File file = new File(fileout);
        if (file.exists()){
            int size = (int) file.length(); // Tamaño del archivo
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
    */
}
