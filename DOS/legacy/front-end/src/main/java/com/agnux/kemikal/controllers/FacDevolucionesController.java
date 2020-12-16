package com.agnux.kemikal.controllers;

import com.agnux.cfd.v2.Base64Coder;
import com.agnux.cfd.v2.BeanFacturador;
import com.agnux.cfdi.BeanFacturadorCfdi;
import com.agnux.cfdi.adendas.AdendaCliente;
import com.agnux.cfdi.timbre.BeanFacturadorCfdiTimbre;
import com.agnux.common.helpers.StringHelper;
import com.agnux.common.obj.DataPost;
import com.agnux.common.obj.ResourceProject;
import com.agnux.common.obj.UserSessionData;
import com.agnux.kemikal.interfacedaos.FacturasInterfaceDao;
import com.agnux.kemikal.interfacedaos.GralInterfaceDao;
import com.agnux.kemikal.interfacedaos.HomeInterfaceDao;
import com.agnux.kemikal.reportes.pdfCfd;
import com.agnux.kemikal.reportes.pdfCfd_CfdiTimbrado;
import com.agnux.kemikal.reportes.pdfCfd_CfdiTimbradoFormato2;
import java.io.File;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;


@Controller
@SessionAttributes({"user"})
@RequestMapping("/facdevoluciones/")
public class FacDevolucionesController {
    ResourceProject resource = new ResourceProject();
    private static final Logger log  = Logger.getLogger(FacDevolucionesController.class.getName());
    
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
    @Qualifier("beanFacturador")
    BeanFacturador bfcfd;
    
    @Autowired
    @Qualifier("beanFacturadorCfdi")
    BeanFacturadorCfdi bfcfdi;
    
    @Autowired
    @Qualifier("beanFacturadorCfdiTf")
    BeanFacturadorCfdiTimbre bfcfditf;
    
    public BeanFacturadorCfdiTimbre getBfcfditf() {
        return bfcfditf;
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
    
    public BeanFacturador getBfcfd() {
        return bfcfd;
    }
    
    public BeanFacturadorCfdi getBfcfdi() {
        return bfcfdi;
    }
    
    
    @RequestMapping(value="/startup.agnux")
    public ModelAndView startUp(HttpServletRequest request, HttpServletResponse response, 
            @ModelAttribute("user") UserSessionData user
            )throws ServletException, IOException {
        
        log.log(Level.INFO, "Ejecutando starUp de {0}", FacDevolucionesController.class.getName());
        LinkedHashMap<String,String> infoConstruccionTabla = new LinkedHashMap<String,String>();
        
        infoConstruccionTabla.put("id", "Acciones:70");
        infoConstruccionTabla.put("serie_folio", "Factura:80");
        infoConstruccionTabla.put("cliente", "Cliente:300");
        infoConstruccionTabla.put("total", "Monto:90");
        infoConstruccionTabla.put("moneda", "Moneda:60");
        infoConstruccionTabla.put("estado", "Estado:90");
        infoConstruccionTabla.put("fecha_facturacion","Fecha facturaci&oacute;n:110");
        
        ModelAndView x = new ModelAndView("facdevoluciones/startup", "title", "Devoluciones");
        
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
        
        //aplicativo Devolucion de Mercancia
        Integer app_selected = 76;
        
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
        
        String data_string = app_selected+"___"+id_usuario+"___"+factura+"___"+cliente+"___"+fecha_inicial+"___"+fecha_final+"___"+codigo+"___"+producto+"___"+agente;
        
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
    public @ResponseBody HashMap<String,ArrayList<HashMap<String, Object>>> getFacturaJson(
            @RequestParam(value="id_factura", required=true) Integer id_factura,
            @RequestParam(value="iu", required=true) String id_user,
            Model model
        ) {
        
        log.log(Level.INFO, "Ejecutando getFacturaJson de {0}", FacDevolucionesController.class.getName());
        HashMap<String,ArrayList<HashMap<String, Object>>> jsonretorno = new HashMap<String,ArrayList<HashMap<String, Object>>>();
        ArrayList<HashMap<String, Object>> datosFactura = new ArrayList<HashMap<String, Object>>();
        ArrayList<HashMap<String, Object>> datosGrid = new ArrayList<HashMap<String, Object>>();
        ArrayList<HashMap<String, Object>> datosNotaCredito = new ArrayList<HashMap<String, Object>>();
        ArrayList<HashMap<String, Object>> valorIva = new ArrayList<HashMap<String, Object>>();
        ArrayList<HashMap<String, Object>> tipoCambioActual = new ArrayList<HashMap<String, Object>>();
        HashMap<String, Object> tc = new HashMap<String, Object>();
        HashMap<String, String> userDat = new HashMap<String, String>();
        
        //aplicativo Devolucion de Mercancia
        Integer app_selected = 76;
        //Decodificar id de usuario
        Integer id_usuario = Integer.parseInt(Base64Coder.decodeString(id_user));
        
        userDat = this.getHomeDao().getUserById(id_usuario);
        
        Integer id_empresa = Integer.parseInt(userDat.get("empresa_id"));
        Integer id_sucursal = Integer.parseInt(userDat.get("sucursal_id"));
        
        if(id_factura!=0){
            datosFactura = this.getFacdao().getFactura_Datos(id_factura);
            datosGrid = this.getFacdao().getFactura_DatosGrid(id_factura);
            datosNotaCredito = this.getFacdao().getFacDevoluciones_DatosNotaCredito( datosFactura.get(0).get("serie_folio").toString(),  datosFactura.get(0).get("cliente_id").toString());
        }
        
        valorIva= this.getFacdao().getValoriva(id_sucursal);
        tc.put("tipo_cambio", StringHelper.roundDouble(this.getFacdao().getTipoCambioActual(), 4)) ;
        tipoCambioActual.add(0,tc);
        
        jsonretorno.put("datosFactura", datosFactura);
        jsonretorno.put("datosGrid", datosGrid);
        jsonretorno.put("iva", valorIva);
        jsonretorno.put("Tc", tipoCambioActual);
        jsonretorno.put("Monedas", this.getFacdao().getFactura_Monedas());
        jsonretorno.put("Vendedores", this.getFacdao().getFactura_Agentes(id_empresa, id_sucursal));
        jsonretorno.put("Condiciones", this.getFacdao().getFactura_DiasDeCredito());
        jsonretorno.put("MetodosPago", this.getFacdao().getMetodosPago(id_empresa));
        jsonretorno.put("NCred", datosNotaCredito);
        jsonretorno.put("TMov", this.getFacdao().getCtb_TiposDeMovimiento(id_empresa, app_selected));
        
        return jsonretorno;
    }
    
    
    
    //Edicion y nuevo
    @RequestMapping(method = RequestMethod.POST, value="/edit.json")
    public @ResponseBody HashMap<String, String> editJson(
            @RequestParam(value="id_factura", required=true) Integer id_factura,
            @RequestParam(value="serie_folio", required=true) String factura,
            @RequestParam(value="id_cliente", required=true) String id_cliente,
            @RequestParam(value="id_impuesto", required=true) String id_impuesto,
            @RequestParam(value="valorimpuesto", required=true) String valor_iva,
            @RequestParam(value="moneda", required=true) String select_moneda,
            @RequestParam(value="vendedor", required=true) String select_vendedor,
            @RequestParam(value="concepto", required=true) String concepto,
            @RequestParam(value="tasa_retencion", required=true) String tasa_retencion,
            @RequestParam(value="tipo_cambio_nota", required=true) String tipo_cambio_nota,
            @RequestParam(value="saldo_fac", required=true) String saldo_fac,
            @RequestParam(value="select_tmov", required=true) Integer select_tmov,
            @RequestParam(value="idproducto", required=false) String[] producto_id,
            @RequestParam(value="idpres", required=false) String[] idpres,
            @RequestParam(value="seleccionado", required=false) String[] seleccionado,
            @RequestParam(value="cantidad", required=false) String[] cantidad,
            @RequestParam(value="costo", required=false) String[] costo,
            @RequestParam(value="cantidad_dev", required=false) String[] cantidad_dev,
            @RequestParam(value="idIeps", required=false) String[] idIeps,
            @RequestParam(value="tasaIeps", required=false) String[] tasaIeps,
            @ModelAttribute("user") UserSessionData user
        ) throws Exception {
            
            System.out.println("Registrar Devolucion de Mercancia");
            HashMap<String, String> jsonretorno = new HashMap<String, String>();
            HashMap<String, String> succes = new HashMap<String, String>();
            HashMap<String, String> userDat = new HashMap<String, String>();
            //HashMap<String,String> datos_emisor = new HashMap<String,String>();
            HashMap<String, String> parametros = new HashMap<String, String>();
            
            //Variables para xml de Nota de Credito CFD y CFDI
            HashMap<String,String> dataCliente = new HashMap<String,String>();
            ArrayList<LinkedHashMap<String,String>> listaConceptos = new ArrayList<LinkedHashMap<String,String>>();
            ArrayList<LinkedHashMap<String,String>> conceptos = new ArrayList<LinkedHashMap<String,String>>();
            ArrayList<HashMap<String, String>> arrayIvas = new ArrayList<HashMap<String, String>>();
            ArrayList<HashMap<String, String>> arrayIeps = new ArrayList<HashMap<String, String>>();
            ArrayList<LinkedHashMap<String,String>> impTrasladados = new ArrayList<LinkedHashMap<String,String>>();
            ArrayList<LinkedHashMap<String,String>> impRetenidos = new ArrayList<LinkedHashMap<String,String>>();
            LinkedHashMap<String,String> datosExtras = new LinkedHashMap<String,String>();
            LinkedHashMap<String,Object> dataAdenda = new LinkedHashMap<String,Object>();
            
            //::ESTAS VARIABLES SON SOLO PARA NOMINAS, AQUI SE DECLARA SOLO PARA RELLENAR PARAMETROS::::::::::::
            ArrayList<LinkedHashMap<String,String>> percepciones = new ArrayList<LinkedHashMap<String,String>>();
            ArrayList<LinkedHashMap<String,String>> deducciones = new ArrayList<LinkedHashMap<String,String>>();
            ArrayList<LinkedHashMap<String,String>> incapacidades = new ArrayList<LinkedHashMap<String,String>>();
            ArrayList<LinkedHashMap<String,String>> hrs_extras = new ArrayList<LinkedHashMap<String,String>>();
            //::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
            
            //variables para Nota de Credito en CFDI
            ArrayList<String> leyendas = new ArrayList<String>();
            
            //variables para PDF de Nota de Credito CFD
            ArrayList<HashMap<String, String>> listaConceptosPdf = new ArrayList<HashMap<String, String>>();
            HashMap<String, String> datosExtrasPdf= new HashMap<String, String>();
            
            //Aplicativo Devolucion de Mercancia
            Integer app_selected = 76;
            String command_selected = "new";
            //Variable para el id  del usuario
            Integer id_usuario= user.getUserId();
            String extra_data_array = "'sin datos'";
            String actualizo = "0";
            String serieFolio="";
            String refId="";
            String rfcEmisor="";
            String tipo_facturacion="";
            String observaciones="NOTA DE CREDITO POR DEVOLUCION DE MERCANCIA";
            String importe="";
            String ieps="";
            String impuesto="";
            String retencion="";
            String total="";
            String generar="true";
            //Variable que almacena valor true cuando la factura se saldará por completo con la NC
            String fac_saldado="false";
            Integer id_nota_credito=0;
            //Importe antes de Impuestos de la partida
            Double importePartida=0.0;
            //Tasa del IEPS de la Partida
            Double tasaIepsPartida=0.0;
            //Importe del IEPS de la Partida
            Double importeIepsPartida=0.0;
            //Importe del IVA de la Partida
            Double impuestoPartida=0.0;
            //Suma de los importes de las partidad
            Double sumaSubTotal = 0.0;
            //Suma de los importes del IEPS
            Double sumaIeps = 0.0;
            //Suma de los importes del iva
            Double sumaImpuesto = 0.0;
            //Monto del iva retenido de acuerdo a la tasa de retencion immex
            Double impuestoRetenido = 0.0;
            //Suma del sumaSubTotal + sumaIeps + sumaImpuesto - impuestoRetenido
            Double sumaTotal = 0.0;
            String select_tipo_documento = "0";
            String refacturar = "false";
            String valorRespuesta="false";
            String msjRespuesta="";
            //Variable que indica si terminó bien el proceso de agregar la Adenda, por default es verdadero, si ocurre algu problema en el proceso se le asigna un false.
            boolean procesoAdendaCorrecto=true;
            
            userDat = this.getHomeDao().getUserById(id_usuario);
            Integer id_empresa = Integer.parseInt(userDat.get("empresa_id"));
            Integer id_sucursal = Integer.parseInt(userDat.get("sucursal_id"));
            
            String arreglo[];
            arreglo = new String[seleccionado.length];
            
            for(int i=0; i<seleccionado.length; i++) { 
                tasaIepsPartida=0.0;
                importePartida = 0.0;
                impuestoPartida = 0.0;
                importeIepsPartida=0.0;
                //calcular totales de los seleccionados
                if(seleccionado[i].equals("1")){
                    importePartida = Double.parseDouble(costo[i]) * Double.parseDouble(cantidad_dev[i]);
                    
                    if(!tasaIeps[i].trim().equals("")){
                        tasaIepsPartida = Double.parseDouble(tasaIeps[i]);
                        if (tasaIepsPartida>0){
                            tasaIepsPartida = tasaIepsPartida/100;
                            importeIepsPartida = importePartida * tasaIepsPartida;
                        }
                    }
                    
                    impuestoPartida = (importePartida + importeIepsPartida) * Double.parseDouble(valor_iva);
                    
                    sumaSubTotal = sumaSubTotal + importePartida;
                    sumaIeps = sumaIeps + importeIepsPartida;
                    sumaImpuesto = sumaImpuesto + impuestoPartida;
                    //System.out.println(costo[i]+"  "+ cantidad_dev[i] +"   "+ importePartida + "   "+impuestoPartida);
                }
                arreglo[i]= "'"+seleccionado[i]+"___"+producto_id[i] +"___" + cantidad[i] +"___" + cantidad_dev[i] +"___"+id_impuesto+"___"+valor_iva+"___"+idpres[i]+"___"+costo[i]+"___"+idIeps[i]+"___"+tasaIepsPartida+"'";
                //System.out.println(arreglo[i]);
            }
            
            //Calcular el total de la retencion
            impuestoRetenido = sumaSubTotal * Double.parseDouble(tasa_retencion);
            
            //Calcula el total sumando el subtotal y el impuesto
            sumaTotal = sumaSubTotal + sumaIeps + sumaImpuesto - impuestoRetenido;
            
            importe = StringHelper.roundDouble(sumaSubTotal,2);
            ieps = StringHelper.roundDouble(sumaIeps,2);
            impuesto = StringHelper.roundDouble(sumaImpuesto,2);
            retencion = StringHelper.roundDouble(impuestoRetenido,2);
            total = StringHelper.roundDouble(sumaTotal,2);
            
            saldo_fac = StringHelper.removerComas(saldo_fac);
            
            if( Double.parseDouble(total) >= Double.parseDouble(saldo_fac) ){
                fac_saldado="true";//indica que la factura queda saldado con la Nota de Credito
            }
            
            //Serializar el arreglo
            extra_data_array = StringUtils.join(arreglo, ",");
            
            String data_string = app_selected+"___"+command_selected+"___"+id_usuario+"___"+id_factura+"___"+id_cliente+"___"+id_impuesto+"___"+valor_iva+"___"+observaciones.toUpperCase()+"___"+select_moneda+"___"+select_vendedor+"___"+concepto.toUpperCase()+"___"+tipo_cambio_nota+"___"+importe+"___"+impuesto+"___"+retencion+"___"+total+"___"+factura+"___"+ieps+"___"+select_tmov;
            
            //System.out.println("data_string: "+data_string);
            
            succes = this.getFacdao().selectFunctionValidateAaplicativo(data_string,app_selected,extra_data_array);
            
            log.log(Level.INFO, "despues de validacion {0}", String.valueOf(succes.get("success")));
            
            if(String.valueOf(succes.get("success")).equals("true")){
                String retorno = this.getFacdao().selectFunctionForFacAdmProcesos(data_string, extra_data_array);
                actualizo = retorno.split(":")[0];
                id_nota_credito = Integer.parseInt(retorno.split(":")[1]);
                jsonretorno.put("actualizo",String.valueOf(actualizo));
            }
            
            //System.out.println("Actualizo::: "+actualizo);
            if(generar.equals("true")){
                
                if(actualizo.equals("1")){
                    System.out.println("::::::::::::Iniciando Generacion de NOTA DE CREDITO por devolucion de Mercancia:::::::::::::::::..");
                    String proposito = "NOTA_CREDITO";
                    
                    //Obtener tipo de facturacion
                    tipo_facturacion = this.getFacdao().getTipoFacturacion(id_empresa);
                    
                    //Obtener el numero del PAC para el Timbrado de la Nota de Credito
                    String noPac = this.getFacdao().getNoPacFacturacion(id_empresa);
                    
                    //Obtener el Ambiente de Facturacion PRUEBAS ó PRODUCCION, solo aplica para Facturacion por Timbre FIscal(cfditf)
                    String ambienteFac = this.getFacdao().getAmbienteFacturacion(id_empresa);
                    
                    System.out.println("Tipo::"+tipo_facturacion+" | noPac::"+noPac+" | Ambiente::"+ambienteFac);
                    
                    //Aqui se obtienen los parametros de la facturacion, nos intersa el tipo de formato para el pdf de la Nota de Credito
                    parametros = this.getFacdao().getFac_Parametros(id_empresa, id_sucursal);

                    //tipo facturacion CFDITF
                    if(tipo_facturacion.equals("cfditf")){
                        System.out.println("::::::::::::Tipo CFDITF:::::::::::::::::..");
                        
                        //Pac 0=Sin PAC, 1=Diverza, 2=ServiSim
                        if(!noPac.equals("0")){
                            //Solo se permite Timbrado con Diverza y ServiSim
                            
                            data_string="";
                            extra_data_array = "'sin datos'";
                            command_selected="genera_nota_credito_cfditf";
                            
                            //Obtener los valores del IEPS e IVAque se estan utilizando
                            arrayIeps = this.getFacdao().getIeps(id_empresa);
                            arrayIvas = this.getFacdao().getIvas();
                            
                            String Serie=this.getGralDao().getSerieNotaCredito(id_empresa, id_sucursal);
                            String Folio=this.getGralDao().getFolioNotaCredito(id_empresa, id_sucursal);
                            rfcEmisor = this.getGralDao().getRfcEmpresaEmisora(id_empresa);

                            //Obtener el concepto para la Nota de Credito cfditf
                            listaConceptos = this.getFacdao().getNotaCreditoCfdiTf_ListaConceptosXml(id_nota_credito);
                            impRetenidos = this.getFacdao().getNotaCreditoCfd_CfdiTf_ImpuestosRetenidosXml();
                            
                            //Aqui solo se obtienen los productos de la Devolución para calcular impuestos
                            conceptos = this.getFacdao().getNotaCreditoCfdiTf_ConceptosParaImpuestosXml(id_nota_credito);
                            impTrasladados = this.getFacdao().getImpuestosTrasladadosFacturaXml(id_sucursal, conceptos, arrayIeps, arrayIvas);
                            
                            //impTrasladados = this.getFacdao().getNotaCreditoCfd_CfdiTf_ImpuestosTrasladadosXml(id_sucursal);
                            dataCliente = this.getFacdao().getNotaCreditoCfd_Cfdi_Datos(id_nota_credito);
                            
                            //obtiene datos extras para el cfdi
                            datosExtras = this.getFacdao().getNotaCreditoCfdi_DatosExtras(id_nota_credito, Serie, Folio);
                            
                            dataCliente.put("comprobante_attr_tc", String.valueOf(datosExtras.get("tipo_cambio")));
                            dataCliente.put("comprobante_attr_moneda", String.valueOf(datosExtras.get("nombre_moneda")));

                            //estos son requeridos para cfditf
                            datosExtras.put("prefactura_id", String.valueOf(id_nota_credito));
                            datosExtras.put("tipo_documento", String.valueOf(select_tipo_documento));
                            datosExtras.put("moneda_id", select_moneda);
                            datosExtras.put("usuario_id", String.valueOf(id_usuario));
                            datosExtras.put("empresa_id", String.valueOf(id_empresa));
                            datosExtras.put("sucursal_id", String.valueOf(id_sucursal));
                            datosExtras.put("refacturar",  refacturar);
                            datosExtras.put("app_selected", String.valueOf(app_selected));
                            datosExtras.put("command_selected", command_selected);
                            datosExtras.put("extra_data_array", extra_data_array);
                            datosExtras.put("noPac", noPac);
                            datosExtras.put("ambienteFac", ambienteFac);
                            
                            
                            //genera xml factura
                            this.getBfcfditf().init(dataCliente, listaConceptos, impRetenidos, impTrasladados, proposito, datosExtras, id_empresa, id_sucursal, percepciones, deducciones, incapacidades, hrs_extras);
                            String timbrado_correcto = this.getBfcfditf().start();
                            
                            String cadRes[] = timbrado_correcto.split("___");
                            
                            
                            /***********************************************/
                            //aqui se checa si el xml fue validado correctamente
                            //si fue correcto debe traer un valor "true", de otra manera trae un error y por lo tanto no se genera el pdf
                            if(cadRes[0].equals("true")){
                                
                                //aqui se debe actializar el registro
                                data_string = app_selected+"___"+command_selected+"___"+id_usuario+"___"+id_nota_credito+"___"+Serie+Folio+"___"+fac_saldado+"___"+id_cliente+"___"+this.getBfcfditf().getRef_id()+"___"+this.getBfcfditf().getXml_timbrado();

                                actualizo = this.getFacdao().selectFunctionForFacAdmProcesos(data_string, extra_data_array);

                                //actualiza el folio de la Nota de Credito
                                this.getGralDao().actualizarFolioNotaCredito(id_empresa, id_sucursal);

                                /*======================================================*/
                                /*Codigo para generar el pdf para nota de credito*/
                                //obtiene serie_folio de la Nota de Credito que se acaba de guardar
                                serieFolio = this.getFacdao().getSerieFolioNotaCredito(id_nota_credito);
                                refId = this.getFacdao().getRefIdNotaCredito(id_nota_credito);
                                
                                String cadena_original=this.getBfcfditf().getCadenaOriginalTimbre();
                                //System.out.println("cadena_original:"+cadena_original);

                                String sello_digital = this.getBfcfditf().getSelloDigital();
                                //System.out.println("sello_digital:"+sello_digital);

                                //este es el timbre fiscal, solo es para cfdi con timbre fiscal. Aqui debe ir vacio
                                String sello_digital_sat = this.getBfcfditf().getSelloDigitalSat();

                                //este es el folio fiscal del la factura timbrada, se obtiene   del xml
                                String uuid = this.getBfcfditf().getUuid();
                                String fechaTimbre = this.getBfcfditf().getFechaTimbrado();
                                String noCertSAT = this.getBfcfditf().getNoCertificadoSAT();

                                //conceptos para el pdfcfd
                                listaConceptosPdf = this.getFacdao().getNotaCreditoCfd_ListaConceptosPdf(serieFolio);
                                
                                //Datos para el pdf
                                datosExtrasPdf = this.getFacdao().getNotaCreditoCfd_DatosExtrasPdf( serieFolio, proposito, cadena_original,sello_digital, id_sucursal, id_empresa);
                                datosExtrasPdf.put("tipo_facturacion", tipo_facturacion);
                                datosExtrasPdf.put("sello_sat", sello_digital_sat);
                                datosExtrasPdf.put("uuid", uuid);
                                datosExtrasPdf.put("fechaTimbre", fechaTimbre);
                                datosExtrasPdf.put("noCertificadoSAT", noCertSAT);
                                datosExtrasPdf.put("fecha_comprobante", this.getBfcfditf().getFecha());
                                
                                //pdf Nota
                                if (parametros.get("formato_factura").equals("2")){
                                    pdfCfd_CfdiTimbradoFormato2 pdfFactura = new pdfCfd_CfdiTimbradoFormato2(this.getGralDao(), dataCliente, listaConceptosPdf,  leyendas, datosExtrasPdf, id_empresa, id_sucursal);
                                    pdfFactura.ViewPDF();
                                }else{
                                    pdfCfd_CfdiTimbrado pdfFactura = new pdfCfd_CfdiTimbrado(this.getGralDao(), dataCliente, listaConceptosPdf, datosExtrasPdf, id_empresa, id_sucursal);
                                }
                                
                                
                                //::::::INICIA AGREGAR ADENDA AL XML DEL CFDI::::::::::::::::::::::::::::::::::::::::::::::::::::::
                                System.out.println("incluye_adenda: "+parametros.get("incluye_adenda")+"  |  dataFacturaClienteAdendaID: "+dataCliente.get("adenda_id"));
                                
                                //Verificar si hay que incluir adenda
                                if (parametros.get("incluye_adenda").equals("true")){
                                    Integer numAdenda = Integer.parseInt(dataCliente.get("adenda_id"));
                                    
                                    //Verificar si el cliente tiene asignada una adenda
                                    if(numAdenda>0){
                                        
                                        //Buscar si la Factura ligada a la Nota de credito Incluye Adenda
                                        int exis = this.getFacdao().buscarAdendaFactura(id_nota_credito);
                                        
                                        //Si es mayor que cero si Incluye Adenda
                                        if(exis>0){
                                        
                                            String path_file = new String();
                                            String xml_file_name = new String();
                                            
                                            //Tipo 9=Nota de credito
                                            int tipoDocAdenda=9;
                                            
                                            path_file = this.getGralDao().getCfdiTimbreEmitidosDir() + this.getGralDao().getRfcEmpresaEmisora(id_empresa);
                                            xml_file_name = refId+".xml";
                                            
                                            //Agregar estos datos para generar el objeto que contiene los datos de la Adenda
                                            dataCliente.put("emailEmisor", this.getGralDao().getEmailSucursal(id_sucursal));
                                            
                                            
                                            //1 indica que es Adenda de una factura
                                            dataAdenda = this.getFacdao().getDatosAdenda(tipoDocAdenda, Integer.parseInt(dataCliente.get("adenda_id")), dataCliente, id_nota_credito, serieFolio, id_empresa);
                                            
                                            //INICIA EJECUCION DE CLASE QUE AGREGA LA ADENDA
                                            AdendaCliente adenda = new AdendaCliente();
                                            adenda.createAdenda(numAdenda, dataAdenda, path_file, xml_file_name);
                                            //TERMINA EJECUCION DE CLASE QUE AGREGA LA ADENDA
                                            
                                            File file_xml_con_adenda = new File(path_file+"/"+xml_file_name);
                                            if(!file_xml_con_adenda.exists()){
                                                //Si el archivo existe indica que se agregó bien la adenda y se creó el nuevo archivo xml
                                                procesoAdendaCorrecto=false;
                                            }
                                        
                                        }else{
                                           System.out.println("La factura ligada a esta Nota de Credito no incluye Adenda, por lo tanto la Nota de Credito tampoco se le incluyó la Adenda.");
                                        }
                                        
                                    }
                                }
                                //::::::TERMINA AGREGAR ADENDA AL XML DEL CFDI::::::::::::::::::::::::::::::::::::::::::::::::::::::
                                
                                
                                jsonretorno.put("folio",Serie+Folio);
                                valorRespuesta="true";
                                //msjRespuesta=cadRes[1];
                                msjRespuesta = "La devoluci&oacute;n se realiz&oacute; con &eacute;xito\nSe gener&oacute; la Nota de Cr&eacute;dito: "+Serie+Folio;
                                
                                if (!procesoAdendaCorrecto){
                                    msjRespuesta = msjRespuesta + ", pero no fue posible agregar la Adenda.\nContacte a Soporte.";
                                }
                                
                            }else{
                                valorRespuesta="false";
                                msjRespuesta=cadRes[1];
                            }
                            
                        }else{
                            valorRespuesta="false";
                            msjRespuesta="No se puede Timbrar la Nota de Cr&eacute;dito con el PAC actual.\nVerifique la configuraci&oacute;n del tipo de Facturaci&oacute;n y del PAC.";
                        }
                        
                            //jsonretorno.put("folio",Serie+Folio);
                    }//termina tipo CFDITF
                    
                    //System.out.println("Folio: "+ String.valueOf(jsonretorno.get("folio")));
                    
                }else{
                    if(actualizo.equals("0")){
                        jsonretorno.put("actualizo",String.valueOf(actualizo));
                        
                        valorRespuesta="false";
                        msjRespuesta="Error al intentar actualizar los datos de la Nota de Cr&eacute;dito. Intente de Nuevo.";
                    }
                }
            }//termina if genarar
            
            
            jsonretorno.put("success",String.valueOf(succes.get("success")));
            jsonretorno.put("valor",valorRespuesta);
            jsonretorno.put("msj",msjRespuesta);
        
            log.log(Level.INFO, "Salida json {0}", String.valueOf(jsonretorno.get("success")));
            System.out.println("valorRespuesta: "+String.valueOf(valorRespuesta));
            System.out.println("msjRespuesta: "+String.valueOf(msjRespuesta));
        return jsonretorno;
    }
    
    
    
    
    
    
    
    
    
    
    
    
}
