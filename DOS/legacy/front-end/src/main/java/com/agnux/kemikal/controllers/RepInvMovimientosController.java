/*
 * Reporte de Movimientos de (KARDEX)
 */
package com.agnux.kemikal.controllers;
import com.agnux.cfd.v2.Base64Coder;
import com.agnux.common.helpers.FileHelper;
import com.agnux.common.obj.ResourceProject;
import com.agnux.common.obj.UserSessionData;
import com.agnux.kemikal.interfacedaos.GralInterfaceDao;
import com.agnux.kemikal.interfacedaos.HomeInterfaceDao;
import com.agnux.kemikal.interfacedaos.InvInterfaceDao;
import com.agnux.kemikal.reportes.PdfReporteMovimientos;
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
@RequestMapping("/repinvmovimientos/")
public class RepInvMovimientosController {
    private static final Logger log  = Logger.getLogger(RepInvMovimientosController.class.getName());
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

        log.log(Level.INFO, "Ejecutando starUp de {0}", RepInvMovimientosController.class.getName());
        LinkedHashMap<String,String> infoConstruccionTabla = new LinkedHashMap<String,String>();


        ModelAndView x = new ModelAndView("repinvmovimientos/startup", "title", "Movimientos de Inventario");

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




    //obtiene datos para el buscador
    @RequestMapping(method = RequestMethod.POST, value="/getDatosBusqueda.json")
    public @ResponseBody HashMap<String,ArrayList<HashMap<String, String>>> getDatosBusquedaJson(
            @RequestParam(value="iu", required=true) String id_user,
            Model model
        ) {

        log.log(Level.INFO, "Ejecutando getDatosBusquedaJson de {0}", RepInvMovimientosController.class.getName());
        HashMap<String,ArrayList<HashMap<String, String>>> jsonretorno = new HashMap<String,ArrayList<HashMap<String, String>>>();
        ArrayList<HashMap<String, String>> Almacenes = new ArrayList<HashMap<String, String>>();
        ArrayList<HashMap<String, String>> TiposMovimiento = new ArrayList<HashMap<String, String>>();
        HashMap<String, String> userDat = new HashMap<String, String>();

        //decodificar id de usuario
        Integer id_usuario = Integer.parseInt(Base64Coder.decodeString(id_user));
        userDat = this.getHomeDao().getUserById(id_usuario);

        Integer id_empresa = Integer.parseInt(userDat.get("empresa_id"));

        Almacenes = this.getInvDao().getAlmacenes2(id_empresa);
        TiposMovimiento = this.getInvDao().getAllTiposMovimientoInventario(id_empresa);

        jsonretorno.put("Almacenes", Almacenes);
        jsonretorno.put("Tmov", TiposMovimiento);

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
        arrayTiposProducto = this.getInvDao().getProducto_Tipos();
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

        productos = this.getInvDao().getBuscadorProductos(sku,tipo ,descripcion, id_empresa);

        jsonretorno.put("Productos", productos);

        return jsonretorno;
    }




    @RequestMapping(method = RequestMethod.POST, value = "/getMovimientos.json")
    public @ResponseBody
    HashMap<String, ArrayList<HashMap<String, String>>> getMovimientosJson(
            @RequestParam(value = "id_tipo_movimiento", required = true)Integer id_tipo_movimiento ,
            @RequestParam(value = "id_almacen", required = true) Integer id_almacen,
            @RequestParam(value = "codigo", required = true) String codigo,
            @RequestParam(value = "descripcion", required = true) String descripcion,
            @RequestParam(value = "fecha_inicial", required = true) String fecha_inicial,
            @RequestParam(value = "fecha_final", required = true) String fecha_final,
            @RequestParam(value = "iu", required = true) String id_user,
            Model model) {

        log.log(Level.INFO, "Ejecutando getMovimientosJson de {0}", RepInvMovimientosController.class.getName());
        HashMap<String, ArrayList<HashMap<String, String>>> jsonretorno = new HashMap<String, ArrayList<HashMap<String, String>>>();
        ArrayList<HashMap<String, String>> Movimientos = new ArrayList<HashMap<String, String>>();
        HashMap<String, String> userDat = new HashMap<String, String>();
        //decodificar id de usuario
        Integer id_usuario = Integer.parseInt(Base64Coder.decodeString(id_user));
        userDat = this.getHomeDao().getUserById(id_usuario);

        Integer id_empresa = Integer.parseInt(userDat.get("empresa_id"));

        Movimientos = this.getInvDao().getMovimientos(id_tipo_movimiento,id_almacen,codigo ,descripcion,fecha_inicial,fecha_final, id_empresa,id_usuario);
        
        jsonretorno.put("Movimientos", Movimientos);
        
        return jsonretorno;
    }


    //Genera pdf Reporte de Movimientos
    @RequestMapping(value = "/getReporteMovimientos/{cadena}/{iu}/out.json", method = RequestMethod.GET )
    public ModelAndView getGeneraPdfRemisionJson(
                @PathVariable("cadena") String cadena,
                @PathVariable("iu") String id_user,
                HttpServletRequest request,
                HttpServletResponse response,
                Model model)
        throws ServletException, IOException, URISyntaxException, DocumentException {

        HashMap<String, String> userDat = new HashMap<String, String>();
        ArrayList<HashMap<String, String>> lista_movimientos = new ArrayList<HashMap<String, String>>();

        System.out.println("Generando reporte de Movimientos");
        Integer select_tipo_movimiento=0;
        Integer select_almacen=0;
        String codigo="";
        String descripcion="";
        String fecha_inicial="";
        String fecha_final="";


        String arrayCad [] = cadena.split("___");

        select_tipo_movimiento = Integer.parseInt(arrayCad [0]);
        select_almacen = Integer.parseInt(arrayCad [1]);
        if(arrayCad [2].equals("0")){
            codigo="";
        }else{
            codigo = arrayCad [2];
        }

        if(arrayCad [3].equals("0")){
            descripcion="";
        }else{
            descripcion = arrayCad [3];
        }

       if(arrayCad [4].equals("0")){
            fecha_inicial="%%";
        }else{
            fecha_inicial = arrayCad [4];
        }
        if(arrayCad [5].equals("0")){
            fecha_final="%%";
        }else{
            fecha_final = arrayCad [5];
        }

        //decodificar id de usuario
        Integer id_usuario = Integer.parseInt(Base64Coder.decodeString(id_user));
        userDat = this.getHomeDao().getUserById(id_usuario);
        Integer id_empresa = Integer.parseInt(userDat.get("empresa_id"));
        String rfc=this.getGralDao().getRfcEmpresaEmisora(id_empresa);

        String razon_social_empresa = this.getGralDao().getRazonSocialEmpresaEmisora(id_empresa);

        //obtener el directorio temporal
        String dir_tmp = this.getGralDao().getTmpDir();


        String[] array_company = razon_social_empresa.split(" ");
        String company_name= array_company[0].toLowerCase();
        String ruta_imagen = this.getGralDao().getImagesDir() +"logo_"+ company_name +".png";


        File file_dir_tmp = new File(dir_tmp);
        //System.out.println("Directorio temporal: "+file_dir_tmp.getCanonicalPath());

        String file_name = "REPMOVIMIENTOS_"+rfc+"_"+fecha_inicial+"_"+fecha_final+".pdf";

        //ruta de archivo de salida
        String fileout = file_dir_tmp +"/"+  file_name;

        //variable estatus agregado por paco
        //obtiene las remisiones del periodo indicado
        lista_movimientos = this.getInvDao().getMovimientos(select_tipo_movimiento,select_almacen,codigo ,descripcion,fecha_inicial,fecha_final, id_empresa,id_usuario);

        //instancia a la clase que construye el pdf del reporte de facturas
        PdfReporteMovimientos x = new PdfReporteMovimientos( fileout,ruta_imagen,razon_social_empresa,fecha_inicial,fecha_final,lista_movimientos);

        //System.out.println("Recuperando archivo: " + fileout);
        File file = new File(fileout);
        int size = (int) file.length(); // Tamaño del archivo
        BufferedInputStream bis = new BufferedInputStream(new FileInputStream(file));
        response.setBufferSize(size);
        response.setContentLength(size);
        response.setContentType("application/pdf");
        response.setHeader("Content-Disposition","attachment; filename=\"" + file.getName() +"\"");
        FileCopyUtils.copy(bis, response.getOutputStream());
        response.flushBuffer();
        try {
            FileHelper.delete(file_name);
        } catch (Exception ex) {
            Logger.getLogger(RepInvMovimientosController.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }
}
