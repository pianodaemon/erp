

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

/*
 * @author Vale Santos
 */
public class PdfReporteClientes {

    public PdfReporteClientes(String fileout, String ruta_imagen, String razon_soc_empresa,String fechaImpresion, ArrayList<HashMap<String, String>> listaClientes) throws DocumentException {

        String[] fi = fechaImpresion.split("-");

        String periodo_reporte = fechaImpresion;

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
            doc.addCreator("valentin.vale8490@gmail.com");
            PdfWriter writer = PdfWriter.getInstance(doc, new FileOutputStream(fileout));
            writer.setPageEvent(event);

            doc.open();
            //float [] widths = {3f, 3f, 3f, 3f, 3f, 3f, 4f};
            float [] widths = {4f, 2f,  3.5f, 10f};
            PdfPTable table = new PdfPTable(widths);
            PdfPCell cell;

            table.setKeepTogether(false);
            table.setHeaderRows(1);

            // Encabezado de Celda
            cell = new PdfPCell(new Paragraph("Razon Social",headerFont));
            cell.setUseAscender(true);
            cell.setHorizontalAlignment(Element.ALIGN_LEFT);
            cell.setUseDescender(true);
            cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            cell.setBackgroundColor(BaseColor.BLACK);
            cell.setFixedHeight(13);
            table.addCell(cell);

            cell = new PdfPCell(new Paragraph("Telefono(s)",headerFont));
            cell.setUseAscender(true);
            cell.setHorizontalAlignment(Element.ALIGN_LEFT);
            cell.setUseDescender(true);
            cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            cell.setBackgroundColor(BaseColor.BLACK);
            cell.setFixedHeight(13);
            table.addCell(cell);


            cell = new PdfPCell(new Paragraph("Email",headerFont));
            cell.setUseAscender(true);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setUseDescender(true);
            cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            cell.setBackgroundColor(BaseColor.BLACK);
            cell.setFixedHeight(13);
            table.addCell(cell);

            cell = new PdfPCell(new Paragraph("Descripcion",headerFont));
            cell.setUseAscender(true);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setUseDescender(true);
            cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            cell.setBackgroundColor(BaseColor.BLACK);
            cell.setFixedHeight(13);
            table.addCell(cell);


            if(listaClientes.size() > 0){




                for (int x=0; x<=listaClientes.size()-1;x++){
                    HashMap<String,String> registro = listaClientes.get(x);


                    //Razon_social
                    cell= new PdfPCell(new Paragraph(registro.get("razon_social"),smallFont));
                    cell.setHorizontalAlignment (Element.ALIGN_LEFT);
                    cell.setBorder(0);
                    table.addCell(cell);
                    //Telefonos
                    cell= new PdfPCell(new Paragraph(registro.get("telefonos"),smallFont));
                    cell.setHorizontalAlignment (Element.ALIGN_LEFT);
                    cell.setBorder(0);
                    table.addCell(cell);

                    //email
                    cell= new PdfPCell(new Paragraph(registro.get("email"),smallFont));
                    cell.setHorizontalAlignment (Element.ALIGN_LEFT);
                    cell.setBorder(0);
                    table.addCell(cell);
                    //direccion del cliente
                    cell= new PdfPCell(new Paragraph(registro.get("direccion_cliente"),smallFont));
                    cell.setHorizontalAlignment (Element.ALIGN_LEFT);
                    cell.setBorder(0);
                    table.addCell(cell);

                }
            }else{
                cell= new PdfPCell(new Paragraph("Esta consulta no genero ningun Resultado",smallFont));
                cell.setHorizontalAlignment (Element.ALIGN_LEFT);
                //cell.setBorder(0);
                cell.setColspan(4);
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
            ColumnText.showTextAligned(writer.getDirectContent(),Element.ALIGN_CENTER, new Phrase("Reporte de Clientes",largeBoldFont),document.getPageSize().getWidth()/2, document.getPageSize().getTop()-38, 0);
            ColumnText.showTextAligned(writer.getDirectContent(),Element.ALIGN_CENTER, new Phrase(this.getPeriodo(),largeFont),document.getPageSize().getWidth()/2, document.getPageSize().getTop()-50, 0);

            SimpleDateFormat formato = new SimpleDateFormat("'Impreso en' MMMMM d, yyyy 'a las' HH:mm:ss 'hrs.'");
            String impreso_en = formato.format(new Date());

            cb = writer.getDirectContent();

            cb.beginText();
            cb.setFontAndSize(helv, 7);
            cb.setTextMatrix(document.left()+90, document.bottom() - 20 );  //definir la posicion de text
            cb.showText(impreso_en);
            cb.endText();

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

