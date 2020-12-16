package com.agnux.cfdi;

import com.agnux.common.helpers.FileHelper;
import com.agnux.common.helpers.StringHelper;
import com.agnux.kemikal.interfacedaos.GralInterfaceDao;
import java.util.HashMap;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

public class BeanCancelaCfdi {
    @Autowired
    @Qualifier("daoGral")
    private GralInterfaceDao gralDao;
    
    
    public static enum Proposito {
        FACTURA, NOTA_CREDITO
    };
    
    private String uuid;
    private String emRfc;
    private String reRfc;
    private String serieFolio;
    
    
    public void init(HashMap<String, String> data, String serie_folio) {
        this.setUuid(data.get("uuid"));
        this.setEmRfc(StringHelper.normalizaString(StringHelper.remueve_tildes(data.get("emisor_rfc")).replace("'", "")));
        this.setReRfc(StringHelper.normalizaString(StringHelper.remueve_tildes(data.get("receptor_rfc")).replace("'", "")));
        this.setSerieFolio(serie_folio);
    }
    
    public void start() {
        String strOutput4File = new String();
        
        StringMakerForCfdiRequest sm = new StringMakerForCfdiRequest();
        strOutput4File += sm.createHeader();
        strOutput4File += sm.createDatos(this.getUuid(), this.getEmRfc(),this.getReRfc());
        
        String txt_file_name = new String();
        
        //genera el nombre del fichero
        txt_file_name += "cancela_";
        txt_file_name += this.getSerieFolio();
        txt_file_name += ".txt";
        
        
        //obtiene el directorio de solicitudes cfi de la empresa
        String directorioSolicitudesCfdi=this.getGralDao().getCfdiSolicitudesDir() + "in/";
        
        boolean fichero_txt_ok = FileHelper.createFileWithText(directorioSolicitudesCfdi , txt_file_name, strOutput4File);
        
        if (fichero_txt_ok) {
            System.out.println("Se ha generado el archivo:"+ directorioSolicitudesCfdi + txt_file_name);
        }else{
            System.out.println("NO se ha PODIDO GENERAR el archivo:"+ directorioSolicitudesCfdi + txt_file_name);
        }
        
    }
    
    private class StringMakerForCfdiRequest{
        
        public String createHeader(){
            
            return  "cancela\n" +
                    "################" +
                    "################" + 
                    "################" + 
                    "################" +
                    "######"+ 
                    "\n";
        }
    
        
        public String createDatos(String uuid,String emisor_rfc, String receptor_rfc){
            String cadena_retorno = new String();
            
            cadena_retorno += "#Requerido\n";
            cadena_retorno += "uuid|" + uuid + "\n";
            cadena_retorno += "#Requerido\n";
            cadena_retorno += "emRfc|" + emisor_rfc + "\n";
            cadena_retorno += "#Requerido\n";
            cadena_retorno += "reRfc|" + receptor_rfc + "\n";
            
            return cadena_retorno;
        
        }
        
        
    }
    
    public String getEmRfc() {
        return emRfc;
    }

    public void setEmRfc(String emRfc) {
        this.emRfc = emRfc;
    }

    public GralInterfaceDao getGralDao() {
        return gralDao;
    }

    public void setGralDao(GralInterfaceDao gralDao) {
        this.gralDao = gralDao;
    }

    public String getReRfc() {
        return reRfc;
    }

    public void setReRfc(String reRfc) {
        this.reRfc = reRfc;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }
    

    public String getSerieFolio() {
        return serieFolio;
    }

    public void setSerieFolio(String serieFolio) {
        this.serieFolio = serieFolio;
    }
    
}
