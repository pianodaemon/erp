/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
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
    
    
/**
 *
 * @author Aaron Cabello
 * acabello@agnux.com
 * 05/Junio/2013
 * 
 */
public final class PdfRequisicion {
    private HashMap<String, String> datosHeaderFooter = new HashMap<String, String>();
    private ArrayList<HashMap<String, String>> lista_productos = new ArrayList<HashMap<String, String>>();
    private String fileout;
    private String ruta_imagen;
    private String emp_razon_social;
    private String emp_no_exterior;
    private String emp_calle;
    private String emp_colonia;
    private String emp_municipio;
    private String emp_estado;
    private String emp_pais;
    private String emp_cp;
    private String emp_rfc;
    
    private String tipo_documento;
    private String folio_requisicion;
    private String fecha_requisicion;
    private String fecha_compromiso;

    
   // private String almacenDestino;
    private String observaciones;
    private String usuario_elaboracion;

   
    public String getFecha_compromiso() {
        return fecha_compromiso;
    }

    public void setFecha_compromiso(String fecha_compromiso) {
        this.fecha_compromiso = fecha_compromiso;
    }

    public HashMap<String, String> getDatosHeaderFooter() {
        return datosHeaderFooter;
    }

    public void setDatosHeaderFooter(HashMap<String, String> datosHeaderFooter) {
        this.datosHeaderFooter = datosHeaderFooter;
    }

    public String getEmp_calle() {
        return emp_calle;
    }

    public void setEmp_calle(String emp_calle) {
        this.emp_calle = emp_calle;
    }

    public String getEmp_colonia() {
        return emp_colonia;
    }

    public void setEmp_colonia(String emp_colonia) {
        this.emp_colonia = emp_colonia;
    }

    public String getEmp_cp() {
        return emp_cp;
    }

    public void setEmp_cp(String emp_cp) {
        this.emp_cp = emp_cp;
    }

    public String getEmp_estado() {
        return emp_estado;
    }

    public void setEmp_estado(String emp_estado) {
        this.emp_estado = emp_estado;
    }

    public String getEmp_municipio() {
        return emp_municipio;
    }

    public void setEmp_municipio(String emp_municipio) {
        this.emp_municipio = emp_municipio;
    }

    public String getEmp_no_exterior() {
        return emp_no_exterior;
    }

    public void setEmp_no_exterior(String emp_no_exterior) {
        this.emp_no_exterior = emp_no_exterior;
    }

    public String getEmp_pais() {
        return emp_pais;
    }

    public void setEmp_pais(String emp_pais) {
        this.emp_pais = emp_pais;
    }

    public String getEmp_razon_social() {
        return emp_razon_social;
    }

    public void setEmp_razon_social(String emp_razon_social) {
        this.emp_razon_social = emp_razon_social;
    }

    public String getEmp_rfc() {
        return emp_rfc;
    }

    public void setEmp_rfc(String emp_rfc) {
        this.emp_rfc = emp_rfc;
    }

    public String getFecha_requisicion() {
        return fecha_requisicion;
    }

    public void setFecha_requisicion(String fecha_requisicion) {
        this.fecha_requisicion = fecha_requisicion;
    }

    public String getFileout() {
        return fileout;
    }

    public void setFileout(String fileout) {
        this.fileout = fileout;
    }

    public String getFolio_requisicion() {
        return folio_requisicion;
    }

    public void setFolio_requisicion(String folio_requisicion) {
        this.folio_requisicion = folio_requisicion;
    }

    public ArrayList<HashMap<String, String>> getLista_productos() {
        return lista_productos;
    }

    public void setLista_productos(ArrayList<HashMap<String, String>> lista_productos) {
        this.lista_productos = lista_productos;
    }

    public String getObservaciones() {
        return observaciones;
    }

    public void setObservaciones(String observaciones) {
        this.observaciones = observaciones;
    }

    public String getRuta_imagen() {
        return ruta_imagen;
    }

    public void setRuta_imagen(String ruta_imagen) {
        this.ruta_imagen = ruta_imagen;
    }

    public String getTipo_documento() {
        return tipo_documento;
    }

    public void setTipo_documento(String tipo_documento) {
        this.tipo_documento = tipo_documento;
    }

    public String getUsuario_elaboracion() {
        return usuario_elaboracion;
    }

    public void setUsuario_elaboracion(String usuario_elaboracion) {
        this.usuario_elaboracion = usuario_elaboracion;
    }
    

    public PdfRequisicion(HashMap<String, String> datos_empresa, HashMap<String, String> datos_requisicion, ArrayList<HashMap<String, String>> lista_productos, String fileout, String ruta_imagen) {
        HashMap<String, String> datos = new HashMap<String, String>();
        this.setFileout(fileout);
        this.setRuta_imagen(ruta_imagen);
        this.setEmp_calle(datos_empresa.get("emp_calle"));
        this.setEmp_colonia(datos_empresa.get("emp_colonia"));
        this.setEmp_cp(datos_empresa.get("emp_cp"));
        this.setEmp_estado(datos_empresa.get("emp_estado"));
        this.setEmp_municipio(datos_empresa.get("emp_municipio"));
        this.setEmp_no_exterior(datos_empresa.get("emp_no_exterior"));
        this.setEmp_pais(datos_empresa.get("emp_pais"));
        this.setEmp_razon_social(datos_empresa.get("emp_razon_social"));
        this.setEmp_rfc(datos_empresa.get("emp_rfc"));
        
        this.setTipo_documento("REQUISICION");
        this.setFolio_requisicion(datos_requisicion.get("folio"));
        this.setFecha_requisicion(datos_requisicion.get("fecha_requisicion"));
        this.setFecha_compromiso(datos_requisicion.get("fecha_compromiso"));
       
        this.setObservaciones(datos_requisicion.get("observaciones"));
        this.setUsuario_elaboracion(datos_requisicion.get("nombre_usuario"));
        this.setLista_productos(lista_productos);
        
        
        //datos para el encabezado, no se esta utilizando
        datos.put("empresa", "");
        datos.put("titulo_reporte", "");
        datos.put("periodo", "");
        
        //datos para el pie de pagina
        datos.put("codigo1", datos_requisicion.get("codigo1"));
        datos.put("codigo2", datos_requisicion.get("codigo2"));
        
        this.setDatosHeaderFooter(datos);
    }
    
    public void ViewPDF() throws URISyntaxException {
        HashMap<String, String> datos = new HashMap<String, String>();
        Font smallsmall = new Font(Font.FontFamily.HELVETICA,5,Font.NORMAL,BaseColor.BLACK);
        Font smallFont = new Font(Font.FontFamily.HELVETICA,7,Font.NORMAL,BaseColor.BLACK);
        Font smallFontBold = new Font(Font.FontFamily.HELVETICA,7,Font.BOLD,BaseColor.BLACK);
        Font smallBoldFont = new Font(Font.FontFamily.HELVETICA,8,Font.BOLD,BaseColor.BLACK);
        Font largeBoldFont = new Font(Font.FontFamily.HELVETICA,10,Font.BOLD,BaseColor.BLACK);
        
        ImagenPDF ipdf = new ImagenPDF();
        CeldaPDF cepdf = new CeldaPDF();
        TablaPDF tablaConceptos = new TablaPDF();
        
        PdfPTable tablaPrincipal;
        PdfPTable table2;
        PdfPTable tableElaboro;
        PdfPCell cell;
        
        try {
            HeaderFooter event = new HeaderFooter(this.getDatosHeaderFooter());
            Document document = new Document(PageSize.LETTER, -50, -50, 20, 30);
           // Document document =      new Document(PageSize.LETTER.rotate(), -50, -50, 60, 30);
            PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(this.getFileout()));
            writer.setPageEvent(event);
            
            System.out.println("PDF: "+this.getFileout());
            
            document.open();
            
            float [] widths = {6,12,6};
            tablaPrincipal = new PdfPTable(widths);
            tablaPrincipal.setKeepTogether(false);
            
            //IMAGEN --> logo empresa
            cell = new PdfPCell(ipdf.addContent());
            cell.setBorderWidthBottom(0);
            cell.setBorderWidthTop(0);
            cell.setBorderWidthRight(0);
            cell.setBorderWidthLeft(0);
            cell.setUseDescender(true);
            cell.setVerticalAlignment(Element.ALIGN_TOP);
            tablaPrincipal.addCell(cell);
            
            
            
            //AQUI COMIENZA LA TABLA PARA DATOS DE LA EMPRESA
            PdfPTable tableDatosEmpresa = new PdfPTable(1);
            PdfPCell cellEmp;
            
            //RAZON SOCIAL --> BeanFromCFD (X_emisor)
            cellEmp = new PdfPCell(new Paragraph(StringHelper.capitalizaString(this.getEmp_razon_social()),largeBoldFont));
            cellEmp.setBorderWidthBottom(0);
            cellEmp.setBorderWidthTop(0);
            cellEmp.setBorderWidthRight(0);
            cellEmp.setBorderWidthLeft(0);
            cellEmp.setUseAscender(true);
            cellEmp.setHorizontalAlignment(Element.ALIGN_CENTER);
            tableDatosEmpresa.addCell(cellEmp);
            
            //celda vacia
            cellEmp = new PdfPCell(new Paragraph(" ", smallFont));
            cellEmp.setBorderWidthBottom(0);
            cellEmp.setBorderWidthTop(0);
            cellEmp.setBorderWidthRight(0);
            cellEmp.setBorderWidthLeft(0);
            tableDatosEmpresa.addCell(cellEmp);
            
            //DOMICILIO FISCAL --> texto
            cellEmp = new PdfPCell(new Paragraph("DOMICILIO FISCAL", smallBoldFont));
            cellEmp.setBorderWidthBottom(0);
            cellEmp.setBorderWidthTop(0);
            cellEmp.setBorderWidthRight(0);
            cellEmp.setBorderWidthLeft(0);
            cellEmp.setUseAscender(true);
            cellEmp.setHorizontalAlignment(Element.ALIGN_CENTER);
            tableDatosEmpresa.addCell(cellEmp);
            
            
            cellEmp = new PdfPCell(new Paragraph(StringHelper.capitalizaString(this.getEmp_calle()) + " " + StringHelper.capitalizaString(this.getEmp_no_exterior()) +  "\n" + StringHelper.capitalizaString(this.getEmp_colonia()) + "\n" + StringHelper.capitalizaString(this.getEmp_municipio()) + ", " + StringHelper.capitalizaString(this.getEmp_estado())+ ", " + StringHelper.capitalizaString(this.getEmp_pais()) + "\nC.P. " + this.getEmp_cp() + "    R.F.C.: " + StringHelper.capitalizaString(this.getEmp_rfc()), smallFont));
            cellEmp.setBorderWidthBottom(0);
            cellEmp.setBorderWidthTop(0);
            cellEmp.setBorderWidthRight(0);
            cellEmp.setBorderWidthLeft(0);
            cellEmp.setUseAscender(true);
            cellEmp.setHorizontalAlignment(Element.ALIGN_CENTER);
            tableDatosEmpresa.addCell(cellEmp);
            //AQUI TERMINA LA TABLA PARA DATOS DE LA EMPRESA
            
            
            //aqui se agrega la tableDatosEmpresa a la tablaPrincipal
            cell = new PdfPCell(tableDatosEmpresa);
            cell.setBorderWidthBottom(0);
            cell.setBorderWidthTop(0);
            cell.setBorderWidthRight(0);
            cell.setBorderWidthLeft(0);
            tablaPrincipal.addCell(cell);
            
            
            
            
            
            ////////////////////////////////////////////////////////////////////////////////
            //aqui se agrega la tabla  superior derecha
            cell = new PdfPCell(cepdf.addContent());
            cell.setBorderWidthBottom(0);
            cell.setBorderWidthTop(0);
            cell.setBorderWidthRight(0);
            cell.setBorderWidthLeft(0);
            tablaPrincipal.addCell(cell);
            ////////////////////////////////////////////////////////////////////////////////
            
            
            tablaPrincipal.setSpacingAfter(20f);
             
            //AQUI SE AGREGA LA TABLA PRINCIPAL AL ENCABEZADO DEL DOCUMENTO
            document.add(tablaPrincipal);
            
            
            float [] widths2 = {2f,4.8f,0.5f,2f,4.8f};
            PdfPTable tableHelper = new PdfPTable(widths2);
           /*
            cell = new PdfPCell(new Paragraph("ALMACEN ORIGEN:",smallFontBold));
            cell.setBorderWidthBottom(0);
            cell.setBorderWidthTop(0);
            cell.setBorderWidthRight(0);
            cell.setBorderWidthLeft(0);
            tableHelper.addCell(cell);
            
            cell = new PdfPCell(new Paragraph(this.getAlmacenOrigen(),smallFont));
            cell.setBorderWidthBottom(1);
            cell.setBorderWidthTop(0);
            cell.setBorderWidthRight(0);
            cell.setBorderWidthLeft(0);
            tableHelper.addCell(cell);
           
            cell = new PdfPCell(new Paragraph("",smallFont));
            cell.setBorderWidthBottom(0);
            cell.setBorderWidthTop(0);
            cell.setBorderWidthRight(0);
            cell.setBorderWidthLeft(0);
            tableHelper.addCell(cell);
            
            cell = new PdfPCell(new Paragraph("ALMACEN DESTINO:",smallFontBold));
            cell.setBorderWidthBottom(0);
            cell.setBorderWidthTop(0);
            cell.setBorderWidthRight(0);
            cell.setBorderWidthLeft(0);
            tableHelper.addCell(cell);
            
            cell = new PdfPCell(new Paragraph(this.getAlmacenDestino(),smallFont));
            cell.setBorderWidthBottom(1);
            cell.setBorderWidthTop(0);
            cell.setBorderWidthRight(0);
            cell.setBorderWidthLeft(0);
            tableHelper.addCell(cell);
          */  
            tableHelper.setSpacingAfter(10f);
            
            document.add(tableHelper);
             
            
            ////////////////////////////////////////////////////////////////////////////////
            //metodo para agregar lista conceptos al pdf
            document.add(tablaConceptos.addContent());
            ////////////////////////////////////////////////////////////////////////////////
            
                    
            table2 = new PdfPTable(1);
            table2.setKeepTogether(true);
            
            if (this.getObservaciones().isEmpty()){
                cell = new PdfPCell(new Paragraph("",smallBoldFont));
                cell.setBorder(0);
                table2.addCell(cell);
                
                cell = new PdfPCell(new Paragraph("", smallFont));
                cell.setBorder(0);
                table2.addCell(cell);
            }else{
                cell = new PdfPCell(new Paragraph("OBSERVACIONES:",smallBoldFont));
                cell.setBorder(0);
                table2.addCell(cell);
                
                //CADENA ORIGINAL --> BeanFromCFD (getCadenaOriginal)
                
                cell = new PdfPCell(new Paragraph(StringHelper.capitalizaString(this.getObservaciones()), smallFont));
                cell.setBorder(0);
                table2.addCell(cell); 
            }
            
            table2.setSpacingAfter(30f);
            document.add(table2);
            
            
            
            
            float [] widths3 = {4,0.5f,4,0.5f,4};
            tableElaboro = new PdfPTable(widths3);
            tableElaboro.setKeepTogether(true);
            
            //FILA 1
            cell = new PdfPCell(new Paragraph("ELABORÓ",smallFont));
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
            
            cell = new PdfPCell(new Paragraph("AUTORIZÓ",smallFont));
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setVerticalAlignment(Element.ALIGN_TOP);
            cell.setBorder(0);
            tableElaboro.addCell(cell);
            
            
            //FILA 2
            cell = new PdfPCell(new Paragraph(this.getUsuario_elaboracion().toUpperCase(),smallFont));
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setVerticalAlignment(Element.ALIGN_BOTTOM);
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
            
            cell = new PdfPCell(new Paragraph("",smallBoldFont));
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
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setVerticalAlignment(Element.ALIGN_BOTTOM);
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
    
    
    
    private class ImagenPDF {
        public Image addContent() {
            Image img = null;
            try {
                img = Image.getInstance(getRuta_imagen());
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
    
    
    
    
    //esta es la tabla que va en la parte Superior Derecha
    private class CeldaPDF {
        public PdfPTable addContent() {
            Font smallFont = new Font(Font.FontFamily.HELVETICA,7,Font.NORMAL,BaseColor.BLACK);
            Font sont = new Font(Font.FontFamily.HELVETICA,8,Font.BOLD,BaseColor.BLACK);
            Font smallBoldFont = new Font(Font.FontFamily.HELVETICA, 8, Font.BOLD, BaseColor.WHITE);
            Font largeBoldFont = new Font(Font.FontFamily.HELVETICA,10,Font.BOLD,BaseColor.BLACK);
            
            PdfPTable table = new PdfPTable(1);
            PdfPCell cell;
            
            cell = new PdfPCell(new Paragraph(getTipo_documento(),largeBoldFont));
            cell.setBorder(0);
            cell.setUseAscender(true);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setUseDescender(true);
            cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            table.addCell(cell);
            
            
            cell = new PdfPCell(new Paragraph("FOLIO REQUISICION",smallBoldFont));
            cell.setUseAscender(true);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setUseDescender(true);
            cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            cell.setBackgroundColor(BaseColor.BLACK);
            table.addCell(cell);
            
            cell = new PdfPCell(new Paragraph(getFolio_requisicion(),sont));
            cell.setUseAscender(true);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setUseDescender(true);
            cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            table.addCell(cell);
            
            cell = new PdfPCell(new Paragraph("FECHA REQUISICION",smallBoldFont));
            cell.setUseAscender(true);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setUseDescender(true);
            cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            cell.setBackgroundColor(BaseColor.BLACK);
            table.addCell(cell);
            
            cell = new PdfPCell(new Paragraph(getFecha_requisicion(),smallFont));
            cell.setUseAscender(true);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setUseDescender(true);
            cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            table.addCell(cell);
            
             cell = new PdfPCell(new Paragraph("FECHA COMPROMISO",smallBoldFont));
            cell.setUseAscender(true);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setUseDescender(true);
            cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            cell.setBackgroundColor(BaseColor.BLACK);
            table.addCell(cell);
            
            cell = new PdfPCell(new Paragraph(getFecha_compromiso(),smallFont));
            cell.setUseAscender(true);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setUseDescender(true);
            cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            table.addCell(cell);
            
            return table;
        }
    }
    
    
    
    private class TablaPDF {
        public PdfPTable addContent() {
            
            Font small = new Font(Font.FontFamily.COURIER,6,Font.NORMAL,BaseColor.BLACK);
            
            Font smallFont = new Font(Font.FontFamily.HELVETICA,7,Font.NORMAL,BaseColor.BLACK);
            Font smallBoldFont = new Font(Font.FontFamily.HELVETICA, 8, Font.BOLD, BaseColor.WHITE);
            Font smallBoldFont1 = new Font(Font.FontFamily.HELVETICA, 7, Font.BOLD, BaseColor.WHITE);
            Font smallBoldFontBlack = new Font(Font.FontFamily.HELVETICA, 8, Font.BOLD, BaseColor.BLACK);
            Font largeFont = new Font(Font.FontFamily.HELVETICA,8,Font.NORMAL,BaseColor.BLACK);
            
            
            //float [] widths = {2f, 5.5f, 3f, 2f, 1.5f, 2f,2f,2f,2f};
            float [] widths = {
                2f,     //codigo
                3f,   //descripcion
                2f,   //Unidad
                2f,    //presentacion
                2f,   //cantidad
                2f   //urgente

           };
            PdfPTable table = new PdfPTable(widths);
            PdfPCell cell;
            
            Iterator it;
            
            table.setKeepTogether(false);
            table.setHeaderRows(1);
            
            cell = new PdfPCell(new Paragraph("Código",smallBoldFont));
            cell.setUseAscender(true);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setUseDescender(true);
            cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            cell.setBackgroundColor(BaseColor.BLACK);
            table.addCell(cell);
            
            cell = new PdfPCell(new Paragraph("Descripción",smallBoldFont));
            cell.setUseAscender(true);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setUseDescender(true);
            cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            cell.setBackgroundColor(BaseColor.BLACK);
            table.addCell(cell);
            
            
            cell = new PdfPCell(new Paragraph("Unidad",smallBoldFont));
            cell.setUseAscender(true);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setUseDescender(true);
            cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            cell.setBackgroundColor(BaseColor.BLACK);
            table.addCell(cell);
            
            cell = new PdfPCell(new Paragraph("Presentación",smallBoldFont));
            cell.setUseAscender(true);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setUseDescender(true);
            cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            cell.setBackgroundColor(BaseColor.BLACK);
            table.addCell(cell);
            
            cell = new PdfPCell(new Paragraph("Cantidad",smallBoldFont));
            cell.setUseAscender(true);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setUseDescender(true);
            cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            cell.setBackgroundColor(BaseColor.BLACK);
            table.addCell(cell);
            
            cell = new PdfPCell(new Paragraph("Urgente",smallBoldFont));
            cell.setUseAscender(true);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setUseDescender(true);
            cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            cell.setBackgroundColor(BaseColor.BLACK);
            table.addCell(cell);
            
                    
            it = getLista_productos().iterator();
           while(it.hasNext()){
                HashMap<String,String> map = (HashMap<String,String>)it.next();
                
                cell = new PdfPCell(new Paragraph(esteAtributoSeDejoNulo(map.get("codigo")), smallFont));
                cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                cell.setHorizontalAlignment(Element.ALIGN_LEFT);
                table.addCell(cell);
                
                
                String descripcion = map.get("titulo");
                descripcion =  StringEscapeUtils.unescapeHtml(descripcion);
                cell = new PdfPCell(new Paragraph(StringHelper.capitalizaString(descripcion), smallFont));
                cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                cell.setHorizontalAlignment(Element.ALIGN_LEFT);
                table.addCell(cell);
                
                cell = new PdfPCell(new Paragraph(StringHelper.capitalizaString(esteAtributoSeDejoNulo(map.get("unidad"))), smallFont));
                cell.setHorizontalAlignment(Element.ALIGN_LEFT);
                cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                table.addCell(cell);
                
                cell = new PdfPCell(new Paragraph(map.get("presentacion"), smallFont));
                cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                table.addCell(cell);
                
                //cantidad del ajuste
                cell = new PdfPCell(new Paragraph(StringHelper.AgregaComas(map.get("cantidad")), smallFont));
                cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                table.addCell(cell);
                
                cell = new PdfPCell(new Paragraph("SI", smallFont));
                cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                table.addCell(cell);

            } 
            
            cell = new PdfPCell(new Paragraph("", smallFont));
            cell.setBorderWidthBottom(0);
            cell.setBorderWidthTop(1);
            cell.setBorderWidthRight(0);
            cell.setBorderWidthLeft(0);
            cell.setColspan(6);
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
