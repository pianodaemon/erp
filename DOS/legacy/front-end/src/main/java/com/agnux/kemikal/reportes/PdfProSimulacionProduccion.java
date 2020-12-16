/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
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
/**
 *
 * @author agnux
 */
public class PdfProSimulacionProduccion {
    private HashMap<String, String> datosHeaderFooter = new HashMap<String, String>();
    private ArrayList<HashMap<String, String>> lista_componentes = new ArrayList<HashMap<String, String>>();
    private String fileout;
    private String emp_razon_social;
    private String tipo_simulacion;
    private String codigo;
    private String descripcion;
    private String cantidad;
    private String cantidad_lt;
    private String densidad;    
    private String version;
    private String usuario_elaboracion;


    
    public PdfProSimulacionProduccion(HashMap<String, String> datos, ArrayList<HashMap<String, String>> componentes) {
        HashMap<String, String> data = new HashMap<String, String>();
        this.setFileout(datos.get("fileout"));
        this.setEmp_razon_social(datos.get("emp_razon_social"));
        this.setTipo_simulacion(datos.get("tipo"));
        this.setCantidad(datos.get("cantidad"));
        this.setCantidad_lt(datos.get("cantidad_lt"));
        this.setDensidad(datos.get("densidad"));
        this.setCodigo(datos.get("codigo"));
        this.setDescripcion(datos.get("descripcion"));
        this.setVersion(datos.get("version"));
        
        this.setUsuario_elaboracion(datos.get("usuario_elaboracion"));
        this.setLista_componentes(componentes);
        
        //datos para el encabezado, no se esta utilizando
        data.put("empresa", datos.get("nombre_empresa_emisora"));
        data.put("titulo_reporte", datos.get("titulo_reporte"));
        data.put("periodo", datos.get("periodo"));
        
        //datos para el pie de pagina
        data.put("codigo1", datos.get("codigo1"));
        data.put("codigo2", datos.get("codigo2"));
        
        this.setDatosHeaderFooter(data);
    }
    
    
    
    
    
    
    public void ViewPDF() throws URISyntaxException {
        HashMap<String, String> datos = new HashMap<String, String>();
        Font smallsmall = new Font(Font.FontFamily.HELVETICA,5,Font.NORMAL,BaseColor.BLACK);
        Font smallFont = new Font(Font.FontFamily.HELVETICA,7,Font.NORMAL,BaseColor.BLACK);
        Font smallFontBold = new Font(Font.FontFamily.HELVETICA,7,Font.BOLD,BaseColor.BLACK);
        Font smallBoldFontWhite = new Font(Font.FontFamily.HELVETICA,8,Font.BOLD,BaseColor.WHITE);
        Font largeBoldFont = new Font(Font.FontFamily.HELVETICA,10,Font.BOLD,BaseColor.BLACK);
        
        
        PdfPTable tablaPrincipal;
        PdfPTable table2;
        PdfPTable tableElaboro;
        PdfPCell cell;
        
        try {
            HeaderFooter event = new HeaderFooter(this.getDatosHeaderFooter());
            Document document = new Document(PageSize.LETTER, -50, -50, 60, 30);
           // Document document =      new Document(PageSize.LETTER.rotate(), -50, -50, 60, 30);
            PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(this.getFileout()));
            writer.setPageEvent(event);
            
            System.out.println("PDF: "+this.getFileout());
            
            document.open();
            
            
            
            
            float [] widths1 = {2f,4.8f,0.5f,2f,4.8f};
            PdfPTable tableHelper = new PdfPTable(widths1);
            
            //Fila 1
            cell = new PdfPCell(new Paragraph("Código:",smallFontBold));
            cell.setBorderWidthBottom(0);
            cell.setBorderWidthTop(0);
            cell.setBorderWidthRight(0);
            cell.setBorderWidthLeft(0);
            tableHelper.addCell(cell);
            
            cell = new PdfPCell(new Paragraph(this.getCodigo(),smallFont));
            cell.setBorderWidthBottom(1);
            cell.setBorderWidthTop(0);
            cell.setBorderWidthRight(0);
            cell.setBorderWidthLeft(0);
            tableHelper.addCell(cell);
            
            cell = new PdfPCell(new Paragraph("",smallFont));
            cell.setBorderWidthBottom(0);
            cell.setBorderWidthTop(0);
            cell.setBorderWidthRight(0);
            cell.setBorderWidthLeft(0);
            tableHelper.addCell(cell);
            
            cell = new PdfPCell(new Paragraph("Cantidad en Kilo:",smallFontBold));
            cell.setBorderWidthBottom(0);
            cell.setBorderWidthTop(0);
            cell.setBorderWidthRight(0);
            cell.setBorderWidthLeft(0);
            tableHelper.addCell(cell);
            
            cell = new PdfPCell(new Paragraph(this.getCantidad(),smallFont));
            cell.setBorderWidthBottom(1);
            cell.setBorderWidthTop(0);
            cell.setBorderWidthRight(0);
            cell.setBorderWidthLeft(0);
            tableHelper.addCell(cell);
            
            //Fila 2
            cell = new PdfPCell(new Paragraph("Descripción:",smallFontBold));
            cell.setBorderWidthBottom(0);
            cell.setBorderWidthTop(0);
            cell.setBorderWidthRight(0);
            cell.setBorderWidthLeft(0);
            tableHelper.addCell(cell);
            
            cell = new PdfPCell(new Paragraph(this.getDescripcion(),smallFont));
            cell.setBorderWidthBottom(1);
            cell.setBorderWidthTop(0);
            cell.setBorderWidthRight(0);
            cell.setBorderWidthLeft(0);
            tableHelper.addCell(cell);
            
            cell = new PdfPCell(new Paragraph("",smallFont));
            cell.setBorderWidthBottom(0);
            cell.setBorderWidthTop(0);
            cell.setBorderWidthRight(0);
            cell.setBorderWidthLeft(0);
            tableHelper.addCell(cell);
            
            cell = new PdfPCell(new Paragraph("Cantidad en Litro:",smallFontBold));
            cell.setBorderWidthBottom(0);
            cell.setBorderWidthTop(0);
            cell.setBorderWidthRight(0);
            cell.setBorderWidthLeft(0);
            tableHelper.addCell(cell);
            
            cell = new PdfPCell(new Paragraph(this.getCantidad_lt(),smallFont));
            cell.setBorderWidthBottom(1);
            cell.setBorderWidthTop(0);
            cell.setBorderWidthRight(0);
            cell.setBorderWidthLeft(0);
            tableHelper.addCell(cell);
            
            //Fila 3
            cell = new PdfPCell(new Paragraph("Densidad:",smallFontBold));
            cell.setBorderWidthBottom(0);
            cell.setBorderWidthTop(0);
            cell.setBorderWidthRight(0);
            cell.setBorderWidthLeft(0);
            tableHelper.addCell(cell);
            
            cell = new PdfPCell(new Paragraph(this.getDensidad(),smallFont));
            cell.setBorderWidthBottom(1);
            cell.setBorderWidthTop(0);
            cell.setBorderWidthRight(0);
            cell.setBorderWidthLeft(0);
            tableHelper.addCell(cell);
            
            cell = new PdfPCell(new Paragraph("",smallFont));
            cell.setBorderWidthBottom(0);
            cell.setBorderWidthTop(0);
            cell.setBorderWidthRight(0);
            cell.setBorderWidthLeft(0);
            tableHelper.addCell(cell);
            
            cell = new PdfPCell(new Paragraph("",smallFontBold));
            cell.setBorderWidthBottom(0);
            cell.setBorderWidthTop(0);
            cell.setBorderWidthRight(0);
            cell.setBorderWidthLeft(0);
            tableHelper.addCell(cell);
            
            cell = new PdfPCell(new Paragraph("",smallFont));
            cell.setBorderWidthBottom(0);
            cell.setBorderWidthTop(0);
            cell.setBorderWidthRight(0);
            cell.setBorderWidthLeft(0);
            tableHelper.addCell(cell);
            
            tableHelper.setSpacingAfter(10f);
            
            //Agregar tabla al documento
            document.add(tableHelper);
            
            
            
            
            
            
            
            
            //float [] widths = {2f, 5.5f, 3f, 2f, 1.5f, 2f,2f,2f,2f};
            float [] widths = {
                2.5f,     //codigo
                6f,   //descripcion
                3f,   //Unidad
                2f,   //Cantidad
                2f   //Descripcion
           };
            PdfPTable tablaComponentes = new PdfPTable(widths);
            
            Iterator it;
            
            tablaComponentes.setKeepTogether(false);
            tablaComponentes.setHeaderRows(1);
            
            cell = new PdfPCell(new Paragraph("Código",smallBoldFontWhite));
            cell.setUseAscender(true);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setUseDescender(true);
            cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            cell.setBackgroundColor(BaseColor.BLACK);
            tablaComponentes.addCell(cell);
            
            cell = new PdfPCell(new Paragraph("Descripción",smallBoldFontWhite));
            cell.setUseAscender(true);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setUseDescender(true);
            cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            cell.setBackgroundColor(BaseColor.BLACK);
            tablaComponentes.addCell(cell);
            
            
            cell = new PdfPCell(new Paragraph("Unidad",smallBoldFontWhite));
            cell.setUseAscender(true);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setUseDescender(true);
            cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            cell.setBackgroundColor(BaseColor.BLACK);
            tablaComponentes.addCell(cell);
            
            cell = new PdfPCell(new Paragraph("Cantidad",smallBoldFontWhite));
            cell.setUseAscender(true);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setUseDescender(true);
            cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            cell.setBackgroundColor(BaseColor.BLACK);
            tablaComponentes.addCell(cell);
            
            cell = new PdfPCell(new Paragraph("Existencia",smallBoldFontWhite));
            cell.setUseAscender(true);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setUseDescender(true);
            cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            cell.setBackgroundColor(BaseColor.BLACK);
            tablaComponentes.addCell(cell);
            
            it = this.getLista_componentes().iterator();
           while(it.hasNext()){
                HashMap<String,String> map = (HashMap<String,String>)it.next();
                
                cell = new PdfPCell(new Paragraph(esteAtributoSeDejoNulo(map.get("sku")), smallFont));
                cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                cell.setHorizontalAlignment(Element.ALIGN_LEFT);
                tablaComponentes.addCell(cell);
                
                String descripcion = map.get("descripcion");
                descripcion =  StringEscapeUtils.unescapeHtml(descripcion);
                cell = new PdfPCell(new Paragraph(StringHelper.capitalizaString(descripcion), smallFont));
                cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                cell.setHorizontalAlignment(Element.ALIGN_LEFT);
                tablaComponentes.addCell(cell);
                
                //cell = new PdfPCell(new Paragraph(StringHelper.capitalizaString(esteAtributoSeDejoNulo(map.get("titulo"))), smallFont));
                cell = new PdfPCell(new Paragraph("KILO", smallFont));
                cell.setHorizontalAlignment(Element.ALIGN_LEFT);
                cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                tablaComponentes.addCell(cell);
                
                Double cantidad = Double.parseDouble(map.get("cantidad"))/100;
                
                Double cantCalculada = Double.parseDouble(this.getCantidad()) * cantidad;
                
                String nuevaCantidad = StringHelper.AgregaComas(StringHelper.roundDouble(cantCalculada,4));
                
                //cantidad
                cell = new PdfPCell(new Paragraph(nuevaCantidad, smallFont));
                cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                tablaComponentes.addCell(cell);
                  
                //Existencia
                cell = new PdfPCell(new Paragraph(StringHelper.AgregaComas(map.get("existencia")), smallFont));
                cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                tablaComponentes.addCell(cell);
            } 
            
            cell = new PdfPCell(new Paragraph("", smallFont));
            cell.setBorderWidthBottom(0);
            cell.setBorderWidthTop(1);
            cell.setBorderWidthRight(0);
            cell.setBorderWidthLeft(0);
            cell.setColspan(5);
            tablaComponentes.addCell(cell);
            
            tablaComponentes.setSpacingAfter(12f);
            
            //Agregar tabla de componentes al documento
            document.add(tablaComponentes);
            
            
            
            
            
            
            
            float [] widths3 = {5,3.5f,5};
            tableElaboro = new PdfPTable(widths3);
            tableElaboro.setKeepTogether(true);
            
            //FILA 1
            cell = new PdfPCell(new Paragraph("ELABORÓ",smallFont));
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setVerticalAlignment(Element.ALIGN_TOP);
            cell.setBorder(0);
            cell.setColspan(3);
            tableElaboro.addCell(cell);
            
            
            //FILA 2
            cell = new PdfPCell(new Paragraph("",smallFont));
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setVerticalAlignment(Element.ALIGN_BOTTOM);
            cell.setFixedHeight(25);
            cell.setBorder(0);
            tableElaboro.addCell(cell);
            
            cell = new PdfPCell(new Paragraph(this.getUsuario_elaboracion().toUpperCase(),smallFont));
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setVerticalAlignment(Element.ALIGN_BOTTOM);
            cell.setFixedHeight(25);
            cell.setBorderWidthBottom(1);
            cell.setBorderWidthTop(0);
            cell.setBorderWidthRight(0);
            cell.setBorderWidthLeft(0);
            tableElaboro.addCell(cell);
            
            cell = new PdfPCell(new Paragraph("",smallFont));
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setVerticalAlignment(Element.ALIGN_BOTTOM);
            cell.setFixedHeight(25);
            cell.setBorder(0);
            tableElaboro.addCell(cell);
            
            //Agregar tableElaboro al documento
            document.add(tableElaboro);
            
            
            document.close();
        }
        catch (Exception e) {
                e.printStackTrace();
        }
    }
    
    
    
    public String esteAtributoSeDejoNulo(String atributo){
         return (atributo != null) ? (atributo) : new String();
    }
    
    
    
    
    public HashMap<String, String> getDatosHeaderFooter() {
        return datosHeaderFooter;
    }

    public void setDatosHeaderFooter(HashMap<String, String> datosHeaderFooter) {
        this.datosHeaderFooter = datosHeaderFooter;
    }


    public String getEmp_razon_social() {
        return emp_razon_social;
    }

    public void setEmp_razon_social(String emp_razon_social) {
        this.emp_razon_social = emp_razon_social;
    }

    public String getFileout() {
        return fileout;
    }

    public void setFileout(String fileout) {
        this.fileout = fileout;
    }

    public ArrayList<HashMap<String, String>> getLista_componentes() {
        return lista_componentes;
    }

    public void setLista_componentes(ArrayList<HashMap<String, String>> lista_componentes) {
        this.lista_componentes = lista_componentes;
    }

    public String getTipo_simulacion() {
        return tipo_simulacion;
    }

    public void setTipo_simulacion(String tipo_simulacion) {
        this.tipo_simulacion = tipo_simulacion;
    }
    
    public String getCantidad() {
        return cantidad;
    }

    public void setCantidad(String cantidad) {
        this.cantidad = cantidad;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }
    
    public String getUsuario_elaboracion() {
        return usuario_elaboracion;
    }

    public void setUsuario_elaboracion(String usuario_elaboracion) {
        this.usuario_elaboracion = usuario_elaboracion;
    }

    public String getCantidad_lt() {
        return cantidad_lt;
    }

    public void setCantidad_lt(String cantidad_lt) {
        this.cantidad_lt = cantidad_lt;
    }

    public String getDensidad() {
        return densidad;
    }

    public void setDensidad(String densidad) {
        this.densidad = densidad;
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
        
        
        /*A√±adimos una tabla con  una imagen del logo de megestiono y creamos la fuente para el documento, la imagen esta escalada para que no se muestre pixelada*/   
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
        
        /*añdimos pie de pagina, borde y mas propiedades*/
        @Override
        public void onEndPage(PdfWriter writer, Document document) {
            ColumnText.showTextAligned(writer.getDirectContent(),Element.ALIGN_CENTER, new Phrase(this.getEmpresa(),largeBoldFont),document.getPageSize().getWidth()/2, document.getPageSize().getTop() -25, 0);
            ColumnText.showTextAligned(writer.getDirectContent(),Element.ALIGN_CENTER, new Phrase(this.getTitulo_reporte(),largeBoldFont),document.getPageSize().getWidth()/2, document.getPageSize().getTop()-38, 0);
            
            
            ColumnText.showTextAligned(writer.getDirectContent(),Element.ALIGN_CENTER, new Phrase(this.getPeriodo(),largeFont),document.getPageSize().getWidth()/2, document.getPageSize().getTop()-50, 0);
            
            cb = writer.getDirectContent();
            float textBase = document.bottom() - 15;
            
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
