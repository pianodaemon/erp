package com.agnux.kemikal.controllers;

import com.agnux.common.obj.ResourceProject;
import com.agnux.common.obj.UserSessionData;
import com.agnux.kemikal.interfacedaos.GralInterfaceDao;
import com.agnux.kemikal.interfacedaos.HomeInterfaceDao;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.apache.commons.lang.StringUtils;
import org.htmlcleaner.HtmlCleaner;
import org.htmlcleaner.TagNode;
import org.htmlcleaner.XPatherException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.servlet.ModelAndView;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;


@Controller
@SessionAttributes({"user"})
@RequestMapping("/home/")
public class HomeController {
    private static final Logger log  = Logger.getLogger(HomeController.class.getName());
    ResourceProject resource = new ResourceProject();
    
    
    @Autowired
    @Qualifier("daoHome")
    private HomeInterfaceDao HomeDao;
    
    @Autowired
    @Qualifier("daoGral")
    private GralInterfaceDao gralDao;
    
    public GralInterfaceDao getGralDao() {
        return gralDao;
    }
    
    
    public HomeInterfaceDao getHomeDao() {
        return HomeDao;
    }
    
    public void setHomeDao(HomeInterfaceDao HomeDao) {
        this.HomeDao = HomeDao;
    }
    
    public ResourceProject getResource() {
        return resource;
    }
    
    public void setResource(ResourceProject resource) {
        this.resource = resource;
    }
    
    
    
    /*Variables para guardar valores DOF*/
    String title = "";
    String description = "";
    String valueDate = "";
    
    
    String valorUsd="";
    String valorEur="";
    String origenTc="";

    public String getOrigenTc() {
        return origenTc;
    }

    public void setOrigenTc(String origenTc) {
        this.origenTc = origenTc;
    }

    public String getValorEur() {
        return valorEur;
    }

    public void setValorEur(String valorEur) {
        this.valorEur = valorEur;
    }

    public String getValorUsd() {
        return valorUsd;
    }

    public void setValorUsd(String valorUsd) {
        this.valorUsd = valorUsd;
    }
    
    

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getValueDate() {
        return valueDate;
    }

    public void setValueDate(String valueDate) {
        this.valueDate = valueDate;
    }
    
    
    
    
    @RequestMapping(value="/startup.agnux")
    public ModelAndView startUp(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        log.log(Level.INFO, "Ejecutando starUp de {0}", HomeController.class.getName());
        LinkedHashMap<String,String> infoConstruccionTabla = new LinkedHashMap<String,String>();
        String username = "";
        UserSessionData userdata = null;
        
        ModelAndView x = new ModelAndView("home/startup", "title", "Home ERP");
        
        x = x.addObject("layoutheader", resource.getLayoutheader());
        x = x.addObject("layoutmenu", resource.getLayoutmenu());
        x = x.addObject("layoutfooter", resource.getLayoutfooter());
        x = x.addObject("grid", resource.generaGrid(infoConstruccionTabla));
        x = x.addObject("url", resource.getUrl(request));
        
        
        //ModelAndView mav = new ModelAndView("user");
        
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal instanceof UserDetails) {
            username = ((UserDetails)principal).getUsername();
        } else {
            username = principal.toString();
        }
        
        /*
         * 
         * EN ESTE BLOQUE DEBERAN DE IR LAS CONSULTAS AL DAO PARA OBTENER
         * USER ID, EMPRESA ID, EMPLEADO ID
         */ 
        HashMap<String, String> succes = new HashMap<String, String>();
        
        succes = this.getHomeDao().getUserByName(username);
        userdata = new UserSessionData(String.valueOf(succes.get("username")), Integer.parseInt(succes.get("id")), Integer.parseInt(succes.get("empresa_id")), String.valueOf(succes.get("empresa")),Integer.parseInt(succes.get("sucursal_id")), String.valueOf(succes.get("sucursal")), String.valueOf(succes.get("incluye_crm")));
        x.addObject("user", userdata);
        
        x = x.addObject("username", userdata.getUserName());
        x = x.addObject("empresa", userdata.getRazonSocialEmpresa());
        x = x.addObject("sucursal", userdata.getSucursal());
        
        /*Para actualizar el tipo de cambio*/
        this.tipoCambioServiceMethod(Integer.parseInt(succes.get("id")), userdata.getEmpresaId());
        
        String grpcHost = System.getenv("SALES_GRPC_HOST"),
               grpcPort = System.getenv("SALES_GRPC_PORT");

        if (grpcHost == null || grpcPort == null) {
            log.log(Level.SEVERE, "SALES gRPC connection params... not found!!!");
        } else {
            log.log(Level.INFO, "SALES gRPC connection params... in good shape.");
        }

        grpcHost = System.getenv("COBRANZA_GRPC_HOST");
        grpcPort = System.getenv("COBRANZA_GRPC_PORT");

        if (grpcHost == null || grpcPort == null) {
            log.log(Level.SEVERE, "COBRANZA gRPC connection params... not found!!!");
        } else {
            log.log(Level.INFO, "COBRANZA gRPC connection params... in good shape.");
        }
        
        String host = System.getenv("CFDIENGINE_HOST"),
               port = System.getenv("CFDIENGINE_PORT");

        if (host == null || port == null) {
            log.log(Level.SEVERE, "CFDIENGINE connection params... not found!!!");
        } else {
            log.log(Level.INFO, "CFDIENGINE connection params... in good shape.");
        }

        return x;
    }
    
    public void tipoCambioServiceMethod(Integer user_id, Integer empresa_id){
        Date date = new Date();
        //Url default para leer el tipo de cambio
        Integer urlId = 1;
        String urlString = "http://dof.gob.mx/indicadores.xml";
        
        Map<String, Object> map = this.getGralDao().getTipoCambio_Url(empresa_id);
        
        if(map.containsKey("url")){
            if(!"".equals(map.get("url").toString())){
                urlString = map.get("url").toString();
                urlId = Integer.parseInt(map.get("id").toString());
            }
        }
        
        if(map.containsKey("institucion")){
            if(!"".equals(map.get("institucion").toString())){
                this.setOrigenTc(map.get("institucion").toString().toUpperCase());
            }else{
                this.setOrigenTc("DOF");
            }
        }else{
            this.setOrigenTc("DOF");
        }
        
        getXmlV2(urlId, urlString);
        
        //121--Proceso Actualizador Tipo de cambion
        String data_string = "121___new___"+user_id+"___0___"+this.getDescription()+"___dolar%";
        String extra_data_array = "'sin datos'";
        
        String arreglo[];
        arreglo = new String[2];
        
        arreglo[0]= "'"+ "dolar___" + this.getValorUsd() +"___"+ this.getOrigenTc() +"'";
        arreglo[1]= "'"+ "euro___" + this.getValorEur() +"___"+ this.getOrigenTc() +"'";
        
        //Serializar el arreglo
        extra_data_array = StringUtils.join(arreglo, ",");
        
        //System.out.println("antes de jdbc data_string:"+data_string+"     extra_data_array:"+extra_data_array);
        String retorno = this.jobTiposMoneda(data_string, extra_data_array);
        /*
        if(retorno.equals("1")){
            System.out.println("despues de jdbc data_string:"+data_string+"     extra_data_array:"+extra_data_array);
        }else{
            System.out.println("despues de jdbc data_string:"+data_string+"     extra_data_array:"+extra_data_array);
        }
        */
    }
    
    public void getXmlV2(Integer id_url, String urlString){
        try {
            //System.out.println("urlString="+urlString);
            URL url = new URL(urlString);
            URLConnection conn = url.openConnection();
            
            if(id_url==1){
                
                DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
                DocumentBuilder builder = factory.newDocumentBuilder();
                Document doc = builder.parse(conn.getInputStream());

                NodeList ndl = doc.getElementsByTagName("item");
                
                for(int i=0; i < ndl.getLength(); i++ ){
                    NodeList nodl = (NodeList) ndl.item(i);
                    for(int j=0; j < nodl.getLength(); j++ ){
                        Node nditem = nodl.item(j);
                        if (nditem.getNodeName().equals("title") ){
                            setTitle(nditem.getTextContent());
                        }
                        if(title.equals("DOLAR")){
                            if (nditem.getNodeName().equals("description") ){
                                setDescription(nditem.getTextContent());
                                this.setValorUsd(nditem.getTextContent());
                            }
                            if (nditem.getNodeName().equals("valueDate") ){
                                setValueDate(nditem.getTextContent());
                            }
                        }
                    }
                }
                
                //Aqui le asignamos por default el valor cero
                this.setValorEur("0");
            }
            
            if(id_url==2){
                conn.connect();
                //Creamos el objeto con el que vamos a leer
                BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                String inputLine;
                String html = "";
                while ((inputLine = in.readLine()) != null) {
                    html += inputLine;
                }
                in.close();
                
                
                HtmlCleaner cleaner = new HtmlCleaner();
                TagNode node = cleaner.clean(html);
                
                boolean tomarValorUsd=false;
                boolean valorUsdTomado=false;
                boolean tomarValorEuro=false;
                boolean valorEuroTomado=false;
                
                for (Object o : node.evaluateXPath("//td")) {
                    String text = ((TagNode)(o)).getText().toString().trim().toLowerCase().replace("ó", "o");
                    String text2 = text;
                    if(text.contains("dolar venta") && text.length()<=11){
                        //TagNode parent = ((TagNode)o).getParent();
                        if(!valorUsdTomado){
                            tomarValorUsd=true;
                        }
                    }else{
                        if(tomarValorUsd && !valorUsdTomado){
                            this.setValorUsd(text);
                            valorUsdTomado=true;
                        }
                    }
                    
                    if(text2.contains("euro venta") && text2.length()<=10){
                        if(!valorEuroTomado){
                            tomarValorEuro=true;
                        }
                    }else{
                        if(tomarValorEuro && !valorEuroTomado){
                            this.setValorEur(text);
                            valorEuroTomado=true;
                        }
                    }
                    
                    if(valorUsdTomado && valorEuroTomado) break;
                    
                }
            }
            
            //System.out.println("title:"+title+" description:"+description+" valueDate:"+valueDate);
            /*
            TransformerFactory factory1 = TransformerFactory.newInstance();
            Transformer xform = factory1.newTransformer();
            
            // that’s the default xform; use a stylesheet to get a real one
            xform.transform(new DOMSource(doc), new StreamResult(System.out));
            */
            
        } catch (XPatherException ex) {
            Logger.getLogger(HomeController.class.getName()).log(Level.SEVERE, null, ex);
        }
        catch (ParserConfigurationException ex) {
            Logger.getLogger(HomeController.class.getName()).log(Level.SEVERE, null, ex);
        }        catch (SAXException ex) {
            Logger.getLogger(HomeController.class.getName()).log(Level.SEVERE, null, ex);
        }        catch (IOException ex) {
            Logger.getLogger(HomeController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public String jobTiposMoneda(String data_string, String extra_data_array){
        
        String actualizo = "0";
        if(!this.getValorUsd().equals(null) && !this.getValorUsd().equals("")){
            actualizo = this.getGralDao().selectFunctionForThisApp(data_string, extra_data_array);
        }
        //actualizo = this.getGralDao().selectFunctionForThisApp(data_string, extra_data_array);
        
        return actualizo;
    }
    
    

    
    
    

}
