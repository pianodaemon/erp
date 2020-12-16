/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.agnux.kemikal.reportes;
import com.agnux.common.helpers.StringHelper;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.ExceptionConverter;
import com.itextpdf.text.Font;
import com.itextpdf.text.Image;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfPageEventHelper;
import com.itextpdf.text.pdf.PdfTemplate;
import com.itextpdf.text.pdf.PdfWriter;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.HashMap;

/**
 *
 * @author agnux
 */
public class PdfReporteComOrdenDeCompraFormatoDos {
        //variable para colocar la imagen.
    private String imagen;

    public String getImagen() {
        return imagen;
    }

    public void setImagen(String imagen) {
        this.imagen = imagen;
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
    }
        
        
        
    //HashMap<String, String> datosEncabezadoPie,String fileout,ArrayList<HashMap<String, String>> lista,HashMap<String, String> datos
    public PdfReporteComOrdenDeCompraFormatoDos(HashMap<String, String> datosEncabezadoPie, HashMap<String, String> datosOrdenCompra, ArrayList<HashMap<String, String>> conceptosOrdenCompra, String razon_social_empresa, String fileout, String ruta_imagen) throws DocumentException, FileNotFoundException {
        Font smallBoldFont = new Font(Font.getFamily("ARIAL"),7,Font.NORMAL,BaseColor.WHITE);
        Font fuenteCont = new Font(Font.getFamily("ARIAL"),7,Font.NORMAL,BaseColor.BLACK);
        Font fuentePie = new Font(Font.getFamily("ARIAL"),6,Font.NORMAL,BaseColor.BLACK);
        Font fuentenegrita = new Font(Font.getFamily("ARIAL"),7,Font.BOLD,BaseColor.BLACK);
        Font encabezado = new Font(Font.getFamily("ARIAL"),7,Font.NORMAL,BaseColor.BLACK);
        Font pedido = new Font(Font.getFamily("ARIAL"),8,Font.NORMAL,BaseColor.BLACK);
        Font boldFontBlack8 = new Font(Font.getFamily("ARIAL"),8,Font.BOLD,BaseColor.BLACK);
        
        TablaDetalleCompraPDF tablita = new TablaDetalleCompraPDF();
        //variable para colocar el total del importe
        
        //se le establece un valor a la variable imagen. con set. 
        PdfReporteComOrdenDeCompraFormatoDos.ImagenPDF imagenpdf = new PdfReporteComOrdenDeCompraFormatoDos.ImagenPDF();
        this.setImagen(ruta_imagen);
        
        
        
        //datos para el encabezado
        datosOrdenCompra.put("empresa", datosEncabezadoPie.get("nombre_empresa_emisora"));
        datosOrdenCompra.put("titulo_reporte", datosEncabezadoPie.get("titulo_reporte"));
        String empresa = datosOrdenCompra.get("empresa").toUpperCase()
                        +"\n"+datosOrdenCompra.get("direccion_empresa").toUpperCase()
                        +"\n"+datosOrdenCompra.get("mun_edo").toUpperCase()
                        +"\n"+"C.P. "+datosOrdenCompra.get("emisor_cp").toUpperCase()
                        //+"R.F.C. "+datosOrdenCompra.get("emisor_rfc").toUpperCase()
                        +"\n TELS. "+datosOrdenCompra.get("telefono_empresa").toUpperCase();
                
        String datos_proveedor = datosOrdenCompra.get("razon_social_prov").toUpperCase()
                                +"\n"+datosOrdenCompra.get("direccion").toUpperCase()
                                +"\n"+datosOrdenCompra.get("colonia").toUpperCase()
                                +"\n"+datosOrdenCompra.get("cpytel").toUpperCase();
        
        String facturar_a = datosOrdenCompra.get("empresa").toUpperCase()
                            +"\n"+datosOrdenCompra.get("direccion_empresa").toUpperCase()
                            +"\n"+datosOrdenCompra.get("mun_edo").toUpperCase()
                            +"\n"+"R.F.C. "+datosOrdenCompra.get("emisor_rfc").toUpperCase();
        /*
        String consignado_a = datosOrdenCompra.get("empresa").toUpperCase()
                            +"\n"+datosOrdenCompra.get("direccion_empresa").toUpperCase()
                            +"\n"+datosOrdenCompra.get("mun_edo").toUpperCase()
                            +"\n"+"R.F.C. "+datosOrdenCompra.get("emisor_rfc").toUpperCase();
       */
        
        String consignado_a = datosOrdenCompra.get("consignado_a").toUpperCase();
                                
        boolean mostrarNumPag=false;
        if(conceptosOrdenCompra.size()>=13){
            mostrarNumPag=true;
        }
        //datos para el pie de pagina
        datosOrdenCompra.put("codigo1", datosEncabezadoPie.get("codigo1"));
        datosOrdenCompra.put("codigo2", datosEncabezadoPie.get("codigo2"));
        datosOrdenCompra.put("mostrar_pag", String.valueOf(mostrarNumPag));
        
        //datosOrdenCompra.put("observaciones", datosEncabezadoPie.get("observaciones"));
        
        HeaderFooter event = new HeaderFooter(datosOrdenCompra);
        Document reporte = new Document(PageSize.LETTER, -50, -50, 20, 30);
        PdfWriter writer = PdfWriter.getInstance(reporte, new FileOutputStream(fileout));
        writer.setPageEvent(event);
        
        
        try {            
            reporte.open();
            //aqui se prepara la tabla del encabezado 1
            float [] tam_tablaHead = {6,9,5};
            PdfPTable tablaHead = new PdfPTable(tam_tablaHead);
            PdfPCell celdaHead;
            tablaHead.setKeepTogether(false);
            
            //Esta fila es para imprimir la leyenda CANCELADO cuando la OC ha sido camcelado
            celdaHead = new PdfPCell(new Paragraph("",fuentenegrita));
            celdaHead.setHorizontalAlignment(Element.ALIGN_CENTER);
            celdaHead.setBorder(0);
            tablaHead.addCell(celdaHead);
            
            celdaHead = new PdfPCell(new Paragraph("",fuentenegrita));
            celdaHead.setHorizontalAlignment(Element.ALIGN_CENTER);
            celdaHead.setBorder(0);
            tablaHead.addCell(celdaHead);
            
            celdaHead = new PdfPCell(new Paragraph(datosOrdenCompra.get("estado_oc"),boldFontBlack8));
            celdaHead.setHorizontalAlignment(Element.ALIGN_CENTER);
            celdaHead.setBorder(0);
            tablaHead.addCell(celdaHead);
            
            
            //IMAGEN --> logo empresa
            celdaHead = new PdfPCell(imagenpdf.addContent());
            celdaHead.setBorder(0);
            //cell.setRowspan(10);
            celdaHead.setRowspan(15);
            celdaHead.setUseDescender(true);
            celdaHead.setVerticalAlignment(Element.ALIGN_TOP);
            tablaHead.addCell(celdaHead);

            //RAZON SOCIAL AQUI VA EL NOMBRE DE LA EMPRESA
            celdaHead = new PdfPCell(new Paragraph(empresa,encabezado));
            celdaHead.setHorizontalAlignment(Element.ALIGN_CENTER);
            celdaHead.setRowspan(15);
            celdaHead.setBorder(0);
            tablaHead.addCell(celdaHead);

            //NOMBRE DE ORDEN DE COMPRA
            celdaHead = new PdfPCell(new Paragraph("ORDEN DE COMPRA",smallBoldFont));
            celdaHead.setHorizontalAlignment(Element.ALIGN_CENTER);
            celdaHead.setBackgroundColor(BaseColor.BLACK);
            tablaHead.addCell(celdaHead);

            //CONTENIDO DE ORDEN DE COMPRA
            celdaHead = new PdfPCell(new Paragraph(datosOrdenCompra.get("folio"),pedido));
            celdaHead.setHorizontalAlignment(Element.ALIGN_CENTER);
            celdaHead.setRowspan(1);
            tablaHead.addCell(celdaHead);
            
             //AQUI VA LA FECHA 
            celdaHead = new PdfPCell(new Paragraph("FECHA - DATE",smallBoldFont));
            celdaHead.setHorizontalAlignment(Element.ALIGN_CENTER);
            celdaHead.setBackgroundColor(BaseColor.BLACK);
            tablaHead.addCell(celdaHead);
            
            //AQUI VA el contenido de la fecha de orden compra 
            celdaHead = new PdfPCell(new Paragraph(datosOrdenCompra.get("fecha"),fuenteCont));
            celdaHead.setHorizontalAlignment(Element.ALIGN_CENTER);
            tablaHead.addCell(celdaHead);
            
            //AQUI VA LA FECHA 
            celdaHead = new PdfPCell(new Paragraph("FECHA REQUERIDA - REQUIRED DATE",smallBoldFont));
            celdaHead.setHorizontalAlignment(Element.ALIGN_CENTER);
            celdaHead.setBackgroundColor(BaseColor.BLACK);
            tablaHead.addCell(celdaHead);
            
            //AQUI VA el contenido de la fecha de orden compra 
            celdaHead = new PdfPCell(new Paragraph(datosOrdenCompra.get("fecha_entrega"),fuenteCont));
            celdaHead.setHorizontalAlignment(Element.ALIGN_CENTER);
            tablaHead.addCell(celdaHead);
            

            //esto es un espacio en blanco PARA DESPEGAR LAS TABLAS
            celdaHead = new PdfPCell(new Paragraph("\n",fuentenegrita));
            celdaHead.setBorder(1);
            celdaHead.setColspan(3);
            tablaHead.addCell(celdaHead);
            
            reporte.add(tablaHead);
            
            //#####################################################################################################
            //aqui se prepara la tabla del encabezado 2 donde van los datos proveedor etc

            PdfPTable tablaHead2 = new PdfPTable(2);
            PdfPCell celdaHead2;
            tablaHead2.setKeepTogether(false);
            
            //TITULOS DE  PROVEEDOR Y CONSIGNACION
            celdaHead2 = new PdfPCell(new Paragraph("PROVEEDOR-SUPLIER",fuentenegrita));
            celdaHead2.setUseAscender(true);
            celdaHead2.setHorizontalAlignment(Element.ALIGN_CENTER);
            tablaHead2.addCell(celdaHead2);
            
            //TITULOS DE CONSIGNAR A
            celdaHead2 = new PdfPCell(new Paragraph("CONSIGNAR A: - CONSIGNEE TO:",fuentenegrita));
            celdaHead2.setUseAscender(true);
            celdaHead2.setHorizontalAlignment(Element.ALIGN_CENTER);
            tablaHead2.addCell(celdaHead2);
            
            //AQUI VAN LOS DATOS DEL PROVEEDOR.
            celdaHead2 = new PdfPCell(new Paragraph(datos_proveedor,fuenteCont));
            celdaHead2.setUseAscender(true);
            celdaHead2.setHorizontalAlignment(Element.ALIGN_LEFT);
            tablaHead2.addCell(celdaHead2);
            
            //CONTENIDO DE CONSIGNADO A, QUE ES LA EMPRESA MISMA QUE HACE LA COMPRA
            celdaHead2 = new PdfPCell(new Paragraph(consignado_a,fuenteCont));
            celdaHead2.setUseAscender(true);
            celdaHead2.setHorizontalAlignment(Element.ALIGN_LEFT);
            celdaHead2.setUseDescender(true);
            tablaHead2.addCell(celdaHead2);
            
            //TITULOS FACTURAR Y CONDICIONES DE PAGO
            celdaHead2 = new PdfPCell(new Paragraph("FACTURAR A: - INVOICE TO: ",fuentenegrita));
            celdaHead2.setUseAscender(true);
            celdaHead2.setHorizontalAlignment(Element.ALIGN_CENTER);
            tablaHead2.addCell(celdaHead2);
            
            //TITULO CONDICIONES DE PAGO
            celdaHead2 = new PdfPCell(new Paragraph("CONDICIONES DE PAGO: - PAYMENT TERM",fuentenegrita));
            celdaHead2.setUseAscender(true);
            celdaHead2.setHorizontalAlignment(Element.ALIGN_CENTER);
            tablaHead2.addCell(celdaHead2);
            
            //CONTENIDO DE FACTURAR A. QUE ES EL NOMBRE DE LA EMPRESA QUE HACE LA COMPRA
            celdaHead2 = new PdfPCell(new Paragraph(facturar_a,fuenteCont));
            celdaHead2.setUseAscender(true);
            celdaHead2.setHorizontalAlignment(Element.ALIGN_LEFT);
            tablaHead2.addCell(celdaHead2);
            
            //CONTENIDO DE CONDICIONES DE PAGO
            celdaHead2 = new PdfPCell(new Paragraph(datosOrdenCompra.get("condiciones_pago").toUpperCase(),fuenteCont));
            celdaHead2.setUseAscender(true);
            celdaHead2.setHorizontalAlignment(Element.ALIGN_LEFT);
            celdaHead2.setUseDescender(true);
            tablaHead2.addCell(celdaHead2);
            
            reporte.add(tablaHead2);
            //#####################################################################################################
            // AQUI VA LA OTRA TABLA DE DATOS DEL PROVEEDOR
            PdfPTable tablaHead3 = new PdfPTable(6);
            PdfPCell celdaHead3;
            tablaHead3.setKeepTogether(false);
            
            //columna 1 fil1 PROVEEDOR Y CONSIGNACION
            celdaHead3 = new PdfPCell(new Paragraph("No. PROVEEDOR",fuentenegrita));
            celdaHead3.setUseAscender(true);
            celdaHead3.setHorizontalAlignment(Element.ALIGN_CENTER);
            tablaHead3.addCell(celdaHead3);
            
            celdaHead3 = new PdfPCell(new Paragraph("TELEFONO",fuentenegrita));
            celdaHead3.setUseAscender(true);
            celdaHead3.setHorizontalAlignment(Element.ALIGN_CENTER);
            tablaHead3.addCell(celdaHead3);
            
            celdaHead3 = new PdfPCell(new Paragraph("REPRESENTANTE",fuentenegrita));
            celdaHead3.setUseAscender(true);
            celdaHead3.setHorizontalAlignment(Element.ALIGN_CENTER);
            tablaHead3.addCell(celdaHead3);
            
            celdaHead3 = new PdfPCell(new Paragraph("VIA DE EMBARQUE - SHIP VIA",fuentenegrita));
            celdaHead3.setUseAscender(true);
            celdaHead3.setColspan(3);
            celdaHead3.setHorizontalAlignment(Element.ALIGN_CENTER);
            tablaHead3.addCell(celdaHead3);
            
            //AQUI VAN LOS CONTENIDOS DE LAS CELDAS DE ARRIBA
            celdaHead3 = new PdfPCell(new Paragraph(datosOrdenCompra.get("no_provedor").toUpperCase(),fuenteCont));
            celdaHead3.setHorizontalAlignment(Element.ALIGN_CENTER);
            tablaHead3.addCell(celdaHead3);
            
            celdaHead3 = new PdfPCell(new Paragraph(datosOrdenCompra.get("tel_proveedor").toUpperCase(),fuenteCont));
            celdaHead3.setHorizontalAlignment(Element.ALIGN_CENTER);
            tablaHead3.addCell(celdaHead3);
            
            celdaHead3 = new PdfPCell(new Paragraph(datosOrdenCompra.get("representante").toUpperCase(),fuenteCont));
            celdaHead3.setHorizontalAlignment(Element.ALIGN_CENTER);
            tablaHead3.addCell(celdaHead3);
            
            celdaHead3 = new PdfPCell(new Paragraph(datosOrdenCompra.get("tipo_embarque").toUpperCase(),fuenteCont));
            celdaHead3.setHorizontalAlignment(Element.ALIGN_LEFT);
            celdaHead3.setColspan(3);
            tablaHead3.addCell(celdaHead3);
            
            celdaHead3 = new PdfPCell(new Paragraph("\n"));
            celdaHead3.setColspan(6);
            celdaHead3.setBorder(0);
            tablaHead3.addCell(celdaHead3);
            reporte.add(tablaHead3);
            
            //#####################################################################################################
            PdfPTable tabla = new PdfPTable(1);
            PdfPCell celda;
            tabla.setKeepTogether(false);
            
            celda = new PdfPCell(tablita.addContent(datosOrdenCompra,conceptosOrdenCompra));
            
            //Esto es para permitir que se se desplace hacia abajo cuando son muchos registros
            if(conceptosOrdenCompra.size()<14){
                //Si es menor de 14 registros, le damos un tamaño fijo
                celda.setFixedHeight(300);
            }
            
            celda.setUseAscender(true);
            celda.setHorizontalAlignment(Element.ALIGN_CENTER);
            celda.setUseDescender(true);
            celda.setVerticalAlignment(Element.ALIGN_LEFT);
            tabla.addCell(celda);
            
            reporte.add(tabla);
            //###################################################################################################
            //{2,2,3,2,1,1,2,1,1,2}
            float [] tam_tamBody = {2,2,3,2,1,1,2,1,1,2};//Tamaño de las Columnas.
            PdfPTable tablaBody = new PdfPTable(tam_tamBody);
            PdfPCell celdaBody;
            
            //AQUI EMPIEZAN LOS TOTALES ETC. ETC. 
            String simbolo_mon ="";
            simbolo_mon = datosOrdenCompra.get("moneda_simbolo");
            String anexa_docs = "";
            
            if(datosOrdenCompra.get("anexar_doc").equals("true")){
                anexa_docs=" X";
            }
            
            //TABLA PARA AGREGAR EL TEXTO "ANEXAR CERTIFICADOS DE CALIDAD Y HOJAS DE SEGURIDAD"
            float [] widths = {0.5f,0.5f,10}; 
            PdfPTable table_texto = new PdfPTable(widths);
            PdfPCell celdatxt;
            
            //fila vacia
            celdatxt = new PdfPCell(new Paragraph("",fuentePie));
            celdatxt.setBorder(0);
            celdatxt.setColspan(3);
            table_texto.addCell(celdatxt);
            
            //fila vacia
            celdatxt = new PdfPCell(new Paragraph("",fuentePie));
            celdatxt.setBorder(0);
            celdatxt.setColspan(3);
            table_texto.addCell(celdatxt);
            
            celdatxt = new PdfPCell(new Paragraph("",fuentePie));
            celdatxt.setHorizontalAlignment(Element.ALIGN_LEFT);
            celdatxt.setVerticalAlignment(Element.ALIGN_CENTER);
            celdatxt.setBorder(0);
            table_texto.addCell(celdatxt);
            
            celdatxt = new PdfPCell(new Paragraph(anexa_docs,fuentenegrita));
            celdatxt.setHorizontalAlignment(Element.ALIGN_LEFT);
            celdatxt.setVerticalAlignment(Element.ALIGN_CENTER);
            celdatxt.setBorderWidthBottom(1);
            celdatxt.setBorderWidthTop(1);
            celdatxt.setBorderWidthLeft(1);
            celdatxt.setBorderWidthRight(1);
            table_texto.addCell(celdatxt);
            
            celdatxt = new PdfPCell(new Paragraph("ANEXAR CERTIFICADOS DE CALIDAD Y HOJAS DE SEGURIDAD",fuentePie));
            celdatxt.setHorizontalAlignment(Element.ALIGN_LEFT);
            celdatxt.setVerticalAlignment(Element.ALIGN_CENTER);
            celdatxt.setBorder(0);
            table_texto.addCell(celdatxt);
            
            
            
            
            
             //SUBTOTAL
             celdaBody = new PdfPCell(table_texto);
             celdaBody.setColspan(6);
             celdaBody.setRowspan(3);
             celdaBody.setBorder(0);
             celdaBody.setVerticalAlignment(Element.ALIGN_CENTER);
             tablaBody.addCell(celdaBody);
             
             celdaBody = new PdfPCell(new Paragraph("SUBTOTAL",fuentenegrita));
             celdaBody.setUseAscender(true);
             celdaBody.setHorizontalAlignment(Element.ALIGN_RIGHT);
             celdaBody.setUseDescender(true);
             celdaBody.setBorder(0);
             celdaBody.setColspan(2);
             celdaBody.setVerticalAlignment(Element.ALIGN_LEFT);
             tablaBody.addCell(celdaBody);
             
             celdaBody = new PdfPCell(new Paragraph(simbolo_mon,fuenteCont));
             celdaBody.setHorizontalAlignment(Element.ALIGN_RIGHT);
             celdaBody.setVerticalAlignment(Element.ALIGN_LEFT);
             celdaBody.setBorderWidthBottom(0);
             celdaBody.setBorderWidthTop(0);
             celdaBody.setBorderWidthRight(0);
             tablaBody.addCell(celdaBody);
             
             celdaBody = new PdfPCell(new Paragraph(StringHelper.AgregaComas(StringHelper.roundDouble(datosOrdenCompra.get("subtotal"),2)),fuenteCont));
             celdaBody.setBorderWidthBottom(0);
             celdaBody.setBorderWidthTop(0);
             celdaBody.setBorderWidthLeft(0);             
             celdaBody.setHorizontalAlignment(Element.ALIGN_RIGHT);
             celdaBody.setVerticalAlignment(Element.ALIGN_LEFT);
             tablaBody.addCell(celdaBody);
             
             //IMPUESTO
             /*
             celdaBody = new PdfPCell(new Paragraph("",fuenteCont));
             celdaBody.setColspan(6);
             celdaBody.setBorder(0);
             celdaBody.setVerticalAlignment(Element.ALIGN_LEFT);
             tablaBody.addCell(celdaBody);
              */
             celdaBody = new PdfPCell(new Paragraph("I.V.A",fuentenegrita));
             celdaBody.setUseAscender(true);
             celdaBody.setHorizontalAlignment(Element.ALIGN_RIGHT);
             celdaBody.setUseDescender(true);
             celdaBody.setBorder(0);
             celdaBody.setColspan(2);
             celdaBody.setVerticalAlignment(Element.ALIGN_LEFT);
             tablaBody.addCell(celdaBody);
             
             celdaBody = new PdfPCell(new Paragraph(simbolo_mon,fuenteCont));
             celdaBody.setHorizontalAlignment(Element.ALIGN_RIGHT);
             celdaBody.setVerticalAlignment(Element.ALIGN_LEFT);
             celdaBody.setBorderWidthBottom(0);
             celdaBody.setBorderWidthTop(0);
             celdaBody.setBorderWidthRight(0);  
             tablaBody.addCell(celdaBody);
             
             celdaBody = new PdfPCell(new Paragraph(StringHelper.AgregaComas(StringHelper.roundDouble(datosOrdenCompra.get("impuesto"),2)),fuenteCont));
             celdaBody.setHorizontalAlignment(Element.ALIGN_RIGHT);
             celdaBody.setVerticalAlignment(Element.ALIGN_LEFT);
             celdaBody.setBorderWidthBottom(0);
             celdaBody.setBorderWidthTop(0);
             celdaBody.setBorderWidthLeft(0);
             tablaBody.addCell(celdaBody);
             
             
             //TOTAL
             /*
             celdaBody = new PdfPCell(new Paragraph("",fuenteCont));
             celdaBody.setColspan(6);
             celdaBody.setBorder(0);
             celdaBody.setVerticalAlignment(Element.ALIGN_LEFT);
             tablaBody.addCell(celdaBody);
              */
             celdaBody = new PdfPCell(new Paragraph("TOTAL",fuentenegrita));
             celdaBody.setUseAscender(true);
             celdaBody.setHorizontalAlignment(Element.ALIGN_RIGHT);
             celdaBody.setUseDescender(true);
             celdaBody.setBorder(0);
             celdaBody.setColspan(2);
             celdaBody.setVerticalAlignment(Element.ALIGN_LEFT);
             tablaBody.addCell(celdaBody);
             
             celdaBody = new PdfPCell(new Paragraph(simbolo_mon,fuenteCont));
             celdaBody.setHorizontalAlignment(Element.ALIGN_RIGHT);
             celdaBody.setVerticalAlignment(Element.ALIGN_LEFT);
             celdaBody.setBorderWidthTop(0);
             celdaBody.setBorderWidthRight(0);  
             tablaBody.addCell(celdaBody);
             
             celdaBody = new PdfPCell(new Paragraph(StringHelper.AgregaComas(StringHelper.roundDouble(datosOrdenCompra.get("total"),2)),fuenteCont));
             celdaBody.setHorizontalAlignment(Element.ALIGN_RIGHT);
             celdaBody.setVerticalAlignment(Element.ALIGN_LEFT);
             celdaBody.setBorderWidthTop(0);
             celdaBody.setBorderWidthLeft(0);
             tablaBody.addCell(celdaBody);
             
             reporte.add(tablaBody);
             
             //###################################################################################################
            
            
            float [] tam_tablapie = {17}; 
            PdfPTable tablapie = new PdfPTable(tam_tablapie);
            PdfPCell celdapie;
            
            
            //Observaciones
           /* celdapie = new PdfPCell(new Paragraph("OBSERVACIONES",fuentenegrita));
            celdapie.setHorizontalAlignment(Element.ALIGN_LEFT);
            celdapie.setBorder(0);tablapie.addCell(celdapie);*/
            
            //Observaciones contenido
            /*celdapie = new PdfPCell(new Paragraph(datosOrdenCompra.get("observaciones"),fuenteCont));
            celdapie.setHorizontalAlignment(Element.ALIGN_LEFT);
            tablapie.addCell(celdapie);*/
            
            //Observaciones
            //cell = new PdfPCell(new Paragraph("CADENA ORIGINAL:",smallBoldFont));
            
            if (datosOrdenCompra.get("observaciones").isEmpty()){
                celdapie = new PdfPCell(new Paragraph("",smallBoldFont));
                celdapie.setBorder(0);
                tablapie.addCell(celdapie);
                
                celdapie = new PdfPCell(new Paragraph("",smallBoldFont));
                celdapie.setBorder(0);
                tablapie.addCell(celdapie);
            }else{
                celdapie = new PdfPCell(new Paragraph("OBSERVACIONES - REMARKS",fuentenegrita));
                celdapie.setHorizontalAlignment(Element.ALIGN_LEFT);
                celdapie.setBorder(0);tablapie.addCell(celdapie);
                
                celdapie = new PdfPCell(new Paragraph(datosOrdenCompra.get("observaciones"),fuentePie));
                celdapie.setHorizontalAlignment(Element.ALIGN_LEFT);
                tablapie.addCell(celdapie);
            }
            
            //6 FILA EN BLANCO PARA SEPARAR LAS TABLAS
            celdapie = new PdfPCell(new Paragraph("\n \n \n",fuenteCont));
            celdapie.setBorder(0);
            celdapie.setColspan(6);
            tablapie.addCell(celdapie); 
            
            reporte.add(tablapie);
            
            
            
            
            //esto es para los cuadro de abajo para finalizar el PDF
            
            PdfPTable tablapie2 = new PdfPTable(6);
            PdfPCell celdapie2;
            //tablapie2.setKeepTogether(false);
            
            //1
            celdapie2 = new PdfPCell(new Paragraph("SOLICITADO POR:                              NOMBRE Y FIRMA:                         FECHA",fuentePie));
            celdapie2.setColspan(3);
            celdapie2.setHorizontalAlignment(Element.ALIGN_LEFT);
            tablapie2.addCell(celdapie2);
            
            celdapie2 = new PdfPCell(new Paragraph("NO PODEMOS RECIBIR NI PAGAR OPORTUNAMENTE SI SUS "
                    + "REMISIONES Y FACTURAS NO LLEVAN EL NÚMERO DE ESTE PEDIDO "
                    + "Y EL DEL PROVEEDOR, ASI COMO LA DESCRIPCIÓN BODEGA "
                    + "INDICADA Y FECHA DE ENTREGA SOLICITADA POR NOSOTROS. "
                    + "SIRVASE VERIFICAR EL SELLO Y FIRMA DE RECIBIDO EN SU "
                    + "REMISIÓN AL ENTREGAR.",fuentePie));
            celdapie2.setRowspan(2); 
            celdapie2.setColspan(3);
            celdapie2.setHorizontalAlignment(Element.ALIGN_LEFT);
            tablapie2.addCell(celdapie2);
            
            //2
            celdapie2 = new PdfPCell(new Paragraph("\n \n"+datosOrdenCompra.get("fecha"),fuentePie));
            celdapie2.setColspan(3);
            celdapie2.setHorizontalAlignment(Element.ALIGN_RIGHT);
            tablapie2.addCell(celdapie2);
            

            
            //columna 1,2,3
            celdapie2 = new PdfPCell(new Paragraph("\n "+datosOrdenCompra.get("user_elabora").toString(),fuentePie));
            celdapie2.setColspan(3);
            celdapie2.setHorizontalAlignment(Element.ALIGN_LEFT);
            celdapie2.setBorderWidthTop(1);
            celdapie2.setBorderWidthBottom(0);
            celdapie2.setBorderWidthLeft(1);
            celdapie2.setBorderWidthRight(1);
            tablapie2.addCell(celdapie2);
            
            //3
            celdapie2 = new PdfPCell(new Paragraph("\n\nTODO EL MATERIAL SE RECIBE SUJETO A INSPECCIÓN DE CALIDAD Y "
                    + "ESPECIFICACIONES ASÍ COMO A RECONTEO, RECIBIMOS DE LUNES A "
                    + "VIERNES DE 8:00 AM A 4:30 PM, TODO MATERIAL DEBERÁ VENIR "
                    + "AMPARADO POR REMISIÓN ORIGINAL Y CUATRO COPIAS.",fuentePie));
            celdapie2.setColspan(3);
            celdapie2.setRowspan(4);
            celdapie2.setHorizontalAlignment(Element.ALIGN_LEFT);
            tablapie2.addCell(celdapie2);
            
            //3
            celdapie2 = new PdfPCell(new Paragraph("AUTORIZADO P/COMPRAS:              NOMBRE Y FIRMA                          FECHA: "+datosOrdenCompra.get("fecha"),fuentePie));
            celdapie2.setColspan(3);
            celdapie2.setHorizontalAlignment(Element.ALIGN_LEFT);
            celdapie2.setBorderWidthBottom(1);
            celdapie2.setBorderWidthTop(0);
            celdapie2.setBorderWidthLeft(1);
            celdapie2.setBorderWidthRight(1);
            tablapie2.addCell(celdapie2);
            
            
            
            //columna 1,2,3
            celdapie2 = new PdfPCell(new Paragraph("\n "+datosOrdenCompra.get("user_autoriza").toString(),fuentePie));
            celdapie2.setColspan(3);
            celdapie2.setHorizontalAlignment(Element.ALIGN_LEFT);
            celdapie2.setBorderWidthTop(1);
            celdapie2.setBorderWidthBottom(0);
            celdapie2.setBorderWidthLeft(1);
            celdapie2.setBorderWidthRight(1);
            tablapie2.addCell(celdapie2);
            
            //columna 1,2,3
            celdapie2 = new PdfPCell(new Paragraph("AUTORIZADO POR:                            NOMBRE Y FIRMA                          FECHA: "+datosOrdenCompra.get("fecha"),fuentePie));
            celdapie2.setColspan(3);
            celdapie2.setHorizontalAlignment(Element.ALIGN_LEFT);
            celdapie2.setBorderWidthBottom(1);
            celdapie2.setBorderWidthTop(0);
            celdapie2.setBorderWidthLeft(1);
            celdapie2.setBorderWidthRight(1);
            tablapie2.addCell(celdapie2);
            
            reporte.add(tablapie2);
        }
         
        catch (Exception e){
             System.out.println(e.toString());
             }
        reporte.close();
    }
    
    
    private class TablaDetalleCompraPDF{
            public PdfPTable addContent(HashMap<String, String> datosOrdenCompra,ArrayList<HashMap<String, String>> conceptosOrdenCompra){
                
                Font smallBoldFont = new Font(Font.getFamily("ARIAL"),7,Font.NORMAL,BaseColor.WHITE);
                Font fuenteCont = new Font(Font.getFamily("ARIAL"),7,Font.NORMAL,BaseColor.BLACK);
                Font fuentePie = new Font(Font.getFamily("ARIAL"),6,Font.NORMAL,BaseColor.BLACK);
                Font fuentenegrita = new Font(Font.getFamily("ARIAL"),7,Font.BOLD,BaseColor.BLACK);
                Font encabezado = new Font(Font.getFamily("ARIAL"),7,Font.NORMAL,BaseColor.BLACK);
                Font pedido = new Font(Font.getFamily("ARIAL"),8,Font.NORMAL,BaseColor.BLACK);
                
                double total_importe = 0;
                
                float [] tam_tamBody = {2,4.5f,1.5f,1.5f,0.5f,1.8f,0.5f,2};//Tamaño de las Columnas.
                PdfPTable tablaBody = new PdfPTable(tam_tamBody);
                PdfPCell celdaBody;
                tablaBody.setKeepTogether(false);
                
                //ESTOS SON LOS ENCABEZADOS DE LA ORDEN DE COMPRA DETALLE  MODIFIED
                celdaBody = new PdfPCell(new Paragraph("CODIGO",smallBoldFont));
                celdaBody.setUseAscender(true);
                celdaBody.setHorizontalAlignment(Element.ALIGN_CENTER);
                celdaBody.setUseDescender(true);
                celdaBody.setVerticalAlignment(Element.ALIGN_CENTER);
                celdaBody.setBackgroundColor(BaseColor.BLACK);            
                tablaBody.addCell(celdaBody);
                //2
                celdaBody = new PdfPCell(new Paragraph("DESCRIPCION-DESCRIPTION",smallBoldFont));
                celdaBody.setUseAscender(true);
                celdaBody.setHorizontalAlignment(Element.ALIGN_CENTER);
                celdaBody.setUseDescender(true);
                celdaBody.setVerticalAlignment(Element.ALIGN_CENTER);
                celdaBody.setBackgroundColor(BaseColor.BLACK);
                tablaBody.addCell(celdaBody);
                //3
                celdaBody = new PdfPCell(new Paragraph("UNIT \n U.M.",smallBoldFont));
                celdaBody.setUseAscender(true);
                celdaBody.setHorizontalAlignment(Element.ALIGN_CENTER);
                celdaBody.setUseDescender(true);
                celdaBody.setVerticalAlignment(Element.ALIGN_CENTER);
                celdaBody.setBackgroundColor(BaseColor.BLACK);
                tablaBody.addCell(celdaBody);
                //4
                celdaBody = new PdfPCell(new Paragraph("CANTIDAD \n QUANTITY",smallBoldFont));
                celdaBody.setUseAscender(true);
                celdaBody.setHorizontalAlignment(Element.ALIGN_CENTER);
                celdaBody.setUseDescender(true);
                celdaBody.setVerticalAlignment(Element.ALIGN_CENTER);
                celdaBody.setBackgroundColor(BaseColor.BLACK);
                tablaBody.addCell(celdaBody);
                //5
                celdaBody = new PdfPCell(new Paragraph("",smallBoldFont));
                celdaBody.setUseAscender(true);
                celdaBody.setHorizontalAlignment(Element.ALIGN_CENTER);
                celdaBody.setUseDescender(true);
                celdaBody.setVerticalAlignment(Element.ALIGN_CENTER);
                celdaBody.setBackgroundColor(BaseColor.BLACK);
                tablaBody.addCell(celdaBody);
                //6
                celdaBody = new PdfPCell(new Paragraph("P. UNITARIO \n UNIT PRICE",smallBoldFont));
                celdaBody.setUseAscender(true);
                celdaBody.setHorizontalAlignment(Element.ALIGN_CENTER);
                celdaBody.setUseDescender(true);
                celdaBody.setVerticalAlignment(Element.ALIGN_CENTER);
                celdaBody.setBackgroundColor(BaseColor.BLACK);
                tablaBody.addCell(celdaBody);
                //7
                celdaBody = new PdfPCell(new Paragraph("",smallBoldFont));
                celdaBody.setUseAscender(true);
                celdaBody.setHorizontalAlignment(Element.ALIGN_CENTER);
                celdaBody.setUseDescender(true);
                celdaBody.setVerticalAlignment(Element.ALIGN_CENTER);
                celdaBody.setBackgroundColor(BaseColor.BLACK);
                tablaBody.addCell(celdaBody);
                //8
                celdaBody = new PdfPCell(new Paragraph("TOTAL-IMPORTE",smallBoldFont));
                celdaBody.setUseAscender(true);
                celdaBody.setHorizontalAlignment(Element.ALIGN_CENTER);
                celdaBody.setUseDescender(true);
                celdaBody.setVerticalAlignment(Element.ALIGN_CENTER);
                celdaBody.setBackgroundColor(BaseColor.BLACK);
                tablaBody.addCell(celdaBody);
               

                //AQUI VA EL CONTENIDO DE LA TABLA. DE LA COMPRA EN DETALLE DATOS.
                for (int j=0;j<conceptosOrdenCompra.size();j++){
                    HashMap<String,String> registro = conceptosOrdenCompra.get(j);                    
                    String simbolo_mon ="";
                    simbolo_mon = registro.get("moneda_simbolo");
                    
                     //1           
                     celdaBody = new PdfPCell(new Paragraph(registro.get("articulo"),fuenteCont));
                     celdaBody.setUseAscender(true);
                     celdaBody.setHorizontalAlignment(Element.ALIGN_LEFT);
                     celdaBody.setUseDescender(true);//celdaBody.setBorder(0);
                     celdaBody.setVerticalAlignment(Element.ALIGN_CENTER);
                     tablaBody.addCell(celdaBody);
                     //2
                     celdaBody = new PdfPCell(new Paragraph(registro.get("descripcion"),fuenteCont));
                     celdaBody.setUseAscender(true);
                     celdaBody.setHorizontalAlignment(Element.ALIGN_LEFT);
                     celdaBody.setUseDescender(true);//celdaBody.setBorder(0);
                     celdaBody.setVerticalAlignment(Element.ALIGN_CENTER);
                     tablaBody.addCell(celdaBody);
          
                     //3
                     celdaBody = new PdfPCell(new Paragraph(registro.get("unit"),fuenteCont));
                     celdaBody.setUseAscender(true);
                     celdaBody.setHorizontalAlignment(Element.ALIGN_CENTER);
                     celdaBody.setUseDescender(true);//celdaBody.setBorder(0);
                     celdaBody.setVerticalAlignment(Element.ALIGN_CENTER);
                     tablaBody.addCell(celdaBody);
                   
                     //4
                     celdaBody = new PdfPCell(new Paragraph(StringHelper.AgregaComas(registro.get("cantidad")),fuenteCont));
                     celdaBody.setUseAscender(true);
                     celdaBody.setHorizontalAlignment(Element.ALIGN_RIGHT);
                     celdaBody.setUseDescender(true);//celdaBody.setBorder(0);
                     celdaBody.setVerticalAlignment(Element.ALIGN_CENTER);
                     tablaBody.addCell(celdaBody);
                     
                     //5
                     celdaBody = new PdfPCell(new Paragraph(simbolo_mon,fuenteCont));
                     celdaBody.setUseAscender(true);
                     celdaBody.setHorizontalAlignment(Element.ALIGN_RIGHT);
                     celdaBody.setUseDescender(true);//celdaBody.setBorder(0);
                     celdaBody.setVerticalAlignment(Element.ALIGN_RIGHT);
                     tablaBody.addCell(celdaBody);
                     
                     //6
                     celdaBody = new PdfPCell(new Paragraph(StringHelper.AgregaComas(StringHelper.roundDouble(registro.get("precio_unitario"),2)),fuenteCont));
                     celdaBody.setUseAscender(true);
                     celdaBody.setHorizontalAlignment(Element.ALIGN_RIGHT);
                     celdaBody.setUseDescender(true);//celdaBody.setBorder(0);
                     celdaBody.setVerticalAlignment(Element.ALIGN_CENTER);
                     tablaBody.addCell(celdaBody);
                     //7
                     celdaBody = new PdfPCell(new Paragraph(simbolo_mon,fuenteCont));
                     celdaBody.setHorizontalAlignment(Element.ALIGN_RIGHT);
                     celdaBody.setVerticalAlignment(Element.ALIGN_RIGHT);
                     tablaBody.addCell(celdaBody);
                     //8
                     total_importe = Double.parseDouble(registro.get("cantidad")) * Double.parseDouble(registro.get("precio_unitario"));
                     celdaBody = new PdfPCell(new Paragraph(StringHelper.AgregaComas(StringHelper.roundDouble(String.valueOf(total_importe),2)),fuenteCont));
                     celdaBody.setUseAscender(true);//celdaBody.setBorder(0);
                     celdaBody.setHorizontalAlignment(Element.ALIGN_RIGHT);
                     celdaBody.setUseDescender(true);
                     celdaBody.setVerticalAlignment(Element.ALIGN_RIGHT);
                     tablaBody.addCell(celdaBody);
                     
                }//termina de recorrer el array list. para detalle de compra.
                
                                
               
            //tablaBody.setSpacingAfter(10);
            
            
            return tablaBody;
            
            
            } 
            
            
            
    }
    

        static class HeaderFooter extends PdfPageEventHelper {
        protected PdfTemplate total;       
        protected BaseFont helv;  
        protected PdfContentByte cb;  
        Font largeBoldFont = new Font(Font.FontFamily.HELVETICA,10,Font.BOLD,BaseColor.BLACK);
        Font largeFont = new Font(Font.FontFamily.HELVETICA,10,Font.NORMAL,BaseColor.BLACK);
        Font smallFont = new Font(Font.FontFamily.HELVETICA,7,Font.NORMAL,BaseColor.BLACK);
        
        //ESTAS SON VARIABLES PRIVADAS DE LA CLASE, SE LE ASIGNA VALOR EN EL CONSTRUCTOR SON SETER
        private String empresa;
        private String titulo_reporte;
        private String codigo1;
        private String codigo2;
        private String direccion_empresa;
        private String codigo_p;
        private boolean mostrarPag;

        public boolean isMostrarPag() {
            return mostrarPag;
        }

        public void setMostrarPag(boolean mostrarPag) {
            this.mostrarPag = mostrarPag;
        }
        
        
        //ESTOS  SON LOS GETER Y SETTER DE LAS VARIABLES PRIVADAS DE LA CLASE
        public String getCodigo_p() {
            return codigo_p;
        }
        
        public void setCodigo_p(String codigo_p) {
            this.codigo_p = codigo_p;
        }              
        
        public String getDireccion_empresa() {
            return direccion_empresa;
        }

        public void setDireccion_empresa(String direccion_empresa) {
            this.direccion_empresa = direccion_empresa;
        }
        
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

        //ESTE ES EL CONSTRUCTOR DE LA CLASE  QUE RECIBE LOS PARAMETROS
        HeaderFooter( HashMap<String, String> datosOCompra ){
            this.setEmpresa(datosOCompra.get("empresa"));
            this.setTitulo_reporte(datosOCompra.get("titulo_reporte"));
            this.setCodigo1(datosOCompra.get("codigo1"));
            this.setCodigo2(datosOCompra.get("codigo2")); 
            this.setDireccion_empresa(datosOCompra.get("direccion_empresa"));
            this.setCodigo_p(datosOCompra.get("codigo_p"));
            this.setMostrarPag(Boolean.parseBoolean(datosOCompra.get("mostrar_pag")));
           // this.observaciones(datosOCompra.get("observaciones"));
            
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
            }
            catch(Exception e) {
                throw new ExceptionConverter(e);
            }
        }
        
        /*añadimos pie de página, borde y más propiedades*/
        @Override
        public void onEndPage(PdfWriter writer, Document document) {
          /*  ColumnText.showTextAligned(writer.getDirectContent(),Element.ALIGN_CENTER, new Phrase(this.getEmpresa(),largeBoldFont),document.getPageSize().getWidth()/2, document.getPageSize().getTop() -25, 0);
            ColumnText.showTextAligned(writer.getDirectContent(),Element.ALIGN_CENTER, new Phrase(this.getTitulo_reporte(),largeBoldFont),document.getPageSize().getWidth()/2, document.getPageSize().getTop()-38, 0);
            ColumnText.showTextAligned(writer.getDirectContent(),Element.ALIGN_CENTER, new Phrase(this.getDireccion_empresa(),largeBoldFont),document.getPageSize().getWidth()/2, document.getPageSize().getTop()-38, 0);
            ColumnText.showTextAligned(writer.getDirectContent(),Element.ALIGN_CENTER, new Phrase(this.getCodigo_p(),largeBoldFont),document.getPageSize().getWidth()/2, document.getPageSize().getTop()-38, 0);
            
//             
        //public String getObservaciones() {
            //return observaciones;
       // }
        
        //public void setObservaciones(String observaciones) {
           //this.observaciones = observaciones;
        //}       SimpleDateFormat formato = new SimpleDateFormat("'Generado el' d 'de' MMMMM 'del' yyyy 'a las' HH:mm:ss 'hrs.'");
//            String impreso_en = formato.format(new Date());
            
//            ColumnText.showTextAligned(writer.getDirectContent(),Element.ALIGN_CENTER, new Phrase(impreso_en,largeFont),document.getPageSize().getWidth()/2, document.getPageSize().getTop()-50, 0);
            */
            cb = writer.getDirectContent();
            float textBase = document.bottom() - 20;
            
            //texto inferior izquieda pie de pagina
            String text_left = this.getCodigo1();
            float text_left_Size = helv.getWidthPoint(text_left, 7);
            cb.beginText();
            cb.setFontAndSize(helv, 7);  
            cb.setTextMatrix(document.left()+85, textBase );  //definir la posicion de text
            cb.showText(text_left);
            cb.endText();
            
            
            if(this.isMostrarPag()){
                //texto centro pie de pagina
                String text_center ="" + writer.getPageNumber() + " / ";
                float text_center_Size = helv.getWidthPoint(text_center, 7);
                float pos_text_center = (document.getPageSize().getWidth()/2)-(text_center_Size/2);
                float adjust = text_center_Size + 3; 
                cb.beginText();  
                cb.setFontAndSize(helv, 7);  
                cb.setTextMatrix(pos_text_center, textBase );  //definir la posicion de text
                cb.showText(text_center);
                cb.endText();
                cb.addTemplate(total, pos_text_center + adjust, textBase);
            }
            
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
