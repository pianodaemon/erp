/********************************************/
/**Written by Edwin Plauchu******************/
/*****************************Agnux Mexico***/
/********************************************/
package com.agnux.cfd.v2;

import com.agnux.common.helpers.FileHelper;
import com.agnux.common.helpers.StringHelper;
import com.agnux.common.helpers.XmlHelper;

import com.agnux.kemikal.interfacedaos.FacturasInterfaceDao;
import com.agnux.kemikal.interfacedaos.GralInterfaceDao;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.logging.Level;
import java.util.logging.Logger;

import java.util.HashMap;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

public final class BeanFacturador {
    
    private static final Logger log = Logger.getLogger(BeanFacturador.class.getName());
    @Autowired
    @Qualifier("daoGral")
    private GralInterfaceDao gralDao;
    
    @Autowired
    @Qualifier("daoFacturas")
    private FacturasInterfaceDao facdao;
    
    public static enum Proposito {
        FACTURA, NOTA_CREDITO, NOTA_CARGO
    };
    
    private String proposito;
    private String fecha;
    private String formaDePago;
    private String noCertificado;
    private String total;
    private String subTotal;
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
    
    public void start() {
        
        try {
            
            Integer id_empresa = Integer.parseInt(this.getDatosExtras().get("empresa_id"));
            Integer id_sucursal = Integer.parseInt(this.getDatosExtras().get("sucursal_id"));
            
            String comprobante_firmado = this.generarComprobanteFirmado(id_empresa, id_sucursal);
            
            BeanFromCfdXml pop = new BeanFromCfdXml(comprobante_firmado.getBytes("UTF-8"));
            
            String xml_file_name = new String();
            
            xml_file_name += pop.getSerie();
            xml_file_name += pop.getFolio();
            xml_file_name += ".xml";
            
            boolean fichero_xml_ok = FileHelper.createFileWithText(this.getGralDao().getCfdEmitidosDir() + this.getRfc_emisor(), xml_file_name, comprobante_firmado);
            
            if (fichero_xml_ok) {
                //Aqui va la rutina que mete los datos de este comprobante fiscal a la tabla erp_facturas
                //De preferencia un store procedure....
                
                String cadena_conceptos = this.getFacdao().formar_cadena_conceptos(pop.getListaConceptos());
                String cadena_imp_trasladados = this.getFacdao().formar_cadena_traslados(pop.getTotalImpuestosTrasladados(),this.getTasaIva());
                String cadena_imp_retenidos = this.getFacdao().formar_cadena_traslados(pop.getTotalImpuestosRetenidos(),this.getTasaRetencion());
                
                Integer id_usuario = Integer.parseInt(this.getDatosExtras().get("usuario_id"));
                String tipo_cambio = this.getDatosExtras().get("tipo_cambio");
                String app_selected = this.getDatosExtras().get("app_selected");
                String command_selected = this.getDatosExtras().get("command_selected");
                String extra_data_array = this.getDatosExtras().get("extra_data_array");
                
                String estado_comprobante="1";
                String regimen_fiscal = pop.getRegimenFiscalEmisor();
                String metodo_pago = pop.getMetodoDePago();
                String num_cuenta = pop.getNumeroCuenta();
                String lugar_de_expedicion = pop.getLugarExpedicion();
                
                String data_string="";
                
                switch (Proposito.valueOf(this.getProposito())) {
                    case FACTURA:
                        Integer prefactura_id = Integer.parseInt(this.getDatosExtras().get("prefactura_id"));
                        String refacturar = this.getDatosExtras().get("refacturar");
                        String id_moneda = this.getDatosExtras().get("moneda_id");
                        this.getFacdao().fnSalvaDatosFacturas(
                                pop.getRfc_receptor(),
                                pop.getSerie(),
                                pop.getFolio(),
                                pop.getNoAprobacion(),
                                pop.getTotal(),
                                pop.getTotalImpuestosTrasladados(),
                                estado_comprobante,
                                xml_file_name ,
                                pop.getFecha(),
                                pop.getRazon_social_receptor(),
                                pop.getTipoDeComprobante(),
                                this.getProposito(),
                                pop.getAnoAprobacion() ,
                                cadena_conceptos,
                                cadena_imp_trasladados,
                                cadena_imp_retenidos,
                                prefactura_id,
                                id_usuario,
                                Integer.parseInt(id_moneda),
                                tipo_cambio,
                                refacturar,
                                regimen_fiscal,
                                metodo_pago,
                                num_cuenta,
                                lugar_de_expedicion
                        );
                        
                        this.getGralDao().actualizarFolioFactura(id_empresa, id_sucursal);
                        break;
                        
                    case NOTA_CREDITO:
                        Integer id_nota_credito = Integer.parseInt(this.getDatosExtras().get("id_nota_credito"));
                        String fac_saldado = this.getDatosExtras().get("fac_saldado");
                        data_string = 
                                app_selected+"___"+
                                command_selected+"___"+
                                id_usuario+"___"+
                                id_nota_credito+"___"+
                                pop.getRfc_receptor()+"___"+
                                pop.getSerie()+"___"+
                                pop.getFolio()+"___"+
                                pop.getNoAprobacion()+"___"+
                                pop.getTotal()+"___"+
                                pop.getTotalImpuestosTrasladados()+"___"+
                                estado_comprobante+"___"+
                                xml_file_name+"___"+
                                pop.getFecha()+"___"+
                                pop.getRazon_social_receptor()+"___"+
                                pop.getTipoDeComprobante()+"___"+
                                this.getProposito()+"___"+
                                pop.getAnoAprobacion() +"___"+
                                cadena_conceptos+"___"+
                                cadena_imp_trasladados+"___"+
                                cadena_imp_retenidos+"___"+
                                tipo_cambio+"___"+
                                regimen_fiscal+"___"+
                                metodo_pago+"___"+
                                num_cuenta+"___"+
                                lugar_de_expedicion+"___"+
                                fac_saldado;
                                
                                String actualizo = this.getFacdao().selectFunctionForFacAdmProcesos(data_string, extra_data_array);
                                this.getGralDao().actualizarFolioNotaCredito(id_empresa, id_sucursal);
                                
                        break;
                        
                        case NOTA_CARGO:
                            this.getGralDao().actualizarFolioNotaCargo(id_empresa, id_sucursal);
                            break;
                }
                

                
            } else {
                throw new Exception("Fallo al generar fichero xml: " + xml_file_name);
            }
            
        } catch (Exception ex) {
            Logger.getLogger(BeanFacturador.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        
    }
    
    private String generarComprobanteFirmado(Integer id_empresa, Integer id_sucursal) throws Exception {
        
        this.checkdata();
        
        String valor_retorno = new String();
        
        CfdXmlBuilder cfd = new CfdXmlBuilder();
        cfd.init();
        
        
        cfd.construyeNodoFactura(
                this.getTipoDeComprobante(),
                this.getCondicionesDePago(),
                this.getFormaDePago(),
                this.getFecha(),
                this.getSubTotal(),
                this.getTotal(),
                this.getNoCertificado(),
                this.getCertificado(),
                this.getMetodoDePago(),
                this.getLugar_expedicion(),
                this.getNumero_cuenta() );
                
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
                this.getCalle_domicilio_receptor(),
                this.getColonia_domicilio_receptor(),
                this.getMunicipio_domicilio_receptor(),
                this.getEstado_domicilio_receptor(),
                this.getCodigoPostal_domicilio_receptor());
        
        cfd.configurarNodoConceptos(this.getListaConceptos());
        
        cfd.configurarImpuestos(this.getListaRetenciones(), this.getListaTraslados());
        
        String comprobante_sin_firmar = cfd.getOutXmlString();
        
        comprobante_sin_firmar = comprobante_sin_firmar.replaceAll("@SUMIMPUESTOS_RETENIDOS", this.getTotalRetenciones().toString());
        comprobante_sin_firmar = comprobante_sin_firmar.replaceAll("@SUMIMPUESTOS_TRASLADADOS", this.getTotalTraslados().toString());
        
        
        switch (Proposito.valueOf(this.getProposito())) {
            case FACTURA:
                String folio_factura = this.getGralDao().getFolioFactura(id_empresa, id_sucursal);
                comprobante_sin_firmar = comprobante_sin_firmar.replaceAll("@FOLIO", folio_factura);
                comprobante_sin_firmar = comprobante_sin_firmar.replaceAll("@SERIE", this.getGralDao().getSerieFactura(id_empresa, id_sucursal));
                comprobante_sin_firmar = comprobante_sin_firmar.replaceAll("@ANO_APROBACION", this.getGralDao().getAnoAprobacionFactura(id_empresa, id_sucursal));
                comprobante_sin_firmar = comprobante_sin_firmar.replaceAll("@NOAPROBACION", this.getGralDao().getNoAprobacionFactura(id_empresa, id_sucursal));
                //this.getGralDao().actualizarFolioFactura(id_empresa, id_sucursal);
                break;
                
            case NOTA_CREDITO:
                String folio_credito = this.getGralDao().getFolioNotaCredito(id_empresa, id_sucursal);
                comprobante_sin_firmar = comprobante_sin_firmar.replaceAll("@FOLIO", folio_credito);
                comprobante_sin_firmar = comprobante_sin_firmar.replaceAll("@SERIE", this.getGralDao().getSerieNotaCredito(id_empresa, id_sucursal));
                comprobante_sin_firmar = comprobante_sin_firmar.replaceAll("@ANO_APROBACION", this.getGralDao().getAnoAprobacionNotaCredito(id_empresa, id_sucursal));
                comprobante_sin_firmar = comprobante_sin_firmar.replaceAll("@NOAPROBACION", this.getGralDao().getNoAprobacionNotaCredito(id_empresa, id_sucursal));
                //this.getGralDao().actualizarFolioNotaCredito(id_empresa, id_sucursal);
                break;
                
            case NOTA_CARGO:
                String folio_notadecargo = this.getGralDao().getFolioNotaCargo(id_empresa, id_sucursal);
                comprobante_sin_firmar = comprobante_sin_firmar.replaceAll("@FOLIO", folio_notadecargo);
                comprobante_sin_firmar = comprobante_sin_firmar.replaceAll("@SERIE", this.getGralDao().getSerieNotaCargo(id_empresa, id_sucursal));
                comprobante_sin_firmar = comprobante_sin_firmar.replaceAll("@ANO_APROBACION", this.getGralDao().getAnoAprobacionNotaCargo(id_empresa, id_sucursal));
                comprobante_sin_firmar = comprobante_sin_firmar.replaceAll("@NOAPROBACION", this.getGralDao().getNoAprobacionNotaCargo(id_empresa, id_sucursal));
                //this.getGralDao().actualizarFolioNotaCargo(id_empresa, id_sucursal);
                break;
        }
        
        String cadena_original = this.cadenaOriginal(comprobante_sin_firmar, id_empresa, id_sucursal);
        this.setCadenaOriginal(cadena_original);
        
        
        String ruta_fichero_llave = this.getGralDao().getSslDir() + this.getGralDao().getRfcEmpresaEmisora(id_empresa)+ "/" + this.getGralDao().getFicheroLlavePrivada(id_empresa, id_sucursal);
        //System.out.println("ruta_fichero_llave: "+ruta_fichero_llave);
        
        String sello = CryptoEngine.sign(ruta_fichero_llave, this.getGralDao().getPasswordLlavePrivada(id_empresa, id_sucursal), cadena_original);
        valor_retorno = comprobante_sin_firmar.replaceAll("@SELLO_DIGITAL", sello);
        
        this.setSelloDigital(sello);
        //System.out.println("valor_retorno genera comrobante:"+valor_retorno);
        return valor_retorno;
    }
    
    private void checkdata() throws Exception {
        this.validar_datos();
        this.validar_Conceptos();
        this.validarImpuestos(this.getListaRetenciones(), this.getListaTraslados());
    }
    
    public void init(HashMap<String, String> data, ArrayList<LinkedHashMap<String, String>> conceptos, ArrayList<LinkedHashMap<String, String>> impuestos_retenidos, ArrayList<LinkedHashMap<String, String>> impuestos_trasladados, String propos, LinkedHashMap<String,String> extras, Integer id_empresa, Integer id_sucursal) {
        
        this.setValedor(new Validacion());
        this.setProposito(propos);
        
        
        System.out.println("Leyendo fichero: "+this.getGralDao().getSslDir() + this.getGralDao().getRfcEmpresaEmisora(id_empresa)+ "/" + this.getGralDao().getCertificadoEmpresaEmisora(id_empresa, id_sucursal));
        
        //Datos Base del CFD ------- INICIO -----------------------------------
        
        this.setCertificado(CryptoEngine.encodeCertToBase64(this.getGralDao().getSslDir() + this.getGralDao().getRfcEmpresaEmisora(id_empresa)+ "/" + this.getGralDao().getCertificadoEmpresaEmisora(id_empresa, id_sucursal)));
        
        this.setFecha(data.get("comprobante_attr_fecha"));
        
        switch (Proposito.valueOf(this.getProposito())) {
            case FACTURA:
                
                this.setTipoDeComprobante("ingreso");
                
                break;
                
            case NOTA_CREDITO:
                
                this.setTipoDeComprobante("egreso");
                
                break;
                
            case NOTA_CARGO:
                
                this.setTipoDeComprobante("ingreso");
                
                break;
        }
        
        this.setCondicionesDePago(StringHelper.normalizaString(StringHelper.remueve_tildes(data.get("comprobante_attr_condicionesdepago")).replace("'", "")));
        this.setFormaDePago(StringHelper.normalizaString(StringHelper.remueve_tildes(data.get("comprobante_attr_formadepago"))));
        this.setMotivoDescuento(StringHelper.normalizaString(StringHelper.remueve_tildes(data.get("comprobante_attr_motivodescuento")).replace("'", "")));
        
        this.setDescuento(data.get("comprobante_attr_descuento"));
        this.setSubTotal(data.get("comprobante_attr_subtotal"));
        this.setTotal(data.get("comprobante_attr_total"));
        
        this.setMetodoDePago(StringHelper.normalizaString(StringHelper.remueve_tildes(data.get("comprobante_attr_metododepago")).replace("'", "")));
        this.setLugar_expedicion(StringHelper.normalizaString(StringHelper.remueve_tildes( this.getGralDao().getMunicipioSucursalEmisora(id_sucursal).toUpperCase()+", "+this.getGralDao().getEstadoSucursalEmisora(id_sucursal).toUpperCase() ).replace("'", "")));
        this.setNumero_cuenta(data.get("comprobante_attr_numerocuenta"));
        
        //Datos Base del CFD ------- FIN --------------------------------------
        
        
        //Datos del Emisor ------- INICIO ------------------------------------- 
        this.setRazon_social_emisor(StringHelper.normalizaString(StringHelper.remueve_tildes(this.getGralDao().getRazonSocialEmpresaEmisora(id_empresa))));
        this.setRfc_emisor(StringHelper.normalizaString(StringHelper.remueve_tildes(this.getGralDao().getRfcEmpresaEmisora(id_empresa))));
        this.setRegimen_fiscal_emisor(StringHelper.normalizaString(StringHelper.remueve_tildes(this.getGralDao().getRegimenFiscalEmpresaEmisora(id_empresa))));
        
        this.setNoCertificado(this.getGralDao().getNoCertificadoEmpresaEmisora(id_empresa, id_sucursal));
        this.setCalle_domicilio_fiscal(StringHelper.normalizaString(StringHelper.remueve_tildes(this.getGralDao().getCalleDomicilioFiscalEmpresaEmisora(id_empresa))));
        this.setCodigoPostal_domicilio_fiscal(StringHelper.normalizaString(StringHelper.remueve_tildes(this.getGralDao().getCpDomicilioFiscalEmpresaEmisora(id_empresa))));
        this.setColonia_domicilio_fiscal(StringHelper.normalizaString(StringHelper.remueve_tildes(this.getGralDao().getColoniaDomicilioFiscalEmpresaEmisora(id_empresa))));
        this.setEstado_domicilio_fiscal(StringHelper.normalizaString(StringHelper.remueve_tildes(this.getGralDao().getEstadoDomicilioFiscalEmpresaEmisora(id_empresa))));
        this.setLocalidad_domicilio_fiscal(StringHelper.normalizaString(StringHelper.remueve_tildes(this.getGralDao().getLocalidadDomicilioFiscalEmpresaEmisora(id_empresa))));
        this.setMunicipio_domicilio_fiscal(StringHelper.normalizaString(StringHelper.remueve_tildes(this.getGralDao().getMunicipioDomicilioFiscalEmpresaEmisora(id_empresa))));
        this.setNoExterior_domicilio_fiscal(StringHelper.normalizaString(StringHelper.remueve_tildes(this.getGralDao().getNoExteriorDomicilioFiscalEmpresaEmisora(id_empresa))));
        this.setNoInterior_domicilio_fiscal(StringHelper.normalizaString(StringHelper.remueve_tildes(this.getGralDao().getNoInteriorDomicilioFiscalEmpresaEmisora(id_empresa))));
        this.setPais_domicilio_fiscal(StringHelper.normalizaString(StringHelper.remueve_tildes(this.getGralDao().getPaisDomicilioFiscalEmpresaEmisora(id_empresa))));
        this.setReferencia_domicilio_fiscal(StringHelper.normalizaString(StringHelper.remueve_tildes(this.getGralDao().getReferenciaDomicilioFiscalEmpresaEmisora(id_empresa))));
        
        
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
    
    private String cadenaOriginal(String comprobante_sin_firmar, Integer id_empresa, Integer id_sucursal) throws Exception {
        String valor_retorno = new String();
        //System.out.println("Comprobante sin firmar:"+comprobante_sin_firmar);
        //valor_retorno = XmlHelper.transformar(comprobante_sin_firmar, this.getGralDao().getXslDir() + "get_original_string.xsl");
        valor_retorno = XmlHelper.transformar(comprobante_sin_firmar, this.getGralDao().getXslDir() + this.getGralDao().getRfcEmpresaEmisora(id_empresa)+"/"+ this.getGralDao().getFicheroXsl(id_empresa, id_sucursal));
        System.out.println("EsquemaXslt: "+this.getGralDao().getXslDir() + this.getGralDao().getRfcEmpresaEmisora(id_empresa)+"/"+ this.getGralDao().getFicheroXsl(id_empresa, id_sucursal));
        return valor_retorno;
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
    
}
