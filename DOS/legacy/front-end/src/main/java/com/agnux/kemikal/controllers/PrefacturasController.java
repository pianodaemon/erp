/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.agnux.kemikal.controllers;

import com.agnux.cfd.v2.ArchivoInformeMensual;
import com.agnux.cfd.v2.Base64Coder;
import com.agnux.cfd.v2.BeanFacturador;
import com.agnux.cfdi.BeanFacturadorCfdi;
import com.agnux.cfdi.LegacyRequest;
import com.agnux.cfdi.timbre.BeanFacturadorCfdiTimbre;
import com.agnux.common.helpers.FileHelper;
import com.agnux.common.helpers.StringHelper;
import com.agnux.common.helpers.TimeHelper;
import com.agnux.common.obj.DataPost;
import com.agnux.common.obj.ResourceProject;
import com.agnux.common.obj.UserSessionData;
import com.agnux.kemikal.interfacedaos.FacturasInterfaceDao;
import com.agnux.kemikal.interfacedaos.GralInterfaceDao;
import com.agnux.kemikal.interfacedaos.HomeInterfaceDao;
import com.agnux.kemikal.interfacedaos.PrefacturasInterfaceDao;
import com.agnux.tcp.BbgumProxy;
import com.agnux.tcp.BbgumProxyError;
import com.maxima.bbgum.ServerReply;
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
import javax.servlet.http.HttpSession;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import com.maxima.sales.cli.grpc.PrefacturaRequest;
import com.maxima.sales.cli.grpc.PrefacturaResponse;
import com.maxima.sales.cli.grpc.SalesGrpc;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.StatusRuntimeException;

/**
 *
 * @author pianodaemon
 */
@Controller
@SessionAttributes({"user"})
@RequestMapping("/prefacturas/")
public class PrefacturasController {
    ResourceProject resource = new ResourceProject();
    private static final Logger log  = Logger.getLogger(PrefacturasController.class.getName());
    private Object outGoingCtrl = new Object();

    @Autowired
    @Qualifier("daoPrefactura")
    private PrefacturasInterfaceDao pdao;
    
    @Autowired
    @Qualifier("daoGral")
    private GralInterfaceDao gralDao;
    
    @Autowired
    @Qualifier("beanFacturador")
    BeanFacturador bfcfd;
    
    @Autowired
    @Qualifier("beanFacturadorCfdi")
    BeanFacturadorCfdi bfcfdi;
    
    @Autowired
    @Qualifier("beanFacturadorCfdiTf")
    BeanFacturadorCfdiTimbre bfCfdiTf;
    
    @Autowired
    @Qualifier("daoFacturas")
    private FacturasInterfaceDao facdao;
        
    @Autowired
    @Qualifier("daoHome")
    private HomeInterfaceDao HomeDao;
    

    
    public HomeInterfaceDao getHomeDao() {
        return HomeDao;
    }
    
    public PrefacturasInterfaceDao getPdao() {
        return pdao;
    }
    
    public GralInterfaceDao getGralDao() {
        return gralDao;
    }
    
    public FacturasInterfaceDao getFacdao() {
        return facdao;
    }
    
    public BeanFacturador getBfCfd() {
        return bfcfd;
    }
    
    public BeanFacturadorCfdi getBfCfdi() {
        return bfcfdi;
    }
    
    public BeanFacturadorCfdiTimbre getBfCfdiTf() {
        return bfCfdiTf;
    }

    
    @RequestMapping(value="/startup.agnux")
    public ModelAndView startUp(HttpServletRequest request, HttpServletResponse response, 
            @ModelAttribute("user") UserSessionData user
            )throws ServletException, IOException {
        
        log.log(Level.INFO, "Ejecutando starUp de {0}", PrefacturasController.class.getName());
        LinkedHashMap<String,String> infoConstruccionTabla = new LinkedHashMap<String,String>();
        
        infoConstruccionTabla.put("id", "Acciones:70");
        infoConstruccionTabla.put("cliente", "Cliente:320");
        infoConstruccionTabla.put("total", "Monto:100");
        infoConstruccionTabla.put("denominacion", "Moneda:70");
        infoConstruccionTabla.put("folio_pedido","Pedido:80");
        infoConstruccionTabla.put("oc","O.C.:80");
        infoConstruccionTabla.put("estado", "Estado:100");
        infoConstruccionTabla.put("fecha_creacion","Fecha creacion:110");
        
        ModelAndView x = new ModelAndView("prefacturas/startup", "title", "Facturaci&oacute;n");
        
        x = x.addObject("layoutheader", resource.getLayoutheader());
        x = x.addObject("layoutmenu", resource.getLayoutmenu());
        x = x.addObject("layoutfooter", resource.getLayoutfooter());
        x = x.addObject("grid", resource.generaGrid(infoConstruccionTabla));
        x = x.addObject("url", resource.getUrl(request));
        x = x.addObject("username", user.getUserName());
        x = x.addObject("empresa", user.getRazonSocialEmpresa());
        x = x.addObject("sucursal", user.getSucursal());
        
        String userId = String.valueOf(user.getUserId());
        
        //System.out.println("id_de_usuario: "+userId);
        
        String codificado = Base64Coder.encodeString(userId);
        
        //id de usuario codificado
        x = x.addObject("iu", codificado);
        
        return x;
    }
    
    
    
    
    
    @RequestMapping(value="/getPrefacturas.json", method = RequestMethod.POST)
    public @ResponseBody HashMap<String,ArrayList<HashMap<String, Object>>> getPrefacturasJson(
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
        
        //aplicativo Prefacturas(Facturacion)
        Integer app_selected = 13;
        
        //decodificar id de usuario
        Integer id_usuario = Integer.parseInt(Base64Coder.decodeString(id_user_cod));
        
        //variables para el buscador
        String folio_pedido = "%"+StringHelper.isNullString(String.valueOf(has_busqueda.get("folio_pedido")))+"%";
        String cliente = "%"+StringHelper.isNullString(String.valueOf(has_busqueda.get("cliente")))+"%";
        String fecha_inicial = ""+StringHelper.isNullString(String.valueOf(has_busqueda.get("fecha_inicial")))+"";
        String fecha_final = ""+StringHelper.isNullString(String.valueOf(has_busqueda.get("fecha_final")))+"";
        String codigo = "%"+StringHelper.isNullString(String.valueOf(has_busqueda.get("codigo")))+"%";
        String producto = "%"+StringHelper.isNullString(String.valueOf(has_busqueda.get("producto")))+"%";
        String agente = ""+StringHelper.isNullString(String.valueOf(has_busqueda.get("agente")))+"";
        
        String data_string = app_selected+"___"+id_usuario+"___"+cliente+"___"+fecha_inicial+"___"+fecha_final+"___"+codigo+"___"+producto+"___"+agente+"___"+folio_pedido;
        
        //obtiene total de registros en base de datos, con los parametros de busqueda
        int total_items = this.getPdao().countAll(data_string);
        
        //calcula el total de paginas
        int total_pags = resource.calculaTotalPag(total_items,items_por_pag);
        
        //variables que necesita el datagrid, para no tener que hacer uno por cada aplicativo
        DataPost dataforpos = new DataPost(orderby, desc, items_por_pag, pag_start, display_pag, input_json, cadena_busqueda,total_items,total_pags, id_user_cod);
        
        int offset = resource.__get_inicio_offset(items_por_pag, pag_start);
        
        //obtiene los registros para el grid, de acuerdo a los parametros de busqueda
        jsonretorno.put("Data", this.getPdao().getPrefacturas__PaginaGrid(data_string, offset, items_por_pag, orderby, desc));
        //obtiene el hash para los datos que necesita el datagrid
        jsonretorno.put("DataForGrid", dataforpos.formaHashForPos(dataforpos));
        
        return jsonretorno;
    }
    
    
    //obtiene los Agentes para el Buscador pricipal del Aplicativo
    @RequestMapping(method = RequestMethod.POST, value="/getAgentesParaBuscador.json")
    public @ResponseBody HashMap<String,ArrayList<HashMap<String, Object>>> getAgentesParaBuscador(
            @RequestParam(value="iu", required=true) String id_user_cod,
            Model model
        ) {
        
        HashMap<String,ArrayList<HashMap<String, Object>>> jsonretorno = new HashMap<String,ArrayList<HashMap<String, Object>>>();
        HashMap<String, String> userDat = new HashMap<String, String>();
        ArrayList<HashMap<String, Object>> agentes = new ArrayList<HashMap<String, Object>>();
        
        //decodificar id de usuario
        Integer id_usuario = Integer.parseInt(Base64Coder.decodeString(id_user_cod));
        userDat = this.getHomeDao().getUserById(id_usuario);
        Integer id_empresa = Integer.parseInt(userDat.get("empresa_id"));
        Integer id_sucursal = Integer.parseInt(userDat.get("sucursal_id"));
        
        agentes = this.getPdao().getVendedores(id_empresa, id_sucursal);
        
        jsonretorno.put("Agentes", agentes);
        return jsonretorno;
    }
    
    
    
    @RequestMapping(method = RequestMethod.POST, value="/getPrefactura.json")
    public @ResponseBody HashMap<String,ArrayList<HashMap<String, Object>>> getPrefacturaJson(
            @RequestParam(value="id_prefactura", required=true) Integer id_prefactura,
            @RequestParam(value="iu", required=true) String id_user,
            Model model
        ) {
        log.log(Level.INFO, "Ejecutando getPrefacturaJson de {0}", PrefacturasController.class.getName());
        HashMap<String,ArrayList<HashMap<String, Object>>> jsonretorno = new HashMap<String,ArrayList<HashMap<String, Object>>>();
        ArrayList<HashMap<String, Object>> datosPrefactura = new ArrayList<HashMap<String, Object>>();
        ArrayList<HashMap<String, Object>> datosGrid = new ArrayList<HashMap<String, Object>>();
        ArrayList<HashMap<String, Object>> datosAdenda = new ArrayList<HashMap<String, Object>>();
        ArrayList<HashMap<String, Object>> valorIva = new ArrayList<HashMap<String, Object>>();
        ArrayList<HashMap<String, Object>> arrayExtras = new ArrayList<HashMap<String, Object>>();
        HashMap<String, Object> extra = new HashMap<String, Object>();
        ArrayList<HashMap<String, Object>> parametros = new ArrayList<HashMap<String, Object>>();
        HashMap<String, String> userDat = new HashMap<String, String>();
        //ArrayList<HashMap<String, Object>> TMov = new ArrayList<HashMap<String, Object>>();
        
        //Decodificar id de usuario
        Integer id_usuario = Integer.parseInt(Base64Coder.decodeString(id_user));
        userDat = this.getHomeDao().getUserById(id_usuario);
        Integer id_empresa = Integer.parseInt(userDat.get("empresa_id"));
        Integer id_sucursal = Integer.parseInt(userDat.get("sucursal_id"));
        boolean incluirAdenda=false;
        
        parametros = this.getPdao().getFac_Parametros(id_sucursal);
        
        if( id_prefactura!=0  ){
            datosPrefactura = this.getPdao().getPrefactura_Datos(id_prefactura);
            datosGrid = this.getPdao().getPrefactura_DatosGrid(id_prefactura);
            
            //Verificar si hay que incluir adenda
            if (parametros.get(0).get("incluye_adenda").equals("true")){
                //Verificar si el cliente tiene asignada una adenda
                if(Integer.parseInt(String.valueOf(datosPrefactura.get(0).get("adenda_id")))>0){
                    //Obtener datos de la Adenda
                    datosAdenda = this.getPdao().getPrefactura_DatosAdenda(id_prefactura);
                    incluirAdenda=true;
                }
            }
        }
        
        valorIva= this.getFacdao().getValoriva(id_sucursal);
        extra.put("tipo_cambio", StringHelper.roundDouble(this.getFacdao().getTipoCambioActual(), 4)) ;
        extra.put("controlExiPres", userDat.get("control_exi_pres"));
        extra.put("validaPresPedido", parametros.get(0).get("validaPresPedido"));
        extra.put("adenda", String.valueOf(incluirAdenda));
        arrayExtras.add(0,extra);
        
        if(userDat.get("incluye_contab").toLowerCase().equals("true")){
            //Aplicacion 13=Prefacturas(Facturacion)
            jsonretorno.put("TMov", this.getFacdao().getCtb_TiposDeMovimiento(id_empresa, 13));
        }
        
        jsonretorno.put("datosPrefactura", datosPrefactura);
        jsonretorno.put("datosGrid", datosGrid);
        jsonretorno.put("datosAdenda", datosAdenda);
        jsonretorno.put("iva", valorIva);
        jsonretorno.put("Monedas", this.getPdao().getMonedas());
        jsonretorno.put("Extras", arrayExtras);
        jsonretorno.put("Vendedores", this.getPdao().getVendedores(id_empresa, id_sucursal));
        jsonretorno.put("Condiciones", this.getPdao().getCondiciones());
        jsonretorno.put("MetodosPago", this.getPdao().getMetodosPago(id_empresa));
        jsonretorno.put("Almacenes", this.getPdao().getAlmacenes(id_empresa));
        
        return jsonretorno;
    }
    
    
    
    
    
    
    //Obtiene datos para generador  de informe
    @RequestMapping(method = RequestMethod.POST, value="/datos_generador_informe.json")
    public @ResponseBody HashMap<String,ArrayList<HashMap<String, Integer>>> get_datos_generador_informeJson(Model model) {
        HashMap<String,ArrayList<HashMap<String, Integer>>> jsonretorno = new HashMap<String,ArrayList<HashMap<String, Integer>>>();
        jsonretorno.put("anioinforme", this.getPdao().getAnioInforme());
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
        
        userDat = this.getHomeDao().getUserById(id_usuario);
        
        Integer id_empresa = Integer.parseInt(userDat.get("empresa_id"));
        Integer id_sucursal = Integer.parseInt(userDat.get("sucursal_id"));
        
        
        jsonretorno.put("clientes", this.getPdao().get_buscador_clientes(cadena,filtro,id_empresa, id_sucursal));
        
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
        
        
        jsonretorno.put("Cliente", this.getPdao().getDatosClienteByNoCliente(no_control, id_empresa, id_sucursal));
        
        return jsonretorno;
    }
    
    
    
    //obtiene los tipos de productos para el buscador de productos
    @RequestMapping(method = RequestMethod.POST, value="/getProductoTipos.json")
    public @ResponseBody HashMap<String,ArrayList<HashMap<String, String>>> getProductoTiposJson(
            @RequestParam(value="iu", required=true) String id_user_cod,
            Model model
            ) {
        
        HashMap<String,ArrayList<HashMap<String, String>>> jsonretorno = new HashMap<String,ArrayList<HashMap<String, String>>>();
        
        ArrayList<HashMap<String, String>> arrayTiposProducto = new ArrayList<HashMap<String, String>>();
        arrayTiposProducto=this.getPdao().getProductoTipos();
        jsonretorno.put("prodTipos", arrayTiposProducto);
        
        return jsonretorno;
    }
    
    
    //Buscador de clientes
    @RequestMapping(method = RequestMethod.POST, value="/get_buscador_productos.json")
    public @ResponseBody HashMap<String,ArrayList<HashMap<String, String>>> get_buscador_productosJson(
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
        
        jsonretorno.put("productos", this.getPdao().getBuscadorProductos(sku,tipo,descripcion,id_empresa));
        
        return jsonretorno;
    }
    
    
    
    
    
    //Buscador de Remisiones del Cliente
    @RequestMapping(method = RequestMethod.POST, value="/getRemisionesCliente.json")
    public @ResponseBody HashMap<String,ArrayList<HashMap<String, String>>> getRemisionesClienteJson(
            @RequestParam(value="id_cliente", required=true) Integer id_cliente,
            Model model
            ) {
        
        HashMap<String,ArrayList<HashMap<String, String>>> jsonretorno = new HashMap<String,ArrayList<HashMap<String, String>>>();
        
        jsonretorno.put("Remisiones", this.getPdao().getRemisionesCliente(id_cliente));
        
        return jsonretorno;
    }
    
    
    
    
    //Obtiene los datos de la remision seleccionada para agregar al grid de productos de la factura
    @RequestMapping(method = RequestMethod.POST, value="/getDatosRemision.json")
    public @ResponseBody HashMap<String,ArrayList<HashMap<String, Object>>> getDatosRemisionJson(
            @RequestParam(value="id_remision", required=true) Integer id_remision,
            @RequestParam(value="iu", required=true) String id_user,
            Model model
        ) {
        
        HashMap<String,ArrayList<HashMap<String, Object>>> jsonretorno = new HashMap<String,ArrayList<HashMap<String, Object>>>();
        ArrayList<HashMap<String, Object>> datos_remision = new ArrayList<HashMap<String, Object>>();
        ArrayList<HashMap<String, Object>> detalles_remision = new ArrayList<HashMap<String, Object>>();
        ArrayList<HashMap<String, Object>> parametros = new ArrayList<HashMap<String, Object>>();
        //ArrayList<HashMap<String, Object>> pres_x_prod = new ArrayList<HashMap<String, Object>>();
        ArrayList<HashMap<String, Object>> arrayExtras = new ArrayList<HashMap<String, Object>>();
        HashMap<String, Object> extra = new HashMap<String, Object>();
        HashMap<String, String> userDat = new HashMap<String, String>();
       
        //decodificar id de usuario
        Integer id_usuario = Integer.parseInt(Base64Coder.decodeString(id_user));
        userDat = this.getHomeDao().getUserById(id_usuario);
        Integer id_sucursal = Integer.parseInt(userDat.get("sucursal_id"));
        boolean incluirAdenda=false;
        
        datos_remision = this.getPdao().getDatosRemision(id_remision);
        String permitir_descuento="false";
        if(datos_remision.get(0).get("pdescto").toString().toLowerCase().equals("true")){
            permitir_descuento="true";
        }
        
        detalles_remision = this.getPdao().getDetallesRemision(id_remision, permitir_descuento);
        //pres_x_prod = this.getPdao().getPresPorProdRemision(id_remision);
        
        parametros = this.getPdao().getFac_Parametros(id_sucursal);
        
        //Verificar si hay que incluir adenda
        if (parametros.get(0).get("incluye_adenda").equals("true")){
            //Verificar si el cliente tiene asignada una adenda
            if(Integer.parseInt(String.valueOf(datos_remision.get(0).get("adenda_id")))>0){
                incluirAdenda=true;
            }
        }
        
        extra.put("validaPresPedido", parametros.get(0).get("validaPresPedido"));
        extra.put("adenda", String.valueOf(incluirAdenda));
        arrayExtras.add(0,extra);
        
        jsonretorno.put("Datos", datos_remision);
        jsonretorno.put("Conceptos", detalles_remision);
        jsonretorno.put("RemExtra", arrayExtras);
        
        return jsonretorno;
    }
    
    
    
    
    //Busca precio unitario del producto si es que ha sido cotizado anteriormente
    @RequestMapping(method = RequestMethod.POST, value="/get_precio_unitario.json")
    public @ResponseBody HashMap<String,HashMap<String, Object>> getPrecioUnitarioProductoJson(
            @RequestParam(value="id_cliente", required=true) Integer id_cliente,
            @RequestParam(value="id_producto", required=true) Integer id_producto,
            @RequestParam(value="id_pres", required=true) Integer id_pres,
            @RequestParam(value="iu", required=true) String id_user,
            Model model
        ) {
        
        HashMap<String,HashMap<String, Object>> jsonretorno = new HashMap<String,HashMap<String, Object>>();
        HashMap<String, String> userDat = new HashMap<String, String>();
        
        //decodificar id de usuario
        Integer id_usuario = Integer.parseInt(Base64Coder.decodeString(id_user));
        //System.out.println("id_usuario: "+id_usuario);
        
        userDat = this.getHomeDao().getUserById(id_usuario);
        Integer id_empresa = Integer.parseInt(userDat.get("empresa_id"));
        Integer id_sucursal = Integer.parseInt(userDat.get("sucursal_id"));
        
        HashMap<String, Object> pu = this.getPdao().getPrecioUnitario(id_cliente,id_producto,id_pres,id_empresa,id_sucursal);
        
        jsonretorno.put("Pu", pu);
        
        return jsonretorno;
    }
    
    
    private String editarPrefactura(PrefacturaRequest prefacturaRequest) {

        ManagedChannel channel = ManagedChannelBuilder.forTarget(Helper.getGrpcConnString("SALES"))
            .usePlaintext()
            .build();

        SalesGrpc.SalesBlockingStub blockingStub = SalesGrpc.newBlockingStub(channel);

        PrefacturaResponse prefacturaResponse;
        String valorRetorno = "0:";

        try {
            prefacturaResponse = blockingStub.editPrefactura(prefacturaRequest);
            valorRetorno = prefacturaResponse.getValorRetorno();
            log.log(Level.INFO, "Prefactura edit Response valorRetorno: {0}", valorRetorno);

        } catch (StatusRuntimeException e) {
            valorRetorno += "Error en llamada a procedimiento remoto.";
            log.log(Level.SEVERE, "gRPC failed: {0}", e.getStatus());

        } finally {
            try {
                channel.shutdownNow().awaitTermination(5, TimeUnit.SECONDS);

            } catch (InterruptedException e) {
                log.log(Level.SEVERE, "Channel shutdown failed.", e);
            }
        }

        return valorRetorno;
    }
    
    //edicion y nuevo
    @RequestMapping(method = RequestMethod.POST, value="/edit.json")
    public @ResponseBody HashMap<String, String> editJson(
        @RequestParam(value="id_prefactura", required=true) Integer id_prefactura,
        @RequestParam(value="select_tipo_documento", required=true) Integer select_tipo_documento,
        @RequestParam(value="id_cliente", required=true) String id_cliente,
        @RequestParam(value="rfc", required=true) String rfc,
        @RequestParam(value="moneda", required=true) String id_moneda,
        @RequestParam(value="moneda_original", required=true) String id_moneda_original,
        @RequestParam(value="tipo_cambio", required=true) String tipo_cambio_vista,
        @RequestParam(value="observaciones", required=true) String observaciones,
        @RequestParam(value="total_tr", required=true) String total_tr,
        @RequestParam(value="vendedor", required=true) String id_vendedor,
        @RequestParam(value="condiciones", required=true) String id_condiciones,
        @RequestParam(value="orden_compra", required=true) String orden_compra,
        @RequestParam(value="refacturar", required=true) String refacturar,
        @RequestParam(value="accion", required=true) String accion,
        @RequestParam(value="select_metodo_pago", required=true) String id_metodo_pago,
        @RequestParam(value="no_cuenta", required=false) String no_cuenta,
        @RequestParam(value="folio_pedido", required=false) String folio_pedido,
        @RequestParam(value="tasa_ret_immex", required=false) String tasa_ret_immex,
        @RequestParam(value="select_almacen", required=false) String select_almacen,
        @RequestParam(value="select_tmov", required=false) String select_tmov,
        @RequestParam(value="pdescto", required=true) String permitir_descto,

        //Estos son para datos de la Adenda
        @RequestParam(value="campo1", required=true) String campo_adenda1,
        @RequestParam(value="campo2", required=true) String campo_adenda2,
        @RequestParam(value="campo3", required=true) String campo_adenda3,
        @RequestParam(value="campo4", required=true) String campo_adenda4,
        @RequestParam(value="campo5", required=true) String campo_adenda5,
        @RequestParam(value="campo6", required=true) String campo_adenda6,
        @RequestParam(value="campo7", required=true) String campo_adenda7,
        @RequestParam(value="campo8", required=true) String campo_adenda8,

        @RequestParam(value="eliminado", required=false) String[] eliminado,
        @RequestParam(value="iddetalle", required=false) String[] iddetalle,
        @RequestParam(value="idproducto", required=false) String[] idproducto,
        @RequestParam(value="idUnidad", required=false) String[] idUnidad,
        @RequestParam(value="id_presentacion", required=false) String[] id_presentacion,
        @RequestParam(value="id_imp_prod", required=false) String[] id_impuesto,
        @RequestParam(value="valor_imp", required=false) String[] valor_imp,
        @RequestParam(value="cantidad", required=false) String[] cantidad,
        @RequestParam(value="costo_promedio", required=false) String[] costo_promedio,
        @RequestParam(value="costo", required=false) String[] costo,
        @RequestParam(value="idIeps", required=false) String[] idIeps,
        @RequestParam(value="tasaIeps", required=false) String[] tasaIeps,
        @RequestParam(value="vdescto", required=false) String[] vdescto,
        @RequestParam(value="ret_id", required=false) String[] ret_id,
        @RequestParam(value="ret_tasa", required=false) String[] ret_tasa,

        @RequestParam(value="id_remision", required=false) String[] id_remision,
        @RequestParam(value="id_df", required=false) String id_df,

        @ModelAttribute("user") UserSessionData user) throws Exception
    {
        
        HashMap<String, String> jsonretorno = new HashMap<String, String>();

        String valorRespuesta = "false";
        String msjRespuesta = "";
        Integer app_selected = 13;
        String command_selected = "";
        String actualizo = "0";
        String retorno = "";
        String folio = "";

        //Variable para el id  del usuario
        Integer id_usuario = user.getUserId();

        String arreglo[];
        arreglo = new String[eliminado.length];
        
        PrefacturaRequest.Builder prefacturaRequestBuilder = PrefacturaRequest.newBuilder();
        
        for (int i = 0; i < eliminado.length; i++) {

            String _costo = StringHelper.removerComas(costo[i]);

            arreglo[i] = "'" + eliminado[i] + "___"
                    + iddetalle[i] + "___"
                    + idproducto[i] + "___"
                    + id_presentacion[i] + "___"
                    + id_impuesto[i] + "___"
                    + cantidad[i] + "___"
                    + _costo + "___"
                    + valor_imp[i] + "___"
                    + id_remision[i] + "___"
                    + costo_promedio[i] + "___"
                    + idUnidad[i] + "___"
                    + idIeps[i] + "___"
                    + tasaIeps[i] + "___"
                    + vdescto[i] + "___"
                    + ret_id[i] + "___"
                    + ret_tasa[i] + "'";

            prefacturaRequestBuilder.addGridDetalle(
                PrefacturaRequest.GridRenglonPrefactura.newBuilder()
                    .setToKeep(Helper.toInt(eliminado[i]))
                    .setId(Helper.toInt(iddetalle[i]))
                    .setProductoId(Helper.toInt(idproducto[i]))
                    .setPresentacionId(Helper.toInt(id_presentacion[i]))
                    .setTipoImpuestoId(Helper.toInt(id_impuesto[i]))
                    .setCantidad(Helper.toDouble(cantidad[i]))
                    .setPrecioUnitario(Helper.toDouble(_costo))
                    .setValorImp(Helper.toDouble(valor_imp[i]))
                    .setRemisionId(Helper.toInt(id_remision[i]))
                    .setCostoPromedio(Helper.toDouble(costo_promedio[i]))
                    .setInvProdUnidadId(Helper.toInt(idUnidad[i]))
                    .setGralIepsId(Helper.toInt(idIeps[i]))
                    .setValorIeps(Helper.toDouble(tasaIeps[i]))
                    .setDescto(Helper.toDouble(vdescto[i]))
                    .setGralImptosRetId(Helper.toInt(ret_id[i]))
                    .setTasaRet(Helper.toDouble(ret_tasa[i]))
            );
        }
        //serializar el arreglo
        String extra_data_array = StringUtils.join(arreglo, ",");

        command_selected = accion;

        if (no_cuenta == null) no_cuenta = "";
        if (id_df.equals("0")) id_df = "1";

        select_tmov = StringHelper.verificarSelect(select_tmov);
        
        String data_string =
            app_selected                + "___" +
            command_selected            + "___" +
            id_usuario                  + "___" +
            id_prefactura               + "___" +
            id_cliente                  + "___" +
            id_moneda                   + "___" +
            observaciones.toUpperCase() + "___" +
            tipo_cambio_vista           + "___" +
            id_vendedor                 + "___" +
            id_condiciones              + "___" +
            orden_compra.toUpperCase()  + "___" +
            refacturar                  + "___" +
            id_metodo_pago              + "___" +
            no_cuenta                   + "___" +
            select_tipo_documento       + "___" +
            folio_pedido                + "___" +
            select_almacen              + "___" +
            id_moneda_original          + "___" +
            id_df                       + "___" +
            campo_adenda1.toUpperCase() + "___" +
            campo_adenda2.toUpperCase() + "___" +
            campo_adenda3               + "___" +
            campo_adenda4.toUpperCase() + "___" +
            campo_adenda5.toUpperCase() + "___" +
            campo_adenda6.toUpperCase() + "___" +
            campo_adenda7.toUpperCase() + "___" +
            campo_adenda8.toUpperCase() + "___" +
            rfc.toUpperCase().trim()    + "___" +
            permitir_descto             + "___" +
            select_tmov;

        prefacturaRequestBuilder
            .setUsuarioId(id_usuario.intValue())
            .setPrefacturaId(id_prefactura.intValue())
            .setClienteId(Helper.toInt(id_cliente))
            .setMonedaId(Helper.toInt(id_moneda))
            .setObservaciones(observaciones.toUpperCase())
            .setTipoCambio(Helper.toDouble(tipo_cambio_vista))
            .setVendedorId(Helper.toInt(id_vendedor))
            .setCondicionesId(Helper.toInt(id_condiciones))
            .setOrdenCompra(orden_compra.toUpperCase())
            .setRefacturar(Boolean.parseBoolean(refacturar))
            .setMetodoPagoId(Helper.toInt(id_metodo_pago))
            .setNoCuenta(no_cuenta)
            .setTipoDocumento(select_tipo_documento.intValue())
            .setMonedaOriginalId(Helper.toInt(id_moneda_original))
            .setAdenda1(campo_adenda1.toUpperCase())
            .setAdenda2(campo_adenda2.toUpperCase())
            .setAdenda3(campo_adenda3)
            .setAdenda4(campo_adenda4.toUpperCase())
            .setAdenda5(campo_adenda5.toUpperCase())
            .setAdenda6(campo_adenda6.toUpperCase())
            .setAdenda7(campo_adenda7.toUpperCase())
            .setAdenda8(campo_adenda8.toUpperCase())
            .setPermitirDescto(Boolean.parseBoolean(permitir_descto));

        HashMap<String, String> success = this.getPdao()
            .selectFunctionValidateAaplicativo(data_string, app_selected, extra_data_array);
        
        log.log(Level.INFO, TimeHelper.getFechaActualYMDH()+"Resultado de validacion {0}", success.get("success"));

        if (success.get("success").equals("true")) {

            retorno = editarPrefactura(prefacturaRequestBuilder.build());

            //retorna un 1, si se  actualizo correctamente
            actualizo = retorno.split(":")[0];

            if (select_tipo_documento == 2) {
                //cuando es remision aqui retorna el folio de la remision
                folio = retorno.split(":")[1];
                jsonretorno.put("folio", folio);
            }

            jsonretorno.put("actualizo", actualizo);
        }

        // Workaround to avoid race conditions when requesting serie and folio
        synchronized (this.outGoingCtrl) {

            if (actualizo.equals("1")) {

                if (!accion.equals("new")) {
                    //select_tipo_documento 1=Factura, 3=Factura de Remision
                    if (select_tipo_documento == 1 || select_tipo_documento == 3) {
                        Logger.getLogger(PrefacturasController.class.getName()).log(
                                Level.INFO, "::::::::::::Iniciando Facturacion:::::::::::::::::..");

                        //Numero de Identificacion unica de la Empresa
                        HashMap<String, String> userDat = this.getHomeDao().getUserById(id_usuario);
                        Integer id_empresa = Integer.parseInt(userDat.get("empresa_id"));
                        String no_id = this.getGralDao().getNoIdEmpresa(id_empresa);
                        String serieFolio = this.getFacdao().q_serie_folio(id_usuario);

                        String filename = no_id + "_" + serieFolio + ".xml";
                        BbgumProxy bbgumProxy = new BbgumProxy();
                        LegacyRequest req = new LegacyRequest();

                        req.sendTo("cxc");
                        req.from("webui");
                        req.action("facturar");
                        HashMap<String, String> kwargs = new HashMap<>();
                        kwargs.put("filename", filename);
                        kwargs.put("usr_id", id_usuario.toString());
                        kwargs.put("prefact_id", id_prefactura.toString());
                        req.args(kwargs);

                        try {
                            String[] connParams = Helper.getCfdiengineConnParams();
                            ServerReply reply = bbgumProxy.uploadBuff(connParams[0], Integer.parseInt(connParams[1]), req.getJson().getBytes());
                            String msg = "core reply code: " + reply.getReplyCode();
                            if (reply.getReplyCode() == 0) {
                                Logger.getLogger(PrefacturasController.class.getName()).log(
                                        Level.INFO, msg);
                                jsonretorno.put("folio", serieFolio);
                                msjRespuesta = "Se gener&oacute; la Factura: " + serieFolio;
                                valorRespuesta = "true";
                            } else {
                                Logger.getLogger(PrefacturasController.class.getName()).log(
                                        Level.WARNING, msg);
                                valorRespuesta = "false";
                                msjRespuesta = msg;
                            }
                        } catch (BbgumProxyError ex) {
                            Logger.getLogger(PrefacturasController.class.getName()).log(
                                    Level.WARNING, ex.getMessage());
                            valorRespuesta = "false";
                            msjRespuesta = ex.getMessage();
                        }

                    } else {
                        valorRespuesta = "true";
                        msjRespuesta = "Se gener&oacute; la Remisi&oacute;n con Folio: " + jsonretorno.get("folio");
                    }

                } else {
                    valorRespuesta = "true";
                    msjRespuesta = "El registro se gener&oacute; con &eacute;xito, puede proceder a Facturar.";
                }

                System.out.println("Folio: " + jsonretorno.get("folio"));

            } else {
                if (actualizo.equals("0")) {
                    jsonretorno.put("actualizo", actualizo);
                    msjRespuesta = retorno.split(":")[1];
                }
            }
        }
        jsonretorno.put("success", success.get("success"));
        jsonretorno.put("valor", valorRespuesta);
        jsonretorno.put("msj", msjRespuesta);

        Logger.getLogger(PrefacturasController.class.getName()).log(
                Level.INFO, "Validacion: " + jsonretorno.get("success"));
        Logger.getLogger(PrefacturasController.class.getName()).log(
                Level.INFO, "valorRespuesta: " + valorRespuesta);
        Logger.getLogger(PrefacturasController.class.getName()).log(
                Level.INFO, "msjRespuesta: " + msjRespuesta);
        return jsonretorno;
    }
    
    //este metodo no se esta utilizando, no se puede borrar una prefactura
    //cambiar a borrado logico un registro
    @RequestMapping(method = RequestMethod.POST, value="/logicDelete.json")
    public @ResponseBody HashMap<String, String> logicDeleteJson(
            @RequestParam(value="id_prefactura", required=true) String id_prefactura,
            @RequestParam(value="iu", required=true) String id_user,
            Model model, HttpSession session
            ) {
        
        System.out.println("Borrado logico de prefactura");
        //decodificar id de usuario
        Integer id_usuario = Integer.parseInt(Base64Coder.decodeString(id_user));
        System.out.println("id_usuario: "+id_usuario);
        
        Integer app_selected = 13;
        String command_selected = "delete";
        String extra_data_array = "'sin datos'";
        
        String data_string = app_selected+"___"+command_selected+"___"+id_usuario+"___"+id_prefactura;
        
        HashMap<String, String> jsonretorno = new HashMap<String, String>();
        jsonretorno.put("success",String.valueOf( this.getPdao().selectFunctionForThisApp(data_string,extra_data_array)) );
        
        return jsonretorno;
    }
    
    
    
    
    
    //Reporte Mensual SAT (Genera txt)
    @RequestMapping(value = "/get_genera_txt_reporte_mensual_sat/{month}/{year}/{iu}/out.json", method = RequestMethod.GET ) 
    public ModelAndView get_genera_txt_reporte_mensual_sat(
            @PathVariable("year") String year, 
            @PathVariable("month") String month,
            @PathVariable("iu") String id_user,
            HttpServletRequest request, 
            HttpServletResponse response, 
            Model model) throws ServletException, IOException, URISyntaxException {
        
        HashMap<String, String> userDat = new HashMap<String, String>();
        
        //decodificar id de usuario
        Integer id_usuario = Integer.parseInt(Base64Coder.decodeString(id_user));
        //System.out.println("id_usuario: "+id_usuario);
        
        userDat = this.getHomeDao().getUserById(id_usuario);
        Integer id_empresa = Integer.parseInt(userDat.get("empresa_id"));
        
        
        
        
        ArchivoInformeMensual aim = new ArchivoInformeMensual();
        
        String nombre_txt = aim.generaNombreArchivoInformeMensual("1",this.getGralDao().getRfcEmpresaEmisora(id_empresa),month,year,"txt");
        String fileout = this.getGralDao().getTmpDir() + nombre_txt;
         File toFile = new File(fileout);
    	if (toFile.exists()) {
            //si el archivo ya esxiste, es eliminado
            toFile.delete();
        }
        
        
        ArrayList<HashMap<String, Object>> valor_emitidos = this.getFacdao().getComprobantesActividadPorMes(year, month,id_empresa);
        
        for(HashMap<String,Object> iteradorX : valor_emitidos){
            String renglon = aim.generarRegistroPorRenglonParaArchivoInformeMensual(
                    String.valueOf(iteradorX.get("rfc_cliente")),
                    String.valueOf(iteradorX.get("serie")),
                    String.valueOf(iteradorX.get("folio_del_comprobante_fiscal")),
                    String.valueOf(iteradorX.get("numero_de_aprobacion")),
                    String.valueOf(iteradorX.get("momento_expedicion")),
                    String.valueOf(iteradorX.get("monto_de_la_operacion")),
                    String.valueOf(iteradorX.get("monto_del_impuesto")),
                    String.valueOf(iteradorX.get("estado_del_comprobante")),
                    String.valueOf(iteradorX.get("efecto_de_comprobante")),
                    String.valueOf(iteradorX.get("pedimento")),
                    String.valueOf(iteradorX.get("fecha_de_pedimento")),
                    String.valueOf(iteradorX.get("aduana")),
                    String.valueOf(iteradorX.get("anoaprovacion")));
            
            if(!renglon.isEmpty()){
                //FileHelper.addText2File(System.getProperty("java.io.tmpdir")+ "/" + nombre_txt,renglon);
                FileHelper.addText2File(this.getGralDao().getTmpDir() + nombre_txt,renglon);
            }
	}
        
        //String fileout = System.getProperty("java.io.tmpdir")+ "/" + nombre_txt;
        
        System.out.println("Recuperando archivo: " + fileout);
        
        File file = new File(fileout);
        if (file.exists()==false){
            System.out.println("No hay facturas en este mes");
            FileHelper.addText2File(this.getGralDao().getTmpDir() + nombre_txt,"");
        }
        
        int size = (int) file.length(); // Tamaño del archivo
        BufferedInputStream bis = new BufferedInputStream(new FileInputStream(file));
        response.setBufferSize(size);
        response.setContentLength(size);
        response.setContentType("text/plain");
        response.setHeader("Content-Disposition","attachment; filename=\"" + file.getName() +"\"");
        FileCopyUtils.copy(bis, response.getOutputStream());
        response.flushBuffer();
        
        return null;
    }
    
}

