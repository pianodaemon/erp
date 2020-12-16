/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.agnux.common.helpers;
/*
import java.io.File;
import net.sourceforge.barbecue.Barcode;
import net.sourceforge.barbecue.BarcodeException;
import net.sourceforge.barbecue.BarcodeFactory;
import net.sourceforge.barbecue.BarcodeImageHandler;
import net.sourceforge.barbecue.output.OutputException;
*/


import java.awt.Color;
import java.io.File;
import net.sourceforge.barbecue.Barcode;
import net.sourceforge.barbecue.BarcodeFactory;
import net.sourceforge.barbecue.BarcodeImageHandler;

/**
 *
 * @author mi_compu
 */
public class BarcodeHelper {
    
    public static String createImgCodBarr128(String lote) throws Exception{
        String ruta_imagen_cod_barr ="/tmp/"+lote+".png";
        
        Barcode barcode = BarcodeFactory.createCode128(lote);
        //barcode.setDrawingText(false);
        barcode.setLabel(lote);
        
        
       
    	File f = new File (ruta_imagen_cod_barr);
        
        BarcodeImageHandler.savePNG(barcode,f);
         
        return ruta_imagen_cod_barr;
    }
    
    
}
