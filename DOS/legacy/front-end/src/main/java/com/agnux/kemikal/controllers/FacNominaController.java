package com.agnux.kemikal.controllers;

import com.agnux.cfd.v2.Base64Coder;
import com.agnux.cfdi.BeanFromCfdiXml;
import com.agnux.cfdi.timbre.BeanFacturadorCfdiTimbre;
import com.agnux.common.helpers.FileHelper;
import com.agnux.common.helpers.StringHelper;
import com.agnux.common.helpers.TimeHelper;
import com.agnux.common.helpers.XmlHelper;
import com.agnux.common.obj.DataPost;
import com.agnux.common.obj.ResourceProject;
import com.agnux.common.obj.UserSessionData;
import com.agnux.kemikal.interfacedaos.FacturasInterfaceDao;
import com.agnux.kemikal.interfacedaos.GralInterfaceDao;
import com.agnux.kemikal.interfacedaos.HomeInterfaceDao;
import com.agnux.kemikal.reportes.PdfCfdiNomina;
import com.itextpdf.text.DocumentException;
import java.io.*;
import java.net.URISyntaxException;
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
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.SessionAttributes;


@Controller
@SessionAttributes({"user"})
@RequestMapping("/facnomina/")
public class FacNominaController {
    ResourceProject resource = new ResourceProject();
    private static final Logger log  = Logger.getLogger(FacNominaController.class.getName());
    
    @Autowired
    @Qualifier("daoFacturas")
    private FacturasInterfaceDao facdao;
        
    @Autowired
    @Qualifier("daoGral")
    private GralInterfaceDao gralDao;
    
    @Autowired
    @Qualifier("daoHome")
    private HomeInterfaceDao HomeDao;
    
    @Autowired
    @Qualifier("beanFacturadorCfdiTf")
    BeanFacturadorCfdiTimbre bfCfdiTf;
    
    public FacturasInterfaceDao getFacdao() {
        return facdao;
    }
    
    public HomeInterfaceDao getHomeDao() {
        return HomeDao;
    }
    
    public GralInterfaceDao getGralDao() {
        return gralDao;
    }
    
    public BeanFacturadorCfdiTimbre getBfCfdiTf() {
        return bfCfdiTf;
    }
    
    @RequestMapping(value="/startup.agnux")
    public ModelAndView startUp(HttpServletRequest request, HttpServletResponse response, 
            @ModelAttribute("user") UserSessionData user
        )throws ServletException, IOException {
        
        log.log(Level.INFO, "Ejecutando starUp de {0}", FacNominaController.class.getName());
        LinkedHashMap<String,String> infoConstruccionTabla = new LinkedHashMap<String,String>();
        
        infoConstruccionTabla.put("id", "Acciones:70");
        infoConstruccionTabla.put("no_periodo", "No. Periodo:100");
        infoConstruccionTabla.put("periodo", "Periodo:320");
        infoConstruccionTabla.put("fecha_pago", "Fecha Pago:100");
        infoConstruccionTabla.put("tipo", "Tipo:120");
        infoConstruccionTabla.put("fecha_creacion","Fecha Creaci&oacute;n:110");
        
        ModelAndView x = new ModelAndView("facnomina/startup", "title", "N&oacute;mina");
        
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
    
    
    
    @RequestMapping(value="/getAllNominas.json", method = RequestMethod.POST)
    public @ResponseBody HashMap<String,ArrayList<HashMap<String, Object>>> getAllNominasJson(
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

        //Aplicativo de Nomina
        Integer app_selected = 173;

        //Decodificar id de usuario
        Integer id_usuario = Integer.parseInt(Base64Coder.decodeString(id_user_cod));

        //Variables para el buscador
        String no_periodo = "%"+StringHelper.isNullString(String.valueOf(has_busqueda.get("no_periodo")))+"%";
        String titulo_periodo = "%"+StringHelper.isNullString(String.valueOf(has_busqueda.get("titulo_periodo")))+"%";
        String tipo_periodo = ""+StringHelper.isNullString(String.valueOf(has_busqueda.get("tipo_periodo")))+"";
        String fecha_inicial = ""+StringHelper.isNullString(String.valueOf(has_busqueda.get("fecha_inicial")))+"";
        String fecha_final = ""+StringHelper.isNullString(String.valueOf(has_busqueda.get("fecha_final")))+"";
        
        String data_string = app_selected+"___"+id_usuario+"___"+no_periodo+"___"+titulo_periodo+"___"+tipo_periodo+"___"+fecha_inicial+"___"+fecha_final;
        
        //Obtiene total de registros en base de datos, con los parametros de busqueda
        int total_items = this.getGralDao().countAll(data_string);
        
        //Calcula el total de paginas
        int total_pags = resource.calculaTotalPag(total_items,items_por_pag);
        
        //Variables que necesita el datagrid, para no tener que hacer uno por cada aplicativo
        DataPost dataforpos = new DataPost(orderby, desc, items_por_pag, pag_start, display_pag, input_json, cadena_busqueda,total_items,total_pags, id_user_cod);

        int offset = resource.__get_inicio_offset(items_por_pag, pag_start);

        //Obtiene los registros para el grid, de acuerdo a los parametros de busqueda
        jsonretorno.put("Data", this.getFacdao().getFacNomina_PaginaGrid(data_string, offset, items_por_pag, orderby, desc));
        
        //Obtiene el hash para los datos que necesita el datagrid
        jsonretorno.put("DataForGrid", dataforpos.formaHashForPos(dataforpos));
        
        return jsonretorno;
    }

    //Obtiene los Tipos de Periodicidad para el Buscador
    @RequestMapping(method = RequestMethod.POST, value="/getDatosParaBuscador.json")
    public @ResponseBody HashMap<String,ArrayList<HashMap<String, Object>>> getDatosParaBuscadorJson(
            @RequestParam(value="iu", required=true) String id_user,
            Model model
        ) {
        HashMap<String,ArrayList<HashMap<String, Object>>> jsonretorno = new HashMap<String,ArrayList<HashMap<String, Object>>>();
        HashMap<String, String> userDat = new HashMap<String, String>();
        
        //Decodificar id de usuario
        Integer id_usuario = Integer.parseInt(Base64Coder.decodeString(id_user));
        userDat = this.getHomeDao().getUserById(id_usuario);
        Integer id_empresa = Integer.parseInt(userDat.get("empresa_id"));
        
        jsonretorno.put("TiposPeriodicidadBusqueda", this.getFacdao().getFacNomina_PeriodicidadPago(id_empresa));
        
        return jsonretorno;
    }

    @RequestMapping(method = RequestMethod.POST, value="/getNomina.json")
    public @ResponseBody HashMap<String,ArrayList<HashMap<String, Object>>> getNominaJson(
            @RequestParam(value="identificador", required=true) Integer identificador,
            @RequestParam(value="iu", required=true) String id_user,
            Model model
        ) {
        log.log(Level.INFO, "Ejecutando getPrefacturaJson de {0}", PrefacturasController.class.getName());
        HashMap<String,ArrayList<HashMap<String, Object>>> jsonretorno = new HashMap<String,ArrayList<HashMap<String, Object>>>();
        ArrayList<HashMap<String, Object>> datos = new ArrayList<HashMap<String, Object>>();
        ArrayList<HashMap<String, Object>> datosGrid = new ArrayList<HashMap<String, Object>>();
        ArrayList<HashMap<String, Object>> parametros = new ArrayList<HashMap<String, Object>>();
        ArrayList<HashMap<String, Object>> arrayExtra = new ArrayList<HashMap<String, Object>>();
        HashMap<String, Object> extra = new HashMap<String, Object>();
        HashMap<String, String> userDat = new HashMap<String, String>();
        
        //Decodificar id de usuario
        Integer id_usuario = Integer.parseInt(Base64Coder.decodeString(id_user));
        userDat = this.getHomeDao().getUserById(id_usuario);
        Integer id_empresa = Integer.parseInt(userDat.get("empresa_id"));
        Integer id_sucursal = Integer.parseInt(userDat.get("sucursal_id"));
        
        if( identificador!=0  ){
            datos = this.getFacdao().getFacNomina_Datos(identificador);
            datosGrid = this.getFacdao().getFacNomina_Grid(identificador);
        }else{
            //Aquí solo entra cuando es nuevo
            //Obtiene los datos del Emisor y los almacena en el HashMap estra
            extra = this.getFacdao().getFacNomina_DatosEmisor(id_empresa);
            
            /*
            Es necesario conocer el id que tomará el registro antes guardar, 
            para eso se utiliza el metodo getIdSeqFacNomina para apartar el siguiente id de la secuencia,
            esto porque la nomina se de cada empleado se guardará uno por uno cada vez que se editen los datos y al guardar ya tenemos que conocer el id de la tabla header(fac_nomina).
            */
            //Agregar el nuevo ID para la tabla fac_nomina
            extra.put("identificador", this.getFacdao().getIdSeqFacNomina());
            
            arrayExtra.add(extra);
        }
        
        //Obtiene parametros
        parametros = this.getFacdao().getFacNomina_Parametros(id_empresa, id_sucursal);
        
        jsonretorno.put("Datos", datos);
        jsonretorno.put("datosGrid", datosGrid);
        jsonretorno.put("Monedas", this.getFacdao().getFactura_Monedas());
        jsonretorno.put("MetodosPago", this.getFacdao().getMetodosPago(id_empresa));
        jsonretorno.put("Periodicidad", this.getFacdao().getFacNomina_PeriodicidadPago(id_empresa));
        jsonretorno.put("Puestos", this.getFacdao().getFacNomina_Puestos(id_empresa));
        jsonretorno.put("Deptos", this.getFacdao().getFacNomina_Departamentos(id_empresa));
        jsonretorno.put("RegimenContrato", this.getFacdao().getFacNomina_RegimenContratacion());
        jsonretorno.put("TipoContrato",this.getFacdao().getFacNomina_TiposContrato());
        jsonretorno.put("TipoJornada",this.getFacdao().getFacNomina_TiposJornada());
        jsonretorno.put("Riesgos",this.getFacdao().getFacNomina_RiesgosPuesto());
        jsonretorno.put("Bancos",this.getFacdao().getFacNomina_Bancos(id_empresa));
        jsonretorno.put("ImpuestoRet",this.getFacdao().getFacNomina_ISR(id_empresa));
        jsonretorno.put("TiposHrsExtra",this.getFacdao().getFacNomina_TiposHoraExtra());
        jsonretorno.put("TiposIncapacidad",this.getFacdao().getFacNomina_TiposIncapacidad());
        
        //Solo debe obtener percepciones y deducciones de la Empresa sin tomar en cuenta el empleado
        Integer tipo=1;
        
        //Al enviar los primeros dos parametros en cero, solo obtiene las Dercepciones de la empresa sin filtrar por empleado
        jsonretorno.put("Percepciones",this.getFacdao().getFacNomina_Percepciones(tipo, 0,0, id_empresa));
        
        //Al enviar los primeros dos parametros en cero, solo obtiene las Deducciones de la empresa sin filtrar por empleado
        jsonretorno.put("Deducciones",this.getFacdao().getFacNomina_Deducciones(tipo,0,0, id_empresa));
        
        jsonretorno.put("Par", parametros);
        jsonretorno.put("Extra", arrayExtra);
        
        return jsonretorno;
    }
 
    //Obtiene todos los empleados que se les paga en el periodo indicado
    @RequestMapping(method = RequestMethod.POST, value="/getEmpleados.json")
    public @ResponseBody HashMap<String,ArrayList<HashMap<String, Object>>> getEmpleadosJson(
            @RequestParam(value="id", required=true) Integer periodicidad_id,
            @RequestParam(value="iu", required=true) String id_user,
            Model model
        ) {
        HashMap<String,ArrayList<HashMap<String, Object>>> jsonretorno = new HashMap<String,ArrayList<HashMap<String, Object>>>();
        HashMap<String, String> userDat = new HashMap<String, String>();
        
        //Decodificar id de usuario
        Integer id_usuario = Integer.parseInt(Base64Coder.decodeString(id_user));
        userDat = this.getHomeDao().getUserById(id_usuario);
        Integer id_empresa = Integer.parseInt(userDat.get("empresa_id"));
        
        jsonretorno.put("Empleados", this.getFacdao().getFacNomina_Empleados(id_empresa, periodicidad_id));
        
        return jsonretorno;
    }

    //Obtiene todos los periodos de un Tipo de Periodicidad
    @RequestMapping(method = RequestMethod.POST, value="/getPeriodosPorTipoPeridicidad.json")
    public @ResponseBody HashMap<String,ArrayList<HashMap<String, Object>>> getPeriodosPorTipoPeridicidadJson(
            @RequestParam(value="id", required=true) Integer periodicidad_id,
            @RequestParam(value="identificador", required=true) Integer identificador,
            @RequestParam(value="iu", required=true) String id_user,
            Model model
        ) {
        HashMap<String,ArrayList<HashMap<String, Object>>> jsonretorno = new HashMap<String,ArrayList<HashMap<String, Object>>>();
        HashMap<String, String> userDat = new HashMap<String, String>();
        
        //Decodificar id de usuario
        Integer id_usuario = Integer.parseInt(Base64Coder.decodeString(id_user));
        userDat = this.getHomeDao().getUserById(id_usuario);
        Integer id_empresa = Integer.parseInt(userDat.get("empresa_id"));
        
        jsonretorno.put("Periodos", this.getFacdao().getFacNomina_PeriodosPorTipo(periodicidad_id, id_empresa, identificador));
        
        return jsonretorno;
    }

    //Obtiene datos de la Nomina de un Empleado
    @RequestMapping(method = RequestMethod.POST, value="/getDataNominaEmpleado.json")
    public @ResponseBody HashMap<String,ArrayList<HashMap<String, Object>>> getDataNominaEmpleadoJson(
            @RequestParam(value="identificador", required=true) Integer identificador,
            @RequestParam(value="accion", required=true) String accion,
            @RequestParam(value="id_reg", required=true) Integer id_nom_det,
            @RequestParam(value="id_empleado", required=true) Integer id_empleado,
            @RequestParam(value="id_periodo", required=true) Integer id_periodo,
            @RequestParam(value="iu", required=true) String id_user,
            Model model
        ) {
        HashMap<String,ArrayList<HashMap<String, Object>>> jsonretorno = new HashMap<String,ArrayList<HashMap<String, Object>>>();
        HashMap<String, String> userDat = new HashMap<String, String>();
        ArrayList<HashMap<String, Object>> datos = new ArrayList<HashMap<String, Object>>();
        
        //Decodificar id de usuario
        Integer id_usuario = Integer.parseInt(Base64Coder.decodeString(id_user));
        userDat = this.getHomeDao().getUserById(id_usuario);
        Integer id_empresa = Integer.parseInt(userDat.get("empresa_id"));
        Integer tipo=0;
        
        if(id_nom_det!=0){
            //Editar. Obtener datos de tabla de Nomina
            datos = this.getFacdao().getFacNomina_DataNomina(id_nom_det, id_empleado);
            if(datos.get(0).get("validado").equals("true")){
                jsonretorno.put("Data", datos);
                jsonretorno.put("HrsExtraEmpleado", this.getFacdao().getFacNomina_HorasExtras(id_nom_det));
                jsonretorno.put("IncapaEmpleado", this.getFacdao().getFacNomina_Incapacidades(id_nom_det));
                //Tipo=3, Obtener las Percepciones y Deducciones de la Nomina de un Periodo en especifico segun el id_reg
                tipo=3;
            }else{
                //Aqui se busca los datos del empleado porque solo existe el registro sin datos
                jsonretorno.put("Data", this.getFacdao().getFacNomina_DataEmpleado(id_empleado));
                jsonretorno.put("Periodo", this.getFacdao().getFacNomina_DataPeriodo(id_periodo, id_empresa));
                //Tipo=2, Obtener las Percepciones y Deducciones configuradas en el catalogo de empleados
                tipo=2;
            }
        }else{
            //Nuevo. Obtener datos de tabla de empleados
            jsonretorno.put("Data", this.getFacdao().getFacNomina_DataEmpleado(id_empleado));
            jsonretorno.put("Periodo", this.getFacdao().getFacNomina_DataPeriodo(id_periodo, id_empresa));
            //Tipo=2, Obtener las Percepciones y Deducciones configuradas en el catalogo de empleados
            tipo=2;
        }
        
        jsonretorno.put("PercepEmpleado", this.getFacdao().getFacNomina_Percepciones(tipo, id_nom_det, id_empleado, id_empresa));
        jsonretorno.put("DeducEmpleado", this.getFacdao().getFacNomina_Deducciones(tipo, id_nom_det, id_empleado, id_empresa));
        
        return jsonretorno;
    }

 
    //Edicion y nuevo
    @RequestMapping(method = RequestMethod.POST, value="/edit.json")
    public @ResponseBody HashMap<String, String> editJson(
            @RequestParam(value="accion", required=true) String accion,
            @RequestParam(value="nivel_ejecucion", required=true) String nivel_ejecucion,
            @RequestParam(value="identificador", required=true) Integer identificador,
            @RequestParam(value="comp_tipo", required=true) String comp_tipo,
            @RequestParam(value="comp_forma_pago", required=true) String comp_forma_pago,
            @RequestParam(value="comp_tc", required=true) String comp_tc,
            @RequestParam(value="comp_no_cuenta", required=true) String comp_no_cuenta,
            @RequestParam(value="fecha_pago", required=true) String fecha_pago,
            @RequestParam(value="select_comp_metodo_pago", required=true) String select_comp_metodo_pago,
            @RequestParam(value="select_comp_moneda", required=true) String select_comp_moneda,
            @RequestParam(value="select_comp_periodicidad", required=true) String select_comp_periodicidad,
            @RequestParam(value="select_no_periodo", required=true) String select_no_periodo,
            @RequestParam(value="generar", required=true) String generar,
            @RequestParam(value="id_generar", required=false) String id_generar,
            @RequestParam(value="elim", required=false) String[] elim,
            @RequestParam(value="id_reg", required=false) String[] id_reg,
            @RequestParam(value="id_emp", required=false) String[] id_empleado,
            @RequestParam(value="tpercep", required=false) String[] tpercep,
            @RequestParam(value="tdeduc", required=false) String[] tdeduc,
            @RequestParam(value="pago_neto", required=false) String[] pago_neto,
            
            @RequestParam(value="noTr", required=false) String[] noTr,
            @ModelAttribute("user") UserSessionData user
        ) throws Exception {
        
        HashMap<String, String> jsonretorno = new HashMap<String, String>();
        HashMap<String, String> userDat = new HashMap<String, String>();
        HashMap<String, String> succes = new HashMap<String, String>();
        HashMap<String, String> parametros = new HashMap<String, String>();
        HashMap<String, String> parametrosEmpresa = new HashMap<String, String>();
        
        ArrayList<HashMap<String,Object>> registros = new ArrayList<HashMap<String,Object>>();
        HashMap<String,String> dataFactura = new HashMap<String,String>();
        ArrayList<LinkedHashMap<String,String>> conceptos = new ArrayList<LinkedHashMap<String,String>>();
        ArrayList<LinkedHashMap<String,String>> impTrasladados = new ArrayList<LinkedHashMap<String,String>>();
        ArrayList<LinkedHashMap<String,String>> impRetenidos = new ArrayList<LinkedHashMap<String,String>>();
        
        LinkedHashMap<String,String> datosExtrasXmlFactura = new LinkedHashMap<String,String>();
        LinkedHashMap<String,Object> dataAdenda = new LinkedHashMap<String,Object>();
        ArrayList<HashMap<String, String>> ieps = new ArrayList<HashMap<String, String>>();
        ArrayList<HashMap<String, String>> iva = new ArrayList<HashMap<String, String>>();
        LinkedHashMap<String,String> datosExtrasCfdi = new LinkedHashMap<String,String>();
        ArrayList<LinkedHashMap<String,String>> listaConceptosCfdi = new ArrayList<LinkedHashMap<String,String>>();
        ArrayList<LinkedHashMap<String,String>> impTrasladadosCfdi = new ArrayList<LinkedHashMap<String,String>>();
        ArrayList<LinkedHashMap<String,String>> impRetenidosCfdi = new ArrayList<LinkedHashMap<String,String>>();
        ArrayList<String> leyendas = new ArrayList<String>();
        
        HashMap<String,String> datos_emisor = new HashMap<String,String>();
        ArrayList<HashMap<String, String>> listaConceptosPdfCfd = new ArrayList<HashMap<String, String>>();
        HashMap<String, String> datosExtrasPdfCfd= new HashMap<String, String>();
        
        String codeRespuesta="0";
        String valorRespuesta="false";
        String msjRespuesta="";
        String cfdis_generados="";
        String cfdis_no_generados="";
        String retorno="";
        String tipo_facturacion="";
        String folio="";
        String serieFolio="";
        String rfcEmisor="";
        
        //Nomina
        Integer app_selected = 173;
        String command_selected = "";
        String succes_validation="";
        boolean actualizar_registro=true;
        String actualizo = "0";
        
        //Variable para el id  del usuario
        Integer id_usuario= user.getUserId();
        userDat = this.getHomeDao().getUserById(id_usuario);
        Integer id_empresa = Integer.parseInt(userDat.get("empresa_id"));
        Integer id_sucursal = Integer.parseInt(userDat.get("sucursal_id"));
        
        if(id_generar==null){id_generar="0"; }
        
        String arreglo[];
        arreglo = new String[elim.length];
        
        for(int i=0; i<elim.length; i++) {
            arreglo[i]= "'"+elim[i] +"___" + noTr[i] +"___" + id_reg[i] +"___" + id_empleado[i] +"___"+ tpercep[i] +"___"+ tdeduc[i] +"___"+ pago_neto[i] +"'";
            //System.out.println(arreglo[i]);
        }
        
        //Serializar el arreglo
        String extra_data_array = StringUtils.join(arreglo, ",");
        
        command_selected = accion.trim().toLowerCase();
        
        String data_string = app_selected+"___"+command_selected+"___"+id_usuario+"___"+identificador+"___"+comp_tipo.toUpperCase()+"___"+comp_forma_pago.toUpperCase()+"___"+comp_tc+"___"+comp_no_cuenta+"___"+fecha_pago+"___"+select_comp_metodo_pago+"___"+select_comp_moneda+"___"+select_comp_periodicidad+"___"+select_no_periodo+"___"+id_generar;
        //System.out.println("data_string: "+data_string);
        
        if(nivel_ejecucion.equals("2") && accion.equals("edit")){
            //Si el nivel de ejecucion es 2 y la accion es edit, no se tiene que actualizar el registro
            actualizar_registro=false;
        }
        
        if(actualizar_registro){
            if(command_selected.equals("new")){
                //Cuando es Nuevo, pasa sin validar
                succes_validation = "true";
            }else{
                //Cuando es diferente de Nuevo, se tiene que validar
                succes = this.getFacdao().selectFunctionValidateAaplicativo(data_string,app_selected,extra_data_array);
                succes_validation = succes.get("success");
                log.log(Level.INFO, TimeHelper.getFechaActualYMDH()+"Despues de validacion {0}", String.valueOf(succes.get("success")));
            }
            
            //System.out.println(TimeHelper.getFechaActualYMDH()+": Inicia actualizacion de datos de la prefactura");
            if( String.valueOf(succes_validation).equals("true")){
                retorno = this.getFacdao().selectFunctionForFacAdmProcesos(data_string, extra_data_array);
                //Retorna un 1, si se  actualizo correctamente
                actualizo=retorno.split(":")[0];
                jsonretorno.put("actualizo",String.valueOf(actualizo));
                
                succes_validation="true";
                codeRespuesta="0";
                msjRespuesta="El registro se actualizo.";
            }
        }else{
            succes_validation="true";
            codeRespuesta="0";
            msjRespuesta="No se actualizo el registro.";
        }
        
        
        
        
        
        //System.out.println(TimeHelper.getFechaActualYMDH()+"::Termina Actualizacion de la Prefactura:: "+actualizo);
        
        if(actualizo.equals("1")){
            if ( generar.toLowerCase().trim().equals("true") ){
                
                    System.out.println(TimeHelper.getFechaActualYMDH()+"::::::::::::Iniciando Facturacion NOMINA:::::::::::::::::..");
                    String proposito = "NOMINA";
                    
                    parametrosEmpresa = this.getFacdao().getParametrosEmpresa(id_empresa);
                    tipo_facturacion = parametrosEmpresa.get("tipo_facturacion");
                    //Numero del PAC para el Timbrado de la Factura
                    String noPac = parametrosEmpresa.get("pac_facturacion");
                    //Ambiente de Facturacion PRUEBAS ó PRODUCCION, solo aplica para Facturacion por Timbre FIscal(cfditf)
                    String ambienteFac = parametrosEmpresa.get("ambiente_facturacion");
                    
                    //System.out.println(TimeHelper.getFechaActualYMDH()+"::::::Tipo::"+tipo_facturacion+" | noPac::"+noPac+" | Ambiente::"+ambienteFac);
                    
                    //Aqui se obtienen los parametros de la facturacion, nos intersa el tipo de formato para el pdf de la factura
                    parametros = this.getFacdao().getFac_Parametros(id_empresa, id_sucursal);
                    
                    //**********************************************************
                    //tipo facturacion CFDITF(CFDI TIMBRE FISCAL)
                    //**********************************************************
                    if(tipo_facturacion.equals("cfditf")){
                        
                        //Pac 0=Sin PAC, 1=Diverza, 2=ServiSim
                        if(!noPac.equals("0")){
                            //Solo se permite generar Factura para Timbrado con Diverza y ServiSim
                            //System.out.println(TimeHelper.getFechaActualYMDH()+":::::::::::Obteniendo datos para CFDI:::::::::::::::::..");
                            command_selected = "facturar_nomina";
                            extra_data_array = "'sin datos'";
                            
                            //Obtener los valores del IEPS e IVAque se estan utilizando
                            //ieps = this.getFacdao().getIeps(id_empresa);
                            //iva = this.getFacdao().getIvas();
                            
                            
                            //Obtiene los registros de nomina que se deben facturar
                            registros = this.getFacdao().getFacNomina_Registros(identificador);
                            
                            if(registros.size()>0){
                                for( HashMap<String,Object> i : registros ){
                                    ArrayList<LinkedHashMap<String,String>> percepciones = new ArrayList<LinkedHashMap<String,String>>();
                                    ArrayList<LinkedHashMap<String,String>> deducciones = new ArrayList<LinkedHashMap<String,String>>();
                                    ArrayList<LinkedHashMap<String,String>> incapacidades = new ArrayList<LinkedHashMap<String,String>>();
                                    ArrayList<LinkedHashMap<String,String>> hrs_extras = new ArrayList<LinkedHashMap<String,String>>();
                                    
                                    Integer id = Integer.parseInt(String.valueOf(i.get("id_reg")));
                                    Integer empleado_id = Integer.parseInt(String.valueOf(i.get("empleado_id")));
                                    
                                    //Obtener datos para el Comprobante, Receptor, Nomina
                                    dataFactura = this.getFacdao().getFacNomina_DataXml(id, empleado_id);
                                    
                                    //Obtener fecha del sistema
                                    String fecha = TimeHelper.getFechaActualYMDH();
                                    String[] fecha_hora = fecha.split(" ");
                                    //formato fecha: 2011-03-01T00:00:00
                                    dataFactura.put("comprobante_attr_fecha",fecha_hora[0]+"T"+fecha_hora[1]);
                                    
                                    //Estos son requeridos para cfditf
                                    //datosExtrasXmlFactura.put("prefactura_id", "");
                                    //datosExtrasXmlFactura.put("tipo_documento", "");
                                    //datosExtrasXmlFactura.put("moneda_id", "");
                                    //datosExtrasXmlFactura.put("refacturar", "false");
                                    datosExtrasXmlFactura.put("id", String.valueOf(id));
                                    datosExtrasXmlFactura.put("empleado_id", String.valueOf(empleado_id));
                                    datosExtrasXmlFactura.put("usuario_id", String.valueOf(id_usuario));
                                    datosExtrasXmlFactura.put("empresa_id", String.valueOf(id_empresa));
                                    datosExtrasXmlFactura.put("sucursal_id", String.valueOf(id_sucursal));
                                    datosExtrasXmlFactura.put("app_selected", String.valueOf(app_selected));
                                    datosExtrasXmlFactura.put("command_selected", command_selected);
                                    datosExtrasXmlFactura.put("extra_data_array", extra_data_array);
                                    datosExtrasXmlFactura.put("noPac", noPac);
                                    datosExtrasXmlFactura.put("ambienteFac", ambienteFac);
                                    
                                    conceptos = this.getFacdao().getFacNomina_ConceptosXml(id, empleado_id);
                                    impRetenidos = this.getFacdao().getFacNomina_ImpuestosRetenidosXml(id, empleado_id);
                                    percepciones = this.getFacdao().getFacNomina_PercepcionesXml(id);
                                    deducciones = this.getFacdao().getFacNomina_DeduccionesXml(id);
                                    incapacidades = this.getFacdao().getFacNomina_IncapacidadesXml(id);
                                    hrs_extras = this.getFacdao().getFacNomina_HorasExtrasXml(id);
                                    
                                    //System.out.println(TimeHelper.getFechaActualYMDH()+":::::::::::Inicia BeanFacturador:::::::::::::::::..");
                                    //Llamada a metodo que inicializa carga de datos para el xml
                                    this.getBfCfdiTf().init(dataFactura, conceptos, impRetenidos, impTrasladados, proposito, datosExtrasXmlFactura, id_empresa, id_sucursal, percepciones, deducciones, incapacidades, hrs_extras);
                                    //Llamada a metodo que costruye, sella y timbra el xml
                                    String timbrado_correcto = this.getBfCfdiTf().start();
                                    
                                    //System.out.println(TimeHelper.getFechaActualYMDH()+":::::::::::Termina BeanFacturador:::::::::::::::::..");
                                    String cadRes[] = timbrado_correcto.split("___");
                                    
                                    //Aqui se checa si el xml fue validado correctamente
                                    //Si fue correcto debe traer un valor "true", de otra manera trae un error y por lo tanto no se genera el pdf
                                    if(cadRes[0].equals("true")){
                                        //Obtiene serie_folio del CFDI de Nomina que se acaba de guardar
                                        serieFolio = this.getFacdao().getFacNomina_RefId(id).get("serie_folio");
                                        cfdis_generados += "[" + dataFactura.get("comprobante_receptor_attr_nombre") + "] = " + serieFolio+"<br>";
                                    }else{
                                        valorRespuesta="false";
                                        codeRespuesta="7001";
                                        cfdis_no_generados+= "[" + dataFactura.get("comprobante_receptor_attr_nombre") + "] = " + cadRes[1]+"<br>";
                                    }
                                }
                                
                                codeRespuesta="0";
                                valorRespuesta="true";
                                if(!cfdis_generados.equals("")){
                                    msjRespuesta += "CFDI de N&oacute;mina Generedos:<br>"+cfdis_generados+"<br><br>";
                                }
        
                                if(!cfdis_no_generados.equals("")){
                                    msjRespuesta += "No Generados:<br>"+cfdis_no_generados+"<br>";
                                }
                                
                            }else{
                                codeRespuesta="7001";
                                valorRespuesta="false";
                                msjRespuesta = "No hay registros de empleados configurados para generar N&oacute;mina.";
                            }
                        }else{
                            valorRespuesta="false";
                            codeRespuesta="7001";
                            msjRespuesta="No se puede Timbrar la N&oacute;mina con el PAC actual.\nVerifique la configuraci&oacute;n del tipo de Facturaci&oacute;n y del PAC.";
                        }
                    }
            }
            
            System.out.println("Folio: "+ String.valueOf(jsonretorno.get("folio")));
            
            
            
        }else{
            if(actualizo.equals("0")){
                jsonretorno.put("actualizo",String.valueOf(actualizo));
            }
        }
        
        jsonretorno.put("success",succes_validation);
        jsonretorno.put("valor",valorRespuesta);
        jsonretorno.put("msj",msjRespuesta);
        
        System.out.println("Validacion: "+ String.valueOf(jsonretorno.get("success")) + " | codeRespuesta: "+String.valueOf(codeRespuesta) + " | "+"valorRespuesta: "+String.valueOf(valorRespuesta)+ " | "+"msjRespuesta: "+String.valueOf(msjRespuesta));
        //System.out.println("Actualizo: "+String.valueOf(jsonretorno.get("actualizo")));
        
        //System.out.println(TimeHelper.getFechaActualYMDH()+": FIN------------------------------------");
        
        return jsonretorno;
    }
    
    
    
    
    
    
    //obtiene los tipos de cancelacion
    @RequestMapping(method = RequestMethod.POST, value="/getVerificaArchivo.json")
    public @ResponseBody HashMap<String,String> getVerificaArchivoGeneradoJson(
            @RequestParam(value="id_reg", required=true) Integer id,
            @RequestParam(value="ext", required=true) String extension,
            @RequestParam(value="iu", required=true) String id_user_cod,
            Model model
            ) {
        
        log.log(Level.INFO, "Ejecutando getVerificaArchivoGeneradoJson de {0}", FacNominaController.class.getName());
        HashMap<String, String> jsonretorno = new HashMap<String,String>();
        HashMap<String, String> userDat = new HashMap<String, String>();
        HashMap<String, String> data = new HashMap<String, String>();
        String existe ="false";
        String dirSalidas = "";
        
        //decodificar id de usuario
        Integer id_usuario = Integer.parseInt(Base64Coder.decodeString(id_user_cod));
        userDat = this.getHomeDao().getUserById(id_usuario);
        Integer id_empresa = Integer.parseInt(userDat.get("empresa_id"));
        
        String tipo_facturacion = this.getFacdao().getTipoFacturacion(id_empresa);
        data = this.getFacdao().getFacNomina_RefId(id);
        
        String nombre_archivo = data.get("ref_id");
        String serie_folio = data.get("serie_folio");
        
        if(tipo_facturacion.equals("cfditf")){
            dirSalidas = this.getGralDao().getCfdiTimbreEmitidosDir() + this.getGralDao().getRfcEmpresaEmisora(id_empresa) + "/nomina";
        }
        
        String fileout = dirSalidas +"/"+ nombre_archivo +"."+extension;
        
        System.out.println("Ruta: " + fileout);
        File file = new File(fileout);
        if (file.exists()){
            existe="true";
        }
        
        jsonretorno.put("descargar", existe);
        
        return jsonretorno;
    }
    
    
    
    //Descarga xml de factura
    @RequestMapping(value = "/getXml/{id_reg}/{iu}/out.json", method = RequestMethod.GET ) 
    public ModelAndView getDescargaXmlFacturaJson(
            @PathVariable("id_reg") Integer id_reg, 
            @PathVariable("iu") String id_user,
            HttpServletRequest request, 
            HttpServletResponse response, 
            Model model) throws ServletException, IOException, URISyntaxException {
        
        HashMap<String, String> userDat = new HashMap<String, String>();
        HashMap<String, String> data = new HashMap<String, String>();
        String dirSalidas = "";
        String nombre_archivo = "";
        
        //decodificar id de usuario
        Integer id_usuario = Integer.parseInt(Base64Coder.decodeString(id_user));
        
        userDat = this.getHomeDao().getUserById(id_usuario);
        Integer id_empresa = Integer.parseInt(userDat.get("empresa_id"));
        
        String tipo_facturacion = this.getFacdao().getTipoFacturacion(id_empresa);
        data = this.getFacdao().getFacNomina_RefId(id_reg);
        nombre_archivo = data.get("ref_id");
        String serie_folio = data.get("serie_folio");
        
        if(tipo_facturacion.equals("cfditf")){
            dirSalidas = this.getGralDao().getCfdiTimbreEmitidosDir() + this.getGralDao().getRfcEmpresaEmisora(id_empresa) + "/nomina";
        }
        
        //Ruta completa del archivo a descargar
        String fileout = dirSalidas + "/" + nombre_archivo +".xml";
        
        System.out.println("Recuperando archivo: " + fileout);
        File file = new File(fileout);
        
        if (file.exists()){
            int size = (int) file.length(); // Tamaño del archivo
            BufferedInputStream bis = new BufferedInputStream(new FileInputStream(file));
            response.setBufferSize(size);
            response.setContentLength(size);
            response.setContentType("text/plain");
            response.setHeader("Content-Disposition","attachment; filename=\"" + file.getName() +"\"");
            FileCopyUtils.copy(bis, response.getOutputStream());  	
            response.flushBuffer();
            
        }
        return null;
    }
    
    
    
    
    
    
    
    //Edicion y nuevo de Nomina de Empleado
    @RequestMapping(method = RequestMethod.POST, value="/edit_nomina_empleado.json")
    public @ResponseBody HashMap<String, String> editNominaEmpleadoJson(
            @RequestParam(value="identificador", required=true) Integer identificador,
            @RequestParam(value="id_reg", required=true) String id_reg,
            @RequestParam(value="id_empleado", required=true) Integer id_empleado,
            @RequestParam(value="no_empleado", required=true) String no_empleado,
            @RequestParam(value="rfc_empleado", required=true) String rfc_empleado,
            @RequestParam(value="nombre_empleado", required=true) String nombre_empleado,
            @RequestParam(value="select_departamento", required=true) String select_departamento,
            @RequestParam(value="select_puesto", required=true) String select_puesto,
            @RequestParam(value="fecha_contrato", required=true) String fecha_contrato,
            @RequestParam(value="antiguedad", required=true) String antiguedad,
            @RequestParam(value="curp", required=true) String curp,
            @RequestParam(value="select_reg_contratacion", required=true) String select_reg_contratacion,
            @RequestParam(value="select_tipo_contrato", required=true) String select_tipo_contrato,
            @RequestParam(value="select_tipo_jornada", required=false) String select_tipo_jornada,
            @RequestParam(value="select_preriodo_pago", required=false) String select_preriodo_pago,
            @RequestParam(value="clabe", required=false) String clabe,
            @RequestParam(value="select_banco", required=false) String select_banco,
            @RequestParam(value="select_riesgo_puesto", required=false) String select_riesgo_puesto,
            @RequestParam(value="imss", required=false) String imss,
            @RequestParam(value="reg_patronal", required=false) String reg_patronal,
            @RequestParam(value="salario_base", required=false) String salario_base,
            @RequestParam(value="fecha_ini_pago", required=false) String fecha_ini_pago,
            @RequestParam(value="fecha_fin_pago", required=false) String fecha_fin_pago,
            @RequestParam(value="salario_integrado", required=false) String salario_integrado,
            @RequestParam(value="no_dias_pago", required=false) String no_dias_pago,
            @RequestParam(value="concepto_descripcion", required=false) String concepto_descripcion,
            @RequestParam(value="concepto_unidad", required=false) String concepto_unidad,
            @RequestParam(value="concepto_cantidad", required=false) String concepto_cantidad,
            @RequestParam(value="concepto_valor_unitario", required=false) String concepto_valor_unitario,
            @RequestParam(value="concepto_importe", required=false) String concepto_importe,
            @RequestParam(value="descuento", required=false) String descuento,
            @RequestParam(value="motivo_descuento", required=false) String motivo_descuento,
            @RequestParam(value="select_impuesto_retencion", required=false) String select_impuesto_retencion,
            @RequestParam(value="importe_retencion", required=false) String importe_retencion,
            @RequestParam(value="comp_subtotal", required=false) String comp_subtotal,
            @RequestParam(value="comp_descuento", required=false) String comp_descuento,
            @RequestParam(value="comp_retencion", required=false) String comp_retencion,
            @RequestParam(value="comp_total", required=false) String comp_total,
            @RequestParam(value="percep_total_gravado", required=false) String percep_total_gravado,
            @RequestParam(value="percep_total_excento", required=false) String percep_total_excento,
            @RequestParam(value="deduc_total_gravado", required=false) String deduc_total_gravado,
            @RequestParam(value="deduc_total_excento", required=false) String deduc_total_excento,
            @RequestParam(value="percepciones", required=false) String percepciones,
            @RequestParam(value="deducciones", required=false) String deducciones,
            @RequestParam(value="hrs_extras", required=false) String hrs_extras,
            @RequestParam(value="incapacidades", required=false) String incapacidades,
            @RequestParam(value="accion", required=false) String accion,
            @RequestParam(value="iu", required=true) String id_user,
            Model model
        ) throws Exception {
        
        System.out.println(TimeHelper.getFechaActualYMDH()+": INICIO-GUADAR NOMINA EMPLEADO------------------------------------");
        HashMap<String, String> jsonretorno = new HashMap<String, String>();
        HashMap<String, String> userDat = new HashMap<String, String>();
        HashMap<String, String> succes = new HashMap<String, String>();
        HashMap<String, String> parametros = new HashMap<String, String>();
        ArrayList<HashMap<String,Object>> extra = new ArrayList<HashMap<String,Object>>();

        
        String retorno="";
        String tipo_facturacion="";
        String folio="";
        String serieFolio="";
        String rfcEmisor="";
        
        //Nomina
        Integer app_selected = 173;
        String command_selected = "new_nomina";
        String extra_data_array = "'sin datos'";
        String succes_validation="";
        String codeRespuesta="";
        String msjRespuesta="";
        String actualizo = "0";
        
        
        //Decodificar id de usuario
        Integer id_usuario = Integer.parseInt(Base64Coder.decodeString(id_user));
        userDat = this.getHomeDao().getUserById(id_usuario);
        Integer id_empresa = Integer.parseInt(userDat.get("empresa_id"));
        Integer id_sucursal = Integer.parseInt(userDat.get("sucursal_id"));
        
        
        
        
        if(Integer.parseInt(id_reg)>0){
            command_selected = "edit_nomina";
        }else{
            //Obtener el id del registro si es que ya existe
            extra = this.getFacdao().getFacNomina_IdNomimaDet(identificador, id_empleado);
            if(extra.size()>0){
                id_reg = String.valueOf(extra.get(0).get("id_reg"));
                command_selected = "edit_nomina";
            }
        }
        
        String data_string = 
                app_selected+"___"+
                command_selected+"___"+
                id_usuario+"___"+
                identificador+"___"+
                id_reg+"___"+
                id_empleado+"___"+
                no_empleado+"___"+
                rfc_empleado+"___"+
                nombre_empleado+"___"+
                select_departamento+"___"+
                select_puesto+"___"+
                fecha_contrato+"___"+
                antiguedad+"___"+
                curp+"___"+
                select_reg_contratacion+"___"+
                select_tipo_contrato+"___"+
                select_tipo_jornada+"___"+
                select_preriodo_pago+"___"+
                clabe+"___"+
                select_banco+"___"+
                select_riesgo_puesto+"___"+
                imss+"___"+
                reg_patronal+"___"+
                salario_base+"___"+
                fecha_ini_pago+"___"+
                fecha_fin_pago+"___"+
                salario_integrado+"___"+
                no_dias_pago+"___"+
                concepto_descripcion+"___"+
                concepto_unidad+"___"+
                concepto_cantidad+"___"+
                concepto_valor_unitario+"___"+
                concepto_importe+"___"+
                descuento+"___"+
                motivo_descuento+"___"+
                select_impuesto_retencion+"___"+
                importe_retencion+"___"+
                comp_subtotal+"___"+
                comp_descuento+"___"+
                comp_retencion+"___"+
                comp_total+"___"+
                percep_total_gravado+"___"+
                percep_total_excento+"___"+
                deduc_total_gravado+"___"+
                deduc_total_excento+"___"+
                percepciones+"___"+
                deducciones+"___"+
                hrs_extras+"___"+
                incapacidades;
        
        System.out.println("data_string_nomina_empleado: "+data_string);
        
        //Cuando es diferente de Nuevo, se tiene que validar
        succes = this.getFacdao().selectFunctionValidateAaplicativo(data_string,app_selected,extra_data_array);
        succes_validation = succes.get("success");
        
        if( String.valueOf(succes_validation).equals("true")){
            retorno = this.getFacdao().selectFunctionForFacAdmProcesos(data_string, extra_data_array);
            
            System.out.println("StringRetorno: "+retorno);
            
            //Retorna un 1, si se  actualizo correctamente
            actualizo = String.valueOf(retorno.split(":")[0]);
            
            //Retorna el id que se creó u actualizó
            jsonretorno.put("id",String.valueOf(retorno.split(":")[1]));
            
            codeRespuesta = String.valueOf(retorno.split(":")[2]);
            msjRespuesta = String.valueOf(retorno.split(":")[3]);
        }else{
            codeRespuesta="7001";
            msjRespuesta ="Error al intentar validar los datos.";
        }
        
        jsonretorno.put("success",succes_validation);
        jsonretorno.put("actualizo",actualizo);
        jsonretorno.put("valor",codeRespuesta);
        jsonretorno.put("msj",msjRespuesta);
        
        System.out.println("Validacion: "+ String.valueOf(jsonretorno.get("success")));
        System.out.println("codeRespuesta: "+String.valueOf(jsonretorno.get("valor")));
        System.out.println("msjRespuesta: "+String.valueOf(jsonretorno.get("msjRespuesta")));
        System.out.println(TimeHelper.getFechaActualYMDH()+": FIN-GUADAR NOMINA EMPLEADO------------------------------------");
        
        return jsonretorno;
    }
    
    
    
    
    
    
    
    //Cancelacion de CFDi de Nomina
    @RequestMapping(method = RequestMethod.POST, value="/getCancelaNomina.json")
    public @ResponseBody HashMap<String, String> getCancelaNominaJson(
            @RequestParam(value="id_reg", required=true) Integer id_reg,
            @RequestParam(value="id_emp", required=true) Integer id_empleado,
            @RequestParam(value="iu", required=true) String id_user,
            Model model
        ) {
        
        HashMap<String, String> userDat = new HashMap<String, String>();
        HashMap<String, String> jsonretorno = new HashMap<String, String>();
        HashMap<String, String> validacion = new HashMap<String, String>();
        HashMap<String, String> data1 = new HashMap<String, String>();
        
        
        //Decodificar id de usuario
        Integer id_usuario = Integer.parseInt(Base64Coder.decodeString(id_user));
        //System.out.println("id_usuario: "+id_usuario);
        userDat = this.getHomeDao().getUserById(id_usuario);
        Integer id_empresa = Integer.parseInt(userDat.get("empresa_id"));
        Integer id_sucursal = Integer.parseInt(userDat.get("sucursal_id"));
        
        //Aplicativo CFDi de Nomina
        Integer app_selected = 173;
        String command_selected = "cancelacion_cfdi_nomina";
        String extra_data_array = "'sin datos'";
        String succcess = "false";
        String nombre_archivo="";
        String serie_folio="";
        String tipo_facturacion="";
        String valorRespuesta="false";
        String msjRespuesta="";
        
        String data_string = app_selected+"___"+command_selected+"___"+id_usuario+"___"+id_reg+"___"+id_empleado;
        
        validacion = this.getFacdao().selectFunctionValidateAaplicativo(data_string,app_selected,extra_data_array);
        
        if(String.valueOf(validacion.get("success")).equals("true")){
            
            tipo_facturacion = this.getFacdao().getTipoFacturacion(id_empresa);
            tipo_facturacion = String.valueOf(tipo_facturacion);
            
            //Obtener el numero del PAC para el Timbrado de la Factura
            String noPac = this.getFacdao().getNoPacFacturacion(id_empresa);
            
            //Obtener el Ambiente de Facturacion PRUEBAS ó PRODUCCION, solo aplica para Facturacion por Timbre FIscal(cfditf)
            String ambienteFac = this.getFacdao().getAmbienteFacturacion(id_empresa);
            
            System.out.println("Tipo::"+tipo_facturacion+" | noPac::"+noPac+" | Ambiente::"+ambienteFac);
            
            
                
            if(tipo_facturacion.equals("cfditf") ){
                try {
                    //Pac 0=Sin PAC, 1=Diverza, 2=ServiSim
                    if(!String.valueOf(noPac).equals("0")){
                        //Solo se permite Cancelar Factura con Diverza y ServiSim
                        data1 = this.getFacdao().getFacNomina_RefId(id_reg);
                        nombre_archivo = data1.get("ref_id");
                        serie_folio = data1.get("serie_folio");


                        //:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
                        //aqui inicia request al webservice
                        //:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
                        String rfcEmpresaEmisora = this.getGralDao().getRfcEmpresaEmisora(id_empresa);
                        String ruta_ejecutable_java = this.getGralDao().getJavaVmDir(id_empresa, id_sucursal);
                        String ruta_jarWebService = this.getGralDao().getCfdiTimbreJarWsDir()+"wscli.jar";

                        String rutaCanceladosDir = this.getGralDao().getCfdiTimbreCanceladosDir();
                        String RutaficheroXml = this.getGralDao().getCfdiTimbreEmitidosDir() + rfcEmpresaEmisora +"/nomina/"+ nombre_archivo+".xml";
                        BeanFromCfdiXml pop = new BeanFromCfdiXml(RutaficheroXml);

                        String uuid = pop.getUuid();
                        String emisor_rfc = pop.getEmisor_rfc();
                        String receptor_rfc = pop.getReceptor_rfc();
                        String tipo_peticion = "cancelacfdi";

                        String str_execute="";

                        //Cancelacion con DIVERZA
                        if(String.valueOf(noPac).equals("1")){
                            String ruta_fichero_llave_pfx = this.getGralDao().getSslDir() + rfcEmpresaEmisora+ "/" +this.getGralDao().getFicheroPfxTimbradoCfdi(id_empresa,id_sucursal) ;
                            String password_pfx = this.getGralDao().getPasswdFicheroPfxTimbradoCfdi(id_empresa, id_sucursal);
                            String ruta_java_almacen_certificados = this.getGralDao().getJavaRutaCacerts(id_empresa, id_sucursal);
                            /*
                            //Datos para cancelacion
                            args[0] = PAC proveedor
                            args[1] = tipo de ambiente(pruebas, produccion)
                            args[2] = tipo_peticion
                            args[3] = FicheroPfxTimbradoCfdi
                            args[4] = PasswdFicheroPfxTimbradoCfdi
                            args[5] = JavaVmDir
                            args[6] = getRfc_emisor
                            args[7] = getRfc_receptor
                            args[8] = uuid
                            args[9] = DirCancelados
                            args[10] = serie_folio
                             */

                            //str_execute = ruta_ejecutable_java+" -jar "+ruta_jarWebService+" "+noPac+" "+ambienteFac+" "+tipo_peticion+" "+ruta_fichero_llave_pfx+" "+password_pfx+" "+ruta_java_almacen_certificados+" "+emisor_rfc+" "+receptor_rfc+" "+uuid+" "+rutaCanceladosDir+" "+serie_folio;
                        }

                        //Cancelacion con SERVISIM
                        if(String.valueOf(noPac).equals("2")){

                            /*
                            //Datos para Cancelacion
                            args[0] = PAC proveedor
                            args[1] = tipo de ambiente(pruebas, produccion)
                            args[2] = tipo_peticion(cancelacion, timbrado)
                            args[3] = Usuario
                            args[4] = Password
                            args[5] = uuid
                            args[6] = rfcEmisor
                            args[7] = serieFolio
                            args[8] = dirCancelados
                            */

                            String usuario = this.getGralDao().getUserContrato(id_empresa, id_sucursal);
                            String contrasena = this.getGralDao().getPasswordUserContrato(id_empresa, id_sucursal);

                            //aqui se forma la cadena con los parametros que se le pasan a jar
                            str_execute = ruta_ejecutable_java+" -jar "+ruta_jarWebService+" "+noPac+" "+ambienteFac+" "+tipo_peticion+" "+usuario+" "+contrasena+" "+uuid+" "+emisor_rfc+" "+serie_folio+" "+rutaCanceladosDir;
                        }


                        System.out.println("str_execute: "+str_execute);
                        Process resultado = null; 

                        resultado = Runtime.getRuntime().exec(str_execute);

                        InputStream myInputStream=null;
                        myInputStream= resultado.getInputStream();

                        BufferedReader reader = new BufferedReader(new InputStreamReader(myInputStream));
                        StringBuilder sb = new StringBuilder();
                        String line = null;
                        while ((line = reader.readLine()) != null) {
                            sb.append(line);
                        }
                        myInputStream.close();

                        System.out.println("Resultado: "+sb.toString());
                        String arrayResult[] = sb.toString().split("___");

                        //Toma el valor true o false
                        valorRespuesta = arrayResult[0];

                        //Toma el mensaje
                        msjRespuesta = arrayResult[1];

                        if(String.valueOf(valorRespuesta).equals("true")){
                            succcess = this.getFacdao().selectFunctionForFacAdmProcesos(data_string, extra_data_array);
                            if(String.valueOf(succcess).equals("true")){
                                HashMap<String, String> data = new HashMap<String, String>();
                                //serie_folio = succcess.split(":")[0];
                                //System.out.println("serie_folio:"+serie_folio + "    Cancelado:"+succcess.split(":")[1]);
                            }
                        }

                        jsonretorno.put("success", succcess);
                    }else{
                        valorRespuesta="false";
                        msjRespuesta="No se puede Cancelar el CFDi de la Nomina con el PAC actual.\nVerifique la configuraci&oacute;n del tipo de Facturaci&oacute;n y del PAC.";
                    }
                } catch (IOException ex) {
                    valorRespuesta="false";
                    msjRespuesta="No fue posible Cancelar el CFDi de la Nomina.";
                    Logger.getLogger(FacCancelacionController.class.getName()).log(Level.SEVERE, null, ex);
                }
                //Termina CANCELACION cfditf
            }else{
                jsonretorno.put("success", succcess);
                valorRespuesta="false";
                msjRespuesta="No se ha configurado el tipo de facturacion.";
            }
            
            
            jsonretorno.put("success", String.valueOf(validacion.get("success")));
        }else{
            jsonretorno.put("success", "false");
            valorRespuesta="false";
            String resultValidacion[] = validacion.get("success").split("___");
            msjRespuesta=resultValidacion[1];
        }
        
        System.out.println("valor_retorno:: "+ jsonretorno.get("success"));
        jsonretorno.put("valor",valorRespuesta);
        jsonretorno.put("msj",msjRespuesta);
        
        System.out.println("valorRespuesta: "+String.valueOf(valorRespuesta));
        System.out.println("msjRespuesta: "+String.valueOf(msjRespuesta));
        
        return jsonretorno;
    }
    
    
    
    
  @RequestMapping(value = "/getPDF/{id_reg}/{id_empleado}/{iu}/out.json", method = RequestMethod.GET ) 
    public ModelAndView getGeneraPdfFacturacionJson(
                @PathVariable("id_reg") Integer id,
                @PathVariable("id_empleado") Integer id_empleado,
                @PathVariable("iu") String id_user,
                HttpServletRequest request, 
                HttpServletResponse response, 
                Model model)
            throws ServletException, IOException, URISyntaxException, DocumentException, FileNotFoundException, Exception {
          
        HashMap<String, String> userDat = new HashMap<String, String>();
        HashMap<String,String> datos_nomina = new HashMap<String,String>();
        HashMap<String, String> data = new HashMap<String, String>();
        ArrayList<LinkedHashMap<String,String>> percepciones = new ArrayList<LinkedHashMap<String,String>>();
        ArrayList<LinkedHashMap<String,String>> hrs_extras = new ArrayList<LinkedHashMap<String,String>>();
        ArrayList<LinkedHashMap<String,String>> incapacidades = new ArrayList<LinkedHashMap<String,String>>();
        ArrayList<LinkedHashMap<String,String>> deducciones = new ArrayList<LinkedHashMap<String,String>>();
        
        String generado ="false";
        String rutaXml = "";
        
        //Decodificar id de usuario
        Integer id_usuario = Integer.parseInt(Base64Coder.decodeString(id_user));
        userDat = this.getHomeDao().getUserById(id_usuario);
        Integer id_empresa = Integer.parseInt(userDat.get("empresa_id"));
        Integer id_sucursal = Integer.parseInt(userDat.get("sucursal_id"));
        String rfcEmpresa = this.getGralDao().getRfcEmpresaEmisora(id_empresa);
        
        //Obtiene serie_folio y ref_id de la nomina
        data = this.getFacdao().getFacNomina_RefId(id);
        
        //Ruta de Directorio de Emitidos de CFDi de Nominas
        rutaXml = this.getGralDao().getCfdiTimbreEmitidosDir() + rfcEmpresa + "/nomina/"+ data.get("ref_id") + ".xml";
        
        //Obtener la cadena completa del xml
        String cadena_xml = FileHelper.stringFromFile(rutaXml);
        
        //Obtener la cadena original del timbre
        String cadena_original_timbre = this.getCadenaOriginalTimbre(cadena_xml, id_empresa, id_sucursal);
        
        //Parsear el xml timbrado
        BeanFromCfdiXml pop2 = new BeanFromCfdiXml(rutaXml);
        
        //Obtener datos del comprobante
        String fecha_comprobante=pop2.getFecha_comprobante();
        String sello_digital_emisor = pop2.getSelloCfd();
        String sello_digital_sat = pop2.getSelloSat();
        String uuid = pop2.getUuid();
        String fechaTimbre = pop2.getFecha_timbre();
        String noCertSAT = pop2.getNoCertificadoSAT();
        String rfcReceptor = pop2.getReceptor_rfc();
        
        datos_nomina = this.getFacdao().getFacNomina_DataXml(id, id_empleado);
        datos_nomina.put("serie_folio",data.get("serie_folio"));
        datos_nomina.put("sello_sat", sello_digital_sat);
        datos_nomina.put("uuid", uuid);
        datos_nomina.put("fecha_comprobante", fecha_comprobante);
        datos_nomina.put("fechaTimbre", fechaTimbre);
        datos_nomina.put("noCertificadoSAT", noCertSAT);
        datos_nomina.put("sello_digital_emisor", sello_digital_emisor);
        datos_nomina.put("cadena_original_timbre", cadena_original_timbre);
        datos_nomina.put("leyenda_nomina", String.valueOf(this.getFacdao().getFacNomina_LeyendaReciboNomina(id_empresa, id_sucursal).get("leyenda_nomina")));
        
        percepciones = this.getFacdao().getFacNomina_PercepcionesXml(id);
        deducciones = this.getFacdao().getFacNomina_DeduccionesXml(id);
        incapacidades = this.getFacdao().getFacNomina_IncapacidadesXml(id);
        hrs_extras = this.getFacdao().getFacNomina_HorasExtrasXml(id);
        
        
        System.out.println("::::::::::::Generando PDF de Nomina:::::::::::::::::..");
        
        //String razon_social_empresa = this.getGralDao().getRazonSocialEmpresaEmisora(id_empresa);
        //String rfc_empresa = this.getGralDao().getRfcEmpresaEmisora(id_empresa);
        
        //obtener el directorio temporal
        String dir_tmp = this.getGralDao().getTmpDir();
        //String ruta_imagen = this.getGralDao().getImagesDir()+rfc_empresa+"_logo.png";
        
        File file_dir_tmp = new File(dir_tmp);
        //System.out.println("Directorio temporal: "+file_dir_tmp.getCanonicalPath());
        
        //genera nombre del archivo
        String file_name = data.get("serie_folio") +".pdf";

        //ruta de archivo de salida
        String fileout = file_dir_tmp +"/"+  file_name;

        
        PdfCfdiNomina Pdf = new PdfCfdiNomina(this.getGralDao(), datos_nomina, percepciones, deducciones, hrs_extras, incapacidades, fileout, id_empresa, id_sucursal);
        Pdf.ViewPDF();
  
        System.out.println("Recuperando archivo: " + fileout);
        File file = new File(fileout);
        int size = (int) file.length(); // Tamaño del archivo
        BufferedInputStream bis = new BufferedInputStream(new FileInputStream(file));
        response.setBufferSize(size);
        response.setContentLength(size);
        response.setContentType("application/pdf");
        response.setHeader("Content-Disposition","attachment; filename=\"" + file.getName() +"\"");
        FileCopyUtils.copy(bis, response.getOutputStream());  	
        response.flushBuffer();
        
        FileHelper.delete(fileout);
        
        return null;
    } 

    
    private String getCadenaOriginalTimbre(String comprobante, Integer id_empresa, Integer id_sucursal) throws Exception {
        String valor_retorno = new String();
        //System.out.println("EsquemaXslt: "+this.getGralDao().getXslDir() + this.getGralDao().getRfcEmpresaEmisora(id_empresa)+"/"+ this.getGralDao().getFicheroXslTimbre(id_empresa, id_sucursal));
        valor_retorno = XmlHelper.transformar(comprobante, this.getGralDao().getXslDir() + this.getGralDao().getRfcEmpresaEmisora(id_empresa)+"/"+ this.getGralDao().getFicheroXslTimbre(id_empresa, id_sucursal));
        
        return valor_retorno;
    }
}
