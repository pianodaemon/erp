/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.agnux.kemikal.controllers;

import com.agnux.cfd.v2.Base64Coder;
import com.agnux.common.helpers.FileHelper;
import com.agnux.common.obj.ResourceProject;
import com.agnux.common.obj.UserSessionData;
import com.agnux.kemikal.interfacedaos.GralInterfaceDao;
import com.agnux.kemikal.interfacedaos.HomeInterfaceDao;
import com.agnux.kemikal.interfacedaos.PocInterfaceDao;
import com.agnux.kemikal.reportes.PdfReporteArticulosReservados;
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

/**
 * @author valentin.vale8490@gmail.com
 * 02/abril/2012
 * Este controller es para para las vistas de los pdf que muestran el pronostico de cobranza por semana 
 */

@Controller
@SessionAttributes({"user"})
@RequestMapping("/reparticulosreservados/")
public class RepArticulosReservadosController {
    private static final Logger log  = Logger.getLogger(RepArticulosReservadosController.class.getName());
    ResourceProject resource = new ResourceProject();
    
    @Autowired
    @Qualifier("daoHome")
    private HomeInterfaceDao HomeDao;
    
    @Autowired
    @Qualifier("daoGral")
    private GralInterfaceDao gralDao;

    @Autowired
    @Qualifier("daoPoc")
    private PocInterfaceDao PocDao;

     public PocInterfaceDao getPocDao() {
          return PocDao;
     }

     public void setPocDao(PocInterfaceDao PocDao) {
          this.PocDao = PocDao;
     }
    public ResourceProject getResource() {
        return resource;
    }
    
    public void setResource(ResourceProject resource) {
        this.resource = resource;
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
        log.log(Level.INFO, "Ejecutando starUp de {0}", RepArticulosReservadosController.class.getName());
        LinkedHashMap<String,String> infoConstruccionTabla = new LinkedHashMap<String,String>();
        
        ModelAndView x = new ModelAndView("reparticulosreservados/startup", "title", "Reporte de Articulos Reservados");
        
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
    
    
    //obtiene datos 
    @RequestMapping(value="/getallArticulosReservados.json", method = RequestMethod.POST)
    public @ResponseBody ArrayList<HashMap<String, String>> getallArticulosReservadosJson(
            @RequestParam(value="folio", required=true) String folio,
            @RequestParam(value="codigo", required=true) String codigo,
            @RequestParam(value="descripcion", required=true) String descripcion,
            @RequestParam(value="iu", required=true) String id_user_cod,
            Model model
            ) {
        log.log(Level.INFO, "Ejecutando getallArticulosReservados de {0}", RepArticulosReservadosController.class.getName());
        HashMap<String, String> userDat = new HashMap<String, String>();
        
        //decodificar id de usuario
        Integer id_usuario = Integer.parseInt(Base64Coder.decodeString(id_user_cod));
        
        userDat = this.getHomeDao().getUserById(id_usuario);
        
        Integer id_empresa = Integer.parseInt(userDat.get("empresa_id"));
        
        ArrayList<HashMap<String, String>> z = this.getPocDao().getReporteArticulosReservados(id_empresa,id_usuario,folio,codigo,descripcion);
        return z;
        
    }
    
    
     @RequestMapping(value = "/getallArticulosReservados/{folio}/{cod}/{desc}/{iu}/out.json", method = RequestMethod.GET ) 
     public ModelAndView PdfVentasNetasxCliente(
             @PathVariable("folio") String folio,
             @PathVariable("cod") String codigo,
             @PathVariable("desc") String descripcion,
             @PathVariable("iu") String id_user,
             HttpServletRequest request,
             HttpServletResponse response, 
             Model model)
     throws ServletException, IOException, URISyntaxException, DocumentException, Exception {
        
        HashMap<String, String> userDat = new HashMap<String, String>();
        
        System.out.println("Generando reporte de Articulos Reservados");
        
        String dir_tmp = this.getGralDao().getTmpDir();
        
        File file_dir_tmp = new File(dir_tmp);
        System.out.println("Directorio temporal: "+file_dir_tmp.getCanonicalPath());
        
        
        String file_name = "rep_art_reservados.pdf";
        
        //ruta de archivo de salida
        String fileout = file_dir_tmp +"/"+  file_name;
        
        //String fileout ="C:\\Users\\micompu\\Desktop\\"+file_name;
        
        ArrayList<HashMap<String, String>> lista_articulos_reservados = new ArrayList<HashMap<String, String>>();
        HashMap<String, String> datosEncabezadoPie= new HashMap<String, String>();
        HashMap<String, String> datos= new HashMap<String, String>();
        
        //decodificar id de usuario
        Integer id_usuario = Integer.parseInt(Base64Coder.decodeString(id_user));
        Integer app_selected = 74; //Reporte de Articulos Reservados
        
        userDat = this.getHomeDao().getUserById(id_usuario);
        Integer id_empresa = Integer.parseInt(userDat.get("empresa_id"));
        String rfc_empresa=this.getGralDao().getRfcEmpresaEmisora(id_empresa);
        String razon_social_empresa = this.getGralDao().getRazonSocialEmpresaEmisora(id_empresa);
        
        String titulo_reporte ="Reporte Articulos Reservados";
        datosEncabezadoPie.put("nombre_empresa_emisora", razon_social_empresa);
        datosEncabezadoPie.put("titulo_reporte", titulo_reporte);
        datosEncabezadoPie.put("codigo1", "");
        datosEncabezadoPie.put("codigo2","");
        
        if(folio.equals("0")){
            folio="";
        }
        
        if(codigo.equals("0")){
            codigo="";
        }
        
        if(descripcion.equals("0")){
            descripcion="";
        }
        
        lista_articulos_reservados = this.getPocDao().getReporteArticulosReservados(id_empresa,id_usuario,folio,codigo,descripcion);  //llamando al pocDao
         
        //instancia a la clase que construye el pdf de la del reporte de Articulos Reservados
        PdfReporteArticulosReservados x = new PdfReporteArticulosReservados(datosEncabezadoPie, fileout,lista_articulos_reservados,datos);
        
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
        
        if(file.exists()){
            FileHelper.delete(fileout);
        }
        
        return null;
        
    }
}
