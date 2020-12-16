/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.agnux.kemikal.controllers;

import com.agnux.cfd.v2.Base64Coder;
import com.agnux.common.obj.ResourceProject;
import com.agnux.common.obj.UserSessionData;
import com.agnux.kemikal.interfacedaos.CxcInterfaceDao;
import com.agnux.kemikal.interfacedaos.GralInterfaceDao;
import com.agnux.kemikal.interfacedaos.HomeInterfaceDao;
import com.agnux.kemikal.reportes.PdfReporteAnticiposNoAplicados;

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
 * @author luis Carrillo
 *
 */
@Controller
@SessionAttributes({"user"})
@RequestMapping("/repanticiposnoaplicados/")
public class RepAnticipiosNoAplicadosController {
    private static final Logger log  = Logger.getLogger(RepAnticipiosNoAplicadosController.class.getName());
    ResourceProject resource = new ResourceProject();
    
    @Autowired
    @Qualifier("daoCxc")
    private CxcInterfaceDao cxcDao;
    
    @Autowired
    @Qualifier("daoHome")
    private HomeInterfaceDao HomeDao;
    
    @Autowired
    @Qualifier("daoGral")
    private GralInterfaceDao gralDao;
    
    public CxcInterfaceDao getCxcDao() {
        return cxcDao;
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
        
        log.log(Level.INFO, "Ejecutando starUp de {0}", RepAnticipiosNoAplicadosController.class.getName());
        LinkedHashMap<String,String> infoConstruccionTabla = new LinkedHashMap<String,String>();
        
        ModelAndView x = new ModelAndView("repanticiposnoaplicados/startup", "title", "Reporte de Anticipios no Aplicados");
        
        x = x.addObject("layoutheader", resource.getLayoutheader());
        x = x.addObject("layoutmenu", resource.getLayoutmenu());
        x = x.addObject("layoutfooter", resource.getLayoutfooter());
        x = x.addObject("grid", resource.generaGrid(infoConstruccionTabla));
        x = x.addObject("url", resource.getUrl(request));
        x = x.addObject("username", user.getUserName());
        x = x.addObject("empresa", user.getRazonSocialEmpresa());
        x = x.addObject("empresa", user.getRazonSocialEmpresa());
        
        String userId = String.valueOf(user.getUserId());
        
        String codificado = Base64Coder.encodeString(userId);
        
        //id de usuario codificado
        x = x.addObject("iu", codificado);
        
        return x;
    }
    
    //Buscador de clientes
    @RequestMapping(method = RequestMethod.POST, value="/get_buscador_clientes.json")
    public @ResponseBody HashMap<String,ArrayList<HashMap<String, Object>>> get_buscador_clientesJson(
        @RequestParam(value="cadena", required=true) String cadena,
        @RequestParam(value="filtro", required=true) Integer filtro,
        @RequestParam(value="iu", required=true) String id_user,
        Model model
        ){
        
        HashMap<String,ArrayList<HashMap<String, Object>>> jsonretorno = new HashMap<String,ArrayList<HashMap<String, Object>>>();
        HashMap<String, String> userDat = new HashMap<String, String>();

        //decodificar id de usuario
        Integer id_usuario = Integer.parseInt(Base64Coder.decodeString(id_user));
        //System.out.println("id_usuario: "+id_usuario);

        userDat = this.getHomeDao().getUserById(id_usuario);

        Integer id_empresa = Integer.parseInt(userDat.get("empresa_id"));
        Integer id_sucursal = Integer.parseInt(userDat.get("sucursal_id"));

        jsonretorno.put("Clientes", this.getCxcDao().getBuscadorClientes(cadena,filtro,id_empresa, id_sucursal));

        return jsonretorno;
    }
    
    //repanticiposnoaplicados/getAnticiposnoAplicados/out.json
    //obtiene los datos para la vista del reporte de anticipos no aplicados.
   @RequestMapping(value="/getAnticiposnoAplicados/out.json", method = RequestMethod.POST)
    public @ResponseBody HashMap<String,ArrayList<HashMap<String,String>>>getAnticiposnoApliadosJson(
            @RequestParam(value="fecha_inicial") String fecha_inicial,
            @RequestParam(value="fecha_final") String fecha_final,
            @RequestParam(value="cliente") Integer cliente,
            @RequestParam(value="iu",required=true) String id_user_cod
            ){
        System.out.println("Me esta arrojando esto:"+fecha_inicial+fecha_final+cliente+id_user_cod);
        log.log(Level.INFO, "Ejecutando getAnticiposnoApliadosJson de {0}", RepAnticipiosNoAplicadosController.class.getName());
        HashMap<String,ArrayList<HashMap<String,String>>>jsonretorno = new HashMap<String,ArrayList<HashMap<String,String>>>();
        HashMap<String,String>userDat = new HashMap<String,String>();
        
        ArrayList<HashMap<String, String>>  anticipos = new ArrayList<HashMap<String, String>>();///* //mi ed 1 start
        ArrayList<HashMap<String, String>>  totales = new ArrayList<HashMap<String, String>>();
        LinkedHashMap<String,String> total = new LinkedHashMap<String,String>();
       
        
        //decodificar id de usuario
        Integer id_usuario = Integer.parseInt(Base64Coder.decodeString(id_user_cod));
        
        userDat = this.getHomeDao().getUserById(id_usuario);
        
        Integer id_empresa = Integer.parseInt(userDat.get("empresa_id"));
        
        anticipos = this.getCxcDao().getAnticiposnoAplicados( fecha_inicial, fecha_final, cliente,  id_empresa);
        
        
        
        jsonretorno.put("Cobranza", anticipos);
        jsonretorno.put("Totales", totales);
        
     
        return jsonretorno;
    }
    
   
    //PDF reporte de ventas netas por Cliente/producto desglosado por factura
     @RequestMapping(value = "/getAnticiposnoAplicados/{cadena}/out.json", method = RequestMethod.GET ) 
     public ModelAndView PdfAnticiposnoAplicados(
        @PathVariable("cadena") String cadena,
            HttpServletRequest request,
            HttpServletResponse response, 
            Model model
        )   
     throws ServletException, IOException, URISyntaxException, DocumentException {
        String arreglo[];
        arreglo = cadena.split("___");
        //arreglo[0]    cliente
        //arreglo[1]   fecha inicial
        //arreglo[2]  fecha final
        //arreglo[3] usuari0
                
        HashMap<String, String> userDat = new HashMap<String, String>();
        
        System.out.println("Generando Reporte de Aticipos no Aplicados");
        
        String dir_tmp = this.getGralDao().getTmpDir();
        
        File file_dir_tmp = new File(dir_tmp);
        String file_name = "AnticiposnoAutorizados"+arreglo[1]+"_"+arreglo[2]+".pdf";
        
        //ruta de archivo de salida
        String fileout = file_dir_tmp +"/"+  file_name;
        
        ArrayList<HashMap<String, String>> anticipos = new ArrayList<HashMap<String, String>>();
        HashMap<String, String> datosEncabezadoPie= new HashMap<String, String>();
        HashMap<String, String> datos= new HashMap<String, String>();
        
        //decodificar id de usuario
        Integer id_usuario = Integer.parseInt(Base64Coder.decodeString(arreglo[3]));
        Integer app_selected = 117;//Reporte Anticipos no Autorizados
        
        userDat = this.getHomeDao().getUserById(id_usuario);
        Integer id_empresa = Integer.parseInt(userDat.get("empresa_id"));
        String rfc_empresa=this.getGralDao().getRfcEmpresaEmisora(id_empresa);
        String razon_social_empresa = this.getGralDao().getRazonSocialEmpresaEmisora(id_empresa);
        
        String titulo_reporte ="Anticipos No Autorizados";
        datosEncabezadoPie.put("nombre_empresa_emisora", razon_social_empresa);
        datosEncabezadoPie.put("titulo_reporte", titulo_reporte);
        datosEncabezadoPie.put("codigo1", "");
        datosEncabezadoPie.put("codigo2","");
        datos.put("fecha_inicial",arreglo[1]);
        datos.put("fecha_final",arreglo[2]);
        
        //obtiene los depositos del periodo indicado
        String codigo="";
        String descripcion="";
        Integer idcliente = Integer.parseInt(arreglo[0]);
       
        anticipos = this.getCxcDao().getAnticiposnoAplicados( arreglo[1], arreglo[2],idcliente,  id_empresa);
        
        //instancia a la clase que construye el pdf de Cobranza Diaria
        PdfReporteAnticiposNoAplicados x = new PdfReporteAnticiposNoAplicados(datosEncabezadoPie, fileout,anticipos,datos);
        
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
