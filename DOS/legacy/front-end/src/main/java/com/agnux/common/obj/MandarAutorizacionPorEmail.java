/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.agnux.common.obj;

import com.agnux.common.helpers.SendEmailWithFileHelper;
import com.agnux.kemikal.reportes.PdfReporteComOrdenDeCompra;
import com.agnux.kemikal.reportes.PdfReporteComOrdenDeCompraFormatoDos;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Ezcure
 */
public final class MandarAutorizacionPorEmail extends Thread{
    private HashMap<String, String> datosEncabezadoPie;
    private HashMap<String, String> datosOrdenCompra;
    private ArrayList<HashMap<String, String>> conceptosOrdenCompra;
    private String razon_social_empresa;
    private String fileout;
    private String ruta_imagen;
    private HashMap<String, String> conecta;
    private String mensaje;

    public String getMensaje() {
        return mensaje;
    }

    public void setMensaje(String mensaje) {
        this.mensaje = mensaje;
    }

    public HashMap<String, String> getConecta() {
        return conecta;
    }

    public void setConecta(HashMap<String, String> conecta) {
        this.conecta = conecta;
    }
    
    
    @Override
    public void run() {
	try {
            
            HashMap<String, String> datosEncabezadoPie = new HashMap<String, String>();
            HashMap<String, String> datosOrdenCompra= new HashMap<String, String>();
            ArrayList<HashMap<String, String>> conceptosOrdenCompra = new ArrayList<HashMap<String, String>>();
            String razon_social_empresa ="";
            String fileout = "";
            String ruta_imagen = "";
            datosEncabezadoPie=this.getDatosEncabezadoPie();
            datosOrdenCompra = this.getDatosOrdenCompra();
            conceptosOrdenCompra = this.getConceptosOrdenCompra();
            razon_social_empresa = this.getRazon_social_empresa();
            fileout = this.getFileout();
            ruta_imagen = this.getRuta_imagen();          
            
            
            if (datosOrdenCompra.get("formato_oc").equals("1")){
                //Instancia a la clase que construye el pdf formato1 de la Orden de Compra
                PdfReporteComOrdenDeCompra x1 = new PdfReporteComOrdenDeCompra(datosEncabezadoPie,datosOrdenCompra,conceptosOrdenCompra,razon_social_empresa,fileout,ruta_imagen);
            }else{
                if (datosOrdenCompra.get("formato_oc").equals("2")){
                    //Instancia a la clase que construye el pdf formato2 de la Orden de Compra
                    PdfReporteComOrdenDeCompraFormatoDos x2 = new PdfReporteComOrdenDeCompraFormatoDos(datosEncabezadoPie,datosOrdenCompra,conceptosOrdenCompra,razon_social_empresa,fileout,ruta_imagen);
                }
            }
            
            PdfReporteComOrdenDeCompra u = new PdfReporteComOrdenDeCompra(datosEncabezadoPie,datosOrdenCompra,conceptosOrdenCompra,razon_social_empresa, fileout,ruta_imagen);
            String email_compras =  datosEncabezadoPie.get("email_compras");
            String password =  datosEncabezadoPie.get("pass_email_compras");
            
            String[] correo2 = email_compras.split("@");
            String hostname = correo2[1];
            String username = correo2[0];
            
            HashMap<String, String> conecta2 = new HashMap<String, String>();
            conecta2.put("hostname",hostname);
            conecta2.put("password",password);
            conecta2.put("username",username);
            setConecta(conecta2);
            System.out.println("Enviando correo electrónico");
            System.out.println("Fileout: "+fileout);
            System.out.println("ruta_imagen: "+ruta_imagen);
            //String correo_prov = "";
            String correo_prov = datosOrdenCompra.get("correo_prov");
            System.out.println("Este es el corre del prov. "+correo_prov);
            
            if (!"0".equals(correo_prov)){
                if ( this.getConecta() != null ) {
                    SendEmailWithFileHelper z = new SendEmailWithFileHelper(conecta2);
                    z.setPuerto("10025");
                    z.setMensaje("Este es un mensaje de autorización de Orden de compra por parte de: "+razon_social_empresa );
                    z.setNombreUsuario(email_compras);
                    //z.setNombreArchivoAdjunto("Orden Compra # "+datosOrdenCompra.get("folio")+".pdf");
                    z.setDestinatario(correo_prov);
                    z.setAsunto("Orden Compra # "+datosOrdenCompra.get("folio") );
                    //z.setArchivoAdjunto(fileout);
                    z.enviarEmail();
                }else {
                    throw new Exception("No se ha enviado ningún e-mail!");
                }
            }else {
                throw new Exception("No se ha enviado ningún e-mail 2!");
            }   
        } catch (Exception ex) {
            Logger.getLogger(MandarAutorizacionPorEmail.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void setConfig(HashMap<String, String> conecta){
        this.setConfig(conecta);
    }
    
    public MandarAutorizacionPorEmail(HashMap<String, String> datosEncabezadoPie, HashMap<String, String> datosOrdenCompra, ArrayList<HashMap<String, String>> conceptosOrdenCompra, String razon_social_empresa, String fileout, String ruta_imagen) {
       this.setDatosEncabezadoPie(datosEncabezadoPie);
       this.setDatosOrdenCompra(datosOrdenCompra);
       this.setConceptosOrdenCompra(conceptosOrdenCompra);
       this.setRazon_social_empresa(razon_social_empresa);
       this.setFileout(fileout);
       this.setRuta_imagen(ruta_imagen);
    }
    
        public ArrayList<HashMap<String, String>> getConceptosOrdenCompra() {
        return conceptosOrdenCompra;
    }

    public void setConceptosOrdenCompra(ArrayList<HashMap<String, String>> conceptosOrdenCompra) {
        this.conceptosOrdenCompra = conceptosOrdenCompra;
    }

    public HashMap<String, String> getDatosEncabezadoPie() {
        return datosEncabezadoPie;
    }

    public void setDatosEncabezadoPie(HashMap<String, String> datosEncabezadoPie) {
        this.datosEncabezadoPie = datosEncabezadoPie;
    }

    public HashMap<String, String> getDatosOrdenCompra() {
        return datosOrdenCompra;
    }

    public void setDatosOrdenCompra(HashMap<String, String> datosOrdenCompra) {
        this.datosOrdenCompra = datosOrdenCompra;
    }

    public String getFileout() {
        return fileout;
    }

    public void setFileout(String fileout) {
        this.fileout = fileout;
    }

    public String getRazon_social_empresa() {
        return razon_social_empresa;
    }

    public void setRazon_social_empresa(String razon_social_empresa) {
        this.razon_social_empresa = razon_social_empresa;
    }

    public String getRuta_imagen() {
        return ruta_imagen;
    }

    public void setRuta_imagen(String ruta_imagen) {
        this.ruta_imagen = ruta_imagen;
    }
    
}
