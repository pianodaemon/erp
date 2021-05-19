package com.agnux.kemikal.springdaos;

import com.agnux.common.helpers.StringHelper;
import com.agnux.kemikal.interfacedaos.GralInterfaceDao;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.io.*;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;


public class GralSpringDao implements GralInterfaceDao{
    private JdbcTemplate jdbcTemplate;
    
    public JdbcTemplate getJdbcTemplate() {
        return jdbcTemplate;
    }
    
    public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }
    
    @Override
    public String getInvoicesPath() {
        Properties prop = new Properties();
        String invoicePath="NO SE ENCONTRO LA RUTA AL ARCHIVO DE CONFIGURACIONES agnux.properties en WEB-INF";
        InputStream is = null;

        try {
            is = this.getClass().getClassLoader().getResourceAsStream("agnux.properties");
            prop.load(is);
            invoicePath=prop.getProperty("INVOICES_PATH");
        } catch(IOException e) {
            System.out.println(e.toString());
        }

        System.out.println("INVOICES_PATH="+invoicePath);
        return invoicePath;
    }

    @Override
    public String getCfdEmitidosDir() {
        String cfdemitidosdir = System.getenv("ERP_ROOT") + "/" + "resources" + "/"+"cfd" + "/"+"emitidos" + "/";
        return cfdemitidosdir;
    }
    
    @Override
    public String getCfdiSolicitudesDir() {
        String cfdemitidosdir = System.getenv("ERP_ROOT") + "/" + "resources" + "/"+"cfdi" + "/"+"solicitudes" + "/";
        return cfdemitidosdir;
    }
    
    @Override
    public String getJvmTmpDir() {
        String jvmtmpdir = System.getProperty("java.io.tmpdir");
        return jvmtmpdir;
    }
    
    @Override
    public String getProdImgDir() {
        String xsldir = System.getenv("ERP_ROOT") + "/resources/productos/img/";
        return xsldir;
    }
    
    @Override
    public String getProdPdfDir() {
        String xsldir = System.getenv("ERP_ROOT") + "/resources/productos/pdf/";
        return xsldir;
    }
    
    @Override
    public String getCfdiTimbreEmitidosDir() {
        String cfditimbreemitidosdir = System.getenv("ERP_ROOT") + "/" + "resources" + "/"+"cfdi" + "/"+"timbre" + "/" + "emitidos" + "/";
        return cfditimbreemitidosdir;
    }
    
    @Override
    public String getCfdiTimbreCanceladosDir() {
        String cfditimbreemitidosdir = System.getenv("ERP_ROOT") + "/" + "resources" + "/"+"cfdi" + "/"+"timbre" + "/" + "cancelados" + "/";
        return cfditimbreemitidosdir;
    }
    
    @Override
    public String getCfdiTimbreJarWsDir() {
        String cfditimbrejarwsdir = System.getenv("ERP_ROOT") + "/" + "resources" + "/"+"cfdi" + "/"+"timbre" + "/" + "jarwscli" + "/";
        return cfditimbrejarwsdir;
    }
    
    @Override
    public String getImagesDir() {
        String imagesdir = System.getenv("ERP_ROOT") + "/" + "resources" + "/"+"images" + "/";
        return imagesdir;
    }
    
    @Override
    public String getNombreFactura(Integer id_pago) {
        String sql_to_query = "select aux_no_fac from erp_pagos where id="+id_pago;
        System.out.println("sql_to_query:"+sql_to_query);
        Map<String, Object> mapConta = this.getJdbcTemplate().queryForMap(sql_to_query);
        String fileName = String.valueOf(mapConta.get("aux_no_fac"));

        System.out.println("Nombre del archivo PDF y XML: "+fileName);

        return fileName;
    }
    
    @Override
    public String getSslDir() {
        String ssldir = System.getenv("ERP_ROOT") + "/" + "resources" + "/"+"ssl" + "/";
        return ssldir;
    }
    
    @Override
    public String getXslDir() {
        String xsldir = System.getenv("ERP_ROOT") + "/" + "resources" +"/"+"schemas" + "/"+"xsl" + "/";
        return xsldir;
    }
    
    @Override
    public String getXsdDir() {
        String xsddir = System.getenv("ERP_ROOT") + "/" + "resources" +"/"+"schemas" + "/"+"xsd" + "/";
        return xsddir;
    }
    
    @Override
    public String getTmpDir() {
        String xsldir = System.getenv("ERP_ROOT") + "/" + "resources" + "/"+"tmp" + "/";
        return xsldir;
    }
    
    @Override
    public String getZebraDir() {
        String zebradir = System.getenv("ERP_ROOT") + "/" + "resources" + "/"+"zebra";
        return zebradir;
    }
    
    @Override
    public String getZebraInDir() {
        String zebradir = this.getZebraDir()+ "/"+"in";
        return zebradir;
    }
    
    @Override
    public String getZebraOutDir() {
        String zebradir = this.getZebraDir()+ "/"+"out";
        return zebradir;
    }
    
    @Override
    public String getZebraProcessingDir() {
        String zebradir = this.getZebraDir()+ "/"+"processing";
        return zebradir;
    }
    
    @Override
    public String getEmpresa_IncluyeModContable(Integer id_empresa){
        String sql_to_query = "select incluye_contabilidad from gral_emp where id="+id_empresa;

        Map<String, Object> mapConta = this.getJdbcTemplate().queryForMap(sql_to_query);
        String incluye_contabilidad = String.valueOf(mapConta.get("incluye_contabilidad"));

        System.out.println("incluye_contabilidad: "+incluye_contabilidad);

        return incluye_contabilidad;
    }
    
    @Override
    public String getEmpresa_NivelCta(Integer id_empresa){
        String sql_to_query = "select nivel_cta from gral_emp where id="+id_empresa;

        Map<String, Object> mapNivel = this.getJdbcTemplate().queryForMap(sql_to_query);
        String nivel_cta = mapNivel.get("nivel_cta").toString();

        System.out.println("nivel_cta: "+nivel_cta);

        return nivel_cta;
    }
    
    @Override
    public String getRazonSocialEmpresaEmisora(Integer id_empresa){
        String sql_to_query = "select titulo from gral_emp where id ="+id_empresa;
        //System.out.println("sql_to_query:"+sql_to_query);

        Map<String, Object> map_razon_social = this.getJdbcTemplate().queryForMap(sql_to_query);
        String razon_social_emisora = map_razon_social.get("titulo").toString();
        return razon_social_emisora;
    }

    @Override
    public String getRfcEmpresaEmisora(Integer id_empresa){
        String sql_to_query = "select rfc from gral_emp where id ="+id_empresa;
        Map<String, Object> map_rfc = this.getJdbcTemplate().queryForMap(sql_to_query);
        String rfc_emisora = map_rfc.get("rfc").toString();
        return rfc_emisora;
    }

    @Override
    public String getRegimenFiscalEmpresaEmisora(Integer id_empresa){
        String sql_to_query = "select regimen_fiscal from gral_emp where id ="+id_empresa;
        Map<String, Object> map_regimen = this.getJdbcTemplate().queryForMap(sql_to_query);
        String regimen_fiscal_emisora = map_regimen.get("regimen_fiscal").toString();
        return regimen_fiscal_emisora;
    }

    
    @Override
    public String getEmailSucursal(Integer id_sucursal){
        String sql_to_query = "select email from gral_suc where id="+id_sucursal+";";
        Map<String, Object> map_email = this.getJdbcTemplate().queryForMap(sql_to_query);
        String email = map_email.get("email").toString();
        return email;
    }
    
    
    @Override
    public String getCertificadoEmpresaEmisora(Integer id_empresa, Integer id_sucursal) {
        String sql_to_query = "select fac_cfds_conf.archivo_certificado from fac_cfds_conf where fac_cfds_conf.empresa_id="+id_empresa+" AND fac_cfds_conf.gral_suc_id="+id_sucursal+";";
        Map<String, Object> map_certificado = this.getJdbcTemplate().queryForMap(sql_to_query);
        String certificado_emisora = map_certificado.get("archivo_certificado").toString();
        return certificado_emisora;
    }


    @Override
    public String getNoCertificadoEmpresaEmisora(Integer id_empresa, Integer id_sucursal) {
        String sql_to_query = "select fac_cfds_conf.numero_certificado from fac_cfds_conf where fac_cfds_conf.empresa_id="+id_empresa+" AND fac_cfds_conf.gral_suc_id="+id_sucursal+";";
        Map<String, Object> map_no_cert = this.getJdbcTemplate().queryForMap(sql_to_query);
        String no_cert_emisora = map_no_cert.get("numero_certificado").toString();
        return no_cert_emisora;
    }


    @Override
    public String getFicheroLlavePrivada(Integer id_empresa, Integer id_sucursal) {
        String sql_to_query = "select fac_cfds_conf.archivo_llave from fac_cfds_conf where fac_cfds_conf.empresa_id="+id_empresa+" AND fac_cfds_conf.gral_suc_id="+id_sucursal+";";
        Map<String, Object> map_archivo_llave = this.getJdbcTemplate().queryForMap(sql_to_query);
        String archivo_llave_emisora = map_archivo_llave.get("archivo_llave").toString();
        return archivo_llave_emisora;
    }

    @Override
    public String getPasswordLlavePrivada(Integer id_empresa, Integer id_sucursal) {
        String sql_to_query = "select fac_cfds_conf.password_llave from fac_cfds_conf where fac_cfds_conf.empresa_id="+id_empresa+" AND fac_cfds_conf.gral_suc_id="+id_sucursal+";";
        Map<String, Object> map_password_llave = this.getJdbcTemplate().queryForMap(sql_to_query);
        String password_llave_emisora = map_password_llave.get("password_llave").toString();
        return password_llave_emisora;
    }

    @Override
    public String getFicheroXsl(Integer id_empresa, Integer id_sucursal) {
        String sql_to_query = "select fac_cfds_conf.archivo_xsl from fac_cfds_conf where fac_cfds_conf.empresa_id="+id_empresa+" AND fac_cfds_conf.gral_suc_id="+id_sucursal+";";
        Map<String, Object> map_certificado = this.getJdbcTemplate().queryForMap(sql_to_query);
        String fichero_xsl = map_certificado.get("archivo_xsl").toString();
        return fichero_xsl;
    }
    
    @Override
    public String getFicheroXslCuentasContables(Integer id_empresa, Integer id_sucursal) {
        String sql_to_query = "select fac_cfds_conf.archivo_xsl_cadena_ctas_contables from fac_cfds_conf where fac_cfds_conf.empresa_id="+id_empresa+" AND fac_cfds_conf.gral_suc_id="+id_sucursal+";";
        Map<String, Object> map_certificado = this.getJdbcTemplate().queryForMap(sql_to_query);
        String fichero_xsl = map_certificado.get("archivo_xsl_cadena_ctas_contables").toString();
        return fichero_xsl;
    }
    
    @Override
    public String getFicheroXslBalanzaComprobacion(Integer id_empresa, Integer id_sucursal) {
        String sql_to_query = "select fac_cfds_conf.archivo_xsl_cadena_balanza_comprobacion from fac_cfds_conf where fac_cfds_conf.empresa_id="+id_empresa+" AND fac_cfds_conf.gral_suc_id="+id_sucursal+";";
        Map<String, Object> map_certificado = this.getJdbcTemplate().queryForMap(sql_to_query);
        String fichero_xsl = map_certificado.get("archivo_xsl_cadena_balanza_comprobacion").toString();
        return fichero_xsl;
    }
    
    @Override
    public String getFicheroXslTimbre(Integer id_empresa, Integer id_sucursal) {
        String sql_to_query = "select fac_cfds_conf.archivo_xsl_cadena_timbre from fac_cfds_conf where fac_cfds_conf.empresa_id="+id_empresa+" AND fac_cfds_conf.gral_suc_id="+id_sucursal+";";
        Map<String, Object> map_certificado = this.getJdbcTemplate().queryForMap(sql_to_query);
        String fichero_xsl = map_certificado.get("archivo_xsl_cadena_timbre").toString();
        return fichero_xsl;
    }

    @Override
    public String getFicheroXsdCfdi(Integer id_empresa, Integer id_sucursal) {
        String sql_to_query = "select fac_cfds_conf.archivo_xsd_cfdi from fac_cfds_conf where fac_cfds_conf.empresa_id="+id_empresa+" AND fac_cfds_conf.gral_suc_id="+id_sucursal+";";
        Map<String, Object> map = this.getJdbcTemplate().queryForMap(sql_to_query);
        String fichero = map.get("archivo_xsd_cfdi").toString();
        return fichero;
    }
    

    @Override
    public String getFicheroXsdXmlCuentasContables(Integer id_empresa, Integer id_sucursal) {
        String sql_to_query = "select fac_cfds_conf.archivo_xsd_ctas_contables from fac_cfds_conf where fac_cfds_conf.empresa_id="+id_empresa+" AND fac_cfds_conf.gral_suc_id="+id_sucursal+";";
        Map<String, Object> map = this.getJdbcTemplate().queryForMap(sql_to_query);
        String fichero = map.get("archivo_xsd_ctas_contables").toString();
        return fichero;
    }

    @Override
    public String getFicheroPfxTimbradoCfdi(Integer id_empresa, Integer id_sucursal) {
        String sql_to_query = "select fac_cfds_conf.ws_pfx_cert from fac_cfds_conf where fac_cfds_conf.empresa_id="+id_empresa+" AND fac_cfds_conf.gral_suc_id="+id_sucursal+";";
        Map<String, Object> map = this.getJdbcTemplate().queryForMap(sql_to_query);
        String fichero = map.get("ws_pfx_cert").toString();
        return fichero;
    }

    @Override
    public String getPasswdFicheroPfxTimbradoCfdi(Integer id_empresa, Integer id_sucursal) {
        String sql_to_query = "select fac_cfds_conf.passwd_ws_pfx from fac_cfds_conf where fac_cfds_conf.empresa_id="+id_empresa+" AND fac_cfds_conf.gral_suc_id="+id_sucursal+";";
        Map<String, Object> map = this.getJdbcTemplate().queryForMap(sql_to_query);
        String fichero = map.get("passwd_ws_pfx").toString();
        return fichero;
    }

    @Override
    public String getJavaVmDir(Integer id_empresa, Integer id_sucursal) {
        String sql_to_query = "select fac_cfds_conf.javavm_dir from fac_cfds_conf where fac_cfds_conf.empresa_id="+id_empresa+" AND fac_cfds_conf.gral_suc_id="+id_sucursal+";";
        Map<String, Object> map = this.getJdbcTemplate().queryForMap(sql_to_query);
        String fichero = map.get("javavm_dir").toString();
        return fichero;
    }

    @Override
    public String getJavaRutaCacerts(Integer id_empresa, Integer id_sucursal) {
        String sql_to_query = "select fac_cfds_conf.javavm_cacerts AS java_cacerts from fac_cfds_conf where fac_cfds_conf.empresa_id="+id_empresa+" AND fac_cfds_conf.gral_suc_id="+id_sucursal+";";
        Map<String, Object> map = this.getJdbcTemplate().queryForMap(sql_to_query);
        String fichero = map.get("java_cacerts").toString();
        return fichero;
    }

    @Override
    public String getUrlFicheroWsdlTimbradoCfdi(Integer id_empresa, Integer id_sucursal) {
        String sql_to_query = "select fac_cfds_conf.archivo_wsdl_timbrado_cfdi from fac_cfds_conf where fac_cfds_conf.empresa_id="+id_empresa+" AND fac_cfds_conf.gral_suc_id="+id_sucursal+";";
        Map<String, Object> map = this.getJdbcTemplate().queryForMap(sql_to_query);
        String fichero = map.get("archivo_wsdl_timbrado_cfdi").toString();
        return fichero;
    }

    //Este metodo es para obtener el usuario del contrato de servisim
    @Override
    public String getUserContrato(Integer id_empresa, Integer id_sucursal) {
        String sql_to_query = "select fac_cfds_conf.usuario from fac_cfds_conf where fac_cfds_conf.empresa_id="+id_empresa+" AND fac_cfds_conf.gral_suc_id="+id_sucursal+";";
        Map<String, Object> map = this.getJdbcTemplate().queryForMap(sql_to_query);
        String usuario = map.get("usuario").toString();
        return usuario;
    }
    
    //Este metodo es para obtener la contraseña asignada al usuario por servisim
    @Override
    public String getPasswordUserContrato(Integer id_empresa, Integer id_sucursal) {
        String sql_to_query = "select fac_cfds_conf.contrasena from fac_cfds_conf where fac_cfds_conf.empresa_id="+id_empresa+" AND fac_cfds_conf.gral_suc_id="+id_sucursal+";";
        Map<String, Object> map = this.getJdbcTemplate().queryForMap(sql_to_query);
        String contrasena = map.get("contrasena").toString();
        return contrasena;
    }
    
    @Override
    public String getFolioFactura(Integer id_empresa, Integer id_sucursal) {
        String sql_to_query = "select fac_cfds_conf_folios.folio_actual "
                + "from fac_cfds_conf "
                + "JOIN fac_cfds_conf_folios ON fac_cfds_conf_folios.fac_cfds_conf_id=fac_cfds_conf.id "
                + "where fac_cfds_conf_folios.proposito = 'FAC' "
                + "AND fac_cfds_conf.empresa_id="+id_empresa+" "
                + "AND fac_cfds_conf.gral_suc_id="+id_sucursal+";";
        Map<String, Object> map_folio_factura = this.getJdbcTemplate().queryForMap(sql_to_query);
        String folio_factura_emisora = map_folio_factura.get("folio_actual").toString();
        return folio_factura_emisora;
    }

    @Override
    public String getSerieFactura(Integer id_empresa, Integer id_sucursal) {
        String sql_to_query = "select fac_cfds_conf_folios.serie "
                + "from fac_cfds_conf "
                + "JOIN fac_cfds_conf_folios ON fac_cfds_conf_folios.fac_cfds_conf_id=fac_cfds_conf.id "
                + "where fac_cfds_conf_folios.proposito='FAC' "
                + "AND fac_cfds_conf.empresa_id="+id_empresa+" "
                + "AND fac_cfds_conf.gral_suc_id="+ id_sucursal+";";
        Map<String, Object> map_serie_factura = this.getJdbcTemplate().queryForMap(sql_to_query);
        String serie_factura_emisora = map_serie_factura.get("serie").toString();
        return serie_factura_emisora;
    }

    @Override
    public String getAnoAprobacionFactura(Integer id_empresa, Integer id_sucursal) {
        String sql_to_query = "select fac_cfds_conf_folios.ano_aprobacion "
                + "from fac_cfds_conf "
                + "JOIN fac_cfds_conf_folios ON fac_cfds_conf_folios.fac_cfds_conf_id=fac_cfds_conf.id "
                + "where fac_cfds_conf_folios.proposito = 'FAC' "
                + "AND fac_cfds_conf.empresa_id="+id_empresa+" "
                + "AND fac_cfds_conf.gral_suc_id="+id_sucursal+";";
        Map<String, Object> map_ano_aprobacion_factura = this.getJdbcTemplate().queryForMap(sql_to_query);
        String ano_aprobacion_factura_emisora = map_ano_aprobacion_factura.get("ano_aprobacion").toString();
        return ano_aprobacion_factura_emisora;
    }

    @Override
    public String getNoAprobacionFactura(Integer id_empresa, Integer id_sucursal) {
        String sql_to_query = "select fac_cfds_conf_folios.no_aprobacion "
                + "from fac_cfds_conf "
                + "JOIN fac_cfds_conf_folios ON fac_cfds_conf_folios.fac_cfds_conf_id=fac_cfds_conf.id "
                + "where fac_cfds_conf_folios.proposito = 'FAC' AND fac_cfds_conf.empresa_id="+id_empresa+" AND fac_cfds_conf.gral_suc_id="+id_sucursal+";";
        Map<String, Object> map_num_aprobacion_factura = this.getJdbcTemplate().queryForMap(sql_to_query);
        String num_aprobacion_factura_emisora = map_num_aprobacion_factura.get("no_aprobacion").toString();
        return num_aprobacion_factura_emisora;
    }

    @Override
    public String getSerieNotaCredito(Integer id_empresa, Integer id_sucursal) {
        String sql_to_query = "select fac_cfds_conf_folios.serie "
                + "from fac_cfds_conf "
                + "JOIN fac_cfds_conf_folios ON fac_cfds_conf_folios.fac_cfds_conf_id=fac_cfds_conf.id "
                + "where fac_cfds_conf_folios.proposito = 'NCR' AND fac_cfds_conf.empresa_id="+id_empresa+" AND fac_cfds_conf.gral_suc_id="+id_sucursal+";";
        Map<String, Object> map_serie_nota_credito = this.getJdbcTemplate().queryForMap(sql_to_query);
        String serie_nota_credito_emisora = map_serie_nota_credito.get("serie").toString();
        return serie_nota_credito_emisora;
    }

    @Override
    public String getFolioNotaCredito(Integer id_empresa, Integer id_sucursal) {
        String sql_to_query = "select fac_cfds_conf_folios.folio_actual "
                + "from fac_cfds_conf "
                + "JOIN fac_cfds_conf_folios ON fac_cfds_conf_folios.fac_cfds_conf_id=fac_cfds_conf.id "
                + "where fac_cfds_conf_folios.proposito = 'NCR' "
                + "AND fac_cfds_conf.empresa_id="+id_empresa+" AND fac_cfds_conf.gral_suc_id="+id_sucursal+";";
        Map<String, Object> map_folio_nota_credito = this.getJdbcTemplate().queryForMap(sql_to_query);
        String folio_nota_credito_emisora = map_folio_nota_credito.get("folio_actual").toString();
        return folio_nota_credito_emisora;
    }

    @Override
    public String getNoAprobacionNotaCredito(Integer id_empresa, Integer id_sucursal) {
        String sql_to_query = "select fac_cfds_conf_folios.no_aprobacion "
                + "from fac_cfds_conf "
                + "JOIN fac_cfds_conf_folios ON fac_cfds_conf_folios.fac_cfds_conf_id=fac_cfds_conf.id "
                + "where fac_cfds_conf_folios.proposito = 'NCR' "
                + "AND fac_cfds_conf.empresa_id="+id_empresa+" AND fac_cfds_conf.gral_suc_id="+id_sucursal+";";
        Map<String, Object> map_num_aprobacion_nota_credito = this.getJdbcTemplate().queryForMap(sql_to_query);
        String num_aprobacion_nota_credito_emisora = map_num_aprobacion_nota_credito.get("no_aprobacion").toString();
        return num_aprobacion_nota_credito_emisora;
    }

    @Override
    public String getAnoAprobacionNotaCredito(Integer id_empresa, Integer id_sucursal) {
        String sql_to_query = "select fac_cfds_conf_folios.ano_aprobacion "
                + "from fac_cfds_conf "
                + "JOIN fac_cfds_conf_folios ON fac_cfds_conf_folios.fac_cfds_conf_id=fac_cfds_conf.id "
                + "where fac_cfds_conf_folios.proposito = 'NCR' "
                + "AND fac_cfds_conf.empresa_id="+id_empresa+" AND fac_cfds_conf.gral_suc_id="+id_sucursal+";";
        Map<String, Object> map_ano_aprobacion_nota_credito = this.getJdbcTemplate().queryForMap(sql_to_query);
        String ano_aprobacion_nota_credito_emisora = map_ano_aprobacion_nota_credito.get("ano_aprobacion").toString();
        return ano_aprobacion_nota_credito_emisora;
    }

    @Override
    public String getSerieNotaCargo(Integer id_empresa, Integer id_sucursal) {
        String sql_to_query = "select fac_cfds_conf_folios.serie  "
                + "from fac_cfds_conf  "
                + "JOIN fac_cfds_conf_folios ON fac_cfds_conf_folios.fac_cfds_conf_id=fac_cfds_conf.id  "
                + "where fac_cfds_conf_folios.proposito = 'NCA'  "
                + "AND fac_cfds_conf.empresa_id="+id_empresa+" AND fac_cfds_conf.gral_suc_id="+id_sucursal+";";
        Map<String, Object> map_serie_nota_cargo = this.getJdbcTemplate().queryForMap(sql_to_query);
        String serie_nota_cargo_emisora = map_serie_nota_cargo.get("serie").toString();
        return serie_nota_cargo_emisora;
    }

    @Override
    public String getFolioNotaCargo(Integer id_empresa, Integer id_sucursal) {
        String sql_to_query = "select fac_cfds_conf_folios.folio_actual "
                + "from fac_cfds_conf "
                + "JOIN fac_cfds_conf_folios ON fac_cfds_conf_folios.fac_cfds_conf_id=fac_cfds_conf.id "
                + "where fac_cfds_conf_folios.proposito = 'NCA' "
                + "AND fac_cfds_conf.empresa_id="+id_empresa+" AND fac_cfds_conf.gral_suc_id="+id_sucursal+";";
        Map<String, Object> map_folio_nota_cargo = this.getJdbcTemplate().queryForMap(sql_to_query);
        String folio_nota_cargo_emisora = map_folio_nota_cargo.get("folio_actual").toString();
        return folio_nota_cargo_emisora;
    }

    @Override
    public String getAnoAprobacionNotaCargo(Integer id_empresa, Integer id_sucursal) {
        String sql_to_query = "select fac_cfds_conf_folios.ano_aprobacion "
                + "from fac_cfds_conf "
                + "JOIN fac_cfds_conf_folios ON fac_cfds_conf_folios.fac_cfds_conf_id=fac_cfds_conf.id "
                + "where fac_cfds_conf_folios.proposito='NCA' "
                + "AND fac_cfds_conf.empresa_id="+id_empresa+" AND fac_cfds_conf.gral_suc_id="+id_sucursal+";";
        Map<String, Object> map_ano_aprobacion_nota_cargo = this.getJdbcTemplate().queryForMap(sql_to_query);
        String ano_aprobacion_nota_cargo_emisora = map_ano_aprobacion_nota_cargo.get("ano_aprobacion").toString();
        return ano_aprobacion_nota_cargo_emisora;
    }

    @Override
    public String getNoAprobacionNotaCargo(Integer id_empresa, Integer id_sucursal) {
        String sql_to_query = "select fac_cfds_conf_folios.no_aprobacion "
                + "from fac_cfds_conf "
                + "JOIN fac_cfds_conf_folios ON fac_cfds_conf_folios.fac_cfds_conf_id=fac_cfds_conf.id "
                + "where fac_cfds_conf_folios.proposito = 'NCA' "
                + "AND fac_cfds_conf.empresa_id="+id_empresa+" AND fac_cfds_conf.gral_suc_id="+id_sucursal+";";
        Map<String, Object> map_num_aprobacion_nota_cargo = this.getJdbcTemplate().queryForMap(sql_to_query);
        String num_aprobacion_nota_cargo_emisora = map_num_aprobacion_nota_cargo.get("no_aprobacion").toString();
        return num_aprobacion_nota_cargo_emisora;
    }

    @Override
    public void actualizarFolioFactura(Integer id_empresa, Integer id_sucursal) {
        String sql_to_query = "select fac_cfds_conf_folios.id "
                + "from fac_cfds_conf "
                + "JOIN fac_cfds_conf_folios ON fac_cfds_conf_folios.fac_cfds_conf_id=fac_cfds_conf.id "
                + "where fac_cfds_conf_folios.proposito='FAC' "
                + "AND fac_cfds_conf.empresa_id="+id_empresa+" AND fac_cfds_conf.gral_suc_id="+id_sucursal+";";
        int id_fac_cfds_conf_folios = this.getJdbcTemplate().queryForInt(sql_to_query);

        String sql_to_query_update = "UPDATE fac_cfds_conf_folios SET folio_actual=(folio_actual+1) where id="+id_fac_cfds_conf_folios;
        this.getJdbcTemplate().execute(sql_to_query_update);
    }

    @Override
    public void actualizarFolioNotaCredito(Integer id_empresa, Integer id_sucursal) {
        String sql_to_query = "select fac_cfds_conf_folios.id "
                + "from fac_cfds_conf "
                + "JOIN fac_cfds_conf_folios ON fac_cfds_conf_folios.fac_cfds_conf_id=fac_cfds_conf.id "
                + "where fac_cfds_conf_folios.proposito = 'NCR' "
                + "AND fac_cfds_conf.empresa_id="+id_empresa+" AND fac_cfds_conf.gral_suc_id="+id_sucursal+";";
        int id_fac_cfds_conf_folios = this.getJdbcTemplate().queryForInt(sql_to_query);

        String sql_to_query_update = "UPDATE fac_cfds_conf_folios SET folio_actual = folio_actual+1 where id = "+id_fac_cfds_conf_folios;
        this.getJdbcTemplate().execute(sql_to_query_update);
    }

    @Override
    public void actualizarFolioNotaCargo(Integer id_empresa, Integer id_sucursal) {
        String sql_to_query = "select fac_cfds_conf_folios.id "
                + "from fac_cfds_conf "
                + "JOIN fac_cfds_conf_folios ON fac_cfds_conf_folios.fac_cfds_conf_id=fac_cfds_conf.id "
                + "where fac_cfds_conf_folios.proposito='NCA' "
                + "AND fac_cfds_conf.empresa_id="+id_empresa+" AND fac_cfds_conf.gral_suc_id="+id_sucursal+";";
        int id_fac_cfds_conf_folios = this.getJdbcTemplate().queryForInt(sql_to_query);

        String sql_to_query_update = "UPDATE fac_cfds_conf_folios SET folio_actual = folio_actual+1 where id = "+id_fac_cfds_conf_folios;
        this.getJdbcTemplate().execute(sql_to_query_update);
    }
    
    @Override
    public String getSerieFacNomina(Integer id_empresa, Integer id_sucursal) {
        String sql_to_query = "select fac_cfds_conf_folios.serie from fac_cfds_conf JOIN fac_cfds_conf_folios ON fac_cfds_conf_folios.fac_cfds_conf_id=fac_cfds_conf.id where lower(trim(fac_cfds_conf_folios.proposito))='nom' AND fac_cfds_conf.empresa_id="+id_empresa+" AND fac_cfds_conf.gral_suc_id="+id_sucursal+";";
        Map<String, Object> map_serie_nota_cargo = this.getJdbcTemplate().queryForMap(sql_to_query);
        String serie_nota_cargo_emisora = map_serie_nota_cargo.get("serie").toString();
        return serie_nota_cargo_emisora;
    }

    @Override
    public String getFolioFacNomina(Integer id_empresa, Integer id_sucursal) {
        String sql_to_query = "select fac_cfds_conf_folios.folio_actual from fac_cfds_conf JOIN fac_cfds_conf_folios ON fac_cfds_conf_folios.fac_cfds_conf_id=fac_cfds_conf.id where lower(trim(fac_cfds_conf_folios.proposito))='nom' AND fac_cfds_conf.empresa_id="+id_empresa+" AND fac_cfds_conf.gral_suc_id="+id_sucursal+";";
        Map<String, Object> map_folio_nota_cargo = this.getJdbcTemplate().queryForMap(sql_to_query);
        String folio_nota_cargo_emisora = map_folio_nota_cargo.get("folio_actual").toString();
        return folio_nota_cargo_emisora;
    }

    @Override
    public String getNoIdEmpresa(Integer id_empresa) {
        String sql_to_query = "select no_id from gral_emp where gral_emp.id ="+id_empresa+";";
        Map<String, Object> map_calle = this.getJdbcTemplate().queryForMap(sql_to_query);
        String no_id = map_calle.get("no_id").toString();
        return no_id;
    }

    @Override
    public String getCalleDomicilioFiscalEmpresaEmisora(Integer id_empresa) {
        String sql_to_query = "select calle from gral_emp where gral_emp.id ="+id_empresa;
        Map<String, Object> map_calle = this.getJdbcTemplate().queryForMap(sql_to_query);
        String calle_emisora = map_calle.get("calle").toString();
        return calle_emisora;
    }

    @Override
    public String getCpDomicilioFiscalEmpresaEmisora(Integer id_empresa) {
        String sql_to_query = "select cp from gral_emp where gral_emp.id ="+id_empresa;
        Map<String, Object> map_cp = this.getJdbcTemplate().queryForMap(sql_to_query);
        String cp_emisora = map_cp.get("cp").toString();
        return cp_emisora;
    }

    @Override
    public String getColoniaDomicilioFiscalEmpresaEmisora(Integer id_empresa) {
        String sql_to_query = "select colonia from gral_emp where gral_emp.id ="+id_empresa;
        Map<String, Object> map_colonia = this.getJdbcTemplate().queryForMap(sql_to_query);
        String colonia_emisora = map_colonia.get("colonia").toString();
        return colonia_emisora;
    }

    @Override
    public String getLocalidadDomicilioFiscalEmpresaEmisora(Integer id_empresa) {
        String localidad_emisora = "";
        return localidad_emisora;
    }

    @Override
    public String getMunicipioDomicilioFiscalEmpresaEmisora(Integer id_empresa) {
        //obtener nombre del municipio
        String sql_to_query_mun = "select gral_mun.titulo from gral_emp JOIN gral_mun ON gral_mun.id = gral_emp.municipio_id where gral_emp.id ="+id_empresa;
        Map<String, Object> map_municipio = this.getJdbcTemplate().queryForMap(sql_to_query_mun);
        String municipio_emisora = map_municipio.get("titulo").toString();

        return municipio_emisora;
    }

    @Override
    public String getEstadoDomicilioFiscalEmpresaEmisora(Integer id_empresa) {
        //obtener nombre del estado
        String sql_query_estado = "select gral_edo.titulo from gral_emp JOIN gral_edo ON gral_edo.id = gral_emp.estado_id where gral_emp.id ="+id_empresa;
        Map<String, Object> map_estado = this.getJdbcTemplate().queryForMap(sql_query_estado);
        String estado_emisora = map_estado.get("titulo").toString();

        return estado_emisora;
    }

    @Override
    public String getPaisDomicilioFiscalEmpresaEmisora(Integer id_empresa) {
        //obtener nombre del pais
        String sql_query_pais = "select gral_pais.titulo from gral_emp JOIN gral_pais ON gral_pais.id = gral_emp.pais_id where gral_emp.id ="+id_empresa;
        Map<String, Object> map_pais = this.getJdbcTemplate().queryForMap(sql_query_pais);
        String pais_emisora = map_pais.get("titulo").toString();
        return pais_emisora;
    }

    @Override
    public String getNoExteriorDomicilioFiscalEmpresaEmisora(Integer id_empresa) {
        String sql_to_query = "select numero_exterior from gral_emp  where id ="+id_empresa;
        Map<String, Object> map_numero = this.getJdbcTemplate().queryForMap(sql_to_query);
        String numero_emisora = map_numero.get("numero_exterior").toString();
        return numero_emisora;
    }

    @Override
    public String getNoInteriorDomicilioFiscalEmpresaEmisora(Integer id_empresa) {
        String numero_emisora = "";
        return numero_emisora;
    }

    @Override
    public String getReferenciaDomicilioFiscalEmpresaEmisora(Integer id_empresa) {
        String referencia_emisora = "";
        return referencia_emisora;
    }

    @Override
    public String getTelefonoEmpresaEmisora(Integer id_empresa) {
        String sql_to_query = "select telefono from gral_emp where id ="+id_empresa;
        Map<String, Object> map_regimen = this.getJdbcTemplate().queryForMap(sql_to_query);
        String tel_emisora = map_regimen.get("telefono").toString();
        return tel_emisora;
    }

    @Override
    public String getPaginaWebEmpresaEmisora(Integer id_empresa) {
        String sql_to_query = "select pagina_web from gral_emp where id ="+id_empresa;
        Map<String, Object> map_pagina_web = this.getJdbcTemplate().queryForMap(sql_to_query);
        String pagina_web = map_pagina_web.get("pagina_web").toString();
        return pagina_web;
    }

    @Override
    public String getPaisSucursalEmisora(Integer id_sucursal) {
        //obtener nombre del pais
        String sql_query_pais = "select gral_pais.titulo from gral_suc JOIN gral_pais ON gral_pais.id = gral_suc.gral_pais_id where gral_suc.id="+id_sucursal;
        Map<String, Object> map_pais = this.getJdbcTemplate().queryForMap(sql_query_pais);
        String pais_suc = map_pais.get("titulo").toString();
        return pais_suc;
    }

    @Override
    public String getEstadoSucursalEmisora(Integer id_sucursal) {
        //obtener nombre del estado
        String sql_query_estado = "select gral_edo.titulo from gral_suc JOIN gral_edo ON gral_edo.id = gral_suc.gral_edo_id where gral_suc.id="+id_sucursal;
        Map<String, Object> map_estado = this.getJdbcTemplate().queryForMap(sql_query_estado);
        String estado_suc = map_estado.get("titulo").toString();

        return estado_suc;
    }

    @Override
    public String getMunicipioSucursalEmisora(Integer id_sucursal) {
        //obtener nombre del municipio
        String sql_to_query_mun = "select gral_mun.titulo from gral_suc JOIN gral_mun ON gral_mun.id = gral_suc.gral_mun_id where gral_suc.id="+id_sucursal;
        Map<String, Object> map_municipio = this.getJdbcTemplate().queryForMap(sql_to_query_mun);
        String municipio_suc = map_municipio.get("titulo").toString();

        return municipio_suc;
    }

    //obtiene codigo1 del iso para reporte
    @Override
    public String getCodigo1Iso(Integer id_empresa, Integer id_app) {
        String valor_retorno="";
        String sql_busqueda = "select count(gral_docs_conf.valor)  "
                + "from gral_docs "
                + "JOIN gral_docs_conf ON gral_docs_conf.gral_doc_id=gral_docs.id "
                + "where gral_docs.gral_emp_id="+id_empresa +" "
                + "AND gral_docs.gral_app_id="+id_app +" "
                + "AND gral_docs_conf.campo='CODIGO1'";
        //esto es para revisar que exista el registro
        int rowCount = this.getJdbcTemplate().queryForInt(sql_busqueda);
        
        //si rowCount es mayor que cero si se encontro registro y extraemos el valor
        if (rowCount>0){
            String sql_to_query = "select gral_docs_conf.valor "
                + "from gral_docs "
                + "JOIN gral_docs_conf ON gral_docs_conf.gral_doc_id=gral_docs.id "
                + "where gral_docs.gral_emp_id="+id_empresa +" "
                + "AND gral_docs.gral_app_id="+id_app +" "
                + "AND gral_docs_conf.campo='CODIGO1'";
            Map<String, Object> map = this.getJdbcTemplate().queryForMap(sql_to_query);
            valor_retorno = map.get("valor").toString();
        }
        
        return valor_retorno;
    }
    
    @Override
    public String getCodigo2Iso(Integer id_empresa, Integer id_app) {
        String valor_retorno="";
        String sql_busqueda = "";
        String sql_to_query="";
        
        sql_busqueda = "select count(gral_docs_conf.valor)  "
                + "from gral_docs "
                + "JOIN gral_docs_conf ON gral_docs_conf.gral_doc_id=gral_docs.id "
                + "where gral_docs.gral_emp_id="+id_empresa +" "
                + "AND gral_docs.gral_app_id="+id_app +" "
                + "AND gral_docs_conf.campo='CODIGO2'";
        //esto es para revisar que exista el registro
        int rowCount = this.getJdbcTemplate().queryForInt(sql_busqueda);

        //si rowCount es mayor que cero si se encontro registro y extraemos el valor
        if (rowCount>0){
            sql_to_query = "select gral_docs_conf.valor "
                + "from gral_docs "
                + "JOIN gral_docs_conf ON gral_docs_conf.gral_doc_id=gral_docs.id "
                + "where gral_docs.gral_emp_id="+id_empresa +" "
                + "AND gral_docs.gral_app_id="+id_app +" "
                + "AND gral_docs_conf.campo='CODIGO2'";
            Map<String, Object> map = this.getJdbcTemplate().queryForMap(sql_to_query);
            valor_retorno = map.get("valor").toString();
        }

        return valor_retorno;
    }

    @Override
    public String getTituloReporte(Integer id_empresa, Integer id_app) {
        String valor_retorno="";
        String sql_busqueda = "";
        String sql_to_query="";
        
        sql_busqueda = "select count(gral_docs.titulo) from gral_docs where gral_docs.gral_emp_id="+id_empresa +" AND gral_docs.gral_app_id="+id_app+";";
        
        //esto es para revisar que exista el registro
        int rowCount = this.getJdbcTemplate().queryForInt(sql_busqueda);
        
        //si rowCount es mayor que cero si se encontro registro y extraemos el valor
        if (rowCount>0){
            sql_to_query = "select gral_docs.titulo from gral_docs where gral_docs.gral_emp_id="+id_empresa +" AND gral_docs.gral_app_id="+id_app+";";
            Map<String, Object> map = this.getJdbcTemplate().queryForMap(sql_to_query);
            valor_retorno = map.get("titulo").toString();
        }
        
        return valor_retorno;
    }

    //metodos  de uso general
    @Override
    public HashMap<String, String> selectFunctionValidateAaplicativo(String data, Integer idApp, String extra_data_array) {
        String sql_to_query = "select erp_fn_validaciones_por_aplicativo from erp_fn_validaciones_por_aplicativo('"+data+"',"+idApp+",array["+extra_data_array+"]);";

        HashMap<String, String> hm = (HashMap<String, String>) this.jdbcTemplate.queryForObject(
            sql_to_query,
            new Object[]{}, new RowMapper() {
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    HashMap<String, String> row = new HashMap<String, String>();
                    row.put("success",rs.getString("erp_fn_validaciones_por_aplicativo"));
                    return row;
                }
            }
        );
        return hm;
    }

    @Override
    public String selectFunctionForThisApp(String campos_data, String extra_data_array) {
        String sql_to_query = "select * from gral_adm_catalogos('"+campos_data+"',array["+extra_data_array+"]);";

        String valor_retorno="";
        Map<String, Object> update = this.getJdbcTemplate().queryForMap(sql_to_query);
        valor_retorno = update.get("gral_adm_catalogos").toString();
        return valor_retorno;
    }

    @Override
    public int countAll(String data_string) {
        String sql_busqueda = "select id from gral_bus_catalogos('"+data_string+"') as foo (id integer)";
        String sql_to_query = "select count(id)::int as total from ("+sql_busqueda+") as subt";

        int rowCount = this.getJdbcTemplate().queryForInt(sql_to_query);
        return rowCount;
    }

    //metodos para el Catalogo de Puestos
    ///guarda los datos de los puestos
    @Override
    public ArrayList<HashMap<String, String>> getPuesto_Datos(Integer id) {

        String sql_to_query = "select id,titulo as puesto from gral_puestos where id="+id;

        ArrayList<HashMap<String, String>> dato_puesto = (ArrayList<HashMap<String, String>>) this.jdbcTemplate.query(
            sql_to_query,
            new Object[]{}, new RowMapper(){
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    HashMap<String, String> row = new HashMap<String, String>();
                    row.put("id",String.valueOf(rs.getInt("id")));
                    row.put("puesto",rs.getString("puesto"));
                    return row;
                }
            }
        );
        return dato_puesto;
    }

    @Override
    public ArrayList<HashMap<String, Object>> getPuestos_PaginaGrid(String data_string, int offset, int pageSize, String orderBy, String asc) {
        String sql_busqueda = "select id from gral_bus_catalogos(?) as foo (id integer)";

	String sql_to_query = ""
        + "select gral_puestos.id, gral_puestos.titulo as puesto "
        +"from gral_puestos "
        +"JOIN ("+sql_busqueda+") AS sbt ON sbt.id = gral_puestos.id "
        +"where gral_puestos.borrado_logico=false "
        +"order by "+orderBy+" "+asc+" limit ? OFFSET ?";

        ArrayList<HashMap<String, Object>> hm = (ArrayList<HashMap<String, Object>>) this.jdbcTemplate.query(
            sql_to_query,
            new Object[]{data_string, new Integer(pageSize),new Integer(offset)}, new RowMapper() {
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    HashMap<String, Object> row = new HashMap<String, Object>();
                    row.put("id",String.valueOf(rs.getInt("id")));
                    row.put("titulo",rs.getString("puesto"));
                    return row;
                }
            }
        );
        return hm;
    }

    //METODOS DEL CATALOGO DE EMPLEADOS
    @Override
    public ArrayList<HashMap<String, Object>> getEmpleados_PaginaGrid(String data_string, int offset, int pageSize, String orderBy, String asc) {
        String sql_busqueda = "select id from gral_bus_catalogos(?) as foo (id integer)";

	String sql_to_query = "select "
				+"gral_empleados.id, "
				+"gral_empleados.clave, "
				+"gral_empleados.nombre_pila ||' '||gral_empleados.apellido_paterno||' '||gral_empleados.apellido_materno as nombre_empleado, "
				+"gral_empleados.curp, "
                                +"gral_puestos.titulo "

                                +"from gral_empleados "
                                +"JOIN gral_puestos on gral_puestos.id=gral_empleados.gral_puesto_id "
                                +"JOIN ("+sql_busqueda+") as subt on subt.id=gral_empleados.id "
                                +"order by "+orderBy+" "+asc+" limit ? OFFSET ? ";

        ArrayList<HashMap<String, Object>> hm = (ArrayList<HashMap<String, Object>>) this.jdbcTemplate.query(
            sql_to_query,
            new Object[]{data_string, new Integer(pageSize),new Integer(offset)}, new RowMapper() {
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    HashMap<String, Object> row = new HashMap<String, Object>();
                    row.put("id",rs.getInt("id"));
                    row.put("clave",rs.getString("clave"));
                    row.put("nombre_empleado",rs.getString("nombre_empleado"));
                    row.put("curp",rs.getString("curp"));
                    row.put("titulo",rs.getString("titulo"));
                    return row;
                }
            }
        );
        return hm;
    }


    //muestra los datos al momento de editar
    @Override
    public ArrayList<HashMap<String, Object>> getEmpleados_Datos(Integer id) {

        String sql_query = ""
            + "select gral_empleados.id as empleado_id, "
                +"gral_empleados.clave, "
                +"gral_empleados.nombre_pila, "
                +"gral_empleados.apellido_paterno, "
                +"gral_empleados.apellido_materno, "
                +"gral_empleados.imss, "
                +"gral_empleados.infonavit, "
                +"gral_empleados.curp, "
                +"gral_empleados.rfc, "
                +"to_char(gral_empleados.fecha_nacimiento,'yyyy-mm-dd')as fecha_nacimiento, "
                +"to_char(gral_empleados.fecha_ingreso,'yyyy-mm-dd') as fecha_ingreso, "
                +"gral_empleados.gral_escolaridad_id, "
                +"gral_empleados.gral_sexo_id, "
                +"gral_empleados.gral_civil_id, "
                +"gral_empleados.gral_religion_id, "
                +"gral_empleados.gral_sangretipo_id, "
                +"gral_empleados.gral_puesto_id, "
                +"gral_empleados.gral_suc_id_empleado, "
                +"gral_empleados.gral_categ_id, "
                +"gral_empleados.telefono, "
                +"gral_empleados.telefono_movil, "
                +"gral_empleados.correo_personal, "
                +"gral_empleados.gral_pais_id, "
                +"gral_empleados.gral_edo_id, "
                +"gral_empleados.gral_mun_id, "
                +"gral_empleados.calle, "
                +"gral_empleados.numero, "
                +"gral_empleados.colonia, "
                +"gral_empleados.cp, "
                +"gral_empleados.contacto_emergencia, "
                +"gral_empleados.telefono_emergencia, "
                +"gral_empleados.enfermedades, "
                +"gral_empleados.alergias, "
                +"gral_empleados.comentarios, "
                +"(case when gral_usr.username is null then '' else gral_usr.username end) AS username,"
                +"(case when gral_usr.password is null then '' else gral_usr.password end) AS password,"
                +"gral_usr.enabled, "
                +"gral_usr.id as id_usuario, "
                +"gral_empleados.comision_agen, "
                +"gral_empleados.region_id_agen, "
                +"gral_empleados.comision2_agen, "
                +"gral_empleados.comision3_agen, "
                +"gral_empleados.comision4_agen, "
                +"gral_empleados.dias_tope_comision, "
                +"gral_empleados.dias_tope_comision2, "
                +"gral_empleados.dias_tope_comision3, "
                +"gral_empleados.monto_tope_comision, "
                +"gral_empleados.monto_tope_comision2, "
                +"gral_empleados.monto_tope_comision3, "
                +"gral_empleados.tipo_comision,"
                +"gral_empleados.correo_empresa, "
                +"gral_empleados.no_int, "
                +"gral_empleados.nom_regimen_contratacion_id AS regimen_id, "
                +"gral_empleados.nom_periodicidad_pago_id AS periodo_pago_id, "
                +"gral_empleados.nom_riesgo_puesto_id AS riesgo_id, "
                +"gral_empleados.nom_tipo_contrato_id AS tipo_contrato_id, "
                +"gral_empleados.nom_tipo_jornada_id AS tipo_jornada_id, "
                +"gral_empleados.tes_ban_id AS banco_id, "
                +"gral_empleados.clabe,"
                +"gral_empleados.salario_base,"
                +"gral_empleados.salario_integrado AS salario_int, "
                +"gral_empleados.registro_patronal AS reg_patronal,"
                +"gral_empleados.genera_nomina,"
                +"gral_empleados.gral_depto_id AS depto_id "
            +"from gral_empleados "
            +"left join  gral_usr on gral_usr.gral_empleados_id=gral_empleados.id "
            +"where gral_empleados.borrado_logico=false AND gral_empleados.id=?;";
          
        ArrayList<HashMap<String, Object>> empleado = (ArrayList<HashMap<String, Object>>) this.jdbcTemplate.query(
            sql_query,
            new Object[]{new Integer(id)}, new RowMapper() {
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    HashMap<String, Object> row = new HashMap<String, Object>();
                    row.put("empleado_id",rs.getInt("empleado_id"));
                    row.put("clave",rs.getString("clave"));
                    row.put("nombre_pila",rs.getString("nombre_pila"));
                    row.put("apellido_paterno",rs.getString("apellido_paterno"));
                    row.put("apellido_materno",rs.getString("apellido_materno"));
                    row.put("imss",rs.getString("imss"));
                    row.put("infonavit",rs.getString("infonavit"));
                    row.put("curp",rs.getString("curp"));
                    row.put("rfc",rs.getString("rfc"));
                    row.put("fecha_nacimiento",rs.getString("fecha_nacimiento"));
                    row.put("fecha_ingreso",rs.getString("fecha_ingreso"));
                    row.put("gral_escolaridad_id",rs.getString("gral_escolaridad_id"));
                    row.put("gral_sexo_id",rs.getString("gral_sexo_id"));
                    row.put("gral_civil_id",rs.getString("gral_civil_id"));
                    row.put("gral_religion_id",rs.getString("gral_religion_id"));
                    row.put("gral_sangretipo_id",rs.getString("gral_sangretipo_id"));
                    row.put("gral_puesto_id",rs.getString("gral_puesto_id"));
                    row.put("gral_suc_id_empleado",rs.getString("gral_suc_id_empleado"));
                    row.put("gral_categ_id",rs.getString("gral_categ_id"));
                    row.put("telefono",rs.getString("telefono"));
                    row.put("telefono_movil",rs.getString("telefono_movil"));
                    row.put("correo_personal",rs.getString("correo_personal"));
                    row.put("gral_pais_id",rs.getString("gral_pais_id"));
                    row.put("gral_edo_id",rs.getString("gral_edo_id"));
                    row.put("gral_mun_id",rs.getString("gral_mun_id"));
                    row.put("calle",rs.getString("calle"));
                    row.put("numero",rs.getString("numero"));
                    row.put("colonia",rs.getString("colonia"));
                    row.put("cp",rs.getString("cp"));
                    row.put("contacto_emergencia",rs.getString("contacto_emergencia"));
                    row.put("telefono_emergencia",rs.getString("telefono_emergencia"));
                    row.put("enfermedades",rs.getString("enfermedades"));
                    row.put("alergias",rs.getString("alergias"));
                    row.put("comentarios",rs.getString("comentarios"));
                    row.put("username",rs.getString("username"));
                    row.put("password",rs.getString("password"));
                    row.put("enabled",String.valueOf(rs.getBoolean("enabled")));
                    row.put("id_usuario",rs.getInt("id_usuario"));
                    row.put("region_id_agen",rs.getInt("region_id_agen"));
                    row.put("comision_agen",StringHelper.roundDouble(rs.getDouble("comision_agen"),2));
                    row.put("comision2_agen",StringHelper.roundDouble(rs.getDouble("comision2_agen"),2));
                    row.put("comision3_agen",StringHelper.roundDouble(rs.getDouble("comision3_agen"),2));
                    row.put("comision4_agen",StringHelper.roundDouble(rs.getDouble("comision4_agen"),2));
                    row.put("dias_tope_comision",StringHelper.roundDouble(rs.getDouble("dias_tope_comision"),2));
                    row.put("dias_tope_comision2",StringHelper.roundDouble(rs.getDouble("dias_tope_comision2"),2));
                    row.put("dias_tope_comision3",StringHelper.roundDouble(rs.getDouble("dias_tope_comision3"),2));
                    row.put("monto_tope_comision",StringHelper.roundDouble(rs.getDouble("monto_tope_comision"),2));
                    row.put("monto_tope_comision2",StringHelper.roundDouble(rs.getDouble("monto_tope_comision2"),2));
                    row.put("monto_tope_comision3",StringHelper.roundDouble(rs.getDouble("monto_tope_comision3"),2));
                    row.put("tipo_comision",rs.getInt("tipo_comision"));
                    row.put("correo_empresa",rs.getString("correo_empresa"));
                    row.put("depto_id",String.valueOf(rs.getInt("depto_id")));
                    
                    row.put("no_int",rs.getString("no_int"));
                    row.put("regimen_id",String.valueOf(rs.getInt("regimen_id")));
                    row.put("periodo_pago_id",String.valueOf(rs.getInt("periodo_pago_id")));
                    row.put("riesgo_id",String.valueOf(rs.getInt("riesgo_id")));
                    row.put("tipo_contrato_id",String.valueOf(rs.getInt("tipo_contrato_id")));
                    row.put("tipo_jornada_id",String.valueOf(rs.getInt("tipo_jornada_id")));
                    row.put("banco_id",String.valueOf(rs.getInt("banco_id")));
                    row.put("clabe",rs.getString("clabe"));
                    row.put("salario_base",StringHelper.roundDouble(rs.getDouble("salario_base"),2));
                    row.put("salario_int",StringHelper.roundDouble(rs.getDouble("salario_int"),2));
                    row.put("reg_patronal",rs.getString("reg_patronal"));
                    row.put("genera_nomina",String.valueOf(rs.getBoolean("genera_nomina")));
                    
                    return row;
                }
            }
        );
        return empleado;
    }

    @Override
    public ArrayList<HashMap<String, Object>> getPaises() {
        String sql_to_query = "select DISTINCT id as cve_pais, titulo as pais_ent from gral_pais;";

        ArrayList<HashMap<String, Object>> pais = (ArrayList<HashMap<String, Object>>) this.jdbcTemplate.query(
            sql_to_query,
            new Object[]{}, new RowMapper(){
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    HashMap<String, Object> row = new HashMap<String, Object>();
                    row.put("cve_pais",rs.getString("cve_pais"));
                    row.put("pais_ent",rs.getString("pais_ent"));
                    return row;
                }
            }
        );
        return pais;
    }



    @Override
    public ArrayList<HashMap<String, Object>> getEntidadesForThisPais(String id_pais) {
        String sql_to_query = "select id as cve_ent, titulo as nom_ent from gral_edo where pais_id="+id_pais+" order by nom_ent;";
        ArrayList<HashMap<String, Object>> hm = (ArrayList<HashMap<String, Object>>) this.jdbcTemplate.query(
            sql_to_query,
            new Object[]{}, new RowMapper(){
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    HashMap<String, Object> row = new HashMap<String, Object>();
                    row.put("cve_ent",rs.getString("cve_ent"));
                    row.put("nom_ent",rs.getString("nom_ent"));
                    return row;
                }
            }
        );
        return hm;
    }



    @Override
    public ArrayList<HashMap<String, Object>> getLocalidadesForThisEntidad(String id_pais, String id_entidad) {
        String sql_to_query = "select id as cve_mun, titulo as nom_mun from gral_mun where estado_id="+id_entidad+" and pais_id="+id_pais+" order by nom_mun;";

        ArrayList<HashMap<String, Object>> hm = (ArrayList<HashMap<String, Object>>) this.jdbcTemplate.query(
            sql_to_query,
            new Object[]{}, new RowMapper(){
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    HashMap<String, Object> row = new HashMap<String, Object>();
                    row.put("cve_mun",rs.getString("cve_mun"));
                    row.put("nom_mun",rs.getString("nom_mun"));
                    return row;
                }
            }
        );
        return hm;
    }

    //alimenta select de tipo de escolaridad
    @Override
    public ArrayList<HashMap<String, Object>> getEscolaridad(Integer id_empresa) {

        String sql_to_query = "select id,titulo from gral_escolaridads where gral_emp_id="+id_empresa+" order by titulo";

        ArrayList<HashMap<String, Object>> escolaridad = (ArrayList<HashMap<String, Object>>) this.jdbcTemplate.query(
            sql_to_query,
            new Object[]{}, new RowMapper(){
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    HashMap<String, Object> row = new HashMap<String, Object>();
                    row.put("id",rs.getString("id"));
                    row.put("titulo",rs.getString("titulo"));
                    return row;
                }
            }
        );
        return escolaridad;
    }

    @Override
    public ArrayList<HashMap<String, Object>> getGeneroSexual() {
        //String sql_to_query = "select DISTINCT cve_pais ,pais_ent from municipios;";
        String sql_to_query = "select id,titulo from gral_sexos order by titulo";

        ArrayList<HashMap<String, Object>> generosexual = (ArrayList<HashMap<String, Object>>) this.jdbcTemplate.query(
            sql_to_query,
            new Object[]{}, new RowMapper(){
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    HashMap<String, Object> row = new HashMap<String, Object>();
                    row.put("id",rs.getString("id"));
                    row.put("titulo",rs.getString("titulo"));
                    return row;
                }
            }
        );
        return generosexual;
    }

    @Override
    public ArrayList<HashMap<String, Object>> getEdoCivil() {
        String sql_to_query = "select id,titulo from gral_civils order by titulo";

        ArrayList<HashMap<String, Object>> edocivil = (ArrayList<HashMap<String, Object>>) this.jdbcTemplate.query(
            sql_to_query,
            new Object[]{}, new RowMapper(){
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    HashMap<String, Object> row = new HashMap<String, Object>();
                    row.put("id",rs.getString("id"));
                    row.put("titulo",rs.getString("titulo"));
                    return row;
                }
            }
        );
        return edocivil;
    }

    @Override
    public ArrayList<HashMap<String, Object>> getReligion(Integer id_empresa) {
        String sql_to_query = "select id,titulo from gral_religions where gral_emp_id="+ id_empresa +" order by titulo";
        
        ArrayList<HashMap<String, Object>> religion = (ArrayList<HashMap<String, Object>>) this.jdbcTemplate.query(
            sql_to_query,
            new Object[]{}, new RowMapper(){
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    HashMap<String, Object> row = new HashMap<String, Object>();
                    row.put("id",rs.getString("id"));
                    row.put("titulo",rs.getString("titulo"));
                    return row;
                }
            }
        );
        return religion;
    }

    @Override
    public ArrayList<HashMap<String, Object>> getTiposangre(Integer id_empresa) {
        String sql_to_query = "select id,titulo from gral_sangretipos where gral_emp_id=? order by titulo";

        ArrayList<HashMap<String, Object>> religion = (ArrayList<HashMap<String, Object>>) this.jdbcTemplate.query(
            sql_to_query,
            new Object[]{new Integer(id_empresa)}, new RowMapper(){
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    HashMap<String, Object> row = new HashMap<String, Object>();
                    row.put("id",rs.getString("id"));
                    row.put("titulo",rs.getString("titulo"));
                    return row;
                }
            }
        );
        return religion;
    }

    @Override
    public ArrayList<HashMap<String, Object>> getPuesto(Integer id_empresa) {
        String sql_to_query = "select id,titulo from gral_puestos  where gral_emp_id=? order by titulo";

        ArrayList<HashMap<String, Object>> religion = (ArrayList<HashMap<String, Object>>) this.jdbcTemplate.query(
            sql_to_query,
            new Object[]{new Integer(id_empresa)}, new RowMapper(){
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    HashMap<String, Object> row = new HashMap<String, Object>();
                    row.put("id",rs.getString("id"));
                    row.put("titulo",rs.getString("titulo"));
                    return row;
                }
            }
        );
        return religion;
    }

    //Obtiene todos los departamentos de la empresa
    @Override
    public ArrayList<HashMap<String, Object>> getDepartamentos(Integer id_empresa) {
        String sql_to_query = "select id, titulo from gral_deptos where borrado_logico=false AND vigente=true AND gral_emp_id=? order by titulo";
        
        ArrayList<HashMap<String, Object>> hm = (ArrayList<HashMap<String, Object>>) this.jdbcTemplate.query(
            sql_to_query,
            new Object[]{new Integer(id_empresa)}, new RowMapper(){
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    HashMap<String, Object> row = new HashMap<String, Object>();
                    row.put("id",rs.getString("id"));
                    row.put("titulo",rs.getString("titulo"));
                    return row;
                }
            }
        );
        return hm;
    }
    
    @Override
    public ArrayList<HashMap<String, Object>> getSucursal(Integer id_empresa) {
        String sql_to_query = "select id,titulo from gral_suc  where empresa_id=? order by titulo";

        ArrayList<HashMap<String, Object>> religion = (ArrayList<HashMap<String, Object>>) this.jdbcTemplate.query(
            sql_to_query,
            new Object[]{new Integer(id_empresa)}, new RowMapper(){
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    HashMap<String, Object> row = new HashMap<String, Object>();
                    row.put("id",rs.getString("id"));
                    row.put("titulo",rs.getString("titulo"));
                    return row;
                }
            }
        );
        return religion;
    }

    @Override
    public ArrayList<HashMap<String, Object>> getPuestoForCategoria(String id_puesto) {
        String sql_to_query = "select "
                                +"gral_categ.titulo, "
                                + "gral_categ.id "
                                + "from "
                                + "gral_categ "
                                + "join gral_puestos on gral_puestos.id=gral_categ.gral_puesto_id "
                                + "where gral_categ.gral_puesto_id ="+id_puesto+" "
                                + "order by titulo";
        ArrayList<HashMap<String, Object>> categoria = (ArrayList<HashMap<String, Object>>) this.jdbcTemplate.query(
            sql_to_query,
            new Object[]{}, new RowMapper(){
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    HashMap<String, Object> row = new HashMap<String, Object>();
                    row.put("id",rs.getString("id"));
                    row.put("titulo",rs.getString("titulo"));
                    return row;
                }
            }
        );
        return categoria;
    }

    //obtiene los roles de los empleados
    @Override
    public ArrayList<HashMap<String, Object>> getRoles() {
        String sql_to_query = "select distinct "
                + "gral_rol.id, "
                + "gral_rol.titulo, "
                + "gral_usr_rol.gral_rol_id "
                + "from gral_rol "
                + "left join gral_usr_rol on gral_usr_rol.gral_rol_id = gral_rol.id "
                + "where gral_rol.borrado_logico=FALSE "
                + "order by titulo ";

        ArrayList<HashMap<String, Object>> roles = (ArrayList<HashMap<String, Object>>) this.jdbcTemplate.query(
            sql_to_query,
            new Object[]{}, new RowMapper(){
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    HashMap<String, Object> row = new HashMap<String, Object>();
                    row.put("id",rs.getString("id"));
                    row.put("titulo",rs.getString("titulo"));
                    return row;
                }
            }
        );
        return roles;
    }

    //Obtiene los roles de los empleados
    @Override
    public ArrayList<HashMap<String, Object>> getRolsEdit(Integer id_usuario) {
        String sql_to_query = ""
        + "select gral_rol.id, "
            +"gral_rol.titulo, "
            +"(case when sbt.gral_rol_id IS NOT NULL then 'checked' else '' end) AS checkeado, "
            +"(case when sbt.gral_rol_id IS NOT NULL then '1' else '0' end) AS seleccionado "
        +"from gral_rol "
        +"left join (select  DISTINCT gral_rol_id from gral_usr_rol where gral_usr_id="+id_usuario+") AS sbt ON sbt.gral_rol_id=gral_rol.id "
        +"where gral_rol.borrado_logico=false "
        +"order by gral_rol.titulo ";
        
        ArrayList<HashMap<String, Object>> roles = (ArrayList<HashMap<String, Object>>) this.jdbcTemplate.query(
            sql_to_query,
            new Object[]{}, new RowMapper(){
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    HashMap<String, Object> row = new HashMap<String, Object>();
                    row.put("id",rs.getString("id"));
                    row.put("titulo",rs.getString("titulo"));
                    row.put("checkeado",rs.getString("checkeado"));
                    row.put("seleccionado",rs.getString("seleccionado"));
                    return row;
                }
            }
        );
        return roles;
    }

    //Cambio de Password en el aplicativo
    @Override
    public ArrayList<HashMap<String,Object>>getUsuario(Integer id_usuario){
        String sql_to_query=" select "
                            + "gral_usr.id,  "
                            + "gral_usr.username, "
                            + "gral_usr.password "
                            + "from gral_usr "
                            + "where gral_usr.id="+id_usuario+" "
                            + "order by username ";

        ArrayList<HashMap<String,Object>>cambio_pass=(ArrayList<HashMap<String,Object>>)this.jdbcTemplate.query(
                sql_to_query,
                new Object[]{},new RowMapper(){
                    @Override
                    public Object mapRow(ResultSet rs,int rowNum)throws SQLException{
                     HashMap<String,Object>row=new HashMap<String,Object>();
                     row.put("id",rs.getString("id"));
                     row.put("username",rs.getString("username"));
                     row.put("password",rs.getString("password"));
                     return row;
                    }
                }
            );
        return cambio_pass;
    }

    //edicion de contraseñas de los usuarios
    @Override
    public ArrayList<HashMap<String, Object>> getRegion() {
        String sql_to_query=" select "
                            + "gral_reg.id,  "
                            + "gral_reg.titulo "
                            + "from gral_reg "
                            + "order by titulo ";

        ArrayList<HashMap<String,Object>>cambio_pass=(ArrayList<HashMap<String,Object>>)this.jdbcTemplate.query(
                sql_to_query,
                new Object[]{},new RowMapper(){
                    @Override
                    public Object mapRow(ResultSet rs,int rowNum)throws SQLException{
                     HashMap<String,Object>row=new HashMap<String,Object>();
                     row.put("id",rs.getString("id"));
                     row.put("titulo",rs.getString("titulo"));

                     return row;
                    }
                }
            );
        return cambio_pass;
    }
 
    //Obtiene los regimenes de contratacion
    @Override
    public ArrayList<HashMap<String, Object>> getEmpleados_RegimenContratacion() {
        String sql_to_query="select id, (case when clave is null then '' else clave end)||' '||titulo AS titulo from nom_regimen_contratacion where activo=true order by id;";
        ArrayList<HashMap<String,Object>>hm=(ArrayList<HashMap<String,Object>>)this.jdbcTemplate.query(
            sql_to_query,
            new Object[]{},new RowMapper(){
                @Override
                public Object mapRow(ResultSet rs,int rowNum)throws SQLException{
                 HashMap<String,Object>row=new HashMap<String,Object>();
                 row.put("id",rs.getString("id"));
                 row.put("titulo",rs.getString("titulo"));
                 return row;
                }
            }
        );
        return hm;
    }

    //Obtiene los tipos de contrato
    @Override
    public ArrayList<HashMap<String, Object>> getEmpleados_TiposContrato() {
        String sql_to_query="select id, titulo from nom_tipo_contrato where activo=true order by id;";
        ArrayList<HashMap<String,Object>>hm=(ArrayList<HashMap<String,Object>>)this.jdbcTemplate.query(
            sql_to_query,
            new Object[]{},new RowMapper(){
                @Override
                public Object mapRow(ResultSet rs,int rowNum)throws SQLException{
                 HashMap<String,Object>row=new HashMap<String,Object>();
                 row.put("id",rs.getString("id"));
                 row.put("titulo",rs.getString("titulo"));
                 return row;
                }
            }
        );
        return hm;
    }
    
    //Obtiene los Tipos de Jornada Laboral
    @Override
    public ArrayList<HashMap<String, Object>> getEmpleados_TiposJornada() {
        String sql_to_query="select id, titulo from nom_tipo_jornada where activo=true order by id;";
        ArrayList<HashMap<String,Object>>hm=(ArrayList<HashMap<String,Object>>)this.jdbcTemplate.query(
            sql_to_query,
            new Object[]{},new RowMapper(){
                @Override
                public Object mapRow(ResultSet rs,int rowNum)throws SQLException{
                 HashMap<String,Object>row=new HashMap<String,Object>();
                 row.put("id",rs.getString("id"));
                 row.put("titulo",rs.getString("titulo"));
                 return row;
                }
            }
        );
        return hm;
    }
    
    
    //Obtiene la Periodicidad del Pago
    @Override
    public ArrayList<HashMap<String, Object>> getEmpleados_PeriodicidadPago(Integer id_empresa) {
        String sql_to_query="select id, titulo from nom_periodicidad_pago where activo=true AND gral_emp_id=? AND borrado_logico=false order by id;";
        ArrayList<HashMap<String,Object>>hm=(ArrayList<HashMap<String,Object>>)this.jdbcTemplate.query(
            sql_to_query,
            new Object[]{new Integer(id_empresa)},new RowMapper(){
                @Override
                public Object mapRow(ResultSet rs,int rowNum)throws SQLException{
                 HashMap<String,Object>row=new HashMap<String,Object>();
                 row.put("id",rs.getString("id"));
                 row.put("titulo",rs.getString("titulo"));
                 return row;
                }
            }
        );
        return hm;
    }
    
    
    //Obtiene los tipos de Riesgos de Puestos
    @Override
    public ArrayList<HashMap<String, Object>> getEmpleados_RiesgosPuesto() {
        String sql_to_query="select id, titulo from nom_riesgo_puesto where activo=true order by id;";
        ArrayList<HashMap<String,Object>>hm=(ArrayList<HashMap<String,Object>>)this.jdbcTemplate.query(
            sql_to_query,
            new Object[]{},new RowMapper(){
                @Override
                public Object mapRow(ResultSet rs,int rowNum)throws SQLException{
                 HashMap<String,Object>row=new HashMap<String,Object>();
                 row.put("id",rs.getString("id"));
                 row.put("titulo",rs.getString("titulo"));
                 return row;
                }
            }
        );
        return hm;
    }
    
    
    //Obtiene los Bancos de la empresa
    @Override
    public ArrayList<HashMap<String, Object>> getEmpleados_Bancos(Integer idEmp) {
        String sql_to_query="select id, (case when clave is null then '' else (case when clave<>'' then clave||' ' else '' end) end)||titulo AS titulo from tes_ban where gral_emp_id=? AND borrado_logico=false order by titulo;";
        ArrayList<HashMap<String,Object>>hm=(ArrayList<HashMap<String,Object>>)this.jdbcTemplate.query(
            sql_to_query,
            new Object[]{new Integer (idEmp)},new RowMapper(){
                @Override
                public Object mapRow(ResultSet rs,int rowNum)throws SQLException{
                 HashMap<String,Object>row=new HashMap<String,Object>();
                 row.put("id",rs.getString("id"));
                 row.put("titulo",rs.getString("titulo"));
                 return row;
                }
            }
        );
        return hm;
    }
    
    
    //Obtiene todas las Percepciones disponibles
    @Override
    public ArrayList<HashMap<String, Object>> getEmpleados_Percepciones(Integer IdEmpleado, Integer idEmpresa) {
        String sql_to_query=""
        + "select nom_percep.id, (case when nom_percep.clave is null then '' else (case when nom_percep.clave<>'' then nom_percep.clave||' ' else '' end) end)||nom_percep.titulo AS titulo, (case when gral_empleado_percep.nom_percep_id is null then '' else 'checked' end) as seleccionado "
        + "from nom_percep "
        + "left join gral_empleado_percep ON (gral_empleado_percep.nom_percep_id=nom_percep.id AND gral_empleado_percep.gral_empleado_id=?) "
        + "where nom_percep.gral_emp_id=? AND nom_percep.activo=true AND nom_percep.borrado_logico=false order by nom_percep.id;";
        
        //System.out.println("QueryPercepciones: "+sql_to_query);
        ArrayList<HashMap<String,Object>>hm=(ArrayList<HashMap<String,Object>>)this.jdbcTemplate.query(
            sql_to_query,
            new Object[]{new Integer (IdEmpleado), new Integer (idEmpresa)},new RowMapper(){
                @Override
                public Object mapRow(ResultSet rs,int rowNum)throws SQLException{
                 HashMap<String,Object>row=new HashMap<String,Object>();
                 row.put("id",rs.getString("id"));
                 row.put("titulo",rs.getString("titulo"));
                 row.put("seleccionado",rs.getString("seleccionado"));
                 return row;
                }
            }
        );
        return hm;
    }
    
    
    //Obtiene todas las Deducciones disponibles
    @Override
    public ArrayList<HashMap<String, Object>> getEmpleados_Deducciones(Integer IdEmpleado, Integer idEmpresa) {
        String sql_to_query=""
        + "select nom_deduc.id, (case when nom_deduc.clave is null then '' else (case when nom_deduc.clave<>'' then nom_deduc.clave||' ' else '' end) end)||nom_deduc.titulo AS titulo, (case when gral_empleado_deduc.nom_deduc_id is null then '' else 'checked' end) as seleccionado "
        + "from nom_deduc "
        + "left join gral_empleado_deduc ON (gral_empleado_deduc.nom_deduc_id=nom_deduc.id AND gral_empleado_deduc.gral_empleado_id=?)"
        + "where nom_deduc.gral_emp_id=? AND nom_deduc.activo=true AND nom_deduc.borrado_logico=false order by nom_deduc.id;";
        
        //System.out.println("QueryDeducciones: "+sql_to_query);
        ArrayList<HashMap<String,Object>>hm=(ArrayList<HashMap<String,Object>>)this.jdbcTemplate.query(
            sql_to_query,
            new Object[]{new Integer (IdEmpleado), new Integer (idEmpresa)},new RowMapper(){
                @Override
                public Object mapRow(ResultSet rs,int rowNum)throws SQLException{
                 HashMap<String,Object>row=new HashMap<String,Object>();
                 row.put("id",rs.getString("id"));
                 row.put("titulo",rs.getString("titulo"));
                 row.put("seleccionado",rs.getString("seleccionado"));
                 return row;
                }
            }
        );
        return hm;
    }
    //TERMINA METODOS DE CATALOGO DE EMPLEADOS








    //metodos para el catalogo de escolaridades
    ///guarda los datos de los escolaridad
    @Override
    public ArrayList<HashMap<String, String>> getEscolaridad_Datos(Integer id) {

        String sql_to_query = "select gral_escolaridads.id,gral_escolaridads.titulo from gral_escolaridads where id="+id;

        ArrayList<HashMap<String, String>> dato_escolaridad = (ArrayList<HashMap<String, String>>) this.jdbcTemplate.query(
            sql_to_query,
            new Object[]{}, new RowMapper(){
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    HashMap<String, String> row = new HashMap<String, String>();
                    row.put("id",String.valueOf(rs.getInt("id")));
                    row.put("escolaridad",rs.getString("titulo"));
                    return row;
                }
            }
        );
        return dato_escolaridad;
    }



    @Override
    public ArrayList<HashMap<String, Object>> getEscolaridad_PaginaGrid(String data_string, int offset, int pageSize, String orderBy, String asc) {
        String sql_busqueda = "select id from gral_bus_catalogos(?) as foo (id integer)";

	String sql_to_query = "select gral_escolaridads.id, gral_escolaridads.titulo "
                                +"from gral_escolaridads "
                                +"JOIN ("+sql_busqueda+") AS sbt ON sbt.id = gral_escolaridads.id "
                                +"where gral_escolaridads.borrado_logico=false "
                                +"order by "+orderBy+" "+asc+" limit ? OFFSET ?";

        //System.out.println("Busqueda GetPage: "+sql_to_query+" "+data_string+" "+ offset +" "+ pageSize);
        //System.out.println("esto es el query  :  "+sql_to_query);
        ArrayList<HashMap<String, Object>> hm = (ArrayList<HashMap<String, Object>>) this.jdbcTemplate.query(
            sql_to_query,
            new Object[]{data_string, new Integer(pageSize),new Integer(offset)}, new RowMapper() {
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    HashMap<String, Object> row = new HashMap<String, Object>();
                    row.put("id",String.valueOf(rs.getInt("id")));
                    row.put("titulo",rs.getString("titulo"));
                    return row;
                }
            }
        );
        return hm;
    }

    //termina metodos para catalogo de escolarfidades


    //INICIA metodos para catalogo de Religiones
    //Esto es para lo de religiones
    @Override
    public ArrayList<HashMap<String, String>> getReligion_Datos(Integer id) {

        String sql_to_query = "select gral_religions.id,gral_religions.titulo from gral_religions where id="+id;

        ArrayList<HashMap<String, String>> dato_religion = (ArrayList<HashMap<String, String>>) this.jdbcTemplate.query(
            sql_to_query,
            new Object[]{}, new RowMapper(){
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    HashMap<String, String> row = new HashMap<String, String>();
                    row.put("id",String.valueOf(rs.getInt("id")));
                    row.put("religion",rs.getString("titulo"));
                    return row;
                }
            }
        );
        return dato_religion;
    }



    @Override
    public ArrayList<HashMap<String, Object>> getReligion_PaginaGrid(String data_string, int offset, int pageSize, String orderBy, String asc) {
        String sql_busqueda = "select id from gral_bus_catalogos(?) as foo (id integer)";

	String sql_to_query = "select gral_religions.id , gral_religions.titulo "
                                +"from gral_religions "
                                +"JOIN ("+sql_busqueda+") AS sbt ON sbt.id = gral_religions.id "
                                +"where gral_religions.borrado_logico=false "
                                +"order by "+orderBy+" "+asc+" limit ? OFFSET ?";

        //System.out.println("Busqueda GetPage: "+sql_to_query+" "+data_string+" "+ offset +" "+ pageSize);
        //System.out.println("esto es el query  :  "+sql_to_query);
        ArrayList<HashMap<String, Object>> hm = (ArrayList<HashMap<String, Object>>) this.jdbcTemplate.query(
            sql_to_query,
            new Object[]{data_string, new Integer(pageSize),new Integer(offset)}, new RowMapper() {
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    HashMap<String, Object> row = new HashMap<String, Object>();
                    row.put("id",String.valueOf(rs.getInt("id")));
                    row.put("religion",rs.getString("titulo"));
                    return row;
                }
            }
        );
        return hm;
    }

    //termina metodos para catalogo de Religiones



    //inicia metodos para Tipos de Sangre
    //Esto es para lo de tipo de sangre
    @Override
    public ArrayList<HashMap<String, String>> getTipoSangre_Datos(Integer id) {

        String sql_to_query = "select gral_sangretipos.id,gral_sangretipos.titulo from gral_sangretipos where id="+id;

        ArrayList<HashMap<String, String>> dato_tiposangre = (ArrayList<HashMap<String, String>>) this.jdbcTemplate.query(
            sql_to_query,
            new Object[]{}, new RowMapper(){
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    HashMap<String, String> row = new HashMap<String, String>();
                    row.put("id",String.valueOf(rs.getInt("id")));
                    row.put("tiposangre",rs.getString("titulo"));
                    return row;
                }
            }
        );
        return dato_tiposangre;
    }



    @Override
    public ArrayList<HashMap<String, Object>> getTipoSangre_PaginaGrid(String data_string, int offset, int pageSize, String orderBy, String asc) {
        String sql_busqueda = "select id from gral_bus_catalogos(?) as foo (id integer)";

	String sql_to_query = "select gral_sangretipos.id , gral_sangretipos.titulo "
                                +"from gral_sangretipos "
                                +"JOIN ("+sql_busqueda+") AS sbt ON sbt.id = gral_sangretipos.id "
                                +"where gral_sangretipos.borrado_logico=false "
                                +"order by "+orderBy+" "+asc+" limit ? OFFSET ?";

        //System.out.println("Busqueda GetPage: "+sql_to_query+" "+data_string+" "+ offset +" "+ pageSize);
        //System.out.println("esto es el query  :  "+sql_to_query);
        ArrayList<HashMap<String, Object>> hm = (ArrayList<HashMap<String, Object>>) this.jdbcTemplate.query(
            sql_to_query,
            new Object[]{data_string, new Integer(pageSize),new Integer(offset)}, new RowMapper() {
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    HashMap<String, Object> row = new HashMap<String, Object>();
                    row.put("id",String.valueOf(rs.getInt("id")));
                    row.put("tiposangre",rs.getString("titulo"));
                    return row;
                }
            }
        );
        return hm;
    }


    //termina metodos para catalogo de tipos de Sangre








    //Esto es para lo de catalogo de categorias

    @Override

    public ArrayList<HashMap<String, String>> getCateg_Datos(Integer id) {
        String sql_to_query = "select gral_categ.id, gral_categ.titulo as categoria, gral_categ.sueldo_por_hora, gral_categ.sueldo_por_horas_ext, gral_categ.gral_puesto_id as idpuesto from gral_categ where id="+id;
        ArrayList<HashMap<String, String>> dato_categ = (ArrayList<HashMap<String, String>>) this.jdbcTemplate.query(
        sql_to_query,
        new Object[]{}, new RowMapper(){
        @Override
        public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
            HashMap<String, String> row = new HashMap<String, String>();
            row.put("id",String.valueOf(rs.getInt("id")));
            row.put("categoria",rs.getString("categoria"));
            row.put("puesto",rs.getString("idpuesto"));
            row.put("sueldo_por_hora",StringHelper.roundDouble(rs.getString("sueldo_por_hora"),2));
            row.put("sueldo_por_horas_ext",StringHelper.roundDouble(rs.getString("sueldo_por_horas_ext"),2));
            return row;
        }
        }
        );
        return dato_categ;
    }


    @Override
    public ArrayList<HashMap<String, Object>> getCateg_PaginaGrid(String data_string, int offset, int pageSize, String orderBy, String asc) {

        String sql_busqueda = "select id from gral_bus_catalogos(?) as foo (id integer)";
        String sql_to_query = "select gral_categ.id ,"
                            + " gral_categ.titulo as categoria, "
                            + " gral_categ.sueldo_por_hora, "
                            + " gral_categ.sueldo_por_horas_ext, "
                            + " gral_categ.gral_puesto_id as idpuesto, "
                            + " gral_puestos.titulo as puesto "
                            + " from gral_categ "
                            + " JOIN gral_puestos ON  gral_puestos.id = gral_categ.gral_puesto_id "
                            +"JOIN ("+sql_busqueda+") AS sbt ON sbt.id = gral_categ.id "
                            +"where gral_categ.borrado_logico=false "
                            +"order by gral_categ.id "+asc+" limit ? OFFSET ?";
            //System.out.println("Busqueda GetPage: "+sql_to_query+" "+data_string+" "+ offset +" "+ pageSize);
            //System.out.println("esto es el query : "+sql_to_query);
            ArrayList<HashMap<String, Object>> hm = (ArrayList<HashMap<String, Object>>) this.jdbcTemplate.query(
            sql_to_query,
            new Object[]{data_string, new Integer(pageSize),new Integer(offset)}, new RowMapper() {
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    HashMap<String, Object> row = new HashMap<String, Object>();
                    row.put("id",String.valueOf(rs.getInt("id")));
                    row.put("categoria",rs.getString("categoria"));
                    row.put("sueldo_por_hora",StringHelper.roundDouble(rs.getString("sueldo_por_hora"),2));
                    row.put("sueldo_por_horas_ext",StringHelper.roundDouble(rs.getString("sueldo_por_horas_ext"),2));
                    row.put("id_puesto",rs.getString("idpuesto"));
                    row.put("nombre_puesto",rs.getString("puesto"));
                    return row;
                }
            }
        );
        return hm;
        } //termina CATEGORIAS





    //ESTO VA PARA EL MISMO DE CATEGORIAS YA QUE SE EXTRAEN LOS PUESTOS PARA UN SELECT
    @Override
    public ArrayList<HashMap<String, String>> getPuestos(Integer id_empresa) {
        String sql_to_query = "select id, titulo as puesto from gral_puestos where gral_emp_id="+id_empresa+";";
        ArrayList<HashMap<String, String>> puesto = (ArrayList<HashMap<String, String>>) this.jdbcTemplate.query(
        sql_to_query,
            new Object[]{}, new RowMapper(){
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    HashMap<String, Object> row = new HashMap<String, Object>();
                    row.put("id",String.valueOf(rs.getInt("id")));
                    row.put("puesto",rs.getString("puesto"));
                    return row;
                }
            }
        );
        return puesto;
    }








    //Catalogo de Departamentos
    @Override
    public ArrayList<HashMap<String, Object>> getGralDeptos_PaginaGrid(String data_string, int offset, int pageSize, String orderBy, String asc) {
        String sql_busqueda = "select id from gral_bus_catalogos(?) as foo (id integer)";

	String sql_to_query = "select gral_deptos.id , gral_deptos.titulo, gral_deptos.costo_prorrateo "
                                +"from gral_deptos "
                                +"JOIN ("+sql_busqueda+") AS sbt ON sbt.id = gral_deptos.id "
                                +"where gral_deptos.vigente=true AND gral_deptos.borrado_logico =false "
                                +"order by "+orderBy+" "+asc+" limit ? OFFSET ?";

//        System.out.println("Busqueda GetPage: "+sql_to_query+" "+data_string+" "+ offset +" "+ pageSize);
//        System.out.println("esto es el query  :  "+sql_to_query);
        ArrayList<HashMap<String, Object>> hm = (ArrayList<HashMap<String, Object>>) this.jdbcTemplate.query(
            sql_to_query,
            new Object[]{data_string, new Integer(pageSize),new Integer(offset)}, new RowMapper() {
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    HashMap<String, Object> row = new HashMap<String, Object>();
                    row.put("id",String.valueOf(rs.getInt("id")));
                    row.put("depto",rs.getString("titulo"));
                    //row.put("costo",rs.getString("costo_prorrateo"));
                    row.put("costo",StringHelper.roundDouble(rs.getString("costo_prorrateo"),2));
                    return row;
                }
            }
        );
        return hm;
    }


     @Override
    public ArrayList<HashMap<String, String>> getGralDeptos_Datos(Integer id) {
        String sql_to_query = "select gral_deptos.id,gral_deptos.titulo, gral_deptos.costo_prorrateo  from gral_deptos where id="+id;

        ArrayList<HashMap<String, String>> datos_unidades = (ArrayList<HashMap<String, String>>) this.jdbcTemplate.query(
            sql_to_query,
            new Object[]{}, new RowMapper(){
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    HashMap<String, String> row = new HashMap<String, String>();
                    row.put("id",String.valueOf(rs.getInt("id")));
                    row.put("depto",rs.getString("titulo"));
                    row.put("costo",StringHelper.roundDouble(rs.getString("costo_prorrateo"),2));
                    return row;
                }
            }
        );
        return datos_unidades;
    }








    //Esto es para lo de catalogo de Turnos
    @Override
    public ArrayList<HashMap<String, String>> getTurnos(Integer id) {
        String sql_to_query = " select gral_deptos_turnos.id, "
                            + " gral_deptos_turnos.turno, "
                            + " gral_deptos_turnos.hora_ini, "
                            + " gral_deptos_turnos.hora_fin, "
                            + " gral_deptos_turnos.gral_deptos_id as iddepto"
                            + " from gral_deptos_turnos where id="+id;
        ArrayList<HashMap<String, String>> dato_depto = (ArrayList<HashMap<String, String>>) this.jdbcTemplate.query(
        sql_to_query,
        new Object[]{}, new RowMapper(){
        @Override
        public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
            HashMap<String, String> row = new HashMap<String, String>();
            row.put("id",String.valueOf(rs.getInt("id")));
            row.put("turno",rs.getString("turno"));
            row.put("hora_inicial",rs.getString("hora_ini"));
            row.put("hora_final",rs.getString("hora_fin"));
            row.put("depto",rs.getString("iddepto"));
            return row;
        }
        }
        );
        return dato_depto;
    }




    @Override
    public ArrayList<HashMap<String, Object>> getTurnos_PaginaGrid(String data_string, int offset, int pageSize, String orderBy, String asc) {
        String sql_busqueda = "select id from gral_bus_catalogos(?) as foo (id integer)";
        String sql_to_query = "select gral_deptos_turnos.id ,"
                            + " gral_deptos_turnos.turno, "
                            + " gral_deptos_turnos.hora_ini, "
                            + " gral_deptos_turnos.hora_fin, "
                            + " gral_deptos_turnos.gral_deptos_id as iddepto, "
                            + " gral_deptos.titulo as depto "
                            + " from gral_deptos_turnos "
                            + " JOIN gral_deptos ON  gral_deptos.id = gral_deptos_turnos.gral_deptos_id "
                            +"JOIN ("+sql_busqueda+") AS sbt ON sbt.id = gral_deptos_turnos.id "
                            +"where gral_deptos_turnos.borrado_logico=false "
                            +"order by gral_deptos_turnos.id "+asc+" limit ? OFFSET ?";
            //System.out.println("Busqueda GetPage: "+sql_to_query+" "+data_string+" "+ offset +" "+ pageSize);
            //System.out.println("esto es el query : "+sql_to_query);
            ArrayList<HashMap<String, Object>> hm = (ArrayList<HashMap<String, Object>>) this.jdbcTemplate.query(
            sql_to_query,
            new Object[]{data_string, new Integer(pageSize),new Integer(offset)}, new RowMapper() {
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    HashMap<String, Object> row = new HashMap<String, Object>();
                    row.put("id",String.valueOf(rs.getInt("id")));
                    row.put("turno",rs.getString("turno"));
                    row.put("hora_inicial",rs.getString("hora_ini"));
                    row.put("hora_final",rs.getString("hora_fin"));
                    row.put("depto",rs.getString("iddepto"));
                    row.put("nombre_depto",rs.getString("depto"));
                    return row;
                }
            }
        );
        return hm;
        }




    //ESTO VA JUNTO CON TURNOS YA QUE NECESITA CARGAR LOS DEPARTAMENTOS EN UN SELECT
    @Override
    public ArrayList<HashMap<String, String>> getDeptos() {
        //String sql_to_query = "select DISTINCT cve_pais ,pais_ent from municipios;";
        String sql_to_query = "select id, "
        + " titulo as depto "
        + " from gral_deptos ;";
        ArrayList<HashMap<String, String>> depto = (ArrayList<HashMap<String, String>>) this.jdbcTemplate.query(
        sql_to_query,
            new Object[]{}, new RowMapper(){
            @Override
            public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
            HashMap<String, Object> row = new HashMap<String, Object>();
            row.put("id",String.valueOf(rs.getInt("id")));
            row.put("depto",rs.getString("depto"));
            return row;
            }
            }
        );
        return depto;
    }








    //Esto es para lo de catalogo de Dias no Laborables

    @Override
    public ArrayList<HashMap<String, String>> getDiasNoLaborables(Integer id) {
        String sql_to_query = " select gral_dias_no_laborables.id, "
                            + " gral_dias_no_laborables.fecha_no_laborable, "
                            + " gral_dias_no_laborables.descripcion "
                            + " from gral_dias_no_laborables where id="+id;
        ArrayList<HashMap<String, String>> dianolab = (ArrayList<HashMap<String, String>>) this.jdbcTemplate.query(
        sql_to_query,
        new Object[]{}, new RowMapper(){
            @Override
            public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                HashMap<String, String> row = new HashMap<String, String>();
                row.put("id",String.valueOf(rs.getInt("id")));
                row.put("fecha_no_laborable",rs.getString("fecha_no_laborable"));
                row.put("descripcion",rs.getString("descripcion"));
                return row;
            }
        }
        );
        return dianolab;
    }



    @Override
    public ArrayList<HashMap<String, Object>> getDiasNoLaborables_PaginaGrid(String data_string, int offset, int pageSize, String orderBy, String asc) {

        String sql_busqueda = " select id from gral_bus_catalogos(?) as foo (id integer) ";
        String sql_to_query = " select gral_dias_no_laborables.id , "
                            + " gral_dias_no_laborables.fecha_no_laborable, "
                            + " gral_dias_no_laborables.descripcion  "
                            + " from gral_dias_no_laborables "
                            +" JOIN ("+sql_busqueda+") AS sbt ON sbt.id = gral_dias_no_laborables.id "
                            +" where gral_dias_no_laborables.borrado_logico=false "
                            +" order by gral_dias_no_laborables.id "+asc+" limit ? OFFSET ?";
            //System.out.println("Busqueda GetPage: "+sql_to_query+" "+data_string+" "+ offset +" "+ pageSize);
            //System.out.println("esto es el query : "+sql_to_query);
            ArrayList<HashMap<String, Object>> hm = (ArrayList<HashMap<String, Object>>) this.jdbcTemplate.query(
            sql_to_query,
            new Object[]{data_string, new Integer(pageSize),new Integer(offset)}, new RowMapper() {
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    HashMap<String, Object> row = new HashMap<String, Object>();
                    row.put("id",String.valueOf(rs.getInt("id")));//estos se almacenan en el controller en la construccion de la tabla
                    row.put("fecha_no_laborable",rs.getString("fecha_no_laborable"));//para el grid que esta en el controller
                    row.put("descripcion",rs.getString("descripcion"));
                    return row;
                }
            }
        );
        return hm;
        }



   //Estos son para email y password de compras.
    @Override
    public String geteMailPurchasingEmpresaEmisora(Integer id_empresa) {
        String sql_to_query = "select CASE WHEN email_compras is null then '' else email_compras END  from gral_emp where id ="+id_empresa;
        Map<String, Object> map_email_compras = this.getJdbcTemplate().queryForMap(sql_to_query);
        String email_compras = map_email_compras.get("email_compras").toString();
        System.out.println("geteMailPurchasingEmpresaEmisora: "+sql_to_query);
        return email_compras;
    }

    @Override
    public String getPasswordeMailPurchasingEmpresaEmisora(Integer id_empresa) {
        String sql_to_query = "select CASE WHEN pass_email_compras is null then '' else pass_email_compras END  from gral_emp where id ="+id_empresa;
        Map<String, Object> map_pass_email_compras = this.getJdbcTemplate().queryForMap(sql_to_query);
        String pass_email_compras = map_pass_email_compras.get("pass_email_compras").toString();
        System.out.println("getPasswordeMailPurchasingEmpresaEmisora: "+sql_to_query);
        return pass_email_compras;
    }
    //end



    //ACTUALIZADOR CODIGOS ISO
    //------------------------------------------Aplicativo de Edicion de Codigo ISO----------------------------------------
    @Override
    public ArrayList<HashMap<String, Object>> getCodigos_PaginaGrid(String data_string, int offset, int pageSize, String orderBy, String asc,Integer id_empresa) {
        String sql_busqueda = "select id from gral_bus_catalogos(?) as foo (id integer)";

	String sql_to_query = "select gral_docs.id, gral_docs.titulo "
                                +"from gral_docs "
                                +"JOIN ("+sql_busqueda+") AS sbt ON sbt.id = gral_docs.id "
                                +"where gral_docs.gral_emp_id="+id_empresa
                                +"order by "+orderBy+" "+asc+" limit ? OFFSET ?";

        //System.out.println("Busqueda GetPage: "+sql_to_query+" "+data_string+" "+ offset +" "+ pageSize);
        //System.out.println("esto es el query  :  "+sql_to_query);
        ArrayList<HashMap<String, Object>> hm = (ArrayList<HashMap<String, Object>>) this.jdbcTemplate.query(
            sql_to_query,
            new Object[]{data_string, new Integer(pageSize),new Integer(offset)}, new RowMapper() {
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    HashMap<String, Object> row = new HashMap<String, Object>();
                    row.put("id",String.valueOf(rs.getInt("id")));
                    row.put("titulo",rs.getString("titulo"));
                    return row;
                }
            }
        );
        return hm;
    }


    @Override
    public ArrayList<HashMap<String, String>> getCodigos_Datos(Integer id) {
        String sql_to_query = "select id,valor as codigo from gral_docs_conf where gral_doc_id="+id;
        ArrayList<HashMap<String, String>> dato_datos = (ArrayList<HashMap<String, String>>) this.jdbcTemplate.query(
            sql_to_query,
            new Object[]{}, new RowMapper(){
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    HashMap<String, String> row = new HashMap<String, String>();
                    row.put("id",String.valueOf(rs.getInt("id")));
                    row.put("codigo",rs.getString("codigo"));

                    return row;
                }
            }
        );
        return dato_datos;
    }



    @Override
    public ArrayList<HashMap<String, String>> getTitulo_Datos(Integer id) {
        String sql_to_query = "select gral_docs.id,gral_docs.titulo  from gral_docs where gral_docs.id="+id;
        ArrayList<HashMap<String, String>> dato_titulo = (ArrayList<HashMap<String, String>>) this.jdbcTemplate.query(
            sql_to_query,
            new Object[]{}, new RowMapper(){
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    HashMap<String, String> row = new HashMap<String, String>();
                    row.put("id",String.valueOf(rs.getInt("id")));
                    row.put("titulo",rs.getString("titulo"));

                    return row;
                }
            }
        );
        return dato_titulo;
    }
    //TERMINA ACTUALIZADOR CODIGOS ISO

    //ACTUALIZADOR DE TIPOS DE CAMBIO
    @Override
    public ArrayList<HashMap<String, String>> getTiposdeCambio() {
        String sql_to_query = "select id, descripcion from  gral_mon where borrado_logico=FALSE  and descripcion != 'Pesos' order by id ASC;";
        //log.log(Level.INFO, "Ejecutando query de {0}", sql_to_query);
        ArrayList<HashMap<String, String>> TiposdeCambio = (ArrayList<HashMap<String, String>>) this.jdbcTemplate.query(
            sql_to_query,
            new Object[]{}, new RowMapper(){
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    HashMap<String, Object> row = new HashMap<String, Object>();
                    row.put("id",rs.getInt("id"));
                    row.put("descripcion",rs.getString("descripcion"));
                    return row;
                }
            }
        );
        return TiposdeCambio;
    }

    @Override

    public ArrayList<HashMap<String, Object>> getTipocambio_PaginaGrid(String data_string, int offset, int pageSize, String orderBy, String asc) {
        String sql_busqueda = "select id from gral_bus_catalogos(?) as foo (id integer)";

	String sql_to_query = "select erp_monedavers.id, "
                                +" erp_monedavers.valor, "
                                +" to_char(erp_monedavers.momento_creacion,'yyyy-mm-dd')as momento_creacion , "
                                +" erp_monedavers.moneda_id, "
                                +" erp_monedavers.version, "
                                +" gral_mon.descripcion_abr,gral_mon.descripcion,gral_mon.simbolo "
                                +" from erp_monedavers "
                                +" JOIN ("+sql_busqueda+") AS sbt ON sbt.id = erp_monedavers.id "
                                +" JOIN gral_mon on gral_mon.id = erp_monedavers.moneda_id  "
                                +" where gral_mon.borrado_logico=false "
                                +" order by "+orderBy+" "+asc+" limit ? OFFSET ?";

        //System.out.println("Busqueda GetPage: "+sql_to_query+" "+data_string+" "+ offset +" "+ pageSize);
        //System.out.println("esto es el query  :  "+sql_to_query);
        ArrayList<HashMap<String, Object>> hm = (ArrayList<HashMap<String, Object>>) this.jdbcTemplate.query(
            sql_to_query,
            new Object[]{data_string, new Integer(pageSize),new Integer(offset)}, new RowMapper() {
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    HashMap<String, Object> row = new HashMap<String, Object>();
                    row.put("id",String.valueOf(rs.getInt("id")));
                    row.put("valor",StringHelper.roundDouble(rs.getString("valor"),4));
                    row.put("momento_creacion",rs.getString("momento_creacion"));                    
                    row.put("moneda_id",rs.getString("moneda_id"));                    
                    row.put("version",rs.getString("version"));                    
                    row.put("descripcion_abr",rs.getString("descripcion_abr")); 
                    row.put("descripcion",rs.getString("descripcion")); 
                    row.put("simbolo",rs.getString("simbolo")); 
                    
                    return row;
                }
            }
        );
        return hm;
    }



    @Override
    public ArrayList<HashMap<String, String>> gettipoCambio_Datos(String erp_monedavers_id) {
        String sql_to_query = "select erp_monedavers.id, "
                                +" erp_monedavers.valor, "
                                +" to_char(erp_monedavers.momento_creacion,'yyyy-mm-dd')as momento_creacion , "
                                +" erp_monedavers.moneda_id, "
                                +" erp_monedavers.version, "
                                +" gral_mon.descripcion_abr,gral_mon.descripcion,gral_mon.simbolo "
                                +" from erp_monedavers  "
                               +" JOIN gral_mon ON gral_mon.id = erp_monedavers.moneda_id "
                              + " where erp_monedavers.id ="+erp_monedavers_id;
        //System.out.println("Id de la tabla erp_Monedavers:  "+erp_monedavers_id);
        ArrayList<HashMap<String, String>> datos_tc = (ArrayList<HashMap<String, String>>) this.jdbcTemplate.query(
            sql_to_query,
            new Object[]{}, new RowMapper(){
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    HashMap<String, String> row = new HashMap<String, String>();
                     row.put("id",String.valueOf(rs.getInt("id")));
                    row.put("valor",StringHelper.roundDouble(rs.getString("valor"),4));
                    row.put("fecha",rs.getString("momento_creacion"));
                    row.put("moneda_id",rs.getString("moneda_id"));
                    row.put("descripcion_abr",rs.getString("descripcion_abr"));
                    row.put("version",rs.getString("version"));
                    return row;
                }
            }
        );
        return datos_tc;
    }

    //TERMINA ACTUALIZADOR DE TIPOS DE CAMBIO



    //Descarga de ficha tecnica
    //------------------------------------------Aplicativo de descarga de ficha tecnica---------------------------------------
    @Override
    public ArrayList<HashMap<String, Object>> getFichaTecnica_PaginaGrid(String data_string, int offset, int pageSize, String orderBy, String asc,Integer id_empresa) {
        String sql_busqueda = "select id from gral_bus_catalogos(?) as foo (id integer)";

	String sql_to_query = "select inv_prod.id, inv_prod.sku, inv_prod.descripcion, inv_prod.archivo_pdf as accesor_descarga "
                                +"from inv_prod "
                                +"JOIN ("+sql_busqueda+") AS sbt ON sbt.id = inv_prod.id "
                                +"where inv_prod.empresa_id="+id_empresa
                                +"order by "+orderBy+" "+asc+" limit ? OFFSET ?";

        //System.out.println("Busqueda GetPage: "+sql_to_query+" "+data_string+" "+ offset +" "+ pageSize);
        //System.out.println("esto es el query  :  "+sql_to_query);
        ArrayList<HashMap<String, Object>> hm = (ArrayList<HashMap<String, Object>>) this.jdbcTemplate.query(
            sql_to_query,
            new Object[]{data_string, new Integer(pageSize),new Integer(offset)}, new RowMapper() {
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    HashMap<String, Object> row = new HashMap<String, Object>();
                    row.put("id",String.valueOf(rs.getInt("id")));
                    row.put("sku",rs.getString("sku"));
                    row.put("descripcion",rs.getString("descripcion"));
                    row.put("accesor_descarga",rs.getString("accesor_descarga"));
                    return row;
                }
            }
        );
        return hm;
    }
    
    
    @Override
    public String getCodigoProductoById(String id_producto) {
        String sql_to_query = "select (case when archivo_pdf is null then '' else archivo_pdf end) as archivo_pdf from inv_prod where id="+id_producto+" limit 1";
        
        System.out.println("Buscar archivo PDF:"+sql_to_query);
        
        String valor_retorno="";
        Map<String, Object> select = this.getJdbcTemplate().queryForMap(sql_to_query);
        
        valor_retorno = select.get("archivo_pdf").toString();

        return valor_retorno;
    }

    
    
    @Override
    public String getNombreEmpleadoByIdUser(Integer id_user) {
        String sql_to_query = ""
                + "select (case when gral_empleados.id is null then '' else (case when gral_empleados.nombre_pila is null then '' else gral_empleados.nombre_pila end)||' '||(case when gral_empleados.apellido_paterno is null then '' else gral_empleados.apellido_paterno end)||' '||(case when gral_empleados.apellido_materno is null then '' else gral_empleados.apellido_materno end) end) AS nombre_empleado "
                + "from gral_usr "
                + "left join gral_empleados ON gral_empleados.id=gral_usr.gral_empleados_id "
                + "where gral_usr.id="+id_user+";";
        Map<String, Object> map_empleado = this.getJdbcTemplate().queryForMap(sql_to_query);
        String nombre_empleado = map_empleado.get("nombre_empleado").toString();
        return nombre_empleado;
    }    
    

    //Aplicativo de cambio de Contraseña de Usuario
    @Override
    public ArrayList<HashMap<String, Object>> GralUserEdit_PaginaGrid(String data_string, int offset, int pageSize, String orderBy, String asc) {
        String sql_busqueda = "select id from gral_bus_catalogos(?) as foo (id integer)";
        
	String sql_to_query = ""
                + "select "
                    + "gral_usr.id,"
                    + "gral_usr.username AS usern,"
                    + "(case when gral_empleados.id is null then '' else (case when gral_empleados.nombre_pila is null then '' else gral_empleados.nombre_pila end)||' '||(case when gral_empleados.apellido_paterno is null then '' else gral_empleados.apellido_paterno end)||' '||(case when gral_empleados.apellido_materno is null then '' else gral_empleados.apellido_materno end) end) AS nombre_empleado "
                + "from gral_usr "
                + "left join gral_empleados ON gral_empleados.id=gral_usr.gral_empleados_id "
                + "JOIN ("+sql_busqueda+") AS sbt ON sbt.id = gral_usr.id "
                + "order by "+orderBy+" "+asc+" limit ? OFFSET ?";
        
        //System.out.println("Busqueda GetPage: "+sql_to_query+" "+data_string+" "+ offset +" "+ pageSize);
        //System.out.println("esto es el query  :  "+sql_to_query);
        ArrayList<HashMap<String, Object>> hm = (ArrayList<HashMap<String, Object>>) this.jdbcTemplate.query(
            sql_to_query,
            new Object[]{data_string, new Integer(pageSize),new Integer(offset)}, new RowMapper() {
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    HashMap<String, Object> row = new HashMap<String, Object>();
                    row.put("id",String.valueOf(rs.getInt("id")));
                    row.put("usern",rs.getString("usern"));
                    row.put("nombre_empleado",rs.getString("nombre_empleado"));
                    return row;
                }
            }
        );
        return hm;
    }

    @Override
    public ArrayList<HashMap<String, String>> GralUserEdit_Datos(Integer id) {
        String sql_to_query = "select gral_usr.id, gral_usr.username as usern from gral_usr where gral_usr.id="+id;
        
        //System.out.println("sql_to_query:  "+sql_to_query);
        ArrayList<HashMap<String, String>> datos_tc = (ArrayList<HashMap<String, String>>) this.jdbcTemplate.query(
            sql_to_query,
            new Object[]{}, new RowMapper(){
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    HashMap<String, String> row = new HashMap<String, String>();
                    row.put("id",String.valueOf(rs.getInt("id")));
                    row.put("usern",rs.getString("usern"));
                    return row;
                }
            }
        );
        return datos_tc;
    }
    
    
    
    
    //Obtiene todos los datos de la Empresa Emisora
    @Override
    public HashMap<String, String> getEmisor_Datos(Integer id_emp) {
        HashMap<String, String> mapDatos = new HashMap<String, String>();
        String sql_query = ""
                + "select "
                + "gral_emp.titulo,"
                + "gral_emp.rfc,"
                + "gral_emp.calle,"
                + "gral_emp.colonia,"
                + "gral_emp.numero_interior,"
                + "gral_emp.numero_exterior,"
                + "gral_pais.titulo AS pais,"
                + "gral_edo.titulo AS estado,"
                + "gral_mun.titulo AS municipio, "
                + "gral_emp.cp,"
                + "gral_emp.telefono,"
                + "gral_emp.regimen_fiscal,"
                + "(case when gral_emp.pagina_web is null then '' else gral_emp.pagina_web end) AS pagina_web "
                + "from gral_emp  "
                + "JOIN gral_pais ON gral_pais.id=gral_emp.pais_id "
                + "JOIN gral_edo ON gral_edo.id=gral_emp.estado_id "
                + "JOIN gral_mun ON gral_mun.id=gral_emp.municipio_id "
                + "where gral_emp.id="+id_emp+";";
        //System.out.println("getDatosEmp: "+sql_query);
        
        Map<String, Object> map = this.getJdbcTemplate().queryForMap(sql_query);
        mapDatos.put("emp_razon_social", String.valueOf(map.get("titulo")));
        mapDatos.put("emp_rfc", String.valueOf(map.get("rfc")));
        mapDatos.put("emp_calle", String.valueOf(map.get("calle")));
        mapDatos.put("emp_no_interior", String.valueOf(map.get("numero_interior")));
        mapDatos.put("emp_no_exterior", String.valueOf(map.get("numero_exterior")));
        mapDatos.put("emp_colonia", String.valueOf(map.get("colonia")));
        mapDatos.put("emp_pais", String.valueOf(map.get("pais")));
        mapDatos.put("emp_estado", String.valueOf(map.get("estado")));
        mapDatos.put("emp_municipio", String.valueOf(map.get("municipio")));
        mapDatos.put("emp_cp", String.valueOf(map.get("cp")));
        mapDatos.put("emp_tel", String.valueOf(map.get("telefono")));
        mapDatos.put("emp_regimen_fiscal", String.valueOf(map.get("regimen_fiscal")));
        mapDatos.put("emp_pagina_web", String.valueOf(map.get("pagina_web")));
        
        return mapDatos;
    }
    
    

    
    
    //metodos para el Catalogo de Ieps
    //guarda los datos de los Ieps
    @Override
    public ArrayList<HashMap<String, String>> getIeps_Datos(Integer id) {

        String sql_to_query = "select id,titulo as ieps, descripcion as descripcion,tasa as tasa from gral_ieps where id="+id;

        ArrayList<HashMap<String, String>> dato_puesto = (ArrayList<HashMap<String, String>>) this.jdbcTemplate.query(
            sql_to_query,
            new Object[]{}, new RowMapper(){
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    HashMap<String, String> row = new HashMap<String, String>();
                    row.put("id",String.valueOf(rs.getInt("id")));
                    row.put("ieps",rs.getString("ieps"));
                    row.put("descripcion",rs.getString("descripcion"));
                    row.put("tasa",rs.getString("tasa"));
                    
                    return row;
                }
            }
        );
        return dato_puesto;
    }



    @Override
    public ArrayList<HashMap<String, Object>> getIeps_PaginaGrid(String data_string, int offset, int pageSize, String orderBy, String asc) {
        String sql_busqueda = "select id from gral_bus_catalogos(?) as foo (id integer)";

	String sql_to_query = "select gral_ieps.id, gral_ieps.titulo as ieps,gral_ieps.descripcion as descripcion ,gral_ieps.tasa as tasa  "
                                +"from gral_ieps "
                                +"JOIN ("+sql_busqueda+") AS sbt ON sbt.id = gral_ieps.id "
                                +"where gral_ieps.borrado_logico=false "
                                +"order by "+orderBy+" "+asc+" limit ? OFFSET ?";

        //System.out.println("Busqueda GetPage: "+sql_to_query+" "+data_string+" "+ offset +" "+ pageSize);
        ArrayList<HashMap<String, Object>> hm = (ArrayList<HashMap<String, Object>>) this.jdbcTemplate.query(
            sql_to_query,
            new Object[]{data_string, new Integer(pageSize),new Integer(offset)}, new RowMapper() {
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    HashMap<String, Object> row = new HashMap<String, Object>();
                    row.put("id",String.valueOf(rs.getInt("id")));
                    row.put("ieps",rs.getString("ieps"));
                    row.put("descripcion",rs.getString("descripcion"));
                    row.put("tasa",rs.getString("tasa"));
                    return row;
                }
            }
        );
        return hm;
    }
    
    
    
    //Métodos para el Catalogo de Percepciones
    @Override
    public ArrayList<HashMap<String, Object>> getPercepciones_Datos(Integer id) {


        String sql_to_query = "select id,clave, titulo as percepcion, activo as estado,nom_percep_tipo_id as tipo_percep from nom_percep where id="+id;
            ArrayList<HashMap<String, Object>> dato_puesto = (ArrayList<HashMap<String, Object>>) this.jdbcTemplate.query(
            sql_to_query,
          
            new Object[]{}, new RowMapper(){
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    HashMap<String, Object> row = new HashMap<String, Object>();
                    row.put("id",String.valueOf(rs.getInt("id")));
                    row.put("clave",rs.getString("clave"));
                    row.put("percepcion",rs.getString("percepcion"));
                    row.put("estado",String.valueOf(rs.getBoolean("estado")));
                    row.put("tipo_percep",String.valueOf(rs.getInt("tipo_percep")));
                    
                    return row;
                }
            }
        );
        return dato_puesto;
    }

    @Override
    public ArrayList<HashMap<String, Object>> getPercepciones_PaginaGrid(String data_string, int offset, int pageSize, String orderBy, String asc) {
        String sql_busqueda = "select id from gral_bus_catalogos(?) as foo (id integer)";
        
	String sql_to_query = ""
        + "select "
            +"nom_percep.id, "
            +"nom_percep.clave as clave, "
            +"nom_percep.titulo as percepcion, "
            +"(case when nom_percep.activo=TRUE then  'ACTIVO' else 'INACTIVO' end) AS estado,"
            +"(case when nom_percep_tipo.clave is null then '' else nom_percep_tipo.clave end)||' '||nom_percep_tipo.titulo AS tipo_percepcion "
        +"from nom_percep "
        +"left join nom_percep_tipo ON nom_percep_tipo.id=nom_percep.nom_percep_tipo_id "
        +"JOIN ("+sql_busqueda+") AS sbt ON sbt.id = nom_percep.id "
        +"where nom_percep.borrado_logico=false "
        +"order by "+orderBy+" "+asc+" limit ? OFFSET ? ";
        
        //System.out.println("Busqueda GetPage: "+sql_to_query+" "+data_string+" "+ offset +" "+ pageSize);
        ArrayList<HashMap<String, Object>> hm = (ArrayList<HashMap<String, Object>>) this.jdbcTemplate.query(
            sql_to_query,
            new Object[]{data_string, new Integer(pageSize),new Integer(offset)}, new RowMapper() {
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    HashMap<String, Object> row = new HashMap<String, Object>();
                    row.put("id",String.valueOf(rs.getInt("id")));
                    row.put("clave",rs.getString("clave"));
                    row.put("percepcion",rs.getString("percepcion"));
                    row.put("estado",rs.getString("estado"));
                    row.put("tipo_percepcion",rs.getString("tipo_percepcion"));
                    return row;
                }
            }
        );
        return hm;
    }

    //Alimenta select de tipo de Tipo Percepciones
    @Override
    public ArrayList<HashMap<String, Object>> getPercepciones_Tipos(Integer id_empresa) {
        String sql_to_query = "select id,(case when clave is null then '' else clave end)||' '||titulo AS titulo from nom_percep_tipo order by titulo";
        ArrayList<HashMap<String, Object>> percepciones = (ArrayList<HashMap<String, Object>>) this.jdbcTemplate.query(
            sql_to_query,
            new Object[]{}, new RowMapper(){
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    HashMap<String, Object> row = new HashMap<String, Object>();
                    row.put("id",rs.getString("id"));
                    row.put("titulo",rs.getString("titulo"));
                    return row;
                }
            }
        );
        return percepciones;
    }
    
    //Métodos para el Catalogo de Deducciones
    //Guarda los datos de los Deducciones
    @Override
    public ArrayList<HashMap<String, Object>> getDeducciones_Datos(Integer id) {
        String sql_to_query = "select id,clave,titulo as deduccion, activo as estado, nom_deduc_tipo_id as tipo_deduc from nom_deduc where id="+id;
            ArrayList<HashMap<String, Object>> dato_puesto = (ArrayList<HashMap<String, Object>>) this.jdbcTemplate.query(
            sql_to_query,
            
            new Object[]{}, new RowMapper(){
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    HashMap<String, Object> row = new HashMap<String, Object>();
                    row.put("id",String.valueOf(rs.getInt("id")));
                    row.put("clave",rs.getString("clave"));
                    row.put("deduccion",rs.getString("deduccion"));
                    row.put("estado",String.valueOf(rs.getBoolean("estado")));
                    row.put("tipo_deduc",String.valueOf(rs.getInt("tipo_deduc")));    
                    return row;
                }
            }
        );
        return dato_puesto;
    }
    
    
    @Override
    public ArrayList<HashMap<String, Object>> getDeducciones_PaginaGrid(String data_string, int offset, int pageSize, String orderBy, String asc) {
        String sql_busqueda = "select id from gral_bus_catalogos(?) as foo (id integer)";
        
	String sql_to_query = ""
        + "select "
            +"nom_deduc.id, "
            +"nom_deduc.clave as clave, "
            +"nom_deduc.titulo as deduccion, "
            +"(case when nom_deduc.activo=TRUE then  'ACTIVO' else 'INACTIVO' end) AS estado,"
            +"(case when nom_deduc_tipo.clave is null then '' else nom_deduc_tipo.clave end)||' '||nom_deduc_tipo.titulo AS tipo_deduccion "
        +"from nom_deduc "
        +"left join nom_deduc_tipo ON nom_deduc_tipo.id=nom_deduc.nom_deduc_tipo_id "
        +"JOIN ("+sql_busqueda+") AS sbt ON sbt.id=nom_deduc.id "
        +"where nom_deduc.borrado_logico=false "
        +"order by "+orderBy+" "+asc+" limit ? OFFSET ? ";

        //System.out.println("Busqueda GetPage: "+sql_to_query+" "+data_string+" "+ offset +" "+ pageSize);
        ArrayList<HashMap<String, Object>> hm = (ArrayList<HashMap<String, Object>>) this.jdbcTemplate.query(
            sql_to_query,
            new Object[]{data_string, new Integer(pageSize),new Integer(offset)}, new RowMapper() {
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    HashMap<String, Object> row = new HashMap<String, Object>();
                    row.put("id",String.valueOf(rs.getInt("id")));
                    row.put("clave",rs.getString("clave"));
                    row.put("deduccion",rs.getString("deduccion"));
                    row.put("estado",rs.getString("estado"));
                    row.put("tipo_deduccion",rs.getString("tipo_deduccion"));
                    return row;
                }
            }
        );
        return hm;
    }
    
    
    
    //Alimenta select de tipo de Tipo Deducciones
    @Override
    public ArrayList<HashMap<String, Object>> getDeducciones_Tipos(Integer id_empresa) {

        String sql_to_query = "select id,(case when clave is null then '' else clave end)||' '||titulo AS titulo from nom_deduc_tipo order by titulo";
        ArrayList<HashMap<String, Object>> percepciones = (ArrayList<HashMap<String, Object>>) this.jdbcTemplate.query(
            sql_to_query,
            new Object[]{}, new RowMapper(){
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    HashMap<String, Object> row = new HashMap<String, Object>();
                    row.put("id",rs.getString("id"));
                    row.put("titulo",rs.getString("titulo"));
                    return row;
                }
            }
        );
        return percepciones;
    }
    
    
    
    
    //Métodos para el Catalogo de Periodicidad de Pago
    @Override
    public ArrayList<HashMap<String, Object>> getPeriodicidadPago_Datos(Integer id) {
        String sql_to_query = "select id,titulo as periodo_pago, no_periodos as numero_periodo,activo as estado from nom_periodicidad_pago where id="+id;
            ArrayList<HashMap<String, Object>> dato_periodicidadpago = (ArrayList<HashMap<String, Object>>) this.jdbcTemplate.query(
            sql_to_query,
            new Object[]{}, new RowMapper(){
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    HashMap<String, Object> row = new HashMap<String, Object>();
                    row.put("id",String.valueOf(rs.getInt("id")));
                    row.put("periodo_pago",rs.getString("periodo_pago"));
                    row.put("numero_periodo",rs.getString("numero_periodo"));
                    row.put("estado",String.valueOf(rs.getBoolean("estado")));
                    return row;
                }
            }
        );
        return dato_periodicidadpago;
    }

    @Override
    public ArrayList<HashMap<String, Object>> getPeriodicidadPago_PaginaGrid(String data_string, int offset, int pageSize, String orderBy, String asc) {
        String sql_busqueda = "select id from gral_bus_catalogos(?) as foo (id integer)";
        
	String sql_to_query = ""
        + "select "
            +"nom_periodicidad_pago.id, "
            +"nom_periodicidad_pago.titulo as periodo_pago, "
            +"nom_periodicidad_pago.no_periodos as numero_periodo, "
            +"(case when nom_periodicidad_pago.activo=TRUE then  'ACTIVO' else 'INACTIVO' end) AS estado "
        +"from nom_periodicidad_pago "
        +"JOIN ("+sql_busqueda+") AS sbt ON sbt.id = nom_periodicidad_pago.id "
        +"where nom_periodicidad_pago.borrado_logico=false "
        +"order by "+orderBy+" "+asc+" limit ? OFFSET ? ";
        
        //System.out.println("Busqueda GetPage: "+sql_to_query+" "+data_string+" "+ offset +" "+ pageSize);
        ArrayList<HashMap<String, Object>> hm = (ArrayList<HashMap<String, Object>>) this.jdbcTemplate.query(
            sql_to_query,
            new Object[]{data_string, new Integer(pageSize),new Integer(offset)}, new RowMapper() {
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    HashMap<String, Object> row = new HashMap<String, Object>();
                    row.put("id",String.valueOf(rs.getInt("id")));
                    row.put("periodo_pago",rs.getString("periodo_pago"));
                    row.put("numero_periodo",rs.getString("numero_periodo"));
                    row.put("estado",rs.getString("estado"));
                    return row;
                }
            }
        );
        return hm;
    }
    //Termina Metodos del catalogo de Periodicidad de Pago
    
    
    
 //Métodos para el Catalogo de Configuracion de Periodos de Pago
    @Override
    public ArrayList<HashMap<String, Object>> getConfigPeriodosPago_Datos(Integer id) {


        String sql_to_query = "select "
                 +"nom_periodos_conf.id, "
                 +"nom_periodos_conf.ano as anio, "
                 +"nom_periodos_conf.nom_periodicidad_pago_id as tipo_periodo, "
                 +"nom_periodos_conf.prefijo as prefijo "
            +"from nom_periodos_conf "
            +"left join nom_periodicidad_pago on nom_periodicidad_pago.id=nom_periodos_conf.nom_periodicidad_pago_id  "
            +"where nom_periodos_conf.id="+id;
            ArrayList<HashMap<String, Object>> dato_periodicidadpago = (ArrayList<HashMap<String, Object>>) this.jdbcTemplate.query(
            sql_to_query,
          
            new Object[]{}, new RowMapper(){
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    HashMap<String, Object> row = new HashMap<String, Object>();
                    row.put("id",String.valueOf(rs.getInt("id")));
                    row.put("anio",rs.getString("anio"));
                    row.put("tipo_periodo",rs.getString("tipo_periodo"));
                    row.put("prefijo",rs.getString("prefijo"));
       
                    return row;
                }
            }
        );
        return dato_periodicidadpago;
    }

    @Override
    public ArrayList<HashMap<String, Object>> getConfigPeriodosPago_PaginaGrid(String data_string, int offset, int pageSize, String orderBy, String asc) {
        String sql_busqueda = "select id from gral_bus_catalogos(?) as foo (id integer)";
        
	String sql_to_query = ""
        + "select "
            +"nom_periodos_conf.id, "
            +"nom_periodos_conf.ano as anio, "
            +"nom_periodicidad_pago.titulo as titulo, "
            +"nom_periodos_conf.prefijo as prefijo "
        +"from nom_periodos_conf "
        +"JOIN nom_periodicidad_pago on nom_periodicidad_pago.id=nom_periodos_conf.nom_periodicidad_pago_id "
        +"JOIN ("+sql_busqueda+") AS sbt ON sbt.id = nom_periodos_conf.id "
        +"where nom_periodos_conf.borrado_logico=false "
        +"order by "+orderBy+" "+asc+" limit ? OFFSET ? ";
        
        //System.out.println("Busqueda GetPage: "+sql_to_query+" "+data_string+" "+ offset +" "+ pageSize);
        ArrayList<HashMap<String, Object>> hm = (ArrayList<HashMap<String, Object>>) this.jdbcTemplate.query(
            sql_to_query,
            new Object[]{data_string, new Integer(pageSize),new Integer(offset)}, new RowMapper() {
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    HashMap<String, Object> row = new HashMap<String, Object>();
                    row.put("id",String.valueOf(rs.getInt("id")));
                    row.put("anio",rs.getString("anio"));
                    row.put("titulo",rs.getString("titulo"));
                    row.put("prefijo",rs.getString("prefijo"));
                    return row;
                }
            }
        );
        return hm;
    }
    
     //Alimenta select de tipo de Tipos Periodicidad
    @Override
    public ArrayList<HashMap<String, Object>> getPeriodicidad_Tipos(Integer id_empresa, Integer id_sucursal) {

        String sql_to_query = "select id,titulo, no_periodos from nom_periodicidad_pago where gral_emp_id=? and gral_suc_id=? and nom_periodicidad_pago.activo=true and nom_periodicidad_pago.borrado_logico=false order by titulo ";
        ArrayList<HashMap<String, Object>> percepciones = (ArrayList<HashMap<String, Object>>) this.jdbcTemplate.query(
            sql_to_query,
            new Object[]{new Integer(id_empresa), new Integer(id_sucursal)}, new RowMapper(){
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    HashMap<String, Object> row = new HashMap<String, Object>();
                    row.put("id",rs.getString("id"));
                    row.put("titulo",rs.getString("titulo"));
                    row.put("no_periodos",rs.getString("no_periodos"));
                    return row;
                }
            }
        );
        return percepciones;
    }
    //Termina Metodos Catalogo de Configuracion de Periodos de Pago.
    
    
     //Metodos para reporte de saldo Mensual------------------------------------------------------------------------------
    //Calcular años a mostrar en el reporte de Saldo Mansual
    @Override
    public ArrayList<HashMap<String, Object>>  getConfigPeriodosPago_Anios() {
        ArrayList<HashMap<String, Object>> anios = new ArrayList<HashMap<String, Object>>();
        
        Calendar c1 = Calendar.getInstance();
        Integer annio = c1.get(Calendar.YEAR);//obtiene el año actual
        
        for(int i=0; i<5; i++) {
            HashMap<String, Object> row = new HashMap<String, Object>();
            row.put("valor",(annio+i));
            anios.add(i, row);
        }
        return anios;
    }

     //Obtiene la lista de Periodos para editar Periodicidad de pago
    @Override
    public ArrayList<HashMap<String, Object>> getConfigPeriodosPago_Grid(Integer id) {
        String sql_to_query="select "
                +"nom_periodos_conf_det.id as id_reg, "
                +"nom_periodos_conf_det.nom_periodos_conf_id as id_periodo, "
                 +"nom_periodos_conf.nom_periodicidad_pago_id as tipo_periodo, "
                 +"nom_periodos_conf_det.folio as folio, "
                 +"nom_periodos_conf_det.titulo as tituloperiodo, "
                 +"(case when nom_periodos_conf_det.fecha_ini is null then '' else nom_periodos_conf_det.fecha_ini::character varying end) AS fecha_inicial, "
                 +"(case when nom_periodos_conf_det.fecha_fin is null then '' else nom_periodos_conf_det.fecha_fin::character varying end) AS fecha_final "
            +"from nom_periodos_conf_det "
            +"left join  nom_periodos_conf on nom_periodos_conf.id =nom_periodos_conf_det.nom_periodos_conf_id "
            +"where nom_periodos_conf_det.nom_periodos_conf_id=?"
            +"order by nom_periodos_conf_det.id;";
      
       ArrayList<HashMap<String,Object>>hm=(ArrayList<HashMap<String,Object>>)this.jdbcTemplate.query(
            sql_to_query,
            new Object[]{new Integer(id)},new RowMapper(){
                @Override
                public Object mapRow(ResultSet rs,int rowNum)throws SQLException{
                    HashMap<String,Object>row=new HashMap<String,Object>();
                    row.put("id_reg",String.valueOf(rs.getInt("id_reg")));
                    row.put("id_periodo",String.valueOf(rs.getInt("id_periodo")));
                    row.put("tipo_periodo",rs.getString("tipo_periodo"));
                    row.put("folio",String.valueOf(rs.getString("folio")));
                    row.put("tituloperiodo",rs.getString("tituloperiodo"));
                    row.put("fecha_inicial",rs.getString("fecha_inicial"));
                    row.put("fecha_final",rs.getString("fecha_final"));
                    return row;
                }
            }
        );
        return hm;
    }

    @Override
    public Map<String, Object> getTipoCambio_Url(Integer id_empresa) {
        String sql_to_query="";
        
        sql_to_query = ""
        + "select "
            + "(case when gral_tc_url.id is null then 0 else gral_tc_url.id end) as id, "
            + "(case when gral_tc_url.url is null then '' else gral_tc_url.url end) as url,"
            + "(case when gral_tc_url.institucion is null then '' else gral_tc_url.institucion end) as institucion "
        + "from gral_emp "
        + "left join gral_tc_url on gral_tc_url.id=gral_emp.gral_tc_url_id "
        + "where gral_emp.id="+id_empresa+";";
        Map<String, Object> map = this.getJdbcTemplate().queryForMap(sql_to_query);

        return map;
    }
    
    
    
    
    
    //Metodos para el Catalogo de IVA Trasladado
    @Override
    public ArrayList<HashMap<String, Object>> getImpTras_PaginaGrid(String data_string, int offset, int pageSize, String orderBy, String asc) {
        String sql_busqueda = "select id from gral_bus_catalogos(?) as foo (id integer)";

	String sql_to_query = ""
        +"select gral_imptos.id, gral_imptos.descripcion as titulo, (gral_imptos.iva_1 * 100) as tasa "
        +"from gral_imptos "
        +"join ("+sql_busqueda+") as sbt on sbt.id = gral_imptos.id "
        +"order by "+orderBy+" "+asc+" limit ? OFFSET ?";

        //System.out.println("Busqueda GetPage: "+sql_to_query+" "+data_string+" "+ offset +" "+ pageSize);
        ArrayList<HashMap<String, Object>> hm = (ArrayList<HashMap<String, Object>>) this.jdbcTemplate.query(
            sql_to_query,
            new Object[]{data_string, new Integer(pageSize),new Integer(offset)}, new RowMapper() {
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    HashMap<String, Object> row = new HashMap<String, Object>();
                    row.put("id",rs.getInt("id"));
                    row.put("titulo",rs.getString("titulo"));
                    row.put("tasa",StringHelper.roundDouble(rs.getString("tasa"),2));
                    return row;
                }
            }
        );
        return hm;
    }
    
    @Override
    public ArrayList<HashMap<String, Object>> getImpTras_Datos(Integer id) {
        String sql_to_query = "select gral_imptos.id, gral_imptos.descripcion as titulo, (gral_imptos.iva_1 * 100) as tasa from gral_imptos where gral_imptos.id=?;";
        
        ArrayList<HashMap<String, Object>> data = (ArrayList<HashMap<String, Object>>) this.jdbcTemplate.query(
            sql_to_query,
            new Object[]{new Integer(id)}, new RowMapper(){
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    HashMap<String, Object> row = new HashMap<String, Object>();
                    row.put("id",rs.getInt("id"));
                    row.put("titulo",rs.getString("titulo"));
                    row.put("tasa",StringHelper.roundDouble(rs.getString("tasa"),2));
                    
                    return row;
                }
            }
        );
        return data;
    }
    
    

    //Obtiene datos de la cuenta Contable de IVA Trasladado
    @Override
    public ArrayList<HashMap<String, Object>> getImpTras_DatosContabilidad(Integer id) {
        String sql_query = ""
        + "select "
            + "(case when tbl_cta.id is null then 0 else tbl_cta.id end) AS id_cta, "
            + "(case when tbl_cta.cta is null or tbl_cta.cta=0 then '' else tbl_cta.cta::character varying end) AS cta, "
            + "(case when tbl_cta.subcta is null or tbl_cta.subcta=0 then '' else tbl_cta.subcta::character varying end) AS subcta, "
            + "(case when tbl_cta.ssubcta is null or tbl_cta.ssubcta=0 then '' else tbl_cta.ssubcta::character varying end) AS ssubcta, "
            + "(case when tbl_cta.sssubcta is null or tbl_cta.sssubcta=0 then '' else tbl_cta.sssubcta::character varying end) AS sssubcta,"
            + "(case when tbl_cta.ssssubcta is null or tbl_cta.ssssubcta=0 then '' else tbl_cta.ssssubcta::character varying end) AS ssssubcta, "
            + "(case when tbl_cta.descripcion is null or tbl_cta.descripcion='' then  (case when tbl_cta.descripcion_ing is null or tbl_cta.descripcion_ing='' then  tbl_cta.descripcion_otr else tbl_cta.descripcion_ing END )  else tbl_cta.descripcion END ) AS descripcion "
        +"from gral_impto_cta "
        +"left join ctb_cta AS tbl_cta ON tbl_cta.id=gral_impto_cta.ctb_cta_id "
        +"where gral_impto_cta.gral_impto_id=?;";
        
        //System.out.println("getImpTras_DatosContabilidad: "+ sql_query);
        //System.out.println("id: "+ id);
        ArrayList<HashMap<String, Object>> contab = (ArrayList<HashMap<String, Object>>) this.jdbcTemplate.query(
            sql_query,
            new Object[]{new Integer(id)}, new RowMapper() {
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    HashMap<String, Object> row = new HashMap<String, Object>();
                    row.put("id_cta",rs.getString("id_cta"));
                    row.put("cta",rs.getString("cta"));
                    row.put("subcta",rs.getString("subcta"));
                    row.put("ssubcta",rs.getString("ssubcta"));
                    row.put("sssubcta",rs.getString("sssubcta"));
                    row.put("ssssubcta",rs.getString("ssssubcta"));
                    row.put("descripcion",rs.getString("descripcion"));
                    return row;
                }
            }
        );
        return contab;
    }
    
    
    
    
    //Metodos para el Catalogo de IVA Retenido
    @Override
    public ArrayList<HashMap<String, Object>> getImpRet_PaginaGrid(String data_string, int offset, int pageSize, String orderBy, String asc) {
        String sql_busqueda = "select id from gral_bus_catalogos(?) as foo (id integer)";

	String sql_to_query = ""
        +"select gral_imptos_ret.id, gral_imptos_ret.titulo, gral_imptos_ret.tasa "
        +"from gral_imptos_ret "
        +"join ("+sql_busqueda+") as sbt on sbt.id = gral_imptos_ret.id "
        +"order by "+orderBy+" "+asc+" limit ? OFFSET ?";

        //System.out.println("Busqueda GetPage: "+sql_to_query+" "+data_string+" "+ offset +" "+ pageSize);
        ArrayList<HashMap<String, Object>> hm = (ArrayList<HashMap<String, Object>>) this.jdbcTemplate.query(
            sql_to_query,
            new Object[]{data_string, new Integer(pageSize),new Integer(offset)}, new RowMapper() {
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    HashMap<String, Object> row = new HashMap<String, Object>();
                    row.put("id",rs.getInt("id"));
                    row.put("titulo",rs.getString("titulo"));
                    row.put("tasa",StringHelper.roundDouble(rs.getString("tasa"),2));
                    return row;
                }
            }
        );
        return hm;
    }
    
    @Override
    public ArrayList<HashMap<String, Object>> getImpRet_Datos(Integer id) {
        String sql_to_query = "select gral_imptos_ret.id, gral_imptos_ret.titulo, gral_imptos_ret.tasa from gral_imptos_ret where gral_imptos_ret.id=?;";
        
        ArrayList<HashMap<String, Object>> data = (ArrayList<HashMap<String, Object>>) this.jdbcTemplate.query(
            sql_to_query,
            new Object[]{new Integer(id)}, new RowMapper(){
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    HashMap<String, Object> row = new HashMap<String, Object>();
                    row.put("id",rs.getInt("id"));
                    row.put("titulo",rs.getString("titulo"));
                    row.put("tasa",StringHelper.roundDouble(rs.getString("tasa"),2));
                    
                    return row;
                }
            }
        );
        return data;
    }
    
    

    //Obtiene datos de la cuenta Contable de IVA Trasladado
    @Override
    public ArrayList<HashMap<String, Object>> getImpRet_DatosContabilidad(Integer id) {
        String sql_query = ""
        + "select "
            + "(case when tbl_cta.id is null then 0 else tbl_cta.id end) AS id_cta, "
            + "(case when tbl_cta.cta is null or tbl_cta.cta=0 then '' else tbl_cta.cta::character varying end) AS cta, "
            + "(case when tbl_cta.subcta is null or tbl_cta.subcta=0 then '' else tbl_cta.subcta::character varying end) AS subcta, "
            + "(case when tbl_cta.ssubcta is null or tbl_cta.ssubcta=0 then '' else tbl_cta.ssubcta::character varying end) AS ssubcta, "
            + "(case when tbl_cta.sssubcta is null or tbl_cta.sssubcta=0 then '' else tbl_cta.sssubcta::character varying end) AS sssubcta,"
            + "(case when tbl_cta.ssssubcta is null or tbl_cta.ssssubcta=0 then '' else tbl_cta.ssssubcta::character varying end) AS ssssubcta, "
            + "(case when tbl_cta.descripcion is null or tbl_cta.descripcion='' then  (case when tbl_cta.descripcion_ing is null or tbl_cta.descripcion_ing='' then  tbl_cta.descripcion_otr else tbl_cta.descripcion_ing END )  else tbl_cta.descripcion END ) AS descripcion "
        +"from gral_impto_ret_cta "
        +"left join ctb_cta AS tbl_cta ON tbl_cta.id=gral_impto_ret_cta.ctb_cta_id "
        +"where gral_impto_ret_cta.gral_impto_ret_id=?;";
        
        //System.out.println("getImpTras_DatosContabilidad: "+ sql_query);
        //System.out.println("id: "+ id);
        ArrayList<HashMap<String, Object>> contab = (ArrayList<HashMap<String, Object>>) this.jdbcTemplate.query(
            sql_query,
            new Object[]{new Integer(id)}, new RowMapper() {
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    HashMap<String, Object> row = new HashMap<String, Object>();
                    row.put("id_cta",rs.getString("id_cta"));
                    row.put("cta",rs.getString("cta"));
                    row.put("subcta",rs.getString("subcta"));
                    row.put("ssubcta",rs.getString("ssubcta"));
                    row.put("sssubcta",rs.getString("sssubcta"));
                    row.put("ssssubcta",rs.getString("ssssubcta"));
                    row.put("descripcion",rs.getString("descripcion"));
                    return row;
                }
            }
        );
        return contab;
    }
    
    
    
    @Override
    public ArrayList<HashMap<String, Object>> getMetodosDePago_PaginaGrid(String data_string, int offset, int pageSize, String orderBy, String asc) {
        String sql_busqueda = "select id from gral_bus_catalogos(?) as foo (id integer)";
        
	String sql_to_query = ""
        + "select "
            +"fac_metodos_pago.id, "
            +"fac_metodos_pago.clave_sat as clave, "
            +"fac_metodos_pago.titulo "
        +"from fac_metodos_pago "
        +"JOIN ("+sql_busqueda+") AS sbt ON sbt.id = fac_metodos_pago.id "
        +"order by "+orderBy+" "+asc+" limit ? OFFSET ? ";
        
        //System.out.println("Busqueda GetPage: "+sql_to_query+" "+data_string+" "+ offset +" "+ pageSize);
        ArrayList<HashMap<String, Object>> hm = (ArrayList<HashMap<String, Object>>) this.jdbcTemplate.query(
            sql_to_query,
            new Object[]{data_string, new Integer(pageSize),new Integer(offset)}, new RowMapper() {
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    HashMap<String, Object> row = new HashMap<String, Object>();
                    row.put("id",rs.getInt("id"));
                    row.put("clave",rs.getString("clave"));
                    row.put("titulo",rs.getString("titulo"));
                    return row;
                }
            }
        );
        return hm;
    }

    
    //Métodos para el Catalogo de Deducciones
    //Guarda los datos de los Deducciones
    @Override
    public ArrayList<HashMap<String, Object>> getMetodosDePago_Datos(Integer id) {
        String sql_to_query = "select id, clave_sat as clave, titulo from fac_metodos_pago where id=?;";
            ArrayList<HashMap<String, Object>> dato_puesto = (ArrayList<HashMap<String, Object>>) this.jdbcTemplate.query(
            sql_to_query,
            
            new Object[]{new Integer(id)}, new RowMapper(){
                @Override
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    HashMap<String, Object> row = new HashMap<String, Object>();
                    row.put("id",rs.getInt("id"));
                    row.put("clave",rs.getString("clave"));
                    row.put("titulo",rs.getString("titulo"));
                    return row;
                }
            }
        );
        return dato_puesto;
    }
    
    
}
