/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.agnux.kemikal.controllers;

import com.agnux.cfd.v2.Base64Coder;
import com.agnux.common.helpers.TimeHelper;
import com.agnux.common.obj.ResourceProject;
import com.agnux.common.obj.UserSessionData;
import com.agnux.kemikal.interfacedaos.ComInterfaceDao;
import com.agnux.kemikal.interfacedaos.GralInterfaceDao;
import com.agnux.kemikal.interfacedaos.HomeInterfaceDao;
import com.agnux.kemikal.reportes.*;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

/**
 *
 * @author luis
 * @fecha 16/11/2012
 */
@Controller
@SessionAttributes({"user"})
@RequestMapping("/repestadisticocompras/")
public class RepEstadisticoComprasController {
    private static final Logger log =Logger.getLogger(RepEstadisticoComprasController.class.getName());
    ResourceProject resource = new ResourceProject();

    @Autowired
    @Qualifier("daoCom")
    private ComInterfaceDao comDao;

    @Autowired
    @Qualifier("daoHome")
    private HomeInterfaceDao HomeDao;

    @Autowired
    @Qualifier("daoGral")
    private GralInterfaceDao gralDao;

    public ComInterfaceDao getComDao() {
        return (ComInterfaceDao) comDao;
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

        log.log(Level.INFO, "Ejecutando starUp de {0}", RepEstadisticoComprasController.class.getName());
        LinkedHashMap<String,String> infoConstruccionTabla = new LinkedHashMap<String,String>();

        ModelAndView x = new ModelAndView("repestadisticocompras/startup", "title", "EstadisticasAnualesCompras");

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

    @RequestMapping(method = RequestMethod.POST,value="/getEstadisticoCompras/out.json")
    public @ResponseBody ArrayList<HashMap<String, String>> getEstadisticoCompras(
        @RequestParam(value="tipo_reporte", required=true) Integer tipo_reporte,
        @RequestParam(value="proveedor", required=true) String proveedor,
        @RequestParam(value="producto", required=true) String producto,
        @RequestParam(value="fecha_inicial", required=true) String fecha_inicial,
        @RequestParam(value="fecha_final", required=true) String fecha_final,
        @RequestParam(value="iu", required=true) String id_user_cod,
        Model model

        ){
        log.log(Level.INFO, "Ejecutando getEstadisticoCompras de {0}", RepEstadisticoComprasController.class.getName());
        HashMap<String,ArrayList<HashMap<String, String>>> jsonretorno = new HashMap<String,ArrayList<HashMap<String, String>>>();
        ArrayList<HashMap<String, String>> lista_compras = new ArrayList<HashMap<String, String>>();
        HashMap<String, String> userDat = new HashMap<String, String>();

        //decodificar id de usuario
        Integer id_usuario = Integer.parseInt(Base64Coder.decodeString(id_user_cod));
        System.out.println("id_usuario: "+id_usuario);

        userDat = this.getHomeDao().getUserById(id_usuario);

        Integer id_empresa = Integer.parseInt(userDat.get("empresa_id"));

        jsonretorno.put("Compras", lista_compras);

        ArrayList<HashMap<String, String>> z = this.getComDao().getEstadisticoCompras(tipo_reporte, proveedor, producto, fecha_inicial, fecha_final, id_empresa);
        return z;
    }


    //obtiene los proveedores para el buscador
    @RequestMapping(method = RequestMethod.POST, value="/getBuscaProveedores.json")
    public @ResponseBody HashMap<String,ArrayList<HashMap<String, String>>> getBuscaProveedoresJson(
            @RequestParam(value="rfc", required=true) String rfc,
            @RequestParam(value="no_proveedor", required=true) String no_proveedor,
            @RequestParam(value="nombre", required=true) String nombre,
            @RequestParam(value="iu", required=true) String id_user,
            Model model
            ) {

        log.log(Level.INFO, "Ejecutando getBuscaProveedoresJson de {0}", CxpRepAntiguedadSaldosController.class.getName());
        HashMap<String,ArrayList<HashMap<String, String>>> jsonretorno = new HashMap<String,ArrayList<HashMap<String, String>>>();
        ArrayList<HashMap<String, String>> proveedores = new ArrayList<HashMap<String, String>>();
        HashMap<String, String> userDat = new HashMap<String, String>();
        //decodificar id de usuario
        Integer id_usuario = Integer.parseInt(Base64Coder.decodeString(id_user));
        userDat = this.getHomeDao().getUserById(id_usuario);

        Integer id_empresa = Integer.parseInt(userDat.get("empresa_id"));

        proveedores = this.getComDao().getBuscadorProveedores(rfc, no_proveedor, nombre,id_empresa);

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
        arrayTiposProducto=this.getComDao().getProductoTipos();
        jsonretorno.put("prodTipos", arrayTiposProducto);

        return jsonretorno;
    }



    //Buscador de productos
    @RequestMapping(method = RequestMethod.POST, value="/get_buscador_productos.json")
    public @ResponseBody HashMap<String,ArrayList<HashMap<String, String>>> get_buscador_productosJson(
            @RequestParam(value="sku", required=true) String sku,
            @RequestParam(value="tipo", required=true) String tipo,
            @RequestParam(value="descripcion", required=true) String descripcion,
            @RequestParam(value="iu", required=true) String id_user,
            Model model
            ) {

        HashMap<String,ArrayList<HashMap<String, String>>> jsonretorno = new HashMap<String,ArrayList<HashMap<String, String>>>();
        HashMap<String, String> userDat = new HashMap<String, String>();

        //decodificar id de usuario
        Integer id_usuario = Integer.parseInt(Base64Coder.decodeString(id_user));

        userDat = this.getHomeDao().getUserById(id_usuario);
        Integer id_empresa = Integer.parseInt(userDat.get("empresa_id"));

        jsonretorno.put("productos", this.getComDao().getBuscadorProductos(sku,tipo,descripcion, id_empresa));
        return jsonretorno;
    }
    //fin del buscador de productos

   //************************************************************************************************************
    //PDF DE ESTADISTICO DE COMPRAS

     @RequestMapping(value = "/getrepestadisticocompras/{cadena}/out.json", method = RequestMethod.GET )
     public ModelAndView PdfVentasNetasProductoFactura(
                @PathVariable("cadena") String cadena,
                HttpServletRequest request,
                HttpServletResponse response,
                Model model)
     throws ServletException, IOException, URISyntaxException, DocumentException {
          //       0                   1              2               3                  4                5
    //cadena = tipo_reporte+"___"+proveedor+"___"+producto+"___"+fecha_inicial+"___"+fecha_final+"___"+usuario
     String arreglo[];
     arreglo = cadena.split("___");


     HashMap<String, String> userDat = new HashMap<String, String>();

        System.out.println("Generando reporte de Estadistico de Compras por producto factura");

        //decodificar id de usuario
        Integer id_usuario = Integer.parseInt(Base64Coder.decodeString(arreglo[5]));
        System.out.println("id_usuario: "+id_usuario);

        userDat = this.getHomeDao().getUserById(id_usuario);
        Integer id_empresa = Integer.parseInt(userDat.get("empresa_id"));

        String razon_social_empresa = this.getGralDao().getRazonSocialEmpresaEmisora(id_empresa);

        //obtener el directorio temporal
        String dir_tmp = this.getGralDao().getTmpDir();


        String[] array_company = razon_social_empresa.split(" ");
        String company_name= array_company[0].toLowerCase();
        //String ruta_imagen = this.getPgdao().getImagesDir() +"logo_"+ company_name +".png";


        File file_dir_tmp = new File(dir_tmp);
        System.out.println("Directorio temporal: "+file_dir_tmp.getCanonicalPath());
        String file_name = "";
        if(Integer.parseInt(arreglo[0]) == 1){//por producto
            file_name = "Estadistico_de_Compras_x_Producto.pdf";
        }
        if(Integer.parseInt(arreglo[0]) == 4){//sumarizado por proveedor
            file_name = "Estadistico_de_Compras_sumarizado_x_Proveedor.pdf";
        }
        if(Integer.parseInt(arreglo[0]) == 2){// por proveedor
            file_name = "Estadistico_de_Compras_x_Proveedor.pdf";
        }

         if(Integer.parseInt(arreglo[0]) == 3){//sumarizado por producto
            file_name = "Estadistico_de_Compras_sumarizado_x_Producto.pdf";
        }
        //ruta de archivo de salida
        String fileout = file_dir_tmp +"/"+  file_name;
        //String fileout = "C:\\Users\\micompu\\Desktop\\mi reporte de proveedores sumarizados.pdf";
        ArrayList<HashMap<String, String>> lista_ventasporproducto = new ArrayList<HashMap<String, String>>();

        //obtiene los informacion de compras  del periodo indicado
        lista_ventasporproducto = this.getComDao().getEstadisticoCompras(Integer.parseInt(arreglo[0]), arreglo[1],arreglo[2], arreglo[3], arreglo[4], id_empresa);

        //[0]tipo_reporte+"___"+[1]proveedor+"___"+[2]producto+"___"+[3]fecha_inicial+"___"+[4]fecha_final+"___"+[5]usuario
        if(Integer.parseInt(arreglo[0])==1){
            //instancia a la clase que construye el pdf  del reporte estadistico de compras por proveedor
            PdfEstadisticoComprasProducto x = new PdfEstadisticoComprasProducto(lista_ventasporproducto,arreglo[3],arreglo[4],razon_social_empresa,fileout);
        }
        if(Integer.parseInt(arreglo[0])==2){
            //instancia a la clase que construye el pdf estadistico compras por Producto
           // PdfReporteVentasNetasProductoFacturados x = new PdfReporteVentasNetasProductoFacturados(lista_ventasporproducto,arreglo[3],arreglo[4],razon_social_empresa,fileout);
           //instancia a la clase que construye el pdf estadistico compras por proveedor
            PdfEstadisticocomprasxProveedor x = new PdfEstadisticocomprasxProveedor(lista_ventasporproducto,arreglo[3],arreglo[4],razon_social_empresa,fileout);

        }
         if(Integer.parseInt(arreglo[0])==3){
            //instancia a la clase que construye el pdf estadistico compras Sumarizado por Producto
            //PdfReporteVentasNetasSumatoriaxProducto x = new PdfReporteVentasNetasSumatoriaxProducto(lista_ventasporproducto,arreglo[1],arreglo[3],arreglo[4],razon_social_empresa,fileout);
                                                                                                    //lista_ventas, proveedor,fecha_inicial,fecha_final, razon_social_empresa, fileout)
           PdfEstadisticoComprasProductoSumarizado x = new    PdfEstadisticoComprasProductoSumarizado(lista_ventasporproducto,arreglo[1],arreglo[3],arreglo[4],razon_social_empresa,fileout);
        }
        if(Integer.parseInt(arreglo[0])==4){
            //instancia a la clase que construye el pdf estadistico compras Sumarizado por Proveedor
            PdfEstadisticoComprasProveedorSumarizado x = new PdfEstadisticoComprasProveedorSumarizado(lista_ventasporproducto,arreglo[2],arreglo[3],arreglo[4],razon_social_empresa,fileout);
        }






        //System.out.println("Recuperando archivo: " + fileout);
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
