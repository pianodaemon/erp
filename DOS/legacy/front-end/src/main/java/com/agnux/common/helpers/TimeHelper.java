/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.agnux.common.helpers;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 *
 * @author pianodaemon
 */
public class TimeHelper {
    
    public static Long now(){
        String unix_epoch_time = String.valueOf( System.currentTimeMillis() );
	return Long.parseLong(unix_epoch_time); 
    }
    
	public static Date getFechaActual(){
		Date fechasalida = new Date();
		fechasalida = new Date(fechasalida.getTime());		
		return fechasalida;
	}
        
	public static Date getFechaDiaAnterior(){
		Date fecharetorno = new Date();
		fecharetorno = new Date(fecharetorno.getTime()-100000000);
		return fecharetorno;
	}
	
	public static String getFechaActualYMD(){
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		String fechasalida = sdf.format(new Date());				
		return fechasalida;
	}
	
	public static String getFechaActualYMD3(Date fecha){
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String fechasalida = sdf.format(fecha);				
		return fechasalida;
	}
	
	public static String getFechaActualYMDH(){
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String fechasalida = sdf.format(new Date());				
		return fechasalida;
	}
        
	public static String getFechaActualYMDHMS(){
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
		String fechasalida = sdf.format(new Date());				
		return fechasalida;
	}
        
	public static String convertirDateToString(Date fecha){
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		String fechasalida = sdf.format(fecha);				
		return fechasalida;		
	}
	public static String convertirDatesToString(Date fecha){
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
		String fechasalida = sdf.format(fecha);				
		return fechasalida;		
	}
	
	public static String getFechaActualMDY(){		
		SimpleDateFormat sdf = new SimpleDateFormat("MM-dd-yyyy");
		Date fechasalida = new Date();
		fechasalida = new Date(fechasalida.getTime());	
		String retorno = sdf.format(fechasalida);
		return retorno;
	}
	
	public static String getFechaActualY(){
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy");
		String fechasalida = sdf.format(new Date());			
		return fechasalida;
	}
        
        
	public static String getMesActual(){
		SimpleDateFormat sdf = new SimpleDateFormat("MM");
		String mesSalida = sdf.format(new Date());			
		return mesSalida;
	}
        
	public static int getNumDiasMes(int anio, int numMes){
            int numDias=0;
            switch(numMes-1){
                case 0: numDias=31; break;// Enero
                case 2: numDias=31; break;  // Marzo
                case 4: numDias=31; break;  // Mayo
                case 6: numDias=31; break;  // Julio
                case 7: numDias=31; break;  // Agosto
                case 9: numDias=31; break;  // Octubre
                case 11: numDias=31; break; // Diciembre
                case 3: numDias=30; break;  // Abril
                case 5: numDias=30; break;  // Junio
                case 8: numDias=30; break;  // Septiembre
                case 10: numDias=30; break; // Noviembre
                case 1:  // Febrero
                    if ( ((anio%100 == 0) && (anio%400 == 0)) ||((anio%100 != 0) && (anio%  4 == 0))   )
                         numDias=29;  // Año Bisiesto
                    else
                        numDias=28;
                    break;
                default:
                    throw new java.lang.IllegalArgumentException("El mes debe estar entre 0 y 11");
            }
            
            
            return numDias;
	}
        

        
        //metodo para convertir fechas de numero a nombre del mes 1 = enero.
        public static String ConvertNumToMonth(int mesEntrada){
            String mesSalida = "";
            switch (mesEntrada){
                case 1:
                    mesSalida = "Enero";
                break;
                    case 2:  mesSalida="Febrero";
                break;
                    case 3:  mesSalida="Marzo";
                break;
                    case 4:  mesSalida="Abril";
                break;
                    case 5:  mesSalida="Mayo";
                break;
                    case 6:  mesSalida="Junio";
                break;
                    case 7:  mesSalida="Julio";
                break;
                    case 8:  mesSalida="Agosto";
                break;
                    case 9:  mesSalida="Septiembre";
                break;
                    case 10:  mesSalida="Octubre";
                break;
                    case 11:  mesSalida="Noviembre";
                break;
                    case 12:  mesSalida="Diciembre";
                break;

                default:mesSalida="";
                    break;
            }
            return mesSalida;
        }

        
}
