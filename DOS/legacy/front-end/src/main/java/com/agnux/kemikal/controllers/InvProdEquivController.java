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
import com.agnux.kemikal.reportes.PdfReporteFormulas;
import com.itextpdf.text.DocumentException;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URISyntaxException;
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
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.servlet.ModelAndView;


@Controller
@SessionAttributes({"user"})
@RequestMapping("/invprodequiv/")
public class InvProdEquivController {
    
    ResourceProject resource = new ResourceProject();
    private static final Logger log  = Logger.getLogger(InvProdEquivController.class.getName());
    
    @Autowired
    @Qualifier("daoInv")   //utilizo todos los metodos de invinterfacedao
    private InvInterfaceDao invDao;
    
    public InvInterfaceDao getInvDao() {
        return invDao;
    }
    
    @Autowired
    @Qualifier("daoHome")   //permite controlar usuarios que entren
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
     @ModelAttribute("user") UserSessionData user)throws ServletException, IOException {
        
        log.log(Level.INFO, "Ejecutando starUp de {0}", InvProdEquivController.class.getName());
        LinkedHashMap<String,String> infoConstruccionTabla = new LinkedHashMap<String,String>();
        
        infoConstruccionTabla.put("id", "Acciones:90");
        infoConstruccionTabla.put("codigo", "Código:100");
        infoConstruccionTabla.put("descripcion", "Descripción:200");
        
        ModelAndView x = new ModelAndView("invprodequiv/startup", "title", "Cat&aacute;logo de Productos Equivalentes");
        
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
        String decodificado = Base64Coder.decodeString(codificado);
        
        //id de usuario codificado
        x = x.addObject("iu", codificado);
     
        return x;
    }
    
    
    @RequestMapping(value="/getAllProductosEquivalentes.json", method = RequestMethod.POST)
    public @ResponseBody HashMap<String,ArrayList<HashMap<String, Object>>> getProductosEquivalentesJson(
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
        
        //aplicativo catalogo de productos equivalentes 
        Integer app_selected = 96;
        
        //decodificar id de usuario
        Integer id_usuario = Integer.parseInt(Base64Coder.decodeString(id_user_cod));
        
        //variables para el buscador
        String codigo = StringHelper.isNullString(String.valueOf(has_busqueda.get("codigo")));
        String descripcion = StringHelper.isNullString(String.valueOf(has_busqueda.get("descripcion")));
        
       
        
        String data_string = app_selected+"___"+id_usuario+"___"+codigo+"___"+descripcion;
        
        //obtiene total de registros en base de datos, con los parametros de busqueda
        int total_items = this.getInvDao().countAll(data_string);              
        System.out.println(data_string);        
        
        //calcula el total de paginas
        int total_pags = resource.calculaTotalPag(total_items,items_por_pag);
        
        //variables que necesita el datagrid, para no tener que hacer uno por cada aplicativo
        DataPost dataforpos = new DataPost(orderby, desc, items_por_pag, pag_start, display_pag, input_json, cadena_busqueda,total_items,total_pags, id_user_cod);
        
        int offset = resource.__get_inicio_offset(items_por_pag, pag_start);
        
        //obtiene los registros para el grid, de acuerdo a los parametros de busqueda
        jsonretorno.put("Data", this.getInvDao().getProductosEquivalentes_PaginaGrid(data_string, offset, items_por_pag, orderby, desc));
        //obtiene el hash para los datos que necesita el datagrid
        jsonretorno.put("DataForGrid", dataforpos.formaHashForPos(dataforpos));
        
        return jsonretorno;
    }
     
     
     @RequestMapping(method = RequestMethod.POST, value="/getProductosEquivalentes.json")
    public @ResponseBody HashMap<String,ArrayList<HashMap<String, String>>> getProductosEquivalentesJson(
            @RequestParam(value="id", required=true) String id,
            @RequestParam(value="iu", required=true) String id_user_cod,
            
            //@RequestParam(value="numero_buscador", required=true) Integer numero_buscador,
            Model model
            ){
        
        log.log(Level.INFO, "Ejecutando getProductosEquivalentesjson de {0}", InvProdEquivController.class.getName());
        HashMap<String,ArrayList<HashMap<String, String>>> jsonretorno = new HashMap<String,ArrayList<HashMap<String, String>>>();
        HashMap<String, String> userDat = new HashMap<String, String>();
       
        ArrayList<HashMap<String, String>> datosProductosEquivalentes = new ArrayList<HashMap<String, String>>(); 
        ArrayList<HashMap<String, String>> datosProductosEquivalentesMinigrid = new ArrayList<HashMap<String, String>>(); 
        
        //decodificar id de usuario
        Integer id_usuario = Integer.parseInt(Base64Coder.decodeString(id_user_cod));
        userDat = this.getHomeDao().getUserById(id_usuario);
        
       // Integer id_empresa = Integer.parseInt(userDat.get("empresa_id"));
        if(!id.equals("0")){
            String id_tmp[] = id.split("---");
            id = id_tmp[0];
        }
        if(!id.equals("0")){
            datosProductosEquivalentes = this.getInvDao().getProductosEquivalentes_Datos(Integer.parseInt(id));
            datosProductosEquivalentesMinigrid = this.getInvDao().getProductosEquivalentes_DatosMinigrid(id);
        }
        
        
       jsonretorno.put("ProductosEquivalentes", datosProductosEquivalentes);
       jsonretorno.put("ProductosEquivalentes_DatosMinigrid", datosProductosEquivalentesMinigrid);
        return jsonretorno;
    }
    
     
    //crear y editar una  formula
    @RequestMapping(method = RequestMethod.POST, value="/edit.json")
    public @ResponseBody HashMap<String, String> editJson(
        @RequestParam(value="id_prod", required=true) String inv_prod_id,
        @RequestParam(value="identificador", required=true) Integer id,
        @RequestParam(value="eliminar", required=true) String[] eliminar,
        @RequestParam(value="observacion", required=true) String[] observacion,
        @RequestParam(value="id_producto_equivalente", required=true) String[] id_producto_equivalente,
        Model model,@ModelAttribute("user") UserSessionData user
    ) {
        
        HashMap<String, String> jsonretorno = new HashMap<String, String>();
        HashMap<String, String> succes = new HashMap<String, String>();
        Integer app_selected = 96;//catalogo de agentes
        String command_selected = "new";
        Integer id_usuario= user.getUserId();//variable para el id  del usuario
        //String extra_data_array = "'sin datos'";
        String actualizo = "0";
        

        String arreglo[];
            arreglo = new String[id_producto_equivalente.length];
            
            for(int i=0; i<id_producto_equivalente.length; i++) { 
                arreglo[i]= "'"+eliminar[i]+"___"+id_producto_equivalente[i]+"___"+observacion[i]+"'";
                System.out.println(arreglo[i]);
            }
            
            //serializar el arreglo
            String extra_data_array = StringUtils.join(arreglo, ",");
        if( id==0 ){
            command_selected = "new";
        }else{
            command_selected = "edit";
        }
        
        
        String data_string = app_selected+"___"+command_selected+"___"+id_usuario+"___"+id+"___"+inv_prod_id;//+"___"+inv_prod_id+"___"+nivel;
        
        succes = this.getInvDao().selectFunctionValidateAaplicativo(data_string,app_selected,extra_data_array);
        
        log.log(Level.INFO, "despues de validacion {0}", String.valueOf(succes.get("success")));
        if( String.valueOf(succes.get("success")).equals("true") ){
            actualizo = this.getInvDao().selectFunctionForThisApp(data_string, extra_data_array);
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
        
        Integer app_selected = 96;
        String command_selected = "delete";
        String extra_data_array = "'sin datos'";
        String data_string = app_selected+"___"+command_selected+"___"+id_usuario+"___"+id;
        
        System.out.println("Ejecutando borrado logico productos");
        jsonretorno.put("success",String.valueOf( this.getInvDao().selectFunctionForThisApp(data_string,extra_data_array)) );
        
        return jsonretorno;
    }
    
    
    
    
    //Buscador de de productos
    @RequestMapping(method = RequestMethod.POST, value="/get_buscador_productos_equivalentes.json")
    public @ResponseBody HashMap<String,ArrayList<HashMap<String, String>>> getBuscadorProductosEquivalentesJson(
            @RequestParam(value="sku", required=true) String sku,
            @RequestParam(value="tipo", required=true) String tipo,
            @RequestParam(value="descripcion", required=true) String descripcion,
            @RequestParam(value="iu", required=true) String id_user,
        @RequestParam(value="id_producto", required=true) String id_prod,
            Model model
            ) {
        
        System.out.println("Busqueda de producto");
        
        HashMap<String,ArrayList<HashMap<String, String>>> jsonretorno = new HashMap<String,ArrayList<HashMap<String, String>>>();
        HashMap<String, String> userDat = new HashMap<String, String>();
        //decodificar id de usuario
        Integer id_usuario = Integer.parseInt(Base64Coder.decodeString(id_user));
        userDat = this.getHomeDao().getUserById(id_usuario);
        
        Integer id_empresa = Integer.parseInt(userDat.get("empresa_id"));
        
        jsonretorno.put("Productos", this.getInvDao().getBuscadorProductosEquivalentes(sku, tipo, descripcion,id_empresa,id_prod));
        
        return jsonretorno;
    }
    //fin del buscador de productos
    
    
    
    //obtiene lineas de producto y datos para el buscador
    @RequestMapping(method = RequestMethod.POST, value="/getProductoTipos.json")
    public @ResponseBody HashMap<String,ArrayList<HashMap<String, String>>> getDataLineasJson(
            @RequestParam(value="iu", required=true) String id_user_cod,
            @RequestParam(value="buscador_producto", required=true) String buscador_producto,
            Model model
    ) {
        System.out.println(buscador_producto);
        HashMap<String,ArrayList<HashMap<String, String>>> jsonretorno = new HashMap<String,ArrayList<HashMap<String, String>>>();
        ArrayList<HashMap<String, String>> arrayTiposProducto = new ArrayList<HashMap<String, String>>();
        
        //decodificar id de usuario
        Integer id_usuario = Integer.parseInt(Base64Coder.decodeString(id_user_cod));
        
        arrayTiposProducto=this.getInvDao().getProducto_Tipos_para_productos_equivalentes(buscador_producto);
        jsonretorno.put("prodTipos", arrayTiposProducto);
        
        return jsonretorno;
    }
    
    //Busca un sku en especifico
    @RequestMapping(method = RequestMethod.POST, value="/get_busca_sku_prod.json")
    public @ResponseBody HashMap<String,ArrayList<HashMap<String, String>>> getBuscaSkuProdJson(
            @RequestParam(value="sku", required=true) String sku,
            @RequestParam(value="iu", required=true) String id_user,
            Model model
            ) {
        Integer id_usuario = Integer.parseInt(Base64Coder.decodeString(id_user));
        
        HashMap<String,ArrayList<HashMap<String, String>>> jsonretorno = new HashMap<String,ArrayList<HashMap<String, String>>>();
        jsonretorno.put("Sku", this.getInvDao().getProductos_sku(sku.toUpperCase(),id_usuario));
        
        return jsonretorno;
    }
    
}
