package com.agnux.kemikal.controllers;

import com.agnux.cfd.v2.Base64Coder;
import com.agnux.common.helpers.FileHelper;
import com.agnux.common.helpers.TimeHelper;
import com.agnux.common.obj.ResourceProject;
import com.agnux.common.obj.UserSessionData;
import com.agnux.kemikal.interfacedaos.GralInterfaceDao;
import com.agnux.kemikal.interfacedaos.HomeInterfaceDao;
import com.agnux.kemikal.interfacedaos.InvInterfaceDao;
import com.agnux.kemikal.reportes.PdfReporteInventarioExistencias;
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
@RequestMapping("/invrepexis/")
public class InvRepExisController {
    private static final Logger log  = Logger.getLogger(InvRepExisController.class.getName());
    ResourceProject resource = new ResourceProject();
    //repinvexis
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
        
        log.log(Level.INFO, "Ejecutando starUp de {0}", InvRepExisController.class.getName());
        LinkedHashMap<String,String> infoConstruccionTabla = new LinkedHashMap<String,String>();
        
        
        ModelAndView x = new ModelAndView("invrepexis/startup", "title", "Existencias en Inventario");
        
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
    
    
    
    
    
    
    //obtiene datos para el buscador de traspasos
    @RequestMapping(method = RequestMethod.POST, value="/getAlmacenes.json")
    public @ResponseBody HashMap<String,ArrayList<HashMap<String, String>>> getAlmacenesJson(
            @RequestParam(value="iu", required=true) String id_user,
            Model model
        ) {
        
        log.log(Level.INFO, "Ejecutando getAlmacenesJson de {0}", InvRepExisController.class.getName());
        HashMap<String,ArrayList<HashMap<String, String>>> jsonretorno = new HashMap<String,ArrayList<HashMap<String, String>>>();
        HashMap<String, String> userDat = new HashMap<String, String>();
        
        //decodificar id de usuario
        Integer id_usuario = Integer.parseInt(Base64Coder.decodeString(id_user));
        userDat = this.getHomeDao().getUserById(id_usuario);
        
        Integer id_empresa = Integer.parseInt(userDat.get("empresa_id"));
        
        jsonretorno.put("Almacenes", this.getInvDao().getAlmacenes2(id_empresa));
        jsonretorno.put("TiposProd", this.getInvDao().getProducto_TiposInventariable());
        
        return jsonretorno;
    }
    
    
    
    
    
    
    
    
    
    //obtiene la existencia de un Almacen en especifico
    @RequestMapping(method = RequestMethod.POST, value="/getExistencias.json")
    public @ResponseBody HashMap<String,ArrayList<HashMap<String, String>>> getExistenciasJson(
            @RequestParam("tipo") Integer tipo_reporte,
            @RequestParam("almacen") Integer almacen,
            @RequestParam("codigo") String codigo_producto,
            @RequestParam("descripcion") String descripcion,
            @RequestParam("tipo_costo") Integer tipo_costo,
            @RequestParam("tipo_prod") Integer tipo_prod,
            @RequestParam(value="iu", required=true) String id_user,
            Model model
            ) {
        
        log.log(Level.INFO, "Ejecutando getExistenciasJson de {0}", InvRepExisController.class.getName());
        HashMap<String,ArrayList<HashMap<String, String>>> jsonretorno = new HashMap<String,ArrayList<HashMap<String, String>>>();
        ArrayList<HashMap<String, String>> existencias = new ArrayList<HashMap<String, String>>();
        ArrayList<HashMap<String, String>> monedas = new ArrayList<HashMap<String, String>>();
        HashMap<String, String> userDat = new HashMap<String, String>();
        
        String codigo;
        String desc;
        //Reporte de Existencias en Inventario
        Integer app_selected=133;
        String command_selected="reporte";
        
        codigo = "%"+codigo_producto+"%";
        desc = "%"+descripcion+"%";
        
        //decodificar id de usuario
        Integer id_usuario = Integer.parseInt(Base64Coder.decodeString(id_user));
        userDat = this.getHomeDao().getUserById(id_usuario);
        
        Integer id_empresa = Integer.parseInt(userDat.get("empresa_id"));
        
        String data_string = app_selected+"___"+id_usuario+"___"+command_selected+"___"+almacen+"___"+codigo+"___"+desc+"___"+tipo_reporte+"___"+tipo_costo+"___"+tipo_prod;
        //existencias = this.getInvDao().getDatos_ReporteExistencias(id_usuario, almacen, codigo, desc, tipo);
        existencias = this.getInvDao().selectFunctionForInvReporte(app_selected,data_string);
        
        monedas = this.getInvDao().getMonedas();
        
        jsonretorno.put("Existencias", existencias);
        jsonretorno.put("Monedas", monedas);
        
        return jsonretorno;
    }
    
    
    
    
    
    
   //Genera pdf de Reporte de Existencias en Inventario
    @RequestMapping(value = "/getReporteExistencias/{tipo}/{almacen}/{codigo}/{descripcion}/{tipo_costo}/{tipo_prod}/{iu}/out.json", method = RequestMethod.GET ) 
    public ModelAndView getReporteExistenciasJson(
                @PathVariable("tipo") Integer tipo_reporte,
                @PathVariable("almacen") Integer almacen,
                @PathVariable("codigo") String codigo_producto,
                @PathVariable("descripcion") String descripcion,
                @PathVariable("tipo_costo") Integer tipo_costo,
                @PathVariable("tipo_prod") Integer tipo_prod,
                @PathVariable("iu") String id_user,
                HttpServletRequest request, 
                HttpServletResponse response, 
                Model model)
            throws ServletException, IOException, URISyntaxException, DocumentException, Exception {
        
        ArrayList<HashMap<String, String>> lista_existencias = new ArrayList<HashMap<String, String>>();
        HashMap<String, String> userDat = new HashMap<String, String>();
        String codigo;
        String desc;
        //Reporte de Existencias en Inventario
        Integer app_selected=133;
        String command_selected="reporte";
        
        if(codigo_producto.equals("0")){
            codigo = "%%";
        }else{
            codigo = "%"+codigo_producto+"%";;
        }

        if(descripcion.equals("0")){
            desc = "%%";
        }else{
            desc = "%"+descripcion+"%";
        }
        
        System.out.println("Generando Reporte de Existencias");
        
        //decodificar id de usuario
        Integer id_usuario = Integer.parseInt(Base64Coder.decodeString(id_user));
        
        userDat = this.getHomeDao().getUserById(id_usuario);
        Integer id_empresa = Integer.parseInt(userDat.get("empresa_id"));
        
        
        String razon_social_empresa = this.getGralDao().getRazonSocialEmpresaEmisora(id_empresa);
        
        //obtener el directorio temporal
        String dir_tmp = this.getGralDao().getTmpDir();
        
        
        String[] array_company = razon_social_empresa.split(" ");
        String company_name= array_company[0].toLowerCase();
        //String ruta_imagen = this.getGralDao().getImagesDir() +"logo_"+ company_name +".png";
        
        File file_dir_tmp = new File(dir_tmp);
        System.out.println("Directorio temporal: "+file_dir_tmp.getCanonicalPath());
        
        String file_name = "exis_inv_"+razon_social_empresa+".pdf";
        //ruta de archivo de salida
        String fileout = file_dir_tmp +"/"+  file_name;
        
        String data_string = app_selected+"___"+id_usuario+"___"+command_selected+"___"+almacen+"___"+codigo+"___"+desc+"___"+tipo_reporte+"___"+tipo_costo+"___"+tipo_prod;
        
        //obtiene las facturas del periodo indicado
        lista_existencias = this.getInvDao().selectFunctionForInvReporte(app_selected,data_string);
        
        
//        String tipo = "";
        String fecha_actual = TimeHelper.getFechaActualYMD();
        
        System.out.println("fecha_actual: "+fecha_actual);
        
        //instancia a la clase que construye el pdf del reporte de existencias
        PdfReporteInventarioExistencias x = new PdfReporteInventarioExistencias( fileout, lista_existencias, razon_social_empresa,fecha_actual,tipo_reporte);
        
        
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
