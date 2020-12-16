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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author root
 */
public class PdfReportedePedidos {
    
    
    
          public PdfReportedePedidos(HashMap<String, String> datosEncabezadoPie, String fileout, ArrayList<HashMap<String, String>> pedidos, HashMap<String, String> datos) throws DocumentException {
              
               
                //variables para las sumatorias
                //pesos
                Double suma_pesos_subtotal = 0.0;
                Double suma_pesos_monto_ieps = 0.0;
                Double suma_pesos_impuesto = 0.0;
                Double suma_pesos_total = 0.0;
                //dolares
                Double suma_dolares_subtotal = 0.0;
                Double suma_dolares_monto_ieps = 0.0;
                Double suma_dolares_impuesto = 0.0;
                Double suma_dolares_total = 0.0;
                //totales
                Double suma_subtotal_mn = 0.0;
                Double suma_monto_ieps_mn = 0.0;
                Double suma_impuesto_mn = 0.0;
                Double suma_total_mn = 0.0;
              
               try {
                    Font smallFont = new Font(Font.FontFamily.HELVETICA,8,Font.NORMAL,BaseColor.BLACK);
                    Font smallBoldFont = new Font(Font.getFamily("ARIAL"),8,Font.BOLD,BaseColor.WHITE);
                    
                    String[] fi = datos.get("fecha_inicial").split("-");
                    String[] ff = datos.get("fecha_final").split("-");
                    String fecha_reporte = "Periodo  del  "+fi[2]+"/"+fi[1]+"/"+fi[0]+"  al  "+ff[2]+"/"+ff[1]+"/"+ff[0];

                    //datos para el encabezado
                    datos.put("empresa", datosEncabezadoPie.get("nombre_empresa_emisora"));
                    datos.put("titulo_reporte", datosEncabezadoPie.get("titulo_reporte"));
                    datos.put("periodo", fecha_reporte);

                    //datos para el pie de pagina
                    datos.put("codigo1", "");
                    datos.put("codigo2", "");

                    PdfReportedePedidos.HeaderFooter event = new PdfReportedePedidos.HeaderFooter(datos);
        
                    Document doc = new Document(PageSize.LETTER.rotate(),-50,-50,60,30);
                    doc.addCreator("ez.eexe21@gmail.com");
                    PdfWriter writer = PdfWriter.getInstance(doc, new FileOutputStream(fileout));
                    writer.setPageEvent(event);
                    
                    doc.open();
                    //float [] widths = {0.8f,0.8f,0.8f,4,0.5f,1,0.5f,1,0.5f,1};//Tamaño de las Columnas.
                    float [] widths = {0.8f,0.8f,0.8f,4,0.5f,1,0.5f,1,0.5f,1,0.5f,1};//Tamaño de las Columnas.
                    PdfPTable tabla = new PdfPTable(widths);
                    PdfPCell cell;
                    tabla.setKeepTogether(false);
                    tabla.setHeaderRows(1);
                    
                    String titulos[ ] ={"Pedido","O.Compra","Fecha","Cliente","","Subtotal","","IEPS","","IVA","","Total"};
                    java.util.List<String>  lista_columnas = (java.util.List<String>) Arrays.asList(titulos); 
                    //añadiendo e arreglo a la lista
            
            for ( String columna_titulo : lista_columnas){
                PdfPCell celda = null;
                 
               //cell = new PdfPCell(new Paragraph(titulos[i],smallBoldFont));
               celda = new PdfPCell(new Paragraph(columna_titulo,smallBoldFont)); 
               celda.setUseAscender(true);
               
               celda.setUseDescender(true);
               celda.setVerticalAlignment(Element.ALIGN_MIDDLE);
               celda.setBackgroundColor(BaseColor.BLACK);
               
                if (columna_titulo.equals("Pedido")){
                   celda.setHorizontalAlignment(Element.ALIGN_CENTER);
                }
                if (columna_titulo.equals("O.Compra")){
                   celda.setHorizontalAlignment(Element.ALIGN_CENTER);
                }
                if (columna_titulo.equals("Fecha")){
                   celda.setHorizontalAlignment(Element.ALIGN_CENTER);
                }
                 if (columna_titulo.equals("Cliente")){
                   celda.setHorizontalAlignment(Element.ALIGN_CENTER);
                }
                 /*
                if (columna_titulo.equals("")){
                   celda.setHorizontalAlignment(Element.ALIGN_CENTER);
                }
                  */
                if (columna_titulo.equals("Subtotal")){
                   celda.setHorizontalAlignment(Element.ALIGN_CENTER);
                }
                /*
                if (columna_titulo.equals("")){
                   celda.setHorizontalAlignment(Element.ALIGN_CENTER);
                }
                 */
                if (columna_titulo.equals("IEPS")){
                   celda.setHorizontalAlignment(Element.ALIGN_CENTER);
                }
                if (columna_titulo.equals("IVA")){
                   celda.setHorizontalAlignment(Element.ALIGN_CENTER);
                }
                if (columna_titulo.equals("Total")){
                   celda.setHorizontalAlignment(Element.ALIGN_CENTER);
                }
                
                if (columna_titulo.equals("")){
                   celda.setHorizontalAlignment(Element.ALIGN_CENTER);
                }
               tabla.addCell(celda);
            }
            
            
            for (int x=0; x<=(pedidos.size())-1; x++){
                 HashMap<String, String> registro= pedidos.get(x);
                 
                //sumar cantidades
                if(registro.get("moneda_factura").equals("M.N.")){
                    suma_pesos_subtotal += Double.parseDouble(registro.get("subtotal"));
                    suma_pesos_monto_ieps += Double.parseDouble(registro.get("monto_ieps"));
                    suma_pesos_impuesto += Double.parseDouble(registro.get("impuesto"));
                    suma_pesos_total += Double.parseDouble(registro.get("total"));
                }

                if(registro.get("moneda_factura").equals("USD")){
                    suma_dolares_subtotal += Double.parseDouble(registro.get("subtotal"));
                    suma_dolares_monto_ieps += Double.parseDouble(registro.get("monto_ieps"));
                    suma_dolares_impuesto += Double.parseDouble(registro.get("impuesto"));
                    suma_dolares_total += Double.parseDouble(registro.get("total"));
                }

                suma_subtotal_mn += Double.parseDouble(registro.get("subtotal_mn"));
                suma_monto_ieps_mn += Double.parseDouble(registro.get("monto_ieps_mn"));
                suma_impuesto_mn += Double.parseDouble(registro.get("impuesto_mn"));
                suma_total_mn += Double.parseDouble(registro.get("total_mn"));
                
                //1  
                cell = new PdfPCell(new Paragraph(registro.get("folio").toString(),smallFont));
                cell.setUseAscender(true);
                cell.setHorizontalAlignment(Element.ALIGN_LEFT);
                cell.setUseDescender(true);
                cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                cell.setBorder(0);
                tabla.addCell(cell);
                //2                              
                cell = new PdfPCell(new Paragraph(registro.get("orden_compra").toString(),smallFont));
                cell.setUseAscender(true);
                cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                cell.setUseDescender(true);
                cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                cell.setBorder(0);
                tabla.addCell(cell);
                //3   
                cell = new PdfPCell(new Paragraph(registro.get("fecha_factura").toString(),smallFont));
                cell.setUseAscender(true);
                cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                cell.setUseDescender(true);
                cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                cell.setBorder(0);
                tabla.addCell(cell);
                //4
                cell = new PdfPCell(new Paragraph(registro.get("cliente").toString(),smallFont));
                cell.setUseAscender(true);
                cell.setHorizontalAlignment(Element.ALIGN_LEFT);
                cell.setUseDescender(true);
                cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                cell.setBorder(0);
                tabla.addCell(cell);
                //5
                cell = new PdfPCell(new Paragraph(registro.get("simbolo_moneda"),smallFont));
                cell.setUseAscender(true);
                cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                cell.setUseDescender(true);
                cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                cell.setBorder(0);
                tabla.addCell(cell);
                //6
                cell = new PdfPCell(new Paragraph(StringHelper.AgregaComas(StringHelper.roundDouble(registro.get("subtotal").toString(),2)),smallFont));
                cell.setUseAscender(true);
                cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                cell.setUseDescender(true);
                cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                cell.setBorder(0);
                tabla.addCell(cell);
                
                
                //Columna simbolo Moneda IEPS
                cell = new PdfPCell(new Paragraph(registro.get("simbolo_moneda"),smallFont));
                cell.setUseAscender(true);
                cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                cell.setUseDescender(true);
                cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                cell.setBorder(0);
                tabla.addCell(cell);
                
                //Columna Monto IEPS
                cell = new PdfPCell(new Paragraph(StringHelper.AgregaComas(StringHelper.roundDouble(registro.get("monto_ieps").toString(),2)),smallFont));
                cell.setUseAscender(true);
                cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                cell.setUseDescender(true);
                cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                cell.setBorder(0);
                tabla.addCell(cell);
                
                
                //Columna simbolo Moneda IVA
                cell = new PdfPCell(new Paragraph(registro.get("simbolo_moneda"),smallFont));
                cell.setUseAscender(true);
                cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                cell.setUseDescender(true);
                cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                cell.setBorder(0);
                tabla.addCell(cell);
                
                //Columna Monto IVA
                cell = new PdfPCell(new Paragraph(StringHelper.AgregaComas(StringHelper.roundDouble(registro.get("impuesto").toString(),2)),smallFont));
                cell.setUseAscender(true);
                cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                cell.setUseDescender(true);
                cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                cell.setBorder(0);
                tabla.addCell(cell);
                //9
                cell = new PdfPCell(new Paragraph(registro.get("simbolo_moneda").toString(),smallFont));
                cell.setUseAscender(true);
                cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                cell.setUseDescender(true);
                cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                cell.setBorder(0);
                tabla.addCell(cell);
                //10
                cell = new PdfPCell(new Paragraph(StringHelper.AgregaComas(StringHelper.roundDouble(registro.get("total").toString(),2)),smallFont));
                cell.setUseAscender(true);
                cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                cell.setUseDescender(true);
                cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                cell.setBorder(0);
                tabla.addCell(cell);    
            }
            
            /*----------------------------------------TOTALES MONEDA NACIONAL------------------------------------------*/
                //1-4
                cell = new PdfPCell(new Paragraph("TOTAL M.N.",smallFont));
                cell.setUseAscender(true);
                cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                cell.setUseDescender(true);
                cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                cell.setBorder(1);
                cell.setColspan(4);
                tabla.addCell(cell);
                //5
                cell = new PdfPCell(new Paragraph("$",smallFont));
                cell.setUseAscender(true);
                cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                cell.setUseDescender(true);
                cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                cell.setBorder(1);
                tabla.addCell(cell);
                
                //6
                cell = new PdfPCell(new Paragraph(StringHelper.AgregaComas(StringHelper.roundDouble(suma_pesos_subtotal,2)),smallFont));
                cell.setUseAscender(true);
                cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                cell.setUseDescender(true);
                cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                cell.setBorder(1);
                tabla.addCell(cell);
                
                
                //IEPS MN
                cell = new PdfPCell(new Paragraph("$",smallFont));
                cell.setUseAscender(true);
                cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                cell.setUseDescender(true);
                cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                cell.setBorder(1);
                tabla.addCell(cell);
                
                cell = new PdfPCell(new Paragraph(StringHelper.AgregaComas(StringHelper.roundDouble(suma_pesos_monto_ieps,2)),smallFont));
                cell.setUseAscender(true);
                cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                cell.setUseDescender(true);
                cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                cell.setBorder(1);
                tabla.addCell(cell);
                
                //7
                cell = new PdfPCell(new Paragraph("$",smallFont));
                cell.setUseAscender(true);
                cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                cell.setUseDescender(true);
                cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                cell.setBorder(1);
                tabla.addCell(cell);
                //8
                cell = new PdfPCell(new Paragraph(StringHelper.AgregaComas(StringHelper.roundDouble(suma_pesos_impuesto,2)),smallFont));
                cell.setUseAscender(true);
                cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                cell.setUseDescender(true);
                cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                cell.setBorder(1);
                tabla.addCell(cell);
                //9
                cell = new PdfPCell(new Paragraph("$",smallFont));
                cell.setUseAscender(true);
                cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                cell.setUseDescender(true);
                cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                cell.setBorder(1);
                tabla.addCell(cell);
                //10
                cell = new PdfPCell(new Paragraph(StringHelper.AgregaComas(StringHelper.roundDouble(suma_pesos_total,2)),smallFont));
                cell.setUseAscender(true);
                cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                cell.setUseDescender(true);
                cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                cell.setBorder(1);
                tabla.addCell(cell);


                /*----------------------------------------TOTALES USD------------------------------------------*/
                //1-4
                cell = new PdfPCell(new Paragraph("TOTAL USD",smallFont));
                cell.setUseAscender(true);
                cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                cell.setUseDescender(true);
                cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                cell.setBorder(0);
                cell.setColspan(4);
                tabla.addCell(cell);
                //5
                cell = new PdfPCell(new Paragraph("USD",smallFont));
                cell.setUseAscender(true);
                cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                cell.setUseDescender(true);
                cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                cell.setBorder(0);
                tabla.addCell(cell);
                //6
                cell = new PdfPCell(new Paragraph(StringHelper.AgregaComas(StringHelper.roundDouble(suma_dolares_subtotal,2)),smallFont));
                cell.setUseAscender(true);
                cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                cell.setUseDescender(true);
                cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                cell.setBorder(0);
                tabla.addCell(cell);
                
                //IEPS USD
                cell = new PdfPCell(new Paragraph("USD",smallFont));
                cell.setUseAscender(true);
                cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                cell.setUseDescender(true);
                cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                cell.setBorder(0);
                tabla.addCell(cell);
                
                cell = new PdfPCell(new Paragraph(StringHelper.AgregaComas(StringHelper.roundDouble(suma_dolares_monto_ieps,2)),smallFont));
                cell.setUseAscender(true);
                cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                cell.setUseDescender(true);
                cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                cell.setBorder(0);
                tabla.addCell(cell);
                
                //7
                cell = new PdfPCell(new Paragraph("USD",smallFont));
                cell.setUseAscender(true);
                cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                cell.setUseDescender(true);
                cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                cell.setBorder(0);
                tabla.addCell(cell);
                //8
                cell = new PdfPCell(new Paragraph(StringHelper.AgregaComas(StringHelper.roundDouble(suma_dolares_impuesto,2)),smallFont));
                cell.setUseAscender(true);
                cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                cell.setUseDescender(true);
                cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                cell.setBorder(0);
                tabla.addCell(cell);

                //9
                cell = new PdfPCell(new Paragraph("USD",smallFont));
                cell.setUseAscender(true);
                cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                cell.setUseDescender(true);
                cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                cell.setBorder(0);
                tabla.addCell(cell);
                //10
                cell = new PdfPCell(new Paragraph(StringHelper.AgregaComas(StringHelper.roundDouble(suma_dolares_total,2)),smallFont));
                cell.setUseAscender(true);
                cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                cell.setUseDescender(true);
                cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                cell.setBorder(0);
                tabla.addCell(cell);
                
                /*----------------------------------------TOTAL GENERAL EN MN------------------------------------------*/
                //1-4
                cell = new PdfPCell(new Paragraph("TOTAL GENERAL M.N.",smallFont));
                cell.setUseAscender(true);
                cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                cell.setUseDescender(true);
                cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                cell.setBorder(0);
                cell.setColspan(4);
                tabla.addCell(cell);
                //5
                cell = new PdfPCell(new Paragraph("$",smallFont));
                cell.setUseAscender(true);
                cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                cell.setUseDescender(true);
                cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                cell.setBorder(0);
                tabla.addCell(cell);
                //6
                cell = new PdfPCell(new Paragraph(StringHelper.AgregaComas(StringHelper.roundDouble(suma_subtotal_mn,2)),smallFont));
                cell.setUseAscender(true);
                cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                cell.setUseDescender(true);
                cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                cell.setBorder(0);
                tabla.addCell(cell);
                
                //Total IEPS MN
                cell = new PdfPCell(new Paragraph("$",smallFont));
                cell.setUseAscender(true);
                cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                cell.setUseDescender(true);
                cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                cell.setBorder(0);
                tabla.addCell(cell);
                
                cell = new PdfPCell(new Paragraph(StringHelper.AgregaComas(StringHelper.roundDouble(suma_monto_ieps_mn,2)),smallFont));
                cell.setUseAscender(true);
                cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                cell.setUseDescender(true);
                cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                cell.setBorder(0);
                tabla.addCell(cell);
                
                
                //7
                cell = new PdfPCell(new Paragraph("$",smallFont));
                cell.setUseAscender(true);
                cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                cell.setUseDescender(true);
                cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                cell.setBorder(0);
                tabla.addCell(cell);
                //8
                cell = new PdfPCell(new Paragraph(StringHelper.AgregaComas(StringHelper.roundDouble(suma_impuesto_mn,2)),smallFont));
                cell.setUseAscender(true);
                cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                cell.setUseDescender(true);
                cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                cell.setBorder(0);
                tabla.addCell(cell);
                //9
                cell = new PdfPCell(new Paragraph("$",smallFont));
                cell.setUseAscender(true);
                cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                cell.setUseDescender(true);
                cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                cell.setBorder(0);
                tabla.addCell(cell);
                //10
                cell = new PdfPCell(new Paragraph(StringHelper.AgregaComas(StringHelper.roundDouble(suma_total_mn,2)),smallFont));
                cell.setUseAscender(true);
                cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                cell.setUseDescender(true);
                cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                cell.setBorder(0);
                tabla.addCell(cell);
                              
                              
            doc.add(tabla);
            doc.close();
	} catch (FileNotFoundException ex) {
            Logger.getLogger(PdfReportedePedidos.class.getName()).log(Level.SEVERE, null, ex);
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
        
        
        /*Añadimos una tabla con  una imagen del logo de megestiono y creamos la fuente para el documento, 
         * la imagen esta escalada para que no se muestre pixelada*/   
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
   };//termina clase HeaderFooter
    
    
    
}
