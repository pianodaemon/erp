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
import com.agnux.kemikal.reportes.PdfCotizacion;
import com.itextpdf.text.DocumentException;
import java.io.File;
import java.io.IOException;
import java.io.ByteArrayInputStream;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.concurrent.TimeUnit;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
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
import com.maxima.sales.cli.grpc.CotRequest;
import com.maxima.sales.cli.grpc.CotResponse;
import com.maxima.sales.cli.grpc.SalesGrpc;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.StatusRuntimeException;


@Controller
@SessionAttributes({"user"})
@RequestMapping("/cotizaciones/")
public class CotizacionesController {
    
    private static final Logger log  = Logger.getLogger(CotizacionesController.class.getName());
    ResourceProject resource = new ResourceProject();

    //dao de procesos comerciales
    @Autowired
    @Qualifier("daoPoc")
    private PocInterfaceDao PocDao;
    
    @Autowired
    @Qualifier("daoGral")
    private GralInterfaceDao gralDao;
    
    @Autowired
    @Qualifier("daoHome")
    private HomeInterfaceDao HomeDao;
    
    public HomeInterfaceDao getHomeDao() {
        return HomeDao;
    }
    
    public PocInterfaceDao getPocDao() {
        return PocDao;
    }
    
    public GralInterfaceDao getGralDao() {
        return gralDao;
    }
    
    @RequestMapping(value="/startup.agnux")
    public ModelAndView startUp(HttpServletRequest request, HttpServletResponse response, 
            @ModelAttribute("user") UserSessionData user)
            throws ServletException, IOException {
        
        log.log(Level.INFO, "Ejecutando starUp de {0}", CotizacionesController.class.getName());
        LinkedHashMap<String,String> infoConstruccionTabla = new LinkedHashMap<String,String>();
        
        infoConstruccionTabla.put("id", "Acciones:90");
        infoConstruccionTabla.put("folio", "Folio:90");
        
        if(user.getIncluyeCrm().equals("true")){
            infoConstruccionTabla.put("tipo", "Tipo:90");
            infoConstruccionTabla.put("cliente", "Cliente/Prospecto:300");
        }else{
            infoConstruccionTabla.put("cliente", "Cliente:300");
        }
        
        infoConstruccionTabla.put("fecha","Fecha:90");
        infoConstruccionTabla.put("fecha_vencimiento","Vencimiento:90");
        infoConstruccionTabla.put("nombre_agente","Agente de Ventas:250");
        
        ModelAndView x = new ModelAndView("cotizaciones/startup", "title", "Cotizaciones");
        
        x = x.addObject("layoutheader", resource.getLayoutheader());
        x = x.addObject("layoutmenu", resource.getLayoutmenu());
        x = x.addObject("layoutfooter", resource.getLayoutfooter());
        x = x.addObject("grid", resource.generaGrid(infoConstruccionTabla));
        x = x.addObject("url", resource.getUrl(request));
        x = x.addObject("username", user.getUserName());
        x = x.addObject("empresa", user.getRazonSocialEmpresa());
        x = x.addObject("sucursal", user.getSucursal());
        x = x.addObject("crm", user.getIncluyeCrm());
        
        String userId = String.valueOf(user.getUserId());
        
        String codificado = Base64Coder.encodeString(userId);
        
        //id de usuario codificado
        x = x.addObject("iu", codificado);
        
        return x;
    }
    
    
    
    
    @RequestMapping(value="/getCotizaciones.json", method = RequestMethod.POST)
    public @ResponseBody HashMap<String,ArrayList<HashMap<String, Object>>> getCotizacionesJson(
           @RequestParam(value="orderby", required=true) String orderby,
           @RequestParam(value="desc", required=true) String desc,
           @RequestParam(value="items_por_pag", required=true) int items_por_pag,
           @RequestParam(value="pag_start", required=true) int pag_start,
           @RequestParam(value="display_pag", required=true) String display_pag,
           @RequestParam(value="input_json", required=true) String input_json,
           @RequestParam(value="cadena_busqueda", required=true) String cadena_busqueda,
           @RequestParam(value="iu", required=true) String id_user_cod,
           Model modcel
    ) {
           
        
        HashMap<String,ArrayList<HashMap<String, Object>>> jsonretorno = new HashMap<String,ArrayList<HashMap<String, Object>>>();
        HashMap<String,String> has_busqueda = StringHelper.convert2hash(StringHelper.ascii2string(cadena_busqueda));
        
        //aplicativo Cotizaciones
        Integer app_selected = 12;
        
        //decodificar id de usuario
        Integer id_usuario = Integer.parseInt(Base64Coder.decodeString(id_user_cod));
        
        //variables para el buscador
        String folio = "%"+StringHelper.isNullString(String.valueOf(has_busqueda.get("folio")))+"%";
        String cliente = "%"+StringHelper.isNullString(String.valueOf(has_busqueda.get("cliente")))+"%";
        String fecha_inicial = ""+StringHelper.isNullString(String.valueOf(has_busqueda.get("fecha_inicial")))+"";
        String fecha_final = ""+StringHelper.isNullString(String.valueOf(has_busqueda.get("fecha_final")))+"";
        String tipo = ""+StringHelper.isNullString(String.valueOf(has_busqueda.get("tipo")))+"";
        String incluye_crm = ""+StringHelper.isNullString(String.valueOf(has_busqueda.get("incluye_crm")))+"";
        String codigo = "%"+StringHelper.isNullString(String.valueOf(has_busqueda.get("codigo")))+"%";
        String producto = "%"+StringHelper.isNullString(String.valueOf(has_busqueda.get("producto")))+"%";
        String agente = ""+StringHelper.isNullString(String.valueOf(has_busqueda.get("agente")))+"";
        
        String data_string = app_selected+"___"+id_usuario+"___"+folio+"___"+cliente+"___"+fecha_inicial+"___"+fecha_final+"___"+tipo+"___"+incluye_crm+"___"+codigo+"___"+producto+"___"+agente;
        
        //obtiene total de registros en base de datos, con los parametros de busqueda
        int total_items = this.getPocDao().countAll(data_string);
        
        //calcula el total de paginas
        int total_pags = resource.calculaTotalPag(total_items,items_por_pag);
        
        //variables que necesita el datagrid, para no tener que hacer uno por cada aplicativo
        DataPost dataforpos = new DataPost(orderby, desc, items_por_pag, pag_start, display_pag, input_json, cadena_busqueda,total_items,total_pags,id_user_cod);
        
        int offset = resource.__get_inicio_offset(items_por_pag, pag_start);
        
        //obtiene los registros para el grid, de acuerdo a los parametros de busqueda
        jsonretorno.put("Data", this.getPocDao().getCotizacion_PaginaGrid(data_string, offset, items_por_pag, orderby, desc));
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
        ArrayList<HashMap<String, String>> arrayExtra = new ArrayList<HashMap<String, String>>();
        HashMap<String, String> extra = new HashMap<String, String>();
        
        //decodificar id de usuario
        Integer id_usuario = Integer.parseInt(Base64Coder.decodeString(id_user_cod));
        userDat = this.getHomeDao().getUserById(id_usuario);
        Integer id_empresa = Integer.parseInt(userDat.get("empresa_id"));
        Integer id_sucursal = Integer.parseInt(userDat.get("sucursal_id"));
        Integer id_agente = Integer.parseInt(userDat.get("empleado_id"));
        boolean obtener_todos_los_agentes=true;
        
        extra = this.getPocDao().getUserRol(id_usuario);
        extra.put("id_agente", String.valueOf(id_agente));
        arrayExtra.add(0,extra);
        
        agentes = this.getPocDao().getAgentes(id_empresa, id_sucursal, obtener_todos_los_agentes);
        
        jsonretorno.put("Extra", arrayExtra);
        jsonretorno.put("Agentes", agentes);
        return jsonretorno;
    }
    
    
    
    //Obtener el Tipo de Cambio de Acuerdo a la Moneda Seleccionada
    @RequestMapping(method = RequestMethod.POST, value="/getValorTc.json")
    public @ResponseBody HashMap<String, String> getValorTcJson(
            @RequestParam(value="idmon", required=false) String idmon,
            Model model
        ) {
        
        HashMap<String, String> jsonretorno = new HashMap<String, String>();
        
        if(idmon==null || idmon.equals("")){
            idmon="0";
        }
        
        jsonretorno= this.getPocDao().getTipoCambioActualPorIdMoneda(Integer.parseInt(idmon));
        
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
    
    
    
    @RequestMapping(method = RequestMethod.POST, value="/getCotizacion.json")
    public @ResponseBody HashMap<String,ArrayList<HashMap<String, String>>> getCotizacionJson(
            @RequestParam(value="id_cotizacion", required=true) String id_cotizacion,
            @RequestParam(value="iu", required=true) String id_user_cod,
            Model model
        ) {
        
        log.log(Level.INFO, "Ejecutando getCotizacionJson de {0}", CotizacionesController.class.getName());
        HashMap<String,ArrayList<HashMap<String, String>>> jsonretorno = new HashMap<String,ArrayList<HashMap<String, String>>>();
        HashMap<String, String> userDat = new HashMap<String, String>();
        ArrayList<HashMap<String, String>> datosCotizacion = new ArrayList<HashMap<String, String>>();
        ArrayList<HashMap<String, String>> DatosCliPros = new ArrayList<HashMap<String, String>>();
        ArrayList<HashMap<String, String>> datosGrid = new ArrayList<HashMap<String, String>>();
        ArrayList<HashMap<String, String>> valorIva = new ArrayList<HashMap<String, String>>();
        ArrayList<HashMap<String, String>> monedas = new ArrayList<HashMap<String, String>>();
        ArrayList<HashMap<String, String>> arrayExtra = new ArrayList<HashMap<String, String>>();
        HashMap<String, String> extra = new HashMap<String, String>();
        HashMap<String, String> tc = new HashMap<String, String>();
        ArrayList<HashMap<String, String>> tipoCambioActual = new ArrayList<HashMap<String, String>>();
        HashMap<String, String> parametros = new HashMap<String, String>();
        
        //decodificar id de usuario
        Integer id_usuario = Integer.parseInt(Base64Coder.decodeString(id_user_cod));
        userDat = this.getHomeDao().getUserById(id_usuario);
        Integer id_empresa = Integer.parseInt(userDat.get("empresa_id"));
        Integer id_sucursal = Integer.parseInt(userDat.get("sucursal_id"));
        String dirImgProd="";
        boolean obtener_todos_los_agentes=false;
        
        //Aqui se obtienen los parametros de la facturacion, nos intersa saber si se debe permitir cambiar_unidad_medida
        parametros = this.getPocDao().getPocPedido_Parametros(id_empresa, id_sucursal);
        extra.put("cambioUM", parametros.get("cambiar_unidad_medida"));
        extra.put("mod_crm", userDat.get("incluye_crm"));
        arrayExtra.add(0,extra);
        
        if( !id_cotizacion.equals("0")  ){
            datosCotizacion = this.getPocDao().getCotizacion_Datos(Integer.parseInt(id_cotizacion));
            if(datosCotizacion.get(0).get("tipo").equals("1")){
                DatosCliPros = this.getPocDao().getCotizacion_DatosCliente(Integer.parseInt(id_cotizacion));
            }else{
                DatosCliPros = this.getPocDao().getCotizacion_DatosProspecto(Integer.parseInt(id_cotizacion));
            }
            datosGrid = this.getPocDao().getCotizacion_DatosGrid(Integer.parseInt(id_cotizacion));
            
            obtener_todos_los_agentes=true;
        }
        
        
        valorIva= this.getPocDao().getValoriva(id_sucursal);
        monedas = this.getPocDao().getMonedas();
        tc.put("tipo_cambio", StringHelper.roundDouble(this.getPocDao().getTipoCambioActual(), 4));
        tipoCambioActual.add(0,tc);
        
        
        //if(parametros.get("cambiar_unidad_medida").toLowerCase().equals("true")){
            jsonretorno.put("UM", this.getPocDao().getUnidadesMedida());
        //}
        
        jsonretorno.put("datosCotizacion", datosCotizacion);
        jsonretorno.put("DatosCP", DatosCliPros);
        jsonretorno.put("datosGrid", datosGrid);
        jsonretorno.put("iva", valorIva);
        jsonretorno.put("Monedas", monedas);
        jsonretorno.put("Extras", arrayExtra);
        jsonretorno.put("Tc", tipoCambioActual);
        jsonretorno.put("Agentes", this.getPocDao().getAgentes(id_empresa, id_sucursal, obtener_todos_los_agentes));
        jsonretorno.put("Incoterms", this.getPocDao().getCotizacion_Incoterms(id_empresa, Integer.parseInt(id_cotizacion)));
        
        return jsonretorno;
    }
    
    
    
    //Buscador de Clientes ó Prospectos segun el parametro Tipo de busqueda
    @RequestMapping(method = RequestMethod.POST, value="/getBuscadorClienteProspecto.json")
    public @ResponseBody HashMap<String,ArrayList<HashMap<String, String>>> getBuscadorClienteProspectoJson(
            @RequestParam(value="tipo", required=true) String tipo,
            @RequestParam(value="cadena", required=true) String cadena,
            @RequestParam(value="filtro", required=true) Integer filtro,
            @RequestParam(value="iu", required=true) String id_user,
            Model model
        ) {
        
        HashMap<String,ArrayList<HashMap<String, String>>> jsonretorno = new HashMap<String,ArrayList<HashMap<String, String>>>();
        HashMap<String, String> userDat = new HashMap<String, String>();
        ArrayList<HashMap<String, String>> datos = new ArrayList<HashMap<String, String>>();
        
        //decodificar id de usuario
        Integer id_usuario = Integer.parseInt(Base64Coder.decodeString(id_user));
        
        userDat = this.getHomeDao().getUserById(id_usuario);
        
        Integer id_empresa = Integer.parseInt(userDat.get("empresa_id"));
        Integer id_sucursal = Integer.parseInt(userDat.get("sucursal_id"));
        
        if(tipo.equals("1")){
           String permite_descto="false";
            //buscar clientes
            datos = this.getPocDao().getBuscadorClientes(cadena,filtro,id_empresa,id_sucursal, permite_descto);
        }else{
            //buscar Prospectos
            datos = this.getPocDao().getBuscadorProspectos(cadena,filtro,id_empresa,id_sucursal);
        }
        
        jsonretorno.put("Resultado", datos);
        
        return jsonretorno;
    }
    
    
    
    //Buscador de clientes
    @RequestMapping(method = RequestMethod.POST, value="/getDataByNoControl.json")
    public @ResponseBody HashMap<String,ArrayList<HashMap<String, String>>> getDataByNoControlJson(
            @RequestParam(value="tipo", required=true) String tipo,
            @RequestParam(value="no_control", required=true) String no_control,
            @RequestParam(value="iu", required=true) String id_user,
            Model model
        ) {
        
        HashMap<String,ArrayList<HashMap<String, String>>> jsonretorno = new HashMap<String,ArrayList<HashMap<String, String>>>();
        HashMap<String, String> userDat = new HashMap<String, String>();
       ArrayList<HashMap<String, String>> datos = new ArrayList<HashMap<String, String>>();
       
        //decodificar id de usuario
        Integer id_usuario = Integer.parseInt(Base64Coder.decodeString(id_user));
        
        userDat = this.getHomeDao().getUserById(id_usuario);
        
        Integer id_empresa = Integer.parseInt(userDat.get("empresa_id"));
        Integer id_sucursal = Integer.parseInt(userDat.get("sucursal_id"));
        
        if(tipo.equals("1")){
            String permite_descto="false";
            //obtener datos de cliente
            datos = this.getPocDao().getDatosClienteByNoCliente(no_control,id_empresa,id_sucursal, permite_descto);
        }else{
            //obtener datos de prospecto
            datos = this.getPocDao().getDatosProspectoByNoControl(no_control,id_empresa,id_sucursal);
        }
        
        jsonretorno.put("Resultado", datos);
        
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
        arrayTiposProducto=this.getPocDao().getProductoTipos();
        jsonretorno.put("prodTipos", arrayTiposProducto);
        
        return jsonretorno;
    }
    
    
    //Buscador de productos
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
        
        jsonretorno.put("productos", this.getPocDao().getBuscadorProductos(sku,tipo,descripcion,id_empresa));
        
        return jsonretorno;
    }
    
    
    //Buscador de presentaciones de producto
    @RequestMapping(method = RequestMethod.POST, value="/getPresentacionesProducto.json")
    public @ResponseBody HashMap<String,ArrayList<HashMap<String, String>>> getPresentacionesProductoJson(
            @RequestParam(value="sku", required=true) String sku,
            @RequestParam(value="lista_precio",required=true) String lista_precio,
            @RequestParam(value="tipo",required=true) String tipo,
            @RequestParam(value="id_client", required=true) String idClient,
            @RequestParam(value="iu", required=true) String id_user,
            Model model
        ) {
        
        HashMap<String,ArrayList<HashMap<String, String>>> jsonretorno = new HashMap<String,ArrayList<HashMap<String, String>>>();
        HashMap<String, String> userDat = new HashMap<String, String>();
        ArrayList<HashMap<String, String>> ArrayPres = new ArrayList<HashMap<String, String>>();
        ArrayList<HashMap<String, String>> ArrayPresProcesado = new ArrayList<HashMap<String, String>>();
        ArrayList<HashMap<String, String>> datos = new ArrayList<HashMap<String, String>>();
        HashMap<String, String> tipoCambio = new HashMap<String, String>();
        
        //decodificar id de usuario
        Integer id_usuario = Integer.parseInt(Base64Coder.decodeString(id_user));
        
        userDat = this.getHomeDao().getUserById(id_usuario);
        Integer id_empresa = Integer.parseInt(userDat.get("empresa_id"));
        Integer id_sucursal = Integer.parseInt(userDat.get("sucursal_id"));
        
        ArrayPres = this.getPocDao().getPresentacionesProducto(sku, lista_precio, id_empresa);
        ArrayPresProcesado = this.getPocDao().getVerificarImpuesto(id_sucursal, Integer.parseInt(idClient), ArrayPres);
        Integer idmon = Integer.parseInt(ArrayPres.get(0).get("id_moneda"));
        
        tipoCambio = this.getPocDao().getTipoCambioActualPorIdMoneda(idmon);
        datos.add(tipoCambio);
        
        jsonretorno.put("Presentaciones", ArrayPresProcesado);
        jsonretorno.put("Datos", datos);
        
        return jsonretorno;
    }
    
    
    
    
    //obtiene la moneda de la lista de precios del cliente
    @RequestMapping(method = RequestMethod.POST,value="/getMonedaLista.json")
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
    
    
    
    
    //descargtar imagen
    @RequestMapping(method = RequestMethod.GET, value="/imgDownloadImg/{name_img}/{id}/{iu}/out.json")
    public @ResponseBody HashMap<String, String> imgDownloadImgJson(
        @PathVariable("name_img") String name_img,
        @PathVariable("id") String id,
        @PathVariable("iu") String id_user,
        HttpServletResponse response, 
        Model model
    ) throws IOException {
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
        
        if(file.exists()){
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
        }
        
        return null;
    }
    
    
    //edicion y nuevo
    @RequestMapping(method = RequestMethod.POST, value="/edit.json")
    public @ResponseBody HashMap<String, String> editJson(
        @RequestParam(value="id_cotizacion", required=true) Integer identificador,
        @RequestParam(value="select_tipo_cotizacion", required=true) String select_tipo_cotizacion,
        @RequestParam(value="id_cliente", required=true) String id_cliente,
        @RequestParam(value="observaciones", required=true) String observaciones,
        @RequestParam(value="check_descripcion_larga", required=false) String check_descripcion_larga,
        @RequestParam(value="check_incluye_iva", required=false) String check_incluye_iva,
        @RequestParam(value="select_accion", required=true) String select_accion,
        @RequestParam(value="vigencia", required=true) String vigencia,
        @RequestParam(value="tc", required=true) String tc,
        @RequestParam(value="tc_usd", required=true) String tc_usd,
        @RequestParam(value="moneda", required=true) String moneda_id,
        @RequestParam(value="fecha", required=true) String fecha,
        @RequestParam(value="select_agente", required=true) String select_agente,

        @RequestParam(value="total_tr", required=true) String total_tr,
        @RequestParam(value="iddetalle", required=true) String[] iddetalle,
        @RequestParam(value="eliminado", required=true) String[] eliminado,
        @RequestParam(value="idproducto", required=true) String[] idproducto,
        @RequestParam(value="select_umedida", required=false) String[] select_umedida,
        @RequestParam(value="id_presentacion", required=true) String[] id_presentacion,
        @RequestParam(value="cantidad", required=true) String[] cantidad,
        @RequestParam(value="precio", required=true) String[] precio,
        @RequestParam(value="monedagrid", required=true) String[] monedagrid,
        @RequestParam(value="id_imp_prod", required=true) String[] id_imp_prod,
        @RequestParam(value="valor_imp", required=true) String[] valor_imp,
        @RequestParam(value="notr", required=true) String[] notr,
        @RequestParam(value="select_incoterms", required=false) String[] select_incoterms,
        @RequestParam(value="statusreg", required=true) String[] statusreg,
        @RequestParam(value="reqauth", required=true) String[] reqauth,
        @RequestParam(value="success", required=true) String[] salvar_registro,

        @ModelAttribute("user") UserSessionData user,
        Model model)
    {
        String arreglo[] = new String[eliminado.length];
        Integer id_usuario= user.getUserId();
        Integer app_selected = 12;
        String command_selected = select_accion;

        check_descripcion_larga = StringHelper.verificarCheckBox(check_descripcion_larga);
        check_incluye_iva = StringHelper.verificarCheckBox(check_incluye_iva);
        
        int cotId = identificador.intValue();
        if (command_selected.equals("new") && cotId != 0) {
            cotId = 0;
        }

        CotRequest.Builder cotRequestBuilder = CotRequest.newBuilder();

        for(int i=0; i<eliminado.length; i++) { 
            select_umedida[i] = StringHelper.verificarSelect(select_umedida[i]);

            System.out.println("statusreg[i]: "+statusreg[i]);

            //statreg&&&valuereg&&&ident
            String partida[] = statusreg[i].split("\\&&&");
            //partida[0]    Estatus autorizacion
            //partida[1]    Valor precio autorizado
            //partida[2]    Usuario que autoriza

            String stat_reg = (StringHelper.isNullString(String.valueOf(partida[0])).equals("0"))? "false":"true";
            String precio_autorizado = StringHelper.isNullString(String.valueOf(partida[1]));
            String id_user_autoriza = (partida[2].trim().equals("0"))?partida[2]:Base64Coder.decodeString(StringHelper.isNullString(String.valueOf(partida[2])));

            int detalleId = Integer.parseInt(iddetalle[i]);
            if (command_selected.equals("new") && detalleId != 0) {
                detalleId = 0;
            }
            
            //Imprimir el contenido de cada celda 
            arreglo[i] = "'" +
                eliminado[i]                            + "___" + 
                String.valueOf(detalleId)               + "___" + 
                idproducto[i]                           + "___" + 
                id_presentacion[i]                      + "___" + 
                cantidad[i]                             + "___" + 
                StringHelper.removerComas(precio[i])    + "___" + 
                monedagrid[i]                           + "___" +
                notr[i]                                 + "___" +
                id_imp_prod[i]                          + "___" +
                valor_imp[i]                            + "___" +
                select_umedida[i]                       + "___" +
                stat_reg                                + "___" +
                precio_autorizado                       + "___" +
                id_user_autoriza                        + "___" +
                reqauth[i]                              + "___" +
                salvar_registro[i]                      + "'";
            System.out.println("arreglo[" + i + "] = " + arreglo[i]);

            
            cotRequestBuilder.addExtraData(
                CotRequest.GridRenglonCot.newBuilder()
                    .setRemovido(Helper.toInt(eliminado[i]))
                    .setIdDetalle(detalleId)
                    .setIdProducto(Helper.toInt(idproducto[i]))
                    .setIdPresentacion(Helper.toInt(id_presentacion[i]))
                    .setCantidad(Helper.toDouble(cantidad[i]))
                    .setPrecio(Helper.toDouble(StringHelper.removerComas(precio[i])))
                    .setMonedaGrId(Helper.toInt(monedagrid[i]))
                    .setNotr(notr[i])
                    .setIdImpProd(Helper.toInt(id_imp_prod[i]))
                    .setValorImp(Helper.toDouble(valor_imp[i]))
                    .setUnidadId(Helper.toInt(select_umedida[i]))
                    .setStatusAutorizacion(Boolean.parseBoolean(stat_reg))
                    .setPrecioAutorizado(Helper.toDouble(precio_autorizado))
                    .setIdUserAut(Helper.toInt(id_user_autoriza))
                    .setRequiereAutorizacion(Boolean.parseBoolean(reqauth[i]))
                    .setSalvarRegistro(salvar_registro[i]));
        }

        //Serializar el arreglo
        String extra_data_array = StringUtils.join(arreglo, ",");

        //System.out.println("select_incoterms: "+select_incoterms);
        String incoterms="";
        int primerIncoterm = 0;
        if (select_incoterms != null){

            for (int i=0; i<select_incoterms.length; i++) { 

                if (primerIncoterm==0){
                    incoterms = select_incoterms[i];
                    primerIncoterm++;

                } else {
                    incoterms += ","+select_incoterms[i];
                    primerIncoterm++;
                }
            }
        }

        String data_string =
            app_selected                + "___" +
            command_selected            + "___" +
            id_usuario                  + "___" +
            String.valueOf(cotId)       + "___" +
            select_tipo_cotizacion      + "___" +
            id_cliente                  + "___" +
            check_descripcion_larga     + "___" +
            observaciones.toUpperCase() + "___" +
            tc                          + "___" +
            moneda_id                   + "___" +
            fecha                       + "___" +
            select_agente               + "___" +
            vigencia                    + "___" +
            check_incluye_iva           + "___" +
            incoterms                   + "___" +
            tc_usd;

        cotRequestBuilder
            .setUsuarioId(id_usuario.intValue())
            .setIdentificador(cotId)
            .setSelectTipoCotizacion(Helper.toInt(select_tipo_cotizacion))
            .setIdClienteOProspecto(Helper.toInt(id_cliente))
            .setCheckDescripcionLarga(Boolean.parseBoolean(check_descripcion_larga))
            .setObservaciones(observaciones.toUpperCase())
            .setTipoCambio(Helper.toDouble(tc))
            .setMonedaId(Helper.toInt(moneda_id))
            .setFecha(fecha)
            .setAgenteId(Helper.toInt(select_agente))
            .setVigencia(Helper.toInt(vigencia))
            .setIncluyeIva(Boolean.parseBoolean(check_incluye_iva))
            .setTcUSD(Helper.toDouble(tc_usd));

        HashMap<String, String> jsonretorno = new HashMap<String, String>();
        HashMap<String, String> success = this.getPocDao()
            .selectFunctionValidateAaplicativo(data_string, app_selected, extra_data_array);

        log.log(Level.INFO, "Resultado de validacion Cot: {0}", success.get("success"));

        if(success.get("success").equals("true")) {
            
            ManagedChannel channel = ManagedChannelBuilder.forTarget(Helper.getGrpcConnString())
                .usePlaintext()
                .build();
            
            SalesGrpc.SalesBlockingStub blockingStub = SalesGrpc.newBlockingStub(channel);

            CotRequest cotRequest = cotRequestBuilder.build();
            CotResponse cotResponse;

            try {
                cotResponse = blockingStub.editCot(cotRequest);
                String valorRetorno = cotResponse.getValorRetorno();

                if (valorRetorno.equals("1")) {
                    jsonretorno.put("success", "true");

                } else {
                    jsonretorno.put("success", valorRetorno);
                }
                log.log(Level.INFO, "Cot Response valorRetorno: {0}", valorRetorno);
            
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
    
    
    
    //cambiar a borrado logico un registro
    @RequestMapping(method = RequestMethod.POST, value="/logicDelete.json")
    public @ResponseBody HashMap<String, String> logicDeleteJson(
            @RequestParam(value="id_cotizacion", required=true) String id,
            @RequestParam(value="iu", required=true) String id_user,
            Model model
        ) {
        HashMap<String, String> jsonretorno = new HashMap<String, String>();
        
        //decodificar id de usuario
        Integer id_usuario = Integer.parseInt(Base64Coder.decodeString(id_user));
        
        Integer app_selected = 12;
        String command_selected = "delete";
        String extra_data_array = "'sin datos'";
        String data_string = app_selected+"___"+command_selected+"___"+id_usuario+"___"+id;
        
        System.out.println("Ejecutando borrado logico de una Cotizacion");
        jsonretorno.put("success",String.valueOf( this.getPocDao().selectFunctionForThisApp(data_string,extra_data_array)) );
        
        return jsonretorno;
    }
    
    

    
    
    
    
    
    @RequestMapping(value = "/getGeneraPdfCotizacion/{id_cotizacion}/{incluye_img}/{incluye_iva}/{iu}/out.json", method = RequestMethod.GET ) 
    public ModelAndView getGeneraPdfCotizacionJson(
                @PathVariable("id_cotizacion") Integer id_cotizacion,
                @PathVariable("incluye_img") String incluye_img,
                @PathVariable("incluye_iva") String incluye_iva,
                @PathVariable("iu") String id_user,
                HttpServletRequest request, 
                HttpServletResponse response, 
                Model model)
            throws ServletException, IOException, URISyntaxException, DocumentException, Exception {
        
        HashMap<String, String> userDat = new HashMap<String, String>();
        HashMap<String, String> HeaderFooter = new HashMap<String, String>();
        HashMap<String, String> datos = new HashMap<String, String>();
        HashMap<String, String> saludo = new HashMap<String, String>();
        HashMap<String, String> despedida = new HashMap<String, String>();
        HashMap<String, String> datosEmisor = new HashMap<String, String>();
        HashMap<String, String> datosReceptor = new HashMap<String, String>();
        ArrayList<HashMap<String, String>> datosCotizacion = new ArrayList<HashMap<String, String>>();
        ArrayList<HashMap<String, String>> datosCliPros = new ArrayList<HashMap<String, String>>();
        ArrayList<HashMap<String, String>> lista_productos = new ArrayList<HashMap<String, String>>();
        
        ArrayList<HashMap<String, String>> condiciones_comerciales = new ArrayList<HashMap<String, String>>();
        ArrayList<HashMap<String, String>> politicas_pago = new ArrayList<HashMap<String, String>>();
        ArrayList<HashMap<String, String>> incoterms = new ArrayList<HashMap<String, String>>();
        
        System.out.println("Generando PDF de Cotizacion");
        
        Integer app_selected = 12; //aplicativo Cotizaciones
        
        //decodificar id de usuario
        Integer id_usuario = Integer.parseInt(Base64Coder.decodeString(id_user));
        
        userDat = this.getHomeDao().getUserById(id_usuario);
        Integer id_empresa = Integer.parseInt(userDat.get("empresa_id"));
        
        String rfc_empresa = this.getGralDao().getRfcEmpresaEmisora(id_empresa);
        String razon_social_empresa = this.getGralDao().getRazonSocialEmpresaEmisora(id_empresa);
        

        //directorio de imagenes de productos
        String dirImgProd = this.getGralDao().getProdImgDir()+rfc_empresa+"/";
        
        //ruta del la imagen del Logotipo
        String rutaLogoEmpresa = String.format("%s%s_logo.jpg", this.getGralDao().getImagesDir(), rfc_empresa);
        File logoFile = new File(rutaLogoEmpresa);

        if (!logoFile.exists()) {
            rutaLogoEmpresa = rutaLogoEmpresa.replace(".jpg", ".png");
        }
        
        String finalFilename = String.format("COT_%s.pdf", rfc_empresa);
        
        
        datosEmisor.put("emp_razon_social", razon_social_empresa);
        datosEmisor.put("emp_rfc", this.getGralDao().getRfcEmpresaEmisora(id_empresa));
        datosEmisor.put("emp_calle", this.getGralDao().getCalleDomicilioFiscalEmpresaEmisora(id_empresa));
        datosEmisor.put("emp_no_exterior", this.getGralDao().getNoExteriorDomicilioFiscalEmpresaEmisora(id_empresa));
        datosEmisor.put("emp_colonia", this.getGralDao().getColoniaDomicilioFiscalEmpresaEmisora(id_empresa));
        datosEmisor.put("emp_pais", this.getGralDao().getPaisDomicilioFiscalEmpresaEmisora(id_empresa));
        datosEmisor.put("emp_estado", this.getGralDao().getEstadoDomicilioFiscalEmpresaEmisora(id_empresa));
        datosEmisor.put("emp_municipio", this.getGralDao().getMunicipioDomicilioFiscalEmpresaEmisora(id_empresa));
        datosEmisor.put("emp_cp", this.getGralDao().getCpDomicilioFiscalEmpresaEmisora(id_empresa));
        datosEmisor.put("emp_pagina_web", this.getGralDao().getPaginaWebEmpresaEmisora(id_empresa));
        datosEmisor.put("emp_telefono", this.getGralDao().getTelefonoEmpresaEmisora(id_empresa));
        
        HeaderFooter.put("titulo_reporte", "COTIZACIÓN");
        HeaderFooter.put("periodo", "");
        HeaderFooter.put("empresa", "");
        HeaderFooter.put("codigo1", this.getGralDao().getCodigo1Iso(id_empresa, app_selected));
        HeaderFooter.put("codigo2", this.getGralDao().getCodigo2Iso(id_empresa, app_selected));
        
        datosCotizacion = this.getPocDao().getCotizacion_Datos(id_cotizacion);
        saludo = this.getPocDao().getCotizacion_Saludo(id_empresa);
        despedida = this.getPocDao().getCotizacion_Despedida(id_empresa);
        //obtiene las condiciones comerciales para la cotizacion
        condiciones_comerciales = this.getPocDao().getCotizacion_CondicionesComerciales(id_empresa);
        
        //Obtiene las politicas de pago para la cotizacion
        politicas_pago = this.getPocDao().getCotizacion_PolitizasPago(id_empresa);
        
        incoterms = this.getPocDao().getCotizacion_Incoterms(id_empresa, id_cotizacion);
        
        lista_productos = this.getPocDao().getCotizacion_DatosGrid(id_cotizacion);
        datos.put("ruta_logo", rutaLogoEmpresa);
        datos.put("dirImagenes", dirImgProd);
        datos.put("tipo", datosCotizacion.get(0).get("tipo"));
        datos.put("folio", datosCotizacion.get(0).get("folio"));
        datos.put("fecha", datosCotizacion.get(0).get("fecha"));
        datos.put("tc_usd", datosCotizacion.get(0).get("tc_usd"));
        datos.put("observaciones", datosCotizacion.get(0).get("observaciones"));
        datos.put("img_desc", incluye_img);
        datos.put("nombre_usuario", datosCotizacion.get(0).get("nombre_usuario"));
        datos.put("puesto_usuario", datosCotizacion.get(0).get("puesto_usuario"));
        datos.put("correo_agente", datosCotizacion.get(0).get("correo_agente"));
        datos.put("saludo", saludo.get("saludo"));
        datos.put("despedida", despedida.get("despedida"));
        datos.put("subtotal", datosCotizacion.get(0).get("subtotal"));
        datos.put("impuesto", datosCotizacion.get(0).get("impuesto"));
        datos.put("total", datosCotizacion.get(0).get("total"));
        datos.put("dias_vigencia", datosCotizacion.get(0).get("dias_vigencia"));
        datos.put("monedaAbr", datosCotizacion.get(0).get("monedaAbr"));
        
        //esta variable viene desde la vista
        //hay un campo en la base de datos pero, no se esta utilizando en el pdf
        datos.put("incluiyeIvaPdf", incluye_iva);
        
        
        if(datosCotizacion.get(0).get("tipo").equals("1")){
            datosCliPros = this.getPocDao().getCotizacion_DatosCliente(id_cotizacion);
        }else{
            datosCliPros = this.getPocDao().getCotizacion_DatosProspecto(id_cotizacion);
        }
        
        datosReceptor.put("clieCalle", datosCliPros.get(0).get("calle"));
        datosReceptor.put("clieNumero", datosCliPros.get(0).get("numero"));
        datosReceptor.put("clieColonia", datosCliPros.get(0).get("colonia"));
        datosReceptor.put("clieMunicipio", datosCliPros.get(0).get("municipio"));
        datosReceptor.put("clieEstado", datosCliPros.get(0).get("estado"));
        datosReceptor.put("cliePais", datosCliPros.get(0).get("pais"));
        datosReceptor.put("clieCp", datosCliPros.get(0).get("cp"));
        datosReceptor.put("clieTel", datosCliPros.get(0).get("telefono"));
        datosReceptor.put("clieRfc", datosCliPros.get(0).get("rfc"));
        datosReceptor.put("clieContacto", datosCliPros.get(0).get("contacto"));
        datosReceptor.put("clieRazonSocial", datosCliPros.get(0).get("razon_social"));
                
        PdfCotizacion pdf = new PdfCotizacion(
                HeaderFooter,
                datosEmisor,
                datos,
                datosReceptor,
                lista_productos,
                condiciones_comerciales,
                politicas_pago,
                incoterms);

        byte[] pdfBytes = pdf.createPDF();
        ByteArrayInputStream pdfInputStream = new ByteArrayInputStream(pdfBytes);

        response.setBufferSize(pdfBytes.length);
        response.setContentLength(pdfBytes.length);
        response.setContentType("application/pdf");
        response.setHeader("Content-Disposition","attachment; filename=\"" + finalFilename +"\"");
        FileCopyUtils.copy(pdfInputStream, response.getOutputStream());
        response.flushBuffer();
        
        return null;
    }
}
