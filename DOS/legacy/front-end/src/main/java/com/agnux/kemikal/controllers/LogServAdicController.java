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
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;


/*
 * Catalogo de Servicios Adicionales para el viaje
 * Toma productos tipo Servicios del catálogo de productos y las mete en una tabla con la finalidad 
 * de conocer cuales servicios se deben utilizar como servicios adicionales en el viaje.
 * 
 */
@Controller
@SessionAttributes({"user"})
@RequestMapping("/logservadic/")
public class LogServAdicController {
     ResourceProject resource = new ResourceProject();
    private static final Logger log  = Logger.getLogger(LogServAdicController.class.getName());
    
    @Autowired
    @Qualifier("daoLog")
    private LogInterfaceDao logDao;
    
    public LogInterfaceDao getLogDao() {
        return logDao;
    }
    
    @Autowired
    @Qualifier("daoHome")
    private HomeInterfaceDao HomeDao;
    
    public HomeInterfaceDao getHomeDao() {
        return HomeDao;
    }
    
    @RequestMapping(value="/startup.agnux")
    public ModelAndView startUp(HttpServletRequest request, HttpServletResponse response,
     @ModelAttribute("user") UserSessionData user)throws ServletException, IOException {
        
        log.log(Level.INFO, "Ejecutando starUp de {0}", LogServAdicController.class.getName());
        LinkedHashMap<String,String> infoConstruccionTabla = new LinkedHashMap<String,String>();
        
        infoConstruccionTabla.put("id", "Acciones:90");
        infoConstruccionTabla.put("sku", "C&oacute;digo:100");
        infoConstruccionTabla.put("descripcion", "Descripci&oacute;n:330");
        infoConstruccionTabla.put("unidad", "UM:100");
        infoConstruccionTabla.put("tipo", "Tipo:110");
        
        ModelAndView x = new ModelAndView("logservadic/startup", "title", "Cat&aacute;logo de Servicios Adicionales");
        
        x = x.addObject("layoutheader", resource.getLayoutheader());
        x = x.addObject("layoutmenu", resource.getLayoutmenu());
        x = x.addObject("layoutfooter", resource.getLayoutfooter());
        x = x.addObject("grid", resource.generaGrid(infoConstruccionTabla));
        
        x = x.addObject("url", resource.getUrl(request));
        x = x.addObject("username", user.getUserName());
        x = x.addObject("empresa", user.getRazonSocialEmpresa());
        x = x.addObject("sucursal", user.getSucursal());
        
        String userId = String.valueOf(user.getUserId());
        
        //Codificar id de usuario
        String codificado = Base64Coder.encodeString(userId);
        
        //Decodificar id de usuario
        //String decodificado = Base64Coder.decodeString(codificado);
        
        //id de usuario codificado
        x = x.addObject("iu", codificado);
     
        return x;
    }
    
    
    @RequestMapping(value="/getAllServAdic.json", method = RequestMethod.POST)
     public @ResponseBody HashMap<String,ArrayList<HashMap<String, Object>>> getAllServAdicJson(
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
        
        //Catalogo de Servicios Adicionales(LOG)
        Integer app_selected = 185;
        
        //decodificar id de usuario
        Integer id_usuario = Integer.parseInt(Base64Coder.decodeString(id_user_cod));
        
        //Variables para el buscador
        String busqueda_codigo = "%"+StringHelper.isNullString(String.valueOf(has_busqueda.get("busqueda_codigo")))+"%";        
        String busqueda_descripcion = "%"+StringHelper.isNullString(String.valueOf(has_busqueda.get("busqueda_descripcion")))+"%";
        
        String data_string = app_selected+"___"+id_usuario+"___"+busqueda_codigo+"___"+busqueda_descripcion;
        
        //Obtiene total de registros en base de datos, con los parametros de busqueda
        int total_items = this.getLogDao().countAll(data_string);
        
        //Calcula el total de paginas
        int total_pags = resource.calculaTotalPag(total_items,items_por_pag);
        
        //Variables que necesita el datagrid, para no tener que hacer uno por cada aplicativo
        DataPost dataforpos = new DataPost(orderby, desc, items_por_pag, pag_start, display_pag, input_json, cadena_busqueda,total_items,total_pags, id_user_cod);
        
        int offset = resource.__get_inicio_offset(items_por_pag, pag_start);
        
        //Obtiene los registros para el grid, de acuerdo a los parametros de busqueda
        jsonretorno.put("Data", this.getLogDao().getServAdic_PaginaGrid(data_string, offset, items_por_pag, orderby, desc));
        
        //Obtiene el hash para los datos que necesita el datagrid
        jsonretorno.put("DataForGrid", dataforpos.formaHashForPos(dataforpos));
        
        return jsonretorno;
    }
    
    
    
    
    @RequestMapping(method = RequestMethod.POST, value="/getInicializar.json")
    public @ResponseBody HashMap<String,Object> getCuentasMayorJson(
            @RequestParam(value="iu", required=true) String id_user,
            Model model
        ) {
        
        log.log(Level.INFO, "Ejecutando getInicializarJson de {0}", LogServAdicController.class.getName());
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
    
    
    
    @RequestMapping(method = RequestMethod.POST, value="/getData.json")
    public @ResponseBody HashMap<String,ArrayList<HashMap<String, Object>>> getDataJson(
            @RequestParam(value="id", required=true) Integer id,
            @RequestParam(value="iu", required=true) String id_user_cod,
            Model model
            ){
        
        log.log(Level.INFO, "Ejecutando getDataJson de {0}", LogServAdicController.class.getName());
        HashMap<String,ArrayList<HashMap<String, Object>>> jsonretorno = new HashMap<String,ArrayList<HashMap<String, Object>>>();
        HashMap<String, String> userDat = new HashMap<String, String>();
        ArrayList<HashMap<String, Object>> datos = new ArrayList<HashMap<String, Object>>();
        ArrayList<HashMap<String, Object>> grid = new ArrayList<HashMap<String, Object>>();
       
        //decodificar id de usuario
        Integer id_usuario = Integer.parseInt(Base64Coder.decodeString(id_user_cod));
        userDat = this.getHomeDao().getUserById(id_usuario);
        
        if( id != 0 ){
            datos = this.getLogDao().getServAdic_Datos(id);
        }
        
        jsonretorno.put("prodTipos", this.getLogDao().getProducto_Tipos());
        jsonretorno.put("Datos", datos);
        
        return jsonretorno;
    }
    
    
    
    
    
    
    //Obtiene los productos para el buscador
    @RequestMapping(method = RequestMethod.POST, value = "/getBuscadorProductos.json")
    public @ResponseBody
    HashMap<String, ArrayList<HashMap<String, Object>>> getBuscadorProductosJson(
            @RequestParam(value = "sku", required = true) String sku,
            @RequestParam(value = "tipo", required = true) String tipo,
            @RequestParam(value = "descripcion", required = true) String descripcion,
            @RequestParam(value = "iu", required = true) String id_user,
            Model model
        ) {
        
        log.log(Level.INFO, "Ejecutando getBuscadorProductosJson de {0}", LogServAdicController.class.getName());
        HashMap<String, ArrayList<HashMap<String, Object>>> jsonretorno = new HashMap<String, ArrayList<HashMap<String, Object>>>();
        HashMap<String, String> userDat = new HashMap<String, String>();
        
        //Decodificar id de usuario
        Integer id_usuario = Integer.parseInt(Base64Coder.decodeString(id_user));
        userDat = this.getHomeDao().getUserById(id_usuario);
        
        Integer id_empresa = Integer.parseInt(userDat.get("empresa_id"));
        
        //Se pasa vacio el numero de cliente para que aplique el filtro
        String no_clie = "";
        
        jsonretorno.put("Productos", this.getLogDao().getBuscadorProductos(no_clie, sku,tipo ,descripcion, id_empresa));
        
        return jsonretorno;
    }
    
    
    
    
    //obtiene los productos para el buscador
    @RequestMapping(method = RequestMethod.POST, value="/getDatosProducto.json")
    public @ResponseBody HashMap<String,ArrayList<HashMap<String, Object>>> getDatosProductoJson(
            @RequestParam(value="codigo", required=true) String codigo,
            @RequestParam(value="iu", required=true) String id_user,
            Model model
        ) {
        
        log.log(Level.INFO, "Ejecutando getDatosProductoJson de {0}", LogServAdicController.class.getName());
        HashMap<String,ArrayList<HashMap<String, Object>>> jsonretorno = new HashMap<String,ArrayList<HashMap<String, Object>>>();
        HashMap<String, String> userDat = new HashMap<String, String>();
        
        //decodificar id de usuario
        Integer id_usuario = Integer.parseInt(Base64Coder.decodeString(id_user));
        userDat = this.getHomeDao().getUserById(id_usuario);
        
        Integer id_empresa = Integer.parseInt(userDat.get("empresa_id"));
        
        //Solo busca productos tipo Servicios
        String tipo = "4";
        //Se pasa vacio el numero de cliente para que aplique el filtro
        String no_clie = "";
        
        jsonretorno.put("Producto", this.getLogDao().getDataProductBySku(no_clie, codigo, tipo, id_empresa));
        
        return jsonretorno;
    }
    
    
    
     //Crear y editar
    @RequestMapping(method = RequestMethod.POST, value="/edit.json")
    public @ResponseBody HashMap<String, String> editJson(
            @RequestParam(value="identificador", required=true) Integer identificador,
            @RequestParam(value="idreg", required=false) String[] idreg,
            @RequestParam(value="elim", required=false) String[] elim,
            @RequestParam(value="idprod", required=false) String[] idprod,
            @RequestParam(value="noTr", required=false) String[] noTr,
            @ModelAttribute("user") UserSessionData user,
            Model model
        ) {
        
        HashMap<String, String> jsonretorno = new HashMap<String, String>();
        HashMap<String, String> succes = new HashMap<String, String>();
        
        //Catalogo de Servicios Adicionales(LOG)
        Integer app_selected = 185;
        String command_selected = "new";
        //Decodificar id de usuario
        Integer id_usuario = user.getUserId();
        
        String extra_data_array = "'sin datos'";
        String actualizo = "0";
        
        if( identificador==0 ){
            command_selected = "new";
        }else{
            command_selected = "edit";
        }
        
        String arreglo[];
        arreglo = new String[elim.length];
        
        for(int i=0; i<idreg.length; i++) { 
            arreglo[i]= "'"+ idreg[i] +"___"+ idprod[i] +"___"+ elim[i] +"___"+ noTr[i] +"'";
            //System.out.println(arreglo[i]);
        }
        
        //Serializar el arreglo
        extra_data_array = StringUtils.join(arreglo, ",");
        
        String data_string = app_selected+"___"+command_selected+"___"+id_usuario+"___"+identificador;
        
        succes = this.getLogDao().selectFunctionValidateAaplicativo(data_string,app_selected,extra_data_array);
        
        //System.out.println("succes: "+succes);
        log.log(Level.INFO, "despues de validacion {0}", String.valueOf(succes.get("success")));
        if( String.valueOf(succes.get("success")).equals("true") ){
            actualizo = this.getLogDao().selectFunctionForLogAdmProcesos(data_string, extra_data_array);
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
        
        //Catalogo de Servicios Adicionales(LOG)
        Integer app_selected = 185;
        String command_selected = "delete";
        String extra_data_array = "'sin datos'";
        String data_string = app_selected+"___"+command_selected+"___"+id_usuario+"___"+id;
        
        jsonretorno.put("success",String.valueOf( this.getLogDao().selectFunctionForLogAdmProcesos(data_string,extra_data_array)) );
        
        return jsonretorno;
    }
    
    
}
