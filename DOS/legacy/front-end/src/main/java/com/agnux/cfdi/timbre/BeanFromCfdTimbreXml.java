/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.agnux.cfdi.timbre;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;
/**
 *
 * @author Noé Martínez
 * gpmarsan@gmail.com
 * 11/diciembre/2012
 * 
 */
public class BeanFromCfdTimbreXml {
    private String cadenaOriginal;
    private Logger log  = Logger.getLogger(BeanFromCfdTimbreXml.class.getName());
    
    private boolean existeExpedidoEn;
    private boolean existenRetenciones;
    private boolean existenTraslados;
    
    private ArrayList<LinkedHashMap<String,String>> listaConceptos = new ArrayList<LinkedHashMap<String,String>>();
    private ArrayList<LinkedHashMap<String,String>> listaRetenciones = new ArrayList<LinkedHashMap<String,String>>();
    private ArrayList<LinkedHashMap<String,String>> listaTraslados = new ArrayList<LinkedHashMap<String,String>>();

    private String totalImpuestosRetenidos;
    private String totalImpuestosTrasladados;
    
    
    private String certificado;
    private String noCertificado;
    private String selloDigital;
    private String tipoDeComprobante;
    private String serie;
    private String folio;
    private String lugarExpedicion;
    private String fecha;
    private String formaDePago;
    private String metodoDePago;
    private String subTotal;
    private String total;
    private String moneda;
    private String tipo_cambio;
    private String numeroCuenta;
    
    private String noAprobacion;
    private String anoAprobacion;
    private String version;
    
    
    
    
    private String condicionesDePago;
    private String descuento;
    private String motivoDescuento;
    
    private String razon_social_emisor;
    private String rfc_emisor;
    private String regimenFiscalEmisor;
    private String calle_domicilio_fiscal;
    private String municipio_domicilio_fiscal;
    private String estado_domicilio_fiscal;
    private String pais_domicilio_fiscal;
    private String codigoPostal_domicilio_fiscal;
    private String colonia_domicilio_fiscal;
    private String noExterior_domicilio_fiscal;
    private String noInterior_domicilio_fiscal;
    private String localidad_domicilio_fiscal;
    private String referencia_domicilio_fiscal;
    private String pais_expedido_en;
    private String calle_expedido_en;
    private String cp_expedido_en;
    private String colonia_expedido_en;
    private String noExterior_expedido_en;
    private String noInterior_expedido_en;
    private String municipio_expedido_en;
    private String referencia_expedido_en;
    private String estado_expedido_en;
    private String localidad_expedido_en;
    private String rfc_receptor;
    private String razon_social_receptor;
    private String pais_domicilio;
    private String calle_domicilio;
    private String municipio_domicilio;
    private String estado_domicilio;
    private String colonia_domicilio;
    private String localidad_domicilio;
    private String referencia_domicilio;
    private String noExterior_domicilio;
    private String noInterior_domicilio;
    private String codigoPostal_domicilio;
    private Boolean addenda;
    private String tipoAddenda;
    private String datosExtras;
    
    


    public BeanFromCfdTimbreXml(byte[] comprobante){
            InputStream is = new ByteArrayInputStream(comprobante);
            leerxml(is);
            //this.setCadenaOriginal(extraerCadenaOriginal(is));
    }

    public BeanFromCfdTimbreXml(String fichero){
        
        String comprobante = new String();

        File file = new File(fichero);
        
        try { BufferedReader in = new BufferedReader(new FileReader(file));
            String str;
            while ((str = in.readLine()) != null) {
                comprobante += str;
            } in.close();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        
        try {
            InputStream is = new ByteArrayInputStream(comprobante.getBytes("UTF-8"));
            leerxml(is);
            //this.setCadenaOriginal(extraerCadenaOriginal(is));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }



    private class ParseaArchivoXmlOfCFD extends DefaultHandler {

            @Override
            public void startElement(String namespaceURI, String localName, String qName, Attributes atts)throws SAXException{

                    if ("cfdi:Comprobante".equals(qName)) {
                        for(int i=0; i < atts.getLength(); i++){
                            String valor = atts.getQName(i);
                            
                            if("certificado".equals(valor)){
                                String certificado = atts.getValue(i);
                                setCertificado(certificado);
                            }
                            if("noCertificado".equals(valor)){
                                String noCertificado = atts.getValue(i);
                                setNoCertificado(noCertificado);
                            }
                            if("sello".equals(valor)){
                                String selloDigital = atts.getValue(i);
                                setSelloDigital(selloDigital);
                            }
                            if("tipoDeComprobante".equals(valor)){
                                String tipoDeComprobante = atts.getValue(i);
                                setTipoDeComprobante(tipoDeComprobante);
                            }
                            if("serie".equals(valor)){
                                String serie = atts.getValue(i);
                                setSerie(serie);
                            }
                            if("folio".equals(valor)){
                                String folio = atts.getValue(i);
                                setFolio(folio);
                            }
                            if("LugarExpedicion".equals(valor)){
                                String lugarExpedicion = atts.getValue(i);
                                setLugarExpedicion(lugarExpedicion);
                            }
                            if("fecha".equals(valor)){
                                String fecha = atts.getValue(i);
                                setFecha(fecha);
                            }
                            if("formaDePago".equals(valor)){
                                String formaDePago = atts.getValue(i);
                                setFormaDePago(formaDePago);
                            }
                            if("metodoDePago".equals(valor)){
                                String metodoDePago = atts.getValue(i);
                                setMetodoDePago(metodoDePago);
                            }
                            if("subTotal".equals(valor)){
                                String subTotal = atts.getValue(i);
                                setSubTotal(subTotal);
                            }

                            if("total".equals(valor)){
                                String total = atts.getValue(i);
                                setTotal(total);
                            }
                            
                            if("Moneda".equals(valor)){
                                String moneda = atts.getValue(i);
                                setMoneda(moneda);
                            }
                            
                            if("TipoCambio".equals(valor)){
                                String tc = atts.getValue(i);
                                setTipo_cambio(tc);
                            }
                            
                            if("NumCtaPago".equals(valor)){
                                String noCuenta = atts.getValue(i);
                                setNumeroCuenta(noCuenta);
                            }
                            
                            
                            
                            
                            //estos datos no estan en la version del xml, los dejo por si es necesario agregar mas adelante
                            if("anoAprobacion".equals(valor)){
                                String anoAprobacion = atts.getValue(i);
                                setAnoAprobacion(anoAprobacion);
                            }
                            if("condicionesDePago".equals(valor)){
                                String condicionesDePago = atts.getValue(i);
                                setCondicionesDePago(condicionesDePago);
                            }
                            if("descuento".equals(valor)){
                                String descuento = atts.getValue(i);
                                setDescuento(descuento);
                            }
                            if("motivoDescuento".equals(valor)){
                                String motivoDescuento = atts.getValue(i);
                                setMotivoDescuento(motivoDescuento);
                            }
                            if("noAprobacion".equals(valor)){
                                String noAprobacion = atts.getValue(i);
                                setNoAprobacion(noAprobacion);
                            }
                            if("version".equals(valor)){
                                String version = atts.getValue(i);
                                setVersion(version);
                            }
                            
                            

                        }
                    }
                    


                    if ("cfdi:Emisor".equals(qName)) {
                        for(int i=0; i < atts.getLength();i++){
                            String valor = atts.getQName(i);
                            if("nombre".equals(valor)){
                                String razon_social_emisor = atts.getValue(i);
                                setRazon_social_emisor(razon_social_emisor);
                            }
                            if("rfc".equals(valor)){
                                String rfc = atts.getValue(i);
                                setRfc_emisor(rfc);
                            }
                        }
                    }

                    if ("cfdi:DomicilioFiscal".equals(qName)) {
                        for(int i=0; i < atts.getLength();i++){
                            String valor = atts.getQName(i);
                            if("calle".equals(valor)){
                                String calle_domicilio_fiscal = atts.getValue(i);
                                setCalle_domicilio_fiscal(calle_domicilio_fiscal);
                            }
                            if("codigoPostal".equals(valor)){
                                String codigoPostal_domicilio_fiscal = atts.getValue(i);
                                setCodigoPostal_domicilio_fiscal(codigoPostal_domicilio_fiscal);
                            }
                            if("colonia".equals(valor)){
                                String colonia_domicilio_fiscal = atts.getValue(i);
                                setColonia_domicilio_fiscal(colonia_domicilio_fiscal);
                            }
                            if("estado".equals(valor)){
                                String estado_domicilio_fiscal = atts.getValue(i);
                                setEstado_domicilio_fiscal(estado_domicilio_fiscal);
                            }
                            if("localidad".equals(valor)){
                                String localidad_domicilio_fiscal = atts.getValue(i);
                                setLocalidad_domicilio_fiscal(localidad_domicilio_fiscal);
                            }
                            if("municipio".equals(valor)){
                                String municipio_domicilio_fiscal = atts.getValue(i);
                                setMunicipio_domicilio_fiscal(municipio_domicilio_fiscal);
                            }
                            if("noExterior".equals(valor)){
                                String noExterior_domicilio_fiscal = atts.getValue(i);
                                setNoExterior_domicilio_fiscal(noExterior_domicilio_fiscal);
                            }
                            if("noInterior".equals(valor)){
                                String noInterior_domicilio_fiscal = atts.getValue(i);
                                setNoInterior_domicilio_fiscal(noInterior_domicilio_fiscal);
                            }
                            if("pais".equals(valor)){
                                String pais_domicilio_fiscal = atts.getValue(i);
                                setPais_domicilio_fiscal(pais_domicilio_fiscal);
                            }
                            if("referencia".equals(valor)){
                                String referencia_domicilio_fiscal = atts.getValue(i);
                                setReferencia_domicilio_fiscal(referencia_domicilio_fiscal);
                            }
                        }
                    }
                    
                    if ("cfdi:ExpedidoEn".equals(qName)) {
                        setExisteExpedidoEn(true);
                        for(int i=0; i < atts.getLength();i++){
                            String valor = atts.getQName(i);
                            if("calle".equals(valor)){
                                String calle_expedido_en = atts.getValue(i);
                                setCalle_expedido_en(calle_expedido_en);
                            }
                            if("codigoPostal".equals(valor)){
                                String cp_expedido_en = atts.getValue(i);
                                setCp_expedido_en(cp_expedido_en);
                            }
                            if("colonia".equals(valor)){
                                String colonia_expedido_en = atts.getValue(i);
                                setColonia_expedido_en(colonia_expedido_en);
                            }
                            if("estado".equals(valor)){
                                String estado_expedido_en = atts.getValue(i);
                                setEstado_expedido_en(estado_expedido_en);
                            }
                            if("localidad".equals(valor)){
                                String localidad_expedido_en = atts.getValue(i);
                                setLocalidad_expedido_en(localidad_expedido_en);
                            }
                            if("municipio".equals(valor)){
                                String municipio_expedido_en = atts.getValue(i);
                                setMunicipio_expedido_en(municipio_expedido_en);
                            }
                            if("noExterior".equals(valor)){
                                String noExterior_expedido_en = atts.getValue(i);
                                setNoExterior_expedido_en(noExterior_expedido_en);
                            }
                            if("noInterior".equals(valor)){
                                String noInterior_expedido_en = atts.getValue(i);
                                setNoInterior_expedido_en(noInterior_expedido_en);
                            }
                            if("pais".equals(valor)){
                                String pais_expedido_en = atts.getValue(i);
                                setPais_expedido_en(pais_expedido_en);
                            }
                            if("referencia".equals(valor)){
                                String referencia_expedido_en = atts.getValue(i);
                                setReferencia_expedido_en(referencia_expedido_en);
                            }
                        }
                    }
                    
                    if ("cfdi:RegimenFiscal".equals(qName)) {
                        for(int i=0; i < atts.getLength();i++){
                            String valor = atts.getQName(i);
                            if("Regimen".equals(valor)){
                                String regimen = atts.getValue(i);
                                setRegimenFiscalEmisor(regimen);
                            }
                        }
                    }
                    
                    if ("cfdi:Receptor".equals(qName)) {
                        for(int i=0; i < atts.getLength();i++){
                            String valor = atts.getQName(i);
                            if("nombre".equals(valor)){
                                String razon_social_receptor = atts.getValue(i);
                                setRazon_social_receptor(razon_social_receptor);
                            }
                            if("rfc".equals(valor)){
                                String rfc = atts.getValue(i);
                                setRfc_receptor(rfc);
                            }
                        }
                    }
                    
                    if ("cfdi:Domicilio".equals(qName)) {
                        for(int i=0; i < atts.getLength();i++){
                            String valor = atts.getQName(i);
                            if("calle".equals(valor)){
                                String calle_domicilio = atts.getValue(i);
                                setCalle_domicilio(calle_domicilio);
                            }
                            if("codigoPostal".equals(valor)){
                                String codigoPostal_domicilio = atts.getValue(i);
                                setCodigoPostal_domicilio(codigoPostal_domicilio);
                            }
                            if("colonia".equals(valor)){
                                String colonia_domicilio = atts.getValue(i);
                                setColonia_domicilio(colonia_domicilio);
                            }
                            if("estado".equals(valor)){
                                String estado_domicilio = atts.getValue(i);
                                setEstado_domicilio(estado_domicilio);
                            }
                            if("localidad".equals(valor)){
                                String localidad_domicilio = atts.getValue(i);
                                setLocalidad_domicilio(localidad_domicilio);
                            }
                            if("municipio".equals(valor)){
                                String municipio_domicilio = atts.getValue(i);
                                setMunicipio_domicilio(municipio_domicilio);
                            }
                            if("noExterior".equals(valor)){
                                String noExterior_domicilio = atts.getValue(i);
                                setNoExterior_domicilio(noExterior_domicilio);
                            }
                            if("noInterior".equals(valor)){
                                String noInterior_domicilio = atts.getValue(i);
                                setNoInterior_domicilio(noInterior_domicilio);
                            }
                            if("pais".equals(valor)){
                                String pais_domicilio = atts.getValue(i);
                                setPais_domicilio(pais_domicilio);
                            }
                            if("referencia".equals(valor)){
                                String referencia_domicilio = atts.getValue(i);
                                setReferencia_domicilio(referencia_domicilio);
                            }
                        }
                    }
                    
                    if ("cfdi:Concepto".equals(qName)) {
                        LinkedHashMap<String,String> articulo = new LinkedHashMap<String,String>();
                        for(int i=0; i < atts.getLength();i++){
                            String valor = atts.getQName(i);
                            if("cantidad".equals(valor)){
                                String cantidad = atts.getValue(i);
                                articulo.put("cantidad", cantidad);
                            }
                            if("descripcion".equals(valor)){
                                String descripcion = atts.getValue(i);
                                articulo.put("descripcion", descripcion);
                            }
                            if("importe".equals(valor)){
                                String importe = atts.getValue(i);
                                articulo.put("importe", importe);
                            }
                            if("noIdentificacion".equals(valor)){
                                String noIdentificacion = atts.getValue(i);
                                articulo.put("noIdentificacion", noIdentificacion);
                            }
                            if("unidad".equals(valor)){
                                String unidad = atts.getValue(i);
                                articulo.put("unidad", unidad);
                            }
                            if("valorUnitario".equals(valor)){
                                String valorUnitario = atts.getValue(i);
                                articulo.put("valorUnitario", valorUnitario);
                            }
                        }
                        ArrayList<LinkedHashMap<String,String>>lista = getListaConceptos();
                        lista.add(articulo);
                        setListaConceptos(lista);
                    }
                    
                    if("cfdi:InformacionAduanera".equals(qName)){
                        LinkedHashMap<String,String> InformacionAduanera = new LinkedHashMap<String,String>();
                        for(int i=0;i<atts.getLength();i++){
                            String valor = atts.getQName(i);
                            if("numero".equals(valor)){
                                String numero = atts.getValue(i);
                                InformacionAduanera.put("numero", numero);
                            }
                            if("fecha".equals(valor)){
                                String fecha = atts.getValue(i);
                                InformacionAduanera.put("fecha", fecha);
                            }
                            if("aduana".equals(valor)){
                                String aduana = atts.getValue(i);
                                InformacionAduanera.put("aduana", aduana);
                            }
                        }
                        ArrayList<LinkedHashMap<String,String>>lista = getListaConceptos();
                        lista.add(InformacionAduanera);
                        setListaConceptos(lista);
                    }
                    
                    
                    if ("cfdi:Impuestos".equals(qName)) {
                        for(int i=0; i < atts.getLength();i++){
                            String valor = atts.getQName(i);
                            if("totalImpuestosRetenidos".equals(valor)){
                                String totalImpuestosRetenidos = atts.getValue(i);
                                setTotalImpuestosRetenidos(totalImpuestosRetenidos);
                            }
                            if("totalImpuestosTrasladados".equals(valor)){
                                String totalImpuestosTrasladados = atts.getValue(i);
                                setTotalImpuestosTrasladados(totalImpuestosTrasladados);
                            }
                        }
                    }

                    if ("cfdi:Retencion".equals(qName)) {
                        setExistenRetenciones(true);
                        LinkedHashMap<String,String> retencion = new LinkedHashMap<String,String>();
                        for(int i=0; i < atts.getLength();i++){
                            String valor = atts.getQName(i);
                            if("importe".equals(valor)){
                                    String importe = atts.getValue(i);
                                    retencion.put("importe",importe);
                            }
                            if("impuesto".equals(valor)){
                                    String impuesto = atts.getValue(i);
                                    retencion.put("impuesto",impuesto);
                            }
                        }
                        ArrayList<LinkedHashMap<String,String>>lista = getListaRetenciones();
                        lista.add(retencion);
                        setListaRetenciones(listaRetenciones);
                    }

                    if ("cfdi:Traslado".equals(qName)) {
                        setExistenTraslados(true);
                        LinkedHashMap<String,String> traslado = new LinkedHashMap<String,String>();
                        for(int i=0; i < atts.getLength();i++){
                            String valor = atts.getQName(i);
                            if("importe".equals(valor)){
                                String importe = atts.getValue(i);
                                traslado.put("importe", importe);
                            }
                            if("impuesto".equals(valor)){
                                String impuesto = atts.getValue(i);
                                traslado.put("impuesto", impuesto);
                            }
                            if("tasa".equals(valor)){
                                String tasa = atts.getValue(i);
                                traslado.put("tasa", tasa);
                            }
                        }
                        ArrayList<LinkedHashMap<String,String>>lista = getListaTraslados();
                        lista.add(traslado);
                        setListaTraslados(listaTraslados);
                    }

                    if ("cfdi:Addenda".equals(qName)) {
                        setAddenda(true);
                        for(int i=0; i < atts.getLength();i++){
                            String valor = atts.getQName(i);
                            if("tipoAddenda".equals(valor)){
                                String tipoAddenda = atts.getValue(i);
                                setTipoAddenda(tipoAddenda);
                            }
                            if("datosExtras".equals(valor)){
                                String datosExtras = atts.getValue(i);
                                setDatosExtras(datosExtras);
                            }
                        }
                    }
                    else{
                            setAddenda(false);
                    }
            }
    }

    private void leerxml(InputStream url){
            try{
                    SAXParserFactory spf = SAXParserFactory.newInstance();
                    SAXParser sp = spf.newSAXParser();
                    sp.parse(url, new ParseaArchivoXmlOfCFD() );
            }
            catch(ParserConfigurationException e){
                    System.err.println("error de  parseo " + e);
            }
            catch(SAXException e2){
                    System.err.println(e2);
                    System.err.println("error de  sax: " + e2.getStackTrace());
            }
            catch (IOException e3) {
                    System.err.println("error de  io: " + e3.getMessage() );
            }
    }

    public String getDatosExtras() {
            return datosExtras;
    }
    public void setDatosExtras(String datosExtras) {
            this.datosExtras = datosExtras;
    }
    public String getTipoAddenda() {
            return tipoAddenda;
    }
    public void setTipoAddenda(String tipoAddenda) {
            this.tipoAddenda = tipoAddenda;
    }
    public Boolean getAddenda() {
            return addenda;
    }
    public void setAddenda(Boolean addenda) {
            this.addenda = addenda;
    }
    public void setCadenaOriginal(String cadenaOriginal) {
            this.cadenaOriginal = cadenaOriginal;
    }
    public String getCadenaOriginal() {
            log.log(Level.INFO,"Cadena Original: " + cadenaOriginal);
            return cadenaOriginal;
    }
    public boolean ExisteExpedidoEn() {
            return existeExpedidoEn;
    }
    public void setExisteExpedidoEn(boolean existeExpedidoEn) {
            this.existeExpedidoEn = existeExpedidoEn;
    }
    public boolean ExistenRetenciones() {
            return existenRetenciones;
    }
    public void setExistenRetenciones(boolean existenRetenciones) {
            this.existenRetenciones = existenRetenciones;
    }
    public boolean ExistenTraslados() {
            return existenTraslados;
    }
    public void setExistenTraslados(boolean existenTraslados) {
            this.existenTraslados = existenTraslados;
    }
    public void setListaRetenciones(ArrayList<LinkedHashMap<String,String>> listaRetenciones) {
            this.listaRetenciones = listaRetenciones;
    }
    public ArrayList<LinkedHashMap<String,String>> getListaRetenciones() {
            return listaRetenciones;
    }
    public void setListaConceptos(ArrayList<LinkedHashMap<String,String>> listaConceptos) {
            this.listaConceptos = listaConceptos;
    }
    public ArrayList<LinkedHashMap<String,String>> getListaConceptos() {
            return listaConceptos;
    }
    public void setListaTraslados(ArrayList<LinkedHashMap<String,String>> listaTraslados) {
            this.listaTraslados = listaTraslados;
    }
    public ArrayList<LinkedHashMap<String,String>> getListaTraslados() {
            return listaTraslados;
    }
    public void setTotalImpuestosRetenidos(String totalImpuestosRetenidos){
            this.totalImpuestosRetenidos = totalImpuestosRetenidos;
    }
    public String getTotalImpuestosRetenidos(){
            return totalImpuestosRetenidos;
    }
    public void setTotalImpuestosTrasladados(String totalImpuestosTrasladados){
            this.totalImpuestosTrasladados = totalImpuestosTrasladados;
    }
    public String getTotalImpuestosTrasladados(){
            return totalImpuestosTrasladados;
    }
    public String getSelloDigital() {
            return selloDigital;
    }
    public void setSelloDigital(String selloDigital) {
            this.selloDigital = selloDigital;
    }
    public String getFolio() {
            return folio;
    }
    public void setFolio(String folio) {
            this.folio = folio;
    }
    public String getFecha() {
            return fecha;
    }
    public void setFecha(String fecha) {
            this.fecha = fecha;
    }
    public String getNoAprobacion() {
            return noAprobacion;
    }
    public void setNoAprobacion(String noAprobacion) {
            this.noAprobacion = noAprobacion;
    }
    public String getAnoAprobacion() {
            return anoAprobacion;
    }
    public void setAnoAprobacion(String anoAprobacion) {
            this.anoAprobacion = anoAprobacion;
    }
    public String getFormaDePago() {
            return formaDePago;
    }
    public void setFormaDePago(String formaDePago) {
            this.formaDePago = formaDePago;
    }
    public String getNoCertificado() {
            return noCertificado;
    }
    public void setNoCertificado(String noCertificado) {
            this.noCertificado = noCertificado;
    }
    public String getVersion() {
            return version;
    }
    public void setVersion(String version) {
            this.version = version;
    }
    public String getTotal() {
            return total;
    }
    public void setTotal(String total) {
            this.total = total;
    }
    public String getSubTotal() {
            return subTotal;
    }
    public void setSubTotal(String subTotal) {
            this.subTotal = subTotal;
    }
    public String getTipoDeComprobante() {
        String valor_retorno = null;
        if(this.tipoDeComprobante.equals("ingreso")){valor_retorno = "I";}
        if(this.tipoDeComprobante.equals("egreso")){valor_retorno = "E";}
        if(this.tipoDeComprobante.equals("traslado")){valor_retorno = "T";}

        return valor_retorno;
    }
    public void setTipoDeComprobante(String tipoDeComprobante) {
            this.tipoDeComprobante = tipoDeComprobante;
    }
    public String getSerie() {
            return serie;
    }
    public void setSerie(String serie) {
            this.serie = serie;
    }
    public String getCertificado() {
            return certificado;
    }
    public void setCertificado(String certificado) {
            this.certificado = certificado;
    }
    public String getCondicionesDePago() {
            return condicionesDePago;
    }
    public void setCondicionesDePago(String condicionesDePago) {
            this.condicionesDePago = condicionesDePago;
    }
    public String getDescuento() {
            return descuento;
    }
    public void setDescuento(String descuento) {
            this.descuento = descuento;
    }
    public String getMotivoDescuento() {
            return motivoDescuento;
    }
    public void setMotivoDescuento(String motivoDescuento) {
            this.motivoDescuento = motivoDescuento;
    }
    public String getMetodoDePago() {
            return metodoDePago;
    }
    public void setMetodoDePago(String metodoDePago) {
            this.metodoDePago = metodoDePago;
    }

    public String getLugarExpedicion() {
        return lugarExpedicion;
    }

    public void setLugarExpedicion(String lugarExpedicion) {
        this.lugarExpedicion = lugarExpedicion;
    }

    public String getNumeroCuenta() {
        return numeroCuenta;
    }

    public void setNumeroCuenta(String numeroCuenta) {
        this.numeroCuenta = numeroCuenta;
    }
    
    public String getRegimenFiscalEmisor() {
        return regimenFiscalEmisor;
    }
    
    public void setRegimenFiscalEmisor(String regimenFiscalEmisor) {
        this.regimenFiscalEmisor = regimenFiscalEmisor;
    }


    public String getRazon_social_emisor() {
            return razon_social_emisor;
    }
    public void setRazon_social_emisor(String razonSocialEmisor) {
            razon_social_emisor = razonSocialEmisor;
    }
    public String getRfc_emisor() {
            return rfc_emisor;
    }
    public void setRfc_emisor(String rfcEmisor) {
            rfc_emisor = rfcEmisor;
    }
    public String getCalle_domicilio_fiscal() {
            return calle_domicilio_fiscal;
    }
    public void setCalle_domicilio_fiscal(String calleDomicilioFiscal) {
            calle_domicilio_fiscal = calleDomicilioFiscal;
    }
    public String getMunicipio_domicilio_fiscal() {
            return municipio_domicilio_fiscal;
    }
    public void setMunicipio_domicilio_fiscal(String municipioDomicilioFiscal) {
            municipio_domicilio_fiscal = municipioDomicilioFiscal;
    }
    public String getEstado_domicilio_fiscal() {
            return estado_domicilio_fiscal;
    }
    public void setEstado_domicilio_fiscal(String estadoDomicilioFiscal) {
            estado_domicilio_fiscal = estadoDomicilioFiscal;
    }
    public String getPais_domicilio_fiscal() {
            return pais_domicilio_fiscal;
    }
    public void setPais_domicilio_fiscal(String paisDomicilioFiscal) {
            pais_domicilio_fiscal = paisDomicilioFiscal;
    }
    public String getCodigoPostal_domicilio_fiscal() {
            return codigoPostal_domicilio_fiscal;
    }
    public void setCodigoPostal_domicilio_fiscal(String codigoPostalDomicilioFiscal) {
            codigoPostal_domicilio_fiscal = codigoPostalDomicilioFiscal;
    }
    public String getColonia_domicilio_fiscal() {
            return colonia_domicilio_fiscal;
    }
    public void setColonia_domicilio_fiscal(String coloniaDomicilioFiscal) {
            colonia_domicilio_fiscal = coloniaDomicilioFiscal;
    }
    public String getNoExterior_domicilio_fiscal() {
            return noExterior_domicilio_fiscal;
    }
    public void setNoExterior_domicilio_fiscal(String noExteriorDomicilioFiscal) {
            noExterior_domicilio_fiscal = noExteriorDomicilioFiscal;
    }
    public String getNoInterior_domicilio_fiscal() {
            return noInterior_domicilio_fiscal;
    }
    public void setNoInterior_domicilio_fiscal(String noInteriorDomicilioFiscal) {
            noInterior_domicilio_fiscal = noInteriorDomicilioFiscal;
    }
    public String getLocalidad_domicilio_fiscal() {
            return localidad_domicilio_fiscal;
    }
    public void setLocalidad_domicilio_fiscal(String localidadDomicilioFiscal) {
            localidad_domicilio_fiscal = localidadDomicilioFiscal;
    }
    public String getReferencia_domicilio_fiscal() {
            return referencia_domicilio_fiscal;
    }
    public void setReferencia_domicilio_fiscal(String referenciaDomicilioFiscal) {
            referencia_domicilio_fiscal = referenciaDomicilioFiscal;
    }
    public String getPais_expedido_en() {
            return pais_expedido_en;
    }
    public void setPais_expedido_en(String paisExpedidoEn) {
            pais_expedido_en = paisExpedidoEn;
    }
    public String getCalle_expedido_en() {
            return calle_expedido_en;
    }
    public void setCalle_expedido_en(String calleExpedidoEn) {
            calle_expedido_en = calleExpedidoEn;
    }
    public String getCp_expedido_en() {
            return cp_expedido_en;
    }
    public void setCp_expedido_en(String cpExpedidoEn) {
            cp_expedido_en = cpExpedidoEn;
    }
    public String getColonia_expedido_en() {
            return colonia_expedido_en;
    }
    public void setColonia_expedido_en(String coloniaExpedidoEn) {
            colonia_expedido_en = coloniaExpedidoEn;
    }
    public String getNoExterior_expedido_en() {
            return noExterior_expedido_en;
    }
    public void setNoExterior_expedido_en(String noExteriorExpedidoEn) {
            noExterior_expedido_en = noExteriorExpedidoEn;
    }
    public String getNoInterior_expedido_en() {
            return noInterior_expedido_en;
    }
    public void setNoInterior_expedido_en(String noInteriorExpedidoEn) {
            noInterior_expedido_en = noInteriorExpedidoEn;
    }
    public String getMunicipio_expedido_en() {
            return municipio_expedido_en;
    }
    public void setMunicipio_expedido_en(String municipioExpedidoEn) {
            municipio_expedido_en = municipioExpedidoEn;
    }
    public String getReferencia_expedido_en() {
            return referencia_expedido_en;
    }
    public void setReferencia_expedido_en(String referenciaExpedidoEn) {
            referencia_expedido_en = referenciaExpedidoEn;
    }
    public String getEstado_expedido_en() {
            return estado_expedido_en;
    }
    public void setEstado_expedido_en(String estadoExpedidoEn) {
            estado_expedido_en = estadoExpedidoEn;
    }
    public String getLocalidad_expedido_en() {
            return localidad_expedido_en;
    }
    public void setLocalidad_expedido_en(String localidadExpedidoEn) {
            localidad_expedido_en = localidadExpedidoEn;
    }
    public String getRfc_receptor() {
            return rfc_receptor;
    }
    public void setRfc_receptor(String rfcReceptor) {
            rfc_receptor = rfcReceptor;
    }
    public String getRazon_social_receptor() {
            return razon_social_receptor;
    }
    public void setRazon_social_receptor(String razonSocialReceptor) {
            razon_social_receptor = razonSocialReceptor;
    }
    public String getPais_domicilio() {
            return pais_domicilio;
    }
    public void setPais_domicilio(String paisDomicilio) {
            pais_domicilio = paisDomicilio;
    }
    public String getCalle_domicilio() {
            return calle_domicilio;
    }
    public void setCalle_domicilio(String calleDomicilio) {
            calle_domicilio = calleDomicilio;
    }
    public String getMunicipio_domicilio() {
            return municipio_domicilio;
    }
    public void setMunicipio_domicilio(String municipioDomicilio) {
            municipio_domicilio = municipioDomicilio;
    }
    public String getEstado_domicilio() {
            return estado_domicilio;
    }
    public void setEstado_domicilio(String estadoDomicilio) {
            estado_domicilio = estadoDomicilio;
    }
    public String getColonia_domicilio() {
            return colonia_domicilio;
    }
    public void setColonia_domicilio(String coloniaDomicilio) {
            colonia_domicilio = coloniaDomicilio;
    }
    public String getLocalidad_domicilio() {
            return localidad_domicilio;
    }
    public void setLocalidad_domicilio(String localidadDomicilio) {
            localidad_domicilio = localidadDomicilio;
    }
    public String getReferencia_domicilio() {
            return referencia_domicilio;
    }
    public void setReferencia_domicilio(String referenciaDomicilio) {
            referencia_domicilio = referenciaDomicilio;
    }
    public String getNoExterior_domicilio() {
            return noExterior_domicilio;
    }
    public void setNoExterior_domicilio(String noExteriorDomicilio) {
            noExterior_domicilio = noExteriorDomicilio;
    }
    public String getNoInterior_domicilio() {
            return noInterior_domicilio;
    }
    public void setNoInterior_domicilio(String noInteriorDomicilio) {
            noInterior_domicilio = noInteriorDomicilio;
    }
    public String getCodigoPostal_domicilio() {
            return codigoPostal_domicilio;
    }
    public void setCodigoPostal_domicilio(String codigoPostalDomicilio) {
            codigoPostal_domicilio = codigoPostalDomicilio;
    }
    
    public String getMoneda() {
        return moneda;
    }

    public void setMoneda(String moneda) {
        this.moneda = moneda;
    }

    public String getTipo_cambio() {
        return tipo_cambio;
    }

    public void setTipo_cambio(String tipo_cambio) {
        this.tipo_cambio = tipo_cambio;
    }
}
