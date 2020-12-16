/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.agnux.kemikal.reportes;
import com.agnux.common.helpers.BarcodeHelper;
import com.agnux.common.helpers.FileHelper;
import com.agnux.common.helpers.StringHelper;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
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
import java.io.FileOutputStream;
import java.net.URISyntaxException;
import java.util.Iterator;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

/**
 *
 * @author agnux
 */
public final class PdfReporteInvExisLotes {
    private HashMap<String, String> datosHeaderFooter = new HashMap<String, String>();
    private ArrayList<HashMap<String, String>> lista_productos = new ArrayList<HashMap<String, String>>();
    private String fileout;
    private String ruta_imagen;
    
    public HashMap<String, String> getDatosHeaderFooter() {
        return datosHeaderFooter;
    }
    
    public void setDatosHeaderFooter(HashMap<String, String> datosHeaderFooter) {
        this.datosHeaderFooter = datosHeaderFooter;
    }
    
    public String getFileout() {
        return fileout;
    }
    
    public void setFileout(String fileout) {
        this.fileout = fileout;
    }
    
    public ArrayList<HashMap<String, String>> getLista_productos() {
        return lista_productos;
    }
    
    public void setLista_productos(ArrayList<HashMap<String, String>> lista_productos) {
        this.lista_productos = lista_productos;
    }
    
    public String getRuta_imagen() {
        return ruta_imagen;
    }
    
    public void setRuta_imagen(String ruta_imagen) {
        this.ruta_imagen = ruta_imagen;
    }
    
    public PdfReporteInvExisLotes(ArrayList<HashMap<String, String>> lista_productos, String fileout, String razon_soc_empresa) {
        HashMap<String, String> datos = new HashMap<String, String>();
        this.setLista_productos(lista_productos);
        this.setFileout(fileout);
        this.setRuta_imagen(ruta_imagen);
        SimpleDateFormat formato = new SimpleDateFormat("'Impreso en' MMMMM d, yyyy 'a las' HH:mm:ss 'hrs.'");
        String impreso_en = formato.format(new Date());
        
        //datos para el encabezado, no se esta utilizando
        datos.put("empresa", razon_soc_empresa);
        datos.put("titulo_reporte", "Reporte de Existencias por Lote");
        datos.put("periodo", impreso_en);
        
        //datos para el pie de pagina
        datos.put("codigo1", "");
        datos.put("codigo2", "");
        
        this.setDatosHeaderFooter(datos);
    }
    
    
    
    
    public void ViewPDF() throws URISyntaxException {
        HashMap<String, String> datos = new HashMap<String, String>();
        Font smallsmall = new Font(Font.FontFamily.HELVETICA,5,Font.NORMAL,BaseColor.BLACK);
        Font smallFont = new Font(Font.FontFamily.HELVETICA,7,Font.NORMAL,BaseColor.BLACK);
        Font smallBoldFont = new Font(Font.FontFamily.HELVETICA,8,Font.BOLD,BaseColor.WHITE);
        Font largeBoldFont = new Font(Font.FontFamily.HELVETICA,10,Font.BOLD,BaseColor.BLACK);
        
        
        
        PdfPTable tablaPrincipal;
        PdfPTable table2;
        PdfPTable tableElaboro;
        PdfPCell cell;
        ImagenPDF ipdf;
        
        try {
            HeaderFooter event = new HeaderFooter(this.getDatosHeaderFooter());
            Document document = new Document(PageSize.LETTER, -50, -50, 60, 30);
            PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(this.getFileout()));
            writer.setPageEvent(event);
            
            document.open();
            
            
            float [] widths2 = {2,5.5f,2,5,5};
            PdfPTable table = new PdfPTable(widths2);
            
            Iterator it;
            
            table.setKeepTogether(false);
            table.setHeaderRows(1);
            
            cell = new PdfPCell(new Paragraph("Código",smallBoldFont));
            cell.setUseAscender(true);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setUseDescender(true);
            cell.setVerticalAlignment(Element.ALIGN_CENTER);
            cell.setBackgroundColor(BaseColor.BLACK);
            table.addCell(cell);
            
            
            cell = new PdfPCell(new Paragraph("Descripción",smallBoldFont));
            cell.setUseAscender(true);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setUseDescender(true);
            cell.setVerticalAlignment(Element.ALIGN_CENTER);
            cell.setBackgroundColor(BaseColor.BLACK);
            table.addCell(cell);
            
            cell = new PdfPCell(new Paragraph("Existencia",smallBoldFont));
            cell.setUseAscender(true);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setUseDescender(true);
            cell.setVerticalAlignment(Element.ALIGN_CENTER);
            cell.setBackgroundColor(BaseColor.BLACK);
            table.addCell(cell);
            
            cell = new PdfPCell(new Paragraph("Lote Interno",smallBoldFont));
            cell.setUseAscender(true);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setUseDescender(true);
            cell.setVerticalAlignment(Element.ALIGN_CENTER);
            cell.setBackgroundColor(BaseColor.BLACK);
            table.addCell(cell);
            
            
            cell = new PdfPCell(new Paragraph("Código Barra",smallBoldFont));
            cell.setUseAscender(true);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setUseDescender(true);
            cell.setVerticalAlignment(Element.ALIGN_CENTER);
            cell.setBackgroundColor(BaseColor.BLACK);
            table.addCell(cell);
            
            
            it = getLista_productos().iterator();
           while(it.hasNext()){
                HashMap<String,String> map = (HashMap<String,String>)it.next();
                
                cell = new PdfPCell(new Paragraph(map.get("codigo"),smallFont));
                cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                cell.setHorizontalAlignment(Element.ALIGN_LEFT);
                cell.setFixedHeight(25);
                table.addCell(cell);
                
                cell = new PdfPCell(new Paragraph(map.get("descripcion"),smallFont));
                cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                cell.setHorizontalAlignment(Element.ALIGN_LEFT);
                cell.setFixedHeight(25);
                table.addCell(cell);
                
                cell = new PdfPCell(new Paragraph(StringHelper.AgregaComas(map.get("existencia")),smallFont));
                cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                cell.setFixedHeight(25);
                table.addCell(cell);
                
                cell = new PdfPCell(new Paragraph(map.get("lote_int"),smallFont));
                cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                cell.setFixedHeight(25);
                table.addCell(cell);
                
                String ruta_codigo=BarcodeHelper.createImgCodBarr128(map.get("lote_int"));             
                ipdf = new ImagenPDF(ruta_codigo);
                cell = new PdfPCell(ipdf.addContent());
                cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                cell.setFixedHeight(25);
                table.addCell(cell);
                FileHelper.delete(ruta_codigo);//eliminar la imagen del codigo de barras generado
            } 
            
            cell = new PdfPCell(new Paragraph("", smallFont));
            cell.setBorderWidthBottom(0);
            cell.setBorderWidthTop(1);
            cell.setBorderWidthRight(0);
            cell.setBorderWidthLeft(0);
            cell.setColspan(5);
            table.addCell(cell);
            
            document.add(table);
             
            
            document.close();
            
            
        }
        catch (Exception e) {
                e.printStackTrace();
        }
    }
    
    
    
    private class ImagenPDF {
        String Ruta;

        public String getRuta() {
            return Ruta;
        }

        public void setRuta(String Ruta) {
            this.Ruta = Ruta;
        }
        public ImagenPDF(String ruta) {
            this.setRuta(ruta);
        }
        public Image addContent() {
            Image img = null;
            try {
                img = Image.getInstance(this.getRuta());
                //img.scaleAbsoluteHeight(100);
                img.scaleAbsoluteHeight(15);
                img.scaleAbsoluteWidth(120);
                img.setAlignment(0);
            }
            catch(Exception e){
                System.out.println(e);
            }
            return img;
        }
    }
    
    
    public String esteAtributoSeDejoNulo(String atributo){
         return (atributo != null) ? (atributo) : new String();
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
