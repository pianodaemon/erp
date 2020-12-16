package com.agnux.kemikal.controllers;

import com.agnux.cfd.v2.Base64Coder;
import com.agnux.common.helpers.FileHelper;
import com.agnux.common.helpers.StringHelper;
import com.agnux.common.obj.DataPost;
import com.agnux.common.obj.ResourceProject;
import com.agnux.common.obj.UserSessionData;
import com.agnux.kemikal.interfacedaos.GralInterfaceDao;
import com.agnux.kemikal.interfacedaos.HomeInterfaceDao;
import com.agnux.kemikal.interfacedaos.InvInterfaceDao;
import com.agnux.kemikal.reportes.pdfOrdenSubensamble;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;


@Controller
@SessionAttributes({"user"})
@RequestMapping("/invordsubensamble/")
public class InvOrdSubensambleController {
    ResourceProject resource = new ResourceProject();
    private static final Logger log  = Logger.getLogger(InvOrdSubensambleController.class.getName());
    
    @Autowired
    @Qualifier("daoInv")
    private InvInterfaceDao invDao;
    
    public InvInterfaceDao getInvDao() {
        return invDao;
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
            @ModelAttribute("user") UserSessionData user
            )throws ServletException, IOException {
        
        log.log(Level.INFO, "Ejecutando starUp de {0}", InvOrdSubensambleController.class.getName());
        LinkedHashMap<String,String> infoConstruccionTabla = new LinkedHashMap<String,String>();
        
        infoConstruccionTabla.put("id", "Acciones:90");
        infoConstruccionTabla.put("folio", "Folio:90");
        infoConstruccionTabla.put("estatus", "Estatus:100");
        infoConstruccionTabla.put("momento_creacion", "Fecha:100");
        infoConstruccionTabla.put("almacen", "Almacen:220");
        
        ModelAndView x = new ModelAndView("invordsubensamble/startup", "title", "Confirmar Orden de Producci&oacute;n Subemsamble");
        
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
        //String decodificado = Base64Coder.decodeString(codificado);
        
        //id de usuario codificado
        x = x.addObject("iu", codificado);
        
        return x;
    }
    
    
    
    
    @RequestMapping(value="/getAllInvOrdSubensamble.json", method = RequestMethod.POST)
    public @ResponseBody HashMap<String,ArrayList<HashMap<String, Object>>> getAllInvOrdSubensambleJson(
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
        
        //aplicativo tipos de poliza
        Integer app_selected = 58;
        
        //decodificar id de usuario
        Integer id_usuario = Integer.parseInt(Base64Coder.decodeString(id_user_cod));
        
        //variables para el buscador
        String folio = "%"+StringHelper.isNullString(String.valueOf(has_busqueda.get("folio")))+"%";
        String codigo = "%"+StringHelper.isNullString(String.valueOf(has_busqueda.get("codigo")))+"%";
        String descripcion = "%"+StringHelper.isNullString(String.valueOf(has_busqueda.get("descripcion")))+"%";
        
        String data_string = app_selected+"___"+id_usuario+"___"+folio+"___"+codigo+"___"+descripcion;
        
        //obtiene total de registros en base de datos, con los parametros de busqueda
        int total_items = this.getInvDao().countAll(data_string);
        
        //calcula el total de paginas
        int total_pags = resource.calculaTotalPag(total_items,items_por_pag);
        
        //variables que necesita el datagrid, para no tener que hacer uno por cada aplicativo
        DataPost dataforpos = new DataPost(orderby, desc, items_por_pag, pag_start, display_pag, input_json, cadena_busqueda,total_items,total_pags, id_user_cod);
        
        int offset = resource.__get_inicio_offset(items_por_pag, pag_start);
        
        //obtiene los registros para el grid, de acuerdo a los parametros de busqueda
        jsonretorno.put("Data", this.getInvDao().getInvOrdSubenGrid(data_string, offset, items_por_pag, orderby, desc));
        //obtiene el hash para los datos que necesita el datagrid
        jsonretorno.put("DataForGrid", dataforpos.formaHashForPos(dataforpos));
        
        return jsonretorno;
    }
    
    
    
    
    
    
    
    //actualizar estatus a enterado
    @RequestMapping(method = RequestMethod.POST, value="/getActualizarEstatus.json")
    public @ResponseBody HashMap<String, String> editJson(
            @RequestParam(value="id_subensamble", required=true) String id_subensamble,
            @RequestParam(value="estatus", required=true) Integer estatus,
            @RequestParam(value="iu", required=true) String id_user_cod,
            Model model
            ) {
        
        HashMap<String, String> jsonretorno = new HashMap<String, String>();
        //HashMap<String, String> succes = new HashMap<String, String>();
        Integer app_selected = 58;
        String command_selected = "actualiza_estatus";
        //decodificar id de usuario
        Integer id_usuario = Integer.parseInt(Base64Coder.decodeString(id_user_cod));
        String arreglo[];
        String extra_data_array = "'sin datos'";
        String actualizo = "0";
        
        String data_string = app_selected+"___"+command_selected+"___"+id_usuario+"___"+id_subensamble+"___"+estatus;
        /*
        succes = this.getInvDao().selectFunctionValidateAaplicativo(data_string,app_selected,extra_data_array);
        log.log(Level.INFO, "despues de validacion {0}", String.valueOf(succes.get("success")));
        if( String.valueOf(succes.get("success")).equals("true") ){
            actualizo = this.getInvDao().selectFunctionForApp_MovimientosInventario(data_string, extra_data_array);
        }*/
        
        actualizo = this.getInvDao().selectFunctionForApp_MovimientosInventario(data_string, extra_data_array);
        
        jsonretorno.put("success",String.valueOf(actualizo));
        
        log.log(Level.INFO, "Salida Actualiza estatus {0}", String.valueOf(jsonretorno.get("success")));
        return jsonretorno;
    }
    
    
    
    
    //este es solo para probar
    @RequestMapping(method = RequestMethod.POST, value="/getInvOrdSub.json")
    public @ResponseBody HashMap<String,ArrayList<HashMap<String, String>>> getInvOrdSubJson(
            @RequestParam(value="id", required=true) String id,
            Model model
            ) {
        
        log.log(Level.INFO, "Ejecutando getInvOrdSub de {0}", InvOrdSubensambleController.class.getName());
        HashMap<String,ArrayList<HashMap<String, String>>> jsonretorno = new HashMap<String,ArrayList<HashMap<String, String>>>();
        ArrayList<HashMap<String, String>> datosInvOrdSub = new ArrayList<HashMap<String, String>>();
        ArrayList<HashMap<String, String>> detalleOrden = new ArrayList<HashMap<String, String>>();
        ArrayList<HashMap<String, String>> lista_componentes = new ArrayList<HashMap<String, String>>();
        
        if(! id.equals("0")  ){
            datosInvOrdSub = this.getInvDao().getInvOrdSuben_Datos(id);
            detalleOrden = this.getInvDao().getInvDetalleOrdSuben(id);
            lista_componentes = this.getInvDao().getInvOrdPreSubenDatosComponentesPorProducto(id);
        }
        
        //TpolGrupos = this.getCtbDao().getTipoPoliza_Grupos();
        
        jsonretorno.put("InvOrdSub", datosInvOrdSub);
        jsonretorno.put("Detalle", detalleOrden);
        jsonretorno.put("componentes", lista_componentes);
        
        return jsonretorno;
    }
    
    //obtienetipos de productos
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
       
        arrayTiposProducto=this.getInvDao().getProducto_Tipos();
       
        //cadenaLineas.put("cad_lineas", genera_treeview( this.getInvDao().getProducto_Lineas() ));
        //arrayLineas.add(cadenaLineas);
        //jsonretorno.put("Lines",arrayLineas);
        jsonretorno.put("prodTipos", arrayTiposProducto);
       
        return jsonretorno;
    }
    
    
    //crear y editar
    @RequestMapping(method = RequestMethod.POST, value="/edit.json")
    public @ResponseBody HashMap<String, String> editJson(
            @RequestParam(value="identificador", required=true) String id,
            @RequestParam(value="folio", required=true) String folio,
            @RequestParam(value="observaciones", required=false) String observaciones,
            @RequestParam(value="estatus", required=false) String estatus,
            Model model,@ModelAttribute("user") UserSessionData user
        ) {
        
        HashMap<String, String> jsonretorno = new HashMap<String, String>();
        HashMap<String, String> succes = new HashMap<String, String>();
        Integer app_selected = 58;
        String command_selected = "new";
        Integer id_usuario= user.getUserId();//variable para el id  del usuario
        int no_partida = 0;
        String arreglo[];
        String extra_data_array = "'sin datos'";
        String actualizo = "0";
        
        
        if( id.equals("0") ){
            command_selected = "new";
        }else{
            command_selected = "edit";
        }
        
        //                      1                   2                       3           4          5              6                 7
        String data_string = app_selected+"___"+command_selected+"___"+id_usuario+"___"+id+"___"+folio+"___"+observaciones+"___"+estatus;
        
        succes = this.getInvDao().selectFunctionValidateAaplicativo(data_string,app_selected,extra_data_array);
        
        log.log(Level.INFO, "despues de validacion {0}", String.valueOf(succes.get("success")));
        if( String.valueOf(succes.get("success")).equals("true") ){
            actualizo = this.getInvDao().selectFunctionForApp_MovimientosInventario(data_string, extra_data_array);
        }
        
        jsonretorno.put("success",String.valueOf(succes.get("success")));
        
        log.log(Level.INFO, "Salida json {0}", String.valueOf(jsonretorno.get("success")));
        return jsonretorno;
    }
    
    
    //cambiar a borrado logico un registro
    @RequestMapping(method = RequestMethod.POST, value="/logicDelete.json")
    public @ResponseBody HashMap<String, String> logicDeleteJson(
            @RequestParam(value="id", required=true) Integer id,
            @RequestParam(value="iu", required=true) String id_user,
            Model model
            ) {
        
        HashMap<String, String> jsonretorno = new HashMap<String, String>();
        
        //decodificar id de usuario
        Integer id_usuario = Integer.parseInt(Base64Coder.decodeString(id_user));
        
        Integer app_selected = 58;
        String command_selected = "delete";
        String extra_data_array = "'sin datos'";
        String data_string = app_selected+"___"+command_selected+"___"+id_usuario+"___"+id;
        
        System.out.println("Ejecutando borrado logico de tipo poliza");
        jsonretorno.put("success",String.valueOf( this.getInvDao().selectFunctionForThisApp(data_string,extra_data_array)) );
        
        return jsonretorno;
    }
    
    
    @RequestMapping(value = "/get_genera_pdf_ordensubensamble/{id_orden}/{iu}/out.json", method = RequestMethod.GET ) 
    public ModelAndView getGeneraPdfOrdenSubensambleJson(
                @PathVariable("id_orden") String id_orden,
                @PathVariable("iu") String id_user,
                HttpServletRequest request, 
                HttpServletResponse response, 
                Model model)
            throws ServletException, IOException, URISyntaxException, DocumentException, Exception {
        
        HashMap<String, String> userDat = new HashMap<String, String>();
        ArrayList<HashMap<String, String>> datosOrdenSuben = new ArrayList<HashMap<String, String>>();
        HashMap<String, String> datos_empresa = new HashMap<String, String>();
        HashMap<String, String> datos_entrada = new HashMap<String, String>();
        ArrayList<HashMap<String, String>> lista_productos = new ArrayList<HashMap<String, String>>();
        ArrayList<HashMap<String, String>> lista_componentes = new ArrayList<HashMap<String, String>>();
        
        System.out.println("Generando PDF de Orden de Produccion");
        
        //decodificar id de usuario
        Integer id_usuario = Integer.parseInt(Base64Coder.decodeString(id_user));
        
        userDat = this.getHomeDao().getUserById(id_usuario);
        Integer id_empresa = Integer.parseInt(userDat.get("empresa_id"));
        
        String razon_social_empresa = this.getGralDao().getRazonSocialEmpresaEmisora(id_empresa);
        String rfc_empresa = this.getGralDao().getRfcEmpresaEmisora(id_empresa);
        
        //obtener el directorio temporal
        String dir_tmp = this.getGralDao().getTmpDir();
        
        String ruta_imagen = this.getGralDao().getImagesDir()+rfc_empresa+"_logo.png";
        File file_dir_tmp = new File(dir_tmp);
        String file_name = "ORDEN_PRODUCC_"+rfc_empresa+".pdf";
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
        
        
        datosOrdenSuben = this.getInvDao().getInvOrdSuben_Datos(id_orden);
        
        datos_entrada.put("folio", String.valueOf(datosOrdenSuben.get(0).get("folio")));
        datos_entrada.put("fecha", String.valueOf(datosOrdenSuben.get(0).get("fecha")));
        datos_entrada.put("comentarios",String.valueOf(datosOrdenSuben.get(0).get("comentarios")));
        
        lista_productos = this.getInvDao().getInvDetalleOrdSuben(id_orden);
        
        lista_componentes = this.getInvDao().getInvOrdPreSubenDatosComponentesPorProducto(id_orden);
        //obtiene el listado de productos para el pdf
        //lista_productos = this.getInvDao().getEntradas_DatosGrid(id_entrada);
        
        //instancia a la clase que construye el pdf del reporte de facturas
        
        pdfOrdenSubensamble ent = new pdfOrdenSubensamble(datos_empresa, datos_entrada,lista_productos,lista_componentes, fileout, ruta_imagen);
        
        
        System.out.println("Recuperando archivo: " + fileout);
        File file = new File(fileout);
        int size = (int) file.length(); // Tamaño del archivo
        BufferedInputStream bis = new BufferedInputStream(new FileInputStream(file));
        response.setBufferSize(size);
        response.setContentLength(size);
        response.setContentType("application/pdf");
        response.setHeader("Content-Disposition","attachment; filename=\"" + file.getCanonicalPath() +"\"");
        FileCopyUtils.copy(bis, response.getOutputStream());  	
        response.flushBuffer();
        
        FileHelper.delete(fileout);
        
        return null;
    }
}
