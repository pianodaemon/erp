/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.agnux.common.helpers;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.Writer;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import java.awt.image.BufferedImage;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import javax.imageio.ImageIO;

/**
 *
 * @author agnux
 */
public class CodigoQRHelper {
    
    public String CodeQR(String FORMATO_IMAGEN, String RUTA_IMAGEN, int ancho, int alto, String cadenaDatos  ) throws WriterException, FileNotFoundException, IOException{
        BitMatrix bm;
        Writer writer = new QRCodeWriter(); 
        bm = writer.encode(cadenaDatos, BarcodeFormat.QR_CODE, ancho, alto);
        
        BufferedImage image = new BufferedImage(ancho, alto, BufferedImage.TYPE_INT_RGB); 
        
        for (int y = 0; y < ancho; y++) {
           for (int x = 0; x < alto; x++) {
               int grayValue = (bm.get(x, y) ? 1 : 0) & 0xff;
               image.setRGB(x, y, (grayValue == 0 ? 0 : 0xFFFFFF));
           }
        }
        
        image = invertirColores(image, ancho, alto);

        FileOutputStream qrCode = new FileOutputStream(RUTA_IMAGEN);
        ImageIO.write(image, FORMATO_IMAGEN, qrCode);
        
        qrCode.close();
        
        return RUTA_IMAGEN;
    }
    
    
    private static  BufferedImage invertirColores(BufferedImage imagen, int ancho, int alto) {
        for (int x = 0; x < ancho; x++) {
            for (int y = 0; y < alto; y++) {
                int rgb = imagen.getRGB(x, y);
                if (rgb == -16777216) {
                    imagen.setRGB(x, y, -1);
                } else {
                    imagen.setRGB(x, y, -16777216);
                }
            }
        }
        return imagen;
    } 
    
    
}
