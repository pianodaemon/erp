/**
 * Modulo: INVENTARIOS
 * Aplicativo: REPORTE DE EXISTECIAS POR PRESENTACIONES
 * @author gpmarsan@gmail.com
 * Fecha: 26/abril/2013
 * 
 */
package com.agnux.kemikal.controllers;
import com.agnux.cfd.v2.Base64Coder;
import com.agnux.common.helpers.FileHelper;
import com.agnux.common.helpers.TimeHelper;
import com.agnux.common.obj.ResourceProject;
import com.agnux.common.obj.UserSessionData;
import com.agnux.kemikal.interfacedaos.GralInterfaceDao;
import com.agnux.kemikal.interfacedaos.HomeInterfaceDao;
import com.agnux.kemikal.interfacedaos.InvInterfaceDao;
import com.agnux.kemikal.reportes.PdfReporteInvExisLotes;
import com.agnux.kemikal.reportes.PdfReporteInvExisPres;
import com.agnux.xml.labels.EtiquetaCompras;
import com.agnux.xml.labels.generandoxml;
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
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.transform.TransformerException;
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
@RequestMapping("/repinvexispres/")
public class RepInvExisPresController {
    private static final Logger log  = Logger.getLogger(RepInvExisPresController.class.getName());
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
            @ModelAttribute("user") UserSessionData user)
            throws ServletException, IOException {
        
        log.log(Level.INFO, "Ejecutando starUp de {0}", RepInvExisPresController.class.getName());
        LinkedHashMap<String,String> infoConstruccionTabla = new LinkedHashMap<String,String>();
        
        ModelAndView x = new ModelAndView("repinvexispres/startup", "title", "Reporte de Existencias por Presentaciones");
        
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
    @RequestMapping(method = RequestMethod.POST, value="/getDatosBuscador.json")
    public @ResponseBody HashMap<String,ArrayList<HashMap<String, String>>> getDatosBuscadorJson(
            @RequestParam(value="iu", required=true) String id_user,
            Model model
        ) {
        
        log.log(Level.INFO, "Ejecutando getDatosBuscadorJson de {0}", RepInvExisPresController.class.getName());
        HashMap<String,ArrayList<HashMap<String, String>>> jsonretorno = new HashMap<String,ArrayList<HashMap<String, String>>>();
        ArrayList<HashMap<String, String>> almacenes = new ArrayList<HashMap<String, String>>();
        ArrayList<HashMap<String, String>> presentaciones = new ArrayList<HashMap<String, String>>();
        HashMap<String, String> userDat = new HashMap<String, String>();
        
        //decodificar id de usuario
        Integer id_usuario = Integer.parseInt(Base64Coder.decodeString(id_user));
        userDat = this.getHomeDao().getUserById(id_usuario);
        
        Integer id_empresa = Integer.parseInt(userDat.get("empresa_id"));
        
        almacenes = this.getInvDao().getAlmacenes2(id_empresa);
        presentaciones = this.getInvDao().getProducto_Presentaciones(0);
        
        jsonretorno.put("Almacenes", almacenes);
        jsonretorno.put("Presentaciones", presentaciones);
        
        return jsonretorno;
    }
    
    
    //obtiene la existencia de un Almacen en especifico
    @RequestMapping(method = RequestMethod.POST, value="/getExistencias.json")
    public @ResponseBody HashMap<String,ArrayList<HashMap<String, String>>> getExistenciasJson(
            @RequestParam("tipo") Integer tipo,
            @RequestParam("almacen") Integer almacen,
            @RequestParam("codigo") String codigo,
            @RequestParam("descripcion") String descripcion,
            @RequestParam("presentacion") String presentacion,
            @RequestParam(value="iu", required=true) String id_user,
            Model model
        ) {
        
        log.log(Level.INFO, "Ejecutando getExistenciasJson de {0}", RepInvExisPresController.class.getName());
        HashMap<String,ArrayList<HashMap<String, String>>> jsonretorno = new HashMap<String,ArrayList<HashMap<String, String>>>();
        ArrayList<HashMap<String, String>> existencias = new ArrayList<HashMap<String, String>>();
        
        HashMap<String, String> userDat = new HashMap<String, String>();
        
        //decodificar id de usuario
        Integer id_usuario = Integer.parseInt(Base64Coder.decodeString(id_user));
        userDat = this.getHomeDao().getUserById(id_usuario);
        
        //Integer id_empresa = Integer.parseInt(userDat.get("empresa_id"));
        Integer app_selected = 139; //Reporte de Existencias por Presentaciones
        String command_selected="reporte";
        
        codigo = "%"+codigo+"%";
        descripcion = "%"+descripcion+"%";
        
        String data_string = app_selected+"___"+id_usuario+"___"+command_selected+"___"+tipo+"___"+almacen+"___"+codigo+"___"+descripcion+"___"+presentacion;
        
        existencias = this.getInvDao().selectFunctionForInvReporte(app_selected,data_string);
        
        jsonretorno.put("Existencias", existencias);
        
        return jsonretorno;
    }
    
    
    
    
   //Genera pdf de Reporte de Existencias en Por Presentaciones
    @RequestMapping(value = "/getReporteExisPres/{cadena}/{iu}/out.json", method = RequestMethod.GET ) 
    public ModelAndView getReporteExistenciasJson(
                @PathVariable("cadena") String cadena,
                @PathVariable("iu") String id_user,
                HttpServletRequest request, 
                HttpServletResponse response, 
                Model model)
            throws ServletException, IOException, URISyntaxException, DocumentException, Exception {
        
        HashMap<String, String> userDat = new HashMap<String, String>();
        ArrayList<HashMap<String, String>> lista_existencias = new ArrayList<HashMap<String, String>>();
        HashMap<String, String> datos = new HashMap<String, String>();
        ArrayList<HashMap<String, String>> almacenes = new ArrayList<HashMap<String, String>>();
        
        //decodificar id de usuario
        Integer id_usuario = Integer.parseInt(Base64Coder.decodeString(id_user));
        userDat = this.getHomeDao().getUserById(id_usuario);
        Integer id_empresa = Integer.parseInt(userDat.get("empresa_id"));
        Integer app_selected = 139; //Reporte de Existencias por Presentaciones
        String command_selected="reporte";
        
        String cad[] = cadena.split("___");
        
        Integer tipo = Integer.parseInt(cad[0]);
        Integer almacen = Integer.parseInt(cad[1]);
        String codigo = cad[2];
        String descripcion = cad[3];
        String presentacion = cad[4];
        
        if(codigo.equals("0")){
            codigo="";
        }
        
        if(descripcion.equals("0")){
            descripcion="";
        }
        
        codigo = "%"+codigo+"%";
        descripcion = "%"+descripcion+"%";
        
        System.out.println("Generando Reporte de Existencias por Presentaciones");
        
        String rfc_empresa = this.getGralDao().getRfcEmpresaEmisora(id_empresa);
        String razon_social_empresa = this.getGralDao().getRazonSocialEmpresaEmisora(id_empresa);
        
        //obtener el directorio temporal
        String dir_tmp = this.getGralDao().getTmpDir();
        File file_dir_tmp = new File(dir_tmp);
        String file_name = "rep_exis_pres"+rfc_empresa+".pdf";
        
        //ruta de archivo de salida
        String fileout = file_dir_tmp +"/"+  file_name;
        
        //String fecha_actual = TimeHelper.getFechaActualYMD();
        //System.out.println("fecha_actual: "+fecha_actual);
        SimpleDateFormat formato = new SimpleDateFormat("'Impreso en' MMMMM d, yyyy 'a las' HH:mm:ss 'hrs.'");
        String impreso_en = formato.format(new Date());
        String nombre_almacen="";
        almacenes = this.getInvDao().getAlmacenes2(id_empresa);
        
        Iterator it = almacenes.iterator();
        while(it.hasNext()){
            HashMap<String,String> map = (HashMap<String,String>)it.next();
            if(almacen == Integer.parseInt(map.get("id"))){
                nombre_almacen = "Almacen: "+map.get("titulo");
            }
            
        }

        datos.put("fileout", fileout);
        datos.put("empresa", razon_social_empresa);
        datos.put("titulo_reporte", "Reporte de Existencias por Presentaciones");
        datos.put("almacen", nombre_almacen);
        datos.put("fecha_impresion", impreso_en);
        datos.put("codigo1", "");
        datos.put("codigo2", "");
        
        String data_string = app_selected+"___"+id_usuario+"___"+command_selected+"___"+tipo+"___"+almacen+"___"+codigo+"___"+descripcion+"___"+presentacion;
        
        lista_existencias = this.getInvDao().selectFunctionForInvReporte(app_selected,data_string);
        
        //instancia a la clase que construye el pdf del reporte de existencias
        PdfReporteInvExisPres pdf = new PdfReporteInvExisPres(lista_existencias, datos);
        pdf.ViewPDF();
        
        
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
