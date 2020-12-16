/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.agnux.kemikal.reportes;

import com.agnux.common.helpers.StringHelper;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.HashMap;

/**
 *
 * @author luis
 */
public class PdfEstadisticoComprasProductoSumarizado {
    public String  empresa_emisora;
    public static String fecha_reporte;

    public static String getFecha_reporte() {
        return fecha_reporte;
    }

    public static void setFecha_reporte(String fecha_reporte) {
        PdfEstadisticoComprasProveedorSumarizado.fecha_reporte = fecha_reporte;
    }
    public java.util.List<HashMap<String, String>> rows;

    public java.util.List<HashMap<String, String>> getRows() {
        return rows;
    }

    public void setRows(java.util.List<HashMap<String, String>> rows) {
        this.rows = rows;
    }

    public String getEmpresa_emisora() {
        return empresa_emisora;
    }

    public void setEmpresa_emisora(String empresa_emisora) {
        this.empresa_emisora = empresa_emisora;
    }



    public PdfEstadisticoComprasProductoSumarizado(ArrayList<HashMap<String, String>> lista_ventas,String proveedor,String fecha_inicial,String fecha_final, String razon_social_empresa, String fileout) {
    this.setEmpresa_emisora(razon_social_empresa);
        //this.setEmpresa_emisora(razon_social_empresa);
        PdfEstadisticoComprasProductoSumarizado.HeaderFooter event = new PdfEstadisticoComprasProductoSumarizado.HeaderFooter();

        Font fontCols = new Font(Font.FontFamily.HELVETICA, 9,Font.NORMAL);
        Font smallFont = new Font(Font.FontFamily.HELVETICA,8,Font.NORMAL,BaseColor.BLACK);
        Font smallBoldFont = new Font(Font.getFamily("ARIAL"),6,Font.BOLD,BaseColor.WHITE);
        Font smallFontN = new Font(Font.FontFamily.HELVETICA,8,Font.BOLD,BaseColor.BLACK);
        PdfPTable table_titulos;
        PdfPTable tabla = null;

        PdfPCell cell;
        try{

            Document document = new Document(PageSize.LETTER.rotate(),-50,-50,60,30);
            PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(fileout));
            writer.setPageEvent(event);
            document.open();
            //TABLA DE FECHAS

            String[] fi = fecha_inicial.split("-");
            String[] ff = fecha_final.split("-");

            String fecha_reporte = "DEL:      "+fi[2]+"/"+fi[1]+"/"+fi[0] + "      AL:      " +ff[2]+"/"+ff[1]+"/"+ff[0];
            this.setFecha_reporte(fecha_reporte);

            float [] widths = {10f,2f,4f,1,4f,1f,4f};
            tabla = new PdfPTable(widths);
            tabla.setHeaderRows(1);
            tabla.setKeepTogether(false);
            tabla.setKeepTogether(true);

            String[] titulos = {"PRODUCTO","UNIDAD","TOTAL CANTIDAD","","P.PROMEDIO","","VENTA TOTAL"};

                for (int i = 0; i<=titulos.length -1; i++){
                                    cell = new PdfPCell(new Paragraph(titulos[i],smallBoldFont));
                                    String PROVEEDOR = titulos[i];
                                    if(titulos[0] == "PRODUCTO"){
                                    cell.setHorizontalAlignment(Element.ALIGN_LEFT);
                                    }else{
                                        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                                    }
                                    cell.setUseAscender(true);
                                    cell.setUseDescender(true);
                                    cell.setBackgroundColor(BaseColor.BLACK);
                                    cell.setBorder(0);
                                    cell.setBorderWidthLeft(0);

                                    tabla.addCell(cell);

                }
               //fin de for de insertar encabezado de la tabla

                String producto ="";
                String unidad="";
                double cantidad=0.0;
                double precio_unitario=0.0;
                int contador_precio_unitario=0;
                double venta_neta=0.0;
                double venta_total_general =0.0;
                int tmp=0;
                int nombre_proveedor=1;

                        producto=lista_ventas.get(0).get("producto");
                        unidad=lista_ventas.get(0).get("unidad");
			for (int j=0; j<=lista_ventas.size()-1; j++){     //inicia for para recorrer las filas de la lista
                                //para conocer el contenido de los hashmap en cada fila de la lista
				HashMap<String, String> registro= lista_ventas.get(j);
                                if(producto.equals(registro.get("producto"))&& unidad.equals(registro.get("unidad"))){
                                     cantidad=cantidad + Double.parseDouble(registro.get("cantidad".intern()));
                                    precio_unitario=precio_unitario + Double.parseDouble(registro.get("precio_unitario".intern()));
                                    venta_neta=venta_neta + Double.parseDouble(registro.get("total_pesos".intern()));
                                    contador_precio_unitario= contador_precio_unitario+1;
                                    venta_total_general=venta_total_general+ Double.parseDouble(registro.get("total_pesos".intern()));

                                }else{





                                        cell = new PdfPCell(new Paragraph(producto.toString(),smallFont));
                                        cell.setHorizontalAlignment(Element.ALIGN_LEFT);
                                        cell.setBorder(0);
                                        tabla.addCell(cell);

                                        cell = new PdfPCell(new Paragraph(unidad.toString(),smallFont));
                                        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                                        cell.setBorder(0);
                                        tabla.addCell(cell);

                                        //String cant=StringHelper.roundDouble(registro.get("cantidad").toString(), 2);
                                        cell = new PdfPCell(new Paragraph(StringHelper.AgregaComas(StringHelper.roundDouble(cantidad,2)),smallFont));
                                        cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                                        cell.setBorder(0);
                                        tabla.addCell(cell);

                                        cell = new PdfPCell(new Paragraph("$",smallFont));
                                        cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                                        cell.setBorder(0);
                                        tabla.addCell(cell);

                                        //String precioUni=StringHelper.roundDouble(registro.get("precio_unitario").toString(), 2);
                                        cell = new PdfPCell(new Paragraph(StringHelper.AgregaComas(StringHelper.roundDouble(venta_neta/cantidad,2)),smallFont));
                                        cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                                        cell.setBorder(0);
                                        tabla.addCell(cell);

                                        cell = new PdfPCell(new Paragraph("$",smallFont));
                                        cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                                        cell.setBorder(0);
                                        tabla.addCell(cell);

                                        //String venNeta=StringHelper.roundDouble(registro.get("total_pesos").toString(), 2);
                                        cell = new PdfPCell(new Paragraph(StringHelper.AgregaComas(StringHelper.roundDouble(venta_neta,2)),smallFont));
                                        cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                                        cell.setBorder(0);
                                        tabla.addCell(cell);


                                        //proveedor=registro.get("razon_social");
                                        cantidad=0.0;
                                        precio_unitario=0.0;
                                        contador_precio_unitario=0;
                                        venta_neta=0.0;




                                    cantidad=cantidad + Double.parseDouble(registro.get("cantidad".intern()));
                                    precio_unitario=precio_unitario + Double.parseDouble(registro.get("precio_unitario".intern()));
                                    producto=registro.get("producto");
                                    unidad=registro.get("unidad");

                                    venta_neta=venta_neta + Double.parseDouble(registro.get("total_pesos".intern()));
                                        venta_total_general=venta_total_general+ Double.parseDouble(registro.get("total_pesos".intern()));
                                        contador_precio_unitario=contador_precio_unitario+1;

                                }



                        }//fin for recorrido de filas

                        cell = new PdfPCell(new Paragraph(producto.toString(),smallFont));
                                    cell.setHorizontalAlignment(Element.ALIGN_LEFT);
                                    cell.setBorder(0);
                                    tabla.addCell(cell);

                                    cell = new PdfPCell(new Paragraph(unidad.toString(),smallFont));
                                    cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                                    cell.setBorder(0);
                                    tabla.addCell(cell);

                                    //String cant=StringHelper.roundDouble(registro.get("cantidad").toString(), 2);
                                    cell = new PdfPCell(new Paragraph(StringHelper.AgregaComas(StringHelper.roundDouble(cantidad,2)),smallFont));
                                    cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                                    cell.setBorder(0);
                                    tabla.addCell(cell);

                                    cell = new PdfPCell(new Paragraph("$",smallFont));
                                    cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                                    cell.setBorder(0);
                                    tabla.addCell(cell);

                                    //String precioUni=StringHelper.roundDouble(registro.get("precio_unitario").toString(), 2);
                                    cell = new PdfPCell(new Paragraph(StringHelper.AgregaComas(StringHelper.roundDouble(precio_unitario,2)),smallFont));
                                    cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                                    cell.setBorder(0);
                                    tabla.addCell(cell);

                                    cell = new PdfPCell(new Paragraph("$",smallFont));
                                    cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                                    cell.setBorder(0);
                                    tabla.addCell(cell);

                                    //String venNeta=StringHelper.roundDouble(registro.get("total_pesos").toString(), 2);
                                    cell = new PdfPCell(new Paragraph(StringHelper.AgregaComas(StringHelper.roundDouble(venta_neta,2)),smallFont));
                                    cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                                    cell.setBorder(0);
                                    tabla.addCell(cell);




                                    cell = new PdfPCell(new Paragraph("Total General:", smallFont));
                                    cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                                    cell.setColspan(5);
                                    cell.setBorder(1);
                                    tabla.addCell(cell);



                        cell = new PdfPCell(new Paragraph("$", smallFont));
                        cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                        cell.setBorder(1);
                        tabla.addCell(cell);

                        //String venNeta=StringHelper.roundDouble(registro.get("total_pesos").toString(), 2);
                        cell = new PdfPCell(new Paragraph(StringHelper.AgregaComas(StringHelper.roundDouble(venta_total_general, 2)), smallFont));
                        cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                        cell.setBorder(1);
                        tabla.addCell(cell);



            document.add(tabla); //añadiendo la tabla
            document.close();
        }catch (Exception e) {
                e.printStackTrace();
        }
    }





    class HeaderFooter extends PdfPageEventHelper  {

        //public Image headerImage;
        protected PdfTemplate total;
        protected BaseFont helv;
        protected PdfContentByte cb;
        Font largeBoldFont = new Font(Font.FontFamily.HELVETICA,10,Font.BOLD,BaseColor.BLACK);
        Font largeFont = new Font(Font.FontFamily.HELVETICA,10,Font.NORMAL,BaseColor.BLACK);
        Font smallFont = new Font(Font.FontFamily.HELVETICA,7,Font.NORMAL,BaseColor.BLACK);

        /*Añadimos una tabla con  una imagen del logo de megestiono y creamos la fuente para el documento, la imagen esta escalada para que no se muestre pixelada*/
        @Override
        public void onOpenDocument(PdfWriter writer, Document document) {
            try {

                //headerImage = Image.getInstance(PdfDepositos.ruta_imagen);
                //headerImage.scalePercent(50);

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
            ColumnText.showTextAligned(writer.getDirectContent(),Element.ALIGN_CENTER, new Phrase(PdfEstadisticoComprasProductoSumarizado.this.getEmpresa_emisora(),largeBoldFont),document.getPageSize().getWidth()/2, document.getPageSize().getTop() -25, 0);
            ColumnText.showTextAligned(writer.getDirectContent(),Element.ALIGN_CENTER, new Phrase("Reporte Estadistico de Compras Sumarizado por Producto.",largeBoldFont),document.getPageSize().getWidth()/2, document.getPageSize().getTop()-38, 0);
            ColumnText.showTextAligned(writer.getDirectContent(),Element.ALIGN_CENTER, new Phrase(PdfEstadisticoComprasProductoSumarizado.getFecha_reporte(),largeFont),document.getPageSize().getWidth()/2, document.getPageSize().getTop()-50, 0);

            cb = writer.getDirectContent();
            //cb.saveState();
            String text = "Página " + writer.getPageNumber() + " de ";
            float textBase = document.bottom() - 20;
            float textSize = helv.getWidthPoint(text, 7);
            float adjust = helv.getWidthPoint("0", 150);
            cb.beginText();
            cb.setFontAndSize(helv, 7);

            //cb.setTextMatrix(document.right() - 120 - adjust, textBase);
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
