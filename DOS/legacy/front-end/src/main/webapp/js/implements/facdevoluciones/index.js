$(function() {
	String.prototype.toCharCode = function(){
	    var str = this.split(''), len = str.length, work = new Array(len);
	    for (var i = 0; i < len; ++i){
			work[i] = this.charCodeAt(i);
	    }
	    return work.join(',');
	};
	
	var arrayAgentes;
	
	$('#header').find('#header1').find('span.emp').text($('#lienzo_recalculable').find('input[name=emp]').val());
	$('#header').find('#header1').find('span.suc').text($('#lienzo_recalculable').find('input[name=suc]').val());
    var $username = $('#header').find('#header1').find('span.username');
	$username.text($('#lienzo_recalculable').find('input[name=user]').val());
	
	var $contextpath = $('#lienzo_recalculable').find('input[name=contextpath]');
	var controller = $contextpath.val()+"/controllers/facdevoluciones";
    
    //Barra para las acciones
    $('#barra_acciones').append($('#lienzo_recalculable').find('.table_acciones'));
    $('#barra_acciones').find('.table_acciones').css({'display':'block'});
    //var $new_prefactura = $('#barra_acciones').find('.table_acciones').find('a[href*=new_item]');
	var $visualiza_buscador = $('#barra_acciones').find('.table_acciones').find('a[href*=visualiza_buscador]');
	
	$('#barra_acciones').find('.table_acciones').find('#nItem').mouseover(function(){
		$(this).removeClass("onmouseOutNewItem").addClass("onmouseOverNewItem");
	});
	$('#barra_acciones').find('.table_acciones').find('#nItem').mouseout(function(){
		$(this).removeClass("onmouseOverNewItem").addClass("onmouseOutNewItem");
	});
	
	$('#barra_acciones').find('.table_acciones').find('#vbuscador').mouseover(function(){
		$(this).removeClass("onmouseOutVisualizaBuscador").addClass("onmouseOverVisualizaBuscador");
	});
	$('#barra_acciones').find('.table_acciones').find('#vbuscador').mouseout(function(){
		$(this).removeClass("onmouseOverVisualizaBuscador").addClass("onmouseOutVisualizaBuscador");
	});
	
	//aqui va el titulo del catalogo
	$('#barra_titulo').find('#td_titulo').append('Devoluci&oacute;n de Mercanc&iacute;a');
	
	//barra para el buscador 
	//$('#barra_buscador').css({'height':'0px'});
	$('#barra_buscador').append($('#lienzo_recalculable').find('.tabla_buscador'));
	//$('#barra_buscador').find('.tabla_buscador').css({'display':'none'});
	//$('#barra_buscador').hide();
	
	
	
	var $cadena_busqueda = "";
	var $busqueda_factura = $('#barra_buscador').find('.tabla_buscador').find('input[name=busqueda_factura]');
	var $busqueda_cliente = $('#barra_buscador').find('.tabla_buscador').find('input[name=busqueda_cliente]');
	var $busqueda_fecha_inicial = $('#barra_buscador').find('.tabla_buscador').find('input[name=busqueda_fecha_inicial]');
	var $busqueda_fecha_final = $('#barra_buscador').find('.tabla_buscador').find('input[name=busqueda_fecha_final]');
	var $busqueda_codigo = $('#barra_buscador').find('.tabla_buscador').find('input[name=busqueda_codigo]');
	var $busqueda_producto = $('#barra_buscador').find('.tabla_buscador').find('input[name=busqueda_producto]');
	var $busqueda_select_agente = $('#barra_buscador').find('.tabla_buscador').find('select[name=busqueda_select_agente]');
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
		valor_retorno += "factura" + signo_separador + $busqueda_factura.val() + "|";
		valor_retorno += "cliente" + signo_separador + $busqueda_cliente.val() + "|";
		valor_retorno += "fecha_inicial" + signo_separador + $busqueda_fecha_inicial.val() + "|";
		valor_retorno += "fecha_final" + signo_separador + $busqueda_fecha_final.val()+ "|";
		valor_retorno += "codigo" + signo_separador + $busqueda_codigo.val() + "|";
		valor_retorno += "producto" + signo_separador + $busqueda_producto.val() + "|";
		valor_retorno += "agente" + signo_separador + $busqueda_select_agente.val();
		return valor_retorno;
	};
    
	cadena = to_make_one_search_string();
	$cadena_busqueda = cadena.toCharCode();
	
	$buscar.click(function(event){
		//event.preventDefault();
		cadena = to_make_one_search_string();
		$cadena_busqueda = cadena.toCharCode();
		$get_datos_grid();
	});
	
	var input_json_lineas = document.location.protocol + '//' + document.location.host + '/'+controller+'/getAgentesParaBuscador.json';
	$arreglo = {'iu':$('#lienzo_recalculable').find('input[name=iu]').val()}
	$.post(input_json_lineas,$arreglo,function(data){
		//Alimentando los campos select_agente
		$busqueda_select_agente.children().remove();
		var agente_hmtl = '<option value="0">[-Seleccionar Agente-]</option>';
		$.each(data['Agentes'],function(entryIndex,agente){
			agente_hmtl += '<option value="' + agente['id'] + '" >' + agente['nombre_vendedor'] + '</option>';
		});
		$busqueda_select_agente.append(agente_hmtl);
		
		//asignamos el arreglo a una variable para utilizarla mas adelante
		arrayAgentes = data['Agentes'];
	});
	
	$limpiar.click(function(event){
		$busqueda_factura.val('');
		$busqueda_cliente.val('');
		$busqueda_fecha_inicial.val('');
		$busqueda_fecha_final.val('');
		$busqueda_codigo.val('');
		$busqueda_producto.val('');
		
		//Recargar select de agentes
		$busqueda_select_agente.children().remove();
		var agente_hmtl = '<option value="0">[-Seleccionar Agente-]</option>';
		$.each(arrayAgentes,function(entryIndex,agente){
			agente_hmtl += '<option value="' + agente['id'] + '" >' + agente['nombre_vendedor'] + '</option>';
		});
		$busqueda_select_agente.append(agente_hmtl);
		
		//asignar el enfoque al limpiar campos
		$busqueda_factura.focus();
	});
	
	
	
	TriggerClickVisializaBuscador = 0;
	
	//visualizar  la barra del buscador
	$visualiza_buscador.click(function(event){
		event.preventDefault();
		
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
		//asignar el enfoque a visualizar el buscador
		$busqueda_factura.focus();
	});
	
	//aplicar evento Keypress para que al pulsar enter ejecute la busqueda
	$(this).aplicarEventoKeypressEjecutaTrigger($busqueda_factura, $buscar);
	$(this).aplicarEventoKeypressEjecutaTrigger($busqueda_cliente, $buscar);
	$(this).aplicarEventoKeypressEjecutaTrigger($busqueda_codigo, $buscar);
	$(this).aplicarEventoKeypressEjecutaTrigger($busqueda_producto, $buscar);
	$(this).aplicarEventoKeypressEjecutaTrigger($busqueda_fecha_inicial, $buscar);
	$(this).aplicarEventoKeypressEjecutaTrigger($busqueda_fecha_final, $buscar);
	$(this).aplicarEventoKeypressEjecutaTrigger($busqueda_select_agente, $buscar);
	

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
        
        
	
	$tabs_li_funxionalidad = function(){
            var $select_prod_tipo = $('#forma-facdevoluciones-window').find('select[name=prodtipo]');
		$('#forma-facdevoluciones-window').find('#submit').mouseover(function(){
			$('#forma-facdevoluciones-window').find('#submit').removeAttr("src").attr("src","../../img/modalbox/bt1.png");
			//$('#forma-facdevoluciones-window').find('#submit').css({backgroundImage:"url(../../img/modalbox/bt1.png)"});
		})
		$('#forma-facdevoluciones-window').find('#submit').mouseout(function(){
			$('#forma-facdevoluciones-window').find('#submit').removeAttr("src").attr("src","../../img/modalbox/btn1.png");
			//$('#forma-facdevoluciones-window').find('#submit').css({backgroundImage:"url(../../img/modalbox/btn1.png)"});
		})
		$('#forma-facdevoluciones-window').find('#boton_cancelar').mouseover(function(){
			$('#forma-facdevoluciones-window').find('#boton_cancelar').css({backgroundImage:"url(../../img/modalbox/bt2.png)"});
		})
		$('#forma-facdevoluciones-window').find('#boton_cancelar').mouseout(function(){
			$('#forma-facdevoluciones-window').find('#boton_cancelar').css({backgroundImage:"url(../../img/modalbox/btn2.png)"});
		})
		
		$('#forma-facdevoluciones-window').find('#close').mouseover(function(){
			$('#forma-facdevoluciones-window').find('#close').css({backgroundImage:"url(../../img/modalbox/close_over.png)"});
		})
		$('#forma-facdevoluciones-window').find('#close').mouseout(function(){
			$('#forma-facdevoluciones-window').find('#close').css({backgroundImage:"url(../../img/modalbox/close.png)"});
		})
		
		$('#forma-facdevoluciones-window').find(".contenidoPes").hide(); //Hide all content
		$('#forma-facdevoluciones-window').find("ul.pestanas li:first").addClass("active").show(); //Activate first tab
		$('#forma-facdevoluciones-window').find(".contenidoPes:first").show(); //Show first tab content
		
		//On Click Event
		$('#forma-facdevoluciones-window').find("ul.pestanas li").click(function() {
			$('#forma-facdevoluciones-window').find(".contenidoPes").hide();
			$('#forma-facdevoluciones-window').find("ul.pestanas li").removeClass("active");
			var activeTab = $(this).find("a").attr("href");
			$('#forma-facdevoluciones-window').find( activeTab , "ul.pestanas li" ).fadeIn().show();
			$(this).addClass("active");
			return false;
		});
	}
	
	
	
	var quitar_comas= function($valor){
		$valor = $valor+'';
		return $valor.split(',').join('');
	}
	
	
	/*
	//aplica evento Click a todos los checkbox
	var aplicar_eventos_a_campos_del_grid = function($tabla){
		$tabla.find('input[name=micheck]').each(function(){
			
			
			$(this).click(function(event){
				if(this.checked){
					$(this).parent().find('input[name=seleccionado]').val("1");
				}else{
					$(this).parent().find('input[name=seleccionado]').val("0");
				}
			});
		});
     }
	*/
	
	
	
	$permitir_solo_numeros = function($campo_input ){
		$campo_input.keypress(function(e){
			// Permitir  numeros, borrar, suprimir, TAB, puntos, comas
			if (e.which == 8 || e.which == 46 || e.which==13 || e.which == 0 || (e.which >= 48 && e.which <= 57 )) {
				return true;
			}else {
				return false;
			}
		});
	}
	
	

	$aplicar_evento_focus = function($campo_input ){
		//elimina cero al hacer clic sobre el campo
		$campo_input.focus(function(e){
			if(parseFloat($campo_input.val())<1){
				$campo_input.val('');
			}
		});
	}
	
	$aplicar_evento_blur = function($campo_input_cant_dev, $campo_input_cant ){
		$campo_input_cant_dev.blur(function(){
			if($campo_input_cant_dev.val()=="" || parseFloat($campo_input_cant_dev.val())==0){
				$campo_input_cant_dev.val(parseFloat(0.00).toFixed(2));//si el campo esta en blanco, pone cero
			}else{
				
				//valida  que la cantidad a devolver no sea mayor a la cantidad vendida en la partida
				if( parseFloat($campo_input_cant_dev.val()) > parseFloat($campo_input_cant.val())  ){
					jAlert("La Cantidad a devolver No debe ser mayor a la cantidad de la partida.\n\nCantidad partida: "+$campo_input_cant.val()+"\nCantidad devoluci&oacute;n: "+$campo_input_cant_dev.val()+"\n\nSe ha asignado por default la cantidad de la partida.", 'Atencion!');
					$campo_input_cant_dev.val(parseFloat( $campo_input_cant.val() ).toFixed(2));
				}else{
					$campo_input_cant_dev.val(parseFloat( $campo_input_cant_dev.val() ).toFixed(2));
				}
			}
		});
	}
	
	
	
	
	
	var contar_seleccionados= function($tabla){
		var seleccionados=0;
		
		$tabla.find('input[name=micheck]').each(function(){
			if(this.checked){
				seleccionados = parseInt(seleccionados) + 1;
			}
		});
		return seleccionados;
	}
	
	
	//Funcion que construye cadena para el concepto de la devolucion
	var construir_cadena_concepto= function($tabla){
		var primero=0;
		var cadena_concepto="";
		$tabla.find('input[name=micheck]').each(function(){
			if(this.checked){
				if(parseInt(primero)==0){
					cadena_concepto = "DEVOLUCION DE "+ $(this).parent().parent().find('input[name=cantidad_dev]').val() +" "+ $(this).parent().parent().find('#uni').val() + "S " + $(this).parent().parent().find('#nom').val();
				}else{
					cadena_concepto += ", "+ $(this).parent().parent().find('input[name=cantidad_dev]').val() +" "+ $(this).parent().parent().find('#uni').val() + "S " + $(this).parent().parent().find('#nom').val();
				}
				primero = parseInt(primero) + 1;
			}
		});
		return cadena_concepto;
	}
	
	
	//aplica evento a  campos del grid
	var aplicar_eventos_a_campos_del_grid = function($tabla){
		$tabla.find('tr').each(function(){
			var $cantidad_partida = $(this).find('input[name=cantidad]');
			var $costo_unitario = $(this).find('input[name=costo]');
			var $cantidad_devolucion = $(this).find('input[name=cantidad_dev]');
			var $importe_devolucion = $(this).find('input[name=importe_dev]');
			var $tasa_iva = $(this).find('input[name=valor_imp]');
			var $importe_iva = $(this).find('input[name=importe_imp_dev]');
			var $tasa_ieps = $(this).find('input[name=tasaIeps]');
			var $importe_ieps = $(this).find('input[name=importe_ieps_dev]');
			
			//aplicar click a los campso check del grid
			$(this).find('input[name=micheck]').click(function(event){
				if( this.checked ){
					$(this).parent().find('input[name=seleccionado]').val("1");
					$(this).parent().parent().find('input[name=cantidad_dev]').css({'background' : '#ffffff'});
					$(this).parent().parent().find('input[name=cantidad_dev]').attr("readonly", false);//habilitar campo
					$(this).parent().parent().find('input[name=cantidad_dev]').focus();
					$('#forma-facdevoluciones-window').find('textarea[name=concepto]').val(construir_cadena_concepto($tabla));
				}else{
					$(this).parent().find('input[name=seleccionado]').val("0");
					$(this).parent().parent().find('input[name=importe_dev]').val(0);
					$(this).parent().parent().find('input[name=importe_imp_dev]').val(0);
					$(this).parent().parent().find('input[name=importe_ieps_dev]').val(0);
					$(this).parent().parent().find('input[name=cantidad_dev]').val(parseFloat(0).toFixed(2));
					$(this).parent().parent().find('input[name=cantidad_dev]').css({'background':'#dddddd'});
					$(this).parent().parent().find('input[name=cantidad_dev]').attr("readonly", true);//deshabilitar campo
					
					$('#forma-facdevoluciones-window').find('textarea[name=concepto]').val(construir_cadena_concepto($tabla));
				}
				
				$calcula_totales_nota_credito();
				
			});
			
			$permitir_solo_numeros( $cantidad_devolucion );
			$aplicar_evento_focus( $cantidad_devolucion );
			//$aplicar_evento_blur( $(this).find('input[name=cantidad_dev]'), $(this).find('input[name=cantidad]') );
			
			$cantidad_devolucion.blur(function(){
				var tasaIeps = 0;
				if($tasa_ieps.val().trim()!=''){
					if(parseFloat($tasa_ieps.val())>0){
						tasaIeps = parseFloat($tasa_ieps.val())/100;
					}
				}
				
				if($cantidad_devolucion.val()=="" || parseFloat($cantidad_devolucion.val())==0){
					$cantidad_devolucion.val(parseFloat(0.00).toFixed(2));//si el campo esta en blanco, pone cero
				}else{
					
					//valida  que la cantidad a devolver no sea mayor a la cantidad vendida en la partida
					if( parseFloat($cantidad_devolucion.val()) > parseFloat($cantidad_partida.val())  ){
						jAlert("La Cantidad a devolver No debe ser mayor a la cantidad de la partida.\n\nCantidad partida: "+$cantidad_partida.val()+"\nCantidad devoluci&oacute;n: "+$cantidad_devolucion.val()+"\n\nSe ha asignado por default la cantidad de la partida.", 'Atencion!');
						$cantidad_devolucion.val(parseFloat( $cantidad_partida.val() ).toFixed(2));
					}else{
						$cantidad_devolucion.val(parseFloat( $cantidad_devolucion.val() ).toFixed(2));
					}
					
					//Calcula el importe de la devolucion
					$importe_devolucion.val( parseFloat(parseFloat($cantidad_devolucion.val()) * parseFloat($costo_unitario.val())).toFixed(4) );
					
					//Calcular el importe del IEPS
					$importe_ieps.val(parseFloat(parseFloat($importe_devolucion.val()) * parseFloat(tasaIeps)).toFixed(4));
					
					//Calcula el iva de la partida
					$importe_iva.val( parseFloat(parseFloat($importe_devolucion.val()) + parseFloat($importe_ieps.val())) * parseFloat($tasa_iva.val()) );
					
					$('#forma-facdevoluciones-window').find('textarea[name=concepto]').val(construir_cadena_concepto($tabla));
				}
				$calcula_totales_nota_credito();
			});
			
		});
     }
	
	
	
	
	//calcula totales de la Nota de Credito(subtotal, impuesto, retencion, total)
	$calcula_totales_nota_credito = function(){
		var $subtotal_nota = $('#forma-facdevoluciones-window').find('input[name=subtotal_nota]');
		var $impuesto_nota = $('#forma-facdevoluciones-window').find('input[name=impuesto_nota]');
		var $impuesto_retenido_nota = $('#forma-facdevoluciones-window').find('input[name=impuesto_retenido_nota]');
		var $total_nota = $('#forma-facdevoluciones-window').find('input[name=total_nota]');
		var $empresa_immex = $('#forma-facdevoluciones-window').find('input[name=empresa_immex]');
		//var $tasa_ret_immex = $('#forma-facdevoluciones-window').find('input[name=tasa_ret_immex]');
		var $tasa_retencion = $('#forma-facdevoluciones-window').find('input[name=tasa_retencion]');
		var $monto_ieps = $('#forma-facdevoluciones-window').find('input[name=ieps]');
		var $ieps_nota = $('#forma-facdevoluciones-window').find('input[name=ieps_nota]');
		
		var $grid_productos = $('#forma-facdevoluciones-window').find('#grid_productos');
		
		//Sumar importes antes de impuestos
		var sumaSubTotal = 0;
		//Sumar los importes del IEPS
		var sumaImporteIeps=0;
		//Sumar importes del IVA
		var sumaImpuesto = 0;
		//Monto del Impuesto retenido de acuerdo a la tasa de retencion immex
		var impuestoRetenido = 0; 
		//suma del subtotal + sumaImporteIeps + totalImpuesto - impuestoRetenido
		var sumaTotal = 0;
		
		$grid_productos.find('tr').each(function (index){
			sumaSubTotal = parseFloat(sumaSubTotal) + parseFloat( $(this).find('input[name=importe_dev]').val() );
			sumaImporteIeps = parseFloat(sumaImporteIeps) + parseFloat( $(this).find('input[name=importe_ieps_dev]').val() );
			sumaImpuesto = parseFloat(sumaImpuesto) + parseFloat( $(this).find('input[name=importe_imp_dev]').val() );
		});
		
		
		
		//calcular  la tasa de retencion IMMEX
		impuestoRetenido = parseFloat(sumaSubTotal) * parseFloat($tasa_retencion.val());
		
		//calcula el total sumando el subtotal y el impuesto
		sumaTotal = parseFloat(sumaSubTotal) + parseFloat(sumaImporteIeps) + parseFloat(sumaImpuesto) - parseFloat(impuestoRetenido);
		
		//redondea a dos digitos el  subtotal y lo asigna  al campo subtotal
		$subtotal_nota.val($(this).agregar_comas(  parseFloat(sumaSubTotal).toFixed(2)  ));
		
		$ieps_nota.val($(this).agregar_comas(  parseFloat(sumaImporteIeps).toFixed(2)  ));
		
		//redondea a dos digitos el impuesto y lo asigna al campo impuesto
		$impuesto_nota.val($(this).agregar_comas(  parseFloat(sumaImpuesto).toFixed(2)  ));
		//redondea a dos digitos el impuesto y lo asigna al campo retencion
		$impuesto_retenido_nota.val($(this).agregar_comas(  parseFloat(impuestoRetenido).toFixed(2)  ));
		//redondea a dos digitos la suma  total y se asigna al campo total
		$total_nota.val($(this).agregar_comas(  parseFloat(sumaTotal).toFixed(2)  ));
		
		
	}//termina calcular totales
	
	
	
	
	
	//ver detalles de una factura
	var carga_formafacdevoluciones00_for_datagrid00Edit = function(id_to_show, accion_mode){
		//aqui entra para eliminar una prefactura
		if(accion_mode == 'cancel'){
			
			var input_json = document.location.protocol + '//' + document.location.host + '/' + controller + '/' + 'logicDelete.json';
			$arreglo = {'id_prefactura':id_to_show,
						'iu':$('#lienzo_recalculable').find('input[name=iu]').val()};
			jConfirm('Realmente desea eliminar  la factura?', 'Dialogo de confirmacion', function(r) {
				if (r){
					$.post(input_json,$arreglo,function(entry){
						if ( entry['success'] == '1' ){
							jAlert("La factura fue eliminada exitosamente", 'Atencion!');
							$get_datos_grid();
						}
						else{
							jAlert("La factura no pudo ser eliminada", 'Atencion!');
						}
					},"json");
				}
			});
			
		}else{
			//aqui  entra para editar un registro
			$('#forma-facdevoluciones-window').remove();
			$('#forma-facdevoluciones-overlay').remove();
            
			var form_to_show = 'formafacdevoluciones00';
			$('#' + form_to_show).each (function(){this.reset();});
			var $forma_selected = $('#' + form_to_show).clone();
			$forma_selected.attr({id : form_to_show + id_to_show});
			
			$(this).modalPanel_facdevoluciones();
			
			$('#forma-facdevoluciones-window').css({"margin-left": -395, 	"margin-top": -220});
			
			$forma_selected.prependTo('#forma-facdevoluciones-window');
			$forma_selected.find('.panelcito_modal').attr({id : 'panelcito_modal' + id_to_show , style:'display:table'});
			
			$tabs_li_funxionalidad();
			
			
			//alert(id_to_show);
			
			if(accion_mode == 'edit'){
				
				var input_json = document.location.protocol + '//' + document.location.host + '/'+controller+'/getFactura.json';
				$arreglo = {'id_factura':id_to_show, 'iu':$('#lienzo_recalculable').find('input[name=iu]').val()};
                                
				var $total_tr = $('#forma-facdevoluciones-window').find('input[name=total_tr]');
				var $id_factura = $('#forma-facdevoluciones-window').find('input[name=id_factura]');
				var $serie_folio = $('#forma-facdevoluciones-window').find('input[name=serie_folio]');
				var $fecha = $('#forma-facdevoluciones-window').find('input[name=fecha]');
				var $folio_pedido = $('#forma-facdevoluciones-window').find('input[name=folio_pedido]');
				var $id_cliente = $('#forma-facdevoluciones-window').find('input[name=id_cliente]');
				var $rfc_cliente = $('#forma-facdevoluciones-window').find('input[name=rfccliente]');
				var $razon_cliente = $('#forma-facdevoluciones-window').find('input[name=razoncliente]');
				var $id_df = $('#forma-facdevoluciones-window').find('input[name=id_df]');
				var $dir_cliente = $('#forma-facdevoluciones-window').find('input[name=dircliente]');
				
				var $select_moneda = $('#forma-facdevoluciones-window').find('select[name=moneda]');
				var $tipo_cambio = $('#forma-facdevoluciones-window').find('input[name=tipo_cambio]');
				var $orden_compra = $('#forma-facdevoluciones-window').find('input[name=orden_compra]');
				
				//var $campo_tc = $('#forma-facdevoluciones-window').find('input[name=tc]');
				var $id_impuesto = $('#forma-facdevoluciones-window').find('input[name=id_impuesto]');
				var $valor_impuesto = $('#forma-facdevoluciones-window').find('input[name=valorimpuesto]');
				var $tasa_retencion = $('#forma-facdevoluciones-window').find('input[name=tasa_retencion]');
				
				var $select_condiciones = $('#forma-facdevoluciones-window').find('select[name=condiciones]');
				var $select_vendedor = $('#forma-facdevoluciones-window').find('select[name=vendedor]');
				
				var $select_metodo_pago = $('#forma-facdevoluciones-window').find('select[name=select_metodo_pago]');
				//var $etiqueta_digit = $('#forma-facdevoluciones-window').find('input[name=digit]');
				//var $no_cuenta = $('#forma-facdevoluciones-window').find('input[name=no_cuenta]');
				var $tipo_cambio_nota = $('#forma-facdevoluciones-window').find('input[name=tipo_cambio_nota]');
				
				
				//grid de productos
				var $grid_productos = $('#forma-facdevoluciones-window').find('#grid_productos');
				//grid de errores
				var $grid_warning = $('#forma-facdevoluciones-window').find('#div_warning_grid').find('#grid_warning');
				
				//Variables para totales de la Factura
				var $subtotal = $('#forma-facdevoluciones-window').find('input[name=subtotal]');
				var $monto_ieps = $('#forma-facdevoluciones-window').find('input[name=ieps]');
				var $impuesto = $('#forma-facdevoluciones-window').find('input[name=impuesto]');
				var $impuesto_retenido = $('#forma-facdevoluciones-window').find('input[name=impuesto_retenido]');
				var $total = $('#forma-facdevoluciones-window').find('input[name=total]');
				var $saldo_fac = $('#forma-facdevoluciones-window').find('input[name=saldo_fac]');
				
				var $concepto = $('#forma-facdevoluciones-window').find('textarea[name=concepto]');
				var $nota_credito = $('#forma-facdevoluciones-window').find('input[name=nota_credito]');
				var $fecha_nc = $('#forma-facdevoluciones-window').find('input[name=fecha_nc]');
				var $select_tmov = $('#forma-facdevoluciones-window').find('select[name=select_tmov]');
				var $registrar_devolucion = $('#forma-facdevoluciones-window').find('#registrar_devolucion');
				
				//variables para totales de la Nota de Credito
				var $subtotal_nota = $('#forma-facdevoluciones-window').find('input[name=subtotal_nota]');
				var $ieps_nota = $('#forma-facdevoluciones-window').find('input[name=ieps_nota]');
				var $impuesto_nota = $('#forma-facdevoluciones-window').find('input[name=impuesto_nota]');
				var $impuesto_retenido_nota = $('#forma-facdevoluciones-window').find('input[name=impuesto_retenido_nota]');
				var $total_nota = $('#forma-facdevoluciones-window').find('input[name=total_nota]');
				
				var $cerrar_plugin = $('#forma-facdevoluciones-window').find('#close');
				var $cancelar_plugin = $('#forma-facdevoluciones-window').find('#boton_cancelar');
				var $submit_actualizar = $('#forma-facdevoluciones-window').find('#submit');
				
				$submit_actualizar.hide();
				$permitir_solo_numeros( $tipo_cambio );
				$permitir_solo_numeros( $tipo_cambio_nota );
				
				//$etiqueta_digit.attr('disabled','-1');
				var respuestaProcesada = function(data){
					if ( data['success'] == "true" ){
						$('#forma-facdevoluciones-window').find('div.interrogacion').css({'display':'none'});
						
						jAlert(data['msj'], 'Atencion!');
						
						if(data['valor']=="true"){
							var remove = function() {$(this).remove();};
							$('#forma-facdevoluciones-overlay').fadeOut(remove);
							$get_datos_grid();
						}
					}else{
						// Desaparece todas las interrogaciones si es que existen
						//$('#forma-facdevoluciones-window').find('.div_one').css({'height':'545px'});//sin errores
						$('#forma-facdevoluciones-window').find('.facdevoluciones_div_one').css({'height':'550px'});//con errores
						$('#forma-facdevoluciones-window').find('div.interrogacion').css({'display':'none'});
						
						$grid_productos.find('#cant').css({'background' : '#ffffff'});
						$grid_productos.find('#cost').css({'background' : '#ffffff'});
						
						$('#forma-facdevoluciones-window').find('#div_warning_grid').css({'display':'none'});
						$('#forma-facdevoluciones-window').find('#div_warning_grid').find('#grid_warning').children().remove();
						
						var valor = data['success'].split('___');
						//muestra las interrogaciones
						for (var element in valor){
							tmp = data['success'].split('___')[element];
							longitud = tmp.split(':');

							if( longitud.length > 1 ){
								$('#forma-facdevoluciones-window').find('img[rel=warning_' + tmp.split(':')[0] + ']')
								.parent()
								.css({'display':'block'})
								.easyTooltip({tooltipId: "easyTooltip2",content: tmp.split(':')[1]});
								
								//alert(tmp.split(':')[0]);
								if(parseInt($("tr", $grid_productos).size())>0){
									for (var i=1;i<=parseInt($("tr", $grid_productos).size());i++){
										if(tmp.split(':')[0]=='cantidad_dev'+i){
											$('#forma-facdevoluciones-window').find('.facdevoluciones_div_one').css({'height':'550px'});
											$('#forma-facdevoluciones-window').find('#div_warning_grid').css({'display':'block'});
											
											if(tmp.split(':')[0].substring(0, 12) == 'cantidad_dev'){
												$grid_productos.find('input[name=cantidad_dev]').eq(parseInt(i) - 1) .css({'background' : '#d41000'});
											}
											
											var tr_warning = '<tr>';
												tr_warning += '<td width="20"><div><IMG SRC="../../img/icono_advertencia.png" ALIGN="top" rel="warning_sku"></td>';
												tr_warning += '<td width="180">';
												tr_warning += '<INPUT TYPE="text" value="'+$grid_productos.find('input[name=nombre' + i + ']').val()+'" class="borde_oculto" readOnly="true" style="width:176px; color:red">';
												tr_warning += '</td>';
												tr_warning += '<td width="270">';
												tr_warning += '<INPUT TYPE="text" value="'+ tmp.split(':')[1] +'" class="borde_oculto" readOnly="true" style="width:270px; color:red">';
												tr_warning += '</td>';
											tr_warning += '</tr>';
											$grid_warning.append(tr_warning);
										}
									}
								}
							}
						}
						
						$grid_warning.find('tr:odd').find('td').css({'background-color' : '#FFFFFF'});
						$grid_warning.find('tr:even').find('td').css({'background-color' : '#e7e8ea'});
					}
				}
				
				var options = {dataType :  'json', success:respuestaProcesada};
				$forma_selected.ajaxForm(options);
				
				//Aqui se cargan los campos al editar
				$.post(input_json,$arreglo,function(entry){
					$id_factura.val(entry['datosFactura'][0]['id']);
					$serie_folio.val(entry['datosFactura'][0]['serie_folio']);
					$fecha.val(entry['datosFactura'][0]['fecha']);
					$folio_pedido.val(entry['datosFactura'][0]['folio_pedido']);
					$id_cliente.val(entry['datosFactura'][0]['cliente_id']);
					$rfc_cliente.val(entry['datosFactura'][0]['rfc']);
					$razon_cliente.val(entry['datosFactura'][0]['razon_social']);
					$id_df.val(entry['datosFactura'][0]['df_id']);
					$dir_cliente.val(entry['datosFactura'][0]['direccion']);
                    $orden_compra.val(entry['datosFactura'][0]['orden_compra']);
					$tasa_retencion.val(entry['datosFactura'][0]['tasa_ret_immex']);
					
					$id_impuesto.val(entry['iva'][0]['id_impuesto']);
					$valor_impuesto.val(entry['iva'][0]['valor_impuesto']);
					
					$subtotal.val( $(this).agregar_comas(entry['datosFactura'][0]['subtotal']));
					$monto_ieps.val( $(this).agregar_comas(entry['datosFactura'][0]['monto_ieps']));
					$impuesto.val( $(this).agregar_comas( entry['datosFactura'][0]['impuesto']) );
					$impuesto_retenido.val( $(this).agregar_comas(entry['datosFactura'][0]['monto_retencion']));
					$total.val($(this).agregar_comas( entry['datosFactura'][0]['total']));
					$saldo_fac.val($(this).agregar_comas( entry['datosFactura'][0]['saldo_fac']));
					$tipo_cambio.val( entry['datosFactura'][0]['tipo_cambio'] );
					
					//Carga select denominacion con todas las monedas
					$select_moneda.children().remove();
					//var moneda_hmtl = '<option value="0">[--   --]</option>';
					var moneda_hmtl = '';
					$.each(entry['Monedas'],function(entryIndex,moneda){
						if(moneda['id'] == entry['datosFactura']['0']['moneda_id']){
							moneda_hmtl += '<option value="' + moneda['id'] + '"  selected="yes">' + moneda['descripcion'] + '</option>';
						}else{
							//moneda_hmtl += '<option value="' + moneda['id'] + '"  >' + moneda['descripcion'] + '</option>';
						}
					});
					$select_moneda.append(moneda_hmtl);
					
					//carga select de vendedores
					$select_vendedor.children().remove();
					var hmtl_vendedor="";
					$.each(entry['Vendedores'],function(entryIndex,vendedor){
						if(entry['datosFactura']['0']['cxc_agen_id'] == vendedor['id']){
							hmtl_vendedor += '<option value="' + vendedor['id'] + '" selected="yes" >' + vendedor['nombre_vendedor'] + '</option>';
						}else{
							//hmtl_vendedor += '<option value="' + vendedor['id'] + '">' + vendedor['nombre_vendedor'] + '</option>';
						}
					});
					$select_vendedor.append(hmtl_vendedor);
					
					//carga select de condiciones
					$select_condiciones.children().remove();
					var hmtl_condiciones="";
					$.each(entry['Condiciones'],function(entryIndex,condicion){
						if(entry['datosFactura']['0']['terminos_id'] == condicion['id']){
							hmtl_condiciones += '<option value="' + condicion['id'] + '" selected="yes" >' + condicion['descripcion'] + '</option>';
						}else{
							//hmtl_condiciones += '<option value="' + condicion['id'] + '">' + condicion['descripcion'] + '</option>';
						}
					});
					$select_condiciones.append(hmtl_condiciones);
					
					
					//carga select de metodos de pago
					$select_metodo_pago.children().remove();
					var hmtl_metodo="";
					$.each(entry['MetodosPago'],function(entryIndex,metodo){
						if(entry['datosFactura']['0']['fac_metodos_pago_id'] == metodo['id']){
							hmtl_metodo += '<option value="' + metodo['id'] + '"  selected="yes">' + metodo['titulo'] + '</option>';
						}else{
							//hmtl_metodo += '<option value="' + metodo['id'] + '"  >' + metodo['titulo'] + '</option>';
						}
					});
					$select_metodo_pago.append(hmtl_metodo);
					
					
					var desactivado="";
					var check="";
					var valor_seleccionado="";
					if(entry['datosGrid'] != null){
						$.each(entry['datosGrid'],function(entryIndex,prod){
							
							//obtiene numero de trs
							var tr = $("tr", $grid_productos).size();
							tr++;
							
							if( parseFloat(prod['cant_dev']) > 0 ){
								valor_seleccionado="1";
								check="checked";
								desactivado="disabled='disabled'";
								//ocultar boton actualizar porque ya se ha generado devoluciones, ya no se puede guardar cambios
								$submit_actualizar.hide();
							}else{
								valor_seleccionado="0";
								check="";
								desactivado="";
							}
							
							var trr = '';
							trr = '<tr>';
							trr += '<td class="grid" style="font-size: 11px;  border:1px solid #C1DAD7;" width="30">';
									trr += '<input type="checkbox" name="micheck" value="true" '+check+' '+desactivado+'>';
									//trr += '<input type="checkbox" name="micheck" value="true" >';
									trr += '<input type="hidden" name="seleccionado" id="selec" value="'+valor_seleccionado+'">';//el 1 significa que el registro no ha sido eliminado
							trr += '</td>';
							trr += '<td class="grid1" style="font-size: 11px;  border:1px solid #C1DAD7;" width="122">';
									trr += '<input type="hidden" name="idproducto" id="idprod" value="'+ prod['inv_prod_id'] +'">';
									trr += '<input type="text" name="sku'+ tr +'" value="'+ prod['codigo_producto'] +'" id="skuprod" class="borde_oculto" readOnly="true" style="width:116px;">';
							trr += '</td>';
							trr += '<td class="grid1" style="font-size: 11px;  border:1px solid #C1DAD7;" width="202">';
								trr += '<input type="text" 	name="nombre'+ tr +'" 	value="'+ prod['titulo'] +'" 	id="nom" class="borde_oculto" readOnly="true" style="width:198px;">';
							trr += '</td>';
							trr += '<td class="grid1" style="font-size: 11px;  border:1px solid #C1DAD7;" width="100">';
								trr += '<input type="text" 	name="unidad'+ tr +'" 	value="'+ prod['unidad'] +'" 	id="uni" class="borde_oculto" readOnly="true" style="width:96px;">';
								trr += '<input type="hidden" name="idpres" value="'+ prod['id_presentacion'] +'">';
							trr += '</td>';
							trr += '<td class="grid1" style="font-size: 11px;  border:1px solid #C1DAD7;" width="80">';
								trr += '<INPUT type="text" 	name="cantidad" value="'+  prod['cantidad'] +'" id="cant" class="borde_oculto" style="width:76px; text-align:right;">';
							trr += '</td>';
							trr += '<td class="grid2" style="font-size: 11px;  border:1px solid #C1DAD7;" width="90">';
								trr += '<INPUT type="text" 	name="costo" 	value="'+  prod['precio_unitario'] +'" 	id="cost" class="borde_oculto" style="width:76px; text-align:right;">';
							trr += '</td>';
							trr += '<td class="grid2" style="font-size: 11px;  border:1px solid #C1DAD7;" width="90">';
								trr += '<INPUT type="text" 	name="importe'+ tr +'" 	value="'+  $(this).agregar_comas( prod['importe'] )  +'" id="import" class="borde_oculto" readOnly="true" style="width:86px; text-align:right;">';
								trr += '<input type="hidden" name="totimpuesto'+ tr +'" id="totimp" value="'+  parseFloat(prod['importe']) * parseFloat(prod['tasa_iva']) +'">';
							trr += '</td>';
							
							var tasaIeps=" ";
							var importeIeps="";
							
							if(parseInt(prod['id_ieps'])>0){
								tasaIeps=prod['tasa_ieps'];
								importeIeps=prod['importe_ieps'];
							}
							trr += '<td class="grid2" style="font-size: 11px;  border:1px solid #C1DAD7;" width="50">';
								trr += '<input type="hidden" name="idIeps"     value="'+ prod['id_ieps'] +'" id="idIeps">';
								trr += '<input type="text" name="tasaIeps" value="'+ tasaIeps +'" class="borde_oculto" id="tasaIeps" style="width:46px; text-align:right;" readOnly="true">';
							trr += '</td>';
							
							trr += '<td class="grid2" style="font-size: 11px;  border:1px solid #C1DAD7;" width="80">';
								trr += '<input type="text" name="importeIeps" value="'+ importeIeps +'" class="borde_oculto" id="importeIeps" style="width:76px; text-align:right;" readOnly="true">';
							trr += '</td>';
							
							trr += '<td class="grid1" style="font-size: 11px;  border:1px solid #C1DAD7;" width="105">';
								trr += '<input type="text" 		name="cantidad_dev" value="'+prod['cant_dev']+'" readOnly="true" id="cantdev" style="width:99px; background:#dddddd">';
								trr += '<input type="hidden" 	name="importe_dev" id="impdev" value="0">';
								trr += '<input type="hidden" 	name="importe_imp_dev" id="importeimpdev" value="0">';
								trr += '<input type="hidden" 	name="importe_ieps_dev" id="importeiepsdev" value="0">';
								trr += '<input type="hidden"    name="valor_imp" value="'+  prod['tasa_iva'] +'" id="ivalorimp">';
							trr += '</td>';
							
							trr += '</tr>';
							$grid_productos.append(trr);
						});
					}
					//$calcula_totales();//llamada a la funcion que calcula totales 
					
					var tmov_id=0;
					if(parseInt(entry['NCred'].length)>0){
						tmov_id = entry['NCred'][0]['tmov_id'];
					}
					
					$select_tmov.children().remove();
					var tmov_hmtl = '<option value="0">[--- ---]</option>';
					if(entry['TMov']){
						if(parseInt(tmov_id)>0){
							tmov_hmtl='';
						}
						$.each(entry['TMov'],function(entryIndex,mov){
							if(parseInt(mov['id'])==parseInt(tmov_id)){
								tmov_hmtl += '<option value="'+ mov['id'] +'" selected="yes">'+ mov['titulo'] + '</option>';
							}else{
								if(!cancelado){
									tmov_hmtl += '<option value="'+ mov['id'] +'">'+ mov['titulo'] + '</option>';
								}
							}
						});
					}
					$select_tmov.append(tmov_hmtl);
					
					
					if(parseInt(entry['NCred'].length)>0){
						$nota_credito.val( entry['NCred'][0]['folio_nota'] );
						$fecha_nc.val( entry['NCred'][0]['fecha_nc'] );
						$tipo_cambio_nota.val( entry['NCred'][0]['tc_nota'] );
						$subtotal_nota.val( entry['NCred'][0]['subtotal_nota'] );
						$ieps_nota.val( entry['NCred'][0]['monto_ieps_nota'] );
						$impuesto_nota.val( entry['NCred'][0]['impuesto_nota'] );
						$impuesto_retenido_nota.val( entry['NCred'][0]['monto_ret_nota'] );
						$total_nota.val( entry['NCred'][0]['total_nota'] );
						$concepto.text(entry['NCred'][0]['concepto_nota']);
						$grid_productos.find('input[name=micheck]').hide();//ocultar
						$grid_productos.find('input[name=cantidad_dev]').css({'background' : '#ffffff'});
						$concepto.attr("readonly", true);
						$tipo_cambio_nota.attr("readonly", true);
						$registrar_devolucion.attr('disabled','-1'); //deshabilitar
					}else{
						//Aqui se debe poner el tipo de cambio actual
						$tipo_cambio_nota.val( entry['Tc'][0]['tipo_cambio'] );
						
						$grid_productos.find('input[name=cantidad_dev]').css({'background' : '#dddddd'});
						
						//aplicar click a los campos check del grid
						//esto es porque aun no hay Nota de Credito asociado es esta factura
						aplicar_eventos_a_campos_del_grid($grid_productos);
					}
					
					$tipo_cambio.attr("readonly", true);
					$folio_pedido.attr("readonly", true);
					$orden_compra.attr("readonly", true);
					$grid_productos.find('#cant').attr("readonly", true);//establece solo lectura campos cantidad del grid
					$grid_productos.find('#cost').attr("readonly", true);//establece solo lectura campos costo del grid					
					
					//Si el estado del comprobante es 0, esta cancelado
					if(entry['datosFactura'][0]['estado']=='CANCELADO'){
						$tipo_cambio.val(entry['datosFactura']['0']['tipo_cambio']);
						$rfc_cliente.attr('disabled','-1'); //deshabilitar
						$folio_pedido.attr('disabled','-1'); //deshabilitar
						$razon_cliente.attr('disabled','-1'); //deshabilitar
						$dir_cliente.attr('disabled','-1'); //deshabilitar
						$serie_folio.attr('disabled','-1'); //deshabilitar
						$concepto.attr('disabled','-1'); //deshabilitar
						$select_moneda.attr('disabled','-1'); //deshabilitar
						$tipo_cambio.attr('disabled','-1'); //deshabilitar
						$select_vendedor.attr('disabled','-1'); //deshabilitar
						$select_condiciones.attr('disabled','-1'); //deshabilitar
						$orden_compra.attr('disabled','-1'); //deshabilitar
						$select_metodo_pago.attr('disabled','-1'); //deshabilitar
						//$no_cuenta.attr('disabled','-1'); //deshabilitar
						$grid_productos.find('#cant').attr('disabled','-1'); //deshabilitar
						$grid_productos.find('#cost').attr('disabled','-1'); //deshabilitar
						$grid_productos.find('#import').attr('disabled','-1'); //deshabilitar
						$grid_productos.find('#cantdev').attr('disabled','-1'); //deshabilitar
						$grid_productos.find('input[name=micheck]').hide();//ocultar
						
						$select_tmov.attr('disabled','-1'); //deshabilitar
						$subtotal.attr('disabled','-1'); //deshabilitar
						$impuesto.attr('disabled','-1'); //deshabilitar
						$impuesto_retenido.attr('disabled','-1'); //deshabilitar
						$total.attr('disabled','-1'); //deshabilitar
						$saldo_fac.attr('disabled','-1'); //deshabilitar
						
						if(!$registrar_devolucion.is(':disabled')) {
							$registrar_devolucion.attr('disabled','-1'); //deshabilitar
						}
						
						$tipo_cambio_nota.attr('disabled','-1'); //deshabilitar
						$nota_credito.attr('disabled','-1'); //deshabilitar
						$subtotal_nota.attr('disabled','-1'); //deshabilitar
						$impuesto_nota.attr('disabled','-1'); //deshabilitar
						$impuesto_retenido_nota.attr('disabled','-1'); //deshabilitar
						$total_nota.attr('disabled','-1'); //deshabilitar
					}
				});
				//Termina llamada json
                
                
				$registrar_devolucion.click(function(event){
					jConfirm('Al hacer la devoluci&oacute;n se crear&aacute; una Nota de Cr&eacute;dito.\nConfirmar devoluci&oacute;n?', 'Dialogo de Confirmacion', function(r) {
						// If they confirmed, manually trigger a form submission
						if (r) {
							$submit_actualizar.trigger('click');
						}else{
							//aqui no hay nada
						}
					});
				});
                
                
				$submit_actualizar.bind('click',function(){
					var selec=0;
					//checa facturas a revision seleccionadas
					selec = contar_seleccionados($grid_productos);
					
					if(parseInt(selec) > 0){
						return true;
					}else{
						jAlert("No hay partidas seleccionadas para devoluci&oacute;n", 'Atencion!');
						return false;
					}
				});
		
                
				//Ligamos el boton cancelar al evento click para eliminar la forma
				$cancelar_plugin.bind('click',function(){
					var remove = function() {$(this).remove();};
					$('#forma-facdevoluciones-overlay').fadeOut(remove);
				});
				
				$cerrar_plugin.bind('click',function(){
					var remove = function() {$(this).remove();};
					$('#forma-facdevoluciones-overlay').fadeOut(remove);
				});
				
				$concepto.focus();
			}
		}
	}
	
	
	
	
    $get_datos_grid = function(){
        var input_json = document.location.protocol + '//' + document.location.host + '/'+controller+'/getAllFacturas.json';
        
        var iu = $('#lienzo_recalculable').find('input[name=iu]').val();
        
        $arreglo = {'orderby':'id','desc':'DESC','items_por_pag':15,'pag_start':1,'display_pag':20,'input_json':'/'+controller+'/getAllFacturas.json', 'cadena_busqueda':$cadena_busqueda, 'iu':iu}
		
        $.post(input_json,$arreglo,function(data){
			
            //pinta_grid
            $.fn.tablaOrdenableEdit(data,$('#lienzo_recalculable').find('.tablesorter'),carga_formafacdevoluciones00_for_datagrid00Edit);

            //resetea elastic, despues de pintar el grid y el slider
            Elastic.reset(document.getElementById('lienzo_recalculable'));
        },"json");
    }
    
    $get_datos_grid();
    
    
});



