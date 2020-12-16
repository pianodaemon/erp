package com.agnux.kemikal.controllers;

import com.agnux.cfd.v2.Base64Coder;
import com.agnux.common.helpers.StringHelper;
import com.agnux.common.obj.DataPost;
import com.agnux.common.obj.ResourceProject;
import com.agnux.common.obj.UserSessionData;
import com.agnux.kemikal.interfacedaos.CxpInterfaceDao;
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


/**
 *Este controller es para ingresar facturas, de las entradas que se dieron con remision, 
 * al ingresar la factura se genera un registro en Cuentas por Pagar.
 *Tambien se utiliza para dar de alta facturas que no provienen de entradas de mercancia.
 * 
 */
@Controller
@SessionAttributes({"user"})
@RequestMapping("/provfacturas/")
public class CxpProvFacturasController {
    ResourceProject resource = new ResourceProject();
    private static final Logger log  = Logger.getLogger(CxpProvFacturasController.class.getName());
    
    @Autowired
    @Qualifier("daoCxp")
    private CxpInterfaceDao cxpDao;
    
    public CxpInterfaceDao getCxpDao() {
        return cxpDao;
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
        
        log.log(Level.INFO, "Ejecutando starUp de {0}", CxpProvFacturasController.class.getName());
        LinkedHashMap<String,String> infoConstruccionTabla = new LinkedHashMap<String,String>();
        
        //infoConstruccionTabla.put("id", "Acciones:90");
        infoConstruccionTabla.put("id", "Acciones:70");
        infoConstruccionTabla.put("factura", "Factura:90");
        infoConstruccionTabla.put("proveedor", "Proveedor:280");
        infoConstruccionTabla.put("moneda", "Moneda:80");
        infoConstruccionTabla.put("total", "Monto:100");
        infoConstruccionTabla.put("fecha_factura","Fecha&nbsp;Fac.:80");
        infoConstruccionTabla.put("estado","Estado:90");
        infoConstruccionTabla.put("tipo", "Tipo:110");
        
        ModelAndView x = new ModelAndView("provfacturas/startup", "title", "Facturas de Proveedores");
        
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
    
    
    @RequestMapping(value="/getAllFacturas.json", method = RequestMethod.POST)
    public @ResponseBody HashMap<String,ArrayList<HashMap<String, Object>>> getAllFacturasJson(
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
        
        //aplicativo facturas de proveedores
        Integer app_selected = 30;
        
        //decodificar id de usuario
        Integer id_usuario = Integer.parseInt(Base64Coder.decodeString(id_user_cod));
        
        //variables para el buscador
        String factura = "%"+StringHelper.isNullString(String.valueOf(has_busqueda.get("factura")))+"%";
        String proveedor = "%"+StringHelper.isNullString(String.valueOf(has_busqueda.get("proveedor")))+"%";
        String fecha_inicial = ""+StringHelper.isNullString(String.valueOf(has_busqueda.get("fecha_inicial")))+"";
        String fecha_final = ""+StringHelper.isNullString(String.valueOf(has_busqueda.get("fecha_final")))+"";
        
        String data_string = app_selected+"___"+id_usuario+"___"+factura+"___"+proveedor+"___"+fecha_inicial+"___"+fecha_final;
        
        //System.out.println("data_string: "+data_string);
        
        //obtiene total de registros en base de datos, con los parametros de busqueda
        int total_items = this.getCxpDao().countAll(data_string);
        
        //calcula el total de paginas
        int total_pags = resource.calculaTotalPag(total_items,items_por_pag);
        
        //variables que necesita el datagrid, para no tener que hacer uno por cada aplicativo
        DataPost dataforpos = new DataPost(orderby, desc, items_por_pag, pag_start, display_pag, input_json, cadena_busqueda,total_items,total_pags,id_user_cod);
        
        int offset = resource.__get_inicio_offset(items_por_pag, pag_start);
        
        //obtiene los registros para el grid, de acuerdo a los parametros de busqueda
        jsonretorno.put("Data", this.getCxpDao().getProvFacturas_PaginaGrid(data_string, offset, items_por_pag, orderby, desc));
        
        //obtiene el hash para los datos que necesita el datagrid
        jsonretorno.put("DataForGrid", dataforpos.formaHashForPos(dataforpos));
        
        return jsonretorno;
    }
    
    
    
    @RequestMapping(method = RequestMethod.POST, value="/getDatosFacturaProveedor.json")
    public @ResponseBody HashMap<String,ArrayList<HashMap<String, String>>> getDatosFacturaProveedorJson(
            @RequestParam(value="id_factura", required=true) Integer id,
            @RequestParam(value="iu", required=true) String id_user,
            Model model
            ) {
        
        log.log(Level.INFO, "Ejecutando getDatosFacturaProveedorJson de {0}", CxpProvFacturasController.class.getName());
        HashMap<String,ArrayList<HashMap<String, String>>> jsonretorno = new HashMap<String,ArrayList<HashMap<String, String>>>();
        
        ArrayList<HashMap<String, String>> monedas = new ArrayList<HashMap<String, String>>();
        ArrayList<HashMap<String, String>> impuestos = new ArrayList<HashMap<String, String>>();
        ArrayList<HashMap<String, String>> tasaFletes = new ArrayList<HashMap<String, String>>();
        ArrayList<HashMap<String, String>> datosFactura = new ArrayList<HashMap<String, String>>();
        ArrayList<HashMap<String, String>> datosProveedor = new ArrayList<HashMap<String, String>>();
        ArrayList<HashMap<String, String>> datosGrid = new ArrayList<HashMap<String, String>>();
        ArrayList<HashMap<String, String>> fleteras = new ArrayList<HashMap<String, String>>();
        ArrayList<HashMap<String, String>> diasCredito = new ArrayList<HashMap<String, String>>();
        ArrayList<HashMap<String, String>> ieps = new ArrayList<HashMap<String, String>>();
        HashMap<String, String> userDat = new HashMap<String, String>();
        
        //decodificar id de usuario
        Integer id_usuario = Integer.parseInt(Base64Coder.decodeString(id_user));
        userDat = this.getHomeDao().getUserById(id_usuario);
        
        Integer id_empresa = Integer.parseInt(userDat.get("empresa_id"));
        Integer id_sucursal = Integer.parseInt(userDat.get("sucursal_id"));
        
        if( id != 0 ){
            datosFactura = this.getCxpDao().getProvFacturas_Datos(id);
            //System.out.println("Id de proveedor: "+datosEntrada.get(0).get("proveedor_id").toString());
            datosProveedor = this.getCxpDao().getProvFacturas_DatosProveedor(Integer.parseInt(datosFactura.get(0).get("cxc_prov_id")));
            datosGrid = this.getCxpDao().getProvFacturas_DatosGrid(id);
        }
        
        monedas = this.getCxpDao().getMonedas();
        impuestos = this.getCxpDao().getImpuestos();
        tasaFletes = this.getCxpDao().getTasaFletes();
        fleteras = this.getCxpDao().getFleteras(id_empresa,id_sucursal);
        diasCredito = this.getCxpDao().getProvFacturas_DiasCredito();
        ieps = this.getCxpDao().getIeps(id_empresa, 0);
        
        jsonretorno.put("datosFactura", datosFactura);
        jsonretorno.put("datosGrid", datosGrid);
        jsonretorno.put("datosProveedor", datosProveedor);
        jsonretorno.put("Monedas", monedas);
        jsonretorno.put("Impuestos", impuestos);
        jsonretorno.put("tasaFletes", tasaFletes);
        jsonretorno.put("Fleteras", fleteras);
        jsonretorno.put("DiasCredito", diasCredito);
        jsonretorno.put("Ieps", ieps);
        
        return jsonretorno;
    }
    
    
    
    //obtiene las entradas de mercancia con remisiones para el buscador
    @RequestMapping(method = RequestMethod.POST, value="/getBuscadorRemisiones.json")
    public @ResponseBody HashMap<String,ArrayList<HashMap<String, String>>> getBuscadorRemisionesJson(
            @RequestParam(value="folio_remision", required=true) String folio_remision,
            @RequestParam(value="folio_entrada", required=true) String folio_entrada,
            @RequestParam(value="proveedor", required=true) String proveedor,
            @RequestParam(value="iu", required=true) String id_user,
            Model model
            ) {
        
        log.log(Level.INFO, "Ejecutando getBuscadorRemisionesJson de {0}", CxpProvFacturasController.class.getName());
        HashMap<String,ArrayList<HashMap<String, String>>> jsonretorno = new HashMap<String,ArrayList<HashMap<String, String>>>();
        ArrayList<HashMap<String, String>> productos = new ArrayList<HashMap<String, String>>();
        HashMap<String, String> userDat = new HashMap<String, String>();
        //decodificar id de usuario
        Integer id_usuario = Integer.parseInt(Base64Coder.decodeString(id_user));
        userDat = this.getHomeDao().getUserById(id_usuario);
        
        Integer id_empresa = Integer.parseInt(userDat.get("empresa_id"));
        Integer id_sucursal = Integer.parseInt(userDat.get("sucursal_id"));
        
        productos = this.getCxpDao().getProvFacturas_BuscaRemisiones(folio_remision, folio_entrada, proveedor, id_empresa, id_sucursal);
        
        jsonretorno.put("Remisiones", productos);
        
        return jsonretorno;
    }
    
    
    
    
    //obtiene los datos de la entrada con remision
    @RequestMapping(method = RequestMethod.POST, value="/getDatosEntradaRemision.json")
    public @ResponseBody HashMap<String,ArrayList<HashMap<String, String>>> getDatosEntradaRemisionJson(
            @RequestParam(value="id_entrada", required=true) Integer id,
            @RequestParam(value="iu", required=true) String id_user,
            Model model
            ) {
        
        log.log(Level.INFO, "Ejecutando getDatosEntradaRemisionJson de {0}", CxpProvFacturasController.class.getName());
        HashMap<String,ArrayList<HashMap<String, String>>> jsonretorno = new HashMap<String,ArrayList<HashMap<String, String>>>();
        
        ArrayList<HashMap<String, String>> datosRemision = new ArrayList<HashMap<String, String>>();
        ArrayList<HashMap<String, String>> datosProveedorRemision = new ArrayList<HashMap<String, String>>();
        ArrayList<HashMap<String, String>> datosGridRemision = new ArrayList<HashMap<String, String>>();
        ArrayList<HashMap<String, String>> monedas = new ArrayList<HashMap<String, String>>();
        ArrayList<HashMap<String, String>> fleteras = new ArrayList<HashMap<String, String>>();
        ArrayList<HashMap<String, String>> diasCredito = new ArrayList<HashMap<String, String>>();
        HashMap<String, String> userDat = new HashMap<String, String>();
        
        //decodificar id de usuario
        Integer id_usuario = Integer.parseInt(Base64Coder.decodeString(id_user));
        userDat = this.getHomeDao().getUserById(id_usuario);
        
        Integer id_empresa = Integer.parseInt(userDat.get("empresa_id"));
        Integer id_sucursal = Integer.parseInt(userDat.get("sucursal_id"));
        
        datosRemision = this.getCxpDao().getProvFacturas_DatosRemision(id);
        datosProveedorRemision = this.getCxpDao().getProvFacturas_DatosProveedor(Integer.parseInt(datosRemision.get(0).get("proveedor_id")));
        datosGridRemision = this.getCxpDao().getProvFacturas_DatosGridRemision(id);
        monedas = this.getCxpDao().getMonedas();
        fleteras = this.getCxpDao().getFleteras(id_empresa,id_sucursal);
        diasCredito = this.getCxpDao().getProvFacturas_DiasCredito();
        
        
        jsonretorno.put("datosRemision", datosRemision);
        jsonretorno.put("datosGridRemision", datosGridRemision);
        jsonretorno.put("datosProveedorRemision", datosProveedorRemision);
        jsonretorno.put("Monedas", monedas);
        jsonretorno.put("Fleteras", fleteras);
        jsonretorno.put("DiasCredito", diasCredito);
        
        return jsonretorno;
    }
    
    
    
    //obtiene los proveedores para el buscador
    @RequestMapping(method = RequestMethod.POST, value="/getBuacadorProveedores.json")
    public @ResponseBody HashMap<String,ArrayList<HashMap<String, String>>> getBuacadorProveedoresJson(
            @RequestParam(value="rfc", required=true) String rfc,
            @RequestParam(value="email", required=true) String email,
            @RequestParam(value="nombre", required=true) String nombre,
            @RequestParam(value="iu", required=true) String id_user,
            Model model
            ) {
        
        log.log(Level.INFO, "Ejecutando getBuacadorProveedores de {0}", CxpProvFacturasController.class.getName());
        HashMap<String,ArrayList<HashMap<String, String>>> jsonretorno = new HashMap<String,ArrayList<HashMap<String, String>>>();
        ArrayList<HashMap<String, String>> proveedores = new ArrayList<HashMap<String, String>>();
        HashMap<String, String> userDat = new HashMap<String, String>();
        
        //decodificar id de usuario
        Integer id_usuario = Integer.parseInt(Base64Coder.decodeString(id_user));
        userDat = this.getHomeDao().getUserById(id_usuario);
        
        Integer id_empresa = Integer.parseInt(userDat.get("empresa_id"));
        
        proveedores = this.getCxpDao().getProvFacturas_BuscadorProveedores(rfc, email, nombre,id_empresa);
        
        jsonretorno.put("proveedores", proveedores);
        
        return jsonretorno;
    }
    
    
    
    //obtiene los productos para el buscador
    @RequestMapping(method = RequestMethod.POST, value="/getTipoCambio.json")
    //public @ResponseBody HashMap<java.lang.String,java.lang.Object> getProveedorJson(
    public @ResponseBody HashMap<String,ArrayList<HashMap<String, String>>> getTipoCambioJson(
            @RequestParam(value="fecha", required=true) String fecha,
            Model model
            ) {
        
        log.log(Level.INFO, "Ejecutando getTipoCambio de {0}", CxpProvFacturasController.class.getName());
        HashMap<String,ArrayList<HashMap<String, String>>> jsonretorno = new HashMap<String,ArrayList<HashMap<String, String>>>();
        ArrayList<HashMap<String, String>> tc = new ArrayList<HashMap<String, String>>();
        
        tc = this.getCxpDao().getProvFacturas_TipoCambio(fecha);
        
        jsonretorno.put("tipoCambio", tc);
        
        return jsonretorno;
    }
    
    
    
    //edicion y nuevo
    @RequestMapping(method = RequestMethod.POST, value="/edit.json")
    public @ResponseBody HashMap<String, String> editJson(
            @RequestParam(value="id_factura", required=true) Integer id_factura,
            @RequestParam(value="tipo_factura", required=true) Integer tipo_factura,
            @RequestParam(value="id_proveedor", required=true) String id_proveedor,
            @RequestParam(value="observaciones", required=true) String observaciones,
            @RequestParam(value="factura", required=true) String factura,
            @RequestParam(value="expedicion", required=true) String expedicion,
            @RequestParam(value="tc", required=true) String tc,
            @RequestParam(value="denominacion", required=true) String denominacion,
            @RequestParam(value="numeroguia", required=true) String numeroguia,
            @RequestParam(value="ordencompra", required=true) String ordencompra,
            @RequestParam(value="fletera", required=true) String fletera_id,
            @RequestParam(value="credito", required=true) String dias_credito_id,
            @RequestParam(value="flete", required=true) String flete,
            @RequestParam(value="total_tr", required=true) Integer total_tr,
            @RequestParam(value="codigo", required=true) String[] codigo_producto,
            @RequestParam(value="titulo", required=true) String[] descripcion_producto,
            @RequestParam(value="unidad", required=true) String[] unidad,
            @RequestParam(value="presentacion", required=true) String[] presentacion,
            @RequestParam(value="cantidad", required=true) String[] cantidad,
            @RequestParam(value="costo", required=true) String[] costo,
            @RequestParam(value="impuesto", required=true) String[] impuesto_id,
            @RequestParam(value="eliminado", required=true) String[] eliminado,
            @RequestParam(value="valorimp", required=true) String[] valor_imp,
            @RequestParam(value="select_ieps", required=true) String[] ieps_id,
            @RequestParam(value="valorieps", required=true) String[] tasa_ieps,
            @ModelAttribute("user") UserSessionData user,
            Model model
            ) {
            
            HashMap<String, String> jsonretorno = new HashMap<String, String>();
            HashMap<String, String> succes = new HashMap<String, String>();
            String extra_data_array = null;
            String arreglo[];
            arreglo = new String[codigo_producto.length];
            String actualizo = "0";
            
            Integer app_selected = 30;
            String command_selected = "new";
            Integer id_usuario= user.getUserId();//variable para el id  del usuario
            
            if(codigo_producto.length > 0){
                for(int i=0; i<codigo_producto.length; i++) { 
                    arreglo[i]= "'"+codigo_producto[i].toUpperCase()+"___"+descripcion_producto[i].toUpperCase()+"___"+unidad[i].toUpperCase()+"___"+presentacion[i].toUpperCase()+"___"+cantidad[i]+"___"+costo[i]+"___"+impuesto_id[i]+"___"+valor_imp[i]+"___"+eliminado[i]+"___"+ieps_id[i]+"___"+tasa_ieps[i]+"'";
                }
                //serializar el arreglo
                extra_data_array = StringUtils.join(arreglo, ",");
            }else{
                extra_data_array ="'sin datos'";
            }
            
            if( id_factura==0 ){
                command_selected = "new";
            }else{
                command_selected = "edit";
            }
            
            String data_string = app_selected+"___"+command_selected+"___"+id_usuario+"___"+id_factura+"___"+id_proveedor+"___"+tipo_factura+"___"+observaciones.toUpperCase()+"___"+factura.toUpperCase()+"___"+expedicion+"___"+tc+"___"+denominacion+"___"+numeroguia+"___"+ordencompra+"___"+fletera_id+"___"+dias_credito_id+"___"+flete;
            
            //System.out.println("data_string: "+data_string);
            
            succes = this.getCxpDao().selectFunctionValidateAaplicativo(data_string,app_selected,extra_data_array);
            
            log.log(Level.INFO, "despues de validacion {0}", String.valueOf(succes.get("success")));
            
            if( String.valueOf(succes.get("success")).equals("true") ){
                actualizo = this.getCxpDao().selectFunctionForCxpAdmProcesos(data_string, extra_data_array);
            }
            
            jsonretorno.put("success",String.valueOf(succes.get("success")));
            
            log.log(Level.INFO, "Salida json {0}", String.valueOf(jsonretorno.get("success")));
        return jsonretorno;
    }
    
    
    
    //obtiene los tipos de cancelacion
    @RequestMapping(method = RequestMethod.POST, value="/getTiposCancelacion.json")
    public @ResponseBody HashMap<String,ArrayList<HashMap<String, String>>> getTiposCancelacionJson(
            Model model
            ) {
        
        log.log(Level.INFO, "Ejecutando getTiposCancelacionJson de {0}", CxpProvFacturasController.class.getName());
        HashMap<String,ArrayList<HashMap<String, String>>> jsonretorno = new HashMap<String,ArrayList<HashMap<String, String>>>();
        ArrayList<HashMap<String, String>> tipos_cancelacion = new ArrayList<HashMap<String, String>>();
        
        tipos_cancelacion = this.getCxpDao().getProvFacturas_TiposCancelacion();
        
        jsonretorno.put("Tipos", tipos_cancelacion);
        
        return jsonretorno;
    }
    
    
    
    
    //cancelacion de facturas
    @RequestMapping(method = RequestMethod.POST, value="/cancelar_factura.json")
    public @ResponseBody HashMap<String, String> getCancelarFactura(
            @RequestParam(value="id_factura", required=true) Integer id_factura,
            @RequestParam(value="tipo_cancelacion", required=true) Integer tipo_cancelacion,
            @RequestParam(value="motivo", required=true) String motivo_cancelacion,
            @RequestParam(value="iu", required=true) String id_user,
            Model model
            ) {
        
        HashMap<String, String> jsonretorno = new HashMap<String, String>();
        System.out.println("Cancelacion de factura de proveedor");
        //decodificar id de usuario
        Integer id_usuario = Integer.parseInt(Base64Coder.decodeString(id_user));
        
        Integer app_selected = 30;
        String command_selected = "cancelacion";
        String extra_data_array = "'sin datos'";
        
        String data_string = app_selected+"___"+command_selected+"___"+id_usuario+"___"+id_factura+"___"+tipo_cancelacion+"___"+motivo_cancelacion.toUpperCase();
        
        jsonretorno.put("success",String.valueOf( this.getCxpDao().selectFunctionForCxpAdmProcesos(data_string,extra_data_array)) );
        
        return jsonretorno;
    }
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
}
