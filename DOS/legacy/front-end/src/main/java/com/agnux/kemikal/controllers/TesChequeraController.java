/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.agnux.kemikal.controllers;

import com.agnux.kemikal.interfacedaos.TesInterfaceDao;

import com.agnux.cfd.v2.Base64Coder;
import com.agnux.common.helpers.StringHelper;
import com.agnux.common.obj.DataPost;
import com.agnux.common.obj.ResourceProject;
import com.agnux.common.obj.UserSessionData;
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
 * @author Vale Santos
 * 12/05/2012
 */

@Controller
@SessionAttributes({"user"})
@RequestMapping("/teschequera/")
public class TesChequeraController {
    ResourceProject resource = new ResourceProject();
    private static final Logger log  = Logger.getLogger(TesChequeraController.class.getName());
    
    @Autowired
    @Qualifier("daoTes")
    private TesInterfaceDao tesDao;
    
    public TesInterfaceDao getTesDao() {
        return tesDao;
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
        
        log.log(Level.INFO, "Ejecutando starUp de {0}", TesChequeraController.class.getName());
        LinkedHashMap<String,String> infoConstruccionTabla = new LinkedHashMap<String,String>();
        
        infoConstruccionTabla.put("id", "Acciones:70");
        infoConstruccionTabla.put("chequera", "Chequera:160");
        infoConstruccionTabla.put("moneda", "Moneda:60");
        infoConstruccionTabla.put("banco", "Banco:200");
        infoConstruccionTabla.put("nombre_sucursal", "Nombre&nbsp;Sucursal:200");
        infoConstruccionTabla.put("numero_sucursal", "Num.&nbsp;Sucursal:120");
        //infoConstruccionTabla.put("clabe", "CLABE:200");
        
        ModelAndView x = new ModelAndView("teschequera/startup", "title", "Cat&aacute;logo de chequera");
        
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
    
    
    
    
    @RequestMapping(value="/getAllTesChequera.json", method = RequestMethod.POST)
    public @ResponseBody HashMap<String,ArrayList<HashMap<String, Object>>> getAllTesChequeraJson(
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
        
        //aplicativo catalogo de bancos
        Integer app_selected = 59;
        
        //decodificar id de usuario
        Integer id_usuario = Integer.parseInt(Base64Coder.decodeString(id_user_cod));
        
        //variables para el buscador
        String id_banco = StringHelper.isNullString(String.valueOf(has_busqueda.get("id_banco")));
        
        
       String data_string = app_selected+"___"+id_usuario+"___"+id_banco;
        
        //obtiene total de registros en base de datos, con los parametros de busqueda
        int total_items = this.getTesDao().countAll(data_string);
        
        //calcula el total de paginas
        int total_pags = resource.calculaTotalPag(total_items,items_por_pag);
        
        //variables que necesita el datagrid, para no tener que hacer uno por cada aplicativo
        DataPost dataforpos = new DataPost(orderby, desc, items_por_pag, pag_start, display_pag, input_json, cadena_busqueda,total_items,total_pags, id_user_cod);
        
        int offset = resource.__get_inicio_offset(items_por_pag, pag_start);
        
        //obtiene los registros para el grid, de acuerdo a los parametros de busqueda
        jsonretorno.put("Data", this.getTesDao().getTesChequeraGrid(data_string, offset, items_por_pag, orderby, desc));
        //obtiene el hash para los datos que necesita el datagrid
        jsonretorno.put("DataForGrid", dataforpos.formaHashForPos(dataforpos));
        
        return jsonretorno;
    }
    
    
    //??????
    
    @RequestMapping(method = RequestMethod.POST, value="/getTesChera.json")
    public @ResponseBody HashMap<String,ArrayList<HashMap<String, String>>> getTesCheraJson(
            @RequestParam(value="id", required=true) Integer id,
            @RequestParam(value="iu", required=true) String id_user_cod,
            Model model
    ) {
        
        log.log(Level.INFO, "Ejecutando getTesChera de {0}", TesChequeraController.class.getName());
        HashMap<String,ArrayList<HashMap<String, String>>> jsonretorno = new HashMap<String,ArrayList<HashMap<String, String>>>();
        ArrayList<HashMap<String, String>> datoschequera = new ArrayList<HashMap<String, String>>();
        ArrayList<HashMap<String, String>> Bancos = new ArrayList<HashMap<String, String>>(); //hasmap para la vista
        ArrayList<HashMap<String, String>> monedas = new ArrayList<HashMap<String, String>>(); //hasmap para la vista
        ArrayList<HashMap<String, String>> paises = new ArrayList<HashMap<String, String>>();
        ArrayList<HashMap<String, String>> entidades = new ArrayList<HashMap<String, String>>();
        ArrayList<HashMap<String, String>> municipios = new ArrayList<HashMap<String, String>>();
        ArrayList<HashMap<String, String>> contab = new ArrayList<HashMap<String, String>>();
        ArrayList<HashMap<String, String>> cuentasMayor = new ArrayList<HashMap<String, String>>();
        ArrayList<HashMap<String, String>> arrayExtra = new ArrayList<HashMap<String, String>>();
        HashMap<String, String> extra = new HashMap<String, String>();
        HashMap<String, String> userDat = new HashMap<String, String>();
        
        //Decodificar id de usuario
        Integer id_usuario = Integer.parseInt(Base64Coder.decodeString(id_user_cod));
        userDat = this.getHomeDao().getUserById(id_usuario);
        
        Integer id_empresa = Integer.parseInt(userDat.get("empresa_id"));
        //Esta variable indica si la empresa incluye modulo de Contabilidad
        extra.put("inc_ctb", userDat.get("incluye_contab"));
        extra.put("nivel_cta", userDat.get("nivel_cta"));
        arrayExtra.add(0,extra);
        
        if( id != 0  ){
            datoschequera = this.getTesDao().getTesChequera_Datos(id);
            entidades = this.getTesDao().getEntidadesForThisPais(datoschequera.get(0).get("pais_id").toString());
            municipios = this.getTesDao().getMunicipiosForThisEntidad(datoschequera.get(0).get("pais_id").toString(), datoschequera.get(0).get("estado_id").toString());
            
            if(userDat.get("incluye_contab").equals("true")){
                contab = this.getTesDao().getTesChequera_DatosContabilidad(id);
            }
        }
        
        Bancos = this.getTesDao().getBancos(id_empresa);
        monedas = this.getTesDao().getMonedas();
        paises = this.getTesDao().getPaises();
        
        if(userDat.get("incluye_contab").equals("true")){
            cuentasMayor = this.getTesDao().getTesChequera_CuentasMayor(id_empresa);
        }
        
        
        jsonretorno.put("Chequera", datoschequera);
        jsonretorno.put("Bancos", Bancos);
        jsonretorno.put("Monedas", monedas);
        jsonretorno.put("Paises", paises);
        jsonretorno.put("Entidades", entidades);
        jsonretorno.put("Municipios", municipios);
        
        jsonretorno.put("Extras", arrayExtra);
        jsonretorno.put("CtaMay", cuentasMayor);
        jsonretorno.put("Contab", contab);
        
        return jsonretorno;
    }
    
    //obtiene el las entidades de un pais
    @RequestMapping(method = RequestMethod.POST, value="/getEntidades.json")
    public @ResponseBody HashMap<String,ArrayList<HashMap<String, String>>> getEntidadesJson(
            @RequestParam(value="id_pais", required=true) String id_pais,
            Model model
            ) {
        
        HashMap<String,ArrayList<HashMap<String, String>>> jsonretorno = new HashMap<String,ArrayList<HashMap<String, String>>>();
        
        jsonretorno.put("Entidades", this.getTesDao().getEntidadesForThisPais(id_pais));
        
        return jsonretorno;
    }
    
    
    //obtiene el las localidades de una entidad
    @RequestMapping(method = RequestMethod.POST, value="/getmunicipios.json")
    public @ResponseBody HashMap<String,ArrayList<HashMap<String, String>>> getLocalidadesJson(
            @RequestParam(value="id_pais", required=true) String id_pais,
            @RequestParam(value="id_entidad", required=true) String id_entidad,
            Model model
            ) {
        
        HashMap<String,ArrayList<HashMap<String, String>>> jsonretorno = new HashMap<String,ArrayList<HashMap<String, String>>>();
        
        jsonretorno.put("Municipios", this.getTesDao().getMunicipiosForThisEntidad(id_pais, id_entidad));
        
        return jsonretorno;
    }
    
    
    //metodo para el Buscador de Cuentas Contables
    @RequestMapping(method = RequestMethod.POST, value="/getBuscadorCuentasContables.json")
    public @ResponseBody HashMap<String,ArrayList<HashMap<String, String>>> getBuscadorCuentasContablesJson(
            @RequestParam(value="cta_mayor", required=true) Integer cta_mayor,
            @RequestParam(value="detalle", required=true) Integer detalle,
            @RequestParam(value="clasifica", required=false) String clasifica,
            @RequestParam(value="cta", required=false) String cta,
            @RequestParam(value="scta", required=false) String scta,
            @RequestParam(value="sscta", required=false) String sscta,
            @RequestParam(value="ssscta", required=false) String ssscta,
            @RequestParam(value="sssscta", required=false) String sssscta,
            @RequestParam(value="descripcion", required=false) String descripcion,
            @RequestParam(value="iu", required=true) String id_user,
            Model model
            ) {
        
        log.log(Level.INFO, "Ejecutando getBuscadorCuentasContablesJson de {0}", ClientsController.class.getName());
        HashMap<String,ArrayList<HashMap<String, String>>> jsonretorno = new HashMap<String,ArrayList<HashMap<String, String>>>();
        HashMap<String, String> userDat = new HashMap<String, String>();
        ArrayList<HashMap<String, String>> cuentasContables = new ArrayList<HashMap<String, String>>();
        
        //decodificar id de usuario
        Integer id_usuario = Integer.parseInt(Base64Coder.decodeString(id_user));
        userDat = this.getHomeDao().getUserById(id_usuario);
        Integer id_empresa = Integer.parseInt(userDat.get("empresa_id"));
        
        System.out.println("cta_mayor: "+cta_mayor);
        
        cuentasContables = this.getTesDao().getTesChequera_CuentasContables(cta_mayor, detalle, clasifica, cta, scta, sscta, ssscta, sssscta, descripcion, id_empresa);
        
        jsonretorno.put("CtaContables", cuentasContables);
        
        return jsonretorno;
    }
    
    
    
    
    
    //crear y editar
    @RequestMapping(method = RequestMethod.POST, value="/edit.json")
    public @ResponseBody HashMap<String, String> editJson(
            @RequestParam(value="identificador", required=true) String id,
            @RequestParam(value="chequera", required=false) String chequera,
            @RequestParam(value="moneda", required=false) String id_moneda,
            @RequestParam(value="check_modificar_consecutivo", required=false) String  chk_modificar_consecutivo,
            @RequestParam(value="check_modificar_fecha", required=false) String        chk_modificar_fecha,
            @RequestParam(value="check_modificar_cheque", required=false) String       chk_modificar_cheque,
            @RequestParam(value="check_imprimir_chequeningles", required=false) String chk_imprimir_chequeningles,
            
            @RequestParam(value="select_banco", required=false) String id_banco,
            @RequestParam(value="numero_sucursal", required=false) String numero_sucursal,
            @RequestParam(value="nombre_sucursal", required=false) String nombre_sucursal,
            @RequestParam(value="calle", required=false) String calle,
            @RequestParam(value="numero", required=false) String numero,
            @RequestParam(value="colonia", required=false) String colonia,
            @RequestParam(value="cp", required=false) String cp,
            @RequestParam(value="pais", required=false) String id_pais,
            @RequestParam(value="estado", required=false) String id_estado,
            @RequestParam(value="municipio", required=false) String id_municipio,
            
            @RequestParam(value="tel1", required=false) String telefono1,
            @RequestParam(value="ext1", required=false) String extencion1,
            @RequestParam(value="tel2", required=false) String telefono2,
            @RequestParam(value="ext2", required=false) String extencion2,
            @RequestParam(value="fax", required=false) String fax,
            @RequestParam(value="gerente", required=false) String gerente,
            @RequestParam(value="ejecutivo", required=false) String ejecutivo,
            @RequestParam(value="email", required=false) String email,
            
            @RequestParam(value="id_cta_activo", required=true) String id_cta_activo,

            Model model,@ModelAttribute("user") UserSessionData user
            ) {

        HashMap<String, String> jsonretorno = new HashMap<String, String>();
        HashMap<String, String> succes = new HashMap<String, String>();
        Integer app_selected = 59;
        String command_selected = "new";
        Integer id_usuario= user.getUserId();//variable para el id  del usuario
        String extra_data_array = "'sin datos'";
        String actualizo = "0";
        
        
        if( id.equals("0") ){
            command_selected = "new";
        }else{
            command_selected = "edit";
        }
        ////titulo character varying,                                   chequera
        //aut_modif_consecutivo boolean,                                chk_modificar_consecutivo
        //aut_modif_fecha boolean,                                      chk_modificar_fecha
        //aut_modif_cheque boolean,                                     chk_modificar_cheque
        //gral_pais_id integer,                                         id_pais
        //gral_mun_id integer,                                          id_municipio
        //gral_edo_id integer,                                          id_estado
        //moneda_id integer,                                            id_moneda
        //tes_ban_id integer,                                           id_banco
        //imp_cheque_ingles boolean NOT NULL DEFAULT false,             chk_imprimir_chequeningles
        //calle character varying,                                      calle
        //numero character varying,                                     numero
        //colonia character varying,                                    colonia
        //codigo_postal                                                 cp
        //clabe character varying,                                          
        //num_contrato character varying,
        //num_sucursal integer,                                         numero_sucursal
        //nombre_sucursal character varying,                            nombre_sucursal
        //telefono1                                                     telefono1
        //extencion1                                                    extencion1
        //telefono2                                                     telefono2
        //extencion2                                                    extencion2
        //fax                                                           fax
        //gerente                                                       gerente
        //ejecutivo                                                     ejecutivo
        //email                                                         email
        //momento_creacion timestamp with time zone,                    now()
        // momento_actualizacion timestamp with time zone,              now()
        //momento_baja timestamp with time zone,                        now()
        //borrado_logico boolean,                                       false
        //gral_usr_id_creacion integer DEFAULT 0,
        //gral_usr_id_actualizacion integer DEFAULT 0,
        //gral_usr_id_baja integer DEFAULT 0,
        //gral_emp_id integer,
        //gral_suc_id integer,
        // id_aux integer,
        //                      1                   2                       3            4               5                      6                       
        //String data_string = app_selected+"___"+command_selected+"___"+id_usuario+"___"+id+"___"+titulo.toUpperCase()+"___"+descripcion.toUpperCase();
        //                      1                   2                       3               4                      5                           6                           7                    8             9                     10          11               12                     13                        14         15              16       17         18                      19                       20        21                 22             23             24          25              26          27                              
        
        if(chk_modificar_consecutivo == null || !chk_modificar_consecutivo.equals("on")){
            chk_modificar_consecutivo = "false";
        }else{
            chk_modificar_consecutivo = "true";
        }
        
        if(chk_modificar_fecha == null || !chk_modificar_fecha.equals("on")){
            chk_modificar_fecha = "false";
        }else{
            chk_modificar_fecha = "true";
        }
        
        if(chk_modificar_cheque == null || !chk_modificar_cheque.equals("on")){
            chk_modificar_cheque = "false";
        }else{
            chk_modificar_cheque = "true";
        }
        
        if(chk_imprimir_chequeningles == null || !chk_imprimir_chequeningles.equals("on")){
            chk_imprimir_chequeningles = "false";
        }else{
            chk_imprimir_chequeningles = "true";
        }
        
        
        String data_string = app_selected+"___"+command_selected+"___"+id_usuario+"___"+id+"___"+chequera+"___"+chk_modificar_consecutivo +"___"+chk_modificar_fecha+"___"+chk_modificar_cheque+"___"+id_pais+"___"+id_municipio+"___"+id_estado+"___"+id_moneda+"___"+id_banco +"___"+chk_imprimir_chequeningles+"___"+calle.toUpperCase()+"___"+numero+"___"+colonia.toUpperCase()+"___"+cp+"___"+numero_sucursal+"___"+nombre_sucursal.toUpperCase()+"___"+telefono1+"___"+extencion1+"___"+telefono2+"___"+extencion2+"___"+fax+"___"+gerente.toUpperCase()+"___"+ejecutivo.toUpperCase()+"___"+email+"___"+id_cta_activo;
        
        
        succes = this.getTesDao().selectFunctionValidateAaplicativo(data_string,app_selected,extra_data_array);
        log.log(Level.INFO, "despues de validacion {0}", String.valueOf(succes.get("success")));
        if( String.valueOf(succes.get("success")).equals("true") ){
            actualizo = this.getTesDao().selectFunctionForThisApp(data_string, extra_data_array);
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
        
        Integer app_selected =59;
        String command_selected = "delete";
        String extra_data_array = "'sin datos'";
        String data_string = app_selected+"___"+command_selected+"___"+id_usuario+"___"+id;
        
        System.out.println("Ejecutando borrado logico de chequera");
        jsonretorno.put("success",String.valueOf( this.getTesDao().selectFunctionForThisApp(data_string,extra_data_array)) );
        
        return jsonretorno;
    }
    
}
