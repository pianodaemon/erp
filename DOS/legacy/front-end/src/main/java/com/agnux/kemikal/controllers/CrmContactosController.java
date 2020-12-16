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
@RequestMapping("/crmcontactos/")
public class CrmContactosController {
    ResourceProject resource = new ResourceProject();
    private static final Logger log  = Logger.getLogger(CrmContactosController.class.getName());
    
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
        
        log.log(Level.INFO, "Ejecutando starUp de {0}", CrmContactosController.class.getName());
        LinkedHashMap<String,String> infoConstruccionTabla = new LinkedHashMap<String,String>();
        infoConstruccionTabla.put("id", "Acciones:90");
        infoConstruccionTabla.put("contacto", "Contacto:270");
        infoConstruccionTabla.put("tipo_contacto", "Tipo:100");
        infoConstruccionTabla.put("agente", "Agente:270");
        
        ModelAndView x = new ModelAndView("crmcontactos/startup", "title", "Cat&aacute;logo de Contactos");
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
        
        x = x.addObject("iu", codificado);
        
        return x;
    }
    
   @RequestMapping(method = RequestMethod.POST, value="/getContacto.json")
    public @ResponseBody HashMap<String,ArrayList<HashMap<String, String>>> getContactoJson(
            @RequestParam(value="id", required=true) Integer id,
            @RequestParam(value="iu", required=true) String id_user_cod,
            Model model
            ){
        
        log.log(Level.INFO, "Ejecutando getContacto de {0}", CrmContactosController.class.getName());
        HashMap<String,ArrayList<HashMap<String, String>>> jsonretorno = new HashMap<String,ArrayList<HashMap<String, String>>>();
        HashMap<String, String> userDat = new HashMap<String, String>();
        ArrayList<HashMap<String, String>> arrayExtra = new ArrayList<HashMap<String, String>>();
        HashMap<String, String> extra = new HashMap<String, String>();
        
        ArrayList<HashMap<String, String>> datosContacto = new ArrayList<HashMap<String, String>>(); 
        
        //decodificar id de usuario
        Integer id_usuario = Integer.parseInt(Base64Coder.decodeString(id_user_cod));
        userDat = this.getHomeDao().getUserById(id_usuario);
        Integer id_empresa = Integer.parseInt(userDat.get("empresa_id"));
        Integer id_agente = Integer.parseInt(userDat.get("empleado_id"));
        
        // Integer id = Integer.parseInt(userDat.get("id"));
        
        //System.out.println("Esto es trae la ID ----->"+id);
        if( id != 0 ){
            datosContacto = this.getCrmDao().getContacto_Datos(id);
        }
        
        extra = this.getCrmDao().getUserRol(id_usuario);
        extra.put("id_agente", String.valueOf(id_agente));
        arrayExtra.add(0,extra);
        
       //datos motivos de llamada es lo que me trajo de la consulta y los pone en el json
       jsonretorno.put("Contacto", datosContacto);
       jsonretorno.put("Extra", arrayExtra);
       jsonretorno.put("Agentes", this.getCrmDao().getAgentes(id_empresa));
       return jsonretorno;
    }
   
   //Muestra los datos en el Grid
    @RequestMapping(value="/getContactos.json", method = RequestMethod.POST)
     public @ResponseBody HashMap<String,ArrayList<HashMap<String, Object>>> getContactosJson(
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
        HashMap<String, String> userDat = new HashMap<String, String>();
        //catalogo de contactos
        Integer app_selected = 127;
        
        //decodificar id de usuario
        Integer id_usuario = Integer.parseInt(Base64Coder.decodeString(id_user_cod));
        userDat = this.getHomeDao().getUserById(id_usuario);
        Integer id_empresa = Integer.parseInt(userDat.get("empresa_id"));
        
        //variables para el buscador
        String nombre = "%"+StringHelper.isNullString(String.valueOf(has_busqueda.get("nombre")))+"%";
        String busquedatipo_contacto = String.valueOf(has_busqueda.get("busquedatipo_contacto"));
        String busqueda_agente = String.valueOf(has_busqueda.get("busqueda_agente"));
        
        String data_string = app_selected+"___"+id_usuario+"___"+nombre+"___"+busquedatipo_contacto+"___"+busqueda_agente;
        
        //System.out.println("Esto es lo que busca "+"---->"+data_string);
        
        //obtiene total de registros en base de datos, con los parametros de busqueda
        int total_items = this.getCrmDao().countAll(data_string);              
        
        //calcula el total de paginas
        int total_pags = resource.calculaTotalPag(total_items,items_por_pag);
        
        //variables que necesita el datagrid, para no tener que hacer uno por cada aplicativo
        DataPost dataforpos = new DataPost(orderby, desc, items_por_pag, pag_start, display_pag, input_json, cadena_busqueda,total_items,total_pags, id_user_cod);
        
        int offset = resource.__get_inicio_offset(items_por_pag, pag_start);
        
        //obtiene los registros para el grid, de acuerdo a los parametros de busqueda
        jsonretorno.put("Data", this.getCrmDao().getContactos_PaginaGrid(data_string, offset, items_por_pag, orderby, desc,id_empresa));
        //System.out.println("Me imprime ---->"+jsonretorno);   
        
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
    
    
    //crear y editar un motivo de llamada
    @RequestMapping(method = RequestMethod.POST, value="/edit.json")
    public @ResponseBody HashMap<String, String> editJson(
            @RequestParam(value="identificador", required=true) Integer id,
            @RequestParam(value="tipo_contacto", required=true) String tipo_contacto,
            @RequestParam(value="folio", required=true) String folio,
            @RequestParam(value="id_cliente", required=true) String id_cliente,
            @RequestParam(value="nombre", required=true) String nombre,
            @RequestParam(value="apellido_paterno", required=true) String apellido_paterno,
            @RequestParam(value="apellido_materno", required=true) String apellido_materno,
            @RequestParam(value="telefono_2", required=true) String telefono_2,
            @RequestParam(value="telefono_1", required=true) String telefono_1,
            @RequestParam(value="fax", required=true) String fax,
            @RequestParam(value="telefono_directo", required=true) String telefono_directo,
            @RequestParam(value="correo_1", required=true) String correo_1,
            @RequestParam(value="correo_2", required=true) String correo_2,
            @RequestParam(value="observaciones", required=true) String observaciones,
            @RequestParam(value="select_agente", required=true) String agente,
            @RequestParam(value="check_decisor", required=false) String check_decisor,
            @RequestParam(value="departamento", required=true) String departamento,
            @RequestParam(value="puesto", required=true) String puesto,
            @ModelAttribute("user") UserSessionData user,
            Model model
        ) {	

        HashMap<String, String> jsonretorno = new HashMap<String, String>();
        HashMap<String, String> succes = new HashMap<String, String>();
        Integer app_selected = 127;//catalogo de contactos
        String command_selected = "new";
        //decodificar id de usuario
        Integer id_usuario = user.getUserId();
        
        String extra_data_array = "'sin datos'";
        String actualizo = "0";
        
        if(id==0){
            command_selected="new";
        }else{
           command_selected = "edit"; 
        }
        
        check_decisor = StringHelper.verificarCheckBox(check_decisor);
        
        String data_string = app_selected+"___"+command_selected+"___"+id_usuario+"___"+id+"___"+tipo_contacto+"___"+folio
                +"___"+id_cliente+"___"+nombre.trim().toUpperCase()+"___"+apellido_paterno.trim().toUpperCase()+"___"+apellido_materno.trim().toUpperCase()+"___"+telefono_1.trim()
                +"___"+telefono_2.trim()+"___"+fax.trim()+"___"+telefono_directo.trim()+"___"+correo_1.trim()+"___"+correo_2.trim()+"___"+
                observaciones.trim().toUpperCase()+"___"+
                agente+"___"+
                departamento.trim().toUpperCase()+"___"+
                puesto.trim().toUpperCase()+"___"+
                check_decisor;
        
        //System.out.println("La cadena que se envia:"+"___"+data_string);
        
        succes = this.getCrmDao().selectFunctionValidateAaplicativo(data_string, app_selected, extra_data_array);
        log.log(Level.INFO, "despues de validacion {0}", String.valueOf(succes.get("success")));
        if( String.valueOf(succes.get("success")).equals("true") ){
            actualizo = this.getCrmDao().selectFunctionForCrmAdmProcesos(data_string, extra_data_array);
        }
        
        jsonretorno.put("success",String.valueOf(succes.get("success")));
        
        log.log(Level.INFO, "Salida json {0}", String.valueOf(jsonretorno.get("success")));
        return jsonretorno;
    }
     
    //cambia el estatus del borrado logico
        @RequestMapping(method = RequestMethod.POST, value="/logicDelete.json")
        public @ResponseBody HashMap<String, String> logicDeleteJson(
                @RequestParam(value="id", required=true) Integer id,
                @RequestParam(value="iu", required=true) String id_user,
                Model model
            ) {
        
        HashMap<String, String> jsonretorno = new HashMap<String, String>();
        //decodificar id de usuario
        Integer id_usuario = Integer.parseInt(Base64Coder.decodeString(id_user));
        
        Integer app_selected = 127;
        String command_selected = "delete";
        String extra_data_array = "'sin datos'";
        String data_string = app_selected+"___"+command_selected+"___"+id_usuario+"___"+id;
        
        
        jsonretorno.put("success",String.valueOf( this.getCrmDao().selectFunctionForCrmAdmProcesos(data_string, extra_data_array)));
        return jsonretorno;
    }
    
    
}
