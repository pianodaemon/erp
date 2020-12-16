package com.agnux.kemikal.controllers;

import com.agnux.cfd.v2.Base64Coder;
import com.agnux.common.helpers.StringHelper;
import com.agnux.common.obj.DataPost;
import com.agnux.common.obj.ResourceProject;
import com.agnux.common.obj.UserSessionData;
import com.agnux.kemikal.interfacedaos.GralInterfaceDao;
import com.agnux.kemikal.interfacedaos.HomeInterfaceDao;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
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
@RequestMapping("/gralfichatecnica/")
public class GralFichaTecnicaController {
    ResourceProject resource = new ResourceProject();
    private static final Logger log  = Logger.getLogger(GralFichaTecnicaController.class.getName());
    
    @Autowired
    @Qualifier("daoGral")
    private GralInterfaceDao gralDao;
    
    @Autowired
    @Qualifier("daoHome")   //permite controlar usuarios que entren
    private HomeInterfaceDao HomeDao;
    
    
    public GralInterfaceDao getGralDao() {
        return gralDao;
    }
    
    public HomeInterfaceDao getHomeDao() {
        return HomeDao;
    }
    
    
    @RequestMapping(value="/startup.agnux")
    public ModelAndView startUp(HttpServletRequest request, HttpServletResponse response, 
            @ModelAttribute("user") UserSessionData user
        )throws ServletException, IOException {
        
        log.log(Level.INFO, "Ejecutando starUp de {0}", GralFichaTecnicaController.class.getName());
        LinkedHashMap<String,String> infoConstruccionTabla = new LinkedHashMap<String,String>();
        infoConstruccionTabla.put("id", "Acciones:70");
        infoConstruccionTabla.put("sku", "Titulo:100");
        infoConstruccionTabla.put("descripcion", "Titulo:200");
        infoConstruccionTabla.put("accesor_descarga", "Archivo:150");
        
        
        ModelAndView x = new ModelAndView("gralfichatecnica/startup", "title", "Descarga de ficha tecnica");
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
    
        @RequestMapping(value="/getAllProductosFichaTecnica.json", method = RequestMethod.POST)
     public @ResponseBody HashMap<String,ArrayList<HashMap<String, Object>>> getAllProductosFichaTecnicaJson(
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
        HashMap<String, String> userDat = new HashMap<String, String>();
        
        //aplicativo Descarga de Ficha Tecnica de Productos
        Integer app_selected = 122;
        
        //decodificar id de usuario
        Integer id_usuario = Integer.parseInt(Base64Coder.decodeString(id_user_cod));
        userDat = this.getHomeDao().getUserById(id_usuario);
        Integer id_empresa = Integer.parseInt(userDat.get("empresa_id"));
        //variables para el buscador
        String codigo = "%"+StringHelper.isNullString(String.valueOf(has_busqueda.get("busqueda_codigo")))+"%";
        String descripcion = "%"+StringHelper.isNullString(String.valueOf(has_busqueda.get("busqueda_descripcion")))+"%";
        
        String data_string = app_selected+"___"+id_usuario+"___"+codigo+"___"+descripcion;
        
        //obtiene total de registros en base de datos, con los parametros de busqueda
        int total_items = this.getGralDao().countAll(data_string);              
                
        
        //calcula el total de paginas
        int total_pags = resource.calculaTotalPag(total_items,items_por_pag);
        
        //variables que necesita el datagrid, para no tener que hacer uno por cada aplicativo
        DataPost dataforpos = new DataPost(orderby, desc, items_por_pag, pag_start, display_pag, input_json, cadena_busqueda,total_items,total_pags, id_user_cod);
        
        int offset = resource.__get_inicio_offset(items_por_pag, pag_start);
        
        //obtiene los registros para el grid, de acuerdo a los parametros de busqueda
        jsonretorno.put("Data", this.getGralDao().getFichaTecnica_PaginaGrid(data_string, offset, items_por_pag, orderby, desc,id_empresa));
        
        //obtiene el hash para los datos que necesita el datagrid
        jsonretorno.put("DataForGrid", dataforpos.formaHashForPos(dataforpos));
        
        return jsonretorno;
    }
        
        
    //Buscar nombre de archivo para ficha tecnica
    @RequestMapping(method = RequestMethod.POST, value="/getExisFichaTecnica.json")
    public @ResponseBody HashMap<String,Object> getMovTiposJson(
            @RequestParam(value="id", required=true) String id_prod,
            @RequestParam(value="iu", required=true) String id_user_cod,
            Model model
        ) {
        
        HashMap<String,Object> jsonretorno = new HashMap<String,Object>();
        HashMap<String, String> userDat = new HashMap<String, String>();
        
        //decodificar id de usuario
        Integer id_usuario = Integer.parseInt(Base64Coder.decodeString(id_user_cod));
        
        String file_name = this.getGralDao().getCodigoProductoById(id_prod);
        
        if(file_name.equals("")){
            jsonretorno.put("exis", false);
            jsonretorno.put("msj", "El producto no tiene relaci&oacute;n con algun archivo de ficha t&eacute;cnica.");
        }else{
            userDat = this.getHomeDao().getUserById(id_usuario);
            Integer id_empresa = Integer.parseInt(userDat.get("empresa_id"));
            String rfc_empresa=this.getGralDao().getRfcEmpresaEmisora(id_empresa);
            
            //ruta de archivo de salida
            String fileout = this.getGralDao().getProdPdfDir()+ rfc_empresa+"/"+  file_name;
            
            //System.out.println("Recuperando archivo: " + fileout);
            File file = new File(fileout);
            
            if (file.isFile()){
                jsonretorno.put("exis", true);
                jsonretorno.put("msj", "");
            }else{
                jsonretorno.put("exis", false);
                jsonretorno.put("msj", "El archivo de ficha t&eacute;cnica no existe.");
            }
        }
        
        return jsonretorno;
    }
    
        
        
        
    //Genera pdf de formulacion de
    @RequestMapping(value = "/getPdfFichaTecnica/{id}/{iu}/out.json", method = RequestMethod.GET ) 
    public ModelAndView getPdfFichaTecnicaJson(
                @PathVariable("id") String id_ficha,
                @PathVariable("iu") String id_user,
                HttpServletRequest request, 
                HttpServletResponse response, 
                Model model)
            throws ServletException, IOException {
        
        HashMap<String, String> userDat = new HashMap<String, String>();
        
        //decodificar id de usuario
        Integer id_usuario = Integer.parseInt(Base64Coder.decodeString(id_user));
        
        userDat = this.getHomeDao().getUserById(id_usuario);
        Integer id_empresa = Integer.parseInt(userDat.get("empresa_id"));
        String rfc_empresa=this.getGralDao().getRfcEmpresaEmisora(id_empresa);
        
        String file_name = this.getGralDao().getCodigoProductoById(id_ficha);
        
        //ruta de archivo de salida
        String fileout = this.getGralDao().getProdPdfDir()+ rfc_empresa+"/"+  file_name;
        
        //System.out.println("Recuperando archivo: " + fileout);
        File file = new File(fileout);
        
        if (file.isFile()){
            int size = (int) file.length(); // Tama√±o del archivo
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
    
}
