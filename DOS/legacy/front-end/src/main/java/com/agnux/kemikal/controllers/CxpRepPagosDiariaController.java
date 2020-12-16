package com.agnux.kemikal.controllers;


import com.agnux.cfd.v2.Base64Coder;
import com.agnux.common.helpers.StringHelper;
import com.agnux.common.obj.ResourceProject;
import com.agnux.common.obj.UserSessionData;
import com.agnux.kemikal.interfacedaos.CxpInterfaceDao;
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


@Controller
@SessionAttributes({"user"})
@RequestMapping("/reppagosdiaria/")
public class CxpRepPagosDiariaController {
    
    private static final Logger log  = Logger.getLogger(CxpRepPagosDiariaController.class.getName());
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
    
    public CxpInterfaceDao getCxpDao() {
        return cxpDao;
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
        
       
        log.log(Level.INFO, "Ejecutando starUp de {0}", CxpRepPagosDiariaController.class.getName());
        LinkedHashMap<String,String> infoConstruccionTabla = new LinkedHashMap<String,String>();
        
        
        ModelAndView x = new ModelAndView("reppagosdiaria/startup", "title", "Relacion de Pagos Diaria");
        
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
    @RequestMapping(method = RequestMethod.POST, value="/getBuscaProveedores.json")
    public @ResponseBody HashMap<String,ArrayList<HashMap<String, String>>> getBuscaProveedoresJson(
            @RequestParam(value="rfc", required=true) String rfc,
            @RequestParam(value="email", required=true) String email,
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
        
        proveedores = this.getCxpDao().getBuscadorProveedores(rfc, email, nombre,id_empresa);
        
        jsonretorno.put("Proveedores", proveedores);
        
        return jsonretorno;
    }

    
    //obtiene datos para el sqlquery de la vista 
    
    @RequestMapping(value="/getPagosDiaria/out.json", method = RequestMethod.POST)
    public @ResponseBody HashMap<String,ArrayList<HashMap<String, String>>> getPagosDiariaJson(
            @RequestParam(value="fecha_inicial", required=true) String fecha_inicial, 
            @RequestParam(value="fecha_final", required=true) String fecha_final,
            @RequestParam(value="proveedor", required=true) String proveedor,
            @RequestParam(value="tipo_prov", required=true) Integer tipo_prov,
            @RequestParam(value="iu", required=true) String id_user_cod,
            Model model
            ) {
        log.log(Level.INFO, "Ejecutando getPagosDiaria de {0}", CxpRepPagosDiariaController.class.getName());
        HashMap<String,ArrayList<HashMap<String, String>>> jsonretorno = new HashMap<String,ArrayList<HashMap<String, String>>>();
        HashMap<String, String> userDat = new HashMap<String, String>();
        ArrayList<HashMap<String, String>>  pagos = new ArrayList<HashMap<String, String>>();///* //mi ed 1 start
        ArrayList<HashMap<String, String>>  totales = new ArrayList<HashMap<String, String>>();
        LinkedHashMap<String,String> total = new LinkedHashMap<String,String>();
        
        Double suma_pesos_monto_total = 0.0;
        Double suma_pesos_monto_pago = 0.0;
        Double suma_dolares_monto_total= 0.0;
        Double suma_dolares_monto_pago = 0.0;
        Double suma_euros_monto_total= 0.0;
        Double suma_euros_monto_pago = 0.0;
        
        //Decodificar id de usuario
        Integer id_usuario = Integer.parseInt(Base64Coder.decodeString(id_user_cod));
        userDat = this.getHomeDao().getUserById(id_usuario);
        Integer id_empresa = Integer.parseInt(userDat.get("empresa_id"));
        proveedor = (proveedor.trim().equals("0"))?"":proveedor.trim();
        
        pagos = this.getCxpDao().getPagosDiaria( fecha_inicial, fecha_final, proveedor,  tipo_prov, id_empresa);
        
        for (int x=0; x<=pagos.size()-1;x++){
            HashMap<String,String> registro = pagos.get(x);
            
            //sumar pagos aplicados en la moneda de la factura
            if(registro.get("id_moneda_fac").equals("1")){
                suma_pesos_monto_total += Double.parseDouble(registro.get("pago_aplicado"));
            }
            
            if(registro.get("id_moneda_fac").equals("2")){
                suma_dolares_monto_total += Double.parseDouble(registro.get("pago_aplicado"));
            }
            
            if(registro.get("id_moneda_fac").equals("3")){
                suma_euros_monto_total += Double.parseDouble(registro.get("pago_aplicado"));
            }
              
            //sumar pagos aplicados en la moneda del pago
            if(registro.get("id_moneda_pago").equals("1")){
                suma_pesos_monto_pago += Double.parseDouble(registro.get("monto_pago"));
            }
            
            if(registro.get("id_moneda_pago").equals("2")){
                suma_dolares_monto_pago += Double.parseDouble(registro.get("monto_pago"));
            }
            
            if(registro.get("id_moneda_pago").equals("3")){
                suma_euros_monto_pago += Double.parseDouble(registro.get("monto_pago"));
            }
        }
        
        total.put("suma_pesos_monto_total", StringHelper.roundDouble(suma_pesos_monto_total,2));
        total.put("suma_pesos_monto_pago", StringHelper.roundDouble(suma_pesos_monto_pago,2));
        total.put("suma_dolares_monto_total", StringHelper.roundDouble(suma_dolares_monto_total,2));
        total.put("suma_dolares_monto_pago", StringHelper.roundDouble(suma_dolares_monto_pago,2));
        total.put("suma_euros_monto_total", StringHelper.roundDouble(suma_euros_monto_total,2));
        total.put("suma_euros_monto_pago", StringHelper.roundDouble(suma_euros_monto_pago,2));
        totales.add(total);
        
        jsonretorno.put("Pagos", pagos);
        jsonretorno.put("Totales", totales);
        
        return jsonretorno;
    }

    
    
    //PDF reporte de ventas netas por Cliente/producto desglosado por factura
     @RequestMapping(value = "/getPagosDiaria/{cadena}/out.json", method = RequestMethod.GET ) 
     public ModelAndView PdfPagosDiaria(
                @PathVariable("cadena") String cadena,
                HttpServletRequest request,
                HttpServletResponse response, 
                Model model
           )   
     throws ServletException, IOException, URISyntaxException, DocumentException {
        String arreglo[];
        arreglo = cadena.split("___");
        //arreglo[0] proveedor
        //arreglo[1] fecha_inicial
        //arreglo[2] fecha_final
        //arreglo[3] usuario
        //arreglo[4] tipo_prov
        
        HashMap<String, String> userDat = new HashMap<String, String>();
        
        System.out.println("Generando Reporte de Pagos Diarios");
        
        String dir_tmp = this.getGralDao().getTmpDir();
        
        File file_dir_tmp = new File(dir_tmp);
        String file_name = "PagosDiarios"+arreglo[1]+"_"+arreglo[2]+".pdf";
        
        //ruta de archivo de salida
        String fileout = file_dir_tmp +"/"+  file_name;
        
        ArrayList<HashMap<String, String>> lista_PagosDiaria = new ArrayList<HashMap<String, String>>();
        HashMap<String, String> datosEncabezadoPie= new HashMap<String, String>();
        HashMap<String, String> datos= new HashMap<String, String>();
        
        //decodificar id de usuario
        Integer id_usuario = Integer.parseInt(Base64Coder.decodeString(arreglo[3]));
        Integer app_selected = 80;//Reporte Pagos Diarios
        
        userDat = this.getHomeDao().getUserById(id_usuario);
        Integer id_empresa = Integer.parseInt(userDat.get("empresa_id"));
        //String rfc_empresa=this.getGralDao().getRfcEmpresaEmisora(id_empresa);
        String razon_social_empresa = this.getGralDao().getRazonSocialEmpresaEmisora(id_empresa);
        
        String titulo_reporte ="Pagos Diarios";
        datosEncabezadoPie.put("nombre_empresa_emisora", razon_social_empresa);
        datosEncabezadoPie.put("titulo_reporte", titulo_reporte);
        datosEncabezadoPie.put("codigo1", "");
        datosEncabezadoPie.put("codigo2","");
        datos.put("fecha_inicial",arreglo[1]);
        datos.put("fecha_final",arreglo[2]);
        
        //Obtiene los depositos del periodo indicado
        String proveedor = (arreglo[0].trim().equals("0"))?"":arreglo[0].trim();
        Integer tipo_prov = Integer.parseInt(arreglo[4]);
        
        lista_PagosDiaria = this.getCxpDao().getPagosDiaria( arreglo[1], arreglo[2], proveedor, tipo_prov, id_empresa);
        
        //instancia a la clase que construye el pdf de Cobranza Diaria
        CxpPdfReportePagosDiaria x = new CxpPdfReportePagosDiaria(datosEncabezadoPie, fileout,lista_PagosDiaria,datos);
        
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
        
        return null;
        
        
    }
        
    
    
    
    
    
    
}
