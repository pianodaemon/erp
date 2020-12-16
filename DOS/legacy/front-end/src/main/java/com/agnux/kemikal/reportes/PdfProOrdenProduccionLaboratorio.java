/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.agnux.kemikal.reportes;
import com.agnux.common.helpers.StringHelper;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map.Entry;

/**
 *
 * @author Ezequiel
 */
public class PdfProOrdenProduccionLaboratorio {
    
    public PdfProOrdenProduccionLaboratorio(HashMap<String, String> datosEncabezadoPie,String fileout,ArrayList<HashMap<String, Object>> lista,HashMap<String, String> datos, ArrayList<HashMap<String, Object>> productos_version) throws FileNotFoundException, DocumentException {
        
        Font smallFont = new Font(Font.FontFamily.HELVETICA,7,Font.NORMAL,BaseColor.BLACK);
        Font smallBoldFont = new Font(Font.FontFamily.HELVETICA, 8, Font.BOLD, BaseColor.WHITE);
        
        Font fuenteCont = new Font(Font.getFamily("ARIAL"),10,Font.NORMAL,BaseColor.BLACK);
        Font fuentenegrita = new Font(Font.getFamily("ARIAL"),8,Font.BOLD,BaseColor.BLACK);
        Font fuenteCont2 = new Font(Font.getFamily("ARIAL"),8,Font.NORMAL,BaseColor.BLACK);
        Font headerFont = new Font(Font.getFamily("ARIAL"),8,Font.BOLD,BaseColor.WHITE);
        
        String nombre_mes = datos.get("nombre_mes");
        String[] fC = datos.get("fecha").split("-");
        String fecha_ruta = fC[0]+"-"+nombre_mes+"-"+fC[2];
        
        //datos para el encabezado
        datos.put("empresa", datosEncabezadoPie.get("nombre_empresa_emisora"));
        datos.put("titulo_reporte", datosEncabezadoPie.get("titulo_reporte"));
        datos.put("periodo", "");
        
        //datos para el pie de pagina
        datos.put("codigo1", datosEncabezadoPie.get("codigo1"));
        datos.put("codigo2", datosEncabezadoPie.get("codigo2"));
        
        HeaderFooter event = new HeaderFooter(datos);
        //Document reporte = new Document(PageSize.LETTER.rotate(),-50,-50,60,30);
        Document reporte = new Document(PageSize.LETTER, -50, -50, 55, 30);
        PdfWriter writer = PdfWriter.getInstance(reporte, new FileOutputStream(fileout));
        writer.setPageEvent(event);
        
        try {
            
            reporte.open();
            
            
            float [] tam_tablax = {2.5f,0.6f,5.5f,0.5f,2f,3};
            PdfPTable tablaX = new PdfPTable(tam_tablax);
            PdfPCell celdaX;
            tablaX.setKeepTogether(false);
            
            /*Inicia construccion de fecha y de datos generales*/
            //Inicia fila 1
            //columna 1 fil1
            celdaX = new PdfPCell(new Paragraph("",fuentenegrita));
            celdaX.setHorizontalAlignment(Element.ALIGN_LEFT);
            celdaX.setVerticalAlignment(Element.ALIGN_MIDDLE);
            celdaX.setBorderWidthBottom(0);
            celdaX.setBorderWidthTop(0);
            celdaX.setBorderWidthRight(0);
            celdaX.setBorderWidthLeft(0);
            tablaX.addCell(celdaX);
            
            //columna 2 fil1
            celdaX = new PdfPCell(new Paragraph("",fuentenegrita));
            celdaX.setHorizontalAlignment(Element.ALIGN_LEFT);
            celdaX.setVerticalAlignment(Element.ALIGN_MIDDLE);
            celdaX.setBorderWidthBottom(0);
            celdaX.setBorderWidthTop(0);
            celdaX.setBorderWidthRight(0);
            celdaX.setColspan(3);
            celdaX.setBorderWidthLeft(0);
            tablaX.addCell(celdaX);
            
            
            //columna 4-6 fil1
            celdaX = new PdfPCell(new Paragraph("Folio: "+datos.get("folio"),fuentenegrita));
            celdaX.setBorderWidthBottom(1);
            celdaX.setBorderWidthTop(0);
            celdaX.setBorderWidthRight(0);
            celdaX.setColspan(2);
            celdaX.setBorderWidthLeft(0);
            tablaX.addCell(celdaX);
            //Termina fila 1
            
            //Inicia fila 2 ******* FECHA *************
            //columna 1-4 fill 2
            celdaX = new PdfPCell(new Paragraph("",fuentenegrita));
            celdaX.setHorizontalAlignment(Element.ALIGN_LEFT);
            celdaX.setVerticalAlignment(Element.ALIGN_MIDDLE);
            celdaX.setBorderWidthBottom(0);
            celdaX.setBorderWidthTop(0);
            celdaX.setBorderWidthRight(0);
            celdaX.setColspan(4);
            celdaX.setBorderWidthLeft(0);
            tablaX.addCell(celdaX);
            
            String fe[] = datos.get("fecha_elavorar").split("-");
            String fechaElab= fe[2]+"/"+fe[1]+"/"+fe[0];
            
            //columna 5-6 fill 2
            celdaX = new PdfPCell(new Paragraph("Fecha: "+fechaElab,fuentenegrita));
            celdaX.setBorderWidthBottom(1);
            celdaX.setBorderWidthTop(0);
            celdaX.setBorderWidthRight(0);
            celdaX.setColspan(2);
            celdaX.setBorderWidthLeft(0);
            tablaX.addCell(celdaX);
            
            
            //Inicia fila 3 Nombre de el producto
            //columna 1-2 fill 3
            celdaX = new PdfPCell(new Paragraph("Nombre del Diseño: ",fuentenegrita));
            celdaX.setHorizontalAlignment(Element.ALIGN_LEFT);
            celdaX.setVerticalAlignment(Element.ALIGN_MIDDLE);
            celdaX.setBorderWidthBottom(0);
            celdaX.setBorderWidthTop(0);
            celdaX.setBorderWidthRight(0);
            celdaX.setColspan(2);
            celdaX.setBorderWidthLeft(0);
            tablaX.addCell(celdaX);
            
            //columna 3-6 fill 3
            celdaX = new PdfPCell(new Paragraph(datos.get("sku")+"  "+datos.get("descripcion"),fuentenegrita));
            celdaX.setBorderWidthBottom(1);
            celdaX.setBorderWidthTop(0);
            celdaX.setBorderWidthRight(0);
            celdaX.setColspan(4);
            celdaX.setBorderWidthLeft(0);
            tablaX.addCell(celdaX);
            //Termina fila 3 Nombre de el producto
            
            //Inicia fila 4 Solicitante
            //columna 1-2 fill 4
            celdaX = new PdfPCell(new Paragraph("Solicitante: ",fuentenegrita));
            celdaX.setHorizontalAlignment(Element.ALIGN_LEFT);
            celdaX.setVerticalAlignment(Element.ALIGN_MIDDLE);
            celdaX.setBorderWidthBottom(0);
            celdaX.setBorderWidthTop(0);
            celdaX.setBorderWidthRight(0);
            celdaX.setColspan(2);
            celdaX.setBorderWidthLeft(0);
            tablaX.addCell(celdaX);
            
            //columna 3-6 fill 4
            celdaX = new PdfPCell(new Paragraph(datos.get("solicitante"),fuentenegrita));
            celdaX.setBorderWidthBottom(1);
            celdaX.setBorderWidthTop(0);
            celdaX.setBorderWidthRight(0);
            celdaX.setColspan(4);
            celdaX.setBorderWidthLeft(0);
            tablaX.addCell(celdaX);
            //Termina fila 4 Solicitante
            
            
            //Inicia fila 5 Ejecutivo de ventas
            //columna 1-2 fill 5
            celdaX = new PdfPCell(new Paragraph("Ejecutivo de ventas: ",fuentenegrita));
            celdaX.setHorizontalAlignment(Element.ALIGN_LEFT);
            celdaX.setVerticalAlignment(Element.ALIGN_MIDDLE);
            celdaX.setBorderWidthBottom(0);
            celdaX.setBorderWidthTop(0);
            celdaX.setBorderWidthRight(0);
            celdaX.setColspan(2);
            celdaX.setBorderWidthLeft(0);
            tablaX.addCell(celdaX);
            
            //columna 3-6 fill 5
            celdaX = new PdfPCell(new Paragraph(datos.get("vendedor"),fuentenegrita));
            celdaX.setBorderWidthBottom(1);
            celdaX.setBorderWidthTop(0);
            celdaX.setBorderWidthRight(0);
            celdaX.setColspan(4);
            celdaX.setBorderWidthLeft(0);
            tablaX.addCell(celdaX);
            //Termina fila 5 Ejecutivo de ventas
            
            //Inicia fila 6 Comentario
            //columna 1-2 fill 6
            celdaX = new PdfPCell(new Paragraph("Observaciones: ",fuentenegrita));
            celdaX.setHorizontalAlignment(Element.ALIGN_LEFT);
            celdaX.setVerticalAlignment(Element.ALIGN_MIDDLE);
            celdaX.setBorderWidthBottom(0);
            celdaX.setBorderWidthTop(0);
            celdaX.setBorderWidthRight(0);
            celdaX.setColspan(2);
            celdaX.setBorderWidthLeft(0);
            tablaX.addCell(celdaX);
            
            //columna 3-6 fill 6
            celdaX = new PdfPCell(new Paragraph(datos.get("observaciones"),fuentenegrita));
            celdaX.setBorderWidthBottom(1);
            celdaX.setBorderWidthTop(0);
            celdaX.setBorderWidthRight(0);
            celdaX.setColspan(4);
            celdaX.setBorderWidthLeft(0);
            tablaX.addCell(celdaX);
            //Termina fila 6 Comentario
            
            
            
            //Fila para los productos de la orden
            celdaX = new PdfPCell(new Paragraph("",fuentenegrita));
            celdaX.setHorizontalAlignment(Element.ALIGN_LEFT);
            celdaX.setVerticalAlignment(Element.ALIGN_MIDDLE);
            celdaX.setBorderWidthBottom(0);
            celdaX.setBorderWidthTop(0);
            celdaX.setBorderWidthRight(0);
            celdaX.setColspan(6);
            celdaX.setBorderWidthLeft(0);
            tablaX.addCell(celdaX);
            
            
            /*Agrega las materias primas al reporte*/
            TablaPDF tabla_tmp = new TablaPDF();
            celdaX = new PdfPCell(tabla_tmp.addProdContent(lista, "fineza1", productos_version));
            celdaX.setBorderWidthBottom(1);
            celdaX.setBorderWidthTop(0);
            celdaX.setBorderWidthRight(0);
            celdaX.setColspan(6);
            celdaX.setBorderWidthLeft(0);
            tablaX.addCell(celdaX);
            
            /*Terimna construccion de fecha y de datos generales*/
            
            celdaX = new PdfPCell(new Paragraph("Elaboro",fuentenegrita));
            celdaX.setBorderWidthTop(0);
            celdaX.setBorderWidthRight(0);
            celdaX.setColspan(6);
            celdaX.setBorderWidthLeft(0);
            tablaX.addCell(celdaX);
            
            
            //Inicia Puesto
            celdaX = new PdfPCell(new Paragraph("Puesto: ",fuentenegrita));
            celdaX.setHorizontalAlignment(Element.ALIGN_RIGHT);
            celdaX.setVerticalAlignment(Element.ALIGN_MIDDLE);
            celdaX.setBorderWidthBottom(0);
            celdaX.setBorderWidthTop(0);
            celdaX.setBorderWidthRight(0);
            celdaX.setColspan(2);
            celdaX.setBorderWidthLeft(0);
            tablaX.addCell(celdaX);
            
            //columna 3-6 fill 4
            celdaX = new PdfPCell(new Paragraph("",fuentenegrita));
            celdaX.setBorderWidthBottom(1);
            celdaX.setBorderWidthTop(0);
            celdaX.setBorderWidthRight(0);
            celdaX.setColspan(2);
            celdaX.setBorderWidthLeft(0);
            tablaX.addCell(celdaX);
            
            celdaX = new PdfPCell(new Paragraph("",fuentenegrita));
            celdaX.setBorderWidthTop(0);
            celdaX.setBorderWidthRight(0);
            celdaX.setColspan(2);
            celdaX.setBorderWidthLeft(0);
            tablaX.addCell(celdaX);
            //Termina Puesto
            
            //Inicia Nombre
            celdaX = new PdfPCell(new Paragraph("Nombre: ",fuentenegrita));
            celdaX.setHorizontalAlignment(Element.ALIGN_RIGHT);
            celdaX.setVerticalAlignment(Element.ALIGN_MIDDLE);
            celdaX.setBorderWidthBottom(0);
            celdaX.setBorderWidthTop(0);
            celdaX.setBorderWidthRight(0);
            celdaX.setColspan(2);
            celdaX.setBorderWidthLeft(0);
            tablaX.addCell(celdaX);
            
            //columna 3-6 fill 4
            celdaX = new PdfPCell(new Paragraph("",fuentenegrita));
            celdaX.setBorderWidthBottom(1);
            celdaX.setBorderWidthTop(0);
            celdaX.setBorderWidthRight(0);
            celdaX.setColspan(2);
            celdaX.setBorderWidthLeft(0);
            tablaX.addCell(celdaX);
            
            celdaX = new PdfPCell(new Paragraph("",fuentenegrita));
            celdaX.setBorderWidthTop(0);
            celdaX.setBorderWidthRight(0);
            celdaX.setColspan(2);
            celdaX.setBorderWidthLeft(0);
            tablaX.addCell(celdaX);
            //Termina Nombre
            
            
            
            //Inicia Firma
            celdaX = new PdfPCell(new Paragraph("Firma: ",fuentenegrita));
            celdaX.setHorizontalAlignment(Element.ALIGN_RIGHT);
            celdaX.setVerticalAlignment(Element.ALIGN_MIDDLE);
            celdaX.setBorderWidthBottom(0);
            celdaX.setBorderWidthTop(0);
            celdaX.setBorderWidthRight(0);
            celdaX.setColspan(2);
            celdaX.setBorderWidthLeft(0);
            tablaX.addCell(celdaX);
            
            //columna 3-6 fill 4
            celdaX = new PdfPCell(new Paragraph("",fuentenegrita));
            celdaX.setBorderWidthBottom(1);
            celdaX.setBorderWidthTop(0);
            celdaX.setBorderWidthRight(0);
            celdaX.setColspan(2);
            celdaX.setBorderWidthLeft(0);
            tablaX.addCell(celdaX);
            
            celdaX = new PdfPCell(new Paragraph("",fuentenegrita));
            celdaX.setBorderWidthTop(0);
            celdaX.setBorderWidthRight(0);
            celdaX.setColspan(2);
            celdaX.setBorderWidthLeft(0);
            tablaX.addCell(celdaX);
            //Termina Firma
            
            reporte.add(tablaX);
            
        }
        
        catch (Exception e){
             System.out.println(e.toString());
             }
        reporte.close();
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
        
        /*a√±adimos pie de p√°gina, borde y m√°s propiedades*/
        @Override
        public void onEndPage(PdfWriter writer, Document document) {
            
            ColumnText.showTextAligned(writer.getDirectContent(),Element.ALIGN_CENTER, new Phrase(this.getEmpresa(),largeBoldFont),document.getPageSize().getWidth()/2, document.getPageSize().getTop() -25, 0);
            ColumnText.showTextAligned(writer.getDirectContent(),Element.ALIGN_CENTER, new Phrase(this.getTitulo_reporte(),largeBoldFont),document.getPageSize().getWidth()/2, document.getPageSize().getTop()-38, 0);
            
            SimpleDateFormat formato = new SimpleDateFormat("'Generado el' d 'de' MMMMM 'del' yyyy 'a las' HH:mm:ss 'hrs.'");
            String impreso_en = formato.format(new Date());
            
            ColumnText.showTextAligned(writer.getDirectContent(),Element.ALIGN_CENTER, new Phrase(impreso_en,largeFont),document.getPageSize().getWidth()/2, document.getPageSize().getTop()-50, 0);
            
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
     
     
     
     
     
     /*Clase para agregar los resultados de analisis*/
     private class TablaPDF {
        
        /*Inicia clase apra agregar lo productos a al documento*/
        public PdfPTable addProdContent(ArrayList<HashMap<String, Object>> lista_productos, String cadena, ArrayList<HashMap<String, Object>>  productos_version) {
            
            Font smallFont = new Font(Font.FontFamily.HELVETICA,7,Font.NORMAL,BaseColor.BLACK);
            Font smallBoldFont = new Font(Font.FontFamily.HELVETICA, 8, Font.BOLD, BaseColor.WHITE);
            
            
            int cols=0;//Guarda la cantidad de columnas que se generaran de acuerdo a la cantidad de versiones de las formulas
            float with_total=6; //Guarda el ancho de el espacio que se usara para las columnas de las versiones
            float with_col=0;//Guarda el ancho de columnas que se generaran de acuerdo a la cantidad de versiones de las formulas
            int num_mp = 0;//guarda la cantidad de materias primas que zse usaron en las versiones
            int num_mp_tmp = 0;
            
            //System.out.println("antes de pdf 1");
            
            String version = "";
            for (int l=0;l<productos_version.size();l++){
                HashMap<String,Object> registro1 = productos_version.get(l);
                
                if(!version.equals(String.valueOf(registro1.get("version")))){
                    version = String.valueOf(registro1.get("version"));
                    cols++;
                    //System.out.println("antes de pdf "+String.valueOf(registro1.get("version")));
                    if(num_mp_tmp > num_mp){
                        num_mp = num_mp_tmp;//Guarda siempore la cantidad mayor de filas por cada materia prima de la formula
                    }
                    
                    num_mp_tmp = 0;
                }
                num_mp_tmp++;
            }
            System.out.println("antes de pdf 2   num_cols"+cols+"      with_total:"+with_total);
            
            with_col = (float)(6/cols);
            float tam_tablay[] = new float[(int)(2+cols+2)];
            
            //System.out.println("antes de pdf 3   cols"+cols+"      with_total:"+with_total);
            tam_tablay[0] = 1f;
            //System.out.println("antes de pdf 4   cols"+cols+"      with_total:"+with_total);
            tam_tablay[1] = 2f;
            
            //System.out.println("antes de pdf 2   with_total:"+with_total);
            for(int i = 1; i <= cols; i++){
                System.out.println("antes de pdf 2   with_total:"+i);
                tam_tablay[1+i] = with_col;
            }
            
            tam_tablay[1+cols+1] = 1f;
            tam_tablay[1+cols+2] = 1f;
            
            //System.out.println("Inicia lista d eproductos");
            
            for(int i = 0; i < tam_tablay.length; i++){
                //System.out.println("col: "+i+"="+tam_tablay[i]);
            }
            
            PdfPTable table = null;
            PdfPCell cell;
            
            table = new PdfPTable(tam_tablay);
            table.setKeepTogether(false);
            //table.setKeepTogether(false);
            //table.setHeaderRows(1);
            
            //System.out.println("Inicia lista d eproductos1");
            
            /*Inicia campos de materia prima*/
            cell = new PdfPCell(new Paragraph("Codigo",smallBoldFont));
            cell.setUseAscender(true);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setUseDescender(true);
            cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            cell.setBackgroundColor(BaseColor.BLACK);
            table.addCell(cell);
            
            cell = new PdfPCell(new Paragraph("Materia Prima",smallBoldFont));
            cell.setUseAscender(true);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setUseDescender(true);
            cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            cell.setBackgroundColor(BaseColor.BLACK);
            table.addCell(cell);
            
            //System.out.println("Inicia lista d eproductos2");
            
            for(int i = 1; i <= cols; i++){
                System.out.println("Inicia lista d eproductos i="+i);
                cell = new PdfPCell(new Paragraph(""+i,smallBoldFont));
                cell.setUseAscender(true);
                cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                cell.setUseDescender(true);
                cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                cell.setBackgroundColor(BaseColor.BLACK);
                table.addCell(cell);
            }
            
            System.out.println("Inicia lista d eproductos3");
            
            cell = new PdfPCell(new Paragraph("Suma",smallBoldFont));
            cell.setUseAscender(true);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setUseDescender(true);
            cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            cell.setBackgroundColor(BaseColor.BLACK);
            table.addCell(cell);
            
            cell = new PdfPCell(new Paragraph("%",smallBoldFont));
            cell.setUseAscender(true);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setUseDescender(true);
            cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            cell.setBackgroundColor(BaseColor.BLACK);            
            table.addCell(cell);
            
            //System.out.println("Inicia lista d eproductos4");
            
            String total = "";
            
            for (int l=0;l<lista_productos.size();l++){
                HashMap<String,Object> producto = lista_productos.get(l);
                
                //System.out.println("Inicia lista d eproductos l-"+l);
                
                cell = new PdfPCell(new Paragraph(String.valueOf(producto.get("sku")),smallFont));
                cell.setUseAscender(true);
                cell.setHorizontalAlignment(Element.ALIGN_LEFT);
                cell.setUseDescender(true);
                cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                table.addCell(cell);
                
                cell = new PdfPCell(new Paragraph(String.valueOf(producto.get("descripcion")),smallFont));
                cell.setUseAscender(true);
                cell.setHorizontalAlignment(Element.ALIGN_LEFT);
                cell.setUseDescender(true);
                cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                table.addCell(cell);
                
                
                String porciento = "";
                String cantidad_mp = "";
                
                
                for(int i = 1; i <= cols; i++){
                    
                    System.out.println("cols i="+i);
                    
                    int encontro = 0;
                    for (int j=0;j< productos_version.size();j++){
                        
                        HashMap<String,Object> producto_version = productos_version.get(j);
                        
                        if((String.valueOf(producto.get("inv_prod_id")).equals(String.valueOf(producto_version.get("inv_prod_id")))) 
                                && ( Integer.parseInt(producto_version.get("version").toString().split("\\.")[0]) ==  Integer.parseInt(String.valueOf(i))) ){
                            
                            
                            System.out.println(i+" - "+producto.get("inv_prod_id")+"=="+producto_version.get("inv_prod_id")+"  &&  "+Integer.parseInt(producto_version.get("version").toString().split("\\.")[0])+"=="+Integer.parseInt(String.valueOf(i))+"      Cant="+producto_version.get("cantidad_usada"));
                            
                            encontro = 1;
                            
                            //System.out.println("Inicia lista d eproductos version-"+i+"  ss");
                            
                            cell = new PdfPCell(new Paragraph(String.valueOf(producto_version.get("cantidad_usada")),smallFont));
                            cell.setUseAscender(true);
                            cell.setHorizontalAlignment(Element.ALIGN_LEFT);
                            cell.setUseDescender(true);
                            cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                            table.addCell(cell);
                            
                            porciento = StringHelper.roundDouble(String.valueOf(producto_version.get("cantidad_porciento")), 2);
                            cantidad_mp = StringHelper.roundDouble(String.valueOf(producto_version.get("cantidad_mp")), 2);
                            total = StringHelper.roundDouble(String.valueOf(producto_version.get("cantidad_total")), 2);
                        }
                    }
                    
                    if(encontro == 0){
                        System.out.println(i+" - "+"Nada: "+i);
                        cell = new PdfPCell(new Paragraph("",smallFont));
                        cell.setUseAscender(true);
                        cell.setHorizontalAlignment(Element.ALIGN_LEFT);
                        cell.setUseDescender(true);
                        cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                        table.addCell(cell);
                    }
                    
                }
                
                
                cell = new PdfPCell(new Paragraph(cantidad_mp,smallFont));
                cell.setUseAscender(true);
                cell.setHorizontalAlignment(Element.ALIGN_LEFT);
                cell.setUseDescender(true);
                cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                table.addCell(cell);
                
                cell = new PdfPCell(new Paragraph(porciento,smallFont));
                cell.setUseAscender(true);
                cell.setHorizontalAlignment(Element.ALIGN_LEFT);
                cell.setUseDescender(true);
                cell.setVerticalAlignment(Element.ALIGN_MIDDLE);     
                table.addCell(cell);
                
            }
            /*Termina campos de materia prima*/
            
            //System.out.println("Inicia lista d especificaciones");
            /*Inicia campos de especificaciones*/
            ArrayList<HashMap<String, String>> parametros = new ArrayList<HashMap<String, String>>();
            HashMap<String, String> fineza = new HashMap<String, String>();
            //System.out.println("Inicia lista d especificaciones Fineza");
            fineza.put("texto","Fineza");
            fineza.put("id","fineza_inicial");
            parametros.add(fineza);
            
            HashMap<String, String> viscosidad1 = new HashMap<String, String>();
            viscosidad1.put("texto","Viscosidad S");
            viscosidad1.put("id","viscosidads_inicial");
            parametros.add(viscosidad1);
            
            HashMap<String, String> viscosidad2 = new HashMap<String, String>();
            viscosidad2.put("texto","Viscosidad KU");
            viscosidad2.put("id","viscosidadku_inicial");
            parametros.add(viscosidad2);
            
            HashMap<String, String> viscosidad3 = new HashMap<String, String>();
            viscosidad3.put("texto","Viscosidad CPS");
            viscosidad3.put("id","viscosidadcps_inicial");
            parametros.add(viscosidad3);
            
            HashMap<String, String> densidad = new HashMap<String, String>();
            densidad.put("texto","Densidad (p)");
            densidad.put("id","densidad_inicial");
            parametros.add(densidad);
            
            HashMap<String, String> ph = new HashMap<String, String>();
            ph.put("texto","pH");
            ph.put("id","hidrogeno_inicial");
            parametros.add(ph);
            
            HashMap<String, String> volatiles = new HashMap<String, String>();
            volatiles.put("texto","% N V");
            volatiles.put("id","volatiles_inicial");
            parametros.add(volatiles);
            
            HashMap<String, String> cubriente = new HashMap<String, String>();
            cubriente.put("texto","Cubr vs Std");
            cubriente.put("id","cubriente_inicial");
            parametros.add(cubriente);
            
            HashMap<String, String> tono = new HashMap<String, String>();
            tono.put("texto","Tono vs Std (AE Max)");
            tono.put("id","tono_inicial");
            parametros.add(tono);
            
            HashMap<String, String> brillo = new HashMap<String, String>();
            brillo.put("texto","Brillo 60º");
            brillo.put("id","brillo_inicial");
            parametros.add(brillo);
            
            HashMap<String, String> dureza = new HashMap<String, String>();
            dureza.put("texto","Dureza");
            dureza.put("id","dureza_inicial");
            parametros.add(dureza);
            
            HashMap<String, String> aderencia = new HashMap<String, String>();
            aderencia.put("texto","Adherencia");
            aderencia.put("id","adherencia_inicial");
            parametros.add(aderencia);
            
            HashMap<String, String> otros = new HashMap<String, String>();
            otros.put("texto","Otros");
            otros.put("id","otros");
            parametros.add(otros);
            
            //System.out.println("Inicia lista d parametros size");
            for (int l=0;l< parametros.size();l++){
                
                HashMap<String,String> parametro = parametros.get(l);
                
                cell = new PdfPCell(new Paragraph(parametro.get("texto"),smallFont));
                cell.setUseAscender(true);
                cell.setHorizontalAlignment(Element.ALIGN_LEFT);
                cell.setUseDescender(true);
                cell.setColspan(2);
                cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                table.addCell(cell);
                
                
                for(int i = 1; i <= cols; i++){
                    //System.out.println("Inicia lista d parametros versiones");
                    int encontro = 0;
                    String row_param = "";
                    
                    for (int j=0;j< productos_version.size();j++){
                        
                        HashMap<String,Object> producto_version = productos_version.get(j);
                        
                        if(( Integer.parseInt(producto_version.get("version").toString().split("\\.")[0]) ==  Integer.parseInt(String.valueOf(i))) 
                                && (encontro == 0) ){
                            //System.out.println("Antes lista d parametros Entry");
                            for (Entry<String,Object> entry : producto_version.entrySet()) {
                                //System.out.println("producto_version key:"+entry.getKey());
                                
                                if (entry.getKey().equals(parametro.get("id").toString())) {
                                    row_param = entry.getKey();
                                    
                                    //System.out.println("Inicia lista d parametros versiones otros");
                                    if((!parametro.get("id").toString().equals("otros")) && row_param.equals(parametro.get("id").toString())){
                                        encontro = 1;
                                        cell = new PdfPCell(new Paragraph(String.valueOf(producto_version.get(parametro.get("id").toString())),smallFont));
                                        cell.setUseAscender(true);
                                        cell.setHorizontalAlignment(Element.ALIGN_LEFT);
                                        cell.setUseDescender(true);
                                        cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                                        table.addCell(cell);
                                        
                                    }
                                }
                            }
                            
                            /*
                            else{
                                cell = new PdfPCell(new Paragraph("",smallFont));
                                cell.setUseAscender(true);
                                cell.setHorizontalAlignment(Element.ALIGN_LEFT);
                                cell.setUseDescender(true);
                                cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                                table.addCell(cell);
                            }
                            */
                            
                        }
                    }
                    
                    if(encontro == 0){
                        
                        cell = new PdfPCell(new Paragraph("",smallFont));
                        cell.setUseAscender(true);
                        cell.setHorizontalAlignment(Element.ALIGN_LEFT);
                        cell.setUseDescender(true);
                        cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                        table.addCell(cell);

                    }
                    
                }
                
                cell = new PdfPCell(new Paragraph("",smallFont));
                cell.setUseAscender(true);
                cell.setHorizontalAlignment(Element.ALIGN_LEFT);
                cell.setUseDescender(true);
                cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                table.addCell(cell);
                
                cell = new PdfPCell(new Paragraph("",smallFont));
                cell.setUseAscender(true);
                cell.setHorizontalAlignment(Element.ALIGN_LEFT);
                cell.setUseDescender(true);
                cell.setVerticalAlignment(Element.ALIGN_MIDDLE);     
                table.addCell(cell);
            }
            
            
            /*Termina campos de especificaciones*/
            
            
            return table;
            
         }
         /*Termina clase apra agregar lo productos a al documento*/
         
    }
    
}
