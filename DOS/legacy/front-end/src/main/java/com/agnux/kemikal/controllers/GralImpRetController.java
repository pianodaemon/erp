package com.agnux.kemikal.controllers;

import com.agnux.cfd.v2.Base64Coder;
import com.agnux.common.helpers.StringHelper;
import com.agnux.common.obj.DataPost;
import com.agnux.common.obj.ResourceProject;
import com.agnux.common.obj.UserSessionData;
import com.agnux.kemikal.interfacedaos.CtbInterfaceDao;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;


@Controller
@SessionAttributes({"user"})
@RequestMapping("/gralimpret/")
public class GralImpRetController {
    ResourceProject resource = new ResourceProject();
    private static final Logger log  = Logger.getLogger(GralImpRetController.class.getName());
    
    @Autowired
    @Qualifier("daoGral")
    private GralInterfaceDao gralDao;
    
    @Autowired
    @Qualifier("daoHome")
    private HomeInterfaceDao HomeDao;
    
    public GralInterfaceDao getGralDao() {
        return gralDao;
    }
    
    public HomeInterfaceDao getHomeDao() {
        return HomeDao;
    }
    
    @Autowired
    @Qualifier("daoCtb")
    private CtbInterfaceDao ctbDao;
    
    public CtbInterfaceDao getCtbDao() {
        return ctbDao;
    }
    
    @RequestMapping(value="/startup.agnux")
    public ModelAndView startUp(HttpServletRequest request, HttpServletResponse response, 
            @ModelAttribute("user") UserSessionData user
        )throws ServletException, IOException {
        
        log.log(Level.INFO, "Ejecutando starUp de {0}", GralImpRetController.class.getName());
        LinkedHashMap<String,String> infoConstruccionTabla = new LinkedHashMap<String,String>();
        infoConstruccionTabla.put("id", "Acciones:70");
        infoConstruccionTabla.put("titulo", "Titulo:200");
        infoConstruccionTabla.put("tasa", "Tasa:100");
        
        ModelAndView x = new ModelAndView("gralimpret/startup", "title", "Cat&aacute;logo de IVA Retenido");
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
    
    
    @RequestMapping(value="/getAllImptos.json", method = RequestMethod.POST)
    public @ResponseBody HashMap<String,ArrayList<HashMap<String, Object>>> getAllImptosJson(
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
        
        //Catalogo de IVA Retenido
        Integer app_selected = 205;
        
        //Decodificar id de usuario
        Integer id_usuario = Integer.parseInt(Base64Coder.decodeString(id_user_cod));
        
        //Variables para el buscador
        String titulo = "%"+StringHelper.isNullString(String.valueOf(has_busqueda.get("titulo")))+"%";
        
        String data_string = app_selected+"___"+id_usuario+"___"+titulo;
        
        //Obtiene total de registros en base de datos, con los parametros de busqueda
        int total_items = this.getGralDao().countAll(data_string);
        
        //Calcula el total de paginas
        int total_pags = resource.calculaTotalPag(total_items,items_por_pag);
        
        //Variables que necesita el datagrid, para no tener que hacer uno por cada aplicativo
        DataPost dataforpos = new DataPost(orderby, desc, items_por_pag, pag_start, display_pag, input_json, cadena_busqueda,total_items,total_pags, id_user_cod);
        
        int offset = resource.__get_inicio_offset(items_por_pag, pag_start);
        
        //Obtiene los registros para el grid, de acuerdo a los parametros de busqueda
        jsonretorno.put("Data", this.getGralDao().getImpRet_PaginaGrid(data_string, offset, items_por_pag, orderby, desc));
               
        //Obtiene el hash para los datos que necesita el datagrid
        jsonretorno.put("DataForGrid", dataforpos.formaHashForPos(dataforpos));
        
        return jsonretorno;
    }
    
    
    
    @RequestMapping(method = RequestMethod.POST, value="/getImpto.json")
    public @ResponseBody HashMap<String,Object> getImptoJson(
            @RequestParam(value="id", required=true) Integer id,
            @RequestParam(value="iu", required=true) String id_user_cod,
            Model model
        ){
        
        log.log(Level.INFO, "Ejecutando getImptoJson de {0}", GralImpRetController.class.getName());
        HashMap<String,Object> jsonretorno = new HashMap<String,Object>();
        HashMap<String, String> userDat = new HashMap<String, String>();
        ArrayList<HashMap<String, Object>> data = new ArrayList<HashMap<String, Object>>();
        HashMap<String, Object> param = new HashMap<String, Object>();
        ArrayList<HashMap<String, Object>> cta = new ArrayList<HashMap<String, Object>>();
        
        //Decodificar id de usuario
        Integer id_usuario = Integer.parseInt(Base64Coder.decodeString(id_user_cod));
        userDat = this.getHomeDao().getUserById(id_usuario);
        Integer id_empresa = Integer.parseInt(userDat.get("empresa_id"));
        //Integer id = Integer.parseInt(userDat.get("id"));
        
        param.put("nivel_cta", userDat.get("nivel_cta"));
        param.put("contab", userDat.get("incluye_contab"));
        
        if(id!=0){
            data = this.getGralDao().getImpRet_Datos(id);
            
            if(userDat.get("incluye_contab").equals("true")){
                cta = this.getGralDao().getImpRet_DatosContabilidad(id);
            }
        }
        
       jsonretorno.put("Data", data);
       jsonretorno.put("Cta", cta);
       jsonretorno.put("CtaMay", this.getCtbDao().getPolizasContables_CuentasMayor(id_empresa));
       jsonretorno.put("Param", param);
       
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
        
        //System.out.println("cta_mayor:"+cta_mayor_class.split("_")[0]+"   clasificacion:"+cta_mayor_class.split("_")[1]);
        
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
            @RequestParam(value="identificador", required=true) Integer id,
            @RequestParam(value="titulo", required=true) String titulo,
            @RequestParam(value="tasa", required=true) String tasa,
            @RequestParam(value="cta_id", required=true) String cta_id,
            @ModelAttribute("user") UserSessionData user,
            Model model
        ) {
        
        HashMap<String, String> jsonretorno = new HashMap<String, String>();
        HashMap<String, String> succes = new HashMap<String, String>();
        //Catalogo de IVA Retenido
        Integer app_selected = 205;
        
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
        
        String data_string = app_selected+"___"+command_selected+"___"+id_usuario+"___"+id+"___"+titulo.toUpperCase()+"___"+tasa+"___"+cta_id;
        
        succes = this.getGralDao().selectFunctionValidateAaplicativo(data_string, app_selected, extra_data_array);
        log.log(Level.INFO, "despues de validacion {0}", String.valueOf(succes.get("success")));
        if( String.valueOf(succes.get("success")).equals("true") ){
            actualizo = this.getGralDao().selectFunctionForThisApp(data_string, extra_data_array);
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
        
        //Catalogo de IVA Retenido
        Integer app_selected = 205;
        
        String command_selected = "delete";
        String extra_data_array = "'sin datos'";
        String data_string = app_selected+"___"+command_selected+"___"+id_usuario+"___"+id;
        
        //System.out.println("Ejecutando borrado logico iepss");
        //System.out.println("cadena a enviar para el cambio de borrado logico:  "+data_string);
        jsonretorno.put("success",String.valueOf( this.getGralDao().selectFunctionForThisApp(data_string, extra_data_array)));
        return jsonretorno;
    }
}
