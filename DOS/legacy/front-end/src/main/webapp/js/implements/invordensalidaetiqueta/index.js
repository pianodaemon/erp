$(function() {
	String.prototype.toCharCode = function(){
	    var str = this.split(''), len = str.length, work = new Array(len);
	    for (var i = 0; i < len; ++i){
			work[i] = this.charCodeAt(i);
	    }
	    return work.join(',');
	};
	
	var $aplicar_evento_click_checkbox = function($campo_checkbox, $campo_valor){
		$campo_checkbox.click(function(){
			if(this.checked){
				$campo_valor.val('true');
			}else{
				$campo_valor.val('false');
			}
		});
	}
	
	//Funcion para hacer que un campo solo acepte numeros
	$permitir_solo_numeros = function($campo){
		//validar campo costo, solo acepte numeros y punto
		$campo.keypress(function(e){
			// Permitir  numeros, borrar, suprimir, TAB, puntos, comas
			if (e.which == 8 || e.which == 46 || e.which==13 || e.which == 0 || (e.which >= 48 && e.which <= 57 )) {
				return true;
			}else {
				return false;
			}
		});
	}
	
	
	//Valida que la cantidad ingresada no tenga mas de un punto decimal
	$validar_numero_puntos = function($campo, campo_nombre){
		//Buscar cuantos puntos tiene  Precio Unitario
		var coincidencias = $campo.val().match(/\./g);
		var numPuntos = coincidencias ? coincidencias.length : 0;
		if(parseInt(numPuntos)>1){
			jAlert('El valor ingresado para el campo '+campo_nombre+' es incorrecto, tiene mas de un punto('+$campo.val()+').', 'Atencion!', function(r) { 
				$campo.focus();
			});
		}
	}
	
	
	$aplica_evento_focus_input_numerico = function($campo){
		//Al iniciar el campo tiene un caracter en blanco o tiene comas, al obtener el foco se elimina el  espacio por espacio en blanco
		$campo.focus(function(e){
			var valor=quitar_comas($(this).val().trim());
			
			if(valor.trim()!=''){
				if(parseFloat(valor)<=0){
					$(this).val('');
				}else{
					$(this).val(valor);
				}
			}
		});
	}
	
	var signar_espacio_blanco_si_esta_vacio = function($campo_input){
		if($campo_input.val().trim()==''){
			$campo_input.val(' ');
		}
	}
	
							
	var aplicar_evento_focus_input_text = function($campo_input){
		$campo_input.focus(function(e){
			//Eliminar espacios en blanco
			$(this).val($(this).val().trim());
		});
	}
	
	var aplicar_evento_blur_input_text = function($campo_input){
		$campo_input.blur(function(e){
			//Asignar espacio en blanco
			if($(this).val().trim()==''){
				$(this).val(' ');
			}
		});
		
	}
	
	var quitar_comas= function($valor){
		$valor = $valor+'';
		return $valor.split(',').join('');
	}
	
	
	var seleccionar_todos_check = function($tabla, $campo_check){
		$campo_check.click(function(event){
			if(this.checked){
				$tabla.find('input[name=micheck]').each(function(){
					this.checked = true;
					$(this).parent().find('input[name=selec_micheck]').val('true');
				});
			}else{
				$tabla.find('input[name=micheck]').each(function(){
					this.checked = false;
					$(this).parent().find('input[name=selec_micheck]').val('false');
				});
			}
		});
     }
     
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
	
	var arrayAgentes;
	
	$('#header').find('#header1').find('span.emp').text($('#lienzo_recalculable').find('input[name=emp]').val());
	$('#header').find('#header1').find('span.suc').text($('#lienzo_recalculable').find('input[name=suc]').val());
    var $username = $('#header').find('#header1').find('span.username');
	$username.text($('#lienzo_recalculable').find('input[name=user]').val());
	
	var $contextpath = $('#lienzo_recalculable').find('input[name=contextpath]');
	var controller = $contextpath.val()+"/controllers/invordensalidaetiqueta";
    
    //Barra para las acciones
    $('#barra_acciones').append($('#lienzo_recalculable').find('.table_acciones'));
    $('#barra_acciones').find('.table_acciones').css({'display':'block'});
    //var $new_prefactura = $('#barra_acciones').find('.table_acciones').find('a[href*=new_item]');
	var $generar_informe = $('#barra_acciones').find('.table_acciones').find('a[href*=generar_informe]');
	var $visualiza_buscador = $('#barra_acciones').find('.table_acciones').find('a[href*=visualiza_buscador]');
	
	$('#barra_acciones').find('.table_acciones').find('#nItem').mouseover(function(){
		$(this).removeClass("onmouseOutNewItem").addClass("onmouseOverNewItem");
	});
	$('#barra_acciones').find('.table_acciones').find('#nItem').mouseout(function(){
		$(this).removeClass("onmouseOverNewItem").addClass("onmouseOutNewItem");
	});
	
	$('#barra_acciones').find('.table_acciones').find('#genInforme').mouseover(function(){
		$(this).removeClass("onmouseOutGeneraInforme").addClass("onmouseOverGeneraInforme");
	});
	$('#barra_acciones').find('.table_acciones').find('#genInforme').mouseout(function(){
		$(this).removeClass("onmouseOverGeneraInforme").addClass("onmouseOutGeneraInforme");
	});
	
	$('#barra_acciones').find('.table_acciones').find('#vbuscador').mouseover(function(){
		$(this).removeClass("onmouseOutVisualizaBuscador").addClass("onmouseOverVisualizaBuscador");
	});
	$('#barra_acciones').find('.table_acciones').find('#vbuscador').mouseout(function(){
		$(this).removeClass("onmouseOverVisualizaBuscador").addClass("onmouseOutVisualizaBuscador");
	});
	
	//aqui va el titulo del catalogo
	$('#barra_titulo').find('#td_titulo').append('Impresi&oacute;n de Etiquetas-Salidas');
	
	//barra para el buscador 
	//$('#barra_buscador').css({'height':'0px'});
	$('#barra_buscador').append($('#lienzo_recalculable').find('.tabla_buscador'));
	//$('#barra_buscador').find('.tabla_buscador').css({'display':'none'});
	//$('#barra_buscador').hide();
	
	
	
	var $cadena_busqueda = "";
	var $busqueda_factura = $('#barra_buscador').find('.tabla_buscador').find('input[name=busqueda_factura]');
	var $busqueda_folio_pedido = $('#barra_buscador').find('.tabla_buscador').find('input[name=busqueda_folio_pedido]');
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
		valor_retorno += "folio_pedido" + signo_separador + $busqueda_folio_pedido.val() + "|";
		valor_retorno += "cliente" + signo_separador + $busqueda_cliente.val() + "|";
		valor_retorno += "fecha_inicial" + signo_separador + $busqueda_fecha_inicial.val() + "|";
		valor_retorno += "fecha_final" + signo_separador + $busqueda_fecha_final.val() + "|";
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
		$busqueda_folio_pedido.val('');
		
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
		$('#barra_genera_informe').find('.tabla_genera_informe').css({'display':'none'});
		
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
		
		//asignar el enfoque al visualizar Buscador
		$busqueda_factura.focus();
	});
	
	//aplicar evento Keypress para que al pulsar enter ejecute la busqueda
	$(this).aplicarEventoKeypressEjecutaTrigger($busqueda_factura, $buscar);
	$(this).aplicarEventoKeypressEjecutaTrigger($busqueda_folio_pedido, $buscar);
	$(this).aplicarEventoKeypressEjecutaTrigger($busqueda_cliente, $buscar);
	$(this).aplicarEventoKeypressEjecutaTrigger($busqueda_codigo, $buscar);
	$(this).aplicarEventoKeypressEjecutaTrigger($busqueda_producto, $buscar);
	$(this).aplicarEventoKeypressEjecutaTrigger($busqueda_fecha_inicial, $buscar);
	$(this).aplicarEventoKeypressEjecutaTrigger($busqueda_fecha_final, $buscar);
	$(this).aplicarEventoKeypressEjecutaTrigger($busqueda_select_agente, $buscar);
	
        
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
		var $select_prod_tipo = $('#forma-invordensalidaetiqueta-window').find('select[name=prodtipo]');
		$('#forma-invordensalidaetiqueta-window').find('#submit').mouseover(function(){
			$('#forma-invordensalidaetiqueta-window').find('#submit').removeAttr("src").attr("src","../../img/modalbox/bt1.png");
			//$('#forma-invordensalidaetiqueta-window').find('#submit').css({backgroundImage:"url(../../img/modalbox/bt1.png)"});
		})
		$('#forma-invordensalidaetiqueta-window').find('#submit').mouseout(function(){
			$('#forma-invordensalidaetiqueta-window').find('#submit').removeAttr("src").attr("src","../../img/modalbox/btn1.png");
			//$('#forma-invordensalidaetiqueta-window').find('#submit').css({backgroundImage:"url(../../img/modalbox/btn1.png)"});
		})
		$('#forma-invordensalidaetiqueta-window').find('#boton_cancelar').mouseover(function(){
			$('#forma-invordensalidaetiqueta-window').find('#boton_cancelar').css({backgroundImage:"url(../../img/modalbox/bt2.png)"});
		})
		$('#forma-invordensalidaetiqueta-window').find('#boton_cancelar').mouseout(function(){
			$('#forma-invordensalidaetiqueta-window').find('#boton_cancelar').css({backgroundImage:"url(../../img/modalbox/btn2.png)"});
		})
		
		$('#forma-invordensalidaetiqueta-window').find('#close').mouseover(function(){
			$('#forma-invordensalidaetiqueta-window').find('#close').css({backgroundImage:"url(../../img/modalbox/close_over.png)"});
		})
		$('#forma-invordensalidaetiqueta-window').find('#close').mouseout(function(){
			$('#forma-invordensalidaetiqueta-window').find('#close').css({backgroundImage:"url(../../img/modalbox/close.png)"});
		})
		
		$('#forma-invordensalidaetiqueta-window').find(".contenidoPes").hide(); //Hide all content
		$('#forma-invordensalidaetiqueta-window').find("ul.pestanas li:first").addClass("active").show(); //Activate first tab
		$('#forma-invordensalidaetiqueta-window').find(".contenidoPes:first").show(); //Show first tab content
		
		//On Click Event
		$('#forma-invordensalidaetiqueta-window').find("ul.pestanas li").click(function() {
			$('#forma-invordensalidaetiqueta-window').find(".contenidoPes").hide();
			$('#forma-invordensalidaetiqueta-window').find("ul.pestanas li").removeClass("active");
			var activeTab = $(this).find("a").attr("href");
			$('#forma-invordensalidaetiqueta-window').find( activeTab , "ul.pestanas li" ).fadeIn().show();
			$(this).addClass("active");
			return false;
		});
	}
	
	
	var generar_etiquetas = function(identificador){
		if(parseInt(identificador)>0){
			var iu = $('#lienzo_recalculable').find('input[name=iu]').val();
			var input_json = document.location.protocol + '//' + document.location.host + '/' + controller + '/getEtiquetas/'+identificador+'/'+iu+'/out.json';
			window.location.href=input_json;
		}else{
			jAlert("No se esta enviando el el Numero de Factura","Atencion!!!")
		}
	}
	

	//ver detalles de una factura
	var carga_formainvordensalidaetiqueta00_for_datagrid00Edit = function(id_to_show, accion_mode){
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
			$('#forma-invordensalidaetiqueta-window').remove();
			$('#forma-invordensalidaetiqueta-overlay').remove();
            
			var form_to_show = 'formainvordensalidaetiqueta00';
			$('#' + form_to_show).each (function(){this.reset();});
			var $forma_selected = $('#' + form_to_show).clone();
			$forma_selected.attr({id : form_to_show + id_to_show});
			
			$(this).modalPanel_invordensalidaetiqueta();
			
			$('#forma-invordensalidaetiqueta-window').css({"margin-left": -415, 	"margin-top": -290});
			
			$forma_selected.prependTo('#forma-invordensalidaetiqueta-window');
			$forma_selected.find('.panelcito_modal').attr({id : 'panelcito_modal' + id_to_show , style:'display:table'});
			
			$tabs_li_funxionalidad();
			
			//alert(id_to_show);
			if(accion_mode == 'edit'){
				var input_json = document.location.protocol + '//' + document.location.host + '/'+controller+'/getFactura.json';
				var $arreglo = {'identificador':id_to_show,'iu':$('#lienzo_recalculable').find('input[name=iu]').val() };
				
				var $opcion = $('#forma-invordensalidaetiqueta-window').find('input[name=opcion]');
				var $identificador = $('#forma-invordensalidaetiqueta-window').find('input[name=identificador]');
				var $folio_pedido = $('#forma-invordensalidaetiqueta-window').find('input[name=folio_pedido]');
				var $id_cliente = $('#forma-invordensalidaetiqueta-window').find('input[name=id_cliente]');
				var $rfc_cliente = $('#forma-invordensalidaetiqueta-window').find('input[name=rfccliente]');
				var $razon_cliente = $('#forma-invordensalidaetiqueta-window').find('input[name=razoncliente]');
				var $dir_cliente = $('#forma-invordensalidaetiqueta-window').find('input[name=dircliente]');
				
				var $serie_folio = $('#forma-invordensalidaetiqueta-window').find('input[name=serie_folio]');
				var $select_moneda = $('#forma-invordensalidaetiqueta-window').find('select[name=moneda]');
				var $tipo_cambio = $('#forma-invordensalidaetiqueta-window').find('input[name=tipo_cambio]');
				var $fecha = $('#forma-invordensalidaetiqueta-window').find('input[name=fecha]');
				var $observaciones = $('#forma-invordensalidaetiqueta-window').find('textarea[name=observaciones]');
				
				var $btn_etiquetas = $('#forma-invordensalidaetiqueta-window').find('#btn_etiquetas');
				
				//Grid de productos
				var $grid_productos = $('#forma-invordensalidaetiqueta-window').find('#grid_productos');
				
				var $check_head = $('#forma-invordensalidaetiqueta-window').find('input[name=check_head]');
				
				//Grid de errores
				var $grid_warning = $('#forma-invordensalidaetiqueta-window').find('#div_warning_grid').find('#grid_warning');
				
				var $cerrar_plugin = $('#forma-invordensalidaetiqueta-window').find('#close');
				var $cancelar_plugin = $('#forma-invordensalidaetiqueta-window').find('#boton_cancelar');
				var $submit_actualizar = $('#forma-invordensalidaetiqueta-window').find('#submit');
				
		
				var respuestaProcesada = function(data){
					if ( data['success'] == "true" ){
						$('#forma-invordensalidaetiqueta-window').find('div.interrogacion').css({'display':'none'});
						
						if(parseInt($opcion.val())==0){
							jAlert("Los datos se guardaron con &eacute;xito", 'Atencion!');
							var remove = function() {$(this).remove();};
							$('#forma-invordensalidaetiqueta-overlay').fadeOut(remove);
						}else{
							if(parseInt($opcion.val())==1){
								//LLamada a la funcion que genera las etiquetas con los datos actuales
								generar_etiquetas(id_to_show);
							}
						}
					}else{
						// Desaparece todas las interrogaciones si es que existen
						//$('#forma-invordensalidaetiqueta-window').find('.div_one').css({'height':'545px'});//sin errores
						$('#forma-invordensalidaetiqueta-window').find('.invordensalidaetiqueta_div_one').css({'height':'575px'});//con errores
						$('#forma-invordensalidaetiqueta-window').find('div.interrogacion').css({'display':'none'});
						
						$grid_productos.find('#cant').css({'background' : '#ffffff'});
						$grid_productos.find('#cost').css({'background' : '#ffffff'});
						
						$('#forma-invordensalidaetiqueta-window').find('#div_warning_grid').css({'display':'none'});
						$('#forma-invordensalidaetiqueta-window').find('#div_warning_grid').find('#grid_warning').children().remove();
						
						var valor = data['success'].split('___');
						//muestra las interrogaciones
						for (var element in valor){
							tmp = data['success'].split('___')[element];
							longitud = tmp.split(':');

							if( longitud.length > 1 ){
								$('#forma-invordensalidaetiqueta-window').find('img[rel=warning_' + tmp.split(':')[0] + ']')
								.parent()
								.css({'display':'block'})
								.easyTooltip({tooltipId: "easyTooltip2",content: tmp.split(':')[1]});

								//alert(tmp.split(':')[0]);

								if(parseInt($("tr", $grid_productos).size())>0){
									for (var i=1;i<=parseInt($("tr", $grid_productos).size());i++){
										if((tmp.split(':')[0]=='cantidad'+i) || (tmp.split(':')[0]=='costo'+i)){
											$('#forma-invordensalidaetiqueta-window').find('.invordensalidaetiqueta_div_one').css({'height':'575px'});
											$('#forma-invordensalidaetiqueta-window').find('#div_warning_grid').css({'display':'block'});
											
											if(tmp.split(':')[0].substring(0, 8) == 'cantidad'){
												$grid_productos.find('input[name=cantidad]').eq(parseInt(i) - 1) .css({'background' : '#d41000'});
												//alert();
											}else{
												if(tmp.split(':')[0].substring(0, 5) == 'costo'){
													$grid_productos.find('input[name=costo]').eq(parseInt(i) - 1) .css({'background' : '#d41000'});
												}
											}
											
											var tr_warning = '<tr>';
												tr_warning += '<td width="20"><div><IMG SRC="../../img/icono_advertencia.png" ALIGN="top" rel="warning_sku"></td>';
												tr_warning += '<td width="120">';
												tr_warning += '<INPUT TYPE="text" value="'+$grid_productos.find('input[name=sku' + i + ']').val()+'" class="borde_oculto" readOnly="true" style="width:116px; color:red">';
												tr_warning += '</td>';
												tr_warning += '<td width="200">';
												tr_warning += '<INPUT TYPE="text" value="'+$grid_productos.find('input[name=nombre' + i + ']').val()+'" class="borde_oculto" readOnly="true" style="width:196px; color:red">';
												tr_warning += '</td>';
												tr_warning += '<td width="235">';
												tr_warning += '<INPUT TYPE="text" value="'+ tmp.split(':')[1] +'" class="borde_oculto" readOnly="true" style="width:230px; color:red">';
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
				
				var options = {dataType :  'json', success : respuestaProcesada};
				$forma_selected.ajaxForm(options);
				
				//Aqui se cargan los campos al editar
				$.post(input_json,$arreglo,function(entry){
					$identificador.val(entry['datosFactura'][0]['id']);
					$folio_pedido.val(entry['datosFactura'][0]['folio_pedido']);
					$id_cliente.val(entry['datosFactura'][0]['cliente_id']);
					//$rfc_cliente.val(entry['datosFactura'][0]['rfc']);
					$razon_cliente.val(entry['datosFactura'][0]['razon_social']);
					//$dir_cliente.val(entry['datosFactura'][0]['direccion']);
					$serie_folio.val(entry['datosFactura'][0]['serie_folio']);
					$observaciones.text(entry['datosFactura'][0]['observaciones']);
                    $fecha.val(entry['datosFactura'][0]['fecha_exp']);
					
					//Carga select denominacion con todas las monedas
					$select_moneda.children().remove();
					//var moneda_hmtl = '<option value="0">[--   --]</option>';
					var moneda_hmtl = '';
					$.each(entry['Monedas'],function(entryIndex,moneda){
						if(moneda['id'] == entry['datosFactura'][0]['moneda_id']){
							moneda_hmtl += '<option value="' + moneda['id'] + '"  selected="yes">' + moneda['descripcion'] + '</option>';
						}else{
							//moneda_hmtl += '<option value="' + moneda['id'] + '"  >' + moneda['descripcion'] + '</option>';
						}
					});
					$select_moneda.append(moneda_hmtl);
					
					
					if(entry['datosGrid'].length > 0){
						//var seleccionado=false;
						var check_checked="";
						var valor_check="";
						var orden_compra="";
						
						$.each(entry['datosGrid'],function(entryIndex,prod){
							//Obtiene numero de trs
							var tr = $("tr", $grid_productos).size();
							tr++;
							
							check_checked="";
							valor_check = "false";
							
							if(prod['seleccionado']){
								check_checked="checked";
								valor_check = "true";
							}
							
							orden_compra = (prod['orden_compra'].trim()=='')? entry['datosFactura'][0]['orden_compra']:prod['orden_compra'];
							
							var trr = '';
							trr = '<tr>';
							trr += '<td class="grid1" style="font-size:11px;  border:1px solid #C1DAD7;" width="100">';
									trr += '<input type="hidden" name="iddet" id="iddet" value="'+ prod['iddet'] +'">';
									trr += '<input type="hidden" name="idprod" id="idprod" value="'+ prod['prod_id'] +'">';
									trr += '<input type="text" name="sku'+ tr +'" value="'+ prod['codigo'] +'" id="skuprod" class="borde_oculto" readOnly="true" style="width:98px;">';
							trr += '</td>';
							trr += '<td class="grid1" style="font-size:11px;  border:1px solid #C1DAD7;" width="220">'+ prod['titulo'] +'</td>';
							trr += '<td class="grid1" style="font-size:11px;  border:1px solid #C1DAD7;" width="90">'+ prod['unidad'] +'</td>';
							trr += '<td class="grid1" style="font-size:11px;  border:1px solid #C1DAD7;" width="90">';
									trr += '<input type="hidden" 	name="idpres"  value="'+  prod['pres_id'] +'" 	id="idpres">';
									trr += '<input type="text" 		name="presentacion'+ tr +'" 	value="'+  prod['pres'] +'" 	id="pres" class="borde_oculto" readOnly="true" style="width:87px;">';
							trr += '</td>';
							trr += '<td class="grid1" style="font-size:11px;  border:1px solid #C1DAD7;" width="100">';
									trr += '<input type="text" name="codigo2" value="'+ prod['codigo2'] +'" id="codigo2" class="codigo2'+ tr +'" style="width:97px;">';
							trr += '</td>';
							trr += '<td class="grid1" style="font-size:11px;  border:1px solid #C1DAD7;" width="105">';
									trr += '<input type="text" name="oc" value="'+ orden_compra +'" id="oc" class="oc'+ tr +'" style="width:102px;">';
							trr += '</td>';
							trr += '<td class="grid1" style="font-size:11px;  border:1px solid #C1DAD7;" width="125">';
									trr += '<input type="text" name="lote" value="'+ prod['lote'] +'" id="lote" class="lote'+ tr +'" style="width:123px;">';
							trr += '</td>';
							trr += '<td class="grid1" style="font-size:11px;  border:1px solid #C1DAD7;" width="75">';
									trr += '<input type="text" name="fcaducidad" value="'+ prod['caducidad'] +'" id="fcaducidad" class="fcaducidad'+ tr +'" style="width:73px;">';
							trr += '</td>';
							trr += '<td class="grid1" style="font-size:11px;  border:1px solid #C1DAD7;" width="80">';
								trr += '<input type="hidden" 	name="cantidad_orig" value="'+  prod['cantidad'] +'">';
								trr += '<input type="text" 	name="cantidad" value="'+  prod['cantidad'] +'" class="cantidad'+ tr +'"	id="cant" style="width:76px; text-align:right;">';
							trr += '</td>';
							trr += '<td class="grid" style="font-size:11px;  border:1px solid #C1DAD7; text-align:center;" width="30">';
								trr += '<input type="checkbox" name="micheck" class="micheck'+ tr +'" value="check" '+ check_checked +'>';
								trr += '<input type="hidden" name="selec_micheck" class="selec_micheck'+ tr +'" value="'+ valor_check +'">';
							trr += '</td>';
							trr += '</tr>';
							
							$grid_productos.append(trr);
							
							
							//Permitir solo numeros y punto
							$permitir_solo_numeros($grid_productos.find('.cantidad'+ tr));
							
							//Aplicar envento focus
							$aplica_evento_focus_input_numerico($grid_productos.find('.cantidad'+ tr));
							
							$grid_productos.find('.cantidad'+ tr).blur(function(){
								$validar_numero_puntos($(this), "Cantidad");
								if($(this).val().trim()==''){
									$(this).val(0);
								}
								
								$(this).val(parseFloat($(this).val()).toFixed(4));
								
								//Valor cantidad
								var valor_cantidad = $(this).val();
								
								//Valor del cantidad anterior
								var valor_cantidad_original = $(this).parent().find('input[name=cantidad_orig]').val();
								
								if(parseFloat(valor_cantidad) > parseFloat(valor_cantidad_original)){
									jAlert('La Cantidad no puede ser mayor a la Facturada.', 'Atencion!', function(r) { 
										$grid_productos.find('.cantidad'+ tr).val(valor_cantidad_original);
										$grid_productos.find('.cantidad'+ tr).focus();
									});
								}
							});
							
							$grid_productos.find('.fcaducidad'+ tr).click(function (s){
								var a=$('div.datepicker');
								a.css({'z-index':100});
							});
							
							$grid_productos.find('.fcaducidad'+ tr).DatePicker({
								format:'Y-m-d',
								date: $grid_productos.find('.fcaducidad'+ tr).val(),
								current: $grid_productos.find('.fcaducidad'+ tr).val(),
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
									$grid_productos.find('.fcaducidad'+ tr).val(formated);
									if (formated.match(patron) ){
										/*
										var valida_fecha=mayor($grid_productos.find('.fcaducidad'+ tr).val(),mostrarFecha());
										if (valida_fecha==true){
											jAlert("Fecha no valida",'! Atencion');
											$grid_productos.find('.fcaducidad'+ tr).val(mostrarFecha());
										}else{
											$grid_productos.find('.fcaducidad'+ tr).DatePickerHide();
										}
										*/
										$grid_productos.find('.fcaducidad'+ tr).DatePickerHide();
									}
								}
							});
							
							//Asignar espacio en blanco
							signar_espacio_blanco_si_esta_vacio($grid_productos.find('.fcaducidad'+ tr));
							signar_espacio_blanco_si_esta_vacio($grid_productos.find('.oc'+ tr));
							signar_espacio_blanco_si_esta_vacio($grid_productos.find('.lote'+ tr));
							signar_espacio_blanco_si_esta_vacio($grid_productos.find('.codigo2'+ tr));
							
							
							aplicar_evento_focus_input_text($grid_productos.find('.fcaducidad'+ tr));
							aplicar_evento_focus_input_text($grid_productos.find('.oc'+ tr));
							aplicar_evento_focus_input_text($grid_productos.find('.lote'+ tr));
							aplicar_evento_focus_input_text($grid_productos.find('.codigo2'+ tr));
							
							aplicar_evento_blur_input_text($grid_productos.find('.fcaducidad'+ tr));
							aplicar_evento_blur_input_text($grid_productos.find('.oc'+ tr));
							aplicar_evento_blur_input_text($grid_productos.find('.lote'+ tr));
							aplicar_evento_blur_input_text($grid_productos.find('.codigo2'+ tr));
							
							//Aplicar eventos a los campos checkbox
							$aplicar_evento_click_checkbox($grid_productos.find('.micheck'+ tr), $grid_productos.find('.selec_micheck'+ tr));
						});
					}
					
					$tipo_cambio.val(entry['datosFactura'][0]['tipo_cambio']);
					
					$rfc_cliente.attr("readonly", true);
					$folio_pedido.attr("readonly", true);
					$razon_cliente.attr("readonly", true);
					$serie_folio.attr("readonly", true);
					//$observaciones.attr("readonly", true);
					$fecha.attr("readonly", true);
					$tipo_cambio.attr("readonly", true);
					//$grid_productos.find('#cant').attr("readonly", true);
					
					//Aplicar evento para seleccionar todos los check del grid al seleccionar el checkbox del encabezado de la columna
					seleccionar_todos_check($grid_productos, $check_head);
				});//termina llamada json
                
                
                
				//Generar etiquetas 
				$btn_etiquetas.click(function(event){
					event.preventDefault();
					
					//Generar etiquetas
					$opcion.val(1);
					$submit_actualizar.parents("FORM").submit();
				 });
                
                
                
				$submit_actualizar.bind('click',function(){
					//Solo guardar
					$opcion.val(0);
					return true;
				});
                
                
				//Ligamos el boton cancelar al evento click para eliminar la forma
				$cancelar_plugin.bind('click',function(){
					var remove = function() {$(this).remove();};
					$('#forma-invordensalidaetiqueta-overlay').fadeOut(remove);
				});
				
				$cerrar_plugin.bind('click',function(){
					var remove = function() {$(this).remove();};
					$('#forma-invordensalidaetiqueta-overlay').fadeOut(remove);
				});
			}
		}
	}
	
	
	
	
    $get_datos_grid = function(){
        var input_json = document.location.protocol + '//' + document.location.host + '/'+controller+'/getAllFacturas.json';
        
        var iu = $('#lienzo_recalculable').find('input[name=iu]').val();
        
        $arreglo = {'orderby':'id','desc':'DESC','items_por_pag':15,'pag_start':1,'display_pag':20,'input_json':'/'+controller+'/getAllFacturas.json', 'cadena_busqueda':$cadena_busqueda, 'iu':iu}

        $.post(input_json,$arreglo,function(data){
			
            //pinta_grid
            $.fn.tablaOrdenableEdit(data,$('#lienzo_recalculable').find('.tablesorter'),carga_formainvordensalidaetiqueta00_for_datagrid00Edit);

            //resetea elastic, despues de pintar el grid y el slider
            Elastic.reset(document.getElementById('lienzo_recalculable'));
        },"json");
    }
    
    $get_datos_grid();
    
    
});



