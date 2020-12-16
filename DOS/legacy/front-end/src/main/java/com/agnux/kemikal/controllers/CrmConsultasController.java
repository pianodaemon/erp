package com.agnux.kemikal.controllers;

import com.agnux.cfd.v2.Base64Coder;
import com.agnux.common.helpers.StringHelper;
import com.agnux.common.obj.ResourceProject;
import com.agnux.common.obj.UserSessionData;
import com.agnux.kemikal.interfacedaos.CrmInterfaceDao;
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
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.servlet.ModelAndView;


@Controller
@SessionAttributes({"user"})
@RequestMapping("/crmconsultas/")
public class CrmConsultasController {
    ResourceProject resource = new ResourceProject();
    private static final Logger log  = Logger.getLogger(CrmConsultasController.class.getName());
    
    @Autowired
    @Qualifier("daoCrm")
    private CrmInterfaceDao CrmlDao;
    
    @Autowired
    @Qualifier("daoHome")   //permite controlar usuarios que entren
    private HomeInterfaceDao HomeDao;
    
    
    public CrmInterfaceDao getCrmDao() {
        return CrmlDao;
    }
    
    public HomeInterfaceDao getHomeDao() {
        return HomeDao;
    }
    
    
    @RequestMapping(value="/startup.agnux")
    public ModelAndView startUp(HttpServletRequest request, HttpServletResponse response, 
            @ModelAttribute("user") UserSessionData user
            )throws ServletException, IOException {
        
        log.log(Level.INFO, "Ejecutando starUp de {0}", CrmConsultasController.class.getName());
        /*LinkedHashMap<String,String> infoConstruccionTabla = new LinkedHashMap<String,String>();
        infoConstruccionTabla.put("id", "Acciones:70");
        infoConstruccionTabla.put("folio", "Folio:100");
        infoConstruccionTabla.put("agente","Agente:300");
        infoConstruccionTabla.put("mes","Mes:100");
        */
        
        ModelAndView x = new ModelAndView("crmconsultas/startup", "title","Consultas");
        x = x.addObject("layoutheader", resource.getLayoutheader());
        x = x.addObject("layoutmenu", resource.getLayoutmenu());
        x = x.addObject("layoutfooter", resource.getLayoutfooter());
        //x = x.addObject("grid", resource.generaGrid(infoConstruccionTabla));
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
    //getLlamadas
    
    @RequestMapping(method = RequestMethod.POST,value="/getDatos.json")
    public @ResponseBody HashMap<String,ArrayList<HashMap<String, String>>> getRegistroJson(
            //@RequestParam(value="id", required=true) Integer id,
            @RequestParam(value="iu", required=true) String id_user,
            Model model
            ) {
        
        log.log(Level.INFO, "Ejecutando getclientsdfJson de {0}", CrmConsultasController.class.getName());
        HashMap<String,ArrayList<HashMap<String, String>>> jsonretorno = new HashMap<String,ArrayList<HashMap<String, String>>>();
        HashMap<String, String> userDat = new HashMap<String, String>();
        //ArrayList<HashMap<String, String>> datos = new ArrayList<HashMap<String, String>>();
        ArrayList<HashMap<String, String>> agentes = new ArrayList<HashMap<String, String>>();
        
        
        
        //decodificar id de usuario
        Integer id_usuario = Integer.parseInt(Base64Coder.decodeString(id_user));
        userDat = this.getHomeDao().getUserById(id_usuario);
        Integer id_empresa = Integer.parseInt(userDat.get("empresa_id"));
        Integer id_agente = Integer.parseInt(userDat.get("empleado_id"));
        
       
        
        agentes = this.getCrmDao().getAgentes(id_empresa);
        
        
        
        jsonretorno.put("Agentes", agentes);
        
        
        return jsonretorno;

        
    }
    
    
    
     //obtiene los Datos para el buscador
    @RequestMapping(method = RequestMethod.POST, value="/get_DatosBuscador.json")
    public @ResponseBody HashMap<String,ArrayList<HashMap<String, String>>> getBuscadorJson(
            @RequestParam(value="agente", required=true) Integer agente,
            @RequestParam(value="tipo_seleccion",required=true)Integer tipo_seleccion,
            @RequestParam(value="status",required=true)Integer status,
            @RequestParam(value="etapa",required=true)Integer etapa,
            @RequestParam(value="fecha_inicial", required=true) String fecha_inicial,
            @RequestParam(value="fecha_final", required=true) String fecha_final,
            @RequestParam(value="id",required=true)Integer id,
            @RequestParam(value="iu", required=true) String id_user,
            Model model
            ) {
        
        log.log(Level.INFO, "Ejecutando getBuscadorJson de {0}", CrmConsultasController.class.getName());
        HashMap<String,ArrayList<HashMap<String, String>>> jsonretorno = new HashMap<String,ArrayList<HashMap<String, String>>>();
        ArrayList<HashMap<String, String>> registros = new ArrayList<HashMap<String, String>>();
        ArrayList<HashMap<String,String>>visitas = new ArrayList<HashMap<String,String>>();
        ArrayList<HashMap<String,String>>casos = new ArrayList<HashMap<String,String>>();
        ArrayList<HashMap<String,String>>oportunidades = new ArrayList<HashMap<String,String>>();
        ArrayList<HashMap<String,String>>varios = new ArrayList<HashMap<String,String>>();
        
        HashMap<String, String> userDat = new HashMap<String, String>();
        //decodificar id de usuario
        userDat = this.getHomeDao().getUserById(Integer.parseInt(Base64Coder.decodeString(id_user)));
        Integer id_empresa = Integer.parseInt(userDat.get("empresa_id"));
        
        //Llamadas y Visitas
        if(id== 1){
            registros=this.getCrmDao().getBuscadorRegistros(id,agente,tipo_seleccion,status,etapa, fecha_inicial, fecha_final, id_empresa);

        }
        if(id ==2){
            visitas =this.getCrmDao().getBuscadorVisitas(id,agente,tipo_seleccion,status,etapa,fecha_inicial,fecha_final,id_empresa);

        }
        if(id==3){
            casos = this.getCrmDao().getBuscadorCasos(id,agente,tipo_seleccion,status,etapa,fecha_inicial,fecha_final,id_empresa);
        }
        if(id==4){
            oportunidades=this.getCrmDao().getBuscadorOportunidades(id,agente,tipo_seleccion,status,etapa,fecha_inicial,fecha_final,id_empresa);
        }
        if(id==5){
            varios = this.getCrmDao().getBuscadorVarios(id,agente,tipo_seleccion,status,etapa,fecha_inicial,fecha_final,id_empresa);
        }
        
        //registros = this.getCrmDao().getBuscadorRegistrosLlamadas(agente, fecha_inicial, fecha_final,id_empresa);
        
        jsonretorno.put("Registros", registros);
        jsonretorno.put("Visitas",visitas);
        jsonretorno.put("Casos", casos);
        jsonretorno.put("Oportunidades", oportunidades);
        jsonretorno.put("Varios", varios);
        
        return jsonretorno;
    }
    
}
