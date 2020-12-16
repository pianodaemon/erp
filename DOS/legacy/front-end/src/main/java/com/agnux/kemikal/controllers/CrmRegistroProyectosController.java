package com.agnux.kemikal.controllers;

import com.agnux.cfd.v2.Base64Coder;
import com.agnux.common.helpers.StringHelper;
import com.agnux.common.obj.DataPost;
import com.agnux.common.obj.ResourceProject;
import com.agnux.common.obj.UserSessionData;
import com.agnux.kemikal.interfacedaos.CrmInterfaceDao;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;


@Controller
@SessionAttributes({"user"})
@RequestMapping("/crmregistroproyectos/")
public class CrmRegistroProyectosController {
    ResourceProject resource = new ResourceProject();
    private static final Logger log  = Logger.getLogger(CrmRegistroProyectosController.class.getName());
    
    @Autowired
    @Qualifier("daoCrm")
    private CrmInterfaceDao CrmlDao;
    
    @Autowired
    @Qualifier("daoHome")
    private HomeInterfaceDao HomeDao;
    
    @Autowired
    @Qualifier("daoCxc")
    private CxcInterfaceDao cxcDao;
    
    public CxcInterfaceDao getCxcDao() {
        return cxcDao;
    }
    
    public void setCxcDao(CxcInterfaceDao cxcDao) {
        this.cxcDao = cxcDao;
    }
    
    public CrmInterfaceDao getCrmDao() {
        return CrmlDao;
    }
    
    public HomeInterfaceDao getHomeDao() {
        return HomeDao;
    }
    
    
    @RequestMapping(value="/startup.agnux")
    public ModelAndView startUp(HttpServletRequest request, HttpServletResponse response, @ModelAttribute("user") UserSessionData user)
            throws ServletException, IOException {
        
        log.log(Level.INFO, "Ejecutando starUp de {0}", CrmRegistroProyectosController.class.getName());
        LinkedHashMap<String,String> infoConstruccionTabla = new LinkedHashMap<String,String>();
        
        infoConstruccionTabla.put("id", "Acciones:90");
        infoConstruccionTabla.put("folio", "Folio:80");
        infoConstruccionTabla.put("fecha", "Fecha:80");
        infoConstruccionTabla.put("proyecto", "Proyecto:280");
        infoConstruccionTabla.put("monto", "Monto:80");
        infoConstruccionTabla.put("agente", "Asignado&nbsp;a:280");
        infoConstruccionTabla.put("cliente", "Cliente:280");
        infoConstruccionTabla.put("estatus", "Estatus:150");
        
        ModelAndView x = new ModelAndView("crmregistroproyectos/startup", "title", "Registro de Proyectos");
        
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
    
    //Obtener todos los registros de Visitas del Agente
    @RequestMapping(value="/getAllRegistros.json", method = RequestMethod.POST)
    public @ResponseBody HashMap<String,ArrayList<HashMap<String, Object>>> getAllRegistrosJson(
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
        
        //Aplicativo de Registro de Proyectos(CRM)
        Integer app_selected = 207;
        
        //decodificar id de usuario
        Integer id_usuario = Integer.parseInt(Base64Coder.decodeString(id_user_cod));
        
        //variables para el buscador
        String folio = "%"+StringHelper.isNullString(String.valueOf(has_busqueda.get("folio")))+"%";
        String proyecto = "%"+StringHelper.isNullString(String.valueOf(has_busqueda.get("proyecto")))+"%";
        String agente = String.valueOf(has_busqueda.get("agente"));
        String fecha_inicial = ""+StringHelper.isNullString(String.valueOf(has_busqueda.get("fecha_inicial")))+"";
        String fecha_final = ""+StringHelper.isNullString(String.valueOf(has_busqueda.get("fecha_final")))+"";
        String cliente = "%"+StringHelper.isNullString(String.valueOf(has_busqueda.get("cliente")))+"%";
        String segmento = StringHelper.isNullString(String.valueOf(has_busqueda.get("segmento")));
        String mercado = StringHelper.isNullString(String.valueOf(has_busqueda.get("mercado")));
        
        String data_string = app_selected+"___"+id_usuario+"___"+folio+"___"+proyecto+"___"+agente+"___"+fecha_inicial+"___"+fecha_final+"___"+cliente+"___"+segmento+"___"+mercado;
        
        //obtiene total de registros en base de datos, con los parametros de busqueda
        int total_items = this.getCrmDao().countAll(data_string);
        
        //calcula el total de paginas
        int total_pags = resource.calculaTotalPag(total_items,items_por_pag);
        
        //variables que necesita el datagrid, para no tener que hacer uno por cada aplicativo
        DataPost dataforpos = new DataPost(orderby, desc, items_por_pag, pag_start, display_pag, input_json, cadena_busqueda,total_items,total_pags, id_user_cod);
        
        int offset = resource.__get_inicio_offset(items_por_pag, pag_start);
        
        //obtiene los registros para el grid, de acuerdo a los parametros de busqueda
        jsonretorno.put("Data", this.getCrmDao().getCrmRegistroProyectos_PaginaGrid(data_string, offset, items_por_pag, orderby, desc));
        //obtiene el hash para los datos que necesita el datagrid
        jsonretorno.put("DataForGrid", dataforpos.formaHashForPos(dataforpos));
        
        return jsonretorno;
    }
    
    
    
    
    
    //obtiene los Agentes para el Buscador pricipal del Aplicativo
    @RequestMapping(method = RequestMethod.POST, value="/getDatosParaBuscador.json")
    public @ResponseBody HashMap<String,Object> getDatosParaBuscadorJson(
            @RequestParam(value="iu", required=true) String id_user_cod,
            Model model
        ) {
        
        HashMap<String,Object> jsonretorno = new HashMap<String,Object>();
        HashMap<String, String> userDat = new HashMap<String, String>();
        ArrayList<HashMap<String, String>> agentes = new ArrayList<HashMap<String, String>>();
        ArrayList<HashMap<String, String>> agentes2 = new ArrayList<HashMap<String, String>>();
        ArrayList<HashMap<String, Object>> arrayExtra = new ArrayList<HashMap<String, Object>>();
        HashMap<String, Object> extra = new HashMap<String, Object>();
        HashMap<String, String> parametros = new HashMap<String, String>();
        
        //Decodificar id de usuario
        Integer id_usuario = Integer.parseInt(Base64Coder.decodeString(id_user_cod));
        userDat = this.getHomeDao().getUserById(id_usuario);
        Integer id_empresa = Integer.parseInt(userDat.get("empresa_id"));
        Integer id_agente = Integer.parseInt(userDat.get("empleado_id"));
        
        parametros = this.getCrmDao().getCrm_Parametros(id_empresa, Integer.parseInt(userDat.get("sucursal_id")));
        
        //Id del Agente de Ventas(id del empleado)
        extra.put("no_agen", String.valueOf(id_agente));
        
        //Obtener los empleados de un departamento
        agentes = this.getCrmDao().getEmpleadosPorDepartamento(id_empresa, Integer.parseInt(parametros.get("depto_id")));
        
        if(Integer.parseInt(this.getCrmDao().getUserRol(id_usuario).get("exis_rol_admin"))<=0){
            for( HashMap<String,String> i : agentes ){
                if(Integer.parseInt(i.get("id").toString())==id_agente){
                    agentes2.add(i);
                }
            }
        }else{
            agentes2 = agentes;
            extra.put("mostrarAgentes", true);
        }
        
        arrayExtra.add(0,extra);
        
        jsonretorno.put("Extra", arrayExtra);
        jsonretorno.put("Agentes", agentes2);
        
        jsonretorno.put("Segmentos", this.getCxcDao().getCliente_Clasificacion1());
        jsonretorno.put("Mercados", this.getCxcDao().getCliente_Clasificacion2());
        
        return jsonretorno;
    }
    
    
    
    @RequestMapping(method = RequestMethod.POST, value="/getRegistroProyecto.json")
    public @ResponseBody HashMap<String,Object> getRegistroVisitaJson(
            @RequestParam(value="id", required=true) Integer id,
            @RequestParam(value="iu", required=true) String id_user,
            Model model
        ) {
        
        log.log(Level.INFO, "Ejecutando getRegistroProyectoJson de {0}", CrmRegistroProyectosController.class.getName());
        HashMap<String,Object> jsonretorno = new HashMap<String,Object>();
        HashMap<String, String> userDat = new HashMap<String, String>();
        ArrayList<HashMap<String, Object>> datos = new ArrayList<HashMap<String, Object>>();
        ArrayList<HashMap<String, String>> arrayExtra = new ArrayList<HashMap<String, String>>();
        HashMap<String, String> extra = new HashMap<String, String>();
        HashMap<String, String> parametros = new HashMap<String, String>();
        
        //Decodificar id de usuario
        Integer id_usuario = Integer.parseInt(Base64Coder.decodeString(id_user));
        userDat = this.getHomeDao().getUserById(id_usuario);
        Integer id_empresa = Integer.parseInt(userDat.get("empresa_id"));
        Integer id_agente = Integer.parseInt(userDat.get("empleado_id"));
        
        parametros = this.getCrmDao().getCrm_Parametros(id_empresa, Integer.parseInt(userDat.get("sucursal_id")));
        
        if( id != 0  ){
            datos = this.getCrmDao().getCrmRegistroProyectos_Datos(id);
            jsonretorno.put("Competidores", this.getCrmDao().getCrmRegistroProyectos_Competidores(id));
        }
        
        extra = this.getCrmDao().getUserRol(id_usuario);
        extra.put("id_agente", String.valueOf(id_agente));
        arrayExtra.add(0,extra);
        
        //Obtener los empleados de un departamento
        jsonretorno.put("Agentes", this.getCrmDao().getEmpleadosPorDepartamento(id_empresa, Integer.parseInt(parametros.get("depto_id"))));
        
        jsonretorno.put("Datos", datos);
        jsonretorno.put("Extra", arrayExtra);
        jsonretorno.put("Monedas", this.getCrmDao().getMonedas());
        jsonretorno.put("Estatus", this.getCrmDao().getCrmRegistroProyectos_Estatus(id_empresa));
        jsonretorno.put("Segmentos", this.getCxcDao().getCliente_Clasificacion1());
        jsonretorno.put("Mercados", this.getCxcDao().getCliente_Clasificacion2());
        
        return jsonretorno;
    }
    
    
    
    //obtiene los Contactos para el buscador
    @RequestMapping(method = RequestMethod.POST, value="/get_buscador_contactos.json")
    public @ResponseBody HashMap<String,ArrayList<HashMap<String, String>>> getBuscadorContactoJson(
            @RequestParam(value="buscador_nombre", required=true) String nombre,
            @RequestParam(value="buscador_apellidop", required=true) String apellidop,
            @RequestParam(value="buscador_apellidom", required=true) String apellidom,
            @RequestParam(value="buscador_tipo_contacto", required=true) String tipo_contacto,
            @RequestParam(value="iu", required=true) String id_user,
            Model model
        ) {
        
        log.log(Level.INFO, "Ejecutando getBuscadorContactoJson de {0}", CrmRegistroProyectosController.class.getName());
        HashMap<String,ArrayList<HashMap<String, String>>> jsonretorno = new HashMap<String,ArrayList<HashMap<String, String>>>();
        ArrayList<HashMap<String, String>> contactos = new ArrayList<HashMap<String, String>>();
        HashMap<String, String> userDat = new HashMap<String, String>();
        
        //decodificar id de usuario
        userDat = this.getHomeDao().getUserById(Integer.parseInt(Base64Coder.decodeString(id_user)));
        
        Integer id_empresa = Integer.parseInt(userDat.get("empresa_id"));
        
        nombre = "%"+StringHelper.isNullString(String.valueOf(nombre))+"%";
        apellidop = "%"+StringHelper.isNullString(String.valueOf(apellidop))+"%";
        apellidom = "%"+StringHelper.isNullString(String.valueOf(apellidom))+"%";
        
        contactos = this.getCrmDao().getBuscadorContactos(nombre, apellidop, apellidom, tipo_contacto, id_empresa);
        
        jsonretorno.put("contactos", contactos);
        
        return jsonretorno;
    }
    
    //Obtiene los proveedores para el buscador
    @RequestMapping(method = RequestMethod.POST, value="/getProveedores.json")
    public @ResponseBody HashMap<String,Object> getProveedoresJson(
            @RequestParam(value="rfc", required=true) String rfc,
            @RequestParam(value="no_prov", required=true) String no_prov,
            @RequestParam(value="nombre", required=true) String nombre,
            @RequestParam(value="iu", required=true) String id_user,
            Model model
        ) {
        
        log.log(Level.INFO, "Ejecutando getProveedoresJson de {0}", CrmRegistroProyectosController.class.getName());
        HashMap<String,Object> jsonretorno = new HashMap<String,Object>();
        ArrayList<HashMap<String, Object>> proveedores = new ArrayList<HashMap<String, Object>>();
        HashMap<String, String> userDat = new HashMap<String, String>();
        //decodificar id de usuario
        Integer id_usuario = Integer.parseInt(Base64Coder.decodeString(id_user));
        userDat = this.getHomeDao().getUserById(id_usuario);
        
        Integer id_empresa = Integer.parseInt(userDat.get("empresa_id"));
        
        proveedores = this.getCrmDao().getBuscadorProveedores(rfc.trim().toUpperCase(), no_prov.trim().toUpperCase(), nombre.trim().toUpperCase(),id_empresa);
        
        jsonretorno.put("proveedores", proveedores);
        
        return jsonretorno;
    }
    
    
    //crear y editar un cliente
    @RequestMapping(method = RequestMethod.POST, value="/edit.json")
    public @ResponseBody HashMap<String, String> editJson(
        @RequestParam(value="identificador", required=true) String identificador,
        @RequestParam(value="nombre", required=true) String nombre,
        @RequestParam(value="descripcion", required=true) String descripcion,
        @RequestParam(value="select_agente", required=true) String select_agente,
        @RequestParam(value="id_contacto", required=true) String id_contacto,
        @RequestParam(value="id_prov", required=true) String id_prov,
        @RequestParam(value="fecha_inicio", required=true) String fecha_inicio,
        @RequestParam(value="fecha_fin", required=true) String fecha_fin,
        @RequestParam(value="select_estatus", required=true) String select_estatus,
        @RequestParam(value="select_prioridad", required=true) String select_prioridad,
        @RequestParam(value="select_muestra", required=true) String select_muestra,
        @RequestParam(value="select_moneda", required=true) String select_moneda,
        @RequestParam(value="select_periodicidad", required=true) String select_periodicidad,
        @RequestParam(value="monto", required=true) String monto,
        @RequestParam(value="kilogramos", required=true) String kilogramos,
        @RequestParam(value="observaciones", required=true) String observaciones,
        
        @RequestParam(value="select_segmento", required=true) String select_segmento,
        @RequestParam(value="select_mercado", required=true) String select_mercado,
        
        @RequestParam(value="iddet", required=false) String[] iddet,
        @RequestParam(value="competidor", required=false) String[] competidor,
        @RequestParam(value="precio", required=false) String[] precio,
        @RequestParam(value="prov", required=false) String[] prov,
        
        Model model,@ModelAttribute("user") UserSessionData user
    ) {
        
        //Aplicativo de Registro de Proyectos(CRM)
        Integer app_selected = 207;
        String command_selected = "new";
        Integer id_usuario= user.getUserId();//variable para el id  del usuario
        String arreglo[];
        String extra_data_array = "'sin_datos'";
        String actualizo = "0";
        arreglo = new String[iddet.length];
        
        HashMap<String, String> jsonretorno = new HashMap<String, String>();
        
        HashMap<String, String> succes = new HashMap<String, String>();
        
        if( identificador.equals("0") ){
            command_selected = "new";
        }else{
            command_selected = "edit";
        }
        
        if(iddet.length>0){
            for(int i=0; i<iddet.length; i++) {
                arreglo[i]= "'"+iddet[i] +"___"+ competidor[i].toUpperCase() +"___"+ precio[i] +"___"+ prov[i].toUpperCase() +"'";
                //System.out.println(arreglo[i]);
            }
            
            //Serializar el arreglo
            extra_data_array = StringUtils.join(arreglo, ",");
        }
        
        select_agente = StringHelper.verificarSelect(select_agente);
        select_estatus = StringHelper.verificarSelect(select_estatus);
        select_prioridad = StringHelper.verificarSelect(select_prioridad);
        select_muestra = StringHelper.verificarSelect(select_muestra);
        select_moneda = StringHelper.verificarSelect(select_moneda);
        select_periodicidad = StringHelper.verificarSelect(select_periodicidad);
        select_segmento = StringHelper.verificarSelect(select_segmento);
        select_mercado = StringHelper.verificarSelect(select_mercado);
        
        String data_string = app_selected+"___"+command_selected+"___"+id_usuario+"___"+identificador+"___"+nombre.toUpperCase()+"___"+descripcion.toUpperCase()+"___"+select_agente+"___"+id_contacto+"___"+id_prov+"___"+fecha_inicio+"___"+fecha_fin+"___"+select_estatus+"___"+select_prioridad+"___"+select_muestra+"___"+observaciones.toUpperCase()+"___"+monto+"___"+select_moneda+"___"+select_periodicidad+"___"+kilogramos+"___"+select_segmento+"___"+select_mercado;
        
        succes = this.getCrmDao().selectFunctionValidateAaplicativo(data_string,app_selected,extra_data_array);
        
        log.log(Level.INFO, "despues de validacion {0}", String.valueOf(succes.get("success")));
        if( String.valueOf(succes.get("success")).equals("true") ){
            actualizo = this.getCrmDao().selectFunctionForCrmAdmProcesos(data_string, extra_data_array);
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
        
        System.out.println("Borrado logico de Registro de Visita");
        //decodificar id de usuario
        Integer id_usuario = Integer.parseInt(Base64Coder.decodeString(id_user));
        
        //Aplicativo de Registro de Proyectos(CRM)
        Integer app_selected = 207;
        String command_selected = "delete";
        String extra_data_array = "'sin datos'";
        
        String data_string = app_selected+"___"+command_selected+"___"+id_usuario+"___"+id;
        
        HashMap<String, String> jsonretorno = new HashMap<String, String>();
        
        jsonretorno.put("success",String.valueOf( this.getCrmDao().selectFunctionForCrmAdmProcesos(data_string,extra_data_array)) );
        
        return jsonretorno;
    }
}
