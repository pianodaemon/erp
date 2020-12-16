package com.agnux.kemikal.controllers;

import com.agnux.cfd.v2.Base64Coder;
import com.agnux.common.helpers.FileHelper;
import com.agnux.common.helpers.StringHelper;
import com.agnux.common.helpers.TimeHelper;
import com.agnux.common.obj.ResourceProject;
import com.agnux.common.obj.UserSessionData;
import com.agnux.kemikal.interfacedaos.CtbInterfaceDao;
import com.agnux.kemikal.interfacedaos.GralInterfaceDao;
import com.agnux.kemikal.interfacedaos.HomeInterfaceDao;
import com.agnux.kemikal.reportes.CtbPdfReporteBalanzaComprobacion;
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
@RequestMapping("/ctbrepbalanzacomp/")
public class CtbRepBalanzaComprobacionController {
    ResourceProject resource = new ResourceProject();
    private static final Logger log  = Logger.getLogger(CtbRepBalanzaComprobacionController.class.getName());
    
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
        
        log.log(Level.INFO, "Ejecutando starUp de {0}", CtbRepBalanzaComprobacionController.class.getName());
        LinkedHashMap<String,String> infoConstruccionTabla = new LinkedHashMap<String,String>();
        ModelAndView x = new ModelAndView("ctbrepbalanzacomp/startup", "title", "Reporte de Balanza de Comprobaci&oacute;n");
        
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
        public @ResponseBody HashMap<String,ArrayList<HashMap<String, Object>>> getDatosJson(
        @RequestParam(value="iu", required=true) String id_user,
        Model model
    ){
        HashMap<String,ArrayList<HashMap<String, Object>>> jsonretorno = new HashMap<String,ArrayList<HashMap<String, Object>>>();
        HashMap<String, Object> extra = new HashMap<String, Object>();
        ArrayList<HashMap<String, Object>> arrayExtra = new ArrayList<HashMap<String, Object>>();
        HashMap<String, String> userDat = new HashMap<String, String>();
        
        //Decodificar id de usuario
        Integer id_usuario = Integer.parseInt(Base64Coder.decodeString(id_user));
        userDat = this.getHomeDao().getUserById(id_usuario);
        Integer id_empresa = Integer.parseInt(userDat.get("empresa_id"));
        
        extra.put("nivel_cta", userDat.get("nivel_cta"));
        extra.put("mesActual", TimeHelper.getMesActual());
        extra.put("anioActual", TimeHelper.getFechaActualY());
        arrayExtra.add(0, extra);
        
        jsonretorno.put("Anios", this.getCtbDao().getCtbRepBalanzaComp_Anios());
        jsonretorno.put("Dato", arrayExtra);
        //Aqui solo nos interesa las subcuentas del nivel uno, por lo tanto le pasamos el numero 1
        jsonretorno.put("Cta", this.getCtbDao().getCtbRepBalanzaComp_Ctas(1,"","", "", "", id_empresa));
        jsonretorno.put("Suc", this.getCtbDao().getCtb_Sucursales(id_empresa));
        
        return jsonretorno;
    }
   
   
   
   //Obtener las subcuentas de acuerdo al nivel que se le indique en el parametro
   @RequestMapping(method = RequestMethod.POST, value="/getCtas.json")
    public @ResponseBody HashMap<String,ArrayList<HashMap<String, Object>>> getCtasJson(
        @RequestParam(value="cta", required=true) String cta,
        @RequestParam(value="scta", required=false) String scta,
        @RequestParam(value="sscta", required=false) String sscta,
        @RequestParam(value="ssscta", required=false) String ssscta,
        @RequestParam(value="nivel", required=true) Integer nivel,
        @RequestParam(value="iu", required=true) String id_user,
        Model model
    ){
        HashMap<String,ArrayList<HashMap<String, Object>>> jsonretorno = new HashMap<String,ArrayList<HashMap<String, Object>>>();
        HashMap<String, String> userDat = new HashMap<String, String>();
        ArrayList<HashMap<String, Object>> ctas = new ArrayList<HashMap<String, Object>>();
        
        //Decodificar id de usuario
        Integer id_usuario = Integer.parseInt(Base64Coder.decodeString(id_user));
        userDat = this.getHomeDao().getUserById(id_usuario);
        Integer id_empresa = Integer.parseInt(userDat.get("empresa_id"));
        /*
        //Verificar select por si vienen null
        cta = StringHelper.verificarSelect(cta);
        scta = StringHelper.verificarSelect(scta);
        sscta = StringHelper.verificarSelect(sscta);
        ssscta = StringHelper.verificarSelect(ssscta);
        */
        
        ctas = this.getCtbDao().getCtbRepBalanzaComp_Ctas(nivel, cta, scta, sscta, ssscta, id_empresa);
        
        jsonretorno.put("Cta", ctas);
        return jsonretorno;
    }
    
   
   //Obtiene datos para mostrar en el navegador
    @RequestMapping(method = RequestMethod.POST, value="/getDatosReporte.json")
    public @ResponseBody HashMap<String,Object> getDatosReporteJson(
            @RequestParam(value="suc", required=true) String suc,
            @RequestParam(value="fecha_ini", required=true) String fecha_ini,
            @RequestParam(value="fecha_fin", required=false) String fecha_fin,
            @RequestParam(value="cuentas", required=true) String cuentas,
            @RequestParam(value="cta", required=false) String cta,
            @RequestParam(value="scta", required=false) String scta,
            @RequestParam(value="sscta", required=false) String sscta,
            @RequestParam(value="ssscta", required=false) String ssscta,
            @RequestParam(value="sssscta", required=false) String sssscta,
            @RequestParam(value="iu", required=true) String id_user_cod,
            Model model
        ) {
        
        log.log(Level.INFO, "Ejecutando getDatosReporteJson de {0}", CtbRepBalanzaComprobacionController.class.getName());
        HashMap<String,Object> jsonretorno = new HashMap<String,Object>();
        
        //HashMap<String, String> userDat = new HashMap<String, String>();
        ArrayList<LinkedHashMap<String, String>> datos = new ArrayList<LinkedHashMap<String, String>>();
        HashMap<String, String> total = new HashMap<String, String>();
        
        //Reporte Balanza de Comprobacion
        Integer app_selected = 158;
        String command_selected="reporte";
        String tipo_doc="nav";
        //Decodificar id de usuario
        Integer id_user = Integer.parseInt(Base64Coder.decodeString(id_user_cod));
        //userDat = this.getHomeDao().getUserById(id_user);
        //Integer id_empresa = Integer.parseInt(userDat.get("empresa_id"));
        
        //Verificar valores por si viene null
        cta = StringHelper.verificarSelect(cta);
        scta = StringHelper.verificarSelect(scta);
        sscta = StringHelper.verificarSelect(sscta);
        ssscta = StringHelper.verificarSelect(ssscta);
        sssscta = StringHelper.verificarSelect(sssscta);
        
        String data_string = app_selected+"___"+id_user+"___"+command_selected+"___"+suc+"___"+fecha_ini+"___"+fecha_fin+"___"+cuentas+"___"+cta+"___"+scta+"___"+sscta+"___"+ssscta+"___"+sssscta+"___"+tipo_doc;
        
        //Obtiene datos del Reporte Auxiliar de Cuentas
        datos = this.getCtbDao().getCtbRepBalanzaComp_Datos(data_string);
        
        
        Double suma_saldo_inicial=0.0;
        Double suma_debe=0.0;
        Double suma_haber=0.0;
        Double suma_saldo_final=0.0;
        
        //Calcular totales
        for( HashMap<String,String> i : datos ){
            //Sumar solo los de nivel 2(1=Auxiliar, 2=Mayor) para evitar duplicar cantidades
            if(i.get("nivel").trim().equals("2")){
                //System.out.println("Nivel="+i.get("nivel")+"    saldo_inicial="+i.get("saldo_inicial")+"    debe="+i.get("debe")+"      haber="+i.get("haber")+"    saldo_final="+i.get("saldo_final"));
                //suma_saldo_inicial += (i.get("saldo_inicial").trim().equals(""))?0:Double.parseDouble(i.get("saldo_inicial"));
                suma_debe += (i.get("debe").trim().equals(""))?0:Double.parseDouble(i.get("debe"));
                suma_haber += (i.get("haber").trim().equals(""))?0:Double.parseDouble(i.get("haber"));
                //suma_saldo_final += (i.get("saldo_final").trim().equals(""))?0:Double.parseDouble(i.get("saldo_final"));
            }
        }
        
        //SALDO FINAL = Suma SALDO INICIAL + Suma DEBE - Suma HABER 
        //suma_saldo_final = suma_saldo_inicial + suma_debe - suma_haber;
        suma_saldo_final = suma_debe - suma_haber;
        
        //System.out.println("suma_saldo_inicial="+suma_saldo_inicial+"    suma_debe="+suma_debe+"      suma_haber="+suma_haber+"    suma_saldo_final="+suma_saldo_final);
        
        total.put("suma_si", StringHelper.roundDouble(suma_saldo_inicial,2));
        total.put("suma_d", StringHelper.roundDouble(suma_debe,2));
        total.put("suma_h", StringHelper.roundDouble(suma_haber,2));
        total.put("suma_sf", StringHelper.roundDouble(suma_saldo_final,2));
        
        jsonretorno.put("Data", datos);
        jsonretorno.put("Total", total);
        
        return jsonretorno;
    }
    
    
    //Genera pdf Reporte de Balanza de Comprobación
    @RequestMapping(value = "/getPdfRepBalanzaComprobacion/{cadena}/{iu}/out.json", method = RequestMethod.GET )
    public ModelAndView getGeneraPdfRepBalanzaComprobacionJson(
                @PathVariable("cadena") String cadena,
                @PathVariable("iu") String id_user_cod,
                HttpServletRequest request,
                HttpServletResponse response,
                Model model)
        throws ServletException, IOException, URISyntaxException, DocumentException, Exception {
        
        HashMap<String, String> userDat = new HashMap<String, String>();
        HashMap<String, String> datosEmpresaEmisora= new HashMap<String, String>();
        HashMap<String, String> datosEncabezadoPie= new HashMap<String, String>();
        ArrayList<LinkedHashMap<String, String>> datos = new ArrayList<LinkedHashMap<String, String>>();
        HashMap<String, String> total = new HashMap<String, String>();
        
        System.out.println("Generando Reporte de Balanza de Comprobación");
        
        //Reporte Balanza de Comprobacion
        Integer app_selected = 158;
        String command_selected="reporte";
        String tipo_doc="pdf";
        
        //Decodificar id de usuario
        Integer id_user = Integer.parseInt(Base64Coder.decodeString(id_user_cod));
        userDat = this.getHomeDao().getUserById(id_user);
        Integer id_empresa = Integer.parseInt(userDat.get("empresa_id"));
        
        String arrayCad [] = cadena.split("___");
        
        String suc=arrayCad[0];
        String fecha_ini=arrayCad[1];
        String fecha_fin=arrayCad[2];
        String cuentas=arrayCad[3];
        String cta=arrayCad[4];
        String scta=arrayCad[5];
        String sscta=arrayCad[6];
        String ssscta=arrayCad[7];
        String sssscta=arrayCad[8];
        
        String data_string = app_selected+"___"+id_user+"___"+command_selected+"___"+suc+"___"+fecha_ini+"___"+fecha_fin+"___"+cuentas+"___"+cta+"___"+scta+"___"+sscta+"___"+ssscta+"___"+sssscta+"___"+tipo_doc;
        
        //Obtiene datos de la Empresa Emisora
        datosEmpresaEmisora = this.getGralDao().getEmisor_Datos(id_empresa);
        datosEmpresaEmisora.put("regedo", "");
        
        String[] fi = fecha_ini.split("-");
        String[] ff = fecha_fin.split("-");
        String periodo_reporte = "Periodo  del  "+fi[2]+"/"+fi[1]+"/"+fi[0]+"  al  "+ff[2]+"/"+ff[1]+"/"+ff[0];
        
        //Crear cadena para imprimir Fecha en el pie de pagina del PDF.
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        String fecha_impresion = sdf.format(new Date());
        
        //Agregar datos para el Encabezado y Pie de pagina
        datosEncabezadoPie.put("empresa", datosEmpresaEmisora.get("emp_razon_social"));
        //datosEncabezadoPie.put("titulo_reporte", this.getGralDao().getTituloReporte(id_empresa, app_selected));
        datosEncabezadoPie.put("titulo_reporte", "Balanza de Comprobación");
        datosEncabezadoPie.put("periodo", periodo_reporte);
        datosEncabezadoPie.put("fecha_impresion", fecha_impresion);
        datosEncabezadoPie.put("codigo1", this.getGralDao().getCodigo1Iso(id_empresa, app_selected));
        datosEncabezadoPie.put("codigo2", this.getGralDao().getCodigo2Iso(id_empresa, app_selected));
        
        //obtener el directorio temporal
        String dir_tmp = this.getGralDao().getTmpDir();
        
        File file_dir_tmp = new File(dir_tmp);
        //System.out.println("Directorio temporal: "+file_dir_tmp.getCanonicalPath());
        
        String file_name = "RepBalanzaComprobacion_"+fi[2]+"-"+fi[1]+"-"+fi[0]+" al "+ff[2]+"-"+ff[1]+"-"+ff[0]+".pdf";
        
        //ruta de archivo de salida
        String fileout = file_dir_tmp +"/"+  file_name;
        
        datos = this.getCtbDao().getCtbRepBalanzaComp_Datos(data_string);
        
        Double suma_saldo_inicial=0.0;
        Double suma_debe=0.0;
        Double suma_haber=0.0;
        Double suma_saldo_final=0.0;
        
        //Calcular totales
        for( HashMap<String,String> i : datos ){
            //Sumar solo los de nivel 2(1=Auxiliar, 2=Mayor) para evitar duplicar cantidades
            if(i.get("nivel").trim().equals("2")){
                //System.out.println("saldo_inicial="+i.get("saldo_inicial")+"    debe="+i.get("debe")+"      haber="+i.get("haber")+"    saldo_final="+i.get("saldo_final"));
                //suma_saldo_inicial += (i.get("saldo_inicial").trim().equals(""))?0:Double.parseDouble(i.get("saldo_inicial"));
                suma_debe += (i.get("debe").trim().equals(""))?0:Double.parseDouble(i.get("debe"));
                suma_haber += (i.get("haber").trim().equals(""))?0:Double.parseDouble(i.get("haber"));
                //suma_saldo_final += (i.get("saldo_final").trim().equals(""))?0:Double.parseDouble(i.get("saldo_final"));
            }
        }
        
        //SALDO FINAL = Suma SALDO INICIAL + Suma DEBE - Suma HABER 
        //suma_saldo_final = suma_saldo_inicial + suma_debe - suma_haber;
        suma_saldo_final = suma_debe - suma_haber;
        
        total.put("suma_si", StringHelper.roundDouble(suma_saldo_inicial,2));
        total.put("suma_d", StringHelper.roundDouble(suma_debe,2));
        total.put("suma_h", StringHelper.roundDouble(suma_haber,2));
        total.put("suma_sf", StringHelper.roundDouble(suma_saldo_final,2));
        
        CtbPdfReporteBalanzaComprobacion x = new CtbPdfReporteBalanzaComprobacion(fileout, datosEncabezadoPie, datosEmpresaEmisora, datos, total);
        
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
        
        if(file.exists()){
            FileHelper.delete(fileout);
        }
        
        return null;
    }
    
}
