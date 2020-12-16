package com.agnux.kemikal.controllers;


import com.agnux.cfd.v2.Base64Coder;
import com.agnux.common.helpers.FileHelper;
import com.agnux.common.obj.ResourceProject;
import com.agnux.common.obj.UserSessionData;
import com.agnux.kemikal.interfacedaos.ComInterfaceDao;
import com.agnux.kemikal.interfacedaos.GralInterfaceDao;
import com.agnux.kemikal.interfacedaos.HomeInterfaceDao;
import com.agnux.kemikal.reportes.ComRepDiasEntregaPromedioPdf;
import com.agnux.kemikal.reportes.ComRepDiasEntregaPromedioXls;
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
@RequestMapping("/comdiasentrega/")
public class ComOcDiasEntController {
    private static final Logger log  = Logger.getLogger(ComOcDiasEntController.class.getName());
    ResourceProject resource = new ResourceProject();
    
    @Autowired
    @Qualifier("daoCom")
    private ComInterfaceDao ComDao;
  
    
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
    
    public ComInterfaceDao getComDao() {
        return ComDao;
    }
    
    
    @RequestMapping(value="/startup.agnux")
    public ModelAndView startUp(HttpServletRequest request, HttpServletResponse response, 
            @ModelAttribute("user") UserSessionData user)
            throws ServletException, IOException {
        
        log.log(Level.INFO, "Ejecutando starUp de {0}", ComOcDiasEntController.class.getName());
        LinkedHashMap<String,String> infoConstruccionTabla = new LinkedHashMap<String,String>();
        
        ModelAndView x = new ModelAndView("comdiasentrega/startup", "title", "D&iacute;as de entrega O.C.");
        
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
        
        proveedores = this.getComDao().getBuscadorProveedores(rfc, email, nombre,id_empresa);
        
        jsonretorno.put("Proveedores", proveedores);
        
        return jsonretorno;
    }
    
    
    
    
    //obtiene las facturas de los  proveedores
    @RequestMapping(method = RequestMethod.POST, value="/getDatosReporte.json")
    public @ResponseBody HashMap<String,ArrayList<HashMap<String, String>>> getDatosReporteJson(
            @RequestParam("tipo_reporte") Integer tipo_reporte,
            @RequestParam("proveedor") String proveedor,
            @RequestParam("f_inicial") String f_inicial,
            @RequestParam("f_final") String f_final,
            @RequestParam(value="iu", required=true) String id_user,
            Model model
        ) {
        
        log.log(Level.INFO, "Ejecutando getDatosReporteJson de {0}", ComOcDiasEntController.class.getName());
        HashMap<String,ArrayList<HashMap<String, String>>> jsonretorno = new HashMap<String,ArrayList<HashMap<String, String>>>();
        
        HashMap<String, String> userDat = new HashMap<String, String>();
        ArrayList<HashMap<String, String>> compras = new ArrayList<HashMap<String, String>>();
        
        //Decodificar id de usuario
        Integer id_usuario = Integer.parseInt(Base64Coder.decodeString(id_user));
        userDat = this.getHomeDao().getUserById(id_usuario);
        
        Integer id_empresa = Integer.parseInt(userDat.get("empresa_id"));
        
        //Obtiene datos de las compras
        compras = this.getComDao().getOC_DiasEntrega(tipo_reporte, proveedor, f_inicial, f_final,id_empresa);
        
        jsonretorno.put("Data", compras);
        
        return jsonretorno;
    }
    
    
    
    
    
    
    
    
    //Genera Reporte de Dicas Promedio de entrega de OC
    @RequestMapping(value = "/getReporte/{cadena}/{iu}/out.json", method = RequestMethod.GET ) 
    public ModelAndView getReporteJson(
                @PathVariable("cadena") String cadena,
                @PathVariable("iu") String id_user_cod,
                HttpServletRequest request,
                HttpServletResponse response, 
                Model model)
            throws ServletException, IOException, URISyntaxException, DocumentException, Exception {
        
        HashMap<String, String> userDat = new HashMap<String, String>();
        
        String tituloReporte="Reporte de Días de Entrega de O.C.";
        System.out.println("Generando reporte de "+tituloReporte);
        
        //decodificar id de usuario
        Integer id_usuario = Integer.parseInt(Base64Coder.decodeString(id_user_cod));
        //System.out.println("id_usuario: "+id_usuario);
        
        userDat = this.getHomeDao().getUserById(id_usuario);
        Integer id_empresa = Integer.parseInt(userDat.get("empresa_id"));
        
        String razon_social_empresa = this.getGralDao().getRazonSocialEmpresaEmisora(id_empresa);
        
        String ArrayCad[] = cadena.split("___");
        Integer tipo_reporte = Integer.parseInt(ArrayCad[0]);
        String f_inicial = ArrayCad[1];
        String f_final = ArrayCad[2];
        String proveedor = ArrayCad[3];
        String tipoFichero = ArrayCad[4];
        
        String arrayFI[] = f_inicial.split("-");
        String arrayFF[] = f_final.split("-");
        
        String periodo = "Periodo del "+arrayFI[2]+"/"+arrayFI[1]+"/"+arrayFI[0] +" al "+ arrayFF[2]+"/"+arrayFF[1]+"/"+arrayFF[0];
        
        
        //obtener el directorio temporal
        //String dir_tmp = System.getProperty("java.io.tmpdir");
        String dir_tmp = this.getGralDao().getTmpDir();
        
        
        File file_dir_tmp = new File(dir_tmp);
        //System.out.println("Directorio temporal: "+file_dir_tmp.getCanonicalPath());
        
        String file_name = "OC_DIAS_PROM"+f_inicial+"_"+f_final+"."+tipoFichero;
        //ruta de archivo de salida
        String fileout = file_dir_tmp +"/"+  file_name;
        
        ArrayList<HashMap<String, String>> compras = new ArrayList<HashMap<String, String>>();
        
        //obtiene facturas en pesos. Moneda 1
        compras = this.getComDao().getOC_DiasEntrega(tipo_reporte, proveedor, f_inicial, f_final,id_empresa);
        

        
        if(tipoFichero.equals("pdf")){
            //Instancia de la clase que construye el pdf
            ComRepDiasEntregaPromedioPdf pdf = new ComRepDiasEntregaPromedioPdf(fileout,tituloReporte,razon_social_empresa,periodo,compras);
        }
        
        if(tipoFichero.equals("xls")){
            //Instancia de la clase que construye el xls
            ComRepDiasEntregaPromedioXls excel = new ComRepDiasEntregaPromedioXls(fileout,tituloReporte,razon_social_empresa,periodo,compras);
        }
        
        System.out.println("Recuperando archivo: " + fileout);
        File file = new File(fileout);
        int size = (int) file.length(); // Tamaño del archivo
        BufferedInputStream bis = new BufferedInputStream(new FileInputStream(file));
        response.setBufferSize(size);
        response.setContentLength(size);
        response.setContentType("application/"+tipoFichero);
        response.setHeader("Content-Disposition","attachment; filename=\"" + file.getName() +"\"");
        FileCopyUtils.copy(bis, response.getOutputStream());  	
        response.flushBuffer();
        
        if(file.exists()){
            FileHelper.delete(fileout);
        }
        
        return null;
    }
}
