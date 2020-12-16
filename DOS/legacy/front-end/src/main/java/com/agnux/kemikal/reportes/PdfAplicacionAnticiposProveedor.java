/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

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

/**
 *
 * @author agnux
 */
public class PdfAplicacionAnticiposProveedor {
    public PdfAplicacionAnticiposProveedor(String fileout, String ruta_imagen, String razon_soc_empresa,  ArrayList<HashMap<String, String>> listaPagos) throws DocumentException {
        String simbolo_moneda="";
        String razon_proveedor="";
        String no_transaccion="";
        String fecha_pago="";
        String simbolo_moneda_pago = "";
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
            Document doc = new Document(PageSize.LETTER.rotate(),-50,-50,60,30);
            doc.addCreator("gpmarsan@gmail.com");
            PdfWriter writer = PdfWriter.getInstance(doc, new FileOutputStream(fileout));
            writer.setPageEvent(event);
            
            
            doc.open();
            //float [] widths = {3f, 3f, 3f, 3f, 3f, 3f, 4f};
            float [] widths = {2f, 2.5f, 4.5f, 2.5f, 3f, 1f, 2.5f};
            PdfPTable table = new PdfPTable(widths);
            PdfPCell cell;
            
            table.setKeepTogether(false);
            table.setHeaderRows(3);
            
            if(listaPagos.size() > 0){
                razon_proveedor = listaPagos.get(0).get("proveedor");
                no_transaccion = listaPagos.get(0).get("folio");
                fecha_pago = listaPagos.get(0).get("fecha_anticipo");
                simbolo_moneda_pago = listaPagos.get(0).get("simbolo_moneda_anticipo");
            }
            
            cell= new PdfPCell(new Paragraph("Proveedor: "+razon_proveedor,smallBoldFont));
            cell.setHorizontalAlignment (Element.ALIGN_LEFT);
            cell.setBorder(0);
            cell.setColspan(4);
            table.addCell(cell);
            
            cell= new PdfPCell(new Paragraph("Número de Transacción: "+no_transaccion,smallBoldFont));
            cell.setHorizontalAlignment (Element.ALIGN_LEFT);
            cell.setBorder(0);
            cell.setColspan(2);
            table.addCell(cell);
            
            cell= new PdfPCell(new Paragraph("Fecha Pago: "+fecha_pago,smallBoldFont));
            cell.setHorizontalAlignment (Element.ALIGN_RIGHT);
            cell.setBorder(0);
            cell.setColspan(3);
            table.addCell(cell);
            
            
            //fila vacia
            cell= new PdfPCell(new Paragraph("",smallBoldFont));
            cell.setHorizontalAlignment (Element.ALIGN_RIGHT);
            cell.setBorder(0);
            cell.setColspan(9);
            table.addCell(cell);
            
            
            
            // Encabezado de Celda
            cell = new PdfPCell(new Paragraph("Moneda",headerFont));
            cell.setUseAscender(true);
            cell.setHorizontalAlignment(Element.ALIGN_LEFT);
            cell.setUseDescender(true);
            cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            cell.setBackgroundColor(BaseColor.BLACK);
            cell.setFixedHeight(13);
            table.addCell(cell);
            
            
            cell = new PdfPCell(new Paragraph("Tipo de cambio",headerFont));
            cell.setUseAscender(true);
            cell.setHorizontalAlignment(Element.ALIGN_LEFT);
            cell.setUseDescender(true);
            cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            cell.setBackgroundColor(BaseColor.BLACK);
            cell.setFixedHeight(13);
            table.addCell(cell);
            
            
            cell = new PdfPCell(new Paragraph("Banco",headerFont));
            cell.setUseAscender(true);
            cell.setHorizontalAlignment(Element.ALIGN_LEFT);
            cell.setUseDescender(true);
            cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            cell.setBackgroundColor(BaseColor.BLACK);
            cell.setFixedHeight(13);
            table.addCell(cell);
            
            
            cell = new PdfPCell(new Paragraph("Forma de Pago",headerFont));
            cell.setUseAscender(true);
            cell.setHorizontalAlignment(Element.ALIGN_LEFT);
            cell.setUseDescender(true);
            cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            cell.setBackgroundColor(BaseColor.BLACK);
            cell.setFixedHeight(13);
            table.addCell(cell);
            
            
            cell = new PdfPCell(new Paragraph("# Cheque/Referencia",headerFont));
            cell.setUseAscender(true);
            cell.setHorizontalAlignment(Element.ALIGN_LEFT);
            cell.setUseDescender(true);
            cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            cell.setBackgroundColor(BaseColor.BLACK);
            cell.setFixedHeight(13);
            table.addCell(cell);
            
            
            
            /*cell = new PdfPCell(new Paragraph("Factura",headerFont));
            cell.setUseAscender(true);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setUseDescender(true);
            cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            cell.setBackgroundColor(BaseColor.BLACK);
            cell.setFixedHeight(13);
            table.addCell(cell);
            
            cell = new PdfPCell(new Paragraph("Fecha Factura",headerFont));
            cell.setUseAscender(true);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setUseDescender(true);
            cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            cell.setBackgroundColor(BaseColor.BLACK);
            cell.setFixedHeight(13);
            table.addCell(cell);*/
            
            cell = new PdfPCell(new Paragraph("Anticipo",headerFont));
            cell.setUseAscender(true);
            cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            cell.setUseDescender(true);
            cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            cell.setBackgroundColor(BaseColor.BLACK);
            cell.setFixedHeight(13);
            cell.setColspan(2);
            table.addCell(cell);
            
            
            if(listaPagos.size() > 0){
                
                for (int x=0; x<=listaPagos.size()-1;x++){
                    HashMap<String,String> registro = listaPagos.get(x);
                    
                    //sumar cantidades
                    suma_montos_aplicados_en_mn += Double.parseDouble(registro.get("monto_anticipo_proveedor"));
                    simbolo_moneda = registro.get("simbolo_moneda_anticipo");
                    
                    //columna moneda
                    cell= new PdfPCell(new Paragraph(registro.get("moneda_anticipo"),smallFont));
                    cell.setHorizontalAlignment (Element.ALIGN_LEFT);
                    cell.setBorder(0);
                    table.addCell(cell);
                    
                    //columna tipo de cambio
                    cell= new PdfPCell(new Paragraph(registro.get("tipo_cambio"),smallFont));
                    cell.setHorizontalAlignment (Element.ALIGN_LEFT);
                    cell.setBorder(0);
                    table.addCell(cell);
                    
                    //columna Banco
                    cell= new PdfPCell(new Paragraph(registro.get("banco"),smallFont));
                    cell.setHorizontalAlignment (Element.ALIGN_LEFT);
                    cell.setBorder(0);
                    table.addCell(cell);

                    //columna Forma de Pago
                    cell= new PdfPCell(new Paragraph(registro.get("forma_pago"),smallFont));
                    cell.setHorizontalAlignment (Element.ALIGN_LEFT);
                    cell.setBorder(0);
                    table.addCell(cell);
                    
                    //columna Cheque/referencia
                    cell= new PdfPCell(new Paragraph(registro.get(""),smallFont));
                    cell.setHorizontalAlignment (Element.ALIGN_LEFT);
                    cell.setBorder(0);
                    table.addCell(cell);
                    
                    /*//columna Factura
                    cell= new PdfPCell(new Paragraph(registro.get("serie_folio"),smallFont));
                    cell.setHorizontalAlignment (Element.ALIGN_CENTER);
                    cell.setBorder(0);
                    table.addCell(cell);
                    
                    //Fecha factura
                    cell= new PdfPCell(new Paragraph(registro.get("fecha_factura"),smallFont));
                    cell.setHorizontalAlignment (Element.ALIGN_CENTER);
                    cell.setBorder(0);
                    table.addCell(cell);*/
                    
                    //simbolo moneda
                    cell= new PdfPCell(new Paragraph(simbolo_moneda,smallFont));
                    cell.setHorizontalAlignment (Element.ALIGN_RIGHT);
                    cell.setBorder(0);
                    table.addCell(cell);
                    
                    //monto aplicado
                    cell= new PdfPCell(new Paragraph(StringHelper.AgregaComas(registro.get("monto_anticipo")),smallFont));
                    cell.setHorizontalAlignment (Element.ALIGN_RIGHT);
                    cell.setBorder(0);
                    table.addCell(cell);
                }
                
                //fila vacia para separar los totales
                cell= new PdfPCell(new Paragraph("",smallBoldFont));
                cell.setHorizontalAlignment (Element.ALIGN_RIGHT);
                cell.setBorder(1);
                cell.setColspan(5);
                cell.setFixedHeight(15);
                table.addCell(cell);
                
                cell= new PdfPCell(new Paragraph("",smallBoldFont));
                cell.setHorizontalAlignment (Element.ALIGN_RIGHT);
                cell.setBorder(1);
                cell.setColspan(2);
                cell.setFixedHeight(15);
                table.addCell(cell);
                
                //simbolo_moneda="$";
                cell= new PdfPCell(new Paragraph("Total Anticipo",smallBoldFont));
                cell.setHorizontalAlignment (Element.ALIGN_RIGHT);
                cell.setBorder(0);
                cell.setColspan(5);
                table.addCell(cell);
                
                cell= new PdfPCell(new Paragraph(simbolo_moneda_pago,smallBoldFont));
                cell.setHorizontalAlignment (Element.ALIGN_RIGHT);
                cell.setBorder(0);
                table.addCell(cell);
                
                cell= new PdfPCell(new Paragraph(StringHelper.AgregaComas(StringHelper.roundDouble(suma_montos_aplicados_en_mn,2)),smallBoldFont));
                cell.setHorizontalAlignment (Element.ALIGN_RIGHT);
                cell.setBorder(0);
                table.addCell(cell);
                
            }else{
                cell= new PdfPCell(new Paragraph("No hay datos que mostrar",smallFont));
                cell.setHorizontalAlignment (Element.ALIGN_LEFT);
                cell.setBorder(0);
                cell.setColspan(11);
                cell.setFixedHeight(18);
                table.addCell(cell);
            }
            
            doc.add(table);
            doc.close();
            
        } catch (FileNotFoundException ex) {
            Logger.getLogger(PdfAplicacionAnticiposProveedor.class.getName()).log(Level.SEVERE, null, ex);
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
            ColumnText.showTextAligned(writer.getDirectContent(),Element.ALIGN_CENTER, new Phrase("Reporte de Anticipos a Proveedores",largeBoldFont),document.getPageSize().getWidth()/2, document.getPageSize().getTop()-38, 0);
            SimpleDateFormat formato = new SimpleDateFormat("' a las' HH:mm:ss 'hrs.'");
            String hora_generacion = formato.format(new Date());
            
            ColumnText.showTextAligned(writer.getDirectContent(),Element.ALIGN_CENTER, new Phrase(this.getPeriodo()+" "+hora_generacion, largeFont),document.getPageSize().getWidth()/2, document.getPageSize().getTop()-50, 0);
            
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
