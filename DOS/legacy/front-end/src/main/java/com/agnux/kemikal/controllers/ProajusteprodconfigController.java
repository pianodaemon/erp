/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.agnux.kemikal.controllers;



import com.agnux.cfd.v2.Base64Coder;
import com.agnux.common.helpers.StringHelper;
import com.agnux.common.obj.ResourceProject;
import com.agnux.common.obj.UserSessionData;
import com.agnux.kemikal.interfacedaos.ProInterfaceDao;
import com.agnux.kemikal.interfacedaos.GralInterfaceDao;
import com.agnux.kemikal.interfacedaos.HomeInterfaceDao;
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
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttributes;

/**
 * /**
 *
 * @author
 * 06-07-2012
 * Este controller es para generar el reporte de Pedidos
 */

@Controller
@SessionAttributes({"user"})
@RequestMapping("/proajusteprodconfig/")

public class ProajusteprodconfigController {
    private static final Logger log  = Logger.getLogger(RepFacturacionController.class.getName());
    ResourceProject resource = new ResourceProject();
    
    @Autowired
    @Qualifier("daoPro")
    private ProInterfaceDao proDao;
        
    
    @Autowired
    @Qualifier("daoHome")
    private HomeInterfaceDao HomeDao;
    
    @Autowired
    @Qualifier("daoGral")
    private GralInterfaceDao gralDao;
    private Integer agente_id;
    
    public ProInterfaceDao getProDao() {
        return proDao;
    }
    
    public HomeInterfaceDao getHomeDao() {
        return HomeDao;
    }
    
    
    public GralInterfaceDao getGralDao() {
        return gralDao;
    }
    
    
    
    @RequestMapping(value="/startup.agnux")
    public ModelAndView startUp(HttpServletRequest request, HttpServletResponse response, 
            @ModelAttribute("user") UserSessionData user)
            throws ServletException, IOException {
        
        log.log(Level.INFO, "Ejecutando starUp de {0}", ProajusteprodconfigController.class.getName());
        LinkedHashMap<String,String> infoConstruccionTabla = new LinkedHashMap<String,String>();
        
        
        ModelAndView x = new ModelAndView("proajusteprodconfig/startup", "title", "Cambio de productos en formulas");
        
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
    
    
    //fin de estado
    
    //obtiene la existencia de un Almacen en especifico
    @RequestMapping(method = RequestMethod.POST, value="/getFormulasConEsteProducto.json")
    public @ResponseBody HashMap<String,ArrayList<HashMap<String, String>>> getFormulasConEsteProductoJson(
            @RequestParam("codigo") String codigo,
            @RequestParam("descipcion") String descipcion,
            @RequestParam(value="iu", required=true) String id_user,
            Model model
            ) {
        
        log.log(Level.INFO, "Ejecutando getPedidos de {0}", ProajusteprodconfigController.class.getName());
        HashMap<String,ArrayList<HashMap<String, String>>> jsonretorno = new HashMap<String,ArrayList<HashMap<String, String>>>();
        ArrayList<HashMap<String, String>> prodFormulas = new ArrayList<HashMap<String, String>>();  
        HashMap<String, String> userDat = new HashMap<String, String>();
        
        
        codigo = "%"+codigo+"%";
        descipcion = "%"+descipcion+"%";
        
        //decodificar id de usuario
        Integer id_usuario = Integer.parseInt(Base64Coder.decodeString(id_user));
        userDat = this.getHomeDao().getUserById(id_usuario);
        
        Integer id_empresa = Integer.parseInt(userDat.get("empresa_id"));
        
        prodFormulas = this.getProDao().getPro_FormulasProductos(codigo,descipcion, id_empresa);
        
        
        jsonretorno.put("ProdFormulas", prodFormulas);
        
        return jsonretorno;
    }
    
    
    //obtiene lineas de producto y datos para el buscador
    @RequestMapping(method = RequestMethod.POST, value="/getProductoTipos.json")
    public @ResponseBody HashMap<String,ArrayList<HashMap<String, String>>> getDataLineasJson(
            @RequestParam(value="iu", required=true) String id_user_cod,
            Model model
            ) {
        
        HashMap<String,ArrayList<HashMap<String, String>>> jsonretorno = new HashMap<String,ArrayList<HashMap<String, String>>>();
        //ArrayList<HashMap<String, String>> arrayLineas = new ArrayList<HashMap<String, String>>();
        ArrayList<HashMap<String, String>> arrayTiposProducto = new ArrayList<HashMap<String, String>>();
        //HashMap<String, String> cadenaLineas = new HashMap<String, String>();
        
        //decodificar id de usuario
        Integer id_usuario = Integer.parseInt(Base64Coder.decodeString(id_user_cod));
        
        arrayTiposProducto=this.getProDao().getProducto_Tipos();
        
        //cadenaLineas.put("cad_lineas", genera_treeview( this.getInvDao().getProducto_Lineas() ));
        //arrayLineas.add(cadenaLineas);
        //jsonretorno.put("Lines",arrayLineas);
        jsonretorno.put("prodTipos", arrayTiposProducto);
        
        return jsonretorno;
    }
    
    
    //obtiene los productos para el buscador
    @RequestMapping(method = RequestMethod.POST, value="/get_buscador_productos.json")
    public @ResponseBody HashMap<String,ArrayList<HashMap<String, String>>> getProductosJson(
            @RequestParam(value="sku", required=true) String sku,
            @RequestParam(value="tipo", required=true) String tipo,
            @RequestParam(value="descripcion", required=true) String descripcion,
            @RequestParam(value="iu", required=true) String id_user,
            Model model
            ) {
        
        log.log(Level.INFO, "Ejecutando getProductosJson de {0}", ProConfigProduccionController.class.getName());
        HashMap<String,ArrayList<HashMap<String, String>>> jsonretorno = new HashMap<String,ArrayList<HashMap<String, String>>>();
        ArrayList<HashMap<String, String>> productos = new ArrayList<HashMap<String, String>>();
        HashMap<String, String> userDat = new HashMap<String, String>();
        //decodificar id de usuario
        Integer id_usuario = Integer.parseInt(Base64Coder.decodeString(id_user));
        userDat = this.getHomeDao().getUserById(id_usuario);
        
        Integer id_empresa = Integer.parseInt(userDat.get("empresa_id"));
        
        productos = this.getProDao().getBuscadorProductos(sku, tipo, descripcion, id_empresa);
        
        jsonretorno.put("productos", productos);
        
        return jsonretorno;
    }
    
    
    //obtiene los productos para el buscador
    @RequestMapping(method = RequestMethod.POST, value="/getActualizaProductoEnFormulas.json")
    public @ResponseBody HashMap<String, String> getActualizaProductoEnFormulasJson(
            @RequestParam(value="id_producto_existe", required=true) String id_producto_existe,
            @RequestParam(value="id_producto_sustituto", required=true) String id_producto_sustituto,
            @RequestParam(value="id_formulas", required=true) String id_formulas,
            @RequestParam(value="iu", required=true) String id_user,
            Model model
            ) {
        
        log.log(Level.INFO, "Ejecutando getProductosJson de {0}", ProConfigProduccionController.class.getName());
        HashMap<String, String> jsonretorno = new HashMap<String, String>();
        HashMap<String, String> succes = new HashMap<String, String>();
        
        HashMap<String, String> userDat = new HashMap<String, String>();
        //decodificar id de usuario
        Integer id_usuario = Integer.parseInt(Base64Coder.decodeString(id_user));
        userDat = this.getHomeDao().getUserById(id_usuario);
        
        Integer id_empresa = Integer.parseInt(userDat.get("empresa_id"));
        
        Integer app_selected = 86;//actualizador de productos
        String command_selected = "new";
        
        //String extra_data_array = "'sin datos'";
        String actualizo = "0";
        int no_partida = 0;
        
        //String arreglo[];
        //arreglo = new String[id_formulas.length];
        String extra_data_array = id_formulas;
        /*
        int total_tr = id_formulas.length;
        if(total_tr > 0){
            for(int i=0; i<id_formulas.length; i++) {
                arreglo[i]= "'"+id_formulas[i] +"'";
            }
            extra_data_array = StringUtils.join(arreglo, ",");
        }else{
            extra_data_array ="'sin datos'";
        }*/
        
        Integer id = 0;
        
        if( id==0 ){
            command_selected = "new";
        }else{
            command_selected = "edit";
        }
        
        
        String data_string = app_selected+"___"+command_selected+"___"+id_usuario+"___"+id+"___"+id_producto_existe+"___"+id_producto_sustituto;
        
        succes = this.getProDao().selectFunctionValidateAaplicativo(data_string,app_selected,extra_data_array);
        
        log.log(Level.INFO, "despues de validacion {0}", String.valueOf(succes.get("success")));
        if( String.valueOf(succes.get("success")).equals("true") ){
            actualizo = this.getProDao().selectFunctionForApp_Produccion(data_string, extra_data_array);
        }
        
        jsonretorno.put("success",String.valueOf(succes.get("success")));
        
        log.log(Level.INFO, "Salida json {0}", String.valueOf(jsonretorno.get("success")));
        return jsonretorno;
        
    }
    
    
    @RequestMapping(method = RequestMethod.POST, value="/getFormula.json")
    public @ResponseBody HashMap<String,ArrayList<HashMap<String, String>>> getFormulasJson(
            @RequestParam(value="id", required=true) String id,
            @RequestParam(value="iu", required=true) String id_user_cod,
            //@RequestParam(value="numero_buscador", required=true) Integer numero_buscador,
            Model model
            ){
        
        log.log(Level.INFO, "Ejecutando getFormulasjson de {0}", InvFormulasController.class.getName());
        HashMap<String,ArrayList<HashMap<String, String>>> jsonretorno = new HashMap<String,ArrayList<HashMap<String, String>>>();
        HashMap<String, String> userDat = new HashMap<String, String>();
       
        ArrayList<HashMap<String, String>> datosFormulas = new ArrayList<HashMap<String, String>>(); 
        ArrayList<HashMap<String, String>> datosFormulasMinigrid = new ArrayList<HashMap<String, String>>(); 
        ArrayList<HashMap<String, String>> datosFormulasProductoSaliente = new ArrayList<HashMap<String, String>>(); 
        
        //ArrayList<HashMap<String, String>> tiposProducto = new ArrayList<HashMap<String, String>>();
        //ArrayList<HashMap<String, String>> unidades = new ArrayList<HashMap<String, String>>();
        
        //decodificar id de usuario
        Integer id_usuario = Integer.parseInt(Base64Coder.decodeString(id_user_cod));
        userDat = this.getHomeDao().getUserById(id_usuario);
        
       // Integer id_empresa = Integer.parseInt(userDat.get("empresa_id"));
       
        if(!id.equals("0")){
            datosFormulas = this.getProDao().getFormula_Datos(id);
            datosFormulasMinigrid = this.getProDao().getFormula_DatosMinigrid(id, "1");
            datosFormulasProductoSaliente = this.getProDao().getFormula_DatosProductoSaliente(id, "1");
        }
        
        //estos aray list seretornan a la vista al momento de click en nuevo   y los retorna en un HashMap<String,ArrayList<HashMap<String, String>>> jsonretorno
        //tiposProducto = this.getInvDao().getProducto_Tipos_para_formulas(numero_buscador);
        //unidades = this.getInvDao().getProducto_Unidades_para_formulas();
        
        
       jsonretorno.put("Formulas", datosFormulas);
       jsonretorno.put("Formulas_DatosMinigrid", datosFormulasMinigrid);
       jsonretorno.put("Formulas_DatosProductoSaliente", datosFormulasProductoSaliente);
        // jsonretorno.put("ProdTipos", tiposProducto);
        //jsonretorno.put("Unidades",unidades);
        //        jsonretorno.put("Regiones", regiones);
       
        return jsonretorno;
    }
    
    //Genera pdf de facturacion de
   /* @RequestMapping(value = "/get_genera_reporte_facturacion/{cadena}/{iu}/out.json", method = RequestMethod.GET ) 
    public ModelAndView getGeneraPdfFacturacionJson(
                @PathVariable("cadena") String cadena,
                @PathVariable("iu") String id_user,
                HttpServletRequest request, 
                HttpServletResponse response, 
                Model model)
            throws ServletException, IOException, URISyntaxException, DocumentException {
        
        HashMap<String, String> userDat = new HashMap<String, String>();
        
        
        System.out.println("Generando reporte de facturacion");
        
        Integer opciones=0;
        String factura="";
        String cliente="";
        String fecha_inicial="";
        String fecha_final="";
        
        String arrayCad [] = cadena.split("___");
        opciones = Integer.parseInt(arrayCad [0]);
        factura = "%"+arrayCad [1]+"%";
        cliente = "%"+arrayCad [2]+"%";
        fecha_inicial = arrayCad [3];
        fecha_final = arrayCad [4];
        
        //decodificar id de usuario
        Integer id_usuario = Integer.parseInt(Base64Coder.decodeString(id_user));
        
        userDat = this.getHomeDao().getUserById(id_usuario);
        Integer id_empresa = Integer.parseInt(userDat.get("empresa_id"));
        
        
        String razon_social_empresa = this.getGralDao().getRazonSocialEmpresaEmisora(id_empresa);
        
        //obtener el directorio temporal
        String dir_tmp = this.getGralDao().getTmpDir();
        
        
        String[] array_company = razon_social_empresa.split(" ");
        String company_name= array_company[0].toLowerCase();
        String ruta_imagen = this.getGralDao().getImagesDir() +"logo_"+ company_name +".png";
        
        
        File file_dir_tmp = new File(dir_tmp);
        System.out.println("Directorio temporal: "+file_dir_tmp.getCanonicalPath());
        
        
        String file_name = "facturas_"+fecha_inicial+"_"+fecha_final+".pdf";
        //ruta de archivo de salida
        String fileout = file_dir_tmp +"/"+  file_name;
        
        ArrayList<HashMap<String, String>> lista_facturas = new ArrayList<HashMap<String, String>>();
        
        //obtiene las facturas del periodo indicado
        lista_facturas = this.getPedidDao().getReportePedidos(opciones, factura, cliente, fecha_inicial, fecha_final );
        
        //instancia a la clase que construye el pdf del reporte de facturas
        PdfFacturacion x = new PdfFacturacion( fileout,ruta_imagen,razon_social_empresa,fecha_inicial,fecha_final,lista_facturas);
        
        
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
        
    } */
    
}
