/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.agnux.kemikal.controllers;

import com.agnux.kemikal.interfacedaos.InvInterfaceDao;

import com.agnux.cfd.v2.Base64Coder;
import com.agnux.common.helpers.StringHelper;
import com.agnux.common.obj.DataPost;
import com.agnux.common.obj.ResourceProject;
import com.agnux.common.obj.UserSessionData;
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

/**
 * @author Paco Mora
 * 
 * 07/05/2012
 */

@Controller
@SessionAttributes({"user"})
@RequestMapping("/tiposmovinventario/")
public class TiposMovInventarioController {
    ResourceProject resource = new ResourceProject();
    private static final Logger log  = Logger.getLogger(TiposMovInventarioController.class.getName());
    
    @Autowired
    @Qualifier("daoInv")
    private InvInterfaceDao ctbDao;
    
    public InvInterfaceDao getCtbDao() {
        return ctbDao;
    }
    
    
    @RequestMapping(value="/startup.agnux")
    public ModelAndView startUp(HttpServletRequest request, HttpServletResponse response, 
            @ModelAttribute("user") UserSessionData user
            )throws ServletException, IOException {
        
        log.log(Level.INFO, "Ejecutando starUp de {0}", TiposMovInventarioController.class.getName());
        LinkedHashMap<String,String> infoConstruccionTabla = new LinkedHashMap<String,String>();
        
        infoConstruccionTabla.put("id", "Acciones:90");
        infoConstruccionTabla.put("titulo", "Tipo de Movimiento:160");
        infoConstruccionTabla.put("descripcion", "Descripcion:300");
        
        ModelAndView x = new ModelAndView("tiposmovinventario/startup", "title", "Tipos de Movimientos de Inventario");
        
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
    
    
    
    
    @RequestMapping(value="/getAllTipoMovInventario.json", method = RequestMethod.POST)
    public @ResponseBody HashMap<String,ArrayList<HashMap<String, Object>>> getAllTipoMovInventarioJson(
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
        
        //aplicativo tipos de poliza
        Integer app_selected = 35;
        
        //decodificar id de usuario
        Integer id_usuario = Integer.parseInt(Base64Coder.decodeString(id_user_cod));
        
        //variables para el buscador
        String tipo = "%"+StringHelper.isNullString(String.valueOf(has_busqueda.get("tipo")))+"%";
        String descripcion = "%"+StringHelper.isNullString(String.valueOf(has_busqueda.get("descripcion")))+"%";
        //String grupo = StringHelper.isNullString(String.valueOf(has_busqueda.get("grupo")));
        
        String data_string = app_selected+"___"+id_usuario+"___"+tipo+"___"+descripcion;
        
        //obtiene total de registros en base de datos, con los parametros de busqueda
        int total_items = this.getCtbDao().countAll(data_string);
        
        //calcula el total de paginas
        int total_pags = resource.calculaTotalPag(total_items,items_por_pag);
        
        //variables que necesita el datagrid, para no tener que hacer uno por cada aplicativo
        DataPost dataforpos = new DataPost(orderby, desc, items_por_pag, pag_start, display_pag, input_json, cadena_busqueda,total_items,total_pags, id_user_cod);
        
        int offset = resource.__get_inicio_offset(items_por_pag, pag_start);
        
        //obtiene los registros para el grid, de acuerdo a los parametros de busqueda
        jsonretorno.put("Data", this.getCtbDao().getTipoMovimientosInventaioGrid(data_string, offset, items_por_pag, orderby, desc));
        //obtiene el hash para los datos que necesita el datagrid
        jsonretorno.put("DataForGrid", dataforpos.formaHashForPos(dataforpos));
        
        return jsonretorno;
    }
    
    
    
    /*
    
    //obtiene los grupos para el buscador
    @RequestMapping(method = RequestMethod.POST, value="/getGruposBuscador.json")
    public @ResponseBody HashMap<String,ArrayList<HashMap<String, String>>> getGruposBuscadorJson(
            Model model
            ) {
        HashMap<String,ArrayList<HashMap<String, String>>> jsonretorno = new HashMap<String,ArrayList<HashMap<String, String>>>();
        ArrayList<HashMap<String, String>> TpolGrupos = new ArrayList<HashMap<String, String>>();
        
        TpolGrupos = this.getCtbDao().getTipoPoliza_Grupos();
        
        jsonretorno.put("Grupos", TpolGrupos);
        
        return jsonretorno;
    }*/
    
    
    //este es solo para probar
    
    @RequestMapping(method = RequestMethod.POST, value="/getTipoMovInventario.json")
    public @ResponseBody HashMap<String,ArrayList<HashMap<String, String>>> getTipoMovInventarioJson(
            @RequestParam(value="id", required=true) Integer id,
            Model model
            ) {
        
        log.log(Level.INFO, "Ejecutando getTipoMovInventario de {0}", TiposMovInventarioController.class.getName());
        HashMap<String,ArrayList<HashMap<String, String>>> jsonretorno = new HashMap<String,ArrayList<HashMap<String, String>>>();
        ArrayList<HashMap<String, String>> datosTipoMovInv = new ArrayList<HashMap<String, String>>();
        //ArrayList<HashMap<String, String>> TpolGrupos = new ArrayList<HashMap<String, String>>();
        
        if( id != 0  ){
            datosTipoMovInv = this.getCtbDao().getTipoMovInv_Datos(id);
        }
        
        //TpolGrupos = this.getCtbDao().getTipoPoliza_Grupos();
        
        jsonretorno.put("TipoMov", datosTipoMovInv);
        //jsonretorno.put("Grupos", TpolGrupos);
        
        return jsonretorno;
    }
    
    
    //crear y editar
    @RequestMapping(method = RequestMethod.POST, value="/edit.json")
    public @ResponseBody HashMap<String, String> editJson(
            @RequestParam(value="identificador", required=true) String id,
            @RequestParam(value="tipo", required=false) String tipo,
            @RequestParam(value="descripcion", required=false) String descripcion,
            @RequestParam(value="mov_de_ajuste", required=false) String mov_de_ajuste,
            @RequestParam(value="afecta_ventas", required=false) String afecta_ventas,
            @RequestParam(value="grupo", required=false) String grupo,
            @RequestParam(value="afecta_compras", required=false) String afecta_compras,
            @RequestParam(value="mov_de_ajuste", required=false) String considera_consumo,
            @RequestParam(value="tipo_costo", required=false) String tipo_costo,
            Model model,@ModelAttribute("user") UserSessionData user
            ) {

        HashMap<String, String> jsonretorno = new HashMap<String, String>();
        HashMap<String, String> succes = new HashMap<String, String>();
        Integer app_selected = 35;
        String command_selected = "new";
        Integer id_usuario= user.getUserId();//variable para el id  del usuario
        String extra_data_array = "'sin datos'";
        String actualizo = "0";
        
        
        if(mov_de_ajuste == null || !mov_de_ajuste.equals("on")){
            mov_de_ajuste = "false";
        }else{
            mov_de_ajuste = "true";
        }
        
        if(afecta_compras == null || !afecta_compras.equals("on")){
            afecta_compras = "false";
        }else{
            afecta_compras = "true";
        }
        if(afecta_ventas == null || !afecta_ventas.equals("on")){
            afecta_ventas = "false";
        }else{
            afecta_ventas = "true";
        }
        if(considera_consumo == null || !considera_consumo.equals("on")){
            considera_consumo = "false";
        }else{
            considera_consumo = "true";
        }
        
        
        if( id.equals("0") ){
            command_selected = "new";
        }else{
            command_selected = "edit";
        }
        //                      1                   2                   3               4               5                   6                                   7
        String data_string = app_selected+"___"+command_selected+"___"+id_usuario+"___"+id+"___"+tipo.toUpperCase()+"___"+descripcion.toUpperCase()+"___"+mov_de_ajuste
        //      8               9                       10              11                      12
        +"___"+grupo+"___"+afecta_compras+"___"+afecta_ventas+"___"+considera_consumo+"___"+tipo_costo;
        
        
        succes = this.getCtbDao().selectFunctionValidateAaplicativo(data_string,app_selected,extra_data_array);
        
        log.log(Level.INFO, "despues de validacion {0}", String.valueOf(succes.get("success")));
        if( String.valueOf(succes.get("success")).equals("true") ){
            actualizo = this.getCtbDao().selectFunctionForThisApp(data_string, extra_data_array);
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
        
        HashMap<String, String> jsonretorno = new HashMap<String, String>();
        
        //decodificar id de usuario
        Integer id_usuario = Integer.parseInt(Base64Coder.decodeString(id_user));
        
        Integer app_selected = 35;
        String command_selected = "delete";
        String extra_data_array = "'sin datos'";
        String data_string = app_selected+"___"+command_selected+"___"+id_usuario+"___"+id;
        
        System.out.println("Ejecutando borrado logico de tipo poliza");
        jsonretorno.put("success",String.valueOf( this.getCtbDao().selectFunctionForThisApp(data_string,extra_data_array)) );
        
        return jsonretorno;
    }
}
