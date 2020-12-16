package com.agnux.kemikal.controllers;


import com.agnux.cfd.v2.Base64Coder;
import com.agnux.cfdi.LegacyRequest;
import com.agnux.common.helpers.FileHelper;
import com.agnux.common.helpers.StringHelper;
import com.agnux.common.obj.DataPost;
import com.agnux.common.obj.ResourceProject;
import com.agnux.common.obj.UserSessionData;
import com.agnux.kemikal.interfacedaos.CxcInterfaceDao;
import com.agnux.kemikal.interfacedaos.GralInterfaceDao;
import com.agnux.kemikal.interfacedaos.HomeInterfaceDao;
import com.agnux.kemikal.reportes.PdfDepositos;
import com.agnux.kemikal.reportes.PdfReporteAplicacionPago;
import com.agnux.tcp.BbgumProxy;
import com.agnux.tcp.BbgumProxyError;
import com.itextpdf.text.DocumentException;
import com.maxima.bbgum.ServerReply;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Calendar;
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
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttributes;


@Controller
@SessionAttributes({"user"})
@RequestMapping("/carteras/")
public class CarterasController {
    private static final Logger log  = Logger.getLogger(CarterasController.class.getName());
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

        log.log(Level.INFO, "Ejecutando starUp de {0}", CarterasController.class.getName());
        LinkedHashMap<String,String> infoConstruccionTabla = new LinkedHashMap<String,String>();

        infoConstruccionTabla.put("id", "Acciones:70");
        infoConstruccionTabla.put("numero_transaccion", "No.&nbsp;Transacci&oacute;n:110");
        infoConstruccionTabla.put("total", "Monto pago:100");
        infoConstruccionTabla.put("cliente", "Cliente:280");
        infoConstruccionTabla.put("forma_pago", "Forma&nbsp;Pago:100");
        infoConstruccionTabla.put("moneda", "Moneda:60");
        infoConstruccionTabla.put("fecha_deposito", "Fecha&nbsp;Dep&oacute;sito:100");
        infoConstruccionTabla.put("estado", "Estado:90");

        ModelAndView x = new ModelAndView("carteras/startup", "title", "Carteras");

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
    @RequestMapping(value="/getPagos.json", method = RequestMethod.POST)
    public @ResponseBody HashMap<String,ArrayList<HashMap<String, Object>>> getPagosJson(
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

        //aplicativo de carteras
        Integer app_selected = 14;

        //decodificar id de usuario
        Integer id_usuario = Integer.parseInt(Base64Coder.decodeString(id_user_cod));

        //variables para el buscador
        String num_transaccion = StringHelper.isNullString(String.valueOf(has_busqueda.get("num_transaccion")));
        String factura = "%"+StringHelper.isNullString(String.valueOf(has_busqueda.get("factura")))+"%";
        String cliente = "%"+StringHelper.isNullString(String.valueOf(has_busqueda.get("cliente")))+"%";
        String fecha_inicial = ""+StringHelper.isNullString(String.valueOf(has_busqueda.get("fecha_inicial")))+"";
        String fecha_final = ""+StringHelper.isNullString(String.valueOf(has_busqueda.get("fecha_final")))+"";

        String data_string = app_selected+"___"+id_usuario+"___"+num_transaccion+"___"+factura+"___"+cliente+"___"+fecha_inicial+"___"+fecha_final;

        //obtiene total de registros en base de datos, con los parametros de busqueda
        int total_items = this.getCxcDao().countAll(data_string);

        //calcula el total de paginas
        int total_pags = resource.calculaTotalPag(total_items,items_por_pag);

        //variables que necesita el datagrid, para no tener que hacer uno por cada aplicativo
        DataPost dataforpos = new DataPost(orderby, desc, items_por_pag, pag_start, display_pag, input_json, cadena_busqueda,total_items,total_pags,id_user_cod);

        int offset = resource.__get_inicio_offset(items_por_pag, pag_start);

        //obtiene los registros para el grid, de acuerdo a los parametros de busqueda
        jsonretorno.put("Data", this.getCxcDao().getCartera_PaginaGrid(data_string, offset, items_por_pag, orderby, desc));
        //obtiene el hash para los datos que necesita el datagrid
        jsonretorno.put("DataForGrid", dataforpos.formaHashForPos(dataforpos));

        return jsonretorno;
    }


    @RequestMapping(method = RequestMethod.POST, value="/getCartera.json")
    public @ResponseBody HashMap<String,ArrayList<HashMap<String, Object>>> getCarteraJson(
            @RequestParam(value="id", required=true) Integer id,
            @RequestParam(value="iu", required=true) String id_user_cod,
            Model model
            ) {

        log.log(Level.INFO, "Ejecutando getCarteraJson de {0}", CarterasController.class.getName());
        HashMap<String,ArrayList<HashMap<String, Object>>> jsonretorno = new HashMap<String,ArrayList<HashMap<String, Object>>>();
        ArrayList<HashMap<String, Object>> tipoMovimeinto = new ArrayList<HashMap<String, Object>>();
        ArrayList<HashMap<String, Object>> formaPago = new ArrayList<HashMap<String, Object>>();
        ArrayList<HashMap<String, Object>> monedas = new ArrayList<HashMap<String, Object>>();
        ArrayList<HashMap<String, Object>> bancos = new ArrayList<HashMap<String, Object>>();
        ArrayList<HashMap<String, Object>> bancosEmpresa = new ArrayList<HashMap<String, Object>>();
        ArrayList<HashMap<String, Object>> tipoCambio = new ArrayList<HashMap<String, Object>>();
        HashMap<String, String> userDat = new HashMap<String, String>();

        //decodificar id de usuario
        Integer id_usuario = Integer.parseInt(Base64Coder.decodeString(id_user_cod));
        //System.out.println("id_usuario: "+id_usuario);

        userDat = this.getHomeDao().getUserById(id_usuario);

        Integer id_empresa = Integer.parseInt(userDat.get("empresa_id"));
        Integer id_sucursal = Integer.parseInt(userDat.get("sucursal_id"));

        tipoMovimeinto = this.getCxcDao().getCartera_TipoMovimiento();
        formaPago = this.getCxcDao().getCartera_FormasPago();
        monedas = this.getCxcDao().getMonedas();
        bancos = this.getCxcDao().getBancos(id_empresa);
        bancosEmpresa = this.getCxcDao().getCartera_BancosEmpresa(id_empresa);
        tipoCambio = this.getCxcDao().getTipoCambioActual();

        jsonretorno.put("tipo_mov", tipoMovimeinto);
        jsonretorno.put("Monedas", monedas);
        jsonretorno.put("Formaspago", formaPago);
        jsonretorno.put("Bancos", bancos);
        jsonretorno.put("Bancos_kemikal", bancosEmpresa);
        jsonretorno.put("Tipocambio", tipoCambio);

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
    
    
    //Obtiene cuentas
    @RequestMapping(method = RequestMethod.POST, value="/getCuentas.json")
    public @ResponseBody HashMap<String,ArrayList<HashMap<String, Object>>> get_cuentasJson(
            @RequestParam(value="id_moneda", required=true) Integer id_moneda,
            @RequestParam(value="iu", required=true) String id_user_cod,
            Model model
            ) {
        
        HashMap<String,ArrayList<HashMap<String, Object>>> jsonretorno = new HashMap<String,ArrayList<HashMap<String, Object>>>();
        ArrayList<HashMap<String, Object>> bancos = new ArrayList<HashMap<String, Object>>();
        Integer id_banco=0;
        ArrayList<HashMap<String, Object>> cuentas = new ArrayList<HashMap<String, Object>>();
        HashMap<String, String> userDat = new HashMap<String, String>();
        
        //decodificar id de usuario
        Integer id_usuario = Integer.parseInt(Base64Coder.decodeString(id_user_cod));
        //System.out.println("id_usuario: "+id_usuario);
        
        userDat = this.getHomeDao().getUserById(id_usuario);
        
        Integer id_empresa = Integer.parseInt(userDat.get("empresa_id"));
        Integer id_sucursal = Integer.parseInt(userDat.get("sucursal_id"));
        
        bancos = this.getCxcDao().getCartera_BancosEmpresa(id_empresa);
        id_banco = Integer.parseInt(bancos.get(0).get("id").toString());
        cuentas = this.getCxcDao().getCartera_CtaBanco(id_moneda,id_banco);
        
        jsonretorno.put("Cuentas",cuentas);
        return jsonretorno;
    }
    
    
    //Obtiene bancos por moneda
    @RequestMapping(method = RequestMethod.POST, value="/obtener_bancos_moneda_kemikal.json")
    public @ResponseBody HashMap<String,ArrayList<HashMap<String, Object>>> get_bancos_moneda_kemikalJson(
            @RequestParam(value="id_moneda", required=true) Integer id_moneda,
            @RequestParam(value="iu", required=true) String id_user_cod,
            Model model
            ) {
        
        HashMap<String,ArrayList<HashMap<String, Object>>> jsonretorno = new HashMap<String,ArrayList<HashMap<String, Object>>>();
        ArrayList<HashMap<String, Object>> bancos = new ArrayList<HashMap<String, Object>>();
        HashMap<String, String> userDat = new HashMap<String, String>();
        
        //decodificar id de usuario
        Integer id_usuario = Integer.parseInt(Base64Coder.decodeString(id_user_cod));
        //System.out.println("id_usuario: "+id_usuario);
        
        userDat = this.getHomeDao().getUserById(id_usuario);
        Integer id_empresa = Integer.parseInt(userDat.get("empresa_id"));
        
        bancos = this.getCxcDao().getCartera_BancosXMoneda(id_moneda, id_empresa);
        
        jsonretorno.put("Bancos_moneda", bancos);
        return jsonretorno;
    }
    
    
    
    //Obtiene numeros de cuentas
    @RequestMapping(method = RequestMethod.POST, value="/obtener_numero_de_cuenta.json")
    public @ResponseBody HashMap<String,ArrayList<HashMap<String, Object>>> get_obtener_numero_de_cuentaJson(
            @RequestParam(value="id_banco", required=true) Integer id_banco,
            @RequestParam(value="id_moneda", required=true) Integer id_moneda,
            Model model
            ) {
        
        HashMap<String,ArrayList<HashMap<String, Object>>> jsonretorno = new HashMap<String,ArrayList<HashMap<String, Object>>>();
        ArrayList<HashMap<String, Object>> bancos = new ArrayList<HashMap<String, Object>>();
        
        bancos = this.getCxcDao().getCartera_CtaBanco(id_moneda, id_banco);
        
        jsonretorno.put("Cuentas", bancos);
        return jsonretorno;
    }


    //Obtiene numeros de cuentas
    @RequestMapping(method = RequestMethod.POST, value="/obtener_anticipos.json")
    public @ResponseBody HashMap<String,ArrayList<HashMap<String, Object>>> getObtenerAanticiposJson(
            @RequestParam(value="id_cliente", required=true) Integer id_cliente,
            Model model
            ) {

        HashMap<String,ArrayList<HashMap<String, Object>>> jsonretorno = new HashMap<String,ArrayList<HashMap<String, Object>>>();
        ArrayList<HashMap<String, Object>> sumanticipos_mn = new ArrayList<HashMap<String, Object>>();
        ArrayList<HashMap<String, Object>> sumanticipos_usd = new ArrayList<HashMap<String, Object>>();
        ArrayList<HashMap<String, Object>> anticipos = new ArrayList<HashMap<String, Object>>();

        sumanticipos_mn = this.getCxcDao().getCartera_SumaAnticiposMN(id_cliente);
        sumanticipos_usd = this.getCxcDao().getCartera_SumaAnticiposUSD(id_cliente);
        anticipos = this.getCxcDao().getCartera_Anticipos(id_cliente);

        jsonretorno.put("suma_mn", sumanticipos_mn);
        jsonretorno.put("suma_usd", sumanticipos_usd);
        jsonretorno.put("anticipos", anticipos);

        return jsonretorno;
    }


    //Obtiene las facturas del cliente
    @RequestMapping(method = RequestMethod.POST, value="/obtener_facturas.json")
    public @ResponseBody HashMap<String,ArrayList<HashMap<String, Object>>> getObtenerFacturasJson(
            @RequestParam(value="id_cliente", required=true) Integer id_cliente,
            Model model
            ) {

        HashMap<String,ArrayList<HashMap<String, Object>>> jsonretorno = new HashMap<String,ArrayList<HashMap<String, Object>>>();
        ArrayList<HashMap<String, Object>> facturas = new ArrayList<HashMap<String, Object>>();

        facturas = this.getCxcDao().getCartera_Facturas(id_cliente);

        jsonretorno.put("Facturas", facturas);
        return jsonretorno;
    }


    //registro de pagos
    @RequestMapping(method = RequestMethod.POST, value="/registra_pagos.json")
    public @ResponseBody HashMap<String, String> editJson(
            @RequestParam(value="forma_pago", required=true) String forma_pago,
            @RequestParam(value="moneda", required=true) String moneda,
            @RequestParam(value="banco", required=true) String banco,
            @RequestParam(value="monto_pago", required=true) String monto_pago,
            @RequestParam(value="observaciones", required=true) String observaciones,
            @RequestParam(value="valores", required=true) String cadena_valores,
            @RequestParam(value="cheque", required=true) String cheque,
            @RequestParam(value="referencia", required=true) String referencia,
            @RequestParam(value="tarjeta", required=true) String tarjeta,
            @RequestParam(value="deuda_usd", required=true) String deuda_usd,
            @RequestParam(value="deuda_pesos", required=true) String deuda_pesos,
            @RequestParam(value="antipo", required=true) String antipo,
            @RequestParam(value="cliente_id", required=true) String cliente_id,
            @RequestParam(value="fecha", required=true) String fecha,
            @RequestParam(value="fecha_deposito", required=true) String fecha_deposito,
            @RequestParam(value="banco_kemikal", required=true) String ficha_banco_kemikal,
            @RequestParam(value="cuenta_deposito", required=true) String ficha_cuenta_deposito,
            @RequestParam(value="movimiento_deposito", required=true) String ficha_movimiento_deposito,
            @RequestParam(value="tipo_cambio", required=true) String tipo_cambio,
            @RequestParam(value="anticipo_gastado", required=true) String anticipo_gastado,
            @RequestParam(value="no_transaccion_anticipo", required=true) String no_transaccion_anticipo,
            @RequestParam(value="iu", required=true) String id_user,
            @RequestParam(value="saldo_a_favor", required=true) String saldo_a_favor,
            Model model
            ) throws BbgumProxyError, IOException {

            Integer id=0;//esta variable solo se declaro para pasar al procedimiento
            Integer app_selected = 14;
            String command_selected = "pago";

            System.out.println("Registro de pago");

            //decodificar id de usuario
            Integer id_usuario = Integer.parseInt(Base64Coder.decodeString(id_user));


            System.out.println("monto_pago:"+monto_pago +"     anticipo_gastado: "+anticipo_gastado +"     no_transaccion_anticipo:"+no_transaccion_anticipo);


            String[] arreglo = cadena_valores.split("&");

            for(int i=0; i<arreglo.length; i++) arreglo[i] = "'"+arreglo[i]+"'";

            Calendar calendario = Calendar.getInstance();
            int hora =calendario.get(Calendar.HOUR_OF_DAY);
            int minutos = calendario.get(Calendar.MINUTE);
            int segundos = calendario.get(Calendar.SECOND);

            //serializar el arreglo
            String extra_data_array = StringUtils.join(arreglo, ",");

            HashMap<String, String> jsonretorno = new HashMap<String, String>();

            HashMap<String, String> succes = new HashMap<String, String>();

            String data_string =
                    app_selected+"___"+                             //1
                    command_selected+"___"+                         //2
                    id_usuario+"___"+                               //3
                    cliente_id+"___"+                               //4
                    deuda_pesos+"___"+                              //5
                    deuda_usd+"___"+                                //6
                    moneda+"___"+                                   //7
                    fecha+" "+hora+":"+minutos+":"+segundos+"___"+  //8
                    banco+"___"+                                    //9
                    observaciones.toUpperCase()+"___"+              //10
                    forma_pago+"___"+                               //11
                    cheque+"___"+                                   //12
                    referencia+"___"+                               //13
                    tarjeta+"___"+                                  //14
                    antipo+"___"+                                   //15
                    monto_pago+"___"+                               //16
                    fecha_deposito+"___"+                           //17
                    ficha_movimiento_deposito+"___"+                //18
                    ficha_cuenta_deposito+"___"+                    //19
                    ficha_banco_kemikal+"___"+                      //20
                    tipo_cambio+"___"+                              //21
                    anticipo_gastado+"___"+
                    no_transaccion_anticipo+"___"+
                    saldo_a_favor;

            succes = this.getCxcDao().selectFunctionValidateAaplicativo(data_string,app_selected,extra_data_array);

            log.log(Level.INFO, "despues de validacion {0}", String.valueOf(succes.get("success")));
            String actualizo = "0";

            if(String.valueOf(succes.get("success")).equals("true")){
                actualizo = this.getCxcDao().selectFunctionForThisApp(data_string, extra_data_array);
                String pag_id = String.valueOf(actualizo.split("___")[0]);
                jsonretorno.put("numero_transaccion", pag_id);
                jsonretorno.put("identificador_pago",String.valueOf(actualizo.split("___")[1]));

                /* From this point onward
                This code really sucks !!, because of there no clear strategy to catch the error and show it at user's interface
                Conversily user's interface will never know if the request has gone missing */
                HashMap<String, String> userDat = this.getHomeDao().getUserById(id_usuario);
                Integer id_empresa = Integer.parseInt(userDat.get("empresa_id"));
                String no_id = this.getGralDao().getNoIdEmpresa(id_empresa);
                String serieFolio = this.getCxcDao().q_serie_folio(id_usuario);
                String filename = no_id + "_" + serieFolio + ".xml";

                LegacyRequest req = new LegacyRequest();

                req.sendTo("cxc");
                req.from("webui");
                req.action("dopago");

                HashMap<String, String> kwargs = new HashMap<String, String>();
                kwargs.put("filename", filename);
                kwargs.put("usr_id", id_usuario.toString());
                kwargs.put("pag_id", pag_id.toString());
                req.args(kwargs);

                BbgumProxy bbgumProxy = new BbgumProxy();

                try {
                    ServerReply reply = bbgumProxy.uploadBuff("localhost", 10080, req.getJson().getBytes());
                    String msg = "core reply code: " + reply.getReplyCode();
                    if (reply.getReplyCode() == 0) {
                        Logger.getLogger(CarterasController.class.getName()).log(
                                Level.INFO, msg);
                        jsonretorno.put("folio", serieFolio);
                    } else {
                        Logger.getLogger(CarterasController.class.getName()).log(
                                Level.WARNING, msg);
                    }
                } catch (BbgumProxyError ex) {
                    Logger.getLogger(CarterasController.class.getName()).log(
                            Level.WARNING, ex.getMessage());
                }
            }

            jsonretorno.put("success",String.valueOf(succes.get("success")));
            System.out.println("numero_transaccion: "+jsonretorno.get("numero_transaccion"));
            System.out.println("identificador_pago: "+jsonretorno.get("identificador_pago"));

            log.log(Level.INFO, "Salida json {0}", String.valueOf(jsonretorno.get("success")));
        return jsonretorno;
    }
    
    
    
    
    
    @RequestMapping(method = RequestMethod.POST, value="/generar_anticipo.json")
    public @ResponseBody HashMap<String, String> GeneraAnticipoJson(
            @RequestParam(value="fecha_anticipo", required=true) String fecha_anticipo,
            @RequestParam(value="monto_anticipo", required=true) String monto_anticipo,
            @RequestParam(value="id_moneda", required=true) String id_moneda,
            @RequestParam(value="id_cliente", required=true) String id_cliente,
            @RequestParam(value="observaciones", required=true) String observaciones,
            @RequestParam(value="iu", required=true) String id_user,
            Model model
            ) {
            
            Integer id=0;//esta variable solo se declaro para pasar al procedimiento selectFunctionForThisApp, no se utiliza
            
            Integer app_selected = 14;
            String command_selected = "anticipo";
            
            System.out.println("Registro de anticipo");
            //decodificar id de usuario
            Integer id_usuario = Integer.parseInt(Base64Coder.decodeString(id_user));
            //System.out.println("id_usuario: "+id_usuario);
            
            //serializar el arreglo
            String extra_data_array ="'sin datos'";
            
            HashMap<String, String> jsonretorno = new HashMap<String, String>();
            
            HashMap<String, String> succes = new HashMap<String, String>();
            
            String data_string = app_selected+"___"+
                                command_selected+"___"+
                                id_usuario+"___"+
                                fecha_anticipo+"___"+
                                monto_anticipo+"___"+
                                id_moneda+"___"+
                                id_cliente+"___"+
                                observaciones.toUpperCase();
            
            
            succes = this.getCxcDao().selectFunctionValidateAaplicativo(data_string,app_selected,extra_data_array);
            
            log.log(Level.INFO, "despues de validacion {0}", String.valueOf(succes.get("success")));
            String actualizo = "0";
            
            if(String.valueOf(succes.get("success")).equals("true")){
                actualizo = this.getCxcDao().selectFunctionForThisApp(data_string, extra_data_array);
            }
            
            
            jsonretorno.put("success",String.valueOf(succes.get("success")));
            jsonretorno.put("numero_transaccion",String.valueOf(actualizo));
            
            
            log.log(Level.INFO, "Salida json {0}", String.valueOf(jsonretorno.get("success")));
        return jsonretorno;
    }
    
    
    
    //cancelacion de pagos
    @RequestMapping(method = RequestMethod.POST, value="/cancelar_pagos.json")
    public @ResponseBody HashMap<String, String> CancelarPagosJson(
            @RequestParam(value="cancelar_por", required=true) String cancelar_por,
            @RequestParam(value="observaciones_canc", required=true) String observaciones_canc,
            @RequestParam(value="numero_trans", required=true) String numero_trans,
            @RequestParam(value="cadena", required=true) String cadena,
            @RequestParam(value="fecha_cancelacion", required=true) String fecha_cancelacion,
            @RequestParam(value="iu", required=true) String id_user,
            Model model
            ) {
            
            Integer id=0;//esta variable solo se declaro para pasar al procedimiento
            
            Integer app_selected = 14;
            String command_selected = "cancelacion";
            
            System.out.println("Registro de cancelacion");
            
            //decodificar id de usuario
            Integer id_usuario = Integer.parseInt(Base64Coder.decodeString(id_user));
            //System.out.println("id_usuario: "+id_usuario);
            
            
            String[] arreglo = cadena.split("___");
            
            for(int i=0; i<arreglo.length; i++) { 
                System.out.println("Valor posicion"+i+": "+arreglo[i]);
                arreglo[i] = "'"+arreglo[i]+"'";
            }
            
            
            Calendar calendario = Calendar.getInstance();
            int hora =calendario.get(Calendar.HOUR_OF_DAY);
            int minutos = calendario.get(Calendar.MINUTE);
            int segundos = calendario.get(Calendar.SECOND);
            
            
            //serializar el arreglo
            String extra_data_array = StringUtils.join(arreglo, ",");
            
            HashMap<String, String> jsonretorno = new HashMap<String, String>();
            
            HashMap<String, String> succes = new HashMap<String, String>();
            
            String data_string = app_selected+"___"+command_selected+"___"+id_usuario+"___"+cancelar_por+"___"+observaciones_canc.toUpperCase()+"___"+numero_trans+"___"+fecha_cancelacion+" "+hora+":"+minutos+":"+segundos;
            System.out.println("data_string: "+data_string);
            succes = this.getCxcDao().selectFunctionValidateAaplicativo(data_string,app_selected,extra_data_array);
            
            log.log(Level.INFO, "despues de validacion {0}", String.valueOf(succes.get("success")));
            String actualizo = "0";
            
            if(String.valueOf(succes.get("success")).equals("true")){
                actualizo = this.getCxcDao().selectFunctionForThisApp(data_string, extra_data_array);
            }
            
            jsonretorno.put("success",String.valueOf(succes.get("success")));
            jsonretorno.put("numero_transaccion",String.valueOf(actualizo));
            log.log(Level.INFO, "Salida json {0}", String.valueOf(jsonretorno.get("success")));
        return jsonretorno;
    }
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
        
    //Obtiene monedas
    @RequestMapping(method = RequestMethod.POST, value="/get_monedas.json")
    public @ResponseBody HashMap<String,ArrayList<HashMap<String, Object>>> getObtenerMonedasJson(
            Model model
            ) {
        
        HashMap<String,ArrayList<HashMap<String, Object>>> jsonretorno = new HashMap<String,ArrayList<HashMap<String, Object>>>();
        ArrayList<HashMap<String, Object>> monedas = new ArrayList<HashMap<String, Object>>();
        monedas = this.getCxcDao().getMonedas();
        
        jsonretorno.put("Monedas", monedas);
        return jsonretorno;
    }
    
    
    //Obtiene los tipos de movimiento
    @RequestMapping(method = RequestMethod.POST, value="/get_tipos_movimiento.json")
    public @ResponseBody HashMap<String,ArrayList<HashMap<String, Object>>> getgetTiposMovimientoJson(
            Model model
            ) {
        
        HashMap<String,ArrayList<HashMap<String, Object>>> jsonretorno = new HashMap<String,ArrayList<HashMap<String, Object>>>();
        ArrayList<HashMap<String, Object>> tipos_mov = new ArrayList<HashMap<String, Object>>();
        tipos_mov = this.getCxcDao().getCartera_TipoMovimiento();
        
        jsonretorno.put("Tiposmov", tipos_mov);
        return jsonretorno;
    }
    
    
    
    //buscador de facturas a cancelar
    @RequestMapping(method = RequestMethod.POST, value="/get_buscador_facturas_cancelar.json")
    public @ResponseBody HashMap<String,ArrayList<HashMap<String, String>>> getBuscarFacturasCancelarJson(
            @RequestParam(value="num_trans", required=true) String num_transaccion,
            @RequestParam(value="factura", required=true) String factura,
            @RequestParam(value="id_cliente", required=true) Integer id_cliente,
            Model model
            ) {
        
        HashMap<String,ArrayList<HashMap<String, String>>> jsonretorno = new HashMap<String,ArrayList<HashMap<String, String>>>();
        ArrayList<HashMap<String, String>> facturas_cancelar = new ArrayList<HashMap<String, String>>();
        facturas_cancelar = this.getCxcDao().getCartera_FacturasCancelar(num_transaccion,factura,id_cliente);
        
        jsonretorno.put("FacturasCancelar", facturas_cancelar);
        return jsonretorno;
    }
    
    
    
    //buscador de facturas de un numero de transaccion en especifico
    @RequestMapping(method = RequestMethod.POST, value="/get_facturas_num_transaccion.json")
    public @ResponseBody HashMap<String,ArrayList<HashMap<String, Object>>> getFacturasNumTransaccionJson(
            @RequestParam(value="num_trans", required=true) String num_transaccion,
            Model model
            ) {
        
        HashMap<String,ArrayList<HashMap<String, Object>>> jsonretorno = new HashMap<String,ArrayList<HashMap<String, Object>>>();
        ArrayList<HashMap<String, Object>> FacturasTransaccion = new ArrayList<HashMap<String, Object>>();
        FacturasTransaccion = this.getCxcDao().getCartera_FacturasTransaccion(num_transaccion);
        
        jsonretorno.put("FacturasTrans", FacturasTransaccion);
        return jsonretorno;
    }
    
    
    
    
    //retorna los 50 ultimos numeros de transaccion de un cliente en especifico
    @RequestMapping(method = RequestMethod.POST, value="/get_num_transaccion_cliente.json")
    public @ResponseBody HashMap<String,ArrayList<HashMap<String, String>>> getNumTransaccionClienteJson(
            @RequestParam(value="id_cliente", required=true) Integer id_cliente,
            Model model
            ) {
        
        HashMap<String,ArrayList<HashMap<String, String>>> jsonretorno = new HashMap<String,ArrayList<HashMap<String, String>>>();
        ArrayList<HashMap<String, String>> NumTransaccion = new ArrayList<HashMap<String, String>>();
        NumTransaccion = this.getCxcDao().getCartera_NumerosDeTransaccionCliente(id_cliente);
        
        jsonretorno.put("NumTrans", NumTransaccion);
        return jsonretorno;
    }
    
    

    //Genera pdf de depositos
    @RequestMapping(value = "/get_genera_pdf_depositos/{fecha_inicial}/{fecha_final}/{iu}/out.json", method = RequestMethod.GET ) 
    public ModelAndView get_genera_pdf_depositosJson(
                @PathVariable("fecha_inicial") String fecha_inicial,
                @PathVariable("fecha_final") String fecha_final,
                @PathVariable("iu") String id_user_cod,
                HttpServletRequest request, 
                HttpServletResponse response, 
                Model model)
            throws ServletException, IOException, URISyntaxException, DocumentException {

        HashMap<String, String> userDat = new HashMap<String, String>();
        
        
        System.out.println("Generando reporte de depositos");
        
        //decodificar id de usuario
        Integer id_usuario = Integer.parseInt(Base64Coder.decodeString(id_user_cod));
        //System.out.println("id_usuario: "+id_usuario);
        
        userDat = this.getHomeDao().getUserById(id_usuario);
        Integer id_empresa = Integer.parseInt(userDat.get("empresa_id"));
        
        String razon_social_empresa = this.getGralDao().getRazonSocialEmpresaEmisora(id_empresa);
        
        //obtener el directorio temporal
        String dir_tmp = this.getGralDao().getTmpDir();
        
        
        String[] array_company = razon_social_empresa.split(" ");
        String company_name= array_company[0].toLowerCase();
        String ruta_imagen = this.getGralDao().getImagesDir() +"logo_"+ company_name +".png";
        
        
        File file_dir_tmp = new File(dir_tmp);
        System.out.println("Directorio temporal: "+file_dir_tmp.getCanonicalPath());
        
        
        String file_name = "depositos_"+fecha_inicial+"_"+fecha_final+".pdf";
        //ruta de archivo de salida
        String fileout = file_dir_tmp +"/"+  file_name;
        
        ArrayList<HashMap<String, String>> lista_depositos = new ArrayList<HashMap<String, String>>();
        
        //obtiene los depositos del periodo indicado
        lista_depositos = this.getCxcDao().getCartera_DatosReporteDepositos(fecha_inicial, fecha_final, id_empresa);
        
        //instancia a la clase que construye el pdf de la del reporte de estado de cuentas del cliente
        PdfDepositos x = new PdfDepositos( fileout,ruta_imagen,razon_social_empresa,fecha_inicial,fecha_final,lista_depositos);
        
        
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
        
        return null;
        
    }
  
    //Genera pdf del reporte de aplicacion de pagos a clientes, al momento de registrar un pago
    @RequestMapping(value = "/getPdfReporteAplicacionPago/{id_pago}/{iu}/out.json", method = RequestMethod.GET ) 
    public ModelAndView getGeneraPdfFacturacionJson(
                @PathVariable("id_pago") Integer id_pago,
                @PathVariable("iu") String id_user,
                HttpServletRequest request, 
                HttpServletResponse response, 
                Model model)
            throws ServletException, IOException, URISyntaxException, DocumentException, Exception {
        
        HashMap<String, String> userDat = new HashMap<String, String>();
        
        
        System.out.println("Generando pdf de aplicacion del pago");
        
        //decodificar id de usuario
        Integer id_usuario = Integer.parseInt(Base64Coder.decodeString(id_user));
        
        userDat = this.getHomeDao().getUserById(id_usuario);
        Integer id_empresa = Integer.parseInt(userDat.get("empresa_id"));
        
        
        String razon_social_empresa = this.getGralDao().getRazonSocialEmpresaEmisora(id_empresa);
        
        //obtener el directorio temporal
        String dir_tmp = this.getGralDao().getTmpDir();
        
        
        String[] array_company = razon_social_empresa.split(" ");
        String company_name= array_company[0].toLowerCase();
        String ruta_imagen = this.getGralDao().getImagesDir() +"logo_"+ company_name +".png";
        
        
        File file_dir_tmp = new File(dir_tmp);
        System.out.println("Directorio temporal: "+file_dir_tmp.getCanonicalPath());
        
        
        String file_name = "recibo_pago.pdf";
        //ruta de archivo de salida
        String fileout = file_dir_tmp +"/"+  file_name;
        
        ArrayList<HashMap<String, String>> datos_header = new ArrayList<HashMap<String, String>>();
        ArrayList<HashMap<String, String>> lista_facturas = new ArrayList<HashMap<String, String>>();
        
        //datos del pago
        datos_header = this.getCxcDao().getCartera_PagosDatosHeader(id_pago,id_empresa);
        
        //obtiene las el listado de los pagos aplicados en esta transaccion
        lista_facturas = this.getCxcDao().getCartera_PagosAplicados(id_pago,id_empresa);
        
        //instancia a la clase que construye el pdf del reporte
        PdfReporteAplicacionPago x = new PdfReporteAplicacionPago(fileout,ruta_imagen,razon_social_empresa, datos_header, lista_facturas);
        
        
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
        
        FileHelper.delete(fileout);
        return null;
        
    } 
    
    @RequestMapping(value = "/getPdfFactura/{id_pago}/{iu}/outPdfFactura.json", method = RequestMethod.GET ) 
    public ModelAndView getPdfFacturaJson(
                @PathVariable("id_pago") Integer id_pago,
                @PathVariable("iu") String id_user,
                HttpServletRequest request, 
                HttpServletResponse response, 
                Model model)
            throws ServletException, IOException, URISyntaxException, DocumentException, Exception {
        
        HashMap<String, String> userDat = new HashMap<String, String>();
        
        
        System.out.println("PDF de Factura");
        Integer id_usuario = Integer.parseInt(Base64Coder.decodeString(id_user));
        String aux_no_fac = this.getGralDao().getNombreFactura(id_pago);
        userDat = this.getHomeDao().getUserById(id_usuario);
        Integer id_empresa = Integer.parseInt(userDat.get("empresa_id"));
        String rfcEmpresa = this.getGralDao().getRfcEmpresaEmisora(id_empresa);
        System.out.println("id_usuario: "+id_usuario);
        System.out.println("id_empresa: "+id_empresa);
        System.out.println("aux_no_fac: "+aux_no_fac);
        System.out.println("rfcEmpresa: "+rfcEmpresa);
        
        //File file_dir_tmp = new File(dir_tmp);
        //System.out.println("Directorio temporal: "+file_dir_tmp.getCanonicalPath());
        String dir_tmp = this.getGralDao().getTmpDir();
        File file_dir_tmp = new File(dir_tmp);
        System.out.println("Directorio temporal: "+file_dir_tmp.getCanonicalPath());
        //File file_dir_tmp = new File("resources/cfdi/timbre/emitidos/"+rfcEmpresa+"/");
        //File file_dir_tmp = new File("C:\\tmp\\");
        String file_name = aux_no_fac+".pdf";
        
        //ruta de archivo de salida
        //String fileout = file_dir_tmp +"/"+ rfcEmpresa + "/" + file_name;
        String fileout = this.getGralDao().getCfdiTimbreEmitidosDir() + "/"+ rfcEmpresa + "/" + file_name;
        System.out.println("Intentanto obtener PDF Factura de la ruta:" +fileout);
        Logger.getLogger(CarterasController.class.getName()).log(Level.INFO, "Intentanto obtener PDF Factura de la ruta:" +fileout);
        //String fileout = file_dir_tmp + "\\" + file_name;
        
        ArrayList<HashMap<String, String>> datos_header = new ArrayList<HashMap<String, String>>();
        ArrayList<HashMap<String, String>> lista_facturas = new ArrayList<HashMap<String, String>>();
        
        /*
        datos_header = this.getCxcDao().getCartera_PagosDatosHeader(id_pago,id_empresa);
        lista_facturas = this.getCxcDao().getCartera_PagosAplicados(id_pago,id_empresa);
        PdfReporteAplicacionPago x = new PdfReporteAplicacionPago(fileout,ruta_imagen,razon_social_empresa, datos_header, lista_facturas);
        */
        
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
               
        //FileHelper.delete(fileout);
        return null;
        
    } 
    
    @RequestMapping(value = "/getXmlFactura/{id_pago}/{iu}/outXmlFactura.json", method = RequestMethod.GET ) 
    public ModelAndView getXmlFacturaJson(
                @PathVariable("id_pago") Integer id_pago,
                @PathVariable("iu") String id_user,
                HttpServletRequest request, 
                HttpServletResponse response, 
                Model model)
            throws ServletException, IOException, URISyntaxException, DocumentException, Exception {
        
        HashMap<String, String> userDat = new HashMap<String, String>();
        
        
        System.out.println("XML de Factura");
        Integer id_usuario = Integer.parseInt(Base64Coder.decodeString(id_user));
        String aux_no_fac = this.getGralDao().getNombreFactura(id_pago);
        userDat = this.getHomeDao().getUserById(id_usuario);
        Integer id_empresa = Integer.parseInt(userDat.get("empresa_id"));
        String rfcEmpresa = this.getGralDao().getRfcEmpresaEmisora(id_empresa);
        System.out.println("id_usuario: "+id_usuario);
        System.out.println("id_empresa: "+id_empresa);
        System.out.println("aux_no_fac: "+aux_no_fac);
        System.out.println("rfcEmpresa: "+rfcEmpresa);
        
        //File file_dir_tmp = new File(dir_tmp);
        //System.out.println("Directorio temporal: "+file_dir_tmp.getCanonicalPath());
        //File file_dir_tmp = new File("resources/cfdi/timbre/emitidos/"+rfcEmpresa+"/");
        //File file_dir_tmp = new File("C:\\tmp\\");
        String dir_tmp = this.getGralDao().getTmpDir();
        File file_dir_tmp = new File(dir_tmp);
        System.out.println("Directorio temporal: "+file_dir_tmp.getCanonicalPath());
        String file_name = aux_no_fac+".xml";
        
        //ruta de archivo de salida
       // String fileout = file_dir_tmp + "/" + rfcEmpresa + "/" +  file_name;
       String fileout = this.getGralDao().getCfdiTimbreEmitidosDir() + "/"+ rfcEmpresa + "/" + file_name;
       System.out.println("Intentanto obtener XML Factura de la ruta:" +fileout);
        Logger.getLogger(CarterasController.class.getName()).log(Level.INFO, "Intentanto obtener XML Factura de la ruta:" +fileout);
        //String fileout = file_dir_tmp + "\\" + file_name;
        
        ArrayList<HashMap<String, String>> datos_header = new ArrayList<HashMap<String, String>>();
        ArrayList<HashMap<String, String>> lista_facturas = new ArrayList<HashMap<String, String>>();
        
        /*
        datos_header = this.getCxcDao().getCartera_PagosDatosHeader(id_pago,id_empresa);
        lista_facturas = this.getCxcDao().getCartera_PagosAplicados(id_pago,id_empresa);
        PdfReporteAplicacionPago x = new PdfReporteAplicacionPago(fileout,ruta_imagen,razon_social_empresa, datos_header, lista_facturas);
        */
        
        System.out.println("Recuperando archivo: " + fileout);
        File file = new File(fileout);
        int size = (int) file.length(); // Tamaño del archivo
        BufferedInputStream bis = new BufferedInputStream(new FileInputStream(file));
        response.setBufferSize(size);
        response.setContentLength(size);
        response.setContentType("application/xml");
        response.setHeader("Content-Disposition","attachment; filename=\"" + file.getName() +"\"");
        FileCopyUtils.copy(bis, response.getOutputStream());  	
        response.flushBuffer();
               
        //FileHelper.delete(fileout);
        return null;
        
    } 
    
}