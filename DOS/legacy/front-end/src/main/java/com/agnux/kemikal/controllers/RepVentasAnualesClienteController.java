/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.agnux.kemikal.controllers;

import com.agnux.cfd.v2.Base64Coder;
import com.agnux.common.helpers.FileHelper;
import com.agnux.common.helpers.StringHelper;
import com.agnux.common.helpers.TimeHelper;
import com.agnux.common.obj.ResourceProject;
import com.agnux.common.obj.UserSessionData;
import com.agnux.kemikal.interfacedaos.CxcInterfaceDao;
import com.agnux.kemikal.interfacedaos.FacturasInterfaceDao;
import com.agnux.kemikal.interfacedaos.GralInterfaceDao;
import com.agnux.kemikal.interfacedaos.HomeInterfaceDao;
import com.agnux.kemikal.reportes.ComRepDiasEntregaPromedioXls;
import com.agnux.kemikal.reportes.CxcRepVentasAnualesClientesXls;
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
 * @author agnux
 */
@Controller
@SessionAttributes({"user"})
@RequestMapping("/repventasanualescliente/")
public class RepVentasAnualesClienteController {
    
    private static final Logger log  = Logger.getLogger(RepVentasAnualesClienteController.class.getName());
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
        
        log.log(Level.INFO, "Ejecutando starUp de {0}", RepVentasAnualesClienteController.class.getName());
        LinkedHashMap<String,String> infoConstruccionTabla = new LinkedHashMap<String,String>();
        
        
        ModelAndView x = new ModelAndView("repventasanualescliente/startup", "title", "Reporte de Ventas Anual por Cliente");
        
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
    @RequestMapping(method = RequestMethod.POST, value="/getVentasAnualesClientes.json")
    public  @ResponseBody HashMap<String,ArrayList<HashMap<String, String>>> getEstadisticaAnualVentasJson(
            @RequestParam("anio") Integer anio,
            @RequestParam(value="iu", required=true) String id_user,
            Model model
            ) {
        
        log.log(Level.INFO, "Ejecutando getVentasAnualesClientes de {0}", RepVentasAnualesClienteController.class.getName());
        HashMap<String,ArrayList<HashMap<String, String>>> jsonretorno = new HashMap<String,ArrayList<HashMap<String, String>>>();
        ArrayList<HashMap<String, String>> VentasAnualesClientes = new ArrayList<HashMap<String, String>>();
        ArrayList<HashMap<String, String>>  totales = new ArrayList<HashMap<String, String>>();
        LinkedHashMap<String,String> total = new LinkedHashMap<String,String>();        
        HashMap<String, String> userDat = new HashMap<String, String>();
       
        
                
        //decodificar id de usuario
        Integer id_usuario = Integer.parseInt(Base64Coder.decodeString(id_user));
        userDat = this.getHomeDao().getUserById(id_usuario);
        
        Integer id_empresa = Integer.parseInt(userDat.get("empresa_id"));
        
        VentasAnualesClientes = this.getCxcDao().getVentasAnualesCliente(anio,id_empresa);
   
        jsonretorno.put("VentasAnualesClientes", VentasAnualesClientes);
       // jsonretorno.put("Totales", totales);
        
        return jsonretorno;
    }

    
      //Genera Reporte de Ventas anuales por Cliente
    @RequestMapping(value = "/get_genera_reporte_ventasanualesclientes/{cadena}/{iu}/out.json", method = RequestMethod.GET ) 
    public ModelAndView getReporteJson(
                @PathVariable("cadena") String cadena,
                @PathVariable("iu") String id_user_cod,
                HttpServletRequest request,
                HttpServletResponse response, 
                Model model)
            throws ServletException, IOException, URISyntaxException, DocumentException, Exception {
        
        HashMap<String, String> userDat = new HashMap<String, String>();
        
        String tituloReporte="Reporte Ventas Anuales por Cliente.";
        System.out.println("Generando reporte de "+tituloReporte);
        
        //decodificar id de usuario
        Integer id_usuario = Integer.parseInt(Base64Coder.decodeString(id_user_cod));
        //System.out.println("id_usuario: "+id_usuario);
        
        userDat = this.getHomeDao().getUserById(id_usuario);
        Integer id_empresa = Integer.parseInt(userDat.get("empresa_id"));
        
        String razon_social_empresa = this.getGralDao().getRazonSocialEmpresaEmisora(id_empresa);

        Integer anio=0;
        String arrayCad [] = cadena.split("___");;
        anio = Integer.parseInt(arrayCad [0]);   
        String periodo = "Periodo Anual "+anio;
        
        
        //obtener el directorio temporal
        //String dir_tmp = System.getProperty("java.io.tmpdir");
        String dir_tmp = this.getGralDao().getTmpDir();
        
        
        File file_dir_tmp = new File(dir_tmp);
        //System.out.println("Directorio temporal: "+file_dir_tmp.getCanonicalPath());
        
        String file_name = "RepVentasAnualesClientes"+"_"+anio+".xls";
        //ruta de archivo de salida
        String fileout = file_dir_tmp +"/"+  file_name;
        
        ArrayList<HashMap<String, String>> ventasanuales = new ArrayList<HashMap<String, String>>();
        
        //obtiene facturas en pesos. Moneda 1
        ventasanuales = this.getCxcDao().getVentasAnualesCliente(anio, id_empresa);

        
        //Instancia de la clase que construye el xls
        CxcRepVentasAnualesClientesXls excel = new CxcRepVentasAnualesClientesXls(fileout,tituloReporte,razon_social_empresa,periodo,ventasanuales);

        
        System.out.println("Recuperando archivo: " + fileout);
        File file = new File(fileout);
        int size = (int) file.length(); // Tamaño del archivo
        BufferedInputStream bis = new BufferedInputStream(new FileInputStream(file));
        response.setBufferSize(size);
        response.setContentLength(size);
        response.setContentType("application/xls");
        response.setHeader("Content-Disposition","attachment; filename=\"" + file.getName() +"\"");
        FileCopyUtils.copy(bis, response.getOutputStream());  	
        response.flushBuffer();
        
        FileHelper.delete(fileout);
        
        return null;
    }
    
}
