/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.agnux.kemikal.controllers;

import com.agnux.cfd.v2.Base64Coder;
import com.agnux.common.helpers.StringHelper;
import com.agnux.common.obj.DataPost;
import com.agnux.common.obj.ResourceProject;
import com.agnux.common.obj.UserSessionData;
import com.agnux.kemikal.interfacedaos.CxpInterfaceDao;
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
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttributes;

/**
 *
 * @author valentin santos
 * valentin.vale8490@gmail.com
 * 24/abril/2012
 */
@Controller
@SessionAttributes({"user"})
@RequestMapping("/proveedoresdirecciones/")
public class ProveedoresDireccionesController {
    ResourceProject resource = new ResourceProject();
    private static final Logger log  = Logger.getLogger(ProveedoresDireccionesController.class.getName());
    
    
    @Autowired
    @Qualifier("daoCxp")
    private CxpInterfaceDao cxpDao;
    
    public CxpInterfaceDao getCxpDao() {
        return cxpDao;
    }
    @Autowired
    @Qualifier("daoHome")
    private HomeInterfaceDao HomeDao;

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
    
    @RequestMapping(value="/startup.agnux")
    public ModelAndView startUp(HttpServletRequest request, HttpServletResponse response, 
            @ModelAttribute("user") UserSessionData user
            )throws ServletException, IOException {
        
            log.log(Level.INFO, "Ejecutando starUp de {0}", ProveedoresDireccionesController.class.getName());
        LinkedHashMap<String,String> infoConstruccionTabla = new LinkedHashMap<String,String>();
        
        infoConstruccionTabla.put("id", "Acciones:90");
        infoConstruccionTabla.put("proveedor", "Proveedor :200");
        infoConstruccionTabla.put("direccion", "Direccion :400");
        infoConstruccionTabla.put("telefonos", "Telefonos :300");
        infoConstruccionTabla.put("calles", "Entre Calles :300");
        
        
        ModelAndView x = new ModelAndView("proveedoresdirecciones/startup", "title", "Cat&aacute;logo de Direcciones de proveedores");//nombre de la carpeta de la vista
        
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
        //String decodificado = Base64Coder.decodeString(codificado);
        
        //id de usuario codificado
        x = x.addObject("iu", codificado);
        
        return x;
    }
    
    //para el grid
    @RequestMapping(value="/getAllgetProveedoresDirecciones.json", method = RequestMethod.POST)
    public @ResponseBody HashMap<String,ArrayList<HashMap<String, Object>>> getAllgetProveedoresDireccionesJson(
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
        
        
       
        //Catalogo direcciones de  proveedores
        Integer app_selected = 56;
        
        //decodificar id de usuario
        Integer id_usuario = Integer.parseInt(Base64Coder.decodeString(id_user_cod));
        
        //variables para el buscador
        String id_proveedor =String.valueOf(has_busqueda.get("id_proveedor"));
        
        String data_string = app_selected+"___"+id_usuario+"___"+id_proveedor;
        
        //obtiene total de registros en base de datos, con los parametros de busqueda
        int total_items = this.getCxpDao().countAll(data_string);
        
        //calcula el total de paginas
        int total_pags = resource.calculaTotalPag(total_items,items_por_pag);
        
        //variables que necesita el datagrid, para no tener que hacer uno por cada aplicativo
        DataPost dataforpos = new DataPost(orderby, desc, items_por_pag, pag_start, display_pag, input_json, cadena_busqueda,total_items,total_pags, id_user_cod);
        
        int offset = resource.__get_inicio_offset(items_por_pag, pag_start);
        
        //obtiene los registros para el grid, de acuerdo a los parametros de busqueda
        jsonretorno.put("Data", this.getCxpDao().getProveedoresDirecciones_PaginaGrid(data_string, offset, items_por_pag, orderby, desc));
              
        
        //obtiene el hash para los datos que necesita el datagrid
        jsonretorno.put("DataForGrid", dataforpos.formaHashForPos(dataforpos));
        
        return jsonretorno;
    }    
    
    @RequestMapping(method = RequestMethod.POST, value="/getProveedoresDirecciones.json")
    public @ResponseBody HashMap<String,ArrayList<HashMap<String, String>>> getProveedoresDireccionesJson(
            @RequestParam(value="id", required=true) Integer id,
            Model model
            ) {
                
                log.log(Level.INFO, "Ejecutando getProveedoresDireccionesJson de {0}", com.agnux.kemikal.controllers.ProveedoresDireccionesController.class.getName());
                HashMap<String,ArrayList<HashMap<String, String>>> jsonretorno = new HashMap<String,ArrayList<HashMap<String, String>>>();
                ArrayList<HashMap<String, String>> datos = new ArrayList<HashMap<String, String>>();
                ArrayList<HashMap<String, String>> paises = new ArrayList<HashMap<String, String>>();
                ArrayList<HashMap<String, String>> estados = new ArrayList<HashMap<String, String>>();
                ArrayList<HashMap<String, String>> municipios = new ArrayList<HashMap<String, String>>();
                paises = this.cxpDao.getPaises();
               // estados= this.getCxpDao().getEntidadesForThisPais(datos.get(0).get("pais_id").toString());
                
                if( id != 0  ){
                    datos = this.getCxpDao().getProveedoresDirecciones_Datos(id);
                    estados = this.getCxpDao().getEntidadesForThisPais(datos.get(0).get("pais_id").toString());
                    municipios = this.getCxpDao().getLocalidadesForThisEntidad(datos.get(0).get("pais_id").toString(), datos.get(0).get("estado_id").toString());
                }

                jsonretorno.put("Direcciones", datos);
                jsonretorno.put("paises", paises);
                jsonretorno.put("estados", estados);
                jsonretorno.put("municipios", municipios);
               
                return jsonretorno;
            }
    
    //crear y editar 
    @RequestMapping(method = RequestMethod.POST, value="/edit.json")
    public @ResponseBody HashMap<String, String> editJson(
            @RequestParam(value="identificador", required=true) String id,
            @RequestParam(value="calle", required=true) String calle,
            @RequestParam(value="codigoPostal", required=true) String codigoPostal,
            @RequestParam(value="colonia", required=true) String colonia,
            @RequestParam(value="entreCalles", required=true) String entreCalles,
            @RequestParam(value="extDos", required=true) String extDos,
            @RequestParam(value="extUno", required=true) String extUno,
            @RequestParam(value="numExterior", required=true) String numExterior,
            @RequestParam(value="numInterior", required=true) String numInterior,
            @RequestParam(value="proveedor", required=true) String proveedor, //no
            @RequestParam(value="id_proveedor", required=true) String id_proveedor,
            @RequestParam(value="select_estado", required=true) Integer id_estado,
            @RequestParam(value="select_municipio", required=true) Integer id_municipio,
            @RequestParam(value="select_pais", required=true) Integer id_pais,
            @RequestParam(value="telDos", required=true) String telDos,
            @RequestParam(value="telUno", required=true) String telUno,
            Model model,@ModelAttribute("user") UserSessionData user) {

        HashMap<String, String> jsonretorno = new HashMap<String, String>();
        HashMap<String, String> succes = new HashMap<String, String>();
        

        Integer app_selected = 56;
        String command_selected = "new";
        Integer id_usuario= user.getUserId();//variable para el id  del usuario
        String extra_data_array = "'sin datos'";
        String actualizo = "0";

        //empresa =this.CxcDao.getSucursales(id_empresa);
        if( id.equals("0") ){
            command_selected = "new";
        }else{
            command_selected = "edit";
        }            

        String data_string = app_selected+"___"+command_selected+"___"+id_usuario+"___"+id+"___"+calle.toUpperCase()+"___"+codigoPostal+"___"+colonia.toUpperCase()+"___"+entreCalles.toUpperCase()+"___"+extDos+"___"+extUno+"___"+numExterior+"___"+numInterior+"___"+id_proveedor+"___"+id_estado+"___"+id_municipio+"___"+id_pais+"___"+telDos+"___"+telUno;

        succes = this.getCxpDao().selectFunctionValidateAaplicativo(data_string,app_selected,extra_data_array);

        log.log(Level.INFO, "despues de validacion {0}", String.valueOf(succes.get("success")));
        
        if( String.valueOf(succes.get("success")).equals("true") ){
            actualizo = this.getCxpDao().selectFunctionForThisApp(data_string, extra_data_array);
        }

        jsonretorno.put("success",String.valueOf(succes.get("success")));

        log.log(Level.INFO, "Salida json {0}", String.valueOf(jsonretorno.get("success")));
        return jsonretorno;
    }
    
    //cambiar a borrado logico un registro
    @RequestMapping(method = RequestMethod.POST, value="/logicDelete.json")
    public @ResponseBody HashMap<String, String> logicDeleteJson(
            @RequestParam(value="id", required=true) Integer id,
            @RequestParam(value="iu", required=true) String id_user,
            Model model
            ) {
        
        HashMap<String, String> jsonretorno = new HashMap<String, String>();
        //decodificar id de usuario
        Integer id_usuario = Integer.parseInt(Base64Coder.decodeString(id_user));
        
        Integer app_selected = 56;
        String command_selected = "delete";
        String extra_data_array = "'sin datos'";
        String data_string = app_selected+"___"+command_selected+"___"+id_usuario+"___"+id;
        
        System.out.println("Ejecutando borrado logico direcciones de proveedores");
        jsonretorno.put("success",String.valueOf( this.getCxpDao().selectFunctionForThisApp(data_string,extra_data_array)) );
        
        return jsonretorno;
    }
    
     //obtiene el las entidades de un pais
    @RequestMapping(method = RequestMethod.POST, value="/getEntidades.json")
    public @ResponseBody HashMap<String,ArrayList<HashMap<String, String>>> getEntidadesJson(
            @RequestParam(value="id_pais", required=true) String id_pais,
            Model model
            ) {
        
        HashMap<String,ArrayList<HashMap<String, String>>> jsonretorno = new HashMap<String,ArrayList<HashMap<String, String>>>();
        
        jsonretorno.put("Entidades", this.getCxpDao().getEntidadesForThisPais(id_pais));
        
        return jsonretorno;
    }
    
    //obtiene el las localidades de una entidad
    @RequestMapping(method = RequestMethod.POST, value="/getLocalidades.json")
    public @ResponseBody HashMap<String,ArrayList<HashMap<String, String>>> getLocalidadesJson(
            @RequestParam(value="id_pais", required=true) String id_pais,
            @RequestParam(value="id_entidad", required=true) String id_entidad,
            Model model
            ) {
        
        HashMap<String,ArrayList<HashMap<String, String>>> jsonretorno = new HashMap<String,ArrayList<HashMap<String, String>>>();
        
        jsonretorno.put("Localidades", this.getCxpDao().getLocalidadesForThisEntidad(id_pais, id_entidad));
        
        return jsonretorno;
    }
    
    //obtiene los proveedores para el buscador
    @RequestMapping(method = RequestMethod.POST, value="/getBuacadorProveedores.json")
    public @ResponseBody HashMap<String,ArrayList<HashMap<String, String>>> getBuacadorProveedoresJson(
            @RequestParam(value="rfc", required=true) String rfc,
            @RequestParam(value="email", required=true) String email,
            @RequestParam(value="nombre", required=true) String nombre,
            @RequestParam(value="iu", required=true) String id_user,
            Model model
            ) {
        
        log.log(Level.INFO, "Ejecutando getBuacadorProveedores de {0}", CxpProvFacturasController.class.getName());
        HashMap<String,ArrayList<HashMap<String, String>>> jsonretorno = new HashMap<String,ArrayList<HashMap<String, String>>>();
        ArrayList<HashMap<String, String>> proveedores = new ArrayList<HashMap<String, String>>();
        HashMap<String, String> userDat = new HashMap<String, String>();
        
        //decodificar id de usuario
        Integer id_usuario = Integer.parseInt(Base64Coder.decodeString(id_user));
        userDat = this.getHomeDao().getUserById(id_usuario);
        
        Integer id_empresa = Integer.parseInt(userDat.get("empresa_id"));
        
        proveedores = this.getCxpDao().getProvFacturas_BuscadorProveedores(rfc, email, nombre,id_empresa);
        
        jsonretorno.put("proveedores", proveedores);
        
        return jsonretorno;
    }
}
