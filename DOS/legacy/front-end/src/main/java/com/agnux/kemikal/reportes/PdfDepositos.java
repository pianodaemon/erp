package com.agnux.kemikal.reportes;


import com.agnux.common.helpers.StringHelper;
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


public final class PdfDepositos {    
    public PdfDepositos(String fileout, String ruta_imagen, String razon_soc_empresa, String fechaInicial, String fechaFinal,ArrayList<HashMap<String, String>> listaDepositos) throws DocumentException {
        String simbolo_moneda="";
        String cta="";
        String nombre_banco="";
        String moneda_cta="";
        int primer_cuenta=0;
        Double suma_efectivo=0.00;
        Double suma_cheque=0.00;
        Double suma_transferencia=0.00;
        Double suma_tarjeta=0.00;
        Double suma_otro=0.00;
        Double suma_total=0.00;
        
        String[] fi = fechaInicial.split("-");
        String[] ff = fechaFinal.split("-");
        String periodo_reporte = "Periodo  del  "+fi[2]+"/"+fi[1]+"/"+fi[0]+"  al  "+ff[2]+"/"+ff[1]+"/"+ff[0];
        
        
        
        try {
            
            //tipos de letras (font's)
            Font smallsmall = new Font(Font.getFamily("ARIAL"),13,Font.NORMAL);
            Font smallBoldFont = new Font(Font.getFamily("ARIAL"),8,Font.BOLD,BaseColor.BLACK);
            Font headerFont = new Font(Font.getFamily("ARIAL"),8,Font.BOLD,BaseColor.WHITE);
            Font smallBoldFont2 = new Font(Font.getFamily("ARIAL"),9,Font.BOLD,BaseColor.BLACK);
            
            Font largeBoldFont = new Font(Font.FontFamily.HELVETICA,10,Font.BOLD,BaseColor.BLACK);
            Font smallFont = new Font(Font.FontFamily.HELVETICA,8,Font.NORMAL,BaseColor.BLACK);
            
            HeaderFooter event = new HeaderFooter(razon_soc_empresa, periodo_reporte);
            Document doc = new Document(PageSize.LETTER,-50,-50,60,30);
            doc.addCreator("gpmarsan@gmail.com");
            PdfWriter writer = PdfWriter.getInstance(doc, new FileOutputStream(fileout));
            writer.setPageEvent(event);
            
            
            doc.open();
            //float [] widths = {3f, 3f, 3f, 3f, 3f, 3f, 4f};
            float [] widths = {3f,1f, 3f,1f, 3f, 1f, 3f, 1f, 3f, 1f, 2.5f, 1f, 3f};
            PdfPTable table = new PdfPTable(widths);
            PdfPCell cell;
            
            table.setKeepTogether(false);
            table.setHeaderRows(1);
            
            
            
            // Encabezado de Celda
            cell = new PdfPCell(new Paragraph("Fecha deposito",headerFont));
            cell.setUseAscender(true);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setUseDescender(true);
            cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            cell.setBackgroundColor(BaseColor.BLACK);
            cell.setFixedHeight(13);
            table.addCell(cell);
            
            
            cell = new PdfPCell(new Paragraph("Efectivo",headerFont));
            cell.setUseAscender(true);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setUseDescender(true);
            cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            cell.setBackgroundColor(BaseColor.BLACK);
            cell.setFixedHeight(13);
            cell.setColspan(2);
            table.addCell(cell);
            
            
            cell = new PdfPCell(new Paragraph("Cheque",headerFont));
            cell.setUseAscender(true);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setUseDescender(true);
            cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            cell.setBackgroundColor(BaseColor.BLACK);
            cell.setFixedHeight(13);
            cell.setColspan(2);
            table.addCell(cell);
                      
            
            cell = new PdfPCell(new Paragraph("Transferencia",headerFont));
            cell.setUseAscender(true);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setUseDescender(true);
            cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            cell.setBackgroundColor(BaseColor.BLACK);
            cell.setFixedHeight(13);
            cell.setColspan(2);
            table.addCell(cell);
            
            
            cell = new PdfPCell(new Paragraph("Tarjeta",headerFont));
            cell.setUseAscender(true);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setUseDescender(true);
            cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            cell.setBackgroundColor(BaseColor.BLACK);
            cell.setFixedHeight(13);
            cell.setColspan(2);
            table.addCell(cell);
            
            
            
            cell = new PdfPCell(new Paragraph("Otro",headerFont));
            cell.setUseAscender(true);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setUseDescender(true);
            cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            cell.setBackgroundColor(BaseColor.BLACK);
            cell.setFixedHeight(13);
            cell.setColspan(2);
            table.addCell(cell);
            
            
            cell = new PdfPCell(new Paragraph("Total",headerFont));
            cell.setUseAscender(true);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setUseDescender(true);
            cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            cell.setBackgroundColor(BaseColor.BLACK);
            cell.setFixedHeight(13);
            cell.setColspan(2);
            table.addCell(cell);   
            
            
            if(listaDepositos.size() > 0){
                for (int x=0; x<=listaDepositos.size()-1;x++){
                    HashMap<String,String> registro = listaDepositos.get(x);
                    if(primer_cuenta==0){
                        cta=registro.get("no_cuenta");
                        nombre_banco=registro.get("banco");
                        moneda_cta=registro.get("moneda_cuenta");
                        cell= new PdfPCell(new Paragraph("Banco: "+nombre_banco +"      Cuenta: "+cta+"     Moneda: "+moneda_cta,smallBoldFont));
                        cell.setHorizontalAlignment (Element.ALIGN_LEFT);
                        cell.setVerticalAlignment(Element.ALIGN_BOTTOM);
                        cell.setBorder(0);
                        cell.setColspan(13);
                        cell.setFixedHeight(18);
                        table.addCell(cell);
                        primer_cuenta=1;
                    }
                    
                    //System.out.println("cta:"+cta +"    No_cta:"+registro.get("no_cuenta"));
                    if(cta.equals(registro.get("no_cuenta"))){
                        
                        //sumar cantidades
                        suma_efectivo += Double.parseDouble(registro.get("efectivo"));
                        suma_cheque += Double.parseDouble(registro.get("cheque"));
                        suma_transferencia += Double.parseDouble(registro.get("transferencia"));
                        suma_tarjeta += Double.parseDouble(registro.get("tarjeta"));
                        suma_otro += Double.parseDouble(registro.get("otro"));
                        suma_total += Double.parseDouble(registro.get("total"));
                        
                        if(registro.get("moneda_cuenta").equals("M.N.")){
                            simbolo_moneda = "$";
                        }
                        if(registro.get("moneda_cuenta").equals("USD")){
                            simbolo_moneda = "USD";
                        }
                        
                        cell= new PdfPCell(new Paragraph(registro.get("fecha_deposito"),smallFont));
                        cell.setHorizontalAlignment (Element.ALIGN_CENTER);
                        cell.setBorder(0);
                        table.addCell(cell);
                        
                        cell= new PdfPCell(new Paragraph(simbolo_moneda,smallFont));
                        cell.setHorizontalAlignment (Element.ALIGN_RIGHT);
                        cell.setBorder(0);
                        table.addCell(cell);
                        
                        cell= new PdfPCell(new Paragraph(StringHelper.AgregaComas(registro.get("efectivo")),smallFont));
                        cell.setHorizontalAlignment (Element.ALIGN_RIGHT);
                        cell.setBorder(0);
                        table.addCell(cell);
                        
                        cell= new PdfPCell(new Paragraph(simbolo_moneda,smallFont));
                        cell.setHorizontalAlignment (Element.ALIGN_RIGHT);
                        cell.setBorder(0);
                        table.addCell(cell);
                        
                        cell= new PdfPCell(new Paragraph(StringHelper.AgregaComas(registro.get("cheque")),smallFont));
                        cell.setHorizontalAlignment (Element.ALIGN_RIGHT);
                        cell.setBorder(0);
                        table.addCell(cell);
                        
                        cell= new PdfPCell(new Paragraph(simbolo_moneda,smallFont));
                        cell.setHorizontalAlignment (Element.ALIGN_RIGHT);
                        cell.setBorder(0);
                        table.addCell(cell);
                        
                        cell= new PdfPCell(new Paragraph(StringHelper.AgregaComas(registro.get("transferencia")),smallFont));
                        cell.setHorizontalAlignment (Element.ALIGN_RIGHT);
                        cell.setBorder(0);
                        table.addCell(cell);
                        
                        cell= new PdfPCell(new Paragraph(simbolo_moneda,smallFont));
                        cell.setHorizontalAlignment (Element.ALIGN_RIGHT);
                        cell.setBorder(0);
                        table.addCell(cell);
                        
                        cell= new PdfPCell(new Paragraph(StringHelper.AgregaComas(registro.get("tarjeta")),smallFont));
                        cell.setHorizontalAlignment (Element.ALIGN_RIGHT);
                        cell.setBorder(0);
                        table.addCell(cell);
                        
                        cell= new PdfPCell(new Paragraph(simbolo_moneda,smallFont));
                        cell.setHorizontalAlignment (Element.ALIGN_RIGHT);
                        cell.setBorder(0);
                        table.addCell(cell);
                        
                        cell= new PdfPCell(new Paragraph(StringHelper.AgregaComas(registro.get("otro")),smallFont));
                        cell.setHorizontalAlignment (Element.ALIGN_RIGHT);
                        cell.setBorder(0);
                        table.addCell(cell);
                        
                        cell= new PdfPCell(new Paragraph(simbolo_moneda,smallFont));
                        cell.setHorizontalAlignment (Element.ALIGN_RIGHT);
                        cell.setBorder(0);
                        table.addCell(cell);
                        
                        cell= new PdfPCell(new Paragraph(StringHelper.AgregaComas(registro.get("total")),smallFont));
                        cell.setHorizontalAlignment (Element.ALIGN_RIGHT);
                        cell.setBorder(0);
                        table.addCell(cell);
                        
                    }else{
                        //imprimir totales de la cuenta anterior
                        cell= new PdfPCell(new Paragraph("Totales",smallBoldFont));
                        cell.setHorizontalAlignment (Element.ALIGN_CENTER);
                        cell.setBorder(1);
                        table.addCell(cell);
                        
                        cell= new PdfPCell(new Paragraph(simbolo_moneda,smallBoldFont));
                        cell.setHorizontalAlignment (Element.ALIGN_RIGHT);
                        cell.setBorder(1);
                        table.addCell(cell);
                        
                        cell= new PdfPCell(new Paragraph(StringHelper.AgregaComas(StringHelper.roundDouble(suma_efectivo,2)),smallBoldFont));
                        cell.setHorizontalAlignment (Element.ALIGN_RIGHT);
                        cell.setBorder(1);
                        table.addCell(cell);
                        
                        cell= new PdfPCell(new Paragraph(simbolo_moneda,smallBoldFont));
                        cell.setHorizontalAlignment (Element.ALIGN_RIGHT);
                        cell.setBorder(1);
                        table.addCell(cell);
                        
                        cell= new PdfPCell(new Paragraph(StringHelper.AgregaComas(StringHelper.roundDouble(suma_cheque,2)),smallBoldFont));
                        cell.setHorizontalAlignment (Element.ALIGN_RIGHT);
                        cell.setBorder(1);
                        table.addCell(cell);
                        
                        cell= new PdfPCell(new Paragraph(simbolo_moneda,smallBoldFont));
                        cell.setHorizontalAlignment (Element.ALIGN_RIGHT);
                        cell.setBorder(1);
                        table.addCell(cell);
                        
                        cell= new PdfPCell(new Paragraph(StringHelper.AgregaComas(StringHelper.roundDouble(suma_transferencia,2)),smallBoldFont));
                        cell.setHorizontalAlignment (Element.ALIGN_RIGHT);
                        cell.setBorder(1);
                        table.addCell(cell);
                        
                        cell= new PdfPCell(new Paragraph(simbolo_moneda,smallBoldFont));
                        cell.setHorizontalAlignment (Element.ALIGN_RIGHT);
                        cell.setBorder(1);
                        table.addCell(cell);
                        
                        cell= new PdfPCell(new Paragraph(StringHelper.AgregaComas(StringHelper.roundDouble(suma_tarjeta,2)),smallBoldFont));
                        cell.setHorizontalAlignment (Element.ALIGN_RIGHT);
                        cell.setBorder(1);
                        table.addCell(cell);
                        
                        cell= new PdfPCell(new Paragraph(simbolo_moneda,smallBoldFont));
                        cell.setHorizontalAlignment (Element.ALIGN_RIGHT);
                        cell.setBorder(1);
                        table.addCell(cell);
                        
                        cell= new PdfPCell(new Paragraph(StringHelper.AgregaComas(StringHelper.roundDouble(suma_otro,2)),smallBoldFont));
                        cell.setHorizontalAlignment (Element.ALIGN_RIGHT);
                        cell.setBorder(1);
                        table.addCell(cell);
                        
                        cell= new PdfPCell(new Paragraph(simbolo_moneda,smallBoldFont));
                        cell.setHorizontalAlignment (Element.ALIGN_RIGHT);
                        cell.setBorder(1);
                        table.addCell(cell);
                        
                        cell= new PdfPCell(new Paragraph(StringHelper.AgregaComas(StringHelper.roundDouble(suma_total,2)),smallBoldFont));
                        cell.setHorizontalAlignment (Element.ALIGN_RIGHT);
                        cell.setBorder(1);
                        table.addCell(cell);
                        
                        //fila vacia
                        cell= new PdfPCell(new Paragraph("",smallBoldFont));
                        cell.setHorizontalAlignment (Element.ALIGN_RIGHT);
                        cell.setBorder(0);
                        cell.setColspan(13);
                        cell.setFixedHeight(20);
                        table.addCell(cell);
                        
                        
                        //inicializar variables
                        suma_efectivo = 0.00;
                        suma_cheque = 0.00;
                        suma_transferencia = 0.00;
                        suma_tarjeta = 0.00;
                        suma_otro = 0.00;
                        suma_total = 0.00;
                        
                        //tomar nuevos valores para variables
                        cta=registro.get("no_cuenta");
                        nombre_banco=registro.get("banco");
                        moneda_cta=registro.get("moneda_cuenta");
                        
                        if(registro.get("moneda_cuenta").equals("M.N.")){
                            simbolo_moneda = "$";
                        }
                        
                        if(registro.get("moneda_cuenta").equals("USD")){
                            simbolo_moneda = "USD";
                        }
                        
                        cell= new PdfPCell(new Paragraph("Banco: "+nombre_banco +"      Cuenta: "+cta+"     Moneda: "+moneda_cta,smallBoldFont));
                        cell.setHorizontalAlignment (Element.ALIGN_LEFT);
                        cell.setVerticalAlignment(Element.ALIGN_BOTTOM);
                        cell.setBorder(0);
                        cell.setColspan(13);
                        cell.setFixedHeight(18);
                        table.addCell(cell);
                        
                        
                        cell= new PdfPCell(new Paragraph(registro.get("fecha_deposito"),smallFont));
                        cell.setHorizontalAlignment (Element.ALIGN_CENTER);
                        cell.setBorder(0);
                        table.addCell(cell);
                        
                        cell= new PdfPCell(new Paragraph(simbolo_moneda,smallFont));
                        cell.setHorizontalAlignment (Element.ALIGN_RIGHT);
                        cell.setBorder(0);
                        table.addCell(cell);

                        cell= new PdfPCell(new Paragraph(StringHelper.AgregaComas(registro.get("efectivo")),smallFont));
                        cell.setHorizontalAlignment (Element.ALIGN_RIGHT);
                        cell.setBorder(0);
                        table.addCell(cell);

                        cell= new PdfPCell(new Paragraph(simbolo_moneda,smallFont));
                        cell.setHorizontalAlignment (Element.ALIGN_RIGHT);
                        cell.setBorder(0);
                        table.addCell(cell);

                        cell= new PdfPCell(new Paragraph(StringHelper.AgregaComas(registro.get("cheque")),smallFont));
                        cell.setHorizontalAlignment (Element.ALIGN_RIGHT);
                        cell.setBorder(0);
                        table.addCell(cell);

                        cell= new PdfPCell(new Paragraph(simbolo_moneda,smallFont));
                        cell.setHorizontalAlignment (Element.ALIGN_RIGHT);
                        cell.setBorder(0);
                        table.addCell(cell);

                        cell= new PdfPCell(new Paragraph(StringHelper.AgregaComas(registro.get("transferencia")),smallFont));
                        cell.setHorizontalAlignment (Element.ALIGN_RIGHT);
                        cell.setBorder(0);
                        table.addCell(cell);

                        cell= new PdfPCell(new Paragraph(simbolo_moneda,smallFont));
                        cell.setHorizontalAlignment (Element.ALIGN_RIGHT);
                        cell.setBorder(0);
                        table.addCell(cell);

                        cell= new PdfPCell(new Paragraph(StringHelper.AgregaComas(registro.get("tarjeta")),smallFont));
                        cell.setHorizontalAlignment (Element.ALIGN_RIGHT);
                        cell.setBorder(0);
                        table.addCell(cell);

                        cell= new PdfPCell(new Paragraph(simbolo_moneda,smallFont));
                        cell.setHorizontalAlignment (Element.ALIGN_RIGHT);
                        cell.setBorder(0);
                        table.addCell(cell);

                        cell= new PdfPCell(new Paragraph(StringHelper.AgregaComas(registro.get("otro")),smallFont));
                        cell.setHorizontalAlignment (Element.ALIGN_RIGHT);
                        cell.setBorder(0);
                        table.addCell(cell);

                        cell= new PdfPCell(new Paragraph(simbolo_moneda,smallFont));
                        cell.setHorizontalAlignment (Element.ALIGN_RIGHT);
                        cell.setBorder(0);
                        table.addCell(cell);

                        cell= new PdfPCell(new Paragraph(StringHelper.AgregaComas(registro.get("total")),smallFont));
                        cell.setHorizontalAlignment (Element.ALIGN_RIGHT);
                        cell.setBorder(0);
                        table.addCell(cell);
                        
                        
                        
                        //sumar el primer registro de la nueva cuenta
                        suma_efectivo += Double.parseDouble(registro.get("efectivo"));
                        suma_cheque += Double.parseDouble(registro.get("cheque"));
                        suma_transferencia += Double.parseDouble(registro.get("transferencia"));
                        suma_tarjeta += Double.parseDouble(registro.get("tarjeta"));
                        suma_otro += Double.parseDouble(registro.get("otro"));
                        suma_total += Double.parseDouble(registro.get("total"));
                        
                    } 
                    
                    
                }

                //imprimir totales de la ultima cuenta
                cell= new PdfPCell(new Paragraph("Totales",smallBoldFont));
                cell.setHorizontalAlignment (Element.ALIGN_CENTER);
                cell.setBorder(1);
                table.addCell(cell);

                cell= new PdfPCell(new Paragraph(simbolo_moneda,smallBoldFont));
                cell.setHorizontalAlignment (Element.ALIGN_RIGHT);
                cell.setBorder(1);
                table.addCell(cell);

                cell= new PdfPCell(new Paragraph(StringHelper.AgregaComas(StringHelper.roundDouble(suma_efectivo,2)),smallBoldFont));
                cell.setHorizontalAlignment (Element.ALIGN_RIGHT);
                cell.setBorder(1);
                table.addCell(cell);

                cell= new PdfPCell(new Paragraph(simbolo_moneda,smallBoldFont));
                cell.setHorizontalAlignment (Element.ALIGN_RIGHT);
                cell.setBorder(1);
                table.addCell(cell);

                cell= new PdfPCell(new Paragraph(StringHelper.AgregaComas(StringHelper.roundDouble(suma_cheque,2)),smallBoldFont));
                cell.setHorizontalAlignment (Element.ALIGN_RIGHT);
                cell.setBorder(1);
                table.addCell(cell);

                cell= new PdfPCell(new Paragraph(simbolo_moneda,smallBoldFont));
                cell.setHorizontalAlignment (Element.ALIGN_RIGHT);
                cell.setBorder(1);
                table.addCell(cell);

                cell= new PdfPCell(new Paragraph(StringHelper.AgregaComas(StringHelper.roundDouble(suma_transferencia,2)),smallBoldFont));
                cell.setHorizontalAlignment (Element.ALIGN_RIGHT);
                cell.setBorder(1);
                table.addCell(cell);

                cell= new PdfPCell(new Paragraph(simbolo_moneda,smallBoldFont));
                cell.setHorizontalAlignment (Element.ALIGN_RIGHT);
                cell.setBorder(1);
                table.addCell(cell);

                cell= new PdfPCell(new Paragraph(StringHelper.AgregaComas(StringHelper.roundDouble(suma_tarjeta,2)),smallBoldFont));
                cell.setHorizontalAlignment (Element.ALIGN_RIGHT);
                cell.setBorder(1);
                table.addCell(cell);

                cell= new PdfPCell(new Paragraph(simbolo_moneda,smallBoldFont));
                cell.setHorizontalAlignment (Element.ALIGN_RIGHT);
                cell.setBorder(1);
                table.addCell(cell);

                cell= new PdfPCell(new Paragraph(StringHelper.AgregaComas(StringHelper.roundDouble(suma_otro,2)),smallBoldFont));
                cell.setHorizontalAlignment (Element.ALIGN_RIGHT);
                cell.setBorder(1);
                table.addCell(cell);

                cell= new PdfPCell(new Paragraph(simbolo_moneda,smallBoldFont));
                cell.setHorizontalAlignment (Element.ALIGN_RIGHT);
                cell.setBorder(1);
                table.addCell(cell);

                cell= new PdfPCell(new Paragraph(StringHelper.AgregaComas(StringHelper.roundDouble(suma_total,2)),smallBoldFont));
                cell.setHorizontalAlignment (Element.ALIGN_RIGHT);
                cell.setBorder(1);
                table.addCell(cell);
                
                
                
            }else{
                cell= new PdfPCell(new Paragraph("No hay depositos, selecciona fechas diferentes y vualva a generar el reporte",smallFont));
                cell.setHorizontalAlignment (Element.ALIGN_LEFT);
                cell.setBorder(0);
                cell.setColspan(13);
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
            ColumnText.showTextAligned(writer.getDirectContent(),Element.ALIGN_CENTER, new Phrase("Depositos en Bancos",largeBoldFont),document.getPageSize().getWidth()/2, document.getPageSize().getTop()-38, 0);
            ColumnText.showTextAligned(writer.getDirectContent(),Element.ALIGN_CENTER, new Phrase(this.getPeriodo(),largeFont),document.getPageSize().getWidth()/2, document.getPageSize().getTop()-50, 0);
            
            SimpleDateFormat formato = new SimpleDateFormat("'Impreso en' MMMMM d, yyyy 'a las' HH:mm:ss 'hrs.'");
            String impreso_en = formato.format(new Date());
            
            
            cb = writer.getDirectContent();
            float textBase = document.bottom() - 20;
            
            //texto inferior izquieda pie de pagina
            String text_left ="";
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
