package com.agnux.kemikal.reportes;

import com.agnux.common.helpers.StringHelper;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.ExceptionConverter;
import com.itextpdf.text.Font;
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
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Arrays;


public class PdfInvComprasNetasProducto {

    public PdfInvComprasNetasProducto(String fileout, String ruta_imagen, String razon_soc_empresa, String fechaInicial,String fechaFinal, List<HashMap<String, String>> lista_compras, Integer tipo_reporte) throws FileNotFoundException, DocumentException {
        String[] fi = fechaInicial.split("-");
        String[] ff = fechaFinal.split("-");
        String periodo_reporte = "Periodo  del  "+fi[2]+"/"+fi[1]+"/"+fi[0]+"  al  "+ff[2]+"/"+ff[1]+"/"+ff[0];
        
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
        
        
        float [] widths = {1.5f, 4f, 1.5f, 1.5f, 1f, 1.5f, 0.7f, 1.5f, 1f, 0.5f, 1.5f, 0.5f, 2f};

        PdfPTable table = new PdfPTable(widths);
        PdfPCell cell;
        
        table.setKeepTogether(false);
        table.setHeaderRows(1);
        
        String  header_clave_codigo="";
        String  header_producto_proveedor="";
        String  etiqueta="";
        
        if(tipo_reporte==1){
            header_clave_codigo="Clave";
            header_producto_proveedor="Proveedor";
            etiqueta = "Producto";
        }
        
        if(tipo_reporte==2){
            header_clave_codigo="Código";
            header_producto_proveedor="Producto";
            etiqueta = "Proveedor";
        }

        
        String[] columnasHeader = {header_clave_codigo,header_producto_proveedor,"Factura","Fecha","Unidad","Cantidad","Costo Unitario","Moneda","Tipo Cambio","SubTotal M.N." };
        List<String>  lista_columnas_header = (List<String>) Arrays.asList(columnasHeader);
        for ( String columna_titulo : lista_columnas_header){
            PdfPCell cellX = new PdfPCell(new Paragraph(columna_titulo,headerFont));
            cellX.setUseAscender(true);
            cellX.setUseDescender(true);
            cellX.setBackgroundColor(BaseColor.BLACK);
            
            if (columna_titulo.equals(header_clave_codigo)){
                cellX.setHorizontalAlignment(Element.ALIGN_LEFT);
                cellX.setVerticalAlignment(Element.ALIGN_MIDDLE);   
            }
            
            if (columna_titulo.equals(header_producto_proveedor)){
                cellX.setHorizontalAlignment(Element.ALIGN_LEFT);
                cellX.setVerticalAlignment(Element.ALIGN_MIDDLE);   
            }
            
            if (columna_titulo.equals("Factura")){
                cellX.setHorizontalAlignment(Element.ALIGN_LEFT);
                cellX.setVerticalAlignment(Element.ALIGN_MIDDLE);   
            }

            if (columna_titulo.equals("Fecha")){
                cellX.setHorizontalAlignment(Element.ALIGN_CENTER);
                cellX.setVerticalAlignment(Element.ALIGN_MIDDLE);   
            }

            if (columna_titulo.equals("Unidad")){
                cellX.setHorizontalAlignment(Element.ALIGN_CENTER);
                cellX.setVerticalAlignment(Element.ALIGN_MIDDLE);   
            }

            if (columna_titulo.equals("Cantidad")){
                cellX.setHorizontalAlignment(Element.ALIGN_CENTER);
                cellX.setVerticalAlignment(Element.ALIGN_MIDDLE);   
            }
            
            if (columna_titulo.equals("Costo Unitario")){
                cellX.setHorizontalAlignment(Element.ALIGN_CENTER);
                cellX.setVerticalAlignment(Element.ALIGN_MIDDLE);
                cellX.setColspan(2);
            }
            
            if (columna_titulo.equals("Moneda")){
                cellX.setHorizontalAlignment(Element.ALIGN_CENTER);
                cellX.setVerticalAlignment(Element.ALIGN_MIDDLE);   
            }
            
            if (columna_titulo.equals("Tipo Cambio")){
                cellX.setHorizontalAlignment(Element.ALIGN_CENTER);
                cellX.setVerticalAlignment(Element.ALIGN_MIDDLE);
                cellX.setColspan(2);
            }
            
            if (columna_titulo.equals("SubTotal M.N.")){
                cellX.setHorizontalAlignment(Element.ALIGN_CENTER);
                cellX.setVerticalAlignment(Element.ALIGN_MIDDLE);
                cellX.setColspan(2);
            }
            
            cellX.setFixedHeight(13);
            table.addCell(cellX);
        }
        
        
        
        
        String simbolo_moneda="";
        //esta variable toma el campo de ordenamiento dependiendo del tipo de reporte
        
        String campo_ordenamiento_actual="";
        
        //inicializar variables
        Double suma_parcial=0.0;//esta variable es para las sumas parciales, puede ser por producto o proveedor dependiento del tipo de reporte
        Double suma_general=0.0;
        
        if(lista_compras.size()>0){

            String campo_proveedor_producto_actual="";
            //estas dos variables indican el indice que debe tomar del arreglo dependiendo del timpo de reporte
            String campo_clave_codigo="";
            String campo_proveedor_producto="";
            String campo_comparador="";
            String valor_campo="";
            
            if(tipo_reporte==1){
                campo_proveedor_producto_actual=lista_compras.get(0).get("producto");
            }
            
            if(tipo_reporte==2){
                campo_proveedor_producto_actual=lista_compras.get(0).get("proveedor");
            }
            
            
            cell = new PdfPCell(new Paragraph(campo_proveedor_producto_actual,smallBoldFont));
            cell.setHorizontalAlignment(Element.ALIGN_LEFT);
            cell.setVerticalAlignment(Element.ALIGN_MIDDLE);   
            cell.setColspan(13);
            cell.setBorder(0);
            table.addCell(cell);            
            
            // Pintar los rows del Reporte
            for (HashMap<String, String> i : lista_compras){
                if(tipo_reporte==1){
                    campo_clave_codigo="clave_proveedor";
                    campo_proveedor_producto="proveedor";
                    campo_comparador="producto";
                }

                if(tipo_reporte==2){
                    campo_clave_codigo="codigo_producto";
                    campo_proveedor_producto="producto";
                    campo_comparador="proveedor";
                }
                

                if(i.get("moneda").equals("M.N.")){
                    simbolo_moneda="$";
                }else{
                    simbolo_moneda=i.get("moneda");
                }
                
                //Indices del HashMap que representa el row
                String[] wordList = {campo_clave_codigo,campo_proveedor_producto,"factura","fecha","unidad","cantidad","","costo_unitario","moneda","$","tipo_cambio","$","compra_neta_mn" };
                
                List<String>  indices = (List<String>) Arrays.asList(wordList);
                
                
                if(campo_proveedor_producto_actual.equals(i.get(campo_comparador))){
                
                    for (String omega : indices){
                        PdfPCell celda = null;

                        if (omega.equals(campo_clave_codigo)){
                            celda = new PdfPCell(new Paragraph(i.get(omega),smallFont));
                            celda.setHorizontalAlignment(Element.ALIGN_LEFT);
                            celda.setVerticalAlignment(Element.ALIGN_MIDDLE);
                        }

                        if (omega.equals(campo_proveedor_producto)){
                            if(i.get(omega).length() > 30 ){
                                valor_campo=i.get(omega).substring(0,30);
                            }else{
                                valor_campo=i.get(omega);
                            }
                            celda = new PdfPCell(new Paragraph(valor_campo,smallFont));
                            celda.setHorizontalAlignment(Element.ALIGN_LEFT);
                            celda.setVerticalAlignment(Element.ALIGN_MIDDLE);
                        }

                        if (omega.equals("factura")){
                            celda = new PdfPCell(new Paragraph(i.get(omega),smallFont));
                            celda.setHorizontalAlignment(Element.ALIGN_LEFT);
                            celda.setVerticalAlignment(Element.ALIGN_MIDDLE);
                        }

                        if (omega.equals("fecha")){
                            celda = new PdfPCell(new Paragraph(i.get(omega),smallFont));
                            celda.setHorizontalAlignment(Element.ALIGN_CENTER);
                            celda.setVerticalAlignment(Element.ALIGN_MIDDLE);
                        }

                        if (omega.equals("unidad")){
                            celda = new PdfPCell(new Paragraph(i.get(omega),smallFont));
                            celda.setHorizontalAlignment(Element.ALIGN_LEFT);
                            celda.setVerticalAlignment(Element.ALIGN_MIDDLE);
                        }

                        if (omega.equals("cantidad")){
                            celda = new PdfPCell(new Paragraph(StringHelper.AgregaComas(i.get(omega)),smallFont));
                            celda.setHorizontalAlignment(Element.ALIGN_RIGHT);
                            celda.setVerticalAlignment(Element.ALIGN_MIDDLE);
                        }

                        if (omega.equals("")){
                            celda = new PdfPCell(new Paragraph(simbolo_moneda,smallFont));
                            celda.setHorizontalAlignment(Element.ALIGN_RIGHT);
                            celda.setVerticalAlignment(Element.ALIGN_MIDDLE);
                        }

                        if (omega.equals("costo_unitario")){
                            celda = new PdfPCell(new Paragraph(StringHelper.AgregaComas(i.get(omega)),smallFont));
                            celda.setHorizontalAlignment(Element.ALIGN_RIGHT);
                            celda.setVerticalAlignment(Element.ALIGN_MIDDLE);
                        }                    
                        
                        if (omega.equals("moneda")){
                            celda = new PdfPCell(new Paragraph(i.get(omega),smallFont));
                            celda.setHorizontalAlignment(Element.ALIGN_CENTER);
                            celda.setVerticalAlignment(Element.ALIGN_MIDDLE);
                        }

                        if (omega.equals("$")){
                            celda = new PdfPCell(new Paragraph("$",smallFont));
                            celda.setHorizontalAlignment(Element.ALIGN_RIGHT);
                            celda.setVerticalAlignment(Element.ALIGN_MIDDLE);
                        }

                        if (omega.equals("tipo_cambio")){
                            celda = new PdfPCell(new Paragraph(StringHelper.AgregaComas(i.get(omega)),smallFont));
                            celda.setHorizontalAlignment(Element.ALIGN_RIGHT);
                            celda.setVerticalAlignment(Element.ALIGN_MIDDLE);
                        }

                        if (omega.equals("$")){
                            celda = new PdfPCell(new Paragraph("$",smallFont));
                            celda.setHorizontalAlignment(Element.ALIGN_RIGHT);
                            celda.setVerticalAlignment(Element.ALIGN_MIDDLE);
                        }

                        if (omega.equals("compra_neta_mn")){
                            celda = new PdfPCell(new Paragraph(StringHelper.AgregaComas(i.get(omega)),smallFont));
                            celda.setHorizontalAlignment(Element.ALIGN_RIGHT);
                            celda.setVerticalAlignment(Element.ALIGN_MIDDLE);
                        }

                        celda.setBorder(0);
                        table.addCell(celda);
                    } 
                    
                    suma_parcial=suma_parcial + Double.parseDouble(i.get("compra_neta_mn"));
                    suma_general=suma_general + Double.parseDouble(i.get("compra_neta_mn"));

                }else{
                    //imprimir totales
                    cell = new PdfPCell(new Paragraph("Total "+etiqueta,smallBoldFont));
                    cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                    cell.setVerticalAlignment(Element.ALIGN_MIDDLE);   
                    cell.setColspan(11);
                    cell.setBorder(0);
                    table.addCell(cell);                         

                    cell = new PdfPCell(new Paragraph("$",smallBoldFont));
                    cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                    cell.setVerticalAlignment(Element.ALIGN_MIDDLE);   
                    cell.setBorder(0);
                    table.addCell(cell);   
                    
                    cell = new PdfPCell(new Paragraph(StringHelper.AgregaComas(StringHelper.roundDouble(suma_parcial,2)),smallBoldFont));
                    cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                    cell.setVerticalAlignment(Element.ALIGN_MIDDLE);   
                    cell.setBorder(1);
                    table.addCell(cell);  
                    
                    //fila vacia
                    cell = new PdfPCell(new Paragraph("",smallBoldFont));
                    cell.setHorizontalAlignment(Element.ALIGN_LEFT);
                    cell.setVerticalAlignment(Element.ALIGN_MIDDLE);   
                    cell.setColspan(13);
                    cell.setBorder(0);
                    table.addCell(cell);                    
                    
                    //reinicializar varibles
                    suma_parcial=0.0;
                    
                    //tomar razon social del proveedor o descripcion del producto, dependiendo del timpo de reporte
                    campo_proveedor_producto_actual = i.get(campo_comparador);
						

                    cell = new PdfPCell(new Paragraph(campo_proveedor_producto_actual,smallBoldFont));
                    cell.setHorizontalAlignment(Element.ALIGN_LEFT);
                    cell.setVerticalAlignment(Element.ALIGN_MIDDLE);   
                    cell.setColspan(13);
                    cell.setBorder(0);
                    table.addCell(cell);            
            
                    for (String omega : indices){
                        PdfPCell celda = null;

                        if (omega.equals(campo_clave_codigo)){
                            celda = new PdfPCell(new Paragraph(i.get(omega),smallFont));
                            celda.setHorizontalAlignment(Element.ALIGN_LEFT);
                            celda.setVerticalAlignment(Element.ALIGN_MIDDLE);
                        }

                        if (omega.equals(campo_proveedor_producto)){
                            if(i.get(omega).length() > 30 ){
                                valor_campo=i.get(omega).substring(0,30);
                            }else{
                                valor_campo=i.get(omega);
                            }
                            celda = new PdfPCell(new Paragraph(valor_campo,smallFont));
                            celda.setHorizontalAlignment(Element.ALIGN_LEFT);
                            celda.setVerticalAlignment(Element.ALIGN_MIDDLE);
                        }

                        if (omega.equals("factura")){
                            celda = new PdfPCell(new Paragraph(i.get(omega),smallFont));
                            celda.setHorizontalAlignment(Element.ALIGN_LEFT);
                            celda.setVerticalAlignment(Element.ALIGN_MIDDLE);
                        }

                        if (omega.equals("fecha")){
                            celda = new PdfPCell(new Paragraph(i.get(omega),smallFont));
                            celda.setHorizontalAlignment(Element.ALIGN_CENTER);
                            celda.setVerticalAlignment(Element.ALIGN_MIDDLE);
                        }

                        if (omega.equals("unidad")){
                            celda = new PdfPCell(new Paragraph(i.get(omega),smallFont));
                            celda.setHorizontalAlignment(Element.ALIGN_LEFT);
                            celda.setVerticalAlignment(Element.ALIGN_MIDDLE);
                        }

                        if (omega.equals("cantidad")){
                            celda = new PdfPCell(new Paragraph(StringHelper.AgregaComas(i.get(omega)),smallFont));
                            celda.setHorizontalAlignment(Element.ALIGN_RIGHT);
                            celda.setVerticalAlignment(Element.ALIGN_MIDDLE);
                        }

                        if (omega.equals("")){
                            celda = new PdfPCell(new Paragraph(simbolo_moneda,smallFont));
                            celda.setHorizontalAlignment(Element.ALIGN_RIGHT);
                            celda.setVerticalAlignment(Element.ALIGN_MIDDLE);
                        }

                        if (omega.equals("costo_unitario")){
                            celda = new PdfPCell(new Paragraph(StringHelper.AgregaComas(i.get(omega)),smallFont));
                            celda.setHorizontalAlignment(Element.ALIGN_RIGHT);
                            celda.setVerticalAlignment(Element.ALIGN_MIDDLE);
                        }                    
                        
                        if (omega.equals("moneda")){
                            celda = new PdfPCell(new Paragraph(i.get(omega),smallFont));
                            celda.setHorizontalAlignment(Element.ALIGN_CENTER);
                            celda.setVerticalAlignment(Element.ALIGN_MIDDLE);
                        }

                        if (omega.equals("$")){
                            celda = new PdfPCell(new Paragraph("$",smallFont));
                            celda.setHorizontalAlignment(Element.ALIGN_RIGHT);
                            celda.setVerticalAlignment(Element.ALIGN_MIDDLE);
                        }

                        if (omega.equals("tipo_cambio")){
                            celda = new PdfPCell(new Paragraph(StringHelper.AgregaComas(i.get(omega)),smallFont));
                            celda.setHorizontalAlignment(Element.ALIGN_RIGHT);
                            celda.setVerticalAlignment(Element.ALIGN_MIDDLE);
                        }

                        if (omega.equals("$")){
                            celda = new PdfPCell(new Paragraph("$",smallFont));
                            celda.setHorizontalAlignment(Element.ALIGN_RIGHT);
                            celda.setVerticalAlignment(Element.ALIGN_MIDDLE);
                        }

                        if (omega.equals("compra_neta_mn")){
                            celda = new PdfPCell(new Paragraph(StringHelper.AgregaComas(i.get(omega)),smallFont));
                            celda.setHorizontalAlignment(Element.ALIGN_RIGHT);
                            celda.setVerticalAlignment(Element.ALIGN_MIDDLE);
                        }

                        celda.setBorder(0);
                        table.addCell(celda);
                    } 
                    
                    
                    suma_parcial=suma_parcial + Double.parseDouble(i.get("compra_neta_mn"));
                    suma_general=suma_general + Double.parseDouble(i.get("compra_neta_mn"));                    
                }
                    
            }
            
            //imprimir totales del ultimo producto o proveedor
            cell = new PdfPCell(new Paragraph("Total "+etiqueta,smallBoldFont));
            cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            cell.setVerticalAlignment(Element.ALIGN_MIDDLE);   
            cell.setColspan(11);
            cell.setBorder(0);
            table.addCell(cell);                         
            
            cell = new PdfPCell(new Paragraph("$",smallBoldFont));
            cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            cell.setVerticalAlignment(Element.ALIGN_MIDDLE);   
            cell.setBorder(0);
            table.addCell(cell);   
            
            cell = new PdfPCell(new Paragraph(StringHelper.AgregaComas(StringHelper.roundDouble(suma_parcial,2)),smallBoldFont));
            cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            cell.setVerticalAlignment(Element.ALIGN_MIDDLE);   
            cell.setBorder(1);
            table.addCell(cell);  
            
            
                    
            //fila vacia para separar total general
            cell = new PdfPCell(new Paragraph("",smallBoldFont));
            cell.setHorizontalAlignment(Element.ALIGN_LEFT);
            cell.setVerticalAlignment(Element.ALIGN_MIDDLE);   
            cell.setColspan(13);
            cell.setBorder(0);
            cell.setFixedHeight(20);
            table.addCell(cell);   
            
            
            //imprimir total general
            cell = new PdfPCell(new Paragraph("Total General en M.N.",smallBoldFont));
            cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            cell.setVerticalAlignment(Element.ALIGN_MIDDLE);   
            cell.setColspan(11);
            cell.setBorder(0);
            table.addCell(cell);                         

            cell = new PdfPCell(new Paragraph("$",smallBoldFont));
            cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            cell.setVerticalAlignment(Element.ALIGN_MIDDLE);   
            cell.setBorder(0);
            table.addCell(cell);   

            cell = new PdfPCell(new Paragraph(StringHelper.AgregaComas(StringHelper.roundDouble(suma_general,2)),smallBoldFont));
            cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            cell.setVerticalAlignment(Element.ALIGN_MIDDLE);   
            cell.setBorder(1);
            table.addCell(cell);  

            
            
        }else{
            PdfPCell cellX = new PdfPCell(new Paragraph("No hay resultados que mostrar, seleccione otro periodo.",smallBoldFont));
            cellX.setHorizontalAlignment(Element.ALIGN_LEFT);
            cellX.setVerticalAlignment(Element.ALIGN_MIDDLE);   
            cellX.setColspan(13);
            table.addCell(cellX);
        }
        
        doc.add(table);
        doc.close();
            
    }//termina constructor
    
    
    
    
    
    
    
    static class HeaderFooter extends PdfPageEventHelper {
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
            ColumnText.showTextAligned(writer.getDirectContent(),Element.ALIGN_CENTER, new Phrase(this.getEmpresa(),largeBoldFont),document.getPageSize().getWidth()/2, document.getPageSize().getTop() -25, 0);
            ColumnText.showTextAligned(writer.getDirectContent(),Element.ALIGN_CENTER, new Phrase("Reporte de Compras Netas por Producto",largeBoldFont),document.getPageSize().getWidth()/2, document.getPageSize().getTop()-38, 0);
            ColumnText.showTextAligned(writer.getDirectContent(),Element.ALIGN_CENTER, new Phrase(this.getPeriodo(),largeFont),document.getPageSize().getWidth()/2, document.getPageSize().getTop()-50, 0);
            
            SimpleDateFormat formato = new SimpleDateFormat("'Impreso en' MMMMM d, yyyy 'a las' HH:mm:ss 'hrs.'");
            String impreso_en = formato.format(new Date());
            
            cb = writer.getDirectContent();
            
            cb.beginText();  
            cb.setFontAndSize(helv, 7);  
            cb.setTextMatrix(document.left()+75, document.bottom() - 20 );  //definir la posicion de text
            cb.showText(impreso_en);
            cb.endText();
            
            //cb.saveState();
            String text = "Página " + writer.getPageNumber() + " de ";  
            float textBase = document.bottom() - 20;
            float textSize = helv.getWidthPoint(text, 7);  
            float adjust = helv.getWidthPoint("0", 150);  
            cb.beginText();  
            cb.setFontAndSize(helv, 7);  
            cb.setTextMatrix(document.right() - 128, textBase);  
            cb.showText(text);  
            cb.endText();  
            cb.addTemplate(total, document.right() - adjust , textBase);
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
