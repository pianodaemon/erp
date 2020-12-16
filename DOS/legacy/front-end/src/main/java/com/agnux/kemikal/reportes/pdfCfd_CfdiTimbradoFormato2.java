/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.agnux.kemikal.reportes;
import com.agnux.common.helpers.CodigoQRHelper;
import com.agnux.common.helpers.FileHelper;
import com.agnux.common.helpers.StringHelper;
import com.agnux.common.helpers.n2t;
import com.agnux.kemikal.interfacedaos.GralInterfaceDao;
import com.google.zxing.WriterException;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;


public class pdfCfd_CfdiTimbradoFormato2 {
   //--variables para pdf--
    private  GralInterfaceDao gralDao;
    public static enum Proposito {NOTA_CARGO};
    private String fileout;
    private String refId;
    
    private String tipo_facturacion;
    private String imagen;
    private String rutaImagenCBB;
    private String cadenaCBB;
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
    private String fachaTimbrado;
    private String noCertificadoSAT;
    
    private ArrayList<HashMap<String, String>> rows;
    private ArrayList<String> leyendas;
    private HashMap<String, String> encabezado;
    private HashMap<String, String> datosCliente;
    private HashMap<String, String> datosExtras;
    private String vendedor;
    private String ordenCompra;
    private String folioPedido;
    private String terminos;
    private int dias;
    private String formaPago;
    private String observaciones;
    private String fecha_pago;
    private String metodo_pago;
    private String no_cuenta;
    private String motivoDescuento;
    private String subTotalConDescuento;
    private String subTotalSinDescuento;
    private String subTotal;
    private String montoDescuento;
    private String montoImpuesto;
    private String montoIeps;
    private String montoRetencion;
    private String montoTotal;
    private String titulo_moneda;
    private String moneda_abr;
    private String simbolo_moneda;
    private String tipoCambio;
    private String monedaIso;
    
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
    
    
    public pdfCfd_CfdiTimbradoFormato2(GralInterfaceDao gDao,HashMap<String, String> datosCliente, ArrayList<HashMap<String, String>> listaConceptos, ArrayList<String> leyendasEspeciales, HashMap<String, String> extras, Integer id_empresa, Integer id_sucursal) {
        this.setRows(listaConceptos);
        this.setDatosCliente(datosCliente);
        this.setDatosExtras(extras);
        this.setLeyendas(leyendasEspeciales);
        
        this.setRefId(extras.get("refId"));
        
        this.setGralDao(gDao);
        this.setTipo_facturacion(extras.get("tipo_facturacion"));
        this.setSello_digital_sat(extras.get("sello_sat"));
        this.setUuid(extras.get("uuid"));
        
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
        this.setMetodo_pago(datosCliente.get("comprobante_attr_metodopagotitulo"));
        this.setNo_cuenta(datosCliente.get("comprobante_attr_numerocuenta"));
        this.setFormaPago("PAGO EN UNA SOLA EXIBICION");
        this.setFachaTimbrado(extras.get("fechaTimbre"));
        this.setNoCertificadoSAT(extras.get("noCertificadoSAT"));
        
        switch (Proposito.valueOf(this.getProposito())) {
            case NOTA_CARGO:
                this.setNo_aprobacion(this.getGralDao().getNoAprobacionNotaCargo(id_empresa, id_sucursal));
                this.setAno_aprobacion(this.getGralDao().getAnoAprobacionNotaCargo(id_empresa, id_sucursal));
                break;
        }
        
        this.setMontoDescuento(extras.get("monto_descto"));
        this.setMotivoDescuento(extras.get("motivo_descto"));
        
        if(Double.parseDouble(this.getMontoDescuento())>0){
            this.setSubTotalConDescuento(extras.get("subtotal"));
            this.setSubTotalSinDescuento(extras.get("subtotal_sin_descto"));
        }else{
            this.setSubTotalConDescuento("0.00");
            this.setSubTotalSinDescuento("0.00");
        }
        
        this.setSubTotal(extras.get("subtotal"));
        this.setMontoIeps(extras.get("monto_ieps"));
        this.setMontoImpuesto(extras.get("impuesto"));
        this.setMontoRetencion(extras.get("monto_retencion"));
        this.setMontoTotal(extras.get("total"));
        this.setMoneda_abr(extras.get("moneda_abr"));
        this.setTitulo_moneda(extras.get("nombre_moneda"));
        this.setSimbolo_moneda(datosCliente.get("comprobante_attr_simbolo_moneda"));
        

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
        this.setMonedaIso(datosCliente.get("comprobante_attr_moneda"));
        this.setTipoCambio(datosCliente.get("comprobante_attr_tc"));
        
        this.setImagen( this.getGralDao().getImagesDir()+this.getEmisora_rfc()+"_logo.png" );
        this.setImagen_cedula( this.getGralDao().getImagesDir()+this.getEmisora_rfc()+"_cedula.png" );
        String tipo = "";
        if(this.getTipo_facturacion().equals("cfd")){
            this.setFileout(this.getGralDao().getCfdEmitidosDir() + this.getEmisora_rfc() + "/" + this.getRefId() +".pdf");
            tipo="ESTE DOCUMENTO ES UNA REPRESENTACIÓN IMPRESA DE UN CFD";
        }
        
        if(this.getTipo_facturacion().equals("cfditf")){
            this.setFileout(this.getGralDao().getCfdiTimbreEmitidosDir() + this.getEmisora_rfc() + "/" + this.getRefId() +".pdf");
            tipo="ESTE DOCUMENTO ES UNA REPRESENTACIÓN IMPRESA DE UN CFDI";
            
            //cadena para el CBB, solo es para cfdi con timbrado Fiscal
            String cadenaCBB = "?re="+this.getEmisora_rfc()+"&rr="+this.getReceptor_rfc()+"&tt="+StringHelper.roundDouble(this.getMontoTotal(), 6)+"&id="+this.getUuid();
            this.setCadenaCBB(cadenaCBB);
            this.setRutaImagenCBB( this.getGralDao().getTmpDir()+this.getReceptor_rfc()+".png");
        }
        
        HashMap<String, String> datos = new HashMap<String, String>();
        //datos para pie de pagina
        datos.put("cadena", tipo);
        datos.put("codigo1", "");
        datos.put("codigo2", "");
        
        this.setEncabezado(datos);
    }
    
    
    public void ViewPDF() throws URISyntaxException, FileNotFoundException, Exception {
        Font smallsmall = new Font(Font.FontFamily.HELVETICA,5,Font.NORMAL,BaseColor.BLACK);
        Font smallFont6 = new Font(Font.FontFamily.HELVETICA,6,Font.NORMAL,BaseColor.BLACK);
        Font smallFont = new Font(Font.FontFamily.HELVETICA,7,Font.NORMAL,BaseColor.BLACK);
        Font smallBoldFont7= new Font(Font.FontFamily.HELVETICA,7,Font.BOLD,BaseColor.BLACK);
        Font smallBoldFont = new Font(Font.FontFamily.HELVETICA,8,Font.BOLD,BaseColor.BLACK);
        Font largeBoldFont = new Font(Font.FontFamily.HELVETICA,10,Font.BOLD,BaseColor.BLACK);
        PdfPTable tableHeader;
        PdfPCell cellHeader;
        PdfPCell cell;
        CeldaCustomer tableCustomer = new CeldaCustomer();
        tablaConceptos tablaCon = new tablaConceptos();
        celdaDatosFiscales tablaSellos = new celdaDatosFiscales();
        PdfPTable table_observ;
        PdfPTable table_leyendas;
        
        ImagenPDF ipdf = new ImagenPDF();
        CeldaPDF cepdf = new CeldaPDF();
        
        try {
            
            HeaderFooter event = new HeaderFooter(this.getEncabezado());
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
            
            //RAZON SOCIAL --> BeanFromCFD (X_emisor)
            cellEmp = new PdfPCell(new Paragraph(StringHelper.capitalizaString(this.getEmpresa_emisora().toUpperCase()),largeBoldFont));
            cellEmp.setBorder(0);
            cellEmp.setUseAscender(true);
            cellEmp.setHorizontalAlignment(Element.ALIGN_CENTER);
            tableDatosEmpresa.addCell(cellEmp);
            
            //celda vacia
            cellEmp = new PdfPCell(new Paragraph("R.F.C: "+this.getEmisora_rfc().toUpperCase(), smallBoldFont7));
            cellEmp.setBorder(0);
            //cellEmp.setFixedHeight(5);
            cellEmp.setHorizontalAlignment(Element.ALIGN_CENTER);
            tableDatosEmpresa.addCell(cellEmp);
            
            //DOMICILIO FISCAL --> texto
            cellEmp = new PdfPCell(new Paragraph("DOMICILIO FISCAL", smallBoldFont7));
            cellEmp.setBorder(0);
            cellEmp.setUseAscender(true);
            cellEmp.setHorizontalAlignment(Element.ALIGN_CENTER);
            tableDatosEmpresa.addCell(cellEmp);
            
            String dirEmisor = this.getEmisora_calle()+ " " + this.getEmisora_numero()+ " " + this.getEmisora_colonia()+ "\n" + this.getEmisora_municipio()+ ", " + this.getEmisora_estado()+ ",  "+ " C.P. " + this.getEmisora_cp()+ "\n"  +"    Tel./Fax. " + this.getEmisora_telefono();
            
            cellEmp = new PdfPCell(new Paragraph(dirEmisor.toUpperCase()+"\n"+this.getEmisora_pagina_web(), smallFont));
            cellEmp.setBorder(0);
            cellEmp.setUseAscender(true);
            cellEmp.setHorizontalAlignment(Element.ALIGN_CENTER);
            tableDatosEmpresa.addCell(cellEmp);
            
            cellEmp = new PdfPCell(new Paragraph(this.getEmisora_regimen_fiacal().toUpperCase(), smallFont));
            cellEmp.setBorder(0);
            cellEmp.setUseAscender(true);
            cellEmp.setHorizontalAlignment(Element.ALIGN_CENTER);
            tableDatosEmpresa.addCell(cellEmp);
            
            //celda vacia
            cellEmp = new PdfPCell(new Paragraph(" ", smallFont));
            cellEmp.setBorder(0);
            cellEmp.setFixedHeight(5);
            tableDatosEmpresa.addCell(cellEmp);
            
            cellEmp = new PdfPCell(new Paragraph("LUGAR DE EXPEDICIÓN", smallBoldFont7));
            cellEmp.setBorder(0);
            cellEmp.setUseAscender(true);
            cellEmp.setHorizontalAlignment(Element.ALIGN_CENTER);
            tableDatosEmpresa.addCell(cellEmp);
            
            cellEmp = new PdfPCell(new Paragraph(this.getLugar_expedidion().toUpperCase(), smallFont));
            cellEmp.setBorder(0);
            cellEmp.setUseAscender(true);
            cellEmp.setHorizontalAlignment(Element.ALIGN_CENTER);
            tableDatosEmpresa.addCell(cellEmp);
            
            //AQUI TERMINA LA TABLA PARA DATOS DE LA EMPRESA--------------------
            //------------------------------------------------------------------
            
            //aqui se agrega la tableDatosEmpresa a la tablaPrincipal
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
            //AQUI AGREGAMOS LA TABLA DE DATOS DEL CLIENTE----------------------
            document.add(tableCustomer.addContent());
            //------------------------------------------------------------------
            
            //TABLA VACIA-------------------
            PdfPTable tableVacia = new PdfPTable(1);
            cellEmp = new PdfPCell(new Paragraph("",largeBoldFont));
            cellEmp.setBorder(0);
            cellEmp.setFixedHeight(15);
            tableVacia.addCell(cellEmp);
            
            document.add(tableVacia);
            
            //------------------------------------------------------------------
            //AQUI AGREGAMOS LA TABLA DE CONCEPTOS------------------------------
            document.add(tablaCon.addContent());
            //------------------------------------------------------------------
            
            //agregar tabla vacia
            document.add(tableVacia);
            
            
            //agregamos la tabla de observaciones
            //aqui comienza la tabla de para las OBSERVACIONES
            float [] widths_table_observ = {1};
            table_observ = new PdfPTable(widths_table_observ);
            table_observ.setKeepTogether(false);
            
            if(!this.getObservaciones().trim().equals("")){
                cell = new PdfPCell(new Paragraph("OBSERVACIONES:",smallBoldFont7));
                cell.setBorder(0);
                cell.setVerticalAlignment(Element.ALIGN_TOP);
                cell.setHorizontalAlignment(Element.ALIGN_LEFT);
                cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
                table_observ.addCell(cell);
                
                cell = new PdfPCell(new Paragraph(this.getObservaciones(),smallFont));
                cell.setUseAscender(true);
                cell.setUseDescender(true);
                cell.setVerticalAlignment(Element.ALIGN_TOP);
                cell.setHorizontalAlignment(Element.ALIGN_LEFT);
                table_observ.addCell(cell);
            }
            document.add(table_observ);
            
            
            //Agregar tabla vacia
            document.add(tableVacia);
            
            
            //------------------------------------------------------------------
            //AQUI AGREGAMOS LA TABLA DE SELLOS---------------------------------
            document.add(tablaSellos.addContent());
            //------------------------------------------------------------------
            /*
            //Agregar Leyendas solo cuando es Factura
            if (this.getProposito().equals("FACTURA")){
                int noElements=this.getLeyendas().size();
                
                //Agregar solo cuando existan leyendas
                if(noElements>0){
                    //Agregar tabla vacia
                    document.add(tableVacia);
                    
                    //Aqui comienza la tabla de para las Leyendas Especiales
                    float [] widths_table_leyendas = {1};
                    table_leyendas = new PdfPTable(widths_table_leyendas);
                    table_leyendas.setKeepTogether(false);
                    int counter=0;
                    
                    for (String i : this.getLeyendas()){
                        cell = new PdfPCell(new Paragraph(i,smallFont));
                        
                        if(counter==0){
                            if(noElements==1){
                                //Cuando solo es un elmento hay que pintar todos los bordes
                                cell.setBorderWidthBottom(0.5f);
                                cell.setBorderWidthLeft(0.5f);
                                cell.setBorderWidthRight(0.5f);
                                cell.setBorderWidthTop(0.5f);                                
                            }else{
                                //Si es mas de un elemento solo hay que pintar borde Superior, Izquierdo y Derecho
                                cell.setBorderWidthBottom(0);
                                cell.setBorderWidthLeft(0.5f);
                                cell.setBorderWidthRight(0.5f);
                                cell.setBorderWidthTop(0.5f);   
                            }
                        }else{
                            if(counter<(noElements-1)){
                                //Aqui entra cuando no es el primero ni el ultimo elemento. Solo se debe pintar el borde Izquierdo y Derecho
                                cell.setBorderWidthBottom(0);
                                cell.setBorderWidthLeft(0.5f);
                                cell.setBorderWidthRight(0.5f);
                                cell.setBorderWidthTop(0);   
                            }else{
                                //Aqui entra cuando es el ultimo elemento. Hay que pintar borde Inferior, Izquierda y Derecha
                                cell.setBorderWidthBottom(0.5f);
                                cell.setBorderWidthLeft(0.5f);
                                cell.setBorderWidthRight(0.5f);
                                cell.setBorderWidthTop(0);
                            }
                        }
                        
                        cell.setVerticalAlignment(Element.ALIGN_TOP);
                        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                        table_leyendas.addCell(cell);
                        counter++;
                    }
                    //Agregar tabla Leyendas Especiales al DOCUMENTO
                    document.add(table_leyendas);
                }
            } */

            
            
            //CERRAR EL DOCUMENTO
            document.close();
            
        } catch (DocumentException ex) {
            Logger.getLogger(pdfCfd_CfdiTimbradoFormato2.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        
        
    }
    
    
    
    
    
    //esta es la tabla que va en la parte Superior Derecha
    private class CeldaPDF {
        public PdfPTable addContent() {
            Font smallFont = new Font(Font.FontFamily.HELVETICA,7,Font.NORMAL,BaseColor.BLACK);
            Font sont = new Font(Font.FontFamily.HELVETICA,8,Font.BOLD,BaseColor.BLACK);
            Font smallBoldFont = new Font(Font.FontFamily.HELVETICA, 8, Font.BOLD, BaseColor.WHITE);
            Font smallBoldFont7= new Font(Font.FontFamily.HELVETICA,7,Font.BOLD,BaseColor.WHITE);
            Font largeBoldFont = new Font(Font.FontFamily.HELVETICA,10,Font.BOLD,BaseColor.BLACK);
            
            PdfPTable table = new PdfPTable(1);
            PdfPCell cell;
            
            //TIPO DE DOCUMENTO
            cell = new PdfPCell(new Paragraph(getEtiqueta_tipo_doc(),largeBoldFont));
            cell.setBorder(0);
            cell.setUseAscender(true);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setUseDescender(true);
            cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            table.addCell(cell);
            
            
            cell = new PdfPCell(new Paragraph("No.",smallBoldFont7));
            cell.setUseAscender(true);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setUseDescender(true);
            cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            cell.setBackgroundColor(BaseColor.BLACK);
            table.addCell(cell);
            
            cell = new PdfPCell(new Paragraph(getSerie_folio(),sont));
            cell.setUseAscender(true);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setUseDescender(true);
            cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            table.addCell(cell);
            
            cell = new PdfPCell(new Paragraph("FECHA Y HORA",smallBoldFont7));
            cell.setUseAscender(true);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setUseDescender(true);
            cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            cell.setBackgroundColor(BaseColor.BLACK);
            table.addCell(cell);
            
            cell = new PdfPCell(new Paragraph(getFacha_comprobante(),smallFont));
            cell.setUseAscender(true);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setUseDescender(true);
            cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            table.addCell(cell);
            
            if(getTipo_facturacion().equals("cfditf")){
                cell = new PdfPCell(new Paragraph("FOLIO FISCAL",smallBoldFont7));
                cell.setUseAscender(true);
                cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                cell.setUseDescender(true);
                cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                cell.setBackgroundColor(BaseColor.BLACK);
                table.addCell(cell);

                cell = new PdfPCell(new Paragraph(getUuid(),smallFont));
                cell.setUseAscender(true);
                cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                cell.setUseDescender(true);
                cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                table.addCell(cell);
            }
            
            if(getTipo_facturacion().equals("cfd")){
                cell = new PdfPCell(new Paragraph("NO. Y AÑO DE APROBACIÓN",smallBoldFont7));
                cell.setUseAscender(true);
                cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                cell.setUseDescender(true);
                cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                cell.setBackgroundColor(BaseColor.BLACK);
                table.addCell(cell);

                cell = new PdfPCell(new Paragraph(getNo_aprobacion() + "   "+getAno_aprobacion(),smallFont));
                cell.setUseAscender(true);
                cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                cell.setUseDescender(true);
                cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                table.addCell(cell);
            }
            
            cell = new PdfPCell(new Paragraph("NO. CERTIFICADO",smallBoldFont7));
            cell.setUseAscender(true);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setUseDescender(true);
            cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            cell.setBackgroundColor(BaseColor.BLACK);
            table.addCell(cell);

            cell = new PdfPCell(new Paragraph(getNo_certificado(),smallFont));
            cell.setUseAscender(true);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setUseDescender(true);
            cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            table.addCell(cell);
            
            //celda vacia
            cell = new PdfPCell(new Paragraph("",smallFont));
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            cell.setBorder(0);
            table.addCell(cell);

            
            return table;
        }
    }
    
    
    
    
    //esta es la tabla para los datos del CLIENTE
    private class CeldaCustomer {
        public PdfPTable addContent() {
            Font smallFont = new Font(Font.FontFamily.HELVETICA,7,Font.NORMAL,BaseColor.BLACK);
            Font sont = new Font(Font.FontFamily.HELVETICA,8,Font.BOLD,BaseColor.BLACK);
            Font smallBoldFont = new Font(Font.FontFamily.HELVETICA, 8, Font.BOLD, BaseColor.WHITE);
            Font smallBoldFont7= new Font(Font.FontFamily.HELVETICA,7,Font.BOLD,BaseColor.BLACK);
            Font largeBoldFont = new Font(Font.FontFamily.HELVETICA,10,Font.BOLD,BaseColor.BLACK);
            
            //tabla contenedor
            float [] widths = {8f,0.3f,10};
            PdfPTable table = new PdfPTable(widths);
            PdfPCell cell;
            
            
            
            PdfPTable tableCustomer = new PdfPTable(1);
            tableCustomer.setKeepTogether(false);
            
            cell = new PdfPCell(new Paragraph("CLIENTE", smallBoldFont7));
            cell.setBorder(0);
            cell.setUseAscender(true);
            cell.setHorizontalAlignment(Element.ALIGN_LEFT);
            cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
            tableCustomer.addCell(cell);
            
            cell = new PdfPCell(new Paragraph(getReceptor_razon_social(), smallFont));
            cell.setBorder(0);
            cell.setUseAscender(true);
            cell.setHorizontalAlignment(Element.ALIGN_LEFT);
            tableCustomer.addCell(cell);
            
            cell = new PdfPCell(new Paragraph("R.F.C.", smallBoldFont7));
            cell.setBorder(0);
            cell.setUseAscender(true);
            cell.setHorizontalAlignment(Element.ALIGN_LEFT);
            cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
            tableCustomer.addCell(cell);
            
            cell = new PdfPCell(new Paragraph(getReceptor_rfc(), smallFont));
            cell.setBorder(0);
            cell.setUseAscender(true);
            cell.setHorizontalAlignment(Element.ALIGN_LEFT);
            tableCustomer.addCell(cell);
            
            
            cell = new PdfPCell(new Paragraph("DIRECCIÓN", smallBoldFont7));
            cell.setBorder(0);
            cell.setUseAscender(true);
            cell.setHorizontalAlignment(Element.ALIGN_LEFT);
            cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
            tableCustomer.addCell(cell);
            
            String municipioCliente="";
            String estadoCliente = "";
            
            if(getReceptor_rfc().equals("XXX000000000")){
                municipioCliente="X";
                estadoCliente = "X";
            }else{
                municipioCliente = getReceptor_municipio().toUpperCase();
                estadoCliente = getReceptor_estado().toUpperCase();
            }
            
            String no="";
            if( !getReceptor_numero().equals("") && getReceptor_numero()!=null){
                no= ""+getReceptor_numero();
            }
            
            if( !getReceptor_numero_exterior().equals("") && getReceptor_numero_exterior()!=null){
                if(no.equals("")){
                    no+= ""+getReceptor_numero_exterior();
                }else{
                    no+= ", "+getReceptor_numero_exterior();
                }
            }
            
            cell = new PdfPCell(new Paragraph( 
                    getReceptor_calle()+ " " + no +"\n"+ 
                    getReceptor_colonia().toUpperCase() + "\n" + 
                    municipioCliente + ", " + estadoCliente+",\n" + 
                    getReceptor_pais().toUpperCase()+ ".\n"+ 
                    "C.P. " + getReceptor_cp(), smallFont));
            cell.setBorder(0);
            cell.setUseAscender(true);
            cell.setHorizontalAlignment(Element.ALIGN_LEFT);
            tableCustomer.addCell(cell);
            
            
            //agregar la tabla con datos de la empresa
            cell = new PdfPCell(tableCustomer);
            cell.setUseAscender(true);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setUseDescender(true);
            cell.setVerticalAlignment(Element.ALIGN_TOP);
            table.addCell(cell);
            
            
            //celda vacia
            cell = new PdfPCell(new Paragraph("", smallBoldFont7));
            cell.setBorder(0);
            table.addCell(cell);
            
            
            
            
            float [] widths2 = {1.5f,2.8f};
            PdfPTable table2 = new PdfPTable(widths2);
            table2.setKeepTogether(false);
            
            //fila 1
            cell = new PdfPCell(new Paragraph("NO. DE CLIENTE", smallBoldFont7));
            cell.setBorder(0);
            cell.setUseAscender(true);
            cell.setHorizontalAlignment(Element.ALIGN_LEFT);
            cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
            table2.addCell(cell);
            
            
            cell = new PdfPCell(new Paragraph("METODO DE PAGO", smallBoldFont7));
            cell.setBorder(0);
            cell.setUseAscender(true);
            cell.setHorizontalAlignment(Element.ALIGN_LEFT);
            cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
            table2.addCell(cell);
            
            cell = new PdfPCell(new Paragraph(getReceptor_no_control(), smallFont));
            cell.setBorder(0);
            cell.setUseAscender(true);
            cell.setHorizontalAlignment(Element.ALIGN_LEFT);
            table2.addCell(cell);
            
            if (getProposito().equals("FACTURA")){
                cell = new PdfPCell(new Paragraph(getMetodo_pago(), smallFont));
                cell.setBorder(0);
                cell.setUseAscender(true);
                cell.setHorizontalAlignment(Element.ALIGN_LEFT);
                table2.addCell(cell);
            }else{
                //cell = new PdfPCell(new Paragraph("", smallFont));
                cell = new PdfPCell(new Paragraph(getMetodo_pago(), smallFont));
                cell.setBorder(0);
                cell.setUseAscender(true);
                cell.setHorizontalAlignment(Element.ALIGN_LEFT);
                table2.addCell(cell);
            }
            
            
            //fila 2
            cell = new PdfPCell(new Paragraph("NO. DE ORDEN", smallBoldFont7));
            cell.setBorder(0);
            cell.setUseAscender(true);
            cell.setHorizontalAlignment(Element.ALIGN_LEFT);
            cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
            table2.addCell(cell);
            
            
            cell = new PdfPCell(new Paragraph("CONDICIONES DE PAGO", smallBoldFont7));
            cell.setBorder(0);
            cell.setUseAscender(true);
            cell.setHorizontalAlignment(Element.ALIGN_LEFT);
            cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
            table2.addCell(cell);
            
            cell = new PdfPCell(new Paragraph(getOrdenCompra(), smallFont));
            cell.setBorder(0);
            cell.setUseAscender(true);
            cell.setHorizontalAlignment(Element.ALIGN_LEFT);
            table2.addCell(cell);
            
            if (getProposito().equals("FACTURA")){
                cell = new PdfPCell(new Paragraph(getTerminos(), smallFont));
                cell.setBorder(0);
                cell.setUseAscender(true);
                cell.setHorizontalAlignment(Element.ALIGN_LEFT);
                table2.addCell(cell);
            }else{
                cell = new PdfPCell(new Paragraph("", smallFont));
                cell.setBorder(0);
                cell.setUseAscender(true);
                cell.setHorizontalAlignment(Element.ALIGN_LEFT);
                table2.addCell(cell);
            }
            
            
            //fila 3
            cell = new PdfPCell(new Paragraph("MONEDA", smallBoldFont7));
            cell.setBorder(0);
            cell.setUseAscender(true);
            cell.setHorizontalAlignment(Element.ALIGN_LEFT);
            cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
            table2.addCell(cell);
            
            cell = new PdfPCell(new Paragraph("FORMA DE PAGO", smallBoldFont7));
            cell.setBorder(0);
            cell.setUseAscender(true);
            cell.setHorizontalAlignment(Element.ALIGN_LEFT);
            cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
            table2.addCell(cell);
            
            cell = new PdfPCell(new Paragraph(getMonedaIso(), smallFont));
            cell.setBorder(0);
            cell.setUseAscender(true);
            cell.setHorizontalAlignment(Element.ALIGN_LEFT);
            table2.addCell(cell);
            
            if (getProposito().equals("FACTURA")){
                cell = new PdfPCell(new Paragraph(getFormaPago(), smallFont));
                cell.setBorder(0);
                cell.setUseAscender(true);
                cell.setHorizontalAlignment(Element.ALIGN_LEFT);
                table2.addCell(cell);
            }else{
                cell = new PdfPCell(new Paragraph("", smallFont));
                cell.setBorder(0);
                cell.setUseAscender(true);
                cell.setHorizontalAlignment(Element.ALIGN_LEFT);
                table2.addCell(cell);
            }
            
            
            //fila 4
            cell = new PdfPCell(new Paragraph("TIPO DE CAMBIO", smallBoldFont7));
            cell.setBorder(0);
            cell.setUseAscender(true);
            cell.setHorizontalAlignment(Element.ALIGN_LEFT);
            cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
            table2.addCell(cell);
            
            cell = new PdfPCell(new Paragraph("NO. DE CUENTA", smallBoldFont7));
            cell.setBorder(0);
            cell.setUseAscender(true);
            cell.setHorizontalAlignment(Element.ALIGN_LEFT);
            cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
            table2.addCell(cell);
            
            cell = new PdfPCell(new Paragraph(getTipoCambio(), smallFont));
            cell.setBorder(0);
            cell.setUseAscender(true);
            cell.setHorizontalAlignment(Element.ALIGN_LEFT);
            table2.addCell(cell);
            
            if (getProposito().equals("FACTURA")){
                cell = new PdfPCell(new Paragraph(getNo_cuenta(), smallFont));
                cell.setBorder(0);
                cell.setUseAscender(true);
                cell.setHorizontalAlignment(Element.ALIGN_LEFT);
                table2.addCell(cell);
            }else{
                cell = new PdfPCell(new Paragraph("", smallFont));
                cell.setBorder(0);
                cell.setUseAscender(true);
                cell.setHorizontalAlignment(Element.ALIGN_LEFT);
                table2.addCell(cell);
            }
            
            //fila 5
            cell = new PdfPCell(new Paragraph("FECHA DE PAGO", smallBoldFont7));
            cell.setBorder(0);
            cell.setUseAscender(true);
            cell.setHorizontalAlignment(Element.ALIGN_LEFT);
            cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
            table2.addCell(cell);
            
            cell = new PdfPCell(new Paragraph("AGENTE DE VENTAS", smallBoldFont7));
            cell.setBorder(0);
            cell.setUseAscender(true);
            cell.setHorizontalAlignment(Element.ALIGN_LEFT);
            cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
            table2.addCell(cell);
            
            cell = new PdfPCell(new Paragraph(getFecha_pago(), smallFont));
            cell.setBorder(0);
            cell.setUseAscender(true);
            cell.setHorizontalAlignment(Element.ALIGN_LEFT);
            table2.addCell(cell);
            
            
            cell = new PdfPCell(new Paragraph(getVendedor(), smallFont));
            cell.setBorder(0);
            cell.setUseAscender(true);
            cell.setHorizontalAlignment(Element.ALIGN_LEFT);
            table2.addCell(cell);
            
            
            
            //agregar tabla de condiciones de venta y pago
            cell = new PdfPCell(table2);
            cell.setUseAscender(true);
            cell.setUseDescender(true);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setVerticalAlignment(Element.ALIGN_TOP);
            table.addCell(cell);
            
            return table;
        }
    }
    
    
    
    //esta es la tabla para los datos del CLIENTE
    private class tablaConceptos {
        public PdfPTable addContent() {
            Font smallFont = new Font(Font.FontFamily.HELVETICA,7,Font.NORMAL,BaseColor.BLACK);
            Font sont = new Font(Font.FontFamily.HELVETICA,8,Font.BOLD,BaseColor.BLACK);
            Font smallBoldFont = new Font(Font.FontFamily.HELVETICA, 7, Font.BOLD, BaseColor.WHITE);
            Font smallBoldFont6= new Font(Font.FontFamily.HELVETICA,7,Font.BOLD,BaseColor.BLACK);
            Font largeBoldFont = new Font(Font.FontFamily.HELVETICA,10,Font.BOLD,BaseColor.BLACK);
            
            //tabla contenedor
            PdfPCell cell;
            float [] widths;
            String[] columnas;
            
            
            float [] widths1 = {
                1f, //CLAVE
                2.3f, //DESCRIPCIÓN
                1f, //UNIDAD
                //1.1f, //PRESENTACIÓN
                1f,//CANTIDAD
                0.5f, //simbolo
                1.2f, //PRECIO UNITARIO
                0.5f, //simbolo
                1.2f //IMPORTE
            };
            float [] widths2 = {1f, 2.3f, 1f, 1f, 0.5f, 1.2f, 0.5f, 1.2f, 0.5f, 1f};
            
            //String[] columnas = {"CLAVE","DESCRIPCIÓN","UNIDAD","PRESENTACIÓN","CANTIDAD"," ","PRECIO UNITARIO"," ","IMPORTE"};
            String[] columnas1 = {"CLAVE","DESCRIPCIÓN","UNIDAD","CANTIDAD"," ","PRECIO UNITARIO"," ","IMPORTE"};
            String[] columnas2 = {"CLAVE","DESCRIPCIÓN","UNIDAD","CANTIDAD"," ","PRECIO UNITARIO"," ","IMPORTE"," ","MONTO IEPS"};
            
            if(Double.parseDouble(getMontoIeps())>0){
                //Aqui entra cuando incluye IEPS                
                if (getProposito().equals("FACTURA")){
                    widths=widths2;
                    columnas = columnas2;
                }else{
                    //Nota de Credito
                    widths=widths1;
                    columnas = columnas1;
                }
            }else{
                //Aqui entra cuando NO INCLUYE IEPS
                widths=widths1;
                columnas = columnas1;
            }
            
            PdfPTable table = new PdfPTable(widths);
            table.setKeepTogether(false);
            table.setHeaderRows(1);
           
            List<String>  lista_columnas = (List<String>) Arrays.asList(columnas);
            Integer contador = 0;
            PdfPCell cellX;
            
            for ( String columna_titulo : lista_columnas){
                cellX = new PdfPCell(new Paragraph(columna_titulo,smallBoldFont));
                cellX.setUseAscender(true);
                cellX.setUseDescender(true);
                cellX.setBackgroundColor(BaseColor.BLACK);
                
                if(columna_titulo.equals("CLAVE")){
                    cellX.setHorizontalAlignment(Element.ALIGN_CENTER);
                    cellX.setVerticalAlignment(Element.ALIGN_TOP);
                }
                if(columna_titulo.equals("DESCRIPCIÓN")){
                    cellX.setHorizontalAlignment(Element.ALIGN_CENTER);
                    cellX.setVerticalAlignment(Element.ALIGN_TOP);
                }
                if(columna_titulo.equals("UNIDAD")){
                    cellX.setHorizontalAlignment(Element.ALIGN_CENTER);
                    cellX.setVerticalAlignment(Element.ALIGN_TOP);
                }
                /*
                if(columna_titulo.equals("PRESENTACIÓN")){
                    cellX.setHorizontalAlignment(Element.ALIGN_CENTER);
                    cellX.setVerticalAlignment(Element.ALIGN_TOP);
                }
                 */
                if(columna_titulo.equals("CANTIDAD")){
                    cellX.setHorizontalAlignment(Element.ALIGN_CENTER);
                    cellX.setVerticalAlignment(Element.ALIGN_TOP);
                }
                if(columna_titulo.equals("PRECIO UNITARIO")){
                    cellX.setHorizontalAlignment(Element.ALIGN_CENTER);
                    cellX.setVerticalAlignment(Element.ALIGN_TOP);
                }
                if(columna_titulo.equals("IMPORTE")){
                    cellX.setHorizontalAlignment(Element.ALIGN_CENTER);
                    cellX.setVerticalAlignment(Element.ALIGN_TOP);
                }
                table.addCell(cellX);
            }
            
            
           for (HashMap<String, String> registro : getRows()){
                //Indices del HashMap que representa el row
                //String[] wordList = {"sku","titulo","unidad","presentacion","cantidad","simbolo_moneda","precio_unitario","simbolo_moneda","importe"};
               String[] wordList;
               String[] wordList1 = {"sku","titulo","unidad","cantidad","simbolo_moneda","precio_unitario","simbolo_moneda","importe"};
               String[] wordList2 = {"sku","titulo","unidad","cantidad","simbolo_moneda","precio_unitario","simbolo_moneda","importe","simbolo_moneda_ieps","importe_ieps"};
                if(Double.parseDouble(getMontoIeps())>0){
                    //Aqui entra cuando incluye IEPS
                    if (getProposito().equals("FACTURA")){
                        wordList=wordList2;
                    }else{
                        //Nota de Credito
                        wordList=wordList1;
                    }
                }else{
                    //Aqui entra cuando NO INCLUYE IEPS
                    wordList=wordList1;
                }
                
               
                List<String>  indices = (List<String>) Arrays.asList(wordList);
                
                for (String omega : indices){
                    PdfPCell celda;
                    
                    if (omega.equals("sku")){
                        celda = new PdfPCell(new Paragraph(registro.get(omega),smallFont));
                        celda.setHorizontalAlignment(Element.ALIGN_LEFT);
                        celda.setVerticalAlignment(Element.ALIGN_TOP);
                        celda.setBorderWidthBottom(0);
                        celda.setBorderWidthLeft(1);
                        celda.setBorderWidthRight(0.5f);
                        celda.setBorderWidthTop(0);
                        celda.setBorderColorRight(BaseColor.LIGHT_GRAY);
                        table.addCell(celda);
                    }
                    
                    if (omega.equals("titulo")){
                        celda = new PdfPCell(new Paragraph(registro.get(omega).toUpperCase() + registro.get("etiqueta_ieps").toUpperCase(),smallFont));
                        celda.setHorizontalAlignment(Element.ALIGN_LEFT);
                        celda.setVerticalAlignment(Element.ALIGN_TOP);
                        celda.setBorderWidthBottom(0);
                        celda.setBorderWidthLeft(0);
                        celda.setBorderWidthTop(0);
                        celda.setBorderWidthRight(0.5f);
                        celda.setBorderColorRight(BaseColor.LIGHT_GRAY);
                        table.addCell(celda);
                    }
                    
                    
                    if (omega.equals("unidad")){
                        celda = new PdfPCell(new Paragraph(registro.get(omega).toUpperCase(),smallFont));
                        celda.setHorizontalAlignment(Element.ALIGN_LEFT);
                        celda.setVerticalAlignment(Element.ALIGN_TOP);
                        celda.setBorderWidthBottom(0);
                        celda.setBorderWidthLeft(0);
                        celda.setBorderWidthTop(0);
                        celda.setBorderWidthRight(0.5f);
                        celda.setBorderColorRight(BaseColor.LIGHT_GRAY);
                        table.addCell(celda);
                    }
                    
                    /*
                    if (omega.equals("presentacion")){
                        celda = new PdfPCell(new Paragraph(registro.get(omega).toUpperCase(),smallFont));
                        celda.setHorizontalAlignment(Element.ALIGN_LEFT);
                        celda.setVerticalAlignment(Element.ALIGN_TOP);
                        celda.setBorderWidthBottom(0);
                        celda.setBorderWidthLeft(0);
                        celda.setBorderWidthTop(0);
                        celda.setBorderWidthRight(0.5f);
                        celda.setBorderColorRight(BaseColor.LIGHT_GRAY);
                        table.addCell(celda);
                    }
                    */
                    if (omega.equals("cantidad")){
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
                    
                    if (omega.equals("simbolo_moneda")){
                        celda = new PdfPCell(new Paragraph( "$",smallFont));
                        celda.setHorizontalAlignment(Element.ALIGN_RIGHT);
                        celda.setVerticalAlignment(Element.ALIGN_TOP);
                        celda.setBorderWidthBottom(0);
                        celda.setBorderWidthLeft(0);
                        celda.setBorderWidthRight(0);
                        celda.setBorderWidthTop(0);
                        table.addCell(celda);
                    }
                    
                    if (omega.equals("precio_unitario")){
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
                    
                    if (omega.equals("importe")){
                        celda= new PdfPCell(new Paragraph(StringHelper.AgregaComas(StringHelper.roundDouble(registro.get(omega).toString(),2)),smallFont));
                        celda.setHorizontalAlignment(Element.ALIGN_RIGHT);
                        celda.setVerticalAlignment(Element.ALIGN_TOP);
                        celda.setBorderWidthBottom(0);
                        celda.setBorderWidthLeft(0);
                        if(Double.parseDouble(getMontoIeps())>0){
                            //Aqui entra cuando incluye IEPS
                            if (getProposito().equals("FACTURA")){
                                celda.setBorderWidthRight(0.5f);
                            }else{
                                //Nota de Credito
                                celda.setBorderWidthRight(1);
                            }
                        }else{
                            //Aqui entra cuando NO INCLUYE IEPS
                            celda.setBorderWidthRight(1);
                        }
                        
                        celda.setBorderWidthTop(0);
                        table.addCell(celda);
                    }
                    
                    
                    if (omega.equals("simbolo_moneda_ieps")){
                        if(Double.parseDouble(registro.get("importe_ieps"))>0){
                            celda = new PdfPCell(new Paragraph( "$",smallFont));
                        }else{
                            celda = new PdfPCell(new Paragraph( "",smallFont));
                        }
                        celda.setHorizontalAlignment(Element.ALIGN_RIGHT);
                        celda.setVerticalAlignment(Element.ALIGN_TOP);
                        celda.setBorderWidthBottom(0);
                        celda.setBorderWidthLeft(0);
                        celda.setBorderWidthRight(0);
                        celda.setBorderWidthTop(0);
                        table.addCell(celda);
                    }
                    
                    if (omega.equals("importe_ieps")){
                        if(Double.parseDouble(registro.get(omega))>0){
                            celda= new PdfPCell(new Paragraph(StringHelper.AgregaComas(StringHelper.roundDouble(registro.get(omega).toString(),2)),smallFont));
                        }else{
                            celda= new PdfPCell(new Paragraph("",smallFont));
                        }
                        celda.setHorizontalAlignment(Element.ALIGN_RIGHT);
                        celda.setVerticalAlignment(Element.ALIGN_TOP);
                        celda.setBorderWidthBottom(0);
                        celda.setBorderWidthLeft(0);
                        celda.setBorderWidthRight(1);
                        celda.setBorderWidthTop(0);
                        table.addCell(celda);
                    }
                }
                contador++;
            }
            
           
           int colspan=5;
           
            if(Double.parseDouble(getMontoIeps())>0){
                //Aqui entra cuando incluye IEPS
                if (getProposito().equals("FACTURA")){
                    colspan=7;
                }
            }
            
            
            if(Double.parseDouble(getMontoDescuento())>0){
                //fila IMPORTE SUBTOTAL
                cell = new PdfPCell(new Paragraph("", smallFont));
                cell.setHorizontalAlignment(Element.ALIGN_LEFT);
                cell.setVerticalAlignment(Element.ALIGN_TOP);
                //cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
                cell.setBorderWidthBottom(0);
                cell.setBorderWidthLeft(0);
                cell.setBorderWidthRight(1);
                cell.setBorderWidthTop(1);
                cell.setColspan(colspan);
                table.addCell(cell);

                cell = new PdfPCell(new Paragraph("IMPORTE",smallBoldFont6));
                cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                cell.setVerticalAlignment(Element.ALIGN_TOP);
                cell.setBorderWidthBottom(0);
                cell.setBorderWidthLeft(0);
                cell.setBorderWidthRight(1);
                cell.setBorderWidthTop(1);
                table.addCell(cell);

                cell= new PdfPCell(new Paragraph(getSimbolo_moneda(),smallFont));
                cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                cell.setVerticalAlignment(Element.ALIGN_TOP);
                cell.setBorderWidthBottom(0);
                cell.setBorderWidthLeft(0);
                cell.setBorderWidthRight(0);
                cell.setBorderWidthTop(1);
                table.addCell(cell);

                cell= new PdfPCell(new Paragraph(StringHelper.AgregaComas(StringHelper.roundDouble(getSubTotalSinDescuento(),2)),smallBoldFont6));
                cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                cell.setVerticalAlignment(Element.ALIGN_TOP);
                cell.setBorderWidthBottom(0);
                cell.setBorderWidthLeft(0);
                cell.setBorderWidthRight(1);
                cell.setBorderWidthTop(1);
                table.addCell(cell);
                
                
                
                //Fila DESCUENTO
                cell = new PdfPCell(new Paragraph("MOTIVO DEL DESCUENTO", smallBoldFont6));
                cell.setHorizontalAlignment(Element.ALIGN_LEFT);
                cell.setVerticalAlignment(Element.ALIGN_TOP);
                cell.setBorderWidthBottom(0);
                cell.setBorderWidthLeft(0);
                cell.setBorderWidthRight(1);
                cell.setBorderWidthTop(0);
                cell.setColspan(colspan);
                table.addCell(cell);
                
                cell = new PdfPCell(new Paragraph("DESCUENTO",smallBoldFont6));
                cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                cell.setVerticalAlignment(Element.ALIGN_TOP);
                cell.setBorderWidthBottom(0);
                cell.setBorderWidthLeft(0);
                cell.setBorderWidthRight(1);
                cell.setBorderWidthTop(0);
                table.addCell(cell);
                
                cell= new PdfPCell(new Paragraph(getSimbolo_moneda(),smallFont));
                cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                cell.setVerticalAlignment(Element.ALIGN_TOP);
                cell.setBorderWidthBottom(0);
                cell.setBorderWidthLeft(0);
                cell.setBorderWidthRight(0);
                cell.setBorderWidthTop(0);
                table.addCell(cell);

                cell= new PdfPCell(new Paragraph(StringHelper.AgregaComas(StringHelper.roundDouble(getMontoDescuento(),2)),smallBoldFont6));
                cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                cell.setVerticalAlignment(Element.ALIGN_TOP);
                cell.setBorderWidthBottom(0);
                cell.setBorderWidthLeft(0);
                cell.setBorderWidthRight(1);
                cell.setBorderWidthTop(0);
                table.addCell(cell);
                
                
                
                //SUBTOTAL
                cell = new PdfPCell(new Paragraph(getMotivoDescuento(), smallFont));
                cell.setHorizontalAlignment(Element.ALIGN_LEFT);
                cell.setVerticalAlignment(Element.ALIGN_TOP);
                cell.setBorderWidthBottom(0);
                cell.setBorderWidthLeft(0);
                cell.setBorderWidthRight(1);
                cell.setBorderWidthTop(0);
                cell.setColspan(colspan);
                table.addCell(cell);
                
                cell = new PdfPCell(new Paragraph("SUB-TOTAL",smallBoldFont6));
                cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                cell.setVerticalAlignment(Element.ALIGN_TOP);
                cell.setBorderWidthBottom(0);
                cell.setBorderWidthLeft(0);
                cell.setBorderWidthRight(1);
                cell.setBorderWidthTop(0);
                table.addCell(cell);
                
                cell= new PdfPCell(new Paragraph(getSimbolo_moneda(),smallFont));
                cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                cell.setVerticalAlignment(Element.ALIGN_TOP);
                cell.setBorderWidthBottom(0);
                cell.setBorderWidthLeft(0);
                cell.setBorderWidthRight(0);
                cell.setBorderWidthTop(0);
                table.addCell(cell);

                cell= new PdfPCell(new Paragraph(StringHelper.AgregaComas(StringHelper.roundDouble(getSubTotalConDescuento(),2)),smallBoldFont6));
                cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                cell.setVerticalAlignment(Element.ALIGN_TOP);
                cell.setBorderWidthBottom(0);
                cell.setBorderWidthLeft(0);
                cell.setBorderWidthRight(1);
                cell.setBorderWidthTop(0);
                table.addCell(cell);
            }else{
                //fila SUBTOTAL
                cell = new PdfPCell(new Paragraph("", smallFont));
                cell.setHorizontalAlignment(Element.ALIGN_LEFT);
                cell.setVerticalAlignment(Element.ALIGN_TOP);
                cell.setBorderWidthBottom(0);
                cell.setBorderWidthLeft(0);
                cell.setBorderWidthRight(1);
                cell.setBorderWidthTop(1);
                cell.setColspan(colspan);
                table.addCell(cell);

                cell = new PdfPCell(new Paragraph("SUB-TOTAL",smallBoldFont6));
                cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                cell.setVerticalAlignment(Element.ALIGN_TOP);
                cell.setBorderWidthBottom(0);
                cell.setBorderWidthLeft(0);
                cell.setBorderWidthRight(1);
                cell.setBorderWidthTop(1);
                table.addCell(cell);

                cell= new PdfPCell(new Paragraph(getSimbolo_moneda(),smallFont));
                cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                cell.setVerticalAlignment(Element.ALIGN_TOP);
                cell.setBorderWidthBottom(0);
                cell.setBorderWidthLeft(0);
                cell.setBorderWidthRight(0);
                cell.setBorderWidthTop(1);
                table.addCell(cell);

                cell= new PdfPCell(new Paragraph(StringHelper.AgregaComas(StringHelper.roundDouble(getSubTotal(),2)),smallBoldFont6));
                cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                cell.setVerticalAlignment(Element.ALIGN_TOP);
                cell.setBorderWidthBottom(0);
                cell.setBorderWidthLeft(0);
                cell.setBorderWidthRight(1);
                cell.setBorderWidthTop(1);
                table.addCell(cell);
            }
            
            
            


            
            //FILA IEPS
            if(Double.parseDouble(getMontoIeps())>0){
                cell = new PdfPCell(new Paragraph("", smallBoldFont6));
                cell.setHorizontalAlignment(Element.ALIGN_LEFT);
                cell.setVerticalAlignment(Element.ALIGN_TOP);
                //celda.setBorder(0);
                cell.setBorderWidthBottom(0);
                cell.setBorderWidthLeft(0);
                cell.setBorderWidthRight(1);
                cell.setBorderWidthTop(0);
                cell.setColspan(colspan);
                table.addCell(cell);
                
                cell = new PdfPCell(new Paragraph("IEPS",smallBoldFont6));
                cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                cell.setVerticalAlignment(Element.ALIGN_TOP);
                //cell.setBorder(0);
                cell.setBorderWidthBottom(0);
                cell.setBorderWidthLeft(0);
                cell.setBorderWidthRight(1);
                cell.setBorderWidthTop(0);
                table.addCell(cell);
                
                cell= new PdfPCell(new Paragraph(getSimbolo_moneda(),smallFont));
                cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                cell.setVerticalAlignment(Element.ALIGN_TOP);
                //cell.setBorder(0);
                cell.setBorderWidthBottom(0);
                cell.setBorderWidthLeft(0);
                cell.setBorderWidthRight(0);
                cell.setBorderWidthTop(0);
                table.addCell(cell);

                cell= new PdfPCell(new Paragraph(StringHelper.AgregaComas(StringHelper.roundDouble(getMontoIeps(),2)),smallBoldFont6));
                cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                cell.setVerticalAlignment(Element.ALIGN_TOP);
                //cell.setBorder(0);
                cell.setBorderWidthBottom(0);
                cell.setBorderWidthLeft(0);
                cell.setBorderWidthRight(1);
                cell.setBorderWidthTop(0);
                table.addCell(cell);
            }
            
            String etiqueta_importe="";
            if(Double.parseDouble(getMontoRetencion())<=0){
                etiqueta_importe = "IMPORTE CON LETRA";
            }
            
            //fila IVA
            cell = new PdfPCell(new Paragraph(etiqueta_importe, smallBoldFont6));
            cell.setHorizontalAlignment(Element.ALIGN_LEFT);
            cell.setVerticalAlignment(Element.ALIGN_TOP);
            //celda.setBorder(0);
            cell.setBorderWidthBottom(0);
            cell.setBorderWidthLeft(0);
            cell.setBorderWidthRight(1);
            cell.setBorderWidthTop(0);
            cell.setColspan(colspan);
            table.addCell(cell);
            
            cell = new PdfPCell(new Paragraph("IVA 16%",smallBoldFont6));
            cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            cell.setVerticalAlignment(Element.ALIGN_TOP);
            //cell.setBorder(0);
            cell.setBorderWidthBottom(0);
            cell.setBorderWidthLeft(0);
            cell.setBorderWidthRight(1);
            cell.setBorderWidthTop(0);
            table.addCell(cell);
            
            cell= new PdfPCell(new Paragraph(getSimbolo_moneda(),smallFont));
            cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            cell.setVerticalAlignment(Element.ALIGN_TOP);
            //cell.setBorder(0);
            cell.setBorderWidthBottom(0);
            cell.setBorderWidthLeft(0);
            cell.setBorderWidthRight(0);
            cell.setBorderWidthTop(0);
            table.addCell(cell);
            
            cell= new PdfPCell(new Paragraph(StringHelper.AgregaComas(StringHelper.roundDouble(getMontoImpuesto(),2)),smallBoldFont6));
            cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            cell.setVerticalAlignment(Element.ALIGN_TOP);
            //cell.setBorder(0);
            cell.setBorderWidthBottom(0);
            cell.setBorderWidthLeft(0);
            cell.setBorderWidthRight(1);
            cell.setBorderWidthTop(0);
            table.addCell(cell);
            
            
            //FILA IVA RETENIDO
            if(Double.parseDouble(getMontoRetencion())>0){
                cell = new PdfPCell(new Paragraph("IMPORTE CON LETRA", smallBoldFont6));
                cell.setHorizontalAlignment(Element.ALIGN_LEFT);
                cell.setVerticalAlignment(Element.ALIGN_TOP);
                //celda.setBorder(0);
                cell.setBorderWidthBottom(0);
                cell.setBorderWidthLeft(0);
                cell.setBorderWidthRight(1);
                cell.setBorderWidthTop(0);
                cell.setColspan(colspan);
                table.addCell(cell);
                
                cell = new PdfPCell(new Paragraph("RETENCIÓN",smallBoldFont6));
                cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                cell.setVerticalAlignment(Element.ALIGN_TOP);
                //cell.setBorder(0);
                cell.setBorderWidthBottom(0);
                cell.setBorderWidthLeft(0);
                cell.setBorderWidthRight(1);
                cell.setBorderWidthTop(0);
                table.addCell(cell);

                cell= new PdfPCell(new Paragraph(getSimbolo_moneda(),smallBoldFont6));
                cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                cell.setVerticalAlignment(Element.ALIGN_TOP);
                //cell.setBorder(0);
                cell.setBorderWidthBottom(0);
                cell.setBorderWidthLeft(0);
                cell.setBorderWidthRight(0);
                cell.setBorderWidthTop(0);
                table.addCell(cell);

                cell= new PdfPCell(new Paragraph(StringHelper.AgregaComas(StringHelper.roundDouble(getMontoRetencion(),2)),smallBoldFont6));
                cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                cell.setVerticalAlignment(Element.ALIGN_TOP);
                //cell.setBorder(0);
                cell.setBorderWidthBottom(0);
                cell.setBorderWidthLeft(0);
                cell.setBorderWidthRight(1);
                cell.setBorderWidthTop(0);
                table.addCell(cell);
            }
            
            
            
            BigInteger num = new BigInteger(getMontoTotal().split("\\.")[0]);
            n2t cal = new n2t();
            String centavos = getMontoTotal().substring(getMontoTotal().indexOf(".")+1);
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
            
            //FILA TOTAL
            cell = new PdfPCell(new Paragraph(numeroMay.toUpperCase() + " " + denominacion.toUpperCase() + " " +centavos+"/100 "+ denom.toUpperCase(), smallFont));
            cell.setHorizontalAlignment(Element.ALIGN_LEFT);
            cell.setVerticalAlignment(Element.ALIGN_TOP);
            //celda.setBorder(0);
            cell.setBorderWidthBottom(0);
            cell.setBorderWidthLeft(0);
            cell.setBorderWidthRight(1);
            cell.setBorderWidthTop(0);
            cell.setColspan(colspan);
            table.addCell(cell);
            
            cell = new PdfPCell(new Paragraph("TOTAL",smallBoldFont6));
            cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            cell.setVerticalAlignment(Element.ALIGN_TOP);
            //cell.setBorder(0);
            cell.setBorderWidthBottom(1);
            cell.setBorderWidthLeft(0);
            cell.setBorderWidthRight(1);
            cell.setBorderWidthTop(0);
            table.addCell(cell);
            
            cell= new PdfPCell(new Paragraph(getSimbolo_moneda(),smallBoldFont6));
            cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            cell.setVerticalAlignment(Element.ALIGN_TOP);
            //cell.setBorder(0);
            cell.setBorderWidthBottom(1);
            cell.setBorderWidthLeft(0);
            cell.setBorderWidthRight(0);
            cell.setBorderWidthTop(0);
            table.addCell(cell);
            
            cell= new PdfPCell(new Paragraph(StringHelper.AgregaComas(StringHelper.roundDouble(getMontoTotal(),2)),smallBoldFont6));
            cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            cell.setVerticalAlignment(Element.ALIGN_TOP);
            //cell.setBorder(0);
            cell.setBorderWidthBottom(1);
            cell.setBorderWidthLeft(0);
            cell.setBorderWidthRight(1);
            cell.setBorderWidthTop(0);
            table.addCell(cell);
            
            return table;
        }
    }//termina tabla conceptos
    
    
    
    
    
    
    
    
    
    
    
    
    //esta es la tabla para los datos del CLIENTE
    private class celdaDatosFiscales {
        public PdfPTable addContent() throws Exception {
            Font smallFontBold5 = new Font(Font.FontFamily.HELVETICA,5,Font.BOLD,BaseColor.BLACK);
            Font smallFont6 = new Font(Font.FontFamily.HELVETICA,6,Font.NORMAL,BaseColor.BLACK);
            Font smallFont7 = new Font(Font.FontFamily.HELVETICA,7,Font.NORMAL,BaseColor.BLACK);
            Font smallBoldFont6= new Font(Font.FontFamily.HELVETICA,6,Font.BOLD,BaseColor.BLACK);
            Font smallBoldFont7= new Font(Font.FontFamily.HELVETICA,7,Font.BOLD,BaseColor.BLACK);
            
            //tabla contenedor
            PdfPCell cell;
            
            float [] widths = {2.5f,10f};
            PdfPTable table = new PdfPTable(widths);
            table.setKeepTogether(false);
            
            if(getTipo_facturacion().equals("cfditf")){
                cell = new PdfPCell(new Paragraph("INFORMACIÓN DEL TIMBRE FISCAL DIGITAL", smallBoldFont7));
                cell.setBorder(0);
                cell.setUseAscender(true);
                cell.setHorizontalAlignment(Element.ALIGN_LEFT);
                cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
                cell.setColspan(2);
                table.addCell(cell);
                
                //fila vacia
                cell = new PdfPCell(new Paragraph("", smallFontBold5));
                cell.setUseAscender(true);
                cell.setBorderWidthBottom(0);
                cell.setColspan(2);
                table.addCell(cell);
                
                
                ImagenCBB icbb = new ImagenCBB();
                
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
                        cell.setBorderWidthTop(0);
                        cell.setBorderWidthBottom(0);
                        cell.setHorizontalAlignment(Element.ALIGN_LEFT);
                        table.addCell(cell);
                        
                        FileHelper.delete(RUTA_IMAGEN);
                        
                    } catch (WriterException ex) {
                        Logger.getLogger(pdfCfd_CfdiTimbradoFormato2.class.getName()).log(Level.SEVERE, null, ex);
                    } catch (FileNotFoundException ex) {
                        Logger.getLogger(pdfCfd_CfdiTimbradoFormato2.class.getName()).log(Level.SEVERE, null, ex);
                    } catch (IOException ex) {
                        Logger.getLogger(pdfCfd_CfdiTimbradoFormato2.class.getName()).log(Level.SEVERE, null, ex);
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

                
                
                
                float [] widths2 = {2,1.5f,2.2f,1.5f};
                PdfPTable table2 = new PdfPTable(widths2);
                table2.setKeepTogether(false);
                
                cell = new PdfPCell(new Paragraph("NO. CERTIFICADO DEL SAT:", smallBoldFont7));
                cell.setBorder(0);
                cell.setUseAscender(true);
                cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                table2.addCell(cell);
                
                cell = new PdfPCell(new Paragraph(getNoCertificadoSAT(), smallFont7));
                cell.setBorder(0);
                cell.setUseAscender(true);
                cell.setHorizontalAlignment(Element.ALIGN_LEFT);
                table2.addCell(cell);
                
                cell = new PdfPCell(new Paragraph("FECHA Y HORA DE CERTIFICACIÓN:", smallBoldFont7));
                cell.setBorder(0);
                cell.setUseAscender(true);
                cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                table2.addCell(cell);
                
                cell = new PdfPCell(new Paragraph(getFachaTimbrado(), smallFont7));
                cell.setBorder(0);
                cell.setUseAscender(true);
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
                cell.setUseAscender(true);
                cell.setBorderWidthBottom(0);
                cell.setColspan(4);
                table2.addCell(cell);
            
                cell = new PdfPCell(new Paragraph(  getCadena_original()  ,smallFont7));
                cell.setUseAscender(true);
                cell.setHorizontalAlignment(Element.ALIGN_LEFT);
                cell.setBorderWidthTop(0);
                cell.setColspan(4);
                table2.addCell(cell);
                
                
                cell = new PdfPCell(new Paragraph("SELLO DIGITAL DEL EMISOR:", smallBoldFont7));
                cell.setHorizontalAlignment(Element.ALIGN_LEFT);
                cell.setUseAscender(true);
                cell.setBorderWidthBottom(0);
                cell.setColspan(4);
                table2.addCell(cell);
            
                cell = new PdfPCell(new Paragraph(getSello_digital()  ,smallFont7));
                cell.setUseAscender(true);
                cell.setHorizontalAlignment(Element.ALIGN_LEFT);
                cell.setBorderWidthTop(0);
                cell.setColspan(4);
                table2.addCell(cell);
                
                cell = new PdfPCell(new Paragraph("SELLO DIGITAL DEL SAT:", smallBoldFont7));
                cell.setHorizontalAlignment(Element.ALIGN_LEFT);
                cell.setUseAscender(true);
                cell.setBorderWidthBottom(0);
                cell.setColspan(4);
                table2.addCell(cell);
            
                cell = new PdfPCell(new Paragraph(getSello_digital_sat()  ,smallFont7));
                cell.setUseAscender(true);
                cell.setHorizontalAlignment(Element.ALIGN_LEFT);
                cell.setBorderWidthTop(0);
                cell.setColspan(4);
                table2.addCell(cell);
                
                
                cell = new PdfPCell(table2);
                cell.setBorderWidthLeft(0);
                cell.setBorderWidthBottom(0);
                cell.setBorderWidthTop(0);
                cell.setUseAscender(true);
                cell.setHorizontalAlignment(Element.ALIGN_LEFT);
                table.addCell(cell);
                
                //fila vacia
                cell = new PdfPCell(new Paragraph("", smallFontBold5));
                cell.setUseAscender(true);
                cell.setBorderWidthTop(0);
                cell.setColspan(2);
                table.addCell(cell);
                
            }
            
            
            if(getTipo_facturacion().equals("cfd")){
                //celda vacia
                /*
                cell = new PdfPCell(new Paragraph("", smallFontBold5));
                cell.setBorder(0);
                cell.setUseAscender(true);
                cell.setHorizontalAlignment(Element.ALIGN_LEFT);
                table.addCell(cell);
                */
                
                PdfPTable table2 = new PdfPTable(1);
                table2.setKeepTogether(false);
                
                cell = new PdfPCell(new Paragraph("CADENA ORIGINAL:",smallBoldFont7));
                cell.setUseAscender(true);
                cell.setHorizontalAlignment(Element.ALIGN_LEFT);
                cell.setBorderWidthBottom(0);
                table2.addCell(cell);
                
                
                cell = new PdfPCell(new Paragraph(  getCadena_original()  ,smallFont7));
                cell.setUseAscender(true);
                cell.setHorizontalAlignment(Element.ALIGN_LEFT);
                cell.setBorderWidthTop(0);
                table2.addCell(cell);
                
                cell = new PdfPCell(new Paragraph("SELLO DIGITAL DEL EMISOR:",smallBoldFont7));
                cell.setUseAscender(true);
                cell.setHorizontalAlignment(Element.ALIGN_LEFT);
                cell.setBorderWidthBottom(0);
                cell.setBorderWidthTop(0);
                table2.addCell(cell);

                cell = new PdfPCell(new Paragraph(  getSello_digital(),smallFont7));
                cell.setUseAscender(true);
                cell.setHorizontalAlignment(Element.ALIGN_LEFT);
                cell.setBorderWidthTop(0);
                table2.addCell(cell);
                
                cell = new PdfPCell(table2);
                cell.setBorder(0);
                cell.setColspan(2);
                cell.setUseAscender(true);
                cell.setHorizontalAlignment(Element.ALIGN_LEFT);
                table.addCell(cell);
                
            }
            
            return table;
        }
    }//termina celda datos fiscales
    
    
    
    


    
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
     
    public String getImagen_cedula() {
        return imagen_cedula;
    }

    public void setImagen_cedula(String imagen_cedula) {
        this.imagen_cedula = imagen_cedula;
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
    

    public ArrayList<String> getLeyendas() {
        return leyendas;
    }

    public void setLeyendas(ArrayList<String> leyendas) {
        this.leyendas = leyendas;
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
    
    public String getSubTotalConDescuento() {
        return subTotalConDescuento;
    }

    public void setSubTotalConDescuento(String subTotalConDescuento) {
        this.subTotalConDescuento = subTotalConDescuento;
    }
    
    public String getSubTotalSinDescuento() {
        return subTotalSinDescuento;
    }

    public void setSubTotalSinDescuento(String subTotalSinDescuento) {
        this.subTotalSinDescuento = subTotalSinDescuento;
    }
    
    public String getMontoDescuento() {
        return montoDescuento;
    }

    public void setMontoDescuento(String montoDescuento) {
        this.montoDescuento = montoDescuento;
    }
    
    public String getMotivoDescuento() {
        return motivoDescuento;
    }

    public void setMotivoDescuento(String motivoDescuento) {
        this.motivoDescuento = motivoDescuento;
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
    
    public String getRefId() {
        return refId;
    }

    public void setRefId(String refId) {
        this.refId = refId;
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
