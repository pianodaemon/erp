/*
 * Este aplicativo es para cancelacion de anticipos de clientes
 */
package com.agnux.kemikal.controllers;
import com.agnux.cfd.v2.Base64Coder;
import com.agnux.common.helpers.StringHelper;
import com.agnux.common.obj.DataPost;
import com.agnux.common.obj.ResourceProject;
import com.agnux.common.obj.UserSessionData;
import com.agnux.kemikal.interfacedaos.CxcInterfaceDao;
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
@RequestMapping("/clientsantcancel/")
public class ClientsAntCancelController {
    private static final Logger log  = Logger.getLogger(ClientsAntCancelController.class.getName());
    ResourceProject resource = new ResourceProject();

    @Autowired
    @Qualifier("daoHome")
    private HomeInterfaceDao HomeDao;

    @Autowired
    @Qualifier("daoGral")
    private GralInterfaceDao gralDao;
    
    @Autowired
    @Qualifier("daoCxc")
    private CxcInterfaceDao cxcDao;
    
    public HomeInterfaceDao getHomeDao() {
        return HomeDao;
    }
    
    public GralInterfaceDao getGralDao() {
        return gralDao;
    }
    
    public void setGralDao(GralInterfaceDao gralDao) {
        this.gralDao = gralDao;
    }
    
    public CxcInterfaceDao getCxcDao() {
        return cxcDao;
    }
    
    @RequestMapping(value="/startup.agnux")
    public ModelAndView startUp(HttpServletRequest request, HttpServletResponse response, @ModelAttribute("user") UserSessionData user)
            throws ServletException, IOException {
        
        log.log(Level.INFO, "Ejecutando starUp de {0}", ClientsAntCancelController.class.getName());
        LinkedHashMap<String,String> infoConstruccionTabla = new LinkedHashMap<String,String>();
        
        infoConstruccionTabla.put("id", "Acciones:70");
        infoConstruccionTabla.put("numero_transaccion", "No.&nbsp;Transacci&oacute;n:110");
        infoConstruccionTabla.put("monto", "Monto:100");
        infoConstruccionTabla.put("moneda", "Moneda:80");
        infoConstruccionTabla.put("cliente", "Cliente:280");
        infoConstruccionTabla.put("fecha", "Fecha:90");
        infoConstruccionTabla.put("estado", "Estado:90");
        //infoConstruccionTabla.put("observacion", "Observaci&oacute;n:280");
        
        ModelAndView x = new ModelAndView("clientsantcancel/startup", "title", "Anticipos de Clientes");
        
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
    

    
    
    //obtiene listado de pagos para el grid
    @RequestMapping(value="/getAllAnticipos.json", method = RequestMethod.POST)
    public @ResponseBody HashMap<String,ArrayList<HashMap<String, Object>>> getAllAnticiposJson(
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
        
        //Aplicativo de Cancelacion de Anticipos(CXC)
        Integer app_selected = 146;
        
        //Decodificar id de usuario
        Integer id_usuario = Integer.parseInt(Base64Coder.decodeString(id_user_cod));
        //System.out.println("id_usuario: "+id_usuario);
        
        //variables para el buscador
        String num_transaccion = StringHelper.isNullString(String.valueOf(has_busqueda.get("num_transaccion")));
        String cliente = "%"+StringHelper.isNullString(String.valueOf(has_busqueda.get("cliente")))+"%";
        String fecha_inicial = ""+StringHelper.isNullString(String.valueOf(has_busqueda.get("fecha_inicial")))+"";
        String fecha_final = ""+StringHelper.isNullString(String.valueOf(has_busqueda.get("fecha_final")))+"";
        
        String data_string = app_selected+"___"+id_usuario+"___"+num_transaccion+"___"+cliente+"___"+fecha_inicial+"___"+fecha_final;
        
        //obtiene total de registros en base de datos, con los parametros de busqueda
        int total_items = this.getCxcDao().countAll(data_string);
        
        //calcula el total de paginas
        int total_pags = resource.calculaTotalPag(total_items,items_por_pag);
        
        //variables que necesita el datagrid, para no tener que hacer uno por cada aplicativo
        DataPost dataforpos = new DataPost(orderby, desc, items_por_pag, pag_start, display_pag, input_json, cadena_busqueda,total_items,total_pags,id_user_cod);
        
        int offset = resource.__get_inicio_offset(items_por_pag, pag_start);
        
        //obtiene los registros para el grid, de acuerdo a los parametros de busqueda
        jsonretorno.put("Data", this.getCxcDao().getClientsAntCancel_PaginaGrid(data_string, offset, items_por_pag, orderby, desc));
        //obtiene el hash para los datos que necesita el datagrid
        jsonretorno.put("DataForGrid", dataforpos.formaHashForPos(dataforpos));
        
        return jsonretorno;
    }
    
    
    
    //obtiene los tipos de cancelacion
    @RequestMapping(method = RequestMethod.POST, value="/getAnticipo.json")
    public @ResponseBody HashMap<String,ArrayList<HashMap<String, Object>>> getAnticipoJson(
            @RequestParam(value="identificador", required=true) Integer identificador,
            Model model
        ) {
        
        log.log(Level.INFO, "Ejecutando getAnticipoJson de {0}", ClientsAntCancelController.class.getName());
        HashMap<String,ArrayList<HashMap<String, Object>>> jsonretorno = new HashMap<String,ArrayList<HashMap<String, Object>>>();
        ArrayList<HashMap<String, Object>> datos = new ArrayList<HashMap<String, Object>>();
        ArrayList<HashMap<String, Object>> monedas = new ArrayList<HashMap<String, Object>>();
        
        datos = this.getCxcDao().getClientsAntCancel_DatosAnticipo(identificador);
        monedas = this.getCxcDao().getMonedas();
        
        jsonretorno.put("Datos", datos);
        jsonretorno.put("Monedas", monedas);
        
        return jsonretorno;
    }
    
    
    
    
    
    //Buscador de clientes
    @RequestMapping(method = RequestMethod.POST, value="/get_buscador_clientes.json")
    public @ResponseBody HashMap<String,ArrayList<HashMap<String, Object>>> get_buscador_clientesJson(
            @RequestParam(value="cadena", required=true) String cadena,
            @RequestParam(value="filtro", required=true) Integer filtro,
            @RequestParam(value="iu", required=true) String id_user,
            Model model
        ) {
        
        HashMap<String,ArrayList<HashMap<String, Object>>> jsonretorno = new HashMap<String,ArrayList<HashMap<String, Object>>>();
        HashMap<String, String> userDat = new HashMap<String, String>();
        
        //decodificar id de usuario
        Integer id_usuario = Integer.parseInt(Base64Coder.decodeString(id_user));
        //System.out.println("id_usuario: "+id_usuario);
        
        userDat = this.getHomeDao().getUserById(id_usuario);
        
        Integer id_empresa = Integer.parseInt(userDat.get("empresa_id"));
        Integer id_sucursal = Integer.parseInt(userDat.get("sucursal_id"));
        
        jsonretorno.put("Clientes", this.getCxcDao().getBuscadorClientes(cadena,filtro,id_empresa, id_sucursal));
        
        return jsonretorno;
    }
    
    
    //Obtener datos del cliente a partir del Numero de Control
    @RequestMapping(method = RequestMethod.POST, value="/getDataByNoClient.json")
    public @ResponseBody HashMap<String,ArrayList<HashMap<String, Object>>> getDataByNoClientJson(
            @RequestParam(value="no_control", required=true) String no_control,
            @RequestParam(value="iu", required=true) String id_user,
            Model model
        ) {
        
        HashMap<String,ArrayList<HashMap<String, Object>>> jsonretorno = new HashMap<String,ArrayList<HashMap<String, Object>>>();
        HashMap<String, String> userDat = new HashMap<String, String>();
       
        //decodificar id de usuario
        Integer id_usuario = Integer.parseInt(Base64Coder.decodeString(id_user));
        
        userDat = this.getHomeDao().getUserById(id_usuario);
        
        Integer id_empresa = Integer.parseInt(userDat.get("empresa_id"));
        Integer id_sucursal = Integer.parseInt(userDat.get("sucursal_id"));
        
        jsonretorno.put("Cliente", this.getCxcDao().getDatosClienteByNoCliente(no_control, id_empresa, id_sucursal));
        
        return jsonretorno;
    }
    
    
    
    
    //crear y editar
    @RequestMapping(method = RequestMethod.POST, value="/edit.json")
    public @ResponseBody HashMap<String, String> editJson(
            @RequestParam(value="identificador", required=true) String id,
            @RequestParam(value="accion", required=true) String accion,
            @RequestParam(value="fecha", required=true) String fecha,
            @RequestParam(value="monto", required=true) String monto,
            @RequestParam(value="select_moneda", required=true) String select_moneda,
            @RequestParam(value="id_cliente", required=true) String id_cliente,
            @RequestParam(value="observaciones", required=true) String observaciones,
            Model model,@ModelAttribute("user") UserSessionData user
        ) {
        
        HashMap<String, String> jsonretorno = new HashMap<String, String>();
        HashMap<String, String> succes = new HashMap<String, String>();
        Integer app_selected = 146;
        String command_selected = "new";
        Integer id_usuario= user.getUserId();//variable para el id  del usuario
        String extra_data_array = "'sin datos'";
        String actualizo = "0";
        String valor="";
        String mensaje="";
        
        command_selected = accion;
        
        String data_string = app_selected+"___"+command_selected+"___"+id_usuario+"___"+id+"___"+id_cliente+"___"+monto+"___"+fecha+"___"+select_moneda+"___"+observaciones;
        
        succes = this.getCxcDao().selectFunctionValidateAaplicativo(data_string,app_selected,extra_data_array);
        
        log.log(Level.INFO, "despues de validacion {0}", String.valueOf(succes.get("success")));
        if( String.valueOf(succes.get("success")).equals("true") ){
            actualizo = this.getCxcDao().selectFunctionForCxcAdmProcesos(data_string, extra_data_array);
            valor="true";
            mensaje=String.valueOf(actualizo);
        }else{
            valor="false";
            mensaje=String.valueOf(succes.get("success"));
        }
        
        jsonretorno.put("success",String.valueOf(succes.get("success")));
        jsonretorno.put("valor",valor);
        jsonretorno.put("msj",mensaje);
        
        log.log(Level.INFO, "Salida json {0}", String.valueOf(jsonretorno.get("success")));
        return jsonretorno;
    }
    
}
