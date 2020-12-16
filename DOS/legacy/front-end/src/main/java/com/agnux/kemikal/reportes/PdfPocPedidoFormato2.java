package com.agnux.kemikal.reportes;


import com.agnux.common.helpers.StringHelper;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.io.File;
import java.util.Arrays;


/*
 * Aquí se construye el formato 2 para Pedidos.
 * Éste formato solo abarca la mitad de la Hoja para todo el pedido.
 */
public class PdfPocPedidoFormato2 {
    private String imagen;
    public String empresa_emisora;
    public java.util.List<HashMap<String, String>> rows;
    public File archivoSalida;
    
    public File getArchivoSalida() {
        return archivoSalida;
    }

    public void setArchivoSalida(File archivoSalida) {
        this.archivoSalida = archivoSalida;
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
    
    public PdfPocPedidoFormato2(HashMap<String, String> datosEncabezadoPie, HashMap<String, String> Datos_Pedido, ArrayList<HashMap<String, String>> conceptos_pedido, String razon_social_empresa, String fileout, String ruta_imagen) {
        Font smallsmall = new Font(Font.FontFamily.HELVETICA,5,Font.NORMAL,BaseColor.BLACK);
        Font smallFont = new Font(Font.FontFamily.HELVETICA,7,Font.NORMAL,BaseColor.BLACK);
        Font smallFontBold = new Font(Font.FontFamily.HELVETICA,7,Font.BOLD,BaseColor.BLACK);
        HashMap<String, String> datos = new HashMap<String, String>();
        Font font = new Font(Font.getFamily("ARIAL"), 8, Font.NORMAL);
        Font largeBoldFont = new Font(Font.FontFamily.HELVETICA,8,Font.BOLD,BaseColor.BLACK);
        Font smallBoldFont = new Font(Font.FontFamily.HELVETICA, 8, Font.BOLD, BaseColor.WHITE);
        
        this.setArchivoSalida(new File(fileout));
        this.setEmpresa_emisora(razon_social_empresa.toUpperCase());
        this.setRows(conceptos_pedido);
        
        ImagenPDF ipdf = new ImagenPDF();
        CeldaPDF cepdf = new CeldaPDF();
        
        this.setImagen(ruta_imagen);
        
        PdfPTable tablaPrincipal;
        PdfPTable encabezado;
        PdfPTable tabla_conceptos;
        PdfPTable tablaTotales;
        PdfPTable tablaDatosExtras;
        PdfPTable tablaFooter;
        
        PdfPCell cell;
        String cadena;
        Integer contador;
        
        try {
           // this.setRows(Pedidos);
            Document document = new Document(PageSize.LETTER, -50, -50, 20, 20);
            PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(fileout));
            document.open();
            
            float [] widths1 = {1};
            tablaPrincipal = new PdfPTable(widths1);
            tablaPrincipal.setKeepTogether(false);
            
            float [] widths = {3,12,5};
            encabezado = new PdfPTable(widths);
            encabezado.setKeepTogether(false);
            
            //IMAGEN --> logo empresa
            cell = new PdfPCell(ipdf.addContent());
            cell.setRowspan(6);
            cell.setBorder(0);
            cell.setVerticalAlignment(Element.ALIGN_TOP);
            cell.setHorizontalAlignment(Element.ALIGN_LEFT);
            cell.setUseAscender(true);
            encabezado.addCell(cell);
            
            //RAZON SOCIAL --> BeanFromCFD (X_emisor)
            cell = new PdfPCell(new Paragraph(this.empresa_emisora,largeBoldFont));
            cell.setBorder(0);
            cell.setUseAscender(true);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            encabezado.addCell(cell);
            
            
           cadena = Datos_Pedido.get("numero_control")
                   + "&" +Datos_Pedido.get("folio")
                   + "&" +Datos_Pedido.get("municipio_sucursal").toUpperCase() + ", " + Datos_Pedido.get("estado_sucursal").toUpperCase() + "\n" + Datos_Pedido.get("fecha_expedicion")
                   + "& " +Datos_Pedido.get("cancelado").trim();
                   
           
            cell = new PdfPCell(cepdf.addContent(cadena));
            cell.setBorder(0);
            cell.setRowspan(6);
            encabezado.addCell(cell);
            
            cell = new PdfPCell(new Paragraph( Datos_Pedido.get("emisor_calle").toUpperCase()+ " " + Datos_Pedido.get("emisor_numero").toUpperCase() +  "\n" + Datos_Pedido.get("emisor_colonia").toUpperCase() + "\n" + Datos_Pedido.get("emisor_expedidoen_municipio").toUpperCase() + ", " + Datos_Pedido.get("emisor_expedidoen_Estado").toUpperCase()+ ", " + Datos_Pedido.get("emisor_pais").toUpperCase() + "\nC.P. " + Datos_Pedido.get("emisor_cp") + "    R.F.C.: " + Datos_Pedido.get("emisor_rfc"), smallFont));
            cell.setBorder(0);
            cell.setRowspan(4);
            cell.setUseAscender(true);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            encabezado.addCell(cell);
            
            
            
            cell = new PdfPCell(new Paragraph("",largeBoldFont));
            cell.setBorder(0);
            cell.setUseAscender(true);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            encabezado.addCell(cell);
            
            
            String datosCliente = Datos_Pedido.get("razon_social").toUpperCase()+"\n"
                                + Datos_Pedido.get("calle").toUpperCase()+" "+ Datos_Pedido.get("numero")+", "+ Datos_Pedido.get("colonia").toUpperCase()+",\n"
                                + Datos_Pedido.get("municipio").toUpperCase()+", "+ Datos_Pedido.get("Estado").toUpperCase()+", "+ Datos_Pedido.get("pais").toUpperCase()+", "+ "C.P. "+ Datos_Pedido.get("cp")+"       TEL. "+Datos_Pedido.get("telefono")+"\n"
                                + "R.F.C.: "+Datos_Pedido.get("rfc")+"\n\n";
            
            cell = new PdfPCell(new Paragraph(datosCliente,smallFont));
            cell.setBorder(0);
            cell.setColspan(3);
            cell.setUseAscender(true);
            cell.setHorizontalAlignment(Element.ALIGN_LEFT);
            encabezado.addCell(cell);
            
            //aqui agregamos la tabla encabezado a la tabla principal
            cell = new PdfPCell(encabezado);
            cell.setBorder(0);
            cell.setUseAscender(true);
            cell.setHorizontalAlignment(Element.ALIGN_LEFT);
            tablaPrincipal.addCell(cell);
            
            
            //Variable que cuenta si hay partidas con IEPS
            int contIeps=0;
            
            //Contar partidas con IEPS
            for (HashMap<String, String> reg : this.getRows()){
                if(Integer.parseInt(reg.get("ieps_id"))>0){
                    contIeps++;
                }
            }
            
            //--Aqui comienza la tabla para los conceptos-------------------------------------
            String moneda = "";
            float [] anchocolumnas;
            String[] columnas;
            String[] wordList;
            
            //Valores SIN IEPS
            float [] anchocolumnas1 = {1f, 4.5f, 1.2f, 2f, 1.5f, 0.5f, 1.3f, 0.5f, 1.5f};
            String[] columnas1 = {"CODIGO","DESCRIPCION","UNIDAD","PRESENTACION","CANTIDAD", "","P.UNITARIO","","IMPORTE"};
            String[] wordList1 = {"codigo","titulo","unidad","presentacion","cantidad","denominacion","precio_unitario","denominacion","importe"};
            
            //Ancho columnas CON IEPS
            float [] anchocolumnas2 = {1f, 4f, 1.2f, 1.7f, 1.2f, 0.5f, 1.3f, 0.5f, 1.3f, 0.5f, 1.1f};
            String[] columnas2 = {"CODIGO","DESCRIPCION","UNIDAD","PRESENTACION","CANTIDAD", "","P.UNITARIO","","IMPORTE","","IEPS"};
            String[] wordList2 = {"codigo","titulo","unidad","presentacion","cantidad","denominacion","precio_unitario","denominacion","importe", "denominacion_ieps","importe_ieps"};
            
            if(contIeps>0){
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
            
            
            //INICIO DE LA TABLA DE CONCEPTOS
            tabla_conceptos = new PdfPTable(anchocolumnas);
            tabla_conceptos.setKeepTogether(true);
            
            //Convierte en Lista el Array de valores para las columnas
            java.util.List<String>  lista_columnas = (java.util.List<String>) Arrays.asList(columnas);
            contador = 0;
            for ( String columna_titulo : lista_columnas){
                PdfPCell cellX = new PdfPCell(new Paragraph(columna_titulo,smallBoldFont));
                cellX.setUseAscender(true);
                cellX.setUseDescender(true);
                cellX.setBackgroundColor(BaseColor.BLACK);
                cellX.setHorizontalAlignment(Element.ALIGN_LEFT);
                tabla_conceptos.addCell(cellX);
            }
            
           for (HashMap<String, String> registro : this.getRows()){
                java.util.List<String>  indices = (java.util.List<String>) Arrays.asList(wordList);                
                
                for (String omega : indices){
                    PdfPCell celda = null;
                    
                    if (omega.equals("codigo")){
                        celda = new PdfPCell(new Paragraph(registro.get(omega),smallFont));
                        celda.setHorizontalAlignment(Element.ALIGN_LEFT);
                        celda.setVerticalAlignment(Element.ALIGN_TOP);
                        celda.setBorder(0);
                        tabla_conceptos.addCell(celda);
                    }
                    
                    if (omega.equals("titulo")){
                        celda = new PdfPCell(new Paragraph(registro.get(omega)+registro.get("etiqueta_ieps"),smallFont));
                        celda.setHorizontalAlignment(Element.ALIGN_LEFT);
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
                    
                    if (omega.equals("presentacion")){
                        celda = new PdfPCell(new Paragraph(registro.get(omega),smallFont));
                        celda.setHorizontalAlignment(Element.ALIGN_LEFT);
                        celda.setVerticalAlignment(Element.ALIGN_TOP);
                        celda.setBorder(0);
                        tabla_conceptos.addCell(celda);
                    }
                    
                    if (omega.equals("cantidad")){
                        celda= new PdfPCell(new Paragraph(StringHelper.AgregaComas(StringHelper.roundDouble(registro.get(omega).toString(),2)),smallFont));
                        celda.setHorizontalAlignment(Element.ALIGN_LEFT);
                        celda.setVerticalAlignment(Element.ALIGN_TOP);
                        celda.setBorder(0);
                        tabla_conceptos.addCell(celda);
                    }
                    
                    if (omega.equals("denominacion")){
                        celda = new PdfPCell(new Paragraph(Datos_Pedido.get("simbolo_moneda"),smallFont));
                        celda.setHorizontalAlignment(Element.ALIGN_RIGHT);
                        celda.setVerticalAlignment(Element.ALIGN_TOP);
                        celda.setBorder(0);
                        tabla_conceptos.addCell(celda);
                    }
                    
                    if (omega.equals("precio_unitario")){
                        celda= new PdfPCell(new Paragraph(StringHelper.AgregaComas(StringHelper.roundDouble(registro.get(omega).toString(),2)),smallFont));
                        celda.setHorizontalAlignment(Element.ALIGN_RIGHT);
                        celda.setVerticalAlignment(Element.ALIGN_TOP);
                        celda.setBorder(4); //borde lateral izquierdo
                        celda.setBorder(0);
                        tabla_conceptos.addCell(celda);
                    }
                    
                    if (omega.equals("importe")){
                        celda= new PdfPCell(new Paragraph(StringHelper.AgregaComas(StringHelper.roundDouble(registro.get(omega).toString(),2)),smallFont));
                        celda.setHorizontalAlignment(Element.ALIGN_RIGHT);
                        celda.setVerticalAlignment(Element.ALIGN_TOP);
                        celda.setBorder(0);
                        tabla_conceptos.addCell(celda);
                    }
                    
                    if (omega.equals("denominacion_ieps")){
                        if(Integer.parseInt(registro.get("ieps_id"))>0){
                            celda = new PdfPCell(new Paragraph(Datos_Pedido.get("simbolo_moneda"),smallFont));   
                        }else{
                            celda = new PdfPCell(new Paragraph("",smallFont));
                        }
                        celda.setHorizontalAlignment(Element.ALIGN_RIGHT);
                        celda.setVerticalAlignment(Element.ALIGN_TOP);
                        celda.setBorder(0);
                        tabla_conceptos.addCell(celda);
                    }
                    
                    if (omega.equals("importe_ieps")){
                        if(Integer.parseInt(registro.get("ieps_id"))>0){
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
            }
           
           
            //Aqui agregamos la tabla de conceptos a la tabla principal
            cell = new PdfPCell(tabla_conceptos);
            cell.setBorder(0);
            cell.setFixedHeight(113);
            cell.setUseAscender(true);
            cell.setHorizontalAlignment(Element.ALIGN_LEFT);
            cell.setVerticalAlignment(Element.ALIGN_TOP);
            tablaPrincipal.addCell(cell);
           
           
            //INICIO DE LA TABLA DE TOTALES
            float [] anchocolumnas3 = {1f, 5f, 1f, 2f, 1.5f, 0.5f, 1.3f, 0.5f, 1.5f};
            
            tablaTotales = new PdfPTable(anchocolumnas3);
            tablaTotales.setKeepTogether(true);
            
            int rowspan=4;
           
            if(contIeps>0){
                //Cuando incluye IEPS se hace un RowsPan de 5
                rowspan=5;
            }
            
            String observaciones="";
            if (!Datos_Pedido.get("observaciones").isEmpty()){
                observaciones = Datos_Pedido.get("observaciones");
            }
            
            cell = new PdfPCell(new Paragraph(observaciones,smallFont));
            cell.setBorderWidthTop(1);
            cell.setBorderWidthBottom(0);
            cell.setBorderWidthLeft(0);
            cell.setBorderWidthRight(0);
            cell.setColspan(5);
            cell.setRowspan(rowspan);
            cell.setUseAscender(true);
            cell.setUseDescender(true);
            cell.setHorizontalAlignment(Element.ALIGN_LEFT);
            tablaTotales.addCell(cell);
            
            cell = new PdfPCell(new Paragraph("SUBTOTAL",smallFont));
            cell.setBorderWidthTop(1);
            cell.setBorderWidthBottom(0);
            cell.setBorderWidthLeft(1);
            cell.setBorderWidthRight(0);
            cell.setColspan(2);
            cell.setUseAscender(true);
            cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            cell.setUseDescender(true);
            tablaTotales.addCell(cell);
            
            cell = new PdfPCell(new Paragraph(Datos_Pedido.get("simbolo_moneda"),smallFont));
            cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            cell.setBorderWidthTop(1);
            cell.setBorderWidthBottom(0);
            cell.setBorderWidthLeft(1);
            cell.setBorderWidthRight(0);
            tablaTotales.addCell(cell);
            
            cell = new PdfPCell(new Paragraph(Datos_Pedido.get("subtotal"),smallFont));
            cell.setBorderWidthTop(1);
            cell.setBorderWidthBottom(0);
            cell.setBorderWidthLeft(0);
            cell.setBorderWidthRight(1);
            cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            cell.setUseAscender(true);
            cell.setUseDescender(true);
            tablaTotales.addCell(cell);
            
            /////////////////////////////////////////////////////////////////////////////////////////////
            if(contIeps>0){
                //FILA IEPS
                /*
                cell = new PdfPCell(new Paragraph("",smallFont));
                cell.setBorder(0);
                cell.setColspan(5);
                cell.setUseAscender(true);
                cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                cell.setUseDescender(true);
                tabla_conceptos.addCell(cell);
                */
                cell = new PdfPCell(new Paragraph("IEPS",smallFont));
                cell.setBorderWidthTop(0);
                cell.setBorderWidthBottom(0);
                cell.setBorderWidthLeft(1);
                cell.setBorderWidthRight(0);
                cell.setColspan(2);
                cell.setUseAscender(true);
                cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                cell.setUseDescender(true);
                tablaTotales.addCell(cell);

                cell = new PdfPCell(new Paragraph(Datos_Pedido.get("simbolo_moneda"),smallFont));
                cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                cell.setBorderWidthTop(0);
                cell.setBorderWidthBottom(0);
                cell.setBorderWidthLeft(1);
                cell.setBorderWidthRight(0);
                tablaTotales.addCell(cell);
                
                cell= new PdfPCell(new Paragraph(StringHelper.AgregaComas(StringHelper.roundDouble(Datos_Pedido.get("monto_ieps"),2)),smallFont));
                cell.setUseAscender(true);
                cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                cell.setUseDescender(true);
                cell.setBorderWidthTop(0);
                cell.setBorderWidthBottom(0);
                cell.setBorderWidthLeft(0);
                cell.setBorderWidthRight(1);
                tablaTotales.addCell(cell);
            }
            
            /////////////////////////////////////////////////////////////////////////////////////////////
            /*
            cell = new PdfPCell(new Paragraph("",smallFont));
            cell.setBorder(0);
            cell.setColspan(5);
            cell.setUseAscender(true);
            cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            cell.setUseDescender(true);
            tabla_conceptos.addCell(cell);
            */
            
            cell = new PdfPCell(new Paragraph("IVA",smallFont));
            cell.setBorderWidthTop(0);
            cell.setBorderWidthBottom(0);
            cell.setBorderWidthLeft(1);
            cell.setBorderWidthRight(0);
            cell.setColspan(2);
            cell.setUseAscender(true);
            cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            cell.setUseDescender(true);
            tablaTotales.addCell(cell);
            
            cell = new PdfPCell(new Paragraph(Datos_Pedido.get("simbolo_moneda"),smallFont));
            cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            cell.setBorderWidthTop(0);
            cell.setBorderWidthBottom(0);
            cell.setBorderWidthLeft(1);
            cell.setBorderWidthRight(0);
            tablaTotales.addCell(cell);
            
            
            cell= new PdfPCell(new Paragraph(StringHelper.AgregaComas(StringHelper.roundDouble(Datos_Pedido.get("impuesto"),2)),smallFont));
            cell.setUseAscender(true);
            cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            cell.setUseDescender(true);
            cell.setBorderWidthTop(0);
            cell.setBorderWidthBottom(0);
            cell.setBorderWidthLeft(0);
            cell.setBorderWidthRight(1);
            tablaTotales.addCell(cell);
            
            /*
            cell = new PdfPCell(new Paragraph("",smallFont));
            cell.setBorder(0);
            cell.setColspan(5);
            cell.setUseAscender(true);
            cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            cell.setUseDescender(true);
            tabla_conceptos.addCell(cell);
            */
            
            cell = new PdfPCell(new Paragraph("RETENCIÓN",smallFont));
            cell.setBorderWidthTop(0);
            cell.setBorderWidthBottom(0);
            cell.setBorderWidthLeft(1);
            cell.setBorderWidthRight(0);
            cell.setColspan(2);
            cell.setUseAscender(true);
            cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            cell.setUseDescender(true);
            tablaTotales.addCell(cell);
            
            cell = new PdfPCell(new Paragraph(Datos_Pedido.get("simbolo_moneda"),smallFont));
            cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            cell.setBorderWidthTop(0);
            cell.setBorderWidthBottom(0);
            cell.setBorderWidthLeft(1);
            cell.setBorderWidthRight(0);
            tablaTotales.addCell(cell);
            
            
            cell= new PdfPCell(new Paragraph(StringHelper.AgregaComas(StringHelper.roundDouble(Datos_Pedido.get("monto_retencion"),2)),smallFont));
            cell.setUseAscender(true);
            cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            cell.setUseDescender(true);
            cell.setBorderWidthTop(0);
            cell.setBorderWidthBottom(0);
            cell.setBorderWidthLeft(0);
            cell.setBorderWidthRight(1);
            tablaTotales.addCell(cell);
            
            /*
            cell = new PdfPCell(new Paragraph("",smallFont));
            cell.setBorder(0);
            cell.setColspan(5);
            cell.setUseAscender(true);
            cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            cell.setUseDescender(true);
            tabla_conceptos.addCell(cell);
            */
            
            cell = new PdfPCell(new Paragraph("TOTAL",smallFont));
            cell.setBorderWidthTop(0);
            cell.setBorderWidthBottom(1);
            cell.setBorderWidthLeft(1);
            cell.setBorderWidthRight(0);
            cell.setColspan(2);
            cell.setUseAscender(true);
            cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            cell.setUseDescender(true);
            tablaTotales.addCell(cell);
            
            cell = new PdfPCell(new Paragraph(Datos_Pedido.get("simbolo_moneda"),smallFont));
            cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            cell.setBorderWidthTop(0);
            cell.setBorderWidthBottom(1);
            cell.setBorderWidthLeft(1);
            cell.setBorderWidthRight(0);
            tablaTotales.addCell(cell);
            
            
            cell= new PdfPCell(new Paragraph(StringHelper.AgregaComas(StringHelper.roundDouble(Datos_Pedido.get("total"),2)),smallFont));
            cell.setUseAscender(true);
            cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            cell.setUseDescender(true);
            cell.setBorderWidthTop(0);
            cell.setBorderWidthBottom(1);
            cell.setBorderWidthLeft(0);
            cell.setBorderWidthRight(1);
            tablaTotales.addCell(cell);
            
            
            
            
            if(contIeps<=0){
                //Agregar fila vacia si el pedido no incluye el IEPS
                /*
                cell = new PdfPCell(new Paragraph("",smallFont));
                cell.setBorder(0);
                cell.setColspan(5);
                cell.setUseAscender(true);
                cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                cell.setUseDescender(true);
                tabla_conceptos.addCell(cell);
                */

                cell = new PdfPCell(new Paragraph("",smallFont));
                cell.setBorderWidthTop(0);
                cell.setBorderWidthBottom(0);
                cell.setBorderWidthLeft(0);
                cell.setBorderWidthRight(0);
                cell.setColspan(4);
                cell.setUseAscender(true);
                cell.setHorizontalAlignment(Element.ALIGN_LEFT);
                cell.setUseDescender(true);
                tablaTotales.addCell(cell);
            }
            
            
            
            //aqui agregamos la tabla de Totales a la tabla principal
            cell = new PdfPCell(tablaTotales);
            cell.setBorder(0);
            cell.setUseAscender(true);
            cell.setHorizontalAlignment(Element.ALIGN_LEFT);
            tablaPrincipal.addCell(cell);
           
            //document.add(tabla_conceptos);
            //FIN DE LA TABLA  DE PEDIDOS ENTREGADO ??
            
            
            
            
            
            
            float [] fother = {1f,1f,0.2f,1.2f,0.8f,0.4f,1f,3.2f};
            tablaDatosExtras = new PdfPTable(fother);
            tablaDatosExtras.setKeepTogether(true);
            
            //FILA 1
            cell = new PdfPCell(new Paragraph("Orden de compra:", smallFont));
            cell.setBorderWidthTop(0);
            cell.setBorderWidthBottom(0);
            cell.setBorderWidthLeft(0);
            cell.setBorderWidthRight(0);
            tablaDatosExtras.addCell(cell);
            
            cell = new PdfPCell(new Paragraph(Datos_Pedido.get("orden_compra"), smallFont));
            cell.setBorderWidthTop(0);
            cell.setBorderWidthBottom(1);
            cell.setBorderWidthLeft(0);
            cell.setBorderWidthRight(0);
            tablaDatosExtras.addCell(cell);
            
            cell = new PdfPCell(new Paragraph("", smallFont));
            cell.setBorderWidthTop(0);
            cell.setBorderWidthBottom(0);
            cell.setBorderWidthLeft(0);
            cell.setBorderWidthRight(0);
            tablaDatosExtras.addCell(cell);
            
            cell = new PdfPCell(new Paragraph("Fecha de compromiso:", smallFont));
            cell.setBorderWidthTop(0);
            cell.setBorderWidthBottom(0);
            cell.setBorderWidthLeft(0);
            cell.setBorderWidthRight(0);
            tablaDatosExtras.addCell(cell);
            
            cell = new PdfPCell(new Paragraph(Datos_Pedido.get("fecha_compromiso"), smallFont));
            cell.setBorderWidthTop(0);
            cell.setBorderWidthBottom(1);
            cell.setBorderWidthLeft(0);
            cell.setBorderWidthRight(0);
            tablaDatosExtras.addCell(cell);
            
            cell = new PdfPCell(new Paragraph("", smallFont));
            cell.setBorderWidthTop(0);
            cell.setBorderWidthBottom(0);
            cell.setBorderWidthLeft(0);
            cell.setBorderWidthRight(0);
            tablaDatosExtras.addCell(cell);
            
            cell = new PdfPCell(new Paragraph("", smallFont));
            cell.setBorderWidthTop(0);
            cell.setBorderWidthBottom(0);
            cell.setBorderWidthLeft(0);
            cell.setBorderWidthRight(0);
            tablaDatosExtras.addCell(cell);
            
            cell = new PdfPCell(new Paragraph("", smallFont));
            cell.setBorderWidthTop(0);
            cell.setBorderWidthBottom(0);
            cell.setBorderWidthLeft(0);
            cell.setBorderWidthRight(0);
            tablaDatosExtras.addCell(cell);
            
            
            //FILA 2
            cell = new PdfPCell(new Paragraph("Agente vendedor:", smallFont));
            cell.setBorderWidthTop(0);
            cell.setBorderWidthBottom(0);
            cell.setBorderWidthLeft(0);
            cell.setBorderWidthRight(0);
            tablaDatosExtras.addCell(cell);
            
            cell = new PdfPCell(new Paragraph(Datos_Pedido.get("nombre_agente"), smallFont));
            cell.setBorderWidthTop(0);
            cell.setBorderWidthBottom(1);
            cell.setBorderWidthLeft(0);
            cell.setBorderWidthRight(0);
            cell.setColspan(4);
            tablaDatosExtras.addCell(cell);
            
            cell = new PdfPCell(new Paragraph("", smallFont));
            cell.setBorderWidthTop(0);
            cell.setBorderWidthBottom(0);
            cell.setBorderWidthLeft(0);
            cell.setBorderWidthRight(0);
            tablaDatosExtras.addCell(cell);
            
            //cell = new PdfPCell(new Paragraph("Solicitado por:", smallFont));
            cell = new PdfPCell(new Paragraph("", smallFont));
            cell.setBorderWidthTop(0);
            cell.setBorderWidthBottom(0);
            cell.setBorderWidthLeft(0);
            cell.setBorderWidthRight(0);
            tablaDatosExtras.addCell(cell);
            
            cell = new PdfPCell(new Paragraph("", smallFont));
            cell.setBorderWidthTop(0);
            cell.setBorderWidthBottom(0);
            cell.setBorderWidthLeft(0);
            cell.setBorderWidthRight(0);
            tablaDatosExtras.addCell(cell);
            
            //FILA 3
            cell = new PdfPCell(new Paragraph("Transporte:", smallFont));
            cell.setBorderWidthTop(0);
            cell.setBorderWidthBottom(0);
            cell.setBorderWidthLeft(0);
            cell.setBorderWidthRight(0);
            tablaDatosExtras.addCell(cell);
            
            cell = new PdfPCell(new Paragraph(Datos_Pedido.get("transporte"), smallFont));
            cell.setBorderWidthTop(0);
            cell.setBorderWidthBottom(1);
            cell.setBorderWidthLeft(0);
            cell.setBorderWidthRight(0);
            cell.setColspan(4);
            tablaDatosExtras.addCell(cell);
            
            cell = new PdfPCell(new Paragraph("", smallFont));
            cell.setBorderWidthTop(0);
            cell.setBorderWidthBottom(0);
            cell.setBorderWidthLeft(0);
            cell.setBorderWidthRight(0);
            tablaDatosExtras.addCell(cell);
            
            cell = new PdfPCell(new Paragraph("Lugar de entrega:", smallFont));
            cell.setBorderWidthTop(0);
            cell.setBorderWidthBottom(0);
            cell.setBorderWidthLeft(0);
            cell.setBorderWidthRight(0);
            tablaDatosExtras.addCell(cell);
            
            cell = new PdfPCell(new Paragraph(Datos_Pedido.get("lugar_entrega"), smallFont));
            cell.setBorderWidthTop(0);
            cell.setBorderWidthBottom(1);
            cell.setBorderWidthLeft(0);
            cell.setBorderWidthRight(0);
            tablaDatosExtras.addCell(cell);
            
            
            //aqui agregamos la tablaDatosExtras a la tabla principal
            cell = new PdfPCell(tablaDatosExtras);
            cell.setBorder(0);
            cell.setUseAscender(true);
            cell.setHorizontalAlignment(Element.ALIGN_LEFT);
            tablaPrincipal.addCell(cell);
            
            
            
            //fin de la tabla espaciadora entre tablas
            
            float [] medidas = {1f,0.5f,1f,1.5f,1f,1f,1f,1f};
            tabla_conceptos = new PdfPTable(medidas);
            tabla_conceptos.setKeepTogether(true);
            
            cell = new PdfPCell(new Paragraph("", smallFont));
            cell.setBorder(0);
            cell.setColspan(8);
            if(contIeps<=0){
                cell.setFixedHeight(10);
            }else{
                cell.setFixedHeight(3);
            }
            
            tabla_conceptos.addCell(cell);
            
            cell = new PdfPCell(new Paragraph("Nombre y Firma de Autorización por credito y cobranza:", smallFont));
            cell.setBorderWidthTop(0);
            cell.setBorderWidthBottom(0);
            cell.setBorderWidthLeft(0);
            cell.setBorderWidthRight(0);
            cell.setColspan(3);
            cell.setVerticalAlignment(Element.ALIGN_BOTTOM);
            tabla_conceptos.addCell(cell);
            
            cell = new PdfPCell(new Paragraph(Datos_Pedido.get("nombre_autorizo_pedido"), smallFont));
            cell.setBorderWidthTop(0);
            cell.setBorderWidthBottom(1);
            cell.setBorderWidthLeft(0);
            cell.setBorderWidthRight(0);
            cell.setColspan(3);
            cell.setVerticalAlignment(Element.ALIGN_BOTTOM);
            tabla_conceptos.addCell(cell);
            
            cell = new PdfPCell(new Paragraph("", smallFont));
            cell.setBorderWidthTop(0);
            cell.setBorderWidthBottom(0);
            cell.setBorderWidthLeft(0);
            cell.setBorderWidthRight(0);
            tabla_conceptos.addCell(cell);
            
            cell = new PdfPCell(new Paragraph("", smallFont));
            cell.setBorderWidthTop(0);
            cell.setBorderWidthBottom(0);
            cell.setBorderWidthLeft(0);
            cell.setBorderWidthRight(0);
            tabla_conceptos.addCell(cell);
            
            
            //aqui agregamos la tablaDatosExtras a la tabla principal
            cell = new PdfPCell(tabla_conceptos);
            cell.setBorder(0);
            cell.setUseAscender(true);
            cell.setHorizontalAlignment(Element.ALIGN_LEFT);
            tablaPrincipal.addCell(cell);
            
            
            
            
            
            
            
            float [] medidas2 = {1,1,1};
            tablaFooter = new PdfPTable(medidas2);
            tablaFooter.setKeepTogether(true);
            
            //fila 1 vacia
            cell = new PdfPCell(new Paragraph("", smallFont));
            cell.setBorder(0);
            cell.setFixedHeight(15);
            cell.setColspan(3);
            tablaFooter.addCell(cell);
            
            //Fila 2
            //CELDA CODIGO 1
            cell = new PdfPCell(new Paragraph(datosEncabezadoPie.get("codigo1"), smallFontBold));
            cell.setBorder(0);
            cell.setHorizontalAlignment(Element.ALIGN_LEFT);
            tablaFooter.addCell(cell);
            
            cell = new PdfPCell(new Paragraph("", smallFont));
            cell.setBorder(0);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            tablaFooter.addCell(cell);
            
            //CELDA CODIGO 2
            cell = new PdfPCell(new Paragraph(datosEncabezadoPie.get("codigo2"), smallFontBold));
            cell.setBorder(0);
            cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            tablaFooter.addCell(cell);
            
            
            //aqui agregamos la tablaFooter a la tabla principal
            cell = new PdfPCell(tablaFooter);
            cell.setBorder(0);
            cell.setUseAscender(true);
            cell.setHorizontalAlignment(Element.ALIGN_LEFT);
            tablaPrincipal.addCell(cell);
            
            //aqui agregamos la tablaPrincipal al documento
            document.add(tablaPrincipal);
            
            document.add(new Paragraph("\n\n"));
            
            //Aqui se vuelve a agregar para repetir la cotizacion en la misma hoja
            document.add(tablaPrincipal);
            
            
            document.close();
            
        }
        catch (Exception e) {
                e.printStackTrace();
        }
    }//termina pdf del Pedido

    
    
    
    public void setImagen(String imagen) {
    	this.imagen = imagen;
    }
    
    public String getImagen() {
    	return imagen;
    }
    
    
    private class ImagenPDF {
        public Image addContent() {
            Image img = null;
            try {
                img = Image.getInstance(getImagen());
                img.scaleAbsoluteHeight(70);
                img.scaleAbsoluteWidth(90);
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
            String pedido_cancelado ="";
            
            if(temp[3].equals(" true")){
                pedido_cancelado = "PEDIDO CANCELADO";
            }
            
            cell = new PdfPCell(new Paragraph(pedido_cancelado,largeBoldFont));
            cell.setUseAscender(true);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setUseDescender(true);
            cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            cell.setBorder(0);
            table.addCell(cell);
            
            
            cell = new PdfPCell(new Paragraph("FOLIO PEDIDO",smallBoldFont));
            cell.setUseAscender(true);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setUseDescender(true);
            cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            cell.setBackgroundColor(BaseColor.BLACK);
            table.addCell(cell);
            
            cell = new PdfPCell(new Paragraph(temp[1],smallFont));
            cell.setUseAscender(true);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setUseDescender(true);
            cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            table.addCell(cell);
            
            
            cell = new PdfPCell(new Paragraph("NO.CLIENTE",smallBoldFont));// AQUI HIBA EL FOLIO
            cell.setUseAscender(true);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setUseDescender(true);
            cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            cell.setBackgroundColor(BaseColor.BLACK);
            table.addCell(cell);
            
            cell = new PdfPCell(new Paragraph(temp[0],smallFont));
            cell.setUseAscender(true);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setUseDescender(true);
            cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
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
        
            /*
            cell = new PdfPCell(new Paragraph("LUGAR DE ENTREGA",smallBoldFont));
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
            //cell.setFixedHeight(20);
            table.addCell(cell);
            
            cell = new PdfPCell(new Paragraph("TRANSPORTE",smallBoldFont));
            cell.setUseAscender(true);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setUseDescender(true);
            cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            cell.setBackgroundColor(BaseColor.BLACK);
            table.addCell(cell);
            
            cell = new PdfPCell(new Paragraph(temp[4],smallFont));
            //cell = new PdfPCell(new Paragraph("transporte x",smallFont));
            cell.setUseAscender(true);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setUseDescender(true);
            cell.setVerticalAlignment(Element.ALIGN_TOP);
            //cell.setFixedHeight(20);
            table.addCell(cell);
            */
            return table;
        }
    }
    
     public String esteAtributoSeDejoNulo(String atributo){
         return (atributo != null) ? (atributo) : new String();
    }
    
    
     /*
     static class HeaderFooter extends PdfPageEventHelper {
        protected PdfTemplate total;       
        protected BaseFont helv;  
        protected PdfContentByte cb;  
        Font largeBoldFont = new Font(Font.FontFamily.HELVETICA,10,Font.BOLD,BaseColor.BLACK);
        Font largeFont = new Font(Font.FontFamily.HELVETICA,10,Font.NORMAL,BaseColor.BLACK);
        Font smallFont = new Font(Font.FontFamily.HELVETICA,7,Font.NORMAL,BaseColor.BLACK);
        
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
        HeaderFooter( HashMap<String, String> datos ){
            this.setEmpresa(datos.get("empresa"));
            this.setTitulo_reporte(datos.get("titulo_reporte"));
            this.setCodigo1(datos.get("codigo1"));
            this.setCodigo2(datos.get("codigo2"));
        }
        
        //Añadimos una tabla con  una imagen del logo de megestiono y creamos la fuente para el documento, la imagen esta escalada para que no se muestre pixelada
        @Override
        public void onOpenDocument(PdfWriter writer, Document document) {
            try {
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
        
        //añadimos pie de página, borde y más propiedades
        @Override
        public void onEndPage(PdfWriter writer, Document document) {
            
            //ColumnText.showTextAligned(writer.getDirectContent(),Element.ALIGN_CENTER, new Phrase(this.getEmpresa(),largeBoldFont),document.getPageSize().getWidth()/2, document.getPageSize().getTop() -25, 0);
            //ColumnText.showTextAligned(writer.getDirectContent(),Element.ALIGN_CENTER, new Phrase("Reporte de Rutas",largeBoldFont),document.getPageSize().getWidth()/2, document.getPageSize().getTop()-38, 0);
            
            //SimpleDateFormat formato = new SimpleDateFormat("'Generado el' d 'de' MMMMM 'del' yyyy 'a las' HH:mm:ss 'hrs.'");
            //String impreso_en = formato.format(new Date());
            
            //ColumnText.showTextAligned(writer.getDirectContent(),Element.ALIGN_CENTER, new Phrase(impreso_en,largeFont),document.getPageSize().getWidth()/2, document.getPageSize().getTop()-50, 0);
            
            
            
            //System.out.println(this.getEmpresa());
            //System.out.println(this.getTitulo_reporte());
            
            cb = writer.getDirectContent();
            float textBase = document.bottom() - 10;
            
            //texto inferior izquieda pie de pagina
            String text_left = this.getCodigo1();
            float text_left_Size = helv.getWidthPoint(text_left, 7);
            cb.beginText();
            cb.setFontAndSize(helv, 7);  
            cb.setTextMatrix(document.left()+85, textBase );  //definir la posicion de text
            cb.showText(text_left);
            cb.endText();
            
            
            //texto centro pie de pagina
            String text_center ="Página " + writer.getPageNumber() + " de ";
            float text_center_Size = helv.getWidthPoint(text_center, 7);
            float pos_text_center = (document.getPageSize().getWidth()/2)-(text_center_Size/2);
            float adjust = text_center_Size + 3; 
            cb.beginText();  
            cb.setFontAndSize(helv, 7);  
            cb.setTextMatrix(pos_text_center, textBase );  //definir la posicion de text
            cb.showText(text_center);
            cb.endText();
            cb.addTemplate(total, pos_text_center + adjust, textBase);
            
            
            //texto inferior derecha pie de pagina
            String text_right = this.getCodigo2();
            float textRightSize = helv.getWidthPoint(text_right, 7);
            float pos_text_right = document.getPageSize().getWidth()-textRightSize - 40;
            cb.beginText();
            cb.setFontAndSize(helv, 7);
            cb.setTextMatrix(pos_text_right, textBase);
            cb.showText(text_right);
            cb.endText();
            //cb.restoreState();  
        }
        
        //aqui escrimos ls paginas totales, para que nos salga de pie de pagina Pagina x de y
        @Override
        public void onCloseDocument(PdfWriter writer, Document document) {
          total.beginText();  
          total.setFontAndSize(helv, 7);  
          total.setTextMatrix(0,0);                                           
          total.showText(String.valueOf(writer.getPageNumber() -1));  
          total.endText();  
        }
   }//termina clase HeaderFooter
     */
}
