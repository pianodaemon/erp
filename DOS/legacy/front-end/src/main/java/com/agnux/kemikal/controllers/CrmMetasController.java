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
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.servlet.ModelAndView;


@Controller
@SessionAttributes({"user"})
@RequestMapping("/crmmetas/")
public class CrmMetasController {
    ResourceProject resource = new ResourceProject();
    private static final Logger log  = Logger.getLogger(CrmMetasController.class.getName());
    
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
        
        log.log(Level.INFO, "Ejecutando starUp de {0}", CrmMetasController.class.getName());
        LinkedHashMap<String,String> infoConstruccionTabla = new LinkedHashMap<String,String>();
        infoConstruccionTabla.put("id", "Acciones:70");
        infoConstruccionTabla.put("folio", "Folio:100");
        infoConstruccionTabla.put("agente","Agente:300");
        infoConstruccionTabla.put("ano","A&ntilde;o:100");
        infoConstruccionTabla.put("mes","Mes:100");
        
        
        ModelAndView x = new ModelAndView("crmmetas/startup", "title", "Registro&nbsp;de&nbsp;Metas");
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
    
    //Muestra los datos en el Grid
    @RequestMapping(value="/getAllCodigos.json", method = RequestMethod.POST)
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
        
        
        //aplicativo catalogo de marcas 
        Integer app_selected = 123;
        
        //decodificar id de usuario
        Integer id_usuario = Integer.parseInt(Base64Coder.decodeString(id_user_cod));
        userDat = this.getHomeDao().getUserById(id_usuario);
        Integer id_empresa = Integer.parseInt(userDat.get("empresa_id"));
        //variables para el buscador
        //String descripcion = "%"+StringHelper.isNullString(String.valueOf(has_busqueda.get("descripcion")))+"%";
        String agente = String.valueOf(has_busqueda.get("agente"));
        
        String data_string = app_selected+"___"+id_usuario+"___"+agente;
        
        //System.out.println("Esto es lo que busca "+"---->"+data_string);
        
        //obtiene total de registros en base de datos, con los parametros de busqueda
        int total_items = this.getCrmDao().countAll(data_string);              
         
        //System.out.println("Cantidad en el COUNT"+total_items);
        
        //calcula el total de paginas
        int total_pags = resource.calculaTotalPag(total_items,items_por_pag);
        
        //variables que necesita el datagrid, para no tener que hacer uno por cada aplicativo
        DataPost dataforpos = new DataPost(orderby, desc, items_por_pag, pag_start, display_pag, input_json, cadena_busqueda,total_items,total_pags, id_user_cod);
        
        int offset = resource.__get_inicio_offset(items_por_pag, pag_start);
       
        //obtiene los registros para el grid, de acuerdo a los parametros de busqueda
       
        jsonretorno.put("Data", this.getCrmDao().getRegistroMetas_PaginaGrid(data_string, offset, items_por_pag, orderby, desc));
        
        //obtiene el hash para los datos que necesita el datagrid
        jsonretorno.put("DataForGrid", dataforpos.formaHashForPos(dataforpos));
        //obtiene los agentes para el buscador
         
        
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
    //obtiene los agentes
    @RequestMapping(method = RequestMethod.POST, value="/getMetas.json")
    public @ResponseBody HashMap<String,ArrayList<HashMap<String, String>>> getRegistroMetasJson(
            @RequestParam(value="id", required=true) Integer id,
            @RequestParam(value="iu", required=true) String id_user,
            Model model
            ) {
        
        log.log(Level.INFO, "Ejecutando getRegistroMetasJson de {0}", CrmMetasController.class.getName());
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
            datos = this.getCrmDao().getCrmRegistoMetas_Datos(id);
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
    
    //crear y editar un cliente
    @RequestMapping(method = RequestMethod.POST, value="/edit.json")
    public @ResponseBody HashMap<String, String> editJson(
        @RequestParam(value="identificador", required=true) String identificador,
        @RequestParam(value="agente", required=true) String select_agente,
        
        @RequestParam(value="opciones", required=true) String opciones,
        @RequestParam(value="cant_llamadas", required=true) String cant_llamadas,
        @RequestParam(value="cant_visitas", required=true) String cant_visitas,
        @RequestParam(value="cant_prospectos", required=true) String cant_prospectos,
        @RequestParam(value="ano", required=true) String ano,
        @RequestParam(value="cant_cotizaciones", required=true) String cant_cotizaciones,
        @RequestParam(value="cant_cotizaciones2", required=true) String cant_cotizaciones2,
        @RequestParam(value="cant_oportunidades", required=true) String cant_oportunidades,
        @RequestParam(value="cant_oportunidades2", required=true) String cant_oportunidades2,
        @RequestParam(value="mes", required=true) String mes,
        @RequestParam(value="monto_cotizaciones", required=true) String monto_cotizaciones,
        @RequestParam(value="monto_cotizaciones2", required=true) String monto_cotizaciones2,
        @RequestParam(value="monto_oportunidades", required=true) String monto_oportunidades,
        @RequestParam(value="monto_oportunidades2", required=true) String monto_oportunidades2,
        @RequestParam(value="ventas_clientes", required=true) String ventas_clientes,
        @RequestParam(value="ventas_opor_clientes", required=true) String ventas_opor_clientes,
        @RequestParam(value="ventas_prospecto", required=true) String ventas_prospecto,
        
        
        Model model,@ModelAttribute("user") UserSessionData user
        ) {
        Integer app_selected = 123;//Aplicativo de Registro de Metas
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
        +"___"+opciones//6
        +"___"+cant_llamadas//7
        +"___"+cant_visitas//8
        +"___"+cant_prospectos//9
        +"___"+ano//10
        +"___"+mes//11
        +"___"+cant_cotizaciones	//12
        +"___"+cant_cotizaciones2//13
        +"___"+cant_oportunidades//14
        +"___"+cant_oportunidades2//15
        +"___"+monto_cotizaciones//16
        +"___"+monto_cotizaciones2//17
        +"___"+monto_oportunidades//18
        +"___"+monto_oportunidades2//19
        +"___"+ventas_clientes//20
        +"___"+ventas_opor_clientes//21	
        +"___"+ventas_prospecto;//22	
        
        //System.out.println("Esto estoy enviando: "+data_string);
        
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
        
        System.out.println("Borrado logico de Registro de Metas");
        //decodificar id de usuario
        Integer id_usuario = Integer.parseInt(Base64Coder.decodeString(id_user));
        
        Integer app_selected = 123;//Aplicativo de Registro de Visitas
        String command_selected = "delete";
        String extra_data_array = "'sin datos'";
        
        String data_string = app_selected+"___"+command_selected+"___"+id_usuario+"___"+id;
        
        HashMap<String, String> jsonretorno = new HashMap<String, String>();
        
        jsonretorno.put("success",String.valueOf( this.getCrmDao().selectFunctionForCrmAdmProcesos(data_string,extra_data_array)) );
        
        return jsonretorno;
    }
    
    
}
