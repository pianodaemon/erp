package com.agnux.contae;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import mx.gob.sat.esquemas.contabilidade._1_1.catalogocuentas.Catalogo;


public class CatalogoCuentasXmlBuilder {
    private ByteArrayOutputStream baos = new ByteArrayOutputStream();
    
    public ByteArrayOutputStream getBaos() {
        return baos;
    }
    
    public CatalogoCuentasXmlBuilder(LinkedHashMap<String,String> datos, ArrayList<LinkedHashMap<String,String>> cuentas) {
        Catalogo catalogoSATXML = null;
        Catalogo.Ctas cta = null;
        
        catalogoSATXML = new Catalogo();
        
        if(!datos.get("anio").trim().equals("")){
            catalogoSATXML.setAnio(Integer.parseInt(datos.get("anio")));
        }
        
        if(!datos.get("mes").trim().equals("")){
            catalogoSATXML.setMes(datos.get("mes"));
        }
        
        if(!datos.get("noCertificado").trim().equals("")){
            catalogoSATXML.setNoCertificado(datos.get("noCertificado"));
        }
        
        if(!datos.get("certificado").trim().equals("")){
            catalogoSATXML.setCertificado(datos.get("certificado"));
        }
        
        if(!datos.get("rfc").trim().equals("")){
            catalogoSATXML.setRFC(datos.get("rfc"));
        }
        
        if(!datos.get("sello").trim().equals("")){
            catalogoSATXML.setSello(datos.get("sello"));
        }
        
        if(!datos.get("version").trim().equals("")){
            catalogoSATXML.setVersion(datos.get("version"));
        }
        
        if(cuentas.size()>0){
            for( LinkedHashMap<String,String> i : cuentas ){
                Iterator it = i.entrySet().iterator();
                cta = new Catalogo.Ctas();
                
                while (it.hasNext()) {
                    Map.Entry elemento_hash = (Map.Entry)it.next();
                    String llave = (String)elemento_hash.getKey();
                    String valor = (String)elemento_hash.getValue();
                    
                    if (llave.equals("codAgrup")){ 
                        if(!valor.equals("") && valor!=null){ 
                            cta.setCodAgrup(valor);
                        } 
                    }
                    if (llave.equals("numCta")){ 
                        if(!valor.equals("") && valor!=null){ 
                            cta.setNumCta(valor);
                        } 
                    }
                    if (llave.equals("desc")){ 
                        if(!valor.equals("") && valor!=null){ 
                            cta.setDesc(valor);
                        } 
                    }
                    if (llave.equals("natur")){ 
                        if(!valor.equals("") && valor!=null){ 
                            cta.setNatur(valor);
                        } 
                    }
                    if (llave.equals("nivel")){
                        if(!valor.trim().equals("") && valor!=null){ 
                            cta.setNivel(Integer.parseInt(valor));
                        } 
                    }
                    if (llave.equals("subCtaDe")){ 
                        if(!valor.equals("") && valor!=null){ 
                            cta.setSubCtaDe(valor);
                        } 
                    }
                }
                
                catalogoSATXML.getCtas().add(cta);
            }
        }
        
        try {
            JAXBContext jaxbCtx = JAXBContext.newInstance(Catalogo.class);
            Marshaller marshaller = jaxbCtx.createMarshaller();
            marshaller.setProperty(Marshaller.JAXB_SCHEMA_LOCATION,"http://www.sat.gob.mx/esquemas/ContabilidadE/1_1/CatalogoCuentas http://www.sat.gob.mx/esquemas/ContabilidadE/1_1/CatalogoCuentas/CatalogoCuentas_1_1.xsd");
            marshaller.setProperty(Marshaller.JAXB_ENCODING, "UTF-8"); //NOI18N
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
            
            //marshaller.marshal(catalogoSATXML, System.out);
            marshaller.marshal(catalogoSATXML, baos);
            
            //System.out.println(baos.toString());
            
        } catch (javax.xml.bind.JAXBException ex) {
            // XXXTODO Handle exception
            java.util.logging.Logger.getLogger("global").log(java.util.logging.Level.SEVERE, null, ex); //NOI18N
        }
    }
    
}
