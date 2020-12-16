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
public class PdfProOrdenProduccion {
    
    public PdfProOrdenProduccion(HashMap<String, String> datosEncabezadoPie,String fileout,ArrayList<HashMap<String, Object>> lista,HashMap<String, String> datos) throws FileNotFoundException, DocumentException {
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
             
            float [] tam_tablax = {2.5f,0.6f,5f,2f,2f,3};
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
            celdaX = new PdfPCell(new Paragraph("FECHA: "+datos.get("fecha_elavorar") +"     HORA: "+datos.get("fecha_elavorar") +"     Folio: "+datos.get("folio")+"\nCosto: $"+datos.get("costo_ultimo"),fuentenegrita));
            celdaX.setHorizontalAlignment(Element.ALIGN_LEFT);
            celdaX.setBorderWidthBottom(1);
            celdaX.setBorderWidthTop(0);
            celdaX.setBorderWidthRight(0);
            celdaX.setColspan(3);
            celdaX.setBorderWidthLeft(0);
            tablaX.addCell(celdaX);
            
            reporte.add(tablaX);
            
            
            String mostrar_autorizacion = "true";
            
            for (int j=0;j<lista.size();j++){
                
                HashMap<String,Object> registro = lista.get(j);
                
                float [] tam_tablax1 = {2f,2.5f,3.2f,2f,1.5f,3};
                PdfPTable tablaX1 = new PdfPTable(tam_tablax1);
                PdfPCell celdaX1;
                tablaX1.setKeepTogether(false);
                
                //columna 1 y 2 fil2
                //columna 1 y 2 fil2
                celdaX1 = new PdfPCell(new Paragraph("Código de Producto:",fuenteCont2));
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
                //celdaX1.setColspan(3);
                celdaX1.setBorderWidthRight(0);
                celdaX1.setBorderWidthLeft(0);
                tablaX1.addCell(celdaX1);
                
                String version="";
                boolean mostrar_observacion = false;
                //System.out.println("tipo_prod_id: "+registro.get("tipo_prod_id"));
                
                if(Integer.parseInt(String.valueOf(registro.get("tipo_prod_id")))==8){
                    //Solo para producto en desarrollo
                    version = "Versión: "+String.valueOf(registro.get("version"));
                    mostrar_observacion=true;
                    mostrar_autorizacion="false";
                    
                    //System.out.println("mostrar_autorizacion: "+mostrar_autorizacion);
                    
                }
                
                //columna 3 a 5 vacio fil2
                celdaX1 = new PdfPCell(new Paragraph(version,fuenteCont2));
                celdaX1.setHorizontalAlignment(Element.ALIGN_LEFT);
                celdaX1.setBorderWidthBottom(0);
                celdaX1.setBorderWidthTop(0);
                celdaX1.setBorderWidthRight(0);
                celdaX1.setBorderWidthLeft(0);
                tablaX1.addCell(celdaX1);
                
                //columna 3 a 5 vacio fil2
                celdaX1 = new PdfPCell(new Paragraph("",fuenteCont2));
                celdaX1.setHorizontalAlignment(Element.ALIGN_LEFT);
                celdaX1.setBorderWidthBottom(0);
                celdaX1.setBorderWidthTop(0);
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
                
                //Para el lote de el producto
                //columna 1 fil2
                celdaX1 = new PdfPCell(new Paragraph("Lote Producto : ",fuenteCont2));
                celdaX1.setHorizontalAlignment(Element.ALIGN_LEFT);
                celdaX1.setVerticalAlignment(Element.ALIGN_MIDDLE);
                celdaX1.setBorderWidthBottom(0);
                celdaX1.setBorderWidthTop(0);
                celdaX1.setBorderWidthRight(0);
                //celdaX.setColspan(2);
                celdaX1.setBorderWidthLeft(0);
                tablaX1.addCell(celdaX1);
                
                if(String.valueOf(registro.get("lote")).equals("")){
                    //columna 1 fil2
                    //columna 2, 3 y 4 vacio fil2
                    celdaX1 = new PdfPCell(new Paragraph(String.valueOf(datos.get("lote")),fuenteCont2));
                    celdaX1.setHorizontalAlignment(Element.ALIGN_LEFT);
                    celdaX1.setBorderWidthBottom(0);
                    celdaX1.setBorderWidthTop(0);
                    celdaX1.setColspan(3);
                    celdaX1.setBorderWidthRight(0);
                    celdaX1.setBorderWidthLeft(0);
                    tablaX1.addCell(celdaX1);

                }else{
                    
                    //columna 2, 3 y 4 vacio fil2
                    celdaX1 = new PdfPCell(new Paragraph(String.valueOf(registro.get("lote")),fuenteCont2));
                    celdaX1.setHorizontalAlignment(Element.ALIGN_LEFT);
                    celdaX1.setBorderWidthBottom(0);
                    celdaX1.setBorderWidthTop(0);
                    celdaX1.setColspan(3);
                    celdaX1.setBorderWidthRight(0);
                    celdaX1.setBorderWidthLeft(0);
                    tablaX1.addCell(celdaX1);
                }
                
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
                    
                    
                    Double cantidad = Double.parseDouble(reg_element.get("cantidad_usada").toString() );
                    Double cantidad_adicional = Double.parseDouble(reg_element.get("cantidad_adicional").toString());
                    Double cantidad_usada = Double.parseDouble(reg_element.get("cantidad_usada").toString() );
                    
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
                    if(cantidad > 0){
                        celdaF = new PdfPCell(new Paragraph(""+StringHelper.roundDouble(String.valueOf(cantidad_usada), 4),smallFont));
                        celdaF.setUseAscender(true);
                        celdaF.setHorizontalAlignment(Element.ALIGN_LEFT);
                        celdaF.setUseDescender(true);
                        celdaF.setVerticalAlignment(Element.ALIGN_MIDDLE);
                        tablaFormX.addCell(celdaF);
                    }else{
                        celdaF = new PdfPCell(new Paragraph("",smallFont));
                        celdaF.setUseAscender(true);
                        celdaF.setHorizontalAlignment(Element.ALIGN_LEFT);
                        celdaF.setUseDescender(true);
                        celdaF.setVerticalAlignment(Element.ALIGN_MIDDLE);
                        tablaFormX.addCell(celdaF);
                    }
                    
                    
                    if(cantidad_adicional > 0){
                        //2cantidad_adicional
                        celdaF = new PdfPCell(new Paragraph(""+StringHelper.roundDouble(String.valueOf(cantidad_usada), 4),smallFont));
                        celdaF.setUseAscender(true);
                        celdaF.setHorizontalAlignment(Element.ALIGN_LEFT);
                        celdaF.setUseDescender(true);
                        celdaF.setVerticalAlignment(Element.ALIGN_MIDDLE);
                        tablaFormX.addCell(celdaF);
                    }else{
                        //2cantidad_adicional
                        celdaF = new PdfPCell(new Paragraph("",smallFont));
                        celdaF.setUseAscender(true);
                        celdaF.setHorizontalAlignment(Element.ALIGN_LEFT);
                        celdaF.setUseDescender(true);
                        celdaF.setVerticalAlignment(Element.ALIGN_MIDDLE);
                        tablaFormX.addCell(celdaF);
                    }
                    
                    //13
                    //celda = new PdfPCell(new Paragraph(registro.get("observaciones"),fuenteCont));
                    celdaF = new PdfPCell(new Paragraph(String.valueOf(reg_element.get("lote")),smallFont));
                    celdaF.setUseAscender(true);
                    celdaF.setHorizontalAlignment(Element.ALIGN_LEFT);
                    celdaF.setUseDescender(true);
                    celdaF.setVerticalAlignment(Element.ALIGN_MIDDLE);     
                    tablaFormX.addCell(celdaF);
                    
                    //agrega a la sum atoria la cantidad usada
                    sumatoria += cantidad_usada;
                    
                    //agrega a la sum atoria la cantidad
                    //sumatoria += cantidad_usada;
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
                
                
                if(mostrar_observacion){
                    if(!registro.get("observaciones").equals("")){
                        //Vacio
                        celdaF = new PdfPCell(new Paragraph("",fuentenegrita));
                        celdaF.setHorizontalAlignment(Element.ALIGN_LEFT);
                        celdaF.setVerticalAlignment(Element.ALIGN_MIDDLE);
                        celdaF.setBorder(0);
                        celdaF.setColspan(6);
                        tablaFormX.addCell(celdaF);

                        celdaF = new PdfPCell(new Paragraph("OBSERVACIONES",fuentenegrita));
                        celdaF.setHorizontalAlignment(Element.ALIGN_LEFT);
                        celdaF.setVerticalAlignment(Element.ALIGN_MIDDLE);
                        celdaF.setBorderWidthBottom(0);
                        celdaF.setBorderWidthTop(0);
                        celdaF.setColspan(6);
                        celdaF.setBorderWidthRight(0);
                        celdaF.setBorderWidthLeft(0);
                        tablaFormX.addCell(celdaF);

                        celdaF = new PdfPCell(new Paragraph(String.valueOf(registro.get("observaciones")),smallFont));
                        celdaF.setHorizontalAlignment(Element.ALIGN_LEFT);
                        celdaF.setVerticalAlignment(Element.ALIGN_MIDDLE);
                        celdaF.setBorderWidthBottom(0);
                        celdaF.setBorderWidthTop(0);
                        celdaF.setColspan(6);
                        celdaF.setBorderWidthRight(0);
                        celdaF.setBorderWidthLeft(0);
                        tablaFormX.addCell(celdaF);

                        //Vacio
                        celdaF = new PdfPCell(new Paragraph("",fuentenegrita));
                        celdaF.setHorizontalAlignment(Element.ALIGN_LEFT);
                        celdaF.setVerticalAlignment(Element.ALIGN_MIDDLE);
                        celdaF.setBorder(0);
                        celdaF.setColspan(6);
                        tablaFormX.addCell(celdaF);
                    }
                }
                
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
                    ArrayList<HashMap<String, String>> especificaciones_produccion = (ArrayList<HashMap<String, String>>)subproceso.get("especificaciones_produccion");
                    
                    /*Codigo para las especificaciones_estandar*/
                     String verifica_especificaicones = "0";
                     for (int l=0;l<especificaciones_estandar.size();l++){
                         HashMap<String,String> registro1 = especificaciones_estandar.get(l);
                         verifica_especificaicones = String.valueOf(registro1.get("id"));
                     }
                     
                     if(!verifica_especificaicones.equals("0")){
                        float [] tam_tablaxx = {1.5f,2f,2f,4f,4f};
                        PdfPTable tablaXX = new PdfPTable(tam_tablaxx);
                        PdfPCell celdaXX;
                        tablaXX.setKeepTogether(false);
                        
                         String id_esp = "";
                         
                         //ArrayList<HashMap<String, String>> especificaciones_produccion = (ArrayList<HashMap<String, String>>)subproceso.get("especificaciones_produccion");
                         
                         ArrayList<HashMap<String, String>> listaesp = especificaciones_estandar;
                         for (int l=0;l<especificaciones_estandar.size();l++){
                             HashMap<String,String> registrotmp = listaesp.get(l);
                             
                             String texto_especificacion = "";
                             
                             if(!id_esp.equals(registrotmp.get("id"))){
                                 id_esp = registrotmp.get("id");
                                 
                                 //colspan 5 fil1
                                celdaXX = new PdfPCell(new Paragraph("",fuentenegrita));
                                celdaXX.setHorizontalAlignment(Element.ALIGN_LEFT);
                                celdaXX.setVerticalAlignment(Element.ALIGN_MIDDLE);
                                celdaXX.setBorderWidthBottom(0);
                                celdaXX.setColspan(5);
                                celdaXX.setBorderWidthTop(0);
                                celdaXX.setBorderWidthRight(0);
                                celdaXX.setBorderWidthLeft(0);
                                tablaXX.addCell(celdaXX);
                                
                                
                                //colspan 4 fil1
                                celdaXX = new PdfPCell(new Paragraph("ESPECIFICACIONES ESTANDAR",fuentenegrita));
                                celdaXX.setHorizontalAlignment(Element.ALIGN_LEFT);
                                celdaXX.setVerticalAlignment(Element.ALIGN_MIDDLE);
                                celdaXX.setBorderWidthBottom(0);
                                celdaXX.setColspan(2);
                                celdaXX.setBorderWidthTop(0);
                                celdaXX.setBorderWidthRight(0);
                                celdaXX.setBorderWidthLeft(0);
                                tablaXX.addCell(celdaXX);
                                
                                celdaXX = new PdfPCell(new Paragraph("INSTRUMENTOS",fuentenegrita));
                                celdaXX.setHorizontalAlignment(Element.ALIGN_LEFT);
                                celdaXX.setVerticalAlignment(Element.ALIGN_MIDDLE);
                                celdaXX.setBorderWidthBottom(0);
                                //celdaXX.setColspan(2);
                                celdaXX.setBorderWidthTop(0);
                                celdaXX.setBorderWidthRight(0);
                                celdaXX.setBorderWidthLeft(0);
                                tablaXX.addCell(celdaXX);
                                
                                celdaXX = new PdfPCell(new Paragraph("RESULTADOS DE ANALISIS",fuentenegrita));
                                celdaXX.setHorizontalAlignment(Element.ALIGN_LEFT);
                                celdaXX.setVerticalAlignment(Element.ALIGN_MIDDLE);
                                celdaXX.setBorderWidthBottom(0);
                                celdaXX.setColspan(2);
                                celdaXX.setBorderWidthTop(0);
                                celdaXX.setBorderWidthRight(0);
                                celdaXX.setBorderWidthLeft(0);
                                tablaXX.addCell(celdaXX);
                                
                                texto_especificacion = "header";
                             }//comentado por mi 17 dec 201
                                
                             
                             String res_tmp ="";
                             TablaPDF tabla_tmp = new TablaPDF();
                             
                             res_tmp = cadena_especificaciones(texto_especificacion, registrotmp.get("fineza_inicial"), registrotmp.get("fineza_final"), "Micras");
                             if(!res_tmp.equals("N.A.") && !res_tmp.trim().equals("")){
                                //columna 1 fil1 para fineza
                                celdaXX = new PdfPCell(new Paragraph("FINEZA: ",smallFont));
                                celdaXX.setBorderWidthBottom(0);
                                celdaXX.setBorderWidthTop(0);
                                celdaXX.setBorderWidthRight(0);
                                celdaXX.setBorderWidthLeft(0);
                                tablaXX.addCell(celdaXX);
                                
                                //fineza
                                celdaXX = new PdfPCell(new Paragraph(res_tmp,smallFont));
                                celdaXX.setBorderWidthBottom(0);
                                celdaXX.setBorderWidthTop(0);
                                celdaXX.setBorderWidthRight(0);
                                celdaXX.setBorderWidthLeft(0);
                                tablaXX.addCell(celdaXX);
                                
                                
                                //fineza
                                //System.out.println("inst_fineza:  "+registrotmp.get("inst_fineza"));
                                celdaXX = new PdfPCell(new Paragraph(StringHelper.isNullString(registrotmp.get("inst_fineza")),smallFont));
                                celdaXX.setBorderWidthBottom(0);
                                celdaXX.setBorderWidthTop(0);
                                celdaXX.setBorderWidthRight(0);
                                celdaXX.setBorderWidthLeft(0);
                                tablaXX.addCell(celdaXX);
                                
                                //fineza
                                celdaXX = new PdfPCell(tabla_tmp.addResultadosAnalisis(especificaciones_produccion, "fineza1"));
                                celdaXX.setBorderWidthBottom(0);
                                celdaXX.setBorderWidthTop(0);
                                celdaXX.setBorderWidthRight(0);
                                celdaXX.setBorderWidthLeft(0);
                                tablaXX.addCell(celdaXX);
                                
                                //fineza
                                celdaXX = new PdfPCell(new Paragraph("",smallFont));
                                celdaXX.setBorderWidthBottom(0);
                                celdaXX.setBorderWidthTop(0);
                                celdaXX.setBorderWidthRight(0);
                                celdaXX.setBorderWidthLeft(0);
                                tablaXX.addCell(celdaXX);
                             }
                             

                                
                            res_tmp = cadena_especificaciones(texto_especificacion, registrotmp.get("viscosidads_inicial"), registrotmp.get("viscosidads_final"), "Segundos");
                            if(!res_tmp.equals("N.A.") && !res_tmp.trim().equals("")){
                                //columna 1 fil1 para VISCOSIDAD
                                celdaXX = new PdfPCell(new Paragraph("VISCOSIDAD: ",smallFont));
                                celdaXX.setBorderWidthBottom(0);
                                celdaXX.setBorderWidthTop(0);
                                celdaXX.setBorderWidthRight(0);
                                celdaXX.setBorderWidthLeft(0);
                                tablaXX.addCell(celdaXX);
                                
                                //VISCOSIDAD
                                celdaXX = new PdfPCell(new Paragraph(res_tmp,smallFont));
                                celdaXX.setBorderWidthBottom(0);
                                celdaXX.setBorderWidthTop(0);
                                celdaXX.setBorderWidthRight(0);
                                celdaXX.setBorderWidthLeft(0);
                                tablaXX.addCell(celdaXX);
                                
                                //VISCOSIDAD
                                //System.out.println("inst_viscosidad1:  "+registrotmp.get("inst_viscosidad1"));
                                celdaXX = new PdfPCell(new Paragraph(StringHelper.isNullString(registrotmp.get("inst_viscosidad1")),smallFont));
                                celdaXX.setBorderWidthBottom(0);
                                celdaXX.setBorderWidthTop(0);
                                celdaXX.setBorderWidthRight(0);
                                celdaXX.setBorderWidthLeft(0);
                                tablaXX.addCell(celdaXX);
                                
                                //VISCOSIDAD
                                celdaXX = new PdfPCell(tabla_tmp.addResultadosAnalisis(especificaciones_produccion, "viscosidad1"));
                                celdaXX.setBorderWidthBottom(0);
                                celdaXX.setBorderWidthTop(0);
                                celdaXX.setBorderWidthRight(0);
                                celdaXX.setBorderWidthLeft(0);
                                tablaXX.addCell(celdaXX);
                                
                                //columna 1 fil1
                                celdaXX = new PdfPCell(new Paragraph("",smallFont));
                                celdaXX.setBorderWidthBottom(0);
                                celdaXX.setBorderWidthTop(0);
                                celdaXX.setBorderWidthRight(0);
                                celdaXX.setBorderWidthLeft(0);
                                tablaXX.addCell(celdaXX);
                            }
                                
                            
                                
                            res_tmp = cadena_especificaciones(texto_especificacion, registrotmp.get("viscosidadku_inicial"), registrotmp.get("viscosidadku_final"), "KU");
                            if(!res_tmp.equals("N.A.") && !res_tmp.trim().equals("")){
                                //columna 1 fil1 para VISCOSIDAD KU
                                celdaXX = new PdfPCell(new Paragraph("VISCOSIDAD: ",smallFont));
                                celdaXX.setBorderWidthBottom(0);
                                celdaXX.setBorderWidthTop(0);
                                celdaXX.setBorderWidthRight(0);
                                celdaXX.setBorderWidthLeft(0);
                                tablaXX.addCell(celdaXX);
                                
                                //VISCOSIDAD KU
                                celdaXX = new PdfPCell(new Paragraph(res_tmp,smallFont));
                                celdaXX.setBorderWidthBottom(0);
                                celdaXX.setBorderWidthTop(0);
                                celdaXX.setBorderWidthRight(0);
                                celdaXX.setBorderWidthLeft(0);
                                tablaXX.addCell(celdaXX);
                                
                                //VISCOSIDAD KU
                                //System.out.println("inst_viscosidad2:  "+registrotmp.get("inst_viscosidad2"));
                                celdaXX = new PdfPCell(new Paragraph(StringHelper.isNullString(registrotmp.get("inst_viscosidad2")),smallFont));
                                celdaXX.setBorderWidthBottom(0);
                                celdaXX.setBorderWidthTop(0);
                                celdaXX.setBorderWidthRight(0);
                                celdaXX.setBorderWidthLeft(0);
                                tablaXX.addCell(celdaXX);
                                
                                //VISCOSIDAD KU
                                celdaXX = new PdfPCell(tabla_tmp.addResultadosAnalisis(especificaciones_produccion, "viscosidad2"));
                                celdaXX.setBorderWidthBottom(0);
                                celdaXX.setBorderWidthTop(0);
                                celdaXX.setBorderWidthRight(0);
                                celdaXX.setBorderWidthLeft(0);
                                tablaXX.addCell(celdaXX);
                                
                                //VISCOSIDAD KU
                                celdaXX = new PdfPCell(new Paragraph("",smallFont));
                                celdaXX.setBorderWidthBottom(0);
                                celdaXX.setBorderWidthTop(0);
                                celdaXX.setBorderWidthRight(0);
                                celdaXX.setBorderWidthLeft(0);
                                tablaXX.addCell(celdaXX);
                            }    

                                
                                
                                
                            res_tmp = cadena_especificaciones(texto_especificacion, registrotmp.get("viscosidadcps_inicial"), registrotmp.get("viscosidadcps_final"), "CPS");
                            if(!res_tmp.equals("N.A.") && !res_tmp.trim().equals("")){
                                //columna 1 fil1 para VISCOSIDAD CPS
                                celdaXX = new PdfPCell(new Paragraph("VISCOSIDAD: ",smallFont));
                                celdaXX.setBorderWidthBottom(0);
                                celdaXX.setBorderWidthTop(0);
                                celdaXX.setBorderWidthRight(0);
                                celdaXX.setBorderWidthLeft(0);
                                tablaXX.addCell(celdaXX);
                                
                                //VISCOSIDAD CPS
                                celdaXX = new PdfPCell(new Paragraph(res_tmp,smallFont));
                                celdaXX.setBorderWidthBottom(0);
                                celdaXX.setBorderWidthTop(0);
                                celdaXX.setBorderWidthRight(0);
                                celdaXX.setBorderWidthLeft(0);
                                tablaXX.addCell(celdaXX);
                                
                                //VISCOSIDAD CPS
                                //System.out.println("inst_viscosidad3:  "+registrotmp.get("inst_viscosidad3"));
                                celdaXX = new PdfPCell(new Paragraph(StringHelper.isNullString(registrotmp.get("inst_viscosidad3")),smallFont));
                                celdaXX.setBorderWidthBottom(0);
                                celdaXX.setBorderWidthTop(0);
                                celdaXX.setBorderWidthRight(0);
                                celdaXX.setBorderWidthLeft(0);
                                tablaXX.addCell(celdaXX);
                                
                                //VISCOSIDAD CPS
                                celdaXX = new PdfPCell(tabla_tmp.addResultadosAnalisis(especificaciones_produccion, "viscosidad3"));
                                celdaXX.setBorderWidthBottom(0);
                                celdaXX.setBorderWidthTop(0);
                                celdaXX.setBorderWidthRight(0);
                                celdaXX.setBorderWidthLeft(0);
                                tablaXX.addCell(celdaXX);
                                
                                //VISCOSIDAD CPS
                                celdaXX = new PdfPCell(new Paragraph("",smallFont));
                                celdaXX.setBorderWidthBottom(0);
                                celdaXX.setBorderWidthTop(0);
                                celdaXX.setBorderWidthRight(0);
                                celdaXX.setBorderWidthLeft(0);
                                tablaXX.addCell(celdaXX);
                            } 

                                
                                
                                
                                
                            res_tmp = cadena_especificaciones(texto_especificacion, registrotmp.get("densidad_inicial"), registrotmp.get("densidad_final"), "Kg\\/L");
                            if(!res_tmp.equals("N.A.") && !res_tmp.trim().equals("")){
                                //columna 1 fil1 para DENSIDAD
                                celdaXX = new PdfPCell(new Paragraph("DENSIDAD: ",smallFont));
                                celdaXX.setBorderWidthBottom(0);
                                celdaXX.setBorderWidthTop(0);
                                celdaXX.setBorderWidthRight(0);
                                celdaXX.setBorderWidthLeft(0);
                                tablaXX.addCell(celdaXX);
                                
                                //DENSIDAD
                                celdaXX = new PdfPCell(new Paragraph(res_tmp,smallFont));
                                celdaXX.setBorderWidthBottom(0);
                                celdaXX.setBorderWidthTop(0);
                                celdaXX.setBorderWidthRight(0);
                                celdaXX.setBorderWidthLeft(0);
                                tablaXX.addCell(celdaXX);
                                
                                //DENSIDAD
                                //System.out.println("inst_densidad:  "+registrotmp.get("inst_densidad"));
                                celdaXX = new PdfPCell(new Paragraph(StringHelper.isNullString(registrotmp.get("inst_densidad")) ,smallFont));
                                celdaXX.setBorderWidthBottom(0);
                                celdaXX.setBorderWidthTop(0);
                                celdaXX.setBorderWidthRight(0);
                                celdaXX.setBorderWidthLeft(0);
                                tablaXX.addCell(celdaXX);
                                
                                //DENSIDAD
                                celdaXX = new PdfPCell(tabla_tmp.addResultadosAnalisis(especificaciones_produccion, "densidad"));
                                celdaXX.setBorderWidthBottom(0);
                                celdaXX.setBorderWidthTop(0);
                                celdaXX.setBorderWidthRight(0);
                                celdaXX.setBorderWidthLeft(0);
                                tablaXX.addCell(celdaXX);
                                
                                //DENSIDAD
                                celdaXX = new PdfPCell(new Paragraph("",smallFont));
                                celdaXX.setBorderWidthBottom(0);
                                celdaXX.setBorderWidthTop(0);
                                celdaXX.setBorderWidthRight(0);
                                celdaXX.setBorderWidthLeft(0);
                                tablaXX.addCell(celdaXX);
                            } 

                                
                                
                                
                            res_tmp = cadena_especificaciones(texto_especificacion, registrotmp.get("volatiles_inicial"), registrotmp.get("volatiles_final"), "%");
                            if(!res_tmp.equals("N.A.") && !res_tmp.trim().equals("")){
                                //columna 1 fil1 para % No VOLATILES
                                celdaXX = new PdfPCell(new Paragraph("% No VOLATILES: ",smallFont));
                                celdaXX.setBorderWidthBottom(0);
                                celdaXX.setBorderWidthTop(0);
                                celdaXX.setBorderWidthRight(0);
                                celdaXX.setBorderWidthLeft(0);
                                tablaXX.addCell(celdaXX);
                                
                                //% No VOLATILES
                                celdaXX = new PdfPCell(new Paragraph(res_tmp,smallFont));
                                celdaXX.setBorderWidthBottom(0);
                                celdaXX.setBorderWidthTop(0);
                                celdaXX.setBorderWidthRight(0);
                                celdaXX.setBorderWidthLeft(0);
                                tablaXX.addCell(celdaXX);
                                
                                //% No VOLATILES
                                //registrotmp.get("inst_volatiles")
                                celdaXX = new PdfPCell(new Paragraph(StringHelper.isNullString(registrotmp.get("inst_volatil")) ,smallFont));
                                celdaXX.setBorderWidthBottom(0);
                                celdaXX.setBorderWidthTop(0);
                                celdaXX.setBorderWidthRight(0);
                                celdaXX.setBorderWidthLeft(0);
                                tablaXX.addCell(celdaXX);
                                
                                //% No VOLATILES
                                celdaXX = new PdfPCell(tabla_tmp.addResultadosAnalisis(especificaciones_produccion, "volatiles"));
                                celdaXX.setBorderWidthBottom(0);
                                celdaXX.setBorderWidthTop(0);
                                celdaXX.setBorderWidthRight(0);
                                celdaXX.setBorderWidthLeft(0);
                                tablaXX.addCell(celdaXX);
                                
                                //% No VOLATILES
                                celdaXX = new PdfPCell(new Paragraph("",smallFont));
                                celdaXX.setBorderWidthBottom(0);
                                celdaXX.setBorderWidthTop(0);
                                celdaXX.setBorderWidthRight(0);
                                celdaXX.setBorderWidthLeft(0);
                                tablaXX.addCell(celdaXX);
                            } 

                                
                                
                                
                            res_tmp = cadena_especificaciones(texto_especificacion, registrotmp.get("hidrogeno_inicial"), registrotmp.get("hidrogeno_final"), " ");
                            if(!res_tmp.equals("N.A.") && !res_tmp.trim().equals("")){
                                //columna 1 fil1 para pH
                                celdaXX = new PdfPCell(new Paragraph("pH: ",smallFont));
                                celdaXX.setBorderWidthBottom(0);
                                celdaXX.setBorderWidthTop(0);
                                celdaXX.setBorderWidthRight(0);
                                celdaXX.setBorderWidthLeft(0);
                                tablaXX.addCell(celdaXX);
                                
                                //pH
                                celdaXX = new PdfPCell(new Paragraph(res_tmp,smallFont));
                                celdaXX.setBorderWidthBottom(0);
                                celdaXX.setBorderWidthTop(0);
                                celdaXX.setBorderWidthRight(0);
                                celdaXX.setBorderWidthLeft(0);
                                tablaXX.addCell(celdaXX);
                                
                                //pH
                                //System.out.println("inst_hidrogeno:  "+registrotmp.get("inst_hidrogeno"));
                                celdaXX = new PdfPCell(new Paragraph(StringHelper.isNullString(registrotmp.get("inst_hidrogeno")) ,smallFont));
                                celdaXX.setBorderWidthBottom(0);
                                celdaXX.setBorderWidthTop(0);
                                celdaXX.setBorderWidthRight(0);
                                celdaXX.setBorderWidthLeft(0);
                                tablaXX.addCell(celdaXX);
                                
                                //pH
                                celdaXX = new PdfPCell(tabla_tmp.addResultadosAnalisis(especificaciones_produccion, "hidrogeno"));
                                celdaXX.setBorderWidthBottom(0);
                                celdaXX.setBorderWidthTop(0);
                                celdaXX.setBorderWidthRight(0);
                                celdaXX.setBorderWidthLeft(0);
                                tablaXX.addCell(celdaXX);
                                
                                //pH
                                celdaXX = new PdfPCell(new Paragraph("",smallFont));
                                celdaXX.setBorderWidthBottom(0);
                                celdaXX.setBorderWidthTop(0);
                                celdaXX.setBorderWidthRight(0);
                                celdaXX.setBorderWidthLeft(0);
                                tablaXX.addCell(celdaXX);
                            } 

                                
                                
                            res_tmp = cadena_especificaciones(texto_especificacion, registrotmp.get("cubriente_inicial"), registrotmp.get("cubriente_final"), "%");
                            if(!res_tmp.equals("N.A.") && !res_tmp.trim().equals("")){
                                //columna 1 fil1 para CUBRIENTE
                                celdaXX = new PdfPCell(new Paragraph("CUBRIENTE: ",smallFont));
                                celdaXX.setBorderWidthBottom(0);
                                celdaXX.setBorderWidthTop(0);
                                celdaXX.setBorderWidthRight(0);
                                celdaXX.setBorderWidthLeft(0);
                                tablaXX.addCell(celdaXX);
                                
                                //CUBRIENTE
                                celdaXX = new PdfPCell(new Paragraph(res_tmp,smallFont));
                                celdaXX.setBorderWidthBottom(0);
                                celdaXX.setBorderWidthTop(0);
                                celdaXX.setBorderWidthRight(0);
                                celdaXX.setBorderWidthLeft(0);
                                tablaXX.addCell(celdaXX);
                                
                                //CUBRIENTE
                                //System.out.println("inst_cubriente:  "+registrotmp.get("inst_cubriente"));
                                celdaXX = new PdfPCell(new Paragraph(StringHelper.isNullString(registrotmp.get("inst_cubriente")) ,smallFont));
                                celdaXX.setBorderWidthBottom(0);
                                celdaXX.setBorderWidthTop(0);
                                celdaXX.setBorderWidthRight(0);
                                celdaXX.setBorderWidthLeft(0);
                                tablaXX.addCell(celdaXX);
                                
                                //CUBRIENTE
                                celdaXX = new PdfPCell(tabla_tmp.addResultadosAnalisis(especificaciones_produccion, "cubriente"));
                                celdaXX.setBorderWidthBottom(0);
                                celdaXX.setBorderWidthTop(0);
                                celdaXX.setBorderWidthRight(0);
                                celdaXX.setBorderWidthLeft(0);
                                tablaXX.addCell(celdaXX);
                                
                                //CUBRIENTE
                                celdaXX = new PdfPCell(new Paragraph("",smallFont));
                                celdaXX.setBorderWidthBottom(0);
                                celdaXX.setBorderWidthTop(0);
                                celdaXX.setBorderWidthRight(0);
                                celdaXX.setBorderWidthLeft(0);
                                tablaXX.addCell(celdaXX);                                
                            } 

                                
                                
                                
                                
                            res_tmp = cadena_especificaciones(texto_especificacion, registrotmp.get("tono_inicial"), registrotmp.get("tono_final"), "");
                            if(!res_tmp.equals("N.A.") && !res_tmp.trim().equals("")){
                                //columna 1 fil1 para TONO
                                celdaXX = new PdfPCell(new Paragraph("TONO: ",smallFont));
                                celdaXX.setBorderWidthBottom(0);
                                celdaXX.setBorderWidthTop(0);
                                celdaXX.setBorderWidthRight(0);
                                celdaXX.setBorderWidthLeft(0);
                                tablaXX.addCell(celdaXX);
                                
                                //TONO
                                celdaXX = new PdfPCell(new Paragraph(res_tmp,smallFont));
                                celdaXX.setBorderWidthBottom(0);
                                celdaXX.setBorderWidthTop(0);
                                celdaXX.setBorderWidthRight(0);
                                celdaXX.setBorderWidthLeft(0);
                                tablaXX.addCell(celdaXX);
                                
                                //TONO
                                //System.out.println("inst_tono:  "+registrotmp.get("inst_tono"));
                                celdaXX = new PdfPCell(new Paragraph(StringHelper.isNullString(registrotmp.get("inst_tono")),smallFont));
                                celdaXX.setBorderWidthBottom(0);
                                celdaXX.setBorderWidthTop(0);
                                celdaXX.setBorderWidthRight(0);
                                celdaXX.setBorderWidthLeft(0);
                                tablaXX.addCell(celdaXX);
                                
                                //TONO
                                celdaXX = new PdfPCell(tabla_tmp.addResultadosAnalisis(especificaciones_produccion, "tono"));
                                celdaXX.setBorderWidthBottom(0);
                                celdaXX.setBorderWidthTop(0);
                                celdaXX.setBorderWidthRight(0);
                                celdaXX.setBorderWidthLeft(0);
                                tablaXX.addCell(celdaXX);
                                
                                //TONO
                                celdaXX = new PdfPCell(new Paragraph("",smallFont));
                                celdaXX.setBorderWidthBottom(0);
                                celdaXX.setBorderWidthTop(0);
                                celdaXX.setBorderWidthRight(0);
                                celdaXX.setBorderWidthLeft(0);
                                tablaXX.addCell(celdaXX);
                            } 

                                
                                
                            res_tmp = cadena_especificaciones(texto_especificacion, registrotmp.get("brillo_inicial"), registrotmp.get("brillo_final"), "Unid. de brillo");
                            if(!res_tmp.equals("N.A.") && !res_tmp.trim().equals("")){
                                //columna 1 fil1 para BRILLO
                                celdaXX = new PdfPCell(new Paragraph("BRILLO: ",smallFont));
                                celdaXX.setBorderWidthBottom(0);
                                celdaXX.setBorderWidthTop(0);
                                celdaXX.setBorderWidthRight(0);
                                celdaXX.setBorderWidthLeft(0);
                                tablaXX.addCell(celdaXX);
                                
                                //BRILLO
                                celdaXX = new PdfPCell(new Paragraph(res_tmp,smallFont));
                                celdaXX.setBorderWidthBottom(0);
                                celdaXX.setBorderWidthTop(0);
                                celdaXX.setBorderWidthRight(0);
                                celdaXX.setBorderWidthLeft(0);
                                tablaXX.addCell(celdaXX);
                                
                                //BRILLO
                                //System.out.println("Brillo:  "+registrotmp.get("inst_brillo"));
                                celdaXX = new PdfPCell(new Paragraph(StringHelper.isNullString(registrotmp.get("inst_brillo")) ,smallFont));
                                celdaXX.setBorderWidthBottom(0);
                                celdaXX.setBorderWidthTop(0);
                                celdaXX.setBorderWidthRight(0);
                                celdaXX.setBorderWidthLeft(0);
                                tablaXX.addCell(celdaXX);
                                
                                //BRILLO
                                celdaXX = new PdfPCell(tabla_tmp.addResultadosAnalisis(especificaciones_produccion, "brillo"));
                                celdaXX.setBorderWidthBottom(0);
                                celdaXX.setBorderWidthTop(0);
                                celdaXX.setBorderWidthRight(0);
                                celdaXX.setBorderWidthLeft(0);
                                tablaXX.addCell(celdaXX);
                                
                                //BRILLO
                                celdaXX = new PdfPCell(new Paragraph("",smallFont));
                                celdaXX.setBorderWidthBottom(0);
                                celdaXX.setBorderWidthTop(0);
                                celdaXX.setBorderWidthRight(0);
                                celdaXX.setBorderWidthLeft(0);
                                tablaXX.addCell(celdaXX);
                            } 

                                
                                
                                
                                
                            if(registrotmp.get("dureza_inicial").equals("N.A.")){
                                res_tmp = "";
                            }else{
                                res_tmp = "DE "+registrotmp.get("dureza_inicial")+" A "+registrotmp.get("dureza_final")+" Letras";
                            }
                            if(!res_tmp.equals("N.A.") && !res_tmp.trim().equals("")){
                                //columna 1 fil1 para DUREZA
                                celdaXX = new PdfPCell(new Paragraph("DUREZA: ",smallFont));
                                celdaXX.setBorderWidthBottom(0);
                                celdaXX.setBorderWidthTop(0);
                                celdaXX.setBorderWidthRight(0);
                                celdaXX.setBorderWidthLeft(0);
                                tablaXX.addCell(celdaXX);
                                
                                //DUREZA
                                celdaXX = new PdfPCell(new Paragraph(res_tmp,smallFont));
                                celdaXX.setBorderWidthBottom(0);
                                celdaXX.setBorderWidthTop(0);
                                celdaXX.setBorderWidthRight(0);
                                celdaXX.setBorderWidthLeft(0);
                                tablaXX.addCell(celdaXX);
                                
                                //DUREZA
                                celdaXX = new PdfPCell(new Paragraph(StringHelper.isNullString(registrotmp.get("inst_dureza")),smallFont));
                                celdaXX.setBorderWidthBottom(0);
                                celdaXX.setBorderWidthTop(0);
                                celdaXX.setBorderWidthRight(0);
                                celdaXX.setBorderWidthLeft(0);
                                tablaXX.addCell(celdaXX);
                                
                                //DUREZA
                                celdaXX = new PdfPCell(tabla_tmp.addResultadosAnalisis(especificaciones_produccion, "dureza"));
                                celdaXX.setBorderWidthBottom(0);
                                celdaXX.setBorderWidthTop(0);
                                celdaXX.setBorderWidthRight(0);
                                celdaXX.setBorderWidthLeft(0);
                                tablaXX.addCell(celdaXX);
                                
                                //DUREZA
                                celdaXX = new PdfPCell(new Paragraph("",smallFont));
                                celdaXX.setBorderWidthBottom(0);
                                celdaXX.setBorderWidthTop(0);
                                celdaXX.setBorderWidthRight(0);
                                celdaXX.setBorderWidthLeft(0);
                                tablaXX.addCell(celdaXX);
                            } 

                                
                                
                                
                            res_tmp = cadena_especificaciones(texto_especificacion, registrotmp.get("adherencia_inicial"), registrotmp.get("adherencia_final"), "%");
                            if(!res_tmp.equals("N.A.") && !res_tmp.trim().equals("")){
                                //columna 1 fil1 para ADHERENCIA
                                celdaXX = new PdfPCell(new Paragraph("ADHERENCIA: ",smallFont));
                                celdaXX.setBorderWidthBottom(0);
                                celdaXX.setBorderWidthTop(0);
                                celdaXX.setBorderWidthRight(0);
                                celdaXX.setBorderWidthLeft(0);
                                tablaXX.addCell(celdaXX);
                                
                                //ADHERENCIA
                                celdaXX = new PdfPCell(new Paragraph(res_tmp,smallFont));
                                celdaXX.setBorderWidthBottom(0);
                                celdaXX.setBorderWidthTop(0);
                                celdaXX.setBorderWidthRight(0);
                                celdaXX.setBorderWidthLeft(0);
                                tablaXX.addCell(celdaXX);
                                
                                //ADHERENCIA
                                //System.out.println("ADHERENCIA:  "+registrotmp.get("inst_adherencia"));
                                celdaXX = new PdfPCell(new Paragraph(StringHelper.isNullString(registrotmp.get("inst_adherencia")) ,smallFont));
                                celdaXX.setBorderWidthBottom(0);
                                celdaXX.setBorderWidthTop(0);
                                celdaXX.setBorderWidthRight(0);
                                celdaXX.setBorderWidthLeft(0);
                                tablaXX.addCell(celdaXX);
                                
                                //ADHERENCIA
                                celdaXX = new PdfPCell(tabla_tmp.addResultadosAnalisis(especificaciones_produccion, "adherencia"));
                                celdaXX.setBorderWidthBottom(0);
                                celdaXX.setBorderWidthTop(0);
                                celdaXX.setBorderWidthRight(0);
                                celdaXX.setBorderWidthLeft(0);
                                tablaXX.addCell(celdaXX);
                                
                                //ADHERENCIA
                                celdaXX = new PdfPCell(new Paragraph("",smallFont));
                                celdaXX.setBorderWidthBottom(0);
                                celdaXX.setBorderWidthTop(0);
                                celdaXX.setBorderWidthRight(0);
                                celdaXX.setBorderWidthLeft(0);
                                tablaXX.addCell(celdaXX);
                            } 

                                
                            //}
                            
                         }
                         
                         reporte.add(tablaXX);
                     }
                }
                
                
                //procedimientos
                //Lista de Subprocesos
                ArrayList<HashMap<String, String>> lista_procedimiento = (ArrayList<HashMap<String, String>>) registro.get("lista_procedimiento");
                
                /*Codigo para los procedidmientos*/
                 String verifica_procedimiento = "0";
                 ArrayList<HashMap<String, String>> listaproc = lista_procedimiento;
                 for (int l=0;l<listaproc.size();l++){
                     HashMap<String,String> registro_tmp = listaproc.get(l);
                     verifica_procedimiento = registro_tmp.get("id");
                 }
                 
                 if(!verifica_procedimiento.equals("0")){
                     
                     //crear nueva pagina, esto permite que lo que sigue se pase a  otra pagina nueva
                     reporte.newPage();
                     
                     float [] widthsProc = {0.8f,2.5f,10.3F,0.8F};//Tama√±o de las Columnas.
                     PdfPTable tablaProc = new PdfPTable(widthsProc);
                     PdfPCell celdaProc;
                     tablaProc.setKeepTogether(false);
                     tablaProc.setHeaderRows(2);
                     
                     
                     String id_proced = "";
                     ArrayList<HashMap<String, String>> listaproced = lista_procedimiento;
                     for (int l=0;l<listaproced.size();l++){
                         HashMap<String,String> registro_tmp = listaproced.get(l);
                         
                        if(!id_proced.equals(registro_tmp.get("pro_subp_prod_id"))){
                            id_proced = registro_tmp.get("pro_subp_prod_id");
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
                             celdaProc = new PdfPCell(new Paragraph("PROCEDIMIENTO PARA : "+registro_tmp.get("titulo"),fuentenegrita));
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
                        
                        
                         celdaProc = new PdfPCell(new Paragraph(registro_tmp.get("posicion"),fuenteCont));
                         celdaProc.setUseAscender(true);
                         celdaProc.setHorizontalAlignment(Element.ALIGN_LEFT);
                         celdaProc.setUseDescender(true);
                         celdaProc.setColspan(2);
                         celdaProc.setVerticalAlignment(Element.ALIGN_MIDDLE);
                         tablaProc.addCell(celdaProc);

                         celdaProc = new PdfPCell(new Paragraph(StringHelper.replaceStringMacro("macrocoma", ",", registro_tmp.get("descripcion")),fuenteCont));
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
            
            
            float [] tam_tablay = {3f,2f,4f,2f,3f};
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
            celdaAut.setRowspan(4);
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
            celdaAut = new PdfPCell(new Paragraph("________________________________",fuentenegrita));
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
            
            //texto ACEPTADO ASEG. DE CALIDAD
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
            celdaAut = new PdfPCell(new Paragraph(" ACEPTADO, ASEG. DE CALIDAD ",fuentenegrita));
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
            
            
            
            
            //2 filas en blanco
            //columna 1 a 6 fil2
            celdaAut = new PdfPCell(new Paragraph(" ",fuentenegrita));
            celdaAut.setHorizontalAlignment(Element.ALIGN_LEFT);
            celdaAut.setVerticalAlignment(Element.ALIGN_MIDDLE);
            celdaAut.setBorderWidthBottom(0);
            celdaAut.setBorderWidthTop(0);
            celdaAut.setBorderWidthRight(0);
            celdaAut.setColspan(5);
            celdaAut.setRowspan(3);
            celdaAut.setBorderWidthLeft(0);
            tablaAut.addCell(celdaAut);
            
            //para el texto verifica y reviso formula
            //columna 1 y 2 fil2
            celdaAut = new PdfPCell(new Paragraph("__________________________________",fuentenegrita));
            celdaAut.setHorizontalAlignment(Element.ALIGN_LEFT);
            celdaAut.setVerticalAlignment(Element.ALIGN_MIDDLE);
            celdaAut.setBorderWidthBottom(0);
            celdaAut.setBorderWidthTop(0);
            celdaAut.setBorderWidthRight(0);
            celdaAut.setColspan(5);
            celdaAut.setBorderWidthLeft(0);
            tablaAut.addCell(celdaAut);
            
            
            //para el texto verifica y reviso formula
            //columna 1 y 2 fil2
            celdaAut = new PdfPCell(new Paragraph("  VERIFICA Y REVISO FORMULA",fuentenegrita));
            celdaAut.setHorizontalAlignment(Element.ALIGN_LEFT);
            celdaAut.setVerticalAlignment(Element.ALIGN_MIDDLE);
            celdaAut.setBorderWidthBottom(0);
            celdaAut.setBorderWidthTop(0);
            celdaAut.setBorderWidthRight(0);
            celdaAut.setColspan(5);
            celdaAut.setBorderWidthLeft(0);
            tablaAut.addCell(celdaAut);
            
            //para el texto verifica y reviso formula
            //columna 1 y 2 fil2
            celdaAut = new PdfPCell(new Paragraph("       ASEG. DE CALIDAD",fuentenegrita));
            celdaAut.setHorizontalAlignment(Element.ALIGN_LEFT);
            celdaAut.setVerticalAlignment(Element.ALIGN_MIDDLE);
            celdaAut.setBorderWidthBottom(0);
            celdaAut.setBorderWidthTop(0);
            celdaAut.setBorderWidthRight(0);
            celdaAut.setColspan(5);
            celdaAut.setBorderWidthLeft(0);
            tablaAut.addCell(celdaAut);
            
            
            if(mostrar_autorizacion.equals("true")){
                reporte.add(tablaAut);
            }
            
        }
        
        catch (Exception e){
             System.out.println(e.toString());
             }
        reporte.close();
    }
    
    
    private String cadena_especificaciones(String cadena, String esp_inicial, String esp_final, String unidad_medida){
        
        String texto_especificacion = "";
        
        if(esp_inicial.equals("N.A.")){
            esp_inicial="-1";
        }
        
        if (!convierte_cadena(esp_inicial).equals("N.A.") ){
            if(cadena.equals("header")){
                texto_especificacion = "DE "+convierte_cadena(esp_inicial)+" A "+convierte_cadena(esp_final)+" "+unidad_medida;
            }else{
                texto_especificacion = convierte_cadena(esp_inicial)+" "+unidad_medida;
            }
        }else{
            texto_especificacion = "";
        }
        
        return texto_especificacion;
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
            /*
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
            */
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
     
     
     /*Clase para agregar los resultados de analisis*/
     private class TablaPDF {
         public PdfPTable addResultadosAnalisis(ArrayList<HashMap<String, String>> especificaciones_produccion, String cadena) {
             //System.out.println("Entro addResultadosAnalisis:  "+cadena);
             
            Font smallFont = new Font(Font.FontFamily.HELVETICA,7,Font.NORMAL,BaseColor.BLACK);
            Font smallBoldFont = new Font(Font.FontFamily.HELVETICA, 8, Font.BOLD, BaseColor.WHITE);
            
            Font fuenteCont = new Font(Font.getFamily("ARIAL"),10,Font.NORMAL,BaseColor.BLACK);
            Font fuentenegrita = new Font(Font.getFamily("ARIAL"),8,Font.BOLD,BaseColor.BLACK);
            Font fuenteCont2 = new Font(Font.getFamily("ARIAL"),8,Font.NORMAL,BaseColor.BLACK);
            Font headerFont = new Font(Font.getFamily("ARIAL"),8,Font.BOLD,BaseColor.WHITE);
            
            PdfPTable table = null;
            PdfPTable table2;
            PdfPCell cell;
            
            //System.out.println("Entro addResultadosAnalisis:  "+cadena);
            String verifica_especificaicones = "0";
            int cantidad = 0;
            for (int l=0;l<especificaciones_produccion.size();l++){
                HashMap<String,String> registro1 = especificaciones_produccion.get(l);
                verifica_especificaicones = registro1.get("id");
                cantidad = l;
            }
            
            
            //System.out.println("Entro verifica_especificaicones:  for verifica_especificaicones:"+verifica_especificaicones +"   cantidad: "+cantidad );
            
            if(!verifica_especificaicones.equals("0")){
                //System.out.println("Entro addResultadosAnalisis:  verifica_especificaicones.equals");
                float [] widths = new float[especificaciones_produccion.size()];// = {6,12,6};
                //System.out.println("Entro addResultadosAnalisis:  widths");
                for (int l=0;l<especificaciones_produccion.size();l++){
                    //System.out.println("widths: "+cantidad);
                    widths[l] = (float) 1.0;
                }
                //System.out.println("Entro volatiles_inicial:  "+cantidad);
                table = new PdfPTable(widths);
                table.setKeepTogether(false);
                //System.out.println("Entro volatiles:  "+cantidad);
                
                 ArrayList<HashMap<String, String>> listaesp = especificaciones_produccion;
                 String res_tmp = "";
                 for (int l=0;l<listaesp.size();l++){
                     //System.out.println("Entro volatiles:  "+cantidad);
                     res_tmp = "";
                    HashMap<String,String> registrotmp = listaesp.get(l);
                    int td_encontrado = 0;
                    if(cadena.equals("fineza1")){
                        //columna 1 fil1
                        res_tmp = cadena_especificaciones("", registrotmp.get("fineza_inicial"), registrotmp.get("fineza_inicial"), "");
                        cell = new PdfPCell(new Paragraph(res_tmp,smallFont));
                        cell.setHorizontalAlignment(Element.ALIGN_LEFT);
                        cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                        cell.setBorderWidthBottom(0.1f);
                        cell.setBorderWidthTop(0.1f);
                        cell.setBorderWidthRight(0.1f);
                        cell.setBorderWidthLeft(0.1f);
                        table.addCell(cell);
                        td_encontrado = 1;
                    }
                    
                    if(cadena.equals("viscosidad1")){
                        //columna 1 fil1
                        res_tmp = cadena_especificaciones("", registrotmp.get("viscosidads_inicial"), registrotmp.get("viscosidads_final"), "");
                        cell = new PdfPCell(new Paragraph(res_tmp,smallFont));
                        cell.setHorizontalAlignment(Element.ALIGN_LEFT);
                        cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                        cell.setBorderWidthBottom(0.1f);
                        cell.setBorderWidthTop(0.1f);
                        cell.setBorderWidthRight(0.1f);
                        cell.setBorderWidthLeft(0.1f);
                        table.addCell(cell);
                        td_encontrado = 1;
                    }
                    
                    if(cadena.equals("viscosidad2")){
                        //columna 1 fil1
                        res_tmp = cadena_especificaciones("", registrotmp.get("viscosidadku_inicial"), registrotmp.get("viscosidadku_final"), "");
                        cell = new PdfPCell(new Paragraph(res_tmp,smallFont));
                        cell.setHorizontalAlignment(Element.ALIGN_LEFT);
                        cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                        cell.setBorderWidthBottom(0.1f);
                        cell.setBorderWidthTop(0.1f);
                        cell.setBorderWidthRight(0.1f);
                        cell.setBorderWidthLeft(0.1f);
                        table.addCell(cell);
                        td_encontrado = 1;
                    }
                    
                    if(cadena.equals("viscosidad3")){
                        //columna 1 fil1
                        res_tmp = cadena_especificaciones("", registrotmp.get("viscosidadcps_inicial"), registrotmp.get("viscosidadcps_final"), "");
                        cell = new PdfPCell(new Paragraph(res_tmp,smallFont));
                        cell.setHorizontalAlignment(Element.ALIGN_LEFT);
                        cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                        cell.setBorderWidthBottom(0.1f);
                        cell.setBorderWidthTop(0.1f);
                        cell.setBorderWidthRight(0.1f);
                        cell.setBorderWidthLeft(0.1f);
                        table.addCell(cell);
                        td_encontrado = 1;
                    }
                    
                    if(cadena.equals("densidad")){
                        //columna 1 fil1
                        res_tmp = cadena_especificaciones("", registrotmp.get("densidad_inicial"), registrotmp.get("densidad_final"), "");
                        cell = new PdfPCell(new Paragraph(res_tmp,smallFont));
                        cell.setHorizontalAlignment(Element.ALIGN_LEFT);
                        cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                        cell.setBorderWidthBottom(0.1f);
                        cell.setBorderWidthTop(0.1f);
                        cell.setBorderWidthRight(0.1f);
                        cell.setBorderWidthLeft(0.1f);
                        table.addCell(cell);
                        td_encontrado = 1;
                    }
                    
                    if(cadena.equals("volatiles")){
                        //columna 1 fil1
                        //System.out.println("enteio en volatiles_inicial:  "+registrotmp.get("volatiles_inicial"));
                        res_tmp = cadena_especificaciones("", registrotmp.get("volatiles_inicial"), registrotmp.get("volatiles_final"), "");
                        cell = new PdfPCell(new Paragraph(res_tmp,smallFont));
                        cell.setHorizontalAlignment(Element.ALIGN_LEFT);
                        cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                        cell.setBorderWidthBottom(0.1f);
                        cell.setBorderWidthTop(0.1f);
                        cell.setBorderWidthRight(0.1f);
                        cell.setBorderWidthLeft(0.1f);
                        table.addCell(cell);
                        td_encontrado = 1;
                    }
                    
                    if(cadena.equals("hidrogeno")){
                        //columna 1 fil1
                        res_tmp = cadena_especificaciones("", registrotmp.get("hidrogeno_inicial"), registrotmp.get("hidrogeno_final"), "");
                        cell = new PdfPCell(new Paragraph(res_tmp,smallFont));
                        cell.setHorizontalAlignment(Element.ALIGN_LEFT);
                        cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                        cell.setBorderWidthBottom(0.1f);
                        cell.setBorderWidthTop(0.1f);
                        cell.setBorderWidthRight(0.1f);
                        cell.setBorderWidthLeft(0.1f);
                        table.addCell(cell);
                        td_encontrado = 1;
                    }
                    
                    if(cadena.equals("cubriente")){
                        //columna 1 fil1
                        res_tmp = cadena_especificaciones("", registrotmp.get("cubriente_inicial"), registrotmp.get("cubriente_final"), "");
                        cell = new PdfPCell(new Paragraph(res_tmp,smallFont));
                        cell.setHorizontalAlignment(Element.ALIGN_LEFT);
                        cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                        cell.setBorderWidthBottom(0.1f);
                        cell.setBorderWidthTop(0.1f);
                        cell.setBorderWidthRight(0.1f);
                        cell.setBorderWidthLeft(0.1f);
                        table.addCell(cell);
                        td_encontrado = 1;
                    }
                    
                    if(cadena.equals("tono")){
                        //columna 1 fil1
                        res_tmp = cadena_especificaciones("", registrotmp.get("tono_inicial"), registrotmp.get("tono_final"), "");
                        cell = new PdfPCell(new Paragraph(res_tmp,smallFont));
                        cell.setHorizontalAlignment(Element.ALIGN_LEFT);
                        cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                        cell.setBorderWidthBottom(0.1f);
                        cell.setBorderWidthTop(0.1f);
                        cell.setBorderWidthRight(0.1f);
                        cell.setBorderWidthLeft(0.1f);
                        table.addCell(cell);
                        td_encontrado = 1;
                    }
                    
                    if(cadena.equals("brillo")){
                        //columna 1 fil1
                        res_tmp = cadena_especificaciones("", registrotmp.get("brillo_inicial"), registrotmp.get("brillo_final"), "");
                        cell = new PdfPCell(new Paragraph(res_tmp,smallFont));
                        cell.setHorizontalAlignment(Element.ALIGN_LEFT);
                        cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                        cell.setBorderWidthBottom(0.1f);
                        cell.setBorderWidthTop(0.1f);
                        cell.setBorderWidthRight(0.1f);
                        cell.setBorderWidthLeft(0.1f);
                        table.addCell(cell);
                        td_encontrado = 1;
                    }
                    
                    if(cadena.equals("dureza")){
                        //columna 1 fil1
                        if(registrotmp.get("dureza_inicial").equals("N.A.")){
                            res_tmp = "";
                        }else{
                            res_tmp = registrotmp.get("dureza_inicial");
                        }
                        //res_tmp = cadena_especificaciones("", registrotmp.get("dureza_inicial"), registrotmp.get("dureza_final"), "");
                        cell = new PdfPCell(new Paragraph(res_tmp,smallFont));
                        cell.setHorizontalAlignment(Element.ALIGN_LEFT);
                        cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                        cell.setBorderWidthBottom(0.1f);
                        cell.setBorderWidthTop(0.1f);
                        cell.setBorderWidthRight(0.1f);
                        cell.setBorderWidthLeft(0.1f);
                        table.addCell(cell);
                        td_encontrado = 1;
                    }
                    
                    if(cadena.equals("adherencia")){
                        //columna 1 fil1
                        res_tmp = cadena_especificaciones("", registrotmp.get("adherencia_inicial"), registrotmp.get("adherencia_final"), "");
                        cell = new PdfPCell(new Paragraph(res_tmp,smallFont));
                        cell.setHorizontalAlignment(Element.ALIGN_LEFT);
                        cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                        cell.setBorderWidthBottom(0.1f);
                        cell.setBorderWidthTop(0.1f);
                        cell.setBorderWidthRight(0.1f);
                        cell.setBorderWidthLeft(0.1f);
                        table.addCell(cell);
                        td_encontrado = 1;
                    }
                    
                    if(td_encontrado == 0){
                        //columna 1 fil1
                        cell = new PdfPCell(new Paragraph("",smallFont));
                        cell.setHorizontalAlignment(Element.ALIGN_LEFT);
                        cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                        cell.setBorderWidthBottom(0.1f);
                        cell.setBorderWidthTop(0.1f);
                        cell.setBorderWidthRight(0.1f);
                        cell.setBorderWidthLeft(0.1f);
                        table.addCell(cell);
                        td_encontrado = 1;
                    }
                    
                 }
             }else{
                float [] widths = {1.0f};
                
                table = new PdfPTable(widths);
                table.setKeepTogether(false);
                
                cell = new PdfPCell(new Paragraph("",smallFont));
                cell.setHorizontalAlignment(Element.ALIGN_LEFT);
                cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                cell.setBorderWidthBottom(0.1f);
                cell.setBorderWidthTop(0.1f);
                cell.setBorderWidthRight(0.1f);
                cell.setBorderWidthLeft(0.1f);
                table.addCell(cell);
            }
            
            return table;
            
        }
    }
    
}
