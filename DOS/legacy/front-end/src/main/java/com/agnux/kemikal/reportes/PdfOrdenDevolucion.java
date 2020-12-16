package com.agnux.kemikal.reportes;


import com.agnux.common.helpers.StringHelper;
import com.itextpdf.text.Image;
import java.net.URISyntaxException;
import java.util.Iterator;
import org.apache.commons.lang.StringEscapeUtils;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfPageEventHelper;
import com.itextpdf.text.pdf.PdfTemplate;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.Element;
import com.itextpdf.text.ExceptionConverter;
import com.itextpdf.text.Font;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.pdf.BaseFont;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.HashMap;


public class PdfOrdenDevolucion {
    //--variables para pdf--
    private String imagen;
    
    private String telefono;
    //----------------------
    
    public PdfOrdenDevolucion(HashMap<String, String> datos_empresa, ArrayList<HashMap<String, String>> datos, HashMap<String, String> datos_cliente, ArrayList<HashMap<String, String>> productos, ArrayList<HashMap<String, String>> lotesgrid,String fileout, String ruta_imagen) throws URISyntaxException {
        HashMap<String, String> datosHeader = new HashMap<String, String>();
        Font smallsmall = new Font(Font.FontFamily.HELVETICA,5,Font.NORMAL,BaseColor.BLACK);
        Font smallFont = new Font(Font.FontFamily.HELVETICA,7,Font.NORMAL,BaseColor.BLACK);
        Font smallBoldFont = new Font(Font.FontFamily.HELVETICA,8,Font.BOLD,BaseColor.BLACK);
        Font largeBoldFont = new Font(Font.FontFamily.HELVETICA,10,Font.BOLD,BaseColor.BLACK);
        
        ImagenPDF ipdf = new ImagenPDF();
        CeldaPDF cepdf = new CeldaPDF();
        TablaPDF tpdf = new TablaPDF();
        
        this.setImagen(ruta_imagen);
        
        PdfPTable table;
        PdfPTable table2;
        PdfPTable tableElaboro;
        PdfPCell cell;
        String cadena;
        
        //datos para el encabezado, no se esta utilizando
        datosHeader.put("empresa", "");
        datosHeader.put("titulo_reporte", "");
        datosHeader.put("periodo", "");
        
        //datos para el pie de pagina
        datosHeader.put("codigo1", datos_empresa.get("codigo1"));
        datosHeader.put("codigo2", datos_empresa.get("codigo2"));
        
        //datos para el pie de pagina
        
        try {
            pdfEntradas.HeaderFooter event = new pdfEntradas.HeaderFooter(datosHeader);
            Document document = new Document(PageSize.LETTER.rotate(), -50, -50, 20, 30);
           // Document document =      new Document(PageSize.LETTER.rotate(), -50, -50, 60, 30);
            PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(fileout));
            writer.setPageEvent(event);
            
            document.open();
            
            float [] widths = {6,12,6};
            table = new PdfPTable(widths);
            table.setKeepTogether(false);
            
            //IMAGEN --> logo empresa
            cell = new PdfPCell(ipdf.addContent());
            cell.setBorder(0);
            //cell.setRowspan(10);
            cell.setRowspan(9);
            cell.setUseDescender(true);
            cell.setVerticalAlignment(Element.ALIGN_TOP);
            table.addCell(cell);
            
            //RAZON SOCIAL --> BeanFromCFD (X_emisor)
            cell = new PdfPCell(new Paragraph(StringHelper.capitalizaString(datos_empresa.get("emp_razon_social")),largeBoldFont));
            cell.setBorder(0);
            cell.setUseAscender(true);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            table.addCell(cell);
            
            ////////////////////////////////////////////////////////////////////////////////
            String tipo_documento = "ORDEN DE DEVOLUCIÓN";
            
            cadena = tipo_documento + "&" + datos.get(0).get("folio") + "&" + datos.get(0).get("fecha_confirmacion");
            
            cell = new PdfPCell(cepdf.addContent(cadena));
            cell.setBorder(0);
            //cell.setRowspan(13);
            cell.setRowspan(12);
            table.addCell(cell);
            
            ////////////////////////////////////////////////////////////////////////////////
            
            //celda vacia
            cell = new PdfPCell(new Paragraph(" ", smallFont));
            cell.setBorder(0);
            table.addCell(cell);
            
            cell = new PdfPCell(new Paragraph("DOMICILIO FISCAL", smallBoldFont));
            cell.setBorder(0);
            cell.setUseAscender(true);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            table.addCell(cell);
            
            cell = new PdfPCell(new Paragraph(StringHelper.capitalizaString(datos_empresa.get("emp_calle")) + " " + StringHelper.capitalizaString(datos_empresa.get("emp_no_exterior")) +  "\n" + StringHelper.capitalizaString(datos_empresa.get("emp_colonia")) + "\n" + StringHelper.capitalizaString(datos_empresa.get("emp_municipio")) + ", " + StringHelper.capitalizaString(datos_empresa.get("emp_estado"))+ ", " + StringHelper.capitalizaString(datos_empresa.get("emp_pais")) + "\nC.P. " + datos_empresa.get("emp_cp") + "    R.F.C.: " + StringHelper.capitalizaString(datos_empresa.get("emp_rfc")), smallFont));
            cell.setBorder(0);
            cell.setRowspan(6);
            cell.setUseAscender(true);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            table.addCell(cell);
            
            
            PdfPTable tableHelper = new PdfPTable(1);
            
            //CLIENTE --> texto
            cell = new PdfPCell(new Paragraph("CLIENTE",smallBoldFont));
            cell.setBorder(0);
            cell.setRightIndent(10);
            tableHelper.addCell(cell);
            
            String datos_cadena="";
            datos_cadena  = StringHelper.capitalizaString(datos_cliente.get("clie_razon_social"));
            datos_cadena+=" \n"+StringHelper.capitalizaString(datos_cliente.get("clie_rfc"));
            
            cell = new PdfPCell(new Paragraph(StringHelper.capitalizaString(datos_cadena), smallFont));
            cell.setBorder(0);
            cell.setRightIndent(10);
            cell.setFixedHeight(35);
            cell.setVerticalAlignment(Element.ALIGN_TOP);
            tableHelper.addCell(cell);
            
            cell = new PdfPCell(tableHelper);
            cell.setBorder(0);
            cell.setColspan(2);
            cell.setRowspan(3);
            table.addCell(cell);
            
            table.setSpacingAfter(10f);
            //document.addHeader("s", table.toString());
            document.add(table);
            
////////////////////////////////////////////////////////////////////////////////
            //metodo para agregar lista conceptos
            /*descomentar tambie ahorita*/
            document.add(tpdf.addContent(datos.get(0).get("moneda_simbolo"), productos,lotesgrid));
////////////////////////////////////////////////////////////////////////////////
            
                    
            table2 = new PdfPTable(1);
            table2.setKeepTogether(true);
            
            if (datos.get(0).get("observaciones").isEmpty()){
                cell = new PdfPCell(new Paragraph("",smallBoldFont));
                cell.setBorder(0);
                table2.addCell(cell);
                
                //CADENA ORIGINAL --> BeanFromCFD (getCadenaOriginal)
                
                cell = new PdfPCell(new Paragraph("", smallFont));
                cell.setBorder(0);
                table2.addCell(cell);
            }else{
                cell = new PdfPCell(new Paragraph("OBSERVACIONES:",smallBoldFont));
                cell.setBorder(0);
                table2.addCell(cell);
                
                //CADENA ORIGINAL --> BeanFromCFD (getCadenaOriginal)
                
                cell = new PdfPCell(new Paragraph(StringHelper.capitalizaString(datos.get(0).get("observaciones")), smallFont));
                cell.setBorder(0);
                table2.addCell(cell); 
            }
            
            table2.setSpacingAfter(20f);
            document.add(table2);
            

            
            
            float [] widths3 = {4,0.5f,4,0.5f,4};
            tableElaboro = new PdfPTable(widths3);
            tableElaboro.setKeepTogether(true);
            
            //FILA 1
            cell = new PdfPCell(new Paragraph("AUTORIZO",smallFont));
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setVerticalAlignment(Element.ALIGN_TOP);
            cell.setBorder(0);
            tableElaboro.addCell(cell);
            
            cell = new PdfPCell(new Paragraph("",smallFont));
            cell.setBorder(0);
            tableElaboro.addCell(cell);
            
            cell = new PdfPCell(new Paragraph("",smallFont));
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setVerticalAlignment(Element.ALIGN_TOP);
            cell.setBorder(0);
            tableElaboro.addCell(cell);
            
            cell = new PdfPCell(new Paragraph("",smallBoldFont));
            cell.setBorder(0);
            tableElaboro.addCell(cell);
            
            cell = new PdfPCell(new Paragraph("RECIBIO",smallFont));
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setVerticalAlignment(Element.ALIGN_TOP);
            cell.setBorder(0);
            tableElaboro.addCell(cell);
            
            
            
            //FILA 2
            cell = new PdfPCell(new Paragraph("",smallBoldFont));
            cell.setFixedHeight(25);
            cell.setBorderWidthBottom(1);
            cell.setBorderWidthTop(0);
            cell.setBorderWidthRight(0);
            cell.setBorderWidthLeft(0);
            tableElaboro.addCell(cell);
            
            cell = new PdfPCell(new Paragraph("",smallFont));
            cell.setFixedHeight(25);
            cell.setBorderWidthBottom(0);
            cell.setBorderWidthTop(0);
            cell.setBorderWidthRight(0);
            cell.setBorderWidthLeft(0);
            tableElaboro.addCell(cell);
            
            //centro
            cell = new PdfPCell(new Paragraph("",smallFont));
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setVerticalAlignment(Element.ALIGN_BOTTOM);
            cell.setFixedHeight(25);
            cell.setBorderWidthBottom(0);
            cell.setBorderWidthTop(0);
            cell.setBorderWidthRight(0);
            cell.setBorderWidthLeft(0);
            tableElaboro.addCell(cell);
            
            cell = new PdfPCell(new Paragraph("",smallBoldFont));
            cell.setFixedHeight(25);
            cell.setBorderWidthBottom(0);
            cell.setBorderWidthTop(0);
            cell.setBorderWidthRight(0);
            cell.setBorderWidthLeft(0);
            tableElaboro.addCell(cell);
            
            cell = new PdfPCell(new Paragraph("",smallFont));
            cell.setFixedHeight(25);
            cell.setBorderWidthBottom(1);
            cell.setBorderWidthTop(0);
            cell.setBorderWidthRight(0);
            cell.setBorderWidthLeft(0);
            tableElaboro.addCell(cell);
            
            document.add(tableElaboro);
            
            document.close();
        }
        catch (Exception e) {
                e.printStackTrace();
        }
    }
    
    
    public void setImagen(String imagen) {
    	this.imagen = imagen;
    }
    
    public String getImagen() {
    	return imagen;
    }
    
    public void setTelefono(String telefono) {
    	this.telefono = telefono;
    }
    
    public String getTelefono() {
    	return telefono;
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
    
    
    private class CeldaPDF {
        public PdfPTable addContent(String cadena) {
            Font smallFont = new Font(Font.FontFamily.HELVETICA,7,Font.NORMAL,BaseColor.BLACK);
            Font sont = new Font(Font.FontFamily.HELVETICA,8,Font.BOLD,BaseColor.BLACK);
            Font smallBoldFont = new Font(Font.FontFamily.HELVETICA, 8, Font.BOLD, BaseColor.WHITE);
            Font largeBoldFont = new Font(Font.FontFamily.HELVETICA,10,Font.BOLD,BaseColor.BLACK);
            Font smallFontCancelado = new Font(Font.FontFamily.HELVETICA,10,Font.BOLD,BaseColor.RED);
            
            String [] temp = cadena.split("&");
            PdfPTable table = new PdfPTable(1);
            PdfPCell cell;
            cell = new PdfPCell(new Paragraph(temp[0],largeBoldFont));
            cell.setBorder(0);
            cell.setUseAscender(true);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setUseDescender(true);
            cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            table.addCell(cell);
            
            //celda vacia
            cell = new PdfPCell(new Paragraph(" ", smallFont));
            cell.setBorder(0);
            table.addCell(cell);

            
            cell = new PdfPCell(new Paragraph("FOLIO",smallBoldFont));
            cell.setUseAscender(true);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setUseDescender(true);
            cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            cell.setBackgroundColor(BaseColor.BLACK);
            table.addCell(cell);
            
            cell = new PdfPCell(new Paragraph(temp[1],sont));
            //cell = new PdfPCell(new Paragraph("folio orden entrad",smallFont));
            cell.setUseAscender(true);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setUseDescender(true);
            cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            table.addCell(cell);
            
            
            //celda vacia
            cell = new PdfPCell(new Paragraph(" ", smallFont));
            cell.setBorder(1);
            table.addCell(cell);
            
            cell = new PdfPCell(new Paragraph("FECHA",smallBoldFont));
            cell.setUseAscender(true);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setUseDescender(true);
            cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            cell.setBackgroundColor(BaseColor.BLACK);
            table.addCell(cell);
            
            
            cell = new PdfPCell(new Paragraph(temp[2],smallFont));
            //cell = new PdfPCell(new Paragraph("2012-12-12",smallFont));
            cell.setUseAscender(true);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setUseDescender(true);
            cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            table.addCell(cell);
            
            //celda vacia
            cell = new PdfPCell(new Paragraph(" ", smallFont));
            cell.setBorder(1);
            table.addCell(cell);
            
            /*
            cell = new PdfPCell(new Paragraph("No. ORDEN COMPRA",smallBoldFont));
            cell.setUseAscender(true);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setUseDescender(true);
            cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            cell.setBackgroundColor(BaseColor.BLACK);
            table.addCell(cell);
            
            cell = new PdfPCell(new Paragraph(temp[3],smallFont));
            //cell = new PdfPCell(new Paragraph("1234",smallFont));
            cell.setUseAscender(true);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setUseDescender(true);
            cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            table.addCell(cell);
            */
            
            //celda vacia
            cell = new PdfPCell(new Paragraph(" ", smallFont));
            cell.setBorder(0);
            table.addCell(cell);
            return table;
        }
    }
    
    
    
    
    private class TablaPDF {
        public PdfPTable addContent(String moneda_simbolo, ArrayList<HashMap<String, String>> conceptos,ArrayList<HashMap<String, String>> lotes) {
            //suma_cantidad();
            Font small = new Font(Font.FontFamily.COURIER,6,Font.NORMAL,BaseColor.BLACK);
            Font smallFont = new Font(Font.FontFamily.HELVETICA,7,Font.NORMAL,BaseColor.BLACK);
            Font smallBoldFont = new Font(Font.FontFamily.HELVETICA, 8, Font.BOLD, BaseColor.WHITE);
            Font smallBoldFont1 = new Font(Font.FontFamily.HELVETICA, 7, Font.BOLD, BaseColor.WHITE);
            Font smallBoldFontBlack = new Font(Font.FontFamily.HELVETICA, 8, Font.BOLD, BaseColor.BLACK);
            Font largeFont = new Font(Font.FontFamily.HELVETICA,8,Font.NORMAL,BaseColor.BLACK);
            
            float [] widths = {2f, 5.5f, 2.5f,3f, 1.5f, 2f,2.5f,2.5f,2.5f,2.5f};
            PdfPTable table = new PdfPTable(widths);
            PdfPCell cell;

            Iterator it;
            Iterator lo;
            Iterator totales;
            
            table.setKeepTogether(false);
            table.setHeaderRows(1);
            
            cell = new PdfPCell(new Paragraph("CÓDIGO",smallBoldFont));
            cell.setUseAscender(true);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setUseDescender(true);
            cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            cell.setBackgroundColor(BaseColor.BLACK);
            table.addCell(cell);
            
            cell = new PdfPCell(new Paragraph("DESCRIPCIÓN",smallBoldFont));
            cell.setUseAscender(true);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setUseDescender(true);
            cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            cell.setBackgroundColor(BaseColor.BLACK);
            table.addCell(cell);
            
            
            cell = new PdfPCell(new Paragraph("UNIDAD",smallBoldFont));
            cell.setUseAscender(true);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setUseDescender(true);
            cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            cell.setBackgroundColor(BaseColor.BLACK);
            table.addCell(cell);
            
            cell = new PdfPCell(new Paragraph("PRESENTACION",smallBoldFont));
            cell.setUseAscender(true);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setUseDescender(true);
            cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            cell.setBackgroundColor(BaseColor.BLACK);
            table.addCell(cell);
            
            cell = new PdfPCell(new Paragraph("CANT. FAC.",smallBoldFont));
            cell.setUseAscender(true);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setUseDescender(true);
            cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            cell.setBackgroundColor(BaseColor.BLACK);
            table.addCell(cell);
            
            cell = new PdfPCell(new Paragraph("PRECIO U.",smallBoldFont));
            cell.setUseAscender(true);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setUseDescender(true);
            cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            cell.setBackgroundColor(BaseColor.BLACK);
            table.addCell(cell);
           
            cell = new PdfPCell(new Paragraph("IMPORTE",smallBoldFont));
            cell.setUseAscender(true);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setUseDescender(true);
            cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            cell.setBackgroundColor(BaseColor.BLACK);
            table.addCell(cell);
            
            
            cell = new PdfPCell(new Paragraph("CANT.DEV.",smallBoldFont));
            cell.setUseAscender(true);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setUseDescender(true);
            cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            cell.setBackgroundColor(BaseColor.BLACK);
            table.addCell(cell);
            
            cell = new PdfPCell(new Paragraph("PEDIMENTO",smallBoldFont));
            cell.setUseAscender(true);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setUseDescender(true);
            cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            cell.setBackgroundColor(BaseColor.BLACK);
            table.addCell(cell);
            
            cell = new PdfPCell(new Paragraph("CADUCIDAD",smallBoldFont));
            cell.setUseAscender(true);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setUseDescender(true);
            cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            cell.setBackgroundColor(BaseColor.BLACK);
            table.addCell(cell);

            
            
            it = conceptos.iterator();
           while(it.hasNext()){
                HashMap<String,String> map = (HashMap<String,String>)it.next();
                float a = 0;
                float b = 0;
                if (map.get("codigo") != null){
                    a = 0;
                    b = (float) 0.5;
                }
                cell = new PdfPCell(new Paragraph(esteAtributoSeDejoNulo(map.get("codigo")), smallFont));
                cell.setIndent(3);
                cell.setUseDescender(true);
                cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                cell.setBorderWidthBottom(a);
                cell.setBorderWidthTop(b);
                table.addCell(cell);
                
                String descripcion = map.get("nombre_producto");
                descripcion =  StringEscapeUtils.unescapeHtml(descripcion);
                cell = new PdfPCell(new Paragraph(StringHelper.capitalizaString(descripcion), smallFont));
                cell.setIndent(3);
                cell.setUseDescender(true);
                cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                cell.setBorderWidthBottom(a);
                cell.setBorderWidthTop(b);
                table.addCell(cell);
                
                cell = new PdfPCell(new Paragraph(StringHelper.capitalizaString(esteAtributoSeDejoNulo(map.get("unidad"))), smallFont));
                cell.setIndent(3);
                cell.setUseDescender(true);
                cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                cell.setBorderWidthBottom(a);
                cell.setBorderWidthTop(b);
                table.addCell(cell);

                //PRESENTACION
                cell = new PdfPCell(new Paragraph(StringHelper.capitalizaString(esteAtributoSeDejoNulo(map.get("presentacion"))), smallFont));
                cell.setIndent(3);
                cell.setUseDescender(true);
                cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                cell.setBorderWidthBottom(a);
                cell.setBorderWidthTop(b);
                table.addCell(cell);
                
                //CANTIDAD
                cell = new PdfPCell(new Paragraph(esteAtributoSeDejoNulo(map.get("cant_fac")), smallFont));
                cell.setRightIndent(3);
                cell.setUseAscender(true);
                cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                cell.setUseDescender(true);
                cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                cell.setBorderWidthBottom(a);
                cell.setBorderWidthTop(b);
                table.addCell(cell);
                
                if(map.get("precio_u")!=null){
                    if(!map.get("precio_u").equals("0.00")){
                        cell = new PdfPCell(new Paragraph(moneda_simbolo+" " + map.get("precio_u"), smallFont));
                        cell.setRightIndent(3);
                        cell.setUseAscender(true);
                        cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                        cell.setUseDescender(true);
                        cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                        cell.setBorderWidthBottom(a);
                        cell.setBorderWidthTop(b);
                        table.addCell(cell);
                    }else{
                        cell = new PdfPCell(new Paragraph(" ", smallFont));
                        cell.setRightIndent(3);
                        cell.setUseAscender(true);
                        cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                        cell.setUseDescender(true);
                        cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                        cell.setBorderWidthBottom(a);
                        cell.setBorderWidthTop(b);
                        table.addCell(cell);
                    }
                }else{
                    cell = new PdfPCell(new Paragraph(" ", smallFont));
                    cell.setRightIndent(3);
                    cell.setUseAscender(true);
                    cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                    cell.setUseDescender(true);
                    cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                    cell.setBorderWidthBottom(a);
                    cell.setBorderWidthTop(b);
                    table.addCell(cell);
                }
                
                if(map.get("importe")!=null){
                    if(!map.get("importe").equals("0.00")){
                        cell = new PdfPCell(new Paragraph(moneda_simbolo+" " + StringHelper.AgregaComas(map.get("importe")), smallFont));
                        cell.setRightIndent(3);
                        cell.setUseAscender(true);
                        cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                        cell.setUseDescender(true);
                        cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                        cell.setBorderWidthBottom(a);
                        cell.setBorderWidthTop(b);
                        table.addCell(cell);
                    }else{
                        cell = new PdfPCell(new Paragraph(" ", smallFont));
                        cell.setRightIndent(3);
                        cell.setUseAscender(true);
                        cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                        cell.setUseDescender(true);
                        cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                        cell.setBorderWidthBottom(a);
                        cell.setBorderWidthTop(b);
                        table.addCell(cell);
                    }
                }else{
                    cell = new PdfPCell(new Paragraph(" ", smallFont));
                    cell.setRightIndent(3);
                    cell.setUseAscender(true);
                    cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                    cell.setUseDescender(true);
                    cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                    cell.setBorderWidthBottom(a);
                    cell.setBorderWidthTop(b);
                    table.addCell(cell);
                 }
                
                
                cell = new PdfPCell(new Paragraph( map.get("cant_dev"), smallFont));
                        cell.setRightIndent(3);
                        cell.setUseAscender(true);
                        cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                        cell.setUseDescender(true);
                        cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                        cell.setBorderWidthBottom(a);
                        cell.setBorderWidthTop(b);
                        table.addCell(cell);
                
                cell = new PdfPCell(new Paragraph(" ", smallFont));
                        cell.setRightIndent(3);
                        cell.setUseAscender(true);
                        cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                        cell.setUseDescender(true);
                        cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                        cell.setBorderWidthBottom(a);
                        cell.setBorderWidthTop(b);
                        table.addCell(cell);
                
                
                cell = new PdfPCell(new Paragraph(" ", smallFont));
                        cell.setRightIndent(3);
                        cell.setUseAscender(true);
                        cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                        cell.setUseDescender(true);
                        cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                        cell.setBorderWidthBottom(a);
                        cell.setBorderWidthTop(b);
                        table.addCell(cell);
             
              lo = lotes.iterator();
              while(lo.hasNext()){
                    HashMap<String,String> lts = (HashMap<String,String>)lo.next();
                    if( map.get("prod_id").equals(lts.get("prod_id")) && map.get("pres_id").equals(lts.get("pres_id")) ){
                                        
                        cell = new PdfPCell(new Paragraph("", smallFont));
                        cell.setColspan(5);
                        cell.setRightIndent(2);
                        cell.setUseAscender(true);
                        cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                        cell.setUseDescender(true);
                        cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                        table.addCell(cell);
                        
                        cell = new PdfPCell(new Paragraph("Lote Int.", smallFont));
                        cell.setRightIndent(2);
                        cell.setUseAscender(true);
                        cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                        cell.setUseDescender(true);
                        cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                        table.addCell(cell);
                        
                        cell = new PdfPCell(new Paragraph(lts.get("lote_int"), smallFont));
                        //cell.setColspan(2);
                        cell.setRightIndent(3);
                        cell.setUseAscender(true);
                        cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                        cell.setUseDescender(true);
                        cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                        table.addCell(cell);

                        cell = new PdfPCell(new Paragraph(lts.get("cant_dev_lote"), smallFont));
                        cell.setRightIndent(3);
                        cell.setUseAscender(true);
                        cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                        cell.setUseDescender(true);
                        cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                        table.addCell(cell);

                        cell = new PdfPCell(new Paragraph(lts.get("pedimento"), smallFont));
                        cell.setRightIndent(3);
                        cell.setUseAscender(true);
                        cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                        cell.setUseDescender(true);
                        cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                        table.addCell(cell);
                        
                        cell = new PdfPCell(new Paragraph(lts.get("caducidad"), smallFont));
                        cell.setRightIndent(3);
                        cell.setUseAscender(true);
                        cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                        cell.setUseDescender(true);
                        cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                        table.addCell(cell);
                    }        
               }
                     
            } 
                
            
            //System.out.println("conceptos.size:"+conceptos.size());
            
            
            
            cell = new PdfPCell(new Paragraph("", smallBoldFontBlack));
            cell.setColspan(10);
            cell.setBorderWidthRight(0);
            cell.setBorderWidthLeft(0);
            cell.setBorderWidthTop(0);
            cell.setBorderWidthBottom(0);
            cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            cell.setUseDescender(true);
            cell.setVerticalAlignment(Element.ALIGN_BOTTOM);
            table.addCell(cell);           
           return table;
        }
        
    }
    
    
    
    
    public String esteAtributoSeDejoNulo(String atributo){
         return (atributo != null) ? (atributo) : new String();
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
            }
            catch(Exception e) {
                throw new ExceptionConverter(e);
            }
        }
        
        /*añadimos pie de página, borde y más propiedades*/
        @Override
        public void onEndPage(PdfWriter writer, Document document) {
            
            //ColumnText.showTextAligned(writer.getDirectContent(),Element.ALIGN_CENTER, new Phrase(this.getEmpresa(),largeBoldFont),document.getPageSize().getWidth()/2, document.getPageSize().getTop() -25, 0);
            //ColumnText.showTextAligned(writer.getDirectContent(),Element.ALIGN_CENTER, new Phrase(this.getTitulo_reporte(),largeBoldFont),document.getPageSize().getWidth()/2, document.getPageSize().getTop()-38, 0);
            //ColumnText.showTextAligned(writer.getDirectContent(),Element.ALIGN_CENTER, new Phrase(this.getPeriodo(),largeFont),document.getPageSize().getWidth()/2, document.getPageSize().getTop()-50, 0);
            
            
            cb = writer.getDirectContent();
            float textBase = document.bottom() - 15;
            
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
        
        /*aqui escrimos ls paginas totales, para que nos salga de pie de pagina Pagina x de y*/
        @Override
        public void onCloseDocument(PdfWriter writer, Document document) {
          total.beginText();  
          total.setFontAndSize(helv, 7);  
          total.setTextMatrix(0,0);                                           
          total.showText(String.valueOf(writer.getPageNumber() -1));  
          total.endText();  
        }
   }//termina clase HeaderFooter
}
