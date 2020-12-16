package com.agnux.kemikal.controllers;

import com.agnux.cfd.v2.Base64Coder;
import com.agnux.common.helpers.FileHelper;
import com.agnux.common.helpers.StringHelper;
import com.agnux.common.helpers.TimeHelper;
import com.agnux.common.obj.DataPost;
import com.agnux.common.obj.ResourceProject;
import com.agnux.common.obj.UserSessionData;
import com.agnux.kemikal.interfacedaos.GralInterfaceDao;
import com.agnux.kemikal.interfacedaos.HomeInterfaceDao;
import com.agnux.kemikal.interfacedaos.InvInterfaceDao;
import com.agnux.kemikal.reportes.PdfTraspaso;
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
@RequestMapping("/invtraspasos/")
public class InvTraspasosController {
    private static final Logger log  = Logger.getLogger(InvTraspasosController.class.getName());
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
        
        log.log(Level.INFO, "Ejecutando starUp de {0}", InvTraspasosController.class.getName());
        LinkedHashMap<String,String> infoConstruccionTabla = new LinkedHashMap<String,String>();
        
        //infoConstruccionTabla.put("id", "Acciones:90");
        infoConstruccionTabla.put("id", "Acciones:70");
        infoConstruccionTabla.put("folio_traspaso", "Folio:90");
        infoConstruccionTabla.put("alm_origen","Almacen&nbsp;Origen:200");
        infoConstruccionTabla.put("alm_destino","Almacen&nbsp;Destino:200");
        infoConstruccionTabla.put("fecha_traspaso", "Fecha&nbsp;Traspaso:110");
        
        ModelAndView x = new ModelAndView("invtraspasos/startup", "title", "Traspasos");
        
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
    @RequestMapping(value="/getAllTraspasos.json", method = RequestMethod.POST)
    public @ResponseBody HashMap<String,ArrayList<HashMap<String, Object>>> getAllTraspasosJson(
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
        
        Integer app_selected = 10;//Traspasos
        
        //decodificar id de usuario
        Integer id_usuario = Integer.parseInt(Base64Coder.decodeString(id_user_cod));
        
        //variables para el buscador
        String folio = "%"+StringHelper.isNullString(String.valueOf(has_busqueda.get("folio")))+"%";
        String codigo = "%"+StringHelper.isNullString(String.valueOf(has_busqueda.get("codigo")))+"%";
        String descripcion = "%"+StringHelper.isNullString(String.valueOf(has_busqueda.get("descripcion")))+"%";
        String alm_origen = StringHelper.isNullString(String.valueOf(has_busqueda.get("alm_origen")));
        String alm_destino = StringHelper.isNullString(String.valueOf(has_busqueda.get("alm_destino")));
        String fecha_inicial = ""+StringHelper.isNullString(String.valueOf(has_busqueda.get("fecha_inicial")))+"";
        String fecha_final = ""+StringHelper.isNullString(String.valueOf(has_busqueda.get("fecha_final")))+"";
        
        String data_string = app_selected+"___"+id_usuario+"___"+folio+"___"+codigo+"___"+descripcion+"___"+alm_origen+"___"+alm_destino+"___"+fecha_inicial+"___"+fecha_final;
        
        
        //obtiene total de registros en base de datos, con los parametros de busqueda
        int total_items = this.getInvDao().countAll(data_string);
        
        //calcula el total de paginas
        int total_pags = resource.calculaTotalPag(total_items,items_por_pag);
        
        //variables que necesita el datagrid, para no tener que hacer uno por cada aplicativo
        DataPost dataforpos = new DataPost(orderby, desc, items_por_pag, pag_start, display_pag, input_json, cadena_busqueda,total_items,total_pags,id_user_cod);
        
        int offset = resource.__get_inicio_offset(items_por_pag, pag_start);
        
        //obtiene los registros para el grid, de acuerdo a los parametros de busqueda
        jsonretorno.put("Data", this.getInvDao().getInvTraspasos_PaginaGrid(data_string, offset, items_por_pag, orderby, desc));
        
        //obtiene el hash para los datos que necesita el datagrid
        jsonretorno.put("DataForGrid", dataforpos.formaHashForPos(dataforpos));
        
        return jsonretorno;
    }
    
    
    
    
    //obtiene los almacenes de la empresa para el buscador
    @RequestMapping(method = RequestMethod.POST, value="/getAlmacenes.json")
    public @ResponseBody HashMap<String,ArrayList<HashMap<String, String>>> getAlmacenesJson(
            @RequestParam(value="iu", required=true) String id_user_cod,
            Model model
        ) {
        HashMap<String,ArrayList<HashMap<String, String>>> jsonretorno = new HashMap<String,ArrayList<HashMap<String, String>>>();
        ArrayList<HashMap<String, String>> almacenes = new ArrayList<HashMap<String, String>>();
        HashMap<String, String> userDat = new HashMap<String, String>();
        
        //decodificar id de usuario
        Integer id_usuario = Integer.parseInt(Base64Coder.decodeString(id_user_cod));
        userDat = this.getHomeDao().getUserById(id_usuario);
        Integer id_empresa = Integer.parseInt(userDat.get("empresa_id"));
        
        almacenes = this.getInvDao().getAlmacenes2(id_empresa);
        
        jsonretorno.put("AlmEmpresa", almacenes);
        
        return jsonretorno;
    }
    
    
    
    
    //obtiene lineas de producto y datos para el buscador
    @RequestMapping(method = RequestMethod.POST, value="/getProductoTipos.json")
    public @ResponseBody HashMap<String,ArrayList<HashMap<String, String>>> getProductoTiposJson(
            @RequestParam(value="iu", required=true) String id_user_cod,
            Model model
        ) {
        HashMap<String,ArrayList<HashMap<String, String>>> jsonretorno = new HashMap<String,ArrayList<HashMap<String, String>>>();
        ArrayList<HashMap<String, String>> arrayTiposProducto = new ArrayList<HashMap<String, String>>();
        
        //decodificar id de usuario
        Integer id_usuario = Integer.parseInt(Base64Coder.decodeString(id_user_cod));
        arrayTiposProducto=this.getInvDao().getProducto_Tipos();
        jsonretorno.put("prodTipos", arrayTiposProducto);
        
        return jsonretorno;
    }
    
    
    //obtiene los productos para el buscador
    @RequestMapping(method = RequestMethod.POST, value="/getBuscadorProductos.json")
    public @ResponseBody HashMap<String,ArrayList<HashMap<String, String>>> getBuscadorProductosJson(
            @RequestParam(value="sku", required=true) String sku,
            @RequestParam(value="tipo", required=true) String tipo,
            @RequestParam(value="descripcion", required=true) String descripcion,
            @RequestParam(value="id_almacen", required=true) Integer id_almacen,
            @RequestParam(value="fecha", required=true) String fecha,
            @RequestParam(value="iu", required=true) String id_user,
            Model model
            ) {
        
        log.log(Level.INFO, "Ejecutando getBuscadorProductosJson de {0}", InvTraspasosController.class.getName());
        HashMap<String,ArrayList<HashMap<String, String>>> jsonretorno = new HashMap<String,ArrayList<HashMap<String, String>>>();
        ArrayList<HashMap<String, String>> productos = new ArrayList<HashMap<String, String>>();
        HashMap<String, String> userDat = new HashMap<String, String>();
        
        //decodificar id de usuario
        Integer id_usuario = Integer.parseInt(Base64Coder.decodeString(id_user));
        userDat = this.getHomeDao().getUserById(id_usuario);
        Integer id_empresa = Integer.parseInt(userDat.get("empresa_id"));
        
        //convertir en arreglo la fecha que viene de la vista
        String f [] = fecha.split("-");
        
        //tomar el año de la fecha que viene de la vista
        Integer ano_actual=Integer.parseInt(f[0]);
        
        productos = this.getInvDao().getInvAjustes_BuscadorProductos(sku, tipo, descripcion, id_empresa, id_almacen, ano_actual);
        
        jsonretorno.put("Productos", productos);
        
        return jsonretorno;
    }
    
    
    
    
    //obtiene datos del producto a agregar
    @RequestMapping(method = RequestMethod.POST, value="/getDatosProducto.json")
    public @ResponseBody HashMap<String,ArrayList<HashMap<String, String>>> getDatosProductoJson(
            @RequestParam(value="sku", required=true) String sku,
            @RequestParam(value="id_almacen", required=true) Integer id_almacen,
            @RequestParam(value="fecha", required=true) String fecha,
            @RequestParam(value="iu", required=true) String id_user,
            Model model
    ) {
        
        log.log(Level.INFO, "Ejecutando getDatosProductoJson de {0}", InvTraspasosController.class.getName());
        HashMap<String,ArrayList<HashMap<String, String>>> jsonretorno = new HashMap<String,ArrayList<HashMap<String, String>>>();
        ArrayList<HashMap<String, String>> datos = new ArrayList<HashMap<String, String>>();
        ArrayList<HashMap<String, String>> presentaciones = new ArrayList<HashMap<String, String>>();
        HashMap<String, String> userDat = new HashMap<String, String>();
        ArrayList<HashMap<String, String>> parematros = new ArrayList<HashMap<String, String>>();
        
        //decodificar id de usuario
        Integer id_usuario = Integer.parseInt(Base64Coder.decodeString(id_user));
        userDat = this.getHomeDao().getUserById(id_usuario);
        Integer id_empresa = Integer.parseInt(userDat.get("empresa_id"));
        Integer idProducto = 0;
        //convertir en arreglo la fecha que viene de la vista
        String f [] = fecha.split("-");
        
        //tomar el año de la fecha que viene de la vista
        Integer ano_actual=Integer.parseInt(f[0]);
        
        datos = this.getInvDao().getInvTraspasos_DatosProducto(sku.toUpperCase(), id_empresa, id_almacen, ano_actual);
        
        if(datos.size()>0){
            idProducto = Integer.parseInt(datos.get(0).get("id_producto"));
            presentaciones = this.getInvDao().getProducto_PresentacionesON(idProducto);
        }
        
        //obtener valor de configuracion para saber si es necesario controlar existencias por Presentacion
        parematros = this.getInvDao().getInvParametros(id_empresa);
        
        jsonretorno.put("Producto", datos);
        jsonretorno.put("Presentaciones", presentaciones);
        jsonretorno.put("Par", parematros);
        
        return jsonretorno;
    }
    
    
    
    //Obtiene la existencia de un Producto en una presentacion en un almacen
    @RequestMapping(method = RequestMethod.POST, value="/getExisPres.json")
    public @ResponseBody HashMap<String,ArrayList<HashMap<String, String>>> getExisPresJson(
            @RequestParam(value="id_prod", required=true) Integer id_prod,
            @RequestParam(value="id_pres", required=true) Integer id_pres,
            @RequestParam(value="id_alm", required=true) Integer id_alm,
            @RequestParam(value="iu", required=true) String id_user,
            Model model
        ) {
        
        log.log(Level.INFO, "Ejecutando getExisPresJson de {0}", InvTraspasosController.class.getName());
        HashMap<String,ArrayList<HashMap<String, String>>> jsonretorno = new HashMap<String,ArrayList<HashMap<String, String>>>();
        ArrayList<HashMap<String, String>> existencias = new ArrayList<HashMap<String, String>>();
        //HashMap<String, String> userDat = new HashMap<String, String>();
        /*
        //decodificar id de usuario
        Integer id_usuario = Integer.parseInt(Base64Coder.decodeString(id_user));
        userDat = this.getHomeDao().getUserById(id_usuario);
        Integer id_empresa = Integer.parseInt(userDat.get("empresa_id"));
        */
        existencias = this.getInvDao().getInvTraspasos_ExistenciaPresentacion(id_prod, id_pres, id_alm);
        
        jsonretorno.put("Existencia", existencias);
        
        return jsonretorno;
    }
    
    
    
    
    
    
    
    @RequestMapping(method = RequestMethod.POST, value="/getTraspaso.json")
    public @ResponseBody HashMap<String,ArrayList<HashMap<String, String>>> getTraspasoJson(
            @RequestParam(value="identificador", required=true) Integer identificador,
            @RequestParam(value="iu", required=true) String id_user_cod,
            Model model
        ){
        
        log.log(Level.INFO, "Ejecutando getTraspasoJson de {0}", InvTraspasosController.class.getName());
        HashMap<String,ArrayList<HashMap<String, String>>> jsonretorno = new HashMap<String,ArrayList<HashMap<String, String>>>();
        HashMap<String, String> userDat = new HashMap<String, String>();
        ArrayList<HashMap<String, String>> datosTraspaso = new ArrayList<HashMap<String, String>>(); 
        ArrayList<HashMap<String, String>> datosGrid = new ArrayList<HashMap<String, String>>(); 
        ArrayList<HashMap<String, String>> sucursales = new ArrayList<HashMap<String, String>>();
        ArrayList<HashMap<String, String>> almacenes = new ArrayList<HashMap<String, String>>();
        ArrayList<HashMap<String, String>> arrayExtras = new ArrayList<HashMap<String, String>>();
        ArrayList<HashMap<String, String>> presentaciones = new ArrayList<HashMap<String, String>>();
        HashMap<String, String> mapExtras = new HashMap<String, String>();
        ArrayList<HashMap<String, String>> parematros = new ArrayList<HashMap<String, String>>();
        
        //decodificar id de usuario
        Integer id_usuario = Integer.parseInt(Base64Coder.decodeString(id_user_cod));
        userDat = this.getHomeDao().getUserById(id_usuario);
        
        Integer id_empresa = Integer.parseInt(userDat.get("empresa_id"));
        Integer id_sucursal = Integer.parseInt(userDat.get("sucursal_id"));
        
        if( identificador != 0 ){
            datosTraspaso = this.getInvDao().getInvTraspasos_Datos(identificador);
            datosGrid = this.getInvDao().getInvTraspasos_DatosGrid(identificador, Integer.parseInt(datosTraspaso.get(0).get("alm_origen")) );
            //este matodo obtiene dato de los almacenes con id de sucursal y id de empresa
            almacenes = this.getInvDao().getAlmacenes2(id_empresa);
        }else{
            //en esta parte se incluye id de sucursal y id de empresa del almacen
            almacenes = this.getInvDao().getAlmacenes(id_empresa);
        }
        
        sucursales = this.getInvDao().getSucursales(id_empresa);
        
        mapExtras.put("fecha_actual", TimeHelper.getFechaActualYMD());
        mapExtras.put("suc_id_actual", String.valueOf(id_sucursal));
        
        arrayExtras.add(mapExtras);
        
        //le pasamos el idProducto=0, esto para que nos devuelva todas las presentaciones disponibles
        presentaciones = this.getInvDao().getProducto_Presentaciones(0);
        
        //obtener valor de configuracion para saber si es necesario controlar existencias por Presentacion
        parematros = this.getInvDao().getInvParametros(id_empresa);
        
        jsonretorno.put("Datos", datosTraspaso);
        jsonretorno.put("DatosGrid", datosGrid);
        jsonretorno.put("Sucursales", sucursales);
        jsonretorno.put("Almacenes", almacenes);
        jsonretorno.put("Extras", arrayExtras);
        jsonretorno.put("Presentaciones", presentaciones);
        jsonretorno.put("Par", parematros);
        
        return jsonretorno;
    }
    
    
    
    
    
    
    //edicion y nuevo
    @RequestMapping(method = RequestMethod.POST, value="/edit.json")
    public @ResponseBody HashMap<String, String> editJson(
            @RequestParam(value="identificador", required=true) Integer identificador,
            @RequestParam(value="fecha_traspaso", required=true) String fecha_traspaso,
            @RequestParam(value="select_suc_origen", required=true) Integer select_suc_origen,
            @RequestParam(value="select_alm_origen", required=true) Integer select_alm_origen,
            @RequestParam(value="select_suc_destino", required=true) Integer select_suc_destino,
            @RequestParam(value="select_alm_destino", required=true) Integer select_alm_destino,
            @RequestParam(value="observaciones", required=true) String observaciones,
            @RequestParam(value="no_tr", required=true)         String[] no_tr,
            @RequestParam(value="idproducto", required=true)    String[] idproducto,
            @RequestParam(value="cant_traspaso", required=true) String[] cant_traspaso,
            @RequestParam(value="select_pres", required=true)    String[] select_pres,
            @ModelAttribute("user") UserSessionData user,
            Model model
        ) {
            
            HashMap<String, String> jsonretorno = new HashMap<String, String>();
            HashMap<String, String> succes = new HashMap<String, String>();
            String extra_data_array = null;
            String arreglo[];
            arreglo = new String[no_tr.length];
            String actualizar = "0";
            String actualizo="0";
            
            Integer app_selected = 10;//Traspasos
            String command_selected = "new";
            Integer id_usuario= user.getUserId();//variable para el id  del usuario
            
            for(int i=0; i<no_tr.length; i++) {
                arreglo[i]= "'"+ idproducto[i] +"___"+ cant_traspaso[i] +"___"+ no_tr[i] +"___"+ select_pres[i] +"'";
                System.out.println(arreglo[i]);
            }
            
            //serializar el arreglo
            extra_data_array = StringUtils.join(arreglo, ",");
            
            if( identificador==0 ){
                command_selected = "new";
            }else{
                command_selected = "edit";
            }
            
            //la accion es para confirmar
            String data_string = 
                    app_selected+"___"+
                    command_selected+"___"+
                    id_usuario+"___"+
                    identificador+"___"+
                    select_suc_origen+"___"+
                    select_alm_origen+"___"+
                    select_suc_destino+"___"+
                    select_alm_destino+"___"+
                    observaciones.toUpperCase()+"___"+
                    fecha_traspaso;
            
            System.out.println("data_string: "+data_string);
            
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
    
    
    
    
    
    @RequestMapping(value = "/getPdfTraspaso/{id_traspaso}/{iu}/out.json", method = RequestMethod.GET ) 
    public ModelAndView getPdfAjusteJson(
                @PathVariable("id_traspaso") Integer id_traspaso,
                @PathVariable("iu") String id_user,
                HttpServletRequest request, 
                HttpServletResponse response, 
                Model model)
            throws ServletException, IOException, URISyntaxException, DocumentException, Exception {
        
        HashMap<String, String> userDat = new HashMap<String, String>();
        HashMap<String, String> datosTraspaso = new HashMap<String, String>();
        ArrayList<HashMap<String, String>> lista_productos = new ArrayList<HashMap<String, String>>();
        HashMap<String, String> datos_empresa = new HashMap<String, String>();
        Integer app_selected = 10; //Traspasos
        
        System.out.println("Generando PDF Traspaso");
        
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
        String file_name = "traspaso_"+rfc_empresa+".pdf";
        
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
        
        datosTraspaso = this.getInvDao().getInvTraspasos_DatosPDF(id_traspaso);
        datosTraspaso.put("codigo1", this.getGralDao().getCodigo1Iso(id_empresa, app_selected));
        datosTraspaso.put("codigo2", this.getGralDao().getCodigo2Iso(id_empresa, app_selected));
        
        lista_productos = this.getInvDao().getInvTraspasos_DatosGridPDF(id_traspaso);
        
        //instancia a la clase, aqui se le pasa los parametros al constructor
        PdfTraspaso traspaso = new PdfTraspaso(datos_empresa, datosTraspaso, lista_productos, fileout, ruta_imagen);
        
        //metodo que construye el pdf
        traspaso.ViewPDF();
        
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
