/*
 * Controller para reporte de Compras Netas por Producto
 */
package com.agnux.kemikal.controllers;

import com.agnux.cfd.v2.Base64Coder;
import com.agnux.common.obj.ResourceProject;
import com.agnux.common.obj.UserSessionData;
import com.agnux.kemikal.interfacedaos.GralInterfaceDao;
import com.agnux.kemikal.interfacedaos.HomeInterfaceDao;
import com.agnux.kemikal.interfacedaos.InvInterfaceDao;
import com.agnux.kemikal.reportes.PdfInvComprasNetasProducto;
import com.agnux.kemikal.reportes.PdfInvComprasNetas_Sumarizados_xproducto_xproveedor;
import com.itextpdf.text.DocumentException;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
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
@RequestMapping("/invrepcomprasnetasporproducto/")
public class InvRepComprasNetasPorProductoController {
    private static final Logger log  = Logger.getLogger(InvRepComprasNetasPorProductoController.class.getName());
    ResourceProject resource = new ResourceProject();
    
    @Autowired
    @Qualifier("daoInv")
    private InvInterfaceDao invDao;
    
    @Autowired
    @Qualifier("daoHome")
    private HomeInterfaceDao HomeDao;
    
    @Autowired
    @Qualifier("daoGral")
    private GralInterfaceDao gralDao;
    
    public InvInterfaceDao getInvDao() {
        return invDao;
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
        
        log.log(Level.INFO, "Ejecutando starUp de {0}", InvRepComprasNetasPorProductoController.class.getName());
        LinkedHashMap<String,String> infoConstruccionTabla = new LinkedHashMap<String,String>();
        
        ModelAndView x = new ModelAndView("invrepcomprasnetasporproducto/startup", "title", "Compras Netas por Producto");
        
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
    
    
    
    
    //obtiene los proveedores para el buscador
    @RequestMapping(method = RequestMethod.POST, value="/getBuacadorProveedores.json")
    public @ResponseBody HashMap<String,ArrayList<HashMap<String, String>>> getBuscaProveedoresJson(
            @RequestParam(value="rfc", required=true) String rfc,
            @RequestParam(value="no_prov", required=true) String no_prov,
            @RequestParam(value="nombre", required=true) String nombre,
            @RequestParam(value="iu", required=true) String id_user,
            Model model
            ) {
        
        log.log(Level.INFO, "Ejecutando getBuscaProveedoresJson de {0}", InvRepComprasNetasPorProductoController.class.getName());
        HashMap<String,ArrayList<HashMap<String, String>>> jsonretorno = new HashMap<String,ArrayList<HashMap<String, String>>>();
        ArrayList<HashMap<String, String>> proveedores = new ArrayList<HashMap<String, String>>();
        HashMap<String, String> userDat = new HashMap<String, String>();
        //decodificar id de usuario
        Integer id_usuario = Integer.parseInt(Base64Coder.decodeString(id_user));
        userDat = this.getHomeDao().getUserById(id_usuario);
        
        Integer id_empresa = Integer.parseInt(userDat.get("empresa_id"));
        
        proveedores = this.getInvDao().getBuscadorProveedores(rfc, no_prov, nombre,id_empresa);
        
        jsonretorno.put("Proveedores", proveedores);
        
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
            @RequestParam(value="iu", required=true) String id_user,
            Model model
            ) {
        
        log.log(Level.INFO, "Ejecutando getBuscadorProductosJson de {0}", InvRepComprasNetasPorProductoController.class.getName());
        HashMap<String,ArrayList<HashMap<String, String>>> jsonretorno = new HashMap<String,ArrayList<HashMap<String, String>>>();
        ArrayList<HashMap<String, String>> productos = new ArrayList<HashMap<String, String>>();
        HashMap<String, String> userDat = new HashMap<String, String>();
        //decodificar id de usuario
        Integer id_usuario = Integer.parseInt(Base64Coder.decodeString(id_user));
        userDat = this.getHomeDao().getUserById(id_usuario);
        
        Integer id_empresa = Integer.parseInt(userDat.get("empresa_id"));
        
        productos = this.getInvDao().getBuscadorProductos(sku, tipo, descripcion, id_empresa);
        
        jsonretorno.put("Productos", productos);
        
        return jsonretorno;
    }
    
    
    
    
    
    //obtiene los datos para la vista del reporte de Compras Netas por Producto
    @RequestMapping(method = RequestMethod.POST, value="/getReporteComprasNetasPorProducto.json")
    public @ResponseBody HashMap<String,ArrayList<HashMap<String, String>>> getReporteComprasNetasPorProductoJson(
            @RequestParam("tipo_reporte") Integer tipo_reporte,
            @RequestParam("proveedor") String proveedor,
            @RequestParam("producto") String producto,
            @RequestParam("fecha_inicial") String fecha_inicial,
            @RequestParam("fecha_final") String fecha_final,
            @RequestParam(value="iu", required=true) String id_user,
            Model model
            ) {
        
        
        log.log(Level.INFO, "Ejecutando getReporteComprasNetasPorProductoJson de {0}", InvRepComprasNetasPorProductoController.class.getName());
        HashMap<String,ArrayList<HashMap<String, String>>> jsonretorno = new HashMap<String,ArrayList<HashMap<String, String>>>();
        
        HashMap<String, String> userDat = new HashMap<String, String>();
        ArrayList<HashMap<String, String>> lista_compras = new ArrayList<HashMap<String, String>>();
        
        //decodificar id de usuario
        Integer id_usuario = Integer.parseInt(Base64Coder.decodeString(id_user));
        userDat = this.getHomeDao().getUserById(id_usuario);
        
        Integer id_empresa = Integer.parseInt(userDat.get("empresa_id"));
        
        //obtiene los datos del reporte 
        //TIPO_REPORTE  
        // 1: por Producto
        // 2: por Por Proveedor
        // 3: ++ por Producto
        // 4: ++ por Proveedor
        lista_compras = this.getInvDao().getDatos_ReporteComprasNetasProducto(tipo_reporte,proveedor, producto, fecha_inicial,fecha_final,id_empresa);
        
        jsonretorno.put("Compras", lista_compras);
        
        return jsonretorno;
    }
    
    
    
    //Genera pdf reporte de Compras Netas por Producto
    @RequestMapping(value = "/getPdfReporteComprasNetasPorProducto/{cadena}/{iu}/out.json", method = RequestMethod.GET ) 
    public ModelAndView get_genera_pdf_edoctaJson(
                @PathVariable("cadena") String cadena,
                @PathVariable("iu") String id_user_cod,
                HttpServletRequest request, 
                HttpServletResponse response, 
                Model model)
            throws ServletException, IOException, URISyntaxException, DocumentException, Exception {
        
        HashMap<String, String> userDat = new HashMap<String, String>();
        //List<HashMap<String, String>> lista_compras;
        ArrayList<HashMap<String, String>> lista_compras = new ArrayList<HashMap<String, String>>();
        
        System.out.println("Generando Pdf de Compras Netas por Producto");
        
        //decodificar id de usuario
        Integer id_usuario = Integer.parseInt(Base64Coder.decodeString(id_user_cod));
        //System.out.println("id_usuario: "+id_usuario);
        
        userDat = this.getHomeDao().getUserById(id_usuario);
        Integer id_empresa = Integer.parseInt(userDat.get("empresa_id"));
        
        
        Integer tipo_reporte=Integer.parseInt(cadena.split("___")[0]);
        String proveedor=cadena.split("___")[1];
        String producto=cadena.split("___")[2];
        String fecha_inicial=cadena.split("___")[3];
        String fecha_final=cadena.split("___")[4];
        
        String razon_social_empresa = this.getGralDao().getRazonSocialEmpresaEmisora(id_empresa);
        
        //obtener el directorio temporal
        String dir_tmp = this.getGralDao().getTmpDir();
        
        String[] array_company = razon_social_empresa.split(" ");
        String company_name= array_company[0].toLowerCase();
        String ruta_imagen = this.getGralDao().getImagesDir() +"logo_"+ company_name +".png";
        
        System.out.println("ruta_imagen: "+ruta_imagen);
        
        File file_dir_tmp = new File(dir_tmp);
        System.out.println("Directorio temporal: "+file_dir_tmp.getCanonicalPath());
        
        String file_name="";
        //TIPO_REPORTE  
        // 1: por Producto
        // 2: por Por Proveedor
        // 3: ++ por Producto
        // 4: ++ por Proveedor
        
        if(tipo_reporte == 1){
                file_name = "compras_producto.pdf"; //compras netas pr producto
        }
        if(tipo_reporte == 2){
                file_name = "compras_producto_x_proveedor.pdf"; // Compra Netas por Proveedor
        }
        if(tipo_reporte == 3){
                file_name = "compras_producto Sumarizado_x_producto.pdf";
        }
        if(tipo_reporte == 4){
                file_name = "compras_producto sumarizado_x_proveedor.pdf";
        }
        //ruta de archivo de salida
        String fileout = file_dir_tmp +"/"+  file_name;
        
        
        //obtiene los datos del reporte
        lista_compras = this.getInvDao().getDatos_ReporteComprasNetasProducto(tipo_reporte,proveedor, producto, fecha_inicial,fecha_final,id_empresa);
       
        if(tipo_reporte == 1 || tipo_reporte == 2){
                //instancia a la clase que construye el pdf de la del reporte de Compras Netas
                PdfInvComprasNetasProducto x = new PdfInvComprasNetasProducto(fileout, ruta_imagen, razon_social_empresa, fecha_inicial,fecha_final,  lista_compras, tipo_reporte);
        }
        
        if(tipo_reporte == 3 || tipo_reporte == 4){
                PdfInvComprasNetas_Sumarizados_xproducto_xproveedor y = new PdfInvComprasNetas_Sumarizados_xproducto_xproveedor(lista_compras,producto,fecha_inicial,fecha_final,razon_social_empresa,fileout,tipo_reporte);
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
