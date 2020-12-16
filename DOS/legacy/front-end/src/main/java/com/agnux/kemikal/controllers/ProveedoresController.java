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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;



/**
 *
 * @author pianodaemon
 */
@Controller
@SessionAttributes({"user"})
@RequestMapping("/proveedores/")
public class ProveedoresController {
    
    private static final Logger log  = Logger.getLogger(ProveedoresController.class.getName());
    ResourceProject resource = new ResourceProject();
    
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
    
    @RequestMapping(value="/startup.agnux")
    public ModelAndView startUp(HttpServletRequest request, HttpServletResponse response, 
                                @ModelAttribute("user") UserSessionData user)
                                throws ServletException, IOException {
        
        log.log(Level.INFO, "Ejecutando starUp de {0}", ProveedoresController.class.getName());
        LinkedHashMap<String,String> infoConstruccionTabla = new LinkedHashMap<String,String>();
        
        infoConstruccionTabla.put("id", "Acciones:90");
        infoConstruccionTabla.put("folio", "No. Proveedor:100");
        infoConstruccionTabla.put("razon_social", "Nombre Proveedor:250");
        infoConstruccionTabla.put("correo_electronico", "E-mail:150");
        infoConstruccionTabla.put("rfc", "RFC:120");
        infoConstruccionTabla.put("titulo","Contacto:250");
        
        ModelAndView x = new ModelAndView("proveedores/startup", "title", "Cat&aacute;logo de Proveedores");
        
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
    
    
    
    
    
    @RequestMapping(value="/getProveedores.json", method = RequestMethod.POST)
    public @ResponseBody HashMap<String,ArrayList<HashMap<String, Object>>> getPproveedoresJson(
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
        
        //catalogo de proveedores
        Integer app_selected = 2;
        
        //decodificar id de usuario
        Integer id_usuario = Integer.parseInt(Base64Coder.decodeString(id_user_cod));
        
        //variables para el buscador
        String busqueda_cadena = "%"+StringHelper.isNullString(String.valueOf(has_busqueda.get("cadena_busqueda")))+"%";
        String por_rfc = "%"+StringHelper.isNullString(String.valueOf(has_busqueda.get("por_rfc")))+"%";
        String folio = "%"+StringHelper.isNullString(String.valueOf(has_busqueda.get("folio")))+"%";
        
        String data_string = app_selected+"___"+id_usuario+"___"+busqueda_cadena+"___"+por_rfc+"___"+folio;
        
        //obtiene total de registros en base de datos, con los parametros de busqueda
        int total_items = this.getCxpDao().countAll(data_string);
        
        //calcula el total de paginas
        int total_pags = resource.calculaTotalPag(total_items,items_por_pag);
        
        //variables que necesita el datagrid, para no tener que hacer uno por cada aplicativo
        DataPost dataforpos = new DataPost(orderby, desc, items_por_pag, pag_start, display_pag, input_json, cadena_busqueda,total_items,total_pags, id_user_cod);
        
        int offset = resource.__get_inicio_offset(items_por_pag, pag_start);
        
        //obtiene los registros para el grid, de acuerdo a los parametros de busqueda
        jsonretorno.put("Data", this.getCxpDao().getProveedor_PaginaGrid(data_string, offset, items_por_pag, orderby, desc));
        
        //obtiene el hash para los datos que necesita el datagrid
        jsonretorno.put("DataForGrid", dataforpos.formaHashForPos(dataforpos));
        
        return jsonretorno;
    }
    
    
    
    
    
    @RequestMapping(method = RequestMethod.POST, value="/getProveedor.json")
    //public @ResponseBody HashMap<java.lang.String,java.lang.Object> getProveedorJson(
    public @ResponseBody HashMap<String,ArrayList<HashMap<String, String>>> getProveedorJson(
            @RequestParam(value="id", required=true) Integer id,
            @RequestParam(value="iu", required=true) String id_user_cod,
            Model model
            ) {
        
        log.log(Level.INFO, "Ejecutando getProveedorJson de {0}", ProveedoresController.class.getName());
        HashMap<String,ArrayList<HashMap<String, String>>> jsonretorno = new HashMap<String,ArrayList<HashMap<String, String>>>();
        HashMap<String, String> userDat = new HashMap<String, String>();
        ArrayList<HashMap<String, String>> proveedor = new ArrayList<HashMap<String, String>>();
        ArrayList<HashMap<String, String>> contactos = new ArrayList<HashMap<String, String>>();
        ArrayList<HashMap<String, String>> paises = new ArrayList<HashMap<String, String>>();
        ArrayList<HashMap<String, String>> entidades = new ArrayList<HashMap<String, String>>();
        ArrayList<HashMap<String, String>> municipios = new ArrayList<HashMap<String, String>>();
        ArrayList<HashMap<String, String>> tipo_proveedor = new ArrayList<HashMap<String, String>>();
        ArrayList<HashMap<String, String>> monedas = new ArrayList<HashMap<String, String>>();
        ArrayList<HashMap<String, String>> t_entrega = new ArrayList<HashMap<String, String>>();
        ArrayList<HashMap<String, String>> zonas = new ArrayList<HashMap<String, String>>();
        ArrayList<HashMap<String, String>> grupos = new ArrayList<HashMap<String, String>>();
        ArrayList<HashMap<String, String>> clasificacion1 = new ArrayList<HashMap<String, String>>();
        ArrayList<HashMap<String, String>> clasificacion2 = new ArrayList<HashMap<String, String>>();
        ArrayList<HashMap<String, String>> clasificacion3 = new ArrayList<HashMap<String, String>>();
        ArrayList<HashMap<String, String>> diasCredito = new ArrayList<HashMap<String, String>>();
        ArrayList<HashMap<String, String>> iniciosCredito = new ArrayList<HashMap<String, String>>();
        ArrayList<HashMap<String, String>> tiposEmbarque = new ArrayList<HashMap<String, String>>();
        ArrayList<HashMap<String, String>> impuestos = new ArrayList<HashMap<String, String>>();
        
        ArrayList<HashMap<String, String>> entidades_contacto_ventas = new ArrayList<HashMap<String, String>>();
        ArrayList<HashMap<String, String>> municipios_contacto_ventas = new ArrayList<HashMap<String, String>>();
        
        ArrayList<HashMap<String, String>> entidades_contacto_cobranza = new ArrayList<HashMap<String, String>>();
        ArrayList<HashMap<String, String>> municipios_contacto_cobranza = new ArrayList<HashMap<String, String>>();
        ArrayList<HashMap<String, String>> cuentasMayor = new ArrayList<HashMap<String, String>>();
        ArrayList<HashMap<String, String>> contab = new ArrayList<HashMap<String, String>>();
        ArrayList<HashMap<String, String>> arrayExtra = new ArrayList<HashMap<String, String>>();
        HashMap<String, String> extra = new HashMap<String, String>();
        
        //decodificar id de usuario
        Integer id_usuario = Integer.parseInt(Base64Coder.decodeString(id_user_cod));
        userDat = this.getHomeDao().getUserById(id_usuario);
        
        Integer id_empresa = Integer.parseInt(userDat.get("empresa_id"));
        extra.put("incluye_contab", userDat.get("incluye_contab"));//esta variable indica si la empresa incluye modulo de Contabilidad
        extra.put("nivel_cta", userDat.get("nivel_cta"));
        arrayExtra.add(0,extra);
        
        if(id != 0){
            proveedor = this.getCxpDao().getProveedor_Datos(id);
            contactos = this.getCxpDao().getProveedor_Contactos(id);            
            entidades = this.getCxpDao().getEntidadesForThisPais(proveedor.get(0).get("pais_id").toString());
            municipios = this.getCxpDao().getLocalidadesForThisEntidad(proveedor.get(0).get("pais_id").toString(), proveedor.get(0).get("estado_id").toString());
            
            municipios_contacto_ventas = this.getCxpDao().getLocalidadesForThisEntidad(proveedor.get(0).get("vent_pais_id").toString(), proveedor.get(0).get("vent_estado_id").toString());
            municipios_contacto_cobranza = this.getCxpDao().getLocalidadesForThisEntidad(proveedor.get(0).get("cob_pais_id").toString(), proveedor.get(0).get("cob_estado_id").toString());
            
            if(userDat.get("incluye_contab").equals("true")){
                contab = this.getCxpDao().getProveedor_DatosContabilidad(id);
            }
        }
        
        paises = this.getCxpDao().getPaises();
        tipo_proveedor = this.getCxpDao().getProveedor_Tipos();
        monedas = this.getCxpDao().getMonedas();
        t_entrega  = this.getCxpDao().getProveedor_TiemposEntrega();
        zonas = this.getCxpDao().getProveedor_Zonas();
        grupos = this.getCxpDao().getProveedor_Grupos();
        clasificacion1 = this.getCxpDao().getProveedor_Clasificacion1(id_empresa);
        clasificacion2 = this.getCxpDao().getProveedor_Clasificacion2(id_empresa);
        clasificacion3 = this.getCxpDao().getProveedor_Clasificacion3(id_empresa);
        diasCredito = this.getCxpDao().getProvFacturas_DiasCredito();
        iniciosCredito = this.getCxpDao().getProveedor_InicioCredito();
        tiposEmbarque = this.getCxpDao().getProveedor_TiposEmbarque();
        impuestos = this.getCxpDao().getImpuestos();
        cuentasMayor = this.getCxpDao().getProveedor_CuentasMayor(id_empresa);
        
        jsonretorno.put("Proveedor", proveedor);
        jsonretorno.put("contactos", contactos);
        jsonretorno.put("pais", paises);
        jsonretorno.put("entidades", entidades);
        jsonretorno.put("municipios", municipios);
        jsonretorno.put("proveedor_tipo", tipo_proveedor);
        jsonretorno.put("monedas", monedas);
        jsonretorno.put("te", t_entrega);
        jsonretorno.put("Zonas", zonas);
        jsonretorno.put("Grupos", grupos);
        jsonretorno.put("Clas1", clasificacion1);
        jsonretorno.put("Clas2", clasificacion2);
        jsonretorno.put("Clas3", clasificacion3);
        jsonretorno.put("DiasCredito", diasCredito);
        jsonretorno.put("InicioCredito", iniciosCredito);
        jsonretorno.put("TiposEmbarque", tiposEmbarque);
        jsonretorno.put("ventmunicipios", municipios_contacto_ventas);
        jsonretorno.put("cobmunicipios", municipios_contacto_cobranza);
        jsonretorno.put("Impuestos", impuestos);
        
        jsonretorno.put("Extras", arrayExtra);
        jsonretorno.put("CtaMay", cuentasMayor);
        jsonretorno.put("Contab", contab);
        
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
        
        jsonretorno.put("localidades", this.getCxpDao().getLocalidadesForThisEntidad(id_pais, id_entidad));
        
        return jsonretorno;
    }
    
    //obtiene el las entidades de un pais
    @RequestMapping(method = RequestMethod.POST, value="/getEntidades.json")
    public @ResponseBody HashMap<String,ArrayList<HashMap<String, String>>> getEntidadesJson(
            @RequestParam(value="id_pais", required=true) String id_pais,
            Model model
            ) {
        
        HashMap<String,ArrayList<HashMap<String, String>>> jsonretorno = new HashMap<String,ArrayList<HashMap<String, String>>>();
        
        jsonretorno.put("entidades", this.getCxpDao().getEntidadesForThisPais(id_pais));
        
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
        
        log.log(Level.INFO, "Ejecutando getBuscadorCuentasContablesJson de {0}", ProveedoresController.class.getName());
        HashMap<String,ArrayList<HashMap<String, String>>> jsonretorno = new HashMap<String,ArrayList<HashMap<String, String>>>();
        HashMap<String, String> userDat = new HashMap<String, String>();
        ArrayList<HashMap<String, String>> cuentasContables = new ArrayList<HashMap<String, String>>();
        
        //decodificar id de usuario
        Integer id_usuario = Integer.parseInt(Base64Coder.decodeString(id_user));
        userDat = this.getHomeDao().getUserById(id_usuario);
        Integer id_empresa = Integer.parseInt(userDat.get("empresa_id"));
        
        cuentasContables = this.getCxpDao().getProveedor_CuentasContables(cta_mayor, detalle, clasifica, cta, scta, sscta, ssscta, sssscta, descripcion, id_empresa);
        
        jsonretorno.put("CtaContables", cuentasContables);
        
        return jsonretorno;
    }
    
    
    
    //edicion y nuevo
    @RequestMapping(method = RequestMethod.POST, value="/edit.json")
    public @ResponseBody HashMap<String, String> editJson(
            @RequestParam(value="id_proveedor", required=true) String id_proveedor,
            @RequestParam(value="folio", required=true) String folio,
            @RequestParam(value="rfc", required=true) String rfc,
            @RequestParam(value="curp", required=true) String curp,
            @RequestParam(value="raz_social", required=true) String raz_social,
            @RequestParam(value="nombre_comercial", required=true) String nombre_comercial,
            @RequestParam(value="calle", required=true) String calle,
            @RequestParam(value="num_calle", required=true) String num_calle,
            @RequestParam(value="colonia", required=true) String colonia,
            @RequestParam(value="cp", required=true) String cp,
            @RequestParam(value="entrecalles", required=true) String entrecalles,
            @RequestParam(value="pais", required=true) String pais,
            @RequestParam(value="estado", required=true) String estado,
            @RequestParam(value="localidad", required=true) String localidad,
            @RequestParam(value="loc_alternativa", required=true) String loc_alternativa,
            @RequestParam(value="tel1", required=true) String tel1,
            @RequestParam(value="ext1", required=true) String ext1,
            @RequestParam(value="fax", required=true) String fax,
            @RequestParam(value="tel2", required=true) String tel2,
            @RequestParam(value="ext2", required=true) String ext2,
            @RequestParam(value="email", required=true) String email,
            @RequestParam(value="pag_web", required=true) String pag_web,
            @RequestParam(value="impuesto", required=true) String impuesto,
            @RequestParam(value="zona", required=true) String zona,
            @RequestParam(value="grupo", required=true) String grupo,
            @RequestParam(value="prov_tipo", required=true) String prov_tipo,
            @RequestParam(value="clasif1", required=true) String clasif1,
            @RequestParam(value="clasif2", required=true) String clasif2,
            @RequestParam(value="clasif3", required=true) String clasif3,
            @RequestParam(value="moneda", required=true) String moneda,
            @RequestParam(value="tentrega", required=true) String tentrega,
            @RequestParam(value="estatus", required=true) String estatus,
            @RequestParam(value="check_transportista", required=false) String check_transportista,
            @RequestParam(value="limite_credito", required=true) String limite_credito,
            @RequestParam(value="credito", required=true) String dias_credito,
            @RequestParam(value="descuento", required=true) String descuento,
            @RequestParam(value="inicio_credito", required=true) String inicio_credito,
            @RequestParam(value="tipo_embarque", required=true) String tipo_embarque,
            @RequestParam(value="flete", required=true) String flete,
            @RequestParam(value="condiciones", required=true) String condiciones,
            @RequestParam(value="observaciones", required=true) String observaciones,
            @RequestParam(value="vent_contacto", required=true) String vent_contacto,
            @RequestParam(value="vent_puesto", required=true) String vent_puesto,
            @RequestParam(value="vent_calle", required=true) String vent_calle,
            @RequestParam(value="vent_numcalle", required=true) String vent_numcalle,
            @RequestParam(value="vent_colonia", required=true) String vent_colonia,
            @RequestParam(value="vent_cp", required=true) String vent_cp,
            @RequestParam(value="vent_entrecalles", required=true) String vent_entrecalles,
            @RequestParam(value="vent_pais", required=true) String vent_pais,
            @RequestParam(value="vent_estado", required=true) String vent_estado,
            @RequestParam(value="vent_localidad", required=true) String vent_localidad,
            @RequestParam(value="vent_tel1", required=true) String vent_tel1,
            @RequestParam(value="vent_ext1", required=true) String vent_ext1,
            @RequestParam(value="vent_fax", required=true) String vent_fax,
            @RequestParam(value="vent_tel2", required=true) String vent_tel2,
            @RequestParam(value="vent_ext2", required=true) String vent_ext2,
            @RequestParam(value="vent_email", required=true) String vent_email,
            @RequestParam(value="cob_contacto", required=true) String cob_contacto,
            @RequestParam(value="cob_puesto", required=true) String cob_puesto,
            @RequestParam(value="cob_calle", required=true) String cob_calle,
            @RequestParam(value="cob_numcalle", required=true) String cob_numcalle,
            @RequestParam(value="cob_colonia", required=true) String cob_colonia,
            @RequestParam(value="cob_cp", required=true) String cob_cp,
            @RequestParam(value="cob_entrecalles", required=true) String cob_entrecalles,
            @RequestParam(value="cob_pais", required=true) String cob_pais,
            @RequestParam(value="cob_estado", required=true) String cob_estado,
            @RequestParam(value="cob_localidad", required=true) String cob_localidad,
            @RequestParam(value="cob_tel1", required=true) String cob_tel1,
            @RequestParam(value="cob_ext1", required=true) String cob_ext1,
            @RequestParam(value="cob_fax", required=true) String cob_fax,
            @RequestParam(value="cob_tel2", required=true) String cob_tel2,
            @RequestParam(value="cob_ext2", required=true) String cob_ext2,
            @RequestParam(value="cob_email", required=true) String cob_email,
            @RequestParam(value="comentarios", required=true) String comentarios,
            
            @RequestParam(value="id_cta_pasivo", required=true) String id_cta_pasivo,
            @RequestParam(value="id_cta_egreso", required=true) String id_cta_egreso,
            @RequestParam(value="id_cta_ietu", required=true) String id_cta_ietu,
            @RequestParam(value="id_cta_complement", required=true) String id_cta_complement,
            @RequestParam(value="id_cta_pasivo_complement", required=true) String id_cta_pasivo_complement,
            
            
            Model model,@ModelAttribute("user") UserSessionData user
        ) {
            
            HashMap<String, String> jsonretorno = new HashMap<String, String>();
            HashMap<String, String> succes = new HashMap<String, String>();
            String rfc2="";
            String actualizo = "0";
            String extra_data_array ="'sin datos'";
            Integer app_selected = 2;
            String command_selected = "new";
            Integer id_usuario= user.getUserId();//variable para el id  del usuario
            
            
            if( id_proveedor.equals("0") ){
                command_selected = "new";
            }else{
                command_selected = "edit";
            }
            
            if(prov_tipo.equals("1")){
                rfc2 = rfc;
            }
            
            if(prov_tipo.equals("2")){
                rfc2 = "XEXX010101000";
            }
            
            check_transportista = StringHelper.verificarCheckBox(check_transportista);
            
            String data_string = 
            app_selected
            +"___"+command_selected
            +"___"+id_usuario
            +"___"+id_proveedor
            +"___"+folio
            +"___"+rfc.toUpperCase()
            +"___"+curp.toUpperCase()
            +"___"+raz_social.toUpperCase()
            +"___"+nombre_comercial.toUpperCase()
            +"___"+calle.toUpperCase()
            +"___"+num_calle
            +"___"+colonia.toUpperCase()
            +"___"+cp
            +"___"+entrecalles.toUpperCase()
            +"___"+pais
            +"___"+estado
            +"___"+localidad
            +"___"+loc_alternativa.toUpperCase()
            +"___"+tel1
            +"___"+ext1
            +"___"+fax
            +"___"+tel2
            +"___"+ext2
            +"___"+email
            +"___"+pag_web
            +"___"+impuesto
            +"___"+zona
            +"___"+grupo
            +"___"+prov_tipo
            +"___"+clasif1
            +"___"+clasif2
            +"___"+clasif3
            +"___"+moneda
            +"___"+tentrega
            +"___"+estatus
            +"___"+limite_credito
            +"___"+dias_credito
            +"___"+descuento
            +"___"+inicio_credito
            +"___"+tipo_embarque
            +"___"+flete
            +"___"+condiciones.toUpperCase()
            +"___"+observaciones.toUpperCase()
            +"___"+vent_contacto.toUpperCase()
            +"___"+vent_puesto.toUpperCase()
            +"___"+vent_calle.toUpperCase()
            +"___"+vent_numcalle
            +"___"+vent_colonia.toUpperCase()
            +"___"+vent_cp
            +"___"+vent_entrecalles.toUpperCase()
            +"___"+vent_pais
            +"___"+vent_estado
            +"___"+vent_localidad
            +"___"+vent_tel1
            +"___"+vent_ext1
            +"___"+vent_fax
            +"___"+vent_tel2
            +"___"+vent_ext2
            +"___"+vent_email
            +"___"+cob_contacto.toUpperCase()
            +"___"+cob_puesto.toUpperCase()
            +"___"+cob_calle.toUpperCase()
            +"___"+cob_numcalle
            +"___"+cob_colonia.toUpperCase()
            +"___"+cob_cp
            +"___"+cob_entrecalles.toUpperCase()
            +"___"+cob_pais
            +"___"+cob_estado
            +"___"+cob_localidad
            +"___"+cob_tel1
            +"___"+cob_ext1
            +"___"+cob_fax
            +"___"+cob_tel2
            +"___"+cob_ext2
            +"___"+cob_email
            +"___"+comentarios.toUpperCase()
            +"___"+id_cta_pasivo
            +"___"+id_cta_egreso
            +"___"+id_cta_ietu
            +"___"+id_cta_complement
            +"___"+id_cta_pasivo_complement
            +"___"+check_transportista;
            
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
            @RequestParam(value="id", required=true) Integer id_proveedor,
            @RequestParam(value="iu", required=true) String id_user_cod,
            Model model
            ) {
        
        System.out.println("Borrado logico de proveedor");
        //decodificar id de usuario
        Integer id_usuario = Integer.parseInt(Base64Coder.decodeString(id_user_cod));
        
        Integer app_selected = 2;
        String command_selected = "delete";
        String extra_data_array = "'sin datos'";
        
        String data_string = app_selected+"___"+command_selected+"___"+id_usuario+"___"+id_proveedor;
        
        HashMap<String, String> jsonretorno = new HashMap<String, String>();
        
        jsonretorno.put("success",String.valueOf( this.getCxpDao().selectFunctionForThisApp(data_string,extra_data_array)) );
        
        return jsonretorno;
    }

    
    
}
