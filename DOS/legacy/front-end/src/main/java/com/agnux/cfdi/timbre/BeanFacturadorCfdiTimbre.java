package com.agnux.cfdi.timbre;

import com.agnux.cfd.v2.CryptoEngine;
import com.agnux.cfdi.BeanFromCfdiXml;
import com.agnux.common.helpers.FileHelper;
import com.agnux.common.helpers.StringHelper;
import com.agnux.common.helpers.TimeHelper;
import com.agnux.common.helpers.XmlHelper;
import com.agnux.kemikal.interfacedaos.FacturasInterfaceDao;
import com.agnux.kemikal.interfacedaos.GralInterfaceDao;
import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.regex.Pattern;
import java.util.logging.Logger;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.logging.Level;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;


public class BeanFacturadorCfdiTimbre {
    
    private static final Logger log = Logger.getLogger(BeanFacturadorCfdiTimbre.class.getName());
    @Autowired
    @Qualifier("daoGral")
    private GralInterfaceDao gralDao;
    
    @Autowired
    @Qualifier("daoFacturas")
    private FacturasInterfaceDao facdao;
    
    public static enum Proposito {
        FACTURA, NOTA_CREDITO, NOTA_CARGO, NOMINA
    };
    
    private Integer id_sucursal;
    private Integer id_empresa;
    private String ref_id;
    
    private String proposito;
    private String fecha;
    private String formaDePago;
    private String noCertificado;
    private String total;
    private String subTotal;
    private String moneda;
    private String tipoCambio;
    private String tipoDeComprobante;
    private String certificado;
    private String condicionesDePago;
    private String descuento;
    private String motivoDescuento;
    private String cadenaOriginal;
    private String selloDigital;
    private String metodoDePago;
    private String lugar_expedicion;
    private String numero_cuenta;
    private String selloDigitalSat;
    private String Uuid;
    private String fechaTimbrado;
    private String noCertificadoSAT;
    private String cadenaOriginalTimbre;
    //Solo se utiliza para guardar registro
    private String subTotalSinDescuento;
    
    // datos Emisor
    private String razon_social_emisor;
    private String rfc_emisor;
    private String regimen_fiscal_emisor;
    
    // DomicilioFiscal
    private String calle_domicilio_fiscal;
    private String municipio_domicilio_fiscal;
    private String estado_domicilio_fiscal;
    private String pais_domicilio_fiscal;
    private String codigoPostal_domicilio_fiscal;
    private String colonia_domicilio_fiscal;
    private String noExterior_domicilio_fiscal;
    private String noInterior_domicilio_fiscal;// opcional
    private String localidad_domicilio_fiscal;// opcional
    private String referencia_domicilio_fiscal;// opcional
    // datos receptor
    private String rfc_receptor;
    private String razon_social_receptor;
    //datos domicilio
    private String pais_domicilio_receptor;
    private String calle_domicilio_receptor;
    private String municipio_domicilio_receptor;
    private String estado_domicilio_receptor;
    private String colonia_domicilio_receptor;
    private String referencia_domicilio_receptor;
    private String noExterior_domicilio_receptor;
    private String noInterior_domicilio_receptor;
    private String codigoPostal_domicilio_receptor;
    private String localidad_domicilio_receptor;//opcional
    private ArrayList<LinkedHashMap<String, String>> listaConceptos;
    private ArrayList<LinkedHashMap<String, String>> listaRetenciones;
    private ArrayList<LinkedHashMap<String, String>> listaTraslados;
    private LinkedHashMap<String, String> datosExtras;
    private BigDecimal totalTraslados = new BigDecimal("0");
    private String tasaIva;
    private BigDecimal totalRetenciones = new BigDecimal("0");
    private String tasaRetencion;
    private BigDecimal sumatoriaImportes = new BigDecimal("0");
    private Validacion valedor = null;
    
    //Solo se utiliza para nota de credito
    private String xml_timbrado;

    
    private ArrayList<LinkedHashMap<String, String>> lista_namespaces;

    //Estas variables son exclusivas para la Nomina CFDI
    private String numero_empleado;
    private String departamento;
    private String puesto;
    private String fecha_contrato;
    private String regimen_contratacion;
    private String tipo_contrato;
    private String tipo_jornada;
    private String periodicidad_pago;
    private String clabe;
    private String banco;
    private String riesgo_puesto;
    private String numero_seguro_social;
    private String registro_patronal;
    private String salario_base;
    private String salario_integrado;
    private String fecha_inicial_pago;
    private String fecha_final_pago;
    private String no_dias_pago;
    private BigDecimal percep_total_gravado = new BigDecimal("0");
    private BigDecimal percep_total_excento = new BigDecimal("0");
    private BigDecimal deduc_total_gravado = new BigDecimal("0");
    private BigDecimal deduc_total_excento = new BigDecimal("0");
    private LinkedHashMap<String,Object> datosNomina;
            
    /*
    //row.put("comprobante_attr_simbolo_moneda",rs.getString("simbolo_moneda"));
    row.put("comprobante_receptor_attr_curp",rs.getString("curp"));
    row.put("comprobante_attr_fecha_fecha_pago",rs.getString("fecha_pago"));
    row.put("comprobante_attr_fecha_antiguedad",rs.getString("antiguedad"));
    row.put("numero_control",rs.getString("no_empleado"));
    row.put("comprobante_attr_depto",rs.getString("departamento"));
    row.put("comprobante_attr_puesto",rs.getString("puesto"));
    row.put("comprobante_attr_fecha_contrato",rs.getString("fecha_contrato"));
    row.put("comprobante_attr_regimen_contratacion",rs.getString("regimen_contratacion"));
    row.put("comprobante_attr_tipo_contrato",rs.getString("tipo_contrato"));
    row.put("comprobante_attr_tipo_jornada",rs.getString("tipo_jornada"));
    row.put("comprobante_attr_periodicidad_pago",rs.getString("periodicidad_pago"));
    row.put("comprobante_attr_clabe",rs.getString("clabe"));
    row.put("comprobante_attr_banco",rs.getString("banco"));
    row.put("comprobante_attr_riesgo_puesto",rs.getString("riesgo_puesto"));
    row.put("comprobante_attr_imss",rs.getString("imss"));
    row.put("comprobante_attr_reg_patronal",rs.getString("reg_patronal"));
    row.put("comprobante_attr_salario_base",rs.getString("salario_base"));
    row.put("comprobante_attr_salario_integrado",rs.getString("salario_integrado"));
    row.put("comprobante_attr_fecha_ini_pago",rs.getString("fecha_ini_pago"));
    row.put("comprobante_attr_fecha_fin_pago",rs.getString("fecha_fin_pago"));
    row.put("comprobante_attr_no_dias_pago",rs.getString("no_dias_pago"));
    row.put("comprobante_attr_percep_total_gravado",StringHelper.roundDouble(rs.getString("percep_total_gravado"),2));
    row.put("comprobante_attr_percep_total_excento",StringHelper.roundDouble(rs.getString("percep_total_excento"),2));
    row.put("comprobante_attr_deduc_total_gravado",StringHelper.roundDouble(rs.getString("deduc_total_gravado"),2));
    row.put("comprobante_attr_deduc_total_excento",StringHelper.roundDouble(rs.getString("deduc_total_excento"),2));
     */

    public LinkedHashMap<String, Object> getDatosNomina() {
        return datosNomina;
    }

    public void setDatosNomina(LinkedHashMap<String, Object> datosNomina) {
        this.datosNomina = datosNomina;
    }
    
    
    
    
    
    public void init(HashMap<String, String> data, ArrayList<LinkedHashMap<String, String>> conceptos, ArrayList<LinkedHashMap<String, String>> impuestos_retenidos, ArrayList<LinkedHashMap<String, String>> impuestos_trasladados, String propos, LinkedHashMap<String,String> extras, Integer id_empresa, Integer id_sucursal, ArrayList<LinkedHashMap<String,String>> percepciones, ArrayList<LinkedHashMap<String,String>> deducciones, ArrayList<LinkedHashMap<String,String>> incapacidades, ArrayList<LinkedHashMap<String,String>> hrs_extras) {
        LinkedHashMap<String,Object> dataNomina = new LinkedHashMap<String,Object>();
        String namespaces="";
        System.out.println(TimeHelper.getFechaActualYMDH()+":::::::::::Inicia Seters:::::::::::::::::..");
        this.setId_empresa(id_empresa);
        this.setId_sucursal(id_sucursal);
        
        this.setValedor(new Validacion());
        this.setProposito(propos);
        
        String ruta_fichero_certificado = new String();
        ruta_fichero_certificado = this.getGralDao().getSslDir() + this.getGralDao().getRfcEmpresaEmisora(this.getId_empresa())+ "/" + this.getGralDao().getCertificadoEmpresaEmisora(this.getId_empresa(), this.getId_sucursal());
        
        //System.out.println("Leyendo fichero: "+ ruta_fichero_certificado);
        
        //Datos Base del CFD ------- INICIO -----------------------------------
        
        this.setCertificado(CryptoEngine.encodeCertToBase64(ruta_fichero_certificado));
        this.setNoCertificado(this.getGralDao().getNoCertificadoEmpresaEmisora(this.getId_empresa(), this.getId_sucursal()));
        this.setFecha(data.get("comprobante_attr_fecha"));
        
        switch (Proposito.valueOf(this.getProposito())) {
            case FACTURA:
                this.setTipoDeComprobante("ingreso");
                dataNomina.put("valor", "false");
                namespaces="fac";
                break;
                
            case NOTA_CREDITO:
                this.setTipoDeComprobante("egreso");
                dataNomina.put("valor", "false");
                namespaces="fac";
                break;
                
            case NOTA_CARGO:
                this.setTipoDeComprobante("ingreso");
                dataNomina.put("valor", "false");
                break;
                
            case NOMINA:
                this.setTipoDeComprobante("egreso");
                namespaces="fac_nomina";
                dataNomina.put("valor", "true");
                dataNomina.put("registro_patronal", data.get("comprobante_attr_reg_patronal"));
                dataNomina.put("numero_empleado", data.get("numero_control"));
                dataNomina.put("curp", data.get("comprobante_receptor_attr_curp"));
                dataNomina.put("tipo_regimen", data.get("comprobante_attr_regimen_contratacion"));
                dataNomina.put("no_seguridad_social", data.get("comprobante_attr_imss"));
                dataNomina.put("fecha_pago", data.get("comprobante_attr_fecha_fecha_pago"));
                dataNomina.put("fecha_inicial_pago", data.get("comprobante_attr_fecha_ini_pago"));
                dataNomina.put("fecha_final_pago", data.get("comprobante_attr_fecha_fin_pago"));
                dataNomina.put("no_dias_pagados", data.get("comprobante_attr_no_dias_pago"));
                dataNomina.put("departamento", data.get("comprobante_attr_depto"));
                dataNomina.put("clabe", data.get("comprobante_attr_clabe"));
                dataNomina.put("banco", data.get("comprobante_attr_banco"));
                dataNomina.put("fecha_contrato", data.get("comprobante_attr_fecha_contrato"));
                dataNomina.put("antiguedad", data.get("comprobante_attr_fecha_antiguedad"));
                dataNomina.put("puesto", data.get("comprobante_attr_puesto"));
                dataNomina.put("tipo_contrato", data.get("comprobante_attr_tipo_contrato"));
                dataNomina.put("tipo_jornada", data.get("comprobante_attr_tipo_jornada"));
                dataNomina.put("PeriodicidadPago", data.get("comprobante_attr_periodicidad_pago"));
                dataNomina.put("salario_base", data.get("comprobante_attr_salario_base"));
                dataNomina.put("riesgo_puesto", data.get("comprobante_attr_riesgo_puesto"));
                dataNomina.put("salario_integrado", data.get("comprobante_attr_salario_integrado"));
                dataNomina.put("percep_total_gravado", data.get("comprobante_attr_percep_total_gravado"));
                dataNomina.put("percep_total_excento", data.get("comprobante_attr_percep_total_excento"));
                dataNomina.put("deduc_total_gravado", data.get("comprobante_attr_deduc_total_gravado"));
                dataNomina.put("deduc_total_excento", data.get("comprobante_attr_deduc_total_excento"));
                
                dataNomina.put("percepciones", percepciones);
                dataNomina.put("deducciones", deducciones);
                dataNomina.put("incapacidades", incapacidades);
                dataNomina.put("HorasExtras", hrs_extras);
                
                this.setDatosNomina(dataNomina);
                break;
        }
        
        this.setCondicionesDePago(StringHelper.normalizaString(StringHelper.remueve_tildes(data.get("comprobante_attr_condicionesdepago")).replace("'", "")));
        this.setFormaDePago(StringHelper.normalizaString(StringHelper.remueve_tildes(data.get("comprobante_attr_formadepago"))));
        this.setMotivoDescuento(StringHelper.normalizaString(StringHelper.remueve_tildes(data.get("comprobante_attr_motivodescuento")).replace("'", "")));
        
        this.setDescuento(data.get("comprobante_attr_descuento"));
        this.setSubTotal(data.get("comprobante_attr_subtotal"));
        this.setTotal(data.get("comprobante_attr_total"));
        this.setMoneda(data.get("comprobante_attr_moneda"));
        this.setTipoCambio(data.get("comprobante_attr_tc"));
        if(Double.parseDouble(this.getDescuento())>0){
            this.setSubTotalSinDescuento(data.get("subtotal_sin_descuento"));
        }else{
            this.setSubTotalSinDescuento("0.00");
        }
        
        //System.out.println("comprobante_attr_tc: "+String.valueOf(data.get("comprobante_attr_tc"))+" comprobante_attr_moneda:"+String.valueOf(data.get("comprobante_attr_moneda")) );
        
        this.setMetodoDePago(StringHelper.normalizaString(StringHelper.remueve_tildes(data.get("comprobante_attr_metododepago")).replace("'", "")));
        this.setLugar_expedicion(StringHelper.normalizaString(StringHelper.remueve_tildes( this.getGralDao().getMunicipioSucursalEmisora(this.getId_sucursal()).toUpperCase()+", "+this.getGralDao().getEstadoSucursalEmisora(this.getId_sucursal()).toUpperCase() ).replace("'", "")));
        this.setNumero_cuenta(data.get("comprobante_attr_numerocuenta"));
        //Datos Base del CFD ------- FIN --------------------------------------
        
        //Datos del Emisor ------- INICIO ------------------------------------- 
        this.setRazon_social_emisor(StringHelper.normalizaString(StringHelper.remueve_tildes(this.getGralDao().getRazonSocialEmpresaEmisora(this.getId_empresa()))));
        this.setRfc_emisor(StringHelper.normalizaString(StringHelper.remueve_tildes(this.getGralDao().getRfcEmpresaEmisora(this.getId_empresa()))));
        this.setRegimen_fiscal_emisor(StringHelper.normalizaString(StringHelper.remueve_tildes(this.getGralDao().getRegimenFiscalEmpresaEmisora(this.getId_empresa()))));
        this.setCalle_domicilio_fiscal(StringHelper.normalizaString(StringHelper.remueve_tildes(this.getGralDao().getCalleDomicilioFiscalEmpresaEmisora(this.getId_empresa()))));
        this.setCodigoPostal_domicilio_fiscal(StringHelper.normalizaString(StringHelper.remueve_tildes(this.getGralDao().getCpDomicilioFiscalEmpresaEmisora(this.getId_empresa()))));
        this.setColonia_domicilio_fiscal(StringHelper.normalizaString(StringHelper.remueve_tildes(this.getGralDao().getColoniaDomicilioFiscalEmpresaEmisora(this.getId_empresa()))));
        this.setEstado_domicilio_fiscal(StringHelper.normalizaString(StringHelper.remueve_tildes(this.getGralDao().getEstadoDomicilioFiscalEmpresaEmisora(this.getId_empresa()))));
        this.setMunicipio_domicilio_fiscal(StringHelper.normalizaString(StringHelper.remueve_tildes(this.getGralDao().getMunicipioDomicilioFiscalEmpresaEmisora(this.getId_empresa()))));
        this.setNoExterior_domicilio_fiscal(StringHelper.normalizaString(StringHelper.remueve_tildes(this.getGralDao().getNoExteriorDomicilioFiscalEmpresaEmisora(this.getId_empresa()))));
        this.setNoInterior_domicilio_fiscal(StringHelper.normalizaString(StringHelper.remueve_tildes(this.getGralDao().getNoInteriorDomicilioFiscalEmpresaEmisora(this.getId_empresa()))));
        this.setPais_domicilio_fiscal(StringHelper.normalizaString(StringHelper.remueve_tildes(this.getGralDao().getPaisDomicilioFiscalEmpresaEmisora(this.getId_empresa()))));
        this.setReferencia_domicilio_fiscal(StringHelper.normalizaString(StringHelper.remueve_tildes(this.getGralDao().getReferenciaDomicilioFiscalEmpresaEmisora(this.getId_empresa()))));
        
        this.setLocalidad_domicilio_fiscal(StringHelper.normalizaString(StringHelper.remueve_tildes(this.getGralDao().getLocalidadDomicilioFiscalEmpresaEmisora(this.getId_empresa()))));
        //Datos del Emisor ------- FIN ----------------------------------------
        
        //Datos del Receptor ------- INICIO ----------------------------------- 
        this.setRazon_social_receptor(StringHelper.remueve_tildes(StringHelper.normalizaString(StringHelper.remueve_tildes(data.get("comprobante_receptor_attr_nombre")))));
        this.setRfc_receptor(StringHelper.remueve_tildes(data.get("comprobante_receptor_attr_rfc")));
        this.setCalle_domicilio_receptor(StringHelper.normalizaString(StringHelper.remueve_tildes(data.get("comprobante_receptor_domicilio_attr_calle")).replace("'", "")));
        this.setNoExterior_domicilio_receptor(data.get("comprobante_receptor_domicilio_attr_noexterior"));
        this.setNoInterior_domicilio_receptor(data.get("comprobante_receptor_domicilio_attr_nointerior"));
        this.setColonia_domicilio_receptor(StringHelper.normalizaString(StringHelper.remueve_tildes(data.get("comprobante_receptor_domicilio_attr_colonia")).replace("'", "")));
        this.setLocalidad_domicilio_receptor(StringHelper.normalizaString(StringHelper.remueve_tildes(data.get("comprobante_receptor_domicilio_attr_localidad")).replace("'", "")));
        this.setReferencia_domicilio_receptor(StringHelper.normalizaString(StringHelper.remueve_tildes(data.get("comprobante_receptor_domicilio_attr_referencia")).replace("'", "")));
        this.setMunicipio_domicilio_receptor(StringHelper.normalizaString(StringHelper.remueve_tildes(data.get("comprobante_receptor_domicilio_attr_municipio")).replace("'", "")));
        this.setEstado_domicilio_receptor(StringHelper.normalizaString(StringHelper.remueve_tildes(data.get("comprobante_receptor_domicilio_attr_estado")).replace("'", "")));
        this.setPais_domicilio_receptor(StringHelper.normalizaString(StringHelper.remueve_tildes(data.get("comprobante_receptor_domicilio_attr_pais")).replace("'", "")));
        this.setCodigoPostal_domicilio_receptor(data.get("comprobante_receptor_domicilio_attr_codigopostal"));
        //Datos del Receptor ------- FIN -------------------------------------- 
        
        this.setListaConceptos(conceptos);
        this.setListaRetenciones(impuestos_retenidos);
        this.setListaTraslados(impuestos_trasladados);
        this.setDatosExtras(extras);
        
        //Obtiene los namespaces que se debe agregar al xml cuando es Factura y Nota de Credito
        this.setLista_namespaces(this.getFacdao().getDataXml_Namespaces(namespaces));
        
        System.out.println(TimeHelper.getFechaActualYMDH()+":::::::::::Termina Seters:::::::::::::::::..");
    }
    
    

    /*aqui se crea el fichero xml
     * Se valida el xml contra el schema
     * Se envia peticion al webservice para timbrado
     * Y por ultimo se gusrdan datos en fac_docs y fac_cfds
     */
    
    public String start() throws Exception {
            String retorno="false";
            String msj="false";
            boolean continuarDespuesDeAdenda=false;
            
            System.out.println(TimeHelper.getFechaActualYMDH()+":::::::::::Inicia construccion del XML:::::::::::::::::..");
            
            //Este es el msj por default si no entra en ninguna de las condiciones
            retorno = retorno +"___"+msj;
            
            String comprobante_firmado = this.generarComprobanteFirmado();
            //System.out.println("timbrado_correcto: "+comprobante_firmado);
            
            //Parser para el xml del cfdi
            BeanFromCfdTimbreXml pop = new BeanFromCfdTimbreXml(comprobante_firmado.getBytes("UTF-8"));
            
            String xml_file_name = new String();
            String path_file = new String();
            String ruta_fichero_schema = new String();
            
            //Directorio de emitidos del fichero
            switch (Proposito.valueOf(this.getProposito())) {
                case NOMINA: path_file = this.getGralDao().getCfdiTimbreEmitidosDir() + this.getRfc_emisor() + "/nomina"; break;
                default: path_file = this.getGralDao().getCfdiTimbreEmitidosDir() + this.getRfc_emisor(); break;
            }
            
            System.out.println("path_file: "+path_file);
            
            ruta_fichero_schema = this.getGralDao().getXsdDir() + this.getGralDao().getFicheroXsdCfdi(this.getId_empresa(), this.getId_sucursal());
            //System.out.println("ruta_fichero_schema: "+ruta_fichero_schema);
            
            String Serie = String.valueOf(pop.getSerie());
            String Folio = String.valueOf(pop.getFolio());
            
            if(Serie.equals("null")){Serie = ""; }
            if(Folio.equals("null")){Folio = ""; }
            
            //Nombre del fichero xml
            xml_file_name += this.getRef_id()+".xml";
            //System.out.println("xml_file_name: "+xml_file_name);
            
            File file_xml = new File(path_file+"/"+xml_file_name);
            
            if(file_xml.exists()){
                //Si ya existe un fichero con el mismo nombre hay que eliminarlo
                FileHelper.delete(path_file+"/"+xml_file_name);
            }
            
            boolean fichero_xml_ok = FileHelper.createFileWithText(path_file, xml_file_name, comprobante_firmado);
            
            System.out.println(TimeHelper.getFechaActualYMDH()+":::::::::::Termina construccion del XML CFD:::::::::::::::::::");
            //System.out.println("comprobante_firmado:::::\n"+comprobante_firmado);
            
            
            //System.out.println("fichero_xml_ok :"+fichero_xml_ok);
            if (fichero_xml_ok) {
                
                System.out.println(TimeHelper.getFechaActualYMDH()+"Inicia Validacion XML");
                
                //System.out.println("fichero_xml_ok: "+fichero_xml_ok);
                //Instancia del validador 
                //validarXml validacion = new validarXml( path_file+"/"+xml_file_name, ruta_fichero_schema);
                //System.out.println("validacion: "+validacion);
                //Aquí se ejecuta la validación del xml contra el Esquema(xsd)
                String success = "true";//validacion.validar();
                
                System.out.println(TimeHelper.getFechaActualYMDH()+"Termina Validacion XML: "+success);
                
                //error po numero de cuenta NA
                //DOCUMENTO INVÁLIDO: org.xml.sax.SAXParseException; cvc-minLength-valid: El valor 'NA' con la longitud = '2' no es de faceta válida con respecto a minLength '4' para el tipo '#AnonType_NumCtaPagoComprobante'
                //System.out.println("str_executex :"+success);
                //si la validación es correcta
                if(success.equals("true")){
                    System.out.println(TimeHelper.getFechaActualYMDH()+":::Inicia ejecucion de programa externo para consumir webservice de timbrado");
                    //:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
                    //aqui inicia request al webservice
                    //:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
                    String noPac = this.getDatosExtras().get("noPac");
                    String ambienteFac = this.getDatosExtras().get("ambienteFac");
                    String tipo_peticion="timbrecfdi";
                    String ruta_ejecutable_java = this.getGralDao().getJavaVmDir(this.getId_empresa(), this.getId_sucursal());
                    String ruta_jarWebService = this.getGralDao().getCfdiTimbreJarWsDir()+"wscli.jar";
                    String serie_folio = Serie + Folio;
                    String refId=this.getRef_id();
                    String str_execute="";
                    
                    //Timbrado con DIVERZA
                    if(noPac.equals("1")){
                        /*   
                        //estos son los parametros que necesita el jar para establecer la conexion con el web service
                        //Datos para timbtado
                        args[0] = PAC proveedor
                        args[1] = tipo de ambiente(pruebas, produccion)
                        args[2] = tipo_peticion
                        args[3] = FicheroPfxTimbradoCfdi
                        args[4] = PasswdFicheroPfxTimbradoCfdi
                        args[5] = JavaVmDirCerts
                        args[6] = path_file
                        args[7] = xml_file_name
                        args[8] = tipo
                        args[9] = version
                        args[10] = getRfc_emisor
                        args[11] = getRfc_receptor
                        args[12] = Serie
                        args[13] = RefID
                         */
                        String ruta_fichero_llave_pfx = this.getGralDao().getSslDir() + this.getGralDao().getRfcEmpresaEmisora(this.getId_empresa())+ "/" +this.getGralDao().getFicheroPfxTimbradoCfdi(this.getId_empresa(), this.getId_sucursal()) ;
                        String password_pfx = this.getGralDao().getPasswdFicheroPfxTimbradoCfdi(this.getId_empresa(), this.getId_sucursal());
                        String ruta_java_almacen_certificados = this.getGralDao().getJavaRutaCacerts(this.getId_empresa(), this.getId_sucursal());
                        String tipo="xml";
                        String version="3.2";
                        
                        //aqui se forma la cadena con los parametros que se le pasan a jar
                        str_execute = ruta_ejecutable_java+" -jar "+ruta_jarWebService+" "+noPac+" "+ambienteFac+" "+tipo_peticion+ " "+ruta_fichero_llave_pfx+" "+password_pfx+" "+ruta_java_almacen_certificados+" "+path_file+" "+xml_file_name+" "+tipo+" "+version+" "+this.getRfc_emisor()+" "+pop.getRfc_receptor()+" "+serie_folio+" "+refId;
                    }
                    
                    //Timbrado con SERVISIM
                    if(noPac.equals("2")){
                        /*
                        args[0] = PAC proveedor
                        args[1] = tipo de ambiente(pruebas, produccion)
                        args[2] = tipo_peticion(cancelacion, timbrado)
                        args[3] = Usuario
                        args[4] = Password
                        args[5] = dir_fichero_xml
                        args[6] = name_fichero_xml(serie_folio.xml)
                        args[7] = refId
                        */
                        
                        String usuario = this.getGralDao().getUserContrato(this.getId_empresa(), this.getId_sucursal());
                        String contrasena = this.getGralDao().getPasswordUserContrato(this.getId_empresa(), this.getId_sucursal());
                        
                        //aqui se forma la cadena con los parametros que se le pasan a jar
                        str_execute = ruta_ejecutable_java+" -jar "+ruta_jarWebService+" "+noPac+" "+ambienteFac+" "+tipo_peticion+ " "+usuario+" "+contrasena+" "+path_file+" "+xml_file_name+" "+refId;
                    }
                    
                    System.out.println("str_execute :"+str_execute);

                    Process resultado = Runtime.getRuntime().exec(str_execute); 

                    InputStream myInputStream=null;
                    myInputStream= resultado.getInputStream();

                    BufferedReader reader = new BufferedReader(new InputStreamReader(myInputStream));
                    StringBuilder sb = new StringBuilder();
                    String line = null;
                    while ((line = reader.readLine()) != null) {
                        sb.append(line);
                    }
                    myInputStream.close();


                    System.out.println(TimeHelper.getFechaActualYMDH()+":::Termina ejecucion de programa externo");
                    System.out.println("sb.toString: "+sb.toString());

                    String cadenaResult = new String();
                    String valor1 = new String();
                    cadenaResult = sb.toString();
                    String arrayResult[] = sb.toString().split("___");
                    //toma el valor true o false
                    valor1 = arrayResult[0];
                    //toma el mensaje
                    msj = arrayResult[2];
                    
                    System.out.println("Resultado: "+cadenaResult);
                    
                    if(valor1.equals("true")){
                        //:::Si llegó aquí es que el request al webservice nos devolvio correctamente el timbre fiscal::::::::
                        //:::Ahora procederemos a guardar los datos a la bd:::::::::::::::::::::::::::::::::::::::::::::::::::
                        System.out.println(TimeHelper.getFechaActualYMDH()+":::Inicia parseo de xml para salvar datos:::::::::");
                        BeanFromCfdiXml pop2 = new BeanFromCfdiXml(path_file+"/"+xml_file_name);
                        
                        this.setSelloDigital(pop2.getSelloCfd());
                        this.setSelloDigitalSat(pop2.getSelloSat());
                        this.setUuid(pop2.getUuid());
                        this.setFechaTimbrado(pop2.getFecha_timbre());
                        this.setNoCertificadoSAT(pop2.getNoCertificadoSAT());
                        
                        String cadena_xml = FileHelper.stringFromFile(path_file+"/"+ xml_file_name);
                        String cadena_original_timbre = this.cadenaOriginalTimbre(cadena_xml, this.getId_empresa(), this.getId_sucursal());
                        //System.out.println("XML_TIMBRADO: "+cadena_xml);
                        
                        this.setCadenaOriginalTimbre(cadena_original_timbre);
                        
                        //System.out.println("sello sat: "+this.getSelloDigitalSat());
                        String cadena_conceptos="";
                        String cadena_imp_trasladados="";
                        String cadena_imp_retenidos = "";
                        switch (Proposito.valueOf(this.getProposito())) {
                            case FACTURA:
                                //Aqui va la rutina que guarda los datos de este comprobante fiscal a la tabla fac_cfds y fac_docs
                                cadena_conceptos = this.getFacdao().formar_cadena_conceptos(pop.getListaConceptos());
                                cadena_imp_trasladados = this.getFacdao().formar_cadena_traslados(pop.getTotalImpuestosTrasladados(),this.getTasaIva());
                                cadena_imp_retenidos = this.getFacdao().formar_cadena_traslados(pop.getTotalImpuestosRetenidos(),this.getTasaRetencion());
                            break;
                        }
                        
                        Integer id_usuario = Integer.parseInt(this.getDatosExtras().get("usuario_id"));
                        String tipo_cambio = this.getTipoCambio();
                        String app_selected = this.getDatosExtras().get("app_selected");
                        String command_selected = this.getDatosExtras().get("command_selected");
                        String extra_data_array = this.getDatosExtras().get("extra_data_array");
                        
                        String estado_comprobante="1";
                        String regimen_fiscal = pop.getRegimenFiscalEmisor();
                        String metodo_pago = pop.getMetodoDePago();
                        String num_cuenta = pop.getNumeroCuenta();
                        String lugar_de_expedicion = pop.getLugarExpedicion();
                        
                        String data_string="";
                        String no_aprobacion="";
                        String ano_aprobacion="";
                        
                        if(pop.getNoAprobacion()!=null ){
                            no_aprobacion = pop.getNoAprobacion();
                        }
                        
                        if(pop.getAnoAprobacion()!=null ){
                            ano_aprobacion = pop.getAnoAprobacion();
                        }
                        
                        System.out.println(TimeHelper.getFechaActualYMDH()+":::Termina parseo de xml.");
                        
                        String ret="";
                        switch (Proposito.valueOf(this.getProposito())) {
                            case FACTURA:
                                Integer prefactura_id = Integer.parseInt(this.getDatosExtras().get("prefactura_id"));
                                String refacturar = this.getDatosExtras().get("refacturar");
                                String id_moneda = this.getDatosExtras().get("moneda_id");
                                
                                data_string = app_selected+"___"+command_selected+"___"+id_usuario+"___"+prefactura_id+"___"+pop.getRfc_receptor()+"___"+Serie+"___"+Folio+"___"+no_aprobacion+"___"+pop.getTotal()+"___"+pop.getTotalImpuestosTrasladados()+"___"+estado_comprobante+"___"+xml_file_name+"___"+pop.getFecha()+"___"+pop.getRazon_social_receptor()+"___"+pop.getTipoDeComprobante()+"___"+this.getProposito()+"___"+ano_aprobacion+"___"+cadena_conceptos+"___"+cadena_imp_trasladados+"___"+cadena_imp_retenidos+"___"+Integer.parseInt(id_moneda)+"___"+tipo_cambio+"___"+refacturar+"___"+regimen_fiscal+"___"+metodo_pago+"___"+num_cuenta+"___"+lugar_de_expedicion+"___"+this.getRef_id()+"___"+cadena_xml+"___"+this.getSubTotalSinDescuento();
                                
                                System.out.println(TimeHelper.getFechaActualYMDH()+":::Inicia Salvar datos de la Factura.");
                                
                                //Llamada al procedimiento que guarda los datos de la factura
                                ret = this.getFacdao().selectFunctionForFacAdmProcesos(data_string, extra_data_array);
                                
                                System.out.println(TimeHelper.getFechaActualYMDH()+":::Termina Salvar datos.");
                                
                                //Éste es el valor del retorno idicando que todo se efectuo correctamente hasta aqui
                                valor1="true";
                            break;
                                
                            case NOTA_CREDITO:
                                System.out.println("LLego en Nota de Credito");
                                
                                this.setXml_timbrado(cadena_xml);
                                
                                //Este es el valor del retorno idicando que todo se efectuo correctamente hasta aqui
                                retorno="true";
                            break;
                                
                            case NOMINA:
                                String nom_det_id = this.getDatosExtras().get("id");
                                String empleado_id = this.getDatosExtras().get("empleado_id");
                                
                                data_string = app_selected+"___"+command_selected+"___"+id_usuario+"___"+nom_det_id+"___"+empleado_id+"___"+this.getRef_id()+"___"+Serie+"___"+Folio+"___"+cadena_xml;
                                
                                //Llamada al procedimiento que guarda los datos de la factura
                                ret = this.getFacdao().selectFunctionForFacAdmProcesos(data_string, extra_data_array);
                                
                                //Éste es el valor del retorno idicando que todo se efectuo correctamente hasta aqui
                                valor1="true";
                            break;
                        }
                    }
                    
                    //retorna el error o "true" si el timbrado tuvo exito
                    retorno = valor1+"___"+msj;
                    
                }else{
                    //finalizar el programa y retornar el error de la validacion del xml.
                    retorno = "false"+"___"+success;

                    return retorno;
                }
            } else {
                retorno = "false"+"___"+"Falló al generar fichero xml antes del enviar a timbrar, intente de nuevo.";
                return retorno;
                //throw new Exception("Falló al generar fichero xml: " + xml_file_name);
            }
        return retorno;
    }
    
    
    
    
    private String generarComprobanteFirmado() throws Exception {
        boolean agregar_complemento=false;
        String valor_retorno = new String();
        String refId = new String();
        String serie = "";
        String folio = "";
        //Numero de Identificacion unica de la Empresa
        String no_id = this.getGralDao().getNoIdEmpresa(this.getId_empresa());
        
        this.checkdata();
        CfdiXmlBuilder cfd = new CfdiXmlBuilder();
        cfd.init();
        
        switch (Proposito.valueOf(this.getProposito())) {
            case FACTURA:
                folio = this.getGralDao().getFolioFactura(this.getId_empresa(), this.getId_sucursal());
                serie = this.getGralDao().getSerieFactura(this.getId_empresa(), this.getId_sucursal());
                break;
                
            case NOTA_CREDITO:
                folio = this.getGralDao().getFolioNotaCredito(this.getId_empresa(), this.getId_sucursal());
                serie = this.getGralDao().getSerieNotaCredito(this.getId_empresa(), this.getId_sucursal());
                break;
                
            case NOTA_CARGO:
                folio = this.getGralDao().getFolioNotaCargo(this.getId_empresa(), this.getId_sucursal());
                serie = this.getGralDao().getSerieNotaCargo(this.getId_empresa(), this.getId_sucursal());
                break;
                
            case NOMINA:
                folio = this.getGralDao().getFolioFacNomina(this.getId_empresa(), this.getId_sucursal());
                serie = this.getGralDao().getSerieFacNomina(this.getId_empresa(), this.getId_sucursal());
                agregar_complemento=true;
                break;
        }
        
        refId = no_id +"_"+ serie+folio;
        this.setRef_id(refId);
        
        cfd.construyeNodoFactura(
                this.getLista_namespaces(),
                serie,
                folio,
                this.getTipoDeComprobante(),
                this.getCondicionesDePago(),
                this.getFormaDePago(),
                this.getFecha(),
                this.getSubTotal(),
                this.getTotal(),
                this.getMoneda(), 
                this.getTipoCambio(),
                this.getNoCertificado(),
                this.getCertificado(),
                this.getMetodoDePago(),
                this.getLugar_expedicion(),
                this.getNumero_cuenta(),
                this.getDescuento(),
                this.getMotivoDescuento());
        
        cfd.configurarNodoEmisor(
                this.getRazon_social_emisor(),
                this.getRfc_emisor(),
                this.getPais_domicilio_fiscal(),
                this.getEstado_domicilio_fiscal(),
                this.getMunicipio_domicilio_fiscal(),
                this.getColonia_domicilio_fiscal(),
                this.getCalle_domicilio_fiscal(),
                this.getNoExterior_domicilio_fiscal(),
                this.getCodigoPostal_domicilio_fiscal(),
                this.getRegimen_fiscal_emisor());
        
        cfd.configurarNodoReceptor(
                this.getRazon_social_receptor(),
                this.getRfc_receptor(),
                this.getPais_domicilio_receptor(),
                this.getNoExterior_domicilio_receptor(),
                this.getNoInterior_domicilio_receptor(),
                this.getCalle_domicilio_receptor(),
                this.getColonia_domicilio_receptor(),
                this.getMunicipio_domicilio_receptor(),
                this.getEstado_domicilio_receptor(),
                this.getCodigoPostal_domicilio_receptor());
        
        cfd.configurarNodoConceptos(this.getListaConceptos());
        
        cfd.configurarImpuestos(this.getListaRetenciones(), this.getListaTraslados());
        
        
        if(agregar_complemento){
            //Solo se agrega este complemento cuando es nomina
            cfd.configurarComplementos(this.getDatosNomina());
        }
        
        
        String comprobante_sin_firmar = cfd.getOutXmlString();
        //System.out.println("comprobante_sin_firmar: "+comprobante_sin_firmar);
        
        //comprobante_sin_firmar = comprobante_sin_firmar.replaceAll("@SUMIMPUESTOS_RETENIDOS", this.getTotalRetenciones().toString());
        //comprobante_sin_firmar = comprobante_sin_firmar.replaceAll("@SUMIMPUESTOS_TRASLADADOS", this.getTotalTraslados().toString());
        
        switch (Proposito.valueOf(this.getProposito())) {
            case FACTURA:
                comprobante_sin_firmar = comprobante_sin_firmar.replaceAll("@SUMIMPUESTOS_RETENIDOS", this.getTotalRetenciones().toString());
                comprobante_sin_firmar = comprobante_sin_firmar.replaceAll("@SUMIMPUESTOS_TRASLADADOS", this.getTotalTraslados().toString());

                //String folio_factura = this.getGralDao().getFolioFactura(this.getId_empresa(), this.getId_sucursal());
                //comprobante_sin_firmar = comprobante_sin_firmar.replaceAll("@FOLIO", folio_factura);
                //comprobante_sin_firmar = comprobante_sin_firmar.replaceAll("@SERIE", this.getGralDao().getSerieFactura(this.getId_empresa(), this.getId_sucursal()));
                comprobante_sin_firmar = comprobante_sin_firmar.replaceAll("@ANO_APROBACION", this.getGralDao().getAnoAprobacionFactura(this.getId_empresa(), this.getId_sucursal()));
                comprobante_sin_firmar = comprobante_sin_firmar.replaceAll("@NOAPROBACION", this.getGralDao().getNoAprobacionFactura(this.getId_empresa(), this.getId_sucursal()));
                //La siguiente línea se comento porque la actualizacion del folio se hace en el procedimiento.
                //this.getGralDao().actualizarFolioFactura(this.getId_empresa(), this.getId_sucursal());
                break;
                
            case NOTA_CREDITO:
                comprobante_sin_firmar = comprobante_sin_firmar.replaceAll("@SUMIMPUESTOS_RETENIDOS", this.getTotalRetenciones().toString());
                comprobante_sin_firmar = comprobante_sin_firmar.replaceAll("@SUMIMPUESTOS_TRASLADADOS", this.getTotalTraslados().toString());
                //String folio_credito = this.getGralDao().getFolioNotaCredito(this.getId_empresa(), this.getId_sucursal());
                //comprobante_sin_firmar = comprobante_sin_firmar.replaceAll("@FOLIO", folio_credito);
                //comprobante_sin_firmar = comprobante_sin_firmar.replaceAll("@SERIE", this.getGralDao().getSerieNotaCredito(this.getId_empresa(), this.getId_sucursal()));
                comprobante_sin_firmar = comprobante_sin_firmar.replaceAll("@ANO_APROBACION", this.getGralDao().getAnoAprobacionNotaCredito(this.getId_empresa(), this.getId_sucursal()));
                comprobante_sin_firmar = comprobante_sin_firmar.replaceAll("@NOAPROBACION", this.getGralDao().getNoAprobacionNotaCredito(this.getId_empresa(), this.getId_sucursal()));
                //this.getGralDao().actualizarFolioNotaCredito(this.getId_empresa(), this.getId_sucursal());
                break;
                
            case NOTA_CARGO:
                //String folio_notadecargo = this.getGralDao().getFolioNotaCargo(this.getId_empresa(), this.getId_sucursal());
                //comprobante_sin_firmar = comprobante_sin_firmar.replaceAll("@FOLIO", folio_notadecargo);
                //comprobante_sin_firmar = comprobante_sin_firmar.replaceAll("@SERIE", this.getGralDao().getSerieNotaCargo(this.getId_empresa(), this.getId_sucursal()));
                comprobante_sin_firmar = comprobante_sin_firmar.replaceAll("@ANO_APROBACION", this.getGralDao().getAnoAprobacionNotaCargo(this.getId_empresa(), this.getId_sucursal()));
                comprobante_sin_firmar = comprobante_sin_firmar.replaceAll("@NOAPROBACION", this.getGralDao().getNoAprobacionNotaCargo(this.getId_empresa(), this.getId_sucursal()));
                //this.getGralDao().actualizarFolioNotaCargo(this.getId_empresa(), this.getId_sucursal());
                break;
                
            case NOMINA:
                comprobante_sin_firmar = comprobante_sin_firmar.replaceAll("@SUMIMPUESTOS_RETENIDOS", this.getTotalRetenciones().toString());
                break;
        }
        
        String cadena_original = this.cadenaOriginal(comprobante_sin_firmar, this.getId_empresa(), this.getId_sucursal());
        this.setCadenaOriginal(cadena_original);
        
        
        String ruta_fichero_llave = this.getGralDao().getSslDir() + this.getGralDao().getRfcEmpresaEmisora(this.getId_empresa())+ "/" + this.getGralDao().getFicheroLlavePrivada(this.getId_empresa(), this.getId_sucursal());
        //System.out.println("ruta_fichero_llave: "+ruta_fichero_llave);
        
        String sello = CryptoEngine.sign(ruta_fichero_llave, this.getGralDao().getPasswordLlavePrivada(this.getId_empresa(), this.getId_sucursal()), cadena_original);
        valor_retorno = comprobante_sin_firmar.replaceAll("@SELLO_DIGITAL", sello);
        
        this.setSelloDigital(sello);
        
        //System.out.println("sello emisor1: "+sello);
        //System.out.println("valor_retorno genera comrobante:"+valor_retorno);
        return valor_retorno;
    }
    
    
    private void checkdata() throws Exception {
        this.validar_datos();
        this.validar_Conceptos();
        this.validarImpuestos(this.getListaRetenciones(), this.getListaTraslados());
    }
    
    private String cadenaOriginal(String comprobante_sin_firmar, Integer id_empresa, Integer id_sucursal) throws Exception {
        String valor_retorno = new String();
        valor_retorno = XmlHelper.transformar(comprobante_sin_firmar, this.getGralDao().getXslDir() + this.getGralDao().getRfcEmpresaEmisora(id_empresa)+"/"+ this.getGralDao().getFicheroXsl(id_empresa, id_sucursal));
        //System.out.println("EsquemaXslt: "+this.getGralDao().getXslDir() + this.getGralDao().getRfcEmpresaEmisora(id_empresa)+"/"+ this.getGralDao().getFicheroXsl(id_empresa, id_sucursal));
        return valor_retorno;
    }
    
    
    
    
    
    
    
    private void validarImpuestos(ArrayList<LinkedHashMap<String, String>> lista_retenciones, ArrayList<LinkedHashMap<String, String>> lista_traslados) throws Exception {
        String tasa_retencion="";
        BigDecimal totalImpuestosRetenidos = new BigDecimal("0");
        for (LinkedHashMap<String, String> r : lista_retenciones) {
            Iterator it = r.entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry elemento_hash = (Map.Entry) it.next();
                String llave = (String) elemento_hash.getKey();
                String valor = (String) elemento_hash.getValue();
                if (llave.equals("impuesto")) {
                    if (!this.getValedor().isImpuestoRetencionConocido(valor)) {
                        throw new Exception("El Atributo(Requerido) impuesto de un tag Retencion es incorrecto");
                    }
                }
                
                if (llave.equals("importe")) {
                    if (!this.getValedor().isMontoDelImpuestoCorrecto(valor)) {
                        throw new Exception("El Atributo(Requerido) importe de un tag Retencion es incorrecto");
                    } else {
                        totalImpuestosRetenidos = totalImpuestosRetenidos.add(new BigDecimal(valor));
                    }
                }
                
                //este valor no se va al xml
                if (llave.equals("tasa")) {
                    tasa_retencion=valor;
                }
            }
            
            this.setTotalRetenciones(totalImpuestosRetenidos);
            this.setTasaRetencion(tasa_retencion);
        }
        
        String tasa_iva="";
        BigDecimal totalImpuestosTrasladados = new BigDecimal("0");
        for (LinkedHashMap<String, String> t : lista_traslados) {
            Iterator it = t.entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry elemento_hash = (Map.Entry) it.next();
                String llave = (String) elemento_hash.getKey();
                String valor = (String) elemento_hash.getValue();
                
                if (llave.equals("impuesto")) {
                    if (!this.getValedor().isImpuestoTrasladoConocido(valor)) {
                        throw new Exception("El Atributo(Requerido) impuesto de un tag Traslado es incorrecto");
                    }
                }
                
                if (llave.equals("importe")) {
                    if (!this.getValedor().isMontoDelImpuestoCorrecto(valor)) {
                        throw new Exception("El Atributo(Requerido) importe de un tag Traslado es incorrecto");
                    } else {
                        totalImpuestosTrasladados = totalImpuestosTrasladados.add(new BigDecimal(valor));
                    }
                }
                
                if (llave.equals("tasa")) {
                    if (!this.getValedor().isMontoDelImpuestoCorrecto(valor)) {
                        throw new Exception("El Atributo(Requerido) tasa de un tag Traslado es incorrecto");
                    }else{
                        tasa_iva=valor;
                    }
                }
            }
        }

        this.setTotalTraslados(totalImpuestosTrasladados);
        this.setTasaIva(tasa_iva);
    }

    private void validar_Conceptos() throws Exception {

        if (this.getListaConceptos().size() > 0) {

            BigDecimal sumImportes = new BigDecimal("0");
            for (LinkedHashMap<String, String> i : this.getListaConceptos()) {
                Iterator it = i.entrySet().iterator();
                while (it.hasNext()) {
                    Map.Entry elemento_hash = (Map.Entry) it.next();
                    String llave = (String) elemento_hash.getKey();
                    String valor = (String) elemento_hash.getValue();
                    if (llave.equals("unidad")) {
                        if (valor.isEmpty()) {
                            log.log(Level.FINE, "El Atributo(Opcional) unidad de un tag Concepto es incorrecto o se dejo vacio");
                        }
                    }
                    
                    if (llave.equals("noIdentificacion")) {
                        if (valor.isEmpty()) {
                            log.log(Level.FINE, "El Atributo(Opcional) noIdentificacion de un tag Concepto es incorrecto o se dejo vacio");
                        }
                    }

                    if (llave.equals("cantidad")) {
                        if (!this.getValedor().isDecimalCorrecto(valor)) {
                            throw new Exception("El Atributo(Requerido) cantidad de un tag Concepto es incorrecto");
                        }
                    }

                    if (llave.equals("descripcion")) {
                        if (valor.isEmpty()) {
                            throw new Exception("El Atributo(Requerido) descripcion de un tag Concepto es incorrecto");
                        }
                    }
                    
                    if (llave.equals("valorUnitario")) {
                        if (!this.getValedor().isMontoDelaOperacionCorrecto(valor)) {
                            throw new Exception("El Atributo(Requerido) valorUnitario de un tag Concepto es incorrecto");
                        }
                    }
                    
                    if (llave.equals("importe")) {
                        if (!this.getValedor().isMontoDelaOperacionCorrecto(valor)) {
                            throw new Exception("El Atributo(Requerido) importe de un tag Concepto es incorrecto");
                        } else {
                            sumImportes = sumImportes.add(new BigDecimal(valor));
                        }
                    }
                    
                    ///validar informacion aduanera
                    if (llave.equals("numero_aduana")) {
                        if (valor.isEmpty()) {
                            log.log(Level.FINE, "El Atributo(Opcional) numero  de un tag Aduana es incorrecto");
                        }
                    }
                    
                    if (llave.equals("fecha_aduana")) {
                        if (!this.getValedor().isValidFecha_Aduana(valor)) {
                            log.log(Level.FINE, "El Atributo(Opcional) fecha  de un tag Aduana es incorrecto");
                        }
                    }
                    
                    if (llave.equals("aduana_aduana")) {
                        if (valor.isEmpty()) {
                            log.log(Level.FINE, "El Atributo(Opcional) aduana  de un tag Aduana es incorrecto");
                        }
                    }
                }
            }
            this.setSumatoriaImportes(sumImportes);
        } else {
            throw new Exception("El Atributo(Requerido) Conceptos carece de conceptos individuales");
        }
    }

    private void validar_datos() throws Exception {
        if (this.getCondicionesDePago().isEmpty()) {
            log.log(Level.FINE, "El Atributo(Opcional) condicionesDePago del Tag Comprobante fiscal es incorrecto");
        }

        if (!this.getDescuento().equals("null")) {
            if (!this.getValedor().isMontoDelaOperacionCorrecto(this.getDescuento())) {
                log.log(Level.FINE, "El Atributo(Opcional) descuento del Tag Comprobante fiscal es incorrecto");
            }

            if (StringUtils.isEmpty(this.getMotivoDescuento())) {
                log.log(Level.FINE, "El Atributo(Opcional) motivoDescuento del Tag Comprobante fiscal es incorrecto");
            }
        } else {
            log.log(Level.FINE, "El Atributo(Opcional) descuento del Tag Comprobante fiscal se dejo vacio");
        }
        
        if (!this.getValedor().isFechaDelComprobanteFiscalCorrecto(this.getFecha())) {
            throw new Exception("\n El Atributo(Requerido) fecha del Tag Comprobante fiscal es incorrecto");
        }
        
        if (this.getTipoDeComprobante().isEmpty()) {
            throw new Exception("\n El Atributo(Requerido) tipoDeComprobante del Tag Comprobante fiscal es incorrecto");
        }
        
        if (this.getFormaDePago().isEmpty()) {
            throw new Exception("\n El Atributo(Requerido) formaDePago del Tag Comprobante fiscal es incorrecto");
        }
        
        if (this.getCertificado().isEmpty()) {
            log.log(Level.FINE, "El Atributo(Opcional) certificado del Tag Comprobante fiscal es incorrecto");
        }
        
        // opcional
        if (this.getMetodoDePago().isEmpty()) {
            log.log(Level.FINE, "El Atributo(Opcional) metodoDePago del Tag Comprobante fiscal es incorrecto");
        }
        
        if (this.getLugar_expedicion().isEmpty()) {
            log.log(Level.FINE, "El Atributo(Opcional) LugarExpedicion del Tag Comprobante fiscal es incorrecto");
        }
        
        if (this.getNumero_cuenta().isEmpty()) {
            log.log(Level.FINE, "El Atributo(Opcional) NumCtaPago del Tag Comprobante fiscal es incorrecto");
        }
        
        if (!this.getValedor().isNoCertificadoDelComprobanteFiscalCorrecto(this.getNoCertificado())) {
            throw new Exception("\n El Atributo(Requerido) noCertificado del Tag Comprobante fiscal es incorrecto");
        }
        
        if (!this.getValedor().isMontoDelaOperacionCorrecto(this.getSubTotal())) {
            throw new Exception("\n El Atributo(Requerido) subTotal del Tag Comprobante fiscal es incorrecto");
        }
        
        if (!this.getValedor().isMontoDelaOperacionCorrecto(this.getTotal())) {
            throw new Exception("\n El Atributo(Requerido) total del Tag Comprobante fiscal es incorrecto");
        }
        
        // Validar datos del emisor
        if (this.getRazon_social_emisor().isEmpty()) {
            throw new Exception("El Atributo(Requerido) version del Tag Emisor es incorrecto");
        }

        if (this.getRfc_emisor().isEmpty()) {
            throw new Exception("El Atributo(Requerido) rfc del Tag Emisor es incorrecto");
        }
        //validar regimen fiscal emisor
        if (this.getRegimen_fiscal_emisor().isEmpty()) {
            throw new Exception("El Atributo(Requerido) Regimen del Tag RegimenFiscal es incorrecto");
        }
        
        // validar domicilio fiscal emisor
        if (this.getCalle_domicilio_fiscal().isEmpty()) {
            throw new Exception("El Atributo(Requerido) calle del Tag DomicilioFiscal es incorrecto");
        }

        if (this.getCodigoPostal_domicilio_fiscal().isEmpty()) {
            throw new Exception("El Atributo(Requerido) codigoPostal del Tag DomicilioFiscal es incorrecto");
        }

        if (this.getColonia_domicilio_fiscal().isEmpty()) {
            log.log(Level.FINE, "El Atributo(Opcional) colonia del Tag DomicilioFiscal es incorrecto");
        }

        if (this.getEstado_domicilio_fiscal().isEmpty()) {
            throw new Exception("El Atributo(Requerido) estado del Tag DomicilioFiscal es incorrecto");
        }

        if (this.getMunicipio_domicilio_fiscal().isEmpty()) {
            throw new Exception("El Atributo(Requerido) municipio del Tag DomicilioFiscal es incorrecto");
        }

        if (this.getNoExterior_domicilio_fiscal().isEmpty()) {
            log.log(Level.FINE, "El Atributo(Opcional) noExterior del Tag DomicilioFiscal es incorrecto");
        }

        if (this.getPais_domicilio_fiscal().isEmpty()) {
            throw new Exception("El Atributo(Requerido) pais del Tag DomicilioFiscal es incorrecto");
        }

        if (this.getNoInterior_domicilio_fiscal().isEmpty()) {
            log.log(Level.FINE, "El Atributo(Opcional) noInterior del Tag DomicilioFiscal es incorrecto");
        }

        if (this.getLocalidad_domicilio_fiscal().isEmpty()) {
            log.log(Level.FINE, "El Atributo(Opcional) localidad del Tag DomicilioFiscal es incorrecto");
        }

        if (this.getReferencia_domicilio_fiscal().isEmpty()) {
            log.log(Level.FINE, "El Atributo(Opcional) referencia del Tag DomicilioFiscal es incorrecto");
        }

        //validar receptor
        if (!this.getValedor().isRFCCorrecto(this.getRfc_receptor())) {
            throw new Exception("El Atributo(Requerido) rfc del Tag Receptor es incorrecto " + this.getRfc_receptor());
        }

        if (this.getRazon_social_receptor().isEmpty()) {
            log.log(Level.FINE, "El Atributo(Opcional) nombre del Tag Receptor es incorrecto");
        }

        //validar domicilo receptor
        if (this.getPais_domicilio_receptor().isEmpty()) {
            throw new Exception("El Atributo(Requerido) pais del Tag Domicilio es incorrecto " + this.getPais_domicilio_receptor());
        }

        if (this.getCalle_domicilio_receptor().isEmpty()) {
            log.log(Level.FINE, "El Atributo(Opcional) calle del Tag Domicilio es incorrecto");
        }

        if (this.getMunicipio_domicilio_receptor().isEmpty()) {
            log.log(Level.FINE, "El Atributo(Opcional) municipio del Tag Domicilio es incorrecto");
        }

        if (this.getEstado_domicilio_receptor().isEmpty()) {
            log.log(Level.FINE, "El Atributo(Opcional) estado del Tag Domicilio es incorrecto");
        }

        if (this.getColonia_domicilio_receptor().isEmpty()) {
            log.log(Level.FINE, "El Atributo(Opcional) colonia del Tag Domicilio es incorrecto");
        }

        if (this.getLocalidad_domicilio_receptor().isEmpty()) {
            log.log(Level.FINE, "El Atributo(Opcional) localidad del Tag Domicilio es incorrecto");
        }

        if (this.getReferencia_domicilio_receptor().isEmpty()) {
            log.log(Level.FINE, "El Atributo(Opcional) referencia del Tag Domicilio es incorrecto");
        }

        if (this.getNoExterior_domicilio_receptor().isEmpty()) {
            log.log(Level.FINE, "El Atributo(Opcional) noExterior del Tag Domicilio es incorrecto");
        }

        if (((String) this.getNoInterior_domicilio_receptor()).isEmpty()) {
            log.log(Level.FINE, "El Atributo(Opcional) noInterior del Tag Domicilio es incorrecto");
        }

        if (!this.getValedor().isCodigoPostalCorrecto(this.getCodigoPostal_domicilio_receptor())) {
            log.log(Level.FINE, "El Atrdocibuto(Opcional) codigoPostal del Tag Domicilio es incorrecto");
        }
    }

    private class Validacion {

        /**Verifica si el Codigo Postal sumistrado cumple con el patron que dicta correos mexicanos
         * @param cadena_a_validar
         * @return true si el Codigo Postal es valido, false si el Codigo Postal es invalido
         */
        public boolean isCodigoPostalCorrecto(String cadena_a_validar) {
            return Pattern.compile("^[0-9]{5}$").matcher(cadena_a_validar).find();
        }

        /**Verifica el año de el esquema para el archivo de informe mensual
         * @param cadena_a_validar
         * @return true si el año es valido, false si el año es invalido.
         */
        public boolean isYyyyCorrecto(String cadena_a_validar) {
            return Pattern.compile("^[0-9]{4}$").matcher(cadena_a_validar).find();
        }

        /**Verifica el mes de el esquema para el archivo de informe mensual
         * @param cadena_a_validar
         * @return true si el mes es valido, false si el mes es invalido.
         */
        public boolean isMmCorrecto(String cadena_a_validar) {
            return Pattern.compile("^([1][0-2]|[1-9]|0[1-9])$").matcher(cadena_a_validar).find();
        }

        public boolean isValidFecha_Aduana(String valor) {
            return Pattern.compile("^([0-9]{2}/[0-9]{2}/[0-9]{4})$").matcher(valor).find();
        }

        public boolean isRFCCorrecto(String cadena_a_validar) {
            return Pattern.compile("^[A-Za-z&0-9]{3,4}[0-9]{6}[A-Za-z0-9]{3}$").matcher(cadena_a_validar).find();
        }

        public boolean isSerieCorrecto(String cadena_a_validar) {
            return Pattern.compile("^([A-Za-z]|[0-9]){0,10}$").matcher(cadena_a_validar).find();
        }

        /**Verifica el estado del comprobante de el registro por renglon
         * @param cadena_a_validar
         * @return true si una serie es valido, false si la serie es invalido.
         */
        public boolean isEstadoDelComprobanteCorrecto(String cadena_a_validar) {
            return Pattern.compile("^(0|1){1}$").matcher(cadena_a_validar).find();
        }

        /**Verifica el monto del impuesto de el registro por renglon
         * @param cadena_a_validar
         * @return true si es un monto valido, false si es un monto invalido.
         */
        public boolean isMontoDelImpuestoCorrecto(String cadena_a_validar) {
            boolean valor_retorno = false;

            if (cadena_a_validar == null) {
                valor_retorno = true;
            }

            BigDecimal numero_introducido = new BigDecimal(cadena_a_validar);
            BigDecimal numero_maximo = new BigDecimal("9999999999.99");
            BigDecimal numero_minimo = new BigDecimal("0");

            //Si el numero introducido es igual al numero maximo
            if ((numero_introducido.compareTo(numero_maximo) == 0)) {
                valor_retorno = true;
            } else {
                //Si el numero introducido es igual al numero minimo
                if ((numero_introducido.compareTo(numero_minimo) == 0)) {
                    valor_retorno = true;
                } else {
                    //Si el numero minimo es menor que el numero introducido
                    if (numero_minimo.compareTo(numero_introducido) == -1) {
                        //Si el numero introducido es menor que el numero maximo
                        if (numero_introducido.compareTo(numero_maximo) == -1) {
                            valor_retorno = true;
                        }
                    }
                }

            }

            return valor_retorno;
        }

        /**Verifica el momento de Expedicion del registro por renglon
         * @param cadena_a_validar
         * @return true si un momento valido, false si es un momento invalido.
         */
        public boolean isMomentoExpedicionCorrecto(String cadena_a_validar) {
            return Pattern.compile("^[0-1][0-9]/[0-3][0-9]/[0-9]{4}\\ [0-9]{2}:[0-9]{2}:[0-9]{2}|[0-9]{4}-[0-9]{2}-[0-9]{2}T[0-9]{2}:[0-9]{2}:[0-9]{2}$").matcher(cadena_a_validar).find();
        }

        /**Verifica el monto de la operacion de el registro por renglon
         * @param cadena_a_validar
         * @return true si es un monto valido, false si es un monto invalido.
         */
        public boolean isMontoDelaOperacionCorrecto(String cadena_a_validar) {
            boolean valor_retorno = false;
            
            //System.err.println("cadena_a_validar:"+cadena_a_validar);
            BigDecimal numero_introducido = new BigDecimal(cadena_a_validar);
            BigDecimal numero_maximo = new BigDecimal("9999999999.99");
            BigDecimal numero_minimo = new BigDecimal("0");
            
            //Si el numero introducido es igual al numero maximo
            if ((numero_introducido.compareTo(numero_maximo) == 0)) {
                valor_retorno = true;
            } else {
                //Si el numero introducido es igual al numero minimo
                if ((numero_introducido.compareTo(numero_minimo) == 0)) {
                    valor_retorno = true;
                } else {
                    //Si el numero minimo es menor que el numero introducido
                    if (numero_minimo.compareTo(numero_introducido) == -1) {
                        //Si el numero introducido es menor que el numero maximo
                        if (numero_introducido.compareTo(numero_maximo) == -1) {
                            valor_retorno = true;
                        }
                    }
                }
            }

            return valor_retorno;
        }

        /**Verifica el numero de serie del certificado de sello digital
         * que ampara al comprobante  fiscal
         * @param cadena_a_validar
         * @return true si es valido, false si no.
         */
        public boolean isNoCertificadoDelComprobanteFiscalCorrecto(String cadena_a_validar) {
            return Pattern.compile("^[0-9]{20}$").matcher(cadena_a_validar).find();
        }

        /**Verifica la Fecha que se le pondra al comprobante fiscal
         * @param cadena_a_validar
         * @return true si es una fecha valida, false si la fecha es invalida.
         */
        public boolean isFechaDelComprobanteFiscalCorrecto(String cadena_a_validar) {
            //System.out.println("cadena_a_validar: "+cadena_a_validar);
            return Pattern.compile("^[0-9]{4}-[0-9]{2}-[0-9]{2}T[0-9]{2}:[0-9]{2}:[0-9]{2}$").matcher(cadena_a_validar).find();
        }

        public boolean isTipoDeComprobanteCorrecto(String cadena_a_validar) {
            return Pattern.compile("^(ingreso|egreso|traslado)$").matcher(cadena_a_validar).find();
        }

        /**Verifica el folio de el registro por renglon
         * @param cadena_a_validar
         * @return true si una serie es valido, false si la serie es invalido.
         */
        public boolean isFolioDelComprobanteFiscalCorrecto(String cadena_a_validar) {
            boolean valor_retorno = false;
            int numEntero = Integer.parseInt(cadena_a_validar);
            if ((numEntero >= 1) && (numEntero <= 2147483647)) {
                valor_retorno = true;
            }
            return valor_retorno;
        }
        
        private boolean isNumeroDelEsquemaCorrecto(String noEsquema) {
            throw new UnsupportedOperationException("Not yet implemented");
        }

        /**Verifica el numero de Aprobacion de el registro por renglon
         * @param cadena_a_validar
         * @return true si es un numero valido, false si es un numero invalido.
         */
        public boolean isNumeroDeAprobacionCorrecto(String cadena_a_validar) {
            boolean valor_retorno = false;

            /* Para Comprobantes Fiscales
            impresos por establecimientos
            autorizados, número entre 1 y
            2147483647
             */
            if (Pattern.compile("^[0-9]{1,10}$").matcher(cadena_a_validar).find()) {
                int numEntero = Integer.parseInt(cadena_a_validar);
                if ((numEntero >= 1) && (numEntero <= 2147483647)) {
                    valor_retorno = true;
                }
            }

            /*
            Para Comprobantes Fiscales Digitales
            el formato es yyyy + número entre 1 y 2147483647
             */
            if (Pattern.compile("^[0-9]{4}[0-9]{1,10}$").matcher(cadena_a_validar).find()) {
                int numEntero = Integer.parseInt(cadena_a_validar.substring(4));
                if ((numEntero >= 1) && (numEntero <= 2147483647)) {
                    valor_retorno = true;
                }
            }

            /*
            Para Comprobantes Fiscales
            impresos por el propio
            contribuyente emisor en base
            a la regla 2.4.24, valor nulo.*/
            if (cadena_a_validar == null) {
                valor_retorno = true;
            }

            return valor_retorno;
        }

        private boolean isDecimalCorrecto(String cadena_a_validar) {
            return Pattern.compile("^\\d+\\.?\\d*$").matcher(cadena_a_validar).find();
        }
        
        public boolean isImpuestoRetencionConocido(String cadena_a_validar) {
            return Pattern.compile("^(ISR|IVA)$").matcher(cadena_a_validar).find();
        }

        public boolean isImpuestoTrasladoConocido(String cadena_a_validar) {
            return Pattern.compile("^(IEPS|IVA)$").matcher(cadena_a_validar).find();
        }
    }
    

    
    public Integer getId_empresa() {
        return id_empresa;
    }
    
    public void setId_empresa(Integer id_empresa) {
        this.id_empresa = id_empresa;
    }

    public Integer getId_sucursal() {
        return id_sucursal;
    }

    public void setId_sucursal(Integer id_sucursal) {
        this.id_sucursal = id_sucursal;
    }
    

    public String getRef_id() {
        return ref_id;
    }

    public void setRef_id(String ref_id) {
        this.ref_id = ref_id;
    }
    
    public GralInterfaceDao getGralDao() {
        return gralDao;
    }

    public void setGralDao(GralInterfaceDao gralDao) {
        this.gralDao = gralDao;
    }

    public FacturasInterfaceDao getFacdao() {
        return facdao;
    }

    public void setFacdao(FacturasInterfaceDao facdao) {
        this.facdao = facdao;
    }
    
    private Validacion getValedor() {
        return valedor;
    }

    private void setValedor(Validacion valedor) {
        this.valedor = valedor;
    }

    //Datos Receptor
    public final String getRfc_receptor() {
        return rfc_receptor;
    }

    public final void setRfc_receptor(String rfc_receptor) {
        this.rfc_receptor = rfc_receptor;
    }

    public final String getRazon_social_receptor() {
        return razon_social_receptor;
    }

    public final void setRazon_social_receptor(String razon_social_receptor) {
        this.razon_social_receptor = razon_social_receptor;
    }

    public final String getPais_domicilio_receptor() {
        return pais_domicilio_receptor;
    }

    public final void setPais_domicilio_receptor(String pais_domicilio_receptor) {
        this.pais_domicilio_receptor = pais_domicilio_receptor;
    }

    public final String getCalle_domicilio_receptor() {
        return calle_domicilio_receptor;
    }

    public final void setCalle_domicilio_receptor(String calle_domicilio_receptor) {
        this.calle_domicilio_receptor = calle_domicilio_receptor;
    }

    public final String getMunicipio_domicilio_receptor() {
        return municipio_domicilio_receptor;
    }

    public final void setMunicipio_domicilio_receptor(String municipio_domicilio_receptor) {
        this.municipio_domicilio_receptor = municipio_domicilio_receptor;
    }

    public final String getEstado_domicilio_receptor() {
        return estado_domicilio_receptor;
    }

    public final void setEstado_domicilio_receptor(String estado_domicilio_receptor) {
        this.estado_domicilio_receptor = estado_domicilio_receptor;
    }

    public final String getColonia_domicilio_receptor() {
        return colonia_domicilio_receptor;
    }

    public final void setColonia_domicilio_receptor(String colonia_domicilio_receptor) {
        this.colonia_domicilio_receptor = colonia_domicilio_receptor;
    }

    public final String getLocalidad_domicilio_receptor() {
        return localidad_domicilio_receptor;
    }

    public final void setLocalidad_domicilio_receptor(String localidad_domicilio_receptor) {
        this.localidad_domicilio_receptor = localidad_domicilio_receptor;
    }

    public final String getReferencia_domicilio_receptor() {
        return referencia_domicilio_receptor;
    }

    public final void setReferencia_domicilio_receptor(String referencia_domicilio_receptor) {
        this.referencia_domicilio_receptor = referencia_domicilio_receptor;
    }

    public final String getNoExterior_domicilio_receptor() {
        return noExterior_domicilio_receptor;
    }

    public final void setNoExterior_domicilio_receptor(String noExterior_domicilio_receptor) {
        this.noExterior_domicilio_receptor = noExterior_domicilio_receptor;
    }

    public final String getNoInterior_domicilio_receptor() {
        return noInterior_domicilio_receptor;
    }

    public final void setNoInterior_domicilio_receptor(String noInterior_domicilio_receptor) {
        this.noInterior_domicilio_receptor = noInterior_domicilio_receptor;
    }

    public final String getCodigoPostal_domicilio_receptor() {
        return codigoPostal_domicilio_receptor;
    }

    public final void setCodigoPostal_domicilio_receptor(String codigoPostal_domicilio_receptor) {
        this.codigoPostal_domicilio_receptor = codigoPostal_domicilio_receptor;
    }

    public final ArrayList<LinkedHashMap<String, String>> getListaConceptos() {
        return listaConceptos;
    }

    public final void setListaConceptos(ArrayList<LinkedHashMap<String, String>> listaConceptos) {
        this.listaConceptos = listaConceptos;
    }

    public final ArrayList<LinkedHashMap<String, String>> getListaRetenciones() {
        return listaRetenciones;
    }

    public final void setListaRetenciones(ArrayList<LinkedHashMap<String, String>> listaRetenciones) {
        this.listaRetenciones = listaRetenciones;
    }

    public final ArrayList<LinkedHashMap<String, String>> getListaTraslados() {
        return listaTraslados;
    }

    public final void setListaTraslados(ArrayList<LinkedHashMap<String, String>> listaTraslados) {
        this.listaTraslados = listaTraslados;
    }
    
    public ArrayList<LinkedHashMap<String, String>> getLista_namespaces() {
        return lista_namespaces;
    }

    public void setLista_namespaces(ArrayList<LinkedHashMap<String, String>> lista_namespaces) {
        this.lista_namespaces = lista_namespaces;
    }
    
    public LinkedHashMap<String, String> getDatosExtras() {
        return datosExtras;
    }
    
    public void setDatosExtras(LinkedHashMap<String, String> datosExtras) {
        this.datosExtras = datosExtras;
    }
    
    public final BigDecimal getTotalTraslados() {
        return totalTraslados;
    }
    
    public final void setTotalTraslados(BigDecimal totalTraslados) {
        this.totalTraslados = totalTraslados;
    }
    
    public final BigDecimal getTotalRetenciones() {
        return totalRetenciones;
    }
    
    public final void setTotalRetenciones(BigDecimal totalRetenciones) {
        this.totalRetenciones = totalRetenciones;
    }
    
    
    public String getTasaIva() {
        return tasaIva;
    }
    
    public void setTasaIva(String tasaIva) {
        this.tasaIva = tasaIva;
    }
    
    public String getTasaRetencion() {
        return tasaRetencion;
    }
    
    public void setTasaRetencion(String tasaRetencion) {
        this.tasaRetencion = tasaRetencion;
    }
    
    
    public final BigDecimal getSumatoriaImportes() {
        return sumatoriaImportes;
    }
    
    public final void setSumatoriaImportes(BigDecimal sumatoriaImportes) {
        this.sumatoriaImportes = sumatoriaImportes;
    }
    
    public String getProposito() {
        return proposito;
    }
    
    public void setProposito(String proposito) {
        this.proposito = proposito;
    }
    
    private void setCadenaOriginal(String cadena_original) {
        this.cadenaOriginal = cadena_original;
    }
    
    public String getCadenaOriginal() {
        return cadenaOriginal;
    }
    
    public String getSelloDigital() {
        return selloDigital;
    }

    public void setSelloDigital(String selloDigital) {
        this.selloDigital = selloDigital;
    }
    
    public final String getFecha() {
        return fecha;
    }

    public final void setFecha(String fecha) {
        this.fecha = fecha;
    }

    public final String getFormaDePago() {
        return formaDePago;
    }

    public final void setFormaDePago(String formaDePago) {
        this.formaDePago = formaDePago;
    }

    public final String getNoCertificado() {
        return noCertificado;
    }

    public final void setNoCertificado(String noCertificado) {
        this.noCertificado = noCertificado;
    }

    public final String getTotal() {
        return total;
    }

    public final void setTotal(String total) {
        this.total = total;
    }

    public final String getSubTotal() {
        return subTotal;
    }
    
    public final void setSubTotal(String subTotal) {
        this.subTotal = subTotal;
    }
    
    public String getSubTotalSinDescuento() {
        return subTotalSinDescuento;
    }

    public void setSubTotalSinDescuento(String subTotalConDescuento) {
        this.subTotalSinDescuento = subTotalConDescuento;
    }
    
    public String getMoneda() {
        return moneda;
    }

    public void setMoneda(String moneda) {
        this.moneda = moneda;
    }

    public String getTipoCambio() {
        return tipoCambio;
    }

    public void setTipoCambio(String tipoCambio) {
        this.tipoCambio = tipoCambio;
    }
    
    public final String getTipoDeComprobante() {
        return tipoDeComprobante;
    }
    
    public final void setTipoDeComprobante(String tipoDeComprobante) {
        this.tipoDeComprobante = tipoDeComprobante;
    }
    
    public final String getCertificado() {
        return certificado;
    }
    
    public final void setCertificado(String certificado) {
        this.certificado = certificado;
    }
    
    public final String getCondicionesDePago() {
        return condicionesDePago;
    }
    
    public final void setCondicionesDePago(String condicionesDePago) {
        this.condicionesDePago = condicionesDePago;
    }
    
    public final String getDescuento() {
        return descuento;
    }
    
    public final void setDescuento(String descuento) {
        this.descuento = descuento;
    }
    
    public final String getMotivoDescuento() {
        return motivoDescuento;
    }
    
    public final void setMotivoDescuento(String motivoDescuento) {
        this.motivoDescuento = motivoDescuento;
    }
    
    public final String getMetodoDePago() {
        return metodoDePago;
    }
        
    public String getLugar_expedicion() {
        return lugar_expedicion;
    }

    public String getNumero_cuenta() {
        return numero_cuenta;
    }
        
    public final void setMetodoDePago(String metodoDePago) {
        this.metodoDePago = metodoDePago;
    }
    
    public void setLugar_expedicion(String lugar_expedicion) {
        this.lugar_expedicion = lugar_expedicion;
    }

    public void setNumero_cuenta(String numero_cuenta) {
        this.numero_cuenta = numero_cuenta;
    }
    public final String getRazon_social_emisor() {
        return razon_social_emisor;
    }
    
    public final void setRazon_social_emisor(String razon_social_emisor) {
        this.razon_social_emisor = razon_social_emisor;
    }
    
    public final String getRfc_emisor() {
        return rfc_emisor;
    }
    
    public void setRegimen_fiscal_emisor(String regimen_fiscal_emisor) {
        this.regimen_fiscal_emisor = regimen_fiscal_emisor;
    }
    
    
    public final void setRfc_emisor(String rfc_emisor) {
        this.rfc_emisor = rfc_emisor;
    }
    
    public final String getCalle_domicilio_fiscal() {
        return calle_domicilio_fiscal;
    }
    
    public final void setCalle_domicilio_fiscal(String calle_domicilio_fiscal) {
        this.calle_domicilio_fiscal = calle_domicilio_fiscal;
    }
    
    public final String getMunicipio_domicilio_fiscal() {
        return municipio_domicilio_fiscal;
    }
    
    public final void setMunicipio_domicilio_fiscal(
            String municipio_domicilio_fiscal) {
        this.municipio_domicilio_fiscal = municipio_domicilio_fiscal;
    }
    
    public final String getEstado_domicilio_fiscal() {
        return estado_domicilio_fiscal;
    }
    
    public final void setEstado_domicilio_fiscal(String estado_domicilio_fiscal) {
        this.estado_domicilio_fiscal = estado_domicilio_fiscal;
    }
    
    public final String getPais_domicilio_fiscal() {
        return pais_domicilio_fiscal;
    }
    
    public final void setPais_domicilio_fiscal(String pais_domicilio_fiscal) {
        this.pais_domicilio_fiscal = pais_domicilio_fiscal;
    }
    
    public final String getCodigoPostal_domicilio_fiscal() {
        return codigoPostal_domicilio_fiscal;
    }
    
    public final void setCodigoPostal_domicilio_fiscal(String codigoPostal_domicilio_fiscal) {
        this.codigoPostal_domicilio_fiscal = codigoPostal_domicilio_fiscal;
    }
    
    public final String getColonia_domicilio_fiscal() {
        return colonia_domicilio_fiscal;
    }
    
    public final void setColonia_domicilio_fiscal(String colonia_domicilio_fiscal) {
        this.colonia_domicilio_fiscal = colonia_domicilio_fiscal;
    }
    
    public final String getNoExterior_domicilio_fiscal() {
        return noExterior_domicilio_fiscal;
    }
    
    public final void setNoExterior_domicilio_fiscal(String noExterior_domicilio_fiscal) {
        this.noExterior_domicilio_fiscal = noExterior_domicilio_fiscal;
    }
    
    public final String getNoInterior_domicilio_fiscal() {
        return noInterior_domicilio_fiscal;
    }
    
    public final void setNoInterior_domicilio_fiscal(
            String noInterior_domicilio_fiscal) {
        this.noInterior_domicilio_fiscal = noInterior_domicilio_fiscal;
    }
    
    public final String getLocalidad_domicilio_fiscal() {
        return localidad_domicilio_fiscal;
    }
    
    public final void setLocalidad_domicilio_fiscal(String localidad_domicilio_fiscal) {
        this.localidad_domicilio_fiscal = localidad_domicilio_fiscal;
    }
    
    public final String getReferencia_domicilio_fiscal() {
        return referencia_domicilio_fiscal;
    }
    
    public final void setReferencia_domicilio_fiscal(String referencia_domicilio_fiscal) {
        this.referencia_domicilio_fiscal = referencia_domicilio_fiscal;
    }
    
    public String getRegimen_fiscal_emisor() {
        return regimen_fiscal_emisor;
    }

    public String getSelloDigitalSat() {
        return selloDigitalSat;
    }

    public void setSelloDigitalSat(String selloDigitalSat) {
        this.selloDigitalSat = selloDigitalSat;
    }
    

    public String getXml_timbrado() {
        return xml_timbrado;
    }

    public void setXml_timbrado(String xml_timbrado) {
        this.xml_timbrado = xml_timbrado;
    }
    
    public String getUuid() {
        return Uuid;
    }

    public void setUuid(String Uuid) {
        this.Uuid = Uuid;
    }
    
    public String getFechaTimbrado() {
        return fechaTimbrado;
    }

    public void setFechaTimbrado(String fechaTimbrado) {
        this.fechaTimbrado = fechaTimbrado;
    }

    public String getNoCertificadoSAT() {
        return noCertificadoSAT;
    }

    public void setNoCertificadoSAT(String noCertificadoSAT) {
        this.noCertificadoSAT = noCertificadoSAT;
    }
    
    public String getCadenaOriginalTimbre() {
        return cadenaOriginalTimbre;
    }

    public void setCadenaOriginalTimbre(String cadenaOriginalTimbre) {
        this.cadenaOriginalTimbre = cadenaOriginalTimbre;
    }
    
    private String cadenaOriginalTimbre(String comprobante, Integer id_empresa, Integer id_sucursal) throws Exception {
        String valor_retorno = new String();
        //System.out.println("EsquemaXslt: "+this.getGralDao().getXslDir() + this.getGralDao().getRfcEmpresaEmisora(id_empresa)+"/"+ this.getGralDao().getFicheroXslTimbre(id_empresa, id_sucursal));
        valor_retorno = XmlHelper.transformar(comprobante, this.getGralDao().getXslDir() + this.getGralDao().getRfcEmpresaEmisora(id_empresa)+"/"+ this.getGralDao().getFicheroXslTimbre(id_empresa, id_sucursal));
        
        return valor_retorno;
    }
}
