package com.agnux.contae;

import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import mx.gob.sat.esquemas.contabilidade._1_1.balanzacomprobacion.Balanza;


public class BalanzaComprobacionXmlBuilder {
    private ByteArrayOutputStream baos = new ByteArrayOutputStream();
    
    public ByteArrayOutputStream getBaos() {
        return baos;
    }
    
    public BalanzaComprobacionXmlBuilder(LinkedHashMap<String,String> datos, ArrayList<LinkedHashMap<String,String>> cuentas) {
        Balanza balanzaSATXML = null;
        Balanza.Ctas cta = null;
        BigDecimal bdSaldoIni = null;
        BigDecimal bdDebe = null;
        BigDecimal bdHaber = null;
        BigDecimal bdSaldoFin = null;
        
        balanzaSATXML = new Balanza();
        
        DatatypeFactory dtf = null;
        
        try {
            dtf = DatatypeFactory.newInstance();
        } catch (DatatypeConfigurationException e) {
            e.getMessage();
        }
        XMLGregorianCalendar xgc = dtf.newXMLGregorianCalendar();
        
        if(!datos.get("anio").trim().equals("")){
            balanzaSATXML.setAnio(Integer.parseInt(datos.get("anio")));
        }
        
        if(!datos.get("mes").trim().equals("")){
            balanzaSATXML.setMes(datos.get("mes"));
        }
        
        if(!datos.get("noCertificado").trim().equals("")){
            balanzaSATXML.setNoCertificado(datos.get("noCertificado"));
        }
        
        if(!datos.get("certificado").trim().equals("")){
            balanzaSATXML.setCertificado(datos.get("certificado"));
        }
        
        if(!datos.get("rfc").trim().equals("")){
            balanzaSATXML.setRFC(datos.get("rfc"));
        }
        
        if(!datos.get("sello").trim().equals("")){
            balanzaSATXML.setSello(datos.get("sello"));
        }
        
        if(!datos.get("version").trim().equals("")){
            balanzaSATXML.setVersion(datos.get("version"));
        }
        
        if(!datos.get("tipoEnvio").trim().equals("")){
            balanzaSATXML.setTipoEnvio(datos.get("tipoEnvio"));
        }
        
        if(!datos.get("fecha").trim().equals("")){
            String fecha[] = datos.get("fecha").split("-");
            
            xgc.setYear(Integer.parseInt(fecha[0]));
            xgc.setMonth(Integer.parseInt(fecha[1]));
            xgc.setDay(Integer.parseInt(fecha[2]));
            
            balanzaSATXML.setFechaModBal(xgc);
        }
        
        
        if(cuentas.size()>0){
            for( LinkedHashMap<String,String> i : cuentas ){
                Iterator it = i.entrySet().iterator();
                
                if(!i.get("numCta").equals("0000")){
                    cta = new Balanza.Ctas();
                    while (it.hasNext()) {
                        Map.Entry elemento_hash = (Map.Entry)it.next();
                        String llave = (String)elemento_hash.getKey();
                        String valor = (String)elemento_hash.getValue();

                        if (llave.equals("numCta")){ 
                            if(!valor.equals("") && valor!=null){ 
                                cta.setNumCta(valor);
                            } 
                        }
                        if (llave.equals("saldo_inicial")){ 
                            if(!valor.equals("") && valor!=null){ 
                                bdSaldoIni = new BigDecimal(valor);
                                cta.setSaldoIni(bdSaldoIni);
                            } 
                        }
                        if (llave.equals("debe")){ 
                            if(!valor.equals("") && valor!=null){
                                bdDebe = new BigDecimal(valor);
                                cta.setDebe(bdDebe);
                            } 
                        }
                        if (llave.equals("haber")){
                            if(!valor.trim().equals("") && valor!=null){
                                bdHaber = new BigDecimal(valor);
                                cta.setHaber(bdHaber);
                            } 
                        }
                        if (llave.equals("saldo_final")){ 
                            if(!valor.equals("") && valor!=null){ 
                                bdSaldoFin = new BigDecimal(valor);
                                cta.setSaldoFin(bdSaldoFin);
                            } 
                        }
                    }
                    
                    balanzaSATXML.getCtas().add(cta);
                }
            }
        }
        
        try {
            JAXBContext jaxbCtx = JAXBContext.newInstance(Balanza.class);
            Marshaller marshaller = jaxbCtx.createMarshaller();
            marshaller.setProperty(Marshaller.JAXB_SCHEMA_LOCATION,"http://www.sat.gob.mx/esquemas/ContabilidadE/1_1/BalanzaComprobacion/BalanzaComprobacion_1_1.xsd");
            marshaller.setProperty(Marshaller.JAXB_ENCODING, "UTF-8"); //NOI18N
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
            
            //marshaller.marshal(catalogoSATXML, System.out);
            marshaller.marshal(balanzaSATXML, baos);
            
            //System.out.println(baos.toString());
            
        } catch (javax.xml.bind.JAXBException ex) {
            // XXXTODO Handle exception
            java.util.logging.Logger.getLogger("global").log(java.util.logging.Level.SEVERE, null, ex); //NOI18N
        }
        
    }
    
}
