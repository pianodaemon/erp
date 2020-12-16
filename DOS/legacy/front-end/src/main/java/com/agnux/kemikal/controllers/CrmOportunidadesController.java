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
@RequestMapping("/crmoportunidades/")
public class CrmOportunidadesController {
    ResourceProject resource = new ResourceProject();
    private static final Logger log  = Logger.getLogger(CrmOportunidadesController.class.getName());
    
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
        
        log.log(Level.INFO, "Ejecutando starUp de {0}", CrmOportunidadesController.class.getName());
        LinkedHashMap<String,String> infoConstruccionTabla = new LinkedHashMap<String,String>();
        infoConstruccionTabla.put("id", "Acciones:70");
        infoConstruccionTabla.put("accesor_empleado", "Agente:250");
        infoConstruccionTabla.put("accesor_contacto", "Contacto:250");
        infoConstruccionTabla.put("fecha_oportunidad", "Fecha Oportunidad:120");
        infoConstruccionTabla.put("accesos_etapa", "Etapa:150");
        infoConstruccionTabla.put("monto", "Monto:100");
        infoConstruccionTabla.put("estatus", "Estatus:100");
        
        /*
        gral_emp_id
        fecha_oportunidad
        crm_tipos_oportunidad_id
        monto
        estatus
        */
        ModelAndView x = new ModelAndView("crmoportunidades/startup", "title", "Oportunidades");
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
    
   @RequestMapping(method = RequestMethod.POST, value="/get_oportunidad.json")
    public @ResponseBody HashMap<String,ArrayList<HashMap<String, String>>> getMotivoVisitaJson(
            @RequestParam(value="id", required=true) Integer id,
            @RequestParam(value="iu", required=true) String id_user_cod,
            Model model
            ){
        
        log.log(Level.INFO, "Ejecutando getMotivoVisitaJson de {0}", CrmOportunidadesController.class.getName());
        HashMap<String,ArrayList<HashMap<String, String>>> jsonretorno = new HashMap<String,ArrayList<HashMap<String, String>>>();
        ArrayList<HashMap<String, String>> dataEmpleado = new ArrayList<HashMap<String, String>>();
        HashMap<String, String> userDat = new HashMap<String, String>();
       
        ArrayList<HashMap<String, String>> datosOportunidad = new ArrayList<HashMap<String, String>>(); 
        
        //decodificar id de usuario
        Integer id_usuario = Integer.parseInt(Base64Coder.decodeString(id_user_cod));
        userDat = this.getHomeDao().getUserById(id_usuario);
        
       // Integer id = Integer.parseInt(userDat.get("id"));
       
        //System.out.println("Esto es trae la ID ----->"+id);
        if( id != 0 ){
            datosOportunidad = this.getCrmDao().getOportunidad_Datos(id);
            dataEmpleado.add(0, this.getHomeDao().getUserById(Integer.parseInt(datosOportunidad.get(0).get("gral_empleados_id"))));
            //System.out.println("Esto es lo que estoy mostrando:"+"___"+datosOportunidad);
        }else{
            dataEmpleado.add(0, this.getHomeDao().getUserById(id_usuario));
        }
        
       //datos motivos visitas es lo que me trajo de la consulta y los pone en el json
       
       jsonretorno.put("Oportunidad", datosOportunidad);
       jsonretorno.put("Session", dataEmpleado);
       jsonretorno.put("Agentes", this.getCrmDao().getAgentes(Integer.parseInt(userDat.get("empresa_id"))));
       jsonretorno.put("EtapasVenta", this.getCrmDao().getEtapasVenta());
       jsonretorno.put("TiposOportunidad", this.getCrmDao().getTiposOportunidad());
       
       return jsonretorno;
       
    }
   
   //Muestra los datos en el Grid
    @RequestMapping(value="/getAllOpotunidades.json", method = RequestMethod.POST)
     public @ResponseBody HashMap<String,ArrayList<HashMap<String, Object>>> getAllCodigosJson(
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
        //aplicativo Oportunidades
        Integer app_selected = 120;
        
        //decodificar id de usuario
        Integer id_usuario = Integer.parseInt(Base64Coder.decodeString(id_user_cod));
        userDat = this.getHomeDao().getUserById(id_usuario);
        Integer id_empresa = Integer.parseInt(userDat.get("empresa_id"));
        //variables para el buscador
        String buscador_contacto = "%"+StringHelper.isNullString(String.valueOf(has_busqueda.get("buscador_contacto")))+"%";
        String buscador_etapa_venta = has_busqueda.get("buscador_etapa_venta");
        String buscador_tipo_oportunidad = has_busqueda.get("buscador_tipo_oportunidad");
        String buscador_agente = has_busqueda.get("buscador_agente");
        
        String data_string = app_selected+"___"+id_usuario+"___"+buscador_contacto+"___"+buscador_etapa_venta
                +"___"+buscador_tipo_oportunidad+"___"+buscador_agente;
        
        //System.out.println("Esto es lo que busca "+"---->"+data_string);
        
        //obtiene total de registros en base de datos, con los parametros de busqueda
        int total_items = this.getCrmDao().countAll(data_string);              
        
        //calcula el total de paginas
        int total_pags = resource.calculaTotalPag(total_items,items_por_pag);
        
        //variables que necesita el datagrid, para no tener que hacer uno por cada aplicativo
        DataPost dataforpos = new DataPost(orderby, desc, items_por_pag, pag_start, display_pag, input_json, cadena_busqueda,total_items,total_pags, id_user_cod);
        
        int offset = resource.__get_inicio_offset(items_por_pag, pag_start);
        
        //obtiene los registros para el grid, de acuerdo a los parametros de busqueda
        jsonretorno.put("Data", this.getCrmDao().getOportunidades_PaginaGrid(data_string, offset, items_por_pag, orderby, desc,id_empresa));
        //System.out.println("Me imprime ---->"+jsonretorno);   
        //obtiene el hash para los datos que necesita el datagrid
        jsonretorno.put("DataForGrid", dataforpos.formaHashForPos(dataforpos));
        
        return jsonretorno;
    }
    
    
    //obtiene los productos para el buscador
    @RequestMapping(method = RequestMethod.POST, value="/get_buscador_contactos.json")
    public @ResponseBody HashMap<String,ArrayList<HashMap<String, String>>> getBuscadorContactoJson(
            @RequestParam(value="buscador_nombre", required=true) String nombre,
            @RequestParam(value="buscador_apellidop", required=true) String apellidop,
            @RequestParam(value="buscador_apellidom", required=true) String apellidom,
            @RequestParam(value="buscador_tipo_contacto", required=true) String tipo_contacto,
            @RequestParam(value="iu", required=true) String id_user,
            Model model
            ) {
        
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
    
    
    /*jsonretorno.put("Agentes", this.getCrmDao().getAgentes(Integer.parseInt(userDat.get("empresa_id"))));
    jsonretorno.put (

    "EtapasVenta", this.getCrmDao().getEtapasVenta());
       jsonretorno.put (

    "TiposOportunidad", this.getCrmDao().getTiposOportunidad());
       */
    //obtiene los roles de EtapasVenta
    @RequestMapping(method=RequestMethod.POST,value="/getTiposOportunidad.json")
    public @ResponseBody HashMap<String,ArrayList<HashMap<String,String>>>getTiposOportunidadJson(
            @RequestParam(value="iu", required=true) String id_user_cod,
            Model model
            ) {
        HashMap<String,ArrayList<HashMap<String,String>>>jsonretorno= new HashMap<String,ArrayList<HashMap<String,String>>>();
        HashMap<String, String> userDat = new HashMap<String, String>();
        
        jsonretorno.put("TiposOportunidad",this.getCrmDao().getTiposOportunidad());
        
        return jsonretorno;
    }
    
    
    //obtiene los roles de EtapasVenta
    @RequestMapping(method=RequestMethod.POST,value="/getEtapasVenta.json")
    public @ResponseBody HashMap<String,ArrayList<HashMap<String,String>>>getEtapasVentaJson(
            @RequestParam(value="iu", required=true) String id_user_cod,
            Model model
            ) {
        HashMap<String,ArrayList<HashMap<String,String>>>jsonretorno= new HashMap<String,ArrayList<HashMap<String,String>>>();
        HashMap<String, String> userDat = new HashMap<String, String>();
        
        jsonretorno.put("EtapasVenta",this.getCrmDao().getEtapasVenta());
        
        return jsonretorno;
    }
    
    //obtiene los roles de getAgentes
    @RequestMapping(method=RequestMethod.POST,value="/getAgentes.json")
    public @ResponseBody HashMap<String,ArrayList<HashMap<String,String>>>getAgentesJson(
            @RequestParam(value="iu", required=true) String id_user_cod,
            Model model
            ) {
        HashMap<String,ArrayList<HashMap<String,String>>>jsonretorno= new HashMap<String,ArrayList<HashMap<String,String>>>();
        HashMap<String, String> userDat = new HashMap<String, String>();
        
        Integer id_usuario = Integer.parseInt(Base64Coder.decodeString(id_user_cod));
        userDat = this.getHomeDao().getUserById(id_usuario);
        
        jsonretorno.put("Agentes",this.getCrmDao().getAgentes(Integer.parseInt(userDat.get("empresa_id"))));
        
        return jsonretorno;
    }
    
    //crear y editar un motivo de visitas
    @RequestMapping(method = RequestMethod.POST, value="/edit.json")
    public @ResponseBody HashMap<String, String> editJson(
            @RequestParam(value="identificador", required=true) Integer id,
            @RequestParam(value="contacto_id", required=true) String contacto_id,
            @RequestParam(value="fecha_oportunidad", required=true) String fecha_oportunidad,
            @RequestParam(value="fecha_cotizacion", required=true) String fecha_cotizacion,
            @RequestParam(value="fecha_cierre", required=true) String fecha_cierre,
            @RequestParam(value="monto", required=true) String monto,
            //select
            @RequestParam(value="empleado", required=true) String empleado,
            @RequestParam(value="tipo_oportunidad", required=true) String tipo_oportunidad,
            @RequestParam(value="etapa_venta", required=true) String etapa_venta,
            @RequestParam(value="estatus", required=true) String estatus,
            @RequestParam(value="cierre_oportunidad", required=true) String cierre_oportunidad,
            @ModelAttribute("user") UserSessionData user,
            Model model
            ) {
        
        HashMap<String, String> jsonretorno = new HashMap<String, String>();
        HashMap<String, String> succes = new HashMap<String, String>();
        Integer app_selected = 120;//catalogo de oportunidades
        String command_selected = "new";
        //decodificar id de usuario
        Integer id_usuario = user.getUserId();
        
        String extra_data_array = "'sin datos'";
        String actualizo = "0";
        
        //System.out.println("Me imprime esto ----->"+id);
        if(id==0){
            command_selected="new";
        }else{
           command_selected = "edit"; 
        }
        
       
        String data_string = app_selected+"___"+command_selected+"___"+id_usuario+"___"+id+"___"+contacto_id+"___"+fecha_oportunidad+"___"+fecha_cotizacion
                +"___"+fecha_cierre+"___"+monto+"___"+empleado+"___"+tipo_oportunidad+"___"+etapa_venta+"___"+estatus+"___"+cierre_oportunidad;//6
        
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
        
        Integer app_selected = 120;//catalogo de oportunidades
        String command_selected = "delete";
        String extra_data_array = "'sin datos'";
        String data_string = app_selected+"___"+command_selected+"___"+id_usuario+"___"+id;
        
        
        jsonretorno.put("success",String.valueOf( this.getCrmDao().selectFunctionForThisApp(data_string, extra_data_array)));
        return jsonretorno;
    }
}
