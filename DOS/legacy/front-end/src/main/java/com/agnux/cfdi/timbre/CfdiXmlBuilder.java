/**
 *
 * @author Noé Martínez
 * gpmarsan@gmail.com
 * 06/diciembre/2012
 * 
 * Ésta clase genera el xml sin Timbre Fiscal
 * 
 */

package com.agnux.cfdi.timbre;

import java.io.StringWriter;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.apache.commons.lang.StringEscapeUtils;

public class CfdiXmlBuilder {
	private DocumentBuilderFactory dbf;
	private Document doc;
	private DocumentBuilder db;
	private DOMImplementation domImpl;
        
	public void init() throws Exception{
            this.setDbf(DocumentBuilderFactory.newInstance());
            this.setDb(this.getDbf().newDocumentBuilder());
            this.setDomImpl(this.getDb().getDOMImplementation());
	}
        
	public void construyeNodoFactura(ArrayList<LinkedHashMap<String,String>> namespaces, String serie, String folio, String tipoDeComprobante,String condicionesDePago,String formaDePago,String fecha,String subTotal,String total, String moneda, String tipo_cambio, String no_certificado_emisor,String certificado, String metodoDePago, String LugarExpedicion, String numTarjeta, String descuento, String motivo_descuento) {
            String schemaLocation = new String();
            schemaLocation="";
            
            Document tmp = this.getDomImpl().createDocument("", "cfdi:Comprobante", null);
            
            if(namespaces.size()>0){
                for( LinkedHashMap<String,String> i : namespaces ){
                    String key = i.get("key_xmlns");
                    String value = i.get("xmlns");
                    String location = i.get("schemalocation");
                    if(!key.equals("") && !value.equals("")){ 
                        tmp.getDocumentElement().setAttribute(key,value); 
                    }
                    if(!location.equals("")){
                        if(!schemaLocation.equals("")){schemaLocation+=" ";}
                        schemaLocation += location;
                    }
                }
            }
            
            tmp.getDocumentElement().setAttribute("xsi:schemaLocation",schemaLocation);
            
            /*
            tmp.getDocumentElement().setAttribute("xmlns:xsi","http://www.w3.org/2001/XMLSchema-instance");
            tmp.getDocumentElement().setAttribute("xmlns:cfdi","http://www.sat.gob.mx/cfd/3");
            tmp.getDocumentElement().setAttribute("xmlns:tfd","http://www.sat.gob.mx/TimbreFiscalDigital");
            tmp.getDocumentElement().setAttribute("xmlns:bfa2","http://www.buzonfiscal.com/ns/addenda/bf/2");
            tmp.getDocumentElement().setAttribute("xmlns:nomina","http://ww.sat.gob.mx/nomina");
            tmp.getDocumentElement().setAttribute("xmlns:detallista","http://www.sat.gob.mx/detallista");
            tmp.getDocumentElement().setAttribute("xmlns:leyendasFisc","http://www.sat.gob.mx/leyendasFiscales");
            tmp.getDocumentElement().setAttribute("xmlns:donat","http://www.sat.gob.mx/donat");
            tmp.getDocumentElement().setAttribute("xmlns:ecc","http://www.sat.gob.mx/ecc");
            tmp.getDocumentElement().setAttribute("xmlns:terceros","http://www.sat.gob.mx/terceros");
            tmp.getDocumentElement().setAttribute("xmlns:iedu","http://www.sat.gob.mx/iedu");
            tmp.getDocumentElement().setAttribute("xmlns:implocal","http://www.sat.gob.mx/implocal");
            tmp.getDocumentElement().setAttribute("xmlns:pfic","http://www.sat.gob.mx/pfic");
            tmp.getDocumentElement().setAttribute("xmlns:ventavehiculos","http://www.sat.gob.mx/ventavehiculos");
            tmp.getDocumentElement().setAttribute("xmlns:divisas","http://www.sat.gob.mx/divisas");
            tmp.getDocumentElement().setAttribute("xmlns:tpe","http://www.sat.gob.mx/TuristaPasajeroExtranjero");
            
            tmp.getDocumentElement().setAttribute("xsi:schemaLocation",
              "http://www.sat.gob.mx/cfd/3 http://www.sat.gob.mx/sitio_internet/cfd/3/cfdv32.xsd "
            + "http://www.sat.gob.mx/TimbreFiscalDigital http://www.sat.gob.mx/sitio_internet/TimbreFiscalDigital/TimbreFiscalDigital.xsd "
            + "http://www.buzonfiscal.com/ns/addenda/bf/2 http://www.buzonfiscal.com/schema/xsd/Addenda_BF_v2.2.xsd "
            + "http://sat.gob.mx/nomina http://sat.gob.mx/cfd/nomina/nomina11.xsd "
            + "http://www.sat.gob.mx/detallista http://www.sat.gob.mx/sitio_internet/cfd/detallista/detallista.xsd "
            + "http://www.sat.gob.mx/leyendasFiscales http://www.sat.gob.mx/sitio_internet/cfd/leyendasFiscales/leyendasFisc.xsd "
            + "http://www.sat.gob.mx/donat http://www.sat.gob.mx/sitio_internet/cfd/donat/donat11.xsd "
            + "http://www.sat.gob.mx/ecc http://www.sat.gob.mx/sitio_internet/cfd/ecc/ecc.xsd "
            + "http://www.sat.gob.mx/terceros http://www.sat.gob.mx/sitio_internet/cfd/terceros/terceros11.xsd "
            + "http://www.sat.gob.mx/iedu http://www.sat.gob.mx/sitio_internet/cfd/iedu/iedu.xsd "
            + "http://www.sat.gob.mx/implocal http://www.sat.gob.mx/sitio_internet/cfd/implocal/implocal.xsd "
            + "http://www.sat.gob.mx/pfic http://www.sat.gob.mx/sitio_internet/cfd/pfic/pfic.xsd "
            + "http://www.sat.gob.mx/ventavehiculos http://www.sat.gob.mx/sitio_internet/cfd/ventavehiculos/ventavehiculos.xsd "
            + "http://www.sat.gob.mx/divisas http://www.sat.gob.mx/sitio_internet/cfd/divisas/Divisas.xsd "
            + "http://www.sat.gob.mx/TuristaPasajeroExtranjero http://www.sat.gob.mx/sitio_internet/cfd/TuristaPasajeroExtranjero/TuristaPasajeroExtranjero.xsd"
            + "");
            */
            
            tmp.getDocumentElement().setAttribute("version","3.2");
            tmp.getDocumentElement().setAttribute("certificado",certificado);
            tmp.getDocumentElement().setAttribute("noCertificado",no_certificado_emisor);
            tmp.getDocumentElement().setAttribute("sello","@SELLO_DIGITAL");
            tmp.getDocumentElement().setAttribute("tipoDeComprobante",tipoDeComprobante);
            
            if(!serie.equals("")){ tmp.getDocumentElement().setAttribute("serie",serie); }
            //if(!folio.equals("")){ tmp.getDocumentElement().setAttribute("folio",folio); }
            tmp.getDocumentElement().setAttribute("folio",folio); 
            
            tmp.getDocumentElement().setAttribute("LugarExpedicion",StringEscapeUtils.escapeHtml(LugarExpedicion));
            tmp.getDocumentElement().setAttribute("fecha",fecha);
            tmp.getDocumentElement().setAttribute("formaDePago",StringEscapeUtils.escapeHtml(formaDePago));
            
            
            if(!metodoDePago.equals("") && metodoDePago!=null){ tmp.getDocumentElement().setAttribute("metodoDePago",StringEscapeUtils.escapeHtml(metodoDePago)); }
            if(!numTarjeta.equals("") && numTarjeta!=null){ tmp.getDocumentElement().setAttribute("NumCtaPago",numTarjeta); }
            tmp.getDocumentElement().setAttribute("subTotal",subTotal);
            if(!descuento.equals("") && !descuento.equals("0.00")){ tmp.getDocumentElement().setAttribute("descuento",descuento); }
            if(!motivo_descuento.equals("") && motivo_descuento!=null){ tmp.getDocumentElement().setAttribute("motivoDescuento",motivo_descuento); }
            tmp.getDocumentElement().setAttribute("total",total);
            if(!moneda.equals("") && moneda!=null){ tmp.getDocumentElement().setAttribute("Moneda",moneda); }
            tmp.getDocumentElement().setAttribute("TipoCambio",tipo_cambio);
            
            this.setDoc(tmp);
	}
        
	public void configurarNodoEmisor(String razon_social_emisor,String rfc_emisor,String pais,String estado,String municipio, String colonia,String calle,String no_exterior,String codigo_postal, String regimenfiscal){
            Document tmp = this.getDoc();
            Element element = tmp.createElement("cfdi:Emisor");
            element.setAttribute("nombre", razon_social_emisor );
            element.setAttribute("rfc", rfc_emisor );
            
            Element extra = this.getDoc().createElement("cfdi:DomicilioFiscal");
            if(!codigo_postal.equals("") && codigo_postal!=null){ extra.setAttribute("codigoPostal",codigo_postal); }
            if(!pais.equals("") && pais!=null){ extra.setAttribute("pais",pais); }
            if(!estado.equals("") && estado!=null){ extra.setAttribute("estado",estado); }
            if(!municipio.equals("") && municipio!=null){ extra.setAttribute("municipio",municipio); }
            if(!calle.equals("") && calle!=null){ extra.setAttribute("calle",calle); }
            if(!colonia.equals("") && colonia!=null){ extra.setAttribute("colonia",colonia); }
            if(!no_exterior.equals("") && no_exterior!=null){ extra.setAttribute("noExterior",no_exterior); }
            element.appendChild(extra);
            
            Element extra2 = this.getDoc().createElement("cfdi:RegimenFiscal");
            extra2.setAttribute("Regimen",regimenfiscal);
            element.appendChild(extra2);
            
            tmp.getDocumentElement().appendChild(element);
            this.setDoc(tmp);
	}
        
	public void configurarNodoReceptor(String nombre,String rfc,String pais, String noExterior, String noInterior, String calle, String colonia, String municipio, String estado, String codigoPostal ){
            Document tmp = this.getDoc();
            Element element = tmp.createElement("cfdi:Receptor");
            element.setAttribute("nombre", StringEscapeUtils.escapeHtml(nombre) );
            element.setAttribute("rfc", rfc );
            
            Element extra = this.getDoc().createElement("cfdi:Domicilio");
            if(!codigoPostal.equals("") && codigoPostal!=null){ extra.setAttribute("codigoPostal",codigoPostal); }
            if(!pais.equals("") && pais!=null){ extra.setAttribute("pais",StringEscapeUtils.escapeHtml(pais)); }
            if(!estado.equals("") && estado!=null){ extra.setAttribute("estado",StringEscapeUtils.escapeHtml(estado)); }
            if(!municipio.equals("") && municipio!=null){ extra.setAttribute("municipio",StringEscapeUtils.escapeHtml(municipio)); }
            if(!colonia.equals("") && colonia!=null){ extra.setAttribute("colonia",StringEscapeUtils.escapeHtml(colonia)); }
            if(!calle.equals("") && calle!=null){ extra.setAttribute("calle",StringEscapeUtils.escapeHtml(calle)); }
            if(!noExterior.equals("") && noExterior!=null){ extra.setAttribute("noExterior",noExterior); }
            if(!noInterior.equals("") && noInterior!=null){ extra.setAttribute("noInterior",noInterior); }
            element.appendChild(extra);
            
            tmp.getDocumentElement().appendChild(element);
            this.setDoc(tmp);
	}
        
        
	public void configurarNodoConceptos(ArrayList<LinkedHashMap<String,String>> lista_de_conceptos) throws Exception {
            Document tmp = this.getDoc();
            Element element = tmp.createElement("cfdi:Conceptos");
            if (lista_de_conceptos.size() > 0){
                for( LinkedHashMap<String,String> i : lista_de_conceptos ){
                    Element c = this.getDoc().createElement("cfdi:Concepto");
                    @SuppressWarnings("rawtypes")
                    Iterator it = i.entrySet().iterator();
                    while (it.hasNext()) {
                        @SuppressWarnings("rawtypes")
                        Map.Entry elemento_hash = (Map.Entry)it.next();
                        String llave = (String)elemento_hash.getKey();
                        String valor = (String)elemento_hash.getValue();
                        
                        if (llave.equals("noIdentificacion")){ if(!valor.equals("") && valor!=null){ c.setAttribute(llave , valor);} }
                        if (llave.equals("descripcion")){ c.setAttribute(llave , StringEscapeUtils.escapeHtml(valor)); }
                        if (llave.equals("unidad")){ c.setAttribute(llave , valor); }
                        if (llave.equals("cantidad")){ c.setAttribute(llave , valor);  }
                        if (llave.equals("valorUnitario")){ c.setAttribute(llave , valor); }
                        if (llave.equals("importe")){ c.setAttribute(llave , valor); }
                        
                        if (llave.equals("infoAduana")){
                            Element extra = this.getDoc().createElement("InformacionAduanera");
                            extra.setAttribute("numero", valor.split("|")[0]);
                            extra.setAttribute("fecha", valor.split("|")[1]);
                            extra.setAttribute("aduana", valor.split("|")[2]);
                            c.appendChild(extra);
                        }
                    }
                    element.appendChild(c);
                }
                tmp.getDocumentElement().appendChild(element);
            }
            this.setDoc(tmp);
	}
        
        public void configurarImpuestos(ArrayList<LinkedHashMap<String,String>> lista_de_retenidos,ArrayList<LinkedHashMap<String,String>> lista_de_traslados){
            Document tmp = this.getDoc();
            Element root = tmp.createElement("cfdi:Impuestos");
            root.setAttribute("totalImpuestosRetenidos", "@SUMIMPUESTOS_RETENIDOS");

            if (lista_de_traslados.size() > 0){
                //Se incluye solamente cuando hay elementos en la lista de impuestos trasladados
                root.setAttribute("totalImpuestosTrasladados", "@SUMIMPUESTOS_TRASLADADOS" );
            }


            Element child_1 = tmp.createElement("cfdi:Retenciones");
            if (lista_de_retenidos.size() > 0){
                for( LinkedHashMap<String,String> i : lista_de_retenidos ){
                    Element child_1_1 = tmp.createElement("cfdi:Retencion");

                    @SuppressWarnings("rawtypes")
                    Iterator it = i.entrySet().iterator();
                    while (it.hasNext()) {
                        @SuppressWarnings("rawtypes")
                        Map.Entry elemento_hash = (Map.Entry)it.next();
                        String llave = (String)elemento_hash.getKey();
                        String valor = (String)elemento_hash.getValue();
                        if (llave.equals("importe")){ child_1_1.setAttribute(llave , valor);  }
                        if (llave.equals("impuesto")){ child_1_1.setAttribute(llave , valor); }
                    }
                    child_1.appendChild(child_1_1);
                }
            }
            root.appendChild(child_1);
            
            if (lista_de_traslados.size() > 0){
                Element child_2 = tmp.createElement("cfdi:Traslados");
                for( LinkedHashMap<String,String> i : lista_de_traslados ){
                    Element child_2_1 = tmp.createElement("cfdi:Traslado");

                    @SuppressWarnings("rawtypes")
                    Iterator it = i.entrySet().iterator();
                    while (it.hasNext()) {
                        Map.Entry elemento_hash = (Map.Entry)it.next();
                        String llave = (String)elemento_hash.getKey();
                        String valor = (String)elemento_hash.getValue();
                        if (llave.equals("importe")){ child_2_1.setAttribute(llave , valor);  }
                        if (llave.equals("impuesto")){ child_2_1.setAttribute(llave , valor); }
                        if (llave.equals("tasa")){ child_2_1.setAttribute(llave , valor); }
                    }
                    child_2.appendChild(child_2_1);
                }
                root.appendChild(child_2);
            }
            
            tmp.getDocumentElement().appendChild(root);
            this.setDoc(tmp);
	}
        
        
        
        
        public void configurarComplementos(LinkedHashMap<String,Object> dataNomina){
            Document tmp = this.getDoc();
            Element root = tmp.createElement("cfdi:Complemento");
            
            //Inicia complemento Nomina
            if (dataNomina.get("valor").equals("true")){
                Element child_1 = tmp.createElement("nomina:Nomina");
                child_1.setAttribute("xmlns:xsi","http://www.w3.org/2001/XMLSchema-instance");
                child_1.setAttribute("xmlns:nomina", "http://www.sat.gob.mx/nomina");
                child_1.setAttribute("xsi:schemaLocation","http://www.sat.gob.mx/nomina http://www.sat.gob.mx/sitio_internet/cfd/nomina/nomina11.xsd");
                child_1.setAttribute("Version", "1.1" );
                
                String registro_patronal = String.valueOf(dataNomina.get("registro_patronal"));
                if(!registro_patronal.equals("") && registro_patronal!=null){ child_1.setAttribute("RegistroPatronal", registro_patronal ); }
                child_1.setAttribute("NumEmpleado", String.valueOf(dataNomina.get("numero_empleado")));
                child_1.setAttribute("CURP", String.valueOf(dataNomina.get("curp")) );
                child_1.setAttribute("TipoRegimen", String.valueOf(dataNomina.get("tipo_regimen")) );

                String nss = String.valueOf(dataNomina.get("no_seguridad_social"));
                if(!nss.equals("") && !nss.equals("null") && nss!=null){ child_1.setAttribute("NumSeguridadSocial", nss ); }

                child_1.setAttribute("FechaPago", String.valueOf(dataNomina.get("fecha_pago")) );
                child_1.setAttribute("FechaInicialPago", String.valueOf(dataNomina.get("fecha_inicial_pago")) );
                child_1.setAttribute("FechaFinalPago", String.valueOf(dataNomina.get("fecha_final_pago")) );
                child_1.setAttribute("NumDiasPagados", String.valueOf(dataNomina.get("no_dias_pagados")) );

                String departamento = String.valueOf(dataNomina.get("departamento"));
                if(!departamento.equals("") && !departamento.equals("null") && departamento!=null){ child_1.setAttribute("Departamento", departamento ); }

                String clabe = String.valueOf(dataNomina.get("clabe"));
                if(!clabe.equals("") && !clabe.equals("null") && clabe!=null){ child_1.setAttribute("CLABE", clabe ); }

                String banco = String.valueOf(dataNomina.get("banco"));
                if(!banco.equals("") && !banco.equals("null") && banco!=null){ child_1.setAttribute("Banco", banco ); }

                String fecha_contrato = String.valueOf(dataNomina.get("fecha_contrato"));
                if(!fecha_contrato.equals("") && !fecha_contrato.equals("null") && fecha_contrato!=null){ child_1.setAttribute("FechaInicioRelLaboral", fecha_contrato ); }

                String antiguedad = String.valueOf(dataNomina.get("antiguedad"));
                if(!antiguedad.equals("") && !antiguedad.equals("null") && antiguedad!=null){ child_1.setAttribute("Antiguedad", antiguedad ); }

                String puesto = String.valueOf(dataNomina.get("puesto"));
                if(!puesto.equals("") && !puesto.equals("null") && puesto!=null){ child_1.setAttribute("Puesto", puesto ); }

                String tipo_contrato = String.valueOf(dataNomina.get("tipo_contrato"));
                if(!tipo_contrato.equals("") && !tipo_contrato.equals("null") && tipo_contrato!=null){ child_1.setAttribute("TipoContrato", tipo_contrato ); }

                String tipo_jornada = String.valueOf(dataNomina.get("tipo_jornada"));
                if(!tipo_jornada.equals("") && !tipo_jornada.equals("null") && tipo_jornada!=null){ child_1.setAttribute("TipoJornada", tipo_jornada ); }

                child_1.setAttribute("PeriodicidadPago", String.valueOf(dataNomina.get("PeriodicidadPago")) );

                String salario_base = String.valueOf(dataNomina.get("salario_base"));
                if(!salario_base.equals("") && !salario_base.equals("null") && salario_base!=null){ child_1.setAttribute("SalarioBaseCotApor", salario_base ); }

                String riesgo_puesto = String.valueOf(dataNomina.get("riesgo_puesto"));
                if(!riesgo_puesto.equals("") && !riesgo_puesto.equals("null") && riesgo_puesto!=null){ child_1.setAttribute("RiesgoPuesto", riesgo_puesto ); }

                String salario_integrado = String.valueOf(dataNomina.get("salario_integrado"));
                if(!salario_integrado.equals("") && !salario_integrado.equals("null") && salario_integrado!=null){ child_1.setAttribute("SalarioDiarioIntegrado", salario_integrado ); }


                //PERCEPCIONES
                ArrayList<LinkedHashMap<String,String>> lista_percepciones = new ArrayList<LinkedHashMap<String,String>>();
                lista_percepciones = (ArrayList<LinkedHashMap<String,String>>) dataNomina.get("percepciones");

                if(lista_percepciones.size()>0){
                    Element child_1_1 = tmp.createElement("nomina:Percepciones");
                    child_1_1.setAttribute("TotalGravado", String.valueOf(dataNomina.get("percep_total_gravado")) );
                    child_1_1.setAttribute("TotalExento", String.valueOf(dataNomina.get("percep_total_excento")) );

                    for( LinkedHashMap<String,String> i : lista_percepciones ){
                        Element child_1_1_1 = tmp.createElement("nomina:Percepcion");
                        @SuppressWarnings("rawtypes")
                        Iterator it = i.entrySet().iterator();
                        while (it.hasNext()) {
                            @SuppressWarnings("rawtypes")
                            Map.Entry elemento_hash = (Map.Entry)it.next();
                            String llave = (String)elemento_hash.getKey();
                            String valor = (String)elemento_hash.getValue();
                            if (llave.equals("TipoPercepcion")){ child_1_1_1.setAttribute(llave, valor);  }
                            if (llave.equals("Clave")){ child_1_1_1.setAttribute(llave, valor); }
                            if (llave.equals("Concepto")){ child_1_1_1.setAttribute(llave, valor); }
                            if (llave.equals("ImporteGravado")){ child_1_1_1.setAttribute(llave, valor); }
                            if (llave.equals("ImporteExento")){ child_1_1_1.setAttribute(llave, valor); }
                        }
                        child_1_1.appendChild(child_1_1_1);
                    }
                    child_1.appendChild(child_1_1);
                }



                //DEDUCCIONES
                ArrayList<LinkedHashMap<String,String>> lista_deducciones = new ArrayList<LinkedHashMap<String,String>>();
                lista_deducciones = (ArrayList<LinkedHashMap<String,String>>) dataNomina.get("deducciones");

                if(lista_deducciones.size()>0){
                    Element child_1_2 = tmp.createElement("nomina:Deducciones");
                    child_1_2.setAttribute("TotalGravado", String.valueOf(dataNomina.get("deduc_total_gravado")) );
                    child_1_2.setAttribute("TotalExento", String.valueOf(dataNomina.get("deduc_total_excento")) );

                    for( LinkedHashMap<String,String> i : lista_deducciones ){
                        Element child_1_2_1 = tmp.createElement("nomina:Deduccion");
                        @SuppressWarnings("rawtypes")
                        Iterator it = i.entrySet().iterator();
                        while (it.hasNext()) {
                            @SuppressWarnings("rawtypes")
                            Map.Entry elemento_hash = (Map.Entry)it.next();
                            String llave = (String)elemento_hash.getKey();
                            String valor = (String)elemento_hash.getValue();
                            if (llave.equals("TipoDeduccion")){ child_1_2_1.setAttribute(llave, valor);  }
                            if (llave.equals("Clave")){ child_1_2_1.setAttribute(llave, valor); }
                            if (llave.equals("Concepto")){ child_1_2_1.setAttribute(llave, valor); }
                            if (llave.equals("ImporteGravado")){ child_1_2_1.setAttribute(llave, valor); }
                            if (llave.equals("ImporteExento")){ child_1_2_1.setAttribute(llave, valor); }
                        }
                        child_1_2.appendChild(child_1_2_1);
                    }
                    child_1.appendChild(child_1_2);
                }




                //INCAPACIDADES
                ArrayList<LinkedHashMap<String,String>> lista_incapacidades = new ArrayList<LinkedHashMap<String,String>>();
                lista_incapacidades = (ArrayList<LinkedHashMap<String,String>>) dataNomina.get("incapacidades");

                if(lista_incapacidades.size()>0){
                    Element child_1_3 = tmp.createElement("nomina:Incapacidades");
                    for( LinkedHashMap<String,String> i : lista_incapacidades ){
                        Element child_1_3_1 = tmp.createElement("nomina:Incapacidad");
                        @SuppressWarnings("rawtypes")
                        Iterator it = i.entrySet().iterator();
                        while (it.hasNext()) {
                            @SuppressWarnings("rawtypes")
                            Map.Entry elemento_hash = (Map.Entry)it.next();
                            String llave = (String)elemento_hash.getKey();
                            String valor = (String)elemento_hash.getValue();
                            if (llave.equals("DiasIncapacidad")){ child_1_3_1.setAttribute(llave, valor);  }
                            if (llave.equals("TipoIncapacidad")){ child_1_3_1.setAttribute(llave, valor); }
                            if (llave.equals("Descuento")){ child_1_3_1.setAttribute(llave, valor); }
                        }
                        child_1_3.appendChild(child_1_3_1);
                    }
                    child_1.appendChild(child_1_3);
                }




                //HORAS EXTRAS
                ArrayList<LinkedHashMap<String,String>> lista_hrs_extras = new ArrayList<LinkedHashMap<String,String>>();
                lista_hrs_extras = (ArrayList<LinkedHashMap<String,String>>) dataNomina.get("HorasExtras");

                if(lista_hrs_extras.size()>0){
                    Element child_1_4 = tmp.createElement("nomina:HorasExtras");
                    for( LinkedHashMap<String,String> i : lista_hrs_extras ){
                        Element child_1_4_1 = tmp.createElement("nomina:HorasExtra");
                        @SuppressWarnings("rawtypes")
                        Iterator it = i.entrySet().iterator();
                        while (it.hasNext()) {
                            @SuppressWarnings("rawtypes")
                            Map.Entry elemento_hash = (Map.Entry)it.next();
                            String llave = (String)elemento_hash.getKey();
                            String valor = (String)elemento_hash.getValue();
                            if (llave.equals("Dias")){ child_1_4_1.setAttribute(llave, valor);  }
                            if (llave.equals("TipoHoras")){ child_1_4_1.setAttribute(llave, valor); }
                            if (llave.equals("HorasExtra")){ child_1_4_1.setAttribute(llave, valor); }
                            if (llave.equals("ImportePagado")){ child_1_4_1.setAttribute(llave, valor); }
                        }
                        child_1_4.appendChild(child_1_4_1);
                    }
                    child_1.appendChild(child_1_4);
                }
                
                root.appendChild(child_1);
            }
            //Termina complemando Nomina
            
            tmp.getDocumentElement().appendChild(root);
            this.setDoc(tmp);
	}

        
        
        
        
	public DocumentBuilder getDb() {
		return db;
	}
        
	public void setDb(DocumentBuilder db) {
		this.db = db;
	}
        
	public DocumentBuilderFactory getDbf() {
		return dbf;
	}
        
	public void setDbf(DocumentBuilderFactory dbf) {
		this.dbf = dbf;
	}
        
	public Document getDoc() {
		return doc;
	}
        
	public void setDoc(Document doc) {
		this.doc = doc;
	}
        
	public DOMImplementation getDomImpl() {
		return domImpl;
	}
        
	public void setDomImpl(DOMImplementation domImpl) {
		this.domImpl = domImpl;
	}
        
        public String getOutXmlString() throws TransformerConfigurationException, TransformerException{
            Transformer transformer = TransformerFactory.newInstance().newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            
            //initialize StreamResult with File object to save to file
            StreamResult result = new StreamResult(new StringWriter());
            DOMSource source = new DOMSource(this.getDoc());
            transformer.transform(source, result);
            
            return result.getWriter().toString().replace("standalone=\"no\"", " ");
        }
}
