package com.agnux.kemikal.controllers;

import com.agnux.cfd.v2.Base64Coder;
import com.agnux.common.helpers.StringHelper;
import com.agnux.common.obj.DataPost;
import com.agnux.common.obj.ResourceProject;
import com.agnux.common.obj.UserSessionData;
import com.agnux.kemikal.interfacedaos.FacturasInterfaceDao;
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
@RequestMapping("/facpar/")
public class FacParController {
    private static final Logger log  = Logger.getLogger(FacParController.class.getName());
    ResourceProject resource = new ResourceProject();
    
    @Autowired
    @Qualifier("daoHome")
    private HomeInterfaceDao HomeDao;
    
    @Autowired
    @Qualifier("daoFacturas")
    private FacturasInterfaceDao facdao;
    
    public HomeInterfaceDao getHomeDao() {
        return HomeDao;
    }
    
    public FacturasInterfaceDao getFacdao() {
        return facdao;
    }
    
    @RequestMapping(value="/startup.agnux")
    public ModelAndView startUp(HttpServletRequest request, HttpServletResponse response, 
            @ModelAttribute("user") UserSessionData user
            )throws ServletException, IOException {
        
        log.log(Level.INFO, "Ejecutando starUp de {0}", FacParController.class.getName());
        LinkedHashMap<String,String> infoConstruccionTabla = new LinkedHashMap<String,String>();
        
        infoConstruccionTabla.put("id", "Acciones:70");
        infoConstruccionTabla.put("clave", "Clave Suc.:90");
        infoConstruccionTabla.put("sucursal", "Sucursal:200");
        
        ModelAndView x = new ModelAndView("facpar/startup", "title", "Configuraci&oacute;n de Par&aacute;metros de Facturaci&oacute;n");
        
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
        
        //id de usuario codificado
        x = x.addObject("iu", codificado);
        
        return x;
    }
    
    
    
    
    //Obtiene datos para el grid principal
    @RequestMapping(value="/getAllFacPar.json", method = RequestMethod.POST)
    public @ResponseBody HashMap<String,ArrayList<HashMap<String, Object>>> getAllFacParJson(
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
        
        //Aplicativo Parametros de Facturacion
        Integer app_selected = 175;
        
        //decodificar id de usuario
        Integer id_usuario = Integer.parseInt(Base64Coder.decodeString(id_user_cod));
        //System.out.println("id_usuario: "+id_usuario);
        
        //variables para el buscador
        //String id_sucursal = StringHelper.isNullString(String.valueOf(has_busqueda.get("id_suc")));
        
        //String data_string = app_selected+"___"+id_usuario+"___"+id_sucursal;
        String data_string = app_selected+"___"+id_usuario;
        
        //obtiene total de registros en base de datos, con los parametros de busqueda
        int total_items = this.getFacdao().countAll(data_string);
        
        //calcula el total de paginas
        int total_pags = resource.calculaTotalPag(total_items,items_por_pag);
        
        //variables que necesita el datagrid, para no tener que hacer uno por cada aplicativo
        DataPost dataforpos = new DataPost(orderby, desc, items_por_pag, pag_start, display_pag, input_json, cadena_busqueda,total_items,total_pags,id_user_cod);
        
        int offset = resource.__get_inicio_offset(items_por_pag, pag_start);
        
        //obtiene los registros para el grid, de acuerdo a los parametros de busqueda
        jsonretorno.put("Data", this.getFacdao().getFacPar_PaginaGrid(data_string, offset, items_por_pag, orderby, desc));
        //obtiene el hash para los datos que necesita el datagrid
        jsonretorno.put("DataForGrid", dataforpos.formaHashForPos(dataforpos));
        
        return jsonretorno;
    }
    
    
    
    
    @RequestMapping(method = RequestMethod.POST, value="/getParametro.json")
    public @ResponseBody HashMap<String,ArrayList<HashMap<String, Object>>> getParametroJson(
            @RequestParam(value="id", required=true) Integer id,
            @RequestParam(value="iu", required=true) String id_user_cod,
            Model model
            ){
        
        log.log(Level.INFO, "Ejecutando getParametroJson de {0}", FacParController.class.getName());
        HashMap<String,ArrayList<HashMap<String, Object>>> jsonretorno = new HashMap<String,ArrayList<HashMap<String, Object>>>();
        HashMap<String, String> userDat = new HashMap<String, String>();
        ArrayList<HashMap<String, Object>> parametro_datos = new ArrayList<HashMap<String, Object>>();
        ArrayList<HashMap<String, Object>> almacenes = new ArrayList<HashMap<String, Object>>();
        
        //decodificar id de usuario
        Integer id_usuario = Integer.parseInt(Base64Coder.decodeString(id_user_cod));
        userDat = this.getHomeDao().getUserById(id_usuario);
        
        Integer id_empresa = Integer.parseInt(userDat.get("empresa_id"));
        Integer id_sucursal = Integer.parseInt(userDat.get("sucursal_id"));
        
        if( id != 0  ){
            parametro_datos = this.getFacdao().getFacPar_Datos(id);
        }
        
        almacenes = this.getFacdao().getFacPar_Almacenes(id_empresa, id_sucursal);
        
        jsonretorno.put("Parametro", parametro_datos);
        jsonretorno.put("Almacenes", almacenes);
        
        return jsonretorno;
    }
    
    
    
    //crear y editar un registro
    @RequestMapping(method = RequestMethod.POST, value="/edit.json")
    public @ResponseBody HashMap<String, String> editJson(
            @RequestParam(value="identificador", required=true) String identificador,
            @RequestParam(value="identificador_suc", required=true) String identificador_suc,
            @RequestParam(value="correo_id", required=true) String correo_id,
            @RequestParam(value="correo_envio", required=true) String correo_envio,
            @RequestParam(value="passwd_correo_envio", required=true) String passwd_correo_envio,
            @RequestParam(value="passwd2_correo_envio", required=true) String passwd2_correo_envio,
            @RequestParam(value="servidor_correo_envio", required=true) String servidor_correo_envio,
            @RequestParam(value="puerto_correo_envio", required=true) String puerto_correo_envio,
            @RequestParam(value="cco_id", required=true) String cco_id,
            @RequestParam(value="correo_cco", required=true) String correo_cco,
            @RequestParam(value="select_almacen_ventas", required=true) String select_almacen_ventas,
            //@RequestParam(value="radio_valida_exi", required=false) String radio_valida_exi,
            @RequestParam(value="select_formato_pedido", required=true) String select_formato_pedido,
            Model model,@ModelAttribute("user") UserSessionData user
            ) {
            
            HashMap<String, String> jsonretorno = new HashMap<String, String>();
            HashMap<String, String> succes = new HashMap<String, String>();
            //Aplicativo Parametros de Facturacion
            Integer app_selected = 175;
            String command_selected = "new";
            Integer id_usuario= user.getUserId();//variable para el id  del usuario
            String extra_data_array = "'sin datos'";
            String valida_exi="true";
            
            command_selected = "edit";
            
            String data_string = 
                    app_selected+"___"+
                    command_selected+"___"+
                    id_usuario+"___"+ 
                    identificador+"___"+
                    identificador_suc+"___"+
                    correo_envio+"___"+
                    passwd_correo_envio+"___"+
                    passwd2_correo_envio+"___"+
                    servidor_correo_envio+"___"+
                    puerto_correo_envio+"___"+
                    correo_cco+"___"+
                    select_almacen_ventas+"___"+
                    select_formato_pedido+"___"+
                    correo_id+"___"+
                    cco_id;
            
            succes = this.getFacdao().selectFunctionValidateAaplicativo(data_string,app_selected,extra_data_array);
            
            log.log(Level.INFO, "despues de validacion {0}", String.valueOf(succes.get("success")));
            String actualizo = "0";
            
            if( String.valueOf(succes.get("success")).equals("true") ){
                actualizo = this.getFacdao().selectFunctionForFacAdmProcesos(data_string, extra_data_array);
            }
            
            jsonretorno.put("success",String.valueOf(succes.get("success")));
            
            log.log(Level.INFO, "Salida json {0}", String.valueOf(jsonretorno.get("success")));
        return jsonretorno;
    }
    
    
    
}
