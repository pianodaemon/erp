/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.agnux.kemikal.controllers;

import com.agnux.cfd.v2.Base64Coder;
import com.agnux.common.helpers.FileHelper;
import com.agnux.common.obj.ResourceProject;
import com.agnux.common.obj.UserSessionData;
import com.agnux.kemikal.interfacedaos.CxcInterfaceDao;
import com.agnux.kemikal.interfacedaos.GralInterfaceDao;
import com.agnux.kemikal.interfacedaos.HomeInterfaceDao;
import com.agnux.kemikal.reportes.PdfReporteCobrazaAgente;
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
@RequestMapping("/repcobranzaagente/")
public class RepCobranzaAgenteController {
    private static final Logger log  = Logger.getLogger(RepCobranzaAgenteController.class.getName());
    ResourceProject resource = new ResourceProject();


    @Autowired
    @Qualifier("daoHome")
    private HomeInterfaceDao HomeDao;

    @Autowired
    @Qualifier("daoGral")
    private GralInterfaceDao gralDao;

    @Autowired
    @Qualifier("daoCxc")
    private CxcInterfaceDao cxcDao;

    public CxcInterfaceDao getCxcDao() {
        return cxcDao;
    }

    public void setCxcDao(CxcInterfaceDao cxcDao) {
        this.cxcDao = cxcDao;
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


        log.log(Level.INFO, "Ejecutando starUp de {0}", RepCobranzaAgenteController.class.getName());
        LinkedHashMap<String,String> infoConstruccionTabla = new LinkedHashMap<String,String>();


        ModelAndView x = new ModelAndView("repcobranzaagente/startup", "title", "Cobranza por Agente ");

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





    //obtiene los tipos de agente
    @RequestMapping(method = RequestMethod.POST, value="/getBuscaDatos.json")
    public @ResponseBody HashMap<String,ArrayList<HashMap<String, String>>> getAgentesJson(
        @RequestParam(value="iu", required=true) String id_user,Model model){

        HashMap<String,ArrayList<HashMap<String, String>>> jsonretorno = new HashMap<String,ArrayList<HashMap<String, String>>>();
        HashMap<String, String> userDat = new HashMap<String, String>();

        //decodificar id de usuario
        Integer id_usuario = Integer.parseInt(Base64Coder.decodeString(id_user));
        userDat = this.getHomeDao().getUserById(id_usuario);
        Integer id_empresa = Integer.parseInt(userDat.get("empresa_id"));

        jsonretorno.put("Agentes", this.getCxcDao().getAgentes(id_empresa));

        return jsonretorno;
    }




    //obtiene datos
    @RequestMapping(value="/getCobranzaAgente.json", method = RequestMethod.POST)
    public @ResponseBody HashMap<String,ArrayList<HashMap<String, String>>> getCobranzaAgente(
            @RequestParam(value="fecha_inicial", required=true) String fecha_inicial,
            @RequestParam(value="fecha_final", required=true) String fecha_final,
            @RequestParam(value="monto_inicial", required=true) Double monto_inicial,
            @RequestParam(value="monto_final", required=true) Double monto_final,
            @RequestParam(value="tipo_comision", required=true) Integer tipo_comision,
            @RequestParam(value="id_agente", required=true) Integer id_agente,
            @RequestParam(value="iu", required=true) String id_user_cod,
            Model model
            ) {

                log.log(Level.INFO, "Ejecutando getCobranzaAgente de {0}", RepCobranzaAgenteController.class.getName());
                HashMap<String,ArrayList<HashMap<String, String>>> jsonretorno = new HashMap<String,ArrayList<HashMap<String, String>>>();
                ArrayList<HashMap<String, String>> z = new ArrayList<HashMap<String, String>>();
                //ArrayList<HashMap<String, String>> arraysumas = new ArrayList<HashMap<String, String>>();

                ArrayList<HashMap<String, String>> Datos = new ArrayList<HashMap<String, String>>();
                ArrayList<HashMap<String, String>> arraytotales = new ArrayList<HashMap<String, String>>();

                HashMap<String, String> userDat = new HashMap<String, String>();
                Double sumatoria_subtotal = 0.0;
                //decodificar id de usuario
                Integer id_usuario = Integer.parseInt(Base64Coder.decodeString(id_user_cod));

                userDat = this.getHomeDao().getUserById(id_usuario);

                Integer id_empresa = Integer.parseInt(userDat.get("empresa_id"));

                z = this.getCxcDao().getCartera_DatosReporteCobranzaAgente(id_agente,fecha_inicial, fecha_final,monto_inicial,monto_final,tipo_comision, id_empresa); //llama al dao

              /*  String numero_agente="";
                HashMap<String, String> sumatorias;
                if (tipo_comision == 1 ) {
                   if (z.size() > 0) {
                    numero_agente = z.get(0).get("numero_agente")  ;
                    String denominacion = z.get(0).get("moneda_factura")  ;
                    String nombre_agente = z.get(0).get("nombre_agente")  ;

                        for (int x = 0; x <= z.size() - 1; x++) {
                            HashMap<String, String> registro = z.get(x);
                           System.out.println(numero_agente+"="+registro.get("numero_agente")+"___"+denominacion+"="+registro.get("moneda_factura"));
                            if (numero_agente.equals(registro.get("numero_agente")) && denominacion.equals(registro.get("moneda_factura"))) {
                                sumatoria_subtotal = sumatoria_subtotal + Double.parseDouble(registro.get("subtotal"));
                            } else {
                                sumatorias = new HashMap<String, String>();
                                sumatorias.put("nombre_agente",registro.get("nombre_agente"));
                                sumatorias.put("numero_agente",numero_agente);
                                sumatorias.put("subtotal", String.valueOf(sumatoria_subtotal));
                                arraytotales.add(sumatorias);

                                //inicializar variables de sumatorias
                                sumatoria_subtotal = 0.0;

                                //tomar el nombre del nuevo cliente
                                numero_agente = registro.get("numero_agente");
                                nombre_agente = registro.get("nombre_agente");
                                denominacion = registro.get("moneda_factura")  ;

                                //sumar primer subtotal del nuevo cliente
                                sumatoria_subtotal = sumatoria_subtotal + Double.parseDouble(registro.get("subtotal"));

                            }
                        }//fin del for
                        sumatorias = new HashMap<String, String>();

                        //agregar al array
                        sumatorias.put("numero_agente", String.valueOf(numero_agente));
                        sumatorias.put("nombre_agente", String.valueOf(nombre_agente));
                        sumatorias.put("subtotal", String.valueOf(sumatoria_subtotal));
                        arraytotales.add(sumatorias);


                    }*/


                        jsonretorno.put("datos_normales", z);
                        //jsonretorno.put("totales", arraytotales);
                       // jsonretorno.put("datos_normales", Datos);

                //}


        return jsonretorno;

}

     //Genera pdf de cobranza agente
    //http://localhost:8080/erp-quimicos/controllers/repcobranzaagente/reporte_cobranza_venta_agente /1/  0 /2013-02-12/2013-03-04/0/3500
     @RequestMapping(value = "/reporte_cobranza_venta_agente/{opcion_seleccionada}/{id_agente}/{fecha_inicial}/{fecha_final}/{monto_inicial}/{monto_final}/{tipo_comision}/{iu}/out.json", method = RequestMethod.GET )
     public ModelAndView getPdfCobranzaAgenteJson(
     @PathVariable("opcion_seleccionada") Integer opcion_seleccionada,//1
     @PathVariable("id_agente") Integer id_agente,//0
     @PathVariable("fecha_inicial") String fecha_inicial, //2013-02-12
     @PathVariable("fecha_final") String fecha_final,//2013-02-12
     @PathVariable("monto_inicial") double monto_inicial,//  0
     @PathVariable("monto_final") double monto_final,//3500
     @PathVariable("tipo_comision") Integer tipo_comision,
     @PathVariable("iu") String id_user_cod,
     HttpServletRequest request,
     HttpServletResponse response,
     Model model)
     throws ServletException, IOException, URISyntaxException, DocumentException, Exception {

        HashMap<String, String> userDat = new HashMap<String, String>();
        String tipo_rep="";
        if(tipo_comision==2 && opcion_seleccionada==1 ){
            if(monto_inicial>0 && monto_final != 0 ){
                tipo_rep="cobranza por agente por Monto";

            }

        }

        if(opcion_seleccionada==1){
            tipo_rep="cobranza por agente";
        }else{
             tipo_rep="Venta por agente";
        }
        System.out.println("Generando reporte de "+tipo_rep);

        //decodificar id de usuario
        Integer id_usuario = Integer.parseInt(Base64Coder.decodeString(id_user_cod));

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
        String file_name="";
        if(tipo_comision==2 && opcion_seleccionada==1 ){
            file_name = "Comision sobre Monto facturas Agente"+fecha_inicial+"_"+fecha_final+".pdf";
        }
        if(opcion_seleccionada==1){
            file_name = "cobranza_x_agente"+fecha_inicial+"_"+fecha_final+".pdf";
        }
        if(opcion_seleccionada==2){
            file_name = "ventas_x_agente"+fecha_inicial+"_"+fecha_final+".pdf";
        }
        //ruta de archivo de salida
        String fileout = file_dir_tmp +"/"+  file_name;
        //String fileout ="C:\\Users\\micompu\\Desktop\\"+file_name;

        ArrayList<HashMap<String, String>> cobranza_venta = new ArrayList<HashMap<String, String>>();

        //obtiene los datos para el reporte cobranza por agente
        if(opcion_seleccionada==1){
             System.out.println("LLenando la lista con de cobranza agente");
               cobranza_venta = this.getCxcDao().getCartera_DatosReporteCobranzaAgente(id_agente, fecha_inicial, fecha_final,monto_inicial,monto_final,tipo_comision, id_empresa);
        }
        if(opcion_seleccionada==2){
             System.out.println("LLenando la lista ventas por agente");
               cobranza_venta = this.getCxcDao().getCartera_DatosReporteVentaxAgente(id_agente, fecha_inicial, fecha_final, id_empresa);
        }

        if(tipo_comision==2 && opcion_seleccionada==1 ){
            if(monto_inicial>0 && monto_final != 0 ){
                System.out.println("LLenando la lista con de cobranza agente");
               cobranza_venta = this.getCxcDao().getCartera_DatosReporteCobranzaAgente(id_agente, fecha_inicial, fecha_final,monto_inicial,monto_final,tipo_comision, id_empresa);

            }

        }

       //instancia a la clase que construye el pdf de la del reporte de cobranza por agente
       PdfReporteCobrazaAgente x = new PdfReporteCobrazaAgente( fileout,cobranza_venta,opcion_seleccionada,tipo_comision,razon_social_empresa,fecha_inicial,fecha_final);


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
        
        FileHelper.delete(fileout);
        
        return null;

    }

    //obtiene datos  para ventas por agente
    @RequestMapping(value="/getVentaxAgente.json", method = RequestMethod.POST)
    public @ResponseBody ArrayList<HashMap<String, String>> getVentaxAgente(
            @RequestParam(value="fecha_inicial", required=true) String fecha_inicial,
            @RequestParam(value="fecha_final", required=true) String fecha_final,
            @RequestParam(value="id_agente", required=true) Integer id_agente,
            @RequestParam(value="iu", required=true) String id_user_cod,
            Model model
            ) {
        log.log(Level.INFO, "Ejecutando getCobranzaAgente de {0}", RepCobranzaAgenteController.class.getName());
        HashMap<String,ArrayList<HashMap<String, String>>> jsonretorno = new HashMap<String,ArrayList<HashMap<String, String>>>();
        HashMap<String, String> userDat = new HashMap<String, String>();

        //decodificar id de usuario
        Integer id_usuario = Integer.parseInt(Base64Coder.decodeString(id_user_cod));

        userDat = this.getHomeDao().getUserById(id_usuario);
        Integer id_empresa = Integer.parseInt(userDat.get("empresa_id"));

        ArrayList<HashMap<String, String>> z = this.getCxcDao().getCartera_DatosReporteVentaxAgente(id_agente, fecha_inicial, fecha_final, id_empresa); //llama al dao
        return z;


    }

}

