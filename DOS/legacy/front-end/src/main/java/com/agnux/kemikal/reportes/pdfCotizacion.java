package com.agnux.kemikal.reportes;

import com.agnux.common.helpers.StringHelper;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Font;
import com.itextpdf.text.Element;
import com.itextpdf.text.ExceptionConverter;
import com.itextpdf.text.Image;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPageEventHelper;
import com.itextpdf.text.pdf.PdfTemplate;
import com.itextpdf.text.pdf.PdfWriter;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.lang.StringEscapeUtils;


public class pdfCotizacion {
    private HashMap<String, String> datosHeaderFooter = new HashMap<String, String>();
    private ArrayList<HashMap<String, String>> lista_productos = new ArrayList<HashMap<String, String>>();
    private ArrayList<HashMap<String, String>> condiciones_comerciales = new ArrayList<HashMap<String, String>>();
    private ArrayList<HashMap<String, String>> politicas_pago = new ArrayList<HashMap<String, String>>();
    private ArrayList<HashMap<String, String>> incoterms = new ArrayList<HashMap<String, String>>();
    private String ruta_logo;
    private String file_out;
    private String dirImgProd;
    private String incluyeImgDesc;
    
    private String tipoDoc;
    private String tipo;
    private String folio;
    private String fecha;
    private String tipoCambio;
    private String observaciones;
    private String nombreUsuario;
    private String puestoUsuario;
    private String correo_agente;
    private String saludo;
    private String despedida;
    private String diasVigencia;
    private String total;
    private String impuesto;
    private String subtotal;
    private String incluyeIva;
    private String monedaAbr;
    private String idMonGlobal;
    
    private String emisorRazonSocial;
    private String emisorRfc;
    private String emisorCalle;
    private String emisorNumero;
    private String emisorColonia;
    private String emisorMunicipio;
    private String emisorEstado;
    private String emisorPais;
    private String emisorCp;
    private String emisorPaginaWeb;
    private String emisorTelefono;
    
    private String clieRazonSocial;
    private String clieCalle;
    private String clieNumero;
    private String clieColonia;
    private String clieMunicipio;
    private String clieEstado;
    private String cliePais;
    private String clieCp;
    private String clieTel;
    private String clieRfc;
    private String clieContacto;
    
    //-----Ccostructor para hacer seters-------------
    public pdfCotizacion(HashMap<String, String> HeaderFooter, HashMap<String, String> datosEmisor, HashMap<String, String> datos, HashMap<String, String> datosCliente, ArrayList<HashMap<String, String>> lista_productos, ArrayList<HashMap<String, String>> condiciones_comerciales, ArrayList<HashMap<String, String>> politicas_pago, ArrayList<HashMap<String, String>> incoterms) throws URISyntaxException {
        this.setDatosHeaderFooter(HeaderFooter);
        this.setLista_productos(lista_productos);
        this.setCondiciones_comerciales(condiciones_comerciales);
        this.setPoliticas_pago(politicas_pago);
        this.setIncoterms(incoterms);
        this.setRuta_logo(datos.get("ruta_logo"));
        this.setFile_out(datos.get("file_out"));
        this.setDirImgProd(datos.get("dirImagenes"));
        
        this.setTipoDoc(HeaderFooter.get("titulo_reporte"));
        this.setTipo(datos.get("tipo"));
        this.setFolio(datos.get("folio"));
        this.setFecha(datos.get("fecha"));
        this.setTipoCambio(datos.get("tc_usd"));
        this.setObservaciones(datos.get("observaciones"));
        this.setIncluyeImgDesc(datos.get("img_desc"));
        this.setPuestoUsuario(datos.get("puesto_usuario"));
        this.setNombreUsuario(datos.get("nombre_usuario"));
        this.setCorreo_agente(datos.get("correo_agente"));
        this.setSaludo(datos.get("saludo"));
        this.setDespedida(datos.get("despedida"));
        this.setDiasVigencia(datos.get("dias_vigencia"));
        this.setTotal(datos.get("total"));
        this.setImpuesto(datos.get("impuesto"));
        this.setSubtotal(datos.get("subtotal"));
        this.setIncluyeIva(datos.get("incluiyeIvaPdf"));
        this.setMonedaAbr(datos.get("monedaAbr"));
        this.setIdMonGlobal(datos.get("moneda_id"));
        
        this.setEmisorCalle(datosEmisor.get("emp_calle"));
        this.setEmisorColonia(datosEmisor.get("emp_colonia"));
        this.setEmisorCp(datosEmisor.get("emp_cp"));
        this.setEmisorEstado(datosEmisor.get("emp_estado"));
        this.setEmisorMunicipio(datosEmisor.get("emp_municipio"));
        this.setEmisorNumero(datosEmisor.get("emp_no_exterior"));
        this.setEmisorPais(datosEmisor.get("emp_pais"));
        this.setEmisorRazonSocial(datosEmisor.get("emp_razon_social"));
        this.setEmisorRfc(datosEmisor.get("emp_rfc"));
        this.setEmisorPaginaWeb(datosEmisor.get("emp_pagina_web"));
        this.setEmisorTelefono(datosEmisor.get("emp_telefono"));
        
        this.setClieRazonSocial(datosCliente.get("clieRazonSocial"));
        this.setClieCalle(datosCliente.get("clieCalle"));
        this.setClieColonia(datosCliente.get("clieColonia"));
        this.setClieContacto(datosCliente.get("clieContacto"));
        this.setClieCp(datosCliente.get("clieCp"));
        this.setClieEstado(datosCliente.get("clieEstado"));
        this.setClieMunicipio(datosCliente.get("clieMunicipio"));
        this.setClieNumero(datosCliente.get("clieNumero"));
        this.setCliePais(datosCliente.get("cliePais"));
        this.setClieRfc(datosCliente.get("clieRfc"));
        this.setClieTel(datosCliente.get("clieTel"));
    }

    public void ViewPDF() throws URISyntaxException {
        try {
            Font smallsmall = new Font(Font.FontFamily.HELVETICA,5,Font.NORMAL,BaseColor.BLACK);
            Font smallFont = new Font(Font.FontFamily.HELVETICA,7,Font.NORMAL,BaseColor.BLACK);
            Font smallFontBold = new Font(Font.FontFamily.HELVETICA,7,Font.BOLD,BaseColor.BLACK);
            Font smallBoldFont = new Font(Font.FontFamily.HELVETICA, 8, Font.BOLD, BaseColor.WHITE);
            Font smallBoldFontBlack = new Font(Font.FontFamily.HELVETICA, 8, Font.BOLD, BaseColor.BLACK);
            Font largeBoldFont = new Font(Font.FontFamily.HELVETICA,10,Font.BOLD,BaseColor.BLACK);
            
            Document document;
            PdfPTable tablaHeader;
            PdfPTable tablaSaludo;
            PdfPTable tablaDespedida;
            PdfPTable tablaPartidas;
            PdfPCell cell;
            Iterator it;
            String cadena;
            LogoPDF logopdf = new LogoPDF();
            CeldaPDF cepdf = new CeldaPDF();
            ImagenProd imgProd;
            String ruta_img_prod;
            
            HeaderFooter event = new HeaderFooter(this.getDatosHeaderFooter());
            
            if(this.getIncluyeImgDesc().equals("true")){
                document = new Document(PageSize.LETTER.rotate(),-50,-50, 20, 30);
            }else{
                document = new Document(PageSize.LETTER,-50,-50, 20, 30);
            }
            
            document.addCreator("gpmarsan@gmail.com");
            PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(this.getFile_out()));
            writer.setPageEvent(event);
            document.open();
            
            float [] widths = {6,12,6};
            tablaHeader = new PdfPTable(widths);
            tablaHeader.setKeepTogether(false);
            
            //IMAGEN --> logo empresa
            cell = new PdfPCell(logopdf.addContent());
            cell.setBorder(0);
            cell.setRowspan(9);
            cell.setUseDescender(true);
            cell.setVerticalAlignment(Element.ALIGN_TOP);
            tablaHeader.addCell(cell);
            
            //RAZON SOCIAL --> BeanFromCFD (X_emisor)
            cell = new PdfPCell(new Paragraph(StringHelper.capitalizaString(this.getEmisorRazonSocial()),largeBoldFont));
            cell.setBorder(0);
            cell.setUseAscender(true);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            tablaHeader.addCell(cell);
            
            ////////////////////////////////////////////////////////////////////////////////            
            cadena = this.getTipoDoc() + "&" + this.getFolio() + "&" +  this.getFecha()+ "&" +  this.getTipoCambio();
            
            cell = new PdfPCell(cepdf.addContent(cadena));
            cell.setBorder(0);
            //cell.setBorderWidth(1);
            cell.setRowspan(9);
            tablaHeader.addCell(cell);
            
            ////////////////////////////////////////////////////////////////////////////////
            //celda vacia
            cell = new PdfPCell(new Paragraph("DOMICILIO FISCAL", smallBoldFontBlack));
            cell.setBorder(0);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            tablaHeader.addCell(cell);
            
            //DOMICILIO FISCAL --> texto
            cell = new PdfPCell(new Paragraph(
                    StringHelper.capitalizaString(this.getEmisorCalle()) + " " + 
                    StringHelper.capitalizaString(this.getEmisorNumero()) +  "\n" + 
                    StringHelper.capitalizaString(this.getEmisorColonia()) + ", "+
                    StringHelper.capitalizaString(this.getEmisorMunicipio()) + "\n " + 
                    StringHelper.capitalizaString(this.getEmisorEstado())+ ", " + 
                    StringHelper.capitalizaString(this.getEmisorPais()) + ", C.P. " + this.getEmisorCp() +"\n"+
                    "TEL. "+ StringHelper.capitalizaString(this.getEmisorTelefono())+"\n"+
                    "R.F.C.: " + StringHelper.capitalizaString(this.getEmisorRfc())+"\n"+
                    this.getEmisorPaginaWeb(), smallFont));
            cell.setBorder(0);
            cell.setUseAscender(true);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setRowspan(7);
            tablaHeader.addCell(cell);
 
            //tabla donde va los datos del cliente
            float [] wid = {1.5f,1};
            PdfPTable tableHelper = new PdfPTable(wid);
            String etiqueta_tipo="";

            //si el origen de la Orden de entrada pone los datos del proveedor en el PDF en otro caso pone el tipo de Movimiento
            if(this.getTipo().equals("1")){
                etiqueta_tipo="CLIENTE";
            }else{
                etiqueta_tipo="PROSPECTO";
            }
            
            String cadena_datos = StringHelper.capitalizaString(this.getClieRazonSocial())+" \n"+
                    StringHelper.capitalizaString(this.getClieCalle()) +" "+ 
                    this.getClieNumero() + ", " + 
                    StringHelper.capitalizaString(this.getClieColonia())+ ", " + 
                    StringHelper.capitalizaString(this.getClieMunicipio()) + ", " + 
                    StringHelper.capitalizaString(this.getClieEstado()) + ", " + 
                    StringHelper.capitalizaString(this.getCliePais()) + 
                    " \nC.P. " + this.getClieCp() + 
                    "     TEL. "+ this.getClieTel() +  
                    "\nR.F.C.: " + StringHelper.capitalizaString(this.getClieRfc());
            
            //aqui va  la etiqueta CLIENTE ó PROSPECTO dependiendo del tipo de cotizacion
            
            //FILA 1
            cell = new PdfPCell(new Paragraph(etiqueta_tipo,smallBoldFontBlack));
            cell.setBorder(0);
            //cell.setBorderWidth(1);
            cell.setRightIndent(10);
            tableHelper.addCell(cell);
            
            cell = new PdfPCell(new Paragraph("",smallBoldFontBlack));
            cell.setBorder(0);
            //cell.setBorderWidth(1);
            cell.setRightIndent(10);
            tableHelper.addCell(cell);
 
            //FILA 2
            cell = new PdfPCell(new Paragraph(StringHelper.capitalizaString(cadena_datos), smallFont));
            cell.setBorder(0);
            cell.setRowspan(2);
            cell.setRightIndent(10);
            cell.setVerticalAlignment(Element.ALIGN_TOP);
            tableHelper.addCell(cell);
 
            cell = new PdfPCell(new Paragraph("CONTACTO:",smallFont));
            cell.setBorder(0);
            cell.setRightIndent(10);
            tableHelper.addCell(cell);

            //FILA 3
            cell = new PdfPCell(new Paragraph(StringHelper.capitalizaString(this.getClieContacto()), smallFont));
            cell.setBorder(0);
            cell.setRightIndent(10);
            cell.setVerticalAlignment(Element.ALIGN_TOP);
            tableHelper.addCell(cell);
            
            
            cell = new PdfPCell(tableHelper);
            cell.setBorder(0);
            cell.setColspan(3);
            tablaHeader.addCell(cell);
            
            tablaHeader.setSpacingAfter(10f);
            document.add(tablaHeader);
            //AQUI TERMINA LA TABLA HEADER--------------------------------------------------
            
            
            if( !this.getSaludo().equals("") ){
                //tabla el SALUDO
                tablaSaludo = new PdfPTable(1);
                
                cell = new PdfPCell(new Paragraph(this.getSaludo(), smallFont));
                cell.setBorder(0);
                cell.setRightIndent(10);
                cell.setVerticalAlignment(Element.ALIGN_TOP);
                tablaSaludo.addCell(cell);
                
                tablaSaludo.setSpacingAfter(10f);
                document.add(tablaSaludo);
            }
 
            float [] medidas;
            
            //INICIA TABLA PARA LISTADO DE PRODUCTOS
            float [] widths1 = {
                1,//codigo
                2,//nombre producto
                1.6f,//imagen
                3,//descripcion
                1,//unidad
                1.1f,//presentacion
                0.9f,//cantidad
                0.9f,//precio unitario
                1,//importe
                0.8f//moneda
            };

            //INICIA TABLA PARA LISTADO DE PRODUCTOS
            float [] widths2 = {
                1,//codigo
                2,//nombre producto
                1,//unidad
                1.1f,//presentacion
                0.9f,//cantidad
                0.9f,//precio unitario
                1,//importe
                0.8f//moneda
            };
            
            if(this.getIncluyeImgDesc().equals("true")){
                medidas = widths1;
            }else{
                medidas = widths2;
            }
            
            tablaPartidas = new PdfPTable(medidas);
            tablaPartidas.setKeepTogether(false);
            tablaPartidas.setHeaderRows(1);
            
            cell = new PdfPCell(new Paragraph("Código",smallBoldFont));
            cell.setUseAscender(true);
            cell.setHorizontalAlignment(Element.ALIGN_LEFT);
            cell.setUseDescender(true);
            cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            cell.setBackgroundColor(BaseColor.BLACK);
            tablaPartidas.addCell(cell);
            
            cell = new PdfPCell(new Paragraph("Nombre del Producto",smallBoldFont));
            cell.setUseAscender(true);
            cell.setHorizontalAlignment(Element.ALIGN_LEFT);
            cell.setUseDescender(true);
            cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            cell.setBackgroundColor(BaseColor.BLACK);
            tablaPartidas.addCell(cell);
            
            if(this.getIncluyeImgDesc().equals("true")){
                cell = new PdfPCell(new Paragraph("Imagen",smallBoldFont));
                cell.setUseAscender(true);
                cell.setHorizontalAlignment(Element.ALIGN_LEFT);
                cell.setUseDescender(true);
                cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                cell.setBackgroundColor(BaseColor.BLACK);
                tablaPartidas.addCell(cell);
                
                cell = new PdfPCell(new Paragraph("Descripción",smallBoldFont));
                cell.setUseAscender(true);
                cell.setHorizontalAlignment(Element.ALIGN_LEFT);
                cell.setUseDescender(true);
                cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                cell.setBackgroundColor(BaseColor.BLACK);
                tablaPartidas.addCell(cell);
            }
            
            cell = new PdfPCell(new Paragraph("Unidad",smallBoldFont));
            cell.setUseAscender(true);
            cell.setHorizontalAlignment(Element.ALIGN_LEFT);
            cell.setUseDescender(true);
            cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            cell.setBackgroundColor(BaseColor.BLACK);
            tablaPartidas.addCell(cell);
            
            cell = new PdfPCell(new Paragraph("Presentación",smallBoldFont));
            cell.setUseAscender(true);
            cell.setHorizontalAlignment(Element.ALIGN_LEFT);
            cell.setUseDescender(true);
            cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            cell.setBackgroundColor(BaseColor.BLACK);
            tablaPartidas.addCell(cell);
            
            cell = new PdfPCell(new Paragraph("Cantidad",smallBoldFont));
            cell.setUseAscender(true);
            cell.setHorizontalAlignment(Element.ALIGN_LEFT);
            cell.setUseDescender(true);
            cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            cell.setBackgroundColor(BaseColor.BLACK);
            tablaPartidas.addCell(cell);
            
            cell = new PdfPCell(new Paragraph("Precio U.",smallBoldFont));
            cell.setUseAscender(true);
            cell.setHorizontalAlignment(Element.ALIGN_LEFT);
            cell.setUseDescender(true);
            cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            cell.setBackgroundColor(BaseColor.BLACK);
            tablaPartidas.addCell(cell);
            
            cell = new PdfPCell(new Paragraph("Importe",smallBoldFont));
            cell.setUseAscender(true);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setUseDescender(true);
            cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            cell.setBackgroundColor(BaseColor.BLACK);
            tablaPartidas.addCell(cell);
            
            cell = new PdfPCell(new Paragraph("Moneda",smallBoldFont));
            cell.setUseAscender(true);
            cell.setHorizontalAlignment(Element.ALIGN_LEFT);
            cell.setUseDescender(true);
            cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            cell.setBackgroundColor(BaseColor.BLACK);
            tablaPartidas.addCell(cell);
            
            it = this.getLista_productos().iterator();
            while(it.hasNext()){
                HashMap<String,String> map = (HashMap<String,String>)it.next();
                
                cell = new PdfPCell(new Paragraph(esteAtributoSeDejoNulo(map.get("codigo")), smallFont));
                cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                cell.setHorizontalAlignment(Element.ALIGN_LEFT);
                tablaPartidas.addCell(cell);
                
                String producto = StringEscapeUtils.unescapeHtml(map.get("producto"));
                cell = new PdfPCell(new Paragraph(StringHelper.capitalizaString(producto), smallFont));
                cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                cell.setHorizontalAlignment(Element.ALIGN_LEFT);
                tablaPartidas.addCell(cell);
                
                if(this.getIncluyeImgDesc().equals("true")){
                    ruta_img_prod="";
                    
                    if(!map.get("archivo_img").equals("")){
                        ruta_img_prod=this.getDirImgProd()+"/"+map.get("archivo_img");             
                        imgProd = new ImagenProd(ruta_img_prod);
                        cell = new PdfPCell(imgProd.addContent());
                    }else{
                        cell = new PdfPCell(new Paragraph("", smallFont));
                    }
                    cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                    cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                    tablaPartidas.addCell(cell);
                    
                    cell = new PdfPCell(new Paragraph(StringHelper.capitalizaString(esteAtributoSeDejoNulo(map.get("descripcion_larga"))), smallFont));
                    cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                    cell.setHorizontalAlignment(Element.ALIGN_LEFT);
                    tablaPartidas.addCell(cell);
                }
                
                cell = new PdfPCell(new Paragraph(StringHelper.capitalizaString(esteAtributoSeDejoNulo(map.get("unidad"))), smallFont));
                cell.setHorizontalAlignment(Element.ALIGN_LEFT);
                cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                tablaPartidas.addCell(cell);
                
                cell = new PdfPCell(new Paragraph(StringHelper.capitalizaString(esteAtributoSeDejoNulo(map.get("presentacion"))), smallFont));
                cell.setHorizontalAlignment(Element.ALIGN_LEFT);
                cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                tablaPartidas.addCell(cell);
                
                cell = new PdfPCell(new Paragraph(StringHelper.AgregaComas(map.get("cantidad")), smallFont));
                cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                tablaPartidas.addCell(cell);
                
                cell = new PdfPCell(new Paragraph(StringHelper.AgregaComas(map.get("precio_unitario")), smallFont));
                cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                tablaPartidas.addCell(cell);
                
                cell = new PdfPCell(new Paragraph(StringHelper.AgregaComas(map.get("importe")), smallFont));
                cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                tablaPartidas.addCell(cell);
                
                cell = new PdfPCell(new Paragraph(esteAtributoSeDejoNulo(map.get("moneda_abr")), smallFont));
                cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                tablaPartidas.addCell(cell);
                
                /*
                //idMonGlobal=0;
                //tipoCambioGlobal
                Integer idMonPartida=0;
                //Importe en la moneda de la Partida
                Double importePartida=0.0;
                //El importe de la partida en la moneda global de la cotizacion
                Double importePartidaMonCot=0.0;
                Double impuestoPartida = 0.0;
                Double tasaImpuestoPartida = 0.0;
                
                
                if(idMonGlobal == idMonPartida){
                        importePartidaMonCot = parseFloat($importePartida.val()).toFixed(4));
                }else{
                        if(idMonGlobal==1 && idMonPartida!=1){
                                importePartidaMonCot =  parseFloat($importePartida.val()) * parseFloat($tc.val()) );
                        }else{
                                if(idMonGlobal!=1 && idMonPartida==1){
                                        importePartidaMonCot =  parseFloat($importePartida.val()) / parseFloat($tc.val()) );
                                }
                        }
                }
                
                //calcula el impuesto para este producto multiplicando el importe por el valor del iva
                impuestoPartida = importePartidaMonCot * tasaImpuestoPartida;
                
                sumaImportes = sumaImportes + importePartidaMonCot;
                sumaImpuesto = sumaImpuesto + impuestoPartida;
                */
            }
            
            
            if(this.getIncluyeIva().equals("true")){
                //FILA SUBTOTAL
                cell = new PdfPCell(new Paragraph(esteAtributoSeDejoNulo(""), smallFont));
                cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                cell.setHorizontalAlignment(Element.ALIGN_LEFT);
                cell.setColspan(2);
                cell.setBorder(0);
                tablaPartidas.addCell(cell);
                
                if(this.getIncluyeImgDesc().equals("true")){
                    cell = new PdfPCell(new Paragraph(esteAtributoSeDejoNulo(""), smallFont));
                    cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                    cell.setHorizontalAlignment(Element.ALIGN_LEFT);
                    cell.setColspan(2);
                    cell.setBorder(0);
                    tablaPartidas.addCell(cell);
                }
                
                cell = new PdfPCell(new Paragraph(esteAtributoSeDejoNulo(""), smallFont));
                cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                cell.setHorizontalAlignment(Element.ALIGN_LEFT);
                cell.setColspan(3);
                cell.setBorder(0);
                tablaPartidas.addCell(cell);
                
                cell = new PdfPCell(new Paragraph("Subtotal", smallFontBold));
                cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                cell.setBorder(0);
                tablaPartidas.addCell(cell);
                
                cell = new PdfPCell(new Paragraph(StringHelper.AgregaComas(this.getSubtotal()), smallFontBold));
                cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                tablaPartidas.addCell(cell);
                
                cell = new PdfPCell(new Paragraph(esteAtributoSeDejoNulo(this.getMonedaAbr()), smallFontBold));
                cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                tablaPartidas.addCell(cell);
                
                //FILA IVA
                cell = new PdfPCell(new Paragraph(esteAtributoSeDejoNulo(""), smallFont));
                cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                cell.setHorizontalAlignment(Element.ALIGN_LEFT);
                cell.setColspan(2);
                cell.setBorder(0);
                tablaPartidas.addCell(cell);
                
                if(this.getIncluyeImgDesc().equals("true")){
                    cell = new PdfPCell(new Paragraph(esteAtributoSeDejoNulo(""), smallFont));
                    cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                    cell.setHorizontalAlignment(Element.ALIGN_LEFT);
                    cell.setColspan(2);
                    cell.setBorder(0);
                    tablaPartidas.addCell(cell);
                }
                
                cell = new PdfPCell(new Paragraph(esteAtributoSeDejoNulo(""), smallFont));
                cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                cell.setHorizontalAlignment(Element.ALIGN_LEFT);
                cell.setColspan(3);
                cell.setBorder(0);
                tablaPartidas.addCell(cell);
                
                cell = new PdfPCell(new Paragraph("I.V.A.", smallFontBold));
                cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                cell.setBorder(0);
                tablaPartidas.addCell(cell);
                
                cell = new PdfPCell(new Paragraph(StringHelper.AgregaComas(this.getImpuesto()), smallFontBold));
                cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                tablaPartidas.addCell(cell);
                
                cell = new PdfPCell(new Paragraph(esteAtributoSeDejoNulo(this.getMonedaAbr()), smallFontBold));
                cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                tablaPartidas.addCell(cell);

                //FILA TOTAL
                cell = new PdfPCell(new Paragraph(esteAtributoSeDejoNulo(""), smallFont));
                cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                cell.setHorizontalAlignment(Element.ALIGN_LEFT);
                cell.setColspan(2);
                cell.setBorder(0);
                tablaPartidas.addCell(cell);

                if(this.getIncluyeImgDesc().equals("true")){
                    cell = new PdfPCell(new Paragraph(esteAtributoSeDejoNulo(""), smallFont));
                    cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                    cell.setHorizontalAlignment(Element.ALIGN_LEFT);
                    cell.setColspan(2);
                    cell.setBorder(0);
                    tablaPartidas.addCell(cell);
                }
                
                cell = new PdfPCell(new Paragraph(esteAtributoSeDejoNulo(""), smallFont));
                cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                cell.setHorizontalAlignment(Element.ALIGN_LEFT);
                cell.setColspan(3);
                cell.setBorder(0);
                tablaPartidas.addCell(cell);
                
                cell = new PdfPCell(new Paragraph("Total", smallFontBold));
                cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                cell.setBorder(0);
                tablaPartidas.addCell(cell);
                
                cell = new PdfPCell(new Paragraph(StringHelper.AgregaComas(this.getTotal()), smallFontBold));
                cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                tablaPartidas.addCell(cell);

                cell = new PdfPCell(new Paragraph(esteAtributoSeDejoNulo(this.getMonedaAbr()), smallFontBold));
                cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                tablaPartidas.addCell(cell);
            }
            
            tablaPartidas.setSpacingAfter(10f);
            document.add(tablaPartidas);
            //TERMINA TABLA PARA EL LISTADO DE PRODUCTOS---------------------------
            
            
            if (!this.getObservaciones().equals("")){
                //tabla para las observaciones
                PdfPTable tableObser = new PdfPTable(1);
                /*
                cell = new PdfPCell(new Paragraph("OBSERVACIONES", smallBoldFontBlack));
                cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                cell.setHorizontalAlignment(Element.ALIGN_LEFT);
                cell.setBorder(0);
                tableObser.addCell(cell);
                */
                cell = new PdfPCell(new Paragraph(esteAtributoSeDejoNulo(this.getObservaciones()), smallFont));
                cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                cell.setHorizontalAlignment(Element.ALIGN_LEFT);
                cell.setBorder(0);
                tableObser.addCell(cell);
                
                tableObser.setSpacingAfter(15f);
                document.add(tableObser);
            }
            
            
            
            if (this.getPoliticas_pago().size() > 0){                
                int contSeleccionados=0;
                Iterator it4;
                it4 = this.getIncoterms().iterator();
                while(it4.hasNext()){
                    HashMap<String,String> map = (HashMap<String,String>)it4.next();
                    if(map.get("mostrar_pdf").equals("true")){
                        contSeleccionados++;
                    }
                }
                
                //mostrar solo cuando exista seleccionados
                if(contSeleccionados>0){
                    //tabla para los incoterms
                    PdfPTable tableIncoterms = new PdfPTable(1);
                    
                    cell = new PdfPCell(new Paragraph("INCOTERMS\n", smallBoldFontBlack));
                    cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                    cell.setHorizontalAlignment(Element.ALIGN_LEFT);
                    cell.setBorder(0);
                    cell.setColspan(2);
                    tableIncoterms.addCell(cell);
                    
                    Iterator it3;
                    it3 = this.getIncoterms().iterator();
                    while(it3.hasNext()){
                        HashMap<String,String> map = (HashMap<String,String>)it3.next();

                        if(map.get("mostrar_pdf").equals("true")){
                            String condicion = esteAtributoSeDejoNulo(map.get("titulo"));
                            cell = new PdfPCell(new Paragraph(condicion, smallFont));
                            cell.setVerticalAlignment(Element.ALIGN_TOP);
                            cell.setHorizontalAlignment(Element.ALIGN_LEFT);
                            cell.setBorder(0);
                            tableIncoterms.addCell(cell);
                        }
                    }
                    tableIncoterms.setSpacingAfter(10f);
                    document.add(tableIncoterms);
                }
            }
            
            
            
            float [] widths3 = {0.2f,10};
            if (this.getCondiciones_comerciales().size() > 0){
                //tabla para las observaciones
                PdfPTable tableCondicionesComerciales = new PdfPTable(widths3);
                int incluyeNota=0;
                
                Iterator it2;
                it2 = this.getCondiciones_comerciales().iterator();
                while(it2.hasNext()){
                    HashMap<String,String> map = (HashMap<String,String>)it2.next();
                    
                    String condicion = esteAtributoSeDejoNulo(map.get("descripcion"));
                    
                    if(condicion.split(":")[0].trim().toUpperCase().equals("NOTA")){
                        condicion += "";
                        cell = new PdfPCell(new Paragraph(condicion, smallFont));
                        cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                        cell.setHorizontalAlignment(Element.ALIGN_LEFT);
                        cell.setBorder(0);
                        cell.setColspan(2);
                        tableCondicionesComerciales.addCell(cell);
                        
                        if(Integer.parseInt(this.getDiasVigencia())>0){
                            if(incluyeNota==0){
                                cell = new PdfPCell(new Paragraph(".", smallBoldFontBlack));
                                cell.setVerticalAlignment(Element.ALIGN_TOP);
                                cell.setHorizontalAlignment(Element.ALIGN_LEFT);
                                cell.setBorder(0);
                                tableCondicionesComerciales.addCell(cell);
                                
                                cell = new PdfPCell(new Paragraph("LA COTIZACIÓN TIENE UNA VIGENCIA DE "+this.getDiasVigencia()+" DIAS CONTADOS A PARTIR DE LA FECHA DE EXPEDICIÓN.", smallFont));
                                cell.setVerticalAlignment(Element.ALIGN_TOP);
                                cell.setHorizontalAlignment(Element.ALIGN_LEFT);
                                cell.setBorder(0);
                                tableCondicionesComerciales.addCell(cell);
                                
                                incluyeNota++;
                            }
                        }
                        
                    }else{
                        
                        if(Integer.parseInt(this.getDiasVigencia())>0){
                            if(incluyeNota==0){
                                cell = new PdfPCell(new Paragraph(".", smallBoldFontBlack));
                                cell.setVerticalAlignment(Element.ALIGN_TOP);
                                cell.setHorizontalAlignment(Element.ALIGN_LEFT);
                                cell.setBorder(0);
                                tableCondicionesComerciales.addCell(cell);

                                cell = new PdfPCell(new Paragraph("LA COTIZACIóN TIENE UNA VIGENCIA DE "+this.getDiasVigencia()+" DIAS CONTADOS A PARTIR DE LA FECHA DE EXPEDICIÓN.", smallFont));
                                cell.setVerticalAlignment(Element.ALIGN_TOP);
                                cell.setHorizontalAlignment(Element.ALIGN_LEFT);
                                cell.setBorder(0);
                                tableCondicionesComerciales.addCell(cell);
                                
                                incluyeNota++;
                            }
                        }
                        
                        cell = new PdfPCell(new Paragraph(".", smallBoldFontBlack));
                        cell.setVerticalAlignment(Element.ALIGN_TOP);
                        cell.setHorizontalAlignment(Element.ALIGN_LEFT);
                        cell.setBorder(0);
                        tableCondicionesComerciales.addCell(cell);
                        
                        cell = new PdfPCell(new Paragraph(condicion, smallFont));
                        cell.setVerticalAlignment(Element.ALIGN_TOP);
                        cell.setHorizontalAlignment(Element.ALIGN_LEFT);
                        cell.setBorder(0);
                        tableCondicionesComerciales.addCell(cell);
                    }
                    
                    
                    
                    
                }
                tableCondicionesComerciales.setSpacingAfter(10f);
                document.add(tableCondicionesComerciales);
            }
            
            if (this.getPoliticas_pago().size() > 0){
                //tabla para las observaciones
                PdfPTable tablePoliticasPago = new PdfPTable(widths3);
                
                cell = new PdfPCell(new Paragraph("POLITICAS DE PAGO\n", smallBoldFontBlack));
                cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                cell.setHorizontalAlignment(Element.ALIGN_LEFT);
                cell.setBorder(0);
                cell.setColspan(2);
                tablePoliticasPago.addCell(cell);
                
                Iterator it3;
                it3 = this.getPoliticas_pago().iterator();
                while(it3.hasNext()){
                    HashMap<String,String> map = (HashMap<String,String>)it3.next();
                    
                    cell = new PdfPCell(new Paragraph(".", smallBoldFontBlack));
                    cell.setVerticalAlignment(Element.ALIGN_TOP);
                    cell.setHorizontalAlignment(Element.ALIGN_LEFT);
                    cell.setBorder(0);
                    tablePoliticasPago.addCell(cell);
                    
                    String condicion = esteAtributoSeDejoNulo(map.get("descripcion"));
                    cell = new PdfPCell(new Paragraph(condicion, smallFont));
                    cell.setVerticalAlignment(Element.ALIGN_TOP);
                    cell.setHorizontalAlignment(Element.ALIGN_LEFT);
                    cell.setBorder(0);
                    tablePoliticasPago.addCell(cell);
                }
                tablePoliticasPago.setSpacingAfter(10f);
                document.add(tablePoliticasPago);
            }
            
            
            
            
            if( !this.getDespedida().equals("") ){
                //tabla el DESPEDIDA
                tablaDespedida = new PdfPTable(1);
                
                cell = new PdfPCell(new Paragraph(this.getDespedida(), smallFont));
                cell.setBorder(0);
                cell.setRightIndent(10);
                cell.setVerticalAlignment(Element.ALIGN_TOP);
                tablaDespedida.addCell(cell);
                
                document.add(tablaDespedida);
            }
            
            //tabla para datos de quien lo Elaboró
            PdfPTable tableElaboro = new PdfPTable(1);
            //agregar fila vacia
            cell = new PdfPCell(new Paragraph("", smallFont));
            cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            cell.setHorizontalAlignment(Element.ALIGN_LEFT);
            cell.setBorder(0);
            cell.setFixedHeight(30);
            tableElaboro.addCell(cell);
            
            /*
            cell = new PdfPCell(new Paragraph("Elaborado por:", smallFont));
            cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            cell.setHorizontalAlignment(Element.ALIGN_LEFT);
            cell.setBorder(0);
            tableElaboro.addCell(cell);
            */
            
            cell = new PdfPCell(new Paragraph(esteAtributoSeDejoNulo(this.getNombreUsuario()), smallFontBold));
            cell.setVerticalAlignment(Element.ALIGN_BOTTOM);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setBorder(0);
            tableElaboro.addCell(cell);
            
            cell = new PdfPCell(new Paragraph(esteAtributoSeDejoNulo(this.getPuestoUsuario()), smallFontBold));
            cell.setVerticalAlignment(Element.ALIGN_TOP);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setBorder(0);
            tableElaboro.addCell(cell);
            
            cell = new PdfPCell(new Paragraph(esteAtributoSeDejoNulo(this.getCorreo_agente()), smallFont));
            cell.setVerticalAlignment(Element.ALIGN_TOP);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setBorder(0);
            tableElaboro.addCell(cell);
            
            document.add(tableElaboro);
            
            document.close();
                
        } catch (FileNotFoundException ex) {
            Logger.getLogger(pdfCotizacion.class.getName()).log(Level.SEVERE, null, ex);
        } catch (DocumentException ex) {
            Logger.getLogger(pdfCotizacion.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//termina ViewPDF

        
    public String esteAtributoSeDejoNulo(String atributo){
         return (atributo != null) ? (atributo) : new String();
    }
    
    
    private class LogoPDF {
        public Image addContent() {
            Image img = null;
            try {
                img = Image.getInstance(getRuta_logo());
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
    
    
    private class ImagenProd {
        String Ruta;

        public String getRuta() {
            return Ruta;
        }

        public void setRuta(String Ruta) {
            this.Ruta = Ruta;
        }
        public ImagenProd(String ruta) {
            this.setRuta(ruta);
        }
        public Image addContent() {
            Image img = null;
            try {
                img = Image.getInstance(this.getRuta());
                //img.scaleAbsoluteHeight(100);
                img.scaleAbsoluteHeight(75);
                img.scaleAbsoluteWidth(85);
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
            //System.out.println("Esta es la cadena: "+cadena);
            //[0]   getTipoDoc
            //[1]   getFolio
            //[2]   getFecha
            //[3]   Tipo de cambio

            cell = new PdfPCell(new Paragraph(temp[0],largeBoldFont));
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
            
            cell = new PdfPCell(new Paragraph(temp[1],sont));
            //cell = new PdfPCell(new Paragraph("folio orden entrad",smallFont));
            cell.setUseAscender(true);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setUseDescender(true);
            cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            table.addCell(cell);
            
            /*
            //celda vacia
            cell = new PdfPCell(new Paragraph(" ", smallFont));
            cell.setBorder(1);
            table.addCell(cell);
            */
            cell = new PdfPCell(new Paragraph("FECHA EXPEDICION",smallBoldFont));
            cell.setUseAscender(true);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setUseDescender(true);
            cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            cell.setBackgroundColor(BaseColor.BLACK);
            table.addCell(cell);
            
            
            cell = new PdfPCell(new Paragraph(temp[2].split("-")[2]+"/"+temp[2].split("-")[1]+"/"+temp[2].split("-")[0],smallFont));
            //cell = new PdfPCell(new Paragraph("2012-12-12",smallFont));
            cell.setUseAscender(true);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setUseDescender(true);
            cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            table.addCell(cell);
            
            
            cell = new PdfPCell(new Paragraph("TIPO DE CAMBIO",smallBoldFont));
            cell.setUseAscender(true);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setUseDescender(true);
            cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            cell.setBackgroundColor(BaseColor.BLACK);
            table.addCell(cell);
            
            
            cell = new PdfPCell(new Paragraph(temp[3],smallFont));
            //cell = new PdfPCell(new Paragraph("2012-12-12",smallFont));
            cell.setUseAscender(true);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setUseDescender(true);
            cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            table.addCell(cell);
            
            
            return table;
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
     
     
    public ArrayList<HashMap<String, String>> getCondiciones_comerciales() {
        return condiciones_comerciales;
    }

    public void setCondiciones_comerciales(ArrayList<HashMap<String, String>> condiciones_comerciales) {
        this.condiciones_comerciales = condiciones_comerciales;
    }

    public ArrayList<HashMap<String, String>> getPoliticas_pago() {
        return politicas_pago;
    }

    public void setPoliticas_pago(ArrayList<HashMap<String, String>> politicas_pago) {
        this.politicas_pago = politicas_pago;
    }
    
    public ArrayList<HashMap<String, String>> getIncoterms() {
        return incoterms;
    }

    public void setIncoterms(ArrayList<HashMap<String, String>> incoterms) {
        this.incoterms = incoterms;
    }
    
    public String getClieCalle() {
        return clieCalle;
    }

    public void setClieCalle(String clieCalle) {
        this.clieCalle = clieCalle;
    }

    public String getClieColonia() {
        return clieColonia;
    }

    public void setClieColonia(String clieColonia) {
        this.clieColonia = clieColonia;
    }

    public String getClieContacto() {
        return clieContacto;
    }

    public void setClieContacto(String clieContacto) {
        this.clieContacto = clieContacto;
    }

    public String getClieCp() {
        return clieCp;
    }

    public void setClieCp(String clieCp) {
        this.clieCp = clieCp;
    }

    public String getClieEstado() {
        return clieEstado;
    }

    public void setClieEstado(String clieEstado) {
        this.clieEstado = clieEstado;
    }

    public String getClieMunicipio() {
        return clieMunicipio;
    }

    public void setClieMunicipio(String clieMunicipio) {
        this.clieMunicipio = clieMunicipio;
    }

    public String getClieNumero() {
        return clieNumero;
    }

    public void setClieNumero(String clieNumero) {
        this.clieNumero = clieNumero;
    }

    public String getCliePais() {
        return cliePais;
    }

    public void setCliePais(String cliePais) {
        this.cliePais = cliePais;
    }

    public String getClieRfc() {
        return clieRfc;
    }

    public void setClieRfc(String clieRfc) {
        this.clieRfc = clieRfc;
    }

    public String getClieTel() {
        return clieTel;
    }

    public void setClieTel(String clieTel) {
        this.clieTel = clieTel;
    }

    public HashMap<String, String> getDatosHeaderFooter() {
        return datosHeaderFooter;
    }

    public void setDatosHeaderFooter(HashMap<String, String> datosHeaderFooter) {
        this.datosHeaderFooter = datosHeaderFooter;
    }

    public String getDirImgProd() {
        return dirImgProd;
    }

    public void setDirImgProd(String dirImgProd) {
        this.dirImgProd = dirImgProd;
    }

    public String getEmisorCalle() {
        return emisorCalle;
    }

    public void setEmisorCalle(String emisorCalle) {
        this.emisorCalle = emisorCalle;
    }

    public String getEmisorColonia() {
        return emisorColonia;
    }

    public void setEmisorColonia(String emisorColonia) {
        this.emisorColonia = emisorColonia;
    }

    public String getEmisorCp() {
        return emisorCp;
    }

    public void setEmisorCp(String emisorCp) {
        this.emisorCp = emisorCp;
    }

    public String getEmisorEstado() {
        return emisorEstado;
    }

    public void setEmisorEstado(String emisorEstado) {
        this.emisorEstado = emisorEstado;
    }

    public String getEmisorMunicipio() {
        return emisorMunicipio;
    }

    public void setEmisorMunicipio(String emisorMunicipio) {
        this.emisorMunicipio = emisorMunicipio;
    }

    public String getEmisorNumero() {
        return emisorNumero;
    }

    public void setEmisorNumero(String emisorNumero) {
        this.emisorNumero = emisorNumero;
    }

    public String getEmisorPais() {
        return emisorPais;
    }

    public void setEmisorPais(String emisorPais) {
        this.emisorPais = emisorPais;
    }

    public String getFile_out() {
        return file_out;
    }

    public void setFile_out(String file_out) {
        this.file_out = file_out;
    }

    public String getFolio() {
        return folio;
    }

    public void setFolio(String folio) {
        this.folio = folio;
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

    public String getRuta_logo() {
        return ruta_logo;
    }

    public void setRuta_logo(String ruta_logo) {
        this.ruta_logo = ruta_logo;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public String getTipoCambio() {
        return tipoCambio;
    }

    public void setTipoCambio(String tipoCambio) {
        this.tipoCambio = tipoCambio;
    }
    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    public String getIncluyeImgDesc() {
        return incluyeImgDesc;
    }

    public void setIncluyeImgDesc(String incluyeImgDesc) {
        this.incluyeImgDesc = incluyeImgDesc;
    }
    
    public String getEmisorRazonSocial() {
        return emisorRazonSocial;
    }

    public void setEmisorRazonSocial(String emisorRazonSocial) {
        this.emisorRazonSocial = emisorRazonSocial;
    }
    
    public String getEmisorRfc() {
        return emisorRfc;
    }

    public void setEmisorRfc(String emisorRfc) {
        this.emisorRfc = emisorRfc;
    }
    
    public String getClieRazonSocial() {
        return clieRazonSocial;
    }

    public void setClieRazonSocial(String clieRazonSocial) {
        this.clieRazonSocial = clieRazonSocial;
    }
    public String getTipoDoc() {
        return tipoDoc;
    }

    public void setTipoDoc(String tipoDoc) {
        this.tipoDoc = tipoDoc;
    }   

    public String getNombreUsuario() {
        return nombreUsuario;
    }

    public void setNombreUsuario(String nombreUsuario) {
        this.nombreUsuario = nombreUsuario;
    }

    public String getPuestoUsuario() {
        return puestoUsuario;
    }

    public void setPuestoUsuario(String puestoUsuario) {
        this.puestoUsuario = puestoUsuario;
    }
    
    public String getEmisorPaginaWeb() {
        return emisorPaginaWeb;
    }

    public void setEmisorPaginaWeb(String emisorPaginaWeb) {
        this.emisorPaginaWeb = emisorPaginaWeb;
    }

    public String getEmisorTelefono() {
        return emisorTelefono;
    }

    public void setEmisorTelefono(String emisorTelefono) {
        this.emisorTelefono = emisorTelefono;
    }
    

    public String getDespedida() {
        return despedida;
    }

    public void setDespedida(String despedida) {
        this.despedida = despedida;
    }

    public String getSaludo() {
        return saludo;
    }

    public void setSaludo(String saludo) {
        this.saludo = saludo;
    }
    
    public String getCorreo_agente() {
        return correo_agente;
    }

    public void setCorreo_agente(String correo_agente) {
        this.correo_agente = correo_agente;
    }
    
    public String getDiasVigencia() {
        return diasVigencia;
    }

    public void setDiasVigencia(String diasVigencia) {
        this.diasVigencia = diasVigencia;
    }

    public String getImpuesto() {
        return impuesto;
    }

    public void setImpuesto(String impuesto) {
        this.impuesto = impuesto;
    }

    public String getIncluyeIva() {
        return incluyeIva;
    }

    public void setIncluyeIva(String incluyeIva) {
        this.incluyeIva = incluyeIva;
    }

    public String getSubtotal() {
        return subtotal;
    }

    public void setSubtotal(String subtotal) {
        this.subtotal = subtotal;
    }

    public String getTotal() {
        return total;
    }

    public void setTotal(String total) {
        this.total = total;
    }
    
    public String getMonedaAbr() {
        return monedaAbr;
    }

    public void setMonedaAbr(String monedaAbr) {
        this.monedaAbr = monedaAbr;
    }
    
    public String getIdMonGlobal() {
        return idMonGlobal;
    }

    public void setIdMonGlobal(String idMonGlobal) {
        this.idMonGlobal = idMonGlobal;
    }
}
