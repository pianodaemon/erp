package com.agnux.kemikal.controllers;

import com.agnux.cfd.v2.Base64Coder;
import com.agnux.common.helpers.StringHelper;
import com.agnux.common.helpers.TimeHelper;
import com.agnux.common.obj.DataPost;
import com.agnux.common.obj.ResourceProject;
import com.agnux.common.obj.UserSessionData;
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
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.servlet.ModelAndView;


@Controller
@SessionAttributes({"user"})
@RequestMapping("/gralconfigperiodospago/")
public class GralConfigPeriodosPagoController {
    ResourceProject resource = new ResourceProject();
    private static final Logger log  = Logger.getLogger(GralConfigPeriodosPagoController.class.getName());
    
    @Autowired
    @Qualifier("daoGral")
    private GralInterfaceDao gralDao;
    
    @Autowired
    @Qualifier("daoHome")   //permite controlar usuarios que entren
    private HomeInterfaceDao HomeDao;
    
    
    public GralInterfaceDao getGralDao() {
        return gralDao;
    }
    
    public HomeInterfaceDao getHomeDao() {
        return HomeDao;
    }
    
    
    @RequestMapping(value="/startup.agnux")
    public ModelAndView startUp(HttpServletRequest request, HttpServletResponse response, 
            @ModelAttribute("user") UserSessionData user
            )throws ServletException, IOException {
        
        log.log(Level.INFO, "Ejecutando starUp de {0}", GralConfigPeriodosPagoController.class.getName());
        LinkedHashMap<String,String> infoConstruccionTabla = new LinkedHashMap<String,String>();
        infoConstruccionTabla.put("id", "Acciones:70");
        infoConstruccionTabla.put("anio", "A&ntilde;o:100");
        infoConstruccionTabla.put("titulo", "Titulo:350");
        
        ModelAndView x = new ModelAndView("gralconfigperiodospago/startup", "title", "Cat&aacute;logo Configuraci&oacute;n de Periodos de Pago");
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
    
     //Cargar cargar datos iniciales para el buscador
   @RequestMapping(method = RequestMethod.POST, value="/getDatos.json")
        public @ResponseBody HashMap<String,ArrayList<HashMap<String, Object>>> getDatosJson(
        @RequestParam(value="iu", required=true) String id_user,
        Model model
    ){
        HashMap<String,ArrayList<HashMap<String, Object>>> jsonretorno = new HashMap<String,ArrayList<HashMap<String, Object>>>();
        HashMap<String, Object> mes = new HashMap<String, Object>();
        ArrayList<HashMap<String, Object>> mesActual = new ArrayList<HashMap<String, Object>>();
        
        //decodificar id de usuario
        Integer id_usuario = Integer.parseInt(Base64Coder.decodeString(id_user));
        
        mes.put("mesActual", TimeHelper.getMesActual());
        mes.put("anioActual", TimeHelper.getFechaActualY());
        mesActual.add(0, mes);
        
        jsonretorno.put("Anios", this.getGralDao().getConfigPeriodosPago_Anios());
        jsonretorno.put("Dato", mesActual);
        return jsonretorno;
    }

    @RequestMapping(method = RequestMethod.POST, value="/getConfigPeriodosPago.json")        
    public @ResponseBody HashMap<String,ArrayList<HashMap<String, Object>>> getConfigPeriodosPagoJson(
            @RequestParam(value="id", required=true) Integer id,
            @RequestParam(value="iu", required=true) String id_user_cod,
            Model model
            ){
        
        log.log(Level.INFO, "Ejecutando getConfigPeriodosPago de {0}", GralConfigPeriodosPagoController.class.getName());
        HashMap<String,ArrayList<HashMap<String, Object>>> jsonretorno = new HashMap<String,ArrayList<HashMap<String, Object>>>();
        HashMap<String, String> userDat = new HashMap<String, String>();
       
        ArrayList<HashMap<String, Object>> datos = new ArrayList<HashMap<String, Object>>();
        ArrayList<HashMap<String, Object>> datosGrid = new ArrayList<HashMap<String, Object>>();
        ArrayList<HashMap<String, Object>> periodos = new ArrayList<HashMap<String, Object>>();




        //decodificar id de usuario
        Integer id_usuario = Integer.parseInt(Base64Coder.decodeString(id_user_cod));
        userDat = this.getHomeDao().getUserById(id_usuario);
        
        Integer id_empresa = Integer.parseInt(userDat.get("empresa_id"));
        Integer id_sucursal = Integer.parseInt(userDat.get("sucursal_id"));
       
        if( id != 0 ){
            datos = this.getGralDao().getConfigPeriodosPago_Datos(id);
            datosGrid = this.getGralDao().getConfigPeriodosPago_Grid(id);
        }
        
        periodos=this.getGralDao().getPeriodicidad_Tipos(id_empresa, id_sucursal);

       //datos ConfigPeriodosPago es lo que me trajo de la consulta y los pone en el json
       jsonretorno.put("ConfigPeriodosPago", datos);
       jsonretorno.put("datosGrid", datosGrid);
       jsonretorno.put("TiposPeriodos", periodos);
       return jsonretorno;
    }
 
     @RequestMapping(value="/getAllConfigPeriodosPago.json", method = RequestMethod.POST)
     public @ResponseBody HashMap<String,ArrayList<HashMap<String, Object>>> getAllConfigPeriodosPagosJson(
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
        
        //aplicativo catalogo de Configuracion de Periodicidad de Pago 
        Integer app_selected = 174;
        
        //decodificar id de usuario
        Integer id_usuario = Integer.parseInt(Base64Coder.decodeString(id_user_cod));
        
        //variables para el buscador
        
        String anio = ""+StringHelper.isNullString(String.valueOf(has_busqueda.get("anio")))+"";
        String periodos = "%"+StringHelper.isNullString(String.valueOf(has_busqueda.get("titulo")))+"%";
        
        String data_string = app_selected+"___"+id_usuario+"___"+anio+"___"+periodos;
        
        //obtiene total de registros en base de datos, con los parametros de busqueda
        int total_items = this.getGralDao().countAll(data_string);              
                
        
        //calcula el total de paginas
        int total_pags = resource.calculaTotalPag(total_items,items_por_pag);
        
        //variables que necesita el datagrid, para no tener que hacer uno por cada aplicativo
        DataPost dataforpos = new DataPost(orderby, desc, items_por_pag, pag_start, display_pag, input_json, cadena_busqueda,total_items,total_pags, id_user_cod);
        
        int offset = resource.__get_inicio_offset(items_por_pag, pag_start);
        
        //obtiene los registros para el grid, de acuerdo a los parametros de busqueda
        jsonretorno.put("Data", this.getGralDao().getConfigPeriodosPago_PaginaGrid(data_string, offset, items_por_pag, orderby, desc));
               
        //obtiene el hash para los datos que necesita el datagrid
        jsonretorno.put("DataForGrid", dataforpos.formaHashForPos(dataforpos));
        
        return jsonretorno;
    }

    //crear y editar una ConfigPeriodosPago
    @RequestMapping(method = RequestMethod.POST, value="/edit.json")
    public @ResponseBody HashMap<String, String> editJson(
            @RequestParam(value="identificador", required=true) Integer id,
            @RequestParam(value="select_anio", required=true) String select_anio,
            @RequestParam(value="select_periodo", required=true) String select_periodo,
            @RequestParam(value="titulo", required=true) String titulo,
            @RequestParam(value="id_reg", required=false) String[] id_reg,
            @RequestParam(value="id_periodo", required=false) String[] id_periodo,
            @RequestParam(value="folio", required=false) String[] folio,
            @RequestParam(value="tituloperiodo", required=true) String[] tituloperiodo,
            @RequestParam(value="fecha_inicial", required=true) String[] fecha_inicio,
            @RequestParam(value="fecha_final", required=true) String[] fecha_final,
            @RequestParam(value="noTr", required=false) String[] noTr,
            
            
            @ModelAttribute("user") UserSessionData user,
            Model model
            ) {
        
        HashMap<String, String> jsonretorno = new HashMap<String, String>();
        HashMap<String, String> userDat = new HashMap<String, String>();
        HashMap<String, String> succes = new HashMap<String, String>();
        Integer app_selected = 174;//catalogo de de Configuracion dePeriodicidad de Pago
        String command_selected = "new";
        //decodificar id de usuario
        Integer id_usuario = user.getUserId();
        userDat = this.getHomeDao().getUserById(id_usuario);
        Integer id_empresa = Integer.parseInt(userDat.get("empresa_id"));
        Integer id_sucursal = Integer.parseInt(userDat.get("sucursal_id"));
        
        //String extra_data_array = "'sin datos'";
        String actualizo = "0";
        
        
        //si los campos select vienen null les asigna un 0(cero)
            select_anio = StringHelper.verificarSelect(select_anio);
            select_periodo = StringHelper.verificarSelect(select_periodo);
            

        
        if( id==0 ){
            command_selected = "new";
        }else{
            command_selected = "edit";
        }
        
        
           String arreglo[];
        arreglo = new String[id_reg.length];
        
       for(int i=0; i<id_reg.length; i++) {
            arreglo[i]= "'"+id_reg[i] +"___" + id_periodo[i] +"___" +folio[i] +"___" + tituloperiodo[i] +"___" + fecha_inicio[i] +"___" + fecha_final[i] +"___" + noTr[i] +"'"; 
            System.out.println(arreglo[i]);
        }
        
        //Serializar el arreglo
       String extra_data_array = StringUtils.join(arreglo, ",");
        
        
        
        
        
        String data_string = app_selected+"___"+command_selected+"___"+id_usuario+"___"+id+"___"+
        select_anio+"___"+
        select_periodo+"___"+
        titulo.toUpperCase()+"___"+
        id_sucursal;
        
        succes = this.getGralDao().selectFunctionValidateAaplicativo(data_string, app_selected, extra_data_array);
        log.log(Level.INFO, "despues de validacion {0}", String.valueOf(succes.get("success")));
        if( String.valueOf(succes.get("success")).equals("true") ){
            actualizo = this.getGralDao().selectFunctionForThisApp(data_string, extra_data_array);
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
        
        Integer app_selected = 174;
        String command_selected = "delete";
        String extra_data_array = "'sin datos'";
        String data_string = app_selected+"___"+command_selected+"___"+id_usuario+"___"+id;
        
        //System.out.println("Ejecutando borrado logico ConfigPeriodosPago");
        //System.out.println("cadena a enviar para el cambio de borrado logico:  "+data_string);
        jsonretorno.put("success",String.valueOf( this.getGralDao().selectFunctionForThisApp(data_string, extra_data_array)));
        return jsonretorno;
    }
    
}
