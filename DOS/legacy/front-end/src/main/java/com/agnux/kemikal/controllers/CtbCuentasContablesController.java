package com.agnux.kemikal.controllers;

import com.agnux.cfd.v2.Base64Coder;
import com.agnux.common.helpers.StringHelper;
import com.agnux.common.obj.DataPost;
import com.agnux.common.obj.ResourceProject;
import com.agnux.common.obj.UserSessionData;
import com.agnux.kemikal.interfacedaos.CtbInterfaceDao;
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
@RequestMapping("/cuentascontables/")
public class CtbCuentasContablesController {
    
    ResourceProject resource = new ResourceProject();
    private static final Logger log  = Logger.getLogger(CtbCuentasContablesController.class.getName());
    
    @Autowired
    @Qualifier("daoCtb")
    private CtbInterfaceDao ctbDao;
    
    public CtbInterfaceDao getCtbDao() {
        return ctbDao;
    }
    
    @Autowired
    @Qualifier("daoHome")
    private HomeInterfaceDao HomeDao;
    
    public HomeInterfaceDao getHomeDao() {
        return HomeDao;
    }
    
    @RequestMapping(value="/startup.agnux")
    public ModelAndView startUp(HttpServletRequest request, HttpServletResponse response, 
            @ModelAttribute("user") UserSessionData user)
            throws ServletException, IOException {
        log.log(Level.INFO, "Ejecutando starUp de {0}", CtbCuentasContablesController.class.getName());
        LinkedHashMap<String,String> infoConstruccionTabla = new LinkedHashMap<String,String>();
        
        infoConstruccionTabla.put("id", "Acciones:90");
        infoConstruccionTabla.put("m", "M:30");
        infoConstruccionTabla.put("c", "C:30");
        infoConstruccionTabla.put("cuenta", "Cuenta:170");
        infoConstruccionTabla.put("detalle","Detalle:50");
        infoConstruccionTabla.put("descripcion","Descripci&oacute;n:330");
        infoConstruccionTabla.put("nivel", "Nivel&nbsp;Cta.:70");
        infoConstruccionTabla.put("estatus","Estatus:100");
        
        ModelAndView x = new ModelAndView("cuentascontables/startup", "title", "Cuentas Contables");
        
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
    
    
    
    @RequestMapping(value="/getAllCuentasContables.json", method = RequestMethod.POST)
    public @ResponseBody HashMap<String,ArrayList<HashMap<String, Object>>> getAllCuentasContablesJson(
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
        
        //Aplicativo Catalogo de Cuentas Contables
        Integer app_selected = 106;
        
        //Decodificar id de usuario
        Integer id_usuario = Integer.parseInt(Base64Coder.decodeString(id_user_cod));
        userDat = this.getHomeDao().getUserById(id_usuario);
        //Integer id_empresa = Integer.parseInt(userDat.get("empresa_id"));
        Integer id_sucursal = Integer.parseInt(userDat.get("sucursal_id"));
        
        //Variables para el buscador
        String cta_mayor = StringHelper.isNullString(String.valueOf(has_busqueda.get("cta_mayor")));
        String descripcion = "%"+StringHelper.isNullString(String.valueOf(has_busqueda.get("descripcion")))+"%";
        String sucursal = StringHelper.isNullString(String.valueOf(has_busqueda.get("sucursal")));
        
        if(this.getCtbDao().getUserRolAdmin(id_usuario)<=0){
            // Si el usuario no es administrador y la sucursal es cero, se asigna la sucursal del usuario.
            if(Integer.valueOf(sucursal)<=0){
                sucursal = String.valueOf(id_sucursal);
            }
        }
        
        String data_string = app_selected+"___"+id_usuario+"___"+cta_mayor+"___"+descripcion+"___"+sucursal;
        
        //obtiene total de registros en base de datos, con los parametros de busqueda
        int total_items = this.getCtbDao().countAll(data_string);
        
        //calcula el total de paginas
        int total_pags = resource.calculaTotalPag(total_items,items_por_pag);
        
        //variables que necesita el datagrid, para no tener que hacer uno por cada aplicativo
        DataPost dataforpos = new DataPost(orderby, desc, items_por_pag, pag_start, display_pag, input_json, cadena_busqueda,total_items,total_pags, id_user_cod);
        
        int offset = resource.__get_inicio_offset(items_por_pag, pag_start);
        
        //obtiene los registros para el grid, de acuerdo a los parametros de busqueda
        jsonretorno.put("Data", this.getCtbDao().getCuentasContables_PaginaGrid(data_string, offset, items_por_pag, orderby, desc));
        //obtiene el hash para los datos que necesita el datagrid
        jsonretorno.put("DataForGrid", dataforpos.formaHashForPos(dataforpos));
        
        return jsonretorno;
    }
    
    
    
    
    @RequestMapping(method = RequestMethod.POST, value="/getInicializar.json")
    public @ResponseBody HashMap<String,Object> getCuentasMayorJson(
            @RequestParam(value="iu", required=true) String id_user,
            Model model
        ) {
        
        log.log(Level.INFO, "Ejecutando getCuentasMayorJson de {0}", CtbCuentasContablesController.class.getName());
        HashMap<String,Object> jsonretorno = new HashMap<String,Object>();
        HashMap<String, String> userDat = new HashMap<String, String>();
        HashMap<String, Object> data = new HashMap<String, Object>();
        
        //Decodificar id de usuario
        Integer id_usuario = Integer.parseInt(Base64Coder.decodeString(id_user));
        userDat = this.getHomeDao().getUserById(id_usuario);
        Integer id_empresa = Integer.parseInt(userDat.get("empresa_id"));
        Integer id_sucursal = Integer.parseInt(userDat.get("sucursal_id"));
        Integer idSucUser=id_sucursal;
        
        data.put("suc", id_sucursal);
        
        if(this.getCtbDao().getUserRolAdmin(id_usuario)>0){
            data.put("versuc", true);
            //Aqui se le asigna cero al id sucursal para hacer que la busqueda de Centros de Costos(CC) sea en todas las sucursales.
            idSucUser=0;
        }else{
            data.put("versuc", false);
        }
        
        jsonretorno.put("Suc", this.getCtbDao().getCtb_Sucursales(id_empresa));
        jsonretorno.put("CC", this.getCtbDao().getPolizasContables_CentrosCostos(id_empresa, idSucUser));
        jsonretorno.put("CtaMay", this.getCtbDao().getCuentasContables_CuentasMayor(id_empresa));
        jsonretorno.put("Data", data);
        
        return jsonretorno;
    }
    
    
    @RequestMapping(method = RequestMethod.POST, value="/getCuentaContable.json")
    public @ResponseBody HashMap<String,ArrayList<HashMap<String, Object>>> getCuentaContableJson(
            @RequestParam(value="id", required=true) Integer id,
            @RequestParam(value="iu", required=true) String id_user,
            Model model
            ) {
        
        log.log(Level.INFO, "Ejecutando getCuentaContableJson de {0}", CtbCuentasContablesController.class.getName());
        HashMap<String,ArrayList<HashMap<String, Object>>> jsonretorno = new HashMap<String,ArrayList<HashMap<String, Object>>>();
        ArrayList<HashMap<String, Object>> datosCC = new ArrayList<HashMap<String, Object>>();
        HashMap<String, String> userDat = new HashMap<String, String>();
        ArrayList<HashMap<String, Object>> arrayExtra = new ArrayList<HashMap<String, Object>>();
        HashMap<String, Object> extra = new HashMap<String, Object>();
        
        //decodificar id de usuario
        Integer id_usuario = Integer.parseInt(Base64Coder.decodeString(id_user));
        userDat = this.getHomeDao().getUserById(id_usuario);
        
        Integer id_empresa = Integer.parseInt(userDat.get("empresa_id"));
        extra.put("incluye_contab", userDat.get("incluye_contab"));//esta variable indica si la empresa incluye modulo de Contabilidad
        extra.put("nivel_cta", userDat.get("nivel_cta"));
        arrayExtra.add(0,extra);
        
        if( id != 0  ){
            datosCC = this.getCtbDao().getCuentasContables_Datos(id);
        }
        
        jsonretorno.put("Cc", datosCC);
        jsonretorno.put("CtaMay", this.getCtbDao().getCuentasContables_CuentasMayor(id_empresa));
        //Se le pasa como parámetro 1 para indicar que solo debe tomar las aplicaciones que se deben mostrar en el catalogo de cuentas contables
        jsonretorno.put("App", this.getCtbDao().getCtb_Aplicaciones(1));
        jsonretorno.put("Extras", arrayExtra);
        
        return jsonretorno;
    }
    
    
    
    //Buscador de cuentas agrupadoras del sat
    @RequestMapping(method = RequestMethod.POST, value="/getCtaAgrupadorSat.json")
    public @ResponseBody HashMap<String,Object> getCtaAgrupadorSatJson(
            @RequestParam(value="codigo", required=true) String codigo,
            @RequestParam(value="descripcion", required=true) String descripcion,
            @RequestParam(value="iu", required=true) String id_user,
            Model model
        ) {
        
        HashMap<String,Object> jsonretorno = new HashMap<String,Object>();
        /*
        HashMap<String, String> userDat = new HashMap<String, String>();
        
        //decodificar id de usuario
        Integer id_usuario = Integer.parseInt(Base64Coder.decodeString(id_user));
        
        userDat = this.getHomeDao().getUserById(id_usuario);
        Integer id_empresa = Integer.parseInt(userDat.get("empresa_id"));
        */
        jsonretorno.put("Ctas", this.getCtbDao().getBuscadorCuentasAgrupadorasSat(codigo,descripcion));
        
        return jsonretorno;
    }
    
    
    //Buscador datos de una cuenta en especifico
    @RequestMapping(method = RequestMethod.POST, value="/getCtaSat.json")
    public @ResponseBody HashMap<String,Object> getCtaSatJson(
            @RequestParam(value="codigo", required=true) String codigo,
            Model model
        ) {
        
        HashMap<String,Object> jsonretorno = new HashMap<String,Object>();
        jsonretorno.put("Cta", this.getCtbDao().getDataCtaSat(codigo));
        
        return jsonretorno;
    }
    
    
    
    //crear y editar
    @RequestMapping(method = RequestMethod.POST, value="/edit.json")
    public @ResponseBody HashMap<String, String> editJson(
            @RequestParam(value="identificador", required=true) String id,
            @RequestParam(value="cuenta", required=true) String cuenta,
            @RequestParam(value="scuenta", required=true) String scuenta,
            @RequestParam(value="sscuenta", required=true) String sscuenta,
            @RequestParam(value="ssscuenta", required=true) String ssscuenta,
            @RequestParam(value="sssscuenta", required=true) String sssscuenta,
            @RequestParam(value="select_cuenta_mayor", required=true) String cuenta_mayor,
            @RequestParam(value="select_centro_costo", required=true) String select_centro_costo,
            @RequestParam(value="descripcion", required=true) String descripcion,
            @RequestParam(value="select_estatus", required=true) String estatus,
            @RequestParam(value="chk_cta_detalle", required=false) String cta_detalle,
            @RequestParam(value="descripcion_es", required=true) String descripcion_es,
            @RequestParam(value="descripcion_in", required=true) String descripcion_in,
            @RequestParam(value="descripcion_otro", required=true) String descripcion_otro,
            @RequestParam(value="select_sucursal", required=false) String select_sucursal,
            @RequestParam(value="select_agrupador", required=false) String select_agrupador,
            @RequestParam(value="select_nivel", required=false) String select_nivel,
            @RequestParam(value="select_naturaleza", required=false) String select_naturaleza,
            @RequestParam(value="select_tipo_cta", required=false) String select_tipo_cta,
            @RequestParam(value="ctasat_id", required=false) String ctasat_id,
            Model model,@ModelAttribute("user") UserSessionData user
        ) {
        
        HashMap<String, String> jsonretorno = new HashMap<String, String>();
        HashMap<String, String> succes = new HashMap<String, String>();
        //Catalogo de Cuentas Contables
        Integer app_selected = 106;
        String command_selected = "new";
        Integer id_usuario= user.getUserId();//variable para el id  del usuario
        String extra_data_array = "'sin datos'";
        String actualizo = "0";
        
        if( id.equals("0") ){
            command_selected = "new";
        }else{
            command_selected = "edit";
        }
        
        if(cta_detalle == null || !cta_detalle.equals("on")){
            cta_detalle = "0";
        }else{
            cta_detalle = "1";
        }
        
        select_sucursal = StringHelper.verificarSelect(select_sucursal);
        ctasat_id = StringHelper.verificarSelect(ctasat_id);
        
        String data_string = app_selected+"___"+command_selected+"___"+id_usuario+"___"+id+"___"+cuenta+"___"+scuenta+"___"+sscuenta+"___"+ssscuenta+"___"+sssscuenta+"___"+cuenta_mayor+"___"+estatus+"___"+cta_detalle+"___"+descripcion_es.toUpperCase()+"___"+descripcion_in.toUpperCase()+"___"+descripcion_otro.toUpperCase()+"___"+select_centro_costo+"___"+select_sucursal+"___"+select_nivel+"___"+select_naturaleza+"___"+select_tipo_cta+"___"+select_agrupador+"___"+ctasat_id;
        
        succes = this.getCtbDao().selectFunctionValidateAaplicativo(data_string,app_selected,extra_data_array);
        
        log.log(Level.INFO, "despues de validacion {0}", String.valueOf(succes.get("success")));
        if( String.valueOf(succes.get("success")).equals("true") ){
            actualizo = this.getCtbDao().selectFunctionForCtbAdmProcesos(data_string, extra_data_array);
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
        
        Integer app_selected = 106;
        String command_selected = "delete";
        String extra_data_array = "'sin datos'";
        String data_string = app_selected+"___"+command_selected+"___"+id_usuario+"___"+id;
        
        System.out.println("Ejecutando borrado logico de Cuenta Contable");
        jsonretorno.put("success",String.valueOf( this.getCtbDao().selectFunctionForCtbAdmProcesos(data_string,extra_data_array)) );
        
        return jsonretorno;
    }   
}
