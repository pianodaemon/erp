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
public class PdfEstadisticoComprasProducto {
    public String  empresa_emisora;
    public static String fecha_reporte;

    public static String getFecha_reporte() {
        return fecha_reporte;
    }

    public static void setFecha_reporte(String fecha) {
        fecha_reporte = fecha;
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



    public PdfEstadisticoComprasProducto(ArrayList<HashMap<String, String>> lista_ventas,String fecha_inicial,String fecha_final, String razon_social_empresa, String fileout) {
		this.setEmpresa_emisora(razon_social_empresa);
        //this.setEmpresa_emisora(razon_social_empresa);
        PdfEstadisticoComprasProducto.HeaderFooter event = new PdfEstadisticoComprasProducto.HeaderFooter();

        //Font largeBoldFont = new Font(Font.FontFamily.HELVETICA,8,Font.BOLD,BaseColor.BLACK);
        //Font smallFont = new Font(Font.FontFamily.HELVETICA,6,Font.NORMAL,BaseColor.BLACK);


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

            String fechaReporte = "DEL:      "+fi[2]+"/"+fi[1]+"/"+fi[0] + "      AL:      " +ff[2]+"/"+ff[1]+"/"+ff[0];
            this.setFecha_reporte(fechaReporte);
            /*
            float [] widths = {0.5f,2f,0.2f,1f,1f};
            table_titulos = new PdfPTable(widths);
            table_titulos.setKeepTogether(false);
            table_titulos.setKeepTogether(true);
            */
            float [] widths = {2.5f,7f,2.5f,2.5f,1f,1.5f,0.5f,1.5f,0.5f,2f,1.5f,0.5f,2f};
            tabla = new PdfPTable(widths);
            tabla.setKeepTogether(false);
            tabla.setKeepTogether(true);
            tabla.setHeaderRows(1);


            String[] titulos = {"CLAVE","PROVEEDOR","FACTURA","FECHA","UNIDAD","CANTIDAD","","PRECIO U.","","VENTA NETA","MONEDA","","TIPO CAMBIO"};

            for (int i = 0; i<=titulos.length -1; i++){

				cell = new PdfPCell(new Paragraph(titulos[i],smallBoldFont));
				cell.setHorizontalAlignment(Element.ALIGN_CENTER);
				cell.setUseAscender(true);
				cell.setUseDescender(true);
				cell.setBackgroundColor(BaseColor.BLACK);
				cell.setBorder(0);
				cell.setBorderWidthLeft(0);

				tabla.addCell(cell);

            }
               //fin de for de insertar encabezado de la tabla



                  int tmp=0;
                  int primer_registro= 0;
                  String producto ="";
                  String unidad="";

			double suma=0.0;
                        double TGsuma=0.0;
                        double sumaunidad=0.0;

			//inicializar variable registros con el primer valor de la lista en el campo razon social
			//String registros=lista_ventas.get(0).get("razon_social").toString();

                        producto =lista_ventas.get(0).get("producto").toString();
                        unidad=lista_ventas.get(0).get("unidad").toString();
                        cell = new PdfPCell(new Paragraph("Producto: "+producto,smallFont));
                        cell.setColspan(13);
                        cell.setBorder(0);
                        tabla.addCell(cell);
			for (int j=0; j<=lista_ventas.size()-1; j++){     //inicia for para recorrer las filas de la lista
                                //para conocer el contenido de los hashmap en cada fila de la lista
				HashMap<String, String> registro= lista_ventas.get(j);



                                if(producto.equals(registro.get("producto")) && unidad.equals(registro.get("unidad")) ){   //compara si todos los registros de la razon social

                                        cell = new PdfPCell(new Paragraph(registro.get("folio").toString(),smallFont));
					cell.setHorizontalAlignment(Element.ALIGN_LEFT);
					cell.setBorder(0);
					tabla.addCell(cell);

					cell = new PdfPCell(new Paragraph(registro.get("razon_social").toString(),smallFont));
					cell.setHorizontalAlignment(Element.ALIGN_LEFT);
					cell.setBorder(0);
					tabla.addCell(cell);

					cell = new PdfPCell(new Paragraph(registro.get("factura").toString(),smallFont));
					cell.setHorizontalAlignment(Element.ALIGN_CENTER);
					cell.setBorder(0);
					tabla.addCell(cell);

                                        cell = new PdfPCell(new Paragraph(registro.get("fecha_factura").toString(),smallFont));
					cell.setHorizontalAlignment(Element.ALIGN_CENTER);
					cell.setBorder(0);
					tabla.addCell(cell);

					cell = new PdfPCell(new Paragraph(registro.get("unidad").toString(),smallFont));
					cell.setHorizontalAlignment(Element.ALIGN_CENTER);
					cell.setBorder(0);
					tabla.addCell(cell);

					//String cant=StringHelper.roundDouble(registro.get("cantidad").toString(), 2);
					cell = new PdfPCell(new Paragraph(StringHelper.AgregaComas(StringHelper.roundDouble(registro.get("cantidad").toString(),2)),smallFont));
					cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
					cell.setBorder(0);
					tabla.addCell(cell);

					cell = new PdfPCell(new Paragraph("$",smallFont));
					cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
					cell.setBorder(0);
					tabla.addCell(cell);

					//String precioUni=StringHelper.roundDouble(registro.get("precio_unitario").toString(), 2);
					cell = new PdfPCell(new Paragraph(StringHelper.AgregaComas(StringHelper.roundDouble(registro.get("precio_unitario").toString(),2)),smallFont));
					cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
					cell.setBorder(0);
					tabla.addCell(cell);

					cell = new PdfPCell(new Paragraph("$",smallFont));
					cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
					cell.setBorder(0);
					tabla.addCell(cell);

					//String venNeta=StringHelper.roundDouble(registro.get("total_pesos").toString(), 2);
					cell = new PdfPCell(new Paragraph(StringHelper.AgregaComas(StringHelper.roundDouble(registro.get("total_pesos").toString(),2)),smallFont));
					cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
					cell.setBorder(0);
					tabla.addCell(cell);



					cell = new PdfPCell(new Paragraph(registro.get("moneda").toString(),smallFont));
					cell.setHorizontalAlignment(Element.ALIGN_CENTER);
					cell.setBorder(0);
					tabla.addCell(cell);

					cell = new PdfPCell(new Paragraph("$",smallFont));
					cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
					cell.setBorder(0);
					tabla.addCell(cell);

					//String tipoCam=StringHelper.roundDouble(registro.get("tipo_cambio").toString(), 4);
					cell = new PdfPCell(new Paragraph(StringHelper.AgregaComas(StringHelper.roundDouble(registro.get("tipo_cambio").toString(),2)),smallFont));
					cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
					cell.setBorder(0);
					tabla.addCell(cell);


                                    //realiza la suma de la venta Neta

                                    sumaunidad=sumaunidad +Double.parseDouble(registro.get("cantidad").toString());
                                    suma = suma  + Double.parseDouble(registro.get("total_pesos").toString());
                                    TGsuma = TGsuma  + Double.parseDouble(registro.get("total_pesos").toString());
                                    //suma=valorVentaNeta +Double.parseDouble(registro.get("total_pesos".intern()));
                                    //suma+=valorVentaNeta;
				}else{
                                    //si ya no coincide el valor del primer registro (0) agrega la ultima fila de cada
					//proveedor mostrando su total de la venta Neta.
					//String resultadoSuma=" "+suma;
                                        cell = new PdfPCell(new Paragraph());
                                        cell.setColspan(4);
                                        cell.setBorder(0);
                                        tabla.addCell(cell);

                                        cell = new PdfPCell(new Paragraph("Total: ",smallFont));
                                        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                                        cell.setBorder(1);
                                        tabla.addCell(cell);

                                        //String resultSuma=StringHelper.roundDouble(sumaunidad, 2);
                                        cell = new PdfPCell(new Paragraph(StringHelper.AgregaComas(StringHelper.roundDouble(sumaunidad,2)),smallFont));
                                        cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                                        cell.setBorder(1);
                                        tabla.addCell(cell);

                                        sumaunidad=0.0;

                                        cell = new PdfPCell(new Paragraph());
                                         cell.setBorder(0);
                                        tabla.addCell(cell);

                                        cell = new PdfPCell(new Paragraph("Total: ",smallFont));
                                        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                                        cell.setBorder(0);
                                        tabla.addCell(cell);


                                        cell = new PdfPCell(new Paragraph("$",smallFont));
                                        cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                                        cell.setBorder(1);
                                        tabla.addCell(cell);

                                        //String resultSuma=StringHelper.roundDouble(suma, 2);
                                        cell = new PdfPCell(new Paragraph(StringHelper.AgregaComas(StringHelper.roundDouble(suma,2)),smallFont));
                                        cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                                        cell.setBorder(1);
                                        tabla.addCell(cell);

                                        cell = new PdfPCell(new Paragraph());
                                        cell.setColspan(3);
                                        cell.setBorder(0);
                                        tabla.addCell(cell);

                                        suma = 0.0;



                                    ///////////////////////////////////////////////////////////////////////////////////////////////////
                                        cell = new PdfPCell(new Paragraph("Producto: "+registro.get("producto"),smallFont));
                                        cell.setColspan(13);
                                        cell.setBorder(0);
                                        tabla.addCell(cell);

					cell = new PdfPCell(new Paragraph(registro.get("folio").toString(),smallFont));
					cell.setHorizontalAlignment(Element.ALIGN_LEFT);
					cell.setBorder(0);
					tabla.addCell(cell);

					cell = new PdfPCell(new Paragraph(registro.get("razon_social").toString(),smallFont));
					cell.setHorizontalAlignment(Element.ALIGN_LEFT);
					cell.setBorder(0);
					tabla.addCell(cell);

					cell = new PdfPCell(new Paragraph(registro.get("factura").toString(),smallFont));
					cell.setHorizontalAlignment(Element.ALIGN_CENTER);
					cell.setBorder(0);
					tabla.addCell(cell);

                                        cell = new PdfPCell(new Paragraph(registro.get("fecha_factura").toString(),smallFont));
					cell.setHorizontalAlignment(Element.ALIGN_CENTER);
					cell.setBorder(0);
					tabla.addCell(cell);

					cell = new PdfPCell(new Paragraph(registro.get("unidad").toString(),smallFont));
					cell.setHorizontalAlignment(Element.ALIGN_CENTER);
					cell.setBorder(0);
					tabla.addCell(cell);

					//String cant=StringHelper.roundDouble(registro.get("cantidad").toString(), 2);
					cell = new PdfPCell(new Paragraph(StringHelper.AgregaComas(StringHelper.roundDouble(registro.get("cantidad").toString(),2)),smallFont));
					cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
					cell.setBorder(0);
					tabla.addCell(cell);

					cell = new PdfPCell(new Paragraph("$",smallFont));
					cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
					cell.setBorder(0);
					tabla.addCell(cell);

					//String precioUni=StringHelper.roundDouble(registro.get("precio_unitario").toString(), 2);
					cell = new PdfPCell(new Paragraph(StringHelper.AgregaComas(StringHelper.roundDouble(registro.get("precio_unitario").toString(),2)),smallFont));
					cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
					cell.setBorder(0);
					tabla.addCell(cell);

					cell = new PdfPCell(new Paragraph("$",smallFont));
					cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
					cell.setBorder(0);
					tabla.addCell(cell);

					//String venNeta=StringHelper.roundDouble(registro.get("total_pesos").toString(), 2);
					cell = new PdfPCell(new Paragraph(StringHelper.AgregaComas(StringHelper.roundDouble(registro.get("total_pesos").toString(),2)),smallFont));
					cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
					cell.setBorder(0);
					tabla.addCell(cell);



					cell = new PdfPCell(new Paragraph(registro.get("moneda").toString(),smallFont));
					cell.setHorizontalAlignment(Element.ALIGN_CENTER);
					cell.setBorder(0);
					tabla.addCell(cell);

					cell = new PdfPCell(new Paragraph("$",smallFont));
					cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
					cell.setBorder(0);
					tabla.addCell(cell);

					//String tipoCam=StringHelper.roundDouble(registro.get("tipo_cambio").toString(), 4);
					cell = new PdfPCell(new Paragraph(StringHelper.AgregaComas(StringHelper.roundDouble(registro.get("tipo_cambio").toString(),2)),smallFont));
					cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
					cell.setBorder(0);
					tabla.addCell(cell);

                                        producto =registro.get("producto").toString();
                                        unidad=registro.get("unidad").toString();
                                        sumaunidad=sumaunidad +Double.parseDouble(registro.get("cantidad").toString());
                                        suma = suma  + Double.parseDouble(registro.get("total_pesos").toString());
                                        TGsuma = TGsuma  + Double.parseDouble(registro.get("total_pesos").toString());
                                    }
                        }//fin for recorrido de filas
			//total venta del ultimo proveedor que se muestra en el pdf
			//String resultadoSuma=" "+suma;
                        //suma del ultimo proveedor

                                        cell = new PdfPCell(new Paragraph());
                                        cell.setColspan(4);
                                        cell.setBorder(0);
                                        tabla.addCell(cell);

                                        cell = new PdfPCell(new Paragraph("Total: ",smallFont));
                                        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                                        cell.setBorder(1);
                                        tabla.addCell(cell);

                                        cell = new PdfPCell(new Paragraph(StringHelper.AgregaComas(StringHelper.roundDouble(sumaunidad,2)),smallFont));
                                        cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                                        cell.setBorder(1);
                                        tabla.addCell(cell);

                                        sumaunidad=0.0;

                                        cell = new PdfPCell(new Paragraph("",smallFont));
                                        cell.setBorder(0);
                                        tabla.addCell(cell);

                                        cell = new PdfPCell(new Paragraph("Total: ",smallFont));
                                        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                                        cell.setBorder(0);
                                        tabla.addCell(cell);


                                        cell = new PdfPCell(new Paragraph("$",smallFont));
                                        cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                                        cell.setBorder(1);
                                        tabla.addCell(cell);

                                        //String resultSuma=StringHelper.roundDouble(suma, 2);
                                        cell = new PdfPCell(new Paragraph(StringHelper.AgregaComas(StringHelper.roundDouble(suma,2)),smallFont));
                                        cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                                        cell.setBorder(1);
                                        tabla.addCell(cell);

                                        cell = new PdfPCell(new Paragraph());
                                        cell.setColspan(3);
                                        cell.setBorder(0);
                                        tabla.addCell(cell);

                                        suma = 0.0;



                                        /////////////////////////

                                        cell = new PdfPCell(new Paragraph("Total General: ",smallFont));
                                        cell.setColspan(8);
                                        cell.setBorder(0);
                                        cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                                        tabla.addCell(cell);

                                        cell = new PdfPCell(new Paragraph("$",smallFont));
                                        cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                                        cell.setBorder(1);
                                        tabla.addCell(cell);


                                        cell = new PdfPCell(new Paragraph(StringHelper.AgregaComas(StringHelper.roundDouble(TGsuma,2)),smallFont));
                                        cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                                        cell.setBorder(1);
                                        tabla.addCell(cell);

                                        cell = new PdfPCell(new Paragraph());
                                        cell.setColspan(3);
                                        cell.setBorder(0);
                                        tabla.addCell(cell);
                                        //fin total venta del ultimo proveedor que se muestra en el pdf

                                        document.add(tabla); //aÃ±adiendo la tabla
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
            ColumnText.showTextAligned(writer.getDirectContent(),Element.ALIGN_CENTER, new Phrase(PdfEstadisticoComprasProducto.this.getEmpresa_emisora(),largeBoldFont),document.getPageSize().getWidth()/2, document.getPageSize().getTop() -25, 0);
            ColumnText.showTextAligned(writer.getDirectContent(),Element.ALIGN_CENTER, new Phrase("Reporte Compras Netas por Producto.",largeBoldFont),document.getPageSize().getWidth()/2, document.getPageSize().getTop()-38, 0);
            ColumnText.showTextAligned(writer.getDirectContent(),Element.ALIGN_CENTER, new Phrase(PdfEstadisticoComprasProducto.getFecha_reporte(),largeFont),document.getPageSize().getWidth()/2, document.getPageSize().getTop()-50, 0);

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
