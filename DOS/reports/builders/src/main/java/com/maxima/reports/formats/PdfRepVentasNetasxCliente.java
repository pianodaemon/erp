package com.maxima.reports.formats;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

public class PdfRepVentasNetasxCliente {

    public String empresa_emisora;
    public static String fecha_reporte;
    public java.util.List<HashMap<String, String>> rows;

    public static String AgregaComas(String num) {
        String cadena_retorno = "";
        String tamano = "";
        String cantidad = num;
        String primer_digito = num.substring(0, 1);//extrae el primer digito de la cantidad
        if (primer_digito.equals(".")) {//si es igual a un punto le agrega un cero
            cantidad = "0" + num;
        }

        String[] valor = cantidad.split("\\.");

        String[] atratar = new StringBuffer(valor[0]).reverse().toString().split("");

        for (String atratar1 : atratar) {
            cadena_retorno += atratar1;
            tamano += atratar1;
            if (tamano.length() % 3 == 0) {
                cadena_retorno += ",";
            }
        }
        if ((cadena_retorno.substring(cadena_retorno.length() - 1, cadena_retorno.length())).equals(",")) {
            cadena_retorno = cadena_retorno.substring(0, cadena_retorno.length() - 1);
        }
        cadena_retorno = new StringBuffer(cadena_retorno).reverse().toString();
        cadena_retorno = cadena_retorno.substring(0, cadena_retorno.length() - 1);
        if (valor.length == 2) {
            cadena_retorno += "." + valor[1];
        }
        return cadena_retorno;
    }

    public static String roundDouble(double numero, int ceros_a_la_derecha) {
        String mascara = "#.";
        String formateado = "";
        String num = "";

        for (int i = 0; i < ceros_a_la_derecha; i++) {
            mascara += "0";
        }
        NumberFormat formatter = new DecimalFormat(mascara);
        num = "" + formatter.format(numero);

        if (num.substring(0, 1).equals(".")) {
            formateado = "0" + num;
        } else {
            formateado = "" + num;
        }

        if (ceros_a_la_derecha == 0) {
            formateado = formateado.substring(0, formateado.length() - 1);
        }

        return formateado;
    }

    public static String roundDouble(String valor, int ceros_a_la_derecha) {
        double numero = Double.parseDouble(valor);
        String mascara = "#.";
        String formateado = "";
        String num = "";

        for (int i = 0; i < ceros_a_la_derecha; i++) {
            mascara += "0";
        }
        NumberFormat formatter = new DecimalFormat(mascara);
        num = "" + formatter.format(numero);

        if (num.substring(0, 1).equals(".")) {
            formateado = "0" + num;
        } else {
            formateado = "" + num;
        }

        if (ceros_a_la_derecha == 0) {
            formateado = formateado.substring(0, formateado.length() - 1);
        }

        return formateado;
    }

    public static String getFecha_reporte() {
        return fecha_reporte;
    }

    public static void setFecha_reporte(String fecha_reporte) {
        PdfRepVentasNetasxCliente.fecha_reporte = fecha_reporte;
    }

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

    public PdfRepVentasNetasxCliente(
            ArrayList<HashMap<String, String>> lista_ventas,
            String fecha_inicial, String fecha_final,
            String razon_social_empresa, String fileout) {

        this.setEmpresa_emisora(razon_social_empresa);
        PdfRepVentasNetasxCliente.HeaderFooter event = new PdfRepVentasNetasxCliente.HeaderFooter();

        Font fontCols = new Font(Font.FontFamily.HELVETICA, 9, Font.NORMAL);
        Font smallFont = new Font(Font.FontFamily.HELVETICA, 8, Font.NORMAL, BaseColor.BLACK);
        Font smallBoldFont = new Font(Font.getFamily("ARIAL"), 8, Font.BOLD, BaseColor.WHITE);
        Font smallFontN = new Font(Font.FontFamily.HELVETICA, 8, Font.BOLD, BaseColor.BLACK);
        PdfPTable table_titulos;
        PdfPTable table_iteraciones = null;

        PdfPCell cell;
        try {

            Document document = new Document(PageSize.LETTER, -50, -50, 60, 30);
            PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(fileout));
            writer.setPageEvent(event);
            document.open();
            //TABLA DE FECHAS 

            this.setRows(lista_ventas);

            String[] fi = fecha_inicial.split("-");
            String[] ff = fecha_final.split("-");

            this.setFecha_reporte("DEl      " + fi[2] + "/" + fi[1] + "/" + fi[0] + "      Al:      " + ff[2] + "/" + ff[1] + "/" + ff[0]);
            float[] pronosticos = {0.5f, 2f, 0.2f, 1f, 1f};
            table_iteraciones = new PdfPTable(pronosticos);
            table_iteraciones.setKeepTogether(false);
            table_iteraciones.setKeepTogether(true);

            String[] titulos = {"No.Control", "Cliente", "", "Venta", "%"};

            for (int i = 0; i <= titulos.length - 1; i++) {

                cell = new PdfPCell(new Paragraph(titulos[i], smallBoldFont));

                if ("No.Control".equals(titulos[i])) {
                    cell.setHorizontalAlignment(Element.ALIGN_LEFT);
                }

                if ("Cliente".equals(titulos[i])) {
                    cell.setHorizontalAlignment(Element.ALIGN_LEFT);
                }

                if ("".equals(titulos[i])) {
                    cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                }

                if ("Venta".equals(titulos[i])) {
                    cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                }

                if ("%".equals(titulos[i])) {
                    cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                }

                cell.setBorder(0);
                cell.setUseAscender(true);
                cell.setUseDescender(true);
                cell.setBackgroundColor(BaseColor.BLACK);
                cell.setBorder(0);
                cell.setBorderWidthLeft(0);
                table_iteraciones.addCell(cell);
            }

            String cliente = "";
            double Sumatoriaventa_neta = 0.0;
            double TPventa_neta = 0.0;
            double porciento = 0.0;
            double sumatotoriaporciento = 0.0;

            for (HashMap<String, String> i : this.getRows()) {
                //Indices del HashMap que representa el row
                String[] wordList = {"numero_control", "cliente", "pesos", "Tventa_neta", "porcentaje"};
                java.util.List<String> indices1 = (java.util.List<String>) Arrays.asList(wordList);
                for (String omega : indices1) {
                    PdfPCell celda = null;

                    if (omega.equals("numero_control")) {
                        celda = new PdfPCell(new Paragraph(i.get(omega), smallFont));
                        celda.setHorizontalAlignment(Element.ALIGN_LEFT);
                        celda.setVerticalAlignment(Element.ALIGN_MIDDLE);
                    }

                    if (omega.equals("cliente")) {

                        celda = new PdfPCell(new Paragraph(i.get(omega), smallFont));
                        celda.setHorizontalAlignment(Element.ALIGN_RIGHT);
                        celda.setVerticalAlignment(Element.ALIGN_MIDDLE);

                    }

                    if (omega.equals("pesos")) {
                        celda = new PdfPCell(new Paragraph(i.get(omega), smallFont));
                        celda.setHorizontalAlignment(Element.ALIGN_RIGHT);
                        celda.setVerticalAlignment(Element.ALIGN_MIDDLE);
                    }
                    if (omega.equals("Tventa_neta")) {
                        celda = new PdfPCell(new Paragraph(PdfRepVentasNetasxCliente.roundDouble(i.get(omega), 2), smallFont));
                        celda.setHorizontalAlignment(Element.ALIGN_RIGHT);
                        celda.setVerticalAlignment(Element.ALIGN_MIDDLE);

                        Sumatoriaventa_neta = Sumatoriaventa_neta + Double.valueOf(i.get(omega)).doubleValue();
                    }

                    if (omega.equals("porcentaje")) {
                        celda = new PdfPCell(new Paragraph("%", smallFont));
                        celda.setHorizontalAlignment(Element.ALIGN_RIGHT);
                        celda.setVerticalAlignment(Element.ALIGN_MIDDLE);
                    }
                }
            }

            for (HashMap<String, String> i : this.getRows()) {

                //Indices del HashMap que representa el row
                String[] wordList = {"numero_control", "cliente", "pesos", "Tventa_neta", "porcentaje"};
                java.util.List<String> indices1 = (java.util.List<String>) Arrays.asList(wordList);

                for (String omega : indices1) {
                    PdfPCell celda = null;

                    if (omega.equals("numero_control")) {
                        celda = new PdfPCell(new Paragraph(i.get(omega), smallFont));
                        celda.setHorizontalAlignment(Element.ALIGN_LEFT);
                        celda.setVerticalAlignment(Element.ALIGN_MIDDLE);
                    }

                    if (omega.equals("cliente")) {

                        celda = new PdfPCell(new Paragraph(i.get(omega), smallFont));
                        celda.setHorizontalAlignment(Element.ALIGN_LEFT);
                        celda.setVerticalAlignment(Element.ALIGN_MIDDLE);
                    }

                    if (omega.equals("pesos")) {
                        celda = new PdfPCell(new Paragraph(i.get(omega), smallFont));
                        celda.setHorizontalAlignment(Element.ALIGN_RIGHT);
                        celda.setVerticalAlignment(Element.ALIGN_MIDDLE);
                    }

                    if (omega.equals("Tventa_neta")) {
                        celda = new PdfPCell(new Paragraph(PdfRepVentasNetasxCliente.AgregaComas(PdfRepVentasNetasxCliente.roundDouble(i.get(omega), 2)), smallFont));
                        celda.setHorizontalAlignment(Element.ALIGN_RIGHT);
                        celda.setVerticalAlignment(Element.ALIGN_MIDDLE);

                        TPventa_neta = Double.valueOf(i.get(omega)).doubleValue();
                        porciento = ((TPventa_neta / Sumatoriaventa_neta) * 100);
                        sumatotoriaporciento = sumatotoriaporciento + ((TPventa_neta / Sumatoriaventa_neta) * 100);
                    }

                    if (omega.equals("porcentaje")) {
                        celda = new PdfPCell(new Paragraph(PdfRepVentasNetasxCliente.AgregaComas(PdfRepVentasNetasxCliente.roundDouble(porciento, 2) + " %"), smallFont));
                        celda.setHorizontalAlignment(Element.ALIGN_RIGHT);
                        celda.setVerticalAlignment(Element.ALIGN_MIDDLE);
                    }

                    table_iteraciones.addCell(celda);
                }
            }

            //FILA DE TOTAL GENERAL (SUMA DE TODOS LAS VENTAS NETAS ABAJO!)
            cell = new PdfPCell(new Paragraph("TOTAL GENERAL:", smallFont));
            cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            cell.setBorder(0);
            //cell.setBorder(11);
            cell.setColspan(2);
            table_iteraciones.addCell(cell);

            cell = new PdfPCell(new Paragraph("$", smallFont));
            cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            cell.setBorder(0);

            table_iteraciones.addCell(cell);

            cell = new PdfPCell(new Paragraph(PdfRepVentasNetasxCliente.AgregaComas(PdfRepVentasNetasxCliente.roundDouble(Sumatoriaventa_neta, 2)), smallFont));
            cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            cell.setBorder(0);

            table_iteraciones.addCell(cell);

            cell = new PdfPCell(new Paragraph(PdfRepVentasNetasxCliente.AgregaComas(PdfRepVentasNetasxCliente.roundDouble(sumatotoriaporciento, 2) + " %"), smallFont));
            cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            cell.setBorder(0);

            table_iteraciones.addCell(cell);
            document.add(table_iteraciones); //añadiendo la tabla 
            document.close();
        } catch (DocumentException | FileNotFoundException | NumberFormatException e) {
            e.printStackTrace();
        }
    }

    class HeaderFooter extends PdfPageEventHelper {

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

            ColumnText.showTextAligned(writer.getDirectContent(), Element.ALIGN_CENTER, new Phrase(PdfRepVentasNetasxCliente.this.getEmpresa_emisora(), largeBoldFont), document.getPageSize().getWidth() / 2, document.getPageSize().getTop() - 25, 0);
            ColumnText.showTextAligned(writer.getDirectContent(), Element.ALIGN_CENTER, new Phrase("Ventas Netas por Cliente", largeBoldFont), document.getPageSize().getWidth() / 2, document.getPageSize().getTop() - 38, 0);
            ColumnText.showTextAligned(writer.getDirectContent(), Element.ALIGN_CENTER, new Phrase(PdfRepVentasNetasxCliente.fecha_reporte, largeFont), document.getPageSize().getWidth() / 2, document.getPageSize().getTop() - 50, 0);

            cb = writer.getDirectContent();

            String text = "Página " + writer.getPageNumber() + " de ";
            float textBase = document.bottom() - 20;
            float textSize = helv.getWidthPoint(text, 7);
            float adjust = helv.getWidthPoint("0", 150);
            cb.beginText();
            cb.setFontAndSize(helv, 7);

            cb.setTextMatrix(document.right() - 128, textBase);  //definir la posicion de text
            cb.showText(text);

            cb.endText();
            cb.addTemplate(total, document.right() - adjust, textBase);  //definir la posicion del total de paginas

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
