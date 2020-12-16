/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.agnux.kemikal.reportes;
import com.agnux.common.helpers.StringHelper;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

/**
 *
 * @author Ezequiel
 */
public class PdfReporteFormulas {
    
    public PdfReporteFormulas(HashMap<String, String> datosEncabezadoPie,String fileout,ArrayList<HashMap<String, Object>> lista,HashMap<String, String> datos, ArrayList<HashMap<String, String>> especificaciones, String stock, String costear, String tipo_cambio, ArrayList<HashMap<String, String>> lista_procedimiento) throws FileNotFoundException, DocumentException {
        
        Font smallBoldFont = new Font(Font.getFamily("ARIAL"),7,Font.NORMAL,BaseColor.WHITE);
        Font fuenteCont = new Font(Font.getFamily("ARIAL"),10,Font.NORMAL,BaseColor.BLACK);
        Font fuentenegrita = new Font(Font.getFamily("ARIAL"),8,Font.BOLD,BaseColor.BLACK);
        Font fuenteCont2 = new Font(Font.getFamily("ARIAL"),8,Font.NORMAL,BaseColor.BLACK);
        
        String nombre_mes = datos.get("nombre_mes");
        String[] fC = datos.get("fecha").split("-");
        String fecha_ruta = fC[0]+"-"+nombre_mes+"-"+fC[2];
        
        //datos para el encabezado
        datos.put("empresa", datosEncabezadoPie.get("nombre_empresa_emisora"));
        datos.put("titulo_reporte", datosEncabezadoPie.get("titulo_reporte"));
        datos.put("periodo", "");
        
        //datos para el pie de pagina
        datos.put("codigo1", datosEncabezadoPie.get("codigo1"));
        datos.put("codigo2", datosEncabezadoPie.get("codigo2"));
        
        HeaderFooter event = new HeaderFooter(datos);
        //Document reporte = new Document(PageSize.LETTER.rotate(),-50,-50,60,30);
        Document reporte = new Document(PageSize.LETTER, -50, -50, 55, 30);
        PdfWriter writer = PdfWriter.getInstance(reporte, new FileOutputStream(fileout));
        writer.setPageEvent(event);
        
        try {
             reporte.open();
             
            float [] tam_tablax = {2.5f,0.6f,5.5f,0.5f,2f,3};
            PdfPTable tablaX = new PdfPTable(tam_tablax);
            PdfPCell celdaX;
            tablaX.setKeepTogether(false);
            
            //columna 1 fil1
            celdaX = new PdfPCell(new Paragraph("",fuentenegrita));
            celdaX.setHorizontalAlignment(Element.ALIGN_LEFT);
            celdaX.setVerticalAlignment(Element.ALIGN_MIDDLE);
            celdaX.setBorderWidthBottom(0);
            celdaX.setBorderWidthTop(0);
            celdaX.setBorderWidthRight(0);
            celdaX.setBorderWidthLeft(0);
            tablaX.addCell(celdaX);
            
            //columna 2 fil1
            celdaX = new PdfPCell(new Paragraph("",fuentenegrita));
            celdaX.setHorizontalAlignment(Element.ALIGN_LEFT);
            celdaX.setVerticalAlignment(Element.ALIGN_MIDDLE);
            celdaX.setBorderWidthBottom(0);
            celdaX.setBorderWidthTop(0);
            celdaX.setBorderWidthRight(0);
            celdaX.setBorderWidthLeft(0);
            tablaX.addCell(celdaX);
             
            //columna 3 fil1
            celdaX = new PdfPCell(new Paragraph("",fuentenegrita));
            celdaX.setHorizontalAlignment(Element.ALIGN_LEFT);
            celdaX.setVerticalAlignment(Element.ALIGN_MIDDLE);
            celdaX.setBorderWidthBottom(0);
            celdaX.setBorderWidthTop(0);
            celdaX.setBorderWidthRight(0);
            celdaX.setBorderWidthLeft(0);
            tablaX.addCell(celdaX);
            
            //columna 5 y 6 fil1
            celdaX = new PdfPCell(new Paragraph("FECHA    "+fecha_ruta+"   F O R M U L A : "+datos.get("sku"),fuentenegrita));
            celdaX.setBorderWidthBottom(1);
            celdaX.setBorderWidthTop(0);
            celdaX.setBorderWidthRight(0);
            celdaX.setColspan(3);
            celdaX.setBorderWidthLeft(0);
            tablaX.addCell(celdaX);
            
            
            
            
            //columna 1 fil2
            celdaX = new PdfPCell(new Paragraph("Producto Terminado : ",fuenteCont2));
            celdaX.setHorizontalAlignment(Element.ALIGN_LEFT);
            celdaX.setVerticalAlignment(Element.ALIGN_MIDDLE);
            celdaX.setBorderWidthBottom(0);
            celdaX.setBorderWidthTop(0);
            celdaX.setBorderWidthRight(0);
            celdaX.setBorderWidthLeft(0);
            tablaX.addCell(celdaX);
            
            //columna 2 vacio fil2
            celdaX = new PdfPCell(new Paragraph("",fuenteCont2));
            celdaX.setHorizontalAlignment(Element.ALIGN_LEFT);
            celdaX.setBorderWidthBottom(0);
            celdaX.setBorderWidthTop(0);
            celdaX.setBorderWidthRight(0);
            celdaX.setBorderWidthLeft(0);
            tablaX.addCell(celdaX);
            
            //columna 3 fil2
            celdaX = new PdfPCell(new Paragraph(datos.get("sku"),fuenteCont2));
            celdaX.setHorizontalAlignment(Element.ALIGN_LEFT);
            celdaX.setVerticalAlignment(Element.ALIGN_MIDDLE);
            celdaX.setBorderWidthBottom(1);
            //celdaX.setColspan(2);
            celdaX.setBorderWidthTop(0);
            celdaX.setBorderWidthRight(0);
            celdaX.setBorderWidthLeft(0);
            tablaX.addCell(celdaX);
            
            //columna 4 fil2
            celdaX = new PdfPCell(new Paragraph("",fuenteCont2));
            celdaX.setBorderWidthBottom(1);
            celdaX.setBorderWidthTop(0);
            //celdaX.setColspan(2);
            celdaX.setBorderWidthRight(0);
            celdaX.setBorderWidthLeft(0);
            tablaX.addCell(celdaX);
            
            //columna 5 fil2
            celdaX = new PdfPCell(new Paragraph("",fuenteCont2));
            celdaX.setBorderWidthBottom(1);
            celdaX.setBorderWidthTop(0);
            //celdaX.setColspan(2);
            celdaX.setBorderWidthRight(0);
            celdaX.setBorderWidthLeft(0);
            tablaX.addCell(celdaX);
            
            //columna 6 vacio fil2
            celdaX = new PdfPCell(new Paragraph("",fuenteCont2));
            celdaX.setBorderWidthBottom(1);
            celdaX.setBorderWidthTop(0);
            //celdaX.setColspan(2);
            celdaX.setBorderWidthRight(0);
            celdaX.setBorderWidthLeft(0);
            tablaX.addCell(celdaX);
            
            //columna 1 fil2
            celdaX = new PdfPCell(new Paragraph("Descripcion : ",fuenteCont2));
            celdaX.setHorizontalAlignment(Element.ALIGN_LEFT);
            celdaX.setVerticalAlignment(Element.ALIGN_MIDDLE);
            celdaX.setBorderWidthBottom(0);
            celdaX.setBorderWidthTop(0);
            celdaX.setBorderWidthRight(0);
            celdaX.setBorderWidthLeft(0);
            tablaX.addCell(celdaX);
            
            //columna 2 vacio fil2
            celdaX = new PdfPCell(new Paragraph("",fuenteCont2));
            celdaX.setHorizontalAlignment(Element.ALIGN_LEFT);
            celdaX.setBorderWidthBottom(0);
            celdaX.setBorderWidthTop(0);
            celdaX.setBorderWidthRight(0);
            celdaX.setBorderWidthLeft(0);
            tablaX.addCell(celdaX);
            
            //columna 3 fil2
            celdaX = new PdfPCell(new Paragraph(datos.get("descripcion"),fuenteCont2));
            celdaX.setHorizontalAlignment(Element.ALIGN_LEFT);
            celdaX.setVerticalAlignment(Element.ALIGN_MIDDLE);
            celdaX.setBorderWidthBottom(1);
            //celdaX.setColspan(2);
            celdaX.setBorderWidthTop(0);
            celdaX.setBorderWidthRight(0);
            celdaX.setBorderWidthLeft(0);
            tablaX.addCell(celdaX);
            
            
            //columna 4 fil2
            celdaX = new PdfPCell(new Paragraph("",fuenteCont2));
            celdaX.setBorderWidthBottom(1);
            celdaX.setBorderWidthTop(0);
            //celdaX.setColspan(2);
            celdaX.setBorderWidthRight(0);
            celdaX.setBorderWidthLeft(0);
            tablaX.addCell(celdaX);
            
            String etiqueta_tc="";
            String valor_tc="";
            if(costear.equals("true")){
                etiqueta_tc="Tipo de Cambio";
                valor_tc = StringHelper.roundDouble(tipo_cambio,4);
            }
            
            //columna 5 fil2
            celdaX = new PdfPCell(new Paragraph(etiqueta_tc,fuenteCont2));
            celdaX.setHorizontalAlignment(Element.ALIGN_RIGHT);
            celdaX.setBorderWidthBottom(0);
            celdaX.setBorderWidthTop(0);
            celdaX.setBorderWidthRight(0);
            celdaX.setBorderWidthLeft(0);
            tablaX.addCell(celdaX);
            
            //columna 6 vacio fil2
            celdaX = new PdfPCell(new Paragraph(valor_tc, fuenteCont2));
            if(costear.equals("true")){
                celdaX.setBorderWidthBottom(1);
            }else{
                celdaX.setBorderWidthBottom(0);
            }
            celdaX.setBorderWidthTop(0);
            celdaX.setBorderWidthRight(0);
            celdaX.setBorderWidthLeft(0);
            tablaX.addCell(celdaX);
            
            System.out.println("Costear:"+ costear);
            reporte.add(tablaX);
             
             
             //sku
            //descripcion
            //cantidad
            //agregado
            //lote
             float [] widths = {0.8f,2.5f,2.5f,5.2f,2.5F,2.2F,2.2F};//Tamaño de las Columnas.
             PdfPTable tabla = new PdfPTable(widths);
             PdfPCell celda;
             tabla.setKeepTogether(false);
             tabla.setHeaderRows(2);
             
             
             //1 AL 6          
             celda = new PdfPCell(new Paragraph("",smallBoldFont));
             celda.setColspan(7);
             celda.setBorder(0);
             tabla.addCell(celda);
             
             
             //AQUI VA EL ENCABEZADO 2 DEL DOCUMENTO
             //1
             celda = new PdfPCell(new Paragraph("#",smallBoldFont));
             celda.setUseAscender(true);
             celda.setHorizontalAlignment(Element.ALIGN_CENTER);
             celda.setUseDescender(true);
             celda.setVerticalAlignment(Element.ALIGN_MIDDLE);
             celda.setBackgroundColor(BaseColor.BLACK);
             tabla.addCell(celda);
             
             //1
             celda = new PdfPCell(new Paragraph("CODIGO",smallBoldFont));
             celda.setUseAscender(true);
             celda.setHorizontalAlignment(Element.ALIGN_CENTER);
             celda.setUseDescender(true);
             celda.setVerticalAlignment(Element.ALIGN_MIDDLE);
             celda.setBackgroundColor(BaseColor.BLACK);
             tabla.addCell(celda);
             
             //AQUI VA EL ENCABEZADO 2 DEL DOCUMENTO
             //1           
             celda = new PdfPCell(new Paragraph("TIPO",smallBoldFont));
             celda.setUseAscender(true);
             celda.setHorizontalAlignment(Element.ALIGN_CENTER);
             celda.setUseDescender(true);
             celda.setVerticalAlignment(Element.ALIGN_MIDDLE);
             celda.setBackgroundColor(BaseColor.BLACK);
             tabla.addCell(celda);
             
             //2
             celda = new PdfPCell(new Paragraph("DESCRIPCION",smallBoldFont));
             celda.setUseAscender(true);
             celda.setHorizontalAlignment(Element.ALIGN_CENTER);
             celda.setUseDescender(true);
             celda.setVerticalAlignment(Element.ALIGN_MIDDLE);
             celda.setBackgroundColor(BaseColor.BLACK);            
             tabla.addCell(celda);
             
             //3
             celda = new PdfPCell(new Paragraph("CANT. REQ.",smallBoldFont));
             celda.setUseAscender(true);
             celda.setHorizontalAlignment(Element.ALIGN_CENTER);
             celda.setUseDescender(true);
             celda.setVerticalAlignment(Element.ALIGN_MIDDLE);
             celda.setBackgroundColor(BaseColor.BLACK);            
             tabla.addCell(celda);
             
             
            if(costear.equals("true")){
                 //12
                 celda = new PdfPCell(new Paragraph("COSTO UNITARIO",smallBoldFont));
                 celda.setUseAscender(true);
                 celda.setHorizontalAlignment(Element.ALIGN_CENTER);
                 celda.setUseDescender(true);
                 celda.setVerticalAlignment(Element.ALIGN_MIDDLE);
                 celda.setBackgroundColor(BaseColor.BLACK);            
                 tabla.addCell(celda);

                 //13
                 celda = new PdfPCell(new Paragraph("IMPORTE",smallBoldFont));
                 celda.setUseAscender(true);
                 celda.setHorizontalAlignment(Element.ALIGN_CENTER);
                 celda.setUseDescender(true);
                 celda.setVerticalAlignment(Element.ALIGN_MIDDLE);
                 celda.setBackgroundColor(BaseColor.BLACK);            
                 tabla.addCell(celda);
            }else{
                 //12
                 celda = new PdfPCell(new Paragraph("AGREGADO",smallBoldFont));
                 celda.setUseAscender(true);
                 celda.setHorizontalAlignment(Element.ALIGN_CENTER);
                 celda.setUseDescender(true);
                 celda.setVerticalAlignment(Element.ALIGN_MIDDLE);
                 celda.setBackgroundColor(BaseColor.BLACK);            
                 tabla.addCell(celda);

                 //13
                 celda = new PdfPCell(new Paragraph("LOTE",smallBoldFont));
                 celda.setUseAscender(true);
                 celda.setHorizontalAlignment(Element.ALIGN_CENTER);
                 celda.setUseDescender(true);
                 celda.setVerticalAlignment(Element.ALIGN_MIDDLE);
                 celda.setBackgroundColor(BaseColor.BLACK);            
                 tabla.addCell(celda);
            }
             
            
             Double suma_importe_costo = 0.0;
             
             int contador = 1;
             Double sumatoria = 0.0;
             for (int j=0;j<lista.size();j++){
                 
                 HashMap<String,Object> registro = lista.get(j);
                 
                 //1           
                 celda = new PdfPCell(new Paragraph(String.valueOf(""+contador),fuenteCont));
                 celda.setUseAscender(true);
                 celda.setHorizontalAlignment(Element.ALIGN_LEFT);
                 celda.setUseDescender(true);
                 celda.setVerticalAlignment(Element.ALIGN_MIDDLE);
                 tabla.addCell(celda);
                 
                 //1           
                 celda = new PdfPCell(new Paragraph(String.valueOf(registro.get("sku")),fuenteCont));
                 celda.setUseAscender(true);
                 celda.setHorizontalAlignment(Element.ALIGN_LEFT);
                 celda.setUseDescender(true);
                 celda.setVerticalAlignment(Element.ALIGN_MIDDLE);
                 tabla.addCell(celda);
                 
                 celda = new PdfPCell(new Paragraph(String.valueOf(registro.get("tipo")),fuenteCont));
                 celda.setUseAscender(true);
                 celda.setHorizontalAlignment(Element.ALIGN_LEFT);
                 celda.setUseDescender(true);
                 celda.setVerticalAlignment(Element.ALIGN_MIDDLE);
                 tabla.addCell(celda);
                 
                 //2
                 celda = new PdfPCell(new Paragraph(String.valueOf(registro.get("descripcion")),fuenteCont));
                 celda.setUseAscender(true);
                 celda.setHorizontalAlignment(Element.ALIGN_LEFT);
                 celda.setUseDescender(true);
                 celda.setVerticalAlignment(Element.ALIGN_MIDDLE);
                 tabla.addCell(celda);
                 
                 //3
                 celda = new PdfPCell(new Paragraph(String.valueOf(registro.get("cantidad")),fuenteCont));
                 celda.setUseAscender(true);
                 celda.setHorizontalAlignment(Element.ALIGN_RIGHT);
                 celda.setUseDescender(true);
                 celda.setVerticalAlignment(Element.ALIGN_MIDDLE);
                 tabla.addCell(celda);
                 
                 if(costear.equals("true")){
                     if(stock.equals("true")){
                         //4
                         celda = new PdfPCell(new Paragraph(StringHelper.AgregaComas(registro.get("costo_unitario").toString()),fuenteCont));
                         celda.setUseAscender(true);
                         celda.setHorizontalAlignment(Element.ALIGN_RIGHT);
                         celda.setUseDescender(true);
                         celda.setVerticalAlignment(Element.ALIGN_MIDDLE);
                         tabla.addCell(celda);
                         
                         //13
                         celda = new PdfPCell(new Paragraph(StringHelper.AgregaComas(registro.get("costo_importe").toString()),fuenteCont));
                         celda.setUseAscender(true);
                         celda.setHorizontalAlignment(Element.ALIGN_RIGHT);
                         celda.setUseDescender(true);
                         celda.setVerticalAlignment(Element.ALIGN_MIDDLE);     
                         tabla.addCell(celda);
                         
                         suma_importe_costo = suma_importe_costo + Double.parseDouble(registro.get("costo_importe").toString());
                     }else{
                         if(registro.get("tipo").equals("MP")){
                             //4
                             celda = new PdfPCell(new Paragraph(StringHelper.AgregaComas(registro.get("costo_unitario").toString()),fuenteCont));
                             celda.setUseAscender(true);
                             celda.setHorizontalAlignment(Element.ALIGN_RIGHT);
                             celda.setUseDescender(true);
                             celda.setVerticalAlignment(Element.ALIGN_MIDDLE);
                             tabla.addCell(celda);
                             
                             //13
                             celda = new PdfPCell(new Paragraph(StringHelper.AgregaComas(registro.get("costo_importe").toString()),fuenteCont));
                             celda.setUseAscender(true);
                             celda.setHorizontalAlignment(Element.ALIGN_RIGHT);
                             celda.setUseDescender(true);
                             celda.setVerticalAlignment(Element.ALIGN_MIDDLE);     
                             tabla.addCell(celda);
                             
                             suma_importe_costo = suma_importe_costo + Double.parseDouble(registro.get("costo_importe").toString());
                             
                         }else{
                             //4
                             celda = new PdfPCell(new Paragraph("",fuenteCont));
                             celda.setUseAscender(true);
                             celda.setHorizontalAlignment(Element.ALIGN_LEFT);
                             celda.setUseDescender(true);
                             celda.setVerticalAlignment(Element.ALIGN_MIDDLE);
                             tabla.addCell(celda);

                             //13
                             celda = new PdfPCell(new Paragraph( "",fuenteCont));
                             celda.setUseAscender(true);
                             celda.setHorizontalAlignment(Element.ALIGN_LEFT);
                             celda.setUseDescender(true);
                             celda.setVerticalAlignment(Element.ALIGN_MIDDLE);     
                             tabla.addCell(celda);   
                         }
                     }

                 }else{
                     //4
                     celda = new PdfPCell(new Paragraph("",fuenteCont));
                     celda.setUseAscender(true);
                     celda.setHorizontalAlignment(Element.ALIGN_LEFT);
                     celda.setUseDescender(true);
                     celda.setVerticalAlignment(Element.ALIGN_MIDDLE);
                     tabla.addCell(celda);

                     //13
                     celda = new PdfPCell(new Paragraph( "",fuenteCont));
                     celda.setUseAscender(true);
                     celda.setHorizontalAlignment(Element.ALIGN_LEFT);
                     celda.setUseDescender(true);
                     celda.setVerticalAlignment(Element.ALIGN_MIDDLE);     
                     tabla.addCell(celda);
                 }
                 
                 
                 if(stock.equals("false")){
                    if(String.valueOf(registro.get("id_tipo")).equals("2") || String.valueOf(registro.get("id_tipo")).equals("1")){
                         ArrayList<HashMap<String, String>> lista1 = (ArrayList<HashMap<String, String>>) registro.get("adicionales");
                         
                         for (int k=0;k<lista1.size();k++){
                             HashMap<String,String> registro1 = lista1.get(k);

                             //1           
                             celda = new PdfPCell(new Paragraph(String.valueOf(""+contador),fuenteCont));
                             celda.setUseAscender(true);
                             celda.setHorizontalAlignment(Element.ALIGN_LEFT);
                             celda.setUseDescender(true);
                             celda.setVerticalAlignment(Element.ALIGN_MIDDLE);
                             tabla.addCell(celda);

                             //1           
                             celda = new PdfPCell(new Paragraph(registro1.get("sku"),fuenteCont));
                             celda.setUseAscender(true);
                             celda.setHorizontalAlignment(Element.ALIGN_LEFT);
                             celda.setUseDescender(true);
                             celda.setVerticalAlignment(Element.ALIGN_MIDDLE);
                             tabla.addCell(celda);

                             celda = new PdfPCell(new Paragraph(registro1.get("tipo"),fuenteCont));
                             celda.setUseAscender(true);
                             celda.setHorizontalAlignment(Element.ALIGN_LEFT);
                             celda.setUseDescender(true);
                             celda.setVerticalAlignment(Element.ALIGN_MIDDLE);
                             tabla.addCell(celda);

                             //2
                             celda = new PdfPCell(new Paragraph(registro1.get("descripcion"),fuenteCont));
                             celda.setUseAscender(true);
                             celda.setHorizontalAlignment(Element.ALIGN_LEFT);
                             celda.setUseDescender(true);
                             celda.setVerticalAlignment(Element.ALIGN_MIDDLE);
                             tabla.addCell(celda);
                             //3
                             celda = new PdfPCell(new Paragraph(registro1.get("cantidad"),fuenteCont));
                             celda.setUseAscender(true);
                             celda.setHorizontalAlignment(Element.ALIGN_RIGHT);
                             celda.setUseDescender(true);
                             celda.setVerticalAlignment(Element.ALIGN_MIDDLE);
                             tabla.addCell(celda);
                             
                             if(costear.equals("true")){
                                 //4
                                 celda = new PdfPCell(new Paragraph(StringHelper.AgregaComas(registro1.get("costo_unitario")),fuenteCont));
                                 celda.setUseAscender(true);
                                 celda.setHorizontalAlignment(Element.ALIGN_RIGHT);
                                 celda.setUseDescender(true);
                                 celda.setVerticalAlignment(Element.ALIGN_MIDDLE);
                                 tabla.addCell(celda);
                                 
                                 //13
                                 celda = new PdfPCell(new Paragraph(StringHelper.AgregaComas(registro1.get("costo_importe")),fuenteCont));
                                 celda.setUseAscender(true);
                                 celda.setHorizontalAlignment(Element.ALIGN_RIGHT);
                                 celda.setUseDescender(true);
                                 celda.setVerticalAlignment(Element.ALIGN_MIDDLE);
                                 tabla.addCell(celda);
                                 
                                 suma_importe_costo = suma_importe_costo + Double.parseDouble(registro1.get("costo_importe").toString());
                             }else{
                                 //4
                                 celda = new PdfPCell(new Paragraph("",fuenteCont));
                                 celda.setUseAscender(true);
                                 celda.setHorizontalAlignment(Element.ALIGN_LEFT);
                                 celda.setUseDescender(true);
                                 celda.setVerticalAlignment(Element.ALIGN_MIDDLE);
                                 tabla.addCell(celda);
                                 
                                 //13
                                 celda = new PdfPCell(new Paragraph( "",fuenteCont));
                                 celda.setUseAscender(true);
                                 celda.setHorizontalAlignment(Element.ALIGN_LEFT);
                                 celda.setUseDescender(true);
                                 celda.setVerticalAlignment(Element.ALIGN_MIDDLE);     
                                 tabla.addCell(celda);
                             }
                             
                         }

                     }
                 }
                 
                 Double sum = Double.parseDouble(registro.get("cantidad").toString());
                 
                 sumatoria += sum;
                 
                 contador++;
                
            }
             //columna 1 fil1
            celda = new PdfPCell(new Paragraph("TOTAL ",fuentenegrita));
            celda.setHorizontalAlignment(Element.ALIGN_RIGHT);
            celda.setVerticalAlignment(Element.ALIGN_MIDDLE);
            celda.setBorderWidthBottom(0);
            celda.setBorderWidthTop(0);
            celda.setColspan(4);
            celda.setBorderWidthRight(0);
            celda.setBorderWidthLeft(0);
            tabla.addCell(celda);
            
            celda = new PdfPCell(new Paragraph(String.valueOf(""+StringHelper.AgregaComas(StringHelper.roundDouble(sumatoria, 2))),fuenteCont));
            celda.setUseAscender(true);
            celda.setHorizontalAlignment(Element.ALIGN_RIGHT);
            celda.setUseDescender(true);
            celda.setVerticalAlignment(Element.ALIGN_MIDDLE);
            tabla.addCell(celda);
            
            if(costear.equals("true")){
                celda = new PdfPCell(new Paragraph("",fuentenegrita));
                celda.setHorizontalAlignment(Element.ALIGN_LEFT);
                celda.setVerticalAlignment(Element.ALIGN_MIDDLE);
                celda.setBorderWidthBottom(0);
                celda.setBorderWidthTop(0);
                celda.setBorderWidthRight(0);
                celda.setBorderWidthLeft(0);
                tabla.addCell(celda);

                celda = new PdfPCell(new Paragraph(String.valueOf(""+StringHelper.AgregaComas(StringHelper.roundDouble(suma_importe_costo, 2))),fuenteCont));
                celda.setUseAscender(true);
                celda.setHorizontalAlignment(Element.ALIGN_RIGHT);
                celda.setUseDescender(true);
                celda.setVerticalAlignment(Element.ALIGN_MIDDLE);
                tabla.addCell(celda);
            }else{
                celda = new PdfPCell(new Paragraph("",fuentenegrita));
                celda.setHorizontalAlignment(Element.ALIGN_LEFT);
                celda.setVerticalAlignment(Element.ALIGN_MIDDLE);
                celda.setBorderWidthBottom(0);
                celda.setBorderWidthTop(0);
                celda.setBorderWidthRight(0);
                celda.setBorderWidthLeft(0);
                celda.setColspan(2);
                tabla.addCell(celda);
            }
            
            reporte.add(tabla);
             
             
            /*Codigo para los procedidmientos*/
             String verifica_especificaicones = "0";
             ArrayList<HashMap<String, String>> listaespecif = especificaciones;
             for (int l=0;l<listaespecif.size();l++){
                 HashMap<String,String> registro = listaespecif.get(l);
                 verifica_especificaicones = registro.get("id");
             }
             
             if(!verifica_especificaicones.equals("0")){
                
                float [] tam_tablaxx = {2f,2.5f,2f,7f};
                PdfPTable tablaXX = new PdfPTable(tam_tablaxx);
                PdfPCell celdaXX;
                tablaXX.setKeepTogether(false);
                
                 String id_esp = "";
                 
                 ArrayList<HashMap<String, String>> listaesp = especificaciones;
                 for (int l=0;l<listaesp.size();l++){
                     HashMap<String,String> registro = listaesp.get(l);
                     if(!id_esp.equals(registro.get("id"))){
                         id_esp = registro.get("id");
                         //colspan 4 fil1
                        celdaXX = new PdfPCell(new Paragraph("",fuentenegrita));
                        celdaXX.setHorizontalAlignment(Element.ALIGN_LEFT);
                        celdaXX.setVerticalAlignment(Element.ALIGN_MIDDLE);
                        celdaXX.setBorderWidthBottom(0);
                        celdaXX.setColspan(4);
                        celdaXX.setBorderWidthTop(0);
                        celdaXX.setBorderWidthRight(0);
                        celdaXX.setBorderWidthLeft(0);
                        tablaXX.addCell(celdaXX);
                        
                        //colspan 4 fil1
                        celdaXX = new PdfPCell(new Paragraph("ESPECIFICACIONES PARA: "+registro.get("titulo"),fuentenegrita));
                        celdaXX.setHorizontalAlignment(Element.ALIGN_LEFT);
                        celdaXX.setVerticalAlignment(Element.ALIGN_MIDDLE);
                        celdaXX.setBorderWidthBottom(0);
                        celdaXX.setColspan(4);
                        celdaXX.setBorderWidthTop(0);
                        celdaXX.setBorderWidthRight(0);
                        celdaXX.setBorderWidthLeft(0);
                        tablaXX.addCell(celdaXX);
                     }
                    
                     int cont_datos=0;
                     String valor1="";
                     String valor2="";
                    
                    valor1 = convierte_cadena(registro.get("fineza_inicial"));
                    valor2 = convierte_cadena(registro.get("fineza_final"));
                    
                    if(!valor1.equals("N.A.") && !valor1.equals("N.A.")){
                        //columna 1 fil1
                        celdaXX = new PdfPCell(new Paragraph("FINEZA: ",fuenteCont2));
                        celdaXX.setHorizontalAlignment(Element.ALIGN_LEFT);
                        celdaXX.setVerticalAlignment(Element.ALIGN_MIDDLE);
                        celdaXX.setBorderWidthBottom(0);
                        celdaXX.setBorderWidthTop(0);
                        celdaXX.setBorderWidthRight(0);
                        celdaXX.setBorderWidthLeft(0);
                        tablaXX.addCell(celdaXX);
                        
                        //columna 2 fil1
                        celdaXX = new PdfPCell(new Paragraph("DE "+valor1+" A "+valor2+" Micras",fuentenegrita));
                        celdaXX.setHorizontalAlignment(Element.ALIGN_LEFT);
                        celdaXX.setVerticalAlignment(Element.ALIGN_MIDDLE);
                        celdaXX.setBorderWidthBottom(0);
                        celdaXX.setBorderWidthTop(0);
                        celdaXX.setBorderWidthRight(0);
                        celdaXX.setBorderWidthLeft(0);
                        tablaXX.addCell(celdaXX);
                        
                        cont_datos++;
                    }
                    
                    
                    
                    valor1 = convierte_cadena(registro.get("viscosidads_inicial"));
                    valor2 = convierte_cadena(registro.get("viscosidads_final"));
                    
                    if(!valor1.equals("N.A.") && !valor1.equals("N.A.")){
                        //columna 3 vacio fil1
                        celdaXX = new PdfPCell(new Paragraph("VISCOSIDAD: ",fuenteCont2));
                        celdaXX.setBorderWidthBottom(0);
                        celdaXX.setBorderWidthTop(0);
                        celdaXX.setBorderWidthRight(0);
                        celdaXX.setBorderWidthLeft(0);
                        tablaXX.addCell(celdaXX);
                        
                        //columna 4 fil1
                        celdaXX = new PdfPCell(new Paragraph("DE "+valor1+" A "+valor2+" Segundos",fuentenegrita));
                        celdaXX.setBorderWidthBottom(0);
                        celdaXX.setBorderWidthTop(0);
                        celdaXX.setBorderWidthRight(0);
                        celdaXX.setBorderWidthLeft(0);
                        tablaXX.addCell(celdaXX);
                        
                        cont_datos++;
                    }
                    
                    
                    /*fila 2*/
                    valor1 = convierte_cadena(registro.get("viscosidadku_inicial"));
                    valor2 = convierte_cadena(registro.get("viscosidadku_final"));
                    
                    if(!valor1.equals("N.A.") && !valor1.equals("N.A.")){
                        //columna 1 fil2
                        celdaXX = new PdfPCell(new Paragraph("VISCOSIDAD: ",fuenteCont2));
                        celdaXX.setBorderWidthBottom(0);
                        celdaXX.setBorderWidthTop(0);
                        celdaXX.setBorderWidthRight(0);
                        celdaXX.setBorderWidthLeft(0);
                        tablaXX.addCell(celdaXX);
                        
                        //columna 2 fil2
                        celdaXX = new PdfPCell(new Paragraph("DE "+valor1+" A "+valor2+" KU",fuentenegrita));
                        celdaXX.setBorderWidthBottom(0);
                        celdaXX.setBorderWidthTop(0);
                        celdaXX.setBorderWidthRight(0);
                        celdaXX.setBorderWidthLeft(0);
                        tablaXX.addCell(celdaXX);
                        
                        cont_datos++;
                    }
                    
                    
                    
                    valor1 = convierte_cadena(registro.get("viscosidadcps_inicial"));
                    valor2 = convierte_cadena(registro.get("viscosidadcps_final"));
                    
                    if(!valor1.equals("N.A.") && !valor1.equals("N.A.")){
                        //columna 3 vacio fil2
                        celdaXX = new PdfPCell(new Paragraph("VISCOSIDAD: ",fuenteCont2));
                        celdaXX.setBorderWidthBottom(0);
                        celdaXX.setBorderWidthTop(0);
                        celdaXX.setBorderWidthRight(0);
                        celdaXX.setBorderWidthLeft(0);
                        tablaXX.addCell(celdaXX);

                        //columna 4 fil2
                        celdaXX = new PdfPCell(new Paragraph("DE "+valor1+" A "+valor2+" CPS",fuentenegrita));
                        celdaXX.setBorderWidthBottom(0);
                        celdaXX.setBorderWidthTop(0);
                        celdaXX.setBorderWidthRight(0);
                        celdaXX.setBorderWidthLeft(0);
                        tablaXX.addCell(celdaXX);
                        
                        cont_datos++;
                    }
                    
                    
                    
                    /*fila 3*/
                    
                    valor1 = convierte_cadena(registro.get("densidad_inicial"));
                    valor2 = convierte_cadena(registro.get("densidad_final"));
                    
                    if(!valor1.equals("N.A.") && !valor1.equals("N.A.")){
                        //columna 1 fil3
                        celdaXX = new PdfPCell(new Paragraph("DENSIDAD: ",fuenteCont2));
                        celdaXX.setBorderWidthBottom(0);
                        celdaXX.setBorderWidthTop(0);
                        celdaXX.setBorderWidthRight(0);
                        celdaXX.setBorderWidthLeft(0);
                        tablaXX.addCell(celdaXX);

                        //columna 2 fil3
                        celdaXX = new PdfPCell(new Paragraph("DE "+valor1+" A "+valor2+" Kg\\/L",fuentenegrita));
                        celdaXX.setBorderWidthBottom(0);
                        celdaXX.setBorderWidthTop(0);
                        celdaXX.setBorderWidthRight(0);
                        celdaXX.setBorderWidthLeft(0);
                        tablaXX.addCell(celdaXX);
                        
                        cont_datos++;
                    }


                    valor1 = convierte_cadena(registro.get("volatiles_inicial"));
                    valor2 = convierte_cadena(registro.get("volatiles_final"));
                    
                    if(!valor1.equals("N.A.") && !valor1.equals("N.A.")){
                        //columna 3 vacio fil3
                        celdaXX = new PdfPCell(new Paragraph("% No VOLATILES: ",fuenteCont2));
                        celdaXX.setBorderWidthBottom(0);
                        celdaXX.setBorderWidthTop(0);
                        celdaXX.setBorderWidthRight(0);
                        celdaXX.setBorderWidthLeft(0);
                        tablaXX.addCell(celdaXX);

                        //columna 4 vacio fil3
                        celdaXX = new PdfPCell(new Paragraph("DE "+valor1+" A "+valor2+" %",fuentenegrita));
                        celdaXX.setBorderWidthBottom(0);
                        celdaXX.setBorderWidthTop(0);
                        celdaXX.setBorderWidthRight(0);
                        celdaXX.setBorderWidthLeft(0);
                        tablaXX.addCell(celdaXX);
                        
                        cont_datos++;
                    }

                    
                    
                    /*fila 4*/
                    
                    valor1 = convierte_cadena(registro.get("hidrogeno_inicial"));
                    valor2 = convierte_cadena(registro.get("hidrogeno_final"));
                    
                    if(!valor1.equals("N.A.") && !valor1.equals("N.A.")){
                        //columna 1 fil4
                        celdaXX = new PdfPCell(new Paragraph("pH: ",fuenteCont2));
                        celdaXX.setBorderWidthBottom(0);
                        celdaXX.setBorderWidthTop(0);
                        celdaXX.setBorderWidthRight(0);
                        celdaXX.setBorderWidthLeft(0);
                        tablaXX.addCell(celdaXX);

                        //columna 2 fil4
                        celdaXX = new PdfPCell(new Paragraph("DE "+valor1+" A "+valor2+" ",fuentenegrita));
                        celdaXX.setBorderWidthBottom(0);
                        celdaXX.setBorderWidthTop(0);
                        celdaXX.setBorderWidthRight(0);
                        celdaXX.setBorderWidthLeft(0);
                        tablaXX.addCell(celdaXX);
                        
                        cont_datos++;
                    }


                    valor1 = convierte_cadena(registro.get("cubriente_inicial"));
                    valor2 = convierte_cadena(registro.get("cubriente_final"));
                    
                    if(!valor1.equals("N.A.") && !valor1.equals("N.A.")){
                        //columna 3 vacio fil4
                        celdaXX = new PdfPCell(new Paragraph("CUBRIENTE: ",fuenteCont2));
                        celdaXX.setBorderWidthBottom(0);
                        celdaXX.setBorderWidthTop(0);
                        celdaXX.setBorderWidthRight(0);
                        celdaXX.setBorderWidthLeft(0);
                        tablaXX.addCell(celdaXX);

                        //columna 4 vacio fil4
                        celdaXX = new PdfPCell(new Paragraph("DE "+valor1+" A "+valor2+" %",fuentenegrita));
                        celdaXX.setBorderWidthBottom(0);
                        celdaXX.setBorderWidthTop(0);
                        celdaXX.setBorderWidthRight(0);
                        celdaXX.setBorderWidthLeft(0);
                        tablaXX.addCell(celdaXX);
                        
                        cont_datos++;
                    }


                    /*fila 5*/
                    
                    valor1 = convierte_cadena(registro.get("tono_inicial"));
                    valor2 = convierte_cadena(registro.get("tono_final"));
                    
                    if(!valor1.equals("N.A.") && !valor1.equals("N.A.")){
                        //columna 1 fil5
                        celdaXX = new PdfPCell(new Paragraph("TONO: ",fuenteCont2));
                        celdaXX.setBorderWidthBottom(0);
                        celdaXX.setBorderWidthTop(0);
                        celdaXX.setBorderWidthRight(0);
                        celdaXX.setBorderWidthLeft(0);
                        tablaXX.addCell(celdaXX);

                        //columna 2 fil5
                        celdaXX = new PdfPCell(new Paragraph("DE "+valor1+" A "+valor2+" ",fuentenegrita));
                        celdaXX.setBorderWidthBottom(0);
                        celdaXX.setBorderWidthTop(0);
                        celdaXX.setBorderWidthRight(0);
                        celdaXX.setBorderWidthLeft(0);
                        tablaXX.addCell(celdaXX);
                        
                        cont_datos++;
                    }


                    valor1 = convierte_cadena(registro.get("brillo_inicial"));
                    valor2 = convierte_cadena(registro.get("brillo_final"));
                    
                    if(!valor1.equals("N.A.") && !valor1.equals("N.A.")){
                        //columna 3 fil5
                        celdaXX = new PdfPCell(new Paragraph("BRILLO: ",fuenteCont2));
                        celdaXX.setBorderWidthBottom(0);
                        celdaXX.setBorderWidthTop(0);
                        celdaXX.setBorderWidthRight(0);
                        celdaXX.setBorderWidthLeft(0);
                        tablaXX.addCell(celdaXX);

                        //columna 4 fil5
                        celdaXX = new PdfPCell(new Paragraph("DE "+valor1+" A "+valor2+" Unid. de brillo",fuentenegrita));
                        celdaXX.setBorderWidthBottom(0);
                        celdaXX.setBorderWidthTop(0);
                        celdaXX.setBorderWidthRight(0);
                        celdaXX.setBorderWidthLeft(0);
                        tablaXX.addCell(celdaXX);
                        
                        cont_datos++;
                    }

                    
                    /*fila 6*/
                    
                    valor1 = registro.get("dureza_inicial").toString();
                    valor2 = registro.get("dureza_final").toString();
                    
                    if(!valor1.equals("N.A.") && !valor1.equals("N.A.")){
                        //columna 1 fil6
                        celdaXX = new PdfPCell(new Paragraph("DUREZA: ",fuenteCont2));
                        celdaXX.setBorderWidthBottom(0);
                        celdaXX.setBorderWidthTop(0);
                        celdaXX.setBorderWidthRight(0);
                        celdaXX.setBorderWidthLeft(0);
                        tablaXX.addCell(celdaXX);

                        //columna 2 fil6
                        celdaXX = new PdfPCell(new Paragraph("DE "+valor1+" A "+valor2+" Letras",fuentenegrita));
                        celdaXX.setBorderWidthBottom(0);
                        celdaXX.setBorderWidthTop(0);
                        celdaXX.setBorderWidthRight(0);
                        celdaXX.setBorderWidthLeft(0);
                        tablaXX.addCell(celdaXX);
                        
                        cont_datos++;
                    }
                    
                    
                    valor1 = convierte_cadena(registro.get("adherencia_inicial"));
                    valor2 = convierte_cadena(registro.get("adherencia_final"));
                    
                    if(!valor1.equals("N.A.") && !valor1.equals("N.A.")){
                        //columna 3 fil6
                        celdaXX = new PdfPCell(new Paragraph("ADHERENCIA: ",fuenteCont2));
                        celdaXX.setBorderWidthBottom(0);
                        celdaXX.setBorderWidthTop(0);
                        celdaXX.setBorderWidthRight(0);
                        celdaXX.setBorderWidthLeft(0);
                        tablaXX.addCell(celdaXX);

                        //columna 4 fil6
                        celdaXX = new PdfPCell(new Paragraph("DE "+valor1+" A "+valor2+" %",fuentenegrita));
                        celdaXX.setBorderWidthBottom(0);
                        celdaXX.setBorderWidthTop(0);
                        celdaXX.setBorderWidthRight(0);
                        celdaXX.setBorderWidthLeft(0);
                        tablaXX.addCell(celdaXX);
                        
                        cont_datos++;
                    }
                    
                    //Verificar si el contador de datos es impar
                    if ((cont_datos%2) > 0){
                        //Si es impar hay que agregar dos celdas vacias para acompletar la tabla
                        celdaXX = new PdfPCell(new Paragraph("",fuenteCont2));
                        celdaXX.setBorderWidthBottom(0);
                        celdaXX.setBorderWidthTop(0);
                        celdaXX.setBorderWidthRight(0);
                        celdaXX.setBorderWidthLeft(0);
                        tablaXX.addCell(celdaXX);
                        
                        celdaXX = new PdfPCell(new Paragraph("",fuentenegrita));
                        celdaXX.setBorderWidthBottom(0);
                        celdaXX.setBorderWidthTop(0);
                        celdaXX.setBorderWidthRight(0);
                        celdaXX.setBorderWidthLeft(0);
                        tablaXX.addCell(celdaXX);
                    }
                    
                 }
                 
                 reporte.add(tablaXX);
             }
            
             
             
             //crear nueva pagina, esto permite que lo que sigue se pase a  otra pagina nueva
             reporte.newPage();
             
             
             /*Codigo para los procedidmientos*/
             String verifica_procedimiento = "0";
             ArrayList<HashMap<String, String>> listaproc = lista_procedimiento;
             for (int l=0;l<listaproc.size();l++){
                 HashMap<String,String> registro = listaproc.get(l);
                 verifica_procedimiento = registro.get("id");
             }
             
             if(!verifica_procedimiento.equals("0")){
                 
                 float [] widthsProc = {0.8f,2.5f,10.3F,0.8F};//Tamaño de las Columnas.
                 PdfPTable tablaProc = new PdfPTable(widthsProc);
                 PdfPCell celdaProc;
                 tablaProc.setKeepTogether(false);
                 tablaProc.setHeaderRows(2);
                 
                 
                 String id_proced = "";
                 ArrayList<HashMap<String, String>> listaproced = lista_procedimiento;
                 for (int l=0;l<listaproced.size();l++){
                     HashMap<String,String> registro = listaproced.get(l);
                     
                    if(!id_proced.equals(registro.get("pro_subp_prod_id"))){
                        id_proced = registro.get("pro_subp_prod_id");
                         //colspan 4 fil1
                         celdaProc = new PdfPCell(new Paragraph("",fuentenegrita));
                         celdaProc.setHorizontalAlignment(Element.ALIGN_LEFT);
                         celdaProc.setVerticalAlignment(Element.ALIGN_MIDDLE);
                         celdaProc.setBorderWidthBottom(0);
                         celdaProc.setColspan(4);
                         celdaProc.setBorderWidthTop(0);
                         celdaProc.setBorderWidthRight(0);
                         celdaProc.setBorderWidthLeft(0);
                         tablaProc.addCell(celdaProc);
                         
                         //colspan 4 fil1
                         celdaProc = new PdfPCell(new Paragraph("PROCEDIMIENTO PARA : "+registro.get("titulo"),fuentenegrita));
                         celdaProc.setHorizontalAlignment(Element.ALIGN_LEFT);
                         celdaProc.setVerticalAlignment(Element.ALIGN_MIDDLE);
                         celdaProc.setBorderWidthBottom(0);
                         celdaProc.setColspan(4);
                         celdaProc.setBorderWidthTop(0);
                         celdaProc.setBorderWidthRight(0);
                         celdaProc.setBorderWidthLeft(0);
                         tablaProc.addCell(celdaProc);

                         //1
                         celdaProc = new PdfPCell(new Paragraph("NUMERO",smallBoldFont));
                         celdaProc.setUseAscender(true);
                         celdaProc.setHorizontalAlignment(Element.ALIGN_CENTER);
                         celdaProc.setUseDescender(true);
                         celdaProc.setColspan(2);
                         celdaProc.setVerticalAlignment(Element.ALIGN_MIDDLE);
                         celdaProc.setBackgroundColor(BaseColor.BLACK);
                         tablaProc.addCell(celdaProc);

                         //AQUI VA EL ENCABEZADO 2 DEL DOCUMENTO
                         //1
                         celdaProc = new PdfPCell(new Paragraph("DESCRIPCION",smallBoldFont));
                         celdaProc.setUseAscender(true);
                         celdaProc.setHorizontalAlignment(Element.ALIGN_CENTER);
                         celdaProc.setUseDescender(true);
                         celdaProc.setColspan(2);
                         celdaProc.setVerticalAlignment(Element.ALIGN_MIDDLE);
                         celdaProc.setBackgroundColor(BaseColor.BLACK);
                         tablaProc.addCell(celdaProc);
                     }
                     
                     
                     celdaProc = new PdfPCell(new Paragraph(registro.get("posicion"),fuenteCont));
                     celdaProc.setUseAscender(true);
                     celdaProc.setHorizontalAlignment(Element.ALIGN_LEFT);
                     celdaProc.setUseDescender(true);
                     celdaProc.setColspan(2);
                     celdaProc.setVerticalAlignment(Element.ALIGN_MIDDLE);
                     tablaProc.addCell(celdaProc);
                     
                     celdaProc = new PdfPCell(new Paragraph(StringHelper.replaceStringMacro("macrocoma", ",", registro.get("descripcion")),fuenteCont));
                     celdaProc.setUseAscender(true);
                     celdaProc.setHorizontalAlignment(Element.ALIGN_LEFT);
                     celdaProc.setUseDescender(true);
                     celdaProc.setColspan(2);
                     celdaProc.setVerticalAlignment(Element.ALIGN_MIDDLE);     
                     tablaProc.addCell(celdaProc);
                     
                 }
                 
                 reporte.add(tablaProc);
             }
        }
        
        catch (Exception e){
             System.out.println(e.toString());
             }
        reporte.close();
    }
    
    
    String convierte_cadena(String cadena){
        String cadena_retorno = "";
        Double num = Double.parseDouble(cadena);
        
        if(num >= 0){
            cadena_retorno = cadena;
        }else{
            cadena_retorno = "N.A.";
        }
        
        return cadena_retorno;
    }
    
     static class HeaderFooter extends PdfPageEventHelper {
        protected PdfTemplate total;       
        protected BaseFont helv;  
        protected PdfContentByte cb;  
        Font largeBoldFont = new Font(Font.FontFamily.HELVETICA,10,Font.BOLD,BaseColor.BLACK);
        Font largeFont = new Font(Font.FontFamily.HELVETICA,10,Font.NORMAL,BaseColor.BLACK);
        Font smallFont = new Font(Font.FontFamily.HELVETICA,7,Font.NORMAL,BaseColor.BLACK);
        
        //ESTAS SON VARIABLES PRIVADAS DE LA CLASE, SE LE ASIGNA VALOR EN EL CONSTRUCTOR SON SETER
        private String empresa;
        private String periodo;
        private String titulo_reporte;
        private String codigo1;
        private String codigo2;
        
		
        //ESTOS  SON LOS GETER Y SETTER DE LAS VARIABLES PRIVADAS DE LA CLASE
        public String getCodigo1() {
            return codigo1;
        }

        public void setCodigo1(String codigo1) {
            this.codigo1 = codigo1;
        }

        public String getCodigo2() {
            return codigo2;
        }

        public void setCodigo2(String codigo2) {
            this.codigo2 = codigo2;
        }

        public String getTitulo_reporte() {
            return titulo_reporte;
        }

        public void setTitulo_reporte(String titulo_reporte) {
            this.titulo_reporte = titulo_reporte;
        }
        
        public String getEmpresa() {
            return empresa;
        }
        
        public void setEmpresa(String empresa) {
            this.empresa = empresa;
        }

        public String getPeriodo() {
            return periodo;
        }

        public void setPeriodo(String periodo) {
            this.periodo = periodo;
        }
        
        //ESTE ES EL CONSTRUCTOR DE LA CLASE  QUE RECIBE LOS PARAMETROS
        HeaderFooter( HashMap<String, String> datos ){
            this.setEmpresa(datos.get("empresa"));
            this.setTitulo_reporte(datos.get("titulo_reporte"));
            this.setPeriodo(datos.get("periodo"));
            this.setCodigo1(datos.get("codigo1"));
            this.setCodigo2(datos.get("codigo2"));
        }
        
        
        /*Añadimos una tabla con  una imagen del logo de megestiono y creamos la fuente para el documento, la imagen esta escalada para que no se muestre pixelada*/   
        @Override
        public void onOpenDocument(PdfWriter writer, Document document) {
            try {
                total = writer.getDirectContent().createTemplate(100, 100);  
                //public Rectangle(int x, int y, int width, int height)
                total.setBoundingBox(new Rectangle(-20, -20, 100, 100));
                total.fill();
                helv = BaseFont.createFont("Helvetica", BaseFont.WINANSI, false);
            }
            catch(Exception e) {
                throw new ExceptionConverter(e);
            }
        }
        
        /*añadimos pie de página, borde y más propiedades*/
        @Override
        public void onEndPage(PdfWriter writer, Document document) {
            ColumnText.showTextAligned(writer.getDirectContent(),Element.ALIGN_CENTER, new Phrase(this.getEmpresa(),largeBoldFont),document.getPageSize().getWidth()/2, document.getPageSize().getTop() -25, 0);
            ColumnText.showTextAligned(writer.getDirectContent(),Element.ALIGN_CENTER, new Phrase(this.getTitulo_reporte(),largeBoldFont),document.getPageSize().getWidth()/2, document.getPageSize().getTop()-38, 0);
            
            SimpleDateFormat formato = new SimpleDateFormat("'Generado el' d 'de' MMMMM 'del' yyyy 'a las' HH:mm:ss 'hrs.'");
            String impreso_en = formato.format(new Date());
            
            ColumnText.showTextAligned(writer.getDirectContent(),Element.ALIGN_CENTER, new Phrase(impreso_en,largeFont),document.getPageSize().getWidth()/2, document.getPageSize().getTop()-50, 0);
            
            cb = writer.getDirectContent();
            float textBase = document.bottom() - 35;
            
            //texto inferior izquieda pie de pagina
            String text_left = this.getCodigo1();
            float text_left_Size = helv.getWidthPoint(text_left, 7);
            cb.beginText();
            cb.setFontAndSize(helv, 7);  
            cb.setTextMatrix(document.left()+85, textBase );  //definir la posicion de text
            cb.showText(text_left);
            cb.endText();
            
            
            //texto centro pie de pagina
            String text_center ="Página " + writer.getPageNumber() + " de ";
            float text_center_Size = helv.getWidthPoint(text_center, 7);
            float pos_text_center = (document.getPageSize().getWidth()/2)-(text_center_Size/2);
            float adjust = text_center_Size + 3; 
            cb.beginText();  
            cb.setFontAndSize(helv, 7);  
            cb.setTextMatrix(pos_text_center, textBase );  //definir la posicion de text
            cb.showText(text_center);
            cb.endText();
            cb.addTemplate(total, pos_text_center + adjust, textBase);
            
            
            //texto inferior derecha pie de pagina
            String text_right = this.getCodigo2();
            float textRightSize = helv.getWidthPoint(text_right, 7);
            float pos_text_right = document.getPageSize().getWidth()-textRightSize - 40;
            cb.beginText();
            cb.setFontAndSize(helv, 7);
            cb.setTextMatrix(pos_text_right, textBase);
            cb.showText(text_right);
            cb.endText();
            //cb.restoreState();  
        }
        
        /*aqui escrimos ls paginas totales, para que nos salga de pie de pagina Pagina x de y*/
        @Override
        public void onCloseDocument(PdfWriter writer, Document document) {
          total.beginText();
          total.setFontAndSize(helv, 7);  
          total.setTextMatrix(0,0);                                           
          total.showText(String.valueOf(writer.getPageNumber() -1));  
          total.endText();  
        }
   }//termina clase HeaderFooter
     
    
}
