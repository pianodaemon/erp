package com.agnux.kemikal.controllers;

import com.agnux.cfd.v2.Base64Coder;
import com.agnux.common.helpers.StringHelper;
import com.agnux.common.obj.DataPost;
import com.agnux.common.obj.ResourceProject;
import com.agnux.common.obj.UserSessionData;
import com.agnux.kemikal.interfacedaos.HomeInterfaceDao;
import com.agnux.kemikal.interfacedaos.InvInterfaceDao;
import java.io.IOException;
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
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttributes;


@Controller
@SessionAttributes({"user"})
@RequestMapping("/almacenes/")
public class AlmacenesController {
    ResourceProject resource = new ResourceProject();
    private static final Logger log  = Logger.getLogger(AlmacenesController.class.getName());
    
    @Autowired
    @Qualifier("daoInv")
    private InvInterfaceDao invDao;
    
    public InvInterfaceDao getInvDao() {
        return invDao;
    }
    
    @Autowired
    @Qualifier("daoHome")
    private HomeInterfaceDao HomeDao;
    
    public HomeInterfaceDao getHomeDao() {
        return HomeDao;
    }
    
    @RequestMapping(value="/startup.agnux")
    public ModelAndView startUp(HttpServletRequest request, HttpServletResponse response, 
            @ModelAttribute("user") UserSessionData user
            )throws ServletException, IOException {
        
        log.log(Level.INFO, "Ejecutando starUp de {0}", AlmacenesController.class.getName());
        LinkedHashMap<String,String> infoConstruccionTabla = new LinkedHashMap<String,String>();
        
        infoConstruccionTabla.put("id", "Acciones:90");
        infoConstruccionTabla.put("titulo", "Nombre:300");
        infoConstruccionTabla.put("tipo", "Tipo:110");
        infoConstruccionTabla.put("reporteo", "Rep.:30");
        infoConstruccionTabla.put("ventas", "Vta.:30");
        infoConstruccionTabla.put("compras", "Com.:30");
        infoConstruccionTabla.put("traspaso", "Tras.:30");
        infoConstruccionTabla.put("reabastecimiento", "Rea.:30");
        infoConstruccionTabla.put("garantias", "Gar.:30");
        infoConstruccionTabla.put("consignacion", "Con.:30");
        infoConstruccionTabla.put("recepcion_mat", "R.Mat.:30");
        infoConstruccionTabla.put("explosion_mat", "E.Mat.:30");
        
        ModelAndView x = new ModelAndView("almacenes/startup", "title", "Cat&aacute;logo de Almacenes");
        
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
    
    
    
    //obtiene los tipos de almacen  para el buscador
    @RequestMapping(method = RequestMethod.POST, value="/getTiposAlmacen.json")
    public @ResponseBody HashMap<String,ArrayList<HashMap<String, String>>> getTiposAlmacenJson(
            Model model
            ) {
        HashMap<String,ArrayList<HashMap<String, String>>> jsonretorno = new HashMap<String,ArrayList<HashMap<String, String>>>();
        ArrayList<HashMap<String, String>> tipos_almacen = new ArrayList<HashMap<String, String>>();
        
        tipos_almacen = this.getInvDao().getAlmacennes_TiposAlmacen();
        
        jsonretorno.put("Tipos", tipos_almacen);
        
        return jsonretorno;
    }
    
    
    
    //obtiene todos los  almacenes para el grid
    @RequestMapping(value="/getAllAlmacenes.json", method = RequestMethod.POST)
    public @ResponseBody HashMap<String,ArrayList<HashMap<String, Object>>> getAllAlmacenesJson(
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
        
        //aplicativo 1,catalogo de almacenes
        Integer app_selected = 1;
        
        //decodificar id de usuario
        Integer id_usuario = Integer.parseInt(Base64Coder.decodeString(id_user_cod));
        
        //variables para el buscador
        String nombre = "%"+StringHelper.isNullString(String.valueOf(has_busqueda.get("nombre")))+"%";
        String tipo = StringHelper.isNullString(String.valueOf(has_busqueda.get("tipo")));
        
        String data_string = app_selected+"___"+id_usuario+"___"+nombre+"___"+tipo;
        
        //obtiene total de registros en base de datos, con los parametros de busqueda
        int total_items = this.getInvDao().countAll(data_string);
        
        //calcula el total de paginas
        int total_pags = resource.calculaTotalPag(total_items,items_por_pag);
        
        //variables que necesita el datagrid, para no tener que hacer uno por cada aplicativo
        DataPost dataforpos = new DataPost(orderby, desc, items_por_pag, pag_start, display_pag, input_json, cadena_busqueda,total_items,total_pags, id_user_cod);
        
        int offset = resource.__get_inicio_offset(items_por_pag, pag_start);
        
        //obtiene los registros para el grid, de acuerdo a los parametros de busqueda
        jsonretorno.put("Data", this.getInvDao().getAlmacenes_PaginaGrid(data_string, offset, items_por_pag, orderby, desc));
        //obtiene el hash para los datos que necesita el datagrid
        jsonretorno.put("DataForGrid", dataforpos.formaHashForPos(dataforpos));
        
        return jsonretorno;
    }
    
    
    
    
    
    @RequestMapping(method = RequestMethod.POST, value="/getAlmacen.json")
    public @ResponseBody HashMap<String,ArrayList<HashMap<String, String>>> getAlmacenJson(
            @RequestParam(value="id", required=true) Integer id_almacen,
            @RequestParam(value="iu", required=true) String id_user_cod,
            Model model
            ) {
        
        log.log(Level.INFO, "Ejecutando getAlmacenJson de {0}", AlmacenesController.class.getName());
        HashMap<String,ArrayList<HashMap<String, String>>> jsonretorno = new HashMap<String,ArrayList<HashMap<String, String>>>();
        HashMap<String, String> userDat = new HashMap<String, String>();
        ArrayList<HashMap<String, String>> datosAlmacen = new ArrayList<HashMap<String, String>>();
        ArrayList<HashMap<String, String>> tiposAlmacen = new ArrayList<HashMap<String, String>>();
        ArrayList<HashMap<String, String>> paises = new ArrayList<HashMap<String, String>>();
        ArrayList<HashMap<String, String>> entidades = new ArrayList<HashMap<String, String>>();
        ArrayList<HashMap<String, String>> municipios = new ArrayList<HashMap<String, String>>();
        
        ArrayList<HashMap<String, String>> sucursales = new ArrayList<HashMap<String, String>>();
        ArrayList<HashMap<String, String>> sucursales_seleccionadas = new ArrayList<HashMap<String, String>>();
        
        
        //decodificar id de usuario
        Integer id_usuario = Integer.parseInt(Base64Coder.decodeString(id_user_cod));
        userDat = this.getHomeDao().getUserById(id_usuario);
        
        Integer id_empresa = Integer.parseInt(userDat.get("empresa_id"));
        
        if( id_almacen != 0  ){
            datosAlmacen = this.getInvDao().getAlmacenes_Datos(id_almacen);
            entidades = this.getInvDao().getEntidadesForThisPais(datosAlmacen.get(0).get("gral_pais_id").toString());
            municipios = this.getInvDao().getLocalidadesForThisEntidad(datosAlmacen.get(0).get("gral_pais_id").toString(), datosAlmacen.get(0).get("gral_edo_id").toString());
            sucursales_seleccionadas = this.getInvDao().getAlmacenes_SucursalesON(id_almacen, id_empresa);
        }
        
        tiposAlmacen=this.getInvDao().getAlmacennes_TiposAlmacen();
        paises = this.getInvDao().getPaises();
        sucursales = this.getInvDao().getAlmacenes_Sucursales(id_almacen, id_empresa);
        
        jsonretorno.put("Almacen", datosAlmacen);
        jsonretorno.put("Tipos", tiposAlmacen);
        jsonretorno.put("Paises", paises);
        jsonretorno.put("Entidades", entidades);
        jsonretorno.put("Municipios", municipios);
        jsonretorno.put("Sucursales", sucursales);
        jsonretorno.put("SucursalesOn", sucursales_seleccionadas);
        
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
        jsonretorno.put("Localidades", this.getInvDao().getLocalidadesForThisEntidad(id_pais, id_entidad));
        return jsonretorno;
    }
    
    
    
    //obtiene el las entidades de un pais
    @RequestMapping(method = RequestMethod.POST, value="/getEntidades.json")
    public @ResponseBody HashMap<String,ArrayList<HashMap<String, String>>> getEntidadesJson(
            @RequestParam(value="id_pais", required=true) String id_pais,
            Model model
            ) {
        
        HashMap<String,ArrayList<HashMap<String, String>>> jsonretorno = new HashMap<String,ArrayList<HashMap<String, String>>>();
        
        jsonretorno.put("Entidades", this.getInvDao().getEntidadesForThisPais(id_pais));
        
        return jsonretorno;
    }
    
    
    
    //crear y editar
    @RequestMapping(method = RequestMethod.POST, value="/edit.json")
    public @ResponseBody HashMap<String, String> editJson(
            @RequestParam(value="identificador", required=true) String id,
            @RequestParam(value="nombre", required=false) String nombre,
            @RequestParam(value="calle", required=false) String calle,
            @RequestParam(value="numero", required=false) String numero,
            @RequestParam(value="colonia", required=false) String colonia,
            @RequestParam(value="cp", required=false) String cp,
            @RequestParam(value="pais", required=false) String pais,
            @RequestParam(value="estado", required=false) String estado,
            @RequestParam(value="municipio", required=false) String municipio,
            @RequestParam(value="tel1", required=false) String tel1,
            @RequestParam(value="tel2", required=false) String tel2,
            @RequestParam(value="ext1", required=false) String ext1,
            @RequestParam(value="ext2", required=false) String ext2,
            @RequestParam(value="responsable", required=false) String responsable,
            @RequestParam(value="puesto", required=false) String puesto,
            @RequestParam(value="email", required=false) String email,
            @RequestParam(value="tipo_almacen", required=false) String tipo_almacen,
            @RequestParam(value="check_compras", required=false) String check_compras,
            @RequestParam(value="check_consignacion", required=false) String check_consignacion,
            @RequestParam(value="check_explosion_mat", required=false) String check_explosion_mat,
            @RequestParam(value="check_garantias", required=false) String check_garantias,
            @RequestParam(value="check_reabastecimiento", required=false) String check_reabastecimiento,
            @RequestParam(value="check_recepcion_mat", required=false) String check_recepcion_mat,
            @RequestParam(value="check_reporteo", required=false) String check_reporteo,
            @RequestParam(value="check_traspaso", required=false) String check_traspaso,
            @RequestParam(value="check_ventas", required=false) String check_ventas,
            @RequestParam(value="suc_on", required=true) String suc_on,
            Model model,@ModelAttribute("user") UserSessionData user
            ) {
        
        HashMap<String, String> jsonretorno = new HashMap<String, String>();
        HashMap<String, String> succes = new HashMap<String, String>();
        Integer app_selected = 1;
        String command_selected = "new";
        Integer id_usuario= user.getUserId();//variable para el id  del usuario
        String extra_data_array = "'sin datos'";
        String actualizo = "0";
        
        
        
        if(check_compras == null || !check_compras.equals("on")){
            check_compras = "false";
        }else{
            check_compras = "true";
        }
        
        if(check_consignacion == null || !check_consignacion.equals("on")){
            check_consignacion = "false";
        }else{
            check_consignacion = "true";
        }
        
        if(check_explosion_mat == null || !check_explosion_mat.equals("on")){
            check_explosion_mat = "false";
        }else{
            check_explosion_mat = "true";
        }
        
        if(check_garantias == null || !check_garantias.equals("on")){
            check_garantias = "false";
        }else{
            check_garantias = "true";
        }
        
        if(check_reabastecimiento == null || !check_reabastecimiento.equals("on")){
            check_reabastecimiento = "false";
        }else{
            check_reabastecimiento = "true";
        }
        
        if(check_recepcion_mat == null || !check_recepcion_mat.equals("on")){
            check_recepcion_mat = "false";
        }else{
            check_recepcion_mat = "true";
        }
        
        if(check_reporteo == null || !check_reporteo.equals("on")){
            check_reporteo = "false";
        }else{
            check_reporteo = "true";
        }
        
        if(check_traspaso == null || !check_traspaso.equals("on")){
            check_traspaso = "false";
        }else{
            check_traspaso = "true";
        }
        
        if(check_ventas == null || !check_ventas.equals("on")){
            check_ventas = "false";
        }else{
            check_ventas = "true";
        }
        
        
        
        if(!suc_on.equals("")){
            String arreglo[] = suc_on.split(",");
            for(int i=0; i<arreglo.length; i++){
                arreglo[i]= "'"+arreglo[i]+"'";
            }
            //serializar el arreglo
            if(arreglo.length > 0){
                extra_data_array = StringUtils.join(arreglo, ",");
            }else{
                extra_data_array = "'sin datos'";
            }
        }else{
            extra_data_array = "'sin datos'";
        }
        
        
        
        if( id.equals("0") ){
            command_selected = "new";
        }else{
            command_selected = "edit";
        }
        
        
        String data_string = 
            app_selected+"___"+
            command_selected+"___"+
            id_usuario+"___"+
            id+"___"+
            nombre.toUpperCase()+"___"+	
            calle.toUpperCase()+"___"+	
            numero+"___"+
            colonia.toUpperCase()+"___"+
            cp+"___"+
            pais+"___"+
            estado+"___"+
            municipio+"___"+	
            tel1+"___"+	
            tel2+"___"+	
            ext1+"___"+	
            ext2+"___"+	
            responsable.toUpperCase()+"___"+
            puesto.toUpperCase()+"___"+
            email+"___"+
            tipo_almacen+"___"+
            check_compras+"___"+
            check_consignacion+"___"+
            check_explosion_mat+"___"+
            check_garantias+"___"+
            check_reabastecimiento+"___"+
            check_recepcion_mat+"___"+
            check_reporteo+"___"+
            check_traspaso+"___"+
            check_ventas;
        
        succes = this.getInvDao().selectFunctionValidateAaplicativo(data_string,app_selected,extra_data_array);
        
        log.log(Level.INFO, "despues de validacion {0}", String.valueOf(succes.get("success")));
        if( String.valueOf(succes.get("success")).equals("true") ){
            actualizo = this.getInvDao().selectFunctionForThisApp(data_string, extra_data_array);
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
        
        Integer app_selected = 1;
        String command_selected = "delete";
        String extra_data_array = "'sin datos'";
        String data_string = app_selected+"___"+command_selected+"___"+id_usuario+"___"+id;
        
        System.out.println("Ejecutando borrado logico  de un almacen");
        jsonretorno.put("success",String.valueOf( this.getInvDao().selectFunctionForThisApp(data_string,extra_data_array)) );
        
        return jsonretorno;
    }
    
    
    
    
    
    
    
    
    
}
