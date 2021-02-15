package com.agnux.kemikal.reportes;

public class DetalleCotizacion {
    private String concepto;
    private String cantidad;
    private String precioUnitario;
    private String importe;

    public DetalleCotizacion(String concepto, String cantidad, String precioUnitario, String importe) {
        this.concepto = concepto;
        this.cantidad = cantidad;
        this.precioUnitario = precioUnitario;
        this.importe = importe;
    }

    public String getConcepto() {
        return concepto;
    }

    public String getCantidad() {
        return cantidad;
    }

    public String getPrecioUnitario() {
        return precioUnitario;
    }

    public String getImporte() {
        return importe;
    }
}