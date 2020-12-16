package com.agnux.kemikal.controllers;

import com.agnux.cfd.v2.Base64Coder;
import com.agnux.common.obj.ResourceProject;
import com.agnux.common.obj.UserSessionData;
import com.agnux.kemikal.interfacedaos.CtbInterfaceDao;
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
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;


@Controller
@SessionAttributes({"user"})
@RequestMapping("/ctbgenerapolizas/")
public class CtbGeneraPolizasController {
    ResourceProject resource = new ResourceProject();
    private static final Logger log  = Logger.getLogger(CtbGeneraPolizasController.class.getName());
    
    @Autowired
    @Qualifier("daoCtb")
    private CtbInterfaceDao ctbDao;
    
    public CtbInterfaceDao getCtbDao() {
        return ctbDao;
    }
    
    @Autowired
    @Qualifier("daoHome")
    private HomeInterfaceDao HomeDao;
    
    public HomeInterfaceDao getHomeDao() {
        return HomeDao;
    }
    
    @RequestMapping(value="/startup.agnux")
    public ModelAndView startUp(HttpServletRequest request, HttpServletResponse response, 
            @ModelAttribute("user") UserSessionData user)
            throws ServletException, IOException {
        log.log(Level.INFO, "Ejecutando starUp de {0}", CtbGeneraPolizasController.class.getName());
        LinkedHashMap<String,String> infoConstruccionTabla = new LinkedHashMap<String,String>();
        /*
        infoConstruccionTabla.put("id", "Acciones:90");
        infoConstruccionTabla.put("folio", "Folio:80");
        infoConstruccionTabla.put("nombre","Nombre:300");
        infoConstruccionTabla.put("tipo", "Tipo de P&oacute;liza:180");
        */
        
        ModelAndView x = new ModelAndView("ctbgenerapolizas/startup", "title", "Generaci&oacute;n de P&oacute;lizas Contables");
        
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
    
    
    @RequestMapping(method = RequestMethod.POST, value="/getData.json")
    public @ResponseBody HashMap<String,ArrayList<HashMap<String, Object>>> getDataJson(
            @RequestParam(value="id", required=true) Integer id,
            @RequestParam(value="iu", required=true) String id_user,
            Model model
        ) {
        
        log.log(Level.INFO, "Ejecutando getDataJson de {0}", CtbGeneraPolizasController.class.getName());
        HashMap<String,ArrayList<HashMap<String, Object>>> jsonretorno = new HashMap<String,ArrayList<HashMap<String, Object>>>();
        HashMap<String, String> userDat = new HashMap<String, String>();
        ArrayList<HashMap<String, Object>> arrayExtra = new ArrayList<HashMap<String, Object>>();
        HashMap<String, Object> extra = new HashMap<String, Object>();
        
        //Decodificar id de usuario
        Integer id_usuario = Integer.parseInt(Base64Coder.decodeString(id_user));
        userDat = this.getHomeDao().getUserById(id_usuario);
        Integer id_empresa = Integer.parseInt(userDat.get("empresa_id"));
        Integer id_sucursal = Integer.parseInt(userDat.get("sucursal_id"));
        
        //Esta variable indica si la empresa incluye modulo de Contabilidad
        extra.put("nivel_cta", userDat.get("nivel_cta"));
        arrayExtra.add(0,extra);
        
        jsonretorno.put("Extras", arrayExtra);
        jsonretorno.put("TM", this.getCtbDao().getPolizasContables_TiposDeMovimiento(id_empresa));
        //Se le pasa como parámetro 2 para indicar que solo debe tomar las aplicaciones que se deben mostrar en el Programa de Definicion de Asientos
        //jsonretorno.put("App", this.getCtbDao().getCtb_Aplicaciones(2));
        
        return jsonretorno;
    }
    
    
    
    
    //Metodo para Buscar movimientos para contabilizar
    @RequestMapping(method = RequestMethod.POST, value="/getBusqueda.json")
    public @ResponseBody HashMap<String,Object> getBusquedaJson(
            @RequestParam(value="fecha_ini", required=true) String fecha_ini,
            @RequestParam(value="fecha_fin", required=true) String fecha_fin,
            @RequestParam(value="tipo_mov", required=false) String tipo_mov,
            @RequestParam(value="iu", required=true) String id_user,
            Model model
        ) {
        
        log.log(Level.INFO, "Ejecutando getBusquedaJson de {0}", CtbGeneraPolizasController.class.getName());
        HashMap<String,Object> jsonretorno = new HashMap<String,Object>();
        HashMap<String, String> userDat = new HashMap<String, String>();
        
        //Decodificar id de usuario
        Integer id_usuario = Integer.parseInt(Base64Coder.decodeString(id_user));
        //userDat = this.getHomeDao().getUserById(id_usuario);
        //Integer id_empresa = Integer.parseInt(userDat.get("empresa_id"));
        
        //Aplicativo Generacion de Polizas Contables(CTB)
        Integer app_selected = 206;
        String command_selected="busqueda";
        
        String data_string = app_selected+"___"+id_usuario+"___"+command_selected+"___"+fecha_ini+"___"+fecha_fin+"___"+tipo_mov;
        
        jsonretorno.put("Data", this.getCtbDao().getCtbGeneraPolizas_busquedaDatos(data_string));
        
        return jsonretorno;
    }
    
    
    
    
    //Crear y editar
    @RequestMapping(method = RequestMethod.POST, value="/edit.json")
    public @ResponseBody HashMap<String, String> editJson(
            @RequestParam(value="identificador", required=true) String identificador,
            @RequestParam(value="fecha_ini", required=true) String fecha_ini,
            @RequestParam(value="fecha_fin", required=true) String fecha_fin,
            @RequestParam(value="select_tipo_mov", required=true) String select_tipo_mov,
            Model model,@ModelAttribute("user") UserSessionData user
        ) {
        
        HashMap<String, String> jsonretorno = new HashMap<String, String>();
        HashMap<String, String> succes = new HashMap<String, String>();
        
        //Aplicativo Generacion de Polizas Contables(CTB)
        Integer app_selected = 206;
        String command_selected = "new";
        Integer id_usuario= user.getUserId();//variable para el id  del usuario
        String extra_data_array = "'sin_datos'";
        String actualizo = "0";
        
        
        if( identificador.equals("0") ){
            command_selected = "new";
        }else{
            command_selected = "edit";
        }
        
        String data_string = app_selected+"___"+command_selected+"___"+id_usuario+"___"+identificador+"___"+fecha_ini+"___"+fecha_fin+"___"+select_tipo_mov;
        
        succes = this.getCtbDao().selectFunctionValidateAaplicativo(data_string,app_selected,extra_data_array);
        
        log.log(Level.INFO, "despues de validacion {0}", String.valueOf(succes.get("success")));
        if( String.valueOf(succes.get("success")).equals("true") ){
            actualizo = this.getCtbDao().selectFunctionForCtbAdmProcesos(data_string, extra_data_array);
        }
        
        jsonretorno.put("success",String.valueOf(succes.get("success")));
        
        log.log(Level.INFO, "Salida json {0}", String.valueOf(jsonretorno.get("success")));
        return jsonretorno;
    }
}
