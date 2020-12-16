package com.agnux.kemikal.controllers;

import com.agnux.cfd.v2.Base64Coder;
import com.agnux.common.helpers.StringHelper;
import com.agnux.common.obj.DataPost;
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
@RequestMapping("/crmregistrocasos/")
public class CrmRegistroCasosController {
    ResourceProject resource = new ResourceProject();
    private static final Logger log  = Logger.getLogger(CrmRegistroCasosController.class.getName());
    
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
    public ModelAndView startUp(HttpServletRequest request, HttpServletResponse response, @ModelAttribute("user") UserSessionData user)
            throws ServletException, IOException {
        
        log.log(Level.INFO, "Ejecutando starUp de {0}", CrmRegistroCasosController.class.getName());
        LinkedHashMap<String,String> infoConstruccionTabla = new LinkedHashMap<String,String>();
        
        infoConstruccionTabla.put("id", "Acciones:90");
        infoConstruccionTabla.put("folio", "Folio:100");
        infoConstruccionTabla.put("razon_social", "Cliente/Prospecto:350");
        infoConstruccionTabla.put("buscado_por", "Tipo de Caso:100");
        infoConstruccionTabla.put("fecha_cierre", "Fecha Cierre:150");
        
        
        ModelAndView x = new ModelAndView("crmregistrocasos/startup", "title", "Registro de Casos");
        
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
    
    
    
    //obtener todos los registros de Casos
    @RequestMapping(value="/getAllRegistroCasos.json", method = RequestMethod.POST)
    public @ResponseBody HashMap<String,ArrayList<HashMap<String, Object>>> getAllRegistroCasosJson(
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
        
        //Aplicativo de Registro de Casos
        Integer app_selected = 124;
        
        //decodificar id de usuario
        Integer id_usuario = Integer.parseInt(Base64Coder.decodeString(id_user_cod));
        
        //variables para el buscador
        String folio = "%"+StringHelper.isNullString(String.valueOf(has_busqueda.get("folio")))+"%";
        String tipo = String.valueOf(has_busqueda.get("tipo"));
        String id_cliente_prospecto = StringHelper.isNullString(String.valueOf(has_busqueda.get("id_cliente_prospecto")));
        String cliente_prospecto = "%"+StringHelper.isNullString(String.valueOf(has_busqueda.get("cliente_prospecto")))+"%";
        String prioridad = String.valueOf(has_busqueda.get("prioridad"));
        String fecha_cierre = ""+StringHelper.isNullString(String.valueOf(has_busqueda.get("fecha_cierre")))+"";
        String agente = String.valueOf(has_busqueda.get("agente"));
       
        //                        1                 2               3         4                   5                       6                     7                8              9                                                                        
        String data_string = app_selected+"___"+id_usuario+"___"+folio+"___"+tipo+"___"+id_cliente_prospecto+"___"+cliente_prospecto+"___"+prioridad+"___"+fecha_cierre+"___"+agente;
        
        //obtiene total de registros en base de datos, con los parametros de busqueda
        int total_items = this.getCrmDao().countAll(data_string);
        
        //calcula el total de paginas
        int total_pags = resource.calculaTotalPag(total_items,items_por_pag);
        
        //variables que necesita el datagrid, para no tener que hacer uno por cada aplicativo
        DataPost dataforpos = new DataPost(orderby, desc, items_por_pag, pag_start, display_pag, input_json, cadena_busqueda,total_items,total_pags, id_user_cod);
        
        int offset = resource.__get_inicio_offset(items_por_pag, pag_start);
        
        //obtiene los registros para el grid, de acuerdo a los parametros de busqueda
        jsonretorno.put("Data", this.getCrmDao().getCrmRegistroCasos_PaginaGrid(data_string, offset, items_por_pag, orderby, desc));
        //obtiene el hash para los datos que necesita el datagrid
        jsonretorno.put("DataForGrid", dataforpos.formaHashForPos(dataforpos));
        
        return jsonretorno;
    }
    
    //obtiene los Agentes para el Buscador pricipal del Aplicativo
    @RequestMapping(method = RequestMethod.POST, value="/getAgentesParaBuscador.json")
    public @ResponseBody HashMap<String,ArrayList<HashMap<String, String>>> getAgentesParaBuscador(
            @RequestParam(value="iu", required=true) String id_user_cod,
            Model model
        ) {
        
        HashMap<String,ArrayList<HashMap<String, String>>> jsonretorno = new HashMap<String,ArrayList<HashMap<String, String>>>();
        HashMap<String, String> userDat = new HashMap<String, String>();
        ArrayList<HashMap<String, String>> agentes = new ArrayList<HashMap<String, String>>();
        ArrayList<HashMap<String, String>> arrayExtra = new ArrayList<HashMap<String, String>>();
        HashMap<String, String> extra = new HashMap<String, String>();
        
        //decodificar id de usuario
        Integer id_usuario = Integer.parseInt(Base64Coder.decodeString(id_user_cod));
        userDat = this.getHomeDao().getUserById(id_usuario);
        Integer id_empresa = Integer.parseInt(userDat.get("empresa_id"));
        Integer id_agente = Integer.parseInt(userDat.get("empleado_id"));
        
        extra = this.getCrmDao().getUserRol(id_usuario);
        extra.put("id_agente", String.valueOf(id_agente));
        arrayExtra.add(0,extra);
        
        agentes = this.getCrmDao().getAgentes(id_empresa);
        
        jsonretorno.put("Extra", arrayExtra);
        jsonretorno.put("Agentes", agentes);
        return jsonretorno;
    }
    
    
    @RequestMapping(method = RequestMethod.POST, value="/getRegistroCaso.json")
    public @ResponseBody HashMap<String,ArrayList<HashMap<String, String>>> getRegistroCasoJson(
            @RequestParam(value="id", required=true) Integer id,
            @RequestParam(value="iu", required=true) String id_user,
            Model model
            ) {
        
        log.log(Level.INFO, "Ejecutando getRegistroCasoJson de {0}", CrmRegistroCasosController.class.getName());
        HashMap<String,ArrayList<HashMap<String, String>>> jsonretorno = new HashMap<String,ArrayList<HashMap<String, String>>>();
        HashMap<String, String> userDat = new HashMap<String, String>();
        ArrayList<HashMap<String, String>> datos = new ArrayList<HashMap<String, String>>();
        ArrayList<HashMap<String, String>> agentes = new ArrayList<HashMap<String, String>>();
        ArrayList<HashMap<String, String>> arrayExtra = new ArrayList<HashMap<String, String>>();
        HashMap<String, String> extra = new HashMap<String, String>();
        
        
        //decodificar id de usuario
        Integer id_usuario = Integer.parseInt(Base64Coder.decodeString(id_user));
        userDat = this.getHomeDao().getUserById(id_usuario);
        Integer id_empresa = Integer.parseInt(userDat.get("empresa_id"));
        Integer id_agente = Integer.parseInt(userDat.get("empleado_id"));
        
        if( id != 0  ){
            datos = this.getCrmDao().getCrmRegistroCasos_Datos(id);
        }
        
        agentes = this.getCrmDao().getAgentes(id_empresa);
       
        
        extra = this.getCrmDao().getUserRol(id_usuario);
        extra.put("id_agente", String.valueOf(id_agente));
        arrayExtra.add(0,extra);
        
        jsonretorno.put("Datos", datos);
        jsonretorno.put("Extra", arrayExtra);
        jsonretorno.put("Agentes", agentes);
        
        
        return jsonretorno;
    }
    
    
    
    //obtiene los Contactos para el buscador
    @RequestMapping(method = RequestMethod.POST, value="/get_buscador_cliente_prospecto.json")
    public @ResponseBody HashMap<String,ArrayList<HashMap<String, String>>> getBuscadorContactoJson(
            @RequestParam(value="buscador_razon_social", required=true) String Razon_Social,
            @RequestParam(value="buscador_rfc", required=true) String Rfc,
            @RequestParam(value="identificador_cliente_prospecto", required=true) Integer Identificador_Cliente_Prospecto,
            @RequestParam(value="iu", required=true) String id_user,
            Model model
            ) {
        
        log.log(Level.INFO, "Ejecutando getBuscadorContactoJson de {0}", CrmRegistroCasosController.class.getName());
        HashMap<String,ArrayList<HashMap<String, String>>> jsonretorno = new HashMap<String,ArrayList<HashMap<String, String>>>();
        ArrayList<HashMap<String, String>> cliente_Prospecto = new ArrayList<HashMap<String, String>>();
        HashMap<String, String> userDat = new HashMap<String, String>();
        
        //decodificar id de usuario
        userDat = this.getHomeDao().getUserById(Integer.parseInt(Base64Coder.decodeString(id_user)));
        
        Integer id_empresa = Integer.parseInt(userDat.get("empresa_id"));
        
        Razon_Social = "%"+StringHelper.isNullString(String.valueOf(Razon_Social))+"%";
        Rfc = "%"+StringHelper.isNullString(String.valueOf(Rfc))+"%";
        
        cliente_Prospecto = this.getCrmDao().getBuscadorCliente_Prospecto(Razon_Social, Rfc,Identificador_Cliente_Prospecto, id_empresa);
        
        jsonretorno.put("array_cliente_prospecto", cliente_Prospecto);
        
        return jsonretorno;
    }
/*
agente	rfffffffffffffffffffffffffffffff
buscando_por	0
cliente_prospecto	INDUSTRIA CARROCERA SAN ROBERTO S.A. DE C.V.
descripcion	gggggggggggggggggggggggg
fecha_cierre	2013-01-12
folio	
id_agente	0
id_cliente_prospecto	367
identificador	0
observacion_agente	nnnnnnnnnnnnnnnnnnnnnnnnnnnnnn
resolucion	ghhhhhhhhhhhhhhhhhhhhhh
select_estatus	2
select_prioridad	1
select_tipo_caso	5
*/
    //crear y editar un Registro de casos
    @RequestMapping(method = RequestMethod.POST, value="/edit.json")
    public @ResponseBody HashMap<String, String> editJson(
        @RequestParam(value="identificador", required=true) String identificador,
        @RequestParam(value="id_cliente_prospecto", required=true) String id_cliente_prospecto,
        @RequestParam(value="select_agente", required=true) String agente_id,
       
        @RequestParam(value="buscando_por", required=true) String tipo_pluguin,
        @RequestParam(value="fecha_cierre", required=true) String fecha_cierre,
        @RequestParam(value="descripcion", required=true) String descripcion,
        @RequestParam(value="resolucion", required=true) String resolucion,
        @RequestParam(value="observacion_agente", required=true) String observacion_agente,
        @RequestParam(value="select_estatus", required=true) String select_estatus,
        @RequestParam(value="select_prioridad", required=true) String select_prioridad,
        @RequestParam(value="select_tipo_caso", required=true) String select_tipo_caso,
        
        Model model,@ModelAttribute("user") UserSessionData user
        
        ) {
        Integer app_selected = 124;//Aplicativo de Registro de Casos
        String command_selected = "new";
        Integer id_usuario= user.getUserId();//variable para el id  del usuario
        String arreglo[];
        String extra_data_array = "'sin datos'";
        String actualizo = "0";
        
        HashMap<String, String> jsonretorno = new HashMap<String, String>();
        
        HashMap<String, String> succes = new HashMap<String, String>();
        
        if( identificador.equals("0") ){
            command_selected = "new";
        }else{
            command_selected = "edit";
        }
        
        String data_string = 
        app_selected
        +"___"+command_selected
        +"___"+id_usuario
        +"___"+identificador
         +"___"+id_cliente_prospecto     
        +"___"+select_estatus
        +"___"+select_prioridad
        +"___"+select_tipo_caso
        +"___"+fecha_cierre
        +"___"+descripcion.toUpperCase()
        +"___"+resolucion.toUpperCase()
        +"___"+observacion_agente.toUpperCase()
        +"___"+tipo_pluguin
        +"___"+agente_id;
        
        //System.out.println("data_string: "+data_string);
        succes = this.getCrmDao().selectFunctionValidateAaplicativo(data_string,app_selected,extra_data_array);
        
        log.log(Level.INFO, "despues de validacion {0}", String.valueOf(succes.get("success")));
        if( String.valueOf(succes.get("success")).equals("true") ){
            actualizo = this.getCrmDao().selectFunctionForCrmAdmProcesos(data_string, extra_data_array);
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
        
        System.out.println("Borrado logico de Registro de Casos");
        //decodificar id de usuario
        Integer id_usuario = Integer.parseInt(Base64Coder.decodeString(id_user));
        
        Integer app_selected = 124;//Aplicativo de Registro de Casos
        String command_selected = "delete";
        String extra_data_array = "'sin datos'";
        
        String data_string = app_selected+"___"+command_selected+"___"+id_usuario+"___"+id;
        
        HashMap<String, String> jsonretorno = new HashMap<String, String>();
        
        jsonretorno.put("success",String.valueOf( this.getCrmDao().selectFunctionForCrmAdmProcesos(data_string,extra_data_array)) );
        
        return jsonretorno;
    }
    
    
    
    
    
}
