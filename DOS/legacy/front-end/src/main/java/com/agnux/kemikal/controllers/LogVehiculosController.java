/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
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

/**
 *
 * @Ezcur
 */

@Controller
@SessionAttributes({"user"})
@RequestMapping("/logvehiculos/")
public class LogVehiculosController {
    ResourceProject resource = new ResourceProject();
    private static final Logger log  = Logger.getLogger(LogVehiculosController.class.getName());
    
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
        
        log.log(Level.INFO, "Ejecutando starUp de {0}", LogVehiculosController.class.getName());
        LinkedHashMap<String,String> infoConstruccionTabla = new LinkedHashMap<String,String>();
        
        infoConstruccionTabla.put("id", "Acciones:90");
        infoConstruccionTabla.put("folio", "Folio:65");
        infoConstruccionTabla.put("marca", "Marca:130");
        infoConstruccionTabla.put("anio", "A&ntilde;o:50");
        infoConstruccionTabla.put("clase", "Clase:60");
        infoConstruccionTabla.put("cap_volumen", "Cap. m&#179;:70");
        infoConstruccionTabla.put("cap_peso", "Cap. Kg.:70");
        infoConstruccionTabla.put("tipo_unidad", "Tipo de Unidad:150");
        infoConstruccionTabla.put("tipo_caja", "Tipo de Caja:150");
        infoConstruccionTabla.put("transportista", "Transportista:250");
        
        ModelAndView x = new ModelAndView("logvehiculos/startup", "title", "Cat&aacute;logo de Unidades");
        
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
    
    
    
    
    @RequestMapping(value="/getAllVehiculos.json", method = RequestMethod.POST)
     public @ResponseBody HashMap<String,ArrayList<HashMap<String, Object>>> getAllVehiculosJson(
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
        
        //Catalogo de Unidades
        Integer app_selected = 73;
        
        //Decodificar id de usuario
        Integer id_usuario = Integer.parseInt(Base64Coder.decodeString(id_user_cod));
        
        //variables para el buscador
        String marca = "%"+StringHelper.isNullString(String.valueOf(has_busqueda.get("marca")))+"%";        
        String numero_economico = "%"+StringHelper.isNullString(String.valueOf(has_busqueda.get("numero_economico")))+"%";        
       
        
        String data_string = app_selected+"___"+id_usuario+"___"+numero_economico+"___"+marca;
        
        //Obtiene total de registros en base de datos, con los parametros de busqueda
        int total_items = this.getLogDao().countAll(data_string);
        
        //Calcula el total de paginas
        int total_pags = resource.calculaTotalPag(total_items,items_por_pag);
        
        //Variables que necesita el datagrid, para no tener que hacer uno por cada aplicativo
        DataPost dataforpos = new DataPost(orderby, desc, items_por_pag, pag_start, display_pag, input_json, cadena_busqueda,total_items,total_pags, id_user_cod);
        
        int offset = resource.__get_inicio_offset(items_por_pag, pag_start);
        
        //obtiene los registros para el grid, de acuerdo a los parametros de busqueda
        jsonretorno.put("Data", this.getLogDao().getUnidades_PaginaGrid(data_string, offset, items_por_pag, orderby, desc));
        //obtiene el hash para los datos que necesita el datagrid
        jsonretorno.put("DataForGrid", dataforpos.formaHashForPos(dataforpos));
        
        return jsonretorno;
    }
    
    
    
    
    @RequestMapping(method = RequestMethod.POST, value="/getInicializar.json")
    public @ResponseBody HashMap<String,Object> getCuentasMayorJson(
            @RequestParam(value="iu", required=true) String id_user,
            Model model
        ) {
        
        log.log(Level.INFO, "Ejecutando getInicializarJson de {0}", LogVehiculosController.class.getName());
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
        data.put("Marcas", this.getLogDao().getUnidades_Marcas(id_empresa));
        data.put("TUnidades", this.getLogDao().getUnidades_Tipos(id_empresa));
        data.put("Clases", this.getLogDao().getUnidades_Clases(id_empresa));
        data.put("TPlacas", this.getLogDao().getUnidades_TiposPlaca(id_empresa));
        data.put("TRodadas", this.getLogDao().getUnidades_TiposRodada(id_empresa));
        data.put("TCajas", this.getLogDao().getUnidades_TiposCaja(id_empresa));
        data.put("Anios", this.getLogDao().getUnidades_AniosUnidad());
        
        jsonretorno.put("Data", data);
        
        return jsonretorno;
    }
    
    
    
    
    //Obtiene los proveedores para el buscador.
    //Solo proveedores marcados con el campo Transportista=True
    @RequestMapping(method = RequestMethod.POST, value="/getBuscaProveedores.json")
    public @ResponseBody HashMap<String,ArrayList<HashMap<String, Object>>> getBuscaProveedoresJson(
            @RequestParam(value="rfc", required=true) String rfc,
            @RequestParam(value="no_proveedor", required=true) String no_proveedor,
            @RequestParam(value="nombre", required=true) String nombre,
            @RequestParam(value="transportista", required=true) String transportista,
            @RequestParam(value="iu", required=true) String id_user,
            Model model
        ) {
        
        log.log(Level.INFO, "Ejecutando getBuscaProveedoresJson de {0}", LogVehiculosController.class.getName());
        HashMap<String,ArrayList<HashMap<String, Object>>> jsonretorno = new HashMap<String,ArrayList<HashMap<String, Object>>>();
        HashMap<String, String> userDat = new HashMap<String, String>();
        //decodificar id de usuario
        Integer id_usuario = Integer.parseInt(Base64Coder.decodeString(id_user));
        userDat = this.getHomeDao().getUserById(id_usuario);
        
        Integer id_empresa = Integer.parseInt(userDat.get("empresa_id"));
        
        jsonretorno.put("Proveedores", this.getLogDao().getBuscadorProveedores(rfc, no_proveedor, nombre, transportista, id_empresa));
        
        return jsonretorno;
    }
    
    
    
    /*
    Obtener datos del Proveedor a partir del Numero de Control
    Solo proveedores marcados con el campo Transportista=True
    */
    @RequestMapping(method = RequestMethod.POST, value="/getDataByNoProv.json")
    public @ResponseBody HashMap<String,ArrayList<HashMap<String, Object>>> getDataByNoProvJson(
            @RequestParam(value="no_proveedor", required=true) String no_proveedor,
            @RequestParam(value="transportista", required=true) String transportista,
            @RequestParam(value="iu", required=true) String id_user,
            Model model
        ) {
        
        HashMap<String,ArrayList<HashMap<String, Object>>> jsonretorno = new HashMap<String,ArrayList<HashMap<String, Object>>>();
        HashMap<String, String> userDat = new HashMap<String, String>();
       
        //Decodificar id de usuario
        Integer id_usuario = Integer.parseInt(Base64Coder.decodeString(id_user));
        userDat = this.getHomeDao().getUserById(id_usuario);
        Integer id_empresa = Integer.parseInt(userDat.get("empresa_id"));
        //Integer id_sucursal = Integer.parseInt(userDat.get("sucursal_id"));
        
        jsonretorno.put("Proveedor", this.getLogDao().getDatosProveedorByNoProv(no_proveedor, transportista, id_empresa));
        return jsonretorno;
    }
    
    
    
    
    /*
    Buscar Operadores(choferes)
    Permite hacer la busqueda de choferes registrados en la sucursal.
    Permite buscar cualquier operador sin importar si esta relacionado con un transportista
    */
    @RequestMapping(method = RequestMethod.POST, value="/getBuscadorOperadores.json")
    public @ResponseBody HashMap<String,ArrayList<HashMap<String, Object>>> getBuscadorOperadoresJson(
            @RequestParam(value="no_operador", required=true) String no_operador,
            @RequestParam(value="nombre", required=true) String nombre,
            @RequestParam(value="id_prov", required=true) Integer id_prov,
            @RequestParam(value="iu", required=true) String id_user,
            Model model
        ) {
        
        HashMap<String,ArrayList<HashMap<String, Object>>> jsonretorno = new HashMap<String,ArrayList<HashMap<String, Object>>>();
        HashMap<String, String> userDat = new HashMap<String, String>();
        
        //Decodificar id de usuario
        Integer id_usuario = Integer.parseInt(Base64Coder.decodeString(id_user));
        userDat = this.getHomeDao().getUserById(id_usuario);
        Integer id_empresa = Integer.parseInt(userDat.get("empresa_id"));
        Integer id_sucursal = Integer.parseInt(userDat.get("sucursal_id"));
        
        jsonretorno.put("Operadores", this.getLogDao().getBuscadorOperadores(no_operador, nombre, id_prov, id_empresa, id_sucursal));
        
        return jsonretorno;
    }
    
    
    /*
    Obtener datos del Operador a partir de la clave
    Permite hacer la busqueda en la sucursal actual.
    Permite buscar cualquier operador sin importar si esta relacionado con un transportista
    */
    @RequestMapping(method = RequestMethod.POST, value="/getDataOperadorByNo.json")
    public @ResponseBody HashMap<String,ArrayList<HashMap<String, Object>>> getDataOperadorByNoJson(
            @RequestParam(value="no_operador", required=true) String no_operador,
            @RequestParam(value="id_prov", required=true) Integer id_prov,
            @RequestParam(value="iu", required=true) String id_user,
            Model model
        ) {
        HashMap<String,ArrayList<HashMap<String, Object>>> jsonretorno = new HashMap<String,ArrayList<HashMap<String, Object>>>();
        HashMap<String, String> userDat = new HashMap<String, String>();
       
        //Decodificar id de usuario
        Integer id_usuario = Integer.parseInt(Base64Coder.decodeString(id_user));
        userDat = this.getHomeDao().getUserById(id_usuario);
        
        Integer id_empresa = Integer.parseInt(userDat.get("empresa_id"));
        Integer id_sucursal = Integer.parseInt(userDat.get("sucursal_id"));
        
        jsonretorno.put("Operador", this.getLogDao().getDatosOperadorByNo(no_operador, id_prov, id_empresa, id_sucursal));
        
        return jsonretorno;
    }
    
    
    
    
    @RequestMapping(method = RequestMethod.POST, value="/getVehiculo.json")
    public @ResponseBody HashMap<String,ArrayList<HashMap<String, String>>> getVehiculoJson(
            @RequestParam(value="id", required=true) Integer id,
            @RequestParam(value="iu", required=true) String id_user_cod,
            Model model
        ){
        
        log.log(Level.INFO, "Ejecutando getVehiculojson de {0}", LogVehiculosController.class.getName());
        HashMap<String,ArrayList<HashMap<String, String>>> jsonretorno = new HashMap<String,ArrayList<HashMap<String, String>>>();
        HashMap<String, String> userDat = new HashMap<String, String>();
       
        ArrayList<HashMap<String, String>> datosVehiculo = new ArrayList<HashMap<String, String>>(); 
       
        //Decodificar id de usuario
        Integer id_usuario = Integer.parseInt(Base64Coder.decodeString(id_user_cod));
        userDat = this.getHomeDao().getUserById(id_usuario);
        
        if( id != 0 ){
            datosVehiculo = this.getLogDao().getUnidades_Datos(id);
        }
        
        //Datos de la Unidad
        jsonretorno.put("Vehiculo", datosVehiculo);
        return jsonretorno;
    }
    
    
    //Crear y editar
    @RequestMapping(method = RequestMethod.POST, value="/edit.json")
    public @ResponseBody HashMap<String, String> editJson(
            @RequestParam(value="identificador", required=true) Integer id,
            @RequestParam(value="select_tipo_unidad", required=true) String select_tipo_unidad,
            @RequestParam(value="select_clase", required=true) String select_clase,
            @RequestParam(value="select_marca", required=true) String select_marca,
            @RequestParam(value="select_anio", required=true) String select_anio,
            @RequestParam(value="color", required=true) String color,
            @RequestParam(value="no_economico", required=true) String no_economico,
            @RequestParam(value="select_tipo_placa", required=true) String select_tipo_placa,
            @RequestParam(value="placas", required=true) String placas,
            @RequestParam(value="no_serie", required=true) String no_serie,
            @RequestParam(value="select_tipo_rodada", required=true) String select_tipo_rodada,
            @RequestParam(value="select_tipo_caja", required=true) String select_tipo_caja,
            @RequestParam(value="cap_volumen", required=true) String cap_volumen,
            @RequestParam(value="cap_peso", required=true) String cap_peso,
            @RequestParam(value="select_clasif2", required=true) String select_clasif2,
            @RequestParam(value="id_prov", required=false) String id_prov,
            @RequestParam(value="id_operador", required=false) String id_operador,
            @RequestParam(value="comentarios", required=false) String comentarios,
            @ModelAttribute("user") UserSessionData user,
            Model model
        ) {
        
        HashMap<String, String> jsonretorno = new HashMap<String, String>();
        HashMap<String, String> succes = new HashMap<String, String>();
        Integer app_selected = 73;//catalogo de vehiculo
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
        
        String data_string = 
                app_selected
                +"___"+command_selected
                +"___"+id_usuario
                +"___"+id
                +"___"+select_tipo_unidad
                +"___"+select_clase
                +"___"+select_marca
                +"___"+select_anio
                +"___"+color.toUpperCase()
                +"___"+no_economico.toUpperCase()
                +"___"+select_tipo_placa
                +"___"+placas.toUpperCase()
                +"___"+no_serie.toUpperCase()
                +"___"+select_tipo_rodada
                +"___"+select_tipo_caja
                +"___"+cap_volumen
                +"___"+cap_peso
                +"___"+select_clasif2
                +"___"+id_prov
                +"___"+id_operador
                +"___"+comentarios.toUpperCase();
        
        succes = this.getLogDao().selectFunctionValidateAaplicativo(data_string,app_selected,extra_data_array);
        //System.out.println("ESTO TRAE SUCEESSSS"+succes);
        
        log.log(Level.INFO, "despues de validacion {0}", String.valueOf(succes.get("success")));
        if( String.valueOf(succes.get("success")).equals("true") ){
            actualizo = this.getLogDao().selectFunctionForThisApp(data_string, extra_data_array);
        }
        
        jsonretorno.put("success",String.valueOf(succes.get("success")));
        
        log.log(Level.INFO, "Salida json {0}", String.valueOf(jsonretorno.get("success")));
        return jsonretorno;
    }
    
    
    //Cambia el estatus del borrado logico
    @RequestMapping(method = RequestMethod.POST, value="/logicDelete.json")
    public @ResponseBody HashMap<String, String> logicDeleteJson(
            @RequestParam(value="id", required=true) Integer id,
            @RequestParam(value="iu", required=true) String id_user,
            Model model
            ) {
        
        HashMap<String, String> jsonretorno = new HashMap<String, String>();
        //decodificar id de usuario
        Integer id_usuario = Integer.parseInt(Base64Coder.decodeString(id_user));
        
        Integer app_selected = 73;
        String command_selected = "delete";
        String extra_data_array = "'sin datos'";
        String data_string = app_selected+"___"+command_selected+"___"+id_usuario+"___"+id;
        
        jsonretorno.put("success",String.valueOf( this.getLogDao().selectFunctionForThisApp(data_string,extra_data_array)) );
        
        return jsonretorno;
    }
    
    
    
}
