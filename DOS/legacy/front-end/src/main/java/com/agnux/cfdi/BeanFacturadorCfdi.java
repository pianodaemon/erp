/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.agnux.cfdi;

import com.agnux.common.helpers.FileHelper;
import com.agnux.common.helpers.StringHelper;
import com.agnux.kemikal.interfacedaos.GralInterfaceDao;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

/**
 *
 * @author pianodaemon
 */
public class BeanFacturadorCfdi {


    
    @Autowired
    @Qualifier("daoGral")
    private GralInterfaceDao gralDao;
    
    
    public static enum Proposito {
        FACTURA, NOTA_CREDITO
    };
    
    private String proposito;
    private String tipoDocumento;
    private String lugar_expedicion;
    private String metodoDePago;
    private String numero_cuenta;
    private String subTotalConceptos;
    private String totalConceptos;
    private String condicionesDePago;
    private String formaDePago;
    private String montoTotal;
    private String montoTotalTexto;
    private String serie;
    private String folio;
    private String ordenCompra;
    private String claveAgente;
    private String observaciones;
    private String nombreMoneda;
    private String tipoCambio;
    
    private String rfc_emisor;
    private String regimen_fiscal_emisor;
    
    private String no_cliente;
    private String rfc_receptor;
    private String nombre_receptor;
    private String colonia_domicilio_receptor;
    private String codigoPostal_domicilio_receptor;
    private String calle_domicilio_receptor;
    private String noExterior_domicilio_receptor;
    private String noInterior_domicilio_receptor;
    private String municipio_domicilio_receptor;
    private String estado_domicilio_receptor;
    private String pais_domicilio_receptor;
    
    private ArrayList<LinkedHashMap<String, String>> listaConceptos;
    private ArrayList<LinkedHashMap<String, String>> listaTraslados;
    private ArrayList<LinkedHashMap<String, String>> listaRetenidos;
    private ArrayList<String> leyendas;
    
    
    
    
    
    public void init(HashMap<String, String> data, ArrayList<LinkedHashMap<String, String>> conceptos, ArrayList<LinkedHashMap<String, String>> impuestos_retenidos, ArrayList<LinkedHashMap<String, String>> impuestos_trasladados, ArrayList<String> leyendas,  String propos, LinkedHashMap<String,String> extras, Integer id_empresa, Integer id_sucursal) {
        
        this.setProposito(propos);
        
        //Datos Base del CFDi ------- INICIO -----------------------------------
        
        switch (Proposito.valueOf(this.getProposito())) {
            
            case FACTURA:
                
                this.setTipoDocumento("Factura");
                
                break;
                
            case NOTA_CREDITO:
                
                this.setTipoDocumento("Nota de Credito");
                
                break;
                
        }
        this.setSerie(StringHelper.normalizaString(StringHelper.remueve_tildes(extras.get("serie")).replace("'", "")));
        this.setFolio(extras.get("folio"));
        this.setSubTotalConceptos(extras.get("subtotal_conceptos"));
        this.setTotalConceptos(extras.get("subtotal_conceptos"));
        this.setMontoTotal(extras.get("monto_total"));
        this.setMontoTotalTexto(StringHelper.normalizaString(StringHelper.remueve_tildes(extras.get("monto_total_texto")).replace("'", "")));
        
        this.setCondicionesDePago(StringHelper.normalizaString(StringHelper.remueve_tildes(data.get("comprobante_attr_condicionesdepago")).replace("'", "")));
        this.setFormaDePago(StringHelper.normalizaString(StringHelper.remueve_tildes(data.get("comprobante_attr_formadepago"))));
        
        this.setMetodoDePago(StringHelper.normalizaString(StringHelper.remueve_tildes(data.get("comprobante_attr_metododepago")).replace("'", "")));
        this.setLugar_expedicion(StringHelper.normalizaString(StringHelper.remueve_tildes( this.getGralDao().getMunicipioSucursalEmisora(id_sucursal).toUpperCase()+", "+this.getGralDao().getEstadoSucursalEmisora(id_sucursal).toUpperCase() ).replace("'", "")));
        this.setNumero_cuenta(data.get("comprobante_attr_numerocuenta"));
        
        this.setOrdenCompra(StringHelper.normalizaString(StringHelper.remueve_tildes(extras.get("orden_compra")).replace("'", "")));
        this.setClaveAgente(extras.get("clave_agente"));
        this.setObservaciones(StringHelper.normalizaString(StringHelper.remueve_tildes(extras.get("observaciones")).replace("'", "")));
        this.setNombreMoneda(StringHelper.normalizaString(StringHelper.remueve_tildes(extras.get("nombre_moneda")).replace("'", "")));
        this.setTipoCambio(extras.get("tipo_cambio"));
        
        //Datos Base del CFDi ------- FIN -----------------------------------
        
        
        //Datos del Emisor ------- INICIO ------------------------------------- 
        
        this.setRfc_emisor(StringHelper.normalizaString(StringHelper.remueve_tildes(this.getGralDao().getRfcEmpresaEmisora(id_empresa))));
        this.setRegimen_fiscal_emisor(StringHelper.normalizaString(StringHelper.remueve_tildes(this.getGralDao().getRegimenFiscalEmpresaEmisora(id_empresa))));
        
        //Datos del Emisor ------- FIN -------------------------------------
        
        
        //Datos del Receptor ------- INICIO ----------------------------------- 
        
        this.setNo_cliente(StringHelper.remueve_tildes(StringHelper.normalizaString(data.get("numero_control"))));
        this.setNombre_receptor(StringHelper.remueve_tildes(StringHelper.normalizaString(data.get("comprobante_receptor_attr_nombre"))));        
        this.setRfc_receptor(StringHelper.remueve_tildes(data.get("comprobante_receptor_attr_rfc")));
        this.setMunicipio_domicilio_receptor(StringHelper.normalizaString(StringHelper.remueve_tildes(data.get("comprobante_receptor_domicilio_attr_municipio")).replace("'", "")));
        this.setEstado_domicilio_receptor(StringHelper.normalizaString(StringHelper.remueve_tildes(data.get("comprobante_receptor_domicilio_attr_estado")).replace("'", "")));
        this.setPais_domicilio_receptor(StringHelper.normalizaString(StringHelper.remueve_tildes(data.get("comprobante_receptor_domicilio_attr_pais")).replace("'", "")));
        this.setColonia_domicilio_receptor(StringHelper.normalizaString(StringHelper.remueve_tildes(data.get("comprobante_receptor_domicilio_attr_colonia")).replace("'", "")));
        this.setCalle_domicilio_receptor(StringHelper.normalizaString(StringHelper.remueve_tildes(data.get("comprobante_receptor_domicilio_attr_calle")).replace("'", "")));
        this.setNoExterior_domicilio_receptor(data.get("comprobante_receptor_domicilio_attr_noexterior"));
        this.setNoInterior_domicilio_receptor(data.get("comprobante_receptor_domicilio_attr_nointerior"));
        this.setCodigoPostal_domicilio_receptor(data.get("comprobante_receptor_domicilio_attr_codigopostal"));
        
        //Datos del Receptor ------- FIN -------------------------------------- 
        
        this.setListaRetenidos(impuestos_retenidos);
        this.setListaTraslados(impuestos_trasladados);
        this.setLeyendas(leyendas);
        this.setListaConceptos(conceptos);
    }
    
    
    public void start() {
        
        String strOutput4File = new String();
        
        StringMakerForCfdiRequest sm = new StringMakerForCfdiRequest();
        
        strOutput4File += sm.createHeader();
        strOutput4File += sm.createDatosGenerales(this.getSerie(), this.getFolio(), Boolean.FALSE);
        strOutput4File += sm.createDatosEmisor(this.getRegimen_fiscal_emisor(), this.getRfc_emisor());
        strOutput4File += sm.createDatosReceptor(this.getRfc_receptor(),this.getNombre_receptor(), this.getCalle_domicilio_receptor(), this.getNoExterior_domicilio_receptor(), this.getNoInterior_domicilio_receptor(), this.getColonia_domicilio_receptor(), this.getMunicipio_domicilio_receptor(), this.getEstado_domicilio_receptor(), this.getPais_domicilio_receptor(), this.getCodigoPostal_domicilio_receptor(), this.getNo_cliente());
        
        strOutput4File += sm.createDatosConceptos(this.getListaConceptos());
        
        strOutput4File += sm.createDatosComplementariosComprobanteNivelGlobal(this.getSubTotalConceptos(), this.getTotalConceptos(), this.getFormaDePago() ,this.getCondicionesDePago(), this.getMetodoDePago(), this.getNumero_cuenta(), this.getLugar_expedicion());
        
        strOutput4File += sm.createDatosComercialesComprobanteNivelGlobal(this.getTipoDocumento(), this.getOrdenCompra(), this.getClaveAgente(), this.getNombreMoneda(), this.getTipoCambio(), this.getObservaciones());
        
        strOutput4File += sm.createImpuestosTrasladados(this.getListaTraslados());
        
        strOutput4File += sm.createImpuestosRetenidos(this.getListaRetenidos());
        
        strOutput4File += sm.createDatosTotales(this.getMontoTotal(), this.getMontoTotalTexto());
        
        
        //String[] leyendas = {"ace", "boom", "crew", "dog", "eon"};
        //List<String> wordList = Arrays.asList(leyendas);  
        
        List<String> wordList = this.getLeyendas();  
        
        strOutput4File += sm.createOtros(wordList);
        
        
        String txt_file_name = new String();
        
        //genera el nombre del fichero
        txt_file_name += this.getSerie();
        txt_file_name += this.getFolio();
        txt_file_name += ".txt";
        
        //obtiene el directorio de solicitudes cfi de la empresa
        //String directorioSolicitudesCfdi=this.getGralDao().getCfdiSolicitudesDir() + this.getRfc_emisor();
        String directorioSolicitudesCfdi=this.getGralDao().getCfdiSolicitudesDir() + "in/";
        
        boolean fichero_txt_ok = FileHelper.createFileWithText(directorioSolicitudesCfdi , txt_file_name, strOutput4File);
        
        if (fichero_txt_ok) {
            System.out.println("Se ha generado el archivo:"+ directorioSolicitudesCfdi + txt_file_name);
        }else{
            System.out.println("NO se ha PODIDO GENERAR el archivo:"+ directorioSolicitudesCfdi + txt_file_name);
        }
        
    }
    
    
    private class StringMakerForCfdiRequest{
        
        public String createHeader(){
            
            return  "HOJA \n" +
                    "#############" +
                    "##############" + 
                    "##############" + 
                    "##############" +
                    "###############"+ 
                    "\n";
        }
        
        public String createDatosGenerales(String serie,String folio, Boolean asignaFolio){
            String cadena_retorno = new String();
            
            cadena_retorno += "[Datos Generales]\n";
            cadena_retorno += "serie|" + serie + "\n";
            cadena_retorno += "folio|" + folio + "\n";
            cadena_retorno += "asignaFolio|" + asignaFolio.toString() + "\n";
            
            return cadena_retorno;
            
        }
        
        
        public String createDatosEmisor(String emRegimen,String emRfc){
            String cadena_retorno = new String();
            
            cadena_retorno += "[Datos del Emisor]\n";
            cadena_retorno += "emRegimen|" + emRegimen + "\n";
            cadena_retorno += "emRfc|" + emRfc + "\n";
            
            return cadena_retorno;
            
        }
        
        
        
        public String createDatosReceptor(String reRfc,String reNombre,String reCalle,String reNoExterior, String reNoInterior,String reColonia, String reMunicipio, String reEstado, String rePais, String reCodigoPostal, String reNoCliente){
            String cadena_retorno = new String();
            
            cadena_retorno += "[Datos del Receptor]\n";
            cadena_retorno += "reRfc|" + reRfc + "\n";
            cadena_retorno += "reNombre|" + reNombre + "\n";
            cadena_retorno += "reCalle|" + reCalle + "\n";
            if(!reNoExterior.equals("") && reNoExterior!=null){
                cadena_retorno += "reNoExterior|" + reNoExterior + "\n";
            }
            if(!reNoInterior.equals("") && reNoInterior!=null){
                cadena_retorno += "reNoInterior|" + reNoInterior + "\n";
            }
            cadena_retorno += "reColonia|" + reColonia + "\n";
            cadena_retorno += "reMunicipio|" + reMunicipio.toUpperCase() + "\n";
            cadena_retorno += "reEstado|" + reEstado.toUpperCase() + "\n";
            cadena_retorno += "rePais|" + rePais.toUpperCase() + "\n";
            cadena_retorno += "reCodigoPostal|" + reCodigoPostal + "\n";
            cadena_retorno += "reNoCliente|" + reNoCliente + "\n";
            
            return cadena_retorno;
        }
        
        
        public String createDatosConceptos(ArrayList<LinkedHashMap<String, String>> lista_conceptos){
            
            String cadena_retorno = new String();
            
            for (HashMap<String, String> i : lista_conceptos){
                String template = "[Datos de Conceptos]\ncantidad|{0}\nunidad|{1}\nnumIdentificacion|{2}\ndescripcion|{3}\nvalorUnitario|{4}\nimporte|{5}\n";
                cadena_retorno += MessageFormat.format(template, i.get("cantidad"),i.get("unidad"), i.get("noIdentificacion"),i.get("descripcion"),i.get("valorUnitario"),i.get("importe"));
            }
            
            return cadena_retorno;
        }
        
        
        
        public String createDatosComplementariosComprobanteNivelGlobal(String subtotalConceptos, String totalConceptos, String pagoForma, String pagoCondiciones, String pagoMetodo, String numCtaPago, String lugarExpedicion){
            String cadena_retorno = "[Datos Complementarios del Comprobante a nivel global]\n";
            
            if(numCtaPago.equals("")){
                numCtaPago="NO IDENTIFICADO";
            }
            
            
            cadena_retorno += "subtotalConceptos|" + subtotalConceptos + "\n";
            cadena_retorno += "totalConceptos|" + totalConceptos + "\n";
            cadena_retorno += "pagoForma|" + pagoForma + "\n";
            cadena_retorno += "pagoCondiciones|" + pagoCondiciones + "\n";
            cadena_retorno += "pagoMetodo|" + pagoMetodo + "\n";
            cadena_retorno += "numCtaPago|" + numCtaPago + "\n";
            cadena_retorno += "lugarExpedicion|" + lugarExpedicion + "\n";
            
            return cadena_retorno;
        }
        
        
        public String createDatosComercialesComprobanteNivelGlobal( String tipoDocumento, String ordenCompra, String agente, String nombreMoneda, String tipoCambio, String observaciones){
            String cadena_retorno = "[Datos Comerciales del Comprobante a nivel global]\n";
            
            cadena_retorno += "tipoDocumento|" + tipoDocumento + "\n";
            cadena_retorno += "ordenCompra|" + ordenCompra + "\n";
            cadena_retorno += "agente|" + agente + "\n";
            
            if(!observaciones.equals("")){
                cadena_retorno += "observaciones|" + observaciones + "\n";
            }
            
            cadena_retorno += "nombreMoneda|" + nombreMoneda + "\n";
            cadena_retorno += "tipoCambio|" + tipoCambio + "\n";
            
            return cadena_retorno;
        }
        
        
        public String createDatosTotales(String montoTotal, String montoTotalTexto){
            
            String cadena_retorno =  "[Datos Totales]\n";
            
            cadena_retorno += "montoTotal|" + montoTotal + "\n";
            cadena_retorno += "montoTotalTexto|" + montoTotalTexto + "\n";
            
            return cadena_retorno;
        }
        
        
        public String createImpuestosTrasladados(ArrayList<LinkedHashMap<String, String>> lista_traslados){
            
            String cadena_retorno = new String();
            
            String subtotalTrasladados = "0";
            
            for(int j=0; j<lista_traslados.size();j++){
                HashMap i = (HashMap) lista_traslados.get(j);
                String template = "[Impuestos Trasladados]\ntrasladadoImpuesto|{0}\ntrasladadoImporte|{1}\ntrasladadoTasa|{2}\nsubtotalTrasladados|{3}\n";
                cadena_retorno += MessageFormat.format(template, i.get("impuesto"),i.get("importe"), i.get("tasa"),i.get("importe"));
            }
            
            return cadena_retorno;
        }
        
        
        
        
        public String createImpuestosRetenidos(ArrayList<LinkedHashMap<String, String>> lista_retenidos){
            
            String cadena_retorno = new String();
            
            String subtotalRetenidos = "0";
            for(int j=0; j<lista_retenidos.size();j++){
                HashMap i = (HashMap) lista_retenidos.get(j);
            //for (HashMap<String, String> i : lista_retenidos){
                String template = "[Impuestos Retenidos]\nretenidoImpuesto|{0}\nretenidoImporte|{1}\nsubtotalRetenidos|{2}\n";
                cadena_retorno += MessageFormat.format(template, i.get("impuesto"),i.get("importe"),i.get("importe"));
            //}
            }
            
            return cadena_retorno;
        }
        
        
        
        //scp j2eeserver@saturno.agnux.com:/home/j2eeserver/resources/cfdi/solicitudes/processed/B3000.txt /home/agnux/Documentos/novasol/
        
        
        public String createOtros(List<String> leyendas){
            String cadena_retorno = "[Otros]" + "\n";
            
            int counter = 1;
            for (String i : leyendas){
                String template = "LeyendaEspecial{0}|{1}\n";
                cadena_retorno += MessageFormat.format(template, counter,i);
                counter++;
            }
            
            if(counter==1){
                cadena_retorno="";
            }
            
            return cadena_retorno;
        }

    }
    
    
    
    
    public ArrayList<LinkedHashMap<String, String>> getListaConceptos() {
        return listaConceptos;
    }
    
    public void setListaConceptos(ArrayList<LinkedHashMap<String, String>> listaConceptos) {
        this.listaConceptos = listaConceptos;
    }
    
    public ArrayList<LinkedHashMap<String, String>> getListaTraslados() {
        return listaTraslados;
    }
    
    public void setListaTraslados(ArrayList<LinkedHashMap<String, String>> listaTraslados) {
        this.listaTraslados = listaTraslados;
    }
    
    public ArrayList<LinkedHashMap<String, String>> getListaRetenidos() {
        return listaRetenidos;
    }
    
    public void setListaRetenidos(ArrayList<LinkedHashMap<String, String>> listaRetenidos) {
        this.listaRetenidos = listaRetenidos;
    }
    
    
    public ArrayList<String> getLeyendas() {
        return leyendas;
    }

    public void setLeyendas(ArrayList<String> leyendas) {
        this.leyendas = leyendas;
    }
    
    
    public String getProposito() {
        return proposito;
    }

    public void setProposito(String proposito) {
        this.proposito = proposito;
    }
    
    
    public String getTipoDocumento() {
        return tipoDocumento;
    }

    public void setTipoDocumento(String tipoDocumento) {
        this.tipoDocumento = tipoDocumento;
    }
    
    public String getLugar_expedicion() {
        return lugar_expedicion;
    }

    public void setLugar_expedicion(String lugar_expedicion) {
        this.lugar_expedicion = lugar_expedicion;
    }
    
    public GralInterfaceDao getGralDao() {
        return gralDao;
    }

    public void setGralDao(GralInterfaceDao gralDao) {
        this.gralDao = gralDao;
    }
    
    public String getMetodoDePago() {
        return metodoDePago;
    }

    public void setMetodoDePago(String metodoDePago) {
        this.metodoDePago = metodoDePago;
    }
    
    public String getNumero_cuenta() {
        return numero_cuenta;
    }

    public void setNumero_cuenta(String numero_cuenta) {
        this.numero_cuenta = numero_cuenta;
    }

    public String getMontoTotal() {
        return montoTotal;
    }

    public void setMontoTotal(String total) {
        this.montoTotal = total;
    }
    
    public String getObservaciones() {
        return observaciones;
    }

    public void setObservaciones(String observaciones) {
        this.observaciones = observaciones;
    }
    
    public String getNombreMoneda() {
        return nombreMoneda;
    }

    public void setNombreMoneda(String nombreMoneda) {
        this.nombreMoneda = nombreMoneda;
    }
    

    public String getTipoCambio() {
        return tipoCambio;
    }

    public void setTipoCambio(String tipoCambio) {
        this.tipoCambio = tipoCambio;
    }
    

    public String getClaveAgente() {
        return claveAgente;
    }

    public void setClaveAgente(String claveAgente) {
        this.claveAgente = claveAgente;
    }

    public String getFolio() {
        return folio;
    }

    public void setFolio(String folio) {
        this.folio = folio;
    }

    public String getMontoTotalTexto() {
        return montoTotalTexto;
    }

    public void setMontoTotalTexto(String montoTotalTexto) {
        this.montoTotalTexto = montoTotalTexto;
    }

    public String getOrdenCompra() {
        return ordenCompra;
    }

    public void setOrdenCompra(String ordenCompra) {
        this.ordenCompra = ordenCompra;
    }

    public String getSerie() {
        return serie;
    }

    public void setSerie(String serie) {
        this.serie = serie;
    }
    
    public String getRegimen_fiscal_emisor() {
        return regimen_fiscal_emisor;
    }
    
    public void setRegimen_fiscal_emisor(String regimen_fiscal_emisor) {
        this.regimen_fiscal_emisor = regimen_fiscal_emisor;
    }
    
    public String getRfc_emisor() {
        return rfc_emisor;
    }
    
    public void setRfc_emisor(String rfc_emisor) {
        this.rfc_emisor = rfc_emisor;
    }
    
    public String getSubTotalConceptos() {
        return subTotalConceptos;
    }
    
    public void setSubTotalConceptos(String subTotalConceptos) {
        this.subTotalConceptos = subTotalConceptos;
    }
    
    public String getTotalConceptos() {
        return totalConceptos;
    }
    
    public void setTotalConceptos(String totalConceptos) {
        this.totalConceptos = totalConceptos;
    }
       
    
    public String getCondicionesDePago() {
        return condicionesDePago;
    }
    
    public void setCondicionesDePago(String condicionesDePago) {
        this.condicionesDePago = condicionesDePago;
    }
    
    
    public String getFormaDePago() {
        return formaDePago;
    }
    
    public void setFormaDePago(String formaDePago) {
        this.formaDePago = formaDePago;
    }
    
    public String getNo_cliente() {
        return no_cliente;
    }
    
    public void setNo_cliente(String no_cliente) {
        this.no_cliente = no_cliente;
    }
    
    public String getNombre_receptor() {
        return nombre_receptor;
    }
    
    public void setNombre_receptor(String razon_social_receptor) {
        this.nombre_receptor = razon_social_receptor;
    }
    
    public String getRfc_receptor() {
        return rfc_receptor;
    }
    
    public void setRfc_receptor(String rfc_receptor) {
        this.rfc_receptor = rfc_receptor;
    }
    
    
    public String getCalle_domicilio_receptor() {
        return calle_domicilio_receptor;
    }
    
    public void setCalle_domicilio_receptor(String calle_domicilio_receptor) {
        this.calle_domicilio_receptor = calle_domicilio_receptor;
    }
    
    public String getNoExterior_domicilio_receptor() {
        return noExterior_domicilio_receptor;
    }
    
    public void setNoExterior_domicilio_receptor(String noExterior_domicilio_receptor) {
        this.noExterior_domicilio_receptor = noExterior_domicilio_receptor;
    }
    
    public String getNoInterior_domicilio_receptor() {
        return noInterior_domicilio_receptor;
    }
    
    public void setNoInterior_domicilio_receptor(String noInterior_domicilio_receptor) {
        this.noInterior_domicilio_receptor = noInterior_domicilio_receptor;
    }
    
    
    public String getColonia_domicilio_receptor() {
        return colonia_domicilio_receptor;
    }
    
    public void setColonia_domicilio_receptor(String colonia_domicilio_receptor) {
        this.colonia_domicilio_receptor = colonia_domicilio_receptor;
    }
    
    
    public String getEstado_domicilio_receptor() {
        return estado_domicilio_receptor;
    }
    
    public void setEstado_domicilio_receptor(String estado_domicilio_receptor) {
        this.estado_domicilio_receptor = estado_domicilio_receptor;
    }
    
    public String getPais_domicilio_receptor() {
        return pais_domicilio_receptor;
    }
    
    public void setPais_domicilio_receptor(String pais_domicilio_receptor) {
        this.pais_domicilio_receptor = pais_domicilio_receptor;
    }
    
    public String getMunicipio_domicilio_receptor() {
        return municipio_domicilio_receptor;
    }

    public void setMunicipio_domicilio_receptor(String municipio_domicilio_receptor) {
        this.municipio_domicilio_receptor = municipio_domicilio_receptor;
    }
    
    public String getCodigoPostal_domicilio_receptor() {
        return codigoPostal_domicilio_receptor;
    }
    
    public void setCodigoPostal_domicilio_receptor(String codigoPostal_domicilio_receptor) {
        this.codigoPostal_domicilio_receptor = codigoPostal_domicilio_receptor;
    }
}
