package com.agnux.kemikal.controllers;

import com.agnux.cfd.v2.Base64Coder;
import com.agnux.common.obj.ResourceProject;
import com.agnux.common.obj.UserSessionData;
import com.agnux.kemikal.interfacedaos.GralInterfaceDao;
import com.agnux.kemikal.interfacedaos.HomeInterfaceDao;
import com.agnux.kemikal.interfacedaos.ProInterfaceDao;
import com.agnux.kemikal.reportes.Pdf_PRO_Produccion_diaria;
import com.agnux.kemikal.reportes.Pdf_PRO_Produccion_por_equipo;
import com.agnux.kemikal.reportes.Pdf_PRO_Produccion_por_operario;
import com.agnux.kemikal.reportes.Pdf_PRO_Produccion_por_producto;
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
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.servlet.ModelAndView;

/**
 * /**
 *
 * @author vale8490 lunes 4 de marzo del 2013 Este controller es para generar el
 * reporte de visitas
 */
@Controller
@SessionAttributes({"user"})
@RequestMapping("/proreporteproduccion/")
public class ProPdfReporteProduccionController {

    private static final Logger log = Logger.getLogger(ProPdfReporteProduccionController.class.getName());
    ResourceProject resource = new ResourceProject();
    @Autowired
    @Qualifier("daoPro")
    private ProInterfaceDao ProDao;

    public ProInterfaceDao getProDao() {
        return ProDao;
    }

    public void setProDao(ProInterfaceDao ProDao) {
        this.ProDao = ProDao;
    }
    @Autowired
    @Qualifier("daoHome")
    private HomeInterfaceDao HomeDao;
    @Autowired
    @Qualifier("daoGral")
    private GralInterfaceDao gralDao;

    public HomeInterfaceDao getHomeDao() {
        return HomeDao;
    }

    public GralInterfaceDao getGralDao() {
        return gralDao;
    }

    @RequestMapping(value = "/startup.agnux")
    public ModelAndView startUp(HttpServletRequest request, HttpServletResponse response,
            @ModelAttribute("user") UserSessionData user)
            throws ServletException, IOException {

        log.log(Level.INFO, "Ejecutando starUp de {0}", ProPdfReporteProduccionController.class.getName());
        LinkedHashMap<String, String> infoConstruccionTabla = new LinkedHashMap<String, String>();


        ModelAndView x = new ModelAndView("proreporteproduccion/startup", "title", "Reporte Produccion");

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

    @RequestMapping(method = RequestMethod.POST, value = "/getProduccion.json")
    public @ResponseBody
    HashMap<String, ArrayList<HashMap<String, String>>> getProduccionJson(
            @RequestParam(value = "id_operario", required = true) Integer id_operario,
            @RequestParam(value = "id_equipo", required = true) Integer id_equipo,
            @RequestParam(value = "tipo_reporte", required = true) Integer tipo_reporte,
            @RequestParam(value = "fecha_inicial", required = true) String fecha_inicial,
            @RequestParam(value = "fecha_final", required = true) String fecha_final,
            @RequestParam(value = "sku", required = true) String sku,
            @RequestParam(value = "sku_descripcion", required = true) String sku_descripcion,
            @RequestParam(value = "iu", required = true) String id_user,
            Model model) {

        log.log(Level.INFO, "Ejecutando getProduccionJson de {0}", ProPdfReporteProduccionController.class.getName());
        HashMap<String, ArrayList<HashMap<String, String>>> jsonretorno = new HashMap<String, ArrayList<HashMap<String, String>>>();

        HashMap<String, String> userDat = new HashMap<String, String>();
        ArrayList<HashMap<String, String>> datos_produccion_diaria = new ArrayList<HashMap<String, String>>();
        ArrayList<HashMap<String, String>> datos_produccion_por_producto= new ArrayList<HashMap<String, String>>();



        //decodificar id de usuario
        Integer id_usuario = Integer.parseInt(Base64Coder.decodeString(id_user));
        userDat = this.getHomeDao().getUserById(id_usuario);
        Integer id_empresa = Integer.parseInt(userDat.get("empresa_id"));





        if(tipo_reporte == 1){
            datos_produccion_diaria = this.getProDao().getProduccion(fecha_inicial, fecha_final, sku, sku_descripcion,id_equipo,id_operario,tipo_reporte ,id_empresa);
            jsonretorno.put("Datos", datos_produccion_diaria);
        }
        if(tipo_reporte == 2){
            datos_produccion_por_producto= this.getProDao().getProduccion_por_producto(fecha_inicial, fecha_final, sku, sku_descripcion,id_equipo,id_operario,tipo_reporte ,id_empresa);
            jsonretorno.put("Datos", datos_produccion_por_producto);
        }

         if(tipo_reporte == 3){
            datos_produccion_por_producto= this.getProDao().getProduccion_por_equipo(fecha_inicial, fecha_final, sku, sku_descripcion,id_equipo,id_operario,tipo_reporte ,id_empresa);
            jsonretorno.put("Datos", datos_produccion_por_producto);
        }

          if(tipo_reporte == 4){
            datos_produccion_por_producto= this.getProDao().getProduccion_por_operario(fecha_inicial, fecha_final, sku, sku_descripcion,id_equipo,id_operario,tipo_reporte ,id_empresa);
            jsonretorno.put("Datos", datos_produccion_por_producto);
        }

        return jsonretorno;
    }

    //obtiene los Operarios
    @RequestMapping(method = RequestMethod.POST, value = "/getOperarios.json")
    public @ResponseBody
    HashMap<String, ArrayList<HashMap<String, String>>> getOperariosJson(
            @RequestParam(value = "iu", required = true) String id_user,
            Model model) {

        log.log(Level.INFO, "Ejecutando getOperariosJson de {0}", InvRepComprasNetasPorProductoController.class.getName());
        HashMap<String, ArrayList<HashMap<String, String>>> jsonretorno = new HashMap<String, ArrayList<HashMap<String, String>>>();
        ArrayList<HashMap<String, String>> Operarios = new ArrayList<HashMap<String, String>>();
        HashMap<String, String> userDat = new HashMap<String, String>();
        //decodificar id de usuario
        Integer id_usuario = Integer.parseInt(Base64Coder.decodeString(id_user));
        userDat = this.getHomeDao().getUserById(id_usuario);

        Integer id_empresa = Integer.parseInt(userDat.get("empresa_id"));

        Operarios = this.getProDao().getOperarios(id_empresa);

        jsonretorno.put("Operarios", Operarios);

        return jsonretorno;
    }


    //obtiene los Equipos
    @RequestMapping(method = RequestMethod.POST, value = "/getEquipos.json")
    public @ResponseBody
    HashMap<String, ArrayList<HashMap<String, String>>> getEquiposJson(
            @RequestParam(value = "iu", required = true) String id_user,
            Model model) {

        log.log(Level.INFO, "Ejecutando getEquiposJson de {0}", InvRepComprasNetasPorProductoController.class.getName());
        HashMap<String, ArrayList<HashMap<String, String>>> jsonretorno = new HashMap<String, ArrayList<HashMap<String, String>>>();
        ArrayList<HashMap<String, String>> productos = new ArrayList<HashMap<String, String>>();
        HashMap<String, String> userDat = new HashMap<String, String>();
        //decodificar id de usuario
        Integer id_usuario = Integer.parseInt(Base64Coder.decodeString(id_user));
        userDat = this.getHomeDao().getUserById(id_usuario);

        Integer id_empresa = Integer.parseInt(userDat.get("empresa_id"));

        productos = this.getProDao().getTiposEquipos(id_empresa);

        jsonretorno.put("Equipos", productos);

        return jsonretorno;
    }

    //obtiene los tipos de productos para el buscador de productos
    @RequestMapping(method = RequestMethod.POST, value = "/getProductoTipos.json")
    public @ResponseBody
    HashMap<String, ArrayList<HashMap<String, String>>> getProductoTiposJson(
            @RequestParam(value = "iu", required = true) String id_user_cod,
            Model model) {

        HashMap<String, ArrayList<HashMap<String, String>>> jsonretorno = new HashMap<String, ArrayList<HashMap<String, String>>>();

        ArrayList<HashMap<String, String>> arrayTiposProducto = new ArrayList<HashMap<String, String>>();
        arrayTiposProducto = this.getProDao().getProducto_Tipos_produccion();
        jsonretorno.put("prodTipos", arrayTiposProducto);

        return jsonretorno;
    }

    //obtiene los productos para el buscador
    @RequestMapping(method = RequestMethod.POST, value = "/getBuscadorProductos.json")
    public @ResponseBody
    HashMap<String, ArrayList<HashMap<String, String>>> getBuscadorProductosJson(
            @RequestParam(value = "sku", required = true) String sku,
            @RequestParam(value = "tipo", required = true) String tipo,
            @RequestParam(value = "descripcion", required = true) String descripcion,
            @RequestParam(value = "iu", required = true) String id_user,
            Model model) {
        
        log.log(Level.INFO, "Ejecutando getBuscadorProductosJson de {0}", InvRepComprasNetasPorProductoController.class.getName());
        HashMap<String, ArrayList<HashMap<String, String>>> jsonretorno = new HashMap<String, ArrayList<HashMap<String, String>>>();
        ArrayList<HashMap<String, String>> productos = new ArrayList<HashMap<String, String>>();
        HashMap<String, String> userDat = new HashMap<String, String>();
        //decodificar id de usuario
        Integer id_usuario = Integer.parseInt(Base64Coder.decodeString(id_user));
        userDat = this.getHomeDao().getUserById(id_usuario);
        
        Integer id_empresa = Integer.parseInt(userDat.get("empresa_id"));
        
        productos = this.getProDao().getBuscadorProductos_produccion(sku,tipo, descripcion, id_empresa);
        
        jsonretorno.put("Productos", productos);
        
        return jsonretorno;
    }

    //PDF reporte
    @RequestMapping(value = "/Crear_PDF_Produccion/{cadena}/{iu}/out.json", method = RequestMethod.GET)
    public ModelAndView Crear_PDF_produccion(
            @PathVariable("cadena") String cadena,
            @PathVariable("iu") String id_user,
            HttpServletRequest request,
            HttpServletResponse response,
            Model model)
            throws ServletException, IOException, URISyntaxException, DocumentException {

        String[] filtros = cadena.split("___");
        //String codigo_sku = "";
        //String descripcion_sku = "";
        //String fecha_inicial = filtros[0];
        //String fecha_final = filtros[1];

 Integer id_operario = Integer.parseInt(filtros[0]);
 Integer id_equipo = Integer.parseInt(filtros[1]);
 Integer tipo_reporte = Integer.parseInt(filtros[2]);
 String  fecha_inicial = filtros[3];
 String  fecha_final = filtros[4];
 String  codigo_sku = filtros[5];
 String  descripcion_sku = filtros[6];
 String iu = filtros[7];


        if (codigo_sku.equals("0")) {
            codigo_sku = "";
        } else {
            codigo_sku = filtros[5];
        }

        if (descripcion_sku.equals("0")) {
            descripcion_sku = "";
        } else {
            descripcion_sku = filtros[6];
        }


        HashMap<String, String> userDat = new HashMap<String, String>();
        String dir_tmp = this.getGralDao().getTmpDir();
        File file_dir_tmp = new File(dir_tmp);
        String file_name = "";
        String titulo_reporte = "";
        System.out.println("Generando Reportes de Produccion");

        ArrayList<HashMap<String, String>> Datos_Produccion = new ArrayList<HashMap<String, String>>();
        HashMap<String, String> datosEncabezadoPie = new HashMap<String, String>();
        HashMap<String, String> datos = new HashMap<String, String>();

        //decodificar id de usuario
        Integer id_usuario = Integer.parseInt(Base64Coder.decodeString(id_user));
        userDat = this.getHomeDao().getUserById(id_usuario);
        Integer id_empresa = Integer.parseInt(userDat.get("empresa_id"));
        //String rfc_empresa=this.getGralDao().getRfcEmpresaEmisora(id_empresa);
        String razon_social_empresa = this.getGralDao().getRazonSocialEmpresaEmisora(id_empresa);

        datosEncabezadoPie.put("nombre_empresa_emisora", razon_social_empresa);

        datosEncabezadoPie.put("codigo1", "pendiente A");
        datosEncabezadoPie.put("codigo2", "Pendiente B");
        datos.put("fecha_inicial", fecha_inicial);
        datos.put("fecha_final", fecha_final);
        if(tipo_reporte == 1){
               file_name = "RepProduccion Diaria  " + fecha_inicial + "al" + fecha_final + ".pdf";
        }
        if(tipo_reporte == 2){
               file_name = "RepProduccion por Producto " + fecha_inicial + "al" + fecha_final + ".pdf";
        }
        if(tipo_reporte == 3){
               file_name = "RepProduccion por Equipo " + fecha_inicial + "al" + fecha_final + ".pdf";
        }
        if(tipo_reporte == 4){
               file_name = "RepProduccion por Operario " + fecha_inicial + "al" + fecha_final + ".pdf";
        }
        //ruta de archivo de salida
        String fileout = file_dir_tmp + "/" + file_name;

        if(tipo_reporte== 1){

            titulo_reporte = "Reporte de Produccion Diaria";
            datosEncabezadoPie.put("titulo_reporte", titulo_reporte);

            Datos_Produccion = this.getProDao().getProduccion(fecha_inicial, fecha_final, codigo_sku, descripcion_sku,id_equipo,id_operario,tipo_reporte ,id_empresa);
            //instancia a la clase que construye el pdf
            Pdf_PRO_Produccion_diaria x = new Pdf_PRO_Produccion_diaria(datosEncabezadoPie, fileout, Datos_Produccion, datos);

        }
        if(tipo_reporte== 2){

            titulo_reporte = "Reporte de Produccion por Producto";
            datosEncabezadoPie.put("titulo_reporte", titulo_reporte);

            Datos_Produccion = this.getProDao().getProduccion_por_producto(fecha_inicial, fecha_final, codigo_sku, descripcion_sku,id_equipo,id_operario,tipo_reporte ,id_empresa);
            //instancia a la clase que construye el pdf
            Pdf_PRO_Produccion_por_producto x = new Pdf_PRO_Produccion_por_producto(datosEncabezadoPie, fileout, Datos_Produccion, datos);
        }

        if(tipo_reporte== 3){

            titulo_reporte = "Reporte de Produccion por Equipo";
            datosEncabezadoPie.put("titulo_reporte", titulo_reporte);

            Datos_Produccion = this.getProDao().getProduccion_por_equipo(fecha_inicial, fecha_final, codigo_sku, descripcion_sku,id_equipo,id_operario,tipo_reporte ,id_empresa);
            //instancia a la clase que construye el pdf
            Pdf_PRO_Produccion_por_equipo x = new Pdf_PRO_Produccion_por_equipo(datosEncabezadoPie, fileout, Datos_Produccion, datos);
        }
        if(tipo_reporte== 4){
            file_name = "RepProduccion por operario " + fecha_inicial + "al" + fecha_final + ".pdf";
            titulo_reporte = "Reporte de Produccion por Operario";
            datosEncabezadoPie.put("titulo_reporte", titulo_reporte);

            Datos_Produccion = this.getProDao().getProduccion_por_operario(fecha_inicial, fecha_final, codigo_sku, descripcion_sku,id_equipo,id_operario,tipo_reporte ,id_empresa);
            //instancia a la clase que construye el pdf
            Pdf_PRO_Produccion_por_operario x = new Pdf_PRO_Produccion_por_operario(datosEncabezadoPie, fileout, Datos_Produccion, datos);

        }


/*
        if(tipo_reporte== 1){
            Datos_Produccion = this.getProDao().getProduccion(fecha_inicial, fecha_final, codigo_sku, descripcion_sku,id_equipo,id_operario,tipo_reporte ,id_empresa);
            //instancia a la clase que construye el pdf
            Pdf_PRO_Produccion_diaria x = new Pdf_PRO_Produccion_diaria(datosEncabezadoPie, fileout, Datos_Produccion, datos);
        }
        if(tipo_reporte== 2){
            Datos_Produccion = this.getProDao().getProduccion_por_producto(fecha_inicial, fecha_final, codigo_sku, descripcion_sku,id_equipo,id_operario,tipo_reporte ,id_empresa);
            //instancia a la clase que construye el pdf
            Pdf_PRO_Produccion_por_producto x = new Pdf_PRO_Produccion_por_producto(datosEncabezadoPie, fileout, Datos_Produccion, datos);
        }
        if(tipo_reporte== 3){
            Datos_Produccion = this.getProDao().getProduccion_por_equipo(fecha_inicial, fecha_final, codigo_sku, descripcion_sku,id_equipo,id_operario,tipo_reporte ,id_empresa);
            //instancia a la clase que construye el pdf
            Pdf_PRO_Produccion_por_equipo x = new Pdf_PRO_Produccion_por_equipo(datosEncabezadoPie, fileout, Datos_Produccion, datos);
        }
        if(tipo_reporte== 4){
            Datos_Produccion = this.getProDao().getProduccion_por_operario(fecha_inicial, fecha_final, codigo_sku, descripcion_sku,id_equipo,id_operario,tipo_reporte ,id_empresa);
            //instancia a la clase que construye el pdf
            Pdf_PRO_Produccion_por_operario x = new Pdf_PRO_Produccion_por_operario(datosEncabezadoPie, fileout, Datos_Produccion, datos);
        }
        */
        System.out.println("Recuperando archivo: " + fileout);
        File file = new File(fileout);
        int size = (int) file.length(); // Tamaño del archivo
        BufferedInputStream bis = new BufferedInputStream(new FileInputStream(file));
        response.setBufferSize(size);
        response.setContentLength(size);
        response.setContentType("application/pdf");
        response.setHeader("Content-Disposition", "attachment; filename=\"" + file.getCanonicalPath() + "\"");
        FileCopyUtils.copy(bis, response.getOutputStream());
        response.flushBuffer();

        return null;

    }
}
