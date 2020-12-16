/********************************************/
/**Written by Edwin Plauchu******************/
/*****************************Agnux Mexico***/
/********************************************/

package com.agnux.cfd.v2;


import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.regex.Pattern;

/**
 *
 * @author pianodaemon
 */
public final class ArchivoInformeMensual {
    
    final private String extensionArchivo = "txt";
    private String noEsquema;
    private String rfc;
    private String mm;
    private String yyyy;
    
    private String estadoDelComprobante;
    private String montoDelImpuesto;
    private String montoDelaOperacion;
    private String momentoExpedicion;
    private String folioDelComprobanteFiscal;
    private String numeroDeAprobacion;
    private String serie;
    private String rfcCliente;
    private String efectoDeComprobante;
    private String pedimento;
    private String fechaDePedimento;
    private String aduana;
    private String anoAprovacion;
    private Validacion valedor;

    public Validacion getValedor() {
        return valedor;
    }

    public void setValedor(Validacion valedor) {
        this.valedor = valedor;
    }
        
    public ArchivoInformeMensual(){
        this.setValedor(new Validacion());
    }

	private boolean checarDatosIngresadosParaAgregarNuevaLinea(){
		boolean valor_retorno = true;
		if (!this.getValedor().isRFCCorrecto( this.rfcCliente )){
			System.out.println("El RFC del cliente" + " " + this.rfcCliente + " " + ", no es un RFC valido");
			valor_retorno = false;
		}
		if (!this.getValedor().isSerieCorrecto( this.serie )){
			System.out.println("La Serie" + " " + this.serie + " " + ", no es una serie valida");
			valor_retorno = false;
		}
		if (!this.getValedor().isFolioDelComprobanteFiscalCorrecto( this.folioDelComprobanteFiscal )){
			System.out.println("El Folio del comprobante fiscal" + " " + this.folioDelComprobanteFiscal + " " + ", no es un folio valido");
			valor_retorno = false;
		}
		if (!this.getValedor().isNumeroDeAprobacionCorrecto( this.numeroDeAprobacion )){
			System.out.println("El Numero de Aprobacion" + " " + this.numeroDeAprobacion + " " + ", no es un numero valido");
			valor_retorno = false;
		}
		if (!this.getValedor().isMomentoExpedicionCorrecto( this.momentoExpedicion )){
			System.out.println("El Momento de la Operacion" + " " + this.momentoExpedicion + " " + ", no es un momento valido");
			valor_retorno = false;
		}
		if (!this.getValedor().isMontoDelaOperacionCorrecto( this.montoDelaOperacion )){
			System.out.println("El Momento de la Operacion" + " " + this.montoDelaOperacion + " " + ", no es un momento valido");
			valor_retorno = false;
		}
		if (!this.getValedor().isMontoDelImpuestoCorrecto( this.montoDelImpuesto )){
			System.out.println("El Momento de la Operacion" + " " + this.montoDelImpuesto + " " + ", no es un momento valido");
			valor_retorno = false;
		}
		if (!this.getValedor().isEstadoDelComprobanteCorrecto( this.estadoDelComprobante )){
			System.out.println("El Estado del comprobante" + " " + this.estadoDelComprobante + " " + ", no es un estado valido");
			valor_retorno = false;
		}
		return valor_retorno;
	}
        
	private boolean checarDatosIngresadosParaGenerarNombreDeArchivo(){
		boolean valor_retorno = true;
		if (!this.getValedor().isRFCCorrecto( this.rfc )){
			System.out.println("El Registro Federal de Contribuyentes" + " " + this.rfc + " " + ", no es un registro valido");
			valor_retorno = false;
		}
		if (!this.getValedor().isNumeroDelEsquemaCorrecto( this.noEsquema )){
			System.out.println("El Esquema" + " " + this.noEsquema + " " + ", no es un esquema valido");
			valor_retorno = false;
		}
		if (!this.getValedor().isYyyyCorrecto( this.yyyy )){
			System.out.println("El Año" + " " + this.yyyy + " " + ", no es un año valido");
			valor_retorno = false;
		}
		if (!this.getValedor().isMmCorrecto( this.mm )){
			System.out.println("El Mes" + " " + this.mm + " " + ", no es un mes valido");
			valor_retorno = false;
		}
		return valor_retorno;
	}
        
	// Genera el nombre que tendra el fichero de informe mensual
	public String generaNombreArchivoInformeMensual(String noEsquema, String rfc ,String mm ,String yyyy , String extensionArchivo){
		String cadena_retorno = null;
		this.noEsquema = noEsquema;
		this.rfc =  rfc;
		this.mm = mm;
		this.yyyy = yyyy;
		if ( this.checarDatosIngresadosParaGenerarNombreDeArchivo() ){
			cadena_retorno =  this.noEsquema +  this.rfc +  this.mm  + this.yyyy + "." + this.extensionArchivo;
		}
		return cadena_retorno;
	}
        
	private String maquilla_fecha(String fecha){
		String[] fechax = fecha.split("T");
		String[] fechay = fechax[0].split("-");
		fecha = fechay[2]+"/"+fechay[1]+"/"+fechay[0]+" "+fechax[1];
		return fecha;
	}
        
	// Genera un registro para el fichero de informe mensual
	public String generarRegistroPorRenglonParaArchivoInformeMensual( String rfcCliente, String serie, String folioDelComprobanteFiscal, 
					String numeroDeAprobacion, String momentoExpedicion  , String montoDelaOperacion , 
					String montoDelImpuesto, String estadoDelComprobante, String efectoDeComprobante, 
					String pedimento, String fechaDePedimento, String aduana, String anoAprovacion){
		String cadena_retorno = null;
		NumberFormat formatter = new DecimalFormat("#0.00");
		montoDelaOperacion = formatter.format(Double.parseDouble(montoDelaOperacion));
		montoDelImpuesto = formatter.format(Double.parseDouble(montoDelImpuesto));
		this.rfcCliente = rfcCliente;
		this.serie = serie;
		this.folioDelComprobanteFiscal = folioDelComprobanteFiscal;
		this.numeroDeAprobacion = numeroDeAprobacion;
		this.momentoExpedicion = momentoExpedicion;
		this.montoDelaOperacion = montoDelaOperacion;
		this.montoDelImpuesto = montoDelImpuesto;
		this.estadoDelComprobante =  estadoDelComprobante;
		this.efectoDeComprobante = esteAtributoSeDejoNulo(efectoDeComprobante);
		this.pedimento = esteAtributoSeDejoNulo(pedimento);
		this.fechaDePedimento= esteAtributoSeDejoNulo(fechaDePedimento);
		this.aduana = esteAtributoSeDejoNulo(aduana);
		this.anoAprovacion = anoAprovacion;
                
		if ( checarDatosIngresadosParaAgregarNuevaLinea() ){
			cadena_retorno = "|" + this.rfcCliente + "|" + this.serie + "|" +  this.folioDelComprobanteFiscal + "|" + this.anoAprovacion + this.numeroDeAprobacion + "|" + maquilla_fecha(this.momentoExpedicion)  + "|" + this.montoDelaOperacion + "|" + this.montoDelImpuesto + "|" + this.estadoDelComprobante + "|" + this.efectoDeComprobante + "|" + this.pedimento + "|" + this.fechaDePedimento + "|" + this.aduana + "|\n";
		}
		return cadena_retorno;
	}

	public boolean cancelarComprobanteFiscalDigitalDeArchivoInformeMensual(String Archivo,String serie,String folio){
		return false;
	}

	private String esteAtributoSeDejoNulo(String atributo){
		return (atributo == null ? "null" != null : !atributo.equals("null") && (atributo == null ? "" != null : !atributo.equals(""))) ? (atributo) : new String();
	}
        

    private class Validacion{
        
        /**Verifica el año de el esquema para el archivo de informe mensual
        * @param cadena_a_validar
        * @return true si el año es valido, false si el año es invalido.
        */
        public boolean isYyyyCorrecto(String cadena_a_validar){
            return Pattern.compile("^[0-9]{4}$").matcher(cadena_a_validar).find();
        }

        /**Verifica el mes de el esquema para el archivo de informe mensual
        * @param cadena_a_validar
        * @return true si el mes es valido, false si el mes es invalido.
        */
        public boolean isMmCorrecto(String cadena_a_validar){
            return Pattern.compile("^([1][0-2]|[1-9]|0[1-9])$").matcher(cadena_a_validar).find();
        }
        
        public boolean isValidFecha_Aduana(String valor) {
            return Pattern.compile("^([0-9]{2}/[0-9]{2}/[0-9]{4})$").matcher(valor).find();
        }
        
        public boolean isRFCCorrecto(String cadena_a_validar){
            return Pattern.compile("^[A-Za-z0-9]{3,4}[0-9]{6}[A-Za-z0-9]{3}$").matcher(cadena_a_validar).find();
        }
            
        public boolean isSerieCorrecto(String cadena_a_validar){
            return Pattern.compile("^([A-Za-z]|[0-9]){0,10}$").matcher(cadena_a_validar).find();
        }
        
        /**Verifica el estado del comprobante de el registro por renglon
        * @param cadena_a_validar
        * @return true si una serie es valido, false si la serie es invalido.
        */
        public boolean isEstadoDelComprobanteCorrecto(String cadena_a_validar){
            return Pattern.compile("^(0|1){1}$").matcher(cadena_a_validar).find();
        }
        
        /**Verifica el monto del impuesto de el registro por renglon
        * @param cadena_a_validar
        * @return true si es un monto valido, false si es un monto invalido.
        */
        public boolean isMontoDelImpuestoCorrecto(String cadena_a_validar){
            boolean valor_retorno = false;

            if (cadena_a_validar == null){ valor_retorno = true; }

            BigDecimal numero_introducido = new BigDecimal(cadena_a_validar);
            BigDecimal numero_maximo = new BigDecimal("9999999999.99");
            BigDecimal numero_minimo = new BigDecimal("0");

            //Si el numero introducido es igual al numero maximo
            if ( (numero_introducido.compareTo(numero_maximo) == 0) ){
                valor_retorno = true;
            }
            else{
                //Si el numero introducido es igual al numero minimo
                if( (numero_introducido.compareTo(numero_minimo) == 0) ){
                    valor_retorno = true;
                }else{
                    //Si el numero minimo es menor que el numero introducido
                    if (numero_minimo.compareTo(numero_introducido) == -1){
                        //Si el numero introducido es menor que el numero maximo
                        if (numero_introducido.compareTo(numero_maximo) == -1){
                            valor_retorno = true;
                        }
                    }
                }

            }

            return valor_retorno;
        }

        
        /**Verifica el momento de Expedicion del registro por renglon
        * @param cadena_a_validar
        * @return true si un momento valido, false si es un momento invalido.
        */
        public boolean isMomentoExpedicionCorrecto(String cadena_a_validar){
            return Pattern.compile("^[0-1][0-9]/[0-3][0-9]/[0-9]{4}\\ [0-9]{2}:[0-9]{2}:[0-9]{2}|[0-9]{4}-[0-9]{2}-[0-9]{2}T[0-9]{2}:[0-9]{2}:[0-9]{2}$").matcher(cadena_a_validar).find();
        }
        
        /**Verifica el monto de la operacion de el registro por renglon
        * @param cadena_a_validar
        * @return true si es un monto valido, false si es un monto invalido.
        */
        public boolean isMontoDelaOperacionCorrecto(String cadena_a_validar){
            boolean valor_retorno = false;
            BigDecimal numero_introducido = new BigDecimal(cadena_a_validar);
            BigDecimal numero_maximo = new BigDecimal("9999999999.99");
            BigDecimal numero_minimo = new BigDecimal("0");

            //Si el numero introducido es igual al numero maximo
            if ( (numero_introducido.compareTo(numero_maximo) == 0) ){
                valor_retorno = true;
            }
            else{
                //Si el numero introducido es igual al numero minimo
                if( (numero_introducido.compareTo(numero_minimo) == 0) ){
                    valor_retorno = true;
                }else{
                    //Si el numero minimo es menor que el numero introducido
                    if (numero_minimo.compareTo(numero_introducido) == -1){
                        //Si el numero introducido es menor que el numero maximo
                        if (numero_introducido.compareTo(numero_maximo) == -1){
                            valor_retorno = true;
                        }
                    }
                }

            }

            return valor_retorno;
        }
        
        /**Verifica el numero de serie del certificado de sello digital
        * que ampara al comprobante  fiscal
        * @param cadena_a_validar
        * @return true si es valido, false si no.
        */
        public boolean isNoCertificadoDelComprobanteFiscalCorrecto(String cadena_a_validar){
            return Pattern.compile("^[0-9]{20}$").matcher(cadena_a_validar).find();
        }
        
        /**Verifica la Fecha que se le pondra al comprobante fiscal
        * @param cadena_a_validar
        * @return true si es una fecha valida, false si la fecha es invalida.
        */
        public boolean isFechaDelComprobanteFiscalCorrecto(String cadena_a_validar){
            return Pattern.compile("^[0-9]{4}-[0-9]{2}-[0-9]{2}T[0-9]{2}:[0-9]{2}:[0-9]{2}$").matcher(cadena_a_validar).find();
        }
        
        public boolean isTipoDeComprobanteCorrecto(String cadena_a_validar){
            return Pattern.compile("^(ingreso|egreso|traslado)$").matcher(cadena_a_validar).find();
        }
        
        /**Verifica el folio de el registro por renglon
        * @param cadena_a_validar
        * @return true si una serie es valido, false si la serie es invalido.
        */
        public boolean isFolioDelComprobanteFiscalCorrecto(String cadena_a_validar){
            boolean valor_retorno = false;
            int numEntero = Integer.parseInt(cadena_a_validar);
            if ( (numEntero >= 1) && (numEntero <= 2147483647) ){
                valor_retorno = true;
            }
            return valor_retorno;
        }
        
        
         /**Verifica el número de el esquema para el archivo de informe mensual
        * @param cadena_a_validar
        * @return true si el esquema es valido, false si el esquema es invalido.
        */
        private boolean isNumeroDelEsquemaCorrecto(String noEsquema) {
            return Pattern.compile("^([123])$").matcher(noEsquema).find();
        }
        
        
        
        /**Verifica el numero de Aprobacion de el registro por renglon
        * @param cadena_a_validar
        * @return true si es un numero valido, false si es un numero invalido.
        */
        public boolean isNumeroDeAprobacionCorrecto(String cadena_a_validar){
            boolean valor_retorno = false;

            /* Para Comprobantes Fiscales
            impresos por establecimientos
            autorizados, número entre 1 y
            2147483647
            */
            if ( Pattern.compile("^[0-9]{1,10}$").matcher(cadena_a_validar).find() ){
                int numEntero = Integer.parseInt(cadena_a_validar);
                if ( (numEntero >= 1) && (numEntero <= 2147483647) ){
                    valor_retorno = true;
                }
            }

            /*
            Para Comprobantes Fiscales Digitales
            el formato es yyyy + número entre 1 y 2147483647
            */
            if ( Pattern.compile("^[0-9]{4}[0-9]{1,10}$").matcher(cadena_a_validar).find() ){
                int numEntero = Integer.parseInt(cadena_a_validar.substring(4));
                if ( (numEntero >= 1) && (numEntero <= 2147483647) ){
                    valor_retorno = true;
                }
            }

            /*
            Para Comprobantes Fiscales
            impresos por el propio
            contribuyente emisor en base
            a la regla 2.4.24, valor nulo.*/
            if (cadena_a_validar == null){ valor_retorno = true; }

            return valor_retorno;
        }
    

            
    }
}
