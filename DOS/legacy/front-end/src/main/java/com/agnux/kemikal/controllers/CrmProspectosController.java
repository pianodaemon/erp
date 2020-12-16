package com.agnux.kemikal.controllers;

import com.agnux.cfd.v2.Base64Coder;
import com.agnux.common.helpers.StringHelper;
import com.agnux.common.obj.DataPost;
import com.agnux.common.obj.ResourceProject;
import com.agnux.common.obj.UserSessionData;
import com.agnux.kemikal.interfacedaos.CrmInterfaceDao;
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
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;


@Controller
@SessionAttributes({"user"})
@RequestMapping("/crmprospectos/")
public class CrmProspectosController {
    ResourceProject resource = new ResourceProject();
    private static final Logger log  = Logger.getLogger(CrmProspectosController.class.getName());
    
    @Autowired
    @Qualifier("daoCrm")
    private CrmInterfaceDao CrmlDao;
    
    @Autowired
    @Qualifier("daoHome")   //permite controlar usuarios que entren
    private HomeInterfaceDao HomeDao;
    
    
    public CrmInterfaceDao getCrmDao() {
        return CrmlDao;
    }
    
    public HomeInterfaceDao getHomeDao() {
        return HomeDao;
    }
    
    @RequestMapping(value="/startup.agnux")
    public ModelAndView startUp(HttpServletRequest request, HttpServletResponse response, 
            @ModelAttribute("user") UserSessionData user
            )throws ServletException, IOException {
        
        log.log(Level.INFO, "Ejecutando starUp de {0}", CrmProspectosController.class.getName());
        LinkedHashMap<String,String> infoConstruccionTabla = new LinkedHashMap<String,String>();
        infoConstruccionTabla.put("id", "Acciones:90");
        infoConstruccionTabla.put("numero_control", "N&uacute;mero&nbsp;control:100");
        infoConstruccionTabla.put("razon_social", "Razon&nbsp;social:250");
        infoConstruccionTabla.put("rfc", "RFC:100");
        infoConstruccionTabla.put("tel", "Tel&eacute;fono:100");
        
        
        ModelAndView x = new ModelAndView("crmprospectos/startup", "title", "Prospectos");
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
    
    @RequestMapping(value="/getProspects.json", method = RequestMethod.POST)
    public @ResponseBody HashMap<String,ArrayList<HashMap<String, Object>>> getClientsJson(
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
        
        //aplicativo de clientes
        Integer app_selected = 113;
        
        //decodificar id de usuario
        Integer id_usuario = Integer.parseInt(Base64Coder.decodeString(id_user_cod));
        
        //variables para el buscador
        String cad_busqueda = "%"+StringHelper.isNullString(String.valueOf(has_busqueda.get("cadena_busqueda")))+"%";
        String filtro_por = StringHelper.isNullString(String.valueOf(has_busqueda.get("filtro_por")));
        
        String data_string = app_selected+"___"+id_usuario+"___"+cad_busqueda+"___"+filtro_por;
        
        //obtiene total de registros en base de datos, con los parametros de busqueda
        int total_items = this.getCrmDao().countAll(data_string);
        
        //calcula el total de paginas
        int total_pags = resource.calculaTotalPag(total_items,items_por_pag);
        
        //variables que necesita el datagrid, para no tener que hacer uno por cada aplicativo
        DataPost dataforpos = new DataPost(orderby, desc, items_por_pag, pag_start, display_pag, input_json, cadena_busqueda,total_items,total_pags, id_user_cod);
        
        int offset = resource.__get_inicio_offset(items_por_pag, pag_start);
        
        //obtiene los registros para el grid, de acuerdo a los parametros de busqueda
        jsonretorno.put("Data", this.getCrmDao().getProspectos_PaginaGrid(data_string, offset, items_por_pag, orderby, desc));
        //obtiene el hash para los datos que necesita el datagrid
        jsonretorno.put("DataForGrid", dataforpos.formaHashForPos(dataforpos));
        
        return jsonretorno;
    }
    
    
    @RequestMapping(method = RequestMethod.POST, value="/get_Prospectos.json")
    public @ResponseBody HashMap<String,ArrayList<HashMap<String,Object>>>get_prospectoJson(
            @RequestParam(value="id",required=true)Integer id,
            @RequestParam(value="iu",required=true) String id_user,
            Model model
            ){
            log.log(Level.INFO,"Ejecutando get_prospecto de {0}",CrmProspectosController.class.getName());
            HashMap<String,ArrayList<HashMap<String,Object>>>jsonretorno = new HashMap<String,ArrayList<HashMap<String,Object>>>();
            HashMap<String,String>userDat = new HashMap<String,String>();

            ArrayList<HashMap<String, Object>> paises = new ArrayList<HashMap<String, Object>>();
            ArrayList<HashMap<String, Object>> entidades = new ArrayList<HashMap<String, Object>>();
            ArrayList<HashMap<String, Object>> localidades = new ArrayList<HashMap<String, Object>>();
            ArrayList<HashMap<String, Object>> datosprospecto = new ArrayList<HashMap<String, Object>>();
            
            //ArrayList<HashMap<String, Object>> Tipo_prospecto = new ArrayList<HashMap<String, Object>>();
            ArrayList<HashMap<String, Object>> tipo_prospectos = new ArrayList<HashMap<String, Object>>();
            ArrayList<HashMap<String, Object>> Etapa_prospecto = new ArrayList<HashMap<String, Object>>();
            ArrayList<HashMap<String, Object>> Clasificacion_prospecto = new ArrayList<HashMap<String, Object>>();
            ArrayList<HashMap<String, Object>> Tipo_industria = new ArrayList<HashMap<String, Object>>();
            
            //decodificar id de usuario
            Integer id_usuario = Integer.parseInt(Base64Coder.decodeString(id_user));
            userDat = this.getHomeDao().getUserById(id_usuario);

            Integer id_empresa = Integer.parseInt(userDat.get("empresa_id"));
        
            if( id != 0  ){
                datosprospecto = this.getCrmDao().getProspecto_Datos(id);
                if(datosprospecto.size()>0){
                    entidades = this.getCrmDao().getEntidadesForThisPais(datosprospecto.get(0).get("pais_id").toString());
                    localidades = this.getCrmDao().getLocalidadesForThisEntidad(datosprospecto.get(0).get("pais_id").toString(), datosprospecto.get(0).get("estado_id").toString());
                }
                //Tipo_prospecto = this.getCrmDao().gettipo_prospecto(datosprospecto.get(0).get("tipo_prospecto_id").toString());
            }

            paises = this.getCrmDao().getPaises();
            tipo_prospectos = this.getCrmDao().getTipo_Prospecto();
            
            
            Etapa_prospecto= this.getCrmDao().getEtapas_prospecto();
            Clasificacion_prospecto= this.getCrmDao().getClasificacion_prospecto();
            Tipo_industria= this.getCrmDao().getTipo_industria();
            
            jsonretorno.put("Prospecto", datosprospecto);
            jsonretorno.put("Paises", paises);
            jsonretorno.put("Entidades", entidades);
            jsonretorno.put("Localidades", localidades);
            
            //jsonretorno.put("Tipo_prospecto", Tipo_prospecto);
            jsonretorno.put("Tipo_prospectos", tipo_prospectos);
            jsonretorno.put("Etapa_prospecto", Etapa_prospecto);
            
            jsonretorno.put("Clasificacion", Clasificacion_prospecto);
            jsonretorno.put("Tipo_industria", Tipo_industria);
            return jsonretorno;
        
    }
        
        
    //obtiene el las localidades de una entidad
    @RequestMapping(method = RequestMethod.POST, value="/getLocalidades.json")
    public @ResponseBody HashMap<String,ArrayList<HashMap<String, Object>>> getLocalidadesJson(
            @RequestParam(value="id_pais", required=true) String id_pais,
            @RequestParam(value="id_entidad", required=true) String id_entidad,
            Model model
            ) {
        
        HashMap<String,ArrayList<HashMap<String, Object>>> jsonretorno = new HashMap<String,ArrayList<HashMap<String, Object>>>();
        
        jsonretorno.put("Localidades", this.getCrmDao().getLocalidadesForThisEntidad(id_pais, id_entidad));
        
        return jsonretorno;
    }
    
    
    
    //obtiene el las entidades de un pais
    @RequestMapping(method = RequestMethod.POST, value="/getEntidades.json")
    public @ResponseBody HashMap<String,ArrayList<HashMap<String, Object>>> getEntidadesJson(
            @RequestParam(value="id_pais", required=true) String id_pais,
            Model model
            ) {
        HashMap<String,ArrayList<HashMap<String, Object>>> jsonretorno = new HashMap<String,ArrayList<HashMap<String, Object>>>();
        jsonretorno.put("Entidades", this.getCrmDao().getEntidadesForThisPais(id_pais));
        
        return jsonretorno;
    }
    
    
    //crear y editar un cliente
    @RequestMapping(method = RequestMethod.POST, value="/edit.json")
    public @ResponseBody HashMap<String, String> editJson(
            @RequestParam(value="identificador_prospecto", required=true) String id_prospecto,
            @RequestParam(value="prospecto", required=true) String razonsocial,
            @RequestParam(value="status", required=true) String status, //  Activo    o    Inicativo
            @RequestParam(value="estatus", required=true) String estatus, //  registros, llamadas presentaciones ...cieerre.
            @RequestParam(value="tipoprospecto", required=true) String tipo_prospecto, //  nacional  ... extrangenroo
            @RequestParam(value="rfc", required=true) String rfc,
            @RequestParam(value="calle", required=true) String calle,
            @RequestParam(value="numero_int", required=true) String numero_int,
            @RequestParam(value="entrecalles", required=true) String entrecalles,
            @RequestParam(value="numero_ext", required=true) String numero_ext,
            @RequestParam(value="colonia", required=true) String colonia,
            @RequestParam(value="cp", required=true) String cp,
            @RequestParam(value="pais", required=true) String pais,
            @RequestParam(value="estado", required=true) String estado,
            @RequestParam(value="municipio", required=true) String municipio,
            @RequestParam(value="loc_alternativa", required=true) String loc_alternativa,
            @RequestParam(value="tel1", required=true) String tel1,
            @RequestParam(value="ext1", required=true) String ext1,
            @RequestParam(value="fax", required=true) String fax,
            @RequestParam(value="tel2", required=true) String tel2,
            @RequestParam(value="ext2", required=true) String ext2,
            @RequestParam(value="email", required=true) String email,
            @RequestParam(value="contacto", required=true) String contacto,
            
            @RequestParam(value="clasificacion", required=true) String select_clasificacion,
            @RequestParam(value="tipoindustria", required=true) String select_tipoindustria,
            @RequestParam(value="observaciones", required=true) String observaciones,

            Model model,@ModelAttribute("user") UserSessionData user
            ) {
            
            Integer app_selected = 113;
            String command_selected = "new";
            Integer id_usuario= user.getUserId();//variable para el id  del usuario
            String arreglo[];
            String extra_data_array = null;
            String actualizo = "0";
          
            extra_data_array = "'sin datos'";
            HashMap<String, String> jsonretorno = new HashMap<String, String>();
            
            HashMap<String, String> succes = new HashMap<String, String>();
            
            if( id_prospecto.equals("0") ){
                command_selected = "new";
            }else{
                command_selected = "edit";
            }
          
            String data_string = 
            app_selected                   //1
            +"___"+command_selected        //2
            +"___"+id_usuario              //3
            +"___"+id_prospecto              //4
            +"___"+status                  //5
            +"___"+estatus                 //6
            +"___"+tipo_prospecto          //7
            +"___"+rfc.toUpperCase()       //8
            +"___"+razonsocial.toUpperCase()//9
            +"___"+calle.toUpperCase()      //10
            +"___"+numero_int               //11
            +"___"+entrecalles.toUpperCase()//12
            +"___"+numero_ext                 //13
            +"___"+colonia.toUpperCase()       //14
            +"___"+cp                          //15
            +"___"+pais                        //16
            +"___"+estado                      //17
            +"___"+municipio                   //18
            +"___"+loc_alternativa.toUpperCase()//19
            +"___"+tel1                         //20
            +"___"+ext1                         //21
            +"___"+fax                             //22
            +"___"+tel2                            //23
            +"___"+ext2                       //24
            +"___"+email                      //25
            +"___"+contacto.toUpperCase()      //26
            +"___"+select_clasificacion
            +"___"+select_tipoindustria
            +"___"+observaciones;
            
            //System.out.println("data_string: "+data_string);
            
            succes = this.getCrmDao().selectFunctionValidateAaplicativo(data_string,app_selected,extra_data_array);
            
            log.log(Level.INFO, "despues de validacion {0}", String.valueOf(succes.get("success")));
            if( String.valueOf(succes.get("success")).equals("true") ){
                actualizo = this.getCrmDao().selectFunctionForThisApp(data_string, extra_data_array);
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
        
        System.out.println("Borrado logico de cliente");
        //decodificar id de usuario
        Integer id_usuario = Integer.parseInt(Base64Coder.decodeString(id_user));
        
        Integer app_selected = 113;
        String command_selected = "delete";
        String extra_data_array = "'sin datos'";
        
        String data_string = app_selected+"___"+command_selected+"___"+id_usuario+"___"+id;
        
        HashMap<String, String> jsonretorno = new HashMap<String, String>();
        
        System.out.println("Ejecutando borrado logico de cliente");
        jsonretorno.put("success",String.valueOf( this.getCrmDao().selectFunctionForThisApp(data_string,extra_data_array)) );
        
        return jsonretorno;
    }
    
    
}
