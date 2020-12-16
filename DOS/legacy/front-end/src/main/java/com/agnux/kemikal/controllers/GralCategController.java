package com.agnux.kemikal.controllers;

import com.agnux.kemikal.springdaos.GralSpringDao;
import com.agnux.cfd.v2.Base64Coder;
import com.agnux.common.helpers.StringHelper;
import com.agnux.common.obj.DataPost;
import com.agnux.common.obj.ResourceProject;
import com.agnux.common.obj.UserSessionData;
import com.agnux.kemikal.interfacedaos.GralInterfaceDao;
import com.agnux.kemikal.interfacedaos.HomeInterfaceDao;
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
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.servlet.ModelAndView;


@Controller
@SessionAttributes({"user"})
@RequestMapping("/gralcateg/")
public class GralCategController {
    ResourceProject resource = new ResourceProject();
    private static final Logger log = Logger.getLogger(GralCategController.class.getName());
    
    @Autowired
    @Qualifier("daoGral")
    private GralInterfaceDao gralDao;
    
    @Autowired
    @Qualifier("daoHome") //permite controlar usuarios que entren
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
    log.log(Level.INFO, "Ejecutando starUp de {0}", GralCategController.class.getName());

    LinkedHashMap<String,String> infoConstruccionTabla = new LinkedHashMap<String,String>();
    infoConstruccionTabla.put("id", "Acciones:70");
    infoConstruccionTabla.put("categoria", "Categoría:120");
    infoConstruccionTabla.put("sueldo_por_hora", "Sueldo por Hora:150");
    infoConstruccionTabla.put("sueldo_por_horas_ext", "Sueldo por Hora Extra:150");
    infoConstruccionTabla.put("nombre_puesto", "Puesto:250");


    ModelAndView x = new ModelAndView("gralcateg/startup", "title", "Cat&aacute;logo de Categor&iacute;as");
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

    @RequestMapping(method = RequestMethod.POST, value="/getCateg.json")
    public @ResponseBody HashMap<String,ArrayList<HashMap<String, String>>> getCategJson(
    @RequestParam(value="id", required=true) Integer id,
    @RequestParam(value="iu", required=true) String id_user_cod,
    Model model
    ){
        log.log(Level.INFO, "Ejecutando getCategJson de {0}", GralCategController.class.getName());


        HashMap<String,ArrayList<HashMap<String, String>>> jsonretorno = new HashMap<String,ArrayList<HashMap<String, String>>>();
        HashMap<String, String> userDat = new HashMap<String, String>();
        ArrayList<HashMap<String, String>> datosCateg = new ArrayList<HashMap<String, String>>();
        ArrayList<HashMap<String, String>> puestos = new ArrayList<HashMap<String, String>>();
        //decodificar id de usuario
        Integer id_usuario = Integer.parseInt(Base64Coder.decodeString(id_user_cod));
        userDat = this.getHomeDao().getUserById(id_usuario);
        Integer id_empresa = Integer.parseInt(userDat.get("empresa_id"));
        
        if( id != 0 ){
            datosCateg = this.getGralDao().getCateg_Datos(id);
        }
        
        puestos = this.getGralDao().getPuestos(id_empresa);
        //datos categs tos es lo que me trajo de la consulta y los pone en el json
        jsonretorno.put("Categ", datosCateg);
        jsonretorno.put("Puestos", puestos);
        return jsonretorno;
    }
    
    
    @RequestMapping(value="/getAllCategs.json", method = RequestMethod.POST)
        public @ResponseBody HashMap<String,ArrayList<HashMap<String, Object>>> getAllCategsJson(
        @RequestParam(value="orderby", required=true) String orderby,
        @RequestParam(value="desc", required=true) String desc,
        @RequestParam(value="items_por_pag", required=true) int items_por_pag,
        @RequestParam(value="pag_start", required=true) int pag_start,
        @RequestParam(value="display_pag", required=true) String display_pag,
        @RequestParam(value="input_json", required=true) String input_json,
        @RequestParam(value="cadena_busqueda", required=true) String cadena_busqueda,
        @RequestParam(value="iu", required=true) String id_user_cod,
        Model modcel
    ) {
        
        HashMap<String,ArrayList<HashMap<String, Object>>> jsonretorno = new HashMap<String,ArrayList<HashMap<String, Object>>>();
        HashMap<String,String> has_busqueda = StringHelper.convert2hash(StringHelper.ascii2string(cadena_busqueda));
        
        //aplicativo catalogo de categorías
        Integer app_selected = 85;
        
        //decodificar id de usuario
        Integer id_usuario = Integer.parseInt(Base64Coder.decodeString(id_user_cod));
        
        //variables para el buscador
        String categoria = StringHelper.isNullString(String.valueOf(has_busqueda.get("categ")));
        String sueldo_por_hora = StringHelper.isNullString(String.valueOf(has_busqueda.get("sueldo_hora")));
        String sueldo_por_horas_ext = StringHelper.isNullString(String.valueOf(has_busqueda.get("sueldo_hora_extra")));
        String puesto =StringHelper.isNullString(String.valueOf(has_busqueda.get("puesto")));
        
        //                         1                 2                3                 4                       5                   6
        String data_string = app_selected+"___"+id_usuario+"___"+categoria+"___"+sueldo_por_hora+"___"+sueldo_por_horas_ext+"___"+puesto;
        //String data_string = categoria+"___"+sueldo_por_hora+"___"+sueldo_por_horas_ext+"___"+puesto;
        System.out.println("Esto trae el data_string"+data_string);
        //obtiene total de registros en base de datos, con los parametros de busqueda
        int total_items = this.getGralDao().countAll(data_string);
        
        //calcula el total de paginas
        int total_pags = resource.calculaTotalPag(total_items,items_por_pag);
        
        //variables que necesita el datagrid, para no tener que hacer uno por cada aplicativo
        DataPost dataforpos = new DataPost(orderby, desc, items_por_pag, pag_start, display_pag, input_json, cadena_busqueda,total_items,total_pags, id_user_cod);
        int offset = resource.__get_inicio_offset(items_por_pag, pag_start);
        
        //obtiene los registros para el grid, de acuerdo a los parametros de busqueda
        jsonretorno.put("Data", this.getGralDao().getCateg_PaginaGrid(data_string, offset, items_por_pag, orderby, desc));
        
        //obtiene el hash para los datos que necesita el datagrid
        jsonretorno.put("DataForGrid", dataforpos.formaHashForPos(dataforpos));
        return jsonretorno;
    }
    
    
    //crear y editar una categoria
    @RequestMapping(method = RequestMethod.POST, value="/edit.json")
    public @ResponseBody HashMap<String, String> editJson(
    @RequestParam(value="identificador", required=true) Integer id,
    @RequestParam(value="categoria", required=true) String categ,
    @RequestParam(value="sueldo_hora", required=true) Double sueldoxhora,
    @RequestParam(value="sueldo_hora_extra", required=true) Double sueldoxhoraext,
    @RequestParam(value="select_puesto", required=true) String puesto,
    @ModelAttribute("user") UserSessionData user,
    Model model

    ) {
        HashMap<String, String> jsonretorno = new HashMap<String, String>();
        HashMap<String, String> succes = new HashMap<String, String>();
        Integer app_selected = 85;//catalogo de categorias
        String command_selected = "new";
        
        //decodificar id de usuario
        Integer id_usuario = user.getUserId();
        String extra_data_array = "'sin datos'";
        String actualizo = "0";
        if( id==0 ){
        command_selected = "new";
        }else{
            command_selected = "edit";
        }
        //                         1                   2                    3            4              5                       6                   7                     8
        String data_string = app_selected+"___"+command_selected+"___"+id_usuario+"___"+id+"___"+categ.toUpperCase()+"___"+sueldoxhora+"___"+sueldoxhoraext+"___"+puesto.toUpperCase();
        
        succes = this.getGralDao().selectFunctionValidateAaplicativo(data_string, app_selected, extra_data_array);
        log.log(Level.INFO, "despues de validacion {0}", String.valueOf(succes.get("success")));
        if( String.valueOf(succes.get("success")).equals("true") ){
            actualizo = this.getGralDao().selectFunctionForThisApp(data_string, extra_data_array);
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
        Integer app_selected = 85;
        String command_selected = "delete";
        String extra_data_array = "'sin datos'";
        String data_string = app_selected+"___"+command_selected+"___"+id_usuario+"___"+id;
        System.out.println("Ejecutando borrado logico categorias");
        System.out.println("cadena a enviar para el cambio de borrado logico: "+data_string);
        jsonretorno.put("success",String.valueOf( this.getGralDao().selectFunctionForThisApp(data_string, extra_data_array)));
        return jsonretorno;
    }

 
    //obtiene los grupos para el buscador    
    @RequestMapping(method = RequestMethod.POST, value="/getPuestos.json")
    public @ResponseBody HashMap<String,ArrayList<HashMap<String, String>>> getPuestosJson(
            @RequestParam(value="iu", required=true) String id_user_cod,
            Model model
            ) {        
            HashMap<String,ArrayList<HashMap<String, String>>> jsonretorno = new HashMap<String,ArrayList<HashMap<String, String>>>();
            ArrayList<HashMap<String, String>> puestos = new ArrayList<HashMap<String, String>>();
            HashMap<String, String> userDat = new HashMap<String, String>();
            Integer id_usuario = Integer.parseInt(Base64Coder.decodeString(id_user_cod));
            userDat = this.getHomeDao().getUserById(id_usuario);
            Integer id_empresa = Integer.parseInt(userDat.get("empresa_id"));
            puestos = this.getGralDao().getPuestos(id_empresa);
            jsonretorno.put("Puestos", puestos);       
            return jsonretorno;         
    }


    
    
    
    
    
    
    @RequestMapping(method = RequestMethod.POST, value="/getCateg_Datos.json")
    public @ResponseBody HashMap<String,ArrayList<HashMap<String, String>>> getCateg_DatosJson(
        @RequestParam(value="id", required=true) Integer id,
        @RequestParam(value="iu", required=true) String id_user_cod,
        Model model ) {
            log.log(Level.INFO, "Ejecutando getCateg_Datos de {0}", GralCategController.class.getName());
            HashMap<String,ArrayList<HashMap<String, String>>> jsonretorno = new HashMap<String,ArrayList<HashMap<String, String>>>();
            ArrayList<HashMap<String, String>> categ = new ArrayList<HashMap<String, String>>();
            ArrayList<HashMap<String, String>> puestos = new ArrayList<HashMap<String, String>>();
            HashMap<String, String> userDat = new HashMap<String, String>();
            Integer id_usuario = Integer.parseInt(Base64Coder.decodeString(id_user_cod));
            userDat = this.getHomeDao().getUserById(id_usuario);
            Integer id_empresa = Integer.parseInt(userDat.get("empresa_id"));
            
            if( id != 0  ){
                categ = this.getGralDao().getCateg_Datos(id);
            }
            puestos = this.getGralDao().getPuestos(id_empresa);
            jsonretorno.put("Puestos", puestos);
            jsonretorno.put("Categ", categ);

            return jsonretorno;
    }

}
