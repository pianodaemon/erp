package com.agnux.kemikal.controllers;

import com.agnux.cfd.v2.Base64Coder;
import com.agnux.common.helpers.FileHelper;
import com.agnux.common.obj.ResourceProject;
import com.agnux.common.obj.UserSessionData;
import com.agnux.kemikal.interfacedaos.CxpInterfaceDao;
import com.agnux.kemikal.interfacedaos.GralInterfaceDao;
import com.agnux.kemikal.interfacedaos.HomeInterfaceDao;
import com.agnux.kemikal.reportes.PdfCxpAntiguedadSaldos;
import com.itextpdf.text.DocumentException;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
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
@RequestMapping("/cxprepantiguedadsaldos/")
public class CxpRepAntiguedadSaldosController {
    private static final Logger log  = Logger.getLogger(CxpRepAntiguedadSaldosController.class.getName());
    ResourceProject resource = new ResourceProject();
    
    @Autowired
    @Qualifier("daoCxp")
    private CxpInterfaceDao cxpDao;
    
    @Autowired
    @Qualifier("daoHome")
    private HomeInterfaceDao HomeDao;
    
    @Autowired
    @Qualifier("daoGral")
    private GralInterfaceDao gralDao;
    
    public CxpInterfaceDao getCxpDao() {
        return cxpDao;
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
        
        log.log(Level.INFO, "Ejecutando starUp de {0}", CxpRepAntiguedadSaldosController.class.getName());
        LinkedHashMap<String,String> infoConstruccionTabla = new LinkedHashMap<String,String>();
        
        ModelAndView x = new ModelAndView("cxprepantiguedadsaldos/startup", "title", "Reporte de Antig&uuml;edad de Saldos de Cuentas por Pagar");
        
        x = x.addObject("layoutheader", resource.getLayoutheader());
        x = x.addObject("layoutmenu", resource.getLayoutmenu());
        x = x.addObject("layoutfooter", resource.getLayoutfooter());
        x = x.addObject("grid", resource.generaGrid(infoConstruccionTabla));
        x = x.addObject("url", resource.getUrl(request));
        x = x.addObject("username", user.getUserName());
        x = x.addObject("empresa", user.getRazonSocialEmpresa());
        x = x.addObject("empresa", user.getRazonSocialEmpresa());
        
        String userId = String.valueOf(user.getUserId());
        
        String codificado = Base64Coder.encodeString(userId);
        
        //id de usuario codificado
        x = x.addObject("iu", codificado);
        
        return x;
    }
    
    
    
    //obtiene los proveedores para el buscador
    @RequestMapping(method = RequestMethod.POST, value="/getBuscaProveedores.json")
    public @ResponseBody HashMap<String,ArrayList<HashMap<String, String>>> getBuscaProveedoresJson(
            @RequestParam(value="rfc", required=true) String rfc,
            @RequestParam(value="email", required=true) String email,
            @RequestParam(value="nombre", required=true) String nombre,
            @RequestParam(value="iu", required=true) String id_user,
            Model model
            ) {
        
        log.log(Level.INFO, "Ejecutando getBuscaProveedoresJson de {0}", CxpRepAntiguedadSaldosController.class.getName());
        HashMap<String,ArrayList<HashMap<String, String>>> jsonretorno = new HashMap<String,ArrayList<HashMap<String, String>>>();
        ArrayList<HashMap<String, String>> proveedores = new ArrayList<HashMap<String, String>>();
        HashMap<String, String> userDat = new HashMap<String, String>();
        //decodificar id de usuario
        Integer id_usuario = Integer.parseInt(Base64Coder.decodeString(id_user));
        userDat = this.getHomeDao().getUserById(id_usuario);
        
        Integer id_empresa = Integer.parseInt(userDat.get("empresa_id"));
        
        proveedores = this.getCxpDao().getBuscadorProveedores(rfc, email, nombre,id_empresa);
        
        jsonretorno.put("Proveedores", proveedores);
        
        return jsonretorno;
    }
    
    
    
    
    //obtiene los datos para la vista del Reporte de Antiguedad de saldos de Cuentas por Pagar
    @RequestMapping(method = RequestMethod.POST, value="/getReporteAntiguedadSaldosCxp.json")
    public @ResponseBody HashMap<String,ArrayList<HashMap<String, String>>> getReporteAntiguedadSaldosCxpJson(
            @RequestParam("proveedor") String proveedor,
            @RequestParam("fecha_corte") String fecha_corte,
            @RequestParam(value="iu", required=true) String id_user,
            Model model
            ) {
        
        log.log(Level.INFO, "Ejecutando getReporteAntiguedadSaldosCxpJson de {0}", CxpRepAntiguedadSaldosController.class.getName());
        HashMap<String,ArrayList<HashMap<String, String>>> jsonretorno = new HashMap<String,ArrayList<HashMap<String, String>>>();
        
        HashMap<String, String> userDat = new HashMap<String, String>();
        ArrayList<HashMap<String, String>> lista_facturas = new ArrayList<HashMap<String, String>>();
        
        //decodificar id de usuario
        Integer id_usuario = Integer.parseInt(Base64Coder.decodeString(id_user));
        userDat = this.getHomeDao().getUserById(id_usuario);
        
        Integer id_empresa = Integer.parseInt(userDat.get("empresa_id"));
        
        //obtiene los datos del reporte
        lista_facturas = this.getCxpDao().getDatos_ReporteAntiguedadSaldosCxP(proveedor, id_empresa);
        
        jsonretorno.put("Facturas", lista_facturas);
        
        return jsonretorno;
    }
    
    
    
    //Genera pdf reporte de Antiguedad de Saldos
    @RequestMapping(value = "/getPdfReporteAntiguedadSaldosCxp/{cadena}/{iu}/out.json", method = RequestMethod.GET ) 
    public ModelAndView get_genera_pdf_edoctaJson(
                @PathVariable("cadena") String cadena,
                @PathVariable("iu") String id_user_cod,
                HttpServletRequest request, 
                HttpServletResponse response, 
                Model model)
            throws ServletException, IOException, URISyntaxException, DocumentException, Exception {
        
        HashMap<String, String> userDat = new HashMap<String, String>();
        List<HashMap<String, String>> lista_facturas;
        
        System.out.println("Generando Pdf de Reporte de Antiguedad de Saldos de Cuentas por Pagar");
        
        //decodificar id de usuario
        Integer id_usuario = Integer.parseInt(Base64Coder.decodeString(id_user_cod));
        //System.out.println("id_usuario: "+id_usuario);
        
        userDat = this.getHomeDao().getUserById(id_usuario);
        Integer id_empresa = Integer.parseInt(userDat.get("empresa_id"));
        
        
        String proveedor=cadena.split("___")[0];
        String fecha_corte=cadena.split("___")[1];
        
        String razon_social_empresa = this.getGralDao().getRazonSocialEmpresaEmisora(id_empresa);
        
        //obtener el directorio temporal
        String dir_tmp = this.getGralDao().getTmpDir();
        
        String[] array_company = razon_social_empresa.split(" ");
        String company_name= array_company[0].toLowerCase();
        String ruta_imagen = this.getGralDao().getImagesDir() +"logo_"+ company_name +".png";
        
        System.out.println("ruta_imagen: "+ruta_imagen);
        
        File file_dir_tmp = new File(dir_tmp);
        System.out.println("Directorio temporal: "+file_dir_tmp.getCanonicalPath());
        
        String file_name = "antiguedad_saldos_cxp.pdf";
        //ruta de archivo de salida
        String fileout = file_dir_tmp +"/"+  file_name;
        
        //obtiene los datos del reporte
        lista_facturas = this.getCxpDao().getDatos_ReporteAntiguedadSaldosCxP(proveedor, id_empresa);
        
        //instancia a la clase que construye el pdf de antiguedad de saldos de cuentas por pagar
        PdfCxpAntiguedadSaldos x = new PdfCxpAntiguedadSaldos(fileout, ruta_imagen, razon_social_empresa, fecha_corte,  lista_facturas);
        
        
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
}
