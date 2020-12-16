package com.agnux.kemikal.controllers;


import com.agnux.cfd.v2.Base64Coder;
import com.agnux.common.helpers.FileHelper;
import com.agnux.common.helpers.StringHelper;
import com.agnux.common.helpers.n2t;
import com.agnux.common.obj.DataPost;
import com.agnux.common.obj.ResourceProject;
import com.agnux.common.obj.UserSessionData;
import com.agnux.kemikal.interfacedaos.CxpInterfaceDao;
import com.agnux.kemikal.interfacedaos.GralInterfaceDao;
import com.agnux.kemikal.interfacedaos.HomeInterfaceDao;
import com.agnux.kemikal.reportes.PdfAplicacionPagoProveedor;
import com.itextpdf.text.DocumentException;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.math.BigInteger;
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
@RequestMapping("/proveedorespagos/")
public class ProveedoresPagosController {
    private static final Logger log  = Logger.getLogger(ProveedoresPagosController.class.getName());
    ResourceProject resource = new ResourceProject();

    @Autowired
    @Qualifier("daoHome")
    private HomeInterfaceDao HomeDao;

    @Autowired
    @Qualifier("daoGral")
    private GralInterfaceDao gralDao;
    
    
    @Autowired
    @Qualifier("daoCxp")
    private CxpInterfaceDao cxpDao;
    
    public CxpInterfaceDao getCxpDao() {
        return cxpDao;
    }
    
    
    public HomeInterfaceDao getHomeDao() {
        return HomeDao;
    }
    

    public GralInterfaceDao getGralDao() {
        return gralDao;
    }

    public void setGralDao(GralInterfaceDao gralDao) {
        this.gralDao = gralDao;
    }
    
    
    @RequestMapping(value="/startup.agnux")
    public ModelAndView startUp(HttpServletRequest request, HttpServletResponse response, @ModelAttribute("user") UserSessionData user)
            throws ServletException, IOException {
        
        log.log(Level.INFO, "Ejecutando starUp de {0}", ProveedoresPagosController.class.getName());
        LinkedHashMap<String,String> infoConstruccionTabla = new LinkedHashMap<String,String>();
        
        infoConstruccionTabla.put("id", "Acciones:70");
        infoConstruccionTabla.put("numero_transaccion", "No. Transaccion:110");
        infoConstruccionTabla.put("total", "Monto pago:100");
        infoConstruccionTabla.put("razon_social", "Proveedor:280");
        infoConstruccionTabla.put("forma_pago", "Forma Pago:100");
        infoConstruccionTabla.put("moneda", "Moneda:60");
        infoConstruccionTabla.put("estado", "Estado:60");
        infoConstruccionTabla.put("fecha_pago", "Fecha Pago:90");
        
        ModelAndView x = new ModelAndView("proveedorespagos/startup", "title", "Pagos a Proveedores");
        
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
        
        //aplicativo de Pagos a Proveedores
        Integer app_selected = 60;
        
        //decodificar id de usuario
        Integer id_usuario = Integer.parseInt(Base64Coder.decodeString(id_user_cod));
        //System.out.println("id_usuario: "+id_usuario);
        
        //variables para el buscador
        String num_transaccion = StringHelper.isNullString(String.valueOf(has_busqueda.get("num_transaccion")));
        String factura = "%"+StringHelper.isNullString(String.valueOf(has_busqueda.get("factura")))+"%";
        String proveedor = "%"+StringHelper.isNullString(String.valueOf(has_busqueda.get("proveedor")))+"%";
        String fecha_inicial = ""+StringHelper.isNullString(String.valueOf(has_busqueda.get("fecha_inicial")))+"";
        String fecha_final = ""+StringHelper.isNullString(String.valueOf(has_busqueda.get("fecha_final")))+"";
        
        String data_string = app_selected+"___"+id_usuario+"___"+num_transaccion+"___"+factura+"___"+proveedor+"___"+fecha_inicial+"___"+fecha_final;
        
        //obtiene total de registros en base de datos, con los parametros de busqueda
        int total_items = this.getCxpDao().countAll(data_string);
        
        //calcula el total de paginas
        int total_pags = resource.calculaTotalPag(total_items,items_por_pag);
        
        //variables que necesita el datagrid, para no tener que hacer uno por cada aplicativo
        DataPost dataforpos = new DataPost(orderby, desc, items_por_pag, pag_start, display_pag, input_json, cadena_busqueda,total_items,total_pags,id_user_cod);
        
        int offset = resource.__get_inicio_offset(items_por_pag, pag_start);
        
        //obtiene los registros para el grid, de acuerdo a los parametros de busqueda
        jsonretorno.put("Data", this.getCxpDao().getProveedoresPagos_PaginaGrid(data_string, offset, items_por_pag, orderby, desc));
        //obtiene el hash para los datos que necesita el datagrid
        jsonretorno.put("DataForGrid", dataforpos.formaHashForPos(dataforpos));
        
        return jsonretorno;
    }
    
    
    
    
    
    //obtiene los proveedores para el buscador
    @RequestMapping(method = RequestMethod.POST, value="/getBuacadorProveedores.json")
    public @ResponseBody HashMap<String,ArrayList<HashMap<String, String>>> getBuacadorProveedoresJson(
            @RequestParam(value="rfc", required=true) String rfc,
            @RequestParam(value="no_prov", required=true) String no_prov,
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
        
        proveedores = this.getCxpDao().getProvFacturas_BuscadorProveedores(rfc, no_prov, nombre,id_empresa);
        jsonretorno.put("Proveedores", proveedores);
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
        
        jsonretorno.put("Proveedor", this.getCxpDao().getDatosProveedorByNoProv(no_prov, id_empresa));
        return jsonretorno;
    }
    
    
    
    @RequestMapping(method = RequestMethod.POST, value="/getPago.json")
    public @ResponseBody HashMap<String,ArrayList<HashMap<String, String>>> getPagoJson(
            @RequestParam(value="id", required=true) Integer id,
            @RequestParam(value="iu", required=true) String id_user_cod,
            Model model
            ) {
        
        log.log(Level.INFO, "Ejecutando getCarteraJson de {0}", ProveedoresPagosController.class.getName());
        HashMap<String,ArrayList<HashMap<String, String>>> jsonretorno = new HashMap<String,ArrayList<HashMap<String, String>>>();
        ArrayList<HashMap<String, String>> tipos_movimiento = new ArrayList<HashMap<String, String>>();
        ArrayList<HashMap<String, String>> formas_pago = new ArrayList<HashMap<String, String>>();
        ArrayList<HashMap<String, String>> monedas = new ArrayList<HashMap<String, String>>();
        ArrayList<HashMap<String, String>> bancos = new ArrayList<HashMap<String, String>>();
        ArrayList<HashMap<String, String>> conceptos = new ArrayList<HashMap<String, String>>();
        ArrayList<HashMap<String, String>> bancos_deposito = new ArrayList<HashMap<String, String>>();
        ArrayList<HashMap<String, String>> tipo_cambio = new ArrayList<HashMap<String, String>>();
        HashMap<String, String> userDat = new HashMap<String, String>();
        ArrayList<HashMap<String, String>> datos_pago = new ArrayList<HashMap<String, String>>();
        ArrayList<HashMap<String, String>> pago_detalle = new ArrayList<HashMap<String, String>>();
        
        //decodificar id de usuario
        Integer id_usuario = Integer.parseInt(Base64Coder.decodeString(id_user_cod));
        //System.out.println("id_usuario: "+id_usuario);
        
        userDat = this.getHomeDao().getUserById(id_usuario);
        
        Integer id_empresa = Integer.parseInt(userDat.get("empresa_id"));
        Integer id_sucursal = Integer.parseInt(userDat.get("sucursal_id"));
        
        if( id!=0  ){
            datos_pago = this.getCxpDao().getProveedoresPagos_Datos(id);
            pago_detalle = this.getCxpDao().getProveedoresPagos_Detalles(id);
        }
        
        tipos_movimiento = this.getCxpDao().getProveedoresPagos_TipoMovimiento();
        formas_pago = this.getCxpDao().getProveedoresPagos_FormasPago();
        monedas = this.getCxpDao().getMonedas();
        bancos = this.getCxpDao().getBancos(id_empresa);
        tipo_cambio = this.getCxpDao().getTipoCambioActual();
        conceptos = this.getCxpDao().getProveedoresPagos_Conceptos(id_empresa);
        
        jsonretorno.put("Tiposmov", tipos_movimiento);
        jsonretorno.put("Monedas", monedas);
        jsonretorno.put("Formaspago", formas_pago);
        jsonretorno.put("Bancos", bancos);
        jsonretorno.put("Conceptos", conceptos);
        jsonretorno.put("Bancosdeposito", bancos_deposito);
        jsonretorno.put("Tipocambio", tipo_cambio);
        jsonretorno.put("Datos", datos_pago);
        jsonretorno.put("Detalles", pago_detalle);
        return jsonretorno;
    }
    
    
    
    //Obtiene numeros de Chequeras de acuerdo al banco y la moneda
    @RequestMapping(method = RequestMethod.POST, value="/getChequeras.json")
    public @ResponseBody HashMap<String,ArrayList<HashMap<String, String>>> get_obtener_numero_de_cuentaJson(
            @RequestParam(value="id_banco", required=true) Integer id_banco,
            @RequestParam(value="id_moneda", required=true) Integer id_moneda,
            @RequestParam(value="iu", required=true) String id_user_cod,
            Model model
            ) {
        
        HashMap<String,ArrayList<HashMap<String, String>>> jsonretorno = new HashMap<String,ArrayList<HashMap<String, String>>>();
        ArrayList<HashMap<String, String>> cuentas = new ArrayList<HashMap<String, String>>();
        
        cuentas = this.getCxpDao().getProveedoresPagos_Chequeras(id_moneda, id_banco);
        
        jsonretorno.put("Chequeras", cuentas);
        return jsonretorno;
    }
    
    
    
    
    
    //Obtiene todas las facturas del proveedor que estan pendientes de pagar
    @RequestMapping(method = RequestMethod.POST, value="/getFacturas.json")
    public @ResponseBody HashMap<String,ArrayList<HashMap<String, String>>> getObtenerFacturasJson(
            @RequestParam(value="id_proveedor", required=true) Integer id_proveedor,
            Model model
            ) {
        
        HashMap<String,ArrayList<HashMap<String, String>>> jsonretorno = new HashMap<String,ArrayList<HashMap<String, String>>>();
        ArrayList<HashMap<String, String>> facturas = new ArrayList<HashMap<String, String>>();
        
        facturas = this.getCxpDao().getProveedoresPagos_Facturas(id_proveedor);
        
        jsonretorno.put("Facturas", facturas);
        return jsonretorno;
    }
    
    
    
    
    
    //registro de pagos
    @RequestMapping(method = RequestMethod.POST, value="/registra_pagos.json")
    public @ResponseBody HashMap<String, String> registra_pagosJson(
            @RequestParam(value="id_pago", required=true) String id_pago,
            @RequestParam(value="id_proveedor", required=true) String id_proveedor,
            @RequestParam(value="fecha_pago", required=true) String fecha_pago,
            @RequestParam(value="tipo_cambio", required=true) String tipo_cambio,
            @RequestParam(value="id_moneda", required=true) String id_moneda,
            @RequestParam(value="forma_pago", required=true) String forma_pago,
            @RequestParam(value="id_banco", required=true) String id_banco,
            @RequestParam(value="id_chequera", required=true) String id_chequera,
            @RequestParam(value="num_cheque", required=true) String num_cheque,
            @RequestParam(value="referencia", required=true) String referencia,
            @RequestParam(value="num_tarjeta", required=true) String num_tarjeta,
            @RequestParam(value="monto_pago", required=true) String monto_pago,
            @RequestParam(value="observaciones", required=true) String observaciones,
            @RequestParam(value="select_concepto", required=true) String select_concepto,
            @RequestParam(value="cadena", required=true) String cadena,
            @RequestParam(value="iu", required=true) String id_user,
            Model model
            ) {
            
            
            HashMap<String, String> jsonretorno = new HashMap<String, String>();
            HashMap<String, String> succes = new HashMap<String, String>();
            
            Integer app_selected = 60;//aplicativo pagos a proveedores
            String command_selected = "new";
            
            System.out.println("Registro de pago a proveedor");
            
            //decodificar id de usuario
            Integer id_usuario = Integer.parseInt(Base64Coder.decodeString(id_user));
            
            if( id_proveedor.equals("0") ){
                id_pago = "new";
            }else{
                id_pago = "edit";
            }
            
            String[] arreglo = cadena.split("&&");
            for(int i=0; i<arreglo.length; i++) { 
                System.out.println("Valor posicion"+i+": "+arreglo[i]);
                arreglo[i] = "'"+arreglo[i]+"'";
            }
            
            //serializar el arreglo
            String extra_data_array = StringUtils.join(arreglo, ",");
            
            
            BigInteger num = new BigInteger(monto_pago.split("\\.")[0]);
            n2t cal = new n2t();
            String centavos = monto_pago.substring(monto_pago.indexOf(".")+1);
            String numero = cal.convertirLetras(num);
            //String numeroMay = StringHelper.capitalizaString(numero);
            //String numeroMay = numero;
            
            //convertir a mayuscula la primera letra de la cadena
            String numeroMay = numero.substring(0, 1).toUpperCase() + numero.substring(1, numero.length());
            
            String denominacion = "";
            String denom = "";
            
            //System.out.append("num:"+num);
            //System.out.append("centavos:"+centavos);
            
            if(centavos.equals(num.toString())){
                centavos="00";
            }
            
            if(id_moneda.equals("1")){
                denominacion = "pesos";
                denom = "M.N.";
            }
            if(id_moneda.equals("2")){
                denominacion = "dolares";
                //denom = "USCY";
                denom = "USD";
            }
            
            String cantidad_letras=numeroMay + " " + denominacion + " " +centavos+"/100 "+ denom;
            
            System.out.append("monto_pago:"+monto_pago);
            System.out.append("cantidad_letras:"+cantidad_letras);
            
            
            String data_string = 
                    app_selected+"___"+
                    command_selected+"___"+
                    id_usuario+"___"+
                    id_pago+"___"+
                    id_proveedor+"___"+
                    fecha_pago+"___"+
                    tipo_cambio+"___"+
                    id_moneda+"___"+
                    forma_pago+"___"+
                    id_banco+"___"+
                    id_chequera+"___"+
                    num_cheque+"___"+
                    referencia+"___"+
                    num_tarjeta+"___"+
                    monto_pago+"___"+
                    observaciones.toUpperCase()+"___"+
                    select_concepto+"___"+
                    cantidad_letras.toUpperCase();
            
            System.out.println("data_string:"+data_string);
            
            succes = this.getCxpDao().selectFunctionValidateAaplicativo(data_string,app_selected,extra_data_array);
            
            log.log(Level.INFO, "despues de validacion {0}", String.valueOf(succes.get("success")));
            String actualizo = "0";
            
            if(String.valueOf(succes.get("success")).equals("true")){
                actualizo = this.getCxpDao().selectFunctionForCxpAdmProcesos(data_string, extra_data_array);
                jsonretorno.put("error_cheque",String.valueOf(actualizo.split("___")[0]));
                jsonretorno.put("numero_transaccion",String.valueOf(actualizo.split("___")[1]));
                jsonretorno.put("identificador_pago",String.valueOf(actualizo.split("___")[2]));
                System.out.println("actualizo: "+actualizo);
            }
            
            jsonretorno.put("success",String.valueOf(succes.get("success")));
            
            log.log(Level.INFO, "Salida json {0}", String.valueOf(jsonretorno.get("success")));
        return jsonretorno;
    }
    
    
    
    
    
    
  
    
    //cancelacion de pagos
    @RequestMapping(method = RequestMethod.POST, value="/cancelar_pago.json")
    public @ResponseBody HashMap<String, String> CancelarPagosJson(
            @RequestParam(value="id_pago", required=true) String id_pago,
            @RequestParam(value="motivo", required=true) String motivo,
            @RequestParam(value="iu", required=true) String id_user,
            Model model
        ) {
        
        HashMap<String, String> jsonretorno = new HashMap<String, String>();
        Integer app_selected = 60;
        String command_selected = "cancelacion";
        
        //decodificar id de usuario
        Integer id_usuario = Integer.parseInt(Base64Coder.decodeString(id_user));
        //System.out.println("id_usuario: "+id_usuario);
        
        String extra_data_array = "'sin datos'";
        String data_string = app_selected+"___"+command_selected+"___"+id_usuario+"___"+id_pago+"___"+motivo.toUpperCase();
        
        System.out.println("Ejecutando Cancelacion de Pago");
        jsonretorno.put("success",String.valueOf( this.getCxpDao().selectFunctionForCxpAdmProcesos(data_string,extra_data_array)) );
        
        log.log(Level.INFO, "Salida json {0}", String.valueOf(jsonretorno.get("success")));
        
        return jsonretorno;
    }
    
    
    
    
    
    //Genera pdf del reporte de aplicacion de pagos a Proveedor, al momento de registrar un pago
    @RequestMapping(value = "/getPdfReporteAplicacionPagoProveedor/{id_pago}/{id_proveedor}/{iu}/out.json", method = RequestMethod.GET ) 
    public ModelAndView getGeneraPdfFacturacionJson(
                @PathVariable("id_pago") Integer id_pago,
                @PathVariable("id_proveedor") Integer id_proveedor,
                @PathVariable("iu") String id_user,
                HttpServletRequest request, 
                HttpServletResponse response, 
                Model model)
            throws ServletException, IOException, URISyntaxException, DocumentException, Exception {
        
        HashMap<String, String> userDat = new HashMap<String, String>();
        
        
        System.out.println("Generando pdf de aplicacion de Pago a Proveedor");
        
        //decodificar id de usuario
        Integer id_usuario = Integer.parseInt(Base64Coder.decodeString(id_user));
        
        userDat = this.getHomeDao().getUserById(id_usuario);
        Integer id_empresa = Integer.parseInt(userDat.get("empresa_id"));
        String rfc_empresa = this.getGralDao().getRfcEmpresaEmisora(id_empresa);
        
        String razon_social_empresa = this.getGralDao().getRazonSocialEmpresaEmisora(id_empresa);
        
        //obtener el directorio temporal
        String dir_tmp = this.getGralDao().getTmpDir();
        
        
        //String[] array_company = razon_social_empresa.split(" ");
        //String company_name= array_company[0].toLowerCase();
        //String ruta_imagen = this.getGralDao().getImagesDir() +"logo_"+ company_name +".png";
        String ruta_imagen = this.getGralDao().getImagesDir()+rfc_empresa+"_logo.png";
        
        File file_dir_tmp = new File(dir_tmp);
        //System.out.println("Directorio temporal: "+file_dir_tmp.getCanonicalPath());
        
        String file_name = "RECIBO_PAGO_PROVEEDOR_"+rfc_empresa+".pdf";
        //ruta de archivo de salida
        String fileout = file_dir_tmp +"/"+  file_name;
        
        ArrayList<HashMap<String, String>> lista_facturas = new ArrayList<HashMap<String, String>>();
        
        //obtiene las el listado de los pagos aplicados en esta transaccion
        lista_facturas = this.getCxpDao().getProveedoresPagos_PagosAplicados(id_pago,id_proveedor);
        
        //instancia a la clase que construye el pdf del reporte
        PdfAplicacionPagoProveedor x = new PdfAplicacionPagoProveedor(fileout,ruta_imagen,razon_social_empresa,lista_facturas);
        
        
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
