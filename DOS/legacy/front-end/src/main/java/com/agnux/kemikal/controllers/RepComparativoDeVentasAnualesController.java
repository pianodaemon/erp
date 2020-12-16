/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.agnux.kemikal.controllers;

import com.agnux.cfd.v2.Base64Coder;
import com.agnux.common.helpers.TimeHelper;
import com.agnux.common.obj.ResourceProject;
import com.agnux.common.obj.UserSessionData;
import com.agnux.kemikal.interfacedaos.CxcInterfaceDao;
import com.agnux.kemikal.interfacedaos.FacturasInterfaceDao;
import com.agnux.kemikal.interfacedaos.GralInterfaceDao;
import com.agnux.kemikal.interfacedaos.HomeInterfaceDao;
import java.io.IOException;
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
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

/**
 *
 * @author Federico Mtz
 * 
 */
@Controller
@SessionAttributes({"user"})
@RequestMapping("/repcomparativoasventasanuales/")
public class RepComparativoDeVentasAnualesController {
    
     private static final Logger log  = Logger.getLogger(RepComparativoDeVentasAnualesController.class.getName());
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
        
        log.log(Level.INFO, "Ejecutando starUp de {0}", RepComparativoDeVentasAnualesController.class.getName());
        LinkedHashMap<String,String> infoConstruccionTabla = new LinkedHashMap<String,String>();
        
        
        ModelAndView x = new ModelAndView("repcomparativoasventasanuales/startup", "title", "Reporte Comparativo de Ventas Anuales por Cliente");
        
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
        mes.put("anio_inActual", TimeHelper.getFechaActualY());
        mesActual.add(0, mes);
        
        jsonretorno.put("Anios", this.getCxcDao().getCxc_AnioReporteSaldoMensual());
        jsonretorno.put("Dato", mesActual);
        return jsonretorno;
    }
   
    //Buscador de clientes
    @RequestMapping(method = RequestMethod.POST, value="/get_buscador_clientes.json")
    public @ResponseBody HashMap<String,ArrayList<HashMap<String, Object>>> get_buscador_clientesJson(
            @RequestParam(value="cadena", required=true) String cadena,
            @RequestParam(value="filtro", required=true) Integer filtro,
            @RequestParam(value="iu", required=true) String id_user,
            Model model
            ) {
        
        HashMap<String,ArrayList<HashMap<String, Object>>> jsonretorno = new HashMap<String,ArrayList<HashMap<String, Object>>>();
        HashMap<String, String> userDat = new HashMap<String, String>();
        
        //decodificar id de usuario
        Integer id_usuario = Integer.parseInt(Base64Coder.decodeString(id_user));
        //System.out.println("id_usuario: "+id_usuario);
        
        userDat = this.getHomeDao().getUserById(id_usuario);
        
        Integer id_empresa = Integer.parseInt(userDat.get("empresa_id"));
        Integer id_sucursal = Integer.parseInt(userDat.get("sucursal_id"));
        
        jsonretorno.put("Clientes", this.getCxcDao().getBuscadorClientes(cadena,filtro,id_empresa, id_sucursal));
        
        return jsonretorno;
    }
    
    

    //Obtiene datos para el reporte comparativo de Ventas por Cliente
    @RequestMapping(method = RequestMethod.POST, value="/getComparativo.json")
    public  @ResponseBody HashMap<String,ArrayList<HashMap<String, String>>> getComparativoAnualVentasJson(
            @RequestParam("anio_in") Integer anio_inicial,
            @RequestParam("anio_fin") Integer anio_final,
            @RequestParam(value="razon_cli") String cliente,
            @RequestParam(value="iu", required=true) String id_user,
            Model model
        ) {
        
        log.log(Level.INFO, "Ejecutando getComparativoVentas de {0}", RepComparativoDeVentasAnualesController.class.getName());
        HashMap<String,ArrayList<HashMap<String, String>>> jsonretorno = new HashMap<String,ArrayList<HashMap<String, String>>>();
        ArrayList<HashMap<String, String>> Comparativo = new ArrayList<HashMap<String, String>>();
        ArrayList<HashMap<String, String>>  totales = new ArrayList<HashMap<String, String>>();
        LinkedHashMap<String,String> total = new LinkedHashMap<String,String>();        
        HashMap<String, String> userDat = new HashMap<String, String>();
        Double suma_total_mes = 0.0;
        Double suma_total_mes2 = 0.0;
        Double suma_pesos_impuesto = 0.0;
        Double suma_pesos_total = 0.0;
        Double suma_dolares_subtotal = 0.0;
        Double suma_dolares_impuesto = 0.0;
        Double suma_dolares_total = 0.0;
        
        Double suma_subtotal_mn = 0.0;
        Double suma_impuesto_mn = 0.0;
        Double suma_total_mn = 0.0;
        
        //app=168 Reporte Comparativo de Ventas por Cliente(CXC)
                
        //decodificar id de usuario
        Integer id_usuario = Integer.parseInt(Base64Coder.decodeString(id_user));
        userDat = this.getHomeDao().getUserById(id_usuario);
        
        Integer id_empresa = Integer.parseInt(userDat.get("empresa_id"));
        
        Comparativo = this.getCxcDao().getComparativoVentas(cliente,anio_inicial,anio_final,id_empresa);
        

        for (int x=0; x<=Comparativo.size()-1;x++){
            HashMap<String,String> registro = Comparativo.get(x);
            //sumar cantidades
            //suma_total_mes +=Double.parseDouble(registro.get("sumameses"));
            //suma_total_mes2 +=Double.parseDouble(registro.get("sub_total"));
        }
        
      //total.put("", StringHelper.roundDouble(suma_total_mes,2));
       //total.put("", StringHelper.roundDouble(suma_total_mes2,2));
       totales.add(total);
        
        jsonretorno.put("Comparativo", Comparativo);
        jsonretorno.put("Totales", totales);
        
        return jsonretorno;
    }
    
    
}
