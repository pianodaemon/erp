/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.agnux.kemikal.reportes;

import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfPageEventHelper;
import com.itextpdf.text.pdf.PdfTemplate;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.Element;
import com.itextpdf.text.ExceptionConverter;
import com.itextpdf.text.Font;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.ColumnText;
import java.io.File;
import java.io.FileOutputStream;
import java.util.Arrays;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import com.agnux.common.helpers.StringHelper;
import com.agnux.common.helpers.TimeHelper;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
/**
 *
 * @author agnux
 */
public class PdfReporteAuxiliarDeCuentas {
    public String empresa_emisora;
    public List<HashMap<String, String>> rows;
    HashMap<String, String> datosEmp;
    public File archivoSalida;

    public File getArchivoSalida() {
        return archivoSalida;
    }

    public void setArchivoSalida(File archivoSalida) {
        this.archivoSalida = archivoSalida;
    }

    public List<HashMap<String, String>> getRows() {
        return rows;
    }

    public void setRows(List<HashMap<String, String>> rows) {
        this.rows = rows;
    }


    public String getFechaB() {
        return fechaB;
    }

    public void setFechaB(String fechaB) {
        this.fechaB = fechaB;
    }
    public String fechaB;
    
    
    public String getEmpresa_emisora() {
        return empresa_emisora;
    }

    public void setEmpresa_emisora(String empresa_emisora) {
        this.empresa_emisora = empresa_emisora;
    }
    
    public HashMap<String, String> getDatosEmp() {
        return datosEmp;
    }

    public void setDatosEmp(HashMap<String, String> datosEmp) {
        this.datosEmp = datosEmp;
    }
    
    public PdfReporteAuxiliarDeCuentas(String fileout,List<HashMap<String, String>> itemsForPrinting, HashMap<String, String> datosEmpresa){
        this.setArchivoSalida(new File(fileout));
        this.setEmpresa_emisora(datosEmpresa.get("razon_soc_empresa").toUpperCase());
        this.setRows(itemsForPrinting);
        this.setDatosEmp(datosEmpresa);
        this.init();
    }
    
    void init(){
        
        Font smallsmall = new Font(Font.getFamily("ARIAL"),13,Font.NORMAL);
        Font smallBoldFontWhite = new Font(Font.getFamily("ARIAL"),9,Font.BOLD,BaseColor.WHITE);
        Font smallFontBlack = new Font(Font.getFamily("ARIAL"),9,Font.NORMAL,BaseColor.BLACK);
        
        
        float [] ancho_columnas = {2.5f, 6f, 1.5f,1.5f, 1.5f, 1.5f};
        
        Font largeBoldFont = new Font(Font.FontFamily.HELVETICA,10,Font.BOLD,BaseColor.BLACK);
        Font smallFont = new Font(Font.FontFamily.HELVETICA,8,Font.NORMAL,BaseColor.BLACK);
        
        HeaderFooter event = new HeaderFooter(this.getEmpresa_emisora());
        Document doc = new Document(PageSize.LETTER.rotate(),-50,-50,60,30);
        doc.addCreator("eplauchu@agnux.com");
        PdfWriter writer = null;
        try {
            
            writer = PdfWriter.getInstance(doc, new FileOutputStream(this.getArchivoSalida()));
            writer.setPageEvent(event);
            
            doc.open();
            
            int numHeaderRows = 3;
            
            //Se declara la tabla y se establecen la configuraciones para la misma
            PdfPTable table = new PdfPTable(ancho_columnas);
            table.setKeepTogether(false);
            table.setHeaderRows(numHeaderRows);
            

            
            String[] columnas = {"direccion","vacio","etiqueta","dato"};
            List<String>  lista_columnas = (List<String>) Arrays.asList(columnas);
            for ( String columna_titulo : lista_columnas){
                String dato="";
                int colspan=1;
                if (columna_titulo.equals("direccion")){
                    dato = this.getDatosEmp().get("calle")+" "+this.getDatosEmp().get("numero")+", "+this.getDatosEmp().get("colonia");
                    colspan = 3;
                }
                
                if (columna_titulo.equals("etiqueta")){
                    dato = "     RFC:";
                }
                
                if (columna_titulo.equals("dato")){
                    dato = this.getDatosEmp().get("rfc");
                }
                
                PdfPCell cellX = new PdfPCell(new Paragraph(dato,smallFontBlack));
                cellX.setHorizontalAlignment(Element.ALIGN_LEFT);
                cellX.setVerticalAlignment(Element.ALIGN_MIDDLE);
                cellX.setUseAscender(true);
                cellX.setUseDescender(true);
                cellX.setFixedHeight(11);
                cellX.setBorder(0);
                cellX.setColspan(colspan);
                table.addCell(cellX);
            }
            
        
            for ( String columna_titulo : lista_columnas){
                String dato="";
                int colspan=1;
                if (columna_titulo.equals("direccion")){
                    dato = this.getDatosEmp().get("municipio")+", "+this.getDatosEmp().get("estado")+", "+this.getDatosEmp().get("pais")+", C.P."+this.getDatosEmp().get("cp");
                    colspan = 3;
                }
                
                if (columna_titulo.equals("etiqueta")){
                    dato = "     Reg. Edo.";
                }
                
                if (columna_titulo.equals("dato")){
                    dato = this.getDatosEmp().get("regedo");
                }
                
                PdfPCell cellX = new PdfPCell(new Paragraph(dato,smallFontBlack));
                cellX.setHorizontalAlignment(Element.ALIGN_LEFT);
                cellX.setVerticalAlignment(Element.ALIGN_TOP);
                cellX.setUseAscender(true);
                cellX.setUseDescender(true);
                cellX.setFixedHeight(22);
                cellX.setBorder(0);
                cellX.setColspan(colspan);
                table.addCell(cellX);
            }
            
            
            String[] columnasHeader = {"Cuenta","Descripcion","Saldo Inicial","Debe","Haber","Saldo Final"};
            List<String>  lista_columnas_header = (List<String>) Arrays.asList(columnasHeader);
            for ( String columna_titulo : lista_columnas_header){
                PdfPCell cellX = new PdfPCell(new Paragraph(columna_titulo,smallBoldFontWhite));
                cellX.setUseAscender(true);
                cellX.setUseDescender(true);
                cellX.setBackgroundColor(BaseColor.BLACK);

                if (columna_titulo.equals("Cuenta")){
                    cellX.setHorizontalAlignment(Element.ALIGN_CENTER);
                    cellX.setVerticalAlignment(Element.ALIGN_MIDDLE);   
                }

                if (columna_titulo.equals("Descripcion")){
                    cellX.setHorizontalAlignment(Element.ALIGN_CENTER);
                    cellX.setVerticalAlignment(Element.ALIGN_MIDDLE);   
                }

                if (columna_titulo.equals("Saldo Inicial")){
                    cellX.setHorizontalAlignment(Element.ALIGN_CENTER);
                    cellX.setVerticalAlignment(Element.ALIGN_MIDDLE);   
                }

                if (columna_titulo.equals("Debe")){
                    cellX.setHorizontalAlignment(Element.ALIGN_CENTER);
                    cellX.setVerticalAlignment(Element.ALIGN_MIDDLE);   
                }

                if (columna_titulo.equals("Haber")){
                    cellX.setHorizontalAlignment(Element.ALIGN_CENTER);
                    cellX.setVerticalAlignment(Element.ALIGN_MIDDLE);   
                }

                if (columna_titulo.equals("Saldo Final")){
                    cellX.setHorizontalAlignment(Element.ALIGN_CENTER);
                    cellX.setVerticalAlignment(Element.ALIGN_MIDDLE);   
                }

                cellX.setFixedHeight(13);
                table.addCell(cellX);
            }
            
            
            
            // Pintar los rows del Reporte
            for (HashMap<String, String> i : this.getRows()){
                //Indices del HashMap que representa el row
                String[] wordList = {"cuenta","descripcion","saldo_inicial","debe","haber","saldo_final"};
                
                List<String>  indices = (List<String>) Arrays.asList(wordList);

                for (String omega : indices){
                    PdfPCell celda = null;

                    if (omega.equals("cuenta")){
                        celda = new PdfPCell(new Paragraph(i.get(omega),smallFont));
                        celda.setHorizontalAlignment(Element.ALIGN_LEFT);
                        celda.setVerticalAlignment(Element.ALIGN_MIDDLE);
                    }

                    if (omega.equals("descripcion")){
                        celda = new PdfPCell(new Paragraph(i.get(omega),smallFont));
                        celda.setHorizontalAlignment(Element.ALIGN_LEFT);
                        celda.setVerticalAlignment(Element.ALIGN_MIDDLE);
                    }

                    if (omega.equals("saldo_inicial")){
                        celda = new PdfPCell(new Paragraph(StringHelper.AgregaComas(i.get(omega)),smallFont));
                        celda.setHorizontalAlignment(Element.ALIGN_RIGHT);
                        celda.setVerticalAlignment(Element.ALIGN_MIDDLE);
                    }

                    if (omega.equals("debe")){
                        celda = new PdfPCell(new Paragraph(StringHelper.AgregaComas(i.get(omega)),smallFont));
                        celda.setHorizontalAlignment(Element.ALIGN_RIGHT);
                        celda.setVerticalAlignment(Element.ALIGN_MIDDLE);
                    }


                    if (omega.equals("haber")){
                        celda = new PdfPCell(new Paragraph(StringHelper.AgregaComas(i.get(omega)),smallFont));
                        celda.setHorizontalAlignment(Element.ALIGN_RIGHT);
                        celda.setVerticalAlignment(Element.ALIGN_MIDDLE);
                    }

                    if (omega.equals("saldo_final")){
                        celda = new PdfPCell(new Paragraph(StringHelper.AgregaComas(i.get(omega)),smallFont));
                        celda.setHorizontalAlignment(Element.ALIGN_RIGHT);
                        celda.setVerticalAlignment(Element.ALIGN_MIDDLE);
                    }
                    
                    celda.setBorder(0);
                    table.addCell(celda);
                }  
                
            }
            
            doc.add(table);   
            doc.close();
        } catch (Exception ex) {
            Logger.getLogger(PdfReporteAuxiliarDeCuentas.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        
        
    }
    
    
    public final class HeaderFooter extends PdfPageEventHelper {
        
        protected String emp_emisora;
        protected PdfTemplate total;       
        protected BaseFont helv;  
        protected PdfContentByte cb;

        Font largeBoldFont = new Font(Font.FontFamily.HELVETICA,10,Font.BOLD,BaseColor.BLACK);
        Font largeFont = new Font(Font.FontFamily.HELVETICA,10,Font.NORMAL,BaseColor.BLACK);
        Font smallFont = new Font(Font.FontFamily.HELVETICA,7,Font.NORMAL,BaseColor.BLACK);

        HeaderFooter(String emp_emisora){
            this.setEmp_emisora(emp_emisora);
        }
        
        public PdfContentByte getCb() {
            return cb;
        }

        public void setCb(PdfContentByte cb) {
            this.cb = cb;
        }
        
        public String getEmp_emisora() {
            return emp_emisora;
        }
        
        public void setEmp_emisora(String emp_emisora) {
            this.emp_emisora = emp_emisora;
        }
        
        
        @Override
        public void onOpenDocument(PdfWriter writer, Document document) {
            try {
                total = writer.getDirectContent().createTemplate(100, 100);  
                total.setBoundingBox(new Rectangle(-20, -20, 100, 100));
                total.fill();
                helv = BaseFont.createFont("Helvetica", BaseFont.WINANSI, false);
            }
            catch(Exception e) {
                throw new ExceptionConverter(e);
            }
            
        }
        
        @Override
        public void onEndPage(PdfWriter writer, Document document) {
            ColumnText.showTextAligned(writer.getDirectContent(),Element.ALIGN_CENTER, new Phrase(this.getEmp_emisora(),largeBoldFont),document.getPageSize().getWidth()/2, document.getPageSize().getTop() -25, 0);
            ColumnText.showTextAligned(writer.getDirectContent(),Element.ALIGN_CENTER, new Phrase("Auxiliar de Cuentas",largeBoldFont),document.getPageSize().getWidth()/2, document.getPageSize().getTop()-38, 0);
            
            SimpleDateFormat formato = new SimpleDateFormat("'a las' HH:mm:ss 'hrs.'");
            String hora_generacion = formato.format(new Date());
            
            ColumnText.showTextAligned(writer.getDirectContent(),Element.ALIGN_CENTER, new Phrase("Generado el dia" + " " + TimeHelper.convertirDatesToString(new Date()) + " " + hora_generacion ,largeFont),document.getPageSize().getWidth()/2, document.getPageSize().getTop()-50, 0);
            
            /*SimpleDateFormat formato = new SimpleDateFormat("'Generado en' MMMMM d, yyyy 'a las' HH:mm:ss 'hrs.'");
            String impreso_en = formato.format(new Date());
            */
            this.setCb(writer.getDirectContent());  
            
            /*this.getCb().beginText();  
            this.getCb().setFontAndSize(helv, 7);  
            this.getCb().setTextMatrix(document.left()+75, document.bottom() - 20 );  //definir la posicion de text
            this.getCb().showText(impreso_en);
            this.getCb().endText();
            */
            
            //cb.saveState();
            String text = "Página " + writer.getPageNumber() + " de ";  
            float textBase = document.bottom() - 20;
            float textSize = helv.getWidthPoint(text, 7);  
            float adjust = helv.getWidthPoint("0", 150);  
            this.getCb().beginText();  
            this.getCb().setFontAndSize(helv, 7);  
            this.getCb().setTextMatrix(document.right() - 128, textBase);  
            this.getCb().showText(text);  
            this.getCb().endText();  
            this.getCb().addTemplate(total, document.right() - adjust , textBase);
            
        }
        
        
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
