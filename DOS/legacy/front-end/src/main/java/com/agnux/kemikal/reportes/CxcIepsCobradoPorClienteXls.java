package com.agnux.kemikal.reportes;

import com.itextpdf.text.Font;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.hssf.util.CellRangeAddress;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.DataFormat;


public class CxcIepsCobradoPorClienteXls {

    public CxcIepsCobradoPorClienteXls(HashMap<String,Object> data) {
        ArrayList<HashMap<String, String>> datos = new ArrayList<HashMap<String, String>>();
        LinkedHashMap<String, String> columns = new LinkedHashMap<String, String>();
        LinkedHashMap<String, String> totales = new LinkedHashMap<String, String>();
        HashMap<String, Object> conf = new HashMap<String, Object>();
        HashMap<String, String> dataHeaderFooter = new HashMap<String, String>();
        
        datos = (ArrayList<HashMap<String, String>>)data.get("datos");
        conf = (HashMap<String, Object>) data.get("conf");
        columns = (LinkedHashMap<String, String>) conf.get("Columns");
        totales = (LinkedHashMap<String, String>) data.get("totales");
        
        
        String fileout = String.valueOf(conf.get("fileout"));
        
        dataHeaderFooter.put("empresa", String.valueOf(conf.get("nombreEmpresa")));
        dataHeaderFooter.put("titulo_reporte", String.valueOf(conf.get("tituloReporte")));
        dataHeaderFooter.put("periodo", String.valueOf(conf.get("periodo")));
        dataHeaderFooter.put("codigo1", "");
        dataHeaderFooter.put("codigo2", "");
        
        Integer noCols = Integer.valueOf(String.valueOf(conf.get("noCols")));
        
        // Se crea el libro
        HSSFWorkbook libro = new HSSFWorkbook();
        // Se crea una hoja dentro del libro
        HSSFSheet hoja1 = null;
        DataFormat format=null;
        boolean formatoNumerico=false;
        short sin_formato = 0;
        int noRow=0;
        int noCell=0;

        hoja1 = libro.createSheet("IEPS_X_CLIENTE");
        hoja1.setDefaultColumnWidth(noCols);

        format = libro.createDataFormat();
        formatoNumerico=false;
        sin_formato = format.getFormat("");
        
        
        HSSFRow fila1 = hoja1.createRow(noRow);
        noCell=0;
        createCell(libro, fila1,(short) noCell, CellStyle.ALIGN_CENTER, CellStyle.VERTICAL_CENTER, String.valueOf(conf.get("nombreEmpresa")), org.apache.poi.ss.usermodel.Font.BOLDWEIGHT_BOLD, formatoNumerico, sin_formato);
        //Combinar celdas
        hoja1.addMergedRegion(new CellRangeAddress(noRow, noRow, 0, noCols-1));
        noRow++;
        
        HSSFRow fila2 = hoja1.createRow(noRow);
        noCell=0;
        createCell(libro, fila2,(short) noCell, CellStyle.ALIGN_CENTER, CellStyle.VERTICAL_CENTER, String.valueOf(conf.get("tituloReporte")), org.apache.poi.ss.usermodel.Font.BOLDWEIGHT_BOLD, formatoNumerico, sin_formato);
        //Combinar celdas
        hoja1.addMergedRegion(new CellRangeAddress(noRow, noRow, 0, noCols-1));
        noRow++;
        
        HSSFRow fila3 = hoja1.createRow(noRow);
        noCell=0;
        createCell(libro, fila3,(short) noCell, CellStyle.ALIGN_CENTER, CellStyle.VERTICAL_CENTER, String.valueOf(conf.get("periodo")), org.apache.poi.ss.usermodel.Font.BOLDWEIGHT_BOLD, formatoNumerico, sin_formato);
        //Combinar celdas
        hoja1.addMergedRegion(new CellRangeAddress(noRow, noRow, 0, noCols-1));
        noRow++;
        
        
        HSSFRow fila4 = hoja1.createRow(noRow);
        noCell=0;
        createCell(libro, fila4,(short) noCell, CellStyle.ALIGN_CENTER, CellStyle.VERTICAL_CENTER, "", org.apache.poi.ss.usermodel.Font.BOLDWEIGHT_BOLD, formatoNumerico, sin_formato);
        //Combinar celdas
        hoja1.addMergedRegion(new CellRangeAddress(noRow, noRow, 0, noCols-1));
        noRow++;
        
        
        
        //Crear encabezados de columnas
        HSSFRow fila5 = hoja1.createRow(noRow);
        Iterator it = columns.keySet().iterator();
        while(it.hasNext()){
            String key = (String)it.next();
            //Encabezado de la columna
            createCell(libro, fila5,(short) noCell, CellStyle.ALIGN_LEFT, CellStyle.VERTICAL_CENTER, columns.get(key).split(":")[0], org.apache.poi.ss.usermodel.Font.BOLDWEIGHT_BOLD, formatoNumerico, sin_formato);
            noCell++;
        }
        noRow++;
        
        
        if(datos.size()>0){
            Iterator it2;
            
            for( HashMap<String,String> i : datos ){
                HSSFRow fila = hoja1.createRow(noRow);
                noCell=0;
                
                it2 = columns.keySet().iterator();
                
                while(it2.hasNext()){
                    String key = (String)it2.next();
                    
                    if(columns.get(key).split(":")[1].equals("left")){
                        createCell(libro, fila,(short) noCell, CellStyle.ALIGN_LEFT, CellStyle.VERTICAL_CENTER, i.get(key), org.apache.poi.ss.usermodel.Font.BOLDWEIGHT_NORMAL, formatoNumerico, sin_formato);
                    }else{
                        if(columns.get(key).split(":")[1].equals("right")){
                            createCell(libro, fila,(short) noCell, CellStyle.ALIGN_RIGHT, CellStyle.VERTICAL_CENTER, i.get(key).replace(",", ""), org.apache.poi.ss.usermodel.Font.BOLDWEIGHT_NORMAL, true, format.getFormat("###,###,###.00"));
                        }
                    }
                    
                    noCell++;
                }
                
                noRow++;
            }
            
            
            //Imprimir totales
            HSSFRow fila_totales = hoja1.createRow(noRow);
            int etiqueta=0;
            noCell=0;
            
            Iterator it3 = columns.keySet().iterator();
            while(it3.hasNext()){
                String key = (String)it3.next();
                formatoNumerico=false;
                
                if(totales.containsKey(key)){
                    if(columns.get(key).split(":")[1].equals("left")){
                        createCell(libro, fila_totales,(short) noCell, CellStyle.ALIGN_LEFT, CellStyle.VERTICAL_CENTER, totales.get(key), org.apache.poi.ss.usermodel.Font.BOLDWEIGHT_BOLD, formatoNumerico, sin_formato);
                    }else{
                        if(columns.get(key).split(":")[1].equals("right")){
                            createCell(libro, fila_totales,(short) noCell, CellStyle.ALIGN_RIGHT, CellStyle.VERTICAL_CENTER, totales.get(key).replace(",", ""), org.apache.poi.ss.usermodel.Font.BOLDWEIGHT_BOLD, true, format.getFormat("###,###,###.00"));
                        }
                    }
                }else{
                    if(etiqueta==0){
                        createCell(libro, fila_totales,(short) noCell, CellStyle.ALIGN_LEFT, CellStyle.VERTICAL_CENTER, "TOTAL", org.apache.poi.ss.usermodel.Font.BOLDWEIGHT_BOLD, formatoNumerico, sin_formato);
                        etiqueta++;
                    }else{
                        createCell(libro, fila_totales,(short) noCell, CellStyle.ALIGN_RIGHT, CellStyle.VERTICAL_CENTER, "", org.apache.poi.ss.usermodel.Font.BOLDWEIGHT_BOLD, formatoNumerico, sin_formato);
                    }
                }
                noCell++;
            }
            noRow++;


            HSSFRow fila_total_ieps = hoja1.createRow(noRow);
            noCell=0;
            createCell(libro, fila_total_ieps,(short) noCell, CellStyle.ALIGN_LEFT, CellStyle.VERTICAL_CENTER, "TOTAL IEPS", org.apache.poi.ss.usermodel.Font.BOLDWEIGHT_BOLD, formatoNumerico, sin_formato);
            //Combinar celdas
            hoja1.addMergedRegion(new CellRangeAddress(
                    noRow, //first row (0-based)
                    noRow, //last row  (0-based)
                    0, //first column (0-based)
                    noCols-2  //last column  (0-based)
            ));
            
            noCell = noCols-1;
            createCell(libro, fila_total_ieps,(short) noCell, CellStyle.ALIGN_RIGHT, CellStyle.VERTICAL_CENTER, totales.get("sumaIeps").replace(",", ""), org.apache.poi.ss.usermodel.Font.BOLDWEIGHT_BOLD, true, format.getFormat("###,###,###.00"));
            
            //FILAS DE NOTAS
            //cell= new PdfPCell(new Paragraph("* Todas las cantidades que se muestran son en M.N.",smallFont));
            //cell= new PdfPCell(new Paragraph("* Solo se consideran facturas pagas totalmente.",smallFont));
        }else{
            //Agregar fila cuando no hay resultados en el reporte
            //cell= new PdfPCell(new Paragraph("No hay datos para mostrar",smallFont));
        }


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
        
        HSSFFont font = wb.createFont();
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
