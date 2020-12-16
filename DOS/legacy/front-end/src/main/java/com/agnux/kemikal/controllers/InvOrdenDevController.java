/*
 * Controler para Aplicativo Ordenes de Devolucion de Mercancia Facturada a clientes
 */
package com.agnux.kemikal.controllers;

import com.agnux.cfd.v2.Base64Coder;
import com.agnux.common.helpers.StringHelper;
import com.agnux.common.obj.DataPost;
import com.agnux.common.obj.ResourceProject;
import com.agnux.common.obj.UserSessionData;
import com.agnux.kemikal.interfacedaos.GralInterfaceDao;
import com.agnux.kemikal.interfacedaos.HomeInterfaceDao;
import com.agnux.kemikal.interfacedaos.InvInterfaceDao;
import com.agnux.kemikal.reportes.PdfOrdenDevolucion;
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
@RequestMapping("/invordendev/")
public class InvOrdenDevController {
    private static final Logger log  = Logger.getLogger(InvOrdenDevController.class.getName());
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
        
        log.log(Level.INFO, "Ejecutando starUp de {0}", InvOrdenDevController.class.getName());
        LinkedHashMap<String,String> infoConstruccionTabla = new LinkedHashMap<String,String>();
        
        //infoConstruccionTabla.put("id", "Acciones:90");
        infoConstruccionTabla.put("id", "Acciones:70");
        infoConstruccionTabla.put("folio", "Folio:90");
        infoConstruccionTabla.put("fecha_dev","Fecha Devoluci&oacute;n:120");
        infoConstruccionTabla.put("cliente", "Cliente:300");
        infoConstruccionTabla.put("factura", "Factura:110");
        infoConstruccionTabla.put("fecha_fac", "Fecha Factura:120");
        
        ModelAndView x = new ModelAndView("invordendev/startup", "title", "Ordenes de Devoluci&oacute;n");
        
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
    @RequestMapping(value="/getAllOrdenesDev.json", method = RequestMethod.POST)
    public @ResponseBody HashMap<String,ArrayList<HashMap<String, Object>>> getAllOrdenesDevJson(
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
        
        //Aplicativo Ordenes de Devolucion
        Integer app_selected = 100;
        
        //Decodificar id de usuario
        Integer id_usuario = Integer.parseInt(Base64Coder.decodeString(id_user_cod));
        
        //Variables para el buscador
        String folio = "%"+StringHelper.isNullString(String.valueOf(has_busqueda.get("folio")))+"%";
        String factura = "%"+StringHelper.isNullString(String.valueOf(has_busqueda.get("factura")))+"%";
        String cliente = "%"+StringHelper.isNullString(String.valueOf(has_busqueda.get("cliente")))+"%";
        String fecha_inicial = ""+StringHelper.isNullString(String.valueOf(has_busqueda.get("fecha_inicial")))+"";
        String fecha_final = ""+StringHelper.isNullString(String.valueOf(has_busqueda.get("fecha_final")))+"";
        
        String data_string = app_selected+"___"+id_usuario+"___"+folio+"___"+factura+"___"+cliente+"___"+fecha_inicial+"___"+fecha_final;
        
        //Obtiene total de registros en base de datos, con los parametros de busqueda
        int total_items = this.getInvDao().countAll(data_string);
        
        //Calcula el total de paginas
        int total_pags = resource.calculaTotalPag(total_items,items_por_pag);
        
        //Variables que necesita el datagrid, para no tener que hacer uno por cada aplicativo
        DataPost dataforpos = new DataPost(orderby, desc, items_por_pag, pag_start, display_pag, input_json, cadena_busqueda,total_items,total_pags,id_user_cod);
        
        int offset = resource.__get_inicio_offset(items_por_pag, pag_start);
        
        //Obtiene los registros para el grid, de acuerdo a los parametros de busqueda
        jsonretorno.put("Data", this.getInvDao().getInvOrdenDev_PaginaGrid(data_string, offset, items_por_pag, orderby, desc));
        
        //Obtiene el hash para los datos que necesita el datagrid
        jsonretorno.put("DataForGrid", dataforpos.formaHashForPos(dataforpos));
        
        return jsonretorno;
    }
    
    
    
    
    
    
    
    @RequestMapping(method = RequestMethod.POST, value="/getOrdenDev.json")
    public @ResponseBody HashMap<String,ArrayList<HashMap<String, String>>> getOrdenDevJson(
            @RequestParam(value="identificador", required=true) Integer id,
            @RequestParam(value="iu", required=true) String id_user,
            Model model
            ) {
        
        log.log(Level.INFO, "Ejecutando getOrdenDevJson de {0}", InvOrdenDevController.class.getName());
        HashMap<String,ArrayList<HashMap<String, String>>> jsonretorno = new HashMap<String,ArrayList<HashMap<String, String>>>();
        
        ArrayList<HashMap<String, String>> monedas = new ArrayList<HashMap<String, String>>();
        ArrayList<HashMap<String, String>> datos = new ArrayList<HashMap<String, String>>();
        ArrayList<HashMap<String, String>> Grid = new ArrayList<HashMap<String, String>>();
        ArrayList<HashMap<String, String>> GridLotes = new ArrayList<HashMap<String, String>>();
        ArrayList<HashMap<String, String>> almacenes = new ArrayList<HashMap<String, String>>();
        ArrayList<HashMap<String, String>> tiposMovimiento = new ArrayList<HashMap<String, String>>();
        HashMap<String, String> userDat = new HashMap<String, String>();
        
        //decodificar id de usuario
        Integer id_usuario = Integer.parseInt(Base64Coder.decodeString(id_user));
        userDat = this.getHomeDao().getUserById(id_usuario);
        
        Integer id_empresa = Integer.parseInt(userDat.get("empresa_id"));
        Integer id_sucursal = Integer.parseInt(userDat.get("sucursal_id"));
        
        if( id != 0 ){
            datos = this.getInvDao().getInvOrdenDev_Datos(id);
            
            Integer tipoDoc = Integer.parseInt(datos.get(0).get("tipo_doc"));
            String folioNcto = datos.get(0).get("folio_ncto");
            Integer clienteId = Integer.parseInt(datos.get(0).get("clie_id"));
            String folioDoc = datos.get(0).get("folio_doc");
            
            if(tipoDoc==1 && !folioNcto.equals("")){
                Grid = this.getInvDao().getInvOrdenDev_DatosGridNcto(folioNcto, clienteId);
            }else{
                Grid = this.getInvDao().getInvOrdenDev_DatosGridOsal(tipoDoc, folioDoc, clienteId);
            }
            GridLotes = this.getInvDao().getInvOrdenDev_DatosGridLotes(id);
        }
        
        monedas = this.getInvDao().getMonedas();
        almacenes = this.getInvDao().getAlmacenes(id_empresa);
        tiposMovimiento = this.getInvDao().getAllTiposMovimientoInventario(id_empresa);
        
        jsonretorno.put("Datos", datos);
        jsonretorno.put("datosGrid", Grid);
        jsonretorno.put("Lotes", GridLotes);
        jsonretorno.put("Monedas", monedas);
        jsonretorno.put("Almacenes", almacenes);
        jsonretorno.put("TMovInv", tiposMovimiento);
        
        return jsonretorno;
    }
    
    
    
    
    
    //edicion y nuevo
    @RequestMapping(method = RequestMethod.POST, value="/edit.json")
    public @ResponseBody HashMap<String, String> editJson(
            @RequestParam(value="identificador", required=true) Integer identificador,
            @RequestParam(value="observaciones", required=true) String observaciones,
            @RequestParam(value="accion", required=true)        String accion,
            @RequestParam(value="eliminado", required=true)     String[] eliminado,
            @RequestParam(value="tipo", required=true)          String[] tipo_registro,
            @RequestParam(value="id_detalle", required=true)    String[] id_detalle,
            @RequestParam(value="id_alm", required=true)        String[] id_almacen,
            @RequestParam(value="id_prod_grid", required=true)  String[] id_prod_grid,
            @RequestParam(value="lote_int", required=true)      String[] lote_interno,
            @RequestParam(value="lote_id", required=true)       String[] lote_id,
            @RequestParam(value="cant_dev", required=true)      String[] cant_dev,
            @RequestParam(value="no_tr", required=true)         String[] no_tr,
            @RequestParam(value="id_partida", required=true)         String[] id_partida,
            @ModelAttribute("user") UserSessionData user,
            Model model
        ) {
            
            HashMap<String, String> jsonretorno = new HashMap<String, String>();
            HashMap<String, String> succes = new HashMap<String, String>();
            String extra_data_array = null;
            String arreglo[];
            arreglo = new String[tipo_registro.length];
            String actualizar = "0";
            String actualizo="0";
            
            
            //aplicativo Ordenes de Devolucion
            Integer app_selected = 100;
            String command_selected = "new";
            Integer id_usuario= user.getUserId();//variable para el id  del usuario
            
            for(int i=0; i<tipo_registro.length; i++) {
                
                arreglo[i]= "'"+eliminado[i]+"___"+tipo_registro[i]+"___"+id_detalle[i]+"___"+id_almacen[i]+"___"+id_prod_grid[i]+"___"+lote_id[i]+"___"+lote_interno[i]+"___"+StringHelper.removerComas(cant_dev[i])+"___"+no_tr[i]+"___"+id_partida[i]+"'";
                        
                //System.out.println(arreglo[i]);
            }
            
            //serializar el arreglo
            extra_data_array = StringUtils.join(arreglo, ",");
            
            command_selected = accion;
            
            //la accion es para confirmar
            String data_string = app_selected+"___"+command_selected+"___"+id_usuario+"___"+identificador+"___"+observaciones.toUpperCase()+"___"+accion;
            System.out.println("data_string: "+data_string);
            
            //aqui entra cuando la accion es Edit o Confirmar
            succes = this.getInvDao().selectFunctionValidateAaplicativo(data_string,app_selected,extra_data_array);
            actualizar = String.valueOf(succes.get("success"));
            
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

    
    
    
    @RequestMapping(value = "/getPdf/{identificador}/{iu}/out.json", method = RequestMethod.GET ) 
    public ModelAndView getPdfJson(
                @PathVariable("identificador") Integer identificador,
                @PathVariable("iu") String id_user,
                HttpServletRequest request, 
                HttpServletResponse response, 
                Model model)
            throws ServletException, IOException, URISyntaxException, DocumentException {
        
        HashMap<String, String> userDat = new HashMap<String, String>();
        HashMap<String, String> datos_empresa = new HashMap<String, String>();
        HashMap<String, String> datos_cliente = new HashMap<String, String>();
        
        ArrayList<HashMap<String, String>> datos = new ArrayList<HashMap<String, String>>();
        ArrayList<HashMap<String, String>> Grid = new ArrayList<HashMap<String, String>>();
        ArrayList<HashMap<String, String>> GridLotes = new ArrayList<HashMap<String, String>>();
        
        System.out.println("Generando PDF de Orden de Devolucion");
        
        //decodificar id de usuario
        Integer id_usuario = Integer.parseInt(Base64Coder.decodeString(id_user));
        
        userDat = this.getHomeDao().getUserById(id_usuario);
        Integer id_empresa = Integer.parseInt(userDat.get("empresa_id"));
        
        
        String razon_social_empresa = this.getGralDao().getRazonSocialEmpresaEmisora(id_empresa);
        
        //obtener el directorio temporal
        String dir_tmp = this.getGralDao().getTmpDir();
        
        
        String[] array_company = razon_social_empresa.split(" ");
        String company_name= array_company[0].toLowerCase();
        //String ruta_imagen = this.getGralDao().getImagesDir() +"logo_"+ company_name +".png";
        
        String ruta_imagen = this.getGralDao().getImagesDir()+this.getGralDao().getRfcEmpresaEmisora(id_empresa)+"_logo.png";
        
        File file_dir_tmp = new File(dir_tmp);
        System.out.println("Directorio temporal: "+file_dir_tmp.getCanonicalPath());
        
        
        String file_name = "Orden de Salida_"+company_name+".pdf";
        //ruta de archivo de salida
        String fileout = file_dir_tmp +"/"+  file_name;
        //aplicativo Orden de Devolucion
        Integer app_selected = 100;
        
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
        
        
        if(identificador>0){
            datos = this.getInvDao().getInvOrdenDev_Datos(identificador);
            
            Integer tipoDoc = Integer.parseInt(datos.get(0).get("tipo_doc"));
            String folioNcto = datos.get(0).get("folio_ncto");
            Integer clienteId = Integer.parseInt(datos.get(0).get("clie_id"));
            String folioDoc = datos.get(0).get("folio_doc");
            
            if(tipoDoc==1 && !folioNcto.equals("")){
                Grid = this.getInvDao().getInvOrdenDev_DatosGridNcto(folioNcto, clienteId);
            }else{
                Grid = this.getInvDao().getInvOrdenDev_DatosGridOsal(tipoDoc, folioDoc, clienteId);
            }
            GridLotes = this.getInvDao().getInvOrdenDev_DatosGridLotes(identificador);
            
            datos_cliente.put("clie_razon_social", datos.get(0).get("cliente"));
            datos_cliente.put("clie_no_control", datos.get(0).get("no_clie"));
            datos_cliente.put("clie_rfc", datos.get(0).get("rfc"));
            
            PdfOrdenDevolucion odev = new PdfOrdenDevolucion(datos_empresa, datos, datos_cliente,Grid,GridLotes, fileout, ruta_imagen);
            
        }
        
        
        
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
        
        return null;        
    }

    
    
    
    
}
