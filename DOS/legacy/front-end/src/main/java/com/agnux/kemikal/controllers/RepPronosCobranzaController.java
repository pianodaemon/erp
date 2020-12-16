/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.agnux.kemikal.controllers;
/*
 *
 * @author valentin santos s.
 */
import com.agnux.cfd.v2.Base64Coder;
import com.agnux.common.helpers.FileHelper;
import com.agnux.common.obj.ResourceProject;
import com.agnux.common.obj.UserSessionData;
import com.agnux.kemikal.interfacedaos.CxcInterfaceDao;
import com.agnux.kemikal.interfacedaos.GralInterfaceDao;
import com.agnux.kemikal.interfacedaos.HomeInterfaceDao;
import com.agnux.kemikal.reportes.PdfReportePronosticoCobranza;
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

/**
 * @author valentin.vale8490@gmail.com
 * 02/abril/2012
 * Este controller es para para las vistas de los pdf que muestran el pronostico de cobranza por semana 
 */

@Controller
@SessionAttributes({"user"})
@RequestMapping("/reppronoscobranza/")
public class RepPronosCobranzaController {
    private static final Logger log  = Logger.getLogger(RepPronosCobranzaController.class.getName());
    ResourceProject resource = new ResourceProject();
    
    
    @Autowired
    @Qualifier("daoHome")
    private HomeInterfaceDao HomeDao;
    
    @Autowired
    @Qualifier("daoGral")
    private GralInterfaceDao gralDao;
    
    @Autowired
    @Qualifier("daoCxc")
    private CxcInterfaceDao cxcDao;

    public CxcInterfaceDao getCxcDao() {
        return cxcDao;
    }

    public void setCxcDao(CxcInterfaceDao cxcDao) {
        this.cxcDao = cxcDao;
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
        
       
        log.log(Level.INFO, "Ejecutando starUp de {0}", RepPronosCobranzaController.class.getName());
        LinkedHashMap<String,String> infoConstruccionTabla = new LinkedHashMap<String,String>();
        
        
        ModelAndView x = new ModelAndView("reppronoscobranza/startup", "title", "Reporte Pronostico de Cobranza");
        
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
    @RequestMapping(value="/getPronosticoDeCobranza.json", method = RequestMethod.POST)
    public @ResponseBody ArrayList<HashMap<String, String>> getPronosticoDeCobranza(
            @RequestParam(value="opcion_seleccionada", required=true) String opcion_seleccionada,
            @RequestParam(value="numero_semanas", required=true) String numero_semanas,
            @RequestParam(value="iu", required=true) String id_user_cod,
            Model model
            ) {
        
        
        
        log.log(Level.INFO, "Ejecutando getPronosticoDeCobranza de {0}", InvRepExisController.class.getName());
        HashMap<String,ArrayList<HashMap<String, String>>> jsonretorno = new HashMap<String,ArrayList<HashMap<String, String>>>();
        //ArrayList<HashMap<String, String>> Almacenes = new ArrayList<HashMap<String, String>>();
        HashMap<String, String> userDat = new HashMap<String, String>();
        
        //decodificar id de usuario
        Integer id_usuario = Integer.parseInt(Base64Coder.decodeString(id_user_cod));
        System.out.println("id_usuario: "+id_usuario);
        
        userDat = this.getHomeDao().getUserById(id_usuario);
        
        Integer id_empresa = Integer.parseInt(userDat.get("empresa_id"));
        
        ArrayList<HashMap<String, String>> z = this.getCxcDao().getPronosticoDeCobranza(numero_semanas, opcion_seleccionada, id_empresa);
        return z;
        
    }
    
    
     //reporte Pronostico de Cobranza
     //http://localhost:8080/erp-quimicos/controllers/reppronoscobranza/getPronosticoDeCobranza/0/4/NA==/out.json
     @RequestMapping(value = "/getPronosticoDeCobranza/{opcion_seleccionada}/{numero_semanas}/{iu}/out.json", method = RequestMethod.GET ) 
     public ModelAndView PdfPronosticodeCobranza(
                @PathVariable("opcion_seleccionada") String opcion_seleccionada,
                @PathVariable("numero_semanas") String numero_semanas,
                @PathVariable("iu") String id_user_cod,
                HttpServletRequest request,
                HttpServletResponse response, 
                Model model)
            throws ServletException, IOException, URISyntaxException, DocumentException {
        
        HashMap<String, String> userDat = new HashMap<String, String>();
        
        System.out.println("Generando reporte de Pronostico de Cobranza");
        
        //decodificar id de usuario
        Integer id_usuario = Integer.parseInt(Base64Coder.decodeString(id_user_cod));
        System.out.println("id_usuario: "+id_usuario);
        
        userDat = this.getHomeDao().getUserById(id_usuario);
        Integer id_empresa = Integer.parseInt(userDat.get("empresa_id"));
        
        String razon_social_empresa = this.getGralDao().getRazonSocialEmpresaEmisora(id_empresa);
        
        //obtener el directorio temporal
        String dir_tmp = this.getGralDao().getTmpDir();
        
        
        String[] array_company = razon_social_empresa.split(" ");
        String company_name= array_company[0].toLowerCase();
        //String ruta_imagen = this.getPgdao().getImagesDir() +"logo_"+ company_name +".png";
        
        
        File file_dir_tmp = new File(dir_tmp);
        System.out.println("Directorio temporal: "+file_dir_tmp.getCanonicalPath());
        
        
        String file_name = "Pronostico de Cobranza.pdf";
        //ruta de archivo de salida
        String fileout = file_dir_tmp +"/"+  file_name;
        
        ArrayList<HashMap<String, String>> lista_pronostico = new ArrayList<HashMap<String, String>>();
        
        //obtiene los depositos del periodo indicado
        lista_pronostico = this.getCxcDao().getPronosticoDeCobranza(numero_semanas,opcion_seleccionada, id_empresa);
        
        
        //instancia a la clase que construye el pdf de la del reporte de estado de cuentas del cliente
        PdfReportePronosticoCobranza x = new PdfReportePronosticoCobranza(lista_pronostico,razon_social_empresa,fileout);
        
        
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
            Logger.getLogger(RepPronosCobranzaController.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
        
    }
}
