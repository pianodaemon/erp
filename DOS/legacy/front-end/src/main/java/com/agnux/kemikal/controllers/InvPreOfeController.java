package com.agnux.kemikal.controllers;

import com.agnux.kemikal.interfacedaos.InvInterfaceDao;

import com.agnux.cfd.v2.Base64Coder;
import com.agnux.common.helpers.StringHelper;
import com.agnux.common.obj.DataPost;
import com.agnux.common.obj.ResourceProject;
import com.agnux.common.obj.UserSessionData;
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
@RequestMapping("/invpreofe/")
public class InvPreOfeController {
    ResourceProject resource = new ResourceProject();
    private static final Logger log  = Logger.getLogger(InvSeccionesController.class.getName());
    
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
    
    @RequestMapping(value="/startup.agnux")
    public ModelAndView startUp(HttpServletRequest request, HttpServletResponse response, 
            @ModelAttribute("user") UserSessionData user
            )throws ServletException, IOException {
        
        log.log(Level.INFO, "Ejecutando starUp de {0}", InvSeccionesController.class.getName());
        LinkedHashMap<String,String> infoConstruccionTabla = new LinkedHashMap<String,String>();
        
        infoConstruccionTabla.put("id", "Acciones:70");
        infoConstruccionTabla.put("sku", "Producto:160");
        infoConstruccionTabla.put("descripcion", "Descripcion:200");
        infoConstruccionTabla.put("precio_oferta", "Precio Oferta:100");
        infoConstruccionTabla.put("fecha_inicial", "Fecha Inicial:100");
        infoConstruccionTabla.put("fecha_final", "Fecha Final:100");
        
        ModelAndView x = new ModelAndView("invpreofe/startup", "title", "Promociones de Articulos");
        
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
    
    
    
    
    @RequestMapping(value="/getAllInvPreOfe.json", method = RequestMethod.POST)
    public @ResponseBody HashMap<String,ArrayList<HashMap<String, Object>>> getAllInvPreOfeJson(
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
        Integer app_selected = 53;
        
        //decodificar id de usuario
        Integer id_usuario = Integer.parseInt(Base64Coder.decodeString(id_user_cod));
        
        //variables para el buscador
        String producto = "%"+StringHelper.isNullString(String.valueOf(has_busqueda.get("producto")))+"%";
        //String descripcion = "%"+StringHelper.isNullString(String.valueOf(has_busqueda.get("descripcion")))+"%";
        
        String data_string = app_selected+"___"+id_usuario+"___"+producto;
        
        //obtiene total de registros en base de datos, con los parametros de busqueda
        int total_items = this.getInvDao().countAll(data_string);
        
        //calcula el total de paginas
        int total_pags = resource.calculaTotalPag(total_items,items_por_pag);
        
        //variables que necesita el datagrid, para no tener que hacer uno por cada aplicativo
        DataPost dataforpos = new DataPost(orderby, desc, items_por_pag, pag_start, display_pag, input_json, cadena_busqueda,total_items,total_pags, id_user_cod);
        
        int offset = resource.__get_inicio_offset(items_por_pag, pag_start);
        
        //obtiene los registros para el grid, de acuerdo a los parametros de busqueda
        jsonretorno.put("Data", this.getInvDao().getInvPreOfeGrid(data_string, offset, items_por_pag, orderby, desc));
        //obtiene el hash para los datos que necesita el datagrid
        jsonretorno.put("DataForGrid", dataforpos.formaHashForPos(dataforpos));
        
        return jsonretorno;
    }
    
    
    //este es solo para probar
    
    @RequestMapping(method = RequestMethod.POST, value="/getInvPreOfe.json")
    public @ResponseBody HashMap<String,ArrayList<HashMap<String, String>>> getInvPreOfeJson(
            @RequestParam(value="id", required=true) Integer id,
            Model model
            ) {
        
        log.log(Level.INFO, "Ejecutando getInvPreOfe de {0}", InvSeccionesController.class.getName());
        HashMap<String,ArrayList<HashMap<String, String>>> jsonretorno = new HashMap<String,ArrayList<HashMap<String, String>>>();
        ArrayList<HashMap<String, String>> datosInvPreOfe = new ArrayList<HashMap<String, String>>();
        //ArrayList<HashMap<String, String>> TpolGrupos = new ArrayList<HashMap<String, String>>();
        
        if( id != 0  ){
            datosInvPreOfe = this.getInvDao().getInvPreOfe_Datos(id);
        }
        
        //TpolGrupos = this.getCtbDao().getTipoPoliza_Grupos();
        
        jsonretorno.put("InvPreOfe", datosInvPreOfe);
        //jsonretorno.put("Grupos", TpolGrupos);
        
        return jsonretorno;
    }
    
    //obtienetipos de productos
    @RequestMapping(method = RequestMethod.POST, value="/getProductoTipos.json")
    public @ResponseBody HashMap<String,ArrayList<HashMap<String, String>>> getProductoTiposJson(
            @RequestParam(value="iu", required=true) String id_user_cod,
            Model model
            ) {
       
        HashMap<String,ArrayList<HashMap<String, String>>> jsonretorno = new HashMap<String,ArrayList<HashMap<String, String>>>();
        ArrayList<HashMap<String, String>> arrayTiposProducto = new ArrayList<HashMap<String, String>>();
        Integer id_usuario = Integer.parseInt(Base64Coder.decodeString(id_user_cod));
       
        arrayTiposProducto=this.getInvDao().getProducto_Tipos();
        jsonretorno.put("prodTipos", arrayTiposProducto);
       
        return jsonretorno;
    }
    
    //Buscador de de productos
    @RequestMapping(method = RequestMethod.POST, value="/get_buscador_productos.json")
    public @ResponseBody HashMap<String,ArrayList<HashMap<String, String>>> getBuscadorProductosJson(
            @RequestParam(value="sku", required=true) String sku,
            @RequestParam(value="tipo", required=true) String tipo,
            @RequestParam(value="descripcion", required=true) String descripcion,
            @RequestParam(value="iu", required=true) String id_user,
            Model model
            ) {
       
        System.out.println("Busqueda de producto");
       
        HashMap<String,ArrayList<HashMap<String, String>>> jsonretorno = new HashMap<String,ArrayList<HashMap<String, String>>>();
        HashMap<String, String> userDat = new HashMap<String, String>();
        //decodificar id de usuario
        Integer id_usuario = Integer.parseInt(Base64Coder.decodeString(id_user));
        userDat = this.getHomeDao().getUserById(id_usuario);
       
        Integer id_empresa = Integer.parseInt(userDat.get("empresa_id"));
       
        jsonretorno.put("Productos", this.getInvDao().getBuscadorProductos(sku, tipo, descripcion,id_empresa));
       
        return jsonretorno;
    }
    
    
    
    //crear y editar
    @RequestMapping(method = RequestMethod.POST, value="/edit.json")
    public @ResponseBody HashMap<String, String> editJson(
            @RequestParam(value="identificador", required=true) String id,
            @RequestParam(value="producto_id", required=true) String producto_id,
            @RequestParam(value="consecutivo", required=false) String escala,
            @RequestParam(value="fecha_inicial", required=false) String fecha_inicial,
            @RequestParam(value="fecha_final", required=false) String fecha_final,
            @RequestParam(value="porprecio", required=false) String porprecio,
            @RequestParam(value="pordescuento", required=false) String pordescuento,
            @RequestParam(value="criterio", required=false) String criterio,
            @RequestParam(value="popreciocheck", required=false) String popreciocheck,
            @RequestParam(value="pordescuentocheck", required=false) String pordescuentocheck,
            
            @RequestParam(value="lista_1", required=false) String lista_1,
            @RequestParam(value="lista_2", required=false) String lista_2,
            @RequestParam(value="lista_3", required=false) String lista_3,
            @RequestParam(value="lista_4", required=false) String lista_4,
            @RequestParam(value="lista_5", required=false) String lista_5,
            @RequestParam(value="lista_6", required=false) String lista_6,
            @RequestParam(value="lista_7", required=false) String lista_7,
            @RequestParam(value="lista_8", required=false) String lista_8,
            @RequestParam(value="lista_9", required=false) String lista_9,
            @RequestParam(value="lista_10", required=false) String lista_10,
            
            Model model,@ModelAttribute("user") UserSessionData user
            ) {
        
        HashMap<String, String> jsonretorno = new HashMap<String, String>();
        HashMap<String, String> succes = new HashMap<String, String>();
        Integer app_selected = 53;
        String command_selected = "new";
        Integer id_usuario= user.getUserId();//variable para el id  del usuario
        String extra_data_array = "'sin datos'";
        String actualizo = "0";
        
        lista_1 = StringHelper.verificarCheckBox(lista_1);
        lista_2 = StringHelper.verificarCheckBox(lista_2);
        lista_3 = StringHelper.verificarCheckBox(lista_3);
        lista_4 = StringHelper.verificarCheckBox(lista_4);
        lista_5 = StringHelper.verificarCheckBox(lista_5);
        lista_6 = StringHelper.verificarCheckBox(lista_6);
        lista_7 = StringHelper.verificarCheckBox(lista_7);
        lista_8 = StringHelper.verificarCheckBox(lista_8);
        lista_9 = StringHelper.verificarCheckBox(lista_9);
        lista_10 = StringHelper.verificarCheckBox(lista_10);
        popreciocheck = StringHelper.verificarCheckBox(popreciocheck);
        pordescuentocheck = StringHelper.verificarCheckBox(pordescuentocheck);
        
        if( id.equals("0") ){
            command_selected = "new";
        }else{
            command_selected = "edit";
        }
        
        //                      1                   2                       3           4           5                   6                   7
        String data_string = app_selected+"___"+command_selected+"___"+id_usuario+"___"+id+"___"+producto_id+"___"+fecha_inicial+"___"+fecha_final
                //       8                  9                10             11          12              13          14              15
                +"___"+porprecio+"___"+pordescuento+"___"+criterio+"___"+lista_1+"___"+lista_2+"___"+lista_3+"___"+lista_4+"___"+lista_5
                //      16              17            16            19          20              21                  22  
                +"___"+lista_6+"___"+lista_7+"___"+lista_8+"___"+lista_9+"___"+lista_10+"___"+popreciocheck+"___"+pordescuentocheck;
        
        succes = this.getInvDao().selectFunctionValidateAaplicativo(data_string,app_selected,extra_data_array);
        
        log.log(Level.INFO, "despues de validacion {0}", String.valueOf(succes.get("success")));
        if( String.valueOf(succes.get("success")).equals("true") ){
            actualizo = this.getInvDao().selectFunctionForThisApp(data_string, extra_data_array);
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
        
        Integer app_selected = 53;
        String command_selected = "delete";
        String extra_data_array = "'sin datos'";
        String data_string = app_selected+"___"+command_selected+"___"+id_usuario+"___"+id;
        
        System.out.println("Ejecutando borrado logico de tipo poliza");
        jsonretorno.put("success",String.valueOf( this.getInvDao().selectFunctionForThisApp(data_string,extra_data_array)) );
        
        return jsonretorno;
    }
}
