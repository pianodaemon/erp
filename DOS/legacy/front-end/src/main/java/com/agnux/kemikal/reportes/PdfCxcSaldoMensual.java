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
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;


public class PdfCxcSaldoMensual {
    /*en el metodo creamos el documento lo abrimosañadimos parrafos de texto y lo cerramos, tambien creamos nuestro archivo pdf*/
    public PdfCxcSaldoMensual(String fileout, String ruta_imagen, String tituloReporte, String razon_soc_empresa, String fecha_corte,ArrayList<HashMap<String, String>> facturas) throws DocumentException, IOException {
        //variables para totales del cliente
        double suma_monto_factura_cliente;
        double suma_importe_pagado_cliente;
        double suma_saldo_pendiente_cliente;
        
        String simbolo_moneda_pesos="";
        String simbolo_moneda_dolar="";
        String simbolo_moneda_euro="";
                
        //variables para totales de la moneda
        double suma_monto_factura_moneda_pesos;
        double suma_importe_pagado_moneda_pesos;
        double suma_saldo_pendiente_moneda_pesos;
        
        double suma_monto_factura_moneda_dolar=0;
        double suma_importe_pagado_moneda_dolar=0;
        double suma_saldo_pendiente_moneda_dolar=0;

        double suma_monto_factura_moneda_euro=0;
        double suma_importe_pagado_moneda_euro=0;
        double suma_saldo_pendiente_moneda_euro=0;
        
        String cliente_actual="";
        
        String[] fc = fecha_corte.split("-");
        String fecha_corte_formateado = "Fecha de Corte  "+ fc[2]+"/"+fc[1]+"/"+fc[0];
        
        try {
            //tipos de letras (font's)
            Font smallsmall = new Font(Font.getFamily("ARIAL"),13,Font.NORMAL);
            Font smallBoldFont = new Font(Font.getFamily("ARIAL"),8,Font.BOLD,BaseColor.BLACK);
            Font headerFont = new Font(Font.getFamily("ARIAL"),8,Font.BOLD,BaseColor.WHITE);
            Font smallBoldFont2 = new Font(Font.getFamily("ARIAL"),9,Font.BOLD,BaseColor.BLACK);
            
            Font largeBoldFont = new Font(Font.FontFamily.HELVETICA,10,Font.BOLD,BaseColor.BLACK);
            Font smallFont = new Font(Font.FontFamily.HELVETICA,8,Font.NORMAL,BaseColor.BLACK);
            
            HeaderFooter event = new HeaderFooter(tituloReporte, razon_soc_empresa, fecha_corte_formateado);
            Document doc = new Document(PageSize.LETTER,-50,-50,60,30);
            doc.addCreator("gpmarsan@gmail.com");
            PdfWriter writer = PdfWriter.getInstance(doc, new FileOutputStream(fileout));
            writer.setPageEvent(event);
            
            
            doc.open();
            
            //float [] widths = {2.5f, 2f, 3f, 1f, 3f, 1f, 3f, 2.5f, 1f, 3f};
            float [] widths = {2.5f, 2f, 3f, 1f, 3f, 1f, 3f, 1f, 3f};
            
            PdfPTable table = new PdfPTable(widths);
            PdfPCell cell;
            
            table.setKeepTogether(false);
            table.setHeaderRows(1);
            
            
            // Encabezado de Celda
            cell = new PdfPCell(new Paragraph("No.Factura",headerFont));
            cell.setUseAscender(true);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setUseDescender(true);
            cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            cell.setBackgroundColor(BaseColor.BLACK);
            cell.setFixedHeight(13);
            table.addCell(cell);
            
            cell = new PdfPCell(new Paragraph("Fecha",headerFont));
            cell.setUseAscender(true);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setUseDescender(true);
            cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            cell.setBackgroundColor(BaseColor.BLACK);
            cell.setFixedHeight(13);
            table.addCell(cell);
            
            cell = new PdfPCell(new Paragraph("Orden de Compra",headerFont));
            cell.setUseAscender(true);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setUseDescender(true);
            cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            cell.setBackgroundColor(BaseColor.BLACK);
            cell.setFixedHeight(13);
            table.addCell(cell);
            
            cell = new PdfPCell(new Paragraph("Monto Facturado",headerFont));
            cell.setUseAscender(true);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setUseDescender(true);
            cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            cell.setBackgroundColor(BaseColor.BLACK);
            cell.setFixedHeight(13);
            cell.setColspan(2);
            table.addCell(cell);
            
            cell = new PdfPCell(new Paragraph("Importe Pagado",headerFont));
            cell.setUseAscender(true);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setUseDescender(true);
            cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            cell.setBackgroundColor(BaseColor.BLACK);
            cell.setFixedHeight(13);
            cell.setColspan(2);
            table.addCell(cell);
            
            /*
            cell = new PdfPCell(new Paragraph("Ultimo Pago",headerFont));
            cell.setUseAscender(true);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setUseDescender(true);
            cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            cell.setBackgroundColor(BaseColor.BLACK);
            cell.setFixedHeight(13);
            table.addCell(cell);
             */
            
            cell = new PdfPCell(new Paragraph("Saldo Pendiente",headerFont));
            cell.setUseAscender(true);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setUseDescender(true);
            cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            cell.setBackgroundColor(BaseColor.BLACK);
            cell.setFixedHeight(13);
            cell.setColspan(2);
            table.addCell(cell);
            
            String simbolo_moneda="";
            if(facturas.size() > 0){
                simbolo_moneda=facturas.get(0).get("moneda_simbolo").toString();
                /*
                cell = new PdfPCell(new Paragraph("Adeudos en Moneda Nacional",largeBoldFont));
                //cell.setUseAscender(true);
                cell.setHorizontalAlignment(Element.ALIGN_LEFT);
                //cell.setUseDescender(true);
                cell.setVerticalAlignment(Element.ALIGN_BOTTOM);
                cell.setColspan(10);
                cell.setBorder(0);
                cell.setFixedHeight(18);
                table.addCell(cell);
                */
                
                
                //pintamos en el pdf la razon social del primer cliente
                cell = new PdfPCell(new Paragraph(facturas.get(0).get("cliente").toString(),smallBoldFont));
                //cell.setUseAscender(true);
                cell.setHorizontalAlignment(Element.ALIGN_LEFT);
                //cell.setUseDescender(true);
                cell.setVerticalAlignment(Element.ALIGN_BOTTOM);
                cell.setColspan(9);
                cell.setBorder(0);
                //cell.setFixedHeight(18);
                table.addCell(cell);
                
                //inicializar variables
                suma_monto_factura_cliente=0;
                suma_importe_pagado_cliente=0;
                suma_saldo_pendiente_cliente=0;
                
                simbolo_moneda_pesos="";
                suma_monto_factura_moneda_pesos=0;
                suma_importe_pagado_moneda_pesos=0;
                suma_saldo_pendiente_moneda_pesos=0;
                
                simbolo_moneda_dolar="";
                suma_monto_factura_moneda_dolar=0;
                suma_importe_pagado_moneda_dolar=0;
                suma_saldo_pendiente_moneda_dolar=0;
                
                simbolo_moneda_euro="";
                suma_monto_factura_moneda_euro=0;
                suma_importe_pagado_moneda_euro=0;
                suma_saldo_pendiente_moneda_euro=0;
                
                //obtiene el la razon social del primer cliente
                cliente_actual=facturas.get(0).get("cliente").toString();
                // Ciclo "FOR"
                for (int x=0; x<=facturas.size()-1;x++){
                    HashMap<String,String> registro = facturas.get(x);
                    
                    
                    if(cliente_actual.equals(registro.get("cliente").toString()) && simbolo_moneda.equals(registro.get("moneda_simbolo"))){
                        cell= new PdfPCell(new Paragraph(registro.get("serie_folio").toString(),smallFont));
                        cell.setVerticalAlignment (Element.ALIGN_CENTER);
                        cell.setBorder(0);
                        table.addCell(cell);
                        
                        cell= new PdfPCell(new Paragraph(registro.get("fecha_factura").toString(),smallFont));
                        cell.setHorizontalAlignment (Element.ALIGN_CENTER);
                        cell.setBorder(0);
                        table.addCell(cell);
                        
                        cell= new PdfPCell(new Paragraph(registro.get("orden_compra"),smallFont));
                        cell.setHorizontalAlignment (Element.ALIGN_LEFT);
                        cell.setBorder(0);
                        table.addCell(cell);
                        
                        //aqui va el simbolo de la moneda
                        cell= new PdfPCell(new Paragraph(simbolo_moneda,smallFont));
                        cell.setHorizontalAlignment (Element.ALIGN_RIGHT);
                        cell.setBorder(0);
                        table.addCell(cell);
                        
                        cell= new PdfPCell(new Paragraph(StringHelper.AgregaComas(registro.get("monto_factura").toString()),smallFont));
                        cell.setHorizontalAlignment (Element.ALIGN_RIGHT);
                        cell.setBorder(0);
                        table.addCell(cell);
                        
                        //aqui va el simbolo de la moneda
                        cell= new PdfPCell(new Paragraph(simbolo_moneda,smallFont));
                        cell.setHorizontalAlignment (Element.ALIGN_RIGHT);
                        cell.setBorder(0);
                        table.addCell(cell);
                        
                        cell= new PdfPCell(new Paragraph(StringHelper.AgregaComas(registro.get("importe_pagado").toString()),smallFont));
                        cell.setHorizontalAlignment (Element.ALIGN_RIGHT);
                        cell.setBorder(0);
                        table.addCell(cell);
                        
                        /*
                        cell= new PdfPCell(new Paragraph(StringHelper.isNullString(registro.get("ultimo_pago").replace("&nbsp;", " ")),smallFont));
                        cell.setHorizontalAlignment (Element.ALIGN_CENTER);
                        cell.setBorder(0);
                        table.addCell(cell);
                        */
                        
                        //aqui va el simbolo de la moneda
                        cell= new PdfPCell(new Paragraph(simbolo_moneda,smallFont));
                        cell.setHorizontalAlignment (Element.ALIGN_RIGHT);
                        cell.setBorder(0);
                        table.addCell(cell);
                        
                        cell= new PdfPCell(new Paragraph(StringHelper.AgregaComas(registro.get("saldo_factura").toString()),smallFont));
                        cell.setHorizontalAlignment (Element.ALIGN_RIGHT);
                        cell.setBorder(0);
                        table.addCell(cell);
                        
                        //acumular totales para el cliente actual
                        suma_monto_factura_cliente = suma_monto_factura_cliente+ Double.parseDouble(registro.get("monto_factura").toString());
                        suma_importe_pagado_cliente = suma_importe_pagado_cliente + Double.parseDouble(registro.get("importe_pagado").toString());
                        suma_saldo_pendiente_cliente = suma_saldo_pendiente_cliente + Double.parseDouble(registro.get("saldo_factura").toString());
                        
                        //acumular montos para PESOS                      
                        if(Integer.parseInt(registro.get("moneda_id"))==1){
                            suma_monto_factura_moneda_pesos = suma_monto_factura_moneda_pesos+ Double.parseDouble(registro.get("monto_factura").toString());
                            suma_importe_pagado_moneda_pesos = suma_importe_pagado_moneda_pesos + Double.parseDouble(registro.get("importe_pagado").toString());
                            suma_saldo_pendiente_moneda_pesos = suma_saldo_pendiente_moneda_pesos + Double.parseDouble(registro.get("saldo_factura").toString());
                            simbolo_moneda_pesos=registro.get("moneda_simbolo");
                        }
                        
                        //acumular montos para DOLARES                      
                        if(Integer.parseInt(registro.get("moneda_id"))==2){
                            suma_monto_factura_moneda_dolar = suma_monto_factura_moneda_dolar + Double.parseDouble(registro.get("monto_factura").toString());
                            suma_importe_pagado_moneda_dolar = suma_importe_pagado_moneda_dolar + Double.parseDouble(registro.get("importe_pagado").toString());
                            suma_saldo_pendiente_moneda_dolar = suma_saldo_pendiente_moneda_dolar + Double.parseDouble(registro.get("saldo_factura").toString());
                            simbolo_moneda_dolar=registro.get("moneda_simbolo");
                        }
                        
                        //acumular montos para EUROS                      
                        if(Integer.parseInt(registro.get("moneda_id"))==3){
                            suma_monto_factura_moneda_euro = suma_monto_factura_moneda_euro + Double.parseDouble(registro.get("monto_factura").toString());
                            suma_importe_pagado_moneda_euro = suma_importe_pagado_moneda_euro + Double.parseDouble(registro.get("importe_pagado").toString());
                            suma_saldo_pendiente_moneda_euro = suma_saldo_pendiente_moneda_euro + Double.parseDouble(registro.get("saldo_factura").toString());
                            simbolo_moneda_euro=registro.get("moneda_simbolo");
                        }
                    }else{
                        
                        //imprimir totales del cliente anterior
                        cell= new PdfPCell(new Paragraph("Totales del cliente",smallFont));
                        cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                        cell.setBorder(0);
                        cell.setColspan(3);
                        table.addCell(cell);
                        
                        //aqui va el simbolo de la moneda
                        cell= new PdfPCell(new Paragraph(simbolo_moneda,smallFont));
                        cell.setHorizontalAlignment (Element.ALIGN_RIGHT);
                        cell.setBorder(0);
                        table.addCell(cell);
                        
                        cell= new PdfPCell(new Paragraph(StringHelper.AgregaComas(StringHelper.roundDouble(suma_monto_factura_cliente,2)),smallFont));
                        cell.setHorizontalAlignment (Element.ALIGN_RIGHT);
                        cell.setBorder(1);
                        table.addCell(cell);
                        
                        //aqui va el simbolo de la moneda
                        cell= new PdfPCell(new Paragraph(simbolo_moneda,smallFont));
                        cell.setHorizontalAlignment (Element.ALIGN_RIGHT);
                        cell.setBorder(0);
                        table.addCell(cell);
                        
                        cell= new PdfPCell(new Paragraph(StringHelper.AgregaComas(StringHelper.roundDouble(suma_importe_pagado_cliente,2)),smallFont));
                        cell.setHorizontalAlignment (Element.ALIGN_RIGHT);
                        cell.setBorder(1);
                        table.addCell(cell);
                        
                        /*
                        cell= new PdfPCell(new Paragraph(" ",smallFont));
                        cell.setHorizontalAlignment (Element.ALIGN_CENTER);
                        cell.setBorder(0);
                        table.addCell(cell);
                        */
                        
                        //aqui va el simbolo de la moneda
                        cell= new PdfPCell(new Paragraph(simbolo_moneda,smallFont));
                        cell.setHorizontalAlignment (Element.ALIGN_RIGHT);
                        cell.setBorder(0);
                        table.addCell(cell);
                        
                        cell= new PdfPCell(new Paragraph(StringHelper.AgregaComas(StringHelper.roundDouble(suma_saldo_pendiente_cliente,2)),smallFont));
                        cell.setHorizontalAlignment (Element.ALIGN_RIGHT);
                        cell.setBorder(1);
                        table.addCell(cell);
                        
                        //ponemos una fila vacia para separar el cliente aterior con el nuevo cliente
                        cell = new PdfPCell(new Paragraph(" ",smallBoldFont2));
                        cell.setColspan(10);
                        cell.setBorder(0);
                        cell.setFixedHeight(16);
                        table.addCell(cell);
                        
                        
                        //obtiene razon social del nuevo cliente
                        cliente_actual=registro.get("cliente").toString();
                        simbolo_moneda=registro.get("moneda_simbolo").toString();
                        
                        //se inicializan variables para el nuevo cliente
                        suma_monto_factura_cliente=0;
                        suma_importe_pagado_cliente=0;
                        suma_saldo_pendiente_cliente=0;
                        
                        
                        //pintamos en el pdf la razon social del nuevo cliente
                        cell = new PdfPCell(new Paragraph(cliente_actual,smallBoldFont));
                        //cell.setUseAscender(true);
                        cell.setHorizontalAlignment(Element.ALIGN_LEFT);
                        //cell.setUseDescender(true);
                        cell.setVerticalAlignment(Element.ALIGN_BOTTOM);
                        cell.setColspan(9);
                        cell.setBorder(0);
                        //cell.setFixedHeight(18);
                        table.addCell(cell);
                        
                        
                        //aqui va la primera fila para el cliente nuevo
                        cell= new PdfPCell(new Paragraph(registro.get("serie_folio").toString(),smallFont));
                        cell.setVerticalAlignment (Element.ALIGN_CENTER);
                        cell.setBorder(0);
                        table.addCell(cell);
                        
                        cell= new PdfPCell(new Paragraph(registro.get("fecha_factura").toString(),smallFont));
                        cell.setHorizontalAlignment (Element.ALIGN_CENTER);
                        cell.setBorder(0);
                        table.addCell(cell);
                        
                        cell= new PdfPCell(new Paragraph(registro.get("orden_compra"),smallFont));
                        cell.setHorizontalAlignment (Element.ALIGN_LEFT);
                        cell.setBorder(0);
                        table.addCell(cell);
                        
                        //aqui va el simbolo de la moneda
                        cell= new PdfPCell(new Paragraph(simbolo_moneda,smallFont));
                        cell.setHorizontalAlignment (Element.ALIGN_RIGHT);
                        cell.setBorder(0);
                        table.addCell(cell);
                        
                        cell= new PdfPCell(new Paragraph(StringHelper.AgregaComas(registro.get("monto_factura").toString()),smallFont));
                        cell.setHorizontalAlignment (Element.ALIGN_RIGHT);
                        cell.setBorder(0);
                        table.addCell(cell);
                        
                        //aqui va el simbolo de la moneda
                        cell= new PdfPCell(new Paragraph(simbolo_moneda,smallFont));
                        cell.setHorizontalAlignment (Element.ALIGN_RIGHT);
                        cell.setBorder(0);
                        table.addCell(cell);
                        
                        cell= new PdfPCell(new Paragraph(StringHelper.AgregaComas(registro.get("importe_pagado").toString()),smallFont));
                        cell.setHorizontalAlignment (Element.ALIGN_RIGHT);
                        cell.setBorder(0);
                        table.addCell(cell);
                        
                        /*
                        cell= new PdfPCell(new Paragraph(registro.get("ultimo_pago").toString().replace("&nbsp;", " "),smallFont));
                        cell.setHorizontalAlignment (Element.ALIGN_CENTER);
                        cell.setBorder(0);
                        table.addCell(cell);
                        */
                        
                        //aqui va el simbolo de la moneda
                        cell= new PdfPCell(new Paragraph(simbolo_moneda,smallFont));
                        cell.setHorizontalAlignment (Element.ALIGN_RIGHT);
                        cell.setBorder(0);
                        table.addCell(cell);
                        
                        cell= new PdfPCell(new Paragraph(StringHelper.AgregaComas(registro.get("saldo_factura").toString()),smallFont));
                        cell.setHorizontalAlignment (Element.ALIGN_RIGHT);
                        cell.setBorder(0);
                        table.addCell(cell);
                        
                        //acumular totales del primer registro del nuevo cliente
                        suma_monto_factura_cliente = suma_monto_factura_cliente+ Double.parseDouble(registro.get("monto_factura").toString());
                        suma_importe_pagado_cliente = suma_importe_pagado_cliente + Double.parseDouble(registro.get("importe_pagado").toString());
                        suma_saldo_pendiente_cliente = suma_saldo_pendiente_cliente + Double.parseDouble(registro.get("saldo_factura").toString());
                        
                        //acumular montos para PESOS                      
                        if(Integer.parseInt(registro.get("moneda_id"))==1){
                            suma_monto_factura_moneda_pesos = suma_monto_factura_moneda_pesos+ Double.parseDouble(registro.get("monto_factura").toString());
                            suma_importe_pagado_moneda_pesos = suma_importe_pagado_moneda_pesos + Double.parseDouble(registro.get("importe_pagado").toString());
                            suma_saldo_pendiente_moneda_pesos = suma_saldo_pendiente_moneda_pesos + Double.parseDouble(registro.get("saldo_factura").toString());
                            simbolo_moneda_pesos=registro.get("moneda_simbolo");
                        }
                        
                        //acumular montos para DOLARES                      
                        if(Integer.parseInt(registro.get("moneda_id"))==2){
                            suma_monto_factura_moneda_dolar = suma_monto_factura_moneda_dolar + Double.parseDouble(registro.get("monto_factura").toString());
                            suma_importe_pagado_moneda_dolar = suma_importe_pagado_moneda_dolar + Double.parseDouble(registro.get("importe_pagado").toString());
                            suma_saldo_pendiente_moneda_dolar = suma_saldo_pendiente_moneda_dolar + Double.parseDouble(registro.get("saldo_factura").toString());
                            simbolo_moneda_dolar=registro.get("moneda_simbolo");
                        }
                        
                        //acumular montos para EUROS                      
                        if(Integer.parseInt(registro.get("moneda_id"))==3){
                            suma_monto_factura_moneda_euro = suma_monto_factura_moneda_euro + Double.parseDouble(registro.get("monto_factura").toString());
                            suma_importe_pagado_moneda_euro = suma_importe_pagado_moneda_euro + Double.parseDouble(registro.get("importe_pagado").toString());
                            suma_saldo_pendiente_moneda_euro = suma_saldo_pendiente_moneda_euro + Double.parseDouble(registro.get("saldo_factura").toString());
                            simbolo_moneda_euro=registro.get("moneda_simbolo");
                        }
                        
                    }
                    
                }
                
                //imprimir totales del ultimo cliente
                cell= new PdfPCell(new Paragraph("Totales del cliente",smallFont));
                cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                cell.setBorder(0);
                cell.setColspan(3);
                table.addCell(cell);

                //aqui va el simbolo de la moneda
                cell= new PdfPCell(new Paragraph(simbolo_moneda,smallFont));
                cell.setHorizontalAlignment (Element.ALIGN_RIGHT);
                cell.setBorder(0);
                table.addCell(cell);
                        
                cell= new PdfPCell(new Paragraph(StringHelper.AgregaComas(StringHelper.roundDouble(suma_monto_factura_cliente,2)),smallFont));
                cell.setHorizontalAlignment (Element.ALIGN_RIGHT);
                cell.setBorder(1);
                table.addCell(cell);
                
                //aqui va el simbolo de la moneda
                cell= new PdfPCell(new Paragraph(simbolo_moneda,smallFont));
                cell.setHorizontalAlignment (Element.ALIGN_RIGHT);
                cell.setBorder(0);
                table.addCell(cell);
                
                cell= new PdfPCell(new Paragraph(StringHelper.AgregaComas(StringHelper.roundDouble(suma_importe_pagado_cliente,2)),smallFont));
                cell.setHorizontalAlignment (Element.ALIGN_RIGHT);
                cell.setBorder(1);
                table.addCell(cell);
                
                /*
                cell= new PdfPCell(new Paragraph(" ",smallFont));
                cell.setHorizontalAlignment (Element.ALIGN_CENTER);
                cell.setBorder(0);
                table.addCell(cell);
                */
                
                //aqui va el simbolo de la moneda
                cell= new PdfPCell(new Paragraph(simbolo_moneda,smallFont));
                cell.setHorizontalAlignment (Element.ALIGN_RIGHT);
                cell.setBorder(0);
                table.addCell(cell);
                
                cell= new PdfPCell(new Paragraph(StringHelper.AgregaComas(StringHelper.roundDouble(suma_saldo_pendiente_cliente,2)),smallFont));
                cell.setHorizontalAlignment (Element.ALIGN_RIGHT);
                cell.setBorder(1);
                table.addCell(cell);
                
                
                //fila vacia para separar totales
                cell = new PdfPCell(new Paragraph(" ",smallBoldFont2));
                cell.setColspan(9);
                cell.setBorder(0);
                cell.setFixedHeight(15);
                table.addCell(cell);
                
                if(suma_monto_factura_moneda_pesos>0 || suma_importe_pagado_moneda_pesos>0 || suma_saldo_pendiente_moneda_pesos>0){
                    //totales en PESOS------------------------------------------------------------------
                    cell= new PdfPCell(new Paragraph("Totales en M.N.",smallBoldFont2));
                    cell.setUseAscender(true);
                    cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                    cell.setVerticalAlignment(Element.ALIGN_CENTER);
                    cell.setUseDescender(true);
                    cell.setBorder(0);
                    cell.setColspan(3);
                    table.addCell(cell);

                    //aqui va el simbolo de la moneda
                    cell= new PdfPCell(new Paragraph(simbolo_moneda_pesos,smallBoldFont));
                    cell.setHorizontalAlignment (Element.ALIGN_RIGHT);
                    cell.setVerticalAlignment(Element.ALIGN_CENTER);
                    cell.setBorder(7);
                    table.addCell(cell);

                    cell= new PdfPCell(new Paragraph(StringHelper.AgregaComas(StringHelper.roundDouble(suma_monto_factura_moneda_pesos,2)),smallBoldFont));
                    cell.setUseAscender(true);
                    cell.setHorizontalAlignment (Element.ALIGN_RIGHT);
                    cell.setVerticalAlignment(Element.ALIGN_CENTER);
                    cell.setUseDescender(true);
                    cell.setBorder(3);
                    table.addCell(cell);

                    //aqui va el simbolo de la moneda
                    cell= new PdfPCell(new Paragraph(simbolo_moneda_pesos,smallBoldFont));
                    cell.setHorizontalAlignment (Element.ALIGN_RIGHT);
                    cell.setVerticalAlignment(Element.ALIGN_CENTER);
                    cell.setUseAscender(true);
                    cell.setUseDescender(true);
                    cell.setBorder(3);
                    table.addCell(cell);

                    cell= new PdfPCell(new Paragraph(StringHelper.AgregaComas(StringHelper.roundDouble(suma_importe_pagado_moneda_pesos,2)),smallBoldFont));
                    cell.setUseAscender(true);
                    cell.setHorizontalAlignment (Element.ALIGN_RIGHT);
                    cell.setVerticalAlignment(Element.ALIGN_CENTER);
                    cell.setUseDescender(true);
                    cell.setBorder(3);
                    table.addCell(cell);
                    
                    /*
                    cell= new PdfPCell(new Paragraph(" ",smallFont));
                    cell.setUseAscender(true);
                    cell.setHorizontalAlignment (Element.ALIGN_CENTER);
                    cell.setVerticalAlignment(Element.ALIGN_CENTER);
                    cell.setUseDescender(true);
                    cell.setBorder(3);
                    table.addCell(cell);
                     */
                    
                    //aqui va el simbolo de la moneda
                    cell= new PdfPCell(new Paragraph(simbolo_moneda_pesos,smallBoldFont));
                    cell.setHorizontalAlignment (Element.ALIGN_RIGHT);
                    cell.setVerticalAlignment(Element.ALIGN_CENTER);
                    cell.setUseAscender(true);
                    cell.setUseDescender(true);
                    cell.setBorder(3);
                    table.addCell(cell);

                    cell= new PdfPCell(new Paragraph(StringHelper.AgregaComas(StringHelper.roundDouble(suma_saldo_pendiente_moneda_pesos,2)),smallBoldFont));
                    cell.setUseAscender(true);
                    cell.setHorizontalAlignment (Element.ALIGN_RIGHT);
                    cell.setVerticalAlignment(Element.ALIGN_CENTER);
                    cell.setUseDescender(true);
                    cell.setBorder(11);
                    table.addCell(cell);
                }
                
                //TERMINA en PESOS------------------------------------------------------------------
                
                
                //INICIA TOTALES EN DOLARES------------------------------------------------------------------
                //fila vacia para separar totales
                cell = new PdfPCell(new Paragraph(" ",smallBoldFont2));
                cell.setColspan(9);
                cell.setBorder(0);
                cell.setFixedHeight(10);
                table.addCell(cell);
                
                if(suma_monto_factura_moneda_dolar>0 || suma_importe_pagado_moneda_dolar>0 || suma_saldo_pendiente_moneda_dolar>0){
                    cell= new PdfPCell(new Paragraph("Totales en Dolares",smallBoldFont2));
                    cell.setUseAscender(true);
                    cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                    cell.setVerticalAlignment(Element.ALIGN_CENTER);
                    cell.setUseDescender(true);
                    cell.setBorder(0);
                    cell.setColspan(3);
                    table.addCell(cell);

                    //aqui va el simbolo de la moneda
                    cell= new PdfPCell(new Paragraph(simbolo_moneda_dolar,smallBoldFont));
                    cell.setHorizontalAlignment (Element.ALIGN_RIGHT);
                    cell.setVerticalAlignment(Element.ALIGN_CENTER);
                    cell.setBorder(7);
                    table.addCell(cell);

                    cell= new PdfPCell(new Paragraph(StringHelper.AgregaComas(StringHelper.roundDouble(suma_monto_factura_moneda_dolar,2)),smallBoldFont));
                    cell.setUseAscender(true);
                    cell.setHorizontalAlignment (Element.ALIGN_RIGHT);
                    cell.setVerticalAlignment(Element.ALIGN_CENTER);
                    cell.setUseDescender(true);
                    cell.setBorder(3);
                    table.addCell(cell);

                    //aqui va el simbolo de la moneda
                    cell= new PdfPCell(new Paragraph(simbolo_moneda_dolar,smallBoldFont));
                    cell.setHorizontalAlignment (Element.ALIGN_RIGHT);
                    cell.setVerticalAlignment(Element.ALIGN_CENTER);
                    cell.setUseAscender(true);
                    cell.setUseDescender(true);
                    cell.setBorder(3);
                    table.addCell(cell);

                    cell= new PdfPCell(new Paragraph(StringHelper.AgregaComas(StringHelper.roundDouble(suma_importe_pagado_moneda_dolar,2)),smallBoldFont));
                    cell.setUseAscender(true);
                    cell.setHorizontalAlignment (Element.ALIGN_RIGHT);
                    cell.setVerticalAlignment(Element.ALIGN_CENTER);
                    cell.setUseDescender(true);
                    cell.setBorder(3);
                    table.addCell(cell);
                    
                    /*
                    cell= new PdfPCell(new Paragraph(" ",smallFont));
                    cell.setUseAscender(true);
                    cell.setHorizontalAlignment (Element.ALIGN_CENTER);
                    cell.setVerticalAlignment(Element.ALIGN_CENTER);
                    cell.setUseDescender(true);
                    cell.setBorder(3);
                    table.addCell(cell);
                     */
                    
                    //aqui va el simbolo de la moneda
                    cell= new PdfPCell(new Paragraph(simbolo_moneda_dolar,smallBoldFont));
                    cell.setHorizontalAlignment (Element.ALIGN_RIGHT);
                    cell.setVerticalAlignment(Element.ALIGN_CENTER);
                    cell.setUseAscender(true);
                    cell.setUseDescender(true);
                    cell.setBorder(3);
                    table.addCell(cell);

                    cell= new PdfPCell(new Paragraph(StringHelper.AgregaComas(StringHelper.roundDouble(suma_saldo_pendiente_moneda_dolar,2)),smallBoldFont));
                    cell.setUseAscender(true);
                    cell.setHorizontalAlignment (Element.ALIGN_RIGHT);
                    cell.setVerticalAlignment(Element.ALIGN_CENTER);
                    cell.setUseDescender(true);
                    cell.setBorder(11);
                    table.addCell(cell);
                }
                //TERMINA en DOLARES------------------------------------------------------------------
                
                //INICIA TOTALES EN EUROS------------------------------------------------------------------
                //fila vacia para separar totales
                cell = new PdfPCell(new Paragraph(" ",smallBoldFont2));
                cell.setColspan(9);
                cell.setBorder(0);
                cell.setFixedHeight(10);
                table.addCell(cell);
                
                if(suma_monto_factura_moneda_euro>0 || suma_importe_pagado_moneda_euro>0 || suma_saldo_pendiente_moneda_euro>0){
                    cell= new PdfPCell(new Paragraph("Totales en Euros",smallBoldFont2));
                    cell.setUseAscender(true);
                    cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                    cell.setVerticalAlignment(Element.ALIGN_CENTER);
                    cell.setUseDescender(true);
                    cell.setBorder(0);
                    cell.setColspan(3);
                    table.addCell(cell);

                    //aqui va el simbolo de la moneda
                    cell= new PdfPCell(new Paragraph(simbolo_moneda_euro,smallBoldFont));
                    cell.setHorizontalAlignment (Element.ALIGN_RIGHT);
                    cell.setVerticalAlignment(Element.ALIGN_CENTER);
                    cell.setBorder(7);
                    table.addCell(cell);

                    cell= new PdfPCell(new Paragraph(StringHelper.AgregaComas(StringHelper.roundDouble(suma_monto_factura_moneda_euro,2)),smallBoldFont));
                    cell.setUseAscender(true);
                    cell.setHorizontalAlignment (Element.ALIGN_RIGHT);
                    cell.setVerticalAlignment(Element.ALIGN_CENTER);
                    cell.setUseDescender(true);
                    cell.setBorder(3);
                    table.addCell(cell);

                    //aqui va el simbolo de la moneda
                    cell= new PdfPCell(new Paragraph(simbolo_moneda_euro,smallBoldFont));
                    cell.setHorizontalAlignment (Element.ALIGN_RIGHT);
                    cell.setVerticalAlignment(Element.ALIGN_CENTER);
                    cell.setUseAscender(true);
                    cell.setUseDescender(true);
                    cell.setBorder(3);
                    table.addCell(cell);

                    cell= new PdfPCell(new Paragraph(StringHelper.AgregaComas(StringHelper.roundDouble(suma_importe_pagado_moneda_euro,2)),smallBoldFont));
                    cell.setUseAscender(true);
                    cell.setHorizontalAlignment (Element.ALIGN_RIGHT);
                    cell.setVerticalAlignment(Element.ALIGN_CENTER);
                    cell.setUseDescender(true);
                    cell.setBorder(3);
                    table.addCell(cell);
                    /*
                    cell= new PdfPCell(new Paragraph(" ",smallFont));
                    cell.setUseAscender(true);
                    cell.setHorizontalAlignment (Element.ALIGN_CENTER);
                    cell.setVerticalAlignment(Element.ALIGN_CENTER);
                    cell.setUseDescender(true);
                    cell.setBorder(3);
                    table.addCell(cell);
                     */
                    
                    //aqui va el simbolo de la moneda
                    cell= new PdfPCell(new Paragraph(simbolo_moneda_euro,smallBoldFont));
                    cell.setHorizontalAlignment (Element.ALIGN_RIGHT);
                    cell.setVerticalAlignment(Element.ALIGN_CENTER);
                    cell.setUseAscender(true);
                    cell.setUseDescender(true);
                    cell.setBorder(3);
                    table.addCell(cell);

                    cell= new PdfPCell(new Paragraph(StringHelper.AgregaComas(StringHelper.roundDouble(suma_saldo_pendiente_moneda_euro,2)),smallBoldFont));
                    cell.setUseAscender(true);
                    cell.setHorizontalAlignment (Element.ALIGN_RIGHT);
                    cell.setVerticalAlignment(Element.ALIGN_CENTER);
                    cell.setUseDescender(true);
                    cell.setBorder(11);
                    table.addCell(cell);

                }
                 
                //TERMINA en EUROS------------------------------------------------------------------
            }
            
            doc.add(table);   
            doc.close();
            
        } catch (FileNotFoundException ex) {
            Logger.getLogger(PdfCxcSaldoMensual.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    
    
    
    
    
    static class HeaderFooter extends PdfPageEventHelper {
        protected PdfTemplate total;       
        protected BaseFont helv;  
        protected PdfContentByte cb;  
        Font largeBoldFont = new Font(Font.FontFamily.HELVETICA,10,Font.BOLD,BaseColor.BLACK);
        Font largeFont = new Font(Font.FontFamily.HELVETICA,10,Font.NORMAL,BaseColor.BLACK);
        Font smallFont = new Font(Font.FontFamily.HELVETICA,7,Font.NORMAL,BaseColor.BLACK);
        
        private String tituloReporte;
        private String empresa;
        private String periodo;
        
        public String getTituloReporte() {
            return tituloReporte;
        }

        public void setTituloReporte(String tituloReporte) {
            this.tituloReporte = tituloReporte;
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
        
        HeaderFooter(String tituloReporte, String razon_soc_empresa, String periodo){
            this.setTituloReporte(tituloReporte);
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
            ColumnText.showTextAligned(writer.getDirectContent(),Element.ALIGN_CENTER, new Phrase(this.getTituloReporte(),largeBoldFont),document.getPageSize().getWidth()/2, document.getPageSize().getTop()-38, 0);
            ColumnText.showTextAligned(writer.getDirectContent(),Element.ALIGN_CENTER, new Phrase(this.getPeriodo(),largeFont),document.getPageSize().getWidth()/2, document.getPageSize().getTop()-50, 0);
            
            SimpleDateFormat formato = new SimpleDateFormat("'Impreso en' MMMMM d, yyyy 'a las' HH:mm:ss 'hrs.'");
            String impreso_en = formato.format(new Date());
            
            cb = writer.getDirectContent();
            
            cb.beginText();  
            cb.setFontAndSize(helv, 7);  
            cb.setTextMatrix(document.left()+75, document.bottom() - 20 );  //definir la posicion de text
            cb.showText(impreso_en);
            cb.endText();
            
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
   }
    
}
