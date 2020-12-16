package com.agnux.kemikal.controllers;


import com.agnux.cfd.v2.Base64Coder;
import com.agnux.common.helpers.StringHelper;
import com.agnux.common.obj.DataPost;
import com.agnux.common.obj.ResourceProject;
import com.agnux.common.obj.UserSessionData;
import com.agnux.kemikal.interfacedaos.GralInterfaceDao;
import com.agnux.kemikal.interfacedaos.HomeInterfaceDao;
import com.agnux.kemikal.interfacedaos.LogInterfaceDao;
import com.agnux.kemikal.reportes.PdfReporteRutas;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;


@Controller
@SessionAttributes({"user"})
@RequestMapping("/logasignarutas/")
public class LogAsignaRutaController {
    ResourceProject resource = new ResourceProject();
    private static final Logger log  = Logger.getLogger(LogAsignaRutaController.class.getName());
    
    @Autowired
    @Qualifier("daoLog")
    private LogInterfaceDao logDao;
    
    @Autowired
    @Qualifier("daoGral")
    private GralInterfaceDao gralDao;
    
    @Autowired
    @Qualifier("daoHome")
    private HomeInterfaceDao HomeDao;
    
    public HomeInterfaceDao getHomeDao() {
        return HomeDao;
    }
    
    public LogInterfaceDao getLogDao() {
        return logDao;
    }
    
    public GralInterfaceDao getGralDao() {
        return gralDao;
    }

    @RequestMapping(value="/startup.agnux")
    public ModelAndView startUp(HttpServletRequest request, HttpServletResponse response, 
            @ModelAttribute("user") UserSessionData user
            )throws ServletException, IOException {
        
        log.log(Level.INFO, "Ejecutando starUp de {0}", LogAsignaRutaController.class.getName());
        LinkedHashMap<String,String> infoConstruccionTabla = new LinkedHashMap<String,String>();
        
        infoConstruccionTabla.put("id", "Acciones:90");
        infoConstruccionTabla.put("folio", "Folio:110");
        infoConstruccionTabla.put("vehiculo", "Vehiculo:200");
        infoConstruccionTabla.put("nombre_chofer", "Nombre Chofer:300");
        
        
        ModelAndView x = new ModelAndView("logasignarutas/startup", "title", "Asignaci&oacute;n de rutas");
        
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

        //id de usuario codificado
        x = x.addObject("iu", codificado);
        
        return x;
    }

    @RequestMapping(value="/getAllRutas.json", method = RequestMethod.POST)
    public @ResponseBody HashMap<String,ArrayList<HashMap<String, Object>>> getAllRutasJson(
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
        
	//aplicativo asignacion de rutas
        Integer app_selected = 72;
        
        //decodificar id de usuario
        Integer id_usuario = Integer.parseInt(Base64Coder.decodeString(id_user_cod));
        
        //variables para el buscador
        String ruta = "%"+StringHelper.isNullString(String.valueOf(has_busqueda.get("ruta")))+"%";
        String marca = "%"+StringHelper.isNullString(String.valueOf(has_busqueda.get("marca")))+"%";
        String chofer = "%"+StringHelper.isNullString(String.valueOf(has_busqueda.get("chofer")))+"%";
        //String grupo = StringHelper.isNullString(String.valueOf(has_busqueda.get("grupo")));
        
        String data_string = app_selected+"___"+id_usuario+"___"+ruta+"___"+marca+"___"+chofer;
        
        //obtiene total de registros en base de datos, con los parametros de busqueda
        int total_items = this.getLogDao().countAll(data_string);           
        
        //calcula el total de paginas
        int total_pags = resource.calculaTotalPag(total_items,items_por_pag);
        
        //variables que necesita el datagrid, para no tener que hacer uno por cada aplicativo
        DataPost dataforpos = new DataPost(orderby, desc, items_por_pag, pag_start, display_pag, input_json, cadena_busqueda,total_items,total_pags, id_user_cod);
        
        int offset = resource.__get_inicio_offset(items_por_pag, pag_start);
        
        //obtiene los registros para el grid, de acuerdo a los parametros de busqueda
        jsonretorno.put("Data", this.getLogDao().getRutas_PaginaGrid(data_string, offset, items_por_pag, orderby, desc));
        //obtiene el hash para los datos que necesita el datagrid
        jsonretorno.put("DataForGrid", dataforpos.formaHashForPos(dataforpos));
        
        return jsonretorno;
    }
    
    
    
    
    
    @RequestMapping(method = RequestMethod.POST, value="/getFacturas.json")
    public @ResponseBody HashMap<String,ArrayList<HashMap<String, String>>> getFacturasJson(
            //@RequestParam(value="id", required=true) Integer id,
            @RequestParam(value="fecha_inicial", required=true) String fecha_inicial,
            @RequestParam(value="fecha_final", required=true) String fecha_final,
            @RequestParam(value="factura", required=true) String factura,
            @RequestParam(value="tipo_busqueda", required=true) Integer tipo_busqueda,
            @RequestParam(value="iu", required=true) String id_user_cod,
            Model model
            ) {
        
         log.log(Level.INFO, "Ejecutando getFacturasJson de {0}", LogAsignaRutaController.class.getName());
         HashMap<String,ArrayList<HashMap<String, String>>> jsonretorno = new HashMap<String,ArrayList<HashMap<String, String>>>();
         HashMap<String, String> userDat = new HashMap<String, String>();
         ArrayList<HashMap<String, String>> facturas = new ArrayList<HashMap<String, String>>();
         //ArrayList<HashMap<String, String>> facturas_rev_cobro = new ArrayList<HashMap<String, String>>();
         
        //decodificar id de usuario
        Integer id_usuario = Integer.parseInt(Base64Coder.decodeString(id_user_cod));
        userDat = this.getHomeDao().getUserById(id_usuario);
        Integer id_empresa = Integer.parseInt(userDat.get("empresa_id"));
        
        facturas = this.getLogDao().getFacturas_entrega_mercancia(id_empresa,fecha_inicial,fecha_final,factura.toUpperCase(), tipo_busqueda);
        
        jsonretorno.put("Facturas_a_entregar", facturas);
        //jsonretorno.put("Facturas_fac_rev_cobro", facturas_rev_cobro);
        
        return jsonretorno;
    }
    
    
    
    
    ///
    @RequestMapping(method = RequestMethod.POST, value="/getFacturas_Rev_cobro.json")
    public @ResponseBody HashMap<String,ArrayList<HashMap<String, String>>> getFacturas_Rev_cobroJson(
            //@RequestParam(value="id", required=true) Integer id,
            @RequestParam(value="folio", required=true) String folio,
            @RequestParam(value="iu", required=true) String id_user_cod,
            Model model
            ) {
        
     log.log(Level.INFO, "Ejecutando getFacturas_Rev_cobroJson de {0}", LogAsignaRutaController.class.getName());
     HashMap<String,ArrayList<HashMap<String, String>>> jsonretorno = new HashMap<String,ArrayList<HashMap<String, String>>>();
     HashMap<String, String> userDat = new HashMap<String, String>();
     
     ArrayList<HashMap<String, String>> facturas_rev_cobro = new ArrayList<HashMap<String, String>>();
     //decodificar id de usuario
        Integer id_usuario = Integer.parseInt(Base64Coder.decodeString(id_user_cod));
        userDat = this.getHomeDao().getUserById(id_usuario);
        
        Integer id_empresa = Integer.parseInt(userDat.get("empresa_id"));
        
        
        facturas_rev_cobro = this.getLogDao().getFacturas_fac_rev_cobro_detalle(id_empresa,folio);
        jsonretorno.put("Facturas_fac_rev_cobro", facturas_rev_cobro);
        
        return jsonretorno;
    }
    ///
    
    
  
    
    @RequestMapping(method = RequestMethod.POST, value="/getrutas.json")
    public @ResponseBody HashMap<String,ArrayList<HashMap<String, String>>> getrutasJson(
            @RequestParam(value="id", required=true) Integer id,
            
            @RequestParam(value="iu", required=true) String id_user_cod,
            Model model
            ) {
        
        log.log(Level.INFO, "Ejecutando getrutasJson de {0}", LogAsignaRutaController.class.getName());
        HashMap<String,ArrayList<HashMap<String, String>>> jsonretorno = new HashMap<String,ArrayList<HashMap<String, String>>>();
        ArrayList<HashMap<String, String>> choferes = new ArrayList<HashMap<String, String>>();
        ArrayList<HashMap<String, String>> vehiculo = new ArrayList<HashMap<String, String>>();
       
        HashMap<String, String> userDat = new HashMap<String, String>();
     
     
     //decodificar id de usuario
        Integer id_usuario = Integer.parseInt(Base64Coder.decodeString(id_user_cod));
        userDat = this.getHomeDao().getUserById(id_usuario);
        
        
       Integer id_empresa = Integer.parseInt(userDat.get("empresa_id"));
        
        choferes = this.getLogDao().getchoferes(id_empresa);
        vehiculo = this.getLogDao().getvehiculo(id_empresa);
               
        
        
        jsonretorno.put("choferes", choferes);
        jsonretorno.put("vehiculos", vehiculo);
        
        //jsonretorno.put("Grupos", TpolGrupos);
        
        return jsonretorno;
    }
    
    @RequestMapping(method = RequestMethod.POST, value="/getrutas_editar.json")
    public @ResponseBody HashMap<String,ArrayList<HashMap<String, String>>> getrutas_editarJson(
            @RequestParam(value="id", required=true) Integer id_ruta,
            @RequestParam(value="iu", required=true) String id_user_cod,
            Model model
            ) {
        
        log.log(Level.INFO, "Ejecutando getrutas_editarJson de {0}", LogAsignaRutaController.class.getName());
        HashMap<String,ArrayList<HashMap<String, String>>> jsonretorno = new HashMap<String,ArrayList<HashMap<String, String>>>();
        ArrayList<HashMap<String, String>> Datos_editar_header = new ArrayList<HashMap<String, String>>();
        ArrayList<HashMap<String, String>> Datos_editar_minigridrutas = new ArrayList<HashMap<String, String>>();
        ArrayList<HashMap<String, String>> Datos_editar_minigridFRC = new ArrayList<HashMap<String, String>>();

        ArrayList<HashMap<String, String>> choferes = new ArrayList<HashMap<String, String>>();
        ArrayList<HashMap<String, String>> vehiculo = new ArrayList<HashMap<String, String>>();
        HashMap<String, String> userDat = new HashMap<String, String>();

     
        //decodificar id de usuario
        Integer id_usuario = Integer.parseInt(Base64Coder.decodeString(id_user_cod));
        userDat = this.getHomeDao().getUserById(id_usuario);

        Integer id_empresa = Integer.parseInt(userDat.get("empresa_id"));

        choferes = this.getLogDao().getchoferes(id_empresa);
        vehiculo = this.getLogDao().getvehiculo( id_empresa);
        Datos_editar_header = this.getLogDao().getdatos_editar_header(id_ruta);
        Datos_editar_minigridrutas = this.getLogDao().getdatos_editar_minigridRutas(id_empresa,id_ruta);
        Datos_editar_minigridFRC = this.getLogDao().getdatos_editar_minigridFRC(id_ruta);
        
        jsonretorno.put("choferes", choferes);
        jsonretorno.put("vehiculos", vehiculo);
        
        jsonretorno.put("header", Datos_editar_header);
        jsonretorno.put("minigrid_Rutas", Datos_editar_minigridrutas);
        jsonretorno.put("minigrid_FRC", Datos_editar_minigridFRC);
        
        
        //jsonretorno.put("Grupos", TpolGrupos);
        
        return jsonretorno;
    }
    
    
    
    
    
    //crear y editar
    @RequestMapping(method = RequestMethod.POST, value="/edit.json")
    public @ResponseBody HashMap<String, String> editJson(
            @RequestParam(value="identificador", required=true) Integer id,
            @RequestParam(value="select_chofer", required=false) String chofer_id,
            @RequestParam(value="select_unidad", required=false) String unidad_id,
            @RequestParam(value="folio_fac_rev_cobro", required=false) String folio,
            @RequestParam(value="accion_proceso", required=false) String accion_proceso,
            @RequestParam(value="id_detalle", required=false) String[] id_detalle,  
            @RequestParam(value="tipo", required=true) String[] tipo,  
            @RequestParam(value="fac_docs_detalle_id", required=true) String[] fac_docs_id,  
            @RequestParam(value="inv_prod_id", required=true) String[] inv_prod_id,
            @RequestParam(value="envase", required=true) String[] envase, 
            @RequestParam(value="fac_rev_cobro", required=true) String[] fac_rev_cobro, 
            @RequestParam(value="seleccionado", required=true) String[] seleccionado,
            @RequestParam(value="eliminado", required=true) String[] eliminado,
            Model model,@ModelAttribute("user") UserSessionData user
            ) {
        
        HashMap<String, String> jsonretorno = new HashMap<String, String>();
        HashMap<String, String> succes = new HashMap<String, String>();
        Integer app_selected = 72;
        String command_selected = "";
        Integer id_usuario= user.getUserId();//variable para el id  del usuario
        //String extra_data_array = "'sin datos'";
        String actualizo = "0";
        Integer contador=0;
        String arreglo[];
        String extra_data_array="";
        int pos=0;
        
        
        if( id == 0  ){
            command_selected = "new";
            for (int i=0; i<seleccionado.length; i++){
                if(seleccionado[i].equals("1")){
                     contador++;
                }
            }
            
            arreglo = new String[contador];
            for(int i=0; i<fac_docs_id.length; i++) { 
                if(seleccionado[i].equals("1")){
                    arreglo[pos]= "'"+tipo[i]+"___"+fac_docs_id[i]+"___"+inv_prod_id[i]+"___"+envase[i]+"___" + fac_rev_cobro[i]+"___" + seleccionado[i]+"___" + id_detalle[i]+"___"+eliminado[i]+"'";
                    //System.out.println(arreglo[pos]);
                    pos++;
                }
            }
            
            //serializar el arreglo
            extra_data_array = StringUtils.join(arreglo, ",");
            
        }else{
            
            command_selected = "edit";
            
            arreglo = new String[seleccionado.length];
            for(int i=0; i<fac_docs_id.length; i++) { 
                arreglo[i]= "'"+tipo[i]+"___"+fac_docs_id[i]+"___"+inv_prod_id[i]+"___"+envase[i]+"___" + fac_rev_cobro[i]+"___" + seleccionado[i]+"___" + id_detalle[i]+"___"+eliminado[i]+"'";
                //System.out.println(arreglo[i]);
            }
            
            //serializar el arreglo
            extra_data_array = StringUtils.join(arreglo, ",");
        }
        
        String data_string = app_selected+"___"+command_selected+"___"+id_usuario+"___"+id+"___"+chofer_id+"___"+unidad_id+"___"+accion_proceso;
        
        
        succes = this.getLogDao().selectFunctionValidateAaplicativo(data_string,app_selected,extra_data_array);
        
        log.log(Level.INFO, "despues de validacion {0}", String.valueOf(succes.get("success")));
        if( String.valueOf(succes.get("success")).equals("true") ){
            actualizo = this.getLogDao().selectFunctionForLogAdmProcesos(data_string, extra_data_array);
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
        
        Integer app_selected = 72;
        String command_selected = "delete";
        String extra_data_array = "'sin datos'";
        String data_string = app_selected+"___"+command_selected+"___"+id_usuario+"___"+id;
        
        System.out.println("Ejecutando borrado logico de tipo poliza");
        jsonretorno.put("success",String.valueOf( this.logDao.selectFunctionForLogAdmProcesos(data_string,extra_data_array)) );
        
        return jsonretorno;
    }
    
    
    
    
    
    //localhost:8080/com.mycompany_Kemikal_war_1.0-SNAPSHOT/controllers/logasignarutas/getPdfRuta/1/NQ==/out.json
    //Genera pdf de facturacion de
    @RequestMapping(value = "/getPdfRuta/{id}/{iu}/out.json", method = RequestMethod.GET ) 
    public ModelAndView getGeneraPdfFacturacionJson(
                @PathVariable("id") Integer id_ruta,
                @PathVariable("iu") String id_user,
                HttpServletRequest request, 
                HttpServletResponse response, 
                Model model)
            throws ServletException, IOException, URISyntaxException, DocumentException {
        
        HashMap<String, String> userDat = new HashMap<String, String>();
        HashMap<String, String> datos= new HashMap<String, String>();
        HashMap<String, String> datosEncabezadoPie= new HashMap<String, String>();
        ArrayList<HashMap<String, String>> lista_facturas = new ArrayList<HashMap<String, String>>();
        
        System.out.println("Generando Pdf de Ruta");
        
        //decodificar id de usuario
        Integer id_usuario = Integer.parseInt(Base64Coder.decodeString(id_user));
        Integer app_selected = 72; //aplicativo asignacion de Rutas
        
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
        lista_facturas = this.getLogDao().getRuta_ListaFacturasPdf(id_ruta);
        datos = this.getLogDao().getRuta_DatosPdf(id_ruta);
        
        String file_name = "RUTA_"+rfc_empresa+"_"+datos.get("folio") +".pdf";
        //ruta de archivo de salida
        String fileout = file_dir_tmp +"/"+  file_name;
        
        //instancia a la clase que construye el pdf del reporte de facturas
        PdfReporteRutas pdf = new PdfReporteRutas(datosEncabezadoPie, fileout,lista_facturas,datos);
        
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







    
    
    
    
    
    
    
    
    
    
    
    
    
    
   
}
