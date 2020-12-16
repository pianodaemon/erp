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
 @author valentin.vale8490@gmail.com
 */
public class PdfReporteVentasNetasSumatoriaxClientes {
    private String  empresa_emisora;
    private static String fecha_reporte;
    private Integer tipo_reporte;
    private Integer tipo;
    
    public static String getFecha_reporte() {
        return fecha_reporte;
    }

    private Integer getTipo() {
        return tipo;
    }

    private void setTipo(Integer tipo) {
        this.tipo = tipo;
    }    
    
    private void setFecha_reporte(String fecha) {
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

    private void setEmpresa_emisora(String empresa_emisora) {
        this.empresa_emisora = empresa_emisora;
    }
    private Integer getTipo_reporte() {
        return tipo_reporte;
    }

    private void setTipo_reporte(Integer tipo_reporte) {
        this.tipo_reporte = tipo_reporte;
    }
    
    public PdfReporteVentasNetasSumatoriaxClientes(Integer tipo, Integer tipo_reporte, ArrayList<HashMap<String, String>> sumatorias, ArrayList<HashMap<String, String>> lista_ventas,String producto,String fecha_inicial,String fecha_final, String razon_social_empresa, String fileout) {
        this.setEmpresa_emisora(razon_social_empresa);
        this.setTipo_reporte(tipo_reporte);
        this.setTipo(tipo);
        
        HeaderFooter event = new HeaderFooter();

        Font fontCols = new Font(Font.FontFamily.HELVETICA, 9,Font.NORMAL);
        Font smallFont = new Font(Font.FontFamily.HELVETICA,8,Font.NORMAL,BaseColor.BLACK);
        Font smallBoldFont = new Font(Font.getFamily("ARIAL"),6,Font.BOLD,BaseColor.WHITE);
        Font smallFontN = new Font(Font.FontFamily.HELVETICA,8,Font.BOLD,BaseColor.BLACK);
        PdfPTable table_titulos;
        PdfPTable tabla = null;

        PdfPCell cell;
        try{

            Document document = null;
            
            if(this.getTipo()==1){
                document = new Document(PageSize.LETTER.rotate(),-50,-50,60,30);
            }else{
                document = new Document(PageSize.LETTER,-50,-50,60,30);
            }
            PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(fileout));
            writer.setPageEvent(event);
            document.open();
            //TABLA DE FECHAS

            String[] fi = fecha_inicial.split("-");
            String[] ff = fecha_final.split("-");

            String fechaReporte = "DEL:      "+fi[2]+"/"+fi[1]+"/"+fi[0] + "      AL:      " +ff[2]+"/"+ff[1]+"/"+ff[0];
            this.setFecha_reporte(fechaReporte);
            
            float [] widths;
            String[] titulos;
            
            float [] widths1 = {12f,2f,2f,0.5f,2f,0.5f,2.5f,0.5f,2f,2.5f,2.5f,2.5f};
            float [] widths2 = {12f,2f,2f,0.5f,2f,0.5f,2.5f};
            
            String[] titulos1 = {"CLIENTE","UNIDAD","TOTAL CANTIDAD","","PRECIO PROMEDIO","","VENTA TOTAL","","COSTO","% POND","% MOP","% M_MOP"};
            String[] titulos2 = {"CLIENTE","UNIDAD","TOTAL CANTIDAD","","PRECIO PROMEDIO","","VENTA TOTAL"};
            
            //Tipo=1 Ventas Netas por Producto
            //Tipo=2 Reporte comercial para agentes de Ventas
            if(this.getTipo()==1){
                widths = widths1;
                titulos = titulos1;
            }else{
                widths = widths2;
                titulos = titulos2;
            }
            
            tabla = new PdfPTable(widths);
            tabla.setHeaderRows(1);
            tabla.setKeepTogether(false);
            tabla.setKeepTogether(true);
            
            for (int i = 0; i<=titulos.length -1; i++){
                cell = new PdfPCell(new Paragraph(titulos[i],smallBoldFont));
                String CLIENTE = titulos[i];
                if("CLIENTE".equals(titulos[0])){
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

            String cliente ="";
            String unidad="";
            double Tventageneral=0.0;
            double Tcostogeneral=0.0;
            double cantidad=0.0;
            double precio_unitario=0.0;
            double venta_neta=0.0;
            double costoxcliente =0.0;
            double ventageneral=0.0;
            double costogeneral=0.0;
            double totalcosto =0.0;
            double totalponderacion = 0.0;
            double totalmop=0.0;
            double totalmediamop =0.0;
            double totalventa =0.0;
            
            for (int m=0; m<=lista_ventas.size()-1; m++){
                HashMap<String, String> rgstro= lista_ventas.get(m);
                Tventageneral = Tventageneral+Double.parseDouble(rgstro.get("venta_pesos").toString());
                Tcostogeneral =Tcostogeneral+Double.parseDouble(rgstro.get("costo").toString());
            }

            cliente =lista_ventas.get(0).get("razon_social").toString();
            unidad=lista_ventas.get(0).get("unidad").toString();
            totalventa = metodo_sumatorias(this.getTipo_reporte(), sumatorias,cliente);

            for (int j=0; j<=lista_ventas.size()-1; j++){     //inicia for para recorrer las filas de la lista
                //para conocer el contenido de los hashmap en cada fila de la lista
                HashMap<String, String> registro= lista_ventas.get(j);

                if(cliente.equals(registro.get("razon_social"))){

                    cantidad=cantidad + Double.parseDouble(registro.get("cantidad"));
                    precio_unitario=precio_unitario + Double.parseDouble(registro.get("precio_unitario"));
                    venta_neta=venta_neta + Double.parseDouble(registro.get("venta_pesos"));
                    costoxcliente=costoxcliente + Double.parseDouble(registro.get("costo"));

                    ventageneral=ventageneral+ Double.parseDouble(registro.get("venta_pesos").toString());
                    costogeneral=costogeneral+ Double.parseDouble(registro.get("costo").toString());

                    totalcosto=totalcosto + Double.parseDouble(registro.get("costo").toString());

                    totalponderacion=(venta_neta/Tventageneral)*100;
                    totalmop=(((venta_neta-costoxcliente)/venta_neta)*100);
                    totalmediamop=((venta_neta / Tventageneral )*100)*(((venta_neta-costoxcliente)/venta_neta)*100);
                }else{
                    cell = new PdfPCell(new Paragraph(cliente.toString(),smallFont));
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

                    cell = new PdfPCell(new Paragraph(StringHelper.AgregaComas(StringHelper.roundDouble(venta_neta,2)),smallFont));
                    cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                    cell.setBorder(0);
                    tabla.addCell(cell);
                    
                    if(this.getTipo()==1){
                        //NUEVO
                        cell = new PdfPCell(new Paragraph("$", smallFont));
                        cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                        cell.setBorder(0);
                        tabla.addCell(cell);

                        cell = new PdfPCell(new Paragraph(""+StringHelper.AgregaComas(StringHelper.roundDouble(totalcosto,2 )), smallFont));
                        cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                        cell.setBorder(0);
                        tabla.addCell(cell);
                        
                        cell = new PdfPCell(new Paragraph(""+StringHelper.AgregaComas(StringHelper.roundDouble(totalponderacion,2 ))+" %", smallFont));
                        cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                        cell.setBorder(0);
                        tabla.addCell(cell);

                        cell = new PdfPCell(new Paragraph(""+StringHelper.AgregaComas(StringHelper.roundDouble(totalmop,2 ))+" %", smallFont));
                        cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                        cell.setBorder(0);
                        tabla.addCell(cell);

                        cell = new PdfPCell(new Paragraph(""+StringHelper.AgregaComas(StringHelper.roundDouble(totalmediamop,2 ))+" %", smallFont));
                        cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                        cell.setBorder(0);
                        tabla.addCell(cell);
                        //FIN NUEVO
                    }
                    
                    //reinicio valores
                    //cliente=registro.get("razon_social");
                    cantidad=0.0;
                    precio_unitario=0.0;
                    venta_neta=0.0;
                    costoxcliente=0.0;
                    totalcosto=0.0;
                    totalponderacion=0.0;
                    totalmop=0.0;
                    totalmediamop=0.0;
                    
                    cliente =registro.get("razon_social").toString();
                    unidad=registro.get("unidad").toString();
                    totalventa = metodo_sumatorias(this.getTipo_reporte(), sumatorias,cliente);
                    
                    cantidad=cantidad + Double.parseDouble(registro.get("cantidad"));
                    precio_unitario=precio_unitario + Double.parseDouble(registro.get("precio_unitario"));
                    venta_neta=venta_neta + Double.parseDouble(registro.get("venta_pesos"));
                    costoxcliente=costoxcliente + Double.parseDouble(registro.get("costo"));

                    ventageneral=ventageneral+ Double.parseDouble(registro.get("venta_pesos").toString());
                    costogeneral=costogeneral+ Double.parseDouble(registro.get("costo").toString());

                    totalcosto=totalcosto + Double.parseDouble(registro.get("costo").toString());

                    //totalponderacion = totalponderacion + (Double.parseDouble(registro.get("venta_pesos").toString()) / totalventa) * 100 ;
                    //totalmop = totalmop + (Double.parseDouble(registro.get("venta_pesos")) - Double.parseDouble(registro.get("costo")))  /(Double.parseDouble(registro.get("venta_pesos"))) * 100;
                    //totalmediamop = totalmediamop + (Double.parseDouble(registro.get("venta_pesos")) / (totalventa))  * (Double.parseDouble(registro.get("venta_pesos")) - Double.parseDouble(registro.get("costo"))) / (Double.parseDouble(registro.get("venta_pesos")))*100;

                    totalponderacion=(venta_neta/Tventageneral)*100;
                    totalmop=((venta_neta-costoxcliente)/venta_neta)*100;
                    totalmediamop=((venta_neta / Tventageneral )*100)*(((venta_neta-costoxcliente)/venta_neta)*100);
                }
            }//fin for recorrido de filas

            cell = new PdfPCell(new Paragraph(cliente.toString(),smallFont));
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
            
            cell = new PdfPCell(new Paragraph(StringHelper.AgregaComas(StringHelper.roundDouble(venta_neta,2)),smallFont));
            cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            cell.setBorder(0);
            tabla.addCell(cell);
            
            if(this.getTipo()==1){
                //NUEVO
                cell = new PdfPCell(new Paragraph("$", smallFont));
                cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                cell.setBorder(0);
                tabla.addCell(cell);

                cell = new PdfPCell(new Paragraph(""+StringHelper.AgregaComas(StringHelper.roundDouble(totalcosto,2 )), smallFont));
                cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                cell.setBorder(0);
                tabla.addCell(cell);
                
                cell = new PdfPCell(new Paragraph(""+StringHelper.AgregaComas(StringHelper.roundDouble(totalponderacion,2 ))+" %", smallFont));
                cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                cell.setBorder(0);
                tabla.addCell(cell);

                cell = new PdfPCell(new Paragraph(""+StringHelper.AgregaComas(StringHelper.roundDouble(totalmop,2 ))+" %", smallFont));
                cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                cell.setBorder(0);
                tabla.addCell(cell);

                cell = new PdfPCell(new Paragraph(""+StringHelper.AgregaComas(StringHelper.roundDouble(totalmediamop,2 ))+" %", smallFont));
                cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                cell.setBorder(0);
                tabla.addCell(cell);
                //FIN NUEVO
            }
            
            
            cell = new PdfPCell(new Paragraph("TOTAL GENERAL :",smallFont));
            cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            cell.setColspan(5);
            cell.setBorder(0);
            tabla.addCell(cell);

            cell = new PdfPCell(new Paragraph("$",smallFont));
            cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            cell.setBorder(1);
            tabla.addCell(cell);

            cell = new PdfPCell(new Paragraph(StringHelper.AgregaComas(StringHelper.roundDouble(ventageneral,2)),smallFont));
            cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            cell.setBorder(1);
            tabla.addCell(cell);
            
            if(this.getTipo()==1){
                cell = new PdfPCell(new Paragraph("$",smallFont));
                cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                cell.setBorder(1);
                tabla.addCell(cell);

                cell = new PdfPCell(new Paragraph(StringHelper.AgregaComas(StringHelper.roundDouble(costogeneral,2)),smallFont));
                cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                cell.setBorder(1);
                tabla.addCell(cell);
                
                cell = new PdfPCell(new Paragraph(StringHelper.AgregaComas(StringHelper.roundDouble((ventageneral/ventageneral)*100,2))+" %",smallFont));
                cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                cell.setBorder(1);
                tabla.addCell(cell);

                cell = new PdfPCell(new Paragraph(StringHelper.AgregaComas(StringHelper.roundDouble(((ventageneral-costogeneral)/ventageneral) *100,2))+" %",smallFont));
                cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                cell.setBorder(1);
                tabla.addCell(cell);

                cell = new PdfPCell(new Paragraph(StringHelper.AgregaComas(StringHelper.roundDouble(((ventageneral/ventageneral) *100)*(((ventageneral-costogeneral)/ventageneral) *100),2))+" %",smallFont));
                cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                cell.setBorder(1);
                tabla.addCell(cell);
            }
            
            document.add(tabla); //añadiendo la tabla
            document.close();
        }catch (Exception e) {
                e.printStackTrace();
        }
    }
    // class Sumatorias {
    private Double metodo_sumatorias(Integer tipo_reporte, ArrayList<HashMap<String, String>> array_sumatoria, String cliente_producto) {
        Double venta_total = 0.0;
        
        try {
            for (int j = 0; j <= array_sumatoria.size() - 1; j++) {     //inicia for para recorrer las filas de la lista
                HashMap<String, String> registro = array_sumatoria.get(j);
                if (this.getTipo_reporte() == 1 || this.getTipo_reporte() == 4) {
                    if (registro.get("cliente").trim().equals(cliente_producto.trim())) {

                        venta_total = Double.parseDouble(registro.get("venta"));
                    }
                }
            }

        } catch (Exception e) {
            System.out.println(e);
        }
        //System.out.println("retorno de venta neta:::" + venta_total);
        return venta_total;
    }
    //}




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
            ColumnText.showTextAligned(writer.getDirectContent(),Element.ALIGN_CENTER, new Phrase(PdfReporteVentasNetasSumatoriaxClientes.this.getEmpresa_emisora(),largeBoldFont),document.getPageSize().getWidth()/2, document.getPageSize().getTop() -25, 0);
            if(PdfReporteVentasNetasSumatoriaxClientes.this.getTipo()==1){
                ColumnText.showTextAligned(writer.getDirectContent(), Element.ALIGN_CENTER, new Phrase("Reporte Ventas Netas por Producto.", largeBoldFont), document.getPageSize().getWidth() / 2, document.getPageSize().getTop() - 38, 0);
            }else{
                ColumnText.showTextAligned(writer.getDirectContent(),Element.ALIGN_CENTER, new Phrase("Reporte Comercial Sumarizado por Cliente.",largeBoldFont),document.getPageSize().getWidth()/2, document.getPageSize().getTop()-38, 0);
            }
            ColumnText.showTextAligned(writer.getDirectContent(),Element.ALIGN_CENTER, new Phrase(PdfReporteVentasNetasSumatoriaxClientes.getFecha_reporte(),largeFont),document.getPageSize().getWidth()/2, document.getPageSize().getTop()-50, 0);

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
