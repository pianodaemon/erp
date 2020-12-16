/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.agnux.common.helpers;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.List;

/**
 *
 * @author pianodaemon
 */
public class FileHelper {
    
    public static boolean createFileWithText(String dir_fichero,String archivo,String cadena_impresa){
    	boolean valor_retorno = true;
        
    	File fichero = new File (dir_fichero,archivo);
        try {
            if (fichero.createNewFile()){
                System.out.println("Se ha creado el Fichero: " + dir_fichero + "/" + archivo);
            }
            else{
                System.out.println("No se ha creado el Fichero: " + dir_fichero  + "/" + archivo);
            }
        } catch (IOException ioe) {
                ioe.printStackTrace();
                valor_retorno = false;
        }
        
        try{
            //System.out.println("Alimentando contenido al Fichero: " + dir_fichero + "/" + archivo);
            FileOutputStream out = new FileOutputStream(fichero,true);
            OutputStreamWriter os = new OutputStreamWriter(out,"UTF-8");
            BufferedWriter br = new BufferedWriter(os);
            PrintWriter fileOut = new PrintWriter(br);
            fileOut.println(cadena_impresa);
            fileOut.close();
            System.out.println("El Contenido fue alimentado con exito al Fichero: " + dir_fichero + "/" + archivo);
        }catch (IOException ioe) {
            System.out.println(ioe.toString());
            System.out.println("No se ha alimentado el contenido para el Fichero: " + dir_fichero + "/" + archivo);
            valor_retorno = false;
        }
        
	return valor_retorno;
    }

    public static boolean createFileWithText(String ruta_fichero,String cadena_impresa){
    	boolean valor_retorno = true;
        
    	File fichero = new File (ruta_fichero);
        try {
            if (fichero.createNewFile()){
                System.out.println("Se ha creado el Fichero: " + ruta_fichero);
            }
            else{
                System.out.println("No se ha creado el Fichero: " + ruta_fichero);
            }
        } catch (IOException ioe) {
                ioe.printStackTrace();
                valor_retorno = false;
        }
        
        try{
            //System.out.println("Alimentando contenido al Fichero: " + dir_fichero + "/" + archivo);
            FileOutputStream out = new FileOutputStream(fichero,true);
            OutputStreamWriter os = new OutputStreamWriter(out,"UTF-8");
            BufferedWriter br = new BufferedWriter(os);
            PrintWriter fileOut = new PrintWriter(br);
            fileOut.println(cadena_impresa);
            fileOut.close();
            System.out.println("El Contenido fue alimentado con exito al Fichero: " + ruta_fichero);
        }catch (IOException ioe) {
            System.out.println(ioe.toString());
            System.out.println("No se ha alimentado el contenido para el Fichero: " + ruta_fichero);
            valor_retorno = false;
        }
        
	return valor_retorno;
    }
    
    public static boolean addText2File(String file,String text){
        boolean valor_retorno = true;
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(file,true)) ;
            writer.write(text) ;
            writer.close();
        }
        catch (IOException e) {
            System.out.println("Error al intentar agregar texto al fichero: " + file);
            valor_retorno = false;
        }
        
        return valor_retorno;
    }
    
    public static void move(String fromFileName, String toFileName) throws IOException, Exception {
    	copy(fromFileName, toFileName);
        delete(fromFileName);
    }
    
    public static void copy(String fromFileName, String toFileName) throws IOException {
        File fromFile = new File(fromFileName);
    	File toFile = new File(toFileName);
        
    	if (!fromFile.exists()) throw new IOException("FileCopy: " + "No se encuentra fichero a copiar: " + fromFileName);
        
    	if (!fromFile.isFile()) throw new IOException("FileCopy: " + "No se puede copiar directorio: " + fromFileName);
        
    	if (!fromFile.canRead()) throw new IOException("FileCopy: " + "No se puede leer fichero a copiar: " + fromFileName);
        
    	if (toFile.isDirectory()) toFile = new File(toFile, fromFile.getName());
        
    	if (toFile.exists()) {
            //throw new IOException("FileCopy: " + "El archivo ya existe y no puede ser sobreescrito");
            toFile.delete();
            
        }
        
    	FileInputStream from = null;
    	FileOutputStream to = null;
    	try {
            from = new FileInputStream(fromFile);
            to = new FileOutputStream(toFile);
            byte[] buffer = new byte[4096];
            int bytesRead;

            while ((bytesRead = from.read(buffer)) != -1){
                to.write(buffer, 0, bytesRead); // write
            }
            
    	} finally {
            if (from != null)
                try {
                    from.close();
    		} catch (IOException e) {

                }
            if (to != null)
    		try {
                    to.close();
    		} catch (IOException e) {

                }
                
            }
    }
    
    
    
    public static boolean delete(String fileName) throws Exception {
        // A File object to represent the filename
        File f = new File(fileName);
        
        // Make sure the file or directory exists and isn't write protected
        if (!f.exists()) {
            throw new Exception("Delete: No se encuentra el fichero o directorio: " + fileName);
        }
        
        
        if (!f.canWrite()){
            throw new Exception("Delete: Existe proteccion contra escritura: "  + fileName);
        }
        
        // If it is a directory, make sure it is empty
        if (f.isDirectory()) {
          String[] files = f.list();
          if (files.length > 0) {
                throw new Exception("Delete: El directorio no se encuentra vacio: " + fileName);
            }
        }
        
        // Attempt to delete it
        boolean success = f.delete();
        
        if (!success) { throw new Exception("Delete: Falla en la eliminacion del fichero"); }
        
        return success;
    }
    
    
    public static String stringFromFile(String url){
        String retorno = new String();
        
	File file = new File(url);
        
	try {
            BufferedReader in = new BufferedReader(new FileReader(file));
            String str;
            while ((str = in.readLine()) != null) {
                retorno += str;
            } in.close();
	}
	catch (IOException e) {
            e.printStackTrace();
        }
        
        return retorno;
        
    }
    
    public static  byte[] BytesFromFile(File file) throws IOException {
        InputStream is = new FileInputStream(file);
        
        // Get the size of the file
        long length = file.length();
        
        if (length > Integer.MAX_VALUE) {
            ;// File is too large
        }
        
        // Create the byte array to hold the data
        byte[] bytes = new byte[(int)length];
        
        // Read in the bytes
        int offset = 0;
        int numRead = 0;
        while (offset < bytes.length && (numRead=is.read(bytes, offset, bytes.length-offset)) >= 0) {
            offset += numRead;
        }
        
        // Ensure all the bytes have been read in
        if (offset < bytes.length) {
            throw new IOException("Could not completely read file "+file.getName());
        }
        
        // Close the input stream and return bytes
        is.close();
        
	return bytes;
    }
    
    
    public static List<File> listRegularFilesOfDirectory(String directorio){
        List<File> valor_retorno = null;
        File dir = new File(directorio);

        // This filter only returns regular files without directories
        FileFilter fileFilter = new FileFilter() {
            public boolean accept(File file) {
                return !file.isDirectory();
            }
        };
        
        valor_retorno = Arrays.asList(dir.listFiles(fileFilter));  
        
        return valor_retorno;
    }
    
    
    public static List<File> listDirectoriesOfDirectory(String directorio){
        List<File> valor_retorno = null;
        File dir = new File(directorio);
        
        // This filter only returns directories over the directory
        FileFilter fileFilter = new FileFilter() {
            
            public boolean accept(File file) {
                return file.isDirectory();
            }
        };
           
        valor_retorno = Arrays.asList(dir.listFiles(fileFilter));  
        
        return valor_retorno;
    }

    public static  String saveByteFile(byte[] file, String dir) throws IOException {
        final String nombreArch = dir;
	final File arch = new File(nombreArch);
	final OutputStream aSalida = new FileOutputStream(arch);
        
	aSalida.write(file);
	aSalida.flush();
	aSalida.close();
        
	return nombreArch;
    }
    
    
    
    
}
