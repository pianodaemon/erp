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

import com.agnux.kemikal.interfacedaos.GralInterfaceDao;
import com.agnux.kemikal.interfacedaos.HomeInterfaceDao;
import com.agnux.kemikal.reportes.PdfEstadisticoVentasAnualesXUnidades;
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
 * @author luis
 */
@Controller
@SessionAttributes({"user"})
@RequestMapping("/repestadisticaanualventasUM/")
public class RepEstadisticasAnualesVentasUnidadesController {
    private static final Logger log  = Logger.getLogger(RepEstadisticasAnualesVentasUnidadesController.class.getName());
    ResourceProject resource = new ResourceProject();
    
    @Autowired
    @Qualifier("daoCxc")
    private CxcInterfaceDao cxcDao;
        
    
    @Autowired
    @Qualifier("daoHome")
    private HomeInterfaceDao HomeDao;
    
    @Autowired
    @Qualifier("daoGral")
    private GralInterfaceDao gralDao;
    
    public CxcInterfaceDao getCxcDao() {
        return (CxcInterfaceDao) cxcDao;
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
        
        log.log(Level.INFO, "Ejecutando starUp de {0}", RepEstadisticasAnualesVentasUnidadesController.class.getName());
        LinkedHashMap<String,String> infoConstruccionTabla = new LinkedHashMap<String,String>();
        
        
        ModelAndView x = new ModelAndView("repestadisticaanualventasunidades/startup", "title", "EstadisticasAnualVentasUnidades");
        
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
   @RequestMapping(method = RequestMethod.POST, value="/getProdTipos.json")
        public @ResponseBody HashMap<String,ArrayList<HashMap<String, String>>> getProdTiposJson(
        @RequestParam(value="iu", required=true) String id_user,
        Model model
        ){
        HashMap<String,ArrayList<HashMap<String, String>>> jsonretorno = new HashMap<String,ArrayList<HashMap<String, String>>>();
        HashMap<String, String> mes = new HashMap<String, String>();
        ArrayList<HashMap<String, String>> mesActual = new ArrayList<HashMap<String, String>>();
        
        //decodificar id de usuario
        Integer id_usuario = Integer.parseInt(Base64Coder.decodeString(id_user));
        
        mes.put("mesActual", TimeHelper.getMesActual());
        mesActual.add(0, mes);
        
        jsonretorno.put("ProdTipo", this.getCxcDao().getProductoTipos());
        jsonretorno.put("Mes", mesActual);
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
        jsonretorno.put("ProdTipo", this.getCxcDao().getProductoTiposV2());
        jsonretorno.put("Dato", mesActual);
        return jsonretorno;
    }
    
   
   
    //obtiene las familias de acuerdo al tipo de producto
   @RequestMapping(method = RequestMethod.POST, value="/getFamilias.json")
        public @ResponseBody HashMap<String,ArrayList<HashMap<String, String>>> getFamiliasJson(
            @RequestParam(value="tipo", required=true) Integer tipo_producto,
            @RequestParam(value="iu", required=true) String id_user,
            Model model
        ){
        HashMap<String,ArrayList<HashMap<String, String>>> jsonretorno = new HashMap<String,ArrayList<HashMap<String, String>>>();
        HashMap<String, String> userDat = new HashMap<String, String>();
        
        //decodificar id de usuario
        Integer id_usuario = Integer.parseInt(Base64Coder.decodeString(id_user));
        userDat = this.getHomeDao().getUserById(id_usuario);
        Integer id_empresa = Integer.parseInt(userDat.get("empresa_id"));
    
        jsonretorno.put("Familias", this.getCxcDao().getFamilias(tipo_producto, id_empresa));
        return jsonretorno;
    }
   
   
   
    //obtiene las subfamilias de acuerdo a la Familia
   @RequestMapping(method = RequestMethod.POST, value="/getSubFamilias.json")
        public @ResponseBody HashMap<String,ArrayList<HashMap<String, String>>> getSubFamiliasJson(
            @RequestParam(value="familia_id", required=true) Integer familia_id,
            Model model
        ){
        HashMap<String,ArrayList<HashMap<String, String>>> jsonretorno = new HashMap<String,ArrayList<HashMap<String, String>>>();
    
        jsonretorno.put("SubFamilias", this.getCxcDao().getSubFamilias(familia_id));
        return jsonretorno;
    }
    
    //obtiene la estadistica de ventas en un periodo espesifico
    @RequestMapping(method = RequestMethod.POST, value="/getEstadisticas.json")
    public  @ResponseBody HashMap<String,ArrayList<HashMap<String, String>>> RepEstadisticasAnualesVentasProductoControllerJson(
            @RequestParam("mes_in") Integer mes_in,
            @RequestParam("mes_fin") Integer mes_fin,
            @RequestParam("familia_id")Integer familia,
            @RequestParam("subfamilia_id")Integer subfamilia,
            @RequestParam("tipo")Integer tipo_producto,
            @RequestParam("anio")Integer anio,
            @RequestParam(value="iu", required=true) String id_user,
            Model model
            ) {
        
        log.log(Level.INFO, "Ejecutando getEstadisticaVentasUnidades de {0}", RepEstadisticasAnualesVentasUnidadesController.class.getName());
        HashMap<String,ArrayList<HashMap<String, String>>> jsonretorno = new HashMap<String,ArrayList<HashMap<String, String>>>();
        ArrayList<HashMap<String, String>> Estadisticas = new ArrayList<HashMap<String, String>>();
        HashMap<String, String> userDat = new HashMap<String, String>();
        
        //decodificar id de usuario
        Integer id_usuario = Integer.parseInt(Base64Coder.decodeString(id_user));
        userDat = this.getHomeDao().getUserById(id_usuario);
        Integer id_empresa = Integer.parseInt(userDat.get("empresa_id"));
        
        Estadisticas = this.getCxcDao().getEstadisticaVentasUnidades(mes_in,mes_fin,tipo_producto,familia,subfamilia,id_empresa, anio);
        
        jsonretorno.put("Estadisticas", Estadisticas);
        
        return jsonretorno;
    }
    
//Genera pdf de facturacion de
    @RequestMapping(value = "/MakePDF/{cadena}/{iu}/out.json", method = RequestMethod.GET ) 
    public ModelAndView getMakePDFJson(
                @PathVariable("cadena") String cadena,
                @PathVariable("iu") String id_user,
                HttpServletRequest request, 
                HttpServletResponse response, 
                Model model)
            throws ServletException, IOException, URISyntaxException, DocumentException {
        
        HashMap<String, String> userDat = new HashMap<String, String>();
        String datitos [] = cadena.split("_");
        
        Integer mes_in = Integer.parseInt(datitos[0]);
        Integer mes_fin = Integer.parseInt(datitos[1]);
        Integer familia = Integer.parseInt(datitos[2]);
        Integer subfamilia = Integer.parseInt(datitos[3]);
        Integer tipo_producto = Integer.parseInt(datitos[4]);
        Integer anio = Integer.parseInt(datitos[5]);
        
        System.out.println("Generando reporte de facturacion");

        //decodificar id de usuario
        Integer id_usuario = Integer.parseInt(Base64Coder.decodeString(id_user));
        userDat = this.getHomeDao().getUserById(id_usuario);
        Integer id_empresa = Integer.parseInt(userDat.get("empresa_id"));
        
        String razon_social_empresa = this.getGralDao().getRazonSocialEmpresaEmisora(id_empresa);
        
        //obtener el directorio temporal
        String dir_tmp = this.getGralDao().getTmpDir();
        String[] array_company = razon_social_empresa.split(" ");
        String company_name= array_company[0].toLowerCase();
        String ruta_imagen = "";
        
        File file_dir_tmp = new File(dir_tmp);
        System.out.println("Directorio temporal: "+file_dir_tmp.getCanonicalPath());
        String mesInicial =TimeHelper.ConvertNumToMonth(mes_in);
        String mesFinal =TimeHelper.ConvertNumToMonth(mes_fin);
        
        String file_name = "RepEstAnualUnidades"+mesInicial+"_"+mesFinal+".pdf";
        //ruta de archivo de salida
        String fileout = file_dir_tmp +"/"+  file_name;
        
        ArrayList<HashMap<String, String>> lista_ventas_anuales = new ArrayList<HashMap<String, String>>();
        
        //obtiene las facturas del periodo indicado
        lista_ventas_anuales = this.getCxcDao().getEstadisticaVentasUnidades(mes_in,mes_fin,tipo_producto,familia,subfamilia,id_empresa, anio);
        
        //instancia a la clase que construye el pdf del reporte de facturas
        PdfEstadisticoVentasAnualesXUnidades x = new PdfEstadisticoVentasAnualesXUnidades(fileout,ruta_imagen,razon_social_empresa,mesInicial,mesFinal,lista_ventas_anuales);
        
        
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
