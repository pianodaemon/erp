package com.agnux.kemikal.controllers;

import com.agnux.cfd.v2.Base64Coder;
import com.agnux.common.helpers.FileHelper;
import com.agnux.common.helpers.StringHelper;
import com.agnux.common.obj.ResourceProject;
import com.agnux.common.obj.UserSessionData;
import com.agnux.kemikal.interfacedaos.CxpInterfaceDao;
import com.agnux.kemikal.interfacedaos.GralInterfaceDao;
import com.agnux.kemikal.interfacedaos.HomeInterfaceDao;
import com.agnux.kemikal.reportes.PdfCxcIepsCobrado;
import com.agnux.kemikal.reportes.PdfCxpIepsPagado;
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


@Controller
@SessionAttributes({"user"})
@RequestMapping("/cxprepiepspagado/")
public class CxpRepIepsPagadoController {
    private static final Logger log  = Logger.getLogger(CxpRepIepsPagadoController.class.getName());
    ResourceProject resource = new ResourceProject();
    
    @Autowired
    @Qualifier("daoCxp")
    private CxpInterfaceDao cxpDao;
    
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
    
    public CxpInterfaceDao getCxpDao() {
        return cxpDao;
    }
    
    
    @RequestMapping(value="/startup.agnux")
    public ModelAndView startUp(HttpServletRequest request, HttpServletResponse response, 
            @ModelAttribute("user") UserSessionData user)
            throws ServletException, IOException {
        
        log.log(Level.INFO, "Ejecutando starUp de {0}", CxpRepIepsPagadoController.class.getName());
        LinkedHashMap<String,String> infoConstruccionTabla = new LinkedHashMap<String,String>();
        
        ModelAndView x = new ModelAndView("cxprepiepspagado/startup", "title", "Reporte de IEPS Pagado");
        
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
    
    
    
    

    //Obtiene datos para el reporte
    @RequestMapping(method = RequestMethod.POST, value="/getDatos.json")
    public @ResponseBody HashMap<String,Object> getDatosReporteJson(
            //@RequestParam("tipo") Integer tipo,
            @RequestParam("proveedor") String proveedor,
            @RequestParam("finicial") String finicial,
            @RequestParam("ffinal") String ffinal,
            @RequestParam(value="iu", required=true) String id_user,
            Model model
        ) {
                                        
        
        log.log(Level.INFO, "Ejecutando getDatosIEPSPagadoJson de {0}", CxpRepIepsPagadoController.class.getName());
        HashMap<String,Object> jsonretorno = new HashMap<String,Object>();
        ArrayList<HashMap<String, String>> datos = new ArrayList<HashMap<String, String>>();
        ArrayList<HashMap<String, String>> listaIeps = new ArrayList<HashMap<String, String>>();
        HashMap<String, String> userDat = new HashMap<String, String>();
        LinkedHashMap<String, String> columnsHead = new LinkedHashMap<String, String>();
        LinkedHashMap<String, String> columnsBody = new LinkedHashMap<String, String>();
        LinkedHashMap<String, String> totales1 = new LinkedHashMap<String, String>();
        ArrayList<String> indexTotales = new ArrayList<String>();
        HashMap<String, Object> conf = new HashMap<String, Object>();
        int widthMainTable=0;
        int noCols=0;
        
        //decodificar id de usuario
        Integer id_usuario = Integer.parseInt(Base64Coder.decodeString(id_user));
        userDat = this.getHomeDao().getUserById(id_usuario);
        
        Integer id_empresa = Integer.parseInt(userDat.get("empresa_id"));
        
        //Obtener los tipos de IEPS
        listaIeps = this.getCxpDao().getListaIeps(id_empresa);
        
        //Crear columnas para la Tabla de la vista
        //columnsHead = Es para los encabezados de las columnas, incluye el TITULO, Ancho y Alineacion del texto en la celda
        //columnsBody = Para mostrar los datos del reporte, incluye Ancho y Alineacion del texto en la celda
        String keyIndex="fecha_pago";
        columnsHead.put(keyIndex, "FECHA&nbsp;PAGO:100:left");
        columnsBody.put(keyIndex, "100:left");
        widthMainTable += 100;
        noCols++;
        
        keyIndex="fecha";
        columnsHead.put(keyIndex, "FECHA&nbsp;FAC.:100:left");
        columnsBody.put(keyIndex, "100:left");
        widthMainTable += 100;
        noCols++;
        
        keyIndex="factura";
        columnsHead.put(keyIndex, "FACTURA:90:left");
        columnsBody.put(keyIndex, "90:left");
        widthMainTable += 90;
        noCols++;
        
        keyIndex="moneda_fac";
        columnsHead.put(keyIndex, "DENOM:60:left");
        columnsBody.put(keyIndex, "60:left");
        widthMainTable += 60;
        noCols++;
        
        keyIndex="moneda_simbolo_subtotal";
        columnsHead.put(keyIndex, " :15:right");
        columnsBody.put(keyIndex, "15:right");
        totales1.put(keyIndex, "$");
        widthMainTable += 15;
        noCols++;
        
        keyIndex="subtotal";
        columnsHead.put(keyIndex, "SUBTOTAL:90:right");
        columnsBody.put(keyIndex, "90:right");
        indexTotales.add(keyIndex);
        totales1.put(keyIndex, "0");
        widthMainTable += 90;
        noCols++;
        
        keyIndex="moneda_simbolo_retencion";
        columnsHead.put(keyIndex, " :15:right");
        columnsBody.put(keyIndex, "15:right");
        totales1.put(keyIndex, "$");
        widthMainTable += 15;
        noCols++;
        
        keyIndex="retencion";
        columnsHead.put(keyIndex, "RETENCION:90:right");
        columnsBody.put(keyIndex, "90:right");
        indexTotales.add(keyIndex);
        totales1.put(keyIndex, "0");
        widthMainTable += 90;
        noCols++;
        
        keyIndex="moneda_simbolo_iva";
        columnsHead.put(keyIndex, " :15:right");
        columnsBody.put(keyIndex, "15:right");
        totales1.put(keyIndex, "$");
        widthMainTable += 15;
        noCols++;
        
        keyIndex="iva";
        columnsHead.put(keyIndex, "IVA:90:right");
        columnsBody.put(keyIndex, "90:right");
        indexTotales.add(keyIndex);
        totales1.put(keyIndex, "0");
        widthMainTable += 90;
        noCols++;
        
        //Crear nombres de campos dinamicamente
        for( HashMap<String,String> i : listaIeps ){
            keyIndex="moneda_simbolo_ieps"+i.get("id");
            columnsHead.put(keyIndex, " :15:right");
            columnsBody.put(keyIndex, "15:right");
            totales1.put(keyIndex, "$");
            widthMainTable += 15;
            noCols++;
        
            String key = "ieps"+i.get("id");
            columnsHead.put(key, "IEPS "+i.get("tasa")+"%:90:right");
            columnsBody.put(key, "90:right");
            indexTotales.add(key);
            totales1.put(key, "0");
            widthMainTable += 90;
            noCols++;
        }
        
        keyIndex="moneda_simbolo_total";
        columnsHead.put(keyIndex, " :15:right");
        columnsBody.put(keyIndex, "15:right");
        totales1.put(keyIndex, "$");
        widthMainTable += 15;
        noCols++;
        
        keyIndex="total";
        //Ultima columna para encabezado
        columnsHead.put(keyIndex, "TOTAL:120:right");
        //La ultima columna para el body mide menos por eso se agrega por separado
        columnsBody.put(keyIndex, "100:right");
        indexTotales.add(keyIndex);
        totales1.put(keyIndex, "0");
        //Sumar la medida de la ultima columna
        widthMainTable += 120;
        //Aumentar una columna para contar la ultima
        noCols++;
        
        
        conf.put("ColumnsHead", columnsHead);
        conf.put("ColumnsBody", columnsBody);
        conf.put("widthMainTable", widthMainTable);
        conf.put("colspanMainTable", noCols);
        conf.put("widthReportTable", (widthMainTable - 20));
        
        
        //Obtener datos del Ieps Pagado
        datos = this.getCxpDao().getDatosReporteIepsPagado(listaIeps, proveedor, finicial, ffinal, id_empresa);
        
        
        Double value=0.0000;
        String remove="";
        if(datos.size()>0){
            for( HashMap<String,String> i : datos ){
                for( String key : indexTotales ){
                    if(i.containsKey(key)){
                        value = 0.0000;
                        value = Double.valueOf(totales1.get(key)) + Double.valueOf(StringHelper.removerComas(i.get(key)));
                        remove = totales1.remove(key);
                        totales1.put(key, StringHelper.roundDouble(value, 2));
                    }
                }
            }
        }
        
        
        String valor="";
        Double sumaIeps=0.0000;
        for( String key : indexTotales ){
            if(totales1.containsKey(key)){
                valor = totales1.remove(key);
                if(key.indexOf("ieps")>=0){
                    sumaIeps += Double.valueOf(valor);
                }
                totales1.put(key, StringHelper.AgregaComas(StringHelper.roundDouble(valor, 2)));
            }
        }
        
        totales1.put("sumaIeps", StringHelper.AgregaComas(StringHelper.roundDouble(sumaIeps,2)));
        jsonretorno.put("Conf", conf);
        jsonretorno.put("Datos", datos);
        jsonretorno.put("Totales", totales1);
        
        
        return jsonretorno;
    }
    
    
    
    //Genera pdf de estados de cuenta
    @RequestMapping(value = "/getPdf/{cadena}/{iu}/out.json", method = RequestMethod.GET ) 
    public ModelAndView getPdfJson(
            @PathVariable("cadena") String cadena,
            @PathVariable("iu") String id_user_cod,
            HttpServletRequest request, 
            HttpServletResponse response, 
            Model model
       )throws ServletException, IOException, URISyntaxException, DocumentException, Exception {
        
        HashMap<String, String> userDat = new HashMap<String, String>();
        ArrayList<HashMap<String, String>> datos = new ArrayList<HashMap<String, String>>();
        ArrayList<HashMap<String, String>> listaIeps = new ArrayList<HashMap<String, String>>();
        LinkedHashMap<String, String> columnsHead = new LinkedHashMap<String, String>();
        LinkedHashMap<String, String> columnsBody = new LinkedHashMap<String, String>();
        LinkedHashMap<String, String> totales1 = new LinkedHashMap<String, String>();
        ArrayList<String> indexTotales = new ArrayList<String>();
        HashMap<String, Object> conf = new HashMap<String, Object>();
        HashMap<String,Object> data = new HashMap<String,Object>();
        //int widthMainTable=0;
        int noCols=0;
        
        String tituloReporte="IEPS Pagado";
        System.out.println("Generando reporte de "+tituloReporte);
        
        //decodificar id de usuario
        Integer id_usuario = Integer.parseInt(Base64Coder.decodeString(id_user_cod));
        //System.out.println("id_usuario: "+id_usuario);
        
        userDat = this.getHomeDao().getUserById(id_usuario);
        Integer id_empresa = Integer.parseInt(userDat.get("empresa_id"));
        
        String razon_social_empresa = this.getGralDao().getRazonSocialEmpresaEmisora(id_empresa);
        
        String ArrayCad[] = cadena.split("___");
        String proveedor = ArrayCad[0];
        String fechaInicial = ArrayCad[1];
        String fechaFinal = ArrayCad[2];
        
        
        String[] fi = fechaInicial.split("-");
        String[] ff = fechaFinal.split("-");
        String periodo_reporte = "Periodo  del  "+fi[2]+"/"+fi[1]+"/"+fi[0]+"  al  "+ff[2]+"/"+ff[1]+"/"+ff[0];
        
        String fecha_corte = fi[2]+"-"+fi[1]+"-"+fi[0]+"  al  "+ff[2]+"-"+ff[1]+"-"+ff[0];
        
        
        //obtener el directorio temporal
        //String dir_tmp = System.getProperty("java.io.tmpdir");
        String dir_tmp = this.getGralDao().getTmpDir();
        
        //String[] array_company = razon_social_empresa.split(" ");
        //String company_name= array_company[0].toLowerCase();
        //String ruta_imagen = this.getGralDao().getImagesDir() +"logo_"+ company_name +".png";
        
        //System.out.println("ruta_imagen: "+ruta_imagen);
        
        File file_dir_tmp = new File(dir_tmp);
        //System.out.println("Directorio temporal: "+file_dir_tmp.getCanonicalPath());
        
        String file_name = "IepsPagado_"+fecha_corte+".pdf";
        
        //ruta de archivo de salida
        String fileout = file_dir_tmp +"/"+  file_name;
        
        
        //Obtener los tipos de IEPS
        listaIeps = this.getCxpDao().getListaIeps(id_empresa);
        
        //Obtener datos del Ieps Pagado
        datos = this.getCxpDao().getDatosReporteIepsPagado(listaIeps, proveedor, fechaInicial, fechaFinal, id_empresa);
        
        
        //Crear columnas para la Tabla de la vista
        //columnsHead = Es para los encabezados de las columnas, incluye el TITULO, Ancho y Alineacion del texto en la celda
        //columnsBody = Para mostrar los datos del reporte, incluye Ancho y Alineacion del texto en la celda
        String keyIndex="fecha_pago";
        columnsHead.put(keyIndex, "F. PAGO:left");
        columnsBody.put(keyIndex, "100:left");
        //widthMainTable += 100;
        noCols++;
        
        keyIndex="fecha";
        columnsHead.put(keyIndex, "F. FAC.:left");
        columnsBody.put(keyIndex, "100:left");
        //widthMainTable += 100;
        noCols++;
        
        keyIndex="factura";
        columnsHead.put(keyIndex, "FACTURA:left");
        columnsBody.put(keyIndex, "90:left");
        //widthMainTable += 90;
        noCols++;
        
        keyIndex="moneda_fac";
        columnsHead.put(keyIndex, "MON:60:left");
        columnsBody.put(keyIndex, "60:left");
        //widthMainTable += 60;
        noCols++;
        
        keyIndex="moneda_simbolo_subtotal";
        columnsHead.put(keyIndex, " :15:right");
        columnsBody.put(keyIndex, "15:right");
        //indexTotales.add(keyIndex);
        totales1.put(keyIndex, "$");
        //widthMainTable += 15;
        noCols++;
        
        keyIndex="subtotal";
        columnsHead.put(keyIndex, "SUBTOTAL:right");
        columnsBody.put(keyIndex, "90:right");
        indexTotales.add(keyIndex);
        totales1.put(keyIndex, "0");
        //widthMainTable += 90;
        noCols++;
        
        keyIndex="moneda_simbolo_retencion";
        columnsHead.put(keyIndex, " :15:right");
        columnsBody.put(keyIndex, "15:right");
        totales1.put(keyIndex, "$");
        noCols++;
        
        keyIndex="retencion";
        columnsHead.put(keyIndex, "RETENCIÓN:right");
        columnsBody.put(keyIndex, "90:right");
        indexTotales.add(keyIndex);
        totales1.put(keyIndex, "0");
        noCols++;
        
        keyIndex="moneda_simbolo_iva";
        columnsHead.put(keyIndex, " :15:right");
        columnsBody.put(keyIndex, "15:right");
        //indexTotales.add(keyIndex);
        totales1.put(keyIndex, "$");
        noCols++;
        
        keyIndex="iva";
        columnsHead.put(keyIndex, "IVA:right");
        columnsBody.put(keyIndex, "90:right");
        indexTotales.add(keyIndex);
        totales1.put(keyIndex, "0");
        noCols++;
        
        //Crear nombres de campos dinamicamente
        for( HashMap<String,String> i : listaIeps ){
            keyIndex="moneda_simbolo_ieps"+i.get("id");
            columnsHead.put(keyIndex, " :15:right");
            columnsBody.put(keyIndex, "15:right");
            totales1.put(keyIndex, "$");
            noCols++;
            
            String key = "ieps"+i.get("id");
            columnsHead.put(key, "IEPS "+i.get("tasa")+"%:right");
            columnsBody.put(key, "90:right");
            indexTotales.add(key);
            totales1.put(key, "0");
            noCols++;
        }
        
        keyIndex="moneda_simbolo_total";
        columnsHead.put(keyIndex, " :15:right");
        columnsBody.put(keyIndex, "15:right");
        totales1.put(keyIndex, "$");
        noCols++;
        
        keyIndex="total";
        //Ultima columna para encabezado
        columnsHead.put(keyIndex, "TOTAL:right");
        //La ultima columna para el body mide menos por eso se agrega por separado
        columnsBody.put(keyIndex, "100:right");
        indexTotales.add(keyIndex);
        totales1.put(keyIndex, "0");
        //Sumar la medida de la ultima columna
        //widthMainTable += 120;
        //Aumentar una columna para contar la ultima
        noCols++;
        
        
        Double value=0.0000;
        String remove="";
        if(datos.size()>0){
            for( HashMap<String,String> i : datos ){
                for( String key : indexTotales ){
                    if(i.containsKey(key)){
                        value = 0.0000;
                        value = Double.valueOf(totales1.get(key)) + Double.valueOf(StringHelper.removerComas(i.get(key)));
                        remove = totales1.remove(key);
                        totales1.put(key, StringHelper.roundDouble(value, 2));
                    }
                }
            }
        }
        
        
        String valor="";
        Double sumaIeps=0.0000;
        for( String key : indexTotales ){
            if(totales1.containsKey(key)){
                valor = totales1.remove(key);
                if(key.indexOf("ieps")>=0){
                    sumaIeps += Double.valueOf(valor);
                }
                totales1.put(key, StringHelper.AgregaComas(StringHelper.roundDouble(valor, 2)));
            }
        }
        
        totales1.put("sumaIeps", StringHelper.AgregaComas(StringHelper.roundDouble(sumaIeps,2)));
        
        
        conf.put("Columns", columnsHead);
        conf.put("noCols", noCols);
        conf.put("fileout", fileout);
        conf.put("tituloReporte", tituloReporte);
        conf.put("nombreEmpresa", razon_social_empresa);
        conf.put("periodo", periodo_reporte);
        
        data.put("conf", conf);
        data.put("datos", datos);
        data.put("totales", totales1);
        
        
        //instancia a la clase que construye el pdf de la del reporte de estado de cuentas del cliente
        PdfCxpIepsPagado x = new PdfCxpIepsPagado(data);
        
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
