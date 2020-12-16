package com.agnux.kemikal.controllers;

import com.agnux.cfd.v2.Base64Coder;
import com.agnux.common.helpers.StringHelper;
import com.agnux.common.obj.ResourceProject;
import com.agnux.common.obj.UserSessionData;
import com.agnux.kemikal.interfacedaos.CrmInterfaceDao;
import com.agnux.kemikal.interfacedaos.HomeInterfaceDao;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
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
@RequestMapping("/crmbigpicture/")
public class CrmBigpictureController {
    ResourceProject resource = new ResourceProject();
    private static final Logger log  = Logger.getLogger(CrmBigpictureController.class.getName());
    
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
        
        log.log(Level.INFO, "Ejecutando starUp de {0}", CrmBigpictureController.class.getName());
        /*LinkedHashMap<String,String> infoConstruccionTabla = new LinkedHashMap<String,String>();
        infoConstruccionTabla.put("id", "Acciones:70");
        infoConstruccionTabla.put("folio", "Folio:100");
        infoConstruccionTabla.put("agente","Agente:300");
        infoConstruccionTabla.put("mes","Mes:100");
        */
        
        ModelAndView x = new ModelAndView("crmbigpicture/startup", "title","Consultas");
        x = x.addObject("layoutheader", resource.getLayoutheader());
        x = x.addObject("layoutmenu", resource.getLayoutmenu());
        x = x.addObject("layoutfooter", resource.getLayoutfooter());
        //x = x.addObject("grid", resource.generaGrid(infoConstruccionTabla));
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
    //getLlamadas
    
    @RequestMapping(method = RequestMethod.POST,value="/getDatosConfigConsulta.json")
    public @ResponseBody HashMap<String,ArrayList<HashMap<String, String>>> getDatosConfigConsultaJson(
            //@RequestParam(value="id", required=true) Integer id,
            @RequestParam(value="iu", required=true) String id_user,
            Model model
            ) {
        
        log.log(Level.INFO, "Ejecutando getDatosConfigConsulta de {0}", CrmBigpictureController.class.getName());
        HashMap<String,ArrayList<HashMap<String, String>>> jsonretorno = new HashMap<String,ArrayList<HashMap<String, String>>>();
        HashMap<String, String> userDat = new HashMap<String, String>();
        //ArrayList<HashMap<String, String>> datos = new ArrayList<HashMap<String, String>>();
        ArrayList<HashMap<String, String>> parametros = new ArrayList<HashMap<String, String>>();
        
        
        
        //decodificar id de usuario
        Integer id_usuario = Integer.parseInt(Base64Coder.decodeString(id_user));
        userDat = this.getHomeDao().getUserById(id_usuario);
        Integer id_empresa = Integer.parseInt(userDat.get("empresa_id"));
        
        parametros = this.getCrmDao().getDatosConfigConsulta(id_empresa, id_usuario);
        
        jsonretorno.put("Config", parametros);
        
        
        return jsonretorno;
        
        
    }
    
    //obtiene los Agentes para el Buscador pricipal del Aplicativo
    @RequestMapping(method = RequestMethod.POST, value="/getAgentesParaBuscador.json")
    public @ResponseBody HashMap<String,ArrayList<HashMap<String, String>>> getAgentesParaBuscador(
            @RequestParam(value="iu", required=true) String id_user_cod,
            Model model
        ) {
        
        HashMap<String,ArrayList<HashMap<String, String>>> jsonretorno = new HashMap<String,ArrayList<HashMap<String, String>>>();
        HashMap<String, String> userDat = new HashMap<String, String>();
        ArrayList<HashMap<String, String>> agentes = new ArrayList<HashMap<String, String>>();
        ArrayList<HashMap<String, String>> arrayExtra = new ArrayList<HashMap<String, String>>();
        HashMap<String, String> extra = new HashMap<String, String>();
        
        //decodificar id de usuario
        Integer id_usuario = Integer.parseInt(Base64Coder.decodeString(id_user_cod));
        userDat = this.getHomeDao().getUserById(id_usuario);
        Integer id_empresa = Integer.parseInt(userDat.get("empresa_id"));
        Integer id_agente = Integer.parseInt(userDat.get("empleado_id"));
        
        extra = this.getCrmDao().getUserRol(id_usuario);
        extra.put("id_agente", String.valueOf(id_agente));
        arrayExtra.add(0,extra);
        
        agentes = this.getCrmDao().getAgentes(id_empresa);
        
        jsonretorno.put("Extra", arrayExtra);
        jsonretorno.put("Agentes", agentes);
        return jsonretorno;
    }
    
    
    
    
    //obtiene los Agentes para el Buscador pricipal del Aplicativo
    @RequestMapping(method = RequestMethod.POST, value="/getResultadosConsulta.json")
    public @ResponseBody HashMap<String,ArrayList<HashMap<String, String>>> getResultadosConsultaJson(
            //agente
            @RequestParam(value="agente", required=true) String agente,
            @RequestParam(value="fecha_inicio", required=true) String fecha_inicio,
            @RequestParam(value="fecha_fin", required=true) String fecha_fin,
            @RequestParam(value="iu", required=true) String id_user_cod,
            Model model
        ) {
        
        HashMap<String,ArrayList<HashMap<String, String>>> jsonretorno = new HashMap<String,ArrayList<HashMap<String, String>>>();
        HashMap<String, String> userDat = new HashMap<String, String>();
        ArrayList<HashMap<String, String>> bigPicture = new ArrayList<HashMap<String, String>>();
        ArrayList<HashMap<String, String>> parametros = new ArrayList<HashMap<String, String>>();
        HashMap<String, String> extra = new HashMap<String, String>();
        
        //decodificar id de usuario
        Integer id_usuario = Integer.parseInt(Base64Coder.decodeString(id_user_cod));
        userDat = this.getHomeDao().getUserById(id_usuario);
        Integer id_empresa = Integer.parseInt(userDat.get("empresa_id"));
        //Integer id_agente = Integer.parseInt(userDat.get("empleado_id"));
        
        parametros = this.getCrmDao().getDatosConfigConsulta(id_empresa, id_usuario);
        bigPicture = this.getCrmDao().getResultadosBigPicture(id_usuario, id_empresa,agente, fecha_inicio,fecha_fin );
        
        jsonretorno.put("ConfigData", parametros);
        jsonretorno.put("bigPicture", bigPicture);
        return jsonretorno;
    }
    
    
    
    //crear y editar
    @RequestMapping(method = RequestMethod.POST, value="/edit.json")
    public @ResponseBody HashMap<String, String> editJson(
            @RequestParam(value="identificador", required=true) String id,
            @RequestParam(value="metas_visita", required=false) String metas_visita,
            @RequestParam(value="totales_visita", required=false) String totales_visita,
            @RequestParam(value="cumplido_visita", required=false) String cumplido_visita,
            @RequestParam(value="conexito_visita", required=false) String conexito_visita,
            @RequestParam(value="conoportunidad_visita", required=false) String conoportunidad_visita,
            @RequestParam(value="seguimiento_visita", required=false) String seguimiento_visita,
            @RequestParam(value="efectividad_visita", required=false) String efectividad_visita,
            @RequestParam(value="gestion_visita", required=false) String gestion_visita,
            @RequestParam(value="avance_visita", required=false) String avance_visita,
            @RequestParam(value="metas_llamadas", required=false) String metas_llamadas,
            @RequestParam(value="total_llamadas", required=false) String total_llamadas,
            @RequestParam(value="cumplido_llamadas", required=false) String cumplido_llamadas,
            @RequestParam(value="entrantes_llamadas", required=false) String entrantes_llamadas,
            @RequestParam(value="salientes_llamadas", required=false) String salientes_llamadas,
            @RequestParam(value="planeadas_llamadas", required=false) String planeadas_llamadas,
            @RequestParam(value="con_exito_llamadas", required=false) String con_exito_llamadas,
            @RequestParam(value="con_cita_llamadas", required=false) String con_cita_llamadas,
            @RequestParam(value="conseguimiento_llamadas", required=false) String conseguimiento_llamadas,
            @RequestParam(value="efectividad_llamadas", required=false) String efectividad_llamadas,
            @RequestParam(value="gestión_llamadas", required=false) String gestión_llamadas,
            @RequestParam(value="avance_llamadas", required=false) String avance_llamadas,
            @RequestParam(value="planeación_llamadas", required=false) String planeación_llamadas,
            @RequestParam(value="facturacion_casos", required=false) String facturacion_casos,
            @RequestParam(value="producto_casos", required=false) String producto_casos,
            @RequestParam(value="garantia_casos", required=false) String garantia_casos,
            @RequestParam(value="distribucion_casos", required=false) String distribucion_casos,
            @RequestParam(value="danos_casos", required=false) String danos_casos,
            @RequestParam(value="devoluciones_casos", required=false) String devoluciones_casos,
            @RequestParam(value="cobranza_casos", required=false) String cobranza_casos,
            @RequestParam(value="varios_casos", required=false) String varios_casos,
            @RequestParam(value="metas_oportunidades", required=false) String metas_oportunidades,
            @RequestParam(value="total_metas_oportunidades", required=false) String total_metas_oportunidades,
            @RequestParam(value="montos_meta_oportunidades", required=false) String montos_meta_oportunidades,
            @RequestParam(value="total_montos_oportunidades", required=false) String total_montos_oportunidades,
            @RequestParam(value="metas_cumplidas_oportunidades", required=false) String metas_cumplidas_oportunidades,
            @RequestParam(value="montos_cumplidas_oportunidades", required=false) String montos_cumplidas_oportunidades,
            @RequestParam(value="inicial_oportunidades", required=false) String inicial_oportunidades,
            @RequestParam(value="seguimiento_oportunidades", required=false) String seguimiento_oportunidades,
            @RequestParam(value="visitas_oportunidades", required=false) String visitas_oportunidades,
            @RequestParam(value="cotizacion_oportunidades", required=false) String cotizacion_oportunidades,
            @RequestParam(value="negociacion_oportunidades", required=false) String negociacion_oportunidades,
            @RequestParam(value="cierre_oportunidades", required=false) String cierre_oportunidades,
            @RequestParam(value="ganados_oportunidades", required=false) String ganados_oportunidades,
            @RequestParam(value="perdidos_oportunidades", required=false) String perdidos_oportunidades,
            Model model,@ModelAttribute("user") UserSessionData user
            ) {
        
        HashMap<String, String> jsonretorno = new HashMap<String, String>();
        HashMap<String, String> succes = new HashMap<String, String>();
        Integer app_selected = 130;
        String command_selected = "new";
        Integer id_usuario= user.getUserId();//variable para el id  del usuario
        String extra_data_array = "'sin datos'";
        String actualizo = "0";
        
        
        metas_visita = StringHelper.verificarCheckBox(metas_visita);
        totales_visita = StringHelper.verificarCheckBox(totales_visita);
        cumplido_visita = StringHelper.verificarCheckBox(cumplido_visita);
        conexito_visita = StringHelper.verificarCheckBox(conexito_visita);
        conoportunidad_visita = StringHelper.verificarCheckBox(conoportunidad_visita);
        seguimiento_visita = StringHelper.verificarCheckBox(seguimiento_visita);
        efectividad_visita = StringHelper.verificarCheckBox(efectividad_visita);
        gestion_visita = StringHelper.verificarCheckBox(gestion_visita);
        avance_visita = StringHelper.verificarCheckBox(avance_visita);
        metas_llamadas = StringHelper.verificarCheckBox(metas_llamadas);
        total_llamadas = StringHelper.verificarCheckBox(total_llamadas);
        cumplido_llamadas = StringHelper.verificarCheckBox(cumplido_llamadas);
        entrantes_llamadas = StringHelper.verificarCheckBox(entrantes_llamadas);
        salientes_llamadas = StringHelper.verificarCheckBox(salientes_llamadas);
        planeadas_llamadas = StringHelper.verificarCheckBox(planeadas_llamadas);
        con_exito_llamadas = StringHelper.verificarCheckBox(con_exito_llamadas);
        con_cita_llamadas = StringHelper.verificarCheckBox(con_cita_llamadas);
        conseguimiento_llamadas = StringHelper.verificarCheckBox(conseguimiento_llamadas);
        efectividad_llamadas = StringHelper.verificarCheckBox(efectividad_llamadas);
        gestión_llamadas = StringHelper.verificarCheckBox(gestión_llamadas);
        avance_llamadas = StringHelper.verificarCheckBox(avance_llamadas);
        planeación_llamadas = StringHelper.verificarCheckBox(planeación_llamadas);
        facturacion_casos = StringHelper.verificarCheckBox(facturacion_casos);
        producto_casos = StringHelper.verificarCheckBox(producto_casos);
        garantia_casos = StringHelper.verificarCheckBox(garantia_casos);
        distribucion_casos = StringHelper.verificarCheckBox(distribucion_casos);
        danos_casos = StringHelper.verificarCheckBox(danos_casos);
        devoluciones_casos = StringHelper.verificarCheckBox(devoluciones_casos);
        cobranza_casos = StringHelper.verificarCheckBox(cobranza_casos);
        varios_casos = StringHelper.verificarCheckBox(varios_casos);
        metas_oportunidades = StringHelper.verificarCheckBox(metas_oportunidades);
        total_metas_oportunidades = StringHelper.verificarCheckBox(total_metas_oportunidades);
        montos_meta_oportunidades = StringHelper.verificarCheckBox(montos_meta_oportunidades);
        total_montos_oportunidades = StringHelper.verificarCheckBox(total_montos_oportunidades);
        metas_cumplidas_oportunidades = StringHelper.verificarCheckBox(metas_cumplidas_oportunidades);
        montos_cumplidas_oportunidades = StringHelper.verificarCheckBox(montos_cumplidas_oportunidades);
        inicial_oportunidades = StringHelper.verificarCheckBox(inicial_oportunidades);
        seguimiento_oportunidades = StringHelper.verificarCheckBox(seguimiento_oportunidades);
        visitas_oportunidades = StringHelper.verificarCheckBox(visitas_oportunidades);
        cotizacion_oportunidades = StringHelper.verificarCheckBox(cotizacion_oportunidades);
        negociacion_oportunidades = StringHelper.verificarCheckBox(negociacion_oportunidades);
        cierre_oportunidades = StringHelper.verificarCheckBox(cierre_oportunidades);
        ganados_oportunidades = StringHelper.verificarCheckBox(ganados_oportunidades);
        perdidos_oportunidades = StringHelper.verificarCheckBox(perdidos_oportunidades);
        
        extra_data_array = "'sin datos'";
        
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
            metas_visita+"___"+	
            totales_visita+"___"+	
            cumplido_visita+"___"+	
            conexito_visita+"___"+	
            conoportunidad_visita+"___"+	
            seguimiento_visita+"___"+	
            efectividad_visita+"___"+	
            gestion_visita+"___"+	
            avance_visita+"___"+	
            metas_llamadas+"___"+	
            total_llamadas+"___"+	
            cumplido_llamadas+"___"+	
            entrantes_llamadas+"___"+	
            salientes_llamadas+"___"+	
            planeadas_llamadas+"___"+	
            con_exito_llamadas+"___"+	
            con_cita_llamadas+"___"+	
            conseguimiento_llamadas+"___"+	
            efectividad_llamadas+"___"+	
            gestión_llamadas+"___"+	
            avance_llamadas+"___"+	
            planeación_llamadas+"___"+	
            facturacion_casos+"___"+	
            producto_casos+"___"+	
            garantia_casos+"___"+	
            distribucion_casos+"___"+	
            danos_casos+"___"+	
            devoluciones_casos+"___"+	
            cobranza_casos+"___"+	
            varios_casos+"___"+	
            metas_oportunidades+"___"+	
            total_metas_oportunidades+"___"+	
            montos_meta_oportunidades+"___"+	
            total_montos_oportunidades+"___"+
            metas_cumplidas_oportunidades+"___"+	
            montos_cumplidas_oportunidades+"___"+	
            inicial_oportunidades+"___"+	
            seguimiento_oportunidades+"___"+	
            visitas_oportunidades+"___"+	
            cotizacion_oportunidades+"___"+	
            negociacion_oportunidades+"___"+	
            cierre_oportunidades+"___"+	
            ganados_oportunidades+"___"+
            perdidos_oportunidades;
        
        succes = this.getCrmDao().selectFunctionValidateAaplicativo(data_string,app_selected,extra_data_array);
        
        log.log(Level.INFO, "despues de validacion {0}", String.valueOf(succes.get("success")));
        if( String.valueOf(succes.get("success")).equals("true") ){
            actualizo = this.getCrmDao().selectFunctionForCrmAdmProcesos(data_string, extra_data_array);
        }
        
        jsonretorno.put("success",String.valueOf(succes.get("success")));
        
        log.log(Level.INFO, "Salida json {0}", String.valueOf(jsonretorno.get("success")));
        return jsonretorno;
        
    }
    
    
    
}
