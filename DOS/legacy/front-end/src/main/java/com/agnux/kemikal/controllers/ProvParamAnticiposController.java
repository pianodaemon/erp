/*
 * Parametros de anticpos
 */
package com.agnux.kemikal.controllers;

import com.agnux.cfd.v2.Base64Coder;
import com.agnux.common.helpers.StringHelper;
import com.agnux.common.obj.DataPost;
import com.agnux.common.obj.ResourceProject;
import com.agnux.common.obj.UserSessionData;
import com.agnux.kemikal.interfacedaos.CxpInterfaceDao;
import com.agnux.kemikal.interfacedaos.GralInterfaceDao;
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
@RequestMapping("/provparamanticipos/")
public class ProvParamAnticiposController {
    private static final Logger log  = Logger.getLogger(ProvParamAnticiposController.class.getName());
    ResourceProject resource = new ResourceProject();
    
    @Autowired
    @Qualifier("daoHome")
    private HomeInterfaceDao HomeDao;
    
    @Autowired
    @Qualifier("daoGral")
    private GralInterfaceDao gralDao;
    
    @Autowired
    @Qualifier("daoCxp")
    private CxpInterfaceDao cxpDao;
    
    public CxpInterfaceDao getCxpDao() {
        return cxpDao;
    }
    
    public HomeInterfaceDao getHomeDao() {
        return HomeDao;
    }
    
    public GralInterfaceDao getGralDao() {
        return gralDao;
    }
    
    
    @RequestMapping(value="/startup.agnux")
    public ModelAndView startUp(HttpServletRequest request, HttpServletResponse response, 
            @ModelAttribute("user") UserSessionData user
            )throws ServletException, IOException {
        
        log.log(Level.INFO, "Ejecutando starUp de {0}", ProvParamAnticiposController.class.getName());
        LinkedHashMap<String,String> infoConstruccionTabla = new LinkedHashMap<String,String>();
        
        infoConstruccionTabla.put("id", "Acciones:90");
        infoConstruccionTabla.put("sucursal", "Sucursal:200");
        infoConstruccionTabla.put("cxp_mov_anticipo", "Anticipo:80");
        infoConstruccionTabla.put("cxp_mov_apl_ant", "Aplicado:80");
        infoConstruccionTabla.put("cxp_mov_apl_fac", "Apl. Fac.:80");
        infoConstruccionTabla.put("cxp_mov_can", "Cancelado:80");
        infoConstruccionTabla.put("requiere_iva", "Incluye Iva:100");
        
        ModelAndView x = new ModelAndView("provparamanticipos/startup", "title", "Par&aacute;metros de Anticipos Proveedor");
        
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
    
    
    
    //obtiene listado de Parametros Anticipos para el grid
    @RequestMapping(value="/getAllParametros.json", method = RequestMethod.POST)
    public @ResponseBody HashMap<String,ArrayList<HashMap<String, Object>>> getAllParametros(
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
        
        //aplicativo Parametros de Anticipos a Proveedores
        Integer app_selected = 62;
        
        //decodificar id de usuario
        Integer id_usuario = Integer.parseInt(Base64Coder.decodeString(id_user_cod));
        //System.out.println("id_usuario: "+id_usuario);
        
        //variables para el buscador
        String id_sucursal = StringHelper.isNullString(String.valueOf(has_busqueda.get("id_suc")));
        
        String data_string = app_selected+"___"+id_usuario+"___"+id_sucursal;
        
        //obtiene total de registros en base de datos, con los parametros de busqueda
        int total_items = this.getCxpDao().countAll(data_string);
        
        //calcula el total de paginas
        int total_pags = resource.calculaTotalPag(total_items,items_por_pag);
        
        //variables que necesita el datagrid, para no tener que hacer uno por cada aplicativo
        DataPost dataforpos = new DataPost(orderby, desc, items_por_pag, pag_start, display_pag, input_json, cadena_busqueda,total_items,total_pags,id_user_cod);
        
        int offset = resource.__get_inicio_offset(items_por_pag, pag_start);
        
        //obtiene los registros para el grid, de acuerdo a los parametros de busqueda
        jsonretorno.put("Data", this.getCxpDao().getProvParamAnticipos_PaginaGrid(data_string, offset, items_por_pag, orderby, desc));
        //obtiene el hash para los datos que necesita el datagrid
        jsonretorno.put("DataForGrid", dataforpos.formaHashForPos(dataforpos));
        
        return jsonretorno;
    }
    
    
    
    
    
    //obtiene las Sucursales de la empresa para el buscador
    @RequestMapping(method = RequestMethod.POST, value="/getSucursalesEmpresa.json")
    public @ResponseBody HashMap<String,ArrayList<HashMap<String, String>>> getDataLineasJson(
            @RequestParam(value="iu", required=true) String id_user_cod,
            Model model) {
        
        HashMap<String,ArrayList<HashMap<String, String>>> jsonretorno = new HashMap<String,ArrayList<HashMap<String, String>>>();
        HashMap<String, String> userDat = new HashMap<String, String>();
        ArrayList<HashMap<String, String>> sucursales = new ArrayList<HashMap<String, String>>();
        
        //decodificar id de usuario
        Integer id_usuario = Integer.parseInt(Base64Coder.decodeString(id_user_cod));
        userDat = this.getHomeDao().getUserById(id_usuario);
        
        Integer id_empresa = Integer.parseInt(userDat.get("empresa_id"));
        
        sucursales=this.getCxpDao().getSucursales(id_empresa);
        
        jsonretorno.put("Sucursales", sucursales);
        
        return jsonretorno;
    }
    
    
    
     
    @RequestMapping(method = RequestMethod.POST, value="/getParametro.json")
    public @ResponseBody HashMap<String,ArrayList<HashMap<String, String>>> getParametroJson(
            @RequestParam(value="id", required=true) Integer id,
            @RequestParam(value="iu", required=true) String id_user_cod,
            Model model
            ){
        
        log.log(Level.INFO, "Ejecutando getParametroJson de {0}", ProvParamAnticiposController.class.getName());
        HashMap<String,ArrayList<HashMap<String, String>>> jsonretorno = new HashMap<String,ArrayList<HashMap<String, String>>>();
        HashMap<String, String> userDat = new HashMap<String, String>();
        ArrayList<HashMap<String, String>> parametro_datos = new ArrayList<HashMap<String, String>>();
        ArrayList<HashMap<String, String>> sucursales = new ArrayList<HashMap<String, String>>();
        ArrayList<HashMap<String, String>> consecutivo_sucursal = new ArrayList<HashMap<String, String>>();
        ArrayList<HashMap<String, String>> tmov_anticipo = new ArrayList<HashMap<String, String>>();
        ArrayList<HashMap<String, String>> tmov_aplicado_anticipo = new ArrayList<HashMap<String, String>>();
        ArrayList<HashMap<String, String>> tmov_aplicado_factura = new ArrayList<HashMap<String, String>>();
        ArrayList<HashMap<String, String>> tmov_cancelacion = new ArrayList<HashMap<String, String>>();
        
        Integer grupo=0;
        Integer naturaleza=0;
        String referenciado="";
        
        //decodificar id de usuario
        Integer id_usuario = Integer.parseInt(Base64Coder.decodeString(id_user_cod));
        userDat = this.getHomeDao().getUserById(id_usuario);
        
        Integer id_empresa = Integer.parseInt(userDat.get("empresa_id"));
        Integer id_sucursal = Integer.parseInt(userDat.get("sucursal_id"));
        
        if( id != 0  ){
            parametro_datos = this.getCxpDao().getProvParamAnticipos_Datos(id);
        }
        
        sucursales=this.getCxpDao().getSucursales(id_empresa);
        
        //obtiene consecutivo de cada sucursal
        consecutivo_sucursal=this.getCxpDao().getProvParamAnticipos_SucConsecutivo(id_empresa, id_sucursal);
        
        //tipo mov para anticipo
        grupo=1;//exclusivo de cxp
        naturaleza=0;//abono
        referenciado="false";
        tmov_anticipo = this.getCxpDao().getProvParamAnticipos_TiposMovimiento(id_empresa, grupo, naturaleza, referenciado);
        
        //tipo mov para Aplicado anticipo
        grupo=1;//exclusivo de cxp
        naturaleza=1;//abono
        referenciado="true";
        tmov_aplicado_anticipo = this.getCxpDao().getProvParamAnticipos_TiposMovimiento(id_empresa, grupo, naturaleza, referenciado);
        
        //tipo mov para Aplicado anticipo
        grupo=1;//exclusivo de cxp
        naturaleza=0;//cargo
        referenciado="true";
        tmov_aplicado_factura = this.getCxpDao().getProvParamAnticipos_TiposMovimiento(id_empresa, grupo, naturaleza, referenciado);        
        
        //tipo mov para tipo mov cancelacion
        grupo=1;//exclusivo de cxp
        naturaleza=1;//abono
        referenciado="true";
        tmov_cancelacion = this.getCxpDao().getProvParamAnticipos_TiposMovimiento(id_empresa, grupo, naturaleza, referenciado);
        
        jsonretorno.put("Parametro", parametro_datos);
        jsonretorno.put("Sucursales", sucursales);
        jsonretorno.put("Consecutivo", consecutivo_sucursal);
        jsonretorno.put("TMovanticipo", tmov_anticipo);
        jsonretorno.put("TMovAplanticipo", tmov_aplicado_anticipo);
        jsonretorno.put("TMovAplfactura", tmov_aplicado_factura);
        jsonretorno.put("TMovCancelacion", tmov_cancelacion);
        
        return jsonretorno;
    }
    
    
    
    //crear y editar un registro
    @RequestMapping(method = RequestMethod.POST, value="/edit.json")
    public @ResponseBody HashMap<String, String> editJson(
            @RequestParam(value="identificador", required=true) String identificador,
            @RequestParam(value="select_sucursal", required=true) String select_sucursal,
            @RequestParam(value="select_anticipo", required=true) String select_anticipo,
            @RequestParam(value="select_apl_anticipo", required=false) String select_apl_anticipo,
            @RequestParam(value="select_apl_factura", required=false) String select_apl_factura,
            @RequestParam(value="select_cacelacion", required=false) String select_cacelacion,
            @RequestParam(value="select_incluye_iva", required=false) String select_incluye_iva,
            @RequestParam(value="check_requiere_oc", required=false) String check_requiere_oc,
            @RequestParam(value="select_consecutivo_sucursal", required=false) String select_consecutivo_sucursal,
            Model model,@ModelAttribute("user") UserSessionData user
            ) {
            
            HashMap<String, String> jsonretorno = new HashMap<String, String>();
            HashMap<String, String> succes = new HashMap<String, String>();
            Integer app_selected = 62;
            String command_selected = "new";
            Integer id_usuario= user.getUserId();//variable para el id  del usuario
            String extra_data_array = "'sin datos'";
            
            //verifica los campos CheckBox y les asigna true o false
            check_requiere_oc = StringHelper.verificarCheckBox(check_requiere_oc);
            
            if( identificador.equals("0") ){
                command_selected = "new";
            }else{
                command_selected = "edit";
            }
            
            String data_string = 
                    app_selected+"___"+
                    command_selected+"___"+
                    id_usuario+"___"+ 
                    identificador+"___"+
                    select_sucursal+"___"+
                    select_anticipo+"___"+
                    select_apl_anticipo+"___"+
                    select_apl_factura+"___"+
                    select_cacelacion+"___"+
                    select_incluye_iva+"___"+
                    check_requiere_oc+"___"+
                    select_consecutivo_sucursal;
            
            succes = this.getCxpDao().selectFunctionValidateAaplicativo(data_string,app_selected,extra_data_array);
            
            log.log(Level.INFO, "despues de validacion {0}", String.valueOf(succes.get("success")));
            String actualizo = "0";
            
            if( String.valueOf(succes.get("success")).equals("true") ){
                actualizo = this.getCxpDao().selectFunctionForCxpAdmProcesos(data_string, extra_data_array);
            }
            
            jsonretorno.put("success",String.valueOf(succes.get("success")));
            
            log.log(Level.INFO, "Salida json {0}", String.valueOf(jsonretorno.get("success")));
        return jsonretorno;
    }
    
    
    
    
    //cambiar a borrado logico un registro
    @RequestMapping(method = RequestMethod.POST, value="/logicDelete.json")
    public @ResponseBody HashMap<String, String> logicDeleteJson(
            @RequestParam(value="identificador", required=true) Integer identificador,
            @RequestParam(value="iu", required=true) String id_user,
            Model model
            ) {
        
        System.out.println("Borrado logico de un parametro de anticipo");
        //decodificar id de usuario
        Integer id_usuario = Integer.parseInt(Base64Coder.decodeString(id_user));
        
        Integer app_selected = 62;
        String command_selected = "delete";
        String extra_data_array = "'sin datos'";
        
        String data_string = app_selected+"___"+command_selected+"___"+id_usuario+"___"+identificador;
        
        HashMap<String, String> jsonretorno = new HashMap<String, String>();
        
        jsonretorno.put("success",String.valueOf( this.getCxpDao().selectFunctionForCxpAdmProcesos(data_string,extra_data_array)) );
        
        return jsonretorno;
    }
    
    
    
    
    
    
}
