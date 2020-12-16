/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
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
import com.agnux.xml.labels.EtiquetaCompras;
import com.agnux.xml.labels.generandoxml;
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

/**
 * @author gpmarsan@gmail.com
 * 31/marzo/2012
 * Este controller es para generar el reporte de Existencias en Inventario
 */

@Controller
@SessionAttributes({"user"})
@RequestMapping("/repinvexislote/")
public class RepInvExisLoteController {
    private static final Logger log  = Logger.getLogger(InvRepExisController.class.getName());
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
        
        log.log(Level.INFO, "Ejecutando starUp de {0}", InvRepExisController.class.getName());
        LinkedHashMap<String,String> infoConstruccionTabla = new LinkedHashMap<String,String>();
        
        
        ModelAndView x = new ModelAndView("repinvexislote/startup", "title", "Existencias en Lotes por Producto");
        
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
        ArrayList<HashMap<String, String>> Almacenes = new ArrayList<HashMap<String, String>>();
        HashMap<String, String> userDat = new HashMap<String, String>();
        
        //decodificar id de usuario
        Integer id_usuario = Integer.parseInt(Base64Coder.decodeString(id_user));
        userDat = this.getHomeDao().getUserById(id_usuario);
        
        Integer id_empresa = Integer.parseInt(userDat.get("empresa_id"));
        
        Almacenes = this.getInvDao().getAlmacenes2(id_empresa);
        
        jsonretorno.put("Almacenes", Almacenes);
        
        return jsonretorno;
    }
    
    
    
    
    
    
    
    
    
    //obtiene la existencia de un Almacen en especifico
    @RequestMapping(method = RequestMethod.POST, value="/getExistencias.json")
    public @ResponseBody HashMap<String,ArrayList<HashMap<String, String>>> getExistenciasJson(
            @RequestParam("tipo") Integer tipo,
            @RequestParam("almacen") Integer almacen,
            @RequestParam("codigo") String codigo_producto,
            @RequestParam("descripcion") String descripcion,
            @RequestParam("lote_interno") String lote_interno,
            @RequestParam(value="iu", required=true) String id_user,
            Model model
            ) {
        
        log.log(Level.INFO, "Ejecutando getExistenciasJson de {0}", InvRepExisController.class.getName());
        HashMap<String,ArrayList<HashMap<String, String>>> jsonretorno = new HashMap<String,ArrayList<HashMap<String, String>>>();
        ArrayList<HashMap<String, String>> existencias = new ArrayList<HashMap<String, String>>();
        ArrayList<HashMap<String, String>> medidas_etiqueta = new ArrayList<HashMap<String, String>>();
        HashMap<String, String> userDat = new HashMap<String, String>();
        
        //decodificar id de usuario
        Integer id_usuario = Integer.parseInt(Base64Coder.decodeString(id_user));
        userDat = this.getHomeDao().getUserById(id_usuario);
        
        Integer id_empresa = Integer.parseInt(userDat.get("empresa_id"));
        
        existencias = this.getInvDao().getReporteExistenciasLotes_Datos(almacen, codigo_producto, descripcion, tipo, lote_interno);
        medidas_etiqueta = this.getInvDao().getReporteExistenciasLotes_MedidasEtiquetas();
        
        jsonretorno.put("Existencias", existencias);
        jsonretorno.put("MedidasEtiqueta", medidas_etiqueta);
        
        return jsonretorno;
    }
    
    
    
    
    
    //impresion de etiquetas
    @RequestMapping(method = RequestMethod.POST, value="/print.json")
    public @ResponseBody HashMap<String, String> editJson(
            @RequestParam(value="id_lote", required=true) String[] id_lote,
            @RequestParam(value="selec", required=true) String[] selec,
            @RequestParam(value="cant", required=true) String[] cant,
            @RequestParam(value="cantProd", required=true) String[] cantProd,
            @RequestParam(value="select_medida", required=true) String[] select_medida,
            @RequestParam(value="tipo_prod", required=true) String[] tipo_prod,
            Model model,
            @ModelAttribute("user") UserSessionData user,
            HttpServletRequest request, 
            HttpServletResponse response
        ) throws TransformerException, IOException {
        
        HashMap<String, String> jsonretorno = new HashMap<String, String>();
        LinkedHashMap<String, Object> datosLote;
        Integer app_selected = 103;//Reporte de existencias por Lote-Incluye impresion de Etiquetas
        String succes = "";
        String lote_interno="";
        String ip_cliente = request.getRemoteAddr();
        
        ip_cliente = ip_cliente.replace(".", "");
        
        int contador=0;
        for (int i=0; i<selec.length; i++){
            if(selec[i].equals("1")){
                datosLote = new LinkedHashMap<String, Object>();
                datosLote = this.getInvDao().getDatosEtiquetaLote(Integer.parseInt(id_lote[i]), Integer.parseInt(tipo_prod[i]), Integer.parseInt(select_medida[i]));
                
                //agrega la cantidad que se le indica desde la interfaz de usuario, para que salgha en la etiqueta
                datosLote.put("producto_cantidad", String.valueOf(cantProd[i]));
                
                lote_interno = String.valueOf(datosLote.get("lote_interno"));
                datosLote.put("cuerpo_titulo", "Etiqueta_Compras");
                
                generandoxml xml=null;
                EtiquetaCompras file_xml=null;
                
                int cantidad = Integer.parseInt(cant[i]);
                for (int c=1; c<=cantidad; c++){
                    //obtener ruta del directorio Zebra
                    String dirZebraIn = this.getGralDao().getZebraInDir();
                    String file_name = ip_cliente+"_"+lote_interno+"_"+c+".xml";
                    
                    datosLote.put("nombre_archivo", file_name);
                    
                    file_xml = new EtiquetaCompras(datosLote);
                    xml =new generandoxml(file_xml.getDoc());
                    String  xmlString=xml.createxml(file_xml.getDoc());
                    
                    
                    File file = new File(dirZebraIn+"/"+file_name);
                    
                    if(!file.exists()){
                        FileHelper.createFileWithText(dirZebraIn,file_name, xmlString);
                    }
                }
                
                contador++;
            }
        }
        
        if(contador > 0){
            succes="true";
        }else{
            succes="false";
        }
        
        jsonretorno.put("success",succes);
        
        log.log(Level.INFO, "Salida json {0}", String.valueOf(jsonretorno.get("success")));
        return jsonretorno;
    }
    
    
    
    
   //Genera pdf de Reporte de Existencias en Inventario
    @RequestMapping(value = "/getReporteExistencias/{cadena}/{iu}/out.json", method = RequestMethod.GET ) 
    public ModelAndView getReporteExistenciasJson(
                @PathVariable("cadena") String cadena,
                @PathVariable("iu") String id_user,
                HttpServletRequest request, 
                HttpServletResponse response, 
                Model model)
            throws ServletException, IOException, URISyntaxException, DocumentException, Exception {
        
        HashMap<String, String> userDat = new HashMap<String, String>();
        
        System.out.println("cadena: "+cadena);
        
        String cad[] = cadena.split("___");
        
        Integer opcion_reporte = Integer.parseInt(cad[0]);
        Integer almacen = Integer.parseInt(cad[1]);
        String codigo_producto = cad[2];
        String descripcion = cad[3];
        String lote_interno = cad[4];
        
        
        if(codigo_producto.equals("0")){
            codigo_producto="";
        }
        
        if(descripcion.equals("0")){
            descripcion="";
        }
        
        if(lote_interno.equals("0")){
            lote_interno="";
        }
        
        System.out.println("Generando Reporte de Existencias de Lotes");
        
        //decodificar id de usuario
        Integer id_usuario = Integer.parseInt(Base64Coder.decodeString(id_user));
        
        userDat = this.getHomeDao().getUserById(id_usuario);
        Integer id_empresa = Integer.parseInt(userDat.get("empresa_id"));
        String rfc_empresa = this.getGralDao().getRfcEmpresaEmisora(id_empresa);
        String razon_social_empresa = this.getGralDao().getRazonSocialEmpresaEmisora(id_empresa);
        
        //obtener el directorio temporal
        String dir_tmp = this.getGralDao().getTmpDir();
        
        
        String[] array_company = razon_social_empresa.split(" ");
        String company_name= array_company[0].toLowerCase();
        //String ruta_imagen = this.getGralDao().getImagesDir() +"logo_"+ company_name +".png";
        
        File file_dir_tmp = new File(dir_tmp);
        System.out.println("Directorio temporal: "+file_dir_tmp.getCanonicalPath());
        
        
        String file_name = "exis_lote_"+rfc_empresa+".pdf";
        //ruta de archivo de salida
        String fileout = file_dir_tmp +"/"+  file_name;
        
        ArrayList<HashMap<String, String>> lista_existencias = new ArrayList<HashMap<String, String>>();
        
        //obtiene las facturas del periodo indicado
        lista_existencias = this.getInvDao().getReporteExistenciasLotes_Datos(almacen, codigo_producto, descripcion, opcion_reporte, lote_interno);
        
        String fecha_actual = TimeHelper.getFechaActualYMD();
        
        System.out.println("fecha_actual: "+fecha_actual);
        
        //instancia a la clase que construye el pdf del reporte de existencias
        PdfReporteInvExisLotes pdf = new PdfReporteInvExisLotes( lista_existencias, fileout, razon_social_empresa);
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
