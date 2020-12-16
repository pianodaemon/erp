/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.agnux.kemikal.reportes;

import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import org.apache.poi.hssf.usermodel.HSSFRichTextString;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.CellRangeAddress;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.DataFormat;
import org.apache.poi.ss.usermodel.Font;

/**
 *
 * @author agnux
 */
public class CxcRepVentasAnualesClientesXls {
    
    public CxcRepVentasAnualesClientesXls(String fileout, String tituloReporte, String razon_soc_empresa, String periodo,ArrayList<HashMap<String, String>> datos) {
        
        // Se crea el libro
        HSSFWorkbook libro = new HSSFWorkbook();

        // Se crea una hoja dentro del libro
        HSSFSheet hoja1 = libro.createSheet("REPORTE");
        hoja1.autoSizeColumn(0);
        hoja1.autoSizeColumn(1);
        hoja1.autoSizeColumn(2);
        hoja1.autoSizeColumn(3);
        hoja1.autoSizeColumn(4);
        hoja1.autoSizeColumn(5);
        hoja1.autoSizeColumn(6);

        DataFormat format = libro.createDataFormat();
        boolean formatoNumerico=false;
        short sin_formato = format.getFormat("");

        Double totalano=0.0;
        Double totalmesenero=0.0;
        Double totalmesfebrero=0.0;
        Double totalmesmarzo=0.0;
        Double totalmesabril=0.0;
        Double totalmesmayo=0.0;
        Double totalmesjunio=0.0;
        Double totalmesjulio=0.0;
        Double totalmesagosto=0.0;
        Double totalmesseptiembre=0.0;
        Double totalmesoctubre=0.0;
        Double totalmesnoviembre=0.0;
        Double totalmesdiciembre=0.0;

        Double totalano2=0.0;
        Double totalmesenero1=0.0;
        Double totalmesfebrero2=0.0;
        Double totalmesmarzo3=0.0;
        Double totalmesabril4=0.0;
        Double totalmesmayo5=0.0;
        Double totalmesjunio6=0.0;
        Double totalmesjulio7=0.0;
        Double totalmesagosto8=0.0;
        Double totalmesseptiembre9=0.0;
        Double totalmesoctubre10=0.0;
        Double totalmesnoviembre11=0.0;
        Double totalmesdiciembre12=0.0;

        Double totalano3=0.0;
        Double totalmesenero11=0.0;
        Double totalmesfebrero12=0.0;
        Double totalmesmarzo13=0.0;
        Double totalmesabril14=0.0;
        Double totalmesmayo15=0.0;
        Double totalmesjunio16=0.0;
        Double totalmesjulio17=0.0;
        Double totalmesagosto18=0.0;
        Double totalmesseptiembre19=0.0;
        Double totalmesoctubre110=0.0;
        Double totalmesnoviembre111=0.0;
        Double totalmesdiciembre112=0.0;
        
        int noRow=0;
        // Se crea una fila1 dentro de la hoja
        HSSFRow fila1 = hoja1.createRow(noRow);
        //HSSFCell celda11 = fila1.createCell((short)0);

        createCell(libro, fila1,(short) 0, CellStyle.ALIGN_CENTER, CellStyle.VERTICAL_CENTER, razon_soc_empresa, Font.BOLDWEIGHT_BOLD, formatoNumerico, sin_formato);

        //Combinar celdas
        hoja1.addMergedRegion(new CellRangeAddress(
                noRow, //first row (0-based)
                noRow, //last row  (0-based)
                0, //first column (0-based)
                39  //last column  (0-based)
        ));


        // Se crea una fila2 dentro de la hoja
        noRow=1;
        HSSFRow fila2 = hoja1.createRow(noRow);
        createCell(libro, fila2,(short) 0, CellStyle.ALIGN_CENTER, CellStyle.VERTICAL_CENTER, tituloReporte, Font.BOLDWEIGHT_BOLD, formatoNumerico, sin_formato);
        hoja1.addMergedRegion(new CellRangeAddress(noRow,noRow,0,39));

        // Se crea una fila3 dentro de la hoja
        noRow=2;
        HSSFRow fila3 = hoja1.createRow(noRow);
        createCell(libro, fila3,(short) 0, CellStyle.ALIGN_CENTER, CellStyle.VERTICAL_CENTER, periodo, Font.BOLDWEIGHT_BOLD, formatoNumerico, sin_formato);
        hoja1.addMergedRegion(new CellRangeAddress(noRow,noRow,0,39));

        // Se crea una fila5 dentro de la hoja
        //Aqui se crean los encabezados de las columnas
        noRow=4;
        HSSFRow fila4 = hoja1.createRow(noRow);
        createCell(libro, fila4,(short) 0, CellStyle.ALIGN_LEFT, CellStyle.VERTICAL_CENTER, "Razon Social", Font.BOLDWEIGHT_BOLD, formatoNumerico, sin_formato);
        createCell(libro, fila4,(short) 1, CellStyle.ALIGN_CENTER, CellStyle.VERTICAL_CENTER, "Enero", Font.BOLDWEIGHT_BOLD, formatoNumerico, sin_formato);
        hoja1.addMergedRegion(new CellRangeAddress(noRow,noRow,1,3));
        createCell(libro, fila4,(short) 4, CellStyle.ALIGN_CENTER, CellStyle.VERTICAL_CENTER, "Febrero", Font.BOLDWEIGHT_BOLD, formatoNumerico, sin_formato);
        hoja1.addMergedRegion(new CellRangeAddress(noRow,noRow,4,6));
        createCell(libro, fila4,(short) 7, CellStyle.ALIGN_CENTER, CellStyle.VERTICAL_CENTER, "Marzo", Font.BOLDWEIGHT_BOLD, formatoNumerico, sin_formato);
        hoja1.addMergedRegion(new CellRangeAddress(noRow,noRow,7,9));
        createCell(libro, fila4,(short) 10, CellStyle.ALIGN_CENTER, CellStyle.VERTICAL_CENTER, "Abril", Font.BOLDWEIGHT_BOLD, formatoNumerico, sin_formato);
        hoja1.addMergedRegion(new CellRangeAddress(noRow,noRow,10,12));
        createCell(libro, fila4,(short) 13, CellStyle.ALIGN_CENTER, CellStyle.VERTICAL_CENTER, "Mayo", Font.BOLDWEIGHT_BOLD, formatoNumerico, sin_formato);
        hoja1.addMergedRegion(new CellRangeAddress(noRow,noRow,13,15));
        createCell(libro, fila4,(short) 16, CellStyle.ALIGN_CENTER, CellStyle.VERTICAL_CENTER, "Junio", Font.BOLDWEIGHT_BOLD, formatoNumerico, sin_formato);
        hoja1.addMergedRegion(new CellRangeAddress(noRow,noRow,16,18));
        createCell(libro, fila4,(short) 19, CellStyle.ALIGN_CENTER, CellStyle.VERTICAL_CENTER, "Julio", Font.BOLDWEIGHT_BOLD, formatoNumerico, sin_formato);
        hoja1.addMergedRegion(new CellRangeAddress(noRow,noRow,19,21));
        createCell(libro, fila4,(short) 22, CellStyle.ALIGN_CENTER, CellStyle.VERTICAL_CENTER, "Agosto", Font.BOLDWEIGHT_BOLD, formatoNumerico, sin_formato);
        hoja1.addMergedRegion(new CellRangeAddress(noRow,noRow,22,24));
        createCell(libro, fila4,(short) 25, CellStyle.ALIGN_CENTER, CellStyle.VERTICAL_CENTER, "Septiembre", Font.BOLDWEIGHT_BOLD, formatoNumerico, sin_formato);
        hoja1.addMergedRegion(new CellRangeAddress(noRow,noRow,25,27));
        createCell(libro, fila4,(short) 28, CellStyle.ALIGN_CENTER, CellStyle.VERTICAL_CENTER, "Octubre", Font.BOLDWEIGHT_BOLD, formatoNumerico, sin_formato);
        hoja1.addMergedRegion(new CellRangeAddress(noRow,noRow,28,30));
        createCell(libro, fila4,(short) 31, CellStyle.ALIGN_CENTER, CellStyle.VERTICAL_CENTER, "Noviembre", Font.BOLDWEIGHT_BOLD, formatoNumerico, sin_formato);
        hoja1.addMergedRegion(new CellRangeAddress(noRow,noRow,31,33));
        createCell(libro, fila4,(short) 34, CellStyle.ALIGN_CENTER, CellStyle.VERTICAL_CENTER, "Diciembre", Font.BOLDWEIGHT_BOLD, formatoNumerico, sin_formato);
        hoja1.addMergedRegion(new CellRangeAddress(noRow,noRow,34,36));
        createCell(libro, fila4,(short) 37, CellStyle.ALIGN_CENTER, CellStyle.VERTICAL_CENTER, "Total Anual", Font.BOLDWEIGHT_BOLD, formatoNumerico, sin_formato);
        hoja1.addMergedRegion(new CellRangeAddress(noRow,noRow,37,39));
        noRow=5;
        HSSFRow fila5 = hoja1.createRow(noRow);
        createCell(libro, fila5,(short) 0, CellStyle.ALIGN_LEFT, CellStyle.VERTICAL_CENTER, "", Font.BOLDWEIGHT_BOLD, formatoNumerico, sin_formato);
        createCell(libro, fila5,(short) 1, CellStyle.ALIGN_LEFT, CellStyle.VERTICAL_CENTER, "Pesos", Font.BOLDWEIGHT_BOLD, formatoNumerico, sin_formato);
        createCell(libro, fila5,(short) 2, CellStyle.ALIGN_LEFT, CellStyle.VERTICAL_CENTER, "Kgs/Lts", Font.BOLDWEIGHT_BOLD, formatoNumerico, sin_formato);
        createCell(libro, fila5,(short) 3, CellStyle.ALIGN_LEFT, CellStyle.VERTICAL_CENTER, "MOP", Font.BOLDWEIGHT_BOLD, formatoNumerico, sin_formato);
        createCell(libro, fila5,(short) 4, CellStyle.ALIGN_LEFT, CellStyle.VERTICAL_CENTER, "Pesos", Font.BOLDWEIGHT_BOLD, formatoNumerico, sin_formato);
        createCell(libro, fila5,(short) 5, CellStyle.ALIGN_LEFT, CellStyle.VERTICAL_CENTER, "Kgs/Lts", Font.BOLDWEIGHT_BOLD, formatoNumerico, sin_formato);
        createCell(libro, fila5,(short) 6, CellStyle.ALIGN_LEFT, CellStyle.VERTICAL_CENTER, "MOP", Font.BOLDWEIGHT_BOLD, formatoNumerico, sin_formato);
        createCell(libro, fila5,(short) 7, CellStyle.ALIGN_LEFT, CellStyle.VERTICAL_CENTER, "Pesos", Font.BOLDWEIGHT_BOLD, formatoNumerico, sin_formato);
        createCell(libro, fila5,(short) 8, CellStyle.ALIGN_LEFT, CellStyle.VERTICAL_CENTER, "Kgs/Lts", Font.BOLDWEIGHT_BOLD, formatoNumerico, sin_formato);
        createCell(libro, fila5,(short) 9, CellStyle.ALIGN_LEFT, CellStyle.VERTICAL_CENTER, "MOP", Font.BOLDWEIGHT_BOLD, formatoNumerico, sin_formato);
        createCell(libro, fila5,(short) 10, CellStyle.ALIGN_LEFT, CellStyle.VERTICAL_CENTER, "Pesos", Font.BOLDWEIGHT_BOLD, formatoNumerico, sin_formato);
        createCell(libro, fila5,(short) 11, CellStyle.ALIGN_LEFT, CellStyle.VERTICAL_CENTER, "Kgs/Lts", Font.BOLDWEIGHT_BOLD, formatoNumerico, sin_formato);
        createCell(libro, fila5,(short) 12, CellStyle.ALIGN_LEFT, CellStyle.VERTICAL_CENTER, "MOP", Font.BOLDWEIGHT_BOLD, formatoNumerico, sin_formato);
        createCell(libro, fila5,(short) 13, CellStyle.ALIGN_LEFT, CellStyle.VERTICAL_CENTER, "Pesos", Font.BOLDWEIGHT_BOLD, formatoNumerico, sin_formato);
        createCell(libro, fila5,(short) 14, CellStyle.ALIGN_LEFT, CellStyle.VERTICAL_CENTER, "Kgs/Lts", Font.BOLDWEIGHT_BOLD, formatoNumerico, sin_formato);
        createCell(libro, fila5,(short) 15, CellStyle.ALIGN_LEFT, CellStyle.VERTICAL_CENTER, "MOP", Font.BOLDWEIGHT_BOLD, formatoNumerico, sin_formato);
        createCell(libro, fila5,(short) 16, CellStyle.ALIGN_LEFT, CellStyle.VERTICAL_CENTER, "Pesos", Font.BOLDWEIGHT_BOLD, formatoNumerico, sin_formato);
        createCell(libro, fila5,(short) 17, CellStyle.ALIGN_LEFT, CellStyle.VERTICAL_CENTER, "Kgs/Lts", Font.BOLDWEIGHT_BOLD, formatoNumerico, sin_formato);
        createCell(libro, fila5,(short) 18, CellStyle.ALIGN_LEFT, CellStyle.VERTICAL_CENTER, "MOP", Font.BOLDWEIGHT_BOLD, formatoNumerico, sin_formato);
        createCell(libro, fila5,(short) 19, CellStyle.ALIGN_LEFT, CellStyle.VERTICAL_CENTER, "Pesos", Font.BOLDWEIGHT_BOLD, formatoNumerico, sin_formato);
        createCell(libro, fila5,(short) 20, CellStyle.ALIGN_LEFT, CellStyle.VERTICAL_CENTER, "Kgs/Lts", Font.BOLDWEIGHT_BOLD, formatoNumerico, sin_formato);
        createCell(libro, fila5,(short) 21, CellStyle.ALIGN_LEFT, CellStyle.VERTICAL_CENTER, "MOP", Font.BOLDWEIGHT_BOLD, formatoNumerico, sin_formato);
        createCell(libro, fila5,(short) 22, CellStyle.ALIGN_LEFT, CellStyle.VERTICAL_CENTER, "Pesos", Font.BOLDWEIGHT_BOLD, formatoNumerico, sin_formato);
        createCell(libro, fila5,(short) 23, CellStyle.ALIGN_LEFT, CellStyle.VERTICAL_CENTER, "Kgs/Lts", Font.BOLDWEIGHT_BOLD, formatoNumerico, sin_formato);
        createCell(libro, fila5,(short) 24, CellStyle.ALIGN_LEFT, CellStyle.VERTICAL_CENTER, "MOP", Font.BOLDWEIGHT_BOLD, formatoNumerico, sin_formato);
        createCell(libro, fila5,(short) 25, CellStyle.ALIGN_LEFT, CellStyle.VERTICAL_CENTER, "Pesos", Font.BOLDWEIGHT_BOLD, formatoNumerico, sin_formato);
        createCell(libro, fila5,(short) 26, CellStyle.ALIGN_LEFT, CellStyle.VERTICAL_CENTER, "Kgs/Lts", Font.BOLDWEIGHT_BOLD, formatoNumerico, sin_formato);
        createCell(libro, fila5,(short) 27, CellStyle.ALIGN_LEFT, CellStyle.VERTICAL_CENTER, "MOP", Font.BOLDWEIGHT_BOLD, formatoNumerico, sin_formato);
        createCell(libro, fila5,(short) 28, CellStyle.ALIGN_LEFT, CellStyle.VERTICAL_CENTER, "Pesos", Font.BOLDWEIGHT_BOLD, formatoNumerico, sin_formato);
        createCell(libro, fila5,(short) 29, CellStyle.ALIGN_LEFT, CellStyle.VERTICAL_CENTER, "Kgs/Lts", Font.BOLDWEIGHT_BOLD, formatoNumerico, sin_formato);
        createCell(libro, fila5,(short) 30, CellStyle.ALIGN_LEFT, CellStyle.VERTICAL_CENTER, "MOP", Font.BOLDWEIGHT_BOLD, formatoNumerico, sin_formato);
        createCell(libro, fila5,(short) 31, CellStyle.ALIGN_LEFT, CellStyle.VERTICAL_CENTER, "Pesos", Font.BOLDWEIGHT_BOLD, formatoNumerico, sin_formato);
        createCell(libro, fila5,(short) 32, CellStyle.ALIGN_LEFT, CellStyle.VERTICAL_CENTER, "Kgs/Lts", Font.BOLDWEIGHT_BOLD, formatoNumerico, sin_formato);
        createCell(libro, fila5,(short) 33, CellStyle.ALIGN_LEFT, CellStyle.VERTICAL_CENTER, "MOP", Font.BOLDWEIGHT_BOLD, formatoNumerico, sin_formato);
        createCell(libro, fila5,(short) 34, CellStyle.ALIGN_LEFT, CellStyle.VERTICAL_CENTER, "Pesos", Font.BOLDWEIGHT_BOLD, formatoNumerico, sin_formato);
        createCell(libro, fila5,(short) 35, CellStyle.ALIGN_LEFT, CellStyle.VERTICAL_CENTER, "Kgs/Lts", Font.BOLDWEIGHT_BOLD, formatoNumerico, sin_formato);
        createCell(libro, fila5,(short) 36, CellStyle.ALIGN_LEFT, CellStyle.VERTICAL_CENTER, "MOP", Font.BOLDWEIGHT_BOLD, formatoNumerico, sin_formato);
        createCell(libro, fila5,(short) 37, CellStyle.ALIGN_LEFT, CellStyle.VERTICAL_CENTER, "Pesos", Font.BOLDWEIGHT_BOLD, formatoNumerico, sin_formato);
        createCell(libro, fila5,(short) 38, CellStyle.ALIGN_LEFT, CellStyle.VERTICAL_CENTER, "Kgs/Lts", Font.BOLDWEIGHT_BOLD, formatoNumerico, sin_formato);
        createCell(libro, fila5,(short) 39, CellStyle.ALIGN_LEFT, CellStyle.VERTICAL_CENTER, "MOP", Font.BOLDWEIGHT_BOLD, formatoNumerico, sin_formato);      

        noRow=6;
        if(datos.size()>0){
            for (int x=0; x<=datos.size()-1;x++){
                HashMap<String,String> registro = datos.get(x);
                HSSFRow fila = hoja1.createRow(noRow);
                createCell(libro, fila,(short) 0, CellStyle.ALIGN_LEFT, CellStyle.VERTICAL_CENTER, registro.get("razon_social"), Font.BOLDWEIGHT_NORMAL, formatoNumerico, sin_formato);
                createCell(libro, fila,(short) 1, CellStyle.ALIGN_LEFT, CellStyle.VERTICAL_CENTER, registro.get("enero"), Font.BOLDWEIGHT_NORMAL, true, format.getFormat("###,###,###.00"));
                createCell(libro, fila,(short) 2, CellStyle.ALIGN_LEFT, CellStyle.VERTICAL_CENTER, registro.get("enero1"), Font.BOLDWEIGHT_NORMAL, true, format.getFormat("###,###,###.00"));
                createCell(libro, fila,(short) 3, CellStyle.ALIGN_LEFT, CellStyle.VERTICAL_CENTER, registro.get("enero11"), Font.BOLDWEIGHT_NORMAL, true, format.getFormat("###,###,###.00"));
                createCell(libro, fila,(short) 4, CellStyle.ALIGN_LEFT, CellStyle.VERTICAL_CENTER, registro.get("febrero"), Font.BOLDWEIGHT_NORMAL, true, format.getFormat("###,###,###.00"));
                createCell(libro, fila,(short) 5, CellStyle.ALIGN_LEFT, CellStyle.VERTICAL_CENTER, registro.get("febrero2"), Font.BOLDWEIGHT_NORMAL, true, format.getFormat("###,###,###.00"));
                createCell(libro, fila,(short) 6, CellStyle.ALIGN_LEFT, CellStyle.VERTICAL_CENTER, registro.get("febrero12"), Font.BOLDWEIGHT_NORMAL, true, format.getFormat("###,###,###.00"));
                createCell(libro, fila,(short) 7, CellStyle.ALIGN_LEFT, CellStyle.VERTICAL_CENTER, registro.get("marzo"), Font.BOLDWEIGHT_NORMAL, true, format.getFormat("###,###,###.00"));
                createCell(libro, fila,(short) 8, CellStyle.ALIGN_LEFT, CellStyle.VERTICAL_CENTER, registro.get("marzo3"), Font.BOLDWEIGHT_NORMAL, true, format.getFormat("###,###,###.00"));
                createCell(libro, fila,(short) 9, CellStyle.ALIGN_LEFT, CellStyle.VERTICAL_CENTER, registro.get("marzo13"), Font.BOLDWEIGHT_NORMAL, true, format.getFormat("###,###,###.00"));
                createCell(libro, fila,(short) 10, CellStyle.ALIGN_RIGHT, CellStyle.VERTICAL_CENTER, registro.get("abril"), Font.BOLDWEIGHT_NORMAL, true, format.getFormat("###,###,###.00"));
                createCell(libro, fila,(short) 11, CellStyle.ALIGN_LEFT, CellStyle.VERTICAL_CENTER, registro.get("abril4"), Font.BOLDWEIGHT_NORMAL, true, format.getFormat("###,###,###.00"));
                createCell(libro, fila,(short) 12, CellStyle.ALIGN_LEFT, CellStyle.VERTICAL_CENTER, registro.get("abril14"), Font.BOLDWEIGHT_NORMAL, true, format.getFormat("###,###,###.00"));
                createCell(libro, fila,(short) 13, CellStyle.ALIGN_RIGHT, CellStyle.VERTICAL_CENTER, registro.get("mayo"), Font.BOLDWEIGHT_NORMAL, true, format.getFormat("###,###,###.00"));
                createCell(libro, fila,(short) 14, CellStyle.ALIGN_RIGHT, CellStyle.VERTICAL_CENTER, registro.get("mayo5"), Font.BOLDWEIGHT_NORMAL, true, format.getFormat("###,###,###.00"));
                createCell(libro, fila,(short) 15, CellStyle.ALIGN_RIGHT, CellStyle.VERTICAL_CENTER, registro.get("mayo15"), Font.BOLDWEIGHT_NORMAL, true, format.getFormat("###,###,###.00"));
                createCell(libro, fila,(short) 16, CellStyle.ALIGN_LEFT, CellStyle.VERTICAL_CENTER, registro.get("junio"), Font.BOLDWEIGHT_NORMAL, true, format.getFormat("###,###,###.00"));
                createCell(libro, fila,(short) 17, CellStyle.ALIGN_LEFT, CellStyle.VERTICAL_CENTER, registro.get("junio6"), Font.BOLDWEIGHT_NORMAL, true, format.getFormat("###,###,###.00"));
                createCell(libro, fila,(short) 18, CellStyle.ALIGN_LEFT, CellStyle.VERTICAL_CENTER, registro.get("junio16"), Font.BOLDWEIGHT_NORMAL, true, format.getFormat("###,###,###.00"));
                createCell(libro, fila,(short) 19, CellStyle.ALIGN_LEFT, CellStyle.VERTICAL_CENTER, registro.get("julio"), Font.BOLDWEIGHT_NORMAL, true, format.getFormat("###,###,###.00"));
                createCell(libro, fila,(short) 20, CellStyle.ALIGN_LEFT, CellStyle.VERTICAL_CENTER, registro.get("julio7"), Font.BOLDWEIGHT_NORMAL, true, format.getFormat("###,###,###.00"));
                createCell(libro, fila,(short) 21, CellStyle.ALIGN_LEFT, CellStyle.VERTICAL_CENTER, registro.get("julio17"), Font.BOLDWEIGHT_NORMAL, true, format.getFormat("###,###,###.00"));
                createCell(libro, fila,(short) 22, CellStyle.ALIGN_LEFT, CellStyle.VERTICAL_CENTER, registro.get("agosto"), Font.BOLDWEIGHT_NORMAL, true, format.getFormat("###,###,###.00"));
                createCell(libro, fila,(short) 23, CellStyle.ALIGN_LEFT, CellStyle.VERTICAL_CENTER, registro.get("agosto8"), Font.BOLDWEIGHT_NORMAL, true, format.getFormat("###,###,###.00"));
                createCell(libro, fila,(short) 24, CellStyle.ALIGN_LEFT, CellStyle.VERTICAL_CENTER, registro.get("agosto18"), Font.BOLDWEIGHT_NORMAL, true, format.getFormat("###,###,###.00"));
                createCell(libro, fila,(short) 25, CellStyle.ALIGN_LEFT, CellStyle.VERTICAL_CENTER, registro.get("septiembre"), Font.BOLDWEIGHT_NORMAL, true, format.getFormat("###,###,###.00"));
                createCell(libro, fila,(short) 26, CellStyle.ALIGN_LEFT, CellStyle.VERTICAL_CENTER, registro.get("septiembre9"), Font.BOLDWEIGHT_NORMAL, true, format.getFormat("###,###,###.00"));
                createCell(libro, fila,(short) 27, CellStyle.ALIGN_LEFT, CellStyle.VERTICAL_CENTER, registro.get("septiembre19"), Font.BOLDWEIGHT_NORMAL, true, format.getFormat("###,###,###.00"));
                createCell(libro, fila,(short) 28, CellStyle.ALIGN_LEFT, CellStyle.VERTICAL_CENTER, registro.get("octubre"), Font.BOLDWEIGHT_NORMAL, true, format.getFormat("###,###,###.00"));
                createCell(libro, fila,(short) 29, CellStyle.ALIGN_LEFT, CellStyle.VERTICAL_CENTER, registro.get("octubre10"), Font.BOLDWEIGHT_NORMAL, true, format.getFormat("###,###,###.00"));
                createCell(libro, fila,(short) 30, CellStyle.ALIGN_LEFT, CellStyle.VERTICAL_CENTER, registro.get("octubre110"), Font.BOLDWEIGHT_NORMAL, true, format.getFormat("###,###,###.00"));
                createCell(libro, fila,(short) 31, CellStyle.ALIGN_LEFT, CellStyle.VERTICAL_CENTER, registro.get("noviembre"), Font.BOLDWEIGHT_NORMAL, true, format.getFormat("###,###,###.00"));
                createCell(libro, fila,(short) 32, CellStyle.ALIGN_LEFT, CellStyle.VERTICAL_CENTER, registro.get("noviembre11"), Font.BOLDWEIGHT_NORMAL, true, format.getFormat("###,###,###.00"));
                createCell(libro, fila,(short) 33, CellStyle.ALIGN_LEFT, CellStyle.VERTICAL_CENTER, registro.get("noviembre111"), Font.BOLDWEIGHT_NORMAL, true, format.getFormat("###,###,###.00"));
                createCell(libro, fila,(short) 34, CellStyle.ALIGN_LEFT, CellStyle.VERTICAL_CENTER, registro.get("diciembre"), Font.BOLDWEIGHT_NORMAL, true, format.getFormat("###,###,###.00"));
                createCell(libro, fila,(short) 35, CellStyle.ALIGN_LEFT, CellStyle.VERTICAL_CENTER, registro.get("diciembre12"), Font.BOLDWEIGHT_NORMAL, true, format.getFormat("###,###,###.00"));
                createCell(libro, fila,(short) 36, CellStyle.ALIGN_LEFT, CellStyle.VERTICAL_CENTER, registro.get("diciembre112"), Font.BOLDWEIGHT_NORMAL, true, format.getFormat("###,###,###.00"));
                createCell(libro, fila,(short) 37, CellStyle.ALIGN_LEFT, CellStyle.VERTICAL_CENTER, registro.get("suma_total"), Font.BOLDWEIGHT_NORMAL, true, format.getFormat("###,###,###.00"));
                createCell(libro, fila,(short) 38, CellStyle.ALIGN_LEFT, CellStyle.VERTICAL_CENTER, registro.get("suma_total2"), Font.BOLDWEIGHT_NORMAL, true, format.getFormat("###,###,###.00"));
                createCell(libro, fila,(short) 39, CellStyle.ALIGN_LEFT, CellStyle.VERTICAL_CENTER, registro.get("suma_total3"), Font.BOLDWEIGHT_NORMAL, true, format.getFormat("###,###,###.00"));
                
                noRow++;

                totalano = totalano + Double.parseDouble(registro.get("suma_total"));
                totalmesenero=totalmesenero + Double.parseDouble(registro.get("enero"));
                totalmesfebrero=totalmesfebrero + Double.parseDouble(registro.get("febrero"));
                totalmesmarzo=totalmesmarzo + Double.parseDouble(registro.get("marzo"));
                totalmesabril=totalmesabril + Double.parseDouble(registro.get("abril"));
                totalmesmayo=totalmesmayo + Double.parseDouble(registro.get("mayo"));
                totalmesjunio=totalmesjunio + Double.parseDouble(registro.get("junio"));
                totalmesjulio=totalmesjulio + Double.parseDouble(registro.get("julio"));
                totalmesagosto=totalmesagosto + Double.parseDouble(registro.get("agosto"));
                totalmesseptiembre=totalmesseptiembre + Double.parseDouble(registro.get("septiembre"));
                totalmesoctubre=totalmesoctubre + Double.parseDouble(registro.get("octubre"));
                totalmesnoviembre=totalmesnoviembre + Double.parseDouble(registro.get("noviembre"));
                totalmesdiciembre=totalmesdiciembre + Double.parseDouble(registro.get("diciembre"));
                
                totalano2 = totalano2 + Double.parseDouble(registro.get("suma_total2"));
                totalmesenero1=totalmesenero1 + Double.parseDouble(registro.get("enero1"));
                totalmesfebrero2=totalmesfebrero2 + Double.parseDouble(registro.get("febrero2"));
                totalmesmarzo3=totalmesmarzo3 + Double.parseDouble(registro.get("marzo3"));
                totalmesabril4=totalmesabril4 + Double.parseDouble(registro.get("abril4"));
                totalmesmayo5=totalmesmayo5 + Double.parseDouble(registro.get("mayo5"));
                totalmesjunio6=totalmesjunio6 + Double.parseDouble(registro.get("junio6"));
                totalmesjulio7=totalmesjulio7 + Double.parseDouble(registro.get("julio7"));
                totalmesagosto8=totalmesagosto8 + Double.parseDouble(registro.get("agosto8"));
                totalmesseptiembre9=totalmesseptiembre9 + Double.parseDouble(registro.get("septiembre9"));
                totalmesoctubre10=totalmesoctubre10 + Double.parseDouble(registro.get("octubre10"));
                totalmesnoviembre11=totalmesnoviembre11 + Double.parseDouble(registro.get("noviembre11"));
                totalmesdiciembre12=totalmesdiciembre12 + Double.parseDouble(registro.get("diciembre12"));
                
                totalano3 = totalano3 + Double.parseDouble(registro.get("suma_total3"));
                totalmesenero11=totalmesenero11 + Double.parseDouble(registro.get("enero11"));
                totalmesfebrero12=totalmesfebrero12 + Double.parseDouble(registro.get("febrero12"));
                totalmesmarzo13=totalmesmarzo13 + Double.parseDouble(registro.get("marzo13"));
                totalmesabril14=totalmesabril14 + Double.parseDouble(registro.get("abril14"));
                totalmesmayo15=totalmesmayo15 + Double.parseDouble(registro.get("mayo15"));
                totalmesjunio16=totalmesjunio16 + Double.parseDouble(registro.get("junio16"));
                totalmesjulio17=totalmesjulio17 + Double.parseDouble(registro.get("julio17"));
                totalmesagosto18=totalmesagosto18 + Double.parseDouble(registro.get("agosto18"));
                totalmesseptiembre19=totalmesseptiembre19 + Double.parseDouble(registro.get("septiembre19"));
                totalmesoctubre110=totalmesoctubre110 + Double.parseDouble(registro.get("octubre110"));
                totalmesnoviembre111=totalmesnoviembre111 + Double.parseDouble(registro.get("noviembre111"));
                totalmesdiciembre112=totalmesdiciembre112 + Double.parseDouble(registro.get("diciembre112"));
                
            }
        }

        HSSFRow fila7 = hoja1.createRow(noRow);
        createCell(libro, fila7,(short) 0, CellStyle.ALIGN_LEFT, CellStyle.VERTICAL_CENTER, "Total Mensual", Font.BOLDWEIGHT_BOLD, formatoNumerico, sin_formato);
        createCell(libro, fila7,(short) 1, CellStyle.ALIGN_LEFT, CellStyle.VERTICAL_CENTER, String.valueOf(totalmesenero), Font.BOLDWEIGHT_NORMAL, true, format.getFormat("###,###,###.00"));
        createCell(libro, fila7,(short) 2, CellStyle.ALIGN_LEFT, CellStyle.VERTICAL_CENTER, String.valueOf(totalmesenero1), Font.BOLDWEIGHT_NORMAL, true, format.getFormat("###,###,###.00"));
        createCell(libro, fila7,(short) 3, CellStyle.ALIGN_LEFT, CellStyle.VERTICAL_CENTER, String.valueOf(totalmesenero11), Font.BOLDWEIGHT_NORMAL, true, format.getFormat("###,###,###.00"));
        createCell(libro, fila7,(short) 4, CellStyle.ALIGN_LEFT, CellStyle.VERTICAL_CENTER, String.valueOf(totalmesfebrero), Font.BOLDWEIGHT_NORMAL, true, format.getFormat("###,###,###.00"));
        createCell(libro, fila7,(short) 5, CellStyle.ALIGN_LEFT, CellStyle.VERTICAL_CENTER, String.valueOf(totalmesfebrero2), Font.BOLDWEIGHT_NORMAL, true, format.getFormat("###,###,###.00"));
        createCell(libro, fila7,(short) 6, CellStyle.ALIGN_LEFT, CellStyle.VERTICAL_CENTER, String.valueOf(totalmesfebrero12), Font.BOLDWEIGHT_NORMAL, true, format.getFormat("###,###,###.00"));
        createCell(libro, fila7,(short) 7, CellStyle.ALIGN_LEFT, CellStyle.VERTICAL_CENTER, String.valueOf(totalmesmarzo), Font.BOLDWEIGHT_NORMAL, true, format.getFormat("###,###,###.00"));
        createCell(libro, fila7,(short) 8, CellStyle.ALIGN_LEFT, CellStyle.VERTICAL_CENTER, String.valueOf(totalmesmarzo3), Font.BOLDWEIGHT_NORMAL, true, format.getFormat("###,###,###.00"));
        createCell(libro, fila7,(short) 9, CellStyle.ALIGN_LEFT, CellStyle.VERTICAL_CENTER, String.valueOf(totalmesmarzo13), Font.BOLDWEIGHT_NORMAL, true, format.getFormat("###,###,###.00"));
        createCell(libro, fila7,(short) 10, CellStyle.ALIGN_RIGHT, CellStyle.VERTICAL_CENTER,String.valueOf(totalmesabril), Font.BOLDWEIGHT_NORMAL, true, format.getFormat("###,###,###.00"));
        createCell(libro, fila7,(short) 11, CellStyle.ALIGN_LEFT, CellStyle.VERTICAL_CENTER, String.valueOf(totalmesabril4), Font.BOLDWEIGHT_NORMAL, true, format.getFormat("###,###,###.00"));
        createCell(libro, fila7,(short) 12, CellStyle.ALIGN_LEFT, CellStyle.VERTICAL_CENTER, String.valueOf(totalmesabril14), Font.BOLDWEIGHT_NORMAL, true, format.getFormat("###,###,###.00"));
        createCell(libro, fila7,(short) 13, CellStyle.ALIGN_RIGHT, CellStyle.VERTICAL_CENTER, String.valueOf(totalmesmayo), Font.BOLDWEIGHT_NORMAL, true, format.getFormat("###,###,###.00"));
        createCell(libro, fila7,(short) 14, CellStyle.ALIGN_RIGHT, CellStyle.VERTICAL_CENTER, String.valueOf(totalmesmayo5), Font.BOLDWEIGHT_NORMAL, true, format.getFormat("###,###,###.00"));
        createCell(libro, fila7,(short) 15, CellStyle.ALIGN_RIGHT, CellStyle.VERTICAL_CENTER, String.valueOf(totalmesmayo15), Font.BOLDWEIGHT_NORMAL, true, format.getFormat("###,###,###.00"));
        createCell(libro, fila7,(short) 16, CellStyle.ALIGN_LEFT, CellStyle.VERTICAL_CENTER, String.valueOf(totalmesjunio), Font.BOLDWEIGHT_NORMAL, true, format.getFormat("###,###,###.00"));
        createCell(libro, fila7,(short) 17, CellStyle.ALIGN_LEFT, CellStyle.VERTICAL_CENTER, String.valueOf(totalmesjunio6), Font.BOLDWEIGHT_NORMAL, true, format.getFormat("###,###,###.00"));
        createCell(libro, fila7,(short) 18, CellStyle.ALIGN_LEFT, CellStyle.VERTICAL_CENTER, String.valueOf(totalmesjunio16), Font.BOLDWEIGHT_NORMAL, true, format.getFormat("###,###,###.00"));
        createCell(libro, fila7,(short) 19, CellStyle.ALIGN_LEFT, CellStyle.VERTICAL_CENTER, String.valueOf(totalmesjulio), Font.BOLDWEIGHT_NORMAL, true, format.getFormat("###,###,###.00"));
        createCell(libro, fila7,(short) 20, CellStyle.ALIGN_LEFT, CellStyle.VERTICAL_CENTER, String.valueOf(totalmesjulio7), Font.BOLDWEIGHT_NORMAL, true, format.getFormat("###,###,###.00"));
        createCell(libro, fila7,(short) 21, CellStyle.ALIGN_LEFT, CellStyle.VERTICAL_CENTER, String.valueOf(totalmesjulio17), Font.BOLDWEIGHT_NORMAL, true, format.getFormat("###,###,###.00"));
        createCell(libro, fila7,(short) 22, CellStyle.ALIGN_LEFT, CellStyle.VERTICAL_CENTER, String.valueOf(totalmesagosto), Font.BOLDWEIGHT_NORMAL, true, format.getFormat("###,###,###.00"));
        createCell(libro, fila7,(short) 23, CellStyle.ALIGN_LEFT, CellStyle.VERTICAL_CENTER, String.valueOf(totalmesagosto8), Font.BOLDWEIGHT_NORMAL, true, format.getFormat("###,###,###.00"));
        createCell(libro, fila7,(short) 24, CellStyle.ALIGN_LEFT, CellStyle.VERTICAL_CENTER, String.valueOf(totalmesagosto18), Font.BOLDWEIGHT_NORMAL, true, format.getFormat("###,###,###.00"));
        createCell(libro, fila7,(short) 25, CellStyle.ALIGN_LEFT, CellStyle.VERTICAL_CENTER, String.valueOf(totalmesseptiembre), Font.BOLDWEIGHT_NORMAL, true, format.getFormat("###,###,###.00"));
        createCell(libro, fila7,(short) 26, CellStyle.ALIGN_LEFT, CellStyle.VERTICAL_CENTER, String.valueOf(totalmesseptiembre9), Font.BOLDWEIGHT_NORMAL, true, format.getFormat("###,###,###.00"));
        createCell(libro, fila7,(short) 27, CellStyle.ALIGN_LEFT, CellStyle.VERTICAL_CENTER, String.valueOf(totalmesseptiembre19), Font.BOLDWEIGHT_NORMAL, true, format.getFormat("###,###,###.00"));
        createCell(libro, fila7,(short) 28, CellStyle.ALIGN_LEFT, CellStyle.VERTICAL_CENTER, String.valueOf(totalmesoctubre), Font.BOLDWEIGHT_NORMAL, true, format.getFormat("###,###,###.00"));
        createCell(libro, fila7,(short) 29, CellStyle.ALIGN_LEFT, CellStyle.VERTICAL_CENTER, String.valueOf(totalmesoctubre10), Font.BOLDWEIGHT_NORMAL, true, format.getFormat("###,###,###.00"));
        createCell(libro, fila7,(short) 30, CellStyle.ALIGN_LEFT, CellStyle.VERTICAL_CENTER, String.valueOf(totalmesoctubre110), Font.BOLDWEIGHT_NORMAL, true, format.getFormat("###,###,###.00"));
        createCell(libro, fila7,(short) 31, CellStyle.ALIGN_LEFT, CellStyle.VERTICAL_CENTER, String.valueOf(totalmesnoviembre), Font.BOLDWEIGHT_NORMAL, true, format.getFormat("###,###,###.00"));
        createCell(libro, fila7,(short) 32, CellStyle.ALIGN_LEFT, CellStyle.VERTICAL_CENTER, String.valueOf(totalmesnoviembre11), Font.BOLDWEIGHT_NORMAL, true, format.getFormat("###,###,###.00"));
        createCell(libro, fila7,(short) 33, CellStyle.ALIGN_LEFT, CellStyle.VERTICAL_CENTER, String.valueOf(totalmesnoviembre111), Font.BOLDWEIGHT_NORMAL, true, format.getFormat("###,###,###.00"));
        createCell(libro, fila7,(short) 34, CellStyle.ALIGN_LEFT, CellStyle.VERTICAL_CENTER, String.valueOf(totalmesdiciembre), Font.BOLDWEIGHT_NORMAL, true, format.getFormat("###,###,###.00"));
        createCell(libro, fila7,(short) 35, CellStyle.ALIGN_LEFT, CellStyle.VERTICAL_CENTER, String.valueOf(totalmesdiciembre12), Font.BOLDWEIGHT_NORMAL, true, format.getFormat("###,###,###.00"));
        createCell(libro, fila7,(short) 36, CellStyle.ALIGN_LEFT, CellStyle.VERTICAL_CENTER, String.valueOf(totalmesdiciembre112), Font.BOLDWEIGHT_NORMAL, true, format.getFormat("###,###,###.00"));
        createCell(libro, fila7,(short) 37, CellStyle.ALIGN_LEFT, CellStyle.VERTICAL_CENTER, String.valueOf(totalano), Font.BOLDWEIGHT_NORMAL, true, format.getFormat("###,###,###.00"));
        createCell(libro, fila7,(short) 38, CellStyle.ALIGN_LEFT, CellStyle.VERTICAL_CENTER, String.valueOf(totalano2), Font.BOLDWEIGHT_NORMAL, true, format.getFormat("###,###,###.00"));
        createCell(libro, fila7,(short) 39, CellStyle.ALIGN_LEFT, CellStyle.VERTICAL_CENTER, String.valueOf(totalano3), Font.BOLDWEIGHT_NORMAL, true, format.getFormat("###,###,###.00"));

                // Se salva el libro.
        try {
            FileOutputStream elFichero = new FileOutputStream(fileout);
            libro.write(elFichero);
            elFichero.close();
        } catch (Exception e) {
            e.printStackTrace();
        }   

        
    }
    
    /**
     * Creates a cell and aligns it a certain way.
     */
    private static void createCell(HSSFWorkbook wb, HSSFRow row, short column, short halign, short valign, String value, short stylefont, boolean numeric, short formato) {
        Cell cell = row.createCell(column);
        CellStyle cellStyle = wb.createCellStyle();
        cellStyle.setAlignment(halign);
        cellStyle.setVerticalAlignment(valign);
        
        Font font = wb.createFont();
        font.setBoldweight(stylefont);
        cellStyle.setFont(font);
        if(numeric){
            cell.setCellValue(Float.parseFloat(value));
            cell.setCellType(Cell.CELL_TYPE_NUMERIC);
            cellStyle.setDataFormat(formato);
        }else{
            HSSFRichTextString cellValue = new HSSFRichTextString(value);
            cell.setCellValue(cellValue);
        }

        cell.setCellStyle(cellStyle);
    }
    
}
