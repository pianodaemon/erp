/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.agnux.kemikal.reportes;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;


public class PdfCxpIepsPagado {

    public PdfCxpIepsPagado(HashMap<String,Object> data) {
        
        ArrayList<HashMap<String, String>> datos = new ArrayList<HashMap<String, String>>();
        LinkedHashMap<String, String> columns = new LinkedHashMap<String, String>();
        LinkedHashMap<String, String> totales = new LinkedHashMap<String, String>();
        HashMap<String, Object> conf = new HashMap<String, Object>();
        HashMap<String, String> dataHeaderFooter = new HashMap<String, String>();
        
        datos = (ArrayList<HashMap<String, String>>)data.get("datos");
        conf = (HashMap<String, Object>) data.get("conf");
        columns = (LinkedHashMap<String, String>) conf.get("Columns");
        totales = (LinkedHashMap<String, String>) data.get("totales");
        
        /*
        conf.put("ColumnsHead", columnsHead);
        conf.put("noCols", noCols);
        conf.put("", fileout);
        */
        
        String fileout = String.valueOf(conf.get("fileout"));
        
        dataHeaderFooter.put("empresa", String.valueOf(conf.get("nombreEmpresa")));
        dataHeaderFooter.put("titulo_reporte", String.valueOf(conf.get("tituloReporte")));
        dataHeaderFooter.put("periodo", String.valueOf(conf.get("periodo")));
        dataHeaderFooter.put("codigo1", "");
        dataHeaderFooter.put("codigo2", "");
        
        Integer noCols = Integer.valueOf(String.valueOf(conf.get("noCols")));
        
        try {
            
            //tipos de letras (font's)
            Font smallsmall = new Font(Font.getFamily("ARIAL"),13,Font.NORMAL);
            Font smallBoldFont = new Font(Font.getFamily("ARIAL"),8,Font.BOLD,BaseColor.BLACK);
            Font headerFont = new Font(Font.getFamily("ARIAL"),8,Font.BOLD,BaseColor.WHITE);
            Font smallBoldFont2 = new Font(Font.getFamily("ARIAL"),9,Font.BOLD,BaseColor.BLACK);
            
            Font largeBoldFont = new Font(Font.FontFamily.HELVETICA,10,Font.BOLD,BaseColor.BLACK);
            Font smallFont = new Font(Font.FontFamily.HELVETICA,8,Font.NORMAL,BaseColor.BLACK);
            
            PdfCxcIepsCobrado.HeaderFooter event = new PdfCxcIepsCobrado.HeaderFooter(dataHeaderFooter);
            Document doc = new Document(PageSize.LETTER.rotate(),-50,-50,60,30);
            doc.addCreator("gpmarsan@gmail.com");
            PdfWriter writer = PdfWriter.getInstance(doc, new FileOutputStream(fileout));
            writer.setPageEvent(event);
            
            
            
            doc.open();
            float[] widths = new float[noCols];
            
            widths[0] = 1.9f;//fecha_pago
            widths[1] = 1.9f;//fecha
            widths[2] = 1.9f;//factura
            widths[3] = 0.8f;//moneda_fac
            widths[4] = 0.3f;//moneda_simbolo_subtotal
            widths[5] = 2f;//subtotal
            widths[6] = 0.3f;//moneda_simbolo_retencion
            widths[7] = 2f;//retencion
            widths[8] = 0.3f;//moneda_simbolo_iva
            widths[9] = 2f;//iva
            int x=10;
            
            do{
                //simbolo moneda ieps
                widths[x] = 0.3f;
                x++;
                //Agregar columnas para el IEPS
                widths[x] = 2f;
                x++;
                
            }while(x<(noCols-2));
            
            //simbolo moneda total
            widths[x] = 0.3f;
            x++;
            
            //Columna total
            widths[x] = 2f;
            
            //Crear tabla 
            PdfPTable table = new PdfPTable(widths);
            PdfPCell cell;
            
            table.setKeepTogether(false);
            table.setHeaderRows(1);
            
            //Crear encabezados de columnas
            Iterator it = columns.keySet().iterator();
            while(it.hasNext()){
                String key = (String)it.next();
                // Encabezado de Celda
                cell = new PdfPCell(new Paragraph(columns.get(key).split(":")[0],headerFont));
                cell.setUseAscender(true);
                cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                cell.setUseDescender(true);
                cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                cell.setBackgroundColor(BaseColor.BLACK);
                if(key.indexOf("moneda_simbolo")<0){
                    cell.setBorderColorRight(BaseColor.WHITE);
                    cell.setBorderWidthRight(0.5f);
                }else{
                    cell.setBorderColorRight(BaseColor.BLACK);
                    cell.setBorderWidthRight(0.5f);
                }
                cell.setFixedHeight(13);
                table.addCell(cell);
            }
            
            
            if(datos.size()>0){
                Iterator it2;
                
                for( HashMap<String,String> i : datos ){
                    it2 = columns.keySet().iterator();
                    while(it2.hasNext()){
                        String key = (String)it2.next();
                        
                        cell= new PdfPCell(new Paragraph(i.get(key),smallFont));
                        if(columns.get(key).split(":")[1].equals("left")){
                            cell.setHorizontalAlignment (Element.ALIGN_LEFT);
                        }else{
                            if(columns.get(key).split(":")[1].equals("right")){
                                cell.setHorizontalAlignment (Element.ALIGN_RIGHT);
                            }
                        }
                        cell.setBorder(0);
                        table.addCell(cell);
                    }
                }
                
                
                int etiqueta=0;
                
                //Imprimir totales
                Iterator it3 = columns.keySet().iterator();
                while(it3.hasNext()){
                    String key = (String)it3.next();
                    
                    if(totales.containsKey(key)){
                        cell= new PdfPCell(new Paragraph(totales.get(key),smallBoldFont));
                    }else{
                        if(etiqueta==0){
                            cell= new PdfPCell(new Paragraph("TOTAL",smallBoldFont));
                            etiqueta++;
                        }else{
                            cell= new PdfPCell(new Paragraph("",smallBoldFont));
                        }
                    }
                    if(columns.get(key).split(":")[1].equals("left")){
                        cell.setHorizontalAlignment (Element.ALIGN_LEFT);
                    }else{
                        if(columns.get(key).split(":")[1].equals("right")){
                            cell.setHorizontalAlignment (Element.ALIGN_RIGHT);
                        }
                    }
                    cell.setBorderWidthTop(1);
                    cell.setBorderWidthBottom(0);
                    cell.setBorderWidthLeft(0);
                    cell.setBorderWidthRight(0);
                    table.addCell(cell);
                }
                
                //Fila total IEPS
                cell= new PdfPCell(new Paragraph("TOTAL IEPS",smallBoldFont));
                cell.setHorizontalAlignment (Element.ALIGN_LEFT);
                cell.setBorderWidthTop(1);
                cell.setBorderWidthBottom(0);
                cell.setBorderWidthLeft(0);
                cell.setBorderWidthRight(0);
                cell.setColspan(noCols-1);
                table.addCell(cell);
                
                cell= new PdfPCell(new Paragraph(totales.get("sumaIeps"),smallBoldFont));
                cell.setHorizontalAlignment (Element.ALIGN_RIGHT);
                cell.setBorderWidthTop(1);
                cell.setBorderWidthBottom(0);
                cell.setBorderWidthLeft(0);
                cell.setBorderWidthRight(0);
                table.addCell(cell);
                
                
                //Fila vacia
                cell= new PdfPCell(new Paragraph("",smallFont));
                cell.setHorizontalAlignment (Element.ALIGN_LEFT);
                cell.setBorder(0);
                cell.setColspan(noCols);
                table.addCell(cell);
                
                //FILAS DE NOTAS
                cell= new PdfPCell(new Paragraph("* F. PAGO",smallBoldFont));
                cell.setHorizontalAlignment (Element.ALIGN_LEFT);
                cell.setBorder(0);
                table.addCell(cell);
                
                cell= new PdfPCell(new Paragraph("Fecha de pago",smallFont));
                cell.setHorizontalAlignment (Element.ALIGN_LEFT);
                cell.setBorder(0);
                cell.setColspan(4);
                table.addCell(cell);
                
                cell= new PdfPCell(new Paragraph("* Todas las cantidades que se muestran son en M.N.",smallFont));
                cell.setHorizontalAlignment (Element.ALIGN_LEFT);
                cell.setBorder(0);
                cell.setColspan(noCols-5);
                table.addCell(cell);
                
                
                
                cell= new PdfPCell(new Paragraph("* F. FAC.",smallBoldFont));
                cell.setHorizontalAlignment (Element.ALIGN_LEFT);
                cell.setBorder(0);
                table.addCell(cell);
                
                cell= new PdfPCell(new Paragraph("Fecha de la factura",smallFont));
                cell.setHorizontalAlignment (Element.ALIGN_LEFT);
                cell.setBorder(0);
                cell.setColspan(4);
                table.addCell(cell);
                
                cell= new PdfPCell(new Paragraph("* Solo se muestran facturas pagas totalmente.",smallFont));
                cell.setHorizontalAlignment (Element.ALIGN_LEFT);
                cell.setBorder(0);
                cell.setColspan(noCols-5);
                table.addCell(cell);
                
                
                
                
                cell= new PdfPCell(new Paragraph("* MON.",smallBoldFont));
                cell.setHorizontalAlignment (Element.ALIGN_LEFT);
                cell.setBorder(0);
                table.addCell(cell);
                
                cell= new PdfPCell(new Paragraph("Moneda de la factura",smallFont));
                cell.setHorizontalAlignment (Element.ALIGN_LEFT);
                cell.setBorder(0);
                cell.setColspan(4);
                table.addCell(cell);
                
                cell= new PdfPCell(new Paragraph("",smallFont));
                cell.setHorizontalAlignment (Element.ALIGN_LEFT);
                cell.setBorder(0);
                cell.setColspan(noCols-5);
                table.addCell(cell);
                
            }else{
                //Agregar fila cuando no hay resultados en el reporte
                cell= new PdfPCell(new Paragraph("No hay datos para mostrar",smallFont));
                cell.setHorizontalAlignment (Element.ALIGN_LEFT);
                cell.setBorder(0);
                cell.setColspan(noCols);
                table.addCell(cell);
            }
            
            
            doc.add(table);
            doc.close();
            
        } catch (DocumentException ex) {
            Logger.getLogger(PdfCxcIepsCobrado.class.getName()).log(Level.SEVERE, null, ex);
        } catch (FileNotFoundException ex) {
            Logger.getLogger(PdfCxcIepsCobrado.class.getName()).log(Level.SEVERE, null, ex);
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
