package com.agnux.kemikal.reportes;

import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import org.springframework.util.ResourceUtils;
import java.io.FileNotFoundException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.io.File;


public class PdfCotizacion {
    private HashMap<String, String> datosHeaderFooter = new HashMap<>();
    private ArrayList<HashMap<String, String>> lista_productos = new ArrayList<HashMap<String, String>>();
    private ArrayList<HashMap<String, String>> condiciones_comerciales = new ArrayList<HashMap<String, String>>();
    private ArrayList<HashMap<String, String>> politicas_pago = new ArrayList<HashMap<String, String>>();
    private ArrayList<HashMap<String, String>> incoterms = new ArrayList<HashMap<String, String>>();
    private String logoPath;
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
    public PdfCotizacion(HashMap<String, String> HeaderFooter, HashMap<String, String> datosEmisor, HashMap<String, String> datos, HashMap<String, String> datosCliente, ArrayList<HashMap<String, String>> lista_productos, ArrayList<HashMap<String, String>> condiciones_comerciales, ArrayList<HashMap<String, String>> politicas_pago, ArrayList<HashMap<String, String>> incoterms) throws URISyntaxException {
        this.setDatosHeaderFooter(HeaderFooter);
        this.setLista_productos(lista_productos);
        this.setCondiciones_comerciales(condiciones_comerciales);
        this.setPoliticas_pago(politicas_pago);
        this.setIncoterms(incoterms);
        this.setLogoPath(datos.get("ruta_logo"));
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

    public byte[] createPDF() throws FileNotFoundException, JRException {

        JasperReport jasperReport = getJasperReport();
        Map<String, Object> parameters = getParameters();
        JRDataSource dataSource = getDataSource();

        JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, dataSource);
        return JasperExportManager.exportReportToPdf(jasperPrint);
    }
    
    private JasperReport getJasperReport() throws FileNotFoundException, JRException {
        
        File template = ResourceUtils.getFile("classpath:rrm_cot.jrxml");
        return JasperCompileManager.compileReport(template.getAbsolutePath());
    }

    private Map<String, Object> getParameters() {
        
        Map<String, Object> parameters = new HashMap<>();
        String emisorMunicipioEstado = String.format("%s, %s", emisorMunicipio, emisorEstado);
        
        parameters.put("logoPath", logoPath);
        parameters.put("lugarFechaLetra", String.format("%s, a %s", emisorMunicipioEstado, convertirFechaALetra(fecha)));
        parameters.put("clienteRazonSocial", clieRazonSocial);
        parameters.put("clienteMunicipioEstado", String.format("%s, %s", clieMunicipio, clieEstado));
        parameters.put("clienteContacto", clieContacto);
        parameters.put("saludo", saludo);
        parameters.put("subtotal", addSeparadorMiles(subtotal, ","));
        parameters.put("impuesto", addSeparadorMiles(impuesto, ","));
        parameters.put("total", addSeparadorMiles(total, ","));
        parameters.put("despedida", despedida);
        parameters.put("nombreUsuario", nombreUsuario);
        parameters.put("emisorRazonSocial", emisorRazonSocial);
        parameters.put("emisorDomicilio", String.format("%s No. %s, Col. %s, C. P. %s, %s. Tels: %s",
                emisorCalle, emisorNumero, emisorColonia, emisorCp, emisorMunicipioEstado, emisorTelefono));
        parameters.put("emisorPaginaWeb", emisorPaginaWeb);
        return parameters;
    }

    private JRDataSource getDataSource() {

        List<DetalleCotizacion> detalleCotGrid = new ArrayList<>();

        for (HashMap<String, String> m : lista_productos) {
            detalleCotGrid.add(new DetalleCotizacion(
                    String.format("%s, %s", m.get("producto"), m.get("presentacion")),
                    addSeparadorMiles(m.get("cantidad"), ","),
                    addSeparadorMiles(m.get("precio_unitario"), ","),
                    addSeparadorMiles(m.get("importe"), ",")));
        }

        return new JRBeanCollectionDataSource(detalleCotGrid);
    }
    
    private static String convertirFechaALetra(String fecha) {
        
        String[] fechaLetra = fecha.split("-");
        String mes;

        switch (fechaLetra[1]) {
            case "01": mes = "enero"; break;
            case "02": mes = "febrero"; break;
            case "03": mes = "marzo"; break;
            case "04": mes = "abril"; break;
            case "05": mes = "mayo"; break;
            case "06": mes = "junio"; break;
            case "07": mes = "julio"; break;
            case "08": mes = "agosto"; break;
            case "09": mes = "septiembre"; break;
            case "10": mes = "octubre"; break;
            case "11": mes = "noviembre"; break;
            case "12": mes = "diciembre"; break;
            default: mes = ""; break;
        }
        return String.format("%s de %s de %s", fechaLetra[2], mes, fechaLetra[0]);
    }
    
    private static String addSeparadorMiles(String input, String sep) {
        String output = "";
        String[] arr = input.split("\\.");
        String s = arr[0];
        int cantGrupos = s.length() / 3;
        int cantCifras = s.length() % 3;
        int pos = 0;

        if (cantCifras != 0) {
            output += s.substring(0, cantCifras);
            if (cantGrupos > 0) {
                output += sep;
            }
            pos = cantCifras;
        }
        for (int i = 1; i <= cantGrupos; i++) {
            output += s.substring(pos, pos + 3);
            if (i < cantGrupos) {
                output += sep;
            }
            pos += 3;
        }
        if (arr.length > 1) {
            output += "." + arr[1];
        }
        return output;
    }

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

    public String getLogoPath() {
        return logoPath;
    }

    public void setLogoPath(String logoPath) {
        this.logoPath = logoPath;
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
