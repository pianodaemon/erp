/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.agnux.kemikal.reportes;

import com.agnux.common.helpers.BarcodeHelper;
import com.agnux.common.helpers.FileHelper;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Image;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

/*
 @author mi_compu
 */

public class PdfEtiqueta_Compras {
    private String imagen;
    public File archivoSalida;
    public String datos_empresa;
    public String datos_tipo_etiqueta;
    public List<HashMap<String, String>> rows;

    public String getDatos_tipo_etiqueta() {
        return datos_tipo_etiqueta;
    }

    public void setDatos_tipo_etiqueta(String datos_tipo_etiqueta) {
        this.datos_tipo_etiqueta = datos_tipo_etiqueta;
    }

    public String getDatos_empresa() {
        return datos_empresa;
    }

    public void setDatos_empresa(String datos_empresa) {
        this.datos_empresa = datos_empresa;
    }
    
     public String getImagen() {
        return imagen;
    }

    public void setImagen(String imagen) {
        this.imagen = imagen;
    }
    
    
    public List<HashMap<String, String>> getRows() {
        return rows;
    }

    public void setRows(List<HashMap<String, String>> rows) {
        this.rows = rows;
    }
    
    public File getArchivoSalida() {
        return archivoSalida;
    }

    public void setArchivoSalida(File archivoSalida) {
        this.archivoSalida = archivoSalida;
    }
    
    
    
    
    private class ImagenPDF {
        public Image addContent() {
            Image img = null;
            try {
                img = Image.getInstance(getImagen());
                //img.scaleAbsoluteHeight(100);
                img.scaleAbsoluteHeight(65);
                img.scaleAbsoluteWidth(120);
                img.setAlignment(0);
            }
            catch(Exception e){
                System.out.println(e);
            }
            return img;
        }
        
        public Image addContentBarCode(String url_barcode){
            Image img = null;
            try {
                img = Image.getInstance(url_barcode);
                //img.scaleAbsoluteHeight(100);
                img.scaleAbsoluteHeight(13);
                img.scaleAbsoluteWidth(120);
                
                img.setAlignment(0);
            }
            catch(Exception e){
                System.out.println(e);
            }
            return img;
        }
    }
    
    private class EncabezadoPDF {
        public PdfPTable addContent(String cadena) {
            Font smallFont = new Font(Font.FontFamily.HELVETICA,7,Font.NORMAL,BaseColor.BLACK);
            Font sont = new Font(Font.FontFamily.HELVETICA,8,Font.BOLD,BaseColor.BLACK);
            Font smallBoldFont = new Font(Font.FontFamily.HELVETICA, 15, Font.BOLD, BaseColor.BLACK);
            Font largeBoldFont = new Font(Font.FontFamily.HELVETICA,10,Font.BOLD,BaseColor.BLACK);
            Font smallFontCancelado = new Font(Font.FontFamily.HELVETICA,10,Font.BOLD,BaseColor.RED);
            /*razon_social+ "&" + 
            calle +numero +colonia+ "&" + 
            municipio+ ", " + estado+ "\n" + "&" + 
            rfc+cp*/
            String [] temp = cadena.split("&");
            PdfPTable table = new PdfPTable(1);
            PdfPCell cell;
            
            cell = new PdfPCell(new Paragraph(temp[0],smallBoldFont));
            cell.setBorder(0);
            cell.setUseAscender(true);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setUseDescender(true);
            cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            table.addCell(cell);
            
            
            //celda vacia
            cell = new PdfPCell(new Paragraph(" ", smallFont));
            cell.setBorder(0);
            table.addCell(cell);
            
            cell = new PdfPCell(new Paragraph(temp[1],largeBoldFont));
            cell.setBorder(0);
            cell.setUseAscender(true);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setUseDescender(true);
            cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            table.addCell(cell);
            
            cell = new PdfPCell(new Paragraph(temp[2],largeBoldFont));
            cell.setBorder(0);
            cell.setUseAscender(true);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setUseDescender(true);
            cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            table.addCell(cell);
            
           cell = new PdfPCell(new Paragraph(temp[3],largeBoldFont));
            cell.setBorder(0);
            cell.setUseAscender(true);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setUseDescender(true);
            cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            table.addCell(cell);
            
            
            //celda vacia
            cell = new PdfPCell(new Paragraph(" ", smallFont));
            cell.setBorder(0);
            table.addCell(cell);
            
            //celda vacia
            cell = new PdfPCell(new Paragraph(" ", smallFont));
            cell.setBorder(0);
            table.addCell(cell);
            
            return table;
        }
        
        public PdfPTable adddatosDocumento(String cadena) {
            Font smallFont = new Font(Font.FontFamily.HELVETICA,7,Font.NORMAL,BaseColor.BLACK);
            Font sont = new Font(Font.FontFamily.HELVETICA,8,Font.BOLD,BaseColor.BLACK);
            Font smallBoldFont = new Font(Font.FontFamily.HELVETICA, 8, Font.BOLD, BaseColor.WHITE);
            Font largeBoldFont = new Font(Font.FontFamily.HELVETICA,10,Font.BOLD,BaseColor.BLACK);
            Font smallFontCancelado = new Font(Font.FontFamily.HELVETICA,10,Font.BOLD,BaseColor.RED);
            /*razon_social+ "&" + 
            calle +numero +colonia+ "&" + 
            municipio+ ", " + estado+ "\n" + "&" + 
            rfc+cp*/
            String [] temp = cadena.split("&");
            PdfPTable table = new PdfPTable(1);
            PdfPCell cell;
            
            cell = new PdfPCell(new Paragraph(temp[0],smallBoldFont));
            cell.setBorder(0);
            cell.setBackgroundColor(BaseColor.BLACK);
            cell.setUseAscender(true);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setUseDescender(true);
            cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            table.addCell(cell);
            
            
            cell = new PdfPCell(new Paragraph(temp[1],largeBoldFont));
            cell.setBorder(1);
            cell.setUseAscender(true);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setUseDescender(true);
            cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            table.addCell(cell);
            
            
            //celda vacia
            cell = new PdfPCell(new Paragraph(" ", smallFont));
            cell.setBorder(0);
            table.addCell(cell);
            
            //celda vacia
            cell = new PdfPCell(new Paragraph(" ", smallFont));
            cell.setBorder(0);
            table.addCell(cell);
            
            return table;
        } 
    }
    
     public PdfEtiqueta_Compras(String fileout, HashMap<String, String> datos_empresa, ArrayList<HashMap<String, String>> lista_etiquetas, String ruta_imagen, String ruta_imagencodbarr,Integer tipo_origen) {
   
        Font smallsmall = new Font(Font.FontFamily.HELVETICA,5,Font.NORMAL,BaseColor.BLACK);
        Font smallFont = new Font(Font.FontFamily.HELVETICA,7,Font.NORMAL,BaseColor.BLACK);
        //Font smallBoldFont = new Font(Font.FontFamily.HELVETICA,8,Font.BOLD,BaseColor.BLACK);
        Font largeBoldFont = new Font(Font.FontFamily.HELVETICA,8,Font.BOLD,BaseColor.BLACK);
        Font smallBoldFont = new Font(Font.FontFamily.HELVETICA, 8, Font.BOLD, BaseColor.WHITE);
        
        this.setArchivoSalida(new File(fileout));
        this.setImagen(ruta_imagen);
        
        
        String razon_social= datos_empresa.get("emp_razon_social");
        String rfc= datos_empresa.get("emp_rfc");
        String calle= datos_empresa.get("emp_calle");
        String numero= datos_empresa.get("emp_no_exterior");
        String colonia= datos_empresa.get("emp_colonia");
        String pais= datos_empresa.get("emp_pais");
        String estado= datos_empresa.get("emp_estado");
        String municipio= datos_empresa.get("emp_municipio");
        String cp= datos_empresa.get("emp_cp");
        
        String cadena= "";
                cadena = razon_social+ "&" + 
                         "Calle:"+calle + ", " + "N0.:"+numero + ", " + "Col.: "+colonia+ "&" + 
                         "Mpio.: "+municipio+ ",   " + "Estado: "+estado+ "\n" + "&" + 
                         "RFC.: "+rfc+ ", " +"CP.: "+cp;
                    
        System.out.println("Esta es la cadena:::"+cadena);
            
        this.setDatos_empresa(cadena);
        String  tipo_etiqueta="";
        String datos_documento="";
        if(tipo_origen == 1){
             tipo_etiqueta="Etiquetas Entradas";
        }
        if(tipo_origen == 2){
              tipo_etiqueta="Etiquetas Producción";
        }
        if(tipo_origen == 3){
              tipo_etiqueta="Etiquetas Requisición";
        }
        datos_documento="TIPO DE ETIQUETA"+ "&" +tipo_etiqueta;
        //StringHelper.capitalizaString(datos_empresa.get("emp_estado"))
        this.setDatos_tipo_etiqueta(datos_documento);
       
        
        ImagenPDF ipdf = new PdfEtiqueta_Compras.ImagenPDF();
        EncabezadoPDF encabezadopdf = new PdfEtiqueta_Compras.EncabezadoPDF();
        
        PdfPTable table = null;
        
        PdfPCell cell;
        Integer contador;
        
        
        
        try {
            Document document = new Document(PageSize.LETTER.rotate(), -50, -50, 20, 30);
            PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(fileout));
            document.open();
            float [] widths = {6,12,6};
            table = new PdfPTable(widths);
            table.setKeepTogether(false);
            
            //IMAGEN --> logo empresa
            cell = new PdfPCell(ipdf.addContent());
            cell.setBorder(0);
            cell.setRowspan(5);
            cell.setUseDescender(true);
            cell.setVerticalAlignment(Element.ALIGN_TOP);
            table.addCell(cell);
            
                           
            cell = new PdfPCell(encabezadopdf.addContent(cadena));
            //cell = new PdfPCell(ipdf.addContent());
            cell.setBorder(0);
            cell.setRowspan(12);
            table.addCell(cell);
            
            
            
            cell = new PdfPCell(encabezadopdf.adddatosDocumento(datos_documento));
            cell.setBorder(0);
            //cell.setRowspan(10);
            cell.setRowspan(5);
            cell.setUseDescender(true);
            cell.setVerticalAlignment(Element.ALIGN_TOP);
            table.addCell(cell);
            
            
            cell = new PdfPCell(new Paragraph("",smallFont));
            cell.setBorder(0);
            cell.setColspan(3);
            cell.setFixedHeight(50);
            cell.setUseDescender(true);
            cell.setVerticalAlignment(Element.ALIGN_TOP);
            table.addCell(cell);
            
            document.add(table);
            
            
            System.out.println("Tamaño lista: "+lista_etiquetas.size());
            
            this.setRows(lista_etiquetas);
            //INICIO DE LA TABLA DE PEDIDO ENTREGADO ??
            float [] anchocolumnas = {2f,4f, 1.5f,4f,4f};
            
            table = new PdfPTable(anchocolumnas);
            table.setHeaderRows(1);
            
            String[] columnas = {"CODIGO","NOMBRE","CADUCIDAD","CODIGO LOTE INTERNO","BARRA LOTE INTERNO "};
            List<String>  lista_columnas = (List<String>) Arrays.asList(columnas);
            
            //Font smallBoldFont2 = new Font(Font.FontFamily.HELVETICA,8,Font.BOLD,BaseColor.BLACK);
            for ( String columna_titulo : lista_columnas){
                PdfPCell cellX = new PdfPCell(new Paragraph(columna_titulo,smallBoldFont));
                cellX.setUseAscender(true);
                cellX.setUseDescender(true);
                cellX.setBackgroundColor(BaseColor.BLACK);
                cellX.setFixedHeight(15);
                table.addCell(cellX);
            }
            
            
           for (HashMap<String, String> registro : this.getRows()){
                    //Indices del HashMap que representa el row
                String[] wordList = {"codigo","nombre_producto","fecha","lote_interno_codigo","lote_interno"};
                
                List<String>  indices = (List<String>) Arrays.asList(wordList);

                for (String omega : indices){
                    PdfPCell celda = null;
                    
                    if (omega.equals("codigo")){
                        celda = new PdfPCell(new Paragraph(registro.get(omega),smallFont));
                        celda.setFixedHeight(30);
                        celda.setHorizontalAlignment(Element.ALIGN_LEFT);
                        celda.setVerticalAlignment(Element.ALIGN_MIDDLE);
                    }

                    if (omega.equals("nombre_producto")){
                        celda = new PdfPCell(new Paragraph(registro.get(omega),smallFont));
                        celda.setFixedHeight(30);
                        celda.setHorizontalAlignment(Element.ALIGN_LEFT);
                        celda.setVerticalAlignment(Element.ALIGN_MIDDLE);
                    }

                    if (omega.equals("fecha")){
                        celda = new PdfPCell(new Paragraph(registro.get(omega),smallFont));
                        celda.setFixedHeight(30);
                        celda.setHorizontalAlignment(Element.ALIGN_CENTER);
                        celda.setVerticalAlignment(Element.ALIGN_MIDDLE);
                    }

                    if (omega.equals("lote_interno_codigo")){
                        celda = new PdfPCell(new Paragraph(registro.get(omega),smallFont));
                        celda.setFixedHeight(30);
                        celda.setHorizontalAlignment(Element.ALIGN_CENTER);
                        celda.setVerticalAlignment(Element.ALIGN_MIDDLE);
                    }

                    if (omega.equals("lote_interno")){
                        String cod_barr=BarcodeHelper.createImgCodBarr128(registro.get(omega));

                        celda = new PdfPCell(ipdf.addContentBarCode(cod_barr));
                        celda.setFixedHeight(30);
                        celda.setHorizontalAlignment(Element.ALIGN_CENTER);
                        celda.setVerticalAlignment(Element.ALIGN_MIDDLE);
                        FileHelper.delete(cod_barr);//eliminar la imagen del codigo de barras generado
                    }

                    celda.setBorder(0);
                    //celda.setBorder(11);
                    table.addCell(celda);
                }

                
            }
            document.add(table);
            //FIN DE LA TABLA 
            
            
            
            document.close();
            
        }
        catch (Exception e) {
                e.printStackTrace();
        }
        
        
        
        
        
        
    }//termina pdfEtiquetacompras
    
}
