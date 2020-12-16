/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.agnux.kemikal.controllers;

import com.agnux.cfd.v2.Base64Coder;
import com.agnux.common.helpers.FileHelper;
import com.agnux.common.helpers.StringHelper;
import com.agnux.common.obj.ResourceProject;
import com.agnux.common.obj.UserSessionData;
import com.agnux.kemikal.interfacedaos.FacturasInterfaceDao;
import com.agnux.kemikal.interfacedaos.GralInterfaceDao;
import com.agnux.kemikal.interfacedaos.HomeInterfaceDao;
import com.agnux.kemikal.reportes.PdfFacturacion;
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

/**
 * @author gpmarsan@gmail.com
 * 21/abril/2012
 * Este controller es para generar el reporte de Facturacion
 */

@Controller
@SessionAttributes({"user"})
@RequestMapping("/repfacturacion/")
public class RepFacturacionController {
    private static final Logger log  = Logger.getLogger(RepFacturacionController.class.getName());
    ResourceProject resource = new ResourceProject();
    
    @Autowired
    @Qualifier("daoFacturas")
    private FacturasInterfaceDao facdao;
        
    
    @Autowired
    @Qualifier("daoHome")
    private HomeInterfaceDao HomeDao;
    
    @Autowired
    @Qualifier("daoGral")
    private GralInterfaceDao gralDao;
    
    public FacturasInterfaceDao getFacdao() {
        return facdao;
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
        
        log.log(Level.INFO, "Ejecutando starUp de {0}", RepFacturacionController.class.getName());
        LinkedHashMap<String,String> infoConstruccionTabla = new LinkedHashMap<String,String>();
        
        
        ModelAndView x = new ModelAndView("repfacturacion/startup", "title", "Facturacion");
        
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
    
    
    
    
    
    
    //obtiene la existencia de un Almacen en especifico
    @RequestMapping(method = RequestMethod.POST, value="/getFacturacion.json")
    public @ResponseBody HashMap<String,ArrayList<HashMap<String, String>>> getFacturacionJson(
            @RequestParam("opcion") Integer opcion,
            @RequestParam("factura") String factura,
            @RequestParam("cliente") String cliente,
            @RequestParam("fecha_inicial") String fecha_inicial,
            @RequestParam("fecha_final") String fecha_final,
            @RequestParam(value="iu", required=true) String id_user,
            Model model
            ) {
        
        log.log(Level.INFO, "Ejecutando getFacturacion de {0}", RepFacturacionController.class.getName());
        HashMap<String,ArrayList<HashMap<String, String>>> jsonretorno = new HashMap<String,ArrayList<HashMap<String, String>>>();
        ArrayList<HashMap<String, String>> facturas = new ArrayList<HashMap<String, String>>();
        ArrayList<HashMap<String, String>>  totales = new ArrayList<HashMap<String, String>>();
        LinkedHashMap<String,String> total = new LinkedHashMap<String,String>();        
        HashMap<String, String> userDat = new HashMap<String, String>();
        Double suma_pesos_subtotal = 0.0;
        Double suma_pesos_monto_ieps = 0.0;
        Double suma_pesos_impuesto = 0.0;
        Double suma_pesos_total = 0.0;
        Double suma_dolares_subtotal = 0.0;
        Double suma_dolares_monto_ieps = 0.0;
        Double suma_dolares_impuesto = 0.0;
        Double suma_dolares_total = 0.0;
        
        Double suma_subtotal_mn = 0.0;
        Double suma_monto_ieps_mn = 0.0;
        Double suma_impuesto_mn = 0.0;
        Double suma_total_mn = 0.0;
        
        factura = "%"+factura+"%";
        cliente = "%"+cliente+"%";
                
        //decodificar id de usuario
        Integer id_usuario = Integer.parseInt(Base64Coder.decodeString(id_user));
        userDat = this.getHomeDao().getUserById(id_usuario);
        
        Integer id_empresa = Integer.parseInt(userDat.get("empresa_id"));
        
        facturas = this.getFacdao().getDatosReporteFacturacion(opcion, factura, cliente, fecha_inicial, fecha_final, id_empresa);
        

        for (int x=0; x<=facturas.size()-1;x++){
            HashMap<String,String> registro = facturas.get(x);
            //sumar cantidades
            if(registro.get("id_moneda").equals("1")){
                suma_pesos_subtotal += Double.parseDouble(registro.get("subtotal"));
                suma_pesos_monto_ieps += Double.parseDouble(registro.get("monto_ieps"));
                suma_pesos_impuesto += Double.parseDouble(registro.get("impuesto"));
                suma_pesos_total += Double.parseDouble(registro.get("total"));
            }
            if(registro.get("id_moneda").equals("2")){
                suma_dolares_subtotal += Double.parseDouble(registro.get("subtotal"));
                suma_dolares_monto_ieps += Double.parseDouble(registro.get("monto_ieps"));
                suma_dolares_impuesto += Double.parseDouble(registro.get("impuesto"));
                suma_dolares_total += Double.parseDouble(registro.get("total"));
            }
            suma_subtotal_mn += Double.parseDouble(registro.get("subtotal_mn"));
            suma_monto_ieps_mn += Double.parseDouble(registro.get("monto_ieps_mn"));
            suma_impuesto_mn += Double.parseDouble(registro.get("impuesto_mn"));
            suma_total_mn += Double.parseDouble(registro.get("total_mn"));
                
        }
        
        total.put("suma_pesos_subtotal", StringHelper.roundDouble(suma_pesos_subtotal,2));
        total.put("suma_pesos_monto_ieps", StringHelper.roundDouble(suma_pesos_monto_ieps,2));
        total.put("suma_pesos_impuesto", StringHelper.roundDouble(suma_pesos_impuesto,2));
        total.put("suma_pesos_total", StringHelper.roundDouble(suma_pesos_total,2));
        
        total.put("suma_dolares_subtotal", StringHelper.roundDouble(suma_dolares_subtotal,2));
        total.put("suma_dolares_monto_ieps", StringHelper.roundDouble(suma_dolares_monto_ieps,2));
        total.put("suma_dolares_impuesto", StringHelper.roundDouble(suma_dolares_impuesto,2));
        total.put("suma_dolares_total", StringHelper.roundDouble(suma_dolares_total,2));
        
        total.put("suma_subtotal_mn", StringHelper.roundDouble(suma_subtotal_mn,2));
        total.put("suma_monto_ieps_mn", StringHelper.roundDouble(suma_monto_ieps_mn,2));
        total.put("suma_impuesto_mn", StringHelper.roundDouble(suma_impuesto_mn,2));
        total.put("suma_total_mn", StringHelper.roundDouble(suma_total_mn,2));
        
        totales.add(total);
        
        jsonretorno.put("Facturas", facturas);
        jsonretorno.put("Totales", totales);
        
        return jsonretorno;
    }
    
    
    
    
    
    
    
    //Genera pdf de facturacion de
    @RequestMapping(value = "/get_genera_reporte_facturacion/{cadena}/{iu}/out.json", method = RequestMethod.GET ) 
    public ModelAndView getGeneraPdfFacturacionJson(
                @PathVariable("cadena") String cadena,
                @PathVariable("iu") String id_user,
                HttpServletRequest request, 
                HttpServletResponse response, 
                Model model)
            throws ServletException, IOException, URISyntaxException, DocumentException, Exception {
        
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
        lista_facturas = this.getFacdao().getDatosReporteFacturacion(opciones, factura, cliente, fecha_inicial, fecha_final, id_empresa);
        
        //instancia a la clase que construye el pdf del reporte de facturas
        PdfFacturacion x = new PdfFacturacion( fileout,ruta_imagen,razon_social_empresa,fecha_inicial,fecha_final,lista_facturas);
        
        
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
    
    
    
    
    
    
    @RequestMapping(method = RequestMethod.POST, value="/getFacturaDetalle.json")
    public @ResponseBody HashMap<String,ArrayList<HashMap<String, Object>>> getFacturaDetalleJson(
            @RequestParam(value="id_factura", required=true) String id_factura,
            @RequestParam(value="iu", required=true) String id_user,
            Model model
            ) {
        
        log.log(Level.INFO, "Ejecutando getFacturaDetalleJson de {0}", FacturasController.class.getName());
        HashMap<String,ArrayList<HashMap<String, Object>>> jsonretorno = new HashMap<String,ArrayList<HashMap<String, Object>>>();
        ArrayList<HashMap<String, Object>> datosFactura = new ArrayList<HashMap<String, Object>>();
        ArrayList<HashMap<String, Object>> datosGrid = new ArrayList<HashMap<String, Object>>();
        ArrayList<HashMap<String, Object>> valorIva = new ArrayList<HashMap<String, Object>>();
        ArrayList<HashMap<String, Object>> monedas = new ArrayList<HashMap<String, Object>>();
        ArrayList<HashMap<String, Object>> tipoCambioActual = new ArrayList<HashMap<String, Object>>();
        HashMap<String, Object> tc = new HashMap<String, Object>();
        ArrayList<HashMap<String, Object>> vendedores = new ArrayList<HashMap<String, Object>>();
        ArrayList<HashMap<String, Object>> condiciones = new ArrayList<HashMap<String, Object>>();
        ArrayList<HashMap<String, Object>> metodos_pago = new ArrayList<HashMap<String, Object>>();
        HashMap<String, String> userDat = new HashMap<String, String>();
        
        //decodificar id de usuario
        Integer id_usuario = Integer.parseInt(Base64Coder.decodeString(id_user));
        
        userDat = this.getHomeDao().getUserById(id_usuario);
        
        Integer id_empresa = Integer.parseInt(userDat.get("empresa_id"));
        Integer id_sucursal = Integer.parseInt(userDat.get("sucursal_id"));
        
        if( (id_factura.equals("0"))==false  ){
            datosFactura = this.getFacdao().getFactura_Datos(Integer.parseInt(id_factura));
            datosGrid = this.getFacdao().getFactura_DatosGrid(Integer.parseInt(id_factura));
        }
        
        valorIva= this.getFacdao().getValoriva(id_sucursal);
        tc.put("tipo_cambio", StringHelper.roundDouble(this.getFacdao().getTipoCambioActual(), 4)) ;
        tipoCambioActual.add(0,tc);
        
        monedas = this.getFacdao().getFactura_Monedas();
        vendedores = this.getFacdao().getFactura_Agentes(id_empresa, id_sucursal);
        condiciones = this.getFacdao().getFactura_DiasDeCredito();
        metodos_pago = this.getFacdao().getMetodosPago(id_empresa);
        
        jsonretorno.put("datosFactura", datosFactura);
        jsonretorno.put("datosGrid", datosGrid);
        jsonretorno.put("iva", valorIva);
        jsonretorno.put("Monedas", monedas);
        jsonretorno.put("Tc", tipoCambioActual);
        jsonretorno.put("Vendedores", vendedores);
        jsonretorno.put("Condiciones", condiciones);
        jsonretorno.put("MetodosPago", metodos_pago);
        
        return jsonretorno;
    }
}
