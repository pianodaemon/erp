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
@RequestMapping("/crmregistrovisitas/")
public class CrmRegistroVisitasController {
    ResourceProject resource = new ResourceProject();
    private static final Logger log  = Logger.getLogger(CrmRegistroVisitasController.class.getName());
    
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
        
        log.log(Level.INFO, "Ejecutando starUp de {0}", CrmRegistroVisitasController.class.getName());
        LinkedHashMap<String,String> infoConstruccionTabla = new LinkedHashMap<String,String>();
        
        infoConstruccionTabla.put("id", "Acciones:90");
        infoConstruccionTabla.put("folio", "Folio:75");
        infoConstruccionTabla.put("fecha", "Fecha:80");
        infoConstruccionTabla.put("agente", "Nombre&nbsp;del&nbsp;Agente:250");
        infoConstruccionTabla.put("motivo", "Motivo:150");
        infoConstruccionTabla.put("calif", "Calificaci&oacute;n:120");
        infoConstruccionTabla.put("tipo_seg", "Tipo&nbsp;de&nbsp;Seguimiento:150");
        infoConstruccionTabla.put("proxvisita", "Pr&oacute;xima&nbsp;visita:118");
        infoConstruccionTabla.put("proxllamada", "Pr&oacute;xima&nbsp;llamada:118");
        
        ModelAndView x = new ModelAndView("crmregistrovisitas/startup", "title", "Registro de Visitas");
        
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
    
    
    
    //obtener todos los registros de Visitas del Agente
    @RequestMapping(value="/getAllRegistroVisitas.json", method = RequestMethod.POST)
    public @ResponseBody HashMap<String,ArrayList<HashMap<String, Object>>> getAllRegistroVisitasJson(
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
        Integer app_selected = 115;
        
        //Decodificar id de usuario
        Integer id_usuario = Integer.parseInt(Base64Coder.decodeString(id_user_cod));
        
        //Variables para el buscador
        String folio = "%"+StringHelper.isNullString(String.valueOf(has_busqueda.get("folio")))+"%";
        String tipo_visita = String.valueOf(has_busqueda.get("tipo_visita"));
        String contacto = "%"+StringHelper.isNullString(String.valueOf(has_busqueda.get("contacto")))+"%";
        String cliente = "%"+StringHelper.isNullString(String.valueOf(has_busqueda.get("cliente")))+"%";
        String agente = String.valueOf(has_busqueda.get("agente"));
        String fecha_inicial = ""+StringHelper.isNullString(String.valueOf(has_busqueda.get("fecha_inicial")))+"";
        String fecha_final = ""+StringHelper.isNullString(String.valueOf(has_busqueda.get("fecha_final")))+"";
        
        String data_string = app_selected+"___"+id_usuario+"___"+folio+"___"+tipo_visita+"___"+contacto+"___"+agente+"___"+fecha_inicial+"___"+fecha_final+"___"+cliente;
        
        //Obtiene total de registros en base de datos, con los parametros de busqueda
        int total_items = this.getCrmDao().countAll(data_string);
        
        //Calcula el total de paginas
        int total_pags = resource.calculaTotalPag(total_items,items_por_pag);
        
        //Variables que necesita el datagrid, para no tener que hacer uno por cada aplicativo
        DataPost dataforpos = new DataPost(orderby, desc, items_por_pag, pag_start, display_pag, input_json, cadena_busqueda,total_items,total_pags, id_user_cod);
        
        int offset = resource.__get_inicio_offset(items_por_pag, pag_start);
        
        //Obtiene los registros para el grid, de acuerdo a los parametros de busqueda
        jsonretorno.put("Data", this.getCrmDao().getCrmRegistroVisitas_PaginaGrid(data_string, offset, items_por_pag, orderby, desc));
        
        //Obtiene el hash para los datos que necesita el datagrid
        jsonretorno.put("DataForGrid", dataforpos.formaHashForPos(dataforpos));
        
        return jsonretorno;
    }
    
    
    //Obtiene los Agentes para el Buscador pricipal del Aplicativo
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
    
    
    //Obtiene los Clientes y Prospectos
    @RequestMapping(method = RequestMethod.POST, value="/getAutoClienteProspecto.json")
    public @ResponseBody ArrayList<HashMap<String, Object>> getAutoClienteProspectoJson(
            @RequestParam(value="tipo", required=true) Integer tipo,
            @RequestParam(value="cadena", required=true) String cadena,
            @RequestParam(value="iu", required=true) String id_user,
            Model model
        ) {
        
        log.log(Level.INFO, "Ejecutando getAutoClienteProspectoJson de {0}", CrmRegistroVisitasController.class.getName());
        HashMap<String, String> userDat = new HashMap<String, String>();
        
        //Decodificar id de usuario
        userDat = this.getHomeDao().getUserById(Integer.parseInt(Base64Coder.decodeString(id_user)));
        
        Integer id_empresa = Integer.parseInt(userDat.get("empresa_id"));
        
        cadena = "%"+StringHelper.isNullString(String.valueOf(cadena.trim()))+"%";
        
        return this.getCrmDao().getClientesProspectos(tipo, cadena, id_empresa);
    }
    
    //Obtiene los Clientes y Prospectos
    @RequestMapping(method = RequestMethod.POST, value="/getAutocompleteContactos.json")
    public @ResponseBody ArrayList<HashMap<String, Object>> getAutocompleteContactosJson(
            @RequestParam(value="tipo", required=true) Integer tipo,
            @RequestParam(value="cadena", required=true) String cadena,
            @RequestParam(value="iu", required=true) String id_user,
            Model model
        ) {
        
        log.log(Level.INFO, "Ejecutando getAutocompleteContactosJson de {0}", CrmRegistroVisitasController.class.getName());
        HashMap<String, String> userDat = new HashMap<String, String>();
        
        //Decodificar id de usuario
        userDat = this.getHomeDao().getUserById(Integer.parseInt(Base64Coder.decodeString(id_user)));
        
        Integer id_empresa = Integer.parseInt(userDat.get("empresa_id"));
        
        cadena = "%"+StringHelper.isNullString(String.valueOf(cadena.trim()))+"%";
        
        return this.getCrmDao().getAutocompletadoContactos(tipo, cadena, id_empresa);
    }
    
    
    @RequestMapping(method = RequestMethod.POST, value="/getRegistroVisita.json")
    public @ResponseBody HashMap<String,ArrayList<HashMap<String, String>>> getRegistroVisitaJson(
            @RequestParam(value="id", required=true) Integer id,
            @RequestParam(value="iu", required=true) String id_user,
            Model model
            ) {
        
        log.log(Level.INFO, "Ejecutando getRegistroVisitaJson de {0}", CrmRegistroVisitasController.class.getName());
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
            datos = this.getCrmDao().getCrmRegistroVisitas_Datos(id);
        }
        
        agentes = this.getCrmDao().getAgentes(id_empresa);
        motivos = this.getCrmDao().getCrmRegistroVisitas_Motivos(id_empresa);
        calificaciones = this.getCrmDao().getCrmRegistroVisitas_Calificaciones(id_empresa);
        seguimientos = this.getCrmDao().getCrmRegistroVisitas_Seguimientos(id_empresa);
        
        extra = this.getCrmDao().getUserRol(id_usuario);
        extra.put("id_agente", String.valueOf(id_agente));
        arrayExtra.add(0,extra);
        
        jsonretorno.put("Datos", datos);
        jsonretorno.put("Extra", arrayExtra);
        jsonretorno.put("Agentes", agentes);
        jsonretorno.put("Motivos", motivos);
        jsonretorno.put("Calificaciones", calificaciones);
        jsonretorno.put("Seguimientos", seguimientos);
        
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
        
        log.log(Level.INFO, "Ejecutando getBuscadorContactoJson de {0}", CrmRegistroVisitasController.class.getName());
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
    
    
    
    //Crear y editar
    @RequestMapping(method = RequestMethod.POST, value="/edit.json")
    public @ResponseBody HashMap<String, String> editJson(
        @RequestParam(value="identificador", required=true) String identificador,
        @RequestParam(value="select_agente", required=true) String select_agente,
        @RequestParam(value="id_contacto", required=true) String id_contacto,
        @RequestParam(value="fecha", required=true) String fecha,
        @RequestParam(value="hora_visita", required=true) String hora_visita,
        @RequestParam(value="hora_duracion", required=true) String hora_duracion,
        @RequestParam(value="select_motivo_visita", required=true) String select_motivo_visita,
        @RequestParam(value="select_calif_visita", required=true) String select_calif_visita,
        @RequestParam(value="select_tipo_seguimiento", required=true) String select_tipo_seguimiento,
        @RequestParam(value="select_oportunidad", required=true) String select_oportunidad,
        @RequestParam(value="recusrsos_visita", required=true) String recusrsos_visita,
        @RequestParam(value="resultado_visita", required=true) String resultado_visita,
        @RequestParam(value="observaciones_visita", required=true) String observaciones_visita,
        @RequestParam(value="fecha_proxima_visita", required=true) String fecha_proxima_visita,
        @RequestParam(value="hora_proxima_visita", required=true) String hora_proxima_visita,
        @RequestParam(value="comentarios_proxima_visita", required=true) String comentarios_proxima_visita,
        @RequestParam(value="productos", required=true) String productos,
        @RequestParam(value="fecha_proxima_llamada", required=true) String fecha_proxima_llamada,
        @RequestParam(value="hora_proxima_llamada", required=true) String hora_proxima_llamada,
        @RequestParam(value="comentarios_proxima_llamada", required=true) String comentarios_proxima_llamada,
        Model model,@ModelAttribute("user") UserSessionData user
    ) {
        Integer app_selected = 115;//Aplicativo de Registro de Visitas
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
        +"___"+select_agente
        +"___"+id_contacto
        +"___"+fecha
        +"___"+hora_visita
        +"___"+hora_duracion
        +"___"+select_motivo_visita
        +"___"+select_calif_visita
        +"___"+select_tipo_seguimiento
        +"___"+select_oportunidad
        +"___"+recusrsos_visita.toUpperCase()
        +"___"+resultado_visita.toUpperCase()
        +"___"+observaciones_visita.toUpperCase()
        +"___"+fecha_proxima_visita
        +"___"+hora_proxima_visita
        +"___"+comentarios_proxima_visita.trim().toUpperCase()
        +"___"+productos.toUpperCase()
        +"___"+fecha_proxima_llamada
        +"___"+hora_proxima_llamada
        +"___"+comentarios_proxima_llamada.trim().toUpperCase();
        
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
        
        System.out.println("Borrado logico de Registro de Visita");
        //decodificar id de usuario
        Integer id_usuario = Integer.parseInt(Base64Coder.decodeString(id_user));
        
        Integer app_selected = 115;//Aplicativo de Registro de Visitas
        String command_selected = "delete";
        String extra_data_array = "'sin datos'";
        
        String data_string = app_selected+"___"+command_selected+"___"+id_usuario+"___"+id;
        
        HashMap<String, String> jsonretorno = new HashMap<String, String>();
        
        jsonretorno.put("success",String.valueOf( this.getCrmDao().selectFunctionForCrmAdmProcesos(data_string,extra_data_array)) );
        
        return jsonretorno;
    }
}
