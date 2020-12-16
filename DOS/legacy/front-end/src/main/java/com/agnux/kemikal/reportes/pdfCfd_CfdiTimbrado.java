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


public final class pdfCfd_CfdiTimbrado {
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
    private String receptor_numero_exterior;
    private String receptor_colonia;
    private String receptor_cp;
    private String receptor_municipio;
    private String receptor_estado;
    private String receptor_pais;
    private String receptor_telefono;
    
    private String etiqueta_tipo_doc;
    
    
    //----------------------
    
   public pdfCfd_CfdiTimbrado(GralInterfaceDao gDao,HashMap<String, String> datosCliente, ArrayList<HashMap<String, String>> listaConceptos, HashMap<String, String> extras, Integer id_empresa, Integer id_sucursal) {
        Font negrita_pequena= new Font(Font.FontFamily.HELVETICA,6,Font.BOLD,BaseColor.BLACK);
        Font smallFont = new Font(Font.FontFamily.HELVETICA,8,Font.NORMAL,BaseColor.BLACK);
        Font largeBoldFont = new Font(Font.FontFamily.HELVETICA,8,Font.BOLD,BaseColor.BLACK);
        Font smallsmall = new Font(Font.FontFamily.HELVETICA,5,Font.NORMAL,BaseColor.BLACK);
        Font smallBoldFont = new Font(Font.FontFamily.HELVETICA, 6, Font.BOLD, BaseColor.WHITE);
             
        ImagenPDF ipdf = new ImagenPDF();
        //CeldaPDF cepdf = new CeldaPDF();
        CeldaFother cepdffother = new CeldaFother();
        TablaTotales tablaTotales = new TablaTotales();//esta es la nueva tabla totales
        TablaInformacionFiscal tablaFiscal = new TablaInformacionFiscal();//tabla para la informacion fiscal
        
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
        this.setReceptor_numero(datosCliente.get("comprobante_receptor_domicilio_attr_nointerior"));
        this.setReceptor_numero_exterior(datosCliente.get("comprobante_receptor_domicilio_attr_noexterior"));
        this.setReceptor_colonia(datosCliente.get("comprobante_receptor_domicilio_attr_colonia"));
        this.setReceptor_cp(datosCliente.get("comprobante_receptor_domicilio_attr_codigopostal"));
        this.setReceptor_municipio(datosCliente.get("comprobante_receptor_domicilio_attr_municipio"));
        this.setReceptor_estado(datosCliente.get("comprobante_receptor_domicilio_attr_estado"));
        this.setReceptor_pais(datosCliente.get("comprobante_receptor_domicilio_attr_pais"));
        this.setReceptor_telefono("");
        
        this.setImagen( this.getGralDao().getImagesDir()+this.getEmisora_rfc()+"_logo.png" );
        this.setImagen_cedula( this.getGralDao().getImagesDir()+this.getEmisora_rfc()+"_cedula.png" );
        
        PdfPTable table;
        PdfPTable table_observ;
        PdfPTable table_vacia;
        PdfPCell cell;
        String fileout="";
        
        if(this.getTipo_facturacion().equals("cfd")){
            fileout = this.getGralDao().getCfdEmitidosDir() + this.getGralDao().getRfcEmpresaEmisora(id_empresa) + "/" + this.getSerie_folio() +".pdf";
        }
        
        if(this.getTipo_facturacion().equals("cfditf")){
            fileout = this.getGralDao().getCfdiTimbreEmitidosDir() + this.getGralDao().getRfcEmpresaEmisora(id_empresa) + "/" + this.getSerie_folio() +".pdf";
        }
        
        try {
            HeaderFooter event = new HeaderFooter();
            Document document = new Document(PageSize.LETTER, -50, -50, 20, 30);
            document.addCreator("gpmarsan@gmail.com");
            PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(fileout));
            writer.setPageEvent(event);
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
            
            String etiqueta_folio_fiscal="";
            String folio_fiscal="";
            
            if(this.getTipo_facturacion().equals("cfditf")){
                etiqueta_folio_fiscal="FOLIO FISCAL:";
                folio_fiscal=this.getUuid();
            }
            
            cell = new PdfPCell(new Paragraph(etiqueta_folio_fiscal,negrita_pequena));
            cell.setBorder(0);
            cell.setUseAscender(true);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            table.addCell(cell);
            
            cell = new PdfPCell(new Paragraph(folio_fiscal,smallFont));
            cell.setBorder(0);
            cell.setColspan(2);
            cell.setUseAscender(true);
            cell.setHorizontalAlignment(Element.ALIGN_LEFT);
            table.addCell(cell);
            /*
            cell = new PdfPCell(new Paragraph("",smallFont));
            cell.setBorder(0);
            cell.setUseAscender(true);
            cell.setHorizontalAlignment(Element.ALIGN_LEFT);
            table.addCell(cell);
            */
            
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
            
            //cell = new PdfPCell(new Paragraph("FACTURA No / INVOICE No.",negrita_pequena));
            cell = new PdfPCell(new Paragraph(this.getEtiqueta_tipo_doc(),negrita_pequena));
            cell.setBorder(0);
            cell.setUseAscender(true);
            cell.setHorizontalAlignment(Element.ALIGN_LEFT);
            table.addCell(cell);
            
            //Serie_folio de la factura o Nota de Credito
            cell = new PdfPCell(new Paragraph(this.getSerie_folio(),smallFont));
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
            
            
            cell = new PdfPCell(new Paragraph("FECHA: / DATE:",negrita_pequena));
            cell.setBorder(0);
            cell.setUseAscender(true);
            cell.setHorizontalAlignment(Element.ALIGN_LEFT);
            table.addCell(cell);
            
            String fecha1[] = this.getFacha_comprobante().split("T");
            
            String fecha[] = fecha1[0].split("-");
            
            //fecha
            cell = new PdfPCell(new Paragraph(fecha[2]+"/"+fecha[1]+"/"+fecha[0],smallFont));
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
            
            
            
            
            cell = new PdfPCell(new Paragraph("HORA:/HOUR:",negrita_pequena));
            cell.setBorder(0);
            cell.setUseAscender(true);
            cell.setHorizontalAlignment(Element.ALIGN_LEFT);
            table.addCell(cell);
            
            String hora="";
            if(this.getFacha_comprobante().contains("T")){
                hora = fecha1[1];
            }
            
            //aqui debe ir la hora
            cell = new PdfPCell(new Paragraph(hora,smallFont));
            cell.setBorder(0);
            cell.setUseAscender(true);
            cell.setHorizontalAlignment(Element.ALIGN_LEFT);
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
            
            String no="";
            if( !this.getReceptor_numero().equals("") && this.getReceptor_numero()!=null){
                no= ""+this.getReceptor_numero();
            }
            
            if( !this.getReceptor_numero_exterior().equals("") && this.getReceptor_numero_exterior()!=null){
                if(no.equals("")){
                    no+= ""+this.getReceptor_numero_exterior();
                }else{
                    no+= ", "+this.getReceptor_numero_exterior();
                }
            }
            
            cell = new PdfPCell(new Paragraph( 
                    this.getReceptor_calle()+ " " + no +"\n"+ 
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
            
            
            if(this.getTipo_facturacion().equals("cfd")){
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
            }

            if(this.getTipo_facturacion().equals("cfditf")){
                cell = new PdfPCell(new Paragraph("",negrita_pequena));
                cell.setBorder(0);
                cell.setBorderWidthRight(0);
                cell.setUseAscender(true);
                cell.setHorizontalAlignment(Element.ALIGN_LEFT);
                cell.setVerticalAlignment(Element.ALIGN_CENTER);
                table.addCell(cell);
                
                cell = new PdfPCell(new Paragraph("",smallFont));
                cell.setBorder(0);
                cell.setBorderWidthLeft(0);
                cell.setUseAscender(true);
                cell.setHorizontalAlignment(Element.ALIGN_LEFT);
                cell.setVerticalAlignment(Element.ALIGN_CENTER);
                table.addCell(cell);
            }
            
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
            
            document.add(table);//agregamos la tabla al documento
            
            
            //&&&&&&&&&&&&&&&&&&&& INICIA TABLA DE DATOS NO. CLIENTE, PEDIDO Y VENDEDOR &&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&
            float [] anchocolumnas1 = {1.2f,1,4};
            PdfPTable table1 = new PdfPTable(anchocolumnas1);
            table1.setKeepTogether(false);
            PdfPCell cell1;
            
            cell1 = new PdfPCell(new Paragraph("CLIENTE NO.:/ CUSTOMER NO.:",negrita_pequena));
            cell1.setUseAscender(true);
            cell1.setHorizontalAlignment(Element.ALIGN_LEFT);
            table1.addCell(cell1);
            
            cell1 = new PdfPCell(new Paragraph("SU PEDIDO: / YOUR ORDER",negrita_pequena));
            cell1.setUseAscender(true);
            cell1.setHorizontalAlignment(Element.ALIGN_LEFT);
            table1.addCell(cell1);
            
            cell1 = new PdfPCell(new Paragraph("VENDEDOR: / SALESMAN:",negrita_pequena));
            cell1.setUseAscender(true);
            cell1.setHorizontalAlignment(Element.ALIGN_LEFT);
            table1.addCell(cell1);
            ////////////////////////////////////////////////////////////////////
            
            
            cell1 = new PdfPCell(new Paragraph(this.getReceptor_no_control(),smallFont));
            cell1.setUseAscender(true);
            cell1.setHorizontalAlignment(Element.ALIGN_CENTER);
            table1.addCell(cell1);
            
            
            cell1 = new PdfPCell(new Paragraph( this.getFolioPedido(),smallFont));
            cell1.setUseAscender(true);
            cell1.setHorizontalAlignment(Element.ALIGN_CENTER);
            table1.addCell(cell1);
            
            cell1 = new PdfPCell(new Paragraph(this.getVendedor(),smallFont));
            cell1.setUseAscender(true);
            cell1.setHorizontalAlignment(Element.ALIGN_LEFT);
            table1.addCell(cell1);
            
            cell1 = new PdfPCell(new Paragraph("",smallFont));
            cell1.setBorder(0);
            cell1.setColspan(3);
            cell1.setFixedHeight(10);
            cell1.setUseAscender(true);
            cell1.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell1.setVerticalAlignment(Element.ALIGN_CENTER);
            table1.addCell(cell1);
            
            document.add(table1);
            ///&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&
            
            
            //&&&&&&&&&&&&&&&&&&&& AQUI INICIA LA CONSTRUCCION DE LA TABLA DE  LOS CONCEPTOS AL DOCUMENTO &&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&
            float [] anchocolumnas2 = {1f, 2.3f, 1f, 1f, 0.5f,1f,1f, 0.5f,1.5f};
            PdfPTable tableConceptos = new PdfPTable(anchocolumnas2);
            tableConceptos.setKeepTogether(false);
            tableConceptos.setHeaderRows(1);
            
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

                tableConceptos.addCell(cellX);
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
                            //celda.setBorder(0);
                            celda.setBorderWidthBottom(0);
                            celda.setBorderWidthLeft(1);
                            celda.setBorderWidthRight(0);
                            celda.setBorderWidthTop(0);
                            tableConceptos.addCell(celda);
                        }
                        
                        if (omega.equals("titulo")){
                            celda = new PdfPCell(new Paragraph(registro.get(omega).toUpperCase(),smallFont));
                            //celda= new PdfPCell(new Paragraph(StringHelper.AgregaComas(StringHelper.roundDouble(registro.get(omega).toString(),2)),smallFont));
                            celda.setHorizontalAlignment(Element.ALIGN_LEFT);
                            celda.setVerticalAlignment(Element.ALIGN_TOP);
                            //celda.setBorder(0);
                            celda.setBorderWidthBottom(0);
                            celda.setBorderWidthLeft(0);
                            celda.setBorderWidthRight(0);
                            celda.setBorderWidthTop(0);
                            tableConceptos.addCell(celda);
                        }
                        
                        if (omega.equals("numero_lote")){
                            //celda = new PdfPCell(new Paragraph(registro.get(omega),smallFont));
                            celda= new PdfPCell(new Paragraph(registro.get(omega).toString(),smallFont));
                            celda.setHorizontalAlignment(Element.ALIGN_CENTER);
                            celda.setVerticalAlignment(Element.ALIGN_TOP);
                            //celda.setBorder(0);
                            celda.setBorderWidthBottom(0);
                            celda.setBorderWidthLeft(0);
                            celda.setBorderWidthRight(0);
                            celda.setBorderWidthTop(0);
                            tableConceptos.addCell(celda);
                        }
                        
                        if (omega.equals("cantidad")){
                            //celda = new PdfPCell(new Paragraph(registro.get(omega),smallFont));
                            celda= new PdfPCell(new Paragraph(StringHelper.AgregaComas(StringHelper.roundDouble(registro.get(omega).toString(),2)),smallFont));
                            celda.setHorizontalAlignment(Element.ALIGN_RIGHT);
                            celda.setVerticalAlignment(Element.ALIGN_TOP);
                            //celda.setBorder(0);
                            celda.setBorderWidthBottom(0);
                            celda.setBorderWidthLeft(0);
                            celda.setBorderWidthRight(0);
                            celda.setBorderWidthTop(0);
                            tableConceptos.addCell(celda);
                        }
                        
                        if (omega.equals("denominacion")){
                                celda = new PdfPCell(new Paragraph( "$",smallFont));
                                celda.setHorizontalAlignment(Element.ALIGN_RIGHT);
                                celda.setVerticalAlignment(Element.ALIGN_TOP);
                                //celda.setBorder(0);
                                celda.setBorderWidthBottom(0);
                                celda.setBorderWidthLeft(0);
                                celda.setBorderWidthRight(0);
                                celda.setBorderWidthTop(0);
                                tableConceptos.addCell(celda);
                        }
                        
                        if (omega.equals("precio_unitario")){
                            //celda = new PdfPCell(new Paragraph(registro.get(omega),smallFont));
                            celda= new PdfPCell(new Paragraph(StringHelper.AgregaComas(StringHelper.roundDouble(registro.get(omega).toString(),2)),smallFont));
                            celda.setHorizontalAlignment(Element.ALIGN_RIGHT);
                            celda.setVerticalAlignment(Element.ALIGN_TOP);
                            //celda.setBorder(0);
                            celda.setBorderWidthBottom(0);
                            celda.setBorderWidthLeft(0);
                            celda.setBorderWidthRight(0);
                            celda.setBorderWidthTop(0);
                            tableConceptos.addCell(celda);
                        }
                        
                        if (omega.equals("moneda")){
                            celda = new PdfPCell(new Paragraph(registro.get(omega).toUpperCase(),smallFont));
                            celda.setHorizontalAlignment(Element.ALIGN_CENTER);
                            celda.setVerticalAlignment(Element.ALIGN_TOP);
                            //celda.setBorder(0);
                            celda.setBorderWidthBottom(0);
                            celda.setBorderWidthLeft(0);
                            celda.setBorderWidthRight(0);
                            celda.setBorderWidthTop(0);
                            tableConceptos.addCell(celda);
                         }
                        
                        if (omega.equals("importe")){
                            celda= new PdfPCell(new Paragraph(StringHelper.AgregaComas(StringHelper.roundDouble(registro.get(omega).toString(),2)),smallFont));
                            celda.setHorizontalAlignment(Element.ALIGN_RIGHT);
                            celda.setVerticalAlignment(Element.ALIGN_TOP);
                            //celda.setBorder(0);
                            celda.setBorderWidthBottom(0);
                            celda.setBorderWidthLeft(0);
                            celda.setBorderWidthRight(1);
                            celda.setBorderWidthTop(0);
                            tableConceptos.addCell(celda);
                         }
                         
                    
                }
                
                contador++;
                
            }

            cellX = new PdfPCell(new Paragraph("",smallFont));
            cellX.setHorizontalAlignment(Element.ALIGN_LEFT);
            cellX.setVerticalAlignment(Element.ALIGN_TOP);
            //celda.setBorder(0);
            cellX.setColspan(9);
            cellX.setBorderWidthBottom(0);
            cellX.setBorderWidthLeft(0);
            cellX.setBorderWidthRight(0);
            cellX.setBorderWidthTop(1);
            tableConceptos.addCell(cellX);
            
            document.add(tableConceptos);
            //&&&&&&&&&&&&&&&&&&&&&&&&&    FIN DE LA TABLE DE CONCEPTOS  &&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&
            //System.out.println("tableConceptos.size: "+tableConceptos.size());
            //System.out.println("tableConceptos.getTotalHeight: "+tableConceptos.getTotalHeight());
            
            Boolean salto=false;
            if((tableConceptos.getTotalHeight())>490 && ( tableConceptos.getTotalHeight()) < 492){
                document.newPage();
                salto=true;
            }
            
            //aqui comienza la tabla de para las OBSERVACIONES
            float [] widths_table_observ = {1};
            table_observ = new PdfPTable(widths_table_observ);
            table_observ.setKeepTogether(false);
            
            cell = new PdfPCell(new Paragraph("\nOBSERVACIONES:",negrita_pequena));
            cell.setBorder(0);
            cell.setVerticalAlignment(Element.ALIGN_TOP);
            cell.setHorizontalAlignment(Element.ALIGN_LEFT);
            table_observ.addCell(cell);
            
            String cuentas="";
            if ( !this.getEmisora_rfc().equals("KCM081010I58") && !this.getEmisora_rfc().equals("KME010221CB4")){
                cuentas="";
            }else{
                if (getProposito().equals("FACTURA")){
                    cuentas="\nDEPOSITAR EN BANORTE (MN) Cta. 0587326205 CLABE 072580005873262052, (USD) Cta. 0557037045 CLABE 072580005570370454";   
                }
            }
            
            cell = new PdfPCell(new Paragraph(this.getObservaciones()+cuentas,smallFont));
            cell.setBorder(0);
            cell.setVerticalAlignment(Element.ALIGN_TOP);
            cell.setHorizontalAlignment(Element.ALIGN_LEFT);
            table_observ.addCell(cell);
            
            cell = new PdfPCell(new Paragraph("",smallFont));
            cell.setBorder(0);
            cell.setVerticalAlignment(Element.ALIGN_TOP);
            cell.setHorizontalAlignment(Element.ALIGN_LEFT);
            table_observ.addCell(cell);
            
            document.add(table_observ);
            /*
            System.out.println("table_observ.size6: "+table_observ.size());
            System.out.println("table_observ.getTotalHeight: "+table_observ.getTotalHeight());
            System.out.println("ALTURA: "+(table_observ.getTotalHeight() + tableConceptos.getTotalHeight()));
            */
            
            //aqui comienza la tabla VACIA
            float [] widths_table_vacia = {1};
            table_vacia = new PdfPTable(widths_table_vacia);
            table_vacia.setKeepTogether(false);
            
            float  altura = (table_observ.getTotalHeight() + tableConceptos.getTotalHeight());
            if(this.getTipo_facturacion().equals("cfditf")){
                altura+=8;
            }
            
            int cont=0;
            while (altura < 276){
                cell = new PdfPCell(new Paragraph(" ",smallFont));
                cell.setBorder(0);
                cell.setVerticalAlignment(Element.ALIGN_TOP);
                cell.setHorizontalAlignment(Element.ALIGN_LEFT);
                table_vacia.addCell(cell);
                altura +=10;
                cont+=1;
            }
            document.add(table_vacia);
            //FIN de la tabla VACIA
            
            //aqui se decide si se debe brincar la cadena original a una nueva pagina
            if((table_observ.getTotalHeight() + tableConceptos.getTotalHeight())>380 && (table_observ.getTotalHeight() + tableConceptos.getTotalHeight()) <492){
                if(salto!=true){
                    document.newPage();
                    salto=true;
                }
            }
            
            //&&&&&&&&&&&&&&&&&&&& AQUI SE AGREGA TABLA DE  LOS TOTALES Y LA CEDULA AL DOCUMENTO &&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&
            document.add(tablaTotales.addContent(this.getSubTotal(), this.getMontoImpuesto(), this.getMontoRetencion(), this.getMontoTotal()  ));
            //&&&&&&&&&&&&&&&&&&&&&&&&&    FIN DE LA TOTALES Y LA CEDULA  &&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&
            
            //aqui se decide si se debe brincar la cadena original a una nueva pagina
            if((table_observ.getTotalHeight() + tableConceptos.getTotalHeight())>276 && (table_observ.getTotalHeight() + tableConceptos.getTotalHeight()) <380){
                if(salto!=true){
                    document.newPage();
                    salto=true;
                }
            }
            
            //&&&&&&&&&&&&&&&&&&&& AQUI SE AGREGA TABLA DE LA INFORMACION FISCAL &&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&
            document.add(tablaFiscal.addContent());
            //&&&&&&&&&&&&&&&&&&&&&&&&&    FIN DE LA INFORMACION FISCAL  &&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&
            
            document.close();
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
                //img.setSpacingBefore(6);
                //img.setAlignment(0);
            }
            catch(Exception e){
                System.out.println(e);
            }
            return img;
        }
    }
    
    
    /*
    
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
            table.setKeepTogether(false);
            table.setHeaderRows(1);
            
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
                            //celda.setBorder(0);
                            celda.setBorderWidthBottom(0);
                            celda.setBorderWidthLeft(1);
                            celda.setBorderWidthRight(0);
                            celda.setBorderWidthTop(0);
                            table.addCell(celda);
                        }
                        
                        if (omega.equals("titulo")){
                            celda = new PdfPCell(new Paragraph(registro.get(omega).toUpperCase(),smallFont));
                            //celda= new PdfPCell(new Paragraph(StringHelper.AgregaComas(StringHelper.roundDouble(registro.get(omega).toString(),2)),smallFont));
                            celda.setHorizontalAlignment(Element.ALIGN_LEFT);
                            celda.setVerticalAlignment(Element.ALIGN_TOP);
                            //celda.setBorder(0);
                            celda.setBorderWidthBottom(0);
                            celda.setBorderWidthLeft(0);
                            celda.setBorderWidthRight(0);
                            celda.setBorderWidthTop(0);
                            table.addCell(celda);
                        }
                        
                        if (omega.equals("numero_lote")){
                            //celda = new PdfPCell(new Paragraph(registro.get(omega),smallFont));
                            celda= new PdfPCell(new Paragraph(registro.get(omega).toString(),smallFont));
                            celda.setHorizontalAlignment(Element.ALIGN_CENTER);
                            celda.setVerticalAlignment(Element.ALIGN_TOP);
                            //celda.setBorder(0);
                            celda.setBorderWidthBottom(0);
                            celda.setBorderWidthLeft(0);
                            celda.setBorderWidthRight(0);
                            celda.setBorderWidthTop(0);
                            table.addCell(celda);
                        }
                        
                        if (omega.equals("cantidad")){
                            //celda = new PdfPCell(new Paragraph(registro.get(omega),smallFont));
                            celda= new PdfPCell(new Paragraph(StringHelper.AgregaComas(StringHelper.roundDouble(registro.get(omega).toString(),2)),smallFont));
                            celda.setHorizontalAlignment(Element.ALIGN_RIGHT);
                            celda.setVerticalAlignment(Element.ALIGN_TOP);
                            //celda.setBorder(0);
                            celda.setBorderWidthBottom(0);
                            celda.setBorderWidthLeft(0);
                            celda.setBorderWidthRight(0);
                            celda.setBorderWidthTop(0);
                            table.addCell(celda);
                        }
                        if (omega.equals("denominacion")){
                                celda = new PdfPCell(new Paragraph( "$",smallFont));
                                celda.setHorizontalAlignment(Element.ALIGN_RIGHT);
                                celda.setVerticalAlignment(Element.ALIGN_TOP);
                                //celda.setBorder(0);
                                celda.setBorderWidthBottom(0);
                                celda.setBorderWidthLeft(0);
                                celda.setBorderWidthRight(0);
                                celda.setBorderWidthTop(0);
                                table.addCell(celda);
                        }
                        
                        
                        if (omega.equals("precio_unitario")){
                            //celda = new PdfPCell(new Paragraph(registro.get(omega),smallFont));
                            celda= new PdfPCell(new Paragraph(StringHelper.AgregaComas(StringHelper.roundDouble(registro.get(omega).toString(),2)),smallFont));
                            celda.setHorizontalAlignment(Element.ALIGN_RIGHT);
                            celda.setVerticalAlignment(Element.ALIGN_TOP);
                            //celda.setBorder(0);
                            celda.setBorderWidthBottom(0);
                            celda.setBorderWidthLeft(0);
                            celda.setBorderWidthRight(0);
                            celda.setBorderWidthTop(0);
                            table.addCell(celda);
                        }
                        
                        if (omega.equals("moneda")){
                            celda = new PdfPCell(new Paragraph(registro.get(omega).toUpperCase(),smallFont));
                            celda.setHorizontalAlignment(Element.ALIGN_CENTER);
                            celda.setVerticalAlignment(Element.ALIGN_TOP);
                            //celda.setBorder(0);
                            celda.setBorderWidthBottom(0);
                            celda.setBorderWidthLeft(0);
                            celda.setBorderWidthRight(0);
                            celda.setBorderWidthTop(0);
                            table.addCell(celda);
                         }
                        
                        
                        
                        if (omega.equals("importe")){
                            celda= new PdfPCell(new Paragraph(StringHelper.AgregaComas(StringHelper.roundDouble(registro.get(omega).toString(),2)),smallFont));
                            celda.setHorizontalAlignment(Element.ALIGN_RIGHT);
                            celda.setVerticalAlignment(Element.ALIGN_TOP);
                            //celda.setBorder(0);
                            celda.setBorderWidthBottom(0);
                            celda.setBorderWidthLeft(0);
                            celda.setBorderWidthRight(1);
                            celda.setBorderWidthTop(0);
                            table.addCell(celda);
                         }
                         
                    
                }
                
                contador++;
                
            }

           cellX = new PdfPCell(new Paragraph("",smallFont));
            cellX.setHorizontalAlignment(Element.ALIGN_LEFT);
            cellX.setVerticalAlignment(Element.ALIGN_TOP);
            //celda.setBorder(0);
            cellX.setColspan(9);
            cellX.setBorderWidthBottom(0);
            cellX.setBorderWidthLeft(0);
            cellX.setBorderWidthRight(0);
            cellX.setBorderWidthTop(1);
            table.addCell(cellX);
           
            return table;
        }
       
    }
    
    */
    
    
    ///Aqui creamos nueva tabla para los totales
    //aqui se construye la tabla para los totales y la imagen de la cedula
    private class TablaTotales {
       public PdfPTable addContent(String SubTotal, String MontoImpuesto, String MontoRetencion, String MontoTotal) {
            Font smallFont = new Font(Font.FontFamily.HELVETICA,8,Font.NORMAL,BaseColor.BLACK);
            
            Font negrita_pequeña= new Font(Font.FontFamily.HELVETICA,6,Font.BOLD,BaseColor.BLACK);
            
            CedulaPDF icedula = new CedulaPDF();
            
            float [] anchocolumnas = {2,8,4,0.7f,4};
            PdfPTable table = new PdfPTable(anchocolumnas);
            table.setKeepTogether(false);
            PdfPCell cell;
            
            int alto=100,  ancho=60;
            
            //FILA 1 //////////////////////////////////////
            cell = new PdfPCell(icedula.addContent(alto,ancho));
            cell.setRowspan(11);
            //cell.setUseAscender(true);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setVerticalAlignment(Element.ALIGN_CENTER);
            cell.setBorderWidthBottom(0);
            cell.setBorderWidthTop(0);
            cell.setBorderWidthLeft(1);
            cell.setBorderWidthRight(1);
            table.addCell(cell);
            
            cell = new PdfPCell(new Paragraph("CANTIDAD CON LETRA:",negrita_pequeña));
            cell.setColspan(4);
            cell.setUseAscender(true);
            cell.setHorizontalAlignment(Element.ALIGN_LEFT);
            cell.setBorderWidthBottom(0);
            cell.setBorderWidthTop(1);
            table.addCell(cell);
            
            BigInteger num = new BigInteger(MontoTotal.split("\\.")[0]);
            n2t cal = new n2t();
            String centavos = MontoTotal.substring(MontoTotal.indexOf(".")+1);
            String numero = cal.convertirLetras(num);
            
            //convertir a mayuscula la primera letra de la cadena
            String numeroMay = numero.substring(0, 1).toUpperCase() + numero.substring(1, numero.length());
            
            String denom = "";
            String denominacion="";
            denominacion = getTitulo_moneda();
            denom = getMoneda_abr();

            if(centavos.equals(num.toString())){
                centavos="00";
            }
            
            //FILA 2 //////////////////////////////////////
            cell = new PdfPCell(new Paragraph(numeroMay.toUpperCase() + " " + denominacion.toUpperCase() + " " +centavos+"/100 "+ denom.toUpperCase(), smallFont));
            cell.setColspan(4);
            cell.setUseAscender(true);
            cell.setHorizontalAlignment(Element.ALIGN_LEFT);
            cell.setBorderWidthTop(0);
            table.addCell(cell);
            System.out.println("Se ha agregado importe con letras al pdf de factura");
            
            
            //FILA 3 //////////////////////////////////////
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
            
   
            //FILA 4 //////////////////////////////////////
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
            
            
            //FILA 5 //////////////////////////////////////
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
            
            
            //FILA 6 //////////////////////////////////////
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
            
            
            //FILA 7 //////////////////////////////////////
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
            
            
            //FILA 8 //////////////////////////////////////
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
            
            
           //FILA 9 //////////////////////////////////////
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
            
            //FILA 10 //////////////////////////////////////
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
            
            
           //FILA 11 //////////////////////////////////////
            cell = new PdfPCell(new Paragraph("",smallFont));
            cell.setUseAscender(true);
            cell.setHorizontalAlignment(Element.ALIGN_LEFT);
            cell.setBorderWidthBottom(1);
            cell.setBorderWidthTop(0);
            table.addCell(cell);
            
            cell = new PdfPCell(new Paragraph("",smallFont));
            cell.setUseAscender(true);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setBorderWidthBottom(1);
            cell.setBorderWidthTop(0);
            table.addCell(cell);
            
            cell = new PdfPCell(new Paragraph("",smallFont));
            cell.setUseAscender(true);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setBorderWidthBottom(1);
            cell.setBorderWidthTop(0);
            cell.setBorderWidthRight(0);
            table.addCell(cell);
            
            cell = new PdfPCell(new Paragraph("",smallFont));
            cell.setUseAscender(true);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setBorderWidthLeft(0);
            cell.setBorderWidthBottom(1);
            cell.setBorderWidthTop(0);
            table.addCell(cell);
            ////////////////////////////////////////////////
            
            return table;
        }
    }    
    //aqui termina nueva tabla para los totales
    
    
    
    
    
    //tabla para Informacion Fiscal
    private class TablaInformacionFiscal {
       public PdfPTable addContent() {
            Font negrita_pequeña= new Font(Font.FontFamily.HELVETICA,6,Font.BOLD,BaseColor.BLACK);
            Font Fontssello__aviso = new Font(Font.FontFamily.HELVETICA,5,Font.NORMAL,BaseColor.BLACK);
            
            float [] anchocolumnas = {1};
            PdfPTable table = new PdfPTable(anchocolumnas);
            table.setKeepTogether(false);
            PdfPCell cell;
            
            //FILA 1 //////////////////////////////////////
            cell = new PdfPCell(new Paragraph("CADENA ORIGINAL:",negrita_pequeña));
            //cell.setColspan(5);
            cell.setUseAscender(true);
            cell.setHorizontalAlignment(Element.ALIGN_LEFT);
            cell.setBorderWidthBottom(0);
            //cell.setBorderWidthTop(0);
            table.addCell(cell);
            //FILA 2 //////////////////////////////////////
            cell = new PdfPCell(new Paragraph(  getCadena_original()  ,Fontssello__aviso));
            //cell.setColspan(5);
            //cell.setFixedHeight(15);
            cell.setUseAscender(true);
            cell.setHorizontalAlignment(Element.ALIGN_LEFT);
            cell.setBorderWidthTop(0);
            table.addCell(cell);
            
            //FILA 3 //////////////////////////////////////
            cell = new PdfPCell(new Paragraph("SELLO DIGITAL DEL EMISOR:",negrita_pequeña));
            //cell.setColspan(5);
            cell.setUseAscender(true);
            cell.setHorizontalAlignment(Element.ALIGN_LEFT);
            cell.setBorderWidthBottom(0);
            cell.setBorderWidthTop(0);
            table.addCell(cell);
            
            //FILA 4 //////////////////////////////////////
            cell = new PdfPCell(new Paragraph(  getSello_digital(),Fontssello__aviso));
            //cell.setColspan(5);
            //cell.setFixedHeight(15);
            cell.setUseAscender(true);
            cell.setHorizontalAlignment(Element.ALIGN_LEFT);
            cell.setBorderWidthTop(0);
            table.addCell(cell);
            
            
            //if(getTipo_facturacion().equals("cfd")){
            if(getTipo_facturacion().equals("cfditf")){
				//FILA 5 //////////////////////////////////////
                cell = new PdfPCell(new Paragraph("SELLO DIGITAL DEL SAT:",negrita_pequeña));
                //cell.setColspan(5);
                cell.setUseAscender(true);
                cell.setHorizontalAlignment(Element.ALIGN_LEFT);
                cell.setBorderWidthBottom(0);
                cell.setBorderWidthTop(0);
                table.addCell(cell);
                
                //FILA 6 //////////////////////////////////////
                cell = new PdfPCell(new Paragraph( getSello_digital_sat(),Fontssello__aviso));
                //cell.setColspan(5);
                //cell.setFixedHeight(15);
                cell.setUseAscender(true);
                cell.setHorizontalAlignment(Element.ALIGN_LEFT);
                cell.setBorderWidthTop(0);
                table.addCell(cell);
            }
            
            /*
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
            }*/
            
            String tipo_fac="";
            
            if(getTipo_facturacion().equals("cfditf")){
                tipo_fac="CFDI";
            }else{
                tipo_fac="CFD";
            }
                
            
            cell = new PdfPCell(new Paragraph("EFECTOS FISCALES AL PAGO. PAGO EN UNA SOLA EXHIBICION. ESTE DOCUMENTO ES UNA REPRESENTACION IMPRESA DE UN "+tipo_fac,Fontssello__aviso));
            //cell.setColspan(5);
            cell.setUseAscender(true);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            table.addCell(cell);
            
            cell = new PdfPCell(new Paragraph("AVISO DE PRIVACIDAD",Fontssello__aviso));
            //cell.setColspan(5);
            cell.setUseAscender(true);
            cell.setHorizontalAlignment(Element.ALIGN_LEFT);
            cell.setBorderWidthBottom(0);
            table.addCell(cell);
            
            //cell = new PdfPCell(new Paragraph("Los datos personales que proporciona a "+ getEmpresa_emisora()+" Nombre,Domicilio,Registro Federal de Contrubuyentes,Correo Electronico son unica y exclusivamente para uso de facturacion, y no seran usados con fines de divulgacion o utilizacion comercial "+datos_encabezado.get("razon_social_expide_remision")+" Domicilio: "+datos_encabezado.get("calle")+" "+datos_encabezado.get("numero")+" "+datos_encabezado.get("colonia") +" "+datos_encabezado.get("municipio") +" "+datos_encabezado.get("Estado")+"  Lo anterior en los terminos del Articulo 2 Fraccion II de la ley Federal de Proteccion de Datos Personales",Fontssello__aviso));
            cell = new PdfPCell(new Paragraph("Los datos personales que proporciona a "+ getEmpresa_emisora()+" Nombre,Domicilio,Registro Federal de Contrubuyentes,Correo Electronico son unica y exclusivamente para uso de facturacion, y no seran usados con fines de divulgacion o utilizacion comercial.   Lo anterior en los terminos del Articulo 2 Fraccion II de la ley Federal de Proteccion de Datos Personales.",Fontssello__aviso));
            //cell.setColspan(5);
            cell.setUseAscender(true);
            cell.setHorizontalAlignment(Element.ALIGN_LEFT);
            cell.setBorderWidthTop(0);
            table.addCell(cell);
            
            
            return table;
        }
    }    
    //Termina la tabla para Informacion Fiscal
    
    
    
    
    
    
    
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
            table.setKeepTogether(false);
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
            //cell.setFixedHeight(35);
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
            
            String tipo_fac="";
            
            if(getTipo_facturacion().equals("cfditf")){
                tipo_fac="CFDI";
            }else{
                tipo_fac="CFD";
            }
                
            
            cell = new PdfPCell(new Paragraph("EFECTOS FISCALES AL PAGO. PAGO EN UNA SOLA EXHIBICION. ESTE DOCUMENTO ES UNA REPRESENTACION IMPRESA DE UN "+tipo_fac,Fontssello__aviso));
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
    
    public String getReceptor_numero_exterior() {
        return receptor_numero_exterior;
    }

    public void setReceptor_numero_exterior(String receptor_numero_exterior) {
        this.receptor_numero_exterior = receptor_numero_exterior;
    }
    
    
    static class HeaderFooter extends PdfPageEventHelper {
        public Image headerImage;
        protected PdfTemplate total;       
        protected BaseFont helv;  
        protected PdfContentByte cb;  
        Font largeBoldFont = new Font(Font.FontFamily.HELVETICA,10,Font.BOLD,BaseColor.BLACK);
        Font largeFont = new Font(Font.FontFamily.HELVETICA,10,Font.NORMAL,BaseColor.BLACK);
        Font smallFont = new Font(Font.FontFamily.HELVETICA,7,Font.NORMAL,BaseColor.BLACK);
        
        
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
            //PdfContentByte cb = writer.getDirectContent();
            //ColumnText.showTextAligned(writer.getDirectContent(),Element.ALIGN_CENTER, new Phrase(this.getEmpresa(),largeBoldFont),document.getPageSize().getWidth()/2, document.getPageSize().getTop() -25, 0);
            //ColumnText.showTextAligned(writer.getDirectContent(),Element.ALIGN_CENTER, new Phrase("Depositos en Bancos",largeBoldFont),document.getPageSize().getWidth()/2, document.getPageSize().getTop()-38, 0);
            //ColumnText.showTextAligned(writer.getDirectContent(),Element.ALIGN_CENTER, new Phrase(this.getPeriodo(),largeFont),document.getPageSize().getWidth()/2, document.getPageSize().getTop()-50, 0);
            
            
            cb = writer.getDirectContent();
            float textBase = document.bottom() - 20;
            
            //texto inferior izquieda pie de pagina
            String text_left ="";
            float text_left_Size = helv.getWidthPoint(text_left, 7);
            cb.beginText();
            cb.setFontAndSize(helv, 7);  
            cb.setTextMatrix(document.left()+85, textBase );  //definir la posicion de text
            cb.showText(text_left);
            cb.endText();
            
            
            //texto centro pie de pagina
            String text_center ="Hoja " + writer.getPageNumber() + " de ";
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
            String text_right = "";
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
