package com.agnux.kemikal.reportes;

import com.agnux.common.helpers.StringHelper;
import java.net.URISyntaxException;
import java.util.Iterator;
import org.apache.commons.lang.StringEscapeUtils;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfPageEventHelper;
import com.itextpdf.text.pdf.PdfTemplate;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.Element;
import com.itextpdf.text.ExceptionConverter;
import com.itextpdf.text.Font;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.ColumnText;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
    

public class PdfInvControlCosto {
    private HashMap<String, String> datosHeaderFooter = new HashMap<String, String>();
    private ArrayList<HashMap<String, String>> lista_productos = new ArrayList<HashMap<String, String>>();
    private String file_out;
    private String tipo_costo;
    
    public HashMap<String, String> getDatosHeaderFooter() {
        return datosHeaderFooter;
    }
    
    public void setDatosHeaderFooter(HashMap<String, String> datosHeaderFooter) {
        this.datosHeaderFooter = datosHeaderFooter;
    }
    
    public ArrayList<HashMap<String, String>> getLista_productos() {
        return lista_productos;
    }
    
    public void setLista_productos(ArrayList<HashMap<String, String>> lista_productos) {
        this.lista_productos = lista_productos;
    }
    
    public String getFile_out() {
        return file_out;
    }
    
    public void setFile_out(String file_out) {
        this.file_out = file_out;
    }
    
    public String getTipo_costo() {
        return tipo_costo;
    }
    
    public void setTipo_costo(String tipo_costo) {
        this.tipo_costo = tipo_costo;
    }
    
    public PdfInvControlCosto(HashMap<String, String> datos, ArrayList<HashMap<String, String>> productos, String fileout, String tipo_costo) {
        this.setFile_out(fileout);
        this.setTipo_costo(tipo_costo);
        this.setDatosHeaderFooter(datos);
        this.setLista_productos(productos);
    }
    
    public void ViewPDF() throws URISyntaxException {
        Font smallsmall = new Font(Font.FontFamily.HELVETICA,5,Font.NORMAL,BaseColor.BLACK);
        Font smallFont = new Font(Font.FontFamily.HELVETICA,7,Font.NORMAL,BaseColor.BLACK);
        Font smallBoldFont = new Font(Font.FontFamily.HELVETICA, 8, Font.BOLD, BaseColor.WHITE);
        Font largeBoldFont = new Font(Font.FontFamily.HELVETICA,10,Font.BOLD,BaseColor.BLACK);
        
        PdfPTable tableAbreviaturas;
        PdfPCell cell;
        Iterator it;
        
        try {
            HeaderFooter event = new HeaderFooter(this.getDatosHeaderFooter());
            Document document = new Document(PageSize.LETTER.rotate(),-60,-60,60,30);
            document.addCreator("gpmarsan@gmail.com");
           // Document document =      new Document(PageSize.LETTER.rotate(), -50, -50, 60, 30);
            PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(this.getFile_out()));
            writer.setPageEvent(event);
            
            System.out.println("PDF: "+this.getFile_out());
            
            document.open();
            
            float [] widths = {1,3,1.4f,1.6f,1,1,0.8f,0.8f,1,1,1,1,1,1,0.8f};
            PdfPTable table = new PdfPTable(widths);
            table.setKeepTogether(false);
            table.setHeaderRows(1);
            
            cell = new PdfPCell(new Paragraph("Código",smallBoldFont));
            cell.setUseAscender(true);
            cell.setHorizontalAlignment(Element.ALIGN_LEFT);
            cell.setUseDescender(true);
            cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            cell.setBackgroundColor(BaseColor.BLACK);
            table.addCell(cell);
            
            cell = new PdfPCell(new Paragraph("Descripción",smallBoldFont));
            cell.setUseAscender(true);
            cell.setHorizontalAlignment(Element.ALIGN_LEFT);
            cell.setUseDescender(true);
            cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            cell.setBackgroundColor(BaseColor.BLACK);
            table.addCell(cell);
            
            cell = new PdfPCell(new Paragraph("Unidad",smallBoldFont));
            cell.setUseAscender(true);
            cell.setHorizontalAlignment(Element.ALIGN_LEFT);
            cell.setUseDescender(true);
            cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            cell.setBackgroundColor(BaseColor.BLACK);
            table.addCell(cell);
            
            cell = new PdfPCell(new Paragraph("Presentación",smallBoldFont));
            cell.setUseAscender(true);
            cell.setHorizontalAlignment(Element.ALIGN_LEFT);
            cell.setUseDescender(true);
            cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            cell.setBackgroundColor(BaseColor.BLACK);
            table.addCell(cell);
            
            cell = new PdfPCell(new Paragraph("O.C.",smallBoldFont));
            cell.setUseAscender(true);
            cell.setHorizontalAlignment(Element.ALIGN_LEFT);
            cell.setUseDescender(true);
            cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            cell.setBackgroundColor(BaseColor.BLACK);
            table.addCell(cell);
            
            cell = new PdfPCell(new Paragraph("Fac.",smallBoldFont));
            cell.setUseAscender(true);
            cell.setHorizontalAlignment(Element.ALIGN_LEFT);
            cell.setUseDescender(true);
            cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            cell.setBackgroundColor(BaseColor.BLACK);
            table.addCell(cell);
            
            cell = new PdfPCell(new Paragraph("Mon.",smallBoldFont));
            cell.setUseAscender(true);
            cell.setHorizontalAlignment(Element.ALIGN_LEFT);
            cell.setUseDescender(true);
            cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            cell.setBackgroundColor(BaseColor.BLACK);
            table.addCell(cell);
            
            cell = new PdfPCell(new Paragraph("T.C.",smallBoldFont));
            cell.setUseAscender(true);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setUseDescender(true);
            cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            cell.setBackgroundColor(BaseColor.BLACK);
            table.addCell(cell);
            
            String etiqueta_tipo_costo ="";
            if(this.getTipo_costo().equals("1")){
                etiqueta_tipo_costo="C.U.";
            }else{
                etiqueta_tipo_costo="C.P.";
            }
            
            cell = new PdfPCell(new Paragraph(etiqueta_tipo_costo,smallBoldFont));
            cell.setUseAscender(true);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setUseDescender(true);
            cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            cell.setBackgroundColor(BaseColor.BLACK);
            table.addCell(cell);
            
            cell = new PdfPCell(new Paragraph("I.G.I.",smallBoldFont));
            cell.setUseAscender(true);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setUseDescender(true);
            cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            cell.setBackgroundColor(BaseColor.BLACK);
            table.addCell(cell);
            
            cell = new PdfPCell(new Paragraph("G.I.",smallBoldFont));
            cell.setUseAscender(true);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setUseDescender(true);
            cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            cell.setBackgroundColor(BaseColor.BLACK);
            table.addCell(cell);
            
            cell = new PdfPCell(new Paragraph("C.A.",smallBoldFont));
            cell.setUseAscender(true);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setUseDescender(true);
            cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            cell.setBackgroundColor(BaseColor.BLACK);
            table.addCell(cell);
            
            cell = new PdfPCell(new Paragraph("C.I.T.",smallBoldFont));
            cell.setUseAscender(true);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setUseDescender(true);
            cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            cell.setBackgroundColor(BaseColor.BLACK);
            table.addCell(cell);
            
            cell = new PdfPCell(new Paragraph("P.M.",smallBoldFont));
            cell.setUseAscender(true);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setUseDescender(true);
            cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            cell.setBackgroundColor(BaseColor.BLACK);
            table.addCell(cell);
            
            cell = new PdfPCell(new Paragraph("M.P.M.",smallBoldFont));
            cell.setUseAscender(true);
            cell.setHorizontalAlignment(Element.ALIGN_LEFT);
            cell.setUseDescender(true);
            cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            cell.setBackgroundColor(BaseColor.BLACK);
            table.addCell(cell);
            
            it = this.getLista_productos().iterator();
            while(it.hasNext()){
                HashMap<String,String> map = (HashMap<String,String>)it.next();
                
                cell = new PdfPCell(new Paragraph(esteAtributoSeDejoNulo(map.get("codigo")), smallFont));
                cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                cell.setHorizontalAlignment(Element.ALIGN_LEFT);
                table.addCell(cell);
                
                String descripcion = map.get("descripcion");
                descripcion =  StringEscapeUtils.unescapeHtml(descripcion);
                cell = new PdfPCell(new Paragraph(StringHelper.capitalizaString(descripcion), smallFont));
                cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                cell.setHorizontalAlignment(Element.ALIGN_LEFT);
                table.addCell(cell);
                
                cell = new PdfPCell(new Paragraph(StringHelper.capitalizaString(esteAtributoSeDejoNulo(map.get("unidad"))), smallFont));
                cell.setHorizontalAlignment(Element.ALIGN_LEFT);
                cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                table.addCell(cell);
                
                cell = new PdfPCell(new Paragraph(StringHelper.capitalizaString(esteAtributoSeDejoNulo(map.get("presentacion"))), smallFont));
                cell.setHorizontalAlignment(Element.ALIGN_LEFT);
                cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                table.addCell(cell);
                
                cell = new PdfPCell(new Paragraph(StringHelper.capitalizaString(esteAtributoSeDejoNulo(map.get("orden_compra"))), smallFont));
                cell.setHorizontalAlignment(Element.ALIGN_LEFT);
                cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                table.addCell(cell);
                
                cell = new PdfPCell(new Paragraph(StringHelper.capitalizaString(esteAtributoSeDejoNulo(map.get("factura_prov"))), smallFont));
                cell.setHorizontalAlignment(Element.ALIGN_LEFT);
                cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                table.addCell(cell);
                
                cell = new PdfPCell(new Paragraph(StringHelper.capitalizaString(esteAtributoSeDejoNulo(map.get("moneda"))), smallFont));
                cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                table.addCell(cell);
                
                cell = new PdfPCell(new Paragraph(StringHelper.AgregaComas(map.get("tipo_cambio")), smallFont));
                cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                table.addCell(cell);
                
                cell = new PdfPCell(new Paragraph(StringHelper.AgregaComas(map.get("costo")), smallFont));
                cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                table.addCell(cell);
                
                cell = new PdfPCell(new Paragraph(StringHelper.AgregaComas(map.get("costo_importacion")), smallFont));
                cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                table.addCell(cell);
                
                cell = new PdfPCell(new Paragraph(StringHelper.AgregaComas(map.get("costo_directo")), smallFont));
                cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                table.addCell(cell);
                
                cell = new PdfPCell(new Paragraph(StringHelper.AgregaComas(map.get("costo_adic")), smallFont));
                cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                table.addCell(cell);
                
                cell = new PdfPCell(new Paragraph(StringHelper.AgregaComas(map.get("costo_referencia")), smallFont));
                cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                table.addCell(cell);
                
                cell = new PdfPCell(new Paragraph(StringHelper.AgregaComas(map.get("precio_minimo")), smallFont));
                cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                table.addCell(cell);
                
                cell = new PdfPCell(new Paragraph(StringHelper.capitalizaString(esteAtributoSeDejoNulo(map.get("moneda_pm"))), smallFont));
                cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                table.addCell(cell);
            } 
            
            cell = new PdfPCell(new Paragraph("", smallFont));
            cell.setBorderWidthBottom(0);
            cell.setBorderWidthTop(1);
            cell.setBorderWidthRight(0);
            cell.setBorderWidthLeft(0);
            cell.setColspan(14);
            table.addCell(cell);
            
            document.add(table);
            
            
            //aquí empieza la tbla de de descripción de Abreviaturas de los campos
            float [] widths3 = {0.8f,5,0.8f,5,0.8f,5,1,5};
            tableAbreviaturas = new PdfPTable(widths3);
            tableAbreviaturas.setKeepTogether(true);
            int altura_fila=11;
            
            //FILA 1
            /*
            cell = new PdfPCell(new Paragraph("*  Los valores para los campos T.C., C.U., I.G.I., G.I., C.I.T., P.M. son en M.N.",smallFont));
            cell.setBorder(0);
            cell.setColspan(8);
            cell.setFixedHeight(altura_fila);
            tableAbreviaturas.addCell(cell);
             */
            
            /*
            cell = new PdfPCell(new Paragraph("",smallFont));
            cell.setBorder(0);
            tableAbreviaturas.addCell(cell);
            
            cell = new PdfPCell(new Paragraph("",smallFont));
            cell.setBorder(0);
            tableAbreviaturas.addCell(cell);
            
            cell = new PdfPCell(new Paragraph("",smallFont));
            cell.setBorder(0);
            tableAbreviaturas.addCell(cell);
            
            cell = new PdfPCell(new Paragraph("",smallFont));
            cell.setBorder(0);
            tableAbreviaturas.addCell(cell);
            
            cell = new PdfPCell(new Paragraph("",smallFont));
            cell.setBorder(0);
            tableAbreviaturas.addCell(cell);
            
            cell = new PdfPCell(new Paragraph("",smallFont));
            cell.setBorder(0);
            tableAbreviaturas.addCell(cell);
            
            cell = new PdfPCell(new Paragraph("",smallFont));
            cell.setBorder(0);
            tableAbreviaturas.addCell(cell);
            */
            
            
            //FILA 2
            cell = new PdfPCell(new Paragraph("O.C.:",smallFont));
            cell.setBorder(0);
            cell.setFixedHeight(altura_fila);
            tableAbreviaturas.addCell(cell);
            
            cell = new PdfPCell(new Paragraph("Orden de Compra",smallFont));
            cell.setBorder(0);
            tableAbreviaturas.addCell(cell);
            
            cell = new PdfPCell(new Paragraph("C.U.:",smallFont));
            cell.setBorder(0);
            tableAbreviaturas.addCell(cell);
            
            cell = new PdfPCell(new Paragraph("Costo de la ultima Compra",smallFont));
            cell.setBorder(0);
            tableAbreviaturas.addCell(cell);
            
            cell = new PdfPCell(new Paragraph("I.G.I.:",smallFont));
            cell.setBorder(0);
            tableAbreviaturas.addCell(cell);
            
            cell = new PdfPCell(new Paragraph("Impuesto General de Importación",smallFont));
            cell.setBorder(0);
            tableAbreviaturas.addCell(cell);
            
            cell = new PdfPCell(new Paragraph("C.I.T.:",smallFont));
            cell.setBorder(0);
            tableAbreviaturas.addCell(cell);
            
            cell = new PdfPCell(new Paragraph("Costo Integrado Total",smallFont));
            cell.setBorder(0);
            tableAbreviaturas.addCell(cell);
            
            //FILA 3
            cell = new PdfPCell(new Paragraph("T.C.:",smallFont));
            cell.setBorder(0);
            cell.setFixedHeight(altura_fila);
            tableAbreviaturas.addCell(cell);
            
            cell = new PdfPCell(new Paragraph("Tipo de Cambio de la Compra",smallFont));
            cell.setBorder(0);
            tableAbreviaturas.addCell(cell);
            
            cell = new PdfPCell(new Paragraph("	C.P.:",smallFont));
            cell.setBorder(0);
            tableAbreviaturas.addCell(cell);
            
            cell = new PdfPCell(new Paragraph("Costo Promedio",smallFont));
            cell.setBorder(0);
            tableAbreviaturas.addCell(cell);
            
            cell = new PdfPCell(new Paragraph("G.I.:",smallFont));
            cell.setBorder(0);
            tableAbreviaturas.addCell(cell);
            
            cell = new PdfPCell(new Paragraph("Gastos de Importación",smallFont));
            cell.setBorder(0);
            tableAbreviaturas.addCell(cell);
            
            cell = new PdfPCell(new Paragraph("P.M.:",smallFont));
            cell.setBorder(0);
            tableAbreviaturas.addCell(cell);
            
            cell = new PdfPCell(new Paragraph("Precio Mínimo de Venta",smallFont));
            cell.setBorder(0);
            tableAbreviaturas.addCell(cell);
            
            //FILA 4
            cell = new PdfPCell(new Paragraph("Mon.:",smallFont));
            cell.setBorder(0);
            cell.setFixedHeight(altura_fila);
            tableAbreviaturas.addCell(cell);
            
            cell = new PdfPCell(new Paragraph("Moneda de la Factura de Compra",smallFont));
            cell.setBorder(0);
            tableAbreviaturas.addCell(cell);
            
            cell = new PdfPCell(new Paragraph("	C.A.:",smallFont));
            cell.setBorder(0);
            tableAbreviaturas.addCell(cell);
            
            cell = new PdfPCell(new Paragraph("Costo Adicional",smallFont));
            cell.setBorder(0);
            tableAbreviaturas.addCell(cell);
            
            cell = new PdfPCell(new Paragraph("",smallFont));
            cell.setBorder(0);
            tableAbreviaturas.addCell(cell);
            
            cell = new PdfPCell(new Paragraph("",smallFont));
            cell.setBorder(0);
            tableAbreviaturas.addCell(cell);
            
            cell = new PdfPCell(new Paragraph("M.P.M.:",smallFont));
            cell.setBorder(0);
            tableAbreviaturas.addCell(cell);
            
            cell = new PdfPCell(new Paragraph("Moneda del Precio Mínimo de Venta",smallFont));
            cell.setBorder(0);
            tableAbreviaturas.addCell(cell);
            
            document.add(tableAbreviaturas);
            
            
            
            document.close();
            
            
        }
        catch (Exception e) {
                e.printStackTrace();
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
