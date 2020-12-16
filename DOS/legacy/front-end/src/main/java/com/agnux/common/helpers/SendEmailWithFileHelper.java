package com.agnux.common.helpers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Properties;
import javax.activation.DataHandler;
import javax.activation.FileDataSource;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;


public class SendEmailWithFileHelper {
    private String ipServidor;
    private String nombreUsuario;
    private String contrasenia;
    private String mensaje;
    private ArrayList<LinkedHashMap<String,String>> adjuntos;
    private ArrayList<LinkedHashMap<String,String>> destinatarios;
    private String destinatario;
    private String asunto;
    private String puerto;

    public String getPuerto() {
        return puerto;
    }

    public void setPuerto(String puerto) {
        this.puerto = puerto;
    }

    public String getAsunto() {
        return asunto;
    }

    public void setAsunto(String asunto) {
        this.asunto = asunto;
    }

    public String getContrasenia() {
        return contrasenia;
    }

    public void setContrasenia(String contrasenia) {
        this.contrasenia = contrasenia;
    }

    public String getDestinatario() {
        return destinatario;
    }

    public void setDestinatario(String destinatario) {
        this.destinatario = destinatario;
    }

    public String getIpServidor() {
        return ipServidor;
    }

    public void setIpServidor(String ipServidor) {
        this.ipServidor = ipServidor;
    }

    public String getMensaje() {
        return mensaje;
    }

    public void setMensaje(String mensaje) {
        this.mensaje = mensaje;
    }

    public String getNombreUsuario() {
        return nombreUsuario;
    }

    public void setNombreUsuario(String nombreUsuario) {
        this.nombreUsuario = nombreUsuario;
    }
    
    
    public ArrayList<LinkedHashMap<String, String>> getAdjuntos() {
        return adjuntos;
    }

    public void setAdjuntos(ArrayList<LinkedHashMap<String, String>> adjuntos) {
        this.adjuntos = adjuntos;
    }

    public ArrayList<LinkedHashMap<String, String>> getDestinatarios() {
        return destinatarios;
    }

    public void setDestinatarios(ArrayList<LinkedHashMap<String, String>> destinatarios) {
        this.destinatarios = destinatarios;
    }
    
    
    
    
    public SendEmailWithFileHelper(HashMap<String, String> conecta){
        this.setIpServidor(conecta.get("hostname"));
        this.setContrasenia(conecta.get("password"));
        this.setNombreUsuario(conecta.get("username"));
    }
    
    
    public String enviarEmail() {
        String retorno="";
        try {
            retorno="Correo enviado!";
            //ESTABLECER LA SESION JAVAMAIL
            Properties props = System.getProperties();
            
            //props.clear();
            
            // Nombre del host de correo, es smtp.gmail.com
            props.setProperty("mail.smtp.host", this.getIpServidor());
            
            // Puerto para envio de correos
            props.setProperty("mail.smtp.port",this.getPuerto());
            
            // Si requiere o no usuario y password para conectarse.
            props.setProperty("mail.smtp.auth", "true");
            
            // TLS si esta disponible
            //props.setProperty("mail.smtp.starttls.enable", "true");
            
            //props.setProperty("mail.smtp.ssl.trust", this.getIpServidor());
            
            if(this.getIpServidor().toLowerCase().equals("smtp.gmail.com")){
                props.setProperty("mail.smtp.starttls.enable", "true");
            }else{
                props.put("mail.smtp.ssl.enable", false);
                props.put("mail.smtp.starttls.enable", false);
            }
            

            
            // Nombre del usuario
            props.setProperty("mail.smtp.user", this.getNombreUsuario());



            Session session = Session.getDefaultInstance(props);
            
            // Para obtener un log de salida mas extenso
            //session.setDebug(true);
            
            //CONSTRUIR EL MENSAJE
            MimeMessage message = new MimeMessage(session);
            
            // Se rellena el From
            message.setFrom(new InternetAddress(this.getNombreUsuario()));
            
            // Se rellena el subject
            message.setSubject(this.getAsunto());
            
            
            
            
            //CONSTRUIR UN MENSAJE COMPLEJO CON ADJUNTOS
            BodyPart texto = new MimeBodyPart();

            // Texto del mensaje
            //texto.setContent(this.getMensaje(), "text/html");
            texto.setText(this.getMensaje());

            
            //JUNTAMOS AMBAS PARTES EN UNA SOLA
            MimeMultipart multiParte = new MimeMultipart();
            multiParte.addBodyPart(texto);
            
            
            if(this.getAdjuntos().size()>0){
                //Adjuntar archivos
                for( LinkedHashMap<String,String> i : this.getAdjuntos() ){
                    BodyPart adjunto = new MimeBodyPart();
                    
                    //Pasamos la ruta del archivo a adjuntar
                    adjunto.setDataHandler(new DataHandler(new FileDataSource(i.get("path_file"))));
                    
                    // Opcional. De esta forma transmitimos al receptor el nombre original del fichero
                    adjunto.setFileName(i.get("file_name"));
                    //adjunto.setFileName(adjunto.getFileName());
                    
                    multiParte.addBodyPart(adjunto);
                }
            }
            
            
            
            if(this.getDestinatarios().size()>0){
                //Se cargan los destinatarios
                for( LinkedHashMap<String,String> i : this.getDestinatarios() ){
                    if(i.get("type").toUpperCase().trim().equals("TO")){
                        message.addRecipient(Message.RecipientType.TO, new InternetAddress(i.get("recipient")));
                    }
                    if(i.get("type").toUpperCase().trim().equals("CC")){
                        message.addRecipient(Message.RecipientType.CC, new InternetAddress(i.get("recipient")));
                    }
                    //Copia oculta
                    if(i.get("type").toUpperCase().trim().equals("BCC")){
                        message.addRecipient(Message.RecipientType.BCC, new InternetAddress(i.get("recipient")));
                    }
                }
            }

            


            // Se mete el texto y la foto adjunta.
            message.setContent(multiParte);
            
            //ENVIAR EL MENSAJE
            Transport t = session.getTransport("smtp");
            
            // Aqui usuario y password
            t.connect(this.getNombreUsuario(), this.getContrasenia());
            t.sendMessage(message,message.getAllRecipients());
            t.close();
            
        }catch (Exception ex) {
            ex.printStackTrace();
            retorno="No fue posible eviar el correo. ["+ ex.getMessage() +"]" ;
        }
        
        return retorno;
    }



}
