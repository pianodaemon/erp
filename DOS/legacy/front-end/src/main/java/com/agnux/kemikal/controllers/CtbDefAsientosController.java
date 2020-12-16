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
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;


@Controller
@SessionAttributes({"user"})
@RequestMapping("/ctbdefasientos/")
public class CtbDefAsientosController {
    ResourceProject resource = new ResourceProject();
    private static final Logger log  = Logger.getLogger(CtbDefAsientosController.class.getName());
    
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
        log.log(Level.INFO, "Ejecutando starUp de {0}", CtbDefAsientosController.class.getName());
        LinkedHashMap<String,String> infoConstruccionTabla = new LinkedHashMap<String,String>();
        
        infoConstruccionTabla.put("id", "Acciones:90");
        infoConstruccionTabla.put("folio", "Folio:80");
        infoConstruccionTabla.put("nombre","Nombre:300");
        infoConstruccionTabla.put("tipo", "Tipo de P&oacute;liza:180");
        
                    
        ModelAndView x = new ModelAndView("ctbdefasientos/startup", "title", "Definici&oacute;n de Asientos Contables");
        
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
    
    
    @RequestMapping(value="/getAllAsientos.json", method = RequestMethod.POST)
    public @ResponseBody HashMap<String,ArrayList<HashMap<String, Object>>> getAllAsientosJson(
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
        HashMap<String, String> userDat = new HashMap<String, String>();
        
        //Aplicativo Definicion de Asientos Contables(CTB)
        Integer app_selected = 203;
        
        //Decodificar id de usuario
        Integer id_usuario = Integer.parseInt(Base64Coder.decodeString(id_user_cod));
        userDat = this.getHomeDao().getUserById(id_usuario);
        Integer id_empresa = Integer.parseInt(userDat.get("empresa_id"));
        Integer id_sucursal = Integer.parseInt(userDat.get("sucursal_id"));
        
        //Variables para el buscador
        //String sucursal = StringHelper.isNullString(String.valueOf(has_busqueda.get("sucursal")));
        String tipo_pol = StringHelper.isNullString(String.valueOf(has_busqueda.get("tipo_pol")));
        String folio = "%"+StringHelper.isNullString(String.valueOf(has_busqueda.get("folio")))+"%";
        String nombre = "%"+StringHelper.isNullString(String.valueOf(has_busqueda.get("nombre")))+"%";
        /*
        if(this.getCtbDao().getUserRolAdmin(id_usuario)<=0){
            // Si el usuario no es administrador y la sucursal es cero, se asigna la sucursal del usuario actual.
            if(Integer.valueOf(sucursal)<=0){
                sucursal = String.valueOf(id_sucursal);
            }
        }
        */
        
        String data_string = app_selected+"___"+id_usuario+"___"+id_sucursal+"___"+tipo_pol+"___"+folio+"___"+nombre;
        //System.out.println("data_string: "+data_string);
        //Obtiene total de registros en base de datos, con los parametros de busqueda
        int total_items = this.getCtbDao().countAll(data_string);
        
        //Calcula el total de paginas
        int total_pags = resource.calculaTotalPag(total_items,items_por_pag);
        
        //Variables que necesita el datagrid, para no tener que hacer uno por cada aplicativo
        DataPost dataforpos = new DataPost(orderby, desc, items_por_pag, pag_start, display_pag, input_json, cadena_busqueda,total_items,total_pags, id_user_cod);
        
        int offset = resource.__get_inicio_offset(items_por_pag, pag_start);
        
        //Obtiene los registros para el grid, de acuerdo a los parametros de busqueda
        jsonretorno.put("Data", this.getCtbDao().getCtbDefinicionAsientos_PaginaGrid(data_string, offset, items_por_pag, orderby, desc));
        
        //Obtiene el hash para los datos que necesita el datagrid
        jsonretorno.put("DataForGrid", dataforpos.formaHashForPos(dataforpos));
        
        return jsonretorno;
    }
    
    
    
    
    @RequestMapping(method = RequestMethod.POST, value="/getInicializar.json")
    public @ResponseBody HashMap<String,Object> getCuentasMayorJson(
            @RequestParam(value="iu", required=true) String id_user,
            Model model
        ) {
        
        log.log(Level.INFO, "Ejecutando getInicializarJson de {0}", CtbDefAsientosController.class.getName());
        HashMap<String,Object> jsonretorno = new HashMap<String,Object>();
        HashMap<String, String> userDat = new HashMap<String, String>();
        HashMap<String, Object> data = new HashMap<String, Object>();
        
        //Decodificar id de usuario
        Integer id_usuario = Integer.parseInt(Base64Coder.decodeString(id_user));
        userDat = this.getHomeDao().getUserById(id_usuario);
        Integer id_empresa = Integer.parseInt(userDat.get("empresa_id"));
        Integer id_sucursal = Integer.parseInt(userDat.get("sucursal_id"));
        
        data.put("suc", id_sucursal);
        data.put("nivel_cta", userDat.get("nivel_cta"));
        
        if(this.getCtbDao().getUserRolAdmin(id_usuario)>0){
            data.put("versuc", true);
        }else{
            data.put("versuc", false);
        }
        
        jsonretorno.put("TP", this.getCtbDao().getPolizasContables_TiposPolizas(id_empresa));
        jsonretorno.put("Suc", this.getCtbDao().getCtb_Sucursales(id_empresa));
        jsonretorno.put("CtaMay", this.getCtbDao().getPolizasContables_CuentasMayor(id_empresa));
        jsonretorno.put("Campos", this.getCtbDao().getCtbDefinicionAsientos_Campos());
        jsonretorno.put("Data", data);
        
        return jsonretorno;
    }
    
    
    
    
    @RequestMapping(method = RequestMethod.POST, value="/getAsiento.json")
    public @ResponseBody HashMap<String,Object> getAsientoJson(
            @RequestParam(value="id", required=true) Integer id,
            @RequestParam(value="iu", required=true) String id_user,
            Model model
        ) {
        
        log.log(Level.INFO, "Ejecutando getPolizaJson de {0}", CtbDefAsientosController.class.getName());
        HashMap<String,Object> jsonretorno = new HashMap<String,Object>();
        ArrayList<HashMap<String, Object>> datos = new ArrayList<HashMap<String, Object>>();
        ArrayList<HashMap<String, Object>> datosGrid = new ArrayList<HashMap<String, Object>>();
        HashMap<String, String> userDat = new HashMap<String, String>();
        ArrayList<HashMap<String, Object>> arrayExtra = new ArrayList<HashMap<String, Object>>();
        HashMap<String, Object> extra = new HashMap<String, Object>();
        
        //Decodificar id de usuario
        Integer id_usuario = Integer.parseInt(Base64Coder.decodeString(id_user));
        userDat = this.getHomeDao().getUserById(id_usuario);
        //Integer id_empresa = Integer.parseInt(userDat.get("empresa_id"));
        //Integer id_sucursal = Integer.parseInt(userDat.get("sucursal_id"));
        
        //Esta variable indica si la empresa incluye modulo de Contabilidad
        extra.put("nivel_cta", userDat.get("nivel_cta"));
        
        arrayExtra.add(0,extra);
        
        if( id != 0  ){
            datos = this.getCtbDao().getCtbDefinicionAsientos_Datos(id);
            datosGrid = this.getCtbDao().getCtbDefinicionAsientos_DatosGrid(id);
        }
        
        jsonretorno.put("Data", datos);
        jsonretorno.put("Grid", datosGrid);
        //Se le pasa como parámetro 2 para indicar que solo debe tomar las aplicaciones que se deben mostrar en el Programa de Definicion de Asientos
        jsonretorno.put("App", this.getCtbDao().getCtb_Aplicaciones(2));
        jsonretorno.put("Extras", arrayExtra);
        
        return jsonretorno;
    }
    
    
    
    
    //Metodo para el Buscador de Cuentas Contables
    @RequestMapping(method = RequestMethod.POST, value="/getBuscadorCuentasContables.json")
    public @ResponseBody HashMap<String,ArrayList<HashMap<String, String>>> getBuscadorCuentasContablesJson(
            @RequestParam(value="cta_mayor", required=true) String cta_mayor_class,
            @RequestParam(value="detalle", required=true) Integer detalle,
            @RequestParam(value="clasifica", required=false) String clasifica,
            @RequestParam(value="cta", required=false) String cta,
            @RequestParam(value="scta", required=false) String scta,
            @RequestParam(value="sscta", required=false) String sscta,
            @RequestParam(value="ssscta", required=false) String ssscta,
            @RequestParam(value="sssscta", required=false) String sssscta,
            @RequestParam(value="descripcion", required=false) String descripcion,
            @RequestParam(value="iu", required=true) String id_user,
            Model model
        ) {
        
        log.log(Level.INFO, "Ejecutando getBuscadorCuentasContablesJson de {0}", CtbDefAsientosController.class.getName());
        HashMap<String,ArrayList<HashMap<String, String>>> jsonretorno = new HashMap<String,ArrayList<HashMap<String, String>>>();
        HashMap<String, String> userDat = new HashMap<String, String>();
        ArrayList<HashMap<String, String>> cuentasContables = new ArrayList<HashMap<String, String>>();
        
        //decodificar id de usuario
        Integer id_usuario = Integer.parseInt(Base64Coder.decodeString(id_user));
        userDat = this.getHomeDao().getUserById(id_usuario);
        Integer id_empresa = Integer.parseInt(userDat.get("empresa_id"));
        
        System.out.println("cta_mayor:"+cta_mayor_class.split("_")[0]+"   clasificacion:"+cta_mayor_class.split("_")[1]);
        
        cuentasContables = this.getCtbDao().getPolizasContables_CuentasContables(Integer.valueOf(cta_mayor_class.split("_")[0]), Integer.valueOf(cta_mayor_class.split("_")[1]), detalle, clasifica, cta, scta, sscta, ssscta, sssscta, descripcion, id_empresa);
        
        jsonretorno.put("CtaContables", cuentasContables);
        
        return jsonretorno;
    }
    
    
    
    
    //Obtiene dados de una cuenta contable en espcifico
    @RequestMapping(method = RequestMethod.POST, value="/getDataCta.json")
    public @ResponseBody HashMap<String,ArrayList<HashMap<String, Object>>> getDataCtaJson(
            @RequestParam(value="detalle", required=true) Integer detalle,
            @RequestParam(value="cta", required=false) String cta,
            @RequestParam(value="scta", required=false) String scta,
            @RequestParam(value="sscta", required=false) String sscta,
            @RequestParam(value="ssscta", required=false) String ssscta,
            @RequestParam(value="sssscta", required=false) String sssscta,
            @RequestParam(value="iu", required=true) String id_user,
            Model model
        ) {
        
        log.log(Level.INFO, "Ejecutando getDataCtaJson de {0}", CtbDefAsientosController.class.getName());
        HashMap<String,ArrayList<HashMap<String, Object>>> jsonretorno = new HashMap<String,ArrayList<HashMap<String, Object>>>();
        HashMap<String, String> userDat = new HashMap<String, String>();
        
        //Decodificar id de usuario
        Integer id_usuario = Integer.parseInt(Base64Coder.decodeString(id_user));
        userDat = this.getHomeDao().getUserById(id_usuario);
        Integer id_empresa = Integer.parseInt(userDat.get("empresa_id"));
        Integer id_sucursal = Integer.parseInt(userDat.get("sucursal_id"));
        
        if(this.getCtbDao().getUserRolAdmin(id_usuario)>0){
            //Sucursal cero cuando el usuario es administrador, esto para permitir la busque de la cuenta contable sin importar la sucursal
            id_sucursal=0;
        }
        
        jsonretorno.put("Cta", this.getCtbDao().getDatosCuentaContable(detalle, cta, scta, sscta, ssscta, sssscta, id_empresa, id_sucursal));
        
        return jsonretorno;
    }
    
    
    
    //Crear y editar
    @RequestMapping(method = RequestMethod.POST, value="/edit.json")
    public @ResponseBody HashMap<String, String> editJson(
            @RequestParam(value="identificador", required=true) String identificador,
            @RequestParam(value="nombre", required=true) String nombre,
            @RequestParam(value="select_fecha", required=true) String select_fecha,
            @RequestParam(value="select_pol_num", required=true) String select_pol_num,
            @RequestParam(value="select_tipo", required=true) String select_tipo,
            @RequestParam(value="select_app", required=true) String select_app,
            @RequestParam(value="id_det", required=false) String[] id_det,
            @RequestParam(value="delete", required=false) String[] eliminado,
            @RequestParam(value="id_cta", required=false) String[] id_cta,
            @RequestParam(value="select_mov", required=false) String[] select_mov,
            @RequestParam(value="select_importe", required=false) String[] select_importe,
            @RequestParam(value="detalle", required=false) String[] detalle,
            @RequestParam(value="no_tr", required=false) String[] no_tr,
            Model model,@ModelAttribute("user") UserSessionData user
        ) {
        
        HashMap<String, String> jsonretorno = new HashMap<String, String>();
        HashMap<String, String> succes = new HashMap<String, String>();
        
        //Aplicativo Definicion de Asientos Contables(CTB)
        Integer app_selected = 203;
        String command_selected = "new";
        Integer id_usuario= user.getUserId();//variable para el id  del usuario
        String extra_data_array = "'sin_datos'";
        String actualizo = "0";
        
        if(id_det!=null){
            String arreglo[];
            arreglo = new String[eliminado.length];

            for(int i=0; i<eliminado.length; i++) {
                arreglo[i]= "'"+ eliminado[i] +"___"+ id_det[i] +"___"+ id_cta[i] +"___"+ select_mov[i] +"___"+ detalle[i] +"___"+ select_importe[i] +"'";
                //System.out.println(arreglo[i]);
            }
            
            //Serializar el arreglo
            extra_data_array = StringUtils.join(arreglo, ",");
        }
        
        //System.out.println(extra_data_array);
        if( identificador.equals("0") ){
            command_selected = "new";
        }else{
            command_selected = "edit";
        }
        
        String data_string = app_selected+"___"+command_selected+"___"+id_usuario+"___"+identificador+"___"+nombre.trim().toUpperCase()+"___"+select_fecha+"___"+select_pol_num+"___"+select_tipo+"___"+select_app;
        
        succes = this.getCtbDao().selectFunctionValidateAaplicativo(data_string,app_selected,extra_data_array);
        
        log.log(Level.INFO, "despues de validacion {0}", String.valueOf(succes.get("success")));
        if( String.valueOf(succes.get("success")).equals("true") ){
            actualizo = this.getCtbDao().selectFunctionForCtbAdmProcesos(data_string, extra_data_array);
        }
        
        jsonretorno.put("success",String.valueOf(succes.get("success")));
        
        log.log(Level.INFO, "Salida json {0}", String.valueOf(jsonretorno.get("success")));
        return jsonretorno;
    }
    
}
