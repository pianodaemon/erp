/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.agnux.kemikal.reportes;

import com.agnux.common.helpers.StringHelper;
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
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.ExceptionConverter;
import com.itextpdf.text.Font;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.ColumnText;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author agnux
 */
public final class PdfReporteCobrazaAgente {

    public static String razon_social_empresa;
    public static String fecha_reporte;
    public static Integer tipo_reporte;
    public static Integer tipo_comision;

    public static Integer getTipo_comision() {
        return tipo_comision;
    }

    public static void setTipo_comision(Integer tipo_comision) {
        PdfReporteCobrazaAgente.tipo_comision = tipo_comision;
    }
    public static String nombre_reporte;

    public static String getNombre_reporte() {
        return nombre_reporte;
    }

    public static void setNombre_reporte(String nombre_reporte) {
        PdfReporteCobrazaAgente.nombre_reporte = nombre_reporte;
    }

    public static Integer getTipo_reporte() {
        return tipo_reporte;
    }

    public static void setTipo_reporte(Integer tipo_reporte) {
        PdfReporteCobrazaAgente.tipo_reporte = tipo_reporte;
    }

    public static String getFecha_reporte() {
        return fecha_reporte;
    }

    public static void setFecha_reporte(String fecha_reporte) {
        PdfReporteCobrazaAgente.fecha_reporte = fecha_reporte;
    }

    public static String getRazon_social_empresa() {
        return razon_social_empresa;
    }

    public static void setRazon_social_empresa(String razon_social_empresa) {
        PdfReporteCobrazaAgente.razon_social_empresa = razon_social_empresa;
    }

//   public PdfReporteCobrazaAgente(String fileout, ArrayList<HashMap<String, String>> lista_cobranza, String razon_social_empresa, String fecha_inicial, String fecha_final) throws DocumentException {
    public PdfReporteCobrazaAgente(String fileout, ArrayList<HashMap<String, String>> cobranza_venta, Integer opcion_seleccionada, Integer tipo_comision, String razon_social_empresa, String fecha_inicial, String fecha_final) throws DocumentException {
        HashMap<String, String> datos = new HashMap<String, String>();

        int tmp = 0;

        int primer_registro = 0;
        String simbolo_moneda = "";
        String nombre_agente = "";
        String denominacion = "SD";
        Double sumatotalvalor = 0.0;
        Double sumatotalcomision = 0.0;

        Double sumatotalsubtotal = 0.0;
        String[] fi = fecha_inicial.split("-");
        String[] ff = fecha_final.split("-");

        String fecha_reporte = "Periodo  del  " + fi[2] + "/" + fi[1] + "/" + fi[0] + "  al  " + ff[2] + "/" + ff[1] + "/" + ff[0];
        this.setRazon_social_empresa(razon_social_empresa);
        this.setFecha_reporte(fecha_reporte);
        this.setTipo_reporte(opcion_seleccionada);
        this.setTipo_comision(tipo_comision);
        Integer tipo_comsion = this.getTipo_comision();
        Integer tipo = this.getTipo_reporte();
        String nombre_reporte = "";
        if (tipo_comsion == 2 && tipo == 1) {
            nombre_reporte = "Reporte de Comisiones por monto factura";
        }

        if (tipo == 1 && tipo_comsion == 1) {
            nombre_reporte = "Reporte de Cobranza por Agente";
        }
        if (tipo == 2) {
            nombre_reporte = "Reporte de Ventas por Agente ";
        }

        //datos para el encabezado
        datos.put("empresa", razon_social_empresa);
        datos.put("titulo_reporte", nombre_reporte);
        datos.put("periodo", fecha_reporte);

        //datos para el pie de pagina
        datos.put("codigo1", "");
        datos.put("codigo2", "");


        try {
            Font fontCols = new Font(Font.FontFamily.HELVETICA, 9, Font.NORMAL);
            Font smallFont = new Font(Font.FontFamily.HELVETICA, 8, Font.NORMAL, BaseColor.BLACK);
            Font smallBoldFont = new Font(Font.getFamily("ARIAL"), 8, Font.BOLD, BaseColor.WHITE);
            Font smallFontN = new Font(Font.FontFamily.HELVETICA, 8, Font.BOLD, BaseColor.BLACK);

            // aqui va  header foother
            HeaderFooter event = new HeaderFooter(datos);
            Document doc = new Document(PageSize.LETTER.rotate(), -50, -50, 60, 30);
            doc.addCreator("valentin.vale8490@gmail.com");
            PdfWriter writer = PdfWriter.getInstance(doc, new FileOutputStream(fileout));
            //PdfWriter writer = PdfWriter.getInstance(doc, new FileOutputStream(fileout));
            writer.setPageEvent(event);


            doc.open();
            Integer tipo_rep = this.getTipo_reporte();
            if (tipo_rep == 1 && tipo_comision == 1) {
                System.out.println("IMprimiendo el reporte de cobranza por agente");
                //String[] columnas = {"No.Control","Nombre","No.Factura","Cliente","","Valor","% Comision","","Total Comision"};
                float[] widths = {
                    1.5f,
                    2f,
                    5,
                    1.5f,
                    1,
                    0.7f,
                    1.3f,
                    1.3f,
                    0.7f,
                    1.5f};//Tamaño de las Columnas.
                PdfPTable tabla = new PdfPTable(widths);
                PdfPCell cell;
                tabla.setKeepTogether(false);
                tabla.setHeaderRows(1);

                cell = new PdfPCell(new Paragraph("No. FACTURA", smallBoldFont));
                cell.setUseAscender(true);
                cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                cell.setUseDescender(true);
                cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                cell.setBackgroundColor(BaseColor.BLACK);
                tabla.addCell(cell);

                cell = new PdfPCell(new Paragraph("FECHA FACTURA", smallBoldFont));
                cell.setUseAscender(true);
                cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                cell.setUseDescender(true);
                cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                cell.setBackgroundColor(BaseColor.BLACK);
                tabla.addCell(cell);

                cell = new PdfPCell(new Paragraph("CLIENTE", smallBoldFont));
                cell.setUseAscender(true);
                cell.setHorizontalAlignment(Element.ALIGN_LEFT);
                cell.setUseDescender(true);
                cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                cell.setBackgroundColor(BaseColor.BLACK);
                tabla.addCell(cell);

                cell = new PdfPCell(new Paragraph("FECHA PAGO", smallBoldFont));
                cell.setUseAscender(true);
                cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                cell.setUseDescender(true);
                cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                cell.setBackgroundColor(BaseColor.BLACK);
                tabla.addCell(cell);

                cell = new PdfPCell(new Paragraph("No. DIAS", smallBoldFont));
                cell.setUseAscender(true);
                cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                cell.setUseDescender(true);
                cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                cell.setBackgroundColor(BaseColor.BLACK);
                tabla.addCell(cell);

                cell = new PdfPCell(new Paragraph("SUBTOTAL", smallBoldFont));
                cell.setUseAscender(true);
                cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                cell.setUseDescender(true);
                cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                cell.setBackgroundColor(BaseColor.BLACK);
                cell.setColspan(2);
                tabla.addCell(cell);

                cell = new PdfPCell(new Paragraph("% COMISION", smallBoldFont));
                cell.setUseAscender(true);
                cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                cell.setUseDescender(true);
                cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                cell.setBackgroundColor(BaseColor.BLACK);
                tabla.addCell(cell);

                cell = new PdfPCell(new Paragraph("T.COMISION", smallBoldFont));
                cell.setUseAscender(true);
                cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                cell.setUseDescender(true);
                cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                cell.setBackgroundColor(BaseColor.BLACK);
                //cell.setFixedHeight(13);
                cell.setColspan(2);
                tabla.addCell(cell);

                for (int x = 0; x <= (cobranza_venta.size()) - 1; x++) {
                    HashMap<String, String> registro = cobranza_venta.get(x);

                    if (primer_registro == 0) {
                        nombre_agente = registro.get("nombre_agente").toString();
                        denominacion = registro.get("moneda_factura").toString();

                        if (registro.get("moneda_factura").equals("M.N.")) {
                            simbolo_moneda = "$";
                        }

                        if (registro.get("moneda_factura").equals("USD")) {
                            simbolo_moneda = "USD";
                        }
                        primer_registro = 1;
                    }


                    if (nombre_agente.equals(registro.get("nombre_agente")) && denominacion.equals(registro.get("moneda_factura"))) {
                        if (tmp == 0) {

                            cell = new PdfPCell(new Paragraph((String) registro.get("nombre_agente"), smallFontN));
                            cell.setHorizontalAlignment(Element.ALIGN_LEFT);
                            cell.setBorder(0);
                            cell.setColspan(10);
                            tabla.addCell(cell);

                            cell = new PdfPCell(new Paragraph((String) registro.get("serie_folio"), smallFont));
                            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                            cell.setBorder(0);
                            tabla.addCell(cell);

                            cell = new PdfPCell(new Paragraph((String) registro.get("fecha_factura"), smallFont));
                            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                            cell.setBorder(0);
                            tabla.addCell(cell);

                            cell = new PdfPCell(new Paragraph((String) registro.get("cliente"), smallFont));
                            cell.setHorizontalAlignment(Element.ALIGN_LEFT);
                            cell.setBorder(0);
                            tabla.addCell(cell);

                            cell = new PdfPCell(new Paragraph((String) registro.get("fecha_pago"), smallFont));
                            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                            cell.setBorder(0);
                            tabla.addCell(cell);

                            cell = new PdfPCell(new Paragraph((String) registro.get("numero_dias_pago"), smallFont));
                            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                            cell.setBorder(0);
                            tabla.addCell(cell);

                            cell = new PdfPCell(new Paragraph(simbolo_moneda, smallFont));
                            cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                            cell.setBorder(0);
                            tabla.addCell(cell);

                            cell = new PdfPCell(new Paragraph(StringHelper.AgregaComas(StringHelper.roundDouble(registro.get("subtotal"), 2)), smallFont));
                            cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                            cell.setBorder(0);
                            tabla.addCell(cell);

                            cell = new PdfPCell(new Paragraph(StringHelper.AgregaComas(StringHelper.roundDouble(registro.get("comision_por_fecha"), 2) + "%"), smallFont));
                            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                            cell.setBorder(0);
                            tabla.addCell(cell);

                            cell = new PdfPCell(new Paragraph(simbolo_moneda, smallFont));
                            cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                            cell.setBorder(0);
                            tabla.addCell(cell);

                            cell = new PdfPCell(new Paragraph(StringHelper.AgregaComas(StringHelper.roundDouble(registro.get("total_comision_por_fecha"), 2)), smallFont));
                            cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                            cell.setBorder(0);
                            tabla.addCell(cell);
                        }

                        if (tmp != 0) {
                            cell = new PdfPCell(new Paragraph("", smallFont));
                            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                            cell.setBorder(0);
                            cell.setColspan(10);
                            tabla.addCell(cell);

                            cell = new PdfPCell(new Paragraph((String) registro.get("serie_folio"), smallFont));
                            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                            cell.setBorder(0);
                            tabla.addCell(cell);

                            cell = new PdfPCell(new Paragraph((String) registro.get("fecha_factura"), smallFont));
                            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                            cell.setBorder(0);
                            tabla.addCell(cell);

                            cell = new PdfPCell(new Paragraph((String) registro.get("cliente"), smallFont));
                            cell.setHorizontalAlignment(Element.ALIGN_LEFT);
                            cell.setBorder(0);
                            tabla.addCell(cell);

                            cell = new PdfPCell(new Paragraph((String) registro.get("fecha_pago"), smallFont));
                            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                            cell.setBorder(0);
                            tabla.addCell(cell);

                            cell = new PdfPCell(new Paragraph((String) registro.get("numero_dias_pago"), smallFont));
                            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                            cell.setBorder(0);
                            tabla.addCell(cell);

                            cell = new PdfPCell(new Paragraph(simbolo_moneda, smallFont));
                            cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                            cell.setBorder(0);
                            tabla.addCell(cell);

                            cell = new PdfPCell(new Paragraph(StringHelper.AgregaComas(StringHelper.roundDouble(registro.get("subtotal"), 2)), smallFont));
                            cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                            cell.setBorder(0);
                            tabla.addCell(cell);

                            cell = new PdfPCell(new Paragraph(StringHelper.AgregaComas(StringHelper.roundDouble(registro.get("comision_por_fecha"), 2) + "%"), smallFont));
                            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                            cell.setBorder(0);
                            tabla.addCell(cell);

                            cell = new PdfPCell(new Paragraph(simbolo_moneda, smallFont));
                            cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                            cell.setBorder(0);
                            tabla.addCell(cell);

                            cell = new PdfPCell(new Paragraph(StringHelper.AgregaComas(StringHelper.roundDouble(registro.get("total_comision_por_fecha"), 2)), smallFont));
                            cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                            cell.setBorder(0);
                            tabla.addCell(cell);
                        }

                        sumatotalvalor = sumatotalvalor + Double.parseDouble(registro.get("subtotal").toString());
                        sumatotalcomision = sumatotalcomision + Double.parseDouble(registro.get("total_comision_por_fecha").toString());
                        tmp = 1;

                    } else {
                        /*
                         cell= new PdfPCell(new Paragraph("",smallFont));
                         cell.setHorizontalAlignment (Element.ALIGN_CENTER);
                         cell.setBorder(0);
                         tabla.addCell(cell);
                         */
                        cell = new PdfPCell(new Paragraph("", smallFont));
                        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                        cell.setBorder(0);
                        tabla.addCell(cell);

                        cell = new PdfPCell(new Paragraph("", smallFont));
                        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                        cell.setBorder(0);
                        tabla.addCell(cell);

                        cell = new PdfPCell(new Paragraph("", smallFont));
                        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                        cell.setBorder(0);
                        tabla.addCell(cell);

                        cell = new PdfPCell(new Paragraph("TOTAL : ", smallFont));
                        cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                        cell.setBorder(0);
                        cell.setColspan(2);
                        tabla.addCell(cell);

                        cell = new PdfPCell(new Paragraph(simbolo_moneda, smallFont));
                        cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                        cell.setBorder(1);
                        tabla.addCell(cell);

                        cell = new PdfPCell(new Paragraph(StringHelper.AgregaComas(StringHelper.roundDouble(sumatotalvalor, 2)), smallFont));
                        cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                        cell.setBorder(1);
                        tabla.addCell(cell);

                        cell = new PdfPCell(new Paragraph("", smallFont));
                        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                        cell.setBorder(1);
                        tabla.addCell(cell);

                        cell = new PdfPCell(new Paragraph(simbolo_moneda, smallFont));
                        cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                        cell.setBorder(1);
                        tabla.addCell(cell);

                        cell = new PdfPCell(new Paragraph(StringHelper.AgregaComas(StringHelper.roundDouble(sumatotalcomision, 2)), smallFont));
                        cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                        cell.setBorder(1);
                        tabla.addCell(cell);

                        //fila vacia
                        cell = new PdfPCell(new Paragraph("", smallFont));
                        cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                        cell.setBorder(0);
                        cell.setColspan(11);

                        tabla.addCell(cell);


                        //inicializando variable
                        sumatotalvalor = 0.0;
                        sumatotalcomision = 0.0;
                        //fin del inicializado de varables


                        if (registro.get("moneda_factura").equals("M.N.")) {
                            simbolo_moneda = "$";
                        }
                        if (registro.get("moneda_factura").equals("USD")) {
                            simbolo_moneda = "USD";
                        }

                        cell = new PdfPCell(new Paragraph((String) registro.get("nombre_agente"), smallFontN));
                        cell.setHorizontalAlignment(Element.ALIGN_LEFT);
                        cell.setBorder(0);
                        cell.setColspan(10);
                        tabla.addCell(cell);



                        cell = new PdfPCell(new Paragraph((String) registro.get("serie_folio"), smallFont));
                        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                        cell.setBorder(0);
                        tabla.addCell(cell);

                        cell = new PdfPCell(new Paragraph((String) registro.get("fecha_factura"), smallFont));
                        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                        cell.setBorder(0);
                        tabla.addCell(cell);

                        cell = new PdfPCell(new Paragraph((String) registro.get("cliente"), smallFont));
                        cell.setHorizontalAlignment(Element.ALIGN_LEFT);
                        cell.setBorder(0);
                        tabla.addCell(cell);

                        cell = new PdfPCell(new Paragraph((String) registro.get("fecha_pago"), smallFont));
                        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                        cell.setBorder(0);
                        tabla.addCell(cell);

                        cell = new PdfPCell(new Paragraph((String) registro.get("numero_dias_pago"), smallFont));
                        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                        cell.setBorder(0);
                        tabla.addCell(cell);

                        cell = new PdfPCell(new Paragraph(simbolo_moneda, smallFont));
                        cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                        cell.setBorder(0);
                        tabla.addCell(cell);

                        cell = new PdfPCell(new Paragraph(StringHelper.AgregaComas(StringHelper.roundDouble(registro.get("subtotal"), 2)), smallFont));
                        cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                        cell.setBorder(0);
                        tabla.addCell(cell);

                        cell = new PdfPCell(new Paragraph(StringHelper.AgregaComas(StringHelper.roundDouble(registro.get("comision_por_fecha"), 2) + "%"), smallFont));
                        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                        cell.setBorder(0);
                        tabla.addCell(cell);

                        cell = new PdfPCell(new Paragraph(simbolo_moneda, smallFont));
                        cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                        cell.setBorder(0);
                        tabla.addCell(cell);

                        cell = new PdfPCell(new Paragraph(StringHelper.AgregaComas(StringHelper.roundDouble(registro.get("total_comision_por_fecha"), 2)), smallFont));
                        //(StringHelper.AgregaComas(StringHelper.roundDouble(sumatotalvalor,2)),smallFont));
                        cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                        cell.setBorder(0);
                        tabla.addCell(cell);

                        sumatotalvalor = sumatotalvalor + Double.parseDouble(registro.get("subtotal").toString());
                        sumatotalcomision = sumatotalcomision + Double.parseDouble(registro.get("total_comision_por_fecha").toString());

                        nombre_agente = registro.get("nombre_agente").toString();
                        denominacion = registro.get("moneda_factura").toString();
                    }

                }

                cell = new PdfPCell(new Paragraph("", smallFont));
                cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                cell.setBorder(0);
                tabla.addCell(cell);

                cell = new PdfPCell(new Paragraph("", smallFont));
                cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                cell.setBorder(0);
                tabla.addCell(cell);

                cell = new PdfPCell(new Paragraph("", smallFont));
                cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                cell.setBorder(0);
                tabla.addCell(cell);


                cell = new PdfPCell(new Paragraph("TOTAL :", smallFont));
                cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                cell.setBorder(0);
                cell.setColspan(2);
                tabla.addCell(cell);

                cell = new PdfPCell(new Paragraph(simbolo_moneda, smallFont));
                cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                cell.setBorder(1);
                tabla.addCell(cell);

                cell = new PdfPCell(new Paragraph(StringHelper.AgregaComas(StringHelper.roundDouble(sumatotalvalor, 2)), smallFont));
                cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                cell.setBorder(1);
                tabla.addCell(cell);

                cell = new PdfPCell(new Paragraph("", smallFont));
                cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                cell.setBorder(1);
                tabla.addCell(cell);

                cell = new PdfPCell(new Paragraph(simbolo_moneda, smallFont));
                cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                cell.setBorder(1);
                tabla.addCell(cell);

                cell = new PdfPCell(new Paragraph(StringHelper.AgregaComas(StringHelper.roundDouble(sumatotalcomision, 2)), smallFont));
                cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                cell.setBorder(1);
                tabla.addCell(cell);

                doc.add(tabla);
            }
            if (tipo_rep == 2) {

                ////////////////////////////////////////////////////////////////////////////////////////////////////////////
                System.out.println("::::::::::::::Imprimiendo el reporte de ventas por agente::::::::::::::::::");
                float[] widths = {
                    2f,
                    1.5f,
                    5.5f,
                    1.3f,
                    2.3f,
                    1.5f,
                    0.7f,
                    2.5f};//Tamaño de las Columnas.
                PdfPTable tabla = new PdfPTable(widths);
                PdfPCell cell;
                tabla.setKeepTogether(false);
                tabla.setHeaderRows(1);

                cell = new PdfPCell(new Paragraph("N°.FACTURA", smallBoldFont));
                cell.setUseAscender(true);
                cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                cell.setUseDescender(true);
                cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                cell.setBackgroundColor(BaseColor.BLACK);
                tabla.addCell(cell);

                cell = new PdfPCell(new Paragraph("FECHA", smallBoldFont));
                cell.setUseAscender(true);
                cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                cell.setUseDescender(true);
                cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                cell.setBackgroundColor(BaseColor.BLACK);
                tabla.addCell(cell);

                cell = new PdfPCell(new Paragraph("CLIENTE", smallBoldFont));
                cell.setUseAscender(true);
                cell.setHorizontalAlignment(Element.ALIGN_LEFT);
                cell.setUseDescender(true);
                cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                cell.setBackgroundColor(BaseColor.BLACK);
                tabla.addCell(cell);


                cell = new PdfPCell(new Paragraph("IMPORTE", smallBoldFont));
                cell.setUseAscender(true);
                cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                cell.setUseDescender(true);
                cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                cell.setBackgroundColor(BaseColor.BLACK);
                cell.setColspan(2);
                tabla.addCell(cell);

                cell = new PdfPCell(new Paragraph("IVA", smallBoldFont));
                cell.setUseAscender(true);
                cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                cell.setUseDescender(true);
                cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                cell.setBackgroundColor(BaseColor.BLACK);
                tabla.addCell(cell);

                cell = new PdfPCell(new Paragraph("TOTAL", smallBoldFont));
                cell.setUseAscender(true);
                cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                cell.setUseDescender(true);
                cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                cell.setBackgroundColor(BaseColor.BLACK);
                cell.setColspan(2);
                tabla.addCell(cell);

                for (int x = 0; x <= (cobranza_venta.size()) - 1; x++) {
                    HashMap<String, String> registro = cobranza_venta.get(x);

                    if (primer_registro == 0) {
                        nombre_agente = registro.get("nombre_agente").toString();
                        denominacion = registro.get("moneda_factura").toString();

                        if (registro.get("moneda_factura").equals("M.N.")) {
                            simbolo_moneda = "$";
                        }
                        if (registro.get("moneda_factura").equals("USD")) {
                            simbolo_moneda = "USD";
                        }
                        primer_registro = 1;
                    }

                    if (nombre_agente.equals(registro.get("nombre_agente")) && denominacion.equals(registro.get("moneda_factura"))) {
                        if (tmp == 0) {

                            cell = new PdfPCell(new Paragraph((String) registro.get("nombre_agente"), smallFontN));
                            cell.setHorizontalAlignment(Element.ALIGN_LEFT);
                            cell.setBorder(0);
                            cell.setColspan(10);
                            tabla.addCell(cell);

                            cell = new PdfPCell(new Paragraph((String) registro.get("serie_folio"), smallFont));
                            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                            cell.setBorder(0);
                            tabla.addCell(cell);

                            cell = new PdfPCell(new Paragraph((String) registro.get("fecha_factura"), smallFont));
                            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                            cell.setBorder(0);
                            tabla.addCell(cell);

                            cell = new PdfPCell(new Paragraph((String) registro.get("cliente"), smallFont));
                            cell.setHorizontalAlignment(Element.ALIGN_LEFT);
                            cell.setBorder(0);
                            tabla.addCell(cell);

                            cell = new PdfPCell(new Paragraph(simbolo_moneda, smallFont));
                            cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                            cell.setBorder(0);
                            tabla.addCell(cell);

                            cell = new PdfPCell(new Paragraph(StringHelper.AgregaComas(StringHelper.roundDouble(registro.get("importe"), 2)), smallFont));
                            cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                            cell.setBorder(0);
                            tabla.addCell(cell);

                            cell = new PdfPCell(new Paragraph(StringHelper.AgregaComas(StringHelper.roundDouble(registro.get("iva"), 2)), smallFont));
                            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                            cell.setBorder(0);
                            tabla.addCell(cell);

                            cell = new PdfPCell(new Paragraph(simbolo_moneda, smallFont));
                            cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                            cell.setBorder(0);
                            tabla.addCell(cell);

                            cell = new PdfPCell(new Paragraph(StringHelper.AgregaComas(StringHelper.roundDouble(registro.get("total"), 2)), smallFont));
                            cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                            cell.setBorder(0);
                            tabla.addCell(cell);
                        }

                        if (tmp != 0) {

                            cell = new PdfPCell(new Paragraph((String) registro.get("serie_folio"), smallFont));
                            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                            cell.setBorder(0);
                            tabla.addCell(cell);

                            cell = new PdfPCell(new Paragraph((String) registro.get("fecha_factura"), smallFont));
                            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                            cell.setBorder(0);
                            tabla.addCell(cell);

                            cell = new PdfPCell(new Paragraph((String) registro.get("cliente"), smallFont));
                            cell.setHorizontalAlignment(Element.ALIGN_LEFT);
                            cell.setBorder(0);
                            tabla.addCell(cell);


                            cell = new PdfPCell(new Paragraph(simbolo_moneda, smallFont));
                            cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                            cell.setBorder(0);
                            tabla.addCell(cell);

                            cell = new PdfPCell(new Paragraph(StringHelper.AgregaComas(StringHelper.roundDouble(registro.get("importe"), 2)), smallFont));
                            cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                            cell.setBorder(0);
                            tabla.addCell(cell);

                            cell = new PdfPCell(new Paragraph(StringHelper.AgregaComas(StringHelper.roundDouble(registro.get("iva"), 2)), smallFont));
                            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                            cell.setBorder(0);
                            tabla.addCell(cell);

                            cell = new PdfPCell(new Paragraph(simbolo_moneda, smallFont));
                            cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                            cell.setBorder(0);
                            tabla.addCell(cell);

                            cell = new PdfPCell(new Paragraph(StringHelper.AgregaComas(StringHelper.roundDouble(registro.get("total"), 2)), smallFont));
                            cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                            cell.setBorder(0);
                            tabla.addCell(cell);

                        }

                        sumatotalvalor = sumatotalvalor + Double.parseDouble(registro.get("importe").toString());
                        sumatotalcomision = sumatotalcomision + Double.parseDouble(registro.get("total").toString());

                        tmp = 1;

                    } else {

                        cell = new PdfPCell(new Paragraph("", smallFont));
                        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                        cell.setBorder(0);
                        tabla.addCell(cell);

                        cell = new PdfPCell(new Paragraph("", smallFont));
                        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                        cell.setBorder(0);
                        tabla.addCell(cell);

                        cell = new PdfPCell(new Paragraph("", smallFont));
                        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                        cell.setBorder(0);
                        tabla.addCell(cell);

                        cell = new PdfPCell(new Paragraph("TOTAL : ", smallFont));
                        cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                        cell.setBorder(0);
                        tabla.addCell(cell);

                        cell = new PdfPCell(new Paragraph(StringHelper.AgregaComas(StringHelper.roundDouble(sumatotalvalor, 2)), smallFont));
                        cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                        cell.setBorder(1);
                        tabla.addCell(cell);

                        cell = new PdfPCell(new Paragraph("", smallFont));
                        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                        cell.setBorder(1);
                        tabla.addCell(cell);

                        cell = new PdfPCell(new Paragraph(simbolo_moneda, smallFont));
                        cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                        cell.setBorder(1);
                        tabla.addCell(cell);

                        cell = new PdfPCell(new Paragraph(StringHelper.AgregaComas(StringHelper.roundDouble(sumatotalcomision, 2)), smallFont));
                        cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                        cell.setBorder(1);
                        tabla.addCell(cell);

                        //fila vacia
                        cell = new PdfPCell(new Paragraph("", smallFont));
                        cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                        cell.setBorder(0);
                        cell.setColspan(11);

                        tabla.addCell(cell);


                        //inicializando variable
                        sumatotalvalor = 0.0;
                        sumatotalcomision = 0.0;
                        //fin del inicializado de varables


                        if (registro.get("moneda_factura").equals("M.N.")) {
                            simbolo_moneda = "$";
                        }
                        if (registro.get("moneda_factura").equals("USD")) {
                            simbolo_moneda = "USD";
                        }

                        cell = new PdfPCell(new Paragraph((String) registro.get("nombre_agente"), smallFontN));
                        cell.setHorizontalAlignment(Element.ALIGN_LEFT);
                        cell.setBorder(0);
                        cell.setColspan(10);
                        tabla.addCell(cell);

                        cell = new PdfPCell(new Paragraph((String) registro.get("serie_folio"), smallFont));
                        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                        cell.setBorder(0);
                        tabla.addCell(cell);

                        cell = new PdfPCell(new Paragraph((String) registro.get("fecha_factura"), smallFont));
                        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                        cell.setBorder(0);
                        tabla.addCell(cell);

                        cell = new PdfPCell(new Paragraph((String) registro.get("cliente"), smallFont));
                        cell.setHorizontalAlignment(Element.ALIGN_LEFT);
                        cell.setBorder(0);
                        tabla.addCell(cell);

                        cell = new PdfPCell(new Paragraph(simbolo_moneda, smallFont));
                        cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                        cell.setBorder(0);
                        tabla.addCell(cell);

                        cell = new PdfPCell(new Paragraph(StringHelper.AgregaComas(StringHelper.roundDouble(registro.get("importe"), 2)), smallFont));
                        cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                        cell.setBorder(0);
                        tabla.addCell(cell);

                        cell = new PdfPCell(new Paragraph(StringHelper.AgregaComas(StringHelper.roundDouble(registro.get("iva"), 2)), smallFont));
                        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                        cell.setBorder(0);
                        tabla.addCell(cell);

                        cell = new PdfPCell(new Paragraph(simbolo_moneda, smallFont));
                        cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                        cell.setBorder(0);
                        tabla.addCell(cell);

                        cell = new PdfPCell(new Paragraph(StringHelper.AgregaComas(StringHelper.roundDouble(registro.get("total"), 2)), smallFont));
                        cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                        cell.setBorder(0);
                        tabla.addCell(cell);

                        sumatotalvalor = sumatotalvalor + Double.parseDouble(registro.get("importe").toString());
                        sumatotalcomision = sumatotalcomision + Double.parseDouble(registro.get("total").toString());

                        nombre_agente = registro.get("nombre_agente").toString();
                        denominacion = registro.get("moneda_factura").toString();
                    }

                }

                cell = new PdfPCell(new Paragraph("", smallFont));
                cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                cell.setBorder(0);
                tabla.addCell(cell);

                cell = new PdfPCell(new Paragraph("", smallFont));
                cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                cell.setBorder(0);
                tabla.addCell(cell);

                cell = new PdfPCell(new Paragraph("", smallFont));
                cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                cell.setBorder(0);
                tabla.addCell(cell);


                cell = new PdfPCell(new Paragraph("TOTAL :", smallFont));
                cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                cell.setBorder(0);
                tabla.addCell(cell);

                cell = new PdfPCell(new Paragraph(StringHelper.AgregaComas(StringHelper.roundDouble(sumatotalvalor, 2)), smallFont));
                cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                cell.setBorder(1);
                tabla.addCell(cell);

                cell = new PdfPCell(new Paragraph("", smallFont));
                cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                cell.setBorder(1);
                tabla.addCell(cell);

                cell = new PdfPCell(new Paragraph(simbolo_moneda, smallFont));
                cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                cell.setBorder(1);
                tabla.addCell(cell);

                cell = new PdfPCell(new Paragraph(StringHelper.AgregaComas(StringHelper.roundDouble(sumatotalcomision, 2)), smallFont));
                cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                cell.setBorder(1);
                tabla.addCell(cell);

                doc.add(tabla);
            }

            if (tipo_rep == 1 && tipo_comision == 2) {
                System.out.println("IMprimiendo el reporte de comison por MOnto");
                //String[] columnas = {"No.Control","Nombre","No.Factura","Cliente","","Valor","% Comision","","Total Comision"};
                float[] widths = {1.5f, 2f, 5f, 1.5f, 2f, 1.5f, 2f};//Tamaño de las Columnas.
                PdfPTable tabla = new PdfPTable(widths);
                PdfPCell cell;
                tabla.setKeepTogether(false);
                tabla.setHeaderRows(1);

                cell = new PdfPCell(new Paragraph("No. FACTURA", smallBoldFont));
                cell.setUseAscender(true);
                cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                cell.setUseDescender(true);
                cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                cell.setBackgroundColor(BaseColor.BLACK);
                tabla.addCell(cell);

                cell = new PdfPCell(new Paragraph("FECHA FACTURA", smallBoldFont));
                cell.setUseAscender(true);
                cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                cell.setUseDescender(true);
                cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                cell.setBackgroundColor(BaseColor.BLACK);
                tabla.addCell(cell);

                cell = new PdfPCell(new Paragraph("CLIENTE", smallBoldFont));
                cell.setUseAscender(true);
                cell.setHorizontalAlignment(Element.ALIGN_LEFT);
                cell.setUseDescender(true);
                cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                cell.setBackgroundColor(BaseColor.BLACK);
                tabla.addCell(cell);



                cell = new PdfPCell(new Paragraph("SUBTOTAL", smallBoldFont));
                cell.setUseAscender(true);
                cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                cell.setUseDescender(true);
                cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                cell.setBackgroundColor(BaseColor.BLACK);
                cell.setColspan(2);
                tabla.addCell(cell);



                cell = new PdfPCell(new Paragraph("COMISION POR MONTO", smallBoldFont));
                cell.setUseAscender(true);
                cell.setColspan(2);
                cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                cell.setUseDescender(true);
                cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                cell.setBackgroundColor(BaseColor.BLACK);
                tabla.addCell(cell);



                nombre_agente = cobranza_venta.get(0).get("nombre_agente").toString();
                denominacion = cobranza_venta.get(0).get("moneda_factura").toString();
                if (cobranza_venta.get(0).get("moneda_factura").equals("M.N.")) {
                    simbolo_moneda = "$";
                }

                if (cobranza_venta.get(0).get("moneda_factura").equals("USD")) {
                    simbolo_moneda = "USD";
                }

                cell = new PdfPCell(new Paragraph((String) nombre_agente, smallFontN));
                cell.setHorizontalAlignment(Element.ALIGN_LEFT);
                // cell.setBorder(0);
                cell.setColspan(7);
                tabla.addCell(cell);

                for (int x = 0; x <= (cobranza_venta.size()) - 1; x++) {
                    HashMap<String, String> registro = cobranza_venta.get(x);

                    if (nombre_agente.equals(registro.get("nombre_agente")) && denominacion.equals(registro.get("moneda_factura"))) {

                        cell = new PdfPCell(new Paragraph((String) registro.get("serie_folio"), smallFont));
                        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                        // cell.setBorder(0);
                        tabla.addCell(cell);

                        cell = new PdfPCell(new Paragraph((String) registro.get("fecha_factura"), smallFont));
                        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                        //cell.setBorder(0);
                        tabla.addCell(cell);

                        cell = new PdfPCell(new Paragraph((String) registro.get("cliente"), smallFont));
                        cell.setHorizontalAlignment(Element.ALIGN_LEFT);
                        //cell.setBorder(0);
                        tabla.addCell(cell);



                        cell = new PdfPCell(new Paragraph(simbolo_moneda, smallFont));
                        cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                        //cell.setBorder(0);
                        tabla.addCell(cell);

                        cell = new PdfPCell(new Paragraph(StringHelper.AgregaComas(StringHelper.roundDouble(registro.get("subtotal"), 2)), smallFont));
                        cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                        //cell.setBorder(0);
                        tabla.addCell(cell);

                        cell = new PdfPCell(new Paragraph(simbolo_moneda, smallFont));
                        cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                        //cell.setBorder(0);
                        tabla.addCell(cell);

                        cell = new PdfPCell(new Paragraph(StringHelper.AgregaComas(StringHelper.roundDouble(registro.get("comision_por_monto"), 2)), smallFont));
                        cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                        //cell.setBorder(0);
                        tabla.addCell(cell);
                        sumatotalsubtotal = sumatotalsubtotal + Double.parseDouble(registro.get("subtotal").toString());
                        sumatotalcomision = sumatotalcomision + Double.parseDouble(registro.get("comision_por_monto").toString());


                    } else {

                        cell = new PdfPCell(new Paragraph("TOTAL : ", smallFont));
                        cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                        // cell.setBorder(0);
                        cell.setColspan(3);
                        tabla.addCell(cell);

                        cell = new PdfPCell(new Paragraph(simbolo_moneda, smallFont));
                        cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                        //cell.setBorder(1);
                        tabla.addCell(cell);

                        cell = new PdfPCell(new Paragraph(StringHelper.AgregaComas(StringHelper.roundDouble(sumatotalsubtotal, 2)), smallFont));
                        cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                        //cell.setBorder(1);
                        tabla.addCell(cell);


                        cell = new PdfPCell(new Paragraph(simbolo_moneda, smallFont));
                        cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                        // cell.setBorder(1);
                        tabla.addCell(cell);

                        cell = new PdfPCell(new Paragraph(StringHelper.AgregaComas(StringHelper.roundDouble(sumatotalcomision, 2)), smallFont));
                        cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                        cell.setBorder(1);
                        tabla.addCell(cell);

                        //fila vacia
                        cell = new PdfPCell(new Paragraph("", smallFont));
                        cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                        //cell.setBorder(0);
                        cell.setColspan(7);
                        tabla.addCell(cell);


                        //inicializando variable
                        sumatotalsubtotal = 0.0;
                        sumatotalcomision = 0.0;
                        //fin del inicializado de varables


                        if (registro.get("moneda_factura").equals("M.N.")) {
                            simbolo_moneda = "$";
                        }
                        if (registro.get("moneda_factura").equals("USD")) {
                            simbolo_moneda = "USD";
                        }

                        cell = new PdfPCell(new Paragraph((String) registro.get("nombre_agente"), smallFontN));
                        cell.setHorizontalAlignment(Element.ALIGN_LEFT);
                        //cell.setBorder(0);
                        cell.setColspan(7);
                        tabla.addCell(cell);



                        cell = new PdfPCell(new Paragraph((String) registro.get("serie_folio"), smallFont));
                        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                        //cell.setBorder(0);
                        tabla.addCell(cell);

                        cell = new PdfPCell(new Paragraph((String) registro.get("fecha_factura"), smallFont));
                        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                        //cell.setBorder(0);
                        tabla.addCell(cell);

                        cell = new PdfPCell(new Paragraph((String) registro.get("cliente"), smallFont));
                        cell.setHorizontalAlignment(Element.ALIGN_LEFT);
                        //cell.setBorder(0);
                        tabla.addCell(cell);



                        cell = new PdfPCell(new Paragraph(simbolo_moneda, smallFont));
                        cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                        //cell.setBorder(0);
                        tabla.addCell(cell);

                        cell = new PdfPCell(new Paragraph(StringHelper.AgregaComas(StringHelper.roundDouble(registro.get("subtotal"), 2)), smallFont));
                        cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                        //cell.setBorder(0);
                        tabla.addCell(cell);

                        cell = new PdfPCell(new Paragraph(simbolo_moneda, smallFont));
                        cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                        //cell.setBorder(0);
                        tabla.addCell(cell);

                        cell = new PdfPCell(new Paragraph(StringHelper.AgregaComas(StringHelper.roundDouble(registro.get("comision_por_monto"), 2)), smallFont));
                        cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                        //cell.setBorder(0);
                        tabla.addCell(cell);



                        sumatotalsubtotal = sumatotalsubtotal + Double.parseDouble(registro.get("subtotal").toString());
                        sumatotalcomision = sumatotalcomision + Double.parseDouble(registro.get("comision_por_monto").toString());

                        nombre_agente = registro.get("nombre_agente").toString();
                        denominacion = registro.get("moneda_factura").toString();
                    }

                }


                cell = new PdfPCell(new Paragraph("TOTAL :", smallFont));
                cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                //cell.setBorder(0);
                cell.setColspan(3);
                tabla.addCell(cell);

                cell = new PdfPCell(new Paragraph(simbolo_moneda, smallFont));
                cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                //cell.setBorder(1);
                tabla.addCell(cell);

                cell = new PdfPCell(new Paragraph(StringHelper.AgregaComas(StringHelper.roundDouble(sumatotalsubtotal, 2)), smallFont));
                cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                //cell.setBorder(1);
                tabla.addCell(cell);

                cell = new PdfPCell(new Paragraph(simbolo_moneda, smallFont));
                cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                //cell.setBorder(1);
                tabla.addCell(cell);


                cell = new PdfPCell(new Paragraph(StringHelper.AgregaComas(StringHelper.roundDouble(sumatotalcomision, 2)), smallFont));
                cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                // cell.setBorder(1);
                tabla.addCell(cell);

                doc.add(tabla);

            }

            doc.close();



        } catch (FileNotFoundException ex) {
            Logger.getLogger(com.agnux.kemikal.reportes.PdfReporteCobrazaAgente.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    static class HeaderFooter extends PdfPageEventHelper {

        protected PdfTemplate total;
        protected BaseFont helv;
        protected PdfContentByte cb;
        Font largeBoldFont = new Font(Font.FontFamily.HELVETICA, 10, Font.BOLD, BaseColor.BLACK);
        Font largeFont = new Font(Font.FontFamily.HELVETICA, 10, Font.NORMAL, BaseColor.BLACK);
        Font smallFont = new Font(Font.FontFamily.HELVETICA, 7, Font.NORMAL, BaseColor.BLACK);
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
        HeaderFooter(HashMap<String, String> datos) {
            this.setEmpresa(datos.get("empresa"));
            this.setTitulo_reporte(datos.get("titulo_reporte"));
            this.setPeriodo(datos.get("periodo"));
            this.setCodigo1(datos.get("codigo1"));
            this.setCodigo2(datos.get("codigo2"));
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
            } catch (Exception e) {
                throw new ExceptionConverter(e);
            }
        }

        /*añadimos pie de página, borde y más propiedades*/
        @Override
        public void onEndPage(PdfWriter writer, Document document) {

            ColumnText.showTextAligned(writer.getDirectContent(), Element.ALIGN_CENTER, new Phrase(this.getEmpresa(), largeBoldFont), document.getPageSize().getWidth() / 2, document.getPageSize().getTop() - 25, 0);
            ColumnText.showTextAligned(writer.getDirectContent(), Element.ALIGN_CENTER, new Phrase(this.getTitulo_reporte(), largeBoldFont), document.getPageSize().getWidth() / 2, document.getPageSize().getTop() - 38, 0);
            ColumnText.showTextAligned(writer.getDirectContent(), Element.ALIGN_CENTER, new Phrase(this.getPeriodo(), largeFont), document.getPageSize().getWidth() / 2, document.getPageSize().getTop() - 50, 0);


            cb = writer.getDirectContent();
            float textBase = document.bottom() - 20;

            //texto inferior izquieda pie de pagina
            String text_left = this.getCodigo1();
            float text_left_Size = helv.getWidthPoint(text_left, 7);
            cb.beginText();
            cb.setFontAndSize(helv, 7);
            cb.setTextMatrix(document.left() + 85, textBase);  //definir la posicion de text
            cb.showText(text_left);
            cb.endText();


            //texto centro pie de pagina
            String text_center = "Página " + writer.getPageNumber() + " de ";
            float text_center_Size = helv.getWidthPoint(text_center, 7);
            float pos_text_center = (document.getPageSize().getWidth() / 2) - (text_center_Size / 2);
            float adjust = text_center_Size + 3;
            cb.beginText();
            cb.setFontAndSize(helv, 7);
            cb.setTextMatrix(pos_text_center, textBase);  //definir la posicion de text
            cb.showText(text_center);
            cb.endText();
            cb.addTemplate(total, pos_text_center + adjust, textBase);


            //texto inferior derecha pie de pagina
            String text_right = this.getCodigo2();
            float textRightSize = helv.getWidthPoint(text_right, 7);
            float pos_text_right = document.getPageSize().getWidth() - textRightSize - 40;
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
            total.setTextMatrix(0, 0);
            total.showText(String.valueOf(writer.getPageNumber() - 1));
            total.endText();
        }
    }//termina clase HeaderFooter
}
