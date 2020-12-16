/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.agnux.kemikal.reportes;
import com.agnux.common.helpers.StringHelper;
import com.agnux.common.helpers.TimeHelper;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.HashMap;

/**
 *
 * @author Ezequiel
 */
public class PdfReporteRutas {
    
    public PdfReporteRutas(HashMap<String, String> datosEncabezadoPie,String fileout,ArrayList<HashMap<String, String>> lista,HashMap<String, String> datos) throws FileNotFoundException, DocumentException {
        Font smallBoldFont = new Font(Font.getFamily("ARIAL"),7,Font.NORMAL,BaseColor.WHITE);
        Font fuenteCont = new Font(Font.getFamily("ARIAL"),7,Font.NORMAL,BaseColor.BLACK);
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
        Document reporte = new Document(PageSize.LETTER.rotate(),-50,-50,60,40);  
        PdfWriter writer = PdfWriter.getInstance(reporte, new FileOutputStream(fileout));
        writer.setPageEvent(event);
        
        
        try {            
             reporte.open();
             
            float [] tam_tablax = {1.4f,0.4f,3.5f,0.3f,1.5f,3,0.5f,1.5f,3};
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
             
            //columna 4 vacio fil1
            celdaX = new PdfPCell(new Paragraph("",fuentenegrita));
            celdaX.setHorizontalAlignment(Element.ALIGN_LEFT);
            celdaX.setBorderWidthBottom(0);
            celdaX.setBorderWidthTop(0);
            celdaX.setBorderWidthRight(0);
            celdaX.setBorderWidthLeft(0);
            tablaX.addCell(celdaX);
             
            //columna 5 fil1  
            celdaX = new PdfPCell(new Paragraph("",fuentenegrita));
            celdaX.setHorizontalAlignment(Element.ALIGN_LEFT);
            celdaX.setBorderWidthBottom(0);
            celdaX.setBorderWidthTop(0);
            celdaX.setBorderWidthRight(0);
            celdaX.setBorderWidthLeft(0);
            tablaX.addCell(celdaX);
             
            //columna 6 fil1 fecha
            celdaX = new PdfPCell(new Paragraph("FECHA        "+fecha_ruta,fuentenegrita));
            celdaX.setBorderWidthBottom(1);
            celdaX.setBorderWidthTop(0);
            celdaX.setBorderWidthRight(0);
            celdaX.setBorderWidthLeft(0);
            tablaX.addCell(celdaX);
             
            //columna 7 vacio fil1
            celdaX = new PdfPCell(new Paragraph("",fuentenegrita));
            celdaX.setBorderWidthBottom(0);
            celdaX.setBorderWidthTop(0);
            celdaX.setBorderWidthRight(0);
            celdaX.setBorderWidthLeft(0);
            tablaX.addCell(celdaX);
             
            //columna 8 fil1
            celdaX = new PdfPCell(new Paragraph("",fuentenegrita));
            celdaX.setHorizontalAlignment(Element.ALIGN_LEFT);
            celdaX.setBorderWidthBottom(1);
            celdaX.setBorderWidthTop(0);
            celdaX.setBorderWidthRight(0);
            celdaX.setBorderWidthLeft(0);
            tablaX.addCell(celdaX);
             
            //columna 9 fil1
            celdaX = new PdfPCell(new Paragraph("R  U  T  A       "+datos.get("folio"),fuentenegrita));
            celdaX.setBorderWidthBottom(1);
            celdaX.setBorderWidthTop(0);
            celdaX.setBorderWidthRight(0);
            celdaX.setBorderWidthLeft(0);
            tablaX.addCell(celdaX);
             
            
            
            
            //columna 1 fil2
            celdaX = new PdfPCell(new Paragraph("Nombre",fuenteCont2));
            celdaX.setHorizontalAlignment(Element.ALIGN_LEFT);
            celdaX.setVerticalAlignment(Element.ALIGN_MIDDLE);
            celdaX.setBorderWidthBottom(0);
            celdaX.setBorderWidthTop(0);
            celdaX.setBorderWidthRight(0);
            celdaX.setBorderWidthLeft(0);
            tablaX.addCell(celdaX);
             
            //columna 2 fil2
            celdaX = new PdfPCell(new Paragraph(datos.get("clave_chofer"),fuenteCont2));
            celdaX.setHorizontalAlignment(Element.ALIGN_LEFT);
            celdaX.setVerticalAlignment(Element.ALIGN_MIDDLE);
            celdaX.setBorderWidthBottom(1);
            celdaX.setBorderWidthTop(0);
            celdaX.setBorderWidthRight(0);
            celdaX.setBorderWidthLeft(0);
            tablaX.addCell(celdaX);
             
            //columna 3 fil2
            celdaX = new PdfPCell(new Paragraph(datos.get("nombre_chofer"),fuenteCont2));
            celdaX.setHorizontalAlignment(Element.ALIGN_LEFT);
            celdaX.setVerticalAlignment(Element.ALIGN_MIDDLE);
            celdaX.setBorderWidthBottom(1);
            celdaX.setBorderWidthTop(0);
            celdaX.setBorderWidthRight(0);
            celdaX.setBorderWidthLeft(0);
            tablaX.addCell(celdaX);
             
            //columna 4 vacio fil2
            celdaX = new PdfPCell(new Paragraph("",fuenteCont2));
            celdaX.setHorizontalAlignment(Element.ALIGN_LEFT);
            celdaX.setBorderWidthBottom(0);
            celdaX.setBorderWidthTop(0);
            celdaX.setBorderWidthRight(0);
            celdaX.setBorderWidthLeft(0);
            tablaX.addCell(celdaX);
             
            //columna 5 fil2
            celdaX = new PdfPCell(new Paragraph("Tambores",fuenteCont2));
            celdaX.setHorizontalAlignment(Element.ALIGN_LEFT);
            celdaX.setBorderWidthBottom(0);
            celdaX.setBorderWidthTop(0);
            celdaX.setBorderWidthRight(0);
            celdaX.setBorderWidthLeft(0);
            tablaX.addCell(celdaX);
             
            //columna 6 fil2
            celdaX = new PdfPCell(new Paragraph("",fuenteCont2));
            celdaX.setBorderWidthBottom(1);
            celdaX.setBorderWidthTop(0);
            celdaX.setBorderWidthRight(0);
            celdaX.setBorderWidthLeft(0);
            tablaX.addCell(celdaX);
             
            //columna 7 vacio fil2
            celdaX = new PdfPCell(new Paragraph("",fuenteCont2));
            celdaX.setBorderWidthBottom(0);
            celdaX.setBorderWidthTop(0);
            celdaX.setBorderWidthRight(0);
            celdaX.setBorderWidthLeft(0);
            tablaX.addCell(celdaX);
             
            //columna 8 fil2
            celdaX = new PdfPCell(new Paragraph("Tambores",fuenteCont2));
            celdaX.setHorizontalAlignment(Element.ALIGN_LEFT);
            celdaX.setBorderWidthBottom(0);
            celdaX.setBorderWidthTop(0);
            celdaX.setBorderWidthRight(0);
            celdaX.setBorderWidthLeft(0);
            tablaX.addCell(celdaX);
             
            //columna 9 fil2
            celdaX = new PdfPCell(new Paragraph("",fuenteCont2));
            celdaX.setBorderWidthBottom(1);
            celdaX.setBorderWidthTop(0);
            celdaX.setBorderWidthRight(0);
            celdaX.setBorderWidthLeft(0);
            tablaX.addCell(celdaX);
             
            
            //columna 1 fil3
            celdaX = new PdfPCell(new Paragraph("Vehiculo",fuenteCont2));
            celdaX.setHorizontalAlignment(Element.ALIGN_LEFT);
            celdaX.setVerticalAlignment(Element.ALIGN_MIDDLE);
            celdaX.setBorderWidthBottom(0);
            celdaX.setBorderWidthTop(0);
            celdaX.setBorderWidthRight(0);
            celdaX.setBorderWidthLeft(0);
            tablaX.addCell(celdaX);
             
            //columna 2 fil3
            celdaX = new PdfPCell(new Paragraph(datos.get("clave_vehiculo"),fuenteCont2));
            celdaX.setHorizontalAlignment(Element.ALIGN_LEFT);
            celdaX.setVerticalAlignment(Element.ALIGN_MIDDLE);
            celdaX.setBorderWidthBottom(1);
            celdaX.setBorderWidthTop(0);
            celdaX.setBorderWidthRight(0);
            celdaX.setBorderWidthLeft(0);
            tablaX.addCell(celdaX);
             
            //columna 3 fil3
            celdaX = new PdfPCell(new Paragraph(datos.get("marca_vehiculo"),fuenteCont2));
            celdaX.setHorizontalAlignment(Element.ALIGN_LEFT);
            celdaX.setVerticalAlignment(Element.ALIGN_MIDDLE);
            celdaX.setBorderWidthBottom(1);
            celdaX.setBorderWidthTop(0);
            celdaX.setBorderWidthRight(0);
            celdaX.setBorderWidthLeft(0);
            tablaX.addCell(celdaX);
             
            //columna 4 vacio fil3
            celdaX = new PdfPCell(new Paragraph("",fuenteCont2));
            celdaX.setHorizontalAlignment(Element.ALIGN_LEFT);
            celdaX.setBorderWidthBottom(0);
            celdaX.setBorderWidthTop(0);
            celdaX.setBorderWidthRight(0);
            celdaX.setBorderWidthLeft(0);
            tablaX.addCell(celdaX);
             
            //columna 5 fil3
            celdaX = new PdfPCell(new Paragraph("Cub/Porron",fuenteCont2));
            celdaX.setHorizontalAlignment(Element.ALIGN_LEFT);
            celdaX.setBorderWidthBottom(0);
            celdaX.setBorderWidthTop(0);
            celdaX.setBorderWidthRight(0);
            celdaX.setBorderWidthLeft(0);
            tablaX.addCell(celdaX);
             
            //columna 6 fil3
            celdaX = new PdfPCell(new Paragraph("",fuenteCont2));
            celdaX.setBorderWidthBottom(1);
            celdaX.setBorderWidthTop(0);
            celdaX.setBorderWidthRight(0);
            celdaX.setBorderWidthLeft(0);
            tablaX.addCell(celdaX);
             
            //columna 7 vacio fil3
            celdaX = new PdfPCell(new Paragraph("",fuenteCont2));
            celdaX.setBorderWidthBottom(0);
            celdaX.setBorderWidthTop(0);
            celdaX.setBorderWidthRight(0);
            celdaX.setBorderWidthLeft(0);
            tablaX.addCell(celdaX);
             
            //columna 8 fil3
            celdaX = new PdfPCell(new Paragraph("Cub/Porron",fuenteCont2));
            celdaX.setHorizontalAlignment(Element.ALIGN_LEFT);
            celdaX.setBorderWidthBottom(0);
            celdaX.setBorderWidthTop(0);
            celdaX.setBorderWidthRight(0);
            celdaX.setBorderWidthLeft(0);
            tablaX.addCell(celdaX);
             
            //columna 9 fil3
            celdaX = new PdfPCell(new Paragraph("",fuenteCont2));
            celdaX.setBorderWidthBottom(1);
            celdaX.setBorderWidthTop(0);
            celdaX.setBorderWidthRight(0);
            celdaX.setBorderWidthLeft(0);
            tablaX.addCell(celdaX);
            
            
            
            
            //columna 1 fil4
            celdaX = new PdfPCell(new Paragraph("Hora Salida",fuenteCont2));
            celdaX.setHorizontalAlignment(Element.ALIGN_LEFT);
            celdaX.setVerticalAlignment(Element.ALIGN_MIDDLE);
            celdaX.setBorderWidthBottom(0);
            celdaX.setBorderWidthTop(0);
            celdaX.setBorderWidthRight(0);
            celdaX.setBorderWidthLeft(0);
            tablaX.addCell(celdaX);
             
            //columna 2 fil4
            celdaX = new PdfPCell(new Paragraph("",fuenteCont2));
            celdaX.setBorderWidthBottom(1);
            celdaX.setBorderWidthTop(0);
            celdaX.setBorderWidthRight(0);
            celdaX.setBorderWidthLeft(0);
            tablaX.addCell(celdaX);
            
            //columna 3 fil4
            celdaX = new PdfPCell(new Paragraph("",fuenteCont2));
            celdaX.setBorderWidthBottom(1);
            celdaX.setBorderWidthTop(0);
            celdaX.setBorderWidthRight(0);
            celdaX.setBorderWidthLeft(0);
            tablaX.addCell(celdaX);
            
            //columna 4 vacio fil4
            celdaX = new PdfPCell(new Paragraph("",fuenteCont2));
            celdaX.setHorizontalAlignment(Element.ALIGN_LEFT);
            celdaX.setBorderWidthBottom(0);
            celdaX.setBorderWidthTop(0);
            celdaX.setBorderWidthRight(0);
            celdaX.setBorderWidthLeft(0);
            tablaX.addCell(celdaX);
             
            //columna 5 fil4
            celdaX = new PdfPCell(new Paragraph("Sacos",fuenteCont2));
            celdaX.setHorizontalAlignment(Element.ALIGN_LEFT);
            celdaX.setBorderWidthBottom(0);
            celdaX.setBorderWidthTop(0);
            celdaX.setBorderWidthRight(0);
            celdaX.setBorderWidthLeft(0);
            tablaX.addCell(celdaX);
             
            //columna 6 fil4
            celdaX = new PdfPCell(new Paragraph("",fuenteCont2));
            celdaX.setBorderWidthBottom(1);
            celdaX.setBorderWidthTop(0);
            celdaX.setBorderWidthRight(0);
            celdaX.setBorderWidthLeft(0);
            tablaX.addCell(celdaX);
            
            //columna 7 vacio fil4
            celdaX = new PdfPCell(new Paragraph("",fuenteCont2));
            celdaX.setBorderWidthBottom(0);
            celdaX.setBorderWidthTop(0);
            celdaX.setBorderWidthRight(0);
            celdaX.setBorderWidthLeft(0);
            tablaX.addCell(celdaX);
             
            //columna 8 fil4
            celdaX = new PdfPCell(new Paragraph("Sacos",fuenteCont2));
            celdaX.setHorizontalAlignment(Element.ALIGN_LEFT);
            celdaX.setBorderWidthBottom(0);
            celdaX.setBorderWidthTop(0);
            celdaX.setBorderWidthRight(0);
            celdaX.setBorderWidthLeft(0);
            tablaX.addCell(celdaX);
             
            //columna 9 fil4
            celdaX = new PdfPCell(new Paragraph("",fuenteCont2));
            celdaX.setBorderWidthBottom(1);
            celdaX.setBorderWidthTop(0);
            celdaX.setBorderWidthRight(0);
            celdaX.setBorderWidthLeft(0);
            tablaX.addCell(celdaX);
            
            
            //columna 1 fil5
            celdaX = new PdfPCell(new Paragraph("Hora Llegada",fuenteCont2));
            celdaX.setHorizontalAlignment(Element.ALIGN_LEFT);
            celdaX.setVerticalAlignment(Element.ALIGN_MIDDLE);
            celdaX.setBorderWidthBottom(0);
            celdaX.setBorderWidthTop(0);
            celdaX.setBorderWidthRight(0);
            celdaX.setBorderWidthLeft(0);
            tablaX.addCell(celdaX);
             
            //columna 2 fil5
            celdaX = new PdfPCell(new Paragraph("",fuenteCont2));
            celdaX.setBorderWidthBottom(1);
            celdaX.setBorderWidthTop(0);
            celdaX.setBorderWidthRight(0);
            celdaX.setBorderWidthLeft(0);
            tablaX.addCell(celdaX);
             
            //columna 3 fil5
            celdaX = new PdfPCell(new Paragraph("",fuenteCont2));
            celdaX.setBorderWidthBottom(1);
            celdaX.setBorderWidthTop(0);
            celdaX.setBorderWidthRight(0);
            celdaX.setBorderWidthLeft(0);
            tablaX.addCell(celdaX);
             
            //columna 4 vacio fil5
            celdaX = new PdfPCell(new Paragraph("",fuenteCont2));
            celdaX.setHorizontalAlignment(Element.ALIGN_LEFT);
            celdaX.setBorderWidthBottom(0);
            celdaX.setBorderWidthTop(0);
            celdaX.setBorderWidthRight(0);
            celdaX.setBorderWidthLeft(0);
            tablaX.addCell(celdaX);
             
            //columna 5 fil5
            celdaX = new PdfPCell(new Paragraph("Contenedor",fuenteCont2));
            celdaX.setHorizontalAlignment(Element.ALIGN_LEFT);
            celdaX.setBorderWidthBottom(0);
            celdaX.setBorderWidthTop(0);
            celdaX.setBorderWidthRight(0);
            celdaX.setBorderWidthLeft(0);
            tablaX.addCell(celdaX);
             
            //columna 6 fil5
            celdaX = new PdfPCell(new Paragraph("",fuenteCont2));
            celdaX.setBorderWidthBottom(1);
            celdaX.setBorderWidthTop(0);
            celdaX.setBorderWidthRight(0);
            celdaX.setBorderWidthLeft(0);
            tablaX.addCell(celdaX);
            
            //columna 7 vacio fil5
            celdaX = new PdfPCell(new Paragraph("",fuenteCont2));
            celdaX.setBorderWidthBottom(0);
            celdaX.setBorderWidthTop(0);
            celdaX.setBorderWidthRight(0);
            celdaX.setBorderWidthLeft(0);
            tablaX.addCell(celdaX);
             
            //columna 8 fil5
            celdaX = new PdfPCell(new Paragraph("Contenedor",fuenteCont2));
            celdaX.setHorizontalAlignment(Element.ALIGN_LEFT);
            celdaX.setBorderWidthBottom(0);
            celdaX.setBorderWidthTop(0);
            celdaX.setBorderWidthRight(0);
            celdaX.setBorderWidthLeft(0);
            tablaX.addCell(celdaX);
             
            //columna 9 fil5
            celdaX = new PdfPCell(new Paragraph("",fuenteCont2));
            celdaX.setBorderWidthBottom(1);
            celdaX.setBorderWidthTop(0);
            celdaX.setBorderWidthRight(0);
            celdaX.setBorderWidthLeft(0);
            tablaX.addCell(celdaX);
            
            
            //columna 1 fil6
            celdaX = new PdfPCell(new Paragraph("",fuenteCont2));
            celdaX.setHorizontalAlignment(Element.ALIGN_LEFT);
            celdaX.setVerticalAlignment(Element.ALIGN_MIDDLE);
            celdaX.setBorderWidthBottom(0);
            celdaX.setBorderWidthTop(0);
            celdaX.setBorderWidthRight(0);
            celdaX.setBorderWidthLeft(0);
            tablaX.addCell(celdaX);
             
            //columna 2 fil6
            celdaX = new PdfPCell(new Paragraph("",fuenteCont2));
            celdaX.setBorderWidthBottom(0);
            celdaX.setBorderWidthTop(0);
            celdaX.setBorderWidthRight(0);
            celdaX.setBorderWidthLeft(0);
            tablaX.addCell(celdaX);
             
            //columna 3 fil6
            celdaX = new PdfPCell(new Paragraph("",fuenteCont2));
            celdaX.setBorderWidthBottom(0);
            celdaX.setBorderWidthTop(0);
            celdaX.setBorderWidthRight(0);
            celdaX.setBorderWidthLeft(0);
            tablaX.addCell(celdaX);
             
            //columna 4 vacio fil6
            celdaX = new PdfPCell(new Paragraph("",fuenteCont2));
            celdaX.setHorizontalAlignment(Element.ALIGN_LEFT);
            celdaX.setBorderWidthBottom(0);
            celdaX.setBorderWidthTop(0);
            celdaX.setBorderWidthRight(0);
            celdaX.setBorderWidthLeft(0);
            tablaX.addCell(celdaX);
             
            //columna 5 fil6
            celdaX = new PdfPCell(new Paragraph("Otros",fuenteCont2));
            celdaX.setHorizontalAlignment(Element.ALIGN_LEFT);
            celdaX.setBorderWidthBottom(0);
            celdaX.setBorderWidthTop(0);
            celdaX.setBorderWidthRight(0);
            celdaX.setBorderWidthLeft(0);
            tablaX.addCell(celdaX);
             
            //columna 6 fil6
            celdaX = new PdfPCell(new Paragraph("",fuenteCont2));
            celdaX.setBorderWidthBottom(1);
            celdaX.setBorderWidthTop(0);
            celdaX.setBorderWidthRight(0);
            celdaX.setBorderWidthLeft(0);
            tablaX.addCell(celdaX);
            
            //columna 7 vacio fil6
            celdaX = new PdfPCell(new Paragraph("",fuenteCont2));
            celdaX.setBorderWidthBottom(0);
            celdaX.setBorderWidthTop(0);
            celdaX.setBorderWidthRight(0);
            celdaX.setBorderWidthLeft(0);
            tablaX.addCell(celdaX);
             
            //columna 8 fil6
            celdaX = new PdfPCell(new Paragraph("Otros",fuenteCont2));
            celdaX.setHorizontalAlignment(Element.ALIGN_LEFT);
            celdaX.setBorderWidthBottom(0);
            celdaX.setBorderWidthTop(0);
            celdaX.setBorderWidthRight(0);
            celdaX.setBorderWidthLeft(0);
            tablaX.addCell(celdaX);
             
            //columna 9 fil6
            celdaX = new PdfPCell(new Paragraph("",fuenteCont2));
            celdaX.setBorderWidthBottom(1);
            celdaX.setBorderWidthTop(0);
            celdaX.setBorderWidthRight(0);
            celdaX.setBorderWidthLeft(0);
            tablaX.addCell(celdaX);
            
            
            //columna 1-9 fil7
            celdaX = new PdfPCell(new Paragraph("",fuenteCont2));
            celdaX.setBorderWidthBottom(0);
            celdaX.setBorderWidthTop(0);
            celdaX.setBorderWidthRight(0);
            celdaX.setBorderWidthLeft(0);
            celdaX.setColspan(9);
            tablaX.addCell(celdaX); 
            
            reporte.add(tablaX);
             
             
             
             
             
             float [] widths = {1,1,1,3.2f,0.7F,1,2.5F,0.6F,1.4F,1.5F,0.8F,1.5F,3};//Tamaño de las Columnas.
             PdfPTable tabla = new PdfPTable(widths);
             PdfPCell celda;
             tabla.setKeepTogether(false);
             tabla.setHeaderRows(2);
             
             
             //1 AL 11           
             celda = new PdfPCell(new Paragraph("",smallBoldFont));
             celda.setColspan(11);
             celda.setBorder(0);
             tabla.addCell(celda);
             //12 Y 13          
             celda = new PdfPCell(new Paragraph("CREDITO Y COBRANZAS",fuenteCont2));
             celda.setUseAscender(true);
             celda.setHorizontalAlignment(Element.ALIGN_CENTER);
             celda.setUseDescender(true);
             celda.setVerticalAlignment(Element.ALIGN_MIDDLE);
             celda.setBackgroundColor(BaseColor.WHITE);
             celda.setColspan(2);
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
             celda = new PdfPCell(new Paragraph("FACT",smallBoldFont));
             celda.setUseAscender(true);
             celda.setHorizontalAlignment(Element.ALIGN_CENTER);
             celda.setUseDescender(true);
             celda.setVerticalAlignment(Element.ALIGN_MIDDLE);
             celda.setBackgroundColor(BaseColor.BLACK);            
             tabla.addCell(celda);
             
             //3
             celda = new PdfPCell(new Paragraph("CLIENTE",smallBoldFont));
             celda.setUseAscender(true);
             celda.setHorizontalAlignment(Element.ALIGN_CENTER);
             celda.setUseDescender(true);
             celda.setVerticalAlignment(Element.ALIGN_MIDDLE);
             celda.setBackgroundColor(BaseColor.BLACK);            
             tabla.addCell(celda);
             
             //4
             celda = new PdfPCell(new Paragraph("NOMBRE",smallBoldFont));
             celda.setUseAscender(true);
             celda.setHorizontalAlignment(Element.ALIGN_CENTER);
             celda.setUseDescender(true);
             celda.setVerticalAlignment(Element.ALIGN_MIDDLE);
             celda.setBackgroundColor(BaseColor.BLACK);            
             tabla.addCell(celda);
             
             //5
             celda = new PdfPCell(new Paragraph("CANT",smallBoldFont));
             celda.setUseAscender(true);
             celda.setHorizontalAlignment(Element.ALIGN_CENTER);
             celda.setUseDescender(true);
             celda.setVerticalAlignment(Element.ALIGN_MIDDLE);
             celda.setBackgroundColor(BaseColor.BLACK);            
             tabla.addCell(celda);
             
             //6
             celda = new PdfPCell(new Paragraph("UNIDAD",smallBoldFont));
             celda.setUseAscender(true);
             celda.setHorizontalAlignment(Element.ALIGN_CENTER);
             celda.setUseDescender(true);
             celda.setVerticalAlignment(Element.ALIGN_MIDDLE);
             celda.setBackgroundColor(BaseColor.BLACK);            
             tabla.addCell(celda);
             
             //7
             celda = new PdfPCell(new Paragraph("DESCRIPCIÓN",smallBoldFont));
             celda.setUseAscender(true);
             celda.setHorizontalAlignment(Element.ALIGN_CENTER);
             celda.setUseDescender(true);
             celda.setVerticalAlignment(Element.ALIGN_MIDDLE);
             celda.setBackgroundColor(BaseColor.BLACK);            
             tabla.addCell(celda);
             
             //8
             celda = new PdfPCell(new Paragraph("",smallBoldFont));
             celda.setUseAscender(true);
             celda.setHorizontalAlignment(Element.ALIGN_CENTER);
             celda.setUseDescender(true);
             celda.setVerticalAlignment(Element.ALIGN_MIDDLE);
             celda.setBackgroundColor(BaseColor.BLACK);            
             tabla.addCell(celda);
             
             //9
             celda = new PdfPCell(new Paragraph("IMPORTE",smallBoldFont));
             celda.setUseAscender(true);
             celda.setHorizontalAlignment(Element.ALIGN_CENTER);
             celda.setUseDescender(true);
             celda.setVerticalAlignment(Element.ALIGN_MIDDLE);
             celda.setBackgroundColor(BaseColor.BLACK);            
             tabla.addCell(celda);
             
             //10
             celda = new PdfPCell(new Paragraph("ENVASE",smallBoldFont));
             celda.setUseAscender(true);
             celda.setHorizontalAlignment(Element.ALIGN_CENTER);
             celda.setUseDescender(true);
             celda.setVerticalAlignment(Element.ALIGN_MIDDLE);
             celda.setBackgroundColor(BaseColor.BLACK);            
             tabla.addCell(celda);
             
             //11
             celda = new PdfPCell(new Paragraph("APROB",smallBoldFont));
             celda.setUseAscender(true);
             celda.setHorizontalAlignment(Element.ALIGN_CENTER);
             celda.setUseDescender(true);
             celda.setVerticalAlignment(Element.ALIGN_MIDDLE);
             celda.setBackgroundColor(BaseColor.BLACK);            
             tabla.addCell(celda);
             
             //12
             celda = new PdfPCell(new Paragraph("ENTREGADO",smallBoldFont));
             celda.setUseAscender(true);
             celda.setHorizontalAlignment(Element.ALIGN_CENTER);
             celda.setUseDescender(true);
             celda.setVerticalAlignment(Element.ALIGN_MIDDLE);
             celda.setBackgroundColor(BaseColor.BLACK);            
             tabla.addCell(celda);
             
             //13
             celda = new PdfPCell(new Paragraph("OBSERVACIONES",smallBoldFont));
             celda.setUseAscender(true);
             celda.setHorizontalAlignment(Element.ALIGN_CENTER);
             celda.setUseDescender(true);
             celda.setVerticalAlignment(Element.ALIGN_MIDDLE);
             celda.setBackgroundColor(BaseColor.BLACK);            
             tabla.addCell(celda);
             
             Double total_mn=0.0;
             Double total_usd=0.0;
             
             for (int j=0;j<lista.size();j++){
                 HashMap<String,String> registro = lista.get(j);
                 
                 //1           
                 celda = new PdfPCell(new Paragraph(registro.get("tipo"),fuenteCont));
                 celda.setUseAscender(true);
                 celda.setHorizontalAlignment(Element.ALIGN_LEFT);
                 celda.setUseDescender(true);
                 celda.setVerticalAlignment(Element.ALIGN_MIDDLE);
                 tabla.addCell(celda);
                 //2
                 celda = new PdfPCell(new Paragraph(registro.get("factura"),fuenteCont));
                 celda.setUseAscender(true);
                 celda.setHorizontalAlignment(Element.ALIGN_CENTER);
                 celda.setUseDescender(true);
                 celda.setVerticalAlignment(Element.ALIGN_MIDDLE);
                 tabla.addCell(celda);
                 //3
                 celda = new PdfPCell(new Paragraph(registro.get("no_cliente"),fuenteCont));
                 celda.setUseAscender(true);
                 celda.setHorizontalAlignment(Element.ALIGN_CENTER);
                 celda.setUseDescender(true);
                 celda.setVerticalAlignment(Element.ALIGN_MIDDLE);
                 tabla.addCell(celda);
                 //4
                 celda = new PdfPCell(new Paragraph(registro.get("cliente"),fuenteCont));
                 celda.setUseAscender(true);
                 celda.setHorizontalAlignment(Element.ALIGN_LEFT);
                 celda.setUseDescender(true);
                 celda.setVerticalAlignment(Element.ALIGN_MIDDLE);
                 tabla.addCell(celda);
                 
                 //5
                 String cant="";
                 
                 if ( Double.parseDouble(registro.get("cantidad")) >0){
                     cant = StringHelper.AgregaComas(registro.get("cantidad"));
                 }
                 
                 celda = new PdfPCell(new Paragraph(cant,fuenteCont));
                 celda.setUseAscender(true);
                 celda.setHorizontalAlignment(Element.ALIGN_CENTER);
                 celda.setUseDescender(true);
                 celda.setVerticalAlignment(Element.ALIGN_MIDDLE);     
                 tabla.addCell(celda);
                 
                 //6
                 celda = new PdfPCell(new Paragraph(registro.get("unidad"),fuenteCont));
                 celda.setUseAscender(true);
                 celda.setHorizontalAlignment(Element.ALIGN_CENTER);
                 celda.setUseDescender(true);
                 celda.setVerticalAlignment(Element.ALIGN_MIDDLE);
                 tabla.addCell(celda);
                 
                 //7
                 celda = new PdfPCell(new Paragraph(registro.get("descripcion"),fuenteCont));
                 celda.setUseAscender(true);
                 celda.setHorizontalAlignment(Element.ALIGN_LEFT);
                 celda.setUseDescender(true);
                 celda.setVerticalAlignment(Element.ALIGN_MIDDLE);
                 tabla.addCell(celda);
                 
                //8
                celda = new PdfPCell(new Paragraph(registro.get("moneda"),fuenteCont));
                //celda.setUseAscender(true);
                celda.setHorizontalAlignment(Element.ALIGN_RIGHT);
                //celda.setUseDescender(true);
                celda.setVerticalAlignment(Element.ALIGN_MIDDLE);
                //celda.setBorderWidthBottom(1);
                //celda.setBorderWidthTop(1);
                celda.setBorderWidthRight(0);
                //celda.setBorderWidthLeft(1);
                tabla.addCell(celda);
                
                 //9
                 celda = new PdfPCell(new Paragraph(StringHelper.AgregaComas(registro.get("importe")),fuenteCont));
                 celda.setUseAscender(true);
                 celda.setHorizontalAlignment(Element.ALIGN_RIGHT);
                 celda.setUseDescender(true);
                 celda.setVerticalAlignment(Element.ALIGN_MIDDLE);     
                 celda.setBorderWidthLeft(0);
                 tabla.addCell(celda);
                 
                 //10
                 celda = new PdfPCell(new Paragraph(registro.get("envase"),fuenteCont));
                 celda.setUseAscender(true);
                 celda.setHorizontalAlignment(Element.ALIGN_CENTER);
                 celda.setUseDescender(true);
                 celda.setVerticalAlignment(Element.ALIGN_MIDDLE);     
                 tabla.addCell(celda);
                 //11
                 celda = new PdfPCell(new Paragraph(registro.get("aprobado"),fuenteCont));
                 celda.setUseAscender(true);
                 celda.setHorizontalAlignment(Element.ALIGN_CENTER);
                 celda.setUseDescender(true);
                 celda.setVerticalAlignment(Element.ALIGN_MIDDLE);     
                 tabla.addCell(celda); 
                 //12
                 celda = new PdfPCell(new Paragraph(registro.get("entregado"),fuenteCont));
                 celda.setUseAscender(true);
                 celda.setHorizontalAlignment(Element.ALIGN_CENTER);
                 celda.setUseDescender(true);
                 celda.setVerticalAlignment(Element.ALIGN_MIDDLE);     
                 tabla.addCell(celda);
                 //13
                 //celda = new PdfPCell(new Paragraph(registro.get("observaciones"),fuenteCont));
                 celda = new PdfPCell(new Paragraph( "",fuenteCont));
                 celda.setUseAscender(true);
                 celda.setHorizontalAlignment(Element.ALIGN_LEFT);
                 celda.setUseDescender(true);
                 celda.setVerticalAlignment(Element.ALIGN_MIDDLE);     
                 tabla.addCell(celda);
                   
                 Double importe = Double.parseDouble(registro.get("importe"));
                        
                if(registro.get("moneda_id").equals("1")){
                    total_mn = total_mn + importe;
                }
                
                if(registro.get("moneda_id").equals("2")){
                    total_usd = total_usd + importe;
                }
                
                
            }

            //ESTO ABARCA DE LA COLUMNA 1 A LA 6
            celda = new PdfPCell(new Paragraph(""));
            celda.setColspan(6);
            celda.setBorder(0);
            tabla.addCell(celda);
            //COLUMNA 7
            celda = new PdfPCell(new Paragraph("MONEDA NACIONAL",fuenteCont));
            celda.setHorizontalAlignment(Element.ALIGN_RIGHT);
            celda.setVerticalAlignment(Element.ALIGN_MIDDLE); 
            //celda.setBackgroundColor(BaseColor.GRAY);   
            celda.setBorderWidthBottom(0);
            celda.setBorderWidthTop(0);
            celda.setBorderWidthRight(0);
            celda.setBorderWidthLeft(0);
            tabla.addCell(celda);
            
            //COLUMNA 8
            celda = new PdfPCell(new Paragraph("$",fuenteCont));
            celda.setHorizontalAlignment(Element.ALIGN_RIGHT);
            celda.setVerticalAlignment(Element.ALIGN_MIDDLE);
            celda.setBorderWidthBottom(1);
            //celda.setBorderWidthTop(0);
            celda.setBorderWidthRight(0);
            celda.setBorderWidthLeft(0);
            tabla.addCell(celda);

             //COLUMNA 9 
             celda = new PdfPCell(new Paragraph(StringHelper.AgregaComas(StringHelper.roundDouble(total_mn, 2)),fuenteCont));
             celda.setHorizontalAlignment(Element.ALIGN_RIGHT);
             celda.setVerticalAlignment(Element.ALIGN_MIDDLE);
            celda.setBorderWidthBottom(1);
            celda.setBorderWidthTop(0);
            celda.setBorderWidthRight(0);
            celda.setBorderWidthLeft(0);
             tabla.addCell(celda);
             //COLUMNA 10 A LA 13           
             celda = new PdfPCell(new Paragraph(""));
             celda.setColspan(4);
             celda.setBorder(0);
             tabla.addCell(celda);
             

            //ESTO ES PARA LOS DOLARES
            //ESTO ABARCA DE LA COLUMNA 1 A LA 6
            celda = new PdfPCell(new Paragraph(""));
            celda.setColspan(6);
            celda.setBorder(0);
            tabla.addCell(celda);
            //COLUMNA 7
            celda = new PdfPCell(new Paragraph("DOLARES",fuenteCont));
            celda.setHorizontalAlignment(Element.ALIGN_RIGHT);
            celda.setVerticalAlignment(Element.ALIGN_MIDDLE);
            //celda.setBackgroundColor(BaseColor.GRAY);
            celda.setBorderWidthBottom(0);
            celda.setBorderWidthTop(0);
            celda.setBorderWidthRight(0);
            celda.setBorderWidthLeft(0);
            tabla.addCell(celda);
            
            //COLUMNA 8
            celda = new PdfPCell(new Paragraph("USD",fuenteCont));
            celda.setHorizontalAlignment(Element.ALIGN_RIGHT);
            celda.setVerticalAlignment(Element.ALIGN_MIDDLE); 
            celda.setBorderWidthBottom(1);
            celda.setBorderWidthTop(0);
            celda.setBorderWidthRight(0);
            celda.setBorderWidthLeft(0);
            tabla.addCell(celda);
             //COLUMNA 9 
            celda = new PdfPCell(new Paragraph(StringHelper.AgregaComas(StringHelper.roundDouble(total_usd, 2)),fuenteCont));
            celda.setHorizontalAlignment(Element.ALIGN_RIGHT);
            celda.setVerticalAlignment(Element.ALIGN_MIDDLE);     
            celda.setBorderWidthBottom(1);
            celda.setBorderWidthTop(0);
            celda.setBorderWidthRight(0);
            celda.setBorderWidthLeft(0);
            tabla.addCell(celda);
            
             //COLUMNA 10 A LA 13       
             celda = new PdfPCell(new Paragraph(""));
             celda.setColspan(4);
             celda.setBorder(0);
             tabla.addCell(celda);
             
             /*
             //ESTO ES PARA DEJAR UNA FILA EN BLANCO
             celda = new PdfPCell(new Paragraph(""));
             celda.setFixedHeight(15);
             celda.setColspan(13);
             celda.setBorder(0);
             tabla.addCell(celda);
             */
             
             
             celda = new PdfPCell(new Paragraph("INCIDENCIAS",fuenteCont2));
            celda.setUseAscender(true);
            celda.setHorizontalAlignment(Element.ALIGN_LEFT);
            celda.setUseDescender(true);
            celda.setVerticalAlignment(Element.ALIGN_MIDDLE);
            celda.setColspan(6);
            celda.setBorder(0);
            tabla.addCell(celda);

            celda = new PdfPCell(new Paragraph(""));
            celda.setColspan(7);
            celda.setBorder(0);
            tabla.addCell(celda);

             //ESTO ES PARA DEJAR 8 RENGLONES
             for(int x=1; x<=6;x++){
                celda = new PdfPCell(new Paragraph("_________________________________________________________",fuenteCont2));
                celda.setUseAscender(true);
                celda.setHorizontalAlignment(Element.ALIGN_LEFT);
                celda.setUseDescender(true);
                celda.setVerticalAlignment(Element.ALIGN_MIDDLE);
                celda.setColspan(6);
                celda.setBorder(0);
                tabla.addCell(celda);

                celda = new PdfPCell(new Paragraph(""));
                celda.setColspan(7);
                celda.setBorder(0);
                tabla.addCell(celda);               
                
             }//TERMINAN LOS 8 RENGLONES METIDOS EN UN CICLO FOR
             //ESTO ES PARA DEJAR UNA FILA EN BLANCO
             celda = new PdfPCell(new Paragraph(""));
             celda.setFixedHeight(15);
             celda.setColspan(13);
             celda.setBorder(0);
             tabla.addCell(celda);
             
             
             
            //columna 1-4
            celda = new PdfPCell(new Paragraph("ELABORÓ",fuenteCont2));
            celda.setHorizontalAlignment(Element.ALIGN_CENTER);
            celda.setColspan(4);
            celda.setBorderWidthBottom(0);
            celda.setBorderWidthTop(0);
            celda.setBorderWidthRight(0);
            celda.setBorderWidthLeft(0);
            tabla.addCell(celda);
             
            //columna 5
            celda = new PdfPCell(new Paragraph("",fuenteCont2));
            celda.setHorizontalAlignment(Element.ALIGN_CENTER);
            celda.setBorderWidthBottom(0);
            celda.setBorderWidthTop(0);
            celda.setBorderWidthRight(0);
            celda.setBorderWidthLeft(0);
            tabla.addCell(celda);
             
            //columna 6-9
            celda = new PdfPCell(new Paragraph("LIBERÓ",fuenteCont2));
            celda.setHorizontalAlignment(Element.ALIGN_CENTER);
            celda.setColspan(4);
            celda.setBorderWidthBottom(0);
            celda.setBorderWidthTop(0);
            celda.setBorderWidthRight(0);
            celda.setBorderWidthLeft(0);
            tabla.addCell(celda);
             
            //columna 10
            celda = new PdfPCell(new Paragraph("",fuenteCont2));
            celda.setHorizontalAlignment(Element.ALIGN_CENTER);
            celda.setBorderWidthBottom(0);
            celda.setBorderWidthTop(0);
            celda.setBorderWidthRight(0);
            celda.setBorderWidthLeft(0);
            tabla.addCell(celda);
            
            //columna 11-13
            celda = new PdfPCell(new Paragraph("OPERADOR",fuenteCont2));
            celda.setHorizontalAlignment(Element.ALIGN_CENTER);
            celda.setColspan(4);
            celda.setBorderWidthBottom(0);
            celda.setBorderWidthTop(0);
            celda.setBorderWidthRight(0);
            celda.setBorderWidthLeft(0);
            tabla.addCell(celda);
             
            //AQUI ESTAN LAS 3 RAYAS DE ELABORO, REVISION, OPERADOR
            //columna 1-4
            celda = new PdfPCell(new Paragraph("",fuenteCont2));
            celda.setHorizontalAlignment(Element.ALIGN_CENTER);
            celda.setColspan(4);
            celda.setBorderWidthBottom(1);
            celda.setBorderWidthTop(0);
            celda.setBorderWidthRight(0);
            celda.setBorderWidthLeft(0);
            celda.setFixedHeight(15);
            tabla.addCell(celda);
             
            //columna 5
            celda = new PdfPCell(new Paragraph("",fuenteCont2));
            celda.setHorizontalAlignment(Element.ALIGN_CENTER);
            celda.setBorderWidthBottom(0);
            celda.setBorderWidthTop(0);
            celda.setBorderWidthRight(0);
            celda.setBorderWidthLeft(0);
            celda.setFixedHeight(15);
            tabla.addCell(celda);
             
            //columna 6-9
            celda = new PdfPCell(new Paragraph("",fuenteCont2));
            celda.setHorizontalAlignment(Element.ALIGN_CENTER);
            celda.setColspan(4);
            celda.setBorderWidthBottom(1);
            celda.setBorderWidthTop(0);
            celda.setBorderWidthRight(0);
            celda.setBorderWidthLeft(0);
            celda.setFixedHeight(15);
            tabla.addCell(celda);
             
            //columna 10
            celda = new PdfPCell(new Paragraph("",fuenteCont2));
            celda.setHorizontalAlignment(Element.ALIGN_CENTER);
            celda.setBorderWidthBottom(0);
            celda.setBorderWidthTop(0);
            celda.setBorderWidthRight(0);
            celda.setBorderWidthLeft(0);
            celda.setFixedHeight(15);
            tabla.addCell(celda);
            
            //columna 11-13
            celda = new PdfPCell(new Paragraph("",fuenteCont2));
            celda.setHorizontalAlignment(Element.ALIGN_CENTER);
            celda.setColspan(4);
            celda.setBorderWidthBottom(1);
            celda.setBorderWidthTop(0);
            celda.setBorderWidthRight(0);
            celda.setBorderWidthLeft(0);
            celda.setFixedHeight(15);
            tabla.addCell(celda);
             
             
             reporte.add(tabla);
             
        }
         
        catch (Exception e){
             System.out.println(e.toString());
             }
        reporte.close();
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
            
            String fecha []= TimeHelper.getFechaActualYMD().split("-");
            
            String nombreMes =TimeHelper.ConvertNumToMonth(Integer.parseInt(fecha[1]));
            String fecha_impresion = "Generado el "+fecha[2]+" de "+nombreMes+" del "+fecha[0];
            
            //SimpleDateFormat formato = new SimpleDateFormat("'Generado el' d 'de' MMMMM 'del' yyyy 'a las' HH:mm:ss 'hrs.'");
            //String impreso_en = formato.format(new Date());
            
            ColumnText.showTextAligned(writer.getDirectContent(),Element.ALIGN_CENTER, new Phrase(fecha_impresion,largeFont),document.getPageSize().getWidth()/2, document.getPageSize().getTop()-50, 0);
            
            cb = writer.getDirectContent();
            float textBase = document.bottom() - 10;
            
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
