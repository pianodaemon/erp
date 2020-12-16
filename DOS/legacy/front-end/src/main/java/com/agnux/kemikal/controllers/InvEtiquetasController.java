package com.agnux.kemikal.controllers;

import com.agnux.cfd.v2.Base64Coder;
import com.agnux.common.helpers.FileHelper;
import com.agnux.common.helpers.StringHelper;
import com.agnux.common.obj.DataPost;
import com.agnux.common.obj.ResourceProject;
import com.agnux.common.obj.UserSessionData;
import com.agnux.kemikal.interfacedaos.GralInterfaceDao;
import com.agnux.kemikal.interfacedaos.HomeInterfaceDao;
import com.agnux.kemikal.interfacedaos.InvInterfaceDao;
import com.agnux.kemikal.reportes.PdfEtiqueta_Compras;
import com.agnux.kemikal.reportes.PdfOrdenEntrada;
import com.agnux.xml.labels.EtiquetaCompras;
import com.agnux.xml.labels.EtiquetaProduccion;
import com.agnux.xml.labels.EtiquetaRequisicion;
import com.agnux.xml.labels.generandoxml;
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
import javax.xml.transform.TransformerException;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;


@Controller
@SessionAttributes({"user"})
@RequestMapping("/invetiquetas/")
public class InvEtiquetasController {
     ResourceProject resource = new ResourceProject();
    private static final Logger log  = Logger.getLogger(InvEtiquetasController.class.getName());
    
    @Autowired
    @Qualifier("daoInv")   //utilizo todos los metodos de invinterfacedao
    private InvInterfaceDao invDao;
    
    public InvInterfaceDao getInvDao() {
        return invDao;
    }
    
    @Autowired
    @Qualifier("daoHome")   //permite controlar usuarios que entren
    private HomeInterfaceDao HomeDao;
    
    public HomeInterfaceDao getHomeDao() {
        return HomeDao;
    }
    
    @Autowired
    @Qualifier("daoGral")
    private GralInterfaceDao gralDao;
    
    public GralInterfaceDao getGralDao() {
        return gralDao;
    }
    
    @RequestMapping(value="/startup.agnux")
    public ModelAndView startUp(HttpServletRequest request, HttpServletResponse response,
     @ModelAttribute("user") UserSessionData user)throws ServletException, IOException {
        
        log.log(Level.INFO, "Ejecutando starUp de {0}", InvEtiquetasController.class.getName());
        LinkedHashMap<String,String> infoConstruccionTabla = new LinkedHashMap<String,String>();
       
        infoConstruccionTabla.put("id", "Acciones:90");
        infoConstruccionTabla.put("folio", "Folio:130");
        infoConstruccionTabla.put("folio_origen", "Folio Origen:130");
        infoConstruccionTabla.put("tipo_origen", "Tipo Origen:130");
        
        
        
        ModelAndView x = new ModelAndView("invetiquetas/startup", "title", "Cat&aacute;logo de Impresion de Etiquetas");
        
        x = x.addObject("layoutheader", resource.getLayoutheader());
        x = x.addObject("layoutmenu", resource.getLayoutmenu());
        x = x.addObject("layoutfooter", resource.getLayoutfooter());
        x = x.addObject("grid", resource.generaGrid(infoConstruccionTabla));
       
        x = x.addObject("url", resource.getUrl(request));
        x = x.addObject("username", user.getUserName());
        x = x.addObject("empresa", user.getRazonSocialEmpresa());
        x = x.addObject("sucursal", user.getSucursal());
       
        String userId = String.valueOf(user.getUserId());
        
        //codificar id de usuario
        String codificado = Base64Coder.encodeString(userId);
       
        //decodificar id de usuario
        String decodificado = Base64Coder.decodeString(codificado);
        
        //id de usuario codificado
        x = x.addObject("iu", codificado);
     
        return x;
    }
    
     @RequestMapping(value="/getAllEtiquetas.json", method = RequestMethod.POST)
    public @ResponseBody HashMap<String,ArrayList<HashMap<String, Object>>> getAllEtiquetasJson(
           @RequestParam(value="orderby", required=true) String orderby,
           @RequestParam(value="desc", required=true) String desc,
           @RequestParam(value="items_por_pag", required=true) int items_por_pag,
           @RequestParam(value="pag_start", required=true) int pag_start,
           @RequestParam(value="display_pag", required=true) String display_pag,
           @RequestParam(value="input_json", required=true) String input_json,
           @RequestParam(value="cadena_busqueda", required=true) String cadena_busqueda,
           @RequestParam(value="iu", required=true) String id_user_cod,
           Model modcel) {
           
        HashMap<String,ArrayList<HashMap<String, Object>>> jsonretorno = new HashMap<String,ArrayList<HashMap<String, Object>>>();
        HashMap<String,String> has_busqueda = StringHelper.convert2hash(StringHelper.ascii2string(cadena_busqueda));
        
        //aplicativo catalogo de Impresion de Etiquetas
      
        Integer app_selected = 98;
        
        //decodificar id de usuario
        Integer id_usuario = Integer.parseInt(Base64Coder.decodeString(id_user_cod));
        
        //variables para el buscador
        String folio = "%"+StringHelper.isNullString(String.valueOf(has_busqueda.get("folio")))+"%";        
        String folio_origen = "%"+StringHelper.isNullString(String.valueOf(has_busqueda.get("folio_origen")))+"%";        
        String tipo_origen = StringHelper.isNullString(String.valueOf(has_busqueda.get("tipo_origen"))); 
        
        System.out.println("Tipo de origen:::"+tipo_origen);
        
        String data_string =null;
       
           
           data_string = app_selected+"___"+id_usuario+"___"+folio+"___"+folio_origen+"___"+tipo_origen;
        
        //obtiene total de registros en base de datos, con los parametros de busqueda
        int total_items = this.getInvDao().countAll(data_string);              
                
        
        //calcula el total de paginas
        int total_pags = resource.calculaTotalPag(total_items,items_por_pag);
        
        //variables que necesita el datagrid, para no tener que hacer uno por cada aplicativo
        DataPost dataforpos = new DataPost(orderby, desc, items_por_pag, pag_start, display_pag, input_json, cadena_busqueda,total_items,total_pags, id_user_cod);
        
        int offset = resource.__get_inicio_offset(items_por_pag, pag_start);
        
        //obtiene los registros para el grid, de acuerdo a los parametros de busqueda
        jsonretorno.put("Data", this.getInvDao().getEtiquetas_PaginaGrid(data_string, offset, items_por_pag, orderby, desc));
        //obtiene el hash para los datos que necesita el datagrid
        jsonretorno.put("DataForGrid", dataforpos.formaHashForPos(dataforpos));
        
        return jsonretorno;
    }
     
     
     @RequestMapping(method = RequestMethod.POST, value="/getEtiquetas.json")
    public @ResponseBody HashMap<String,ArrayList<HashMap<String, String>>> getEtiquetasJson(
            @RequestParam(value="id", required=true) Integer id,
            @RequestParam(value="tipo_origen", required=true) Integer tipo_origen,
            @RequestParam(value="iu", required=true) String id_user_cod,
            Model model
            ){
        
        log.log(Level.INFO, "Ejecutando getEtiquetasjson de {0}", InvEtiquetasController.class.getName());
        HashMap<String,ArrayList<HashMap<String, String>>> jsonretorno = new HashMap<String,ArrayList<HashMap<String, String>>>();
        HashMap<String, String> userDat = new HashMap<String, String>();
       
        
        ArrayList<HashMap<String, String>> datosEtiquetas_header = new ArrayList<HashMap<String, String>>(); 
        ArrayList<HashMap<String, String>> datosEtiquetas_grid = new ArrayList<HashMap<String, String>>(); 
        ArrayList<HashMap<String, String>> medidas_etiqueta = new ArrayList<HashMap<String, String>>();
       
        //decodificar id de usuario
        Integer id_usuario = Integer.parseInt(Base64Coder.decodeString(id_user_cod));
        userDat = this.getHomeDao().getUserById(id_usuario);
        
       // Integer id_empresa = Integer.parseInt(userDat.get("empresa_id"));
       
        System.out.println("id: "+id+"tipo_origen:  "+tipo_origen);
        if( id != 0  ){
            datosEtiquetas_header = this.getInvDao().getEtiquetas_Datos_header(id);
            datosEtiquetas_grid = this.getInvDao().getEtiquetas_Datos_grid(id,tipo_origen);
            medidas_etiqueta = this.getInvDao().getReporteExistenciasLotes_MedidasEtiquetas();
        }
        
       //datos etiquetas es lo que me trajo de la consulta y los pone en el json
       
       jsonretorno.put("Etiquetas_grid", datosEtiquetas_grid);
       jsonretorno.put("Etiquetas_header", datosEtiquetas_header);
       jsonretorno.put("MedidasEtiqueta", medidas_etiqueta);
     
       return jsonretorno;
    }
   
     
     //buscador de Entradas
     @RequestMapping(method = RequestMethod.POST, value="/getBuscadorEntradas.json")
    public @ResponseBody HashMap<String,ArrayList<HashMap<String, String>>> getBuscadorEntradasJson(
            @RequestParam(value="folio", required=true) String folio,
            @RequestParam(value="fecha_inicial", required=true) String fecha_inicial,
            @RequestParam(value="fecha_final", required=true) String fecha_final,
            @RequestParam(value="tipo_origen", required=true) Integer tipo_origen,
            @RequestParam(value="iu", required=true) String id_user,
            Model model
            ) {
       
        HashMap<String,ArrayList<HashMap<String, String>>> jsonretorno = new HashMap<String,ArrayList<HashMap<String, String>>>();
        HashMap<String, String> userDat = new HashMap<String, String>();
        
        //decodificar id de usuario
        Integer id_usuario = Integer.parseInt(Base64Coder.decodeString(id_user));
        
        userDat = this.getHomeDao().getUserById(id_usuario);
        Integer id_empresa = Integer.parseInt(userDat.get("empresa_id"));  
        
        
        jsonretorno.put("entradas", this.getInvDao().getBuscadorEntradas(folio,fecha_inicial,fecha_final,tipo_origen,id_empresa));
      
        return jsonretorno;
    }
    
    //cargando el grid de entradas(primer plugin) EL GRID SE CARGA CON ENTRADAS, PRODUCCION Y REQUISICION DE ACUERDO AL TIPO ORIGEN QUE SE LE ENVIE EL EL JSON.
     @RequestMapping(method = RequestMethod.POST, value="/getgridEntradas.json")
    public @ResponseBody HashMap<String,ArrayList<HashMap<String, String>>> getgetgridEntradasJson(
            @RequestParam(value="folio", required=true) String folio,
            @RequestParam(value="fecha_inicial",required=true) String fecha_inicial,
            @RequestParam(value="fecha_final", required=true) String fecha_final,
            @RequestParam(value="tipo_origen", required=true) Integer tipo_origen,
            @RequestParam(value="iu", required=true) String id_user,
            Model model
            ) {
        
        HashMap<String,ArrayList<HashMap<String, String>>> jsonretorno = new HashMap<String,ArrayList<HashMap<String, String>>>();
        HashMap<String, String> userDat = new HashMap<String, String>();
        ArrayList<HashMap<String, String>> medidas_etiqueta = new ArrayList<HashMap<String, String>>();
        
        //decodificar id de usuario
        Integer id_usuario = Integer.parseInt(Base64Coder.decodeString(id_user));
        
        userDat = this.getHomeDao().getUserById(id_usuario);
        Integer id_empresa = Integer.parseInt(userDat.get("empresa_id"));  
        
        medidas_etiqueta = this.getInvDao().getReporteExistenciasLotes_MedidasEtiquetas();
        
        jsonretorno.put("grid_entradas", this.getInvDao().getgridEntradas(folio,fecha_inicial,fecha_final,tipo_origen,id_empresa));
        jsonretorno.put("MedidasEtiqueta", medidas_etiqueta);
        
        return jsonretorno;
    }
     
    //crear y editar una ETIQUETA.
    @RequestMapping(method = RequestMethod.POST, value="/edit.json")
    public @ResponseBody HashMap<String, String> editJson(
            @RequestParam(value="identificador",           required=true) Integer id,
            @RequestParam(value="accion",                  required=true) String accion,
            @RequestParam(value="etiqueta_detalle_id",     required=true) String[] etiqueta_detalle_id,
            @RequestParam(value="tipo_origen",             required=true) Integer tipo_origen,
            @RequestParam(value="folio_or",                required=true) String folio_origen,
            @RequestParam(value="lote_interno",            required=true) String[] lote_interno,
            @RequestParam(value="cantidad",                required=true) String[] cantidad,
            @RequestParam(value="cantidad_produccion",     required=true) String[] cantidad_produccion,
            @RequestParam(value="id_producto",             required=true) String[] producto_id,
            @RequestParam(value="id_lote",                 required=true) String[] lote_id,
            @RequestParam(value="inv_etiqueta_medidas_id", required=true) String[] inv_etiqueta_medidas_id,
            @RequestParam(value="tipo_de_producto_id",     required=true) String[] tipo_de_producto_id,
            @RequestParam(value="inv_etiquetas_id",     required=true) String[] inv_etiquetas_id,
            
            
                                 
            HttpServletRequest request, 
            HttpServletResponse response,
            Model model,@ModelAttribute("user") UserSessionData user
            ) throws TransformerException, IOException {
        
            HashMap<String, String> jsonretorno = new HashMap<String, String>();
            HashMap<String, String> succes = new HashMap<String, String>();
            HashMap<String, String> userDat = new HashMap<String, String>();
        

            LinkedHashMap<String, Object> datos_etiqueta; 
            Integer app_selected = 98;//catalogo de agentes
            String command_selected = "new";
            Integer id_usuario= user.getUserId();//variable para el id  del usuario
            
            userDat = this.getHomeDao().getUserById(id_usuario);
            Integer id_empresa = Integer.parseInt(userDat.get("empresa_id"));
            // String extra_data_array = "'sin datos'";
            String actualizo = "0";

            ////////###############    obteniendo la ip del cliente   ########/////////////////
            String ip_cliente = request.getRemoteAddr();
            System.out.println("ip_cliente1: "+ip_cliente);
            ip_cliente = ip_cliente.replace(".", "");
            
            
            String arreglo[];
            arreglo = new String[cantidad.length];
            System.out.println("checando el id de de latabla etiquetas detalle::::::   ");
            
            for(int i=0; i<cantidad.length; i++) {
                arreglo[i]= "'"+ lote_interno[i]+"___" + cantidad[i]+"___" + producto_id[i]+"___" + lote_id[i]+"___" + cantidad_produccion[i]+"___" +inv_etiqueta_medidas_id[i]+"___"+etiqueta_detalle_id[i]+"'";
                //System.out.println(arreglo[i]);
                System.out.println("etiqueta_detalle_id:::    "+etiqueta_detalle_id[i]);  
            }
            
            
            //serializar el arreglo
            String extra_data_array = StringUtils.join(arreglo, ",");
            
        if( id==0 ){
            command_selected = "new";
        }else {
            command_selected = "edit";
            //LinkedHashMap<String, Object> datos_etiqueta= new LinkedHashMap<String, Object>();
            if (accion.equals("generando_xml_etiqueta")){
                    generandoxml xml=null;
                    EtiquetaCompras file_xml=null;
                    EtiquetaProduccion file_xml_produccion =null;
                    EtiquetaRequisicion  file_xml_requisicion=null;
                    
                int contador=0;
                for (int i=0; i<producto_id.length; i++){
                    int cant = Integer.parseInt(cantidad[i]);                
                        if (tipo_origen == 1)  {
                            System.out.println("Generando XML  de ETIQUETAS COMPRAS");
                            
                            for (int c=1; c<=cant; c++){
                                datos_etiqueta = new LinkedHashMap<String, Object>();
                                datos_etiqueta = this.getInvDao().getDatosEtiquetaLote(Integer.parseInt(lote_id[i]), Integer.parseInt(tipo_de_producto_id[i]), Integer.parseInt(inv_etiqueta_medidas_id[i]));    
                                String file_name = ip_cliente+"_"+ lote_interno[i]+"_"+c+".xml";
                                datos_etiqueta.put("cuerpo_titulo", "Etiqueta_Compras");
                                datos_etiqueta.put("nombre_archivo", file_name); 
                                if (datos_etiqueta.size()<=0){
                                    System.out.println("No trajo datos");
                                }else{
                                    file_xml = new EtiquetaCompras(datos_etiqueta);

                                    xml =new generandoxml(file_xml.getDoc());
                                    String  xmlString=xml.createxml(file_xml.getDoc());

                                    //obtener el directorio temporal
                                    String dir_tmp = this.getGralDao().getZebraDir();
                                    
                                    File file_dir_tmp = new File(dir_tmp);
                                    System.out.println("Directorio temporal: "+file_dir_tmp.getCanonicalPath());
                                    
                                    
                                    //ruta de archivo de salida
                                    String ruta = file_dir_tmp +"/in";
                                    String nombre_archivo = file_name;
                                    //File file = new File(fileout);

                                    System.out.println("IMPRIMIENDO la Ruta:\n\n" + ruta);
                                    System.out.println("IMPRIMIENDO el nombre del Archivo\n\n" + nombre_archivo);
                                    System.out.println("IMPRIMIENDO Ruta completa\n\n" + ruta +"/"+nombre_archivo);
                                    //System.out.println("IMPRIMIENDO EL  XML:\n\n" + xmlString);
                                    //FileHelper.createFileWithText("/home/mi_compu/NetBeansProjects/Ejemploxml", "xmletiquetaproduccion_b", xmlString);
                                    //FileHelper.createFileWithText(ruta,nombre_archivo, xmlString);
                                    File file = new File(ruta+"/"+file_name);

                                    if(!file.exists()){
                                        FileHelper.createFileWithText(ruta,file_name, xmlString);
                                    }
                                    
                                }
                            }
                         }
                        if (tipo_origen == 2)  {
                            System.out.println("Generando XML  de ETIQUETAS PRODUCCION");
                            
                            for (int c=1; c<=cant; c++){
                                datos_etiqueta = new LinkedHashMap<String, Object>();
                                
                                datos_etiqueta = this.getInvDao().getDatosEtiquetaLote_produccion(lote_interno[i],Integer.parseInt( inv_etiquetas_id[i]), Integer.parseInt(inv_etiqueta_medidas_id[i]));    
                                String file_name = ip_cliente+"_"+ lote_interno[i]+"_"+c+".xml";
                                datos_etiqueta.put("nombre_archivo",file_name); 
                                datos_etiqueta.put("cuerpo_titulo","etiqueta_Produccion");
                                if (datos_etiqueta.size()<=0){
                                    System.out.println("No trajo datos");
                                }else{
                                file_xml_produccion = new EtiquetaProduccion(datos_etiqueta);
                                
                                xml =new generandoxml(file_xml_produccion.getDoc());
                                String  xmlString=xml.createxml(file_xml_produccion.getDoc());

                                //obtener el directorio temporal
                                String dir_tmp = this.getGralDao().getZebraDir();

                                File file_dir_tmp = new File(dir_tmp);
                                System.out.println("Directorio temporal: "+file_dir_tmp.getCanonicalPath());

                                
                                //ruta de archivo de salida
                                String ruta = file_dir_tmp +"/in";
                                String nombre_archivo = file_name;
                                //File file = new File(fileout);

                                System.out.println("IMPRIMIENDO la Ruta:\n\n" + ruta);
                                System.out.println("IMPRIMIENDO el nombre del Archivo\n\n" + nombre_archivo);
                                System.out.println("IMPRIMIENDO Ruta completa\n\n" + ruta +"/"+nombre_archivo);
                                //System.out.println("IMPRIMIENDO EL  XML:\n\n" + xmlString);
                                //FileHelper.createFileWithText("/home/mi_compu/NetBeansProjects/Ejemploxml", "xmletiquetaproduccion_b", xmlString);
                                //FileHelper.createFileWithText(ruta,nombre_archivo, xmlString);
                                
                                    File file = new File(ruta+"/"+file_name);
                    
                                    if(!file.exists()){
                                        FileHelper.createFileWithText(ruta,file_name, xmlString);
                                    }
                                }
                            }
                         }
                        
                         if (tipo_origen == 3)  {
                            System.out.println("Generando XML  de ETIQUETAS REQUISICION");
                            
                            for (int c=1; c<=cant; c++){
                                datos_etiqueta = new LinkedHashMap<String, Object>();
                                
                                datos_etiqueta = this.getInvDao().getDatosEtiquetaLote_requisicion(etiqueta_detalle_id[i]);    
                                String file_name = ip_cliente+"_"+ lote_interno[i]+"_"+c+".xml";
                                datos_etiqueta.put("nombre_archivo",file_name); 
                                datos_etiqueta.put("cuerpo_titulo","etiqueta_Requisicion");
                                
                                    
                                if (datos_etiqueta.size()<=0){
                                    System.out.println("No trajo datos");
                                }else{
                                file_xml_requisicion = new EtiquetaRequisicion(datos_etiqueta);
                                
                                xml =new generandoxml(file_xml_requisicion.getDoc());
                                String  xmlString=xml.createxml(file_xml_requisicion.getDoc());

                                //obtener el directorio temporal
                                String dir_tmp = this.getGralDao().getZebraDir();

                                File file_dir_tmp = new File(dir_tmp);
                                System.out.println("Directorio temporal: "+file_dir_tmp.getCanonicalPath());

                                
                                //ruta de archivo de salida
                                String ruta = file_dir_tmp +"/in";
                                String nombre_archivo = file_name;
                                //File file = new File(fileout);

                                System.out.println("IMPRIMIENDO la Ruta:\n\n" + ruta);
                                System.out.println("IMPRIMIENDO el nombre del Archivo\n\n" + nombre_archivo);
                                System.out.println("IMPRIMIENDO Ruta completa\n\n" + ruta +"/"+nombre_archivo);
                                //System.out.println("IMPRIMIENDO EL  XML:\n\n" + xmlString);
                                //FileHelper.createFileWithText("/home/mi_compu/NetBeansProjects/Ejemploxml", "xmletiquetaproduccion_b", xmlString);
                                //FileHelper.createFileWithText(ruta,nombre_archivo, xmlString);
                                
                                    File file = new File(ruta+"/"+file_name);
                    
                                    if(!file.exists()){
                                        FileHelper.createFileWithText(ruta,file_name, xmlString);
                                    }
                                }
                            }
                         }
                         
                        
                contador++;     
                }
                
            
            }
        }
        
        //                         1                    2                       3       4           5                   6                                 
        String data_string = app_selected+"___"+command_selected+"___"+id_usuario+"___"+id+"___"+folio_origen+"___"+tipo_origen;
        
        succes = this.getInvDao().selectFunctionValidateAaplicativo(data_string,app_selected,extra_data_array);
        
        
        log.log(Level.INFO, "despues de validacion {0}", String.valueOf(succes.get("success")));
        if( String.valueOf(succes.get("success")).equals("true") ){
            actualizo = this.getInvDao().selectFunctionForApp_MovimientosInventario(data_string, extra_data_array);
        }
        
        jsonretorno.put("success",String.valueOf(succes.get("success")));
        
        log.log(Level.INFO, "Salida json {0}", String.valueOf(jsonretorno.get("success")));
        return jsonretorno;
    }
    
     //cambia el estatus del borrado logico
    @RequestMapping(method = RequestMethod.POST, value="/logicDelete.json")
    public @ResponseBody HashMap<String, String> logicDeleteJson(
            @RequestParam(value="id", required=true) Integer id,
            @RequestParam(value="iu", required=true) String id_user,
            Model model
            ) {
        
        HashMap<String, String> jsonretorno = new HashMap<String, String>();
        //decodificar id de usuario
        Integer id_usuario = Integer.parseInt(Base64Coder.decodeString(id_user));
        
        Integer app_selected = 98;
        String command_selected = "delete";
        String extra_data_array = "'sin datos'";
        String data_string = app_selected+"___"+command_selected+"___"+id_usuario+"___"+id;
        
        System.out.println("Ejecutando borrado logico etiquetas");
        jsonretorno.put("success",String.valueOf( this.getInvDao().selectFunctionForThisApp(data_string,extra_data_array)) );
        
        return jsonretorno;
    }
    
    
    @RequestMapping(value = "/get_genera_pdf_etiquetas/{id_etiqueta}/{tipo_origen}/{iu}/out.json", method = RequestMethod.GET ) 
    public ModelAndView get_genera_pdf_etiquetas_entradasJson(
                @PathVariable("id_etiqueta") Integer etiqueta_id,
                @PathVariable("tipo_origen") Integer tipo_origen,
                @PathVariable("iu") String id_user,
                HttpServletRequest request, 
                HttpServletResponse response, 
                Model model)
            throws ServletException, IOException, URISyntaxException, DocumentException {
        
        HashMap<String, String> userDat = new HashMap<String, String>();  
        HashMap<String, String> datos_empresa = new HashMap<String, String>();
        
        
        ArrayList<HashMap<String, String>> lista_etiquetas = new ArrayList<HashMap<String, String>>();
        
        if (tipo_origen == 1)  {
               System.out.println("Generando PDF  de ETIQUETAS DE ENTRADAS"); 
        }
        if (tipo_origen == 2)  {
               System.out.println("Generando PDF  de ETIQUETAS DE PRODUCCION"); 
        }
        if (tipo_origen == 3)  {
               System.out.println("Generando PDF  de ETIQUETAS DE REQUISICIONES"); 
        }
        
        //decodificar id de usuario
        Integer id_usuario = Integer.parseInt(Base64Coder.decodeString(id_user));
        
        userDat = this.getHomeDao().getUserById(id_usuario);
        Integer id_empresa = Integer.parseInt(userDat.get("empresa_id"));
        
        
        String razon_social_empresa = this.getGralDao().getRazonSocialEmpresaEmisora(id_empresa);
        
        //obtener el directorio temporal
        String dir_tmp = this.getGralDao().getTmpDir();
        
        
        String[] array_company = razon_social_empresa.split(" ");
        String company_name= array_company[0].toLowerCase();
        //String ruta_imagen = this.getGralDao().getImagesDir() +"logo_"+ company_name +".png";
        
        String ruta_imagen = this.getGralDao().getImagesDir()+this.getGralDao().getRfcEmpresaEmisora(id_empresa)+"_logo.png";
        
        File file_dir_tmp = new File(dir_tmp);
        System.out.println("Directorio temporal: "+file_dir_tmp.getCanonicalPath());
        
        
        String file_name = "Etiqueta de  entradas_"+company_name+".pdf";
        //ruta de archivo de salida
        String fileout = file_dir_tmp +"/"+  file_name;
        Integer app_selected = 98; //aplicativo Impresion de etiquetas
        
        datos_empresa.put("emp_razon_social", razon_social_empresa);
        datos_empresa.put("emp_rfc", this.getGralDao().getRfcEmpresaEmisora(id_empresa));
        datos_empresa.put("emp_calle", this.getGralDao().getCalleDomicilioFiscalEmpresaEmisora(id_empresa));
        datos_empresa.put("emp_no_exterior", this.getGralDao().getNoExteriorDomicilioFiscalEmpresaEmisora(id_empresa));
        datos_empresa.put("emp_colonia", this.getGralDao().getColoniaDomicilioFiscalEmpresaEmisora(id_empresa));
        datos_empresa.put("emp_pais", this.getGralDao().getPaisDomicilioFiscalEmpresaEmisora(id_empresa));
        datos_empresa.put("emp_estado", this.getGralDao().getEstadoDomicilioFiscalEmpresaEmisora(id_empresa));
        datos_empresa.put("emp_municipio", this.getGralDao().getMunicipioDomicilioFiscalEmpresaEmisora(id_empresa));
        datos_empresa.put("emp_cp", this.getGralDao().getCpDomicilioFiscalEmpresaEmisora(id_empresa));
        
        datos_empresa.put("codigo1", this.getGralDao().getCodigo1Iso(id_empresa, app_selected));
        datos_empresa.put("codigo2", this.getGralDao().getCodigo2Iso(id_empresa, app_selected));
        
        //obtiene el listado de etiquetas depedieno del tipo de de origen (1 : entradas  2: produccion   3: requisicion)
        lista_etiquetas = this.getInvDao().getEtiquetas_Entrada(etiqueta_id,tipo_origen,id_empresa);
       
        String ruta_imagencodbarr="";
        //instancia a la clase que construye..... PDFS DE ETIQUETAS....
        PdfEtiqueta_Compras Etiqueta_Compras = new PdfEtiqueta_Compras(fileout,datos_empresa,lista_etiquetas,ruta_imagen,ruta_imagencodbarr,tipo_origen);               
        
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
    
    /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    
    @RequestMapping(value = "/get_imprime_Etiquetas/{id_etiqueta}/{tipo_origen}/{iu}/out.json", method = RequestMethod.GET ) 
    public ModelAndView get_imprime_EtiquetasJson(
                                                    @PathVariable("id_etiqueta") Integer etiqueta_id,
                                                    @PathVariable("tipo_origen") Integer tipo_origen,
                                                    @PathVariable("iu") String id_user,
                                                    HttpServletRequest request, 
                                                    HttpServletResponse response, 
                                                    Model model
                                                 )
    throws ServletException, IOException, URISyntaxException, DocumentException, TransformerException {
        LinkedHashMap<String, Object> datos_etiqueta= new LinkedHashMap<String, Object>();
        
        ////////###############    obteniendo la ip del cliente   ########/////////////////
        String ip_cliente = request.getRemoteAddr();
        System.out.println("ip_cliente1: "+ip_cliente);
        ip_cliente = ip_cliente.replace(".", "");
        ////////////////////////////////////////////////////////////////////////////////
        datos_etiqueta.put("cuerpo_titulo", "Etiqueta_Requisicion");              //  para compras ,           para produccion
        
        
        datos_etiqueta.put("producto_codigo", "PKA126");                         //  para compras ,           para produccion
        datos_etiqueta.put("producto_nombre", "sustancia x");                    //  para compras ,           para produccion
        datos_etiqueta.put("lote_interno", "543322");
        datos_etiqueta.put("lote_proveedor", "23456778");
        datos_etiqueta.put("lote_produccion", "23456778");                       //para produccion,
        datos_etiqueta.put("producto_unidad", "kgg");                            //para produccion,
        datos_etiqueta.put("producto_cantidad", "100001");                       //para produccion,
        
        
        
        datos_etiqueta.put("caducidad_fecha", "2012/02/1212");                   //  para compras ,           para produccion
        
        datos_etiqueta.put("nombre_cliente", "matiales electricos sa de cv.");   //  para compras ,           para produccion
        datos_etiqueta.put("etiqueta_largo", "54");                              //  para compras ,           para produccion
        datos_etiqueta.put("etiqueta_alto", "34");                               //  para compras ,           para produccion
        datos_etiqueta.put("modelo_impresora", "123xxxxx");
        
        //instanciando  la clase que genera el xml de eqtiquetas compras
        //EtiquetaCompras file_xml = new EtiquetaCompras(datos_etiqueta);
        
        //instanciando  la clase que genera el xml de eqtiquetas compras
        //EtiquetaProduccion file_xml = new EtiquetaProduccion(datos_etiqueta);
        
        //instanciando  la clase que genera el xml de eqtiquetas compras
        EtiquetaRequisicion file_xml = new EtiquetaRequisicion(datos_etiqueta);
        
        generandoxml xml =new generandoxml(file_xml.getDoc());
        String  xmlString=xml.createxml(file_xml.getDoc());
        
        //obtener el directorio temporal
        String dir_tmp = this.getGralDao().getZebraDir();
        
        File file_dir_tmp = new File(dir_tmp);
        System.out.println("Directorio temporal: "+file_dir_tmp.getCanonicalPath());
        
        String file_name = "EtiquetaRequisicion.xml";
        //ruta de archivo de salida
        String ruta = file_dir_tmp +"/in";
        String nombre_archivo = file_name;
        //File file = new File(fileout);
       
        System.out.println("IMPRIMIENDO la Ruta:\n\n" + ruta);
        System.out.println("IMPRIMIENDO el nombre del Archivo\n\n" + nombre_archivo);
        System.out.println("IMPRIMIENDO Ruta completa\n\n" + ruta +"/"+nombre_archivo);
        System.out.println("IMPRIMIENDO EL  XML:\n\n" + xmlString);
        //FileHelper.createFileWithText("/home/mi_compu/NetBeansProjects/Ejemploxml", "xmletiquetaproduccion_b", xmlString);
        FileHelper.createFileWithText(ruta,nombre_archivo, xmlString);
       
        return null;
        
    }  
    
}
