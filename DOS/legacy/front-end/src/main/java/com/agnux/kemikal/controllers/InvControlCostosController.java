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
import com.agnux.kemikal.reportes.PdfInvControlCosto;
import com.itextpdf.text.DocumentException;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URISyntaxException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
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
@RequestMapping("/invcontrolcostos/")
public class InvControlCostosController {
    private static final Logger log  = Logger.getLogger(InvControlCostosController.class.getName());
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
        
        log.log(Level.INFO, "Ejecutando starUp de {0}", InvControlCostosController.class.getName());
        LinkedHashMap<String,String> infoConstruccionTabla = new LinkedHashMap<String,String>();
        
        //infoConstruccionTabla.put("id", "Acciones:70");
        infoConstruccionTabla.put("codigo", "C&oacute;digo:90");
        infoConstruccionTabla.put("descripcion","Descripci&oacute;n:150");
        infoConstruccionTabla.put("unidad", "Unidad:80");
        infoConstruccionTabla.put("presentacion", "Presentaci&oacute;n:100");
        infoConstruccionTabla.put("orden_compra", "O.C.:80");
        infoConstruccionTabla.put("factura_prov", "Fac. Prov.:80");
        infoConstruccionTabla.put("moneda", "Moneda:60");
        infoConstruccionTabla.put("tipo_cambio", "T.C.:50");
        infoConstruccionTabla.put("costo", "C.U.:65");
        infoConstruccionTabla.put("costo_importacion", "I.G.I.:60");
        infoConstruccionTabla.put("costo_directo", "G.I.:60");
        infoConstruccionTabla.put("costo_adic", "C.A.:60");
        infoConstruccionTabla.put("costo_referencia", "C.I.T.:65");
        infoConstruccionTabla.put("precio_minimo", "P.M.:80");
        infoConstruccionTabla.put("moneda_pm", "Moneda&nbsp;P.M.:90");
        
        
        
        ModelAndView x = new ModelAndView("invcontrolcostos/startup", "title", "Control de Costos");
        
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
    @RequestMapping(value="/getAllCostos.json", method = RequestMethod.POST)
    public @ResponseBody HashMap<String,ArrayList<HashMap<String, Object>>> getAllCostosJson(
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
        
        //aplicativo Constorl de Costos
        Integer app_selected = 125;
        
        //decodificar id de usuario
        Integer id_usuario = Integer.parseInt(Base64Coder.decodeString(id_user_cod));
        
        //variables para el buscador
        String tipo_producto = StringHelper.isNullString(String.valueOf(has_busqueda.get("tipo_producto")));
        String familia = StringHelper.isNullString(String.valueOf(has_busqueda.get("familia")));
        String subfamilia = StringHelper.isNullString(String.valueOf(has_busqueda.get("subfamilia")));
        String marca = StringHelper.isNullString(String.valueOf(has_busqueda.get("marca")));
        String presentacion = StringHelper.isNullString(String.valueOf(has_busqueda.get("presentacion")));
        String producto = "%"+StringHelper.isNullString(String.valueOf(has_busqueda.get("producto")))+"%";
        String codigo = "%"+StringHelper.isNullString(String.valueOf(has_busqueda.get("codigo")))+"%";
        String ano = StringHelper.isNullString(String.valueOf(has_busqueda.get("ano")));
        String mes = StringHelper.isNullString(String.valueOf(has_busqueda.get("mes")));
        
        String tipo_costo="1";//calculo a partir del ultimo costo
        String importacion="0";
        String directo="0";
        String pminimo="0";
        String simulacion="false";
        String tipo_cambio="0";
        
        //esta parte no es igual a todos los aplicativos porque se cambia el procedimiento de busqueda,
        //Se utiliza el mismo procedimiento que se utiliza en el plugin de Control de Costos.
        //se tomo la decision de utilizar el mismo proc porque se hace varios calculos y asi evitamos volver a construir codigo para el grid
        int offset = resource.__get_inicio_offset(items_por_pag, pag_start);
        
        String data_string = app_selected+"___"+id_usuario+"___"+tipo_producto+"___"+marca+"___"+familia+"___"+subfamilia+"___"+producto+"___"+presentacion+"___"+tipo_costo+"___"+simulacion+"___"+importacion+"___"+directo+"___"+pminimo+"___"+tipo_cambio+"___"+codigo+"___"+ano+"___"+mes;
        
        //obtiene total de registros en base de datos, con los parametros de busqueda
        int total_items = this.getInvDao().countAll(data_string);
        
        //calcula el total de paginas
        int total_pags = resource.calculaTotalPag(total_items,items_por_pag);
        
        //variables que necesita el datagrid, para no tener que hacer uno por cada aplicativo
        DataPost dataforpos = new DataPost(orderby, desc, items_por_pag, pag_start, display_pag, input_json, cadena_busqueda,total_items,total_pags,id_user_cod);
        
        //obtiene los registros para el grid, de acuerdo a los parametros de busqueda
        jsonretorno.put("Data",this.getInvDao().getInvControlCostos_PaginaGrid(data_string, offset, items_por_pag, orderby, desc) );
        
        //obtiene el hash para los datos que necesita el datagrid
        jsonretorno.put("DataForGrid", dataforpos.formaHashForPos(dataforpos));
        
        return jsonretorno;
    }
    
    
    //obtiene datos Buscador principal
    @RequestMapping(method = RequestMethod.POST, value="/getDatosBuscadorPrincipal.json")
    public @ResponseBody HashMap<String,ArrayList<HashMap<String, String>>> getDatosBuscadorPrincipalJson(
            @RequestParam(value="iu", required=true) String id_user_cod,
            Model model
        ) {
        HashMap<String,ArrayList<HashMap<String, String>>> jsonretorno = new HashMap<String,ArrayList<HashMap<String, String>>>();
        HashMap<String, String> userDat = new HashMap<String, String>();
        ArrayList<HashMap<String, String>> tiposProducto = new ArrayList<HashMap<String, String>>();
        ArrayList<HashMap<String, String>> marcas = new ArrayList<HashMap<String, String>>();
        ArrayList<HashMap<String, String>> familias = new ArrayList<HashMap<String, String>>();
        ArrayList<HashMap<String, String>> presentaciones = new ArrayList<HashMap<String, String>>();
        
        //decodificar id de usuario
        Integer id_usuario = Integer.parseInt(Base64Coder.decodeString(id_user_cod));
        userDat = this.getHomeDao().getUserById(id_usuario);
        Integer id_empresa = Integer.parseInt(userDat.get("empresa_id"));
        String id_tipo_producto = "1";//Tipo de Producto TERMINADO(Para que se traiga por default las Familias de Productos Terminados)
        
        tiposProducto = this.getInvDao().getProducto_Tipos();
        marcas = this.getInvDao().getProducto_Marcas(id_empresa);
        familias = this.getInvDao().getInvProdSubFamiliasByTipoProd(id_empresa, id_tipo_producto);
        
        //Se le pasa como parametro el cero para que devuelva todas las presentaciones 
        presentaciones = this.getInvDao().getProducto_Presentaciones(0);
        
        jsonretorno.put("Anios", this.getInvDao().getInvControlCostos_Anios());
        jsonretorno.put("Marcas", marcas);
        jsonretorno.put("Familias", familias);
        jsonretorno.put("ProdTipos", tiposProducto);
        jsonretorno.put("Presentaciones", presentaciones);
        return jsonretorno;
    }
    
    
    
    @RequestMapping(method = RequestMethod.POST, value="/getDatosCalculoCosto.json")
    public @ResponseBody HashMap<String,ArrayList<HashMap<String, String>>> getDatosCalculoCostoJson(
            @RequestParam(value="identificador", required=true) Integer identificador,
            @RequestParam(value="iu", required=true) String id_user_cod,
            Model model
        ) {
        
        log.log(Level.INFO, "Ejecutando getDatosCalculoCostoJson de {0}", InvControlCostosController.class.getName());
        HashMap<String,ArrayList<HashMap<String, String>>> jsonretorno = new HashMap<String,ArrayList<HashMap<String, String>>>();
        HashMap<String, String> userDat = new HashMap<String, String>();
        ArrayList<HashMap<String, String>> datos = new ArrayList<HashMap<String, String>>();
        ArrayList<HashMap<String, String>> tiposProducto = new ArrayList<HashMap<String, String>>();
        ArrayList<HashMap<String, String>> marcas = new ArrayList<HashMap<String, String>>();
        ArrayList<HashMap<String, String>> familias = new ArrayList<HashMap<String, String>>();
        ArrayList<HashMap<String, String>> subfamilias = new ArrayList<HashMap<String, String>>();
        ArrayList<HashMap<String, String>> presentaciones = new ArrayList<HashMap<String, String>>();
        ArrayList<HashMap<String, String>> arrayExtra = new ArrayList<HashMap<String, String>>();
        HashMap<String, String> extra = new HashMap<String, String>();
        String id_tipo_producto="";
        
        //decodificar id de usuario
        Integer id_usuario = Integer.parseInt(Base64Coder.decodeString(id_user_cod));
        userDat = this.getHomeDao().getUserById(id_usuario);
        Integer id_empresa = Integer.parseInt(userDat.get("empresa_id"));
        
        tiposProducto = this.getInvDao().getProducto_Tipos();
        
        if( identificador != 0  ){
            datos = this.getInvDao().getProducto_Datos(identificador);
            id_tipo_producto = datos.get(0).get("tipo_prod_id");
        }else{
            id_tipo_producto = "1";//Tipo de Producto TERMINADO(Para que se traiga por default las Familias de Productos Terminados)
        }
        
        marcas = this.getInvDao().getProducto_Marcas(id_empresa);
        familias = this.getInvDao().getInvProdSubFamiliasByTipoProd(id_empresa, id_tipo_producto);
        
        //Se le pasa como parametro el cero para que devuelva todas las presentaciones 
        presentaciones = this.getInvDao().getProducto_Presentaciones(0);
        
        jsonretorno.put("Datos",datos);
        jsonretorno.put("Marcas", marcas);
        jsonretorno.put("Familias", familias);
        jsonretorno.put("ProdTipos", tiposProducto);
        jsonretorno.put("Presentaciones", presentaciones);
        
        return jsonretorno;
    }
    
    
    
    
    
    //obtiene los Familias del producto seleccionado
    @RequestMapping(method = RequestMethod.POST, value="/getSubFamiliasByFamProd.json")
    //public @ResponseBody HashMap<java.lang.String,java.lang.Object> getProveedorJson(
    public @ResponseBody HashMap<String,ArrayList<HashMap<String, String>>> getSubFamiliasByFamProdJson(
            @RequestParam(value="fam", required=true) String familia_id,
            @RequestParam(value="iu", required=true) String id_user_cod,
            Model model
        ) {
        
        log.log(Level.INFO, "Ejecutando getSubFamiliasByFamProdJson de {0}", InvControlCostosController.class.getName());
        HashMap<String,ArrayList<HashMap<String, String>>> jsonretorno = new HashMap<String,ArrayList<HashMap<String, String>>>();
        ArrayList<HashMap<String, String>> subfamilias = new ArrayList<HashMap<String, String>>();
        HashMap<String, String> userDat = new HashMap<String, String>();
        
        Integer id_usuario = Integer.parseInt(Base64Coder.decodeString(id_user_cod));
        userDat = this.getHomeDao().getUserById(id_usuario);
        
        Integer id_empresa = Integer.parseInt(userDat.get("empresa_id"));
        
        subfamilias = this.getInvDao().getProducto_Subfamilias(id_empresa,familia_id );
        
        jsonretorno.put("SubFamilias", subfamilias);
        
        return jsonretorno;
    }
    
    //obtiene los Familias del producto seleccionado
    @RequestMapping(method = RequestMethod.POST, value="/getFamiliasByTipoProd.json")
    public @ResponseBody HashMap<String,ArrayList<HashMap<String, String>>> getFamiliasByTipoProdJson(
            @RequestParam(value="tipo_prod", required=true) String tipo_prod,
            @RequestParam(value="iu", required=true) String id_user_cod,
            Model model
        ) {
        
        log.log(Level.INFO, "Ejecutando getFamiliasByTipoProdJson de {0}", InvControlCostosController.class.getName());
        HashMap<String,ArrayList<HashMap<String, String>>> jsonretorno = new HashMap<String,ArrayList<HashMap<String, String>>>();
        ArrayList<HashMap<String, String>> familias = new ArrayList<HashMap<String, String>>();
        HashMap<String, String> userDat = new HashMap<String, String>();
        
        Integer id_usuario = Integer.parseInt(Base64Coder.decodeString(id_user_cod));
        userDat = this.getHomeDao().getUserById(id_usuario);
        
        Integer id_empresa = Integer.parseInt(userDat.get("empresa_id"));
        
        familias = this.getInvDao().getInvProdSubFamiliasByTipoProd(id_empresa, tipo_prod);
        
        jsonretorno.put("Familias", familias);
        
        return jsonretorno;
    }
    
    
    //obtiene los productos para el buscador
    @RequestMapping(method = RequestMethod.POST, value="/getBuscadorProductos.json")
    public @ResponseBody HashMap<String,ArrayList<HashMap<String, String>>> getBuscadorProductosJson(
            @RequestParam(value="marca", required=true) String marca,
            @RequestParam(value="familia", required=true) String familia,
            @RequestParam(value="subfamilia", required=true) String subfamilia,
            @RequestParam(value="sku", required=true) String sku,
            @RequestParam(value="tipo", required=true) String tipo,
            @RequestParam(value="descripcion", required=true) String descripcion,
            @RequestParam(value="iu", required=true) String id_user,
            Model model
        ) {
        
        log.log(Level.INFO, "Ejecutando getBuscadorProductosJson de {0}", InvControlCostosController.class.getName());
        HashMap<String,ArrayList<HashMap<String, String>>> jsonretorno = new HashMap<String,ArrayList<HashMap<String, String>>>();
        ArrayList<HashMap<String, String>> productos = new ArrayList<HashMap<String, String>>();
        HashMap<String, String> userDat = new HashMap<String, String>();
        //decodificar id de usuario
        Integer id_usuario = Integer.parseInt(Base64Coder.decodeString(id_user));
        userDat = this.getHomeDao().getUserById(id_usuario);
        
        Integer id_empresa = Integer.parseInt(userDat.get("empresa_id"));
        
        productos = this.getInvDao().getBuscadorProductosParaControlCostos(marca, familia, subfamilia, sku, tipo, descripcion, id_empresa);
        
        jsonretorno.put("productos", productos);
        
        return jsonretorno;
    }
    
    
    
    
    //obtiene Productos para el Grid,
    //tambien calcula costos cuando es Simulacion
    @RequestMapping(method = RequestMethod.POST, value="/getBusquedaProductos.json")
    public @ResponseBody HashMap<String,ArrayList<HashMap<String, String>>> getBusquedaProductosJson(
            @RequestParam(value="tipo_prod", required=true) String tipo_prod,
            @RequestParam(value="mar", required=true) String mar,
            @RequestParam(value="fam", required=true) String fam,
            @RequestParam(value="subfam", required=true) String subfam,
            @RequestParam(value="codigo", required=true) String codigo,
            @RequestParam(value="producto", required=true) String producto,
            @RequestParam(value="pres", required=true) String pres,
            @RequestParam(value="tipo_costo", required=true) String tipo_costo,
            @RequestParam(value="simulacion", required=true) String simulacion,
            @RequestParam(value="importacion", required=true) String importacion,
            @RequestParam(value="directo", required=true) String directo,
            @RequestParam(value="pminimo", required=true) String pminimo,
            @RequestParam(value="tc", required=true) String tc,
            @RequestParam(value="costo_adic", required=true) String costo_adic,
            @RequestParam(value="iu", required=true) String id_user_cod,
            Model model
        ) {
        
        log.log(Level.INFO, "Ejecutando getBusquedaProductosJson de {0}", InvControlCostosController.class.getName());
        HashMap<String,ArrayList<HashMap<String, String>>> jsonretorno = new HashMap<String,ArrayList<HashMap<String, String>>>();
        ArrayList<HashMap<String, String>> productos = new ArrayList<HashMap<String, String>>();
        HashMap<String, String> userDat = new HashMap<String, String>();
        
        Integer id_usuario = Integer.parseInt(Base64Coder.decodeString(id_user_cod));
        userDat = this.getHomeDao().getUserById(id_usuario);
        
        Integer id_empresa = Integer.parseInt(userDat.get("empresa_id"));
        
        //aplicativo Control de Costos
        Integer app_selected = 125;
        
        //estos dos parematros se necesitan pero no se utilizan en esta parte,
        //por talñ motivo se le pasa cero por default
        //estos parametros son utilizados en el buscador del grid y paginado porque se utiliza el mismo procedimiento
        String ano="0";
        String mes="0";
        
        if(importacion.equals("")){
            importacion="0";
        }
        
        if(directo.equals("")){
            directo="0";
        }
        
        if(pminimo.equals("")){
            pminimo="0";
        }
        
        if(costo_adic.equals("")){
            costo_adic="0";
        }
        
        String data_string = app_selected+"___"+id_usuario+"___"+tipo_prod+"___"+mar+"___"+fam+"___"+subfam+"___%"+producto+"%___"+pres+"___"+tipo_costo+"___"+simulacion+"___"+importacion+"___"+directo+"___"+pminimo+"___"+tc+"___"+codigo+"___"+ano+"___"+mes+"___"+costo_adic;
        
        productos = this.getInvDao().selectFunctionForInvReporte(app_selected, data_string);
        
        jsonretorno.put("Grid", productos);
        
        return jsonretorno;
    }
    
    
    
                    
                    
    //Actualizar registros
    @RequestMapping(method = RequestMethod.POST, value="/edit.json")
    public @ResponseBody HashMap<String, String> editJson(
            @RequestParam(value="select_tipo_prod", required=true) String select_tipo_prod,
            @RequestParam(value="select_marca", required=true) String select_marca,
            @RequestParam(value="select_familia", required=true) String select_familia,
            @RequestParam(value="select_subfamilia", required=true) String select_subfamilia,
            @RequestParam(value="codigo", required=true) String codigo,
            @RequestParam(value="producto", required=true) String producto,
            @RequestParam(value="select_presentacion", required=true) String select_presentacion,
            @RequestParam(value="tipo_costo", required=true) String tipo_costo,
            @RequestParam(value="costo_importacion", required=true) String costo_importacion,
            @RequestParam(value="costo_directo", required=true) String costo_directo,
            @RequestParam(value="precio_minimo", required=true) String precio_minimo,
            @RequestParam(value="tipo_cambio", required=true) String tipo_cambio,
            @RequestParam(value="costo_adic", required=true) String costo_adic,
            @ModelAttribute("user") UserSessionData user,
            Model model
        ) {
            
            HashMap<String, String> jsonretorno = new HashMap<String, String>();
            HashMap<String, String> succes = new HashMap<String, String>();
            String extra_data_array = "'sin datos'";
            String actualizo="0";
            
            
            Integer app_selected = 125; //aplicativo Control de Costos
            String command_selected = "new";
            Integer id_usuario= user.getUserId();//variable para el id  del usuario
            
            command_selected = "edit";
            
            String data_string = app_selected+"___"+command_selected+"___"+id_usuario+"___"+select_tipo_prod+"___"+select_marca+"___"+select_familia+"___"+select_subfamilia+"___%"+producto+"%___%"+codigo+"%___"+select_presentacion+"___"+tipo_costo+"___"+costo_importacion+"___"+costo_directo+"___"+precio_minimo+"___"+costo_adic;
            
            actualizo = this.getInvDao().selectFunctionForApp_MovimientosInventario(data_string, extra_data_array);
            
            if(actualizo.equals("1")){
                jsonretorno.put("success","true");
            }else{
                jsonretorno.put("success","false");
            }
            
            log.log(Level.INFO, "Salida json {0}", actualizo);
        return jsonretorno;
    }

    
    
    
    
    @RequestMapping(value = "/getPdfReporteCostos/{cadena}/{costo_adic}/{iu}/out.json", method = RequestMethod.GET ) 
    public ModelAndView getPdfReporteCostos(
                @PathVariable("cadena") String cadena,
                @PathVariable("costo_adic") String costo_adic,
                @PathVariable("iu") String id_user,
                HttpServletRequest request, 
                HttpServletResponse response, 
                Model model
        )throws ServletException, IOException, URISyntaxException, DocumentException, Exception {
        
        HashMap<String, String> userDat = new HashMap<String, String>();
        HashMap<String, String> datos = new HashMap<String, String>();
        ArrayList<HashMap<String, String>> productos = new ArrayList<HashMap<String, String>>();
        Integer app_selected = 125; //Control de Costos
        
        System.out.println("Generando PDF Ajuste Inventario");
        
        //decodificar id de usuario
        Integer id_usuario = Integer.parseInt(Base64Coder.decodeString(id_user));
        userDat = this.getHomeDao().getUserById(id_usuario);
        Integer id_empresa = Integer.parseInt(userDat.get("empresa_id"));
        
        //cadena de parametros de la busqueda
        String data_string = app_selected+"___"+id_usuario+"___"+cadena+"___"+0+"___"+0+"___"+costo_adic;
        
        String razon_social_empresa = this.getGralDao().getRazonSocialEmpresaEmisora(id_empresa);
        String rfc_empresa = this.getGralDao().getRfcEmpresaEmisora(id_empresa);
        
        //obtener el directorio temporal
        String dir_tmp = this.getGralDao().getTmpDir();
        String ruta_imagen = this.getGralDao().getImagesDir()+rfc_empresa+"_logo.png";
        File file_dir_tmp = new File(dir_tmp);
        String file_name = "calculo_costos"+rfc_empresa+".pdf";
        
        //ruta de archivo de salida
        String fileout = file_dir_tmp +"/"+  file_name;
        
        String nombreMes= TimeHelper.ConvertNumToMonth(Integer.parseInt(TimeHelper.getMesActual()));
        
        SimpleDateFormat formato = new SimpleDateFormat("'Impreso el' d 'de "+nombreMes+" del ' yyyy 'a las' HH:mm:ss 'hrs.'");
        String impreso_en = formato.format(new Date());
        
        String cad[] = cadena.split("___");
        
        String tipo_costo=cad[6];
        
        if(cad[7].equals("true")){
            datos.put("titulo_reporte", "Reporte de Simulación de Cálculo de Costos");
        }else{
            datos.put("titulo_reporte", "Reporte de Cálculo de Costos");
        }
        datos.put("periodo", impreso_en);
        datos.put("empresa", razon_social_empresa);
        datos.put("codigo1", this.getGralDao().getCodigo1Iso(id_empresa, app_selected));
        datos.put("codigo2", this.getGralDao().getCodigo2Iso(id_empresa, app_selected));
        
        productos = this.getInvDao().selectFunctionForInvReporte(app_selected, data_string);
        
        //instancia a la clase, aqui se le pasa los parametros al constructor
        PdfInvControlCosto pdfcostos = new PdfInvControlCosto(datos, productos, fileout, tipo_costo);
        
        //metodo que construye el pdf
        pdfcostos.ViewPDF();
        
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
