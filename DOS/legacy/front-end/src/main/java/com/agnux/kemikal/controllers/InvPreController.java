package com.agnux.kemikal.controllers;

import com.agnux.cfd.v2.Base64Coder;
import com.agnux.common.helpers.StringHelper;
import com.agnux.common.obj.DataPost;
import com.agnux.common.obj.ResourceProject;
import com.agnux.common.obj.UserSessionData;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;


@Controller
@SessionAttributes({"user"})
@RequestMapping("/invpre/")
public class InvPreController {
    ResourceProject resource = new ResourceProject();
    private static final Logger log  = Logger.getLogger(InvPreController.class.getName());
    
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
        
        log.log(Level.INFO, "Ejecutando starUp de {0}", InvPreController.class.getName());
        LinkedHashMap<String,String> infoConstruccionTabla = new LinkedHashMap<String,String>();
        
        infoConstruccionTabla.put("id", "Acciones:90");
        infoConstruccionTabla.put("sku", "C&oacute;digo:100");
        infoConstruccionTabla.put("titulo_es", "Descripci&oacute;n:200");
        infoConstruccionTabla.put("pres", "Presentaci&oacute;n:120");
        infoConstruccionTabla.put("precio_1", "Lista 1:60");
        infoConstruccionTabla.put("precio_2", "Lista 2:60");
        infoConstruccionTabla.put("precio_3", "Lista 3:60");
        infoConstruccionTabla.put("precio_4", "Lista 4:60");
        infoConstruccionTabla.put("precio_5", "Lista 5:60");
        infoConstruccionTabla.put("precio_6", "Lista 6:60");
        infoConstruccionTabla.put("precio_7", "Lista 7:60");
        infoConstruccionTabla.put("precio_8", "Lista 8:60");
        infoConstruccionTabla.put("precio_9", "Lista 9:60");
        infoConstruccionTabla.put("precio_10", "Lista 10:65");
        
        
        ModelAndView x = new ModelAndView("invpre/startup", "title", "Precios");
        
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
    
    
    
    //obtiene los tipos de productos para el buscador de productos
    @RequestMapping(method = RequestMethod.POST, value="/getProductoTipos.json")
    public @ResponseBody HashMap<String,ArrayList<HashMap<String, String>>> getProductoTiposJson(
            @RequestParam(value="iu", required=true) String id_user_cod,
            Model model
        ) {
        
        HashMap<String,ArrayList<HashMap<String, String>>> jsonretorno = new HashMap<String,ArrayList<HashMap<String, String>>>();
        ArrayList<HashMap<String, String>> arrayTiposProducto = new ArrayList<HashMap<String, String>>();
        
        arrayTiposProducto=this.getInvDao().getProducto_Tipos();
        
        jsonretorno.put("prodTipos", arrayTiposProducto);
        
        return jsonretorno;
    }
    
    
    
    
    //obtiene datos Buscador principal
    @RequestMapping(method = RequestMethod.POST, value="/getDatosBuscadorPrincipal.json")
    public @ResponseBody HashMap<String,ArrayList<HashMap<String, String>>> getDatosBuscadorPrincipalJson(
            @RequestParam(value="iu", required=true) String id_user_cod,
            Model model
        ) {
        HashMap<String,ArrayList<HashMap<String, String>>> jsonretorno = new HashMap<String,ArrayList<HashMap<String, String>>>();
        HashMap<String, String> userDat = new HashMap<String, String>();
        ArrayList<HashMap<String, String>> tiposProducto = new ArrayList<HashMap<String, String>>();
        ArrayList<HashMap<String, String>> presentaciones = new ArrayList<HashMap<String, String>>();
        ArrayList<HashMap<String, String>> arrayExtra = new ArrayList<HashMap<String, String>>();
        HashMap<String, String> extra = new HashMap<String, String>();
        
        //decodificar id de usuario
        Integer id_usuario = Integer.parseInt(Base64Coder.decodeString(id_user_cod));
        userDat = this.getHomeDao().getUserById(id_usuario);
        Integer id_empresa = Integer.parseInt(userDat.get("empresa_id"));
        
        tiposProducto = this.getInvDao().getProducto_Tipos();
        
        //Se le pasa como parametro el cero para que devuelva todas las presentaciones 
        presentaciones = this.getInvDao().getProducto_Presentaciones(0);
        
        extra = this.getInvDao().getUserRolAgenteVenta(id_usuario);
        arrayExtra.add(0,extra);
        
        jsonretorno.put("ProdTipos", tiposProducto);
        jsonretorno.put("Presentaciones", presentaciones);
        jsonretorno.put("Extra", arrayExtra);
        return jsonretorno;
    }
    
    
    
    
    
    
    @RequestMapping(value="/getAllInvPre.json", method = RequestMethod.POST)
    public @ResponseBody HashMap<String,ArrayList<HashMap<String, Object>>> getAllInvPreJson(
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
        Integer app_selected = 47;
        
        //decodificar id de usuario
        Integer id_usuario = Integer.parseInt(Base64Coder.decodeString(id_user_cod));
        
        //variables para el buscador
        String tipo_prod = StringHelper.isNullString(String.valueOf(has_busqueda.get("tipo_prod")));
        String codigo = "%"+StringHelper.isNullString(String.valueOf(has_busqueda.get("codigo")))+"%";
        String descripcion = "%"+StringHelper.isNullString(String.valueOf(has_busqueda.get("descripcion")))+"%";
        String presentacion = StringHelper.isNullString(String.valueOf(has_busqueda.get("presentacion")));
        String data_string = app_selected+"___"+id_usuario+"___"+tipo_prod+"___"+codigo+"___"+descripcion+"___"+presentacion;
        
        //obtiene total de registros en base de datos, con los parametros de busqueda
        int total_items = this.getInvDao().countAll(data_string);
        
        //calcula el total de paginas
        int total_pags = resource.calculaTotalPag(total_items,items_por_pag);
        
        //variables que necesita el datagrid, para no tener que hacer uno por cada aplicativo
        DataPost dataforpos = new DataPost(orderby, desc, items_por_pag, pag_start, display_pag, input_json, cadena_busqueda,total_items,total_pags, id_user_cod);
        
        int offset = resource.__get_inicio_offset(items_por_pag, pag_start);
        
        //obtiene los registros para el grid, de acuerdo a los parametros de busqueda
        jsonretorno.put("Data", this.getInvDao().getInvPreGrid(data_string, offset, items_por_pag, orderby, desc));
        //obtiene el hash para los datos que necesita el datagrid
        jsonretorno.put("DataForGrid", dataforpos.formaHashForPos(dataforpos));
        
        return jsonretorno;
    }
    
    
    //este es solo para probar
    @RequestMapping(method = RequestMethod.POST, value="/getInvPre.json")
    public @ResponseBody HashMap<String,ArrayList<HashMap<String, String>>> getInvPreJson(
            @RequestParam(value="id", required=true) Integer id,
            @RequestParam(value="iu", required=true) String id_user,
            Model model
        ) {
        
        log.log(Level.INFO, "Ejecutando getInvSeccion de {0}", InvPreController.class.getName());
        HashMap<String,ArrayList<HashMap<String, String>>> jsonretorno = new HashMap<String,ArrayList<HashMap<String, String>>>();
        HashMap<String, String> userDat = new HashMap<String, String>();
        ArrayList<HashMap<String, String>> datosInvPre = new ArrayList<HashMap<String, String>>();
        ArrayList<HashMap<String, String>> presentaciones = new ArrayList<HashMap<String, String>>();
        ArrayList<HashMap<String, String>> monedas = new ArrayList<HashMap<String, String>>();
        
        //decodificar id de usuario
        Integer id_usuario = Integer.parseInt(Base64Coder.decodeString(id_user));
        userDat = this.getHomeDao().getUserById(id_usuario);
        
        Integer id_empresa = Integer.parseInt(userDat.get("empresa_id"));
        
        if( id != 0  ){
            datosInvPre = this.getInvDao().getInvPre_Datos(id);
            presentaciones=this.getInvDao().getProducto_PresentacionesON(Integer.parseInt(datosInvPre.get(0).get("inv_prod_id")));
        }else{
            //este es solo para obtener el id de la moneda de las listas
            datosInvPre = this.getInvDao().getInvPre_MonedaListas(id_empresa);
        }
        
        monedas=this.getInvDao().getMonedas2();
        
        jsonretorno.put("InvPre", datosInvPre);
        jsonretorno.put("Presentaciones", presentaciones);
        jsonretorno.put("Monedas", monedas);
        
        
        return jsonretorno;
    }
    
    //obtiene los productos para el buscador
    @RequestMapping(method = RequestMethod.POST, value="/get_buscador_productos.json")
    public @ResponseBody HashMap<String,ArrayList<HashMap<String, String>>> getProductosJson(
            @RequestParam(value="sku", required=true) String sku,
            @RequestParam(value="tipo", required=true) String tipo,
            @RequestParam(value="descripcion", required=true) String descripcion,
            @RequestParam(value="iu", required=true) String id_user,
            Model model
            ) {
        
        log.log(Level.INFO, "Ejecutando getProductosJson de {0}", InvPreController.class.getName());
        HashMap<String,ArrayList<HashMap<String, String>>> jsonretorno = new HashMap<String,ArrayList<HashMap<String, String>>>();
        ArrayList<HashMap<String, String>> productos = new ArrayList<HashMap<String, String>>();
        HashMap<String, String> userDat = new HashMap<String, String>();
        //decodificar id de usuario
        Integer id_usuario = Integer.parseInt(Base64Coder.decodeString(id_user));
        userDat = this.getHomeDao().getUserById(id_usuario);
        
        Integer id_empresa = Integer.parseInt(userDat.get("empresa_id"));
        
        productos = this.getInvDao().getBuscadorProductos(sku, tipo, descripcion, id_empresa);
        
        jsonretorno.put("productos", productos);
        
        return jsonretorno;
    }
    
    
    
    //obtiene los productos para el buscador
    @RequestMapping(method = RequestMethod.POST, value="/gatDatosProducto.json")
    public @ResponseBody HashMap<String,ArrayList<HashMap<String, String>>> gatDatosProductoJson(
            @RequestParam(value="codigo", required=true) String codigo,
            @RequestParam(value="iu", required=true) String id_user,
            Model model
        ) {
        
        log.log(Level.INFO, "Ejecutando getProductosJson de {0}", InvPreController.class.getName());
        HashMap<String,ArrayList<HashMap<String, String>>> jsonretorno = new HashMap<String,ArrayList<HashMap<String, String>>>();
        ArrayList<HashMap<String, String>> producto = new ArrayList<HashMap<String, String>>();
        ArrayList<HashMap<String, String>> presentaciones = new ArrayList<HashMap<String, String>>();
        HashMap<String, String> userDat = new HashMap<String, String>();
        
        //decodificar id de usuario
        Integer id_usuario = Integer.parseInt(Base64Coder.decodeString(id_user));
        userDat = this.getHomeDao().getUserById(id_usuario);
        
        Integer id_empresa = Integer.parseInt(userDat.get("empresa_id"));
        
        producto = this.getInvDao().getDataProductBySku(codigo, id_empresa);
        
        if(producto.size()>0){
            presentaciones=this.getInvDao().getProducto_PresentacionesON(Integer.parseInt(producto.get(0).get("id")));
        }
        
        jsonretorno.put("Producto", producto);
        jsonretorno.put("Presentaciones", presentaciones);
        
        return jsonretorno;
    }
    
    
    //obtiene las presentaciones de un producto en especifico
    @RequestMapping(method = RequestMethod.POST, value="/getPresentacionesProducto.json")
    public @ResponseBody HashMap<String,ArrayList<HashMap<String, String>>> getPresentacionesProductoJson(
            @RequestParam(value="id_prod", required=true) Integer id_prod,
            Model model
    ) {
        
        HashMap<String,ArrayList<HashMap<String, String>>> jsonretorno = new HashMap<String,ArrayList<HashMap<String, String>>>();
        ArrayList<HashMap<String, String>> presentaciones = new ArrayList<HashMap<String, String>>();
        
        presentaciones=this.getInvDao().getProducto_PresentacionesON(id_prod);
        jsonretorno.put("Presentaciones", presentaciones);
        
        return jsonretorno;
    }
    
    
    
    
    
    //crear y editar
    @RequestMapping(method = RequestMethod.POST, value="/edit.json")
    public @ResponseBody HashMap<String, String> editJson(
            @RequestParam(value="identificador", required=true) String id,
            @RequestParam(value="producto_id", required=true) String producto_id,
            @RequestParam(value="select_presentacion", required=true) String select_presentacion,
            @RequestParam(value="lista1", required=false) String lista1,
            @RequestParam(value="lista2", required=false) String lista2,
            @RequestParam(value="lista3", required=false) String lista3,
            @RequestParam(value="lista4", required=false) String lista4,
            @RequestParam(value="lista5", required=false) String lista5,
            @RequestParam(value="lista6", required=false) String lista6,
            @RequestParam(value="lista7", required=false) String lista7,
            @RequestParam(value="lista8", required=false) String lista8,
            @RequestParam(value="lista9", required=false) String lista9,
            @RequestParam(value="lista10", required=false) String lista10,
            @RequestParam(value="descto1", required=false) String descto1,
            @RequestParam(value="descto2", required=false) String descto2,
            @RequestParam(value="descto3", required=false) String descto3,
            @RequestParam(value="descto4", required=false) String descto4,
            @RequestParam(value="descto5", required=false) String descto5,
            @RequestParam(value="descto6", required=false) String descto6,
            @RequestParam(value="descto7", required=false) String descto7,
            @RequestParam(value="descto8", required=false) String descto8,
            @RequestParam(value="descto9", required=false) String descto9,
            @RequestParam(value="descto10", required=false) String descto10,
            @RequestParam(value="select_base_precio1", required=false) String select_base_precio1,
            @RequestParam(value="select_base_precio2", required=false) String select_base_precio2,
            @RequestParam(value="select_base_precio3", required=false) String select_base_precio3,
            @RequestParam(value="select_base_precio4", required=false) String select_base_precio4,
            @RequestParam(value="select_base_precio5", required=false) String select_base_precio5,
            @RequestParam(value="select_base_precio6", required=false) String select_base_precio6,
            @RequestParam(value="select_base_precio7", required=false) String select_base_precio7,
            @RequestParam(value="select_base_precio8", required=false) String select_base_precio8,
            @RequestParam(value="select_base_precio9", required=false) String select_base_precio9,
            @RequestParam(value="select_base_precio10", required=false) String select_base_precio10,
            @RequestParam(value="valor_default_l1", required=false) String valor_default_l1,
            @RequestParam(value="valor_default_l2", required=false) String valor_default_l2,
            @RequestParam(value="valor_default_l3", required=false) String valor_default_l3,
            @RequestParam(value="valor_default_l4", required=false) String valor_default_l4,
            @RequestParam(value="valor_default_l5", required=false) String valor_default_l5,
            @RequestParam(value="valor_default_l6", required=false) String valor_default_l6,
            @RequestParam(value="valor_default_l7", required=false) String valor_default_l7,
            @RequestParam(value="valor_default_l8", required=false) String valor_default_l8,
            @RequestParam(value="valor_default_l9", required=false) String valor_default_l9,
            @RequestParam(value="valor_default_l10", required=false) String valor_default_l10,
            @RequestParam(value="select_forma_calculo1", required=false) String select_forma_calculo1,
            @RequestParam(value="select_forma_calculo2", required=false) String select_forma_calculo2,
            @RequestParam(value="select_forma_calculo3", required=false) String select_forma_calculo3,
            @RequestParam(value="select_forma_calculo4", required=false) String select_forma_calculo4,
            @RequestParam(value="select_forma_calculo5", required=false) String select_forma_calculo5,
            @RequestParam(value="select_forma_calculo6", required=false) String select_forma_calculo6,
            @RequestParam(value="select_forma_calculo7", required=false) String select_forma_calculo7,
            @RequestParam(value="select_forma_calculo8", required=false) String select_forma_calculo8,
            @RequestParam(value="select_forma_calculo9", required=false) String select_forma_calculo9,
            @RequestParam(value="select_forma_calculo10", required=false) String select_forma_calculo10,
            @RequestParam(value="select_operacion1", required=false) String select_operacion1,
            @RequestParam(value="select_operacion2", required=false) String select_operacion2,
            @RequestParam(value="select_operacion3", required=false) String select_operacion3,
            @RequestParam(value="select_operacion4", required=false) String select_operacion4,
            @RequestParam(value="select_operacion5", required=false) String select_operacion5,
            @RequestParam(value="select_operacion6", required=false) String select_operacion6,
            @RequestParam(value="select_operacion7", required=false) String select_operacion7,
            @RequestParam(value="select_operacion8", required=false) String select_operacion8,
            @RequestParam(value="select_operacion9", required=false) String select_operacion9,
            @RequestParam(value="select_operacion10", required=false) String select_operacion10,
            @RequestParam(value="select_tipo_redondeo1", required=false) String select_tipo_redondeo1,
            @RequestParam(value="select_tipo_redondeo2", required=false) String select_tipo_redondeo2,
            @RequestParam(value="select_tipo_redondeo3", required=false) String select_tipo_redondeo3,
            @RequestParam(value="select_tipo_redondeo4", required=false) String select_tipo_redondeo4,
            @RequestParam(value="select_tipo_redondeo5", required=false) String select_tipo_redondeo5,
            @RequestParam(value="select_tipo_redondeo6", required=false) String select_tipo_redondeo6,
            @RequestParam(value="select_tipo_redondeo7", required=false) String select_tipo_redondeo7,
            @RequestParam(value="select_tipo_redondeo8", required=false) String select_tipo_redondeo8,
            @RequestParam(value="select_tipo_redondeo9", required=false) String select_tipo_redondeo9,
            @RequestParam(value="select_tipo_redondeo10", required=false) String select_tipo_redondeo10,
            
            @RequestParam(value="select_moneda1", required=false) String select_moneda1,
            @RequestParam(value="select_moneda2", required=false) String select_moneda2,
            @RequestParam(value="select_moneda3", required=false) String select_moneda3,
            @RequestParam(value="select_moneda4", required=false) String select_moneda4,
            @RequestParam(value="select_moneda5", required=false) String select_moneda5,
            @RequestParam(value="select_moneda6", required=false) String select_moneda6,
            @RequestParam(value="select_moneda7", required=false) String select_moneda7,
            @RequestParam(value="select_moneda8", required=false) String select_moneda8,
            @RequestParam(value="select_moneda9", required=false) String select_moneda9,
            @RequestParam(value="select_moneda10", required=false) String select_moneda10,
            
        Model model,@ModelAttribute("user") UserSessionData user
        ) {

        HashMap<String, String> jsonretorno = new HashMap<String, String>();
        HashMap<String, String> succes = new HashMap<String, String>();
        Integer app_selected = 47;
        String command_selected = "new";
        Integer id_usuario= user.getUserId();//variable para el id  del usuario
        String extra_data_array = "'sin datos'";
        String actualizo = "0";
        
        lista1 = StringHelper.removerComas(lista1);
        lista2 = StringHelper.removerComas(lista2);
        lista3 = StringHelper.removerComas(lista3);
        lista4 = StringHelper.removerComas(lista4);
        lista5 = StringHelper.removerComas(lista5);
        lista6 = StringHelper.removerComas(lista6);
        lista7 = StringHelper.removerComas(lista7);
        lista8 = StringHelper.removerComas(lista8);
        lista9 = StringHelper.removerComas(lista9);
        lista10 = StringHelper.removerComas(lista10);
        descto1 = StringHelper.removerComas(descto1);
        descto2 = StringHelper.removerComas(descto2);
        descto3 = StringHelper.removerComas(descto3);
        descto4 = StringHelper.removerComas(descto4);
        descto5 = StringHelper.removerComas(descto5);
        descto6 = StringHelper.removerComas(descto6);
        descto7 = StringHelper.removerComas(descto7);
        descto8 = StringHelper.removerComas(descto8);
        descto9 = StringHelper.removerComas(descto9);
        descto10 = StringHelper.removerComas(descto10);
        valor_default_l1 = StringHelper.removerComas(valor_default_l1);
        valor_default_l2 = StringHelper.removerComas(valor_default_l2);
        valor_default_l3 = StringHelper.removerComas(valor_default_l3);
        valor_default_l4 = StringHelper.removerComas(valor_default_l4);
        valor_default_l5 = StringHelper.removerComas(valor_default_l5);
        valor_default_l6 = StringHelper.removerComas(valor_default_l6);
        valor_default_l7 = StringHelper.removerComas(valor_default_l7);
        valor_default_l8 = StringHelper.removerComas(valor_default_l8);
        valor_default_l9 = StringHelper.removerComas(valor_default_l9);
        valor_default_l10 = StringHelper.removerComas(valor_default_l10);
        
        select_moneda1 = StringHelper.verificarSelect(select_moneda1);
        select_moneda2 = StringHelper.verificarSelect(select_moneda2);
        select_moneda3 = StringHelper.verificarSelect(select_moneda3);
        select_moneda4 = StringHelper.verificarSelect(select_moneda4);
        select_moneda5 = StringHelper.verificarSelect(select_moneda5);
        select_moneda6 = StringHelper.verificarSelect(select_moneda6);
        select_moneda7 = StringHelper.verificarSelect(select_moneda7);
        select_moneda8 = StringHelper.verificarSelect(select_moneda8);
        select_moneda9 = StringHelper.verificarSelect(select_moneda9);
        select_moneda10 = StringHelper.verificarSelect(select_moneda10);
        
        
        if( id.equals("0") ){
            command_selected = "new";
        }else{
            command_selected = "edit";
        }
        //descto10 = 25
        String data_string = 
                app_selected+"___"+
                command_selected+"___"+
                id_usuario+"___"+
                id+"___"+
                producto_id+"___"+
                lista1+"___"+
                lista2+"___"+
                lista3+"___"+
                lista4+"___"+
                lista5+"___"+
                lista6+"___"+
                lista7+"___"+
                lista8+"___"+
                lista9+"___"+
                lista10+"___"+
                descto1+"___"+
                descto2+"___"+
                descto3+"___"+
                descto4+"___"+
                descto5+"___"+
                descto6+"___"+
                descto7+"___"+
                descto8+"___"+
                descto9+"___"+
                descto10+"___"+
                valor_default_l1+"___"+
                valor_default_l2+"___"+
                valor_default_l3+"___"+
                valor_default_l4+"___"+
                valor_default_l5+"___"+
                valor_default_l6+"___"+
                valor_default_l7+"___"+
                valor_default_l8+"___"+
                valor_default_l9+"___"+
                valor_default_l10+"___"+
                select_base_precio1+"___"+
                select_base_precio2+"___"+
                select_base_precio3+"___"+
                select_base_precio4+"___"+
                select_base_precio5+"___"+
                select_base_precio6+"___"+
                select_base_precio7+"___"+
                select_base_precio8+"___"+
                select_base_precio9+"___"+
                select_base_precio10+"___"+
                select_forma_calculo1+"___"+
                select_forma_calculo2+"___"+
                select_forma_calculo3+"___"+
                select_forma_calculo4+"___"+
                select_forma_calculo5+"___"+
                select_forma_calculo6+"___"+
                select_forma_calculo7+"___"+
                select_forma_calculo8+"___"+
                select_forma_calculo9+"___"+
                select_forma_calculo10+"___"+
                select_operacion1+"___"+
                select_operacion2+"___"+
                select_operacion3+"___"+
                select_operacion4+"___"+
                select_operacion5+"___"+
                select_operacion6+"___"+
                select_operacion7+"___"+
                select_operacion8+"___"+
                select_operacion9+"___"+
                select_operacion10+"___"+
                select_tipo_redondeo1+"___"+
                select_tipo_redondeo2+"___"+
                select_tipo_redondeo3+"___"+
                select_tipo_redondeo4+"___"+
                select_tipo_redondeo5+"___"+
                select_tipo_redondeo6+"___"+
                select_tipo_redondeo7+"___"+
                select_tipo_redondeo8+"___"+
                select_tipo_redondeo9+"___"+
                select_tipo_redondeo10+"___"+
                select_presentacion+"___"+
                select_moneda1+"___"+
                select_moneda2+"___"+
                select_moneda3+"___"+
                select_moneda4+"___"+
                select_moneda5+"___"+
                select_moneda6+"___"+
                select_moneda7+"___"+
                select_moneda8+"___"+
                select_moneda9+"___"+
                select_moneda10;
        
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
        
        Integer app_selected = 47;
        String command_selected = "delete";
        String extra_data_array = "'sin datos'";
        String data_string = app_selected+"___"+command_selected+"___"+id_usuario+"___"+id;
        
        System.out.println("Ejecutando borrado logico de Precio");
        jsonretorno.put("success",String.valueOf( this.getInvDao().selectFunctionForThisApp(data_string,extra_data_array)) );
        
        return jsonretorno;
    }
}
