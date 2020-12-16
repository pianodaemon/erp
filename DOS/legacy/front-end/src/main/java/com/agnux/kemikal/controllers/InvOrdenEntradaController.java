package com.agnux.kemikal.controllers;

import com.agnux.cfd.v2.Base64Coder;
import com.agnux.common.helpers.FileHelper;
import com.agnux.common.helpers.StringHelper;
import com.agnux.common.obj.DataPost;
import com.agnux.common.obj.ResourceProject;
import com.agnux.common.obj.UserSessionData;
import com.agnux.kemikal.interfacedaos.GralInterfaceDao;
import com.agnux.kemikal.interfacedaos.HomeInterfaceDao;
import com.agnux.kemikal.interfacedaos.InvInterfaceDao;
import com.agnux.kemikal.reportes.PdfOrdenEntrada;
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
@RequestMapping("/invordenentrada/")
public class InvOrdenEntradaController {
    private static final Logger log  = Logger.getLogger(InvOrdenEntradaController.class.getName());
    ResourceProject resource = new ResourceProject();
    
    
    @Autowired
    @Qualifier("daoInv")
    private InvInterfaceDao invDao;
    
    
    public InvInterfaceDao getInvDao() {
        return invDao;
    }
    
    @Autowired
    @Qualifier("daoHome")
    private HomeInterfaceDao HomeDao;
    
    public HomeInterfaceDao getHomeDao() {
        return HomeDao;
    }
    
    @Autowired
    @Qualifier("daoGral")
    private GralInterfaceDao gralDao;
    
    public GralInterfaceDao getGralDao() {
        return gralDao;
    }
    
    @RequestMapping(value="/startup.agnux")
    public ModelAndView startUp(HttpServletRequest request, HttpServletResponse response, 
            @ModelAttribute("user") UserSessionData user)
            throws ServletException, IOException {
        
        log.log(Level.INFO, "Ejecutando starUp de {0}", InvOrdenEntradaController.class.getName());
        LinkedHashMap<String,String> infoConstruccionTabla = new LinkedHashMap<String,String>();
        
        //infoConstruccionTabla.put("id", "Acciones:90");
        infoConstruccionTabla.put("id", "Acciones:70");
        infoConstruccionTabla.put("folio", "Folio:90");
        infoConstruccionTabla.put("fecha_entrada","Fecha Entrada:90");
        infoConstruccionTabla.put("origen", "Origen:300");
        infoConstruccionTabla.put("orden_compra", "Orden Compra:110");
        infoConstruccionTabla.put("folio_doc", "Folio Doc.:110");
        infoConstruccionTabla.put("fecha_doc","Fecha Doc.:110");
        infoConstruccionTabla.put("estado","Estado:90");
        
        ModelAndView x = new ModelAndView("invordenentrada/startup", "title", "Ordenes de Entrada");
        
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
    
    
    
    
    //Metodo para el grid y el Paginado
    @RequestMapping(value="/getAllOrdenesEntrada.json", method = RequestMethod.POST)
    public @ResponseBody HashMap<String,ArrayList<HashMap<String, Object>>> getAllOrdenesEntradaJson(
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
        
        //aplicativo Ordenes de Entrada
        Integer app_selected = 87;
        
        //decodificar id de usuario
        Integer id_usuario = Integer.parseInt(Base64Coder.decodeString(id_user_cod));
        
        //variables para el buscador
        String folio = "%"+StringHelper.isNullString(String.valueOf(has_busqueda.get("folio")))+"%";
        String orden_compra = "%"+StringHelper.isNullString(String.valueOf(has_busqueda.get("orden_compra")))+"%";
        String factura = "%"+StringHelper.isNullString(String.valueOf(has_busqueda.get("factura")))+"%";
        String proveedor = "%"+StringHelper.isNullString(String.valueOf(has_busqueda.get("proveedor")))+"%";
        String tipo_doc = StringHelper.isNullString(String.valueOf(has_busqueda.get("tipo_doc")));
        String codigo = "%"+StringHelper.isNullString(String.valueOf(has_busqueda.get("codigo")))+"%";
        
        String data_string = app_selected+"___"+id_usuario+"___"+folio+"___"+orden_compra+"___"+factura+"___"+proveedor+"___"+ tipo_doc +"___"+ codigo;
        
        //obtiene total de registros en base de datos, con los parametros de busqueda
        int total_items = this.getInvDao().countAll(data_string);
        
        //calcula el total de paginas
        int total_pags = resource.calculaTotalPag(total_items,items_por_pag);
        
        //variables que necesita el datagrid, para no tener que hacer uno por cada aplicativo
        DataPost dataforpos = new DataPost(orderby, desc, items_por_pag, pag_start, display_pag, input_json, cadena_busqueda,total_items,total_pags,id_user_cod);
        
        int offset = resource.__get_inicio_offset(items_por_pag, pag_start);
        
        //obtiene los registros para el grid, de acuerdo a los parametros de busqueda
        jsonretorno.put("Data", this.getInvDao().getInvOrdenEntrada_PaginaGrid(data_string, offset, items_por_pag, orderby, desc));
        
        //obtiene el hash para los datos que necesita el datagrid
        jsonretorno.put("DataForGrid", dataforpos.formaHashForPos(dataforpos));
        
        return jsonretorno;
    }
    
    
    
    
    
    @RequestMapping(method = RequestMethod.POST, value="/getOrdenEntrada.json")
    public @ResponseBody HashMap<String,ArrayList<HashMap<String, String>>> getOrdenEntradaJson(
            @RequestParam(value="identificador", required=true) Integer id,
            @RequestParam(value="iu", required=true) String id_user,
            Model model
            ) {
        
        log.log(Level.INFO, "Ejecutando getOrdenEntradaJson de {0}", InvOrdenEntradaController.class.getName());
        HashMap<String,ArrayList<HashMap<String, String>>> jsonretorno = new HashMap<String,ArrayList<HashMap<String, String>>>();
        
        ArrayList<HashMap<String, String>> monedas = new ArrayList<HashMap<String, String>>();
        ArrayList<HashMap<String, String>> impuestos = new ArrayList<HashMap<String, String>>();
        ArrayList<HashMap<String, String>> datosOrdenEntrada = new ArrayList<HashMap<String, String>>();
        ArrayList<HashMap<String, String>> datosGrid = new ArrayList<HashMap<String, String>>();
        ArrayList<HashMap<String, String>> datosGridLotes = new ArrayList<HashMap<String, String>>();
        ArrayList<HashMap<String, String>> almacenes = new ArrayList<HashMap<String, String>>();
        ArrayList<HashMap<String, String>> tiposMovimiento = new ArrayList<HashMap<String, String>>();
        HashMap<String, String> userDat = new HashMap<String, String>();
        
        //decodificar id de usuario
        Integer id_usuario = Integer.parseInt(Base64Coder.decodeString(id_user));
        userDat = this.getHomeDao().getUserById(id_usuario);
        
        Integer id_empresa = Integer.parseInt(userDat.get("empresa_id"));
        Integer id_sucursal = Integer.parseInt(userDat.get("sucursal_id"));
        
        if( id != 0 ){
            datosOrdenEntrada = this.getInvDao().getInvOrdenEntrada_Datos(id);
            datosGrid = this.getInvDao().getInvOrdenEntrada_DatosGrid(id);
            datosGridLotes = this.getInvDao().getInvOrdenEntrada_DatosGridLotes(id);
        }
        
        monedas = this.getInvDao().getMonedas();
        impuestos = this.getInvDao().getEntradas_Impuestos();
        almacenes = this.getInvDao().getAlmacenes(id_empresa);
        tiposMovimiento = this.getInvDao().getAllTiposMovimientoInventario(id_empresa);
        
        jsonretorno.put("Datos", datosOrdenEntrada);
        jsonretorno.put("datosGrid", datosGrid);
        jsonretorno.put("Lotes", datosGridLotes);
        jsonretorno.put("Monedas", monedas);
        jsonretorno.put("Impuestos", impuestos);
        jsonretorno.put("Almacenes", almacenes);
        jsonretorno.put("TMovInv", tiposMovimiento);
        
        return jsonretorno;
    }
    
    
    
    
    
    
    //edicion y nuevo
    @RequestMapping(method = RequestMethod.POST, value="/edit.json")
    public @ResponseBody HashMap<String, String> editJson(
            @RequestParam(value="identificador", required=true) Integer id_oent,
            @RequestParam(value="estatus", required=true) String estatus,
            @RequestParam(value="observaciones", required=true) String observaciones,
            @RequestParam(value="accion", required=true) String accion,
            @RequestParam(value="eliminado", required=true) String[] eliminado,
            @RequestParam(value="tipo", required=true)      String[] tipo_registro,
            @RequestParam(value="id_detalle", required=true) String[] id_detalle,
            @RequestParam(value="oent_detalle_id", required=true) String[] oent_detalle_id,
            @RequestParam(value="id_alm", required=true) String[] id_almacen,
            @RequestParam(value="id_prod_grid", required=true) String[] id_prod_grid,
            @RequestParam(value="req_lote", required=true) String[] req_lote,
            @RequestParam(value="cantidad", required=true) String[] cantidad_fac,
            @RequestParam(value="cant_rec", required=true) String[] cantidad_rec,
            @RequestParam(value="lote_int", required=true) String[] lote_int,
            @RequestParam(value="lote_prov", required=true) String[] lote_prov,
            @RequestParam(value="pedimento", required=true) String[] pedimento,
            @RequestParam(value="caducidad", required=true) String[] caducidad,
            @RequestParam(value="no_tr", required=true) String[] no_tr,
            @ModelAttribute("user") UserSessionData user,
            Model model
            ) {
            
            HashMap<String, String> jsonretorno = new HashMap<String, String>();
            HashMap<String, String> succes = new HashMap<String, String>();
            String extra_data_array = null;
            String arreglo[];
            arreglo = new String[eliminado.length];
            String actualizar = "0";
            String actualizo="0";
            
            
            Integer app_selected = 87;
            String command_selected = "new";
            Integer id_usuario= user.getUserId();//variable para el id  del usuario
            
            for(int i=0; i<eliminado.length; i++) {
                /*
                if(tipo_registro[i].equals("LOT")){
                    if(id_detalle[i].equals("0")){
                        lote_interno = StringHelper.generaUUID();
                    }
                }
                */
                arreglo[i]= "'"+
                        eliminado[i]+"___"+
                        tipo_registro[i]+"___"+
                        id_detalle[i]+"___"+
                        id_almacen[i]+"___"+
                        id_prod_grid[i]+"___"+
                        cantidad_fac[i]+"___"+
                        cantidad_rec[i]+"___"+
                        lote_prov[i]+"___"+
                        pedimento[i].toUpperCase()+"___"+
                        caducidad[i]+"___"+
                        no_tr[i]+"___"+
                        req_lote[i]+"___"+
                        oent_detalle_id[i]+"___"+
                        lote_int+"'";
                        
                //System.out.println(arreglo[i]);
            }
            //serializar el arreglo
            extra_data_array = StringUtils.join(arreglo, ",");
            
            
            if( id_oent==0 ){
                command_selected = "new";
            }else{
                //la accion puede ser edit o cancelar
                command_selected = accion;
            }
            
            String data_string = app_selected+"___"+command_selected+"___"+id_usuario+"___"+id_oent+"___"+observaciones.toUpperCase();
            //System.out.println("data_string: "+data_string);
            
            if (!accion.equals("cancelar")){
                succes = this.getInvDao().selectFunctionValidateAaplicativo(data_string,app_selected,extra_data_array);
                actualizar = String.valueOf(succes.get("success"));
            }else{
                //cuando la accion es  cancelar ya no se valida, solo se actualiza para permitir la cancelacion
                actualizar="true";
            }
            
            
            log.log(Level.INFO, "despues de validacion {0}", actualizar);
            
            if( actualizar.equals("true") ){
                actualizo = this.getInvDao().selectFunctionForApp_MovimientosInventario(data_string, extra_data_array);
            }
            
            //jsonretorno.put("success",String.valueOf(succes.get("success")));
            jsonretorno.put("success",String.valueOf(actualizar));
            
            //log.log(Level.INFO, "Salida json {0}", String.valueOf(jsonretorno.get("success")));
            log.log(Level.INFO, "Salida json {0}", actualizar);
        return jsonretorno;
    }
    
    
    
    
    
    
@RequestMapping(value = "/get_genera_pdf_invOrdenEntrada/{id_invOrdenEntrada}/{iu}/out.json", method = RequestMethod.GET ) 
    public ModelAndView get_genera_pdf_invOrdenEntradaJson(
                @PathVariable("id_invOrdenEntrada") Integer id_OrdenEntrada,
                @PathVariable("iu") String id_user,
                HttpServletRequest request, 
                HttpServletResponse response, 
                Model model)
            throws ServletException, IOException, URISyntaxException, DocumentException, Exception {
        
        HashMap<String, String> userDat = new HashMap<String, String>();
        ArrayList<HashMap<String, String>> datosProveedor = new ArrayList<HashMap<String, String>>();
        ArrayList<HashMap<String, String>> proveedorContactos = new ArrayList<HashMap<String, String>>();
        HashMap<String, String> datos_empresa = new HashMap<String, String>();
        HashMap<String, String> datos_entrada = new HashMap<String, String>();
        HashMap<String, String> datos_proveedor = new HashMap<String, String>();
        ArrayList<HashMap<String, String>> lista_productos = new ArrayList<HashMap<String, String>>();
        ArrayList<HashMap<String, String>> LotesGrid = new ArrayList<HashMap<String, String>>();
        
        System.out.println("Generando PDF de Orden de Entrada");
        
        //decodificar id de usuario
        Integer id_usuario = Integer.parseInt(Base64Coder.decodeString(id_user));
        
        userDat = this.getHomeDao().getUserById(id_usuario);
        Integer id_empresa = Integer.parseInt(userDat.get("empresa_id"));
        
        
        String razon_social_empresa = this.getGralDao().getRazonSocialEmpresaEmisora(id_empresa);
        
        //obtener el directorio temporal
        String dir_tmp = this.getGralDao().getTmpDir();
        
        
        //String[] array_company = razon_social_empresa.split(" ");
        //String company_name= array_company[0].toLowerCase();
        //String ruta_imagen = this.getGralDao().getImagesDir() +"logo_"+ company_name +".png";
        String rfc_empresa = this.getGralDao().getRfcEmpresaEmisora(id_empresa);
        
        String ruta_imagen = this.getGralDao().getImagesDir()+rfc_empresa+"_logo.png";
        
        File file_dir_tmp = new File(dir_tmp);
        System.out.println("Directorio temporal: "+file_dir_tmp.getCanonicalPath());
        
        
        String file_name = "ORDEN_ENTRADA_"+rfc_empresa+".pdf";
        //ruta de archivo de salida
        String fileout = file_dir_tmp +"/"+  file_name;
        Integer app_selected = 87; //aplicativo Orden de Entrada
        
        datos_empresa.put("emp_razon_social", razon_social_empresa);
        datos_empresa.put("emp_rfc", this.getGralDao().getRfcEmpresaEmisora(id_empresa));
        datos_empresa.put("emp_calle", this.getGralDao().getCalleDomicilioFiscalEmpresaEmisora(id_empresa));
        datos_empresa.put("emp_no_exterior", this.getGralDao().getNoExteriorDomicilioFiscalEmpresaEmisora(id_empresa));
        datos_empresa.put("emp_colonia", this.getGralDao().getColoniaDomicilioFiscalEmpresaEmisora(id_empresa));
        datos_empresa.put("emp_pais", this.getGralDao().getPaisDomicilioFiscalEmpresaEmisora(id_empresa));
        datos_empresa.put("emp_estado", this.getGralDao().getEstadoDomicilioFiscalEmpresaEmisora(id_empresa));
        datos_empresa.put("emp_municipio", this.getGralDao().getMunicipioDomicilioFiscalEmpresaEmisora(id_empresa));
        datos_empresa.put("emp_cp", this.getGralDao().getCpDomicilioFiscalEmpresaEmisora(id_empresa));
        
        datos_empresa.put("codigo1", this.getGralDao().getCodigo1Iso(id_empresa, app_selected));
        datos_empresa.put("codigo2", this.getGralDao().getCodigo2Iso(id_empresa, app_selected));
        
        datos_entrada = this.getInvDao().getInvOrdenEntrada_Datos_PDF(id_OrdenEntrada);
        datosProveedor = this.getInvDao().getEntradas_DatosProveedor(Integer.parseInt(datos_entrada.get("proveedor_id")));
        proveedorContactos = this.getInvDao().getProveedor_Contacto(Integer.parseInt(datos_entrada.get("proveedor_id")));
        
        if(datosProveedor.size()<=0){
            datos_proveedor.put("prov_razon_social", "");
            datos_proveedor.put("prov_rfc", "");
            datos_proveedor.put("prov_calle", "");
            datos_proveedor.put("prov_numero", "");
            datos_proveedor.put("prov_colonia", "");
            datos_proveedor.put("prov_municipio", "");
            datos_proveedor.put("prov_estado", "");
            datos_proveedor.put("prov_pais", "");
            datos_proveedor.put("prov_cp", "");
            datos_proveedor.put("prov_telefono", "");
        }else{
            datos_proveedor.put("prov_razon_social", datosProveedor.get(0).get("razon_social"));
            datos_proveedor.put("prov_rfc", datosProveedor.get(0).get("rfc"));
            datos_proveedor.put("prov_calle", datosProveedor.get(0).get("calle"));
            datos_proveedor.put("prov_numero", datosProveedor.get(0).get("numero"));
            datos_proveedor.put("prov_colonia", datosProveedor.get(0).get("colonia"));
            datos_proveedor.put("prov_municipio", datosProveedor.get(0).get("municipio"));
            datos_proveedor.put("prov_estado", datosProveedor.get(0).get("estado"));
            datos_proveedor.put("prov_pais", datosProveedor.get(0).get("pais"));
            datos_proveedor.put("prov_cp", datosProveedor.get(0).get("cp"));
            datos_proveedor.put("prov_telefono", datosProveedor.get(0).get("telefono"));
        }
        
        if(proveedorContactos.size()<=0){
            datos_proveedor.put("prov_contacto", "");
        }else{
            datos_proveedor.put("prov_contacto", proveedorContactos.get(0).get("contacto"));
        }
        
        //Obtiene el listado de productos para el pdf
        lista_productos = this.getInvDao().getInvOrdenEntrada_DatosGrid(id_OrdenEntrada);
        LotesGrid = this.getInvDao().getInvOrdenEntrada_DatosGridLotes(id_OrdenEntrada);
        
        PdfOrdenEntrada OEntradas = new PdfOrdenEntrada(datos_empresa, datos_entrada, datos_proveedor,lista_productos,LotesGrid, fileout, ruta_imagen);
        
        
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
