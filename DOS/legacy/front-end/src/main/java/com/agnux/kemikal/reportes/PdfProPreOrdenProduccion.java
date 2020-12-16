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
public class PdfProPreOrdenProduccion {
    
    public PdfProPreOrdenProduccion(HashMap<String, String> datosEncabezadoPie,String fileout,ArrayList<HashMap<String, Object>> lista,HashMap<String, String> datos) throws FileNotFoundException, DocumentException {
        Font smallFont = new Font(Font.FontFamily.HELVETICA,7,Font.NORMAL,BaseColor.BLACK);
        Font smallBoldFont = new Font(Font.FontFamily.HELVETICA, 8, Font.BOLD, BaseColor.WHITE);
        
        Font fuenteCont = new Font(Font.getFamily("ARIAL"),10,Font.NORMAL,BaseColor.BLACK);
        Font fuentenegrita = new Font(Font.getFamily("ARIAL"),8,Font.BOLD,BaseColor.BLACK);
        Font fuenteCont2 = new Font(Font.getFamily("ARIAL"),8,Font.NORMAL,BaseColor.BLACK);
        Font headerFont = new Font(Font.getFamily("ARIAL"),8,Font.BOLD,BaseColor.WHITE);
        
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
            celdaX = new PdfPCell(new Paragraph("FECHA:"+datos.get("fecha_elavorar") +"   HORA:"+datos.get("fecha_elavorar") +"   No. de Lote : "+datos.get("folio"),fuentenegrita));
            celdaX.setBorderWidthBottom(1);
            celdaX.setBorderWidthTop(0);
            celdaX.setBorderWidthRight(0);
            celdaX.setColspan(3);
            celdaX.setBorderWidthLeft(0);
            tablaX.addCell(celdaX);
            
            reporte.add(tablaX);
            
            for (int j=0;j<lista.size();j++){
                
                HashMap<String,Object> registro = lista.get(j);
                
                float [] tam_tablax1 = {2f,1.6f,3.5f,2f,2f,3};
                PdfPTable tablaX1 = new PdfPTable(tam_tablax1);
                PdfPCell celdaX1;
                tablaX1.setKeepTogether(false);
                
                //columna 1 y 2 fil2
                //columna 1 y 2 fil2
                celdaX1 = new PdfPCell(new Paragraph("Codigo de Producto : ",fuenteCont2));
                celdaX1.setHorizontalAlignment(Element.ALIGN_LEFT);
                celdaX1.setVerticalAlignment(Element.ALIGN_MIDDLE);
                celdaX1.setBorderWidthBottom(0);
                celdaX1.setBorderWidthTop(0);
                celdaX1.setBorderWidthRight(0);
                //celdaX.setColspan(2);
                celdaX1.setBorderWidthLeft(0);
                tablaX1.addCell(celdaX1);
                
                //columna 3 a 5 vacio fil2
                celdaX1 = new PdfPCell(new Paragraph(String.valueOf(registro.get("sku")),fuenteCont2));
                celdaX1.setHorizontalAlignment(Element.ALIGN_LEFT);
                celdaX1.setBorderWidthBottom(0);
                celdaX1.setBorderWidthTop(0);
                celdaX1.setColspan(3);
                celdaX1.setBorderWidthRight(0);
                celdaX1.setBorderWidthLeft(0);
                tablaX1.addCell(celdaX1);
                
                //columna 6 vacio fil2
                celdaX1 = new PdfPCell(new Paragraph(String.valueOf("CANTIDAD EN "+registro.get("unidad").toString().toUpperCase()+": "+registro.get("cantidad")),fuenteCont2));
                celdaX1.setHorizontalAlignment(Element.ALIGN_LEFT);
                celdaX1.setBorderWidthBottom(0);
                celdaX1.setBorderWidthTop(0);
                celdaX1.setColspan(2);
                celdaX1.setBorderWidthRight(0);
                celdaX1.setBorderWidthLeft(0);
                tablaX1.addCell(celdaX1);
                
                //columna 1 fil2
                celdaX1 = new PdfPCell(new Paragraph("Nombre Producto : ",fuenteCont2));
                celdaX1.setHorizontalAlignment(Element.ALIGN_LEFT);
                celdaX1.setVerticalAlignment(Element.ALIGN_MIDDLE);
                celdaX1.setBorderWidthBottom(0);
                celdaX1.setBorderWidthTop(0);
                celdaX1.setBorderWidthRight(0);
                //celdaX.setColspan(2);
                celdaX1.setBorderWidthLeft(0);
                tablaX1.addCell(celdaX1);
                
                //columna 2, 3 y 4 vacio fil2
                celdaX1 = new PdfPCell(new Paragraph(String.valueOf(registro.get("descripcion")),fuenteCont2));
                celdaX1.setHorizontalAlignment(Element.ALIGN_LEFT);
                celdaX1.setBorderWidthBottom(0);
                celdaX1.setBorderWidthTop(0);
                celdaX1.setColspan(3);
                celdaX1.setBorderWidthRight(0);
                celdaX1.setBorderWidthLeft(0);
                tablaX1.addCell(celdaX1);
                
                //columna 5 y 6 vacio fil2
                celdaX1 = new PdfPCell(new Paragraph(String.valueOf("CANTIDAD EN "+registro.get("unidad_tmp").toString().toUpperCase()+": "+registro.get("cantidad_tmp")),fuenteCont2));
                celdaX1.setHorizontalAlignment(Element.ALIGN_LEFT);
                celdaX1.setBorderWidthBottom(0);
                celdaX1.setBorderWidthTop(0);
                celdaX1.setColspan(2);
                celdaX1.setBorderWidthRight(0);
                celdaX1.setBorderWidthLeft(0);
                tablaX1.addCell(celdaX1);
                
                //columna 1 fil2
                celdaX1 = new PdfPCell(new Paragraph("",fuenteCont2));
                celdaX1.setHorizontalAlignment(Element.ALIGN_LEFT);
                celdaX1.setVerticalAlignment(Element.ALIGN_MIDDLE);
                celdaX1.setBorderWidthBottom(0);
                celdaX1.setBorderWidthTop(0);
                celdaX1.setBorderWidthRight(0);
                //celdaX.setColspan(2);
                celdaX1.setBorderWidthLeft(0);
                tablaX1.addCell(celdaX1);
                
                //columna 2, 3 y 4 vacio fil2
                celdaX1 = new PdfPCell(new Paragraph("",fuenteCont2));
                celdaX1.setHorizontalAlignment(Element.ALIGN_LEFT);
                celdaX1.setBorderWidthBottom(0);
                celdaX1.setBorderWidthTop(0);
                celdaX1.setColspan(3);
                celdaX1.setBorderWidthRight(0);
                celdaX1.setBorderWidthLeft(0);
                tablaX1.addCell(celdaX1);
                
                //columna 5 y 6 vacio fil2
                celdaX1 = new PdfPCell(new Paragraph(String.valueOf("DENSIDAD:        "+registro.get("densidad")),fuenteCont2));
                celdaX1.setHorizontalAlignment(Element.ALIGN_LEFT);
                celdaX1.setBorderWidthBottom(0);
                celdaX1.setBorderWidthTop(0);
                celdaX1.setColspan(2);
                celdaX1.setBorderWidthRight(0);
                celdaX1.setBorderWidthLeft(0);
                tablaX1.addCell(celdaX1);
                
                reporte.add(tablaX1);
                
                float [] form_tabla = {2f,3.8f,2f,2f,2f,3f};
                PdfPTable tablaFormX = new PdfPTable(form_tabla);
                PdfPCell celdaF;
                tablaFormX.setKeepTogether(false);
                tablaFormX.setHeaderRows(1);
                
                
                /*Encabezado para los productos que componen la formula*/
                // Encabezado de Celd
                //1
                celdaF = new PdfPCell(new Paragraph("CODIGO",smallBoldFont));
                celdaF.setUseAscender(true);
                celdaF.setHorizontalAlignment(Element.ALIGN_CENTER);
                celdaF.setUseDescender(true);
                celdaF.setVerticalAlignment(Element.ALIGN_MIDDLE);
                celdaF.setBackgroundColor(BaseColor.BLACK);
                tablaFormX.addCell(celdaF);
                
                
                celdaF = new PdfPCell(new Paragraph("DESCRIPCION",smallBoldFont));
                celdaF.setUseAscender(true);
                celdaF.setHorizontalAlignment(Element.ALIGN_CENTER);
                celdaF.setUseDescender(true);
                celdaF.setVerticalAlignment(Element.ALIGN_MIDDLE);
                celdaF.setBackgroundColor(BaseColor.BLACK);
                tablaFormX.addCell(celdaF);
                
                
                celdaF = new PdfPCell(new Paragraph("SIRE",headerFont));
                celdaF.setUseAscender(true);
                celdaF.setHorizontalAlignment(Element.ALIGN_CENTER);
                celdaF.setUseDescender(true);
                celdaF.setVerticalAlignment(Element.ALIGN_MIDDLE);
                celdaF.setBackgroundColor(BaseColor.BLACK);
                tablaFormX.addCell(celdaF);
                
                
                celdaF = new PdfPCell(new Paragraph("CANTIDAD",headerFont));
                celdaF.setUseAscender(true);
                celdaF.setHorizontalAlignment(Element.ALIGN_CENTER);
                celdaF.setUseDescender(true);
                celdaF.setVerticalAlignment(Element.ALIGN_MIDDLE);
                celdaF.setBackgroundColor(BaseColor.BLACK);
                tablaFormX.addCell(celdaF);
                
                
                celdaF = new PdfPCell(new Paragraph("AGREGADOS",headerFont));
                celdaF.setUseAscender(true);
                celdaF.setHorizontalAlignment(Element.ALIGN_CENTER);
                celdaF.setUseDescender(true);
                celdaF.setVerticalAlignment(Element.ALIGN_MIDDLE);
                celdaF.setBackgroundColor(BaseColor.BLACK);
                tablaFormX.addCell(celdaF);
                
                celdaF = new PdfPCell(new Paragraph("LOTE",smallBoldFont));
                celdaF.setUseAscender(true);
                celdaF.setHorizontalAlignment(Element.ALIGN_CENTER);
                celdaF.setUseDescender(true);
                celdaF.setVerticalAlignment(Element.ALIGN_MIDDLE);
                celdaF.setBackgroundColor(BaseColor.BLACK);            
                tablaFormX.addCell(celdaF);
                
                /*Termina Encabezado para los productos que componen la formula*/
                
                int contador = 1;
                Double sumatoria = 0.0;
                
                ArrayList<HashMap<String, String>> prod_formula = (ArrayList<HashMap<String, String>>) registro.get("formula");
                for (int k=0;k<prod_formula.size();k++){
                    
                    HashMap<String,String> reg_element = prod_formula.get(k);
                    
                    Double cantidad = Double.parseDouble(reg_element.get("cantidad").toString() );
                    
                    //1           
                    celdaF = new PdfPCell(new Paragraph(String.valueOf(reg_element.get("sku")),smallFont));
                    celdaF.setUseAscender(true);
                    celdaF.setHorizontalAlignment(Element.ALIGN_LEFT);
                    celdaF.setUseDescender(true);
                    celdaF.setVerticalAlignment(Element.ALIGN_MIDDLE);
                    tablaFormX.addCell(celdaF);
                    
                    //1           
                    celdaF = new PdfPCell(new Paragraph(String.valueOf(reg_element.get("descripcion")),smallFont));
                    celdaF.setUseAscender(true);
                    celdaF.setHorizontalAlignment(Element.ALIGN_LEFT);
                    celdaF.setUseDescender(true);
                    celdaF.setVerticalAlignment(Element.ALIGN_MIDDLE);
                    tablaFormX.addCell(celdaF);
                    
                    celdaF = new PdfPCell(new Paragraph("",smallFont));
                    celdaF.setUseAscender(true);
                    celdaF.setHorizontalAlignment(Element.ALIGN_LEFT);
                    celdaF.setUseDescender(true);
                    celdaF.setVerticalAlignment(Element.ALIGN_MIDDLE);
                    tablaFormX.addCell(celdaF);
                    
                    //2
                    celdaF = new PdfPCell(new Paragraph(""+StringHelper.roundDouble(String.valueOf(cantidad), 4),smallFont));
                    celdaF.setUseAscender(true);
                    celdaF.setHorizontalAlignment(Element.ALIGN_LEFT);
                    celdaF.setUseDescender(true);
                    celdaF.setVerticalAlignment(Element.ALIGN_MIDDLE);
                    tablaFormX.addCell(celdaF);
                    
                    //2
                    celdaF = new PdfPCell(new Paragraph("",fuenteCont));
                    celdaF.setUseAscender(true);
                    celdaF.setHorizontalAlignment(Element.ALIGN_LEFT);
                    celdaF.setUseDescender(true);
                    celdaF.setVerticalAlignment(Element.ALIGN_MIDDLE);
                    tablaFormX.addCell(celdaF);
                    
                    //13
                    //celda = new PdfPCell(new Paragraph(registro.get("observaciones"),fuenteCont));
                    celdaF = new PdfPCell(new Paragraph(String.valueOf(reg_element.get("lote")),smallFont));
                    celdaF.setUseAscender(true);
                    celdaF.setHorizontalAlignment(Element.ALIGN_LEFT);
                    celdaF.setUseDescender(true);
                    celdaF.setVerticalAlignment(Element.ALIGN_MIDDLE);     
                    tablaFormX.addCell(celdaF);
                    
                    sumatoria += cantidad;
                    contador++;
                }
                
                
                 //columna 1 fil1
                celdaF = new PdfPCell(new Paragraph("TOTAL ",fuentenegrita));
                celdaF.setHorizontalAlignment(Element.ALIGN_RIGHT);
                celdaF.setVerticalAlignment(Element.ALIGN_MIDDLE);
                celdaF.setBorderWidthBottom(0);
                celdaF.setBorderWidthTop(0);
                celdaF.setColspan(3);
                celdaF.setBorderWidthRight(0);
                celdaF.setBorderWidthLeft(0);
                tablaFormX.addCell(celdaF);
                
                celdaF = new PdfPCell(new Paragraph(String.valueOf(""+StringHelper.roundDouble(sumatoria, 2)),fuenteCont));
                celdaF.setUseAscender(true);
                celdaF.setHorizontalAlignment(Element.ALIGN_RIGHT);
                celdaF.setUseDescender(true);
                celdaF.setVerticalAlignment(Element.ALIGN_MIDDLE);
                tablaFormX.addCell(celdaF);
                
                celdaF = new PdfPCell(new Paragraph("",fuentenegrita));
                celdaF.setHorizontalAlignment(Element.ALIGN_LEFT);
                celdaF.setVerticalAlignment(Element.ALIGN_MIDDLE);
                celdaF.setBorderWidthBottom(0);
                celdaF.setBorderWidthTop(0);
                celdaF.setColspan(2);
                celdaF.setBorderWidthRight(0);
                celdaF.setBorderWidthLeft(0);
                tablaFormX.addCell(celdaF);
                
                reporte.add(tablaFormX);
                
                
                
                //Lista de Subprocesos
                ArrayList<HashMap<String, Object>> subprocesos = (ArrayList<HashMap<String, Object>>) registro.get("subprocesos");
                for (int k=0;k<subprocesos.size();k++){
                    
                    HashMap<String,Object> subproceso = subprocesos.get(k);
                    
                    float [] tam_tablasubp = {1.7f,2.3f,2f,2.2f,2.8f,3};
                    PdfPTable tablaSubp = new PdfPTable(tam_tablasubp);
                    PdfPCell celdaSubp;
                    tablaX1.setKeepTogether(false);
                    
                    //columna 1 a 6 fil2
                    celdaSubp = new PdfPCell(new Paragraph(" ",fuentenegrita));
                    celdaSubp.setHorizontalAlignment(Element.ALIGN_LEFT);
                    celdaSubp.setVerticalAlignment(Element.ALIGN_MIDDLE);
                    celdaSubp.setBorderWidthBottom(0);
                    celdaSubp.setBorderWidthTop(0);
                    celdaSubp.setBorderWidthRight(0);
                    celdaSubp.setColspan(6);
                    celdaSubp.setBorderWidthLeft(0);
                    tablaSubp.addCell(celdaSubp);
                    
                    
                    //columna 1 y 2 fil2
                    celdaSubp = new PdfPCell(new Paragraph("SUBPROCESO : ",fuentenegrita));
                    celdaSubp.setHorizontalAlignment(Element.ALIGN_LEFT);
                    celdaSubp.setVerticalAlignment(Element.ALIGN_MIDDLE);
                    celdaSubp.setBorderWidthBottom(0);
                    celdaSubp.setBorderWidthTop(0);
                    celdaSubp.setBorderWidthRight(0);
                    celdaSubp.setBorderWidthLeft(0);
                    tablaSubp.addCell(celdaSubp);
                    
                    //columna 3 a 6 vacio fil2
                    celdaSubp = new PdfPCell(new Paragraph(String.valueOf(subproceso.get("subproceso")),fuentenegrita));
                    celdaSubp.setHorizontalAlignment(Element.ALIGN_LEFT);
                    celdaSubp.setBorderWidthBottom(0);
                    celdaSubp.setBorderWidthTop(0);
                    celdaSubp.setColspan(5);
                    celdaSubp.setBorderWidthRight(0);
                    celdaSubp.setBorderWidthLeft(0);
                    tablaSubp.addCell(celdaSubp);
                    
                    //para mostrar el operario, equipo y equipo adicional
                    //columna 1 y 2 fil2
                    celdaSubp = new PdfPCell(new Paragraph("OPERADOR : ",fuentenegrita));
                    celdaSubp.setHorizontalAlignment(Element.ALIGN_LEFT);
                    celdaSubp.setVerticalAlignment(Element.ALIGN_MIDDLE);
                    celdaSubp.setBorderWidthBottom(0);
                    celdaSubp.setBorderWidthTop(0);
                    celdaSubp.setBorderWidthRight(0);
                    celdaSubp.setBorderWidthLeft(0);
                    tablaSubp.addCell(celdaSubp);
                    
                    celdaSubp = new PdfPCell(new Paragraph(String.valueOf(subproceso.get("empleado")),smallFont));
                    celdaSubp.setHorizontalAlignment(Element.ALIGN_LEFT);
                    celdaSubp.setVerticalAlignment(Element.ALIGN_MIDDLE);
                    celdaSubp.setBorderWidthBottom(0);
                    celdaSubp.setBorderWidthTop(0);
                    celdaSubp.setColspan(5);
                    celdaSubp.setBorderWidthRight(0);
                    celdaSubp.setBorderWidthLeft(0);
                    tablaSubp.addCell(celdaSubp);
                    
                    //columna 1
                    celdaSubp = new PdfPCell(new Paragraph("EQUIPO : ",fuentenegrita));
                    celdaSubp.setHorizontalAlignment(Element.ALIGN_LEFT);
                    celdaSubp.setVerticalAlignment(Element.ALIGN_MIDDLE);
                    celdaSubp.setBorderWidthBottom(0);
                    celdaSubp.setBorderWidthTop(0);
                    celdaSubp.setBorderWidthRight(0);
                    celdaSubp.setBorderWidthLeft(0);
                    tablaSubp.addCell(celdaSubp);
                    
                    //columna 2 y 3 
                    celdaSubp = new PdfPCell(new Paragraph(String.valueOf(subproceso.get("equipo")),smallFont));
                    celdaSubp.setHorizontalAlignment(Element.ALIGN_LEFT);
                    celdaSubp.setVerticalAlignment(Element.ALIGN_MIDDLE);
                    celdaSubp.setBorderWidthBottom(0);
                    celdaSubp.setBorderWidthTop(0);
                    celdaSubp.setColspan(2);
                    celdaSubp.setBorderWidthRight(0);
                    celdaSubp.setBorderWidthLeft(0);
                    tablaSubp.addCell(celdaSubp);
                    
                    
                    //columna 4
                    celdaSubp = new PdfPCell(new Paragraph("EQUIPO ADICIONAL:",fuentenegrita));
                    celdaSubp.setHorizontalAlignment(Element.ALIGN_LEFT);
                    celdaSubp.setVerticalAlignment(Element.ALIGN_MIDDLE);
                    celdaSubp.setBorderWidthBottom(0);
                    celdaSubp.setBorderWidthTop(0);
                    celdaSubp.setBorderWidthRight(0);
                    celdaSubp.setBorderWidthLeft(0);
                    tablaSubp.addCell(celdaSubp);
                    
                    //columna 5 y 6
                    celdaSubp = new PdfPCell(new Paragraph(String.valueOf(subproceso.get("equipo_adic")),smallFont));
                    celdaSubp.setHorizontalAlignment(Element.ALIGN_LEFT);
                    celdaSubp.setVerticalAlignment(Element.ALIGN_MIDDLE);
                    celdaSubp.setBorderWidthBottom(0);
                    celdaSubp.setBorderWidthTop(0);
                    celdaSubp.setColspan(2);
                    celdaSubp.setBorderWidthRight(0);
                    celdaSubp.setBorderWidthLeft(0);
                    tablaSubp.addCell(celdaSubp);
                    
                    
                    reporte.add(tablaSubp);
                    
                    
                    ArrayList<HashMap<String, String>> especificaciones_estandar = (ArrayList<HashMap<String, String>>)subproceso.get("especificaciones_estandar");
                    /*Codigo para las especificaciones_estandar*/
                     String verifica_especificaicones = "0";
                     for (int l=0;l<especificaciones_estandar.size();l++){
                         HashMap<String,String> registro1 = especificaciones_estandar.get(l);
                         verifica_especificaicones = registro1.get("id");
                     }
                     
                     if(!verifica_especificaicones.equals("0")){
                         
                        float [] tam_tablaxx = {2f,2.5f,2f,7f};
                        PdfPTable tablaXX = new PdfPTable(tam_tablaxx);
                        PdfPCell celdaXX;
                        tablaXX.setKeepTogether(false);
                        
                         String id_esp = "";
                         
                         ArrayList<HashMap<String, String>> listaesp = especificaciones_estandar;
                         for (int l=0;l<especificaciones_estandar.size();l++){
                             HashMap<String,String> registrotmp = listaesp.get(l);
                             
                             if(!id_esp.equals(registrotmp.get("id"))){
                                 id_esp = registrotmp.get("id");
                                 //colspan 5 fil1
                                celdaXX = new PdfPCell(new Paragraph("",fuentenegrita));
                                celdaXX.setHorizontalAlignment(Element.ALIGN_LEFT);
                                celdaXX.setVerticalAlignment(Element.ALIGN_MIDDLE);
                                celdaXX.setBorderWidthBottom(0);
                                celdaXX.setColspan(4);
                                celdaXX.setBorderWidthTop(0);
                                celdaXX.setBorderWidthRight(0);
                                celdaXX.setBorderWidthLeft(0);
                                tablaXX.addCell(celdaXX);
                                
                                
                                //colspan 5 fil1
                                celdaXX = new PdfPCell(new Paragraph("ESPECIFICACIONES ESTANDAR",fuentenegrita));
                                celdaXX.setHorizontalAlignment(Element.ALIGN_LEFT);
                                celdaXX.setVerticalAlignment(Element.ALIGN_MIDDLE);
                                celdaXX.setBorderWidthBottom(0);
                                celdaXX.setColspan(4);
                                celdaXX.setBorderWidthTop(0);
                                celdaXX.setBorderWidthRight(0);
                                celdaXX.setBorderWidthLeft(0);
                                tablaXX.addCell(celdaXX);
                                
                                
                             }
                             
                            //columna 1 fil1
                            celdaXX = new PdfPCell(new Paragraph("FINEZA: ",smallFont));
                            celdaXX.setHorizontalAlignment(Element.ALIGN_LEFT);
                            celdaXX.setVerticalAlignment(Element.ALIGN_MIDDLE);
                            celdaXX.setBorderWidthBottom(0);
                            celdaXX.setBorderWidthTop(0);
                            celdaXX.setBorderWidthRight(0);
                            celdaXX.setBorderWidthLeft(0);
                            tablaXX.addCell(celdaXX);

                            //columna 1 fil1
                            celdaXX = new PdfPCell(new Paragraph("DE "+convierte_cadena(registrotmp.get("fineza_inicial"))+" A "+convierte_cadena(registrotmp.get("fineza_final"))+" Micras",smallFont));
                            celdaXX.setHorizontalAlignment(Element.ALIGN_LEFT);
                            celdaXX.setVerticalAlignment(Element.ALIGN_MIDDLE);
                            celdaXX.setBorderWidthBottom(0);
                            celdaXX.setBorderWidthTop(0);
                            celdaXX.setBorderWidthRight(0);
                            celdaXX.setBorderWidthLeft(0);
                            tablaXX.addCell(celdaXX);

                            //columna 4 vacio fil1
                            celdaXX = new PdfPCell(new Paragraph("VISCOSIDAD: ",smallFont));
                            celdaXX.setBorderWidthBottom(0);
                            celdaXX.setBorderWidthTop(0);
                            celdaXX.setBorderWidthRight(0);
                            celdaXX.setBorderWidthLeft(0);
                            tablaXX.addCell(celdaXX);

                            //columna 5 fil1
                            celdaXX = new PdfPCell(new Paragraph("DE "+convierte_cadena(registrotmp.get("viscosidads_inicial"))+" A "+convierte_cadena(registrotmp.get("viscosidads_final"))+" Segundos",smallFont));
                            celdaXX.setBorderWidthBottom(0);
                            celdaXX.setBorderWidthTop(0);
                            celdaXX.setBorderWidthRight(0);
                            celdaXX.setBorderWidthLeft(0);
                            tablaXX.addCell(celdaXX);



                            /*fila 2*/
                            //columna 1 fil1

                            celdaXX = new PdfPCell(new Paragraph("VISCOSIDAD: ",smallFont));
                            celdaXX.setBorderWidthBottom(0);
                            celdaXX.setBorderWidthTop(0);
                            celdaXX.setBorderWidthRight(0);
                            celdaXX.setBorderWidthLeft(0);
                            tablaXX.addCell(celdaXX);

                            //columna 2 fil1
                            celdaXX = new PdfPCell(new Paragraph("DE "+convierte_cadena(registrotmp.get("viscosidadku_inicial"))+" A "+convierte_cadena(registrotmp.get("viscosidadku_final"))+" KU",smallFont));
                            celdaXX.setBorderWidthBottom(0);
                            celdaXX.setBorderWidthTop(0);
                            celdaXX.setBorderWidthRight(0);
                            celdaXX.setBorderWidthLeft(0);
                            tablaXX.addCell(celdaXX);


                            //columna 4 vacio fil1
                            celdaXX = new PdfPCell(new Paragraph("VISCOSIDAD: ",smallFont));
                            celdaXX.setBorderWidthBottom(0);
                            celdaXX.setBorderWidthTop(0);
                            celdaXX.setBorderWidthRight(0);
                            celdaXX.setBorderWidthLeft(0);
                            tablaXX.addCell(celdaXX);

                            //columna 5 fil1
                            celdaXX = new PdfPCell(new Paragraph("DE "+convierte_cadena(registrotmp.get("viscosidadcps_inicial"))+" A "+convierte_cadena(registrotmp.get("viscosidadcps_final"))+" CPS",smallFont));
                            celdaXX.setBorderWidthBottom(0);
                            celdaXX.setBorderWidthTop(0);
                            celdaXX.setBorderWidthRight(0);
                            celdaXX.setBorderWidthLeft(0);
                            tablaXX.addCell(celdaXX);



                            /*fila 3*/
                            //columna 1 fil1

                            celdaXX = new PdfPCell(new Paragraph("DENSIDAD: ",smallFont));
                            celdaXX.setBorderWidthBottom(0);
                            celdaXX.setBorderWidthTop(0);
                            celdaXX.setBorderWidthRight(0);
                            celdaXX.setBorderWidthLeft(0);
                            tablaXX.addCell(celdaXX);


                            //columna 2 fil1
                            celdaXX = new PdfPCell(new Paragraph("DE "+convierte_cadena(registrotmp.get("densidad_inicial"))+" A "+convierte_cadena(registrotmp.get("densidad_final"))+" Kg\\/L",smallFont));
                            celdaXX.setBorderWidthBottom(0);
                            celdaXX.setBorderWidthTop(0);
                            celdaXX.setBorderWidthRight(0);
                            celdaXX.setBorderWidthLeft(0);
                            tablaXX.addCell(celdaXX);

                            //columna 4 vacio fil1
                            celdaXX = new PdfPCell(new Paragraph("% No VOLATILES: ",smallFont));
                            celdaXX.setBorderWidthBottom(0);
                            celdaXX.setBorderWidthTop(0);
                            celdaXX.setBorderWidthRight(0);
                            celdaXX.setBorderWidthLeft(0);
                            tablaXX.addCell(celdaXX);

                            celdaXX = new PdfPCell(new Paragraph("DE "+convierte_cadena(registrotmp.get("volatiles_inicial"))+" A "+convierte_cadena(registrotmp.get("volatiles_final"))+" %",smallFont));
                            celdaXX.setBorderWidthBottom(0);
                            celdaXX.setBorderWidthTop(0);
                            celdaXX.setBorderWidthRight(0);
                            celdaXX.setBorderWidthLeft(0);
                            tablaXX.addCell(celdaXX);


                            /*fila 4*/

                            //columna 1 fil1
                            celdaXX = new PdfPCell(new Paragraph("pH: ",smallFont));
                            celdaXX.setBorderWidthBottom(0);
                            celdaXX.setBorderWidthTop(0);
                            celdaXX.setBorderWidthRight(0);
                            celdaXX.setBorderWidthLeft(0);
                            tablaXX.addCell(celdaXX);

                            //columna 2 fil1
                            celdaXX = new PdfPCell(new Paragraph("DE "+convierte_cadena(registrotmp.get("hidrogeno_inicial"))+" A "+convierte_cadena(registrotmp.get("hidrogeno_final"))+" ",smallFont));
                            celdaXX.setBorderWidthBottom(0);
                            celdaXX.setBorderWidthTop(0);
                            celdaXX.setBorderWidthRight(0);
                            celdaXX.setBorderWidthLeft(0);
                            tablaXX.addCell(celdaXX);

                            //columna 4 vacio fil1
                            celdaXX = new PdfPCell(new Paragraph("CUBRIENTE: ",smallFont));
                            celdaXX.setBorderWidthBottom(0);
                            celdaXX.setBorderWidthTop(0);
                            celdaXX.setBorderWidthRight(0);
                            celdaXX.setBorderWidthLeft(0);
                            tablaXX.addCell(celdaXX);

                            celdaXX = new PdfPCell(new Paragraph("DE "+convierte_cadena(registrotmp.get("cubriente_inicial"))+" A "+convierte_cadena(registrotmp.get("cubriente_final"))+" %",smallFont));
                            celdaXX.setBorderWidthBottom(0);
                            celdaXX.setBorderWidthTop(0);
                            celdaXX.setBorderWidthRight(0);
                            celdaXX.setBorderWidthLeft(0);
                            tablaXX.addCell(celdaXX);

                            /*fila 5*/

                            //columna 1 fil1
                            celdaXX = new PdfPCell(new Paragraph("TONO: ",smallFont));
                            celdaXX.setBorderWidthBottom(0);
                            celdaXX.setBorderWidthTop(0);
                            celdaXX.setBorderWidthRight(0);
                            celdaXX.setBorderWidthLeft(0);
                            tablaXX.addCell(celdaXX);

                            //columna 2 fil1
                            celdaXX = new PdfPCell(new Paragraph("DE "+convierte_cadena(registrotmp.get("tono_inicial"))+" A "+convierte_cadena(registrotmp.get("tono_final"))+" ",smallFont));
                            celdaXX.setBorderWidthBottom(0);
                            celdaXX.setBorderWidthTop(0);
                            celdaXX.setBorderWidthRight(0);
                            celdaXX.setBorderWidthLeft(0);
                            tablaXX.addCell(celdaXX);

                            //columna 3 fil1
                            celdaXX = new PdfPCell(new Paragraph("BRILLO: ",smallFont));
                            celdaXX.setBorderWidthBottom(0);
                            celdaXX.setBorderWidthTop(0);
                            celdaXX.setBorderWidthRight(0);
                            celdaXX.setBorderWidthLeft(0);
                            tablaXX.addCell(celdaXX);

                            //columna 5 fil1
                            celdaXX = new PdfPCell(new Paragraph("DE "+convierte_cadena(registrotmp.get("brillo_inicial"))+" A "+convierte_cadena(registrotmp.get("brillo_final"))+" Unid. de brillo",smallFont));
                            celdaXX.setBorderWidthBottom(0);
                            celdaXX.setBorderWidthTop(0);
                            celdaXX.setBorderWidthRight(0);
                            celdaXX.setBorderWidthLeft(0);
                            tablaXX.addCell(celdaXX);
                            
                            /*fila 6*/

                            //columna 1 fil1
                            celdaXX = new PdfPCell(new Paragraph("DUREZA: ",smallFont));
                            celdaXX.setBorderWidthBottom(0);
                            celdaXX.setBorderWidthTop(0);
                            celdaXX.setBorderWidthRight(0);
                            celdaXX.setBorderWidthLeft(0);
                            tablaXX.addCell(celdaXX);

                            celdaXX = new PdfPCell(new Paragraph("DE "+registrotmp.get("dureza_inicial").toString()+" A "+registrotmp.get("dureza_final").toString()+" Letras",smallFont));
                            celdaXX.setBorderWidthBottom(0);
                            celdaXX.setBorderWidthTop(0);
                            celdaXX.setBorderWidthRight(0);
                            celdaXX.setBorderWidthLeft(0);
                            tablaXX.addCell(celdaXX);

                            //columna 3 fil1
                            celdaXX = new PdfPCell(new Paragraph("ADHERENCIA: ",smallFont));
                            celdaXX.setBorderWidthBottom(0);
                            celdaXX.setBorderWidthTop(0);
                            celdaXX.setBorderWidthRight(0);
                            celdaXX.setBorderWidthLeft(0);
                            tablaXX.addCell(celdaXX);
                            
                            celdaXX = new PdfPCell(new Paragraph("DE "+convierte_cadena(registrotmp.get("adherencia_inicial"))+" A "+convierte_cadena(registrotmp.get("adherencia_final"))+" %",smallFont));
                            celdaXX.setBorderWidthBottom(0);
                            celdaXX.setBorderWidthTop(0);
                            celdaXX.setBorderWidthRight(0);
                            celdaXX.setBorderWidthLeft(0);
                            tablaXX.addCell(celdaXX);
                            
                         }
                         
                         reporte.add(tablaXX);
                     }
                    
                    
                    ArrayList<HashMap<String, String>> especificaciones_produccion = (ArrayList<HashMap<String, String>>)subproceso.get("especificaciones_produccion");
                    verifica_especificaicones = "0";
                    for (int l=0;l<especificaciones_produccion.size();l++){
                         HashMap<String,String> registro1 = especificaciones_produccion.get(l);
                         verifica_especificaicones = registro1.get("id");
                     }
                     
                     if(!verifica_especificaicones.equals("0")){
                         
                        float [] tam_tablaxx = {2f,2.5f,2f,7f};
                        PdfPTable tablaXX = new PdfPTable(tam_tablaxx);
                        PdfPCell celdaXX;
                        tablaXX.setKeepTogether(false);
                        
                         String id_esp = "";
                         
                         ArrayList<HashMap<String, String>> listaesp = especificaciones_produccion;
                         for (int l=0;l<listaesp.size();l++){
                             HashMap<String,String> registrotmp = listaesp.get(l);
                             
                             if(!id_esp.equals(registrotmp.get("id"))){
                                 id_esp = registrotmp.get("id");
                                 //colspan 5 fil1
                                celdaXX = new PdfPCell(new Paragraph("",smallFont));
                                celdaXX.setHorizontalAlignment(Element.ALIGN_LEFT);
                                celdaXX.setVerticalAlignment(Element.ALIGN_MIDDLE);
                                celdaXX.setBorderWidthBottom(0);
                                celdaXX.setColspan(4);
                                celdaXX.setBorderWidthTop(0);
                                celdaXX.setBorderWidthRight(0);
                                celdaXX.setBorderWidthLeft(0);
                                tablaXX.addCell(celdaXX);
                                
                                
                                //colspan 5 fil1
                                celdaXX = new PdfPCell(new Paragraph("RESULTADO DE ANALISIS",fuentenegrita));
                                celdaXX.setHorizontalAlignment(Element.ALIGN_LEFT);
                                celdaXX.setVerticalAlignment(Element.ALIGN_MIDDLE);
                                celdaXX.setBorderWidthBottom(0);
                                celdaXX.setColspan(4);
                                celdaXX.setBorderWidthTop(0);
                                celdaXX.setBorderWidthRight(0);
                                celdaXX.setBorderWidthLeft(0);
                                tablaXX.addCell(celdaXX);
                                
                                
                             }
                             
                            //columna 1 fil1
                            celdaXX = new PdfPCell(new Paragraph("FINEZA: ",smallFont));
                            celdaXX.setHorizontalAlignment(Element.ALIGN_LEFT);
                            celdaXX.setVerticalAlignment(Element.ALIGN_MIDDLE);
                            celdaXX.setBorderWidthBottom(0);
                            celdaXX.setBorderWidthTop(0);
                            celdaXX.setBorderWidthRight(0);
                            celdaXX.setBorderWidthLeft(0);
                            tablaXX.addCell(celdaXX);

                            //columna 1 fil1
                            celdaXX = new PdfPCell(new Paragraph("DE "+convierte_cadena(registrotmp.get("fineza_inicial"))+" A "+convierte_cadena(registrotmp.get("fineza_final"))+" Micras",smallFont));
                            celdaXX.setHorizontalAlignment(Element.ALIGN_LEFT);
                            celdaXX.setVerticalAlignment(Element.ALIGN_MIDDLE);
                            celdaXX.setBorderWidthBottom(0);
                            celdaXX.setBorderWidthTop(0);
                            celdaXX.setBorderWidthRight(0);
                            celdaXX.setBorderWidthLeft(0);
                            tablaXX.addCell(celdaXX);

                            //columna 4 vacio fil1
                            celdaXX = new PdfPCell(new Paragraph("VISCOSIDAD: ",smallFont));
                            celdaXX.setBorderWidthBottom(0);
                            celdaXX.setBorderWidthTop(0);
                            celdaXX.setBorderWidthRight(0);
                            celdaXX.setBorderWidthLeft(0);
                            tablaXX.addCell(celdaXX);

                            //columna 5 fil1
                            celdaXX = new PdfPCell(new Paragraph("DE "+convierte_cadena(registrotmp.get("viscosidads_inicial"))+" A "+convierte_cadena(registrotmp.get("viscosidads_final"))+" Segundos",smallFont));
                            celdaXX.setBorderWidthBottom(0);
                            celdaXX.setBorderWidthTop(0);
                            celdaXX.setBorderWidthRight(0);
                            celdaXX.setBorderWidthLeft(0);
                            tablaXX.addCell(celdaXX);



                            /*fila 2*/
                            //columna 1 fil1

                            celdaXX = new PdfPCell(new Paragraph("VISCOSIDAD: ",smallFont));
                            celdaXX.setBorderWidthBottom(0);
                            celdaXX.setBorderWidthTop(0);
                            celdaXX.setBorderWidthRight(0);
                            celdaXX.setBorderWidthLeft(0);
                            tablaXX.addCell(celdaXX);

                            //columna 2 fil1
                            celdaXX = new PdfPCell(new Paragraph("DE "+convierte_cadena(registrotmp.get("viscosidadku_inicial"))+" A "+convierte_cadena(registrotmp.get("viscosidadku_final"))+" KU",smallFont));
                            celdaXX.setBorderWidthBottom(0);
                            celdaXX.setBorderWidthTop(0);
                            celdaXX.setBorderWidthRight(0);
                            celdaXX.setBorderWidthLeft(0);
                            tablaXX.addCell(celdaXX);


                            //columna 4 vacio fil1
                            celdaXX = new PdfPCell(new Paragraph("VISCOSIDAD: ",smallFont));
                            celdaXX.setBorderWidthBottom(0);
                            celdaXX.setBorderWidthTop(0);
                            celdaXX.setBorderWidthRight(0);
                            celdaXX.setBorderWidthLeft(0);
                            tablaXX.addCell(celdaXX);

                            //columna 5 fil1
                            celdaXX = new PdfPCell(new Paragraph("DE "+convierte_cadena(registrotmp.get("viscosidadcps_inicial"))+" A "+convierte_cadena(registrotmp.get("viscosidadcps_final"))+" CPS",smallFont));
                            celdaXX.setBorderWidthBottom(0);
                            celdaXX.setBorderWidthTop(0);
                            celdaXX.setBorderWidthRight(0);
                            celdaXX.setBorderWidthLeft(0);
                            tablaXX.addCell(celdaXX);



                            /*fila 3*/
                            //columna 1 fil1

                            celdaXX = new PdfPCell(new Paragraph("DENSIDAD: ",smallFont));
                            celdaXX.setBorderWidthBottom(0);
                            celdaXX.setBorderWidthTop(0);
                            celdaXX.setBorderWidthRight(0);
                            celdaXX.setBorderWidthLeft(0);
                            tablaXX.addCell(celdaXX);


                            //columna 2 fil1
                            celdaXX = new PdfPCell(new Paragraph("DE "+convierte_cadena(registrotmp.get("densidad_inicial"))+" A "+convierte_cadena(registrotmp.get("densidad_final"))+" Kg\\/L",smallFont));
                            celdaXX.setBorderWidthBottom(0);
                            celdaXX.setBorderWidthTop(0);
                            celdaXX.setBorderWidthRight(0);
                            celdaXX.setBorderWidthLeft(0);
                            tablaXX.addCell(celdaXX);

                            //columna 4 vacio fil1
                            celdaXX = new PdfPCell(new Paragraph("% No VOLATILES: ",smallFont));
                            celdaXX.setBorderWidthBottom(0);
                            celdaXX.setBorderWidthTop(0);
                            celdaXX.setBorderWidthRight(0);
                            celdaXX.setBorderWidthLeft(0);
                            tablaXX.addCell(celdaXX);

                            celdaXX = new PdfPCell(new Paragraph("DE "+convierte_cadena(registrotmp.get("volatiles_inicial"))+" A "+convierte_cadena(registrotmp.get("volatiles_final"))+" %",smallFont));
                            celdaXX.setBorderWidthBottom(0);
                            celdaXX.setBorderWidthTop(0);
                            celdaXX.setBorderWidthRight(0);
                            celdaXX.setBorderWidthLeft(0);
                            tablaXX.addCell(celdaXX);


                            /*fila 4*/

                            //columna 1 fil1
                            celdaXX = new PdfPCell(new Paragraph("pH: ",smallFont));
                            celdaXX.setBorderWidthBottom(0);
                            celdaXX.setBorderWidthTop(0);
                            celdaXX.setBorderWidthRight(0);
                            celdaXX.setBorderWidthLeft(0);
                            tablaXX.addCell(celdaXX);

                            //columna 2 fil1
                            celdaXX = new PdfPCell(new Paragraph("DE "+convierte_cadena(registrotmp.get("hidrogeno_inicial"))+" A "+convierte_cadena(registrotmp.get("hidrogeno_final"))+" ",smallFont));
                            celdaXX.setBorderWidthBottom(0);
                            celdaXX.setBorderWidthTop(0);
                            celdaXX.setBorderWidthRight(0);
                            celdaXX.setBorderWidthLeft(0);
                            tablaXX.addCell(celdaXX);

                            //columna 4 vacio fil1
                            celdaXX = new PdfPCell(new Paragraph("CUBRIENTE: ",smallFont));
                            celdaXX.setBorderWidthBottom(0);
                            celdaXX.setBorderWidthTop(0);
                            celdaXX.setBorderWidthRight(0);
                            celdaXX.setBorderWidthLeft(0);
                            tablaXX.addCell(celdaXX);

                            celdaXX = new PdfPCell(new Paragraph("DE "+convierte_cadena(registrotmp.get("cubriente_inicial"))+" A "+convierte_cadena(registrotmp.get("cubriente_final"))+" %",smallFont));
                            celdaXX.setBorderWidthBottom(0);
                            celdaXX.setBorderWidthTop(0);
                            celdaXX.setBorderWidthRight(0);
                            celdaXX.setBorderWidthLeft(0);
                            tablaXX.addCell(celdaXX);

                            /*fila 5*/

                            //columna 1 fil1
                            celdaXX = new PdfPCell(new Paragraph("TONO: ",smallFont));
                            celdaXX.setBorderWidthBottom(0);
                            celdaXX.setBorderWidthTop(0);
                            celdaXX.setBorderWidthRight(0);
                            celdaXX.setBorderWidthLeft(0);
                            tablaXX.addCell(celdaXX);
                            
                            //columna 2 fil1
                            celdaXX = new PdfPCell(new Paragraph("DE "+convierte_cadena(registrotmp.get("tono_inicial"))+" A "+convierte_cadena(registrotmp.get("tono_final"))+" ",smallFont));
                            celdaXX.setBorderWidthBottom(0);
                            celdaXX.setBorderWidthTop(0);
                            celdaXX.setBorderWidthRight(0);
                            celdaXX.setBorderWidthLeft(0);
                            tablaXX.addCell(celdaXX);
                            
                            //columna 3 fil1
                            celdaXX = new PdfPCell(new Paragraph("BRILLO: ",smallFont));
                            celdaXX.setBorderWidthBottom(0);
                            celdaXX.setBorderWidthTop(0);
                            celdaXX.setBorderWidthRight(0);
                            celdaXX.setBorderWidthLeft(0);
                            tablaXX.addCell(celdaXX);

                            //columna 5 fil1
                            celdaXX = new PdfPCell(new Paragraph("DE "+convierte_cadena(registrotmp.get("brillo_inicial"))+" A "+convierte_cadena(registrotmp.get("brillo_final"))+" Unid. de brillo",smallFont));
                            celdaXX.setBorderWidthBottom(0);
                            celdaXX.setBorderWidthTop(0);
                            celdaXX.setBorderWidthRight(0);
                            celdaXX.setBorderWidthLeft(0);
                            tablaXX.addCell(celdaXX);

                            /*fila 6*/

                            //columna 1 fil1
                            celdaXX = new PdfPCell(new Paragraph("DUREZA: ",smallFont));
                            celdaXX.setBorderWidthBottom(0);
                            celdaXX.setBorderWidthTop(0);
                            celdaXX.setBorderWidthRight(0);
                            celdaXX.setBorderWidthLeft(0);
                            tablaXX.addCell(celdaXX);

                            celdaXX = new PdfPCell(new Paragraph("DE "+registrotmp.get("dureza_inicial").toString()+" A "+registrotmp.get("dureza_final").toString()+" Letras",smallFont));
                            celdaXX.setBorderWidthBottom(0);
                            celdaXX.setBorderWidthTop(0);
                            celdaXX.setBorderWidthRight(0);
                            celdaXX.setBorderWidthLeft(0);
                            tablaXX.addCell(celdaXX);
                            
                            //columna 3 fil1
                            celdaXX = new PdfPCell(new Paragraph("ADHERENCIA: ",smallFont));
                            celdaXX.setBorderWidthBottom(0);
                            celdaXX.setBorderWidthTop(0);
                            celdaXX.setBorderWidthRight(0);
                            celdaXX.setBorderWidthLeft(0);
                            tablaXX.addCell(celdaXX);
                            
                            celdaXX = new PdfPCell(new Paragraph("DE "+convierte_cadena(registrotmp.get("adherencia_inicial"))+" A "+convierte_cadena(registrotmp.get("adherencia_final"))+" %",smallFont));
                            celdaXX.setBorderWidthBottom(0);
                            celdaXX.setBorderWidthTop(0);
                            celdaXX.setBorderWidthRight(0);
                            celdaXX.setBorderWidthLeft(0);
                            tablaXX.addCell(celdaXX);
                            
                         }
                         
                         reporte.add(tablaXX);
                     }
                    
                }
                
                
                /*
                subprocesos
                for (int k=0;k<registro.size();k++){
                    
                }
                */
                
            }
            
            
            float [] tam_tablay = {3.5f,2f,3f,2f,3.5f};
            PdfPTable tablaAut = new PdfPTable(tam_tablay);
            PdfPCell celdaAut;
            tablaAut.setKeepTogether(false);
            
            //columna 1 a 6 fil2
            celdaAut = new PdfPCell(new Paragraph(" ",fuentenegrita));
            celdaAut.setHorizontalAlignment(Element.ALIGN_LEFT);
            celdaAut.setVerticalAlignment(Element.ALIGN_MIDDLE);
            celdaAut.setBorderWidthBottom(0);
            celdaAut.setBorderWidthTop(0);
            celdaAut.setBorderWidthRight(0);
            celdaAut.setColspan(5);
            celdaAut.setBorderWidthLeft(0);
            tablaAut.addCell(celdaAut);
            
            //columna 1 a 6 fil2
            celdaAut = new PdfPCell(new Paragraph(" ",fuentenegrita));
            celdaAut.setHorizontalAlignment(Element.ALIGN_LEFT);
            celdaAut.setVerticalAlignment(Element.ALIGN_MIDDLE);
            celdaAut.setBorderWidthBottom(0);
            celdaAut.setBorderWidthTop(0);
            celdaAut.setBorderWidthRight(0);
            celdaAut.setColspan(5);
            celdaAut.setBorderWidthLeft(0);
            tablaAut.addCell(celdaAut);
            
            //columna 1 y 2 fil2
            celdaAut = new PdfPCell(new Paragraph(" ",fuentenegrita));
            celdaAut.setHorizontalAlignment(Element.ALIGN_LEFT);
            celdaAut.setVerticalAlignment(Element.ALIGN_MIDDLE);
            celdaAut.setBorderWidthBottom(0);
            celdaAut.setBorderWidthTop(0);
            celdaAut.setBorderWidthRight(0);
            celdaAut.setColspan(2);
            celdaAut.setBorderWidthLeft(0);
            tablaAut.addCell(celdaAut);
            
            //columna 3 fil2
            celdaAut = new PdfPCell(new Paragraph("___________________",fuentenegrita));
            celdaAut.setHorizontalAlignment(Element.ALIGN_LEFT);
            celdaAut.setVerticalAlignment(Element.ALIGN_MIDDLE);
            celdaAut.setBorderWidthBottom(0);
            celdaAut.setBorderWidthTop(0);
            celdaAut.setBorderWidthRight(0);
            celdaAut.setBorderWidthLeft(0);
            tablaAut.addCell(celdaAut);
            
            //columna 4 y 5 fil2
            celdaAut = new PdfPCell(new Paragraph(" ",fuentenegrita));
            celdaAut.setHorizontalAlignment(Element.ALIGN_LEFT);
            celdaAut.setVerticalAlignment(Element.ALIGN_MIDDLE);
            celdaAut.setBorderWidthBottom(0);
            celdaAut.setBorderWidthTop(0);
            celdaAut.setBorderWidthRight(0);
            celdaAut.setColspan(2);
            celdaAut.setBorderWidthLeft(0);
            tablaAut.addCell(celdaAut);
            
            //columna 1 y 2 fil2
            celdaAut = new PdfPCell(new Paragraph(" ",fuentenegrita));
            celdaAut.setHorizontalAlignment(Element.ALIGN_LEFT);
            celdaAut.setVerticalAlignment(Element.ALIGN_MIDDLE);
            celdaAut.setBorderWidthBottom(0);
            celdaAut.setBorderWidthTop(0);
            celdaAut.setBorderWidthRight(0);
            celdaAut.setColspan(2);
            celdaAut.setBorderWidthLeft(0);
            tablaAut.addCell(celdaAut);
            
            //columna 3 fil2
            celdaAut = new PdfPCell(new Paragraph(" VERIFICA Y RECIBE ",fuentenegrita));
            celdaAut.setHorizontalAlignment(Element.ALIGN_LEFT);
            celdaAut.setVerticalAlignment(Element.ALIGN_MIDDLE);
            celdaAut.setBorderWidthBottom(0);
            celdaAut.setBorderWidthTop(0);
            celdaAut.setBorderWidthRight(0);
            celdaAut.setBorderWidthLeft(0);
            tablaAut.addCell(celdaAut);
            
            //columna 4 y 5 fil2
            celdaAut = new PdfPCell(new Paragraph(" ",fuentenegrita));
            celdaAut.setHorizontalAlignment(Element.ALIGN_LEFT);
            celdaAut.setVerticalAlignment(Element.ALIGN_MIDDLE);
            celdaAut.setBorderWidthBottom(0);
            celdaAut.setBorderWidthTop(0);
            celdaAut.setBorderWidthRight(0);
            celdaAut.setColspan(2);
            celdaAut.setBorderWidthLeft(0);
            tablaAut.addCell(celdaAut);
            
            reporte.add(tablaAut);
            
        }
        
        catch (Exception e){
             System.out.println(e.toString());
             }
        reporte.close();
    }
    
    String calculaTotalComponenteFormula(String cantidad,String total){
        String retorno = "";
        double cantidad_elemento = Double.parseDouble(cantidad);
        double total_prod = Double.parseDouble(total);
        
        double total_calculo = 0;
        
        total_calculo = (cantidad_elemento/100) * (total_prod);
        
        retorno = String.valueOf(total_calculo);
        
        return retorno;
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
        
        
        /*A√±adimos una tabla con  una imagen del logo de megestiono y creamos la fuente para el documento, la imagen esta escalada para que no se muestre pixelada*/   
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
        
        /*a√±adimos pie de p√°gina, borde y m√°s propiedades*/
        @Override
        public void onEndPage(PdfWriter writer, Document document) {
            
            ColumnText.showTextAligned(writer.getDirectContent(),Element.ALIGN_CENTER, new Phrase(this.getEmpresa(),largeBoldFont),document.getPageSize().getWidth()/2, document.getPageSize().getTop() -25, 0);
            ColumnText.showTextAligned(writer.getDirectContent(),Element.ALIGN_CENTER, new Phrase(this.getTitulo_reporte(),largeBoldFont),document.getPageSize().getWidth()/2, document.getPageSize().getTop()-38, 0);
            
            SimpleDateFormat formato = new SimpleDateFormat("'Generado el' d 'de' MMMMM 'del' yyyy 'a las' HH:mm:ss 'hrs.'");
            String impreso_en = formato.format(new Date());
            
            ColumnText.showTextAligned(writer.getDirectContent(),Element.ALIGN_CENTER, new Phrase(impreso_en,largeFont),document.getPageSize().getWidth()/2, document.getPageSize().getTop()-50, 0);
            
            cb = writer.getDirectContent();
            float textBase = document.bottom() - 15;
            
            //texto inferior izquieda pie de pagina
            String text_left = this.getCodigo1();
            float text_left_Size = helv.getWidthPoint(text_left, 7);
            cb.beginText();
            cb.setFontAndSize(helv, 7);
            cb.setTextMatrix(document.left()+85, textBase );//definir la posicion de text
            cb.showText(text_left);
            cb.endText();
            
            
            //texto centro pie de pagina
            String text_center ="P√°gina " + writer.getPageNumber() + " de ";
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
