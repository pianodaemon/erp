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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;


@Controller
@SessionAttributes({"user"})
@RequestMapping("/crmregistrollamadas/")
public class CrmRegistroLlamadasController {
    ResourceProject resource = new ResourceProject();
    private static final Logger log  = Logger.getLogger(CrmRegistroLlamadasController.class.getName());
    
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
        
        log.log(Level.INFO, "Ejecutando starUp de {0}", CrmRegistroLlamadasController.class.getName());
        LinkedHashMap<String,String> infoConstruccionTabla = new LinkedHashMap<String,String>();
        infoConstruccionTabla.put("id", "Acciones:90");
        infoConstruccionTabla.put("folio", "Folio:100");
        infoConstruccionTabla.put("agente", "Nombre del Agente:250");
        infoConstruccionTabla.put("motivo", "Motivo:150");
        infoConstruccionTabla.put("calif", "Calificaci&oacute;n:120");
        infoConstruccionTabla.put("tipo_seg", "Tipo de Seguimiento:150");
        infoConstruccionTabla.put("fecha", "Fecha:100");
        infoConstruccionTabla.put("hora", "Hora:100");
        
        ModelAndView x = new ModelAndView("crmregistrollamadas/startup", "title", "Registro de  Llamadas");
        
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
    
    //obtener todos los registros de Llamads del Agente
    @RequestMapping(value="/getAllRegistroLlamadas.json", method = RequestMethod.POST)
    public @ResponseBody HashMap<String,ArrayList<HashMap<String, Object>>> getAllRegistroLlamadasJson(
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
        
        //Aplicativo de Registro de Visitas
        Integer app_selected = 114;
        
        //decodificar id de usuario
        Integer id_usuario = Integer.parseInt(Base64Coder.decodeString(id_user_cod));
        
        //variables para el buscador
        String folio = "%"+StringHelper.isNullString(String.valueOf(has_busqueda.get("folio")))+"%";
        String tipo_visita = String.valueOf(has_busqueda.get("tipo_visita"));
        String contacto = "%"+StringHelper.isNullString(String.valueOf(has_busqueda.get("contacto")))+"%";
        String agente = String.valueOf(has_busqueda.get("agente"));
        String fecha_inicial = ""+StringHelper.isNullString(String.valueOf(has_busqueda.get("fecha_inicial")))+"";
        String fecha_final = ""+StringHelper.isNullString(String.valueOf(has_busqueda.get("fecha_final")))+"";
        
        String data_string = app_selected+"___"+id_usuario+"___"+folio+"___"+tipo_visita+"___"+contacto+"___"+agente+"___"+fecha_inicial+"___"+fecha_final;
        
        //obtiene total de registros en base de datos, con los parametros de busqueda
        int total_items = this.getCrmDao().countAll(data_string);
        
        //calcula el total de paginas
        int total_pags = resource.calculaTotalPag(total_items,items_por_pag);
        
        //variables que necesita el datagrid, para no tener que hacer uno por cada aplicativo
        DataPost dataforpos = new DataPost(orderby, desc, items_por_pag, pag_start, display_pag, input_json, cadena_busqueda,total_items,total_pags, id_user_cod);
        
        int offset = resource.__get_inicio_offset(items_por_pag, pag_start);
        
        //obtiene los registros para el grid, de acuerdo a los parametros de busqueda
        jsonretorno.put("Data", this.getCrmDao().getRegistroLlamadas_PaginaGrid(data_string, offset, items_por_pag, orderby, desc));
        
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
    
    @RequestMapping(method = RequestMethod.POST,value="/getRegistroLlamada.json")
    public @ResponseBody HashMap<String,ArrayList<HashMap<String, String>>> getRegistroJson(
            @RequestParam(value="id", required=true) Integer id,
            @RequestParam(value="iu", required=true) String id_user,
            Model model
            ) {
        
        log.log(Level.INFO, "Ejecutando getclientsdfJson de {0}", CrmRegistroLlamadasController.class.getName());
        HashMap<String,ArrayList<HashMap<String, String>>> jsonretorno = new HashMap<String,ArrayList<HashMap<String, String>>>();
        HashMap<String, String> userDat = new HashMap<String, String>();
        ArrayList<HashMap<String, String>> datos = new ArrayList<HashMap<String, String>>();
        ArrayList<HashMap<String, String>> agentes = new ArrayList<HashMap<String, String>>();
        ArrayList<HashMap<String, String>> motivos = new ArrayList<HashMap<String, String>>();
        ArrayList<HashMap<String, String>> calificaciones = new ArrayList<HashMap<String, String>>();
        ArrayList<HashMap<String, String>> seguimientos = new ArrayList<HashMap<String, String>>();
        ArrayList<HashMap<String, String>> arrayExtra = new ArrayList<HashMap<String, String>>();
        HashMap<String, String> extra = new HashMap<String, String>();
        
        
        //decodificar id de usuario
        Integer id_usuario = Integer.parseInt(Base64Coder.decodeString(id_user));
        userDat = this.getHomeDao().getUserById(id_usuario);
        Integer id_empresa = Integer.parseInt(userDat.get("empresa_id"));
        Integer id_agente = Integer.parseInt(userDat.get("empleado_id"));
        
        if( id != 0  ){
            datos = this.getCrmDao().getCrmRegistroLlamadas_Datos(id);
        }
        
        agentes = this.getCrmDao().getAgentes(id_empresa);
        motivos = this.getCrmDao().getMotivos_Llamadas(id_empresa);
        calificaciones = this.getCrmDao().getCalificacion_Llamadas(id_empresa);
        seguimientos = this.getCrmDao().getRegistroLlamadas_Seguimiento(id_empresa);
        
        extra = this.getCrmDao().getUserRol(id_usuario);
        extra.put("id_agente", String.valueOf(id_agente));
        arrayExtra.add(0,extra);
        
        jsonretorno.put("Datos", datos);
        jsonretorno.put("Extra", arrayExtra);
        jsonretorno.put("Agentes", agentes);
        jsonretorno.put("MotivosLlamadas", motivos);
        jsonretorno.put("CalificacionLlamadas", calificaciones);
        jsonretorno.put("TipoSeguimiento", seguimientos);
        
        return jsonretorno;

        
    }
    
     //obtiene los Contactos para el buscador
    @RequestMapping(method = RequestMethod.POST, value="/get_buscador_contactos.json")
    public @ResponseBody HashMap<String,ArrayList<HashMap<String, String>>> getBuscadorContactoJson(
            @RequestParam(value="buscador_nombre", required=true) String nombre,
            @RequestParam(value="buscador_apellidop", required=true) String apellidop,
            @RequestParam(value="buscador_apellidom", required=true) String apellidom,
            @RequestParam(value="buscador_tipo_contacto", required=true) String tipo_contacto,
            @RequestParam(value="iu", required=true) String id_user,
            Model model
            ) {
        
        log.log(Level.INFO, "Ejecutando getBuscadorContactoJson de {0}", CrmRegistroLlamadasController.class.getName());
        HashMap<String,ArrayList<HashMap<String, String>>> jsonretorno = new HashMap<String,ArrayList<HashMap<String, String>>>();
        ArrayList<HashMap<String, String>> contactos = new ArrayList<HashMap<String, String>>();
        HashMap<String, String> userDat = new HashMap<String, String>();
        
        //decodificar id de usuario
        userDat = this.getHomeDao().getUserById(Integer.parseInt(Base64Coder.decodeString(id_user)));
        
        Integer id_empresa = Integer.parseInt(userDat.get("empresa_id"));
        
        nombre = "%"+StringHelper.isNullString(String.valueOf(nombre))+"%";
        apellidop = "%"+StringHelper.isNullString(String.valueOf(apellidop))+"%";
        apellidom = "%"+StringHelper.isNullString(String.valueOf(apellidom))+"%";
        
        contactos = this.getCrmDao().getBuscadorContactos(nombre, apellidop, apellidom,tipo_contacto, id_empresa);
        
        jsonretorno.put("contactos", contactos);
        
        return jsonretorno;
    }
    
    //crear y editar un cliente
    @RequestMapping(method = RequestMethod.POST, value="/edit.json")
    
    
    public @ResponseBody HashMap<String, String> editJson(
        @RequestParam(value="identificador_prospecto", required=true) String identificador,
        @RequestParam(value="select_agente", required=true) String select_agente,
        @RequestParam(value="id_contacto", required=true) String id_contacto,
        @RequestParam(value="fecha_registro", required=true) String fecha_registro,
        @RequestParam(value="hora_registro", required=true) String hora_registro,
        @RequestParam(value="duracion_llamada", required=true) String duracion_llamada,
        @RequestParam(value="select_motivo_llamada", required=true) String select_motivo_llamada,
        @RequestParam(value="select_calificacion", required=true) String calificacion,
        @RequestParam(value="select_tipo_seguimiento", required=true) String select_tipo_seguimiento,
        @RequestParam(value="select_consiguio_cita", required=true) String select_consiguio_cita,
        @RequestParam(value="select_llamada_completa", required=true) String select_llamada_completa,
        @RequestParam(value="select_llamada_planeada", required=true) String select_llamada_planeada,
        @RequestParam(value="select_tipo_llamada",required=true)String select_tipo_llamada,
        @RequestParam(value="resultado_llamada", required=true) String resultado_llamada,
        @RequestParam(value="comentarios", required=true) String comentarios,
        @RequestParam(value="observaciones",required=true)String observaciones,
        @RequestParam(value="fecha_cita_proxima", required=true) String fecha_cita_proxima,
        @RequestParam(value="hora_prox_cita", required=true) String hora_prox_cita,
        
        
        Model model,@ModelAttribute("user") UserSessionData user
        ) {
        Integer app_selected = 114;//Aplicativo de Registro de llamadas
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
        app_selected//1
        +"___"+command_selected//2
        +"___"+id_usuario//3
        +"___"+identificador//4
        +"___"+select_agente//5
        +"___"+id_contacto//6
        +"___"+fecha_registro//7
        +"___"+hora_registro//8
        +"___"+duracion_llamada//9
        +"___"+select_motivo_llamada//10
        +"___"+calificacion//11
        +"___"+select_tipo_seguimiento//12
        +"___"+select_consiguio_cita//13
        +"___"+select_llamada_completa//14
        +"___"+select_llamada_planeada//15
        +"___"+select_tipo_llamada//16
        +"___"+resultado_llamada//17
        +"___"+comentarios//18
        +"___"+observaciones//19
        +"___"+fecha_cita_proxima//20
        +"___"+hora_prox_cita;//21
        
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
        
        System.out.println("Borrado logico de Registro de Visita");
        //decodificar id de usuario
        Integer id_usuario = Integer.parseInt(Base64Coder.decodeString(id_user));
        
        Integer app_selected = 114;//Aplicativo de Registro de llamadas
        String command_selected = "delete";
        String extra_data_array = "'sin datos'";
        
        String data_string = app_selected+"___"+command_selected+"___"+id_usuario+"___"+id;
        
        HashMap<String, String> jsonretorno = new HashMap<String, String>();
        
        jsonretorno.put("success",String.valueOf( this.getCrmDao().selectFunctionForCrmAdmProcesos(data_string,extra_data_array)) );
        
        return jsonretorno;
    }
    
}
