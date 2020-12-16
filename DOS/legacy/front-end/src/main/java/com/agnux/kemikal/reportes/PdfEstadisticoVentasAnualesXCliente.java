/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.agnux.kemikal.reportes;
import com.agnux.common.helpers.StringHelper;
import com.agnux.common.helpers.TimeHelper;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.ExceptionConverter;
import com.itextpdf.text.Font;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.ColumnText;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfPageEventHelper;
import com.itextpdf.text.pdf.PdfTemplate;
import com.itextpdf.text.pdf.PdfWriter;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
/**
 *
 * @author Ezequiel Cruz
 */

public class PdfEstadisticoVentasAnualesXCliente {

    public PdfEstadisticoVentasAnualesXCliente(String fileout, String ruta_imagen, String razon_social_empresa, String mes_inicial, String mes_final, ArrayList<HashMap<String, String>> lista_ventas_anuales) throws FileNotFoundException, DocumentException {
        HashMap<String, String> datos = new HashMap<String, String>();
        Font smallBoldFont = new Font(Font.getFamily("ARIAL"),7,Font.NORMAL,BaseColor.WHITE);
        Font fuenteCont = new Font(Font.getFamily("ARIAL"),7,Font.NORMAL,BaseColor.WHITE);
        Font fuenteCont2 = new Font(Font.getFamily("ARIAL"),7,Font.NORMAL,BaseColor.BLACK);
        String mesIni =TimeHelper.ConvertNumToMonth(Integer.parseInt(mes_inicial));
        String mesFin =TimeHelper.ConvertNumToMonth(Integer.parseInt(mes_final));         
        String periodo = "Periodo del mes de:"+" "+ mesIni +" al mes de: "+" "+mesFin;
        
        double total_anual = 0.0;
        double total_enero = 0.0;
        double total_febrero = 0.0;
        double total_marzo = 0.0;
        double total_abril = 0.0;
        double total_mayo = 0.0;
        double total_junio = 0.0;
        double total_julio = 0.0;
        double total_agosto = 0.0;
        double total_septiembre = 0.0;
        double total_octubre = 0.0;
        double total_noviembre = 0.0;
        double total_diciembre = 0.0;
        
        //datos para el encabezado
        datos.put("empresa", razon_social_empresa);
        datos.put("titulo_reporte", "Reporte Estadístico de Ventas Anuales por Cliente");
        datos.put("periodo", periodo);
        
        //datos para el pie de pagina
        datos.put("codigo1", "");
        datos.put("codigo2", "");
         
        HeaderFooter event = new HeaderFooter(datos);
        Document reporte = new Document(PageSize.LETTER.rotate(),-50,-50,60,30);  
        PdfWriter writer = PdfWriter.getInstance(reporte, new FileOutputStream(fileout));
        writer.setPageEvent(event);
        
        try {
            
            reporte.open();
            float [] widths = {
                0.3f,2,
                0.3f,2,
                0.3f,2,
                0.3f,2,
                0.3f,2,
                0.3f,2,
                0.3f,2,
                0.3f,2,
                0.3f,2,
                0.3f,2,
                0.3f,2,
                0.3f,2,
                0.3f,2};//TamaÃ±o de las Columnas.
            PdfPTable tabla = new PdfPTable(widths);
            PdfPCell celda;
            tabla.setKeepTogether(false);
            tabla.setHeaderRows(1);
            
            //1           
            celda = new PdfPCell(new Paragraph("ENERO",smallBoldFont));
            celda.setUseAscender(true);
            celda.setHorizontalAlignment(Element.ALIGN_CENTER);
            celda.setUseDescender(true);
            celda.setVerticalAlignment(Element.ALIGN_MIDDLE);
            celda.setBackgroundColor(BaseColor.BLACK);
            celda.setColspan(2);
            tabla.addCell(celda);
            //2          
            celda = new PdfPCell(new Paragraph("FEBRERO",smallBoldFont));
            celda.setUseAscender(true);
            celda.setHorizontalAlignment(Element.ALIGN_CENTER);
            celda.setUseDescender(true);
            celda.setVerticalAlignment(Element.ALIGN_MIDDLE);
            celda.setBackgroundColor(BaseColor.BLACK);
            celda.setColspan(2);
            tabla.addCell(celda);
            //3  
            celda = new PdfPCell(new Paragraph("MARZO",smallBoldFont));
            celda.setUseAscender(true);
            celda.setHorizontalAlignment(Element.ALIGN_CENTER);
            celda.setUseDescender(true);
            celda.setVerticalAlignment(Element.ALIGN_MIDDLE);
            celda.setBackgroundColor(BaseColor.BLACK);
            celda.setColspan(2);
            tabla.addCell(celda);
            //4 
            celda = new PdfPCell(new Paragraph("ABRIL",smallBoldFont));
            celda.setUseAscender(true);
            celda.setHorizontalAlignment(Element.ALIGN_CENTER);
            celda.setUseDescender(true);
            celda.setVerticalAlignment(Element.ALIGN_MIDDLE);
            celda.setBackgroundColor(BaseColor.BLACK);
            celda.setColspan(2);
            tabla.addCell(celda);
            //5
            celda = new PdfPCell(new Paragraph("MAYO",smallBoldFont));
            celda.setUseAscender(true);
            celda.setHorizontalAlignment(Element.ALIGN_CENTER);
            celda.setUseDescender(true);
            celda.setVerticalAlignment(Element.ALIGN_MIDDLE);
            celda.setBackgroundColor(BaseColor.BLACK);
            celda.setColspan(2);
            tabla.addCell(celda);
            //6
            celda = new PdfPCell(new Paragraph("JUNIO",smallBoldFont));
            celda.setUseAscender(true);
            celda.setHorizontalAlignment(Element.ALIGN_CENTER);
            celda.setUseDescender(true);
            celda.setVerticalAlignment(Element.ALIGN_MIDDLE);
            celda.setBackgroundColor(BaseColor.BLACK);
            celda.setColspan(2);
            tabla.addCell(celda);
            //7
            celda = new PdfPCell(new Paragraph("JULIO",smallBoldFont));
            celda.setUseAscender(true);
            celda.setHorizontalAlignment(Element.ALIGN_CENTER);
            celda.setUseDescender(true);
            celda.setVerticalAlignment(Element.ALIGN_MIDDLE);
            celda.setBackgroundColor(BaseColor.BLACK);
            celda.setColspan(2);
            tabla.addCell(celda);
            //8
            celda = new PdfPCell(new Paragraph("AGOSTO",smallBoldFont));
            celda.setUseAscender(true);
            celda.setHorizontalAlignment(Element.ALIGN_CENTER);
            celda.setUseDescender(true);
            celda.setVerticalAlignment(Element.ALIGN_MIDDLE);
            celda.setBackgroundColor(BaseColor.BLACK);
            celda.setColspan(2);
            tabla.addCell(celda);
            //9
            celda = new PdfPCell(new Paragraph("SEPTIEMBRE",smallBoldFont));
            celda.setUseAscender(true);
            celda.setHorizontalAlignment(Element.ALIGN_CENTER);
            celda.setUseDescender(true);
            celda.setVerticalAlignment(Element.ALIGN_MIDDLE);
            celda.setBackgroundColor(BaseColor.BLACK);
            celda.setColspan(2);
            tabla.addCell(celda);
            //10
            celda = new PdfPCell(new Paragraph("OCTUBRE",smallBoldFont));
            celda.setUseAscender(true);
            celda.setHorizontalAlignment(Element.ALIGN_CENTER);
            celda.setUseDescender(true);
            celda.setVerticalAlignment(Element.ALIGN_MIDDLE);
            celda.setBackgroundColor(BaseColor.BLACK);
            celda.setColspan(2);
            tabla.addCell(celda);
            //11
            celda = new PdfPCell(new Paragraph("NOVIEMBRE",smallBoldFont));
            celda.setUseAscender(true);
            celda.setHorizontalAlignment(Element.ALIGN_CENTER);
            celda.setUseDescender(true);
            celda.setVerticalAlignment(Element.ALIGN_MIDDLE);
            celda.setBackgroundColor(BaseColor.BLACK);
            celda.setColspan(2);
            tabla.addCell(celda);
            
            //12
            celda = new PdfPCell(new Paragraph("DICIEMBRE",smallBoldFont));
            celda.setUseAscender(true);
            celda.setHorizontalAlignment(Element.ALIGN_CENTER);
            celda.setUseDescender(true);
            celda.setVerticalAlignment(Element.ALIGN_MIDDLE);
            celda.setBackgroundColor(BaseColor.BLACK);
            celda.setColspan(2);
            tabla.addCell(celda);
            
            //13
            celda = new PdfPCell(new Paragraph("TOTAL ANUAL",smallBoldFont));
            celda.setUseAscender(true);
            celda.setHorizontalAlignment(Element.ALIGN_CENTER);
            celda.setUseDescender(true);
            celda.setVerticalAlignment(Element.ALIGN_MIDDLE);
            celda.setBackgroundColor(BaseColor.BLACK);
            celda.setColspan(2);
            tabla.addCell(celda);
            
            for (int j=0;j<lista_ventas_anuales.size();j++){
                 HashMap<String,String> registro = lista_ventas_anuales.get(j);
                 
                 //1-13           
                 celda = new PdfPCell(new Paragraph(registro.get("razon_social"),fuenteCont2));
                 celda.setUseAscender(true);
                 celda.setHorizontalAlignment(Element.ALIGN_LEFT);
                 celda.setUseDescender(true);
                 celda.setVerticalAlignment(Element.ALIGN_MIDDLE);
                 celda.setColspan(26);
                 celda.setBorder(0);
                 tabla.addCell(celda);
                 
                 //1
                 celda = new PdfPCell(new Paragraph("$",fuenteCont2));
                 celda.setUseAscender(true);
                 celda.setHorizontalAlignment(Element.ALIGN_RIGHT);
                 celda.setUseDescender(true);
                 celda.setVerticalAlignment(Element.ALIGN_MIDDLE);
                 celda.setBorder(0);
                 tabla.addCell(celda);
                 
                 celda = new PdfPCell(new Paragraph(StringHelper.AgregaComas(StringHelper.roundDouble(registro.get("enero"),2)),fuenteCont2));
                 celda.setUseAscender(true);
                 celda.setHorizontalAlignment(Element.ALIGN_RIGHT);
                 celda.setUseDescender(true);
                 celda.setVerticalAlignment(Element.ALIGN_MIDDLE);
                 celda.setBorder(0);
                 tabla.addCell(celda);
                 //2          
                 celda = new PdfPCell(new Paragraph("$",fuenteCont2));
                 celda.setUseAscender(true);
                 celda.setHorizontalAlignment(Element.ALIGN_RIGHT);
                 celda.setUseDescender(true);
                 celda.setVerticalAlignment(Element.ALIGN_MIDDLE);
                 celda.setBorder(0);
                 tabla.addCell(celda);
                 
                 celda = new PdfPCell(new Paragraph(StringHelper.AgregaComas(StringHelper.roundDouble(registro.get("febrero"),2)),fuenteCont2));
                 celda.setUseAscender(true);
                 celda.setHorizontalAlignment(Element.ALIGN_RIGHT);
                 celda.setUseDescender(true);
                 celda.setVerticalAlignment(Element.ALIGN_MIDDLE);
                 celda.setBorder(0);
                 tabla.addCell(celda);
                 //3
                 celda = new PdfPCell(new Paragraph("$",fuenteCont2));
                 celda.setUseAscender(true);
                 celda.setHorizontalAlignment(Element.ALIGN_RIGHT);
                 celda.setUseDescender(true);
                 celda.setVerticalAlignment(Element.ALIGN_MIDDLE);
                 celda.setBorder(0);
                 tabla.addCell(celda);
                 
                 celda = new PdfPCell(new Paragraph(StringHelper.AgregaComas(StringHelper.roundDouble(registro.get("marzo"),2)),fuenteCont2));
                 celda.setUseAscender(true);
                 celda.setHorizontalAlignment(Element.ALIGN_RIGHT);
                 celda.setUseDescender(true);
                 celda.setVerticalAlignment(Element.ALIGN_MIDDLE);
                 celda.setBorder(0);
                 tabla.addCell(celda);
                 
                 //4           
                 celda = new PdfPCell(new Paragraph("$",fuenteCont2));
                 celda.setUseAscender(true);
                 celda.setHorizontalAlignment(Element.ALIGN_RIGHT);
                 celda.setUseDescender(true);
                 celda.setVerticalAlignment(Element.ALIGN_MIDDLE);
                 celda.setBorder(0);
                 tabla.addCell(celda);
                 
                 celda = new PdfPCell(new Paragraph(StringHelper.AgregaComas(StringHelper.roundDouble(registro.get("abril"),2)),fuenteCont2));
                 celda.setUseAscender(true);
                 celda.setHorizontalAlignment(Element.ALIGN_RIGHT);
                 celda.setUseDescender(true);
                 celda.setVerticalAlignment(Element.ALIGN_MIDDLE);
                 celda.setBorder(0);
                 tabla.addCell(celda);
                 //5
                 celda = new PdfPCell(new Paragraph("$",fuenteCont2));
                 celda.setUseAscender(true);
                 celda.setHorizontalAlignment(Element.ALIGN_RIGHT);
                 celda.setUseDescender(true);
                 celda.setVerticalAlignment(Element.ALIGN_MIDDLE);
                 celda.setBorder(0);
                 tabla.addCell(celda);
                 
                 celda = new PdfPCell(new Paragraph(StringHelper.AgregaComas(StringHelper.roundDouble(registro.get("mayo"),2)),fuenteCont2));
                 celda.setUseAscender(true);
                 celda.setHorizontalAlignment(Element.ALIGN_RIGHT);
                 celda.setUseDescender(true);
                 celda.setVerticalAlignment(Element.ALIGN_MIDDLE);
                 celda.setBorder(0);
                 tabla.addCell(celda);
                 //6
                 celda = new PdfPCell(new Paragraph("$",fuenteCont2));
                 celda.setUseAscender(true);
                 celda.setHorizontalAlignment(Element.ALIGN_RIGHT);
                 celda.setUseDescender(true);
                 celda.setVerticalAlignment(Element.ALIGN_MIDDLE);
                 celda.setBorder(0);
                 tabla.addCell(celda);
                 
                 celda = new PdfPCell(new Paragraph(StringHelper.AgregaComas(StringHelper.roundDouble(registro.get("junio"),2)),fuenteCont2));
                 celda.setUseAscender(true);
                 celda.setHorizontalAlignment(Element.ALIGN_RIGHT);
                 celda.setUseDescender(true);
                 celda.setVerticalAlignment(Element.ALIGN_MIDDLE);
                 celda.setBorder(0);
                 tabla.addCell(celda);
                 //7
                 celda = new PdfPCell(new Paragraph("$",fuenteCont2));
                 celda.setUseAscender(true);
                 celda.setHorizontalAlignment(Element.ALIGN_RIGHT);
                 celda.setUseDescender(true);
                 celda.setVerticalAlignment(Element.ALIGN_MIDDLE);
                 celda.setBorder(0);
                 tabla.addCell(celda);
                 
                 celda = new PdfPCell(new Paragraph(StringHelper.AgregaComas(StringHelper.roundDouble(registro.get("julio"),2)),fuenteCont2));
                 celda.setUseAscender(true);
                 celda.setHorizontalAlignment(Element.ALIGN_RIGHT);
                 celda.setUseDescender(true);
                 celda.setVerticalAlignment(Element.ALIGN_MIDDLE);
                 celda.setBorder(0);
                 tabla.addCell(celda);
                 //8
                 celda = new PdfPCell(new Paragraph("$",fuenteCont2));
                 celda.setUseAscender(true);
                 celda.setHorizontalAlignment(Element.ALIGN_RIGHT);
                 celda.setUseDescender(true);
                 celda.setVerticalAlignment(Element.ALIGN_MIDDLE);
                 celda.setBorder(0);
                 tabla.addCell(celda);
                 
                 celda = new PdfPCell(new Paragraph(StringHelper.AgregaComas(StringHelper.roundDouble(registro.get("agosto"),2)),fuenteCont2));
                 celda.setUseAscender(true);
                 celda.setHorizontalAlignment(Element.ALIGN_RIGHT);
                 celda.setUseDescender(true);
                 celda.setVerticalAlignment(Element.ALIGN_MIDDLE);
                 celda.setBorder(0);
                 tabla.addCell(celda);
                 //9
                 celda = new PdfPCell(new Paragraph("$",fuenteCont2));
                 celda.setUseAscender(true);
                 celda.setHorizontalAlignment(Element.ALIGN_RIGHT);
                 celda.setUseDescender(true);
                 celda.setVerticalAlignment(Element.ALIGN_MIDDLE);
                 celda.setBorder(0);
                 tabla.addCell(celda);
                 
                 celda = new PdfPCell(new Paragraph(StringHelper.AgregaComas(StringHelper.roundDouble(registro.get("septiembre"),2)),fuenteCont2));
                 celda.setUseAscender(true);
                 celda.setHorizontalAlignment(Element.ALIGN_RIGHT);
                 celda.setUseDescender(true);
                 celda.setVerticalAlignment(Element.ALIGN_MIDDLE);
                 celda.setBorder(0);
                 tabla.addCell(celda);
                 //10
                 celda = new PdfPCell(new Paragraph("$",fuenteCont2));
                 celda.setUseAscender(true);
                 celda.setHorizontalAlignment(Element.ALIGN_RIGHT);
                 celda.setUseDescender(true);
                 celda.setVerticalAlignment(Element.ALIGN_MIDDLE);
                 celda.setBorder(0);
                 tabla.addCell(celda);
                 
                 celda = new PdfPCell(new Paragraph(StringHelper.AgregaComas(StringHelper.roundDouble(registro.get("octubre"),2)),fuenteCont2));
                 celda.setUseAscender(true);
                 celda.setHorizontalAlignment(Element.ALIGN_RIGHT);
                 celda.setUseDescender(true);
                 celda.setVerticalAlignment(Element.ALIGN_MIDDLE);
                 celda.setBorder(0);
                 tabla.addCell(celda);
                 //11
                 celda = new PdfPCell(new Paragraph("$",fuenteCont2));
                 celda.setUseAscender(true);
                 celda.setHorizontalAlignment(Element.ALIGN_RIGHT);
                 celda.setUseDescender(true);
                 celda.setVerticalAlignment(Element.ALIGN_MIDDLE);
                 celda.setBorder(0);
                 tabla.addCell(celda);
                 
                 celda = new PdfPCell(new Paragraph(StringHelper.AgregaComas(StringHelper.roundDouble(registro.get("noviembre"),2)),fuenteCont2));
                 celda.setUseAscender(true);
                 celda.setHorizontalAlignment(Element.ALIGN_RIGHT);
                 celda.setUseDescender(true);
                 celda.setVerticalAlignment(Element.ALIGN_MIDDLE);
                 celda.setBorder(0);
                 tabla.addCell(celda);
                 //12
                 
                 celda = new PdfPCell(new Paragraph("$",fuenteCont2));
                 celda.setUseAscender(true);
                 celda.setHorizontalAlignment(Element.ALIGN_RIGHT);
                 celda.setUseDescender(true);
                 celda.setVerticalAlignment(Element.ALIGN_MIDDLE);
                 celda.setBorder(0);
                 tabla.addCell(celda);
                 
                 celda = new PdfPCell(new Paragraph(StringHelper.AgregaComas(StringHelper.roundDouble(registro.get("diciembre"),2)),fuenteCont2));
                 celda.setUseAscender(true);
                 celda.setHorizontalAlignment(Element.ALIGN_RIGHT);
                 celda.setUseDescender(true);
                 celda.setVerticalAlignment(Element.ALIGN_MIDDLE);
                 celda.setBorder(0);
                 tabla.addCell(celda);
                 
                 
                 celda = new PdfPCell(new Paragraph("$",fuenteCont2));
                 celda.setUseAscender(true);
                 celda.setHorizontalAlignment(Element.ALIGN_RIGHT);
                 celda.setUseDescender(true);
                 celda.setVerticalAlignment(Element.ALIGN_MIDDLE);
                 celda.setBorder(0);
                 tabla.addCell(celda);
                 
                 //13
                 celda = new PdfPCell(new Paragraph(StringHelper.AgregaComas(StringHelper.roundDouble(registro.get("suma_total"),2)),fuenteCont2));
                 celda.setUseAscender(true);
                 celda.setHorizontalAlignment(Element.ALIGN_RIGHT);
                 celda.setUseDescender(true);
                 celda.setVerticalAlignment(Element.ALIGN_MIDDLE);
                 celda.setBorder(0);
                 tabla.addCell(celda);
                 
                 total_enero = total_enero + Double.parseDouble(registro.get("enero").toString());
                 total_febrero = total_febrero + Double.parseDouble(registro.get("febrero").toString());
                 total_marzo = total_marzo + Double.parseDouble(registro.get("marzo").toString());
                 total_abril = total_abril + Double.parseDouble(registro.get("abril").toString());
                 total_mayo = total_mayo + Double.parseDouble(registro.get("mayo").toString());
                 total_junio = total_junio + Double.parseDouble(registro.get("junio").toString());
                 total_julio = total_julio + Double.parseDouble(registro.get("julio").toString());
                 total_agosto = total_agosto + Double.parseDouble(registro.get("agosto").toString());
                 total_septiembre = total_septiembre + Double.parseDouble(registro.get("septiembre").toString());
                 total_octubre = total_octubre + Double.parseDouble(registro.get("octubre").toString());
                 total_noviembre = total_noviembre + Double.parseDouble(registro.get("noviembre").toString());
                 total_diciembre = total_diciembre + Double.parseDouble(registro.get("diciembre").toString());
                 total_anual = total_anual + Double.parseDouble(registro.get("suma_total").toString());
            }
            
            //aqui va el ingreso anual.
            //1
            celda = new PdfPCell(new Paragraph("$",fuenteCont2));
            celda.setHorizontalAlignment(Element.ALIGN_RIGHT);
            celda.setVerticalAlignment(Element.ALIGN_MIDDLE);
            celda.setBorderWidthBottom(0);
            celda.setBorderWidthTop(1);
            celda.setBorderWidthRight(0);
            celda.setBorderWidthLeft(0);
            tabla.addCell(celda);
            
            celda= new PdfPCell(new Paragraph(StringHelper.AgregaComas(StringHelper.roundDouble(total_enero,2)),fuenteCont2));
            celda.setHorizontalAlignment(Element.ALIGN_RIGHT);
            celda.setVerticalAlignment(Element.ALIGN_MIDDLE);
            celda.setBorderWidthBottom(0);
            celda.setBorderWidthTop(1);
            celda.setBorderWidthRight(0);
            celda.setBorderWidthLeft(0);
            tabla.addCell(celda);
            //2
            celda = new PdfPCell(new Paragraph("$",fuenteCont2));
            celda.setHorizontalAlignment(Element.ALIGN_RIGHT);
            celda.setVerticalAlignment(Element.ALIGN_MIDDLE);
            celda.setBorderWidthBottom(0);
            celda.setBorderWidthTop(1);
            celda.setBorderWidthRight(0);
            celda.setBorderWidthLeft(0);
            tabla.addCell(celda);
            
            celda= new PdfPCell(new Paragraph(StringHelper.AgregaComas(StringHelper.roundDouble(total_febrero,2)),fuenteCont2));
            celda.setHorizontalAlignment(Element.ALIGN_RIGHT);
            celda.setVerticalAlignment(Element.ALIGN_MIDDLE);
            celda.setBorderWidthBottom(0);
            celda.setBorderWidthTop(1);
            celda.setBorderWidthRight(0);
            celda.setBorderWidthLeft(0);
            tabla.addCell(celda);
            //3
            celda = new PdfPCell(new Paragraph("$",fuenteCont2));
            celda.setHorizontalAlignment(Element.ALIGN_RIGHT);
            celda.setVerticalAlignment(Element.ALIGN_MIDDLE);
            celda.setBorderWidthBottom(0);
            celda.setBorderWidthTop(1);
            celda.setBorderWidthRight(0);
            celda.setBorderWidthLeft(0);
            tabla.addCell(celda);
            
            celda= new PdfPCell(new Paragraph(StringHelper.AgregaComas(StringHelper.roundDouble(total_marzo,2)),fuenteCont2));
            celda.setHorizontalAlignment(Element.ALIGN_RIGHT);
            celda.setVerticalAlignment(Element.ALIGN_MIDDLE);
            celda.setBorderWidthBottom(0);
            celda.setBorderWidthTop(1);
            celda.setBorderWidthRight(0);
            celda.setBorderWidthLeft(0);
            tabla.addCell(celda);
            //4
            celda = new PdfPCell(new Paragraph("$",fuenteCont2));
            celda.setHorizontalAlignment(Element.ALIGN_RIGHT);
            celda.setVerticalAlignment(Element.ALIGN_MIDDLE);
            celda.setBorderWidthBottom(0);
            celda.setBorderWidthTop(1);
            celda.setBorderWidthRight(0);
            celda.setBorderWidthLeft(0);
            tabla.addCell(celda);
            
            celda= new PdfPCell(new Paragraph(StringHelper.AgregaComas(StringHelper.roundDouble(total_abril,2)),fuenteCont2));
            celda.setHorizontalAlignment(Element.ALIGN_RIGHT);
            celda.setVerticalAlignment(Element.ALIGN_MIDDLE);
            celda.setBorderWidthBottom(0);
            celda.setBorderWidthTop(1);
            celda.setBorderWidthRight(0);
            celda.setBorderWidthLeft(0);
            tabla.addCell(celda);
            //5
            celda = new PdfPCell(new Paragraph("$",fuenteCont2));
            celda.setHorizontalAlignment(Element.ALIGN_RIGHT);
            celda.setVerticalAlignment(Element.ALIGN_MIDDLE);
            celda.setBorderWidthBottom(0);
            celda.setBorderWidthTop(1);
            celda.setBorderWidthRight(0);
            celda.setBorderWidthLeft(0);
            tabla.addCell(celda);
            
            celda= new PdfPCell(new Paragraph(StringHelper.AgregaComas(StringHelper.roundDouble(total_mayo,2)),fuenteCont2));
            celda.setHorizontalAlignment(Element.ALIGN_RIGHT);
            celda.setVerticalAlignment(Element.ALIGN_MIDDLE);
            celda.setBorderWidthBottom(0);
            celda.setBorderWidthTop(1);
            celda.setBorderWidthRight(0);
            celda.setBorderWidthLeft(0);
            tabla.addCell(celda);
            //6
            celda = new PdfPCell(new Paragraph("$",fuenteCont2));
            celda.setHorizontalAlignment(Element.ALIGN_RIGHT);
            celda.setVerticalAlignment(Element.ALIGN_MIDDLE);
            celda.setBorderWidthBottom(0);
            celda.setBorderWidthTop(1);
            celda.setBorderWidthRight(0);
            celda.setBorderWidthLeft(0);
            tabla.addCell(celda);
            
            celda= new PdfPCell(new Paragraph(StringHelper.AgregaComas(StringHelper.roundDouble(total_junio,2)),fuenteCont2));
            celda.setHorizontalAlignment(Element.ALIGN_RIGHT);
            celda.setVerticalAlignment(Element.ALIGN_MIDDLE);
            celda.setBorderWidthBottom(0);
            celda.setBorderWidthTop(1);
            celda.setBorderWidthRight(0);
            celda.setBorderWidthLeft(0);
            tabla.addCell(celda);
            //7
            celda = new PdfPCell(new Paragraph("$",fuenteCont2));
            celda.setHorizontalAlignment(Element.ALIGN_RIGHT);
            celda.setVerticalAlignment(Element.ALIGN_MIDDLE);
            celda.setBorderWidthBottom(0);
            celda.setBorderWidthTop(1);
            celda.setBorderWidthRight(0);
            celda.setBorderWidthLeft(0);
            tabla.addCell(celda);
            
            celda= new PdfPCell(new Paragraph(StringHelper.AgregaComas(StringHelper.roundDouble(total_julio,2)),fuenteCont2));
            celda.setHorizontalAlignment(Element.ALIGN_RIGHT);
            celda.setVerticalAlignment(Element.ALIGN_MIDDLE);
            celda.setBorderWidthBottom(0);
            celda.setBorderWidthTop(1);
            celda.setBorderWidthRight(0);
            celda.setBorderWidthLeft(0);
            tabla.addCell(celda);
            //8
            celda = new PdfPCell(new Paragraph("$",fuenteCont2));
            celda.setHorizontalAlignment(Element.ALIGN_RIGHT);
            celda.setVerticalAlignment(Element.ALIGN_MIDDLE);
            celda.setBorderWidthBottom(0);
            celda.setBorderWidthTop(1);
            celda.setBorderWidthRight(0);
            celda.setBorderWidthLeft(0);
            tabla.addCell(celda);
            
            celda= new PdfPCell(new Paragraph(StringHelper.AgregaComas(StringHelper.roundDouble(total_agosto,2)),fuenteCont2));
            celda.setHorizontalAlignment(Element.ALIGN_RIGHT);
            celda.setVerticalAlignment(Element.ALIGN_MIDDLE);
            celda.setBorderWidthBottom(0);
            celda.setBorderWidthTop(1);
            celda.setBorderWidthRight(0);
            celda.setBorderWidthLeft(0);
            tabla.addCell(celda);
            //9
            celda = new PdfPCell(new Paragraph("$",fuenteCont2));
            celda.setHorizontalAlignment(Element.ALIGN_RIGHT);
            celda.setVerticalAlignment(Element.ALIGN_MIDDLE);
            celda.setBorderWidthBottom(0);
            celda.setBorderWidthTop(1);
            celda.setBorderWidthRight(0);
            celda.setBorderWidthLeft(0);
            tabla.addCell(celda);
            
            celda= new PdfPCell(new Paragraph(StringHelper.AgregaComas(StringHelper.roundDouble(total_septiembre,2)),fuenteCont2));
            celda.setHorizontalAlignment(Element.ALIGN_RIGHT);
            celda.setVerticalAlignment(Element.ALIGN_MIDDLE);
            celda.setBorderWidthBottom(0);
            celda.setBorderWidthTop(1);
            celda.setBorderWidthRight(0);
            celda.setBorderWidthLeft(0);
            tabla.addCell(celda);
            //10
            celda = new PdfPCell(new Paragraph("$",fuenteCont2));
            celda.setHorizontalAlignment(Element.ALIGN_RIGHT);
            celda.setVerticalAlignment(Element.ALIGN_MIDDLE);
            celda.setBorderWidthBottom(0);
            celda.setBorderWidthTop(1);
            celda.setBorderWidthRight(0);
            celda.setBorderWidthLeft(0);
            tabla.addCell(celda);
            
            celda= new PdfPCell(new Paragraph(StringHelper.AgregaComas(StringHelper.roundDouble(total_octubre,2)),fuenteCont2));
            celda.setHorizontalAlignment(Element.ALIGN_RIGHT);
            celda.setVerticalAlignment(Element.ALIGN_MIDDLE);
            celda.setBorderWidthBottom(0);
            celda.setBorderWidthTop(1);
            celda.setBorderWidthRight(0);
            celda.setBorderWidthLeft(0);
            tabla.addCell(celda);
            //11
            celda = new PdfPCell(new Paragraph("$",fuenteCont2));
            celda.setHorizontalAlignment(Element.ALIGN_RIGHT);
            celda.setVerticalAlignment(Element.ALIGN_MIDDLE);
            celda.setBorderWidthBottom(0);
            celda.setBorderWidthTop(1);
            celda.setBorderWidthRight(0);
            celda.setBorderWidthLeft(0);
            tabla.addCell(celda);
            
            celda= new PdfPCell(new Paragraph(StringHelper.AgregaComas(StringHelper.roundDouble(total_noviembre,2)),fuenteCont2));
            celda.setHorizontalAlignment(Element.ALIGN_RIGHT);
            celda.setVerticalAlignment(Element.ALIGN_MIDDLE);
            celda.setBorderWidthBottom(0);
            celda.setBorderWidthTop(1);
            celda.setBorderWidthRight(0);
            celda.setBorderWidthLeft(0);
            tabla.addCell(celda);
            //12
            celda = new PdfPCell(new Paragraph("$",fuenteCont2));
            celda.setHorizontalAlignment(Element.ALIGN_RIGHT);
            celda.setVerticalAlignment(Element.ALIGN_MIDDLE);
            celda.setBorderWidthBottom(0);
            celda.setBorderWidthTop(1);
            celda.setBorderWidthRight(0);
            celda.setBorderWidthLeft(0);
            tabla.addCell(celda);
            
            celda= new PdfPCell(new Paragraph(StringHelper.AgregaComas(StringHelper.roundDouble(total_diciembre,2)),fuenteCont2));
            celda.setHorizontalAlignment(Element.ALIGN_RIGHT);
            celda.setVerticalAlignment(Element.ALIGN_MIDDLE);
            celda.setBorderWidthBottom(0);
            celda.setBorderWidthTop(1);
            celda.setBorderWidthRight(0);
            celda.setBorderWidthLeft(0);
            tabla.addCell(celda);
            
            
            celda = new PdfPCell(new Paragraph("$",fuenteCont2));
            celda.setHorizontalAlignment(Element.ALIGN_RIGHT);
            celda.setVerticalAlignment(Element.ALIGN_MIDDLE);
            celda.setBorderWidthBottom(0);
            celda.setBorderWidthTop(1);
            celda.setBorderWidthRight(0);
            celda.setBorderWidthLeft(0);
            tabla.addCell(celda);
            
            //13
            celda= new PdfPCell(new Paragraph(StringHelper.AgregaComas(StringHelper.roundDouble(total_anual,2)),fuenteCont2));
            celda.setHorizontalAlignment(Element.ALIGN_RIGHT);
            celda.setVerticalAlignment(Element.ALIGN_MIDDLE);
            celda.setBorderWidthBottom(0);
            celda.setBorderWidthTop(1);
            celda.setBorderWidthRight(0);
            celda.setBorderWidthLeft(0);
            tabla.addCell(celda);
            
            
            
            reporte.add(tabla);   
            reporte.close();
            }catch(Exception e){
                throw new ExceptionConverter(e);
            }
            
        
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
            ColumnText.showTextAligned(writer.getDirectContent(),Element.ALIGN_CENTER, new Phrase(this.getPeriodo(),largeFont),document.getPageSize().getWidth()/2, document.getPageSize().getTop()-50, 0);
            
            
            cb = writer.getDirectContent();
            float textBase = document.bottom() - 20;
            
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
