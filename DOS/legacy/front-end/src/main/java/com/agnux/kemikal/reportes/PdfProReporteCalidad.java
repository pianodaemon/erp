package com.agnux.kemikal.reportes;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;


public class PdfProReporteCalidad {

    public PdfProReporteCalidad(HashMap<String, String> datosEncabezadoPie, String fileout, ArrayList<HashMap<String, String>> Datos_Reporte_Calidad) throws DocumentException {

        

        try {
            //tipos de letras (font's)
            Font smallsmall = new Font(Font.getFamily("ARIAL"),13,Font.NORMAL);
            Font smallBoldFont = new Font(Font.getFamily("ARIAL"),8,Font.BOLD,BaseColor.BLACK);
            Font headerFont = new Font(Font.getFamily("ARIAL"),8,Font.BOLD,BaseColor.WHITE);
            Font smallBoldFont2 = new Font(Font.getFamily("ARIAL"),9,Font.BOLD,BaseColor.BLACK);

            Font largeBoldFont = new Font(Font.FontFamily.HELVETICA,10,Font.BOLD,BaseColor.BLACK);
            Font smallFont = new Font(Font.FontFamily.HELVETICA,8,Font.NORMAL,BaseColor.BLACK);
            
            HeaderFooter event = new HeaderFooter(datosEncabezadoPie);
            Document doc = new Document(PageSize.LEGAL.rotate(),-60,-60,60,30);
            doc.addCreator("gpmarsan@gmail.com");
            PdfWriter writer = PdfWriter.getInstance(doc, new FileOutputStream(fileout));
            writer.setPageEvent(event);

            doc.open();
            
            float [] widths = {
                0.5f, //cantK
                0.5f, //cantL
                0.6f, //Lote
                0.5f, //Fecha
                0.3f, //Fineza
                0.5f, //Viscosidad
                0.4f, //Densidad
                0.3f, //PC %
                0.3f, //DE
                0.3f, //Brillo
                0.3f, //Dureza
                0.3f, //% NV
                0.3f, //PH
                0.5f, //Adhesion
                0.7f, //MP_Deshabasto
                0.7f, //MP_Contratipo
                0.7f, //MP_Agregados
                0.6f, //Observ
                1.3f  //Comentarios
            };
            
            PdfPTable table = new PdfPTable(widths);
            PdfPCell cell;

            table.setKeepTogether(false);
            table.setHeaderRows(1);

            // Encabezado de Celda
            cell = new PdfPCell(new Paragraph("Cant. K.",headerFont));
            cell.setUseAscender(true);
            cell.setHorizontalAlignment(Element.ALIGN_LEFT);
            cell.setUseDescender(true);
            cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            cell.setBackgroundColor(BaseColor.BLACK);
            table.addCell(cell);

            cell = new PdfPCell(new Paragraph("Cant. L.",headerFont));
            cell.setUseAscender(true);
            cell.setHorizontalAlignment(Element.ALIGN_LEFT);
            cell.setUseDescender(true);
            cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            cell.setBackgroundColor(BaseColor.BLACK);
            table.addCell(cell);

            cell = new PdfPCell(new Paragraph("Folio",headerFont));
            cell.setUseAscender(true);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setUseDescender(true);
            cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            cell.setBackgroundColor(BaseColor.BLACK);
            table.addCell(cell);

            cell = new PdfPCell(new Paragraph("Fecha",headerFont));
            cell.setUseAscender(true);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setUseDescender(true);
            cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            cell.setBackgroundColor(BaseColor.BLACK);
            table.addCell(cell);

            cell = new PdfPCell(new Paragraph("Fineza",headerFont));
            cell.setUseAscender(true);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setUseDescender(true);
            cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            cell.setBackgroundColor(BaseColor.BLACK);
            table.addCell(cell);

            cell = new PdfPCell(new Paragraph("Viscosidad",headerFont));
            cell.setUseAscender(true);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setUseDescender(true);
            cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            cell.setBackgroundColor(BaseColor.BLACK);
            table.addCell(cell);

            cell = new PdfPCell(new Paragraph("Densidad",headerFont));
            cell.setUseAscender(true);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setUseDescender(true);
            cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            cell.setBackgroundColor(BaseColor.BLACK);
            table.addCell(cell);

            cell = new PdfPCell(new Paragraph("PC %",headerFont));
            cell.setUseAscender(true);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setUseDescender(true);
            cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            cell.setBackgroundColor(BaseColor.BLACK);
            table.addCell(cell);

            cell = new PdfPCell(new Paragraph("DE",headerFont));
            cell.setUseAscender(true);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setUseDescender(true);
            cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            cell.setBackgroundColor(BaseColor.BLACK);
            table.addCell(cell);
            
            cell = new PdfPCell(new Paragraph("Brillo",headerFont));
            cell.setUseAscender(true);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setUseDescender(true);
            cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            cell.setBackgroundColor(BaseColor.BLACK);
            table.addCell(cell);
            
            cell = new PdfPCell(new Paragraph("Dureza",headerFont));
            cell.setUseAscender(true);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setUseDescender(true);
            cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            cell.setBackgroundColor(BaseColor.BLACK);
            table.addCell(cell);
            
            cell = new PdfPCell(new Paragraph("% N.V.",headerFont));
            cell.setUseAscender(true);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setUseDescender(true);
            cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            cell.setBackgroundColor(BaseColor.BLACK);
            table.addCell(cell);
            
            cell = new PdfPCell(new Paragraph("PH",headerFont));
            cell.setUseAscender(true);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setUseDescender(true);
            cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            cell.setBackgroundColor(BaseColor.BLACK);
            table.addCell(cell);
            
            cell = new PdfPCell(new Paragraph("Adhesion",headerFont));
            cell.setUseAscender(true);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setUseDescender(true);
            cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            cell.setBackgroundColor(BaseColor.BLACK);
            table.addCell(cell);
            
            cell = new PdfPCell(new Paragraph("MP Deshabasto",headerFont));
            cell.setUseAscender(true);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setUseDescender(true);
            cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            cell.setBackgroundColor(BaseColor.BLACK);
            table.addCell(cell);
            
            cell = new PdfPCell(new Paragraph("MP Contratipo",headerFont));
            cell.setUseAscender(true);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setUseDescender(true);
            cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            cell.setBackgroundColor(BaseColor.BLACK);
            table.addCell(cell);
            
            cell = new PdfPCell(new Paragraph("MP Agregados",headerFont));
            cell.setUseAscender(true);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setUseDescender(true);
            cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            cell.setBackgroundColor(BaseColor.BLACK);
            table.addCell(cell);
            
            cell = new PdfPCell(new Paragraph("Observ",headerFont));
            cell.setUseAscender(true);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setUseDescender(true);
            cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            cell.setBackgroundColor(BaseColor.BLACK);
            table.addCell(cell);
            
            cell = new PdfPCell(new Paragraph("Comentarios",headerFont));
            cell.setUseAscender(true);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setUseDescender(true);
            cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            cell.setBackgroundColor(BaseColor.BLACK);
            table.addCell(cell);
            
            Integer id_pro=0;
            
            if(Datos_Reporte_Calidad.size() > 0){
                for (int x=0; x<=Datos_Reporte_Calidad.size()-1;x++){
                    HashMap<String,String> registro = Datos_Reporte_Calidad.get(x);
                    
                    if(id_pro != Integer.parseInt(registro.get("id"))){
                        cell= new PdfPCell(new Paragraph(registro.get("codigo"),smallFont));
                        cell.setHorizontalAlignment (Element.ALIGN_LEFT);
                        cell.setBorder(0);
                        cell.setColspan(2);
                        table.addCell(cell);

                        cell= new PdfPCell(new Paragraph(registro.get("descripcion"),smallFont));
                        cell.setHorizontalAlignment (Element.ALIGN_LEFT);
                        cell.setColspan(17);
                        cell.setBorder(0);
                        table.addCell(cell);
                        
                        id_pro = Integer.parseInt(registro.get("id"));
                    
                    }
                    

                    
                    
                    cell= new PdfPCell(new Paragraph(registro.get("cantk"),smallFont));
                    cell.setHorizontalAlignment (Element.ALIGN_RIGHT);
                    cell.setBorder(0);
                    table.addCell(cell);
                    
                    cell= new PdfPCell(new Paragraph(registro.get("cantl"),smallFont));
                    cell.setHorizontalAlignment (Element.ALIGN_RIGHT);
                    cell.setBorder(0);
                    table.addCell(cell);
                    
                    cell= new PdfPCell(new Paragraph(registro.get("lote"),smallFont));
                    cell.setHorizontalAlignment (Element.ALIGN_CENTER);
                    cell.setBorder(0);
                    table.addCell(cell);
                    
                    cell= new PdfPCell(new Paragraph(registro.get("fecha"),smallFont));
                    cell.setHorizontalAlignment (Element.ALIGN_CENTER);
                    cell.setBorder(0);
                    table.addCell(cell);
                    
                    cell= new PdfPCell(new Paragraph(registro.get("fineza"),smallFont));
                    cell.setHorizontalAlignment (Element.ALIGN_CENTER);
                    cell.setBorder(0);
                    table.addCell(cell);
                    
                    cell= new PdfPCell(new Paragraph(registro.get("viscosidad"),smallFont));
                    cell.setHorizontalAlignment (Element.ALIGN_CENTER);
                    cell.setBorder(0);
                    table.addCell(cell);
                    
                    cell= new PdfPCell(new Paragraph(registro.get("densidad"),smallFont));
                    cell.setHorizontalAlignment (Element.ALIGN_CENTER);
                    cell.setBorder(0);
                    table.addCell(cell);
                    
                    cell= new PdfPCell(new Paragraph(registro.get("pc"),smallFont));
                    cell.setHorizontalAlignment (Element.ALIGN_CENTER);
                    cell.setBorder(0);
                    table.addCell(cell);

                    cell= new PdfPCell(new Paragraph(registro.get("de"),smallFont));
                    cell.setHorizontalAlignment (Element.ALIGN_CENTER);
                    cell.setBorder(0);
                    table.addCell(cell);
                    
                    cell= new PdfPCell(new Paragraph(registro.get("brillo"),smallFont));
                    cell.setHorizontalAlignment (Element.ALIGN_CENTER);
                    cell.setBorder(0);
                    table.addCell(cell);
                    
                    cell= new PdfPCell(new Paragraph(registro.get("dureza"),smallFont));
                    cell.setHorizontalAlignment (Element.ALIGN_CENTER);
                    cell.setBorder(0);
                    table.addCell(cell);
                    
                    cell= new PdfPCell(new Paragraph(registro.get("nv"),smallFont));
                    cell.setHorizontalAlignment (Element.ALIGN_CENTER);
                    cell.setBorder(0);
                    table.addCell(cell);
                    
                    cell= new PdfPCell(new Paragraph(registro.get("ph"),smallFont));
                    cell.setHorizontalAlignment (Element.ALIGN_CENTER);
                    cell.setBorder(0);
                    table.addCell(cell);
                    
                    cell= new PdfPCell(new Paragraph(registro.get("adhesion"),smallFont));
                    cell.setHorizontalAlignment (Element.ALIGN_CENTER);
                    cell.setBorder(0);
                    table.addCell(cell);
                    
                    cell= new PdfPCell(new Paragraph(registro.get("mp_deshabasto"),smallFont));
                    cell.setHorizontalAlignment (Element.ALIGN_CENTER);
                    cell.setVerticalAlignment(Element.ALIGN_CENTER);
                    cell.setBorder(0);
                    table.addCell(cell);
                    
                    cell= new PdfPCell(new Paragraph(registro.get("mp_contratipo"),smallFont));
                    cell.setHorizontalAlignment (Element.ALIGN_CENTER);
                    cell.setVerticalAlignment(Element.ALIGN_CENTER);
                    cell.setBorder(0);
                    table.addCell(cell);
                    
                    cell= new PdfPCell(new Paragraph(registro.get("mp_agregados"),smallFont));
                    cell.setHorizontalAlignment (Element.ALIGN_CENTER);
                    cell.setVerticalAlignment(Element.ALIGN_CENTER);
                    cell.setBorder(0);
                    table.addCell(cell);
                    
                    cell= new PdfPCell(new Paragraph(registro.get("observ"),smallFont));
                    cell.setHorizontalAlignment (Element.ALIGN_LEFT);
                    cell.setVerticalAlignment(Element.ALIGN_CENTER);
                    cell.setBorder(0);
                    table.addCell(cell);
                    
                    cell= new PdfPCell(new Paragraph(registro.get("comentarios"),smallFont));
                    cell.setHorizontalAlignment (Element.ALIGN_LEFT);
                    cell.setVerticalAlignment(Element.ALIGN_CENTER);
                    cell.setBorder(0);
                    table.addCell(cell);

                }
            }else{
                cell= new PdfPCell(new Paragraph("Esta consulta no genero ningun Resultado",smallFont));
                cell.setHorizontalAlignment (Element.ALIGN_LEFT);
                //cell.setBorder(0);
                cell.setColspan(8);
                cell.setFixedHeight(18);
                table.addCell(cell);
            }

            doc.add(table);
            
            
            doc.close();

        } catch (FileNotFoundException ex) {
            Logger.getLogger(PdfDepositos.class.getName()).log(Level.SEVERE, null, ex);
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
   };//termina clase HeaderFooter
    

}
