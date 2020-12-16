$(function() {
        
        
	String.prototype.toCharCode = function(){
	    var str = this.split(''), len = str.length, work = new Array(len);
	    for (var i = 0; i < len; ++i){
			work[i] = this.charCodeAt(i);
	    }
	    return work.join(',');
	};
	
	$('#header').find('#header1').find('span.emp').text($('#lienzo_recalculable').find('input[name=emp]').val());
	$('#header').find('#header1').find('span.suc').text($('#lienzo_recalculable').find('input[name=suc]').val());
    var $username = $('#header').find('#header1').find('span.username');
	$username.text($('#lienzo_recalculable').find('input[name=user]').val());
	
	var $contextpath = $('#lienzo_recalculable').find('input[name=contextpath]');
	var controller = $contextpath.val()+"/controllers/carteras";
    
    
    //Barra para las acciones
    $('#barra_acciones').append($('#lienzo_recalculable').find('.table_acciones'));
    $('#barra_acciones').find('.table_acciones').css({'display':'block'});
    var $new_pago = $('#barra_acciones').find('.table_acciones').find('a[href*=new_item]');
	var $reporte_edo_cta_cliente = $('#barra_acciones').find('.table_acciones').find('a[href*=reporte_edo_cta_cliente]');
	var $visualiza_buscador = $('#barra_acciones').find('.table_acciones').find('a[href*=visualiza_buscador]');
    
	$('#barra_acciones').find('.table_acciones').find('#nItem').mouseover(function(){
		$(this).removeClass("onmouseOutNewItem").addClass("onmouseOverNewItem");
	});
	$('#barra_acciones').find('.table_acciones').find('#nItem').mouseout(function(){
		$(this).removeClass("onmouseOverNewItem").addClass("onmouseOutNewItem");
	});
    
    
	$('#barra_acciones').find('.table_acciones').find('#repEdoCta').mouseover(function(){
		$(this).removeClass("onmouseOutGeneraInforme").addClass("onmouseOverGeneraInforme");
	});
	$('#barra_acciones').find('.table_acciones').find('#repEdoCta').mouseout(function(){
		$(this).removeClass("onmouseOverGeneraInforme").addClass("onmouseOutGeneraInforme");
	});
	
	$('#barra_acciones').find('.table_acciones').find('#vbuscador').mouseover(function(){
		$(this).removeClass("onmouseOutVisualizaBuscador").addClass("onmouseOverVisualizaBuscador");
	});
	$('#barra_acciones').find('.table_acciones').find('#vbuscador').mouseout(function(){
		$(this).removeClass("onmouseOverVisualizaBuscador").addClass("onmouseOutVisualizaBuscador");
	});
	
    
	//aqui va el titulo del catalogo
	$('#barra_titulo').find('#td_titulo').append('Carteras');
    
	
	//barra para el buscador 
	//$('#barra_buscador').css({'height':'80px'});
	$('#barra_buscador').append($('#lienzo_recalculable').find('.tabla_buscador'));
	//$('#barra_buscador').find('.tabla_buscador').css({'display':'block'});
	//$('#barra_buscador').hide();
	
	
	var $cadena_busqueda = "";
	var $busqueda_num_transaccion = $('#barra_buscador').find('.tabla_buscador').find('input[name=busqueda_num_transaccion]');
	var $busqueda_factura = $('#barra_buscador').find('.tabla_buscador').find('input[name=busqueda_factura]');
	var $busqueda_cliente = $('#barra_buscador').find('.tabla_buscador').find('input[name=busqueda_cliente]');
	var $busqueda_fecha_inicial = $('#barra_buscador').find('.tabla_buscador').find('input[name=busqueda_fecha_inicial]');
	var $busqueda_fecha_final = $('#barra_buscador').find('.tabla_buscador').find('input[name=busqueda_fecha_final]');
	
	var $buscar = $('#barra_buscador').find('.tabla_buscador').find('#boton_buscador');
	var $limpiar = $('#barra_buscador').find('.tabla_buscador').find('#boton_limpiar');
	$buscar.mouseover(function(){
		$(this).removeClass("onmouseOutBuscar").addClass("onmouseOverBuscar");
	});
	$buscar.mouseout(function(){
		$(this).removeClass("onmouseOverBuscar").addClass("onmouseOutBuscar");
	});
	   
	$limpiar.mouseover(function(){
		$(this).removeClass("onmouseOutLimpiar").addClass("onmouseOverLimpiar");
	});
	$limpiar.mouseout(function(){
		$(this).removeClass("onmouseOverLimpiar").addClass("onmouseOutLimpiar");
	});
	
	var to_make_one_search_string = function(){
		var valor_retorno = "";
		var signo_separador = "=";
		valor_retorno += "num_transaccion" + signo_separador + $busqueda_num_transaccion.val() + "|";
		valor_retorno += "factura" + signo_separador + $busqueda_factura.val() + "|";
		valor_retorno += "cliente" + signo_separador + $busqueda_cliente.val() + "|";
		valor_retorno += "fecha_inicial" + signo_separador + $busqueda_fecha_inicial.val() + "|";
		valor_retorno += "fecha_final" + signo_separador + $busqueda_fecha_final.val();
		return valor_retorno;
	};
        
	cadena = to_make_one_search_string();
	$cadena_busqueda = cadena.toCharCode();
	
	$buscar.click(function(event){
		event.preventDefault();
		cadena = to_make_one_search_string();
		$cadena_busqueda = cadena.toCharCode();
		$get_datos_grid();
	});
    
	$limpiar.click(function(event){
		$busqueda_num_transaccion.val('');
		$busqueda_factura.val('');
		$busqueda_cliente.val('');
		$busqueda_fecha_inicial.val('');
		$busqueda_fecha_final.val('');
		$busqueda_num_transaccion.focus();
		
		//$get_datos_grid();
	});    
    
    
	//reportes 
	$('#barra_genera_informe').append($('#lienzo_recalculable').find('.tabla_genera_reportes'));
    var $boton_generar_reporte_depositos = $('#barra_genera_informe').find('.tabla_genera_reportes').find('#reporte_depositos');
   
    
    
	TriggerClickVisializaBuscador = 0;
	TriggerClickVisualizaGeneradorInforme=0;
	//visualizar  la barra del buscador
	$visualiza_buscador.click(function(event){
		event.preventDefault();
		$('#barra_genera_informe').find('.tabla_genera_reportes').css({'display':'none'});
		
		if(parseInt(TriggerClickVisualizaGeneradorInforme)==1){
			$reporte_edo_cta_cliente.trigger('click');
		}
		
		var alto=0;
		if(TriggerClickVisializaBuscador==0){
			 TriggerClickVisializaBuscador=1;
			 var height2 = $('#cuerpo').css('height');
			 //alert('height2: '+height2);
			 
			 alto = parseInt(height2)-220;
			 var pix_alto=alto+'px';
			 //alert('pix_alto: '+pix_alto);
			 
			 $('#barra_buscador').find('.tabla_buscador').css({'display':'block'});
			 $('#barra_buscador').animate({height: '80px'}, 500);
			 $('#cuerpo').css({'height': pix_alto});
			 
			 //alert($('#cuerpo').css('height'));
		}else{
			 TriggerClickVisializaBuscador=0;
			 var height2 = $('#cuerpo').css('height');
			 alto = parseInt(height2)+220;
			 var pix_alto=alto+'px';
			 
			 $('#barra_buscador').animate({height:'0px'}, 500);
			 $('#cuerpo').css({'height': pix_alto});
		};
		$busqueda_num_transaccion.focus();
	});
	
	//aplicar evento Keypress para que al pulsar enter ejecute la busqueda
	$(this).aplicarEventoKeypressEjecutaTrigger($busqueda_num_transaccion, $buscar);
	$(this).aplicarEventoKeypressEjecutaTrigger($busqueda_factura, $buscar);
	$(this).aplicarEventoKeypressEjecutaTrigger($busqueda_cliente, $buscar);
	$(this).aplicarEventoKeypressEjecutaTrigger($busqueda_fecha_inicial, $buscar);
	$(this).aplicarEventoKeypressEjecutaTrigger($busqueda_fecha_final, $buscar);
	
	//visualizar generador de informe mensual
	$reporte_edo_cta_cliente.click(function(event){
		event.preventDefault();
		var alto=0;
		$('#barra_buscador').find('.tabla_buscador').css({'display':'none'});
		//$('#barra_descarga_xml').find('.tabla_decarga_xml').css({'display':'none'});
		
		if(parseInt(TriggerClickVisializaBuscador)==1){
			$visualiza_buscador.trigger('click');
		}
		/*
		if(parseInt(TriggerClickVisualizaDescargaXml)==1){
			$descargar_xml.trigger('click');
		}
		*/
		
		if(TriggerClickVisualizaGeneradorInforme==0){
			 TriggerClickVisualizaGeneradorInforme=1;
			 var height2 = $('#cuerpo').css('height');
			 
			 alto = parseInt(height2)-220;
			 var pix_alto=alto+'px';
			 
			 $('#barra_genera_informe').find('.tabla_genera_reportes').css({'display':'block'});
			 $('#barra_genera_informe').animate({height: '80px'}, 500);
			 $('#cuerpo').css({'height': pix_alto});
		}else{
			 TriggerClickVisualizaGeneradorInforme=0;
			 var height2 = $('#cuerpo').css('height');
			 alto = parseInt(height2)+220;
			 var pix_alto=alto+'px';
			 
			 $('#barra_genera_informe').animate({height:'0px'}, 500);
			 $('#cuerpo').css({'height': pix_alto});
		};
	});
	
	
	
    
	//----------------------------------------------------------------
	//valida la fecha seleccionada
	function mayor(fecha, fecha2){
		var xMes=fecha.substring(5, 7);
		var xDia=fecha.substring(8, 10);
		var xAnio=fecha.substring(0,4);
		var yMes=fecha2.substring(5, 7);
		var yDia=fecha2.substring(8, 10);
		var yAnio=fecha2.substring(0,4);
                
		if (xAnio > yAnio){
			return(true);
		}else{
			if (xAnio == yAnio){
				if (xMes > yMes){
					return(true);
				}
				if (xMes == yMes){
					if (xDia > yDia){
						return(true);
					}else{
						return(false);
					}
				}else{
					return(false);
				}
			}else{
				return(false);
			}
		}
	}
	//muestra la fecha actual
	var mostrarFecha = function mostrarFecha(){
		var ahora = new Date();
		var anoActual = ahora.getFullYear();
		var mesActual = ahora.getMonth();
		mesActual = mesActual+1;
		mesActual = (mesActual <= 9)?"0" + mesActual : mesActual;
		var diaActual = ahora.getDate();
		diaActual = (diaActual <= 9)?"0" + diaActual : diaActual;
		var Fecha = anoActual + "-" + mesActual + "-" + diaActual;		
		return Fecha;
	}
	//----------------------------------------------------------------
        
        
	$busqueda_fecha_inicial.click(function (s){
		var a=$('div.datepicker');
		a.css({'z-index':100});
	});
        
	$busqueda_fecha_inicial.DatePicker({
		format:'Y-m-d',
		date: $(this).val(),
		current: $(this).val(),
		starts: 1,
		position: 'bottom',
		locale: {
			days: ['Domingo', 'Lunes', 'Martes', 'Miércoles', 'Jueves', 'Viernes', 'Sábado','Domingo'],
			daysShort: ['Dom', 'Lun', 'Mar', 'Mir', 'Jue', 'Vir', 'Sab','Dom'],
			daysMin: ['Do', 'Lu', 'Ma', 'Mi', 'Ju', 'Vi', 'Sa','Do'],
			months: ['Enero', 'Febrero', 'Marzo', 'Abril', 'Mayo','Junio', 'Julio', 'Agosto', 'Septiembre','Octubre', 'Noviembre', 'Diciembre'],
			monthsShort: ['Ene', 'Feb', 'Mar', 'Abr','May', 'Jun', 'Jul', 'Ago','Sep', 'Oct', 'Nov', 'Dic'],
			weekMin: 'se'
		},
		onChange: function(formated, dates){
			var patron = new RegExp("^[0-9]{4}-[0-9]{1,2}-[0-9]{1,2}$");
			$busqueda_fecha_inicial.val(formated);
			if (formated.match(patron) ){
				var valida_fecha=mayor($busqueda_fecha_inicial.val(),mostrarFecha());
				
				if (valida_fecha==true){
					jAlert("Fecha no valida",'! Atencion');
					$busqueda_fecha_inicial.val(mostrarFecha());
				}else{
					$busqueda_fecha_inicial.DatePickerHide();	
				}
			}
		}
	});
        
	$busqueda_fecha_final.click(function (s){
		var a=$('div.datepicker');
		a.css({'z-index':100});
	});
        
	$busqueda_fecha_final.DatePicker({
		format:'Y-m-d',
		date: $(this).val(),
		current: $(this).val(),
		starts: 1,
		position: 'bottom',
		locale: {
			days: ['Domingo', 'Lunes', 'Martes', 'Miércoles', 'Jueves', 'Viernes', 'Sábado','Domingo'],
			daysShort: ['Dom', 'Lun', 'Mar', 'Mir', 'Jue', 'Vir', 'Sab','Dom'],
			daysMin: ['Do', 'Lu', 'Ma', 'Mi', 'Ju', 'Vi', 'Sa','Do'],
			months: ['Enero', 'Febrero', 'Marzo', 'Abril', 'Mayo','Junio', 'Julio', 'Agosto', 'Septiembre','Octubre', 'Noviembre', 'Diciembre'],
			monthsShort: ['Ene', 'Feb', 'Mar', 'Abr','May', 'Jun', 'Jul', 'Ago','Sep', 'Oct', 'Nov', 'Dic'],
			weekMin: 'se'
		},
		onChange: function(formated, dates){
			var patron = new RegExp("^[0-9]{4}-[0-9]{1,2}-[0-9]{1,2}$");
			$busqueda_fecha_final.val(formated);
			if (formated.match(patron) ){
				var valida_fecha=mayor($busqueda_fecha_final.val(),mostrarFecha());
				
				if (valida_fecha==true){
					jAlert("Fecha no valida",'! Atencion');
					$busqueda_fecha_final.val(mostrarFecha());
				}else{
					$busqueda_fecha_final.DatePickerHide();	
				}
			}
		}
	});
        
        
        
        
	
	
	$boton_generar_reporte_depositos.click(function(event){
		$(this).modalPanel_depositos();
		var $dialogoc =  $('#forma-depositos-window');
		$dialogoc.append($('div.reporte_depositos').find('table.formaDepositos').clone());
		$('#forma-depositos-window').css({"margin-left": -100, 	"margin-top": -120});
		
		
		var $genera_rep_depositos = $('#forma-depositos-window').find('a[href*=genera_rep_depositos]');
		var $cencela_depositos = $('#forma-depositos-window').find('a[href*=cencela_depositos]');
		//var $busca_cliente = $('#forma-depositos-window').find('a[href*=busca_cliente]');
	
		//var $select_tipo_reporte = $('#forma-depositos-window').find('select[name=tipo_reporte]');
		var $fecha_inicial = $('#forma-depositos-window').find('input[name=fecha_inicial]');
		var $fecha_final = $('#forma-depositos-window').find('input[name=fecha_final]');
		//var $id_cliente_edo_cta = $('#forma-depositos-window').find('input[name=id_cliente_edo_cta]');
		//var $rfc_cli = $('#forma-depositos-window').find('input[name=rfc_cli]');
		//var $razon_cli = $('#forma-depositos-window').find('input[name=razon_cli]');
		
		
		$fecha_inicial.attr('readonly',true);
		$fecha_final.attr('readonly',true);
		/*
		$rfc_cli.attr('readonly',true);
		$razon_cli.attr('readonly',true);
		
		
		$('#forma-depositos-window').find('tr.cliente').hide();
		$select_tipo_reporte.children().remove();
		html='<option value="0">General</option>';
		html+='<option value="1">Por cliente</option>';
		$select_tipo_reporte.append(html);
		*/
		
		$fecha_inicial.val(mostrarFecha());
		$fecha_inicial.click(function (s){
			var a=$('div.datepicker');
			a.css({'z-index':100,});
		});
		$fecha_inicial.DatePicker({
			format:'Y-m-d',
			onBeforeShow: function(){
				$fecha_inicial.DatePickerSetDate($fecha_inicial.val(), true);
			},
			date: $fecha_inicial.val(),
			current: $fecha_inicial.val(),
			starts: 1,
			position: 'bottom',
			locale: {
				days: ['Domingo', 'Lunes', 'Martes', 'Miércoles', 'Jueves', 'Viernes', 'Sábado','Domingo'],
				daysShort: ['Dom', 'Lun', 'Mar', 'Mir', 'Jue', 'Vir', 'Sab','Dom'],
				daysMin: ['Do', 'Lu', 'Ma', 'Mi', 'Ju', 'Vi', 'Sa','Do'],
				months: ['Enero', 'Febrero', 'Marzo', 'Abril', 'Mayo','Junio', 'Julio', 'Agosto', 'Septiembre','Octubre', 'Noviembre', 'Diciembre'],
				monthsShort: ['Ene', 'Feb', 'Mar', 'Abr','May', 'Jun', 'Jul', 'Ago','Sep', 'Oct', 'Nov', 'Dic'],
				weekMin: 'se'
			},
			onChange: function(formated, dates){
				var patron = new RegExp("^[0-9]{4}-[0-9]{1,2}-[0-9]{1,2}$");
				$fecha_inicial.val(formated);
				if (formated.match(patron) ){
					$fecha_inicial.DatePickerHide();
				};
			}
		});
		
		$fecha_final.val(mostrarFecha());
		$fecha_final.click(function (s){
			var a=$('div.datepicker');
			a.css({'z-index':100,});
		});
		$fecha_final.DatePicker({
			format:'Y-m-d',
			onBeforeShow: function(){
				$fecha_final.DatePickerSetDate($fecha_final.val(), true);
			},
			date: $fecha_final.val(),
			current: $fecha_final.val(),
			starts: 1,
			position: 'bottom',
			locale: {
				days: ['Domingo', 'Lunes', 'Martes', 'Miércoles', 'Jueves', 'Viernes', 'Sábado','Domingo'],
				daysShort: ['Dom', 'Lun', 'Mar', 'Mir', 'Jue', 'Vir', 'Sab','Dom'],
				daysMin: ['Do', 'Lu', 'Ma', 'Mi', 'Ju', 'Vi', 'Sa','Do'],
				months: ['Enero', 'Febrero', 'Marzo', 'Abril', 'Mayo','Junio', 'Julio', 'Agosto', 'Septiembre','Octubre', 'Noviembre', 'Diciembre'],
				monthsShort: ['Ene', 'Feb', 'Mar', 'Abr','May', 'Jun', 'Jul', 'Ago','Sep', 'Oct', 'Nov', 'Dic'],
				weekMin: 'se'
			},
			onChange: function(formated, dates){
				var patron = new RegExp("^[0-9]{4}-[0-9]{1,2}-[0-9]{1,2}$");
				$fecha_final.val(formated);
				if (formated.match(patron) ){
					$fecha_final.DatePickerHide();
				};
			}
		});
		
		//cadena json
		//http://localhost:8080/com.mycompany_Kemikal_war_1.0-SNAPSHOT/controllers/carteras/get_genera_pdf_depositos/2012-01-01/2012-03-01/out.json
		
		//click generar reporte de depositos
		$genera_rep_depositos.click(function(event){
			event.preventDefault();
                        alert("$genera_rep_depositos");
			var fechainicial = $fecha_inicial.val();
			var fechafinal = $fecha_final.val();
			var iu = $('#lienzo_recalculable').find('input[name=iu]').val();
			var input_json = document.location.protocol + '//' + document.location.host + '/' + controller + '/get_genera_pdf_depositos/'+fechainicial+'/'+fechafinal+'/'+iu+'/out.json';
			window.location.href=input_json;
		});//termina llamada json
		
                //NLE: Obtener PDF de Factura
                
                //$("#pdf_factura").click(function(event){
                /*    try {
                        var selector = $(this).data('selector');
                        alert("selector:"+selector);
                            event.preventDefault();
                            var iu = $('#lienzo_recalculable').find('input[name=iu]').val();
                            var input_json = document.location.protocol + '//' + document.location.host + '/' + controller + '/get_genera_pdf_depositos/'+iu+'/outFactura.json';
                            window.location.href=input_json;
                    }catch(err){
                        alert("Error:"+err);
                    }
                */
                //}
                
		//cancela busqueda
		$cencela_depositos.click(function(event){
			event.preventDefault();
			var remove = function() {$(this).remove();};
			$('#forma-depositos-overlay').fadeOut(remove);
		});
	
	});
	
	
	

	
	$tabs_li_funxionalidad = function(){
            var $select_prod_tipo = $('#forma-carteras-window').find('select[name=prodtipo]');
            //boton registrar pago
            $('#forma-carteras-window').find('#submit_pago').mouseover(function(){
                //$('#forma-carteras-window').find('#submit_pago').removeAttr("src").attr("src","../../img/modalbox/bt1.png");
                $('#forma-carteras-window').find('#submit_pago').css({backgroundImage:"url(../../img/modalbox/pago_over.png)"});
            })
            $('#forma-carteras-window').find('#submit_pago').mouseout(function(){
                //$('#forma-carteras-window').find('#submit_pago').removeAttr("src").attr("src","../../img/modalbox/btn1.png");
                $('#forma-carteras-window').find('#submit_pago').css({backgroundImage:"url(../../img/modalbox/pago.png)"});
            })
            
            //boton registrar anticipo
            $('#forma-carteras-window').find('#registra_anticipo').mouseover(function(){
                //$('#forma-carteras-window').find('#submit_pago').removeAttr("src").attr("src","../../img/modalbox/bt1.png");
                $('#forma-carteras-window').find('#registra_anticipo').css({backgroundImage:"url(../../img/modalbox/anticipo_over.png)"});
            })
            $('#forma-carteras-window').find('#registra_anticipo').mouseout(function(){
                //$('#forma-carteras-window').find('#submit_pago').removeAttr("src").attr("src","../../img/modalbox/btn1.png");
                $('#forma-carteras-window').find('#registra_anticipo').css({backgroundImage:"url(../../img/modalbox/anticipo.png)"});
            })
            
            
            //boton registrar cancelacion
            $('#forma-carteras-window').find('#submit_cancel').mouseover(function(){
                //$('#forma-carteras-window').find('#submit_cancel').removeAttr("src").attr("src","../../img/modalbox/bt1.png");
                $('#forma-carteras-window').find('#submit_cancel').css({backgroundImage:"url(../../img/modalbox/cancelacion_over.png)"});
            })
            $('#forma-carteras-window').find('#submit_cancel').mouseout(function(){
                //$('#forma-carteras-window').find('#submit_cancel').removeAttr("src").attr("src","../../img/modalbox/btn1.png");
                $('#forma-carteras-window').find('#submit_cancel').css({backgroundImage:"url(../../img/modalbox/cancelacion.png)"});
            })
            
            $('#forma-carteras-window').find('#boton_cancelar').mouseover(function(){
                $('#forma-carteras-window').find('#boton_cancelar').css({backgroundImage:"url(../../img/modalbox/bt2.png)"});
            })
            $('#forma-carteras-window').find('#boton_cancelar').mouseout(function(){
                $('#forma-carteras-window').find('#boton_cancelar').css({backgroundImage:"url(../../img/modalbox/btn2.png)"});
            })
            $('#forma-carteras-window').find('#close').mouseover(function(){
                $('#forma-carteras-window').find('#close').css({backgroundImage:"url(../../img/modalbox/close_over.png)"});
            })
            $('#forma-carteras-window').find('#close').mouseout(function(){
                $('#forma-carteras-window').find('#close').css({backgroundImage:"url(../../img/modalbox/close.png)"});
            })
			
            $('#forma-carteras-window').find(".contenidoPes").hide(); //Hide all content
            $('#forma-carteras-window').find("ul.pestanas li:first").addClass("active").show(); //Activate first tab
            $('#forma-carteras-window').find(".contenidoPes:first").show(); //Show first tab content

            //On Click Event
            $('#forma-carteras-window').find("ul.pestanas li").click(function() {
                $('#forma-carteras-window').find(".contenidoPes").hide();
                $('#forma-carteras-window').find("ul.pestanas li").removeClass("active");
                var activeTab = $(this).find("a").attr("href");
                $('#forma-carteras-window').find( activeTab , "ul.pestanas li" ).fadeIn().show();
                $(this).addClass("active");
                return false;
            });
	}
	
	
	var $agregarDatosClienteSeleccionado = function(idCliente, rfc, noControl, razonSocial, $select_moneda, $select_forma_pago, $select_banco, $campo_banco_deposito, arrayMonedas, arrayFormasPago, arrayBancos, arrayBancosDeposito){
		$('#forma-carteras-window').find('input[name=identificador_cliente]').val(idCliente);
		$('#forma-carteras-window').find('input[name=rfccliente]').val(rfc);
		$('#forma-carteras-window').find('input[name=nocliente]').val(noControl);
		$('#forma-carteras-window').find('input[name=cliente]').val(razonSocial);
		
		
		//carga select con todas las monedas
		$select_moneda.children().remove();
		var tipo_moneda_hmtl="";// = '<option value="0" selected="yes">Seleccione una opcion</option>';
		$.each(arrayMonedas,function(entryIndex,moneda){
			tipo_moneda_hmtl += '<option value="' + moneda['id'] + '"  >' + moneda['descripcion'] + '</option>';
		});
		$select_moneda.append(tipo_moneda_hmtl);
		
		//carga select con todas las formas de pago
		$select_forma_pago.children().remove();
		var forma_pago_hmtl="";// = '<option value="0" selected="yes">Seleccione una opcion</option>';
		$.each(arrayFormasPago,function(entryIndex,fpago){
			forma_pago_hmtl += '<option value="' + fpago['titulo'] + '"  >' + fpago['titulo'] + '</option>';
		});
		$select_forma_pago.append(forma_pago_hmtl);
		
		
		//carga select con todos los bancos
		$select_banco.children().remove();
		var banco_hmtl="";// = '<option value="0" selected="yes">Seleccione una opcion</option>';
		$.each(arrayBancos,function(entryIndex,banco){
			banco_hmtl += '<option value="' + banco['id'] + '"  >' + banco['titulo'] + '</option>';
		});
		$select_banco.append(banco_hmtl);
	
		//carga select con todos los bancos de kemikal
		$campo_banco_deposito.children().remove();
		var banco_k_hmtl="";// = '<option value="0" selected="yes">Seleccione una opcion</option>';
		$.each(arrayBancosDeposito,function(entryIndex,bancok){
			banco_k_hmtl += '<option value="' + bancok['id'] + '"  >' + bancok['titulo'] + '</option>';
		});
		$campo_banco_deposito.append(banco_k_hmtl);
		
		
		
	   //habilitar select de tipo movimiento
		$('#forma-carteras-window').find('select[name=tipo_mov]').removeAttr('disabled');//habilitar select
		$('#forma-carteras-window').find('select[name=tipo_mov]').focus();
	}
	
	
	//buscador de clientes
	$busca_clientes = function(tipo, numero_control, razon_social_cliente, $select_moneda, $select_forma_pago, $select_banco, $campo_banco_deposito, arrayMonedas, arrayFormasPago, arrayBancos, arrayBancosDeposito){
            $(this).modalPanel_Buscacliente();
            var $dialogoc =  $('#forma-buscacliente-window');
            //var $dialogoc.prependTo('#forma-buscaproduct-window');
            $dialogoc.append($('div.buscador_clientes').find('table.formaBusqueda_clientes').clone());
            $('#forma-buscacliente-window').css({"margin-left": -200, 	"margin-top": -180});

            var $tabla_resultados = $('#forma-buscacliente-window').find('#tabla_resultado');
			
			var $busca_cliente_modalbox = $('#forma-buscacliente-window').find('#busca_cliente_modalbox');
			var $cancelar_plugin_busca_cliente = $('#forma-buscacliente-window').find('#cencela');
			
            var $cadena_buscar = $('#forma-buscacliente-window').find('input[name=cadena_buscar]');
            var $select_filtro_por = $('#forma-buscacliente-window').find('select[name=filtropor]');
            
            //funcionalidad botones
			$busca_cliente_modalbox.mouseover(function(){
				$(this).removeClass("onmouseOutBuscar").addClass("onmouseOverBuscar");
			});
			$busca_cliente_modalbox.mouseout(function(){
				$(this).removeClass("onmouseOverBuscar").addClass("onmouseOutBuscar");
			});
			   
			$cancelar_plugin_busca_cliente.mouseover(function(){
				$(this).removeClass("onmouseOutCancelar").addClass("onmouseOverCancelar");
			});
			$cancelar_plugin_busca_cliente.mouseout(function(){
				$(this).removeClass("onmouseOverCancelar").addClass("onmouseOutCancelar");
			});
            
            var html = '';
			$select_filtro_por.children().remove();
			html='<option value="0">[-- Opcion busqueda --]</option>';
			
			if(numero_control != ''){
				//asignamos el numero de control al campo de busqueda
				$cadena_buscar.val(numero_control);
				if(razon_social_cliente == ''){
					html+='<option value="1" selected="yes">No. de control</option>';
				}else{
					html+='<option value="1">No. de control</option>';
				}
			}else{
				html+='<option value="1">No. de control</option>';
			}
			html+='<option value="2">RFC</option>';
			if(razon_social_cliente != ''){
				//asignamos la Razon Social del Cliente al campo Nombre
				$cadena_buscar.val(razon_social_cliente);
				html+='<option value="3" selected="yes">Razon social</option>';
			}else{
				if(razon_social_cliente == '' && numero_control == ''){
					html+='<option value="3" selected="yes">Razon social</option>';
				}else{
					html+='<option value="3">Razon social</option>';
				}
			}
			html+='<option value="4">CURP</option>';
			html+='<option value="5">Alias</option>';
			$select_filtro_por.append(html);
            
            $cadena_buscar.select();
            $cadena_buscar.focus();
            
            //click buscar clientes
            $busca_cliente_modalbox.click(function(event){
                var input_json = document.location.protocol + '//' + document.location.host + '/'+controller+'/get_buscador_clientes.json';
                $arreglo = {'cadena':$cadena_buscar.val(),
                             'filtro':$select_filtro_por.val(),
                             'iu': $('#lienzo_recalculable').find('input[name=iu]').val()
                            }
                            
                var trr = '';
                $tabla_resultados.children().remove();
                $.post(input_json,$arreglo,function(entry){
                    $.each(entry['Clientes'],function(entryIndex,cliente){
                        trr = '<tr>';
                            trr += '<td width="80">';
                                trr += '<input type="hidden" id="idclient" value="'+cliente['id']+'">';
                                trr += '<input type="hidden" id="direccion" value="'+cliente['direccion']+'">';
                                trr += '<input type="hidden" id="id_moneda" value="'+cliente['moneda_id']+'">';
                                trr += '<input type="hidden" id="moneda" value="'+cliente['moneda']+'">';
                                trr += '<span class="no_control">'+cliente['numero_control']+'</span>';
                            trr += '</td>';
                            trr += '<td width="145"><span class="rfc">'+cliente['rfc']+'</span></td>';
                            trr += '<td width="375"><span class="razon">'+cliente['razon_social']+'</span></td>';
                        trr += '</tr>';
                        
                        $tabla_resultados.append(trr);
                    });

                    $tabla_resultados.find('tr:odd').find('td').css({'background-color' : '#e7e8ea'});
                    $tabla_resultados.find('tr:even').find('td').css({'background-color' : '#FFFFFF'});

                    $('tr:odd' , $tabla_resultados).hover(function () {
                        $(this).find('td').css({background : '#FBD850'});
                    }, function() {
                            //$(this).find('td').css({'background-color':'#DDECFF'});
                        $(this).find('td').css({'background-color':'#e7e8ea'});
                    });
                    $('tr:even' , $tabla_resultados).hover(function () {
                        $(this).find('td').css({'background-color':'#FBD850'});
                    }, function() {
                        $(this).find('td').css({'background-color':'#FFFFFF'});
                    });
					
                    //seleccionar un producto del grid de resultados
                    $tabla_resultados.find('tr').click(function(){
						
						if(parseInt(tipo)==1){
							var idCliente = $(this).find('#idclient').val();
							var rfc = $(this).find('span.rfc').html();
							var noControl = $(this).find('span.no_control').html();
							var razonSocial = $(this).find('span.razon').html();
							
							//llamada a la funcion para agregar los datos del cliente
							$agregarDatosClienteSeleccionado(idCliente, rfc, noControl, razonSocial, $select_moneda, $select_forma_pago, $select_banco, $campo_banco_deposito, arrayMonedas, arrayFormasPago, arrayBancos, arrayBancosDeposito);
							
                        }
                        
                        //elimina la ventana de busqueda
                        var remove = function() {$(this).remove();};
                        $('#forma-buscacliente-overlay').fadeOut(remove);
                        //asignar el enfoque al campo sku del producto
                    });
                });
            });//termina llamada json
			
			
			//si hay algo en el campo cadena_buscar al cargar el buscador, ejecuta la busqueda
			if($cadena_buscar.val() != ''){
				$busca_cliente_modalbox.trigger('click');
			}
			
			$(this).aplicarEventoKeypressEjecutaTrigger($cadena_buscar, $busca_cliente_modalbox);
			$(this).aplicarEventoKeypressEjecutaTrigger($select_filtro_por, $busca_cliente_modalbox);
			
            $cancelar_plugin_busca_cliente.click(function(event){
                var remove = function() {$(this).remove();};
                $('#forma-buscacliente-overlay').fadeOut(remove);
                $('#forma-carteras-window').find('input[name=cliente]').focus();
            });
	}//termina buscador de clientes
	
	
	
	var quitar_comas= function($valor){
		$valor = $valor+'';
		return $valor.split(',').join('');
	}
	
    
	//aqui entra depenediendo cuando de cambia el tipo de movimiento
	var tipos_de_movimiento = function($contenido2,tipo_movimiento,$submit_registrar_pago,$registra_anticipo,$submit_registrar_cancelacion){
				//alert($('#forma-carteras-window').find('select[name=tipo_mov]').val());
                
                var $identificador_cliente = $('#forma-carteras-window').find('input[name=identificador_cliente]');
				var $rfccliente = $('#forma-carteras-window').find('input[name=rfccliente]');
				var $nocliente = $('#forma-carteras-window').find('input[name=nocliente]');
				
                var $cliente = $('#forma-carteras-window').find('input[name=cliente]');
                var $monto_pago = $('#forma-carteras-window').find('input[name=monto_pago]');
                
                $monto_pago.val('0.00');
                var $monto_anticipo=0;
                
                var $select_moneda = $('#forma-carteras-window').find('select[name=moneda]');
                var $campo_fecha = $('#forma-carteras-window').find('input[name=fecha_pago]');
                
                /***********************************************************************************************************************************/
                /**/var $campo_banco_deposito = $('#forma-carteras-window').find('div.ficha_deposito').find('select[name=forma_banco_kemikal]');           /**/
                /**/var $campo_cuenta_deposito = $('#forma-carteras-window').find('div.ficha_deposito').find('select[name=cuenta_deposito]');       /**/
                /**/var $campo_movimiento_deposito = $('#forma-carteras-window').find('div.ficha_deposito').find('input[name=movimiento_deposito]');/**/
                /**/var $campo_fecha_deposito= $('#forma-carteras-window').find('div.ficha_deposito').find('input[name=fecha_pago_deposito]');      /**/
                /***********************************************************************************************************************************/
                
                var $tipo_cambio = $('#forma-carteras-window').find('input[name=tipo_cambio]');
                var $textarea_anticipo = $('#forma-carteras-window').find('textarea[name=anticipos]');
                
                var $sum_anticipos_mn = $('#forma-carteras-window').find('input[name=sum_anticipos_mn]');
                var $sum_anticipos_usd = $('#forma-carteras-window').find('input[name=sum_anticipos_usd]');
				
                var $textarea_monto_antipo = $('#forma-carteras-window').find('textarea[name=monto_anticipos]');
                var $textarea_observaciones = $('#forma-carteras-window').find('textarea[name=observaciones]');
                var $select_forma_pago = $('#forma-carteras-window').find('select[name=forma_pago]');
                var $campo_cheque = $('#forma-carteras-window').find('input[name=num_cheque]');
                var $campo_tarjeta = $('#forma-carteras-window').find('input[name=num_tarjeta]');
                var $campo_referencia = $('#forma-carteras-window').find('input[name=referencia]');
                var $select_banco = $('#forma-carteras-window').find('select[name=bancos]');
                var $textarea_observaciones2 = $('#forma-carteras-window').find('textarea[name=observaciones_2]');
                
                var $campo_monto_ant_selecc = $('#forma-carteras-window').find('input[name=monto_ant_selec]');
                var $campo_pagosxguardar = $('#forma-carteras-window').find('input[name=pagosxguardar]');
                
                var $deuda_pesos=0;
                var $deuda_usd=0;
                
                var $anticipos = 0;
                $monto_anticipo = $monto_pago.val();
                //funciones
                
				function mayor(fecha, fecha2){
					//alert("fecha:"+fecha);
					var xMes=fecha.substring(5, 7);
					var xDia=fecha.substring(8, 10);
					var xAnio=fecha.substring(0,4);
					var yMes=fecha2.substring(5, 7);
					var yDia=fecha2.substring(8, 10);
					var yAnio=fecha2.substring(0,4);
					
					if (xAnio > yAnio){
						return(true);
					}else{
						if (xAnio == yAnio){
						if (xMes > yMes){
							return(true);
						}
						if (xMes == yMes){
							if (xDia > yDia){
								return(true);
							}else{
							return(false);
							}
						}else{
							return(false);
						}
						}else{
						return(false);
						}
					}
				}
				
				
				var mostrarFecha = function mostrarFecha(){
					var ahora = new Date();
					var anoActual = ahora.getFullYear();
					var mesActual = ahora.getMonth();
					mesActual = mesActual+1;
					mesActual = (mesActual <= 9)?"0" + mesActual : mesActual;
					var diaActual = ahora.getDate();
					diaActual = (diaActual <= 9)?"0" + diaActual : diaActual;
					var Fecha = anoActual + "-" + mesActual + "-" + diaActual;		
					return Fecha;
				}

				var validar_movimiento=function(){
					var patron= /^[0-7]{4}$/;
					if(patron.test($campo_cuenta_deposito.val())){
						
					}else{
						jAlert('el numero de cuenta no es valido','! Atencion');
					}
				}
				
				$campo_fecha.attr('readonly',true);
				$campo_cuenta_deposito.attr('readonly',true);
				$campo_fecha_deposito.attr('readonly',true);
				$campo_fecha_deposito.val(mostrarFecha());
				
				$campo_fecha_deposito.click(function (s){
					var a=$('div.datepicker');
					a.css({'z-index':100,});
				});
				
				
				$campo_fecha_deposito.DatePicker({
					format:'Y-m-d',
					//date: $(this).val(),
					//current: $(this).val(),
					date: $campo_fecha_deposito.val(),
					current: $campo_fecha_deposito.val(),
					starts: 1,
					position: 'bottom',
					locale: {
						days: ['Domingo', 'Lunes', 'Martes', 'Miércoles', 'Jueves', 'Viernes', 'Sábado','Domingo'],
						daysShort: ['Dom', 'Lun', 'Mar', 'Mir', 'Jue', 'Vir', 'Sab','Dom'],
						daysMin: ['Do', 'Lu', 'Ma', 'Mi', 'Ju', 'Vi', 'Sa','Do'],
						months: ['Enero', 'Febrero', 'Marzo', 'Abril', 'Mayo','Junio', 'Julio', 'Agosto', 'Septiembre','Octubre', 'Noviembre', 'Diciembre'],
						monthsShort: ['Ene', 'Feb', 'Mar', 'Abr','May', 'Jun', 'Jul', 'Ago','Sep', 'Oct', 'Nov', 'Dic'],
						weekMin: 'se'
					},
					onChange: function(formated, dates){
						var patron = new RegExp("^[0-9]{4}-[0-9]{1,2}-[0-9]{1,2}$");

						$campo_fecha_deposito.val(formated);
						if (formated.match(patron) ){
							var valida_fecha=mayor($campo_fecha_deposito.val(),mostrarFecha());
							//alert($campo_fecha_deposito.val());
							if (valida_fecha==true){
								jAlert("Fecha no valida",'! Atencion');
								$campo_fecha_deposito.val(mostrarFecha());
							}else{
								$campo_fecha_deposito.DatePickerHide();
							}
						};
					}
				});
				
				
				
				$campo_fecha.val(mostrarFecha());
				$campo_fecha.css({'background' : '#F0F0F0'});
				
				/*
				$campo_fecha.click(function (s){
					var a=$('div.datepicker');
					a.css({'z-index':100,});
				});
				
				
				$campo_fecha.DatePicker({
					format:'Y-m-d',
					date: $campo_fecha.val(),
					current: $campo_fecha.val(),
					starts: 1,
					position: 'bottom',
					locale: {
						days: ['Domingo', 'Lunes', 'Martes', 'Miércoles', 'Jueves', 'Viernes', 'Sábado','Domingo'],
						daysShort: ['Dom', 'Lun', 'Mar', 'Mir', 'Jue', 'Vir', 'Sab','Dom'],
						daysMin: ['Do', 'Lu', 'Ma', 'Mi', 'Ju', 'Vi', 'Sa','Do'],
						months: ['Enero', 'Febrero', 'Marzo', 'Abril', 'Mayo','Junio', 'Julio', 'Agosto', 'Septiembre','Octubre', 'Noviembre', 'Diciembre'],
						monthsShort: ['Ene', 'Feb', 'Mar', 'Abr','May', 'Jun', 'Jul', 'Ago','Sep', 'Oct', 'Nov', 'Dic'],
						weekMin: 'se'
					},
					onChange: function(formated, dates){
						var patron = new RegExp("^[0-9]{4}-[0-9]{1,2}-[0-9]{1,2}$");
						$campo_fecha.val(formated);
						if (formated.match(patron) ){
							//alert($campo_fecha.val());
							var valida_fecha=mayor($campo_fecha.val(),mostrarFecha());
							
							if (valida_fecha==true){
								jAlert("fecha no valida",'! Atencion');
								$campo_fecha.val(mostrarFecha());
							}else{
								$campo_fecha.DatePickerHide();
							}
						};
					}
				});
				*/
				
				
				var verificar_saldo=function(saldo,cantidad){
					if(saldo >= cantidad){
						return true;
					}
					return false;
				}
				var corroborar_datos = function(denominacion,factor){
					if( denominacion == 'M.N.')
						denominacion = 'false';
					else
						denominacion = 'true';
						
					//var a = $select_moneda.val();
					var a;
					var moneda = $select_moneda.val();
					if(parseInt(moneda)==1){
						a='false';
					}else{
						a='true'
					}
					
					
					
					if(a == denominacion){
						return $monto_anticipo;
					}else{
						if( a== 'false'){
							var s = parseFloat($monto_anticipo / factor);
							return s;
						}else{
							var s = parseFloat($monto_anticipo * factor);
							return s;
						}
					}
				}
				
				
				verificar_precios=function(saldo,denominacion,factor){
					factor = parseFloat(factor);
					$monto_anticipo = parseFloat($monto_anticipo);
					saldo = parseFloat(quitar_comas(saldo));
					if( denominacion == 'M.N.')
						denominacion = 'false';
					else
						denominacion = 'true';
						
					//var a = $select_moneda.val();
					
					var a;
					var moneda = $select_moneda.val();
					if(parseInt(moneda)==1){
						a='false';
					}else{
						a='true'
					}
					
					
					if(a == denominacion){
						if($monto_anticipo > saldo)
							return true;
						return false;
					}else{
						if( a == 'false'){
							var s = parseFloat($monto_anticipo / factor);
							if(s > saldo)
								return true;
							return false;
						}else{
							var s = parseFloat($monto_anticipo * factor);
							if( s > saldo)
								return true;
							return false;
						}
					}
				}
				
				
				
				var regresa_monto = function(saldo,denominacion,factor){
					var tip_cam = parseFloat(factor);
					//alert("Factor: "+factor);
					$monto_anticipo = parseFloat($monto_anticipo);
					saldo = parseFloat(quitar_comas(saldo));
					if( denominacion == 'M.N.')
						denominacion = 'false';
					else
						denominacion = 'true';
					
					
					var a;
					var moneda = $select_moneda.val();
					if(parseInt(moneda)==1){
						a='false';
					}else{
						a='true'
					}
					
					if(a == 'false'){
						if( denominacion == 'false'){
							return saldo;
						}else{
							var s = parseFloat(saldo * tip_cam);
							return s;
						}
					}else{
						if( denominacion == 'true'){
							return saldo;
						}else{
							var s = parseFloat(saldo / tip_cam);
							return s;
						}
					}
				}
				
				
				
				
				var quitar_monto = function(saldo,denominacion,factor){
					var tip_cam = parseFloat(factor);
					$monto_anticipo = parseFloat($monto_anticipo);
					saldo = parseFloat(quitar_comas(saldo));
					if( denominacion == 'M.N.')
						denominacion = 'false';
					else
						denominacion = 'true';
					
					//var a = $select_moneda.val();
					var a;
					var moneda = $select_moneda.val();
					if(parseInt(moneda)==1){
						a='false';
					}else{
						a='true'
					}
					
					if(denominacion == 'false'){
						if( a == 'false'){
							return saldo;
						}else{
							var s = parseFloat(saldo * tip_cam);
							return s;
						}
					}else{
						if( a == 'true'){
							return saldo;
						}else{
							var s = parseFloat(saldo / tip_cam);
							return s;
						}
					}
				}
				
				
				var verificar_monto = function(){
					$monto_anticipo = parseFloat($monto_anticipo);
					if($monto_anticipo > 0)
						return true;
					return false;
				}
				
				var iterar_facturas_input = function(){
					$('tbody.contenido_entrada tr',$contenido2).find('input[name=saldar]').click(function(event){
						if(event.currentTarget === this){
						var inicio = $(this).val();
						var precio_tope = parseFloat(quitar_comas($(this).parent().parent().find('td:eq(4)').html()));
						var facturado_en = $(this).parent().parent().find('td:eq(6)').html();
						var cambio_ver = $(this).parent().parent().find('input[name=tipocamb]').val();
						$saldo = regresa_monto(quitar_comas($(this).parent().find('span').html()),facturado_en,cambio_ver);
						$(this).blur(function(event2){
							
							if(event2.currentTarget === this){
								$monto_anticipo = parseFloat(parseFloat($monto_anticipo) + parseFloat($saldo));
								var patron = /^\d+(\.?\d+)?$/;
								if(patron.test($(this).val())){
									$saldo1 = quitar_monto($(this).val(),facturado_en,cambio_ver);
									//alert("Saldo1: "+$saldo1);
									if(parseFloat($saldo1) > precio_tope){
										$monto_anticipo = parseFloat(parseFloat($monto_anticipo) - parseFloat($saldo));
										//alert("Monto Anticipo: "+$monto_anticipo);
										$textarea_monto_antipo.val($(this).agregar_comas($monto_anticipo.toFixed(2)));
										$(this).val(inicio);
										event2.stopImmediatePropagation();
									}
									if(verificar_precios($saldo1,facturado_en,cambio_ver)){	
										$(this).val($(this).agregar_comas($saldo1.toFixed(2)));
										//$(this).val($saldo1);
										$(this).parent().find('span').remove();
										$(this).parent().append('<span style="display:none">'+$saldo1+'</span>');
										//$saldo = regresa_monto(quitar_comas($(this).val()),facturado_en,cambio_ver);
										$saldo = regresa_monto($saldo1,facturado_en,cambio_ver);
										//alert("Monto anticipo 1: "+$monto_anticipo+" - "+$saldo);
										$monto_anticipo = parseFloat(parseFloat($monto_anticipo) - parseFloat($saldo));
										$textarea_monto_antipo.val($(this).agregar_comas($monto_anticipo.toFixed(2)));
										event2.stopImmediatePropagation();
									}else{
										$monto_anticipo = parseFloat(parseFloat($monto_anticipo) - parseFloat($saldo));
										$(this).val(inicio);
										$textarea_monto_antipo.val($(this).agregar_comas($monto_anticipo.toFixed(2)));
										event2.stopImmediatePropagation();
									}
								}else{
									$monto_anticipo = parseFloat(parseFloat($monto_anticipo) - parseFloat($saldo));
									$textarea_monto_antipo.val($(this).agregar_comas($monto_anticipo.toFixed(2)));
									$(this).val(inicio);
									event2.stopImmediatePropagation();
								}
								recalcular_monto_gastado();//recalcular monto gastado
							}
							});
						}
					});
				}
				
				//retorna numero de transaccion y cantidad seleccionada de la lista de anticipos, si no hay seleccionado retorna 0
				var trans_cant_anticipo_seleccionado = function(tipo){
					var dato_retorno="";
					var haydato=0;
					$('#tablagrid tr', $contenido2).each(function (index) {
						if(parseInt($(this).find('span.seleccionado').html())==1) {
							if(parseInt(tipo)==1){
								//retorna numero de transaccion del anticipo seleccionado
								dato_retorno=$(this).find('span.num_trans').html();
								haydato=1;
							}else{
								//retorna cantidad del anticipo seleccionado
								dato_retorno=quitar_comas($(this).find('span.cant2').html());
								haydato=1;
							}
						}
					});
					if(parseInt(haydato)!=1){
						dato_retorno="0";
					}
					//alert("anticipo"+dato_retorno);
					return dato_retorno;
				}
				
				
				//recalcula monto gastado
				var recalcular_monto_gastado = function(){
					actualizar_monto_anticipo();//actualiza monto anticipo en grid
					$('#tablagrid tr', $contenido2).each(function (index) {
						if(parseInt($(this).find('span.seleccionado').html())==1) {
							var monto_anticipo_gastado=(parseFloat(quitar_comas($monto_pago.val()))-parseFloat(trans_cant_anticipo_seleccionado(0))).toFixed(2);
							$campo_monto_ant_selecc.attr({ 'value' :''});
							$campo_monto_ant_selecc.attr({ 'value' : monto_anticipo_gastado});
						}
					});
				}
				/*
				var actualizar_tipo_cambio_dos= function($valor){
					$valor.parent().parent().find('input[value=<<>>]').click(function(event){
						if (event.target == this ){ 
							event.preventDefault();
							var $cambia = $valor.parent().parent().find('input[name=tipocamb]');
							$(this).modalPanel_for_cartera_tipo_cambio();
							$cambio = $('#dialogo-cartera-tipo-cambio-window');	
							$cambio.append($('div.tipo_cambio_alterado').find('table.formaBusqueda').clone());
							var $cam = $cambio.find('input[name^=data[tipo_cambio_alterado]]'); 
							$cam.focus();
							$cambio.find('input[value=Cancelar]').click(function (){
								$('#dialogo-cartera-tipo-cambio-overlay').remove();
							});
							$cambio.find('input[value=Actualizar]').click(function (event){
								event.preventDefault();
								var patron = /^\d+(\.?\d+)?$/;
								if(patron.test($cam.val())){
									if($cam.val() >= 0){
										$cambia.val($cam.val());										
										$('#dialogo-cartera-tipo-cambio-overlay').remove();
									}else{
										jAlert('El tipo de cambio no es valido','! Atencion');
									}
								}else{
									jAlert('El tipo de cambio no es valido','! Atencion');
								}
							});
						}
					});
				}
				*/
				var iterar_facturas = function(){
					$('tbody.contenido_entrada tr',$contenido2).find('input[name=micheck]').click(function(){
						var facturado_en = $(this).parent().parent().find('td:eq(6)').html();
						var cambio_ver = $(this).parent().parent().find('input[name=tipocamb]').val();
						if(this.checked){
							$(this).parent().parent().find('td:eq(7)').find('span').remove();
							
							$saldo = $(this).parent().parent().find('td:eq(4)').html();
							//alert("Saldo: "+$saldo);
							if($tipo_cambio.val() != ''){
								
								if(verificar_precios($saldo,facturado_en,cambio_ver)){
									$(this).parent().parent().find('input[name=saldar]').val($saldo);
									$(this).parent().parent().find('input[name=saldar]').attr({'disabled':false})
									$(this).parent().parent().find('td:eq(5)').append('<span style="display:none;">'+$saldo+'</span>');
									$saldo = regresa_monto(quitar_comas($saldo),facturado_en,cambio_ver);
									
									//alert("Saldo: "+$saldo);
									$monto_anticipo = parseFloat(parseFloat($monto_anticipo) - parseFloat($saldo));
									$textarea_monto_antipo.val($(this).agregar_comas($monto_anticipo.toFixed(2)));
									
									actualizar_monto_anticipo();//actualiza monto anticipo en grid
									recalcular_monto_gastado();//recalcular monto gastado
									
									$tipo_cambio.attr("readonly", true);
									$tipo_cambio.css({'background' : '#DDDDDD'});
								}else{
									if(verificar_monto()){
										var valor_correcto = corroborar_datos(facturado_en,cambio_ver);
										//alert("Valor correcto:: "+valor_correcto);
										$(this).parent().parent().find('input[name=saldar]').val($(this).agregar_comas(valor_correcto.toFixed(2)));
										$(this).parent().parent().find('input[name=saldar]').attr({'disabled':false});
										$(this).parent().parent().find('td:eq(5)').append('<span style="display:none;">'+valor_correcto+'</span>');
										$monto_anticipo = parseFloat(parseFloat($monto_anticipo) - parseFloat($monto_anticipo));
										$textarea_monto_antipo.val($(this).agregar_comas($monto_anticipo.toFixed(2)));
										
										actualizar_monto_anticipo();//actualiza monto anticipo en grid
										recalcular_monto_gastado();//recalcular monto gastado
										
										$tipo_cambio.attr("readonly", true);
										$tipo_cambio.css({'background' : '#DDDDDD'});
									}else{
										this.checked = false;
										/*
										var cambio_tipo = "<span class='button_for_ie'><input align='center' class='borde_oculto' type='botton' value='<<>>' style='font-size: 11px; background-color: rgb(255, 255, 255);  padding: 1px; width: 35px;'></span>"
										$(this).parent().parent().find('td:eq(7)').append(cambio_tipo);
										actualizar_tipo_cambio_dos($(this));
										*/
										$tipo_cambio.attr("readonly", false);
										$tipo_cambio.css({'background' : '#ffffff'});
									}
								}
								
							}else{
								this.checked = false;
								jAlert('El tipo de cambio no es valido','! Atencion');
							}
							
						}else{
							//var cambio_tipo = "<span class='button_for_ie'><input align='center' class='borde_oculto' type='botton' value='<<>>' style='font-size: 11px; background-color: rgb(255, 255, 255);  padding: 1px; width: 35px;'></span>"
							//$(this).parent().parent().find('td:eq(7)').append(cambio_tipo);
							
							$saldo = quitar_comas($(this).parent().parent().find('td:eq(5)').find('span').html());
							//alert($saldo);
							$saldo = regresa_monto($saldo,facturado_en,cambio_ver);
							$monto_anticipo = parseFloat(parseFloat($monto_anticipo) + parseFloat($saldo));
							$textarea_monto_antipo.val($(this).agregar_comas($monto_anticipo.toFixed(2)));
							$(this).parent().parent().find('input[name=saldar]').val('0.00');
							$(this).parent().parent().find('td:eq(5)').find('span').remove();
							$(this).parent().parent().find('input[name=saldar]').attr({'disabled':true});
							//actualizar_tipo_cambio_dos($(this));
							actualizar_monto_anticipo();//actualiza monto anticipo en grid
							recalcular_monto_gastado();//recalcular monto gastado
							
							$tipo_cambio.attr("readonly", false);
							$tipo_cambio.css({'background' : '#ffffff'});
						}
					
						if(obtener_folios()){
							$monto_pago.attr({'disabled':true});
							$select_moneda.attr({'disabled':true});
							$contenido2.find('tbody tr:eq(6)').find('td:eq(3)').find('div').find('span').remove();
							
							$campo_pagosxguardar.attr({ 'value' : 1});//existen pagos por guardar
						}else{
							$campo_pagosxguardar.attr({ 'value' : 0});//no hay pagos por guardar
							$monto_pago.attr({'disabled':false});
							$select_moneda.attr({'disabled':false});
							var a = $select_moneda.val();
							calcular_monto(a);
							$contenido2.find('tbody tr:eq(6)').find('td:eq(3)').find('div').find('span').remove();
							/*
							var cambia_tipo_cambio="<span class='button_for_ie'><input align='center' class='borde_oculto' type='botton' value='<<>>' style='font-size: 11px; background-color: rgb(255, 255, 255);  padding: 1px; width: 35px;'></span>";
							$contenido2.find('tbody tr:eq(6)').find('td:eq(3)').find('div').append(cambia_tipo_cambio);
							tipo_cambio_global();
							*/
						}
					});
					//comentado temporalmente
					//actualizar_tipo_cambio_uno();
				}
				
				var obtener_deuda= function(){
					$('tbody.contenido_entrada tr',$contenido2).find('input[name=micheck]').each(function(){
						var facturado_en = $(this).parent().parent().find('td:eq(6)').html();
						var dueda = quitar_comas($(this).parent().parent().find('td:eq(4)').html());
						if(facturado_en=='M.N.'){
							$deuda_pesos = parseFloat($deuda_pesos) + parseFloat(dueda);
						}else{
							$deuda_pesos =parseFloat($deuda_pesos) + parseFloat(dueda*$tipo_cambio.val());
						}
						
						if(facturado_en=='M.N.'){
							$deuda_usd = parseFloat($deuda_usd) + parseFloat(dueda/$tipo_cambio.val())
						}else{
							$deuda_usd = parseFloat($deuda_usd) + parseFloat(dueda);
						}
					});
					$deuda_usd = parseFloat($deuda_usd).toFixed(2);
					$deuda_pesos = parseFloat($deuda_pesos).toFixed(2);
					
				}
				
				var calcular_monto = function(evaluar){
					var psos;
					var usd;
					usd=0;
					psos=0;
					
					//var psos = parseFloat($sum_anticipos_mn.val());
					//var usd = parseFloat($sum_anticipos_usd.val());
					//alert(psos);
					//alert(usd);
					
					if(parseInt(evaluar)==1){
						var total;
						total = parseFloat(parseFloat(quitar_comas($monto_pago.val())) +  psos + (usd*$tipo_cambio.val())).toFixed(2);
						$textarea_monto_antipo.val($(this).agregar_comas(total));
						$monto_anticipo = quitar_comas($textarea_monto_antipo.val());
						//alert("Monto anticipo pesos: "+psos);
						//alert("Monto anticipo: "+$monto_anticipo);
					}else{
						var total;
						if(psos==0)
							total = parseFloat(parseFloat(quitar_comas($monto_pago.val())) + usd).toFixed(2);
						else
							total = parseFloat(parseFloat(quitar_comas($monto_pago.val())) +  usd + (psos/$tipo_cambio.val())).toFixed(2);
						$textarea_monto_antipo.val($(this).agregar_comas(total));
						$monto_anticipo = quitar_comas($textarea_monto_antipo.val());
					}
				}
				
				var obtener_folios= function(){
					var folios=false;
					$('tbody.contenido_entrada tr',$contenido2).find('input[name=micheck]').each(function(){
						if(this.checked){
							folios=true;
						}
					});
					return folios;
				}
				
				//elimina cero al hacer clic sobre el campo
				$monto_pago.focus(function(e){
					if(parseFloat($monto_pago.val())<1){
						$monto_pago.val('');
					}
				});	
				
				$monto_pago.keypress(function(e){
					// Permitir  numeros, borrar, suprimir, TAB, puntos
					if (e.which == 8 || e.which == 46 || e.which==13 || e.which == 0 || (e.which >= 48 && e.which <= 57 )) {
						if(e.which == 8 || e.which == 46 ||(e.which >= 48 && e.which <= 57 )){
							$('#tablagrid').find('span.seleccionado').text(0);
							$select_moneda.attr({'disabled':false});
							$campo_monto_ant_selecc.val(0);
						}
						return true;
					}else {
						return false;
					}
					
				}); 
				
				$monto_pago.blur(function(){
					if($monto_pago.val()=="" || parseFloat($monto_pago.val())==0){
						$monto_pago.val("0.00");//si el campo esta en blanco, pone cero
					}
					var a = $select_moneda.val();
					$monto_pago.val(quitar_comas($monto_pago.val()));
					var patron = /^\d+(\.?\d+)?$/;
					if(patron.test($monto_pago.val())){
						$monto_pago.val($(this).agregar_comas($monto_pago.val()));
						calcular_monto(a);
						if(obtener_folios()){
							$monto_pago.attr({'disabled':true});
						}else{
							$monto_pago.attr({'disabled':false});
						}
					}else{
						jAlert('El monto ingresado no es valido','! Atencion');
						// $monto_pago.focus();
					}
				});
				
				var actualizar_tipo_cambio_grid = function(tipocambio_new){
					$('tbody.contenido_entrada tr',$contenido2).find('input[name=tipocamb]').each(function(){
						$(this).val(tipocambio_new);
					});
				}
				
				
				var tipo_cambio_global = function($cam){
					/*
					$contenido2.find('tbody tr:eq(6)').find('td:eq(3)').find('div').find('input[value=<<>>]').click(function(event){
							event.preventDefault();
							$(this).modalPanel_for_cartera_tipo_cambio();
							$cambio = $('#dialogo-cartera-tipo-cambio-window');	
							$cambio.append($('div.tipo_cambio_alterado').find('table.formaBusqueda').clone());
							var $cam = $cambio.find('input[name^=data[tipo_cambio_alterado]]'); 
							$cam.focus();
							$cambio.find('input[value=Cancelar]').click(function (){
								$('#dialogo-cartera-tipo-cambio-overlay').remove();
							});
							$cambio.find('input[value=Actualizar]').click(function (event){
								event.preventDefault();
							*/
								$cam.focus();
								var patron = /^\d+(\.?\d+)?$/;
								if(patron.test($cam.val())){
									if($cam.val() >= 0){
										$tipo_cambio.val($cam.val());
										var a = $select_moneda.val();
										calcular_monto(a);
										obtener_deuda();
										actualizar_tipo_cambio_grid($cam.val());
										//$('#dialogo-cartera-tipo-cambio-overlay').remove();
									}else{
										jAlert('El tipo de cambio no es valido','! Atencion');
									}
								}else{
									jAlert('El tipo de cambio no es valido','! Atencion');
								}
								
								
							/*
							});
							
						});
						*/
				}
				
				
				
				/*
				var actualizar_tipo_cambio_uno= function(){
					$('tbody.contenido_entrada tr',$contenido2).find('input[value=<<>>]').click(function(event){
						if (event.target == this ){ 
							event.preventDefault();
							var $cambia = $(this).parent().parent().find('input[name=tipocamb]');
							$(this).modalPanel_for_cartera_tipo_cambio();
							$cambio = $('#dialogo-cartera-tipo-cambio-window');	
							$cambio.append($('div.tipo_cambio_alterado').find('table.formaBusqueda').clone());
							var $cam = $cambio.find('input[name^=data[tipo_cambio_alterado]]'); 
							$cam.focus();
							$cambio.find('input[value=Cancelar]').click(function (){
								$('#dialogo-cartera-tipo-cambio-overlay').remove();
							});
							$cambio.find('input[value=Actualizar]').click(function (event){
								event.preventDefault();
								var patron = /^\d+(\.?\d+)?$/;
								if(patron.test($cam.val())){
									if($cam.val() >= 0){
										$cambia.val($cam.val());										
										$('#dialogo-cartera-tipo-cambio-overlay').remove();
									}else{
										jAlert('El tipo de cambio no es valido','! Atencion');
									}
								}else{
									jAlert('El tipo de cambio no es valido','! Atencion');
								}
							});
						}
					});
				}
				*/
				
				
				//recalcula monto de anticipo
				var actualizar_monto_anticipo= function(){
					$('#tablagrid tr', $contenido2).each(function (index) {
						if(parseInt($(this).find('span.seleccionado').html())==1) {
							$(this).find('span.cant2').text($textarea_monto_antipo.val());
						}
					});
				}
                //termina funciones
                
                
				
				
				$select_moneda.attr({'disabled':false});
				$campo_monto_ant_selecc.attr({ 'value' : 0});
				$campo_pagosxguardar.attr({ 'value' : 0});
				$monto_pago.attr({'disabled':false});
				
				
				/*------------------------------------------------------------------------------------------------------------*/
				//movimiento pago
				/*------------------------------------------------------------------------------------------------------------*/
				
				if(tipo_movimiento==1){
					
					//definir posicion de la ventana
					$('#forma-carteras-window').css({"margin-left": -350,"margin-top": -230});
					//definir alto de la ventana
					$('#forma-carteras-window').find('.carteras_div_one').css({'height':'510px'});
					
					//ancho de la ventana
					$('#forma-carteras-window').find('.carteras_div_one').css({'width':'913px'});
					$('#forma-carteras-window').find('.carteras_div_two').css({'width':'913px'});
					$('#forma-carteras-window').find('#div_two').find('#carteras_div_titulo_ventana').css({'width':'873px'});
					$('#forma-carteras-window').find('#div_two').find('#carteras_div_titulo_ventana').find('strong').text('');
					$('#forma-carteras-window').find('#div_two').find('#carteras_div_titulo_ventana').find('strong').text('Registro de pagos');
					$('#forma-carteras-window').find('.carteras_div_three').css({'width':'903px'});
					//posicion de los botones
					$('#forma-carteras-window').find('#botones').css({'width':'886px'});
					
					$contenido2.find('tr.uno').find('td.oculta_anticipo').show();
					$contenido2.find('tr.uno').find('a[href*=busca_cliente]').hide();
					
					
					
					$('div.datepicker').blur(function(e){
						//alert("eres covarde");
					});
					
					//$contenido2.find('table.formaBusqueda').attr({'width':1000});
					//$contenido2.css({'margin-left':-500});
					$contenido2.find('tbody tr').show();
					$contenido2.find('tr.transaccion').remove();
					$contenido2.find('tr.facturas').remove();
					//$contenido2.css({'margin-top':-230});
					$contenido2.find('tr.uno').find('tbody tr.cancelar').remove();
					$contenido2.find('tr.uno').find('tbody tr.cancelar_pagos').remove();
					$contenido2.find('tr.uno').find('tbody tr.cheque').hide();
					$contenido2.find('tr.uno').find('tbody tr.transferencia').hide();
					$contenido2.find('.observaciones_2').hide();
					$contenido2.find('tr.anticipo').remove();
					$contenido2.find('tr.registros').hide();
					$contenido2.find('.ficha_deposito').show();
					$contenido2.find('.anticipos_txarea').show();
					$contenido2.find('.pago_a').show();
					$contenido2.find('.fecha_nc').show();
					$submit_registrar_pago.show();
					$submit_registrar_cancelacion.hide();
					$registra_anticipo.hide();
					
					//$contenido2.find('tr.salida').find('input[value=Registrar Cancelacion]').remove();
					//$contenido2.find('tr.salida').find('input[value=Registrar Anticipo]').remove();
					//$contenido2.find('tr.salida').find('input[value=Registrar Nota de Credito]').remove();
												
					$contenido2.find('tr.uno').find('tbody tr.tarjeta').hide();
					$monto_anticipo=0;
					$monto_pago.attr({ 'value' : 0.00});
					
					/*
					var cambia_tipo_cambio="<span class='button_for_ie'><input align='center' type='botton' value='<<>>' style='font-size: 11px; background-color: rgb(255, 255, 255);  padding: 1px; width: 40px;'></span>";
					$contenido2.find('tr.uno').find('tbody tr.pago').find('td:eq(3)').find('div').append(cambia_tipo_cambio);
					var registra_pago='<input align="center" type="botton" value="Registrar Pago" style="font-size: 11px; background-color: rgb(255, 255, 255); text-align:center; border: 2px solid rgb(204, 204, 204); padding: 1px; width: 100px; background:none repeat scroll 0 0 transparent; color:blue; cursor: pointer; font-family:Tahoma;">';
					$contenido2.find('tr.salida').find('td:eq(1)').append(registra_pago);
					*/
					$contenido2.find('tr.uno').find('tbody tr.moneda').find('textarea[name=observaciones]').show();
					
					
					var get_cuentas = document.location.protocol + '//' + document.location.host + '/' + controller + '/getCuentas.json';
					$arreglo = {'id_moneda': $select_moneda.val(),
								'iu':$('#lienzo_recalculable').find('input[name=iu]').val()
								};
					var cs_cuentashtml = '<option value="0">0</option>';
					$.post(get_cuentas,$arreglo,function(entry){
						$campo_cuenta_deposito.children().remove();
						$.each(entry['Cuentas'],function(entryIndex,cuenta){
							cs_cuentashtml += '<option value="' + cuenta['id'] + '"  >' + cuenta['titulo'] + '</option>';
						});
						$campo_cuenta_deposito.append(cs_cuentashtml);
					});
						
					
												
					//****obtiene numero de cuenta****//
					$select_moneda.change(function(){
						//var select_cuenta_json4=document.location.protocol + '//' + document.location.host + '/' + controller + '/' + 'obtener_numero_bancos_friser' + '/' + $select_moneda.val()  +'/out.json';
						var select_cuenta_json4 = document.location.protocol + '//' + document.location.host + '/' + controller + '/obtener_bancos_moneda_kemikal.json';
						$arreglo = {'id_moneda': $select_moneda.val(),
									'iu':$('#lienzo_recalculable').find('input[name=iu]').val()
									};
						
						$.post(select_cuenta_json4,$arreglo,function(entry){
							var trama_hmtl = '<option value="0" >[--    --]</option>';
							$campo_banco_deposito.children().remove();
							$.each(entry['Bancos_moneda'],function(entryIndex,bank){
								trama_hmtl += '<option value="' + bank['id'] + '"  >' + bank['titulo'] + '</option>';
							});
							$campo_banco_deposito.append(trama_hmtl);
						});
						
						//vaciar select de centas de deposito
						$campo_cuenta_deposito.children().remove();
						var cuentashtml = '<option value="0">0</option>';
						$campo_cuenta_deposito.append(cuentashtml);
					});
					
							
					$campo_banco_deposito.change(function(){
						//var select_cuenta_json4=document.location.protocol + '//' + document.location.host + '/' + controller + '/' + 'obtener_numero_de_cuenta' + '/' + $campo_banco_frio.val() +'/' + $select_moneda.val()  +'/out.json';
						var select_cuenta_json4 = document.location.protocol + '//' + document.location.host + '/' + controller + '/obtener_numero_de_cuenta.json';
						$arreglo = {    'id_banco': $campo_banco_deposito.val(),
										'id_moneda': $select_moneda.val()
									};
									
						$.post(select_cuenta_json4,$arreglo,function(entry){
							var trama_hmtl='';
							$campo_cuenta_deposito.children().remove();
							$.each(entry['Cuentas'],function(entryIndex,cuenta){
								trama_hmtl += '<option value="' + cuenta['id'] + '"  >' + cuenta['titulo'] + '</option>';
							});
							$campo_cuenta_deposito.append(trama_hmtl);
						});
					});
												
												
												
					//var input_busqueda_json3 = document.location.protocol + '//' + document.location.host + '/' + controller + '/' + 'obtener_anticipos' + '/' + $identificador_cliente.val()  +'/out.json';
					var input_busqueda_json3 = document.location.protocol + '//' + document.location.host + '/' + controller + '/obtener_anticipos.json';
					$arreglo = {'id_cliente': $identificador_cliente.val()};
					
					
					$.post(input_busqueda_json3,$arreglo,function(data){
					//$.getJSON(input_busqueda_json3,function(data){
						
						$sum_anticipos_mn.val(data['suma_mn'][0]['suma_mn']);
						$sum_anticipos_usd.val(data['suma_usd'][0]['suma_usd']);
						//$textarea_anticipo.val(data['suma_anticipos']);
						//var psos = parseFloat($textarea_anticipo.val().split('\n')[0].split(' ')[3].split(',').join(''));
						//var usd = parseFloat($textarea_anticipo.val().split('\n')[1].split(' ')[3].split(',').join(''));
						var psos = parseFloat($sum_anticipos_mn.val());
						var usd = parseFloat($sum_anticipos_usd.val());
						var total;
						//alert(psos);
						//alert(usd);
						
						//alert(data['anticipo']);
						
						
						//-----------------------------------------------------------------------
						//aqui se crea el listado de anticipos
						$('tbody.contenido_anticipos',$contenido2).children().remove();
						var contenido_a="";
						contenido_a +="<tr id='filaanticipos'>";
						contenido_a +="<td colspan='2' width='5'>";
						contenido_a +="<table border=0 id='tablagrid' width='100%'>";
						$.each(data['anticipos'],function(entryIndex, ant){
							contenido_a +="<tr id='gridanticipos'>";
							contenido_a +="<td style='font-size: 11px;'><span class='seleccionado' style='display:none;'>0</span></td>";
							contenido_a +="<td style='font-size: 11px;'><span class='num_trans'>"+ ant['numero_transaccion'] +"</span></td>";
							contenido_a +="<td style='font-size: 11px;'><span class='denom2'>"+ ant['denominacion'] +"</span></td>";
							contenido_a +="<td style='font-size: 11px; text-align:right;'><span class='cant2'>"+$(this).agregar_comas(parseFloat(ant['anticipo_actual']).toFixed(2))+"</span></td>";
							contenido_a +="</tr>";
						});
						contenido_a +="</table>";
						contenido_a +="</td>";
						contenido_a +="<tr id='filaanticipos'>";
						$('tbody.contenido_anticipos',$contenido2).append(contenido_a);
						
						
						//muestra el grid del listado de facturas
						$contenido2.find('tbody tr.registros').show();
						
						
						//$('#filaanticipos').css('display','none');
						$('#tablagrid').find('tr').click(function() {
							//seleccionado0();//restaurar seleccionado a cero
							//alert("Haz hecho clien en la fila");
							if(parseFloat(quitar_comas($textarea_monto_antipo.val()))>0){
								jAlert('Hay un Monto Total  de $'+$textarea_monto_antipo.val()+' '+$select_moneda.find('option:selected').html()+' por asignar','Atencion!');
							}else{
								//alert("Pendientes por guardar: "+$campo_pagosxguardar.val());
								
								if(parseInt($campo_pagosxguardar.val())!=1){
									$campo_monto_ant_selecc.attr({ 'value' : 0});
									var denominacion_anticipo = $(this).find('span.denom2').html();
									var valor = $(this).find('span.cant2').html();
									var id_ant = $(this).find('span.id_ant').html();
									$campo_monto_ant_selecc.attr({ 'value' : valor});
									$('#tablagrid').find('span.seleccionado').text(0);
									if(parseFloat(quitar_comas(valor))>0){
										$(this).find('td').css({'background-color':'#DDECFF'});
										if(denominacion_anticipo=='M.N.'){
											$select_moneda.find('option[value=1]').attr('selected','selected');
										}else{
											$select_moneda.find('option[value=2]').attr('selected','selected');
										}
										$(this).find('span.seleccionado').text(1);
										$monto_pago.val(valor);
										$monto_pago.trigger('blur');
									}
								}else{
									jAlert('Hay pagos pendientes por guardar.','Atencion!');
								}
							}
																
						});
						
						
						
						$('#tablagrid tr:odd').hover(function () {
							if(parseInt($(this).find('span.seleccionado').html())==1) {
								$(this).find('td').css({'background-color':'#DDECFF'});
							}else{
								$(this).find('td').css({'background-color':'#FBD850'});
							}
						}, function() {
							if(parseInt($(this).find('span.seleccionado').html())==1) {
								$(this).find('td').css({'background-color':'#DDECFF'});
							}else{
								$(this).find('td').css({'background-color':'#FFFFFF'});
							}
						});
						
						$('#tablagrid tr:even').hover(function () {
							if(parseInt($(this).find('span.seleccionado').html())==1) {
								$(this).find('td').css({'background-color':'#DDECFF'});
							}else{
								$(this).find('td').css({'background-color':'#FBD850'});
							}
						}, function() {
							if(parseInt($(this).find('span.seleccionado').html())==1) {
								$(this).find('td').css({'background-color':'#DDECFF'});
							}else{
								$(this).find('td').css({'background-color':'#FFFFFF'});
							}
						});
						
						//-----------------------------------------------------------------------
						
						total = parseFloat(parseFloat($monto_pago.val()) +  psos + (usd*$tipo_cambio.val()));
						//$monto_anticipo = total;
						$textarea_monto_antipo.val($(this).agregar_comas(0.00));
					});
					
					
					
					//validar campo tipo_cambio, solo acepte numeros y punto
					$tipo_cambio.keypress(function(e){
						// Permitir  numeros, borrar, suprimir, TAB, puntos, comas
						if (e.which == 8 || e.which == 46 || e.which==13 || e.which == 0 || (e.which >= 48 && e.which <= 57 )) {
							return true;
						}else {
							return false;
						}
					});

					
					$tipo_cambio.blur(function(){
						tipo_cambio_global($tipo_cambio);
					});
					
					
					/*
					$('input[value$=Registrar Pago]',$contenido2).hover(function () {
						$(this).css({'background-color':'#FBD850'});
					}, function() {
						$(this).css({'background-color':'#FFFFFF'});
					});
					*/
					
					
					//alert("estoy aqui adentro");
					//var input_busqueda_json = document.location.protocol + '//' + document.location.host + '/' + controller + '/' + 'obtener_facturas' + '/' + $identificador_cliente.val()  +'/out.json';
					var input_busqueda_json = document.location.protocol + '//' + document.location.host + '/' + controller + '/obtener_facturas.json';
					$arreglo = {'id_cliente': $identificador_cliente.val()};
					
					//$.getJSON(input_busqueda_json,function(data){
					$.post(input_busqueda_json,$arreglo,function(data){
						$('tbody.contenido_entrada',$contenido2).children().remove();
						
						$.each(data['Facturas'],function(entryIndex, entry){
							$contenido2.find('tbody tr.registros').show();
							//$contenido2.css({'margin-top':-300});
							var contenido_c="";
							
							contenido_c += "<tr><td class='grid' style='font-size: 11px;  border:1px solid #C1DAD7;' width='10'><input type='checkbox' name='micheck' value='check'></td>";
							//contenido_c += "<td class='grid1' style='font-size: 11px;  border:1px solid #C1DAD7 ;' width='60'>" + entry['numero_factura'] + "<span style='display:none'>"+entry['folio']+"</span></td>";
							contenido_c += "<td class='grid1' style='font-size: 11px;  border:1px solid #C1DAD7 ;' width='60'>" + entry['numero_factura'] + "</td>";
							//alert(entry['facturar_en']);
							/*
							if(entry['facturar_en']){
								contenido_c += "<td class='grid' style='font-size: 11px;  border:1px solid #C1DAD7;' width='75' align='right'>"+ $(this).agregar_comas(entry['total_usd'])+"</td>";
								contenido_c += "<td class='grid' style='font-size: 11px;  border:1px solid #C1DAD7;' width='85' align='right'>"+ $(this).agregar_comas(entry['accesor_saldo'])+"</td>";
								var saldo= parseFloat(entry['total_usd']) - parseFloat(entry['accesor_saldo']);
								contenido_c += "<td class='grid2' style='font-size: 11px;  border:1px solid #C1DAD7;' width='75' align='right'>"+$(this).agregar_comas(saldo.toFixed(2))+"</td>";
							}else{*/
								contenido_c += "<td class='grid2' style='font-size: 11px;  border:1px solid #C1DAD7;' width='75' align='right'>"+$(this).agregar_comas(entry['monto_factura'])+"</td>";
								contenido_c += "<td class='grid2' style='font-size: 11px;  border:1px solid #C1DAD7;' width='85' align='right'>"+$(this).agregar_comas(entry['monto_pagado'])+"</td>";
								var saldo = parseFloat(entry['monto_factura'])- parseFloat(entry['monto_pagado']);
								contenido_c += "<td class='grid2' style='font-size: 11px;  border:1px solid #C1DAD7;' width='75' align='right'>"+$(this).agregar_comas(saldo.toFixed(2))+"</td>";
							//}
							
							contenido_c += "<td class='grid' style='font-size: 11px;  border:1px solid #C1DAD7;' width='80'><input type='text' name='saldar' value='0.00' style='width: 80px; background-color:#F6F8FB; text-align:right;' disabled=true' align='right'></td>";
							contenido_c += "<td class='grid1' style='font-size: 11px;  border:1px solid #C1DAD7;' width='70'>" +entry['denominacion_factura']+ "</td>";
							//contenido_c += "<td class='grid1' style='font-size: 11px;  border:1px solid #C1DAD7;' width='125'><input type='text' name='tipocamb' value='"+$tipo_cambio.val()+"' style='width: 80px; background-color:#F6F8FB;' align='right' disabled=true><span class='button_for_ie'><input align='center' class='borde_oculto' type='botton' value='<<>>' style='font-size: 11px; background-color: rgb(255, 255, 255);  padding: 1px; width: 35px;'></span></td>";
							contenido_c += "<td class='grid1' style='font-size: 11px;  border:1px solid #C1DAD7;' width='125'><input type='text' name='tipocamb' value='"+$tipo_cambio.val()+"' style='width: 80px; background-color:#F6F8FB;' align='right' disabled=true></td>";
							contenido_c += "<td class='grid1' style='font-size: 11px;  border:1px solid #C1DAD7;' width='92'>" +entry['fecha_ultimo_pago']+ "</td>";
							contenido_c += "<td class='grid1' style='font-size: 11px;  border:1px solid #C1DAD7;' width='95'>" +entry['fecha_facturacion']+ "</td>";
							
							$('tbody.contenido_entrada',$contenido2).append(contenido_c);
							
							
						});
						
						/************************************/
						/***********************************/
						/***/	iterar_facturas();		/***/
						/***/	iterar_facturas_input();/***/
						/***/	obtener_deuda();		/***/
						/***********************************/
						/***********************************/
						
						
						
					});
					
					//tipo_cambio_global();
					
					var verifinado_forma_de_pago = function(forma_pago){
						if(forma_pago=='Efectivo'){
							return true;
						}
						if(forma_pago=='Cheque'){
							var patron = /^\d+$/;
							if(patron.test($campo_cheque.val())){
								return true;
							}else{
								jAlert('El numero de cheque no es valido','Atencion!');
								return false;
							}
						}
						if(forma_pago=='Transferencia'){
							var patron = /^\d+$/;
							if(patron.test($campo_referencia.val())){
								return true;
							}else{
								jAlert('La referencia no es valida','Atencion!');
								return false;
							}
						}
						if(forma_pago=='Tarjeta'){
							var patron = /^((67\d{2})|(4\d{3})|(5[1-5]\d{2})|(6011))(-?\s?\d{4}){3}|(3[4,7])\ d{2}-?\s?\d{6}-?\s?\d{5}$/;
							if(patron.test($campo_tarjeta.val())){
								return true;
							}else{
								jAlert('El numero de tarjeta no es valido','Atencion!');
								return false;
							}
						}
					}
					
					
					
					
					
					//$boton_pago.click(function(event){
					//	event.preventDefault();//evita que se ejecuta el submit
					
					$submit_registrar_pago.click(function(event){
							event.preventDefault();//evita que se ejecuta el submit
							if(verifinado_forma_de_pago($select_forma_pago.val())){
								var cadena="";
								var folios="";
								var correc = false;
								var table_envio="";
								var contador=0;
								$('tbody.contenido_entrada tr',$contenido2).find('input[name=micheck]').each(function(){
									if(this.checked){
										correc=true;
										contador++;
									}
								});
								
								if(correc){
									table_envio += "<table><tr><td>";
									table_envio += "<table><thead><tr><th style='background-color:#DCDCDC;border-top: 2px solid rgb(220, 220, 220); border-bottom: 2px solid rgb(220, 220, 220); border-left: 2px solid rgb(220, 220, 220);' width=70>Factura</th><th style='border-top: 2px solid rgb(220, 220, 220); border-bottom: 2px solid rgb(220, 220, 220); background-color:#DCDCDC;' width=15></th><th style='text-align: center ! important; border-top: 2px solid rgb(220, 220, 220); border-bottom: 2px solid rgb(220, 220, 220); border-right: 2px solid rgb(220, 220, 220); background-color:#DCDCDC;' width=100>Cantidad a saldar</th></tr></thead></table>";
									table_envio += '<div style="overflow-x: hidden; overflow-y: auto; width: 100%; height: 150px;">'
									table_envio += "<table><tbody>";
									$('tbody.contenido_entrada tr',$contenido2).find('input[name=micheck]').each(function(){
										if(this.checked){
											$saldo = parseFloat(quitar_comas($(this).parent().parent().find('td:eq(5)').find('input[name=saldar]').val()));
											$factura = $(this).parent().parent().find('td:eq(1)').find('span').html();
											$factura_vista = $(this).parent().parent().find('td:eq(1)').html().split('<span')[0];
											$monto = quitar_comas($(this).parent().parent().find('td:eq(2)').html());
											$pagos = parseFloat(quitar_comas($(this).parent().parent().find('td:eq(3)').html()));
											$tipocambio = $(this).parent().parent().find('td:eq(7)').find('input[name=tipocamb]').val();
											$total_pagos = parseFloat($saldo + $pagos).toFixed(2);
											$saldado=false;
											//alert("total_pagos:"+$total_pagos+"  monto:"+$monto);
											if(parseFloat($total_pagos) == parseFloat($monto)){
												$saldado = true
											}
											//alert("saldado:"+$saldado);
											table_envio +=  "<tr><td style='text-align: center ! important; border-top: 2px solid rgb(220, 220, 220); border-bottom: 2px solid rgb(220, 220, 220); border-left: 2px solid rgb(220, 220, 220);' width=70>"+$factura_vista+"</td><td width=15 style='border-top: 2px solid rgb(220, 220, 220); border-bottom: 2px solid rgb(220, 220, 220);'></td><td style='text-align:right !important; border-top: 2px solid rgb(220, 220, 220); border-bottom: 2px solid rgb(220, 220, 220);border-right:  2px solid rgb(220, 220, 220);' width=100>"+ $(this).agregar_comas($saldo) +"</td></tr>";
											cadena += $factura_vista + "___" + $saldado + "___" + $saldo + "___" + $tipocambio + "&";
										}
									});
									
									table_envio +="</tbody></table></div></td></tr></table>";
									var antipo=0;
									var text_anticipo="";
									
									//var psos = parseFloat($textarea_anticipo.val().split('\n')[0].split(' ')[3].split(',').join(''));
									//var usd = parseFloat($textarea_anticipo.val().split('\n')[1].split(' ')[3].split(',').join(''));
									var psos = parseFloat($sum_anticipos_mn.val());
									var usd = parseFloat($sum_anticipos_usd.val());
									//alert("psos: "+psos+" usd: "+usd );
									if ($deuda_pesos > psos){
										if ($deuda_usd > usd){
											if($monto_anticipo > 0){
												//alert("Usd: "+$deuda_usd +"  "+usd+"\nPesos: "+$deuda_pesos+" "+psos+" \n anticipo: "+$monto_anticipo);
												antipo = parseFloat($monto_anticipo).toFixed(2);
												if($select_moneda.val()=='1')
													text_anticipo +="se generar&aacute; un anticipo de: " + $(this).agregar_comas(antipo) + " Pesos\n"
												else
													text_anticipo +="se generar&aacute; un anticipo de: " + $(this).agregar_comas(antipo) + " USD\n"
											}
										}
									}
									
									
									var variables_to_pass = {
										'fecha':$campo_fecha.val(),
										'fecha_deposito':$campo_fecha_deposito.val(),
										
										/********************************************************/
										'banco_kemikal':$campo_banco_deposito.val(),
										'cuenta_deposito':$campo_cuenta_deposito.val(),
										'movimiento_deposito':$campo_movimiento_deposito.val(),
										/*******************************************************/										
										
										'tipo_cambio':$tipo_cambio.val(),
										'forma_pago':$select_forma_pago.val(),
										'moneda':$select_moneda.val(),
										'banco':$select_banco.val(),
										'monto_pago':quitar_comas($monto_pago.val()),
										'tarjeta':$campo_tarjeta.val(),
										'cheque':$campo_cheque.val(),
										'referencia':$campo_referencia.val(),
										'observaciones':$textarea_observaciones.val(),
										'deuda_pesos' : $deuda_pesos,
										'deuda_usd' : $deuda_usd,
										'valores':cadena,
										'antipo': antipo,
										'cliente_id': $identificador_cliente.val(),
										'anticipo_gastado': $campo_monto_ant_selecc.val(),//asignar valor  de anticipo gastado.
										'no_transaccion_anticipo': trans_cant_anticipo_seleccionado(1),//numero de anticipo
										'iu': $('#lienzo_recalculable').find('input[name=iu]').val(),
										'saldo_a_favor':$textarea_monto_antipo.val()
									};
									
									//var input_busqueda_json = document.location.protocol + '//' + document.location.host + '/' + controller + '/' + 'registra_pagos' + '/out.json';
									var input_registra_json = document.location.protocol + '//' + document.location.host + '/' + controller + '/registra_pagos.json';
									jConfirm('Se relaizar&aacute; el pago para el cliente:\n'+$cliente.val()+"\nse saldaran "+ contador +" facturas\n"+text_anticipo+"Datos de las facturas a saldar\n"+
									table_envio, 'Dialogo de confirmaci&oacute;n', function(r) {
										if (r){
											$.post(input_registra_json,variables_to_pass,function(data){
												
												if ( data['success'] == "true" ){
													// Desaparece todas las interrogaciones si es que existen
													$('#forma-carteras-window').find('div.interrogacion').css({'display':'none'});
													jAlert('Pago registrado con &eacute;xito.\nN&uacute;mero de transacci&oacute;n: '+data['numero_transaccion'],'Atencion!');
													
													$textarea_observaciones.val('');
													
													/*
													//COMENTADO PARA SAAR Y NOVASOL, HABILITAR SOLO PARA KEMIKAL Y KATHION
													var iu = $('#lienzo_recalculable').find('input[name=iu]').val();
													var input_json = document.location.protocol + '//' + document.location.host + '/' + controller + '/getPdfReporteAplicacionPago/'+data['identificador_pago']+'/'+iu+'/out.json';
													window.location.href=input_json;
													*/
													
													movimiento0($contenido2);
													$get_datos_grid();
												}else{
													// Desaparece todas las interrogaciones si es que existen
													$('#forma-carteras-window').find('div.interrogacion').css({'display':'none'});
													
													var valor = data['success'].split('___');
													//muestra las interrogaciones
													for (var element in valor){
														tmp = data['success'].split('___')[element];
														longitud = tmp.split(':');
														
														if( longitud.length > 1 ){
															$('#forma-carteras-window').find('img[rel=warning_' + tmp.split(':')[0] + ']')
															.parent()
															.css({'display':'block'})
															.easyTooltip({tooltipId: "easyTooltip2",content: tmp.split(':')[1]});
															
															if(tmp.split(':')[0].substring(0, 9) == 'cancelado'){
																jAlert(tmp.split(':')[1],'Atencion!')
															}
														}
													}
												}
												
											});
										}
									});
									
								}else{
									jAlert("No hay pagos a registrar","Atencion!");
								}
							}
					});
					
					
				}//termina tipo movimiento 1 pagos
				
				
				
				
				/*------------------------------------------------------------------------------------------------------------*/
				/** movimiento Anticipo ***/
				/*------------------------------------------------------------------------------------------------------------*/
				if(tipo_movimiento==2){
					//alert("Este modulo esta en construccion");
					//definir posicion de la ventana cuando no hay opcion seleccionado
					$('#forma-carteras-window').css({"margin-left": -270,"margin-top": -200});
					//definir alto de la ventana cuando no hay opcion seleccionado
					$('#forma-carteras-window').find('.carteras_div_one').css({'height':'293px'});
					
					//ancho de la ventana
					$('#forma-carteras-window').find('.carteras_div_one').css({'width':'800px'});
					$('#forma-carteras-window').find('.carteras_div_two').css({'width':'800px'});
					$('#forma-carteras-window').find('#div_two').find('#carteras_div_titulo_ventana').css({'width':'760px'});
					$('#forma-carteras-window').find('#div_two').find('#carteras_div_titulo_ventana').find('strong').text('');
					$('#forma-carteras-window').find('#div_two').find('#carteras_div_titulo_ventana').find('strong').text('Registro de anticipos');
					$('#forma-carteras-window').find('.carteras_div_three').css({'width':'790px'});
					//posicion de los botones
					$('#forma-carteras-window').find('#botones').css({'width':'773px'});
					$registra_anticipo.show();
					$submit_registrar_pago.hide();
					$submit_registrar_cancelacion.hide();
					
					$contenido2.find('tr.uno').find('a[href*=busca_cliente]').hide();
					$contenido2.find('tr.uno').find('td.oculta_anticipo').hide();
					
					$contenido2.find('tr.transaccion').remove();
					$contenido2.find('tr.facturas').remove();
					$contenido2.find('tr.uno').find('tbody tr.cheque').hide();
					$contenido2.find('tr.uno').find('tbody tr.cancelar').remove();
					$contenido2.find('tr.uno').find('tbody tr.cancelar_pagos').remove();
					$contenido2.find('tr.uno').find('tbody tr.transferencia').hide();
					$contenido2.find('tr.uno').find('tbody tr.registros').hide();
					$contenido2.find('tr.uno').find('tbody tr.tarjeta').hide();
					$contenido2.find('tr.uno').find('tbody tr.pago').hide();
					$contenido2.find('tr.uno').find('tbody tr.monto').hide();
					$contenido2.find('tr.uno').find('tbody tr.monto_total').hide();
					$contenido2.find('tr.uno').find('tbody tr.moneda').hide();
					$contenido2.find('.observaciones_2').hide();
					$contenido2.find('tbody tr.anticipo').remove();
					$contenido2.find('tr.registros').hide();
					$contenido2.find('tr.uno').find('tbody tr.pago').find('td:eq(3)').find('div span').remove();
					$contenido2.find('tr.salida').find('input[value=Registrar Pago]').remove();
					$contenido2.find('tr.salida').find('input[value=Registrar Anticipo]').remove();
					$contenido2.find('tr.salida').find('input[value=Registrar Cancelacion]').remove();
					$contenido2.find('tr.salida').find('input[value=Registrar Nota de Credito]').remove();
					$contenido2.find('tr.uno').find('tbody tr.anticipo').remove();
					$contenido2.find('.ficha_deposito').hide();
					$contenido2.find('.anticipos_txarea').hide();
					$monto_pago.attr({ 'value' : 0.00});
					$contenido2.find('tr.uno').find('tbody tr.moneda').before($('table.anticipo').find('tr.anticipo').clone());
					
					
					var $moneda_anticipo = $contenido2.find('tr.anticipo').find('select[name=moneda_anticpo]');
					var $campo_fecha_anticipo = $contenido2.find('tr.anticipo').find('input[name=fecha_anticipo]');
					var $campo_monto_anticpo = $contenido2.find('tr.anticipo').find('input[name=monto_anticipo]');
					var $textarea_obser_anticipo = $contenido2.find('tr.anticipo').find('textarea[name=obser_anticipo]');
					
					
					var input_json = document.location.protocol + '//' + document.location.host + '/'+controller+'/get_monedas.json';
					$arreglo = {};
					$.post(input_json,$arreglo,function(entry){
						//carga select con todas las monedas
						$moneda_anticipo.children().remove();
						var tipo_moneda_hmtl="";// = '<option value="0" selected="yes">Seleccione una opcion</option>';
						$.each(entry['Monedas'],function(entryIndex,moneda){
								tipo_moneda_hmtl += '<option value="' + moneda['id'] + '"  >' + moneda['descripcion'] + '</option>';
						});
						$moneda_anticipo.append(tipo_moneda_hmtl);
						
					});//termina llamada json
					
					
					
					
					$campo_fecha_anticipo.val(mostrarFecha());
					$campo_fecha_anticipo.click(function (s){
						var a=$('div.datepicker');
						a.css({'z-index':100,});
					});
					$campo_fecha_anticipo.DatePicker({
						format:'Y-m-d',
						onBeforeShow: function(){
							$campo_fecha_anticipo.DatePickerSetDate($campo_fecha_anticipo.val(), true);
						},
						date: $campo_fecha_anticipo.val(),
						current: $campo_fecha_anticipo.val(),
						starts: 1,
						position: 'bottom',
						locale: {
							days: ['Domingo', 'Lunes', 'Martes', 'Miércoles', 'Jueves', 'Viernes', 'Sábado','Domingo'],
							daysShort: ['Dom', 'Lun', 'Mar', 'Mir', 'Jue', 'Vir', 'Sab','Dom'],
							daysMin: ['Do', 'Lu', 'Ma', 'Mi', 'Ju', 'Vi', 'Sa','Do'],
							months: ['Enero', 'Febrero', 'Marzo', 'Abril', 'Mayo','Junio', 'Julio', 'Agosto', 'Septiembre','Octubre', 'Noviembre', 'Diciembre'],
							monthsShort: ['Ene', 'Feb', 'Mar', 'Abr','May', 'Jun', 'Jul', 'Ago','Sep', 'Oct', 'Nov', 'Dic'],
							weekMin: 'se'
						},
						onChange: function(formated, dates){
							var patron = new RegExp("^[0-9]{4}-[0-9]{1,2}-[0-9]{1,2}$");
							$campo_fecha_anticipo.val(formated);
							if (formated.match(patron) ){
								$campo_fecha_anticipo.DatePickerHide();
							};
						}
					});
					
					//quita cero al obtener el enfoque, si es mayor a 0 entonces no hace nada
					$campo_monto_anticpo.focus(function(e){
						if(parseFloat($campo_monto_anticpo.val())<1){
							$campo_monto_anticpo.val('');
						}
					});
					//pone cero al perder el enfoque, cuando no se ingresa un valor o cuando el valor es igual a cero, si hay un valor mayor que cero no hace nada
					$campo_monto_anticpo.blur(function(e){
						if(parseFloat($campo_monto_anticpo.val())==0 || $campo_monto_anticpo.val()==''){
							$campo_monto_anticpo.val("0.00");
						}
					});	
					
					$campo_monto_anticpo.keypress(function(e){
						// Permitir  numeros, borrar, suprimir, TAB, puntos
						if (e.which == 8 || e.which == 46 || e.which==13 || e.which == 0 || (e.which >= 48 && e.which <= 57 )) {
							return true;
						}else {
							return false;
						}
					});
					
					
					$registra_anticipo.click(function(event){
						//alert($textarea_obser_anticipo.val());
						patron =/^\d+(\.?\d+)?$/;
						if(patron.test($campo_monto_anticpo.val())){
							if ($campo_monto_anticpo.val()>0){
								var variables_to_pass = {'fecha_anticipo':$campo_fecha_anticipo.val(),
									'monto_anticipo':$campo_monto_anticpo.val(),
									'id_moneda':$contenido2.find('select[name=moneda_anticpo]').val(),
									'id_cliente':$identificador_cliente.val(),
									'observaciones':$textarea_obser_anticipo.val(),
									'iu': $('#lienzo_recalculable').find('input[name=iu]').val()
								};
								var input_registra_json = document.location.protocol + '//' + document.location.host + '/' + controller + '/generar_anticipo.json';
								jConfirm('Del cliente:\n'+$cliente.val()+"\nSe realizar&aacute; un Anticipo de "+ $(this).agregar_comas($campo_monto_anticpo.val()) + " " + $contenido2.find('select[name=moneda_anticpo] :selected').text() , 'Dialogo de confirmacion', function(r) {
									if (r){
										$.post(input_registra_json,variables_to_pass,function(data){
											jAlert('El anticipo se registro con &eacute;xito.\nN&uacute;mero de transacci&oacute;n: '+data['numero_transaccion'],'Atencion!')
											//$('#dialogo-cartera-overlay').remove();
											movimiento0($contenido2);
										});
									}
								});
							}else{
								jAlert('El monto del Anticipo debe de ser mayor a cero','Atencion!');
							}
						}else{
							jAlert('El valor del campo anticipo no es valido','Atencion!');
						}
					});
					
				}//termina tipo movimiento 2 anticipos




				/*------------------------------------------------------------------------------------------------------------*/
				/**Cancelacion de Pagos ***/
				/*------------------------------------------------------------------------------------------------------------*/
				if(tipo_movimiento==3){
					//$contenido2.find('table.formaBusqueda').attr({'width':1000});
					//$contenido2.css({'margin-left':-500});
					
					//definir posicion de la ventana cuando no hay opcion seleccionado
					$('#forma-carteras-window').css({"margin-left": -270,"margin-top": -230});
					//definir alto de la ventana cuando no hay opcion seleccionado
					$('#forma-carteras-window').find('.carteras_div_one').css({'height':'217px'});
					
					//alert("Haz escogido cancelar");
					//ancho de la ventana
					$('#forma-carteras-window').find('.carteras_div_one').css({'width':'800px'});
					$('#forma-carteras-window').find('.carteras_div_two').css({'width':'800px'});
					$('#forma-carteras-window').find('#div_two').find('#carteras_div_titulo_ventana').css({'width':'760px'});
					$('#forma-carteras-window').find('#div_two').find('#carteras_div_titulo_ventana').find('strong').text('');
					$('#forma-carteras-window').find('#div_two').find('#carteras_div_titulo_ventana').find('strong').text('Cancelacion de pagos');
					$('#forma-carteras-window').find('.carteras_div_three').css({'width':'790px'});
					//posicion de los botones
					$('#forma-carteras-window').find('#botones').css({'width':'773px'});
					
					$contenido2.find('tr.uno').find('td.oculta_anticipo').hide();
					$contenido2.find('tr.uno').find('a[href*=busca_cliente]').hide();
					
					$contenido2.find('tr.transaccion').remove();
					$contenido2.find('tr.facturas').remove();
					$contenido2.find('tr.uno').find('tbody tr.cheque').hide();
					$contenido2.find('tr.uno').find('tbody tr.anticipo').remove();
					$contenido2.find('tr.uno').find('tbody tr.transferencia').hide();
					$contenido2.find('tr.uno').find('tbody tr.registros').hide();
					$contenido2.find('tr.uno').find('tbody tr.tarjeta').hide();
					$contenido2.find('tr.uno').find('tbody tr.pago').hide();
					$contenido2.find('tr.uno').find('tbody tr.monto').hide();
					$contenido2.find('tr.uno').find('tbody tr.monto_total').hide();
					$contenido2.find('tr.uno').find('tbody tr.moneda').hide();
					$contenido2.find('tr.registros').hide();
					$contenido2.find('tr.anticipo').remove();
					$contenido2.find('tr.uno').find('tbody tr.pago').find('td:eq(3)').find('div span').remove();
					$contenido2.find('tr.uno').find('tbody tr.moneda').before($('table.cancelar').find('tr.cancelar').clone());
					$submit_registrar_pago.hide();
					$registra_anticipo.hide();
					$submit_registrar_cancelacion.show();
					
					$monto_pago.attr({ 'value' : 0.00});
					
					var $select_cancelar = $contenido2.find('select[name=cancelar]');
					var $campo_fecha_cancelar = $contenido2.find('tr.cancelar').find('input[name=fecha_cancelacion]');
					
					$contenido2.find('tr.uno').find('tr.cancelar').find("div.etiqueta_fecha_cancelacion").hide();
					$campo_fecha_cancelar.hide();
					$contenido2.find('tr.uno').find('tbody tr.cancelar_pagos').find('a[href*=buscar]').hide();
					
					
					$select_cancelar.children().remove();
					var tipo_cancel_hmtl = '<option value="0" selected="yes">Seleccione una opcion</option>';
						tipo_cancel_hmtl += '<option value="1">Pagos</option>';
						tipo_cancel_hmtl += '<option value="2">Nota de credito</option>';
						tipo_cancel_hmtl += '<option value="3">Nota de cargo</option>';
					$select_cancelar.append(tipo_cancel_hmtl);
					
					
					$select_cancelar.change(function(){
						var tipo_cancelacion = $(this).val();
						if(tipo_cancelacion==0){
							$('#forma-carteras-window').find('.carteras_div_one').css({'height':'215px'});
							$contenido2.find('tr.uno').find('tr.cancelar').find("div.etiqueta_fecha_cancelacion").hide();
							$contenido2.find('tr.uno').find('tbody tr.cancelar_pagos').find('a[href*=buscar]').hide();
							$campo_fecha_cancelar.hide();
							$contenido2.find('tr.uno').find('tbody tr.cancelar_pagos').remove();
							$contenido2.find('tr.transaccion').remove();
							$contenido2.find('tr.facturas').remove();
						}
						//cancelar pago
						if(tipo_cancelacion==1){
							$('#forma-carteras-window').find('.carteras_div_one').css({'height':'315px'});
							$contenido2.find('tr.transaccion').remove();
							$contenido2.find('tr.facturas').remove();
							$contenido2.find('tr.uno').find('tbody tr.cancelar_pagos').remove();
							$contenido2.find('tr.uno').find('tbody tr.moneda').before($('table.cancelar').find('tr.cancelar_pagos').clone());
							$contenido2.find('tr.uno').find('tbody tr.cancelar').find('td').show();
							$contenido2.find('tr.uno').find('tr.cancelar').find("div.etiqueta_fecha_cancelacion").show();
							$campo_fecha_cancelar.show();
							
							var $bonton_busqueda_cancel = $contenido2.find('tr.uno').find('tbody tr.cancelar_pagos').find('a[href*=buscar]');
							//var $bonton_busqueda_cancel_salir = $contenido2.find('tr.uno').find('tbody tr.cancelar_pagos').find('input[value$=Buscar]');
							$bonton_busqueda_cancel.hide();
							
							var $select_cancelar_por = $contenido2.find('tr.uno').find('tbody tr.cancelar_pagos').find('select[name=cancelar_pagos]');
							$select_cancelar_por.children().remove();
								var cancela_por_hmtl = '<option value="0" selected="yes">Seleccione una opcion</option>';
								cancela_por_hmtl += '<option value="1">Cancelar por Facturas</option>';
								cancela_por_hmtl += '<option value="2">Cancelar por No. de Transaccion</option>';
							$select_cancelar_por.append(cancela_por_hmtl);
							
							$campo_fecha_cancelar.val(mostrarFecha());
							$campo_fecha_cancelar.click(function (s){
								var a=$('div.datepicker');
								a.css({'z-index':100,});
							});
							
							String.prototype.toCharCode = function(){
								var str = this.split(''), len = str.length, work = new Array(len);
								for (var i = 0; i < len; ++i){
									work[i] = this.charCodeAt(i);
								}
								return work.join(',');
							};
							$campo_fecha_cancelar.DatePicker({
								format:'Y-m-d',
								onBeforeShow: function(){
									$campo_fecha_cancelar.DatePickerSetDate($campo_fecha_cancelar.val(), true);
								},
								date: $(this).val(),
								current: $(this).val(),
								starts: 1,
								position: 'bottom',
								locale: {
									days: ['Domingo', 'Lunes', 'Martes', 'Miércoles', 'Jueves', 'Viernes', 'Sábado','Domingo'],
									daysShort: ['Dom', 'Lun', 'Mar', 'Mir', 'Jue', 'Vir', 'Sab','Dom'],
									daysMin: ['Do', 'Lu', 'Ma', 'Mi', 'Ju', 'Vi', 'Sa','Do'],
									months: ['Enero', 'Febrero', 'Marzo', 'Abril', 'Mayo','Junio', 'Julio', 'Agosto', 'Septiembre','Octubre', 'Noviembre', 'Diciembre'],
									monthsShort: ['Ene', 'Feb', 'Mar', 'Abr','May', 'Jun', 'Jul', 'Ago','Sep', 'Oct', 'Nov', 'Dic'],
									weekMin: 'se'
								},
								onChange: function(formated, dates){
									var patron = new RegExp("^[0-9]{4}-[0-9]{1,2}-[0-9]{1,2}$");
									$campo_fecha_cancelar.val(formated);
									if (formated.match(patron) ){
										$campo_fecha_cancelar.DatePickerHide();
									};
								}
							});
							
							$bonton_busqueda_cancel.click(function(event){
								event.preventDefault();
								$(this).modalPanel_Buscafactura();
								var $dialogoc =  $('#forma-buscafactura-window');
								$dialogoc.append($('div.busqueda_cancelar').find('table.formaBusqueda_facturas').clone());
								$('#forma-buscafactura-window').css({"margin-left": -100, 	"margin-top": -210});
								
								var $tabla_resultados = $('#forma-buscafactura-window').find('#tabla_resultado');
								
								var $busca_factura_modalbox = $('#forma-buscafactura-window').find('#busca_factura_modalbox');
								var $cancelar_plugin_busca_factura = $('#forma-buscafactura-window').find('#cencela');
								
								var $num_trans = $('#forma-buscafactura-window').find('input[name=num_trans]');
								var $factura = $('#forma-buscafactura-window').find('input[name=factura]');
								
								//funcionalidad botones
								$busca_factura_modalbox.mouseover(function(){
									$(this).removeClass("onmouseOutBuscar").addClass("onmouseOverBuscar");
								});
								$busca_factura_modalbox.mouseout(function(){
									$(this).removeClass("onmouseOverBuscar").addClass("onmouseOutBuscar");
								});
								   
								$cancelar_plugin_busca_factura.mouseover(function(){
									$(this).removeClass("onmouseOutCancelar").addClass("onmouseOverCancelar");
								});
								$cancelar_plugin_busca_factura.mouseout(function(){
									$(this).removeClass("onmouseOverCancelar").addClass("onmouseOutCancelar");
								});
								
								//click buscar facturas
								$busca_factura_modalbox.click(function(event){
									var input_json = document.location.protocol + '//' + document.location.host + '/'+controller+'/get_buscador_facturas_cancelar.json';
									$arreglo = {'num_trans':$num_trans.val(),
												'factura':$factura.val(),
												'id_cliente':$identificador_cliente.val(),
												'iu': $('#lienzo_recalculable').find('input[name=iu]').val()
												}
									
									var trr = '';
									$tabla_resultados.children().remove();
									$.post(input_json,$arreglo,function(entry){
										$.each(entry['FacturasCancelar'],function(entryIndex,factura){
											trr = '<tr>';
												trr += '<td width="100%">';
												trr += '<span style="display:none">'+factura['numero_transaccion']+'</span>';
												trr += 'No. Transaccion: '+factura['numero_transaccion']+' Fecha: '+factura['momento_creacion'].split('.')[0]+'<br>';
												trr +=' Facturas: '+factura['serie_folio']+'<br>';
												trr +=' Observaciones: '+factura['observaciones'];
												trr += '</td>';
											trr += '</tr>';
											
											$tabla_resultados.append(trr);
										});

										$tabla_resultados.find('tr:odd').find('td').css({'background-color' : '#e7e8ea'});
										$tabla_resultados.find('tr:even').find('td').css({'background-color' : '#FFFFFF'});

										$('tr:odd' , $tabla_resultados).hover(function () {
											$(this).find('td').css({background : '#FBD850'});
										}, function() {
												//$(this).find('td').css({'background-color':'#DDECFF'});
											$(this).find('td').css({'background-color':'#e7e8ea'});
										});
										$('tr:even' , $tabla_resultados).hover(function () {
											$(this).find('td').css({'background-color':'#FBD850'});
										}, function() {
											$(this).find('td').css({'background-color':'#FFFFFF'});
										});

										//seleccionar un producto del grid de resultados
										$tabla_resultados.find('tr').click(function(){
											var num_tran = $(this).find('span').html();
											
											//var json_datos = document.location.protocol + '//' + document.location.host + '/' + 'Carteras' + '/' + 'facturas_num_transaccion' + '/' + num_tran + '/out.json';
											var input_json = document.location.protocol + '//' + document.location.host + '/'+controller+'/get_facturas_num_transaccion.json';
											$arreglo = {'num_trans':num_tran }
											$.post(input_json,$arreglo,function(entry){
												$('#forma-carteras-window').find('.carteras_div_one').css({'height':'480px'});
												$contenido2.find('tr.facturas').remove();
												$contenido2.find('tr.vacio').before($('table.cancelar').find('tr.facturas').clone());
												$.each(entry['FacturasTrans'],function(entryIndex, fact){
													var contenido_c="";
													contenido_c += "<tr><td class='grid' style='font-size: 11px;  border:1px solid #C1DAD7;' width='15'><input type='checkbox' name='micheck' value='check'></td>";
													contenido_c += "<td class='grid' style='font-size: 11px;  border:1px solid #C1DAD7 ;' width='80'>" + fact['serie_folio'] + "<span style='display:none'>"+fact['serie_folio']+"</span></td>";
													
													contenido_c += "<td class='grid2' style='font-size: 11px;  border:1px solid #C1DAD7;' width='90' align='right'>"+ $(this).agregar_comas(fact['monto_total'])+"</td>";
													contenido_c += "<td class='grid2' style='font-size: 11px;  border:1px solid #C1DAD7;' width='90' align='right'>"+ $(this).agregar_comas(fact['cantidad'])+"</td>";
													
													contenido_c += "<td class='grid' style='font-size: 11px;  border:1px solid #C1DAD7;' width='90'>" +fact['denominacion']+ "</td>";
													contenido_c += "<td class='grid' style='font-size: 11px;  border:1px solid #C1DAD7;' width='110'>" +fact['momento_facturacion'].split(' ')[0]+" "+fact['momento_facturacion'].split(' ')[1].split('-')[0]+ "</td>";
													contenido_c += "<td class='grid' style='font-size: 11px;  border:1px solid #C1DAD7;' width='110'>" +fact['momento_pago'].split(' ')[0]+" "+fact['momento_pago'].split(' ')[1].split('-')[0]+ "</td>";
													contenido_c += "<td class='grid' style='font-size: 11px;  border:1px solid #C1DAD7;' width='110'>" +num_tran+ "</td>";
													
													$('tbody.contenido_entrada_facturas_can',$contenido2).append(contenido_c);
												});
											});
											
											//elimina la ventana de busqueda
											var remove = function() {$(this).remove();};
											$('#forma-buscafactura-overlay').fadeOut(remove);
											//asignar el enfoque al campo sku del producto
										});

									});
								});//termina llamada json
								
								//cancela busqueda
								$cancelar_plugin_busca_factura.click(function(event){
									var remove = function() {$(this).remove();};
									$('#forma-buscafactura-overlay').fadeOut(remove);
								});
									
							});
							
							$select_cancelar_por.change(function(){
								var variable = $(this).val();
								//sin tipo de busqueda
								if(variable==0){
									$('#forma-carteras-window').find('.carteras_div_one').css({'height':'315px'});
									$contenido2.find('tr.transaccion').remove();
									$contenido2.find('tr.facturas').remove();
									$bonton_busqueda_cancel.hide();
								}
								
								//cancelacion por facturas
								if(variable==1){
									$('#forma-carteras-window').find('.carteras_div_one').css({'height':'315px'});
									$contenido2.find('tr.transaccion').remove();
									$bonton_busqueda_cancel.show();//visualiza el boton de buscar facturas
								}
								
								//cancelacion por numero de transaccion
								if(variable==2){
									$contenido2.find('tr.uno').find('tbody tr.cancelar_pagos').find('a[href*=buscar]').hide();//oculta boton de buscar facturas
									var input_json = document.location.protocol + '//' + document.location.host + '/'+controller+'/get_num_transaccion_cliente.json';
									$arreglo = {'id_cliente':$identificador_cliente.val() }
									
									$.post(input_json,$arreglo,function(entry){
										$('#forma-carteras-window').find('.carteras_div_one').css({'height':'480px'});
										$contenido2.find('tr.transaccion').remove();
										$contenido2.find('tr.facturas').remove();
										if(entry.length!=0){
											$contenido2.find('tr.vacio').before($('table.cancelar').find('tr.transaccion').clone());
										}
										$.each(entry['NumTrans'],function(entryIndex, trans){
											//$contenido2.css({'margin-top':-300});
											var contenido_c="";
											contenido_c += "<tr><td class='grid' style='font-size: 11px;  border:1px solid #C1DAD7;' width='15'><input type='checkbox' name='micheck' value='check'></td>";
											contenido_c += "<td class='grid1' style='font-size: 11px;  border:1px solid #C1DAD7 ;' width='100'>"+trans['numero_transaccion']+"</td>";
											contenido_c += "<td class='grid1' style='font-size: 11px;  border:1px solid #C1DAD7;' width='500'>"+trans['facturas']+"</td>";
											contenido_c += "<td class='grid1' style='font-size: 11px;  border:1px solid #C1DAD7;' width='110' >"+trans['momento_pago']+"</td>";
											$('tbody.contenido_entrada_cancel',$contenido2).append(contenido_c);
										});

									});
								}
								
							});
						}
						
						//cancelar nota credito
						if(tipo_cancelacion==2){
							//$contenido2.find('table.formaBusqueda').attr({'width':1000});
							//$contenido2.css({'margin-left':-500});
							$contenido2.find('tr.uno').find('tr.cancelar td:eq(2)').css({'display':'none'});
							$contenido2.find('tr.uno').find('tr.cancelar td:eq(3)').css({'display':'none'});
							$contenido2.find('tr.uno').find('tbody tr.cancelar_pagos').remove();
							$contenido2.find('tr.transaccion').remove();
							$contenido2.find('tr.facturas').remove();
						}
						
						//cancelar nota cargo
						if(tipo_cancelacion==3){
							//$contenido2.find('table.formaBusqueda').attr({'width':1000});
							//$contenido2.css({'margin-left':-500});
							$contenido2.find('tr.uno').find('tr.cancelar td:eq(2)').css({'display':'none'});
							$contenido2.find('tr.uno').find('tr.cancelar td:eq(3)').css({'display':'none'});
							$contenido2.find('tr.uno').find('tbody tr.cancelar_pagos').remove();
							$contenido2.find('tr.transaccion').remove();
							$contenido2.find('tr.facturas').remove();
						}
					});
					
					$submit_registrar_cancelacion.click(function(){
						var $select_cancelar_por = $contenido2.find('tr.uno').find('tbody tr.cancelar_pagos').find('select[name=cancelar_pagos]');
						var $textarea_cancelar = $contenido2.find('tr.uno').find('tbody tr.cancelar_pagos').find('textarea[name=observaciones_cancelacion]');
						var patron = /^.{10,500}$/;
						if($select_cancelar_por.val()==0){
							jAlert("No se puede registrar la cancelacion","Atencion!");
						}
						
						if($select_cancelar_por.val()==1){
							if(patron.test($textarea_cancelar.val())){
								var correc = false;
								var table_envio="";
								var contador=0;
								var cadena="";
								$('tbody.contenido_entrada_facturas_can tr',$contenido2).find('input[name=micheck]').each(function(){
									if(this.checked){
										correc=true;
										contador++;
									}
								});
								if(correc){
									var $numero;
									table_envio += "<table><tr><td>";
									table_envio += "<table><thead><tr><th style='background-color:#DCDCDC;border-top: 2px solid rgb(220, 220, 220); border-bottom: 2px solid rgb(220, 220, 220); border-left: 2px solid rgb(220, 220, 220);' width=80>Factura</th></thead></table>";
									table_envio += '<div style="overflow-x: hidden; overflow-y: auto; width: 100%; height: 150px;">'
									table_envio += "<table><tbody>";
									$('tbody.contenido_entrada_facturas_can tr',$contenido2).find('input[name=micheck]').each(function(){
										if(this.checked){
											$factura_vista= $(this).parent().parent().find('td:eq(1)').html().split('<span')[0];
											
											$numero = $(this).parent().parent().find('td:eq(7)').html();
											$factura= $(this).parent().parent().find('td:eq(1)').find('span').html();
											//alert("Factura: "+$factura);
											table_envio +=  "<tr><td style='border-top: 2px solid rgb(220, 220, 220); border-bottom: 2px solid rgb(220, 220, 220); border-left: 2px solid rgb(220, 220, 220); border-right:2px solid rgb(220, 220, 220);' width=80>"+$factura_vista+"</td></tr>";
											cadena += $factura + "___";
										}
									});
									table_envio +="</tbody></table></div></td></tr></table>";
									$arreglo = {
										'cancelar_por':$select_cancelar_por.val(),
										'observaciones_canc':$textarea_cancelar.val(),
										'numero_trans':$numero,
										'cadena':cadena,
										'fecha_cancelacion':$campo_fecha_cancelar.val(),
										'iu': $('#lienzo_recalculable').find('input[name=iu]').val()
									}
									var input_json = document.location.protocol + '//' + document.location.host + '/'+controller+'/cancelar_pagos.json';
									jConfirm('Del cliente:\n'+$cliente.val()+"\n se cancelaran "+ contador +" facturas\nDatos de la cancelacion\n"+
									table_envio, 'Dialogo de confirmacion', function(r) {
										if (r){
											//alert("Envio");
											$.post(input_json,$arreglo,function(data){
												jAlert('Numero de transaccion: '+data['numero_transaccion']+'. Cancelacion exitosa','Atencion!')
												//$('#dialogo-cartera-overlay').remove();
												movimiento0($contenido2);
												$get_datos_grid();
											});
										}
									});
								}else{
										jAlert("No se puede registrar la cancelacion","Atencion!");
								}
							}else{
								jAlert("El campo observaciones es obligatorio","Atencion!");
							}
						}
						
						
						if($select_cancelar_por.val()==2){
							if(patron.test($textarea_cancelar.val())){
								var correcto = false;
								var table_envio="";
								var contador=0;
								var cadena="";
								$('tbody.contenido_entrada_cancel tr',$contenido2).find('input[name=micheck]').each(function(){
									if(this.checked){
										correcto=true;
										contador++;
									}
								});
								if(correcto){
									table_envio += "<table><tr><td>";
									table_envio += "<table><thead><tr><th style='background-color:#DCDCDC;border-top: 2px solid rgb(220, 220, 220); border-bottom: 2px solid rgb(220, 220, 220); border-left: 2px solid rgb(220, 220, 220);' width=150>Numeo de Transaccion</th></thead></table>";
									table_envio += '<div style="overflow-x: hidden; overflow-y: auto; width: 100%; height: 150px;">'
									table_envio += "<table><tbody>";
									$('tbody.contenido_entrada_cancel tr',$contenido2).find('input[name=micheck]').each(function(){
										if(this.checked){
											$numero= $(this).parent().parent().find('td:eq(1)').html();
											
											table_envio +=  "<tr><td style='border-top: 2px solid rgb(220, 220, 220); border-bottom: 2px solid rgb(220, 220, 220); border-left: 2px solid rgb(220, 220, 220); border-right:2px solid rgb(220, 220, 220);' width=150>"+$numero+"</td></tr>";
											cadena += $numero + "___";
										}
									});
									table_envio +="</tbody></table></div></td></tr></table>";
									var variables_to_pass = {
										'cancelar_por':$select_cancelar_por.val(),
										'observaciones_canc':$textarea_cancelar.val(),
										'numero_trans':'0000000000',
										'cadena':cadena,
										'fecha_cancelacion':$campo_fecha_cancelar.val(),
										'iu': $('#lienzo_recalculable').find('input[name=iu]').val()
									};
									
									var input_busqueda_json = document.location.protocol + '//' + document.location.host + '/' + controller + '/cancelar_pagos.json';
									jConfirm('Del cliente:\n'+$cliente.val()+"\nse cancelaran "+ contador +" trasacciones\nDatos de la cancelaci&oacute;n\n"+
									table_envio, 'Dialogo de confirmaci&oacute;n', function(r) {
										if (r){
											$.post(input_busqueda_json,variables_to_pass,function(data){
												//jAlert('Numero de transaccion: '+data['numero_transaccion'],'Atencion!')
												//$('#dialogo-cartera-overlay').remove();
												jAlert('Numeros de transacci&oacute;n: '+data['numero_transaccion'].split(',')[0]+'. Cancelados con &eacute;xito.','Atencion!')
												movimiento0($contenido2);	
												$get_datos_grid();													
											});
										}
									});
									
								}else{
									jAlert("No se puede registrar la cancelacion","Atencion!");
								}
							}else{
								jAlert("El campo observaciones es obligatorio","Atencion!");
							}
						}
						
						
						
					});
					
					
				}//termina tipo movimiento 3
				
				
				
				/*------------------------------------------------------------------------------------------------------------*/
				/** Nota de credito ***/
				/*------------------------------------------------------------------------------------------------------------*/
				if(tipo_movimiento==4){
					
					$('#forma-carteras-window').find('#div_two').find('#carteras_div_titulo_ventana').find('strong').text('');
					$('#forma-carteras-window').find('#div_two').find('#carteras_div_titulo_ventana').find('strong').text('Registro de notas de credito');
					
					$('div.datepicker').blur(function(e){
						//alert("eres covarde");
					});
					
					
					alert("Este modulo esta en construccion");
					//$contenido2.find('table.formaBusqueda').attr({'width':1000});
					//$contenido2.css({'margin-left':-500});
					$contenido2.find('tbody tr').show();
					$contenido2.find('tr.transaccion').remove();
					$contenido2.find('tr.facturas').remove();
					//$contenido2.css({'margin-top':-230});
					$contenido2.find('tr.uno').find('tbody tr.cancelar').remove();
					$contenido2.find('tr.uno').find('tbody tr.cancelar_pagos').remove();
					$contenido2.find('tr.uno').find('tbody tr.cheque').hide();
					$contenido2.find('tr.uno').find('tbody tr.transferencia').hide();
					$contenido2.find('tr.registros').hide();
					$contenido2.find('.ficha_deposito').hide();
					$contenido2.find('.observaciones_2').show();
					$contenido2.find('.anticipos_txarea').hide();
					$contenido2.find('tr.anticipo').hide();
					$contenido2.find('.pago_a').hide();
					$contenido2.find('.fecha_nc').hide();
					$monto_pago.attr({ 'value' : 0.00});
					/*
					$contenido2.find('tr.salida').find('input[value=Registrar Cancelacion]').remove();
					$contenido2.find('tr.salida').find('input[value=Registrar Anticipo]').remove();
					$contenido2.find('tr.salida').find('input[value=Registrar Pago]').remove();
					$contenido2.find('tr.salida').find('input[value=Registrar Nota de Credito]').remove();
					*/
					$contenido2.find('tr.uno').find('tbody tr.tarjeta').hide();
					$submit_registrar_pago.hide();
					$submit_registrar_cancelacion.hide();
					$registra_anticipo.show();
					
					/*
					var cambia_tipo_cambio="<span class='button_for_ie'><input align='center' type='botton' value='<<>>' style='font-size: 11px; background-color: rgb(255, 255, 255);  padding: 1px; width: 40px;'></span>";
					$contenido2.find('tr.uno').find('tbody tr.pago').find('td:eq(3)').find('div').append(cambia_tipo_cambio);
					var registra_pago='<input align="center" type="botton" value="Registrar Nota de Credito" style="font-size: 11px; background-color: rgb(255, 255, 255); text-align:center; border: 2px solid rgb(204, 204, 204); padding: 1px; width: 150px; background:none repeat scroll 0 0 transparent; color:blue; cursor: pointer; font-family:Tahoma;">';
					$contenido2.find('tr.salida').find('td:eq(1)').append(registra_pago);
					var $boton_nota_credito = $contenido2.find('input[value$=Registrar Nota de Credito]');
					*/
					var input_busqueda_json = document.location.protocol + '//' + document.location.host + '/' + controller + '/' + 'obtener_facturas' + '/' + $identificador_cliente.val()  +'/out.json';
					var input_busqueda_json3 = document.location.protocol + '//' + document.location.host + '/' + controller + '/' + 'obtener_anticipos' + '/' + $identificador_cliente.val()  +'/out.json';

					var get_cuentas = document.location.protocol + '//' + document.location.host + '/' + controller + '/' + 'get_cuentas' + '/out.json';
					
					$.getJSON(get_cuentas,function(data){
						var cuentas_array = data['DataForControls']['cuentas'];
						
						$campo_cuenta_deposito.children().remove();	
						var cs_cuentashtml='';
						for ( iterador = 0; iterador < cuentas_array.length; iterador++ ){
							var cadena_to_cut = cuentas_array[iterador];
							var valor = cadena_to_cut.split(':')[0];
							var nom_valor = cadena_to_cut.split(':')[1];
							
							cs_cuentashtml += '<option value="' + valor + '"  >' + nom_valor + '</option>';
						}
						$campo_cuenta_deposito.append(cs_cuentashtml);
					});
					
					$.getJSON(input_busqueda_json3,function(data){
						$textarea_anticipo.val(data['anticipo']);								
						var psos = parseFloat($textarea_anticipo.val().split('\n')[0].split(' ')[3].split(',').join(''));
						var usd = parseFloat($textarea_anticipo.val().split('\n')[1].split(' ')[3].split(',').join(''));
						var total;
						total = parseFloat(parseFloat($monto_pago.val()) +  psos + (usd*$tipo_cambio.val()));
						//$monto_anticipo = total;
						$textarea_monto_antipo.val($(this).agregar_comas(0.00));
					});
					
					
					//alert("estoy aqui adentro");
					$.getJSON(input_busqueda_json,function(data){
						$('tbody.contenido_entrada',$contenido2).children().remove();
						
						$.each(data,function(entryIndex, entry){
							$contenido2.find('tbody tr.registros').show();
							$contenido2.css({'margin-top':-300});
							var contenido_c="";
							
							contenido_c += "<tr><td style='font-size: 11px;  border:1px solid #C1DAD7;' width='10'><input type='checkbox' name='micheck' value='check'></td>";
							contenido_c += "<td style='font-size: 11px;  border:1px solid #C1DAD7 ;' width='60'>" + entry[0]['numero_factura'] + "<span style='display:none'>"+entry[0]['folio']+"</span></td>";
							if(entry[0]['facturar_en']){
								contenido_c += "<td style='font-size: 11px;  border:1px solid #C1DAD7;' width='75' align='right'>"+ $(this).agregar_comas(entry[0]['total_usd'])+"</td>";
								contenido_c += "<td style='font-size: 11px;  border:1px solid #C1DAD7;' width='85' align='right'>"+ $(this).agregar_comas(entry[0]['accesor_saldo'])+"</td>";
								var saldo= parseFloat(entry[0]['total_usd']) - parseFloat(entry[0]['accesor_saldo']);
								contenido_c += "<td style='font-size: 11px;  border:1px solid #C1DAD7;' width='75' align='right'>"+$(this).agregar_comas(saldo.toFixed(2))+"</td>";
							}else{
								contenido_c += "<td style='font-size: 11px;  border:1px solid #C1DAD7;' width='75' align='right'>"+$(this).agregar_comas(entry[0]['suma_total'])+"</td>";
								contenido_c += "<td style='font-size: 11px;  border:1px solid #C1DAD7;' width='85' align='right'>"+$(this).agregar_comas(entry[0]['accesor_saldo'])+"</td>";
								var saldo = parseFloat(entry[0]['suma_total'])- parseFloat(entry[0]['accesor_saldo']);
								contenido_c += "<td style='font-size: 11px;  border:1px solid #C1DAD7;' width='75' align='right'>"+$(this).agregar_comas(saldo.toFixed(2))+"</td>";
							}
							contenido_c += "<td style='font-size: 11px;  border:1px solid #C1DAD7;' width='80'><input type='text' name='saldar' value='0.00' style='width: 80px; background-color:#F6F8FB; text-align:right;' disabled=true' align='right'></td>";
							contenido_c += "<td style='font-size: 11px;  border:1px solid #C1DAD7;' width='70'>" +entry[0]['accesor_denominacion']+ "</td>";
							contenido_c += "<td style='font-size: 11px;  border:1px solid #C1DAD7;' width='125'><input type='text' name='tipocamb' value='"+$tipo_cambio.val()+"' style='width: 80px; background-color:#F6F8FB;' align='right' disabled=true><span class='button_for_ie'><input align='center' type='botton' value='<<>>' style='font-size: 11px; background-color: rgb(255, 255, 255);  padding: 1px; width: 40px;'></span></td>";
							contenido_c += "<td style='font-size: 11px;  border:1px solid #C1DAD7;' width='92'>" +entry[0]['accesor_fecha_pago']+ "</td>";
							contenido_c += "<td style='font-size: 11px;  border:1px solid #C1DAD7;' width='95'>" +entry[0]['momento_creacion']+ "</td>";
							
							$('tbody.contenido_entrada',$contenido2).append(contenido_c);
							
						});
						
				/************************************/
				/***********************************/
				/***/	iterar_facturas();		/***/
				/***/	iterar_facturas_input();/***/
				/***/	obtener_deuda();		/***/
				/***********************************/
				/***********************************/
					});
					
					//tipo_cambio_global();
					
					var valida_notas_credito = function(monto, restante, observaciones,cadena_alert){
						retorno = false;
						monto = parseFloat(monto.split(',').join(''));
						restante = parseFloat(restante.split(',').join(''));
						
						if(monto == restante)
							cadena_alert = "";
						else
							cadena_alert = "false";
						
						if(restante == 0 && cadena_alert == "false" )
							cadena_alert = "";
						else
							return cadena_alert = "Debe de consumir todo el monto del pago";
						
						
						if(observaciones != ""  && cadena_alert == ""  )
							cadena_alert = "";
						else
							return cadena_alert = "Debe ingresar Observaciones";
						
						return cadena_alert;
					}
					
					$boton_nota_credito.click(function(){
						resultado = valida_notas_credito($monto_pago.val(),$textarea_monto_antipo.val(),$textarea_observaciones2.val(),cadena_alert="");
						if(resultado == ""){
							var cadena="";
							var folios="";
							var correc = false;
							var table_envio="";
							var contador=0;
							
							
							$obtiene_valor_mn = function($valor_cambio, $cantidad){
								$valor_cambio = parseFloat($valor_cambio);
								$cantidad = parseFloat($cantidad);
								return $valor_cambio*$cantidad
							}
							
							$obtiene_valor_usd = function($valor_cambio, $cantidad){
								$valor_cambio = parseFloat($valor_cambio);
								$cantidad = parseFloat($cantidad);
								return $cantidad/$valor_cambio;
							}
							
							
								table_envio += "<table><tr><td>";
								table_envio += "<table><thead><tr><th style='background-color:#DCDCDC;border-top: 2px solid rgb(220, 220, 220); border-bottom: 2px solid rgb(220, 220, 220); border-left: 2px solid rgb(220, 220, 220);' width=70>Factura</th><th style='border-top: 2px solid rgb(220, 220, 220); border-bottom: 2px solid rgb(220, 220, 220); background-color:#DCDCDC;' width=15></th><th style='text-align: center ! important; border-top: 2px solid rgb(220, 220, 220); border-bottom: 2px solid rgb(220, 220, 220); border-right: 2px solid rgb(220, 220, 220); background-color:#DCDCDC;' width=100>Cantidad a saldar</th></tr></thead></table>";
								table_envio += '<div style="overflow-x: hidden; overflow-y: auto; width: 100%; height: 150px;">'
								table_envio += "<table><tbody>";
								$('tbody.contenido_entrada tr',$contenido2).find('input[name=micheck]').each(function(){
									if(this.checked){
										$saldar = parseFloat(quitar_comas($(this).parent().parent().find('td:eq(5)').find('input[name=saldar]').val()));
										$factura = $(this).parent().parent().find('td:eq(1)').find('span').html();
										$factura_vista = $(this).parent().parent().find('td:eq(1)').html().split('<span')[0];
										$monto = quitar_comas($(this).parent().parent().find('td:eq(2)').html());
										$pagos = parseFloat(quitar_comas($(this).parent().parent().find('td:eq(3)').html()));
										$saldo = parseFloat(quitar_comas($(this).parent().parent().find('td:eq(4)').html()));
										$facturado_en = $(this).parent().parent().find('td:eq(6)').html();
										$tipocambio = $(this).parent().parent().find('td:eq(7)').find('input[name=tipocamb]').val();
										$total_pagos = parseFloat($saldo + $pagos).toFixed(2);
										$saldado=false;
										
										if($total_pagos == $monto){
											$saldado = true
										}
										
										$credito_pesos = $saldar;
										$credito_pesos = 0.00;
										$credito_usd = 0.00;
										if($facturado_en == "USD"){
											$credito_pesos = $obtiene_valor_mn($tipocambio,$saldar);
											$credito_usd = $saldar;
										}else{
											$credito_usd = $obtiene_valor_usd($tipocambio,$saldar);
											$credito_pesos = $saldar;
											//.split(',').join('')
										}
										
										table_envio +=  "<tr><td style='text-align: center ! important; border-top: 2px solid rgb(220, 220, 220); border-bottom: 2px solid rgb(220, 220, 220); border-left: 2px solid rgb(220, 220, 220);' width=70>"+$factura_vista+"</td><td width=15 style='border-top: 2px solid rgb(220, 220, 220); border-bottom: 2px solid rgb(220, 220, 220);'></td><td style='text-align:right !important; border-top: 2px solid rgb(220, 220, 220); border-bottom: 2px solid rgb(220, 220, 220);border-right:  2px solid rgb(220, 220, 220);' width=100>"+ $(this).agregar_comas($saldar) +"</td></tr>";
										cadena += $tipocambio +"|"+$pagos+"|"+$monto+"|"+$factura_vista+"|"+$factura+"|"+$saldado+"|"+$saldo+"|"+$credito_pesos+"|"+$credito_usd+"|"+$facturado_en+"&";
									}
								});
								
								table_envio +="</tbody></table></div></td></tr></table>";
								var antipo=0;
								var text_anticipo="";
								
								var psos = parseFloat($textarea_anticipo.val().split('\n')[0].split(' ')[3].split(',').join(''));
								var usd = parseFloat($textarea_anticipo.val().split('\n')[1].split(' ')[3].split(',').join(''));
								
								pago_pesos = 0.00;
								pago_usd = 0.00;
								if ($select_moneda.val() == "true"){
									pago_pesos = $obtiene_valor_mn($tipo_cambio.val().split(',').join(''),$monto_pago.val().split(',').join(''));
									pago_usd = $monto_pago.val().split(',').join('');
								}else{
									pago_usd = $obtiene_valor_usd($tipo_cambio.val().split(',').join(''),$monto_pago.val().split(',').join(''));
									pago_pesos = $monto_pago.val().split(',').join('');
								}
								
								var variables_to_pass = {
									'fecha':$campo_fecha.val(),
									'moneda':$select_moneda.val(),
									'tipo_cambionotacres':$tipo_cambio.val(),
									'monto_credito_usd':pago_usd,
									'monto_credito_mn':pago_pesos,
									'observaciones_2':$textarea_observaciones2.val(),
									'valores':cadena,
									'subnumero': $identificador_cliente.val(),
								};
								//observaciones_2
								
								var input_busqueda_json = document.location.protocol + '//' + document.location.host + '/' + controller + '/' + 'registra_nota_credito' + '/out.json';
								jConfirm('Se relaizara Nota de Credito para del cliente:\n'+$cliente.val()+"\nafectara "+ contador +" facturas\n"+text_anticipo+"Datos de las facturas afectadas\n"+
								table_envio, 'Dialogo de confirmacion', function(r) {
									if (r){
										
										$.post(input_busqueda_json,variables_to_pass,function(data){
											jAlert('Numero de transaccion: '+data['numero_transaccion'],'Atencion!')
											//$('#dialogo-cartera-overlay').remove();
											movimiento0($contenido2);
										},'json');
									}
								});
							
						}else{
							jAlert(resultado,"Atencion!");
						}
					});
				}//termina tipo movimiento 4
				
				

						
						
						
				if(tipo_movimiento==0){
					movimiento0($contenido2);
					$monto_pago.attr({ 'value' : 0.00});
				}
				

				$select_moneda.change(function(){
					calcular_monto($(this).val());
					if(obtener_folios()){
						$select_moneda.attr({'disabled':true});
					}else{
						$select_moneda.attr({'disabled':false});
					}
				});
            
				
				if(parseInt(tipo_movimiento)==1){
					$monto_pago.focus();
				}
        }//termina cambio de tipos de movimient0
        
        
        
        //funcion para cuando se selecciona tipo de moviminto=0
		function movimiento0($contenido1){
			//definir posicion de la ventana cuando no hay opcion seleccionado
			$('#forma-carteras-window').css({"margin-left": -350,"margin-top": -200});
			//definir alto de la ventana cuando no hay opcion seleccionado
			$('#forma-carteras-window').find('.carteras_div_one').css({'height':'190px'});
			
			//ancho de la ventana
			$('#forma-carteras-window').find('.carteras_div_one').css({'width':'913px'});
			$('#forma-carteras-window').find('.carteras_div_two').css({'width':'913px'});
			$('#forma-carteras-window').find('#carteras_div_titulo_ventana').css({'width':'873px'});
			$('#forma-carteras-window').find('.carteras_div_three').css({'width':'903px'});
			//posicion de los botones
			$('#forma-carteras-window').find('#botones').css({'width':'886px'});
			
			
			$contenido1.find('input[name=pagosxguardar]').attr({ 'value' : 0});
			$contenido1.find('input[name=monto_ant_selec]').attr({ 'value' : 0});
			
			$contenido1.find('tr.uno').find('td.oculta_anticipo').show();
			$contenido1.find('tr.uno').find('a[href*=busca_cliente]').show();
			$contenido1.find('tr.uno').find('tbody tr.cheque').hide();
			$contenido1.find('tr.uno').find('tbody tr.cancelar').remove();
			$contenido1.find('tr.uno').find('tbody tr.cancelar_pagos').remove();
			$contenido1.find('tr.uno').find('tbody tr.transferencia').hide();
			$contenido1.find('tr.uno').find('tbody tr.registros').hide();
			$contenido1.find('tr.uno').find('tbody tr.tarjeta').hide();
			$contenido1.find('tr.uno').find('tbody tr.pago').hide();
			$contenido1.find('tr.uno').find('tbody tr.monto').hide();
			$contenido1.find('tr.uno').find('tbody tr.monto_total').hide();
			$contenido1.find('tr.uno').find('tbody tr.moneda').hide();
			$contenido1.find('tr.uno').find('tbody tr.moneda').find('textarea[name=observaciones]').text('');
			$contenido1.find('tr.uno').find('tbody tr.moneda').find('textarea[name=observaciones]').hide();
			
			$contenido1.find('tr.uno').find('tbody tr.anticipo').remove();
			$contenido1.find('tr.registros').hide();
			$contenido1.find('tr.transaccion').remove();
			$contenido1.find('tr.facturas').remove();
			$contenido1.find('tr.anticipo').remove();
			$contenido1.find('tr.uno').find('tbody tr.pago').find('td:eq(3)').find('div span').remove();
			$('#forma-carteras-window').find('#submit_pago').hide();
			$('#forma-carteras-window').find('#submit_cancel').hide();
			
			
			var input_json = document.location.protocol + '//' + document.location.host + '/'+controller+'/get_tipos_movimiento.json';
			$arreglo = {};
			$.post(input_json,$arreglo,function(entry){
				//carga select con todas las monedas
				$contenido1.find('select[name=tipo_mov]').children().remove();
				var tipo_mov_hmtl = '<option value="0" selected="yes">Seleccione una opcion</option>';
				$.each(entry['Tiposmov'],function(entryIndex,tm){
						tipo_mov_hmtl += '<option value="' + tm['id'] + '"  >' + tm['titulo'] + '</option>';
				});
				$contenido1.find('select[name=tipo_mov]').append(tipo_mov_hmtl);
				
			});//termina llamada json
			
			
		};
	
	
        
        
        
        
	//nuevo pago
	$new_pago.click(function(event){
		event.preventDefault();
		var id_to_show = 0;
		
		$(this).modalPanel_carteras();
		
		var form_to_show = 'formaPagos00';
		$('#' + form_to_show).each (function(){this.reset();});
		var $forma_selected = $('#' + form_to_show).clone();
		$forma_selected.attr({id : form_to_show + id_to_show});
		//var accion = "getCotizacion";
		
		$forma_selected.prependTo('#forma-carteras-window');
		$forma_selected.find('.panelcito_modal').attr({id : 'panelcito_modal' + id_to_show , style:'display:table'});
		
		$('#forma-carteras-window').css({"margin-left": -350,"margin-top": -200});
		$('#forma-carteras-window').find('.carteras_div_one').css({'height':'190px'});
        $forma_selected.find('tr.uno').find('tbody tr.pago').hide();
        $forma_selected.find('tr.uno').find('tbody tr.cheque').hide();
        $forma_selected.find('tr.uno').find('tbody tr.monto').hide();
        
		//$contenido2.find('tbody tr:eq(1)').find('td').append('<span>'+$cliente.val()+'</span>');
		//$select_tipo_movimiento = $contenido2.find('select[name^=data[tipo_mov]]');
		//var $monto_pago = $contenido2.find('input[name^=data[monto_pago]]');
		
		
		$tabs_li_funxionalidad();
		
		//var json_string = document.location.protocol + '//' + document.location.host + '/' + controller + '/' + accion + '/' + id_to_show + '/out.json';
		var input_json = document.location.protocol + '//' + document.location.host + '/'+controller+'/getCartera.json';
		$arreglo = {'id':id_to_show,
					'iu':$('#lienzo_recalculable').find('input[name=iu]').val()
					};
		
		
        
		var $id_pago = $('#forma-carteras-window').find('input[name=id_pago]');
		var $total_tr = $('#forma-carteras-window').find('input[name=total_tr]');
		var $select_tipo_movimiento = $('#forma-carteras-window').find('select[name=tipo_mov]');
		var $campo_banco_deposito = $('#forma-carteras-window').find('div.ficha_deposito').find('select[name=forma_banco_kemikal]');
		var $select_forma_pago = $('#forma-carteras-window').find('select[name=forma_pago]');
		var $select_banco = $('#forma-carteras-window').find('select[name=bancos]');
		var $select_moneda = $('#forma-carteras-window').find('select[name=moneda]');
		var $tipo_cambio = $('#forma-carteras-window').find('input[name=tipo_cambio]');
		
		var $busca_cliente = $('#forma-carteras-window').find('a[href*=busca_cliente]');
		var $razon_cliente = $('#forma-carteras-window').find('input[name=cliente]');
		var $nocliente = $('#forma-carteras-window').find('input[name=nocliente]');
		var $rfccliente = $('#forma-carteras-window').find('input[name=rfccliente]');
		
		var $cerrar_plugin = $('#forma-carteras-window').find('#close');
		var $cancelar_plugin = $('#forma-carteras-window').find('#boton_cancelar');
		var $submit_registrar_pago = $('#forma-carteras-window').find('#submit_pago');
		var $registra_anticipo = $('#forma-carteras-window').find('#registra_anticipo');
        var $submit_registrar_cancelacion = $('#forma-carteras-window').find('#submit_cancel');
        
		//deshabilitar select tipo movimiento, se habilita hasta que se seleccione un cliente
		$select_tipo_movimiento.attr('disabled','-1');
        
        $submit_registrar_pago.hide();
        $submit_registrar_cancelacion.hide();
        $registra_anticipo.hide();
		//$rfccliente.css({'background' : '#F0F0F0'});
		$nocliente.focus();
		
		var respuestaProcesada = function(data){
			if ( data['success'] == "true" ){
				jAlert("La cotizacion se guardó con exito", 'Atencion!');
				var remove = function() {$(this).remove();};
				$('#forma-carteras-overlay').fadeOut(remove);
				$get_datos_grid();
			}else{
				// Desaparece todas las interrogaciones si es que existen
				$('#forma-carteras-window').find('div.interrogacion').css({'display':'none'});
				
				var valor = data['success'].split('___');
				//muestra las interrogaciones
				for (var element in valor){
					tmp = data['success'].split('___')[element];
					longitud = tmp.split(':');
					
					if( longitud.length > 1 ){
						$('#forma-carteras-window').find('img[rel=warning_' + tmp.split(':')[0] + ']')
						.parent()
						.css({'display':'block'})
						.easyTooltip({tooltipId: "easyTooltip2",content: tmp.split(':')[1]});
					}
				}
			}
		}
		
                
                
		var options = {dataType :  'json', success : respuestaProcesada};
		$forma_selected.ajaxForm(options);
		
		//$.getJSON(json_string,function(entry){
		$.post(input_json,$arreglo,function(entry){
			
			//carga select denominacion con todos los tipos de movimiento
			$select_tipo_movimiento.children().remove();
			var tipo_mov_hmtl = '<option value="0" selected="yes">Seleccione una opcion</option>';
			$.each(entry['tipo_mov'],function(entryIndex,mov){
				tipo_mov_hmtl += '<option value="' + mov['id'] + '"  >' + mov['titulo'] + '</option>';
			});
			$select_tipo_movimiento.append(tipo_mov_hmtl);
			
			//carga select con todas las monedas
			$select_moneda.children().remove();
			var tipo_moneda_hmtl="";// = '<option value="0" selected="yes">Seleccione una opcion</option>';
			$.each(entry['Monedas'],function(entryIndex,moneda){
				tipo_moneda_hmtl += '<option value="' + moneda['id'] + '"  >' + moneda['descripcion'] + '</option>';
			});
			$select_moneda.append(tipo_moneda_hmtl);
			
			//carga select con todas las formas de pago
			$select_forma_pago.children().remove();
			var forma_pago_hmtl="";// = '<option value="0" selected="yes">Seleccione una opcion</option>';
			$.each(entry['Formaspago'],function(entryIndex,fpago){
				forma_pago_hmtl += '<option value="' + fpago['titulo'] + '"  >' + fpago['titulo'] + '</option>';
			});
			$select_forma_pago.append(forma_pago_hmtl);
			
			
			//carga select con todos los bancos
			$select_banco.children().remove();
			var banco_hmtl="";// = '<option value="0" selected="yes">Seleccione una opcion</option>';
			$.each(entry['Bancos'],function(entryIndex,banco){
				banco_hmtl += '<option value="' + banco['id'] + '"  >' + banco['titulo'] + '</option>';
			});
			$select_banco.append(banco_hmtl);
		
			//carga select con todos los bancos de kemikal
			$campo_banco_deposito.children().remove();
			var banco_k_hmtl="";// = '<option value="0" selected="yes">Seleccione una opcion</option>';
			$.each(entry['Bancos_kemikal'],function(entryIndex,bancok){
				banco_k_hmtl += '<option value="' + bancok['id'] + '"  >' + bancok['titulo'] + '</option>';
			});
			$campo_banco_deposito.append(banco_k_hmtl);
			
			
			$tipo_cambio.val(entry['Tipocambio'][0]['valor_tipo_cambio']);
			
			
			
			
			//buscador de clientes
			$busca_cliente.click(function(event){
				event.preventDefault();
				//1 para que retorne datos en buscador para realizar pagos
				$busca_clientes(1, $nocliente.val(), $razon_cliente.val(), $select_moneda, $select_forma_pago, $select_banco, $campo_banco_deposito, entry['Monedas'], entry['Formaspago'], entry['Bancos'], entry['Bancos_kemikal']);
			});
			
			//asignar evento keypress al campo Razon Social del cliente
			$(this).aplicarEventoKeypressEjecutaTrigger($razon_cliente, $busca_cliente);
			
			
			//$select_tipo_movimiento, $select_moneda, $select_forma_pago, $select_banco, $campo_banco_deposito, arrayTipoMov, arrayMonedas, arrayFormasPago, arrayBancos, arrayBancosDeposito
			
			$nocliente.keypress(function(e){
				if(e.which == 13){
					
					var input_json2 = document.location.protocol + '//' + document.location.host + '/'+controller+'/getDataByNoClient.json';
					$arreglo2 = {'no_control':$nocliente.val(),  'iu':$('#lienzo_recalculable').find('input[name=iu]').val() };
					
					$.post(input_json2,$arreglo2,function(entry2){
						
						if(parseInt(entry2['Cliente'].length) > 0 ){
							var idCliente = entry2['Cliente'][0]['id'];
							var rfc = entry2['Cliente'][0]['rfc'];
							var noControl = entry2['Cliente'][0]['numero_control'];
							var razonSocial = entry2['Cliente'][0]['razon_social'];
							
							//llamada a la funcion para agregar los datos del cliente
							$agregarDatosClienteSeleccionado(idCliente, rfc, noControl, razonSocial, $select_moneda, $select_forma_pago, $select_banco, $campo_banco_deposito, entry['Monedas'], entry['Formaspago'], entry['Bancos'], entry['Bancos_kemikal']);
							
						}else{
							$('#forma-carteras-window').find('input[name=identificador_cliente]').val(0);
							$('#forma-carteras-window').find('input[name=rfccliente]').val('');
							$('#forma-carteras-window').find('input[name=nocliente]').val('');
							$('#forma-carteras-window').find('input[name=cliente]').val('');
							
							jAlert('N&uacute;mero de cliente desconocido.', 'Atencion!', function(r) { 
								$('#forma-carteras-window').find('input[name=nocliente]').focus(); 
							});
						}
					},"json");//termina llamada json
					
					return false;
				}
			});
			
		});//termina new
		
		
		
		
		
		
		/*
		$razon_cliente.keypress(function(e){
			if(e.which==13 ) {
				$busca_cliente.trigger('click');
			}
		});
		*/
		
        //seleccionar tipo de movimiento
		$select_tipo_movimiento.change(function(){
			var tipo_movimiento = $(this).val();
			tipos_de_movimiento($forma_selected,tipo_movimiento,$submit_registrar_pago,$registra_anticipo,$submit_registrar_cancelacion);
		});
        
        
        /*
		$select_moneda.change(function(){
			calcular_monto($(this).val());
			if(obtener_folios()){
				$select_moneda.attr({'disabled':true});
			}else{
				$select_moneda.attr({'disabled':false});
			}
		});
		*/
		
		$select_forma_pago.change(function(){
			var forma_pago = $(this).val();
			
			$forma_selected.find('tbody tr.cheque').find('input[name=num_cheque]').val('');
			$forma_selected.find('tbody tr.cheque').find('input[name=referencia]').val('');
			$forma_selected.find('tbody tr.cheque').find('input[name=num_tarjeta]').val('');
			
			$forma_selected.find('tbody tr.cheque').hide();							
			$forma_selected.find('tbody tr.transferencia').hide();
			$forma_selected.find('tbody tr.tarjeta').hide();
			$('#forma-carteras-window').find('.carteras_div_one').css({'height':'513px'});
			if(forma_pago=='Cheque'){
				$forma_selected.find('tbody tr.cheque').show();
				$('#forma-carteras-window').find('.carteras_div_one').css({'height':'531px'});
			}
			if(forma_pago=='Transferencia'){
				$forma_selected.find('tbody tr.transferencia').show();
				$('#forma-carteras-window').find('.carteras_div_one').css({'height':'531px'});
			}
			if(forma_pago=='Tarjeta'){
				$forma_selected.find('tbody tr.tarjeta').show();
				$('#forma-carteras-window').find('.carteras_div_one').css({'height':'531px'});
			}
		});
		
		/*
		$contenido2.find('input[value=Salir]').click(function (){
			//alert("Estas saliendo");
			if($select_tipo_movimiento.val()==0) {
				$('#dialogo-cartera-overlay').remove();
				$campo_monto_ant_selecc.attr({ 'value' : 0});
				$boton_limpia_campos.trigger('click');
				$boton_busca_pago.trigger('click');//recarga grid
			}else{
				movimiento0($forma_selected);
			}
		}); 
		*/
		
		
        /*
		$submit_actualizar.bind('click',function(){
			var trCount = $("tr", $grid_productos).size();
			$total_tr.val(trCount);
			if(parseInt(trCount) > 0){                        
				return true;
			}else{
				jAlert("No hay datos para actualizar", 'Atencion!');
				return false;
			}
		});
		*/
		
		//cerrar plugin
		$cerrar_plugin.bind('click',function(){
			var remove = function() {$(this).remove();};
			$('#forma-carteras-overlay').fadeOut(remove);
		});
		
		
		//boton cancelar y cerrar plugin
		$cancelar_plugin.click(function(event){
			var remove = function() {$(this).remove();};
			$('#forma-carteras-overlay').fadeOut(remove);
		});
		
	});
	
	
	
	var carga_formaPagos00_for_datagrid00 = function(id_to_show, accion_mode){
		//aqui entra para generar pdf del pago
                //alert("accion_mode:"+accion_mode);
		if(accion_mode == 'genera_pdf'){
                    //alert("aquí");
			var iu = $('#lienzo_recalculable').find('input[name=iu]').val();
			var input_json = document.location.protocol + '//' + document.location.host + '/' + controller + '/getPdfReporteAplicacionPago/'+id_to_show+'/'+iu+'/out.json';
			window.location.href=input_json;
		}else if(accion_mode == 'genera_pdfFactura'){
                    //alert("aquí");
			var iu = $('#lienzo_recalculable').find('input[name=iu]').val();
			var input_json = document.location.protocol + '//' + document.location.host + '/' + controller + '/getPdfFactura/'+id_to_show+'/'+iu+'/outPdfFactura.json';
			window.location.href=input_json;                   
                }else if(accion_mode == 'genera_xmlFactura'){
			var iu = $('#lienzo_recalculable').find('input[name=iu]').val();
			var input_json = document.location.protocol + '//' + document.location.host + '/' + controller + '/getXmlFactura/'+id_to_show+'/'+iu+'/outXmlFactura.json';
			window.location.href=input_json;                    
                }
	}
    
    
    
    
    $get_datos_grid = function(){
        var input_json = document.location.protocol + '//' + document.location.host + '/'+controller+'/getPagos.json';
        
        var iu = $('#lienzo_recalculable').find('input[name=iu]').val();
        
        $arreglo = {'orderby':'id','desc':'DESC','items_por_pag':15,'pag_start':1,'display_pag':20,'input_json':'/'+controller+'/getPagos.json', 'cadena_busqueda':$cadena_busqueda, 'iu':iu}
		
        $.post(input_json,$arreglo,function(data){
			
            //pinta_grid
            $.fn.tablaOrdenableCarteras(data,$('#lienzo_recalculable').find('.tablesorter'),carga_formaPagos00_for_datagrid00);
			
            //resetea elastic, despues de pintar el grid y el slider
            Elastic.reset(document.getElementById('lienzo_recalculable'));
        },"json");
    }
    
    $get_datos_grid();
    
    
});

function getPDF(item) {
    alert("Ok");
}
