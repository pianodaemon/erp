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
import com.agnux.kemikal.reportes.pdfCotizacion;
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


@Controller
@SessionAttributes({"user"})
@RequestMapping("/remisionesIMSS/")
public class RemisionesIMSSController {
    
    private static final Logger log  = Logger.getLogger(RemisionesIMSSController.class.getName());
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
        
        log.log(Level.INFO, "Ejecutando starUp de {0}", RemisionesIMSSController.class.getName());
        LinkedHashMap<String,String> infoConstruccionTabla = new LinkedHashMap<String,String>();
        
        infoConstruccionTabla.put("id", "Acciones:90");
        infoConstruccionTabla.put("folio", "id:90");
        
        if(user.getIncluyeCrm().equals("true")){
            infoConstruccionTabla.put("tipo", "Tipo:90");
            infoConstruccionTabla.put("cliente", "Cliente/Prospecto:300");
        }else{
            infoConstruccionTabla.put("cliente", "Cliente:120");
        }
        infoConstruccionTabla.put("numero_contrato","N&uacute;m. Contrato.:120");
        infoConstruccionTabla.put("fecha_expedicion","Fecha Exp.:90");
        infoConstruccionTabla.put("fecha_pago","Fecha Pago:90");
        infoConstruccionTabla.put("descripcion","Estatus:120");
        
        ModelAndView x = new ModelAndView("remisionesIMSS/startup", "title", "Remisiones IMSS");
        
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
    
    
    
    
    @RequestMapping(value="/getRemisionesIMSS.json", method = RequestMethod.POST)
    //public @ResponseBody HashMap<String,ArrayList<HashMap<String, Object>>> getCotizacionesJson(
    public @ResponseBody HashMap<String,ArrayList<HashMap<String, Object>>> getRemisionesIMSSJson(
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
           
        System.out.println("Entrando a getRemisionesIMSS.json...");
        HashMap<String,ArrayList<HashMap<String, Object>>> jsonretorno = new HashMap<String,ArrayList<HashMap<String, Object>>>();
        HashMap<String,String> has_busqueda = StringHelper.convert2hash(StringHelper.ascii2string(cadena_busqueda));
        System.out.println("has_busqueda="+has_busqueda);
        
        //aplicativo Cotizaciones
        Integer app_selected = 210;
        
        //decodificar id de usuario
        Integer id_usuario = Integer.parseInt(Base64Coder.decodeString(id_user_cod));
        
        //variables para el buscador
        String folio = "'"+StringHelper.isNullString(String.valueOf(has_busqueda.get("folio")))+"'";
        String cliente = "'"+StringHelper.isNullString(String.valueOf(has_busqueda.get("cliente")))+"'";
        String fecha_inicial = "'"+StringHelper.isNullString(String.valueOf(has_busqueda.get("fecha_inicial")))+"'";
        String fecha_final = "'"+StringHelper.isNullString(String.valueOf(has_busqueda.get("fecha_final")))+"'";
        String tipo = "'"+StringHelper.isNullString(String.valueOf(has_busqueda.get("tipo")))+"'";
        String incluye_crm = "'"+StringHelper.isNullString(String.valueOf(has_busqueda.get("incluye_crm")))+"'";
        String codigo = "'"+StringHelper.isNullString(String.valueOf(has_busqueda.get("codigo")))+"'";
        String producto = "'"+StringHelper.isNullString(String.valueOf(has_busqueda.get("producto")))+"'";
        String agente = "'"+StringHelper.isNullString(String.valueOf(has_busqueda.get("agente")))+"'";
        String status = "'"+StringHelper.isNullString(String.valueOf(has_busqueda.get("status")))+"'";
        String folioIMSS = "'"+StringHelper.isNullString(String.valueOf(has_busqueda.get("busqueda_folioIMSS")))+"'";
        String numContrato = "'"+StringHelper.isNullString(String.valueOf(has_busqueda.get("busqueda_numContrato")))+"'";

        
        
        String data_string = app_selected+"___"+id_usuario+"___"+folio+"___"+cliente+"___"+fecha_inicial+"___"+fecha_final+"___"+tipo+"___"+incluye_crm+"___"+folioIMSS+"___"+numContrato+"___"+status;
        System.out.println("data_string="+data_string);
        //obtiene total de registros en base de datos, con los parametros de busqueda
        //int total_items = this.getPocDao().countAll(data_string);
        
        int total_items = 3;
        
        //calcula el total de paginas
        int total_pags = resource.calculaTotalPag(total_items,items_por_pag);
        
        //variables que necesita el datagrid, para no tener que hacer uno por cada aplicativo
        DataPost dataforpos = new DataPost(orderby, desc, items_por_pag, pag_start, display_pag, input_json, cadena_busqueda,total_items,total_pags,id_user_cod);
        
        int offset = resource.__get_inicio_offset(items_por_pag, pag_start);
        
        //obtiene los registros para el grid, de acuerdo a los parametros de busqueda
        //jsonretorno.put("Data", this.getPocDao().getCotizacion_PaginaGrid(data_string, offset, items_por_pag, orderby, desc));
        jsonretorno.put("Data", this.getPocDao().getRemisionesIMSS_PaginaGrid(data_string, offset, items_por_pag, orderby, desc));
        
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
    
    
    //NLE: Método para obtener los Estatus de Remisiones IMSS
    @RequestMapping(method = RequestMethod.POST, value="/getStatusRemisionIMSS.json")
    public @ResponseBody ArrayList<HashMap<String, String>> getStatusRemisionIMSSJson(
            Model model
        ) {
        
        ArrayList<HashMap<String, String>> jsonretorno = new ArrayList<HashMap<String, String>>();
        
        jsonretorno= this.getPocDao().getStatusRemisionIMSS();
        
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
        
        log.log(Level.INFO, "Ejecutando getCotizacionJson de {0}", RemisionesIMSSController.class.getName());
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
    
    
    @RequestMapping(method = RequestMethod.POST, value="/edit.json")
    public @ResponseBody HashMap<String, String> editJson2(
           @RequestParam(value="folio", required=true) String identificador,
           @RequestParam(value="numeroContrato", required=true) String numeroContrato,
           @RequestParam(value="folioIMSS", required=true) String folioIMSS,
           @RequestParam(value="conducto_pago", required=true) String conducto_pago,
           @RequestParam(value="total", required=true) String total,
           @RequestParam(value="fecha", required=true) String fecha,
           @RequestParam(value="fecha2", required=true) String fecha2,
           @RequestParam(value="statusRemisionIMSS", required=true) String statusRemisionIMSS,
           @RequestParam(value="doc1", required=true) String doc1,
           @RequestParam(value="doc2", required=true) String doc2,
           @RequestParam(value="doc3", required=true) String doc3,
           @RequestParam(value="doc4", required=true) String doc4,
           @RequestParam(value="doc5", required=true) String doc5,
           @RequestParam(value="doc6", required=true) String doc6,
           @RequestParam(value="doc7", required=true) String doc7,
           @RequestParam(value="doc8", required=true) String doc8,
           @RequestParam(value="doc9", required=true) String doc9,
           @RequestParam(value="doc10", required=true) String doc10,
           @ModelAttribute("user") UserSessionData user,
           Model model

    ){
        //log.log(Level.INFO, "edit.json {0}", "Ok!");
        HashMap<String, String> jsonretorno = new HashMap<String, String>();
        HashMap<String, String> succes = new HashMap<String, String>();
        System.out.println("Entró al Controller de Remisiones IMSS...");
        try {
            System.out.println("identificador="+identificador);
            System.out.println("numeroContrato="+numeroContrato);
            System.out.println("folioIMSS="+folioIMSS);
            System.out.println("conducto_pago="+conducto_pago);
            System.out.println("total="+total);
            System.out.println("fecha="+fecha);
            System.out.println("fecha2="+fecha2);
            System.out.println("statusRemisionIMSS="+statusRemisionIMSS);
            System.out.println("doc1="+doc1);
            System.out.println("doc2="+doc2);
            System.out.println("doc3="+doc3);
            System.out.println("doc4="+doc4);
            System.out.println("doc5="+doc5);
            System.out.println("doc6="+doc6);
            System.out.println("doc7="+doc7);
            System.out.println("doc8="+doc8);
            System.out.println("doc9="+doc9);
            System.out.println("doc10="+doc10);
            
            String arreglo[];
            //arreglo = new String[eliminado.length];
            Integer id_usuario = user.getUserId();//variable para el id  del usuario
            int app_selected = 12;
            String command_selected = "new";
            String actualizo = "0";
            
            //command_selected=select_accion;
            String data_string = identificador + "___"+ numeroContrato + "___"+ folioIMSS + "___"+ conducto_pago + "___"+ total + "___"+ fecha + "___"+ fecha2 + "___"+ statusRemisionIMSS + "___"+ doc1 + "___" + doc2 + "___" + doc3 + "___" + doc4 + "___" + doc5 + "___" + doc6 + "___" + doc7 + "___" + doc8 + "___" + doc9 + "___" + doc10 + "___" + id_usuario.intValue();
            //NLE
            log.log(Level.INFO, "data_string {0}", data_string);
            System.out.println("data_string="+data_string);
            
            succes = this.getPocDao().setRemisionIMSS(data_string, app_selected);
            
            log.log(Level.INFO, "despues de validacion {0}", String.valueOf(succes.get("success")));
            System.out.println("Validación="+succes.get("success"));
            
            if( String.valueOf(succes.get("success")).equals("true")  ){
                //actualizo = this.getPocDao().selectFunctionForThisApp(data_string, extra_data_array);
            }else{
                //NLE
                log.log(Level.INFO, "Validación no exitosa {0}", String.valueOf(succes.get("success")));
                System.out.println("Validación no exitosa="+succes.get("success"));
            }
            jsonretorno.put("success",String.valueOf(succes.get("success")));
            log.log(Level.INFO, "Salida json {0}", String.valueOf(jsonretorno.get("success")));
        }catch(Throwable e){
            System.out.println("Error:"+e);
            jsonretorno.put("success",String.valueOf(succes.get("success")));
        }
        return jsonretorno;
    }

    
    //NLE: Método para obtener los Estatus de Remisiones IMSS
    @RequestMapping(method = RequestMethod.POST, value="/getFormRemisionIMSS.json")
    public @ResponseBody HashMap<String, String> getFormRemisionIMSSJson(
            @RequestParam(value="id", required=true) Integer identificador,
            Model model
        ) {
        
        System.out.println("Entrando a getFormRemisionIMSS.json");
        HashMap<String, String> jsonretorno = new HashMap<String, String>();
        
        jsonretorno= this.getPocDao().getFormRemisionIMSS(identificador);
        
        return jsonretorno;
    }
    
    
    //edicion y nuevo
    @RequestMapping(method = RequestMethod.POST, value="/edit2.json")
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
            Model model
        ) throws Exception {
            log.log(Level.INFO, "edit.json {0}", "Ok!");
            System.out.println("Entró al Controller de Remisiones IMSS...");
            HashMap<String, String> jsonretorno = new HashMap<String, String>();
            HashMap<String, String> succes = new HashMap<String, String>();
            String arreglo[];
            arreglo = new String[eliminado.length];
            Integer id_usuario= user.getUserId();//variable para el id  del usuario
            Integer app_selected = 12;
            String command_selected = "new";
            String actualizo = "0";
            
            command_selected=select_accion;
            
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
                
                //Imprimir el contenido de cada celda 
                arreglo[i]= "'"+eliminado[i] +"___" + iddetalle[i] +"___" + idproducto[i] +"___" + id_presentacion[i] +"___" + cantidad[i] +"___" + StringHelper.removerComas(precio[i]) +"___" + monedagrid[i]+"___"+notr[i]+"___"+id_imp_prod[i]+"___"+valor_imp[i]+"___"+select_umedida[i]+"___"+stat_reg+"___"+precio_autorizado+"___"+id_user_autoriza+"___"+reqauth[i]+"___"+salvar_registro[i]+"'";
                System.out.println("arreglo["+i+"] = "+arreglo[i]);
            }
            
            //Serializar el arreglo
            String extra_data_array = StringUtils.join(arreglo, ",");
            
            //System.out.println("select_incoterms: "+select_incoterms);
            String incoterms="";
            int primerIncoterm = 0;
            if(select_incoterms != null){
                for(int i=0; i<select_incoterms.length; i++) { 
                    if(primerIncoterm==0){
                        incoterms = select_incoterms[i];
                        primerIncoterm++;
                    }else{
                        incoterms += ","+select_incoterms[i];
                        primerIncoterm++;
                    }
                }
            }
            
            check_descripcion_larga = StringHelper.verificarCheckBox(check_descripcion_larga);
            check_incluye_iva = StringHelper.verificarCheckBox(check_incluye_iva);
            
            String data_string = app_selected + "___"+ command_selected + "___"+ id_usuario + "___"+ identificador + "___"+ select_tipo_cotizacion + "___"+ id_cliente + "___"+ check_descripcion_larga + "___"+ observaciones.toUpperCase() + "___"+ tc+"___"+moneda_id+"___"+fecha+"___"+select_agente+"___"+vigencia+"___"+check_incluye_iva+"___"+incoterms+"___"+tc_usd;
            //NLE
            log.log(Level.INFO, "data_string {0}", data_string);
            System.out.println("data_string="+data_string);
            
            succes = this.getPocDao().selectFunctionValidateAaplicativo(data_string, app_selected, extra_data_array);
            
            log.log(Level.INFO, "despues de validacion {0}", String.valueOf(succes.get("success")));
            System.out.println("Validación="+succes.get("success"));
            
            if( String.valueOf(succes.get("success")).equals("true")  ){
                //actualizo = this.getPocDao().selectFunctionForThisApp(data_string, extra_data_array);
            }else{
                //NLE
                log.log(Level.INFO, "Validación no exitosa {0}", String.valueOf(succes.get("success")));
                System.out.println("Validación no exitosa="+succes.get("success"));
            }
            jsonretorno.put("success",String.valueOf(succes.get("success")));
            
            log.log(Level.INFO, "Salida json {0}", String.valueOf(jsonretorno.get("success")));
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
        
        Integer app_selected = 210;
        String command_selected = "delete";
        String extra_data_array = "'sin datos'";
        String data_string = app_selected+"___"+command_selected+"___"+id_usuario+"___"+id;
        System.out.println("data_string="+data_string);
        System.out.println("Ejecutando borrado logico del Contra Recibo IMSS id="+id);
        jsonretorno.put("success",String.valueOf( this.getPocDao().logicDeleteRemisionIMSS(data_string,app_selected)) );
        
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
        
        //obtener el directorio temporal
        String dir_tmp = this.getGralDao().getTmpDir();
        
        //directorio de imagenes de productos
        String dirImgProd = this.getGralDao().getProdImgDir()+rfc_empresa+"/";
        
        //ruta del la imagen del Logotipo
        String rutaLogoEmpresa = this.getGralDao().getImagesDir()+rfc_empresa+"_logo.png";
        
        
        String file_name = "COT_"+rfc_empresa+".pdf";
        
        //ruta de archivo de salida
        String fileout = dir_tmp + file_name;
        
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
        datos.put("file_out", fileout);
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
                
        pdfCotizacion pdf = new pdfCotizacion(HeaderFooter, datosEmisor, datos,datosReceptor,lista_productos, condiciones_comerciales, politicas_pago, incoterms);
        pdf.ViewPDF();
        
        
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
    
    
    
}
