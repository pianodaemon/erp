package com.agnux.kemikal.reportes;

import com.agnux.common.helpers.StringHelper;
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
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Arrays;


public class PdfCxpAntiguedadSaldos {

    public PdfCxpAntiguedadSaldos(String fileout, String ruta_imagen, String razon_soc_empresa, String fecha_actual,List<HashMap<String, String>> lista_facturas) throws DocumentException, FileNotFoundException {
        
        String[] fa = fecha_actual.split("-");
        String fecha_corte = "Generado el día  "+fa[2]+"/"+fa[1]+"/"+fa[0];
        
        //tipos de letras (font's)
        Font smallsmall = new Font(Font.getFamily("ARIAL"),13,Font.NORMAL);
        Font smallBoldFont = new Font(Font.getFamily("ARIAL"),8,Font.BOLD,BaseColor.BLACK);
        Font headerFont = new Font(Font.getFamily("ARIAL"),8,Font.BOLD,BaseColor.WHITE);
        Font smallBoldFont2 = new Font(Font.getFamily("ARIAL"),9,Font.BOLD,BaseColor.BLACK);
        
        Font largeBoldFont = new Font(Font.FontFamily.HELVETICA,10,Font.BOLD,BaseColor.BLACK);
        Font smallFont = new Font(Font.FontFamily.HELVETICA,8,Font.NORMAL,BaseColor.BLACK);
        
        HeaderFooter event = new HeaderFooter(razon_soc_empresa,fecha_corte);
        
        Document doc = new Document(PageSize.LETTER,-50,-50,60,30);
        doc.addCreator("gpmarsan@gmail.com");
        PdfWriter writer = PdfWriter.getInstance(doc, new FileOutputStream(fileout));
        writer.setPageEvent(event);
        
        doc.open();
        
        
        float [] widths = { 1.5f,2f,2.5f,0.7f,2f,0.7f,2f,0.7f,2f,0.7f,2f};
        
        PdfPTable table = new PdfPTable(widths);
        PdfPCell cell;
        
        table.setKeepTogether(false);
        table.setHeaderRows(1);
        
        String[] columnasHeader = {"Factura","Fecha Factura","Fecha Vencimiento","Por Vencer","30 Dias","60 Dias","90+ Dias" };
        List<String>  lista_columnas_header = (List<String>) Arrays.asList(columnasHeader);
        for ( String columna_titulo : lista_columnas_header){
            PdfPCell cellX = new PdfPCell(new Paragraph(columna_titulo,headerFont));
            cellX.setUseAscender(true);
            cellX.setUseDescender(true);
            cellX.setBackgroundColor(BaseColor.BLACK);
            
            if (columna_titulo.equals("Factura")){
                cellX.setHorizontalAlignment(Element.ALIGN_LEFT);
                cellX.setVerticalAlignment(Element.ALIGN_MIDDLE);   
            }

            if (columna_titulo.equals("Fecha Factura")){
                cellX.setHorizontalAlignment(Element.ALIGN_CENTER);
                cellX.setVerticalAlignment(Element.ALIGN_MIDDLE);   
            }

            if (columna_titulo.equals("Fecha Vencimiento")){
                cellX.setHorizontalAlignment(Element.ALIGN_CENTER);
                cellX.setVerticalAlignment(Element.ALIGN_MIDDLE);   
            }
            
            if (columna_titulo.equals("Por Vencer")){
                cellX.setHorizontalAlignment(Element.ALIGN_RIGHT);
                cellX.setVerticalAlignment(Element.ALIGN_MIDDLE);
                cellX.setColspan(2);
            }
            
            if (columna_titulo.equals("30 Dias")){
                cellX.setHorizontalAlignment(Element.ALIGN_RIGHT);
                cellX.setVerticalAlignment(Element.ALIGN_MIDDLE);
                cellX.setColspan(2);
            }
            
            if (columna_titulo.equals("60 Dias")){
                cellX.setHorizontalAlignment(Element.ALIGN_RIGHT);
                cellX.setVerticalAlignment(Element.ALIGN_MIDDLE);
                cellX.setColspan(2);
            }
            
            if (columna_titulo.equals("90+ Dias")){
                cellX.setHorizontalAlignment(Element.ALIGN_RIGHT);
                cellX.setVerticalAlignment(Element.ALIGN_MIDDLE);
                cellX.setColspan(2);
            }
            
            cellX.setFixedHeight(13);
            table.addCell(cellX);
        }
        
        if(lista_facturas.size()>0){
            //inicializar variables
            String campo_proveedor_actual = lista_facturas.get(0).get("proveedor");
            String campo_moneda_actual = lista_facturas.get(0).get("moneda_factura");
            String simbolo_moneda_actual = lista_facturas.get(0).get("simbolo_moneda");
            String valor_campo="";
            String valor_moneda="";
            
            Double suma_parcial_por_vencer=0.0;
            Double suma_parcial_30_dias=0.0;
            Double suma_parcial_60_dias=0.0;
            Double suma_parcial_90_dias=0.0;

            Double suma_total_por_vencer_mn=0.0;
            Double suma_total_30_dias_mn=0.0;
            Double suma_total_60_dias_mn=0.0;
            Double suma_total_90_dias_mn=0.0;

            Double suma_total_por_vencer_usd=0.0;	
            Double suma_total_30_dias_usd=0.0;
            Double suma_total_60_dias_usd=0.0;
            Double suma_total_90_dias_usd=0.0;
                        
            //aqui se imprime el nombre del primer proveedor
            PdfPCell cellX = new PdfPCell(new Paragraph(campo_proveedor_actual,smallBoldFont));
            cellX.setHorizontalAlignment(Element.ALIGN_LEFT);
            cellX.setVerticalAlignment(Element.ALIGN_MIDDLE);
            cellX.setBorder(0);
            cellX.setColspan(11);
            table.addCell(cellX);
            // Pintar los rows del Reporte
            for (HashMap<String, String> i : lista_facturas){
                
                if(i.get("moneda_factura").equals("M.N.") ){
                    suma_total_por_vencer_mn += Double.parseDouble(StringHelper.roundDouble(i.get("por_vencer"), 2));
                    suma_total_30_dias_mn += Double.parseDouble(StringHelper.roundDouble(i.get("de_1_a_30_dias"), 2));
                    suma_total_60_dias_mn += Double.parseDouble(StringHelper.roundDouble(i.get("de_31_a_60_dias"), 2));
                    suma_total_90_dias_mn += Double.parseDouble(StringHelper.roundDouble(i.get("de_61_dias_en_adelante"), 2));
                }else{
                    suma_total_por_vencer_usd += Double.parseDouble(StringHelper.roundDouble(i.get("por_vencer"), 2));
                    suma_total_30_dias_usd += Double.parseDouble(StringHelper.roundDouble(i.get("de_1_a_30_dias"), 2));
                    suma_total_60_dias_usd += Double.parseDouble(StringHelper.roundDouble(i.get("de_31_a_60_dias"), 2));
                    suma_total_90_dias_usd += Double.parseDouble(StringHelper.roundDouble(i.get("de_61_dias_en_adelante"), 2));
                }
                
                //Indices del HashMap que representa el row
                String[] wordList = {"serie_folio","fecha_factura","fecha_vencimiento","moneda_por_vencer","por_vencer","moneda_1_a_30_dias","de_1_a_30_dias","moneda_31_a_60_dias","de_31_a_60_dias","moneda_61_dias_en_adelante","de_61_dias_en_adelante" };
                
                List<String>  indices = (List<String>) Arrays.asList(wordList);
                if(campo_proveedor_actual.equals(i.get("proveedor")) && campo_moneda_actual.equals(i.get("moneda_factura"))){
                    for (String omega : indices){
                        PdfPCell celda = null;
                        if (omega.equals("serie_folio")){
                            celda = new PdfPCell(new Paragraph(i.get(omega),smallFont));
                            celda.setHorizontalAlignment(Element.ALIGN_LEFT);
                            celda.setVerticalAlignment(Element.ALIGN_MIDDLE);
                        }
                        if (omega.equals("fecha_factura")){
                            celda = new PdfPCell(new Paragraph(i.get(omega),smallFont));
                            celda.setHorizontalAlignment(Element.ALIGN_CENTER);
                            celda.setVerticalAlignment(Element.ALIGN_MIDDLE);
                        }
                        
                        if (omega.equals("fecha_vencimiento")){
                            celda = new PdfPCell(new Paragraph(i.get(omega),smallFont));
                            celda.setHorizontalAlignment(Element.ALIGN_CENTER);
                            celda.setVerticalAlignment(Element.ALIGN_MIDDLE);
                        }
                        
                        if (omega.equals("moneda_por_vencer")){
                            celda = new PdfPCell(new Paragraph(i.get(omega),smallFont));
                            celda.setHorizontalAlignment(Element.ALIGN_RIGHT);
                            celda.setVerticalAlignment(Element.ALIGN_MIDDLE);
                        }
                        
                        if (omega.equals("por_vencer")){
                            if( Double.parseDouble(i.get(omega)) <=0 ){
                                valor_campo="";
                            }else{
                                valor_campo = StringHelper.AgregaComas(i.get(omega));
                            }
                            celda = new PdfPCell(new Paragraph(valor_campo,smallFont));
                            celda.setHorizontalAlignment(Element.ALIGN_RIGHT);
                            celda.setVerticalAlignment(Element.ALIGN_MIDDLE);
                        }                    
                        
                        if (omega.equals("moneda_1_a_30_dias")){
                            celda = new PdfPCell(new Paragraph(i.get(omega),smallFont));
                            celda.setHorizontalAlignment(Element.ALIGN_RIGHT);
                            celda.setVerticalAlignment(Element.ALIGN_MIDDLE);
                        }
                        if (omega.equals("de_1_a_30_dias")){
                            if( Double.parseDouble(i.get(omega)) <=0 ){
                                valor_campo="";
                            }else{
                                valor_campo = StringHelper.AgregaComas(i.get(omega));
                            }
                            celda = new PdfPCell(new Paragraph(valor_campo,smallFont));
                            celda.setHorizontalAlignment(Element.ALIGN_RIGHT);
                            celda.setVerticalAlignment(Element.ALIGN_MIDDLE);
                        }
                        
                        if (omega.equals("moneda_31_a_60_dias")){
                            celda = new PdfPCell(new Paragraph(i.get(omega),smallFont));
                            celda.setHorizontalAlignment(Element.ALIGN_RIGHT);
                            celda.setVerticalAlignment(Element.ALIGN_MIDDLE);
                        }
                        if (omega.equals("de_31_a_60_dias")){
                            if( Double.parseDouble(i.get(omega)) <=0 ){
                                valor_campo="";
                            }else{
                                valor_campo = StringHelper.AgregaComas(i.get(omega));
                            }
                            celda = new PdfPCell(new Paragraph(valor_campo,smallFont));
                            celda.setHorizontalAlignment(Element.ALIGN_RIGHT);
                            celda.setVerticalAlignment(Element.ALIGN_MIDDLE);
                        }
                        
                        if (omega.equals("moneda_61_dias_en_adelante")){
                            celda = new PdfPCell(new Paragraph(i.get(omega),smallFont));
                            celda.setHorizontalAlignment(Element.ALIGN_RIGHT);
                            celda.setVerticalAlignment(Element.ALIGN_MIDDLE);
                        }
                        
                        if (omega.equals("de_61_dias_en_adelante")){
                            if( Double.parseDouble(i.get(omega)) <=0 ){
                                valor_campo="";
                            }else{
                                valor_campo = StringHelper.AgregaComas(i.get(omega));
                            }
                            celda = new PdfPCell(new Paragraph(valor_campo,smallFont));
                            celda.setHorizontalAlignment(Element.ALIGN_RIGHT);
                            celda.setVerticalAlignment(Element.ALIGN_MIDDLE);
                        }
                        
                        celda.setBorder(0);
                        table.addCell(celda);
                    } 
                    
                }else{
                    
                    //imprimir totales
                    cell = new PdfPCell(new Paragraph("Total del Proveedor",smallBoldFont));
                    cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                    cell.setVerticalAlignment(Element.ALIGN_MIDDLE);   
                    cell.setColspan(3);
                    cell.setBorder(0);
                    table.addCell(cell);                         
                    
                    if( suma_parcial_por_vencer <=0 ){
                        valor_campo="";
                        valor_moneda="";
                    }else{
                        valor_campo = StringHelper.AgregaComas(StringHelper.roundDouble(suma_parcial_por_vencer,2));
                        valor_moneda=simbolo_moneda_actual;
                    }
                    cell = new PdfPCell(new Paragraph(valor_moneda,smallBoldFont));
                    cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                    cell.setVerticalAlignment(Element.ALIGN_MIDDLE);   
                    cell.setBorder(1);
                    table.addCell(cell);
                    
                    //columna por vencer 
                    cell = new PdfPCell(new Paragraph(valor_campo,smallBoldFont));
                    cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                    cell.setVerticalAlignment(Element.ALIGN_MIDDLE);   
                    cell.setBorder(1);
                    table.addCell(cell);  
                    
                    if( suma_parcial_30_dias <=0 ){
                        valor_campo="";
                        valor_moneda="";
                    }else{
                        valor_campo = StringHelper.AgregaComas(StringHelper.roundDouble(suma_parcial_30_dias,2));
                        valor_moneda=simbolo_moneda_actual;
                    }
                    cell = new PdfPCell(new Paragraph(valor_moneda,smallBoldFont));
                    cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                    cell.setVerticalAlignment(Element.ALIGN_MIDDLE);   
                    cell.setBorder(1);
                    table.addCell(cell);
                    
                    //columna 30 dias
                    cell = new PdfPCell(new Paragraph(valor_campo,smallBoldFont));
                    cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                    cell.setVerticalAlignment(Element.ALIGN_MIDDLE);   
                    cell.setBorder(1);
                    table.addCell(cell); 
                    
                    if( suma_parcial_60_dias <=0 ){
                        valor_campo="";
                        valor_moneda="";
                    }else{
                        valor_campo = StringHelper.AgregaComas(StringHelper.roundDouble(suma_parcial_60_dias,2));
                        valor_moneda=simbolo_moneda_actual;
                    }
                    cell = new PdfPCell(new Paragraph(valor_moneda,smallBoldFont));
                    cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                    cell.setVerticalAlignment(Element.ALIGN_MIDDLE);   
                    cell.setBorder(1);
                    table.addCell(cell);
                    
                    //columna 60 dias
                    cell = new PdfPCell(new Paragraph(valor_campo,smallBoldFont));
                    cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                    cell.setVerticalAlignment(Element.ALIGN_MIDDLE);   
                    cell.setBorder(1);
                    table.addCell(cell); 
                    
                    if( suma_parcial_90_dias <=0 ){
                        valor_campo="";
                        valor_moneda="";
                    }else{
                        valor_campo = StringHelper.AgregaComas(StringHelper.roundDouble(suma_parcial_90_dias,2));
                        valor_moneda=simbolo_moneda_actual;
                    }
                    cell = new PdfPCell(new Paragraph(valor_moneda,smallBoldFont));
                    cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                    cell.setVerticalAlignment(Element.ALIGN_MIDDLE);   
                    cell.setBorder(1);
                    table.addCell(cell);
                    
                    //columna 90 dias
                    cell = new PdfPCell(new Paragraph(valor_campo,smallBoldFont));
                    cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                    cell.setVerticalAlignment(Element.ALIGN_MIDDLE);   
                    cell.setBorder(1);
                    table.addCell(cell); 
                    //aqui termina totales del proveedor 
                    
                    //aqui se agrega una fila vacia para separar el proveedor
                    cell = new PdfPCell(new Paragraph("",smallBoldFont));
                    cell.setHorizontalAlignment(Element.ALIGN_LEFT);
                    cell.setVerticalAlignment(Element.ALIGN_MIDDLE);   
                    cell.setColspan(11);
                    cell.setBorder(0);
                    cell.setFixedHeight(20);
                    table.addCell(cell);   
                    
                    
                    
                    
                    //inicializar variables
                    campo_proveedor_actual = i.get("proveedor");
                    campo_moneda_actual = i.get("moneda_factura");
                    simbolo_moneda_actual = i.get("simbolo_moneda");
                    valor_campo="";
                    valor_moneda="";

                    suma_parcial_por_vencer=0.0;
                    suma_parcial_30_dias=0.0;
                    suma_parcial_60_dias=0.0;
                    suma_parcial_90_dias=0.0;
                    
                    //aqui se imprime el nombre del siguiente proveedor
                    cell = new PdfPCell(new Paragraph(campo_proveedor_actual,smallBoldFont));
                    cell.setHorizontalAlignment(Element.ALIGN_LEFT);
                    cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                    cell.setBorder(0);
                    cell.setColspan(11);
                    table.addCell(cell);
                    //imprimir primer registro del siguiente proveedor 
                    for (String omega : indices){
                        PdfPCell celda = null;
                        if (omega.equals("serie_folio")){
                            celda = new PdfPCell(new Paragraph(i.get(omega),smallFont));
                            celda.setHorizontalAlignment(Element.ALIGN_LEFT);
                            celda.setVerticalAlignment(Element.ALIGN_MIDDLE);
                        }
                        if (omega.equals("fecha_factura")){
                            celda = new PdfPCell(new Paragraph(i.get(omega),smallFont));
                            celda.setHorizontalAlignment(Element.ALIGN_CENTER);
                            celda.setVerticalAlignment(Element.ALIGN_MIDDLE);
                        }
                        
                        if (omega.equals("fecha_vencimiento")){
                            celda = new PdfPCell(new Paragraph(i.get(omega),smallFont));
                            celda.setHorizontalAlignment(Element.ALIGN_CENTER);
                            celda.setVerticalAlignment(Element.ALIGN_MIDDLE);
                        }
                        
                        if (omega.equals("moneda_por_vencer")){
                            celda = new PdfPCell(new Paragraph(i.get(omega),smallFont));
                            celda.setHorizontalAlignment(Element.ALIGN_RIGHT);
                            celda.setVerticalAlignment(Element.ALIGN_MIDDLE);
                        }
                        
                        if (omega.equals("por_vencer")){
                            if( Double.parseDouble(i.get(omega)) <=0 ){
                                valor_campo="";
                            }else{
                                valor_campo = StringHelper.AgregaComas(i.get(omega));
                            }
                            celda = new PdfPCell(new Paragraph(valor_campo,smallFont));
                            celda.setHorizontalAlignment(Element.ALIGN_RIGHT);
                            celda.setVerticalAlignment(Element.ALIGN_MIDDLE);
                        }                    
                        
                        if (omega.equals("moneda_1_a_30_dias")){
                            celda = new PdfPCell(new Paragraph(i.get(omega),smallFont));
                            celda.setHorizontalAlignment(Element.ALIGN_RIGHT);
                            celda.setVerticalAlignment(Element.ALIGN_MIDDLE);
                        }
                        if (omega.equals("de_1_a_30_dias")){
                            if( Double.parseDouble(i.get(omega)) <=0 ){
                                valor_campo="";
                            }else{
                                valor_campo = StringHelper.AgregaComas(i.get(omega));
                            }
                            celda = new PdfPCell(new Paragraph(valor_campo,smallFont));
                            celda.setHorizontalAlignment(Element.ALIGN_RIGHT);
                            celda.setVerticalAlignment(Element.ALIGN_MIDDLE);
                        }
                        
                        if (omega.equals("moneda_31_a_60_dias")){
                            celda = new PdfPCell(new Paragraph(i.get(omega),smallFont));
                            celda.setHorizontalAlignment(Element.ALIGN_RIGHT);
                            celda.setVerticalAlignment(Element.ALIGN_MIDDLE);
                        }
                        if (omega.equals("de_31_a_60_dias")){
                            if( Double.parseDouble(i.get(omega)) <=0 ){
                                valor_campo="";
                            }else{
                                valor_campo = StringHelper.AgregaComas(i.get(omega));
                            }
                            celda = new PdfPCell(new Paragraph(valor_campo,smallFont));
                            celda.setHorizontalAlignment(Element.ALIGN_RIGHT);
                            celda.setVerticalAlignment(Element.ALIGN_MIDDLE);
                        }
                        
                        if (omega.equals("moneda_61_dias_en_adelante")){
                            celda = new PdfPCell(new Paragraph(i.get(omega),smallFont));
                            celda.setHorizontalAlignment(Element.ALIGN_RIGHT);
                            celda.setVerticalAlignment(Element.ALIGN_MIDDLE);
                        }
                        
                        if (omega.equals("de_61_dias_en_adelante")){
                            if( Double.parseDouble(i.get(omega)) <=0 ){
                                valor_campo="";
                            }else{
                                valor_campo = StringHelper.AgregaComas(i.get(omega));
                            }
                            celda = new PdfPCell(new Paragraph(valor_campo,smallFont));
                            celda.setHorizontalAlignment(Element.ALIGN_RIGHT);
                            celda.setVerticalAlignment(Element.ALIGN_MIDDLE);
                        }
                        
                        celda.setBorder(0);
                        table.addCell(celda);
                    } 
                    
                }
                
                //suma parcial de elementos
                suma_parcial_por_vencer += Double.parseDouble(StringHelper.roundDouble(i.get("por_vencer"), 2));
                suma_parcial_30_dias += Double.parseDouble(StringHelper.roundDouble(i.get("de_1_a_30_dias"), 2));
                suma_parcial_60_dias += Double.parseDouble(StringHelper.roundDouble(i.get("de_31_a_60_dias"), 2));
                suma_parcial_90_dias += Double.parseDouble(StringHelper.roundDouble(i.get("de_61_dias_en_adelante"), 2));
                
            }//termina for de items
            
            //imprimir totales del ultimo proveedor
            cell = new PdfPCell(new Paragraph("Total del Proveedor",smallBoldFont));
            cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            cell.setVerticalAlignment(Element.ALIGN_MIDDLE);   
            cell.setColspan(3);
            cell.setBorder(0);
            table.addCell(cell);                         

            if( suma_parcial_por_vencer <=0 ){
                valor_campo="";
                valor_moneda="";
            }else{
                valor_campo = StringHelper.AgregaComas(StringHelper.roundDouble(suma_parcial_por_vencer,2));
                valor_moneda=simbolo_moneda_actual;
            }
            cell = new PdfPCell(new Paragraph(valor_moneda,smallBoldFont));
            cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            cell.setVerticalAlignment(Element.ALIGN_MIDDLE);   
            cell.setBorder(1);
            table.addCell(cell);

            //columna por vencer 
            cell = new PdfPCell(new Paragraph(valor_campo,smallBoldFont));
            cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            cell.setVerticalAlignment(Element.ALIGN_MIDDLE);   
            cell.setBorder(1);
            table.addCell(cell);  

            if( suma_parcial_30_dias <=0 ){
                valor_campo="";
                valor_moneda="";
            }else{
                valor_campo = StringHelper.AgregaComas(StringHelper.roundDouble(suma_parcial_30_dias,2));
                valor_moneda=simbolo_moneda_actual;
            }
            cell = new PdfPCell(new Paragraph(valor_moneda,smallBoldFont));
            cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            cell.setVerticalAlignment(Element.ALIGN_MIDDLE);   
            cell.setBorder(1);
            table.addCell(cell);

            //columna 30 dias
            cell = new PdfPCell(new Paragraph(valor_campo,smallBoldFont));
            cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            cell.setVerticalAlignment(Element.ALIGN_MIDDLE);   
            cell.setBorder(1);
            table.addCell(cell); 

            if( suma_parcial_60_dias <=0 ){
                valor_campo="";
                valor_moneda="";
            }else{
                valor_campo = StringHelper.AgregaComas(StringHelper.roundDouble(suma_parcial_60_dias,2));
                valor_moneda=simbolo_moneda_actual;
            }
            cell = new PdfPCell(new Paragraph(valor_moneda,smallBoldFont));
            cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            cell.setVerticalAlignment(Element.ALIGN_MIDDLE);   
            cell.setBorder(1);
            table.addCell(cell);

            //columna 60 dias
            cell = new PdfPCell(new Paragraph(valor_campo,smallBoldFont));
            cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            cell.setVerticalAlignment(Element.ALIGN_MIDDLE);   
            cell.setBorder(1);
            table.addCell(cell); 

            if( suma_parcial_90_dias <=0 ){
                valor_campo="";
                valor_moneda="";
            }else{
                valor_campo = StringHelper.AgregaComas(StringHelper.roundDouble(suma_parcial_90_dias,2));
                valor_moneda=simbolo_moneda_actual;
            }
            cell = new PdfPCell(new Paragraph(valor_moneda,smallBoldFont));
            cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            cell.setVerticalAlignment(Element.ALIGN_MIDDLE);   
            cell.setBorder(1);
            table.addCell(cell);

            //columna 90 dias
            cell = new PdfPCell(new Paragraph(valor_campo,smallBoldFont));
            cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            cell.setVerticalAlignment(Element.ALIGN_MIDDLE);   
            cell.setBorder(1);
            table.addCell(cell); 
            //aqui termina totales del proveedor 
            
            
            //aqui se agrega una fila vacia para separar totales del ultimo proveedor de los totales Generales
            cell = new PdfPCell(new Paragraph("",smallBoldFont));
            cell.setHorizontalAlignment(Element.ALIGN_LEFT);
            cell.setVerticalAlignment(Element.ALIGN_MIDDLE);   
            cell.setColspan(11);
            cell.setBorder(0);
            cell.setFixedHeight(20);
            table.addCell(cell);   
            
            
            
            if(suma_total_por_vencer_mn !=0 || suma_total_30_dias_mn !=0 || suma_total_60_dias_mn !=0  || suma_total_90_dias_mn !=0){
                //totales generales en pesos 
                cell = new PdfPCell(new Paragraph("Total General en M.N.",smallBoldFont));
                cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                cell.setVerticalAlignment(Element.ALIGN_MIDDLE);   
                cell.setColspan(3);
                cell.setBorder(0);
                table.addCell(cell);                         

                if( suma_total_por_vencer_mn <=0 ){
                    valor_campo="";
                    valor_moneda="";
                }else{
                    valor_campo = StringHelper.AgregaComas(StringHelper.roundDouble(suma_total_por_vencer_mn,2));
                    valor_moneda="$";
                }
                cell = new PdfPCell(new Paragraph(valor_moneda,smallBoldFont));
                cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                cell.setVerticalAlignment(Element.ALIGN_MIDDLE);   
                cell.setBorder(0);
                table.addCell(cell);

                //columna por vencer 
                cell = new PdfPCell(new Paragraph(valor_campo,smallBoldFont));
                cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                cell.setVerticalAlignment(Element.ALIGN_MIDDLE);   
                cell.setBorder(0);
                table.addCell(cell);  

                if( suma_total_30_dias_mn <=0 ){
                    valor_campo="";
                    valor_moneda="";
                }else{
                    valor_campo = StringHelper.AgregaComas(StringHelper.roundDouble(suma_total_30_dias_mn,2));
                    valor_moneda="$";
                }
                cell = new PdfPCell(new Paragraph(valor_moneda,smallBoldFont));
                cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                cell.setVerticalAlignment(Element.ALIGN_MIDDLE);   
                cell.setBorder(0);
                table.addCell(cell);

                //columna 30 dias
                cell = new PdfPCell(new Paragraph(valor_campo,smallBoldFont));
                cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                cell.setVerticalAlignment(Element.ALIGN_MIDDLE);   
                cell.setBorder(0);
                table.addCell(cell); 

                if( suma_total_60_dias_mn <=0 ){
                    valor_campo="";
                    valor_moneda="";
                }else{
                    valor_campo = StringHelper.AgregaComas(StringHelper.roundDouble(suma_total_60_dias_mn,2));
                    valor_moneda="$";
                }
                cell = new PdfPCell(new Paragraph(valor_moneda,smallBoldFont));
                cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                cell.setVerticalAlignment(Element.ALIGN_MIDDLE);   
                cell.setBorder(0);
                table.addCell(cell);

                //columna 60 dias
                cell = new PdfPCell(new Paragraph(valor_campo,smallBoldFont));
                cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                cell.setVerticalAlignment(Element.ALIGN_MIDDLE);   
                cell.setBorder(0);
                table.addCell(cell); 

                if( suma_total_90_dias_mn <=0 ){
                    valor_campo="";
                    valor_moneda="";
                }else{
                    valor_campo = StringHelper.AgregaComas(StringHelper.roundDouble(suma_total_90_dias_mn,2));
                    valor_moneda="$";
                }
                cell = new PdfPCell(new Paragraph(valor_moneda,smallBoldFont));
                cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                cell.setVerticalAlignment(Element.ALIGN_MIDDLE);   
                cell.setBorder(0);
                table.addCell(cell);

                //columna 90 dias
                cell = new PdfPCell(new Paragraph(valor_campo,smallBoldFont));
                cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                cell.setVerticalAlignment(Element.ALIGN_MIDDLE);   
                cell.setBorder(0);
                table.addCell(cell); 
            
            
                //aqui se agrega una fila vacia para separar total general en dolares del total general en Pesos
                cell = new PdfPCell(new Paragraph("",smallBoldFont));
                cell.setHorizontalAlignment(Element.ALIGN_LEFT);
                cell.setVerticalAlignment(Element.ALIGN_MIDDLE);   
                cell.setColspan(3);
                cell.setBorder(0);
                cell.setFixedHeight(20);
                table.addCell(cell);   

                cell = new PdfPCell(new Paragraph("",smallBoldFont));
                cell.setHorizontalAlignment(Element.ALIGN_LEFT);
                cell.setVerticalAlignment(Element.ALIGN_MIDDLE);   
                cell.setColspan(8);
                cell.setBorder(1);
                cell.setFixedHeight(20);
                table.addCell(cell);   
            }
            
            
            
            if(suma_total_por_vencer_usd !=0 || suma_total_30_dias_usd !=0 || suma_total_60_dias_usd !=0  || suma_total_90_dias_usd !=0){
                //totales generales en Dolares Dolares Dolares Dolares Dolares Dolares Dolares Dolares Dolares Dolares Dolares Dolares Dolares Dolares 
                cell = new PdfPCell(new Paragraph("Total General en USD",smallBoldFont));
                cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                cell.setVerticalAlignment(Element.ALIGN_MIDDLE);   
                cell.setColspan(3);
                cell.setBorder(0);
                table.addCell(cell);                         

                if( suma_total_por_vencer_usd <=0 ){
                    valor_campo="";
                    valor_moneda="";
                }else{
                    valor_campo = StringHelper.AgregaComas(StringHelper.roundDouble(suma_total_por_vencer_usd,2));
                    valor_moneda="USD";
                }
                cell = new PdfPCell(new Paragraph(valor_moneda,smallBoldFont));
                cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                cell.setVerticalAlignment(Element.ALIGN_MIDDLE);   
                cell.setBorder(0);
                table.addCell(cell);

                //columna por vencer 
                cell = new PdfPCell(new Paragraph(valor_campo,smallBoldFont));
                cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                cell.setVerticalAlignment(Element.ALIGN_MIDDLE);   
                cell.setBorder(0);
                table.addCell(cell);  

                if( suma_total_30_dias_usd <=0 ){
                    valor_campo="";
                    valor_moneda="";
                }else{
                    valor_campo = StringHelper.AgregaComas(StringHelper.roundDouble(suma_total_30_dias_usd,2));
                    valor_moneda="USD";
                }
                cell = new PdfPCell(new Paragraph(valor_moneda,smallBoldFont));
                cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                cell.setVerticalAlignment(Element.ALIGN_MIDDLE);   
                cell.setBorder(0);
                table.addCell(cell);

                //columna 30 dias
                cell = new PdfPCell(new Paragraph(valor_campo,smallBoldFont));
                cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                cell.setVerticalAlignment(Element.ALIGN_MIDDLE);   
                cell.setBorder(0);
                table.addCell(cell); 

                if( suma_total_60_dias_usd <=0 ){
                    valor_campo="";
                    valor_moneda="";
                }else{
                    valor_campo = StringHelper.AgregaComas(StringHelper.roundDouble(suma_total_60_dias_usd,2));
                    valor_moneda="USD";
                }
                cell = new PdfPCell(new Paragraph(valor_moneda,smallBoldFont));
                cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                cell.setVerticalAlignment(Element.ALIGN_MIDDLE);   
                cell.setBorder(0);
                table.addCell(cell);
                
                //columna 60 dias
                cell = new PdfPCell(new Paragraph(valor_campo,smallBoldFont));
                cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                cell.setVerticalAlignment(Element.ALIGN_MIDDLE);   
                cell.setBorder(0);
                table.addCell(cell); 

                if( suma_total_90_dias_usd <=0 ){
                    valor_campo="";
                    valor_moneda="";
                }else{
                    valor_campo = StringHelper.AgregaComas(StringHelper.roundDouble(suma_total_90_dias_usd,2));
                    valor_moneda="USD";
                }
                cell = new PdfPCell(new Paragraph(valor_moneda,smallBoldFont));
                cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                cell.setVerticalAlignment(Element.ALIGN_MIDDLE);   
                cell.setBorder(0);
                table.addCell(cell);

                //columna 90 dias
                cell = new PdfPCell(new Paragraph(valor_campo,smallBoldFont));
                cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                cell.setVerticalAlignment(Element.ALIGN_MIDDLE);   
                cell.setBorder(0);
                table.addCell(cell); 
           
            
            
                //aqui se agrega una fila vacia
                cell = new PdfPCell(new Paragraph("",smallBoldFont));
                cell.setHorizontalAlignment(Element.ALIGN_LEFT);
                cell.setVerticalAlignment(Element.ALIGN_MIDDLE);   
                cell.setColspan(3);
                cell.setBorder(0);
                cell.setFixedHeight(20);
                table.addCell(cell);   

                cell = new PdfPCell(new Paragraph("",smallBoldFont));
                cell.setHorizontalAlignment(Element.ALIGN_LEFT);
                cell.setVerticalAlignment(Element.ALIGN_MIDDLE);   
                cell.setColspan(8);
                cell.setBorder(1);
                cell.setFixedHeight(20);
                table.addCell(cell);   
             }
            
            
            
        }else{
            
            PdfPCell cellX = new PdfPCell(new Paragraph("No hay resultados que mostrar, seleccione otro proveedor.",smallBoldFont));
            cellX.setHorizontalAlignment(Element.ALIGN_LEFT);
            cellX.setVerticalAlignment(Element.ALIGN_MIDDLE);   
            cellX.setColspan(11);
            table.addCell(cellX);
        }
        
        
        doc.add(table);
        doc.close();
        
    }//termina constructor
    
    
    static class HeaderFooter extends PdfPageEventHelper {
        protected PdfTemplate total;       
        protected BaseFont helv;  
        protected PdfContentByte cb;  
        Font largeBoldFont = new Font(Font.FontFamily.HELVETICA,10,Font.BOLD,BaseColor.BLACK);
        Font largeFont = new Font(Font.FontFamily.HELVETICA,10,Font.NORMAL,BaseColor.BLACK);
        Font smallFont = new Font(Font.FontFamily.HELVETICA,7,Font.NORMAL,BaseColor.BLACK);
        
        private String empresa;
        private String periodo;
        

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
        
        HeaderFooter(String razon_soc_empresa, String periodo){
            this.setEmpresa(razon_soc_empresa);
            this.setPeriodo(periodo);
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
            ColumnText.showTextAligned(writer.getDirectContent(),Element.ALIGN_CENTER, new Phrase("Reporte de Antigüedad de Saldos de Cuentas por Pagar",largeBoldFont),document.getPageSize().getWidth()/2, document.getPageSize().getTop()-38, 0);
            
            SimpleDateFormat formato = new SimpleDateFormat("'a las' HH:mm:ss 'hrs.'");
            String hora_generacion = formato.format(new Date());
            
            ColumnText.showTextAligned(writer.getDirectContent(),Element.ALIGN_CENTER, new Phrase(this.getPeriodo()+" "+hora_generacion, largeFont),document.getPageSize().getWidth()/2, document.getPageSize().getTop()-50, 0);
            
            cb = writer.getDirectContent();
            
            //cb.saveState();
            String text = "Página " + writer.getPageNumber() + " de ";  
            float textBase = document.bottom() - 20;
            float textSize = helv.getWidthPoint(text, 7);  
            float adjust = helv.getWidthPoint("0", 150);  
            cb.beginText();  
            cb.setFontAndSize(helv, 7);  
            cb.setTextMatrix(document.right() - 128, textBase);  
            cb.showText(text);  
            cb.endText();  
            cb.addTemplate(total, document.right() - adjust , textBase);
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
