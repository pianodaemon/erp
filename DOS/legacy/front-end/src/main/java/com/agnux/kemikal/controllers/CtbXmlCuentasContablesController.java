package com.agnux.kemikal.controllers;

import com.agnux.cfd.v2.Base64Coder;
import com.agnux.common.helpers.TimeHelper;
import com.agnux.common.obj.ResourceProject;
import com.agnux.common.obj.UserSessionData;
import com.agnux.contae.BeanConstruyeSellaXml;
import com.agnux.kemikal.interfacedaos.CtbInterfaceDao;
import com.agnux.kemikal.interfacedaos.GralInterfaceDao;
import com.agnux.kemikal.interfacedaos.HomeInterfaceDao;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.*;
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
@RequestMapping("/ctbxmlcuentascontables/")
public class CtbXmlCuentasContablesController {
    ResourceProject resource = new ResourceProject();
    private static final Logger log  = Logger.getLogger(CtbXmlCuentasContablesController.class.getName());
    
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
        
        log.log(Level.INFO, "Ejecutando starUp de {0}", CtbXmlCuentasContablesController.class.getName());
        LinkedHashMap<String,String> infoConstruccionTabla = new LinkedHashMap<String,String>();
        ModelAndView x = new ModelAndView("ctbxmlcuentascontables/startup", "title", "Xml de Cuentas Contables");
        
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
    
    
    
    
    //Buscador de clientes
    @RequestMapping(method = RequestMethod.POST, value="/getCreaXml.json")
    public @ResponseBody HashMap<String,Object> getCreaXmlJson(
            @RequestParam(value="iu", required=true) String id_user_cod,
            Model model
        ) {
        
        HashMap<String,Object> jsonretorno = new HashMap<String,Object>();
        
        HashMap<String, String> userDat = new HashMap<String, String>();
        LinkedHashMap<String,String> datos = new LinkedHashMap<String,String>();
        ArrayList<LinkedHashMap<String,String>> cuentas = new ArrayList<LinkedHashMap<String,String>>();
        ArrayList<LinkedHashMap<String,String>> errores = new ArrayList<LinkedHashMap<String,String>>();
        
        System.out.println("Generando xml de cuentas contables");
        
        //Se utiliza el mismo numero de aplicativo de Catalogo de cuentas
        Integer app_selected = 106;
        String command_selected="reporte";
        String anio = TimeHelper.getFechaActualY();
        String mes = TimeHelper.getMesActual();
        
        //Decodificar id de usuario
        Integer id_user = Integer.parseInt(Base64Coder.decodeString(id_user_cod));
        userDat = this.getHomeDao().getUserById(id_user);
        Integer id_empresa = Integer.parseInt(userDat.get("empresa_id"));
        Integer id_sucursal = Integer.parseInt(userDat.get("sucursal_id"));
        
        String rfcEmpresa = this.getGralDao().getRfcEmpresaEmisora(id_empresa);
        String rutaFicheroXml = this.getGralDao().getCfdiTimbreEmitidosDir() + this.getGralDao().getRfcEmpresaEmisora(id_empresa);
        String rutaFicheroCertificado = this.getGralDao().getSslDir() + rfcEmpresa + "/" + this.getGralDao().getCertificadoEmpresaEmisora(id_empresa, id_sucursal);
        String ficheroXsl = this.getGralDao().getXslDir() + rfcEmpresa +"/"+ this.getGralDao().getFicheroXslCuentasContables(id_empresa, id_sucursal);
        String rutaFicheroLlave = this.getGralDao().getSslDir() + rfcEmpresa + "/" + this.getGralDao().getFicheroLlavePrivada(id_empresa, id_sucursal);
        String passwordLlavePrivada = this.getGralDao().getPasswordLlavePrivada(id_empresa, id_sucursal);
        String rutaFicheroXsd = this.getGralDao().getXsdDir() + this.getGralDao().getFicheroXsdXmlCuentasContables(id_empresa, id_sucursal);
        
        /*
        System.out.println("rutaFicheroCertificado= "+rutaFicheroCertificado);
        System.out.println("ficheroXsl= "+ficheroXsl);
        System.out.println("rutaFicheroLlave= "+rutaFicheroLlave);
        System.out.println("passwordLlavePrivada= "+passwordLlavePrivada);
        */
        String file_name = rfcEmpresa.toUpperCase() + anio + mes +"CT";
        
        rutaFicheroXml = rutaFicheroXml +"/"+ file_name + ".xml";
        
        File file_xml = new File(rutaFicheroXml);
        if(file_xml.exists()){
            file_xml.delete();
        }
        
        datos.put("anio", anio);
        datos.put("mes", mes);
        datos.put("noCertificado", this.getGralDao().getNoCertificadoEmpresaEmisora(id_empresa, id_sucursal));
        datos.put("rfc", rfcEmpresa);
        datos.put("version", "1.1");
        
        cuentas = this.getCtbDao().getCtbXml_CuentasContables(app_selected+"___"+id_user+"___"+command_selected);
        
        errores = validarCuentas(cuentas);
        
        if(errores.size()<=0){
            //Instancia a la clase que construye xml
            BeanConstruyeSellaXml xml = new BeanConstruyeSellaXml("CATALOGO",datos, cuentas, rutaFicheroCertificado, ficheroXsl, rutaFicheroLlave, passwordLlavePrivada, rutaFicheroXml, rutaFicheroXsd);

            jsonretorno.put("success",xml.isSuccess());
            jsonretorno.put("msj",xml.getMensaje());

            if(xml.isSuccess()){
                jsonretorno.put("name",file_name);
            }
        }else{
            jsonretorno.put("success",false);
            jsonretorno.put("errores",errores);
            jsonretorno.put("msj","Hay datos requeridos que no se estan proporcionando");
        }

        
        return jsonretorno;
    }
    
    
    //Descarga xml de factura
    @RequestMapping(value = "/getXml/{nombre}/{iu}/out.json", method = RequestMethod.GET ) 
    public ModelAndView getXmlJson(
            @PathVariable("nombre") String nombre_archivo,
            @PathVariable("iu") String id_user,
            HttpServletRequest request, 
            HttpServletResponse response, 
            Model model) throws ServletException, IOException, URISyntaxException {
        
        HashMap<String, String> userDat = new HashMap<String, String>();
        String dirSalidas = "";
        
        //decodificar id de usuario
        Integer id_usuario = Integer.parseInt(Base64Coder.decodeString(id_user));
        
        userDat = this.getHomeDao().getUserById(id_usuario);
        Integer id_empresa = Integer.parseInt(userDat.get("empresa_id"));
        
        dirSalidas = this.getGralDao().getCfdiTimbreEmitidosDir() + this.getGralDao().getRfcEmpresaEmisora(id_empresa);
        
        //ruta completa del archivo a descargar
        String fileout = dirSalidas + "/" + nombre_archivo +".xml";
        
        File file = new File(fileout);
        
        if (file.exists()){
            int size = (int) file.length(); // Tamaño del archivo
            BufferedInputStream bis = new BufferedInputStream(new FileInputStream(file));
            response.setBufferSize(size);
            response.setContentLength(size);
            response.setContentType("text/plain");
            response.setHeader("Content-Disposition","attachment; filename=\"" + file.getName() +"\"");
            FileCopyUtils.copy(bis, response.getOutputStream());  	
            response.flushBuffer();
            
        }
     
        return null;
    }
    
    
    
    private ArrayList<LinkedHashMap<String,String>> validarCuentas(ArrayList<LinkedHashMap<String,String>> cuentas){
        ArrayList<LinkedHashMap<String,String>> retorno=null;        
        boolean error = false;
        
        if(cuentas.size()>0){
            retorno = new ArrayList<LinkedHashMap<String,String>>();
            
            for( LinkedHashMap<String,String> i : cuentas ){
                Iterator it = i.entrySet().iterator();
                error = false;
                
                while (it.hasNext()) {
                    Map.Entry elemento_hash = (Map.Entry)it.next();
                    String llave = (String)elemento_hash.getKey();
                    String valor = (String)elemento_hash.getValue();
                    
                    if (llave.equals("codAgrup")){ 
                        if(valor.equals("") || valor==null){ 
                            error=true;
                        } 
                    }
                    if (llave.equals("numCta")){ 
                        if(valor.equals("") || valor==null){ 
                            error=true;
                        } 
                    }
                    if (llave.equals("desc")){ 
                        if(valor.equals("") || valor==null){ 
                            error=true;
                        } 
                    }
                    if (llave.equals("natur")){ 
                        if(valor.equals("") || valor==null){ 
                            error=true;
                        } 
                    }
                    if (llave.equals("nivel")){
                        if(valor.equals("") || valor==null){ 
                            error=true;
                        } 
                    }
                    /*
                    if (llave.equals("subCtaDe")){ 
                        if(valor.equals("") || valor==null){ 
                            error=true;
                        } 
                    }
                    */
                }
                
                //System.out.println("codAgrup="+i.get("codAgrup")+" | numCta="+i.get("numCta")+" | subCtaDe="+i.get("subCtaDe")+" | natur="+i.get("natur")+" | nivel="+i.get("nivel")+" | desc="+i.get("desc"));
                
                if(error){ 
                    
                    retorno.add(i);
                }
            } 
        }       
        
        return retorno;
    }
    

    
    
    /*
    //Genera pdf Reporte Auxiliar de Cuentas
    @RequestMapping(value = "/getXml/{iu}/out.json", method = RequestMethod.GET )
    public ModelAndView getXmlJson(
                @PathVariable("iu") String id_user_cod,
                HttpServletRequest request,
                HttpServletResponse response,
                Model model)
        throws ServletException, IOException, URISyntaxException, DocumentException {
        
        HashMap<String, String> userDat = new HashMap<String, String>();
        LinkedHashMap<String,String> datos = new LinkedHashMap<String,String>();
        ArrayList<LinkedHashMap<String,String>> cuentas = new ArrayList<LinkedHashMap<String,String>>();
        
        System.out.println("Generando xml de cuentas contables");
        
        //Se utiliza el mismo numero de aplicativo de Catalogo de cuentas
        Integer app_selected = 106;
        String command_selected="reporte";
        
        //Decodificar id de usuario
        Integer id_user = Integer.parseInt(Base64Coder.decodeString(id_user_cod));
        userDat = this.getHomeDao().getUserById(id_user);
        Integer id_empresa = Integer.parseInt(userDat.get("empresa_id"));
        Integer id_sucursal = Integer.parseInt(userDat.get("sucursal_id"));
        
        String rfcEmpresa = this.getGralDao().getRfcEmpresaEmisora(id_empresa);
        String rutaFicheroCertificado = this.getGralDao().getSslDir() + rfcEmpresa + "/" + this.getGralDao().getCertificadoEmpresaEmisora(id_empresa, id_sucursal);
        String ficheroXsl = this.getGralDao().getXslDir() + rfcEmpresa +"/"+ this.getGralDao().getFicheroXslCuentasContables(id_empresa, id_sucursal);
        String rutaFicheroLlave = this.getGralDao().getSslDir() + rfcEmpresa + "/" + this.getGralDao().getFicheroLlavePrivada(id_empresa, id_sucursal);
        String passwordLlavePrivada = this.getGralDao().getPasswordLlavePrivada(id_empresa, id_sucursal);
        String rutaFicheroXml = this.getGralDao().getCfdiTimbreEmitidosDir() + this.getGralDao().getRfcEmpresaEmisora(id_empresa);
        String rutaFicheroXsd = this.getGralDao().getXsdDir() + this.getGralDao().getFicheroXsdCfdi(id_empresa, id_sucursal);
        
        System.out.println("rutaFicheroCertificado= "+rutaFicheroCertificado);
        System.out.println("ficheroXsl= "+ficheroXsl);
        System.out.println("rutaFicheroLlave= "+rutaFicheroLlave);
        System.out.println("passwordLlavePrivada= "+passwordLlavePrivada);
        
        String anio = TimeHelper.getFechaActualY();
        String mes = TimeHelper.getMesActual();
        
        datos.put("anio", anio);
        datos.put("mes", mes);
        datos.put("noCertificado", this.getGralDao().getNoCertificadoEmpresaEmisora(id_empresa, id_sucursal));
        datos.put("rfc", rfcEmpresa);
        datos.put("version", "1.1");
        
        cuentas = this.getCtbDao().getCtbXml_CuentasContables(app_selected+"___"+id_user+"___"+command_selected);
        
        
        String file_name = rfcEmpresa.toUpperCase() + anio + mes +"CT.xml";
        
        rutaFicheroXml = rutaFicheroXml +"/"+ file_name;
        
        //Instancia a la clase que construye xml
        BeanConstruyeSellaXml xml = new BeanConstruyeSellaXml(datos, cuentas, rutaFicheroCertificado, ficheroXsl, rutaFicheroLlave, passwordLlavePrivada, rutaFicheroXml, rutaFicheroXsd);
        
        
        
        //Guardar xml
        
        
        //Descargar
        response.setBufferSize(xml.getBaos().size());
        response.setContentLength(xml.getBaos().size());
        response.setContentType("text/plain");
        response.setHeader("Content-Disposition","attachment; filename=\"" + file_name +"\"");
        OutputStream os = response.getOutputStream();
        xml.getBaos().writeTo(os);
        os.flush();
        os.close();
        
        return null;
    }
    */
    
  
}
