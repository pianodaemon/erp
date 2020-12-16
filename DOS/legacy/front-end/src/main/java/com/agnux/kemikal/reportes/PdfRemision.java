/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.agnux.kemikal.reportes;

import com.agnux.common.helpers.StringHelper;
import com.agnux.common.helpers.n2t;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;
import java.io.FileOutputStream;
import java.math.BigInteger;
import java.net.URISyntaxException;
import java.util.*;
import java.util.List;
/**
 *
 * @author vale
 */
public class PdfRemision {
   //--variables para pdf--
    private String imagen;
    public List<HashMap<String, String>> rows;

    
    public List<HashMap<String, String>> getRows() {
        return rows;
    }

    public void setRows(List<HashMap<String, String>> rows) {
        this.rows = rows;
    }
    
    
    public void setImagen(String imagen) {
    	this.imagen = imagen;
    }
    
    public String getImagen() {
    	return imagen;
    }
    
    
   public PdfRemision(HashMap<String, String> datos_remision, ArrayList<HashMap<String, String>> conceptos, String fileout, String ruta_imagen)throws URISyntaxException {
        Font smallsmall = new Font(Font.FontFamily.HELVETICA,5,Font.NORMAL,BaseColor.BLACK);
        Font smallFont = new Font(Font.FontFamily.HELVETICA,7,Font.NORMAL,BaseColor.BLACK);
        
        Font font = new Font(Font.getFamily("ARIAL"), 8, Font.NORMAL);
        Font largeBoldFont = new Font(Font.FontFamily.HELVETICA,8,Font.BOLD,BaseColor.BLACK);
        Font smallBoldFont = new Font(Font.FontFamily.HELVETICA, 8, Font.BOLD, BaseColor.WHITE);
        Font smallBoldFontBlack = new Font(Font.FontFamily.HELVETICA,7,Font.BOLD,BaseColor.BLACK);
        
        this.setRows(conceptos);
        this.setImagen(ruta_imagen);
        
        ImagenPDF ipdf = new ImagenPDF();
        CeldaPDF cepdf = new CeldaPDF();
        //TablaPDF tpdf = new TablaPDF();
        
        
        //this.setTelefono(telefono);
        PdfPTable encabezado;
        PdfPTable tabla_etiqueta;
        PdfPTable tabla_conceptos;
        PdfPTable tabla_totales;
        PdfPTable tablavacia;
        
        PdfPCell cell;
        String cadena;
        Integer contador;
        
        try {
            Document document = new Document(PageSize.LETTER, -50, -50, 20, 30);
            PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(fileout));
            document.open();
            
            float [] widths = {6,12,6};
            encabezado = new PdfPTable(widths);
            encabezado.setKeepTogether(false);
            
            //IMAGEN --> logo empresa
            cell = new PdfPCell(ipdf.addContent());
            cell.setBorder(0);
//cell.setBorderWidth(1);
            cell.setRowspan(6);
            cell.setVerticalAlignment(Element.ALIGN_TOP);
            cell.setHorizontalAlignment(Element.ALIGN_LEFT);
            cell.setUseAscender(true);
            encabezado.addCell(cell);
            
            
            
            //RAZON SOCIAL --> BeanFromCFD (X_emisor)
            cell = new PdfPCell(new Paragraph(datos_remision.get("emisor_razon_social").toUpperCase(),largeBoldFont));
            cell.setBorder(0);
//cell.setBorderWidth(1);
            cell.setUseAscender(true);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            encabezado.addCell(cell);
            
            
            cadena = datos_remision.get("tipo_documento") + "&" +
                    datos_remision.get("folio") + "&" +
                    datos_remision.get("lugar_expedicion") + "\n" + datos_remision.get("fecha_remision")+"&"+
                    datos_remision.get("dias_credito")+ "&" +
                    datos_remision.get("cancelado")+ "&" +
                    datos_remision.get("orden_compra")+" ";
                    
            //System.out.println("Esta es la cadena"+cadena);
            cell = new PdfPCell(cepdf.addContent(cadena));
            cell.setBorder(0);
//cell.setBorderWidth(1);
            cell.setRowspan(7);
            encabezado.addCell(cell);
            
            
            cell = new PdfPCell(new Paragraph("",largeBoldFont));
            cell.setBorder(0);
//cell.setBorderWidth(1);
            cell.setFixedHeight(10);
            cell.setUseAscender(true);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            encabezado.addCell(cell);
            
           
            //cell = new PdfPCell(new Paragraph("2",largeBoldFont));
            cell = new PdfPCell(new Paragraph( datos_remision.get("emisor_calle").toUpperCase()+ " " + datos_remision.get("emisor_numero").toUpperCase() +  "\n" + datos_remision.get("emisor_colonia").toUpperCase() + "\n" + datos_remision.get("emisor_municipio").toUpperCase() + ", " + datos_remision.get("emisor_estado").toUpperCase()+ ", " + datos_remision.get("emisor_pais").toUpperCase() + "\nC.P. " + datos_remision.get("emisor_cp") + "    R.F.C.: " + datos_remision.get("emisor_rfc"), smallFont));
            cell.setBorder(0);
//cell.setBorderWidth(1);
            cell.setRowspan(2);
            cell.setUseAscender(true);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            encabezado.addCell(cell);
            
            cell = new PdfPCell(new Paragraph("",largeBoldFont));
            cell.setBorder(0);
//cell.setBorderWidth(1);
            cell.setUseAscender(true);
            cell.setFixedHeight(10);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            encabezado.addCell(cell);
            
            cell = new PdfPCell(new Paragraph("SUCURSAL: "+datos_remision.get("sucursal_emisor").toUpperCase(),smallFont));
            cell.setBorder(0);
//cell.setBorderWidth(1);
            cell.setUseAscender(true);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            encabezado.addCell(cell);
            
            
            //////////////////////////////////////////////////////////////////
            
            String datosCliente = 
                    datos_remision.get("cliente")+" \n"+ 
                    datos_remision.get("cliente_calle") +" "+ 
                    datos_remision.get("cliente_numero") + ", " + 
                    datos_remision.get("cliente_colonia")+ ", " + 
                    datos_remision.get("cliente_municipio")+ ", " + 
                    datos_remision.get("cliente_estado") + ", " + 
                    datos_remision.get("cliente_pais") + " \nC.P. " + 
                    datos_remision.get("cliente_cp") + "     TEL. "+ 
                    datos_remision.get("cliente_telefono") +  "\nR.F.C.: " + 
                    datos_remision.get("cliente_rfc");
            
            
            cell = new PdfPCell(new Paragraph(StringHelper.capitalizaString(datosCliente), smallFont));
            cell.setBorder(0);
//cell.setBorderWidth(1);
            cell.setColspan(2);
            cell.setUseAscender(true);
            cell.setHorizontalAlignment(Element.ALIGN_LEFT);
            encabezado.addCell(cell);
            
            
                
            
            cell = new PdfPCell(new Paragraph("",largeBoldFont));
            cell.setBorder(0);
            //cell.setBorder(1);
            //cell.setBorder(11);
            cell.setUseAscender(true);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            encabezado.addCell(cell);
            
            document.add(encabezado);
            
            
            ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
            //tabla quespacia las tablas con informacion
            
            tablavacia = new PdfPTable(1);
            tablavacia.setKeepTogether(true);
            cell = new PdfPCell(new Paragraph("",largeBoldFont));
            cell.setBorder(0);
            cell.setUseAscender(true);
            cell.setHorizontalAlignment(Element.ALIGN_LEFT);
            cell.setUseDescender(true);
            tablavacia.addCell(cell);
            document.add(tablavacia);
            
            //fin de la tabla espaciadora entre tablas
            ///////////////////
            
            //--------INICIA TABLA CONTENEDOR DE LA TABLA CONCEPTOS---------------------------------------------------------------------------
           
            tabla_etiqueta = new PdfPTable(1);
            tabla_etiqueta.setKeepTogether(true);
            
            tabla_etiqueta.addCell(cell);
            cell = new PdfPCell(new Paragraph("CONCEPTOS",largeBoldFont));
            cell.setBorder(0);
            cell.setUseAscender(true);
            cell.setHorizontalAlignment(Element.ALIGN_LEFT);
            cell.setUseDescender(true);
            tabla_etiqueta.addCell(cell);
            
            //Agregar tabla para etiqueta CONCEPTOS
            document.add(tabla_etiqueta);
            
            
            
            //--------INICIA TABLA CONCEPTOS---------------------------------------------------------------------------
            
            double sumaimporte=0.0;
            double iva=0.0;
            double iva_retenido=0.0;
            String sumaTotal;
            String moneda = "";
            
            
            float [] anchocolumnas;
            String[] columnas;
            String[] wordList;
            
            //Valores cuando NO incliye IEPS
            float [] anchocolumnas1 = {1.5f, 1f, 1.5f, 4f, 0.5f,1.3f, 0.5f,1.5f};
            String[] columnas1 = {"CODIGO","CANTIDAD","UNIDAD","DESCRIPCION","","P.UNITARIO","","IMPORTE"};
            String[] wordList1 = {"codigo","cantidad","unidad","descripcion","denominacion","precio_unitario","denominacion","importe"};
            
            //Ancho columnas con IEPS
            float [] anchocolumnas2 = {1.5f, 1.1f, 1.5f, 3.1f, 0.5f,1.3f, 0.5f,1.4f,0.5f,1.3f};
            String[] columnas2 = {"CODIGO","CANTIDAD","UNIDAD","DESCRIPCION","","P.UNITARIO","","IMPORTE","","MONTO IEPS"};
            String[] wordList2 = {"codigo","cantidad","unidad","descripcion","denominacion","precio_unitario","denominacion","importe", "denominacion","importe_ieps"};
            
            
            
            if(Double.parseDouble(datos_remision.get("monto_ieps"))>0){
                //Aqui entra cuando SI incluye IEPS
                anchocolumnas=anchocolumnas2;
                columnas = columnas2;
                wordList = wordList2;
            }else{
                //Aqui entra cuando NO INCLUYE IEPS
                anchocolumnas=anchocolumnas1;
                columnas = columnas1;
                wordList = wordList1;
            }
            
            //Definir columnas para la tabla de conceptos
            tabla_conceptos = new PdfPTable(anchocolumnas);
            tabla_conceptos.setKeepTogether(true);
            tabla_conceptos.setHeaderRows(1);
            
            List<String>  lista_columnas = (List<String>) Arrays.asList(columnas);
            contador = 0;
            
            //Font smallBoldFont2 = new Font(Font.FontFamily.HELVETICA,8,Font.BOLD,BaseColor.BLACK);
            for ( String columna_titulo : lista_columnas){
                PdfPCell cellX = new PdfPCell(new Paragraph(columna_titulo,smallBoldFont));
                cellX.setUseAscender(true);
                cellX.setUseDescender(true);
                cellX.setBackgroundColor(BaseColor.BLACK);
                tabla_conceptos.addCell(cellX);
            }
            
           for (HashMap<String, String> registro : this.getRows()){
                //Indices del HashMap que representa el row
                //String[] wordList = {"codigo","cantidad","unidad","descripcion","denominacion","precio_unitario","denominacion","importe"};
                List<String>  indices = (List<String>) Arrays.asList(wordList);
                for (String omega : indices){
                    PdfPCell celda = null;
                    
                    if (omega.equals("codigo")){
                        celda = new PdfPCell(new Paragraph(registro.get(omega),smallFont));
                        celda.setHorizontalAlignment(Element.ALIGN_LEFT);
                        celda.setVerticalAlignment(Element.ALIGN_TOP);
                        celda.setBorder(0);
                        tabla_conceptos.addCell(celda);
                    }

                    if (omega.equals("cantidad")){
                        //celda = new PdfPCell(new Paragraph(registro.get(omega),smallFont));
                        celda= new PdfPCell(new Paragraph(StringHelper.AgregaComas(StringHelper.roundDouble(registro.get(omega).toString(),2)),smallFont));
                        celda.setHorizontalAlignment(Element.ALIGN_RIGHT);
                        celda.setVerticalAlignment(Element.ALIGN_TOP);
                        celda.setBorder(0);
                        tabla_conceptos.addCell(celda);
                    }

                    if (omega.equals("unidad")){
                        celda = new PdfPCell(new Paragraph(registro.get(omega),smallFont));
                        celda.setHorizontalAlignment(Element.ALIGN_LEFT);
                        celda.setVerticalAlignment(Element.ALIGN_TOP);
                        celda.setBorder(0);
                        tabla_conceptos.addCell(celda);
                    }

                    if (omega.equals("descripcion")){
                        celda = new PdfPCell(new Paragraph(registro.get(omega) + registro.get("etiqueta_ieps").toUpperCase(),smallFont));
                        celda.setHorizontalAlignment(Element.ALIGN_LEFT);
                        celda.setVerticalAlignment(Element.ALIGN_TOP);
                        celda.setBorder(0);
                        tabla_conceptos.addCell(celda);
                    }

                    if (omega.equals("denominacion")){
                        celda = new PdfPCell(new Paragraph(datos_remision.get("simbolo_moneda"),smallFont));
                        celda.setHorizontalAlignment(Element.ALIGN_RIGHT);
                        celda.setVerticalAlignment(Element.ALIGN_TOP);
                        celda.setBorder(0);
                        tabla_conceptos.addCell(celda);
                    }

                    if (omega.equals("precio_unitario")){
                        celda= new PdfPCell(new Paragraph(StringHelper.AgregaComas(StringHelper.roundDouble(registro.get(omega).toString(),2)),smallFont));
                        celda.setHorizontalAlignment(Element.ALIGN_RIGHT);
                        celda.setVerticalAlignment(Element.ALIGN_TOP);
                        celda.setBorder(0);
                        tabla_conceptos.addCell(celda);
                    }
                    
                    if (omega.equals("importe")){
                        celda= new PdfPCell(new Paragraph(StringHelper.AgregaComas(StringHelper.roundDouble(registro.get(omega).toString(),4)),smallFont));
                        celda.setHorizontalAlignment(Element.ALIGN_RIGHT);
                        celda.setVerticalAlignment(Element.ALIGN_TOP);
                        celda.setBorder(0);
                        tabla_conceptos.addCell(celda);
                    }
                    if (omega.equals("importe_ieps")){
                        if(Double.parseDouble(registro.get(omega))>0){
                            celda= new PdfPCell(new Paragraph(StringHelper.AgregaComas(StringHelper.roundDouble(registro.get(omega).toString(),4)),smallFont));
                        }else{
                            celda= new PdfPCell(new Paragraph("",smallFont));
                        }
                        celda.setHorizontalAlignment(Element.ALIGN_RIGHT);
                        celda.setVerticalAlignment(Element.ALIGN_TOP);
                        celda.setBorder(0);
                        tabla_conceptos.addCell(celda);
                    }
                }
                contador++;
            }
           
            for(int i=contador; i<=25; i++){
                PdfPCell celda = null;
                celda = new PdfPCell(new Paragraph("",smallFont));
                celda.setHorizontalAlignment(Element.ALIGN_LEFT);
                celda.setVerticalAlignment(Element.ALIGN_TOP);
                celda.setBorder(0);
                celda.setColspan(8);
                celda.setFixedHeight(10);
                tabla_conceptos.addCell(celda);
            }
           
            
            //agregamos al documento la tabla de conceptos
            document.add(tabla_conceptos);
            //--------TERMINA TABLA CONCEPTOS---------------------------------------------------------------------------
            
            
           
            
            
            
            
            
            //--------INICIA TABLA DE TOTALES---------------------------------------------------------------------------
            float [] anchocolumnastotales = {1f, 1f, 2f, 4f, 0.5f,1.3f, 0.5f,1.5f};
            
            tabla_totales = new PdfPTable(anchocolumnastotales);
            tabla_totales.setKeepTogether(true);
            
           //--FILA SUBTOTAL----------
            cell = new PdfPCell(new Paragraph("",smallFont));
            cell.setBorderWidthTop(1);
            cell.setBorderWidthBottom(0);
            cell.setBorderWidthLeft(1);
            cell.setBorderWidthRight(0);
            cell.setColspan(4);
            cell.setUseAscender(true);
            cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            cell.setUseDescender(true);
            tabla_totales.addCell(cell);
            
            
            cell = new PdfPCell(new Paragraph("SUB-TOTAL",smallBoldFontBlack));
            cell.setColspan(2);
            cell.setUseAscender(true);
            cell.setBorderWidthTop(1);
            cell.setBorderWidthBottom(0);
            cell.setBorderWidthLeft(0);
            cell.setBorderWidthRight(1);
            cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            cell.setUseDescender(true);
            tabla_totales.addCell(cell);
            
            cell = new PdfPCell(new Paragraph(datos_remision.get("simbolo_moneda"),smallBoldFontBlack));
            cell.setBorderWidthTop(1);
            cell.setBorderWidthBottom(0);
            cell.setBorderWidthLeft(0);
            cell.setBorderWidthRight(0);
            cell.setUseAscender(true);
            cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            cell.setUseDescender(true);
            tabla_totales.addCell(cell);
            
            cell = new PdfPCell(new Paragraph(StringHelper.AgregaComas(StringHelper.roundDouble(datos_remision.get("subtotal"),2)),smallBoldFontBlack));
            cell.setBorderWidthTop(1);
            cell.setBorderWidthBottom(0);
            cell.setBorderWidthLeft(0);
            cell.setBorderWidthRight(1);
            cell.setUseAscender(true);
            cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            cell.setUseDescender(true);
            tabla_totales.addCell(cell);
            /////////////////////////////////////////////////////////////////////////////////////////////777777777
            
            
            //--FILA IEPS----------
            if(Double.parseDouble(datos_remision.get("monto_ieps"))>0){
                //Aqui entra cuando SI incluye IEPS
                cell = new PdfPCell(new Paragraph("",smallFont));
                cell.setBorderWidthTop(0);
                cell.setBorderWidthBottom(0);
                cell.setBorderWidthLeft(1);
                cell.setBorderWidthRight(0);
                cell.setColspan(4);
                cell.setUseAscender(true);
                cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                cell.setUseDescender(true);
                tabla_totales.addCell(cell);
                
                cell = new PdfPCell(new Paragraph("IEPS",smallBoldFontBlack));
                cell.setColspan(2);
                cell.setUseAscender(true);
                cell.setBorderWidthTop(0);
                cell.setBorderWidthBottom(0);
                cell.setBorderWidthLeft(0);
                cell.setBorderWidthRight(1);
                cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                cell.setUseDescender(true);
                tabla_totales.addCell(cell);

                cell = new PdfPCell(new Paragraph(datos_remision.get("simbolo_moneda"),smallBoldFontBlack));
                cell.setBorderWidthTop(0);
                cell.setBorderWidthBottom(0);
                cell.setBorderWidthLeft(0);
                cell.setBorderWidthRight(0);
                cell.setUseAscender(true);
                cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                cell.setUseDescender(true);
                tabla_totales.addCell(cell);

                cell = new PdfPCell(new Paragraph(StringHelper.AgregaComas(StringHelper.roundDouble(datos_remision.get("monto_ieps"),2)),smallBoldFontBlack));
                cell.setBorderWidthTop(0);
                cell.setBorderWidthBottom(0);
                cell.setBorderWidthLeft(0);
                cell.setBorderWidthRight(1);
                cell.setUseAscender(true);
                cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                cell.setUseDescender(true);
                tabla_totales.addCell(cell);
            }
            
            
            //--FILA IVA----------
            cell = new PdfPCell(new Paragraph("",smallFont));
            cell.setBorderWidthTop(0);
            cell.setBorderWidthBottom(0);
            cell.setBorderWidthLeft(1);
            cell.setBorderWidthRight(0);
            cell.setColspan(4);
            cell.setUseAscender(true);
            cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            cell.setUseDescender(true);
            tabla_totales.addCell(cell);
            
            
            cell = new PdfPCell(new Paragraph("IVA",smallBoldFontBlack));
            cell.setBorder(0);
            cell.setColspan(2);
            cell.setUseAscender(true);
            cell.setBorderWidthTop(0);
            cell.setBorderWidthBottom(0);
            cell.setBorderWidthLeft(0);
            cell.setBorderWidthRight(1);
            cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            cell.setUseDescender(true);
            tabla_totales.addCell(cell);
           
            cell = new PdfPCell(new Paragraph(datos_remision.get("simbolo_moneda"),smallBoldFontBlack));
            cell.setBorder(0);
            cell.setUseAscender(true);
            cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            cell.setUseDescender(true);
            tabla_totales.addCell(cell);
            
            
            cell= new PdfPCell(new Paragraph(StringHelper.AgregaComas(StringHelper.roundDouble(datos_remision.get("impuesto"),2)),smallBoldFontBlack));
            cell.setBorderWidthTop(0);
            cell.setBorderWidthBottom(0);
            cell.setBorderWidthLeft(0);
            cell.setBorderWidthRight(1);
            cell.setUseAscender(true);
            cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            cell.setUseDescender(true);
            tabla_totales.addCell(cell);
            /////////////////////////////////////////////////////////////////////////////////////////////////////77
            
            cell = new PdfPCell(new Paragraph("IMPORTE CON LETRA:",smallFont));
            cell.setBorderWidthTop(0);
            cell.setBorderWidthBottom(0);
            cell.setBorderWidthLeft(1);
            cell.setBorderWidthRight(0);
            cell.setColspan(4);
            cell.setUseAscender(true);
            cell.setHorizontalAlignment(Element.ALIGN_LEFT);
            cell.setUseDescender(true);
            tabla_totales.addCell(cell);
            
            
            cell = new PdfPCell(new Paragraph("RETENCIÓN",smallBoldFontBlack));
            cell.setBorder(0);
            cell.setColspan(2);
            cell.setUseAscender(true);
            cell.setBorderWidthTop(0);
            cell.setBorderWidthBottom(0);
            cell.setBorderWidthLeft(0);
            cell.setBorderWidthRight(1);
            cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            cell.setUseDescender(true);
            tabla_totales.addCell(cell);
            
            cell = new PdfPCell(new Paragraph(datos_remision.get("simbolo_moneda"),smallBoldFontBlack));
            cell.setBorder(0);
            cell.setUseAscender(true);
            cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            cell.setUseDescender(true);
            tabla_totales.addCell(cell);
            
            cell= new PdfPCell(new Paragraph(StringHelper.AgregaComas(StringHelper.roundDouble(datos_remision.get("monto_retencion"),2)),smallBoldFontBlack));
            cell.setBorderWidthTop(0);
            cell.setBorderWidthBottom(0);
            cell.setBorderWidthLeft(0);
            cell.setBorderWidthRight(1);
            cell.setUseAscender(true);
            cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            cell.setUseDescender(true);
            tabla_totales.addCell(cell);
            ////////////////////////////////////////////////////////////////////////////////////////////7777
            
            
            sumaTotal = StringHelper.roundDouble(datos_remision.get("total"),2);
            BigInteger num = new BigInteger(sumaTotal.split("\\.")[0]);
            
            //System.out.println("sumaTotal: "+sumaTotal);
            //System.out.println("num: "+num);
            
            n2t cal = new n2t();
            String centavos = sumaTotal.substring(sumaTotal.indexOf(".")+1);
            //System.out.println("centavos: "+centavos);
            
            String numero = cal.convertirLetras(num);
            //System.out.println("numero: "+numero);
            //String numeroMay = StringHelper.capitalizaString(numero);
            //String numeroMay = numero;

            //convertir a mayuscula la primera letra de la cadena
            //String numeroMay = numero.substring(0, 1).toUpperCase() + numero.substring(1, numero.length());
            String numeroMay = numero;
            
            String denominacion = datos_remision.get("titulo_moneda").toLowerCase();
            String denom = datos_remision.get("moneda_abr");
            
            cell = new PdfPCell(new Paragraph(numeroMay.toUpperCase() + " " + denominacion.toUpperCase() + " " +centavos+"/100 "+ denom.toUpperCase(), font));
            cell.setBorderWidthTop(0);
            cell.setBorderWidthBottom(1);
            cell.setBorderWidthLeft(1);
            cell.setBorderWidthRight(0);
            cell.setColspan(4);
            cell.setUseAscender(true);
            cell.setHorizontalAlignment(Element.ALIGN_LEFT);
            cell.setUseDescender(true);
            tabla_totales.addCell(cell);
            //tablavacia.setSpacingAfter(25f);
            
            cell = new PdfPCell(new Paragraph("TOTAL",smallBoldFontBlack));
            cell.setBorder(0);
            cell.setColspan(2);
            cell.setUseAscender(true);
            cell.setBorderWidthTop(0);
            cell.setBorderWidthBottom(1);
            cell.setBorderWidthLeft(0);
            cell.setBorderWidthRight(1);
            cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            cell.setUseDescender(true);
            tabla_totales.addCell(cell);
            
            cell = new PdfPCell(new Paragraph(datos_remision.get("simbolo_moneda"),smallBoldFontBlack));
            cell.setBorderWidthTop(0);
            cell.setBorderWidthBottom(1);
            cell.setBorderWidthLeft(0);
            cell.setBorderWidthRight(0);
            cell.setUseAscender(true);
            cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            cell.setUseDescender(true);
            tabla_totales.addCell(cell);
            
            //cell = new PdfPCell(new Paragraph("funcionaaaaaa",smallFont));
            cell = new PdfPCell(new Paragraph(StringHelper.AgregaComas(StringHelper.roundDouble(datos_remision.get("total"),2)),smallBoldFontBlack));
            cell.setBorderWidthTop(0);
            cell.setBorderWidthBottom(1);
            cell.setBorderWidthLeft(0);
            cell.setBorderWidthRight(1);
            cell.setUseAscender(true);
            cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            cell.setUseDescender(true);
            tabla_totales.addCell(cell);
            
            //--------TERMINA TABLA DE TOTALES---------------------------------------------------------------------------
            
            document.add(tabla_totales);
            
            
            
            
            //tabla quespacia las tablas con informacion
            tablavacia = new PdfPTable(1);
            tablavacia.setKeepTogether(true);
            
            cell = new PdfPCell(new Paragraph("",largeBoldFont));
            cell.setBorder(0);
            cell.setUseAscender(true);
            cell.setHorizontalAlignment(Element.ALIGN_LEFT);
            cell.setUseDescender(true);
            //cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            tablavacia.addCell(cell);
            
            tablavacia.setSpacingAfter(2f);
            document.add(tablavacia);
            
            //fin de la tabla espaciadora entre tablas
            
            
            
            tabla_conceptos = new PdfPTable(1);
            tabla_conceptos.setKeepTogether(true);
            cell = new PdfPCell(new Paragraph("OBSERVACIONES",largeBoldFont));
            cell.setBorder(0);
            cell.setUseAscender(true);
            cell.setHorizontalAlignment(Element.ALIGN_LEFT);
            cell.setUseDescender(true);
            tabla_conceptos.addCell(cell);
            
            if ( !datos_remision.get("observaciones").isEmpty() && !datos_remision.get("observaciones").equals("LUGAR DE ENTREGA: ")){
                cell = new PdfPCell(new Paragraph(datos_remision.get("observaciones"), smallFont));
                cell.setBorderWidthTop(1);
                cell.setBorderWidthBottom(1);
                cell.setBorderWidthLeft(1);
                cell.setBorderWidthRight(1);
                cell.setHorizontalAlignment(Element.ALIGN_LEFT);
                tabla_conceptos.addCell(cell); 
            }else{
                cell = new PdfPCell(new Paragraph("",smallFont));
                cell.setBorderWidthTop(0);
                cell.setBorderWidthBottom(0);
                cell.setBorderWidthLeft(0);
                cell.setBorderWidthRight(0);
                tabla_conceptos.addCell(cell);
            }
            
            
            
            //fila  vacia
            cell = new PdfPCell(new Paragraph("",smallBoldFont));
            cell.setBorderWidthTop(0);
            cell.setBorderWidthBottom(0);
            cell.setBorderWidthLeft(0);
            cell.setBorderWidthRight(0);
            cell.setFixedHeight(50);
            tabla_conceptos.addCell(cell);
            
            
            cell = new PdfPCell(new Paragraph("__________________________________________________________________",smallBoldFontBlack));
            cell.setBorderWidthTop(0);
            cell.setBorderWidthBottom(0);
            cell.setBorderWidthLeft(0);
            cell.setBorderWidthRight(0);
            cell.setVerticalAlignment(Element.ALIGN_BOTTOM);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            tabla_conceptos.addCell(cell);
            
            cell = new PdfPCell(new Paragraph("RECIBIDO",smallBoldFontBlack));
            cell.setBorderWidthTop(0);
            cell.setBorderWidthBottom(0);
            cell.setBorderWidthLeft(0);
            cell.setBorderWidthRight(0);
            cell.setVerticalAlignment(Element.ALIGN_TOP);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            tabla_conceptos.addCell(cell);
            
            
            document.add(tabla_conceptos);
            
            
            
            
            
            
            
            document.close();
            PdfReader reader = new PdfReader(fileout);
            
            PdfStamper stamper = new PdfStamper(reader,new FileOutputStream(fileout+".pdf"));
            PdfContentByte over;
           
            int total = reader.getNumberOfPages() + 1;
            for (int i=1; i<total; i++) {
                over = stamper.getOverContent(i);
                PdfTemplate f = over.createAppearance(500,200);
                f.setBoundingBox(encabezado.getDefaultCell());
                over.addTemplate(f, 70,770);
                over.beginText();
                over.setFontAndSize(BaseFont.createFont(BaseFont.HELVETICA, BaseFont.WINANSI, BaseFont.NOT_EMBEDDED), 7);
                over.showTextAligned(PdfContentByte.ALIGN_CENTER, "PAG. " + i + " DE " + (total-1), 570, 773, 0);
                over.endText();
            }
            stamper.close();
            reader.close();
        }
        catch (Exception e) {
                e.printStackTrace();
        }
        
    }//termina pdfRemision

    

   
   
   
    private class ImagenPDF {
        public Image addContent() {
            Image img = null;
            try {
                img = Image.getInstance(getImagen());
                //img.scaleAbsoluteHeight(100);
                img.scaleAbsoluteHeight(90);
                img.scaleAbsoluteWidth(145);
                img.setAlignment(0);
            }
            catch(Exception e){
                System.out.println(e);
            }
            return img;
        }
    }
    
    
    
    
    
    private class CeldaPDF {
       public PdfPTable addContent(String cadena) {
            Font smallFont = new Font(Font.FontFamily.HELVETICA,7,Font.NORMAL,BaseColor.BLACK);
            Font sont = new Font(Font.FontFamily.HELVETICA,8,Font.BOLD,BaseColor.BLACK);
            Font smallBoldFont = new Font(Font.FontFamily.HELVETICA, 8, Font.BOLD, BaseColor.WHITE);
            Font largeBoldFont = new Font(Font.FontFamily.HELVETICA,8,Font.BOLD,BaseColor.BLACK);
            
            
            String [] temp = cadena.split("&");
            PdfPTable table = new PdfPTable(1);
            PdfPCell cell;
            
            String remision_cancelada="";
            
            if ( !temp[4].equals("NO") ){
                remision_cancelada = temp[4];
            }
            
            //imprime REMISION CANCELADA si ya esta cancelada
            cell = new PdfPCell(new Paragraph(remision_cancelada ,largeBoldFont) );
            cell.setBorder(0);
            cell.setUseAscender(true);
            cell.setUseDescender(true);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            table.addCell(cell);
            
            
            //celda vacia
            cell = new PdfPCell(new Paragraph("", smallFont));
            cell.setBorder(0);
            cell.setFixedHeight(8);
            table.addCell(cell);
            
            
            cell = new PdfPCell(new Paragraph("FOLIO REMISIÓN",smallBoldFont));// AQUI HIBA EL FOLIO
            cell.setUseAscender(true);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setUseDescender(true);
            cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            cell.setBackgroundColor(BaseColor.BLACK);
            table.addCell(cell);
            
            cell = new PdfPCell(new Paragraph(temp[1],sont));
            cell.setUseAscender(true);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setUseDescender(true);
            cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            table.addCell(cell);
            
            
            //celda vacia
            cell = new PdfPCell(new Paragraph(" ", smallFont));
            cell.setBorder(1);
            cell.setFixedHeight(8);
            table.addCell(cell);
            
            
            cell = new PdfPCell(new Paragraph("LUGAR Y FECHA",smallBoldFont));
            cell.setUseAscender(true);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setUseDescender(true);
            cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            cell.setBackgroundColor(BaseColor.BLACK);
            table.addCell(cell);
            
            cell = new PdfPCell(new Paragraph(temp[2],smallFont));
            cell.setUseAscender(true);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setUseDescender(true);
            cell.setVerticalAlignment(Element.ALIGN_TOP);
            table.addCell(cell);
            
            
            //celda vacia
            cell = new PdfPCell(new Paragraph(" ", smallFont));
            cell.setBorder(0);
            cell.setFixedHeight(8);
            table.addCell(cell);
            
            
            cell = new PdfPCell(new Paragraph("DIAS DE CREDITO",smallBoldFont));
            cell.setUseAscender(true);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setUseDescender(true);
            cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            cell.setBackgroundColor(BaseColor.BLACK);
            table.addCell(cell);
            
            cell = new PdfPCell(new Paragraph(temp[3],smallFont));
            cell.setUseAscender(true);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setUseDescender(true);
            cell.setVerticalAlignment(Element.ALIGN_TOP);
            table.addCell(cell);
            
            //celda vacia
            cell = new PdfPCell(new Paragraph(" ", smallFont));
            cell.setBorder(0);
            cell.setFixedHeight(8);
            table.addCell(cell);
            
            cell = new PdfPCell(new Paragraph("NO. ORDEN COMPRA",smallBoldFont));
            cell.setUseAscender(true);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setUseDescender(true);
            cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            cell.setBackgroundColor(BaseColor.BLACK);
            table.addCell(cell);
            
            cell = new PdfPCell(new Paragraph(temp[5].trim(),smallFont));
            cell.setUseAscender(true);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setUseDescender(true);
            cell.setVerticalAlignment(Element.ALIGN_TOP);
            table.addCell(cell);
            
            
            cell = new PdfPCell(new Paragraph("",smallFont));
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setVerticalAlignment(Element.ALIGN_TOP);
            cell.setBorder(0);
            table.addCell(cell);
            
            return table;
        }
    }
    
     public String esteAtributoSeDejoNulo(String atributo){
         return (atributo != null) ? (atributo) : new String();
    }
}
