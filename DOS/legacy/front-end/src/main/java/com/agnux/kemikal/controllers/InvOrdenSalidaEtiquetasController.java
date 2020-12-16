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
import com.agnux.kemikal.interfacedaos.InvInterfaceDao;
import com.agnux.kemikal.reportes.PdfEtiquetas;
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
@RequestMapping("/invordensalidaetiqueta/")
public class InvOrdenSalidaEtiquetasController {
    private static final Logger log  = Logger.getLogger(InvOrdenSalidaEtiquetasController.class.getName());
    ResourceProject resource = new ResourceProject();
    
    @Autowired
    @Qualifier("daoInv")
    private InvInterfaceDao invDao;
    
    @Autowired
    @Qualifier("daoHome")
    private HomeInterfaceDao HomeDao;
    
    @Autowired
    @Qualifier("daoGral")
    private GralInterfaceDao gralDao;
    
    public InvInterfaceDao getInvDao() {
        return invDao;
    }
    
    public HomeInterfaceDao getHomeDao() {
        return HomeDao;
    }
    
    public GralInterfaceDao getGralDao() {
        return gralDao;
    }
    
    
    @RequestMapping(value="/startup.agnux")
    public ModelAndView startUp(HttpServletRequest request, HttpServletResponse response, 
            @ModelAttribute("user") UserSessionData user
        )throws ServletException, IOException {
        
        log.log(Level.INFO, "Ejecutando starUp de {0}", InvOrdenSalidaEtiquetasController.class.getName());
        LinkedHashMap<String,String> infoConstruccionTabla = new LinkedHashMap<String,String>();
        
        infoConstruccionTabla.put("id", "Acciones:70");
        infoConstruccionTabla.put("serie_folio", "Factura:80");
        infoConstruccionTabla.put("cliente", "Cliente:300");
        infoConstruccionTabla.put("total", "Monto:90");
        infoConstruccionTabla.put("moneda", "Moneda:60");
        infoConstruccionTabla.put("fecha_facturacion","Fecha&nbsp;Exp.:80");
        //infoConstruccionTabla.put("fecha_venc","Fecha&nbsp;Ven.:80");
        infoConstruccionTabla.put("folio_pedido","Pedido:80");
        infoConstruccionTabla.put("oc","O.C.:80");
        //infoConstruccionTabla.put("estado", "Estado:80");
        //infoConstruccionTabla.put("fecha_pago","Fecha&nbsp;Pago:80");
        
        ModelAndView x = new ModelAndView("invordensalidaetiqueta/startup", "title", "Impresi&oacute;n de Etiquetas-Salidas");
        
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
    
    
    //Obtiene los Agentes para el Buscador pricipal del Aplicativo
    @RequestMapping(method = RequestMethod.POST, value="/getAgentesParaBuscador.json")
    public @ResponseBody HashMap<String,ArrayList<HashMap<String, Object>>> getAgentesParaBuscador(
            @RequestParam(value="iu", required=true) String id_user_cod,
            Model model
        ) {
        
        HashMap<String,ArrayList<HashMap<String, Object>>> jsonretorno = new HashMap<String,ArrayList<HashMap<String, Object>>>();
        HashMap<String, String> userDat = new HashMap<String, String>();
        ArrayList<HashMap<String, Object>> agentes = new ArrayList<HashMap<String, Object>>();
        
        //decodificar id de usuario
        Integer id_usuario = Integer.parseInt(Base64Coder.decodeString(id_user_cod));
        userDat = this.getHomeDao().getUserById(id_usuario);
        Integer id_empresa = Integer.parseInt(userDat.get("empresa_id"));
        Integer id_sucursal = Integer.parseInt(userDat.get("sucursal_id"));
        
        agentes = this.getInvDao().AgentesDeVentas(id_empresa, id_sucursal);
        
        jsonretorno.put("Agentes", agentes);
        return jsonretorno;
    }
    
    
    
    
    @RequestMapping(value="/getAllFacturas.json", method = RequestMethod.POST)
    public @ResponseBody HashMap<String,ArrayList<HashMap<String, Object>>> getAllFacturasacturasJson(
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
        
        //Aplicativo Ordenes de Salida con Impresion de Etiqueta
        Integer app_selected = 200;
        
        //Decodificar id de usuario
        Integer id_usuario = Integer.parseInt(Base64Coder.decodeString(id_user_cod));
        
        //Variables para el buscador
        String factura = "%"+StringHelper.isNullString(String.valueOf(has_busqueda.get("factura")))+"%";
        String cliente = "%"+StringHelper.isNullString(String.valueOf(has_busqueda.get("cliente")))+"%";
        String fecha_inicial = ""+StringHelper.isNullString(String.valueOf(has_busqueda.get("fecha_inicial")))+"";
        String fecha_final = ""+StringHelper.isNullString(String.valueOf(has_busqueda.get("fecha_final")))+"";
        String codigo = "%"+StringHelper.isNullString(String.valueOf(has_busqueda.get("codigo")))+"%";
        String producto = "%"+StringHelper.isNullString(String.valueOf(has_busqueda.get("producto")))+"%";
        String agente = ""+StringHelper.isNullString(String.valueOf(has_busqueda.get("agente")))+"";
        String folio_pedido = "%"+StringHelper.isNullString(String.valueOf(has_busqueda.get("folio_pedido")))+"%";
        
        String data_string = app_selected+"___"+id_usuario+"___"+factura+"___"+cliente+"___"+fecha_inicial+"___"+fecha_final+"___"+codigo+"___"+producto+"___"+agente+"___"+folio_pedido;
        
        //Obtiene total de registros en base de datos, con los parametros de busqueda
        int total_items = this.getInvDao().countAll(data_string);
        
        //Calcula el total de paginas
        int total_pags = resource.calculaTotalPag(total_items,items_por_pag);
        
        //Variables que necesita el datagrid, para no tener que hacer uno por cada aplicativo
        DataPost dataforpos = new DataPost(orderby, desc, items_por_pag, pag_start, display_pag, input_json, cadena_busqueda,total_items,total_pags, id_user_cod);
        
        int offset = resource.__get_inicio_offset(items_por_pag, pag_start);
        
        //Obtiene los registros para el grid, de acuerdo a los parametros de busqueda
        jsonretorno.put("Data", this.getInvDao().getInvOrdenSalidaEtiqueta_PaginaGrid(data_string, offset, items_por_pag, orderby, desc));
        
        //Obtiene el hash para los datos que necesita el datagrid
        jsonretorno.put("DataForGrid", dataforpos.formaHashForPos(dataforpos));
        
        return jsonretorno;
    }
    
    
    
    @RequestMapping(method = RequestMethod.POST, value="/getFactura.json")
    public @ResponseBody HashMap<String, Object> getFacturaJson(
            @RequestParam(value="identificador", required=true) Integer identificador,
            @RequestParam(value="iu", required=true) String id_user,
            Model model
        ) {
        
        log.log(Level.INFO, "Ejecutando getFacturaJson de {0}", FacConsultasController.class.getName());
        HashMap<String, Object> jsonretorno = new HashMap<String, Object>();
        ArrayList<HashMap<String, Object>> datosFactura = new ArrayList<HashMap<String, Object>>();
        ArrayList<HashMap<String, Object>> datosGrid = new ArrayList<HashMap<String, Object>>();
        //HashMap<String, String> userDat = new HashMap<String, String>();
        
        //Decodificar id de usuario
        //Integer id_usuario = Integer.parseInt(Base64Coder.decodeString(id_user));
        //userDat = this.getHomeDao().getUserById(id_usuario);
        
        //Integer id_empresa = Integer.parseInt(userDat.get("empresa_id"));
        //Integer id_sucursal = Integer.parseInt(userDat.get("sucursal_id"));
        
        System.out.println("id: "+identificador);
        
        if(identificador!=0){
            datosFactura = this.getInvDao().getInvOrdenSalidaEtiqueta_Datos(identificador);
            datosGrid = this.getInvDao().getInvOrdenSalidaEtiqueta_DatosGrid(identificador, false);
        }
        
        jsonretorno.put("datosFactura", datosFactura);
        jsonretorno.put("datosGrid", datosGrid);
        jsonretorno.put("Monedas", this.getInvDao().getMonedas());
        
        return jsonretorno;
    }
    
    
    
    //Edicion y nuevo
    @RequestMapping(method = RequestMethod.POST, value="/edit.json")
    public @ResponseBody HashMap<String, String> editJson(
            @RequestParam(value="identificador", required=true) Integer identificador,
            @RequestParam(value="observaciones", required=true) String observaciones,
            @RequestParam(value="iddet", required=true)         String[] iddet,
            @RequestParam(value="idprod", required=true)        String[] idprod,
            @RequestParam(value="idpres", required=true)        String[] idpres,
            @RequestParam(value="oc", required=true)            String[] oc,
            @RequestParam(value="lote", required=true)          String[] lote,
            @RequestParam(value="fcaducidad", required=true)    String[] fcaducidad,
            @RequestParam(value="cantidad", required=true)      String[] cantidad,
            @RequestParam(value="codigo2", required=true)       String[] codigo2,
            @RequestParam(value="selec_micheck", required=true) String[] selec_micheck,
            @ModelAttribute("user") UserSessionData user,
            Model model
        ) {
        
        HashMap<String, String> jsonretorno = new HashMap<String, String>();
        HashMap<String, String> succes = new HashMap<String, String>();
        String extra_data_array = null;
        String arreglo[];
        arreglo = new String[iddet.length];
        String actualizar = "0";
        String actualizo="0";
        
        //Aplicativo Ordenes de Salida con Impresion de Etiqueta
        Integer app_selected = 200;
        
        String command_selected = "new";
        Integer id_usuario= user.getUserId();//variable para el id  del usuario
        
        for(int i=0; i<iddet.length; i++) {
            arreglo[i]= "'"+ iddet[i] +"___"+ idprod[i] +"___"+ idpres[i] +"___"+ oc[i].trim().toUpperCase() +"___"+ lote[i].trim() +"___"+ fcaducidad[i].trim() +"___"+ cantidad[i] +"___"+ codigo2[i].trim().toUpperCase() +"___"+ selec_micheck[i] +"'";
            //System.out.println(arreglo[i]);
        }
        
        //serializar el arreglo
        extra_data_array = StringUtils.join(arreglo, ",");
        
        if( identificador!=0 ){
            command_selected = "edit";
        }
        
        String data_string = app_selected+"___"+command_selected+"___"+id_usuario+"___"+identificador +"___"+ observaciones.toUpperCase();
        //System.out.println("data_string: "+data_string);
        
        //Aqui entra cuando la accion es Edit o Confirmar
        succes = this.getInvDao().selectFunctionValidateAaplicativo(data_string,app_selected,extra_data_array);
        actualizar = String.valueOf(succes.get("success"));
        
        log.log(Level.INFO, "despues de validacion {0}", actualizar);
        
        if(actualizar.equals("true")){
            actualizo = this.getInvDao().selectFunctionForApp_MovimientosInventario(data_string, extra_data_array);
        }
        
        jsonretorno.put("success",String.valueOf(actualizar));
        
        log.log(Level.INFO, "Salida json {0}", actualizar);
        
        return jsonretorno;
    }
    
    
    //Genera pdf de etiquetas
    @RequestMapping(value = "/getEtiquetas/{identificador}/{iu}/out.json", method = RequestMethod.GET ) 
    public ModelAndView getEtiquetasJson(
                @PathVariable("identificador") Integer identificador,
                @PathVariable("iu") String id_user_cod,
                HttpServletRequest request, 
                HttpServletResponse response, 
                Model model
    )throws ServletException, IOException, URISyntaxException, DocumentException, Exception {
        
        HashMap<String, String> userDat = new HashMap<String, String>();
        ArrayList<HashMap<String, Object>> datosFactura = new ArrayList<HashMap<String, Object>>();
        ArrayList<HashMap<String, Object>> datos = new ArrayList<HashMap<String, Object>>();
        HashMap<String, String> datosEncabezadoPie= new HashMap<String, String>();
        String observaciones = "";
        System.out.println("Generando PDF de Etiquetas");
        
        //decodificar id de usuario
        Integer id_usuario = Integer.parseInt(Base64Coder.decodeString(id_user_cod));
        
        userDat = this.getHomeDao().getUserById(id_usuario);
        Integer id_empresa = Integer.parseInt(userDat.get("empresa_id"));
        
        //String razon_social_empresa = this.getGralDao().getRazonSocialEmpresaEmisora(id_empresa);
        //String rfc_empresa=this.getGralDao().getRfcEmpresaEmisora(id_empresa);
        //Integer id_sucursal = Integer.parseInt(userDat.get("sucursal_id"));
        //datosEncabezadoPie.put("nombre_empresa_emisora", razon_social_empresa);
        
        //Obtener el directorio temporal
        String dir_tmp = this.getGralDao().getTmpDir();
        
        File file_dir_tmp = new File(dir_tmp);
        
        datosFactura = this.getInvDao().getInvOrdenSalidaEtiqueta_Datos(identificador);
        
        if(datosFactura.size()>0){
            observaciones=datosFactura.get(0).get("observaciones").toString();
        }
        
        //Obtener los datos para las etiquetas. El parametro true es para que obtenga solo los seleccionados para imprimir
        datos = this.getInvDao().getInvOrdenSalidaEtiqueta_DatosGrid(identificador, true);
        
        //genera nombre del archivo
        String file_name = "Etiquetas_"+ TimeHelper.getFechaActualYMDHMS() +".pdf";
        
        //ruta de archivo de salida
        String fileout = file_dir_tmp +"/"+  file_name;
        
        PdfEtiquetas pdf = new PdfEtiquetas(datos,observaciones,fileout,dir_tmp);
        
        System.out.println("Recuperando archivo: " + fileout);
        File file = new File(fileout);
        int size = (int) file.length(); //Tamaño del archivo
        BufferedInputStream bis = new BufferedInputStream(new FileInputStream(file));
        response.setBufferSize(size);
        response.setContentLength(size);
        response.setContentType("application/pdf");
        response.setHeader("Content-Disposition","attachment; filename=\"" + file.getName() +"\"");
        FileCopyUtils.copy(bis, response.getOutputStream());          
        response.flushBuffer();
        
        if(file.exists()){
            FileHelper.delete(fileout);
        }
        
        return null;
        
    }

}
