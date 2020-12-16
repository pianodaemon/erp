package com.agnux.kemikal.controllers;


import com.agnux.kemikal.interfacedaos.CtbInterfaceDao;

import com.agnux.cfd.v2.Base64Coder;
import com.agnux.common.helpers.StringHelper;
import com.agnux.common.obj.DataPost;
import com.agnux.common.obj.ResourceProject;
import com.agnux.common.obj.UserSessionData;
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
@RequestMapping("/tipopolizas/")
public class CtbTipopolizasController {
    ResourceProject resource = new ResourceProject();
    private static final Logger log  = Logger.getLogger(CtbTipopolizasController.class.getName());
    
    @Autowired
    @Qualifier("daoCtb")
    private CtbInterfaceDao ctbDao;
    
    public CtbInterfaceDao getCtbDao() {
        return ctbDao;
    }
    
    
    @RequestMapping(value="/startup.agnux")
    public ModelAndView startUp(HttpServletRequest request, HttpServletResponse response, 
            @ModelAttribute("user") UserSessionData user
            )throws ServletException, IOException {
        
        log.log(Level.INFO, "Ejecutando starUp de {0}", CtbTipopolizasController.class.getName());
        LinkedHashMap<String,String> infoConstruccionTabla = new LinkedHashMap<String,String>();
        
        infoConstruccionTabla.put("id", "Acciones:90");
        infoConstruccionTabla.put("tipo", "Tipo de poliza:110");
        infoConstruccionTabla.put("titulo", "Descripcion:300");
        infoConstruccionTabla.put("grupo", "Grupo:120");
        
        ModelAndView x = new ModelAndView("tipopolizas/startup", "title", "Tipos de Polizas");
        
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
       
        //decodificar id de usuario
        //String decodificado = Base64Coder.decodeString(codificado);
        
        //id de usuario codificado
        x = x.addObject("iu", codificado);
        
        return x;
    }
    
    
    
    
    @RequestMapping(value="/getAllTipoPolizas.json", method = RequestMethod.POST)
    public @ResponseBody HashMap<String,ArrayList<HashMap<String, Object>>> ggetAllTipoPolizasJson(
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
        Integer app_selected = 16;
        
        //decodificar id de usuario
        Integer id_usuario = Integer.parseInt(Base64Coder.decodeString(id_user_cod));
        
        //variables para el buscador
        String tipo = StringHelper.isNullString(String.valueOf(has_busqueda.get("tipo")));
        String descripcion = "%"+StringHelper.isNullString(String.valueOf(has_busqueda.get("descripcion")))+"%";
        String grupo = StringHelper.isNullString(String.valueOf(has_busqueda.get("grupo")));
        
        String data_string = app_selected+"___"+id_usuario+"___"+tipo+"___"+descripcion+"___"+grupo;
        
        //obtiene total de registros en base de datos, con los parametros de busqueda
        int total_items = this.getCtbDao().countAll(data_string);
        
        //calcula el total de paginas
        int total_pags = resource.calculaTotalPag(total_items,items_por_pag);
        
        //variables que necesita el datagrid, para no tener que hacer uno por cada aplicativo
        DataPost dataforpos = new DataPost(orderby, desc, items_por_pag, pag_start, display_pag, input_json, cadena_busqueda,total_items,total_pags, id_user_cod);
        
        int offset = resource.__get_inicio_offset(items_por_pag, pag_start);
        
        //obtiene los registros para el grid, de acuerdo a los parametros de busqueda
        jsonretorno.put("Data", this.getCtbDao().getTipoPolizas_PaginaGrid(data_string, offset, items_por_pag, orderby, desc));
        //obtiene el hash para los datos que necesita el datagrid
        jsonretorno.put("DataForGrid", dataforpos.formaHashForPos(dataforpos));
        
        return jsonretorno;
    }
    
    
    
    
    
    //obtiene los grupos para el buscador
    @RequestMapping(method = RequestMethod.POST, value="/getGruposBuscador.json")
    public @ResponseBody HashMap<String,ArrayList<HashMap<String, String>>> getGruposBuscadorJson(
            Model model
            ) {
        HashMap<String,ArrayList<HashMap<String, String>>> jsonretorno = new HashMap<String,ArrayList<HashMap<String, String>>>();
        ArrayList<HashMap<String, String>> TpolGrupos = new ArrayList<HashMap<String, String>>();
        
        TpolGrupos = this.getCtbDao().getTipoPoliza_Grupos();
        
        jsonretorno.put("Grupos", TpolGrupos);
        
        return jsonretorno;
    }
    
    
    
    
    @RequestMapping(method = RequestMethod.POST, value="/getTipoPoliza.json")
    public @ResponseBody HashMap<String,ArrayList<HashMap<String, String>>> getTipoPolizaJson(
            @RequestParam(value="id", required=true) Integer id,
            Model model
            ) {
        
        log.log(Level.INFO, "Ejecutando getTipoPoliza de {0}", CtbTipopolizasController.class.getName());
        HashMap<String,ArrayList<HashMap<String, String>>> jsonretorno = new HashMap<String,ArrayList<HashMap<String, String>>>();
        ArrayList<HashMap<String, String>> datosTipoPoliza = new ArrayList<HashMap<String, String>>();
        ArrayList<HashMap<String, String>> TpolGrupos = new ArrayList<HashMap<String, String>>();
        
        if( id != 0  ){
            datosTipoPoliza = this.getCtbDao().getTipoPoliza_Datos(id);
        }
        
        TpolGrupos = this.getCtbDao().getTipoPoliza_Grupos();
        
        jsonretorno.put("Tpol", datosTipoPoliza);
        jsonretorno.put("Grupos", TpolGrupos);
        
        return jsonretorno;
    }
    
    
    //crear y editar
    @RequestMapping(method = RequestMethod.POST, value="/edit.json")
    public @ResponseBody HashMap<String, String> editJson(
            @RequestParam(value="identificador", required=true) String id,
            @RequestParam(value="tipo", required=true) String tipo,
            @RequestParam(value="descripcion", required=true) String descripcion,
            @RequestParam(value="select_grupo", required=true) String grupo,
            Model model,@ModelAttribute("user") UserSessionData user
            ) {

        HashMap<String, String> jsonretorno = new HashMap<String, String>();
        HashMap<String, String> succes = new HashMap<String, String>();
        Integer app_selected = 16;
        String command_selected = "new";
        Integer id_usuario= user.getUserId();//variable para el id  del usuario
        String extra_data_array = "'sin datos'";
        String actualizo = "0";

        if( id.equals("0") ){
            command_selected = "new";
        }else{
            command_selected = "edit";
        }
        
        String data_string = app_selected+"___"+command_selected+"___"+id_usuario+"___"+id+"___"+tipo+"___"+descripcion.toUpperCase()+"___"+grupo;
        
        succes = this.getCtbDao().selectFunctionValidateAaplicativo(data_string,app_selected,extra_data_array);
        
        log.log(Level.INFO, "despues de validacion {0}", String.valueOf(succes.get("success")));
        if( String.valueOf(succes.get("success")).equals("true") ){
            actualizo = this.getCtbDao().selectFunctionForThisApp(data_string, extra_data_array);
        }
        
        jsonretorno.put("success",String.valueOf(succes.get("success")));
        
        log.log(Level.INFO, "Salida json {0}", String.valueOf(jsonretorno.get("success")));
        return jsonretorno;
    }
    
    
    
    
    
    //cambiar a borrado logico un registro
    @RequestMapping(method = RequestMethod.POST, value="/logicDelete.json")
    public @ResponseBody HashMap<String, String> logicDeleteJson(
            @RequestParam(value="id", required=true) Integer id,
            @RequestParam(value="iu", required=true) String id_user,
            Model model
            ) {
        
        HashMap<String, String> jsonretorno = new HashMap<String, String>();
        
        //decodificar id de usuario
        Integer id_usuario = Integer.parseInt(Base64Coder.decodeString(id_user));
        
        Integer app_selected = 16;
        String command_selected = "delete";
        String extra_data_array = "'sin datos'";
        String data_string = app_selected+"___"+command_selected+"___"+id_usuario+"___"+id;
        
        System.out.println("Ejecutando borrado logico de tipo poliza");
        jsonretorno.put("success",String.valueOf( this.getCtbDao().selectFunctionForThisApp(data_string,extra_data_array)) );
        
        return jsonretorno;
    }
    
    
    
    
    
    
    
    
    
}
