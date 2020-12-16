/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.agnux.common.helpers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;

/**
 *
 * @author agnux
 */
public class MandaEmail extends Thread{
    private HashMap<String, String> conecta = new HashMap<String, String>();
    private ArrayList<LinkedHashMap<String,String>> arrayAdjuntos = new ArrayList<LinkedHashMap<String,String>>();
    private ArrayList<LinkedHashMap<String,String>> arrayDestinatarios = new ArrayList<LinkedHashMap<String,String>>();
    private String port;
    private String asunto;
    private String mesaje;
    
    public MandaEmail(HashMap<String, String> conecta, ArrayList<LinkedHashMap<String,String>> destinatarios, ArrayList<LinkedHashMap<String,String>> adjuntos, String Port, String Asunto, String msj) {
        this.setConecta(conecta);
        this.setArrayAdjuntos(adjuntos);
        this.setArrayDestinatarios(destinatarios);
        this.setPort(Port);
        this.setAsunto(Asunto);
        this.setMesaje(msj);
    }
    
    
    @Override
    public void run() {
	try {
            SendEmailWithFileHelper send = new SendEmailWithFileHelper(this.getConecta());
            send.setPuerto(this.getPort());
            send.setMensaje(this.getMesaje());
            send.setAdjuntos(this.getArrayAdjuntos());
            send.setDestinatarios(this.getArrayDestinatarios());
            send.setAsunto(this.getAsunto());
            
            //respuesta = send.enviarEmail();
            //valor="true";
            //respuesta="Correo enviado!";
            
            System.out.println(send.enviarEmail());
        } catch (Exception ex) {
            //Logger.getLogger(MandaEmail.class.getName()).log(Level.SEVERE, null, ex);
            System.out.println(ex.getMessage());
        }
    }
    
    public HashMap<String, String> getConecta() {
        return conecta;
    }

    public void setConecta(HashMap<String, String> conecta) {
        this.conecta = conecta;
    }
    
    public ArrayList<LinkedHashMap<String, String>> getArrayAdjuntos() {
        return arrayAdjuntos;
    }

    public void setArrayAdjuntos(ArrayList<LinkedHashMap<String, String>> arrayAdjuntos) {
        this.arrayAdjuntos = arrayAdjuntos;
    }

    public ArrayList<LinkedHashMap<String, String>> getArrayDestinatarios() {
        return arrayDestinatarios;
    }

    public void setArrayDestinatarios(ArrayList<LinkedHashMap<String, String>> arrayDestinatarios) {
        this.arrayDestinatarios = arrayDestinatarios;
    }
    
    public String getAsunto() {
        return asunto;
    }

    public void setAsunto(String asunto) {
        this.asunto = asunto;
    }

    public String getMesaje() {
        return mesaje;
    }

    public void setMesaje(String mesaje) {
        this.mesaje = mesaje;
    }

    public String getPort() {
        return port;
    }

    public void setPort(String port) {
        this.port = port;
    }
}
