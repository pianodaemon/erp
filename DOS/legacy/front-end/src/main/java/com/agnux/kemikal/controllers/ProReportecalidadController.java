package com.agnux.kemikal.controllers;


import com.agnux.cfd.v2.Base64Coder;
import com.agnux.common.obj.ResourceProject;
import com.agnux.common.obj.UserSessionData;
import com.agnux.kemikal.interfacedaos.GralInterfaceDao;
import com.agnux.kemikal.interfacedaos.HomeInterfaceDao;
import com.agnux.kemikal.interfacedaos.InvInterfaceDao;
import com.agnux.kemikal.interfacedaos.ProInterfaceDao;
import com.agnux.kemikal.reportes.PdfProReporteCalidad;
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
@RequestMapping("/proreportecalidad/")
public class ProReportecalidadController {
    private static final Logger log  = Logger.getLogger(ProReportecalidadController.class.getName());
    ResourceProject resource = new ResourceProject();
    
    @Autowired
    @Qualifier("daoPro")
    private ProInterfaceDao daoPro;
    
    public ProInterfaceDao getProDao() {
        return daoPro;
    }
    
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
            @ModelAttribute("user") UserSessionData user)
            throws ServletException, IOException {
        
        log.log(Level.INFO, "Ejecutando starUp de {0}", ProReportecalidadController.class.getName());
        LinkedHashMap<String,String> infoConstruccionTabla = new LinkedHashMap<String,String>();
        
        ModelAndView x = new ModelAndView("proreportecalidad/startup", "title", "Reporte de Calidad");
        
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


    //obtiene datos para el autopletar
    @RequestMapping(value="/get_proordentipos.json", method = RequestMethod.POST)
    public @ResponseBody HashMap<String,ArrayList<HashMap<String, String>>> getProOrdenTiposJson(
           @RequestParam(value="iu", required=true) String id_user_cod,
           Model modcel) {
        
        HashMap<String, String> userDat = new HashMap<String, String>();
        HashMap<String,ArrayList<HashMap<String, String>>> jsonretorno = new HashMap<String,ArrayList<HashMap<String, String>>>();
        ArrayList<HashMap<String, String>> tc = new ArrayList<HashMap<String, String>>();
        
        Integer id_usuario = Integer.parseInt(Base64Coder.decodeString(id_user_cod));
        userDat = this.getHomeDao().getUserById(id_usuario);
        
        Integer id_empresa = Integer.parseInt(userDat.get("empresa_id"));
        Integer id_sucursal = Integer.parseInt(userDat.get("sucursal_id"));
        
        jsonretorno.put("ordenTipos", this.getProDao().getProOrdenTipos(id_empresa));
        
        return jsonretorno;
    }
    
    
    //obtiene datos para el buscador
    @RequestMapping(method = RequestMethod.POST, value="/getDatosBusqueda.json")
    public @ResponseBody HashMap<String,ArrayList<HashMap<String, String>>> getDatosBusquedaJson(
            @RequestParam(value="iu", required=true) String id_user_cod,
            Model model
        ) {
        
        log.log(Level.INFO, "Ejecutando getDatosBusquedaJson de {0}", ProReportecalidadController.class.getName());
        HashMap<String, String> userDat = new HashMap<String, String>();
        HashMap<String,ArrayList<HashMap<String, String>>> jsonretorno = new HashMap<String,ArrayList<HashMap<String, String>>>();
        ArrayList<HashMap<String, String>> tc = new ArrayList<HashMap<String, String>>();
        
        Integer id_usuario = Integer.parseInt(Base64Coder.decodeString(id_user_cod));
        userDat = this.getHomeDao().getUserById(id_usuario);
        
        Integer id_empresa = Integer.parseInt(userDat.get("empresa_id"));
        Integer id_sucursal = Integer.parseInt(userDat.get("sucursal_id"));
        
        jsonretorno.put("ordenTipos", this.getProDao().getProOrdenTipos(id_empresa));
        
        return jsonretorno;
    }
    
    
    
    
    @RequestMapping(method = RequestMethod.POST, value = "/getProReporteCalidad.json")
    public @ResponseBody
    HashMap<String, ArrayList<HashMap<String, String>>> getProReporteCalidadJson(
            @RequestParam(value = "folio", required = true) String folio,
            @RequestParam(value = "codigo", required = true) String codigo,
            @RequestParam(value = "tipo", required = true) String tipo,
            @RequestParam(value = "f_inicial", required = true) String f_inicial,
            @RequestParam(value = "f_final", required = true) String f_final,
            @RequestParam(value = "iu", required = true) String id_user,
        Model model) {
        
        log.log(Level.INFO, "Ejecutando getProReporteCalidadJson de {0}", ProReportecalidadController.class.getName());
        HashMap<String, ArrayList<HashMap<String, String>>> jsonretorno = new HashMap<String, ArrayList<HashMap<String, String>>>();
        ArrayList<HashMap<String, String>> datosReporte = new ArrayList<HashMap<String, String>>();
        String data_string = new String();
        
        //HashMap<String, String> userDat = new HashMap<String, String>();
        
        //decodificar id de usuario
        Integer id_usuario = Integer.parseInt(Base64Coder.decodeString(id_user));
        
        //userDat = this.getHomeDao().getUserById(id_usuario);
        //Integer id_empresa = Integer.parseInt(userDat.get("empresa_id"));
        
        data_string = id_usuario+"___"+folio+"___"+codigo+"___"+tipo+"___"+f_inicial+"___"+f_final;
        
        datosReporte = this.getProDao().getPro_ReporteCalidad(data_string);
        
        jsonretorno.put("Datos_R_Calidad", datosReporte);
        
        return jsonretorno;
    }


    //Genera pdf Reporte de Movimientos
    @RequestMapping(value = "/getReporteCalidad/{cadena}/{iu}/out.json", method = RequestMethod.GET )
    public ModelAndView getGeneraPdfRemisionJson(
                @PathVariable("cadena") String cadena,
                @PathVariable("iu") String id_user,
                HttpServletRequest request,
                HttpServletResponse response,
                Model model)
        throws ServletException, IOException, URISyntaxException, DocumentException {
        
        HashMap<String, String> userDat = new HashMap<String, String>();
        ArrayList<HashMap<String, String>> Datos_Reporte_Calidad = new ArrayList<HashMap<String, String>>();
        HashMap<String, String> datosEncabezadoPie= new HashMap<String, String>();
        
        System.out.println("Generando reporte de Calidad");
        
        String arrayCad [] = cadena.split("___");
        
        System.out.println("cadena: "+cadena);
        
        if(arrayCad[0].equals("0")){ arrayCad[0]=""; }
        if(arrayCad[1].equals("0")){ arrayCad[1]=""; }
        
        if(arrayCad[3].equals("0")){ arrayCad[3]=""; }
        if(arrayCad[4].equals("0")){ arrayCad[4]=""; }
        
        String fecha_inicial=arrayCad[3];
        String fecha_final=arrayCad[4];
        
        //decodificar id de usuario
        Integer id_usuario = Integer.parseInt(Base64Coder.decodeString(id_user));
        userDat = this.getHomeDao().getUserById(id_usuario);
        Integer id_empresa = Integer.parseInt(userDat.get("empresa_id"));
        
        //Reporte de Calidad
        Integer app_selected=144;
        String[] fi = fecha_inicial.split("-");
        String[] ff = fecha_final.split("-");
        String periodo_reporte = "Periodo  del  "+fi[2]+"/"+fi[1]+"/"+fi[0]+"  al  "+ff[2]+"/"+ff[1]+"/"+ff[0];
        
        String rfc=this.getGralDao().getRfcEmpresaEmisora(id_empresa);
        String razon_social_empresa = this.getGralDao().getRazonSocialEmpresaEmisora(id_empresa);
        
        //obtener el directorio temporal
        String dir_tmp = this.getGralDao().getTmpDir();
        
        File file_dir_tmp = new File(dir_tmp);
        System.out.println("Directorio temporal: "+file_dir_tmp.getCanonicalPath());
        
        String file_name = "REP_CALIDAD_"+rfc+".pdf";
        
        //ruta de archivo de salida
        String fileout = file_dir_tmp +"/"+  file_name;
        
        String data_string = new String();
        data_string = id_usuario+"___"+arrayCad[0]+"___"+arrayCad[1]+"___"+arrayCad[2]+"___"+arrayCad[3]+"___"+arrayCad[4];
        
        //Obtiene los datos Para el reporte de Calidad (Modulo de Produccion)
        Datos_Reporte_Calidad = this.getProDao().getPro_ReporteCalidad(data_string);
        
        //Agregar informacion para el encabezado y pie de pagina
        datosEncabezadoPie.put("empresa", razon_social_empresa);
        datosEncabezadoPie.put("titulo_reporte", this.getGralDao().getTituloReporte(id_empresa, app_selected));
        datosEncabezadoPie.put("periodo", periodo_reporte);
        datosEncabezadoPie.put("codigo1", this.getGralDao().getCodigo1Iso(id_empresa, app_selected));
        datosEncabezadoPie.put("codigo2", this.getGralDao().getCodigo2Iso(id_empresa, app_selected));
        
        //instancia a la clase que construye el pdf del reporte de calidad
        PdfProReporteCalidad x = new PdfProReporteCalidad(datosEncabezadoPie, fileout,Datos_Reporte_Calidad);
        
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
        
        return null;
    }










}
