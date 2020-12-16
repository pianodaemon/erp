package com.agnux.kemikal.reportes;

import com.agnux.kemikal.interfacedaos.GralInterfaceDao;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import com.agnux.common.helpers.StringHelper;
import com.agnux.common.helpers.n2t;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;
import java.io.FileOutputStream;
import java.math.BigInteger;
import java.util.List;



public final class pdfCfd {
   //--variables para pdf--
    private  GralInterfaceDao gralDao;
    
    public static enum Proposito {
        FACTURA, NOTA_CREDITO, NOTA_CARGO
    };
    
    private String tipo_facturacion;
    private String imagen;
    private String imagen_cedula;
    private String empresa_emisora;
    private String emisora_rfc;
    private String emisora_regimen_fiacal;
    private String emisora_calle;
    private String emisora_numero;
    private String emisora_colonia;
    private String emisora_cp;
    private String emisora_municipio;
    private String emisora_estado;
    private String emisora_pais;
    private String emisora_telefono;
    private String emisora_pagina_web;

    private String lugar_expedidion;
    
    private String proposito;
    private String no_aprobacion;
    private String ano_aprobacion;
    private String no_certificado;
    private String cadena_original;
    private String sello_digital;
    private String sello_digital_sat;
    private String uuid;
    private String serie_folio;
    private String facha_comprobante;
    
    
    private ArrayList<HashMap<String, String>> rows;
    private HashMap<String, String> encabezado;
    private HashMap<String, String> datosCliente;
    private HashMap<String, String> datosExtras;
    private String vendedor;
    private String ordenCompra;
    private String folioPedido;
    private String terminos;
    private int dias;
    private String observaciones;
    private String fecha_pago;
    private String metodo_pago;
    private String no_cuenta;
    private String subTotal;
    private String montoImpuesto;
    private String montoRetencion;
    private String montoTotal;
    private String titulo_moneda;
    private String moneda_abr;
    private String simbolo_moneda;
    
    
    private String receptor_razon_social;
    private String receptor_no_control;
    private String receptor_rfc;
    private String receptor_calle;
    private String receptor_numero;
    private String receptor_colonia;
    private String receptor_cp;
    private String receptor_municipio;
    private String receptor_estado;
    private String receptor_pais;
    private String receptor_telefono;
    
    private String etiqueta_tipo_doc;
    
    
    //----------------------
    
   public pdfCfd(GralInterfaceDao gDao,HashMap<String, String> datosCliente, ArrayList<HashMap<String, String>> listaConceptos, HashMap<String, String> extras, Integer id_empresa, Integer id_sucursal) {
        Font negrita_pequena= new Font(Font.FontFamily.HELVETICA,6,Font.BOLD,BaseColor.BLACK);
        Font smallFont = new Font(Font.FontFamily.HELVETICA,8,Font.NORMAL,BaseColor.BLACK);
        Font largeBoldFont = new Font(Font.FontFamily.HELVETICA,8,Font.BOLD,BaseColor.BLACK);
        ImagenPDF ipdf = new ImagenPDF();
        CeldaPDF cepdf = new CeldaPDF();
        CeldaFother cepdffother = new CeldaFother();
        
        this.setRows(listaConceptos);
        this.setDatosCliente(datosCliente);
        this.setDatosExtras(extras);
        
        this.setGralDao(gDao);
        this.setTipo_facturacion(extras.get("tipo_facturacion"));
        this.setSello_digital_sat(extras.get("sello_sat"));
        this.setUuid(extras.get("uuid"));
        
        //System.out.println("id_empresa::"+ id_empresa);
        System.out.println("Razon_soc: "+ this.getGralDao().getRazonSocialEmpresaEmisora(id_empresa)  );
        
        this.setEmpresa_emisora( this.getGralDao().getRazonSocialEmpresaEmisora(id_empresa) );
        this.setEmisora_rfc(this.getGralDao().getRfcEmpresaEmisora(id_empresa));
        this.setEmisora_regimen_fiacal(this.getGralDao().getRegimenFiscalEmpresaEmisora(id_empresa));
        this.setEmisora_calle( this.getGralDao().getCalleDomicilioFiscalEmpresaEmisora(id_empresa));
        this.setEmisora_numero( this.getGralDao().getNoExteriorDomicilioFiscalEmpresaEmisora(id_empresa));
        this.setEmisora_colonia(this.getGralDao().getColoniaDomicilioFiscalEmpresaEmisora(id_empresa));
        this.setEmisora_municipio(this.getGralDao().getMunicipioDomicilioFiscalEmpresaEmisora(id_empresa));
        this.setEmisora_estado(this.getGralDao().getEstadoDomicilioFiscalEmpresaEmisora(id_empresa));
        this.setEmisora_pais(this.getGralDao().getPaisDomicilioFiscalEmpresaEmisora(id_empresa));
        this.setEmisora_cp(this.getGralDao().getCpDomicilioFiscalEmpresaEmisora(id_empresa));
        this.setEmisora_telefono(this.getGralDao().getTelefonoEmpresaEmisora(id_empresa));
        this.setEmisora_pagina_web(this.getGralDao().getPaginaWebEmpresaEmisora(id_empresa));
        this.setLugar_expedidion( this.getGralDao().getMunicipioSucursalEmisora(id_sucursal)+", "+ this.getGralDao().getEstadoSucursalEmisora(id_sucursal));
        
        
        
        this.setNo_certificado(this.getGralDao().getNoCertificadoEmpresaEmisora(id_empresa, id_sucursal));
        this.setProposito(extras.get("proposito"));
        this.setSerie_folio(extras.get("serieFolio"));
        this.setCadena_original(extras.get("cadena_original"));
        this.setSello_digital(extras.get("sello_digital"));
        this.setFacha_comprobante(extras.get("fecha_comprobante"));
        this.setOrdenCompra(extras.get("orden_compra"));
        this.setFolioPedido(extras.get("folio_pedido"));
        this.setTerminos(extras.get("terminos"));
        this.setDias(Integer.parseInt(extras.get("dias")));
        this.setVendedor(extras.get("nombre_vendedor"));
        this.setObservaciones(extras.get("observaciones"));
        this.setFecha_pago(extras.get("fecha_vencimiento"));
        this.setMetodo_pago(datosCliente.get("comprobante_attr_metododepago"));
        this.setNo_cuenta(datosCliente.get("comprobante_attr_numerocuenta"));
        
        switch (Proposito.valueOf(this.getProposito())) {
            case FACTURA:
                this.setNo_aprobacion(this.getGralDao().getNoAprobacionFactura(id_empresa, id_sucursal));
                this.setAno_aprobacion(this.getGralDao().getAnoAprobacionFactura(id_empresa, id_sucursal));
                this.setEtiqueta_tipo_doc("FACTURA No. / INVOICE No.");
                break;
                
            case NOTA_CREDITO:
                this.setNo_aprobacion(this.getGralDao().getNoAprobacionNotaCredito(id_empresa, id_sucursal));
                this.setAno_aprobacion(this.getGralDao().getAnoAprobacionNotaCredito(id_empresa, id_sucursal));
                this.setEtiqueta_tipo_doc("NOTA DE CREDITO No. / CREDIT NOTE No.");
                break;
                
            case NOTA_CARGO:
                this.setNo_aprobacion(this.getGralDao().getNoAprobacionNotaCargo(id_empresa, id_sucursal));
                this.setAno_aprobacion(this.getGralDao().getAnoAprobacionNotaCargo(id_empresa, id_sucursal));
                break;
        }
        
        
        
        
        this.setSubTotal(extras.get("subtotal"));
        this.setMontoImpuesto(extras.get("impuesto"));
        this.setMontoRetencion(extras.get("monto_retencion"));
        this.setMontoTotal(extras.get("total"));
        this.setMoneda_abr(extras.get("moneda_abr"));
        this.setTitulo_moneda(extras.get("nombre_moneda"));
        
        
        this.setReceptor_razon_social(datosCliente.get("comprobante_receptor_attr_nombre"));
        this.setReceptor_no_control(datosCliente.get("numero_control"));
        this.setReceptor_rfc(datosCliente.get("comprobante_receptor_attr_rfc"));
        this.setReceptor_calle(datosCliente.get("comprobante_receptor_domicilio_attr_calle"));
        this.setReceptor_numero(datosCliente.get("comprobante_receptor_domicilio_attr_noexterior"));
        this.setReceptor_colonia(datosCliente.get("comprobante_receptor_domicilio_attr_colonia"));
        this.setReceptor_cp(datosCliente.get("comprobante_receptor_domicilio_attr_codigopostal"));
        this.setReceptor_municipio(datosCliente.get("comprobante_receptor_domicilio_attr_municipio"));
        this.setReceptor_estado(datosCliente.get("comprobante_receptor_domicilio_attr_estado"));
        this.setReceptor_pais(datosCliente.get("comprobante_receptor_domicilio_attr_pais"));
        this.setReceptor_telefono("");
        
        
        
        this.setImagen( this.getGralDao().getImagesDir()+this.getEmisora_rfc()+"_logo.png" );
        this.setImagen_cedula( this.getGralDao().getImagesDir()+this.getEmisora_rfc()+"_cedula.png" );
        
        PdfPTable table;
        PdfPTable table_totales;
        PdfPCell cell;
        String fileout="";
        
        if(this.getTipo_facturacion().equals("cfd")){
            fileout = this.getGralDao().getCfdEmitidosDir() + this.getGralDao().getRfcEmpresaEmisora(id_empresa) + "/" + this.getSerie_folio() +".pdf";
        }
        
        if(this.getTipo_facturacion().equals("cfditf")){
            fileout = this.getGralDao().getCfdiTimbreEmitidosDir() + this.getGralDao().getRfcEmpresaEmisora(id_empresa) + "/" + this.getSerie_folio() +".pdf";
        }
        
        
        try {
            Document document = new Document(PageSize.LETTER, -50, -50, 20, 30);
            PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(fileout));
            document.open();
            
            float [] widths = {4.5f,5.5f,4,3,4,4};
            table = new PdfPTable(widths);
            table.setKeepTogether(false);
            
            cell = new PdfPCell(new Paragraph("",largeBoldFont));
            cell.setColspan(6);
            cell.setUseAscender(true);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setBorder(0);
            table.addCell(cell);
            
            
            
            //IMAGEN --> logo empresa
            cell = new PdfPCell(ipdf.addContent());
            cell.setBorder(0);
            cell.setRowspan(7);
            cell.setColspan(2);
            cell.setVerticalAlignment(Element.ALIGN_TOP);
            cell.setHorizontalAlignment(Element.ALIGN_LEFT);
            cell.setUseAscender(true);
            table.setWidthPercentage(77);
            table.addCell(cell);
            
            cell = new PdfPCell(new Paragraph("",largeBoldFont));
            cell.setBorder(0);
            //cell.setBorder(11);
            //cell.setBorder(12);
            cell.setUseAscender(true);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            table.addCell(cell);
            
            
            cell = new PdfPCell(new Paragraph("",largeBoldFont));
            cell.setBorder(0);
            cell.setUseAscender(true);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            table.addCell(cell);
            
            cell = new PdfPCell(new Paragraph("",largeBoldFont));
            cell.setBorder(0);
            cell.setUseAscender(true);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            table.addCell(cell);
            ////////////////////////////////////////////////////////////////////
            
            cell = new PdfPCell(new Paragraph("",largeBoldFont));
            cell.setBorder(0);
            cell.setUseAscender(true);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            table.addCell(cell);
            
            cell = new PdfPCell(new Paragraph("",largeBoldFont));
            cell.setBorder(0);
            cell.setUseAscender(true);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            table.addCell(cell);
            
            cell = new PdfPCell(new Paragraph("",largeBoldFont));
            cell.setBorder(0);
            cell.setUseAscender(true);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            table.addCell(cell);
            
            
            cell = new PdfPCell(new Paragraph(this.getEmpresa_emisora(),largeBoldFont));
            cell.setBorder(0);
            cell.setColspan(2);
            cell.setUseAscender(true);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            table.addCell(cell);
            ////////////////////////////////////////////////////////////////////
            
            
            cell = new PdfPCell(new Paragraph("",largeBoldFont));
            cell.setBorder(0);
            cell.setUseAscender(true);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            table.addCell(cell);
            
            
            cell = new PdfPCell(new Paragraph("",largeBoldFont));
            cell.setBorder(0);
            cell.setUseAscender(true);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            table.addCell(cell);
            
            cell = new PdfPCell(new Paragraph("",largeBoldFont));
            cell.setBorder(0);
            cell.setUseAscender(true);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            table.addCell(cell);
            
            
            cell = new PdfPCell(new Paragraph("",largeBoldFont));
            cell.setBorder(0);
            cell.setUseAscender(true);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            table.addCell(cell);
            
            cell = new PdfPCell(new Paragraph("",largeBoldFont));
            cell.setBorder(0);
            cell.setUseAscender(true);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            table.addCell(cell);
            
            cell = new PdfPCell(new Paragraph("",largeBoldFont));
            cell.setBorder(0);
            cell.setUseAscender(true);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            table.addCell(cell);
            ///////////////////////////////////////////////////////////////
            
            
            cell = new PdfPCell(new Paragraph( 
                    this.getEmisora_calle()+ " " + 
                    this.getEmisora_numero()+ " " + 
                    this.getEmisora_colonia()+ "\n" + 
                    this.getEmisora_municipio()+ ", " + 
                    this.getEmisora_estado()+ ",  "+ " C.P. " + 
                    this.getEmisora_cp()+ "\n" +"    R.F.C.: " + 
                    this.getEmisora_rfc()+ "\n"  +"    Tel./Fax. " + 
                    this.getEmisora_telefono()+ "\n" + 
                    this.getEmisora_regimen_fiacal(), smallFont)
            );
            cell.setBorder(0);
//            cell.setBorder(11);
//            cell.setBorder(12);
            cell.setColspan(2);
            cell.setUseAscender(true);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            table.addCell(cell);
            
            
            cell = new PdfPCell(new Paragraph("",largeBoldFont));
            cell.setBorder(0);
            cell.setUseAscender(true);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            table.addCell(cell);
            
            cell = new PdfPCell(new Paragraph("",largeBoldFont));
            cell.setBorder(0);
            cell.setUseAscender(true);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            table.addCell(cell);
            
            
            cell = new PdfPCell(new Paragraph(this.getEmisora_pagina_web(),largeBoldFont));
            cell.setBorder(0);
            cell.setColspan(2);
            cell.setUseAscender(true);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            table.addCell(cell);
            //////////////////////////////////////////////////////////////////////
            
            
            cell = new PdfPCell(new Paragraph("",largeBoldFont));
            cell.setBorder(0);
            cell.setFixedHeight(15);
            cell.setBorderWidthRight(1);
            cell.setUseAscender(true);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            table.addCell(cell);
            
            cell = new PdfPCell(new Paragraph("",largeBoldFont));
            cell.setBorder(0);
            cell.setUseAscender(true);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            table.addCell(cell);
            
            cell = new PdfPCell(new Paragraph("",largeBoldFont));
            cell.setBorder(0);
            cell.setUseAscender(true);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            table.addCell(cell);
            
            cell = new PdfPCell(new Paragraph("",largeBoldFont));
            cell.setBorder(0);
            cell.setUseAscender(true);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            table.addCell(cell);
            
            cell = new PdfPCell(new Paragraph("",largeBoldFont));
            cell.setBorder(0);
            cell.setBorderWidthRight(1);
            cell.setUseAscender(true);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            table.addCell(cell);
            /////////////////////////////////////////////////////////////////////
            
            
            cell = new PdfPCell(new Paragraph("",largeBoldFont));
            cell.setBorder(0);
            cell.setUseAscender(true);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            table.addCell(cell);
            
            
            
            //cell = new PdfPCell(new Paragraph("FACTURA No / INVOICE No.",negrita_pequena));
            cell = new PdfPCell(new Paragraph(this.getEtiqueta_tipo_doc(),negrita_pequena));
            cell.setBorder(0);
            cell.setUseAscender(true);
            cell.setHorizontalAlignment(Element.ALIGN_LEFT);
            table.addCell(cell);
            
            cell = new PdfPCell(new Paragraph(this.getSerie_folio(),smallFont));
            cell.setBorder(0);
            cell.setUseAscender(true);
            cell.setHorizontalAlignment(Element.ALIGN_LEFT);
            table.addCell(cell);
            
            
            ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
            
            
            cell = new PdfPCell(new Paragraph("CLIENTE: / CUSTOMER:",negrita_pequena));
            cell.setBorder(0);
            cell.setColspan(2);
            cell.setUseAscender(true);
            cell.setHorizontalAlignment(Element.ALIGN_LEFT);
            table.addCell(cell);
            
            
            cell = new PdfPCell(new Paragraph("",negrita_pequena));
            cell.setBorder(0);
            cell.setBorderWidthRight(1);
            cell.setUseAscender(true);
            cell.setHorizontalAlignment(Element.ALIGN_LEFT);
            table.addCell(cell);
            
            
            cell = new PdfPCell(new Paragraph("",largeBoldFont));
            cell.setBorder(0);
            cell.setUseAscender(true);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            table.addCell(cell);
            
            cell = new PdfPCell(new Paragraph("FECHA: / DATE:",negrita_pequena));
            cell.setBorder(0);
            cell.setUseAscender(true);
            cell.setHorizontalAlignment(Element.ALIGN_LEFT);
            table.addCell(cell);
            
            String fecha[] = this.getFacha_comprobante().split("-");
            
            cell = new PdfPCell(new Paragraph(fecha[2]+"/"+fecha[1]+"/"+fecha[0],smallFont));
            cell.setBorder(0);
            cell.setUseAscender(true);
            cell.setHorizontalAlignment(Element.ALIGN_LEFT);
            table.addCell(cell);
            
            ///////////////////////////////////////////////////////////////////////////////////////////////////////
            
            
            
            cell = new PdfPCell(new Paragraph(this.getReceptor_razon_social(),smallFont));
            cell.setBorder(0);
            cell.setColspan(3);
            cell.setBorderWidthRight(1);
            cell.setUseAscender(true);
            cell.setHorizontalAlignment(Element.ALIGN_LEFT);
            table.addCell(cell);
            
            cell = new PdfPCell(new Paragraph("",smallFont));
            cell.setBorder(0);
            cell.setUseAscender(true);
            cell.setHorizontalAlignment(Element.ALIGN_LEFT);
            table.addCell(cell);
            
            
            cell = new PdfPCell(new Paragraph("HORA:/HOUR:",negrita_pequena));
            cell.setBorder(0);
            cell.setUseAscender(true);
            cell.setHorizontalAlignment(Element.ALIGN_LEFT);
            table.addCell(cell);
            
            //hora
            cell = new PdfPCell(new Paragraph("",smallFont));
            cell.setBorder(0);
            cell.setUseAscender(true);
            cell.setHorizontalAlignment(Element.ALIGN_LEFT);
            table.addCell(cell);
            
            
            
            cell = new PdfPCell(new Paragraph("R.F.C. / TAX ID:",negrita_pequena));
            cell.setBorder(0);
            cell.setColspan(2);
            cell.setUseAscender(true);
            cell.setHorizontalAlignment(Element.ALIGN_LEFT);
            table.addCell(cell);
            
            
            
            cell = new PdfPCell(new Paragraph("",negrita_pequena));
            cell.setBorder(0);
            cell.setBorderWidthRight(1);
            cell.setUseAscender(true);
            cell.setHorizontalAlignment(Element.ALIGN_LEFT);
            table.addCell(cell);
            
            
            cell = new PdfPCell(new Paragraph("",smallFont));
            cell.setBorder(0);
            cell.setUseAscender(true);
            cell.setHorizontalAlignment(Element.ALIGN_LEFT);
            table.addCell(cell);
            
            
            
            
            cell = new PdfPCell(new Paragraph("",largeBoldFont));
            cell.setBorder(0);
            cell.setUseAscender(true);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            table.addCell(cell);
            
            
            cell = new PdfPCell(new Paragraph("",largeBoldFont));
            cell.setBorder(0);
            cell.setUseAscender(true);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            table.addCell(cell);
            /////////////////////////////////////////////////////////////////////////
            
            
            cell = new PdfPCell(new Paragraph(this.getReceptor_rfc(),smallFont));
            cell.setBorder(0);
            cell.setColspan(3);
            cell.setBorderWidthRight(1);
            cell.setUseAscender(true);
            cell.setHorizontalAlignment(Element.ALIGN_LEFT);
            table.addCell(cell);
            /*
            cell = new PdfPCell(new Paragraph("",negrita_pequena));
            cell.setBorder(0);
            
            cell.setUseAscender(true);
            cell.setHorizontalAlignment(Element.ALIGN_LEFT);
            table.addCell(cell);
            */
            cell = new PdfPCell(new Paragraph("",negrita_pequena));
            cell.setBorder(0);
            cell.setUseAscender(true);
            cell.setHorizontalAlignment(Element.ALIGN_LEFT);
            table.addCell(cell);
            
            cell = new PdfPCell(new Paragraph("",negrita_pequena));
            cell.setBorder(0);
            cell.setUseAscender(true);
            cell.setHorizontalAlignment(Element.ALIGN_LEFT);
            table.addCell(cell);
            
            cell = new PdfPCell(new Paragraph("",smallFont));
            cell.setBorder(0);
            cell.setUseAscender(true);
            cell.setHorizontalAlignment(Element.ALIGN_LEFT);
            table.addCell(cell);
            ///////////////////////////////////////////////////////////////////
            
            
            cell = new PdfPCell(new Paragraph("DIRECCION / ADRESS",negrita_pequena));
            cell.setBorder(0);
            cell.setUseAscender(true);
            cell.setColspan(2);
            cell.setHorizontalAlignment(Element.ALIGN_LEFT);
            table.addCell(cell);
            
            
            cell = new PdfPCell(new Paragraph("",negrita_pequena));
            cell.setBorder(0);
            cell.setBorderWidthRight(1);
            cell.setUseAscender(true);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            table.addCell(cell);
            
            
            cell = new PdfPCell(new Paragraph("EXPEDIDO EN:",negrita_pequena));
            cell.setBorder(0);
            cell.setUseAscender(true);
            cell.setHorizontalAlignment(Element.ALIGN_LEFT);
            cell.setColspan(3);
            table.addCell(cell);
            
            String municipioCliente="";
            String estadoCliente = "";
            
            if(this.getReceptor_rfc().equals("XXX000000000")){
                municipioCliente="X";
                estadoCliente = "X";
            }else{
                municipioCliente = this.getReceptor_municipio().toUpperCase();
                estadoCliente = this.getReceptor_estado().toUpperCase();
            }
            
            
            cell = new PdfPCell(new Paragraph( 
                    this.getReceptor_calle()+ " " + this.getReceptor_numero() +"\n"+ 
                    this.getReceptor_colonia().toUpperCase() + "\n" + 
                    municipioCliente + ", " + estadoCliente+", " + this.getReceptor_pais().toUpperCase()+ ".\n"+ 
                    "C.P. " + this.getReceptor_cp(), smallFont));
            
            cell.setBorder(0);
            cell.setColspan(3);
            cell.setBorderWidthRight(1);
            cell.setUseAscender(true);
            cell.setHorizontalAlignment(Element.ALIGN_LEFT);
            table.addCell(cell);
            
            /*
            cell = new PdfPCell(new Paragraph("",negrita_pequena));
            cell.setBorder(0);
            cell.setBorderWidthRight(1);
            cell.setUseAscender(true);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            table.addCell(cell);
            */
            
            
            cell = new PdfPCell(new Paragraph( this.getLugar_expedidion().toUpperCase(),smallFont));
            cell.setBorder(0);
            cell.setColspan(3);
            cell.setUseAscender(true);
            cell.setHorizontalAlignment(Element.ALIGN_LEFT);
            table.addCell(cell);
            
            /////////////////////////////////////////////////////////
            //fila vacia
            cell = new PdfPCell(new Paragraph("",smallFont));
            cell.setBorder(1);
            cell.setColspan(6);
            cell.setFixedHeight(8);
            cell.setUseAscender(true);
            cell.setHorizontalAlignment(Element.ALIGN_LEFT);
            table.addCell(cell);
            /////////////////////////////////////////////////////////
            
            cell = new PdfPCell(new Paragraph("NO. Y AÑO DE APROBACION :",negrita_pequena));
            cell.setBorder(0);
            cell.setBorderWidthRight(0);
            cell.setUseAscender(true);
            cell.setHorizontalAlignment(Element.ALIGN_LEFT);
            cell.setVerticalAlignment(Element.ALIGN_CENTER);
            table.addCell(cell);
            
            
            cell = new PdfPCell(new Paragraph(this.getNo_aprobacion() + "   "+this.getAno_aprobacion(),smallFont));
            cell.setBorder(0);
            cell.setBorderWidthLeft(0);
            cell.setUseAscender(true);
            cell.setHorizontalAlignment(Element.ALIGN_LEFT);
            cell.setVerticalAlignment(Element.ALIGN_CENTER);
            table.addCell(cell);
            
            
            cell = new PdfPCell(new Paragraph("NO. CERTIFICADO :",negrita_pequena));
            cell.setBorder(0);
            cell.setBorderWidthRight(0);
            cell.setUseAscender(true);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setVerticalAlignment(Element.ALIGN_CENTER);
            table.addCell(cell);
            
            
            cell = new PdfPCell(new Paragraph(this.getNo_certificado(),smallFont));
            cell.setBorder(0);
            cell.setBorderWidthLeft(0);
            cell.setColspan(3);
            cell.setUseAscender(true);
            cell.setHorizontalAlignment(Element.ALIGN_LEFT);
            cell.setVerticalAlignment(Element.ALIGN_CENTER);
            table.addCell(cell);
            /////////////////////////////////////////////////////////
            
            
            //fila vacia
            cell = new PdfPCell(new Paragraph("",smallFont));
            cell.setBorder(0);
            cell.setColspan(6);
            cell.setFixedHeight(5);
            cell.setUseAscender(true);
            cell.setHorizontalAlignment(Element.ALIGN_LEFT);
            table.addCell(cell);
            /////////////////////////////////////////////////////////
            
            
            
            cell = new PdfPCell(new Paragraph("CLIENTE NO.:/ CUSTOMER NO.:",negrita_pequena));
            cell.setUseAscender(true);
            cell.setHorizontalAlignment(Element.ALIGN_LEFT);
            table.addCell(cell);
            
            cell = new PdfPCell(new Paragraph("SU PEDIDO: / YOUR ORDER",negrita_pequena));
            cell.setUseAscender(true);
            cell.setHorizontalAlignment(Element.ALIGN_LEFT);
            table.addCell(cell);
            
            cell = new PdfPCell(new Paragraph("VENDEDOR: / SALESMAN:",negrita_pequena));
            cell.setColspan(4);
            cell.setUseAscender(true);
            cell.setHorizontalAlignment(Element.ALIGN_LEFT);
            table.addCell(cell);
            ////////////////////////////////////////////////////////////////////
            
            
            cell = new PdfPCell(new Paragraph(this.getReceptor_no_control(),smallFont));
            cell.setUseAscender(true);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            table.addCell(cell);
            
            
            cell = new PdfPCell(new Paragraph( this.getFolioPedido(),smallFont));
            cell.setUseAscender(true);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            table.addCell(cell);
            
            cell = new PdfPCell(new Paragraph(this.getVendedor(),smallFont));
            cell.setColspan(4);
            cell.setUseAscender(true);
            cell.setHorizontalAlignment(Element.ALIGN_LEFT);
            table.addCell(cell);
            
            ///&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&
            cell = new PdfPCell(new Paragraph("",smallFont));
            cell.setBorder(0);
            cell.setColspan(6);
            cell.setFixedHeight(10);
            cell.setUseAscender(true);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setVerticalAlignment(Element.ALIGN_CENTER);
            table.addCell(cell);
            
            
            //&&&&&&&&&&&&&&&&&&&&        TABLA DE  LOS CONCEPTOS        &&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&
            cell = new PdfPCell(cepdf.addContent());
            cell.setColspan(6);
            
            cell.setFixedHeight(210);
            cell.setBorderWidthBottom(1);
            cell.setBorderWidthLeft(1);
            cell.setBorderWidthTop(1);
            cell.setBorderWidthRight(1);
            cell.setUseAscender(true);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setVerticalAlignment(Element.ALIGN_TOP);
            table.addCell(cell);
            //&&&&&&&&&&&&&&&&&&&&&&&&&    FIN DE LA TABLE DE CONCEPTOS  &&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&6666
            
            
            cell = new PdfPCell(new Paragraph("OBSERVACIONES:",negrita_pequena));
            cell.setBorder(0);
            cell.setColspan(6);
            cell.setBorderWidthBottom(0);
            cell.setUseAscender(true);
            cell.setHorizontalAlignment(Element.ALIGN_LEFT);
            table.addCell(cell);
            
            String cuentas="";
            
            if ( !this.getEmisora_rfc().equals("KCM081010I58") && !this.getEmisora_rfc().equals("KME010221CB4")){
                cuentas="";
            }else{
                cuentas="\nDEPOSITAR EN BANORTE (MN) Cta. 0587326205 CLABE 072580005873262052, (USD) Cta. 0557037045 CLABE 072580005570370454";
            }
            
            System.out.println("table.size4: "+table.size());
            System.out.println("table.getTotalHeight4: "+table.getTotalHeight());
            cell = new PdfPCell(new Paragraph(this.getObservaciones()+cuentas,smallFont));
            cell.setBorder(0);
            cell.setColspan(6);
            cell.setBorderWidthTop(0);
            cell.setBorderWidthBottom(0);
            //cell.setFixedHeight(60);
            cell.setUseAscender(true);
            cell.setHorizontalAlignment(Element.ALIGN_LEFT);
            cell.setVerticalAlignment(Element.ALIGN_TOP);
            table.addCell(cell);
            
            System.out.println("table.size5: "+table.size());
            System.out.println("table.getTotalHeight5: "+table.getTotalHeight());
/*
            if(!this.getObservaciones().equals("LUGAR DE ENTREGA: ")){
                cell = new PdfPCell(new Paragraph(this.getObservaciones(),smallFont));
                cell.setBorder(0);
                cell.setColspan(6);
                cell.setBorderWidthTop(0);
                cell.setBorderWidthBottom(0);
                cell.setFixedHeight(20);
                cell.setUseAscender(true);
                cell.setHorizontalAlignment(Element.ALIGN_LEFT);
                table.addCell(cell); 
            }else{
                cell = new PdfPCell(new Paragraph("",smallFont));
                cell.setBorder(0);
                cell.setColspan(6);
                cell.setBorderWidthTop(0);
                cell.setBorderWidthBottom(0);
                cell.setUseAscender(true);
                cell.setHorizontalAlignment(Element.ALIGN_LEFT);
                table.addCell(cell); 
            }
*/
            cell = new PdfPCell(new Paragraph("",smallFont));
            cell.setBorder(0);
            cell.setColspan(6);
            cell.setBorderWidthTop(0);
            cell.setBorderWidthBottom(0);
            cell.setFixedHeight(10);
            cell.setUseAscender(true);
            cell.setHorizontalAlignment(Element.ALIGN_LEFT);
            table.addCell(cell);
            
            /*
            cell = new PdfPCell(cepdffother.addContent(this.getSubTotal(), this.getMontoImpuesto(), this.getMontoRetencion(), this.getMontoTotal()   ));
            cell.setColspan(6);
            cell.setUseAscender(true);
            cell.setHorizontalAlignment(Element.ALIGN_LEFT);
            table.addCell(cell);
            */
            
            document.add(table);
            System.out.println("table.size6: "+table.size());
            System.out.println("table.getTotalHeight6: "+table.getTotalHeight());
            
            //aqui comienza la tabla de totales
            float [] widths_table_totales = {1};
            table_totales = new PdfPTable(widths_table_totales);
            table_totales.setKeepTogether(false);
            
            cell = new PdfPCell(cepdffother.addContent(this.getSubTotal(), this.getMontoImpuesto(), this.getMontoRetencion(), this.getMontoTotal()   ));
            cell.setColspan(6);
            cell.setUseAscender(true);
            cell.setVerticalAlignment(Element.ALIGN_TOP);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            table_totales.addCell(cell);
            
            table_totales.setSpacingBefore(dias);
            table_totales.setSpacingBefore(1);
            
            document.add(table_totales);
            
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
        
    }//termina factura

    
    
    
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
                img.scaleAbsoluteHeight(100);
                img.scaleAbsoluteWidth(145);
                //img.setAlignment(0);
                img.setAlignment(Element.ALIGN_CENTER);
            }
            catch(Exception e){
                System.out.println(e);
            }
            return img;
        }
    }
    
    
    
    
     public String getImagen_cedula() {
        return imagen_cedula;
    }

    public void setImagen_cedula(String imagen_cedula) {
        this.imagen_cedula = imagen_cedula;
    }
    
    
    private class CedulaPDF {
        public Image addContent(int alto, int ancho) {
            Image img = null;
            //alto=130  ancho=85
            try {
                img = Image.getInstance(getImagen_cedula());
                img.scaleAbsoluteHeight(alto);
                img.scaleAbsoluteWidth(ancho);
                img.setAlignment(Element.ALIGN_CENTER);
                img.setSpacingBefore(6);
                //img.setAlignment(0);
            }
            catch(Exception e){
                System.out.println(e);
            }
            return img;
        }
    }
    
    
    
    
    private class CeldaPDF {
       public PdfPTable addContent() {
            Font smallsmall = new Font(Font.FontFamily.HELVETICA,5,Font.NORMAL,BaseColor.BLACK);
            Font smallFont = new Font(Font.FontFamily.HELVETICA,8,Font.NORMAL,BaseColor.BLACK);
            
            Font negrita_pequena= new Font(Font.FontFamily.HELVETICA,6,Font.BOLD,BaseColor.BLACK);
             Font smallBoldFont = new Font(Font.FontFamily.HELVETICA, 6, Font.BOLD, BaseColor.WHITE);
            //INICIO DE LA TABLA DE PEDIDO ENTREGADO ??
            
            float [] anchocolumnas = {1f, 2.3f, 1f, 1f, 0.5f,1f,1f, 0.5f,1.5f};
            String moneda="";
            PdfPTable table = new PdfPTable(anchocolumnas);
            table.setKeepTogether(true);
            String[] columnas;
            String[] columnas1 = {"CLAVE\nCODE","DESCRIPCION\nDESCRIPTION","No. LOTE\nLOT No.","TOTAL KGS\nTOTAL KGS"," ","PRECIO KG\nPRICE KG","MONEDA\nCURRENCY"," ","TOTAL\nEXTENDED VALUE"};
            String[] columnas2 = {"CLAVE\nCODE","DESCRIPCION\nDESCRIPTION","No. LOTE\nLOT No.","CANTIDAD.\nQUANTITY"," ","PRECIO\nPRICE","MONEDA\nCURRENCY"," ","TOTAL\nEXTENDED VALUE"};
            
            if(getTipo_facturacion().equals("cfd")){
                columnas = columnas1;
            }else{
                columnas = columnas2;
            }
            
            List<String>  lista_columnas = (List<String>) Arrays.asList(columnas);
            Integer contador = 0;
           PdfPCell cellX;
           
           for ( String columna_titulo : lista_columnas){
                    
                cellX = new PdfPCell(new Paragraph(columna_titulo,smallBoldFont));

                cellX.setUseAscender(true);
                cellX.setUseDescender(true);
                cellX.setBackgroundColor(BaseColor.BLACK);
                //cellX.setBorder(1);
                cellX.setFixedHeight(16);
                //cellX.setBorder(11);
                //cellX.setBorder(12);
                if(columna_titulo.equals("CLAVE\nCODE")){
                    cellX.setHorizontalAlignment(Element.ALIGN_LEFT);
                    cellX.setVerticalAlignment(Element.ALIGN_TOP);
                }
                if(columna_titulo.equals("DESCRIPCION\nDESCRIPTION")){
                    cellX.setHorizontalAlignment(Element.ALIGN_LEFT);
                    cellX.setVerticalAlignment(Element.ALIGN_TOP);
                }
                if(columna_titulo.equals("No. LOTE\nLOT No.")){
                    cellX.setHorizontalAlignment(Element.ALIGN_CENTER);
                    cellX.setVerticalAlignment(Element.ALIGN_TOP);
                }
                
                if(getTipo_facturacion().equals("cfd")){
                    if(columna_titulo.equals("TOTAL KGS\nTOTAL KGS")){
                        cellX.setHorizontalAlignment(Element.ALIGN_CENTER);
                        cellX.setVerticalAlignment(Element.ALIGN_TOP);
                    }                
                    
                    if(columna_titulo.equals("PRECIO KG\nPRICE KG")){
                        cellX.setHorizontalAlignment(Element.ALIGN_CENTER);
                        cellX.setVerticalAlignment(Element.ALIGN_TOP);
                    }
                }else{
                    if(columna_titulo.equals("CANTIDAD.\nQUANTITY")){
                        cellX.setHorizontalAlignment(Element.ALIGN_CENTER);
                        cellX.setVerticalAlignment(Element.ALIGN_TOP);
                    }                

                    if(columna_titulo.equals("PRECIO\nPRICE")){
                        cellX.setHorizontalAlignment(Element.ALIGN_CENTER);
                        cellX.setVerticalAlignment(Element.ALIGN_TOP);
                    }
                }

                
                if(columna_titulo.equals("MONEDA\nCURRENCY")){
                    cellX.setHorizontalAlignment(Element.ALIGN_CENTER);
                    cellX.setVerticalAlignment(Element.ALIGN_TOP);
                }
                
                if(columna_titulo.equals("TOTAL\nEXTENDED VALUE")){
                    cellX.setHorizontalAlignment(Element.ALIGN_CENTER);
                    cellX.setVerticalAlignment(Element.ALIGN_TOP);
                }

                table.addCell(cellX);
            }
           
           for (HashMap<String, String> registro : getRows()){
            
                    //Indices del HashMap que representa el row
                    String[] wordList = {"sku","titulo","numero_lote","cantidad","denominacion","precio_unitario","moneda","denominacion","importe"};
                   
                    List<String>  indices = (List<String>) Arrays.asList(wordList);
                    
                    for (String omega : indices){
                        PdfPCell celda;
                        
                        
                        if (omega.equals("sku")){
                            celda = new PdfPCell(new Paragraph(registro.get(omega),smallFont));
                            celda.setHorizontalAlignment(Element.ALIGN_LEFT);
                            celda.setVerticalAlignment(Element.ALIGN_TOP);
                            celda.setBorder(0);
                            table.addCell(celda);
                        }
                        
                        if (omega.equals("titulo")){
                            celda = new PdfPCell(new Paragraph(registro.get(omega).toUpperCase(),smallFont));
                            //celda= new PdfPCell(new Paragraph(StringHelper.AgregaComas(StringHelper.roundDouble(registro.get(omega).toString(),2)),smallFont));
                            celda.setHorizontalAlignment(Element.ALIGN_LEFT);
                            celda.setVerticalAlignment(Element.ALIGN_TOP);
                            celda.setBorder(0);
                            table.addCell(celda);
                        }
                        
                        if (omega.equals("numero_lote")){
                            //celda = new PdfPCell(new Paragraph(registro.get(omega),smallFont));
                            celda= new PdfPCell(new Paragraph(registro.get(omega).toString(),smallFont));
                            celda.setHorizontalAlignment(Element.ALIGN_CENTER);
                            celda.setVerticalAlignment(Element.ALIGN_TOP);
                            celda.setBorder(0);
                            table.addCell(celda);
                        }
                        
                        if (omega.equals("cantidad")){
                            //celda = new PdfPCell(new Paragraph(registro.get(omega),smallFont));
                            celda= new PdfPCell(new Paragraph(StringHelper.AgregaComas(StringHelper.roundDouble(registro.get(omega).toString(),2)),smallFont));
                            celda.setHorizontalAlignment(Element.ALIGN_RIGHT);
                            celda.setVerticalAlignment(Element.ALIGN_TOP);
                            celda.setBorder(0);
                            table.addCell(celda);
                        }
                        if (omega.equals("denominacion")){
                           
                                celda = new PdfPCell(new Paragraph( "$",smallFont));
                                celda.setHorizontalAlignment(Element.ALIGN_RIGHT);
                                celda.setVerticalAlignment(Element.ALIGN_TOP);
                                celda.setBorder(0);
                                table.addCell(celda);
                            
                        }
                        
                        
                        if (omega.equals("precio_unitario")){
                            //celda = new PdfPCell(new Paragraph(registro.get(omega),smallFont));
                            celda= new PdfPCell(new Paragraph(StringHelper.AgregaComas(StringHelper.roundDouble(registro.get(omega).toString(),2)),smallFont));
                            celda.setHorizontalAlignment(Element.ALIGN_RIGHT);
                            celda.setVerticalAlignment(Element.ALIGN_TOP);
                            celda.setBorder(0);
                            table.addCell(celda);
                        }
                        
                        if (omega.equals("moneda")){
                            celda = new PdfPCell(new Paragraph(registro.get(omega).toUpperCase(),smallFont));
                            celda.setHorizontalAlignment(Element.ALIGN_CENTER);
                            celda.setVerticalAlignment(Element.ALIGN_TOP);
                            celda.setBorder(0);
                            table.addCell(celda);
                         }
                        
                        
                        
                        if (omega.equals("importe")){
                            celda= new PdfPCell(new Paragraph(StringHelper.AgregaComas(StringHelper.roundDouble(registro.get(omega).toString(),2)),smallFont));
                            celda.setHorizontalAlignment(Element.ALIGN_RIGHT);
                            celda.setVerticalAlignment(Element.ALIGN_TOP);
                            celda.setBorder(0);
                            table.addCell(celda);
                         }
                         
                    
                }
                
                contador++;
                
            }
           
           
            return table;
        }
    }
    
    
    
    
    
    
    
    
    /////////////////////////////////////////////(((((((((((((((((((((((((
    private class CeldaFother {
        
       public PdfPTable addContent(String SubTotal, String MontoImpuesto, String MontoRetencion, String MontoTotal) {
            Font smallsmall = new Font(Font.FontFamily.HELVETICA,5,Font.NORMAL,BaseColor.BLACK);
            Font smallFont = new Font(Font.FontFamily.HELVETICA,8,Font.NORMAL,BaseColor.BLACK);
            
            Font negrita_pequeña= new Font(Font.FontFamily.HELVETICA,6,Font.BOLD,BaseColor.BLACK);
            Font Fontssello__aviso = new Font(Font.FontFamily.HELVETICA,5,Font.NORMAL,BaseColor.BLACK);
            //INICIO DE LA TABLA DE PEDIDO ENTREGADO ??
            CedulaPDF icedula = new CedulaPDF();
            HashMap<String, String> datos_encabezado = getEncabezado();
            
            
            
            float [] anchocolumnas = {4,8,4,0.7f,4};
            PdfPTable table = new PdfPTable(anchocolumnas);
            PdfPCell cell;
            int alto=136,  ancho=85;
            
            if(getTipo_facturacion().equals("cfditf")){
                alto=160; ancho=87;
            }
            
            cell = new PdfPCell(icedula.addContent(alto,ancho));
            
            if(getTipo_facturacion().equals("cfd")){
            //if(getTipo_facturacion().equals("cfditf")){
                cell.setRowspan(15);
            }
            
            if(getTipo_facturacion().equals("cfditf")){
            //if(getTipo_facturacion().equals("cfd")){
                cell.setRowspan(17);
            }
            
            //cell.setUseAscender(true);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setVerticalAlignment(Element.ALIGN_CENTER);
            cell.setBorderWidthBottom(1);
            cell.setBorderWidthTop(1);
            cell.setBorderWidthLeft(1);
            cell.setBorderWidthRight(1);
            table.addCell(cell);
            
            cell = new PdfPCell(new Paragraph("CANTIDAD CON LETRA:",negrita_pequeña));
            cell.setColspan(4);
            cell.setUseAscender(true);
            cell.setHorizontalAlignment(Element.ALIGN_LEFT);
            cell.setBorderWidthBottom(0);
            cell.setBorderWidthTop(0);
            table.addCell(cell);
            
            /*
            String sumaTotal="";
            double  Total=0.0;
            
            String subtotal = datos_encabezado.get("subtotal");
            String iva = datos_encabezado.get("iva");
            String iva_retenido = datos_encabezado.get("iva_retenido");
            
            Total = Double.parseDouble(subtotal.toString()) + Double.parseDouble(iva.toString()) - Double.parseDouble(iva_retenido.toString());
            
            
            sumaTotal=String.valueOf(Total); //convirtiendo double a integer
            //sumaTotal="12312323";
            */
            
            BigInteger num = new BigInteger(MontoTotal.split("\\.")[0]);
            n2t cal = new n2t();
            String centavos = MontoTotal.substring(MontoTotal.indexOf(".")+1);
            String numero = cal.convertirLetras(num);
            
            //convertir a mayuscula la primera letra de la cadena
            String numeroMay = numero.substring(0, 1).toUpperCase() + numero.substring(1, numero.length());


            String denom = "";
            String denominacion="";
            
            /*
            String denominacion=datos_encabezado.get("moneda");
            System.out.print("Esta es la denominacion "+denominacion);



            if(denominacion.equals("PESOS")){
                denominacion = "pesos";
                denom = "M.N.";
            }
            if(denominacion.equals("USD")){
                denominacion = "dolares";
                //denom = "USCY";
                denom = "USD";
            }
            */
            
            
            denominacion = getTitulo_moneda();
            denom = getMoneda_abr();
            //getSimbolo_moneda();

            if(centavos.equals(num.toString())){
                centavos="00";
            }
            
            cell = new PdfPCell(new Paragraph(numeroMay.toUpperCase() + " " + denominacion.toUpperCase() + " " +centavos+"/100 "+ denom.toUpperCase(), smallFont));
            cell.setColspan(4);
            cell.setUseAscender(true);
            cell.setHorizontalAlignment(Element.ALIGN_LEFT);
            cell.setBorderWidthTop(0);
            table.addCell(cell);
            System.out.println("Se ha agregado importe con letras al pdf de factura");
            
            
            
            //////////////////////////////////////////////////////////////7

            if (getProposito().equals("FACTURA")){
                cell = new PdfPCell(new Paragraph("FECHA DE PAGO / PAYMENT DUE",negrita_pequeña));
                cell.setUseAscender(true);
                cell.setHorizontalAlignment(Element.ALIGN_LEFT);
                cell.setBorderWidthTop(0);
                cell.setBorderWidthBottom(0);
                table.addCell(cell);
            }else{
                cell = new PdfPCell(new Paragraph("",smallFont));
                cell.setUseAscender(true);
                cell.setHorizontalAlignment(Element.ALIGN_LEFT);
                cell.setBorderWidthTop(0);
                cell.setBorderWidthBottom(0);
                table.addCell(cell);
            }
            
            
            cell = new PdfPCell(new Paragraph("",smallFont));
            cell.setUseAscender(true);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setBorderWidthTop(0);
            cell.setBorderWidthBottom(0);
            table.addCell(cell);
            
            cell = new PdfPCell(new Paragraph("",smallFont));
            cell.setUseAscender(true);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setBorderWidthTop(0);
            cell.setBorderWidthBottom(0);
            cell.setBorderWidthRight(0);
            table.addCell(cell);
            
            cell = new PdfPCell(new Paragraph("",smallFont));
            cell.setUseAscender(true);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setBorderWidthTop(0);
            cell.setBorderWidthBottom(0);
            cell.setBorderWidthLeft(0);
            table.addCell(cell);
            
            
   
            ////////////////////////////////////////////
            //Aqui agregamos la fecha de pago
            if (getProposito().equals("FACTURA")){
                cell = new PdfPCell(new Paragraph(getFecha_pago(),smallFont));
                cell.setUseAscender(true);
                cell.setHorizontalAlignment(Element.ALIGN_LEFT);
                cell.setBorderWidthTop(0);
                table.addCell(cell);
            }else{
                cell = new PdfPCell(new Paragraph("",smallFont));
                cell.setUseAscender(true);
                cell.setHorizontalAlignment(Element.ALIGN_LEFT);
                cell.setBorderWidthTop(0);
                cell.setBorderWidthBottom(0);
                table.addCell(cell);
            }
            cell = new PdfPCell(new Paragraph("SUBTOTAL:",negrita_pequeña));
            cell.setUseAscender(true);
            cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            cell.setBorderWidthTop(0);
            cell.setBorderWidthBottom(0);
            table.addCell(cell);
            
            cell = new PdfPCell(new Paragraph("$",smallFont));
            cell.setUseAscender(true);
            cell.setHorizontalAlignment(Element.ALIGN_RIGHT);//cell.setBorder(0);
            cell.setBorderWidthTop(0);
            cell.setBorderWidthRight(0);
            cell.setBorderWidthBottom(0);
            table.addCell(cell);
            
            cell = new PdfPCell(new Paragraph(StringHelper.AgregaComas(StringHelper.roundDouble( getSubTotal() ,2)),smallFont));
            cell.setUseAscender(true);
            cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            cell.setBorderWidthLeft(0);
            cell.setBorderWidthTop(0);
            cell.setBorderWidthBottom(0);
            table.addCell(cell);
            
            
            
            
            ////////////////////////////////////////////////
            if (getProposito().equals("FACTURA")){
                cell = new PdfPCell(new Paragraph("TERMINOS DE PAGO / PAYMENT TERMS",negrita_pequeña));
                cell.setUseAscender(true);
                cell.setHorizontalAlignment(Element.ALIGN_LEFT);
                cell.setBorderWidthBottom(0);
                table.addCell(cell);
            }else{
                cell = new PdfPCell(new Paragraph("",smallFont));
                cell.setUseAscender(true);
                cell.setHorizontalAlignment(Element.ALIGN_LEFT);
                cell.setBorderWidthTop(0);
                cell.setBorderWidthBottom(0);
                table.addCell(cell);
            }
            
            
            cell = new PdfPCell(new Paragraph("",smallFont));
            cell.setUseAscender(true);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setBorderWidthBottom(0);
            cell.setBorderWidthTop(0);
            table.addCell(cell);
            
            cell = new PdfPCell(new Paragraph("",smallFont));
            cell.setUseAscender(true);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setBorderWidthBottom(0);
            cell.setBorderWidthTop(0);
            cell.setBorderWidthRight(0);
            table.addCell(cell);
            
            cell = new PdfPCell(new Paragraph("",smallFont));
            cell.setUseAscender(true);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setBorderWidthBottom(0);
            cell.setBorderWidthTop(0);
            cell.setBorderWidthLeft(0);
            table.addCell(cell);
            ////////////////////////////////////////////////
            
            
            
            ////////Aqui van los terminos de pago
            if (getProposito().equals("FACTURA")){
                cell = new PdfPCell(new Paragraph( getTerminos() ,smallFont ));
                cell.setUseAscender(true);
                cell.setHorizontalAlignment(Element.ALIGN_LEFT);
                cell.setBorderWidthTop(0);
                cell.setBorderWidthBottom(0);
                table.addCell(cell);
            }else{
                cell = new PdfPCell(new Paragraph("",smallFont));
                cell.setUseAscender(true);
                cell.setHorizontalAlignment(Element.ALIGN_LEFT);
                cell.setBorderWidthTop(0);
                cell.setBorderWidthBottom(0);
                table.addCell(cell);
            }
            
            
            
            cell = new PdfPCell(new Paragraph("IVA / V.A.T:",negrita_pequeña));
            cell.setUseAscender(true);
            cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            cell.setBorderWidthTop(0);
            cell.setBorderWidthBottom(0);
            table.addCell(cell);
            
            cell = new PdfPCell(new Paragraph("$",smallFont));
            cell.setUseAscender(true);
            cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            cell.setBorderWidthRight(0);
            cell.setBorderWidthTop(0);
            cell.setBorderWidthBottom(0);
            table.addCell(cell);
            
            cell = new PdfPCell(new Paragraph(StringHelper.AgregaComas(StringHelper.roundDouble( getMontoImpuesto() ,2))   ,smallFont));
            cell.setUseAscender(true);
            cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            cell.setBorderWidthTop(0);
            cell.setBorderWidthLeft(0);
            cell.setBorderWidthBottom(0);
            table.addCell(cell);
            ////////////////////////////////////////////////
            
            
            
            
            
            
            ////////////////////////////////////////////////
            if (getProposito().equals("FACTURA")){
                cell = new PdfPCell(new Paragraph("METODO DE PAGO:  /  No. CUENTA",negrita_pequeña));
                cell.setUseAscender(true);
                cell.setHorizontalAlignment(Element.ALIGN_LEFT);
                cell.setBorderWidthTop(1);
                cell.setBorderWidthBottom(0);
                table.addCell(cell);
            }else{
                cell = new PdfPCell(new Paragraph("",smallFont));
                cell.setUseAscender(true);
                cell.setHorizontalAlignment(Element.ALIGN_LEFT);
                cell.setBorderWidthTop(0);
                cell.setBorderWidthBottom(0);
                table.addCell(cell);
            }
            
            
            cell = new PdfPCell(new Paragraph("",smallFont));
            cell.setUseAscender(true);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setBorderWidthTop(0);
            cell.setBorderWidthBottom(0);
            table.addCell(cell);
            
            cell = new PdfPCell(new Paragraph("",smallFont));
            cell.setUseAscender(true);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setBorderWidthBottom(0);
            cell.setBorderWidthTop(0);
            cell.setBorderWidthRight(0);
            table.addCell(cell);
            
            cell = new PdfPCell(new Paragraph("",smallFont));
            cell.setUseAscender(true);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setBorderWidthBottom(0);
            cell.setBorderWidthTop(0);
            cell.setBorderWidthLeft(0);
            table.addCell(cell);
            ////////////////////////////////////////////////
            
            

            ////////Aqui va el metodo de pago
            if (getProposito().equals("FACTURA")){
                cell = new PdfPCell(new Paragraph(  getMetodo_pago() +"   "+ getNo_cuenta(),smallFont));
                cell.setUseAscender(true);
                cell.setHorizontalAlignment(Element.ALIGN_LEFT);
                cell.setBorderWidthTop(0);
                cell.setBorderWidthBottom(1);
                table.addCell(cell);
            }else{
                cell = new PdfPCell(new Paragraph("",smallFont));
                cell.setUseAscender(true);
                cell.setHorizontalAlignment(Element.ALIGN_LEFT);
                cell.setBorderWidthTop(0);
                cell.setBorderWidthBottom(0);
                table.addCell(cell);
            }
            
            
            cell = new PdfPCell(new Paragraph("IVA RETENIDO:",negrita_pequeña));
            cell.setUseAscender(true);
            cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            cell.setBorderWidthTop(0);
            cell.setBorderWidthBottom(0);
            table.addCell(cell);
            
            cell = new PdfPCell(new Paragraph("$",smallFont));
            cell.setUseAscender(true);
            cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            cell.setBorderWidthTop(0);
            cell.setBorderWidthRight(0);
            cell.setBorderWidthBottom(0);
            table.addCell(cell);
            
            cell = new PdfPCell(new Paragraph(StringHelper.AgregaComas(StringHelper.roundDouble( getMontoRetencion() ,2))  ,smallFont));
            cell.setUseAscender(true);
            cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            cell.setBorderWidthTop(0);
            cell.setBorderWidthBottom(0);
            cell.setBorderWidthLeft(0);
            table.addCell(cell);
            ////////////////////////////////////////////////
            
            
            
           ////////////////////////////////////////////////
            cell = new PdfPCell(new Paragraph("",smallFont));
            cell.setUseAscender(true);
            cell.setHorizontalAlignment(Element.ALIGN_LEFT);
            cell.setBorderWidthBottom(0);
            cell.setBorderWidthTop(0);
            table.addCell(cell);
            
            cell = new PdfPCell(new Paragraph("",smallFont));
            cell.setUseAscender(true);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setBorderWidthBottom(0);
            cell.setBorderWidthTop(0);
            table.addCell(cell);
            
            cell = new PdfPCell(new Paragraph("",smallFont));
            cell.setUseAscender(true);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setBorderWidthBottom(0);
            cell.setBorderWidthTop(0);
            cell.setBorderWidthRight(0);
            table.addCell(cell);
            
            cell = new PdfPCell(new Paragraph("",smallFont));
            cell.setUseAscender(true);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setBorderWidthLeft(0);
            cell.setBorderWidthBottom(0);
            cell.setBorderWidthTop(0);
            table.addCell(cell);
            ////////////////////////////////////////////////
            
            
            
            
            
            
            ////////////////////////////////////////////////
            cell = new PdfPCell(new Paragraph("",smallFont));
            cell.setUseAscender(true);
            cell.setHorizontalAlignment(Element.ALIGN_LEFT);
            cell.setBorderWidthTop(0);
            cell.setBorderWidthBottom(0);
            table.addCell(cell);
            
            cell = new PdfPCell(new Paragraph("TOTAL:",negrita_pequeña));
            cell.setUseAscender(true);
            cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            cell.setBorderWidthTop(0);
            cell.setBorderWidthBottom(0);
            table.addCell(cell);
            
            cell = new PdfPCell(new Paragraph("$",smallFont));
            cell.setUseAscender(true);
            cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            cell.setBorderWidthTop(0);
            cell.setBorderWidthBottom(0);
            cell.setBorderWidthRight(0);
            table.addCell(cell);
            
            cell = new PdfPCell(new Paragraph(StringHelper.AgregaComas(StringHelper.roundDouble( getMontoTotal() ,2)) ,smallFont));
            cell.setUseAscender(true);
            cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            cell.setBorderWidthTop(0);
            cell.setBorderWidthLeft(0);
            cell.setBorderWidthBottom(0);
            table.addCell(cell);
            
            ////////////////////////////////////////////////
            
            
            
            
            
            cell = new PdfPCell(new Paragraph("CADENA ORIGINAL:",negrita_pequeña));
            cell.setColspan(5);
            cell.setUseAscender(true);
            cell.setHorizontalAlignment(Element.ALIGN_LEFT);
            cell.setBorderWidthBottom(0);
            //cell.setBorderWidthTop(0);
            table.addCell(cell);
            
            cell = new PdfPCell(new Paragraph(  getCadena_original()  ,Fontssello__aviso));
            cell.setColspan(5);
            cell.setRowspan(2);
            cell.setFixedHeight(35);
            cell.setUseAscender(true);
            cell.setHorizontalAlignment(Element.ALIGN_LEFT);
            cell.setBorderWidthTop(0);
            table.addCell(cell);
            
            cell = new PdfPCell(new Paragraph("SELLO DIGITAL DEL EMISOR:",negrita_pequeña));
            cell.setColspan(5);
            cell.setUseAscender(true);
            cell.setHorizontalAlignment(Element.ALIGN_LEFT);
            cell.setBorderWidthBottom(0);
            cell.setBorderWidthTop(0);
            table.addCell(cell);
            
            cell = new PdfPCell(new Paragraph(  getSello_digital(),Fontssello__aviso));
            cell.setColspan(5);
            cell.setFixedHeight(15);
            cell.setUseAscender(true);
            cell.setHorizontalAlignment(Element.ALIGN_LEFT);
            cell.setBorderWidthTop(0);
            table.addCell(cell);
            
            //if(getTipo_facturacion().equals("cfd")){
            if(getTipo_facturacion().equals("cfditf")){
                cell = new PdfPCell(new Paragraph("SELLO DIGITAL DEL SAT:",negrita_pequeña));
                cell.setColspan(5);
                cell.setUseAscender(true);
                cell.setHorizontalAlignment(Element.ALIGN_LEFT);
                cell.setBorderWidthBottom(0);
                cell.setBorderWidthTop(0);
                table.addCell(cell);
                
                cell = new PdfPCell(new Paragraph( getSello_digital_sat(),Fontssello__aviso));
                cell.setColspan(5);
                cell.setFixedHeight(15);
                cell.setUseAscender(true);
                cell.setHorizontalAlignment(Element.ALIGN_LEFT);
                cell.setBorderWidthTop(0);
                table.addCell(cell);
            }
            
            
            cell = new PdfPCell(new Paragraph("EFECTOS FISCALES AL PAGO. PAGO EN UNA SOLA EXHIBICION. ESTE DOCUMENTO ES UNA REPRESENTACION IMPRESA DE UN CFD",Fontssello__aviso));
            cell.setColspan(5);
            cell.setUseAscender(true);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            table.addCell(cell);
            
            cell = new PdfPCell(new Paragraph("AVISO DE PRIVACIDAD",Fontssello__aviso));
            cell.setColspan(5);
            cell.setUseAscender(true);
            cell.setHorizontalAlignment(Element.ALIGN_LEFT);
            cell.setBorderWidthBottom(0);
            table.addCell(cell);
            
            //cell = new PdfPCell(new Paragraph("Los datos personales que proporciona a "+ getEmpresa_emisora()+" Nombre,Domicilio,Registro Federal de Contrubuyentes,Correo Electronico son unica y exclusivamente para uso de facturacion, y no seran usados con fines de divulgacion o utilizacion comercial "+datos_encabezado.get("razon_social_expide_remision")+" Domicilio: "+datos_encabezado.get("calle")+" "+datos_encabezado.get("numero")+" "+datos_encabezado.get("colonia") +" "+datos_encabezado.get("municipio") +" "+datos_encabezado.get("Estado")+"  Lo anterior en los terminos del Articulo 2 Fraccion II de la ley Federal de Proteccion de Datos Personales",Fontssello__aviso));
            cell = new PdfPCell(new Paragraph("Los datos personales que proporciona a "+ getEmpresa_emisora()+" Nombre,Domicilio,Registro Federal de Contrubuyentes,Correo Electronico son unica y exclusivamente para uso de facturacion, y no seran usados con fines de divulgacion o utilizacion comercial.   Lo anterior en los terminos del Articulo 2 Fraccion II de la ley Federal de Proteccion de Datos Personales",Fontssello__aviso));
            cell.setColspan(5);
            cell.setUseAscender(true);
            cell.setHorizontalAlignment(Element.ALIGN_LEFT);
            cell.setBorderWidthTop(0);
            table.addCell(cell);
            
            
            return table;
        }
    }
    
    ///%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%55
     public String esteAtributoSeDejoNulo(String atributo){
         return (atributo != null) ? (atributo) : new String();
    }
    
     
     
     
    public String getCadena_original() {
        return cadena_original;
    }

    public void setCadena_original(String cadena_original) {
        this.cadena_original = cadena_original;
    }

    public int getDias() {
        return dias;
    }

    public void setDias(int dias) {
        this.dias = dias;
    }

    public String getEmpresa_emisora() {
        return empresa_emisora;
    }

    public void setEmpresa_emisora(String empresa_emisora) {
        this.empresa_emisora = empresa_emisora;
    }

    public String getFacha_comprobante() {
        return facha_comprobante;
    }

    public void setFacha_comprobante(String facha_comprobante) {
        this.facha_comprobante = facha_comprobante;
    }

    public String getOrdenCompra() {
        return ordenCompra;
    }

    public void setOrdenCompra(String ordenCompra) {
        this.ordenCompra = ordenCompra;
    }

    public String getProposito() {
        return proposito;
    }

    public void setProposito(String proposito) {
        this.proposito = proposito;
    }

    public String getSello_digital() {
        return sello_digital;
    }

    public void setSello_digital(String sello_digital) {
        this.sello_digital = sello_digital;
    }

    public String getSerie_folio() {
        return serie_folio;
    }

    public void setSerie_folio(String serie_folio) {
        this.serie_folio = serie_folio;
    }

    public String getTerminos() {
        return terminos;
    }

    public void setTerminos(String terminos) {
        this.terminos = terminos;
    }

    public String getVendedor() {
        return vendedor;
    }

    public void setVendedor(String vendedor) {
        this.vendedor = vendedor;
    }

    
    public HashMap<String, String> getEncabezado() {
        return encabezado;
    }

    public void setEncabezado(HashMap<String, String> encabezado) {
        this.encabezado = encabezado;
    }
    
    public ArrayList<HashMap<String, String>> getRows() {
        return rows;
    }
    
    public void setRows(ArrayList<HashMap<String, String>> rows) {
        this.rows = rows;
    }
    
    
    public HashMap<String, String> getDatosCliente() {
        return datosCliente;
    }
    
    public void setDatosCliente(HashMap<String, String> datosCliente) {
        this.datosCliente = datosCliente;
    }
    
    public HashMap<String, String> getDatosExtras() {
        return datosExtras;
    }
    
    public void setDatosExtras(HashMap<String, String> datosExtras) {
        this.datosExtras = datosExtras;
    }
    
    public String getEmisora_calle() {
        return emisora_calle;
    }
    
    public void setEmisora_calle(String emisora_calle) {
        this.emisora_calle = emisora_calle;
    }
    
    public String getEmisora_colonia() {
        return emisora_colonia;
    }
    
    public void setEmisora_colonia(String emisora_colonia) {
        this.emisora_colonia = emisora_colonia;
    }
    
    public String getEmisora_cp() {
        return emisora_cp;
    }
    
    public void setEmisora_cp(String emisora_cp) {
        this.emisora_cp = emisora_cp;
    }
    
    public String getEmisora_estado() {
        return emisora_estado;
    }
    
    public void setEmisora_estado(String emisora_estado) {
        this.emisora_estado = emisora_estado;
    }
    
    public String getEmisora_municipio() {
        return emisora_municipio;
    }

    public void setEmisora_municipio(String emisora_municipio) {
        this.emisora_municipio = emisora_municipio;
    }

    public String getEmisora_numero() {
        return emisora_numero;
    }

    public void setEmisora_numero(String emisora_numero) {
        this.emisora_numero = emisora_numero;
    }

    public String getEmisora_pais() {
        return emisora_pais;
    }

    public void setEmisora_pais(String emisora_pais) {
        this.emisora_pais = emisora_pais;
    }

    public String getEmisora_rfc() {
        return emisora_rfc;
    }

    public void setEmisora_rfc(String emisora_rfc) {
        this.emisora_rfc = emisora_rfc;
    }
    
    public GralInterfaceDao getGralDao() {
        return gralDao;
    }
    
    public void setGralDao(GralInterfaceDao gralDao) {
        this.gralDao = gralDao;
    }
    
    public String getEmisora_telefono() {
        return emisora_telefono;
    }

    public void setEmisora_telefono(String emisora_telefono) {
        this.emisora_telefono = emisora_telefono;
    }
    
    public String getEmisora_pagina_web() {
        return emisora_pagina_web;
    }

    public void setEmisora_pagina_web(String emisora_pagina_web) {
        this.emisora_pagina_web = emisora_pagina_web;
    }
    
    public String getLugar_expedidion() {
        return lugar_expedidion;
    }

    public void setLugar_expedidion(String lugar_expedidion) {
        this.lugar_expedidion = lugar_expedidion;
    }
    

    public String getEmisora_regimen_fiacal() {
        return emisora_regimen_fiacal;
    }

    public void setEmisora_regimen_fiacal(String emisora_regimen_fiacal) {
        this.emisora_regimen_fiacal = emisora_regimen_fiacal;
    }
    
    public String getAno_aprobacion() {
        return ano_aprobacion;
    }

    public void setAno_aprobacion(String ano_aprobacion) {
        this.ano_aprobacion = ano_aprobacion;
    }
    
    public String getNo_aprobacion() {
        return no_aprobacion;
    }
    
    public void setNo_aprobacion(String no_aprobacion) {
        this.no_aprobacion = no_aprobacion;
    }
    
    public String getNo_certificado() {
        return no_certificado;
    }
    
    public void setNo_certificado(String no_certificado) {
        this.no_certificado = no_certificado;
    }
    
    
    public String getReceptor_calle() {
        return receptor_calle;
    }

    public void setReceptor_calle(String receptor_calle) {
        this.receptor_calle = receptor_calle;
    }

    public String getReceptor_colonia() {
        return receptor_colonia;
    }

    public void setReceptor_colonia(String receptor_colonia) {
        this.receptor_colonia = receptor_colonia;
    }

    public String getReceptor_cp() {
        return receptor_cp;
    }

    public void setReceptor_cp(String receptor_cp) {
        this.receptor_cp = receptor_cp;
    }

    public String getReceptor_estado() {
        return receptor_estado;
    }

    public void setReceptor_estado(String receptor_estado) {
        this.receptor_estado = receptor_estado;
    }

    public String getReceptor_municipio() {
        return receptor_municipio;
    }

    public void setReceptor_municipio(String receptor_municipio) {
        this.receptor_municipio = receptor_municipio;
    }

    public String getReceptor_numero() {
        return receptor_numero;
    }

    public void setReceptor_numero(String receptor_numero) {
        this.receptor_numero = receptor_numero;
    }

    public String getReceptor_pais() {
        return receptor_pais;
    }

    public void setReceptor_pais(String receptor_pais) {
        this.receptor_pais = receptor_pais;
    }

    public String getReceptor_razon_social() {
        return receptor_razon_social;
    }

    public void setReceptor_razon_social(String receptor_razon_social) {
        this.receptor_razon_social = receptor_razon_social;
    }

    public String getReceptor_no_control() {
        return receptor_no_control;
    }

    public void setReceptor_no_control(String receptor_no_control) {
        this.receptor_no_control = receptor_no_control;
    }
    
    public String getReceptor_rfc() {
        return receptor_rfc;
    }

    public void setReceptor_rfc(String receptor_rfc) {
        this.receptor_rfc = receptor_rfc;
    }

    public String getReceptor_telefono() {
        return receptor_telefono;
    }

    public void setReceptor_telefono(String receptor_telefono) {
        this.receptor_telefono = receptor_telefono;
    }
    
    
    public String getFecha_pago() {
        return fecha_pago;
    }

    public void setFecha_pago(String fecha_pago) {
        this.fecha_pago = fecha_pago;
    }

    public String getMetodo_pago() {
        return metodo_pago;
    }

    public void setMetodo_pago(String metodo_pago) {
        this.metodo_pago = metodo_pago;
    }

    public String getNo_cuenta() {
        return no_cuenta;
    }

    public void setNo_cuenta(String no_cuenta) {
        this.no_cuenta = no_cuenta;
    }

    public String getObservaciones() {
        return observaciones;
    }

    public void setObservaciones(String observaciones) {
        this.observaciones = observaciones;
    }
    

    public String getFolioPedido() {
        return folioPedido;
    }

    public void setFolioPedido(String folioPedido) {
        this.folioPedido = folioPedido;
    }
    
    public String getMontoImpuesto() {
        return montoImpuesto;
    }

    public void setMontoImpuesto(String montoImpuesto) {
        this.montoImpuesto = montoImpuesto;
    }

    public String getMontoRetencion() {
        return montoRetencion;
    }

    public void setMontoRetencion(String montoRetencion) {
        this.montoRetencion = montoRetencion;
    }

    public String getMontoTotal() {
        return montoTotal;
    }

    public void setMontoTotal(String montoTotal) {
        this.montoTotal = montoTotal;
    }

    public String getSubTotal() {
        return subTotal;
    }

    public void setSubTotal(String subTotal) {
        this.subTotal = subTotal;
    }
   
    public String getMoneda_abr() {
        return moneda_abr;
    }

    public void setMoneda_abr(String moneda_abr) {
        this.moneda_abr = moneda_abr;
    }

    public String getSimbolo_moneda() {
        return simbolo_moneda;
    }

    public void setSimbolo_moneda(String simbolo_moneda) {
        this.simbolo_moneda = simbolo_moneda;
    }

    public String getTitulo_moneda() {
        return titulo_moneda;
    }

    public void setTitulo_moneda(String titulo_moneda) {
        this.titulo_moneda = titulo_moneda;
    }
    
    public String getEtiqueta_tipo_doc() {
        return etiqueta_tipo_doc;
    }

    public void setEtiqueta_tipo_doc(String etiqueta_tipo_doc) {
        this.etiqueta_tipo_doc = etiqueta_tipo_doc;
    }

    public String getTipo_facturacion() {
        return tipo_facturacion;
    }

    public void setTipo_facturacion(String tipo_facturacion) {
        this.tipo_facturacion = tipo_facturacion;
    }
    
    public String getSello_digital_sat() {
        return sello_digital_sat;
    }

    public void setSello_digital_sat(String sello_digital_sat) {
        this.sello_digital_sat = sello_digital_sat;
    }
    
    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }
}
