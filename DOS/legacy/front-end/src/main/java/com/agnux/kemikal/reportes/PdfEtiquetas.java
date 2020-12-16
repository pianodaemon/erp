package com.agnux.kemikal.reportes;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import net.sourceforge.barbecue.BarcodeFactory;
import net.sourceforge.barbecue.BarcodeImageHandler;


public class PdfEtiquetas {
    public ArrayList<HashMap<String, Object>> rows;

    public ArrayList<HashMap<String, Object>> getRows() {
        return rows;
    }
    
    public void setRows(ArrayList<HashMap<String, Object>> rows) {
        this.rows = rows;
    }
    
    public File archivoSalida;
    
    public File getArchivoSalida() {
        return archivoSalida;
    }

    public void setArchivoSalida(File archivoSalida) {
        this.archivoSalida = archivoSalida;
    }

    
    public PdfEtiquetas(ArrayList<HashMap<String, Object>> datos, String observaciones, String fileout, String dir_tmp) {
        this.setArchivoSalida(new File(fileout));
        this.setRows(datos);
        
        Font smallFont = new Font(Font.FontFamily.HELVETICA,7,Font.NORMAL,BaseColor.BLACK);
        Font smallFontBold = new Font(Font.FontFamily.HELVETICA,14,Font.BOLD,BaseColor.BLACK);
        
        PdfPTable tablaPrincipal;
        PdfPTable tabla_etiquetas;
        PdfPCell cell;
        PdfPCell cellEtiqueta;
        ImagenPDF ipdf;
        String ruta_imagen = "";
        String fecha_caducidad="";
        int alto_celda = 73;
        
        try {
            Document document = new Document(PageSize.LETTER, -50, -50, 20, 20);
            PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(fileout));
            document.open();
            
            float [] widths1 = {1};
            tablaPrincipal = new PdfPTable(widths1);
            tablaPrincipal.setKeepTogether(false);
            
            //Columnas para la tabla de etiquetas
            float [] anchocolumnas = {1.2f, 1.8f, 1f, 0.8f, 0.8f};
            
            int contador = 0;
            
            if(this.getRows().size()>0){
                for (HashMap<String, Object> i : this.getRows()){
                    tabla_etiquetas = new PdfPTable(anchocolumnas);
                    tabla_etiquetas.setKeepTogether(true);
                    ruta_imagen = "";
                    fecha_caducidad="";
                    contador++;

                    //FILA 1 
                    cellEtiqueta = new PdfPCell(new Paragraph("Orden Compra",smallFontBold));
                    cellEtiqueta.setHorizontalAlignment(Element.ALIGN_LEFT);
                    cellEtiqueta.setVerticalAlignment(Element.ALIGN_MIDDLE);
                    cellEtiqueta.setFixedHeight(alto_celda);
                    tabla_etiquetas.addCell(cellEtiqueta);

                    cellEtiqueta = new PdfPCell(new Paragraph(i.get("orden_compra").toString(),smallFontBold));
                    cellEtiqueta.setHorizontalAlignment(Element.ALIGN_LEFT);
                    cellEtiqueta.setVerticalAlignment(Element.ALIGN_MIDDLE);
                    cellEtiqueta.setFixedHeight(alto_celda);
                    tabla_etiquetas.addCell(cellEtiqueta);

                    if("".equals(i.get("orden_compra").toString().trim())){
                        cellEtiqueta = new PdfPCell(new Paragraph("",smallFontBold));
                        cellEtiqueta.setHorizontalAlignment(Element.ALIGN_CENTER);
                        cellEtiqueta.setVerticalAlignment(Element.ALIGN_MIDDLE);
                        cellEtiqueta.setFixedHeight(alto_celda);
                        cellEtiqueta.setColspan(3);
                        tabla_etiquetas.addCell(cellEtiqueta);
                    }else{
                        ruta_imagen = dir_tmp + i.get("orden_compra").toString() +".png";
                        net.sourceforge.barbecue.Barcode barcode = BarcodeFactory.createCode128(i.get("orden_compra").toString());
                        File f = new File(ruta_imagen);
                        BarcodeImageHandler.savePNG(barcode, f);
                        ipdf = new ImagenPDF(ruta_imagen);

                        cellEtiqueta = new PdfPCell(ipdf.addContent());
                        cellEtiqueta.setHorizontalAlignment(Element.ALIGN_CENTER);
                        cellEtiqueta.setVerticalAlignment(Element.ALIGN_MIDDLE);
                        cellEtiqueta.setFixedHeight(alto_celda);
                        cellEtiqueta.setColspan(3);
                        tabla_etiquetas.addCell(cellEtiqueta);
                    }

                    //FILA 2
                    cellEtiqueta = new PdfPCell(new Paragraph("Código",smallFontBold));
                    cellEtiqueta.setHorizontalAlignment(Element.ALIGN_LEFT);
                    cellEtiqueta.setVerticalAlignment(Element.ALIGN_MIDDLE);
                    cellEtiqueta.setFixedHeight(alto_celda);
                    tabla_etiquetas.addCell(cellEtiqueta);

                    cellEtiqueta = new PdfPCell(new Paragraph(i.get("codigo").toString(),smallFontBold));
                    cellEtiqueta.setHorizontalAlignment(Element.ALIGN_LEFT);
                    cellEtiqueta.setVerticalAlignment(Element.ALIGN_MIDDLE);
                    cellEtiqueta.setFixedHeight(alto_celda);
                    tabla_etiquetas.addCell(cellEtiqueta);

                    if("".equals(i.get("codigo").toString().trim())){
                        cellEtiqueta = new PdfPCell(new Paragraph("",smallFontBold));
                        cellEtiqueta.setVerticalAlignment(Element.ALIGN_MIDDLE);
                        cellEtiqueta.setHorizontalAlignment(Element.ALIGN_CENTER);
                        cellEtiqueta.setFixedHeight(alto_celda);
                        cellEtiqueta.setColspan(3);
                        tabla_etiquetas.addCell(cellEtiqueta);
                    }else{
                        ruta_imagen = dir_tmp + i.get("codigo").toString() +".png";
                        net.sourceforge.barbecue.Barcode barcode = BarcodeFactory.createCode128(i.get("codigo").toString());
                        File f = new File(ruta_imagen);
                        BarcodeImageHandler.savePNG(barcode, f);
                        ipdf = new ImagenPDF(ruta_imagen);

                        cellEtiqueta = new PdfPCell(ipdf.addContent());
                        cellEtiqueta.setVerticalAlignment(Element.ALIGN_MIDDLE);
                        cellEtiqueta.setHorizontalAlignment(Element.ALIGN_CENTER);
                        cellEtiqueta.setFixedHeight(alto_celda);
                        cellEtiqueta.setColspan(3);
                        tabla_etiquetas.addCell(cellEtiqueta);
                    }
                    
                    
                    //FILA 3
                    cellEtiqueta = new PdfPCell(new Paragraph("Código 2",smallFontBold));
                    cellEtiqueta.setHorizontalAlignment(Element.ALIGN_LEFT);
                    cellEtiqueta.setVerticalAlignment(Element.ALIGN_MIDDLE);
                    cellEtiqueta.setFixedHeight(alto_celda);
                    tabla_etiquetas.addCell(cellEtiqueta);

                    cellEtiqueta = new PdfPCell(new Paragraph(i.get("codigo2").toString(),smallFontBold));
                    cellEtiqueta.setHorizontalAlignment(Element.ALIGN_LEFT);
                    cellEtiqueta.setVerticalAlignment(Element.ALIGN_MIDDLE);
                    cellEtiqueta.setFixedHeight(alto_celda);
                    tabla_etiquetas.addCell(cellEtiqueta);

                    if("".equals(i.get("codigo2").toString().trim())){
                        cellEtiqueta = new PdfPCell(new Paragraph("",smallFontBold));
                        cellEtiqueta.setVerticalAlignment(Element.ALIGN_MIDDLE);
                        cellEtiqueta.setHorizontalAlignment(Element.ALIGN_CENTER);
                        cellEtiqueta.setFixedHeight(alto_celda);
                        cellEtiqueta.setColspan(3);
                        tabla_etiquetas.addCell(cellEtiqueta);
                    }else{
                        ruta_imagen = dir_tmp + i.get("codigo2").toString() +".png";
                        net.sourceforge.barbecue.Barcode barcode = BarcodeFactory.createCode128(i.get("codigo2").toString());
                        File f = new File(ruta_imagen);
                        BarcodeImageHandler.savePNG(barcode, f);
                        ipdf = new ImagenPDF(ruta_imagen);

                        cellEtiqueta = new PdfPCell(ipdf.addContent());
                        cellEtiqueta.setVerticalAlignment(Element.ALIGN_MIDDLE);
                        cellEtiqueta.setHorizontalAlignment(Element.ALIGN_CENTER);
                        cellEtiqueta.setFixedHeight(alto_celda);
                        cellEtiqueta.setColspan(3);
                        tabla_etiquetas.addCell(cellEtiqueta);
                    }

                    //FILA 4
                    cellEtiqueta = new PdfPCell(new Paragraph("Lote",smallFontBold));
                    cellEtiqueta.setHorizontalAlignment(Element.ALIGN_LEFT);
                    cellEtiqueta.setVerticalAlignment(Element.ALIGN_MIDDLE);
                    cellEtiqueta.setFixedHeight(alto_celda);
                    tabla_etiquetas.addCell(cellEtiqueta);

                    cellEtiqueta = new PdfPCell(new Paragraph(i.get("lote").toString(),smallFontBold));
                    cellEtiqueta.setHorizontalAlignment(Element.ALIGN_LEFT);
                    cellEtiqueta.setVerticalAlignment(Element.ALIGN_MIDDLE);
                    cellEtiqueta.setFixedHeight(alto_celda);
                    tabla_etiquetas.addCell(cellEtiqueta);

                    if("".equals(i.get("lote").toString().trim())){
                        cellEtiqueta = new PdfPCell(new Paragraph("",smallFontBold));
                        cellEtiqueta.setVerticalAlignment(Element.ALIGN_MIDDLE);
                        cellEtiqueta.setHorizontalAlignment(Element.ALIGN_CENTER);
                        cellEtiqueta.setFixedHeight(alto_celda);
                        cellEtiqueta.setColspan(3);
                        tabla_etiquetas.addCell(cellEtiqueta);
                    }else{
                        ruta_imagen = dir_tmp + i.get("lote").toString() +".png";
                        net.sourceforge.barbecue.Barcode barcode = BarcodeFactory.createCode128(i.get("lote").toString());
                        File f = new File(ruta_imagen);
                        BarcodeImageHandler.savePNG(barcode, f);
                        ipdf = new ImagenPDF(ruta_imagen);

                        cellEtiqueta = new PdfPCell(ipdf.addContent());
                        cellEtiqueta.setVerticalAlignment(Element.ALIGN_MIDDLE);
                        cellEtiqueta.setHorizontalAlignment(Element.ALIGN_CENTER);
                        cellEtiqueta.setFixedHeight(alto_celda);
                        cellEtiqueta.setColspan(3);
                        tabla_etiquetas.addCell(cellEtiqueta);
                    }


                    //FILA 4
                    cellEtiqueta = new PdfPCell(new Paragraph("Fecha Caducidad",smallFontBold));
                    cellEtiqueta.setHorizontalAlignment(Element.ALIGN_LEFT);
                    cellEtiqueta.setVerticalAlignment(Element.ALIGN_MIDDLE);
                    cellEtiqueta.setFixedHeight(30);
                    tabla_etiquetas.addCell(cellEtiqueta);

                    if(!"".equals(i.get("caducidad").toString().trim())){
                        fecha_caducidad = i.get("caducidad").toString().split("-")[2] +"/"+ i.get("caducidad").toString().split("-")[1] +"/"+ i.get("caducidad").toString().split("-")[0];
                    }

                    cellEtiqueta = new PdfPCell(new Paragraph(fecha_caducidad,smallFontBold));
                    cellEtiqueta.setHorizontalAlignment(Element.ALIGN_MIDDLE);
                    cellEtiqueta.setVerticalAlignment(Element.ALIGN_MIDDLE);
                    cellEtiqueta.setFixedHeight(30);
                    tabla_etiquetas.addCell(cellEtiqueta);

                    cellEtiqueta = new PdfPCell(new Paragraph("Cantidad",smallFontBold));
                    cellEtiqueta.setHorizontalAlignment(Element.ALIGN_LEFT);
                    cellEtiqueta.setVerticalAlignment(Element.ALIGN_MIDDLE);
                    cellEtiqueta.setFixedHeight(30);
                    tabla_etiquetas.addCell(cellEtiqueta);

                    cellEtiqueta = new PdfPCell(new Paragraph(i.get("cantidad").toString(),smallFontBold));
                    cellEtiqueta.setHorizontalAlignment(Element.ALIGN_LEFT);
                    cellEtiqueta.setVerticalAlignment(Element.ALIGN_MIDDLE);
                    cellEtiqueta.setColspan(2);
                    cellEtiqueta.setFixedHeight(30);
                    tabla_etiquetas.addCell(cellEtiqueta);

                    if(!"".equals(observaciones)){
                        cellEtiqueta = new PdfPCell(new Paragraph(observaciones.toUpperCase(),smallFont));
                        cellEtiqueta.setHorizontalAlignment(Element.ALIGN_LEFT);
                        cellEtiqueta.setVerticalAlignment(Element.ALIGN_MIDDLE);
                        cellEtiqueta.setColspan(5);
                        cellEtiqueta.setFixedHeight(30);
                        tabla_etiquetas.addCell(cellEtiqueta);
                    }else{
                        cellEtiqueta = new PdfPCell(new Paragraph("",smallFont));
                        cellEtiqueta.setHorizontalAlignment(Element.ALIGN_LEFT);
                        cellEtiqueta.setVerticalAlignment(Element.ALIGN_MIDDLE);
                        cellEtiqueta.setColspan(5);
                        cellEtiqueta.setFixedHeight(30);
                        tabla_etiquetas.addCell(cellEtiqueta);
                    }

                    //Aqui agregamos la tablaPrincipal al documento
                    document.add(tabla_etiquetas);

                    if(contador==1){
                        document.add(new Paragraph("\n\n"));
                    }else{
                        if(contador==2){
                            contador=0;

                            //Agregar nueva pagina
                            document.newPage();
                        }
                    }
                }
            }else{
                tabla_etiquetas = new PdfPTable(anchocolumnas);
                tabla_etiquetas.setKeepTogether(true);
                
                cellEtiqueta = new PdfPCell(new Paragraph("No se ha seleccionado ningun producto para generar etiqueta",smallFontBold));
                cellEtiqueta.setHorizontalAlignment(Element.ALIGN_LEFT);
                cellEtiqueta.setVerticalAlignment(Element.ALIGN_MIDDLE);
                cellEtiqueta.setColspan(5);
                cellEtiqueta.setFixedHeight(30);
                tabla_etiquetas.addCell(cellEtiqueta);
                
                //Aqui agregamos la tablaPrincipal al documento
                document.add(tabla_etiquetas);
            }
            
            document.close();
        }catch (Exception e) {
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
                img.scaleAbsoluteHeight(60);
                img.scaleAbsoluteWidth(250);
                img.setAlignment(0);
            }
            catch(Exception e){
                System.out.println(e);
            }
            return img;
        }
    }
    
    
}
