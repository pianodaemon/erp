package com.agnux.kemikal.reportes;

import com.agnux.common.helpers.StringHelper;
import java.net.URISyntaxException;
import java.util.Iterator;
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
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.ColumnText;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
    

public class PdfInvActualizaPrecio {
    private HashMap<String, String> datosHeaderFooter = new HashMap<String, String>();
    private ArrayList<HashMap<String, String>> lista_productos = new ArrayList<HashMap<String, String>>();
    private String file_out;
    
    //porcentajes utilizados para actualizar Precios
    private String porcentajeLista1;
    private String porcentajeLista2;
    private String porcentajeLista3;
    private String porcentajeLista4;
    private String porcentajeLista5;
    private String porcentajeLista6;
    private String porcentajeLista7;
    private String porcentajeLista8;
    private String porcentajeLista9;
    private String porcentajeLista10;
    private String porcentajeDescto1;
    private String porcentajeDescto2;
    private String porcentajeDescto3;
    private String porcentajeDescto4;
    private String porcentajeDescto5;
    private String porcentajeDescto6;
    private String porcentajeDescto7;
    private String porcentajeDescto8;
    private String porcentajeDescto9;
    private String porcentajeDescto10;
    private String actualizar_descto;
    
    
    public PdfInvActualizaPrecio(HashMap<String, String> datos, HashMap<String, String> datos_actualizacion, ArrayList<HashMap<String, String>> productos, String fileout) {
        this.setFile_out(fileout);
        this.setDatosHeaderFooter(datos);
        this.setLista_productos(productos);
        this.setPorcentajeLista1(datos_actualizacion.get("lista1"));
        this.setPorcentajeLista2(datos_actualizacion.get("lista2"));
        this.setPorcentajeLista3(datos_actualizacion.get("lista3"));
        this.setPorcentajeLista4(datos_actualizacion.get("lista4"));
        this.setPorcentajeLista5(datos_actualizacion.get("lista5"));
        this.setPorcentajeLista6(datos_actualizacion.get("lista6"));
        this.setPorcentajeLista7(datos_actualizacion.get("lista7"));
        this.setPorcentajeLista8(datos_actualizacion.get("lista8"));
        this.setPorcentajeLista9(datos_actualizacion.get("lista9"));
        this.setPorcentajeLista10(datos_actualizacion.get("lista10"));
        this.setActualizar_descto(datos_actualizacion.get("actualizar_descto"));
        this.setPorcentajeDescto1(datos_actualizacion.get("descto1"));
        this.setPorcentajeDescto2(datos_actualizacion.get("descto2"));
        this.setPorcentajeDescto3(datos_actualizacion.get("descto3"));
        this.setPorcentajeDescto4(datos_actualizacion.get("descto4"));
        this.setPorcentajeDescto5(datos_actualizacion.get("descto5"));
        this.setPorcentajeDescto6(datos_actualizacion.get("descto6"));
        this.setPorcentajeDescto7(datos_actualizacion.get("descto7"));
        this.setPorcentajeDescto8(datos_actualizacion.get("descto8"));
        this.setPorcentajeDescto9(datos_actualizacion.get("descto9"));
        this.setPorcentajeDescto10(datos_actualizacion.get("descto10"));
    }
    
    
    
    public void ViewPDF() throws URISyntaxException {
        Font smallsmall = new Font(Font.FontFamily.HELVETICA,5,Font.NORMAL,BaseColor.BLACK);
        Font smallFont = new Font(Font.FontFamily.HELVETICA,7,Font.NORMAL,BaseColor.BLACK);
        Font smallBoldFont = new Font(Font.FontFamily.HELVETICA, 8, Font.BOLD, BaseColor.WHITE);
        Font smallFontBoldBlack = new Font(Font.FontFamily.HELVETICA, 8, Font.BOLD, BaseColor.BLACK);
        Font largeBoldFont = new Font(Font.FontFamily.HELVETICA,10,Font.BOLD,BaseColor.BLACK);
        
        PdfPTable tableAbreviaturas;
        PdfPCell cell;
        Iterator it;
        
        try {
            HeaderFooter event = new HeaderFooter(this.getDatosHeaderFooter());
            Document document = new Document(PageSize.LETTER.rotate(),-50,-50,60,30);
            document.addCreator("gpmarsan@gmail.com");
           // Document document =      new Document(PageSize.LETTER.rotate(), -50, -50, 60, 30);
            PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(this.getFile_out()));
            writer.setPageEvent(event);
            
            System.out.println("PDF: "+this.getFile_out());
            
            document.open();
            
            float [] widths = {
                1.2f,//codigo
                //3,//descripcion
                0.9f,//unidad
                1.6f,//presentacion
                1,//PMIN
                0.6f,//moneda Lista 1
                1,//Lista 1
                0.6f,//moneda Lista 2
                1,//Lista 2
                0.6f,//moneda Lista 3
                1,//Lista 3
                0.6f,//moneda Lista 4
                1,//Lista 4
                0.6f,//moneda Lista 5
                1,//Lista 5
                0.6f,//moneda Lista 6
                1,//Lista 6
                0.6f,//moneda Lista 7
                1,//Lista 7
                0.6f,//moneda Lista 8
                1,//Lista 8
                0.6f,//moneda Lista 9
                1,//Lista 9
                0.6f,//moneda Lista 10
                1//Lista 10
            };
            PdfPTable table = new PdfPTable(widths);
            table.setKeepTogether(false);
            if(this.getActualizar_descto().equals("true")){
                table.setHeaderRows(3);
            }else{
                table.setHeaderRows(2);
            }
            
            
            cell = new PdfPCell(new Paragraph("% para actualización de Listas", smallFontBoldBlack));
            cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            cell.setHorizontalAlignment(Element.ALIGN_LEFT);
            //cell.setBackgroundColor(BaseColor.BLACK);
            cell.setBorderWidthTop(1);
            cell.setBorderWidthBottom(0);
            cell.setBorderWidthLeft(1);
            cell.setBorderWidthRight(0);
            cell.setColspan(4);
            table.addCell(cell);
            
            cell = new PdfPCell(new Paragraph(this.getPorcentajeLista1()+"%", smallBoldFont));
            cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setBackgroundColor(BaseColor.BLACK);
            cell.setColspan(2);
            table.addCell(cell);
            
            cell = new PdfPCell(new Paragraph(this.getPorcentajeLista2()+"%", smallBoldFont));
            cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setBackgroundColor(BaseColor.BLACK);
            cell.setColspan(2);
            table.addCell(cell);
            
            cell = new PdfPCell(new Paragraph(this.getPorcentajeLista3()+"%", smallBoldFont));
            cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setBackgroundColor(BaseColor.BLACK);
            cell.setColspan(2);
            table.addCell(cell);
            
            cell = new PdfPCell(new Paragraph(this.getPorcentajeLista4()+"%", smallBoldFont));
            cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setBackgroundColor(BaseColor.BLACK);
            cell.setColspan(2);
            table.addCell(cell);
            
            cell = new PdfPCell(new Paragraph(this.getPorcentajeLista5()+"%", smallBoldFont));
            cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setBackgroundColor(BaseColor.BLACK);
            cell.setColspan(2);
            table.addCell(cell);
            
            cell = new PdfPCell(new Paragraph(this.getPorcentajeLista6()+"%", smallBoldFont));
            cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setBackgroundColor(BaseColor.BLACK);
            cell.setColspan(2);
            table.addCell(cell);
            
            cell = new PdfPCell(new Paragraph(this.getPorcentajeLista7()+"%", smallBoldFont));
            cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setBackgroundColor(BaseColor.BLACK);
            cell.setColspan(2);
            table.addCell(cell);
            
            cell = new PdfPCell(new Paragraph(this.getPorcentajeLista8()+"%", smallBoldFont));
            cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setBackgroundColor(BaseColor.BLACK);
            cell.setColspan(2);
            table.addCell(cell);
            
            cell = new PdfPCell(new Paragraph(this.getPorcentajeLista9()+"%", smallBoldFont));
            cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setBackgroundColor(BaseColor.BLACK);
            cell.setColspan(2);
            table.addCell(cell);
            
            cell = new PdfPCell(new Paragraph(this.getPorcentajeLista10()+"%", smallBoldFont));
            cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setBackgroundColor(BaseColor.BLACK);
            cell.setColspan(2);
            table.addCell(cell);
            
            if(this.getActualizar_descto().equals("true")){
                cell = new PdfPCell(new Paragraph("% para actualización de Descuentos", smallFontBoldBlack));
                cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                cell.setHorizontalAlignment(Element.ALIGN_LEFT);
                //cell.setBackgroundColor(BaseColor.BLACK);
                cell.setBorderWidthTop(0);
                cell.setBorderWidthBottom(0);
                cell.setBorderWidthLeft(1);
                cell.setBorderWidthRight(0);
                cell.setColspan(4);
                table.addCell(cell);
                
                cell = new PdfPCell(new Paragraph(this.getPorcentajeDescto1()+"%", smallBoldFont));
                cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                cell.setBackgroundColor(BaseColor.BLACK);
                cell.setColspan(2);
                table.addCell(cell);
                
                cell = new PdfPCell(new Paragraph(this.getPorcentajeDescto2()+"%", smallBoldFont));
                cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                cell.setBackgroundColor(BaseColor.BLACK);
                cell.setColspan(2);
                table.addCell(cell);
                
                cell = new PdfPCell(new Paragraph(this.getPorcentajeDescto3()+"%", smallBoldFont));
                cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                cell.setBackgroundColor(BaseColor.BLACK);
                cell.setColspan(2);
                table.addCell(cell);
                
                cell = new PdfPCell(new Paragraph(this.getPorcentajeDescto4()+"%", smallBoldFont));
                cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                cell.setBackgroundColor(BaseColor.BLACK);
                cell.setColspan(2);
                table.addCell(cell);
                
                cell = new PdfPCell(new Paragraph(this.getPorcentajeDescto5()+"%", smallBoldFont));
                cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                cell.setBackgroundColor(BaseColor.BLACK);
                cell.setColspan(2);
                table.addCell(cell);
                
                cell = new PdfPCell(new Paragraph(this.getPorcentajeDescto6()+"%", smallBoldFont));
                cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                cell.setBackgroundColor(BaseColor.BLACK);
                cell.setColspan(2);
                table.addCell(cell);
                
                cell = new PdfPCell(new Paragraph(this.getPorcentajeDescto7()+"%", smallBoldFont));
                cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                cell.setBackgroundColor(BaseColor.BLACK);
                cell.setColspan(2);
                table.addCell(cell);
                
                cell = new PdfPCell(new Paragraph(this.getPorcentajeDescto8()+"%", smallBoldFont));
                cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                cell.setBackgroundColor(BaseColor.BLACK);
                cell.setColspan(2);
                table.addCell(cell);
                
                cell = new PdfPCell(new Paragraph(this.getPorcentajeDescto9()+"%", smallBoldFont));
                cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                cell.setBackgroundColor(BaseColor.BLACK);
                cell.setColspan(2);
                table.addCell(cell);
                
                cell = new PdfPCell(new Paragraph(this.getPorcentajeDescto10()+"%", smallBoldFont));
                cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                cell.setBackgroundColor(BaseColor.BLACK);
                cell.setColspan(2);
                table.addCell(cell);
            }
            
            
            cell = new PdfPCell(new Paragraph("Código",smallBoldFont));
            cell.setUseAscender(true);
            cell.setHorizontalAlignment(Element.ALIGN_LEFT);
            cell.setUseDescender(true);
            cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            cell.setBackgroundColor(BaseColor.BLACK);
            table.addCell(cell);
            /*
            cell = new PdfPCell(new Paragraph("Descripción",smallBoldFont));
            cell.setUseAscender(true);
            cell.setHorizontalAlignment(Element.ALIGN_LEFT);
            cell.setUseDescender(true);
            cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            cell.setBackgroundColor(BaseColor.BLACK);
            table.addCell(cell);
            */
            cell = new PdfPCell(new Paragraph("UM",smallBoldFont));
            cell.setUseAscender(true);
            cell.setHorizontalAlignment(Element.ALIGN_LEFT);
            cell.setUseDescender(true);
            cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            cell.setBackgroundColor(BaseColor.BLACK);
            table.addCell(cell);
            
            cell = new PdfPCell(new Paragraph("Presentación",smallBoldFont));
            cell.setUseAscender(true);
            cell.setHorizontalAlignment(Element.ALIGN_LEFT);
            cell.setUseDescender(true);
            cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            cell.setBackgroundColor(BaseColor.BLACK);
            table.addCell(cell);
            
            cell = new PdfPCell(new Paragraph("PMIN.",smallBoldFont));
            cell.setUseAscender(true);
            cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            cell.setUseDescender(true);
            cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            cell.setBackgroundColor(BaseColor.BLACK);
            table.addCell(cell);
            
            //Lista 1
            cell = new PdfPCell(new Paragraph("Lista 1",smallBoldFont));
            cell.setUseAscender(true);
            cell.setUseDescender(true);
            cell.setBackgroundColor(BaseColor.BLACK);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setVerticalAlignment(Element.ALIGN_CENTER);
            cell.setColspan(2);
            table.addCell(cell);
            
            //Lista 2
            cell = new PdfPCell(new Paragraph("Lista 2",smallBoldFont));
            cell.setUseAscender(true);
            cell.setUseDescender(true);
            cell.setBackgroundColor(BaseColor.BLACK);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setVerticalAlignment(Element.ALIGN_CENTER);
            cell.setColspan(2);
            table.addCell(cell);
            
            //Lista 3
            cell = new PdfPCell(new Paragraph("Lista 3",smallBoldFont));
            cell.setUseAscender(true);
            cell.setUseDescender(true);
            cell.setBackgroundColor(BaseColor.BLACK);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setVerticalAlignment(Element.ALIGN_CENTER);
            cell.setColspan(2);
            table.addCell(cell);
            
            //Lista 4
            cell = new PdfPCell(new Paragraph("Lista 4",smallBoldFont));
            cell.setUseAscender(true);
            cell.setUseDescender(true);
            cell.setBackgroundColor(BaseColor.BLACK);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setVerticalAlignment(Element.ALIGN_CENTER);
            cell.setColspan(2);
            table.addCell(cell);
            
            //Lista 5
            cell = new PdfPCell(new Paragraph("Lista 5",smallBoldFont));
            cell.setUseAscender(true);
            cell.setUseDescender(true);
            cell.setBackgroundColor(BaseColor.BLACK);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setVerticalAlignment(Element.ALIGN_CENTER);
            cell.setColspan(2);
            table.addCell(cell);
            
            //Lista 6
            cell = new PdfPCell(new Paragraph("Lista 6",smallBoldFont));
            cell.setUseAscender(true);
            cell.setUseDescender(true);
            cell.setBackgroundColor(BaseColor.BLACK);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setVerticalAlignment(Element.ALIGN_CENTER);
            cell.setColspan(2);
            table.addCell(cell);
            
            //Lista 7
            cell = new PdfPCell(new Paragraph("Lista 7",smallBoldFont));
            cell.setUseAscender(true);
            cell.setUseDescender(true);
            cell.setBackgroundColor(BaseColor.BLACK);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setVerticalAlignment(Element.ALIGN_CENTER);
            cell.setColspan(2);
            table.addCell(cell);
            
            //Lista 8
            cell = new PdfPCell(new Paragraph("Lista 8",smallBoldFont));
            cell.setUseAscender(true);
            cell.setUseDescender(true);
            cell.setBackgroundColor(BaseColor.BLACK);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setVerticalAlignment(Element.ALIGN_CENTER);
            cell.setColspan(2);
            table.addCell(cell);
            
            //Lista 9
            cell = new PdfPCell(new Paragraph("Lista 9",smallBoldFont));
            cell.setUseAscender(true);
            cell.setUseDescender(true);
            cell.setBackgroundColor(BaseColor.BLACK);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setVerticalAlignment(Element.ALIGN_CENTER);
            cell.setColspan(2);
            table.addCell(cell);
            
            //Lista 10
            cell = new PdfPCell(new Paragraph("Lista 10",smallBoldFont));
            cell.setUseAscender(true);
            cell.setUseDescender(true);
            cell.setBackgroundColor(BaseColor.BLACK);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setVerticalAlignment(Element.ALIGN_CENTER);
            cell.setColspan(2);
            table.addCell(cell);
            
            it = this.getLista_productos().iterator();
            while(it.hasNext()){
                HashMap<String,String> map = (HashMap<String,String>)it.next();
                
                cell = new PdfPCell(new Paragraph(esteAtributoSeDejoNulo(map.get("codigo")), smallFont));
                cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                cell.setHorizontalAlignment(Element.ALIGN_LEFT);
                cell.setBorder(0);
                table.addCell(cell);
                /*
                String descripcion =  StringEscapeUtils.unescapeHtml(map.get("descripcion"));
                cell = new PdfPCell(new Paragraph(StringHelper.capitalizaString(descripcion), smallFont));
                cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                cell.setHorizontalAlignment(Element.ALIGN_LEFT);
                table.addCell(cell);
                */
                cell = new PdfPCell(new Paragraph(StringHelper.capitalizaString(esteAtributoSeDejoNulo(map.get("unidad"))), smallFont));
                cell.setHorizontalAlignment(Element.ALIGN_LEFT);
                cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                cell.setBorder(0);
                table.addCell(cell);
                
                cell = new PdfPCell(new Paragraph(StringHelper.capitalizaString(esteAtributoSeDejoNulo(map.get("presentacion"))), smallFont));
                cell.setHorizontalAlignment(Element.ALIGN_LEFT);
                cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                cell.setBorder(0);
                table.addCell(cell);
                
                cell = new PdfPCell(new Paragraph(StringHelper.AgregaComas(map.get("precio_minimo")), smallFont));
                cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                cell.setBorder(0);
                table.addCell(cell);
                
                cell = new PdfPCell(new Paragraph(esteAtributoSeDejoNulo(map.get("mon1")), smallFont));
                cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                cell.setBorder(0);
                table.addCell(cell);
                
                cell = new PdfPCell(new Paragraph(verificaValorCero(map.get("precio_1")), smallFont));
                cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                cell.setBorder(0);
                table.addCell(cell);
                
                cell = new PdfPCell(new Paragraph(esteAtributoSeDejoNulo(map.get("mon2")), smallFont));
                cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                cell.setBorder(0);
                table.addCell(cell);
                
                cell = new PdfPCell(new Paragraph(verificaValorCero(map.get("precio_2")), smallFont));
                cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                cell.setBorder(0);
                table.addCell(cell);
                
                cell = new PdfPCell(new Paragraph(esteAtributoSeDejoNulo(map.get("mon3")), smallFont));
                cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                cell.setBorder(0);
                table.addCell(cell);
                
                cell = new PdfPCell(new Paragraph(verificaValorCero(map.get("precio_3")), smallFont));
                cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                cell.setBorder(0);
                table.addCell(cell);
                
                cell = new PdfPCell(new Paragraph(esteAtributoSeDejoNulo(map.get("mon4")), smallFont));
                cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                cell.setBorder(0);
                table.addCell(cell);
                
                cell = new PdfPCell(new Paragraph(verificaValorCero(map.get("precio_4")), smallFont));
                cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                cell.setBorder(0);
                table.addCell(cell);
                
                cell = new PdfPCell(new Paragraph(esteAtributoSeDejoNulo(map.get("mon5")), smallFont));
                cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                cell.setBorder(0);
                table.addCell(cell);
                
                cell = new PdfPCell(new Paragraph(verificaValorCero(map.get("precio_5")), smallFont));
                cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                cell.setBorder(0);
                table.addCell(cell);
                
                cell = new PdfPCell(new Paragraph(esteAtributoSeDejoNulo(map.get("mon6")), smallFont));
                cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                cell.setBorder(0);
                table.addCell(cell);
                
                cell = new PdfPCell(new Paragraph(verificaValorCero(map.get("precio_6")), smallFont));
                cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                cell.setBorder(0);
                table.addCell(cell);
                
                cell = new PdfPCell(new Paragraph(esteAtributoSeDejoNulo(map.get("mon7")), smallFont));
                cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                cell.setBorder(0);
                table.addCell(cell);
                
                cell = new PdfPCell(new Paragraph(verificaValorCero(map.get("precio_7")), smallFont));
                cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                cell.setBorder(0);
                table.addCell(cell);
                
                cell = new PdfPCell(new Paragraph(esteAtributoSeDejoNulo(map.get("mon8")), smallFont));
                cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                cell.setBorder(0);
                table.addCell(cell);
                
                cell = new PdfPCell(new Paragraph(verificaValorCero(map.get("precio_8")), smallFont));
                cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                cell.setBorder(0);
                table.addCell(cell);
                
                cell = new PdfPCell(new Paragraph(esteAtributoSeDejoNulo(map.get("mon9")), smallFont));
                cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                cell.setBorder(0);
                table.addCell(cell);
                
                cell = new PdfPCell(new Paragraph(verificaValorCero(map.get("precio_9")), smallFont));
                cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                cell.setBorder(0);
                table.addCell(cell);
                
                cell = new PdfPCell(new Paragraph(esteAtributoSeDejoNulo(map.get("mon10")), smallFont));
                cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                cell.setBorder(0);
                table.addCell(cell);
                
                cell = new PdfPCell(new Paragraph(verificaValorCero(map.get("precio_10")), smallFont));
                cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                cell.setBorder(0);
                table.addCell(cell);
                
            } 
            
            cell = new PdfPCell(new Paragraph("", smallFont));
            cell.setBorderWidthBottom(0);
            cell.setBorderWidthTop(1);
            cell.setBorderWidthRight(0);
            cell.setBorderWidthLeft(0);
            cell.setColspan(24);
            table.addCell(cell);
            
            document.add(table);
            
            
            
            //aquí empieza la tbla de de descripción de Abreviaturas de los campos
            float [] widths3 = {0.8f,5,0.8f,5,0.8f,5,0.8f,5};
            tableAbreviaturas = new PdfPTable(widths3);
            tableAbreviaturas.setKeepTogether(true);
            int altura_fila=11;
            
            /*
            //FILA 1
            cell = new PdfPCell(new Paragraph("*  Los valores para el campo PMIN. son en M.N.",smallFont));
            cell.setBorder(0);
            cell.setColspan(8);
            cell.setFixedHeight(altura_fila);
            tableAbreviaturas.addCell(cell);
            */
            
            //FILA 2
            cell = new PdfPCell(new Paragraph("PMIN.:",smallFont));
            cell.setBorder(0);
            cell.setFixedHeight(altura_fila);
            tableAbreviaturas.addCell(cell);
            
            cell = new PdfPCell(new Paragraph("Precio Mínimo de Venta",smallFont));
            cell.setBorder(0);
            tableAbreviaturas.addCell(cell);
            
            cell = new PdfPCell(new Paragraph("",smallFont));
            cell.setBorder(0);
            tableAbreviaturas.addCell(cell);
            
            cell = new PdfPCell(new Paragraph("",smallFont));
            cell.setBorder(0);
            tableAbreviaturas.addCell(cell);
            
            cell = new PdfPCell(new Paragraph("",smallFont));
            cell.setBorder(0);
            tableAbreviaturas.addCell(cell);
            
            cell = new PdfPCell(new Paragraph("",smallFont));
            cell.setBorder(0);
            tableAbreviaturas.addCell(cell);
            
            cell = new PdfPCell(new Paragraph("",smallFont));
            cell.setBorder(0);
            tableAbreviaturas.addCell(cell);
            
            cell = new PdfPCell(new Paragraph("",smallFont));
            cell.setBorder(0);
            tableAbreviaturas.addCell(cell);
            
            
            //FILA 3
            cell = new PdfPCell(new Paragraph("UM.:",smallFont));
            cell.setBorder(0);
            cell.setFixedHeight(altura_fila);
            tableAbreviaturas.addCell(cell);
            
            cell = new PdfPCell(new Paragraph("Unidad de Medida",smallFont));
            cell.setBorder(0);
            tableAbreviaturas.addCell(cell);
            
            cell = new PdfPCell(new Paragraph("",smallFont));
            cell.setBorder(0);
            tableAbreviaturas.addCell(cell);
            
            cell = new PdfPCell(new Paragraph("",smallFont));
            cell.setBorder(0);
            tableAbreviaturas.addCell(cell);
            
            cell = new PdfPCell(new Paragraph("",smallFont));
            cell.setBorder(0);
            tableAbreviaturas.addCell(cell);
            
            cell = new PdfPCell(new Paragraph("",smallFont));
            cell.setBorder(0);
            tableAbreviaturas.addCell(cell);
            
            cell = new PdfPCell(new Paragraph("",smallFont));
            cell.setBorder(0);
            tableAbreviaturas.addCell(cell);
            
            cell = new PdfPCell(new Paragraph("",smallFont));
            cell.setBorder(0);
            tableAbreviaturas.addCell(cell);
            
            document.add(tableAbreviaturas);
            
            
            document.close();
            
            
        }
        catch (Exception e) {
                e.printStackTrace();
        }
    }
    
    
    
    public HashMap<String, String> getDatosHeaderFooter() {
        return datosHeaderFooter;
    }
    
    public void setDatosHeaderFooter(HashMap<String, String> datosHeaderFooter) {
        this.datosHeaderFooter = datosHeaderFooter;
    }
    
    public ArrayList<HashMap<String, String>> getLista_productos() {
        return lista_productos;
    }
    
    public void setLista_productos(ArrayList<HashMap<String, String>> lista_productos) {
        this.lista_productos = lista_productos;
    }
    
    public String getFile_out() {
        return file_out;
    }
    
    public void setFile_out(String file_out) {
        this.file_out = file_out;
    }
    
    public String esteAtributoSeDejoNulo(String atributo){
         return (atributo != null) ? (atributo) : new String();
    }
    
    public String verificaValorCero(String valor){
         return (!valor.equals("0.00")) ? (StringHelper.AgregaComas(valor)) : new String();
    }

    public String getActualizar_descto() {
        return actualizar_descto;
    }

    public void setActualizar_descto(String actualizar_descto) {
        this.actualizar_descto = actualizar_descto;
    }

    public String getPorcentajeDescto1() {
        return porcentajeDescto1;
    }

    public void setPorcentajeDescto1(String porcentajeDescto1) {
        this.porcentajeDescto1 = porcentajeDescto1;
    }

    public String getPorcentajeDescto10() {
        return porcentajeDescto10;
    }

    public void setPorcentajeDescto10(String porcentajeDescto10) {
        this.porcentajeDescto10 = porcentajeDescto10;
    }

    public String getPorcentajeDescto2() {
        return porcentajeDescto2;
    }

    public void setPorcentajeDescto2(String porcentajeDescto2) {
        this.porcentajeDescto2 = porcentajeDescto2;
    }

    public String getPorcentajeDescto3() {
        return porcentajeDescto3;
    }

    public void setPorcentajeDescto3(String porcentajeDescto3) {
        this.porcentajeDescto3 = porcentajeDescto3;
    }

    public String getPorcentajeDescto4() {
        return porcentajeDescto4;
    }

    public void setPorcentajeDescto4(String porcentajeDescto4) {
        this.porcentajeDescto4 = porcentajeDescto4;
    }

    public String getPorcentajeDescto5() {
        return porcentajeDescto5;
    }

    public void setPorcentajeDescto5(String porcentajeDescto5) {
        this.porcentajeDescto5 = porcentajeDescto5;
    }

    public String getPorcentajeDescto6() {
        return porcentajeDescto6;
    }

    public void setPorcentajeDescto6(String porcentajeDescto6) {
        this.porcentajeDescto6 = porcentajeDescto6;
    }

    public String getPorcentajeDescto7() {
        return porcentajeDescto7;
    }

    public void setPorcentajeDescto7(String porcentajeDescto7) {
        this.porcentajeDescto7 = porcentajeDescto7;
    }

    public String getPorcentajeDescto8() {
        return porcentajeDescto8;
    }

    public void setPorcentajeDescto8(String porcentajeDescto8) {
        this.porcentajeDescto8 = porcentajeDescto8;
    }

    public String getPorcentajeDescto9() {
        return porcentajeDescto9;
    }

    public void setPorcentajeDescto9(String porcentajeDescto9) {
        this.porcentajeDescto9 = porcentajeDescto9;
    }

    public String getPorcentajeLista1() {
        return porcentajeLista1;
    }

    public void setPorcentajeLista1(String porcentajeLista1) {
        this.porcentajeLista1 = porcentajeLista1;
    }

    public String getPorcentajeLista10() {
        return porcentajeLista10;
    }

    public void setPorcentajeLista10(String porcentajeLista10) {
        this.porcentajeLista10 = porcentajeLista10;
    }

    public String getPorcentajeLista2() {
        return porcentajeLista2;
    }

    public void setPorcentajeLista2(String porcentajeLista2) {
        this.porcentajeLista2 = porcentajeLista2;
    }

    public String getPorcentajeLista3() {
        return porcentajeLista3;
    }

    public void setPorcentajeLista3(String porcentajeLista3) {
        this.porcentajeLista3 = porcentajeLista3;
    }

    public String getPorcentajeLista4() {
        return porcentajeLista4;
    }

    public void setPorcentajeLista4(String porcentajeLista4) {
        this.porcentajeLista4 = porcentajeLista4;
    }

    public String getPorcentajeLista5() {
        return porcentajeLista5;
    }

    public void setPorcentajeLista5(String porcentajeLista5) {
        this.porcentajeLista5 = porcentajeLista5;
    }

    public String getPorcentajeLista6() {
        return porcentajeLista6;
    }

    public void setPorcentajeLista6(String porcentajeLista6) {
        this.porcentajeLista6 = porcentajeLista6;
    }

    public String getPorcentajeLista7() {
        return porcentajeLista7;
    }

    public void setPorcentajeLista7(String porcentajeLista7) {
        this.porcentajeLista7 = porcentajeLista7;
    }

    public String getPorcentajeLista8() {
        return porcentajeLista8;
    }

    public void setPorcentajeLista8(String porcentajeLista8) {
        this.porcentajeLista8 = porcentajeLista8;
    }

    public String getPorcentajeLista9() {
        return porcentajeLista9;
    }

    public void setPorcentajeLista9(String porcentajeLista9) {
        this.porcentajeLista9 = porcentajeLista9;
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
            
            ColumnText.showTextAligned(writer.getDirectContent(),Element.ALIGN_CENTER, new Phrase(this.getEmpresa(),largeBoldFont),document.getPageSize().getWidth()/2, document.getPageSize().getTop() -25, 0);
            ColumnText.showTextAligned(writer.getDirectContent(),Element.ALIGN_CENTER, new Phrase(this.getTitulo_reporte(),largeBoldFont),document.getPageSize().getWidth()/2, document.getPageSize().getTop()-38, 0);
            ColumnText.showTextAligned(writer.getDirectContent(),Element.ALIGN_CENTER, new Phrase(this.getPeriodo(),largeFont),document.getPageSize().getWidth()/2, document.getPageSize().getTop()-50, 0);
            
            
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
