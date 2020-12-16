/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.agnux.kemikal.controllers;

import com.agnux.cfd.v2.Base64Coder;
import com.agnux.common.helpers.StringHelper;
import com.agnux.common.helpers.TimeHelper;
import com.agnux.common.obj.ResourceProject;
import com.agnux.common.obj.UserSessionData;
import com.agnux.kemikal.interfacedaos.CxcInterfaceDao;
import com.agnux.kemikal.interfacedaos.FacturasInterfaceDao;
import com.agnux.kemikal.interfacedaos.GralInterfaceDao;
import com.agnux.kemikal.interfacedaos.HomeInterfaceDao;
import com.agnux.kemikal.reportes.PdfEstadisticoVentasAnualesXCliente;
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
 *
 * @author luiscarrillo
 * @fecha  12/07/2012
 */

@Controller
@SessionAttributes({"user"})
@RequestMapping("/repestadisticasanualesventas/")
public class RepEstadisticasAnualesVentasController {
    
    private static final Logger log  = Logger.getLogger(RepEstadisticasAnualesVentasController.class.getName());
    ResourceProject resource = new ResourceProject();
    
    @Autowired
    @Qualifier("daoCxc")
    private CxcInterfaceDao cxcDao;
    
    @Autowired
    @Qualifier("daoFacturas")
    private FacturasInterfaceDao facdao;
        
    
    @Autowired
    @Qualifier("daoHome")
    private HomeInterfaceDao HomeDao;
    
    @Autowired
    @Qualifier("daoGral")
    private GralInterfaceDao gralDao;
    
    
    public CxcInterfaceDao getCxcDao() {
        return (CxcInterfaceDao) cxcDao;
    }
    
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
        
        log.log(Level.INFO, "Ejecutando starUp de {0}", RepEstadisticasAnualesVentasController.class.getName());
        LinkedHashMap<String,String> infoConstruccionTabla = new LinkedHashMap<String,String>();
        
        
        ModelAndView x = new ModelAndView("repestadisticasanualesventas/startup", "title", "EstadisticasAnualVentas");
        
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
    
    
    /*
    //cargar tipos de productos
   @RequestMapping(method = RequestMethod.POST, value="/getMesActual.json")
        public @ResponseBody HashMap<String, String> getMesActualJson(
        @RequestParam(value="iu", required=true) String id_user,
        Model model
    ){
        HashMap<String, String> jsonretorno = new HashMap<String, String>();
        
        jsonretorno.put("mesActual", TimeHelper.getMesActual());
        return jsonretorno;
    }
    */
    
    //Cargar cargar datos iniciales para el buscador
   @RequestMapping(method = RequestMethod.POST, value="/getDatos.json")
        public @ResponseBody HashMap<String,ArrayList<HashMap<String, Object>>> getDatosJson(
        @RequestParam(value="iu", required=true) String id_user,
        Model model
    ){
        HashMap<String,ArrayList<HashMap<String, Object>>> jsonretorno = new HashMap<String,ArrayList<HashMap<String, Object>>>();
        HashMap<String, Object> mes = new HashMap<String, Object>();
        ArrayList<HashMap<String, Object>> mesActual = new ArrayList<HashMap<String, Object>>();
        
        //decodificar id de usuario
        Integer id_usuario = Integer.parseInt(Base64Coder.decodeString(id_user));
        
        mes.put("mesActual", TimeHelper.getMesActual());
        mes.put("anioActual", TimeHelper.getFechaActualY());
        mesActual.add(0, mes);
        
        jsonretorno.put("Anios", this.getCxcDao().getCxc_AnioReporteSaldoMensual());
        jsonretorno.put("Dato", mesActual);
        return jsonretorno;
    }
    
    

    //obtiene la estadistica de ventas en un periodo espesifico
    @RequestMapping(method = RequestMethod.POST, value="/getEstadisticas.json")
    public  @ResponseBody HashMap<String,ArrayList<HashMap<String, String>>> getEstadisticaAnualVentasJson(
            @RequestParam("mes_in") Integer mes_in,
            @RequestParam("mes_fin") Integer mes_fin,
            @RequestParam("anio") Integer anio,
            @RequestParam(value="iu", required=true) String id_user,
            Model model
            ) {
        
        log.log(Level.INFO, "Ejecutando getEstadisticaVentas de {0}", RepEstadisticasAnualesVentasController.class.getName());
        HashMap<String,ArrayList<HashMap<String, String>>> jsonretorno = new HashMap<String,ArrayList<HashMap<String, String>>>();
        ArrayList<HashMap<String, String>> Estadisticas = new ArrayList<HashMap<String, String>>();
        ArrayList<HashMap<String, String>>  totales = new ArrayList<HashMap<String, String>>();
        LinkedHashMap<String,String> total = new LinkedHashMap<String,String>();        
        HashMap<String, String> userDat = new HashMap<String, String>();
        Double suma_total_mes = 0.0;
        Double suma_pesos_impuesto = 0.0;
        Double suma_pesos_total = 0.0;
        Double suma_dolares_subtotal = 0.0;
        Double suma_dolares_impuesto = 0.0;
        Double suma_dolares_total = 0.0;
        
        Double suma_subtotal_mn = 0.0;
        Double suma_impuesto_mn = 0.0;
        Double suma_total_mn = 0.0;
        
        
                
        //decodificar id de usuario
        Integer id_usuario = Integer.parseInt(Base64Coder.decodeString(id_user));
        userDat = this.getHomeDao().getUserById(id_usuario);
        
        Integer id_empresa = Integer.parseInt(userDat.get("empresa_id"));
        
        Estadisticas = this.getCxcDao().getEstadisticaVentas(mes_in,mes_fin,anio,id_empresa);
        

        for (int x=0; x<=Estadisticas.size()-1;x++){
            HashMap<String,String> registro = Estadisticas.get(x);
            //sumar cantidades
            suma_total_mes +=Double.parseDouble(registro.get("suma_total"));
        }
        
        total.put("", StringHelper.roundDouble(suma_total_mes,2));
        totales.add(total);
        
        jsonretorno.put("Estadisticas", Estadisticas);
        jsonretorno.put("Totales", totales);
        
        return jsonretorno;
    }
    
    
    
    
    
    
    
    //Genera pdf de facturacion de
    @RequestMapping(value = "/get_genera_reporte_estadistica/{cadena}/{iu}/out.json", method = RequestMethod.GET ) 
    public ModelAndView getGeneraPdfFacturacionJson(
                @PathVariable("cadena") String cadena,
                @PathVariable("iu") String id_user,
                HttpServletRequest request, 
                HttpServletResponse response, 
                Model model)
            throws ServletException, IOException, URISyntaxException, DocumentException {
        
        HashMap<String, String> userDat = new HashMap<String, String>();
        
        
        System.out.println("Generando reporte Estadistico de Ventas Anuales por Cliente");
        
        //Integer opciones=0;
        //String factura="";
        //String cliente="";
        String mes_inicial="";
        String mes_final="";
        Integer anio=0;
        
        String arrayCad [] = cadena.split("___");
        //opciones = Integer.parseInt(arrayCad [0]);
        //factura = "%"+arrayCad [1]+"%";
        //cliente = "%"+arrayCad [2]+"%";
        mes_inicial = arrayCad [0];
        mes_final = arrayCad [1];
        anio = Integer.parseInt(arrayCad [2]);
        Integer mesInicial = Integer.parseInt(mes_inicial);
        Integer mesFinal = Integer.parseInt(mes_final);
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
        String ruta_imagen = "";
        
        
        File file_dir_tmp = new File(dir_tmp);
        System.out.println("Directorio temporal: "+file_dir_tmp.getCanonicalPath());
        
        
        String file_name = "RepEstAnualCliente"+mes_inicial+"_"+mes_final+".pdf";
        //ruta de archivo de salida
        String fileout = file_dir_tmp +"/"+  file_name;
        //String fileout = "C:/Users/designer/Documents/"+file_name;
        
        ArrayList<HashMap<String, String>> lista_ventas_anuales = new ArrayList<HashMap<String, String>>();
        
        //obtiene las facturas del periodo indicado
        lista_ventas_anuales = this.getCxcDao().getEstadisticaVentas(mesInicial, mesFinal, anio, id_empresa);
        
        //instancia a la clase que construye el pdf del reporte de facturas
        PdfEstadisticoVentasAnualesXCliente x = new PdfEstadisticoVentasAnualesXCliente(fileout,ruta_imagen,razon_social_empresa,mes_inicial,mes_final,lista_ventas_anuales);
        
        
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
