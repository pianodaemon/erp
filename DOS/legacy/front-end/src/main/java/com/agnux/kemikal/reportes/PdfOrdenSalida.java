/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.agnux.kemikal.reportes;

import com.agnux.common.helpers.StringHelper;
import com.itextpdf.text.Image;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfStamper;
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

/**
 *  mi_compu 
 * @author Vale s.s
 * valentin.vale8490@gmail.com
 */
public class PdfOrdenSalida {
    //--variables para pdf--
    private String imagen;
    
    private String telefono;
    //----------------------
    
    public PdfOrdenSalida(HashMap<String, String> datos_empresa, HashMap<String, String> datos_salida, HashMap<String, String> datos_cliente, ArrayList<HashMap<String, String>> lista_productos, ArrayList<HashMap<String, String>> lotesgrid,String fileout, String ruta_imagen) throws URISyntaxException {
        HashMap<String, String> datos = new HashMap<String, String>();
        Font smallsmall = new Font(Font.FontFamily.HELVETICA,5,Font.NORMAL,BaseColor.BLACK);
        Font smallFont = new Font(Font.FontFamily.HELVETICA,7,Font.NORMAL,BaseColor.BLACK);
        Font smallBoldFont = new Font(Font.FontFamily.HELVETICA,8,Font.BOLD,BaseColor.BLACK);
        Font largeBoldFont = new Font(Font.FontFamily.HELVETICA,10,Font.BOLD,BaseColor.BLACK);
        
        PdfOrdenSalida.ImagenPDF ipdf = new PdfOrdenSalida.ImagenPDF();
        PdfOrdenSalida.CeldaPDF cepdf = new PdfOrdenSalida.CeldaPDF();
        PdfOrdenSalida.TablaPDF tpdf = new PdfOrdenSalida.TablaPDF();
        
        this.setImagen(ruta_imagen);
        //this.setImagen(Ri);
        
        //this.setTelefono(telefono);
        PdfPTable table;
        PdfPTable table2;
        PdfPTable tableElaboro;
        PdfPCell cell;
        String cadena;
        
        //datos para el encabezado, no se esta utilizando
        datos.put("empresa", "");
        datos.put("titulo_reporte", "");
        datos.put("periodo", "");
        
        //datos para el pie de pagina
        datos.put("codigo1", datos_empresa.get("codigo1"));
        datos.put("codigo2", datos_empresa.get("codigo2"));
        
        //datos para el pie de pagina
        
        try {
            pdfEntradas.HeaderFooter event = new pdfEntradas.HeaderFooter(datos);
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
            String tipo_documento = "ORDEN DE SALIDA";
            
            cadena = tipo_documento + "&" + //ORDEN DE SALIDA
                    datos_salida.get("folio") + "&" +  //2
                    datos_salida.get("fecha_expedicion")+ "&" + 
                    datos_salida.get("orden_compra");
            
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
            
            //DOMICILIO FISCAL --> texto
            cell = new PdfPCell(new Paragraph("DOMICILIO FISCAL", smallBoldFont));
            cell.setBorder(0);
            cell.setUseAscender(true);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            table.addCell(cell);
            
            
            //DOMICILIO FISCAL --> BeanFromCFD (X_emisor, X_domicilio_fiscal)
            cell = new PdfPCell(new Paragraph(StringHelper.capitalizaString(datos_empresa.get("emp_calle")) + " " + StringHelper.capitalizaString(datos_empresa.get("emp_no_exterior")) +  "\n" + StringHelper.capitalizaString(datos_empresa.get("emp_colonia")) + "\n" + StringHelper.capitalizaString(datos_empresa.get("emp_municipio")) + ", " + StringHelper.capitalizaString(datos_empresa.get("emp_estado"))+ ", " + StringHelper.capitalizaString(datos_empresa.get("emp_pais")) + "\nC.P. " + datos_empresa.get("emp_cp") + "    R.F.C.: " + StringHelper.capitalizaString(datos_empresa.get("emp_rfc")), smallFont));
            cell.setBorder(0);
            cell.setRowspan(6);
            cell.setUseAscender(true);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            table.addCell(cell);
            
            
            PdfPTable tableHelper = new PdfPTable(1);
            
            //CLIENTE --> texto
            cell = new PdfPCell(new Paragraph(datos_salida.get("origen"),smallBoldFont));
            cell.setBorder(0);
            cell.setRightIndent(10);
            tableHelper.addCell(cell);
            
            String datos_cadena="";
            
            if(datos_salida.get("origen").equals("CLIENTE")){
                datos_cadena  = StringHelper.capitalizaString(datos_cliente.get("clie_razon_social"));
                datos_cadena+=" \n"+StringHelper.capitalizaString(datos_cliente.get("clie_calle")) +" "+ datos_cliente.get("clie_numero") + ", " + StringHelper.capitalizaString(datos_cliente.get("clie_colonia"))+ ", " + StringHelper.capitalizaString(datos_cliente.get("clie_municipio")) + ", " + StringHelper.capitalizaString(datos_cliente.get("clie_estado")) + ", " + StringHelper.capitalizaString(datos_cliente.get("clie_pais")) + " \nC.P. " + datos_cliente.get("clie_cp") + "     TEL. "+ datos_cliente.get("clie_telefono") +  "\nR.F.C.: " + StringHelper.capitalizaString(datos_cliente.get("clie_rfc"));
            }else{
                datos_cadena=datos_salida.get("proveedor_tipo_movimiento");
            }
            
            cell = new PdfPCell(new Paragraph(StringHelper.capitalizaString(datos_cadena), smallFont));
            cell.setBorder(0);
            cell.setRightIndent(10);
            cell.setFixedHeight(35);
            cell.setVerticalAlignment(Element.ALIGN_TOP);
            tableHelper.addCell(cell);
            
            cell = new PdfPCell(new Paragraph("",smallBoldFont));
            cell.setBorder(0);
            cell.setRightIndent(10);
            tableHelper.addCell(cell);
            
            
            //cell = new PdfPCell(new Paragraph(StringHelper.capitalizaString(datos_cliente.get("clie_contacto")),smallFont));
            cell = new PdfPCell(new Paragraph("",smallBoldFont));
            cell.setBorder(0);
            cell.setRightIndent(10);
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
            //metodo para agregar lista conceptos --> BeanFromCFD
            /*descomentar tambie ahorita*/
            document.add(tpdf.addContent(datos_salida, lista_productos,lotesgrid));   // ???????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????
            //document.add(tpdf.addContent(daoEntradas.getListaConceptos(), daoEntradas.getSubtotal(), daoEntradas.getImpuesto(), daoEntradas.getTotal(), daoEntradas.getMoneda_id(), daoEntradas.getMoneda()));
            //(ArrayList<LinkedHashMap<String,String>> conceptos, String subTotal,String impuesto, String total, String moneda)
////////////////////////////////////////////////////////////////////////////////
            
                    
            table2 = new PdfPTable(1);
            table2.setKeepTogether(true);
            
            if (datos_salida.get("observaciones").isEmpty()){
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
                
                cell = new PdfPCell(new Paragraph(StringHelper.capitalizaString(datos_salida.get("observaciones")), smallFont));
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
            
            /*
            PdfReader reader = new PdfReader(fileout);
            
            PdfStamper stamper = new PdfStamper(reader,new FileOutputStream(fileout+".pdf"));
            PdfContentByte over;
            int total = reader.getNumberOfPages() + 1;
            for (int i=1; i<total; i++) {
                over = stamper.getOverContent(i);
                PdfTemplate f = over.createAppearance(500,200);
                f.setBoundingBox(table.getDefaultCell());
                over.addTemplate(f, 70,770);
                over.beginText();
                over.setFontAndSize(BaseFont.createFont(BaseFont.HELVETICA, BaseFont.WINANSI, BaseFont.NOT_EMBEDDED), 7);
                over.showTextAligned(PdfContentByte.ALIGN_CENTER, "PAG. " + i + " DE " + (total-1), 570, 773, 0);
                over.endText();
               
            }
            stamper.close();
            reader.close();
            */
            
            
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
            
//              0      cadena = tipo_documento + "&" + 
//              1      datos_salida.get("folio_Orden_Entrada") + "&" + 
//              2      StringHelper.capitalizaString(datos_empresa.get("emp_municipio")) + ", " +
//              3      StringHelper.capitalizaString(datos_empresa.get("emp_estado")) + "\n" +
//              4      datos_salida.get("fecha_ordenentrada")+ "&" + 
//              5      StringHelper.capitalizaString(datos_salida.get("orden_compra"));
            String [] temp = cadena.split("&");
            PdfPTable table = new PdfPTable(1);
            PdfPCell cell;
            //System.out.println("Esta es la cadena: "+cadena);
            //[0]   ORDEN DE SALIDA
            //[1]   9
            //[2]   2012/12/12
            //[3]   4
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

            
            cell = new PdfPCell(new Paragraph("FOLIO ORDEN SALIDA",smallBoldFont));
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
            
            cell = new PdfPCell(new Paragraph("FECHA EXPEDICION",smallBoldFont));
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
            
            //celda vacia
            cell = new PdfPCell(new Paragraph(" ", smallFont));
            cell.setBorder(0);
            table.addCell(cell);
            
           
            
            //celda vacia
            cell = new PdfPCell(new Paragraph(" ", smallFont));
            cell.setBorder(0);
            table.addCell(cell);
            
            return table;
        }
    }
    
    
    
    private class TablaPDF {
        public PdfPTable addContent(HashMap<String, String> datos_salida,ArrayList<HashMap<String, String>> conceptos,ArrayList<HashMap<String, String>> lotes) {
            
            String subTotal="0";
            String impuesto="0";
            String total="0";
            String moneda_i="0"; 
            String moneda="0";
            
            String denominacion = "";
            String denom = "";
            String simbolo_moneda="";
            if(datos_salida.get("moneda_id").equals("1")){
                denominacion = "pesos";
                denom = "M.N.";
                simbolo_moneda="$";
            }
            if(datos_salida.get("moneda_id").equals("2")){
                denominacion = "dolares";
                //denom = "USCY";
                denom = "USD";
                simbolo_moneda="USD";
            }
            
            //suma_cantidad();
            Font small = new Font(Font.FontFamily.COURIER,6,Font.NORMAL,BaseColor.BLACK);
            //Font smallFont = new Font(Font.FontFamily.COURIER,8,Font.NORMAL,BaseColor.BLACK);
            Font smallFont = new Font(Font.FontFamily.HELVETICA,7,Font.NORMAL,BaseColor.BLACK);
            Font smallBoldFont = new Font(Font.FontFamily.HELVETICA, 8, Font.BOLD, BaseColor.WHITE);
            Font smallBoldFont1 = new Font(Font.FontFamily.HELVETICA, 7, Font.BOLD, BaseColor.WHITE);
            Font smallBoldFontBlack = new Font(Font.FontFamily.HELVETICA, 8, Font.BOLD, BaseColor.BLACK);
            Font largeFont = new Font(Font.FontFamily.HELVETICA,8,Font.NORMAL,BaseColor.BLACK);
            
            
            //float [] widths = {2f, 5.5f, 3f, 2f, 1.5f, 2f,2f,2f,2f};
            float [] widths = {2f, 5.5f, 2.5f,3f, 1.5f, 2f,2.5f,2.5f,2.5f,2.5f};
            PdfPTable table = new PdfPTable(widths);
            PdfPCell cell;

            Iterator it;
            Iterator lo;
            Iterator totales;
            
            table.setKeepTogether(false);
            table.setHeaderRows(1);
            
            cell = new PdfPCell(new Paragraph("CODIGO",smallBoldFont));
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
            
            cell = new PdfPCell(new Paragraph("CANT. FAC",smallBoldFont));
            cell.setUseAscender(true);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setUseDescender(true);
            cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            cell.setBackgroundColor(BaseColor.BLACK);
            table.addCell(cell);
            
            cell = new PdfPCell(new Paragraph("COSTO.U",smallBoldFont));
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
            
            
            cell = new PdfPCell(new Paragraph("CANT.SURT.",smallBoldFont));
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
                
                //if(map.get("titulo") !=null){
                    String descripcion = map.get("titulo");
                    descripcion =  StringEscapeUtils.unescapeHtml(descripcion);
                    cell = new PdfPCell(new Paragraph(StringHelper.capitalizaString(descripcion), smallFont));
                    cell.setIndent(3);
                    cell.setUseDescender(true);
                    cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                    cell.setBorderWidthBottom(a);
                    cell.setBorderWidthTop(b);
                    table.addCell(cell);
                //}
                
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
                
                if(map.get("precio_unitario")!=null){
                    if(!map.get("precio_unitario").equals("0.00")){
                        cell = new PdfPCell(new Paragraph(simbolo_moneda+" " + map.get("precio_unitario"), smallFont));
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
                        cell = new PdfPCell(new Paragraph(simbolo_moneda+" " + StringHelper.AgregaComas(map.get("importe")), smallFont));
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
                
                
                cell = new PdfPCell(new Paragraph( map.get("cant_sur"), smallFont));
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
//                    System.out.println("Valores a evaluar::"+map.get("id_detalle_osal") + lts.get("id_osal_detalle"));
                    if(map.get("id_detalle_osal").equals(lts.get("id_osal_detalle"))){
                                        
                        cell = new PdfPCell(new Paragraph("", smallFont));
                        cell.setColspan(3);
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
                        cell.setColspan(3);
                        cell.setRightIndent(3);
                        cell.setUseAscender(true);
                        cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                        cell.setUseDescender(true);
                        cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                        table.addCell(cell);

                        cell = new PdfPCell(new Paragraph(lts.get("cantidad_sal"), smallFont));
                        cell.setRightIndent(3);
                        cell.setUseAscender(true);
                        cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                        cell.setUseDescender(true);
                        cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                        table.addCell(cell);

                        cell = new PdfPCell(new Paragraph(lts.get("ped_lote"), smallFont));
                        cell.setRightIndent(3);
                        cell.setUseAscender(true);
                        cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                        cell.setUseDescender(true);
                        cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                        table.addCell(cell);


                        cell = new PdfPCell(new Paragraph(lts.get("cad_lote"), smallFont));
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

            cell = new PdfPCell(new Paragraph("SUB-TOTAL  "+simbolo_moneda, smallBoldFontBlack));
            cell.setColspan(9);
            cell.setBorderWidthRight(0);
            cell.setBorderWidthLeft(0);
            cell.setBorderWidthTop(0);
            cell.setBorderWidthBottom(0);
            cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            cell.setUseDescender(true);
            cell.setVerticalAlignment(Element.ALIGN_BOTTOM);
            table.addCell(cell);

            cell = new PdfPCell(new Paragraph(StringHelper.AgregaComas(datos_salida.get("subtotal")), smallBoldFontBlack));
            cell.setBorderWidthRight(0);
            cell.setBorderWidthLeft(0);
            cell.setBorderWidthTop(0);
            cell.setBorderWidthBottom(0);
            cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            cell.setUseDescender(true);
            cell.setVerticalAlignment(Element.ALIGN_BOTTOM);
            table.addCell(cell);
            
            
            if(Double.parseDouble(datos_salida.get("monto_ieps"))>0){
                //FILA IEPS
                cell = new PdfPCell(new Paragraph("IEPS  "+simbolo_moneda,smallBoldFontBlack));
                cell.setColspan(9);
                cell.setBorderWidthRight(0);
                cell.setBorderWidthLeft(0);
                cell.setBorderWidthTop(0);
                cell.setBorderWidthBottom(0);
                cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                cell.setUseDescender(true);
                cell.setVerticalAlignment(Element.ALIGN_BOTTOM);
                table.addCell(cell);
                
                cell = new PdfPCell(new Paragraph(StringHelper.AgregaComas(datos_salida.get("monto_ieps")), smallBoldFontBlack));
                cell.setBorderWidthRight(0);
                cell.setBorderWidthLeft(0);
                cell.setBorderWidthTop(0);
                cell.setBorderWidthBottom(0);
                cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                cell.setUseDescender(true);
                cell.setVerticalAlignment(Element.ALIGN_BOTTOM);
                table.addCell(cell);    
            }
            
            
            //FILA IVA
            cell = new PdfPCell(new Paragraph("IVA  "+simbolo_moneda,smallBoldFontBlack));
            cell.setColspan(9);
            cell.setBorderWidthRight(0);
            cell.setBorderWidthLeft(0);
            cell.setBorderWidthTop(0);
            cell.setBorderWidthBottom(0);
            cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            cell.setUseDescender(true);
            cell.setVerticalAlignment(Element.ALIGN_BOTTOM);
            table.addCell(cell);


            cell = new PdfPCell(new Paragraph(StringHelper.AgregaComas(datos_salida.get("iva")), smallBoldFontBlack));
            cell.setBorderWidthRight(0);
            cell.setBorderWidthLeft(0);
            cell.setBorderWidthTop(0);
            cell.setBorderWidthBottom(0);
            cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            cell.setUseDescender(true);
            cell.setVerticalAlignment(Element.ALIGN_BOTTOM);
            table.addCell(cell);
            
            
            if(Double.parseDouble(datos_salida.get("retencion"))>0){
                cell = new PdfPCell(new Paragraph("RETENCIÓN  "+simbolo_moneda,smallBoldFontBlack));
                cell.setColspan(9);
                cell.setBorderWidthRight(0);
                cell.setBorderWidthLeft(0);
                cell.setBorderWidthTop(0);
                cell.setBorderWidthBottom(0);
                cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                cell.setUseDescender(true);
                cell.setVerticalAlignment(Element.ALIGN_BOTTOM);
                table.addCell(cell);
                
                cell = new PdfPCell(new Paragraph(StringHelper.AgregaComas(datos_salida.get("retencion")), smallBoldFontBlack));
                cell.setBorderWidthRight(0);
                cell.setBorderWidthLeft(0);
                cell.setBorderWidthTop(0);
                cell.setBorderWidthBottom(0);
                cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                cell.setUseDescender(true);
                cell.setVerticalAlignment(Element.ALIGN_BOTTOM);
                table.addCell(cell);
            }
            
            
            cell = new PdfPCell(new Paragraph("TOTAL  "+simbolo_moneda,smallBoldFontBlack));
            cell.setColspan(9);
            cell.setBorderWidthRight(0);
            cell.setBorderWidthLeft(0);
            cell.setBorderWidthTop(0);
            cell.setBorderWidthBottom(0);
            cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            cell.setUseDescender(true);
            cell.setVerticalAlignment(Element.ALIGN_BOTTOM);
            table.addCell(cell);
            
            cell = new PdfPCell(new Paragraph(StringHelper.AgregaComas(datos_salida.get("total")), smallBoldFontBlack));
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
