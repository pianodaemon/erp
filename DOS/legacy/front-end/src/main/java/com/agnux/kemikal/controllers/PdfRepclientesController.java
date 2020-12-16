package com.agnux.kemikal.controllers;
import com.agnux.common.helpers.TimeHelper;
import com.agnux.cfd.v2.Base64Coder;
import com.agnux.common.helpers.FileHelper;
import com.agnux.common.obj.ResourceProject;
import com.agnux.common.obj.UserSessionData;
import com.agnux.kemikal.interfacedaos.CxcInterfaceDao;
import com.agnux.kemikal.interfacedaos.GralInterfaceDao;
import com.agnux.kemikal.interfacedaos.HomeInterfaceDao;
import com.agnux.kemikal.reportes.PdfReporteClientes;
import com.itextpdf.text.DocumentException;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URISyntaxException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
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
 *
yooo
 */
@Controller
@SessionAttributes({"user"})
@RequestMapping("/repclientes/")
public class PdfRepclientesController {
    private static final Logger log  = Logger.getLogger(PdfRepclientesController.class.getName());
    ResourceProject resource = new ResourceProject();
    
    @Autowired
    @Qualifier("daoCxc")
    private CxcInterfaceDao cxcDao;

    public CxcInterfaceDao getCxcDao() {
        return cxcDao;
    }

    public void setCxcDao(CxcInterfaceDao cxcDao) {
        this.cxcDao = cxcDao;
    }
    
    @Autowired
    @Qualifier("daoHome")
    private HomeInterfaceDao HomeDao;

    @Autowired
    @Qualifier("daoGral")
    private GralInterfaceDao gralDao;
    
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

        log.log(Level.INFO, "Ejecutando starUp de {0}", PdfRepclientesController.class.getName());
        LinkedHashMap<String,String> infoConstruccionTabla = new LinkedHashMap<String,String>();
        
        ModelAndView x = new ModelAndView("crmrepclientes/startup", "title", "Reporte de Clientes");
        
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
    
    
    
    //obtiene datos para el buscador
    @RequestMapping(method = RequestMethod.POST, value="/getDatosBusqueda.json")
    //obtiene los tipos de agente
    public @ResponseBody HashMap<String,ArrayList<HashMap<String, String>>> getAgentesJson(
        @RequestParam(value="iu", required=true) String id_user,Model model){

        HashMap<String,ArrayList<HashMap<String, String>>> jsonretorno = new HashMap<String,ArrayList<HashMap<String, String>>>();
        HashMap<String, String> userDat = new HashMap<String, String>();

        //decodificar id de usuario
        Integer id_usuario = Integer.parseInt(Base64Coder.decodeString(id_user));
        userDat = this.getHomeDao().getUserById(id_usuario);
        Integer id_empresa = Integer.parseInt(userDat.get("empresa_id"));

        jsonretorno.put("Agentes", this.getCxcDao().getAgentes(id_empresa));

        return jsonretorno;
    }
    
    
    
    @RequestMapping(method = RequestMethod.POST, value = "/getReporteClientes.json")
    public @ResponseBody
    HashMap<String, ArrayList<HashMap<String, String>>> getMovimientosJson(
            @RequestParam(value = "id_agente", required = true) Integer id_agente,
            @RequestParam(value = "iu", required = true) String id_user,
            Model model) {

        log.log(Level.INFO, "Ejecutando getMovimientosJson de {0}", PdfRepclientesController.class.getName());
        HashMap<String, ArrayList<HashMap<String, String>>> jsonretorno = new HashMap<String, ArrayList<HashMap<String, String>>>();
        ArrayList<HashMap<String, String>> lista_clientes = new ArrayList<HashMap<String, String>>();
        HashMap<String, String> userDat = new HashMap<String, String>();
        //decodificar id de usuario
        Integer id_usuario = Integer.parseInt(Base64Coder.decodeString(id_user));
        userDat = this.getHomeDao().getUserById(id_usuario);

        Integer id_empresa = Integer.parseInt(userDat.get("empresa_id"));

        lista_clientes = this.getCxcDao().getListaClientes(id_empresa,id_agente);

        jsonretorno.put("Clientes", lista_clientes);

        return jsonretorno;
    }


    //Genera pdf Reporte de Movimientos
    @RequestMapping(value = "/getReporteClientes/{cadena}/{iu}/out.json", method = RequestMethod.GET )
    public ModelAndView getGeneraPdfRemisionJson(
                @PathVariable("cadena") String cadena,
                @PathVariable("iu") String id_user,
                HttpServletRequest request,
                HttpServletResponse response,
                Model model)
        throws ServletException, IOException, URISyntaxException, DocumentException, Exception {

        HashMap<String, String> userDat = new HashMap<String, String>();
        ArrayList<HashMap<String, String>> lista_clientes = new ArrayList<HashMap<String, String>>();

        System.out.println("Generando Reporte de Clientes");
        Integer id_agente=0;
        
        String arrayCad [] = cadena.split("___");
        id_agente = Integer.parseInt(arrayCad [0]);
        
        //decodificar id de usuario
        Integer id_usuario = Integer.parseInt(Base64Coder.decodeString(id_user));
        userDat = this.getHomeDao().getUserById(id_usuario);
        Integer id_empresa = Integer.parseInt(userDat.get("empresa_id"));
        String rfc=this.getGralDao().getRfcEmpresaEmisora(id_empresa);

        String razon_social_empresa = this.getGralDao().getRazonSocialEmpresaEmisora(id_empresa);

        //fecha de impresion..
        String nombreMes= TimeHelper.ConvertNumToMonth(Integer.parseInt(TimeHelper.getMesActual()));

        SimpleDateFormat formato = new SimpleDateFormat("'Impreso el' d 'de "+nombreMes+" del ' yyyy 'a las' HH:mm:ss 'hrs.'");
        String impreso_en = formato.format(new Date());

        //obtener el directorio temporal
        String dir_tmp = this.getGralDao().getTmpDir();


        String[] array_company = razon_social_empresa.split(" ");
        String company_name= array_company[0].toLowerCase();
        String ruta_imagen = this.getGralDao().getImagesDir() +"logo_"+ company_name +".png";


        File file_dir_tmp = new File(dir_tmp);
        //System.out.println("Directorio temporal: "+file_dir_tmp.getCanonicalPath());

        String file_name = "REPCLIENTES_"+nombreMes+".pdf";

        //ruta de archivo de salida
        String fileout = file_dir_tmp +"/"+  file_name;


        lista_clientes = this.getCxcDao().getListaClientes(id_empresa,id_agente);
        //instancia a la clase que construye el pdf del reporte de facturas
        PdfReporteClientes x = new PdfReporteClientes( fileout,ruta_imagen,razon_social_empresa,impreso_en,lista_clientes);

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
