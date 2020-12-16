package com.agnux.kemikal.controllers;


import com.agnux.cfd.v2.Base64Coder;
import com.agnux.common.helpers.StringHelper;
import com.agnux.common.obj.DataPost;
import com.agnux.common.obj.ResourceProject;
import com.agnux.common.obj.UserSessionData;
import com.agnux.kemikal.interfacedaos.HomeInterfaceDao;
import com.agnux.kemikal.interfacedaos.LogInterfaceDao;
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
@RequestMapping("/logoperadores/")
public class LogOperadoresController {
    
     ResourceProject resource = new ResourceProject();
    private static final Logger log  = Logger.getLogger(LogOperadoresController.class.getName());
    
    @Autowired
    @Qualifier("daoLog")   //utilizo todos los metodos de invinterfacedao
    private LogInterfaceDao logDao;
    
    public LogInterfaceDao getLogDao() {
        return logDao;
    }
    
    @Autowired
    @Qualifier("daoHome")   //permite controlar usuarios que entren
    private HomeInterfaceDao HomeDao;
    
    public HomeInterfaceDao getHomeDao() {
        return HomeDao;
    }
    
    @RequestMapping(value="/startup.agnux")
    public ModelAndView startUp(HttpServletRequest request, HttpServletResponse response,
     @ModelAttribute("user") UserSessionData user)throws ServletException, IOException {
        
        log.log(Level.INFO, "Ejecutando starUp de {0}", LogOperadoresController.class.getName());
        LinkedHashMap<String,String> infoConstruccionTabla = new LinkedHashMap<String,String>();
     
        infoConstruccionTabla.put("id", "Acciones:90");
        infoConstruccionTabla.put("numero_control", "No. Control:90");
        infoConstruccionTabla.put("nombre", "Nombre:350");
        infoConstruccionTabla.put("transportista", "Transportista:350");
        
        ModelAndView x = new ModelAndView("logoperadores/startup", "title", "Cat&aacute;logo de Operadores");
        
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
        String decodificado = Base64Coder.decodeString(codificado);
        
        //id de usuario codificado
        x = x.addObject("iu", codificado);
     
        return x;
    }
    
    
    
    @RequestMapping(method = RequestMethod.POST, value="/getInicializar.json")
    public @ResponseBody HashMap<String,Object> getCuentasMayorJson(
            @RequestParam(value="iu", required=true) String id_user,
            Model model
        ) {
        
        log.log(Level.INFO, "Ejecutando getInicializarJson de {0}", LogOperadoresController.class.getName());
        HashMap<String,Object> jsonretorno = new HashMap<String,Object>();
        HashMap<String, String> userDat = new HashMap<String, String>();
        HashMap<String, Object> data = new HashMap<String, Object>();
        
        //Decodificar id de usuario
        Integer id_usuario = Integer.parseInt(Base64Coder.decodeString(id_user));
        userDat = this.getHomeDao().getUserById(id_usuario);
        Integer id_empresa = Integer.parseInt(userDat.get("empresa_id"));
        Integer id_sucursal = Integer.parseInt(userDat.get("sucursal_id"));
        
        data.put("suc_id", id_sucursal);
        
        if(this.getLogDao().getUserRolAdmin(id_usuario)>0){
            data.put("versuc", true);
            //Si es administrador asignamos id de sucursal cero, para obtener todos los transportistas sin importar la empresa 
            id_sucursal=0;
        }else{
            data.put("versuc", false);
        }
        
        data.put("Suc", this.getLogDao().getSucursales(id_empresa));
        data.put("Trans", this.getLogDao().getTransportistas(id_empresa, id_sucursal));
        jsonretorno.put("Data", data);
        
        return jsonretorno;
    }
    
    
    
    @RequestMapping(method = RequestMethod.POST, value="/getOperador.json")
    public @ResponseBody HashMap<String,ArrayList<HashMap<String, String>>> getOperadorJson(
            @RequestParam(value="id", required=true) Integer id,
            @RequestParam(value="iu", required=true) String id_user_cod,
            Model model
            ){
        
        log.log(Level.INFO, "Ejecutando getOperadorJson de {0}", LogOperadoresController.class.getName());
        HashMap<String,ArrayList<HashMap<String, String>>> jsonretorno = new HashMap<String,ArrayList<HashMap<String, String>>>();
        HashMap<String, String> userDat = new HashMap<String, String>();
       
        ArrayList<HashMap<String, String>> datosOperador = new ArrayList<HashMap<String, String>>(); 
       
        //decodificar id de usuario
        Integer id_usuario = Integer.parseInt(Base64Coder.decodeString(id_user_cod));
        userDat = this.getHomeDao().getUserById(id_usuario);
        
        if( id != 0 ){
            datosOperador = this.getLogDao().getOperadores_Datos(id);
          
        }
        
       //datos marcas es lo que me trajo de la consulta y los pone en el json
       jsonretorno.put("Operador", datosOperador);
        
        return jsonretorno;
    }
    
    @RequestMapping(value="/getAllOperadores.json", method = RequestMethod.POST)
     public @ResponseBody HashMap<String,ArrayList<HashMap<String, Object>>> getAllOperadoresJson(
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
        
        //aplicativo catalogo de operadores 
        Integer app_selected = 154;
        
        //decodificar id de usuario
        Integer id_usuario = Integer.parseInt(Base64Coder.decodeString(id_user_cod));
        
        //Variables para el buscador
        String sucursal = StringHelper.isNullString(String.valueOf(has_busqueda.get("sucursal")));
        String transportista = StringHelper.isNullString(String.valueOf(has_busqueda.get("transportista")));
        String clave_operador = "%"+StringHelper.isNullString(String.valueOf(has_busqueda.get("clave_operador")))+"%";        
        String nombre = "%"+StringHelper.isNullString(String.valueOf(has_busqueda.get("nombre")))+"%";
        
        String data_string = app_selected+"___"+id_usuario+"___"+clave_operador+"___"+nombre+"___"+sucursal+"___"+transportista;
        
        //Obtiene total de registros en base de datos, con los parametros de busqueda
        int total_items = this.getLogDao().countAll(data_string);
        
        //Calcula el total de paginas
        int total_pags = resource.calculaTotalPag(total_items,items_por_pag);
        
        //Variables que necesita el datagrid, para no tener que hacer uno por cada aplicativo
        DataPost dataforpos = new DataPost(orderby, desc, items_por_pag, pag_start, display_pag, input_json, cadena_busqueda,total_items,total_pags, id_user_cod);
        
        int offset = resource.__get_inicio_offset(items_por_pag, pag_start);
        
        //Obtiene los registros para el grid, de acuerdo a los parametros de busqueda
        jsonretorno.put("Data", this.getLogDao().getOperadores_PaginaGrid(data_string, offset, items_por_pag, orderby, desc));
        
        //Obtiene el hash para los datos que necesita el datagrid
        jsonretorno.put("DataForGrid", dataforpos.formaHashForPos(dataforpos));
        
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
        
        Integer app_selected = 154;
        String command_selected = "delete";
        String extra_data_array = "'sin datos'";
        String data_string = app_selected+"___"+command_selected+"___"+id_usuario+"___"+id;
        
        jsonretorno.put("success",String.valueOf( this.getLogDao().selectFunctionForLogAdmProcesos(data_string,extra_data_array)) );
        
        return jsonretorno;
    }
    
    
     //crear y editar un operador
    @RequestMapping(method = RequestMethod.POST, value="/edit.json")
    public @ResponseBody HashMap<String, String> editJson(
            @RequestParam(value="identificador", required=true) Integer id,
            @RequestParam(value="nombre", required=true) String nombre,
            @RequestParam(value="apellido_paterno", required=true) String apellido_paterno,
            @RequestParam(value="apellido_materno", required=true) String apellido_materno,
            @RequestParam(value="select_transportista", required=true) String select_transportista,
            @ModelAttribute("user") UserSessionData user,
            Model model
            ) {
        
        HashMap<String, String> jsonretorno = new HashMap<String, String>();
        HashMap<String, String> succes = new HashMap<String, String>();
        Integer app_selected = 154;//catalogo de operadores
        String command_selected = "new";
        //decodificar id de usuario
        Integer id_usuario = user.getUserId();
        
        String extra_data_array = "'sin datos'";
        String actualizo = "0";
        
        if( id==0 ){
            command_selected = "new";
        }else{
            command_selected = "edit";
        }
        
        
        String data_string = app_selected+"___"+command_selected+"___"+id_usuario+"___"+id+"___"+nombre.toUpperCase()+"___"+apellido_paterno.toUpperCase()+"___"+apellido_materno.toUpperCase()+"___"+select_transportista;
        
        succes = this.getLogDao().selectFunctionValidateAaplicativo(data_string,app_selected,extra_data_array);
        //System.out.println("ESTO TRAE SUCEESSSS"+succes);
        log.log(Level.INFO, "despues de validacion {0}", String.valueOf(succes.get("success")));
        if( String.valueOf(succes.get("success")).equals("true") ){
            actualizo = this.getLogDao().selectFunctionForLogAdmProcesos(data_string, extra_data_array);
        }
        
        jsonretorno.put("success",String.valueOf(succes.get("success")));
        
        log.log(Level.INFO, "Salida json {0}", String.valueOf(jsonretorno.get("success")));
        return jsonretorno;
    }
    
    
    
    
    
    
    
    
    
    
    
}
