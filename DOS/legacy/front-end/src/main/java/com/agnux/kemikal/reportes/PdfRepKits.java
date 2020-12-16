/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.agnux.kemikal.reportes;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import com.agnux.common.helpers.TimeHelper;

/**
 *
 * @author Vale.vale
 */
public class PdfRepKits {
    //public PdfRepKits(String fileout, ArrayList<HashMap<String, String>> lista_existencias, String razon_social_empresa, String fecha_actual) throws DocumentException {	
        public PdfRepKits(ArrayList<HashMap<String, String>> lista_kits, String razon_social_empresa, String fileout) throws DocumentException {
        try {
           
            Font fontCols = new Font(Font.FontFamily.HELVETICA, 9,Font.NORMAL);
            Font smallFont = new Font(Font.FontFamily.HELVETICA,8,Font.NORMAL,BaseColor.BLACK);
            Font smallBoldFont = new Font(Font.getFamily("ARIAL"),8,Font.BOLD,BaseColor.WHITE);

            // aqui ba header foother
           
            HeaderFooter event = new HeaderFooter(razon_social_empresa);

            Document doc = new Document(PageSize.LETTER.rotate(),-50,-50,60,30);
            doc.addCreator("valentin.vale8490@gmail.com");
            PdfWriter writer = PdfWriter.getInstance(doc, new FileOutputStream(fileout));
            //PdfWriter writer = PdfWriter.getInstance(doc, new FileOutputStream(fileout));
            writer.setPageEvent(event);
            
            
            doc.open();
            //String[] columnas = {"No.Control","Nombre","No.Factura","Cliente","","Valor","% Comision","","Total Comision"};
            float [] widths = {1f,1f, 1f};//Tamaño de las Columnas.
            PdfPTable tabla = new PdfPTable(widths);
            PdfPCell cell;
            tabla.setKeepTogether(false);
            tabla.setHeaderRows(1);
            int contador=0;
            
            String titulos[ ] ={"CODIGO","DESCRIPCION","CANTIDAD KITS"};
            java.util.List<String>  lista_columnas = (java.util.List<String>) Arrays.asList(titulos); //añadiendo e arreglo a la lista
            
            for ( String columna_titulo : lista_columnas){
            //for(int i=0; i<titulos.length; i++ ){
                PdfPCell celda = null;
                 
               //cell = new PdfPCell(new Paragraph(titulos[i],smallBoldFont));
               celda = new PdfPCell(new Paragraph(columna_titulo,smallBoldFont)); 
               celda.setUseAscender(true);
               
               celda.setUseDescender(true);
               celda.setVerticalAlignment(Element.ALIGN_MIDDLE);
               celda.setBackgroundColor(BaseColor.BLACK);
               
                if (columna_titulo.equals("CODIGO")){
                   celda.setHorizontalAlignment(Element.ALIGN_LEFT);
                }
                if (columna_titulo.equals("DESCRIPCION")){
                   celda.setHorizontalAlignment(Element.ALIGN_LEFT);
                }
                if (columna_titulo.equals("CANTIDAD KITS")){
                   celda.setHorizontalAlignment(Element.ALIGN_RIGHT);
                }
              
               
               //cell.setFixedHeight(13);
               tabla.addCell(celda);
            }
            
            
            for (int x=0; x<=(lista_kits.size())-1; x++){
                 
                HashMap<String, String> registro= lista_kits.get(x);
                        cell = new PdfPCell(new Paragraph(registro.get("codigo").toString(),smallFont));
                        cell.setUseAscender(true);
                        cell.setHorizontalAlignment(Element.ALIGN_LEFT);
                        cell.setUseDescender(true);
                        cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                        cell.setBorder(0);
                        //cell.setBackgroundColor(BaseColor.BLACK);
                        //cell.setFixedHeight(13);
                        tabla.addCell(cell);
                        
                        cell = new PdfPCell(new Paragraph(registro.get("producto_kit").toString(),smallFont));
                        cell.setUseAscender(true);
                        cell.setHorizontalAlignment(Element.ALIGN_LEFT);
                        cell.setUseDescender(true);
                        cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                        cell.setBorder(0);
                        //cell.setBackgroundColor(BaseColor.BLACK);
                        //cell.setFixedHeight(13);
                        tabla.addCell(cell);
                        
                        cell = new PdfPCell(new Paragraph(registro.get("cantidad_de_kits").toString(),smallFont));
                        cell.setUseAscender(true);
                        cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                        cell.setUseDescender(true);
                        cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                        cell.setBorder(0);
                        
                        tabla.addCell(cell);
                        
                        contador++;
            }
            

            //fila en blanco
            cell= new PdfPCell(new Paragraph(""+contador,smallFont));
            cell.setHorizontalAlignment (Element.ALIGN_RIGHT);
            cell.setBorder(0);
            cell.setColspan(3);
            tabla.addCell(cell);
            
            
            doc.add(tabla);
            doc.close();
	} catch (FileNotFoundException ex) {
            Logger.getLogger(PdfRepKits.class.getName()).log(Level.SEVERE, null, ex);
	}
    }

     

    
    
        
    
    static class HeaderFooter extends PdfPageEventHelper {
        //public Image headerImage;
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
        
        HeaderFooter(String razon_soc_empresa){
            this.setEmpresa(razon_soc_empresa);
            
        }
        
        
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
            ColumnText.showTextAligned(writer.getDirectContent(),Element.ALIGN_CENTER, new Phrase(this.getEmpresa(),largeBoldFont),document.getPageSize().getWidth()/2, document.getPageSize().getTop() -25, 0);
            ColumnText.showTextAligned(writer.getDirectContent(),Element.ALIGN_CENTER, new Phrase("Reporte Existencias en Kits",largeBoldFont),document.getPageSize().getWidth()/2, document.getPageSize().getTop()-38, 0);
            
            SimpleDateFormat formato = new SimpleDateFormat("'a las' HH:mm:ss 'hrs.'");
            String hora_generacion = formato.format(new Date());
            
            String fecha = TimeHelper.getFechaActualMDY();
            
            ColumnText.showTextAligned(writer.getDirectContent(),Element.ALIGN_CENTER, new Phrase("Generado el:  "+fecha+" "+hora_generacion),document.getPageSize().getWidth()/2, document.getPageSize().getTop()-50, 0);
            
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
