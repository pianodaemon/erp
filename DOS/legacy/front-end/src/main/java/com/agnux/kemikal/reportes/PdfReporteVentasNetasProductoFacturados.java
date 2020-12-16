package com.agnux.kemikal.reportes;

import com.agnux.common.helpers.StringHelper;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.HashMap;

/*
 @author valentin.vale8490@gmail.com
 */
public class PdfReporteVentasNetasProductoFacturados {

    private String empresa_emisora;
    private static String fecha_reporte;
    private Integer tipo_reporte;
    private Integer tipo;
    private java.util.List<HashMap<String, String>> rows;
    //public java.util.List<HashMap<String, String>> sumatorias;

    private Integer getTipo() {
        return tipo;
    }

    private void setTipo(Integer tipo) {
        this.tipo = tipo;
    }    
    
    private Integer getTipo_reporte() {
        return tipo_reporte;
    }

    private void setTipo_reporte(Integer tipo_reporte) {
        this.tipo_reporte = tipo_reporte;
    }

    public static String getFecha_reporte() {
        return fecha_reporte;
    }

    private void setFecha_reporte(String fecha) {
        fecha_reporte = fecha;
    }

    /*public java.util.List<HashMap<String, String>> getSumatorias() {
        return sumatorias;
    }

    public void setSumatorias(java.util.List<HashMap<String, String>> sumatorias) {
        this.sumatorias = sumatorias;
    }*/

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

    public PdfReporteVentasNetasProductoFacturados(Integer tipo, Integer tipo_reporte, ArrayList<HashMap<String, String>> sumatorias, ArrayList<HashMap<String, String>> lista_ventas, String fecha_inicial, String fecha_final, String razon_social_empresa, String fileout) {
        this.setEmpresa_emisora(razon_social_empresa);
        this.setTipo_reporte(tipo_reporte);
        this.setTipo(tipo);
        
        //this.setSumatorias(sumatorias);
        HeaderFooter event = new HeaderFooter();
        Font fontCols = new Font(Font.FontFamily.HELVETICA, 9, Font.NORMAL);
        Font smallFont = new Font(Font.FontFamily.HELVETICA, 8, Font.NORMAL, BaseColor.BLACK);
        Font smallBoldFont = new Font(Font.getFamily("ARIAL"), 6, Font.BOLD, BaseColor.WHITE);
        Font smallFontN = new Font(Font.FontFamily.HELVETICA, 8, Font.BOLD, BaseColor.BLACK);
        PdfPTable tabla = null;

        PdfPCell cell;
        try {

            Document document = new Document(PageSize.LETTER.rotate(), -60, -60, 60, 30);
            PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(fileout));
            writer.setPageEvent(event);
            document.open();

            String[] fi = fecha_inicial.split("-");
            String[] ff = fecha_final.split("-");

            String fechaReporte = "DEL:      " + fi[2] + "/" + fi[1] + "/" + fi[0] + "      AL:      " + ff[2] + "/" + ff[1] + "/" + ff[0];
            this.setFecha_reporte(fechaReporte);
            
            float [] widths;
            String[] titulos;
            
            float [] widths1 = {1.7f, 6f, 1.6f, 2f, 1.3f, 1.2f, 0.5f, 1.2f, 0.5f, 1.2f, 1.4f, 1.2f,0.5f,1.2f,1.2f,1.2f,1.2f};
            float [] widths2 = {1.7f, 6f, 1.6f, 2f, 1.3f, 1.2f, 0.5f, 1.2f, 0.5f, 1.2f, 1.4f, 1.2f};
            
            String[] titulos1 = {"N.CONTROL", "CLIENTE", "FACTURA", "FECHA", "UNIDAD", "CANT.", "", "P.UNI.", "","V. NETA.", "MONEDA","T. C.","","% COSTO","% POND","% MOP","% M_MOP"};
            String[] titulos2 = {"N.CONTROL", "CLIENTE", "FACTURA", "FECHA", "UNIDAD", "CANT.", "", "P.UNI.", "","V. NETA.", "MONEDA","T. C."};
            
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
            tabla.setKeepTogether(false);
            tabla.setKeepTogether(true);
            tabla.setHeaderRows(1);
            
            for (int i = 0; i <= titulos.length - 1; i++) {
                cell = new PdfPCell(new Paragraph(titulos[i], smallBoldFont));
                cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                cell.setUseAscender(true);
                cell.setUseDescender(true);
                cell.setBackgroundColor(BaseColor.BLACK);
                cell.setBorder(0);
                cell.setBorderWidthLeft(0);
                tabla.addCell(cell);
            }
            //fin de for de insertar encabezado de la tabla
            String producto = "";
            String unidad = "";
            double totalventa = 0.0;
            double TGsuma = 0.0;
            double totalponderacion=0.0;
            double totalcosto =0.0;

            double totalmediamop =0.0;
            double totalmop=0.0;
            double sumaunidad = 0.0;
            double ventageneral=0.0;
            double costogeneral=0.0;
            producto = lista_ventas.get(0).get("producto").toString();
            unidad = lista_ventas.get(0).get("unidad").toString();
            
            cell = new PdfPCell(new Paragraph("Producto: " + producto, smallFont));
            cell.setColspan(18);
            cell.setBorder(0);
            tabla.addCell(cell);

            totalventa = metodo_sumatorias(this.getTipo_reporte(), sumatorias, producto);

            for (int j = 0; j <= lista_ventas.size() - 1; j++) {     //inicia for para recorrer las filas de la lista
                //para conocer el contenido de los hashmap en cada fila de la lista
                HashMap<String, String> registro = lista_ventas.get(j); //lista_ventas.get(0)
                
                if (producto.equals(registro.get("producto"))) {   //compara si todos los registros de la razon social
                    cell = new PdfPCell(new Paragraph(registro.get("numero_control").toString(), smallFont));
                    cell.setHorizontalAlignment(Element.ALIGN_LEFT);
                    cell.setBorder(0);
                    tabla.addCell(cell);
                    
                    cell = new PdfPCell(new Paragraph(registro.get("razon_social").toString(), smallFont));
                    cell.setHorizontalAlignment(Element.ALIGN_LEFT);
                    cell.setBorder(0);
                    tabla.addCell(cell);
                    
                    cell = new PdfPCell(new Paragraph(registro.get("factura").toString(), smallFont));
                    cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                    cell.setBorder(0);
                    tabla.addCell(cell);
                    
                    cell = new PdfPCell(new Paragraph(registro.get("fecha_factura").toString(), smallFont));
                    cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                    cell.setBorder(0);
                    tabla.addCell(cell);
                    
                    cell = new PdfPCell(new Paragraph(registro.get("unidad").toString(), smallFont));
                    cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                    cell.setBorder(0);
                    tabla.addCell(cell);
                    
                    //String cant=StringHelper.roundDouble(registro.get("cantidad").toString(), 2);
                    cell = new PdfPCell(new Paragraph(StringHelper.AgregaComas(StringHelper.roundDouble(registro.get("cantidad").toString(), 2)), smallFont));
                    cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                    cell.setBorder(0);
                    tabla.addCell(cell);
                    
                    cell = new PdfPCell(new Paragraph("$", smallFont));
                    cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                    cell.setBorder(0);
                    tabla.addCell(cell);
                    
                    //String precioUni=StringHelper.roundDouble(registro.get("precio_unitario").toString(), 2);
                    cell = new PdfPCell(new Paragraph(StringHelper.AgregaComas(StringHelper.roundDouble(registro.get("precio_unitario").toString(), 2)), smallFont));
                    cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                    cell.setBorder(0);
                    tabla.addCell(cell);
                    
                    cell = new PdfPCell(new Paragraph("$", smallFont));
                    cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                    cell.setBorder(0);
                    tabla.addCell(cell);
                    
                    cell = new PdfPCell(new Paragraph(StringHelper.AgregaComas(StringHelper.roundDouble(registro.get("venta_pesos").toString(), 2)), smallFont));
                    cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                    cell.setBorder(0);
                    tabla.addCell(cell);
                    
                    cell = new PdfPCell(new Paragraph(registro.get("moneda").toString(), smallFont));
                    cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                    cell.setBorder(0);
                    tabla.addCell(cell);
                    
                    cell = new PdfPCell(new Paragraph(StringHelper.AgregaComas(StringHelper.roundDouble(registro.get("tipo_cambio").toString(), 4)), smallFont));
                    cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                    cell.setBorder(0);
                    tabla.addCell(cell);
                    
                    if(this.getTipo()==1){
                        //NUEVO
                        cell = new PdfPCell(new Paragraph("$", smallFont));
                        cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                        cell.setBorder(0);
                        tabla.addCell(cell);

                        cell = new PdfPCell(new Paragraph(StringHelper.AgregaComas(StringHelper.roundDouble(registro.get("costo").toString(), 2)), smallFont));
                        cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                        cell.setBorder(0);
                        tabla.addCell(cell);
                        
                        cell = new PdfPCell(new Paragraph(StringHelper.AgregaComas(StringHelper.roundDouble((Double.parseDouble(registro.get("venta_pesos")) / (totalventa)) *100, 2)), smallFont));
                        cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                        cell.setBorder(0);
                        tabla.addCell(cell);

                        cell = new PdfPCell(new Paragraph(StringHelper.AgregaComas(StringHelper.roundDouble((Double.parseDouble(registro.get("venta_pesos")) - Double.parseDouble(registro.get("costo")))  /(Double.parseDouble(registro.get("venta_pesos"))) * 100 , 2)) , smallFont));
                        cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                        cell.setBorder(0);
                        tabla.addCell(cell);

                        cell = new PdfPCell(new Paragraph(StringHelper.AgregaComas(StringHelper.roundDouble((Double.parseDouble(registro.get("venta_pesos")) / (totalventa))  * (Double.parseDouble(registro.get("venta_pesos")) - Double.parseDouble(registro.get("costo"))) / (Double.parseDouble(registro.get("venta_pesos")))*100 , 2)) , smallFont));
                        cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                        cell.setBorder(0);
                        tabla.addCell(cell);
                        //FIN NUEVO
                    }
                    
                    sumaunidad = sumaunidad + Double.parseDouble(registro.get("cantidad").toString());
                    //suma = suma + Double.parseDouble(registro.get("venta_pesos").toString());
                    TGsuma = TGsuma + Double.parseDouble(registro.get("venta_pesos").toString());
                    
                    totalcosto=totalcosto + Double.parseDouble(registro.get("costo").toString());
                    totalponderacion = totalponderacion + (Double.parseDouble(registro.get("venta_pesos").toString()) / totalventa) * 100 ;
                    totalmop = totalmop + (Double.parseDouble(registro.get("venta_pesos")) - Double.parseDouble(registro.get("costo")))  /(Double.parseDouble(registro.get("venta_pesos"))) * 100;
                    totalmediamop = totalmediamop + (Double.parseDouble(registro.get("venta_pesos")) / (totalventa))  * (Double.parseDouble(registro.get("venta_pesos")) - Double.parseDouble(registro.get("costo"))) / (Double.parseDouble(registro.get("venta_pesos")))*100;
                    
                    ventageneral=ventageneral+ Double.parseDouble(registro.get("venta_pesos").toString());
                    costogeneral=costogeneral+ Double.parseDouble(registro.get("costo").toString());
                } else {
                    //si ya no coincide el valor del primer registro (0) agrega la ultima fila de cada
                    //cliente mostrando su total de la venta Neta.

                    cell = new PdfPCell(new Paragraph());
                    cell.setColspan(4);
                    cell.setBorder(0);
                    tabla.addCell(cell);

                    cell = new PdfPCell(new Paragraph("Total", smallFont));
                    cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                    cell.setBorder(1);
                    tabla.addCell(cell);

                    cell = new PdfPCell(new Paragraph(StringHelper.AgregaComas(StringHelper.roundDouble(sumaunidad, 2)), smallFont));
                    cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                    cell.setBorder(1);
                    tabla.addCell(cell);

                    cell = new PdfPCell(new Paragraph("", smallFont));
                    cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                    cell.setBorder(0);
                    tabla.addCell(cell);

                    cell = new PdfPCell(new Paragraph("Total", smallFont));
                    cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                    cell.setBorder(1);
                    tabla.addCell(cell);

                    cell = new PdfPCell(new Paragraph("$", smallFont));
                    cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                    cell.setBorder(1);
                    tabla.addCell(cell);

                    cell = new PdfPCell(new Paragraph(StringHelper.AgregaComas(StringHelper.roundDouble(totalventa, 2)), smallFont));
                    cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                    cell.setBorder(1);
                    tabla.addCell(cell);

                    cell = new PdfPCell(new Paragraph("", smallFont));
                    cell.setColspan(2);
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
                    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
                    
                    //Reiniciar valores
                    //suma = 0.0;
                    sumaunidad = 0.0;
                    totalcosto=0.0;
                    totalponderacion = 0.0;
                    totalmop = 0.0;
                    totalmediamop=0.0;
                    ////////////////////////////////
                    unidad =registro.get("unidad");
                    producto = registro.get("producto");
                    totalventa = metodo_sumatorias(this.getTipo_reporte(), sumatorias, producto);
                    ////////////////////////////////
                    
                    cell = new PdfPCell(new Paragraph("Producto: " + registro.get("producto"), smallFont));
                    cell.setColspan(18);
                    cell.setBorder(0);
                    tabla.addCell(cell);

                    cell = new PdfPCell(new Paragraph(registro.get("numero_control").toString(), smallFont));
                    cell.setHorizontalAlignment(Element.ALIGN_LEFT);
                    cell.setBorder(0);
                    tabla.addCell(cell);

                    cell = new PdfPCell(new Paragraph(registro.get("razon_social").toString(), smallFont));
                    cell.setHorizontalAlignment(Element.ALIGN_LEFT);
                    cell.setBorder(0);
                    tabla.addCell(cell);

                    cell = new PdfPCell(new Paragraph(registro.get("factura").toString(), smallFont));
                    cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                    cell.setBorder(0);
                    tabla.addCell(cell);

                    cell = new PdfPCell(new Paragraph(registro.get("fecha_factura").toString(), smallFont));
                    cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                    cell.setBorder(0);
                    tabla.addCell(cell);

                    cell = new PdfPCell(new Paragraph(registro.get("unidad").toString(), smallFont));
                    cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                    cell.setBorder(0);
                    tabla.addCell(cell);

                    cell = new PdfPCell(new Paragraph(StringHelper.AgregaComas(StringHelper.roundDouble(registro.get("cantidad").toString(), 2)), smallFont));
                    cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                    cell.setBorder(0);
                    tabla.addCell(cell);

                    cell = new PdfPCell(new Paragraph("$", smallFont));
                    cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                    cell.setBorder(0);
                    tabla.addCell(cell);

                    cell = new PdfPCell(new Paragraph(StringHelper.AgregaComas(StringHelper.roundDouble(registro.get("precio_unitario").toString(), 2)), smallFont));
                    cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                    cell.setBorder(0);
                    tabla.addCell(cell);

                    cell = new PdfPCell(new Paragraph("$", smallFont));
                    cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                    cell.setBorder(0);
                    tabla.addCell(cell);


                    cell = new PdfPCell(new Paragraph(StringHelper.AgregaComas(StringHelper.roundDouble(registro.get("venta_pesos").toString(), 2)), smallFont));
                    cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                    cell.setBorder(0);
                    tabla.addCell(cell);



                    cell = new PdfPCell(new Paragraph(registro.get("moneda").toString(), smallFont));
                    cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                    cell.setBorder(0);
                    tabla.addCell(cell);



                    cell = new PdfPCell(new Paragraph(StringHelper.AgregaComas(StringHelper.roundDouble(registro.get("tipo_cambio").toString(), 2)), smallFont));
                    cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                    cell.setBorder(0);
                    tabla.addCell(cell);
                    
                    if(this.getTipo()==1){
                        //NUEVO
                        cell = new PdfPCell(new Paragraph("$", smallFont));
                        cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                        cell.setBorder(0);
                        tabla.addCell(cell);

                        cell = new PdfPCell(new Paragraph(StringHelper.AgregaComas(StringHelper.roundDouble(registro.get("costo").toString(), 2)), smallFont));
                        cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                        cell.setBorder(0);
                        tabla.addCell(cell);
                        
                        cell = new PdfPCell(new Paragraph(StringHelper.AgregaComas(StringHelper.roundDouble((Double.parseDouble(registro.get("venta_pesos")) / (totalventa)) *100, 2)), smallFont));
                        cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                        cell.setBorder(0);
                        tabla.addCell(cell);

                        cell = new PdfPCell(new Paragraph(StringHelper.AgregaComas(StringHelper.roundDouble((Double.parseDouble(registro.get("venta_pesos")) - Double.parseDouble(registro.get("costo")))  /(Double.parseDouble(registro.get("venta_pesos"))) * 100 , 2)) , smallFont));
                        cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                        cell.setBorder(0);
                        tabla.addCell(cell);

                        cell = new PdfPCell(new Paragraph(StringHelper.AgregaComas(StringHelper.roundDouble((Double.parseDouble(registro.get("venta_pesos")) / (totalventa))  * (Double.parseDouble(registro.get("venta_pesos")) - Double.parseDouble(registro.get("costo"))) / (Double.parseDouble(registro.get("venta_pesos")))*100 , 2)) , smallFont));
                        cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                        cell.setBorder(0);
                        tabla.addCell(cell);
                        //FIN NUEVO
                    }
                    
                    //cliente = registro.get("razon_social");
                    //unidad = registro.get("unidad");
                    sumaunidad = sumaunidad + Double.parseDouble(registro.get("cantidad").toString());
                    //suma = suma + Double.parseDouble(registro.get("venta_pesos").toString());
                    TGsuma = TGsuma + Double.parseDouble(registro.get("venta_pesos").toString());

                    totalcosto=totalcosto + Double.parseDouble(registro.get("costo").toString());
                    totalponderacion = totalponderacion + (Double.parseDouble(registro.get("venta_pesos").toString()) / totalventa) * 100 ;
                    totalmop = totalmop + (Double.parseDouble(registro.get("venta_pesos")) - Double.parseDouble(registro.get("costo")))  /(Double.parseDouble(registro.get("venta_pesos"))) * 100;
                    totalmediamop = totalmediamop + (Double.parseDouble(registro.get("venta_pesos")) / (totalventa))  * (Double.parseDouble(registro.get("venta_pesos")) - Double.parseDouble(registro.get("costo"))) / (Double.parseDouble(registro.get("venta_pesos")))*100;

                    ventageneral=ventageneral+ Double.parseDouble(registro.get("venta_pesos").toString());
                    costogeneral=costogeneral+ Double.parseDouble(registro.get("costo").toString());

                }
            }//fin for recorrido de filas
            //total venta del ultimo cliente que se muestra en el pdf


           cell = new PdfPCell(new Paragraph());
            cell.setColspan(4);
            cell.setBorder(0);
            tabla.addCell(cell);

            cell = new PdfPCell(new Paragraph("Total", smallFont));
            cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            cell.setBorder(1);
            tabla.addCell(cell);

            cell = new PdfPCell(new Paragraph(StringHelper.AgregaComas(StringHelper.roundDouble(sumaunidad, 2)), smallFont));
            cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            cell.setBorder(1);
            tabla.addCell(cell);
            
            cell = new PdfPCell(new Paragraph());
            cell.setBorder(0);
            tabla.addCell(cell);

            cell = new PdfPCell(new Paragraph("Total ", smallFont));
            cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            cell.setBorder(1);
            tabla.addCell(cell);

            cell = new PdfPCell(new Paragraph("$", smallFont));
            cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            cell.setBorder(1);
            tabla.addCell(cell);

            //cell = new PdfPCell(new Paragraph(StringHelper.AgregaComas(StringHelper.roundDouble(suma, 2)), smallFont));
            cell = new PdfPCell(new Paragraph(StringHelper.AgregaComas(StringHelper.roundDouble(totalventa, 2)), smallFont));
            cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            cell.setBorder(1);
            tabla.addCell(cell);

            cell = new PdfPCell(new Paragraph());
            cell.setColspan(2);
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
            
            //fin total venta del ultimo cliente que se muestra en el pdf
            cell = new PdfPCell(new Paragraph("TOTAL GENERAL :",smallFont));
            cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            cell.setColspan(8);
            cell.setBorder(0);
            tabla.addCell(cell);

            cell = new PdfPCell(new Paragraph("$",smallFont));
            cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            cell.setBorder(0);
            tabla.addCell(cell);

            cell = new PdfPCell(new Paragraph(StringHelper.AgregaComas(StringHelper.roundDouble(ventageneral,2)),smallFont));
            cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            cell.setBorder(0);
            tabla.addCell(cell);

            cell = new PdfPCell(new Paragraph("",smallFont));
            cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            cell.setColspan(2);
            cell.setBorder(0);
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

                cell = new PdfPCell(new Paragraph(StringHelper.AgregaComas(StringHelper.roundDouble(((ventageneral/ventageneral) )*(((ventageneral-costogeneral)/ventageneral) )*100,2))+" %",smallFont));
                cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                cell.setBorder(1);
                tabla.addCell(cell);
            }
            
            document.add(tabla);
            document.close();
        } catch (Exception e) {
            e.printStackTrace();
        }






    }

    // class Sumatorias {
    private Double metodo_sumatorias(Integer tipo_reporte, ArrayList<HashMap<String, String>> array_sumatoria, String cliente_producto) {
        Double venta_total = 0.0;

        try {
            for (int j = 0; j <= array_sumatoria.size() - 1; j++) {     //inicia for para recorrer las filas de la lista
                HashMap<String, String> registro = array_sumatoria.get(j);
                if (this.getTipo_reporte() == 2 || this.getTipo_reporte() == 3) {
                    if (registro.get("producto").equals(cliente_producto)) {

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

    class HeaderFooter extends PdfPageEventHelper {

        //public Image headerImage;
        protected PdfTemplate total;
        protected BaseFont helv;
        protected PdfContentByte cb;
        Font largeBoldFont = new Font(Font.FontFamily.HELVETICA, 10, Font.BOLD, BaseColor.BLACK);
        Font largeFont = new Font(Font.FontFamily.HELVETICA, 10, Font.NORMAL, BaseColor.BLACK);
        Font smallFont = new Font(Font.FontFamily.HELVETICA, 7, Font.NORMAL, BaseColor.BLACK);

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
            } catch (Exception e) {
                throw new ExceptionConverter(e);
            }
        }

        /*añadimos pie de página, borde y más propiedades*/
        @Override
        public void onEndPage(PdfWriter writer, Document document) {
            //PdfContentByte cb = writer.getDirectContent();
            ColumnText.showTextAligned(writer.getDirectContent(), Element.ALIGN_CENTER, new Phrase(PdfReporteVentasNetasProductoFacturados.this.getEmpresa_emisora(), largeBoldFont), document.getPageSize().getWidth() / 2, document.getPageSize().getTop() - 25, 0);
            if(PdfReporteVentasNetasProductoFacturados.this.getTipo()==1){
                ColumnText.showTextAligned(writer.getDirectContent(), Element.ALIGN_CENTER, new Phrase("Reporte Ventas Netas por Producto.", largeBoldFont), document.getPageSize().getWidth() / 2, document.getPageSize().getTop() - 38, 0);
            }else{
                ColumnText.showTextAligned(writer.getDirectContent(), Element.ALIGN_CENTER, new Phrase("Reporte Comercial por Producto.", largeBoldFont), document.getPageSize().getWidth() / 2, document.getPageSize().getTop() - 38, 0);
            }
            ColumnText.showTextAligned(writer.getDirectContent(), Element.ALIGN_CENTER, new Phrase(PdfReporteVentasNetasProductoFacturados.getFecha_reporte(), largeFont), document.getPageSize().getWidth() / 2, document.getPageSize().getTop() - 50, 0);

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
            cb.addTemplate(total, document.right() - adjust, textBase);  //definir la posicion del total de paginas
            //cb.restoreState();
        }

        /*aqui escrimos ls paginas totales, para que nos salga de pie de pagina Pagina x de y*/
        @Override
        public void onCloseDocument(PdfWriter writer, Document document) {
            total.beginText();
            total.setFontAndSize(helv, 7);
            total.setTextMatrix(0, 0);
            total.showText(String.valueOf(writer.getPageNumber() - 1));
            total.endText();
        }
    }
}
