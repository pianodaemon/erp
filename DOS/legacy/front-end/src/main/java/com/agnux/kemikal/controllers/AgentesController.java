package com.agnux.kemikal.controllers;

import com.agnux.cfd.v2.Base64Coder;
import com.agnux.common.helpers.StringHelper;
import com.agnux.common.obj.DataPost;
import com.agnux.common.obj.ResourceProject;
import com.agnux.common.obj.UserSessionData;
import com.agnux.kemikal.interfacedaos.CxcInterfaceDao;
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
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttributes;


@Controller
@SessionAttributes({"user"})
@RequestMapping("/agentes/")
public class AgentesController {
    ResourceProject resource = new ResourceProject();
    private static final Logger log  = Logger.getLogger(CtbCentroCostosController.class.getName());
    
    
    @Autowired
    @Qualifier("daoCxc")
    private CxcInterfaceDao cxcDao;
    
    public CxcInterfaceDao getCxcDao() {
        return cxcDao;
    }
    
    @Autowired
    @Qualifier("daoHome")
    private HomeInterfaceDao HomeDao;
    
    public HomeInterfaceDao getHomeDao() {
        return HomeDao;
    }
    
    @RequestMapping(value="/startup.agnux")
    public ModelAndView startUp(HttpServletRequest request, HttpServletResponse response, 
            @ModelAttribute("user") UserSessionData user
            )throws ServletException, IOException {
        
        log.log(Level.INFO, "Ejecutando starUp de {0}", CtbCentroCostosController.class.getName());
        LinkedHashMap<String,String> infoConstruccionTabla = new LinkedHashMap<String,String>();
        
        infoConstruccionTabla.put("id", "Acciones:90");
        infoConstruccionTabla.put("usuario", "Usuario:130");
        infoConstruccionTabla.put("nombre", "Nombre:400");
        infoConstruccionTabla.put("region", "Region:120");
        
        ModelAndView x = new ModelAndView("agentes/startup", "title", "Cat&aacute;logo de Agentes de ventas");
        
        x = x.addObject("layoutheader", resource.getLayoutheader());
        x = x.addObject("layoutmenu", resource.getLayoutmenu());
        x = x.addObject("layoutfooter", resource.getLayoutfooter());
        x = x.addObject("grid", resource.generaGrid(infoConstruccionTabla));
        x = x.addObject("url", resource.getUrl(request));
        x = x.addObject("username", user.getUserName());
        x = x.addObject("empresa", user.getRazonSocialEmpresa());
        x = x.addObject("sucursal", user.getSucursal());
        
        String userId = String.valueOf(user.getUserId());
        
        //codificar id de usuario
        String codificado = Base64Coder.encodeString(userId);
       
        //id de usuario codificado
        x = x.addObject("iu", codificado);
        
        return x;
    }
    
    
    
    //obtiene los grupos para el buscador
    @RequestMapping(method = RequestMethod.POST, value="/getRegionesBuscador.json")
    public @ResponseBody HashMap<String,ArrayList<HashMap<String, String>>> getRegionesBuscadorJson(
            Model model
            ) {
        HashMap<String,ArrayList<HashMap<String, String>>> jsonretorno = new HashMap<String,ArrayList<HashMap<String, String>>>();
        ArrayList<HashMap<String, String>> regiones = new ArrayList<HashMap<String, String>>();
        
        regiones = this.getCxcDao().getAgente_Regiones();
        
        jsonretorno.put("Regiones", regiones);
        
        return jsonretorno;
    }
    
    @RequestMapping(value="/getAllAgentes.json", method = RequestMethod.POST)
    public @ResponseBody HashMap<String,ArrayList<HashMap<String, Object>>> getAllAgentesJson(
           @RequestParam(value="orderby", required=true) String orderby,
           @RequestParam(value="desc", required=true) String desc,
           @RequestParam(value="items_por_pag", required=true) int items_por_pag,
           @RequestParam(value="pag_start", required=true) int pag_start,
           @RequestParam(value="display_pag", required=true) String display_pag,
           @RequestParam(value="input_json", required=true) String input_json,
           @RequestParam(value="cadena_busqueda", required=true) String cadena_busqueda,
           @RequestParam(value="iu", required=true) String id_user_cod,
           Model modcel) {
           
        HashMap<String,ArrayList<HashMap<String, Object>>> jsonretorno = new HashMap<String,ArrayList<HashMap<String, Object>>>();
        HashMap<String,String> has_busqueda = StringHelper.convert2hash(StringHelper.ascii2string(cadena_busqueda));
        
        //aplicativo tipos de poliza
        Integer app_selected = 19;
        
        //decodificar id de usuario
        Integer id_usuario = Integer.parseInt(Base64Coder.decodeString(id_user_cod));
        
        //variables para el buscador
        String nombre = "%"+StringHelper.isNullString(String.valueOf(has_busqueda.get("nombre")))+"%";
        String usuario = "%"+StringHelper.isNullString(String.valueOf(has_busqueda.get("usuario")))+"%";
        String region = StringHelper.isNullString(String.valueOf(has_busqueda.get("region")));
        
        String data_string = app_selected+"___"+id_usuario+"___"+nombre+"___"+usuario+"___"+region;
        
        //obtiene total de registros en base de datos, con los parametros de busqueda
        int total_items = this.getCxcDao().countAll(data_string);
        
        //calcula el total de paginas
        int total_pags = resource.calculaTotalPag(total_items,items_por_pag);
        
        //variables que necesita el datagrid, para no tener que hacer uno por cada aplicativo
        DataPost dataforpos = new DataPost(orderby, desc, items_por_pag, pag_start, display_pag, input_json, cadena_busqueda,total_items,total_pags, id_user_cod);
        
        int offset = resource.__get_inicio_offset(items_por_pag, pag_start);
        
        //obtiene los registros para el grid, de acuerdo a los parametros de busqueda
        jsonretorno.put("Data", this.getCxcDao().getAgente_PaginaGrid(data_string, offset, items_por_pag, orderby, desc));
        //obtiene el hash para los datos que necesita el datagrid
        jsonretorno.put("DataForGrid", dataforpos.formaHashForPos(dataforpos));
        
        return jsonretorno;
    }
    

    @RequestMapping(method = RequestMethod.POST, value="/getAgente.json")
    public @ResponseBody HashMap<String,ArrayList<HashMap<String, String>>> getAgenteJson(
            @RequestParam(value="id", required=true) Integer id,
            @RequestParam(value="iu", required=true) String id_user_cod,
            Model model
            ) {
        
        log.log(Level.INFO, "Ejecutando getAgenteJson de {0}", AgentesController.class.getName());
        HashMap<String,ArrayList<HashMap<String, String>>> jsonretorno = new HashMap<String,ArrayList<HashMap<String, String>>>();
        HashMap<String, String> userDat = new HashMap<String, String>();
        ArrayList<HashMap<String, String>> usuariosDisponibles = new ArrayList<HashMap<String, String>>();
        ArrayList<HashMap<String, String>> datosAgente = new ArrayList<HashMap<String, String>>();
        ArrayList<HashMap<String, String>> regiones = new ArrayList<HashMap<String, String>>();
        
        
        //decodificar id de usuario
        Integer id_usuario = Integer.parseInt(Base64Coder.decodeString(id_user_cod));
        userDat = this.getHomeDao().getUserById(id_usuario);
        
        Integer id_empresa = Integer.parseInt(userDat.get("empresa_id"));
        Integer usuario_agente = 0;
        
        if( id != 0  ){
            datosAgente = this.getCxcDao().getAgente_Datos(id);
            usuario_agente = Integer.parseInt(datosAgente.get(0).get("gral_usr_id"));
        }
        
        usuariosDisponibles=this.getCxcDao().getUsuarios(id_empresa, usuario_agente);
        regiones = this.getCxcDao().getAgente_Regiones();
        
        jsonretorno.put("Agente", datosAgente);
        jsonretorno.put("Usuarios", usuariosDisponibles);
        jsonretorno.put("Regiones", regiones);
        
        return jsonretorno;
    }


    //crear y editar un Agente
    @RequestMapping(method = RequestMethod.POST, value="/edit.json")
    public @ResponseBody HashMap<String, String> editJson(
            @RequestParam(value="identificador", required=true) Integer id,
            @RequestParam(value="nombre", required=true) String nombre_agente,
            @RequestParam(value="select_usuario", required=true) Integer usuario_agente,
            @RequestParam(value="comision", required=true) String comision,
            @RequestParam(value="select_region", required=true) Integer region,
            Model model,@ModelAttribute("user") UserSessionData user
            ) {
        
        HashMap<String, String> jsonretorno = new HashMap<String, String>();
        HashMap<String, String> succes = new HashMap<String, String>();
        Integer app_selected = 19;//catalogo de agentes
        String command_selected = "new";
        Integer id_usuario= user.getUserId();//variable para el id  del usuario
        String extra_data_array = "'sin datos'";
        String actualizo = "0";
        
        if( id==0 ){
            command_selected = "new";
        }else{
            command_selected = "edit";
        }
        
        String data_string = app_selected+"___"+command_selected+"___"+id_usuario+"___"+id+"___"+nombre_agente.toUpperCase()+"___"+usuario_agente+"___"+comision+"___"+region;
        
        succes = this.getCxcDao().selectFunctionValidateAaplicativo(data_string,app_selected,extra_data_array);
        
        log.log(Level.INFO, "despues de validacion {0}", String.valueOf(succes.get("success")));
        if( String.valueOf(succes.get("success")).equals("true") ){
            actualizo = this.getCxcDao().selectFunctionForThisApp(data_string, extra_data_array);
        }
        
        jsonretorno.put("success",String.valueOf(succes.get("success")));
        
        log.log(Level.INFO, "Salida json {0}", String.valueOf(jsonretorno.get("success")));
        return jsonretorno;
    }

}
