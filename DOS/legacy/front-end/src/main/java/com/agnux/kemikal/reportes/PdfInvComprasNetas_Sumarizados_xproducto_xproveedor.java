package com.agnux.kemikal.reportes;

import com.agnux.common.helpers.StringHelper;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.HashMap;

/**
 @author valentin.vale8490@gmail.com
 * fecha_elaboracion::  miercoles 10 de Octubre del 2012.
 */
public class PdfInvComprasNetas_Sumarizados_xproducto_xproveedor {
    public String  empresa_emisora;
    public static String fecha_reporte;
    
    public String  tipo_reporte;

    public String getTipo_reporte() {
        return tipo_reporte;
    }

    public void setTipo_reporte(String tipo_reporte) {
        this.tipo_reporte = tipo_reporte;
    }

    public static String getFecha_reporte() {
        return fecha_reporte;
    }

    public static void setFecha_reporte(String fecha) {
        fecha_reporte = fecha;
    }
    public java.util.List<HashMap<String, String>> rows;

    

    public java.util.List<HashMap<String, String>> getRows() {
        return rows;
    }

    public void setRows(java.util.List<HashMap<String, String>> rows) {
        this.rows = rows;
    }
    
    public String getEmpresa_emisora() {
        return empresa_emisora;
    }

    public void setEmpresa_emisora(String empresa_emisora) {
        this.empresa_emisora = empresa_emisora;
    }

    
    
    public PdfInvComprasNetas_Sumarizados_xproducto_xproveedor(ArrayList<HashMap<String, String>> lista_compras,  String producto, String fecha_inicial, String fecha_final, String razon_social_empresa, String fileout,Integer tipo_reporte) {
    
     
    this.setEmpresa_emisora(razon_social_empresa);
        //this.setEmpresa_emisora(razon_social_empresa);
        PdfInvComprasNetas_Sumarizados_xproducto_xproveedor.HeaderFooter event = new PdfInvComprasNetas_Sumarizados_xproducto_xproveedor.HeaderFooter();
        
        Font fontCols = new Font(Font.FontFamily.HELVETICA, 9,Font.NORMAL);
        Font smallFont = new Font(Font.FontFamily.HELVETICA,8,Font.NORMAL,BaseColor.BLACK);
        Font smallBoldFont = new Font(Font.getFamily("ARIAL"),6,Font.BOLD,BaseColor.WHITE);
        Font smallFontN = new Font(Font.FontFamily.HELVETICA,8,Font.BOLD,BaseColor.BLACK);
        PdfPTable table_titulos;
        PdfPTable tabla = null;
        
        PdfPCell cell;
        try{
            
            Document document = new Document(PageSize.LETTER.rotate(),-50,-50,60,30);
            PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(fileout));
            writer.setPageEvent(event);
            document.open();
            String T_R="";
            String PROVEEDOR_PRODUCTO="";
            
            if(tipo_reporte == 3){
                T_R="Reporte Compras Netas Sumarizado por Producto.";
                PROVEEDOR_PRODUCTO="PRODUCTO";
            }
            if(tipo_reporte == 4){
                T_R="Reporte Compras Netas Sumarizado por Proveedor.";
                PROVEEDOR_PRODUCTO="PROVEEDOR";
            }
            
            this.setTipo_reporte(T_R);
            //TABLA DE FECHAS 
            
            String[] fi = fecha_inicial.split("-");
            String[] ff = fecha_final.split("-");

            String fecha_reporte = "DEL:      "+fi[2]+"/"+fi[1]+"/"+fi[0] + "      AL:      " +ff[2]+"/"+ff[1]+"/"+ff[0];
            this.setFecha_reporte(fecha_reporte);
           
            float [] widths = {10f,2f,4f,1,4f,1f,4f};
            tabla = new PdfPTable(widths);
            tabla.setHeaderRows(1);
            tabla.setKeepTogether(false);
            tabla.setKeepTogether(true);
            
            String[] titulos = {PROVEEDOR_PRODUCTO,"UNIDAD","TOTAL","","PRECIO PROMEDIO","","VENTA TOTAL"};
            
                for (int i = 0; i<=titulos.length -1; i++){	
                                    cell = new PdfPCell(new Paragraph(titulos[i],smallBoldFont));
                                    
                                    if(titulos[0] == PROVEEDOR_PRODUCTO){
                                    cell.setHorizontalAlignment(Element.ALIGN_LEFT);
                                    }else{
                                        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                                    }
                                    
                                    
                                    cell.setUseAscender(true);
                                    cell.setUseDescender(true);
                                    cell.setBackgroundColor(BaseColor.BLACK);
                                    cell.setBorder(0);
                                    cell.setBorderWidthLeft(0);
                                    
                                    tabla.addCell(cell);   

                } //fin de for de insertar encabezado de la tabla
                
                
                 
                String producto_proveedor = "";
                //String producto_prov = "";
                String unidad = "";
                String indice_extraido = "";
                double cantidad=0.0;
                double costo_unitario=0.0;
                double costo_promedio=0.0;
                double suma_general =0.0;
                int contador_costo_unitario=0;
                double compra_neta=0.0;
                double sumatoriacompra_neta=0.0;
                
                
                //int nombre_producto_proveedor=1;
                        if(lista_compras.size() > 0){
                                if(tipo_reporte == 3){

                                    producto_proveedor = lista_compras.get(0).get("producto").toString();
                                    unidad = lista_compras.get(0).get("unidad").toString();
                                    indice_extraido="producto";
                                }
                                if(tipo_reporte == 4){
                                    producto_proveedor = lista_compras.get(0).get("proveedor").toString();
                                    unidad = lista_compras.get(0).get("unidad").toString();
                                    indice_extraido="proveedor";
                                }
                        }
                
                        for (int j=0; j<=lista_compras.size()-1; j++){     //inicia for para recorrer las filas de la lista    
                                HashMap<String, String> registro= lista_compras.get(j);//para conocer el contenido de los hashmap en cada fila de la lista por si indice
                                 
                                
                                
                                if(producto_proveedor.equals(registro.get(indice_extraido) ) ){
                                    if( unidad.equals(registro.get("unidad")) ){
                                         cantidad=cantidad + Double.parseDouble(registro.get("cantidad".intern()));
                                        costo_unitario=costo_unitario + Double.parseDouble(registro.get("costo_unitario".intern()));
                                        compra_neta=compra_neta + Double.parseDouble(registro.get("compra_neta_mn".intern()));
                                        sumatoriacompra_neta=sumatoriacompra_neta + Double.parseDouble(registro.get("compra_neta_mn".intern()));
                                        contador_costo_unitario= contador_costo_unitario+1;
                                    }else{
                                        cell = new PdfPCell(new Paragraph(producto_proveedor.toString(),smallFont));	
                                        cell.setHorizontalAlignment(Element.ALIGN_LEFT);
                                        cell.setBorder(0);
                                        tabla.addCell(cell);  

                                        cell = new PdfPCell(new Paragraph(unidad.toString(),smallFont));	
                                        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                                        cell.setBorder(0);
                                        tabla.addCell(cell);  

                                        //String cant=StringHelper.roundDouble(registro.get("cantidad").toString(), 2);	            		
                                        cell = new PdfPCell(new Paragraph(StringHelper.AgregaComas(StringHelper.roundDouble(cantidad,2)),smallFont));
                                        cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                                        cell.setBorder(0);
                                        tabla.addCell(cell);

                                        cell = new PdfPCell(new Paragraph("$",smallFont));	
                                        cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                                        cell.setBorder(0);
                                        tabla.addCell(cell);

                                        //String precioUni=StringHelper.roundDouble(registro.get("precio_unitario").toString(), 2);	            		
                                        cell = new PdfPCell(new Paragraph(StringHelper.AgregaComas(StringHelper.roundDouble(costo_promedio,2)),smallFont));
                                        cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                                        cell.setBorder(0);
                                        tabla.addCell(cell);

                                        cell = new PdfPCell(new Paragraph("$",smallFont));	
                                        cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                                        cell.setBorder(0);
                                        tabla.addCell(cell);  

                                        //String venNeta=StringHelper.roundDouble(registro.get("total_pesos").toString(), 2);
                                        cell = new PdfPCell(new Paragraph(StringHelper.AgregaComas(StringHelper.roundDouble(compra_neta,2)),smallFont));
                                        cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                                        cell.setBorder(0);
                                        tabla.addCell(cell);
                                        //reinicio varibles
                                        cantidad                =0.0;
                                        costo_unitario          =0.0;
                                        compra_neta             = 0.0;
                                        contador_costo_unitario =0;
                                        costo_promedio          =0.0;
                                        
                                        
                                         cantidad=cantidad + Double.parseDouble(registro.get("cantidad".intern()));
                                        costo_unitario=costo_unitario + Double.parseDouble(registro.get("costo_unitario".intern()));
                                        compra_neta=compra_neta + Double.parseDouble(registro.get("compra_neta_mn".intern()));
                                        sumatoriacompra_neta=sumatoriacompra_neta + Double.parseDouble(registro.get("compra_neta_mn".intern()));
                                        contador_costo_unitario= contador_costo_unitario+1;
                                        costo_promedio=costo_unitario/contador_costo_unitario;
                                        suma_general =suma_general +Double.parseDouble(registro.get("compra_neta_mn".intern()));
                                        
                                    
                                    }
                                    producto_proveedor=registro.get(indice_extraido);
                                    unidad=registro.get("unidad");
                                }else{
                                  cell = new PdfPCell(new Paragraph(producto_proveedor.toString(),smallFont));	
                                        cell.setHorizontalAlignment(Element.ALIGN_LEFT);
                                        cell.setBorder(0);
                                        tabla.addCell(cell);  

                                        cell = new PdfPCell(new Paragraph(unidad.toString(),smallFont));	
                                        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                                        cell.setBorder(0);
                                        tabla.addCell(cell);  

                                        //String cant=StringHelper.roundDouble(registro.get("cantidad").toString(), 2);	            		
                                        cell = new PdfPCell(new Paragraph(StringHelper.AgregaComas(StringHelper.roundDouble(cantidad,2)),smallFont));
                                        cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                                        cell.setBorder(0);
                                        tabla.addCell(cell);

                                        cell = new PdfPCell(new Paragraph("$",smallFont));	
                                        cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                                        cell.setBorder(0);
                                        tabla.addCell(cell);

                                        //String precioUni=StringHelper.roundDouble(registro.get("precio_unitario").toString(), 2);	            		
                                        cell = new PdfPCell(new Paragraph(StringHelper.AgregaComas(StringHelper.roundDouble(costo_promedio,2)),smallFont));
                                        cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                                        cell.setBorder(0);
                                        tabla.addCell(cell);

                                        cell = new PdfPCell(new Paragraph("$",smallFont));	
                                        cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                                        cell.setBorder(0);
                                        tabla.addCell(cell);  

                                        //String venNeta=StringHelper.roundDouble(registro.get("total_pesos").toString(), 2);
                                        cell = new PdfPCell(new Paragraph(StringHelper.AgregaComas(StringHelper.roundDouble(compra_neta,2)),smallFont));
                                        cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                                        cell.setBorder(0);
                                        tabla.addCell(cell);
                                        //reinicio varibles
                                        cantidad                =0.0;
                                        costo_unitario          =0.0;
                                        compra_neta             = 0.0;
                                        contador_costo_unitario =0;
                                        costo_promedio          =0.0;
                                        
                                        
                                         cantidad=cantidad + Double.parseDouble(registro.get("cantidad".intern()));
                                        costo_unitario=costo_unitario + Double.parseDouble(registro.get("costo_unitario".intern()));
                                        compra_neta=compra_neta + Double.parseDouble(registro.get("compra_neta_mn".intern()));
                                        sumatoriacompra_neta=sumatoriacompra_neta + Double.parseDouble(registro.get("compra_neta_mn".intern()));
                                        contador_costo_unitario= contador_costo_unitario+1;
                                        costo_promedio=costo_unitario/contador_costo_unitario;
                                        suma_general =suma_general +Double.parseDouble(registro.get("compra_neta_mn".intern()));
                                
                                }
                                
                                 producto_proveedor=registro.get(indice_extraido);
                                 unidad=registro.get("unidad");
                                
                        }//fin for recorrido de filas
                        
                                    cell = new PdfPCell(new Paragraph(producto_proveedor.toString(),smallFont));	
                                    cell.setHorizontalAlignment(Element.ALIGN_LEFT);
                                    cell.setBorder(0);
                                    tabla.addCell(cell);  

                                    cell = new PdfPCell(new Paragraph(unidad.toString(),smallFont));	
                                    cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                                    cell.setBorder(0);
                                    tabla.addCell(cell);  

                                    //String cant=StringHelper.roundDouble(registro.get("cantidad").toString(), 2);	            		
                                    cell = new PdfPCell(new Paragraph(StringHelper.AgregaComas(StringHelper.roundDouble(cantidad,2)),smallFont));
                                    cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                                    cell.setBorder(0);
                                    tabla.addCell(cell);

                                    cell = new PdfPCell(new Paragraph("$",smallFont));	
                                    cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                                    cell.setBorder(0);
                                    tabla.addCell(cell);

                                    //String precioUni=StringHelper.roundDouble(registro.get("precio_unitario").toString(), 2);	            		
                                    cell = new PdfPCell(new Paragraph(StringHelper.AgregaComas(StringHelper.roundDouble(costo_promedio,2)),smallFont));
                                    cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                                    cell.setBorder(0);
                                    tabla.addCell(cell);

                                    cell = new PdfPCell(new Paragraph("$",smallFont));	
                                    cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                                    cell.setBorder(0);
                                    tabla.addCell(cell);  

                                    //String venNeta=StringHelper.roundDouble(registro.get("total_pesos").toString(), 2);
                                    cell = new PdfPCell(new Paragraph(StringHelper.AgregaComas(StringHelper.roundDouble(compra_neta,2)),smallFont));
                                    cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                                    cell.setBorder(0);
                                    tabla.addCell(cell);
                                    
                                    
                                    
                                    
                                    ///////////////////////////  TOTAL GENERAL //////////
                                    /////////////////////////                //////////
                                    
                                    //String precioUni=StringHelper.roundDouble(registro.get("precio_unitario").toString(), 2);	            		
                                    cell = new PdfPCell(new Paragraph("",smallFont));
                                    cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                                    cell.setBorder(0);
                                    cell.setColspan(7);
                                    cell.setFixedHeight(35);
                                    tabla.addCell(cell);
                                    
                                    //String precioUni=StringHelper.roundDouble(registro.get("precio_unitario").toString(), 2);	            		
                                    cell = new PdfPCell(new Paragraph("Total:",smallBoldFont));
                                    cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                                    cell.setBorder(0);
                                    cell.setColspan(5);
                                    tabla.addCell(cell);

                                    cell = new PdfPCell(new Paragraph("$",smallFont));	
                                    cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                                    cell.setBorder(0);
                                    tabla.addCell(cell);  
                                    //sumatoriacompra_neta=sumatoriacompra_neta + Double.parseDouble(registro.get("compra_neta_mn".intern()));
                                    //String venNeta=StringHelper.roundDouble(registro.get("total_pesos").toString(), 2);
                                    cell = new PdfPCell(new Paragraph(StringHelper.AgregaComas(StringHelper.roundDouble(sumatoriacompra_neta,2)),smallFont));
                                    cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                                    cell.setBorder(0);
                                    tabla.addCell(cell);
                                    
                                    
			
                        
            
            document.add(tabla); //añadiendo la tabla 
            document.close();
        }catch (Exception e) {
                e.printStackTrace();
        }
    }

    

    
    
    class HeaderFooter extends PdfPageEventHelper  {
        
        //public Image headerImage;
        protected PdfTemplate total;       
        protected BaseFont helv;  
        protected PdfContentByte cb;  
        Font largeBoldFont = new Font(Font.FontFamily.HELVETICA,10,Font.BOLD,BaseColor.BLACK);
        Font largeFont = new Font(Font.FontFamily.HELVETICA,10,Font.NORMAL,BaseColor.BLACK);
        Font smallFont = new Font(Font.FontFamily.HELVETICA,7,Font.NORMAL,BaseColor.BLACK);
        
        /*Añadimos una tabla con  una imagen del logo de megestiono y creamos la fuente para el documento, la imagen esta escalada para que no se muestre pixelada*/   
        @Override
        public void onOpenDocument(PdfWriter writer, Document document) {
            try {
                
                //headerImage = Image.getInstance(PdfDepositos.ruta_imagen);
                //headerImage.scalePercent(50);
                
                total = writer.getDirectContent().createTemplate(100, 100);  
                //public Rectangle(int x, int y, int width, int height)
                total.setBoundingBox(new Rectangle(-20, -20, 100, 100));
                total.fill();
                helv = BaseFont.createFont("Helvetica", BaseFont.WINANSI, false); 
            }
            catch(Exception e) {
                throw new ExceptionConverter(e);
            }
        }
        
        /*añadimos pie de página, borde y más propiedades*/
        @Override
        public void onEndPage(PdfWriter writer, Document document) {
            //PdfContentByte cb = writer.getDirectContent();
            ColumnText.showTextAligned(writer.getDirectContent(),Element.ALIGN_CENTER, new Phrase(PdfInvComprasNetas_Sumarizados_xproducto_xproveedor.this.getEmpresa_emisora(),largeBoldFont),document.getPageSize().getWidth()/2, document.getPageSize().getTop() -25, 0);
            ColumnText.showTextAligned(writer.getDirectContent(),Element.ALIGN_CENTER, new Phrase(PdfInvComprasNetas_Sumarizados_xproducto_xproveedor.this.getTipo_reporte(),largeBoldFont),document.getPageSize().getWidth()/2, document.getPageSize().getTop()-38, 0);
            ColumnText.showTextAligned(writer.getDirectContent(),Element.ALIGN_CENTER, new Phrase(PdfReporteVentasNetasSumatoriaxClientes.getFecha_reporte(),largeFont),document.getPageSize().getWidth()/2, document.getPageSize().getTop()-50, 0);
            
            cb = writer.getDirectContent();  
            //cb.saveState();  
            String text = "Página " + writer.getPageNumber() + " de ";  
            float textBase = document.bottom() - 20;
            float textSize = helv.getWidthPoint(text, 7);  
            float adjust = helv.getWidthPoint("0", 150);  
            cb.beginText();  
            cb.setFontAndSize(helv, 7);  

            //cb.setTextMatrix(document.right() - 120 - adjust, textBase);
            cb.setTextMatrix(document.right() - 128, textBase);  //definir la posicion de text
            cb.showText(text);  

            cb.endText();  
            cb.addTemplate(total, document.right() - adjust , textBase);  //definir la posicion del total de paginas
            //cb.restoreState();
        }
        
        
        /*aqui escrimos ls paginas totales, para que nos salga de pie de pagina Pagina x de y*/
        @Override
        public void onCloseDocument(PdfWriter writer, Document document) {
          total.beginText();  
          total.setFontAndSize(helv, 7);  
          total.setTextMatrix(0,0);                                           
          total.showText(String.valueOf(writer.getPageNumber() -1));  
          total.endText();  
        }    
    }
}
