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
import com.maxima.sales.cli.grpc.PedidoAuthRequest;
import com.maxima.sales.cli.grpc.PedidoAuthResponse;
import com.maxima.sales.cli.grpc.SalesGrpc;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.StatusRuntimeException;


@Controller
@SessionAttributes({"user"})
@RequestMapping("/pocpedidosautoriza/")
public class PocPedidosAutorizaController {
    ResourceProject resource = new ResourceProject();
    private static final Logger log  = Logger.getLogger(PocPedidosAutorizaController.class.getName());
    
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
        
        log.log(Level.INFO, "Ejecutando starUp de {0}", PocPedidosAutorizaController.class.getName());
        LinkedHashMap<String,String> infoConstruccionTabla = new LinkedHashMap<String,String>();
        infoConstruccionTabla.put("id", "Acciones:70");
        infoConstruccionTabla.put("folio", "Folio:70");
        infoConstruccionTabla.put("cliente", "Cliente:320");
        infoConstruccionTabla.put("total", "Monto:100");
        infoConstruccionTabla.put("denominacion", "Moneda:70");
        infoConstruccionTabla.put("estado", "Estado:100");
        infoConstruccionTabla.put("fecha_creacion","Fecha creaci&oacute;n:110");
        infoConstruccionTabla.put("suc","Sucursal:150");
        
        ModelAndView x = new ModelAndView("pocpedidosautoriza/startup", "title", "Autorizaci&oacute;n de Pedidos de Clientes");
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
        
        //aplicativo Autorizacion de Pedidos de Clientes
        Integer app_selected = 65;
        
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
        
        //decodificar id de usuario
        Integer id_usuario = Integer.parseInt(Base64Coder.decodeString(id_user_cod));
        userDat = this.getHomeDao().getUserById(id_usuario);
        Integer id_empresa = Integer.parseInt(userDat.get("empresa_id"));
        Integer id_sucursal = Integer.parseInt(userDat.get("sucursal_id"));
        boolean obtener_todos_los_agentes=true;
        
        agentes = this.getPocDao().getAgentes(id_empresa, id_sucursal, obtener_todos_los_agentes);
        
        jsonretorno.put("Agentes", agentes);
        return jsonretorno;
    }
    
    
    @RequestMapping(method = RequestMethod.POST, value="/getPedido.json")
    public @ResponseBody HashMap<String,ArrayList<HashMap<String, String>>> getPedidoJson(
            @RequestParam(value="id_pedido", required=true) String id_pedido,
            @RequestParam(value="iu", required=true) String id_user,
            Model model
            ) {
        
        log.log(Level.INFO, "Ejecutando getPedidoJson de {0}", PocPedidosAutorizaController.class.getName());
        HashMap<String,ArrayList<HashMap<String, String>>> jsonretorno = new HashMap<String,ArrayList<HashMap<String, String>>>();
        ArrayList<HashMap<String, String>> datosPedido = new ArrayList<HashMap<String, String>>();
        ArrayList<HashMap<String, String>> datosGrid = new ArrayList<HashMap<String, String>>();
        ArrayList<HashMap<String, String>> valorIva = new ArrayList<HashMap<String, String>>();
        ArrayList<HashMap<String, String>> tipoCambioActual = new ArrayList<HashMap<String, String>>();
        HashMap<String, String> tc = new HashMap<String, String>();
        HashMap<String, String> userDat = new HashMap<String, String>();
        boolean obtener_todos_los_agentes=true;
        
        //decodificar id de usuario
        Integer id_usuario = Integer.parseInt(Base64Coder.decodeString(id_user));
        userDat = this.getHomeDao().getUserById(id_usuario);
        
        Integer id_empresa = Integer.parseInt(userDat.get("empresa_id"));
        Integer id_sucursal = Integer.parseInt(userDat.get("sucursal_id"));
        
        if( (id_pedido.equals("0"))==false  ){
            datosPedido = this.getPocDao().getPocPedido_Datos(Integer.parseInt(id_pedido));
            datosGrid = this.getPocDao().getPocPedido_DatosGrid(Integer.parseInt(id_pedido));
        }
        
        valorIva= this.getPocDao().getValoriva(id_sucursal);
        tc.put("tipo_cambio", StringHelper.roundDouble(this.getPocDao().getTipoCambioActual(), 4)) ;
        tipoCambioActual.add(0,tc);
        
        jsonretorno.put("datosPedido", datosPedido);
        jsonretorno.put("datosGrid", datosGrid);
        jsonretorno.put("iva", valorIva);
        jsonretorno.put("Monedas", this.getPocDao().getMonedas());
        jsonretorno.put("Tc", tipoCambioActual);
        jsonretorno.put("Vendedores", this.getPocDao().getAgentes(id_empresa, id_sucursal, obtener_todos_los_agentes));
        jsonretorno.put("Condiciones", this.getPocDao().getCondicionesDePago());
        jsonretorno.put("MetodosPago", this.getPocDao().getMetodosPago(id_empresa));
        
        return jsonretorno;
    }
    
    
    private void autorizarPedido(int pedidoId, int usuarioId, HashMap<String,String> jsonretorno) {

        ManagedChannel channel = ManagedChannelBuilder.forTarget(Helper.getGrpcConnString())
            .usePlaintext()
            .build();

        SalesGrpc.SalesBlockingStub blockingStub = SalesGrpc.newBlockingStub(channel);

        PedidoAuthRequest pedidoAuthRequest =
            PedidoAuthRequest.newBuilder()
                .setPedidoId(pedidoId)
                .setUsuarioId(usuarioId)
                .build();
        PedidoAuthResponse pedidoAuthResponse;

        try {
            pedidoAuthResponse = blockingStub.authPedido(pedidoAuthRequest);
            String valorRetorno = pedidoAuthResponse.getValorRetorno();

            if (valorRetorno.equals("1")) {
                jsonretorno.put("success", "true");
                jsonretorno.put("actualizo", valorRetorno);

            } else {
                jsonretorno.put("success", valorRetorno);
            }
            log.log(Level.INFO, "Pedido Auth Response valorRetorno: {0}", valorRetorno);

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
    
    //edicion y nuevo
    @RequestMapping(method = RequestMethod.POST, value="/edit.json")
    public @ResponseBody HashMap<String, String> editJson (
        @RequestParam(value="id_pedido", required=true)      Integer id_pedido,
        @RequestParam(value="accion_proceso", required=true) String accion_proceso,
        @ModelAttribute("user")                              UserSessionData user)
    {

        System.out.println(accion_proceso.toUpperCase() + " el Pedido (desde PocPedidosAutorizaController)");

        Integer app_selected = 65;
        String command_selected = "new";
        Integer id_usuario = user.getUserId();

        //serializar el arreglo
        String extra_data_array = extra_data_array = "'sin datos'";

        if (accion_proceso.equals("autorizar") && id_pedido != 0) {
            command_selected = accion_proceso;
        }

        if (accion_proceso.equals("cancelar") && id_pedido != 0) {
            command_selected = accion_proceso;
        }

        String data_string =
            app_selected     + "___" +
            command_selected + "___" +
            id_usuario       + "___" +
            id_pedido;
        //System.out.println("data_string: "+data_string);

        HashMap<String, String> jsonretorno = new HashMap<String, String>();
        HashMap<String, String> success = this.getPocDao()
            .selectFunctionValidateAaplicativo(data_string, app_selected, extra_data_array);

        log.log(Level.INFO, "Resultado de validacion Pedido {0}", accion_proceso + ": " + success.get("success"));

        if (success.get("success").equals("true")) {
            
            if (accion_proceso.equals("autorizar")) {
                autorizarPedido(id_pedido.intValue(), id_usuario.intValue(), jsonretorno);
                
            } else if (accion_proceso.equals("cancelar")) {
                String actualizo = this.getPocDao().selectFunctionForThisApp(data_string, extra_data_array);
                jsonretorno.put("actualizo", actualizo);
                jsonretorno.put("success", "true");
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
        Integer app_selected = 64; //Aqui se deja el aplizativo 64 de Pedidos de clientes, en autorizacion de pedidos se utiliza el mismo pdf del reporte
        
        
        System.out.println("Generando PDF de Pedido");
        
        //decodificar id de usuario
        Integer id_usuario = Integer.parseInt(Base64Coder.decodeString(id_user_cod));
        System.out.println("id_usuario: "+id_usuario);
        
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
        System.out.println("Directorio temporal: "+file_dir_tmp.getCanonicalPath());
        
        //aqui se obtienen los parametros de la facturacion, nos intersa el tipo de formato para el pdf del pedido
        parametros = this.getPocDao().getPocPedido_Parametros(id_empresa, id_sucursal);
        
        datospedido_pdf = this.getPocDao().getDatosPDF(id_pedido);
        conceptos_pedido = this.getPocDao().getPocPedido_DatosGrid(id_pedido);
        
        String municipio = this.getGralDao().getMunicipioSucursalEmisora(id_sucursal);
        String Estado = this.getGralDao().getEstadoSucursalEmisora(id_sucursal);
        
        String calle = this.getGralDao().getCalleDomicilioFiscalEmpresaEmisora(id_empresa);
        String numero= this.getGralDao().getNoExteriorDomicilioFiscalEmpresaEmisora(id_empresa);
        String colonia= this.getGralDao().getColoniaDomicilioFiscalEmpresaEmisora(id_empresa);
        String pais= this.getGralDao().getPaisSucursalEmisora(id_sucursal);
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
