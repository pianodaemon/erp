package com.agnux.kemikal.reportes;

import com.agnux.common.helpers.StringHelper;
import com.agnux.common.helpers.TimeHelper;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.ExceptionConverter;
import com.itextpdf.text.Font;
import com.itextpdf.text.Image;
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
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;


public class PdfReporteAplicacionPago {
    public PdfReporteAplicacionPago(String fileout, String ruta_imagen, String razon_soc_empresa,  ArrayList<HashMap<String, String>> datos_header, ArrayList<HashMap<String, String>> listaPagos) throws DocumentException {
        String simbolo_moneda="";
        String razon_cliente="";
        String no_transaccion="";
        String fecha_deposito="";
        //String monto_total_pago = "0.0";
        Double suma_montos_aplicados_en_mn=0.0;
        
                    
        
        String fecha_actual = TimeHelper.getFechaActualYMD();
        
        String[] fi = fecha_actual.split("-");
        String periodo_reporte = "Generado el día  "+fi[2]+"/"+fi[1]+"/"+fi[0];
        
        try {
            
            //tipos de letras (font's)
            Font smallsmall = new Font(Font.getFamily("ARIAL"),13,Font.NORMAL);
            Font smallBoldFont = new Font(Font.getFamily("ARIAL"),8,Font.BOLD,BaseColor.BLACK);
            Font headerFont = new Font(Font.getFamily("ARIAL"),8,Font.BOLD,BaseColor.WHITE);
            Font smallBoldFont2 = new Font(Font.getFamily("ARIAL"),9,Font.BOLD,BaseColor.BLACK);
            
            Font largeBoldFont = new Font(Font.FontFamily.HELVETICA,10,Font.BOLD,BaseColor.BLACK);
            Font smallFont = new Font(Font.FontFamily.HELVETICA,8,Font.NORMAL,BaseColor.BLACK);
            
            HeaderFooter event = new HeaderFooter(razon_soc_empresa,periodo_reporte);
            Document doc = new Document(PageSize.LETTER,-50,-50,60,30);
            doc.addCreator("gpmarsan@gmail.com");
            PdfWriter writer = PdfWriter.getInstance(doc, new FileOutputStream(fileout));
            writer.setPageEvent(event);
            
            
            doc.open();
            
            //aqui inicia tabla para datos  del Pago 
            float [] widths2 = {1.7f,5,0.5f,1.5f,3};
            PdfPTable tableHelper = new PdfPTable(widths2);
            PdfPCell cell1;
            
            //fila 1
            cell1 = new PdfPCell(new Paragraph("No. de Transacción:",smallBoldFont));
            cell1.setBorderWidthBottom(0);
            cell1.setBorderWidthTop(0);
            cell1.setBorderWidthRight(0);
            cell1.setBorderWidthLeft(0);
            tableHelper.addCell(cell1);
            
            cell1 = new PdfPCell(new Paragraph(datos_header.get(0).get("numero_transaccion"),smallFont));
            cell1.setBorderWidthBottom(1);
            cell1.setBorderWidthTop(0);
            cell1.setBorderWidthRight(0);
            cell1.setBorderWidthLeft(0);
            tableHelper.addCell(cell1);
            
            cell1 = new PdfPCell(new Paragraph("",smallFont));
            cell1.setBorderWidthBottom(0);
            cell1.setBorderWidthTop(0);
            cell1.setBorderWidthRight(0);
            cell1.setBorderWidthLeft(0);
            tableHelper.addCell(cell1);
            
            cell1 = new PdfPCell(new Paragraph("Banco Depósito:",smallBoldFont));
            cell1.setBorderWidthBottom(0);
            cell1.setBorderWidthTop(0);
            cell1.setBorderWidthRight(0);
            cell1.setBorderWidthLeft(0);
            tableHelper.addCell(cell1);
            
            cell1 = new PdfPCell(new Paragraph(datos_header.get(0).get("banco_deposito"),smallFont));
            cell1.setBorderWidthBottom(1);
            cell1.setBorderWidthTop(0);
            cell1.setBorderWidthRight(0);
            cell1.setBorderWidthLeft(0);
            tableHelper.addCell(cell1);
            
            
            //fila 2
            cell1 = new PdfPCell(new Paragraph("Forma de Pago:",smallBoldFont));
            cell1.setBorderWidthBottom(0);
            cell1.setBorderWidthTop(0);
            cell1.setBorderWidthRight(0);
            cell1.setBorderWidthLeft(0);
            tableHelper.addCell(cell1);
            
            cell1 = new PdfPCell(new Paragraph(datos_header.get(0).get("forma_pago").toUpperCase(),smallFont));
            cell1.setBorderWidthBottom(1);
            cell1.setBorderWidthTop(0);
            cell1.setBorderWidthRight(0);
            cell1.setBorderWidthLeft(0);
            tableHelper.addCell(cell1);
            
            cell1 = new PdfPCell(new Paragraph("",smallFont));
            cell1.setBorderWidthBottom(0);
            cell1.setBorderWidthTop(0);
            cell1.setBorderWidthRight(0);
            cell1.setBorderWidthLeft(0);
            tableHelper.addCell(cell1);
            
            cell1 = new PdfPCell(new Paragraph("Cuenta Depósito:",smallBoldFont));
            cell1.setBorderWidthBottom(0);
            cell1.setBorderWidthTop(0);
            cell1.setBorderWidthRight(0);
            cell1.setBorderWidthLeft(0);
            tableHelper.addCell(cell1);
            
            cell1 = new PdfPCell(new Paragraph(datos_header.get(0).get("no_cuenta_deposito"),smallFont));
            cell1.setBorderWidthBottom(1);
            cell1.setBorderWidthTop(0);
            cell1.setBorderWidthRight(0);
            cell1.setBorderWidthLeft(0);
            tableHelper.addCell(cell1);
            
            
            //fila 3
            cell1 = new PdfPCell(new Paragraph("Cheque/Referencia:",smallBoldFont));
            cell1.setBorderWidthBottom(0);
            cell1.setBorderWidthTop(0);
            cell1.setBorderWidthRight(0);
            cell1.setBorderWidthLeft(0);
            tableHelper.addCell(cell1);
            
            cell1 = new PdfPCell(new Paragraph(datos_header.get(0).get("cheque_referencia"),smallFont));
            cell1.setBorderWidthBottom(1);
            cell1.setBorderWidthTop(0);
            cell1.setBorderWidthRight(0);
            cell1.setBorderWidthLeft(0);
            tableHelper.addCell(cell1);
            
            cell1 = new PdfPCell(new Paragraph("",smallFont));
            cell1.setBorderWidthBottom(0);
            cell1.setBorderWidthTop(0);
            cell1.setBorderWidthRight(0);
            cell1.setBorderWidthLeft(0);
            tableHelper.addCell(cell1);
            
            cell1 = new PdfPCell(new Paragraph("Fecha Depósito:",smallBoldFont));
            cell1.setBorderWidthBottom(0);
            cell1.setBorderWidthTop(0);
            cell1.setBorderWidthRight(0);
            cell1.setBorderWidthLeft(0);
            tableHelper.addCell(cell1);
            
            cell1 = new PdfPCell(new Paragraph(datos_header.get(0).get("fecha_deposito"),smallFont));
            cell1.setBorderWidthBottom(1);
            cell1.setBorderWidthTop(0);
            cell1.setBorderWidthRight(0);
            cell1.setBorderWidthLeft(0);
            tableHelper.addCell(cell1);
            
            
            
            //fila 4
            cell1 = new PdfPCell(new Paragraph("Moneda Pago:",smallBoldFont));
            cell1.setBorderWidthBottom(0);
            cell1.setBorderWidthTop(0);
            cell1.setBorderWidthRight(0);
            cell1.setBorderWidthLeft(0);
            tableHelper.addCell(cell1);
            
            cell1 = new PdfPCell(new Paragraph(datos_header.get(0).get("moneda_pago"),smallFont));
            cell1.setBorderWidthBottom(1);
            cell1.setBorderWidthTop(0);
            cell1.setBorderWidthRight(0);
            cell1.setBorderWidthLeft(0);
            tableHelper.addCell(cell1);
            
            cell1 = new PdfPCell(new Paragraph("",smallFont));
            cell1.setBorderWidthBottom(0);
            cell1.setBorderWidthTop(0);
            cell1.setBorderWidthRight(0);
            cell1.setBorderWidthLeft(0);
            tableHelper.addCell(cell1);
            
            cell1 = new PdfPCell(new Paragraph("Monto Pago:",smallBoldFont));
            cell1.setBorderWidthBottom(0);
            cell1.setBorderWidthTop(0);
            cell1.setBorderWidthRight(0);
            cell1.setBorderWidthLeft(0);
            tableHelper.addCell(cell1);
            
            cell1 = new PdfPCell(new Paragraph(datos_header.get(0).get("simbolo_moneda_pago") + "  "+ StringHelper.AgregaComas(datos_header.get(0).get("monto_pago")),smallFont));
            cell1.setBorderWidthBottom(1);
            cell1.setBorderWidthTop(0);
            cell1.setBorderWidthRight(0);
            cell1.setBorderWidthLeft(0);
            tableHelper.addCell(cell1);
            
            
            
            //fila 5
            cell1 = new PdfPCell(new Paragraph("Cliente:",smallBoldFont));
            cell1.setBorderWidthBottom(0);
            cell1.setBorderWidthTop(0);
            cell1.setBorderWidthRight(0);
            cell1.setBorderWidthLeft(0);
            tableHelper.addCell(cell1);
            
            cell1 = new PdfPCell(new Paragraph(datos_header.get(0).get("cliente"),smallFont));
            cell1.setBorderWidthBottom(1);
            cell1.setBorderWidthTop(0);
            cell1.setBorderWidthRight(0);
            cell1.setBorderWidthLeft(0);
            tableHelper.addCell(cell1);
            
            cell1 = new PdfPCell(new Paragraph("",smallFont));
            cell1.setBorderWidthBottom(0);
            cell1.setBorderWidthTop(0);
            cell1.setBorderWidthRight(0);
            cell1.setBorderWidthLeft(0);
            tableHelper.addCell(cell1);
            
            cell1 = new PdfPCell(new Paragraph("Tipo de Cambio:",smallBoldFont));
            cell1.setBorderWidthBottom(0);
            cell1.setBorderWidthTop(0);
            cell1.setBorderWidthRight(0);
            cell1.setBorderWidthLeft(0);
            tableHelper.addCell(cell1);
            
            cell1 = new PdfPCell(new Paragraph(datos_header.get(0).get("tipo_cambio"),smallFont));
            cell1.setBorderWidthBottom(1);
            cell1.setBorderWidthTop(0);
            cell1.setBorderWidthRight(0);
            cell1.setBorderWidthLeft(0);
            tableHelper.addCell(cell1);
            
            tableHelper.setSpacingAfter(10f);
            
            doc.add(tableHelper);
             
            
            
            
            
            //float [] widths = {3f, 3f, 3f, 3f, 3f, 3f, 4f};
            float [] widths = {2.5f,2f, 2f, 1f, 2.5f, 2f};
            PdfPTable table = new PdfPTable(widths);
            PdfPCell cell;
            
            table.setKeepTogether(false);
            table.setHeaderRows(1);
            
            
            // Encabezado de Celda
            cell = new PdfPCell(new Paragraph("Factura",headerFont));
            cell.setUseAscender(true);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setUseDescender(true);
            cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            cell.setBackgroundColor(BaseColor.BLACK);
            cell.setFixedHeight(13);
            table.addCell(cell);
            
            cell = new PdfPCell(new Paragraph("Moneda Fac.",headerFont));
            cell.setUseAscender(true);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setUseDescender(true);
            cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            cell.setBackgroundColor(BaseColor.BLACK);
            cell.setFixedHeight(13);
            table.addCell(cell);
            
            cell = new PdfPCell(new Paragraph("Fecha Fac.",headerFont));
            cell.setUseAscender(true);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setUseDescender(true);
            cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            cell.setBackgroundColor(BaseColor.BLACK);
            cell.setFixedHeight(13);
            table.addCell(cell);
            
            cell = new PdfPCell(new Paragraph("Monto Aplicado",headerFont));
            cell.setUseAscender(true);
            cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            cell.setUseDescender(true);
            cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            cell.setBackgroundColor(BaseColor.BLACK);
            cell.setFixedHeight(13);
            cell.setColspan(2);
            table.addCell(cell);
            
            cell = new PdfPCell(new Paragraph("Estado",headerFont));
            cell.setUseAscender(true);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setUseDescender(true);
            cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            cell.setBackgroundColor(BaseColor.BLACK);
            cell.setFixedHeight(13);
            table.addCell(cell);
            
            
            if(listaPagos.size() > 0){
                
                for (int x=0; x<=listaPagos.size()-1;x++){
                    HashMap<String,String> registro = listaPagos.get(x);
                    
                    //sumar cantidades
                    suma_montos_aplicados_en_mn += Double.parseDouble(registro.get("monto_aplicado_mn"));
                    
                    simbolo_moneda =registro.get("simbolo_moneda_fac");
                    
                    /*
                    moneda_fac_id
                    moneda_fac
                    simbolo_moneda_fac
                    serie_folio
                    fecha_factura
                    monto_aplicado
                    monto_aplicado_mn
                    estado
                    */     
                    
                    
                    //columna Factura
                    cell= new PdfPCell(new Paragraph(registro.get("serie_folio"),smallFont));
                    cell.setHorizontalAlignment (Element.ALIGN_CENTER);
                    cell.setBorder(0);
                    table.addCell(cell);
                    
                    //columna Factura
                    cell= new PdfPCell(new Paragraph(registro.get("moneda_fac"),smallFont));
                    cell.setHorizontalAlignment (Element.ALIGN_CENTER);
                    cell.setBorder(0);
                    table.addCell(cell);
                    
                    //Fecha factura
                    cell= new PdfPCell(new Paragraph(registro.get("fecha_factura"),smallFont));
                    cell.setHorizontalAlignment (Element.ALIGN_CENTER);
                    cell.setBorder(0);
                    table.addCell(cell);
                    
                    //simbolo moneda
                    cell= new PdfPCell(new Paragraph(simbolo_moneda,smallFont));
                    cell.setHorizontalAlignment (Element.ALIGN_RIGHT);
                    cell.setBorder(0);
                    table.addCell(cell);
                    
                    
                    //monto aplicado
                    cell= new PdfPCell(new Paragraph(StringHelper.AgregaComas(registro.get("monto_aplicado")),smallFont));
                    cell.setHorizontalAlignment (Element.ALIGN_RIGHT);
                    cell.setBorder(0);
                    table.addCell(cell);

                    //columna Estado
                    cell= new PdfPCell(new Paragraph(registro.get("estado"),smallFont));
                    cell.setHorizontalAlignment (Element.ALIGN_CENTER);
                    cell.setBorder(0);
                    table.addCell(cell);
                }
                
                //fila vacia para separar los totales
                cell= new PdfPCell(new Paragraph("",smallBoldFont));
                cell.setHorizontalAlignment (Element.ALIGN_RIGHT);
                cell.setBorder(1);
                cell.setColspan(6);
                cell.setFixedHeight(5);
                table.addCell(cell);
                
                
                
                //simbolo_moneda="$";
                cell= new PdfPCell(new Paragraph("Total Pago",smallBoldFont));
                cell.setHorizontalAlignment (Element.ALIGN_RIGHT);
                cell.setBorder(0);
                cell.setColspan(3);
                table.addCell(cell);
                
                cell= new PdfPCell(new Paragraph(datos_header.get(0).get("simbolo_moneda_pago"),smallBoldFont));
                cell.setHorizontalAlignment (Element.ALIGN_RIGHT);
                cell.setBorder(0);
                table.addCell(cell);
                
                cell= new PdfPCell(new Paragraph(StringHelper.AgregaComas(StringHelper.roundDouble(suma_montos_aplicados_en_mn,2)),smallBoldFont));
                cell.setHorizontalAlignment (Element.ALIGN_RIGHT);
                cell.setBorder(0);
                table.addCell(cell);
                
                
                cell= new PdfPCell(new Paragraph("",smallBoldFont));
                cell.setHorizontalAlignment (Element.ALIGN_RIGHT);
                cell.setBorder(0);
                table.addCell(cell);
                
                
            }else{
                cell= new PdfPCell(new Paragraph("No hay datos que mostrar",smallFont));
                cell.setHorizontalAlignment (Element.ALIGN_LEFT);
                cell.setBorder(0);
                cell.setColspan(6);
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
        public Image headerImage;
        protected PdfTemplate total;       
        protected BaseFont helv;  
        protected PdfContentByte cb;
        protected PdfContentByte cb2;
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
                /*
                headerImage = Image.getInstance(PdfDepositos.ruta_imagen);
                headerImage.scalePercent(50);
                */
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
            ColumnText.showTextAligned(writer.getDirectContent(),Element.ALIGN_CENTER, new Phrase("Reporte de Aplicación de Pagos de Clientes",largeBoldFont),document.getPageSize().getWidth()/2, document.getPageSize().getTop()-38, 0);
            SimpleDateFormat formato = new SimpleDateFormat("' a las' HH:mm:ss 'hrs.'");
            String hora_generacion = formato.format(new Date());
            
            ColumnText.showTextAligned(writer.getDirectContent(),Element.ALIGN_CENTER, new Phrase(this.getPeriodo()+" "+hora_generacion, largeFont),document.getPageSize().getWidth()/2, document.getPageSize().getTop()-50, 0);
            
            cb = writer.getDirectContent();
            float textBase = document.bottom() - 20;
            
            //texto inferior izquieda pie de pagina
            String text_left = "";
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
            String text_right = "";
            float textRightSize = helv.getWidthPoint(text_right, 7);
            float pos_text_right = document.getPageSize().getWidth()-textRightSize - 40;
            cb.beginText();
            cb.setFontAndSize(helv, 7);
            cb.setTextMatrix(pos_text_right, textBase);
            cb.showText(text_right);
            cb.endText();
            
            /*
            cb = writer.getDirectContent();  
            //cb.saveState();  
            String text = "Página " + writer.getPageNumber() + " de ";  
            float textBase = document.bottom() - 20;
            float adjust = helv.getWidthPoint("0", 150);  
            cb.beginText();  
            cb.setFontAndSize(helv, 7);  
            cb.setTextMatrix(document.right() - 128, textBase);  //definir la posicion de text
            cb.showText(text);  
            
            cb.endText();
            cb.addTemplate(total, document.right() - adjust , textBase);  //definir la posicion del total de paginas
            //cb.restoreState();
             * 
             */
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
