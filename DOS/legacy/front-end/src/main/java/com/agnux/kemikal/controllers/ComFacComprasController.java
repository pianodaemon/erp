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
import com.agnux.kemikal.reportes.pdfEntradas;
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
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;


@Controller
@SessionAttributes({"user"})
@RequestMapping("/entradamercancias/")
public class ComFacComprasController {
    private static final Logger log  = Logger.getLogger(ComFacComprasController.class.getName());
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
        
        log.log(Level.INFO, "Ejecutando starUp de {0}", ComFacComprasController.class.getName());
        LinkedHashMap<String,String> infoConstruccionTabla = new LinkedHashMap<String,String>();
        
        //infoConstruccionTabla.put("id", "Acciones:90");
        infoConstruccionTabla.put("id", "Acciones:70");
        infoConstruccionTabla.put("folio", "Folio entrada:90");
        infoConstruccionTabla.put("fecha_entrada","Fecha Entrada:100");
        infoConstruccionTabla.put("orden_compra", "Orden Compra:110");
        infoConstruccionTabla.put("proveedor", "Proveedor:250");
        infoConstruccionTabla.put("tipo_doc", "Tipo Doc.:100");
        infoConstruccionTabla.put("factura", "Folio Doc.:100");
        infoConstruccionTabla.put("fecha_factura","Fecha Doc.:100");
        infoConstruccionTabla.put("estado","Estado:90");
        
        ModelAndView x = new ModelAndView("entradamercancias/startup", "title", "Facturas Compras");
        
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
    
    
    
    
    @RequestMapping(value="/get_all_entradas.json", method = RequestMethod.POST)
    public @ResponseBody HashMap<String,ArrayList<HashMap<String, Object>>> getEntradasJson(
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
        
        //aplicativo Entradas de mercancias
        Integer app_selected = 9;
        
        //decodificar id de usuario
        Integer id_usuario = Integer.parseInt(Base64Coder.decodeString(id_user_cod));
        
        //variables para el buscador
        String tipoDoc = ""+StringHelper.isNullString(String.valueOf(has_busqueda.get("tipo_doc")))+"";
        String folio = "%"+StringHelper.isNullString(String.valueOf(has_busqueda.get("folio")))+"%";
        String orden_compra = "%"+StringHelper.isNullString(String.valueOf(has_busqueda.get("orden_compra")))+"%";
        String factura = "%"+StringHelper.isNullString(String.valueOf(has_busqueda.get("factura")))+"%";
        String proveedor = "%"+StringHelper.isNullString(String.valueOf(has_busqueda.get("proveedor")))+"%";
        String codigo = "%"+StringHelper.isNullString(String.valueOf(has_busqueda.get("codigo")))+"%";
        String producto = "%"+StringHelper.isNullString(String.valueOf(has_busqueda.get("producto")))+"%";
        
        
        String data_string = app_selected+"___"+id_usuario+"___"+folio+"___"+orden_compra+"___"+factura+"___"+proveedor+"___"+tipoDoc+"___"+codigo+"___"+producto;
        
        //obtiene total de registros en base de datos, con los parametros de busqueda
        int total_items = this.getInvDao().countAll(data_string);
        
        //calcula el total de paginas
        int total_pags = resource.calculaTotalPag(total_items,items_por_pag);
        
        //variables que necesita el datagrid, para no tener que hacer uno por cada aplicativo
        DataPost dataforpos = new DataPost(orderby, desc, items_por_pag, pag_start, display_pag, input_json, cadena_busqueda,total_items,total_pags,id_user_cod);
        
        int offset = resource.__get_inicio_offset(items_por_pag, pag_start);
        
        //obtiene los registros para el grid, de acuerdo a los parametros de busqueda
        jsonretorno.put("Data", this.getInvDao().getEntradas_PaginaGrid(data_string, offset, items_por_pag, orderby, desc));
        
        //obtiene el hash para los datos que necesita el datagrid
        jsonretorno.put("DataForGrid", dataforpos.formaHashForPos(dataforpos));
        
        return jsonretorno;
    }
    
    
    
    
    
    //obtiene lineas de producto y datos para el buscador
    @RequestMapping(method = RequestMethod.POST, value="/getProductoTipos.json")
    public @ResponseBody HashMap<String,ArrayList<HashMap<String, String>>> getProductoTiposJson(
            @RequestParam(value="iu", required=true) String id_user_cod,
            Model model
        ) {
        
        HashMap<String,ArrayList<HashMap<String, String>>> jsonretorno = new HashMap<String,ArrayList<HashMap<String, String>>>();
        
        //Decodificar id de usuario
        //Integer id_usuario = Integer.parseInt(Base64Coder.decodeString(id_user_cod));
        
        jsonretorno.put("prodTipos", this.getInvDao().getProducto_Tipos());
        
        return jsonretorno;
    }
    
    
    
    //obtiene datos de la Orden de Compra
    @RequestMapping(method = RequestMethod.POST, value="/getDatosOrdenCompra.json")
    public @ResponseBody HashMap<String,ArrayList<HashMap<String, String>>> getDatosOrdenCompraJson(
            @RequestParam(value="orden_compra", required=true) String orden_compra,
            @RequestParam(value="iu", required=true) String id_user,
            Model model
            ) {
        
        log.log(Level.INFO, "Ejecutando getDatosOrdenCompraJson de {0}", ComFacComprasController.class.getName());
        HashMap<String,ArrayList<HashMap<String, String>>> jsonretorno = new HashMap<String,ArrayList<HashMap<String, String>>>();
        ArrayList<HashMap<String, String>> DatosOC = new ArrayList<HashMap<String, String>>();
        ArrayList<HashMap<String, String>> DetallesOC = new ArrayList<HashMap<String, String>>();
        HashMap<String, String> userDat = new HashMap<String, String>();
        
        //Decodificar id de usuario
        Integer id_usuario = Integer.parseInt(Base64Coder.decodeString(id_user));
        userDat = this.getHomeDao().getUserById(id_usuario);
        
        Integer id_empresa = Integer.parseInt(userDat.get("empresa_id"));
        
        DatosOC = this.getInvDao().getEntradas_DatosOrdenCompra(orden_compra, id_empresa);
        if(DatosOC.size()>0){
            DetallesOC = this.getInvDao().getEntradas_DetallesOrdenCompra(Integer.parseInt(DatosOC.get(0).get("id")));
        }
        
        jsonretorno.put("DatosOC", DatosOC);
        jsonretorno.put("DetallesOC", DetallesOC);
        
        return jsonretorno;
    }
    
    
    
    
    
    
    
    
    @RequestMapping(method = RequestMethod.POST, value="/get_datos_entrada_mercancia.json")
    public @ResponseBody HashMap<String,Object> get_datos_entrada_mercanciaJson(
            @RequestParam(value="id_entrada", required=true) Integer id,
            @RequestParam(value="iu", required=true) String id_user,
            Model model
        ) {
        
        log.log(Level.INFO, "Ejecutando get_datos_entrada_mercanciaJson de {0}", ComFacComprasController.class.getName());
        HashMap<String,Object> jsonretorno = new HashMap<String,Object>();
        ArrayList<HashMap<String, String>> datosEntrada = new ArrayList<HashMap<String, String>>();
        ArrayList<HashMap<String, String>> datosProveedor = new ArrayList<HashMap<String, String>>();
        ArrayList<HashMap<String, String>> datosGrid = new ArrayList<HashMap<String, String>>();
        HashMap<String, String> userDat = new HashMap<String, String>();
        HashMap<String, String> comPar = new HashMap<String, String>();
        HashMap<String, String> extra = new HashMap<String, String>();
        
        //Decodificar id de usuario
        Integer id_usuario = Integer.parseInt(Base64Coder.decodeString(id_user));
        userDat = this.getHomeDao().getUserById(id_usuario);
        Integer id_empresa = Integer.parseInt(userDat.get("empresa_id"));
        Integer id_sucursal = Integer.parseInt(userDat.get("sucursal_id"));
        comPar = this.getInvDao().getCom_Par(id_empresa, id_sucursal);
        
        extra.put("mostrar_lab", comPar.get("mostrar_lab"));
        extra.put("texto_lab", comPar.get("texto_lab"));
        
        if( id != 0 ){
            datosEntrada = this.getInvDao().getEntrada_Datos(id);
            datosProveedor = this.getInvDao().getEntradas_DatosProveedor(Integer.parseInt(datosEntrada.get(0).get("proveedor_id").toString()));
            datosGrid = this.getInvDao().getEntradas_DatosGrid(id);
        }
        //valorIva= this.getCdao().getValoriva();
        
        jsonretorno.put("datosGrid", datosGrid);
        jsonretorno.put("datosEntrada", datosEntrada);
        jsonretorno.put("datosProveedor", datosProveedor);
        jsonretorno.put("Monedas", this.getInvDao().getMonedas());
        jsonretorno.put("Impuestos", this.getInvDao().getEntradas_Impuestos());
        jsonretorno.put("Ieps", this.getInvDao().getIeps(id_empresa, 0));
        jsonretorno.put("tasaFletes", this.getInvDao().getEntradas_TasaFletes());
        jsonretorno.put("Fleteras", this.getInvDao().geteEntradas_Fleteras(id_empresa,id_sucursal));
        jsonretorno.put("Almacenes", this.getInvDao().getAlmacenes2(id_empresa));
        jsonretorno.put("Extra", extra);
        
        return jsonretorno;
    }
    

    //obtiene los proveedores para el buscador
    @RequestMapping(method = RequestMethod.POST, value="/get_proveedores.json")
    public @ResponseBody HashMap<String,ArrayList<HashMap<String, String>>> getProveedoresJson(
            @RequestParam(value="rfc", required=true) String rfc,
            @RequestParam(value="no_prov", required=true) String no_prov,
            @RequestParam(value="nombre", required=true) String nombre,
            @RequestParam(value="iu", required=true) String id_user,
            Model model
            ) {
        
        log.log(Level.INFO, "Ejecutando getProveedoresJson de {0}", ComFacComprasController.class.getName());
        HashMap<String,ArrayList<HashMap<String, String>>> jsonretorno = new HashMap<String,ArrayList<HashMap<String, String>>>();
        ArrayList<HashMap<String, String>> proveedores = new ArrayList<HashMap<String, String>>();
        HashMap<String, String> userDat = new HashMap<String, String>();
        //decodificar id de usuario
        Integer id_usuario = Integer.parseInt(Base64Coder.decodeString(id_user));
        userDat = this.getHomeDao().getUserById(id_usuario);
        
        Integer id_empresa = Integer.parseInt(userDat.get("empresa_id"));
        
        proveedores = this.getInvDao().getBuscadorProveedores(rfc, no_prov, nombre,id_empresa);
        
        jsonretorno.put("proveedores", proveedores);
        
        return jsonretorno;
    }
    
    
    
    //Obtener datos del Proveedor a partir del Numero de Control
    @RequestMapping(method = RequestMethod.POST, value="/getDataByNoProv.json")
    public @ResponseBody HashMap<String,ArrayList<HashMap<String, String>>> getDataByNoProvJson(
            @RequestParam(value="no_prov", required=true) String no_prov,
            @RequestParam(value="iu", required=true) String id_user,
            Model model
        ) {
        
        HashMap<String,ArrayList<HashMap<String, String>>> jsonretorno = new HashMap<String,ArrayList<HashMap<String, String>>>();
        HashMap<String, String> userDat = new HashMap<String, String>();
       
        //decodificar id de usuario
        Integer id_usuario = Integer.parseInt(Base64Coder.decodeString(id_user));
        userDat = this.getHomeDao().getUserById(id_usuario);
        Integer id_empresa = Integer.parseInt(userDat.get("empresa_id"));
        //Integer id_sucursal = Integer.parseInt(userDat.get("sucursal_id"));
        
        jsonretorno.put("Proveedor", this.getInvDao().getDatosProveedorByNoProv(no_prov, id_empresa));
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
        
        log.log(Level.INFO, "Ejecutando getProductosJson de {0}", ComFacComprasController.class.getName());
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
    
    
    
    
    //Obtiene los presentaciones del producto seleccionado
    @RequestMapping(method = RequestMethod.POST, value="/get_presentaciones_producto.json")
    public @ResponseBody HashMap<String,ArrayList<HashMap<String, String>>> getPresentacionesproductoJson(
            @RequestParam(value="sku", required=true) String sku,
            @RequestParam(value="iu", required=true) String id_user,
            Model model
        ) {
        
        log.log(Level.INFO, "Ejecutando getPresentacionesproductoJson de {0}", ComFacComprasController.class.getName());
        HashMap<String,ArrayList<HashMap<String, String>>> jsonretorno = new HashMap<String,ArrayList<HashMap<String, String>>>();
        ArrayList<HashMap<String, String>> Presentaciones = new ArrayList<HashMap<String, String>>();
        HashMap<String, String> userDat = new HashMap<String, String>();
        Integer id_usuario = Integer.parseInt(Base64Coder.decodeString(id_user));
        userDat = this.getHomeDao().getUserById(id_usuario);
        
        Integer id_empresa = Integer.parseInt(userDat.get("empresa_id"));
        
        Presentaciones = this.getInvDao().getEntradas_PresentacionesProducto(sku, id_empresa);
        
        jsonretorno.put("Presentaciones", Presentaciones);
        
        return jsonretorno;
    }
    
    
    
    
    //obtiene los productos para el buscador
    @RequestMapping(method = RequestMethod.POST, value="/obtener_tipo_cambio.json")
    public @ResponseBody HashMap<String,ArrayList<HashMap<String, String>>> getTipocambioJson(
            @RequestParam(value="fecha", required=true) String fecha,
            Model model
            ) {
        
        log.log(Level.INFO, "Ejecutando getTipocambioJson de {0}", ComFacComprasController.class.getName());
        HashMap<String,ArrayList<HashMap<String, String>>> jsonretorno = new HashMap<String,ArrayList<HashMap<String, String>>>();
        ArrayList<HashMap<String, String>> tc = new ArrayList<HashMap<String, String>>();
        
        tc = this.getInvDao().getEntradas_TipoCambio(fecha);
        
        jsonretorno.put("tipoCambio", tc);
        
        return jsonretorno;
    }
    
    
    
    //edicion y nuevo
    @RequestMapping(method = RequestMethod.POST, value="/edit.json")
    public @ResponseBody HashMap<String, String> editJson(
            @RequestParam(value="id_entrada", required=true) Integer id_entrada,
            @RequestParam(value="id_proveedor", required=true) String id_proveedor,
            @RequestParam(value="factura", required=true) String factura,
            @RequestParam(value="expedicion", required=true) String expedicion,
            @RequestParam(value="numeroguia", required=true) String numeroguia,
            @RequestParam(value="ordencompra", required=true) String ordencompra,
            @RequestParam(value="denominacion", required=true) String denominacion,
            @RequestParam(value="total_tr", required=true) Integer total_tr,
            @RequestParam(value="tc", required=true) String tc,
            @RequestParam(value="observaciones", required=true) String observaciones,
            @RequestParam(value="fletera", required=true) String fletera_id,
            @RequestParam(value="almacen_destino", required=true) String almacen_destino,
            @RequestParam(value="tipodoc", required=true) String tipo_documento,
            @RequestParam(value="flete", required=true) String flete,
            @RequestParam(value="check_lab", required=false) String check_lab,
            
            @RequestParam(value="cantidad", required=true) String[] cantidad,
            @RequestParam(value="costo", required=true) String[] costo,
            @RequestParam(value="id_prod_grid", required=true) String[] id_prod_grid,
            @RequestParam(value="impuesto", required=true) String[] impuesto,
            @RequestParam(value="valorimp", required=true) String[] valor_imp,
            @RequestParam(value="id_pres", required=true) String[] id_pres,
            @RequestParam(value="eliminado", required=true) String[] eliminado,
            @RequestParam(value="select_ieps", required=true) String[] id_ieps,
            @RequestParam(value="valorieps", required=true) String[] tasa_eps,
            @RequestParam(value="iddetoc", required=true) String[] iddetoc,
            @RequestParam(value="nooc", required=true) String[] nooc,
            
            //@RequestParam(value="lote", required=true) String[] lote,
            //@RequestParam(value="pedimento", required=true) String[] pedimento,
            //@RequestParam(value="caducidad", required=true) String[] caducidad,
            @ModelAttribute("user") UserSessionData user,
            Model model
        ) {
            
            HashMap<String, String> jsonretorno = new HashMap<String, String>();
            HashMap<String, String> succes = new HashMap<String, String>();
            int no_partida = 0;
            String extra_data_array = null;
            String arreglo[];
            arreglo = new String[eliminado.length];
            String actualizo = "0";
            
            Integer app_selected = 9;
            String command_selected = "new";
            Integer id_usuario= user.getUserId();//variable para el id  del usuario
            String numeros_oc = "";
            String oc_actual = "";
            int primer_oc=0;
            
            if(total_tr > 0){
                for(int i=0; i<eliminado.length; i++) {
                    if(Integer.parseInt(eliminado[i]) != 0){
                        no_partida++;//si no esta eliminado incrementa el contador de partidas
                        
                        if(!oc_actual.equals(nooc[i])){
                            oc_actual = nooc[i];
                            if(primer_oc==0){
                                numeros_oc = numeros_oc + nooc[i];
                            }else{
                                numeros_oc = numeros_oc + "," + nooc[i];
                            }
                            primer_oc++;
                        }
                    }
                    arreglo[i]= "'"+no_partida+"___"+cantidad[i]+"___"+costo[i]+"___"+id_prod_grid[i]+"___"+impuesto[i]+"___"+valor_imp[i]+"___"+id_pres[i]+"___"+eliminado[i]+"___"+id_ieps[i]+"___"+tasa_eps[i]+ "___"+ iddetoc[i] +"'";
                }
                
                if(primer_oc > 1){
                    //Numeros de OC en esta factura
                    ordencompra = numeros_oc;
                }
                
                //serializar el arreglo
                extra_data_array = StringUtils.join(arreglo, ",");
            }else{
                extra_data_array ="'sin datos'";
            }
            
            if( id_entrada==0 ){
                command_selected = "new";
            }else{
                command_selected = "edit";
            }
            
            check_lab = StringHelper.verificarCheckBox(check_lab);
            
            String data_string = app_selected+"___"+command_selected+"___"+id_usuario+"___"+id_entrada+"___"+id_proveedor+"___"+factura.toUpperCase()+"___"+expedicion+"___"+numeroguia+"___"+ordencompra+"___"+denominacion+"___"+tc+"___"+observaciones.toUpperCase()+"___"+fletera_id+"___"+flete+"___"+almacen_destino+"___"+tipo_documento+"___"+check_lab;
            
            //succes = this.getEdao().selectFunctionValidateAaplicativo(data_string,9,string_array);
            succes = this.getInvDao().selectFunctionValidateAaplicativo(data_string,app_selected,extra_data_array);
            
            log.log(Level.INFO, "despues de validacion {0}", String.valueOf(succes.get("success")));
            
            if( String.valueOf(succes.get("success")).equals("true") ){
                actualizo = this.getInvDao().selectFunctionForApp_MovimientosInventario(data_string, extra_data_array);
            }
            
            jsonretorno.put("success",String.valueOf(succes.get("success")));
            
            log.log(Level.INFO, "Salida json {0}", String.valueOf(jsonretorno.get("success")));
        return jsonretorno;
    }
    
    
    
    
    
    //cancelacion de facturas
    @RequestMapping(method = RequestMethod.POST, value="/cancelar_entrada.json")
    public @ResponseBody HashMap<String, String> getCancelarFactura(
            @RequestParam(value="id_entrada", required=true) Integer id_entrada,
            @RequestParam(value="motivo", required=true) String motivo_cancelacion,
            @RequestParam(value="iu", required=true) String id_user,
            Model model
        ) {
        
        HashMap<String, String> jsonretorno = new HashMap<String, String>();
        System.out.println("Cancelacion de entrada de mercancia");
        //decodificar id de usuario
        Integer id_usuario = Integer.parseInt(Base64Coder.decodeString(id_user));
        
        Integer app_selected = 9;
        String command_selected = "cancelacion";
        String extra_data_array = "'sin datos'";
        
        String data_string = app_selected+"___"+command_selected+"___"+id_usuario+"___"+id_entrada+"___"+motivo_cancelacion.toUpperCase();
        
        jsonretorno.put("success",String.valueOf( this.getInvDao().selectFunctionForApp_MovimientosInventario(data_string,extra_data_array)) );
        
        return jsonretorno;
    }
    
    
    @RequestMapping(value = "/get_genera_pdf_entrada/{id_entrada}/{iu}/out.json", method = RequestMethod.GET ) 
    public ModelAndView getGeneraPdfEntradaJson(
                @PathVariable("id_entrada") Integer id_entrada,
                @PathVariable("iu") String id_user,
                HttpServletRequest request, 
                HttpServletResponse response, 
                Model model)
            throws ServletException, IOException, URISyntaxException, DocumentException, Exception {
        
        HashMap<String, String> userDat = new HashMap<String, String>();
        ArrayList<HashMap<String, String>> datosEntrada = new ArrayList<HashMap<String, String>>();
        ArrayList<HashMap<String, String>> datosProveedor = new ArrayList<HashMap<String, String>>();
        ArrayList<HashMap<String, String>> proveedorContactos = new ArrayList<HashMap<String, String>>();
        HashMap<String, String> datos_empresa = new HashMap<String, String>();
        HashMap<String, String> datos_entrada = new HashMap<String, String>();
        HashMap<String, String> datos_proveedor = new HashMap<String, String>();
        ArrayList<HashMap<String, String>> lista_productos = new ArrayList<HashMap<String, String>>();
        
        System.out.println("Generando PDF de Entrada de Mercancia");
        
        //decodificar id de usuario
        Integer id_usuario = Integer.parseInt(Base64Coder.decodeString(id_user));
        
        userDat = this.getHomeDao().getUserById(id_usuario);
        Integer id_empresa = Integer.parseInt(userDat.get("empresa_id"));
        
        
        String razon_social_empresa = this.getGralDao().getRazonSocialEmpresaEmisora(id_empresa);
        
        //obtener el directorio temporal
        String dir_tmp = this.getGralDao().getTmpDir();
        
        
        //String[] array_company = razon_social_empresa.split(" ");
        //String company_name= array_company[0].toLowerCase();
        //String ruta_imagen = this.getGralDao().getImagesDir() +"logo_"+ company_name +".png";
        String rfc_empresa = this.getGralDao().getRfcEmpresaEmisora(id_empresa);
        
        String ruta_imagen = this.getGralDao().getImagesDir()+rfc_empresa+"_logo.png";
        
        File file_dir_tmp = new File(dir_tmp);
        //System.out.println("Directorio temporal: "+file_dir_tmp.getCanonicalPath());
        
        String file_name = "FAC_COM_"+rfc_empresa+".pdf";
        //ruta de archivo de salida
        String fileout = file_dir_tmp +"/"+  file_name;
        
        datos_empresa.put("emp_razon_social", razon_social_empresa);
        datos_empresa.put("emp_rfc", this.getGralDao().getRfcEmpresaEmisora(id_empresa));
        datos_empresa.put("emp_calle", this.getGralDao().getCalleDomicilioFiscalEmpresaEmisora(id_empresa));
        datos_empresa.put("emp_no_exterior", this.getGralDao().getNoExteriorDomicilioFiscalEmpresaEmisora(id_empresa));
        datos_empresa.put("emp_colonia", this.getGralDao().getColoniaDomicilioFiscalEmpresaEmisora(id_empresa));
        datos_empresa.put("emp_pais", this.getGralDao().getPaisDomicilioFiscalEmpresaEmisora(id_empresa));
        datos_empresa.put("emp_estado", this.getGralDao().getEstadoDomicilioFiscalEmpresaEmisora(id_empresa));
        datos_empresa.put("emp_municipio", this.getGralDao().getMunicipioDomicilioFiscalEmpresaEmisora(id_empresa));
        datos_empresa.put("emp_cp", this.getGralDao().getCpDomicilioFiscalEmpresaEmisora(id_empresa));
        
        
        datosEntrada = this.getInvDao().getEntrada_Datos(id_entrada);
        datos_entrada.put("folio_entrada", datosEntrada.get(0).get("no_entrada"));
        datos_entrada.put("fecha_entrada", datosEntrada.get(0).get("fecha_entrada"));
        datos_entrada.put("factura", datosEntrada.get(0).get("factura"));
        datos_entrada.put("fecha_factura", datosEntrada.get(0).get("expedicion"));
        datos_entrada.put("fecha_fac", datosEntrada.get(0).get("fecha_fac"));
        datos_entrada.put("orden_compra", datosEntrada.get(0).get("orden_compra"));
        datos_entrada.put("observaciones", datosEntrada.get(0).get("observaciones"));
        datos_entrada.put("flete", datosEntrada.get(0).get("flete"));
        datos_entrada.put("subtotal", datosEntrada.get(0).get("subtotal"));
        datos_entrada.put("iva", datosEntrada.get(0).get("iva"));
        datos_entrada.put("retencion", datosEntrada.get(0).get("retencion"));
        datos_entrada.put("total", datosEntrada.get(0).get("total"));
        datos_entrada.put("monto_ieps", datosEntrada.get(0).get("monto_ieps"));
        datos_entrada.put("moneda_id", datosEntrada.get(0).get("moneda_id"));
        datos_entrada.put("moneda", datosEntrada.get(0).get("moneda"));
        datos_entrada.put("moneda_abr", datosEntrada.get(0).get("moneda_abr"));
        datos_entrada.put("moneda_simbolo", datosEntrada.get(0).get("moneda_simbolo"));
        
        if(datosEntrada.get(0).get("estado").equals("")){
            datos_entrada.put("estado", "0");
        }else{
            datos_entrada.put("estado", datosEntrada.get(0).get("estado"));
        }
        
        datosProveedor = this.getInvDao().getEntradas_DatosProveedor(Integer.parseInt(datosEntrada.get(0).get("proveedor_id")));
        proveedorContactos = this.getInvDao().getProveedor_Contacto(Integer.parseInt(datosEntrada.get(0).get("proveedor_id")));
        
        datos_proveedor.put("prov_razon_social", datosProveedor.get(0).get("razon_social"));
        datos_proveedor.put("prov_rfc", datosProveedor.get(0).get("rfc"));
        datos_proveedor.put("prov_calle", datosProveedor.get(0).get("calle"));
        datos_proveedor.put("prov_numero", datosProveedor.get(0).get("numero"));
        datos_proveedor.put("prov_colonia", datosProveedor.get(0).get("colonia"));
        datos_proveedor.put("prov_municipio", datosProveedor.get(0).get("municipio"));
        datos_proveedor.put("prov_estado", datosProveedor.get(0).get("estado"));
        datos_proveedor.put("prov_pais", datosProveedor.get(0).get("pais"));
        datos_proveedor.put("prov_cp", datosProveedor.get(0).get("cp"));
        datos_proveedor.put("prov_telefono", datosProveedor.get(0).get("telefono"));
        
        if(proveedorContactos.size()<=0){
            datos_proveedor.put("prov_contacto", "");
        }else{
            datos_proveedor.put("prov_contacto", proveedorContactos.get(0).get("contacto"));
        }
        
        //obtiene el listado de productos para el pdf
        lista_productos = this.getInvDao().getEntradas_DatosGrid(id_entrada);
        
        //instancia a la clase que construye el pdf del reporte de facturas
        pdfEntradas ent = new pdfEntradas(datos_empresa, datos_entrada, datos_proveedor,lista_productos, fileout, ruta_imagen);
        
        System.out.println("Recuperando archivo: " + fileout);
        File file = new File(fileout);
        int size = (int) file.length(); // Tamaño del archivo
        BufferedInputStream bis = new BufferedInputStream(new FileInputStream(file));
        response.setBufferSize(size);
        response.setContentLength(size);
        response.setContentType("application/pdf");
        response.setHeader("Content-Disposition","attachment; filename=\"" + file.getCanonicalPath() +"\"");
        FileCopyUtils.copy(bis, response.getOutputStream());  	
        response.flushBuffer();
        
        FileHelper.delete(fileout);
        return null;
    } 
}
