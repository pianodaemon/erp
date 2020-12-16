
/*
 @author: Valentin santos s.
 FECHA:  19 De Abril del 2013.
 Mail to:: valentin.vale8490@gmail.com
 */
package com.agnux.kemikal.reportes;

import com.agnux.common.helpers.StringHelper;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.Element;
import com.itextpdf.text.ExceptionConverter;
import com.itextpdf.text.Font;
import com.itextpdf.text.Image;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfPageEventHelper;
import com.itextpdf.text.pdf.PdfTemplate;
import com.itextpdf.text.pdf.PdfWriter;
import java.io.FileOutputStream;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;

public class PdfEnvasado {

    private HashMap<String, String> datosHeaderFooter = new HashMap<String, String>();
    private String folio_reenvasado;
    private String folio_o_produccion;
    private String equipo;
    private String merma;
    private Double existencia_presentacion;
    private double existencia_unidad;
    private ArrayList<HashMap<String, String>> datos_grid = new ArrayList<HashMap<String, String>>();
    private String fileout;
    private String ruta_imagen;
    private String emp_razon_social;
    private String emp_no_exterior;
    private String emp_calle;
    private String emp_colonia;
    private String emp_municipio;
    private String emp_estado;
    private String emp_pais;
    private String emp_cp;
    private String emp_rfc;
    private String nombre_empleado;
    private String nombre_almacen;
    private String codigo;
    private String descripcion;
    private String presentacion;
    private String fecha_programacion;
    private String hora_programacion;
    private String estado;
    private String unidad;

    public String getFolio_o_produccion() {
        return folio_o_produccion;
    }

    public void setFolio_o_produccion(String folio_o_produccion) {
        this.folio_o_produccion = folio_o_produccion;
    }

    public String getEquipo() {
        return equipo;
    }

    public void setEquipo(String equipo) {
        this.equipo = equipo;
    }

    public String getMerma() {
        return merma;
    }

    public void setMerma(String merma) {
        this.merma = merma;
    }

    public Double getExistencia_presentacion() {
        return existencia_presentacion;
    }

    public void setExistencia_presentacion(Double existencia_presentacion) {
        this.existencia_presentacion = existencia_presentacion;
    }

    public double getExistencia_unidad() {
        return existencia_unidad;
    }

    public void setExistencia_unidad(double existencia_unidad) {
        this.existencia_unidad = existencia_unidad;
    }

    public String getFolio_reenvasado() {
        return folio_reenvasado;
    }

    public void setFolio_reenvasado(String folio_reenvasado) {
        this.folio_reenvasado = folio_reenvasado;
    }

    public ArrayList<HashMap<String, String>> getDatos_grid() {
        return datos_grid;
    }

    public void setDatos_grid(ArrayList<HashMap<String, String>> datos_grid) {
        this.datos_grid = datos_grid;
    }

    public String getEmp_no_exterior() {
        return emp_no_exterior;
    }

    public void setEmp_no_exterior(String emp_no_exterior) {
        this.emp_no_exterior = emp_no_exterior;
    }

    public String getEmp_calle() {
        return emp_calle;
    }

    public void setEmp_calle(String emp_calle) {
        this.emp_calle = emp_calle;
    }

    public String getEmp_colonia() {
        return emp_colonia;
    }

    public void setEmp_colonia(String emp_colonia) {
        this.emp_colonia = emp_colonia;
    }

    public String getEmp_municipio() {
        return emp_municipio;
    }

    public void setEmp_municipio(String emp_municipio) {
        this.emp_municipio = emp_municipio;
    }

    public String getEmp_estado() {
        return emp_estado;
    }

    public void setEmp_estado(String emp_estado) {
        this.emp_estado = emp_estado;
    }

    public String getEmp_pais() {
        return emp_pais;
    }

    public void setEmp_pais(String emp_pais) {
        this.emp_pais = emp_pais;
    }

    public String getEmp_cp() {
        return emp_cp;
    }

    public void setEmp_cp(String emp_cp) {
        this.emp_cp = emp_cp;
    }

    public String getEmp_rfc() {
        return emp_rfc;
    }

    public void setEmp_rfc(String emp_rfc) {
        this.emp_rfc = emp_rfc;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public String getFecha_programacion() {
        return fecha_programacion;
    }

    public void setFecha_programacion(String fecha_programacion) {
        this.fecha_programacion = fecha_programacion;
    }

    public String getHora_programacion() {
        return hora_programacion;
    }

    public void setHora_programacion(String hora_programacion) {
        this.hora_programacion = hora_programacion;
    }

    public String getPresentacion() {
        return presentacion;
    }

    public void setPresentacion(String presentacion) {
        this.presentacion = presentacion;
    }

    public String getUnidad() {
        return unidad;
    }

    public void setUnidad(String unidad) {
        this.unidad = unidad;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getNombre_almacen() {
        return nombre_almacen;
    }

    public void setNombre_almacen(String nombre_almacen) {
        this.nombre_almacen = nombre_almacen;
    }

    public String getNombre_empleado() {
        return nombre_empleado;
    }

    public void setNombre_empleado(String nombre_empleado) {
        this.nombre_empleado = nombre_empleado;
    }

    public HashMap<String, String> getDatosHeaderFooter() {
        return datosHeaderFooter;
    }

    public void setDatosHeaderFooter(HashMap<String, String> datosHeaderFooter) {
        this.datosHeaderFooter = datosHeaderFooter;
    }

    public String getFileout() {
        return fileout;
    }

    public void setFileout(String fileout) {
        this.fileout = fileout;
    }

    public String getRuta_imagen() {
        return ruta_imagen;
    }

    public void setRuta_imagen(String ruta_imagen) {
        this.ruta_imagen = ruta_imagen;
    }

    public String getEmp_razon_social() {
        return emp_razon_social;
    }

    public void setEmp_razon_social(String emp_razon_social) {
        this.emp_razon_social = emp_razon_social;
    }

    public PdfEnvasado(HashMap<String, String> datos_header, ArrayList<HashMap<String, String>> datos_grid, String fileout, String ruta_imagen) {

        HashMap<String, String> datos = new HashMap<String, String>();
        this.setRuta_imagen(ruta_imagen);
        this.setFileout(fileout);
        this.setFolio_reenvasado(datos_header.get("folio"));
        this.setEmp_razon_social(datos_header.get("emp_razon_social"));

        this.setEmp_calle(datos_header.get("emp_calle"));
        this.setEmp_colonia(datos_header.get("emp_colonia"));
        this.setEmp_cp(datos_header.get("emp_cp"));
        this.setEmp_estado(datos_header.get("emp_estado"));
        this.setEmp_municipio(datos_header.get("emp_municipio"));
        this.setEmp_no_exterior(datos_header.get("emp_no_exterior"));
        this.setEmp_pais(datos_header.get("emp_pais"));

        this.setEmp_rfc(datos_header.get("emp_rfc"));

        this.setNombre_empleado(datos_header.get("nombre_operador"));
        this.setNombre_almacen(datos_header.get("almacen"));

        this.setFolio_o_produccion(datos_header.get("folio_o_produccion"));
        this.setCodigo(datos_header.get("codigo_producto"));
        this.setDescripcion(datos_header.get("descripcion_producto"));
        this.setPresentacion(datos_header.get("presentacion"));
        this.setUnidad(datos_header.get("unidad"));
        this.setMerma(datos_header.get("merma"));

        this.setExistencia_presentacion(Double.parseDouble(datos_header.get("presentacion_existente")));
        this.setExistencia_unidad(Double.parseDouble(datos_header.get("unidad_existente")));

        this.setEquipo(datos_header.get("equipo"));




        this.setFecha_programacion(datos_header.get("fecha"));
        this.setHora_programacion(datos_header.get("hora"));
        this.setEstado(datos_header.get("estado"));


        this.setDatos_grid(datos_grid);

        //datos para el encabezado, no se esta utilizando
        datos.put("empresa", "");
        datos.put("titulo_reporte", "");
        datos.put("periodo", "");

        //datos para el pie de pagina
        datos.put("codigo1", datos_header.get("codigo1"));
        datos.put("codigo2", datos_header.get("codigo2"));

        this.setDatosHeaderFooter(datos);



    }

    public void ViewPDF() throws URISyntaxException {
        Font smallBoldFontWhite = new Font(Font.FontFamily.HELVETICA, 8, Font.BOLD, BaseColor.WHITE);
        Font smallFont = new Font(Font.FontFamily.HELVETICA, 7, Font.NORMAL, BaseColor.BLACK);
        Font smallBoldFont = new Font(Font.FontFamily.HELVETICA, 8, Font.BOLD, BaseColor.BLACK);
        Font largeBoldFont = new Font(Font.FontFamily.HELVETICA, 10, Font.BOLD, BaseColor.BLACK);
        Font headerFont = new Font(Font.getFamily("ARIAL"), 8, Font.BOLD, BaseColor.WHITE);

        PdfEnvasado.ImagenPDF ipdf = new PdfEnvasado.ImagenPDF();
        PdfEnvasado.CeldaPDF cepdf = new PdfEnvasado.CeldaPDF();


        PdfPTable tablaPrincipal;
        PdfPTable table_datos;
        //PdfPTable tableElaboro;
        PdfPCell cell;

        try {


            PdfEnvasado.HeaderFooter event = new PdfEnvasado.HeaderFooter(this.getDatosHeaderFooter());
            Document doc = new Document(PageSize.LETTER, -50, -50, 20, 30);
            // Document document =      new Document(PageSize.LETTER.rotate(), -50, -50, 60, 30);
            PdfWriter writer = PdfWriter.getInstance(doc, new FileOutputStream(this.getFileout()));
            writer.setPageEvent(event);

            System.out.println("archivo de salida::  " + this.getFileout());

            doc.open();

            float[] widths = {6, 12, 6};
            tablaPrincipal = new PdfPTable(widths);
            tablaPrincipal.setKeepTogether(false);

            //IMAGEN --> logo empresa
            cell = new PdfPCell(ipdf.addContent());
            cell.setBorderWidthBottom(0);
            cell.setBorderWidthTop(0);
            cell.setBorderWidthRight(0);
            cell.setBorderWidthLeft(0);
            cell.setUseDescender(true);
            cell.setVerticalAlignment(Element.ALIGN_TOP);
            tablaPrincipal.addCell(cell);



            //AQUI COMIENZA LA TABLA PARA DATOS DEL HEADER(DATOS ENCABEZADO DEL REPORTE)
            PdfPTable tableDatosEmpresa = new PdfPTable(1);
            PdfPCell cellEmp;

            //RAZON SOCIAL --> BeanFromCFD (X_emisor)
            cellEmp = new PdfPCell(new Paragraph(StringHelper.capitalizaString(this.getEmp_razon_social()), largeBoldFont));
            cellEmp.setBorderWidthBottom(0);
            cellEmp.setBorderWidthTop(0);
            cellEmp.setBorderWidthRight(0);
            cellEmp.setBorderWidthLeft(0);
            cellEmp.setUseAscender(true);
            cellEmp.setHorizontalAlignment(Element.ALIGN_CENTER);
            tableDatosEmpresa.addCell(cellEmp);



            //celda vacia
            cellEmp = new PdfPCell(new Paragraph(" ", smallFont));
            cellEmp.setBorderWidthBottom(0);
            cellEmp.setBorderWidthTop(0);
            cellEmp.setBorderWidthRight(0);
            cellEmp.setBorderWidthLeft(0);
            tableDatosEmpresa.addCell(cellEmp);

            //DOMICILIO FISCAL --> texto
            cellEmp = new PdfPCell(new Paragraph("DOMICILIO FISCAL", smallBoldFont));
            cellEmp.setBorderWidthBottom(0);
            cellEmp.setBorderWidthTop(0);
            cellEmp.setBorderWidthRight(0);
            cellEmp.setBorderWidthLeft(0);
            cellEmp.setUseAscender(true);
            cellEmp.setHorizontalAlignment(Element.ALIGN_CENTER);
            tableDatosEmpresa.addCell(cellEmp);


            String cadena = StringHelper.capitalizaString(this.getEmp_calle()) + " "
                    + StringHelper.capitalizaString(this.getEmp_no_exterior()) + "\n"
                    + StringHelper.capitalizaString(this.getEmp_colonia()) + "\n"
                    + StringHelper.capitalizaString(this.getEmp_municipio()) + ", "
                    + StringHelper.capitalizaString(this.getEmp_estado()) + ", "
                    + StringHelper.capitalizaString(this.getEmp_pais()) + "\nC.P. "
                    + this.getEmp_cp() + "    R.F.C.: "
                    + StringHelper.capitalizaString(this.getEmp_rfc());
            //cellEmp = new PdfPCell(new Paragraph(StringHelper.capitalizaString(this.getEmp_calle()) + " " + StringHelper.capitalizaString(this.getEmp_no_exterior()) +  "\n" + StringHelper.capitalizaString(this.getEmp_colonia()) + "\n" + StringHelper.capitalizaString(this.getEmp_municipio()) + ", " + StringHelper.capitalizaString(this.getEmp_estado())+ ", " + StringHelper.capitalizaString(this.getEmp_pais()) + "\nC.P. " + this.getEmp_cp() + "    R.F.C.: " + StringHelper.capitalizaString(this.getEmp_rfc()), smallFont));
            cellEmp = new PdfPCell(new Paragraph(StringHelper.capitalizaString(cadena), smallFont));
            cellEmp.setBorderWidthBottom(0);
            cellEmp.setBorderWidthTop(0);
            cellEmp.setBorderWidthRight(0);
            cellEmp.setBorderWidthLeft(0);
            cellEmp.setUseAscender(true);
            cellEmp.setHorizontalAlignment(Element.ALIGN_CENTER);
            tableDatosEmpresa.addCell(cellEmp);
            //AQUI TERMINA LA TABLA PARA DATOS DE LA EMPRESA

            cellEmp = new PdfPCell(new Paragraph("", smallFont));
            cellEmp.setBorderWidthBottom(0);
            cellEmp.setBorderWidthTop(0);
            cellEmp.setBorderWidthRight(0);
            cellEmp.setBorderWidthLeft(0);
            cellEmp.setUseAscender(true);
            cellEmp.setHorizontalAlignment(Element.ALIGN_CENTER);
            tableDatosEmpresa.addCell(cellEmp);

            cellEmp = new PdfPCell(new Paragraph("", smallFont));
            cellEmp.setBorderWidthBottom(0);
            cellEmp.setBorderWidthTop(0);
            cellEmp.setBorderWidthRight(0);
            cellEmp.setBorderWidthLeft(0);
            cellEmp.setUseAscender(true);
            cellEmp.setHorizontalAlignment(Element.ALIGN_CENTER);
            tableDatosEmpresa.addCell(cellEmp);
            cellEmp = new PdfPCell(new Paragraph("", smallFont));
            cellEmp.setBorderWidthBottom(0);
            cellEmp.setBorderWidthTop(0);
            cellEmp.setBorderWidthRight(0);
            cellEmp.setBorderWidthLeft(0);
            cellEmp.setUseAscender(true);
            cellEmp.setHorizontalAlignment(Element.ALIGN_CENTER);
            tableDatosEmpresa.addCell(cellEmp);
            cellEmp = new PdfPCell(new Paragraph("", smallFont));
            cellEmp.setBorderWidthBottom(0);
            cellEmp.setBorderWidthTop(0);
            cellEmp.setBorderWidthRight(0);
            cellEmp.setBorderWidthLeft(0);
            cellEmp.setUseAscender(true);
            cellEmp.setHorizontalAlignment(Element.ALIGN_CENTER);
            tableDatosEmpresa.addCell(cellEmp);
            cellEmp = new PdfPCell(new Paragraph("", smallFont));
            cellEmp.setBorderWidthBottom(0);
            cellEmp.setBorderWidthTop(0);
            cellEmp.setBorderWidthRight(0);
            cellEmp.setBorderWidthLeft(0);
            cellEmp.setUseAscender(true);
            cellEmp.setHorizontalAlignment(Element.ALIGN_CENTER);
            tableDatosEmpresa.addCell(cellEmp);

            cellEmp = new PdfPCell(new Paragraph("", smallFont));
            cellEmp.setBorderWidthBottom(0);
            cellEmp.setBorderWidthTop(0);
            cellEmp.setBorderWidthRight(0);
            cellEmp.setBorderWidthLeft(0);
            cellEmp.setUseAscender(true);
            cellEmp.setHorizontalAlignment(Element.ALIGN_CENTER);
            tableDatosEmpresa.addCell(cellEmp);

            cellEmp = new PdfPCell(new Paragraph("", smallFont));
            cellEmp.setBorderWidthBottom(0);
            cellEmp.setBorderWidthTop(0);
            cellEmp.setBorderWidthRight(0);
            cellEmp.setBorderWidthLeft(0);
            cellEmp.setUseAscender(true);
            cellEmp.setHorizontalAlignment(Element.ALIGN_CENTER);
            tableDatosEmpresa.addCell(cellEmp);

            cellEmp = new PdfPCell(new Paragraph("", smallFont));
            cellEmp.setBorderWidthBottom(0);
            cellEmp.setBorderWidthTop(0);
            cellEmp.setBorderWidthRight(0);
            cellEmp.setBorderWidthLeft(0);
            cellEmp.setUseAscender(true);
            cellEmp.setHorizontalAlignment(Element.ALIGN_CENTER);
            tableDatosEmpresa.addCell(cellEmp);







            //aqui se agrega la tableDatosEmpresa a la tablaPrincipal
            cell = new PdfPCell(tableDatosEmpresa);
            cell.setBorderWidthBottom(0);
            cell.setBorderWidthTop(0);
            cell.setBorderWidthRight(0);
            cell.setBorderWidthLeft(0);
            tablaPrincipal.addCell(cell);





            ////////////////////////////////////////////////////////////////////////////////
            //aqui se agrega la tabla  superior derecha
            cell = new PdfPCell(cepdf.addContent());
            cell.setBorderWidthBottom(0);
            cell.setBorderWidthTop(0);
            cell.setBorderWidthRight(0);
            cell.setBorderWidthLeft(0);
            cell.setRowspan(2);
            tablaPrincipal.addCell(cell);
            ////////////////////////////////////////////////////////////////////////////////




            doc.add(tablaPrincipal);

            //AQUI COMIENZA LA TABLA PARA DATOS DEL HEADER
            float[] tamaños = {4, 6, 4, 6, 4f, 6f};
            table_datos = new PdfPTable(tamaños);
            PdfPCell celltable_datos;

            celltable_datos = new PdfPCell(new Paragraph("", smallBoldFont));
            celltable_datos.setBorderWidthBottom(0);
            celltable_datos.setBorderWidthTop(0);
            celltable_datos.setBorderWidthRight(0);
            celltable_datos.setBorderWidthLeft(0);
            celltable_datos.setFixedHeight(30);
            celltable_datos.setColspan(6);
            celltable_datos.setUseAscender(true);
            celltable_datos.setHorizontalAlignment(Element.ALIGN_LEFT);
            table_datos.addCell(celltable_datos);

            celltable_datos = new PdfPCell(new Paragraph("Almacen :", smallBoldFont));
            celltable_datos.setBorderWidthBottom(0);
            celltable_datos.setBorderWidthTop(0);
            celltable_datos.setBorderWidthRight(0);
            celltable_datos.setBorderWidthLeft(0);
            celltable_datos.setUseAscender(true);
            celltable_datos.setHorizontalAlignment(Element.ALIGN_LEFT);
            table_datos.addCell(celltable_datos);

            celltable_datos = new PdfPCell(new Paragraph(StringHelper.capitalizaString(this.getNombre_almacen()), smallFont));
            celltable_datos.setBorderWidthBottom(0);
            celltable_datos.setBorderWidthTop(0);
            celltable_datos.setBorderWidthRight(0);
            celltable_datos.setBorderWidthLeft(0);
            celltable_datos.setUseAscender(true);
            celltable_datos.setColspan(5);
            celltable_datos.setHorizontalAlignment(Element.ALIGN_LEFT);
            table_datos.addCell(celltable_datos);
////////////////////////////
            celltable_datos = new PdfPCell(new Paragraph("Operador :", smallBoldFont));
            celltable_datos.setBorderWidthBottom(0);
            celltable_datos.setBorderWidthTop(0);
            celltable_datos.setBorderWidthRight(0);
            celltable_datos.setBorderWidthLeft(0);
            celltable_datos.setUseAscender(true);
            celltable_datos.setHorizontalAlignment(Element.ALIGN_LEFT);
            table_datos.addCell(celltable_datos);

            celltable_datos = new PdfPCell(new Paragraph(StringHelper.capitalizaString(this.getNombre_empleado()), smallFont));
            celltable_datos.setBorderWidthBottom(0);
            celltable_datos.setBorderWidthTop(0);
            celltable_datos.setBorderWidthRight(0);
            celltable_datos.setBorderWidthLeft(0);
            celltable_datos.setUseAscender(true);
            celltable_datos.setHorizontalAlignment(Element.ALIGN_LEFT);
            table_datos.addCell(celltable_datos);

            celltable_datos = new PdfPCell(new Paragraph("Equipo :", smallBoldFont));
            celltable_datos.setBorderWidthBottom(0);
            celltable_datos.setBorderWidthTop(0);
            celltable_datos.setBorderWidthRight(0);
            celltable_datos.setBorderWidthLeft(0);
            celltable_datos.setUseAscender(true);
            celltable_datos.setHorizontalAlignment(Element.ALIGN_LEFT);
            table_datos.addCell(celltable_datos);

            celltable_datos = new PdfPCell(new Paragraph(this.getEquipo(), smallFont));
            celltable_datos.setBorderWidthBottom(0);
            celltable_datos.setBorderWidthTop(0);
            celltable_datos.setBorderWidthRight(0);
            celltable_datos.setBorderWidthLeft(0);
            celltable_datos.setUseAscender(true);
            celltable_datos.setHorizontalAlignment(Element.ALIGN_LEFT);
            table_datos.addCell(celltable_datos);

            celltable_datos = new PdfPCell(new Paragraph("Merma :", smallBoldFont));
            celltable_datos.setBorderWidthBottom(0);
            celltable_datos.setBorderWidthTop(0);
            celltable_datos.setBorderWidthRight(0);
            celltable_datos.setBorderWidthLeft(0);
            celltable_datos.setUseAscender(true);
            celltable_datos.setHorizontalAlignment(Element.ALIGN_LEFT);
            table_datos.addCell(celltable_datos);

            celltable_datos = new PdfPCell(new Paragraph(this.getMerma(), smallFont));
            celltable_datos.setBorderWidthBottom(0);
            celltable_datos.setBorderWidthTop(0);
            celltable_datos.setBorderWidthRight(0);
            celltable_datos.setBorderWidthLeft(0);
            celltable_datos.setUseAscender(true);
            celltable_datos.setHorizontalAlignment(Element.ALIGN_LEFT);
            table_datos.addCell(celltable_datos);
////////////////////////////

            celltable_datos = new PdfPCell(new Paragraph("Folio O.Prod :", smallBoldFont));
            celltable_datos.setBorderWidthBottom(0);
            celltable_datos.setBorderWidthTop(0);
            celltable_datos.setBorderWidthRight(0);
            celltable_datos.setBorderWidthLeft(0);
            celltable_datos.setUseAscender(true);
            celltable_datos.setHorizontalAlignment(Element.ALIGN_LEFT);
            table_datos.addCell(celltable_datos);

            celltable_datos = new PdfPCell(new Paragraph(StringHelper.capitalizaString(this.getFolio_o_produccion()), smallFont));
            celltable_datos.setBorderWidthBottom(0);
            celltable_datos.setBorderWidthTop(0);
            celltable_datos.setBorderWidthRight(0);
            celltable_datos.setBorderWidthLeft(0);
            celltable_datos.setUseAscender(true);
            celltable_datos.setHorizontalAlignment(Element.ALIGN_LEFT);
            table_datos.addCell(celltable_datos);

            celltable_datos = new PdfPCell(new Paragraph("Codigo :", smallBoldFont));
            celltable_datos.setBorderWidthBottom(0);
            celltable_datos.setBorderWidthTop(0);
            celltable_datos.setBorderWidthRight(0);
            celltable_datos.setBorderWidthLeft(0);
            celltable_datos.setUseAscender(true);
            celltable_datos.setHorizontalAlignment(Element.ALIGN_LEFT);
            table_datos.addCell(celltable_datos);

            celltable_datos = new PdfPCell(new Paragraph(StringHelper.capitalizaString(this.getCodigo()), smallFont));
            celltable_datos.setBorderWidthBottom(0);
            celltable_datos.setBorderWidthTop(0);
            celltable_datos.setBorderWidthRight(0);
            celltable_datos.setBorderWidthLeft(0);
            celltable_datos.setUseAscender(true);
            celltable_datos.setHorizontalAlignment(Element.ALIGN_LEFT);
            table_datos.addCell(celltable_datos);

            celltable_datos = new PdfPCell(new Paragraph("Descripcion :", smallBoldFont));
            celltable_datos.setBorderWidthBottom(0);
            celltable_datos.setBorderWidthTop(0);
            celltable_datos.setBorderWidthRight(0);
            celltable_datos.setBorderWidthLeft(0);
            celltable_datos.setUseAscender(true);
            celltable_datos.setHorizontalAlignment(Element.ALIGN_LEFT);
            table_datos.addCell(celltable_datos);

            celltable_datos = new PdfPCell(new Paragraph(StringHelper.capitalizaString(this.getDescripcion()), smallFont));
            celltable_datos.setBorderWidthBottom(0);
            celltable_datos.setBorderWidthTop(0);
            celltable_datos.setBorderWidthRight(0);
            celltable_datos.setBorderWidthLeft(0);
            celltable_datos.setUseAscender(true);
            celltable_datos.setHorizontalAlignment(Element.ALIGN_LEFT);
            table_datos.addCell(celltable_datos);
////////////////////////////

            celltable_datos = new PdfPCell(new Paragraph("Presentacion :", smallBoldFont));
            celltable_datos.setBorderWidthBottom(0);
            celltable_datos.setBorderWidthTop(0);
            celltable_datos.setBorderWidthRight(0);
            celltable_datos.setBorderWidthLeft(0);
            celltable_datos.setUseAscender(true);
            celltable_datos.setHorizontalAlignment(Element.ALIGN_LEFT);
            table_datos.addCell(celltable_datos);

            celltable_datos = new PdfPCell(new Paragraph(StringHelper.capitalizaString(this.getPresentacion()), smallFont));
            celltable_datos.setBorderWidthBottom(0);
            celltable_datos.setBorderWidthTop(0);
            celltable_datos.setBorderWidthRight(0);
            celltable_datos.setBorderWidthLeft(0);
            celltable_datos.setUseAscender(true);
            celltable_datos.setHorizontalAlignment(Element.ALIGN_LEFT);
            table_datos.addCell(celltable_datos);


            celltable_datos = new PdfPCell(new Paragraph("Existencia pres:", smallBoldFont));
            celltable_datos.setBorderWidthBottom(0);
            celltable_datos.setBorderWidthTop(0);
            celltable_datos.setBorderWidthRight(0);
            celltable_datos.setBorderWidthLeft(0);
            celltable_datos.setUseAscender(true);
            celltable_datos.setHorizontalAlignment(Element.ALIGN_LEFT);
            table_datos.addCell(celltable_datos);

            celltable_datos = new PdfPCell(new Paragraph("" + this.getExistencia_presentacion(), smallFont));
            celltable_datos.setBorderWidthBottom(0);
            celltable_datos.setBorderWidthTop(0);
            celltable_datos.setBorderWidthRight(0);
            celltable_datos.setBorderWidthLeft(0);
            celltable_datos.setUseAscender(true);
            celltable_datos.setHorizontalAlignment(Element.ALIGN_LEFT);
            table_datos.addCell(celltable_datos);

            celltable_datos = new PdfPCell(new Paragraph("", smallBoldFont));
            celltable_datos.setBorderWidthBottom(0);
            celltable_datos.setBorderWidthTop(0);
            celltable_datos.setBorderWidthRight(0);
            celltable_datos.setBorderWidthLeft(0);
            celltable_datos.setUseAscender(true);
            celltable_datos.setHorizontalAlignment(Element.ALIGN_LEFT);
            table_datos.addCell(celltable_datos);

            celltable_datos = new PdfPCell(new Paragraph("", smallFont));
            celltable_datos.setBorderWidthBottom(0);
            celltable_datos.setBorderWidthTop(0);
            celltable_datos.setBorderWidthRight(0);
            celltable_datos.setBorderWidthLeft(0);
            celltable_datos.setUseAscender(true);
            celltable_datos.setHorizontalAlignment(Element.ALIGN_LEFT);
            table_datos.addCell(celltable_datos);
////////////////////////////



            celltable_datos = new PdfPCell(new Paragraph("Unidad :", smallBoldFont));
            celltable_datos.setBorderWidthBottom(0);
            celltable_datos.setBorderWidthTop(0);
            celltable_datos.setBorderWidthRight(0);
            celltable_datos.setBorderWidthLeft(0);
            celltable_datos.setUseAscender(true);
            celltable_datos.setHorizontalAlignment(Element.ALIGN_LEFT);
            table_datos.addCell(celltable_datos);

            celltable_datos = new PdfPCell(new Paragraph(StringHelper.capitalizaString(this.getUnidad()), smallFont));
            celltable_datos.setBorderWidthBottom(0);
            celltable_datos.setBorderWidthTop(0);
            celltable_datos.setBorderWidthRight(0);
            celltable_datos.setBorderWidthLeft(0);
            celltable_datos.setUseAscender(true);
            celltable_datos.setHorizontalAlignment(Element.ALIGN_LEFT);
            table_datos.addCell(celltable_datos);

            celltable_datos = new PdfPCell(new Paragraph("existencia unidad:", smallBoldFont));
            celltable_datos.setBorderWidthBottom(0);
            celltable_datos.setBorderWidthTop(0);
            celltable_datos.setBorderWidthRight(0);
            celltable_datos.setBorderWidthLeft(0);
            celltable_datos.setUseAscender(true);
            celltable_datos.setHorizontalAlignment(Element.ALIGN_LEFT);
            table_datos.addCell(celltable_datos);

            celltable_datos = new PdfPCell(new Paragraph("" + this.getExistencia_unidad(), smallFont));
            celltable_datos.setBorderWidthBottom(0);
            celltable_datos.setBorderWidthTop(0);
            celltable_datos.setBorderWidthRight(0);
            celltable_datos.setBorderWidthLeft(0);
            celltable_datos.setUseAscender(true);
            celltable_datos.setHorizontalAlignment(Element.ALIGN_LEFT);
            table_datos.addCell(celltable_datos);

            celltable_datos = new PdfPCell(new Paragraph("", smallBoldFont));
            celltable_datos.setBorderWidthBottom(0);
            celltable_datos.setBorderWidthTop(0);
            celltable_datos.setBorderWidthRight(0);
            celltable_datos.setBorderWidthLeft(0);
            celltable_datos.setUseAscender(true);
            celltable_datos.setHorizontalAlignment(Element.ALIGN_LEFT);
            table_datos.addCell(celltable_datos);

            celltable_datos = new PdfPCell(new Paragraph("", smallFont));
            celltable_datos.setBorderWidthBottom(0);
            celltable_datos.setBorderWidthTop(0);
            celltable_datos.setBorderWidthRight(0);
            celltable_datos.setBorderWidthLeft(0);
            celltable_datos.setUseAscender(true);
            celltable_datos.setHorizontalAlignment(Element.ALIGN_LEFT);
            table_datos.addCell(celltable_datos);


            doc.add(table_datos);
            /*cell = new PdfPCell(table_datos);
             cell.setBorderWidthBottom(0);
             cell.setBorderWidthTop(0);
             cell.setBorderWidthRight(0);
             cell.setBorderWidthLeft(0);
             cell.setColspan(2);
             tablaPrincipal.addCell(cell);

             //FILA VACIA
             cell = new PdfPCell(new Paragraph("",smallBoldFont));
             cell.setFixedHeight(10);
             cell.setBorder(0);
             cell.setColspan(3);
             tablaPrincipal.addCell(cell);
             */


            float[] columnas = {7, 7, 4, 4, 4, 4};
            PdfPTable table_grid = new PdfPTable(columnas);
            PdfPCell cell_table_grid;

            table_grid.setKeepTogether(false);
            table_grid.setHeaderRows(1);

            cell_table_grid = new PdfPCell(new Paragraph("ALMACEN ORIGEN", headerFont));
            cell_table_grid.setUseAscender(true);
            cell_table_grid.setHorizontalAlignment(Element.ALIGN_LEFT);
            cell_table_grid.setUseDescender(true);
            cell_table_grid.setVerticalAlignment(Element.ALIGN_MIDDLE);
            cell_table_grid.setBackgroundColor(BaseColor.BLACK);
            cell_table_grid.setFixedHeight(13);
            table_grid.addCell(cell_table_grid);
            // Encabezado de Celda
            cell_table_grid = new PdfPCell(new Paragraph("ALMACEN DESTINO", headerFont));
            cell_table_grid.setUseAscender(true);
            cell_table_grid.setHorizontalAlignment(Element.ALIGN_LEFT);
            cell_table_grid.setUseDescender(true);
            cell_table_grid.setVerticalAlignment(Element.ALIGN_MIDDLE);
            cell_table_grid.setBackgroundColor(BaseColor.BLACK);
            cell_table_grid.setFixedHeight(13);
            table_grid.addCell(cell_table_grid);

            cell_table_grid = new PdfPCell(new Paragraph("PRESENTACION", headerFont));
            cell_table_grid.setUseAscender(true);
            cell_table_grid.setHorizontalAlignment(Element.ALIGN_LEFT);
            cell_table_grid.setUseDescender(true);
            cell_table_grid.setVerticalAlignment(Element.ALIGN_MIDDLE);
            cell_table_grid.setBackgroundColor(BaseColor.BLACK);
            cell_table_grid.setFixedHeight(13);
            table_grid.addCell(cell_table_grid);

            cell_table_grid = new PdfPCell(new Paragraph("CANTIDAD PRES.", headerFont));
            cell_table_grid.setUseAscender(true);
            cell_table_grid.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell_table_grid.setUseDescender(true);
            cell_table_grid.setVerticalAlignment(Element.ALIGN_MIDDLE);
            cell_table_grid.setBackgroundColor(BaseColor.BLACK);
            cell_table_grid.setFixedHeight(13);
            table_grid.addCell(cell_table_grid);

            cell_table_grid = new PdfPCell(new Paragraph("UNIDAD", headerFont));
            cell_table_grid.setUseAscender(true);
            cell_table_grid.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell_table_grid.setUseDescender(true);
            cell_table_grid.setVerticalAlignment(Element.ALIGN_MIDDLE);
            cell_table_grid.setBackgroundColor(BaseColor.BLACK);
            cell_table_grid.setFixedHeight(13);
            table_grid.addCell(cell_table_grid);

            cell_table_grid = new PdfPCell(new Paragraph("CANTIDAD UNIDAD", headerFont));
            cell_table_grid.setUseAscender(true);
            cell_table_grid.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell_table_grid.setUseDescender(true);
            cell_table_grid.setVerticalAlignment(Element.ALIGN_MIDDLE);
            cell_table_grid.setBackgroundColor(BaseColor.BLACK);
            cell_table_grid.setFixedHeight(13);
            table_grid.addCell(cell_table_grid);

            System.out.println("Tamaño :" + this.getDatos_grid().size());
            /*  PROPIEDADES DE CELL PARA PONERLE O QUITARLE BORDES A UNA CELDA
             cell_table_grid.setBorder(0);
             cell_table_grid.setBorderWidthBottom(0);//bord inferior
             cell_table_grid.setBorderWidthTop(0); // border de arriba
             cell_table_grid.setBorderWidthRight(0); //border de la derecha
             cell_table_grid.setBorderWidthLeft(0);//borde izquierda
             */

            if (this.getDatos_grid().size() > 0) {


                for (int x = 0; x <= this.getDatos_grid().size() - 1; x++) {
                    HashMap<String, String> registro = this.getDatos_grid().get(x);


                    //almacen_destino
                    cell_table_grid = new PdfPCell(new Paragraph(registro.get("almacen_origen"), smallFont));
                    cell_table_grid.setHorizontalAlignment(Element.ALIGN_LEFT);
                    cell_table_grid.setVerticalAlignment(Element.ALIGN_MIDDLE);
                    //cell_table_grid.setBorder(0);
                    //cell_table_grid.setBorderWidthBottom(0);//bord inferior
                    cell_table_grid.setBorderWidthTop(0); // border de arriba
                    cell_table_grid.setBorderWidthRight(0); //border de la derecha
                    //cell_table_grid.setBorderWidthLeft(0);//borde izquierda
                    table_grid.addCell(cell_table_grid);

                    //almacen_destino
                    cell_table_grid = new PdfPCell(new Paragraph(registro.get("almacen_destino"), smallFont));
                    cell_table_grid.setHorizontalAlignment(Element.ALIGN_LEFT);
                    cell_table_grid.setVerticalAlignment(Element.ALIGN_MIDDLE);
                    //cell_table_grid.setBorder(0);
                    //cell_table_grid.setBorderWidthBottom(0);//bord inferior
                    cell_table_grid.setBorderWidthTop(0); // border de arriba
                    cell_table_grid.setBorderWidthRight(0); //border de la derecha
                    //cell_table_grid.setBorderWidthLeft(0);//borde izquierda
                    table_grid.addCell(cell_table_grid);

                    //Tipo de almacen_destino
                    cell_table_grid = new PdfPCell(new Paragraph(registro.get("presentacion_destino"), smallFont));
                    cell_table_grid.setHorizontalAlignment(Element.ALIGN_LEFT);
                    cell_table_grid.setVerticalAlignment(Element.ALIGN_MIDDLE);
                    //cell_table_grid.setBorder(0);
                    //cell_table_grid.setBorderWidthBottom(0);//bord inferior
                    cell_table_grid.setBorderWidthTop(0); // border de arriba
                    cell_table_grid.setBorderWidthRight(0); //border de la derecha
                    cell_table_grid.setBorderWidthLeft(0);//borde izquierda
                    table_grid.addCell(cell_table_grid);



                    //Fecha del cantidad_destino
                    cell_table_grid = new PdfPCell(new Paragraph(registro.get("cantidad_presentacion"), smallFont));
                    cell_table_grid.setHorizontalAlignment(Element.ALIGN_RIGHT);
                    cell_table_grid.setVerticalAlignment(Element.ALIGN_MIDDLE);
                    //cell_table_grid.setBorder(0);
                    //cell_table_grid.setBorderWidthBottom(0);//bord inferior
                    cell_table_grid.setBorderWidthTop(0); // border de arriba
                    cell_table_grid.setBorderWidthRight(0); //border de la derecha
                    cell_table_grid.setBorderWidthLeft(0);//borde izquierda
                    table_grid.addCell(cell_table_grid);
                    //unidad_destino
                    cell_table_grid = new PdfPCell(new Paragraph(registro.get("unidad_destino"), smallFont));
                    cell_table_grid.setHorizontalAlignment(Element.ALIGN_LEFT);
                    cell.setVerticalAlignment(Element.ALIGN_MIDDLE);

                    //cell_table_grid.setBorder(0);
                    //cell_table_grid.setBorderWidthBottom(0);//bord inferior
                    cell_table_grid.setBorderWidthTop(0); // border de arriba
                    cell_table_grid.setBorderWidthRight(0); //border de la derecha
                    cell_table_grid.setBorderWidthLeft(0);//borde izquierda
                    table_grid.addCell(cell_table_grid);
                    //unidad_destino
                    cell_table_grid = new PdfPCell(new Paragraph(registro.get("cantidad_unidad"), smallFont));
                    cell_table_grid.setHorizontalAlignment(Element.ALIGN_RIGHT);
                    cell_table_grid.setVerticalAlignment(Element.ALIGN_MIDDLE);
                    //cell_table_grid.setBorder(0);
                    //cell_table_grid.setBorderWidthBottom(0);//bord inferior
                    cell_table_grid.setBorderWidthTop(0); // border de arriba
                    //cell_table_grid.setBorderWidthRight(0); //border de la derecha
                    cell_table_grid.setBorderWidthLeft(0);//borde izquierda
                    table_grid.addCell(cell_table_grid);




                }
            } else {
                cell_table_grid = new PdfPCell(new Paragraph("Esta consulta no genero ningun Resultado", smallFont));
                cell_table_grid.setHorizontalAlignment(Element.ALIGN_LEFT);
                //cell_table_grid.setBorder(0);
                cell_table_grid.setColspan(8);
                cell_table_grid.setFixedHeight(18);
                table_grid.addCell(cell_table_grid);
            }






            doc.add(table_grid);
            doc.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }// fin del constructor

    //esta es la tabla que va en la parte Superior Derecha
    private class CeldaPDF {

        public PdfPTable addContent() {

            Font smallFont = new Font(Font.FontFamily.HELVETICA, 7, Font.NORMAL, BaseColor.BLACK);
            Font sont = new Font(Font.FontFamily.HELVETICA, 8, Font.BOLD, BaseColor.BLACK);
            Font smallBoldFont = new Font(Font.FontFamily.HELVETICA, 8, Font.BOLD, BaseColor.WHITE);
            Font smallBoldFontBlack = new Font(Font.FontFamily.HELVETICA, 8, Font.BOLD, BaseColor.BLACK);
            PdfPTable table = new PdfPTable(1);
            PdfPCell cell;


            cell = new PdfPCell(new Paragraph("FOLIO", smallBoldFont));
            cell.setUseAscender(true);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setUseDescender(true);
            cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            cell.setBackgroundColor(BaseColor.BLACK);
            table.addCell(cell);

            cell = new PdfPCell(new Paragraph(getFolio_reenvasado(), sont));
            cell.setUseAscender(true);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setUseDescender(true);
            cell.setVerticalAlignment(Element.ALIGN_TOP);
            table.addCell(cell);

            cell = new PdfPCell(new Paragraph("FECHA ", smallBoldFont));
            cell.setUseAscender(true);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setUseDescender(true);
            cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            cell.setBackgroundColor(BaseColor.BLACK);
            table.addCell(cell);

            cell = new PdfPCell(new Paragraph(getFecha_programacion(), sont));
            cell.setUseAscender(true);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setUseDescender(true);
            cell.setVerticalAlignment(Element.ALIGN_TOP);
            table.addCell(cell);

            cell = new PdfPCell(new Paragraph("HORA", smallBoldFont));
            cell.setUseAscender(true);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setUseDescender(true);
            cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            cell.setBackgroundColor(BaseColor.BLACK);
            table.addCell(cell);

            cell = new PdfPCell(new Paragraph(getHora_programacion(), sont));
            cell.setUseAscender(true);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setUseDescender(true);
            cell.setVerticalAlignment(Element.ALIGN_TOP);
            table.addCell(cell);



            cell = new PdfPCell(new Paragraph("ESTADO", smallBoldFont));
            cell.setUseAscender(true);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setUseDescender(true);
            cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            cell.setBackgroundColor(BaseColor.BLACK);
            table.addCell(cell);

            cell = new PdfPCell(new Paragraph(getEstado(), sont));
            cell.setUseAscender(true);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setUseDescender(true);
            cell.setVerticalAlignment(Element.ALIGN_TOP);
            table.addCell(cell);







            return table;
        }
    }// fin de la tabla  anidada en lado derecho

    private class ImagenPDF {

        public Image addContent() {
            Image img = null;
            try {
                img = Image.getInstance(getRuta_imagen());
                //img.scaleAbsoluteHeight(100);
                img.scaleAbsoluteHeight(65);
                img.scaleAbsoluteWidth(120);
                img.setAlignment(0);
            } catch (Exception e) {
                System.out.println(e);
            }
            return img;
        }
    }

    static class HeaderFooter extends PdfPageEventHelper {

        protected PdfTemplate total;
        protected BaseFont helv;
        protected PdfContentByte cb;
        Font largeBoldFont = new Font(Font.FontFamily.HELVETICA, 10, Font.BOLD, BaseColor.BLACK);
        Font largeFont = new Font(Font.FontFamily.HELVETICA, 10, Font.NORMAL, BaseColor.BLACK);
        Font smallFont = new Font(Font.FontFamily.HELVETICA, 7, Font.NORMAL, BaseColor.BLACK);
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
        HeaderFooter(HashMap<String, String> datos) {
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
            } catch (Exception e) {
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
            cb.setTextMatrix(document.left() + 85, textBase);  //definir la posicion de text
            cb.showText(text_left);
            cb.endText();


            //texto centro pie de pagina
            String text_center = "Página " + writer.getPageNumber() + " de ";
            float text_center_Size = helv.getWidthPoint(text_center, 7);
            float pos_text_center = (document.getPageSize().getWidth() / 2) - (text_center_Size / 2);
            float adjust = text_center_Size + 3;
            cb.beginText();
            cb.setFontAndSize(helv, 7);
            cb.setTextMatrix(pos_text_center, textBase);  //definir la posicion de text
            cb.showText(text_center);
            cb.endText();
            cb.addTemplate(total, pos_text_center + adjust, textBase);


            //texto inferior derecha pie de pagina
            String text_right = this.getCodigo2();
            float textRightSize = helv.getWidthPoint(text_right, 7);
            float pos_text_right = document.getPageSize().getWidth() - textRightSize - 40;
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
            total.setTextMatrix(0, 0);
            total.showText(String.valueOf(writer.getPageNumber() - 1));
            total.endText();
        }
    }//termina clase HeaderFooter
}//fin de la clase
