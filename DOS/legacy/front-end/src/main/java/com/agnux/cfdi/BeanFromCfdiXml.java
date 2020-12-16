/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.agnux.cfdi;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 *
 * @author agnux
 */
public class BeanFromCfdiXml {
    private String uuid;
    private String selloSat;
    private String selloCfd;
    private String emisor_rfc;
    private String receptor_rfc;
    private String fecha_comprobante;
    private String fecha_timbre;
    private String noCertificadoSAT;
    
    public BeanFromCfdiXml(String fichero) {
        String comprobante = new String();

        File file = new File(fichero);

        try { BufferedReader in = new BufferedReader(new FileReader(file));
            String str;
            while ((str = in.readLine()) != null) {
                comprobante += str;
            } in.close();
        }
        catch (IOException e) {
            e.printStackTrace();
        }

        try {
            InputStream is = new ByteArrayInputStream(comprobante.getBytes("UTF-8"));
            leerxml(is);
            //this.setCadenaOriginal(extraerCadenaOriginal(is));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }
    
    
    private void leerxml(InputStream url){
        try{
            SAXParserFactory spf = SAXParserFactory.newInstance();
            SAXParser sp = spf.newSAXParser();
            sp.parse(url, new ParseaArchivoXmlOfCFDI() );
        }
        catch(ParserConfigurationException e){
            System.err.println("error de  parseo " + e);
        }
        catch(SAXException e2){
            System.err.println(e2);
            System.err.println("error de  sax: " + e2.getStackTrace());
        }
        catch (IOException e3) {
            System.err.println("error de  io: " + e3.getMessage() );
        }
    }
    
    
    
    private class ParseaArchivoXmlOfCFDI extends DefaultHandler {
        public void startElement(String uri, String localName, String qName, Attributes atts) throws SAXException {
            if(qName.equals("tfd:TimbreFiscalDigital")){
                for(int i=0; i < atts.getLength();i++){
                    String valor=atts.getQName(i);
                    if (valor.equals("UUID")){
                        setUuid(atts.getValue(i));
                    }
                    if (valor.equals("selloSAT")){
                        setSelloSat(atts.getValue(i));
                    }
                    if (valor.equals("selloCFD")){
                        setSelloCfd(atts.getValue(i));
                    }
                    if (valor.equals("noCertificadoSAT")){
                        setNoCertificadoSAT(atts.getValue(i));
                    }
                    if (valor.equals("FechaTimbrado")){
                        setFecha_timbre(atts.getValue(i));
                    }
                }
            }
            
            
            if(qName.equals("cfdi:Emisor")){
                for(int i=0; i < atts.getLength();i++){
                    String valor=atts.getQName(i);
                    if (valor.equals("rfc")){
                        setEmisor_rfc(atts.getValue(i));
                    }
                }
            }
            
            
            if(qName.equals("cfdi:Receptor")){
                for(int i=0; i < atts.getLength();i++){
                    String valor=atts.getQName(i);
                    if (valor.equals("rfc")){
                        setReceptor_rfc(atts.getValue(i));
                    }
                }
            }
            
            if ("cfdi:Comprobante".equals(qName)) {
                for(int i=0; i < atts.getLength(); i++){
                    String valor=atts.getQName(i);
                    if (valor.equals("fecha")){
                        setFecha_comprobante(atts.getValue(i));
                    }
                }
            }
            
        }
        
    }
    
    
    
    
    public String getEmisor_rfc() {
        return emisor_rfc;
    }

    public void setEmisor_rfc(String emisor_rfc) {
        this.emisor_rfc = emisor_rfc;
    }

    public String getReceptor_rfc() {
        return receptor_rfc;
    }

    public void setReceptor_rfc(String receptor_rfc) {
        this.receptor_rfc = receptor_rfc;
    }

    
    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }
    
    public String getSelloSat() {
        return selloSat;
    }

    public void setSelloSat(String selloSat) {
        this.selloSat = selloSat;
    }

    public String getSelloCfd() {
        return selloCfd;
    }

    public void setSelloCfd(String selloCfd) {
        this.selloCfd = selloCfd;
    }
    public String getFecha_comprobante() {
        return fecha_comprobante;
    }

    public void setFecha_comprobante(String fecha_comprobante) {
        this.fecha_comprobante = fecha_comprobante;
    }
    
    public String getFecha_timbre() {
        return fecha_timbre;
    }

    public void setFecha_timbre(String fecha_timbre) {
        this.fecha_timbre = fecha_timbre;
    }

    public String getNoCertificadoSAT() {
        return noCertificadoSAT;
    }

    public void setNoCertificadoSAT(String noCertificadoSAT) {
        this.noCertificadoSAT = noCertificadoSAT;
    }
}
