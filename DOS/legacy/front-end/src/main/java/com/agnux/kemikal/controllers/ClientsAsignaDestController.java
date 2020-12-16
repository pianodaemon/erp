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
import org.apache.commons.lang.StringUtils;
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
@RequestMapping("/clientsasignadest/")
public class ClientsAsignaDestController {
    private static final Logger log  = Logger.getLogger(ClientsAsignaDestController.class.getName());
    ResourceProject resource = new ResourceProject();
    
    @Autowired
    @Qualifier("daoCxc")
    private CxcInterfaceDao cxcDao;
    
    public CxcInterfaceDao getCxcDao() {
        return cxcDao;
    }
    
    public void setCxcDao(CxcInterfaceDao cxcDao) {
        this.cxcDao = cxcDao;
    }
    
    @Autowired
    @Qualifier("daoHome")
    private HomeInterfaceDao HomeDao;
    
    public HomeInterfaceDao getHomeDao() {
        return HomeDao;
    }
    
    @RequestMapping(value="/startup.agnux")
    public ModelAndView startUp(HttpServletRequest request, HttpServletResponse response, @ModelAttribute("user") UserSessionData user)
            throws ServletException, IOException {
        
        log.log(Level.INFO, "Ejecutando starUp de {0}", ClientsAsignaDestController.class.getName());
        LinkedHashMap<String,String> infoConstruccionTabla = new LinkedHashMap<String,String>();
        
        infoConstruccionTabla.put("id", "Acciones:90");
        infoConstruccionTabla.put("numero_control", "No.&nbsp;Cliente:100");
        infoConstruccionTabla.put("razon_social", "Razon&nbsp;social:300");
        infoConstruccionTabla.put("rfc", "RFC:100");
        
        ModelAndView x = new ModelAndView("clientsasignadest/startup", "title", "Asignaci&oacute;n de Destinatarios");
        
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
    
    
    
    //Obtener todos los Clientes con Remitentes Asignados
    @RequestMapping(value="/getAllAsignacion.json", method = RequestMethod.POST)
    public @ResponseBody HashMap<String,ArrayList<HashMap<String, Object>>> getAllAsignacionJson(
           @RequestParam(value="orderby", required=true) String orderby,
           @RequestParam(value="desc", required=true) String desc,
           @RequestParam(value="items_por_pag", required=true) int items_por_pag,
           @RequestParam(value="pag_start", required=true) int pag_start,
           @RequestParam(value="display_pag", required=true) String display_pag,
           @RequestParam(value="input_json", required=true) String input_json,
           @RequestParam(value="cadena_busqueda", required=true) String cadena_busqueda,
           @RequestParam(value="iu", required=true) String id_user_cod,
           Model modcel
        ) {
        
        HashMap<String,ArrayList<HashMap<String, Object>>> jsonretorno = new HashMap<String,ArrayList<HashMap<String, Object>>>();
        HashMap<String,String> has_busqueda = StringHelper.convert2hash(StringHelper.ascii2string(cadena_busqueda));
        
        //Asignacion de Destinatarios
        Integer app_selected = 151;
        
        //Decodificar id de usuario
        Integer id_usuario = Integer.parseInt(Base64Coder.decodeString(id_user_cod));
        
        //variables para el buscador
        String nocontrol = ""+StringHelper.isNullString(String.valueOf(has_busqueda.get("nocontrol")))+"";
        String razonsoc = "%"+StringHelper.isNullString(String.valueOf(has_busqueda.get("razonsoc")))+"%";
        String rfc = "%"+StringHelper.isNullString(String.valueOf(has_busqueda.get("rfc")))+"%";
        
        String data_string = app_selected+"___"+id_usuario+"___"+nocontrol.toUpperCase()+"___"+razonsoc+"___"+rfc;
        
        //obtiene total de registros en base de datos, con los parametros de busqueda
        int total_items = this.getCxcDao().countAll(data_string);
        
        //calcula el total de paginas
        int total_pags = resource.calculaTotalPag(total_items,items_por_pag);
        
        //variables que necesita el datagrid, para no tener que hacer uno por cada aplicativo
        DataPost dataforpos = new DataPost(orderby, desc, items_por_pag, pag_start, display_pag, input_json, cadena_busqueda,total_items,total_pags, id_user_cod);
        
        int offset = resource.__get_inicio_offset(items_por_pag, pag_start);
        
        //obtiene los registros para el grid, de acuerdo a los parametros de busqueda
        jsonretorno.put("Data", this.getCxcDao().getClientsAsignaRem_PaginaGrid(data_string, offset, items_por_pag, orderby, desc));
        
        //obtiene el hash para los datos que necesita el datagrid
        jsonretorno.put("DataForGrid", dataforpos.formaHashForPos(dataforpos));
        
        return jsonretorno;
    }
    
    
    
    @RequestMapping(method = RequestMethod.POST, value="/getAsignacion.json")
    public @ResponseBody HashMap<String,ArrayList<HashMap<String, Object>>> getAsignacionJson(
            @RequestParam(value="id", required=true) Integer id,
            @RequestParam(value="iu", required=true) String id_user,
            Model model
        ) {
        
        log.log(Level.INFO, "Ejecutando getAsignacionJson de {0}", ClientsAsignaDestController.class.getName());
        HashMap<String,ArrayList<HashMap<String, Object>>> jsonretorno = new HashMap<String,ArrayList<HashMap<String, Object>>>();
        HashMap<String, String> userDat = new HashMap<String, String>();
        ArrayList<HashMap<String, Object>> datos = new ArrayList<HashMap<String, Object>>();
        ArrayList<HashMap<String, Object>> grid = new ArrayList<HashMap<String, Object>>();
        
        //decodificar id de usuario
        Integer id_usuario = Integer.parseInt(Base64Coder.decodeString(id_user));
        userDat = this.getHomeDao().getUserById(id_usuario);
        
        if( id != 0  ){
            datos = this.getCxcDao().getClientsAsignaDest_Datos(id);
            grid = this.getCxcDao().getClientsAsignaDest_DestinatariosAsignados(id);
        }
        
        jsonretorno.put("Datos", datos);
        jsonretorno.put("Grid", grid);
        return jsonretorno;
    }
    
    
    
    
    @RequestMapping(method = RequestMethod.POST, value="/getDestinatariosAsignados.json")
    public @ResponseBody HashMap<String,ArrayList<HashMap<String, Object>>> getDestinatariosAsignadosJson(
            @RequestParam(value="id", required=true) Integer id,
            @RequestParam(value="iu", required=true) String id_user,
            Model model
        ) {
        
        log.log(Level.INFO, "Ejecutando getDestinatariosAsignadosJson de {0}", ClientsAsignaDestController.class.getName());
        HashMap<String,ArrayList<HashMap<String, Object>>> jsonretorno = new HashMap<String,ArrayList<HashMap<String, Object>>>();
        HashMap<String, String> userDat = new HashMap<String, String>();
        ArrayList<HashMap<String, Object>> grid = new ArrayList<HashMap<String, Object>>();
        
        //decodificar id de usuario
        Integer id_usuario = Integer.parseInt(Base64Coder.decodeString(id_user));
        
        if( id != 0  ){
            grid = this.getCxcDao().getClientsAsignaDest_DestinatariosAsignados(id);
        }
        
        jsonretorno.put("Asignados", grid);
        return jsonretorno;
    }
    
    
    
    
    
    
    
    //Buscador de clientes
    @RequestMapping(method = RequestMethod.POST, value="/getBuscadorClientes.json")
    public @ResponseBody HashMap<String,ArrayList<HashMap<String, Object>>> getBuscadorClientesJson(
            @RequestParam(value="cadena", required=true) String cadena,
            @RequestParam(value="filtro", required=true) Integer filtro,
            @RequestParam(value="iu", required=true) String id_user,
            Model model
            ) {
        
        HashMap<String,ArrayList<HashMap<String, Object>>> jsonretorno = new HashMap<String,ArrayList<HashMap<String, Object>>>();
        HashMap<String, String> userDat = new HashMap<String, String>();
        
        //decodificar id de usuario
        Integer id_usuario = Integer.parseInt(Base64Coder.decodeString(id_user));
        //System.out.println("id_usuario: "+id_usuario);
        
        userDat = this.getHomeDao().getUserById(id_usuario);
        
        Integer id_empresa = Integer.parseInt(userDat.get("empresa_id"));
        Integer id_sucursal = 0;//se le asigna cero para que no filtre por sucursal
        
        jsonretorno.put("Clientes", this.getCxcDao().getBuscadorClientes(cadena,filtro,id_empresa, id_sucursal));
        
        return jsonretorno;
    }
    
    
    
    //Obtener datos del cliente a partir del Numero de Control
    @RequestMapping(method = RequestMethod.POST, value="/getDataByNoClient.json")
    public @ResponseBody HashMap<String,ArrayList<HashMap<String, Object>>> getDataByNoClientJson(
            @RequestParam(value="no_control", required=true) String no_control,
            @RequestParam(value="iu", required=true) String id_user,
            Model model
        ) {
        
        HashMap<String,ArrayList<HashMap<String, Object>>> jsonretorno = new HashMap<String,ArrayList<HashMap<String, Object>>>();
        HashMap<String, String> userDat = new HashMap<String, String>();
       
        //decodificar id de usuario
        Integer id_usuario = Integer.parseInt(Base64Coder.decodeString(id_user));
        
        userDat = this.getHomeDao().getUserById(id_usuario);
        
        Integer id_empresa = Integer.parseInt(userDat.get("empresa_id"));
        //Integer id_sucursal = Integer.parseInt(userDat.get("sucursal_id"));
        Integer id_sucursal = 0;
        
        jsonretorno.put("Cliente", this.getCxcDao().getDatosClienteByNoCliente(no_control, id_empresa, id_sucursal));
        
        return jsonretorno;
    }
    
    
    //Buscador de Destinatarios
    @RequestMapping(method = RequestMethod.POST, value="/getBuscadorDestinatarios.json")
    public @ResponseBody HashMap<String,ArrayList<HashMap<String, Object>>> getBuscadorDestinatariosJson(
            @RequestParam(value="cadena", required=true) String cadena,
            @RequestParam(value="filtro", required=true) Integer filtro,
            @RequestParam(value="iu", required=true) String id_user,
            Model model
            ) {
        
        HashMap<String,ArrayList<HashMap<String, Object>>> jsonretorno = new HashMap<String,ArrayList<HashMap<String, Object>>>();
        HashMap<String, String> userDat = new HashMap<String, String>();
        
        //decodificar id de usuario
        Integer id_usuario = Integer.parseInt(Base64Coder.decodeString(id_user));
        //System.out.println("id_usuario: "+id_usuario);
        
        userDat = this.getHomeDao().getUserById(id_usuario);
        
        Integer id_empresa = Integer.parseInt(userDat.get("empresa_id"));
        Integer id_sucursal = 0;//se le asigna cero para que no filtre por sucursal
        
        jsonretorno.put("Remitentes", this.getCxcDao().getBuscadorDestinatarios(cadena,filtro,id_empresa, id_sucursal));
        
        return jsonretorno;
    }
    
    
    //Obtener datos del Destinatario a partir del Numero de Control
    @RequestMapping(method = RequestMethod.POST, value="/getDataByNoDestinatario.json")
    public @ResponseBody HashMap<String,ArrayList<HashMap<String, Object>>> getDataByNoDestinatarioJson(
            @RequestParam(value="no_control", required=true) String no_control,
            @RequestParam(value="iu", required=true) String id_user,
            Model model
        ) {
        
        HashMap<String,ArrayList<HashMap<String, Object>>> jsonretorno = new HashMap<String,ArrayList<HashMap<String, Object>>>();
        HashMap<String, String> userDat = new HashMap<String, String>();
       
        //decodificar id de usuario
        Integer id_usuario = Integer.parseInt(Base64Coder.decodeString(id_user));
        
        userDat = this.getHomeDao().getUserById(id_usuario);
        
        Integer id_empresa = Integer.parseInt(userDat.get("empresa_id"));
        //Integer id_sucursal = Integer.parseInt(userDat.get("sucursal_id"));
        Integer id_sucursal = 0;
        
        jsonretorno.put("Dest", this.getCxcDao().getDatosByNoDestinatario(no_control, id_empresa, id_sucursal));
        
        return jsonretorno;
    }
    
    
    
    
    //crear y editar
    @RequestMapping(method = RequestMethod.POST, value="/edit.json")
    public @ResponseBody HashMap<String, String> editJson(
        @RequestParam(value="identificador", required=true) String identificador,
        @RequestParam(value="id_cliente", required=true) String id_cliente,
        @RequestParam(value="eliminado", required=false) String[] eliminado,
        @RequestParam(value="iddet", required=false) String[] iddet,
        @RequestParam(value="idcli", required=false) String[] idcli,
        @RequestParam(value="iddest", required=false) String[] iddest,
        @RequestParam(value="noTr", required=false) String[] noTr,
        Model model,@ModelAttribute("user") UserSessionData user
    ) {
        //Asignacion de Destinatarios
        Integer app_selected = 151;
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
        
        arreglo = new String[eliminado.length];
        for(int i=0; i<eliminado.length; i++) { 
            arreglo[i]= "'"+eliminado[i] +"___" + iddet[i] +"___" + idcli[i] +"___" + iddest[i] +"___" + noTr[i]+"'";
            System.out.println(arreglo[i]);
        }
        
        //Serializar el arreglo
        extra_data_array = StringUtils.join(arreglo, ",");
        
        String data_string = app_selected+"___"+command_selected+"___"+id_usuario+"___"+identificador+"___"+id_cliente;
        
        System.out.println("data_string: "+data_string);
        
        succes = this.getCxcDao().selectFunctionValidateAaplicativo(data_string,app_selected,extra_data_array);
        
        log.log(Level.INFO, "despues de validacion {0}", String.valueOf(succes.get("success")));
        if( String.valueOf(succes.get("success")).equals("true") ){
            actualizo = this.getCxcDao().selectFunctionForCxcAdmProcesos(data_string, extra_data_array);
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
        
        System.out.println("Eliminar registro de Asignacion de Destinatarios");
        //decodificar id de usuario
        Integer id_usuario = Integer.parseInt(Base64Coder.decodeString(id_user));
        
        //Asignacion de Destinatarios
        Integer app_selected = 151;
        String command_selected = "delete";
        String extra_data_array = "'sin datos'";
        
        String data_string = app_selected+"___"+command_selected+"___"+id_usuario+"___"+id;
        
        HashMap<String, String> jsonretorno = new HashMap<String, String>();
        
        jsonretorno.put("success",String.valueOf( this.getCxcDao().selectFunctionForCxcAdmProcesos(data_string,extra_data_array)) );
        
        return jsonretorno;
    }
}
