
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
 * @author Federico
 */
public class LogAdmPdfReporte {
    
    public LogAdmPdfReporte(HashMap<String, String> datosEncabezadoPie,String fileout,ArrayList<HashMap<String, String>> lista,HashMap<String, String> datos,ArrayList<HashMap<String, Object>> serv_adic) throws FileNotFoundException, DocumentException {
        Font smallBoldFont = new Font(Font.getFamily("ARIAL"),7,Font.NORMAL,BaseColor.WHITE);
        Font smallBoldFont2 = new Font(Font.getFamily("ARIAL"),6,Font.NORMAL,BaseColor.WHITE);
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
        
        PdfPCell celda;
        
        
        try {            
             reporte.open();
             
            float [] tam_tablax = {0.5f,.8f,0.1f,0.5f,0.3f,0.1f,0.3f,0.6f,0.1f,0.5f,0.5f,0.1f,0.3f,0.5f,0.1f,0.2f,0.4f};
            PdfPTable tablaX = new PdfPTable(tam_tablax);
            PdfPCell celdaX;
            tablaX.setKeepTogether(false);
            
            //columna 1 fil1
            celdaX = new PdfPCell(new Paragraph("Bodega",fuenteCont2));
            celdaX.setHorizontalAlignment(Element.ALIGN_LEFT);
            celdaX.setVerticalAlignment(Element.ALIGN_MIDDLE);
            celdaX.setBorderWidthBottom(0);
            celdaX.setBorderWidthTop(0);
            celdaX.setBorderWidthRight(0);
            celdaX.setBorderWidthLeft(0);
            tablaX.addCell(celdaX);
             
            //columna 2 fil1
            celdaX = new PdfPCell(new Paragraph(datos.get("titulo_almacen"),fuenteCont2));
            celdaX.setHorizontalAlignment(Element.ALIGN_LEFT);
            celdaX.setVerticalAlignment(Element.ALIGN_MIDDLE);
            celdaX.setBorderWidthBottom(1);
            celdaX.setBorderWidthTop(0);
            celdaX.setBorderWidthRight(0);
            celdaX.setBorderWidthLeft(0);
            tablaX.addCell(celdaX);
            
            //columna 3 fil1
            celdaX = new PdfPCell(new Paragraph("",fuenteCont2));
            celdaX.setHorizontalAlignment(Element.ALIGN_LEFT);
            celdaX.setVerticalAlignment(Element.ALIGN_MIDDLE);
            celdaX.setBorderWidthBottom(0);
            celdaX.setBorderWidthTop(0);
            celdaX.setBorderWidthRight(0);
            celdaX.setBorderWidthLeft(0);
            tablaX.addCell(celdaX);
            
            //columna 4 fil1
            celdaX = new PdfPCell(new Paragraph("No. de Unidad",fuenteCont2));
            celdaX.setHorizontalAlignment(Element.ALIGN_LEFT);
            celdaX.setVerticalAlignment(Element.ALIGN_MIDDLE);
            celdaX.setBorderWidthBottom(0);
            celdaX.setBorderWidthTop(0);
            celdaX.setBorderWidthRight(0);
            celdaX.setBorderWidthLeft(0);
            tablaX.addCell(celdaX);
           
             
            //columna 5 fil1
            celdaX = new PdfPCell(new Paragraph(datos.get("no_unidad"),fuenteCont2));
            celdaX.setHorizontalAlignment(Element.ALIGN_LEFT);
            celdaX.setVerticalAlignment(Element.ALIGN_MIDDLE);
            celdaX.setBorderWidthBottom(1);
            celdaX.setBorderWidthTop(0);
            celdaX.setBorderWidthRight(0);
            celdaX.setBorderWidthLeft(0);
            tablaX.addCell(celdaX);
            
            //columna 6 fil1
            celdaX = new PdfPCell(new Paragraph("",fuenteCont2));
            celdaX.setHorizontalAlignment(Element.ALIGN_LEFT);
            celdaX.setVerticalAlignment(Element.ALIGN_MIDDLE);
            celdaX.setBorderWidthBottom(0);
            celdaX.setBorderWidthTop(0);
            celdaX.setBorderWidthRight(0);
            celdaX.setBorderWidthLeft(0);
            tablaX.addCell(celdaX);
            
              //columna 7 fil1
            celdaX = new PdfPCell(new Paragraph("Vehiculo",fuenteCont2));
            celdaX.setHorizontalAlignment(Element.ALIGN_LEFT);
            celdaX.setVerticalAlignment(Element.ALIGN_MIDDLE);
            celdaX.setBorderWidthBottom(0);
            celdaX.setBorderWidthTop(0);
            celdaX.setBorderWidthRight(0);
            celdaX.setBorderWidthLeft(0);
            tablaX.addCell(celdaX);
             
            //columna 8 fil1
            celdaX = new PdfPCell(new Paragraph(datos.get("unidad"),fuenteCont2));
            celdaX.setHorizontalAlignment(Element.ALIGN_LEFT);
            celdaX.setVerticalAlignment(Element.ALIGN_MIDDLE);
            celdaX.setBorderWidthBottom(1);
            celdaX.setBorderWidthTop(0);
            celdaX.setBorderWidthRight(0);
            celdaX.setBorderWidthLeft(0);
            tablaX.addCell(celdaX);
            
            //columna 9 fil1
            celdaX = new PdfPCell(new Paragraph("",fuenteCont2));
            celdaX.setHorizontalAlignment(Element.ALIGN_LEFT);
            celdaX.setVerticalAlignment(Element.ALIGN_MIDDLE);
            celdaX.setBorderWidthBottom(0);
            celdaX.setBorderWidthTop(0);
            celdaX.setBorderWidthRight(0);
            celdaX.setBorderWidthLeft(0);
            tablaX.addCell(celdaX);
            
             //columna 10 fil1
            celdaX = new PdfPCell(new Paragraph("No. Economico",fuenteCont2));
            celdaX.setHorizontalAlignment(Element.ALIGN_LEFT);
            celdaX.setVerticalAlignment(Element.ALIGN_MIDDLE);
            celdaX.setBorderWidthBottom(0);
            celdaX.setBorderWidthTop(0);
            celdaX.setBorderWidthRight(0);
            celdaX.setBorderWidthLeft(0);
            tablaX.addCell(celdaX);
            
             //columna 11 fil1
            celdaX = new PdfPCell(new Paragraph(datos.get("no_economico"),fuenteCont2));
            celdaX.setHorizontalAlignment(Element.ALIGN_LEFT);
            celdaX.setVerticalAlignment(Element.ALIGN_MIDDLE);
            celdaX.setBorderWidthBottom(1);
            celdaX.setBorderWidthTop(0);
            celdaX.setBorderWidthRight(0);
            celdaX.setBorderWidthLeft(0);
            tablaX.addCell(celdaX);
            
            //columna 12 fil1
            celdaX = new PdfPCell(new Paragraph("",fuenteCont2));
            celdaX.setHorizontalAlignment(Element.ALIGN_LEFT);
            celdaX.setVerticalAlignment(Element.ALIGN_MIDDLE);
            celdaX.setBorderWidthBottom(0);
            celdaX.setBorderWidthTop(0);
            celdaX.setBorderWidthRight(0);
            celdaX.setBorderWidthLeft(0);
            tablaX.addCell(celdaX);
            
            //columna 13 fil1
            celdaX = new PdfPCell(new Paragraph("Placas",fuenteCont2));
            celdaX.setHorizontalAlignment(Element.ALIGN_LEFT);
            celdaX.setVerticalAlignment(Element.ALIGN_MIDDLE);
            celdaX.setBorderWidthBottom(0);
            celdaX.setBorderWidthTop(0);
            celdaX.setBorderWidthRight(0);
            celdaX.setBorderWidthLeft(0);
            tablaX.addCell(celdaX);
            
            //columna 14 fil1
            celdaX = new PdfPCell(new Paragraph(datos.get("placas"),fuenteCont2));
            celdaX.setHorizontalAlignment(Element.ALIGN_LEFT);
            celdaX.setVerticalAlignment(Element.ALIGN_MIDDLE);
            celdaX.setBorderWidthBottom(1);
            celdaX.setBorderWidthTop(0);
            celdaX.setBorderWidthRight(0);
            celdaX.setBorderWidthLeft(0);
            tablaX.addCell(celdaX);
            
            //columna 15 fil1
            celdaX = new PdfPCell(new Paragraph("",fuenteCont2));
            celdaX.setHorizontalAlignment(Element.ALIGN_LEFT);
            celdaX.setVerticalAlignment(Element.ALIGN_MIDDLE);
            celdaX.setBorderWidthBottom(0);
            celdaX.setBorderWidthTop(0);
            celdaX.setBorderWidthRight(0);
            celdaX.setBorderWidthLeft(0);
            tablaX.addCell(celdaX);
            
            //columna 16 fil1
            celdaX = new PdfPCell(new Paragraph("Tipo",fuenteCont2));
            celdaX.setHorizontalAlignment(Element.ALIGN_LEFT);
            celdaX.setVerticalAlignment(Element.ALIGN_MIDDLE);
            celdaX.setBorderWidthBottom(0);
            celdaX.setBorderWidthTop(0);
            celdaX.setBorderWidthRight(0);
            celdaX.setBorderWidthLeft(0);
            tablaX.addCell(celdaX);
            
            //columna 17 fil1
            celdaX = new PdfPCell(new Paragraph(datos.get("tipo"),fuenteCont2));
            celdaX.setHorizontalAlignment(Element.ALIGN_LEFT);
            celdaX.setVerticalAlignment(Element.ALIGN_MIDDLE);
            celdaX.setBorderWidthBottom(1);
            celdaX.setBorderWidthTop(0);
            celdaX.setBorderWidthRight(0);
            celdaX.setBorderWidthLeft(0);
            tablaX.addCell(celdaX);
            
            
            
            
            //columna 1 fil2
            celdaX = new PdfPCell(new Paragraph("Zona",fuenteCont2));
            celdaX.setHorizontalAlignment(Element.ALIGN_LEFT);
            celdaX.setVerticalAlignment(Element.ALIGN_MIDDLE);
            celdaX.setBorderWidthBottom(0);
            celdaX.setBorderWidthTop(0);
            celdaX.setBorderWidthRight(0);
            celdaX.setBorderWidthLeft(0);
            tablaX.addCell(celdaX);
             
            //columna 2 fil2
            celdaX = new PdfPCell(new Paragraph(datos.get(""),fuenteCont2));
            celdaX.setHorizontalAlignment(Element.ALIGN_LEFT);
            celdaX.setVerticalAlignment(Element.ALIGN_MIDDLE);
            celdaX.setBorderWidthBottom(1);
            celdaX.setBorderWidthTop(0);
            celdaX.setBorderWidthRight(0);
            celdaX.setBorderWidthLeft(0);
            tablaX.addCell(celdaX);
            
            //columna 3 fil2
            celdaX = new PdfPCell(new Paragraph("",fuenteCont2));
            celdaX.setHorizontalAlignment(Element.ALIGN_LEFT);
            celdaX.setVerticalAlignment(Element.ALIGN_MIDDLE);
            celdaX.setBorderWidthBottom(0);
            celdaX.setBorderWidthTop(0);
            celdaX.setBorderWidthRight(0);
            celdaX.setBorderWidthLeft(0);
            tablaX.addCell(celdaX);
            
            //columna 4 fil12
            celdaX = new PdfPCell(new Paragraph("Transportista",fuenteCont2));
            celdaX.setHorizontalAlignment(Element.ALIGN_LEFT);
            celdaX.setVerticalAlignment(Element.ALIGN_MIDDLE);
            celdaX.setBorderWidthBottom(0);
            celdaX.setBorderWidthTop(0);
            celdaX.setBorderWidthRight(0);
            celdaX.setBorderWidthLeft(0);
            tablaX.addCell(celdaX);
           
            //columna 5-11 fil2
            celdaX = new PdfPCell(new Paragraph(datos.get("tranportista_proveedor"),fuenteCont2));
            celdaX.setHorizontalAlignment(Element.ALIGN_LEFT);
            celdaX.setVerticalAlignment(Element.ALIGN_MIDDLE);
            celdaX.setBorderWidthBottom(1);
            celdaX.setBorderWidthTop(0);
            celdaX.setBorderWidthRight(0);
            celdaX.setBorderWidthLeft(0);
            celdaX.setColspan(7);
            tablaX.addCell(celdaX);
            
            //columna 12 fil2
            celdaX = new PdfPCell(new Paragraph("",fuenteCont2));
            celdaX.setHorizontalAlignment(Element.ALIGN_LEFT);
            celdaX.setVerticalAlignment(Element.ALIGN_MIDDLE);
            celdaX.setBorderWidthBottom(0);
            celdaX.setBorderWidthTop(0);
            celdaX.setBorderWidthRight(0);
            celdaX.setBorderWidthLeft(0);
            tablaX.addCell(celdaX);
            
              //columna 13 fil2
            celdaX = new PdfPCell(new Paragraph("Cerrada",fuenteCont2));
            celdaX.setHorizontalAlignment(Element.ALIGN_LEFT);
            celdaX.setVerticalAlignment(Element.ALIGN_MIDDLE);
            celdaX.setBorderWidthBottom(0);
            celdaX.setBorderWidthTop(0);
            celdaX.setBorderWidthRight(0);
            celdaX.setBorderWidthLeft(0);
            tablaX.addCell(celdaX);
             
            //columna 14 fil2
            celdaX = new PdfPCell(new Paragraph(datos.get(""),fuenteCont2));
            celdaX.setHorizontalAlignment(Element.ALIGN_LEFT);
            celdaX.setVerticalAlignment(Element.ALIGN_MIDDLE);
            celdaX.setBorderWidthBottom(1);
            celdaX.setBorderWidthTop(0);
            celdaX.setBorderWidthRight(0);
            celdaX.setBorderWidthLeft(0);
            tablaX.addCell(celdaX);
            
             //columna 15-17 fil2
            celdaX = new PdfPCell(new Paragraph("",fuenteCont2));
            celdaX.setHorizontalAlignment(Element.ALIGN_LEFT);
            celdaX.setVerticalAlignment(Element.ALIGN_MIDDLE);
            celdaX.setBorderWidthBottom(0);
            celdaX.setBorderWidthTop(0);
            celdaX.setBorderWidthRight(0);
            celdaX.setBorderWidthLeft(0);
            celdaX.setColspan(3);
            tablaX.addCell(celdaX);
            
  
  
            //columna 1 fil3
            celdaX = new PdfPCell(new Paragraph("Porteo",fuenteCont2));
            celdaX.setHorizontalAlignment(Element.ALIGN_LEFT);
            celdaX.setVerticalAlignment(Element.ALIGN_MIDDLE);
            celdaX.setBorderWidthBottom(0);
            celdaX.setBorderWidthTop(0);
            celdaX.setBorderWidthRight(0);
            celdaX.setBorderWidthLeft(0);
            tablaX.addCell(celdaX);
             
            //columna 2 fil3
            celdaX = new PdfPCell(new Paragraph(datos.get(""),fuenteCont2));
            celdaX.setHorizontalAlignment(Element.ALIGN_LEFT);
            celdaX.setVerticalAlignment(Element.ALIGN_MIDDLE);
            celdaX.setBorderWidthBottom(1);
            celdaX.setBorderWidthTop(0);
            celdaX.setBorderWidthRight(0);
            celdaX.setBorderWidthLeft(0);
            tablaX.addCell(celdaX);
            
            //columna 3 fil3
            celdaX = new PdfPCell(new Paragraph("",fuenteCont2));
            celdaX.setHorizontalAlignment(Element.ALIGN_LEFT);
            celdaX.setVerticalAlignment(Element.ALIGN_MIDDLE);
            celdaX.setBorderWidthBottom(0);
            celdaX.setBorderWidthTop(0);
            celdaX.setBorderWidthRight(0);
            celdaX.setBorderWidthLeft(0);
            tablaX.addCell(celdaX);
            
            //columna 4 fil3
            celdaX = new PdfPCell(new Paragraph("No. Ruta",fuenteCont2));
            celdaX.setHorizontalAlignment(Element.ALIGN_LEFT);
            celdaX.setVerticalAlignment(Element.ALIGN_MIDDLE);
            celdaX.setBorderWidthBottom(0);
            celdaX.setBorderWidthTop(0);
            celdaX.setBorderWidthRight(0);
            celdaX.setBorderWidthLeft(0);
            tablaX.addCell(celdaX);
             
            //columna 5 fil3
            celdaX = new PdfPCell(new Paragraph(datos.get("no_ruta"),fuenteCont2));
            celdaX.setHorizontalAlignment(Element.ALIGN_LEFT);
            celdaX.setVerticalAlignment(Element.ALIGN_MIDDLE);
            celdaX.setBorderWidthBottom(1);
            celdaX.setBorderWidthTop(0);
            celdaX.setBorderWidthRight(0);
            celdaX.setBorderWidthLeft(0);
            tablaX.addCell(celdaX);
            
            //columna 6 fil3
            celdaX = new PdfPCell(new Paragraph("",fuenteCont2));
            celdaX.setHorizontalAlignment(Element.ALIGN_LEFT);
            celdaX.setVerticalAlignment(Element.ALIGN_MIDDLE);
            celdaX.setBorderWidthBottom(0);
            celdaX.setBorderWidthTop(0);
            celdaX.setBorderWidthRight(0);
            celdaX.setBorderWidthLeft(0);
            tablaX.addCell(celdaX);
            
            //columna 7 fil3
            celdaX = new PdfPCell(new Paragraph("Ruta",fuenteCont2));
            celdaX.setHorizontalAlignment(Element.ALIGN_LEFT);
            celdaX.setVerticalAlignment(Element.ALIGN_MIDDLE);
            celdaX.setBorderWidthBottom(0);
            celdaX.setBorderWidthTop(0);
            celdaX.setBorderWidthRight(0);
            celdaX.setBorderWidthLeft(0);
            tablaX.addCell(celdaX);
            
             //columna 8-14 fil3
            celdaX = new PdfPCell(new Paragraph(datos.get("titulo_ruta"),fuenteCont2));
            celdaX.setHorizontalAlignment(Element.ALIGN_LEFT);
            celdaX.setVerticalAlignment(Element.ALIGN_MIDDLE);
            celdaX.setBorderWidthBottom(1);
            celdaX.setBorderWidthTop(0);
            celdaX.setBorderWidthRight(0);
            celdaX.setBorderWidthLeft(0);
            celdaX.setColspan(7);   
            tablaX.addCell(celdaX);
            
            //columna 15 fil3
            celdaX = new PdfPCell(new Paragraph("",fuenteCont2));
            celdaX.setHorizontalAlignment(Element.ALIGN_LEFT);
            celdaX.setVerticalAlignment(Element.ALIGN_MIDDLE);
            celdaX.setBorderWidthBottom(0);
            celdaX.setBorderWidthTop(0);
            celdaX.setBorderWidthRight(0);
            celdaX.setBorderWidthLeft(0);
            tablaX.addCell(celdaX);
            
            //columna 16 fil3
            celdaX = new PdfPCell(new Paragraph("Kms.",fuenteCont2));
            celdaX.setHorizontalAlignment(Element.ALIGN_LEFT);
            celdaX.setVerticalAlignment(Element.ALIGN_MIDDLE);
            celdaX.setBorderWidthBottom(0);
            celdaX.setBorderWidthTop(0);
            celdaX.setBorderWidthRight(0);
            celdaX.setBorderWidthLeft(0);
            tablaX.addCell(celdaX);
            
            //columna 17 fil3
            celdaX = new PdfPCell(new Paragraph(datos.get("km_ruta"),fuenteCont2));
            celdaX.setHorizontalAlignment(Element.ALIGN_LEFT);
            celdaX.setVerticalAlignment(Element.ALIGN_MIDDLE);
            celdaX.setBorderWidthBottom(1);
            celdaX.setBorderWidthTop(0);
            celdaX.setBorderWidthRight(0);
            celdaX.setBorderWidthLeft(0);
            tablaX.addCell(celdaX);
            
            
            
            //columna 1 fil4
            celdaX = new PdfPCell(new Paragraph("No de Telefono",fuenteCont2));
            celdaX.setHorizontalAlignment(Element.ALIGN_LEFT);
            celdaX.setVerticalAlignment(Element.ALIGN_MIDDLE);
            celdaX.setBorderWidthBottom(0);
            celdaX.setBorderWidthTop(0);
            celdaX.setBorderWidthRight(0);
            celdaX.setBorderWidthLeft(0);
            tablaX.addCell(celdaX);
            
            //columna 2 fil4
            celdaX = new PdfPCell(new Paragraph(datos.get(""),fuenteCont2));
            celdaX.setHorizontalAlignment(Element.ALIGN_LEFT);
            celdaX.setVerticalAlignment(Element.ALIGN_MIDDLE);
            celdaX.setBorderWidthBottom(1);
            celdaX.setBorderWidthTop(0);
            celdaX.setBorderWidthRight(0);
            celdaX.setBorderWidthLeft(0);
            tablaX.addCell(celdaX);
            
            //columna 3 fil4
            celdaX = new PdfPCell(new Paragraph("",fuenteCont2));
            celdaX.setHorizontalAlignment(Element.ALIGN_LEFT);
            celdaX.setVerticalAlignment(Element.ALIGN_MIDDLE);
            celdaX.setBorderWidthBottom(0);
            celdaX.setBorderWidthTop(0);
            celdaX.setBorderWidthRight(0);
            celdaX.setBorderWidthLeft(0);
            tablaX.addCell(celdaX);
            
             //columna 4 fil14
            celdaX = new PdfPCell(new Paragraph("Costo de Flete",fuenteCont2));
            celdaX.setHorizontalAlignment(Element.ALIGN_LEFT);
            celdaX.setVerticalAlignment(Element.ALIGN_MIDDLE);
            celdaX.setBorderWidthBottom(0);
            celdaX.setBorderWidthTop(0);
            celdaX.setBorderWidthRight(0);
            celdaX.setBorderWidthLeft(0);
            tablaX.addCell(celdaX);
           
            //columna 5 fil4
            celdaX = new PdfPCell(new Paragraph(datos.get("costo_ruta"),fuenteCont2));
            celdaX.setHorizontalAlignment(Element.ALIGN_LEFT);
            celdaX.setVerticalAlignment(Element.ALIGN_MIDDLE);
            celdaX.setBorderWidthBottom(1);
            celdaX.setBorderWidthTop(0);
            celdaX.setBorderWidthRight(0);
            celdaX.setBorderWidthLeft(0);
            tablaX.addCell(celdaX);
            
            //columna 6-9 fil4
            celdaX = new PdfPCell(new Paragraph("",fuenteCont2));
            celdaX.setHorizontalAlignment(Element.ALIGN_LEFT);
            celdaX.setVerticalAlignment(Element.ALIGN_MIDDLE);
            celdaX.setBorderWidthBottom(0);
            celdaX.setBorderWidthTop(0);
            celdaX.setBorderWidthRight(0);
            celdaX.setBorderWidthLeft(0);
            celdaX.setColspan(4);
            tablaX.addCell(celdaX);
            
            //columna 10 fil4
            celdaX = new PdfPCell(new Paragraph("Tar Varc:",fuenteCont2));
            celdaX.setHorizontalAlignment(Element.ALIGN_LEFT);
            celdaX.setVerticalAlignment(Element.ALIGN_MIDDLE);
            celdaX.setBorderWidthBottom(0);
            celdaX.setBorderWidthTop(0);
            celdaX.setBorderWidthRight(0);
            celdaX.setBorderWidthLeft(0);
            tablaX.addCell(celdaX);
            
            //columna 11 fil4
            celdaX = new PdfPCell(new Paragraph(datos.get(""),fuenteCont2));
            celdaX.setHorizontalAlignment(Element.ALIGN_LEFT);
            celdaX.setVerticalAlignment(Element.ALIGN_MIDDLE);
            celdaX.setBorderWidthBottom(1);
            celdaX.setBorderWidthTop(0);
            celdaX.setBorderWidthRight(0);
            celdaX.setBorderWidthLeft(0);
            tablaX.addCell(celdaX);
            
            //columna 12-17 fil4
            celdaX = new PdfPCell(new Paragraph("",fuenteCont2));
            celdaX.setHorizontalAlignment(Element.ALIGN_LEFT);
            celdaX.setVerticalAlignment(Element.ALIGN_MIDDLE);
            celdaX.setBorderWidthBottom(0);
            celdaX.setBorderWidthTop(0);
            celdaX.setBorderWidthRight(0);
            celdaX.setBorderWidthLeft(0);
            celdaX.setColspan(6);
            tablaX.addCell(celdaX);
          
            
            
            
            //columna1 fil5
            celdaX = new PdfPCell(new Paragraph("Folio Viaje",fuenteCont2));
            celdaX.setHorizontalAlignment(Element.ALIGN_LEFT);
            celdaX.setVerticalAlignment(Element.ALIGN_MIDDLE);
            celdaX.setBorderWidthBottom(0);
            celdaX.setBorderWidthTop(0);
            celdaX.setBorderWidthRight(0);
            celdaX.setBorderWidthLeft(0);
            tablaX.addCell(celdaX);
            
            //columna2 fil5
            celdaX = new PdfPCell(new Paragraph(datos.get("folio"),fuenteCont2));
            celdaX.setHorizontalAlignment(Element.ALIGN_LEFT);
            celdaX.setVerticalAlignment(Element.ALIGN_MIDDLE);
            celdaX.setBorderWidthBottom(1);
            celdaX.setBorderWidthTop(0);
            celdaX.setBorderWidthRight(0);
            celdaX.setBorderWidthLeft(0);
            tablaX.addCell(celdaX);
            
            //columna 3 fil5
            celdaX = new PdfPCell(new Paragraph("",fuenteCont2));
            celdaX.setHorizontalAlignment(Element.ALIGN_LEFT);
            celdaX.setVerticalAlignment(Element.ALIGN_MIDDLE);
            celdaX.setBorderWidthBottom(0);
            celdaX.setBorderWidthTop(0);
            celdaX.setBorderWidthRight(0);
            celdaX.setBorderWidthLeft(0);
            tablaX.addCell(celdaX);
            
            //columna 4 fil5
            celdaX = new PdfPCell(new Paragraph("Fecha",fuenteCont2));
            celdaX.setHorizontalAlignment(Element.ALIGN_LEFT);
            celdaX.setVerticalAlignment(Element.ALIGN_MIDDLE);
            celdaX.setBorderWidthBottom(0);
            celdaX.setBorderWidthTop(0);
            celdaX.setBorderWidthRight(0);
            celdaX.setBorderWidthLeft(0);
            tablaX.addCell(celdaX);
            
            //columna 5-8 fil5
            celdaX = new PdfPCell(new Paragraph(datos.get("fecha"),fuenteCont2));
            celdaX.setHorizontalAlignment(Element.ALIGN_LEFT);
            celdaX.setVerticalAlignment(Element.ALIGN_MIDDLE);
            celdaX.setBorderWidthBottom(1);
            celdaX.setBorderWidthTop(0);
            celdaX.setBorderWidthRight(0);
            celdaX.setBorderWidthLeft(0);
            celdaX.setColspan(3);
            tablaX.addCell(celdaX);
            
            //columna 9-10 fil5
            celdaX = new PdfPCell(new Paragraph("",fuenteCont2));
            celdaX.setHorizontalAlignment(Element.ALIGN_LEFT);
            celdaX.setVerticalAlignment(Element.ALIGN_MIDDLE);
            celdaX.setBorderWidthBottom(0);
            celdaX.setBorderWidthTop(0);
            celdaX.setBorderWidthRight(0);
            celdaX.setBorderWidthLeft(0);
            celdaX.setColspan(2);
            tablaX.addCell(celdaX);
            
            //columna 11 fil5
            celdaX = new PdfPCell(new Paragraph("Hora",fuenteCont2));
            celdaX.setHorizontalAlignment(Element.ALIGN_LEFT);
            celdaX.setVerticalAlignment(Element.ALIGN_MIDDLE);
            celdaX.setBorderWidthBottom(0);
            celdaX.setBorderWidthTop(0);
            celdaX.setBorderWidthRight(0);
            celdaX.setBorderWidthLeft(0);
            tablaX.addCell(celdaX);
            
            //columna 12- fil5
            celdaX = new PdfPCell(new Paragraph(datos.get("hora"),fuenteCont2));
            celdaX.setHorizontalAlignment(Element.ALIGN_LEFT);
            celdaX.setVerticalAlignment(Element.ALIGN_MIDDLE);
            celdaX.setBorderWidthBottom(1);
            celdaX.setBorderWidthTop(0);
            celdaX.setBorderWidthRight(0);
            celdaX.setBorderWidthLeft(0);
            celdaX.setColspan(1);
            tablaX.addCell(celdaX);
            
            //columna 13 fil5
            celdaX = new PdfPCell(new Paragraph("",fuenteCont2));
            celdaX.setHorizontalAlignment(Element.ALIGN_LEFT);
            celdaX.setVerticalAlignment(Element.ALIGN_MIDDLE);
            celdaX.setBorderWidthBottom(0);
            celdaX.setBorderWidthTop(0);
            celdaX.setBorderWidthRight(0);
            celdaX.setBorderWidthLeft(0);
            tablaX.addCell(celdaX);
            
            //columna 14 fil5
            celdaX = new PdfPCell(new Paragraph("Sucursal",fuenteCont2));
            celdaX.setHorizontalAlignment(Element.ALIGN_LEFT);
            celdaX.setVerticalAlignment(Element.ALIGN_MIDDLE);
            celdaX.setBorderWidthBottom(0);
            celdaX.setBorderWidthTop(0);
            celdaX.setBorderWidthRight(0);
            celdaX.setBorderWidthLeft(0);
            tablaX.addCell(celdaX);
            
            //columna 15-17 fil5
            celdaX = new PdfPCell(new Paragraph(datos.get("nom_suc"),fuenteCont2));
            celdaX.setHorizontalAlignment(Element.ALIGN_LEFT);
            celdaX.setVerticalAlignment(Element.ALIGN_MIDDLE);
            celdaX.setBorderWidthBottom(1);
            celdaX.setBorderWidthTop(0);
            celdaX.setBorderWidthRight(0);
            celdaX.setBorderWidthLeft(0);
            celdaX.setColspan(5);
            tablaX.addCell(celdaX);
            

            
            
            //columna1 fil6
            celdaX = new PdfPCell(new Paragraph("No. Operador",fuenteCont2));
            celdaX.setHorizontalAlignment(Element.ALIGN_LEFT);
            celdaX.setVerticalAlignment(Element.ALIGN_MIDDLE);
            celdaX.setBorderWidthBottom(0);
            celdaX.setBorderWidthTop(0);
            celdaX.setBorderWidthRight(0);
            celdaX.setBorderWidthLeft(0);
            tablaX.addCell(celdaX);
            
            //columna2 fil6
            celdaX = new PdfPCell(new Paragraph(datos.get("no_operador"),fuenteCont2));
            celdaX.setHorizontalAlignment(Element.ALIGN_LEFT);
            celdaX.setVerticalAlignment(Element.ALIGN_MIDDLE);
            celdaX.setBorderWidthBottom(1);
            celdaX.setBorderWidthTop(0);
            celdaX.setBorderWidthRight(0);
            celdaX.setBorderWidthLeft(0);
            tablaX.addCell(celdaX);
            
            //columna 3 fil6
            celdaX = new PdfPCell(new Paragraph("",fuenteCont2));
            celdaX.setHorizontalAlignment(Element.ALIGN_LEFT);
            celdaX.setVerticalAlignment(Element.ALIGN_MIDDLE);
            celdaX.setBorderWidthBottom(0);
            celdaX.setBorderWidthTop(0);
            celdaX.setBorderWidthRight(0);
            celdaX.setBorderWidthLeft(0);
            tablaX.addCell(celdaX);
            
            //columna 4 fil6
            celdaX = new PdfPCell(new Paragraph("Operador",fuenteCont2));
            celdaX.setHorizontalAlignment(Element.ALIGN_LEFT);
            celdaX.setVerticalAlignment(Element.ALIGN_MIDDLE);
            celdaX.setBorderWidthBottom(0);
            celdaX.setBorderWidthTop(0);
            celdaX.setBorderWidthRight(0);
            celdaX.setBorderWidthLeft(0);
            tablaX.addCell(celdaX);
            
            //columna 5-8 fil6
            celdaX = new PdfPCell(new Paragraph(datos.get("operador"),fuenteCont2));
            celdaX.setHorizontalAlignment(Element.ALIGN_LEFT);
            celdaX.setVerticalAlignment(Element.ALIGN_MIDDLE);
            celdaX.setBorderWidthBottom(1);
            celdaX.setBorderWidthTop(0);
            celdaX.setBorderWidthRight(0);
            celdaX.setBorderWidthLeft(0);
            celdaX.setColspan(4);
            tablaX.addCell(celdaX);
            
    
            
            //columna 9-17 fil6
            celdaX = new PdfPCell(new Paragraph("",fuenteCont2));
            celdaX.setHorizontalAlignment(Element.ALIGN_LEFT);
            celdaX.setVerticalAlignment(Element.ALIGN_MIDDLE);
            celdaX.setBorderWidthBottom(0);
            celdaX.setBorderWidthTop(0);
            celdaX.setBorderWidthRight(0);
            celdaX.setBorderWidthLeft(0);
            celdaX.setColspan(9);
            tablaX.addCell(celdaX);
            
            //columna 1-17 fil8 ultima fila
            celdaX = new PdfPCell(new Paragraph("",fuenteCont2));
            celdaX.setBorderWidthBottom(0);
            celdaX.setBorderWidthTop(0);
            celdaX.setBorderWidthRight(0);
            celdaX.setBorderWidthLeft(0);
            celdaX.setColspan(17);
            tablaX.addCell(celdaX); 
 
            reporte.add(tablaX);
             
     
             
             float [] widths = {1.1F,1.5F,1.5F,1.5F,3F,3F,1.3F,1.2F,1.2F,0.8F,1.2F,1.2F,0.8F,0.9F,0.8F,0.9F};//Tamaño de las Columnas.
             PdfPTable tabla = new PdfPTable(widths);
             //PdfPCell celda;
             tabla.setKeepTogether(false);
             tabla.setHeaderRows(1);
             
             //AQUI VA EL ENCABEZADO 2 DEL DOCUMENTO
             //1
             celda = new PdfPCell(new Paragraph("CLIENTE",smallBoldFont));
             celda.setUseAscender(true);
             celda.setHorizontalAlignment(Element.ALIGN_CENTER);
             celda.setUseDescender(true);
             celda.setVerticalAlignment(Element.ALIGN_MIDDLE);
             celda.setBackgroundColor(BaseColor.BLACK);            
             tabla.addCell(celda);
             
             //2
             celda = new PdfPCell(new Paragraph("No. CARGA",smallBoldFont));
             celda.setUseAscender(true);
             celda.setHorizontalAlignment(Element.ALIGN_CENTER);
             celda.setUseDescender(true);
             celda.setVerticalAlignment(Element.ALIGN_MIDDLE);
             celda.setBackgroundColor(BaseColor.BLACK);            
             tabla.addCell(celda);
             
             //3
             celda = new PdfPCell(new Paragraph("No. PEDIDO",smallBoldFont));
             celda.setUseAscender(true);
             celda.setHorizontalAlignment(Element.ALIGN_CENTER);
             celda.setUseDescender(true);
             celda.setVerticalAlignment(Element.ALIGN_MIDDLE);
             celda.setBackgroundColor(BaseColor.BLACK);            
             tabla.addCell(celda);
             
             //4
             celda = new PdfPCell(new Paragraph("No. DEST.",smallBoldFont));
             celda.setUseAscender(true);
             celda.setHorizontalAlignment(Element.ALIGN_CENTER);
             celda.setUseDescender(true);
             celda.setVerticalAlignment(Element.ALIGN_MIDDLE);
             celda.setBackgroundColor(BaseColor.BLACK);            
             tabla.addCell(celda);
             
             //5
             celda = new PdfPCell(new Paragraph("DESTINATARIO",smallBoldFont));
             celda.setUseAscender(true);
             celda.setHorizontalAlignment(Element.ALIGN_CENTER);
             celda.setUseDescender(true);
             celda.setVerticalAlignment(Element.ALIGN_MIDDLE);
             celda.setBackgroundColor(BaseColor.BLACK);            
             tabla.addCell(celda);
             
             //6
             celda = new PdfPCell(new Paragraph("POBLACIÓN",smallBoldFont));
             celda.setUseAscender(true);
             celda.setHorizontalAlignment(Element.ALIGN_CENTER);
             celda.setUseDescender(true);
             celda.setVerticalAlignment(Element.ALIGN_MIDDLE);
             celda.setBackgroundColor(BaseColor.BLACK);            
             tabla.addCell(celda);
             
             //7
             celda = new PdfPCell(new Paragraph("CANT. UNI.",smallBoldFont));
             celda.setUseAscender(true);
             celda.setHorizontalAlignment(Element.ALIGN_CENTER);
             celda.setUseDescender(true);
             celda.setVerticalAlignment(Element.ALIGN_MIDDLE);
             celda.setBackgroundColor(BaseColor.BLACK);            
             tabla.addCell(celda);
             
             //8
             celda = new PdfPCell(new Paragraph("PES0",smallBoldFont));
             celda.setUseAscender(true);
             celda.setHorizontalAlignment(Element.ALIGN_CENTER);
             celda.setUseDescender(true);
             celda.setVerticalAlignment(Element.ALIGN_MIDDLE);
             celda.setBackgroundColor(BaseColor.BLACK);            
             tabla.addCell(celda);
             
             //9
             celda = new PdfPCell(new Paragraph("VOLUMEN",smallBoldFont));
             celda.setUseAscender(true);
             celda.setHorizontalAlignment(Element.ALIGN_CENTER);
             celda.setUseDescender(true);
             celda.setVerticalAlignment(Element.ALIGN_MIDDLE);
             celda.setBackgroundColor(BaseColor.BLACK);            
             tabla.addCell(celda);
             
             /*//10
             celda = new PdfPCell(new Paragraph("ESTATUS",smallBoldFont));
             celda.setUseAscender(true);
             celda.setHorizontalAlignment(Element.ALIGN_CENTER);
             celda.setUseDescender(true);
             celda.setVerticalAlignment(Element.ALIGN_MIDDLE);
             celda.setBackgroundColor(BaseColor.BLACK);            
             tabla.addCell(celda);*/
             
             //11
             celda = new PdfPCell(new Paragraph("FACTURA",smallBoldFont));
             celda.setUseAscender(true);
             celda.setHorizontalAlignment(Element.ALIGN_CENTER);
             celda.setUseDescender(true);
             celda.setVerticalAlignment(Element.ALIGN_MIDDLE);
             celda.setBackgroundColor(BaseColor.BLACK);
             celda.setColspan(2);
             tabla.addCell(celda);
             
             //12
             celda = new PdfPCell(new Paragraph("RECIBIDO",smallBoldFont));
             celda.setUseAscender(true);
             celda.setHorizontalAlignment(Element.ALIGN_CENTER);
             celda.setUseDescender(true);
             celda.setVerticalAlignment(Element.ALIGN_MIDDLE);
             celda.setBackgroundColor(BaseColor.BLACK);            
             tabla.addCell(celda);
             
             //13
             celda = new PdfPCell(new Paragraph("FIRMA",smallBoldFont));
             celda.setUseAscender(true);
             celda.setHorizontalAlignment(Element.ALIGN_CENTER);
             celda.setUseDescender(true);
             celda.setVerticalAlignment(Element.ALIGN_MIDDLE);
             celda.setBackgroundColor(BaseColor.BLACK);            
             tabla.addCell(celda);
             
             //14
             celda = new PdfPCell(new Paragraph("SELLO",smallBoldFont));
             celda.setUseAscender(true);
             celda.setHorizontalAlignment(Element.ALIGN_CENTER);
             celda.setUseDescender(true);
             celda.setVerticalAlignment(Element.ALIGN_MIDDLE);
             celda.setBackgroundColor(BaseColor.BLACK);            
             tabla.addCell(celda);
             
             //15
             celda = new PdfPCell(new Paragraph("Ch/",smallBoldFont));
             celda.setUseAscender(true);
             celda.setHorizontalAlignment(Element.ALIGN_CENTER);
             celda.setUseDescender(true);
             celda.setVerticalAlignment(Element.ALIGN_MIDDLE);
             celda.setBackgroundColor(BaseColor.BLACK);            
             tabla.addCell(celda);
  
             //16
             celda = new PdfPCell(new Paragraph("EFTVO.",smallBoldFont));
             celda.setUseAscender(true);
             celda.setHorizontalAlignment(Element.ALIGN_CENTER);
             celda.setUseDescender(true);
             celda.setVerticalAlignment(Element.ALIGN_MIDDLE);
             celda.setBackgroundColor(BaseColor.BLACK);            
             tabla.addCell(celda);
             
             
             
             Integer nopedidos=0;
             Integer nofacturas=0;
             Double total_unidades=0.0;
             Double total_peso=0.0;
             Double total_volumen=0.0;
             
             for (int j=0;j<lista.size();j++){
                 HashMap<String,String> registro = lista.get(j);

                 //1
                 celda = new PdfPCell(new Paragraph(registro.get("no_clie"),fuenteCont));
                 celda.setUseAscender(true);
                 celda.setHorizontalAlignment(Element.ALIGN_CENTER);
                 celda.setUseDescender(true);
                 celda.setVerticalAlignment(Element.ALIGN_MIDDLE);
                 tabla.addCell(celda);
                 
                 //2
                 celda = new PdfPCell(new Paragraph(registro.get("no_carga"),fuenteCont));
                 celda.setUseAscender(true);
                 celda.setHorizontalAlignment(Element.ALIGN_CENTER);
                 celda.setUseDescender(true);
                 celda.setVerticalAlignment(Element.ALIGN_MIDDLE);
                 tabla.addCell(celda);
                 
                 //3
                 celda = new PdfPCell(new Paragraph(registro.get("no_pedido"),fuenteCont));
                 celda.setUseAscender(true);
                 celda.setHorizontalAlignment(Element.ALIGN_LEFT);
                 celda.setUseDescender(true);
                 celda.setVerticalAlignment(Element.ALIGN_MIDDLE);
                 tabla.addCell(celda);
                 
                 //4
                 celda = new PdfPCell(new Paragraph(registro.get("no_dest"),fuenteCont));
                 celda.setUseAscender(true);
                 celda.setHorizontalAlignment(Element.ALIGN_CENTER);
                 celda.setUseDescender(true);
                 celda.setVerticalAlignment(Element.ALIGN_MIDDLE);
                 tabla.addCell(celda);
                 
                 //5
                 celda = new PdfPCell(new Paragraph(registro.get("nombre_dest"),fuenteCont));
                 celda.setUseAscender(true);
                 celda.setHorizontalAlignment(Element.ALIGN_LEFT);
                 celda.setUseDescender(true);
                 celda.setVerticalAlignment(Element.ALIGN_MIDDLE);
                 tabla.addCell(celda);
                 
                 //6
                 celda = new PdfPCell(new Paragraph(registro.get("municipio"),fuenteCont));
                 celda.setUseAscender(true);
                 celda.setHorizontalAlignment(Element.ALIGN_LEFT);
                 celda.setUseDescender(true);
                 celda.setVerticalAlignment(Element.ALIGN_MIDDLE);
                 tabla.addCell(celda);
                 
                //7
                 String cant_uni="";
                 
                 if ( Double.parseDouble(registro.get("cant_uni")) >0){
                     cant_uni = StringHelper.AgregaComas(registro.get("cant_uni"));
                 }
                 
                 celda = new PdfPCell(new Paragraph(cant_uni,fuenteCont));
                 celda.setUseAscender(true);
                 celda.setHorizontalAlignment(Element.ALIGN_RIGHT);
                 celda.setUseDescender(true);
                 celda.setVerticalAlignment(Element.ALIGN_MIDDLE);     
                 tabla.addCell(celda);
                 
                //8
                 String cant="";
                 
                 if ( Double.parseDouble(registro.get("peso")) >0){
                     cant = StringHelper.AgregaComas(registro.get("peso"));
                 }
                 
                 celda = new PdfPCell(new Paragraph(cant,fuenteCont));
                 celda.setUseAscender(true);
                 celda.setHorizontalAlignment(Element.ALIGN_RIGHT);
                 celda.setUseDescender(true);
                 celda.setVerticalAlignment(Element.ALIGN_MIDDLE);     
                 tabla.addCell(celda);
                 
                 //9
                 String vol="";
                 
                 if ( Double.parseDouble(registro.get("volumen")) >0){
                     vol = StringHelper.AgregaComas(registro.get("volumen"));
                 }
                 
                 celda = new PdfPCell(new Paragraph(vol,fuenteCont));
                 celda.setUseAscender(true);
                 celda.setHorizontalAlignment(Element.ALIGN_RIGHT);
                 celda.setUseDescender(true);
                 celda.setVerticalAlignment(Element.ALIGN_MIDDLE);     
                 tabla.addCell(celda);
                 
                 /*//10
                 celda = new PdfPCell(new Paragraph(registro.get("status_det"),fuenteCont));
                 celda.setUseAscender(true);
                 celda.setHorizontalAlignment(Element.ALIGN_CENTER);
                 celda.setUseDescender(true);
                 celda.setVerticalAlignment(Element.ALIGN_MIDDLE);     
                 tabla.addCell(celda);*/
                 
                 //11
                 celda = new PdfPCell(new Paragraph(registro.get("no_fac"),fuenteCont));
                 celda.setUseAscender(true);
                 celda.setHorizontalAlignment(Element.ALIGN_CENTER);
                 celda.setUseDescender(true);
                 celda.setVerticalAlignment(Element.ALIGN_MIDDLE);
                 celda.setColspan(2);
                 tabla.addCell(celda); 
                 
                 //12
                 celda = new PdfPCell(new Paragraph( "",fuenteCont));
                 celda.setUseAscender(true);
                 celda.setHorizontalAlignment(Element.ALIGN_LEFT);
                 celda.setUseDescender(true);
                 celda.setVerticalAlignment(Element.ALIGN_MIDDLE);     
                 tabla.addCell(celda);
                 
                 //13
                 celda = new PdfPCell(new Paragraph(registro.get("firma"),fuenteCont));
                 celda.setUseAscender(true);
                 celda.setHorizontalAlignment(Element.ALIGN_CENTER);
                 celda.setUseDescender(true);
                 celda.setVerticalAlignment(Element.ALIGN_MIDDLE);     
                 tabla.addCell(celda);
                 
                 //14
                 celda = new PdfPCell(new Paragraph(registro.get("sello"),fuenteCont));
                 celda.setUseAscender(true);
                 celda.setHorizontalAlignment(Element.ALIGN_CENTER);
                 celda.setUseDescender(true);
                 celda.setVerticalAlignment(Element.ALIGN_MIDDLE);     
                 tabla.addCell(celda);
                 
                 //15
                 celda = new PdfPCell(new Paragraph(registro.get("cheque"),fuenteCont));
                 celda.setUseAscender(true);
                 celda.setHorizontalAlignment(Element.ALIGN_CENTER);
                 celda.setUseDescender(true);
                 celda.setVerticalAlignment(Element.ALIGN_MIDDLE);     
                 tabla.addCell(celda);
      
                 //16
                 celda = new PdfPCell(new Paragraph(registro.get("efectivo"),fuenteCont));
                 celda.setHorizontalAlignment(Element.ALIGN_CENTER);
                 celda.setVerticalAlignment(Element.ALIGN_MIDDLE);
                 celda.setUseDescender(true);
                 celda.setUseAscender(true);
                 tabla.addCell(celda);
                 
                 
                 //Integer nopedido = Integer.parseInt(registro.get("nopedidos"));
                 //Contar pedidos
                 nopedidos++;
                 
                 //Contar facturas
                 nofacturas = nofacturas + Integer.parseInt(registro.get("nofacturas"));
                 total_unidades = total_unidades + Double.parseDouble(registro.get("cant_uni"));
                 total_peso = total_peso + Double.parseDouble(registro.get("peso"));
                 total_volumen = total_volumen + Double.parseDouble(registro.get("volumen"));
            }
             
            //ESTO ABARCA DE LA COLUMNA 1 A LA 5
            celda = new PdfPCell(new Paragraph("No. Pedidos: "+StringHelper.AgregaComas(StringHelper.roundDouble(nopedidos, 0)) +"     No. de Facturas: "+StringHelper.AgregaComas(StringHelper.roundDouble(nofacturas, 0)),fuenteCont));
            celda.setColspan(5);
            celda.setHorizontalAlignment(Element.ALIGN_LEFT);
            celda.setBorder(0);
            tabla.addCell(celda);
            
            //COLUMNA 6
            celda = new PdfPCell(new Paragraph("TOTAL",fuenteCont));
            celda.setHorizontalAlignment(Element.ALIGN_RIGHT);
            celda.setBorder(0);
            tabla.addCell(celda);
            
            //COLUMNA 7
            celda = new PdfPCell(new Paragraph(StringHelper.AgregaComas(StringHelper.roundDouble(total_unidades, 2)),fuenteCont));
            celda.setHorizontalAlignment(Element.ALIGN_RIGHT);
            celda.setVerticalAlignment(Element.ALIGN_MIDDLE);    
            celda.setUseAscender(true);
            celda.setUseDescender(true);
            tabla.addCell(celda);

            //COLUMNA 8
            celda = new PdfPCell(new Paragraph(StringHelper.AgregaComas(StringHelper.roundDouble(total_peso, 2)),fuenteCont));
            celda.setHorizontalAlignment(Element.ALIGN_RIGHT);
            celda.setVerticalAlignment(Element.ALIGN_MIDDLE);
            celda.setUseAscender(true);
            celda.setUseDescender(true);
            tabla.addCell(celda);
             
            //COLUMNA 9
            celda = new PdfPCell(new Paragraph(StringHelper.AgregaComas(StringHelper.roundDouble(total_volumen, 2)),fuenteCont));
            celda.setHorizontalAlignment(Element.ALIGN_RIGHT);
            celda.setVerticalAlignment(Element.ALIGN_MIDDLE);
            celda.setUseAscender(true);
            celda.setUseDescender(true);
            tabla.addCell(celda);
            
             //columna 10-16 ultima fila
            celda = new PdfPCell(new Paragraph(" ",fuenteCont2));
            celda.setBorder(0);
            celda.setColspan(7);
            tabla.addCell(celda);
            
             //fila vacia 1-16
            celda = new PdfPCell(new Paragraph(" ",fuenteCont2));
            celda.setBorder(0);
            celda.setColspan(16);
            celda.setFixedHeight(15);
            tabla.addCell(celda);
            
            reporte.add(tabla);
            
            
            
            
            
            //Verificar si existen servicios adicionales
            if(serv_adic.size() > 0){
                float [] widths1 = {2.5F,5.5F,1F,1.5F,12F};//Tamaño de las Columnas.
                PdfPTable tablaservadic = new PdfPTable(widths1);
                tablaservadic.setKeepTogether(false);
                tablaservadic.setHeaderRows(2);
                
                //COLUMNA 1-5
                celda = new PdfPCell(new Paragraph("SERVICIOS ADICIONALES",fuenteCont2));
                celda.setHorizontalAlignment(Element.ALIGN_LEFT);
                celda.setVerticalAlignment(Element.ALIGN_MIDDLE);
                celda.setBorderWidthBottom(0);
                celda.setBorderWidthTop(0);
                celda.setBorderWidthRight(0);
                celda.setBorderWidthLeft(0);
                celda.setColspan(5);
                tablaservadic.addCell(celda);

                //COLUMNA 1
                celda = new PdfPCell(new Paragraph("CODIGO",smallBoldFont));
                celda.setUseAscender(true);
                celda.setUseDescender(true);
                celda.setHorizontalAlignment(Element.ALIGN_CENTER);
                celda.setVerticalAlignment(Element.ALIGN_MIDDLE);
                celda.setBackgroundColor(BaseColor.BLACK);
                //celda.setColspan(2);
                tablaservadic.addCell(celda);

                //COLUMNA 2
                celda = new PdfPCell(new Paragraph("DESCRIPCIÓN",smallBoldFont));
                celda.setUseAscender(true);
                celda.setHorizontalAlignment(Element.ALIGN_LEFT);
                celda.setUseDescender(true);
                celda.setVerticalAlignment(Element.ALIGN_MIDDLE);
                celda.setBackgroundColor(BaseColor.BLACK);
                //celda.setColspan(3);
                tablaservadic.addCell(celda);

                //COLUMNA 3
                celda = new PdfPCell(new Paragraph("",smallBoldFont));
                celda.setUseAscender(true);
                celda.setHorizontalAlignment(Element.ALIGN_CENTER);
                celda.setUseDescender(true);
                celda.setVerticalAlignment(Element.ALIGN_MIDDLE);
                celda.setBackgroundColor(BaseColor.BLACK);
                tablaservadic.addCell(celda);

                //COLUMNA 4
                celda = new PdfPCell(new Paragraph("IMPORTE",smallBoldFont));
                celda.setUseAscender(true);
                celda.setHorizontalAlignment(Element.ALIGN_CENTER);
                celda.setUseDescender(true);
                celda.setVerticalAlignment(Element.ALIGN_MIDDLE);
                celda.setBackgroundColor(BaseColor.BLACK);
                tablaservadic.addCell(celda);

                //columna 5
                celda = new PdfPCell(new Paragraph("",fuenteCont2));
                celda.setBorderWidthBottom(0);
                celda.setBorderWidthTop(0);
                celda.setBorderWidthRight(0);
                celda.setBorderWidthLeft(0);
                tablaservadic.addCell(celda); 


                Double total_serv_adic=0.0;

                for (int j=0;j<serv_adic.size();j++){
                    HashMap<String,Object> registro = serv_adic.get(j);

                    //COLUMNA 1
                    celda = new PdfPCell(new Paragraph((String) registro.get("codigo"),fuenteCont));
                    celda.setUseAscender(true);
                    celda.setHorizontalAlignment(Element.ALIGN_LEFT);
                    celda.setUseDescender(true);
                    celda.setVerticalAlignment(Element.ALIGN_MIDDLE);
                    tablaservadic.addCell(celda);

                    //COLUMNA 2
                    celda = new PdfPCell(new Paragraph((String) registro.get("descripcion"),fuenteCont));
                    celda.setUseAscender(true);
                    celda.setHorizontalAlignment(Element.ALIGN_LEFT);
                    celda.setUseDescender(true);
                    celda.setVerticalAlignment(Element.ALIGN_MIDDLE);
                    tablaservadic.addCell(celda);

                    //COLUMNA 3
                    celda = new PdfPCell(new Paragraph("$",fuenteCont));
                    celda.setHorizontalAlignment(Element.ALIGN_RIGHT);
                    //celda.setUseDescender(true);
                    celda.setVerticalAlignment(Element.ALIGN_MIDDLE);
                    celda.setBorderWidthBottom(0);
                    //celda.setBorderWidthTop(1);
                    celda.setBorderWidthRight(0);
                    //celda.setBorderWidthLeft(1);
                    tablaservadic.addCell(celda);

                    //COLUMNA 4
                    celda = new PdfPCell(new Paragraph((String) registro.get("precio"),fuenteCont));
                    celda.setUseAscender(true);
                    celda.setHorizontalAlignment(Element.ALIGN_RIGHT);
                    celda.setUseDescender(true);
                    celda.setVerticalAlignment(Element.ALIGN_MIDDLE);
                    celda.setBorderWidthLeft(0);
                    celda.setBorderWidthBottom(0);
                    tablaservadic.addCell(celda);

                    //columna 5
                    celda = new PdfPCell(new Paragraph("",fuenteCont2));
                    celda.setBorderWidthBottom(0);
                    celda.setBorderWidthTop(0);
                    celda.setBorderWidthRight(0);
                    celda.setBorderWidthLeft(0);
                    tablaservadic.addCell(celda); 

                    Double total = Double.parseDouble((String) registro.get("precio"));
                    total_serv_adic = total_serv_adic + total;
                }
             
                //COLUMNA 1-2
                celda = new PdfPCell(new Paragraph("TOTAL",fuenteCont));
                celda.setHorizontalAlignment(Element.ALIGN_RIGHT);
                celda.setVerticalAlignment(Element.ALIGN_MIDDLE);   
                celda.setBorderWidthBottom(0);
                celda.setBorderWidthTop(0);
                celda.setBorderWidthRight(0);
                celda.setBorderWidthLeft(0);
                celda.setColspan(2);
                tablaservadic.addCell(celda);

                //COLUMNA 3
                celda = new PdfPCell(new Paragraph("$",fuenteCont));
                //celda.setUseAscender(true);
                celda.setHorizontalAlignment(Element.ALIGN_RIGHT);
                //celda.setUseDescender(true);
                celda.setVerticalAlignment(Element.ALIGN_MIDDLE);
                celda.setBorderWidthRight(0);
                tablaservadic.addCell(celda);

                //COLUMNA 4
                celda = new PdfPCell(new Paragraph(StringHelper.AgregaComas(StringHelper.roundDouble(total_serv_adic, 2)),fuenteCont));
                celda.setHorizontalAlignment(Element.ALIGN_RIGHT);
                celda.setVerticalAlignment(Element.ALIGN_MIDDLE);
                celda.setBorderWidthLeft(0);
                tablaservadic.addCell(celda);
                
                //columna 5
                celda = new PdfPCell(new Paragraph("",fuenteCont2));
                celda.setBorder(0);
                tablaservadic.addCell(celda); 
                
                
                //Fila vacia
                celda = new PdfPCell(new Paragraph("",fuenteCont2));
                celda.setBorder(0);
                celda.setColspan(5);
                celda.setFixedHeight(15);
                tablaservadic.addCell(celda); 
                
                reporte.add(tablaservadic);
            }
            
            
            
            if (!datos.get("observaciones").isEmpty()){
                float [] widths2 = {1};//Tamaño de las Columnas.
                PdfPTable tabla_obser = new PdfPTable(widths2);
                tabla_obser.setKeepTogether(false);
                
                //columna1 
                celda = new PdfPCell(new Paragraph("OBSERVACIONES",fuenteCont2));
                celda.setHorizontalAlignment(Element.ALIGN_LEFT);
                celda.setVerticalAlignment(Element.ALIGN_MIDDLE);
                celda.setBorder(0);
                tabla_obser.addCell(celda);

                //columna2-5 
                celda = new PdfPCell(new Paragraph(datos.get("observaciones"),fuenteCont));
                celda.setHorizontalAlignment(Element.ALIGN_LEFT);
                celda.setVerticalAlignment(Element.ALIGN_MIDDLE);
                celda.setUseAscender(true);
                celda.setUseDescender(true);
                celda.setColspan(5);
                tabla_obser.addCell(celda);
                
                //fila en blanco
                celda = new PdfPCell(new Paragraph("",fuenteCont2));
                celda.setBorder(0);
                celda.setFixedHeight(20);
                tabla_obser.addCell(celda);
                
                reporte.add(tabla_obser);
            }
            

            
            
            
            
            
            float [] widths2 = {2.5F,5.5F,1F,1.5F,2F,10F};//Tamaño de las Columnas.
            PdfPTable tabla3 = new PdfPTable(widths2);
            tabla3.setKeepTogether(false);
             //tabla3.setHeaderRows(2);
            
            //columna 1-6
            celda = new PdfPCell(new Paragraph(datos.get("operador"),fuenteCont2));
            celda.setHorizontalAlignment(Element.ALIGN_CENTER);
            celda.setVerticalAlignment(Element.ALIGN_MIDDLE);
            celda.setBorderWidthBottom(0);
            celda.setBorderWidthTop(0);
            celda.setBorderWidthRight(0);
            celda.setBorderWidthLeft(0);
            celda.setColspan(6);
            tabla3.addCell(celda);
             
            //columna 1-6
            celda = new PdfPCell(new Paragraph("_________________________________________",fuenteCont2));
            celda.setHorizontalAlignment(Element.ALIGN_CENTER);
            celda.setVerticalAlignment(Element.ALIGN_MIDDLE);
            celda.setBorderWidthBottom(0);
            celda.setBorderWidthTop(0);
            celda.setBorderWidthRight(0);
            celda.setBorderWidthLeft(0);
            celda.setColspan(6);
            tabla3.addCell(celda);
             
            //columna 1-6
            celda = new PdfPCell(new Paragraph("R E C I B I",fuenteCont2));
            celda.setHorizontalAlignment(Element.ALIGN_CENTER);
            celda.setVerticalAlignment(Element.ALIGN_MIDDLE);
            celda.setBorderWidthBottom(0);
            celda.setBorderWidthTop(0);
            celda.setBorderWidthRight(0);
            celda.setBorderWidthLeft(0);
            celda.setColspan(6);
            tabla3.addCell(celda);
            
            //columna 1-6
            celda= new PdfPCell(new Paragraph("NOMBRE Y FIRMA CHOFER",fuenteCont2));
            celda.setHorizontalAlignment(Element.ALIGN_CENTER);
            celda.setVerticalAlignment(Element.ALIGN_MIDDLE);
            celda.setBorderWidthBottom(0);
            celda.setBorderWidthTop(0);
            celda.setBorderWidthRight(0);
            celda.setBorderWidthLeft(0);
            celda.setColspan(6);
            tabla3.addCell(celda);
            
            //ESTO ES PARA DEJAR UNA FILA EN BLANCO
            celda = new PdfPCell(new Paragraph(""));
            celda.setFixedHeight(15);
            celda.setColspan(6);
            celda.setBorder(0);
            tabla3.addCell(celda);
            

            
            //columna1-5 
            celda = new PdfPCell(new Paragraph("A LAS ________ HORAS DEL DIA ________ DEL MES DE ________________ DE 20______, RECIBI DE:",fuenteCont));
            celda.setHorizontalAlignment(Element.ALIGN_LEFT);
            celda.setVerticalAlignment(Element.ALIGN_MIDDLE);
            celda.setBorderWidthBottom(0);
            celda.setBorderWidthTop(0);
            celda.setBorderWidthRight(0);
            celda.setBorderWidthLeft(0);
            celda.setColspan(5);
            tabla3.addCell(celda);
            
            //columna 6
            celda = new PdfPCell(new Paragraph(datosEncabezadoPie.get("nombre_empresa_emisora"),fuenteCont));
            celda.setHorizontalAlignment(Element.ALIGN_LEFT);
            celda.setVerticalAlignment(Element.ALIGN_MIDDLE);
            celda.setBorderWidthBottom(0);
            celda.setBorderWidthTop(0);
            celda.setBorderWidthRight(0);
            celda.setBorderWidthLeft(0);
            tabla3.addCell(celda);
            
            //columna 1-6
            celda = new PdfPCell(new Paragraph("LAS FACTURAS ORIGINALES ASI COMO LOS PRODUCTOS Y MERCANCIAS QUE AMPARAN A DICHAS FACTURAS Y LAS CUALES SE DETALLAN Y ESPECIFICAN EN LA PRESENTE RELACION DE REPARTOS  A EFECTO DE QUE, EN CUMPLIMIENTO DE MI TRABAJO"
                    + ", HAGA ENTREGA A LOS DESTINATARIOS QUE SE INDICAN LOS PRODUCTOS Y MERCANCIAS  A QUE SE HA HECHO ALUSION, OBLIGANDOME A DEVOLVER DEBIDAMENTE FIRMADAS Y SELLADAS POR EL DESTINATARIO LAS FACTURAS ORIGINALES A:",fuenteCont));
            celda.setHorizontalAlignment(Element.ALIGN_JUSTIFIED);
            celda.setVerticalAlignment(Element.ALIGN_MIDDLE);
            celda.setBorderWidthBottom(0);
            celda.setBorderWidthTop(0);
            celda.setBorderWidthRight(0);
            celda.setBorderWidthLeft(0);
            celda.setColspan(6);
            tabla3.addCell(celda);
            
            //columna1-6
            celda = new PdfPCell(new Paragraph(datosEncabezadoPie.get("nombre_empresa_emisora"),fuenteCont));
            celda.setHorizontalAlignment(Element.ALIGN_LEFT);
            celda.setVerticalAlignment(Element.ALIGN_MIDDLE);
            celda.setBorderWidthBottom(0);
            celda.setBorderWidthTop(0);
            celda.setBorderWidthRight(0);
            celda.setBorderWidthLeft(0);
            celda.setColspan(6);
            tabla3.addCell(celda);
            
            
           
            reporte.add(tabla3);
             
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
