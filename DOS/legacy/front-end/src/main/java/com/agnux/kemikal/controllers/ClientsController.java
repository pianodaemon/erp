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



/**
 *
 * @author agnux
 */
@Controller
@SessionAttributes({"user"})
@RequestMapping("/clients/")
public class ClientsController {
    
    private static final Logger log  = Logger.getLogger(ClientsController.class.getName());
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
        
        log.log(Level.INFO, "Ejecutando starUp de {0}", ClientsController.class.getName());
        LinkedHashMap<String,String> infoConstruccionTabla = new LinkedHashMap<String,String>();
        
        infoConstruccionTabla.put("id", "Acciones:90");
        infoConstruccionTabla.put("numero_control", "N&uacute;mero&nbsp;control:100");
        infoConstruccionTabla.put("razon_social", "Razon&nbsp;social:250");
        infoConstruccionTabla.put("rfc", "RFC:100");
        infoConstruccionTabla.put("tipo_cliente", "Tipo&nbsp;cliente:100");
        infoConstruccionTabla.put("tel", "Tel&eacute;fono:100");
        
        ModelAndView x = new ModelAndView("clients/startup", "title", "Cat&aacute;logo de Clientes");
        
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
    
    
    
    
    @RequestMapping(value="/getClients.json", method = RequestMethod.POST)
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
        Integer app_selected = 5;
        
        //decodificar id de usuario
        Integer id_usuario = Integer.parseInt(Base64Coder.decodeString(id_user_cod));
        
        //variables para el buscador
        String nocontrol = ""+StringHelper.isNullString(String.valueOf(has_busqueda.get("nocontrol")))+"";
        String razonsoc = "%"+StringHelper.isNullString(String.valueOf(has_busqueda.get("razonsoc")))+"%";
        String rfc = "%"+StringHelper.isNullString(String.valueOf(has_busqueda.get("rfc")))+"%";
        
        String data_string = app_selected+"___"+id_usuario+"___"+nocontrol.toUpperCase()+"___"+razonsoc+"___"+rfc;
        
        //obtiene total de registros en base de datos, con los parametros de busqueda
        int total_items = this.getCxcDao().countAll(data_string);
        
        //calcula el total de paginas
        int total_pags = resource.calculaTotalPag(total_items,items_por_pag);
        
        //variables que necesita el datagrid, para no tener que hacer uno por cada aplicativo
        DataPost dataforpos = new DataPost(orderby, desc, items_por_pag, pag_start, display_pag, input_json, cadena_busqueda,total_items,total_pags, id_user_cod);
        
        int offset = resource.__get_inicio_offset(items_por_pag, pag_start);
        
        //obtiene los registros para el grid, de acuerdo a los parametros de busqueda
        jsonretorno.put("Data", this.getCxcDao().getClientes_PaginaGrid(data_string, offset, items_por_pag, orderby, desc));
        //obtiene el hash para los datos que necesita el datagrid
        jsonretorno.put("DataForGrid", dataforpos.formaHashForPos(dataforpos));
        
        return jsonretorno;
    }
    
    
    
    //obtiene lineas de producto y datos para el buscador
    @RequestMapping(method = RequestMethod.POST, value="/getInicio.json")
    public @ResponseBody HashMap<String,ArrayList<HashMap<String, String>>> getInicioJson(
            @RequestParam(value="iu", required=true) String id_user_cod,
            Model model
        ) {
        
        HashMap<String,ArrayList<HashMap<String, String>>> jsonretorno = new HashMap<String,ArrayList<HashMap<String, String>>>();
        ArrayList<HashMap<String, String>> arrayExtra = new ArrayList<HashMap<String, String>>();
        HashMap<String, String> extra = new HashMap<String, String>();
        
        //decodificar id de usuario
        Integer id_usuario = Integer.parseInt(Base64Coder.decodeString(id_user_cod));
        
        extra = this.getCxcDao().getUserRolAgenteVenta(id_usuario);
        arrayExtra.add(0,extra);
        
        jsonretorno.put("Extra", arrayExtra);
        return jsonretorno;
    }
    
    
    
    
    @RequestMapping(method = RequestMethod.POST, value="/get_cliente.json")
    public @ResponseBody HashMap<String,ArrayList<HashMap<String, Object>>> get_clienteJson(
            @RequestParam(value="id", required=true) Integer id,
            @RequestParam(value="iu", required=true) String id_user,
            Model model
        ) {
        
        log.log(Level.INFO, "Ejecutando get_clienteJson de {0}", ClientsController.class.getName());
        HashMap<String,ArrayList<HashMap<String, Object>>> jsonretorno = new HashMap<String,ArrayList<HashMap<String, Object>>>();
        HashMap<String, String> userDat = new HashMap<String, String>();
        ArrayList<HashMap<String, Object>> monedas = new ArrayList<HashMap<String, Object>>();
        ArrayList<HashMap<String, Object>> tiposclient = new ArrayList<HashMap<String, Object>>();
        ArrayList<HashMap<String, Object>> paises = new ArrayList<HashMap<String, Object>>();
        ArrayList<HashMap<String, Object>> entidades = new ArrayList<HashMap<String, Object>>();
        ArrayList<HashMap<String, Object>> localidades = new ArrayList<HashMap<String, Object>>();
        ArrayList<HashMap<String, Object>> datoscliente = new ArrayList<HashMap<String, Object>>();
        ArrayList<HashMap<String, Object>> direcciones_consignacion = new ArrayList<HashMap<String, Object>>();
        ArrayList<HashMap<String, Object>> vendedores = new ArrayList<HashMap<String, Object>>();
        ArrayList<HashMap<String, Object>> condiciones = new ArrayList<HashMap<String, Object>>();
        ArrayList<HashMap<String, Object>> zonas = new ArrayList<HashMap<String, Object>>();
        ArrayList<HashMap<String, Object>> grupos = new ArrayList<HashMap<String, Object>>();
        ArrayList<HashMap<String, Object>> clasificacion1 = new ArrayList<HashMap<String, Object>>();
        ArrayList<HashMap<String, Object>> clasificacion2 = new ArrayList<HashMap<String, Object>>();
        ArrayList<HashMap<String, Object>> clasificacion3 = new ArrayList<HashMap<String, Object>>();
        ArrayList<HashMap<String, Object>> impuestos = new ArrayList<HashMap<String, Object>>();
        ArrayList<HashMap<String, Object>> iniciosCredito = new ArrayList<HashMap<String, Object>>();
        ArrayList<HashMap<String, Object>> tiposEmbarque = new ArrayList<HashMap<String, Object>>();
        ArrayList<HashMap<String, Object>> municipios_contacto_compras = new ArrayList<HashMap<String, Object>>();
        ArrayList<HashMap<String, Object>> municipios_contacto_pagos = new ArrayList<HashMap<String, Object>>();
        ArrayList<HashMap<String, Object>> metodos_pago = new ArrayList<HashMap<String, Object>>();
        ArrayList<HashMap<String, Object>> cuentasMayor = new ArrayList<HashMap<String, Object>>();
        ArrayList<HashMap<String, Object>> contab = new ArrayList<HashMap<String, Object>>();
        ArrayList<HashMap<String, Object>> arrayExtra = new ArrayList<HashMap<String, Object>>();
        HashMap<String, Object> extra = new HashMap<String, Object>();
        
        //Decodificar id de usuario
        Integer id_usuario = Integer.parseInt(Base64Coder.decodeString(id_user));
        userDat = this.getHomeDao().getUserById(id_usuario);
        
        Integer id_empresa = Integer.parseInt(userDat.get("empresa_id"));
        
        //Esta variable indica si la empresa incluye modulo de Contabilidad
        extra.put("incluye_contab", userDat.get("incluye_contab"));
        extra.put("nivel_cta", userDat.get("nivel_cta"));
        arrayExtra.add(0,extra);
        
        if( id != 0  ){
            datoscliente = this.getCxcDao().getCliente_Datos(id);
            entidades = this.getCxcDao().getEntidadesForThisPais(datoscliente.get(0).get("pais_id").toString());
            localidades = this.getCxcDao().getLocalidadesForThisEntidad(datoscliente.get(0).get("pais_id").toString(), datoscliente.get(0).get("estado_id").toString());
            direcciones_consignacion = this.getCxcDao().getCliente_DirConsignacion(id);
            municipios_contacto_compras = this.getCxcDao().getLocalidadesForThisEntidad(datoscliente.get(0).get("contacto_compras_pais_id").toString(), datoscliente.get(0).get("contacto_compras_estado_id").toString());
            municipios_contacto_pagos = this.getCxcDao().getLocalidadesForThisEntidad(datoscliente.get(0).get("contacto_pagos_pais_id").toString(), datoscliente.get(0).get("contacto_pagos_estado_id").toString());
            
            if(userDat.get("incluye_contab").equals("true")){
                contab = this.getCxcDao().getCliente_DatosContabilidad(id);
            }
        }
        
        //valorIva= this.getCdao().getValoriva();
        monedas = this.getCxcDao().getMonedas();
        tiposclient = this.getCxcDao().getCliente_Tipos();
        paises = this.getCxcDao().getPaises();
        vendedores = this.getCxcDao().getCliente_Vendedores(id_empresa);
        condiciones = this.getCxcDao().getCliente_Condiciones();
        zonas = this.getCxcDao().getCliente_Zonas();
        grupos = this.getCxcDao().getCliente_Grupos();
        clasificacion1 = this.getCxcDao().getCliente_Clasificacion1();
        clasificacion2 = this.getCxcDao().getCliente_Clasificacion2();
        clasificacion3 = this.getCxcDao().getCliente_Clasificacion3();
        impuestos = this.getCxcDao().getImpuestos();
        iniciosCredito = this.getCxcDao().getCliente_InicioCredito();
        tiposEmbarque = this.getCxcDao().getCliente_TiposEmbarque();
        cuentasMayor = this.getCxcDao().getCliente_CuentasMayor(id_empresa);
        metodos_pago = this.getCxcDao().getMetodosPago(id_empresa);
        
        jsonretorno.put("Cliente", datoscliente);
        jsonretorno.put("Tiposclient", tiposclient);
        jsonretorno.put("Monedas", monedas);
        jsonretorno.put("Paises", paises);
        jsonretorno.put("Entidades", entidades);
        jsonretorno.put("Localidades", localidades);
        jsonretorno.put("Direcciones", direcciones_consignacion);
        jsonretorno.put("Vendedores", vendedores);
        jsonretorno.put("Condiciones", condiciones);
        jsonretorno.put("Zonas", zonas);
        jsonretorno.put("Grupos", grupos);
        jsonretorno.put("Clas1", clasificacion1);
        jsonretorno.put("Clas2", clasificacion2);
        jsonretorno.put("Clas3", clasificacion3);
        jsonretorno.put("Impuestos", impuestos);
        jsonretorno.put("InicioCredito", iniciosCredito);
        jsonretorno.put("TiposEmbarque", tiposEmbarque);
        jsonretorno.put("Comprasmunicipios", municipios_contacto_compras);
        jsonretorno.put("Pagosmunicipios", municipios_contacto_pagos);
        jsonretorno.put("MetodosPago", metodos_pago);
        jsonretorno.put("Extras", arrayExtra);
        jsonretorno.put("CtaMay", cuentasMayor);
        jsonretorno.put("Contab", contab);
        return jsonretorno;
    }
    
    
    
    
    
    
    
    //obtiene los datos para la forma de direcciones de consignacion
    @RequestMapping(method = RequestMethod.POST, value="/get_datos_forma_consignacion.json")
    public @ResponseBody HashMap<String,ArrayList<HashMap<String, Object>>> get_datos_forma_consignacionJson(
            @RequestParam(value="numFila", required=true) Integer numFila,
            @RequestParam(value="id_pais", required=true) String id_pais,
            @RequestParam(value="id_entidad", required=true) String id_entidad,
            Model model
            ) {
        
        log.log(Level.INFO, "Ejecutando get_datos_forma_consignacionJson de {0}", ClientsController.class.getName());
        HashMap<String,ArrayList<HashMap<String, Object>>> jsonretorno = new HashMap<String,ArrayList<HashMap<String, Object>>>();
        ArrayList<HashMap<String, Object>> paises = new ArrayList<HashMap<String, Object>>();
        ArrayList<HashMap<String, Object>> entidades = new ArrayList<HashMap<String, Object>>();
        ArrayList<HashMap<String, Object>> localidades = new ArrayList<HashMap<String, Object>>();
        
        
        if( numFila != 0  ){
            entidades = this.getCxcDao().getEntidadesForThisPais(id_pais);
            localidades = this.getCxcDao().getLocalidadesForThisEntidad(id_pais, id_entidad);
        }
        
        paises = this.getCxcDao().getPaises();
        
        jsonretorno.put("Paises", paises);
        jsonretorno.put("Entidades", entidades);
        jsonretorno.put("Localidades", localidades);
        
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
        
        jsonretorno.put("Localidades", this.getCxcDao().getLocalidadesForThisEntidad(id_pais, id_entidad));
        
        return jsonretorno;
    }
    
    
    
    //obtiene el las entidades de un pais
    @RequestMapping(method = RequestMethod.POST, value="/getEntidades.json")
    public @ResponseBody HashMap<String,ArrayList<HashMap<String, Object>>> getEntidadesJson(
            @RequestParam(value="id_pais", required=true) String id_pais,
            Model model
            ) {
        HashMap<String,ArrayList<HashMap<String, Object>>> jsonretorno = new HashMap<String,ArrayList<HashMap<String, Object>>>();
        jsonretorno.put("Entidades", this.getCxcDao().getEntidadesForThisPais(id_pais));
        
        return jsonretorno;
    }
    
    
    
    //validacion  de los campos del formulario de direcciones de consignacion
    @RequestMapping(method = RequestMethod.POST, value="/valida_direcciones_consignacion.json")
    public @ResponseBody HashMap<String, String> valida_direcciones_consignacionJson(
        @RequestParam(value="pais", required=true) String pais,
        @RequestParam(value="entidad", required=true) String entidad,
        @RequestParam(value="localidad", required=true) String localidad,
        @RequestParam(value="calle", required=true) String calle,
        @RequestParam(value="numero", required=true) String numero,
        @RequestParam(value="colonia", required=true) String colonia,
        @RequestParam(value="cp", required=true) String cp,
        @RequestParam(value="telefono", required=true) String telefono,
        @RequestParam(value="localternativa", required=true) String localternativa,
        @RequestParam(value="fax", required=true) String fax,
        Model model
        ) {
        
        String data_string = pais+"___"+entidad+"___"+localidad+"___"+calle+"___"+numero+"___"+colonia+"___"+cp+"___"+telefono+"___"+localternativa+"___"+fax;
        
        HashMap<String, String> jsonretorno = new HashMap<String, String>();
        
        HashMap<String, String> succes = new HashMap<String, String>();
        
        succes = this.getCxcDao().getCliente_ValidaDirConsignacion(data_string);
        
        jsonretorno.put("success",String.valueOf(succes.get("success")));
        
        log.log(Level.INFO, "Salida json {0}", String.valueOf(jsonretorno.get("success")));
        
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
        
        cuentasContables = this.getCxcDao().getCliente_CuentasContables(cta_mayor, detalle, clasifica, cta, scta, sscta, ssscta, sssscta, descripcion, id_empresa);
        
        jsonretorno.put("CtaContables", cuentasContables);
        
        return jsonretorno;
    }
    
    
    
    
    //crear y editar un cliente
    @RequestMapping(method = RequestMethod.POST, value="/edit.json")
    public @ResponseBody HashMap<String, String> editJson(
            @RequestParam(value="identificador_cliente", required=true) String id_cliente,
            @RequestParam(value="campo_consignacion", required=true) String campo_consignacion,
            @RequestParam(value="nocontrol", required=true) String nocontrol,
            @RequestParam(value="rfc", required=true) String rfc,
            @RequestParam(value="curp", required=true) String curp,
            @RequestParam(value="razonsocial", required=true) String razonsocial,
            @RequestParam(value="clave_comercial", required=true) String clave_comercial,
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
            @RequestParam(value="agente", required=true) String agente,
            @RequestParam(value="contacto", required=true) String contacto,
            @RequestParam(value="zona", required=true) String zona,
            @RequestParam(value="grupo", required=true) String grupo,
            @RequestParam(value="tipocliente", required=true) String tipocliente,
            @RequestParam(value="clasif1", required=true) String clasif1,
            @RequestParam(value="clasif2", required=true) String clasif2,
            @RequestParam(value="clasif3", required=true) String clasif3,
            @RequestParam(value="moneda", required=true) String moneda,
            @RequestParam(value="filial", required=true) String empresa_filial,
            @RequestParam(value="estatus", required=true) String estatus,
            @RequestParam(value="impuesto", required=true) String impuesto,
            @RequestParam(value="limite_credito", required=true) String limite_credito,
            @RequestParam(value="dias_credito", required=true) String dias_credito,
            @RequestParam(value="credito_suspendido", required=true) String credito_suspendido,
            @RequestParam(value="inicio_credito", required=true) String inicio_credito,
            @RequestParam(value="tipo_embarque", required=true) String tipo_embarque,
            @RequestParam(value="cad_cotizacion", required=true) String cad_cotizacion,
            @RequestParam(value="condiciones", required=true) String condiciones,
            @RequestParam(value="observaciones", required=true) String observaciones,
            @RequestParam(value="comp_contacto", required=true) String comp_contacto,
            @RequestParam(value="comp_puesto", required=true) String comp_puesto,
            @RequestParam(value="comp_calle", required=true) String comp_calle,
            @RequestParam(value="comp_numcalle", required=true) String comp_numcalle,
            @RequestParam(value="comp_colonia", required=true) String comp_colonia,
            @RequestParam(value="comp_cp", required=true) String comp_cp,
            @RequestParam(value="comp_entrecalles", required=true) String comp_entrecalles,
            @RequestParam(value="comp_pais", required=true) String comp_pais,
            @RequestParam(value="comp_estado", required=true) String comp_estado,
            @RequestParam(value="comp_municipio", required=true) String comp_municipio,
            @RequestParam(value="comp_tel1", required=true) String comp_tel1,
            @RequestParam(value="comp_ext1", required=true) String comp_ext1,
            @RequestParam(value="comp_fax", required=true) String comp_fax,
            @RequestParam(value="comp_tel2", required=true) String comp_tel2,
            @RequestParam(value="comp_ext2", required=true) String comp_ext2,
            @RequestParam(value="comp_email", required=true) String comp_email,
            @RequestParam(value="pag_contacto", required=true) String pag_contacto,
            @RequestParam(value="pag_puesto", required=true) String pag_puesto,
            @RequestParam(value="pag_calle", required=true) String pag_calle,
            @RequestParam(value="pag_numcalle", required=true) String pag_numcalle,
            @RequestParam(value="pag_colonia", required=true) String pag_colonia,
            @RequestParam(value="pag_cp", required=true) String pag_cp,
            @RequestParam(value="pag_entrecalles", required=true) String pag_entrecalles,
            @RequestParam(value="pag_pais", required=true) String pag_pais,
            @RequestParam(value="pag_estado", required=true) String pag_estado,
            @RequestParam(value="pag_municipio", required=true) String pag_municipio,
            @RequestParam(value="pag_tel1", required=true) String pag_tel1,
            @RequestParam(value="pag_ext1", required=true) String pag_ext1,
            @RequestParam(value="pag_fax", required=true) String pag_fax,
            @RequestParam(value="pag_tel2", required=true) String pag_tel2,
            @RequestParam(value="pag_ext2", required=true) String pag_ext2,
            @RequestParam(value="pag_email", required=true) String pag_email,
            @RequestParam(value="total_tr", required=true) String total_tr,
            @RequestParam(value="dc_eliminado", required=false) String[] dc_eliminado,
            @RequestParam(value="dc_calle", required=false) String[] dc_calle,
            @RequestParam(value="dc_numero", required=false) String[] dc_numero,
            @RequestParam(value="dc_colonia", required=false) String[] dc_colonia,
            @RequestParam(value="dc_idpais", required=false) String [] dc_idpais,
            @RequestParam(value="dc_identidad", required=false) String[] dc_identidad,
            @RequestParam(value="dc_idlocalidad", required=false) String[] dc_idlocalidad,
            @RequestParam(value="dc_codigop", required=false) String[] dc_codigop,
            @RequestParam(value="dc_localternativa", required=false) String[] dc_localternativa,
            @RequestParam(value="dc_telefono", required=false) String[] dirc_telefono,
            @RequestParam(value="dc_numfax", required=false) String[] dc_numfax,
            @RequestParam(value="select_immex", required=true) String select_immex,
            @RequestParam(value="retencion_immex", required=false) String retencion_immex,
            @RequestParam(value="select_metodo_pago", required=true) String select_metodo_pago,
            @RequestParam(value="select_dia_revision", required=true) String select_dia_revision,
            @RequestParam(value="select_dia_pago", required=true) String select_dia_pago,
            @RequestParam(value="cuenta_mn", required=true) String cuenta_mn,
            @RequestParam(value="cuenta_usd", required=true) String cuenta_usd,
            @RequestParam(value="select_lista_precio", required=true) String select_lista_precio,
            @RequestParam(value="id_cta_activo", required=true) String id_cta_activo,
            @RequestParam(value="id_cta_ingreso", required=true) String id_cta_ingreso,
            @RequestParam(value="id_cta_ietu", required=true) String id_cta_ietu,
            @RequestParam(value="id_cta_complementaria", required=true) String id_cta_complementaria,
            @RequestParam(value="id_cta_activo_complementaria", required=true) String id_cta_activo_complementaria,
            Model model,@ModelAttribute("user") UserSessionData user
            ) {
            
            Integer app_selected = 5;
            String command_selected = "new";
            Integer id_usuario= user.getUserId();//variable para el id  del usuario
            String arreglo[];
            String extra_data_array = null;
            String actualizo = "0";
            
            if(Integer.parseInt(campo_consignacion) == 1 ){
                System.out.println("Si hay trs, EL total de tr es:"+total_tr);
                arreglo = new String[dc_eliminado.length];
                
                for(int i=0; i<dc_eliminado.length; i++) {
                    arreglo[i]= "'" + dc_calle[i].toUpperCase() +"___"+ dc_numero[i] +"___"+ dc_colonia[i].toUpperCase() +"___"+ dc_idpais[i] +"___"+ dc_identidad[i] +"___"+ dc_idlocalidad[i] +"___"+ dc_codigop[i] +"___"+ dc_localternativa[i]+"___"+ dirc_telefono[i] +"___"+ dc_numfax[i] +"'";
                    System.out.println(arreglo[i]);
                }
                //serializar el arreglo
                extra_data_array = StringUtils.join(arreglo, ",");
            }else{
                extra_data_array = "'sin datos'";
            }
            
            
            HashMap<String, String> jsonretorno = new HashMap<String, String>();
            
            HashMap<String, String> succes = new HashMap<String, String>();
            
            if( id_cliente.equals("0") ){
                command_selected = "new";
            }else{
                command_selected = "edit";
            }
            
            if (retencion_immex==null){
                retencion_immex="0";
            }
            
            String data_string = 
            app_selected
            +"___"+command_selected
            +"___"+id_usuario
            +"___"+id_cliente
            +"___"+nocontrol
            +"___"+rfc.toUpperCase()
            +"___"+curp.toUpperCase()
            +"___"+razonsocial.toUpperCase()
            +"___"+clave_comercial.toUpperCase()
            +"___"+calle.toUpperCase()
            +"___"+numero_int
            +"___"+entrecalles.toUpperCase()
            +"___"+numero_ext
            +"___"+colonia.toUpperCase()
            +"___"+cp
            +"___"+pais
            +"___"+estado
            +"___"+municipio
            +"___"+loc_alternativa.toUpperCase()
            +"___"+tel1
            +"___"+ext1
            +"___"+fax
            +"___"+tel2
            +"___"+ext2
            +"___"+email
            +"___"+agente
            +"___"+contacto.toUpperCase()
            +"___"+zona
            +"___"+grupo
            +"___"+tipocliente
            +"___"+clasif1
            +"___"+clasif2
            +"___"+clasif3
            +"___"+moneda
            +"___"+empresa_filial
            +"___"+estatus
            +"___"+impuesto
            +"___"+limite_credito
            +"___"+dias_credito
            +"___"+credito_suspendido
            +"___"+inicio_credito
            +"___"+tipo_embarque
            +"___"+cad_cotizacion
            +"___"+condiciones.toUpperCase()
            +"___"+observaciones.toUpperCase()
            +"___"+comp_contacto.toUpperCase()
            +"___"+comp_puesto.toUpperCase()
            +"___"+comp_calle.toUpperCase()
            +"___"+comp_numcalle
            +"___"+comp_colonia.toUpperCase()
            +"___"+comp_cp
            +"___"+comp_entrecalles.toUpperCase()
            +"___"+comp_pais
            +"___"+comp_estado
            +"___"+comp_municipio
            +"___"+comp_tel1
            +"___"+comp_ext1
            +"___"+comp_fax
            +"___"+comp_tel2
            +"___"+comp_ext2
            +"___"+comp_email
            +"___"+pag_contacto.toUpperCase()
            +"___"+pag_puesto.toUpperCase()
            +"___"+pag_calle.toUpperCase()
            +"___"+pag_numcalle
            +"___"+pag_colonia.toUpperCase()
            +"___"+pag_cp
            +"___"+pag_entrecalles.toUpperCase()
            +"___"+pag_pais
            +"___"+pag_estado
            +"___"+pag_municipio
            +"___"+pag_tel1
            +"___"+pag_ext1
            +"___"+pag_fax
            +"___"+pag_tel2
            +"___"+pag_ext2
            +"___"+pag_email
            +"___"+select_immex
            +"___"+retencion_immex
            +"___"+select_dia_revision
            +"___"+select_dia_pago
            +"___"+cuenta_mn
            +"___"+cuenta_usd
            +"___"+id_cta_activo
            +"___"+id_cta_ingreso
            +"___"+id_cta_ietu
            +"___"+id_cta_complementaria
            +"___"+id_cta_activo_complementaria
            +"___"+select_lista_precio
            +"___"+select_metodo_pago;
            
            //System.out.println("data_string: "+data_string);
            
            succes = this.getCxcDao().selectFunctionValidateAaplicativo(data_string,app_selected,extra_data_array);
            
            log.log(Level.INFO, "despues de validacion {0}", String.valueOf(succes.get("success")));
            if( String.valueOf(succes.get("success")).equals("true") ){
                actualizo = this.getCxcDao().selectFunctionForThisApp(data_string, extra_data_array);
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
        
        Integer app_selected = 5;
        String command_selected = "delete";
        String extra_data_array = "'sin datos'";
        
        String data_string = app_selected+"___"+command_selected+"___"+id_usuario+"___"+id;
        
        HashMap<String, String> jsonretorno = new HashMap<String, String>();
        
        System.out.println("Ejecutando borrado logico de cliente");
        jsonretorno.put("success",String.valueOf( this.getCxcDao().selectFunctionForThisApp(data_string,extra_data_array)) );
        
        return jsonretorno;
    }
    
    
    
    
    
    
    
    
    
}//termina class ClientsController