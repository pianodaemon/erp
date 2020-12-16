/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.agnux.common.helpers;

import java.io.*;
import java.math.BigInteger;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.apache.commons.lang.StringUtils;
import org.w3c.dom.Document;

/**
 *
 * @author fmora
 */
public class StringHelper {
    
    public StringHelper(){
    }
    
    public static String roundDouble(double numero, int ceros_a_la_derecha) {
        String mascara = "#.";
        String formateado="";
        String num="";
        
        for(int i = 0;i < ceros_a_la_derecha;i++){
            mascara += "0";
        }
        NumberFormat formatter = new DecimalFormat(mascara);
        num = ""+formatter.format(numero);
        
        if(num.substring(0, 1).equals(".")){
            formateado ="0"+num;
        }else{
            formateado =""+num;
        }
        
        if(ceros_a_la_derecha==0){
            formateado = formateado.substring( 0, formateado.length()-1 );
        }
        
        return formateado;
    }
    
    public static String roundDouble(String valor, int ceros_a_la_derecha) {
        double numero=Double.parseDouble(valor);
        String mascara = "#.";
        String formateado="";
        String num="";
        
        for(int i = 0;i < ceros_a_la_derecha;i++){
            mascara += "0";
        }
        NumberFormat formatter = new DecimalFormat(mascara);
        num = ""+formatter.format(numero);
        
        if(num.substring(0, 1).equals(".")){
            formateado ="0"+num;
        }else{
            formateado =""+num;
        }
        
        if(ceros_a_la_derecha==0){
            formateado = formateado.substring( 0, formateado.length()-1 );
        }
        
        return formateado;
    }
    
    //convierte un documento a cadena
    public static String convertDocument2Utf8String(Document doc){
        
        String cadena_retorno = new String();
        
        DOMSource domSource = new DOMSource(doc);
        
        try{
            //set up a transformer
            TransformerFactory transfac = TransformerFactory.newInstance();
            Transformer trans = transfac.newTransformer();
            trans.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
            trans.setOutputProperty(OutputKeys.INDENT, "yes");
            trans.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "no");
            
            //create string from xml tree
            StringWriter sw = new StringWriter();
            StreamResult result = new StreamResult(sw);

            trans.transform(domSource, result);
            cadena_retorno = sw.toString();
        }
        catch(Exception e){
            e.printStackTrace();
        }

        return cadena_retorno.replaceAll("standalone=\"no\"", "");

    }
    
    //convierte una cadena a ascci a string
    public static String ascii2string(String ascii_string){
        String cadena = "";
        List<String> valor = Arrays.asList(ascii_string.split(","));
        for(String elemento: valor){
            char c = (char)Integer.parseInt(elemento);
            cadena += c;
        }
        return cadena;
    }
    
    //convierte la cadena del buscador a HashMap
    public static HashMap<String,String> convert2hash(String cadena){
        HashMap<String,String> valor= new HashMap<String,String>();
        List<String> convierte = Arrays.asList(cadena.split("\\|"));
        for( String elemento : convierte){
             String elemento_dividido[] = elemento.split("=");
             if(elemento_dividido.length > 1){
                 valor.put(elemento_dividido[0],elemento_dividido[1]);
             }
        }	
        return valor;
    }
    
    //normaliza una cadena
    public static String normalizaString(String cadena){
        cadena = cadena.replaceAll("[\\s]+", " ");
        cadena = cadena.replaceAll("[\\t]+", " ");
        cadena = cadena.replaceAll("[\\r]+", " ");
        cadena = cadena.replaceAll("[\\n]+", " ");
        cadena = cadena.trim();

        return cadena;
    }
    
    //quita caracteres ilegibles de una cadena
    public String quitaCaracteresRaros(String cadena){
        cadena = cadena.replaceAll("[\\s]+", "");
        cadena = cadena.replaceAll("[\\t]+", "");
        cadena = cadena.replaceAll("[\\r]+", "");
        cadena = cadena.replaceAll("[\\n]+", "");
        cadena = cadena.replaceAll("[\\.]", "");
        cadena = cadena.replaceAll("[\\/]", "");
        cadena = cadena.replaceAll("[\\&]", "");
        cadena = cadena.replaceAll("[/]", "");
        cadena = cadena.replaceAll("\\,", "");
        cadena = cadena.replaceAll("&", "");
        cadena = cadena.replaceAll("ñ", "n");
        cadena = cadena.replaceAll("Ñ", "N");
        //cadena = cadena.replaceAll(" ", "");
        cadena = cadena.replaceAll("[\\']", "");
        //cadena = cadena.replaceAll(":", "");
        //cadena = cadena.replaceAll("-", "");
        //cadena = cadena.replaceAll("_", "");
        //cadena = cadena.replaceAll(",", "");
        //cadena = cadena.replaceAll(">", "");
        //cadena = cadena.replaceAll("<", "");
        cadena = cadena.trim();

        return cadena;
    }
    
    
    public static byte[] convByte(String path_fichero) {
        byte[] cadByte=null;
        
        try {
            String comprobante = new String();
            
            File file = new File(path_fichero);
            
            try { BufferedReader in = new BufferedReader(new FileReader(file));
                String str;
                while ((str = in.readLine()) != null) {
                    comprobante += str;
                } in.close();
            }
            catch (IOException e) {
                e.printStackTrace();
            }
            
            cadByte = comprobante.getBytes("UTF8");
            
        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(StringHelper.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return cadByte;
    }

    public static String convertirnumero(String numero2){
	StringTokenizer token;
	String tok;
        n2t numero;
	String num;
	String res = "";
        num = numero2;
        if(num.indexOf(".") != -1){
		token = new StringTokenizer(num, ".");
		while(token.hasMoreElements()){
                    tok = token.nextToken();
                    numero = new n2t(Integer.parseInt(tok));
                    res = numero.convertirLetras(new BigInteger(tok));
		    tok = token.nextToken();
		    res = res + " PESOS " + tok + "/100 MN";
		}
	}else{
		numero = new n2t(Integer.parseInt(num));
		res = numero.convertirLetras(new BigInteger(num));
	}
	return res;
    }
    
    
    //convierte una cadena a muyusculas
    public static String getMayuscula(String car){
        String carMay = car;
		if(car.equals("á"))
			carMay = "Á";
        if(car.equals("a"))
            carMay = "A";
        if(car.equals("b"))
            carMay = "B";
        if(car.equals("c"))
            carMay = "C";
        if(car.equals("d"))
            carMay = "D";
        if(car.equals("e"))
            carMay = "E";
        if(car.equals("é"))
            carMay = "É";
        if(car.equals("f"))
            carMay = "F";
        if(car.equals("g"))
            carMay = "G";
        if(car.equals("h"))
            carMay = "H";
        if(car.equals("i"))
            carMay = "I";
        if(car.equals("í"))
            carMay = "Í";
        if(car.equals("j"))
            carMay = "J";
        if(car.equals("k"))
            carMay = "K";
        if(car.equals("l"))
            carMay = "L";
        if(car.equals("m"))
            carMay = "M";
        if(car.equals("n"))
            carMay = "N";
        if(car.equals("�"))
            carMay = "�";
        if(car.equals("o"))
            carMay = "O";
        if(car.equals("ó"))
            carMay = "Ó";
        if(car.equals("p"))
            carMay = "P";
        if(car.equals("q"))
            carMay = "Q";
        if(car.equals("r"))
            carMay = "R";
        if(car.equals("s"))
            carMay = "S";
        if(car.equals("t"))
            carMay = "T";
        if(car.equals("u"))
            carMay = "U";
        if(car.equals("ú"))
            carMay = "Ú";
        if(car.equals("v"))
            carMay = "V";
        if(car.equals("w"))
            carMay = "W";
        if(car.equals("x"))
            carMay = "X";
        if(car.equals("y"))
            carMay = "Y";
        if(car.equals("z"))
            carMay = "Z";
        return carMay;
    }
    
    //agrega comas a una cantidad en numer
    public static String AgregaComas(String num) {
	String cadena_retorno="";
	String tamano="";
        String cantidad=num;
        String primer_digito = num.substring(0, 1);//extrae el primer digito de la cantidad
        if(primer_digito.equals(".")){//si es igual a un punto le agrega un cero
            cantidad="0"+num;
        }
        
        String[] valor = cantidad.split("\\.");
        
	String[] atratar = new StringBuffer(valor[0]).reverse().toString().split("");
        for(int i=0; i < atratar.length; i++){
                cadena_retorno += atratar[i];
                tamano +=atratar[i];
                if(tamano.length() % 3 == 0){
                        cadena_retorno += ",";
                }
        }
        if((cadena_retorno.substring(cadena_retorno.length()-1,cadena_retorno.length())).equals(",")){
                        cadena_retorno = cadena_retorno.substring(0,cadena_retorno.length()-1);
        }
        cadena_retorno = new StringBuffer(cadena_retorno).reverse().toString();
        cadena_retorno = cadena_retorno.substring(0,cadena_retorno.length()-1);
        if(valor.length == 2){
                cadena_retorno += "." + valor[1];;
        }
        return cadena_retorno;
    }
    
    
    
    //quitar comas a una cantidad en numer
    public static String removerComas(String num) {
        String cadena_retorno="0";
        String cadena[] = num.split(",");
        
        cadena_retorno = StringUtils.join(cadena, "");
        
        return cadena_retorno;
    }
    
    //asignar valor cero si un campo numerico viene vacio
    public static String asignarCero(String campo) {
        String cadena_retorno="0";
        if(!campo.equals("") && campo != null){
            cadena_retorno=campo;
        }
        
        return cadena_retorno;
    }
    
    
    
    public static String isNullString(String str){
        if(str.equals(null)){
            str = "";
        }
        if(str.equals("null")){
            str = "";
        }
        return str;
    }
    
    public static String capitalizaString(String cadena){
        String [] temp;
        String retorno="";
        
	temp = cadena.split("");
	for(String i:Arrays.asList(temp))
            retorno += getMayuscula(i);

        return retorno;
    }
    
    
    public static String remueve_tildes(String input) {
        // Cadena de caracteres original a sustituir.
        String original = "áàäéèëíìïóòöúùuñÁÀÄÉÈËÍÌÏÓÒÖÚÙÜÑçÇ";
        // Cadena de caracteres ASCII que reemplazarán los originales.
        String ascii = "aaaeeeiiiooouuunAAAEEEIIIOOOUUUNcC";
        String output = input;
        for (int i=0; i<original.length(); i++) {
            // Reemplazamos los caracteres especiales.
            output = output.replace(original.charAt(i), ascii.charAt(i));
        }//for i
        return output;
    }

    
    
    //verificar si un campo de tipo checkbox esta o no seleccionado al enviar el
    public static String verificarCheckBox(String campo) {
        String cadena_retorno = "false";
        
        if(campo == null || !campo.equals("on")){
            cadena_retorno = "false";
        }else{
            cadena_retorno = "true";
        }
        return cadena_retorno;
    }
    
    
    //verificar si un campo de tipo Select esta o no habilitado al enviar al controller
    public static String verificarSelect(String campo) {
        String cadena_retorno = "0";
        if(campo == null){
            cadena_retorno = "0";
        }else{
            cadena_retorno = campo;
        }
        return cadena_retorno;
    }
    
    //reemplazar una cadena por una macro
    public static String addMacroString(String macro, String texto, String cadena) {
        cadena = cadena.replaceAll(texto, macro);
        
        return cadena;
    }
    
    //reemplazar una macro por una cadena
    public static String replaceStringMacro(String macro, String texto, String cadena) {
        cadena = cadena.replaceAll(macro, texto);
        
        return cadena;
    }
    
    
    //generar identificador unico universal
    public static String generaUUID() {
        String  new_uuid =  String.valueOf(UUID.randomUUID());
        return new_uuid;
    }
    
    
    public static boolean validateEmail(String email) {
        String PATTERN_EMAIL = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
        
        // Compiles the given regular expression into a pattern.
        Pattern pattern = Pattern.compile(PATTERN_EMAIL);
        
        // Match the given input against this pattern
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }
    
    
}
