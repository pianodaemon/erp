package com.agnux.kemikal.controllers;

import com.agnux.cfd.v2.ArchivoInformeMensual;
import com.agnux.cfd.v2.Base64Coder;
import com.agnux.cfd.v2.BeanFromCfdXml;
import com.agnux.cfd.v2.CryptoEngine;
import com.agnux.cfdi.BeanCancelaCfdi;
import com.agnux.cfdi.BeanFromCfdiXml;
import com.agnux.cfdi.adendas.AdendaCliente;
import com.agnux.common.helpers.*;
import com.agnux.common.obj.DataPost;
import com.agnux.common.obj.ResourceProject;
import com.agnux.common.obj.UserSessionData;
import com.agnux.kemikal.interfacedaos.FacturasInterfaceDao;
import com.agnux.kemikal.interfacedaos.GralInterfaceDao;
import com.agnux.kemikal.interfacedaos.HomeInterfaceDao;
import com.agnux.kemikal.reportes.pdfCfd_CfdiTimbrado;
import com.agnux.kemikal.reportes.pdfCfd_CfdiTimbradoFormato2;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Image;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfStamper;
import java.io.*;
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
@RequestMapping("/facconsultas/")
public class FacConsultasController {
    ResourceProject resource = new ResourceProject();
    private static final Logger log  = Logger.getLogger(FacConsultasController.class.getName());
    
    
    @Autowired
    @Qualifier("daoGral")
    private GralInterfaceDao gralDao;
    
    
    @Autowired
    @Qualifier("daoFacturas")
    private FacturasInterfaceDao facdao;
    
    @Autowired
    @Qualifier("daoHome")
    private HomeInterfaceDao HomeDao;
    
    
    @Autowired
    @Qualifier("beanCancelaCfdi")
    BeanCancelaCfdi bcancelafdi;
    
    public BeanCancelaCfdi getBcancelafdi() {
        return bcancelafdi;
    }
    
    public HomeInterfaceDao getHomeDao() {
        return HomeDao;
    }
    
    public GralInterfaceDao getGralDao() {
        return gralDao;
    }
    
    public FacturasInterfaceDao getFacdao() {
        return facdao;
    }
    
    @RequestMapping(value="/startup.agnux")
    public ModelAndView startUp(HttpServletRequest request, HttpServletResponse response, 
            @ModelAttribute("user") UserSessionData user
            )throws ServletException, IOException {
        
        log.log(Level.INFO, "Ejecutando starUp de {0}", FacConsultasController.class.getName());
        LinkedHashMap<String,String> infoConstruccionTabla = new LinkedHashMap<String,String>();
        
        infoConstruccionTabla.put("id", "Acciones:70");
        infoConstruccionTabla.put("serie_folio", "Factura:80");
        infoConstruccionTabla.put("cliente", "Cliente:300");
        infoConstruccionTabla.put("total", "Monto:90");
        infoConstruccionTabla.put("moneda", "Moneda:60");
        infoConstruccionTabla.put("fecha_facturacion","Fecha&nbsp;Exp.:80");
        infoConstruccionTabla.put("fecha_venc","Fecha&nbsp;Ven.:80");
        infoConstruccionTabla.put("folio_pedido","Pedido:80");
        infoConstruccionTabla.put("oc","O.C.:80");
        infoConstruccionTabla.put("estado", "Estado:80");
        infoConstruccionTabla.put("fecha_pago","Fecha&nbsp;Pago:80");
        
        ModelAndView x = new ModelAndView("facconsultas/startup", "title", "Consulta de Facturas");
        
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
    
    
    
    
    @RequestMapping(value="/getAllFacturas.json", method = RequestMethod.POST)
    public @ResponseBody HashMap<String,ArrayList<HashMap<String, Object>>> getAllFacturasacturasJson(
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
        
        //aplicativo Consulta de Facturas
        Integer app_selected = 142;
        
        //decodificar id de usuario
        Integer id_usuario = Integer.parseInt(Base64Coder.decodeString(id_user_cod));
        
        //variables para el buscador
        String factura = "%"+StringHelper.isNullString(String.valueOf(has_busqueda.get("factura")))+"%";
        String cliente = "%"+StringHelper.isNullString(String.valueOf(has_busqueda.get("cliente")))+"%";
        String fecha_inicial = ""+StringHelper.isNullString(String.valueOf(has_busqueda.get("fecha_inicial")))+"";
        String fecha_final = ""+StringHelper.isNullString(String.valueOf(has_busqueda.get("fecha_final")))+"";
        String codigo = "%"+StringHelper.isNullString(String.valueOf(has_busqueda.get("codigo")))+"%";
        String producto = "%"+StringHelper.isNullString(String.valueOf(has_busqueda.get("producto")))+"%";
        String agente = ""+StringHelper.isNullString(String.valueOf(has_busqueda.get("agente")))+"";
        String folio_pedido = "%"+StringHelper.isNullString(String.valueOf(has_busqueda.get("folio_pedido")))+"%";
        
        String data_string = app_selected+"___"+id_usuario+"___"+factura+"___"+cliente+"___"+fecha_inicial+"___"+fecha_final+"___"+codigo+"___"+producto+"___"+agente+"___"+folio_pedido;
        
        //obtiene total de registros en base de datos, con los parametros de busqueda
        int total_items = this.getFacdao().countAll(data_string);
        
        //calcula el total de paginas
        int total_pags = resource.calculaTotalPag(total_items,items_por_pag);
        
        //variables que necesita el datagrid, para no tener que hacer uno por cada aplicativo
        DataPost dataforpos = new DataPost(orderby, desc, items_por_pag, pag_start, display_pag, input_json, cadena_busqueda,total_items,total_pags, id_user_cod);
        
        int offset = resource.__get_inicio_offset(items_por_pag, pag_start);
        
        //obtiene los registros para el grid, de acuerdo a los parametros de busqueda
        jsonretorno.put("Data", this.getFacdao().getFacturas_PaginaGrid(data_string, offset, items_por_pag, orderby, desc));
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
        
        agentes = this.getFacdao().getFactura_Agentes(id_empresa, id_sucursal);
        
        jsonretorno.put("Agentes", agentes);
        return jsonretorno;
    }
    
    
    @RequestMapping(method = RequestMethod.POST, value="/getFactura.json")
    public @ResponseBody HashMap<String, Object> getFacturaJson(
            @RequestParam(value="id_factura", required=true) String id_factura,
            @RequestParam(value="iu", required=true) String id_user,
            Model model
        ) {
        
        log.log(Level.INFO, "Ejecutando getFacturaJson de {0}", FacConsultasController.class.getName());
        HashMap<String, Object> jsonretorno = new HashMap<String, Object>();
        ArrayList<HashMap<String, Object>> datosFactura = new ArrayList<HashMap<String, Object>>();
        ArrayList<HashMap<String, Object>> datosGrid = new ArrayList<HashMap<String, Object>>();
        ArrayList<HashMap<String, Object>> valorIva = new ArrayList<HashMap<String, Object>>();
        ArrayList<HashMap<String, Object>> tipoCambioActual = new ArrayList<HashMap<String, Object>>();
        HashMap<String, Object> tc = new HashMap<String, Object>();
        HashMap<String, String> userDat = new HashMap<String, String>();
        HashMap<String, String> parametros = new HashMap<String, String>();
        ArrayList<HashMap<String, Object>> datosAdenda = new ArrayList<HashMap<String, Object>>();
        
        //Decodificar id de usuario
        Integer id_usuario = Integer.parseInt(Base64Coder.decodeString(id_user));
        userDat = this.getHomeDao().getUserById(id_usuario);
        
        Integer id_empresa = Integer.parseInt(userDat.get("empresa_id"));
        Integer id_sucursal = Integer.parseInt(userDat.get("sucursal_id"));
        boolean incluirAdenda=false;
        
        parametros = this.getFacdao().getFac_Parametros(id_empresa, id_sucursal);
        
        if(!id_factura.equals("0")){
            datosFactura = this.getFacdao().getFactura_Datos(Integer.parseInt(id_factura));
            datosGrid = this.getFacdao().getFactura_DatosGrid(Integer.parseInt(id_factura));
            
            //Verificar si hay que incluir adenda
            if (parametros.get("incluye_adenda").equals("true")){
                //Verificar si el cliente tiene asignada una adenda
                if(Integer.parseInt(String.valueOf(datosFactura.get(0).get("t_adenda_id")))>0){
                    
                    //Obtener datos de la Adenda
                    datosAdenda = this.getFacdao().getFactura_DatosAdenda(Integer.valueOf(id_factura));
                    
                    incluirAdenda=true;
                }
            }
        }
        
        if(incluirAdenda){
            if(datosAdenda.size()<=0){
                /*
                Si es nuevo, aun no se ha ingresado datos para la addenda.
                Hay que cargar los datos conocidos que debe llevar la addenda
                */
                HashMap<String, Object> row = new HashMap<String, Object>();
                row.put("id_adenda",0);
                row.put("generado","false");
                
                if(Integer.parseInt(String.valueOf(datosFactura.get(0).get("t_adenda_id")))==3){
                    row.put("valor1",datosFactura.get(0).get("orden_compra"));//Orden Compra
                    row.put("valor2",this.getGralDao().geteMailPurchasingEmpresaEmisora(id_empresa));//Email-Emisor
                    row.put("valor3",datosFactura.get(0).get("moneda_4217"));//Moneda
                    row.put("valor4",(datosFactura.get(0).get("moneda_4217").equals("MXN"))?"1":datosFactura.get(0).get("tipo_cambio"));//Tipo de Cambio
                    row.put("valor5",datosFactura.get(0).get("subtotal"));//Subtotal factura
                    row.put("valor6",datosFactura.get(0).get("total"));//Total Factura
                    row.put("valor7","");
                    row.put("valor8","");
                }
                
                datosAdenda.add(row);
            }
        }
        
        valorIva= this.getFacdao().getValoriva(id_sucursal);
        tc.put("tipo_cambio", StringHelper.roundDouble(this.getFacdao().getTipoCambioActual(), 4)) ;
        tipoCambioActual.add(0,tc);
        
        jsonretorno.put("datosFactura", datosFactura);
        jsonretorno.put("datosGrid", datosGrid);
        jsonretorno.put("datosAdenda", datosAdenda);
        jsonretorno.put("Addenda", incluirAdenda);
        jsonretorno.put("iva", valorIva);
        jsonretorno.put("Tc", tipoCambioActual);
        jsonretorno.put("Monedas", this.getFacdao().getFactura_Monedas());
        jsonretorno.put("Vendedores", this.getFacdao().getFactura_Agentes(id_empresa, id_sucursal));
        jsonretorno.put("Condiciones", this.getFacdao().getFactura_DiasDeCredito());
        jsonretorno.put("MetodosPago", this.getFacdao().getMetodosPago(id_empresa));
        
        return jsonretorno;
    }
    
    
    
    
    
    
    //obtiene datos para generador  de informe
    @RequestMapping(method = RequestMethod.POST, value="/datos_generador_informe.json")
    public @ResponseBody HashMap<String,ArrayList<HashMap<String, Integer>>> get_datos_generador_informeJson(Model model) {
        HashMap<String,ArrayList<HashMap<String, Integer>>> jsonretorno = new HashMap<String,ArrayList<HashMap<String, Integer>>>();
        
        jsonretorno.put("anioinforme", this.getFacdao().getFactura_AnioInforme());
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
    
    
    
    
    
    //Enviar archivos por E-Mail
    @RequestMapping(method = RequestMethod.POST, value="/getSendMail.json")
    public @ResponseBody HashMap<String,String> getSendMailJson(
            @RequestParam(value="id", required=true) Integer id_fac,
            @RequestParam(value="correo", required=true) String correo,
            @RequestParam(value="asunto", required=true) String asunto,
            @RequestParam(value="msj", required=true) String msj,
            @RequestParam(value="xml", required=true) String xml,
            @RequestParam(value="pdf", required=true) String pdf,
            @RequestParam(value="iu", required=true) String id_user_cod,
            Model model
        ) {
        
        log.log(Level.INFO, "Ejecutando getSendMailJson de {0}", FacConsultasController.class.getName());
        HashMap<String, String> jsonretorno = new HashMap<String,String>();
        HashMap<String, String> userDat = new HashMap<String, String>();
        ArrayList<HashMap<String, String>> email_envio = new ArrayList<HashMap<String, String>>();
        ArrayList<HashMap<String, String>> email_cco = new ArrayList<HashMap<String, String>>();
        HashMap<String, String> conecta = new HashMap<String, String>();
        LinkedHashMap<String,String> destinatario;
        LinkedHashMap<String,String> adjunto;
        ArrayList<LinkedHashMap<String,String>> listaAdjuntos = new ArrayList<LinkedHashMap<String,String>>();
        ArrayList<LinkedHashMap<String,String>> ListaDestinatarios = new ArrayList<LinkedHashMap<String,String>>();
        
        String valor ="false";
        String respuesta ="";
        String existen="";
        String dirSalidas = "";
        String nombre_archivo="";
        boolean emailDestinoCorrecto=false;
        boolean emailOrigenCorrecto=false;
        
        //decodificar id de usuario
        Integer id_usuario = Integer.parseInt(Base64Coder.decodeString(id_user_cod));
        userDat = this.getHomeDao().getUserById(id_usuario);
        Integer id_empresa = Integer.parseInt(userDat.get("empresa_id"));
        Integer id_sucursal = Integer.parseInt(userDat.get("sucursal_id"));
        
        email_envio=this.getFacdao().getEmailEnvio(id_empresa, id_sucursal);
        email_cco=this.getFacdao().getEmailCopiaOculta(id_empresa, id_sucursal);
        
        
        
        
        //Obtener tipo de facturacion
        String tipo_facturacion = this.getFacdao().getTipoFacturacion(id_empresa);
        
        if(tipo_facturacion.equals("cfd")){
            dirSalidas = this.getGralDao().getCfdEmitidosDir() + this.getGralDao().getRfcEmpresaEmisora(id_empresa);
        }
        
        if(tipo_facturacion.equals("cfdi")){
            dirSalidas = this.getGralDao().getCfdiSolicitudesDir() + "out/";
        }
        
        if(tipo_facturacion.equals("cfditf")){
            dirSalidas = this.getGralDao().getCfdiTimbreEmitidosDir() + this.getGralDao().getRfcEmpresaEmisora(id_empresa);
        }
        
        nombre_archivo = this.getFacdao().getRefIdFactura(id_fac, id_empresa);
        
        String fileout = dirSalidas +"/"+ nombre_archivo;
        
        //Tomar solo serie y folio para nombre del archivo
        if(nombre_archivo.contains("_")){
            nombre_archivo = nombre_archivo.split("_")[1];
        }
        
        
        //Verificar si existe el XML
        if(xml.equals("true")){
            System.out.println("Ruta: " + fileout + ".xml");
            File file = new File(fileout + ".xml");
            if (!file.exists()){
                existen="NO existe el archivo: "+nombre_archivo+".xml <br>";
            }
        }
        
        //Verificar si existe el PDF
        if(pdf.equals("true")){
            System.out.println("Ruta: " + fileout + ".pdf");
            File file2 = new File(fileout + ".pdf");
            if (!file2.exists()){
                existen="NO existe el archivo: "+nombre_archivo+".pdf";
            }
        }
        
        //Validar correo del destinatario
        emailDestinoCorrecto = StringHelper.validateEmail(correo);
        
        if(emailDestinoCorrecto){
            if(existen.equals("")){
                if(email_envio.size()>0){
                    if(!email_envio.get(0).get("email").equals("")){
                        //Validar correo de envio
                        emailOrigenCorrecto = StringHelper.validateEmail(email_envio.get(0).get("email"));
                        
                        if(emailOrigenCorrecto){
                            if(!email_envio.get(0).get("passwd").equals("")){
                                if(!email_envio.get(0).get("port").equals("")){
                                    if(!email_envio.get(0).get("port").equals("")){
                                        //Datos de conexion para el envio
                                        conecta.put("hostname", email_envio.get(0).get("host"));
                                        conecta.put("username", email_envio.get(0).get("email"));
                                        conecta.put("password", email_envio.get(0).get("passwd"));
                                        
                                        //Crear lista de Destinatarios
                                        destinatario = new LinkedHashMap<String,String>();
                                        destinatario.put("type", "TO");
                                        destinatario.put("recipient", correo);
                                        ListaDestinatarios.add(destinatario);

                                        if(email_cco.size()>0){
                                            if(!email_cco.get(0).get("email").equals("")){
                                                destinatario = new LinkedHashMap<String,String>();
                                                destinatario.put("type", "BCC");
                                                destinatario.put("recipient", email_cco.get(0).get("email"));
                                                ListaDestinatarios.add(destinatario);
                                            }
                                        }

                                        //Crear lista de Archivos Adjuntos
                                        if(xml.equals("true")){
                                            //Adjuntar xml
                                            adjunto = new LinkedHashMap<String,String>();
                                            adjunto.put("path_file", fileout + ".xml");
                                            adjunto.put("file_name", nombre_archivo + ".xml");
                                            listaAdjuntos.add(adjunto);
                                        }
                                        if(pdf.equals("true")){
                                            //Adjuntar pdf
                                            adjunto = new LinkedHashMap<String,String>();
                                            adjunto.put("path_file", fileout + ".pdf");
                                            adjunto.put("file_name", nombre_archivo + ".pdf");
                                            listaAdjuntos.add(adjunto);
                                        }
                                        
                                        SendEmailWithFileHelper send = new SendEmailWithFileHelper(conecta);
                                        send.setPuerto(email_envio.get(0).get("port"));
                                        send.setMensaje(msj);
                                        send.setAdjuntos(listaAdjuntos);
                                        send.setDestinatarios(ListaDestinatarios);
                                        send.setAsunto(asunto);
                                        respuesta = send.enviarEmail();
                                        
                                        valor="true";
                                        //respuesta="Correo enviado!";
                                        
                                        /*
                                        MandaEmail senderHilo = new MandaEmail(conecta, ListaDestinatarios, listaAdjuntos, email_envio.get(0).get("port"), asunto, msj);
                                        senderHilo.start();
                                        
                                        valor="true";
                                        respuesta="Correo enviado!";
                                        */
                                    }else{
                                        valor="false";
                                        respuesta="No se ha definido el Servidor SMTP para el env&iacute;o.";
                                    }
                                }else{
                                    valor="false";
                                    respuesta="No se ha definido el Puerto para el env&iacute;o.";
                                }
                            }else{
                                valor="false";
                                respuesta="Falta la contrase&ntilde;a del correo de env&iacute;o.";
                            }
                        }else{
                            valor="false";
                            respuesta="El correo de env&iacute;o es invalido: "+email_envio.get(0).get("email");
                        }
                    }else{
                        valor="false";
                        respuesta="No se ha definido un correo de env&iacute;o.";
                    }
                }else{
                    valor="false";
                    respuesta="No existe una configuraci&oacute;n para el env&iacute;o de correos.";
                }
            }else{
                valor="false";
                respuesta=existen;
            }
        }else{
            valor="false";
            respuesta="Email de destinatario es incorrecto: "+ correo;
        }
        
        
        jsonretorno.put("valor", valor);
        jsonretorno.put("msj", respuesta);
        
        return jsonretorno;
    }
    
    

    
    
    
    
    //obtiene los tipos de cancelacion
    @RequestMapping(method = RequestMethod.POST, value="/getVerificaArchivoGenerado.json")
    public @ResponseBody HashMap<String,String> getVerificaArchivoGeneradoJson(
            @RequestParam(value="serie_folio", required=true) String serie_folio,
            @RequestParam(value="ext", required=true) String extension,
            @RequestParam(value="id", required=true) Integer id_fac,
            @RequestParam(value="iu", required=true) String id_user_cod,
            Model model
            ) {
        
        log.log(Level.INFO, "Ejecutando getVerificaArchivoGeneradoJson de {0}", FacConsultasController.class.getName());
        HashMap<String, String> jsonretorno = new HashMap<String,String>();
        HashMap<String, String> userDat = new HashMap<String, String>();
        String existe ="false";
        String dirSalidas = "";
        String nombre_archivo="";
        
        //decodificar id de usuario
        Integer id_usuario = Integer.parseInt(Base64Coder.decodeString(id_user_cod));
        userDat = this.getHomeDao().getUserById(id_usuario);
        Integer id_empresa = Integer.parseInt(userDat.get("empresa_id"));
        
        //obtener tipo de facturacion
        String tipo_facturacion = this.getFacdao().getTipoFacturacion(id_empresa);
        
        if(tipo_facturacion.equals("cfd")){
            dirSalidas = this.getGralDao().getCfdEmitidosDir() + this.getGralDao().getRfcEmpresaEmisora(id_empresa);
        }
        
        if(tipo_facturacion.equals("cfdi")){
            dirSalidas = this.getGralDao().getCfdiSolicitudesDir() + "out/";
        }
        
        if(tipo_facturacion.equals("cfditf")){
            dirSalidas = this.getGralDao().getCfdiTimbreEmitidosDir() + this.getGralDao().getRfcEmpresaEmisora(id_empresa);
        }
        
        nombre_archivo = this.getFacdao().getRefIdFactura(id_fac, id_empresa);
        
        //String generado = this.getFacdao().verifica_fac_docs_salidas( id_factura );
        //String dirSalidasBuzonFiscal = this.getGralDao().getCfdiSolicitudesDir() + "out";
        
        String fileout = dirSalidas +"/"+ nombre_archivo +"."+extension;
        
        System.out.println("Ruta: " + fileout);
        File file = new File(fileout);
        if (file.exists()){
            existe="true";
        }
        
        jsonretorno.put("descargar", existe);
        
        return jsonretorno;
    }
    
    
    
    
    
    
    
    //Descarga pdf de factura generado anteriormente
    @RequestMapping(value = "/get_descargar_pdf_factura/{id_factura}/{iu}/out.json", method = RequestMethod.GET ) 
    public ModelAndView getDescargaPdfFacturaJson(
            @PathVariable("id_factura") Integer id_factura, 
            @PathVariable("iu") String id_user,
            HttpServletRequest request, HttpServletResponse response, Model model)
            throws ServletException, IOException, URISyntaxException, DocumentException {
        
        HashMap<String, String> userDat = new HashMap<String, String>();
        String dirSalidas = "";
        String nombre_archivo = "";
        
        //decodificar id de usuario
        Integer id_usuario = Integer.parseInt(Base64Coder.decodeString(id_user));
        
        userDat = this.getHomeDao().getUserById(id_usuario);
        Integer id_empresa = Integer.parseInt(userDat.get("empresa_id"));
        
        
        //obtener tipo de facturacion
        String tipo_facturacion = this.getFacdao().getTipoFacturacion(id_empresa);
        
        if(tipo_facturacion.equals("cfd")){
            dirSalidas = this.getGralDao().getCfdEmitidosDir() + this.getGralDao().getRfcEmpresaEmisora(id_empresa);
        }
        
        if(tipo_facturacion.equals("cfdi")){
            dirSalidas = this.getGralDao().getCfdiSolicitudesDir() + "out/";
        }
        
        if(tipo_facturacion.equals("cfditf")){
            dirSalidas = this.getGralDao().getCfdiTimbreEmitidosDir() + this.getGralDao().getRfcEmpresaEmisora(id_empresa);
        }
        
        //nombre_archivo = this.getFacdao().getSerieFolioFactura(id_factura, id_empresa);
        nombre_archivo = this.getFacdao().getRefIdFactura(id_factura, id_empresa);
        
        
        String fileout = dirSalidas + "/" + nombre_archivo +".pdf";

        
        //System.out.println("Recuperando archivo: " + fileout);
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
    
    
    
    
    
    
    
    
    //Descarga xml de factura
    @RequestMapping(value = "/get_descargar_xml_factura/{id_factura}/{iu}/out.json", method = RequestMethod.GET ) 
    public ModelAndView getDescargaXmlFacturaJson(
            @PathVariable("id_factura") Integer id_factura, 
            @PathVariable("iu") String id_user,
            HttpServletRequest request, 
            HttpServletResponse response, 
            Model model) throws ServletException, IOException, URISyntaxException {
        
        HashMap<String, String> userDat = new HashMap<String, String>();
        String dirSalidas = "";
        String nombre_archivo = "";
        
        //decodificar id de usuario
        Integer id_usuario = Integer.parseInt(Base64Coder.decodeString(id_user));
        
        userDat = this.getHomeDao().getUserById(id_usuario);
        Integer id_empresa = Integer.parseInt(userDat.get("empresa_id"));
        
        
        //obtener tipo de facturacion
        String tipo_facturacion = this.getFacdao().getTipoFacturacion(id_empresa);
        
        if(tipo_facturacion.equals("cfd")){
            dirSalidas = this.getGralDao().getCfdEmitidosDir() + this.getGralDao().getRfcEmpresaEmisora(id_empresa);
        }
        
        if(tipo_facturacion.equals("cfdi")){
            dirSalidas = this.getGralDao().getCfdiSolicitudesDir() + "out/";
        }
        
        if(tipo_facturacion.equals("cfditf")){
            dirSalidas = this.getGralDao().getCfdiTimbreEmitidosDir() + this.getGralDao().getRfcEmpresaEmisora(id_empresa);
        }
        
        //nombre_archivo = this.getFacdao().getSerieFolioFactura(id_factura, id_empresa);
        nombre_archivo = this.getFacdao().getRefIdFactura(id_factura, id_empresa);
        
        //ruta completa del archivo a descargar
        String fileout = dirSalidas + "/" + nombre_archivo +".xml";
        
        
        System.out.println("Recuperando archivo: " + fileout);
        File file = new File(fileout);
        
        if (file.exists()){
            int size = (int) file.length(); // Tamaño del archivo
            BufferedInputStream bis = new BufferedInputStream(new FileInputStream(file));
            response.setBufferSize(size);
            response.setContentLength(size);
            response.setContentType("text/plain");
            response.setHeader("Content-Disposition","attachment; filename=\"" + file.getName() +"\"");
            FileCopyUtils.copy(bis, response.getOutputStream());  	
            response.flushBuffer();
            
        }
     
        return null;
    }
    
    
    
    
    
    
    
    
    
    
    //Reconstruir el pdf de la factura para CFD y CFDI con Tiembrado Fiscal
    @RequestMapping(method = RequestMethod.POST, value="/getReconstruirPdfFactura.json")
    public @ResponseBody HashMap<String,String> getReconstruirPdfFacturaJson(
            @RequestParam(value="id_factura", required=true) Integer id_factura,
            @RequestParam(value="iu", required=true) String id_user_cod,
            Model model
            ) {
        
        log.log(Level.INFO, "Ejecutando getReconstruirPdfFacturaJson de {0}", FacConsultasController.class.getName());
        HashMap<String, String> jsonretorno = new HashMap<String,String>();
        HashMap<String, String> userDat = new HashMap<String, String>();
        ArrayList<LinkedHashMap<String,String>> conceptos = new ArrayList<LinkedHashMap<String,String>>();
        HashMap<String,String> dataFacturaCliente = new HashMap<String,String>();
        ArrayList<HashMap<String, String>> listaConceptosPdfCfd = new ArrayList<HashMap<String, String>>();
        HashMap<String, String> datosExtrasPdfCfd= new HashMap<String, String>();
        HashMap<String, String> parametros = new HashMap<String, String>();
        ArrayList<String> leyendas = new ArrayList<String>();
        String generado ="false";
        String dirSalidas = "";
        
        //decodificar id de usuario
        Integer id_usuario = Integer.parseInt(Base64Coder.decodeString(id_user_cod));
        userDat = this.getHomeDao().getUserById(id_usuario);
        Integer id_empresa = Integer.parseInt(userDat.get("empresa_id"));
        Integer id_sucursal = Integer.parseInt(userDat.get("sucursal_id"));
        String rfcEmpresa = this.getGralDao().getRfcEmpresaEmisora(id_empresa);
        
        //obtener tipo de facturacion
        String tipo_facturacion = this.getFacdao().getTipoFacturacion(id_empresa);
        String serieFolio = this.getFacdao().getSerieFolioFactura(id_factura, id_empresa);
        String refId = this.getFacdao().getRefIdFactura(id_factura, id_empresa);
        Integer id_prefactura = this.getFacdao().getIdPrefacturaByIdFactura(id_factura);
        
        //aqui se obtienen los parametros de la facturacion, nos intersa el tipo de formato para el pdf de la factura
        parametros = this.getFacdao().getFac_Parametros(id_empresa, id_sucursal);
        
        //Obtiene las leyendas para agregarlo al PDF
        leyendas = this.getFacdao().getLeyendasEspecialesCfdi(id_empresa);
        
        String proposito = "FACTURA";
        String fileout="";
        File file;
        
        if(tipo_facturacion.equals("cfd")){
            dirSalidas = this.getGralDao().getCfdEmitidosDir() + rfcEmpresa;
            fileout = dirSalidas +"/"+ refId +".pdf";
            System.out.println("Ruta: " + fileout);
            file = new File(fileout);
            if (file.exists()){
                file.delete();
            }
            
            String cadena_xml = FileHelper.stringFromFile(dirSalidas+"/"+ refId +".xml");
            //System.out.println("cadena_xml: "+cadena_xml);
            try {
                
                String cadena_original = this.cadenaOriginal(cadena_xml, id_empresa, id_sucursal);
                System.out.println("cadena_original: "+cadena_original);
                
                String ruta_fichero_llave = this.getGralDao().getSslDir() + rfcEmpresa + "/" + this.getGralDao().getFicheroLlavePrivada(id_empresa, id_sucursal);
                System.out.println("ruta_fichero_llave: "+ruta_fichero_llave);
                
                String sello_digital_emisor = CryptoEngine.sign(ruta_fichero_llave, this.getGralDao().getPasswordLlavePrivada(id_empresa, id_sucursal), cadena_original);
                System.out.println("sello_digital_emisor: "+sello_digital_emisor);
                
                //BeanFromCfdiXml pop2 = new BeanFromCfdiXml(dirSalidas+"/"+serieFolio +".xml");
                
                BeanFromCfdXml pop = new BeanFromCfdXml(dirSalidas+"/"+ refId +".xml");
                
                //sacar la fecha del comprobante 
                String fecha_comprobante=pop.getFecha();
                
                //este es el timbre fiscal, solo es para cfdi con timbre fiscal. Aqui debe ir vacio
                String sello_digital_sat = "";
                String uuid = "";
                String fechaTimbre = "";
                String noCertSAT = "";
                
                
                conceptos = this.getFacdao().getListaConceptosFacturaXml(id_prefactura);
                dataFacturaCliente = this.getFacdao().getDataFacturaXml(id_prefactura);
                
                //conceptos para el pdfcfd
                listaConceptosPdfCfd = this.getFacdao().getListaConceptosPdfCfd(serieFolio);
                
                //datos para el pdf
                datosExtrasPdfCfd = this.getFacdao().getDatosExtrasPdfCfd( serieFolio, proposito, cadena_original, sello_digital_emisor, id_sucursal);
                datosExtrasPdfCfd.put("tipo_facturacion", tipo_facturacion);
                datosExtrasPdfCfd.put("sello_sat", sello_digital_sat);
                datosExtrasPdfCfd.put("uuid", uuid);
                datosExtrasPdfCfd.put("fecha_comprobante", fecha_comprobante);
                datosExtrasPdfCfd.put("fecha_comprobante", fecha_comprobante);
                datosExtrasPdfCfd.put("fechaTimbre", fechaTimbre);
                datosExtrasPdfCfd.put("noCertificadoSAT", noCertSAT);
                
                //pdf factura
                if (parametros.get("formato_factura").equals("2")){
                    pdfCfd_CfdiTimbradoFormato2 pdfFactura = new pdfCfd_CfdiTimbradoFormato2(this.getGralDao(), dataFacturaCliente, listaConceptosPdfCfd, leyendas, datosExtrasPdfCfd, id_empresa, id_sucursal);
                    pdfFactura.ViewPDF();
                }else{
                    pdfCfd_CfdiTimbrado pdfFactura = new pdfCfd_CfdiTimbrado(this.getGralDao(), dataFacturaCliente, listaConceptosPdfCfd, datosExtrasPdfCfd, id_empresa, id_sucursal);
                }
                
                generado ="true";
            } catch (Exception ex) {
                Logger.getLogger(FacConsultasController.class.getName()).log(Level.SEVERE, null, ex);
            }
            
            
            
        }
        
        
        if(tipo_facturacion.equals("cfditf")){
            dirSalidas = this.getGralDao().getCfdiTimbreEmitidosDir() + rfcEmpresa;
            fileout = dirSalidas +"/"+ refId +".pdf";
            file = new File(fileout);
            if (file.exists()){
                file.delete();
            }
            
            
            String cadena_xml = FileHelper.stringFromFile(dirSalidas+"/"+ refId +".xml");
            //System.out.println("cadena_xml: "+cadena_xml);
            
            try {
                
                String cadena_original = this.cadenaOriginalTimbre(cadena_xml, id_empresa, id_sucursal);
                System.out.println("cadena_original: "+cadena_original);
                
                //String ruta_fichero_llave = this.getGralDao().getSslDir() + rfcEmpresa + "/" + this.getGralDao().getFicheroLlavePrivada(id_empresa, id_sucursal);
                //System.out.println("ruta_fichero_llave: "+ruta_fichero_llave);
                
                //String sello_digital_emisor = CryptoEngine.sign(ruta_fichero_llave, this.getGralDao().getPasswordLlavePrivada(id_empresa, id_sucursal), cadena_original);
                //System.out.println("sello_digital_emisor: "+sello_digital_emisor);
                /*
                String cadena_original ="";
                String sello_digital_emisor="";
                */
                BeanFromCfdiXml pop2 = new BeanFromCfdiXml(dirSalidas+"/"+refId +".xml");
                
                //sacar la fecha del comprobante 
                String fecha_comprobante=pop2.getFecha_comprobante();
                
                String sello_digital_emisor=pop2.getSelloCfd();
                System.out.println("sello_digital_emisor: "+sello_digital_emisor);
                
                //este es el timbre fiscal, solo es para cfdi con timbre fiscal.
                String sello_digital_sat = pop2.getSelloSat();
                System.out.println("sello_digital_sat: "+sello_digital_sat);
                
                String uuid = pop2.getUuid();
                System.out.println("uuid: "+uuid);
                
                String fechaTimbre = pop2.getFecha_timbre();
                String noCertSAT = pop2.getNoCertificadoSAT();
                String rfcReceptor = pop2.getReceptor_rfc();
                
                //conceptos = this.getFacdao().getListaConceptosFacturaXml(id_prefactura);
                dataFacturaCliente = this.getFacdao().getDataFacturaXml(id_prefactura);
                
                //conceptos para el pdfcfd
                listaConceptosPdfCfd = this.getFacdao().getListaConceptosPdfCfd(serieFolio);
                
                //datos para el pdf
                datosExtrasPdfCfd = this.getFacdao().getDatosExtrasPdfCfd( serieFolio, proposito, cadena_original, sello_digital_emisor, id_sucursal);
                datosExtrasPdfCfd.put("tipo_facturacion", tipo_facturacion);
                datosExtrasPdfCfd.put("sello_sat", sello_digital_sat);
                datosExtrasPdfCfd.put("uuid", uuid);
                datosExtrasPdfCfd.put("fecha_comprobante", fecha_comprobante);
                datosExtrasPdfCfd.put("fechaTimbre", fechaTimbre);
                datosExtrasPdfCfd.put("noCertificadoSAT", noCertSAT);
                
                //pdf factura
                if (parametros.get("formato_factura").equals("2")){
                    pdfCfd_CfdiTimbradoFormato2 pdfFactura = new pdfCfd_CfdiTimbradoFormato2(this.getGralDao(), dataFacturaCliente, listaConceptosPdfCfd, leyendas, datosExtrasPdfCfd, id_empresa, id_sucursal);
                    pdfFactura.ViewPDF();
                }else{
                    pdfCfd_CfdiTimbrado pdfFactura = new pdfCfd_CfdiTimbrado(this.getGralDao(), dataFacturaCliente, listaConceptosPdfCfd, datosExtrasPdfCfd, id_empresa, id_sucursal);
                }
                
                generado ="true";
                
            } catch (Exception ex) {
                Logger.getLogger(FacConsultasController.class.getName()).log(Level.SEVERE, null, ex);
            }
            
        }
        
        
        if(tipo_facturacion.equals("cfdi")){
            dirSalidas = this.getGralDao().getCfdiSolicitudesDir() + "out/";
        }

        jsonretorno.put("generado", generado);
        
        return jsonretorno;
    }
        
    
    
    private String cadenaOriginal(String comprobante_sin_firmar, Integer id_empresa, Integer id_sucursal) throws Exception {
        String valor_retorno = new String();
        System.out.println("EsquemaXslt: "+this.getGralDao().getXslDir() + this.getGralDao().getRfcEmpresaEmisora(id_empresa)+"/"+ this.getGralDao().getFicheroXsl(id_empresa, id_sucursal));
        valor_retorno = XmlHelper.transformar(comprobante_sin_firmar, this.getGralDao().getXslDir() + this.getGralDao().getRfcEmpresaEmisora(id_empresa)+"/"+ this.getGralDao().getFicheroXsl(id_empresa, id_sucursal));
        
        return valor_retorno;
    }
    
    private String cadenaOriginalTimbre(String comprobante, Integer id_empresa, Integer id_sucursal) throws Exception {
        String valor_retorno = new String();
        System.out.println("EsquemaXslt: "+this.getGralDao().getXslDir() + this.getGralDao().getRfcEmpresaEmisora(id_empresa)+"/"+ this.getGralDao().getFicheroXslTimbre(id_empresa, id_sucursal));
        valor_retorno = XmlHelper.transformar(comprobante, this.getGralDao().getXslDir() + this.getGralDao().getRfcEmpresaEmisora(id_empresa)+"/"+ this.getGralDao().getFicheroXslTimbre(id_empresa, id_sucursal));
        
        return valor_retorno;
    }
    
    
    
    //Reconstruir el pdf de la factura para CFD y CFDI con Tiembrado Fiscal
    @RequestMapping(method = RequestMethod.POST, value="/getAddAddenda.json")
    public @ResponseBody HashMap<String,String> getAddAddendaJson(
            @RequestParam(value="id_fac", required=true) Integer id_fac_doc,
            @RequestParam(value="t_addenda_id", required=true) Integer tipo_addenda_id,
            @RequestParam(value="cadena_datos", required=true) String cadena_datos,
            @RequestParam(value="iu", required=true) String id_user_cod,
            Model model
        ) {
        
        log.log(Level.INFO, "Ejecutando getAddAddendaJson de {0}", FacConsultasController.class.getName());
        HashMap<String, String> jsonretorno = new HashMap<String,String>();
        HashMap<String, String> userDat = new HashMap<String, String>();
        HashMap<String, String> parametros = new HashMap<String, String>();
        HashMap<String, String> succes = new HashMap<String, String>();
        LinkedHashMap<String,Object> dataAdenda = new LinkedHashMap<String,Object>();
        /*
         dataFacturaCliente
         * Este solo lo declaro porque es un objeto que necesita el metodo que obtiene los datos para la addenda,
         * asi se declaro en las primeras addendas que se hicieron
         */
        HashMap<String,String> dataFacturaCliente = new HashMap<String,String>();
        
        //aplicativo Consulta de Facturas
        Integer app_selected = 142;
        String command_selected = "guardar_addenda";
        String extra_data_array = "'sin datos'";
        String data_string = "";
        String actualizo="0";
        String generado ="false";
        String dirSalidas = "";
        String msj = "";
        
        //Decodificar id de usuario
        Integer id_usuario = Integer.parseInt(Base64Coder.decodeString(id_user_cod));
        userDat = this.getHomeDao().getUserById(id_usuario);
        Integer id_empresa = Integer.parseInt(userDat.get("empresa_id"));
        Integer id_sucursal = Integer.parseInt(userDat.get("sucursal_id"));
        String rfcEmpresa = this.getGralDao().getRfcEmpresaEmisora(id_empresa);
        
        //Obtener tipo de facturacion
        String tipo_facturacion = this.getFacdao().getTipoFacturacion(id_empresa);
        String serieFolio = this.getFacdao().getSerieFolioFactura(id_fac_doc, id_empresa);
        String refId = this.getFacdao().getRefIdFactura(id_fac_doc, id_empresa);
        //Integer id_prefactura = this.getFacdao().getIdPrefacturaByIdFactura(id_factura);
        
        //aqui se obtienen los parametros de la facturacion, nos intersa el tipo de formato para el pdf de la factura
        parametros = this.getFacdao().getFac_Parametros(id_empresa, id_sucursal);
        
        
        String fileout="";
        File file;
        
        //Convertir en array los datos
        String array_datos [] = cadena_datos.split("\\|");
        
        String adenda_campo1="";
        String adenda_campo2="";
        String adenda_campo3="";
        String adenda_campo4="";
        String adenda_campo5="";
        String adenda_campo6="";
        String adenda_campo7="";
        String adenda_campo8="";
        
        //Guardar datos de la addenda
        if(tipo_addenda_id==3){
            //Verificar que los campos no vengan vacios(Si traen &&&&&, entonces se toma como vacio)
            adenda_campo1 = (array_datos[0].equals("___"))?"":array_datos[0];//Orden de compra
            adenda_campo2 = (array_datos[1].equals("___"))?"":array_datos[1].toLowerCase();//Email-Emisor
            adenda_campo3 = (array_datos[2].equals("___"))?"":array_datos[2];//Moneda
            adenda_campo4 = (array_datos[3].equals("___"))?"":array_datos[3];//Tipo de Cambio
            adenda_campo5 = (array_datos[4].equals("___"))?"":array_datos[4];//Subtotal factura
            adenda_campo6 = (array_datos[5].equals("___"))?"":array_datos[5];//Total Factura
        }
        
        //System.out.println("data_string: "+data_string);
        data_string = app_selected +"___"+command_selected+"___"+id_usuario+"___"+id_fac_doc+"___"+tipo_addenda_id+"___"+adenda_campo1.toUpperCase()+"___"+adenda_campo2+"___"+adenda_campo3.toUpperCase()+"___"+adenda_campo4.toUpperCase()+"___"+adenda_campo5.toUpperCase()+"___"+adenda_campo6.toUpperCase()+"___"+adenda_campo7.toUpperCase()+"___"+adenda_campo8.toUpperCase();
        System.out.println("data_string: "+data_string);
        
        //System.out.println(TimeHelper.getFechaActualYMDH()+"::::Inicia Validacion de la Prefactura::::::::::::::::::");
        succes = this.getFacdao().selectFunctionValidateAaplicativo(data_string,app_selected,extra_data_array);
        
        log.log(Level.INFO, TimeHelper.getFechaActualYMDH()+"Despues de validacion {0}", String.valueOf(succes.get("success")));
        
        //System.out.println(TimeHelper.getFechaActualYMDH()+": Inicia actualizacion de datos de la prefactura");
        if( String.valueOf(succes.get("success")).equals("true")){
            actualizo = this.getFacdao().selectFunctionForFacAdmProcesos(data_string, extra_data_array);
        }else{
            jsonretorno.put("success", String.valueOf(succes.get("success")));
        }
        
        
        
        if(actualizo.equals("1")){
            if(tipo_facturacion.equals("cfditf")){
                /*
                dirSalidas = this.getGralDao().getCfdiTimbreEmitidosDir() + rfcEmpresa;
                fileout = dirSalidas +"/"+ refId +".pdf";
                file = new File(fileout);
                if (file.exists()){
                    file.delete();
                }
                */
                
                //String cadena_xml = FileHelper.stringFromFile(dirSalidas+"/"+ refId +".xml");
                //System.out.println("cadena_xml: "+cadena_xml);

                try {

                    /*
                    String cadena_original ="";
                    String sello_digital_emisor="";
                    */
                    //BeanFromCfdiXml pop2 = new BeanFromCfdiXml(dirSalidas+"/"+refId +".xml");


                    //::::::INICIA AGREGAR ADENDA AL XML DEL CFDI::::::::::::::::::::::::::::::::::::::::::::::::::::::
                    System.out.println("incluye_adenda: "+parametros.get("incluye_adenda")+"  |  dataFacturaClienteAdendaID: "+tipo_addenda_id);

                    //Verificar si hay que incluir adenda
                    if (parametros.get("incluye_adenda").equals("true")){

                        Integer numAdenda = tipo_addenda_id;
                        
                        
                        if(this.getFacdao().getStatusAdendaFactura(numAdenda, id_fac_doc)<=0){
                        
                            //Verificar si el cliente tiene asignada una adenda
                            if(numAdenda>0){
                                String path_file = new String();
                                String xml_file_name = new String();

                                //Tipo de DOCUMENTO(1=Factura, 2=Consignacion, 3=Retenciones(Honorarios, Arrendamientos, Fletes), 8=Nota de Cargo, 9=Nota de Credito)
                                int tipoDocAdenda=1;


                                path_file = this.getGralDao().getCfdiTimbreEmitidosDir() + this.getGralDao().getRfcEmpresaEmisora(id_empresa);
                                xml_file_name = refId+".xml";


                                if(numAdenda==1){
                                    //Agregar estos datos para generar el objeto que contiene los datos de la Adenda
                                    dataFacturaCliente.put("emailEmisor", this.getGralDao().getEmailSucursal(id_sucursal));
                                }

                                //1 indica que es Adenda de una factura
                                dataAdenda = this.getFacdao().getDatosAdenda(tipoDocAdenda, numAdenda, dataFacturaCliente, id_fac_doc, serieFolio, id_empresa);

                                //INICIA EJECUCION DE CLASE QUE AGREGA LA ADENDA
                                AdendaCliente adenda = new AdendaCliente();
                                adenda.createAdenda(numAdenda, dataAdenda, path_file, xml_file_name);
                                //TERMINA EJECUCION DE CLASE QUE AGREGA LA ADENDA


                                //::::Actualizar registro para indicar que la addenda se ha generado::::::
                                command_selected = "actualizar_addenda";
                                data_string = app_selected +"___"+command_selected+"___"+id_usuario+"___"+id_fac_doc+"___"+tipo_addenda_id;
                                actualizo = this.getFacdao().selectFunctionForFacAdmProcesos(data_string, extra_data_array);
                                //::::Actualizar registro para indicar que la addenda se ha generado::::::

                                generado ="true";

                                File file_xml_con_adenda = new File(path_file+"/"+xml_file_name);
                                if(!file_xml_con_adenda.exists()){
                                    //Si el archivo NO existe indica que NO se agregó bien la adenda y NO se creó el nuevo archivo xml
                                    msj = "Hubo problemas al intentar crear el archivo xml["+xml_file_name+"].";
                                }else{
                                    msj = "La addenda se agreg&oacute; correctamente.";
                                }
                            }
                        }else{
                            msj = "La addenda ya fue generado anteriormente para la Factura "+serieFolio+".";
                            
                        }
                        
                    }
                    //::::::TERMINA AGREGAR ADENDA AL XML DEL CFDI::::::::::::::::::::::::::::::::::::::::::::::::::::::

                    

                } catch (Exception ex) {
                    //Logger.getLogger(FacConsultasController.class.getName()).log(Level.SEVERE, null, ex);
                    System.out.println("ERROR: "+ ex.getMessage());
                }

            }
            
        }else{
            //Falló a guardar datos.
            msj = "Fall&oacute; al intentar guardar datos.";
        }
        
        jsonretorno.put("success", String.valueOf(succes.get("success")));
        jsonretorno.put("actualizo",String.valueOf(actualizo));
        jsonretorno.put("generado", generado);
        jsonretorno.put("msj", msj);
        
        System.out.println("success="+jsonretorno.get("success")+"  |  actualizo="+jsonretorno.get("actualizo")+" | generado="+jsonretorno.get("generado")+" | msj="+jsonretorno.get("msj"));
        
        return jsonretorno;
    }
    
    
}
