package com.agnux.kemikal.controllers;

import com.agnux.cfd.v2.Base64Coder;
import com.agnux.common.helpers.FileHelper;
import com.agnux.common.helpers.StringHelper;
import com.agnux.common.obj.DataPost;
import com.agnux.common.obj.ResourceProject;
import com.agnux.common.obj.UserSessionData;
import com.agnux.kemikal.interfacedaos.CxpInterfaceDao;
import com.agnux.kemikal.interfacedaos.GralInterfaceDao;
import com.agnux.kemikal.interfacedaos.HomeInterfaceDao;
import com.agnux.kemikal.reportes.PdfNotaCreditoProveedor;
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


@Controller
@SessionAttributes({"user"})
@RequestMapping("/provnotascredito/")
public class ProvNotasCreditoController {
    ResourceProject resource = new ResourceProject();
    private static final Logger log  = Logger.getLogger(ProvNotasCreditoController.class.getName());
    
    @Autowired
    @Qualifier("daoGral")
    private GralInterfaceDao gralDao;
    
    @Autowired
    @Qualifier("daoCxp")
    private CxpInterfaceDao cxpDao;
    
    @Autowired
    @Qualifier("daoHome")
    private HomeInterfaceDao HomeDao;
    
    public HomeInterfaceDao getHomeDao() {
        return HomeDao;
    }
    
    public GralInterfaceDao getGralDao() {
        return gralDao;
    }
    
    public CxpInterfaceDao getCxpDao() {
        return cxpDao;
    }
    
    @RequestMapping(value="/startup.agnux")
    public ModelAndView startUp(HttpServletRequest request, HttpServletResponse response, 
            @ModelAttribute("user") UserSessionData user
            )throws ServletException, IOException {
        
        log.log(Level.INFO, "Ejecutando starUp de {0}", ProvNotasCreditoController.class.getName());
        LinkedHashMap<String,String> infoConstruccionTabla = new LinkedHashMap<String,String>();
        
        infoConstruccionTabla.put("id", "Acciones:70");
        infoConstruccionTabla.put("folio", "Folio:80");
        infoConstruccionTabla.put("nc", "Nota Cr&eacute;dito:100");
        infoConstruccionTabla.put("proveedor", "Proveedor:300");
        infoConstruccionTabla.put("total", "Monto:90");
        infoConstruccionTabla.put("fecha_expedicion","Fecha Expedici&oacute;n:110");
        infoConstruccionTabla.put("moneda", "Moneda:60");
        infoConstruccionTabla.put("factura", "Factura:80");
        infoConstruccionTabla.put("estado", "Estado:90");
        
        
        ModelAndView x = new ModelAndView("provnotascredito/startup", "title", "Notas de Cr&eacute;dito - Proveedores");
        
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
    
    
    
    
    @RequestMapping(value="/getAllProvNotasCredito.json", method = RequestMethod.POST)
    public @ResponseBody HashMap<String,ArrayList<HashMap<String, Object>>> getAllProvNotasCreditoJson(
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
        
        //aplicativo Notas de Credito Proveedores
        Integer app_selected = 101;
        
        //decodificar id de usuario
        Integer id_usuario = Integer.parseInt(Base64Coder.decodeString(id_user_cod));
        
        //variables para el buscador
        String folio = "%"+StringHelper.isNullString(String.valueOf(has_busqueda.get("folio")))+"%";
        String nota_credito = "%"+StringHelper.isNullString(String.valueOf(has_busqueda.get("nota_credito")))+"%";
        String proveedor = "%"+StringHelper.isNullString(String.valueOf(has_busqueda.get("proveedor")))+"%";
        String fecha_inicial = ""+StringHelper.isNullString(String.valueOf(has_busqueda.get("fecha_inicial")))+"";
        String fecha_final = ""+StringHelper.isNullString(String.valueOf(has_busqueda.get("fecha_final")))+"";
        
        String data_string = app_selected+"___"+id_usuario+"___"+folio+"___"+nota_credito+"___"+proveedor+"___"+fecha_inicial+"___"+fecha_final;
        
        //obtiene total de registros en base de datos, con los parametros de busqueda
        int total_items = this.getCxpDao().countAll(data_string);
        
        //calcula el total de paginas
        int total_pags = resource.calculaTotalPag(total_items,items_por_pag);
        
        //variables que necesita el datagrid, para no tener que hacer uno por cada aplicativo
        DataPost dataforpos = new DataPost(orderby, desc, items_por_pag, pag_start, display_pag, input_json, cadena_busqueda,total_items,total_pags, id_user_cod);
        
        int offset = resource.__get_inicio_offset(items_por_pag, pag_start);
        
        //obtiene los registros para el grid, de acuerdo a los parametros de busqueda
        jsonretorno.put("Data", this.getCxpDao().getProvNotasCredito_PaginaGrid(data_string, offset, items_por_pag, orderby, desc));
        //obtiene el hash para los datos que necesita el datagrid
        jsonretorno.put("DataForGrid", dataforpos.formaHashForPos(dataforpos));
        
        return jsonretorno;
    }
    
    
    
    //obtiene los proveedores para el buscador
    @RequestMapping(method = RequestMethod.POST, value="/getProveedores.json")
    public @ResponseBody HashMap<String,ArrayList<HashMap<String, String>>> getProveedoresJson(
            @RequestParam(value="rfc", required=true) String rfc,
            @RequestParam(value="email", required=true) String email,
            @RequestParam(value="nombre", required=true) String nombre,
            @RequestParam(value="iu", required=true) String id_user,
            Model model
            ) {
        
        log.log(Level.INFO, "Ejecutando getProveedoresJson de {0}", ProvNotasCreditoController.class.getName());
        HashMap<String,ArrayList<HashMap<String, String>>> jsonretorno = new HashMap<String,ArrayList<HashMap<String, String>>>();
        ArrayList<HashMap<String, String>> proveedores = new ArrayList<HashMap<String, String>>();
        HashMap<String, String> userDat = new HashMap<String, String>();
        
        //decodificar id de usuario
        Integer id_usuario = Integer.parseInt(Base64Coder.decodeString(id_user));
        userDat = this.getHomeDao().getUserById(id_usuario);
        
        Integer id_empresa = Integer.parseInt(userDat.get("empresa_id"));
        
        proveedores = this.getCxpDao().getBuscadorProveedores(rfc, email, nombre,id_empresa);
        
        jsonretorno.put("proveedores", proveedores);
        
        return jsonretorno;
    }
    
    
    
    
    
    //Obtener el valor del impuesto a utilizar para la nota de credito
    @RequestMapping(method = RequestMethod.POST, value="/getValorIva.json")
    public @ResponseBody HashMap<String, String> getValorIvaJson(
            @RequestParam(value="id_proveedor", required=true) Integer id_proveedor,
            @RequestParam(value="iu", required=true) String id_user,
            Model model
            ) {
        
        HashMap<String, String> jsonretorno = new HashMap<String, String>();
        ArrayList<HashMap<String, String>> IvaProveedor = new ArrayList<HashMap<String, String>>();
        ArrayList<HashMap<String, String>> IvaSucursal = new ArrayList<HashMap<String, String>>();
        HashMap<String, String> userDat = new HashMap<String, String>();
        int id_impuesto_proveedor=0;
        //decodificar id de usuario
        Integer id_usuario = Integer.parseInt(Base64Coder.decodeString(id_user));
        
        userDat = this.getHomeDao().getUserById(id_usuario);
        
        Integer id_empresa = Integer.parseInt(userDat.get("empresa_id"));
        Integer id_sucursal = Integer.parseInt(userDat.get("sucursal_id"));
        
        
        //obtener el tipo e impuesto asignado al Proveedor
        IvaProveedor = this.getCxpDao().getProvNotasCredito_Impuesto(id_proveedor);
        
        //obtener el id del impuesto del proveedor
        id_impuesto_proveedor = Integer.parseInt(IvaProveedor.get(0).get("id_impuesto"));
        
        if(id_impuesto_proveedor == 0){
            //si el proveedor no tiene asignado un impuesto, se toma el de la sucursal actual
            IvaSucursal = this.getCxpDao().getValoriva(id_sucursal);
            jsonretorno.put("valor_impuesto", IvaSucursal.get(0).get("valor_impuesto"));
            jsonretorno.put("id_impuesto", IvaSucursal.get(0).get("id_impuesto"));
        }else{
            //si el proveedor  si tiene asignado un impuesto, se  toma el valor
            //si el proveedor es Extranjero, este valos siempre viene en cero
            jsonretorno.put("valor_impuesto", IvaProveedor.get(0).get("valor_impuesto"));
            jsonretorno.put("id_impuesto", IvaProveedor.get(0).get("id_impuesto"));
        }
        
        return jsonretorno;
    }
    
    
    
    
    //Buscador de facturas del Proveedor
    @RequestMapping(method = RequestMethod.POST, value="/getFacturasProveedor.json")
    public @ResponseBody HashMap<String,ArrayList<HashMap<String, String>>> getValorIvaJson(
            @RequestParam(value="id_proveedor", required=true) Integer id_proveedor,
            Model model
            ) {
        
        HashMap<String,ArrayList<HashMap<String, String>>> jsonretorno = new HashMap<String,ArrayList<HashMap<String, String>>>();
        
        jsonretorno.put("Facturas", this.getCxpDao().getProvNotasCredito_Facturas(id_proveedor));
        return jsonretorno;
    }
    
    
    
    
    
    
    @RequestMapping(method = RequestMethod.POST, value="/getNotaCredito.json")
    public @ResponseBody HashMap<String,ArrayList<HashMap<String, String>>> getNotaCreditoJson(
            @RequestParam(value="id_nota_credito", required=true) Integer id_nota_credito,
            @RequestParam(value="iu", required=true) String id_user,
            Model model
            ) {
        
        log.log(Level.INFO, "Ejecutando getNotaCreditoJson de {0}", ProvNotasCreditoController.class.getName());
        HashMap<String,ArrayList<HashMap<String, String>>> jsonretorno = new HashMap<String,ArrayList<HashMap<String, String>>>();
        ArrayList<HashMap<String, String>> datosNota = new ArrayList<HashMap<String, String>>();
        ArrayList<HashMap<String, String>> valorIva = new ArrayList<HashMap<String, String>>();
        ArrayList<HashMap<String, String>> monedas = new ArrayList<HashMap<String, String>>();
        ArrayList<HashMap<String, String>> tipoCambioActual = new ArrayList<HashMap<String, String>>();
        HashMap<String, String> tc = new HashMap<String, String>();
        
        HashMap<String, String> userDat = new HashMap<String, String>();
        
        //decodificar id de usuario
        Integer id_usuario = Integer.parseInt(Base64Coder.decodeString(id_user));
        
        userDat = this.getHomeDao().getUserById(id_usuario);
        
        Integer id_empresa = Integer.parseInt(userDat.get("empresa_id"));
        Integer id_sucursal = Integer.parseInt(userDat.get("sucursal_id"));
        
        if( id_nota_credito != 0  ){
            datosNota = this.getCxpDao().getProvNotasCredito_Datos(id_nota_credito);
        }
        
        valorIva= this.getCxpDao().getValoriva(id_sucursal);
        tipoCambioActual= this.getCxpDao().getTipoCambioActual();
        monedas = this.getCxpDao().getMonedas();
        
        jsonretorno.put("datosNota", datosNota);
        jsonretorno.put("iva", valorIva);
        jsonretorno.put("Monedas", monedas);
        jsonretorno.put("Tc", tipoCambioActual);
        
        return jsonretorno;
    }
    
    
    
    
    
    
    
    
    
    //edicion y nuevo
    @RequestMapping(method = RequestMethod.POST, value="/edit.json")
    public @ResponseBody HashMap<String, String> editJson(
            @RequestParam(value="identificador", required=true) Integer id_nota_credito,
            @RequestParam(value="folio_nota_credito", required=true) String folio_nota_credito,
            @RequestParam(value="fecha_expedicion", required=true) String fecha_expedicion,
            @RequestParam(value="id_proveedor", required=true) String id_proveedor,
            @RequestParam(value="id_impuesto", required=true) String id_impuesto,
            @RequestParam(value="valorimpuesto", required=true) String valor_impuesto,
            @RequestParam(value="observaciones", required=true) String observaciones,
            @RequestParam(value="select_moneda", required=true) String select_moneda,
            @RequestParam(value="concepto", required=true) String concepto,
            @RequestParam(value="tipo_cambio", required=true) String tipo_cambio,
            @RequestParam(value="importe", required=true) String importe,
            @RequestParam(value="impuesto", required=true) String impuesto,
            @RequestParam(value="total", required=true) String total,
            @RequestParam(value="factura", required=true) String factura,
            @RequestParam(value="generar", required=true) String generar,
            @RequestParam(value="select_tipo_nota", required=true) String select_tipo_nota,
            @RequestParam(value="fac_saldado", required=true) String fac_saldado,
            @ModelAttribute("user") UserSessionData user
            ) {
            
            System.out.println("Guardar del Nota de Credito Proveedor");
            HashMap<String, String> jsonretorno = new HashMap<String, String>();
            HashMap<String, String> succes = new HashMap<String, String>();
            String tipo_facturacion="";
            
            HashMap<String, String> userDat = new HashMap<String, String>();
            HashMap<String,String> datosEmpresa = new HashMap<String,String>();
            HashMap<String,String> dataProveedor = new HashMap<String,String>();
            HashMap<String, String> datosNota= new HashMap<String, String>();
            
            
            Integer app_selected = 101;//aplicativo notas de credito proveedores
            String command_selected = "new";
            Integer id_usuario= user.getUserId();//variable para el id  del usuario
            String extra_data_array = "'sin datos'";
            String actualizo = "0";
            String serieFolio="";
            String rfcEmisor="";
            userDat = this.getHomeDao().getUserById(id_usuario);
            Integer id_empresa = Integer.parseInt(userDat.get("empresa_id"));
            Integer id_sucursal = Integer.parseInt(userDat.get("sucursal_id"));
            
            if( id_nota_credito==0 ){
                command_selected = "new";
            }else{
                command_selected = "edit";
            }
            
            
            String data_string = 
                    app_selected+"___"+
                    command_selected+"___"+
                    id_usuario+"___"+
                    id_nota_credito+"___"+
                    id_proveedor+"___"+
                    id_impuesto+"___"+
                    valor_impuesto+"___"+
                    observaciones.toUpperCase()+"___"+
                    select_moneda+"___"+
                    concepto.toUpperCase()+"___"+
                    tipo_cambio+"___"+
                    importe+"___"+
                    impuesto+"___"+
                    total+"___"+
                    factura+"___"+
                    fac_saldado+"___"+
                    folio_nota_credito.toUpperCase()+"___"+
                    fecha_expedicion+"___"+
                    select_tipo_nota;
            
            System.out.println("data_string: "+data_string);
            
            succes = this.getCxpDao().selectFunctionValidateAaplicativo(data_string,app_selected,extra_data_array);
            
            log.log(Level.INFO, "despues de validacion {0}", String.valueOf(succes.get("success")));
            
            if( String.valueOf(succes.get("success")).equals("true") ){
                actualizo = this.getCxpDao().selectFunctionForCxpAdmProcesos(data_string, extra_data_array);
                jsonretorno.put("actualizo",String.valueOf(actualizo));
            }
            
            System.out.println("Actualizo::: "+actualizo);
            
            jsonretorno.put("success",String.valueOf(succes.get("success")));
            
            log.log(Level.INFO, "Salida json {0}", String.valueOf(jsonretorno.get("success")));
        return jsonretorno;
    }
    

    
    
    
    
    
    
    //cancelacion de Notas de Credito
    @RequestMapping(method = RequestMethod.POST, value="/cancelarNotaCredito.json")
    public @ResponseBody HashMap<String, String> cancelarNotaCreditoJson(
            @RequestParam(value="id_nota", required=true) Integer id_nota,
            @RequestParam(value="motivo", required=true) String motivo_cancelacion,
            @RequestParam(value="iu", required=true) String id_user,
            Model model
            ) {
        
        HashMap<String, String> jsonretorno = new HashMap<String, String>();
        Integer id_usuario=0;//aqui va el id del usuario
        
        //decodificar id de usuario
        id_usuario = Integer.parseInt(Base64Coder.decodeString(id_user));
        
        //aplicativo notas de credito Proveedores
        Integer app_selected = 101;
        String command_selected = "cancelacion";
        String extra_data_array = "'sin datos'";
        String succcess = "false";
        String serie_folio="";
        
        String data_string = app_selected+"___"+command_selected+"___"+id_usuario+"___"+id_nota+"___"+motivo_cancelacion.toUpperCase();
        
        succcess = this.getCxpDao().selectFunctionForCxpAdmProcesos(data_string, extra_data_array);
        
        jsonretorno.put("success", succcess);
        //System.out.println("Success:: "+ succcess);
        return jsonretorno;
    }
    
    
    
    
    
    
    @RequestMapping(value = "/getPdfNotaCreditoProveedor/{id_nota}/{iu}/out.json", method = RequestMethod.GET ) 
    public ModelAndView get_genera_pdf_invOrdenEntradaJson(
                @PathVariable("id_nota") Integer id_nota,
                @PathVariable("iu") String id_user,
                HttpServletRequest request, 
                HttpServletResponse response, 
                Model model)
            throws ServletException, IOException, URISyntaxException, DocumentException, Exception {
        
        HashMap<String, String> userDat = new HashMap<String, String>();
        HashMap<String, String> datosNota = new HashMap<String, String>();
        HashMap<String, String> datos_empresa = new HashMap<String, String>();
        Integer app_selected = 101; //Notas de Credito Proveedores
        
        System.out.println("Generando PDF NC");
        
        //decodificar id de usuario
        Integer id_usuario = Integer.parseInt(Base64Coder.decodeString(id_user));
        userDat = this.getHomeDao().getUserById(id_usuario);
        Integer id_empresa = Integer.parseInt(userDat.get("empresa_id"));
        
        String razon_social_empresa = this.getGralDao().getRazonSocialEmpresaEmisora(id_empresa);
        String rfc_empresa = this.getGralDao().getRfcEmpresaEmisora(id_empresa);
        
        //obtener el directorio temporal
        String dir_tmp = this.getGralDao().getTmpDir();
        String ruta_imagen = this.getGralDao().getImagesDir()+rfc_empresa+"_logo.png";
        File file_dir_tmp = new File(dir_tmp);
        String file_name = "NCPROV_"+rfc_empresa+".pdf";
        
        //ruta de archivo de salida
        String fileout = file_dir_tmp +"/"+  file_name;
        
        datos_empresa.put("emp_razon_social", razon_social_empresa);
        datos_empresa.put("emp_rfc", this.getGralDao().getRfcEmpresaEmisora(id_empresa));
        datos_empresa.put("emp_calle", this.getGralDao().getCalleDomicilioFiscalEmpresaEmisora(id_empresa));
        datos_empresa.put("emp_no_exterior", this.getGralDao().getNoExteriorDomicilioFiscalEmpresaEmisora(id_empresa));
        datos_empresa.put("emp_colonia", this.getGralDao().getColoniaDomicilioFiscalEmpresaEmisora(id_empresa));
        datos_empresa.put("emp_pais", this.getGralDao().getPaisDomicilioFiscalEmpresaEmisora(id_empresa));
        datos_empresa.put("emp_estado", this.getGralDao().getEstadoDomicilioFiscalEmpresaEmisora(id_empresa));
        datos_empresa.put("emp_municipio", this.getGralDao().getMunicipioDomicilioFiscalEmpresaEmisora(id_empresa));
        datos_empresa.put("emp_cp", this.getGralDao().getCpDomicilioFiscalEmpresaEmisora(id_empresa));
        
        datosNota = this.getCxpDao().getProvNotasCredito_DatosPDF(id_nota);
        //datosNota.put("codigo1", this.getGralDao().getCodigo1Iso(id_empresa, app_selected));
        //datosNota.put("codigo2", this.getGralDao().getCodigo2Iso(id_empresa, app_selected));
        datosNota.put("codigo1", "");
        datosNota.put("codigo2", "");
        
        //instancia a la clase, aqui se le pasa los parametros al constructor
        PdfNotaCreditoProveedor nota = new PdfNotaCreditoProveedor(datos_empresa, datosNota, fileout, ruta_imagen);
        
        //metodo que construye el pdf
        nota.ViewPDF();
        
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
