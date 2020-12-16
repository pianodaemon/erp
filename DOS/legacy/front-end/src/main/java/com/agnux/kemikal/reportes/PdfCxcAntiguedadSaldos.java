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


public class PdfCxcAntiguedadSaldos {

    public PdfCxcAntiguedadSaldos(String fileout, String ruta_imagen, String razon_soc_empresa, String fecha_actual,List<HashMap<String, String>> lista_facturas) throws DocumentException, FileNotFoundException {
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
        Document doc = new Document(PageSize.LETTER.rotate(),-50,-50,60,30);
        doc.addCreator("gpmarsan@gmail.com");
        PdfWriter writer = PdfWriter.getInstance(doc, new FileOutputStream(fileout));
        writer.setPageEvent(event);
        
        doc.open();
        
        
        float [] widths = { 1.2f,1.6f,1.6f,0.7f,1.7f,0.7f,1.7f,0.7f,1.7f,0.7f,1.7f,0.7f,1.7f,0.7f,1.7f,0.7f,1.7f,0.7f,1.9f};
        
        PdfPTable table = new PdfPTable(widths);
        PdfPCell cell;
        
        table.setKeepTogether(false);
        table.setHeaderRows(1);
        
        String[] columnasHeader = {"Factura","Fecha Fac.","Fecha Ven.","Por Vencer","15 Dias","30 Dias","45 Dias","60 Dias","90 Dias","+90 Dias","Total"};
        
        List<String> lista_columnas_header = (List<String>) Arrays.asList(columnasHeader);
        for (String columna_titulo : lista_columnas_header) {
            PdfPCell cellX = new PdfPCell(new Paragraph(columna_titulo, headerFont));
            cellX.setUseAscender(true);
            cellX.setUseDescender(true);
            cellX.setBackgroundColor(BaseColor.BLACK);
            
            if (columna_titulo.equals("Factura")) {
                cellX.setHorizontalAlignment(Element.ALIGN_LEFT);
                cellX.setVerticalAlignment(Element.ALIGN_MIDDLE);
            }

            if (columna_titulo.equals("Fecha Fac.")) {
                cellX.setHorizontalAlignment(Element.ALIGN_CENTER);
                cellX.setVerticalAlignment(Element.ALIGN_MIDDLE);
            }

            if (columna_titulo.equals("Fecha Ven.")) {
                cellX.setHorizontalAlignment(Element.ALIGN_CENTER);
                cellX.setVerticalAlignment(Element.ALIGN_MIDDLE);
            }

            if (columna_titulo.equals("Por Vencer")) {
                cellX.setHorizontalAlignment(Element.ALIGN_RIGHT);
                cellX.setVerticalAlignment(Element.ALIGN_MIDDLE);
                cellX.setColspan(2);
            }
            
            if (columna_titulo.equals("15 Dias")) {
                cellX.setHorizontalAlignment(Element.ALIGN_RIGHT);
                cellX.setVerticalAlignment(Element.ALIGN_MIDDLE);
                cellX.setColspan(2);
            }

            if (columna_titulo.equals("30 Dias")) {
                cellX.setHorizontalAlignment(Element.ALIGN_RIGHT);
                cellX.setVerticalAlignment(Element.ALIGN_MIDDLE);
                cellX.setColspan(2);
            }

            if (columna_titulo.equals("45 Dias")) {
                cellX.setHorizontalAlignment(Element.ALIGN_RIGHT);
                cellX.setVerticalAlignment(Element.ALIGN_MIDDLE);
                cellX.setColspan(2);
            }
            
            if (columna_titulo.equals("60 Dias")) {
                cellX.setHorizontalAlignment(Element.ALIGN_RIGHT);
                cellX.setVerticalAlignment(Element.ALIGN_MIDDLE);
                cellX.setColspan(2);
            }
            
            if (columna_titulo.equals("90 Dias")) {
                cellX.setHorizontalAlignment(Element.ALIGN_RIGHT);
                cellX.setVerticalAlignment(Element.ALIGN_MIDDLE);
                cellX.setColspan(2);
            }
            
            if (columna_titulo.equals("+90 Dias")) {
                cellX.setHorizontalAlignment(Element.ALIGN_RIGHT);
                cellX.setVerticalAlignment(Element.ALIGN_MIDDLE);
                cellX.setColspan(2);
            }
            if (columna_titulo.equals("Total")) {
                cellX.setHorizontalAlignment(Element.ALIGN_RIGHT);
                cellX.setVerticalAlignment(Element.ALIGN_MIDDLE);
                cellX.setColspan(2);
            }
            
            cellX.setFixedHeight(13);
            table.addCell(cellX);
        }
        
        
        if(lista_facturas.size()>0){
            //inicializar variables
            Double suma_parcial_por_vencer=0.0;
            Double suma_parcial_menor_igual_15=0.0;
            Double suma_parcial_menor_igual_30=0.0;
            Double suma_parcial_menor_igual_45=0.0;
            Double suma_parcial_menor_igual_60=0.0;
            Double suma_parcial_menor_igual_90=0.0;
            Double suma_parcial_mayor_90=0.0;
            
            Double suma_parcial_saldo_factura_total=0.0;
            
            Double suma_total_por_vencer_mn=0.0;
            Double suma_total_menor_igual_15_mn=0.0;
            Double suma_total_menor_igual_30_mn=0.0;
            Double suma_total_menor_igual_45_mn=0.0;
            Double suma_total_menor_igual_60_mn=0.0;
            Double suma_total_menor_igual_90_mn=0.0;
            Double suma_total_mayor_90_mn=0.0;
            
            Double suma_total_saldo_factura_total_mn=0.0;
            
            Double suma_total_por_vencer_usd=0.0;
            Double suma_total_menor_igual_15_usd=0.0;
            Double suma_total_menor_igual_30_usd=0.0;
            Double suma_total_menor_igual_45_usd=0.0;
            Double suma_total_menor_igual_60_usd=0.0;
            Double suma_total_menor_igual_90_usd=0.0;
            Double suma_total_mayor_90_usd=0.0;
            
            Double suma_total_saldo_factura_total_usd=0.0;
            
            String clave_cliente_actual = lista_facturas.get(0).get("clave_cliente");
            String campo_cliente_actual = lista_facturas.get(0).get("cliente");
            String campo_moneda_actual  = lista_facturas.get(0).get("moneda_factura");
            String simbolo_moneda_actual= lista_facturas.get(0).get("simbolo_moneda");
            
            PdfPCell cellX;
            
            //aqui se imprime el nombre del primer cliente
            cellX = new PdfPCell(new Paragraph(clave_cliente_actual,smallBoldFont));
            cellX.setHorizontalAlignment(Element.ALIGN_LEFT);
            cellX.setVerticalAlignment(Element.ALIGN_MIDDLE);
            cellX.setBorder(0);
            table.addCell(cellX);
            
            //aqui se imprime el nombre del primer cliente
            cellX = new PdfPCell(new Paragraph(campo_cliente_actual,smallBoldFont));
            cellX.setHorizontalAlignment(Element.ALIGN_LEFT);
            cellX.setVerticalAlignment(Element.ALIGN_MIDDLE);
            cellX.setBorder(0);
            cellX.setColspan(18);
            table.addCell(cellX);
            
            //::TERMINA For que recorre listado de registros
            // Pintar los rows del Reporte
            for (HashMap<String, String> i : lista_facturas){
                if(i.get("moneda_factura").equals("M.N.")){
                    suma_total_por_vencer_mn += Double.parseDouble(StringHelper.roundDouble(i.get("por_vencer"), 2));
                    suma_total_menor_igual_15_mn += Double.parseDouble(StringHelper.roundDouble(i.get("menor_igual_15"), 2));
                    suma_total_menor_igual_30_mn += Double.parseDouble(StringHelper.roundDouble(i.get("menor_igual_30"), 2));
                    suma_total_menor_igual_45_mn += Double.parseDouble(StringHelper.roundDouble(i.get("menor_igual_45"), 2));
                    suma_total_menor_igual_60_mn += Double.parseDouble(StringHelper.roundDouble(i.get("menor_igual_60"), 2));
                    suma_total_menor_igual_90_mn += Double.parseDouble(StringHelper.roundDouble(i.get("menor_igual_90"), 2));
                    suma_total_mayor_90_mn       += Double.parseDouble(StringHelper.roundDouble(i.get("mayor_90"), 2));
                    
                    suma_total_saldo_factura_total_mn += Double.parseDouble(StringHelper.roundDouble(i.get("saldo_factura"), 2));
                    
                    
                }else{
                    suma_total_por_vencer_usd += Double.parseDouble(StringHelper.roundDouble(i.get("por_vencer"), 2));
                    suma_total_menor_igual_15_usd += Double.parseDouble(StringHelper.roundDouble(i.get("menor_igual_15"), 2));
                    suma_total_menor_igual_30_usd += Double.parseDouble(StringHelper.roundDouble(i.get("menor_igual_30"), 2));
                    suma_total_menor_igual_45_usd += Double.parseDouble(StringHelper.roundDouble(i.get("menor_igual_45"), 2));
                    suma_total_menor_igual_60_usd += Double.parseDouble(StringHelper.roundDouble(i.get("menor_igual_60"), 2));
                    suma_total_menor_igual_90_usd += Double.parseDouble(StringHelper.roundDouble(i.get("menor_igual_90"), 2));
                    suma_total_mayor_90_usd += Double.parseDouble(StringHelper.roundDouble(i.get("mayor_90"), 2));
                    
                    suma_total_saldo_factura_total_usd += Double.parseDouble(StringHelper.roundDouble(i.get("saldo_factura"), 2));
                }
                
                //Indices del HashMap que representa el row
                String[] wordList = {"factura","fecha_facturacion","fecha_vencimiento","moneda_por_vencer","por_vencer","moneda_menor_igual_15","menor_igual_15","moneda_menor_igual_30","menor_igual_30","moneda_menor_igual_45","menor_igual_45","moneda_menor_igual_60","menor_igual_60","moneda_menor_igual_90","menor_igual_90","moneda_mayor_90","mayor_90","moneda_saldo_factura","saldo_factura"};
                
                List<String>  indices = (List<String>) Arrays.asList(wordList);
                
                if(clave_cliente_actual.equals(i.get("clave_cliente")) && simbolo_moneda_actual.equals(i.get("simbolo_moneda"))){
                    
                    for (String omega : indices){
                        PdfPCell celda = null;
                        if (omega.equals("factura")){
                            celda = new PdfPCell(new Paragraph(i.get(omega),smallFont));
                            celda.setHorizontalAlignment(Element.ALIGN_LEFT);
                            celda.setVerticalAlignment(Element.ALIGN_MIDDLE);
                        }
                        if (omega.equals("fecha_facturacion")){
                            celda = new PdfPCell(new Paragraph(i.get(omega),smallFont));
                            celda.setHorizontalAlignment(Element.ALIGN_CENTER);
                            celda.setVerticalAlignment(Element.ALIGN_MIDDLE);
                        }
                        
                        if (omega.equals("fecha_vencimiento")){
                            celda = new PdfPCell(new Paragraph(i.get(omega),smallFont));
                            celda.setHorizontalAlignment(Element.ALIGN_CENTER);
                            celda.setVerticalAlignment(Element.ALIGN_MIDDLE);
                        }
                        
                        if ( omega.equals("moneda_por_vencer") || omega.equals("moneda_menor_igual_15") || omega.equals("moneda_menor_igual_30") || omega.equals("moneda_menor_igual_45") || omega.equals("moneda_menor_igual_60") || omega.equals("moneda_menor_igual_90") || omega.equals("moneda_mayor_90")|| omega.equals("moneda_saldo_factura") ){
                            celda = new PdfPCell(new Paragraph(i.get(omega),smallFont));
                            celda.setHorizontalAlignment(Element.ALIGN_RIGHT);
                            celda.setVerticalAlignment(Element.ALIGN_MIDDLE);
                        }
                        
                        if (omega.equals("por_vencer") || omega.equals("menor_igual_15") || omega.equals("menor_igual_30") || omega.equals("menor_igual_45") || omega.equals("menor_igual_60") || omega.equals("menor_igual_90") || omega.equals("mayor_90") || omega.equals("saldo_factura")){
                            celda = new PdfPCell(new Paragraph( ((Double.parseDouble(i.get(omega))<=0)? "":StringHelper.AgregaComas(i.get(omega)) ) ,smallFont));
                            celda.setHorizontalAlignment(Element.ALIGN_RIGHT);
                            celda.setVerticalAlignment(Element.ALIGN_MIDDLE);
                        }                       
                        celda.setBorder(0);
                        table.addCell(celda);
                    }
                    
                }else{
                    
                    //imprimir totales
                    cell = new PdfPCell(new Paragraph("Total del Cliente",smallBoldFont));
                    cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                    cell.setVerticalAlignment(Element.ALIGN_MIDDLE);   
                    cell.setColspan(3);
                    cell.setBorder(0);
                    table.addCell(cell);
                    
                    //simbolo moneda
                    cell = new PdfPCell(new Paragraph(((suma_parcial_por_vencer<=0)? "": simbolo_moneda_actual ),smallBoldFont));
                    cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                    cell.setVerticalAlignment(Element.ALIGN_MIDDLE);   
                    cell.setBorder(1);
                    table.addCell(cell);
                    
                    //columna por vencer
                    cell = new PdfPCell(new Paragraph( ((suma_parcial_por_vencer<=0)? "":StringHelper.AgregaComas(StringHelper.roundDouble(suma_parcial_por_vencer,2)) ) ,smallBoldFont));
                    cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                    cell.setVerticalAlignment(Element.ALIGN_MIDDLE);   
                    cell.setBorder(1);
                    table.addCell(cell);  
                    
                    //simbolo moneda
                    cell = new PdfPCell(new Paragraph(((suma_parcial_menor_igual_15<=0)? "": simbolo_moneda_actual ),smallBoldFont));
                    cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                    cell.setVerticalAlignment(Element.ALIGN_MIDDLE);   
                    cell.setBorder(1);
                    table.addCell(cell);
                    
                    //columna suma cliente <=15 dias
                    cell = new PdfPCell(new Paragraph( ((suma_parcial_menor_igual_15<=0)? "":StringHelper.AgregaComas(StringHelper.roundDouble(suma_parcial_menor_igual_15,2)) ) ,smallBoldFont));
                    cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                    cell.setVerticalAlignment(Element.ALIGN_MIDDLE);   
                    cell.setBorder(1);
                    table.addCell(cell);
                    
                    //simbolo moneda
                    cell = new PdfPCell(new Paragraph(((suma_parcial_menor_igual_30<=0)? "": simbolo_moneda_actual ),smallBoldFont));
                    cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                    cell.setVerticalAlignment(Element.ALIGN_MIDDLE);   
                    cell.setBorder(1);
                    table.addCell(cell);
                    
                    //columna suma cliente <=30 dias
                    cell = new PdfPCell(new Paragraph( ((suma_parcial_menor_igual_30<=0)? "":StringHelper.AgregaComas(StringHelper.roundDouble(suma_parcial_menor_igual_30,2)) ) ,smallBoldFont));
                    cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                    cell.setVerticalAlignment(Element.ALIGN_MIDDLE);   
                    cell.setBorder(1);
                    table.addCell(cell);
                    
                    //simbolo moneda
                    cell = new PdfPCell(new Paragraph(((suma_parcial_menor_igual_45<=0)? "": simbolo_moneda_actual ),smallBoldFont));
                    cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                    cell.setVerticalAlignment(Element.ALIGN_MIDDLE);   
                    cell.setBorder(1);
                    table.addCell(cell);
                    
                    //columna suma cliente <=45 dias
                    cell = new PdfPCell(new Paragraph( ((suma_parcial_menor_igual_45<=0)? "":StringHelper.AgregaComas(StringHelper.roundDouble(suma_parcial_menor_igual_45,2)) ) ,smallBoldFont));
                    cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                    cell.setVerticalAlignment(Element.ALIGN_MIDDLE);   
                    cell.setBorder(1);
                    table.addCell(cell);
                    
                    //simbolo moneda
                    cell = new PdfPCell(new Paragraph(((suma_parcial_menor_igual_60<=0)? "": simbolo_moneda_actual ),smallBoldFont));
                    cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                    cell.setVerticalAlignment(Element.ALIGN_MIDDLE);   
                    cell.setBorder(1);
                    table.addCell(cell);
                    
                    //columna suma cliente <=60 dias
                    cell = new PdfPCell(new Paragraph( ((suma_parcial_menor_igual_60<=0)? "":StringHelper.AgregaComas(StringHelper.roundDouble(suma_parcial_menor_igual_60,2)) ) ,smallBoldFont));
                    cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                    cell.setVerticalAlignment(Element.ALIGN_MIDDLE);   
                    cell.setBorder(1);
                    table.addCell(cell);
                    
                    //simbolo moneda
                    cell = new PdfPCell(new Paragraph(((suma_parcial_menor_igual_90<=0)? "": simbolo_moneda_actual ),smallBoldFont));
                    cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                    cell.setVerticalAlignment(Element.ALIGN_MIDDLE);   
                    cell.setBorder(1);
                    table.addCell(cell);
                    
                    //columna suma cliente <=90 dias
                    cell = new PdfPCell(new Paragraph( ((suma_parcial_menor_igual_90<=0)? "":StringHelper.AgregaComas(StringHelper.roundDouble(suma_parcial_menor_igual_90,2)) ) ,smallBoldFont));
                    cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                    cell.setVerticalAlignment(Element.ALIGN_MIDDLE);   
                    cell.setBorder(1);
                    table.addCell(cell);
                    
                    //simbolo moneda
                    cell = new PdfPCell(new Paragraph(((suma_parcial_mayor_90<=0)? "": simbolo_moneda_actual ),smallBoldFont));
                    cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                    cell.setVerticalAlignment(Element.ALIGN_MIDDLE);   
                    cell.setBorder(1);
                    table.addCell(cell);
                    
                    //columna suma cliente <=+90 dias
                    cell = new PdfPCell(new Paragraph( ((suma_parcial_mayor_90<=0)? "":StringHelper.AgregaComas(StringHelper.roundDouble(suma_parcial_mayor_90,2)) ) ,smallBoldFont));
                    cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                    cell.setVerticalAlignment(Element.ALIGN_MIDDLE);   
                    cell.setBorder(1);
                    table.addCell(cell);
                    
                    
                    //simbolo moneda
                    cell = new PdfPCell(new Paragraph(((suma_parcial_saldo_factura_total<=0)? "": simbolo_moneda_actual ),smallBoldFont));
                    cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                    cell.setVerticalAlignment(Element.ALIGN_MIDDLE);   
                    cell.setBorder(1);
                    table.addCell(cell);
                    
                    cell = new PdfPCell(new Paragraph( ((suma_parcial_saldo_factura_total<=0)? "":StringHelper.AgregaComas(StringHelper.roundDouble(suma_parcial_saldo_factura_total,2)) ) ,smallBoldFont));
                    cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                    cell.setVerticalAlignment(Element.ALIGN_MIDDLE);   
                    cell.setBorder(1);
                    table.addCell(cell);
                    
                    //aqui se agrega una fila vacia para separar el cliente
                    cell = new PdfPCell(new Paragraph("",smallBoldFont));
                    cell.setHorizontalAlignment(Element.ALIGN_LEFT);
                    cell.setVerticalAlignment(Element.ALIGN_MIDDLE);   
                    cell.setColspan(19);
                    cell.setBorder(0);
                    cell.setFixedHeight(20);
                    table.addCell(cell);   
                    
                    
                    //inicializar variables
                    clave_cliente_actual="";
                    campo_cliente_actual="";
                    simbolo_moneda_actual="";
                    
                    suma_parcial_por_vencer = 0.0;
                    suma_parcial_menor_igual_15 = 0.0;
                    suma_parcial_menor_igual_30 = 0.0;
                    suma_parcial_menor_igual_45 = 0.0;
                    suma_parcial_menor_igual_60 = 0.0;
                    suma_parcial_menor_igual_90 = 0.0;
                    suma_parcial_mayor_90 = 0.0;
                    suma_parcial_saldo_factura_total= 0.0;
                    
                    
                    //tomar datos del nuevo cliente 
                    clave_cliente_actual  = i.get("clave_cliente");
                    simbolo_moneda_actual = i.get("simbolo_moneda");
                    campo_cliente_actual = i.get("cliente");
						

                    //aqui se imprime el nombre del primer cliente
                    cell = new PdfPCell(new Paragraph(clave_cliente_actual,smallBoldFont));
                    cell.setHorizontalAlignment(Element.ALIGN_LEFT);
                    cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                    cell.setBorder(0);
                    table.addCell(cell);

                    //aqui se imprime el nombre del primer cliente
                    cell = new PdfPCell(new Paragraph(campo_cliente_actual,smallBoldFont));
                    cell.setHorizontalAlignment(Element.ALIGN_LEFT);
                    cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                    cell.setBorder(0);
                    cell.setColspan(18);
                    table.addCell(cell);

                    
                    for (String omega : indices){
                        PdfPCell celda = null;
                        if (omega.equals("factura")){
                            celda = new PdfPCell(new Paragraph(i.get(omega),smallFont));
                            celda.setHorizontalAlignment(Element.ALIGN_LEFT);
                            celda.setVerticalAlignment(Element.ALIGN_MIDDLE);
                        }
                        if (omega.equals("fecha_facturacion")){
                            celda = new PdfPCell(new Paragraph(i.get(omega),smallFont));
                            celda.setHorizontalAlignment(Element.ALIGN_CENTER);
                            celda.setVerticalAlignment(Element.ALIGN_MIDDLE);
                        }
                        
                        if (omega.equals("fecha_vencimiento")){
                            celda = new PdfPCell(new Paragraph(i.get(omega),smallFont));
                            celda.setHorizontalAlignment(Element.ALIGN_CENTER);
                            celda.setVerticalAlignment(Element.ALIGN_MIDDLE);
                        }
                        
                        if ( omega.equals("moneda_por_vencer") || omega.equals("moneda_menor_igual_15") || omega.equals("moneda_menor_igual_30") || omega.equals("moneda_menor_igual_45") || omega.equals("moneda_menor_igual_60") || omega.equals("moneda_menor_igual_90") || omega.equals("moneda_mayor_90") || omega.equals("moneda_saldo_factura")){
                            celda = new PdfPCell(new Paragraph(i.get(omega),smallFont));
                            celda.setHorizontalAlignment(Element.ALIGN_RIGHT);
                            celda.setVerticalAlignment(Element.ALIGN_MIDDLE);
                        }
                        
                        if (omega.equals("por_vencer") || omega.equals("menor_igual_15") || omega.equals("menor_igual_30") || omega.equals("menor_igual_45") || omega.equals("menor_igual_60") || omega.equals("menor_igual_90") || omega.equals("mayor_90") || omega.equals("saldo_factura")){
                            celda = new PdfPCell(new Paragraph( ((Double.parseDouble(i.get(omega))<=0)? "":StringHelper.AgregaComas(i.get(omega)) ) ,smallFont));
                            celda.setHorizontalAlignment(Element.ALIGN_RIGHT);
                            celda.setVerticalAlignment(Element.ALIGN_MIDDLE);
                        }  
                        
                        
                        
                    
                        celda.setBorder(0);
                        table.addCell(celda);
                    }
                }
                
                
                //suma parcial de elementos
                suma_parcial_por_vencer += Double.parseDouble(StringHelper.roundDouble(i.get("por_vencer"), 2));
                suma_parcial_menor_igual_15 += Double.parseDouble(StringHelper.roundDouble(i.get("menor_igual_15"), 2));
                suma_parcial_menor_igual_30 += Double.parseDouble(StringHelper.roundDouble(i.get("menor_igual_30"), 2));
                suma_parcial_menor_igual_45 += Double.parseDouble(StringHelper.roundDouble(i.get("menor_igual_45"), 2));
                suma_parcial_menor_igual_60 += Double.parseDouble(StringHelper.roundDouble(i.get("menor_igual_60"), 2));
                suma_parcial_menor_igual_90 += Double.parseDouble(StringHelper.roundDouble(i.get("menor_igual_90"), 2));
                suma_parcial_mayor_90 += Double.parseDouble(StringHelper.roundDouble(i.get("mayor_90"), 2));
                
                suma_parcial_saldo_factura_total += Double.parseDouble(StringHelper.roundDouble(i.get("saldo_factura"), 2));
            }
            //::TERMINA For que recorre listado de registros
            
            //imprimir totales de ULTIMO CLIENTE
            cell = new PdfPCell(new Paragraph("Total del Cliente",smallBoldFont));
            cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            cell.setVerticalAlignment(Element.ALIGN_MIDDLE);   
            cell.setColspan(3);
            cell.setBorder(0);
            table.addCell(cell);

            //simbolo moneda
            cell = new PdfPCell(new Paragraph(((suma_parcial_por_vencer<=0)? "": simbolo_moneda_actual ),smallBoldFont));
            cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            cell.setVerticalAlignment(Element.ALIGN_MIDDLE);   
            cell.setBorder(1);
            table.addCell(cell);

            //columna por vencer
            cell = new PdfPCell(new Paragraph( ((suma_parcial_por_vencer<=0)? "":StringHelper.AgregaComas(StringHelper.roundDouble(suma_parcial_por_vencer,2)) ) ,smallBoldFont));
            cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            cell.setVerticalAlignment(Element.ALIGN_MIDDLE);   
            cell.setBorder(1);
            table.addCell(cell);  

            //simbolo moneda
            cell = new PdfPCell(new Paragraph(((suma_parcial_menor_igual_15<=0)? "": simbolo_moneda_actual ),smallBoldFont));
            cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            cell.setVerticalAlignment(Element.ALIGN_MIDDLE);   
            cell.setBorder(1);
            table.addCell(cell);

            //columna suma cliente <=15 dias
            cell = new PdfPCell(new Paragraph( ((suma_parcial_menor_igual_15<=0)? "":StringHelper.AgregaComas(StringHelper.roundDouble(suma_parcial_menor_igual_15,2)) ) ,smallBoldFont));
            cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            cell.setVerticalAlignment(Element.ALIGN_MIDDLE);   
            cell.setBorder(1);
            table.addCell(cell);

            //simbolo moneda
            cell = new PdfPCell(new Paragraph(((suma_parcial_menor_igual_30<=0)? "": simbolo_moneda_actual ),smallBoldFont));
            cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            cell.setVerticalAlignment(Element.ALIGN_MIDDLE);   
            cell.setBorder(1);
            table.addCell(cell);

            //columna suma cliente <=30 dias
            cell = new PdfPCell(new Paragraph( ((suma_parcial_menor_igual_30<=0)? "":StringHelper.AgregaComas(StringHelper.roundDouble(suma_parcial_menor_igual_30,2)) ) ,smallBoldFont));
            cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            cell.setVerticalAlignment(Element.ALIGN_MIDDLE);   
            cell.setBorder(1);
            table.addCell(cell);

            //simbolo moneda
            cell = new PdfPCell(new Paragraph(((suma_parcial_menor_igual_45<=0)? "": simbolo_moneda_actual ),smallBoldFont));
            cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            cell.setVerticalAlignment(Element.ALIGN_MIDDLE);   
            cell.setBorder(1);
            table.addCell(cell);

            //columna suma cliente <=45 dias
            cell = new PdfPCell(new Paragraph( ((suma_parcial_menor_igual_45<=0)? "":StringHelper.AgregaComas(StringHelper.roundDouble(suma_parcial_menor_igual_45,2)) ) ,smallBoldFont));
            cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            cell.setVerticalAlignment(Element.ALIGN_MIDDLE);   
            cell.setBorder(1);
            table.addCell(cell);

            //simbolo moneda
            cell = new PdfPCell(new Paragraph(((suma_parcial_menor_igual_60<=0)? "": simbolo_moneda_actual ),smallBoldFont));
            cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            cell.setVerticalAlignment(Element.ALIGN_MIDDLE);   
            cell.setBorder(1);
            table.addCell(cell);

            //columna suma cliente <=60 dias
            cell = new PdfPCell(new Paragraph( ((suma_parcial_menor_igual_60<=0)? "":StringHelper.AgregaComas(StringHelper.roundDouble(suma_parcial_menor_igual_60,2)) ) ,smallBoldFont));
            cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            cell.setVerticalAlignment(Element.ALIGN_MIDDLE);   
            cell.setBorder(1);
            table.addCell(cell);

            //simbolo moneda
            cell = new PdfPCell(new Paragraph(((suma_parcial_menor_igual_90<=0)? "": simbolo_moneda_actual ),smallBoldFont));
            cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            cell.setVerticalAlignment(Element.ALIGN_MIDDLE);   
            cell.setBorder(1);
            table.addCell(cell);

            //columna suma cliente <=90 dias
            cell = new PdfPCell(new Paragraph( ((suma_parcial_menor_igual_90<=0)? "":StringHelper.AgregaComas(StringHelper.roundDouble(suma_parcial_menor_igual_90,2)) ) ,smallBoldFont));
            cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            cell.setVerticalAlignment(Element.ALIGN_MIDDLE);   
            cell.setBorder(1);
            table.addCell(cell);
            
            
            
            //simbolo moneda
            cell = new PdfPCell(new Paragraph(((suma_parcial_mayor_90<=0)? "": simbolo_moneda_actual ),smallBoldFont));
            cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            cell.setVerticalAlignment(Element.ALIGN_MIDDLE);   
            cell.setBorder(1);
            table.addCell(cell);

            //columna suma cliente <=+90 dias
            cell = new PdfPCell(new Paragraph( ((suma_parcial_mayor_90<=0)? "":StringHelper.AgregaComas(StringHelper.roundDouble(suma_parcial_mayor_90,2)) ) ,smallBoldFont));
            cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            cell.setVerticalAlignment(Element.ALIGN_MIDDLE);   
            cell.setBorder(1);
            table.addCell(cell);

            //simbolo moneda
            cell = new PdfPCell(new Paragraph(((suma_parcial_saldo_factura_total<=0)? "": simbolo_moneda_actual ),smallBoldFont));
            cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            cell.setVerticalAlignment(Element.ALIGN_MIDDLE);   
            cell.setBorder(1);
            table.addCell(cell);
            
            cell = new PdfPCell(new Paragraph( ((suma_parcial_saldo_factura_total<=0)? "":StringHelper.AgregaComas(StringHelper.roundDouble(suma_parcial_saldo_factura_total,2)) ) ,smallBoldFont));
            cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            cell.setVerticalAlignment(Element.ALIGN_MIDDLE);   
            cell.setBorder(1);
            table.addCell(cell);
            
            
            //aqui se agrega una FILA VACIA para separar el cliente de los  totales
            cell = new PdfPCell(new Paragraph("",smallBoldFont));
            cell.setHorizontalAlignment(Element.ALIGN_LEFT);
            cell.setVerticalAlignment(Element.ALIGN_MIDDLE);   
            cell.setColspan(19);
            cell.setBorder(0);
            cell.setFixedHeight(30);
            table.addCell(cell);
            
            
            //::::::::::::::::::::::::::::::::aqui se AGREGA LOS TOTALES EN PESOS:::::::::::::::::::::::::::::::::::::::::::::
            cell = new PdfPCell(new Paragraph("Total en M.N.",smallBoldFont));
            cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            cell.setVerticalAlignment(Element.ALIGN_MIDDLE);   
            cell.setColspan(3);
            cell.setBorder(0);
            table.addCell(cell);

            //simbolo moneda
            cell = new PdfPCell(new Paragraph("$",smallBoldFont));
            cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            cell.setVerticalAlignment(Element.ALIGN_MIDDLE);   
            cell.setBorder(1);
            table.addCell(cell);
            
            //columna por vencer
            cell = new PdfPCell(new Paragraph( ((suma_total_por_vencer_mn<=0)? "":StringHelper.AgregaComas(StringHelper.roundDouble(suma_total_por_vencer_mn,2)) ) ,smallBoldFont));
            cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            cell.setVerticalAlignment(Element.ALIGN_MIDDLE);   
            cell.setBorder(1);
            table.addCell(cell);  

            //simbolo moneda
            cell = new PdfPCell(new Paragraph("$",smallBoldFont));
            cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            cell.setVerticalAlignment(Element.ALIGN_MIDDLE);   
            cell.setBorder(1);
            table.addCell(cell);

            //columna suma cliente <=15 dias
            cell = new PdfPCell(new Paragraph( ((suma_total_menor_igual_15_mn<=0)? "":StringHelper.AgregaComas(StringHelper.roundDouble(suma_total_menor_igual_15_mn,2)) ) ,smallBoldFont));
            cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            cell.setVerticalAlignment(Element.ALIGN_MIDDLE);   
            cell.setBorder(1);
            table.addCell(cell);
                    
            //simbolo moneda
            cell = new PdfPCell(new Paragraph("$",smallBoldFont));
            cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            cell.setVerticalAlignment(Element.ALIGN_MIDDLE);   
            cell.setBorder(1);
            table.addCell(cell);

            //columna suma cliente <=30 dias
            cell = new PdfPCell(new Paragraph( ((suma_total_menor_igual_30_mn<=0)? "":StringHelper.AgregaComas(StringHelper.roundDouble(suma_total_menor_igual_30_mn,2)) ) ,smallBoldFont));
            cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            cell.setVerticalAlignment(Element.ALIGN_MIDDLE);   
            cell.setBorder(1);
            table.addCell(cell);
                    
            //simbolo moneda
            cell = new PdfPCell(new Paragraph("$",smallBoldFont));
            cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            cell.setVerticalAlignment(Element.ALIGN_MIDDLE);   
            cell.setBorder(1);
            table.addCell(cell);

            //columna suma cliente <=45 dias
            cell = new PdfPCell(new Paragraph( ((suma_total_menor_igual_45_mn<=0)? "":StringHelper.AgregaComas(StringHelper.roundDouble(suma_total_menor_igual_45_mn,2)) ) ,smallBoldFont));
            cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            cell.setVerticalAlignment(Element.ALIGN_MIDDLE);   
            cell.setBorder(1);
            table.addCell(cell);

            //simbolo moneda
            cell = new PdfPCell(new Paragraph("$",smallBoldFont));
            cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            cell.setVerticalAlignment(Element.ALIGN_MIDDLE);   
            cell.setBorder(1);
            table.addCell(cell);

            //columna suma cliente <=60 dias
            cell = new PdfPCell(new Paragraph( ((suma_total_menor_igual_60_mn<=0)? "":StringHelper.AgregaComas(StringHelper.roundDouble(suma_total_menor_igual_60_mn,2)) ) ,smallBoldFont));
            cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            cell.setVerticalAlignment(Element.ALIGN_MIDDLE);   
            cell.setBorder(1);
            table.addCell(cell);
            
            //simbolo moneda
            cell = new PdfPCell(new Paragraph("$",smallBoldFont));
            cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            cell.setVerticalAlignment(Element.ALIGN_MIDDLE);   
            cell.setBorder(1);
            table.addCell(cell);

            //columna suma cliente <=90 dias
            cell = new PdfPCell(new Paragraph( ((suma_total_menor_igual_90_mn<=0)? "":StringHelper.AgregaComas(StringHelper.roundDouble(suma_total_menor_igual_90_mn,2)) ) ,smallBoldFont));
            cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            cell.setVerticalAlignment(Element.ALIGN_MIDDLE);   
            cell.setBorder(1);
            table.addCell(cell);

            //simbolo moneda
            cell = new PdfPCell(new Paragraph("$",smallBoldFont));
            cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            cell.setVerticalAlignment(Element.ALIGN_MIDDLE);   
            cell.setBorder(1);
            table.addCell(cell);

            //columna suma cliente <=+90 dias
            cell = new PdfPCell(new Paragraph( ((suma_total_mayor_90_mn<=0)? "":StringHelper.AgregaComas(StringHelper.roundDouble(suma_total_mayor_90_mn,2)) ) ,smallBoldFont));
            cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            cell.setVerticalAlignment(Element.ALIGN_MIDDLE);   
            cell.setBorder(1);
            table.addCell(cell);
            
            //simbolo moneda
            cell = new PdfPCell(new Paragraph("$",smallBoldFont));
            cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            cell.setVerticalAlignment(Element.ALIGN_MIDDLE);   
            cell.setBorder(1);
            table.addCell(cell);
            
            cell = new PdfPCell(new Paragraph( ((suma_total_saldo_factura_total_mn<=0)? "":StringHelper.AgregaComas(StringHelper.roundDouble(suma_total_saldo_factura_total_mn,2)) ) ,smallBoldFont));
            cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            cell.setVerticalAlignment(Element.ALIGN_MIDDLE);   
            cell.setBorder(1);
            table.addCell(cell);
            
            //:::::::::::::aqui se AGREGA LOS TOTALES EN DOLARES:::::::::::::::::::::::::::::::::::::::::::::
            cell = new PdfPCell(new Paragraph("Total en USD",smallBoldFont));
            cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            cell.setVerticalAlignment(Element.ALIGN_MIDDLE);   
            cell.setColspan(3);
            cell.setBorder(0);
            table.addCell(cell);

            //simbolo moneda
            cell = new PdfPCell(new Paragraph("USD",smallBoldFont));
            cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            cell.setVerticalAlignment(Element.ALIGN_MIDDLE);   
            cell.setBorder(1);
            table.addCell(cell);
            
            //columna por vencer
            cell = new PdfPCell(new Paragraph( ((suma_total_por_vencer_usd<=0)? "":StringHelper.AgregaComas(StringHelper.roundDouble(suma_total_por_vencer_usd,2)) ) ,smallBoldFont));
            cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            cell.setVerticalAlignment(Element.ALIGN_MIDDLE);   
            cell.setBorder(1);
            table.addCell(cell);  

            //simbolo moneda
            cell = new PdfPCell(new Paragraph("USD",smallBoldFont));
            cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            cell.setVerticalAlignment(Element.ALIGN_MIDDLE);   
            cell.setBorder(1);
            table.addCell(cell);

            //columna suma cliente <=15 dias
            cell = new PdfPCell(new Paragraph( ((suma_total_menor_igual_15_usd<=0)? "":StringHelper.AgregaComas(StringHelper.roundDouble(suma_total_menor_igual_15_usd,2)) ) ,smallBoldFont));
            cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            cell.setVerticalAlignment(Element.ALIGN_MIDDLE);   
            cell.setBorder(1);
            table.addCell(cell);
                    
            //simbolo moneda
            cell = new PdfPCell(new Paragraph("USD",smallBoldFont));
            cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            cell.setVerticalAlignment(Element.ALIGN_MIDDLE);   
            cell.setBorder(1);
            table.addCell(cell);

            //columna suma cliente <=30 dias
            cell = new PdfPCell(new Paragraph( ((suma_total_menor_igual_30_usd<=0)? "":StringHelper.AgregaComas(StringHelper.roundDouble(suma_total_menor_igual_30_usd,2)) ) ,smallBoldFont));
            cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            cell.setVerticalAlignment(Element.ALIGN_MIDDLE);   
            cell.setBorder(1);
            table.addCell(cell);
            
            //simbolo moneda
            cell = new PdfPCell(new Paragraph("USD",smallBoldFont));
            cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            cell.setVerticalAlignment(Element.ALIGN_MIDDLE);   
            cell.setBorder(1);
            table.addCell(cell);
            
            //columna suma cliente <=45 dias
            cell = new PdfPCell(new Paragraph( ((suma_total_menor_igual_45_usd<=0)? "":StringHelper.AgregaComas(StringHelper.roundDouble(suma_total_menor_igual_45_usd,2)) ) ,smallBoldFont));
            cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            cell.setVerticalAlignment(Element.ALIGN_MIDDLE);   
            cell.setBorder(1);
            table.addCell(cell);

            //simbolo moneda
            cell = new PdfPCell(new Paragraph("USD",smallBoldFont));
            cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            cell.setVerticalAlignment(Element.ALIGN_MIDDLE);   
            cell.setBorder(1);
            table.addCell(cell);

            //columna suma cliente <=60 dias
            cell = new PdfPCell(new Paragraph( ((suma_total_menor_igual_60_usd<=0)? "":StringHelper.AgregaComas(StringHelper.roundDouble(suma_total_menor_igual_60_usd,2)) ) ,smallBoldFont));
            cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            cell.setVerticalAlignment(Element.ALIGN_MIDDLE);   
            cell.setBorder(1);
            table.addCell(cell);
            
            //simbolo moneda
            cell = new PdfPCell(new Paragraph("USD",smallBoldFont));
            cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            cell.setVerticalAlignment(Element.ALIGN_MIDDLE);   
            cell.setBorder(1);
            table.addCell(cell);

            //columna suma cliente <=90 dias
            cell = new PdfPCell(new Paragraph( ((suma_total_menor_igual_90_usd<=0)? "":StringHelper.AgregaComas(StringHelper.roundDouble(suma_total_menor_igual_90_usd,2)) ) ,smallBoldFont));
            cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            cell.setVerticalAlignment(Element.ALIGN_MIDDLE);   
            cell.setBorder(1);
            table.addCell(cell);

            //simbolo moneda
            cell = new PdfPCell(new Paragraph("USD",smallBoldFont));
            cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            cell.setVerticalAlignment(Element.ALIGN_MIDDLE);   
            cell.setBorder(1);
            table.addCell(cell);

            //columna suma cliente <=+90 dias
            cell = new PdfPCell(new Paragraph( ((suma_total_mayor_90_usd<=0)? "":StringHelper.AgregaComas(StringHelper.roundDouble(suma_total_mayor_90_usd,2)) ) ,smallBoldFont));
            cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            cell.setVerticalAlignment(Element.ALIGN_MIDDLE);   
            cell.setBorder(1);
            table.addCell(cell);
            
            //simbolo moneda
            cell = new PdfPCell(new Paragraph("USD",smallBoldFont));
            cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            cell.setVerticalAlignment(Element.ALIGN_MIDDLE);   
            cell.setBorder(1);
            table.addCell(cell);
            
            cell = new PdfPCell(new Paragraph( ((suma_total_saldo_factura_total_usd<=0)? "":StringHelper.AgregaComas(StringHelper.roundDouble(suma_total_saldo_factura_total_usd,2)) ) ,smallBoldFont));
            cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            cell.setVerticalAlignment(Element.ALIGN_MIDDLE);   
            cell.setBorder(1);
            table.addCell(cell);
            
                    
                    
            //aqui se agrega una FILA VACIA
            
            
            cell = new PdfPCell(new Paragraph("",smallBoldFont));
            cell.setHorizontalAlignment(Element.ALIGN_LEFT);
            cell.setVerticalAlignment(Element.ALIGN_MIDDLE);   
            cell.setColspan(19);
            cell.setBorder(1);
            cell.setFixedHeight(10);
            table.addCell(cell);
            
        }else{
            PdfPCell cellX = new PdfPCell(new Paragraph("No hay resultados que mostrar, seleccione otro cliente diferente.",smallBoldFont));
            cellX.setHorizontalAlignment(Element.ALIGN_LEFT);
            cellX.setVerticalAlignment(Element.ALIGN_MIDDLE);   
            cellX.setColspan(19);
            table.addCell(cellX);
        }
        
        doc.add(table);
        doc.close();
        
    }
    
    
    
    
    
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
            ColumnText.showTextAligned(writer.getDirectContent(),Element.ALIGN_CENTER, new Phrase("Reporte de Antigüedad de Saldos de Clientes",largeBoldFont),document.getPageSize().getWidth()/2, document.getPageSize().getTop()-38, 0);
            
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
