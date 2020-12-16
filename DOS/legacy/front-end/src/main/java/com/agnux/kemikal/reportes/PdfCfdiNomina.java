/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.agnux.kemikal.reportes;

import com.agnux.common.helpers.CodigoQRHelper;
import com.agnux.common.helpers.FileHelper;
import com.agnux.kemikal.interfacedaos.GralInterfaceDao;
import com.google.zxing.WriterException;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import com.agnux.common.helpers.StringHelper;
import com.agnux.common.helpers.n2t;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;
import java.io.FileOutputStream;
import java.math.BigInteger;
import java.net.URISyntaxException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author agnux
 */
public final class PdfCfdiNomina {
       //--variables para pdf--
    private  GralInterfaceDao gralDao;
    public static enum Proposito {FACTURA, NOTA_CREDITO, NOTA_CARGO};
    private String fileout;
    
    private String comprobante_cancelado;
    private String tipo_facturacion;
    private String imagen;
    private String rutaImagenCBB;
    private String cadenaCBB;
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
    private String fachaTimbrado;
    private String noCertificadoSAT;
    
    //private ArrayList<HashMap<String, String>> rows;
    private ArrayList<LinkedHashMap<String,String>> rows;
    private ArrayList<LinkedHashMap<String,String>> rowsd;
    private ArrayList<LinkedHashMap<String,String>> rows_horas_extras;
    private ArrayList<LinkedHashMap<String,String>> rows_incapacidades;
    private HashMap<String, String> encabezado;
    private HashMap<String, String> datos_nomina;
    private String terminos;
    private String formaPago;
    private String fecha_pago;
    private String metodo_pago;
    private String no_cuenta;
    private String subTotal;
    private String montoImpuesto;
    private String montoIeps;
    private String montoRetencion;
    private String montoTotal;
    private String titulo_moneda;
    private String moneda_abr;
    private String simbolo_moneda;
    private String tipoCambio;
    private String monedaIso;
    
    
    private String percep_total_gravado;
    private String percep_total_excento;
    private String deduc_total_gravado;
    private String deduc_total_excento;


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
    private String leyenda;
    
    private String nss;
    private String regpatronal;
    private String regimen;
    private String regimen_contratacion_titulo;
    private String depto;
    private String puesto;
    private String riesgo_puesto;
    private String riesgo_puesto_titulo;
    private String tipo_contrato;
    private String tipo_jornada;
    private String fecha_antiguedad;
    private String fecha_contrato;
    private String periodicidad_pago;
    private String salario_base;
    private String sdi;
    private String fechapago;
    private String fechain;
    private String fechafin;
    private String ndias;
    private String banco;
    private String nombre_banco;
    private String clabe;
    private String condiciones_pago;
    private String curp;
    
    
  
    
    public PdfCfdiNomina(GralInterfaceDao gDao,HashMap<String, String> datos_nomina, ArrayList<LinkedHashMap<String,String>> conceptospercepciones, ArrayList<LinkedHashMap<String,String>>  conceptosdeducciones, ArrayList<LinkedHashMap<String,String>> horas_extras, ArrayList<LinkedHashMap<String,String>> incapacidades, String fileout,Integer id_empresa, Integer id_sucursal) {
        this.setGralDao(gDao);
        this.setRows(conceptospercepciones);
        this.setRowsd(conceptosdeducciones);
        this.setRows_horas_extras(horas_extras);
        this.setRows_incapacidades(incapacidades);
        this.setDatosCliente(datos_nomina);
        
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
        //this.setProposito(extras.get("proposito"));
        
        
        if(datos_nomina.get("comprobante_cancelado").equals("true")){
            this.setComprobante_cancelado("CANCELADO");
        }else{
            this.setComprobante_cancelado("");
        }
        
        this.setSerie_folio(datos_nomina.get("serie_folio"));
        this.setFacha_comprobante(datos_nomina.get("fecha_comprobante"));
        //this.setTerminos(datos_nomina.get("serie_folio"));
        //this.setFecha_pago(datos_nomina.get("comprobante_attr_fecha_fecha_pago"));
        this.setMetodo_pago(datos_nomina.get("comprobante_attr_metododepago"));
        this.setNo_cuenta(datos_nomina.get("comprobante_attr_numerocuenta"));
        this.setFormaPago(datos_nomina.get("comprobante_attr_formadepago"));
        this.setCondicionesPago(datos_nomina.get("comprobante_attr_condicionesdepago"));
        
        this.setSello_digital_sat(datos_nomina.get("sello_sat"));
        this.setUuid(datos_nomina.get("uuid"));
        this.setFachaTimbrado(datos_nomina.get("fechaTimbre"));
        this.setNoCertificadoSAT(datos_nomina.get("noCertificadoSAT"));
        this.setCadena_original(datos_nomina.get("cadena_original_timbre"));
        this.setSello_digital(datos_nomina.get("sello_digital_emisor"));
        
        
        
        this.setNo_aprobacion(this.getGralDao().getNoAprobacionFactura(id_empresa, id_sucursal));
        this.setAno_aprobacion(this.getGralDao().getAnoAprobacionFactura(id_empresa, id_sucursal));
        this.setEtiqueta_tipo_doc("RECIBO DE NÓMINA");
        
        
        //percepciones
        this.setSubTotal(datos_nomina.get("comprobante_attr_subtotal"));
        this.setMontoIeps(datos_nomina.get("comprobante_attr_descuento"));
        this.setMontoImpuesto(datos_nomina.get("comprobante_attr_retencion"));
        this.setMontoTotal(datos_nomina.get("comprobante_attr_total"));
        this.setTitulo_moneda(datos_nomina.get("comprobante_attr_moneda"));
        this.setSimbolo_moneda(datos_nomina.get("comprobante_attr_simbolo_moneda"));
        this.setMoneda_abr(datos_nomina.get("comprobante_attr_simbolo_moneda_abr"));
        
        
        this.setPercep_total_excento(datos_nomina.get("comprobante_attr_percep_total_excento"));
        this.setPercep_total_gravado(datos_nomina.get("comprobante_attr_percep_total_gravado"));
        this.setDeduc_total_excento(datos_nomina.get("comprobante_attr_deduc_total_excento"));
        this.setDeduc_total_gravado(datos_nomina.get("comprobante_attr_deduc_total_gravado"));
        
        /*//deducciones
        this.setDeducSubTotal(datos_nomina.get("comprobante_attr_subtotal"));
        this.setDeducDescuento(datos_nomina.get("comprobante_attr_descuento"));
        this.setDeducMontoRetencion(datos_nomina.get("comprobante_attr_retencion"));
        this.setDeducMontoTotal(datos_nomina.get("comprobante_attr_total"));
        //this.setTitulo_moneda(datos_nomina.get("comprobante_attr_moneda"));
        //this.setSimbolo_moneda(datos_nomina.get("comprobante_attr_simbolo_moneda"));*/
        
        
        //Datos del Empleado
        this.setReceptor_regpatronal(datos_nomina.get("comprobante_attr_reg_patronal"));
        this.setReceptor_no_control(datos_nomina.get("numero_control"));            
        this.setReceptor_regimen(datos_nomina.get("comprobante_attr_regimen_contratacion"));
        this.setRegimen_contratacion_titulo(datos_nomina.get("regimen_contratacion_titulo"));
        this.setReceptor_depto(datos_nomina.get("comprobante_attr_depto"));
        this.setReceptor_puesto(datos_nomina.get("comprobante_attr_puesto"));
        this.setReceptor_riesgo_puesto(datos_nomina.get("comprobante_attr_riesgo_puesto"));
        this.setRiesgo_puesto_titulo(datos_nomina.get("riesgo_puesto_titulo"));
        this.setReceptor_tipo_contrato(datos_nomina.get("comprobante_attr_tipo_contrato"));
        this.setReceptor_tipo_jornada(datos_nomina.get("comprobante_attr_tipo_jornada"));
        this.setReceptor_fecha_antiguedad(datos_nomina.get("comprobante_attr_fecha_antiguedad"));
        this.setReceptor_fecha_contrato(datos_nomina.get("comprobante_attr_fecha_contrato"));
        this.setReceptor_periodicidad_pago(datos_nomina.get("comprobante_attr_periodicidad_pago"));
        this.setReceptor_salario_base(datos_nomina.get("comprobante_attr_salario_base"));
        this.setReceptor_sdi(datos_nomina.get("comprobante_attr_salario_integrado"));
        this.setReceptor_fechapago(datos_nomina.get("comprobante_attr_fecha_fecha_pago"));
        this.setReceptor_fechain(datos_nomina.get("comprobante_attr_fecha_ini_pago"));
        this.setReceptor_fechafin(datos_nomina.get("comprobante_attr_fecha_fin_pago"));
        this.setReceptor_ndias(datos_nomina.get("comprobante_attr_no_dias_pago"));
        this.setReceptor_banco(datos_nomina.get("comprobante_attr_banco"));
        this.setNombre_banco(datos_nomina.get("nombre_banco"));
        this.setReceptor_clabe(datos_nomina.get("comprobante_attr_clabe"));

        this.setReceptor_razon_social(datos_nomina.get("comprobante_receptor_attr_nombre"));
        this.setReceptor_curp(datos_nomina.get("comprobante_receptor_attr_curp"));
        this.setReceptor_rfc(datos_nomina.get("comprobante_receptor_attr_rfc"));
        this.setReceptor_calle(datos_nomina.get("comprobante_receptor_domicilio_attr_calle"));
        this.setReceptor_numero(datos_nomina.get("comprobante_receptor_domicilio_attr_nointerior"));
        this.setReceptor_numero_exterior(datos_nomina.get("comprobante_receptor_domicilio_attr_noexterior"));
        this.setReceptor_colonia(datos_nomina.get("comprobante_receptor_domicilio_attr_colonia"));
        this.setReceptor_cp(datos_nomina.get("comprobante_receptor_domicilio_attr_codigopostal"));
        this.setReceptor_municipio(datos_nomina.get("comprobante_receptor_domicilio_attr_municipio"));
        this.setReceptor_estado(datos_nomina.get("comprobante_receptor_domicilio_attr_estado"));
        this.setReceptor_pais(datos_nomina.get("comprobante_receptor_domicilio_attr_pais"));
        this.setReceptor_telefono("");
        this.setMonedaIso(datos_nomina.get("comprobante_attr_moneda"));
        this.setTipoCambio(datos_nomina.get("comprobante_attr_tc"));
        
        this.setLeyenda(datos_nomina.get("leyenda_nomina"));
        
        
        this.setNss(datos_nomina.get("comprobante_attr_imss"));
        
        
        this.setImagen( this.getGralDao().getImagesDir()+this.getEmisora_rfc()+"_logo.png" );
        String tipo = "";
        
        this.setFileout(fileout);
        tipo="ESTE DOCUMENTO ES UNA REPRESENTACIÓN IMPRESA DE UN CFDI";
        
        //cadena para el CBB, solo es para cfdi con timbrado Fiscal
        String cadenaCBB = "?re="+this.getEmisora_rfc()+"&rr="+this.getReceptor_rfc()+"&tt="+StringHelper.roundDouble(this.getMontoTotal(), 6)+"&id="+this.getUuid();
        this.setCadenaCBB(cadenaCBB);
        this.setRutaImagenCBB( this.getGralDao().getTmpDir()+this.getReceptor_rfc()+".png");
        
        HashMap<String, String> datos = new HashMap<String, String>();
        //datos para pie de pagina
        datos.put("cadena", tipo);
        datos.put("codigo1", "");
        datos.put("codigo2", "");
        
        this.setEncabezado(datos);
    }
    
    
    public void ViewPDF() throws URISyntaxException, FileNotFoundException, Exception {
        Font smallFont6 = new Font(Font.FontFamily.HELVETICA,6,Font.NORMAL,BaseColor.BLACK);
        Font smallFont = new Font(Font.FontFamily.HELVETICA,7,Font.NORMAL,BaseColor.BLACK);
        Font smallBoldFont7= new Font(Font.FontFamily.HELVETICA,7,Font.BOLD,BaseColor.BLACK);
        Font largeBoldFont = new Font(Font.FontFamily.HELVETICA,10,Font.BOLD,BaseColor.BLACK);
        PdfPTable tableHeader;
        PdfPCell cellHeader;
        PdfPCell cell;
        CeldaDataNomina tableDataNomina = new CeldaDataNomina();
        TableComprobante tablaComprobante = new TableComprobante();
        TableReceptor tablaReceptor = new TableReceptor();
        tablaConceptos tablaCon = new tablaConceptos();
        celdaDatosFiscales tablaSellos = new celdaDatosFiscales();
        PdfPTable table_leyenda;
        
        ImagenPDF ipdf = new ImagenPDF();
        CeldaPDF cepdf = new CeldaPDF();
        
        try {
            
            PdfCfdiNomina.HeaderFooter event = new PdfCfdiNomina.HeaderFooter(this.getEncabezado());
            Document document = new Document(PageSize.LETTER, -50, -50, 20, 30);
            document.addCreator("gpmarsan@gmail.com");
            PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(this.getFileout()));
            writer.setPageEvent(event);
            document.open();
            
            
            float [] widths = {6,12,6};
            tableHeader = new PdfPTable(widths);
            tableHeader.setKeepTogether(false);
            
            
            //IMAGEN --> logo empresa
            cellHeader = new PdfPCell(ipdf.addContent());
            cellHeader.setBorder(0);
            cellHeader.setUseDescender(true);
            cellHeader.setVerticalAlignment(Element.ALIGN_TOP);
            tableHeader.addCell(cellHeader);
            
            
            //------------------------------------------------------------------
            //AQUI COMIENZA LA TABLA PARA DATOS DE LA EMPRESA-------------------
            PdfPTable tableDatosEmpresa = new PdfPTable(1);
            PdfPCell cellEmp;
            
            //RAZON SOCIAL
            cellEmp = new PdfPCell(new Paragraph(StringHelper.capitalizaString(this.getEmpresa_emisora().toUpperCase()),largeBoldFont));
            cellEmp.setBorder(0);
            cellEmp.setUseAscender(true);
            cellEmp.setHorizontalAlignment(Element.ALIGN_CENTER);
            tableDatosEmpresa.addCell(cellEmp);
            
            //celda vacia
            cellEmp = new PdfPCell(new Paragraph("R.F.C: "+this.getEmisora_rfc().toUpperCase(), smallBoldFont7));
            cellEmp.setBorder(0);
            cellEmp.setHorizontalAlignment(Element.ALIGN_CENTER);
            tableDatosEmpresa.addCell(cellEmp);
            
            
            //String dirEmisor = this.getEmisora_calle()+ " " + this.getEmisora_numero()+ " " + this.getEmisora_colonia()+ "\n" + this.getEmisora_municipio()+ ", " + this.getEmisora_estado()+ ",  "+ " C.P. " + this.getEmisora_cp()+ "\n"  +"    Tel./Fax. " + this.getEmisora_telefono();
            String dirEmisor = this.getEmisora_calle()+ " " + this.getEmisora_numero()+ " " + this.getEmisora_colonia()+ "\n" + this.getEmisora_municipio()+ ", " + this.getEmisora_estado()+ ",  "+ " C.P. " + this.getEmisora_cp();
            
            //cellEmp = new PdfPCell(new Paragraph(dirEmisor.toUpperCase()+"\n"+this.getEmisora_pagina_web(), smallFont));
            cellEmp = new PdfPCell(new Paragraph(dirEmisor.toUpperCase(), smallFont));
            cellEmp.setBorder(0);
            cellEmp.setUseAscender(true);
            cellEmp.setHorizontalAlignment(Element.ALIGN_CENTER);
            tableDatosEmpresa.addCell(cellEmp);
            
            //celda vacia
            cellEmp = new PdfPCell(new Paragraph("", smallFont));
            cellEmp.setBorder(0);
            cellEmp.setFixedHeight(5);
            tableDatosEmpresa.addCell(cellEmp);
            
            
            //AQUI TERMINA LA TABLA PARA DATOS DE LA EMPRESA--------------------
            //------------------------------------------------------------------
            
            //Aqui se agrega la tableDatosEmpresa a la tablaPrincipal
            cellHeader = new PdfPCell(tableDatosEmpresa);
            cellHeader.setBorder(0);
            tableHeader.addCell(cellHeader);
            
            
            ////////////////////////////////////////////////////////////////////////////////
            //aqui se agrega la tabla  superior derecha
            cellHeader = new PdfPCell(cepdf.addContent());
            cellHeader.setBorder(0);
            tableHeader.addCell(cellHeader);
            ////////////////////////////////////////////////////////////////////////////////
            tableHeader.setSpacingAfter(5f);
            
            //AQUI SE AGREGA LA TABLA PRINCIPAL AL ENCABEZADO DEL DOCUMENTO
            document.add(tableHeader);
            
            
            //------------------------------------------------------------------
            //AQUI AGREGAMOS LA TABLA CON LOS DATOS DEL COMROBANTE FISCAL
            document.add(tablaComprobante.addContent());
            //------------------------------------------------------------------
            
            
            //TABLA VACIA-------------------
            PdfPTable tableVacia = new PdfPTable(1);
            cellEmp = new PdfPCell(new Paragraph("",largeBoldFont));
            cellEmp.setBorder(0);
            tableVacia.addCell(cellEmp);
            document.add(tableVacia);
            document.add(tableVacia);
            
                    
            //------------------------------------------------------------------
            //AQUI AGREGAMOS LA TABLA DE DATOS DEL EMPLEADO
            document.add(tablaReceptor.addContent());
            //------------------------------------------------------------------
            
            
            //------------------------------------------------------------------
            //AQUI AGREGAMOS LA TABLA DE DATOS DE LA NOMINA----------------------
            document.add(tableDataNomina.addContent());
            //------------------------------------------------------------------
            
            document.add(tableVacia);
            document.add(tableVacia);
            
            //------------------------------------------------------------------
            //AQUI AGREGAMOS LA TABLA DE PERCEPCIONES, DEDUCCIONES, HORAS EXTRAS, INCAPACIDADES Y TOTALES
            document.add(tablaCon.addContent());
            //------------------------------------------------------------------
            
            //agregar tabla vacia
            document.add(tableVacia);
            document.add(tableVacia);
            document.add(tableVacia);
            
            
            
            
            //Aqui comienza la tabla de para la Leyenda de la Nomina
            float [] widths_table_leyenda = {1,1,1,1,1};
            table_leyenda = new PdfPTable(widths_table_leyenda);
            table_leyenda.setKeepTogether(false);
            
            cell = new PdfPCell(new Paragraph("",smallBoldFont7));
            cell.setBorder(0);
            cell.setColspan(2);
            table_leyenda.addCell(cell);
            
            cell = new PdfPCell(new Paragraph("FIRMA DEL EMPLEADO",smallBoldFont7));
            cell.setBorderWidthBottom(0);
            cell.setBorderWidthLeft(0);
            cell.setBorderWidthRight(0);
            cell.setBorderWidthTop(1);
            cell.setVerticalAlignment(Element.ALIGN_TOP);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            table_leyenda.addCell(cell);
            
            cell = new PdfPCell(new Paragraph("",smallBoldFont7));
            cell.setBorder(0);
            cell.setColspan(2);
            table_leyenda.addCell(cell);
            
            if(!this.getLeyenda().trim().equals("")){
                cell = new PdfPCell(new Paragraph("",smallBoldFont7));
                cell.setBorder(0);
                table_leyenda.addCell(cell);
                
                cell = new PdfPCell(new Paragraph(this.getLeyenda().replace("\"X\"", this.getEmpresa_emisora().toUpperCase()),smallFont6));
                cell.setBorder(0);
                cell.setColspan(3);
                cell.setVerticalAlignment(Element.ALIGN_CENTER);
                cell.setHorizontalAlignment(Element.ALIGN_JUSTIFIED);
                table_leyenda.addCell(cell);
                
                cell = new PdfPCell(new Paragraph("",smallBoldFont7));
                cell.setBorder(0);
                table_leyenda.addCell(cell);
            }
            document.add(table_leyenda);
            
            
            //Agregar tabla vacia
            document.add(tableVacia);
            
            
            //------------------------------------------------------------------
            //AQUI AGREGAMOS LA TABLA DE SELLOS---------------------------------
            document.add(tablaSellos.addContent());
            //------------------------------------------------------------------
            
            
            //CERRAR EL DOCUMENTO
            document.close();
            
        } catch (DocumentException ex) {
            Logger.getLogger(PdfCfdiNomina.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        
        
    }
    
    
    
    
    
    //esta es la tabla que va en la parte Superior Derecha
    private class CeldaPDF {
        public PdfPTable addContent() {
            Font smallFont = new Font(Font.FontFamily.HELVETICA,7,Font.NORMAL,BaseColor.BLACK);
            Font largeBoldFont13 = new Font(Font.FontFamily.HELVETICA,13,Font.BOLD,BaseColor.BLACK);
            Font largeBoldFont13Red = new Font(Font.FontFamily.HELVETICA,13,Font.BOLD,BaseColor.RED);
            
            PdfPTable table = new PdfPTable(1);
            table.setKeepTogether(false);
            PdfPCell cell;
            
            cell = new PdfPCell(new Paragraph(getEtiqueta_tipo_doc(),largeBoldFont13));
            cell.setBorder(0);
            cell.setUseAscender(true);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setUseDescender(true);
            cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            table.addCell(cell);
            
            cell = new PdfPCell(new Paragraph("",largeBoldFont13));
            cell.setBorder(0);
            cell.setUseAscender(true);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setUseDescender(true);
            cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            table.addCell(cell);
            
            cell = new PdfPCell(new Paragraph("CFDI",largeBoldFont13));
            cell.setBorder(0);
            cell.setUseAscender(true);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setUseDescender(true);
            cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            table.addCell(cell);
            
            cell = new PdfPCell(new Paragraph("",largeBoldFont13));
            cell.setBorder(0);
            cell.setUseAscender(true);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setUseDescender(true);
            cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            table.addCell(cell);
            
            //Celda vacia
            cell = new PdfPCell(new Paragraph(getComprobante_cancelado(),largeBoldFont13Red));
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setVerticalAlignment(Element.ALIGN_CENTER);
            if(getComprobante_cancelado().equals("CANCELADO")){
                cell.setBorderWidthLeft(1);
                cell.setBorderWidthRight(1);
                cell.setBorderWidthBottom(1);
                cell.setBorderWidthTop(1);
            }else{
                cell.setBorder(0);
            }
            table.addCell(cell);

            
            return table;
        }
    }
    
    
    
    
    
    
    
    
    //esta es la tabla que va en la parte Superior Derecha
    private class TableComprobante {
        public PdfPTable addContent() {
            Font smallFontBoldBlack6 = new Font(Font.FontFamily.HELVETICA,6,Font.BOLD,BaseColor.BLACK);
            Font smallFontNormalBlack6 = new Font(Font.FontFamily.HELVETICA,6,Font.NORMAL,BaseColor.BLACK);
            Font smallBoldFont7White = new Font(Font.FontFamily.HELVETICA,7,Font.BOLD,BaseColor.WHITE);
            
            float [] widths_table = {2f,5f,0.5f,2.4f,5f};
            PdfPTable table = new PdfPTable(widths_table);
            PdfPCell cell;
            
            cell = new PdfPCell(new Paragraph("COMPROBANTE FISCAL DIGITAL POR INTERNET", smallBoldFont7White));
            cell.setBorderWidthLeft(1);
            cell.setBorderWidthRight(1);
            cell.setBorderWidthBottom(0);
            cell.setBorderWidthTop(0);
            cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
            cell.setHorizontalAlignment(Element.ALIGN_LEFT);
            cell.setBorderColorRight(BaseColor.LIGHT_GRAY);
            cell.setBorderColorLeft(BaseColor.LIGHT_GRAY);
            cell.setColspan(5);
            cell.setUseAscender(true);
            table.addCell(cell);
            
            
            //fila 1
            cell = new PdfPCell(new Paragraph("FOLIO FISCAL", smallFontBoldBlack6));
            cell.setHorizontalAlignment(Element.ALIGN_LEFT);
            cell.setVerticalAlignment(Element.ALIGN_CENTER);
            cell.setBorder(0);
            table.addCell(cell);
            
            
            cell = new PdfPCell(new Paragraph(getUuid(),smallFontNormalBlack6));
            cell.setHorizontalAlignment(Element.ALIGN_LEFT);
            cell.setVerticalAlignment(Element.ALIGN_CENTER);
            cell.setBorder(0);
            table.addCell(cell);
            
            //Celda Vacia
            cell = new PdfPCell(new Paragraph("",smallFontNormalBlack6));
            cell.setHorizontalAlignment(Element.ALIGN_LEFT);
            cell.setVerticalAlignment(Element.ALIGN_CENTER);
            cell.setBorder(0);
            table.addCell(cell);
            
            
            cell = new PdfPCell(new Paragraph("NO. CERTIFICADO", smallFontBoldBlack6));
            cell.setHorizontalAlignment(Element.ALIGN_LEFT);
            cell.setVerticalAlignment(Element.ALIGN_CENTER);
            cell.setBorder(0);
            table.addCell(cell);
            
            cell = new PdfPCell(new Paragraph(getNo_certificado(),smallFontNormalBlack6));
            cell.setHorizontalAlignment(Element.ALIGN_LEFT);
            cell.setVerticalAlignment(Element.ALIGN_CENTER);
            cell.setBorder(0);
            table.addCell(cell);
            
            
            //fila 2
            cell = new PdfPCell(new Paragraph("SERIE Y FOLIO", smallFontBoldBlack6));
            cell.setHorizontalAlignment(Element.ALIGN_LEFT);
            cell.setVerticalAlignment(Element.ALIGN_CENTER);
            cell.setBorder(0);
            table.addCell(cell);
            
            
            cell = new PdfPCell(new Paragraph(getSerie_folio(),smallFontNormalBlack6));
            cell.setHorizontalAlignment(Element.ALIGN_LEFT);
            cell.setVerticalAlignment(Element.ALIGN_CENTER);
            cell.setBorder(0);
            table.addCell(cell);
            
            //Celda Vacia
            cell = new PdfPCell(new Paragraph("",smallFontNormalBlack6));
            cell.setHorizontalAlignment(Element.ALIGN_LEFT);
            cell.setVerticalAlignment(Element.ALIGN_CENTER);
            cell.setBorder(0);
            table.addCell(cell);
            
            cell = new PdfPCell(new Paragraph("REGIMEN FISCAL", smallFontBoldBlack6));
            cell.setHorizontalAlignment(Element.ALIGN_LEFT);
            cell.setVerticalAlignment(Element.ALIGN_CENTER);
            cell.setBorder(0);
            table.addCell(cell);
            
            
            cell = new PdfPCell(new Paragraph(getEmisora_regimen_fiacal(),smallFontNormalBlack6));
            cell.setHorizontalAlignment(Element.ALIGN_LEFT);
            cell.setVerticalAlignment(Element.ALIGN_CENTER);
            cell.setBorder(0);
            table.addCell(cell);
            
            
            
            //fila 3
            cell = new PdfPCell(new Paragraph("LUGAR DE EXPEDICIÓN", smallFontBoldBlack6));
            cell.setHorizontalAlignment(Element.ALIGN_LEFT);
            cell.setVerticalAlignment(Element.ALIGN_CENTER);
            cell.setBorder(0);
            table.addCell(cell);
            
            
            cell = new PdfPCell(new Paragraph(getLugar_expedidion().toUpperCase(),smallFontNormalBlack6));
            cell.setHorizontalAlignment(Element.ALIGN_LEFT);
            cell.setVerticalAlignment(Element.ALIGN_CENTER);
            cell.setBorder(0);
            table.addCell(cell);
            
            //Celda Vacia
            cell = new PdfPCell(new Paragraph("",smallFontNormalBlack6));
            cell.setHorizontalAlignment(Element.ALIGN_LEFT);
            cell.setVerticalAlignment(Element.ALIGN_CENTER);
            cell.setBorder(0);
            table.addCell(cell);
            
            
            cell = new PdfPCell(new Paragraph("FECHA Y HORA DE EMISIÓN", smallFontBoldBlack6));
            cell.setHorizontalAlignment(Element.ALIGN_LEFT);
            cell.setVerticalAlignment(Element.ALIGN_CENTER);
            cell.setBorder(0);
            table.addCell(cell);
            
            cell = new PdfPCell(new Paragraph(getFacha_comprobante(),smallFontNormalBlack6));
            cell.setHorizontalAlignment(Element.ALIGN_LEFT);
            cell.setVerticalAlignment(Element.ALIGN_CENTER);
            cell.setBorder(0);
            table.addCell(cell);
            

            
            //fila 4
            cell = new PdfPCell(new Paragraph("FORMA DE PAGO", smallFontBoldBlack6));
            cell.setHorizontalAlignment(Element.ALIGN_LEFT);
            cell.setVerticalAlignment(Element.ALIGN_CENTER);
            cell.setBorder(0);
            table.addCell(cell);
            
            
            cell = new PdfPCell(new Paragraph(getFormaPago(),smallFontNormalBlack6));
            cell.setHorizontalAlignment(Element.ALIGN_LEFT);
            cell.setVerticalAlignment(Element.ALIGN_CENTER);
            cell.setBorder(0);
            table.addCell(cell);
            
            //Celda Vacia
            cell = new PdfPCell(new Paragraph("",smallFontNormalBlack6));
            cell.setHorizontalAlignment(Element.ALIGN_LEFT);
            cell.setVerticalAlignment(Element.ALIGN_CENTER);
            cell.setBorder(0);
            table.addCell(cell);
            
            
            cell = new PdfPCell(new Paragraph("TIPO COMPROBANTE", smallFontBoldBlack6));
            cell.setHorizontalAlignment(Element.ALIGN_LEFT);
            cell.setVerticalAlignment(Element.ALIGN_CENTER);
            cell.setBorder(0);
            table.addCell(cell);
            
            cell = new PdfPCell(new Paragraph(getEtiqueta_tipo_doc(),smallFontNormalBlack6));
            cell.setHorizontalAlignment(Element.ALIGN_LEFT);
            cell.setVerticalAlignment(Element.ALIGN_CENTER);
            cell.setBorder(0);
            table.addCell(cell);


            
            //fila 5
            cell = new PdfPCell(new Paragraph("MÉTODO DE PAGO", smallFontBoldBlack6));
            cell.setHorizontalAlignment(Element.ALIGN_LEFT);
            cell.setVerticalAlignment(Element.ALIGN_CENTER);
            cell.setBorder(0);
            table.addCell(cell);
            
            
            cell = new PdfPCell(new Paragraph(getMetodo_pago().toUpperCase(),smallFontNormalBlack6));
            cell.setHorizontalAlignment(Element.ALIGN_LEFT);
            cell.setVerticalAlignment(Element.ALIGN_CENTER);
            cell.setBorder(0);
            table.addCell(cell);
            
            //Celda Vacia
            cell = new PdfPCell(new Paragraph("",smallFontNormalBlack6));
            cell.setHorizontalAlignment(Element.ALIGN_LEFT);
            cell.setVerticalAlignment(Element.ALIGN_CENTER);
            cell.setBorder(0);
            table.addCell(cell);
            
            
            cell = new PdfPCell(new Paragraph("NO. DE CUENTA", smallFontBoldBlack6));
            cell.setHorizontalAlignment(Element.ALIGN_LEFT);
            cell.setVerticalAlignment(Element.ALIGN_CENTER);
            cell.setBorder(0);
            table.addCell(cell);
            
            cell = new PdfPCell(new Paragraph(getNo_cuenta(),smallFontNormalBlack6));
            cell.setHorizontalAlignment(Element.ALIGN_LEFT);
            cell.setVerticalAlignment(Element.ALIGN_CENTER);
            cell.setBorder(0);
            table.addCell(cell);

            
            
            //fila 6
            cell = new PdfPCell(new Paragraph("MONEDA", smallFontBoldBlack6));
            cell.setHorizontalAlignment(Element.ALIGN_LEFT);
            cell.setVerticalAlignment(Element.ALIGN_CENTER);
            cell.setBorder(0);
            table.addCell(cell);
            
            
            cell = new PdfPCell(new Paragraph(getMonedaIso().toUpperCase(),smallFontNormalBlack6));
            cell.setHorizontalAlignment(Element.ALIGN_LEFT);
            cell.setVerticalAlignment(Element.ALIGN_CENTER);
            cell.setBorder(0);
            table.addCell(cell);
            
            //Celda Vacia
            cell = new PdfPCell(new Paragraph("",smallFontNormalBlack6));
            cell.setHorizontalAlignment(Element.ALIGN_LEFT);
            cell.setVerticalAlignment(Element.ALIGN_CENTER);
            cell.setBorder(0);
            table.addCell(cell);
            
            
            cell = new PdfPCell(new Paragraph("TIPO DE CAMBIO", smallFontBoldBlack6));
            cell.setHorizontalAlignment(Element.ALIGN_LEFT);
            cell.setVerticalAlignment(Element.ALIGN_CENTER);
            cell.setBorder(0);
            table.addCell(cell);
            
            cell = new PdfPCell(new Paragraph(getTipoCambio(),smallFontNormalBlack6));
            cell.setHorizontalAlignment(Element.ALIGN_LEFT);
            cell.setVerticalAlignment(Element.ALIGN_CENTER);
            cell.setBorder(0);
            table.addCell(cell);
            
            
            return table;
        }
    }
    
    
    
    
    
    
    
    
    
    //Tabla empleado
    private class TableReceptor {
        public PdfPTable addContent() {
            Font smallFontBoldBlack6 = new Font(Font.FontFamily.HELVETICA,6,Font.BOLD,BaseColor.BLACK);
            Font smallFontNormalBlack6 = new Font(Font.FontFamily.HELVETICA,6,Font.NORMAL,BaseColor.BLACK);
            Font smallBoldFont7White = new Font(Font.FontFamily.HELVETICA,7,Font.BOLD,BaseColor.WHITE);
            
            float [] widths_table = {1f,5f,0.6f,2.5f,0.6f,2.5f};
            PdfPTable table = new PdfPTable(widths_table);
            PdfPCell cell;
            
            cell = new PdfPCell(new Paragraph("EMPLEADO", smallBoldFont7White));
            cell.setBorderWidthLeft(1);
            cell.setBorderWidthRight(1);
            cell.setBorderWidthBottom(0);
            cell.setBorderWidthTop(0);
            cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
            cell.setHorizontalAlignment(Element.ALIGN_LEFT);
            cell.setBorderColorRight(BaseColor.LIGHT_GRAY);
            cell.setBorderColorLeft(BaseColor.LIGHT_GRAY);
            cell.setColspan(6);
            cell.setUseAscender(true);
            table.addCell(cell);
            
            
            
            //fila 1
            cell = new PdfPCell(new Paragraph("NOMBRE", smallFontBoldBlack6));
            cell.setHorizontalAlignment(Element.ALIGN_LEFT);
            cell.setVerticalAlignment(Element.ALIGN_CENTER);
            cell.setBorder(0);
            table.addCell(cell);
            
            cell = new PdfPCell(new Paragraph(getReceptor_razon_social(),smallFontNormalBlack6));
            cell.setHorizontalAlignment(Element.ALIGN_LEFT);
            cell.setVerticalAlignment(Element.ALIGN_CENTER);
            cell.setBorder(0);
            table.addCell(cell);
            
            cell = new PdfPCell(new Paragraph("R.F.C.", smallFontBoldBlack6));
            cell.setHorizontalAlignment(Element.ALIGN_LEFT);
            cell.setVerticalAlignment(Element.ALIGN_CENTER);
            cell.setBorder(0);
            table.addCell(cell);
            
            //RFC
            cell = new PdfPCell(new Paragraph(getReceptor_rfc(),smallFontNormalBlack6));
            cell.setHorizontalAlignment(Element.ALIGN_LEFT);
            cell.setVerticalAlignment(Element.ALIGN_CENTER);
            cell.setBorder(0);
            table.addCell(cell);
            
            cell = new PdfPCell(new Paragraph("N.S.S.", smallFontBoldBlack6));
            cell.setHorizontalAlignment(Element.ALIGN_LEFT);
            cell.setVerticalAlignment(Element.ALIGN_CENTER);
            cell.setBorder(0);
            table.addCell(cell);
            
            //NUMERO DE SEGURIDAD SOCIAL
            cell = new PdfPCell(new Paragraph(getNss(),smallFontNormalBlack6));
            cell.setHorizontalAlignment(Element.ALIGN_LEFT);
            cell.setVerticalAlignment(Element.ALIGN_CENTER);
            cell.setBorder(0);
            table.addCell(cell);
            
            
            
            //fila 2
            cell = new PdfPCell(new Paragraph("DIRECCIÓN", smallFontBoldBlack6));
            cell.setHorizontalAlignment(Element.ALIGN_LEFT);
            cell.setVerticalAlignment(Element.ALIGN_CENTER);
            cell.setBorder(0);
            table.addCell(cell);
            
            
            String dir_receptor = getReceptor_calle()+ " " + getReceptor_numero() +" "+ getReceptor_colonia().toUpperCase() + " " + getReceptor_municipio().toUpperCase() + ", " + getReceptor_estado().toUpperCase()+", " + getReceptor_pais().toUpperCase()+ " "+ "C.P. " + getReceptor_cp();
            
            cell = new PdfPCell(new Paragraph(dir_receptor,smallFontNormalBlack6));
            cell.setHorizontalAlignment(Element.ALIGN_LEFT);
            cell.setVerticalAlignment(Element.ALIGN_CENTER);
            cell.setColspan(3);
            cell.setBorder(0);
            table.addCell(cell);
            
            cell = new PdfPCell(new Paragraph("C.U.R.P.", smallFontBoldBlack6));
            cell.setHorizontalAlignment(Element.ALIGN_LEFT);
            cell.setVerticalAlignment(Element.ALIGN_CENTER);
            cell.setBorder(0);
            table.addCell(cell);
            
            cell = new PdfPCell(new Paragraph(getReceptor_curp(),smallFontNormalBlack6));
            cell.setHorizontalAlignment(Element.ALIGN_LEFT);
            cell.setVerticalAlignment(Element.ALIGN_CENTER);
            cell.setBorder(0);
            table.addCell(cell);
            
            return table;
        }
    }
    
    
    
    
    
   //Tabla para datos de la Nomina
    private class CeldaDataNomina {
        public PdfPTable addContent() {
            Font smallFontBoldBlack6 = new Font(Font.FontFamily.HELVETICA,6,Font.BOLD,BaseColor.BLACK);
            Font smallFontNormalBlack6 = new Font(Font.FontFamily.HELVETICA,6,Font.NORMAL,BaseColor.BLACK);
            Font smallBoldFont7White = new Font(Font.FontFamily.HELVETICA,7,Font.BOLD,BaseColor.WHITE);
            
            float [] widths_table = {2f,4.8f,0.5f,2.6f,4.8f};
            PdfPTable table = new PdfPTable(widths_table);
            PdfPCell cell;
            
            /*
            //Fila 1
            cell = new PdfPCell(new Paragraph("COMPROBANTE FISCAL DIGITAL POR INTERNET", smallBoldFont7White));
            cell.setBorderWidthLeft(1);
            cell.setBorderWidthRight(1);
            cell.setBorderWidthBottom(0);
            cell.setBorderWidthTop(0);
            cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
            cell.setHorizontalAlignment(Element.ALIGN_LEFT);
            cell.setBorderColorRight(BaseColor.LIGHT_GRAY);
            cell.setBorderColorLeft(BaseColor.LIGHT_GRAY);
            cell.setColspan(5);
            cell.setUseAscender(true);
            table.addCell(cell);
            */
            
            //fila 1
            cell = new PdfPCell(new Paragraph("NÚMERO DE EMPLEADO", smallFontBoldBlack6));
            cell.setHorizontalAlignment(Element.ALIGN_LEFT);
            cell.setVerticalAlignment(Element.ALIGN_CENTER);
            cell.setBorder(0);
            table.addCell(cell);
            
            cell = new PdfPCell(new Paragraph(getReceptor_no_control(),smallFontNormalBlack6));
            cell.setHorizontalAlignment(Element.ALIGN_LEFT);
            cell.setVerticalAlignment(Element.ALIGN_CENTER);
            cell.setBorder(0);
            table.addCell(cell);
            
            //Celda Vacia
            cell = new PdfPCell(new Paragraph("",smallFontNormalBlack6));
            cell.setHorizontalAlignment(Element.ALIGN_LEFT);
            cell.setVerticalAlignment(Element.ALIGN_CENTER);
            cell.setBorder(0);
            table.addCell(cell);
            
            cell = new PdfPCell(new Paragraph("REGISTRO PATRONAL", smallFontBoldBlack6));
            cell.setHorizontalAlignment(Element.ALIGN_LEFT);
            cell.setVerticalAlignment(Element.ALIGN_CENTER);
            cell.setBorder(0);
            table.addCell(cell);
            
            cell = new PdfPCell(new Paragraph(getReceptor_regpatronal(),smallFontNormalBlack6));
            cell.setHorizontalAlignment(Element.ALIGN_LEFT);
            cell.setVerticalAlignment(Element.ALIGN_CENTER);
            cell.setBorder(0);
            table.addCell(cell);
            
            
            
            
            //Fila 2
            cell = new PdfPCell(new Paragraph("DEPARTAMENTO", smallFontBoldBlack6));
            cell.setHorizontalAlignment(Element.ALIGN_LEFT);
            cell.setVerticalAlignment(Element.ALIGN_CENTER);
            cell.setBorder(0);
            table.addCell(cell);
            
            cell = new PdfPCell(new Paragraph(getReceptor_depto().toUpperCase(),smallFontNormalBlack6));
            cell.setHorizontalAlignment(Element.ALIGN_LEFT);
            cell.setVerticalAlignment(Element.ALIGN_CENTER);
            cell.setBorder(0);
            table.addCell(cell);
            
            //Celda Vacia
            cell = new PdfPCell(new Paragraph("",smallFontNormalBlack6));
            cell.setHorizontalAlignment(Element.ALIGN_LEFT);
            cell.setVerticalAlignment(Element.ALIGN_CENTER);
            cell.setBorder(0);
            table.addCell(cell);
            
            cell = new PdfPCell(new Paragraph("PUESTO", smallFontBoldBlack6));
            cell.setHorizontalAlignment(Element.ALIGN_LEFT);
            cell.setVerticalAlignment(Element.ALIGN_CENTER);
            cell.setBorder(0);
            table.addCell(cell);
            
            cell = new PdfPCell(new Paragraph(getReceptor_puesto().toUpperCase(),smallFontNormalBlack6));
            cell.setHorizontalAlignment(Element.ALIGN_LEFT);
            cell.setVerticalAlignment(Element.ALIGN_CENTER);
            cell.setBorder(0);
            table.addCell(cell);
            
            
            
            
            
            
            
            //Fila 3
            cell = new PdfPCell(new Paragraph("TIPO DE CONTRATO", smallFontBoldBlack6));
            cell.setHorizontalAlignment(Element.ALIGN_LEFT);
            cell.setVerticalAlignment(Element.ALIGN_CENTER);
            cell.setBorder(0);
            table.addCell(cell);
            
            cell = new PdfPCell(new Paragraph(getReceptor_tipo_contrato().toUpperCase(),smallFontNormalBlack6));
            cell.setHorizontalAlignment(Element.ALIGN_LEFT);
            cell.setVerticalAlignment(Element.ALIGN_CENTER);
            cell.setBorder(0);
            table.addCell(cell);
            
            //Celda Vacia
            cell = new PdfPCell(new Paragraph("",smallFontNormalBlack6));
            cell.setHorizontalAlignment(Element.ALIGN_LEFT);
            cell.setVerticalAlignment(Element.ALIGN_CENTER);
            cell.setBorder(0);
            table.addCell(cell);
            
            cell = new PdfPCell(new Paragraph("RÉGIMEN DE CONTRATACIÓN", smallFontBoldBlack6));
            cell.setHorizontalAlignment(Element.ALIGN_LEFT);
            cell.setVerticalAlignment(Element.ALIGN_CENTER);
            cell.setBorder(0);
            table.addCell(cell);
            
            cell = new PdfPCell(new Paragraph(getReceptor_regimen() + " " +getRegimen_contratacion_titulo().toUpperCase(),smallFontNormalBlack6));
            cell.setHorizontalAlignment(Element.ALIGN_LEFT);
            cell.setVerticalAlignment(Element.ALIGN_CENTER);
            cell.setBorder(0);
            table.addCell(cell);
            
            
            
            
            //Fila 4
            cell = new PdfPCell(new Paragraph("FECHA DE CONTRATO", smallFontBoldBlack6));
            cell.setHorizontalAlignment(Element.ALIGN_LEFT);
            cell.setVerticalAlignment(Element.ALIGN_CENTER);
            cell.setBorder(0);
            table.addCell(cell);
            
            cell = new PdfPCell(new Paragraph(geteReceptor_fecha_contrato(),smallFontNormalBlack6));
            cell.setHorizontalAlignment(Element.ALIGN_LEFT);
            cell.setVerticalAlignment(Element.ALIGN_CENTER);
            cell.setBorder(0);
            table.addCell(cell);
            
            //Celda Vacia
            cell = new PdfPCell(new Paragraph("",smallFontNormalBlack6));
            cell.setHorizontalAlignment(Element.ALIGN_LEFT);
            cell.setVerticalAlignment(Element.ALIGN_CENTER);
            cell.setBorder(0);
            table.addCell(cell);
            
            cell = new PdfPCell(new Paragraph("ANTIGÜEDAD", smallFontBoldBlack6));
            cell.setHorizontalAlignment(Element.ALIGN_LEFT);
            cell.setVerticalAlignment(Element.ALIGN_CENTER);
            cell.setBorder(0);
            table.addCell(cell);
            
            String etiqueta_semanas="";
            if(!getReceptor_fecha_antiguedad().equals("")){
                if(Double.parseDouble(getReceptor_fecha_antiguedad())>0){
                    etiqueta_semanas = "SEMANAS";
                }
            }
            
            cell = new PdfPCell(new Paragraph(getReceptor_fecha_antiguedad() + " " + etiqueta_semanas,smallFontNormalBlack6));
            cell.setHorizontalAlignment(Element.ALIGN_LEFT);
            cell.setVerticalAlignment(Element.ALIGN_CENTER);
            cell.setBorder(0);
            table.addCell(cell);
            
            
            
            
            //Fila 5
            cell = new PdfPCell(new Paragraph("TIPO DE JORNADA", smallFontBoldBlack6));
            cell.setHorizontalAlignment(Element.ALIGN_LEFT);
            cell.setVerticalAlignment(Element.ALIGN_CENTER);
            cell.setBorder(0);
            table.addCell(cell);
            
            cell = new PdfPCell(new Paragraph(getReceptor_tipo_jornada().toUpperCase(),smallFontNormalBlack6));
            cell.setHorizontalAlignment(Element.ALIGN_LEFT);
            cell.setVerticalAlignment(Element.ALIGN_CENTER);
            cell.setBorder(0);
            table.addCell(cell);
            
            //Celda Vacia
            cell = new PdfPCell(new Paragraph("",smallFontNormalBlack6));
            cell.setHorizontalAlignment(Element.ALIGN_LEFT);
            cell.setVerticalAlignment(Element.ALIGN_CENTER);
            cell.setBorder(0);
            table.addCell(cell);
            
            cell = new PdfPCell(new Paragraph("RIESGO DE PUESTO", smallFontBoldBlack6));
            cell.setHorizontalAlignment(Element.ALIGN_LEFT);
            cell.setVerticalAlignment(Element.ALIGN_CENTER);
            cell.setBorder(0);
            table.addCell(cell);
            
            cell = new PdfPCell(new Paragraph(getReceptor_riesgo_puesto() + " " + getRiesgo_puesto_titulo().toUpperCase(),smallFontNormalBlack6));
            cell.setHorizontalAlignment(Element.ALIGN_LEFT);
            cell.setVerticalAlignment(Element.ALIGN_CENTER);
            cell.setBorder(0);
            table.addCell(cell);
            
            
            
            
            //Fila 6
            cell = new PdfPCell(new Paragraph("BANCO", smallFontBoldBlack6));
            cell.setHorizontalAlignment(Element.ALIGN_LEFT);
            cell.setVerticalAlignment(Element.ALIGN_CENTER);
            cell.setBorder(0);
            table.addCell(cell);
            
            cell = new PdfPCell(new Paragraph(getReceptor_banco()+" " + getNombre_banco().toUpperCase(),smallFontNormalBlack6));
            cell.setHorizontalAlignment(Element.ALIGN_LEFT);
            cell.setVerticalAlignment(Element.ALIGN_CENTER);
            cell.setBorder(0);
            table.addCell(cell);
            
            //Celda Vacia
            cell = new PdfPCell(new Paragraph("",smallFontNormalBlack6));
            cell.setHorizontalAlignment(Element.ALIGN_LEFT);
            cell.setVerticalAlignment(Element.ALIGN_CENTER);
            cell.setBorder(0);
            table.addCell(cell);
            
            cell = new PdfPCell(new Paragraph("SALARIO BASE DE COTIZACIÓN", smallFontBoldBlack6));
            cell.setHorizontalAlignment(Element.ALIGN_LEFT);
            cell.setVerticalAlignment(Element.ALIGN_CENTER);
            cell.setBorder(0);
            table.addCell(cell);
            
            cell = new PdfPCell(new Paragraph(getReceptor_salario_base(),smallFontNormalBlack6));
            cell.setHorizontalAlignment(Element.ALIGN_LEFT);
            cell.setVerticalAlignment(Element.ALIGN_CENTER);
            cell.setBorder(0);
            table.addCell(cell);
            
            
            
            
            //Fila 7
            cell = new PdfPCell(new Paragraph("CLABE", smallFontBoldBlack6));
            cell.setHorizontalAlignment(Element.ALIGN_LEFT);
            cell.setVerticalAlignment(Element.ALIGN_CENTER);
            cell.setBorder(0);
            table.addCell(cell);
            
            cell = new PdfPCell(new Paragraph(getReceptor_clabe(),smallFontNormalBlack6));
            cell.setHorizontalAlignment(Element.ALIGN_LEFT);
            cell.setVerticalAlignment(Element.ALIGN_CENTER);
            cell.setBorder(0);
            table.addCell(cell);
            
            //Celda Vacia
            cell = new PdfPCell(new Paragraph("",smallFontNormalBlack6));
            cell.setHorizontalAlignment(Element.ALIGN_LEFT);
            cell.setVerticalAlignment(Element.ALIGN_CENTER);
            cell.setBorder(0);
            table.addCell(cell);
            
            cell = new PdfPCell(new Paragraph("SALARIO DIARIO INTEGRADO", smallFontBoldBlack6));
            cell.setHorizontalAlignment(Element.ALIGN_LEFT);
            cell.setVerticalAlignment(Element.ALIGN_CENTER);
            cell.setBorder(0);
            table.addCell(cell);
            
            cell = new PdfPCell(new Paragraph(getReceptor_sdi(),smallFontNormalBlack6));
            cell.setHorizontalAlignment(Element.ALIGN_LEFT);
            cell.setVerticalAlignment(Element.ALIGN_CENTER);
            cell.setBorder(0);
            table.addCell(cell);
            
            

            

            
            //Fila 8
            cell = new PdfPCell(new Paragraph("PERIODICIDAD DE PAGO", smallFontBoldBlack6));
            cell.setHorizontalAlignment(Element.ALIGN_LEFT);
            cell.setVerticalAlignment(Element.ALIGN_CENTER);
            cell.setBorder(0);
            table.addCell(cell);
            
            cell = new PdfPCell(new Paragraph(getReceptor_periodicidad_pago().toUpperCase(),smallFontNormalBlack6));
            cell.setHorizontalAlignment(Element.ALIGN_LEFT);
            cell.setVerticalAlignment(Element.ALIGN_CENTER);
            cell.setBorder(0);
            table.addCell(cell);
            
            //Celda Vacia
            cell = new PdfPCell(new Paragraph("",smallFontNormalBlack6));
            cell.setHorizontalAlignment(Element.ALIGN_LEFT);
            cell.setVerticalAlignment(Element.ALIGN_CENTER);
            cell.setBorder(0);
            table.addCell(cell);
            
            cell = new PdfPCell(new Paragraph("FECHA DE PAGO", smallFontBoldBlack6));
            cell.setHorizontalAlignment(Element.ALIGN_LEFT);
            cell.setVerticalAlignment(Element.ALIGN_CENTER);
            cell.setBorder(0);
            table.addCell(cell);
            
            cell = new PdfPCell(new Paragraph(getReceptor_fechapago(),smallFontNormalBlack6));
            cell.setHorizontalAlignment(Element.ALIGN_LEFT);
            cell.setVerticalAlignment(Element.ALIGN_CENTER);
            cell.setBorder(0);
            table.addCell(cell);
            
            
            
            //Fila 9
            cell = new PdfPCell(new Paragraph("FECHA INICIAL DE PAGO", smallFontBoldBlack6));
            cell.setHorizontalAlignment(Element.ALIGN_LEFT);
            cell.setVerticalAlignment(Element.ALIGN_CENTER);
            cell.setBorder(0);
            table.addCell(cell);
            
            cell = new PdfPCell(new Paragraph(getReceptor_fechain(),smallFontNormalBlack6));
            cell.setHorizontalAlignment(Element.ALIGN_LEFT);
            cell.setVerticalAlignment(Element.ALIGN_CENTER);
            cell.setBorder(0);
            table.addCell(cell);
            
            //Celda Vacia
            cell = new PdfPCell(new Paragraph("",smallFontNormalBlack6));
            cell.setHorizontalAlignment(Element.ALIGN_LEFT);
            cell.setVerticalAlignment(Element.ALIGN_CENTER);
            cell.setBorder(0);
            table.addCell(cell);
            
            cell = new PdfPCell(new Paragraph("FECHA FINAL DE PAGO", smallFontBoldBlack6));
            cell.setHorizontalAlignment(Element.ALIGN_LEFT);
            cell.setVerticalAlignment(Element.ALIGN_CENTER);
            cell.setBorder(0);
            table.addCell(cell);
            
            cell = new PdfPCell(new Paragraph(getReceptor_fechafin(),smallFontNormalBlack6));
            cell.setHorizontalAlignment(Element.ALIGN_LEFT);
            cell.setVerticalAlignment(Element.ALIGN_CENTER);
            cell.setBorder(0);
            table.addCell(cell);
            
            
            
            //Fila 10
            cell = new PdfPCell(new Paragraph("NO. DE DÍAS PAGADOS", smallFontBoldBlack6));
            cell.setHorizontalAlignment(Element.ALIGN_LEFT);
            cell.setVerticalAlignment(Element.ALIGN_CENTER);
            cell.setBorder(0);
            table.addCell(cell);
            
            cell = new PdfPCell(new Paragraph(getReceptor_ndias(),smallFontNormalBlack6));
            cell.setHorizontalAlignment(Element.ALIGN_LEFT);
            cell.setVerticalAlignment(Element.ALIGN_CENTER);
            cell.setBorder(0);
            table.addCell(cell);
            
            //Celda Vacia
            cell = new PdfPCell(new Paragraph("",smallFontNormalBlack6));
            cell.setHorizontalAlignment(Element.ALIGN_LEFT);
            cell.setVerticalAlignment(Element.ALIGN_CENTER);
            cell.setBorder(0);
            table.addCell(cell);
            
            cell = new PdfPCell(new Paragraph("", smallFontBoldBlack6));
            cell.setHorizontalAlignment(Element.ALIGN_LEFT);
            cell.setVerticalAlignment(Element.ALIGN_CENTER);
            cell.setBorder(0);
            table.addCell(cell);
            
            cell = new PdfPCell(new Paragraph("",smallFontNormalBlack6));
            cell.setHorizontalAlignment(Element.ALIGN_LEFT);
            cell.setVerticalAlignment(Element.ALIGN_CENTER);
            cell.setBorder(0);
            table.addCell(cell);
            

            return table;
        }
    }
    
    
    
    //esta es la tabla para los datos del CLIENTE
    private class tablaConceptos {
        public PdfPTable addContent() {
            Font smallFont = new Font(Font.FontFamily.HELVETICA,7,Font.NORMAL,BaseColor.BLACK);
            //Font smallBoldFont = new Font(Font.FontFamily.HELVETICA, 6, Font.BOLD, BaseColor.WHITE);
            Font smallBoldFont = new Font(Font.FontFamily.HELVETICA, 6, Font.BOLD, BaseColor.BLACK);
            Font smallBoldFont7Black= new Font(Font.FontFamily.HELVETICA,7,Font.BOLD,BaseColor.BLACK);
            Font smallBoldFont7White= new Font(Font.FontFamily.HELVETICA,7,Font.BOLD,BaseColor.WHITE);
            
            //tabla contenedor
            float [] widthscont = {8f,8f};
            PdfPTable table4 = new PdfPTable(widthscont);
            PdfPCell cell;
            
            //fila 1 -> Columna 1 table4
            cell = new PdfPCell(new Paragraph("PERCEPCIONES", smallBoldFont7White));
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setVerticalAlignment(Element.ALIGN_CENTER);
            cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
            cell.setBorderWidthBottom(0);
            cell.setBorderWidthTop(0);
            cell.setBorderWidthLeft(1);
            cell.setBorderWidthRight(0.5f);
            cell.setBorderColorRight(BaseColor.WHITE);
            cell.setBorderColorLeft(BaseColor.LIGHT_GRAY);
            cell.setUseAscender(true);
            table4.addCell(cell);
            
             //fila  1 -> Columna 2 table4
            cell = new PdfPCell(new Paragraph("DEDUCCIONES", smallBoldFont7White));
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setVerticalAlignment(Element.ALIGN_CENTER);
            cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
            cell.setBorderWidthBottom(0);
            cell.setBorderWidthTop(0);
            cell.setBorderWidthLeft(0);
            cell.setBorderWidthRight(1);
            cell.setBorderColorRight(BaseColor.LIGHT_GRAY);
            cell.setUseAscender(true);
            table4.addCell(cell);
            
            
            
            String[] columnas_percep = {"Tipo","Clave","Concepto","Importe Gravado","Importe Excento"};
            
            //INICIA TABLA DE PERCEPCIONES
            float [] widths_percep = {
                0.3f, //TIPO PERCEPCION
                0.4f,//CLAVE
                2f, //CONCEPTO
                0.8f, //IMPORTE GRAVADO
                0.8f //IMPORTE EXCENTO
            };
            
            PdfPTable table = new PdfPTable(widths_percep);
            table.setKeepTogether(false);
            
            List<String>  lista_columnas = (List<String>) Arrays.asList(columnas_percep);
            Integer contador = 0;
            PdfPCell cellX;
            
            for ( String columna_titulo : lista_columnas){
                cellX = new PdfPCell(new Paragraph(columna_titulo,smallBoldFont));
                cellX.setUseAscender(true);
                cellX.setUseDescender(true);
                //cellX.setBackgroundColor(BaseColor.BLACK);
                
                if(columna_titulo.equals("Tipo de Percepción")){
                    cellX.setHorizontalAlignment(Element.ALIGN_LEFT);
                    cellX.setVerticalAlignment(Element.ALIGN_CENTER);
                }
                if(columna_titulo.equals("Clave")){
                    cellX.setHorizontalAlignment(Element.ALIGN_LEFT);
                    cellX.setVerticalAlignment(Element.ALIGN_CENTER);
                }
                if(columna_titulo.equals("Concepto")){
                    cellX.setHorizontalAlignment(Element.ALIGN_LEFT);
                    cellX.setVerticalAlignment(Element.ALIGN_CENTER);
                }
                if(columna_titulo.equals("Importe Gravado")){
                    cellX.setHorizontalAlignment(Element.ALIGN_CENTER);
                    cellX.setVerticalAlignment(Element.ALIGN_CENTER);
                }
                if(columna_titulo.equals("Importe Excento")){
                    cellX.setHorizontalAlignment(Element.ALIGN_CENTER);
                    cellX.setVerticalAlignment(Element.ALIGN_CENTER);
                }

                table.addCell(cellX);
            }
            
            //Percepciones
           for (LinkedHashMap<String,String> registro : getRows()){
                //Indices del HashMap que representa el row
               String[] wordList1 = {"TipoPercepcion","Clave","Concepto","ImporteGravado","ImporteExento"};
                List<String>  indices = (List<String>) Arrays.asList(wordList1);
                
                for (String omega : indices){
                    PdfPCell celda;
                    
                    if (omega.equals("TipoPercepcion")){
                        celda = new PdfPCell(new Paragraph(registro.get(omega).toString(),smallFont));
                        celda.setHorizontalAlignment(Element.ALIGN_LEFT);
                        celda.setVerticalAlignment(Element.ALIGN_TOP);
                        celda.setBorderWidthBottom(0);
                        celda.setBorderWidthLeft(0);
                        celda.setBorderWidthRight(0.5f);
                        celda.setBorderWidthTop(0);
                        celda.setBorderColorRight(BaseColor.LIGHT_GRAY);
                        table.addCell(celda);
                    }
                    if (omega.equals("Clave")){
                        celda = new PdfPCell(new Paragraph(registro.get(omega).toString(),smallFont));
                        celda.setHorizontalAlignment(Element.ALIGN_LEFT);
                        celda.setVerticalAlignment(Element.ALIGN_TOP);
                        celda.setBorderWidthBottom(0);
                        celda.setBorderWidthLeft(0);
                        celda.setBorderWidthTop(0);
                        celda.setBorderWidthRight(0.5f);
                        celda.setBorderColorRight(BaseColor.LIGHT_GRAY);
                        table.addCell(celda);
                    }
                    if (omega.equals("Concepto")){
                        celda = new PdfPCell(new Paragraph(registro.get(omega).toString().toUpperCase(),smallFont));
                        celda.setHorizontalAlignment(Element.ALIGN_LEFT);
                        celda.setVerticalAlignment(Element.ALIGN_TOP);
                        celda.setBorderWidthBottom(0);
                        celda.setBorderWidthLeft(0);
                        celda.setBorderWidthTop(0);
                        celda.setBorderWidthRight(0.5f);
                        celda.setBorderColorRight(BaseColor.LIGHT_GRAY);
                        table.addCell(celda);
                    }
                    if (omega.equals("ImporteGravado")){
                        celda= new PdfPCell(new Paragraph(StringHelper.AgregaComas(StringHelper.roundDouble(registro.get(omega).toString(),2)),smallFont));
                        celda.setHorizontalAlignment(Element.ALIGN_RIGHT);
                        celda.setVerticalAlignment(Element.ALIGN_TOP);
                        celda.setBorderWidthBottom(0);
                        celda.setBorderWidthLeft(0);
                        celda.setBorderWidthTop(0);
                        celda.setBorderWidthRight(0.5f);
                        celda.setBorderColorRight(BaseColor.LIGHT_GRAY);
                        table.addCell(celda);
                    }
                    if (omega.equals("ImporteExento")){
                        celda= new PdfPCell(new Paragraph(StringHelper.AgregaComas(StringHelper.roundDouble(registro.get(omega).toString(),2)),smallFont));
                        celda.setHorizontalAlignment(Element.ALIGN_RIGHT);
                        celda.setVerticalAlignment(Element.ALIGN_TOP);
                        celda.setBorderWidthRight(0);
                        celda.setBorderWidthBottom(0);
                        celda.setBorderWidthLeft(0);
                        celda.setBorderWidthTop(0);
                        table.addCell(celda);
                    }
                }
            }
           
            //agregar tabla de condiciones de venta y pago
            cell = new PdfPCell(table);
            cell.setUseAscender(true);
            cell.setUseDescender(true);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setVerticalAlignment(Element.ALIGN_TOP);
            table4.addCell(cell);
            
            
            
           
            
            
            /*---------------------------------------------------------------------------------------------------------------------*/
            //INICIA TABLA DE DEDUCCIONES
            
            String[] columnas_deduc = {"Tipo","Clave","Concepto","Importe Gravado","Importe Excento"};
            
            float [] widths_deduc = {
                0.3f, //TIPO DEDUCCIÓN
                0.4f,//CLAVE
                2f, //CONCEPTO
                0.8f, //IMPORTE GRAVADO
                0.8f //IMPORTE EXCENTO
            };
            
            PdfPTable tabled = new PdfPTable(widths_deduc);
            tabled.setKeepTogether(false);
            
            List<String>  lista_columnas2 = (List<String>) Arrays.asList(columnas_deduc);
            Integer contador2 = 0;
            PdfPCell cellX2;
            
            for ( String columna_titulo2 : lista_columnas2){
                cellX2 = new PdfPCell(new Paragraph(columna_titulo2,smallBoldFont));
                cellX2.setUseAscender(true);
                cellX2.setUseDescender(true);
                //cellX2.setBackgroundColor(BaseColor.BLACK);
                
                if(columna_titulo2.equals("Tipo de Deducción")){
                    cellX2.setHorizontalAlignment(Element.ALIGN_LEFT);
                    cellX2.setVerticalAlignment(Element.ALIGN_CENTER);
                }
                if(columna_titulo2.equals("Clave")){
                    cellX2.setHorizontalAlignment(Element.ALIGN_LEFT);
                    cellX2.setVerticalAlignment(Element.ALIGN_CENTER);
                }
                if(columna_titulo2.equals("Concepto")){
                    cellX2.setHorizontalAlignment(Element.ALIGN_LEFT);
                    cellX2.setVerticalAlignment(Element.ALIGN_CENTER);
                }
                if(columna_titulo2.equals("Importe Gravado")){
                    cellX2.setHorizontalAlignment(Element.ALIGN_CENTER);
                    cellX2.setVerticalAlignment(Element.ALIGN_CENTER);
                }
                if(columna_titulo2.equals("Importe Excento")){
                    cellX2.setHorizontalAlignment(Element.ALIGN_CENTER);
                    cellX2.setVerticalAlignment(Element.ALIGN_CENTER);
                }
                tabled.addCell(cellX2);
            }
            
           //Deducciones
           for (LinkedHashMap<String,String> registro : getRowsd()){
                //Indices del HashMap que representa el row
                String[] wordList2 = {"TipoDeduccion","Clave","Concepto","ImporteGravado","ImporteExento"};
                List<String>  indices2 = (List<String>) Arrays.asList(wordList2);
                
                for (String omega : indices2){
                    PdfPCell celdad;
                    
                    if (omega.equals("TipoDeduccion")){
                        //celdad = new PdfPCell(new Paragraph(String.valueOf(registro.get("sku")),fuenteCont));
                        celdad = new PdfPCell(new Paragraph(registro.get(omega).toString(),smallFont));
                        celdad.setHorizontalAlignment(Element.ALIGN_LEFT);
                        celdad.setVerticalAlignment(Element.ALIGN_TOP);
                        celdad.setBorderWidthBottom(0);
                        celdad.setBorderWidthLeft(0);
                        celdad.setBorderWidthRight(0.5f);
                        celdad.setBorderWidthTop(0);
                        celdad.setBorderColorRight(BaseColor.LIGHT_GRAY);
                        tabled.addCell(celdad);
                    }
                    
                    if (omega.equals("Clave")){
                        celdad = new PdfPCell(new Paragraph(registro.get(omega).toString(),smallFont));
                        celdad.setHorizontalAlignment(Element.ALIGN_LEFT);
                        celdad.setVerticalAlignment(Element.ALIGN_TOP);
                        celdad.setBorderWidthBottom(0);
                        celdad.setBorderWidthLeft(0);
                        celdad.setBorderWidthTop(0);
                        celdad.setBorderWidthRight(0.5f);
                        celdad.setBorderColorRight(BaseColor.LIGHT_GRAY);
                        tabled.addCell(celdad);
                    }
                    
                    
                    if (omega.equals("Concepto")){
                        celdad = new PdfPCell(new Paragraph(registro.get(omega).toString().toUpperCase(),smallFont));
                        celdad.setHorizontalAlignment(Element.ALIGN_LEFT);
                        celdad.setVerticalAlignment(Element.ALIGN_TOP);
                        celdad.setBorderWidthBottom(0);
                        celdad.setBorderWidthLeft(0);
                        celdad.setBorderWidthTop(0);
                        celdad.setBorderWidthRight(0.5f);
                        celdad.setBorderColorRight(BaseColor.LIGHT_GRAY);
                        tabled.addCell(celdad);
                    }
                    
                    if (omega.equals("ImporteGravado")){
                        celdad= new PdfPCell(new Paragraph(StringHelper.AgregaComas(StringHelper.roundDouble(registro.get(omega).toString(),2)),smallFont));
                        celdad.setHorizontalAlignment(Element.ALIGN_RIGHT);
                        celdad.setVerticalAlignment(Element.ALIGN_TOP);
                        celdad.setBorderWidthBottom(0);
                        celdad.setBorderWidthLeft(0);
                        celdad.setBorderWidthTop(0);
                        celdad.setBorderWidthRight(0.5f);
                        celdad.setBorderColorRight(BaseColor.LIGHT_GRAY);
                        tabled.addCell(celdad);
                    }
                    
                    if (omega.equals("ImporteExento")){
                        celdad= new PdfPCell(new Paragraph(StringHelper.AgregaComas(StringHelper.roundDouble(registro.get(omega).toString(),2)),smallFont));
                        celdad.setHorizontalAlignment(Element.ALIGN_RIGHT);
                        celdad.setVerticalAlignment(Element.ALIGN_TOP);
                        celdad.setBorderWidthRight(0);
                        celdad.setBorderWidthBottom(0);
                        celdad.setBorderWidthLeft(0);
                        celdad.setBorderWidthTop(0);
                        tabled.addCell(celdad);
                    }
                }
                contador2++;
            }
           
            //Agregar tabla deducciones
            cell = new PdfPCell(tabled);
            cell.setUseAscender(true);
            cell.setUseDescender(true);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setVerticalAlignment(Element.ALIGN_TOP);
            table4.addCell(cell);
           
            
            
            
            
            
            
            
            
            //Crear tabla de SUMAS DE PERCEPCIONES
            float [] widths_sumas_percep = {2.7f,0.8f,0.8f};
            PdfPTable table_sumas_percep = new PdfPTable(widths_sumas_percep);
            tabled.setKeepTogether(false);
            cellX2 = new PdfPCell(new Paragraph("Suma de las Percepciones",smallBoldFont));
            cellX2.setHorizontalAlignment(Element.ALIGN_LEFT);
            cellX2.setVerticalAlignment(Element.ALIGN_CENTER);
            cellX2.setBorder(0);
            table_sumas_percep.addCell(cellX2);
            
            cellX2 = new PdfPCell(new Paragraph(StringHelper.AgregaComas(StringHelper.roundDouble(getPercep_total_gravado(),2)),smallBoldFont));
            cellX2.setHorizontalAlignment(Element.ALIGN_RIGHT);
            cellX2.setVerticalAlignment(Element.ALIGN_CENTER);
            cellX2.setBorder(0);
            table_sumas_percep.addCell(cellX2);
            
            cellX2 = new PdfPCell(new Paragraph(StringHelper.AgregaComas(StringHelper.roundDouble(getPercep_total_excento(),2)),smallBoldFont));
            cellX2.setHorizontalAlignment(Element.ALIGN_RIGHT);
            cellX2.setVerticalAlignment(Element.ALIGN_CENTER);
            cellX2.setBorder(0);
            table_sumas_percep.addCell(cellX2);
            
            //Agregar tabla  SUMAS DE PERCEPCIONES
            cell = new PdfPCell(table_sumas_percep);
            cell.setUseAscender(true);
            cell.setUseDescender(true);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setVerticalAlignment(Element.ALIGN_TOP);
            table4.addCell(cell);
            
            
            
            //Crear tabla de SUMAS DE DEDUCCIONES
            float [] widths_sumas_deduc = {2.7f,0.8f,0.8f};
            PdfPTable table_sumas_deduc = new PdfPTable(widths_sumas_deduc);
            tabled.setKeepTogether(false);
            cellX2 = new PdfPCell(new Paragraph("Suma de las Deducciones",smallBoldFont));
            cellX2.setHorizontalAlignment(Element.ALIGN_LEFT);
            cellX2.setVerticalAlignment(Element.ALIGN_CENTER);
            cellX2.setBorder(0);
            table_sumas_deduc.addCell(cellX2);
            
            cellX2 = new PdfPCell(new Paragraph(StringHelper.AgregaComas(StringHelper.roundDouble(getDeduc_total_gravado(),2)),smallBoldFont));
            cellX2.setHorizontalAlignment(Element.ALIGN_RIGHT);
            cellX2.setBorder(0);
            table_sumas_deduc.addCell(cellX2);
            
            cellX2 = new PdfPCell(new Paragraph(StringHelper.AgregaComas(StringHelper.roundDouble(getDeduc_total_excento(),2)),smallBoldFont));
            cellX2.setHorizontalAlignment(Element.ALIGN_RIGHT);
            cellX2.setVerticalAlignment(Element.ALIGN_CENTER);
            cellX2.setBorder(0);
            table_sumas_deduc.addCell(cellX2);
            
            //Agregar tabla  SUMAS DE DEDUCCIONES
            cell = new PdfPCell(table_sumas_deduc);
            cell.setUseAscender(true);
            cell.setUseDescender(true);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setVerticalAlignment(Element.ALIGN_TOP);
            table4.addCell(cell);
            
            
            
            
            
            
            
            //Agregar fila Vacia
            cell = new PdfPCell(new Paragraph("", smallBoldFont7Black));
            cell.setBorder(0);
            cell.setColspan(2);
            table4.addCell(cell);
           
            //Agregar fila Vacia
            cell = new PdfPCell(new Paragraph("", smallBoldFont7Black));
            cell.setBorder(0);
            cell.setColspan(2);
            table4.addCell(cell);
            
            
            
            
            
            //fila 3 -> Columna 1 table4
            if(getRows_horas_extras().size()>0){
                cell = new PdfPCell(new Paragraph("Detalle de Horas Extras", smallBoldFont7White));
                cell.setBorderWidthBottom(0);
                cell.setBorderWidthTop(0);
                cell.setBorderWidthLeft(1);
                cell.setBorderWidthRight(0.5f);
                cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
                cell.setHorizontalAlignment(Element.ALIGN_LEFT);
                cell.setBorderColorRight(BaseColor.WHITE);
                cell.setBorderColorLeft(BaseColor.LIGHT_GRAY);
            }else{
                cell = new PdfPCell(new Paragraph("", smallBoldFont7White));
                cell.setBorder(0);
            }
            cell.setUseAscender(true);
            table4.addCell(cell);
            
            
            
            
            //fila  3 -> Columna 2 table4
            if(getRows_incapacidades().size()>0){
                cell = new PdfPCell(new Paragraph("Detalle de Incapacidades", smallBoldFont7White));
                cell.setBorderWidthBottom(0);
                cell.setBorderWidthTop(0);
                cell.setBorderWidthLeft(0);
                cell.setBorderWidthRight(1);
                cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
                cell.setHorizontalAlignment(Element.ALIGN_LEFT);
                cell.setBorderColorRight(BaseColor.LIGHT_GRAY);
            }else{
                cell = new PdfPCell(new Paragraph("", smallBoldFont7White));
                cell.setBorder(0);
            }
            cell.setUseAscender(true);
            table4.addCell(cell);
            
            
            
            
            
            
            //fila 4 -> Columna 1 table4
            if(getRows_horas_extras().size()>0){
                //Crear tabla de HORAS EXTRAS
                String[] columnas_hrs = {"Días","Horas","Tipo de Hora","Importe Pagado"};
                float [] widths_hrs = {0.3f,0.4f,2f,1.6f};
                PdfPTable table_hrs = new PdfPTable(widths_hrs);
                tabled.setKeepTogether(false);
                
                List<String>  lista_columnas_hrs = (List<String>) Arrays.asList(columnas_hrs);
                PdfPCell cellHrs;
                for ( String columna_titulo2 : lista_columnas_hrs){
                    cellHrs = new PdfPCell(new Paragraph(columna_titulo2,smallBoldFont));
                    cellHrs.setUseAscender(true);
                    cellHrs.setUseDescender(true);
                    //cellHrs.setBackgroundColor(BaseColor.BLACK);
                    if(columna_titulo2.equals("Días")){
                        cellHrs.setHorizontalAlignment(Element.ALIGN_LEFT);
                    }
                    if(columna_titulo2.equals("Horas")){
                        cellHrs.setHorizontalAlignment(Element.ALIGN_LEFT);
                    }
                    if(columna_titulo2.equals("Tipo de Hora")){
                        cellHrs.setHorizontalAlignment(Element.ALIGN_LEFT);
                    }
                    if(columna_titulo2.equals("Importe Pagado")){
                        cellHrs.setHorizontalAlignment(Element.ALIGN_RIGHT);
                    }
                    table_hrs.addCell(cellHrs);
                }
                
                for (LinkedHashMap<String,String> registro : getRows_horas_extras()){
                    //Indices del HashMap que representa el row
                    String[] wordList2 = {"Dias","HorasExtra","TipoHoras","ImportePagado"};
                    List<String>  indices2 = (List<String>) Arrays.asList(wordList2);
                    for (String omega : indices2){
                        PdfPCell celdad;
                        if (omega.equals("Dias")){
                            celdad = new PdfPCell(new Paragraph(registro.get(omega).toString(),smallFont));
                            celdad.setHorizontalAlignment(Element.ALIGN_LEFT);
                            celdad.setVerticalAlignment(Element.ALIGN_TOP);
                            celdad.setBorderWidthBottom(0);
                            celdad.setBorderWidthLeft(0);
                            celdad.setBorderWidthRight(0.5f);
                            celdad.setBorderWidthTop(0);
                            celdad.setBorderColorRight(BaseColor.LIGHT_GRAY);
                            table_hrs.addCell(celdad);
                        }
                        if (omega.equals("HorasExtra")){
                            celdad = new PdfPCell(new Paragraph(registro.get(omega).toString(),smallFont));
                            celdad.setHorizontalAlignment(Element.ALIGN_LEFT);
                            celdad.setVerticalAlignment(Element.ALIGN_TOP);
                            celdad.setBorderWidthBottom(0);
                            celdad.setBorderWidthLeft(0);
                            celdad.setBorderWidthTop(0);
                            celdad.setBorderWidthRight(0.5f);
                            celdad.setBorderColorRight(BaseColor.LIGHT_GRAY);
                            table_hrs.addCell(celdad);
                        }
                        if (omega.equals("TipoHoras")){
                            celdad = new PdfPCell(new Paragraph(registro.get(omega).toString().toUpperCase(),smallFont));
                            celdad.setHorizontalAlignment(Element.ALIGN_LEFT);
                            celdad.setVerticalAlignment(Element.ALIGN_TOP);
                            celdad.setBorderWidthBottom(0);
                            celdad.setBorderWidthLeft(0);
                            celdad.setBorderWidthTop(0);
                            celdad.setBorderWidthRight(0.5f);
                            celdad.setBorderColorRight(BaseColor.LIGHT_GRAY);
                            table_hrs.addCell(celdad);
                        }
                        if (omega.equals("ImportePagado")){
                            celdad= new PdfPCell(new Paragraph(StringHelper.AgregaComas(StringHelper.roundDouble(registro.get(omega).toString(),2)),smallFont));
                            celdad.setHorizontalAlignment(Element.ALIGN_RIGHT);
                            celdad.setVerticalAlignment(Element.ALIGN_TOP);
                            celdad.setBorderWidthRight(0);
                            celdad.setBorderWidthLeft(0);
                            celdad.setBorderWidthBottom(0);
                            celdad.setBorderWidthTop(0);
                            table_hrs.addCell(celdad);
                        }
                    }
                }

                //Agregar tabla de HORAS EXTRAS
                cell = new PdfPCell(table_hrs);
                cell.setUseAscender(true);
                cell.setUseDescender(true);
                cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                cell.setVerticalAlignment(Element.ALIGN_TOP);
                table4.addCell(cell);
            }else{
                cell = new PdfPCell(new Paragraph("", smallBoldFont7White));
                cell.setBorder(0);
                cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                table4.addCell(cell);
            }

            
            
            
            
            //fila  4 -> Columna 2 table4
            if(getRows_incapacidades().size()>0){
                //Crear tabla de INCAPACIDADES
                String[] columnas_incapa = {"Días","Tipo de Incapacidad","Descuento"};
                float [] widths_incapa = {0.3f,2.4f,1.6f};
                PdfPTable table_incapacidades = new PdfPTable(widths_incapa);
                tabled.setKeepTogether(false);
                
                List<String>  lista_columnas_incapa = (List<String>) Arrays.asList(columnas_incapa);
                PdfPCell cellIncapa;
                for ( String columna_titulo2 : lista_columnas_incapa){
                    cellIncapa = new PdfPCell(new Paragraph(columna_titulo2,smallBoldFont));
                    cellIncapa.setUseAscender(true);
                    cellIncapa.setUseDescender(true);
                    //cellIncapa.setBackgroundColor(BaseColor.BLACK);
                    if(columna_titulo2.equals("Días")){
                        cellIncapa.setHorizontalAlignment(Element.ALIGN_LEFT);
                    }
                    if(columna_titulo2.equals("Tipo de Incapacidad")){
                        cellIncapa.setHorizontalAlignment(Element.ALIGN_LEFT);
                    }
                    if(columna_titulo2.equals("Descuento")){
                        cellIncapa.setHorizontalAlignment(Element.ALIGN_RIGHT);
                    }
                    table_incapacidades.addCell(cellIncapa);
                }
                
                
                for (LinkedHashMap<String,String> registro : getRows_incapacidades()){
                    //Indices del HashMap que representa el row
                    String[] wordList2 = {"DiasIncapacidad","titulo_tipo_incapacidad","Descuento"};
                    List<String>  indices2 = (List<String>) Arrays.asList(wordList2);
                    for (String omega : indices2){
                        PdfPCell celdad;
                        if (omega.equals("DiasIncapacidad")){
                            celdad = new PdfPCell(new Paragraph(registro.get(omega).toString(),smallFont));
                            celdad.setHorizontalAlignment(Element.ALIGN_LEFT);
                            celdad.setVerticalAlignment(Element.ALIGN_TOP);
                            celdad.setBorderWidthBottom(0);
                            celdad.setBorderWidthLeft(0);
                            celdad.setBorderWidthRight(0.5f);
                            celdad.setBorderWidthTop(0);
                            celdad.setBorderColorRight(BaseColor.LIGHT_GRAY);
                            table_incapacidades.addCell(celdad);
                        }
                        if (omega.equals("titulo_tipo_incapacidad")){
                            celdad = new PdfPCell(new Paragraph(registro.get(omega).toString().toUpperCase(),smallFont));
                            celdad.setHorizontalAlignment(Element.ALIGN_LEFT);
                            celdad.setVerticalAlignment(Element.ALIGN_TOP);
                            celdad.setBorderWidthBottom(0);
                            celdad.setBorderWidthLeft(0);
                            celdad.setBorderWidthTop(0);
                            celdad.setBorderWidthRight(0.5f);
                            celdad.setBorderColorRight(BaseColor.LIGHT_GRAY);
                            table_incapacidades.addCell(celdad);
                        }
                        if (omega.equals("Descuento")){
                            celdad= new PdfPCell(new Paragraph(StringHelper.AgregaComas(StringHelper.roundDouble(registro.get(omega).toString(),2)),smallFont));
                            celdad.setHorizontalAlignment(Element.ALIGN_RIGHT);
                            celdad.setVerticalAlignment(Element.ALIGN_TOP);
                            celdad.setBorderWidthLeft(0);
                            celdad.setBorderWidthRight(0);
                            celdad.setBorderWidthTop(0);
                            celdad.setBorderWidthBottom(0);
                            table_incapacidades.addCell(celdad);
                        }
                    }
                }
                
                //Agregar tabla de INCAPACIDADES
                cell = new PdfPCell(table_incapacidades);
                cell.setUseAscender(true);
                cell.setUseDescender(true);
                cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                cell.setVerticalAlignment(Element.ALIGN_TOP);
                table4.addCell(cell);
            }else{
                cell = new PdfPCell(new Paragraph("", smallBoldFont7White));
                cell.setBorder(0);
                cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                table4.addCell(cell);
            }
            
            
            
            
            
            
            
            
            
            
            
            
            
            
            
            //INICIA TABLA TOTALES
            float [] widthstot = {5f,1f,0.5f,1f};
            PdfPTable table5 = new PdfPTable(widthstot);
            PdfPCell celltot;
            
            //fila SUBTOTAL
            celltot = new PdfPCell(new Paragraph("", smallBoldFont7Black));
            celltot.setHorizontalAlignment(Element.ALIGN_LEFT);
            celltot.setVerticalAlignment(Element.ALIGN_TOP);
            celltot.setBorderWidthBottom(0);
            celltot.setBorderWidthLeft(0);
            celltot.setBorderWidthRight(0);
            celltot.setBorderWidthTop(0);
            table5.addCell(celltot);
            
            celltot = new PdfPCell(new Paragraph("SUB-TOTAL",smallBoldFont7Black));
            celltot.setHorizontalAlignment(Element.ALIGN_RIGHT);
            celltot.setVerticalAlignment(Element.ALIGN_TOP);
            celltot.setBorderWidthBottom(0);
            celltot.setBorderWidthLeft(0);
            celltot.setBorderWidthRight(0);
            celltot.setBorderWidthTop(0);
            table5.addCell(celltot);
            
            celltot= new PdfPCell(new Paragraph(getSimbolo_moneda(),smallBoldFont7Black));
            celltot.setHorizontalAlignment(Element.ALIGN_RIGHT);
            celltot.setVerticalAlignment(Element.ALIGN_TOP);
            celltot.setBorderWidthBottom(0);
            celltot.setBorderWidthLeft(0);
            celltot.setBorderWidthRight(0);
            celltot.setBorderWidthTop(0);
            table5.addCell(celltot);
            
            celltot= new PdfPCell(new Paragraph(StringHelper.AgregaComas(StringHelper.roundDouble(getSubTotal(),2)),smallBoldFont7Black));
            celltot.setHorizontalAlignment(Element.ALIGN_RIGHT);
            celltot.setVerticalAlignment(Element.ALIGN_TOP);
            celltot.setBorderWidthBottom(0);
            celltot.setBorderWidthLeft(0);
            celltot.setBorderWidthRight(0);
            celltot.setBorderWidthTop(0);
            table5.addCell(celltot);
            
            
            celltot = new PdfPCell(new Paragraph("", smallBoldFont7Black));
            celltot.setHorizontalAlignment(Element.ALIGN_LEFT);
            celltot.setVerticalAlignment(Element.ALIGN_TOP);
            celltot.setBorderWidthBottom(0);
            celltot.setBorderWidthLeft(0);
            celltot.setBorderWidthRight(0);
            celltot.setBorderWidthTop(0);
            table5.addCell(celltot);

            celltot = new PdfPCell(new Paragraph("DESCUENTOS",smallBoldFont7Black));
            celltot.setHorizontalAlignment(Element.ALIGN_RIGHT);
            celltot.setVerticalAlignment(Element.ALIGN_TOP);
            celltot.setBorderWidthBottom(0);
            celltot.setBorderWidthLeft(0);
            celltot.setBorderWidthRight(0);
            celltot.setBorderWidthTop(0);
            table5.addCell(celltot);

            celltot= new PdfPCell(new Paragraph(getSimbolo_moneda(),smallBoldFont7Black));
            celltot.setHorizontalAlignment(Element.ALIGN_RIGHT);
            celltot.setVerticalAlignment(Element.ALIGN_TOP);
            celltot.setBorderWidthBottom(0);
            celltot.setBorderWidthLeft(0);
            celltot.setBorderWidthRight(0);
            celltot.setBorderWidthTop(0);
            table5.addCell(celltot);

            celltot= new PdfPCell(new Paragraph(StringHelper.AgregaComas(StringHelper.roundDouble(getMontoIeps(),2)),smallBoldFont7Black));
            celltot.setHorizontalAlignment(Element.ALIGN_RIGHT);
            celltot.setVerticalAlignment(Element.ALIGN_TOP);
            celltot.setBorderWidthBottom(0);
            celltot.setBorderWidthLeft(0);
            celltot.setBorderWidthRight(0);
            celltot.setBorderWidthTop(0);
            table5.addCell(celltot);

            
            //fila IVA
            celltot = new PdfPCell(new Paragraph("IMPORTE CON LETRA", smallBoldFont7Black));
            celltot.setHorizontalAlignment(Element.ALIGN_LEFT);
            celltot.setVerticalAlignment(Element.ALIGN_TOP);
            celltot.setBorderWidthBottom(0);
            celltot.setBorderWidthLeft(0);
            celltot.setBorderWidthRight(0);
            celltot.setBorderWidthTop(0);
            table5.addCell(celltot);
            
            celltot = new PdfPCell(new Paragraph("ISR",smallBoldFont7Black));
            celltot.setHorizontalAlignment(Element.ALIGN_RIGHT);
            celltot.setVerticalAlignment(Element.ALIGN_TOP);
            celltot.setBorderWidthBottom(0);
            celltot.setBorderWidthLeft(0);
            celltot.setBorderWidthRight(0);
            celltot.setBorderWidthTop(0);
            table5.addCell(celltot);
            
            celltot= new PdfPCell(new Paragraph(getSimbolo_moneda(),smallBoldFont7Black));
            celltot.setHorizontalAlignment(Element.ALIGN_RIGHT);
            celltot.setVerticalAlignment(Element.ALIGN_TOP);
            celltot.setBorderWidthBottom(0);
            celltot.setBorderWidthLeft(0);
            celltot.setBorderWidthRight(0);
            celltot.setBorderWidthTop(0);
            table5.addCell(celltot);
            
            celltot= new PdfPCell(new Paragraph(StringHelper.AgregaComas(StringHelper.roundDouble(getMontoImpuesto(),2)),smallBoldFont7Black));
            celltot.setHorizontalAlignment(Element.ALIGN_RIGHT);
            celltot.setVerticalAlignment(Element.ALIGN_TOP);
            celltot.setBorderWidthBottom(0);
            celltot.setBorderWidthLeft(0);
            celltot.setBorderWidthRight(0);
            celltot.setBorderWidthTop(0);
            table5.addCell(celltot);
            

     
            
            
            BigInteger num = new BigInteger(getMontoTotal().split("\\.")[0]);
            n2t cal = new n2t();
            String centavos = getMontoTotal().substring(getMontoTotal().indexOf(".")+1);
            String numero = cal.convertirLetras(num);
            
            //Convertir a mayuscula la primera letra de la cadena
            String numeroMay = numero.substring(0, 1).toUpperCase() + numero.substring(1, numero.length());
            
            String denom = "";
            String denominacion="";
            denominacion = getTitulo_moneda();
            denom = getMoneda_abr();

            if(centavos.equals(num.toString())){
                centavos="00";
            }
            
            //FILA TOTAL
            celltot = new PdfPCell(new Paragraph(numeroMay.toUpperCase().toString() + " " + denominacion.toUpperCase().toString() + " " +centavos+"/100 "+ denom.toUpperCase().toString(), smallFont));
            //celltot = new PdfPCell(new Paragraph("letra",smallBoldFont6));
            celltot.setHorizontalAlignment(Element.ALIGN_LEFT);
            celltot.setVerticalAlignment(Element.ALIGN_TOP);
            //celda.setBorder(0);
            celltot.setBorderWidthBottom(0);
            celltot.setBorderWidthLeft(0);
            celltot.setBorderWidthRight(0);
            celltot.setBorderWidthTop(0);
            table5.addCell(celltot);
            
            celltot = new PdfPCell(new Paragraph("NETO A PAGAR",smallBoldFont7Black));
            celltot.setHorizontalAlignment(Element.ALIGN_RIGHT);
            celltot.setVerticalAlignment(Element.ALIGN_TOP);
            celltot.setBorderWidthBottom(0);
            celltot.setBorderWidthLeft(0);
            celltot.setBorderWidthRight(0);
            celltot.setBorderWidthTop(0);
            table5.addCell(celltot);
            
            celltot= new PdfPCell(new Paragraph(getSimbolo_moneda(),smallBoldFont7Black));
            celltot.setHorizontalAlignment(Element.ALIGN_RIGHT);
            celltot.setVerticalAlignment(Element.ALIGN_TOP);
            celltot.setBorderWidthBottom(0);
            celltot.setBorderWidthLeft(0);
            celltot.setBorderWidthRight(0);
            celltot.setBorderWidthTop(0);
            table5.addCell(celltot);
            
            celltot= new PdfPCell(new Paragraph(StringHelper.AgregaComas(StringHelper.roundDouble(getMontoTotal(),2)),smallBoldFont7Black));
            celltot.setHorizontalAlignment(Element.ALIGN_RIGHT);
            celltot.setVerticalAlignment(Element.ALIGN_TOP);
            celltot.setBorderWidthBottom(0);
            celltot.setBorderWidthLeft(0);
            celltot.setBorderWidthRight(0);
            celltot.setBorderWidthTop(0);
            table5.addCell(celltot);
            
            
            //agregar tabla totales
            cell = new PdfPCell(table5);
            cell.setUseAscender(true);
            cell.setUseDescender(true);
            cell.setColspan(2);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setVerticalAlignment(Element.ALIGN_TOP);
            cell.setBorderWidthBottom(0);
            cell.setBorderWidthLeft(0);
            cell.setBorderWidthRight(0);
            cell.setBorderWidthTop(0);
            table4.addCell(cell);
            
            
            
  
      
      
           
     
            return table4;
        }
    }//termina tabla conceptos
    
    
    
    
    
    
    
    
    
    
    
    
    //esta es la tabla para los datos del CLIENTE
    private class celdaDatosFiscales {
        public PdfPTable addContent() throws Exception {
            Font smallFontBold5 = new Font(Font.FontFamily.HELVETICA,5,Font.BOLD,BaseColor.BLACK);
            Font smallFont7 = new Font(Font.FontFamily.HELVETICA,7,Font.NORMAL,BaseColor.BLACK);
            Font smallBoldFont7= new Font(Font.FontFamily.HELVETICA,7,Font.BOLD,BaseColor.BLACK);
            Font smallBoldFont7White= new Font(Font.FontFamily.HELVETICA,7,Font.BOLD,BaseColor.WHITE);
            
            //tabla contenedor
            PdfPCell cell;
            
            float [] widths = {2.5f,10f};
            PdfPTable table = new PdfPTable(widths);
            table.setKeepTogether(false);
            
            
            
            cell = new PdfPCell(new Paragraph("INFORMACIÓN DEL TIMBRE FISCAL DIGITAL", smallBoldFont7White));
            cell.setBorder(0);
            cell.setUseAscender(true);
            cell.setHorizontalAlignment(Element.ALIGN_LEFT);
            cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
            cell.setColspan(2);
            table.addCell(cell);

            //fila vacia
            cell = new PdfPCell(new Paragraph("", smallFontBold5));
            cell.setBorder(0);
            cell.setUseAscender(true);
            cell.setBorderWidthBottom(0);
            cell.setColspan(2);
            table.addCell(cell);


            PdfCfdiNomina.ImagenCBB icbb = new PdfCfdiNomina.ImagenCBB();

            if(!getRutaImagenCBB().trim().equals("")){
                try {
                    String FORMATO_IMAGEN="png";
                    String RUTA_IMAGEN = getRutaImagenCBB();
                    int ancho=500;
                    int alto=500;
                    String cadenaDatos = getCadenaCBB();

                    CodigoQRHelper codeQR = new CodigoQRHelper();

                    String rutaImgCodQR = new String();

                    rutaImgCodQR = codeQR.CodeQR(FORMATO_IMAGEN, RUTA_IMAGEN, ancho, alto, cadenaDatos);

                    //celda imagen cbb
                    cell = new PdfPCell(icbb.addContent(rutaImgCodQR));
                    cell.setUseAscender(true);
                    cell.setBorderWidthRight(0);
                    cell.setBorderWidthLeft(0);
                    cell.setBorderWidthTop(0);
                    cell.setBorderWidthBottom(0);
                    cell.setHorizontalAlignment(Element.ALIGN_LEFT);
                    table.addCell(cell);

                    FileHelper.delete(RUTA_IMAGEN);

                } catch (WriterException ex) {
                    Logger.getLogger(PdfCfdiNomina.class.getName()).log(Level.SEVERE, null, ex);
                } catch (FileNotFoundException ex) {
                    Logger.getLogger(PdfCfdiNomina.class.getName()).log(Level.SEVERE, null, ex);
                } catch (IOException ex) {
                    Logger.getLogger(PdfCfdiNomina.class.getName()).log(Level.SEVERE, null, ex);
                }
            }else{
                //no hay imagen
                cell = new PdfPCell(new Paragraph("", smallFontBold5));
                cell.setUseAscender(true);
                cell.setBorderWidthRight(0);
                cell.setBorderWidthTop(0);
                cell.setBorderWidthBottom(0);
                cell.setHorizontalAlignment(Element.ALIGN_LEFT);
                table.addCell(cell);
            }




            float [] widths2 = {1.7f,1.8f,2.2f,1.5f};
            PdfPTable table2 = new PdfPTable(widths2);
            table2.setKeepTogether(false);

            cell = new PdfPCell(new Paragraph("NO. CERTIFICADO DEL SAT:", smallBoldFont7));
            cell.setBorder(0);
            cell.setHorizontalAlignment(Element.ALIGN_LEFT);
            table2.addCell(cell);

            cell = new PdfPCell(new Paragraph(getNoCertificadoSAT(), smallFont7));
            cell.setBorder(0);
            cell.setHorizontalAlignment(Element.ALIGN_LEFT);
            table2.addCell(cell);

            cell = new PdfPCell(new Paragraph("FECHA Y HORA DE CERTIFICACIÓN:", smallBoldFont7));
            cell.setBorder(0);
            cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            table2.addCell(cell);

            cell = new PdfPCell(new Paragraph(getFachaTimbrado(), smallFont7));
            cell.setBorder(0);
            cell.setHorizontalAlignment(Element.ALIGN_LEFT);
            table2.addCell(cell);


            //fila vacia
            cell = new PdfPCell(new Paragraph("", smallFont7));
            cell.setBorder(0);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setColspan(4);
            table2.addCell(cell);

            cell = new PdfPCell(new Paragraph("CADENA ORIGINAL DEL TIMBRE:", smallBoldFont7));
            cell.setHorizontalAlignment(Element.ALIGN_LEFT);
            cell.setBorder(0);
            cell.setColspan(4);
            table2.addCell(cell);

            cell = new PdfPCell(new Paragraph(  getCadena_original()+"\n"  ,smallFont7));
            cell.setBorder(0);
            cell.setHorizontalAlignment(Element.ALIGN_LEFT);
            cell.setColspan(4);
            table2.addCell(cell);


            cell = new PdfPCell(new Paragraph("SELLO DIGITAL DEL CFDI:", smallBoldFont7));
            cell.setHorizontalAlignment(Element.ALIGN_LEFT);
            cell.setBorder(0);
            cell.setColspan(4);
            table2.addCell(cell);

            cell = new PdfPCell(new Paragraph(getSello_digital()+"\n"  ,smallFont7));
            cell.setHorizontalAlignment(Element.ALIGN_LEFT);
            cell.setBorder(0);
            cell.setColspan(4);
            table2.addCell(cell);

            cell = new PdfPCell(new Paragraph("SELLO DIGITAL DEL SAT:", smallBoldFont7));
            cell.setHorizontalAlignment(Element.ALIGN_LEFT);
            cell.setBorder(0);
            cell.setColspan(4);
            table2.addCell(cell);

            cell = new PdfPCell(new Paragraph(getSello_digital_sat()+"\n"  ,smallFont7));
            cell.setHorizontalAlignment(Element.ALIGN_LEFT);
            cell.setBorder(0);
            cell.setColspan(4);
            table2.addCell(cell);

            
            cell = new PdfPCell(table2);
            cell.setBorderWidthRight(0);
            cell.setBorderWidthLeft(0);
            cell.setBorderWidthBottom(0);
            cell.setBorderWidthTop(0);
            cell.setUseAscender(true);
            cell.setHorizontalAlignment(Element.ALIGN_LEFT);
            table.addCell(cell);
            
            return table;
        }
    }//termina celda datos fiscales
    
    
    
    


    
    private class ImagenPDF {
        public Image addContent() {
            Image img = null;
            try {
                img = Image.getInstance(getImagen());
                img.scaleAbsoluteHeight(60);
                img.scaleAbsoluteWidth(120);
                //img.setAlignment(0);
                img.setAlignment(Element.ALIGN_CENTER);
            }
            catch(Exception e){
                System.out.println(e);
            }
            return img;
        }
    }
    
    
    
    
    private class ImagenCBB {
        public Image addContent(String rutaImgCodQR) {
            Image img = null;
            try {
                img = Image.getInstance(rutaImgCodQR);
                img.scaleAbsoluteHeight(110);
                img.scaleAbsoluteWidth(110);
                //img.setAlignment(0);
                img.setAlignment(Element.ALIGN_CENTER);
            }
            catch(Exception e){
                System.out.println(e);
            }
            return img;
        }
    }
    
    
    
    
    ///%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%55
    public String esteAtributoSeDejoNulo(String atributo){
         return (atributo != null) ? (atributo) : new String();
    }
    
    

    public String getFileout() {
        return fileout;
    }

    public void setFileout(String fileout) {
        this.fileout = fileout;
    }
    
    public void setImagen(String imagen) {
    	this.imagen = imagen;
    }
    
    public String getImagen() {
    	return imagen;
    }
     
    
    public String getCadena_original() {
        return cadena_original;
    }

    public void setCadena_original(String cadena_original) {
        this.cadena_original = cadena_original;
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

    public HashMap<String, String> getEncabezado() {
        return encabezado;
    }

    public void setEncabezado(HashMap<String, String> encabezado) {
        this.encabezado = encabezado;
    }
    
    public ArrayList<LinkedHashMap<String,String>> getRows() {
        return rows;
    }
    
    public void setRows(ArrayList<LinkedHashMap<String,String>> rows) {
        this.rows = rows;
    }
    
        public ArrayList<LinkedHashMap<String,String>> getRowsd() {
        return rowsd;
    }
    
    public void setRowsd(ArrayList<LinkedHashMap<String,String>> rowsd) {
        this.rowsd = rowsd;
    }
    
    
    public HashMap<String, String> getDatosCliente() {
        return datos_nomina;
    }
    
    public void setDatosCliente(HashMap<String, String> datos_nomina) {
        this.datos_nomina = datos_nomina;
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
    
    public String getMontoImpuesto() {
        return montoImpuesto;
    }

    public void setMontoImpuesto(String montoImpuesto) {
        this.montoImpuesto = montoImpuesto;
    }
    
    public String getMontoIeps() {
        return montoIeps;
    }

    public void setMontoIeps(String montoIeps) {
        this.montoIeps = montoIeps;
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
    
    public String getFormaPago() {
        return formaPago;
    }

    public void setFormaPago(String formaPago) {
        this.formaPago = formaPago;
    }
    
    public String getMonedaIso() {
        return monedaIso;
    }

    public void setMonedaIso(String monedaIso) {
        this.monedaIso = monedaIso;
    }

    public String getTipoCambio() {
        return tipoCambio;
    }

    public void setTipoCambio(String tipoCambio) {
        this.tipoCambio = tipoCambio;
    }
    
    public String getFachaTimbrado() {
        return fachaTimbrado;
    }

    public void setFachaTimbrado(String fachaTimbrado) {
        this.fachaTimbrado = fachaTimbrado;
    }

    public String getNoCertificadoSAT() {
        return noCertificadoSAT;
    }

    public void setNoCertificadoSAT(String noCertificadoSAT) {
        this.noCertificadoSAT = noCertificadoSAT;
    }
    
    public String getRutaImagenCBB() {
        return rutaImagenCBB;
    }

    public void setRutaImagenCBB(String rutaImagenCBB) {
        this.rutaImagenCBB = rutaImagenCBB;
    }
    
    
    public String getCadenaCBB() {
        return cadenaCBB;
    }

    public void setCadenaCBB(String cadenaCBB) {
        this.cadenaCBB = cadenaCBB;
    }
    //cliente
     public String getReceptor_regimen() {
        return regimen;
    }

    public void setReceptor_regimen(String regimen) {
        this.regimen = regimen;
    }
    
    public String getRegimen_contratacion_titulo() {
        return regimen_contratacion_titulo;
    }

    public void setRegimen_contratacion_titulo(String regimen_contratacion_titulo) {
        this.regimen_contratacion_titulo = regimen_contratacion_titulo;
    }
    
    //depto
    public String getReceptor_depto() {
        return depto;
    }

    public void setReceptor_depto(String depto) {
        this.depto = depto;
    }
    //
    public String getReceptor_puesto() {
        return puesto;
    }

    public void setReceptor_puesto(String puesto) {
        this.puesto = puesto;
    }
    //
      public String getReceptor_riesgo_puesto() {
        return riesgo_puesto;
    }

    public void setReceptor_riesgo_puesto(String riesgo_puesto) {
        this.riesgo_puesto = riesgo_puesto;
    }
    
    public String getRiesgo_puesto_titulo() {
        return riesgo_puesto_titulo;
    }

    public void setRiesgo_puesto_titulo(String riesgo_puesto_titulo) {
        this.riesgo_puesto_titulo = riesgo_puesto_titulo;
    }
    
    //
    public String getReceptor_tipo_contrato() {
        return tipo_contrato;
    }

    public void setReceptor_tipo_contrato(String tipo_contrato) {
        this.tipo_contrato = tipo_contrato;
    }
    
    //
    public String getReceptor_tipo_jornada() {
        return tipo_jornada;
    }

    public void setReceptor_tipo_jornada(String tipo_jornada) {
        this.tipo_jornada = tipo_jornada;
    }
    //
      public String getReceptor_fecha_antiguedad() {
        return fecha_antiguedad;
    }

    public void setReceptor_fecha_antiguedad(String fecha_antiguedad) {
        this.fecha_antiguedad = fecha_antiguedad;
    }
    //
      public String geteReceptor_fecha_contrato() {
        return fecha_contrato;
    }

    public void setReceptor_fecha_contrato(String fecha_contrato) {
        this.fecha_contrato = fecha_contrato;
    }
    //
      public String getReceptor_periodicidad_pago() {
        return periodicidad_pago;
    }

    public void setReceptor_periodicidad_pago(String periodicidad_pago) {
        this.periodicidad_pago = periodicidad_pago;
    }
    //
      public String getReceptor_salario_base() {
        return salario_base;
    }

    public void setReceptor_salario_base(String salario_base) {
        this.salario_base = salario_base;
    }
    //
      public String getReceptor_sdi() {
        return sdi;
    }

    public void setReceptor_sdi(String sdi) {
        this.sdi = sdi;
    }

    //
    public String getReceptor_fechapago() {
        return fechapago;
    }

    public void setReceptor_fechapago(String fechapago) {
        this.fechapago = fechapago;
    }
    //
    public String getReceptor_fechain() {
        return fechain;
    }

    public void setReceptor_fechain(String fechain) {
        this.fechain = fechain;
    }
        
   //
      public String getReceptor_fechafin() {
        return fechafin;
    }

    public void setReceptor_fechafin(String fechafin) {
        this.fechafin = fechafin;
    }
    //
      public String getReceptor_ndias() {
        return ndias;
    }

    public void setReceptor_ndias(String ndias) {
        this.ndias = ndias;
    }
       //
      public String getReceptor_banco() {
        return banco;
    }

    public void setReceptor_banco(String banco) {
        this.banco = banco;
    }
    //
      public String getReceptor_clabe() {
        return clabe;
    }

    public void setReceptor_clabe(String clabe) {
        this.clabe = clabe;
    }
    //
      public String getReceptor_regpatronal() {
        return regpatronal;
    }

    public void setReceptor_regpatronal(String regpatronal) {
        this.regpatronal = regpatronal;
    }
    
    //
      public String getCondicionesPago() {
        return condiciones_pago;
    }

    public void setCondicionesPago(String condiciones_pago) {
        this.condiciones_pago = condiciones_pago;
    }
    
    public String getReceptor_curp() {
        return curp;
    }

    public void setReceptor_curp(String curp) {
        this.curp = curp;
    }

    public String getLeyenda() {
        return leyenda;
    }

    public void setLeyenda(String leyenda) {
        this.leyenda = leyenda;
    }
    
    public ArrayList<LinkedHashMap<String, String>> getRows_horas_extras() {
        return rows_horas_extras;
    }

    public void setRows_horas_extras(ArrayList<LinkedHashMap<String, String>> rows_horas_extras) {
        this.rows_horas_extras = rows_horas_extras;
    }

    public ArrayList<LinkedHashMap<String, String>> getRows_incapacidades() {
        return rows_incapacidades;
    }

    public void setRows_incapacidades(ArrayList<LinkedHashMap<String, String>> rows_incapacidades) {
        this.rows_incapacidades = rows_incapacidades;
    }
    
    public String getDeduc_total_excento() {
        return deduc_total_excento;
    }

    public void setDeduc_total_excento(String deduc_total_excento) {
        this.deduc_total_excento = deduc_total_excento;
    }

    public String getDeduc_total_gravado() {
        return deduc_total_gravado;
    }

    public void setDeduc_total_gravado(String deduc_total_gravado) {
        this.deduc_total_gravado = deduc_total_gravado;
    }

    public String getPercep_total_excento() {
        return percep_total_excento;
    }

    public void setPercep_total_excento(String percep_total_excento) {
        this.percep_total_excento = percep_total_excento;
    }

    public String getPercep_total_gravado() {
        return percep_total_gravado;
    }

    public void setPercep_total_gravado(String percep_total_gravado) {
        this.percep_total_gravado = percep_total_gravado;
    }
    
    public String getNss() {
        return nss;
    }

    public void setNss(String nss) {
        this.nss = nss;
    }
    
    public String getNombre_banco() {
        return nombre_banco;
    }

    public void setNombre_banco(String nombre_banco) {
        this.nombre_banco = nombre_banco;
    }
    

    public String getComprobante_cancelado() {
        return comprobante_cancelado;
    }

    public void setComprobante_cancelado(String comprobante_cancelado) {
        this.comprobante_cancelado = comprobante_cancelado;
    }
    
    static class HeaderFooter extends PdfPageEventHelper {
        protected PdfTemplate total;
        protected BaseFont helv;
        protected PdfContentByte cb;
        protected PdfContentByte cb2;
        Font largeBoldFont = new Font(Font.FontFamily.HELVETICA,10,Font.BOLD,BaseColor.BLACK);
        Font largeFont = new Font(Font.FontFamily.HELVETICA,10,Font.NORMAL,BaseColor.BLACK);
        Font smallFont = new Font(Font.FontFamily.HELVETICA,7,Font.NORMAL,BaseColor.BLACK);
        
        //ESTAS SON VARIABLES PRIVADAS DE LA CLASE, SE LE ASIGNA VALOR EN EL CONSTRUCTOR SON SETER
        private String cadena;
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
        

        public String getCadena() {
            return cadena;
        }

        public void setCadena(String cadena) {
            this.cadena = cadena;
        }
        
        //ESTE ES EL CONSTRUCTOR DE LA CLASE  QUE RECIBE LOS PARAMETROS
        HeaderFooter( HashMap<String, String> datos ){
            this.setCadena(datos.get("cadena"));
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
            //ColumnText.showTextAligned(writer.getDirectContent(),Element.ALIGN_CENTER, new Phrase(this.getPeriodo(),largeFont),document.getPageSize().getWidth()/2, document.getPageSize().getTop()-200, 0);
            
            cb = writer.getDirectContent();
            cb2 = writer.getDirectContent();
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
            String text_center1 = this.getCadena();
            float text_center_Size1 = helv.getWidthPoint(text_center1, 7);
            float pos_text_center1 = (document.getPageSize().getWidth()/2)-(text_center_Size1/2);
            cb2.beginText();  
            cb2.setFontAndSize(helv, 7);  
            cb2.setTextMatrix(pos_text_center1, (textBase+10) );  //definir la posicion de text
            cb2.showText(text_center1);
            cb2.endText();
            
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
