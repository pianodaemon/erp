package com.agnux.kemikal.springdaos;

import com.agnux.kemikal.interfacedaos.ParametrosGeneralesInterfaceDao;
import org.springframework.jdbc.core.JdbcTemplate;
import java.util.Map;


public class ParametrosGeneralesSpringDao implements ParametrosGeneralesInterfaceDao{
    
    private JdbcTemplate jdbcTemplate;
    
    public JdbcTemplate getJdbcTemplate() {
        return jdbcTemplate;
    }
    
    public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }
    
    @Override
    public String getCfdEmitidosDir() {
        String cfdemitidosdir = System.getenv("ERP_ROOT") + "/" + "resources" + "/"+"cfd" + "/"+"emitidos" + "/";
        return cfdemitidosdir;
    }
    
    @Override
    public String getImagesDir() {
        String imagesdir = System.getenv("ERP_ROOT") + "/" + "resources" + "/"+"images" + "/";
        return imagesdir;
    }
    
    @Override
    public String getSslDir() {
        String ssldir = System.getenv("ERP_ROOT") + "/" + "resources" + "/"+"ssl" + "/";
        //System.out.println(ssldir);
        return ssldir;
    }
    
    
    @Override
    public String getXslDir() {
        String xsldir = System.getenv("ERP_ROOT") + "/" + "resources" + "/"+"schemas" + "/";
        //System.out.println(xsldir);
        return xsldir;
    }
    
    @Override
    public String getTmpDir() {
        String xsldir = System.getenv("ERP_ROOT") + "/" + "resources" + "/"+"tmp" + "/";
        return xsldir;
    }

    
    
    @Override
    public String getRfcEmpresaEmisora(){
        String sql_to_query = "SELECT valor FROM erp_parametros_generales WHERE variable ILIKE 'rfc'";
        Map<String, Object> map_rfc = this.getJdbcTemplate().queryForMap(sql_to_query);
        String rfc_emisora = map_rfc.get("valor").toString();
        return rfc_emisora;
    }
    
    
    @Override
    public String getRazonSocialEmpresaEmisora(){
        String sql_to_query = "SELECT valor FROM erp_parametros_generales WHERE variable ILIKE 'razon_social'";
        Map<String, Object> map_razon_social = this.getJdbcTemplate().queryForMap(sql_to_query);
        String razon_social_emisora = map_razon_social.get("valor").toString();
        return razon_social_emisora;
    }
   
    
    @Override
    public String getCertificadoEmpresaEmisora() {
        String sql_to_query = "SELECT valor as archivo_certificado FROM erp_parametros_generales WHERE variable ILIKE 'archivo_certificado'";
        Map<String, Object> map_certificado = this.getJdbcTemplate().queryForMap(sql_to_query);
        String certificado_emisora = map_certificado.get("archivo_certificado").toString();
        return certificado_emisora;
    }
    
    
    @Override
    public String getNoCertificadoEmpresaEmisora() {
        String sql_to_query = "SELECT valor as numero_certificado FROM erp_parametros_generales WHERE variable ILIKE 'numero_certificado'";
        Map<String, Object> map_no_cert = this.getJdbcTemplate().queryForMap(sql_to_query);
        String no_cert_emisora = map_no_cert.get("numero_certificado").toString();
        return no_cert_emisora;
    }
    
    
    @Override
    public String getFicheroLlavePrivada() {
        String sql_to_query = "SELECT valor as archivo_llave FROM erp_parametros_generales WHERE variable ILIKE 'archivo_llave'";
        Map<String, Object> map_archivo_llave = this.getJdbcTemplate().queryForMap(sql_to_query);
        String archivo_llave_emisora = map_archivo_llave.get("archivo_llave").toString();
        return archivo_llave_emisora;
    }
    
    @Override
    public String getPasswordLlavePrivada() {
        String sql_to_query = "SELECT valor as password_llave FROM erp_parametros_generales WHERE variable ILIKE 'password_llave'";
        Map<String, Object> map_password_llave = this.getJdbcTemplate().queryForMap(sql_to_query);
        String password_llave_emisora = map_password_llave.get("password_llave").toString();
        return password_llave_emisora;
    }
    
    @Override
    public String getFolioFactura() {
        String sql_to_query = "SELECT valor as folio_factura FROM erp_parametros_generales WHERE variable ILIKE 'folio_factura'";
        Map<String, Object> map_folio_factura = this.getJdbcTemplate().queryForMap(sql_to_query);
        String folio_factura_emisora = map_folio_factura.get("folio_factura").toString();
        return folio_factura_emisora;
    }
    
    @Override
    public String getSerieFactura() {
        String sql_to_query = "SELECT valor as serie_factura FROM erp_parametros_generales WHERE variable ILIKE 'serie_factura'";
        Map<String, Object> map_serie_factura = this.getJdbcTemplate().queryForMap(sql_to_query);
        String serie_factura_emisora = map_serie_factura.get("serie_factura").toString();
        return serie_factura_emisora;
    }
    
    @Override
    public String getAnoAprobacionFactura() {
        String sql_to_query = "SELECT valor as ano_aprobacion_factura FROM erp_parametros_generales WHERE variable ILIKE 'ano_aprobacion_factura'";
        Map<String, Object> map_ano_aprobacion_factura = this.getJdbcTemplate().queryForMap(sql_to_query);
        String ano_aprobacion_factura_emisora = map_ano_aprobacion_factura.get("ano_aprobacion_factura").toString();
        return ano_aprobacion_factura_emisora;
    }

    @Override
    public String getNoAprobacionFactura() {
        String sql_to_query = "SELECT valor as num_aprobacion_factura FROM erp_parametros_generales WHERE variable ILIKE 'num_aprobacion_factura'";
        Map<String, Object> map_num_aprobacion_factura = this.getJdbcTemplate().queryForMap(sql_to_query);
        String num_aprobacion_factura_emisora = map_num_aprobacion_factura.get("num_aprobacion_factura").toString();
        return num_aprobacion_factura_emisora;
    }
    
    
    @Override
    public String getSerieNotaCredito() {
        String sql_to_query = "SELECT valor as serie_nota_credito FROM erp_parametros_generales WHERE variable ILIKE 'serie_nota_credito'";
        Map<String, Object> map_serie_nota_credito = this.getJdbcTemplate().queryForMap(sql_to_query);
        String serie_nota_credito_emisora = map_serie_nota_credito.get("serie_nota_credito").toString();
        return serie_nota_credito_emisora;
    }
    
    @Override
    public String getFolioNotaCredito() {
        String sql_to_query = "SELECT valor as folio_nota_credito FROM erp_parametros_generales WHERE variable ILIKE 'folio_nota_credito'";
        Map<String, Object> map_folio_nota_credito = this.getJdbcTemplate().queryForMap(sql_to_query);
        String folio_nota_credito_emisora = map_folio_nota_credito.get("folio_nota_credito").toString();
        return folio_nota_credito_emisora;
    }
    
    @Override
    public String getNoAprobacionNotaCredito() {
        String sql_to_query = "SELECT valor as num_aprobacion_nota_credito FROM erp_parametros_generales WHERE variable ILIKE 'num_aprobacion_nota_credito'";
        Map<String, Object> map_num_aprobacion_nota_credito = this.getJdbcTemplate().queryForMap(sql_to_query);
        String num_aprobacion_nota_credito_emisora = map_num_aprobacion_nota_credito.get("num_aprobacion_nota_credito").toString();
        return num_aprobacion_nota_credito_emisora;
    }
    
    @Override
    public String getAnoAprobacionNotaCredito() {
        String sql_to_query = "SELECT valor as ano_aprobacion_nota_credito FROM erp_parametros_generales WHERE variable ILIKE 'ano_aprobacion_nota_credito'";
        Map<String, Object> map_ano_aprobacion_nota_credito = this.getJdbcTemplate().queryForMap(sql_to_query);
        String ano_aprobacion_nota_credito_emisora = map_ano_aprobacion_nota_credito.get("ano_aprobacion_nota_credito").toString();
        return ano_aprobacion_nota_credito_emisora;
    }
    
    
    
    @Override
    public String getSerieNotaCargo() {
        String sql_to_query = "SELECT valor as serie_nota_cargo FROM erp_parametros_generales WHERE variable ILIKE 'serie_nota_cargo'";
        Map<String, Object> map_serie_nota_cargo = this.getJdbcTemplate().queryForMap(sql_to_query);
        String serie_nota_cargo_emisora = map_serie_nota_cargo.get("serie_nota_cargo").toString();
        return serie_nota_cargo_emisora;
    }
    
    @Override
    public String getFolioNotaCargo() {
        String sql_to_query = "SELECT valor as folio_nota_cargo FROM erp_parametros_generales WHERE variable ILIKE 'folio_nota_cargo'";
        Map<String, Object> map_folio_nota_cargo = this.getJdbcTemplate().queryForMap(sql_to_query);
        String folio_nota_cargo_emisora = map_folio_nota_cargo.get("folio_nota_cargo").toString();
        return folio_nota_cargo_emisora;
    }
    
    
    @Override
    public String getAnoAprobacionNotaCargo() {
        String sql_to_query = "SELECT valor as ano_aprobacion_nota_cargo FROM erp_parametros_generales WHERE variable ILIKE 'ano_aprobacion_nota_cargo'";
        Map<String, Object> map_ano_aprobacion_nota_cargo = this.getJdbcTemplate().queryForMap(sql_to_query);
        String ano_aprobacion_nota_cargo_emisora = map_ano_aprobacion_nota_cargo.get("ano_aprobacion_nota_cargo").toString();
        return ano_aprobacion_nota_cargo_emisora;
    }
    
    @Override
    public String getNoAprobacionNotaCargo() {
        String sql_to_query = "SELECT valor as num_aprobacion_nota_cargo FROM erp_parametros_generales WHERE variable ILIKE 'num_aprobacion_nota_cargo'";
        Map<String, Object> map_num_aprobacion_nota_cargo = this.getJdbcTemplate().queryForMap(sql_to_query);
        String num_aprobacion_nota_cargo_emisora = map_num_aprobacion_nota_cargo.get("num_aprobacion_nota_cargo").toString();
        return num_aprobacion_nota_cargo_emisora;
    }
    
    
    
    
    @Override
    public String getCalleDomicilioFiscalEmpresaEmisora() {
        String sql_to_query = "SELECT valor FROM erp_parametros_generales WHERE variable ILIKE 'calle'";
        Map<String, Object> map_calle = this.getJdbcTemplate().queryForMap(sql_to_query);
        String calle_emisora = map_calle.get("valor").toString();
        return calle_emisora;
    }
    
    @Override
    public String getCpDomicilioFiscalEmpresaEmisora() {
        String sql_to_query = "SELECT valor FROM erp_parametros_generales WHERE variable ILIKE 'cp'";
        Map<String, Object> map_cp = this.getJdbcTemplate().queryForMap(sql_to_query);
        String cp_emisora = map_cp.get("valor").toString();
        return cp_emisora;
    }
    
    @Override
    public String getColoniaDomicilioFiscalEmpresaEmisora() {
        String sql_to_query = "SELECT valor FROM erp_parametros_generales WHERE variable ILIKE 'colonia'";
        Map<String, Object> map_colonia = this.getJdbcTemplate().queryForMap(sql_to_query);
        String colonia_emisora = map_colonia.get("valor").toString();
        return colonia_emisora;
    }
    
    @Override
    public String getLocalidadDomicilioFiscalEmpresaEmisora() {
        String localidad_emisora = "";
        return localidad_emisora;
    }
    
    @Override
    public String getMunicipioDomicilioFiscalEmpresaEmisora() {
        //obtener clave de pais
        String sql_to_query_pais = "SELECT valor FROM erp_parametros_generales WHERE variable ILIKE 'pais'";
        Map<String, Object> map_pais = this.getJdbcTemplate().queryForMap(sql_to_query_pais);
        String pais_emisora = map_pais.get("valor").toString();
        
        //obtener clave de estado
        String sql_to_query = "SELECT valor FROM erp_parametros_generales WHERE variable ILIKE 'estado'";
        Map<String, Object> map_calle = this.getJdbcTemplate().queryForMap(sql_to_query);
        String estado_emisora = map_calle.get("valor").toString();
        
        //obtener nombre del municipio
        String sql_to_query_mun = "SELECT DISTINCT municipios.nom_mun "
                + "FROM erp_parametros_generales, municipios "
                + "WHERE municipios.cve_mun = erp_parametros_generales.valor "
                + "AND erp_parametros_generales.variable ILIKE 'localidad' "
                + "AND municipios.cve_pais ILIKE '"+pais_emisora+"' "
                + "AND municipios.cve_ent ILIKE '"+estado_emisora+"' LIMIT 1";
        Map<String, Object> map_municipio = this.getJdbcTemplate().queryForMap(sql_to_query_mun);
        String municipio_emisora = map_municipio.get("nom_mun").toString();
        
        return municipio_emisora;
    }
    
    
    @Override
    public String getEstadoDomicilioFiscalEmpresaEmisora() {
        //obtener clave del pais
        String sql_to_query_pais = "SELECT valor FROM erp_parametros_generales WHERE variable ILIKE 'pais'";
        Map<String, Object> map_pais = this.getJdbcTemplate().queryForMap(sql_to_query_pais);
        String pais_emisora = map_pais.get("valor").toString();
        
        //obtener nombre del estado
        String sql_query_estado = "SELECT DISTINCT municipios.nom_ent "
                + "FROM erp_parametros_generales, municipios "
                + "WHERE municipios.cve_ent = erp_parametros_generales.valor "
                + "AND erp_parametros_generales.variable ILIKE 'estado' "
                + "AND municipios.cve_pais ILIKE '"+pais_emisora+"' LIMIT 1";
        Map<String, Object> map_estado = this.getJdbcTemplate().queryForMap(sql_query_estado);
        String estado_emisora = map_estado.get("nom_ent").toString();
        
        return estado_emisora;
    }
    
    
    @Override
    public String getPaisDomicilioFiscalEmpresaEmisora() {
        //obtener nombre del pais
        String sql_query_pais = "SELECT DISTINCT municipios.pais_ent "
                + "FROM erp_parametros_generales, municipios  "
                + "WHERE municipios.cve_pais = erp_parametros_generales.valor "
                + "AND erp_parametros_generales.variable ILIKE 'pais' LIMIT 1";
        Map<String, Object> map_pais = this.getJdbcTemplate().queryForMap(sql_query_pais);
        String pais_emisora = map_pais.get("pais_ent").toString();
        return pais_emisora;
    }
    
    @Override
    public String getNoExteriorDomicilioFiscalEmpresaEmisora() {
        String sql_to_query = "SELECT valor FROM erp_parametros_generales WHERE variable ILIKE 'numero'";
        Map<String, Object> map_numero = this.getJdbcTemplate().queryForMap(sql_to_query);
        String numero_emisora = map_numero.get("valor").toString();
        return numero_emisora;
    }
    
    @Override
    public String getNoInteriorDomicilioFiscalEmpresaEmisora() {
        String numero_emisora = "";
        return numero_emisora;
    }
    
    
    @Override
    public String getReferenciaDomicilioFiscalEmpresaEmisora() {
        String referencia_emisora = "";
        return referencia_emisora;
    }
    
    @Override
    public void actualizarFolioFactura() {
        String sql_to_query = "UPDATE erp_parametros_generales SET valor = (SELECT valor::integer FROM erp_parametros_generales WHERE variable ILIKE 'folio_factura')+1 "
                + "WHERE variable ILIKE 'folio_factura'";
        this.getJdbcTemplate().execute(sql_to_query);
    }
    
    @Override
    public void actualizarFolioNotaCredito() {
        String sql_to_query = "UPDATE erp_parametros_generales SET valor = (SELECT valor::integer FROM erp_parametros_generales WHERE variable ILIKE 'folio_nota_credito')+1 "
                + "WHERE variable ILIKE 'folio_nota_credito'";
        this.getJdbcTemplate().execute(sql_to_query);
    }
    
    @Override
    public void actualizarFolioNotaCargo() {
        String sql_to_query = "UPDATE erp_parametros_generales SET valor = (SELECT valor::integer FROM erp_parametros_generales WHERE variable ILIKE 'folio_nota_cargo')+1 "
                + "WHERE variable ILIKE 'folio_nota_cargo'";
        this.getJdbcTemplate().execute(sql_to_query);
    }
    
    
}
