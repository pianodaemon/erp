package com.agnux.kemikal.controllers;

import com.agnux.cfd.v2.Base64Coder;
import com.agnux.common.helpers.FileHelper;
import com.agnux.common.helpers.TimeHelper;
import com.agnux.common.obj.ResourceProject;
import com.agnux.common.obj.UserSessionData;
import com.agnux.kemikal.interfacedaos.CtbInterfaceDao;
import com.agnux.kemikal.interfacedaos.GralInterfaceDao;
import com.agnux.kemikal.interfacedaos.HomeInterfaceDao;
import com.agnux.kemikal.reportes.CtbPdfReporteBalanceGeneral;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;


@Controller
@SessionAttributes({"user"})
@RequestMapping("/ctbrepestadoresult/")
public class CtbRepEstadoResultadosController {
    ResourceProject resource = new ResourceProject();
    private static final Logger log  = Logger.getLogger(CtbRepEstadoResultadosController.class.getName());
    
    @Autowired
    @Qualifier("daoCtb")
    private CtbInterfaceDao ctbDao;
    
    @Autowired
    @Qualifier("daoHome")
    private HomeInterfaceDao HomeDao;
    
    @Autowired
    @Qualifier("daoGral")
    private GralInterfaceDao gralDao;
    
    public CtbInterfaceDao getCtbDao() {
        return ctbDao;
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
        
        log.log(Level.INFO, "Ejecutando starUp de {0}", CtbRepEstadoResultadosController.class.getName());
        LinkedHashMap<String,String> infoConstruccionTabla = new LinkedHashMap<String,String>();
        ModelAndView x = new ModelAndView("ctbrepestadoresult/startup", "title", "Reporte de Estado de Resultados");
        
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
    
    
    //Cargar año y mes actual
   @RequestMapping(method = RequestMethod.POST, value="/getDatos.json")
        public @ResponseBody HashMap<String,Object> getDatosJson(
        @RequestParam(value="iu", required=true) String id_user,
        Model model
    ){
        HashMap<String,Object> jsonretorno = new HashMap<String,Object>();
        HashMap<String, String> userDat = new HashMap<String, String>();
        
        //Decodificar id de usuario
        Integer id_usuario = Integer.parseInt(Base64Coder.decodeString(id_user));
        userDat = this.getHomeDao().getUserById(id_usuario);
        Integer id_empresa = Integer.parseInt(userDat.get("empresa_id"));
        
        jsonretorno.put("fecha", TimeHelper.getFechaActualYMD());
        jsonretorno.put("Suc", this.getCtbDao().getCtb_Sucursales(id_empresa));
        
        return jsonretorno;
    }
    
    
    //Obtiene datos para mostrar en el navegador
    @RequestMapping(method = RequestMethod.POST, value="/getDatosReporte.json")
    public @ResponseBody HashMap<String,ArrayList<HashMap<String, String>>> getDatosReporteJson(
            @RequestParam(value="suc", required=true) String suc,
            @RequestParam(value="fecha", required=true) String fecha,
            @RequestParam(value="iu", required=true) String id_user_cod,
            Model model
        ) {
        
        log.log(Level.INFO, "Ejecutando getDatosReporteJson de {0}", CtbRepEstadoResultadosController.class.getName());
        HashMap<String,ArrayList<HashMap<String, String>>> jsonretorno = new HashMap<String,ArrayList<HashMap<String, String>>>();
        
        //HashMap<String, String> userDat = new HashMap<String, String>();
        ArrayList<HashMap<String, String>> datos = new ArrayList<HashMap<String, String>>();
        
        //Reporte de Estado de Resultados(CTB)
        Integer app_selected = 161;
        String command_selected="reporte";
        String tipo_doc="nav";
        //Decodificar id de usuario
        Integer id_user = Integer.parseInt(Base64Coder.decodeString(id_user_cod));
        //userDat = this.getHomeDao().getUserById(id_user);
        //Integer id_empresa = Integer.parseInt(userDat.get("empresa_id"));
        
        String data_string = app_selected+"___"+id_user+"___"+command_selected+"___"+tipo_doc+"___"+suc+"___"+fecha;
        
        //Obtiene datos del Reporte Auxiliar de Cuentas
        datos = this.getCtbDao().getCtbRepBalanceGral_Datos(data_string);
        
        jsonretorno.put("Data", datos);
        
        return jsonretorno;
    }
    
    
    //Genera pdf Reporte Auxiliar de Cuentas
    @RequestMapping(value = "/getPdfRepBalanceGeneral/{cadena}/{iu}/out.json", method = RequestMethod.GET )
    public ModelAndView getGeneraPdfRepBalanceGeneralJson(
                @PathVariable("cadena") String cadena,
                @PathVariable("iu") String id_user_cod,
                HttpServletRequest request,
                HttpServletResponse response,
                Model model)
        throws ServletException, IOException, URISyntaxException, DocumentException, Exception {
        
        HashMap<String, String> userDat = new HashMap<String, String>();
        HashMap<String, String> datosEmpresaEmisora= new HashMap<String, String>();
        HashMap<String, String> datosEncabezadoPie= new HashMap<String, String>();
        ArrayList<HashMap<String, String>> datos = new ArrayList<HashMap<String, String>>();
        
        System.out.println("Generando Reporte de Balance General");
        
        //Reporte de Estado de Resultados(CTB)
        Integer app_selected = 161;
        String command_selected="reporte";
        String tipo_doc="pdf";
        
        //Decodificar id de usuario
        Integer id_user = Integer.parseInt(Base64Coder.decodeString(id_user_cod));
        userDat = this.getHomeDao().getUserById(id_user);
        Integer id_empresa = Integer.parseInt(userDat.get("empresa_id"));
        
        String arrayCad [] = cadena.split("___");
        String suc=arrayCad[0];
        String fecha=arrayCad[1];
        
        String data_string = app_selected+"___"+id_user+"___"+command_selected+"___"+tipo_doc+"___"+suc+"___"+fecha;
        
        //Obtiene datos de la Empresa Emisora
        datosEmpresaEmisora = this.getGralDao().getEmisor_Datos(id_empresa);
        datosEmpresaEmisora.put("regedo", "");
        
        String[] fi = fecha.split("-");
        String periodo_reporte = "Del 01/01/"+fi[0]+" al "+fi[2]+"/"+fi[1]+"/"+fi[0];
        
        //Crear cadena para imprimir Fecha en el pie de pagina del PDF.
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        String fecha_impresion = sdf.format(new Date());
        
        //Agregar datos para el Encabezado y Pie de pagina
        datosEncabezadoPie.put("empresa", datosEmpresaEmisora.get("emp_razon_social"));
        //datosEncabezadoPie.put("titulo_reporte", this.getGralDao().getTituloReporte(id_empresa, app_selected));
        datosEncabezadoPie.put("titulo_reporte", "Estado de Resultados");
        datosEncabezadoPie.put("periodo", periodo_reporte);
        datosEncabezadoPie.put("fecha_impresion", fecha_impresion);
        datosEncabezadoPie.put("codigo1", this.getGralDao().getCodigo1Iso(id_empresa, app_selected));
        datosEncabezadoPie.put("codigo2", this.getGralDao().getCodigo2Iso(id_empresa, app_selected));
        
        //Obtener el directorio temporal
        String dir_tmp = this.getGralDao().getTmpDir();
        File file_dir_tmp = new File(dir_tmp);
        
        String file_name = "EstadoDeResultadosDel_01-01-"+fi[0]+"_al_"+fi[2]+"-"+fi[1]+"-"+fi[0]+".pdf";
        
        //Ruta de archivo de salida
        String fileout = file_dir_tmp +"/"+  file_name;
        
        datos = this.getCtbDao().getCtbRepBalanceGral_Datos(data_string);
        
        //Utiliza el mismo PDF de Balance General
        CtbPdfReporteBalanceGeneral x = new CtbPdfReporteBalanceGeneral(fileout, datosEncabezadoPie, datosEmpresaEmisora, datos);
        
        System.out.println("Recuperando archivo:" + fileout);
        File file = new File(fileout);
        int size = (int) file.length(); // Tamaño del archivo
        BufferedInputStream bis = new BufferedInputStream(new FileInputStream(file));
        response.setBufferSize(size);
        response.setContentLength(size);
        response.setContentType("application/pdf");
        response.setHeader("Content-Disposition","attachment; filename=\"" + file.getName() +"\"");
        FileCopyUtils.copy(bis, response.getOutputStream());
        response.flushBuffer();
        
        if(file.exists()){
            FileHelper.delete(fileout);
        }
        
        return null;
    }  
}
