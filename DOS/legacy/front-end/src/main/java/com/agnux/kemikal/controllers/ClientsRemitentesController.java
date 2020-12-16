package com.agnux.kemikal.controllers;

import com.agnux.cfd.v2.Base64Coder;
import com.agnux.common.helpers.StringHelper;
import com.agnux.common.obj.DataPost;
import com.agnux.common.obj.ResourceProject;
import com.agnux.common.obj.UserSessionData;
import com.agnux.kemikal.interfacedaos.CxcInterfaceDao;
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


@Controller
@SessionAttributes({"user"})
@RequestMapping("/clientsremiten/")
public class ClientsRemitentesController {
    private static final Logger log  = Logger.getLogger(ClientsRemitentesController.class.getName());
    ResourceProject resource = new ResourceProject();
    
    @Autowired
    @Qualifier("daoCxc")
    private CxcInterfaceDao cxcDao;
    
    public CxcInterfaceDao getCxcDao() {
        return cxcDao;
    }
    
    public void setCxcDao(CxcInterfaceDao cxcDao) {
        this.cxcDao = cxcDao;
    }
    
    @Autowired
    @Qualifier("daoHome")
    private HomeInterfaceDao HomeDao;
    
    public HomeInterfaceDao getHomeDao() {
        return HomeDao;
    }
    
    @RequestMapping(value="/startup.agnux")
    public ModelAndView startUp(HttpServletRequest request, HttpServletResponse response, @ModelAttribute("user") UserSessionData user)
            throws ServletException, IOException {
        
        log.log(Level.INFO, "Ejecutando starUp de {0}", ClientsRemitentesController.class.getName());
        LinkedHashMap<String,String> infoConstruccionTabla = new LinkedHashMap<String,String>();
        
        infoConstruccionTabla.put("id", "Acciones:90");
        infoConstruccionTabla.put("folio", "No.Control:100");
        infoConstruccionTabla.put("rfc", "RFC:100");
        infoConstruccionTabla.put("remitente", "Nombre o Razon Social:250");
        infoConstruccionTabla.put("tel", "Tel&eacute;fono:100");
        infoConstruccionTabla.put("tipo", "Tipo:100");
        
        ModelAndView x = new ModelAndView("clientsremiten/startup", "title", "Cat&aacute;logo de Remitentes");
        
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
    
    
    
    //obtener todas las direcciones fiscales de los clientes
    @RequestMapping(value="/getAllClientsRemiten.json", method = RequestMethod.POST)
    public @ResponseBody HashMap<String,ArrayList<HashMap<String, Object>>> getAllClientsRemitenJson(
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
        
        //Catalogo de Remitentes
        Integer app_selected = 147;
        
        //Decodificar id de usuario
        Integer id_usuario = Integer.parseInt(Base64Coder.decodeString(id_user_cod));
        
        //Variables para el buscador
        String folio = ""+StringHelper.isNullString(String.valueOf(has_busqueda.get("folio")))+"";
        String remitente = "%"+StringHelper.isNullString(String.valueOf(has_busqueda.get("remitente")))+"%";
        String rfc = "%"+StringHelper.isNullString(String.valueOf(has_busqueda.get("rfc")))+"%";
        String tipo = StringHelper.isNullString(String.valueOf(has_busqueda.get("tipo")));
        
        String data_string = app_selected+"___"+id_usuario+"___"+folio.toUpperCase()+"___"+remitente.toUpperCase()+"___"+rfc.toUpperCase()+"___"+tipo;
        
        //obtiene total de registros en base de datos, con los parametros de busqueda
        int total_items = this.getCxcDao().countAll(data_string);
        
        //calcula el total de paginas
        int total_pags = resource.calculaTotalPag(total_items,items_por_pag);
        
        //variables que necesita el datagrid, para no tener que hacer uno por cada aplicativo
        DataPost dataforpos = new DataPost(orderby, desc, items_por_pag, pag_start, display_pag, input_json, cadena_busqueda,total_items,total_pags, id_user_cod);
        
        int offset = resource.__get_inicio_offset(items_por_pag, pag_start);
        
        //obtiene los registros para el grid, de acuerdo a los parametros de busqueda
        jsonretorno.put("Data", this.getCxcDao().getClientsRemiten_PaginaGrid(data_string, offset, items_por_pag, orderby, desc));
        
        //obtiene el hash para los datos que necesita el datagrid
        jsonretorno.put("DataForGrid", dataforpos.formaHashForPos(dataforpos));
        
        return jsonretorno;
    }
    
    
    
    @RequestMapping(method = RequestMethod.POST, value="/getRemitente.json")
    public @ResponseBody HashMap<String,ArrayList<HashMap<String, Object>>> getRemitenteJson(
            @RequestParam(value="id", required=true) Integer id,
            @RequestParam(value="iu", required=true) String id_user,
            Model model
            ) {
        
        log.log(Level.INFO, "Ejecutando getRemitenteJson de {0}", ClientsRemitentesController.class.getName());
        HashMap<String,ArrayList<HashMap<String, Object>>> jsonretorno = new HashMap<String,ArrayList<HashMap<String, Object>>>();
        HashMap<String, String> userDat = new HashMap<String, String>();
        ArrayList<HashMap<String, Object>> paises = new ArrayList<HashMap<String, Object>>();
        ArrayList<HashMap<String, Object>> estados = new ArrayList<HashMap<String, Object>>();
        ArrayList<HashMap<String, Object>> municipios = new ArrayList<HashMap<String, Object>>();
        ArrayList<HashMap<String, Object>> datos = new ArrayList<HashMap<String, Object>>();

        
        //decodificar id de usuario
        Integer id_usuario = Integer.parseInt(Base64Coder.decodeString(id_user));
        userDat = this.getHomeDao().getUserById(id_usuario);
        
        if( id != 0  ){
            datos = this.getCxcDao().getClientsRemiten_Datos(id);
            estados = this.getCxcDao().getEntidadesForThisPais(datos.get(0).get("pais_id").toString());
            municipios = this.getCxcDao().getLocalidadesForThisEntidad(datos.get(0).get("pais_id").toString(), datos.get(0).get("estado_id").toString());
        }
        
        paises = this.getCxcDao().getPaises();
        
        jsonretorno.put("Datos", datos);
        jsonretorno.put("Paises", paises);
        jsonretorno.put("Estados", estados);
        jsonretorno.put("Municipios", municipios);
        return jsonretorno;
    }
    
    
    //obtiene el las Municipios de un Estado
    @RequestMapping(method = RequestMethod.POST, value="/getMunicipios.json")
    public @ResponseBody HashMap<String,ArrayList<HashMap<String, Object>>> getMunicipiosJson(
            @RequestParam(value="id_pais", required=true) String id_pais,
            @RequestParam(value="id_entidad", required=true) String id_entidad,
            Model model
        ) {
        HashMap<String,ArrayList<HashMap<String, Object>>> jsonretorno = new HashMap<String,ArrayList<HashMap<String, Object>>>();
        jsonretorno.put("Municipios", this.getCxcDao().getLocalidadesForThisEntidad(id_pais, id_entidad));
        return jsonretorno;
    }
    
    
    
    //obtiene el las Estados de un Pais
    @RequestMapping(method = RequestMethod.POST, value="/getEstados.json")
    public @ResponseBody HashMap<String,ArrayList<HashMap<String, Object>>> getEstadosJson(
            @RequestParam(value="id_pais", required=true) String id_pais,
            Model model
        ) {
        HashMap<String,ArrayList<HashMap<String, Object>>> jsonretorno = new HashMap<String,ArrayList<HashMap<String, Object>>>();
        jsonretorno.put("Estados", this.getCxcDao().getEntidadesForThisPais(id_pais));
        return jsonretorno;
    }
    
    
    //Crear y editar
    @RequestMapping(method = RequestMethod.POST, value="/edit.json")
    public @ResponseBody HashMap<String, String> editJson(
        @RequestParam(value="identificador", required=true) String identificador,
        @RequestParam(value="remitente", required=true) String remitente,
        @RequestParam(value="rfc", required=true) String rfc,
        @RequestParam(value="calle", required=true) String calle,
        @RequestParam(value="colonia", required=true) String colonia,
        @RequestParam(value="numero_int", required=true) String numero_int,
        @RequestParam(value="numero_ext", required=true) String numero_ext,
        @RequestParam(value="cp", required=true) String cp,
        @RequestParam(value="select_tipo", required=false) String select_tipo,
        @RequestParam(value="select_pais", required=false) String select_pais,
        @RequestParam(value="select_estado", required=false) String select_estado,
        @RequestParam(value="select_municipio", required=false) String select_municipio,
        @RequestParam(value="tel1", required=true) String tel1,
        @RequestParam(value="tel2", required=true) String tel2,
        @RequestParam(value="ext1", required=true) String ext1,
        @RequestParam(value="email", required=true) String email,
        Model model,@ModelAttribute("user") UserSessionData user
    ) {
        
        //Catalogo de Remitentes
        Integer app_selected = 147;
        String command_selected = "new";
        Integer id_usuario= user.getUserId();//variable para el id  del usuario
        String arreglo[];
        String extra_data_array = "'sin datos'";
        String actualizo = "0";
        
        HashMap<String, String> jsonretorno = new HashMap<String, String>();
        
        HashMap<String, String> succes = new HashMap<String, String>();
        
        if( identificador.equals("0") ){
            command_selected = "new";
        }else{
            command_selected = "edit";
        }
        
        //Verificar si los select trae valor null, asignar un cero
        select_tipo = StringHelper.verificarSelect(select_tipo);
        select_pais = StringHelper.verificarSelect(select_pais);
        select_estado = StringHelper.verificarSelect(select_estado);
        select_municipio = StringHelper.verificarSelect(select_municipio);
        
        String data_string = 
        app_selected
        +"___"+command_selected
        +"___"+id_usuario
        +"___"+identificador
        +"___"+remitente.toUpperCase()
        +"___"+rfc.toUpperCase()
        +"___"+select_tipo
        +"___"+calle.toUpperCase()
        +"___"+numero_int
        +"___"+numero_ext
        +"___"+colonia.toUpperCase()
        +"___"+cp
        +"___"+select_pais
        +"___"+select_estado
        +"___"+select_municipio
        +"___"+tel1
        +"___"+ext1
        +"___"+tel2
        +"___"+email;
        
        //System.out.println("data_string: "+data_string);
        
        succes = this.getCxcDao().selectFunctionValidateAaplicativo(data_string,app_selected,extra_data_array);
        
        log.log(Level.INFO, "despues de validacion {0}", String.valueOf(succes.get("success")));
        if( String.valueOf(succes.get("success")).equals("true") ){
            actualizo = this.getCxcDao().selectFunctionForCxcAdmProcesos(data_string, extra_data_array);
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
        
        System.out.println("Borrado logico de Remitente");
        //decodificar id de usuario
        Integer id_usuario = Integer.parseInt(Base64Coder.decodeString(id_user));
        
        //Catalogo de Remitentes
        Integer app_selected = 147;
        String command_selected = "delete";
        String extra_data_array = "'sin datos'";
        
        String data_string = app_selected+"___"+command_selected+"___"+id_usuario+"___"+id;
        
        HashMap<String, String> jsonretorno = new HashMap<String, String>();
        
        jsonretorno.put("success",String.valueOf( this.getCxcDao().selectFunctionForCxcAdmProcesos(data_string,extra_data_array)) );
        
        return jsonretorno;
    }
}
