package com.agnux.contae;


import com.agnux.cfd.v2.CryptoEngine;
import com.agnux.common.helpers.FileHelper;
import com.agnux.common.helpers.XmlHelper;
import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;


public class BeanConstruyeSellaXml {

    private ByteArrayOutputStream baos = null;
    private boolean success = false;
    private String mensaje = null;

    public String getMensaje() {
        return mensaje;
    }

    public final void setMensaje(String mensaje) {
        this.mensaje = mensaje;
    }

    public boolean isSuccess() {
        return success;
    }

    public final void setSuccess(boolean success) {
        this.success = success;
    }
    
    public ByteArrayOutputStream getBaos() {
        return baos;
    }
    
    public final void setBaos(ByteArrayOutputStream baos) {
        this.baos = baos;
    }
    
    public BeanConstruyeSellaXml(String tipo, LinkedHashMap<String,String> datos, ArrayList<LinkedHashMap<String,String>> cuentas, String rutaFicheroCertificado, String rutaFicheroXsl, String rutaFicheroLlave, String passwordLlavePrivada, String rutaFicheroXml, String rutaFicheroXsd) {
        this.setBaos(new ByteArrayOutputStream());
        this.setSuccess(false);
        this.setMensaje("");
        
        try {
            String cadenaXml = new String();
            
            //Obtener el certificado en cadena base64
            String certificadoBase64 = CryptoEngine.encodeCertToBase64(rutaFicheroCertificado);
            
            datos.put("certificado", certificadoBase64);
            datos.put("sello", "@SELLO_DIGITAL");
            
            
            
            if(tipo.equals("CATALOGO")){
                //Construir xml
                CatalogoCuentasXmlBuilder xml = new CatalogoCuentasXmlBuilder(datos, cuentas);

                cadenaXml = xml.getBaos().toString();
            }
            
            if(tipo.equals("BALANZA")){
                //Construir xml
                BalanzaComprobacionXmlBuilder xml = new BalanzaComprobacionXmlBuilder(datos, cuentas);

                cadenaXml = xml.getBaos().toString();
            }
            
            //Obtener la cadena original necesario para obtener el sello
            String cadenaOriginal = XmlHelper.transformar(cadenaXml, rutaFicheroXsl);
            
            //Obtener el sello
            String sello = CryptoEngine.sign(rutaFicheroLlave, passwordLlavePrivada, cadenaOriginal);
            
            cadenaXml = cadenaXml.replaceAll("@SELLO_DIGITAL", sello);
            
            boolean fichero_xml_ok = FileHelper.createFileWithText(rutaFicheroXml, cadenaXml);
            
            if(fichero_xml_ok){
 
                this.setSuccess(true);

            }else{
                this.setSuccess(false);
                this.setMensaje("No se ha podido crear el fichero xml");
            }

        } catch (Exception ex) {
            Logger.getLogger(BeanConstruyeSellaXml.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
}
