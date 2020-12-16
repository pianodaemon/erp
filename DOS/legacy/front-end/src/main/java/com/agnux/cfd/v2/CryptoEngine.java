package com.agnux.cfd.v2;

import java.io.DataInputStream;
import java.io.File;
import java.security.*;
import javax.security.cert.X509Certificate;
import java.io.IOException;
import java.io.InputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import org.apache.commons.ssl.PKCS8Key;

/**
 *
 * @author agnux
 */
public class CryptoEngine {

    /**
     * ********************* INICIA - Codigo para la md5sum ***************************
     */
    /*private static byte[] createChecksum(String filename) {
        byte[] arreglo_retorno;
        try {
            InputStream fis = new FileInputStream(filename);

            byte[] buffer = new byte[1024];
            MessageDigest complete = MessageDigest.getInstance("MD5");
            int numRead;
            do {
                numRead = fis.read(buffer);
                if (numRead > 0) {
                    complete.update(buffer, 0, numRead);
                }
            } while (numRead != -1);
            fis.close();
            arreglo_retorno = complete.digest();
        } catch (Exception e) {
            System.out.println(e.toString());
            arreglo_retorno = null;
        }

        return arreglo_retorno;
    }*/

    /**
     * Convierte una fichero a una cadena md5
     *
     * @param filename
     * @return la cadena md5.
     */
    /*public static String md5sum(String filename) throws Exception {
        byte[] b = createChecksum(filename);
        String result = "";
        for (int i = 0; i < b.length; i++) {
            result
                    += Integer.toString((b[i] & 0xff) + 0x100, 16).substring(1);
        }
        return result;
    }*/

    /**
     * ********************* FINALIZA - Codigo para la md5sum *************************
     */

    /**
     * ********************* INICIA - Codigo para la sign *******************************
     */
    /**
     * Verify a MD5 Hash Signed.
     *
     * @param pk The public key from The Cert used.
     * @param cadenaoriginal The Origina String unsigned.
     * @param hash_md5_firmado The Md5 Signed on Array byte format.
     *
     * @return True if the verify was successful.
     */
    /*   public static boolean verifySign(PublicKey pk , String cadenaoriginal, byte[] hash_md5_firmado){
        boolean valor_retorno = false;

        Signature firma;
        try {
            firma = Signature.getInstance("SHA1withRSA");
            firma.initVerify(pk);
            firma.update(cadenaoriginal.getBytes("UTF-8"));
            valor_retorno = firma.verify(hash_md5_firmado);
        } catch (Exception ex) {
            ex.printStackTrace();
        }


        return valor_retorno;
    }*/
    /**
     * Extract a Public key from a CertFile.
     *
     * @param certfile The file with a Path where private key is found.
     *
     * @return A public key instance.
     */
    /*    public static PublicKey getPublicKeyFromCertFile(String certifle){
        PublicKey pk = null;
        InputStream fis;
        try {
            fis = new FileInputStream(certifle); // Se maneja la excepcion
            X509Certificate cert = X509Certificate.getInstance(fis);
            pk = cert.getPublicKey();
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return pk;
    }*/
    /**
     * Digitally Sign a String inserted.
     *
     * @param filename The file with a Path where private key is found.
     * @param password The password of The Prtivate key.
     * @param cadenaoriginal The String to be Signed.
     *
     * @return A character array with the Base64 encoded data.
     */
    public static String sign(String filename, String password, String cadenaoriginal) {

        String valor_retorno = null;
        //InputStream archivoClavePrivada = null;
        byte[] clavePrivada = null;
        Signature firma = null;
        PKCS8Key pkcs8 = null;
        PrivateKey pk = null;

        try {
            clavePrivada = serializeFile(filename);
            //archivoClavePrivada = new FileInputStream(filename);
            //clavePrivada = getBytesFromInputStream(archivoClavePrivada);
        } catch (Exception e) {
            System.out.println(e.toString());
        }

        try {
            pkcs8 = new PKCS8Key(clavePrivada, password.toCharArray());
            pk = pkcs8.getPrivateKey();
            firma = Signature.getInstance("SHA1withRSA", "SunRsaSign");
            firma.initSign(pk);
        } catch (GeneralSecurityException e) {
            e.printStackTrace();
        }

        try {
            firma.update(cadenaoriginal.getBytes("UTF-8"));
            byte[] firmaDigital = firma.sign();
            valor_retorno = new String(Base64Coder.encode(firmaDigital));
            //valor_retorno = base64(new String(firma.sign()));
        } catch (Exception e) {
            e.printStackTrace();
        }

        return valor_retorno;
    }

    public static String encodeCertToBase64(String certifle){
        char[] psB64Certificate = null;
        FileInputStream fis;
        try {
            fis = new FileInputStream(certifle); // Se maneja la excepcion
            X509Certificate cert = X509Certificate.getInstance(fis);
            byte[] buf = cert.getEncoded();
            psB64Certificate = Base64Coder.encode(buf);
        } catch (Exception ex) {
            ex.printStackTrace();
        }


        return new String(psB64Certificate);
    }

 /*
     * Agregado Viernes 9 de julio del 2010
     *
     */
 /*    public static X509Certificate decodeCertFromBase64(String psB64Certificate){
        X509Certificate cert = null;
        try {
            cert = X509Certificate.getInstance(Base64Coder.decode(psB64Certificate));
        } catch (Exception ex) {
            ex.printStackTrace();
        }


        return cert;
    }*/

 /*    public static PublicKey getPublicKeyFromCertOnBase64(String psB64Certificate){
        X509Certificate cert = decodeCertFromBase64(psB64Certificate);
        return cert.getPublicKey();
    }*/

 
/*    private static byte[] getBytesFromInputStream(InputStream is) {
        int totalBytes = 714;
        byte[] buffer = null;
        try {
            buffer = new byte[totalBytes];
            is.read(buffer, 0, totalBytes);
            is.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return buffer;
    }*/
    private static byte[] serializeFile(final String absPathToFile) throws FileNotFoundException, IOException {
        File f = new File(absPathToFile);
        FileInputStream fis = new FileInputStream(f);
        DataInputStream dis = new DataInputStream(fis);
        byte[] buffer = new byte[(int) f.length()];
        dis.readFully(buffer);
        dis.close();
        return buffer;
    }
    /**
     * ********************* FINALIZA - Codigo para la sign ***************************
     */
}
