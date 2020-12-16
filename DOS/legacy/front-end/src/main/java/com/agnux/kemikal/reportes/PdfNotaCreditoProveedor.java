package com.agnux.kemikal.reportes;


import com.agnux.common.helpers.StringHelper;
import com.agnux.common.helpers.n2t;
import com.itextpdf.text.Image;
import java.net.URISyntaxException;
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
import java.math.BigInteger;
import java.util.HashMap;
    
    
public final class PdfNotaCreditoProveedor {
    private HashMap<String, String> datosHeaderFooter = new HashMap<String, String>();
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
    
    private String prov_folio;
    private String prov_razon_social;
    private String prov_rfc;
    private String prov_calle;
    private String prov_numero;
    private String prov_colonia;
    private String prov_cp;
    private String prov_municipio;
    private String prov_estado;
    private String prov_pais;
    
    private String tipo_documento;
    private String nota_tipo;
    private String nota_folio_registro;
    private String nota_serie_folio;
    private String nota_concepto;
    private String nota_fecha_exp;
    private String nota_moneda;
    private String nota_moneda_abr;
    private String nota_simbolo_moneda;
    private String nota_observaciones;
    private String nota_tipo_cambio;
    private String nota_subtotal;
    private String nota_ieps;
    private String nota_impuesto;
    private String nota_total;
    
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
    
    public String getFileout() {
        return fileout;
    }
    
    public void setFileout(String fileout) {
        this.fileout = fileout;
    }
    
    public String getNota_concepto() {
        return nota_concepto;
    }
    
    public void setNota_concepto(String nota_concepto) {
        this.nota_concepto = nota_concepto;
    }
    
    public String getNota_fecha_exp() {
        return nota_fecha_exp;
    }
    
    public void setNota_fecha_exp(String nota_fecha_exp) {
        this.nota_fecha_exp = nota_fecha_exp;
    }
    
    public String getNota_folio_registro() {
        return nota_folio_registro;
    }
    
    public void setNota_folio_registro(String nota_folio_registro) {
        this.nota_folio_registro = nota_folio_registro;
    }
    
    public String getNota_impuesto() {
        return nota_impuesto;
    }
    
    public void setNota_impuesto(String nota_impuesto) {
        this.nota_impuesto = nota_impuesto;
    }
    
    public String getNota_moneda() {
        return nota_moneda;
    }
    
    public void setNota_moneda(String nota_moneda) {
        this.nota_moneda = nota_moneda;
    }
    
    public String getNota_moneda_abr() {
        return nota_moneda_abr;
    }
    
    public void setNota_moneda_abr(String nota_moneda_abr) {
        this.nota_moneda_abr = nota_moneda_abr;
    }
    
    public String getNota_observaciones() {
        return nota_observaciones;
    }
    
    public void setNota_observaciones(String nota_observaciones) {
        this.nota_observaciones = nota_observaciones;
    }
    
    public String getNota_serie_folio() {
        return nota_serie_folio;
    }
    
    public void setNota_serie_folio(String nota_serie_folio) {
        this.nota_serie_folio = nota_serie_folio;
    }
    
    public String getNota_simbolo_moneda() {
        return nota_simbolo_moneda;
    }
    
    public void setNota_simbolo_moneda(String nota_simbolo_moneda) {
        this.nota_simbolo_moneda = nota_simbolo_moneda;
    }
    
    public String getNota_subtotal() {
        return nota_subtotal;
    }
    
    public void setNota_subtotal(String nota_subtotal) {
        this.nota_subtotal = nota_subtotal;
    }
    
    public String getNota_ieps() {
        return nota_ieps;
    }

    public void setNota_ieps(String nota_ieps) {
        this.nota_ieps = nota_ieps;
    }
    
    public String getNota_tipo_cambio() {
        return nota_tipo_cambio;
    }
    
    public void setNota_tipo_cambio(String nota_tipo_cambio) {
        this.nota_tipo_cambio = nota_tipo_cambio;
    }
    
    public String getNota_tipo() {
        return nota_tipo;
    }

    public void setNota_tipo(String nota_tipo) {
        this.nota_tipo = nota_tipo;
    }
    
    public String getNota_total() {
        return nota_total;
    }
    
    public void setNota_total(String nota_total) {
        this.nota_total = nota_total;
    }
    
    public String getProv_calle() {
        return prov_calle;
    }
    
    public void setProv_calle(String prov_calle) {
        this.prov_calle = prov_calle;
    }
    
    public String getProv_colonia() {
        return prov_colonia;
    }
    
    public void setProv_colonia(String prov_colonia) {
        this.prov_colonia = prov_colonia;
    }
    
    public String getProv_cp() {
        return prov_cp;
    }
    
    public void setProv_cp(String prov_cp) {
        this.prov_cp = prov_cp;
    }
    
    public String getProv_estado() {
        return prov_estado;
    }
    
    public void setProv_estado(String prov_estado) {
        this.prov_estado = prov_estado;
    }
    
    public String getProv_folio() {
        return prov_folio;
    }
    
    public void setProv_folio(String prov_folio) {
        this.prov_folio = prov_folio;
    }
    
    public String getProv_municipio() {
        return prov_municipio;
    }
    
    public void setProv_municipio(String prov_municipio) {
        this.prov_municipio = prov_municipio;
    }
    
    public String getProv_numero() {
        return prov_numero;
    }
    
    public void setProv_numero(String prov_numero) {
        this.prov_numero = prov_numero;
    }
    
    public String getProv_pais() {
        return prov_pais;
    }
    
    public void setProv_pais(String prov_pais) {
        this.prov_pais = prov_pais;
    }
    
    public String getProv_razon_social() {
        return prov_razon_social;
    }
    
    public void setProv_razon_social(String prov_razon_social) {
        this.prov_razon_social = prov_razon_social;
    }
    
    public String getProv_rfc() {
        return prov_rfc;
    }
    
    public void setProv_rfc(String prov_rfc) {
        this.prov_rfc = prov_rfc;
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
    
    
    
    
    public PdfNotaCreditoProveedor(HashMap<String, String> datos_empresa, HashMap<String, String> datos_nota, String fileout, String ruta_imagen) {
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
        
        this.setProv_folio(datos_nota.get("prov_folio"));
        this.setProv_razon_social(datos_nota.get("prov_razon_social"));
        this.setProv_rfc(datos_nota.get("prov_rfc"));
        this.setProv_calle(datos_nota.get("prov_calle"));
        this.setProv_numero(datos_nota.get("prov_numero"));
        this.setProv_colonia(datos_nota.get("prov_colonia"));
        this.setProv_cp(datos_nota.get("prov_cp"));
        this.setProv_municipio(datos_nota.get("prov_municipio"));
        this.setProv_estado(datos_nota.get("prov_estado"));
        this.setProv_pais(datos_nota.get("prov_pais"));
        
        this.setTipo_documento("NOTA CREDITO PROVEEDOR");
        this.setNota_tipo(datos_nota.get("nota_tipo"));
        this.setNota_folio_registro(datos_nota.get("nota_folio_registro"));
        this.setNota_serie_folio(datos_nota.get("nota_serie_folio"));
        this.setNota_concepto(datos_nota.get("nota_concepto"));
        this.setNota_fecha_exp(datos_nota.get("nota_fecha_exp"));
        this.setNota_moneda(datos_nota.get("nota_moneda"));
        this.setNota_moneda_abr(datos_nota.get("nota_moneda_abr"));
        this.setNota_simbolo_moneda(datos_nota.get("nota_simbolo_moneda"));
        this.setNota_observaciones(datos_nota.get("nota_observaciones"));
        this.setNota_tipo_cambio(datos_nota.get("nota_tipo_cambio"));
        this.setNota_subtotal(datos_nota.get("nota_subtotal"));
        this.setNota_ieps(datos_nota.get("monto_ieps"));
        this.setNota_impuesto(datos_nota.get("nota_impuesto"));
        this.setNota_total(datos_nota.get("nota_total"));
        
        //datos para el encabezado, no se esta utilizando
        datos.put("empresa", "");
        datos.put("titulo_reporte", "");
        datos.put("periodo", "");
        
        //datos para el pie de pagina
        datos.put("codigo1", datos_nota.get("codigo1"));
        datos.put("codigo2", datos_nota.get("codigo2"));
        
        this.setDatosHeaderFooter(datos);
    }
    
    
    
    
    public void ViewPDF() throws URISyntaxException {
        HashMap<String, String> datos = new HashMap<String, String>();
        Font smallBoldFontWhite = new Font(Font.FontFamily.HELVETICA, 8, Font.BOLD, BaseColor.WHITE);
        Font smallFont = new Font(Font.FontFamily.HELVETICA,7,Font.NORMAL,BaseColor.BLACK);
        Font smallBoldFont = new Font(Font.FontFamily.HELVETICA,8,Font.BOLD,BaseColor.BLACK);
        Font largeBoldFont = new Font(Font.FontFamily.HELVETICA,10,Font.BOLD,BaseColor.BLACK);
        
        ImagenPDF ipdf = new ImagenPDF();
        CeldaPDF cepdf = new CeldaPDF();
        
        
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
            
            //System.out.println("PDF: "+this.getFileout());
            
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
            cell.setRowspan(2);
            tablaPrincipal.addCell(cell);
            ////////////////////////////////////////////////////////////////////////////////
            
            
            ////////////////////////////////////////////////////////////////////////////////
            //aqui inicia construccion de tabla para datos de los proveedores
            float [] widths2 = {18};
            PdfPTable tableDatosProveedor = new PdfPTable(widths2);
            
            cell = new PdfPCell(new Paragraph("PROVEEDOR:",smallBoldFont));
            cell.setBorderWidthBottom(0);
            cell.setBorderWidthTop(0);
            cell.setBorderWidthRight(0);
            cell.setBorderWidthLeft(0);
            cell.setVerticalAlignment(Element.ALIGN_BOTTOM);
            tableDatosProveedor.addCell(cell);
            
            cell = new PdfPCell(new Paragraph(this.getProv_razon_social(),smallFont));
            cell.setBorderWidthBottom(0);
            cell.setBorderWidthTop(0);
            cell.setBorderWidthRight(0);
            cell.setBorderWidthLeft(0);
            cell.setVerticalAlignment(Element.ALIGN_TOP);
            tableDatosProveedor.addCell(cell);
            
            
            cell = new PdfPCell(new Paragraph("RFC:",smallBoldFont));
            cell.setBorderWidthBottom(0);
            cell.setBorderWidthTop(0);
            cell.setBorderWidthRight(0);
            cell.setBorderWidthLeft(0);
            cell.setVerticalAlignment(Element.ALIGN_BOTTOM);
            tableDatosProveedor.addCell(cell);
            
            cell = new PdfPCell(new Paragraph(this.getProv_rfc(),smallFont));
            cell.setBorderWidthBottom(0);
            cell.setBorderWidthTop(0);
            cell.setBorderWidthRight(0);
            cell.setBorderWidthLeft(0);
            cell.setVerticalAlignment(Element.ALIGN_TOP);
            tableDatosProveedor.addCell(cell);
            
            
            cell = new PdfPCell(new Paragraph("DIRECCIÓN:",smallBoldFont));
            cell.setBorderWidthBottom(0);
            cell.setBorderWidthTop(0);
            cell.setBorderWidthRight(0);
            cell.setBorderWidthLeft(0);
            cell.setVerticalAlignment(Element.ALIGN_BOTTOM);
            tableDatosProveedor.addCell(cell);
            
            cell = new PdfPCell(new Paragraph(StringHelper.capitalizaString(this.getProv_calle()) + " " + StringHelper.capitalizaString(this.getProv_numero()) +  ", " + StringHelper.capitalizaString(this.getProv_colonia()) + ", \n" + StringHelper.capitalizaString(this.getProv_municipio()) + ", " + StringHelper.capitalizaString(this.getProv_estado())+ ", " + StringHelper.capitalizaString(this.getProv_pais()) + ", C.P. " + this.getProv_cp(),smallFont));
            cell.setBorderWidthBottom(0);
            cell.setBorderWidthTop(0);
            cell.setBorderWidthRight(0);
            cell.setBorderWidthLeft(0);
            cell.setVerticalAlignment(Element.ALIGN_TOP);
            tableDatosProveedor.addCell(cell);
            
            
            
            
            //aqui para los datos del proveedor
            cell = new PdfPCell(tableDatosProveedor);
            cell.setBorderWidthBottom(0);
            cell.setBorderWidthTop(0);
            cell.setBorderWidthRight(0);
            cell.setBorderWidthLeft(0);
            cell.setColspan(2);
            tablaPrincipal.addCell(cell);
            ////////////////////////////////////////////////////////////////////////////////
            
            
            
            //FILA VACIA
            cell = new PdfPCell(new Paragraph("",smallBoldFont));
            cell.setFixedHeight(10);
            cell.setBorder(0);
            cell.setColspan(3);
            tablaPrincipal.addCell(cell);
            
            
            
            //***********************************************************************************************************
            //aqui inicia construccion de tabla concepto
            float [] widths3 = {6,0.3F,1.5f};
            PdfPTable tablaConcepto = new PdfPTable(widths3);
            
            tablaConcepto.setKeepTogether(false);
            tablaConcepto.setHeaderRows(1);
            
            
            cell = new PdfPCell(new Paragraph("CONCEPTO",smallBoldFontWhite));
            cell.setUseAscender(true);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setUseDescender(true);
            cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            cell.setBackgroundColor(BaseColor.BLACK);
            tablaConcepto.addCell(cell);
            
            cell = new PdfPCell(new Paragraph("",smallBoldFont));
            cell.setUseAscender(true);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setUseDescender(true);
            cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            cell.setBackgroundColor(BaseColor.BLACK);
            tablaConcepto.addCell(cell);
            
            cell = new PdfPCell(new Paragraph("IMPORTE",smallBoldFontWhite));
            cell.setUseAscender(true);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setUseDescender(true);
            cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            cell.setBackgroundColor(BaseColor.BLACK);
            tablaConcepto.addCell(cell);
            
            

            cell = new PdfPCell(new Paragraph(StringHelper.capitalizaString(this.getNota_concepto()), smallFont));
            cell.setVerticalAlignment(Element.ALIGN_TOP);
            cell.setHorizontalAlignment(Element.ALIGN_LEFT);
            tablaConcepto.addCell(cell);

            cell = new PdfPCell(new Paragraph(StringHelper.capitalizaString(this.getNota_simbolo_moneda()), smallFont));
            cell.setVerticalAlignment(Element.ALIGN_TOP);
            cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            cell.setBorderWidthRight(0);
            tablaConcepto.addCell(cell);
            
            cell = new PdfPCell(new Paragraph(StringHelper.AgregaComas(this.getNota_subtotal()), smallFont));
            cell.setVerticalAlignment(Element.ALIGN_TOP);
            cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            cell.setBorderWidthLeft(0);
            tablaConcepto.addCell(cell);
            
            
            
            //aqui  SE AGREGA LA TABLA CONCEPTOS A LA TABLA PRINCIPAL
            cell = new PdfPCell(tablaConcepto);
            cell.setBorderWidthBottom(1);
            cell.setBorderWidthTop(1);
            cell.setBorderWidthRight(1);
            cell.setBorderWidthLeft(1);
            cell.setColspan(3);
            cell.setFixedHeight(120);
            cell.setVerticalAlignment(Element.ALIGN_TOP);
            tablaPrincipal.addCell(cell);
            //***********************************************************************************************************
            
            
            BigInteger num = new BigInteger(this.getNota_total().split("\\.")[0]);
            n2t cal = new n2t();
            String centavos = this.getNota_total().substring(this.getNota_total().indexOf(".")+1);
            String numero = cal.convertirLetras(num);
            
            //convertir a mayuscula la primera letra de la cadena
            String numeroMay = numero.substring(0, 1).toUpperCase() + numero.substring(1, numero.length());
            
            String denom = "";
            String denominacion="";
            
            denominacion = this.getNota_moneda();
            denom = this.getNota_moneda_abr();
            //getSimbolo_moneda();

            if(centavos.equals(num.toString())){
                centavos="00";
            }
            
            String total_letras = numeroMay.toUpperCase() + " " + denominacion.toUpperCase() + " " +centavos+"/100 "+ denom.toUpperCase();
            
            
            
            
            //***********************************************************************************************************
            //aqui inicia construccion de tabla TOTALES
            float [] widths4 = {5,1,0.3F,1.5f};
            PdfPTable tablaTotales = new PdfPTable(widths4);
            int colspan=4;
            if(Double.parseDouble(this.getNota_ieps())>0){
                colspan=5;
            }
            
            //fila subtotal
            cell = new PdfPCell(new Paragraph(total_letras, smallFont));
            cell.setVerticalAlignment(Element.ALIGN_BOTTOM);
            cell.setHorizontalAlignment(Element.ALIGN_LEFT);
            cell.setRowspan(colspan);
            tablaTotales.addCell(cell);
            
            cell = new PdfPCell(new Paragraph("SUBTOTAL", smallBoldFont));
            cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            cell.setBorderWidthBottom(0);
            tablaTotales.addCell(cell);

            cell = new PdfPCell(new Paragraph(StringHelper.capitalizaString(this.getNota_simbolo_moneda()), smallFont));
            cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            cell.setBorderWidthBottom(0);
            cell.setBorderWidthRight(0);
            tablaTotales.addCell(cell);
            
            cell = new PdfPCell(new Paragraph(StringHelper.AgregaComas(this.getNota_subtotal()), smallFont));
            cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            cell.setBorderWidthBottom(0);
            cell.setBorderWidthLeft(0);
            tablaTotales.addCell(cell);
            
            
            if(colspan==5){
                //fila IEPS
                /*
                cell = new PdfPCell(new Paragraph("", smallFont));
                cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                tablaTotales.addCell(cell);
                */
                cell = new PdfPCell(new Paragraph("IEPS", smallBoldFont));
                cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                cell.setBorderWidthBottom(0);
                cell.setBorderWidthTop(0);
                tablaTotales.addCell(cell);

                cell = new PdfPCell(new Paragraph(StringHelper.capitalizaString(this.getNota_simbolo_moneda()), smallFont));
                cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                cell.setBorderWidthBottom(0);
                cell.setBorderWidthTop(0);
                cell.setBorderWidthRight(0);
                tablaTotales.addCell(cell);

                cell = new PdfPCell(new Paragraph(StringHelper.AgregaComas(this.getNota_ieps()), smallFont));
                cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                cell.setBorderWidthBottom(0);
                cell.setBorderWidthTop(0);
                cell.setBorderWidthLeft(0);
                tablaTotales.addCell(cell);                
            }

            
            //fila impuesto
            /*
            cell = new PdfPCell(new Paragraph("", smallFont));
            cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            tablaTotales.addCell(cell);
            */
            cell = new PdfPCell(new Paragraph("IVA", smallBoldFont));
            cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            cell.setBorderWidthBottom(0);
            cell.setBorderWidthTop(0);
            tablaTotales.addCell(cell);

            cell = new PdfPCell(new Paragraph(StringHelper.capitalizaString(this.getNota_simbolo_moneda()), smallFont));
            cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            cell.setBorderWidthBottom(0);
            cell.setBorderWidthTop(0);
            cell.setBorderWidthRight(0);
            tablaTotales.addCell(cell);
            
            cell = new PdfPCell(new Paragraph(StringHelper.AgregaComas(this.getNota_impuesto()), smallFont));
            cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            cell.setBorderWidthBottom(0);
            cell.setBorderWidthTop(0);
            cell.setBorderWidthLeft(0);
            tablaTotales.addCell(cell);
            
            
            //fila TOTAL
            /*
            cell = new PdfPCell(new Paragraph("", smallFont));
            cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            tablaTotales.addCell(cell);
            */
            cell = new PdfPCell(new Paragraph("TOTAL", smallBoldFont));
            cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            cell.setBorderWidthTop(0);
            tablaTotales.addCell(cell);

            cell = new PdfPCell(new Paragraph(StringHelper.capitalizaString(this.getNota_simbolo_moneda()), smallFont));
            cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            cell.setBorderWidthTop(0);
            cell.setBorderWidthRight(0);
            tablaTotales.addCell(cell);
            
            cell = new PdfPCell(new Paragraph(StringHelper.AgregaComas(this.getNota_total()), smallFont));
            cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            cell.setBorderWidthTop(0);
            cell.setBorderWidthLeft(0);
            tablaTotales.addCell(cell);
            
            
            
            //aqui  SE AGREGA LA TABLA TOTALES A LA TABLA PRINCIPAL
            cell = new PdfPCell(tablaTotales);
            cell.setBorderWidthBottom(0);
            cell.setBorderWidthTop(0);
            cell.setBorderWidthRight(0);
            cell.setBorderWidthLeft(0);
            cell.setColspan(3);
            tablaPrincipal.addCell(cell);
            //***********************************************************************************************************
            
            
            tablaPrincipal.setSpacingAfter(20f);
            
            //AQUI SE AGREGA LA TABLA PRINCIPAL AL ENCABEZADO DEL DOCUMENTO
            document.add(tablaPrincipal);
            
            
            
            
            
            
            table2 = new PdfPTable(1);
            table2.setKeepTogether(true);
            
            if (this.getNota_observaciones().isEmpty()){
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
                
                cell = new PdfPCell(new Paragraph(StringHelper.capitalizaString(this.getNota_observaciones()), smallFont));
                cell.setBorder(0);
                table2.addCell(cell); 
            }
            
            table2.setSpacingAfter(30f);
            document.add(table2);
            
            
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
            Font smallBoldFontBlack = new Font(Font.FontFamily.HELVETICA,8,Font.BOLD,BaseColor.BLACK);
            PdfPTable table = new PdfPTable(1);
            PdfPCell cell;
            
            cell = new PdfPCell(new Paragraph(getTipo_documento(),smallBoldFontBlack));
            cell.setBorder(0);
            cell.setUseAscender(true);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setUseDescender(true);
            cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            table.addCell(cell);
            
            
            cell = new PdfPCell(new Paragraph("FOLIO",smallBoldFont));
            cell.setUseAscender(true);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setUseDescender(true);
            cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            cell.setBackgroundColor(BaseColor.BLACK);
            table.addCell(cell);
            
            cell = new PdfPCell(new Paragraph(getNota_folio_registro(),sont));
            cell.setUseAscender(true);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setUseDescender(true);
            cell.setVerticalAlignment(Element.ALIGN_TOP);
            table.addCell(cell);
            
            cell = new PdfPCell(new Paragraph(" ", smallFont));
            cell.setBorder(0);
            table.addCell(cell);
            
            cell = new PdfPCell(new Paragraph("NOTA CREDITO No.",smallBoldFont));
            cell.setUseAscender(true);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setUseDescender(true);
            cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            cell.setBackgroundColor(BaseColor.BLACK);
            table.addCell(cell);
            
            cell = new PdfPCell(new Paragraph(getNota_serie_folio(),sont));
            cell.setUseAscender(true);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setUseDescender(true);
            cell.setVerticalAlignment(Element.ALIGN_TOP);
            table.addCell(cell);
            
            cell = new PdfPCell(new Paragraph(" ", smallFont));
            cell.setBorder(0);
            table.addCell(cell);
            
            cell = new PdfPCell(new Paragraph("FECHA EXPEDICIÓN",smallBoldFont));
            cell.setUseAscender(true);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setUseDescender(true);
            cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            cell.setBackgroundColor(BaseColor.BLACK);
            table.addCell(cell);
            
            cell = new PdfPCell(new Paragraph(getNota_fecha_exp(),smallFont));
            cell.setUseAscender(true);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setUseDescender(true);
            cell.setVerticalAlignment(Element.ALIGN_TOP);
            table.addCell(cell);
            
            cell = new PdfPCell(new Paragraph(" ", smallFont));
            cell.setBorder(0);
            table.addCell(cell);
            cell = new PdfPCell(new Paragraph("TIPO NOTA CRÉDITO",smallBoldFont));
            cell.setUseAscender(true);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setUseDescender(true);
            cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            cell.setBackgroundColor(BaseColor.BLACK);
            table.addCell(cell);
            
            cell = new PdfPCell(new Paragraph(getNota_tipo(),smallFont));
            cell.setUseAscender(true);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setUseDescender(true);
            cell.setVerticalAlignment(Element.ALIGN_TOP);
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
