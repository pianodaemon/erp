/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.agnux.kemikal.controllers;
import com.agnux.kemikal.reportes.PdfReporteRemisiones;

import com.agnux.cfd.v2.Base64Coder;
import com.agnux.common.helpers.FileHelper;
import com.agnux.common.helpers.StringHelper;
import com.agnux.common.obj.ResourceProject;
import com.agnux.common.obj.UserSessionData;
import com.agnux.kemikal.interfacedaos.FacturasInterfaceDao;
import com.agnux.kemikal.interfacedaos.GralInterfaceDao;
import com.agnux.kemikal.interfacedaos.HomeInterfaceDao;
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
 *
 * @author Vale Santos
 */
@Controller
@SessionAttributes({"user"})
@RequestMapping("/repremision/")
public class RepRemisionController {
    private static final Logger log  = Logger.getLogger(RepRemisionController.class.getName());
    ResourceProject resource = new ResourceProject();
    
    @Autowired
    @Qualifier("daoFacturas")
    private FacturasInterfaceDao facdao;
        
    
    @Autowired
    @Qualifier("daoHome")
    private HomeInterfaceDao HomeDao;
    
    @Autowired
    @Qualifier("daoGral")
    private GralInterfaceDao gralDao;
    
    public FacturasInterfaceDao getFacdao() {
        return facdao;
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
        
        log.log(Level.INFO, "Ejecutando starUp de {0}", RepRemisionController.class.getName());
        LinkedHashMap<String,String> infoConstruccionTabla = new LinkedHashMap<String,String>();
        
        
        ModelAndView x = new ModelAndView("repremision/startup", "title", "Remision");
        
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
    
    
    
    
    
    
    //obtiene la existencia de un Almacen en especifico
    @RequestMapping(method = RequestMethod.POST, value="/getRemision.json")
    public @ResponseBody HashMap<String,ArrayList<HashMap<String, String>>> getRemisionJson(
            @RequestParam("opcion") Integer opcion,
            @RequestParam("estatus") Integer estatus,//agregado por paco
            @RequestParam("remision") String remision,
            @RequestParam("cliente") String cliente,
            @RequestParam("fecha_inicial") String fecha_inicial,
            @RequestParam("fecha_final") String fecha_final,
            @RequestParam(value="iu", required=true) String id_user,
            Model model
            ) {
        
        log.log(Level.INFO, "Ejecutando getRemision de {0}", RepRemisionController.class.getName());
        HashMap<String,ArrayList<HashMap<String, String>>> jsonretorno = new HashMap<String,ArrayList<HashMap<String, String>>>();
        ArrayList<HashMap<String, String>> remisiones = new ArrayList<HashMap<String, String>>();
        ArrayList<HashMap<String, String>>  totales = new ArrayList<HashMap<String, String>>();
        LinkedHashMap<String,String> total = new LinkedHashMap<String,String>();        
        HashMap<String, String> userDat = new HashMap<String, String>();
        Double suma_pesos_subtotal = 0.0;
        Double suma_pesos_monto_ieps = 0.0;
        Double suma_pesos_impuesto = 0.0;
        Double suma_pesos_total = 0.0;
        Double suma_dolares_subtotal = 0.0;
        Double suma_dolares_monto_ieps = 0.0;
        Double suma_dolares_impuesto = 0.0;
        Double suma_dolares_total = 0.0;
        
        Double suma_subtotal_mn = 0.0;
        Double suma_monto_ieps_mn = 0.0;
        Double suma_impuesto_mn = 0.0;
        Double suma_total_mn = 0.0;
        
        remision = "%"+remision+"%";
        cliente = "%"+cliente+"%";
                
        //decodificar id de usuario
        Integer id_usuario = Integer.parseInt(Base64Coder.decodeString(id_user));
        userDat = this.getHomeDao().getUserById(id_usuario);
        
        Integer id_empresa = Integer.parseInt(userDat.get("empresa_id"));
        
        //variable estatus agregado por paco
        remisiones = this.getFacdao().getDatosReporteRemision(opcion, remision, cliente, fecha_inicial, fecha_final, id_empresa, estatus);
        
        
        for (int x=0; x<=remisiones.size()-1;x++){
            HashMap<String,String> registro = remisiones.get(x);
            //sumar cantidades
            if(registro.get("moneda_id").equals("1")){
                suma_pesos_subtotal += Double.parseDouble(registro.get("subtotal"));
                suma_pesos_monto_ieps += Double.parseDouble(registro.get("monto_ieps"));
                suma_pesos_impuesto += Double.parseDouble(registro.get("impuesto"));
                suma_pesos_total += Double.parseDouble(registro.get("total"));
            }
            if(registro.get("moneda_id").equals("2")){
                suma_dolares_subtotal += Double.parseDouble(registro.get("subtotal"));
                suma_dolares_monto_ieps += Double.parseDouble(registro.get("monto_ieps"));
                suma_dolares_impuesto += Double.parseDouble(registro.get("impuesto"));
                suma_dolares_total += Double.parseDouble(registro.get("total"));
            }
            suma_subtotal_mn += Double.parseDouble(registro.get("subtotal_mn"));
            suma_monto_ieps_mn += Double.parseDouble(registro.get("monto_ieps_mn"));
            suma_impuesto_mn += Double.parseDouble(registro.get("impuesto_mn"));
            suma_total_mn += Double.parseDouble(registro.get("total_mn"));
        }
        
        total.put("suma_pesos_subtotal", StringHelper.roundDouble(suma_pesos_subtotal,2));
        total.put("suma_pesos_monto_ieps", StringHelper.roundDouble(suma_pesos_monto_ieps,2));
        total.put("suma_pesos_impuesto", StringHelper.roundDouble(suma_pesos_impuesto,2));
        total.put("suma_pesos_total", StringHelper.roundDouble(suma_pesos_total,2));
        
        total.put("suma_dolares_subtotal", StringHelper.roundDouble(suma_dolares_subtotal,2));
        total.put("suma_dolares_monto_ieps", StringHelper.roundDouble(suma_dolares_monto_ieps,2));
        total.put("suma_dolares_impuesto", StringHelper.roundDouble(suma_dolares_impuesto,2));
        total.put("suma_dolares_total", StringHelper.roundDouble(suma_dolares_total,2));
        
        total.put("suma_subtotal_mn", StringHelper.roundDouble(suma_subtotal_mn,2));
        total.put("suma_monto_ieps_mn", StringHelper.roundDouble(suma_monto_ieps_mn,2));
        total.put("suma_impuesto_mn", StringHelper.roundDouble(suma_impuesto_mn,2));
        total.put("suma_total_mn", StringHelper.roundDouble(suma_total_mn,2));
        
        totales.add(total);
        jsonretorno.put("Remisiones", remisiones);
        jsonretorno.put("Totales", totales);
        
        return jsonretorno;
    }
    
    
    
    
    
    
    
    //Genera pdf de Remisiones de
    @RequestMapping(value = "/get_genera_reporte_remision/{cadena}/{iu}/out.json", method = RequestMethod.GET ) 
    public ModelAndView getGeneraPdfRemisionJson(
                @PathVariable("cadena") String cadena,
                @PathVariable("iu") String id_user,
                HttpServletRequest request, 
                HttpServletResponse response, 
                Model model)
        throws ServletException, IOException, URISyntaxException, DocumentException {
        
        HashMap<String, String> userDat = new HashMap<String, String>();
        ArrayList<HashMap<String, String>> lista_remisiones = new ArrayList<HashMap<String, String>>();
        
        System.out.println("Generando reporte de Remision");
        Integer opciones=0;
        String remision="";
        String cliente="";
        String fecha_inicial="";
        String fecha_final="";
        String estatus="";//agregado por paco
        
        String arrayCad [] = cadena.split("___");
        opciones = Integer.parseInt(arrayCad [0]);
        remision = "%"+arrayCad [1]+"%";
        cliente = "%"+arrayCad [2]+"%";
        fecha_inicial = arrayCad [3];
        fecha_final = arrayCad [4];
        estatus = arrayCad [5];//agregado por paco
        
        //decodificar id de usuario
        Integer id_usuario = Integer.parseInt(Base64Coder.decodeString(id_user));
        userDat = this.getHomeDao().getUserById(id_usuario);
        Integer id_empresa = Integer.parseInt(userDat.get("empresa_id"));
        String rfc=this.getGralDao().getRfcEmpresaEmisora(id_empresa);
        
        String razon_social_empresa = this.getGralDao().getRazonSocialEmpresaEmisora(id_empresa);
        
        //obtener el directorio temporal
        String dir_tmp = this.getGralDao().getTmpDir();
        
        
        String[] array_company = razon_social_empresa.split(" ");
        String company_name= array_company[0].toLowerCase();
        String ruta_imagen = this.getGralDao().getImagesDir() +"logo_"+ company_name +".png";
        
        
        File file_dir_tmp = new File(dir_tmp);
        System.out.println("Directorio temporal: "+file_dir_tmp.getCanonicalPath());
        
        String file_name = "REPREM_"+rfc+"_"+fecha_inicial+"_"+fecha_final+".pdf";
        
        //ruta de archivo de salida
        String fileout = file_dir_tmp +"/"+  file_name;
        
        //variable estatus agregado por paco
        //obtiene las remisiones del periodo indicado
        lista_remisiones = this.getFacdao().getDatosReporteRemision(opciones, remision, cliente, fecha_inicial, fecha_final, id_empresa, Integer.parseInt(estatus));
        
        //instancia a la clase que construye el pdf del reporte de facturas
        PdfReporteRemisiones x = new PdfReporteRemisiones( fileout,ruta_imagen,razon_social_empresa,fecha_inicial,fecha_final,lista_remisiones);
        
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
            Logger.getLogger(RepRemisionController.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    } 
    
    
    
    //Buscador de clientes
    @RequestMapping(method = RequestMethod.POST, value="/get_buscador_clientes.json")
    public @ResponseBody HashMap<String,ArrayList<HashMap<String, String>>> get_buscador_clientesJson(
            @RequestParam(value="cadena", required=true) String cadena,
            @RequestParam(value="filtro", required=true) Integer filtro,
            @RequestParam(value="iu", required=true) String id_user,
            Model model
            ) {
        
        HashMap<String,ArrayList<HashMap<String, String>>> jsonretorno = new HashMap<String,ArrayList<HashMap<String, String>>>();
        HashMap<String, String> userDat = new HashMap<String, String>();
        
        //decodificar id de usuario
        Integer id_usuario = Integer.parseInt(Base64Coder.decodeString(id_user));
        //System.out.println("id_usuario: "+id_usuario);
        
        userDat = this.getHomeDao().getUserById(id_usuario);
        
        Integer id_empresa = Integer.parseInt(userDat.get("empresa_id"));
        Integer id_sucursal = Integer.parseInt(userDat.get("sucursal_id"));
        
        jsonretorno.put("Clientes", this.getFacdao().getBuscadorClientes(cadena,filtro,id_empresa, id_sucursal));
        
        return jsonretorno;
    }
}
