/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
*/
package com.agnux.kemikal.controllers;

import com.agnux.cfd.v2.Base64Coder;
import com.agnux.common.helpers.FileHelper;
import com.agnux.common.helpers.StringHelper;
import com.agnux.common.helpers.TimeHelper;
import com.agnux.common.obj.DataPost;
import com.agnux.common.obj.ResourceProject;
import com.agnux.common.obj.UserSessionData;
import com.agnux.kemikal.interfacedaos.GralInterfaceDao;
import com.agnux.kemikal.interfacedaos.HomeInterfaceDao;
import com.agnux.kemikal.interfacedaos.ProInterfaceDao;
import com.agnux.kemikal.reportes.PdfProPreOrdenProduccion;
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
import org.apache.commons.lang.StringUtils;
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
 *
 * @author agnux
 * En esta clase se utiliza una macro "macrocoma", para sistituir las comas en la lista de procedidmientos 
 * 
 */

@Controller
@SessionAttributes({"user"})
@RequestMapping("/propreordenproduccion/")
public class ProPreOrdenProduccionController {
    private static final Logger log  = Logger.getLogger(ProPreOrdenProduccionController.class.getName());
    ResourceProject resource = new ResourceProject();
    
    
    @Autowired
    @Qualifier("daoPro")
    private ProInterfaceDao daoPro;

    
    public ProInterfaceDao getProDao() {
        return daoPro;
    }
    
    @Autowired
    @Qualifier("daoHome")
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
            @ModelAttribute("user") UserSessionData user)
            throws ServletException, IOException {
        
        log.log(Level.INFO, "Ejecutando starUp de {0}", ProPreOrdenProduccionController.class.getName());
        LinkedHashMap<String,String> infoConstruccionTabla = new LinkedHashMap<String,String>();
        
        //infoConstruccionTabla.put("id", "Acciones:90");
        infoConstruccionTabla.put("id", "Acciones:70");
        infoConstruccionTabla.put("folio", "Folio:100");
        infoConstruccionTabla.put("accesor_tipo", "Tipo de preorden:150");
        infoConstruccionTabla.put("momento_creacion", "Fecha:150");
        infoConstruccionTabla.put("confirmado", "Estatus:130");
        
        ModelAndView x = new ModelAndView("propreordenproduccion/startup", "title", "Preorden de produccion");
        
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
    
    
    @RequestMapping(value="/get_all_preordenesproduccion.json", method = RequestMethod.POST)
    public @ResponseBody HashMap<String,ArrayList<HashMap<String, Object>>> getAllPreordenesProduccionJson(
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
        
        //aplicativo preorden de produccion
        Integer app_selected = 89;
        
        //decodificar id de usuario
        Integer id_usuario = Integer.parseInt(Base64Coder.decodeString(id_user_cod));
        
        //variables para el buscador
        String folio_preorden = "%"+StringHelper.isNullString(String.valueOf(has_busqueda.get("folio_preorden")))+"%";
        String tipo_orden = has_busqueda.get("tipo_orden").equals(null) ? "0" : has_busqueda.get("tipo_orden");
        
        String data_string = app_selected+"___"+id_usuario+"___"+folio_preorden+"___"+tipo_orden;
        
        //obtiene total de registros en base de datos, con los parametros de busqueda
        int total_items = this.getProDao().countAll(data_string);
        
        //calcula el total de paginas
        int total_pags = resource.calculaTotalPag(total_items,items_por_pag);
        
        //variables que necesita el datagrid, para no tener que hacer uno por cada aplicativo
        DataPost dataforpos = new DataPost(orderby, desc, items_por_pag, pag_start, display_pag, input_json, cadena_busqueda,total_items,total_pags,id_user_cod);
        
        int offset = resource.__get_inicio_offset(items_por_pag, pag_start);
        
        //obtiene los registros para el grid, de acuerdo a los parametros de busqueda
        jsonretorno.put("Data", this.getProDao().getProPreorden_PaginaGrid(data_string, offset, items_por_pag, orderby, desc));
        
        //obtiene el hash para los datos que necesita el datagrid
        jsonretorno.put("DataForGrid", dataforpos.formaHashForPos(dataforpos));
        
        return jsonretorno;
    }
    
    //obtiene datos para el autopletar
    @RequestMapping(value="/get_proordentipos.json", method = RequestMethod.POST)
    public @ResponseBody HashMap<String,ArrayList<HashMap<String, String>>> getProOrdenTiposJson(
           @RequestParam(value="iu", required=true) String id_user_cod,
           Model modcel) {
        
        HashMap<String, String> userDat = new HashMap<String, String>();
        HashMap<String,ArrayList<HashMap<String, String>>> jsonretorno = new HashMap<String,ArrayList<HashMap<String, String>>>();
        ArrayList<HashMap<String, String>> tc = new ArrayList<HashMap<String, String>>();
        
        Integer id_usuario = Integer.parseInt(Base64Coder.decodeString(id_user_cod));
        userDat = this.getHomeDao().getUserById(id_usuario);
        
        Integer id_empresa = Integer.parseInt(userDat.get("empresa_id"));
        Integer id_sucursal = Integer.parseInt(userDat.get("sucursal_id"));
        
        jsonretorno.put("ordenTipos", this.getProDao().getProOrdenTipos(id_empresa));
        
        return jsonretorno;
    }
    
    
    @RequestMapping(method = RequestMethod.POST, value="/get_datos_preorden.json")
    public @ResponseBody HashMap<String,ArrayList<HashMap<String, String>>> getPreordenJson(
            @RequestParam(value="id_preorden", required=true) Integer id,
            @RequestParam(value="iu", required=true) String id_user,
            Model model
            ) {
        
        log.log(Level.INFO, "Ejecutando get_datos_preorden de {0}", ProPreOrdenProduccionController.class.getName());
        HashMap<String,ArrayList<HashMap<String, String>>> jsonretorno = new HashMap<String,ArrayList<HashMap<String, String>>>();
        
        
        ArrayList<HashMap<String, String>> datosPreorden = new ArrayList<HashMap<String, String>>();
        ArrayList<HashMap<String, String>> datosPreordenDet = new ArrayList<HashMap<String, String>>();
        
        HashMap<String, String> userDat = new HashMap<String, String>();
        
        //decodificar id de usuario
        Integer id_usuario = Integer.parseInt(Base64Coder.decodeString(id_user));
        userDat = this.getHomeDao().getUserById(id_usuario);
        
        Integer id_empresa = Integer.parseInt(userDat.get("empresa_id"));
        Integer id_sucursal = Integer.parseInt(userDat.get("sucursal_id"));
        
        if( id != 0 ){
            datosPreorden = this.getProDao().getProPreorden_Datos(id);
            datosPreordenDet = this.getProDao().getProPreorden_Detalle(id);
        }
        
        jsonretorno.put("Preorden", datosPreorden);
        jsonretorno.put("PreordenDet", datosPreordenDet);
        jsonretorno.put("ordenTipos", this.getProDao().getProOrdenTipos(id_empresa));
        
        return jsonretorno;
    }
    
            
    //obtiene los productos para el buscador
    @RequestMapping(method = RequestMethod.POST, value="/get_buscador_pedidos.json")
    public @ResponseBody HashMap<String,ArrayList<HashMap<String, String>>> getBuscadorPedidosJson(
            @RequestParam(value="folio", required=true) String folio,
            @RequestParam(value="proveedor", required=true) String proveedor,
            @RequestParam(value="iu", required=true) String id_user,
            Model model
            ) {
        
        log.log(Level.INFO, "Ejecutando getProductosJson de {0}", ProPreOrdenProduccionController.class.getName());
        HashMap<String,ArrayList<HashMap<String, String>>> jsonretorno = new HashMap<String,ArrayList<HashMap<String, String>>>();
        ArrayList<HashMap<String, String>> pedidos = new ArrayList<HashMap<String, String>>();
        HashMap<String, String> userDat = new HashMap<String, String>();
        //decodificar id de usuario
        Integer id_usuario = Integer.parseInt(Base64Coder.decodeString(id_user));
        userDat = this.getHomeDao().getUserById(id_usuario);
        
        Integer id_empresa = Integer.parseInt(userDat.get("empresa_id"));
        
        folio = "%"+StringHelper.isNullString(String.valueOf(folio))+"%";
        
        proveedor = "%"+StringHelper.isNullString(String.valueOf(proveedor))+"%";
        
        pedidos = this.getProDao().getBuscadorPedidos(folio, proveedor,id_empresa);
        
        jsonretorno.put("pedidos", pedidos);
        
        return jsonretorno;
    }
    
    
    //obtiene los productos para el buscador
    @RequestMapping(method = RequestMethod.POST, value="/get_productos_pedido.json")
    public @ResponseBody HashMap<String,ArrayList<HashMap<String, String>>> getProductosPedidoJson(
            @RequestParam(value="id_pedido", required=true) String id_pedido,
            @RequestParam(value="iu", required=true) String id_user,
            Model model
            ) {
        
        log.log(Level.INFO, "Ejecutando getProductosJson de {0}", ProPreOrdenProduccionController.class.getName());
        HashMap<String,ArrayList<HashMap<String, String>>> jsonretorno = new HashMap<String,ArrayList<HashMap<String, String>>>();
        ArrayList<HashMap<String, String>> productos = new ArrayList<HashMap<String, String>>();
        HashMap<String, String> userDat = new HashMap<String, String>();
        //decodificar id de usuario
        Integer id_usuario = Integer.parseInt(Base64Coder.decodeString(id_user));
        userDat = this.getHomeDao().getUserById(id_usuario);
        
        Integer id_empresa = Integer.parseInt(userDat.get("empresa_id"));
        
        productos = this.getProDao().getProductosPedidoSeleccionado(id_pedido, id_usuario);
        
        jsonretorno.put("productos", productos);
        
        return jsonretorno;
    }
    
    
    //crear, editar y cancelar preorden
    @RequestMapping(method = RequestMethod.POST, value="/edit.json")
    public @ResponseBody HashMap<String, String> editFormulasJson(
        @RequestParam(value="id_preorden", required=true) Integer id,
        @RequestParam(value="tipoorden", required=true) String tipoorden,
        @RequestParam(value="command_selected", required=true) String command_selected,
        @RequestParam(value="observaciones", required=true) String observaciones,
        
        @RequestParam(value="eliminar", required=false) String[] eliminado,
        @RequestParam(value="id_reg", required=false) String[] id_reg,
        @RequestParam(value="presentacion_id", required=false) String[] presentacion_id,
        @RequestParam(value="inv_prod_id", required=false) String[] inv_prod_id,
        @RequestParam(value="id_pedido", required=false) String[] id_pedido,
        @RequestParam(value="cantidad", required=false) String[] cantidad,
        @RequestParam(value="umedida", required=false) String[] umedida,
        @RequestParam(value="umedida_id", required=false) String[] umedida_id,
        
        Model model,@ModelAttribute("user") UserSessionData user
    ) {
        HashMap<String, String> jsonretorno = new HashMap<String, String>();
        HashMap<String, String> succes = new HashMap<String, String>();
        Integer app_selected = 89;//catalogo de preorden produccion
        
        Integer id_usuario= user.getUserId();//variable para el id  del usuario
        //String extra_data_array = "'sin datos'";
        String actualizo = "0";
        int no_partida = 0;
        String extra_data_array = "'sin datos'";
        
        String arreglo[];
        
        if(eliminado!=null){
            arreglo = new String[eliminado.length];
            
            for(int i=0; i<eliminado.length; i++) {
                if(Integer.parseInt(eliminado[i]) != 0){
                    no_partida++;//si no esta eliminado incrementa el contador de partidas
                }
                
                arreglo[i]= "'"+id_reg[i] +"___" + presentacion_id[i]+"___" + inv_prod_id[i]+"___" + id_pedido[i]+"___" + no_partida+"___" + cantidad[i]+"___" + eliminado[i]+"___" + umedida[i]+"___" + umedida_id[i]+"'";
                //System.out.println(arreglo[i]);
            }
            
            //serializar el arreglo
            extra_data_array = StringUtils.join(arreglo, ",");
        }
        /*
        if( id ==0 ){
            command_selected = "new";
        }else{
            command_selected = "edit";
        }
        */
        
        String data_string = app_selected+"___"+command_selected+"___"+id_usuario+"___"+id+"___"+tipoorden+"___"+observaciones.toUpperCase();
        
        succes = this.getProDao().selectFunctionValidateAaplicativo(data_string,app_selected,extra_data_array);
        
        log.log(Level.INFO, "despues de validacion {0}", String.valueOf(succes.get("success")));
        if( String.valueOf(succes.get("success")).equals("true") ){
            actualizo = this.getProDao().selectFunctionForApp_Produccion(data_string, extra_data_array);
            jsonretorno.put("success",String.valueOf(actualizo));
        }else{
            jsonretorno.put("success",String.valueOf(succes.get("success")));
        }
        
        
        
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
        
        Integer app_selected = 69;
        String command_selected = "delete";
        String extra_data_array = "'sin datos'";
        String data_string = app_selected+"___"+command_selected+"___"+id_usuario+"___"+id;
        
        System.out.println("Ejecutando borrado logico formulas");
        jsonretorno.put("success",String.valueOf( this.getProDao().selectFunctionForApp_Produccion(data_string,extra_data_array)) );
        
        return jsonretorno;
    }
    
    
    //localhost:8080/com.mycompany_Kemikal_war_1.0-SNAPSHOT/controllers/logasignarutas/getPdfProduccion/1/NQ==/out.json
    //Genera pdf de formulacion de
    @RequestMapping(value = "/getPdfPreorden/{id}/{iu}/out.json", method = RequestMethod.GET ) 
    public ModelAndView getGeneraPdfTerminadoJson(
                @PathVariable("id") String id_preorden,
                @PathVariable("iu") String id_user,
                HttpServletRequest request, 
                HttpServletResponse response, 
                Model model)
            throws ServletException, IOException, URISyntaxException, DocumentException {
        
        HashMap<String, String> userDat = new HashMap<String, String>();
        HashMap<String, String> datos = new HashMap<String, String>();
        ArrayList<HashMap<String, Object>> productos= new ArrayList<HashMap<String, Object>>();
        
        ArrayList<HashMap<String, String>> datos_orden = new ArrayList<HashMap<String, String>>();
        HashMap<String, String> datosEncabezadoPie= new HashMap<String, String>();
        ArrayList<HashMap<String, Object>> lista_productos = new ArrayList<HashMap<String, Object>>();
        ArrayList<HashMap<String, String>> lista_procedimiento = new ArrayList<HashMap<String, String>>();
        
        //decodificar id de usuario
        Integer id_usuario = Integer.parseInt(Base64Coder.decodeString(id_user));
        Integer app_selected = 89;//catalogo de preorden produccion
        
        userDat = this.getHomeDao().getUserById(id_usuario);
        Integer id_empresa = Integer.parseInt(userDat.get("empresa_id"));
        String rfc_empresa=this.getGralDao().getRfcEmpresaEmisora(id_empresa);
        String razon_social_empresa = this.getGralDao().getRazonSocialEmpresaEmisora(id_empresa);
        
        datosEncabezadoPie.put("nombre_empresa_emisora", razon_social_empresa);
        datosEncabezadoPie.put("titulo_reporte", this.getGralDao().getTituloReporte(id_empresa, app_selected));
        datosEncabezadoPie.put("codigo1", this.getGralDao().getCodigo1Iso(id_empresa, app_selected));
        datosEncabezadoPie.put("codigo2", this.getGralDao().getCodigo2Iso(id_empresa, app_selected));
        
        //obtener el directorio temporal
        String dir_tmp = this.getGralDao().getTmpDir();
        
        File file_dir_tmp = new File(dir_tmp);
        System.out.println("Directorio temporal: "+file_dir_tmp.getCanonicalPath());
        
        //obtiene las facturas del periodo indicado
        productos = this.getProDao().getPro_DatosPreOrdenProduccionPdf(id_preorden);
        
        datos_orden = this.getProDao().getProOrden_Datos(Integer.parseInt(id_preorden));
        datos.put("fecha", TimeHelper.getFechaActualYMDH());
        
        datos.put("folio", datos_orden.get(0).get("folio"));
        datos.put("fecha_elavorar", datos_orden.get(0).get("fecha_elavorar"));
        datos.put("flujo", datos_orden.get(0).get("flujo"));
        datos.put("observaciones", datos_orden.get(0).get("observaciones"));
        
        String file_name = "PRODUCCION_"+rfc_empresa+"_"+datos.get("folio") +".pdf";
        //ruta de archivo de salida
        String fileout = file_dir_tmp +"/"+  file_name;
        
        //instancia a la clase que construye el pdf del reporte de facturas
        PdfProPreOrdenProduccion pdf = new PdfProPreOrdenProduccion(datosEncabezadoPie, fileout,productos,datos);
        
        System.out.println("Recuperando archivo: " + fileout);
        File file = new File(fileout);
        if (file.exists()){
            int size = (int) file.length(); // Tamaño del archivo
            BufferedInputStream bis = new BufferedInputStream(new FileInputStream(file));
            response.setBufferSize(size);
            response.setContentLength(size);
            response.setContentType("text/plain");
            response.setHeader("Content-Disposition","attachment; filename=\"" + file.getCanonicalPath() +"\"");
            FileCopyUtils.copy(bis, response.getOutputStream());
            response.flushBuffer();
        }
        
        return null;
    }
    
    
}