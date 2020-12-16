package com.agnux.kemikal.controllers;

import com.agnux.cfd.v2.Base64Coder;
import com.agnux.common.helpers.FileHelper;
import com.agnux.common.obj.ResourceProject;
import com.agnux.common.obj.UserSessionData;
import com.agnux.kemikal.interfacedaos.CrmInterfaceDao;
import com.agnux.kemikal.interfacedaos.GralInterfaceDao;
import com.agnux.kemikal.interfacedaos.HomeInterfaceDao;
import com.agnux.kemikal.reportes.Pdf_CRM_registroProyectos;
import com.agnux.kemikal.reportes.CRMReporteProyectosXls;
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
@RequestMapping("/crmreporteproyectos/")

public class CrmPdfReporteProyectosController {
    private static final Logger log  = Logger.getLogger(CrmPdfReporteProyectosController.class.getName());
    ResourceProject resource = new ResourceProject();
    
    @Autowired
    @Qualifier("daoCrm")
    private CrmInterfaceDao CrmDao;
    
    public CrmInterfaceDao getCrmDao() {
        return CrmDao;
    }
    
    public void setCrmDao(CrmInterfaceDao CrmDao) {
        this.CrmDao = CrmDao;
    }
    
    @Autowired
    @Qualifier("daoHome")
    private HomeInterfaceDao HomeDao;
    
    @Autowired
    @Qualifier("daoGral")
    private GralInterfaceDao gralDao;
    private Integer agente_id;
    
   // private Integer cliente_id;
    
    public HomeInterfaceDao getHomeDao() {
        return HomeDao;
    }
    
    public GralInterfaceDao getGralDao() {
        return gralDao;
    }
    
    @RequestMapping(value="/startup.agnux")
    public ModelAndView startUp(HttpServletRequest request, HttpServletResponse response,
            @ModelAttribute("user") UserSessionData user)
            throws ServletException, IOException {
        
        log.log(Level.INFO, "Ejecutando starUp de {0}", CrmPdfReporteProyectosController.class.getName());
        LinkedHashMap<String,String> infoConstruccionTabla = new LinkedHashMap<String,String>();
        
        ModelAndView x = new ModelAndView("crmreporteproyectos/startup", "title", "Reporte Proyectos");
        
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
    
    
    //obtiene los Agentes para el Buscador pricipal del Aplicativo
    @RequestMapping(method = RequestMethod.POST, value="/getAgentesParaBuscador.json")
    public @ResponseBody HashMap<String,ArrayList<HashMap<String, String>>> getAgentesParaBuscador(
            @RequestParam(value="iu", required=true) String id_user_cod,
            Model model
        ) {
        
        HashMap<String,ArrayList<HashMap<String, String>>> jsonretorno = new HashMap<String,ArrayList<HashMap<String, String>>>();
        HashMap<String, String> userDat = new HashMap<String, String>();
        ArrayList<HashMap<String, String>> agentes = new ArrayList<HashMap<String, String>>();
        ArrayList<HashMap<String, String>> arrayExtra = new ArrayList<HashMap<String, String>>();
        HashMap<String, String> extra = new HashMap<String, String>();
        
        //decodificar id de usuario
        Integer id_usuario = Integer.parseInt(Base64Coder.decodeString(id_user_cod));
        userDat = this.getHomeDao().getUserById(id_usuario);
        Integer id_empresa = Integer.parseInt(userDat.get("empresa_id"));
        Integer id_agente = Integer.parseInt(userDat.get("empleado_id"));
        
        extra = this.getCrmDao().getUserRol(id_usuario);
        extra.put("id_agente", String.valueOf(id_agente));
        arrayExtra.add(0,extra);
        
        agentes = this.getCrmDao().getAgentes(id_empresa);
        
        jsonretorno.put("Extra", arrayExtra);
        jsonretorno.put("Agentes", agentes);
        return jsonretorno;
    }
    
     //obtiene los ClientesProspectos para el Buscador pricipal del Aplicativo
   // @RequestMapping(method = RequestMethod.POST, value="/getClientesProspectosParaBuscador.json")
   // public @ResponseBody HashMap<String,ArrayList<HashMap<String, String>>> getClientesProspectosParaBuscador(
    //        @RequestParam(value="iu", required=true) String id_user_cod,
    //        Model model
    //    ) {
    //    
    //   HashMap<String,ArrayList<HashMap<String, String>>> jsonretorno = new HashMap<String,ArrayList<HashMap<String, String>>>();
    //    HashMap<String, String> userDat = new HashMap<String, String>();
    //    ArrayList<HashMap<String, String>> clientesprospectos = new ArrayList<HashMap<String, String>>();
    //    ArrayList<HashMap<String, String>> arrayExtra = new ArrayList<HashMap<String, String>>();
    //    HashMap<String, String> extra = new HashMap<String, String>();
        
        //decodificar id de usuario
    //    Integer id_usuario = Integer.parseInt(Base64Coder.decodeString(id_user_cod));
    //    userDat = this.getHomeDao().getUserById(id_usuario);
    //    Integer id_empresa = Integer.parseInt(userDat.get("empresa_id"));
    //    Integer id_agente = Integer.parseInt(userDat.get("empleado_id"));
        
    //    extra = this.getCrmDao().getUserRol(id_usuario);
    //    extra.put("id_cliente", String.valueOf(id_cliente));
    //    arrayExtra.add(0,extra);
        
     //   clientesprospectos = this.getCrmDao().getClientesProspectos(tipo, cadena, id_empresa);
        
    //   jsonretorno.put("Extra", arrayExtra);
    //    jsonretorno.put("Clientes", clientes);
    //    return jsonretorno;
    //}
    
    
    
    @RequestMapping(method = RequestMethod.POST, value="/getProyectos.json")
    public @ResponseBody HashMap<String,ArrayList<HashMap<String, String>>> getProyectosJson(
            @RequestParam(value="fecha_inicial", required=true) String fecha_inicial,
            @RequestParam(value="fecha_final", required=true) String fecha_final,
            @RequestParam(value="iu", required=true) String id_user,
            @RequestParam(value="agente", required=true) Integer agente,
            Model model
        ) {
        
        log.log(Level.INFO, "Ejecutando getProyectosJson de {0}", CrmPdfReporteProyectosController.class.getName());
        HashMap<String,ArrayList<HashMap<String, String>>> jsonretorno = new HashMap<String,ArrayList<HashMap<String, String>>>();
        
        HashMap<String, String> userDat = new HashMap<String, String>();
        ArrayList<HashMap<String, String>> datos = new ArrayList<HashMap<String, String>>();
        
        //decodificar id de usuario
        Integer id_usuario = Integer.parseInt(Base64Coder.decodeString(id_user));
        userDat = this.getHomeDao().getUserById(id_usuario);
        Integer id_empresa = Integer.parseInt(userDat.get("empresa_id"));
        
        datos = this.getCrmDao().getProyectos(fecha_inicial, fecha_final,id_empresa, agente);
        
        jsonretorno.put("Datos", datos);
        
        return jsonretorno;
    }




     //PDF reporte
     @RequestMapping(value = "/Crear_PDF_reg_Proyectos/{cadena}/{iu}/out.json", method = RequestMethod.GET )
     public ModelAndView Crear_PDF_reg_Proyectos(
        @PathVariable("cadena") String cadena,
        @PathVariable("iu") String id_user,
        HttpServletRequest request,
        HttpServletResponse response,
        Model model)
     throws ServletException, IOException, URISyntaxException, DocumentException, Exception {
         
        String[] filtros = cadena.split("___");
        
        String fecha_inicial = filtros[0];
        String fecha_final = filtros[1];
        Integer agente = Integer.parseInt(filtros[2]);
        HashMap<String, String> userDat = new HashMap<String, String>();
        System.out.println("Generando Reporte de Proyectos");
        String dir_tmp = this.getGralDao().getTmpDir();
        File file_dir_tmp = new File(dir_tmp);
        String file_name = "RepProyectos del "+fecha_inicial+"al"+fecha_final+".pdf";
        
        //ruta de archivo de salida
        String fileout = file_dir_tmp +"/"+  file_name;
        
        ArrayList<HashMap<String, String>> reg_proyectos = new ArrayList<HashMap<String, String>>();
        HashMap<String, String> datosEncabezadoPie = new HashMap<String, String>();
        HashMap<String, String> datos= new HashMap<String, String>();
        
        //decodificar id de usuario
        Integer id_usuario = Integer.parseInt(Base64Coder.decodeString(id_user));
        userDat = this.getHomeDao().getUserById(id_usuario);
        Integer id_empresa = Integer.parseInt(userDat.get("empresa_id"));
        Integer app_selected=128;
        String razon_social_empresa = this.getGralDao().getRazonSocialEmpresaEmisora(id_empresa);
        
        String titulo_reporte ="Reporte de Proyectos";
        datosEncabezadoPie.put("nombre_empresa_emisora", razon_social_empresa);
        datosEncabezadoPie.put("titulo_reporte", titulo_reporte);

        datosEncabezadoPie.put("codigo1", this.getGralDao().getCodigo1Iso(id_empresa, app_selected));
        datosEncabezadoPie.put("codigo2", this.getGralDao().getCodigo2Iso(id_empresa, app_selected));
        datos.put("fecha_inicial",fecha_inicial);
        datos.put("fecha_final",fecha_final);
        
        //Obtener datos para el reporte de proyectos
        reg_proyectos =this.getCrmDao().getProyectos(fecha_inicial,fecha_final, id_empresa, agente);
        
        //Instancia a la clase que construye el pdf del reporte de visitas
        //Pdf_CRM_registroProyectos x = new Pdf_CRM_registroProyectos(datosEncabezadoPie, fileout,reg_proyectos,datos);
        
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
        
        FileHelper.delete(fileout);
        
        return null;
    }

       //Genera EXCEL
     
    @RequestMapping(value = "/get_genera_reporte_proyectos/{cadena}/{iu}/out.json", method = RequestMethod.GET ) 
    public ModelAndView getReporteJson(
                @PathVariable("cadena") String cadena,
                @PathVariable("iu") String id_user_cod,
                HttpServletRequest request,
                HttpServletResponse response, 
                Model model)
            throws ServletException, IOException, URISyntaxException, DocumentException, Exception {
        
        
        HashMap<String, String> userDat = new HashMap<String, String>();
        
        String tituloReporte="Reporte de Proyectos";
        System.out.println("Generando reporte de "+tituloReporte);
        
        //decodificar id de usuario
        Integer id_usuario = Integer.parseInt(Base64Coder.decodeString(id_user_cod));
        //System.out.println("id_usuario: "+id_usuario);
        
        userDat = this.getHomeDao().getUserById(id_usuario);
        Integer id_empresa = Integer.parseInt(userDat.get("empresa_id"));
        
        String razon_social_empresa = this.getGralDao().getRazonSocialEmpresaEmisora(id_empresa);

        Integer anio=0;
        String arrayCad [] = cadena.split("___");;
        anio = Integer.parseInt(arrayCad [0]);   
        String periodo = "Periodo Anual "+anio;
        
        
        //obtener el directorio temporal
        //String dir_tmp = System.getProperty("java.io.tmpdir");
        String dir_tmp = this.getGralDao().getTmpDir();
        
        
        File file_dir_tmp = new File(dir_tmp);
        //System.out.println("Directorio temporal: "+file_dir_tmp.getCanonicalPath());
        
        String file_name = "ReporteProyectos.xls";
        //ruta de archivo de salida
        String fileout = file_dir_tmp +"/"+  file_name;
      
        ArrayList<HashMap<String, String>> reg_proyectos = new ArrayList<HashMap<String, String>>();
        HashMap<String, String> datosEncabezadoPie = new HashMap<String, String>();
        HashMap<String, String> datos= new HashMap<String, String>();
        
        HashMap<String,ArrayList<HashMap<String, String>>> jsonretorno = new HashMap<String,ArrayList<HashMap<String, String>>>();
      //  HashMap<String, String> userDat = new HashMap<String, String>();
        ArrayList<HashMap<String, String>> agentes = new ArrayList<HashMap<String, String>>();
        ArrayList<HashMap<String, String>> arrayExtra = new ArrayList<HashMap<String, String>>();
        HashMap<String, String> extra = new HashMap<String, String>();
        
        //decodificar id de usuario
    //    Integer id_usuario = Integer.parseInt(Base64Coder.decodeString(id_user_cod));
        userDat = this.getHomeDao().getUserById(id_usuario);
     //   Integer id_empresa = Integer.parseInt(userDat.get("empresa_id"));
        Integer id_agente = Integer.parseInt(userDat.get("empleado_id"));
        
        extra = this.getCrmDao().getUserRol(id_usuario);
        extra.put("id_agente", String.valueOf(id_agente));
        arrayExtra.add(0,extra);
        
        agentes = this.getCrmDao().getAgentes(id_empresa);
        //Obtener datos para el reporte de proyectos
    //    reg_proyectos =this.getCrmDao().getProyectos(fecha_inicial,fecha_final, id_empresa, agente);

        
        //Instancia de la clase que construye el xls
        CRMReporteProyectosXls excel = new CRMReporteProyectosXls(fileout,tituloReporte,agentes);

        
        System.out.println("Recuperando archivo: " + fileout);
        File file = new File(fileout);
        int size = (int) file.length(); // Tamaño del archivo
        BufferedInputStream bis = new BufferedInputStream(new FileInputStream(file));
        response.setBufferSize(size);
        response.setContentLength(size);
        response.setContentType("application/xls");
        response.setHeader("Content-Disposition","attachment; filename=\"" + file.getName() +"\"");
        FileCopyUtils.copy(bis, response.getOutputStream());  	
        response.flushBuffer();
        
        FileHelper.delete(fileout);
        
        return null;
    }
    
     
}
