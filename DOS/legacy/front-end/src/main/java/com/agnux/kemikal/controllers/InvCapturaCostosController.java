package com.agnux.kemikal.controllers;

import com.agnux.cfd.v2.Base64Coder;
import com.agnux.common.helpers.StringHelper;
import com.agnux.common.helpers.TimeHelper;
import com.agnux.common.obj.DataPost;
import com.agnux.common.obj.ResourceProject;
import com.agnux.common.obj.UserSessionData;
import com.agnux.kemikal.interfacedaos.GralInterfaceDao;
import com.agnux.kemikal.interfacedaos.HomeInterfaceDao;
import com.agnux.kemikal.interfacedaos.InvInterfaceDao;
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
@RequestMapping("/invcapturacostos/")
public class InvCapturaCostosController {
    private static final Logger log  = Logger.getLogger(InvCapturaCostosController.class.getName());
    ResourceProject resource = new ResourceProject();
    
    @Autowired
    @Qualifier("daoInv")
    private InvInterfaceDao invDao;
    
    public InvInterfaceDao getInvDao() {
        return invDao;
    }
    
    @Autowired
    @Qualifier("daoHome")
    private HomeInterfaceDao HomeDao;
    
    public HomeInterfaceDao getHomeDao() {
        return HomeDao;
    }
    
    @Autowired
    @Qualifier("daoGral")
    private GralInterfaceDao gralDao;
    
    public GralInterfaceDao getGralDao() {
        return gralDao;
    }
    
    @RequestMapping(value="/startup.agnux")
    public ModelAndView startUp(HttpServletRequest request, HttpServletResponse response, 
            @ModelAttribute("user") UserSessionData user)
            throws ServletException, IOException {
        
        log.log(Level.INFO, "Ejecutando starUp de {0}", InvCapturaCostosController.class.getName());
        LinkedHashMap<String,String> infoConstruccionTabla = new LinkedHashMap<String,String>();
        HashMap<String, String> userDat = new HashMap<String, String>();
        HashMap<String, String> comPar = new HashMap<String, String>();
        
        String userId = String.valueOf(user.getUserId());
        userDat = this.getHomeDao().getUserById(Integer.parseInt(userId));
        Integer id_empresa = Integer.parseInt(userDat.get("empresa_id"));
        Integer id_sucursal = Integer.parseInt(userDat.get("sucursal_id"));
        comPar = this.getInvDao().getCom_Par(id_empresa, id_sucursal);
        
        if(comPar.get("captura_costo_ref").equals("false")){
            infoConstruccionTabla.put("id", "Acciones:70");
            infoConstruccionTabla.put("codigo", "C&oacute;digo:120");
            infoConstruccionTabla.put("descripcion","Descripci&oacute;n:300");
            infoConstruccionTabla.put("moneda", "Moneda:120");
            infoConstruccionTabla.put("tc", "T.C.:80");
            infoConstruccionTabla.put("costo","Costo:100");
            infoConstruccionTabla.put("fecha", "Fecha Actualizaci&oacute;n:130");
        }else{
            infoConstruccionTabla.put("id", "Acciones:70");
            infoConstruccionTabla.put("codigo", "C&oacute;digo:120");
            infoConstruccionTabla.put("descripcion","Descripci&oacute;n:300");
            infoConstruccionTabla.put("presentacion","Presentaci&oacute;n:120");
            infoConstruccionTabla.put("moneda", "Moneda:60");
            infoConstruccionTabla.put("tc", "T.C.:60");
            infoConstruccionTabla.put("costo","Costo:70");
            infoConstruccionTabla.put("igi","I.G.I.:65");
            infoConstruccionTabla.put("gi","G.I.:65");
            infoConstruccionTabla.put("ca","C.A.:65");
            infoConstruccionTabla.put("cit","C.I.T.:80");
            infoConstruccionTabla.put("pmin","P.MIN.:80");
            infoConstruccionTabla.put("fecha", "Fecha Actualizaci&oacute;n:130");
        }
        
                    
        ModelAndView x = new ModelAndView("invcapturacostos/startup", "title", "Captura de Costo");
        
        x = x.addObject("layoutheader", resource.getLayoutheader());
        x = x.addObject("layoutmenu", resource.getLayoutmenu());
        x = x.addObject("layoutfooter", resource.getLayoutfooter());
        x = x.addObject("grid", resource.generaGrid(infoConstruccionTabla));
        x = x.addObject("url", resource.getUrl(request));
        x = x.addObject("username", user.getUserName());
        x = x.addObject("empresa", user.getRazonSocialEmpresa());
        x = x.addObject("sucursal", user.getSucursal());
        
        String codificado = Base64Coder.encodeString(userId);
        
        //id de usuario codificado
        x = x.addObject("iu", codificado);
        
        return x;
    }
    
    
    
    
    
    //Metodo para el grid y el Paginado
    @RequestMapping(value="/getAllCostos.json", method = RequestMethod.POST)
    public @ResponseBody HashMap<String,ArrayList<HashMap<String, Object>>> getAllCostosJson(
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
        HashMap<String, String> comPar = new HashMap<String, String>();
        
        //Aplicativo para Captura de Costos(INV)
        Integer app_selected = 145;
        
        //decodificar id de usuario
        Integer id_usuario = Integer.parseInt(Base64Coder.decodeString(id_user_cod));
        userDat = this.getHomeDao().getUserById(id_usuario);
        Integer id_empresa = Integer.parseInt(userDat.get("empresa_id"));
        Integer id_sucursal = Integer.parseInt(userDat.get("sucursal_id"));
        comPar = this.getInvDao().getCom_Par(id_empresa, id_sucursal);
        
        //variables para el buscador
        String codigo = "%"+StringHelper.isNullString(String.valueOf(has_busqueda.get("codigo")))+"%";
        String descripcion = "%"+StringHelper.isNullString(String.valueOf(has_busqueda.get("descripcion")))+"%";
        String tipo = StringHelper.isNullString(String.valueOf(has_busqueda.get("tipo")));
        
        String[] arregloFecha = TimeHelper.getFechaActualYMDH().split("-");
        String anoActual = arregloFecha[0];
        String mesActual = String.valueOf(Integer.parseInt(arregloFecha[1]));
        
        String data_string = app_selected+"___"+id_usuario+"___"+codigo+"___"+descripcion+"___"+tipo+"___"+anoActual+"___"+mesActual;
        
        //obtiene total de registros en base de datos, con los parametros de busqueda
        int total_items = this.getInvDao().countAll(data_string);
        
        //calcula el total de paginas
        int total_pags = resource.calculaTotalPag(total_items,items_por_pag);
        
        //variables que necesita el datagrid, para no tener que hacer uno por cada aplicativo
        DataPost dataforpos = new DataPost(orderby, desc, items_por_pag, pag_start, display_pag, input_json, cadena_busqueda,total_items,total_pags,id_user_cod);
        
        int offset = resource.__get_inicio_offset(items_por_pag, pag_start);
        
        //Obtiene los registros para el grid, de acuerdo a los parametros de busqueda
        if(comPar.get("captura_costo_ref").equals("false")){
            //Obtiene solo costos ultimos
            jsonretorno.put("Data", this.getInvDao().getInvCapturaCosto_PaginaGrid(data_string, offset, items_por_pag, orderby, desc));
        }else{
            //Obtiene con costos de Referencia
            jsonretorno.put("Data", this.getInvDao().getInvCapturaCosto_PaginaGrid2(data_string, offset, items_por_pag, orderby, desc));
        }
        
        //obtiene el hash para los datos que necesita el datagrid
        jsonretorno.put("DataForGrid", dataforpos.formaHashForPos(dataforpos));
        
        return jsonretorno;
    }
    
    
    //obtiene lineas de producto y datos para el buscador
    @RequestMapping(method = RequestMethod.POST, value="/getProductoTipos.json")
    public @ResponseBody HashMap<String,ArrayList<HashMap<String, String>>> getDataLineasJson(
            @RequestParam(value="iu", required=true) String id_user_cod,
            Model model
        ) {
        
        HashMap<String,ArrayList<HashMap<String, String>>> jsonretorno = new HashMap<String,ArrayList<HashMap<String, String>>>();
        ArrayList<HashMap<String, String>> arrayTiposProducto = new ArrayList<HashMap<String, String>>();
        
        //decodificar id de usuario
        //Integer id_usuario = Integer.parseInt(Base64Coder.decodeString(id_user_cod));
        arrayTiposProducto=this.getInvDao().getProducto_Tipos();
        
        jsonretorno.put("prodTipos", arrayTiposProducto);
        return jsonretorno;
    }
    
    
    
    
    
    
    @RequestMapping(method = RequestMethod.POST, value="/getCosto.json")
    public @ResponseBody HashMap<String,ArrayList<HashMap<String, String>>> getCostoJson(
            @RequestParam(value="identificador", required=true) Integer identificador,
            @RequestParam(value="iu", required=true) String id_user_cod,
            Model model
        ){
        
        log.log(Level.INFO, "Ejecutando getCostoJson de {0}", InvCapturaCostosController.class.getName());
        HashMap<String,ArrayList<HashMap<String, String>>> jsonretorno = new HashMap<String,ArrayList<HashMap<String, String>>>();
        HashMap<String, String> userDat = new HashMap<String, String>();
        ArrayList<HashMap<String, String>> costoProd = new ArrayList<HashMap<String, String>>(); 
        ArrayList<HashMap<String, String>> monedas = new ArrayList<HashMap<String, String>>();
        ArrayList<HashMap<String, String>> arrayExtra = new ArrayList<HashMap<String, String>>();
        HashMap<String, String> comPar = new HashMap<String, String>();
        
        //Decodificar id de usuario
        Integer id_usuario = Integer.parseInt(Base64Coder.decodeString(id_user_cod));
        userDat = this.getHomeDao().getUserById(id_usuario);
        Integer id_empresa = Integer.parseInt(userDat.get("empresa_id"));
        Integer id_sucursal = Integer.parseInt(userDat.get("sucursal_id"));
        comPar = this.getInvDao().getCom_Par(id_empresa, id_sucursal);
        arrayExtra.add(comPar);
        
        if( identificador != 0 ){
            //Aquí solo debe entrar cuando es EDITAR.
            //Convertir en arreglo la fecha actual
            String f [] = TimeHelper.getFechaActualYMD().split("-");
            
            //Tomar el año actual
            Integer ano_actual=Integer.parseInt(f[0]);
            
            //Tomar el mes actual
            Integer mes_actual=Integer.parseInt(f[1]);
            
            costoProd = this.getInvDao().getInvCapturaCosto_CostoProducto(identificador, ano_actual, mes_actual, comPar.get("captura_costo_ref"));
        }
        
        monedas = this.getInvDao().getMonedas();
        
        jsonretorno.put("Monedas", monedas);
        jsonretorno.put("Costo", costoProd);
        jsonretorno.put("Extra", arrayExtra);
        return jsonretorno;
    }
    
    
    
    
    
    
    //Obtiene los productos para el buscador
    @RequestMapping(method = RequestMethod.POST, value = "/getBuscadorProductos.json")
    public @ResponseBody
    HashMap<String, ArrayList<HashMap<String, String>>> getBuscadorProductosJson(
            @RequestParam(value = "sku", required = true) String sku,
            @RequestParam(value = "tipo", required = true) String tipo,
            @RequestParam(value = "descripcion", required = true) String descripcion,
            @RequestParam(value = "iu", required = true) String id_user,
            Model model
        ) {
        
        log.log(Level.INFO, "Ejecutando getBuscadorProductosJson de {0}", InvCapturaCostosController.class.getName());
        HashMap<String, ArrayList<HashMap<String, String>>> jsonretorno = new HashMap<String, ArrayList<HashMap<String, String>>>();
        ArrayList<HashMap<String, String>> productos = new ArrayList<HashMap<String, String>>();
        HashMap<String, String> userDat = new HashMap<String, String>();
        
        //decodificar id de usuario
        Integer id_usuario = Integer.parseInt(Base64Coder.decodeString(id_user));
        userDat = this.getHomeDao().getUserById(id_usuario);
        
        Integer id_empresa = Integer.parseInt(userDat.get("empresa_id"));
        
        productos = this.getInvDao().getBuscadorProductos(sku,tipo ,descripcion, id_empresa);
        
        jsonretorno.put("Productos", productos);
        
        return jsonretorno;
    }
    
    
    
    
    //Obtiene datos del producto a agregar
    @RequestMapping(method = RequestMethod.POST, value="/getDatosProducto.json")
    public @ResponseBody HashMap<String,ArrayList<HashMap<String, String>>> getDatosProductoJson(
            @RequestParam(value="sku", required=true) String sku,
            @RequestParam(value="iu", required=true) String id_user,
            Model model
    ) {
        
        log.log(Level.INFO, "Ejecutando getDatosProductoJson de {0}", InvCapturaCostosController.class.getName());
        HashMap<String,ArrayList<HashMap<String, String>>> jsonretorno = new HashMap<String,ArrayList<HashMap<String, String>>>();
        ArrayList<HashMap<String, String>> datos = new ArrayList<HashMap<String, String>>();
        ArrayList<HashMap<String, String>> monedas = new ArrayList<HashMap<String, String>>();
        HashMap<String, String> userDat = new HashMap<String, String>();
        HashMap<String, String> comPar = new HashMap<String, String>();
        
        
        
        //Decodificar id de usuario
        Integer id_usuario = Integer.parseInt(Base64Coder.decodeString(id_user));
        userDat = this.getHomeDao().getUserById(id_usuario);
        Integer id_empresa = Integer.parseInt(userDat.get("empresa_id"));
        Integer id_sucursal = Integer.parseInt(userDat.get("sucursal_id"));
        comPar = this.getInvDao().getCom_Par(id_empresa, id_sucursal);
        
        //Convertir en arreglo la fecha actual
        String f [] = TimeHelper.getFechaActualYMD().split("-");
        
        //Tomar el año actual
        Integer ano_actual=Integer.parseInt(f[0]);
        
        //Tomar el mes actual
        Integer mes_actual=Integer.parseInt(f[1]);
        
        datos = this.getInvDao().getInvCapturaCosto_DatosProducto(sku.toUpperCase(), id_empresa, mes_actual, ano_actual, comPar.get("captura_costo_ref"));
        //monedas = this.getInvDao().getMonedas();
        
        jsonretorno.put("Producto", datos);
        //jsonretorno.put("Monedas", monedas);
        
        return jsonretorno;
    }
    
    
    
    
    //Edicion y nuevo
    @RequestMapping(method = RequestMethod.POST, value="/edit.json")
    public @ResponseBody HashMap<String, String> editJson(
            @RequestParam(value="identificador", required=true) Integer identificador,
            @RequestParam(value="idreg", required=false) String[] idreg,
            @RequestParam(value="idprod", required=false) String[] idprod,
            @RequestParam(value="costo_ultimo", required=false) String[] costo_ultimo,
            @RequestParam(value="selectMon", required=false) String[] selectMon,
            @RequestParam(value="tc", required=false) String[] tc,
            @RequestParam(value="notr", required=false) String[] notr,
            @RequestParam(value="id_pres", required=false) String[] id_pres,
            @RequestParam(value="igi", required=false) String[] igi,
            @RequestParam(value="gi", required=false) String[] gi,
            @RequestParam(value="ca", required=false) String[] ca,
            @RequestParam(value="margen_pmin", required=false) String[] margen_pmin,
            @ModelAttribute("user") UserSessionData user
        ) {
        
        //System.out.println("Actualizar costos");
        HashMap<String, String> jsonretorno = new HashMap<String, String>();
        HashMap<String, String> succes = new HashMap<String, String>();
        
        //Aplicativo para Captura de Costos(INV)
        Integer app_selected = 145;
        
        //Aqui le dejamos por default "edit", porque siempre seran actualizaciones de registros
        String command_selected = "edit";
        Integer id_usuario= user.getUserId();//variable para el id  del usuario
        
        String arreglo[];
        arreglo = new String[idreg.length];
        
        for(int i=0; i<idreg.length; i++) { 
            selectMon[i] = StringHelper.verificarSelect(selectMon[i]);
            arreglo[i]= "'"+idreg[i] +"___" + idprod[i] +"___" + costo_ultimo[i] +"___" + selectMon[i] +"___" + tc[i] +"___" + notr[i] +"___"+ id_pres[i] +"___" + igi[i] +"___" + gi[i] +"___" + ca[i] +"___" + margen_pmin[i] +"'";
            //System.out.println(arreglo[i]);
        }
        
        //Serializar el arreglo
        String extra_data_array = StringUtils.join(arreglo, ",");
        
        String data_string = app_selected+"___"+command_selected+"___"+id_usuario+"___"+identificador;
        
        //System.out.println("data_string: "+data_string);
        succes = this.getInvDao().selectFunctionValidateAaplicativo(data_string,app_selected,extra_data_array);
        
        log.log(Level.INFO, "despues de validacion {0}", String.valueOf(succes.get("success")));
        String actualizo = "0";
        
        if( String.valueOf(succes.get("success")).equals("true") ){
            actualizo = this.getInvDao().selectFunctionForApp_MovimientosInventario(data_string, extra_data_array);
            jsonretorno.put("actualizo",String.valueOf(actualizo));
        }
        
        jsonretorno.put("success",String.valueOf(succes.get("success")));
        
        log.log(Level.INFO, "Salida json {0}", String.valueOf(jsonretorno.get("success")));
        return jsonretorno;
    }
    
    
    
}
