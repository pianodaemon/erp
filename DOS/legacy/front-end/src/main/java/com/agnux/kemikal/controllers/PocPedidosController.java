package com.agnux.kemikal.controllers;

import com.agnux.cfd.v2.Base64Coder;
import com.agnux.common.helpers.FileHelper;
import com.agnux.common.helpers.StringHelper;
import com.agnux.common.obj.DataPost;
import com.agnux.common.obj.ResourceProject;
import com.agnux.common.obj.UserSessionData;
import com.agnux.kemikal.interfacedaos.GralInterfaceDao;
import com.agnux.kemikal.interfacedaos.HomeInterfaceDao;
import com.agnux.kemikal.interfacedaos.PocInterfaceDao;
import com.agnux.kemikal.reportes.PdfPocPedidoFormato1;
import com.agnux.kemikal.reportes.PdfPocPedidoFormato2;
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
import java.util.concurrent.TimeUnit;
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
import com.maxima.sales.cli.grpc.PedidoRequest;
import com.maxima.sales.cli.grpc.PedidoResponse;
import com.maxima.sales.cli.grpc.PedidoCancelRequest;
import com.maxima.sales.cli.grpc.PedidoCancelResponse;
import com.maxima.sales.cli.grpc.SalesGrpc;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.StatusRuntimeException;


@Controller
@SessionAttributes({"user"})
@RequestMapping("/pocpedidos/")
public class PocPedidosController {
    ResourceProject resource = new ResourceProject();
    private static final Logger log  = Logger.getLogger(PocPedidosController.class.getName());
    
    @Autowired
    @Qualifier("daoGral")
    private GralInterfaceDao gralDao;
    
    @Autowired
    @Qualifier("daoHome")
    private HomeInterfaceDao HomeDao;
    
    //dao de procesos comerciales
    @Autowired
    @Qualifier("daoPoc")
    private PocInterfaceDao PocDao;
    
    public HomeInterfaceDao getHomeDao() {
        return HomeDao;
    }
    
    public GralInterfaceDao getGralDao() {
        return gralDao;
    }
    
    public PocInterfaceDao getPocDao() {
        return PocDao;
    }
    
    @RequestMapping(value="/startup.agnux")
    public ModelAndView startUp(HttpServletRequest request, HttpServletResponse response, 
            @ModelAttribute("user") UserSessionData user
            )throws ServletException, IOException {
        
        log.log(Level.INFO, "Ejecutando starUp de {0}", PocPedidosController.class.getName());
        LinkedHashMap<String,String> infoConstruccionTabla = new LinkedHashMap<String,String>();
        infoConstruccionTabla.put("id", "Acciones:70");
        infoConstruccionTabla.put("folio", "Folio:70");
        infoConstruccionTabla.put("cliente", "Cliente:320");
        infoConstruccionTabla.put("total", "Monto:100");
        infoConstruccionTabla.put("denominacion", "Moneda:70");
        infoConstruccionTabla.put("estado", "Estado:100");
        infoConstruccionTabla.put("fecha_creacion","Fecha creacion:110");
        
        ModelAndView x = new ModelAndView("pocpedidos/startup", "title", "Pedidos de Clientes");
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
    
    @RequestMapping(value="/getAllPedidos.json", method = RequestMethod.POST)
    public @ResponseBody HashMap<String,ArrayList<HashMap<String, Object>>> getAllPedidosJson(
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
        
        //aplicativo pedidos de clientes
        Integer app_selected = 64;
        
        //decodificar id de usuario
        Integer id_usuario = Integer.parseInt(Base64Coder.decodeString(id_user_cod));
        
        //variables para el buscador
        String folio = "%"+StringHelper.isNullString(String.valueOf(has_busqueda.get("folio")))+"%";
        String cliente = "%"+StringHelper.isNullString(String.valueOf(has_busqueda.get("cliente")))+"%";
        String codigo = "%"+StringHelper.isNullString(String.valueOf(has_busqueda.get("codigo")))+"%";
        String producto = "%"+StringHelper.isNullString(String.valueOf(has_busqueda.get("producto")))+"%";
        String agente = ""+StringHelper.isNullString(String.valueOf(has_busqueda.get("agente")))+"";
        String fecha_inicial = ""+StringHelper.isNullString(String.valueOf(has_busqueda.get("fecha_inicial")))+"";
        String fecha_final = ""+StringHelper.isNullString(String.valueOf(has_busqueda.get("fecha_final")))+"";
        
        String data_string = app_selected+"___"+id_usuario+"___"+folio+"___"+cliente+"___"+fecha_inicial+"___"+fecha_final+"___"+codigo+"___"+producto+"___"+agente;
        
        //obtiene total de registros en base de datos, con los parametros de busqueda
        int total_items = this.getPocDao().countAll(data_string);
        
        //calcula el total de paginas
        int total_pags = resource.calculaTotalPag(total_items,items_por_pag);
        
        //variables que necesita el datagrid, para no tener que hacer uno por cada aplicativo
        DataPost dataforpos = new DataPost(orderby, desc, items_por_pag, pag_start, display_pag, input_json, cadena_busqueda,total_items,total_pags, id_user_cod);
        
        int offset = resource.__get_inicio_offset(items_por_pag, pag_start);
        
        //obtiene los registros para el grid, de acuerdo a los parametros de busqueda
        jsonretorno.put("Data", this.getPocDao().getPocPedidos_PaginaGrid(data_string, offset, items_por_pag, orderby, desc));
        //obtiene el hash para los datos que necesita el datagrid
        jsonretorno.put("DataForGrid", dataforpos.formaHashForPos(dataforpos));
        
        return jsonretorno;
    }
    
    
    //obtiene los Agentes para el Buscador pricipal del Aplicativo
    @RequestMapping(method = RequestMethod.POST, value="/getAgentesParaBuscador.json")
    public @ResponseBody HashMap<String,ArrayList<HashMap<String, String>>> getAgentesParaBuscador(
            @RequestParam(value="iu", required=true) String id_user_cod,
            Model model
        ) {
        
        HashMap<String,ArrayList<HashMap<String, String>>> jsonretorno = new HashMap<String,ArrayList<HashMap<String, String>>>();
        HashMap<String, String> userDat = new HashMap<String, String>();
        ArrayList<HashMap<String, String>> agentes = new ArrayList<HashMap<String, String>>();
        
        //Decodificar id de usuario
        Integer id_usuario = Integer.parseInt(Base64Coder.decodeString(id_user_cod));
        userDat = this.getHomeDao().getUserById(id_usuario);
        Integer id_empresa = Integer.parseInt(userDat.get("empresa_id"));
        Integer id_sucursal = Integer.parseInt(userDat.get("sucursal_id"));
        
        boolean obtener_todos_los_agentes=true;
        
        agentes = this.getPocDao().getAgentes(id_empresa, id_sucursal, obtener_todos_los_agentes);
        
        jsonretorno.put("Agentes", agentes);
        return jsonretorno;
    }
    
    
    
    //Valida el usuario que autoriza precios
    @RequestMapping(method = RequestMethod.POST, value="/getAuth.json")
    public @ResponseBody HashMap<String,Object> getAuthJson(
            @RequestParam(value="cad", required=true) String cadena_autorizacion,
            @RequestParam(value="iu", required=true) String id_user_cod,
            Model model
        ) {
        
        HashMap<String,Object> jsonretorno = new HashMap<String,Object>();
        HashMap<String, String> userDat = new HashMap<String, String>();
        
        //Decodificar id de usuario
        Integer id_usuario = Integer.parseInt(Base64Coder.decodeString(id_user_cod));
        userDat = this.getHomeDao().getUserById(id_usuario);
        String id_sucursal = userDat.get("sucursal_id");
        
        HashMap<String,String> hash_autorizacion = StringHelper.convert2hash(StringHelper.ascii2string(cadena_autorizacion));
        String username = StringHelper.isNullString(String.valueOf(hash_autorizacion.get("idauth")));
        String password = StringHelper.isNullString(String.valueOf(hash_autorizacion.get("passauth")));
        
        System.out.println("cadena_autorizacion: "+cadena_autorizacion);
        System.out.println("username: "+username+"    password: "+password);
        
        jsonretorno.put("Data", this.getPocDao().getValidarUser(username, password, id_sucursal));
        
        return jsonretorno;
    }
    
    
    //Trae los datos del pedido
    @RequestMapping(method = RequestMethod.POST, value="/getPedido.json")
    public @ResponseBody HashMap<String,ArrayList<HashMap<String, String>>> getPedidoJson(
            @RequestParam(value="id_pedido", required=true) String id_pedido,
            @RequestParam(value="iu", required=true) String id_user,
            Model model
        ) {
        
        log.log(Level.INFO, "Ejecutando getPedidoJson de {0}", PocPedidosController.class.getName());
        HashMap<String,ArrayList<HashMap<String, String>>> jsonretorno = new HashMap<String,ArrayList<HashMap<String, String>>>();
        ArrayList<HashMap<String, String>> datosPedido = new ArrayList<HashMap<String, String>>();
        ArrayList<HashMap<String, String>> datosGrid = new ArrayList<HashMap<String, String>>();
        ArrayList<HashMap<String, String>> tipoCambioActual = new ArrayList<HashMap<String, String>>();
        ArrayList<HashMap<String, String>> arrayExtra = new ArrayList<HashMap<String, String>>();
        HashMap<String, String> extra = new HashMap<String, String>();
        HashMap<String, String> tc = new HashMap<String, String>();

        boolean obtener_todos_los_agentes=false;
        
        //Decodificar id de usuario
        Integer id_usuario = Integer.parseInt(Base64Coder.decodeString(id_user));
        HashMap<String, String> userDat = this.getHomeDao().getUserById(id_usuario);
        
        Integer id_empresa = Integer.parseInt(userDat.get("empresa_id"));
        Integer id_sucursal = Integer.parseInt(userDat.get("sucursal_id"));
        //Esta variable indica si la empresa incluye modulo de produccion
        extra.put("mod_produccion", userDat.get("incluye_produccion"));

        //Esta variable indica si la empresa es transportista
        extra.put("transportista", userDat.get("transportista").toLowerCase());

        if( !id_pedido.equals("0")  ){
            datosPedido = this.getPocDao().getPocPedido_Datos(Integer.parseInt(id_pedido));
            datosGrid = this.getPocDao().getPocPedido_DatosGrid(Integer.parseInt(id_pedido));
            System.out.println("proceso_flujo_id="+datosPedido.get(0).get("proceso_flujo_id"));
            //1;"COTIZACION",  4;"PEDIDO"
            if(Integer.parseInt(datosPedido.get(0).get("proceso_flujo_id"))!=1 && Integer.parseInt(datosPedido.get(0).get("proceso_flujo_id"))!=4){
                //Esto es para permitir obtener los datos de los agentes eliminados, ya que se debe mostrar en los pedidos historicos
                obtener_todos_los_agentes=true;
            }
        }
        
        //Aqui se obtienen los parametros de la facturacion, nos intersa saber si se debe permitir cambiar_unidad_medida
        HashMap<String, String> parametros = this.getPocDao().getPocPedido_Parametros(id_empresa, id_sucursal);
        extra.put("cambioUM", parametros.get("cambiar_unidad_medida"));
        extra.put("per_descto", parametros.get("permitir_descto"));
        
        //Esta variable indica si se debe generar requisicion de compra
        extra.put("per_req", parametros.get("permitir_req"));
        
        ArrayList<HashMap<String, String>> valorIva = this.getPocDao().getValoriva(id_sucursal);
        tc.put("tipo_cambio", StringHelper.roundDouble(this.getPocDao().getTipoCambioActual(), 4));
        tipoCambioActual.add(0,tc);
        
        arrayExtra.add(0,extra);
        jsonretorno.put("datosPedido", datosPedido);
        jsonretorno.put("datosGrid", datosGrid);
        jsonretorno.put("iva", valorIva);
        jsonretorno.put("Tc", tipoCambioActual);
        jsonretorno.put("Extras", arrayExtra);
        jsonretorno.put("Monedas", this.getPocDao().getMonedas());
        jsonretorno.put("Vendedores", this.getPocDao().getAgentes(id_empresa, id_sucursal, obtener_todos_los_agentes));
        jsonretorno.put("Condiciones", this.getPocDao().getCondicionesDePago());
        jsonretorno.put("MetodosPago", this.getPocDao().getMetodosPago(id_empresa));
        jsonretorno.put("Almacenes", this.getPocDao().getPocPedido_Almacenes(id_sucursal));
        jsonretorno.put("UM", this.getPocDao().getUnidadesMedida());
        jsonretorno.put("Ieps", this.getPocDao().getIeps(id_empresa, 0));
        jsonretorno.put("Usos", this.getPocDao().getUsos());
        jsonretorno.put("Metodos", this.getPocDao().getMetodos());
        return jsonretorno;
    }
    
    
    //Obtiene el las Municipios de un Estado
    @RequestMapping(method = RequestMethod.POST, value="/getMunicipios.json")
    public @ResponseBody HashMap<String,ArrayList<HashMap<String, String>>> getMunicipiosJson(
            @RequestParam(value="id_pais", required=true) String id_pais,
            @RequestParam(value="id_entidad", required=true) String id_entidad,
            Model model
        ) {
        HashMap<String,ArrayList<HashMap<String, String>>> jsonretorno = new HashMap<String,ArrayList<HashMap<String, String>>>();
        jsonretorno.put("Municipios", this.getPocDao().getLocalidadesForThisEntidad(id_pais, id_entidad));
        return jsonretorno;
    }
    
    
    
    //Obtiene el las Estados de un Pais
    @RequestMapping(method = RequestMethod.POST, value="/getEstados.json")
    public @ResponseBody HashMap<String,ArrayList<HashMap<String, String>>> getEstadosJson(
            @RequestParam(value="id_pais", required=true) String id_pais,
            Model model
        ) {
        HashMap<String,ArrayList<HashMap<String, String>>> jsonretorno = new HashMap<String,ArrayList<HashMap<String, String>>>();
        jsonretorno.put("Estados", this.getPocDao().getEntidadesForThisPais(id_pais));
        return jsonretorno;
    }
    
    
    //Buscador de clientes
    @RequestMapping(method = RequestMethod.POST, value="/getBuscadorClientes.json")
    public @ResponseBody HashMap<String,ArrayList<HashMap<String, String>>> getBuscadorClientesJson(
            @RequestParam(value="cadena", required=true) String cadena,
            @RequestParam(value="filtro", required=true) Integer filtro,
            @RequestParam(value="iu", required=true) String id_user,
            Model model
        ) {
        
        HashMap<String,ArrayList<HashMap<String, String>>> jsonretorno = new HashMap<String,ArrayList<HashMap<String, String>>>();
        HashMap<String, String> userDat = new HashMap<String, String>();
        HashMap<String, String> parametros = new HashMap<String, String>();
        
        //decodificar id de usuario
        Integer id_usuario = Integer.parseInt(Base64Coder.decodeString(id_user));
        
        userDat = this.getHomeDao().getUserById(id_usuario);
        
        Integer id_empresa = Integer.parseInt(userDat.get("empresa_id"));
        Integer id_sucursal = Integer.parseInt(userDat.get("sucursal_id"));
        
        parametros = this.getPocDao().getPocPedido_Parametros(id_empresa, id_sucursal);
        String permite_descto = parametros.get("permitir_descto").toLowerCase();
        
        jsonretorno.put("clientes", this.getPocDao().getBuscadorClientes(cadena,filtro,id_empresa, id_sucursal, permite_descto));
        
        return jsonretorno;
    }
    
    
    
    //Obtener datos del cliente a partir del Numero de Control
    @RequestMapping(method = RequestMethod.POST, value="/getDataByNoClient.json")
    public @ResponseBody HashMap<String,ArrayList<HashMap<String, String>>> getDataByNoClientJson(
            @RequestParam(value="no_control", required=true) String no_control,
            @RequestParam(value="iu", required=true) String id_user,
            Model model
        ) {
        
        HashMap<String,ArrayList<HashMap<String, String>>> jsonretorno = new HashMap<String,ArrayList<HashMap<String, String>>>();
        ArrayList<HashMap<String, String>> datosCliente = new ArrayList<HashMap<String, String>>();
        HashMap<String, String> userDat = new HashMap<String, String>();
        HashMap<String, String> parametros = new HashMap<String, String>();
        
        //Decodificar id de usuario
        Integer id_usuario = Integer.parseInt(Base64Coder.decodeString(id_user));
        
        userDat = this.getHomeDao().getUserById(id_usuario);
        
        Integer id_empresa = Integer.parseInt(userDat.get("empresa_id"));
        Integer id_sucursal = Integer.parseInt(userDat.get("sucursal_id"));
        
        parametros = this.getPocDao().getPocPedido_Parametros(id_empresa, id_sucursal);
        String permite_descto = parametros.get("permitir_descto").toLowerCase();
        
        datosCliente = this.getPocDao().getDatosClienteByNoCliente(no_control, id_empresa, id_sucursal, permite_descto);
        
        jsonretorno.put("Cliente", datosCliente);
        
        return jsonretorno;
    }
    
    
    
    
    
    //Obtener datos del cliente a partir del Numero de Control
    @RequestMapping(method = RequestMethod.POST, value="/getDatosCotizacion.json")
    public @ResponseBody HashMap<String,Object> getDatosCotizacionJson(
            @RequestParam(value="no_cot", required=true) String folio_cot,
            @RequestParam(value="iu", required=true) String id_user,
            Model model
        ) {
        
        HashMap<String,Object> jsonretorno = new HashMap<String,Object>();
        ArrayList<HashMap<String, String>> datosCotizacion = new ArrayList<HashMap<String, String>>();
        ArrayList<HashMap<String, String>> datosGridCotizacion = new ArrayList<HashMap<String, String>>();
        HashMap<String, String> userDat = new HashMap<String, String>();
        HashMap<String, String> parametros = new HashMap<String, String>();
        
        //Decodificar id de usuario
        Integer id_usuario = Integer.parseInt(Base64Coder.decodeString(id_user));
        
        userDat = this.getHomeDao().getUserById(id_usuario);
        
        Integer id_empresa = Integer.parseInt(userDat.get("empresa_id"));
        Integer id_sucursal = Integer.parseInt(userDat.get("sucursal_id"));
        
        parametros = this.getPocDao().getPocPedido_Parametros(id_empresa, id_sucursal);
        String permite_descto = parametros.get("permitir_descto").toLowerCase();
        
        HashMap<String, Object> verif_cotizacion = this.getPocDao().getVerificarCotizacion(folio_cot, id_sucursal);
        
        if(String.valueOf(verif_cotizacion.get("success")).equals("true")){
            datosCotizacion = this.getPocDao().getPocPedido_DatosCotizacion(folio_cot, id_empresa);
            if(datosCotizacion.size()>0){
                datosGridCotizacion = this.getPocDao().getPocPedido_DatosCotizacionGrid(datosCotizacion.get(0).get("id_cot"));
            }
        }
        
        jsonretorno.put("COTDATOS", datosCotizacion);
        jsonretorno.put("COTGRID", datosGridCotizacion);
        jsonretorno.put("VERIF", verif_cotizacion);
        
        return jsonretorno;
    }
    
    
    
    
    
    //obtiene las Direcciones Fiscales del Cliente
    //esta busqueda solo es cuando el cliente tiene mas de una Dirección Fiscal
    @RequestMapping(method = RequestMethod.POST,value="/getDireccionesFiscalesCliente.json")
    public @ResponseBody HashMap<String,ArrayList<HashMap<String,String>>>getDireccionesFiscalesClienteJson(
            @RequestParam(value="id_cliente",required=true )Integer id_cliente,
            Model model
        ){
        HashMap<String,ArrayList<HashMap<String, String>>> jsonretorno = new HashMap<String,ArrayList<HashMap<String, String>>>();
        
        ArrayList<HashMap<String, String>> direcciones_fiscales = new ArrayList<HashMap<String, String>>();
        direcciones_fiscales=this.getPocDao().getPocPedido_DireccionesFiscalesCliente(id_cliente);
        jsonretorno.put("DirFiscal", direcciones_fiscales);
        
        return jsonretorno;
    }
    
    //obtiene la moneda de la lista de precios por el cliente
    @RequestMapping(method = RequestMethod.POST,value="/getMonedaListaCliente.json")
    public @ResponseBody HashMap<String,ArrayList<HashMap<String,String>>>getMonedaListaClienteJson(
            @RequestParam(value="lista_precio",required=true )Integer lista_precio,
            Model model
            ){
            HashMap<String,ArrayList<HashMap<String, String>>> jsonretorno = new HashMap<String,ArrayList<HashMap<String, String>>>();
        
        ArrayList<HashMap<String, String>> arraylistaprecio = new ArrayList<HashMap<String, String>>();
        arraylistaprecio=this.getPocDao().getListaPrecio(lista_precio);
        jsonretorno.put("listaprecio", arraylistaprecio);
        
        return jsonretorno;
    }
    
    
    //Obtiene los tipos de productos para el buscador de productos
    @RequestMapping(method = RequestMethod.POST, value="/getProductoTipos.json")
    public @ResponseBody HashMap<String,ArrayList<HashMap<String, String>>> getProductoTiposJson(
            @RequestParam(value="iu", required=true) String id_user_cod,
            Model model
        ) {
        
        HashMap<String,ArrayList<HashMap<String, String>>> jsonretorno = new HashMap<String,ArrayList<HashMap<String, String>>>();
        
        ArrayList<HashMap<String, String>> arrayTiposProducto = new ArrayList<HashMap<String, String>>();
        arrayTiposProducto=this.getPocDao().getProductoTipos();
        jsonretorno.put("prodTipos", arrayTiposProducto);
        
        return jsonretorno;
    }
    
    
    
    
    //Buscador de clientes
    @RequestMapping(method = RequestMethod.POST, value="/getBuscadorProductos.json")
    public @ResponseBody HashMap<String,ArrayList<HashMap<String, String>>> getBuscadorProductosJson(
            @RequestParam(value="sku", required=true) String sku,
            @RequestParam(value="tipo", required=true) String tipo,
            @RequestParam(value="descripcion", required=true) String descripcion,
            @RequestParam(value="iu", required=true) String id_user,
            Model model
        ) {
        
        HashMap<String,ArrayList<HashMap<String, String>>> jsonretorno = new HashMap<String,ArrayList<HashMap<String, String>>>();
        HashMap<String, String> userDat = new HashMap<String, String>();
        
        //decodificar id de usuario
        Integer id_usuario = Integer.parseInt(Base64Coder.decodeString(id_user));
        
        userDat = this.getHomeDao().getUserById(id_usuario);
        Integer id_empresa = Integer.parseInt(userDat.get("empresa_id"));
        
        jsonretorno.put("productos", this.getPocDao().getBuscadorProductos(sku,tipo,descripcion,id_empresa));
        
        return jsonretorno;
    }
    
    
    //Buscador de presentaciones de producto
    @RequestMapping(method = RequestMethod.POST, value="/getPresentacionesProducto.json")
    public @ResponseBody HashMap<String,ArrayList<HashMap<String, String>>> getPresentacionesProductoJson(
            @RequestParam(value="sku", required=true) String sku,
            @RequestParam(value="lista_precios",required=true) String lista_precio,
            @RequestParam(value="id_client", required=true) String idClient,
            @RequestParam(value="iu", required=true) String id_user,
            Model model
        ) {
        
        HashMap<String,ArrayList<HashMap<String, String>>> jsonretorno = new HashMap<String,ArrayList<HashMap<String, String>>>();
        HashMap<String, String> userDat = new HashMap<String, String>();
        ArrayList<HashMap<String, String>> ArrayPres = new ArrayList<HashMap<String, String>>();
        ArrayList<HashMap<String, String>> ArrayPresProcesado = new ArrayList<HashMap<String, String>>();
        
        //decodificar id de usuario
        Integer id_usuario = Integer.parseInt(Base64Coder.decodeString(id_user));
        
        userDat = this.getHomeDao().getUserById(id_usuario);
        Integer id_empresa = Integer.parseInt(userDat.get("empresa_id"));
        Integer id_sucursal = Integer.parseInt(userDat.get("sucursal_id"));
        
        ArrayPres = this.getPocDao().getPresentacionesProducto(sku.trim(),lista_precio,id_empresa);
        ArrayPresProcesado = this.getPocDao().getVerificarImpuesto(id_sucursal, Integer.parseInt(idClient), ArrayPres);
        
        jsonretorno.put("Presentaciones", ArrayPresProcesado);
        
        return jsonretorno;
    }
    
    
    
    
    //Buscar Unidades(vehiculos)
    @RequestMapping(method = RequestMethod.POST, value="/getBuscadorUnidades.json")
    public @ResponseBody HashMap<String,ArrayList<HashMap<String, String>>> getBuscadorUnidadesJson(
            @RequestParam(value="no_economico", required=true) String no_economico,
            @RequestParam(value="marca", required=true) String marca,
            @RequestParam(value="iu", required=true) String id_user,
            Model model
        ) {
        
        HashMap<String,ArrayList<HashMap<String, String>>> jsonretorno = new HashMap<String,ArrayList<HashMap<String, String>>>();
        HashMap<String, String> userDat = new HashMap<String, String>();
        
        //Decodificar id de usuario
        Integer id_usuario = Integer.parseInt(Base64Coder.decodeString(id_user));
        userDat = this.getHomeDao().getUserById(id_usuario);
        Integer id_empresa = Integer.parseInt(userDat.get("empresa_id"));
        //Integer id_sucursal = Integer.parseInt(userDat.get("sucursal_id"));
        
        //Le pasamos sucursal cro para que no aplique el filtro
        Integer id_sucursal=0;
        jsonretorno.put("Vehiculos", this.getPocDao().getBuscadorUnidades(no_economico,marca,id_empresa, id_sucursal));
        
        return jsonretorno;
    }
    
    
    //Obtener datos de la Unidad a partir del Numero economico
    @RequestMapping(method = RequestMethod.POST, value="/getDataUnidadByNoEco.json")
    public @ResponseBody HashMap<String,ArrayList<HashMap<String, String>>> getDataUnidadByNoEcoJson(
            @RequestParam(value="no_economico", required=true) String no_economico,
            @RequestParam(value="iu", required=true) String id_user,
            Model model
        ) {
        HashMap<String,ArrayList<HashMap<String, String>>> jsonretorno = new HashMap<String,ArrayList<HashMap<String, String>>>();
        HashMap<String, String> userDat = new HashMap<String, String>();
       
        //Decodificar id de usuario
        Integer id_usuario = Integer.parseInt(Base64Coder.decodeString(id_user));
        userDat = this.getHomeDao().getUserById(id_usuario);
        
        Integer id_empresa = Integer.parseInt(userDat.get("empresa_id"));
        //Integer id_sucursal = Integer.parseInt(userDat.get("sucursal_id"));
        //Le pasamos sucursal cro para que no aplique el filtro
        Integer id_sucursal=0;
        jsonretorno.put("Vehiculo", this.getPocDao().getDatosUnidadByNoEco(no_economico, id_empresa, id_sucursal));
        
        return jsonretorno;
    }
    
    

    
    //Buscar Operadores(choferes)
    @RequestMapping(method = RequestMethod.POST, value="/getBuscadorOperadores.json")
    public @ResponseBody HashMap<String,ArrayList<HashMap<String, String>>> getBuscadorOperadoresJson(
            @RequestParam(value="no_operador", required=true) String no_operador,
            @RequestParam(value="nombre", required=true) String nombre,
            @RequestParam(value="iu", required=true) String id_user,
            Model model
        ) {
        
        HashMap<String,ArrayList<HashMap<String, String>>> jsonretorno = new HashMap<String,ArrayList<HashMap<String, String>>>();
        HashMap<String, String> userDat = new HashMap<String, String>();
        
        //Decodificar id de usuario
        Integer id_usuario = Integer.parseInt(Base64Coder.decodeString(id_user));
        userDat = this.getHomeDao().getUserById(id_usuario);
        Integer id_empresa = Integer.parseInt(userDat.get("empresa_id"));
        //Integer id_sucursal = Integer.parseInt(userDat.get("sucursal_id"));
        
        //Le pasamos sucursal cro para que no aplique el filtro
        Integer id_sucursal=0;
        jsonretorno.put("Operadores", this.getPocDao().getBuscadorOperadores(no_operador,nombre,id_empresa, id_sucursal));
        
        return jsonretorno;
    }
    
    
    
    //Obtener datos del Operador a partir de la clave
    @RequestMapping(method = RequestMethod.POST, value="/getDataOperadorByNo.json")
    public @ResponseBody HashMap<String,ArrayList<HashMap<String, String>>> getDataOperadorByNoJson(
            @RequestParam(value="no_operador", required=true) String no_operador,
            @RequestParam(value="iu", required=true) String id_user,
            Model model
        ) {
        HashMap<String,ArrayList<HashMap<String, String>>> jsonretorno = new HashMap<String,ArrayList<HashMap<String, String>>>();
        HashMap<String, String> userDat = new HashMap<String, String>();
       
        //Decodificar id de usuario
        Integer id_usuario = Integer.parseInt(Base64Coder.decodeString(id_user));
        userDat = this.getHomeDao().getUserById(id_usuario);
        
        Integer id_empresa = Integer.parseInt(userDat.get("empresa_id"));
        //Integer id_sucursal = Integer.parseInt(userDat.get("sucursal_id"));
        //Le pasamos sucursal cro para que no aplique el filtro
        Integer id_sucursal=0;
        jsonretorno.put("Operador", this.getPocDao().getDatosOperadorByNo(no_operador, id_empresa, id_sucursal));
        
        return jsonretorno;
    }
    
    
    //Buscador de Agentes Aduanales
    @RequestMapping(method = RequestMethod.POST, value="/getBuscadorAgenA.json")
    public @ResponseBody HashMap<String,ArrayList<HashMap<String, Object>>> getBuscadorAgenAJson(
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
        Integer id_sucursal = 0;//se le asigna cero para que no filtre por sucursal
        
        jsonretorno.put("AgentesAduanales", this.getPocDao().getBuscadorAgentesAduanales(cadena,filtro,id_empresa, id_sucursal));
        
        return jsonretorno;
    }
    
    
    //Obtener datos del Agente Aduanal a partir del Numero de Control
    @RequestMapping(method = RequestMethod.POST, value="/getDataByNoAgen.json")
    public @ResponseBody HashMap<String,ArrayList<HashMap<String, Object>>> getDataByNoAgenAJson(
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
        //Integer id_sucursal = Integer.parseInt(userDat.get("sucursal_id"));
        Integer id_sucursal = 0;
        
        jsonretorno.put("AgenA", this.getPocDao().getDatosByNoAgenteAduanal(no_control, id_empresa, id_sucursal));
        
        return jsonretorno;
    }
    
    
    //Buscador de Remitentes
    @RequestMapping(method = RequestMethod.POST, value="/getBuscadorRemitentes.json")
    public @ResponseBody HashMap<String,ArrayList<HashMap<String, Object>>> getBuscadorRemitentesJson(
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
        Integer id_sucursal = 0;//se le asigna cero para que no filtre por sucursal
        
        jsonretorno.put("Remitentes", this.getPocDao().getBuscadorRemitentes(cadena,filtro,id_empresa, id_sucursal));
        
        return jsonretorno;
    }
    
    
    //Obtener datos del cliente a partir del Numero de Control
    @RequestMapping(method = RequestMethod.POST, value="/getDataByNoRemitente.json")
    public @ResponseBody HashMap<String,ArrayList<HashMap<String, Object>>> getDataByNoRemitenteJson(
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
        //Integer id_sucursal = Integer.parseInt(userDat.get("sucursal_id"));
        Integer id_sucursal = 0;
        
        jsonretorno.put("Remitente", this.getPocDao().getDatosClienteByNoRemitente(no_control, id_empresa, id_sucursal));
        
        return jsonretorno;
    }
    
    
    
    //Buscador de Destinatarios
    @RequestMapping(method = RequestMethod.POST, value="/getBuscadorDestinatarios.json")
    public @ResponseBody HashMap<String,ArrayList<HashMap<String, Object>>> getBuscadorDestinatariosJson(
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
        Integer id_sucursal = 0;//se le asigna cero para que no filtre por sucursal
        
        jsonretorno.put("Destinatarios", this.getPocDao().getBuscadorDestinatarios(cadena,filtro,id_empresa, id_sucursal));
        
        return jsonretorno;
    }
    
    
    //Obtener datos del Destinatario a partir del Numero de Control
    @RequestMapping(method = RequestMethod.POST, value="/getDataByNoDestinatario.json")
    public @ResponseBody HashMap<String,ArrayList<HashMap<String, Object>>> getDataByNoDestinatarioJson(
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
        //Integer id_sucursal = Integer.parseInt(userDat.get("sucursal_id"));
        Integer id_sucursal = 0;
        
        jsonretorno.put("Dest", this.getPocDao().getDatosByNoDestinatario(no_control, id_empresa, id_sucursal));
        
        return jsonretorno;
    }
    
    
    private void cancelPedido(int pedidoId, int usuarioId, HashMap<String,String> jsonretorno) {

        ManagedChannel channel = ManagedChannelBuilder.forTarget(Helper.getGrpcConnString())
            .usePlaintext()
            .build();
            
        SalesGrpc.SalesBlockingStub blockingStub = SalesGrpc.newBlockingStub(channel);
        
        PedidoCancelRequest pedidoCancelRequest =
            PedidoCancelRequest.newBuilder()
                .setPedidoId(pedidoId)
                .setUsuarioId(usuarioId)
                .build();

        PedidoCancelResponse pedidoCancelResponse;

        try {
            pedidoCancelResponse = blockingStub.cancelPedido(pedidoCancelRequest);
            String valorRetorno = pedidoCancelResponse.getValorRetorno();

            if (valorRetorno.equals("1")) {
                jsonretorno.put("success", "true");
                jsonretorno.put("actualizo", valorRetorno);

            } else {
                jsonretorno.put("success", valorRetorno);
            }
            log.log(Level.INFO, "Pedido Cancel Response valorRetorno: {0}", valorRetorno);

        } catch (StatusRuntimeException e) {
            jsonretorno.put("success", "Error en llamada a procedimiento remoto.");
            log.log(Level.SEVERE, "RPC failed: {0}", e.getStatus());

        } finally {
            try {
                channel.shutdownNow().awaitTermination(5, TimeUnit.SECONDS);

            } catch (InterruptedException e) {
                log.log(Level.SEVERE, "Channel shutdown failed.", e);
            }
        }
    }
    
    
    //Edicion, nuevo y cancelacion
    @RequestMapping(method = RequestMethod.POST, value="/edit.json")
    public @ResponseBody HashMap<String, String> editJson(
        @RequestParam(value="id_pedido", required=true)           Integer id_pedido,
        @RequestParam(value="id_cliente", required=true)          String id_cliente,
        @RequestParam(value="select_moneda", required=true)       String select_moneda,
        @RequestParam(value="tasa_ret_immex", required=true)      String tasa_ret_immex,
        @RequestParam(value="tipo_cambio", required=true)         String tipo_cambio,
        @RequestParam(value="observaciones", required=true)       String observaciones,
        @RequestParam(value="vendedor", required=true)            String id_agente,
        @RequestParam(value="select_condiciones", required=true)  String select_condiciones,
        @RequestParam(value="orden_compra", required=true)        String orden_compra,
        @RequestParam(value="fecha_compromiso", required=true)    String fecha_compromiso,
        @RequestParam(value="lugar_entrega", required=true)       String lugar_entrega,
        @RequestParam(value="transporte", required=true)          String transporte,
        @RequestParam(value="accion_proceso", required=true)      String accion_proceso,
        @RequestParam(value="select_metodo_pago", required=true)  Integer select_forma_pago,
        @RequestParam(value="select_uso", required=true)          String select_uso,
        @RequestParam(value="select_metodo", required=true)       String select_metodo,
        @RequestParam(value="no_cuenta", required=false)          String no_cuenta,
        @RequestParam(value="check_ruta", required=false)         String check_ruta,
        @RequestParam(value="select_almacen", required=false)     String select_almacen,
        @RequestParam(value="id_df", required=true)               String id_df,
        @RequestParam(value="check_enviar_obser", required=false) String check_enviar_obser,
        @RequestParam(value="pdescto", required=true)             String permitir_descto,
        @RequestParam(value="motivo_descuento", required=true)    String motivo_descuento,
        @RequestParam(value="valor_descto", required=true)        String porcentaje_descto,

        @RequestParam(value="eliminado", required=false)       String[] eliminado,
        @RequestParam(value="iddetalle", required=false)       String[] iddetalle,
        @RequestParam(value="idproducto", required=false)      String[] idproducto,
        @RequestParam(value="select_umedida", required=false)  String[] select_umedida,
        @RequestParam(value="id_presentacion", required=false) String[] id_presentacion,
        @RequestParam(value="id_imp_prod", required=false)     String[] id_impuesto,
        @RequestParam(value="valor_imp", required=false)       String[] valor_imp,
        @RequestParam(value="vdescto", required=false)         String[] vdescto,
        @RequestParam(value="idIeps", required=false)          String[] idIeps,
        @RequestParam(value="tasaIeps", required=false)        String[] tasaIeps,
        @RequestParam(value="ret_id", required=false)          String[] ret_id,
        @RequestParam(value="ret_tasa", required=false)        String[] ret_tasa,

        @RequestParam(value="cantidad", required=false)     String[] cantidad,
        @RequestParam(value="costo", required=false)        String[] costo,
        @RequestParam(value="noTr", required=false)         String[] noTr,
        @RequestParam(value="seleccionado", required=false) String[] seleccionado,
        @RequestParam(value="idcot", required=false)        String[] idcot,
        @RequestParam(value="iddetcot", required=false)     String[] iddetcot,
        @RequestParam(value="nocot", required=true)         String[] nocot,
        @RequestParam(value="statusreg", required=true)     String[] statusreg,
        @RequestParam(value="reqauth", required=true)       String[] reqauth,
        @RequestParam(value="success", required=true)       String[] salvar_registro,

        @RequestParam(value="transportista", required=true)                String transportista,
        @RequestParam(value="check_flete", required=false)                 String check_flete,
        @RequestParam(value="nombre_documentador", required=false)         String nombre_documentador,
        @RequestParam(value="valor_declarado", required=false)             String valor_declarado,
        @RequestParam(value="select_tviaje", required=false)               String select_tviaje,
        @RequestParam(value="remolque1", required=false)                   String remolque1,
        @RequestParam(value="remolque2", required=false)                   String remolque2,
        @RequestParam(value="id_vehiculo", required=false)                 String id_vehiculo,
        @RequestParam(value="no_operador", required=false)                 String no_operador,
        @RequestParam(value="nombre_operador", required=false)             String nombre_operador,
        @RequestParam(value="select_pais_origen", required=false)          String select_pais_origen,
        @RequestParam(value="select_estado_origen", required=false)        String select_estado_origen,
        @RequestParam(value="select_municipio_origen", required=false)     String select_municipio_origen,
        @RequestParam(value="select_pais_dest", required=false)            String select_pais_dest,
        @RequestParam(value="select_estado_dest", required=false)          String select_estado_dest,
        @RequestParam(value="select_municipio_dest", required=false)       String select_municipio_dest,
        @RequestParam(value="agena_id", required=false)                    String agena_id,
        @RequestParam(value="rem_id", required=false)                      String rem_id,
        @RequestParam(value="rem_dir_alterna", required=false)             String rem_dir_alterna,
        @RequestParam(value="dest_id", required=false)                     String dest_id,
        @RequestParam(value="dest_dir_alterna", required=false)            String dest_dir_alterna,
        @RequestParam(value="observaciones_transportista", required=false) String observaciones_transportista,

        @ModelAttribute("user") UserSessionData user)
    {
            
        log.log(Level.INFO, "Edicion/nuevo/cancelacion de pedido");

        Integer id_usuario = user.getUserId();
        String folio_cot = "";
        HashMap<String, String> jsonretorno = new HashMap<String, String>();
        
        if (accion_proceso.equals("cancelar")) {
            cancelPedido(id_pedido.intValue(), id_usuario.intValue(), jsonretorno);
            return jsonretorno;
        }

        String arreglo[] = new String[eliminado.length];
        
        PedidoRequest.Builder pedidoRequestBuilder = PedidoRequest.newBuilder();

        for (int i = 0; i < eliminado.length; i++) {

            if (!nocot[i].trim().equals("") && !nocot[i].equals("0")) {
                folio_cot = nocot[i];
            }

            //statreg&&&valuereg&&&ident
            String partida[] = statusreg[i].split("\\&&&");
            //partida[0]    Estatus autorizacion
            //partida[1]    Valor precio autorizado
            //partida[2]    Usuario que autoriza

            String stat_reg = (StringHelper.isNullString(String.valueOf(partida[0])).equals("0")) ? "false" : "true";
            String precio_autorizado = StringHelper.isNullString(String.valueOf(partida[1]));
            String id_user_autoriza = (
                partida[2].trim().equals("0")) ? partida[2] : Base64Coder.decodeString(StringHelper.isNullString(String.valueOf(partida[2]))
            );

            select_umedida[i] = StringHelper.verificarSelect(select_umedida[i]);
            arreglo[i] =
                "'"                +
                eliminado[i]       + "___" + 
                iddetalle[i]       + "___" + 
                idproducto[i]      + "___" + 
                id_presentacion[i] + "___" + 
                id_impuesto[i]     + "___" + 
                cantidad[i]        + "___" + 
                costo[i]           + "___" +
                valor_imp[i]       + "___" +
                noTr[i]            + "___" +
                seleccionado[i]    + "___" + 
                select_umedida[i]  + "___" + 
                idIeps[i]          + "___" + 
                tasaIeps[i]        + "___" + 
                vdescto[i]         + "___" + 
                idcot[i]           + "___" + 
                iddetcot[i]        + "___" +
                stat_reg           + "___" +
                precio_autorizado  + "___" +
                id_user_autoriza   + "___" +
                reqauth[i]         + "___" +
                salvar_registro[i] + "___" + 
                ret_id[i]          + "___" + 
                ret_tasa[i]        +
                "'";

            pedidoRequestBuilder.addGridDetalle(
                PedidoRequest.GridRenglonPedido.newBuilder()
                    .setId(Helper.toInt(iddetalle[i]))
                    .setToKeep(Helper.toInt(eliminado[i]))
                    .setInvProdId(Helper.toInt(idproducto[i]))
                    .setPresentacionId(Helper.toInt(id_presentacion[i]))
                    .setCantidad(Helper.toDouble(cantidad[i]))
                    .setPrecioUnitario(Helper.toDouble(costo[i]))
                    .setGralImpId(Helper.toInt(id_impuesto[i]))
                    .setValorImp(Helper.toDouble(valor_imp[i]))
                    .setInvProdUnidadId(Helper.toInt(select_umedida[i]))
                    .setGralIepsId(Helper.toInt(idIeps[i]))
                    .setValorIeps(Helper.toDouble(tasaIeps[i]))
                    .setDescto(Helper.toDouble(vdescto[i]))
                    .setCotId(Helper.toInt(idcot[i]))
                    .setCotDetalleId(Helper.toInt(iddetcot[i]))
                    .setRequiereAut(Boolean.parseBoolean(reqauth[i]))
                    .setAutorizado(Boolean.parseBoolean(stat_reg))
                    .setPrecioAut(Helper.toDouble(precio_autorizado))
                    .setGralUsrIdAut(Helper.toInt(id_user_autoriza))
                    .setGralImptosRetId(Helper.toInt(ret_id[i]))
                    .setTasaRet(Helper.toDouble(ret_tasa[i])));
        }

        //Serializar el arreglo
        String extra_data_array = StringUtils.join(arreglo, ",");

        PotCatCusorder pc = new PotCatCusorder();
        pc.matrix         = extra_data_array;
        pc.no_cot         = folio_cot;
        pc.usuario_id     = id_usuario.toString();
        pc.comments       = observaciones.toUpperCase();
        pc.salesman_id    = id_agente;
        pc.currency_val   = tipo_cambio;
        pc.purch_order    = orden_compra.toUpperCase();
        pc.tasaretimmex   = tasa_ret_immex;
        pc.date_lim       = fecha_compromiso;
        pc.customer_id    = id_cliente;
        pc.pedido_id      = id_pedido.toString();
        pc.trans          = transporte.toUpperCase();
        pc.delivery_place = lugar_entrega.toUpperCase();
        pc.allow_desc     = permitir_descto;
        pc.razon_desc     = motivo_descuento.toUpperCase();
        pc.perc_desc      = porcentaje_descto;

        if ( id_pedido == 0 ) {
            pc.cmd = "new";
        } else {
            if (accion_proceso.equals("cancelar")) {
                pc.cmd = accion_proceso;
            } else {
                pc.cmd = "edit";
            }
        }

        if (no_cuenta == null) {
            pc.account = "";
        } else {
            pc.account = no_cuenta;
        }

        if (select_forma_pago == null) {
            pc.forma_pago_id = "0";
        } else {
            pc.forma_pago_id = select_forma_pago.toString();
        }

        if (select_uso == null) {
            pc.uso_id = "0";
        } else {
            pc.uso_id = select_uso.toString();
        }

        if (select_metodo == null) {
            pc.met_pago_id = "0";
        } else {
            pc.met_pago_id = select_metodo.toString();
        }
        
        //Verificar valores
        pc.warehouse_id = StringHelper.verificarSelect(select_almacen);
        pc.currency_id = StringHelper.verificarSelect(select_moneda);
        pc.sup_credays_id = StringHelper.verificarSelect(select_condiciones);
        pc.send_route = StringHelper.verificarCheckBox(check_ruta);
        pc.send_comments = StringHelper.verificarCheckBox(check_enviar_obser);
        pc.flete_enable = StringHelper.verificarCheckBox(check_flete);

        if (id_df.equals("0")) {
            id_df = "1";//si viene cero, le asignamos uno para indicar que debe tomar la direccion de la tabla cxc_clie.
        }
        pc.cust_df_id = id_df;
        
        pedidoRequestBuilder
            .setUsuarioId(Helper.toInt(pc.usuario_id))
            .setAgenteId(Helper.toInt(pc.salesman_id))
            .setClienteId(Helper.toInt(pc.customer_id))
            .setClienteDfId(Helper.toInt(pc.cust_df_id))
            .setAlmacenId(Helper.toInt(pc.warehouse_id))
            .setMonedaId(Helper.toInt(pc.currency_id))
            .setProvCrediasId(Helper.toInt(pc.sup_credays_id))
            .setCfdiMetPagoId(Helper.toInt(pc.met_pago_id))
            .setFormaPagoId(Helper.toInt(pc.forma_pago_id))
            .setCfdiUsoId(Helper.toInt(pc.uso_id))
            .setPedidoId(Helper.toInt(pc.pedido_id))
            .setTasaRetencionImmex(Helper.toDouble(pc.tasaretimmex))
            .setTipoCambio(Helper.toDouble(pc.currency_val))
            .setPorcentajeDescto(Helper.toDouble(pc.perc_desc))
            .setDesctoAllowed(Boolean.parseBoolean(pc.allow_desc))
            .setEnviarObserFac(Boolean.parseBoolean(pc.send_comments))
            .setFleteEnabled(Boolean.parseBoolean(pc.flete_enable))
            .setEnviarRuta(Boolean.parseBoolean(pc.send_route))
            .setObservaciones(pc.comments)
            .setMotivoDescto(pc.razon_desc)
            .setTransporte(pc.trans)
            .setFechaCompromiso(pc.date_lim)
            .setLugarEntrega(pc.delivery_place)
            .setOrdenCompra(pc.purch_order)
            .setNumCuenta(pc.account)
            .setFolioCot(pc.no_cot);

        HashMap<String, String> success = this.getPocDao().poc_val_cusorder(
            new Integer(id_usuario),
            tipo_cambio,
            fecha_compromiso,
            select_forma_pago,
            no_cuenta,
            extra_data_array
        );
        log.log(Level.INFO, "Resultado de validacion Pedido: {0}", success.get("success"));

        if ( success.get("success").equals("true") ) {

            System.out.println(pc.conform_cat_store());

            ManagedChannel channel = ManagedChannelBuilder.forTarget(Helper.getGrpcConnString())
                .usePlaintext()
                .build();
            
            SalesGrpc.SalesBlockingStub blockingStub = SalesGrpc.newBlockingStub(channel);

            PedidoRequest pedidoRequest = pedidoRequestBuilder.build();
            PedidoResponse pedidoResponse;

            try {
                pedidoResponse = blockingStub.editPedido(pedidoRequest);
                String valorRetorno = pedidoResponse.getValorRetorno();

                if (valorRetorno.equals("1")) {
                    jsonretorno.put("success", "true");
                    jsonretorno.put("actualizo", valorRetorno);

                } else {
                    jsonretorno.put("success", valorRetorno);
                }
                log.log(Level.INFO, "Pedido Response valorRetorno: {0}", valorRetorno);
            
            } catch (StatusRuntimeException e) {
                jsonretorno.put("success", "Error en llamada a procedimiento remoto.");
                log.log(Level.SEVERE, "RPC failed: {0}", e.getStatus());
            
            } finally {
                try {
                    channel.shutdownNow().awaitTermination(5, TimeUnit.SECONDS);

                } catch (InterruptedException e) {
                    log.log(Level.SEVERE, "Channel shutdown failed.", e);
                }
            }
            
        } else {
            jsonretorno.put("success", success.get("success"));
        }

        return jsonretorno;
    }
    
    
    
    
    //Genera pdf de PEDIDOS
    @RequestMapping(value = "/get_genera_pdf_pedido/{id_pedido}/{iu}/out.json", method = RequestMethod.GET ) 
    public ModelAndView get_genera_pdf_pedidoJson(
                @PathVariable("id_pedido") Integer id_pedido,
                @PathVariable("iu") String id_user_cod,
                HttpServletRequest request, 
                HttpServletResponse response, 
                Model model
    )throws ServletException, IOException, URISyntaxException, DocumentException, Exception {
        
        HashMap<String, String> userDat = new HashMap<String, String>();
        HashMap<String, String> parametros = new HashMap<String, String>();
        HashMap<String, String> datosEncabezadoPie= new HashMap<String, String>();
        HashMap<String, String> datospedido_pdf = new HashMap<String, String>();
        ArrayList<HashMap<String, String>> conceptos_pedido = new ArrayList<HashMap<String, String>>();
        
        System.out.println("Generando PDF de Pedido");
        
        Integer app_selected = 64; //aplicativo Pedidos de clientes
        
        //decodificar id de usuario
        Integer id_usuario = Integer.parseInt(Base64Coder.decodeString(id_user_cod));
        
        userDat = this.getHomeDao().getUserById(id_usuario);
        Integer id_empresa = Integer.parseInt(userDat.get("empresa_id"));
        
        String razon_social_empresa = this.getGralDao().getRazonSocialEmpresaEmisora(id_empresa);
        String rfc_empresa=this.getGralDao().getRfcEmpresaEmisora(id_empresa);
        Integer id_sucursal = Integer.parseInt(userDat.get("sucursal_id"));
        datosEncabezadoPie.put("nombre_empresa_emisora", razon_social_empresa);
        datosEncabezadoPie.put("titulo_reporte", this.getGralDao().getTituloReporte(id_empresa, app_selected));
        datosEncabezadoPie.put("codigo1", this.getGralDao().getCodigo1Iso(id_empresa, app_selected));
        datosEncabezadoPie.put("codigo2", this.getGralDao().getCodigo2Iso(id_empresa, app_selected));
        
        //obtener el directorio temporal
        String dir_tmp = this.getGralDao().getTmpDir();
        
        String ruta_imagen = this.getGralDao().getImagesDir()+rfc_empresa+"_logo.png";
        
        File file_dir_tmp = new File(dir_tmp);
        
        //Aqui se obtienen los parametros de la facturacion, nos intersa el tipo de formato para el pdf del pedido
        parametros = this.getPocDao().getPocPedido_Parametros(id_empresa, id_sucursal);
        
        datospedido_pdf = this.getPocDao().getDatosPDF(id_pedido);
        conceptos_pedido = this.getPocDao().getPocPedido_DatosGrid(id_pedido);
        
        String municipio = this.getGralDao().getMunicipioSucursalEmisora(id_sucursal);
        String Estado = this.getGralDao().getEstadoSucursalEmisora(id_sucursal);
        
        String calle = this.getGralDao().getCalleDomicilioFiscalEmpresaEmisora(id_empresa);
        String numero= this.getGralDao().getNoExteriorDomicilioFiscalEmpresaEmisora(id_empresa);
        String colonia= this.getGralDao().getColoniaDomicilioFiscalEmpresaEmisora(id_empresa);
        String pais= this.getGralDao().getPaisDomicilioFiscalEmpresaEmisora(id_empresa);
        String cp= this.getGralDao().getCpDomicilioFiscalEmpresaEmisora(id_empresa);
        String rfc=this.getGralDao().getRfcEmpresaEmisora(id_empresa);
        String municipio_sucursal = this.getGralDao().getMunicipioSucursalEmisora(id_sucursal);
        String estado_sucursal = this.getGralDao().getEstadoSucursalEmisora(id_sucursal);
        
        
        datospedido_pdf.put("emisor_calle", calle);
        datospedido_pdf.put("emisor_numero", numero);
        datospedido_pdf.put("emisor_colonia", colonia);
        datospedido_pdf.put("emisor_expedidoen_municipio", municipio);
        datospedido_pdf.put("emisor_expedidoen_Estado", Estado);
        datospedido_pdf.put("emisor_pais", pais);
        datospedido_pdf.put("emisor_cp", cp);
        datospedido_pdf.put("emisor_rfc", rfc);
        datospedido_pdf.put("municipio_sucursal", municipio_sucursal);
        datospedido_pdf.put("estado_sucursal", estado_sucursal);
        
        //genera nombre del archivo
        String file_name = "PED_"+ rfc +"_"+ datospedido_pdf.get("folio") +".pdf";
        
        //ruta de archivo de salida
        String fileout = file_dir_tmp +"/"+  file_name;
        
        if (parametros.get("formato_pedido").equals("1")){
            //Formato 1 del Pedido. El Pedido abarca toda la hoja.
            PdfPocPedidoFormato1 x = new PdfPocPedidoFormato1(datosEncabezadoPie,datospedido_pdf,conceptos_pedido,razon_social_empresa,fileout,ruta_imagen);
        }else{
            //Formato 2 del Pedido. Solo abarca la mitad de la hoja y repite el mismo pedido en la segunda mitad.
            PdfPocPedidoFormato2 x2 = new PdfPocPedidoFormato2(datosEncabezadoPie,datospedido_pdf,conceptos_pedido,razon_social_empresa,fileout,ruta_imagen);
        }
        
        System.out.println("Recuperando archivo: " + fileout);
        File file = new File(fileout);
        int size = (int) file.length(); // Tamaño del archivo
        BufferedInputStream bis = new BufferedInputStream(new FileInputStream(file));
        response.setBufferSize(size);
        response.setContentLength(size);
        response.setContentType("application/pdf");
        response.setHeader("Content-Disposition","attachment; filename=\"" + file.getName() +"\"");
        FileCopyUtils.copy(bis, response.getOutputStream());          
        response.flushBuffer();
        
        if(file.exists()){
            FileHelper.delete(fileout);
        }
        
        return null;
        
    }
   
}
