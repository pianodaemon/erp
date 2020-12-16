package com.agnux.kemikal.controllers;


import com.agnux.cfd.v2.Base64Coder;
import com.agnux.common.helpers.FileHelper;
import com.agnux.common.helpers.StringHelper;
import com.agnux.common.obj.DataPost;
import com.agnux.common.obj.ResourceProject;
import com.agnux.common.obj.UserSessionData;
import com.agnux.kemikal.interfacedaos.GralInterfaceDao;
import com.agnux.kemikal.interfacedaos.HomeInterfaceDao;
import com.agnux.kemikal.interfacedaos.PocInterfaceDao;
import com.agnux.kemikal.reportes.PdfRemision;
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


@Controller
@SessionAttributes({"user"})
@RequestMapping("/remisiones/")
public class RemisionesController {
    ResourceProject resource = new ResourceProject();
    private static final Logger log  = Logger.getLogger(RemisionesController.class.getName());
    
    @Autowired
    @Qualifier("daoGral")
    private GralInterfaceDao gralDao;
    
    //dao de procesos comerciales
    @Autowired
    @Qualifier("daoPoc")
    private PocInterfaceDao PocDao;
    
    @Autowired
    @Qualifier("daoHome")
    private HomeInterfaceDao HomeDao;
    
    public HomeInterfaceDao getHomeDao() {
        return HomeDao;
    }
    
    public PocInterfaceDao getPocDao() {
        return PocDao;
    }
    
    public GralInterfaceDao getGralDao() {
        return gralDao;
    }
    
    
    @RequestMapping(value="/startup.agnux")
    public ModelAndView startUp(HttpServletRequest request, HttpServletResponse response, 
            @ModelAttribute("user") UserSessionData user
            )throws ServletException, IOException {
        
        log.log(Level.INFO, "Ejecutando starUp de {0}", RemisionesController.class.getName());
        LinkedHashMap<String,String> infoConstruccionTabla = new LinkedHashMap<String,String>();
        
        infoConstruccionTabla.put("id", "Acciones:70");
        infoConstruccionTabla.put("folio", "Folio:70");
        infoConstruccionTabla.put("cliente", "Cliente:320");
        infoConstruccionTabla.put("total", "Monto:100");
        infoConstruccionTabla.put("denominacion", "Moneda:70");
        infoConstruccionTabla.put("folio_pedido", "Pedido:70");
        infoConstruccionTabla.put("estado", "Estado:100");
        infoConstruccionTabla.put("fecha_creacion","Fecha creacion:110");
        
        ModelAndView x = new ModelAndView("remisiones/startup", "title", "Remisiones de Clientes");
        
        x = x.addObject("layoutheader", resource.getLayoutheader());
        x = x.addObject("layoutmenu", resource.getLayoutmenu());
        x = x.addObject("layoutfooter", resource.getLayoutfooter());
        x = x.addObject("grid", resource.generaGrid(infoConstruccionTabla));
        x = x.addObject("url", resource.getUrl(request));
        x = x.addObject("username", user.getUserName());
        x = x.addObject("empresa", user.getRazonSocialEmpresa());
        x = x.addObject("sucursal", user.getSucursal());
        
        String userId = String.valueOf(user.getUserId());
        
        //System.out.println("id_de_usuario: "+userId);
        
        String codificado = Base64Coder.encodeString(userId);
        
        //id de usuario codificado
        x = x.addObject("iu", codificado);
        
        return x;
    }
    
    
    
    @RequestMapping(value="/getAllRemisiones.json", method = RequestMethod.POST)
    public @ResponseBody HashMap<String,ArrayList<HashMap<String, Object>>> getAllRemisionesJson(
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
        
        //aplicativo Remisiones de Clientes
        Integer app_selected = 66;
        
        //decodificar id de usuario
        Integer id_usuario = Integer.parseInt(Base64Coder.decodeString(id_user_cod));
        
        //variables para el buscador
        String folio = "%"+StringHelper.isNullString(String.valueOf(has_busqueda.get("folio")))+"%";
        String cliente = "%"+StringHelper.isNullString(String.valueOf(has_busqueda.get("cliente")))+"%";
        String fecha_inicial = ""+StringHelper.isNullString(String.valueOf(has_busqueda.get("fecha_inicial")))+"";
        String fecha_final = ""+StringHelper.isNullString(String.valueOf(has_busqueda.get("fecha_final")))+"";
        String codigo = "%"+StringHelper.isNullString(String.valueOf(has_busqueda.get("codigo")))+"%";
        String producto = "%"+StringHelper.isNullString(String.valueOf(has_busqueda.get("producto")))+"%";
        String agente = ""+StringHelper.isNullString(String.valueOf(has_busqueda.get("agente")))+"";
        String folio_pedido = "%"+StringHelper.isNullString(String.valueOf(has_busqueda.get("folio_pedido")))+"%";
        
        String data_string = app_selected+"___"+id_usuario+"___"+folio+"___"+cliente+"___"+fecha_inicial+"___"+fecha_final+"___"+codigo+"___"+producto+"___"+agente+"___"+folio_pedido;
        
        //obtiene total de registros en base de datos, con los parametros de busqueda
        int total_items = this.getPocDao().countAll(data_string);
        
        //calcula el total de paginas
        int total_pags = resource.calculaTotalPag(total_items,items_por_pag);
        
        //variables que necesita el datagrid, para no tener que hacer uno por cada aplicativo
        DataPost dataforpos = new DataPost(orderby, desc, items_por_pag, pag_start, display_pag, input_json, cadena_busqueda,total_items,total_pags, id_user_cod);
        
        int offset = resource.__get_inicio_offset(items_por_pag, pag_start);
        
        //obtiene los registros para el grid, de acuerdo a los parametros de busqueda
        jsonretorno.put("Data", this.getPocDao().getRemisiones_PaginaGrid(data_string, offset, items_por_pag, orderby, desc));
        //obtiene el hash para los datos que necesita el datagrid
        jsonretorno.put("DataForGrid", dataforpos.formaHashForPos(dataforpos));
        
        return jsonretorno;
    }
    
    
    
    
    //obtiene los Agentes para el Buscador pricipal del Aplicativo
    @RequestMapping(method = RequestMethod.POST, value="/getAgentesParaBuscador.json")
    public @ResponseBody HashMap<String,ArrayList<HashMap<String, String>>> getAgentesParaBuscador(
            @RequestParam(value="iu", required=true) String id_user_cod,
            Model model
        ) {
        
        HashMap<String,ArrayList<HashMap<String, String>>> jsonretorno = new HashMap<String,ArrayList<HashMap<String, String>>>();
        HashMap<String, String> userDat = new HashMap<String, String>();
        ArrayList<HashMap<String, String>> agentes = new ArrayList<HashMap<String, String>>();
        
        //decodificar id de usuario
        Integer id_usuario = Integer.parseInt(Base64Coder.decodeString(id_user_cod));
        userDat = this.getHomeDao().getUserById(id_usuario);
        Integer id_empresa = Integer.parseInt(userDat.get("empresa_id"));
        Integer id_sucursal = Integer.parseInt(userDat.get("sucursal_id"));
        boolean obtener_todos_los_agentes=false;
        
        agentes = this.getPocDao().getAgentes(id_empresa, id_sucursal, obtener_todos_los_agentes);
        
        jsonretorno.put("Agentes", agentes);
        return jsonretorno;
    }
    
    
    
    @RequestMapping(method = RequestMethod.POST, value="/getRemision.json")
    public @ResponseBody HashMap<String,Object> getRemisionJson(
            @RequestParam(value="id_remision", required=true) String id_remision,
            @RequestParam(value="iu", required=true) String id_user,
            Model model
            ) {
        
        log.log(Level.INFO, "Ejecutando getRemisionJson de {0}", RemisionesController.class.getName());
        HashMap<String,Object> jsonretorno = new HashMap<String,Object>();
        ArrayList<HashMap<String, String>> datosRemision = new ArrayList<HashMap<String, String>>();
        ArrayList<HashMap<String, String>> datosGrid = new ArrayList<HashMap<String, String>>();
        ArrayList<HashMap<String, String>> valorIva = new ArrayList<HashMap<String, String>>();
        ArrayList<HashMap<String, String>> tipoCambioActual = new ArrayList<HashMap<String, String>>();
        HashMap<String, String> tc = new HashMap<String, String>();
        HashMap<String, String> userDat = new HashMap<String, String>();
        boolean obtener_todos_los_agentes=true;
        
        //decodificar id de usuario
        Integer id_usuario = Integer.parseInt(Base64Coder.decodeString(id_user));
        userDat = this.getHomeDao().getUserById(id_usuario);
        
        Integer id_empresa = Integer.parseInt(userDat.get("empresa_id"));
        Integer id_sucursal = Integer.parseInt(userDat.get("sucursal_id"));
        
        if( (id_remision.equals("0"))==false  ){
            datosRemision = this.getPocDao().getRemisiones_Datos(Integer.parseInt(id_remision));
            datosGrid = this.getPocDao().getRemisiones_DatosGrid(Integer.parseInt(id_remision));
        }
        
        valorIva= this.getPocDao().getValoriva(id_sucursal);
        tc.put("tipo_cambio", StringHelper.roundDouble(this.getPocDao().getTipoCambioActual(), 4)) ;
        tipoCambioActual.add(0,tc);
        
        jsonretorno.put("datosRemision", datosRemision);
        jsonretorno.put("datosGrid", datosGrid);
        jsonretorno.put("iva", valorIva);
        jsonretorno.put("Monedas", this.getPocDao().getMonedas());
        jsonretorno.put("Tc", tipoCambioActual);
        jsonretorno.put("Vendedores", this.getPocDao().getAgentes(id_empresa, id_sucursal, obtener_todos_los_agentes));
        jsonretorno.put("Condiciones", this.getPocDao().getCondicionesDePago());
        jsonretorno.put("MetodosPago", this.getPocDao().getMetodosPago(id_empresa));
        jsonretorno.put("Extra", this.getPocDao().getUserRol(id_usuario));
        
        return jsonretorno;
    }
    
    
    
    
    //edicion y nuevo
    @RequestMapping(method = RequestMethod.POST, value="/edit.json")
    public @ResponseBody HashMap<String, String> editJson(
            @RequestParam(value="id_remision", required=true) Integer identificador,
            @RequestParam(value="orden_compra", required=true) String orden_compra,
            @ModelAttribute("user") UserSessionData user,
            Model model
        ) {
            
            HashMap<String, String> jsonretorno = new HashMap<String, String>();
            HashMap<String, String> succes = new HashMap<String, String>();
            Integer id_usuario= user.getUserId();//variable para el id  del usuario
            Integer app_selected = 66;//Remisiones de Clientes
            String command_selected = "new";
            String actualizo = "0";
            
            
            if( identificador!=0 ){
                command_selected = "edit";
            }
            
            //serializar el arreglo
            String extra_data_array = "'sin datos'";
            
            String data_string = app_selected + "___"+ command_selected + "___"+ id_usuario + "___"+ identificador + "___"+ orden_compra.toUpperCase();
            
            succes = this.getPocDao().selectFunctionValidateAaplicativo(data_string, app_selected, extra_data_array);
            
            log.log(Level.INFO, "despues de validacion {0}", String.valueOf(succes.get("success")));
            
            if( String.valueOf(succes.get("success")).equals("true")  ){
                actualizo = this.getPocDao().selectFunctionForThisApp(data_string, extra_data_array);
            }
            
            jsonretorno.put("success",String.valueOf(succes.get("success")));
            
            log.log(Level.INFO, "Salida json {0}", String.valueOf(jsonretorno.get("success")));
        return jsonretorno;
    }
            
    
    
    
    
    
    
    //cancelar remision
    @RequestMapping(method = RequestMethod.POST, value="/getCancelaRemision.json")
    public @ResponseBody HashMap<String, String> getCancelaRemisionJson(
            @RequestParam(value="id_remision", required=true) String id_remision,
            @RequestParam(value="iu", required=true) String id_user,
            Model model
        ) {
        
        System.out.println("::::::::::::Iniciando Cancelar Remision:::::::::::::::::..");
        //decodificar id de usuario
        Integer id_usuario = Integer.parseInt(Base64Coder.decodeString(id_user));
        
        Integer app_selected = 66;
        String command_selected = "cancelar";
        String extra_data_array = "'sin datos'";
        
        String data_string = app_selected+"___"+command_selected+"___"+id_usuario+"___"+id_remision;
        
        HashMap<String, String> jsonretorno = new HashMap<String, String>();
        jsonretorno.put("success",String.valueOf( this.getPocDao().selectFunctionForThisApp(data_string,extra_data_array)) );
        
        return jsonretorno;
    }
    
    
    
    //pagar remision, agregado por paco
    @RequestMapping(method = RequestMethod.POST, value="/getPagaRemision.json")
    public @ResponseBody HashMap<String, String> getPagaRemisionJson(
            @RequestParam(value="id_remision", required=true) String id_remision,
            @RequestParam(value="iu", required=true) String id_user,
            Model model
            ) {
        
        System.out.println("::::::::::::Iniciando Pago Remision:::::::::::::::::..");
        //decodificar id de usuario
        Integer id_usuario = Integer.parseInt(Base64Coder.decodeString(id_user));
        
        Integer app_selected = 66;
        String command_selected = "pagar";
        String extra_data_array = "'sin datos'";
        
        String data_string = app_selected+"___"+command_selected+"___"+id_usuario+"___"+id_remision;
        
        HashMap<String, String> jsonretorno = new HashMap<String, String>();
        jsonretorno.put("success",String.valueOf( this.getPocDao().selectFunctionForThisApp(data_string,extra_data_array)) );
        
        return jsonretorno;
    }


    
    
    
    
    @RequestMapping(value = "/getPdfRemision/{id_remision}/{iu}/out.json", method = RequestMethod.GET ) 
    public ModelAndView getGeneraPdfFacturacionJson(
                @PathVariable("id_remision") Integer id_remision,
                @PathVariable("iu") String id_user,
                HttpServletRequest request, 
                HttpServletResponse response, 
                Model model)
            throws ServletException, IOException, URISyntaxException, DocumentException {
        
        HashMap<String, String> userDat = new HashMap<String, String>();
        HashMap<String, String> datos_remision = new HashMap<String, String>();
        ArrayList<HashMap<String, String>> conceptos_remision = new ArrayList<HashMap<String, String>>();
        
        System.out.println("::::::::::::Generando PDF de Remision:::::::::::::::::..");
        //decodificar id de usuario
        Integer id_usuario = Integer.parseInt(Base64Coder.decodeString(id_user));
        
        userDat = this.getHomeDao().getUserById(id_usuario);
        Integer id_empresa = Integer.parseInt(userDat.get("empresa_id"));
        Integer id_sucursal = Integer.parseInt(userDat.get("sucursal_id"));
        String sucursal = userDat.get("sucursal");
        
        
        String razon_social_empresa = this.getGralDao().getRazonSocialEmpresaEmisora(id_empresa);
        String rfc_empresa = this.getGralDao().getRfcEmpresaEmisora(id_empresa);
        
        //obtener el directorio temporal
        String dir_tmp = this.getGralDao().getTmpDir();
        
        String ruta_imagen = this.getGralDao().getImagesDir()+rfc_empresa+"_logo.png";
        //String ruta_imagen = this.getGralDao().getImagesDir() +"logo_"+ company_name +".png";
        
        File file_dir_tmp = new File(dir_tmp);
        System.out.println("Directorio temporal: "+file_dir_tmp.getCanonicalPath());
        
        
        datos_remision =  this.getPocDao().getRemisiones_DatosPdf(id_remision);
        datos_remision.put("emisor_razon_social", this.getGralDao().getRazonSocialEmpresaEmisora(id_empresa));
        datos_remision.put("emisor_rfc", this.getGralDao().getRfcEmpresaEmisora(id_empresa));
        datos_remision.put("emisor_calle", this.getGralDao().getCalleDomicilioFiscalEmpresaEmisora(id_empresa));
        datos_remision.put("emisor_numero", this.getGralDao().getNoInteriorDomicilioFiscalEmpresaEmisora(id_empresa));
        datos_remision.put("emisor_colonia", this.getGralDao().getColoniaDomicilioFiscalEmpresaEmisora(id_empresa));
        datos_remision.put("emisor_municipio", this.getGralDao().getMunicipioDomicilioFiscalEmpresaEmisora(id_empresa));
        datos_remision.put("emisor_estado", this.getGralDao().getEstadoDomicilioFiscalEmpresaEmisora(id_empresa));
        datos_remision.put("emisor_pais", this.getGralDao().getPaisDomicilioFiscalEmpresaEmisora(id_empresa));
        datos_remision.put("emisor_cp", this.getGralDao().getCpDomicilioFiscalEmpresaEmisora(id_empresa));
        datos_remision.put("sucursal_emisor", sucursal);
        
        
        String lugar_expedicion = this.getGralDao().getMunicipioSucursalEmisora(id_sucursal) + ", "+ this.getGralDao().getEstadoSucursalEmisora(id_sucursal);
        datos_remision.put("lugar_expedicion", lugar_expedicion.toUpperCase());
        
        conceptos_remision = this.getPocDao().getRemisiones_ConceptosPdf(id_remision,rfc_empresa);

        //genera nombre del archivo
        String file_name = "REM_"+ rfc_empresa +"_"+ datos_remision.get("folio") +".pdf";
        
        
        //ruta de archivo de salida
        String fileout = file_dir_tmp +"/"+  file_name;
        
        //genera pdf
        PdfRemision Pdf = new PdfRemision(datos_remision, conceptos_remision, fileout, ruta_imagen);
        
        
        System.out.println("Recuperando archivo: " + fileout);
        File file = new File(fileout);
        int size = (int) file.length(); // Tamaño del archivo
        BufferedInputStream bis = new BufferedInputStream(new FileInputStream(file));
        response.setBufferSize(size);
        response.setContentLength(size);
        response.setContentType("application/pdf");
        response.setHeader("Content-Disposition","attachment; filename=\"" + file.getName() +"\"");
        FileCopyUtils.copy(bis, response.getOutputStream());  	
        response.flushBuffer();
        try {
            FileHelper.delete(fileout);
        } catch (Exception ex) {
            System.out.println("No fue posible eliminar el fichero: "+file.getName());
        }
        
        return null;
    } 
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
}
