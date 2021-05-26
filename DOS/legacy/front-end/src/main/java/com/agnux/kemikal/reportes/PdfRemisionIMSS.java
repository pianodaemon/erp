package com.agnux.kemikal.reportes;

import net.sf.jasperreports.engine.*;
//import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import org.springframework.util.ResourceUtils;
import java.io.FileNotFoundException;
import java.io.File;
import java.util.HashMap;
import java.util.Map;
//import java.util.ArrayList;
//import java.util.List;

public class PdfRemisionIMSS {

    public static byte[] createPdf(HashMap<String, String> data) throws FileNotFoundException, JRException {

        JasperReport jasperReport = getJasperReport();
//        Map<String, Object> parameters = getParameters(data);
        Map<String, Object> parameters = (Map<String, Object>) data.clone();
        parameters.put("importe", Helper.addSeparadorMiles((String) parameters.get("importe"), ","));
//        parameters.put("proveedor", "RR Medica SA de CV");
//        parameters.remove("cliente");
//        parameters.remove("id_status");
//        JRDataSource dataSource = getDataSource();

//        JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, dataSource);
        JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, new JREmptyDataSource());
        return JasperExportManager.exportReportToPdf(jasperPrint);
    }

    private static JasperReport getJasperReport() throws FileNotFoundException, JRException {

        File template = ResourceUtils.getFile("classpath:rrm_cr_imss.jrxml");
//        File template = ResourceUtils.getFile("classpath:rrm_cot.jrxml");
        return JasperCompileManager.compileReport(template.getAbsolutePath());
    }

//    private static Map<String, Object> getParameters(HashMap<String, String> data) {
//
//        Map<String, Object> parameters = new HashMap<>();
//
//        parameters.put("Proveedor", data.get("Proveedor"));
//        parameters.put("Folio", String.format("%s, a %s", emisorMunicipioEstado, convertirFechaALetra(fecha)));
//        parameters.put("NoContrato", clieRazonSocial);
//        parameters.put("FolioImss", String.format("%s, %s", clieMunicipio, clieEstado));
//        parameters.put("FechaExp", clieContacto);
//        parameters.put("FechaPago", saludo);
//        parameters.put("Estatus", addSeparadorMiles(subtotal, ","));
//        parameters.put("Importe", addSeparadorMiles(impuesto, ","));
//        parameters.put("Doc1", addSeparadorMiles(total, ","));
//
//        return parameters;
//    }

//    private static JRDataSource getDataSource() {
//
//        List<DetalleCotizacion> detalleCotGrid = new ArrayList<>();
//
//        return new JRBeanCollectionDataSource(detalleCotGrid);
//    }
}
