package com.agnux.kemikal.interfacedaos;

public interface ParametrosGeneralesInterfaceDao {
    
    public String getImagesDir();
    
    public String getSslDir();

    public String getCfdEmitidosDir();
    
    public String getXslDir();
    
    public String getTmpDir();
    
    
    public String getRazonSocialEmpresaEmisora();
    
    public String getRfcEmpresaEmisora();
    
    public String getCertificadoEmpresaEmisora();
    
    public String getNoCertificadoEmpresaEmisora();
    
    public String getFicheroLlavePrivada();
    
    public String getPasswordLlavePrivada();
    
    public String getFolioFactura();
    
    public String getFolioNotaCredito();
    
    public String getFolioNotaCargo();
    
    public String getSerieNotaCargo();
    
    public String getAnoAprobacionNotaCargo();
    
    public String getNoAprobacionNotaCargo();
    
    public String getNoAprobacionNotaCredito();
    
    public String getSerieNotaCredito();
    
    public String getAnoAprobacionNotaCredito();
    
    public String getSerieFactura();
    
    public String getAnoAprobacionFactura();
    
    public String getNoAprobacionFactura();
    
    public String getCalleDomicilioFiscalEmpresaEmisora();
    
    public String getCpDomicilioFiscalEmpresaEmisora();
    
    public String getColoniaDomicilioFiscalEmpresaEmisora();
    
    public String getEstadoDomicilioFiscalEmpresaEmisora();
    
    public String getLocalidadDomicilioFiscalEmpresaEmisora();
    
    public String getMunicipioDomicilioFiscalEmpresaEmisora();
    
    public String getNoExteriorDomicilioFiscalEmpresaEmisora();
    
    public String getNoInteriorDomicilioFiscalEmpresaEmisora();
    
    public String getPaisDomicilioFiscalEmpresaEmisora();
    
    public String getReferenciaDomicilioFiscalEmpresaEmisora();
    
    public void actualizarFolioFactura();
    
    public void actualizarFolioNotaCredito();
    
    public void actualizarFolioNotaCargo();
    
}
