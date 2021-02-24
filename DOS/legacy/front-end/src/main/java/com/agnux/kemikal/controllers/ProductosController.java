package com.agnux.kemikal.controllers;

import com.agnux.cfd.v2.Base64Coder;
import com.agnux.common.helpers.FileHelper;
import com.agnux.common.helpers.StringHelper;
import com.agnux.common.obj.DataPost;
import com.agnux.common.obj.ResourceProject;
import com.agnux.common.obj.UserSessionData;
import com.agnux.kemikal.interfacedaos.GralInterfaceDao;
import com.agnux.kemikal.interfacedaos.HomeInterfaceDao;
import com.agnux.kemikal.interfacedaos.InvInterfaceDao;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;


@Controller
@SessionAttributes({"user"})
@RequestMapping("/productos/")
public class ProductosController {
    
    private static final Logger log  = Logger.getLogger(ProductosController.class.getName());
    ResourceProject resource = new ResourceProject();
    
    @Autowired
    @Qualifier("daoInv")
    private InvInterfaceDao invDao;
    
    @Autowired
    @Qualifier("daoHome")
    private HomeInterfaceDao HomeDao;
    
    @Autowired
    @Qualifier("daoGral")
    private GralInterfaceDao gralDao;
    
    public GralInterfaceDao getGralDao() {
        return gralDao;
    }
    
    public InvInterfaceDao getInvDao() {
        return invDao;
    }
    
    public HomeInterfaceDao getHomeDao() {
        return HomeDao;
    }
    

    
    
    @RequestMapping(value="/startup.agnux")
    public ModelAndView startUp(HttpServletRequest request, HttpServletResponse response, @ModelAttribute("user") UserSessionData user)
            throws ServletException, IOException {
        
        log.log(Level.INFO, "Ejecutando starUp de {0}", ProductosController.class.getName());
        LinkedHashMap<String,String> infoConstruccionTabla = new LinkedHashMap<String,String>();
        
        infoConstruccionTabla.put("id", "Acciones:90");
        infoConstruccionTabla.put("sku", "C&oacute;digo:100");
        infoConstruccionTabla.put("descripcion", "Descripci&oacute;n:330");
        infoConstruccionTabla.put("unidad", "UM:100");
        infoConstruccionTabla.put("tipo", "Tipo:110");
        
        ModelAndView x = new ModelAndView("productos/startup", "title", "Cat&aacute;logo de Productos");
        
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
    
    
    
    //datos del grid
    @RequestMapping(value="/getProductos.json", method = RequestMethod.POST)
    public @ResponseBody HashMap<String,ArrayList<HashMap<String, Object>>> getProductosJson(
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
        
        //aplicativo productos
        Integer app_selected = 8;
        
        //decodificar id de usuario
        Integer id_usuario = Integer.parseInt(Base64Coder.decodeString(id_user_cod));
        
        
        //variables para el buscador
        String sku = "%"+StringHelper.isNullString(String.valueOf(has_busqueda.get("sku_busqueda")))+"%";
        String descripcion = "%"+StringHelper.isNullString(String.valueOf(has_busqueda.get("descripcion_busqueda")))+"%";
        String por_tipo = StringHelper.isNullString(String.valueOf(has_busqueda.get("por_tipo")));
        
        String data_string = app_selected+"___"+id_usuario+"___"+sku+"___"+descripcion+"___"+por_tipo;
        
        //obtiene total de registros en base de datos, con los parametros de busqueda
        int total_items = this.getInvDao().countAll(data_string);
        
        //calcula el total de paginas
        int total_pags = resource.calculaTotalPag(total_items,items_por_pag);
        
        //variables que necesita el datagrid, para no tener que hacer uno por cada aplicativo
        DataPost dataforpos = new DataPost(orderby, desc, items_por_pag, pag_start, display_pag, input_json, cadena_busqueda,total_items,total_pags, id_user_cod);
        
        int offset = resource.__get_inicio_offset(items_por_pag, pag_start);
        
        //obtiene los registros para el grid, de acuerdo a los parametros de busqueda
        jsonretorno.put("Data", this.getInvDao().getProductos_PaginaGrid(data_string, offset, items_por_pag, orderby, desc));
        
        //obtiene el hash para los datos que necesita el datagrid
        jsonretorno.put("DataForGrid", dataforpos.formaHashForPos(dataforpos));
        
        return jsonretorno;
    }
    
    
    
    
    //obtiene lineas de producto y datos para el buscador
    @RequestMapping(method = RequestMethod.POST, value="/getInit.json")
    public @ResponseBody HashMap<String,ArrayList<HashMap<String, String>>> getInitJson(
            @RequestParam(value="iu", required=true) String id_user_cod,
            Model model
        ) {
        
        HashMap<String,ArrayList<HashMap<String, String>>> jsonretorno = new HashMap<String,ArrayList<HashMap<String, String>>>();
        ArrayList<HashMap<String, String>> arrayExtra = new ArrayList<HashMap<String, String>>();
        HashMap<String, String> extra = new HashMap<String, String>();
        HashMap<String, String> userDat = new HashMap<String, String>();
        
        //Decodificar id de usuario
        Integer id_usuario = Integer.parseInt(Base64Coder.decodeString(id_user_cod));
        userDat = this.getHomeDao().getUserById(id_usuario);
        Integer id_empresa = Integer.parseInt(userDat.get("empresa_id"));
        
        extra = this.getInvDao().getUserRolAgenteVenta(id_usuario);
        arrayExtra.add(0,extra);
        
        jsonretorno.put("prodTipos", this.getInvDao().getProducto_Tipos());
        jsonretorno.put("Lineas", this.getInvDao().getProducto_Lineas(id_empresa));
        jsonretorno.put("Marcas", this.getInvDao().getProducto_Marcas(id_empresa));
        jsonretorno.put("Monedas", this.getInvDao().getMonedas());
        
        jsonretorno.put("Extra", arrayExtra);
        return jsonretorno;
    }
    
    
    
    
    private String genera_treeview (ArrayList<HashMap<String, String>> lineas){
        String cad_retorno = "<ul id='navigation'>";
        //System.out.println("Tamaño del Arreglo2: "+hm_emp_dir.size());
        for (int i=0; i<=lineas.size()-1;i++){
            HashMap<String,String> lin = lineas.get(i);
            
            if(Integer.parseInt(lin.get("id").toString())==Integer.parseInt(lin.get("id_linea_padre").toString())){
                String tmp_ret=genera_treeview_nivel1(1,Integer.parseInt(lin.get("id").toString()),lineas);
                cad_retorno += "<li> <a href='#"+lin.get("id").toString()+"'>"+lin.get("titulo").toString()+"</a><ul>"+tmp_ret +"</ul></li>";
            }
        }
        cad_retorno += "</ul>";
        
        return cad_retorno;
    }
    
    private String genera_treeview_nivel1 (Integer tiene_hijos, Integer id_parent,ArrayList<HashMap<String, String>> lineas){
        String cad_retorno="";
	String cad_hijos = "";
        
        for (int i=0; i<=lineas.size()-1;i++){
            HashMap<String,String> lin = lineas.get(i);
            Integer idpadre = Integer.parseInt(lin.get("id_linea_padre").toString());
            Integer id = Integer.parseInt(lin.get("id").toString());
            String titulo = lin.get("titulo").toString();
            
            if(idpadre == id_parent && id != id_parent){
                tiene_hijos = vefica_hijos_treeview(id,lineas);
                if(tiene_hijos== 1){
                        cad_hijos = genera_treeview_nivel2(tiene_hijos,id,lineas);
                        cad_retorno += "<li> <a href='#"+id+"'>"+titulo+"</a>"+cad_hijos+"</li>";
                }else{
                        cad_retorno += "<li><a href='#"+id+"'>"+titulo+"</a></li>";
                }
            } 
        }
        
        return cad_retorno;
    }
    
    
    
    private int vefica_hijos_treeview (Integer id_parent,ArrayList<HashMap<String, String>> lineas){
        int retorno=0;
        for (int i=0; i<=lineas.size()-1;i++){
            HashMap<String,String> lin = lineas.get(i);
            //System.out.println("id_linea_padre:"+lin.get("id_linea_padre").toString());
            Integer id = Integer.parseInt(lin.get("id").toString());
            Integer idpadre = Integer.parseInt(lin.get("id_linea_padre").toString());
            
            if(id == id_parent && id != idpadre){
                retorno = 1;
            } 
            
        }
        return retorno;
    }
    
    private String genera_treeview_nivel2 (Integer tiene_hijos, Integer id_parent,ArrayList<HashMap<String, String>> lineas){
        String cad_retorno="";
        
        for (int i=0; i<=lineas.size()-1;i++){
            HashMap<String,String> lin = lineas.get(i);
            Integer id = Integer.parseInt(lin.get("id").toString());
            Integer idpadre = Integer.parseInt(lin.get("id_linea_padre").toString());
            String titulo = lin.get("titulo").toString();
            
            if(idpadre == id_parent && id != id_parent){
                tiene_hijos = vefica_hijos_treeview(id,lineas);
                String cad_hijos = "";
                if(tiene_hijos == 1){
                    cad_hijos= genera_treeview_nivel1(tiene_hijos,id,lineas);
                    cad_retorno += "<ul><li><a href='#"+id+"'>"+titulo+"</a><ul>"+cad_hijos+"</ul></li></ul>";
                }else{
                    cad_retorno += "<ul><li><a href='#"+id+"'>"+titulo+"</a></li></ul>";
                }
            }
        }
        
        return cad_retorno;
    }
    
    
    
    @RequestMapping(method = RequestMethod.POST, value="/getProducto.json")
    //public @ResponseBody HashMap<java.lang.String,java.lang.Object> getProveedorJson(
    public @ResponseBody HashMap<String,ArrayList<HashMap<String, String>>> getProductoJson(
            @RequestParam(value="id_producto", required=true) Integer id_producto,
            @RequestParam(value="iu", required=true) String id_user_cod,
            Model model
            ) {
        
        log.log(Level.INFO, "Ejecutando getProductoJson de {0}", ProductosController.class.getName());
        HashMap<String,ArrayList<HashMap<String, String>>> jsonretorno = new HashMap<String,ArrayList<HashMap<String, String>>>();
        HashMap<String, String> userDat = new HashMap<String, String>();
        ArrayList<HashMap<String, String>> producto = new ArrayList<HashMap<String, String>>();
        //ArrayList<HashMap<String, String>> lineas = new ArrayList<HashMap<String, String>>();
        //ArrayList<HashMap<String, String>> marcas = new ArrayList<HashMap<String, String>>();
        //ArrayList<HashMap<String, String>> familias = new ArrayList<HashMap<String, String>>();
        //ArrayList<HashMap<String, String>> subfamilias = new ArrayList<HashMap<String, String>>();
        //ArrayList<HashMap<String, String>> tiposProducto = new ArrayList<HashMap<String, String>>();
        ArrayList<HashMap<String, String>> ingredientes = new ArrayList<HashMap<String, String>>();
        ArrayList<HashMap<String, String>> presentaciones_seleccionadas = new ArrayList<HashMap<String, String>>();
        ArrayList<HashMap<String, String>> contab = new ArrayList<HashMap<String, String>>();
        ArrayList<HashMap<String, String>> arrayExtra = new ArrayList<HashMap<String, String>>();
        HashMap<String, String> extra = new HashMap<String, String>();
        Integer id_del_producto=0;
        
        //decodificar id de usuario
        Integer id_usuario = Integer.parseInt(Base64Coder.decodeString(id_user_cod));
        userDat = this.getHomeDao().getUserById(id_usuario);
        
        Integer id_empresa = Integer.parseInt(userDat.get("empresa_id"));
        Integer id_suc=0;
        //Esta variable indica si la empresa incluye modulo de produccion
        extra.put("mod_produccion", userDat.get("incluye_produccion"));
        //Esta variable indica si la empresa incluye modulo de Contabilidad
        extra.put("incluye_contab", userDat.get("incluye_contab"));
        //Esta variable indica si la empresa incluye modulo de Logistica
        extra.put("ilog", userDat.get("incluye_log"));
        //Variable que indica el nivel de la cuenta para contabilidad
        extra.put("nivel_cta", userDat.get("nivel_cta"));
        arrayExtra.add(0,extra);
        
        if( id_producto != 0  ){
            producto = this.getInvDao().getProducto_Datos(id_producto);
            ingredientes = this.getInvDao().getProducto_Ingredientes(id_producto);
            presentaciones_seleccionadas = this.getInvDao().getProducto_PresentacionesON(id_producto);
            id_del_producto = id_producto;
            
            if(userDat.get("incluye_contab").equals("true")){
                contab = this.getInvDao().getProducto_DatosContabilidad(id_producto);
            }
        }
        
        //lineas = this.getInvDao().getProducto_Lineas(id_empresa);
        //marcas = this.getInvDao().getProducto_Marcas(id_empresa);
        //familias = this.getInvDao().getInvProdSubFamilias_Familias(id_empresa);
        //subfamilias = this.getInvDao().getProducto_Subfamilias(id_empresa);
        //tiposProducto = this.getInvDao().getProducto_Tipos();
        
        jsonretorno.put("Producto",producto);
        //jsonretorno.put("Lineas", lineas);
        jsonretorno.put("Secciones", this.getInvDao().getInvProdLineas_Secciones(id_empresa));
        jsonretorno.put("Grupos", this.getInvDao().getProducto_Grupos(id_empresa));
        //jsonretorno.put("Marcas", marcas);
        //jsonretorno.put("Familias", familias);
        //jsonretorno.put("Subfamilias", subfamilias);
        jsonretorno.put("ClasifStock", this.getInvDao().getProducto_ClasificacionStock(id_empresa));
        jsonretorno.put("Clases", this.getInvDao().getProducto_Clases(id_empresa));
        jsonretorno.put("Impuestos", this.getInvDao().getEntradas_Impuestos());
        jsonretorno.put("Ieps", this.getInvDao().getIeps(id_empresa, id_suc));
        jsonretorno.put("ImptosRet", this.getInvDao().getTasasRetencionIva(id_empresa, id_suc));
        jsonretorno.put("Unidades",this.getInvDao().getProducto_Unidades());
        //jsonretorno.put("ProdTipos", tiposProducto);
        jsonretorno.put("Ingredientes", ingredientes);
        jsonretorno.put("Presentaciones", this.getInvDao().getProducto_Presentaciones(id_del_producto));
        jsonretorno.put("PresOn", presentaciones_seleccionadas);
        jsonretorno.put("Extras", arrayExtra);
        jsonretorno.put("CtaMay", this.getInvDao().getProducto_CuentasMayor(id_empresa));
        jsonretorno.put("Contab", contab);
        
        return jsonretorno;
    }
    
    //obtiene los Familias del producto seleccionado
    @RequestMapping(method = RequestMethod.POST, value="/getSubFamiliasByFamProd.json")
    //public @ResponseBody HashMap<java.lang.String,java.lang.Object> getProveedorJson(
    public @ResponseBody HashMap<String,ArrayList<HashMap<String, String>>> getSubFamiliasByFamProdJson(
            @RequestParam(value="fam", required=true) String familia_id,
            @RequestParam(value="iu", required=true) String id_user_cod,
            Model model
            ) {
        
        log.log(Level.INFO, "Ejecutando getSubFamiliasByFamProdJson de {0}", ProductosController.class.getName());
        HashMap<String,ArrayList<HashMap<String, String>>> jsonretorno = new HashMap<String,ArrayList<HashMap<String, String>>>();
        ArrayList<HashMap<String, String>> subfamilias = new ArrayList<HashMap<String, String>>();
        HashMap<String, String> userDat = new HashMap<String, String>();
        
        Integer id_usuario = Integer.parseInt(Base64Coder.decodeString(id_user_cod));
        userDat = this.getHomeDao().getUserById(id_usuario);
        
        Integer id_empresa = Integer.parseInt(userDat.get("empresa_id"));
        
        subfamilias = this.getInvDao().getProducto_Subfamilias(id_empresa,familia_id );
        
        jsonretorno.put("SubFamilias", subfamilias);
        
        return jsonretorno;
    }
    
    //obtiene los Familias del producto seleccionado
    @RequestMapping(method = RequestMethod.POST, value="/getFamiliasByTipoProd.json")
    public @ResponseBody HashMap<String,ArrayList<HashMap<String, String>>> getFamiliasByTipoProdJson(
            @RequestParam(value="tipo_prod", required=true) String tipo_prod,
            @RequestParam(value="iu", required=true) String id_user_cod,
            Model model
            ) {
        
        log.log(Level.INFO, "Ejecutando getFamiliasByTipoProdJson de {0}", ProductosController.class.getName());
        HashMap<String,ArrayList<HashMap<String, String>>> jsonretorno = new HashMap<String,ArrayList<HashMap<String, String>>>();
        ArrayList<HashMap<String, String>> familias = new ArrayList<HashMap<String, String>>();
        HashMap<String, String> userDat = new HashMap<String, String>();
        
        Integer id_usuario = Integer.parseInt(Base64Coder.decodeString(id_user_cod));
        userDat = this.getHomeDao().getUserById(id_usuario);
        
        Integer id_empresa = Integer.parseInt(userDat.get("empresa_id"));
        
        familias = this.getInvDao().getInvProdSubFamiliasByTipoProd(id_empresa, tipo_prod);
        
        jsonretorno.put("Familias", familias);
        
        return jsonretorno;
    }
    
    //Buscador de de productos
    @RequestMapping(method = RequestMethod.POST, value="/get_buscador_productos_ingredientes.json")
    public @ResponseBody HashMap<String,ArrayList<HashMap<String, String>>> getBuscadorProductosIngredientesJson(
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
    
    
    
    
    
    
    //Busca un sku en especifico
    @RequestMapping(method = RequestMethod.POST, value="/get_busca_sku_prod.json")
    public @ResponseBody HashMap<String,ArrayList<HashMap<String, String>>> getBuscaSkuProdJson(
            @RequestParam(value="sku", required=true) String sku,
            @RequestParam(value="iu", required=true) String id_user,
            Model model
            ) {
        System.out.println("Busqueda de sku");
        Integer id_usuario = Integer.parseInt(Base64Coder.decodeString(id_user));
        
        HashMap<String,ArrayList<HashMap<String, String>>> jsonretorno = new HashMap<String,ArrayList<HashMap<String, String>>>();
        jsonretorno.put("Sku", this.getInvDao().getProducto_sku(sku.toUpperCase(),id_usuario));
        
        return jsonretorno;
    }
    
    
    
    //Buscador de clientes
    @RequestMapping(method = RequestMethod.POST, value="/getBuscadorClientes.json")
    public @ResponseBody HashMap<String,ArrayList<HashMap<String, Object>>> getBuscadorClientesJson(
            @RequestParam(value="cadena", required=true) String cadena,
            @RequestParam(value="filtro", required=true) Integer filtro,
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
        
        
        jsonretorno.put("Clientes", this.getInvDao().getBuscadorClientes(cadena,filtro,id_empresa, id_sucursal));
        
        return jsonretorno;
    }
    
    
    
    //metodo para el Buscador de Cuentas Contables
    @RequestMapping(method = RequestMethod.POST, value="/getBuscadorCuentasContables.json")
    public @ResponseBody HashMap<String,ArrayList<HashMap<String, String>>> getBuscadorCuentasContablesJson(
            @RequestParam(value="cta_mayor", required=true) Integer cta_mayor,
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
        
        log.log(Level.INFO, "Ejecutando getBuscadorCuentasContablesJson de {0}", ProductosController.class.getName());
        HashMap<String,ArrayList<HashMap<String, String>>> jsonretorno = new HashMap<String,ArrayList<HashMap<String, String>>>();
        HashMap<String, String> userDat = new HashMap<String, String>();
        ArrayList<HashMap<String, String>> cuentasContables = new ArrayList<HashMap<String, String>>();
        
        //decodificar id de usuario
        Integer id_usuario = Integer.parseInt(Base64Coder.decodeString(id_user));
        userDat = this.getHomeDao().getUserById(id_usuario);
        Integer id_empresa = Integer.parseInt(userDat.get("empresa_id"));
        
        System.out.println("cta_mayor: "+cta_mayor);
        
        cuentasContables = this.getInvDao().getProducto_CuentasContables(cta_mayor, detalle, clasifica, cta, scta, sscta, ssscta, sssscta, descripcion, id_empresa);
        
        jsonretorno.put("CtaContables", cuentasContables);
        
        return jsonretorno;
    }
    
    
    
    
    //crear y editar un producto
    @RequestMapping(method = RequestMethod.POST, value="/edit.json")
    public @ResponseBody HashMap<String, String> editJson(
            @RequestParam(value="id_producto", required=true) String id_producto,
            @RequestParam(value="codigo", required=true) String codigo,
            @RequestParam(value="descripcion", required=true) String descripcion,
            @RequestParam(value="codigo_barras", required=true) String codigo_barras,
            @RequestParam(value="tentrega", required=false) String tentrega,
            @RequestParam(value="select_clase", required=false) String select_clase,
            @RequestParam(value="select_clasifstock", required=false) String select_clasifstock,
            @RequestParam(value="select_estatus", required=false) String select_estatus,
            @RequestParam(value="select_familia", required=false) String select_familia,
            @RequestParam(value="select_subfamilia", required=false) String select_subfamilia,
            @RequestParam(value="select_grupo", required=false) String select_grupo,
            @RequestParam(value="select_ieps", required=false) String select_ieps,
            @RequestParam(value="select_iva", required=false) String select_iva,
            @RequestParam(value="select_retencion", required=false) String select_retencion,
            @RequestParam(value="select_moneda", required=false) String select_moneda,
            @RequestParam(value="select_linea", required=false) String select_linea,
            @RequestParam(value="select_marca", required=false) String select_marca,
            @RequestParam(value="select_prodtipo", required=false) String select_prodtipo,
            @RequestParam(value="select_seccion", required=false) String select_seccion,
            @RequestParam(value="select_unidad", required=false) String select_unidad,
            @RequestParam(value="check_nolote", required=false) String check_nolote,
            @RequestParam(value="check_nom", required=false) String check_nom,
            @RequestParam(value="check_noserie", required=false) String check_noserie,
            @RequestParam(value="check_pedimento", required=false) String check_pedimento,
            @RequestParam(value="check_stock", required=false) String check_stock,
            @RequestParam(value="check_ventaext", required=false) String check_ventaext,
            @RequestParam(value="check_compraext", required=false) String check_compraext,
            @RequestParam(value="pres_on", required=true) String pres_on,
            @RequestParam(value="select_pres_default", required=false) String select_pres_default,
            @RequestParam(value="densidad", required=false) String densidad,
            @RequestParam(value="valor_maximo", required=false) String valor_maximo,
            @RequestParam(value="valor_minimo", required=false) String valor_minimo,
            @RequestParam(value="punto_reorden", required=false) String punto_reorden,
            @RequestParam(value="eliminar", required=false) String[] id_prod_formula,
            @RequestParam(value="porcentaje_grid", required=false) String[] porcentaje,
            @RequestParam(value="id_proveedor", required=true) String id_proveedor,
            @RequestParam(value="id_cta_gasto", required=true) String id_cta_gasto,
            @RequestParam(value="id_cta_costvent", required=true) String id_cta_costoventa,
            @RequestParam(value="id_cta_vent", required=true) String id_cta_venta,
            @RequestParam(value="nameimg", required=true) String nameimg,
            @RequestParam(value="namepdf", required=true) String namepdf,
            @RequestParam(value="descripcion_corta", required=true) String descripcion_corta,
            @RequestParam(value="descripcion_larga", required=true) String descripcion_larga,
            @RequestParam(value="edito_pdf", required=true) String edito_pdf,
            @RequestParam(value="edito_imagen", required=true) String edito_imagen,
            @RequestParam(value="check_flete", required=false) String check_flete,
            @RequestParam(value="no_clie", required=true) String no_clie,
            @RequestParam(value="clave_cfdi_claveprodserv", required=true) String clave_cfdi_claveprodserv,            
            Model model,@ModelAttribute("user") UserSessionData user
        ) {
            
            System.out.println("*** Entrando a Productos editJson ****");
            System.out.println("clave_cfdi_claveprodserv="+clave_cfdi_claveprodserv);
            HashMap<String, String> jsonretorno = new HashMap<String, String>();
            HashMap<String, String> succes = new HashMap<String, String>();
            HashMap<String, String> userDat = new HashMap<String, String>();
            Integer app_selected = 8;
            String command_selected = "new";
            Integer id_usuario= user.getUserId();//variable para el id  del usuario
            String incluye_mod_prod="";
            String arreglo[];
            String extra_data_array = null;
            
            codigo = codigo.toUpperCase();
            
            userDat = this.getHomeDao().getUserById(id_usuario);
            incluye_mod_prod = userDat.get("incluye_produccion");
            Integer id_empresa = Integer.parseInt(userDat.get("empresa_id"));
            Integer prodTipo=Integer.parseInt(select_prodtipo);
            //tipos 2=Subensamble, 3=Kit
            if(incluye_mod_prod.equals("true")){
                if(prodTipo==3 ){
                    arreglo = new String[id_prod_formula.length];
                    for(int i=0; i<id_prod_formula.length; i++) { 
                        arreglo[i]= "'"+id_prod_formula[i] +"___" + porcentaje[i] +"'";
                    }
                    //serializar el arreglo
                    extra_data_array = StringUtils.join(arreglo, ",");
                }else{
                    extra_data_array = "'sin datos'";
                }
            }else{
                if(prodTipo==1 || prodTipo==2 || prodTipo==3 || prodTipo==8){
                    arreglo = new String[id_prod_formula.length];
                    for(int i=0; i<id_prod_formula.length; i++) { 
                        arreglo[i]= "'"+id_prod_formula[i] +"___" + porcentaje[i] +"'";
                    }
                    //serializar el arreglo
                    extra_data_array = StringUtils.join(arreglo, ",");
                }else{
                    extra_data_array = "'sin datos'";
                }
            }
            
            //Quitar las comillas simples de la cadena
            descripcion = descripcion.replaceAll("'", "\"");
            descripcion_larga = descripcion_larga.replaceAll("'", "\"");
            descripcion_corta = descripcion_corta.replaceAll("'", "\"");
            
            //Si los campos select vienen null les asigna un 0(cero)
            tentrega = StringHelper.verificarSelect(tentrega);
            select_clase = StringHelper.verificarSelect(select_clase);
            select_clasifstock = StringHelper.verificarSelect(select_clasifstock);
            select_estatus = StringHelper.verificarSelect(select_estatus);
            select_familia = StringHelper.verificarSelect(select_familia);
            select_subfamilia = StringHelper.verificarSelect(select_subfamilia);
            select_grupo = StringHelper.verificarSelect(select_grupo);
            select_ieps = StringHelper.verificarSelect(select_ieps);
            select_iva = StringHelper.verificarSelect(select_iva);
            select_moneda = StringHelper.verificarSelect(select_moneda);
            select_linea = StringHelper.verificarSelect(select_linea);
            select_marca = StringHelper.verificarSelect(select_marca);
            select_prodtipo = StringHelper.verificarSelect(select_prodtipo);
            select_seccion = StringHelper.verificarSelect(select_seccion);
            select_unidad = StringHelper.verificarSelect(select_unidad);
            densidad = StringHelper.verificarSelect(densidad);
            valor_maximo = StringHelper.verificarSelect(valor_maximo);
            valor_minimo = StringHelper.verificarSelect(valor_minimo);
            punto_reorden = StringHelper.verificarSelect(punto_reorden);
            select_pres_default = StringHelper.verificarSelect(select_pres_default);
            
            //Verifica los campos CheckBox y les asigna true o false
            check_nolote = StringHelper.verificarCheckBox(check_nolote);
            check_nom = StringHelper.verificarCheckBox(check_nom);
            check_noserie = StringHelper.verificarCheckBox(check_noserie);
            check_pedimento = StringHelper.verificarCheckBox(check_pedimento);
            check_stock = StringHelper.verificarCheckBox(check_stock);
            check_ventaext = StringHelper.verificarCheckBox(check_ventaext);
            check_compraext = StringHelper.verificarCheckBox(check_compraext);
            check_flete = StringHelper.verificarCheckBox(check_flete);
            
            if( id_producto.equals("0") ){
                command_selected = "new";
            }else{
                command_selected = "edit";
            }
            
            String data_string = 
                    app_selected+"___"+//1
                    command_selected+"___"+//2
                    id_usuario+"___"+//3
                    id_producto+"___"+//4
                    descripcion.toUpperCase()+"___"+//5
                    codigo_barras+"___"+//6
                    tentrega+"___"+//7
                    select_clase+"___"+//8
                    select_clasifstock+"___"+//9
                    select_estatus+"___"+//10
                    select_familia+"___"+//11
                    select_subfamilia+"___"+//12
                    select_grupo+"___"+//13
                    select_ieps+"___"+//14
                    select_iva+"___"+//15
                    select_linea+"___"+//16
                    select_marca+"___"+//17
                    select_prodtipo+"___"+//18
                    select_seccion+"___"+//19
                    select_unidad+"___"+//20
                    check_nolote+"___"+//21
                    check_nom+"___"+//22
                    check_noserie+"___"+//23
                    check_pedimento+"___"+//24
                    check_stock+"___"+//25
                    check_ventaext+"___"+//26
                    check_compraext+"___"+//27
                    pres_on+"___"+//28
                    id_proveedor+"___"+//29
                    densidad+"___"+//30
                    codigo+"___"+//31
                    valor_maximo+"___"+//32
                    valor_minimo+"___"+//33
                    punto_reorden+"___"+//34
                    id_cta_gasto+"___"+//35
                    id_cta_costoventa+"___"+//36
                    id_cta_venta+"___"+//37
                    nameimg+"___"+//38
                    namepdf+"___"+//39
                    descripcion_corta+"___"+//40
                    descripcion_larga+"___"+//41
                    select_pres_default+"___"+//42
                    check_flete+"___"+//43
                    no_clie+"___"+//44
                    select_moneda+"___"+//45
                    select_retencion+"___"+//46
                    clave_cfdi_claveprodserv;//47
            succes = this.getInvDao().selectFunctionValidateAaplicativo(data_string,app_selected,extra_data_array);
            
            String actualizo = "0";
            
            if( String.valueOf(succes.get("success")).equals("true") ){
                actualizo = this.getInvDao().selectFunctionForThisApp(data_string, extra_data_array);
                
                //mover los archivos de tmp a resources
                if(edito_imagen.equals("1")){
                    try {
                        FileHelper.move(this.getGralDao().getJvmTmpDir()+"/"+nameimg, this.getGralDao().getProdImgDir()+this.getGralDao().getRfcEmpresaEmisora(id_empresa)+"/" +nameimg);
                    } catch (IOException ex) {
                        Logger.getLogger(ProductosController.class.getName()).log(Level.SEVERE, null, ex);
                    } catch (Exception ex) {
                        Logger.getLogger(ProductosController.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
                
                if(edito_pdf.equals("1")){
                    try {
                        FileHelper.move(this.getGralDao().getJvmTmpDir()+"/"+namepdf, this.getGralDao().getProdPdfDir()+this.getGralDao().getRfcEmpresaEmisora(id_empresa)+"/" +namepdf);
                    } catch (IOException ex) {
                        Logger.getLogger(ProductosController.class.getName()).log(Level.SEVERE, null, ex);
                    } catch (Exception ex) {
                        Logger.getLogger(ProductosController.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
            
            jsonretorno.put("success",String.valueOf(succes.get("success")));
            
            log.log(Level.INFO, "Salida json {0}", String.valueOf(jsonretorno.get("success")));
        return jsonretorno;
    }
    
    
    
    
    //cambiar a borrado logico un registro
    @RequestMapping(method = RequestMethod.POST, value="/logicDelete.json")
    public @ResponseBody HashMap<String, String> logicDeleteJson(
            @RequestParam(value="id_producto", required=true) Integer id_producto,
            @RequestParam(value="iu", required=true) String id_user,
            Model model
            ) {
        
        System.out.println("Borrado logico de producto");
        //decodificar id de usuario
        Integer id_usuario = Integer.parseInt(Base64Coder.decodeString(id_user));
        
        Integer app_selected = 8;
        String command_selected = "delete";
        String extra_data_array = "'sin datos'";
        
        String data_string = app_selected+"___"+command_selected+"___"+id_usuario+"___"+id_producto;
        
        HashMap<String, String> jsonretorno = new HashMap<String, String>();
        
        jsonretorno.put("success",String.valueOf( this.getInvDao().selectFunctionForThisApp(data_string,extra_data_array)) );
        
        return jsonretorno;
    }
    
    
    //para subir el archivo a la carpeta temporal de java
    @RequestMapping(method = RequestMethod.POST, value="/fileUpload.json")
    public @ResponseBody HashMap<String, String> fileUploadJson(
            @RequestParam(value="file", required=true) MultipartFile uploadImg,
            Model model
            ) throws IOException {
        
        //System.out.println("fileUpload:");
        
        HashMap<String, String> jsonretorno = new HashMap<String, String>();
        
        if (!uploadImg.isEmpty()) {
            byte[] bytes = uploadImg.getBytes();
            
            System.out.println("FileHelper:");
            jsonretorno.put("success",FileHelper.saveByteFile(bytes, this.getGralDao().getJvmTmpDir()+"/"+uploadImg.getOriginalFilename()));
            //jsonretorno.put("success",FileHelper.saveByteFile(bytes, uploadImg.getOriginalFilename()));
            
            //log.log(Level.INFO, "Test upload {0}", uploadImg.getOriginalFilename());
            //String ul_img = FileHelper.saveByteFile(bytes, "/tmp/"+uploadImg.getOriginalFilename());
            String ul_img = uploadImg.getOriginalFilename();
            //System.out.println("getJvmTmpDir:"+this.getGralDao().getJvmTmpDir());
            
            /*
            ImgHelper img = new ImgHelper();
            img.procesaImg(ul_img, uploadImg.getOriginalFilename(),osv.getTmpDirOs());
            */
            //img.procesaImg(ul_img, uploadImg.getOriginalFilename(),"/tmp/");
            
            jsonretorno.put("url",ul_img);
            
            jsonretorno.put("success","true");
            
        } else {
            log.log(Level.INFO, "Test upload {0}", "uploadFailure");
            jsonretorno.put("url","no");
            jsonretorno.put("success","false");
        }
        
        return jsonretorno;
    }
    
    @RequestMapping(method = RequestMethod.GET, value = "/prodserv_suggestions")
    public @ResponseBody ArrayList<String[]> getClaveProdServSuggestions(
        @RequestParam(value="search_term", required=true) String searchTerm) {

        ArrayList<String[]> arr = this.getInvDao().getClaveProdServSuggestions(searchTerm);
        return arr;
    }

    //descargtar imagen
    @RequestMapping(method = RequestMethod.GET, value="/imgDownloadImg/{name_img}/{id}/{iu}/out.json")
    public @ResponseBody HashMap<String, String> imgDownloadImgJson(@PathVariable("name_img") String name_img,
        @PathVariable("id") String id,
        @PathVariable("iu") String id_user,
        HttpServletResponse response, 
        Model model) throws IOException {
        ServletOutputStream out;
        
        HashMap<String, String> userDat = new HashMap<String, String>();
        
        //decodificar id de usuario
        Integer id_usuario = Integer.parseInt(Base64Coder.decodeString(id_user));
        userDat = this.getHomeDao().getUserById(id_usuario);
        Integer id_empresa = Integer.parseInt(userDat.get("empresa_id"));
        String rfc_empresa=this.getGralDao().getRfcEmpresaEmisora(id_empresa);
        
        String varDir = "";
        if(id.equals("0")){
            varDir = this.getGralDao().getJvmTmpDir();
        }else{
            varDir = this.getGralDao().getProdImgDir()+rfc_empresa;
        }
        
        File file = new File(varDir+"/"+name_img);
        
        byte[] fichero = FileHelper.BytesFromFile(file);
        response.setContentType ("application/png");
        response.setHeader ( "Content-disposition", "inline; filename=" + name_img );
        response.setHeader ( "Cache-Control", "max-age=30" );
        response.setHeader ( "Pragma", "No-cache" );
        response.setDateHeader ("Expires", 0);
        response.setContentLength (fichero.length);
        out = response.getOutputStream ();
        out.write (fichero, 0, fichero.length);
        out.flush ();
        out.close ();
        
        return null;
    }
    
    //descargtar pdf
    @RequestMapping(method = RequestMethod.GET, value="/imgDownloadPdf/{id}/{name_img}/out.json")
    public @ResponseBody HashMap<String, String> imgDownloadPdfJson(@PathVariable("name_img") String name_img,
        @PathVariable("id") String id,
        HttpServletResponse response, 
        Model model) throws IOException {
        ServletOutputStream out;
        
        String varDir = "";
        if(id.equals("0")){
            varDir = this.getGralDao().getJvmTmpDir();
        }else{
            varDir = this.getGralDao().getJvmTmpDir();
        }
        
        File file = new File(varDir+"/"+name_img);
        
        byte[] fichero = FileHelper.BytesFromFile(file);
        response.setContentType ("application/pdf");
        response.setHeader ( "Content-disposition", "inline; filename=" + name_img );
        response.setHeader ( "Cache-Control", "max-age=30" );
        response.setHeader ( "Pragma", "No-cache" );
        response.setDateHeader ("Expires", 0);
        response.setContentLength (fichero.length);
        out = response.getOutputStream ();
        out.write (fichero, 0, fichero.length);
        out.flush ();
        out.close ();
        
        return null;
    }
    
}
