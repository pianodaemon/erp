/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.agnux.kemikal.reportes;

import com.agnux.common.helpers.StringHelper;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

/**
 *
 * @author valentin.vale8490@gmail.com
 */

public class PdfReporteProgramacionPagos {
    public String  empresa_emisora;
    public java.util.List<HashMap<String, String>> rows;

    public java.util.List<HashMap<String, String>> getRows() {
        return rows;
    }

    public void setRows(java.util.List<HashMap<String, String>> rows) {
        this.rows = rows;
    }
    

    

    public String getEmpresa_emisora() {
        return empresa_emisora;
    }

    public void setEmpresa_emisora(String empresa_emisora) {
        this.empresa_emisora = empresa_emisora;
    }

    
    
    public PdfReporteProgramacionPagos(ArrayList<HashMap<String, String>> lista_pronostico, String razon_social_empresa, String fileout) {
    this.setEmpresa_emisora(razon_social_empresa);
        //this.setEmpresa_emisora(razon_social_empresa);
        PdfReporteProgramacionPagos.HeaderFooter event = new PdfReporteProgramacionPagos.HeaderFooter();
        
        //Font largeBoldFont = new Font(Font.FontFamily.HELVETICA,8,Font.BOLD,BaseColor.BLACK); 
        //Font smallFont = new Font(Font.FontFamily.HELVETICA,6,Font.NORMAL,BaseColor.BLACK);
        
        Font fontCols = new Font(Font.FontFamily.HELVETICA, 9,Font.NORMAL);
        Font smallFont = new Font(Font.FontFamily.HELVETICA,8,Font.NORMAL,BaseColor.BLACK);
        Font smallBoldFont = new Font(Font.getFamily("ARIAL"),8,Font.BOLD,BaseColor.WHITE);
        Font smallFontN = new Font(Font.FontFamily.HELVETICA,8,Font.BOLD,BaseColor.BLACK);
        PdfPTable table_titulos;
        PdfPTable table_iteraciones = null;
        
        PdfPCell cell;
        try{
            
            Document document = new Document(PageSize.LETTER,-50,-50,60,30);
            PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(fileout));
            writer.setPageEvent(event);
            document.open();
            //TABLA DE FECHAS 
            
            this.setRows(lista_pronostico);
            float []   fechas = {1f,1f};
            table_titulos = new PdfPTable(fechas);
            table_titulos.setKeepTogether(false);
            table_titulos.setKeepTogether(true);
            
            //String[] titulos = {"Cliente","Factura","Lunes","Martes","Miercoles","Jueves","Viernes","Total"};
          int p_reg = 0;
           for (HashMap<String, String>  k : this.getRows()){
                        if(p_reg == 0){
                                PdfPCell celda = null;
                                
                                celda = new PdfPCell(new Paragraph( "Del:  " + k.get("lunes_proximo"),smallFont));
                                celda.setHorizontalAlignment(Element.ALIGN_RIGHT);
                                celda.setBorder(0);
                                table_titulos.addCell(celda);
                              
                                celda = new PdfPCell(new Paragraph("        Al:        " +k.get("viernes_proximo"),smallFont));
                                celda.setUseAscender(true);
                                celda.setHorizontalAlignment(Element.ALIGN_LEFT);
                                celda.setBorder(0);
                                table_titulos.addCell(celda);
                                
                                
                                celda = new PdfPCell(new Paragraph("   ",smallFont));
                                celda.setHorizontalAlignment(Element.ALIGN_LEFT);
                                celda.setColspan(2);
                                celda.setBorder(0);
                                celda.setFixedHeight(15);
                                table_titulos.addCell(celda);
                        }
                        p_reg= 1;
           
           
         }
            document.add(table_titulos);
            //FIN DE LA TABLA DE FECHAS
 
            
            
            
            float [] widths = {4f,1f,1f,1f,1f,1f,1f,1f};
            table_titulos = new PdfPTable(widths);
            table_titulos.setKeepTogether(false);
            table_titulos.setKeepTogether(true);
            
            String[] titulos = {"Proveedor","Factura","Lunes","Martes","Miercoles","Jueves","Viernes","Total"};
            
            for (int i = 0; i<=titulos.length -1; i++){
                cell = new PdfPCell(new Paragraph(titulos[i],smallBoldFont));
                String cliente = titulos[i];
                if(titulos[0] == "Proveedor"){
                cell.setHorizontalAlignment(Element.ALIGN_LEFT);
                }else{
                    cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                }
               
                cell.setUseAscender(true);
                cell.setUseDescender(true);
                cell.setBackgroundColor(BaseColor.BLACK);
                cell.setBorder(0);
                cell.setBorderWidthLeft(0);
                table_titulos.addCell(cell);
            }
            document.add(table_titulos);
            

            int primer_registro = 0;
            String cliente = "";
            
            int tmp = 0;
            String simbolo_moneda = "$";
            //table_iteraciones
            System.out.print(lista_pronostico.size());
            
            
           float [] filas = {4f,1f,1f,1f,1f,1f,1f,1f};
            table_iteraciones = new PdfPTable(filas);
            table_iteraciones.setKeepTogether(false);
            table_iteraciones.setKeepTogether(true);
            
            double  t_lunes = 0.0;
            double  t_martes = 0.0;
            double  t_miercoles = 0.0;
            double  t_jueves = 0.0;
            double  t_viernes = 0.0;
            double  t_total = 0.0;
            
            double  tg_lunes = 0.0;
            double  tg_martes = 0.0;
            double  tg_miercoles = 0.0;
            double  tg_jueves = 0.0;
            double  tg_viernes = 0.0;
            double  tg_total = 0.0;
            
            for (HashMap<String, String>  i : this.getRows()){
                //Indices del HashMap que representa el row
               String[] wordList = {"proveedor","factura","lunes","martes","miercoles","jueves","viernes","total"};
               java.util.List<String>  rows= (java.util.List<String>) Arrays.asList(wordList);
               System.out.print("Esto es lo que trae el arreglo de objetos");
               System.out.print(rows);
                if(cliente.equals(i.get("proveedor"))){
                   if(primer_registro == 0){
                         for (String omega : rows){
                                PdfPCell celda = null;

                                if (omega.equals("proveedor")){
                                    cliente = i.get(omega);
                                        celda = new PdfPCell(new Paragraph("",smallFont));
                                        celda.setHorizontalAlignment(Element.ALIGN_LEFT);
                                        celda.setVerticalAlignment(Element.ALIGN_MIDDLE);
                                    
                                }

                                if (omega.equals("factura")){
                                    celda = new PdfPCell(new Paragraph(i.get(omega),smallFont));
                                    celda.setHorizontalAlignment(Element.ALIGN_RIGHT);
                                    celda.setVerticalAlignment(Element.ALIGN_MIDDLE);
                                }
                                
                                /*if (omega.equals("pesos")){
                                    celda = new PdfPCell(new Paragraph("$",smallFont));
                                    celda.setHorizontalAlignment(Element.ALIGN_RIGHT);
                                    celda.setVerticalAlignment(Element.ALIGN_MIDDLE);
                                }*/
                                 
                                if (omega.equals("lunes")){
                                    celda = new PdfPCell(new Paragraph(StringHelper.AgregaComas(StringHelper.roundDouble(i.get(omega),2)),smallFont));
                                    celda.setHorizontalAlignment(Element.ALIGN_RIGHT);
                                    celda.setVerticalAlignment(Element.ALIGN_MIDDLE);
                                    t_lunes = t_lunes + Double.valueOf(i.get(omega)).doubleValue();
                                    tg_lunes = tg_lunes + Double.valueOf(i.get(omega)).doubleValue();
                                }

                                if (omega.equals("martes")){
                                    celda = new PdfPCell(new Paragraph(StringHelper.AgregaComas(StringHelper.roundDouble(i.get(omega),2)),smallFont));
                                    celda.setHorizontalAlignment(Element.ALIGN_RIGHT);
                                    celda.setVerticalAlignment(Element.ALIGN_MIDDLE);
                                    t_martes = t_martes + Double.valueOf(i.get(omega)).doubleValue();
                                    tg_martes = tg_martes + Double.valueOf(i.get(omega)).doubleValue();
                                }



                                if (omega.equals("miercoles")){
                                    celda = new PdfPCell(new Paragraph(StringHelper.AgregaComas(StringHelper.roundDouble(i.get(omega),2)),smallFont));
                                    celda.setHorizontalAlignment(Element.ALIGN_RIGHT);
                                    celda.setVerticalAlignment(Element.ALIGN_MIDDLE);
                                    t_miercoles = t_miercoles + Double.valueOf(i.get(omega)).doubleValue();
                                    tg_miercoles = tg_miercoles + Double.valueOf(i.get(omega)).doubleValue();
                                }
                                if (omega.equals("jueves")){
                                    celda = new PdfPCell(new Paragraph(StringHelper.AgregaComas(StringHelper.roundDouble(i.get(omega),2)),smallFont));
                                    celda.setHorizontalAlignment(Element.ALIGN_RIGHT);
                                    celda.setVerticalAlignment(Element.ALIGN_MIDDLE);
                                    t_jueves = t_jueves + Double.valueOf(i.get(omega)).doubleValue();
                                    tg_jueves = tg_jueves + Double.valueOf(i.get(omega)).doubleValue();
                                }
                                if (omega.equals("viernes")){
                                    celda = new PdfPCell(new Paragraph(StringHelper.AgregaComas(StringHelper.roundDouble(i.get(omega),2)),smallFont));
                                    celda.setHorizontalAlignment(Element.ALIGN_RIGHT);
                                    celda.setVerticalAlignment(Element.ALIGN_MIDDLE);
                                    t_viernes = t_viernes + Double.valueOf(i.get(omega)).doubleValue();
                                    tg_viernes = tg_viernes + Double.valueOf(i.get(omega)).doubleValue();
                                }
                                if (omega.equals("total")){
                                    celda = new PdfPCell(new Paragraph(StringHelper.AgregaComas(StringHelper.roundDouble(i.get(omega),2)),smallFont));
                                    celda.setHorizontalAlignment(Element.ALIGN_RIGHT);
                                    celda.setVerticalAlignment(Element.ALIGN_MIDDLE);
                                    t_total = t_total + Double.valueOf(i.get(omega)).doubleValue();
                                    tg_total = tg_total + Double.valueOf(i.get(omega)).doubleValue();
                                }


                                celda.setBorder(0);
                                table_iteraciones.addCell(celda);
                            }
                    }  
                   
                   if(primer_registro != 0){
                                for (String omega : rows){
                                PdfPCell celda = null;
                                
                                if (omega.equals("proveedor")){
                                    celda = new PdfPCell(new Paragraph("",smallFont));
                                    celda.setHorizontalAlignment(Element.ALIGN_LEFT);
                                    celda.setVerticalAlignment(Element.ALIGN_MIDDLE);
                                    
                                }

                                if (omega.equals("factura")){
                                    celda = new PdfPCell(new Paragraph(i.get(omega),smallFont));
                                    celda.setHorizontalAlignment(Element.ALIGN_RIGHT);
                                    celda.setVerticalAlignment(Element.ALIGN_MIDDLE);
                                }
                                
                               /* if (omega.equals("pesos")){
                                    celda = new PdfPCell(new Paragraph(i.get(omega),smallFont));
                                    celda.setHorizontalAlignment(Element.ALIGN_RIGHT);
                                    celda.setVerticalAlignment(Element.ALIGN_MIDDLE);
                                }*/
                                
                                if (omega.equals("lunes")){
                                    celda = new PdfPCell(new Paragraph(StringHelper.AgregaComas(StringHelper.roundDouble(i.get(omega),2)),smallFont));
                                    celda.setHorizontalAlignment(Element.ALIGN_RIGHT);
                                    celda.setVerticalAlignment(Element.ALIGN_MIDDLE);
                                    
                                    t_lunes = t_lunes + Double.valueOf(i.get(omega)).doubleValue();
                                    tg_lunes = tg_lunes + Double.valueOf(i.get(omega)).doubleValue();
                                }

                                if (omega.equals("martes")){
                                    celda = new PdfPCell(new Paragraph(StringHelper.AgregaComas(StringHelper.roundDouble(i.get(omega),2)),smallFont));
                                    celda.setHorizontalAlignment(Element.ALIGN_RIGHT);
                                    celda.setVerticalAlignment(Element.ALIGN_MIDDLE);
                                    t_martes = t_martes + Double.valueOf(i.get(omega)).doubleValue();
                                    tg_martes = tg_martes + Double.valueOf(i.get(omega)).doubleValue();
                                    
                                }



                                if (omega.equals("miercoles")){
                                    celda = new PdfPCell(new Paragraph(StringHelper.AgregaComas(StringHelper.roundDouble(i.get(omega),2)),smallFont));
                                    celda.setHorizontalAlignment(Element.ALIGN_RIGHT);
                                    celda.setVerticalAlignment(Element.ALIGN_MIDDLE);
                                    t_miercoles = t_miercoles + Double.valueOf(i.get(omega)).doubleValue();
                                    tg_miercoles = tg_miercoles + Double.valueOf(i.get(omega)).doubleValue();
                                    
                                }
                                if (omega.equals("jueves")){
                                    celda = new PdfPCell(new Paragraph(StringHelper.AgregaComas(StringHelper.roundDouble(i.get(omega),2)),smallFont));
                                    celda.setHorizontalAlignment(Element.ALIGN_RIGHT);
                                    celda.setVerticalAlignment(Element.ALIGN_MIDDLE);
                                    t_jueves = t_jueves + Double.valueOf(i.get(omega)).doubleValue();
                                    tg_jueves = tg_jueves + Double.valueOf(i.get(omega)).doubleValue();
                                }
                                if (omega.equals("viernes")){
                                    celda = new PdfPCell(new Paragraph(StringHelper.AgregaComas(StringHelper.roundDouble(i.get(omega),2)),smallFont));
                                    celda.setHorizontalAlignment(Element.ALIGN_RIGHT);
                                    celda.setVerticalAlignment(Element.ALIGN_MIDDLE);
                                    t_viernes = t_viernes + Double.valueOf(i.get(omega)).doubleValue();
                                    tg_viernes = tg_viernes + Double.valueOf(i.get(omega)).doubleValue();
                                    
                                }
                                if (omega.equals("total")){
                                    celda = new PdfPCell(new Paragraph(StringHelper.AgregaComas(StringHelper.roundDouble(i.get(omega),2)),smallFont));
                                    celda.setHorizontalAlignment(Element.ALIGN_RIGHT);
                                    celda.setVerticalAlignment(Element.ALIGN_MIDDLE);
                                    t_total = t_total + Double.valueOf(i.get(omega)).doubleValue();
                                    tg_total = tg_total + Double.valueOf(i.get(omega)).doubleValue();
                                    
                                }


                                celda.setBorder(0);
                                table_iteraciones.addCell(celda);
                            }
                    } 
                   cliente = i.get("proveedor");
                   primer_registro = 1;
                   
                   
                }else{          
                                if(t_total != 0){
                                        cell = new PdfPCell(new Paragraph("TOTAL:",smallFont));
                                        cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                                        cell.setBorder(0);
                                        cell.setColspan(2);
                                        table_iteraciones.addCell(cell);
                                        
                                     /*   cell = new PdfPCell(new Paragraph("$",smallFont));
                                        cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                                        cell.setBorder(1);
                                        table_iteraciones.addCell(cell);
                                    */   
                                        
                                        cell = new PdfPCell(new Paragraph(StringHelper.AgregaComas(StringHelper.roundDouble(t_lunes,2)),smallFont));
                                        cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                                        cell.setBorder(1);
                                        table_iteraciones.addCell(cell);
                                        
                                        cell = new PdfPCell(new Paragraph(StringHelper.AgregaComas(StringHelper.roundDouble(t_martes,2)),smallFont));
                                        cell.setBorder(1);
                                        cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                                        table_iteraciones.addCell(cell);
                                        
                                        cell = new PdfPCell(new Paragraph(StringHelper.AgregaComas(StringHelper.roundDouble(t_miercoles,2)),smallFont));
                                        cell.setBorder(1);
                                        cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                                        table_iteraciones.addCell(cell);
                                        
                                        cell = new PdfPCell(new Paragraph(StringHelper.AgregaComas(StringHelper.roundDouble(t_jueves,2)),smallFont));
                                        cell.setBorder(1);
                                        cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                                        table_iteraciones.addCell(cell);
                                        
                                        cell = new PdfPCell(new Paragraph(StringHelper.AgregaComas(StringHelper.roundDouble(t_viernes,2)),smallFont));
                                        cell.setBorder(1);
                                        cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                                        table_iteraciones.addCell(cell);
                                        
                                        cell = new PdfPCell(new Paragraph(StringHelper.AgregaComas(StringHelper.roundDouble(t_total,2)),smallFont));
                                        cell.setBorder(1);
                                        cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                                        table_iteraciones.addCell(cell);


                                        cell = new PdfPCell();
                                        cell.setBorder(0);
                                        cell.setColspan(8);
                                        cell.getBorderWidthRight();
                                        cell.setFixedHeight(15);//alto  de la celda en blanco
                                        table_iteraciones.addCell(cell);
                                
                                }
                                
                                
                                t_lunes=0;
                                t_martes=0;
                                t_miercoles=0;
                                t_jueves=0;
                                t_viernes=0;
                                t_total=0;
                                
                                for (String omega : rows){
                                        PdfPCell celda = null;

                                        if (omega.equals("proveedor")){
                                            celda = new PdfPCell(new Paragraph(i.get(omega),smallFont));
                                            celda.setHorizontalAlignment(Element.ALIGN_LEFT);
                                            celda.setVerticalAlignment(Element.ALIGN_MIDDLE);
                                        }

                                        if (omega.equals("factura")){
                                            celda = new PdfPCell(new Paragraph(i.get(omega),smallFont));
                                            celda.setHorizontalAlignment(Element.ALIGN_RIGHT);
                                            celda.setVerticalAlignment(Element.ALIGN_MIDDLE);
                                        }
                                        
                                      /*  if (omega.equals("pesos")){
                                            celda = new PdfPCell(new Paragraph("$",smallFont));
                                            celda.setHorizontalAlignment(Element.ALIGN_RIGHT);
                                            celda.setVerticalAlignment(Element.ALIGN_MIDDLE);
                                        }
                                      */

                                        if (omega.equals("lunes")){
                                            celda = new PdfPCell(new Paragraph(StringHelper.AgregaComas(StringHelper.roundDouble(i.get(omega),2)),smallFont));
                                            celda.setHorizontalAlignment(Element.ALIGN_RIGHT);
                                            celda.setVerticalAlignment(Element.ALIGN_MIDDLE);
                                            t_lunes = t_lunes + Double.valueOf(i.get(omega)).doubleValue();
                                            tg_lunes = tg_lunes + Double.valueOf(i.get(omega)).doubleValue();
                                        }

                                        if (omega.equals("martes")){
                                            celda = new PdfPCell(new Paragraph(StringHelper.AgregaComas(StringHelper.roundDouble(i.get(omega),2)),smallFont));
                                            celda.setHorizontalAlignment(Element.ALIGN_RIGHT);
                                            celda.setVerticalAlignment(Element.ALIGN_MIDDLE);
                                            t_martes = t_martes + Double.valueOf(i.get(omega)).doubleValue();
                                            tg_martes = tg_martes + Double.valueOf(i.get(omega)).doubleValue();
                                        }



                                        if (omega.equals("miercoles")){
                                            celda = new PdfPCell(new Paragraph(StringHelper.AgregaComas(StringHelper.roundDouble(i.get(omega),2)),smallFont));
                                            celda.setHorizontalAlignment(Element.ALIGN_RIGHT);
                                            celda.setVerticalAlignment(Element.ALIGN_MIDDLE);
                                            t_miercoles = t_miercoles + Double.valueOf(i.get(omega)).doubleValue();
                                            tg_miercoles = tg_miercoles + Double.valueOf(i.get(omega)).doubleValue();
                                        }
                                        if (omega.equals("jueves")){
                                            celda = new PdfPCell(new Paragraph(StringHelper.AgregaComas(StringHelper.roundDouble(i.get(omega),2)),smallFont));
                                            celda.setHorizontalAlignment(Element.ALIGN_RIGHT);
                                            celda.setVerticalAlignment(Element.ALIGN_MIDDLE);
                                            t_jueves = t_jueves + Double.valueOf(i.get(omega)).doubleValue();
                                            tg_jueves = tg_jueves + Double.valueOf(i.get(omega)).doubleValue();
                                        }
                                        if (omega.equals("viernes")){
                                            celda = new PdfPCell(new Paragraph(StringHelper.AgregaComas(StringHelper.roundDouble(i.get(omega),2)),smallFont));
                                            celda.setHorizontalAlignment(Element.ALIGN_RIGHT);
                                            celda.setVerticalAlignment(Element.ALIGN_MIDDLE);
                                            t_viernes = t_viernes + Double.valueOf(i.get(omega)).doubleValue();
                                            tg_viernes = tg_viernes + Double.valueOf(i.get(omega)).doubleValue();
                                        }
                                        if (omega.equals("total")){
                                            celda = new PdfPCell(new Paragraph(StringHelper.AgregaComas(StringHelper.roundDouble(i.get(omega),2)),smallFont));
                                                    //(StringHelper.roundDouble(i.get(omega), 2),smallFont));
                                            celda.setHorizontalAlignment(Element.ALIGN_RIGHT);
                                            celda.setVerticalAlignment(Element.ALIGN_MIDDLE);
                                            t_total = t_total + Double.valueOf(i.get(omega)).doubleValue();
                                            tg_total = tg_total + Double.valueOf(i.get(omega)).doubleValue();
                                        }


                                        celda.setBorder(0);
                                        table_iteraciones.addCell(celda);
                            }
                                
                                
                                
                                
                                
                                
                                cliente = i.get("proveedor");
                }
                
            }
                                
                                cell = new PdfPCell(new Paragraph("",smallFont));
                                cell.setBorder(0);
                                cell.setColspan(2);
                                cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                                table_iteraciones.addCell(cell);
                                
                                
                                /*cell = new PdfPCell(new Paragraph("$",smallFont));
                                cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                                cell.setBorder(1);
                                table_iteraciones.addCell(cell);
                                
                                */
                                cell = new PdfPCell(new Paragraph(StringHelper.AgregaComas(StringHelper.roundDouble(t_lunes,2)),smallFont));
                                cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                                cell.setBorder(1);
                                table_iteraciones.addCell(cell);
                                
                                cell = new PdfPCell(new Paragraph(StringHelper.AgregaComas(StringHelper.roundDouble(t_martes,2)),smallFont));
                                cell.setBorder(1);
                                cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                                table_iteraciones.addCell(cell);
                                
                                cell = new PdfPCell(new Paragraph(StringHelper.AgregaComas(StringHelper.roundDouble(t_miercoles,2)),smallFont));
                                cell.setBorder(1);
                                cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                                table_iteraciones.addCell(cell);
                                
                                cell = new PdfPCell(new Paragraph(StringHelper.AgregaComas(StringHelper.roundDouble(t_jueves,2)),smallFont));
                                cell.setBorder(1);
                                cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                                table_iteraciones.addCell(cell);
                                
                                cell = new PdfPCell(new Paragraph(StringHelper.AgregaComas(StringHelper.roundDouble(t_viernes,2)),smallFont));
                                cell.setBorder(1);
                                cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                                table_iteraciones.addCell(cell);
                                
                                cell = new PdfPCell(new Paragraph(StringHelper.AgregaComas(StringHelper.roundDouble(t_total,2)),smallFont));
                                cell.setBorder(1);
                                cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                                table_iteraciones.addCell(cell);
                                
                                
                                cell = new PdfPCell();
                                cell.setBorder(0);
                                cell.setColspan(9);
                                cell.getBorderWidthRight();
                                cell.setFixedHeight(15);//alto  de la celda en blanco
                                table_iteraciones.addCell(cell);
                                
                                
                                
                                
                                
                                
                                
                                cell = new PdfPCell(new Paragraph("TOTAL GENERAL",smallFont));
                                cell.setBorder(0);
                                cell.setColspan(2);
                                cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                                table_iteraciones.addCell(cell);
                                
                                
                                
                                
                                cell = new PdfPCell(new Paragraph(StringHelper.AgregaComas(StringHelper.roundDouble(tg_lunes,2)),smallFont));
                                cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                                cell.setBorder(1);
                                table_iteraciones.addCell(cell);
                                
                                cell = new PdfPCell(new Paragraph(StringHelper.AgregaComas(StringHelper.roundDouble(tg_martes,2)),smallFont));
                                cell.setBorder(1);
                                cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                                table_iteraciones.addCell(cell);
                                
                                cell = new PdfPCell(new Paragraph(StringHelper.AgregaComas(StringHelper.roundDouble(tg_miercoles,2)),smallFont));
                                cell.setBorder(1);
                                cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                                table_iteraciones.addCell(cell);
                                
                                cell = new PdfPCell(new Paragraph(StringHelper.AgregaComas(StringHelper.roundDouble(tg_jueves,2)),smallFont));
                                cell.setBorder(1);
                                cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                                table_iteraciones.addCell(cell);
                                
                                cell = new PdfPCell(new Paragraph(StringHelper.AgregaComas(StringHelper.roundDouble(tg_viernes,2)),smallFont));
                                cell.setBorder(1);
                                cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                                table_iteraciones.addCell(cell);
                                
                                cell = new PdfPCell(new Paragraph(StringHelper.AgregaComas(StringHelper.roundDouble(tg_total,2)),smallFont));
                                cell.setBorder(1);
                                cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                                table_iteraciones.addCell(cell);
           document.add(table_iteraciones); //añadiendo la tabla 
           document.close();
        }catch (Exception e) {
                e.printStackTrace();
        }
    }

    

    
    
    class HeaderFooter extends PdfPageEventHelper  {
        
        //public Image headerImage;
        protected PdfTemplate total;       
        protected BaseFont helv;  
        protected PdfContentByte cb;  
        Font largeBoldFont = new Font(Font.FontFamily.HELVETICA,10,Font.BOLD,BaseColor.BLACK);
        Font largeFont = new Font(Font.FontFamily.HELVETICA,10,Font.NORMAL,BaseColor.BLACK);
        Font smallFont = new Font(Font.FontFamily.HELVETICA,7,Font.NORMAL,BaseColor.BLACK);
        
        /*Añadimos una tabla con  una imagen del logo de megestiono y creamos la fuente para el documento, la imagen esta escalada para que no se muestre pixelada*/   
        @Override
        public void onOpenDocument(PdfWriter writer, Document document) {
            try {
                
                //headerImage = Image.getInstance(PdfDepositos.ruta_imagen);
                //headerImage.scalePercent(50);
                
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
            //PdfContentByte cb = writer.getDirectContent();
            ColumnText.showTextAligned(writer.getDirectContent(),Element.ALIGN_CENTER, new Phrase(PdfReporteProgramacionPagos.this.getEmpresa_emisora(),largeBoldFont),document.getPageSize().getWidth()/2, document.getPageSize().getTop() -25, 0);
            ColumnText.showTextAligned(writer.getDirectContent(),Element.ALIGN_CENTER, new Phrase("Programacion de Pagos",largeBoldFont),document.getPageSize().getWidth()/2, document.getPageSize().getTop()-38, 0);
            //ColumnText.showTextAligned(writer.getDirectContent(),Element.ALIGN_CENTER, new Phrase(ReporteLibroMayor.fecha_reporte,largeFont),document.getPageSize().getWidth()/2, document.getPageSize().getTop()-50, 0);
            
            cb = writer.getDirectContent();  
            //cb.saveState();  
            String text = "Página " + writer.getPageNumber() + " de ";  
            float textBase = document.bottom() - 20;
            float textSize = helv.getWidthPoint(text, 7);  
            float adjust = helv.getWidthPoint("0", 150);  
            cb.beginText();  
            cb.setFontAndSize(helv, 7);  

            //cb.setTextMatrix(document.right() - 120 - adjust, textBase);
            cb.setTextMatrix(document.right() - 128, textBase);  //definir la posicion de text
            cb.showText(text);  

            cb.endText();  
            cb.addTemplate(total, document.right() - adjust , textBase);  //definir la posicion del total de paginas
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
